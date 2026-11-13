#include <algorithm>
#include <array>
#include <chrono>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <future>
#include <initializer_list>
#include <string>
#include <thread>
#include <tuple>
#include <vector>

#include <aspartame/vector.hpp>
#include <catch2/catch_test_macros.hpp>
#include <ze_api.h>

#include <gemstone/program_binary.hpp>
#include <gemstone/test_support.hpp>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include "../../rpc/test/rpc_fixture.hpp"

using namespace aspartame;

using ImportedPointerFunction = ze_result_t (*)(ze_driver_handle_t, void *, size_t);

namespace {
struct ContextHandles {
  ze_driver_handle_t driver;
  ze_device_handle_t device;
  ze_context_handle_t context;
};

ContextHandles createContext() {
  REQUIRE(zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS);
  uint32_t count = 1;
  ze_driver_handle_t driver = nullptr;
  REQUIRE(zeDriverGet(&count, &driver) == ZE_RESULT_SUCCESS);
  ze_device_handle_t device = nullptr;
  REQUIRE(zeDeviceGet(driver, &count, &device) == ZE_RESULT_SUCCESS);
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE(zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS);
  return {.driver = driver, .device = device, .context = context};
}

ze_command_list_handle_t createImmediateList(const ContextHandles &handles) {
  ze_command_queue_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  desc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  ze_command_list_handle_t list = nullptr;
  REQUIRE(zeCommandListCreateImmediate(handles.context, handles.device, &desc, &list) == ZE_RESULT_SUCCESS);
  return list;
}

ze_image_desc_t imageDescription(ze_image_type_t type, uint32_t width, uint32_t height, uint32_t depth, uint32_t arrayLevels) {
  ze_image_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_IMAGE_DESC;
  desc.flags = ZE_IMAGE_FLAG_KERNEL_WRITE;
  desc.type = type;
  desc.format = {.layout = ZE_IMAGE_FORMAT_LAYOUT_8_8_8_8,
                 .type = ZE_IMAGE_FORMAT_TYPE_UINT,
                 .x = ZE_IMAGE_FORMAT_SWIZZLE_R,
                 .y = ZE_IMAGE_FORMAT_SWIZZLE_G,
                 .z = ZE_IMAGE_FORMAT_SWIZZLE_B,
                 .w = ZE_IMAGE_FORMAT_SWIZZLE_A};
  desc.width = width;
  desc.height = height;
  desc.depth = depth;
  desc.arraylevels = arrayLevels;
  return desc;
}

std::vector<uint32_t> pixelRamp(size_t count) {
  std::vector<uint32_t> pixels(count);
  for (size_t index = 0; index < count; ++index)
    pixels[index] = static_cast<uint32_t>(index + 1);
  return pixels;
}
} // namespace

