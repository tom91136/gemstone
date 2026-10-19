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
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

#include "opencl/session.hpp"

using namespace aspartame;

namespace {
using namespace unified::opencl;

enum class Access { Float, Int, Uint };

void requireFormat(Session &session, cl_mem_flags flags, cl_mem_object_type type, cl_image_format format) {
  cl_uint count = 0;
  check(clGetSupportedImageFormats(session.context, flags, type, 0, nullptr, &count), "format count");
  std::vector<cl_image_format> formats(count);
  check(clGetSupportedImageFormats(session.context, flags, type, count, formats.data(), nullptr), "formats");
  if (formats ^ none_match([&](const cl_image_format &entry) {
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
  auto bytes = iota<size_t>(0, count) | map([](size_t index) { return static_cast<uint8_t>(((index * 37) + 11) % 251); }) | to_vector();
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
    case CL_FLOAT: patch(4, [](size_t i) { return (static_cast<float>(i) * 0.25F) - 3.0F; }); break;
    default: break;
  }
  return bytes;
}

struct RoundTrip {
  cl_image_format format;
  size_t elementSize;
  Access access;
};

const char *accessOptions(Access access) {
  switch (access) {
    case Access::Float: return "-DREAD=read_imagef -DWRITE=write_imagef";
    case Access::Int: return "-DREAD=read_imagei -DWRITE=write_imagei";
    case Access::Uint: return "-DREAD=read_imageui -DWRITE=write_imageui";
  }
  return "";
}

void roundTrip(const RoundTrip &shape) {
  Session session;
  session.require(Capability::Images);
  requireFormat(session, CL_MEM_READ_ONLY, CL_MEM_OBJECT_IMAGE2D, shape.format);
  requireFormat(session, CL_MEM_WRITE_ONLY, CL_MEM_OBJECT_IMAGE2D, shape.format);
  constexpr size_t width = 7;
  constexpr size_t height = 5;
  auto input = texelBytes(shape.format.image_channel_data_type, width * height * shape.elementSize);
  auto *source =
      session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, shape.format, describe(CL_MEM_OBJECT_IMAGE2D, width, height), input.data());
  auto *target = session.image(CL_MEM_WRITE_ONLY, shape.format, describe(CL_MEM_OBJECT_IMAGE2D, width, height));
  const char *sourceText = R"cl(
    kernel void copy(read_only image2d_t src, write_only image2d_t dst) {
      int2 p = (int2)(get_global_id(0), get_global_id(1));
      WRITE(dst, p, READ(src, p));
    })cl";
  auto *kernel = session.kernel(sourceText, accessOptions(shape.access), "copy");
  arg(kernel, 0, source, "source argument");
  arg(kernel, 1, target, "target argument");
  session.run(kernel, 2, {width, height, 1}, nullptr);
  std::vector<uint8_t> output(input.size());
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {width, height, 1};
  session.readImage(target, origin, region, output.data(), "read image");
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
  const cl_image_format format{.image_channel_order = CL_R, .image_channel_data_type = CL_FLOAT};
  std::vector<float> ramp{0.0F, 1.0F, 2.0F, 3.0F};
  auto *image = session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, describe(CL_MEM_OBJECT_IMAGE2D, 4, 1), ramp.data());
  auto *sampler = session.sampler(shape.normalised, shape.addressing, shape.filter);
  auto *kernel = session.kernel(SamplingSource, "", "sample");
  const auto coordinates = shape.coordinates ^ flatten();
  auto *coordinateBuffer = session.buffer(coordinates);
  auto *out = session.buffer(std::vector<float>(shape.coordinates.size() * 4, -1.0F));
  arg(kernel, 0, image, "image argument");
  arg(kernel, 1, sampler, "sampler argument");
  arg(kernel, 2, coordinateBuffer, "coordinate argument");
  arg(kernel, 3, out, "output argument");
  session.run(kernel, 1, {shape.coordinates.size(), 1, 1}, nullptr);
  const auto texels = session.read<float>(out, shape.coordinates.size() * 4);
  for (size_t index = 0; index < shape.coordinates.size(); ++index) {
    INFO(fmt::format("coordinate ({}, {})", shape.coordinates[index][0], shape.coordinates[index][1]));
    CHECK(std::abs(texels[index * 4] - shape.expected[index][0]) <= 1e-3F);
    CHECK(texels[(index * 4) + 1] == 0.0F);
    CHECK(texels[(index * 4) + 2] == 0.0F);
    CHECK(std::abs(texels[(index * 4) + 3] - shape.expected[index][1]) <= 1e-3F);
  }
}

