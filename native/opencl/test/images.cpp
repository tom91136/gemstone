#define CL_USE_DEPRECATED_OPENCL_1_2_APIS
#include <algorithm>
#include <array>
#include <cstdint>
#include <cstring>
#include <string>
#include <string_view>
#include <variant>
#include <vector>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include "gemstone_image_descriptor.hpp"

#include "../../rpc/test/rpc_fixture.hpp"

namespace {
cl_device_id gpu() {
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  return device;
}

template <class T> T info(cl_device_id device, cl_device_info name) {
  T value{};
  REQUIRE((clGetDeviceInfo(device, name, sizeof(value), &value, nullptr) == CL_SUCCESS));
  return value;
}

bool hasFeature(cl_device_id device, std::string_view feature) {
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_FEATURES, 0, nullptr, &size) == CL_SUCCESS));
  std::vector<cl_name_version> features(size / sizeof(cl_name_version));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_OPENCL_C_FEATURES, size, features.data(), nullptr) == CL_SUCCESS));
  return std::any_of(features.begin(), features.end(), [&](const auto &entry) { return entry.name == feature; });
}

struct Session {
  gemstone_test::RpcFixture server;
  gemstone_test::RpcEnvironment environment{server};
  cl_device_id device = gpu();
  cl_context context = nullptr;
  cl_command_queue queue = nullptr;
  Session() {
    cl_int error = CL_SUCCESS;
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
    REQUIRE((context && error == CL_SUCCESS));
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
    REQUIRE((queue && error == CL_SUCCESS));
  }
  ~Session() {
    clFinish(queue);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
  }
  cl_mem image(cl_mem_object_type type, cl_image_format format, size_t width, size_t height = 0, size_t depth = 0, size_t layers = 0,
               cl_mem_flags flags = CL_MEM_READ_WRITE, void *host = nullptr, size_t rowPitch = 0, cl_mem buffer = nullptr) const {
    cl_image_desc desc{};
    desc.image_type = type;
    desc.image_width = width;
    desc.image_height = height;
    desc.image_depth = depth;
    desc.image_array_size = layers;
    desc.image_row_pitch = rowPitch;
    desc.buffer = buffer;
    cl_int error = CL_SUCCESS;
    auto result = clCreateImage(context, flags, &format, &desc, host, &error);
    INFO("clCreateImage returned " << error);
    REQUIRE((result && error == CL_SUCCESS));
    return result;
  }
  cl_int create(cl_mem_object_type type, cl_image_format format, size_t width, size_t height, cl_mem_flags flags, void *host,
                size_t rowPitch = 0, cl_mem buffer = nullptr) const {
    cl_image_desc desc{};
    desc.image_type = type;
    desc.image_width = width;
    desc.image_height = height;
    desc.image_row_pitch = rowPitch;
    desc.buffer = buffer;
    cl_int error = CL_SUCCESS;
    auto result = clCreateImage(context, flags, &format, &desc, host, &error);
    if (result) clReleaseMemObject(result);
    return error;
  }
};

constexpr cl_image_format Rgba8{CL_RGBA, CL_UNORM_INT8};

std::vector<cl_image_format> supported(cl_context context, cl_mem_flags flags, cl_mem_object_type type) {
  cl_uint count = 0;
  REQUIRE((clGetSupportedImageFormats(context, flags, type, 0, nullptr, &count) == CL_SUCCESS));
  std::vector<cl_image_format> formats(count);
  REQUIRE((clGetSupportedImageFormats(context, flags, type, count, formats.data(), nullptr) == CL_SUCCESS));
  return formats;
}

bool contains(const std::vector<cl_image_format> &formats, cl_channel_order order, cl_channel_type type) {
  return std::any_of(formats.begin(), formats.end(),
                     [&](const auto &format) { return format.image_channel_order == order && format.image_channel_data_type == type; });
}
} // namespace

TEST_CASE("OpenCL advertises image support with the full-profile limits", "[opencl][images]") {
  const auto device = gpu();
  CHECK(info<cl_bool>(device, CL_DEVICE_IMAGE_SUPPORT) == CL_TRUE);
  CHECK(info<size_t>(device, CL_DEVICE_IMAGE2D_MAX_WIDTH) >= 16384);
  CHECK(info<size_t>(device, CL_DEVICE_IMAGE2D_MAX_HEIGHT) >= 16384);
  CHECK(info<size_t>(device, CL_DEVICE_IMAGE3D_MAX_WIDTH) >= 2048);
  CHECK(info<size_t>(device, CL_DEVICE_IMAGE3D_MAX_HEIGHT) >= 2048);
  CHECK(info<size_t>(device, CL_DEVICE_IMAGE3D_MAX_DEPTH) >= 2048);
  CHECK(info<size_t>(device, CL_DEVICE_IMAGE_MAX_BUFFER_SIZE) >= 65536);
  CHECK(info<size_t>(device, CL_DEVICE_IMAGE_MAX_ARRAY_SIZE) >= 2048);
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_READ_IMAGE_ARGS) >= 128);
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_WRITE_IMAGE_ARGS) >= 64);
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_READ_WRITE_IMAGE_ARGS) >= 64);
  CHECK(info<cl_uint>(device, CL_DEVICE_MAX_SAMPLERS) >= 16);
  CHECK(hasFeature(device, "__opencl_c_images"));
  CHECK(hasFeature(device, "__opencl_c_read_write_images"));
  CHECK(hasFeature(device, "__opencl_c_3d_image_writes"));
}

