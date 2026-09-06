#include <ze_api.h>

#include <cassert>
#include <cstdint>
#include <cstring>
#include <cstdlib>

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
  assert(zeDriverGetExtensionFunctionAddress(
             driver, "zexDriverImportExternalPointer", reinterpret_cast<void **>(&import)) == ZE_RESULT_SUCCESS);
  auto import_pointer = reinterpret_cast<ImportedPointerFunction>(import);
  void *external = std::malloc(64);
  assert(external != nullptr);
  assert(import_pointer(driver, external, 64) == ZE_RESULT_SUCCESS);
  void *release = nullptr;
  assert(zeDriverGetExtensionFunctionAddress(
             driver, "zexDriverReleaseImportedPointer", reinterpret_cast<void **>(&release)) == ZE_RESULT_SUCCESS);
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

  ze_device_mem_alloc_desc_t alloc_desc{};
  alloc_desc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *source = nullptr;
  void *destination = nullptr;
  assert(zeMemAllocDevice(context, &alloc_desc, 64, 16, device, &source) == ZE_RESULT_SUCCESS);
  assert(zeMemAllocDevice(context, &alloc_desc, 64, 16, device, &destination) == ZE_RESULT_SUCCESS);
  std::memset(source, 0x5a, 64);

  ze_command_queue_desc_t queue_desc{};
  queue_desc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queue_desc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  queue_desc.priority = ZE_COMMAND_QUEUE_PRIORITY_NORMAL;
  ze_command_list_handle_t list = nullptr;
  assert(zeCommandListCreateImmediate(context, device, &queue_desc, &list) == ZE_RESULT_SUCCESS);
  assert(zeCommandListAppendMemoryCopy(list, destination, source, 64, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS);
  assert(std::memcmp(source, destination, 64) == 0);

  assert(zeCommandListDestroy(list) == ZE_RESULT_SUCCESS);
  ze_memory_free_ext_desc_t free_desc{};
  free_desc.stype = ZE_STRUCTURE_TYPE_MEMORY_FREE_EXT_DESC;
  free_desc.freePolicy = ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_BLOCKING_FREE;
  assert(zeMemFreeExt(context, &free_desc, destination) == ZE_RESULT_SUCCESS);
  assert(zeMemFree(context, source) == ZE_RESULT_SUCCESS);
  assert(zeContextDestroy(context) == ZE_RESULT_SUCCESS);
  return 0;
}