void integerSampling() {
  Session session;
  session.require(Capability::Images);
  std::vector<float> ramp{0.0F, 1.0F, 2.0F, 3.0F};
  auto *image = session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, {.image_channel_order = CL_R, .image_channel_data_type = CL_FLOAT},
                              describe(CL_MEM_OBJECT_IMAGE2D, 4, 1), ramp.data());
  auto *sampler = session.sampler(CL_FALSE, CL_ADDRESS_CLAMP_TO_EDGE, CL_FILTER_NEAREST);
  auto *kernel = session.kernel(SamplingSource, "", "sample_int");
  auto *coordinates = session.buffer(std::vector<int32_t>{0, 0, 2, 0, 3, 0, -4, 0, 9, 0});
  auto *out = session.buffer(std::vector<float>(20, -1.0F));
  arg(kernel, 0, image, "image argument");
  arg(kernel, 1, sampler, "sampler argument");
  arg(kernel, 2, coordinates, "coordinate argument");
  arg(kernel, 3, out, "output argument");
  session.run(kernel, 1, {5, 1, 1}, nullptr);
  const auto texels = session.read<float>(out, 20);
  const std::array<float, 5> expected{0.0F, 2.0F, 3.0F, 0.0F, 3.0F};
  for (size_t index = 0; index < expected.size(); ++index) {
    INFO(fmt::format("coordinate {}", index));
    CHECK(texels[index * 4] == expected[index]);
    CHECK(texels[(index * 4) + 3] == 1.0F);
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
  const cl_image_format format{.image_channel_order = CL_R, .image_channel_data_type = CL_UNSIGNED_INT32};
  requireFormat(session, CL_MEM_WRITE_ONLY, shape.type, format);
  cl_mem buffer = nullptr;
  if (shape.type == CL_MEM_OBJECT_IMAGE1D_BUFFER) buffer = session.buffer(std::vector<uint32_t>(shape.width, 0));
  auto *image =
      session.image(CL_MEM_READ_WRITE, format, describe(shape.type, shape.width, shape.height, shape.depth, shape.layers, buffer));
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
  const size_t rows = std::max<size_t>(shape.height, 1);
  const size_t slices = std::max({size_t{1}, shape.depth, shape.layers});
  const Shape global{shape.width, rows, slices};
  cl_uint dimensions = 1;
  if (slices > 1) dimensions = 3;
  else if (rows > 1) dimensions = 2;
  auto *fill = session.kernel(source.c_str(), "", "fill");
  arg(fill, 0, image, "fill argument");
  session.run(fill, dimensions, global, nullptr);

  std::vector<uint32_t> texels(shape.width * rows * slices);
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {shape.width, shape.type == CL_MEM_OBJECT_IMAGE1D_ARRAY ? shape.layers : rows,
                           shape.type == CL_MEM_OBJECT_IMAGE1D_ARRAY ? 1 : slices};
  session.readImage(image, origin, region, texels.data(), "read image");
  uint64_t expectedSum = 0;
  for (size_t z = 0; z < slices; ++z)
    for (size_t y = 0; y < rows; ++y)
      for (size_t x = 0; x < shape.width; ++x) {
        const auto expected = static_cast<uint32_t>(x + (100 * y) + (10000 * z));
        expectedSum += expected;
        INFO(fmt::format("texel ({}, {}, {})", x, y, z));
        REQUIRE(texels[(((z * rows) + y) * shape.width) + x] == expected);
      }

  auto *out = session.buffer(std::vector<uint32_t>{0});
  auto *sum = session.kernel(source.c_str(), "", "sum");
  arg(sum, 0, image, "sum image argument");
  arg(sum, 1, out, "sum output argument");
  session.run(sum, dimensions, global, nullptr);
  CHECK(session.read<uint32_t>(out, 1)[0] == static_cast<uint32_t>(expectedSum));

  auto *info = session.buffer(std::vector<int32_t>(6, -1));
  auto *query = session.kernel(source.c_str(), "", "query");
  arg(query, 0, image, "query image argument");
  arg(query, 1, info, "query output argument");
  session.run(query, 1, {1, 1, 1}, nullptr);
  const auto reported = session.read<int32_t>(info, 6);
  CHECK(std::cmp_equal(reported[0], shape.width));
  CHECK(std::cmp_equal(reported[1], shape.height));
  CHECK(std::cmp_equal(reported[2], shape.depth));
  CHECK(std::cmp_equal(reported[3], shape.layers));
  CHECK(reported[4] == CL_R);
  CHECK(reported[5] == CL_UNSIGNED_INT32);
}

