#include "test_support.hpp"
#include <fmt/format.h>

#define CL_TARGET_OPENCL_VERSION 300
#include <algorithm>
#include <array>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <vector>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include "opencl/session.hpp"

namespace {
using namespace unified::opencl;

enum class Access { Float, Int, Uint };

void requireFormat(Session &session, cl_mem_flags flags, cl_mem_object_type type, cl_image_format format) {
  cl_uint count = 0;
  check(clGetSupportedImageFormats(session.context, flags, type, 0, nullptr, &count), "format count");
  std::vector<cl_image_format> formats(count);
  check(clGetSupportedImageFormats(session.context, flags, type, count, formats.data(), nullptr), "formats");
  if (std::none_of(formats.begin(), formats.end(), [&](const auto &entry) {
        return entry.image_channel_order == format.image_channel_order && entry.image_channel_data_type == format.image_channel_data_type;
      }))
    SKIP("image format unsupported");
}

cl_image_desc describe(cl_mem_object_type type, size_t width, size_t height = 0, size_t depth = 0, size_t layers = 0,
                       cl_mem buffer = nullptr) {
  cl_image_desc desc{};
  desc.image_type = type;
  desc.image_width = width;
  desc.image_height = height;
  desc.image_depth = depth;
  desc.image_array_size = layers;
  desc.buffer = buffer;
  return desc;
}

// Bytes every implementation round-trips exactly: SNORM excludes the most negative code, floats stay finite.
std::vector<uint8_t> texelBytes(cl_channel_type type, size_t count) {
  std::vector<uint8_t> bytes(count);
  for (size_t index = 0; index < count; ++index)
    bytes[index] = static_cast<uint8_t>((index * 37 + 11) % 251);
  const auto patch = [&](size_t width, auto value) {
    for (size_t index = 0; index + width <= count; index += width) {
      auto element = value(index / width);
      std::memcpy(bytes.data() + index, &element, width);
    }
  };
  switch (type) {
    case CL_SNORM_INT8: patch(1, [](size_t i) { return static_cast<int8_t>(static_cast<int>(i * 13 % 255) - 127); }); break;
    case CL_SNORM_INT16: patch(2, [](size_t i) { return static_cast<int16_t>(static_cast<int>(i * 997 % 65535) - 32767); }); break;
    case CL_HALF_FLOAT: patch(2, [](size_t i) { return static_cast<uint16_t>(0x3c00 + (i * 29 % 1024)); }); break;
    case CL_FLOAT: patch(4, [](size_t i) { return static_cast<float>(i) * 0.25f - 3.0f; }); break;
    default: break;
  }
  return bytes;
}

struct RoundTrip {
  cl_image_format format;
  size_t elementSize;
  Access access;
};

void roundTrip(const RoundTrip &shape) {
  Session session;
  session.require(Capability::Images);
  requireFormat(session, CL_MEM_READ_ONLY, CL_MEM_OBJECT_IMAGE2D, shape.format);
  requireFormat(session, CL_MEM_WRITE_ONLY, CL_MEM_OBJECT_IMAGE2D, shape.format);
  constexpr size_t width = 7, height = 5;
  auto input = texelBytes(shape.format.image_channel_data_type, width * height * shape.elementSize);
  auto source =
      session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, shape.format, describe(CL_MEM_OBJECT_IMAGE2D, width, height), input.data());
  auto target = session.image(CL_MEM_WRITE_ONLY, shape.format, describe(CL_MEM_OBJECT_IMAGE2D, width, height));
  const char *source_text = R"cl(
    kernel void copy(read_only image2d_t src, write_only image2d_t dst) {
      int2 p = (int2)(get_global_id(0), get_global_id(1));
      WRITE(dst, p, READ(src, p));
    })cl";
  const auto options = shape.access == Access::Float ? "-DREAD=read_imagef -DWRITE=write_imagef"
                       : shape.access == Access::Int ? "-DREAD=read_imagei -DWRITE=write_imagei"
                                                     : "-DREAD=read_imageui -DWRITE=write_imageui";
  auto kernel = session.kernel(source_text, options, "copy");
  check(clSetKernelArg(kernel, 0, sizeof(source), &source), "source argument");
  check(clSetKernelArg(kernel, 1, sizeof(target), &target), "target argument");
  session.run(kernel, 2, {width, height, 1}, nullptr);
  std::vector<uint8_t> output(input.size());
  const size_t origin[] = {0, 0, 0}, region[] = {width, height, 1};
  check(clEnqueueReadImage(session.queue, target, CL_TRUE, origin, region, 0, 0, output.data(), 0, nullptr, nullptr), "read image");
  for (size_t index = 0; index < input.size(); ++index) {
    INFO(fmt::format("byte {} of texel {}", index % shape.elementSize, index / shape.elementSize));
    REQUIRE(output[index] == input[index]);
  }
}

