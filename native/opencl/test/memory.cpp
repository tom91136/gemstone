// Native memory transport and scheduling regression client.
#include <CL/cl.h>
#include <CL/cl_ext.h>

#include "../../rpc/test/memory_patterns.hpp"

namespace memory_patterns {
struct MemoryClient {
  std::string kind;
  std::string allocationKind() const { return kind; }
  decltype(&clDeviceMemAllocINTEL) clDeviceMemAllocINTELFn{};
  decltype(&clEnqueueMemcpyINTEL) clEnqueueMemcpyINTELFn{};
  decltype(&clHostMemAllocINTEL) clHostMemAllocINTELFn{};
  decltype(&clMemFreeINTEL) clMemFreeINTELFn{};
  decltype(&clSetKernelArgMemPointerINTEL) clSetKernelArgMemPointerINTELFn{};
  decltype(&clSharedMemAllocINTEL) clSharedMemAllocINTELFn{};
  bool atomicStorage = true;
  std::vector<void *> allocations;
  cl_device_id device{};
  cl_context context{};
  cl_command_queue queue{};
  cl_program program{};
  cl_kernel kernel{};
  explicit MemoryClient(std::string kind, uint64_t selected = 0, bool atomics = true, const char *fixture = "kernel.spv")
      : kind(std::move(kind)), atomicStorage(atomics) {
    cl_platform_id platform{};
    check(clGetPlatformIDs(1, &platform, nullptr));
    if (selected) {
      device = reinterpret_cast<cl_device_id>(selected);
      check(clGetDeviceInfo(device, CL_DEVICE_PLATFORM, sizeof(platform), &platform, nullptr));
    } else check(clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, 1, &device, nullptr));
    clDeviceMemAllocINTELFn =
        reinterpret_cast<decltype(clDeviceMemAllocINTELFn)>(clGetExtensionFunctionAddressForPlatform(platform, "clDeviceMemAllocINTEL"));
    clEnqueueMemcpyINTELFn =
        reinterpret_cast<decltype(clEnqueueMemcpyINTELFn)>(clGetExtensionFunctionAddressForPlatform(platform, "clEnqueueMemcpyINTEL"));
    clHostMemAllocINTELFn =
        reinterpret_cast<decltype(clHostMemAllocINTELFn)>(clGetExtensionFunctionAddressForPlatform(platform, "clHostMemAllocINTEL"));
    clMemFreeINTELFn = reinterpret_cast<decltype(clMemFreeINTELFn)>(clGetExtensionFunctionAddressForPlatform(platform, "clMemFreeINTEL"));
    clSetKernelArgMemPointerINTELFn = reinterpret_cast<decltype(clSetKernelArgMemPointerINTELFn)>(
        clGetExtensionFunctionAddressForPlatform(platform, "clSetKernelArgMemPointerINTEL"));
    clSharedMemAllocINTELFn =
        reinterpret_cast<decltype(clSharedMemAllocINTELFn)>(clGetExtensionFunctionAddressForPlatform(platform, "clSharedMemAllocINTEL"));
    if (allocationKind() == "usm_host" || allocationKind() == "usm_shared")
      require(clHostMemAllocINTELFn && clSharedMemAllocINTELFn && clMemFreeINTELFn && clSetKernelArgMemPointerINTELFn,
              "advertised USM entry points missing");
    cl_int status{};
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &status);
    check(status);
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &status);
    check(status);
    auto code = image(GEMSTONE_MEMORY_IMAGE_DIRECTORY, fixture);
    program = clCreateProgramWithIL(context, code.data(), code.size() - 1, &status);
    check(status);
    check(clBuildProgram(program, 1, &device, "", nullptr, nullptr));
    kernel = clCreateKernel(program, "probe", &status);
    check(status);
  }
  ~MemoryClient() {
    clReleaseKernel(kernel);
    clReleaseProgram(program);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
  }
  cl_command_queue overlapQueues[2]{};
  void overlapSubmit(unsigned i, void *input, uint32_t *output, unsigned mode) {
    cl_int status{};
    overlapQueues[i] = clCreateCommandQueueWithProperties(context, device, nullptr, &status);
    check(status);
    enqueue(input, output, reinterpret_cast<uint32_t *>(input), mode, 1, overlapQueues[i], 0, nullptr, nullptr);
    check(clFlush(overlapQueues[i]));
  }
  void overlapFinish() {
    for (auto q : overlapQueues) {
      check(clFinish(q));
      check(clReleaseCommandQueue(q));
    }
  }

  void *allocateDevice(size_t size) {
    cl_int status{};
    void *p = clDeviceMemAllocINTELFn(context, device, nullptr, size, 64, &status);
    check(status);
    return p;
  }
  void releaseDevice(void *p) { check(clMemFreeINTELFn(context, p)); }
  void deviceWrite(void *p, const void *bytes, size_t size) {
    check(clEnqueueMemcpyINTELFn(queue, CL_TRUE, p, bytes, size, 0, nullptr, nullptr));
  }
  void deviceRead(void *bytes, const void *p, size_t size) {
    check(clEnqueueMemcpyINTELFn(queue, CL_TRUE, bytes, p, size, 0, nullptr, nullptr));
  }
  void *allocateBacking(size_t size) {
    void *p{};
    cl_int status{};
    if (allocationKind() == "usm_host") p = clHostMemAllocINTELFn(context, nullptr, size, 64, &status);
    else if (allocationKind() == "usm_shared") p = clSharedMemAllocINTELFn(context, device, nullptr, size, 64, &status);
    else p = clSVMAlloc(context, CL_MEM_READ_WRITE | CL_MEM_SVM_FINE_GRAIN_BUFFER | (atomicStorage ? CL_MEM_SVM_ATOMICS : 0), size, 64);
    check(status);
    require(p, "SVM allocation");
    allocations.push_back(p);
    return p;
  }
  void releaseBacking(void *p) {
    allocations.erase(std::remove(allocations.begin(), allocations.end(), p), allocations.end());
    if (allocationKind() == "usm_host" || allocationKind() == "usm_shared") check(clMemFreeINTELFn(context, p));
    else clSVMFree(context, p);
  }
  void enqueue(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count, cl_command_queue target, cl_uint waitCount,
               const cl_event *waits, cl_event *event) {
    if (!allocations.empty()) {
      const auto info = allocationKind() == "usm_host" || allocationKind() == "usm_shared" ? CL_KERNEL_EXEC_INFO_USM_PTRS_INTEL
                                                                                           : CL_KERNEL_EXEC_INFO_SVM_PTRS;
      check(clSetKernelExecInfo(kernel, info, allocations.size() * sizeof(void *), allocations.data()));
    }
    auto setPointer =
        allocationKind() == "usm_host" || allocationKind() == "usm_shared" ? clSetKernelArgMemPointerINTELFn : clSetKernelArgSVMPointer;
    check(setPointer(kernel, 0, input));
    check(setPointer(kernel, 1, output));
    check(setPointer(kernel, 2, indices));
    check(clSetKernelArg(kernel, 3, sizeof(mode), &mode));
    const size_t global = count, local = count == 1 ? 1 : 32;
    check(clEnqueueNDRangeKernel(target, kernel, 1, nullptr, &global, &local, waitCount, waits, event));
  }
  void submit(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(input, output, indices, mode, count, queue, 0, nullptr, nullptr);
  }
  void finish() { check(clFinish(queue)); }
  void launch(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count) {
    enqueue(input, output, indices, mode, count, queue, 0, nullptr, nullptr);
    check(clFinish(queue));
  }
  explicit MemoryClient(bool raw = false) : MemoryClient(memory_patterns::allocationKind("svm")) { this->raw = raw; }
  bool raw = false;
  void *allocate(size_t size) { return raw ? ordinary(size) : allocateBacking(size); }
  void release(void *p) {
    if (raw) std::free(p);
    else releaseBacking(p);
  }
  bool concurrentCapabilities() {
    cl_device_svm_capabilities svm{};
    check(clGetDeviceInfo(device, CL_DEVICE_SVM_CAPABILITIES, sizeof(svm), &svm, nullptr));
    require(svm & CL_DEVICE_SVM_FINE_GRAIN_BUFFER, "OpenCL fine-grained SVM unavailable");
    require(svm & CL_DEVICE_SVM_ATOMICS, "OpenCL SVM atomics unavailable");
    for (auto name : {CL_DEVICE_HOST_MEM_CAPABILITIES_INTEL, CL_DEVICE_SINGLE_DEVICE_SHARED_MEM_CAPABILITIES_INTEL}) {
      cl_device_unified_shared_memory_capabilities_intel flags{};
      check(clGetDeviceInfo(device, name, sizeof(flags), &flags, nullptr));
      require(flags & CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL, "Intel USM concurrency unavailable");
      const auto atomic = CL_UNIFIED_SHARED_MEMORY_ATOMIC_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ATOMIC_ACCESS_INTEL;
      require((flags & atomic) == atomic, "Intel USM atomics unavailable");
    }
    require(svm & CL_DEVICE_SVM_FINE_GRAIN_SYSTEM, "OpenCL fine-grained system SVM unavailable");
    cl_device_unified_shared_memory_capabilities_intel system{};
    check(clGetDeviceInfo(device, CL_DEVICE_SHARED_SYSTEM_MEM_CAPABILITIES_INTEL, sizeof(system), &system, nullptr));
    const auto concurrentAtomic =
        CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ATOMIC_ACCESS_INTEL;
    require((system & concurrentAtomic) == concurrentAtomic, "Intel shared system memory concurrency unavailable");
    return true;
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