void readWrite() {
  Session session;
  session.require(Capability::Images);
  session.require(Capability::ReadWriteImages);
  const cl_image_format format{.image_channel_order = CL_RGBA, .image_channel_data_type = CL_SIGNED_INT16};
  requireFormat(session, CL_MEM_KERNEL_READ_AND_WRITE, CL_MEM_OBJECT_IMAGE2D, format);
  constexpr size_t width = 6;
  constexpr size_t height = 4;
  auto initial = iota<size_t>(0, width * height * 4)
                 | map([](size_t index) { return static_cast<int16_t>(static_cast<int>(index * 7) - 50); }) | to_vector();
  auto *image =
      session.image(CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, format, describe(CL_MEM_OBJECT_IMAGE2D, width, height), initial.data());
  auto *kernel = session.kernel(R"cl(
    kernel void bump(read_write image2d_t image) {
      int2 p = (int2)(get_global_id(0), get_global_id(1));
      write_imagei(image, p, read_imagei(image, p) + (int4)(1, 2, 3, 4));
    })cl",
                                "", "bump");
  arg(kernel, 0, image, "image argument");
  session.run(kernel, 2, {width, height, 1}, nullptr);
  std::vector<int16_t> result(initial.size());
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {width, height, 1};
  session.readImage(image, origin, region, result.data(), "read image");
  for (size_t index = 0; index < result.size(); ++index) {
    INFO(fmt::format("component {}", index));
    REQUIRE(result[index] == initial[index] + static_cast<int16_t>((index % 4) + 1));
  }
}