TEST_CASE("OpenCL lists the required image formats for every image type", "[opencl][images]") {
  Session session;
  constexpr cl_channel_type everyType[] = {CL_UNORM_INT8,     CL_UNORM_INT16,    CL_SNORM_INT8,   CL_SNORM_INT16,
                                           CL_SIGNED_INT8,    CL_SIGNED_INT16,   CL_SIGNED_INT32, CL_UNSIGNED_INT8,
                                           CL_UNSIGNED_INT16, CL_UNSIGNED_INT32, CL_HALF_FLOAT,   CL_FLOAT};
  constexpr cl_channel_type readWriteTypes[] = {CL_UNORM_INT8,     CL_SIGNED_INT8,    CL_SIGNED_INT16, CL_SIGNED_INT32, CL_UNSIGNED_INT8,
                                                CL_UNSIGNED_INT16, CL_UNSIGNED_INT32, CL_HALF_FLOAT,   CL_FLOAT};
  for (const auto type : {CL_MEM_OBJECT_IMAGE1D, CL_MEM_OBJECT_IMAGE1D_BUFFER, CL_MEM_OBJECT_IMAGE1D_ARRAY, CL_MEM_OBJECT_IMAGE2D,
                          CL_MEM_OBJECT_IMAGE2D_ARRAY, CL_MEM_OBJECT_IMAGE3D}) {
    INFO("image type " << type);
    const auto readable = supported(session.context, CL_MEM_READ_ONLY, type);
    for (const auto order : {CL_R, CL_RG, CL_RGBA})
      for (const auto data : everyType)
        CHECK(contains(readable, order, data));
    CHECK(contains(readable, CL_BGRA, CL_UNORM_INT8));
    CHECK(contains(readable, CL_sRGBA, CL_UNORM_INT8));
    for (const auto flags : {CL_MEM_WRITE_ONLY, CL_MEM_READ_WRITE, CL_MEM_KERNEL_READ_AND_WRITE}) {
      const auto writable = supported(session.context, flags, type);
      for (const auto order : {CL_R, CL_RGBA})
        for (const auto data : readWriteTypes)
          CHECK(contains(writable, order, data));
    }
  }
}

