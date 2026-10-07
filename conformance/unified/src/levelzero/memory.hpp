#pragma once
#if __has_include(<level_zero/ze_api.h>)
  #include <level_zero/ze_api.h>
#else
  #include <ze_api.h>
#endif

#include "memory_files.hpp"
#include "memory_support.hpp"

namespace memory_patterns {
struct Backend {
  std::string kind;
  ze_driver_handle_t driver{};
  ze_context_handle_t context{};
  ze_device_handle_t device{};
  ze_module_handle_t module{};
  ze_kernel_handle_t kernel{};
  ze_command_list_handle_t list{};
  void check(ze_result_t status) {
    if (status == ZE_RESULT_SUCCESS) return;
    const char *detail = nullptr;
    if (driver) (void)zeDriverGetLastErrorDescription(driver, &detail);
    throw std::runtime_error("Level Zero status " + std::to_string(status) + (detail ? std::string(": ") + detail : ""));
  }
  Backend(std::string kind, uint64_t selected, const char *fixture) : kind(std::move(kind)) {
    check(zeInit(0));
    uint32_t count = 0;
    check(zeDriverGet(&count, nullptr));
    std::vector<ze_driver_handle_t> drivers(count);
    check(zeDriverGet(&count, drivers.data()));
    for (auto candidate : drivers) {
      count = 0;
      check(zeDeviceGet(candidate, &count, nullptr));
      std::vector<ze_device_handle_t> devices(count);
      check(zeDeviceGet(candidate, &count, devices.data()));
      for (auto d : devices)
        if (!device && (!selected || reinterpret_cast<uintptr_t>(d) == selected)) {
          driver = candidate;
          device = d;
        }
    }
    require(device != nullptr, "selected Level Zero device unavailable");
    ze_context_desc_t cd{ZE_STRUCTURE_TYPE_CONTEXT_DESC};
    check(zeContextCreate(driver, &cd, &context));
    auto code = image(fixture);
    ze_module_desc_t md{ZE_STRUCTURE_TYPE_MODULE_DESC};
    md.format = ZE_MODULE_FORMAT_IL_SPIRV;
    md.inputSize = code.size() - 1;
    md.pInputModule = reinterpret_cast<const uint8_t *>(code.data());
    check(zeModuleCreate(context, device, &md, &module, nullptr));
    ze_kernel_desc_t kd{ZE_STRUCTURE_TYPE_KERNEL_DESC};
    kd.pKernelName = "probe";
    check(zeKernelCreate(module, &kd, &kernel));
    check(zeKernelSetIndirectAccess(kernel, ZE_KERNEL_INDIRECT_ACCESS_FLAG_HOST | ZE_KERNEL_INDIRECT_ACCESS_FLAG_SHARED
                                                | ZE_KERNEL_INDIRECT_ACCESS_FLAG_DEVICE));
    ze_command_queue_desc_t qd{ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC};
    qd.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
    check(zeCommandListCreateImmediate(context, device, &qd, &list));
  }
  ~Backend() {
    zeCommandListDestroy(list);
    zeKernelDestroy(kernel);
    zeModuleDestroy(module);
    zeContextDestroy(context);
  }
  void *allocate(size_t size) {
    void *p{};
    ze_device_mem_alloc_desc_t dd{ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC};
    ze_host_mem_alloc_desc_t hd{ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC};
    if (kind == "host") check(zeMemAllocHost(context, &hd, size, 64, &p));
    else check(zeMemAllocShared(context, &dd, &hd, size, 64, device, &p));
    return p;
  }
  void release(void *p) { check(zeMemFree(context, p)); }
  void enqueue(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count, ze_command_list_handle_t target,
               ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
    check(zeKernelSetArgumentValue(kernel, 0, sizeof(input), &input));
    check(zeKernelSetArgumentValue(kernel, 1, sizeof(output), &output));
    check(zeKernelSetArgumentValue(kernel, 2, sizeof(indices), &indices));
    check(zeKernelSetArgumentValue(kernel, 3, sizeof(mode), &mode));
    check(zeKernelSetGroupSize(kernel, count == 1 ? 1 : 32, 1, 1));
    ze_group_count_t groups{count == 1 ? 1 : count / 32, 1, 1};
    check(zeCommandListAppendLaunchKernel(target, kernel, &groups, signal, waitCount, waits));
  }
  void submit(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(input, output, indices, mode, count, list, nullptr, 0, nullptr);
  }
  void finish() { check(zeCommandListHostSynchronize(list, 30000000000ULL)); }
  void launch(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(input, output, indices, mode, count, list, nullptr, 0, nullptr);
    check(zeCommandListHostSynchronize(list, UINT64_MAX));
  }
  void ordered(uint32_t *input, uint32_t *middle, uint32_t *output, unsigned count, bool crossQueue) {
    ze_command_queue_desc_t qd{ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC};
    qd.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
    ze_command_queue_handle_t first{}, second{};
    check(zeCommandQueueCreate(context, device, &qd, &first));
    check(zeCommandQueueCreate(context, device, &qd, &second));
    ze_command_list_desc_t ld{ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC};
    ze_command_list_handle_t producer{}, consumer{};
    check(zeCommandListCreate(context, device, &ld, &producer));
    check(zeCommandListCreate(context, device, &ld, &consumer));
    ze_event_pool_desc_t pd{ZE_STRUCTURE_TYPE_EVENT_POOL_DESC};
    pd.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
    pd.count = 2;
    ze_event_pool_handle_t pool{};
    check(zeEventPoolCreate(context, &pd, 1, &device, &pool));
    ze_event_desc_t ed{ZE_STRUCTURE_TYPE_EVENT_DESC};
    ed.signal = ed.wait = ZE_EVENT_SCOPE_FLAG_HOST;
    ze_event_handle_t produced{}, consumed{};
    check(zeEventCreate(pool, &ed, &produced));
    ed.index = 1;
    check(zeEventCreate(pool, &ed, &consumed));
    enqueue(input, middle, input, 0, count, producer, produced, 0, nullptr);
    enqueue(middle, output, input, 0, count, consumer, consumed, 1, &produced);
    check(zeCommandListClose(producer));
    check(zeCommandListClose(consumer));
    check(zeCommandQueueExecuteCommandLists(first, 1, &producer, nullptr));
    check(zeCommandQueueExecuteCommandLists(crossQueue ? second : first, 1, &consumer, nullptr));
    check(zeEventHostSynchronize(consumed, 30000000000ULL));
    check(zeCommandQueueSynchronize(first, 30000000000ULL));
    check(zeCommandQueueSynchronize(second, 30000000000ULL));
    check(zeCommandListDestroy(consumer));
    check(zeCommandListDestroy(producer));
    check(zeCommandQueueDestroy(second));
    check(zeCommandQueueDestroy(first));
    check(zeEventDestroy(consumed));
    check(zeEventDestroy(produced));
    check(zeEventPoolDestroy(pool));
  }
};

} // namespace memory_patterns
