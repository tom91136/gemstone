#include <atomic>
#include <cstdint>
#include <vector>

#include "cuda/support.cuh"
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime.h>

namespace {

constexpr unsigned Count = 64;
constexpr size_t Bytes = Count * sizeof(uint32_t);

__global__ void addOne(uint32_t *values, unsigned n) {
  const unsigned i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i < n) values[i] += 1;
}

void CUDART_CB bumpCounter(void *counter) { static_cast<std::atomic<int> *>(counter)->fetch_add(1); }

uint32_t *allocate() {
  uint32_t *device = nullptr;
  check(cudaMalloc(&device, Bytes), "allocate");
  check(cudaMemset(device, 0, Bytes), "clear");
  return device;
}

std::vector<uint32_t> download(const uint32_t *device) {
  std::vector<uint32_t> host(Count, 0);
  check(cudaMemcpy(host.data(), device, Bytes, cudaMemcpyDeviceToHost), "download");
  return host;
}

cudaKernelNodeParams addOneParams(uint32_t *&values, unsigned &n, void **args) {
  args[0] = &values;
  args[1] = &n;
  cudaKernelNodeParams params{};
  params.func = reinterpret_cast<void *>(addOne);
  params.gridDim = dim3(1, 1, 1);
  params.blockDim = dim3(Count, 1, 1);
  params.kernelParams = args;
  return params;
}

cudaMemsetParams fillParams(uint32_t *device, unsigned value) {
  cudaMemsetParams params{};
  params.dst = device;
  params.value = value;
  params.elementSize = sizeof(uint32_t);
  params.width = Count;
  params.height = 1;
  return params;
}

} // namespace

