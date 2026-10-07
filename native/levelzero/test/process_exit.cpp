#include <cstdlib>
#include <filesystem>
#include <iterator>

#include <dlfcn.h>

#include <ze_api.h>

namespace {

auto threads() { return std::distance(std::filesystem::directory_iterator("/proc/self/task"), {}); }

ze_context_handle_t context = nullptr;
ze_device_handle_t device = nullptr;
ze_event_pool_handle_t pool = nullptr;

// Runtimes layered on the driver, such as SYCL's Unified Runtime adapter, register teardown after loading the driver
// but before its first submission, and release their objects from it.
void teardown() {
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t late = nullptr;
  ze_event_desc_t eventDesc{};
  eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  eventDesc.index = 1;
  eventDesc.signal = eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t signalled = nullptr;
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t signalling = nullptr;
  const bool released =
      zeCommandQueueCreate(context, device, &queueDesc, &late) == ZE_RESULT_SUCCESS
      && zeEventCreate(pool, &eventDesc, &signalled) == ZE_RESULT_SUCCESS
      && zeCommandListCreate(context, device, &listDesc, &signalling) == ZE_RESULT_SUCCESS
      && zeCommandListAppendSignalEvent(signalling, signalled) == ZE_RESULT_SUCCESS && zeCommandListClose(signalling) == ZE_RESULT_SUCCESS
      && zeCommandQueueExecuteCommandLists(late, 1, &signalling, nullptr) == ZE_RESULT_SUCCESS
      && zeEventHostSynchronize(signalled, 1'000'000'000ULL) == ZE_RESULT_SUCCESS && zeCommandQueueDestroy(late) == ZE_RESULT_SUCCESS
      && zeCommandListDestroy(signalling) == ZE_RESULT_SUCCESS && zeEventDestroy(signalled) == ZE_RESULT_SUCCESS;
  if (!released) std::_Exit(EXIT_FAILURE);
}

} // namespace

int main() {
  // The loader loads every installed driver, including in processes that never use this one.
  const auto before = threads();
  if (!dlopen(GEMSTONE_LEVEL_ZERO_DRIVER, RTLD_NOW | RTLD_LOCAL) || threads() != before) return EXIT_FAILURE;
  if (std::atexit(teardown) != 0) return EXIT_FAILURE;
  if (zeInit(ZE_INIT_FLAG_GPU_ONLY) != ZE_RESULT_SUCCESS) return EXIT_FAILURE;
  uint32_t count = 1;
  ze_driver_handle_t driver = nullptr;
  if (zeDriverGet(&count, &driver) != ZE_RESULT_SUCCESS || zeDeviceGet(driver, &count, &device) != ZE_RESULT_SUCCESS) return EXIT_FAILURE;
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  if (zeContextCreate(driver, &contextDesc, &context) != ZE_RESULT_SUCCESS) return EXIT_FAILURE;
  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  if (zeCommandQueueCreate(context, device, &queueDesc, &queue) != ZE_RESULT_SUCCESS) return EXIT_FAILURE;
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
  poolDesc.count = 2;
  if (zeEventPoolCreate(context, &poolDesc, 0, nullptr, &pool) != ZE_RESULT_SUCCESS) return EXIT_FAILURE;
  ze_event_desc_t eventDesc{};
  eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  eventDesc.signal = eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t gate = nullptr;
  if (zeEventCreate(pool, &eventDesc, &gate) != ZE_RESULT_SUCCESS) return EXIT_FAILURE;
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  if (zeCommandListCreate(context, device, &listDesc, &list) != ZE_RESULT_SUCCESS) return EXIT_FAILURE;
  // The queue worker blocks on an event the host never signals.
  if (zeCommandListAppendWaitOnEvents(list, 1, &gate) != ZE_RESULT_SUCCESS || zeCommandListClose(list) != ZE_RESULT_SUCCESS)
    return EXIT_FAILURE;
  if (zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) != ZE_RESULT_SUCCESS) return EXIT_FAILURE;
  return EXIT_SUCCESS;
}
