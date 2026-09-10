#include <atomic>
#include <cassert>
#include <chrono>
#include <cstdlib>
#include <cstring>
#include <string>
#include <thread>

#include "../../../native/execution/test/rpc_fixture.hpp"
#include <CL/cl.h>

int main(int argc, char **argv) {
  assert(argc == 2);
  gemstone_test::RpcFixture server;
  const auto port = std::to_string(server.port());
  assert(setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0);
  assert(setenv("GEMSTONE_RPC_TOKEN", gemstone_test::RpcFixture::token, 1) == 0);
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  assert(clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS);
  assert(clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS);
  cl_int error = 0;
  auto context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  assert(context && error == CL_SUCCESS);
  const bool out_of_order = std::strcmp(argv[1], "out-of-order") == 0 || std::strcmp(argv[1], "command-marker-ooo") == 0
                            || std::strcmp(argv[1], "command-barrier-ooo") == 0 || std::strcmp(argv[1], "command-blocking-rect-ooo") == 0
                            || std::strcmp(argv[1], "command-blocking-svm-ooo") == 0;
  const cl_queue_properties properties[] = {CL_QUEUE_PROPERTIES, CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE, 0};
  auto queue = clCreateCommandQueueWithProperties(context, device, out_of_order ? properties : nullptr, &error);
  assert(queue && error == CL_SUCCESS);
  auto program = clCreateProgramWithIL(context, server.spirv, sizeof(server.spirv), &error);
  assert(program && error == CL_SUCCESS);
  assert(clBuildProgram(program, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS);
  auto kernel = clCreateKernel(program, "groups", &error);
  assert(kernel && error == CL_SUCCESS);
  auto *original = static_cast<uint32_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, 4, 8));
  auto *replacement = static_cast<uint32_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, 4, 8));
  assert(original && replacement);
  *original = 0;
  *replacement = 0;
  std::atomic_uint destroyed{0};
  auto memory = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 4, original, &error);
  assert(memory && error == CL_SUCCESS);
  assert(clSetMemObjectDestructorCallback(
             memory, [](cl_mem, void *data) { static_cast<std::atomic_uint *>(data)->fetch_add(1); }, &destroyed)
         == CL_SUCCESS);
  auto other = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 4, replacement, &error);
  assert(other && error == CL_SUCCESS);
  const std::string command = argv[1];
  void *mapped = nullptr;
  if ((command == "command-unmap" || command == "command-unmap-error")) {
    mapped = clEnqueueMapBuffer(queue, memory, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, 0, 4, 0, nullptr, nullptr, &error);
    assert(mapped && error == CL_SUCCESS);
  }
  if ((command == "command-svm-unmap" || command == "command-svm-unmap-error"))
    assert(clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, original, 4, 0, nullptr, nullptr) == CL_SUCCESS);
  auto gate = clCreateUserEvent(context, &error);
  assert(gate && error == CL_SUCCESS);
  const bool remote_failure = std::strcmp(argv[1], "remote-error") == 0;
  const bool no_event = std::strcmp(argv[1], "error-no-event") == 0;
  const bool teardown = std::strcmp(argv[1], "teardown") == 0;
  if (remote_failure || no_event) server.failNextCompletion();
  uint32_t seed = 7;
  assert(clSetKernelArg(kernel, 0, sizeof(memory), &memory) == CL_SUCCESS);
  assert(clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS);
  server.expect({1, 1, 1, 0, 7});
  const size_t one = 1;
  cl_event first = nullptr, second = nullptr, third = nullptr;
  if (std::strcmp(argv[1], "validation") == 0) {
    const size_t too_large = size_t(UINT32_MAX) + 1;
    assert(clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &too_large, &one, 1, &gate, &first) == CL_INVALID_GLOBAL_WORK_SIZE);
    assert(first == nullptr);
  }
  assert(clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &one, &one, 1, &gate, no_event ? nullptr : &first) == CL_SUCCESS);
  if (teardown) {
    assert(clReleaseCommandQueue(queue) == CL_SUCCESS);
    queue = nullptr;
  }
  assert(server.launches() == 0);
  const bool snapshot = std::strcmp(argv[1], "snapshot") == 0;
  const bool consecutive = std::strcmp(argv[1], "consecutive") == 0 || command == "out-of-order";
  const bool transfer = std::strcmp(argv[1], "transfer") == 0;
  uint32_t readback = 0;
  const bool failure = std::strcmp(argv[1], "dependency-error") == 0 || command.ends_with("-error");
  if (snapshot) {
    seed = 99;
    assert(clSetKernelArg(kernel, 0, sizeof(other), &other) == CL_SUCCESS);
    assert(clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS);
    assert(clReleaseMemObject(memory) == CL_SUCCESS);
    memory = nullptr;
    assert(destroyed.load() == 0);
    assert(clReleaseKernel(kernel) == CL_SUCCESS);
    kernel = nullptr;
    assert(clReleaseProgram(program) == CL_SUCCESS);
    program = nullptr;
  }
  if (consecutive) {
    // This must return before the user signals the preceding command's gate.
    assert(clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &one, &one, 0, nullptr, &second) == CL_SUCCESS);
    if (out_of_order) {
      const auto until = std::chrono::steady_clock::now() + std::chrono::seconds(3);
      cl_int second_status = CL_QUEUED;
      do {
        assert(clGetEventInfo(second, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(second_status), &second_status, nullptr) == CL_SUCCESS);
        if (second_status == CL_COMPLETE) break;
        std::this_thread::sleep_for(std::chrono::milliseconds(1));
      } while (std::chrono::steady_clock::now() < until);
      assert(second_status == CL_COMPLETE && server.launches() == 1);
    } else assert(server.launches() == 0);
  }
  if (transfer) {
    assert(clEnqueueReadBuffer(queue, memory, CL_FALSE, 0, 4, &readback, 0, nullptr, &second) == CL_SUCCESS);
    assert(readback == 0 && server.launches() == 0);
  }
  size_t origin[3]{0, 0, 0}, region[3]{4, 1, 1};
  uint32_t pattern = 13, input = 17;
  if (command == "command-copy" || command == "command-copy-release")
    assert(clEnqueueCopyBuffer(queue, memory, other, 0, 0, 4, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-fill") assert(clEnqueueFillBuffer(queue, other, &pattern, 4, 0, 4, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-read-rect")
    assert(clEnqueueReadBufferRect(queue, memory, CL_FALSE, origin, origin, region, 0, 0, 0, 0, &readback, 0, nullptr, &second)
           == CL_SUCCESS);
  if (command == "command-write-rect")
    assert(clEnqueueWriteBufferRect(queue, other, CL_FALSE, origin, origin, region, 0, 0, 0, 0, &input, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-copy-rect")
    assert(clEnqueueCopyBufferRect(queue, memory, other, origin, origin, region, 0, 0, 0, 0, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-map" || command == "command-map-error" || command == "command-map-twice") {
    mapped = clEnqueueMapBuffer(queue, memory, CL_FALSE, CL_MAP_READ, 0, 4, failure ? 1 : 0, failure ? &gate : nullptr, &second, &error);
    assert(mapped && error == CL_SUCCESS);
    if (command == "command-map-twice") {
      auto again = clEnqueueMapBuffer(queue, memory, CL_FALSE, CL_MAP_READ, 0, 4, 0, nullptr, &third, &error);
      assert(again == mapped && error == CL_SUCCESS);
    }
  }
  if ((command == "command-unmap" || command == "command-unmap-error"))
    assert(clEnqueueUnmapMemObject(queue, memory, mapped, failure ? 1 : 0, failure ? &gate : nullptr, &second) == CL_SUCCESS);
  if (command == "command-svm-copy")
    assert(clEnqueueSVMMemcpy(queue, CL_FALSE, replacement, original, 4, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-svm-fill") assert(clEnqueueSVMMemFill(queue, replacement, &pattern, 4, 4, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-svm-map" || command == "command-svm-map-error")
    assert(clEnqueueSVMMap(queue, CL_FALSE, CL_MAP_READ, original, 4, failure ? 1 : 0, failure ? &gate : nullptr, &second) == CL_SUCCESS);
  if ((command == "command-svm-unmap" || command == "command-svm-unmap-error"))
    assert(clEnqueueSVMUnmap(queue, original, failure ? 1 : 0, failure ? &gate : nullptr, &second) == CL_SUCCESS);
  if (command == "command-marker") assert(clEnqueueMarkerWithWaitList(queue, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-barrier") assert(clEnqueueBarrierWithWaitList(queue, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-migrate") assert(clEnqueueMigrateMemObjects(queue, 1, &memory, 0, 0, nullptr, &second) == CL_SUCCESS);
  if (command == "command-empty-kernel") {
    const size_t zero = 0;
    assert(clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &zero, &one, 0, nullptr, &second) == CL_SUCCESS);
  }
  if (command == "command-blocking-rect-ooo") {
    // A blocking command waits for its own completion, not an unrelated
    // blocked predecessor on an out-of-order queue.
    assert(clEnqueueReadBufferRect(queue, other, CL_TRUE, origin, origin, region, 0, 0, 0, 0, &readback, 0, nullptr, &second)
           == CL_SUCCESS);
    assert(readback == 0 && server.launches() == 0);
  }
  if (command == "command-blocking-svm-ooo") {
    assert(clEnqueueSVMMemcpy(queue, CL_TRUE, replacement, original, 4, 0, nullptr, &second) == CL_SUCCESS);
    assert(*replacement == 0 && server.launches() == 0);
  }
  if (command == "command-intel-copy") {
    using Copy = cl_int(CL_API_CALL *)(cl_command_queue, cl_bool, void *, const void *, size_t, cl_uint, const cl_event *, cl_event *);
    auto copy = reinterpret_cast<Copy>(clGetExtensionFunctionAddressForPlatform(platform, "clEnqueueMemcpyINTEL"));
    assert(copy && copy(queue, CL_FALSE, replacement, original, 4, 0, nullptr, &second) == CL_SUCCESS);
  }
  if (command == "command-intel-fill") {
    using Fill = cl_int(CL_API_CALL *)(cl_command_queue, void *, const void *, size_t, size_t, cl_uint, const cl_event *, cl_event *);
    auto fill = reinterpret_cast<Fill>(clGetExtensionFunctionAddressForPlatform(platform, "clEnqueueMemFillINTEL"));
    assert(fill && fill(queue, replacement, &pattern, 4, 4, 0, nullptr, &second) == CL_SUCCESS);
  }
  if (command == "command-intel-advise") {
    using Advise = cl_int(CL_API_CALL *)(cl_command_queue, const void *, size_t, cl_uint, cl_uint, const cl_event *, cl_event *);
    auto advise = reinterpret_cast<Advise>(clGetExtensionFunctionAddressForPlatform(platform, "clEnqueueMemAdviseINTEL"));
    assert(advise && advise(queue, original, 4, 0, 0, nullptr, &second) == CL_SUCCESS);
  }
  if (command == "command-copy-release") {
    assert(clReleaseMemObject(memory) == CL_SUCCESS);
    assert(clReleaseMemObject(other) == CL_SUCCESS);
    memory = nullptr;
    other = nullptr;
    assert(destroyed.load() == 0);
  }
  if (command == "command-marker-ooo" || command == "command-barrier-ooo") {
    if (command == "command-marker-ooo") assert(clEnqueueMarkerWithWaitList(queue, 0, nullptr, &second) == CL_SUCCESS);
    else assert(clEnqueueBarrierWithWaitList(queue, 0, nullptr, &second) == CL_SUCCESS);
    assert(clEnqueueCopyBuffer(queue, memory, other, 0, 0, 4, 0, nullptr, &third) == CL_SUCCESS);
    if (command == "command-marker-ooo") {
      assert(clWaitForEvents(1, &third) == CL_SUCCESS);
      cl_int marker_status = CL_COMPLETE;
      assert(clGetEventInfo(second, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(marker_status), &marker_status, nullptr) == CL_SUCCESS);
      assert(marker_status > CL_COMPLETE);
    }
  }
  if (command.starts_with("command-")) {
    assert(second != nullptr && server.launches() == 0 && *replacement == 0 && readback == 0);
    // The API consumes these parameter values at enqueue, not at execution.
    region[0] = 1000;
    origin[0] = 1000;
    pattern = 99;
  }
  assert(clSetUserEventStatus(gate, failure ? -1 : CL_COMPLETE) == CL_SUCCESS);
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(3);
  cl_int status = CL_QUEUED;
  if (!no_event) do {
      assert(clGetEventInfo(first, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS);
      if (status <= CL_COMPLETE) break;
      std::this_thread::sleep_for(std::chrono::milliseconds(1));
    } while (std::chrono::steady_clock::now() < deadline);
  if (!no_event)
    assert(status == (failure ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : remote_failure ? CL_OUT_OF_RESOURCES : CL_COMPLETE));
  if (queue) assert(clFinish(queue) == (failure || remote_failure || no_event ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS));
  assert(server.launches() == (failure ? 0U : consecutive ? 2U : 1U));
  assert(*original == (failure || remote_failure || no_event ? 0U : 7U));
  const uint32_t expected_replacement = command == "command-copy" || command == "command-copy-release" || command == "command-copy-rect"
                                                || command == "command-svm-copy" || command == "command-intel-copy"
                                                || command == "command-barrier-ooo"
                                            ? 7
                                        : command == "command-fill" || command == "command-svm-fill" || command == "command-intel-fill" ? 13
                                        : command == "command-write-rect"                                                               ? 17
                                                                                                                                        : 0;
  assert(*replacement == expected_replacement);
  if (command == "command-read-rect") assert(readback == 7);
  if (command == "command-map" || command == "command-map-twice") {
    if (command == "command-map-twice") {
      cl_uint maps = 0;
      assert(clGetMemObjectInfo(memory, CL_MEM_MAP_COUNT, sizeof(maps), &maps, nullptr) == CL_SUCCESS && maps == 2);
      assert(clEnqueueUnmapMemObject(queue, memory, mapped, 0, nullptr, nullptr) == CL_SUCCESS);
    }
    assert(*static_cast<uint32_t *>(mapped) == 7);
    assert(clEnqueueUnmapMemObject(queue, memory, mapped, 0, nullptr, nullptr) == CL_SUCCESS);
    assert(clFinish(queue) == CL_SUCCESS);
  }
  if (command == "command-svm-map") {
    assert(clEnqueueSVMUnmap(queue, original, 0, nullptr, nullptr) == CL_SUCCESS);
    assert(clFinish(queue) == CL_SUCCESS);
  }
  if (command == "command-map-error") {
    cl_uint maps = 99;
    assert(clGetMemObjectInfo(memory, CL_MEM_MAP_COUNT, sizeof(maps), &maps, nullptr) == CL_SUCCESS && maps == 0);
  }
  if (command == "command-svm-map-error") {
    assert(clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ, original, 4, 0, nullptr, nullptr) == CL_SUCCESS);
    assert(clEnqueueSVMUnmap(queue, original, 0, nullptr, nullptr) == CL_SUCCESS);
  }
  if (command == "command-unmap-error") assert(clEnqueueUnmapMemObject(queue, memory, mapped, 0, nullptr, nullptr) == CL_SUCCESS);
  if (command == "command-svm-unmap-error") assert(clEnqueueSVMUnmap(queue, original, 0, nullptr, nullptr) == CL_SUCCESS);
  if (third) assert(clReleaseEvent(third) == CL_SUCCESS);
  if (transfer) assert(readback == 7);
  if (snapshot) assert(destroyed.load() == 1);
  if (second) assert(clReleaseEvent(second) == CL_SUCCESS);
  if (first) assert(clReleaseEvent(first) == CL_SUCCESS);
  assert(clReleaseEvent(gate) == CL_SUCCESS);
  if (memory) assert(clReleaseMemObject(memory) == CL_SUCCESS);
  if (other) assert(clReleaseMemObject(other) == CL_SUCCESS);
  if (kernel) assert(clReleaseKernel(kernel) == CL_SUCCESS);
  if (program) assert(clReleaseProgram(program) == CL_SUCCESS);
  if (queue) assert(clReleaseCommandQueue(queue) == CL_SUCCESS);
  clSVMFree(context, original);
  clSVMFree(context, replacement);
  assert(clReleaseContext(context) == CL_SUCCESS);
}