TEST_CASE("Level Zero devices advertise image and sampler limits", "[level-zero][image]") {
  auto [driver, device, context] = createContext();
  ze_device_image_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_IMAGE_PROPERTIES;
  REQUIRE(zeDeviceGetImageProperties(device, &properties) == ZE_RESULT_SUCCESS);
  CHECK(properties.maxImageDims1D > 0);
  CHECK(properties.maxImageDims2D > 0);
  CHECK(properties.maxImageDims3D > 0);
  CHECK(properties.maxImageArraySlices > 0);
  CHECK(properties.maxSamplers > 0);
  CHECK(properties.maxReadImageArgs > 0);
  CHECK(properties.maxWriteImageArgs > 0);

  ze_image_properties_t imageProperties{};
  imageProperties.stype = ZE_STRUCTURE_TYPE_IMAGE_PROPERTIES;
  const auto desc = imageDescription(ZE_IMAGE_TYPE_2D, 16, 16, 1, 0);
  REQUIRE(zeImageGetProperties(device, &desc, &imageProperties) == ZE_RESULT_SUCCESS);
  CHECK((imageProperties.samplerFilterFlags & ZE_IMAGE_SAMPLER_FILTER_FLAG_LINEAR) != 0);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero samplers are created and destroyed", "[level-zero][sampler]") {
  auto [driver, device, context] = createContext();
  ze_sampler_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_SAMPLER_DESC;
  desc.addressMode = ZE_SAMPLER_ADDRESS_MODE_NONE;
  desc.filterMode = ZE_SAMPLER_FILTER_MODE_LINEAR;
  desc.isNormalized = true;
  std::vector<ze_sampler_handle_t> samplers;
  for (int i = 0; i < 5; ++i) {
    ze_sampler_handle_t sampler = nullptr;
    REQUIRE(zeSamplerCreate(context, device, &desc, &sampler) == ZE_RESULT_SUCCESS);
    CHECK(sampler != nullptr);
    samplers.push_back(sampler);
  }
  CHECK(zeSamplerCreate(context, device, nullptr, samplers.data()) == ZE_RESULT_ERROR_INVALID_NULL_POINTER);
  desc.addressMode = static_cast<ze_sampler_address_mode_t>(0x7f);
  ze_sampler_handle_t rejected = nullptr;
  CHECK(zeSamplerCreate(context, device, &desc, &rejected) == ZE_RESULT_ERROR_INVALID_ENUMERATION);
  for (auto *const sampler : samplers)
    CHECK(zeSamplerDestroy(sampler) == ZE_RESULT_SUCCESS);
  CHECK(zeSamplerDestroy(nullptr) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero images copy to and from memory and between images", "[level-zero][image]") {
  const auto handles = createContext();
  auto [driver, device, context] = handles;
  auto *const list = createImmediateList(handles);
  constexpr uint32_t width = 20;
  constexpr uint32_t height = 20;
  const auto desc = imageDescription(ZE_IMAGE_TYPE_2D, width, height, 1, 0);
  ze_image_handle_t source = nullptr;
  ze_image_handle_t target = nullptr;
  REQUIRE(zeImageCreate(context, device, &desc, &source) == ZE_RESULT_SUCCESS);
  REQUIRE(zeImageCreate(context, device, &desc, &target) == ZE_RESULT_SUCCESS);

  const auto pixels = pixelRamp(static_cast<size_t>(width) * height);
  REQUIRE(zeCommandListAppendImageCopyFromMemory(list, source, pixels.data(), nullptr, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  std::vector<uint32_t> back(static_cast<size_t>(width) * height, 0);
  REQUIRE(zeCommandListAppendImageCopyToMemory(list, back.data(), source, nullptr, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(back == pixels);

  REQUIRE(zeCommandListAppendImageCopy(list, target, source, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  std::fill(back.begin(), back.end(), 0);
  REQUIRE(zeCommandListAppendImageCopyToMemory(list, back.data(), target, nullptr, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(back == pixels);

  const ze_image_region_t region{.originX = 2, .originY = 3, .originZ = 0, .width = 5, .height = 4, .depth = 1};
  constexpr size_t pitchPixels = 8;
  std::vector<uint32_t> padded(pitchPixels * region.height, 0);
  REQUIRE(
      zeCommandListAppendImageCopyToMemoryExt(list, padded.data(), source, &region, pitchPixels * sizeof(uint32_t), 0, nullptr, 0, nullptr)
      == ZE_RESULT_SUCCESS);
  for (uint32_t y = 0; y < region.height; ++y) {
    for (uint32_t x = 0; x < pitchPixels; ++x) {
      const auto expected = x < region.width ? pixels[((region.originY + y) * width) + region.originX + x] : 0U;
      CHECK(padded[(y * pitchPixels) + x] == expected);
    }
  }

  const ze_image_region_t destination{.originX = 10, .originY = 10, .originZ = 0, .width = 5, .height = 4, .depth = 1};
  REQUIRE(zeCommandListAppendImageCopyFromMemoryExt(list, target, padded.data(), &destination, pitchPixels * sizeof(uint32_t), 0, nullptr,
                                                    0, nullptr)
          == ZE_RESULT_SUCCESS);
  const ze_image_region_t sourceRegion{.originX = 0, .originY = 0, .originZ = 0, .width = 3, .height = 3, .depth = 1};
  const ze_image_region_t targetRegion{.originX = 1, .originY = 1, .originZ = 0, .width = 3, .height = 3, .depth = 1};
  REQUIRE(zeCommandListAppendImageCopyRegion(list, target, source, &targetRegion, &sourceRegion, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendImageCopyToMemory(list, back.data(), target, nullptr, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(back[(10 * width) + 10] == pixels[(3 * width) + 2]);
  CHECK(back[(13 * width) + 14] == pixels[(6 * width) + 6]);
  CHECK(back[(9 * width) + 9] == pixels[(9 * width) + 9]);
  CHECK(back[(1 * width) + 1] == pixels[0]);
  CHECK(back[(3 * width) + 3] == pixels[(2 * width) + 2]);

  const ze_image_region_t outside{.originX = 18, .originY = 0, .originZ = 0, .width = 5, .height = 1, .depth = 1};
  CHECK(zeCommandListAppendImageCopyToMemory(list, back.data(), source, &outside, nullptr, 0, nullptr) != ZE_RESULT_SUCCESS);

  REQUIRE(zeImageDestroy(source) == ZE_RESULT_SUCCESS);
  REQUIRE(zeImageDestroy(target) == ZE_RESULT_SUCCESS);
  CHECK(zeImageDestroy(nullptr) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero volume and array images keep their slices apart", "[level-zero][image]") {
  const auto handles = createContext();
  auto [driver, device, context] = handles;
  auto *const list = createImmediateList(handles);
  for (const auto type : {ZE_IMAGE_TYPE_3D, ZE_IMAGE_TYPE_2DARRAY}) {
    DYNAMIC_SECTION((type == ZE_IMAGE_TYPE_3D ? "3D" : "2D array")) {
      const auto desc = imageDescription(type, 4, 3, type == ZE_IMAGE_TYPE_3D ? 5 : 1, type == ZE_IMAGE_TYPE_3D ? 0 : 5);
      ze_image_handle_t image = nullptr;
      REQUIRE(zeImageCreate(context, device, &desc, &image) == ZE_RESULT_SUCCESS);
      const auto pixels = pixelRamp(size_t{4} * 3 * 5);
      REQUIRE(zeCommandListAppendImageCopyFromMemory(list, image, pixels.data(), nullptr, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
      const ze_image_region_t slice{.originX = 1, .originY = 1, .originZ = 3, .width = 2, .height = 2, .depth = 1};
      std::vector<uint32_t> out(4, 0);
      REQUIRE(zeCommandListAppendImageCopyToMemory(list, out.data(), image, &slice, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
      const auto base = (3 * 12) + (1 * 4) + 1;
      CHECK(out == std::vector<uint32_t>{pixels[base], pixels[base + 1], pixels[base + 4], pixels[base + 5]});
      REQUIRE(zeImageDestroy(image) == ZE_RESULT_SUCCESS);
    }
  }
  auto unsupported = imageDescription(ZE_IMAGE_TYPE_2D, 4, 4, 1, 0);
  unsupported.format.layout = ZE_IMAGE_FORMAT_LAYOUT_NV12;
  ze_image_handle_t image = nullptr;
  CHECK(zeImageCreate(context, device, &unsupported, &image) == ZE_RESULT_ERROR_UNSUPPORTED_IMAGE_FORMAT);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero rejected modules fail their creation with a build log", "[level-zero]") {
  using gemstone::execution::FailureKind;
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  auto [driver, device, context] = createContext();
  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  moduleDesc.inputSize = sizeof(gemstone_test::RpcFixture::Spirv);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(gemstone_test::RpcFixture::Spirv);
  for (const auto kind : {FailureKind::InvalidProgram, FailureKind::InvalidRequest, FailureKind::Unsupported}) {
    CAPTURE(static_cast<int>(kind));
    server.rejectNext(gemstone::execution::operation::LoadProgram, kind);
    ze_module_handle_t module = nullptr;
    ze_module_build_log_handle_t log = nullptr;
    CHECK(zeModuleCreate(context, device, &moduleDesc, &module, &log) == ZE_RESULT_ERROR_MODULE_BUILD_FAILURE);
    CHECK(module == nullptr);
    REQUIRE(log != nullptr);
    size_t size = 0;
    CHECK(zeModuleBuildLogGetString(log, &size, nullptr) == ZE_RESULT_SUCCESS);
    CHECK(size > 1);
    CHECK(zeModuleBuildLogDestroy(log) == ZE_RESULT_SUCCESS);
  }
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero kernel creation names the failure the service reported", "[level-zero]") {
  using gemstone::execution::FailureKind;
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  server.kernelMetadata(0, 0, {});
  auto [driver, device, context] = createContext();
  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  moduleDesc.inputSize = sizeof(gemstone_test::RpcFixture::Spirv);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(gemstone_test::RpcFixture::Spirv);
  ze_module_handle_t module = nullptr;
  REQUIRE(zeModuleCreate(context, device, &moduleDesc, &module, nullptr) == ZE_RESULT_SUCCESS);
  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "absent";
  const struct {
    FailureKind kind;
    ze_result_t expected;
  } cases[]{
      {.kind = FailureKind::NotFound, .expected = ZE_RESULT_ERROR_INVALID_KERNEL_NAME},
      {.kind = FailureKind::InvalidProgram, .expected = ZE_RESULT_ERROR_MODULE_BUILD_FAILURE},
  };
  for (const auto &[kind, expected] : cases) {
    CAPTURE(static_cast<int>(kind));
    server.rejectNext(gemstone::execution::operation::DescribeKernel, kind);
    ze_kernel_handle_t kernel = nullptr;
    CHECK(zeKernelCreate(module, &kernelDesc, &kernel) == expected);
    CHECK(kernel == nullptr);
  }
  REQUIRE(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero native modules accept the OpenCL program binary", "[level-zero]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  auto [driver, device, context] = createContext();
  const auto *spirv = reinterpret_cast<const uint8_t *>(gemstone_test::RpcFixture::Spirv);
  const std::vector<uint8_t> il(spirv, spirv + sizeof(gemstone_test::RpcFixture::Spirv));
  const auto binary = gemstone::encodeProgramBinary(/* CL_PROGRAM_BINARY_TYPE_EXECUTABLE */ 4, "kernel void k() {}", il);
  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_NATIVE;
  moduleDesc.inputSize = binary.size();
  moduleDesc.pInputModule = binary.data();
  ze_module_handle_t module = nullptr;
  REQUIRE(zeModuleCreate(context, device, &moduleDesc, &module, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(server.lastProgramImage() == il);
  REQUIRE(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero kernel group sizes honour device limits and required dimensions", "[level-zero]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  server.kernelMetadata(0, 0, {});
  auto [driver, device, context] = createContext();
  // Two entry points share one module; only "sized" has a required local size.
  const uint32_t image[] = {0x07230203,
                            0x00010000,
                            0,
                            7,
                            0,
                            (2U << 16) | 17U,
                            4,
                            (2U << 16) | 17U,
                            6,
                            (3U << 16) | 14U,
                            2,
                            2,
                            (5U << 16) | 15U,
                            6,
                            3,
                            0x69616c70,
                            0x0000006e,
                            (5U << 16) | 15U,
                            6,
                            5,
                            0x657a6973,
                            0x00000064,
                            (6U << 16) | 16U,
                            5,
                            17,
                            4,
                            2,
                            1,
                            (2U << 16) | 19U,
                            1,
                            (3U << 16) | 33U,
                            2,
                            1,
                            (5U << 16) | 54U,
                            1,
                            3,
                            0,
                            2,
                            (2U << 16) | 248U,
                            4,
                            (1U << 16) | 253U,
                            (1U << 16) | 56U,
                            (5U << 16) | 54U,
                            1,
                            5,
                            0,
                            2,
                            (2U << 16) | 248U,
                            6,
                            (1U << 16) | 253U,
                            (1U << 16) | 56U};
  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  moduleDesc.inputSize = sizeof(image);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(image);
  ze_module_handle_t module = nullptr;
  REQUIRE(zeModuleCreate(context, device, &moduleDesc, &module, nullptr) == ZE_RESULT_SUCCESS);
  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "plain";
  ze_kernel_handle_t plain = nullptr;
  REQUIRE(zeKernelCreate(module, &kernelDesc, &plain) == ZE_RESULT_SUCCESS);
  ze_device_compute_properties_t limits{};
  limits.stype = ZE_STRUCTURE_TYPE_DEVICE_COMPUTE_PROPERTIES;
  REQUIRE(zeDeviceGetComputeProperties(device, &limits) == ZE_RESULT_SUCCESS);
  for (const auto size : {std::array<uint32_t, 3>{0, 1, 1},
                          {1, 0, 1},
                          {1, 1, 0},
                          {limits.maxGroupSizeX + 1, 1, 1},
                          {1, limits.maxGroupSizeY + 1, 1},
                          {1, 1, limits.maxGroupSizeZ + 1},
                          {limits.maxTotalGroupSize, 2, 1},
                          {UINT32_MAX, UINT32_MAX, UINT32_MAX}}) {
    CHECK(zeKernelSetGroupSize(plain, size[0], size[1], size[2]) == ZE_RESULT_ERROR_INVALID_GROUP_SIZE_DIMENSION);
  }
  REQUIRE(zeKernelSetGroupSize(plain, limits.maxTotalGroupSize, 1, 1) == ZE_RESULT_SUCCESS);
  ze_kernel_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_KERNEL_PROPERTIES;
  REQUIRE(zeKernelGetProperties(plain, &properties) == ZE_RESULT_SUCCESS);
  CHECK(properties.requiredGroupSizeX == 0);
  CHECK(properties.requiredGroupSizeY == 0);
  CHECK(properties.requiredGroupSizeZ == 0);
  kernelDesc.pKernelName = "sized";
  ze_kernel_handle_t sized = nullptr;
  server.kernelWorkGroup({4, 2, 1});
  REQUIRE(zeKernelCreate(module, &kernelDesc, &sized) == ZE_RESULT_SUCCESS);
  REQUIRE(zeKernelGetProperties(sized, &properties) == ZE_RESULT_SUCCESS);
  CHECK(properties.requiredGroupSizeX == 4);
  CHECK(properties.requiredGroupSizeY == 2);
  CHECK(properties.requiredGroupSizeZ == 1);
  CHECK(zeKernelSetGroupSize(sized, 8, 1, 1) == ZE_RESULT_ERROR_INVALID_GROUP_SIZE_DIMENSION);
  REQUIRE(zeKernelSetGroupSize(sized, 4, 2, 1) == ZE_RESULT_SUCCESS);
  uint32_t x = 0;
  uint32_t y = 0;
  uint32_t z = 0;
  REQUIRE(zeKernelSuggestGroupSize(sized, 16, 8, 1, &x, &y, &z) == ZE_RESULT_SUCCESS);
  CHECK(x == 4);
  CHECK(y == 2);
  CHECK(z == 1);
  REQUIRE(zeKernelDestroy(sized) == ZE_RESULT_SUCCESS);
  REQUIRE(zeKernelDestroy(plain) == ZE_RESULT_SUCCESS);
  REQUIRE(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero completed memory hints release allocations before their event", "[level-zero]") {
  for (const bool advice : {false, true}) {
    DYNAMIC_SECTION((advice ? "memory advice" : "memory prefetch")) {
      auto [driver, device, context] = createContext();
      ze_command_queue_desc_t queueDesc{};
      queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
      queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
      ze_command_queue_handle_t queue = nullptr;
      REQUIRE(zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
      ze_command_list_desc_t listDesc{};
      listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
      ze_command_list_handle_t list = nullptr;
      REQUIRE(zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS);
      ze_event_pool_desc_t poolDesc{};
      poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
      poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
      poolDesc.count = 2;
      ze_event_pool_handle_t pool = nullptr;
      REQUIRE(zeEventPoolCreate(context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS);
      ze_event_desc_t eventDesc{};
      eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
      eventDesc.signal = ZE_EVENT_SCOPE_FLAG_HOST;
      eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
      ze_event_handle_t ready = nullptr;
      ze_event_handle_t release = nullptr;
      REQUIRE(zeEventCreate(pool, &eventDesc, &ready) == ZE_RESULT_SUCCESS);
      eventDesc.index = 1;
      REQUIRE(zeEventCreate(pool, &eventDesc, &release) == ZE_RESULT_SUCCESS);
      ze_host_mem_alloc_desc_t allocationDesc{};
      allocationDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
      void *memory = nullptr;
      REQUIRE(zeMemAllocHost(context, &allocationDesc, 64, 1, &memory) == ZE_RESULT_SUCCESS);
      const auto appended = advice ? zeCommandListAppendMemAdvise(list, device, memory, 64, ZE_MEMORY_ADVICE_SET_READ_MOSTLY)
                                   : zeCommandListAppendMemoryPrefetch(list, memory, 64);
      REQUIRE(appended == ZE_RESULT_SUCCESS);
      REQUIRE(zeMemFree(context, memory) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);
      REQUIRE(zeCommandListAppendSignalEvent(list, ready) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListAppendWaitOnEvents(list, 1, &release) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListClose(list) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS);
      const auto readyStatus = zeEventHostSynchronize(ready, 5'000'000'000ULL);
      const auto freeStatus = readyStatus == ZE_RESULT_SUCCESS ? zeMemFree(context, memory) : ZE_RESULT_NOT_READY;
      // Unblock the worker before checking results, so a red test cannot leave it waiting.
      REQUIRE(zeEventHostSignal(release) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueSynchronize(queue, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
      CHECK(readyStatus == ZE_RESULT_SUCCESS);
      CHECK(freeStatus == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
      if (freeStatus != ZE_RESULT_SUCCESS) REQUIRE(zeMemFree(context, memory) == ZE_RESULT_SUCCESS);
      REQUIRE(zeEventDestroy(ready) == ZE_RESULT_SUCCESS);
      REQUIRE(zeEventDestroy(release) == ZE_RESULT_SUCCESS);
      REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
      REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
    }
  }
}

TEST_CASE("Level Zero completed device memory hints release allocations before their fence", "[level-zero]") {
  auto [driver, device, context] = createContext();
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  REQUIRE(zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS);
  ze_device_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *memory = nullptr;
  REQUIRE(zeMemAllocDevice(context, &allocationDesc, 64, 64, device, &memory) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendMemoryPrefetch(list, memory, 64) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendMemAdvise(list, device, memory, 64, ZE_MEMORY_ADVICE_SET_READ_MOSTLY) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListClose(list) == ZE_RESULT_SUCCESS);
  ze_fence_desc_t fenceDesc{};
  fenceDesc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  ze_fence_handle_t fence = nullptr;
  REQUIRE(zeFenceCreate(queue, &fenceDesc, &fence) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueExecuteCommandLists(queue, 1, &list, fence) == ZE_RESULT_SUCCESS);
  REQUIRE(zeFenceHostSynchronize(fence, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(context, memory) == ZE_RESULT_SUCCESS);
  REQUIRE(zeFenceDestroy(fence) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero memory free policies defer the free past recorded work", "[level-zero]") {
  auto [driver, device, context] = createContext();

  uint32_t extensionCount = 0;
  REQUIRE(zeDriverGetExtensionProperties(driver, &extensionCount, nullptr) == ZE_RESULT_SUCCESS);
  std::vector<ze_driver_extension_properties_t> extensions(extensionCount);
  REQUIRE(zeDriverGetExtensionProperties(driver, &extensionCount, extensions.data()) == ZE_RESULT_SUCCESS);
  CHECK(std::any_of(extensions.begin(), extensions.end(),
                    [](const auto &e) { return std::string(e.name) == ZE_MEMORY_FREE_POLICIES_EXT_NAME; }));

  ze_driver_memory_free_ext_properties_t policies{};
  policies.stype = ZE_STRUCTURE_TYPE_DRIVER_MEMORY_FREE_EXT_PROPERTIES;
  ze_driver_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_DRIVER_PROPERTIES;
  properties.pNext = &policies;
  REQUIRE(zeDriverGetProperties(driver, &properties) == ZE_RESULT_SUCCESS);
  CHECK(policies.freePolicies == (ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_BLOCKING_FREE | ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_DEFER_FREE));

  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  REQUIRE(zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS);
  ze_device_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *memory = nullptr;
  REQUIRE(zeMemAllocDevice(context, &allocationDesc, 64, 64, device, &memory) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendMemoryPrefetch(list, memory, 64) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(context, memory) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);

  ze_memory_free_ext_desc_t freeDesc{};
  freeDesc.stype = ZE_STRUCTURE_TYPE_MEMORY_FREE_EXT_DESC;
  freeDesc.freePolicy = ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_DEFER_FREE;
  REQUIRE(zeMemFreeExt(context, &freeDesc, memory) == ZE_RESULT_SUCCESS);
  CHECK(zeMemFreeExt(context, &freeDesc, memory) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  ze_memory_allocation_properties_t memoryProperties{};
  memoryProperties.stype = ZE_STRUCTURE_TYPE_MEMORY_ALLOCATION_PROPERTIES;
  REQUIRE(zeMemGetAllocProperties(context, memory, &memoryProperties, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(memoryProperties.type == ZE_MEMORY_TYPE_UNKNOWN);

  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero facade smoke test", "[level-zero]") {
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));

  uint32_t driverCount = 0;
  REQUIRE((zeDriverGet(&driverCount, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((driverCount >= 1));
  ze_driver_handle_t driver = nullptr;
  driverCount = 1;
  REQUIRE((zeDriverGet(&driverCount, &driver) == ZE_RESULT_SUCCESS));

  // Intel's major.minor.build encoding at the build from which the SYCL runtime reports sub-group clocks
  ze_driver_properties_t driverProperties{};
  driverProperties.stype = ZE_STRUCTURE_TYPE_DRIVER_PROPERTIES;
  REQUIRE((zeDriverGetProperties(driver, &driverProperties) == ZE_RESULT_SUCCESS));
  CHECK((driverProperties.driverVersion >> 24) == 1);
  CHECK(((driverProperties.driverVersion >> 16) & 0xff) == 6);
  CHECK((driverProperties.driverVersion & 0xffff) == 34666);

  uint32_t extensionCount = 0;
  REQUIRE((zeDriverGetExtensionProperties(driver, &extensionCount, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((extensionCount >= 2));
  std::vector<ze_driver_extension_properties_t> extensions(extensionCount);
  REQUIRE((zeDriverGetExtensionProperties(driver, &extensionCount, extensions.data()) == ZE_RESULT_SUCCESS));
  REQUIRE((extensions ^ exists([](const auto &extension) { return std::strcmp(extension.name, ZE_GLOBAL_OFFSET_EXP_NAME) == 0; })));

  void *import = nullptr;
  REQUIRE((zeDriverGetExtensionFunctionAddress(driver, "zexDriverImportExternalPointer", reinterpret_cast<void **>(&import))
           == ZE_RESULT_SUCCESS));
  auto importPointer = reinterpret_cast<ImportedPointerFunction>(import);
  void *external = std::malloc(64);
  REQUIRE((external));
  REQUIRE((importPointer(driver, external, 64) == ZE_RESULT_SUCCESS));
  void *release = nullptr;
  REQUIRE((zeDriverGetExtensionFunctionAddress(driver, "zexDriverReleaseImportedPointer", reinterpret_cast<void **>(&release))
           == ZE_RESULT_SUCCESS));
  auto releasePointer = reinterpret_cast<ImportedPointerFunction>(release);
  REQUIRE((releasePointer(driver, external, 0) == ZE_RESULT_SUCCESS));
  std::free(external);

  uint32_t deviceCount = 0;
  REQUIRE((zeDeviceGet(driver, &deviceCount, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((deviceCount == 1));
  ze_device_handle_t device = nullptr;
  deviceCount = 1;
  REQUIRE((zeDeviceGet(driver, &deviceCount, &device) == ZE_RESULT_SUCCESS));

  ze_device_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
  REQUIRE((zeDeviceGetProperties(device, &properties) == ZE_RESULT_SUCCESS));
  REQUIRE((properties.type == ZE_DEVICE_TYPE_GPU));
  REQUIRE((std::strstr(properties.name, "Gemstone") != nullptr));
  REQUIRE((properties.timerResolution == 1));
  properties.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES_1_2;
  REQUIRE((zeDeviceGetProperties(device, &properties) == ZE_RESULT_SUCCESS));
  REQUIRE((properties.timerResolution == 1000000000ULL));

  ze_pci_ext_properties_t pciProperties{};
  pciProperties.stype = ZE_STRUCTURE_TYPE_PCI_EXT_PROPERTIES;
  REQUIRE((zeDevicePciGetPropertiesExt(device, &pciProperties) == ZE_RESULT_SUCCESS));
  REQUIRE((pciProperties.address.domain == 0));
  REQUIRE((pciProperties.address.bus == 0));
  REQUIRE((pciProperties.address.device == 1));
  REQUIRE((pciProperties.address.function == 0));

  ze_device_compute_properties_t computeProperties{};
  computeProperties.stype = ZE_STRUCTURE_TYPE_DEVICE_COMPUTE_PROPERTIES;
  REQUIRE((zeDeviceGetComputeProperties(device, &computeProperties) == ZE_RESULT_SUCCESS));
  REQUIRE((computeProperties.numSubGroupSizes == 3));
  REQUIRE((computeProperties.subGroupSizes[0] == 8));
  REQUIRE((computeProperties.subGroupSizes[1] == 16));
  REQUIRE((computeProperties.subGroupSizes[2] == 32));

  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS));
  ze_context_handle_t extendedContext = nullptr;
  REQUIRE((zeContextCreateEx(driver, &contextDesc, 0, nullptr, &extendedContext) == ZE_RESULT_SUCCESS));
  ze_event_pool_desc_t retainedPoolDesc{};
  retainedPoolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  retainedPoolDesc.count = 1;
  ze_event_pool_handle_t retainedPool = nullptr;
  REQUIRE((zeEventPoolCreate(extendedContext, &retainedPoolDesc, 0, nullptr, &retainedPool) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(extendedContext) == ZE_RESULT_SUCCESS));
  ze_event_desc_t staleEventDesc{};
  staleEventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  ze_event_handle_t staleEvent = nullptr;
  REQUIRE((zeEventCreate(retainedPool, &staleEventDesc, &staleEvent) == ZE_RESULT_SUCCESS));
  ze_context_handle_t replacementContext = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &replacementContext) == ZE_RESULT_SUCCESS));
  ze_command_list_desc_t staleListDesc{};
  staleListDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t staleList = nullptr;
  REQUIRE((zeCommandListCreate(replacementContext, device, &staleListDesc, &staleList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendSignalEvent(staleList, staleEvent) == ZE_RESULT_ERROR_INVALID_ARGUMENT));
  REQUIRE((zeCommandListDestroy(staleList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(replacementContext) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventDestroy(staleEvent) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventPoolDestroy(retainedPool) == ZE_RESULT_SUCCESS));
  ze_context_handle_t otherContext = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &otherContext) == ZE_RESULT_SUCCESS));

  ze_device_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  ze_host_mem_alloc_desc_t hostAllocationDesc{};
  hostAllocationDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  const size_t alignments[] = {0, 1, 16, 64, 4096, 8192};
  const ze_memory_type_t types[] = {ZE_MEMORY_TYPE_DEVICE, ZE_MEMORY_TYPE_HOST, ZE_MEMORY_TYPE_SHARED};
  for (const auto type : types) {
    for (const auto alignment : alignments) {
      void *slab = nullptr;
      const size_t slabSize = 65536;
      const auto status = [&] {
        if (type == ZE_MEMORY_TYPE_DEVICE) return zeMemAllocDevice(context, &allocationDesc, slabSize, alignment, device, &slab);
        if (type == ZE_MEMORY_TYPE_HOST) return zeMemAllocHost(context, &hostAllocationDesc, slabSize, alignment, &slab);
        return zeMemAllocShared(context, &allocationDesc, &hostAllocationDesc, slabSize, alignment, device, &slab);
      }();
      REQUIRE((status == ZE_RESULT_SUCCESS));
      ze_memory_allocation_properties_t allocationProperties{};
      allocationProperties.stype = ZE_STRUCTURE_TYPE_MEMORY_ALLOCATION_PROPERTIES;
      REQUIRE((zeMemGetAllocProperties(context, slab, &allocationProperties, nullptr) == ZE_RESULT_SUCCESS));
      void *rangeBase = nullptr;
      size_t rangeSize = 0;
      REQUIRE((zeMemGetAddressRange(context, static_cast<char *>(slab) + 1, &rangeBase, nullptr) == ZE_RESULT_SUCCESS));
      REQUIRE((rangeBase == slab));
      REQUIRE((zeMemGetAddressRange(context, slab, nullptr, &rangeSize) == ZE_RESULT_SUCCESS));
      REQUIRE((rangeSize == slabSize));
      const auto address = reinterpret_cast<uintptr_t>(slab);
      REQUIRE((allocationProperties.pageSize != 0));
      REQUIRE((address % allocationProperties.pageSize == 0));
      REQUIRE((alignment == 0 || address % alignment == 0));
      // UMF needs two aligned 32 KiB buckets to fit without extra slab padding.
      const auto firstBucket = (address + 63U) & ~uintptr_t{63U};
      REQUIRE((firstBucket + slabSize <= address + slabSize));
      REQUIRE((zeMemFree(context, slab) == ZE_RESULT_SUCCESS));
    }
  }
  void *source = nullptr;
  void *destination = nullptr;
  REQUIRE((zeMemAllocDevice(context, &allocationDesc, 64, 16, device, &source) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemAllocDevice(context, &allocationDesc, 64, 16, device, &destination) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(otherContext, source) == ZE_RESULT_ERROR_INVALID_ARGUMENT));
  ze_memory_allocation_properties_t foreignProperties{};
  foreignProperties.stype = ZE_STRUCTURE_TYPE_MEMORY_ALLOCATION_PROPERTIES;
  REQUIRE((zeMemGetAllocProperties(otherContext, source, &foreignProperties, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((foreignProperties.type == ZE_MEMORY_TYPE_DEVICE));
  int nonUsm = 0;
  ze_memory_allocation_properties_t ordinaryProperties{};
  ordinaryProperties.stype = ZE_STRUCTURE_TYPE_MEMORY_ALLOCATION_PROPERTIES;
  REQUIRE((zeMemGetAllocProperties(context, &nonUsm, &ordinaryProperties, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((ordinaryProperties.type == ZE_MEMORY_TYPE_UNKNOWN));
  std::memset(source, 0x5a, 64);

  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  queueDesc.priority = ZE_COMMAND_QUEUE_PRIORITY_NORMAL;
  ze_command_list_handle_t list = nullptr;
  REQUIRE((zeCommandListCreateImmediate(context, device, &queueDesc, &list) == ZE_RESULT_SUCCESS));
  void *foreignDestination = nullptr;
  REQUIRE((zeMemAllocDevice(otherContext, &allocationDesc, 64, 16, device, &foreignDestination) == ZE_RESULT_SUCCESS));
  // AppendMemoryCopyFromContext only requires the source context to be named and
  // both ranges to be accessible by the device, so a destination from another
  // context on the same driver is legal.
  REQUIRE(
      (zeCommandListAppendMemoryCopyFromContext(list, foreignDestination, context, source, 4, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(otherContext, foreignDestination) == ZE_RESULT_SUCCESS));
  ze_command_queue_handle_t foreignQueue = nullptr;
  ze_command_list_handle_t foreignList = nullptr;
  ze_command_list_desc_t isolationListDesc{};
  isolationListDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  REQUIRE((zeCommandQueueCreate(otherContext, device, &queueDesc, &foreignQueue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListCreate(otherContext, device, &isolationListDesc, &foreignList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(foreignList) == ZE_RESULT_SUCCESS));
  ze_command_list_handle_t wrongContextLists[] = {foreignList};
  ze_command_queue_handle_t ownerQueue = nullptr;
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &ownerQueue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueExecuteCommandLists(ownerQueue, 1, wrongContextLists, nullptr) == ZE_RESULT_SUCCESS));
  ze_fence_desc_t fenceDesc{};
  fenceDesc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  ze_fence_handle_t ownerFence = nullptr;
  REQUIRE((zeFenceCreate(ownerQueue, &fenceDesc, &ownerFence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(ownerQueue) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
  ze_fence_handle_t foreignFence = nullptr;
  REQUIRE((zeFenceCreate(foreignQueue, &fenceDesc, &foreignFence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueExecuteCommandLists(ownerQueue, 0, nullptr, foreignFence) == ZE_RESULT_ERROR_INVALID_ARGUMENT));
  REQUIRE((zeFenceDestroy(foreignFence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeFenceDestroy(ownerFence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListDestroy(foreignList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(foreignQueue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(ownerQueue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(ownerQueue) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE));
  REQUIRE((zeCommandListAppendMemoryPrefetch(list, source, 64) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendMemAdvise(list, device, source, 64, ZE_MEMORY_ADVICE_SET_READ_MOSTLY) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendMemoryCopy(list, destination, source, 64, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((std::memcmp(source, destination, 64) == 0));

  // Rectangular copies must preserve pitches, origins and guard bytes.
  auto *sourceBytes = static_cast<uint8_t *>(source);
  auto *destinationBytes = static_cast<uint8_t *>(destination);
  for (uint32_t i = 0; i < 64; ++i)
    sourceBytes[i] = static_cast<uint8_t>(i);
  std::memset(destination, 0xa5, 64);
  ze_copy_region_t sourceRegion{.originX = 1, .originY = 1, .originZ = 0, .width = 4, .height = 3, .depth = 0};
  ze_copy_region_t destinationRegion{.originX = 2, .originY = 0, .originZ = 0, .width = 4, .height = 3, .depth = 0};
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE | ZE_EVENT_POOL_FLAG_KERNEL_TIMESTAMP;
  poolDesc.count = 2;
  ze_event_pool_handle_t pool{};
  REQUIRE((zeEventPoolCreate(context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS));
  ze_event_desc_t eventDesc{};
  eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  eventDesc.signal = eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t ready{};
  ze_event_handle_t done{};
  REQUIRE((zeEventCreate(pool, &eventDesc, &ready) == ZE_RESULT_SUCCESS));
  eventDesc.index = 1;
  REQUIRE((zeEventCreate(pool, &eventDesc, &done) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventHostSignal(ready) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &destinationRegion, 10, 0, source, &sourceRegion, 8, 0, done, 1, &ready)
           == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventQueryStatus(done) == ZE_RESULT_SUCCESS));
  ze_kernel_timestamp_result_t timestamp{};
  REQUIRE((zeEventQueryKernelTimestamp(done, &timestamp) == ZE_RESULT_SUCCESS));
  REQUIRE((timestamp.global.kernelStart < timestamp.global.kernelEnd));
  REQUIRE((timestamp.context.kernelStart == timestamp.global.kernelStart));
  REQUIRE((timestamp.context.kernelEnd == timestamp.global.kernelEnd));
  REQUIRE((zeEventDestroy(done) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventDestroy(ready) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS));

  poolDesc.count = 1;
  ze_event_pool_handle_t recordedPool = nullptr;
  REQUIRE((zeEventPoolCreate(context, &poolDesc, 0, nullptr, &recordedPool) == ZE_RESULT_SUCCESS));
  ze_event_handle_t recordedEvent = nullptr;
  REQUIRE((zeEventCreate(recordedPool, &eventDesc, &recordedEvent) == ZE_RESULT_SUCCESS));
  ze_command_list_handle_t recordedList = nullptr;
  REQUIRE((zeCommandListCreate(context, device, &isolationListDesc, &recordedList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendSignalEvent(recordedList, recordedEvent) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventDestroy(recordedEvent) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventQueryStatus(recordedEvent) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE));
  REQUIRE((zeCommandListDestroy(recordedList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventPoolDestroy(recordedPool) == ZE_RESULT_SUCCESS));
  for (uint32_t i = 0; i < 64; ++i) {
    const bool copied = i >= 2 && i < 26 && (i - 2) % 10 < 4;
    REQUIRE((destinationBytes[i] == (copied ? sourceBytes[1 + ((1 + ((i - 2) / 10)) * 8) + ((i - 2) % 10)] : 0xa5)));
  }
  // A single slice may have a slice pitch smaller than the row pitch times
  // the destination's row origin; no second slice can overlap it.
  std::memset(destination, 0xa5, 64);
  ze_copy_region_t const oneSliceSource{.originX = 0, .originY = 0, .originZ = 0, .width = 4, .height = 3, .depth = 1};
  ze_copy_region_t const oneSliceDestination{.originX = 2, .originY = 1, .originZ = 0, .width = 4, .height = 3, .depth = 1};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &oneSliceDestination, 10, 30, source, &oneSliceSource, 8, 24, nullptr, 0,
                                               nullptr)
           == ZE_RESULT_SUCCESS));
  for (uint32_t i = 0; i < 64; ++i) {
    const bool copied = i >= 12 && i < 36 && (i - 12) % 10 < 4;
    REQUIRE((destinationBytes[i] == (copied ? sourceBytes[(((i - 12) / 10) * 8) + ((i - 12) % 10)] : 0xa5)));
  }
  std::memset(destination, 0xa5, 64);
  ze_copy_region_t const empty{.originX = 0, .originY = 0, .originZ = 0, .width = 0, .height = 0, .depth = 0};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &empty, 0, 0, source, &empty, 0, 0, nullptr, 0, nullptr)
           == ZE_RESULT_SUCCESS));
  for (uint32_t i = 0; i < 64; ++i)
    REQUIRE((destinationBytes[i] == 0xa5));
  ze_copy_region_t const invalidRegion{.originX = 0, .originY = 0, .originZ = 0, .width = 5, .height = 3, .depth = 0};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &invalidRegion, 10, 0, source, &sourceRegion, 8, 0, nullptr, 0, nullptr)
           == ZE_RESULT_ERROR_INVALID_SIZE));
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, source, &sourceRegion, 8, 0, source, &sourceRegion, 8, 0, nullptr, 0, nullptr)
           == ZE_RESULT_ERROR_OVERLAPPING_REGIONS));

  auto outOfBounds = destinationRegion;
  outOfBounds.originY = 10;
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &outOfBounds, 10, 0, source, &sourceRegion, 8, 0, nullptr, 0, nullptr)
           == ZE_RESULT_ERROR_INVALID_SIZE));
  ze_copy_region_t const overflow{.originX = 0, .originY = 0, .originZ = UINT32_MAX, .width = 4, .height = 1, .depth = UINT32_MAX};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &overflow, 8, UINT32_MAX, source, &overflow, 8, UINT32_MAX, nullptr, 0,
                                               nullptr)
           == ZE_RESULT_ERROR_INVALID_SIZE));
  ze_copy_region_t const left{.originX = 0, .originY = 0, .originZ = 0, .width = 2, .height = 2, .depth = 0};
  ze_copy_region_t const right{.originX = 4, .originY = 0, .originZ = 0, .width = 2, .height = 2, .depth = 0};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, source, &right, 8, 0, source, &left, 8, 0, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((sourceBytes[4] == 0 && sourceBytes[5] == 1 && sourceBytes[12] == 8 && sourceBytes[13] == 9));

  // Deferred copies retain allocation ownership and read data at execution, not recording time.
  ze_command_list_desc_t deferredDesc{};
  deferredDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t deferred{};
  ze_command_queue_handle_t queue{};
  REQUIRE((zeCommandListCreate(context, device, &deferredDesc, &deferred) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
  sourceRegion = {.originX = 0, .originY = 0, .originZ = 0, .width = 4, .height = 2, .depth = 2};
  destinationRegion = {.originX = 0, .originY = 0, .originZ = 0, .width = 4, .height = 2, .depth = 2};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(deferred, destination, &destinationRegion, 8, 24, source, &sourceRegion, 8, 32, nullptr, 0,
                                               nullptr)
           == ZE_RESULT_SUCCESS));
  sourceRegion.width = destinationRegion.width = 0;
  std::memset(source, 0x37, 64);
  std::memset(destination, 0xa5, 64);
  REQUIRE((zeMemFree(context, source) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
  REQUIRE((zeMemFree(context, destination) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
  REQUIRE((zeCommandListClose(deferred) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &deferred, nullptr) == ZE_RESULT_SUCCESS));
  for (uint32_t i = 0; i < 64; ++i) {
    const bool copied = (i < 12 || (i >= 24 && i < 36)) && i % 8 < 4;
    REQUIRE((destinationBytes[i] == (copied ? 0x37 : 0xa5)));
  }
  REQUIRE((zeCommandListDestroy(deferred) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
  // Linear operations must distinguish an invalid tracked span from an ordinary host pointer.
  ze_command_list_handle_t checked = nullptr;
  REQUIRE((zeCommandListCreate(context, device, &deferredDesc, &checked) == ZE_RESULT_SUCCESS));
  uint8_t hostBytes[80];
  std::memset(hostBytes, 0x3c, sizeof(hostBytes));
  const uint8_t pattern = 0x17;
  for (size_t const offset : {size_t{0}, size_t{63}, size_t{64}}) {
    const size_t bytes = 65 - offset;
    REQUIRE((zeCommandListAppendMemoryCopy(checked, static_cast<uint8_t *>(destination) + offset, hostBytes, bytes, nullptr, 0, nullptr)
             == ZE_RESULT_ERROR_INVALID_SIZE));
    REQUIRE((zeCommandListAppendMemoryCopy(checked, hostBytes, static_cast<uint8_t *>(source) + offset, bytes, nullptr, 0, nullptr)
             == ZE_RESULT_ERROR_INVALID_SIZE));
    REQUIRE((zeCommandListAppendMemoryFill(checked, static_cast<uint8_t *>(destination) + offset, &pattern, 1, bytes, nullptr, 0, nullptr)
             == ZE_RESULT_ERROR_INVALID_SIZE));
  }
  REQUIRE((zeCommandListAppendMemoryCopy(checked, destination, hostBytes, 64, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendMemoryFill(checked, static_cast<uint8_t *>(destination) + 1, &pattern, 1, 62, nullptr, 0, nullptr)
           == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(checked) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &checked, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((static_cast<uint8_t *>(destination)[0] == 0x3c && static_cast<uint8_t *>(destination)[63] == 0x3c));
  for (size_t i = 1; i < 63; ++i)
    REQUIRE((static_cast<uint8_t *>(destination)[i] == pattern));
  REQUIRE((zeCommandListDestroy(checked) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  ze_memory_free_ext_desc_t freeDesc{};
  freeDesc.stype = ZE_STRUCTURE_TYPE_MEMORY_FREE_EXT_DESC;
  freeDesc.freePolicy = ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_BLOCKING_FREE;
  REQUIRE((zeMemFreeExt(context, &freeDesc, destination) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(context, source) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(otherContext) == ZE_RESULT_SUCCESS));
}

TEST_CASE("Level Zero asynchronous queue submits before host dependency resolves", "[level-zero]") {
  auto [driver, device, context] = createContext();

  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  ze_command_list_handle_t fastList = nullptr;
  REQUIRE((zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListCreate(context, device, &listDesc, &fastList) == ZE_RESULT_SUCCESS));
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
  poolDesc.count = 2;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE((zeEventPoolCreate(context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS));
  ze_event_desc_t eventDesc{};
  eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  eventDesc.signal = eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t ready = nullptr;
  ze_event_handle_t release = nullptr;
  REQUIRE((zeEventCreate(pool, &eventDesc, &ready) == ZE_RESULT_SUCCESS));
  eventDesc.index = 1;
  REQUIRE((zeEventCreate(pool, &eventDesc, &release) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendSignalEvent(fastList, ready) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(fastList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendWaitOnEvents(list, 1, &release) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));
  ze_fence_desc_t fenceDesc{};
  fenceDesc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  ze_fence_handle_t fence = nullptr;
  ze_fence_handle_t pendingFence = nullptr;
  REQUIRE((zeFenceCreate(queue, &fenceDesc, &fence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeFenceCreate(queue, &fenceDesc, &pendingFence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &fastList, fence) == ZE_RESULT_SUCCESS));

  std::promise<ze_result_t> result;
  auto submitted = result.get_future();
  std::thread submitter([&] { result.set_value(zeCommandQueueExecuteCommandLists(queue, 1, &list, pendingFence)); });
  const auto readyStatus = zeEventHostSynchronize(ready, 5'000'000'000ULL);
  const bool returnedBeforeRelease = submitted.wait_for(std::chrono::seconds(5)) == std::future_status::ready;
  const auto fenceStatus = zeFenceHostSynchronize(fence, 5'000'000'000ULL);
  const auto pendingStatus = zeCommandQueueSynchronize(queue, 0);
  const auto pendingDestroy = zeFenceDestroy(pendingFence);
  const auto signalStatus = zeEventHostSignal(release);
  submitter.join();
  CHECK((readyStatus == ZE_RESULT_SUCCESS));
  CHECK(returnedBeforeRelease);
  CHECK((fenceStatus == ZE_RESULT_SUCCESS));
  CHECK((pendingStatus == ZE_RESULT_NOT_READY));
  CHECK((pendingDestroy == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
  REQUIRE((signalStatus == ZE_RESULT_SUCCESS));
  REQUIRE((submitted.get() == ZE_RESULT_SUCCESS));
  REQUIRE((zeFenceHostSynchronize(fence, 5'000'000'000ULL) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueSynchronize(queue, 5'000'000'000ULL) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListDestroy(fastList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeFenceDestroy(fence) == ZE_RESULT_SUCCESS));
  if (pendingDestroy != ZE_RESULT_SUCCESS) REQUIRE((zeFenceDestroy(pendingFence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventDestroy(ready) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventDestroy(release) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
}

TEST_CASE("Level Zero resetting completed lists makes their fence safe to destroy", "[level-zero]") {
  auto [driver, device, context] = createContext();
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  ze_command_list_handle_t emptyList = nullptr;
  REQUIRE(zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListCreate(context, device, &listDesc, &emptyList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListClose(emptyList) == ZE_RESULT_SUCCESS);
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
  poolDesc.count = 1;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE(zeEventPoolCreate(context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS);
  ze_event_desc_t eventDesc{};
  eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  eventDesc.signal = eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t completed = nullptr;
  REQUIRE(zeEventCreate(pool, &eventDesc, &completed) == ZE_RESULT_SUCCESS);
  ze_fence_desc_t fenceDesc{};
  fenceDesc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  // Empty trailing lists amplify submission-reference cleanup without adding
  // device work after the completion event. Resetting the first list must not
  // expose an unfinished fence solely because that cleanup is still running.
  std::vector<ze_command_list_handle_t> lists(8193, emptyList);
  lists.front() = list;
  size_t failedDestructions = 0;
  for (size_t iteration = 0; iteration < 25; ++iteration) {
    REQUIRE(zeCommandListAppendSignalEvent(list, completed) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListClose(list) == ZE_RESULT_SUCCESS);
    ze_fence_handle_t fence = nullptr;
    REQUIRE(zeFenceCreate(queue, &fenceDesc, &fence) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandQueueExecuteCommandLists(queue, static_cast<uint32_t>(lists.size()), lists.data(), fence) == ZE_RESULT_SUCCESS);
    REQUIRE(zeEventHostSynchronize(completed, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
    REQUIRE(zeFenceReset(fence) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListReset(list) == ZE_RESULT_SUCCESS);
    const auto destroyed = zeFenceDestroy(fence);
    REQUIRE(zeCommandQueueSynchronize(queue, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
    if (destroyed != ZE_RESULT_SUCCESS) {
      ++failedDestructions;
      REQUIRE(zeFenceDestroy(fence) == ZE_RESULT_SUCCESS);
    }
    REQUIRE(zeEventHostReset(completed) == ZE_RESULT_SUCCESS);
  }
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(emptyList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(completed) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
  CAPTURE(failedDestructions);
  CHECK(failedDestructions == 0);
}

TEST_CASE("Level Zero completed fences can be destroyed before queue synchronisation", "[level-zero]") {
  auto [driver, device, context] = createContext();
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  REQUIRE((zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));
  ze_fence_desc_t fenceDesc{};
  fenceDesc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  size_t failedDestructions = 0;
  // Polling exposes the interval between publishing fence completion and the
  // worker releasing its last internal reference, without a queue sync masking it.
  for (size_t iteration = 0; iteration < 1000; ++iteration) {
    ze_fence_handle_t fence = nullptr;
    REQUIRE((zeFenceCreate(queue, &fenceDesc, &fence) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, fence) == ZE_RESULT_SUCCESS));
    const auto deadline = std::chrono::steady_clock::now() + gemstone_test::scaled(std::chrono::seconds(5));
    auto completed = zeFenceQueryStatus(fence);
    while (completed == ZE_RESULT_NOT_READY && std::chrono::steady_clock::now() < deadline)
      completed = zeFenceQueryStatus(fence);
    CAPTURE(iteration, completed);
    REQUIRE((completed == ZE_RESULT_SUCCESS));
    // UR resets completed fences before returning them to its pool, including
    // fences it subsequently destroys during context teardown.
    if (iteration % 2 == 0) REQUIRE((zeFenceReset(fence) == ZE_RESULT_SUCCESS));
    const auto destroyed = zeFenceDestroy(fence);
    if (destroyed != ZE_RESULT_SUCCESS) {
      ++failedDestructions;
      // Drain the queue so a rejected destroy does not leak the fence.
      REQUIRE((zeCommandQueueSynchronize(queue, 1'000'000'000ULL) == ZE_RESULT_SUCCESS));
      REQUIRE((zeFenceDestroy(fence) == ZE_RESULT_SUCCESS));
    }
  }
  REQUIRE((zeCommandQueueSynchronize(queue, 1'000'000'000ULL) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
  CAPTURE(failedDestructions);
  CHECK((failedDestructions == 0));
}

TEST_CASE("Level Zero completed transfers release allocations before their event", "[level-zero]") {
  auto [driver, device, context] = createContext();
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
  poolDesc.count = 1;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE((zeEventPoolCreate(context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS));
  ze_event_desc_t eventDesc{};
  eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  eventDesc.signal = eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t event = nullptr;
  REQUIRE((zeEventCreate(pool, &eventDesc, &event) == ZE_RESULT_SUCCESS));
  ze_host_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  std::array<uint8_t, 64> source{};
  source.fill(0x5a);
  const ze_copy_region_t region{.originX = 0, .originY = 0, .originZ = 0, .width = 8, .height = 8, .depth = 0};
  for (const bool immediate : {false, true}) {
    for (unsigned operation = 0; operation < 3; ++operation) {
      size_t failedFrees = 0;
      for (size_t iteration = 0; iteration < 1000; ++iteration) {
        ze_command_list_handle_t list = nullptr;
        if (immediate) REQUIRE((zeCommandListCreateImmediate(context, device, &queueDesc, &list) == ZE_RESULT_SUCCESS));
        else REQUIRE((zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS));
        REQUIRE((zeEventHostReset(event) == ZE_RESULT_SUCCESS));
        void *memory = nullptr;
        REQUIRE((zeMemAllocHost(context, &allocationDesc, source.size(), 1, &memory) == ZE_RESULT_SUCCESS));
        if (operation == 0)
          REQUIRE((zeCommandListAppendMemoryCopy(list, memory, source.data(), source.size(), event, 0, nullptr) == ZE_RESULT_SUCCESS));
        else if (operation == 1)
          REQUIRE((zeCommandListAppendMemoryFill(list, memory, source.data(), 1, source.size(), event, 0, nullptr) == ZE_RESULT_SUCCESS));
        else
          REQUIRE((zeCommandListAppendMemoryCopyRegion(list, memory, &region, 8, 0, source.data(), &region, 8, 0, event, 0, nullptr)
                   == ZE_RESULT_SUCCESS));
        if (!immediate) {
          REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));
          REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
        }
        const auto deadline = std::chrono::steady_clock::now() + gemstone_test::scaled(std::chrono::seconds(5));
        auto completed = zeEventQueryStatus(event);
        while (completed == ZE_RESULT_NOT_READY && std::chrono::steady_clock::now() < deadline)
          completed = zeEventQueryStatus(event);
        const auto freed = completed == ZE_RESULT_SUCCESS ? zeMemFree(context, memory) : ZE_RESULT_NOT_READY;
        if (freed != ZE_RESULT_SUCCESS) ++failedFrees;
        // Destroy only after trying the free: synchronising the whole list first
        // would hide premature event publication. Cleanup also keeps reds safe.
        REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
        if (freed != ZE_RESULT_SUCCESS) REQUIRE((zeMemFree(context, memory) == ZE_RESULT_SUCCESS));
        CAPTURE(immediate, operation, iteration, completed);
        REQUIRE((completed == ZE_RESULT_SUCCESS));
      }
      CAPTURE(immediate, operation, failedFrees);
      CHECK((failedFrees == 0));
    }
  }
  REQUIRE((zeCommandQueueSynchronize(queue, 5'000'000'000ULL) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventDestroy(event) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
}

namespace {
ze_command_list_handle_t immediateListOn(const ContextHandles &handles, uint32_t ordinal, ze_command_queue_mode_t mode) {
  ze_command_queue_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  desc.ordinal = ordinal;
  desc.mode = mode;
  ze_command_list_handle_t list = nullptr;
  REQUIRE(zeCommandListCreateImmediate(handles.context, handles.device, &desc, &list) == ZE_RESULT_SUCCESS);
  return list;
}

ze_event_handle_t counterEvent(const ContextHandles &handles) {
  ze_event_counter_based_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_EVENT_COUNTER_BASED_DESC;
  desc.flags = ZE_EVENT_COUNTER_BASED_FLAG_IMMEDIATE | ZE_EVENT_COUNTER_BASED_FLAG_NON_IMMEDIATE | ZE_EVENT_COUNTER_BASED_FLAG_HOST_VISIBLE;
  desc.signal = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t event = nullptr;
  REQUIRE(zeEventCounterBasedCreate(handles.context, handles.device, &desc, &event) == ZE_RESULT_SUCCESS);
  return event;
}

ze_event_pool_handle_t hostPool(const ContextHandles &handles, uint32_t count) {
  ze_event_pool_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  desc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
  desc.count = count;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE(zeEventPoolCreate(handles.context, &desc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS);
  return pool;
}

ze_event_handle_t poolEvent(ze_event_pool_handle_t pool, uint32_t index) {
  ze_event_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  desc.index = index;
  desc.signal = ZE_EVENT_SCOPE_FLAG_HOST;
  desc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t event = nullptr;
  REQUIRE(zeEventCreate(pool, &desc, &event) == ZE_RESULT_SUCCESS);
  return event;
}

} // namespace

TEST_CASE("Level Zero counter-based events complete after each appended signal", "[level-zero][events]") {
  const auto handles = createContext();
  auto *const list = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  auto *const event = counterEvent(handles);
  ze_event_counter_based_flags_t flags = 0;
  REQUIRE(zeEventGetCounterBasedFlags(event, &flags) == ZE_RESULT_SUCCESS);
  CHECK((flags & ZE_EVENT_COUNTER_BASED_FLAG_IMMEDIATE) != 0);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_SUCCESS);
  CHECK(zeEventHostSignal(event) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  CHECK(zeEventHostReset(event) == ZE_RESULT_ERROR_INVALID_ARGUMENT);

  auto *const pool = hostPool(handles, 1);
  auto *gate = poolEvent(pool, 0);
  REQUIRE(zeCommandListAppendWaitOnEvents(list, 1, &gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendSignalEvent(list, event) == ZE_RESULT_SUCCESS);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_NOT_READY);
  CHECK(zeEventHostSynchronize(event, 1'000'000) == ZE_RESULT_NOT_READY);
  REQUIRE(zeEventHostSignal(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventHostSynchronize(event, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_SUCCESS);

  REQUIRE(zeEventHostReset(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendWaitOnEvents(list, 1, &gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendSignalEvent(list, event) == ZE_RESULT_SUCCESS);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_NOT_READY);
  REQUIRE(zeEventHostSignal(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListHostSynchronize(list, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_SUCCESS);

  uint64_t completion = 0;
  uint64_t address = 0;
  REQUIRE(zeEventCounterBasedGetDeviceAddress(event, &completion, &address) == ZE_RESULT_SUCCESS);
  CHECK(completion == 2);
  CHECK(address != 0);
  CHECK(zeEventCounterBasedGetDeviceAddress(gate, &completion, &address) == ZE_RESULT_ERROR_INVALID_ARGUMENT);

  REQUIRE(zeEventDestroy(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero counter-based events wait for every appended signal when lists complete out of order", "[level-zero][events]") {
  const auto handles = createContext();
  auto *const slow = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  auto *const fast = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  auto *const event = counterEvent(handles);
  auto *const pool = hostPool(handles, 1);
  auto *gate = poolEvent(pool, 0);
  REQUIRE(zeCommandListAppendWaitOnEvents(slow, 1, &gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendSignalEvent(slow, event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendSignalEvent(fast, event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListHostSynchronize(fast, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_NOT_READY);
  CHECK(zeEventHostSynchronize(event, 1'000'000) == ZE_RESULT_NOT_READY);
  REQUIRE(zeEventHostSignal(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventHostSynchronize(event, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  CHECK(zeCommandListHostSynchronize(slow, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);

  REQUIRE(zeEventDestroy(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(fast) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(slow) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero counter-based waits observe the signals appended before them on regular lists", "[level-zero][events]") {
  const auto handles = createContext();
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(handles.context, handles.device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t producer = nullptr;
  ze_command_list_handle_t consumer = nullptr;
  REQUIRE(zeCommandListCreate(handles.context, handles.device, &listDesc, &producer) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListCreate(handles.context, handles.device, &listDesc, &consumer) == ZE_RESULT_SUCCESS);
  auto *counter = counterEvent(handles);
  REQUIRE(zeCommandListAppendSignalEvent(producer, counter) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendWaitOnEvents(consumer, 1, &counter) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListClose(producer) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListClose(consumer) == ZE_RESULT_SUCCESS);
  for (int round = 0; round < 3; ++round) {
    REQUIRE(zeCommandQueueExecuteCommandLists(queue, 1, &producer, nullptr) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandQueueExecuteCommandLists(queue, 1, &consumer, nullptr) == ZE_RESULT_SUCCESS);
  }
  REQUIRE(zeCommandQueueSynchronize(queue, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  CHECK(zeEventQueryStatus(counter) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(counter) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(producer) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(consumer) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero event pools may be destroyed before their events", "[level-zero][events]") {
  const auto handles = createContext();
  auto *const pool = hostPool(handles, 1);
  auto *const event = poolEvent(pool, 0);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  CHECK(zeEventHostSignal(event) == ZE_RESULT_SUCCESS);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero rounds non-power-of-two allocation alignments up to the granularity", "[level-zero][memory]") {
  const auto handles = createContext();
  ze_device_mem_alloc_desc_t deviceDesc{};
  deviceDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  ze_host_mem_alloc_desc_t hostDesc{};
  hostDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  const std::array<std::tuple<size_t, uintptr_t, uintptr_t>, 3> cases{{{3, 4096, 65536}, {12, 4096, 65536}, {12288, 16384, 65536}}};
  for (const auto &[alignment, hostAlignment, deviceAlignment] : cases) {
    DYNAMIC_SECTION("alignment " << alignment) {
      void *host = nullptr;
      void *device = nullptr;
      void *shared = nullptr;
      REQUIRE(zeMemAllocHost(handles.context, &hostDesc, 64, alignment, &host) == ZE_RESULT_SUCCESS);
      REQUIRE(zeMemAllocDevice(handles.context, &deviceDesc, 64, alignment, handles.device, &device) == ZE_RESULT_SUCCESS);
      REQUIRE(zeMemAllocShared(handles.context, &deviceDesc, &hostDesc, 64, alignment, handles.device, &shared) == ZE_RESULT_SUCCESS);
      CHECK(reinterpret_cast<uintptr_t>(host) % hostAlignment == 0);
      CHECK(reinterpret_cast<uintptr_t>(device) % deviceAlignment == 0);
      CHECK(reinterpret_cast<uintptr_t>(shared) % deviceAlignment == 0);
      REQUIRE(zeMemFree(handles.context, host) == ZE_RESULT_SUCCESS);
      REQUIRE(zeMemFree(handles.context, device) == ZE_RESULT_SUCCESS);
      REQUIRE(zeMemFree(handles.context, shared) == ZE_RESULT_SUCCESS);
    }
  }
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero device allocations keep in-granule overruns off their neighbours", "[level-zero][memory]") {
  const auto handles = createContext();
  ze_device_mem_alloc_desc_t deviceDesc{};
  deviceDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  constexpr size_t requested = 64;
  constexpr size_t granule = size_t{64} * 1024U;
  void *first = nullptr;
  void *second = nullptr;
  REQUIRE(zeMemAllocDevice(handles.context, &deviceDesc, requested, 64, handles.device, &first) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemAllocDevice(handles.context, &deviceDesc, requested, 64, handles.device, &second) == ZE_RESULT_SUCCESS);
  void *base = nullptr;
  size_t range = 0;
  REQUIRE(zeMemGetAddressRange(handles.context, first, &base, &range) == ZE_RESULT_SUCCESS);
  CHECK(base == first);
  CHECK(range == requested);
  const std::vector<uint8_t> neighbour(requested, 0xa5);
  std::memcpy(second, neighbour.data(), requested);
  std::memset(static_cast<uint8_t *>(first) + requested, 0x5a, granule - requested);
  CHECK(std::memcmp(second, neighbour.data(), requested) == 0);
  REQUIRE(zeMemFree(handles.context, first) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(handles.context, second) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero memory range barriers, system barriers and timestamps", "[level-zero][barrier]") {
  const auto handles = createContext();
  auto *const list = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS);
  ze_device_mem_alloc_desc_t deviceDesc{};
  deviceDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *memory = nullptr;
  REQUIRE(zeMemAllocDevice(handles.context, &deviceDesc, 8192, 1, handles.device, &memory) == ZE_RESULT_SUCCESS);
  const size_t sizes[] = {4096, 4096};
  const void *ranges[] = {memory, static_cast<const uint8_t *>(memory) + 4096};
  CHECK(zeCommandListAppendMemoryRangesBarrier(list, 2, sizes, ranges, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(zeCommandListAppendMemoryRangesBarrier(list, 0, sizes, ranges, nullptr, 0, nullptr) == ZE_RESULT_ERROR_INVALID_NULL_POINTER);
  const size_t oversized[] = {1ULL << 30};
  CHECK(zeCommandListAppendMemoryRangesBarrier(list, 1, oversized, ranges, nullptr, 0, nullptr) == ZE_RESULT_ERROR_INVALID_SIZE);
  CHECK(zeContextSystemBarrier(handles.context, handles.device) == ZE_RESULT_SUCCESS);
  CHECK(zeContextSystemBarrier(nullptr, handles.device) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE);

  uint64_t before = 0;
  uint64_t host = 0;
  REQUIRE(zeDeviceGetGlobalTimestamps(handles.device, &host, &before) == ZE_RESULT_SUCCESS);
  uint64_t written = 0;
  REQUIRE(zeCommandListAppendWriteGlobalTimestamp(list, &written, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(written >= before);
  CHECK(zeCommandListAppendWriteGlobalTimestamp(list, nullptr, nullptr, 0, nullptr) == ZE_RESULT_ERROR_INVALID_NULL_POINTER);
  REQUIRE(zeMemFree(handles.context, memory) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero atomic access attributes follow the advertised capabilities", "[level-zero][memory]") {
  const auto handles = createContext();
  ze_device_mem_alloc_desc_t deviceDesc{};
  deviceDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  ze_host_mem_alloc_desc_t hostDesc{};
  hostDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  void *shared = nullptr;
  REQUIRE(zeMemAllocShared(handles.context, &deviceDesc, &hostDesc, 256, 1, handles.device, &shared) == ZE_RESULT_SUCCESS);
  ze_memory_atomic_attr_exp_flags_t attributes = 0;
  CHECK(zeMemSetAtomicAccessAttributeExp(handles.context, handles.device, shared, 256, ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_ATOMICS)
        == ZE_RESULT_SUCCESS);
  CHECK(zeMemGetAtomicAccessAttributeExp(handles.context, handles.device, shared, 256, &attributes) == ZE_RESULT_SUCCESS);
  CHECK(attributes == ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_ATOMICS);
  CHECK(zeMemSetAtomicAccessAttributeExp(handles.context, handles.device, shared, 256, ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_SYSTEM_ATOMICS)
        == ZE_RESULT_SUCCESS);
  CHECK(zeMemGetAtomicAccessAttributeExp(handles.context, handles.device, shared, 256, &attributes) == ZE_RESULT_SUCCESS);
  CHECK(attributes == ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_SYSTEM_ATOMICS);
  CHECK(zeMemSetAtomicAccessAttributeExp(handles.context, handles.device, shared, 256,
                                         ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_ATOMICS | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_HOST_ATOMICS)
        == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  int ordinary = 0;
  CHECK(zeMemSetAtomicAccessAttributeExp(handles.context, handles.device, &ordinary, sizeof(ordinary),
                                         ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_SYSTEM_ATOMICS)
        == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  REQUIRE(zeMemFree(handles.context, shared) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero reports float atomics and external memory handle types through its properties", "[level-zero][device]") {
  const auto handles = createContext();
  ze_float_atomic_ext_properties_t atomics{};
  atomics.stype = ZE_STRUCTURE_TYPE_FLOAT_ATOMIC_EXT_PROPERTIES;
  ze_device_module_properties_t module{};
  module.stype = ZE_STRUCTURE_TYPE_DEVICE_MODULE_PROPERTIES;
  module.pNext = &atomics;
  REQUIRE(zeDeviceGetModuleProperties(handles.device, &module) == ZE_RESULT_SUCCESS);
  constexpr auto atomicOps = ZE_DEVICE_FP_ATOMIC_EXT_FLAG_GLOBAL_ADD | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_LOCAL_ADD;
  CHECK((atomics.fp32Flags & atomicOps) == atomicOps);
  CHECK((atomics.fp64Flags & atomicOps) == atomicOps);
  constexpr auto fp16Ops = atomicOps | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_GLOBAL_MIN_MAX | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_LOCAL_MIN_MAX
                           | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_GLOBAL_LOAD_STORE | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_LOCAL_LOAD_STORE;
  CHECK((atomics.fp16Flags & fp16Ops) == fp16Ops);

  ze_device_external_memory_properties_t external{};
  external.stype = ZE_STRUCTURE_TYPE_DEVICE_EXTERNAL_MEMORY_PROPERTIES;
  REQUIRE(zeDeviceGetExternalMemoryProperties(handles.device, &external) == ZE_RESULT_SUCCESS);
  CHECK(external.memoryAllocationImportTypes == 0);
  CHECK(external.memoryAllocationExportTypes == 0);
  CHECK(external.imageImportTypes == 0);
  CHECK(external.imageExportTypes == 0);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero appends to a closed regular list are not recorded", "[level-zero][list]") {
  const auto handles = createContext();
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(handles.context, handles.device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  REQUIRE(zeCommandListCreate(handles.context, handles.device, &listDesc, &list) == ZE_RESULT_SUCCESS);
  ze_host_mem_alloc_desc_t hostDesc{};
  hostDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  void *memory = nullptr;
  REQUIRE(zeMemAllocHost(handles.context, &hostDesc, 16, 1, &memory) == ZE_RESULT_SUCCESS);
  const uint8_t first = 1;
  const uint8_t second = 2;
  REQUIRE(zeCommandListAppendMemoryFill(list, memory, &first, 1, 16, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListClose(list) == ZE_RESULT_SUCCESS);
  CHECK(zeCommandListAppendMemoryFill(list, memory, &second, 1, 16, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueSynchronize(queue, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  CHECK(static_cast<uint8_t *>(memory)[0] == first);
  REQUIRE(zeMemFree(handles.context, memory) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero defers context teardown until its queues are destroyed", "[level-zero][queue]") {
  const auto handles = createContext();
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(handles.context, handles.device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
  CHECK(zeContextDestroy(handles.context) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE);
  REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero timed host synchronize waits for its timeout", "[level-zero][events]") {
  const auto handles = createContext();
  auto *const pool = hostPool(handles, 1);
  auto *const event = poolEvent(pool, 0);
  constexpr auto timeout = std::chrono::milliseconds(5);
  for (int attempt = 0; attempt < 5; ++attempt) {
    const auto start = std::chrono::steady_clock::now();
    REQUIRE(zeEventHostSynchronize(event, std::chrono::nanoseconds(timeout).count()) == ZE_RESULT_NOT_READY);
    const auto elapsed = std::chrono::steady_clock::now() - start;
    CHECK(elapsed >= timeout);
    CHECK(elapsed < timeout + std::chrono::milliseconds(500));
  }

  REQUIRE(zeEventDestroy(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero asynchronous immediate appends return before host-gated work runs", "[level-zero][list]") {
  const auto handles = createContext();
  auto *const list = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  ze_host_mem_alloc_desc_t hostDesc{};
  hostDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  void *memory = nullptr;
  REQUIRE(zeMemAllocHost(handles.context, &hostDesc, 16, 1, &memory) == ZE_RESULT_SUCCESS);
  std::memset(memory, 0, 16);

  auto *const pool = hostPool(handles, 1);
  auto *gate = poolEvent(pool, 0);
  const uint8_t value = 9;
  REQUIRE(zeCommandListAppendWaitOnEvents(list, 1, &gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListAppendMemoryFill(list, memory, &value, 1, 16, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  std::this_thread::sleep_for(std::chrono::milliseconds(50));
  CHECK(static_cast<uint8_t *>(memory)[15] == 0);
  CHECK(zeCommandListHostSynchronize(list, 1'000'000ULL) == ZE_RESULT_NOT_READY);
  REQUIRE(zeEventHostSignal(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListHostSynchronize(list, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  CHECK(static_cast<uint8_t *>(memory)[15] == value);

  REQUIRE(zeEventDestroy(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(handles.context, memory) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero asynchronous immediate lists on one engine execute dependency-free work in submission order", "[level-zero][list]") {
  const auto handles = createContext();
  auto *const first = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  auto *const second = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  constexpr size_t size = 32 << 20;
  ze_host_mem_alloc_desc_t hostDesc{};
  hostDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  void *memory = nullptr;
  REQUIRE(zeMemAllocHost(handles.context, &hostDesc, size, 1, &memory) == ZE_RESULT_SUCCESS);
  const uint8_t large = 7;
  const uint8_t small = 9;
  for (int round = 0; round < 10; ++round) {
    REQUIRE(zeCommandListAppendMemoryFill(first, memory, &large, 1, size, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendMemoryFill(second, memory, &small, 1, 16, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListHostSynchronize(second, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListHostSynchronize(first, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
    CHECK(static_cast<uint8_t *>(memory)[0] == small);
    CHECK(static_cast<uint8_t *>(memory)[size - 1] == large);
  }

  REQUIRE(zeMemFree(handles.context, memory) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(second) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(first) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

namespace {
std::vector<ze_command_queue_group_properties_t> queueGroups(const ContextHandles &handles) {
  uint32_t count = 0;
  REQUIRE(zeDeviceGetCommandQueueGroupProperties(handles.device, &count, nullptr) == ZE_RESULT_SUCCESS);
  std::vector<ze_command_queue_group_properties_t> groups(count);
  for (auto &group : groups)
    group.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_GROUP_PROPERTIES;
  REQUIRE(zeDeviceGetCommandQueueGroupProperties(handles.device, &count, groups.data()) == ZE_RESULT_SUCCESS);
  return groups;
}

ze_command_queue_handle_t queueOn(const ContextHandles &handles, uint32_t ordinal, uint32_t index, ze_command_queue_mode_t mode) {
  ze_command_queue_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  desc.ordinal = ordinal;
  desc.index = index;
  desc.mode = mode;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(handles.context, handles.device, &desc, &queue) == ZE_RESULT_SUCCESS);
  return queue;
}

ze_command_list_handle_t regularListOn(const ContextHandles &handles, uint32_t ordinal) {
  ze_command_list_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  desc.commandQueueGroupOrdinal = ordinal;
  ze_command_list_handle_t list = nullptr;
  REQUIRE(zeCommandListCreate(handles.context, handles.device, &desc, &list) == ZE_RESULT_SUCCESS);
  return list;
}

uint32_t copyOrdinal(const ContextHandles &handles) {
  const auto groups = queueGroups(handles);
  for (uint32_t ordinal = 0; ordinal < groups.size(); ++ordinal)
    if ((groups[ordinal].flags & ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COPY)
        && !(groups[ordinal].flags & ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COMPUTE))
      return ordinal;
  FAIL("no copy-only queue group");
  return 0;
}

ze_result_t execute(ze_command_queue_handle_t queue, ze_command_list_handle_t list) {
  return zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr);
}
} // namespace

TEST_CASE("Level Zero exposes a copy-only queue group beside the compute group", "[level-zero][queue-group]") {
  const auto handles = createContext();
  const auto groups = queueGroups(handles);
  REQUIRE(groups.size() == 2);
  CHECK((groups[0].flags & ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COMPUTE) != 0);
  CHECK((groups[0].flags & ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COPY) != 0);
  CHECK((groups[0].flags & ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COOPERATIVE_KERNELS) != 0);
  CHECK(groups[1].flags == ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COPY);
  for (const auto &group : groups) {
    CHECK(group.numQueues >= 1);
    CHECK(group.maxMemoryFillPatternSize > 0);
  }

  uint32_t count = 1;
  ze_command_queue_group_properties_t first{};
  first.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_GROUP_PROPERTIES;
  REQUIRE(zeDeviceGetCommandQueueGroupProperties(handles.device, &count, &first) == ZE_RESULT_SUCCESS);
  CHECK(count == 1);
  CHECK(first.flags == groups[0].flags);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero validates queue group ordinals and indices", "[level-zero][queue-group]") {
  const auto handles = createContext();
  const auto groups = queueGroups(handles);
  const auto outside = static_cast<uint32_t>(groups.size());

  for (uint32_t ordinal = 0; ordinal < groups.size(); ++ordinal) {
    DYNAMIC_SECTION("ordinal " << ordinal) {
      ze_command_queue_desc_t desc{};
      desc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
      desc.ordinal = ordinal;
      desc.index = groups[ordinal].numQueues;
      ze_command_queue_handle_t queue = nullptr;
      CHECK(zeCommandQueueCreate(handles.context, handles.device, &desc, &queue) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
      ze_command_list_handle_t list = nullptr;
      CHECK(zeCommandListCreateImmediate(handles.context, handles.device, &desc, &list) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
      desc.index = groups[ordinal].numQueues - 1;
      REQUIRE(zeCommandQueueCreate(handles.context, handles.device, &desc, &queue) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListCreateImmediate(handles.context, handles.device, &desc, &list) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
    }
  }

  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.ordinal = outside;
  ze_command_queue_handle_t queue = nullptr;
  CHECK(zeCommandQueueCreate(handles.context, handles.device, &queueDesc, &queue) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  ze_command_list_handle_t list = nullptr;
  CHECK(zeCommandListCreateImmediate(handles.context, handles.device, &queueDesc, &list) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  listDesc.commandQueueGroupOrdinal = outside;
  CHECK(zeCommandListCreate(handles.context, handles.device, &listDesc, &list) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  CHECK(list == nullptr);

  const auto copy = copyOrdinal(handles);
  auto *const computeQueue = queueOn(handles, 0, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  auto *const copyList = regularListOn(handles, copy);
  auto *const computeList = regularListOn(handles, 0);
  auto *const immediate = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS);
  REQUIRE(zeCommandListClose(copyList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListClose(computeList) == ZE_RESULT_SUCCESS);
  CHECK(execute(computeQueue, copyList) == ZE_RESULT_ERROR_INVALID_COMMAND_LIST_TYPE);
  CHECK(execute(computeQueue, immediate) == ZE_RESULT_ERROR_INVALID_COMMAND_LIST_TYPE);
  CHECK(execute(computeQueue, computeList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueSynchronize(computeQueue, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);

  REQUIRE(zeCommandListDestroy(immediate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(computeList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(copyList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(computeQueue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero queue groups hold independent engines", "[level-zero][queue-group]") {
  const auto handles = createContext();
  const auto groups = queueGroups(handles);
  for (uint32_t ordinal = 0; ordinal < groups.size(); ++ordinal) {
    DYNAMIC_SECTION("ordinal " << ordinal) {
      REQUIRE(groups[ordinal].numQueues >= 2);
      auto *const pool = hostPool(handles, 1);
      ze_event_handle_t event = poolEvent(pool, 0);
      auto *const waiting = regularListOn(handles, ordinal);
      auto *const signalling = regularListOn(handles, ordinal);
      REQUIRE(zeCommandListAppendWaitOnEvents(waiting, 1, &event) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListAppendSignalEvent(signalling, event) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListClose(waiting) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListClose(signalling) == ZE_RESULT_SUCCESS);
      auto *const first = queueOn(handles, ordinal, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
      auto *const second = queueOn(handles, ordinal, 1, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
      REQUIRE(execute(first, waiting) == ZE_RESULT_SUCCESS);
      REQUIRE(execute(second, signalling) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueSynchronize(second, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueSynchronize(first, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueDestroy(second) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueDestroy(first) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListDestroy(signalling) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandListDestroy(waiting) == ZE_RESULT_SUCCESS);
      REQUIRE(zeEventDestroy(event) == ZE_RESULT_SUCCESS);
      REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
    }
  }
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero copy lists accept copy commands and reject kernel launches", "[level-zero][queue-group]") {
  gemstone_test::RpcFixture const server;
  const gemstone_test::RpcEnvironment environment(server);
  const auto handles = createContext();
  const auto copy = copyOrdinal(handles);

  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  moduleDesc.inputSize = sizeof(gemstone_test::RpcFixture::Spirv);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(gemstone_test::RpcFixture::Spirv);
  ze_module_handle_t module = nullptr;
  REQUIRE(zeModuleCreate(handles.context, handles.device, &moduleDesc, &module, nullptr) == ZE_RESULT_SUCCESS);
  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "groups";
  ze_kernel_handle_t kernel = nullptr;
  REQUIRE(zeKernelCreate(module, &kernelDesc, &kernel) == ZE_RESULT_SUCCESS);

  ze_host_mem_alloc_desc_t hostDesc{};
  hostDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  void *source = nullptr;
  void *destination = nullptr;
  REQUIRE(zeMemAllocHost(handles.context, &hostDesc, 64, 1, &source) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemAllocHost(handles.context, &hostDesc, 64, 1, &destination) == ZE_RESULT_SUCCESS);
  const ze_group_count_t groups{.groupCountX = 1, .groupCountY = 1, .groupCountZ = 1};
  const uint8_t pattern = 0x11;
  auto *const pool = hostPool(handles, 1);
  auto *const event = poolEvent(pool, 0);

  for (const bool immediate : {false, true}) {
    CAPTURE(immediate);
    auto *const list = immediate ? immediateListOn(handles, copy, ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS) : regularListOn(handles, copy);
    CHECK(zeCommandListAppendLaunchKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_ERROR_UNSUPPORTED_FEATURE);
    CHECK(zeCommandListAppendLaunchCooperativeKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_ERROR_UNSUPPORTED_FEATURE);
    CHECK(zeCommandListAppendLaunchKernelIndirect(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_ERROR_UNSUPPORTED_FEATURE);

    std::memset(source, 0x5a, 64);
    std::memset(destination, 0, 64);
    CHECK(zeCommandListAppendMemoryCopy(list, destination, source, 64, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    CHECK(zeCommandListAppendMemoryFill(list, destination, &pattern, 1, 16, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    CHECK(zeCommandListAppendBarrier(list, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    CHECK(zeCommandListAppendSignalEvent(list, event) == ZE_RESULT_SUCCESS);
    CHECK(zeCommandListAppendWriteGlobalTimestamp(list, static_cast<uint64_t *>(source) + 4, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    if (immediate) {
      REQUIRE(zeCommandListHostSynchronize(list, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
    } else {
      auto *const queue = queueOn(handles, copy, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
      REQUIRE(zeCommandListClose(list) == ZE_RESULT_SUCCESS);
      REQUIRE(execute(queue, list) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueSynchronize(queue, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
      REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
    }
    CHECK(zeEventQueryStatus(event) == ZE_RESULT_SUCCESS);
    CHECK(static_cast<uint8_t *>(destination)[0] == 0x11);
    CHECK(static_cast<uint8_t *>(destination)[16] == 0x5a);
    REQUIRE(zeEventHostReset(event) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  }

  REQUIRE(zeEventDestroy(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(handles.context, destination) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(handles.context, source) == ZE_RESULT_SUCCESS);
  REQUIRE(zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS);
  REQUIRE(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero compute and copy engines pipeline through events", "[level-zero][queue-group][pipeline]") {
  const auto handles = createContext();
  const auto copy = copyOrdinal(handles);
  constexpr size_t size = 1 << 12;
  constexpr uint32_t iterations = 8;

  auto *const computeQueue = queueOn(handles, 0, 0, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  auto *const computeList = regularListOn(handles, 0);
  auto *const uploadList = immediateListOn(handles, copy, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);
  auto *const downloadList = immediateListOn(handles, copy, ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS);

  auto *const pool = hostPool(handles, 3);
  auto *uploadReady = poolEvent(pool, 0);
  auto *computeReady = poolEvent(pool, 1);
  auto *downloadReady = poolEvent(pool, 2);

  ze_device_mem_alloc_desc_t deviceDesc{};
  deviceDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  ze_host_mem_alloc_desc_t hostDesc{};
  hostDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  void *device = nullptr;
  void *host = nullptr;
  REQUIRE(zeMemAllocDevice(handles.context, &deviceDesc, size, 1, handles.device, &device) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemAllocHost(handles.context, &hostDesc, size, 1, &host) == ZE_RESULT_SUCCESS);
  std::memset(host, 0, size);

  for (uint32_t i = 0; i < iterations; ++i) {
    const uint8_t pattern = static_cast<uint8_t>(i + 1);
    REQUIRE(zeCommandListAppendMemoryCopy(uploadList, device, host, size, nullptr, 1, &uploadReady) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendBarrier(uploadList, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendEventReset(uploadList, uploadReady) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendSignalEvent(uploadList, computeReady) == ZE_RESULT_SUCCESS);

    REQUIRE(zeCommandListAppendWaitOnEvents(computeList, 1, &computeReady) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendMemoryFill(computeList, device, &pattern, 1, size, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendBarrier(computeList, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendEventReset(computeList, computeReady) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendSignalEvent(computeList, downloadReady) == ZE_RESULT_SUCCESS);

    REQUIRE(zeCommandListAppendMemoryCopy(downloadList, host, device, size, nullptr, 1, &downloadReady) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendBarrier(downloadList, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendEventReset(downloadList, downloadReady) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListAppendSignalEvent(downloadList, uploadReady) == ZE_RESULT_SUCCESS);
  }
  REQUIRE(zeCommandListClose(computeList) == ZE_RESULT_SUCCESS);
  REQUIRE(execute(computeQueue, computeList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventHostSignal(uploadReady) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueSynchronize(computeQueue, 10'000'000'000ULL) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListHostSynchronize(downloadList, 10'000'000'000ULL) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListHostSynchronize(uploadList, 10'000'000'000ULL) == ZE_RESULT_SUCCESS);

  const auto *bytes = static_cast<const uint8_t *>(host);
  CHECK(std::all_of(bytes, bytes + size, [](uint8_t value) { return value == iterations; }));

  REQUIRE(zeMemFree(handles.context, host) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(handles.context, device) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(downloadReady) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(computeReady) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(uploadReady) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(downloadList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(uploadList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(computeList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(computeQueue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero advertises the kernel timestamp query extension", "[level-zero][events]") {
  const auto handles = createContext();
  uint32_t count = 0;
  REQUIRE(zeDriverGetExtensionProperties(handles.driver, &count, nullptr) == ZE_RESULT_SUCCESS);
  std::vector<ze_driver_extension_properties_t> extensions(count);
  REQUIRE(zeDriverGetExtensionProperties(handles.driver, &count, extensions.data()) == ZE_RESULT_SUCCESS);
  CHECK(extensions ^ exists([](const ze_driver_extension_properties_t &extension) {
          return std::strcmp(extension.name, ZE_EVENT_QUERY_KERNEL_TIMESTAMPS_EXT_NAME) == 0
                 && extension.version == ZE_EVENT_QUERY_KERNEL_TIMESTAMPS_EXT_VERSION_CURRENT;
        }));
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero kernel timestamps are queried from completed events", "[level-zero][events]") {
  const auto handles = createContext();
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE | ZE_EVENT_POOL_FLAG_KERNEL_MAPPED_TIMESTAMP;
  poolDesc.count = 2;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE(zeEventPoolCreate(handles.context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS);
  auto *const signalled = poolEvent(pool, 0);
  auto *const pending = poolEvent(pool, 1);

  ze_kernel_timestamp_result_t timestamps{};
  CHECK(zeEventQueryKernelTimestamp(pending, &timestamps) == ZE_RESULT_NOT_READY);
  REQUIRE(zeEventHostSignal(signalled) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventQueryKernelTimestamp(signalled, &timestamps) == ZE_RESULT_SUCCESS);
  CHECK(timestamps.global.kernelEnd > timestamps.global.kernelStart);
  CHECK(timestamps.context.kernelStart == timestamps.global.kernelStart);

  uint32_t packets = 0;
  REQUIRE(zeEventQueryKernelTimestampsExt(signalled, handles.device, &packets, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(packets == 1);
  ze_kernel_timestamp_result_t extended{};
  ze_synchronized_timestamp_result_ext_t synchronised{};
  ze_event_query_kernel_timestamps_results_ext_properties_t properties{
      .stype = ZE_STRUCTURE_TYPE_EVENT_QUERY_KERNEL_TIMESTAMPS_RESULTS_EXT_PROPERTIES,
      .pNext = nullptr,
      .pKernelTimestampsBuffer = &extended,
      .pSynchronizedTimestampsBuffer = &synchronised};
  REQUIRE(zeEventQueryKernelTimestampsExt(signalled, handles.device, &packets, &properties) == ZE_RESULT_SUCCESS);
  CHECK(extended.global.kernelStart == timestamps.global.kernelStart);
  CHECK(extended.global.kernelEnd == timestamps.global.kernelEnd);
  CHECK(synchronised.global.kernelStart == timestamps.global.kernelStart);
  CHECK(synchronised.context.kernelEnd == timestamps.context.kernelEnd);
  CHECK(zeEventQueryKernelTimestampsExt(pending, handles.device, &packets, &properties) == ZE_RESULT_NOT_READY);

  REQUIRE(zeEventDestroy(pending) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(signalled) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero command lists append kernel timestamp queries at the requested offsets", "[level-zero][events]") {
  const auto handles = createContext();
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE | ZE_EVENT_POOL_FLAG_KERNEL_MAPPED_TIMESTAMP;
  poolDesc.count = 1;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE(zeEventPoolCreate(handles.context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS);
  auto *const event = poolEvent(pool, 0);
  REQUIRE(zeEventHostSignal(event) == ZE_RESULT_SUCCESS);
  ze_kernel_timestamp_result_t expected{};
  REQUIRE(zeEventQueryKernelTimestamp(event, &expected) == ZE_RESULT_SUCCESS);

  auto *const list = immediateListOn(handles, 0, ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS);
  std::array<ze_kernel_timestamp_result_t, 2> written{};
  ze_event_handle_t events[] = {event};
  const size_t offsets[] = {sizeof(ze_kernel_timestamp_result_t)};
  REQUIRE(zeCommandListAppendQueryKernelTimestamps(list, 1, events, written.data(), offsets, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(written[0].global.kernelEnd == 0);
  CHECK(written[1].global.kernelStart == expected.global.kernelStart);
  CHECK(written[1].global.kernelEnd == expected.global.kernelEnd);
  REQUIRE(zeCommandListAppendQueryKernelTimestamps(list, 1, events, written.data(), nullptr, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(written[0].context.kernelStart == expected.context.kernelStart);

  CHECK(zeCommandListAppendQueryKernelTimestamps(list, 1, nullptr, written.data(), nullptr, nullptr, 0, nullptr)
        == ZE_RESULT_ERROR_INVALID_NULL_POINTER);
  CHECK(zeCommandListAppendQueryKernelTimestamps(list, 1, events, nullptr, nullptr, nullptr, 0, nullptr)
        == ZE_RESULT_ERROR_INVALID_NULL_POINTER);
  CHECK(zeCommandListAppendQueryKernelTimestamps(nullptr, 1, events, written.data(), nullptr, nullptr, 0, nullptr)
        == ZE_RESULT_ERROR_INVALID_NULL_HANDLE);

  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero event pools round-trip IPC handles within a process", "[level-zero][events][ipc]") {
  const auto handles = createContext();
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE | ZE_EVENT_POOL_FLAG_IPC;
  poolDesc.count = 2;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE(zeEventPoolCreate(handles.context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS);

  ze_ipc_event_pool_handle_t ipc{};
  REQUIRE(zeEventPoolGetIpcHandle(pool, &ipc) == ZE_RESULT_SUCCESS);
  CHECK(zeEventPoolGetIpcHandle(pool, nullptr) == ZE_RESULT_ERROR_INVALID_NULL_POINTER);

  ze_event_pool_handle_t opened = nullptr;
  ze_ipc_event_pool_handle_t mismatched = ipc;
  mismatched.data[0] = static_cast<char>(mismatched.data[0] ^ 0x01);
  CHECK(zeEventPoolOpenIpcHandle(handles.context, mismatched, &opened) == ZE_RESULT_ERROR_UNSUPPORTED_FEATURE);
  REQUIRE(zeEventPoolOpenIpcHandle(handles.context, ipc, &opened) == ZE_RESULT_SUCCESS);
  CHECK(zeEventPoolCloseIpcHandle(pool) == ZE_RESULT_ERROR_INVALID_ARGUMENT);

  auto *const event = poolEvent(opened, 0);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_NOT_READY);
  REQUIRE(zeEventHostSignal(event) == ZE_RESULT_SUCCESS);
  CHECK(zeEventQueryStatus(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(event) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolCloseIpcHandle(opened) == ZE_RESULT_SUCCESS);

  ze_event_pool_desc_t plainDesc = poolDesc;
  plainDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
  ze_event_pool_handle_t plain = nullptr;
  REQUIRE(zeEventPoolCreate(handles.context, &plainDesc, 0, nullptr, &plain) == ZE_RESULT_SUCCESS);
  ze_ipc_event_pool_handle_t plainIpc{};
  CHECK(zeEventPoolGetIpcHandle(plain, &plainIpc) == ZE_RESULT_ERROR_INVALID_SYNCHRONIZATION_OBJECT);
  REQUIRE(zeEventPoolDestroy(plain) == ZE_RESULT_SUCCESS);

  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  CHECK(zeEventPoolOpenIpcHandle(handles.context, ipc, &opened) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero command queue priorities advertise a range and order gated work", "[level-zero][queue][priority]") {
  const auto handles = createContext();
  ze_device_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
  REQUIRE(zeDeviceGetProperties(handles.device, &properties) == ZE_RESULT_SUCCESS);
  CHECK(properties.maxCommandQueuePriority == ZE_COMMAND_QUEUE_PRIORITY_PRIORITY_HIGH);

  ze_command_queue_desc_t invalidDesc{};
  invalidDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  invalidDesc.priority = static_cast<ze_command_queue_priority_t>(3);
  ze_command_queue_handle_t rejectedQueue = nullptr;
  CHECK(zeCommandQueueCreate(handles.context, handles.device, &invalidDesc, &rejectedQueue) == ZE_RESULT_ERROR_INVALID_ENUMERATION);
  ze_command_list_handle_t rejectedList = nullptr;
  CHECK(zeCommandListCreateImmediate(handles.context, handles.device, &invalidDesc, &rejectedList) == ZE_RESULT_ERROR_INVALID_ENUMERATION);

  constexpr uint32_t lowCount = 5;
  constexpr size_t lowBytes = size_t{1} << 20;
  constexpr size_t highBytes = 1024;
  ze_device_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *buffer = nullptr;
  REQUIRE(zeMemAllocDevice(handles.context, &allocationDesc, lowBytes, 64, handles.device, &buffer) == ZE_RESULT_SUCCESS);

  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE | ZE_EVENT_POOL_FLAG_KERNEL_TIMESTAMP;
  poolDesc.count = lowCount + 2;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE(zeEventPoolCreate(handles.context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS);
  auto *gate = poolEvent(pool, 0);

  const auto record = [&](ze_command_queue_priority_t priority, size_t bytes, uint32_t index) {
    ze_command_queue_desc_t queueDesc{};
    queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
    queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
    queueDesc.priority = priority;
    ze_command_queue_handle_t queue = nullptr;
    REQUIRE(zeCommandQueueCreate(handles.context, handles.device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
    ze_command_list_desc_t listDesc{};
    listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
    ze_command_list_handle_t list = nullptr;
    REQUIRE(zeCommandListCreate(handles.context, handles.device, &listDesc, &list) == ZE_RESULT_SUCCESS);
    auto *const done = poolEvent(pool, index);
    const uint8_t pattern = 0x5a;
    REQUIRE(zeCommandListAppendMemoryFill(list, buffer, &pattern, 1, bytes, done, 1, &gate) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListClose(list) == ZE_RESULT_SUCCESS);
    return std::tuple(queue, list, done);
  };

  std::vector<std::tuple<ze_command_queue_handle_t, ze_command_list_handle_t, ze_event_handle_t>> lows;
  lows.reserve(lowCount);
  for (uint32_t i = 0; i < lowCount; ++i)
    lows.push_back(record(ZE_COMMAND_QUEUE_PRIORITY_PRIORITY_LOW, lowBytes, i + 1));
  const auto [highQueue, highList, highDone] = record(ZE_COMMAND_QUEUE_PRIORITY_PRIORITY_HIGH, highBytes, lowCount + 1);

  for (const auto &[queue, list, done] : lows)
    REQUIRE(execute(queue, list) == ZE_RESULT_SUCCESS);
  REQUIRE(execute(highQueue, highList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventHostSignal(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventHostSynchronize(highDone, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
  ze_kernel_timestamp_result_t highTimestamp{};
  REQUIRE(zeEventQueryKernelTimestamp(highDone, &highTimestamp) == ZE_RESULT_SUCCESS);

  uint32_t lowsAfterHigh = 0;
  for (const auto &[queue, list, done] : lows) {
    REQUIRE(zeEventHostSynchronize(done, 5'000'000'000ULL) == ZE_RESULT_SUCCESS);
    ze_kernel_timestamp_result_t timestamp{};
    REQUIRE(zeEventQueryKernelTimestamp(done, &timestamp) == ZE_RESULT_SUCCESS);
    lowsAfterHigh += timestamp.global.kernelEnd > highTimestamp.global.kernelEnd;
  }
  CHECK(lowsAfterHigh == lowCount);

  for (const auto &[queue, list, done] : lows) {
    REQUIRE(zeEventDestroy(done) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
    REQUIRE(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  }
  REQUIRE(zeEventDestroy(highDone) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandListDestroy(highList) == ZE_RESULT_SUCCESS);
  REQUIRE(zeCommandQueueDestroy(highQueue) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventDestroy(gate) == ZE_RESULT_SUCCESS);
  REQUIRE(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  REQUIRE(zeMemFree(handles.context, buffer) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero reserves address space beyond the server allocation limit", "[level-zero][memory][virtual]") {
  const auto handles = createContext();
  constexpr size_t size = size_t{1} << 40;
  void *reservation = nullptr;
  REQUIRE(zeVirtualMemReserve(handles.context, nullptr, size, &reservation) == ZE_RESULT_SUCCESS);
  REQUIRE(reservation != nullptr);
  REQUIRE(zeVirtualMemFree(handles.context, reservation, size) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(handles.context) == ZE_RESULT_SUCCESS);
}

namespace {
std::vector<uint8_t> spirvImage(uint8_t generator) {
  const auto *bytes = reinterpret_cast<const uint8_t *>(gemstone_test::RpcFixture::Spirv);
  std::vector<uint8_t> image(bytes, bytes + sizeof(gemstone_test::RpcFixture::Spirv));
  image[8] = generator;
  return image;
}

ze_module_handle_t createModule(ze_context_handle_t context, ze_device_handle_t device, const std::vector<uint8_t> &image) {
  ze_module_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  desc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  desc.inputSize = image.size();
  desc.pInputModule = image.data();
  ze_module_handle_t module = nullptr;
  REQUIRE(zeModuleCreate(context, device, &desc, &module, nullptr) == ZE_RESULT_SUCCESS);
  return module;
}
} // namespace

TEST_CASE("Level Zero program modules link their inputs into one program", "[level-zero][link]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  auto [driver, device, context] = createContext();
  const auto caller = spirvImage(1);
  const auto provider = spirvImage(2);
  const uint8_t *images[] = {caller.data(), provider.data()};
  const size_t sizes[] = {caller.size(), provider.size()};
  ze_module_program_exp_desc_t program{};
  program.stype = ZE_STRUCTURE_TYPE_MODULE_PROGRAM_EXP_DESC;
  program.count = 2;
  program.inputSizes = sizes;
  program.pInputModules = images;
  ze_module_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  desc.pNext = &program;
  desc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  ze_module_handle_t module = nullptr;
  REQUIRE(zeModuleCreate(context, device, &desc, &module, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(server.lastLinkInputs() == std::vector<std::vector<uint8_t>>{caller, provider});
  CHECK(server.programLoads == 1);
  auto linked = caller;
  linked.insert(linked.end(), provider.begin(), provider.end());
  CHECK(server.lastProgramImage() == linked);
  REQUIRE(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  CHECK(server.programUnloads == 1);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero dynamic linking moves the modules onto one shared program", "[level-zero][link]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  auto [driver, device, context] = createContext();
  const auto caller = spirvImage(1);
  const auto provider = spirvImage(2);
  ze_module_handle_t modules[] = {createModule(context, device, caller), createModule(context, device, provider)};
  CHECK(server.programLoads == 2);

  ze_module_build_log_handle_t log = nullptr;
  REQUIRE(zeModuleDynamicLink(2, modules, &log) == ZE_RESULT_SUCCESS);
  if (log) CHECK(zeModuleBuildLogDestroy(log) == ZE_RESULT_SUCCESS);
  CHECK(server.lastLinkInputs() == std::vector<std::vector<uint8_t>>{caller, provider});
  CHECK(server.programLoads == 3);
  CHECK(server.programUnloads == 2);

  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "add";
  for (auto *module : modules) {
    ze_kernel_handle_t kernel = nullptr;
    REQUIRE(zeKernelCreate(module, &kernelDesc, &kernel) == ZE_RESULT_SUCCESS);
    REQUIRE(zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS);
  }

  REQUIRE(zeModuleDestroy(modules[0]) == ZE_RESULT_SUCCESS);
  CHECK(server.programUnloads == 2);
  REQUIRE(zeModuleDestroy(modules[1]) == ZE_RESULT_SUCCESS);
  CHECK(server.programUnloads == 3);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero dynamic linking reports a rejected link and keeps the modules", "[level-zero][link]") {
  using gemstone::execution::FailureKind;
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  auto [driver, device, context] = createContext();
  ze_module_handle_t modules[] = {createModule(context, device, spirvImage(1)), createModule(context, device, spirvImage(2))};

  server.rejectNext(gemstone::execution::operation::LinkImages, FailureKind::InvalidRequest);
  ze_module_build_log_handle_t log = nullptr;
  CHECK(zeModuleDynamicLink(2, modules, &log) == ZE_RESULT_ERROR_MODULE_LINK_FAILURE);
  REQUIRE(log != nullptr);
  size_t size = 0;
  CHECK(zeModuleBuildLogGetString(log, &size, nullptr) == ZE_RESULT_SUCCESS);
  CHECK(size > 1);
  CHECK(zeModuleBuildLogDestroy(log) == ZE_RESULT_SUCCESS);
  CHECK(server.programLoads == 2);
  CHECK(server.programUnloads == 0);

  ze_module_handle_t withNull[] = {modules[0], nullptr};
  CHECK(zeModuleDynamicLink(2, withNull, nullptr) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE);
  CHECK(zeModuleDynamicLink(0, modules, nullptr) == ZE_RESULT_ERROR_INVALID_NULL_POINTER);

  for (auto *module : modules)
    REQUIRE(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero linked modules resolve a shared kernel name per module", "[level-zero][link]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  auto [driver, device, context] = createContext();
  server.linkTable({{.module = 0, .name = "k", .linkageName = "k"}, {.module = 1, .name = "k", .linkageName = "k.1"}});
  ze_module_handle_t modules[] = {createModule(context, device, spirvImage(1)), createModule(context, device, spirvImage(2))};
  ze_module_build_log_handle_t log = nullptr;
  REQUIRE(zeModuleDynamicLink(2, modules, &log) == ZE_RESULT_SUCCESS);
  if (log) CHECK(zeModuleBuildLogDestroy(log) == ZE_RESULT_SUCCESS);

  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "k";
  ze_kernel_handle_t kernels[] = {nullptr, nullptr};
  for (size_t index = 0; index < 2; ++index) {
    REQUIRE(zeKernelCreate(modules[index], &kernelDesc, &kernels[index]) == ZE_RESULT_SUCCESS);
    char name[16] = {};
    size_t size = sizeof(name);
    REQUIRE(zeKernelGetName(kernels[index], &size, name) == ZE_RESULT_SUCCESS);
    CHECK(std::string(name) == "k");
  }
  CHECK(kernels[0] != kernels[1]);
  void *pointer = nullptr;
  REQUIRE(zeModuleGetFunctionPointer(modules[1], "k", &pointer) == ZE_RESULT_SUCCESS);
  CHECK(server.describedKernelNames() == std::vector<std::string>{"k", "k.1", "k.1"});

  for (auto *kernel : kernels)
    REQUIRE(zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS);
  for (auto *module : modules)
    REQUIRE(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}

TEST_CASE("Level Zero dynamic linking names an unresolved user symbol in the build log", "[level-zero][link]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  auto [driver, device, context] = createContext();
  ze_module_handle_t modules[] = {createModule(context, device, spirvImage(1)), createModule(context, device, spirvImage(2))};

  server.rejectNextLink("linkage: unresolved symbol 'missing_fn'");
  ze_module_build_log_handle_t log = nullptr;
  CHECK(zeModuleDynamicLink(2, modules, &log) == ZE_RESULT_ERROR_MODULE_LINK_FAILURE);
  REQUIRE(log != nullptr);
  size_t size = 0;
  REQUIRE(zeModuleBuildLogGetString(log, &size, nullptr) == ZE_RESULT_SUCCESS);
  std::string text(size, '\0');
  REQUIRE(zeModuleBuildLogGetString(log, &size, text.data()) == ZE_RESULT_SUCCESS);
  CHECK(text.contains("unresolved symbol 'missing_fn'"));
  CHECK(zeModuleBuildLogDestroy(log) == ZE_RESULT_SUCCESS);
  CHECK(server.programLoads == 2);

  for (auto *module : modules)
    REQUIRE(zeModuleDestroy(module) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}