constexpr const char *SamplingSource = R"cl(
  kernel void sample(read_only image2d_t src, sampler_t sampler, global const float2 *coords, global float4 *out) {
    size_t i = get_global_id(0);
    out[i] = read_imagef(src, sampler, coords[i]);
  }
  kernel void sample_int(read_only image2d_t src, sampler_t sampler, global const int2 *coords, global float4 *out) {
    size_t i = get_global_id(0);
    out[i] = read_imagef(src, sampler, coords[i]);
  })cl";

struct Sampling {
  cl_bool normalised;
  cl_addressing_mode addressing;
  cl_filter_mode filter;
  std::vector<std::array<float, 2>> coordinates;
  std::vector<std::array<float, 2>> expected; // red and alpha
};

void sampling(const Sampling &shape) {
  Session session;
  session.require(Capability::Images);
  const cl_image_format format{CL_R, CL_FLOAT};
  std::vector<float> ramp{0.0f, 1.0f, 2.0f, 3.0f};
  auto image = session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, describe(CL_MEM_OBJECT_IMAGE2D, 4, 1), ramp.data());
  auto sampler = session.sampler(shape.normalised, shape.addressing, shape.filter);
  auto kernel = session.kernel(SamplingSource, "", "sample");
  std::vector<float> coordinates;
  for (const auto &coordinate : shape.coordinates)
    coordinates.insert(coordinates.end(), coordinate.begin(), coordinate.end());
  auto coordinateBuffer = session.buffer(coordinates);
  auto out = session.buffer(std::vector<float>(shape.coordinates.size() * 4, -1.0f));
  check(clSetKernelArg(kernel, 0, sizeof(image), &image), "image argument");
  check(clSetKernelArg(kernel, 1, sizeof(sampler), &sampler), "sampler argument");
  check(clSetKernelArg(kernel, 2, sizeof(coordinateBuffer), &coordinateBuffer), "coordinate argument");
  check(clSetKernelArg(kernel, 3, sizeof(out), &out), "output argument");
  session.run(kernel, 1, {shape.coordinates.size(), 1, 1}, nullptr);
  const auto texels = session.read<float>(out, shape.coordinates.size() * 4);
  for (size_t index = 0; index < shape.coordinates.size(); ++index) {
    INFO(fmt::format("coordinate ({}, {})", shape.coordinates[index][0], shape.coordinates[index][1]));
    CHECK(std::abs(texels[index * 4] - shape.expected[index][0]) <= 1e-3f);
    CHECK(texels[index * 4 + 1] == 0.0f);
    CHECK(texels[index * 4 + 2] == 0.0f);
    CHECK(std::abs(texels[index * 4 + 3] - shape.expected[index][1]) <= 1e-3f);
  }
}

void integerSampling() {
  Session session;
  session.require(Capability::Images);
  std::vector<float> ramp{0.0f, 1.0f, 2.0f, 3.0f};
  auto image = session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, {CL_R, CL_FLOAT}, describe(CL_MEM_OBJECT_IMAGE2D, 4, 1), ramp.data());
  auto sampler = session.sampler(CL_FALSE, CL_ADDRESS_CLAMP_TO_EDGE, CL_FILTER_NEAREST);
  auto kernel = session.kernel(SamplingSource, "", "sample_int");
  auto coordinates = session.buffer(std::vector<int32_t>{0, 0, 2, 0, 3, 0, -4, 0, 9, 0});
  auto out = session.buffer(std::vector<float>(20, -1.0f));
  check(clSetKernelArg(kernel, 0, sizeof(image), &image), "image argument");
  check(clSetKernelArg(kernel, 1, sizeof(sampler), &sampler), "sampler argument");
  check(clSetKernelArg(kernel, 2, sizeof(coordinates), &coordinates), "coordinate argument");
  check(clSetKernelArg(kernel, 3, sizeof(out), &out), "output argument");
  session.run(kernel, 1, {5, 1, 1}, nullptr);
  const auto texels = session.read<float>(out, 20);
  const std::array<float, 5> expected{0.0f, 2.0f, 3.0f, 0.0f, 3.0f};
  for (size_t index = 0; index < expected.size(); ++index) {
    INFO(fmt::format("coordinate {}", index));
    CHECK(texels[index * 4] == expected[index]);
    CHECK(texels[index * 4 + 3] == 1.0f);
  }
}