TEST_CASE("runtime/cuda/graphs/capture-replays-recorded-work", "[unified][runtime][async]") {
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  uint32_t *buffer = allocate();

  check(cudaStreamBeginCapture(stream, cudaStreamCaptureModeThreadLocal), "begin capture");
  cudaStreamCaptureStatus status = cudaStreamCaptureStatusNone;
  check(cudaStreamIsCapturing(stream, &status), "is capturing");
  REQUIRE(status == cudaStreamCaptureStatusActive);
  check(cudaMemsetAsync(buffer, 0x11, Bytes, stream), "captured memset");
  addOne<<<1, Count, 0, stream>>>(buffer, Count);
  cudaGraph_t graph = nullptr;
  check(cudaStreamEndCapture(stream, &graph), "end capture");
  check(cudaStreamIsCapturing(stream, &status), "is capturing after end");
  REQUIRE(status == cudaStreamCaptureStatusNone);
  REQUIRE(download(buffer) == std::vector<uint32_t>(Count, 0));

  cudaGraphExec_t exec = nullptr;
  check(cudaGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  check(cudaGraphLaunch(exec, stream), "launch");
  check(cudaStreamSynchronize(stream), "synchronize");
  expectEqual(download(buffer), std::vector<uint32_t>(Count, 0x11111112U), "captured round trip");

  check(cudaGraphExecDestroy(exec), "exec destroy");
  check(cudaGraphDestroy(graph), "graph destroy");
  check(cudaFree(buffer), "free");
  check(cudaStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/cuda/graphs/capture-fork-join-through-events", "[unified][runtime][async]") {
  cudaStream_t origin = nullptr;
  cudaStream_t branch = nullptr;
  check(cudaStreamCreate(&origin), "origin stream");
  check(cudaStreamCreate(&branch), "branch stream");
  cudaEvent_t fork = nullptr;
  cudaEvent_t join = nullptr;
  check(cudaEventCreateWithFlags(&fork, cudaEventDisableTiming), "fork event");
  check(cudaEventCreateWithFlags(&join, cudaEventDisableTiming), "join event");
  uint32_t *first = allocate();
  uint32_t *second = allocate();
  uint32_t *copied = allocate();

  check(cudaStreamBeginCapture(origin, cudaStreamCaptureModeGlobal), "begin capture");
  check(cudaEventRecord(fork, origin), "fork record");
  check(cudaStreamWaitEvent(branch, fork, 0), "fork wait");
  check(cudaMemsetAsync(first, 0x10, Bytes, origin), "first memset");
  check(cudaMemsetAsync(second, 0x20, Bytes, branch), "second memset");
  check(cudaEventRecord(join, branch), "join record");
  check(cudaStreamWaitEvent(origin, join, 0), "join wait");
  check(cudaMemcpyAsync(copied, second, Bytes, cudaMemcpyDeviceToDevice, origin), "copy after join");
  cudaGraph_t graph = nullptr;
  check(cudaStreamEndCapture(origin, &graph), "end capture");

  cudaGraphExec_t exec = nullptr;
  check(cudaGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  check(cudaGraphLaunch(exec, origin), "launch");
  check(cudaStreamSynchronize(origin), "synchronize");
  expectEqual(download(first), std::vector<uint32_t>(Count, 0x10101010U), "origin branch");
  expectEqual(download(copied), std::vector<uint32_t>(Count, 0x20202020U), "joined branch");

  check(cudaGraphExecDestroy(exec), "exec destroy");
  check(cudaGraphDestroy(graph), "graph destroy");
  check(cudaFree(first), "free first");
  check(cudaFree(second), "free second");
  check(cudaFree(copied), "free copied");
  check(cudaEventDestroy(fork), "fork destroy");
  check(cudaEventDestroy(join), "join destroy");
  check(cudaStreamDestroy(origin), "origin destroy");
  check(cudaStreamDestroy(branch), "branch destroy");
}

TEST_CASE("runtime/cuda/graphs/explicit-kernel-dependency-chain", "[unified][runtime][async]") {
  uint32_t *source = allocate();
  uint32_t *target = allocate();
  cudaGraph_t graph = nullptr;
  check(cudaGraphCreate(&graph, 0), "graph create");

  cudaMemsetParams fill = fillParams(source, 0x22222222U);
  cudaGraphNode_t fillNode = nullptr;
  check(cudaGraphAddMemsetNode(&fillNode, graph, nullptr, 0, &fill), "memset node");

  cudaGraphNode_t copyNode = nullptr;
  check(cudaGraphAddMemcpyNode1D(&copyNode, graph, &fillNode, 1, target, source, Bytes, cudaMemcpyDeviceToDevice), "copy node");

  uint32_t *kernelTarget = target;
  unsigned kernelCount = Count;
  void *args[2] = {nullptr, nullptr};
  cudaKernelNodeParams kernel = addOneParams(kernelTarget, kernelCount, args);
  cudaGraphNode_t kernelNode = nullptr;
  check(cudaGraphAddKernelNode(&kernelNode, graph, &copyNode, 1, &kernel), "kernel node");

  size_t roots = 0;
  check(cudaGraphGetRootNodes(graph, nullptr, &roots), "root count");
  REQUIRE(roots == 1);

  cudaGraphExec_t exec = nullptr;
  check(cudaGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  check(cudaGraphLaunch(exec, stream), "launch");
  check(cudaStreamSynchronize(stream), "synchronize");
  expectEqual(download(source), std::vector<uint32_t>(Count, 0x22222222U), "source fill");
  expectEqual(download(target), std::vector<uint32_t>(Count, 0x22222223U), "target after copy and kernel");

  check(cudaGraphExecDestroy(exec), "exec destroy");
  check(cudaGraphDestroy(graph), "graph destroy");
  check(cudaFree(source), "free source");
  check(cudaFree(target), "free target");
  check(cudaStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/cuda/graphs/host-node-runs-each-launch", "[unified][runtime][async]") {
  std::atomic<int> calls{0};
  uint32_t *buffer = allocate();
  cudaGraph_t graph = nullptr;
  check(cudaGraphCreate(&graph, 0), "graph create");

  cudaHostNodeParams host{};
  host.fn = bumpCounter;
  host.userData = &calls;
  cudaGraphNode_t hostNode = nullptr;
  check(cudaGraphAddHostNode(&hostNode, graph, nullptr, 0, &host), "host node");
  cudaMemsetParams fill = fillParams(buffer, 0x33333333U);
  cudaGraphNode_t fillNode = nullptr;
  check(cudaGraphAddMemsetNode(&fillNode, graph, &hostNode, 1, &fill), "memset node");

  cudaGraphExec_t exec = nullptr;
  check(cudaGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  check(cudaGraphLaunch(exec, stream), "first launch");
  check(cudaGraphLaunch(exec, stream), "second launch");
  check(cudaStreamSynchronize(stream), "synchronize");
  REQUIRE(calls.load() == 2);
  expectEqual(download(buffer), std::vector<uint32_t>(Count, 0x33333333U), "memset after host node");

  check(cudaGraphExecDestroy(exec), "exec destroy");
  check(cudaGraphDestroy(graph), "graph destroy");
  check(cudaFree(buffer), "free");
  check(cudaStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/cuda/graphs/empty-node-joins-branches", "[unified][runtime][async]") {
  uint32_t *left = allocate();
  uint32_t *right = allocate();
  uint32_t *copied = allocate();
  cudaGraph_t graph = nullptr;
  check(cudaGraphCreate(&graph, 0), "graph create");

  cudaMemsetParams leftFill = fillParams(left, 0x30303030U);
  cudaGraphNode_t leftNode = nullptr;
  check(cudaGraphAddMemsetNode(&leftNode, graph, nullptr, 0, &leftFill), "left memset");
  cudaMemsetParams rightFill = fillParams(right, 0x40404040U);
  cudaGraphNode_t rightNode = nullptr;
  check(cudaGraphAddMemsetNode(&rightNode, graph, nullptr, 0, &rightFill), "right memset");

  cudaGraphNode_t joins[2] = {leftNode, rightNode};
  cudaGraphNode_t join = nullptr;
  check(cudaGraphAddEmptyNode(&join, graph, joins, 2), "join node");
  cudaGraphNode_t copyNode = nullptr;
  check(cudaGraphAddMemcpyNode1D(&copyNode, graph, &join, 1, copied, left, Bytes, cudaMemcpyDeviceToDevice), "copy after join");

  size_t nodes = 0;
  check(cudaGraphGetNodes(graph, nullptr, &nodes), "node count");
  REQUIRE(nodes == 4);

  cudaGraphExec_t exec = nullptr;
  check(cudaGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  check(cudaGraphLaunch(exec, stream), "launch");
  check(cudaStreamSynchronize(stream), "synchronize");
  expectEqual(download(copied), std::vector<uint32_t>(Count, 0x30303030U), "copy after join");
  expectEqual(download(right), std::vector<uint32_t>(Count, 0x40404040U), "independent branch");

  check(cudaGraphExecDestroy(exec), "exec destroy");
  check(cudaGraphDestroy(graph), "graph destroy");
  check(cudaFree(left), "free left");
  check(cudaFree(right), "free right");
  check(cudaFree(copied), "free copied");
  check(cudaStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/cuda/graphs/exec-update-same-topology", "[unified][runtime][async]") {
  uint32_t *buffer = allocate();
  auto build = [&](unsigned value) {
    cudaGraph_t graph = nullptr;
    check(cudaGraphCreate(&graph, 0), "graph create");
    cudaMemsetParams fill = fillParams(buffer, value);
    cudaGraphNode_t fillNode = nullptr;
    check(cudaGraphAddMemsetNode(&fillNode, graph, nullptr, 0, &fill), "memset node");
    return graph;
  };

  cudaGraph_t original = build(0x01010101U);
  cudaGraphExec_t exec = nullptr;
  check(cudaGraphInstantiateWithFlags(&exec, original, 0), "instantiate");
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  check(cudaGraphLaunch(exec, stream), "first launch");
  check(cudaStreamSynchronize(stream), "first synchronize");
  expectEqual(download(buffer), std::vector<uint32_t>(Count, 0x01010101U), "original value");

  cudaGraph_t changed = build(0x05050505U);
#if CUDART_VERSION >= 12000
  cudaGraphExecUpdateResultInfo info{};
  check(cudaGraphExecUpdate(exec, changed, &info), "update");
  REQUIRE(info.result == cudaGraphExecUpdateSuccess);
#else
  cudaGraphNode_t errorNode = nullptr;
  cudaGraphExecUpdateResult result{};
  check(cudaGraphExecUpdate(exec, changed, &errorNode, &result), "update");
  REQUIRE(result == cudaGraphExecUpdateSuccess);
#endif
  check(cudaGraphLaunch(exec, stream), "second launch");
  check(cudaStreamSynchronize(stream), "second synchronize");
  expectEqual(download(buffer), std::vector<uint32_t>(Count, 0x05050505U), "updated value");

  check(cudaGraphExecDestroy(exec), "exec destroy");
  check(cudaGraphDestroy(original), "original destroy");
  check(cudaGraphDestroy(changed), "changed destroy");
  check(cudaFree(buffer), "free");
  check(cudaStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/cuda/graphs/instance-relaunches-accumulate", "[unified][runtime][async]") {
  uint32_t *buffer = allocate();
  cudaGraph_t graph = nullptr;
  check(cudaGraphCreate(&graph, 0), "graph create");
  uint32_t *target = buffer;
  unsigned n = Count;
  void *args[2] = {nullptr, nullptr};
  cudaKernelNodeParams kernel = addOneParams(target, n, args);
  cudaGraphNode_t kernelNode = nullptr;
  check(cudaGraphAddKernelNode(&kernelNode, graph, nullptr, 0, &kernel), "kernel node");

  cudaGraphExec_t exec = nullptr;
  check(cudaGraphInstantiateWithFlags(&exec, graph, 0), "instantiate");
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  for (int i = 0; i < 3; ++i)
    check(cudaGraphLaunch(exec, stream), "launch");
  check(cudaStreamSynchronize(stream), "synchronize");
  expectEqual(download(buffer), std::vector<uint32_t>(Count, 3U), "three launches");

  check(cudaGraphExecDestroy(exec), "exec destroy");
  check(cudaGraphDestroy(graph), "graph destroy");
  check(cudaFree(buffer), "free");
  check(cudaStreamDestroy(stream), "stream destroy");
}

TEST_CASE("runtime/cuda/graphs/legacy-stream-capture-unsupported", "[unified][runtime][async]") {
  cudaStreamCaptureStatus status = cudaStreamCaptureStatusNone;
  check(cudaStreamIsCapturing(cudaStreamLegacy, &status), "legacy is capturing");
  REQUIRE(status == cudaStreamCaptureStatusNone);
  REQUIRE(cudaStreamBeginCapture(cudaStreamLegacy, cudaStreamCaptureModeGlobal) == cudaErrorStreamCaptureUnsupported);
}