void transfers() {
  Session session;
  session.require(Capability::Images);
  const cl_image_format format{.image_channel_order = CL_RGBA, .image_channel_data_type = CL_UNORM_INT8};
  constexpr size_t width = 5;
  constexpr size_t height = 4;
  constexpr size_t hostPitch = 24;
  std::vector<uint8_t> host(hostPitch * height, 0xee);
  for (size_t y = 0; y < height; ++y)
    for (size_t x = 0; x < width * 4; ++x)
      host[(y * hostPitch) + x] = static_cast<uint8_t>((y * 40) + x);
  auto *image = session.image(CL_MEM_READ_WRITE, format, describe(CL_MEM_OBJECT_IMAGE2D, width, height));
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {width, height, 1};
  check(clEnqueueWriteImage(session.queue, image, CL_TRUE, origin, region, hostPitch, 0, host.data(), 0, nullptr, nullptr), "write image");

  const size_t inner[] = {1, 1, 0};
  const size_t innerRegion[] = {3, 2, 1};
  std::vector<uint8_t> window(size_t{3} * 2 * 4);
  session.readImage(image, inner, innerRegion, window.data(), "read window");
  for (size_t y = 0; y < 2; ++y)
    for (size_t x = 0; x < 12; ++x)
      CHECK(window[(y * 12) + x] == host[((y + 1) * hostPitch) + 4 + x]);

  auto *copy = session.image(CL_MEM_READ_WRITE, format, describe(CL_MEM_OBJECT_IMAGE2D, 3, 2));
  check(clEnqueueCopyImage(session.queue, image, copy, inner, origin, innerRegion, 0, nullptr, nullptr), "copy image");
  auto *staging = session.buffer(std::vector<uint8_t>(64, 0));
  check(clEnqueueCopyImageToBuffer(session.queue, copy, staging, origin, innerRegion, 8, 0, nullptr, nullptr), "copy to buffer");
  const auto staged = session.read<uint8_t>(staging, 64);
  CHECK((staged ^ drop(8) ^ take(window.size())) == window);

  auto *restored = session.image(CL_MEM_READ_WRITE, format, describe(CL_MEM_OBJECT_IMAGE2D, 3, 2));
  check(clEnqueueCopyBufferToImage(session.queue, staging, restored, 8, origin, innerRegion, 0, nullptr, nullptr), "copy to image");
  std::vector<uint8_t> roundTripped(window.size());
  session.readImage(restored, origin, innerRegion, roundTripped.data(), "read restored");
  CHECK(roundTripped == window);

  const cl_float4 colour{{1.0F, 0.5F, 0.25F, 0.0F}};
  const size_t corner[] = {1, 0, 0};
  const size_t cornerRegion[] = {1, 1, 1};
  check(clEnqueueFillImage(session.queue, restored, &colour, corner, cornerRegion, 0, nullptr, nullptr), "fill image");
  std::array<uint8_t, 4> filled{};
  session.readImage(restored, corner, cornerRegion, filled.data(), "read fill");
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
    CHECK(std::equal(mapped + (y * rowPitch), mapped + (y * rowPitch) + 12, window.begin() + (y * 12)));
  mapped[rowPitch] = 0x42;
  check(clEnqueueUnmapMemObject(session.queue, image, mapped, 0, nullptr, nullptr), "unmap image");
  std::array<uint8_t, 4> texel{};
  const size_t secondRow[] = {1, 2, 0};
  session.readImage(image, secondRow, cornerRegion, texel.data(), "read mapped");
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
  constexpr size_t width = 5;
  constexpr size_t height = 3;
  const size_t step = size_t{alignment} * 4;
  const size_t pitch = (((width * 4) + step - 1) / step * step) + step;
  std::vector<uint8_t> input(pitch * height, 0xee);
  for (size_t y = 0; y < height; ++y)
    for (size_t x = 0; x < width * 4; ++x)
      input[(y * pitch) + x] = static_cast<uint8_t>((y * 50) + x);
  auto *sourceBuffer = session.buffer(input);
  auto *targetBuffer = session.buffer(std::vector<uint8_t>(pitch * height, 0x77));
  const cl_image_format format{.image_channel_order = CL_RGBA, .image_channel_data_type = CL_UNORM_INT8};
  auto described = [&](cl_mem buffer) {
    auto desc = describe(CL_MEM_OBJECT_IMAGE2D, width, height, 0, 0, buffer);
    desc.image_row_pitch = pitch;
    return desc;
  };
  auto *source = session.image(CL_MEM_READ_ONLY, format, described(sourceBuffer));
  auto *target = session.image(CL_MEM_WRITE_ONLY, format, described(targetBuffer));
  auto *kernel = session.kernel(AliasSource, "", "swizzle");
  arg(kernel, 0, source, "source");
  arg(kernel, 1, target, "target");
  session.run(kernel, 2, {width, height, 1}, nullptr);
  const auto output = session.read<uint8_t>(targetBuffer, pitch * height);
  for (size_t y = 0; y < height; ++y)
    for (size_t x = 0; x < pitch; ++x) {
      INFO(fmt::format("row {} byte {}", y, x));
      CHECK(output[(y * pitch) + x] == (x < width * 4 ? input[(y * pitch) + (x / 4 * 4) + 3 - (x % 4)] : 0x77));
    }
}