struct Layout {
  cl_mem_object_type type;
  size_t width, height, depth, layers;
  const char *imageType;
  const char *coordinate;
  bool writes3D;
};

// Each texel stores x + 100y + 10000z, where z is the layer or slice.
void layouts(const Layout &shape) {
  Session session;
  session.require(Capability::Images);
  if (shape.writes3D) session.require(Capability::ImageWrites3D);
  const cl_image_format format{CL_R, CL_UNSIGNED_INT32};
  requireFormat(session, CL_MEM_WRITE_ONLY, shape.type, format);
  cl_mem buffer = nullptr;
  if (shape.type == CL_MEM_OBJECT_IMAGE1D_BUFFER) buffer = session.buffer(std::vector<uint32_t>(shape.width, 0));
  auto image = session.image(CL_MEM_READ_WRITE, format, describe(shape.type, shape.width, shape.height, shape.depth, shape.layers, buffer));
  const auto source = fmt::format(R"cl(
    kernel void fill(write_only {0} image) {{
      size_t x = get_global_id(0), y = get_global_id(1), z = get_global_id(2);
      write_imageui(image, {1}, (uint4)((uint)(x + 100 * y + 10000 * z), 0, 0, 0));
    }}
    kernel void sum(read_only {0} image, global uint *out) {{
      size_t x = get_global_id(0), y = get_global_id(1), z = get_global_id(2);
      atomic_add(out, read_imageui(image, {1}).x);
    }}
    kernel void query(read_only {0} image, global int *out) {{
      out[0] = get_image_width(image);
      out[1] = {2};
      out[2] = {3};
      out[3] = {4};
      out[4] = get_image_channel_order(image);
      out[5] = get_image_channel_data_type(image);
    }})cl",
                                  shape.imageType, shape.coordinate, shape.height ? "get_image_height(image)" : "0",
                                  shape.depth ? "get_image_depth(image)" : "0", shape.layers ? "(int)get_image_array_size(image)" : "0");
  const size_t rows = std::max<size_t>(shape.height, 1),
               slices = std::max(std::max<size_t>(shape.depth, 1), std::max<size_t>(shape.layers, 1));
  const Shape global{shape.width, rows, slices};
  const auto dimensions = static_cast<cl_uint>(slices > 1 ? 3 : rows > 1 ? 2 : 1);
  auto fill = session.kernel(source.c_str(), "", "fill");
  check(clSetKernelArg(fill, 0, sizeof(image), &image), "fill argument");
  session.run(fill, dimensions, global, nullptr);

  std::vector<uint32_t> texels(shape.width * rows * slices);
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {shape.width, shape.type == CL_MEM_OBJECT_IMAGE1D_ARRAY ? shape.layers : rows,
                           shape.type == CL_MEM_OBJECT_IMAGE1D_ARRAY ? 1 : slices};
  check(clEnqueueReadImage(session.queue, image, CL_TRUE, origin, region, 0, 0, texels.data(), 0, nullptr, nullptr), "read image");
  uint64_t expectedSum = 0;
  for (size_t z = 0; z < slices; ++z)
    for (size_t y = 0; y < rows; ++y)
      for (size_t x = 0; x < shape.width; ++x) {
        const auto expected = static_cast<uint32_t>(x + 100 * y + 10000 * z);
        expectedSum += expected;
        INFO(fmt::format("texel ({}, {}, {})", x, y, z));
        REQUIRE(texels[(z * rows + y) * shape.width + x] == expected);
      }

  auto out = session.buffer(std::vector<uint32_t>{0});
  auto sum = session.kernel(source.c_str(), "", "sum");
  check(clSetKernelArg(sum, 0, sizeof(image), &image), "sum image argument");
  check(clSetKernelArg(sum, 1, sizeof(out), &out), "sum output argument");
  session.run(sum, dimensions, global, nullptr);
  CHECK(session.read<uint32_t>(out, 1)[0] == static_cast<uint32_t>(expectedSum));

  auto info = session.buffer(std::vector<int32_t>(6, -1));
  auto query = session.kernel(source.c_str(), "", "query");
  check(clSetKernelArg(query, 0, sizeof(image), &image), "query image argument");
  check(clSetKernelArg(query, 1, sizeof(info), &info), "query output argument");
  session.run(query, 1, {1, 1, 1}, nullptr);
  const auto reported = session.read<int32_t>(info, 6);
  CHECK(reported[0] == static_cast<int32_t>(shape.width));
  CHECK(reported[1] == static_cast<int32_t>(shape.height));
  CHECK(reported[2] == static_cast<int32_t>(shape.depth));
  CHECK(reported[3] == static_cast<int32_t>(shape.layers));
  CHECK(reported[4] == CL_R);
  CHECK(reported[5] == CL_UNSIGNED_INT32);
}

