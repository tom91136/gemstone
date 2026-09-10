#define CL_USE_DEPRECATED_OPENCL_1_1_APIS
#include <CL/cl.h>
#include "../../../native/execution/test/rpc_fixture.hpp"
#include <string>
#include <cstdlib>

#include <cassert>
#include <array>
#include <cstdint>
#include <cstring>

int main() {
  gemstone_test::RpcFixture server;
  const auto port = std::to_string(server.port());
  assert(setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0);
  assert(setenv("GEMSTONE_RPC_TOKEN", gemstone_test::RpcFixture::token, 1) == 0);

  cl_platform_id platform = nullptr;
  cl_uint platform_count = 0;
  assert(clGetPlatformIDs(1, &platform, &platform_count) == CL_SUCCESS);
  assert(platform != nullptr && platform_count == 1);

  // The Khronos loader discovers an ICD through this extension lookup, not only dlsym.
  using GetPlatforms = cl_int(CL_API_CALL *)(cl_uint, cl_platform_id *, cl_uint *);
  auto get_platforms = reinterpret_cast<GetPlatforms>(clGetExtensionFunctionAddress("clIcdGetPlatformIDsKHR"));
  assert(get_platforms != nullptr);
  cl_platform_id discovered = nullptr;
  assert(get_platforms(1, &discovered, nullptr) == CL_SUCCESS);
  assert(discovered == platform);

  cl_device_id device = nullptr;
  cl_uint device_count = 0;
  assert(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, &device_count) == CL_SUCCESS);
  assert(device != nullptr && device_count == 1);
  assert(clRetainDevice(device) == CL_SUCCESS);
  assert(clReleaseDevice(device) == CL_SUCCESS);

  cl_ulong timer_resolution = 1;
  assert(clGetPlatformInfo(platform, CL_PLATFORM_HOST_TIMER_RESOLUTION,
                           sizeof(timer_resolution), &timer_resolution, nullptr) == CL_SUCCESS);
  assert(timer_resolution == 0);
  cl_ulong device_timestamp = 0, host_timestamp = 0;
  assert(clGetDeviceAndHostTimer(device, &device_timestamp, &host_timestamp) == CL_INVALID_OPERATION);
  assert(clGetDeviceAndHostTimer(device, nullptr, nullptr) == CL_INVALID_OPERATION);
  assert(clGetHostTimer(device, &host_timestamp) == CL_INVALID_OPERATION);
  assert(clGetHostTimer(device, nullptr) == CL_INVALID_OPERATION);

  cl_uint vector_width = 0;
  assert(clGetDeviceInfo(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE,
                         sizeof(vector_width), &vector_width, nullptr) == CL_SUCCESS);
  assert(vector_width > 0);
  assert(clGetDeviceInfo(device, CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE,
                         sizeof(vector_width), &vector_width, nullptr) == CL_SUCCESS);
  assert(vector_width > 0);
  cl_device_fp_config double_config = 0;
  assert(clGetDeviceInfo(device, CL_DEVICE_DOUBLE_FP_CONFIG,
                         sizeof(double_config), &double_config, nullptr) == CL_SUCCESS);
  constexpr cl_device_fp_config required_double =
    CL_FP_FMA | CL_FP_ROUND_TO_NEAREST | CL_FP_ROUND_TO_ZERO |
    CL_FP_ROUND_TO_INF | CL_FP_INF_NAN | CL_FP_DENORM;
  assert((double_config & required_double) == required_double);

  cl_int error = CL_SUCCESS;
  cl_context context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  assert(context != nullptr && error == CL_SUCCESS);
  cl_command_queue queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  assert(queue != nullptr && error == CL_SUCCESS);
  cl_context other_context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  assert(other_context != nullptr && error == CL_SUCCESS);
  cl_mem foreign_buffer = clCreateBuffer(other_context, CL_MEM_READ_WRITE, 16, nullptr, &error);
  assert(foreign_buffer != nullptr && error == CL_SUCCESS);
  std::array<uint8_t, 16> foreign_bytes{};
  assert(clEnqueueReadBuffer(queue, foreign_buffer, CL_TRUE, 0, foreign_bytes.size(), foreign_bytes.data(),
                             0, nullptr, nullptr) == CL_INVALID_CONTEXT);
  // The attached memory object retains its context after the application's reference is released.
  assert(clReleaseContext(other_context) == CL_SUCCESS);
  size_t foreign_size = 0;
  assert(clGetMemObjectInfo(foreign_buffer, CL_MEM_SIZE, sizeof(foreign_size), &foreign_size, nullptr) == CL_SUCCESS);
  assert(foreign_size == foreign_bytes.size());
  assert(clReleaseMemObject(foreign_buffer) == CL_SUCCESS);

  const char *unbuilt_source = "__kernel void unbuilt() {}";
  cl_program unbuilt_program = clCreateProgramWithSource(context, 1, &unbuilt_source, nullptr, &error);
  assert(unbuilt_program != nullptr && error == CL_SUCCESS);
  cl_kernel unbuilt_kernel = clCreateKernel(unbuilt_program, "unbuilt", &error);
  assert(unbuilt_kernel == nullptr && error == CL_INVALID_PROGRAM_EXECUTABLE);
  assert(clReleaseProgram(unbuilt_program) == CL_SUCCESS);

  const cl_queue_properties profiling_properties[] = {
    CL_QUEUE_PROPERTIES, CL_QUEUE_PROFILING_ENABLE, 0
  };
  cl_command_queue profiling_queue =
    clCreateCommandQueueWithProperties(context, device, profiling_properties, &error);
  assert(profiling_queue != nullptr && error == CL_SUCCESS);
  cl_event profiling_event = nullptr;
  assert(clEnqueueMarkerWithWaitList(profiling_queue, 0, nullptr, &profiling_event) == CL_SUCCESS);
  cl_ulong queued = 0, submitted = 0, started = 0, ended = 0;
  assert(clGetEventProfilingInfo(profiling_event, CL_PROFILING_COMMAND_QUEUED,
                                 sizeof(queued), &queued, nullptr) == CL_SUCCESS);
  assert(clGetEventProfilingInfo(profiling_event, CL_PROFILING_COMMAND_SUBMIT,
                                 sizeof(submitted), &submitted, nullptr) == CL_SUCCESS);
  assert(clGetEventProfilingInfo(profiling_event, CL_PROFILING_COMMAND_START,
                                 sizeof(started), &started, nullptr) == CL_SUCCESS);
  assert(clGetEventProfilingInfo(profiling_event, CL_PROFILING_COMMAND_END,
                                 sizeof(ended), &ended, nullptr) == CL_SUCCESS);
  assert(queued <= submitted && submitted <= started && started <= ended);
  assert(clReleaseEvent(profiling_event) == CL_SUCCESS);
  assert(clReleaseCommandQueue(profiling_queue) == CL_SUCCESS);

  auto *svm = static_cast<uint8_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, 64, 16));
  assert(svm != nullptr);
  std::memset(svm, 0, 64);

  cl_mem svm_buffer = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 64, svm, &error);
  assert(svm_buffer != nullptr && error == CL_SUCCESS);
  void *svm_buffer_map = clEnqueueMapBuffer(queue, svm_buffer, CL_TRUE,
                                            CL_MAP_READ | CL_MAP_WRITE, 0, 64,
                                            0, nullptr, nullptr, &error);
  assert(svm_buffer_map == svm && error == CL_SUCCESS);
  assert(clEnqueueUnmapMemObject(queue, svm_buffer, svm_buffer_map, 0, nullptr, nullptr) == CL_SUCCESS);
  assert(clReleaseMemObject(svm_buffer) == CL_SUCCESS);

  cl_mem oversized_svm_buffer = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 65, svm, &error);
  assert(oversized_svm_buffer == nullptr && error == CL_INVALID_BUFFER_SIZE);

  cl_event map_event = nullptr;
  assert(clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, svm + 8, 16, 0, nullptr, &map_event) ==
         CL_SUCCESS);
  assert(map_event != nullptr);
  assert(clWaitForEvents(1, &map_event) == CL_SUCCESS);
  assert(clReleaseEvent(map_event) == CL_SUCCESS);
  std::memset(svm + 8, 0x5a, 16);

  cl_event unmap_event = nullptr;
  assert(clEnqueueSVMUnmap(queue, svm + 8, 0, nullptr, &unmap_event) == CL_SUCCESS);
  assert(unmap_event != nullptr);
  assert(clWaitForEvents(1, &unmap_event) == CL_SUCCESS);
  assert(clReleaseEvent(unmap_event) == CL_SUCCESS);
  for (size_t index = 0; index < 8; ++index) assert(svm[index] == 0);
  for (size_t index = 8; index < 24; ++index) assert(svm[index] == 0x5a);
  for (size_t index = 24; index < 64; ++index) assert(svm[index] == 0);

  cl_program program = clCreateProgramWithIL(context, server.spirv, sizeof(server.spirv), &error);
  assert(program != nullptr && error == CL_SUCCESS);
  assert(server.programLoads() == 0);
  assert(clBuildProgram(program, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS);
  assert(server.programLoads() == 1);
  cl_kernel kernel = clCreateKernel(program, "groups", &error);
  assert(kernel != nullptr && error == CL_SUCCESS);
  constexpr size_t capacity = 9 * 33 + 2;
  auto *memory = static_cast<uint32_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, capacity * 4, 8));
  assert(memory != nullptr);
  assert(clSetKernelArgSVMPointer(kernel, 0, memory + 1) == CL_SUCCESS);
  for (bool implicit_local : {false, true})
    for (uint32_t groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t chosen : {31U, 32U, 33U}) {
        if (implicit_local && chosen != 32) continue;
        const size_t local = chosen;
        const size_t count = groups * local - (implicit_local ? 1 : 0);
        const size_t origin = 7;
        for (uint32_t seed : {17U, 91U}) {
          assert(clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_WRITE, memory, capacity * 4, 0, nullptr, nullptr) == CL_SUCCESS);
          std::fill(memory, memory + capacity, 0U);
          assert(clEnqueueSVMUnmap(queue, memory, 0, nullptr, nullptr) == CL_SUCCESS);
          assert(clFinish(queue) == CL_SUCCESS);
          server.expect({chosen, groups, static_cast<uint32_t>(count), 7, seed});
          assert(clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS);
          cl_event event = nullptr;
          assert(clEnqueueNDRangeKernel(queue, kernel, 1, &origin, &count, implicit_local ? nullptr : &local, 0, nullptr, &event)
                 == CL_SUCCESS);
          assert(event != nullptr && clWaitForEvents(1, &event) == CL_SUCCESS);
          cl_int status = CL_QUEUED;
          assert(clGetEventInfo(event, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS);
          assert(status == CL_COMPLETE);
          assert(clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ, memory, capacity * 4, 0, nullptr, nullptr) == CL_SUCCESS);
          for (size_t index = 0; index < count; ++index)
            assert(memory[index + 1] == seed + origin + index);
          assert(memory[0] == 0);
          assert(std::all_of(memory + count + 1, memory + capacity, [](uint32_t value) { return value == 0; }));
          assert(clEnqueueSVMUnmap(queue, memory, 0, nullptr, nullptr) == CL_SUCCESS);
          assert(clFinish(queue) == CL_SUCCESS);
          assert(clReleaseEvent(event) == CL_SUCCESS);
        }
      }
  assert(clReleaseKernel(kernel) == CL_SUCCESS);
  assert(clReleaseProgram(program) == CL_SUCCESS);
  clSVMFree(context, memory);

  clSVMFree(context, svm);
  assert(clReleaseCommandQueue(queue) == CL_SUCCESS);
  assert(clReleaseContext(context) == CL_SUCCESS);
  assert(server.launches() == 40);
  server.check();
  return 0;
}
