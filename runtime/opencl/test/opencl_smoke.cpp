#include <CL/cl.h>

#include <cassert>
#include <cstdint>
#include <cstring>

int main() {
  cl_platform_id platform = nullptr;
  cl_uint platform_count = 0;
  assert(clGetPlatformIDs(1, &platform, &platform_count) == CL_SUCCESS);
  assert(platform != nullptr && platform_count == 1);

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

  clSVMFree(context, svm);
  assert(clReleaseCommandQueue(queue) == CL_SUCCESS);
  assert(clReleaseContext(context) == CL_SUCCESS);
  return 0;
}