void readWrite() {
  Session session;
  session.require(Capability::Images);
  session.require(Capability::ReadWriteImages);
  const cl_image_format format{CL_RGBA, CL_SIGNED_INT16};
  requireFormat(session, CL_MEM_KERNEL_READ_AND_WRITE, CL_MEM_OBJECT_IMAGE2D, format);
  constexpr size_t width = 6, height = 4;
  std::vector<int16_t> initial(width * height * 4);
  for (size_t index = 0; index < initial.size(); ++index)
    initial[index] = static_cast<int16_t>(index * 7) - 50;
  auto image =
      session.image(CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, format, describe(CL_MEM_OBJECT_IMAGE2D, width, height), initial.data());
  auto kernel = session.kernel(R"cl(
    kernel void bump(read_write image2d_t image) {
      int2 p = (int2)(get_global_id(0), get_global_id(1));
      write_imagei(image, p, read_imagei(image, p) + (int4)(1, 2, 3, 4));
    })cl",
                               "", "bump");
  check(clSetKernelArg(kernel, 0, sizeof(image), &image), "image argument");
  session.run(kernel, 2, {width, height, 1}, nullptr);
  std::vector<int16_t> result(initial.size());
  const size_t origin[] = {0, 0, 0}, region[] = {width, height, 1};
  check(clEnqueueReadImage(session.queue, image, CL_TRUE, origin, region, 0, 0, result.data(), 0, nullptr, nullptr), "read image");
  for (size_t index = 0; index < result.size(); ++index) {
    INFO(fmt::format("component {}", index));
    REQUIRE(result[index] == initial[index] + static_cast<int16_t>(index % 4 + 1));
  }
}

void transfers() {
  Session session;
  session.require(Capability::Images);
  const cl_image_format format{CL_RGBA, CL_UNORM_INT8};
  constexpr size_t width = 5, height = 4, hostPitch = 24;
  std::vector<uint8_t> host(hostPitch * height, 0xee);
  for (size_t y = 0; y < height; ++y)
    for (size_t x = 0; x < width * 4; ++x)
      host[y * hostPitch + x] = static_cast<uint8_t>(y * 40 + x);
  auto image = session.image(CL_MEM_READ_WRITE, format, describe(CL_MEM_OBJECT_IMAGE2D, width, height));
  const size_t origin[] = {0, 0, 0}, region[] = {width, height, 1};
  check(clEnqueueWriteImage(session.queue, image, CL_TRUE, origin, region, hostPitch, 0, host.data(), 0, nullptr, nullptr), "write image");

  const size_t inner[] = {1, 1, 0}, innerRegion[] = {3, 2, 1};
  std::vector<uint8_t> window(3 * 2 * 4);
  check(clEnqueueReadImage(session.queue, image, CL_TRUE, inner, innerRegion, 0, 0, window.data(), 0, nullptr, nullptr), "read window");
  for (size_t y = 0; y < 2; ++y)
    for (size_t x = 0; x < 12; ++x)
      CHECK(window[y * 12 + x] == host[(y + 1) * hostPitch + 4 + x]);

  auto copy = session.image(CL_MEM_READ_WRITE, format, describe(CL_MEM_OBJECT_IMAGE2D, 3, 2));
  check(clEnqueueCopyImage(session.queue, image, copy, inner, origin, innerRegion, 0, nullptr, nullptr), "copy image");
  auto staging = session.buffer(std::vector<uint8_t>(64, 0));
  check(clEnqueueCopyImageToBuffer(session.queue, copy, staging, origin, innerRegion, 8, 0, nullptr, nullptr), "copy to buffer");
  const auto staged = session.read<uint8_t>(staging, 64);
  CHECK(std::equal(window.begin(), window.end(), staged.begin() + 8));

  auto restored = session.image(CL_MEM_READ_WRITE, format, describe(CL_MEM_OBJECT_IMAGE2D, 3, 2));
  check(clEnqueueCopyBufferToImage(session.queue, staging, restored, 8, origin, innerRegion, 0, nullptr, nullptr), "copy to image");
  std::vector<uint8_t> roundTripped(window.size());
  check(clEnqueueReadImage(session.queue, restored, CL_TRUE, origin, innerRegion, 0, 0, roundTripped.data(), 0, nullptr, nullptr),
        "read restored");
  CHECK(roundTripped == window);

  const cl_float4 colour{{1.0f, 0.5f, 0.25f, 0.0f}};
  const size_t corner[] = {1, 0, 0}, cornerRegion[] = {1, 1, 1};
  check(clEnqueueFillImage(session.queue, restored, &colour, corner, cornerRegion, 0, nullptr, nullptr), "fill image");
  std::array<uint8_t, 4> filled{};
  check(clEnqueueReadImage(session.queue, restored, CL_TRUE, corner, cornerRegion, 0, 0, filled.data(), 0, nullptr, nullptr), "read fill");
  CHECK(filled[0] == 255);
  CHECK((filled[1] == 127 || filled[1] == 128));
  CHECK((filled[2] == 63 || filled[2] == 64));
  CHECK(filled[3] == 0);

  size_t rowPitch = 0;
  cl_int status = CL_SUCCESS;
  auto *mapped = static_cast<uint8_t *>(clEnqueueMapImage(session.queue, image, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, inner, innerRegion,
                                                          &rowPitch, nullptr, 0, nullptr, nullptr, &status));
  check(status, "map image");
  for (size_t y = 0; y < 2; ++y)
    CHECK(std::equal(mapped + y * rowPitch, mapped + y * rowPitch + 12, window.begin() + y * 12));
  mapped[rowPitch] = 0x42;
  check(clEnqueueUnmapMemObject(session.queue, image, mapped, 0, nullptr, nullptr), "unmap image");
  std::array<uint8_t, 4> texel{};
  const size_t secondRow[] = {1, 2, 0};
  check(clEnqueueReadImage(session.queue, image, CL_TRUE, secondRow, cornerRegion, 0, 0, texel.data(), 0, nullptr, nullptr), "read mapped");
  CHECK(texel[0] == 0x42);
}
constexpr const char *AliasSource = R"cl(
  kernel void swizzle(read_only image2d_t source, write_only image2d_t target) {
    int2 at = (int2)(get_global_id(0), get_global_id(1));
    write_imagef(target, at, read_imagef(source, at).wzyx);
  }
  kernel void linear(read_only image2d_t view, global float *out) {
    out[get_global_id(0)] = read_imagef(view, (int2)(get_global_id(0), 0)).x;
  })cl";

