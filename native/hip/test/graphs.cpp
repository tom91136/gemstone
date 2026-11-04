#include <atomic>
#include <cstddef>
#include <cstdint>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime_api.h>

#include "../../rpc/test/rpc_fixture.hpp"

namespace {

void *allocate(size_t size) {
  void *device = nullptr;
  REQUIRE((hipMalloc(&device, size) == hipSuccess));
  return device;
}

std::vector<uint8_t> readDevice(const void *device, size_t size) {
  std::vector<uint8_t> bytes(size);
  REQUIRE((hipMemcpy(bytes.data(), device, size, hipMemcpyDeviceToHost) == hipSuccess));
  return bytes;
}

hipMemsetParams fill(void *device, unsigned value, size_t size) {
  return hipMemsetParams{.dst = device, .elementSize = 1, .height = 1, .pitch = 0, .value = value, .width = size};
}

void countCall(void *userData) { ++*static_cast<std::atomic<int> *>(userData); }

} // namespace

TEST_CASE("HIP stream capture records async memset and copy without running them", "[hip][graph][capture]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  constexpr size_t size = 64;
  void *source = allocate(size);
  void *target = allocate(size);
  REQUIRE((hipMemset(source, 0, size) == hipSuccess));
  REQUIRE((hipMemset(target, 0, size) == hipSuccess));
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));

  REQUIRE((hipStreamBeginCapture(stream, hipStreamCaptureModeGlobal) == hipSuccess));
  hipStreamCaptureStatus status = hipStreamCaptureStatusNone;
  REQUIRE((hipStreamIsCapturing(stream, &status) == hipSuccess));
  CHECK((status == hipStreamCaptureStatusActive));
  REQUIRE((hipMemsetAsync(source, 0x11, size, stream) == hipSuccess));
  REQUIRE((hipMemcpyAsync(target, source, size, hipMemcpyDeviceToDevice, stream) == hipSuccess));

  hipGraph_t graph = nullptr;
  REQUIRE((hipStreamEndCapture(stream, &graph) == hipSuccess));
  REQUIRE((graph != nullptr));
  REQUIRE((hipStreamIsCapturing(stream, &status) == hipSuccess));
  CHECK((status == hipStreamCaptureStatusNone));
  CHECK((readDevice(source, size) == std::vector<uint8_t>(size, 0)));
  CHECK((readDevice(target, size) == std::vector<uint8_t>(size, 0)));

  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, graph, 0) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  CHECK((readDevice(target, size) == std::vector<uint8_t>(size, 0x11)));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
  REQUIRE((hipFree(source) == hipSuccess));
  REQUIRE((hipFree(target) == hipSuccess));
}

