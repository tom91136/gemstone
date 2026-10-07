#pragma once
#include <CL/cl.h>
#include <CL/cl_ext.h>

#include "memory_files.hpp"
#include "memory_support.hpp"

namespace memory_patterns {
struct Backend {
  std::string kind;
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
  Backend(std::string kind, uint64_t selected, bool atomics, const char *fixture) : kind(std::move(kind)), atomicStorage(atomics) {
    cl_platform_id platform{};
    check(clGetPlatformIDs(1, &platform, nullptr));
    if (selected) {
      device = reinterpret_cast<cl_device_id>(selected);
      check(clGetDeviceInfo(device, CL_DEVICE_PLATFORM, sizeof(platform), &platform, nullptr));
    } else check(clGetDeviceIDs(platform, CL_DEVICE_TYPE_ALL, 1, &device, nullptr));
    clHostMemAllocINTELFn =
        reinterpret_cast<decltype(clHostMemAllocINTELFn)>(clGetExtensionFunctionAddressForPlatform(platform, "clHostMemAllocINTEL"));
    clMemFreeINTELFn = reinterpret_cast<decltype(clMemFreeINTELFn)>(clGetExtensionFunctionAddressForPlatform(platform, "clMemFreeINTEL"));
    clSetKernelArgMemPointerINTELFn = reinterpret_cast<decltype(clSetKernelArgMemPointerINTELFn)>(
        clGetExtensionFunctionAddressForPlatform(platform, "clSetKernelArgMemPointerINTEL"));
    clSharedMemAllocINTELFn =
        reinterpret_cast<decltype(clSharedMemAllocINTELFn)>(clGetExtensionFunctionAddressForPlatform(platform, "clSharedMemAllocINTEL"));
    if (kind == "usm_host" || kind == "usm_shared")
      require(clHostMemAllocINTELFn && clSharedMemAllocINTELFn && clMemFreeINTELFn && clSetKernelArgMemPointerINTELFn,
              "advertised USM entry points missing");
    cl_int status{};
    context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &status);
    check(status);
    queue = clCreateCommandQueueWithProperties(context, device, nullptr, &status);
    check(status);
    auto code = image(fixture);
    program = clCreateProgramWithIL(context, code.data(), code.size() - 1, &status);
    check(status);
    check(clBuildProgram(program, 1, &device, "", nullptr, nullptr));
    kernel = clCreateKernel(program, "probe", &status);
    check(status);
  }
  ~Backend() {
    clReleaseKernel(kernel);
    clReleaseProgram(program);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
  }
  void *allocate(size_t size) {
    void *p{};
    cl_int status{};
    if (kind == "usm_host") p = clHostMemAllocINTELFn(context, nullptr, size, 64, &status);
    else if (kind == "usm_shared") p = clSharedMemAllocINTELFn(context, device, nullptr, size, 64, &status);
    else p = clSVMAlloc(context, CL_MEM_READ_WRITE | CL_MEM_SVM_FINE_GRAIN_BUFFER | (atomicStorage ? CL_MEM_SVM_ATOMICS : 0), size, 64);
    check(status);
    require(p, "SVM allocation");
    allocations.push_back(p);
    return p;
  }
  void release(void *p) {
    allocations.erase(std::remove(allocations.begin(), allocations.end(), p), allocations.end());
    if (kind == "usm_host" || kind == "usm_shared") check(clMemFreeINTELFn(context, p));
    else clSVMFree(context, p);
  }
  void enqueue(void *input, uint32_t *output, uint32_t *indices, unsigned mode, unsigned count, cl_command_queue target, cl_uint waitCount,
               const cl_event *waits, cl_event *event) {
    if (!allocations.empty()) {
      const auto info = kind == "usm_host" || kind == "usm_shared" ? CL_KERNEL_EXEC_INFO_USM_PTRS_INTEL : CL_KERNEL_EXEC_INFO_SVM_PTRS;
      check(clSetKernelExecInfo(kernel, info, allocations.size() * sizeof(void *), allocations.data()));
    }
    auto setPointer = kind == "usm_host" || kind == "usm_shared" ? clSetKernelArgMemPointerINTELFn : clSetKernelArgSVMPointer;
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
  void ordered(uint32_t *input, uint32_t *middle, uint32_t *output, unsigned count, bool crossQueue) {
    cl_int status{};
    auto second = clCreateCommandQueueWithProperties(context, device, nullptr, &status);
    check(status);
    cl_event produced{}, consumed{};
    enqueue(input, middle, input, 0, count, queue, 0, nullptr, &produced);
    enqueue(middle, output, input, 0, count, crossQueue ? second : queue, 1, &produced, &consumed);
    check(clFlush(queue));
    check(clFlush(second));
    check(clWaitForEvents(1, &consumed));
    check(clReleaseEvent(consumed));
    check(clReleaseEvent(produced));
    check(clReleaseCommandQueue(second));
  }
};

} // namespace memory_patterns