void bufferImages() {
  Session session;
  session.require(Capability::Images);
  session.require(Capability::Image2DFromBuffer);
  cl_uint alignment = 0;
  check(clGetDeviceInfo(session.device, CL_DEVICE_IMAGE_PITCH_ALIGNMENT, sizeof(alignment), &alignment, nullptr), "pitch alignment");
  constexpr size_t width = 5, height = 3;
  const size_t step = alignment * 4, pitch = (width * 4 + step - 1) / step * step + step;
  std::vector<uint8_t> input(pitch * height, 0xee);
  for (size_t y = 0; y < height; ++y)
    for (size_t x = 0; x < width * 4; ++x)
      input[y * pitch + x] = static_cast<uint8_t>(y * 50 + x);
  auto sourceBuffer = session.buffer(input);
  auto targetBuffer = session.buffer(std::vector<uint8_t>(pitch * height, 0x77));
  const cl_image_format format{CL_RGBA, CL_UNORM_INT8};
  auto described = [&](cl_mem buffer) {
    auto desc = describe(CL_MEM_OBJECT_IMAGE2D, width, height, 0, 0, buffer);
    desc.image_row_pitch = pitch;
    return desc;
  };
  auto source = session.image(CL_MEM_READ_ONLY, format, described(sourceBuffer));
  auto target = session.image(CL_MEM_WRITE_ONLY, format, described(targetBuffer));
  auto kernel = session.kernel(AliasSource, "", "swizzle");
  check(clSetKernelArg(kernel, 0, sizeof(source), &source), "source");
  check(clSetKernelArg(kernel, 1, sizeof(target), &target), "target");
  session.run(kernel, 2, {width, height, 1}, nullptr);
  const auto output = session.read<uint8_t>(targetBuffer, pitch * height);
  for (size_t y = 0; y < height; ++y)
    for (size_t x = 0; x < pitch; ++x) {
      INFO(fmt::format("row {} byte {}", y, x));
      CHECK(output[y * pitch + x] == (x < width * 4 ? input[y * pitch + x / 4 * 4 + 3 - x % 4] : 0x77));
    }
}