TEST_CASE("HIP stream capture queries write only the status unless capturing", "[hip][graph][capture]") {
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  hipStreamCaptureStatus status = hipStreamCaptureStatusActive;
  unsigned long long id = 7;
  hipGraph_t graph = reinterpret_cast<hipGraph_t>(&id);
  const hipGraphNode_t *dependencies = reinterpret_cast<const hipGraphNode_t *>(&id);
  size_t count = 9;
  REQUIRE((hipStreamGetCaptureInfo_v2(stream, &status, &id, &graph, &dependencies, &count) == hipSuccess));
  CHECK((status == hipStreamCaptureStatusNone));
  CHECK((id == 7));
  CHECK((graph == reinterpret_cast<hipGraph_t>(&id)));
  CHECK((dependencies == reinterpret_cast<const hipGraphNode_t *>(&id)));
  CHECK((count == 9));

  REQUIRE((hipStreamBeginCapture(stream, hipStreamCaptureModeGlobal) == hipSuccess));
  REQUIRE((hipStreamGetCaptureInfo_v2(stream, &status, &id, &graph, &dependencies, &count) == hipSuccess));
  CHECK((status == hipStreamCaptureStatusActive));
  CHECK((id != 7));
  CHECK((graph != reinterpret_cast<hipGraph_t>(&id)));
  CHECK((count == 0));
  hipGraph_t captured = nullptr;
  REQUIRE((hipStreamEndCapture(stream, &captured) == hipSuccess));
  CHECK((captured == graph));
  REQUIRE((hipGraphDestroy(captured) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
}

TEST_CASE("HIP stream capture forks and joins through events", "[hip][graph][capture]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  constexpr size_t size = 32;
  void *firstBuffer = allocate(size);
  void *secondBuffer = allocate(size);
  void *thirdBuffer = allocate(size);
  REQUIRE((hipMemset(thirdBuffer, 0, size) == hipSuccess));
  hipStream_t first = nullptr;
  hipStream_t second = nullptr;
  REQUIRE((hipStreamCreate(&first) == hipSuccess));
  REQUIRE((hipStreamCreate(&second) == hipSuccess));
  hipEvent_t fork = nullptr;
  hipEvent_t join = nullptr;
  REQUIRE((hipEventCreate(&fork) == hipSuccess));
  REQUIRE((hipEventCreate(&join) == hipSuccess));

  REQUIRE((hipStreamBeginCapture(first, hipStreamCaptureModeGlobal) == hipSuccess));
  REQUIRE((hipMemsetAsync(firstBuffer, 0x10, size, first) == hipSuccess));
  REQUIRE((hipEventRecord(fork, first) == hipSuccess));
  REQUIRE((hipStreamWaitEvent(second, fork, 0) == hipSuccess));
  hipStreamCaptureStatus status = hipStreamCaptureStatusNone;
  REQUIRE((hipStreamIsCapturing(second, &status) == hipSuccess));
  CHECK((status == hipStreamCaptureStatusActive));
  REQUIRE((hipMemsetAsync(secondBuffer, 0x20, size, second) == hipSuccess));
  REQUIRE((hipEventRecord(join, second) == hipSuccess));
  REQUIRE((hipStreamWaitEvent(first, join, 0) == hipSuccess));
  REQUIRE((hipMemcpyAsync(thirdBuffer, firstBuffer, size, hipMemcpyDeviceToDevice, first) == hipSuccess));

  hipGraph_t graph = nullptr;
  REQUIRE((hipStreamEndCapture(first, &graph) == hipSuccess));
  REQUIRE((hipStreamIsCapturing(second, &status) == hipSuccess));
  CHECK((status == hipStreamCaptureStatusNone));

  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, graph, 0) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, first) == hipSuccess));
  REQUIRE((hipStreamSynchronize(first) == hipSuccess));
  CHECK((readDevice(firstBuffer, size) == std::vector<uint8_t>(size, 0x10)));
  CHECK((readDevice(secondBuffer, size) == std::vector<uint8_t>(size, 0x20)));
  CHECK((readDevice(thirdBuffer, size) == std::vector<uint8_t>(size, 0x10)));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipEventDestroy(fork) == hipSuccess));
  REQUIRE((hipEventDestroy(join) == hipSuccess));
  REQUIRE((hipStreamDestroy(first) == hipSuccess));
  REQUIRE((hipStreamDestroy(second) == hipSuccess));
  REQUIRE((hipFree(firstBuffer) == hipSuccess));
  REQUIRE((hipFree(secondBuffer) == hipSuccess));
  REQUIRE((hipFree(thirdBuffer) == hipSuccess));
}

TEST_CASE("HIP stream capture records host functions", "[hip][graph][capture][host]") {
  std::atomic<int> calls = 0;
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipStreamBeginCapture(stream, hipStreamCaptureModeGlobal) == hipSuccess));
  REQUIRE((hipLaunchHostFunc(stream, countCall, &calls) == hipSuccess));
  CHECK((calls.load() == 0));
  hipGraph_t graph = nullptr;
  REQUIRE((hipStreamEndCapture(stream, &graph) == hipSuccess));
  size_t nodes = 0;
  REQUIRE((hipGraphGetNodes(graph, nullptr, &nodes) == hipSuccess));
  CHECK((nodes == 1));

  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, graph, 0) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  CHECK((calls.load() == 2));
  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
}

