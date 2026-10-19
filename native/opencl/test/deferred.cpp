#include <atomic>
#include <chrono>
#include <cstdint>
#include <cstdlib>
#include <optional>
#include <string>
#include <thread>

#include <CL/cl.h>
#include <catch2/catch_test_macros.hpp>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include "../../rpc/test/rpc_fixture.hpp"

namespace {

uint32_t expectedReplacement(const std::string &command) {
  if (command == "command-copy" || command == "command-copy-release" || command == "command-copy-rect" || command == "command-svm-copy"
      || command == "command-intel-copy" || command == "command-barrier-ooo")
    return 7;
  if (command == "command-fill" || command == "command-svm-fill" || command == "command-intel-fill") return 13;
  if (command == "command-write-rect") return 17;
  return 0;
}

struct RemoteFailure {
  gemstone::execution::FailureKind kind;
  cl_int status;
};

std::optional<RemoteFailure> remoteFailureOf(const std::string &command) {
  using gemstone::execution::FailureKind;
  if (command == "remote-error") return RemoteFailure{.kind = FailureKind::Failed, .status = CL_OUT_OF_RESOURCES};
  if (command == "remote-illegal-address") return RemoteFailure{.kind = FailureKind::IllegalAddress, .status = CL_OUT_OF_RESOURCES};
  if (command == "remote-invalid-program")
    return RemoteFailure{.kind = FailureKind::InvalidProgram, .status = CL_INVALID_PROGRAM_EXECUTABLE};
  if (command == "remote-invalid-configuration")
    return RemoteFailure{.kind = FailureKind::InvalidConfiguration, .status = CL_INVALID_WORK_GROUP_SIZE};
  return std::nullopt;
}

} // namespace

