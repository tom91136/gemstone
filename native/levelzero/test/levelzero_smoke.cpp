#include <cstdlib>
#include <cstring>
#include <initializer_list>

#include <stdint.h>

#include <catch2/catch_test_macros.hpp>
#include <ze_api.h>

using ImportedPointerFunction = ze_result_t (*)(ze_driver_handle_t, void *, size_t);

TEST_CASE("Level Zero facade smoke test", "[level-zero]") {
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));

  uint32_t driverCount = 0;
  REQUIRE((zeDriverGet(&driverCount, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((driverCount >= 1));
  ze_driver_handle_t driver = nullptr;
  driverCount = 1;
  REQUIRE((zeDriverGet(&driverCount, &driver) == ZE_RESULT_SUCCESS));

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
  REQUIRE((zeMemGetAllocProperties(other_context, source, &foreignProperties, nullptr) == ZE_RESULT_ERROR_INVALID_ARGUMENT));
  std::memset(source, 0x5a, 64);

  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  queueDesc.priority = ZE_COMMAND_QUEUE_PRIORITY_NORMAL;
  ze_command_list_handle_t list = nullptr;
  REQUIRE((zeCommandListCreateImmediate(context, device, &queueDesc, &list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
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
  for (uint32_t i = 0; i < 64; ++i) {
    const bool copied = i >= 2 && i < 26 && (i - 2) % 10 < 4;
    REQUIRE((destinationBytes[i] == (copied ? sourceBytes[1 + (1 + (i - 2) / 10) * 8 + (i - 2) % 10] : 0xa5)));
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