TEST_CASE("HIP explicit graph runs nodes in dependency order", "[hip][graph][explicit]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  constexpr size_t size = 48;
  void *source = allocate(size);
  void *target = allocate(size);
  REQUIRE((hipMemset(source, 0, size) == hipSuccess));
  REQUIRE((hipMemset(target, 0, size) == hipSuccess));

  hipGraph_t graph = nullptr;
  REQUIRE((hipGraphCreate(&graph, 0) == hipSuccess));
  hipGraphNode_t nodes[3] = {};
  const auto firstSet = fill(source, 0x22, size);
  REQUIRE((hipGraphAddMemsetNode(&nodes[0], graph, nullptr, 0, &firstSet) == hipSuccess));
  REQUIRE((hipGraphAddMemcpyNode1D(&nodes[1], graph, &nodes[0], 1, target, source, size, hipMemcpyDeviceToDevice) == hipSuccess));
  const auto secondSet = fill(source, 0x44, size);
  REQUIRE((hipGraphAddMemsetNode(&nodes[2], graph, &nodes[1], 1, &secondSet) == hipSuccess));

  hipGraphNode_t roots[4] = {};
  size_t rootCount = 4;
  REQUIRE((hipGraphGetRootNodes(graph, roots, &rootCount) == hipSuccess));
  CHECK((rootCount == 1));
  CHECK((roots[0] == nodes[0]));

  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, graph, 0) == hipSuccess));
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  CHECK((readDevice(source, size) == std::vector<uint8_t>(size, 0x44)));
  CHECK((readDevice(target, size) == std::vector<uint8_t>(size, 0x22)));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
  REQUIRE((hipFree(source) == hipSuccess));
  REQUIRE((hipFree(target) == hipSuccess));
}

TEST_CASE("HIP memset nodes honour element size and ignore pitch for one row", "[hip][graph][memset]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  constexpr size_t size = 32;
  void *buffer = allocate(size);
  REQUIRE((hipMemset(buffer, 0, size) == hipSuccess));

  hipGraph_t graph = nullptr;
  REQUIRE((hipGraphCreate(&graph, 0) == hipSuccess));
  hipGraphNode_t wide = nullptr;
  const hipMemsetParams word{.dst = buffer, .elementSize = 4, .height = 1, .pitch = 0, .value = 0x01020304U, .width = size / 4};
  REQUIRE((hipGraphAddMemsetNode(&wide, graph, nullptr, 0, &word) == hipSuccess));
  hipGraphNode_t half = nullptr;
  const hipMemsetParams halves{
      .dst = static_cast<uint8_t *>(buffer) + 16, .elementSize = 2, .height = 1, .pitch = 0, .value = 0xA1B2U, .width = 4};
  REQUIRE((hipGraphAddMemsetNode(&half, graph, &wide, 1, &halves) == hipSuccess));
  hipGraphNode_t rows = nullptr;
  const hipMemsetParams tall{
      .dst = static_cast<uint8_t *>(buffer) + 24, .elementSize = 1, .height = 2, .pitch = 4, .value = 0x7F, .width = 2};
  REQUIRE((hipGraphAddMemsetNode(&rows, graph, &half, 1, &tall) == hipSuccess));
  hipMemsetParams invalid = word;
  invalid.elementSize = 3;
  hipGraphNode_t rejected = nullptr;
  CHECK((hipGraphAddMemsetNode(&rejected, graph, nullptr, 0, &invalid) == hipErrorInvalidValue));

  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, graph, 0) == hipSuccess));
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  const std::vector<uint8_t> expected = {0x04, 0x03, 0x02, 0x01, 0x04, 0x03, 0x02, 0x01, 0x04, 0x03, 0x02, 0x01, 0x04, 0x03, 0x02, 0x01,
                                         0xB2, 0xA1, 0xB2, 0xA1, 0xB2, 0xA1, 0xB2, 0xA1, 0x7F, 0x7F, 0x02, 0x01, 0x7F, 0x7F, 0x02, 0x01};
  CHECK((readDevice(buffer, size) == expected));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
  REQUIRE((hipFree(buffer) == hipSuccess));
}

