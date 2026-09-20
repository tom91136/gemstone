#define CL_USE_DEPRECATED_OPENCL_1_1_APIS
#include <algorithm>
#include <array>
#include <cstdlib>
#include <string>

#include <stdint.h>

#include <CL/cl.h>
#include <CL/cl_ext.h>
#include <catch2/catch_test_macros.hpp>

#include "../../rpc/test/rpc_fixture.hpp"

TEST_CASE("OpenCL facade launch and memory smoke test", "[opencl]") {
  gemstone_test::RpcFixture server;
  server.programCatalog({{"groups", "groups"}}, {});
  const auto port = std::to_string(server.port());
  REQUIRE((setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0));
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));

  cl_platform_id platform = nullptr;
  cl_uint platformCount = 0;
  REQUIRE((clGetPlatformIDs(1, &platform, &platformCount) == CL_SUCCESS));
  REQUIRE((platform && platformCount == 1));

  // The Khronos loader discovers an ICD through this extension lookup, not only dlsym.
  using GetPlatforms = cl_int(CL_API_CALL *)(cl_uint, cl_platform_id *, cl_uint *);
  auto getPlatforms = reinterpret_cast<GetPlatforms>(clGetExtensionFunctionAddress("clIcdGetPlatformIDsKHR"));
  REQUIRE((getPlatforms));
  cl_platform_id discovered = nullptr;
  REQUIRE((getPlatforms(1, &discovered, nullptr) == CL_SUCCESS));
  REQUIRE((discovered == platform));

  cl_device_id device = nullptr;
  cl_uint deviceCount = 0;
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, &deviceCount) == CL_SUCCESS));
  REQUIRE((device && deviceCount == 1));
  REQUIRE((clRetainDevice(device) == CL_SUCCESS));
  REQUIRE((clReleaseDevice(device) == CL_SUCCESS));

  cl_device_pci_bus_info_khr pci{};
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_PCI_BUS_INFO_KHR, sizeof(pci), &pci, nullptr) == CL_SUCCESS));
  REQUIRE((pci.pci_domain == 0));
  REQUIRE((pci.pci_bus == 0));
  REQUIRE((pci.pci_device == 1));
  REQUIRE((pci.pci_function == 0));

  cl_ulong timerResolution = 1;
  REQUIRE(
      (clGetPlatformInfo(platform, CL_PLATFORM_HOST_TIMER_RESOLUTION, sizeof(timerResolution), &timerResolution, nullptr) == CL_SUCCESS));
  REQUIRE((timerResolution == 0));
  cl_ulong deviceTimestamp = 0, hostTimestamp = 0;
  REQUIRE((clGetDeviceAndHostTimer(device, &deviceTimestamp, &hostTimestamp) == CL_INVALID_OPERATION));
  REQUIRE((clGetDeviceAndHostTimer(device, nullptr, nullptr) == CL_INVALID_OPERATION));
  REQUIRE((clGetHostTimer(device, &hostTimestamp) == CL_INVALID_OPERATION));
  REQUIRE((clGetHostTimer(device, nullptr) == CL_INVALID_OPERATION));

  cl_uint vectorWidth = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE, sizeof(vectorWidth), &vectorWidth, nullptr) == CL_SUCCESS));
  REQUIRE((vectorWidth > 0));
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE, sizeof(vectorWidth), &vectorWidth, nullptr) == CL_SUCCESS));
  REQUIRE((vectorWidth > 0));
  cl_device_fp_config doubleConfig = 0;
  REQUIRE((clGetDeviceInfo(device, CL_DEVICE_DOUBLE_FP_CONFIG, sizeof(doubleConfig), &doubleConfig, nullptr) == CL_SUCCESS));
  constexpr cl_device_fp_config requiredDouble =
      CL_FP_FMA | CL_FP_ROUND_TO_NEAREST | CL_FP_ROUND_TO_ZERO | CL_FP_ROUND_TO_INF | CL_FP_INF_NAN | CL_FP_DENORM;
  REQUIRE(((doubleConfig & requiredDouble) == requiredDouble));

  cl_int error = CL_SUCCESS;
  cl_context context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  cl_command_queue queue = clCreateCommandQueueWithProperties(context, device, nullptr, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  cl_context otherContext = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((otherContext && error == CL_SUCCESS));
  cl_mem foreignBuffer = clCreateBuffer(otherContext, CL_MEM_READ_WRITE, 16, nullptr, &error);
  REQUIRE((foreignBuffer && error == CL_SUCCESS));
  std::array<uint8_t, 16> foreignBytes{};
  REQUIRE((clEnqueueReadBuffer(queue, foreignBuffer, CL_TRUE, 0, foreignBytes.size(), foreignBytes.data(), 0, nullptr, nullptr)
           == CL_INVALID_CONTEXT));
  // The attached memory object retains its context after the application's reference is released.
  REQUIRE((clReleaseContext(otherContext) == CL_SUCCESS));
  size_t foreignSize = 0;
  REQUIRE((clGetMemObjectInfo(foreignBuffer, CL_MEM_SIZE, sizeof(foreignSize), &foreignSize, nullptr) == CL_SUCCESS));
  REQUIRE((foreignSize == foreignBytes.size()));
  REQUIRE((clReleaseMemObject(foreignBuffer) == CL_SUCCESS));

  const char *unbuiltSource = "__kernel void unbuilt() {}";
  cl_program unbuiltProgram = clCreateProgramWithSource(context, 1, &unbuiltSource, nullptr, &error);
  REQUIRE((unbuiltProgram && error == CL_SUCCESS));
  cl_kernel unbuiltKernel = clCreateKernel(unbuiltProgram, "unbuilt", &error);
  REQUIRE((!unbuiltKernel && error == CL_INVALID_PROGRAM_EXECUTABLE));
  REQUIRE((clReleaseProgram(unbuiltProgram) == CL_SUCCESS));

  const cl_queue_properties profilingProperties[] = {CL_QUEUE_PROPERTIES, CL_QUEUE_PROFILING_ENABLE, 0};
  cl_command_queue profilingQueue = clCreateCommandQueueWithProperties(context, device, profilingProperties, &error);
  REQUIRE((profilingQueue && error == CL_SUCCESS));
  cl_event profilingEvent = nullptr;
  REQUIRE((clEnqueueMarkerWithWaitList(profilingQueue, 0, nullptr, &profilingEvent) == CL_SUCCESS));
  cl_ulong queued = 0, submitted = 0, started = 0, ended = 0;
  REQUIRE((clGetEventProfilingInfo(profilingEvent, CL_PROFILING_COMMAND_QUEUED, sizeof(queued), &queued, nullptr) == CL_SUCCESS));
  REQUIRE((clGetEventProfilingInfo(profilingEvent, CL_PROFILING_COMMAND_SUBMIT, sizeof(submitted), &submitted, nullptr) == CL_SUCCESS));
  REQUIRE((clGetEventProfilingInfo(profilingEvent, CL_PROFILING_COMMAND_START, sizeof(started), &started, nullptr) == CL_SUCCESS));
  REQUIRE((clGetEventProfilingInfo(profilingEvent, CL_PROFILING_COMMAND_END, sizeof(ended), &ended, nullptr) == CL_SUCCESS));
  REQUIRE((queued <= submitted && submitted <= started && started <= ended));
  REQUIRE((clReleaseEvent(profilingEvent) == CL_SUCCESS));
  REQUIRE((clReleaseCommandQueue(profilingQueue) == CL_SUCCESS));

  auto *svm = static_cast<uint8_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, 64, 16));
  REQUIRE((svm));
  std::fill_n(svm, 64, uint8_t{0});

  cl_mem svmBuffer = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 64, svm, &error);
  REQUIRE((svmBuffer && error == CL_SUCCESS));
  void *svmBufferMap = clEnqueueMapBuffer(queue, svmBuffer, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, 0, 64, 0, nullptr, nullptr, &error);
  REQUIRE((svmBufferMap == svm && error == CL_SUCCESS));
  REQUIRE((clEnqueueUnmapMemObject(queue, svmBuffer, svmBufferMap, 0, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((clReleaseMemObject(svmBuffer) == CL_SUCCESS));

  cl_mem oversizedSvmBuffer = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 65, svm, &error);
  REQUIRE((!oversizedSvmBuffer && error == CL_INVALID_BUFFER_SIZE));

  cl_event mapEvent = nullptr;
  REQUIRE((clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, svm + 8, 16, 0, nullptr, &mapEvent) == CL_SUCCESS));
  REQUIRE((mapEvent));
  REQUIRE((clWaitForEvents(1, &mapEvent) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(mapEvent) == CL_SUCCESS));
  std::fill_n(svm + 8, 16, uint8_t{0x5a});

  cl_event unmapEvent = nullptr;
  REQUIRE((clEnqueueSVMUnmap(queue, svm + 8, 0, nullptr, &unmapEvent) == CL_SUCCESS));
  REQUIRE((unmapEvent));
  REQUIRE((clWaitForEvents(1, &unmapEvent) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(unmapEvent) == CL_SUCCESS));
  for (size_t index = 0; index < 8; ++index)
    REQUIRE((svm[index] == 0));
  for (size_t index = 8; index < 24; ++index)
    REQUIRE((svm[index] == 0x5a));
  for (size_t index = 24; index < 64; ++index)
    REQUIRE((svm[index] == 0));

  cl_program program = clCreateProgramWithIL(context, server.spirv, sizeof(server.spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((server.programLoads() == 0));
  REQUIRE((clBuildProgram(program, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS));
  REQUIRE((server.programLoads() == 1));
  cl_kernel kernel = clCreateKernel(program, "groups", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  constexpr size_t capacity = 9 * 33 + 2;
  auto *memory = static_cast<uint32_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, capacity * 4, 8));
  REQUIRE((memory));
  REQUIRE((clSetKernelArgSVMPointer(kernel, 0, memory + 1) == CL_SUCCESS));
  for (bool implicitLocal : {false, true})
    for (uint32_t groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t chosen : {31U, 32U, 33U}) {
        if (implicitLocal && chosen != 32) continue;
        const size_t local = chosen;
        const size_t count = groups * local - (implicitLocal ? 1 : 0);
        const size_t origin = 7;
        for (uint32_t seed : {17U, 91U}) {
          REQUIRE((clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_WRITE, memory, capacity * 4, 0, nullptr, nullptr) == CL_SUCCESS));
          std::fill(memory, memory + capacity, 0U);
          REQUIRE((clEnqueueSVMUnmap(queue, memory, 0, nullptr, nullptr) == CL_SUCCESS));
          REQUIRE((clFinish(queue) == CL_SUCCESS));
          server.expect({chosen, groups, static_cast<uint32_t>(count), 7, seed});
          REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
          cl_event event = nullptr;
          REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, &origin, &count, implicitLocal ? nullptr : &local, 0, nullptr, &event)
                   == CL_SUCCESS));
          REQUIRE((event && clWaitForEvents(1, &event) == CL_SUCCESS));
          cl_int status = CL_QUEUED;
          REQUIRE((clGetEventInfo(event, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS));
          REQUIRE((status == CL_COMPLETE));
          REQUIRE((clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ, memory, capacity * 4, 0, nullptr, nullptr) == CL_SUCCESS));
          for (size_t index = 0; index < count; ++index)
            REQUIRE((memory[index + 1] == seed + origin + index));
          REQUIRE((memory[0] == 0));
          REQUIRE((std::all_of(memory + count + 1, memory + capacity, [](uint32_t value) { return value == 0; })));
          REQUIRE((clEnqueueSVMUnmap(queue, memory, 0, nullptr, nullptr) == CL_SUCCESS));
          REQUIRE((clFinish(queue) == CL_SUCCESS));
          REQUIRE((clReleaseEvent(event) == CL_SUCCESS));
        }
      }
  REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
  REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  clSVMFree(context, memory);

  clSVMFree(context, svm);
  REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
  REQUIRE((server.launches() == 40));
  server.check();
}