void srgbView() {
  Session session;
  session.require(Capability::Images);
  requireFormat(session, CL_MEM_READ_ONLY, CL_MEM_OBJECT_IMAGE2D,
                {.image_channel_order = CL_sRGBA, .image_channel_data_type = CL_UNORM_INT8});
  std::vector<uint8_t> texels{0, 0, 0, 255, 128, 0, 0, 255, 255, 0, 0, 255};
  auto *image =
      session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, {.image_channel_order = CL_RGBA, .image_channel_data_type = CL_UNORM_INT8},
                    describe(CL_MEM_OBJECT_IMAGE2D, 3, 1), texels.data());
  // No query reports whether a 2D image can view another, and several OpenCL 3.0 drivers reject it.
  const cl_image_format srgb{.image_channel_order = CL_sRGBA, .image_channel_data_type = CL_UNORM_INT8};
  const auto desc = describe(CL_MEM_OBJECT_IMAGE2D, 3, 1, 0, 0, image);
  cl_int status = CL_SUCCESS;
  auto *view = clCreateImage(session.context, CL_MEM_READ_ONLY, &srgb, &desc, nullptr, &status);
  if (status != CL_SUCCESS) SKIP(fmt::format("2D image views of images unavailable ({})", status));
  auto *out = session.buffer(std::vector<float>(3, -1.0F));
  auto *kernel = session.kernel(AliasSource, "", "linear");
  arg(kernel, 0, view, "view");
  arg(kernel, 1, out, "output");
  session.run(kernel, 1, {3, 1, 1}, nullptr);
  const auto values = session.read<float>(out, 3);
  CHECK(values[0] == 0.0F);
  CHECK(std::abs(values[1] - 0.2158605F) < 1e-3F);
  CHECK(std::abs(values[2] - 1.0F) < 1e-3F);
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
  const cl_image_format format{.image_channel_order = CL_DEPTH, .image_channel_data_type = type};
  requireFormat(session, CL_MEM_READ_ONLY, CL_MEM_OBJECT_IMAGE2D, format);
  requireFormat(session, CL_MEM_WRITE_ONLY, CL_MEM_OBJECT_IMAGE2D, format);
  requireFormat(session, CL_MEM_READ_ONLY, CL_MEM_OBJECT_IMAGE2D_ARRAY, format);
  constexpr size_t width = 4;
  constexpr size_t height = 2;
  const std::vector<float> depths{0.0F, 0.25F, 0.5F, 1.0F, 0.125F, 0.75F, 0.375F, 0.625F};
  const auto store = [&](const std::vector<float> &values) {
    std::vector<uint8_t> bytes;
    for (const float value : values) {
      if (type == CL_FLOAT) {
        const auto *raw = reinterpret_cast<const uint8_t *>(&value);
        bytes.insert(bytes.end(), raw, raw + 4);
      } else {
        const auto encoded = static_cast<uint16_t>(std::lround(value * 65535.0F));
        bytes.push_back(static_cast<uint8_t>(encoded));
        bytes.push_back(static_cast<uint8_t>(encoded >> 8));
      }
    }
    return bytes;
  };
  auto input = store(depths);
  auto *source =
      session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, describe(CL_MEM_OBJECT_IMAGE2D, width, height), input.data());
  auto *target = session.image(CL_MEM_WRITE_ONLY, format, describe(CL_MEM_OBJECT_IMAGE2D, width, height));
  auto *out = session.buffer(std::vector<float>(width * height, -1.0F));
  auto *info = session.buffer(std::vector<int32_t>(2, 0));
  auto *copy = session.kernel(DepthSource, "", "depth_copy");
  arg(copy, 0, source, "source");
  arg(copy, 1, target, "target");
  arg(copy, 2, out, "output");
  arg(copy, 3, info, "info");
  session.run(copy, 2, {width, height, 1}, nullptr);
  const float tolerance = type == CL_FLOAT ? 0.0F : 1.0F / 65535.0F;
  const auto read = session.read<float>(out, width * height);
  for (size_t index = 0; index < depths.size(); ++index) {
    INFO(fmt::format("texel {}", index));
    CHECK(std::abs(read[index] - depths[index]) <= tolerance);
  }
  CHECK(session.read<int32_t>(info, 2) == std::vector<int32_t>{CL_DEPTH, static_cast<int32_t>(type)});
  std::vector<uint8_t> written(input.size());
  const size_t origin[] = {0, 0, 0};
  const size_t region[] = {width, height, 1};
  session.readImage(target, origin, region, written.data(), "read target");
  const auto expected = store(depths ^ map([](float value) { return 1.0F - value; }));
  for (size_t index = 0; index < expected.size(); index += input.size() / depths.size()) {
    INFO(fmt::format("written texel byte {}", index));
    if (type == CL_FLOAT) {
      CHECK(std::memcmp(written.data() + index, expected.data() + index, 4) == 0);
    } else {
      const int actual = written[index] | written[index + 1] << 8;
      const int wanted = expected[index] | expected[index + 1] << 8;
      CHECK(std::abs(actual - wanted) <= 1);
    }
  }

  auto layered = store({0.0F, 0.0F, 0.0F, 0.5F, 0.25F, 1.0F});
  auto *array =
      session.image(CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, format, describe(CL_MEM_OBJECT_IMAGE2D_ARRAY, 3, 1, 0, 2), layered.data());
  auto *sampler = session.sampler(CL_FALSE, CL_ADDRESS_CLAMP_TO_EDGE, CL_FILTER_NEAREST);
  auto *layers = session.buffer(std::vector<float>(3, -1.0F));
  auto *sample = session.kernel(DepthSource, "", "depth_layers");
  arg(sample, 0, array, "array");
  arg(sample, 1, sampler, "sampler");
  arg(sample, 2, layers, "layers");
  session.run(sample, 1, {3, 1, 1}, nullptr);
  const auto sampled = session.read<float>(layers, 3);
  const std::array<float, 3> wanted{0.5F, 0.25F, 1.0F};
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
  sampling({.normalised = CL_FALSE,
            .addressing = CL_ADDRESS_CLAMP_TO_EDGE,
            .filter = CL_FILTER_NEAREST,
            .coordinates = {{-1.5F, 0.5F}, {0.5F, 0.5F}, {1.7F, 0.5F}, {3.5F, 0.5F}, {9.0F, 0.5F}},
            .expected = {{0.0F, 1.0F}, {0.0F, 1.0F}, {1.0F, 1.0F}, {3.0F, 1.0F}, {3.0F, 1.0F}}});
}

