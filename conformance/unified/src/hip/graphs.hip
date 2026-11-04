#include <atomic>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "support.hpp"
#include <fmt/format.h>

using namespace hip_support;

namespace {

constexpr unsigned count = 64;
constexpr size_t bytes = count * sizeof(uint32_t);

__global__ void addOne(uint32_t *values, unsigned n) {
  const unsigned i = blockIdx.x * blockDim.x + threadIdx.x;
  if (i < n) values[i] += 1;
}

void bumpCounter(void *counter) { static_cast<std::atomic<int> *>(counter)->fetch_add(1); }

uint32_t *allocate() {
  uint32_t *device = nullptr;
  check(hipMalloc(&device, bytes), "allocate");
  check(hipMemset(device, 0, bytes), "clear");
  return device;
}

std::vector<uint32_t> download(const uint32_t *device) {
  std::vector<uint32_t> host(count, 0);
  check(hipMemcpy(host.data(), device, bytes, hipMemcpyDeviceToHost), "download");
  return host;
}

hipKernelNodeParams addOneParams(uint32_t *&values, unsigned &n, void **args) {
  args[0] = &values;
  args[1] = &n;
  hipKernelNodeParams params{};
  params.func = reinterpret_cast<void *>(addOne);
  params.gridDim = dim3(1, 1, 1);
  params.blockDim = dim3(count, 1, 1);
  params.kernelParams = args;
  return params;
}

hipMemsetParams fillParams(uint32_t *device, unsigned value) {
  hipMemsetParams params{};
  params.dst = device;
  params.value = value;
  params.elementSize = sizeof(uint32_t);
  params.width = count;
  params.height = 1;
  return params;
}

void expectEqual(const std::vector<uint32_t> &actual, const std::vector<uint32_t> &expected, std::string_view name) {
  for (size_t i = 0; i < expected.size(); ++i)
    if (actual[i] != expected[i])
      throw std::runtime_error(fmt::format("{} mismatch at {}: expected {:#x}, got {:#x}", name, i, expected[i], actual[i]));
}

} // namespace

TEST_CASE("runtime/hip/graphs/capture-replays-recorded-work", "[unified][runtime][async]") {
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  uint32_t *buffer = allocate();

  check(hipStreamBeginCapture(stream, hipStreamCaptureModeThreadLocal), "begin capture");
  hipStreamCaptureStatus status = hipStreamCaptureStatusNone;
  check(hipStreamIsCapturing(stream, &status), "is capturing");
  REQUIRE(status == hipStreamCaptureStatusActive);
  check(hipMemsetAsync(buffer, 0x11, bytes, stream), "captured memset");
  addOne<<<1, count, 0, stream>>>(buffer, count);
  hipGraph_t graph = nullptr;
  check(hipStreamEndCapture(stream, &graph), "end capture");
  check(hipStreamIsCapturing(stream, &status), "is capturing after end");
  REQUIRE(status == hipStreamCaptureStatusNone);
  REQUIRE(download(buffer) == std::vector<uint32_t>(count, 0));

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  check(hipGraphLaunch(exec, stream), "launch");
  check(hipStreamSynchronize(stream), "synchronize");
  expectEqual(download(buffer), std::vector<uint32_t>(count, 0x11111112u), "captured round trip");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(graph), "graph destroy");
  check(hipFree(buffer), "free");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/capture-fork-join-through-events", "[unified][runtime][async]") {
  hipStream_t origin = nullptr;
  hipStream_t branch = nullptr;
  check(hipStreamCreate(&origin), "origin stream");
  check(hipStreamCreate(&branch), "branch stream");
  hipEvent_t fork = nullptr;
  hipEvent_t join = nullptr;
  check(hipEventCreateWithFlags(&fork, hipEventDisableTiming), "fork event");
  check(hipEventCreateWithFlags(&join, hipEventDisableTiming), "join event");
  uint32_t *first = allocate();
  uint32_t *second = allocate();
  uint32_t *copied = allocate();

  check(hipStreamBeginCapture(origin, hipStreamCaptureModeGlobal), "begin capture");
  check(hipEventRecord(fork, origin), "fork record");
  check(hipStreamWaitEvent(branch, fork, 0), "fork wait");
  check(hipMemsetAsync(first, 0x10, bytes, origin), "first memset");
  check(hipMemsetAsync(second, 0x20, bytes, branch), "second memset");
  check(hipEventRecord(join, branch), "join record");
  check(hipStreamWaitEvent(origin, join, 0), "join wait");
  check(hipMemcpyAsync(copied, second, bytes, hipMemcpyDeviceToDevice, origin), "copy after join");
  hipGraph_t graph = nullptr;
  check(hipStreamEndCapture(origin, &graph), "end capture");

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  check(hipGraphLaunch(exec, origin), "launch");
  check(hipStreamSynchronize(origin), "synchronize");
  expectEqual(download(first), std::vector<uint32_t>(count, 0x10101010u), "origin branch");
  expectEqual(download(copied), std::vector<uint32_t>(count, 0x20202020u), "joined branch");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(graph), "graph destroy");
  check(hipFree(first), "free first");
  check(hipFree(second), "free second");
  check(hipFree(copied), "free copied");
  check(hipEventDestroy(fork), "fork destroy");
  check(hipEventDestroy(join), "join destroy");
  check(hipStreamDestroy(origin), "origin destroy");
  check(hipStreamDestroy(branch), "branch destroy");
}