void srgbView() {
  Session session;
  session.require(Capability::Images);
  requireFormat(session, CL_MEM_READ_ONLY, CL_MEM_OBJECT_IMAGE2D, {CL_sRGBA, CL_UNORM_INT8});
  const std::vector<uint8_t> texels{0, 0, 0, 255, 128, 0, 0, 255, 255, 0, 0, 255};
  auto image = session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, {CL_RGBA, CL_UNORM_INT8}, describe(CL_MEM_OBJECT_IMAGE2D, 3, 1),
                             const_cast<uint8_t *>(texels.data()));
  // No query reports whether a 2D image can view another, and several OpenCL 3.0 drivers reject it.
  const cl_image_format srgb{CL_sRGBA, CL_UNORM_INT8};
  const auto desc = describe(CL_MEM_OBJECT_IMAGE2D, 3, 1, 0, 0, image);
  cl_int status = CL_SUCCESS;
  auto view = clCreateImage(session.context, CL_MEM_READ_ONLY, &srgb, &desc, nullptr, &status);
  if (status != CL_SUCCESS) SKIP(fmt::format("2D image views of images unavailable ({})", status));
  auto out = session.buffer(std::vector<float>(3, -1.0f));
  auto kernel = session.kernel(AliasSource, "", "linear");
  check(clSetKernelArg(kernel, 0, sizeof(view), &view), "view");
  check(clSetKernelArg(kernel, 1, sizeof(out), &out), "output");
  session.run(kernel, 1, {3, 1, 1}, nullptr);
  const auto values = session.read<float>(out, 3);
  CHECK(values[0] == 0.0f);
  CHECK(std::abs(values[1] - 0.2158605f) < 1e-3f);
  CHECK(std::abs(values[2] - 1.0f) < 1e-3f);
  clReleaseMemObject(view);
}
constexpr const char *DepthSource = R"cl(
  kernel void depth_copy(read_only image2d_depth_t source, write_only image2d_depth_t target, global float *out, global int *info) {
    int2 at = (int2)(get_global_id(0), get_global_id(1));
    float depth = read_imagef(source, at);
    write_imagef(target, at, 1.0f - depth);
    out[at.y * get_image_width(source) + at.x] = depth;
    if (at.x == 0 && at.y == 0) {
      info[0] = get_image_channel_order(source);
      info[1] = get_image_channel_data_type(source);
    }
  }
  kernel void depth_layers(read_only image2d_array_depth_t source, sampler_t sampler, global float *out) {
    int x = (int)get_global_id(0);
    out[x] = read_imagef(source, sampler, (float4)(x + 0.5f, 0.5f, 1.0f, 0.0f));
  })cl";