TEST_CASE("edge-cases/runtime/opencl/images/sampling/nearest-clamp", "[unified][runtime][compact]") {
  sampling({.normalised = CL_FALSE,
            .addressing = CL_ADDRESS_CLAMP,
            .filter = CL_FILTER_NEAREST,
            .coordinates = {{-1.5F, 0.5F}, {2.5F, 0.5F}, {4.5F, 0.5F}},
            .expected = {{0.0F, 1.0F}, {2.0F, 1.0F}, {0.0F, 1.0F}}});
}

TEST_CASE("edge-cases/runtime/opencl/images/sampling/linear-repeat", "[unified][runtime][compact]") {
  sampling({.normalised = CL_TRUE,
            .addressing = CL_ADDRESS_REPEAT,
            .filter = CL_FILTER_LINEAR,
            .coordinates = {{0.125F, 0.5F}, {0.25F, 0.5F}, {1.0F, 0.5F}, {1.125F, 0.5F}},
            .expected = {{0.0F, 1.0F}, {0.5F, 1.0F}, {1.5F, 1.0F}, {0.0F, 1.0F}}});
}

TEST_CASE("edge-cases/runtime/opencl/images/sampling/nearest-mirrored-repeat", "[unified][runtime][compact]") {
  sampling({.normalised = CL_TRUE,
            .addressing = CL_ADDRESS_MIRRORED_REPEAT,
            .filter = CL_FILTER_NEAREST,
            .coordinates = {{1.3F, 0.5F}, {1.625F, 0.5F}, {-0.125F, 0.5F}, {0.6F, 0.5F}},
            .expected = {{2.0F, 1.0F}, {1.0F, 1.0F}, {0.0F, 1.0F}, {2.0F, 1.0F}}});
}