TEST_CASE("HIP memcpy nodes accept a zero pitch for a single row", "[hip][graph][memcpy]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  constexpr size_t size = 24;
  void *source = allocate(size);
  void *target = allocate(size);
  REQUIRE((hipMemset(source, 0x5A, size) == hipSuccess));
  REQUIRE((hipMemset(target, 0, size) == hipSuccess));

  hipMemcpy3DParms copy{};
  copy.srcPtr = hipPitchedPtr{.ptr = source, .pitch = 0, .xsize = size, .ysize = 1};
  copy.dstPtr = hipPitchedPtr{.ptr = target, .pitch = 0, .xsize = size, .ysize = 1};
  copy.extent = hipExtent{.width = size, .height = 1, .depth = 1};
  copy.kind = hipMemcpyDeviceToDevice;
  hipGraph_t graph = nullptr;
  REQUIRE((hipGraphCreate(&graph, 0) == hipSuccess));
  hipGraphNode_t node = nullptr;
  REQUIRE((hipGraphAddMemcpyNode(&node, graph, nullptr, 0, &copy) == hipSuccess));

  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, graph, 0) == hipSuccess));
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  CHECK((readDevice(target, size) == std::vector<uint8_t>(size, 0x5A)));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
  REQUIRE((hipFree(source) == hipSuccess));
  REQUIRE((hipFree(target) == hipSuccess));
}

TEST_CASE("HIP graph dependencies reject cycles, self edges and foreign nodes", "[hip][graph][dependencies]") {
  hipGraph_t graph = nullptr;
  hipGraph_t other = nullptr;
  REQUIRE((hipGraphCreate(&graph, 0) == hipSuccess));
  REQUIRE((hipGraphCreate(&other, 0) == hipSuccess));
  hipGraphNode_t first = nullptr;
  hipGraphNode_t second = nullptr;
  hipGraphNode_t foreign = nullptr;
  REQUIRE((hipGraphAddEmptyNode(&first, graph, nullptr, 0) == hipSuccess));
  REQUIRE((hipGraphAddEmptyNode(&second, graph, nullptr, 0) == hipSuccess));
  REQUIRE((hipGraphAddEmptyNode(&foreign, other, nullptr, 0) == hipSuccess));

  REQUIRE((hipGraphAddDependencies(graph, &first, &second, 1) == hipSuccess));
  CHECK((hipGraphAddDependencies(graph, &second, &first, 1) == hipErrorInvalidValue));
  CHECK((hipGraphAddDependencies(graph, &first, &first, 1) == hipErrorInvalidValue));
  CHECK((hipGraphAddDependencies(graph, &foreign, &second, 1) == hipErrorInvalidHandle));
  CHECK((hipGraphAddEmptyNode(&foreign, graph, &foreign, 1) == hipErrorInvalidHandle));

  hipGraphNode_t roots[4] = {};
  size_t rootCount = 4;
  REQUIRE((hipGraphGetRootNodes(graph, roots, &rootCount) == hipSuccess));
  CHECK((rootCount == 1));
  CHECK((roots[0] == first));
  size_t nodeCount = 0;
  REQUIRE((hipGraphGetNodes(graph, nullptr, &nodeCount) == hipSuccess));
  CHECK((nodeCount == 2));

  REQUIRE((hipGraphDestroy(other) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
}

TEST_CASE("HIP host node runs its callback on each launch", "[hip][graph][host]") {
  std::atomic<int> calls = 0;
  hipHostNodeParams params{};
  params.fn = countCall;
  params.userData = &calls;

  hipGraph_t graph = nullptr;
  REQUIRE((hipGraphCreate(&graph, 0) == hipSuccess));
  hipGraphNode_t node = nullptr;
  REQUIRE((hipGraphAddHostNode(&node, graph, nullptr, 0, &params) == hipSuccess));
  const hipHostNodeParams empty{};
  CHECK((hipGraphAddHostNode(&node, graph, nullptr, 0, &empty) == hipErrorInvalidValue));

  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, graph, 0) == hipSuccess));
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  CHECK((calls.load() == 2));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
}