void depthImages(cl_channel_type type) {
  Session session;
  session.require(Capability::Images);
  session.require(Capability::DepthImages);
  const cl_image_format format{CL_DEPTH, type};
  requireFormat(session, CL_MEM_READ_ONLY, CL_MEM_OBJECT_IMAGE2D, format);
  requireFormat(session, CL_MEM_WRITE_ONLY, CL_MEM_OBJECT_IMAGE2D, format);
  requireFormat(session, CL_MEM_READ_ONLY, CL_MEM_OBJECT_IMAGE2D_ARRAY, format);
  constexpr size_t width = 4, height = 2;
  const std::vector<float> depths{0.0f, 0.25f, 0.5f, 1.0f, 0.125f, 0.75f, 0.375f, 0.625f};
  const auto store = [&](const std::vector<float> &values) {
    std::vector<uint8_t> bytes;
    for (const float value : values) {
      if (type == CL_FLOAT) {
        const auto *raw = reinterpret_cast<const uint8_t *>(&value);
        bytes.insert(bytes.end(), raw, raw + 4);
      } else {
        const auto encoded = static_cast<uint16_t>(std::lround(value * 65535.0f));
        bytes.push_back(static_cast<uint8_t>(encoded));
        bytes.push_back(static_cast<uint8_t>(encoded >> 8));
      }
    }
    return bytes;
  };
  auto input = store(depths);
  auto source =
      session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, describe(CL_MEM_OBJECT_IMAGE2D, width, height), input.data());
  auto target = session.image(CL_MEM_WRITE_ONLY, format, describe(CL_MEM_OBJECT_IMAGE2D, width, height));
  auto out = session.buffer(std::vector<float>(width * height, -1.0f));
  auto info = session.buffer(std::vector<int32_t>(2, 0));
  auto copy = session.kernel(DepthSource, "", "depth_copy");
  check(clSetKernelArg(copy, 0, sizeof(source), &source), "source");
  check(clSetKernelArg(copy, 1, sizeof(target), &target), "target");
  check(clSetKernelArg(copy, 2, sizeof(out), &out), "output");
  check(clSetKernelArg(copy, 3, sizeof(info), &info), "info");
  session.run(copy, 2, {width, height, 1}, nullptr);
  const float tolerance = type == CL_FLOAT ? 0.0f : 1.0f / 65535.0f;
  const auto read = session.read<float>(out, width * height);
  for (size_t index = 0; index < depths.size(); ++index) {
    INFO(fmt::format("texel {}", index));
    CHECK(std::abs(read[index] - depths[index]) <= tolerance);
  }
  CHECK(session.read<int32_t>(info, 2) == std::vector<int32_t>{CL_DEPTH, static_cast<int32_t>(type)});
  std::vector<uint8_t> written(input.size());
  const size_t origin[] = {0, 0, 0}, region[] = {width, height, 1};
  check(clEnqueueReadImage(session.queue, target, CL_TRUE, origin, region, 0, 0, written.data(), 0, nullptr, nullptr), "read target");
  std::vector<float> inverted;
  for (const float value : depths)
    inverted.push_back(1.0f - value);
  const auto expected = store(inverted);
  for (size_t index = 0; index < expected.size(); index += input.size() / depths.size()) {
    INFO(fmt::format("written texel byte {}", index));
    if (type == CL_FLOAT) {
      CHECK(std::memcmp(written.data() + index, expected.data() + index, 4) == 0);
    } else {
      const int actual = written[index] | written[index + 1] << 8, wanted = expected[index] | expected[index + 1] << 8;
      CHECK(std::abs(actual - wanted) <= 1);
    }
  }

  auto layered = store({0.0f, 0.0f, 0.0f, 0.5f, 0.25f, 1.0f});
  auto array =
      session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, describe(CL_MEM_OBJECT_IMAGE2D_ARRAY, 3, 1, 0, 2), layered.data());
  auto sampler = session.sampler(CL_FALSE, CL_ADDRESS_CLAMP_TO_EDGE, CL_FILTER_NEAREST);
  auto layers = session.buffer(std::vector<float>(3, -1.0f));
  auto sample = session.kernel(DepthSource, "", "depth_layers");
  check(clSetKernelArg(sample, 0, sizeof(array), &array), "array");
  check(clSetKernelArg(sample, 1, sizeof(sampler), &sampler), "sampler");
  check(clSetKernelArg(sample, 2, sizeof(layers), &layers), "layers");
  session.run(sample, 1, {3, 1, 1}, nullptr);
  const auto sampled = session.read<float>(layers, 3);
  const std::array<float, 3> wanted{0.5f, 0.25f, 1.0f};
  for (size_t index = 0; index < 3; ++index) {
    INFO(fmt::format("layer texel {}", index));
    CHECK(std::abs(sampled[index] - wanted[index]) <= tolerance);
  }
}
} // namespace

#define UNIFIED_OPENCL_ROUND_TRIP(NAME, ORDER, TYPE, SIZE, ACCESS)                                                                         \
  TEST_CASE("edge-cases/runtime/opencl/images/round-trip/" NAME, "[unified][runtime][compact]") {                                          \
    roundTrip({{ORDER, TYPE}, SIZE, Access::ACCESS});                                                                                      \
  }

UNIFIED_OPENCL_ROUND_TRIP("rgba-unorm8", CL_RGBA, CL_UNORM_INT8, 4, Float)
UNIFIED_OPENCL_ROUND_TRIP("bgra-unorm8", CL_BGRA, CL_UNORM_INT8, 4, Float)
UNIFIED_OPENCL_ROUND_TRIP("rgba-snorm16", CL_RGBA, CL_SNORM_INT16, 8, Float)
UNIFIED_OPENCL_ROUND_TRIP("r-unorm16", CL_R, CL_UNORM_INT16, 2, Float)
UNIFIED_OPENCL_ROUND_TRIP("rg-half", CL_RG, CL_HALF_FLOAT, 4, Float)
UNIFIED_OPENCL_ROUND_TRIP("r-float", CL_R, CL_FLOAT, 4, Float)
UNIFIED_OPENCL_ROUND_TRIP("rgba-sint8", CL_RGBA, CL_SIGNED_INT8, 4, Int)
UNIFIED_OPENCL_ROUND_TRIP("rgba-sint32", CL_RGBA, CL_SIGNED_INT32, 16, Int)
UNIFIED_OPENCL_ROUND_TRIP("r-uint8", CL_R, CL_UNSIGNED_INT8, 1, Uint)
UNIFIED_OPENCL_ROUND_TRIP("rg-uint32", CL_RG, CL_UNSIGNED_INT32, 8, Uint)

