#include <algorithm>
#include <array>
#include <cstdlib>
#include <cstring>
#include <future>
#include <initializer_list>
#include <thread>
#include <vector>

#include <stdint.h>

#include <catch2/catch_test_macros.hpp>
#include <ze_api.h>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

using ImportedPointerFunction = ze_result_t (*)(ze_driver_handle_t, void *, size_t);

namespace {
void checkSamplerCapability(bool immediate) {
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
  ze_command_list_handle_t list = nullptr;
  if (immediate) {
    ze_command_queue_desc_t desc{};
    desc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
    REQUIRE(zeCommandListCreateImmediate(context, device, &desc, &list) == ZE_RESULT_SUCCESS);
  } else {
    ze_command_list_desc_t desc{};
    desc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
    REQUIRE(zeCommandListCreate(context, device, &desc, &list) == ZE_RESULT_SUCCESS);
  }
  // Both CTS sampler cases only create their module when this image query succeeds.
  ze_device_image_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_IMAGE_PROPERTIES;
  REQUIRE(zeDeviceGetImageProperties(device, &properties) == ZE_RESULT_ERROR_UNSUPPORTED_FEATURE);
  ze_sampler_desc_t desc{};
  desc.stype = ZE_STRUCTURE_TYPE_SAMPLER_DESC;
  desc.addressMode = ZE_SAMPLER_ADDRESS_MODE_NONE;
  desc.filterMode = ZE_SAMPLER_FILTER_MODE_LINEAR;
  desc.isNormalized = true;
  for (int i = 0; i < 5; ++i) {
    ze_sampler_handle_t sampler = nullptr;
    CHECK(zeSamplerCreate(context, device, &desc, &sampler) == ZE_RESULT_ERROR_UNSUPPORTED_FEATURE);
    CHECK(sampler == nullptr);
    if (sampler) REQUIRE(zeSamplerDestroy(sampler) == ZE_RESULT_SUCCESS);
  }
  REQUIRE(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  REQUIRE(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
}
} // namespace

TEST_CASE("Level Zero regular sampler kernels require implemented image support", "[level-zero][sampler]") {
  checkSamplerCapability(false);
}

TEST_CASE("Level Zero immediate sampler kernels require implemented image support", "[level-zero][sampler]") {
  checkSamplerCapability(true);
}

TEST_CASE("Level Zero kernel group sizes honor device limits and required dimensions", "[level-zero]") {
  gemstone_test::RpcFixture server;
  const gemstone_test::RpcEnvironment environment(server);
  server.kernelMetadata(0, 0, {});
  REQUIRE(zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS);
  uint32_t count = 1;
  ze_driver_handle_t driver = nullptr;
  REQUIRE(zeDriverGet(&count, &driver) == ZE_RESULT_SUCCESS);
  ze_device_handle_t device = nullptr;
  count = 1;
  REQUIRE(zeDeviceGet(driver, &count, &device) == ZE_RESULT_SUCCESS);
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE(zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS);
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
  uint32_t x = 0, y = 0, z = 0;
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
      REQUIRE(zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS);
      uint32_t count = 1;
      ze_driver_handle_t driver = nullptr;
      REQUIRE(zeDriverGet(&count, &driver) == ZE_RESULT_SUCCESS);
      ze_device_handle_t device = nullptr;
      count = 1;
      REQUIRE(zeDeviceGet(driver, &count, &device) == ZE_RESULT_SUCCESS);
      ze_context_desc_t contextDesc{};
      contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
      ze_context_handle_t context = nullptr;
      REQUIRE(zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS);
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

TEST_CASE("Level Zero facade smoke test", "[level-zero]") {
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));

  uint32_t driverCount = 0;
  REQUIRE((zeDriverGet(&driverCount, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((driverCount >= 1));
  ze_driver_handle_t driver = nullptr;
  driverCount = 1;
  REQUIRE((zeDriverGet(&driverCount, &driver) == ZE_RESULT_SUCCESS));

  uint32_t extensionCount = 0;
  REQUIRE((zeDriverGetExtensionProperties(driver, &extensionCount, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((extensionCount >= 2));
  std::vector<ze_driver_extension_properties_t> extensions(extensionCount);
  REQUIRE((zeDriverGetExtensionProperties(driver, &extensionCount, extensions.data()) == ZE_RESULT_SUCCESS));
  REQUIRE((std::any_of(extensions.begin(), extensions.end(),
                       [](const auto &extension) { return std::strcmp(extension.name, ZE_GLOBAL_OFFSET_EXP_NAME) == 0; })));

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
  ze_context_handle_t extended_context = nullptr;
  REQUIRE((zeContextCreateEx(driver, &contextDesc, 0, nullptr, &extended_context) == ZE_RESULT_SUCCESS));
  ze_event_pool_desc_t retainedPoolDesc{};
  retainedPoolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  retainedPoolDesc.count = 1;
  ze_event_pool_handle_t retainedPool = nullptr;
  REQUIRE((zeEventPoolCreate(extended_context, &retainedPoolDesc, 0, nullptr, &retainedPool) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(extended_context) == ZE_RESULT_SUCCESS));
  ze_event_desc_t staleEventDesc{};
  staleEventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  ze_event_handle_t staleEvent = nullptr;
  REQUIRE((zeEventCreate(retainedPool, &staleEventDesc, &staleEvent) == ZE_RESULT_SUCCESS));
  ze_context_handle_t replacement_context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &replacement_context) == ZE_RESULT_SUCCESS));
  ze_command_list_desc_t staleListDesc{};
  staleListDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t staleList = nullptr;
  REQUIRE((zeCommandListCreate(replacement_context, device, &staleListDesc, &staleList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendSignalEvent(staleList, staleEvent) == ZE_RESULT_ERROR_INVALID_ARGUMENT));
  REQUIRE((zeCommandListDestroy(staleList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(replacement_context) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventDestroy(staleEvent) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventPoolDestroy(retainedPool) == ZE_RESULT_SUCCESS));
  ze_context_handle_t other_context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &other_context) == ZE_RESULT_SUCCESS));

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
      const auto status = type == ZE_MEMORY_TYPE_DEVICE ? zeMemAllocDevice(context, &allocationDesc, slabSize, alignment, device, &slab)
                          : type == ZE_MEMORY_TYPE_HOST
                              ? zeMemAllocHost(context, &hostAllocationDesc, slabSize, alignment, &slab)
                              : zeMemAllocShared(context, &allocationDesc, &hostAllocationDesc, slabSize, alignment, device, &slab);
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
  void *invalid = nullptr;
  REQUIRE((zeMemAllocDevice(context, &allocationDesc, 64, 3, device, &invalid) == ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT));
  REQUIRE((zeMemAllocHost(context, &hostAllocationDesc, 64, 3, &invalid) == ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT));
  REQUIRE(
      (zeMemAllocShared(context, &allocationDesc, &hostAllocationDesc, 64, 3, device, &invalid) == ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT));

  void *source = nullptr;
  void *destination = nullptr;
  REQUIRE((zeMemAllocDevice(context, &allocationDesc, 64, 16, device, &source) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemAllocDevice(context, &allocationDesc, 64, 16, device, &destination) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(other_context, source) == ZE_RESULT_ERROR_INVALID_ARGUMENT));
  ze_memory_allocation_properties_t foreignProperties{};
  foreignProperties.stype = ZE_STRUCTURE_TYPE_MEMORY_ALLOCATION_PROPERTIES;
  REQUIRE((zeMemGetAllocProperties(other_context, source, &foreignProperties, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((foreignProperties.type == ZE_MEMORY_TYPE_UNKNOWN));
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
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
  void *foreignDestination = nullptr;
  REQUIRE((zeMemAllocDevice(other_context, &allocationDesc, 64, 16, device, &foreignDestination) == ZE_RESULT_SUCCESS));
  // AppendMemoryCopyFromContext only requires the source context to be named and
  // both ranges to be accessible by the device, so a destination from another
  // context on the same driver is legal.
  REQUIRE(
      (zeCommandListAppendMemoryCopyFromContext(list, foreignDestination, context, source, 4, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(other_context, foreignDestination) == ZE_RESULT_SUCCESS));
  ze_command_queue_handle_t foreignQueue = nullptr;
  ze_command_list_handle_t foreignList = nullptr;
  ze_command_list_desc_t isolationListDesc{};
  isolationListDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  REQUIRE((zeCommandQueueCreate(other_context, device, &queueDesc, &foreignQueue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListCreate(other_context, device, &isolationListDesc, &foreignList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(foreignList) == ZE_RESULT_SUCCESS));
  ze_command_list_handle_t wrongContextLists[] = {foreignList};
  ze_command_queue_handle_t ownerQueue = nullptr;
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &ownerQueue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueExecuteCommandLists(ownerQueue, 1, wrongContextLists, nullptr) == ZE_RESULT_ERROR_INVALID_ARGUMENT));
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
  ze_copy_region_t sourceRegion{1, 1, 0, 4, 3, 0};
  ze_copy_region_t destinationRegion{2, 0, 0, 4, 3, 0};
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE | ZE_EVENT_POOL_FLAG_KERNEL_TIMESTAMP;
  poolDesc.count = 2;
  ze_event_pool_handle_t pool{};
  REQUIRE((zeEventPoolCreate(context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS));
  ze_event_desc_t eventDesc{};
  eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  eventDesc.signal = eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t ready{}, done{};
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
    REQUIRE((destinationBytes[i] == (copied ? sourceBytes[1 + (1 + (i - 2) / 10) * 8 + (i - 2) % 10] : 0xa5)));
  }
  // A single slice may have a slice pitch smaller than the row pitch times
  // the destination's row origin; no second slice can overlap it.
  std::memset(destination, 0xa5, 64);
  ze_copy_region_t oneSliceSource{0, 0, 0, 4, 3, 1};
  ze_copy_region_t oneSliceDestination{2, 1, 0, 4, 3, 1};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &oneSliceDestination, 10, 30, source, &oneSliceSource, 8, 24, nullptr, 0,
                                               nullptr)
           == ZE_RESULT_SUCCESS));
  for (uint32_t i = 0; i < 64; ++i) {
    const bool copied = i >= 12 && i < 36 && (i - 12) % 10 < 4;
    REQUIRE((destinationBytes[i] == (copied ? sourceBytes[((i - 12) / 10) * 8 + (i - 12) % 10] : 0xa5)));
  }
  ze_copy_region_t invalidRegion{0, 0, 0, 5, 3, 0};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &invalidRegion, 10, 0, source, &sourceRegion, 8, 0, nullptr, 0, nullptr)
           == ZE_RESULT_ERROR_INVALID_SIZE));
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, source, &sourceRegion, 8, 0, source, &sourceRegion, 8, 0, nullptr, 0, nullptr)
           == ZE_RESULT_ERROR_OVERLAPPING_REGIONS));

  auto outOfBounds = destinationRegion;
  outOfBounds.originY = 10;
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &outOfBounds, 10, 0, source, &sourceRegion, 8, 0, nullptr, 0, nullptr)
           == ZE_RESULT_ERROR_INVALID_SIZE));
  ze_copy_region_t overflow{0, 0, UINT32_MAX, 4, 1, UINT32_MAX};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, destination, &overflow, 8, UINT32_MAX, source, &overflow, 8, UINT32_MAX, nullptr, 0,
                                               nullptr)
           == ZE_RESULT_ERROR_INVALID_SIZE));
  ze_copy_region_t left{0, 0, 0, 2, 2, 0}, right{4, 0, 0, 2, 2, 0};
  REQUIRE((zeCommandListAppendMemoryCopyRegion(list, source, &right, 8, 0, source, &left, 8, 0, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((sourceBytes[4] == 0 && sourceBytes[5] == 1 && sourceBytes[12] == 8 && sourceBytes[13] == 9));

  // Deferred copies retain allocation ownership and read data at execution, not recording time.
  ze_command_list_desc_t deferredDesc{};
  deferredDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t deferred{};
  ze_command_queue_handle_t queue{};
  REQUIRE((zeCommandListCreate(context, device, &deferredDesc, &deferred) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
  sourceRegion = {0, 0, 0, 4, 2, 2};
  destinationRegion = {0, 0, 0, 4, 2, 2};
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
  for (size_t offset : {size_t(0), size_t(63), size_t(64)}) {
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
  REQUIRE((zeContextDestroy(other_context) == ZE_RESULT_SUCCESS));
}

TEST_CASE("Level Zero asynchronous queue submits before host dependency resolves", "[level-zero]") {
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));
  uint32_t count = 1;
  ze_driver_handle_t driver = nullptr;
  REQUIRE((zeDriverGet(&count, &driver) == ZE_RESULT_SUCCESS));
  ze_device_handle_t device = nullptr;
  REQUIRE((zeDeviceGet(driver, &count, &device) == ZE_RESULT_SUCCESS));
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS));

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
  ze_fence_handle_t fence = nullptr, pendingFence = nullptr;
  REQUIRE((zeFenceCreate(queue, &fenceDesc, &fence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeFenceCreate(queue, &fenceDesc, &pendingFence) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &fastList, fence) == ZE_RESULT_SUCCESS));

  std::promise<ze_result_t> result;
  auto submitted = result.get_future();
  std::thread submitter([&] { result.set_value(zeCommandQueueExecuteCommandLists(queue, 1, &list, pendingFence)); });
  const auto readyStatus = zeEventHostSynchronize(ready, 5'000'000'000ULL);
  const bool returnedBeforeRelease = submitted.wait_for(std::chrono::milliseconds(250)) == std::future_status::ready;
  const auto fenceStatus = zeFenceQueryStatus(fence);
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
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE(zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS);
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr, emptyList = nullptr;
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

TEST_CASE("Level Zero completed fences can be destroyed before queue synchronization", "[level-zero]") {
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));
  uint32_t count = 1;
  ze_driver_handle_t driver = nullptr;
  REQUIRE((zeDriverGet(&count, &driver) == ZE_RESULT_SUCCESS));
  ze_device_handle_t device = nullptr;
  REQUIRE((zeDeviceGet(driver, &count, &device) == ZE_RESULT_SUCCESS));
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS));
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
    const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
    ze_result_t completed;
    do {
      completed = zeFenceQueryStatus(fence);
    } while (completed == ZE_RESULT_NOT_READY && std::chrono::steady_clock::now() < deadline);
    CAPTURE(iteration, completed);
    REQUIRE((completed == ZE_RESULT_SUCCESS));
    // UR resets completed fences before returning them to its pool, including
    // fences it subsequently destroys during context teardown.
    if (iteration % 2 == 0) REQUIRE((zeFenceReset(fence) == ZE_RESULT_SUCCESS));
    const auto destroyed = zeFenceDestroy(fence);
    if (destroyed != ZE_RESULT_SUCCESS) {
      ++failedDestructions;
      // Clean up the old implementation before reporting the regression.
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
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));
  uint32_t count = 1;
  ze_driver_handle_t driver = nullptr;
  REQUIRE((zeDriverGet(&count, &driver) == ZE_RESULT_SUCCESS));
  ze_device_handle_t device = nullptr;
  REQUIRE((zeDeviceGet(driver, &count, &device) == ZE_RESULT_SUCCESS));
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS));
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
  const ze_copy_region_t region{0, 0, 0, 8, 8, 0};
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
        const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
        ze_result_t completed;
        do {
          completed = zeEventQueryStatus(event);
        } while (completed == ZE_RESULT_NOT_READY && std::chrono::steady_clock::now() < deadline);
        const auto freed = completed == ZE_RESULT_SUCCESS ? zeMemFree(context, memory) : ZE_RESULT_NOT_READY;
        if (freed != ZE_RESULT_SUCCESS) ++failedFrees;
        // Destroy only after trying the free: synchronizing the whole list first
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