TEST_CASE("HIP graph exec update accepts same topology and rejects a changed one", "[hip][graph][update]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  constexpr size_t size = 16;
  void *buffer = allocate(size);
  REQUIRE((hipMemset(buffer, 0, size) == hipSuccess));

  hipGraph_t graph = nullptr;
  REQUIRE((hipGraphCreate(&graph, 0) == hipSuccess));
  hipGraphNode_t node = nullptr;
  const auto initial = fill(buffer, 0x31, size);
  REQUIRE((hipGraphAddMemsetNode(&node, graph, nullptr, 0, &initial) == hipSuccess));
  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, graph, 0) == hipSuccess));

  hipGraph_t updated = nullptr;
  REQUIRE((hipGraphCreate(&updated, 0) == hipSuccess));
  const auto replacement = fill(buffer, 0x52, size);
  REQUIRE((hipGraphAddMemsetNode(&node, updated, nullptr, 0, &replacement) == hipSuccess));
  hipGraphNode_t errorNode = reinterpret_cast<hipGraphNode_t>(buffer);
  hipGraphExecUpdateResult result = hipGraphExecUpdateError;
  REQUIRE((hipGraphExecUpdate(exec, updated, &errorNode, &result) == hipSuccess));
  CHECK((result == hipGraphExecUpdateSuccess));

  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  CHECK((readDevice(buffer, size) == std::vector<uint8_t>(size, 0x52)));

  hipGraph_t reshaped = nullptr;
  REQUIRE((hipGraphCreate(&reshaped, 0) == hipSuccess));
  hipGraphNode_t extra = nullptr;
  REQUIRE((hipGraphAddEmptyNode(&extra, reshaped, nullptr, 0) == hipSuccess));
  REQUIRE((hipGraphAddMemsetNode(&node, reshaped, nullptr, 0, &replacement) == hipSuccess));
  errorNode = extra;
  CHECK((hipGraphExecUpdate(exec, reshaped, &errorNode, &result) == hipErrorGraphExecUpdateFailure));
  CHECK((result == hipGraphExecUpdateErrorTopologyChanged));
  CHECK((errorNode == nullptr));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipGraphDestroy(updated) == hipSuccess));
  REQUIRE((hipGraphDestroy(reshaped) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
  REQUIRE((hipFree(buffer) == hipSuccess));
}

TEST_CASE("HIP capture misuse and invalid arguments return errors", "[hip][graph][capture][errors]") {
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  hipGraph_t graph = nullptr;

  CHECK((hipStreamBeginCapture(stream, static_cast<hipStreamCaptureMode>(99)) == hipErrorInvalidValue));
  CHECK((hipStreamBeginCapture(hipStreamLegacy, hipStreamCaptureModeGlobal) == hipErrorStreamCaptureUnsupported));
  CHECK((hipStreamBeginCapture(nullptr, hipStreamCaptureModeGlobal) == hipErrorStreamCaptureUnsupported));
  CHECK((hipStreamEndCapture(stream, &graph) == hipErrorInvalidValue));

  REQUIRE((hipStreamBeginCapture(stream, hipStreamCaptureModeThreadLocal) == hipSuccess));
  CHECK((hipStreamBeginCapture(stream, hipStreamCaptureModeGlobal) == hipErrorStreamCaptureUnsupported));
  CHECK((hipStreamEndCapture(stream, nullptr) == hipErrorInvalidValue));
  REQUIRE((hipStreamEndCapture(stream, &graph) == hipSuccess));
  REQUIRE((graph != nullptr));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));

  graph = nullptr;
  REQUIRE((hipStreamBeginCapture(stream, hipStreamCaptureModeRelaxed) == hipSuccess));
  REQUIRE((hipStreamEndCapture(stream, &graph) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));

  CHECK((hipGraphInstantiateWithFlags(nullptr, nullptr, 0) == hipErrorInvalidValue));
  hipGraph_t empty = nullptr;
  REQUIRE((hipGraphCreate(&empty, 0) == hipSuccess));
  hipGraphExec_t exec = nullptr;
  CHECK((hipGraphInstantiateWithFlags(&exec, empty, hipGraphInstantiateFlagDeviceLaunch) == hipErrorNotSupported));
  hipGraphNode_t kernel = nullptr;
  const hipKernelNodeParams noKernel{};
  CHECK((hipGraphAddKernelNode(&kernel, empty, nullptr, 0, &noKernel) == hipErrorInvalidDeviceFunction));
  REQUIRE((hipGraphDestroy(empty) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
}

TEST_CASE("HIP graph clone copies nodes and dependencies independently of the original", "[hip][graph][clone]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  constexpr size_t size = 32;
  void *source = allocate(size);
  void *target = allocate(size);
  REQUIRE((hipMemset(source, 0, size) == hipSuccess));
  REQUIRE((hipMemset(target, 0, size) == hipSuccess));

  hipGraph_t graph = nullptr;
  REQUIRE((hipGraphCreate(&graph, 0) == hipSuccess));
  hipGraphNode_t nodes[3] = {};
  const auto firstSet = fill(source, 0x33, size);
  REQUIRE((hipGraphAddMemsetNode(&nodes[0], graph, nullptr, 0, &firstSet) == hipSuccess));
  REQUIRE((hipGraphAddMemcpyNode1D(&nodes[1], graph, &nodes[0], 1, target, source, size, hipMemcpyDeviceToDevice) == hipSuccess));
  REQUIRE((hipGraphAddEmptyNode(&nodes[2], graph, &nodes[1], 1) == hipSuccess));

  hipGraph_t clone = nullptr;
  CHECK((hipGraphClone(nullptr, graph) == hipErrorInvalidValue));
  CHECK((hipGraphClone(&clone, nullptr) == hipErrorInvalidValue));
  REQUIRE((hipGraphClone(&clone, graph) == hipSuccess));
  REQUIRE((clone != nullptr));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));

  hipGraphNode_t roots[4] = {};
  size_t rootCount = 4;
  REQUIRE((hipGraphGetRootNodes(clone, roots, &rootCount) == hipSuccess));
  CHECK((rootCount == 1));

  hipGraphExec_t exec = nullptr;
  REQUIRE((hipGraphInstantiateWithFlags(&exec, clone, 0) == hipSuccess));
  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  CHECK((readDevice(target, size) == std::vector<uint8_t>(size, 0x33)));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(clone) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
  REQUIRE((hipFree(source) == hipSuccess));
  REQUIRE((hipFree(target) == hipSuccess));
}