TEST_CASE("edge-cases/runtime/opencl/images/sampling/integer-coordinates", "[unified][runtime][compact]") { integerSampling(); }

TEST_CASE("edge-cases/runtime/opencl/images/layout/1d", "[unified][runtime][compact]") {
  layouts({.type = CL_MEM_OBJECT_IMAGE1D,
           .width = 37,
           .height = 0,
           .depth = 0,
           .layers = 0,
           .imageType = "image1d_t",
           .coordinate = "(int)x",
           .writes3D = false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/1d-buffer", "[unified][runtime][compact]") {
  layouts({.type = CL_MEM_OBJECT_IMAGE1D_BUFFER,
           .width = 33,
           .height = 0,
           .depth = 0,
           .layers = 0,
           .imageType = "image1d_buffer_t",
           .coordinate = "(int)x",
           .writes3D = false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/1d-array", "[unified][runtime][compact]") {
  layouts({.type = CL_MEM_OBJECT_IMAGE1D_ARRAY,
           .width = 9,
           .height = 0,
           .depth = 0,
           .layers = 5,
           .imageType = "image1d_array_t",
           .coordinate = "(int2)(x, z)",
           .writes3D = false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/2d", "[unified][runtime][compact]") {
  layouts({.type = CL_MEM_OBJECT_IMAGE2D,
           .width = 13,
           .height = 7,
           .depth = 0,
           .layers = 0,
           .imageType = "image2d_t",
           .coordinate = "(int2)(x, y)",
           .writes3D = false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/2d-array", "[unified][runtime][compact]") {
  layouts({.type = CL_MEM_OBJECT_IMAGE2D_ARRAY,
           .width = 7,
           .height = 5,
           .depth = 0,
           .layers = 3,
           .imageType = "image2d_array_t",
           .coordinate = "(int4)(x, y, z, 0)",
           .writes3D = false});
}

TEST_CASE("edge-cases/runtime/opencl/images/layout/3d", "[unified][runtime][compact]") {
  layouts({.type = CL_MEM_OBJECT_IMAGE3D,
           .width = 5,
           .height = 4,
           .depth = 3,
           .layers = 0,
           .imageType = "image3d_t",
           .coordinate = "(int4)(x, y, z, 0)",
           .writes3D = true});
}

TEST_CASE("edge-cases/runtime/opencl/images/read-write/rgba-sint16", "[unified][runtime][compact]") { readWrite(); }

TEST_CASE("edge-cases/runtime/opencl/images/transfers/rgba-unorm8", "[unified][runtime][compact]") { transfers(); }

TEST_CASE("edge-cases/runtime/opencl/images/aliasing/2d-from-buffer-pitched", "[unified][runtime][compact]") { bufferImages(); }

TEST_CASE("edge-cases/runtime/opencl/images/aliasing/srgb-view", "[unified][runtime][compact]") { srgbView(); }

TEST_CASE("edge-cases/runtime/opencl/images/depth/unorm16", "[unified][runtime][compact]") { depthImages(CL_UNORM_INT16); }

TEST_CASE("edge-cases/runtime/opencl/images/depth/float", "[unified][runtime][compact]") { depthImages(CL_FLOAT); }