TEST_CASE("edge-cases/runtime/opencl/images/sampling/nearest-clamp-to-edge", "[unified][runtime][compact]") {
  sampling({CL_FALSE,
            CL_ADDRESS_CLAMP_TO_EDGE,
            CL_FILTER_NEAREST,
            {{-1.5f, 0.5f}, {0.5f, 0.5f}, {1.7f, 0.5f}, {3.5f, 0.5f}, {9.0f, 0.5f}},
            {{0.0f, 1.0f}, {0.0f, 1.0f}, {1.0f, 1.0f}, {3.0f, 1.0f}, {3.0f, 1.0f}}});
}

TEST_CASE("edge-cases/runtime/opencl/images/sampling/nearest-clamp", "[unified][runtime][compact]") {
  sampling({CL_FALSE,
            CL_ADDRESS_CLAMP,
            CL_FILTER_NEAREST,
            {{-1.5f, 0.5f}, {2.5f, 0.5f}, {4.5f, 0.5f}},
            {{0.0f, 1.0f}, {2.0f, 1.0f}, {0.0f, 1.0f}}});
}

TEST_CASE("edge-cases/runtime/opencl/images/sampling/linear-repeat", "[unified][runtime][compact]") {
  sampling({CL_TRUE,
            CL_ADDRESS_REPEAT,
            CL_FILTER_LINEAR,
            {{0.125f, 0.5f}, {0.25f, 0.5f}, {1.0f, 0.5f}, {1.125f, 0.5f}},
            {{0.0f, 1.0f}, {0.5f, 1.0f}, {1.5f, 1.0f}, {0.0f, 1.0f}}});
}

TEST_CASE("edge-cases/runtime/opencl/images/sampling/nearest-mirrored-repeat", "[unified][runtime][compact]") {
  sampling({CL_TRUE,
            CL_ADDRESS_MIRRORED_REPEAT,
            CL_FILTER_NEAREST,
            {{1.3f, 0.5f}, {1.625f, 0.5f}, {-0.125f, 0.5f}, {0.6f, 0.5f}},
            {{2.0f, 1.0f}, {1.0f, 1.0f}, {0.0f, 1.0f}, {2.0f, 1.0f}}});
}

TEST_CASE("edge-cases/runtime/opencl/images/sampling/integer-coordinates", "[unified][runtime][compact]") { integerSampling(); }

TEST_CASE("edge-cases/runtime/opencl/images/layout/1d", "[unified][runtime][compact]") {
  layouts({CL_MEM_OBJECT_IMAGE1D, 37, 0, 0, 0, "image1d_t", "(int)x", false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/1d-buffer", "[unified][runtime][compact]") {
  layouts({CL_MEM_OBJECT_IMAGE1D_BUFFER, 33, 0, 0, 0, "image1d_buffer_t", "(int)x", false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/1d-array", "[unified][runtime][compact]") {
  layouts({CL_MEM_OBJECT_IMAGE1D_ARRAY, 9, 0, 0, 5, "image1d_array_t", "(int2)(x, z)", false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/2d", "[unified][runtime][compact]") {
  layouts({CL_MEM_OBJECT_IMAGE2D, 13, 7, 0, 0, "image2d_t", "(int2)(x, y)", false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/2d-array", "[unified][runtime][compact]") {
  layouts({CL_MEM_OBJECT_IMAGE2D_ARRAY, 7, 5, 0, 3, "image2d_array_t", "(int4)(x, y, z, 0)", false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/3d", "[unified][runtime][compact]") {
  layouts({CL_MEM_OBJECT_IMAGE3D, 5, 4, 3, 0, "image3d_t", "(int4)(x, y, z, 0)", true});
}

TEST_CASE("edge-cases/runtime/opencl/images/read-write/rgba-sint16", "[unified][runtime][compact]") { readWrite(); }

TEST_CASE("edge-cases/runtime/opencl/images/transfers/rgba-unorm8", "[unified][runtime][compact]") { transfers(); }

TEST_CASE("edge-cases/runtime/opencl/images/aliasing/2d-from-buffer-pitched", "[unified][runtime][compact]") { bufferImages(); }

TEST_CASE("edge-cases/runtime/opencl/images/aliasing/srgb-view", "[unified][runtime][compact]") { srgbView(); }

TEST_CASE("edge-cases/runtime/opencl/images/depth/unorm16", "[unified][runtime][compact]") { depthImages(CL_UNORM_INT16); }

TEST_CASE("edge-cases/runtime/opencl/images/depth/float", "[unified][runtime][compact]") { depthImages(CL_FLOAT); }