TEST_CASE("OpenCL images report their shape and round-trip through every transfer", "[opencl][images]") {
  Session session;
  constexpr size_t width = 4, height = 3, hostPitch = 20;
  std::vector<uint8_t> host(hostPitch * height, 0xee);
  for (size_t y = 0; y < height; ++y)
    for (size_t x = 0; x < width * 4; ++x)
      host[y * hostPitch + x] = static_cast<uint8_t>(y * 16 + x);
  auto image =
      session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, width, height, 0, 0, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, host.data(), hostPitch);

  cl_image_format format{};
  size_t element = 0, rowPitch = 0, slicePitch = 1, reportedWidth = 0, reportedHeight = 0, depth = 1, layers = 1;
  cl_uint levels = 1, samples = 1;
  cl_mem buffer = reinterpret_cast<cl_mem>(1);
  REQUIRE((clGetImageInfo(image, CL_IMAGE_FORMAT, sizeof(format), &format, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_ELEMENT_SIZE, sizeof(element), &element, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_ROW_PITCH, sizeof(rowPitch), &rowPitch, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_SLICE_PITCH, sizeof(slicePitch), &slicePitch, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_WIDTH, sizeof(reportedWidth), &reportedWidth, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_HEIGHT, sizeof(reportedHeight), &reportedHeight, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_DEPTH, sizeof(depth), &depth, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_ARRAY_SIZE, sizeof(layers), &layers, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_BUFFER, sizeof(buffer), &buffer, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_NUM_MIP_LEVELS, sizeof(levels), &levels, nullptr) == CL_SUCCESS));
  REQUIRE((clGetImageInfo(image, CL_IMAGE_NUM_SAMPLES, sizeof(samples), &samples, nullptr) == CL_SUCCESS));
  CHECK(format.image_channel_order == CL_RGBA);
  CHECK(format.image_channel_data_type == CL_UNORM_INT8);
  CHECK(element == 4);
  CHECK(rowPitch == width * 4);
  CHECK(slicePitch == 0);
  CHECK(reportedWidth == width);
  CHECK(reportedHeight == height);
  CHECK(depth == 0);
  CHECK(layers == 0);
  CHECK(buffer == nullptr);
  CHECK(levels == 0);
  CHECK(samples == 0);
  cl_mem_object_type type = 0;
  size_t size = 0;
  REQUIRE((clGetMemObjectInfo(image, CL_MEM_TYPE, sizeof(type), &type, nullptr) == CL_SUCCESS));
  REQUIRE((clGetMemObjectInfo(image, CL_MEM_SIZE, sizeof(size), &size, nullptr) == CL_SUCCESS));
  CHECK(type == CL_MEM_OBJECT_IMAGE2D);
  CHECK(size >= width * height * 4);

  const size_t origin[] = {0, 0, 0}, region[] = {width, height, 1};
  std::vector<uint8_t> packed(width * height * 4);
  REQUIRE((clEnqueueReadImage(session.queue, image, CL_TRUE, origin, region, 0, 0, packed.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  for (size_t y = 0; y < height; ++y)
    CHECK(std::equal(packed.begin() + y * width * 4, packed.begin() + (y + 1) * width * 4, host.begin() + y * hostPitch));

  const size_t patchOrigin[] = {1, 1, 0}, patchRegion[] = {2, 1, 1};
  const std::array<uint8_t, 8> patch{1, 2, 3, 4, 5, 6, 7, 8};
  REQUIRE((clEnqueueWriteImage(session.queue, image, CL_TRUE, patchOrigin, patchRegion, 0, 0, patch.data(), 0, nullptr, nullptr)
           == CL_SUCCESS));
  std::array<uint8_t, 8> patched{};
  REQUIRE((clEnqueueReadImage(session.queue, image, CL_TRUE, patchOrigin, patchRegion, 0, 0, patched.data(), 0, nullptr, nullptr)
           == CL_SUCCESS));
  CHECK(patched == patch);

  auto copy = session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2);
  const size_t copyOrigin[] = {0, 1, 0}, copyRegion[] = {2, 1, 1};
  REQUIRE((clEnqueueCopyImage(session.queue, image, copy, patchOrigin, copyOrigin, copyRegion, 0, nullptr, nullptr) == CL_SUCCESS));
  std::array<uint8_t, 4> copied{};
  const size_t lastTexel[] = {1, 1, 0}, oneTexel[] = {1, 1, 1};
  REQUIRE((clEnqueueReadImage(session.queue, copy, CL_TRUE, lastTexel, oneTexel, 0, 0, copied.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(std::equal(copied.begin(), copied.end(), patch.begin() + 4));

  cl_int error = CL_SUCCESS;
  auto staging = clCreateBuffer(session.context, CL_MEM_READ_WRITE, 64, nullptr, &error);
  REQUIRE((staging && error == CL_SUCCESS));
  REQUIRE((clEnqueueCopyImageToBuffer(session.queue, image, staging, patchOrigin, patchRegion, 4, 0, nullptr, nullptr) == CL_SUCCESS));
  std::array<uint8_t, 8> staged{};
  REQUIRE((clEnqueueReadBuffer(session.queue, staging, CL_TRUE, 4, staged.size(), staged.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(staged == patch);
  const size_t corner[] = {0, 0, 0};
  REQUIRE((clEnqueueCopyBufferToImage(session.queue, staging, copy, 4, corner, patchRegion, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clEnqueueReadImage(session.queue, copy, CL_TRUE, corner, patchRegion, 0, 0, patched.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(patched == patch);

  const cl_float4 colour{{1.0f, 0.5f, 0.0f, 1.0f}};
  REQUIRE((clEnqueueFillImage(session.queue, copy, &colour, origin, oneTexel, 0, nullptr, nullptr) == CL_SUCCESS));
  std::array<uint8_t, 4> filled{};
  REQUIRE((clEnqueueReadImage(session.queue, copy, CL_TRUE, origin, oneTexel, 0, 0, filled.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(filled == std::array<uint8_t, 4>{255, 128, 0, 255});

  size_t mappedRow = 0, mappedSlice = 1;
  auto *mapped = static_cast<uint8_t *>(clEnqueueMapImage(session.queue, image, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, patchOrigin,
                                                          patchRegion, &mappedRow, &mappedSlice, 0, nullptr, nullptr, &error));
  REQUIRE((mapped && error == CL_SUCCESS));
  CHECK(mappedRow >= 8);
  CHECK(std::equal(patch.begin(), patch.end(), mapped));
  mapped[0] = 0x77;
  REQUIRE((clEnqueueUnmapMemObject(session.queue, image, mapped, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clEnqueueReadImage(session.queue, image, CL_TRUE, patchOrigin, patchRegion, 0, 0, patched.data(), 0, nullptr, nullptr)
           == CL_SUCCESS));
  CHECK(patched[0] == 0x77);

  clReleaseMemObject(staging);
  clReleaseMemObject(copy);
  clReleaseMemObject(image);
}

TEST_CASE("OpenCL image creation validates formats, sizes and host pointers", "[opencl][images]") {
  Session session;
  std::array<uint8_t, 64> host{};
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, {CL_RGB, CL_FLOAT}, 2, 2, CL_MEM_READ_WRITE, nullptr) == CL_IMAGE_FORMAT_NOT_SUPPORTED);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, {CL_RGBA, 0x1234}, 2, 2, CL_MEM_READ_WRITE, nullptr) == CL_INVALID_IMAGE_FORMAT_DESCRIPTOR);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, Rgba8, 0, 2, CL_MEM_READ_WRITE, nullptr) == CL_INVALID_IMAGE_SIZE);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2, CL_MEM_COPY_HOST_PTR, nullptr) == CL_INVALID_HOST_PTR);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2, CL_MEM_READ_WRITE, host.data()) == CL_INVALID_HOST_PTR);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2, CL_MEM_COPY_HOST_PTR, host.data(), 10) == CL_INVALID_IMAGE_DESCRIPTOR);
  cl_int error = CL_SUCCESS;
  auto small = clCreateBuffer(session.context, CL_MEM_READ_WRITE, 8, nullptr, &error);
  REQUIRE((small && error == CL_SUCCESS));
  CHECK(session.create(CL_MEM_OBJECT_IMAGE1D_BUFFER, Rgba8, 4, 0, CL_MEM_READ_WRITE, nullptr, 0, small) == CL_INVALID_IMAGE_SIZE);
  clReleaseMemObject(small);
}

TEST_CASE("OpenCL 1D buffer images share their buffer's storage", "[opencl][images]") {
  Session session;
  std::array<float, 4> values{1.0f, 2.0f, 3.0f, 4.0f};
  cl_int error = CL_SUCCESS;
  auto buffer = clCreateBuffer(session.context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, sizeof(values), values.data(), &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  auto image = session.image(CL_MEM_OBJECT_IMAGE1D_BUFFER, {CL_R, CL_FLOAT}, 4, 0, 0, 0, CL_MEM_READ_WRITE, nullptr, 0, buffer);
  clReleaseMemObject(
      session.image(CL_MEM_OBJECT_IMAGE1D_BUFFER, {CL_R, CL_FLOAT}, 4, 0, 0, 0, CL_MEM_READ_WRITE, nullptr, sizeof(values), buffer));
  cl_mem reported = nullptr;
  REQUIRE((clGetImageInfo(image, CL_IMAGE_BUFFER, sizeof(reported), &reported, nullptr) == CL_SUCCESS));
  CHECK(reported == buffer);
  std::array<float, 4> read{};
  const size_t origin[] = {0, 0, 0}, region[] = {4, 1, 1};
  REQUIRE((clEnqueueReadImage(session.queue, image, CL_TRUE, origin, region, 0, 0, read.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(read == values);
  const float replacement = 9.0f;
  const size_t second[] = {1, 0, 0}, one[] = {1, 1, 1};
  REQUIRE((clEnqueueWriteImage(session.queue, image, CL_TRUE, second, one, 0, 0, &replacement, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clEnqueueReadBuffer(session.queue, buffer, CL_TRUE, 0, sizeof(read), read.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(read == std::array<float, 4>{1.0f, 9.0f, 3.0f, 4.0f});
  clReleaseMemObject(image);
  clReleaseMemObject(buffer);
}

TEST_CASE("OpenCL samplers report their state and reject unnormalised repetition", "[opencl][images]") {
  Session session;
  const cl_sampler_properties properties[] = {
      CL_SAMPLER_NORMALIZED_COORDS, CL_TRUE, CL_SAMPLER_ADDRESSING_MODE, CL_ADDRESS_REPEAT, CL_SAMPLER_FILTER_MODE, CL_FILTER_LINEAR, 0};
  cl_int error = CL_SUCCESS;
  auto sampler = clCreateSamplerWithProperties(session.context, properties, &error);
  REQUIRE((sampler && error == CL_SUCCESS));
  cl_bool normalised = CL_FALSE;
  cl_addressing_mode addressing = 0;
  cl_filter_mode filter = 0;
  cl_context context = nullptr;
  cl_uint references = 0;
  REQUIRE((clGetSamplerInfo(sampler, CL_SAMPLER_NORMALIZED_COORDS, sizeof(normalised), &normalised, nullptr) == CL_SUCCESS));
  REQUIRE((clGetSamplerInfo(sampler, CL_SAMPLER_ADDRESSING_MODE, sizeof(addressing), &addressing, nullptr) == CL_SUCCESS));
  REQUIRE((clGetSamplerInfo(sampler, CL_SAMPLER_FILTER_MODE, sizeof(filter), &filter, nullptr) == CL_SUCCESS));
  REQUIRE((clGetSamplerInfo(sampler, CL_SAMPLER_CONTEXT, sizeof(context), &context, nullptr) == CL_SUCCESS));
  REQUIRE((clRetainSampler(sampler) == CL_SUCCESS));
  REQUIRE((clGetSamplerInfo(sampler, CL_SAMPLER_REFERENCE_COUNT, sizeof(references), &references, nullptr) == CL_SUCCESS));
  CHECK(normalised == CL_TRUE);
  CHECK(addressing == CL_ADDRESS_REPEAT);
  CHECK(filter == CL_FILTER_LINEAR);
  CHECK(context == session.context);
  CHECK(references == 2);
  size_t size = 0;
  REQUIRE((clGetSamplerInfo(sampler, CL_SAMPLER_PROPERTIES, 0, nullptr, &size) == CL_SUCCESS));
  CHECK(size == sizeof(properties));
  REQUIRE((clReleaseSampler(sampler) == CL_SUCCESS));
  REQUIRE((clReleaseSampler(sampler) == CL_SUCCESS));

  auto legacy = clCreateSampler(session.context, CL_FALSE, CL_ADDRESS_CLAMP, CL_FILTER_NEAREST, &error);
  REQUIRE((legacy && error == CL_SUCCESS));
  REQUIRE((clReleaseSampler(legacy) == CL_SUCCESS));
  CHECK(!clCreateSampler(session.context, CL_FALSE, CL_ADDRESS_REPEAT, CL_FILTER_NEAREST, &error));
  CHECK(error == CL_INVALID_VALUE);
}

TEST_CASE("OpenCL image and sampler kernel arguments pass bounded descriptor records", "[opencl][images]") {
  Session session;
  session.server.opaqueLaunches();
  session.server.programCatalogue({{"groups", "groups"}}, {});
  session.server.kernelMetadata(16, 2, {{8, 0, 1, 0}, {8, 8, 1, 0}});
  cl_int error = CL_SUCCESS;
  auto program = clCreateProgramWithIL(session.context, session.server.Spirv, sizeof(session.server.Spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &session.device, "", nullptr, nullptr) == CL_SUCCESS));
  auto kernel = clCreateKernel(program, "groups", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  auto image = session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, 3, 2);
  auto sampler = clCreateSampler(session.context, CL_FALSE, CL_ADDRESS_CLAMP_TO_EDGE, CL_FILTER_NEAREST, &error);
  REQUIRE((sampler && error == CL_SUCCESS));
  REQUIRE((clSetKernelArg(kernel, 0, sizeof(image), &image) == CL_SUCCESS));
  REQUIRE((clSetKernelArg(kernel, 1, sizeof(sampler), &sampler) == CL_SUCCESS));
  const size_t global = 1;
  REQUIRE((clEnqueueNDRangeKernel(session.queue, kernel, 1, nullptr, &global, nullptr, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clFinish(session.queue) == CL_SUCCESS));
  const auto launch = session.server.lastLaunch();
  REQUIRE(launch.arguments.size() == 2);
  const auto *record = std::get_if<gemstone::execution::ArgumentMemory>(&launch.arguments[0]);
  const auto *state = std::get_if<gemstone::execution::ArgumentMemory>(&launch.arguments[1]);
  REQUIRE(record);
  REQUIRE(state);
  namespace descriptor = gemstone::spirv::ImageDescriptor;
  CHECK(record->range.has_value());
  CHECK(record->range->value == descriptor::Layout::LevelTable + descriptor::Layout::LevelBytes + descriptor::Layout::PitchBytes);
  CHECK(state->range.has_value());
  CHECK(state->range->value == descriptor::SamplerWord::Count * 4);
  CHECK(std::any_of(launch.pointerRelocations.begin(), launch.pointerRelocations.end(), [&](const auto &relocation) {
    const auto *source = std::get_if<gemstone::execution::RelocationSourceMemory>(&relocation.source);
    return source && source->region.value == record->region.value
           && relocation.sourceOffset.value == static_cast<int64_t>(descriptor::Layout::Texels);
  }));
  clReleaseSampler(sampler);
  clReleaseMemObject(image);
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL C compiles image kernels", "[opencl][images]") {
  Session session;
  const char *source = R"CLC(
    #if !defined(__opencl_c_images) || !defined(__opencl_c_read_write_images) || !defined(__opencl_c_3d_image_writes)
    #error image features are not enabled
    #endif
    constant sampler_t nearest = CLK_NORMALIZED_COORDS_FALSE | CLK_ADDRESS_CLAMP_TO_EDGE | CLK_FILTER_NEAREST;
    kernel void images(read_only image2d_t src, write_only image3d_t volume, read_write image2d_array_t layers,
                       sampler_t sampler, global float4 *out) {
      int2 p = (int2)(get_global_id(0), 0);
      out[0] = read_imagef(src, nearest, p) + read_imagef(src, sampler, (float2)(0.5f, 0.5f)) + read_imagef(src, p);
      write_imagef(volume, (int4)(p, 0, 0), out[0]);
      write_imageui(layers, (int4)(p, 0, 0), read_imageui(layers, (int4)(p, 0, 0)));
    })CLC";
  cl_int error = CL_SUCCESS;
  auto program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  const auto build = clBuildProgram(program, 1, &session.device, "-cl-std=CL3.0", nullptr, nullptr);
  size_t size = 0;
  clGetProgramBuildInfo(program, session.device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
  std::string log(size, '\0');
  clGetProgramBuildInfo(program, session.device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr);
  INFO(log);
  CHECK(build == CL_SUCCESS);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL image and sampler arguments are checked against the kernel's declarations", "[opencl][images]") {
  Session session;
  session.server.programCatalogue({{"k", "k"}}, {});
  session.server.kernelMetadata(32, 4, {{8, 0, 1, 0}, {8, 8, 1, 0}, {8, 16, 1, 0}, {8, 24, 1, 0}});
  const char *source = R"CLC(
    kernel void k(read_only image2d_t a, write_only image2d_t b, read_write image2d_t c, sampler_t s) {
      write_imagef(b, (int2)(0, 0), read_imagef(a, s, (int2)(0, 0)) + read_imagef(c, (int2)(0, 0)));
    })CLC";
  cl_int error = CL_SUCCESS;
  auto program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &session.device, "-cl-std=CL3.0 -cl-kernel-arg-info", nullptr, nullptr) == CL_SUCCESS));
  auto kernel = clCreateKernel(program, "k", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  auto readable = session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2, 0, 0, CL_MEM_READ_ONLY);
  auto writable = session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2, 0, 0, CL_MEM_WRITE_ONLY);
  auto both = session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2, 0, 0, CL_MEM_READ_WRITE);
  auto buffer = clCreateBuffer(session.context, CL_MEM_READ_WRITE, 16, nullptr, &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  auto sampler = clCreateSampler(session.context, CL_FALSE, CL_ADDRESS_CLAMP_TO_EDGE, CL_FILTER_NEAREST, &error);
  REQUIRE((sampler && error == CL_SUCCESS));
  CHECK(clSetKernelArg(kernel, 3, sizeof(buffer), &buffer) == CL_INVALID_SAMPLER);
  CHECK(clSetKernelArg(kernel, 3, sizeof(sampler), &sampler) == CL_SUCCESS);
  CHECK(clSetKernelArg(kernel, 0, sizeof(writable), &writable) == CL_INVALID_ARG_VALUE);
  CHECK(clSetKernelArg(kernel, 0, sizeof(readable), &readable) == CL_SUCCESS);
  CHECK(clSetKernelArg(kernel, 1, sizeof(readable), &readable) == CL_INVALID_ARG_VALUE);
  CHECK(clSetKernelArg(kernel, 1, sizeof(writable), &writable) == CL_SUCCESS);
  CHECK(clSetKernelArg(kernel, 2, sizeof(readable), &readable) == CL_INVALID_ARG_VALUE);
  CHECK(clSetKernelArg(kernel, 2, sizeof(both), &both) == CL_SUCCESS);
  CHECK(clSetKernelArg(kernel, 0, sizeof(buffer), &buffer) == CL_INVALID_MEM_OBJECT);
  cl_kernel_arg_address_qualifier address = 0;
  REQUIRE((clGetKernelArgInfo(kernel, 0, CL_KERNEL_ARG_ADDRESS_QUALIFIER, sizeof(address), &address, nullptr) == CL_SUCCESS));
  CHECK(address == CL_KERNEL_ARG_ADDRESS_GLOBAL);
  clReleaseSampler(sampler);
  clReleaseMemObject(buffer);
  clReleaseMemObject(both);
  clReleaseMemObject(writable);
  clReleaseMemObject(readable);
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL image capabilities are consistent with their extensions and queries", "[opencl][images]") {
  Session session;
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  CHECK(extensions.find("cl_khr_3d_image_writes") != std::string::npos);
  CHECK(extensions.find("cl_khr_image2d_from_buffer") != std::string::npos);
  CHECK(info<cl_uint>(session.device, CL_DEVICE_IMAGE_PITCH_ALIGNMENT) != 0);
  CHECK(info<cl_uint>(session.device, CL_DEVICE_IMAGE_BASE_ADDRESS_ALIGNMENT) != 0);
}

TEST_CASE("OpenCL 2D images alias buffers through a row pitch and other 2D images through their texels", "[opencl][images]") {
  Session session;
  std::array<uint8_t, 32> bytes{};
  for (size_t index = 0; index < bytes.size(); ++index)
    bytes[index] = static_cast<uint8_t>(index);
  cl_int error = CL_SUCCESS;
  auto buffer = clCreateBuffer(session.context, CL_MEM_READ_WRITE | CL_MEM_COPY_HOST_PTR, bytes.size(), bytes.data(), &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  auto image = session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, 3, 2, 0, 0, CL_MEM_READ_WRITE, nullptr, 16, buffer);
  size_t pitch = 0;
  cl_mem associated = nullptr;
  REQUIRE((clGetImageInfo(image, CL_IMAGE_ROW_PITCH, sizeof(pitch), &pitch, nullptr) == CL_SUCCESS));
  REQUIRE((clGetMemObjectInfo(image, CL_MEM_ASSOCIATED_MEMOBJECT, sizeof(associated), &associated, nullptr) == CL_SUCCESS));
  CHECK(pitch == 16);
  CHECK(associated == buffer);

  std::array<uint8_t, 24> read{};
  const size_t origin[] = {0, 0, 0}, region[] = {3, 2, 1};
  REQUIRE((clEnqueueReadImage(session.queue, image, CL_TRUE, origin, region, 12, 0, read.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  for (size_t index = 0; index < read.size(); ++index) {
    INFO("byte " << index);
    CHECK(read[index] == index / 12 * 16 + index % 12);
  }
  const std::array<uint8_t, 4> texel{9, 9, 9, 9};
  const size_t last[] = {2, 1, 0}, one[] = {1, 1, 1};
  REQUIRE((clEnqueueWriteImage(session.queue, image, CL_TRUE, last, one, 0, 0, texel.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clEnqueueReadBuffer(session.queue, buffer, CL_TRUE, 0, bytes.size(), bytes.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(bytes[24] == 9);
  CHECK(bytes[27] == 9);
  CHECK(bytes[28] == 28);

  auto view = session.image(CL_MEM_OBJECT_IMAGE2D, {CL_sRGBA, CL_UNORM_INT8}, 3, 2, 0, 0, CL_MEM_READ_ONLY, nullptr, 0, image);
  REQUIRE((clGetMemObjectInfo(view, CL_MEM_ASSOCIATED_MEMOBJECT, sizeof(associated), &associated, nullptr) == CL_SUCCESS));
  CHECK(associated == image);
  std::array<uint8_t, 4> aliased{};
  REQUIRE((clEnqueueReadImage(session.queue, view, CL_TRUE, last, one, 0, 0, aliased.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(aliased == texel);

  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, Rgba8, 3, 2, CL_MEM_READ_WRITE, nullptr, 14, buffer) == CL_INVALID_IMAGE_FORMAT_DESCRIPTOR);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, Rgba8, 3, 3, CL_MEM_READ_WRITE, nullptr, 16, buffer) == CL_INVALID_IMAGE_SIZE);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, {CL_RGBA, CL_FLOAT}, 3, 2, CL_MEM_READ_WRITE, nullptr, 0, image)
        == CL_INVALID_IMAGE_FORMAT_DESCRIPTOR);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, {CL_sRGBA, CL_UNORM_INT8}, 2, 2, CL_MEM_READ_ONLY, nullptr, 0, image)
        == CL_INVALID_IMAGE_DESCRIPTOR);
  clReleaseMemObject(view);
  clReleaseMemObject(image);
  clReleaseMemObject(buffer);
}

TEST_CASE("OpenCL argument checks follow underscored qualifiers, default access and null values", "[opencl][images]") {
  Session session;
  session.server.programCatalogue({{"k", "k"}}, {});
  session.server.kernelMetadata(24, 3, {{8, 0, 1, 0}, {8, 8, 1, 0}, {8, 16, 1, 0}});
  const char *source = R"CLC(
    __kernel void k(__write_only image2d_t target, image2d_t source, sampler_t sampler) {
      write_imagef(target, (int2)(0, 0), read_imagef(source, sampler, (int2)(0, 0)));
    })CLC";
  cl_int error = CL_SUCCESS;
  auto program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &session.device, "-cl-std=CL3.0", nullptr, nullptr) == CL_SUCCESS));
  auto kernel = clCreateKernel(program, "k", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  auto readable = session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2, 0, 0, CL_MEM_READ_ONLY);
  auto writable = session.image(CL_MEM_OBJECT_IMAGE2D, Rgba8, 2, 2, 0, 0, CL_MEM_WRITE_ONLY);
  CHECK(clSetKernelArg(kernel, 0, sizeof(readable), &readable) == CL_INVALID_ARG_VALUE);
  CHECK(clSetKernelArg(kernel, 1, sizeof(writable), &writable) == CL_INVALID_ARG_VALUE);
  CHECK(clSetKernelArg(kernel, 1, sizeof(cl_mem), nullptr) == CL_INVALID_MEM_OBJECT);
  CHECK(clSetKernelArg(kernel, 2, sizeof(cl_sampler), nullptr) == CL_INVALID_SAMPLER);
  clReleaseMemObject(writable);
  clReleaseMemObject(readable);
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL argument checks only treat image types as images", "[opencl][images]") {
  Session session;
  session.server.programCatalogue({{"k", "k"}}, {});
  session.server.kernelMetadata(16, 2, {{8, 0, 1, 0}, {8, 8, 2, 0}});
  const char *source = R"CLC(
    typedef struct { int width; } image_data;
    __kernel void k(read_only image2d_t input, __global image_data *out) { out->width = get_image_width(input); })CLC";
  cl_int error = CL_SUCCESS;
  auto program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &session.device, "-cl-std=CL3.0 -cl-kernel-arg-info", nullptr, nullptr) == CL_SUCCESS));
  auto kernel = clCreateKernel(program, "k", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  auto buffer = clCreateBuffer(session.context, CL_MEM_READ_WRITE, 16, nullptr, &error);
  REQUIRE((buffer && error == CL_SUCCESS));
  CHECK(clSetKernelArg(kernel, 1, sizeof(buffer), &buffer) == CL_SUCCESS);
  cl_kernel_arg_address_qualifier address = 0;
  REQUIRE((clGetKernelArgInfo(kernel, 1, CL_KERNEL_ARG_ADDRESS_QUALIFIER, sizeof(address), &address, nullptr) == CL_SUCCESS));
  CHECK(address == CL_KERNEL_ARG_ADDRESS_GLOBAL);
  clReleaseMemObject(buffer);
  clReleaseKernel(kernel);
  clReleaseProgram(program);
}

TEST_CASE("OpenCL depth images hold UNORM_INT16 and FLOAT depths in 2D and 2D array images", "[opencl][images]") {
  Session session;
  size_t size = 0;
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, 0, nullptr, &size) == CL_SUCCESS));
  std::string extensions(size, '\0');
  REQUIRE((clGetDeviceInfo(session.device, CL_DEVICE_EXTENSIONS, size, extensions.data(), nullptr) == CL_SUCCESS));
  CHECK(extensions.find("cl_khr_depth_images") != std::string::npos);
  for (const auto type : {CL_MEM_OBJECT_IMAGE2D, CL_MEM_OBJECT_IMAGE2D_ARRAY})
    for (const auto flags : {CL_MEM_READ_ONLY, CL_MEM_WRITE_ONLY, CL_MEM_KERNEL_READ_AND_WRITE}) {
      const auto formats = supported(session.context, flags, type);
      INFO("type " << type << " flags " << flags);
      CHECK(contains(formats, CL_DEPTH, CL_UNORM_INT16));
      CHECK(contains(formats, CL_DEPTH, CL_FLOAT));
    }
  for (const auto type : {CL_MEM_OBJECT_IMAGE1D, CL_MEM_OBJECT_IMAGE3D}) {
    INFO("type " << type);
    CHECK_FALSE(contains(supported(session.context, CL_MEM_READ_ONLY, type), CL_DEPTH, CL_FLOAT));
  }
  CHECK(session.create(CL_MEM_OBJECT_IMAGE2D, {CL_DEPTH, CL_UNORM_INT8}, 2, 2, CL_MEM_READ_WRITE, nullptr)
        == CL_IMAGE_FORMAT_NOT_SUPPORTED);
  CHECK(session.create(CL_MEM_OBJECT_IMAGE1D, {CL_DEPTH, CL_FLOAT}, 2, 0, CL_MEM_READ_WRITE, nullptr) == CL_IMAGE_FORMAT_NOT_SUPPORTED);

  auto image = session.image(CL_MEM_OBJECT_IMAGE2D, {CL_DEPTH, CL_UNORM_INT16}, 3, 2);
  const std::array<uint16_t, 6> depths{0, 1, 0x8000, 0xfffe, 0xffff, 7};
  const size_t origin[] = {0, 0, 0}, region[] = {3, 2, 1};
  REQUIRE((clEnqueueWriteImage(session.queue, image, CL_TRUE, origin, region, 0, 0, depths.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  const float half = 0.5f;
  const size_t corner[] = {2, 1, 0}, one[] = {1, 1, 1};
  REQUIRE((clEnqueueFillImage(session.queue, image, &half, corner, one, 0, nullptr, nullptr) == CL_SUCCESS));
  std::array<uint16_t, 6> read{};
  REQUIRE((clEnqueueReadImage(session.queue, image, CL_TRUE, origin, region, 0, 0, read.data(), 0, nullptr, nullptr) == CL_SUCCESS));
  CHECK(read == std::array<uint16_t, 6>{0, 1, 0x8000, 0xfffe, 0xffff, 0x8000});
  size_t element = 0;
  REQUIRE((clGetImageInfo(image, CL_IMAGE_ELEMENT_SIZE, sizeof(element), &element, nullptr) == CL_SUCCESS));
  CHECK(element == 2);
  clReleaseMemObject(image);
}

TEST_CASE("OpenCL C compiles depth image kernels", "[opencl][images]") {
  Session session;
  const char *source = R"CLC(
    #ifndef cl_khr_depth_images
    #error depth images are not enabled
    #endif
    kernel void depths(read_only image2d_depth_t source, write_only image2d_array_depth_t target, global float *out) {
      float depth = read_imagef(source, (int2)(0, 0));
      write_imagef(target, (int4)(0, 0, 1, 0), depth);
      out[0] = depth + (float)get_image_channel_order(source);
    })CLC";
  cl_int error = CL_SUCCESS;
  auto program = clCreateProgramWithSource(session.context, 1, &source, nullptr, &error);
  REQUIRE((program && error == CL_SUCCESS));
  const auto build = clBuildProgram(program, 1, &session.device, "-cl-std=CL3.0", nullptr, nullptr);
  size_t size = 0;
  clGetProgramBuildInfo(program, session.device, CL_PROGRAM_BUILD_LOG, 0, nullptr, &size);
  std::string log(size, '\0');
  clGetProgramBuildInfo(program, session.device, CL_PROGRAM_BUILD_LOG, size, log.data(), nullptr);
  INFO(log);
  CHECK(build == CL_SUCCESS);
  clReleaseProgram(program);
}
