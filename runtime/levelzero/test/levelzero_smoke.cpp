#include <cassert>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <initializer_list>

#include <ze_api.h>

using ImportedPointerFunction = ze_result_t (*)(ze_driver_handle_t, void *, size_t);

int main() {
  assert(zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS);

  uint32_t driver_count = 0;
  assert(zeDriverGet(&driver_count, nullptr) == ZE_RESULT_SUCCESS);
  assert(driver_count >= 1);
  ze_driver_handle_t driver = nullptr;
  driver_count = 1;
  assert(zeDriverGet(&driver_count, &driver) == ZE_RESULT_SUCCESS);

  void *import = nullptr;
  assert(zeDriverGetExtensionFunctionAddress(driver, "zexDriverImportExternalPointer", reinterpret_cast<void **>(&import))
         == ZE_RESULT_SUCCESS);
  auto import_pointer = reinterpret_cast<ImportedPointerFunction>(import);
  void *external = std::malloc(64);
  assert(external != nullptr);
  assert(import_pointer(driver, external, 64) == ZE_RESULT_SUCCESS);
  void *release = nullptr;
  assert(zeDriverGetExtensionFunctionAddress(driver, "zexDriverReleaseImportedPointer", reinterpret_cast<void **>(&release))
         == ZE_RESULT_SUCCESS);
  auto release_pointer = reinterpret_cast<ImportedPointerFunction>(release);
  assert(release_pointer(driver, external, 0) == ZE_RESULT_SUCCESS);
  std::free(external);

  uint32_t device_count = 0;
  assert(zeDeviceGet(driver, &device_count, nullptr) == ZE_RESULT_SUCCESS);
  assert(device_count == 1);
  ze_device_handle_t device = nullptr;
  device_count = 1;
  assert(zeDeviceGet(driver, &device_count, &device) == ZE_RESULT_SUCCESS);

  ze_device_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
  assert(zeDeviceGetProperties(device, &properties) == ZE_RESULT_SUCCESS);
  assert(properties.type == ZE_DEVICE_TYPE_GPU);
  assert(std::strstr(properties.name, "Gemstone") != nullptr);

  ze_device_compute_properties_t compute_properties{};
  compute_properties.stype = ZE_STRUCTURE_TYPE_DEVICE_COMPUTE_PROPERTIES;
  assert(zeDeviceGetComputeProperties(device, &compute_properties) == ZE_RESULT_SUCCESS);
  assert(compute_properties.numSubGroupSizes == 3);
  assert(compute_properties.subGroupSizes[0] == 8);
  assert(compute_properties.subGroupSizes[1] == 16);
  assert(compute_properties.subGroupSizes[2] == 32);

  ze_context_desc_t context_desc{};
  context_desc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  assert(zeContextCreate(driver, &context_desc, &context) == ZE_RESULT_SUCCESS);
  ze_context_handle_t other_context = nullptr;
  assert(zeContextCreate(driver, &context_desc, &other_context) == ZE_RESULT_SUCCESS);

  ze_device_mem_alloc_desc_t alloc_desc{};
  alloc_desc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  ze_host_mem_alloc_desc_t host_alloc_desc{};
  host_alloc_desc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  const size_t alignments[] = {0, 1, 16, 64, 4096, 8192};
  const ze_memory_type_t types[] = {ZE_MEMORY_TYPE_DEVICE, ZE_MEMORY_TYPE_HOST, ZE_MEMORY_TYPE_SHARED};
  for (const auto type : types) {
    for (const auto alignment : alignments) {
      void *slab = nullptr;
      const size_t slab_size = 65536;
      const auto status = type == ZE_MEMORY_TYPE_DEVICE ? zeMemAllocDevice(context, &alloc_desc, slab_size, alignment, device, &slab)
                          : type == ZE_MEMORY_TYPE_HOST
                              ? zeMemAllocHost(context, &host_alloc_desc, slab_size, alignment, &slab)
                              : zeMemAllocShared(context, &alloc_desc, &host_alloc_desc, slab_size, alignment, device, &slab);
      assert(status == ZE_RESULT_SUCCESS);
      ze_memory_allocation_properties_t allocation_properties{};
      allocation_properties.stype = ZE_STRUCTURE_TYPE_MEMORY_ALLOCATION_PROPERTIES;
      assert(zeMemGetAllocProperties(context, slab, &allocation_properties, nullptr) == ZE_RESULT_SUCCESS);
      const auto address = reinterpret_cast<uintptr_t>(slab);
      assert(allocation_properties.pageSize != 0);
      assert(address % allocation_properties.pageSize == 0);
      assert(alignment == 0 || address % alignment == 0);
      // UMF needs two aligned 32 KiB buckets to fit without extra slab padding.
      const auto first_bucket = (address + 63U) & ~uintptr_t{63U};
      assert(first_bucket + slab_size <= address + slab_size);
      assert(zeMemFree(context, slab) == ZE_RESULT_SUCCESS);
    }
  }
  void *invalid = nullptr;
  assert(zeMemAllocDevice(context, &alloc_desc, 64, 3, device, &invalid) == ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT);
  assert(zeMemAllocHost(context, &host_alloc_desc, 64, 3, &invalid) == ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT);
  assert(zeMemAllocShared(context, &alloc_desc, &host_alloc_desc, 64, 3, device, &invalid) == ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT);

  void *source = nullptr;
  void *destination = nullptr;
  assert(zeMemAllocDevice(context, &alloc_desc, 64, 16, device, &source) == ZE_RESULT_SUCCESS);
  assert(zeMemAllocDevice(context, &alloc_desc, 64, 16, device, &destination) == ZE_RESULT_SUCCESS);
  assert(zeMemFree(other_context, source) == ZE_RESULT_ERROR_INVALID_ARGUMENT);
  ze_memory_allocation_properties_t foreign_properties{};
  foreign_properties.stype = ZE_STRUCTURE_TYPE_MEMORY_ALLOCATION_PROPERTIES;
  assert(zeMemGetAllocProperties(other_context, source, &foreign_properties, nullptr) ==
         ZE_RESULT_ERROR_INVALID_ARGUMENT);
  std::memset(source, 0x5a, 64);

  ze_command_queue_desc_t queue_desc{};
  queue_desc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queue_desc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  queue_desc.priority = ZE_COMMAND_QUEUE_PRIORITY_NORMAL;
  ze_command_list_handle_t list = nullptr;
  assert(zeCommandListCreateImmediate(context, device, &queue_desc, &list) == ZE_RESULT_SUCCESS);
  assert(zeContextDestroy(context) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);
  ze_command_queue_handle_t foreign_queue = nullptr;
  ze_command_list_handle_t foreign_list = nullptr;
  ze_command_list_desc_t isolation_list_desc{};
  isolation_list_desc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  assert(zeCommandQueueCreate(other_context, device, &queue_desc, &foreign_queue) == ZE_RESULT_SUCCESS);
  assert(zeCommandListCreate(other_context, device, &isolation_list_desc, &foreign_list) == ZE_RESULT_SUCCESS);
  assert(zeCommandListClose(foreign_list) == ZE_RESULT_SUCCESS);
  ze_command_list_handle_t wrong_context_lists[] = {foreign_list};
  ze_command_queue_handle_t owner_queue = nullptr;
  assert(zeCommandQueueCreate(context, device, &queue_desc, &owner_queue) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueExecuteCommandLists(owner_queue, 1, wrong_context_lists, nullptr) ==
         ZE_RESULT_ERROR_INVALID_ARGUMENT);
  ze_fence_desc_t fence_desc{};
  fence_desc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  ze_fence_handle_t owner_fence = nullptr;
  assert(zeFenceCreate(owner_queue, &fence_desc, &owner_fence) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueDestroy(owner_queue) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);
  ze_fence_handle_t foreign_fence = nullptr;
  assert(zeFenceCreate(foreign_queue, &fence_desc, &foreign_fence) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueExecuteCommandLists(owner_queue, 0, nullptr, foreign_fence) ==
         ZE_RESULT_ERROR_INVALID_ARGUMENT);
  assert(zeFenceDestroy(foreign_fence) == ZE_RESULT_SUCCESS);
  assert(zeFenceDestroy(owner_fence) == ZE_RESULT_SUCCESS);
  assert(zeCommandListDestroy(foreign_list) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueDestroy(foreign_queue) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueDestroy(owner_queue) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueDestroy(owner_queue) == ZE_RESULT_ERROR_INVALID_NULL_HANDLE);
  assert(zeCommandListAppendMemoryCopy(list, destination, source, 64, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  assert(std::memcmp(source, destination, 64) == 0);

  // Rectangular copies must preserve pitches, origins and guard bytes.
  auto *src_bytes = static_cast<uint8_t *>(source);
  auto *dst_bytes = static_cast<uint8_t *>(destination);
  for (uint32_t i = 0; i < 64; ++i) src_bytes[i] = static_cast<uint8_t>(i);
  std::memset(destination, 0xa5, 64);
  ze_copy_region_t src_region{1, 1, 0, 4, 3, 0};
  ze_copy_region_t dst_region{2, 0, 0, 4, 3, 0};
  ze_event_pool_desc_t pool_desc{};
  pool_desc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  pool_desc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
  pool_desc.count = 2;
  ze_event_pool_handle_t pool{};
  assert(zeEventPoolCreate(context, &pool_desc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS);
  ze_event_desc_t event_desc{};
  event_desc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  event_desc.signal = event_desc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t ready{}, done{};
  assert(zeEventCreate(pool, &event_desc, &ready) == ZE_RESULT_SUCCESS);
  event_desc.index = 1;
  assert(zeEventCreate(pool, &event_desc, &done) == ZE_RESULT_SUCCESS);
  assert(zeEventHostSignal(ready) == ZE_RESULT_SUCCESS);
  assert(zeCommandListAppendMemoryCopyRegion(list, destination, &dst_region, 10, 0,
      source, &src_region, 8, 0, done, 1, &ready) == ZE_RESULT_SUCCESS);
  assert(zeEventQueryStatus(done) == ZE_RESULT_SUCCESS);
  assert(zeEventDestroy(done) == ZE_RESULT_SUCCESS);
  assert(zeEventDestroy(ready) == ZE_RESULT_SUCCESS);
  assert(zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS);
  for (uint32_t i = 0; i < 64; ++i) {
    const bool copied = i >= 2 && i < 26 && (i - 2) % 10 < 4;
    assert(dst_bytes[i] == (copied ? src_bytes[1 + (1 + (i - 2) / 10) * 8 + (i - 2) % 10] : 0xa5));
  }
  ze_copy_region_t invalid_region{0, 0, 0, 5, 3, 0};
  assert(zeCommandListAppendMemoryCopyRegion(list, destination, &invalid_region, 10, 0,
      source, &src_region, 8, 0, nullptr, 0, nullptr) == ZE_RESULT_ERROR_INVALID_SIZE);
  assert(zeCommandListAppendMemoryCopyRegion(list, source, &src_region, 8, 0,
      source, &src_region, 8, 0, nullptr, 0, nullptr) == ZE_RESULT_ERROR_OVERLAPPING_REGIONS);

  auto out_of_bounds = dst_region;
  out_of_bounds.originY = 10;
  assert(zeCommandListAppendMemoryCopyRegion(list, destination, &out_of_bounds, 10, 0,
      source, &src_region, 8, 0, nullptr, 0, nullptr) == ZE_RESULT_ERROR_INVALID_SIZE);
  ze_copy_region_t overflow{0, 0, UINT32_MAX, 4, 1, UINT32_MAX};
  assert(zeCommandListAppendMemoryCopyRegion(list, destination, &overflow, 8, UINT32_MAX,
      source, &overflow, 8, UINT32_MAX, nullptr, 0, nullptr) == ZE_RESULT_ERROR_INVALID_SIZE);
  ze_copy_region_t left{0, 0, 0, 2, 2, 0}, right{4, 0, 0, 2, 2, 0};
  assert(zeCommandListAppendMemoryCopyRegion(list, source, &right, 8, 0,
      source, &left, 8, 0, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  assert(src_bytes[4] == 0 && src_bytes[5] == 1 && src_bytes[12] == 8 && src_bytes[13] == 9);

  // Deferred copies retain allocation ownership and read data at execution, not recording time.
  ze_command_list_desc_t deferred_desc{};
  deferred_desc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t deferred{};
  ze_command_queue_handle_t queue{};
  assert(zeCommandListCreate(context, device, &deferred_desc, &deferred) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueCreate(context, device, &queue_desc, &queue) == ZE_RESULT_SUCCESS);
  src_region = {0, 0, 0, 4, 2, 2};
  dst_region = {0, 0, 0, 4, 2, 2};
  assert(zeCommandListAppendMemoryCopyRegion(deferred, destination, &dst_region, 8, 24,
      source, &src_region, 8, 32, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  src_region.width = dst_region.width = 0;
  std::memset(source, 0x37, 64);
  std::memset(destination, 0xa5, 64);
  assert(zeMemFree(context, source) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);
  assert(zeMemFree(context, destination) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE);
  assert(zeCommandListClose(deferred) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueExecuteCommandLists(queue, 1, &deferred, nullptr) == ZE_RESULT_SUCCESS);
  for (uint32_t i = 0; i < 64; ++i) {
    const bool copied = (i < 12 || (i >= 24 && i < 36)) && i % 8 < 4;
    assert(dst_bytes[i] == (copied ? 0x37 : 0xa5));
  }
  assert(zeCommandListDestroy(deferred) == ZE_RESULT_SUCCESS);
  assert(zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS);
  assert(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  // Linear operations must distinguish an invalid tracked span from an ordinary host pointer.
  ze_command_list_handle_t checked = nullptr;
  assert(zeCommandListCreate(context,device,&deferred_desc,&checked)==ZE_RESULT_SUCCESS);
  uint8_t host_bytes[80];std::memset(host_bytes,0x3c,sizeof(host_bytes));
  const uint8_t pattern=0x17;
  for(size_t offset:{size_t(0),size_t(63),size_t(64)}) {
    const size_t bytes=65-offset;
    assert(zeCommandListAppendMemoryCopy(checked,static_cast<uint8_t *>(destination)+offset,host_bytes,bytes,
      nullptr,0,nullptr)==ZE_RESULT_ERROR_INVALID_SIZE);
    assert(zeCommandListAppendMemoryCopy(checked,host_bytes,static_cast<uint8_t *>(source)+offset,bytes,
      nullptr,0,nullptr)==ZE_RESULT_ERROR_INVALID_SIZE);
    assert(zeCommandListAppendMemoryFill(checked,static_cast<uint8_t *>(destination)+offset,&pattern,1,bytes,
      nullptr,0,nullptr)==ZE_RESULT_ERROR_INVALID_SIZE);
  }
  assert(zeCommandListAppendMemoryCopy(checked,destination,host_bytes,64,nullptr,0,nullptr)==ZE_RESULT_SUCCESS);
  assert(zeCommandListAppendMemoryFill(checked,static_cast<uint8_t *>(destination)+1,&pattern,1,62,nullptr,0,nullptr)==ZE_RESULT_SUCCESS);
  assert(zeCommandListClose(checked)==ZE_RESULT_SUCCESS);
  assert(zeCommandQueueCreate(context,device,&queue_desc,&queue)==ZE_RESULT_SUCCESS);
  assert(zeCommandQueueExecuteCommandLists(queue,1,&checked,nullptr)==ZE_RESULT_SUCCESS);
  assert(static_cast<uint8_t *>(destination)[0]==0x3c && static_cast<uint8_t *>(destination)[63]==0x3c);
  for(size_t i=1;i<63;++i) assert(static_cast<uint8_t *>(destination)[i]==pattern);
  assert(zeCommandListDestroy(checked)==ZE_RESULT_SUCCESS);
  assert(zeCommandQueueDestroy(queue)==ZE_RESULT_SUCCESS);
  ze_memory_free_ext_desc_t free_desc{};
  free_desc.stype = ZE_STRUCTURE_TYPE_MEMORY_FREE_EXT_DESC;
  free_desc.freePolicy = ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_BLOCKING_FREE;
  assert(zeMemFreeExt(context, &free_desc, destination) == ZE_RESULT_SUCCESS);
  assert(zeMemFree(context, source) == ZE_RESULT_SUCCESS);
  assert(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
  assert(zeContextDestroy(other_context) == ZE_RESULT_SUCCESS);
  return 0;
}