TEST_CASE("HIP legacy instantiate forwards to graph instantiation", "[hip][graph][instantiate]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  constexpr size_t size = 16;
  void *target = allocate(size);
  REQUIRE((hipMemset(target, 0, size) == hipSuccess));

  hipGraph_t graph = nullptr;
  REQUIRE((hipGraphCreate(&graph, 0) == hipSuccess));
  hipGraphNode_t node = nullptr;
  const auto set = fill(target, 0x55, size);
  REQUIRE((hipGraphAddMemsetNode(&node, graph, nullptr, 0, &set) == hipSuccess));

  char log[64] = {};
  hipGraphNode_t errorNode = nullptr;
  hipGraphExec_t exec = nullptr;
  CHECK((hipGraphInstantiate(nullptr, nullptr, &errorNode, log, sizeof log) == hipErrorInvalidValue));
  CHECK((log[0] == '\0'));
  CHECK((errorNode == nullptr));
  REQUIRE((hipGraphInstantiate(&exec, graph, &errorNode, log, sizeof log) == hipSuccess));

  hipStream_t stream = nullptr;
  REQUIRE((hipStreamCreate(&stream) == hipSuccess));
  REQUIRE((hipGraphLaunch(exec, stream) == hipSuccess));
  REQUIRE((hipStreamSynchronize(stream) == hipSuccess));
  CHECK((readDevice(target, size) == std::vector<uint8_t>(size, 0x55)));

  REQUIRE((hipGraphExecDestroy(exec) == hipSuccess));
  REQUIRE((hipGraphDestroy(graph) == hipSuccess));
  REQUIRE((hipStreamDestroy(stream) == hipSuccess));
  REQUIRE((hipFree(target) == hipSuccess));
}