static void deferred(const std::string &command) {
  gemstone_test::RpcFixture server;
  server.programCatalogue({{.name = "groups", .linkageName = "groups"}}, {});
  const gemstone_test::RpcEnvironment environment(server);
  cl_platform_id platform = nullptr;
  cl_device_id device = nullptr;
  REQUIRE((clGetPlatformIDs(1, &platform, nullptr) == CL_SUCCESS));
  REQUIRE((clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device, nullptr) == CL_SUCCESS));
  cl_int error = 0;
  auto *context = clCreateContext(nullptr, 1, &device, nullptr, nullptr, &error);
  REQUIRE((context && error == CL_SUCCESS));
  const bool outOfOrder = command == "out-of-order" || command == "command-marker-ooo" || command == "command-barrier-ooo"
                          || command == "command-blocking-rect-ooo" || command == "command-blocking-svm-ooo";
  const cl_queue_properties properties[] = {CL_QUEUE_PROPERTIES, CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE, 0};
  auto *queue = clCreateCommandQueueWithProperties(context, device, outOfOrder ? properties : nullptr, &error);
  REQUIRE((queue && error == CL_SUCCESS));
  auto *program = clCreateProgramWithIL(context, gemstone_test::RpcFixture::Spirv, sizeof(gemstone_test::RpcFixture::Spirv), &error);
  REQUIRE((program && error == CL_SUCCESS));
  REQUIRE((clBuildProgram(program, 1, &device, nullptr, nullptr, nullptr) == CL_SUCCESS));
  auto *kernel = clCreateKernel(program, "groups", &error);
  REQUIRE((kernel && error == CL_SUCCESS));
  auto *original = static_cast<uint32_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, 4, 8));
  auto *replacement = static_cast<uint32_t *>(clSVMAlloc(context, CL_MEM_READ_WRITE, 4, 8));
  REQUIRE((original && replacement));
  *original = 0;
  *replacement = 0;
  std::atomic_uint destroyed{0};
  auto *memory = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 4, original, &error);
  REQUIRE((memory && error == CL_SUCCESS));
  REQUIRE((clSetMemObjectDestructorCallback(
               memory, [](cl_mem, void *data) { static_cast<std::atomic_uint *>(data)->fetch_add(1); }, &destroyed)
           == CL_SUCCESS));
  auto *other = clCreateBuffer(context, CL_MEM_USE_HOST_PTR, 4, replacement, &error);
  REQUIRE((other && error == CL_SUCCESS));
  void *mapped = nullptr;
  if ((command == "command-unmap" || command == "command-unmap-error")) {
    mapped = clEnqueueMapBuffer(queue, memory, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, 0, 4, 0, nullptr, nullptr, &error);
    REQUIRE((mapped && error == CL_SUCCESS));
  }
  if ((command == "command-svm-unmap" || command == "command-svm-unmap-error"))
    REQUIRE((clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ | CL_MAP_WRITE, original, 4, 0, nullptr, nullptr) == CL_SUCCESS));
  auto *gate = clCreateUserEvent(context, &error);
  REQUIRE((gate && error == CL_SUCCESS));
  const auto remote = remoteFailureOf(command);
  const bool remoteFailure = remote.has_value();
  const bool noEvent = command == "error-no-event";
  const bool teardown = command == "teardown";
  if (remoteFailure || noEvent) server.failNextCompletion(remote ? remote->kind : gemstone::execution::FailureKind::Failed);
  uint32_t seed = 7;
  REQUIRE((clSetKernelArg(kernel, 0, sizeof(memory), &memory) == CL_SUCCESS));
  REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
  server.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 7});
  const size_t one = 1;
  cl_event first = nullptr;
  cl_event second = nullptr;
  cl_event third = nullptr;
  if (command == "validation") {
    const size_t tooLarge = size_t{UINT32_MAX} + 1;
    REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &tooLarge, &one, 1, &gate, &first) == CL_INVALID_GLOBAL_WORK_SIZE));
    REQUIRE((!first));
  }
  REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &one, &one, 1, &gate, noEvent ? nullptr : &first) == CL_SUCCESS));
  if (teardown) {
    REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
    queue = nullptr;
  }
  REQUIRE((server.launches.load() == 0));
  const bool snapshot = command == "snapshot";
  const bool consecutive = command == "consecutive" || command == "out-of-order";
  const bool transfer = command == "transfer";
  uint32_t readback = 0;
  const bool waitedFailure = command == "waited-error";
  const bool failure = command == "dependency-error" || waitedFailure || command.ends_with("-error");
  if (snapshot) {
    seed = 99;
    REQUIRE((clSetKernelArg(kernel, 0, sizeof(other), &other) == CL_SUCCESS));
    REQUIRE((clSetKernelArg(kernel, 1, sizeof(seed), &seed) == CL_SUCCESS));
    REQUIRE((clReleaseMemObject(memory) == CL_SUCCESS));
    memory = nullptr;
    REQUIRE((destroyed.load() == 0));
    REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
    kernel = nullptr;
    REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
    program = nullptr;
  }
  if (consecutive) {
    // This must return before the user signals the preceding command's gate.
    REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &one, &one, 0, nullptr, &second) == CL_SUCCESS));
    if (outOfOrder) {
      const auto until = std::chrono::steady_clock::now() + std::chrono::seconds(3);
      cl_int secondStatus = CL_QUEUED;
      do {
        REQUIRE((clGetEventInfo(second, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(secondStatus), &secondStatus, nullptr) == CL_SUCCESS));
        if (secondStatus == CL_COMPLETE) break;
        std::this_thread::sleep_for(std::chrono::milliseconds(1));
      } while (std::chrono::steady_clock::now() < until);
      REQUIRE((secondStatus == CL_COMPLETE && server.launches.load() == 1));
    } else {
      REQUIRE((server.launches.load() == 0));
    }
  }
  if (transfer) {
    REQUIRE((clEnqueueReadBuffer(queue, memory, CL_FALSE, 0, 4, &readback, 0, nullptr, &second) == CL_SUCCESS));
    REQUIRE((readback == 0 && server.launches.load() == 0));
  }
  size_t origin[3]{0, 0, 0};
  size_t region[3]{4, 1, 1};
  uint32_t pattern = 13;
  uint32_t input = 17;
  if (command == "command-copy" || command == "command-copy-release")
    REQUIRE((clEnqueueCopyBuffer(queue, memory, other, 0, 0, 4, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-fill") REQUIRE((clEnqueueFillBuffer(queue, other, &pattern, 4, 0, 4, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-read-rect")
    REQUIRE((clEnqueueReadBufferRect(queue, memory, CL_FALSE, origin, origin, region, 0, 0, 0, 0, &readback, 0, nullptr, &second)
             == CL_SUCCESS));
  if (command == "command-write-rect")
    REQUIRE(
        (clEnqueueWriteBufferRect(queue, other, CL_FALSE, origin, origin, region, 0, 0, 0, 0, &input, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-copy-rect")
    REQUIRE((clEnqueueCopyBufferRect(queue, memory, other, origin, origin, region, 0, 0, 0, 0, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-map" || command == "command-map-error" || command == "command-map-twice") {
    mapped = clEnqueueMapBuffer(queue, memory, CL_FALSE, CL_MAP_READ, 0, 4, failure ? 1 : 0, failure ? &gate : nullptr, &second, &error);
    REQUIRE((mapped && error == CL_SUCCESS));
    if (command == "command-map-twice") {
      auto *again = clEnqueueMapBuffer(queue, memory, CL_FALSE, CL_MAP_READ, 0, 4, 0, nullptr, &third, &error);
      REQUIRE((again == mapped && error == CL_SUCCESS));
    }
  }
  if ((command == "command-unmap" || command == "command-unmap-error"))
    REQUIRE((clEnqueueUnmapMemObject(queue, memory, mapped, failure ? 1 : 0, failure ? &gate : nullptr, &second) == CL_SUCCESS));
  if (command == "command-svm-copy")
    REQUIRE((clEnqueueSVMMemcpy(queue, CL_FALSE, replacement, original, 4, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-svm-fill") REQUIRE((clEnqueueSVMMemFill(queue, replacement, &pattern, 4, 4, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-svm-map" || command == "command-svm-map-error")
    REQUIRE(
        (clEnqueueSVMMap(queue, CL_FALSE, CL_MAP_READ, original, 4, failure ? 1 : 0, failure ? &gate : nullptr, &second) == CL_SUCCESS));
  if ((command == "command-svm-unmap" || command == "command-svm-unmap-error"))
    REQUIRE((clEnqueueSVMUnmap(queue, original, failure ? 1 : 0, failure ? &gate : nullptr, &second) == CL_SUCCESS));
  if (command == "command-marker") REQUIRE((clEnqueueMarkerWithWaitList(queue, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-barrier") REQUIRE((clEnqueueBarrierWithWaitList(queue, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-migrate") REQUIRE((clEnqueueMigrateMemObjects(queue, 1, &memory, 0, 0, nullptr, &second) == CL_SUCCESS));
  if (command == "command-empty-kernel") {
    const size_t zero = 0;
    REQUIRE((clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &zero, &one, 0, nullptr, &second) == CL_SUCCESS));
  }
  if (command == "command-blocking-rect-ooo") {
    // A blocking command waits for its own completion, not an unrelated
    // blocked predecessor on an out-of-order queue.
    REQUIRE(
        (clEnqueueReadBufferRect(queue, other, CL_TRUE, origin, origin, region, 0, 0, 0, 0, &readback, 0, nullptr, &second) == CL_SUCCESS));
    REQUIRE((readback == 0 && server.launches.load() == 0));
  }
  if (command == "command-blocking-svm-ooo") {
    REQUIRE((clEnqueueSVMMemcpy(queue, CL_TRUE, replacement, original, 4, 0, nullptr, &second) == CL_SUCCESS));
    REQUIRE((*replacement == 0 && server.launches.load() == 0));
  }
  if (command == "command-intel-copy") {
    using Copy = cl_int(CL_API_CALL *)(cl_command_queue, cl_bool, void *, const void *, size_t, cl_uint, const cl_event *, cl_event *);
    auto copy = reinterpret_cast<Copy>(clGetExtensionFunctionAddressForPlatform(platform, "clEnqueueMemcpyINTEL"));
    REQUIRE((copy && copy(queue, CL_FALSE, replacement, original, 4, 0, nullptr, &second) == CL_SUCCESS));
  }
  if (command == "command-intel-fill") {
    using Fill = cl_int(CL_API_CALL *)(cl_command_queue, void *, const void *, size_t, size_t, cl_uint, const cl_event *, cl_event *);
    auto fill = reinterpret_cast<Fill>(clGetExtensionFunctionAddressForPlatform(platform, "clEnqueueMemFillINTEL"));
    REQUIRE((fill && fill(queue, replacement, &pattern, 4, 4, 0, nullptr, &second) == CL_SUCCESS));
  }
  if (command == "command-intel-advise") {
    using Advise = cl_int(CL_API_CALL *)(cl_command_queue, const void *, size_t, cl_uint, cl_uint, const cl_event *, cl_event *);
    auto advise = reinterpret_cast<Advise>(clGetExtensionFunctionAddressForPlatform(platform, "clEnqueueMemAdviseINTEL"));
    REQUIRE((advise && advise(queue, original, 4, 0, 0, nullptr, &second) == CL_SUCCESS));
  }
  if (command == "command-copy-release") {
    REQUIRE((clReleaseMemObject(memory) == CL_SUCCESS));
    REQUIRE((clReleaseMemObject(other) == CL_SUCCESS));
    memory = nullptr;
    other = nullptr;
    REQUIRE((destroyed.load() == 0));
  }
  if (command == "command-marker-ooo" || command == "command-barrier-ooo") {
    if (command == "command-marker-ooo") REQUIRE((clEnqueueMarkerWithWaitList(queue, 0, nullptr, &second) == CL_SUCCESS));
    else REQUIRE((clEnqueueBarrierWithWaitList(queue, 0, nullptr, &second) == CL_SUCCESS));
    REQUIRE((clEnqueueCopyBuffer(queue, memory, other, 0, 0, 4, 0, nullptr, &third) == CL_SUCCESS));
    if (command == "command-marker-ooo") {
      REQUIRE((clWaitForEvents(1, &third) == CL_SUCCESS));
      cl_int markerStatus = CL_COMPLETE;
      REQUIRE((clGetEventInfo(second, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(markerStatus), &markerStatus, nullptr) == CL_SUCCESS));
      REQUIRE((markerStatus > CL_COMPLETE));
    }
  }
  if (command.starts_with("command-")) {
    REQUIRE((second && server.launches.load() == 0 && *replacement == 0 && readback == 0));
    // The API consumes these parameter values at enqueue, not at execution.
    region[0] = 1000;
    origin[0] = 1000;
    pattern = 99;
  }
  REQUIRE((clSetUserEventStatus(gate, failure ? -1 : CL_COMPLETE) == CL_SUCCESS));
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(3);
  cl_int status = CL_QUEUED;
  if (!noEvent) do {
      REQUIRE((clGetEventInfo(first, CL_EVENT_COMMAND_EXECUTION_STATUS, sizeof(status), &status, nullptr) == CL_SUCCESS));
      if (status <= CL_COMPLETE) break;
      std::this_thread::sleep_for(std::chrono::milliseconds(1));
    } while (std::chrono::steady_clock::now() < deadline);
  if (!noEvent)
    REQUIRE((status == (failure ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : remoteFailure ? remote->status : CL_COMPLETE)));
  if (waitedFailure) REQUIRE((clWaitForEvents(1, &first) == CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST));
  if (queue)
    REQUIRE((clFinish(queue)
             == ((failure && !waitedFailure) || remoteFailure || noEvent ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS)));
  REQUIRE((server.launches.load() == (failure ? 0U : consecutive ? 2U : 1U)));
  REQUIRE((*original == (failure || remoteFailure || noEvent ? 0U : 7U)));
  REQUIRE((*replacement == expectedReplacement(command)));
  if (command == "command-read-rect") REQUIRE((readback == 7));
  if (command == "command-map" || command == "command-map-twice") {
    if (command == "command-map-twice") {
      cl_uint maps = 0;
      REQUIRE((clGetMemObjectInfo(memory, CL_MEM_MAP_COUNT, sizeof(maps), &maps, nullptr) == CL_SUCCESS && maps == 2));
      REQUIRE((clEnqueueUnmapMemObject(queue, memory, mapped, 0, nullptr, nullptr) == CL_SUCCESS));
    }
    REQUIRE((*static_cast<uint32_t *>(mapped) == 7));
    REQUIRE((clEnqueueUnmapMemObject(queue, memory, mapped, 0, nullptr, nullptr) == CL_SUCCESS));
    REQUIRE((clFinish(queue) == CL_SUCCESS));
  }
  if (command == "command-svm-map") {
    REQUIRE((clEnqueueSVMUnmap(queue, original, 0, nullptr, nullptr) == CL_SUCCESS));
    REQUIRE((clFinish(queue) == CL_SUCCESS));
  }
  if (command == "command-map-error") {
    cl_uint maps = 99;
    REQUIRE((clGetMemObjectInfo(memory, CL_MEM_MAP_COUNT, sizeof(maps), &maps, nullptr) == CL_SUCCESS && maps == 0));
  }
  if (command == "command-svm-map-error") {
    REQUIRE((clEnqueueSVMMap(queue, CL_TRUE, CL_MAP_READ, original, 4, 0, nullptr, nullptr) == CL_SUCCESS));
    REQUIRE((clEnqueueSVMUnmap(queue, original, 0, nullptr, nullptr) == CL_SUCCESS));
  }
  if (command == "command-unmap-error") REQUIRE((clEnqueueUnmapMemObject(queue, memory, mapped, 0, nullptr, nullptr) == CL_SUCCESS));
  if (command == "command-svm-unmap-error") REQUIRE((clEnqueueSVMUnmap(queue, original, 0, nullptr, nullptr) == CL_SUCCESS));
  if (third) REQUIRE((clReleaseEvent(third) == CL_SUCCESS));
  if (transfer) REQUIRE((readback == 7));
  if (snapshot) REQUIRE((destroyed.load() == 1));
  if (second) REQUIRE((clReleaseEvent(second) == CL_SUCCESS));
  if (first) REQUIRE((clReleaseEvent(first) == CL_SUCCESS));
  REQUIRE((clReleaseEvent(gate) == CL_SUCCESS));
  if (memory) REQUIRE((clReleaseMemObject(memory) == CL_SUCCESS));
  if (other) REQUIRE((clReleaseMemObject(other) == CL_SUCCESS));
  if (kernel) REQUIRE((clReleaseKernel(kernel) == CL_SUCCESS));
  if (program) REQUIRE((clReleaseProgram(program) == CL_SUCCESS));
  if (queue) REQUIRE((clReleaseCommandQueue(queue) == CL_SUCCESS));
  clSVMFree(context, original);
  clSVMFree(context, replacement);
  REQUIRE((clReleaseContext(context) == CL_SUCCESS));
}

#define OPENCL_DEFERRED_CASE(name)                                                                                                         \
  TEST_CASE("OpenCL deferred execution: " name, "[opencl][deferred]") { deferred(name); }

OPENCL_DEFERRED_CASE("boundary")
OPENCL_DEFERRED_CASE("completion")
OPENCL_DEFERRED_CASE("snapshot")
OPENCL_DEFERRED_CASE("consecutive")
OPENCL_DEFERRED_CASE("dependency-error")
OPENCL_DEFERRED_CASE("waited-error")
OPENCL_DEFERRED_CASE("out-of-order")
OPENCL_DEFERRED_CASE("transfer")
OPENCL_DEFERRED_CASE("remote-error")
OPENCL_DEFERRED_CASE("remote-illegal-address")
OPENCL_DEFERRED_CASE("remote-invalid-program")
OPENCL_DEFERRED_CASE("remote-invalid-configuration")
OPENCL_DEFERRED_CASE("error-no-event")
OPENCL_DEFERRED_CASE("teardown")
OPENCL_DEFERRED_CASE("validation")
OPENCL_DEFERRED_CASE("command-copy")
OPENCL_DEFERRED_CASE("command-fill")
OPENCL_DEFERRED_CASE("command-read-rect")
OPENCL_DEFERRED_CASE("command-write-rect")
OPENCL_DEFERRED_CASE("command-copy-rect")
OPENCL_DEFERRED_CASE("command-map")
OPENCL_DEFERRED_CASE("command-unmap")
OPENCL_DEFERRED_CASE("command-svm-copy")
OPENCL_DEFERRED_CASE("command-svm-fill")
OPENCL_DEFERRED_CASE("command-svm-map")
OPENCL_DEFERRED_CASE("command-svm-unmap")
OPENCL_DEFERRED_CASE("command-marker")
OPENCL_DEFERRED_CASE("command-barrier")
OPENCL_DEFERRED_CASE("command-migrate")
OPENCL_DEFERRED_CASE("command-empty-kernel")
OPENCL_DEFERRED_CASE("command-marker-ooo")
OPENCL_DEFERRED_CASE("command-barrier-ooo")
OPENCL_DEFERRED_CASE("command-map-error")
OPENCL_DEFERRED_CASE("command-unmap-error")
OPENCL_DEFERRED_CASE("command-svm-map-error")
OPENCL_DEFERRED_CASE("command-svm-unmap-error")
OPENCL_DEFERRED_CASE("command-intel-copy")
OPENCL_DEFERRED_CASE("command-intel-fill")
OPENCL_DEFERRED_CASE("command-intel-advise")
OPENCL_DEFERRED_CASE("command-copy-release")
OPENCL_DEFERRED_CASE("command-blocking-rect-ooo")
OPENCL_DEFERRED_CASE("command-blocking-svm-ooo")
OPENCL_DEFERRED_CASE("command-map-twice")

#undef OPENCL_DEFERRED_CASE