TEST_CASE("runtime/hip/graphs/capture-info-reports-active-capture", "[unified][runtime][async]") {
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  uint32_t *buffer = allocate();
  hipStreamCaptureStatus status = hipStreamCaptureStatusActive;
  check(hipStreamGetCaptureInfo_v2(stream, &status, nullptr, nullptr, nullptr, nullptr), "idle info");
  REQUIRE(status == hipStreamCaptureStatusNone);

  check(hipStreamBeginCapture(stream, hipStreamCaptureModeThreadLocal), "begin capture");
  unsigned long long id = 0;
  hipGraph_t graph = nullptr;
  const hipGraphNode_t *dependencies = nullptr;
  size_t dependencyCount = 1;
  check(hipStreamGetCaptureInfo_v2(stream, &status, &id, &graph, &dependencies, &dependencyCount), "active info");
  REQUIRE(status == hipStreamCaptureStatusActive);
  REQUIRE(id != 0);
  REQUIRE(graph != nullptr);
  REQUIRE(dependencyCount == 0);

  check(hipMemsetAsync(buffer, 0x01, bytes, stream), "captured memset");
  check(hipStreamGetCaptureInfo_v2(stream, &status, nullptr, nullptr, &dependencies, &dependencyCount), "info after work");
  REQUIRE(dependencyCount == 1);
  hipGraph_t captured = nullptr;
  check(hipStreamEndCapture(stream, &captured), "end capture");
  REQUIRE(captured == graph);

  check(hipGraphDestroy(captured), "graph destroy");
  check(hipFree(buffer), "free");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/capture-records-host-function", "[unified][runtime][async]") {
  std::atomic<int> calls{0};
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  uint32_t *buffer = allocate();

  check(hipStreamBeginCapture(stream, hipStreamCaptureModeThreadLocal), "begin capture");
  check(hipLaunchHostFunc(stream, bumpCounter, &calls), "captured host function");
  check(hipMemsetAsync(buffer, 0x02, bytes, stream), "captured memset");
  hipGraph_t graph = nullptr;
  check(hipStreamEndCapture(stream, &graph), "end capture");
  REQUIRE(calls.load() == 0);

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  check(hipGraphLaunch(exec, stream), "first launch");
  check(hipGraphLaunch(exec, stream), "second launch");
  check(hipStreamSynchronize(stream), "synchronize");
  REQUIRE(calls.load() == 2);
  expectEqual(download(buffer), std::vector<uint32_t>(count, 0x02020202u), "memset after host function");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(graph), "graph destroy");
  check(hipFree(buffer), "free");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/explicit-kernel-dependency-chain", "[unified][runtime][async]") {
  uint32_t *source = allocate();
  uint32_t *target = allocate();
  hipGraph_t graph = nullptr;
  check(hipGraphCreate(&graph, 0), "graph create");

  hipMemsetParams fill = fillParams(source, 0x22222222u);
  hipGraphNode_t fillNode = nullptr;
  check(hipGraphAddMemsetNode(&fillNode, graph, nullptr, 0, &fill), "memset node");

  hipGraphNode_t copyNode = nullptr;
  check(hipGraphAddMemcpyNode1D(&copyNode, graph, &fillNode, 1, target, source, bytes, hipMemcpyDeviceToDevice), "copy node");

  uint32_t *kernelTarget = target;
  unsigned kernelCount = count;
  void *args[2] = {nullptr, nullptr};
  hipKernelNodeParams kernel = addOneParams(kernelTarget, kernelCount, args);
  hipGraphNode_t kernelNode = nullptr;
  check(hipGraphAddKernelNode(&kernelNode, graph, &copyNode, 1, &kernel), "kernel node");

  size_t roots = 0;
  check(hipGraphGetRootNodes(graph, nullptr, &roots), "root count");
  REQUIRE(roots == 1);

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  check(hipGraphLaunch(exec, stream), "launch");
  check(hipStreamSynchronize(stream), "synchronize");
  expectEqual(download(source), std::vector<uint32_t>(count, 0x22222222u), "source fill");
  expectEqual(download(target), std::vector<uint32_t>(count, 0x22222223u), "target after copy and kernel");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(graph), "graph destroy");
  check(hipFree(source), "free source");
  check(hipFree(target), "free target");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/host-node-runs-each-launch", "[unified][runtime][async]") {
  std::atomic<int> calls{0};
  uint32_t *buffer = allocate();
  hipGraph_t graph = nullptr;
  check(hipGraphCreate(&graph, 0), "graph create");

  hipHostNodeParams host{};
  host.fn = bumpCounter;
  host.userData = &calls;
  hipGraphNode_t hostNode = nullptr;
  check(hipGraphAddHostNode(&hostNode, graph, nullptr, 0, &host), "host node");
  hipMemsetParams fill = fillParams(buffer, 0x33333333u);
  hipGraphNode_t fillNode = nullptr;
  check(hipGraphAddMemsetNode(&fillNode, graph, &hostNode, 1, &fill), "memset node");

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  check(hipGraphLaunch(exec, stream), "first launch");
  check(hipGraphLaunch(exec, stream), "second launch");
  check(hipStreamSynchronize(stream), "synchronize");
  REQUIRE(calls.load() == 2);
  expectEqual(download(buffer), std::vector<uint32_t>(count, 0x33333333u), "memset after host node");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(graph), "graph destroy");
  check(hipFree(buffer), "free");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/empty-node-joins-branches", "[unified][runtime][async]") {
  uint32_t *left = allocate();
  uint32_t *right = allocate();
  uint32_t *copied = allocate();
  hipGraph_t graph = nullptr;
  check(hipGraphCreate(&graph, 0), "graph create");

  hipMemsetParams leftFill = fillParams(left, 0x30303030u);
  hipGraphNode_t leftNode = nullptr;
  check(hipGraphAddMemsetNode(&leftNode, graph, nullptr, 0, &leftFill), "left memset");
  hipMemsetParams rightFill = fillParams(right, 0x40404040u);
  hipGraphNode_t rightNode = nullptr;
  check(hipGraphAddMemsetNode(&rightNode, graph, nullptr, 0, &rightFill), "right memset");

  hipGraphNode_t joins[2] = {leftNode, rightNode};
  hipGraphNode_t join = nullptr;
  check(hipGraphAddEmptyNode(&join, graph, joins, 2), "join node");
  hipGraphNode_t copyNode = nullptr;
  check(hipGraphAddMemcpyNode1D(&copyNode, graph, &join, 1, copied, left, bytes, hipMemcpyDeviceToDevice), "copy after join");

  size_t nodes = 0;
  check(hipGraphGetNodes(graph, nullptr, &nodes), "node count");
  REQUIRE(nodes == 4);

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  check(hipGraphLaunch(exec, stream), "launch");
  check(hipStreamSynchronize(stream), "synchronize");
  expectEqual(download(copied), std::vector<uint32_t>(count, 0x30303030u), "copy after join");
  expectEqual(download(right), std::vector<uint32_t>(count, 0x40404040u), "independent branch");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(graph), "graph destroy");
  check(hipFree(left), "free left");
  check(hipFree(right), "free right");
  check(hipFree(copied), "free copied");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/clone-runs-independently", "[unified][runtime][async]") {
  uint32_t *source = allocate();
  uint32_t *target = allocate();
  hipGraph_t graph = nullptr;
  check(hipGraphCreate(&graph, 0), "graph create");
  hipMemsetParams fill = fillParams(source, 0x44444444u);
  hipGraphNode_t fillNode = nullptr;
  check(hipGraphAddMemsetNode(&fillNode, graph, nullptr, 0, &fill), "memset node");
  hipGraphNode_t copyNode = nullptr;
  check(hipGraphAddMemcpyNode1D(&copyNode, graph, &fillNode, 1, target, source, bytes, hipMemcpyDeviceToDevice), "copy node");

  hipGraph_t clone = nullptr;
  check(hipGraphClone(&clone, graph), "clone");
  check(hipGraphDestroy(graph), "original destroy");

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, clone, 0), "instantiate");
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  check(hipGraphLaunch(exec, stream), "launch");
  check(hipStreamSynchronize(stream), "synchronize");
  expectEqual(download(target), std::vector<uint32_t>(count, 0x44444444u), "copied after clone");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(clone), "clone destroy");
  check(hipFree(source), "free source");
  check(hipFree(target), "free target");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/exec-update-same-topology", "[unified][runtime][async]") {
  uint32_t *buffer = allocate();
  auto build = [&](unsigned value) {
    hipGraph_t graph = nullptr;
    check(hipGraphCreate(&graph, 0), "graph create");
    hipMemsetParams fill = fillParams(buffer, value);
    hipGraphNode_t fillNode = nullptr;
    check(hipGraphAddMemsetNode(&fillNode, graph, nullptr, 0, &fill), "memset node");
    return graph;
  };

  hipGraph_t original = build(0x01010101u);
  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, original, 0), "instantiate");
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  check(hipGraphLaunch(exec, stream), "first launch");
  check(hipStreamSynchronize(stream), "first synchronize");
  expectEqual(download(buffer), std::vector<uint32_t>(count, 0x01010101u), "original value");

  hipGraph_t changed = build(0x05050505u);
  hipGraphNode_t errorNode = nullptr;
  hipGraphExecUpdateResult result{};
  check(hipGraphExecUpdate(exec, changed, &errorNode, &result), "update");
  REQUIRE(result == hipGraphExecUpdateSuccess);
  check(hipGraphLaunch(exec, stream), "second launch");
  check(hipStreamSynchronize(stream), "second synchronize");
  expectEqual(download(buffer), std::vector<uint32_t>(count, 0x05050505u), "updated value");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(original), "original destroy");
  check(hipGraphDestroy(changed), "changed destroy");
  check(hipFree(buffer), "free");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/exec-kernel-node-set-params", "[unified][runtime][async]") {
  uint32_t *first = allocate();
  uint32_t *second = allocate();
  hipGraph_t graph = nullptr;
  check(hipGraphCreate(&graph, 0), "graph create");
  uint32_t *target = first;
  unsigned n = count;
  void *args[2] = {nullptr, nullptr};
  hipKernelNodeParams kernel = addOneParams(target, n, args);
  hipGraphNode_t kernelNode = nullptr;
  check(hipGraphAddKernelNode(&kernelNode, graph, nullptr, 0, &kernel), "kernel node");

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  check(hipGraphLaunch(exec, stream), "first launch");

  uint32_t *retargeted = second;
  void *replacementArgs[2] = {nullptr, nullptr};
  hipKernelNodeParams replacement = addOneParams(retargeted, n, replacementArgs);
  check(hipGraphExecKernelNodeSetParams(exec, kernelNode, &replacement), "set params");
  check(hipGraphLaunch(exec, stream), "second launch");
  check(hipGraphLaunch(exec, stream), "third launch");
  check(hipStreamSynchronize(stream), "synchronize");
  expectEqual(download(first), std::vector<uint32_t>(count, 1u), "original target");
  expectEqual(download(second), std::vector<uint32_t>(count, 2u), "retargeted launches");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(graph), "graph destroy");
  check(hipFree(first), "free first");
  check(hipFree(second), "free second");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/instance-relaunches-accumulate", "[unified][runtime][async]") {
  uint32_t *buffer = allocate();
  hipGraph_t graph = nullptr;
  check(hipGraphCreate(&graph, 0), "graph create");
  uint32_t *target = buffer;
  unsigned n = count;
  void *args[2] = {nullptr, nullptr};
  hipKernelNodeParams kernel = addOneParams(target, n, args);
  hipGraphNode_t kernelNode = nullptr;
  check(hipGraphAddKernelNode(&kernelNode, graph, nullptr, 0, &kernel), "kernel node");

  hipGraphExec_t exec = nullptr;
  check(hipGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  hipStream_t stream = nullptr;
  check(hipStreamCreate(&stream), "stream");
  for (int i = 0; i < 3; ++i)
    check(hipGraphLaunch(exec, stream), "launch");
  check(hipStreamSynchronize(stream), "synchronize");
  expectEqual(download(buffer), std::vector<uint32_t>(count, 3u), "three launches");

  check(hipGraphExecDestroy(exec), "exec destroy");
  check(hipGraphDestroy(graph), "graph destroy");
  check(hipFree(buffer), "free");
  check(hipStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/hip/graphs/legacy-stream-capture-unsupported", "[unified][runtime][async]") {
  hipStreamCaptureStatus status = hipStreamCaptureStatusActive;
  check(hipStreamIsCapturing(hipStreamLegacy, &status), "legacy is capturing");
  REQUIRE(status == hipStreamCaptureStatusNone);
  REQUIRE(hipStreamBeginCapture(hipStreamLegacy, hipStreamCaptureModeGlobal) == hipErrorStreamCaptureUnsupported);
  (void)hipGetLastError();
}
