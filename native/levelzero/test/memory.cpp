// Native memory transport and scheduling regression client.
#if __has_include(<level_zero/ze_api.h>)
  #include <level_zero/ze_api.h>
#else
  #include <ze_api.h>
#endif

#include "../../rpc/test/memory_patterns.hpp"

namespace memory_patterns {
struct MemoryClient {
  std::string kind;
  std::string allocationKind() const { return kind; }
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
  explicit MemoryClient(std::string kind, uint64_t selected = 0, const char *fixture = "kernel.spv", bool asynchronous = false)
      : kind(std::move(kind)) {
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
    auto code = image(GEMSTONE_MEMORY_IMAGE_DIRECTORY, fixture);
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
    qd.mode = asynchronous ? ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS : ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
    check(zeCommandListCreateImmediate(context, device, &qd, &list));
  }
  ~MemoryClient() {
    zeCommandListDestroy(list);
    zeKernelDestroy(kernel);
    zeModuleDestroy(module);
    zeContextDestroy(context);
  }
  ze_command_list_handle_t overlapLists[2]{};
  void overlapSubmit(unsigned i, void *input, uint32_t *output, unsigned mode) {
    ze_command_queue_desc_t desc{ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC};
    desc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
    check(zeCommandListCreateImmediate(context, device, &desc, &overlapLists[i]));
    enqueue(input, output, reinterpret_cast<uint32_t *>(input), mode, 1, overlapLists[i], nullptr, 0, nullptr);
  }
  void overlapFinish() {
    for (auto list : overlapLists) {
      check(zeCommandListHostSynchronize(list, 30000000000ULL));
      check(zeCommandListDestroy(list));
    }
  }

  void *allocateDevice(size_t size) {
    ze_device_mem_alloc_desc_t desc{ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC};
    void *p{};
    check(zeMemAllocDevice(context, &desc, size, 64, device, &p));
    return p;
  }
  void releaseDevice(void *p) { check(zeMemFree(context, p)); }
  void deviceWrite(void *p, const void *bytes, size_t size) {
    check(zeCommandListAppendMemoryCopy(list, p, bytes, size, nullptr, 0, nullptr));
  }
  void deviceRead(void *bytes, const void *p, size_t size) {
    check(zeCommandListAppendMemoryCopy(list, bytes, p, size, nullptr, 0, nullptr));
  }
  void *allocateBacking(size_t size) {
    void *p{};
    ze_device_mem_alloc_desc_t dd{ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC};
    ze_host_mem_alloc_desc_t hd{ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC};
    if (allocationKind() == "host") check(zeMemAllocHost(context, &hd, size, 64, &p));
    else check(zeMemAllocShared(context, &dd, &hd, size, 64, device, &p));
    return p;
  }
  void releaseBacking(void *p) { check(zeMemFree(context, p)); }
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
  explicit MemoryClient(bool raw = false)
      : MemoryClient(memory_patterns::allocationKind("shared"), 0, "kernel.spv",
                     std::getenv("GEMSTONE_MEMORY_SUBMISSION_PROBE") != nullptr) {
    this->raw = raw;
  }
  bool raw = false;
  void *allocate(size_t size) { return raw ? ordinary(size) : allocateBacking(size); }
  void release(void *p) {
    if (raw) std::free(p);
    else releaseBacking(p);
  }
  bool concurrentCapabilities() {
    ze_device_memory_access_properties_t properties{ZE_STRUCTURE_TYPE_DEVICE_MEMORY_ACCESS_PROPERTIES};
    check(zeDeviceGetMemoryAccessProperties(device, &properties));
    const auto shared = ZE_MEMORY_ACCESS_CAP_FLAG_RW | ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC | ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT
                        | ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT_ATOMIC;
    require(properties.sharedSingleDeviceAllocCapabilities == shared, "L0 shared allocation concurrency unavailable");
    require(properties.hostAllocCapabilities == shared, "L0 host allocation concurrency unavailable");
    for (auto flags :
         {properties.deviceAllocCapabilities, properties.sharedCrossDeviceAllocCapabilities, properties.sharedSystemAllocCapabilities}) {
      require(flags & ZE_MEMORY_ACCESS_CAP_FLAG_RW, "L0 memory access unavailable");
      require(!(flags & (ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT | ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT_ATOMIC)),
              "L0 concurrency unexpectedly advertised");
    }
    return true; // This client uses zeMemAllocShared on the single device.
  }
};
} // namespace memory_patterns

int main(int argc, char **) {
  try {
    memory_patterns::MemoryClient backend(argc > 1);
    return run(backend);
  } catch (const std::exception &e) {
    std::cerr << e.what() << '\n';
    return 1;
  }
}
