#include <atomic>
#include <cstddef>
#include <cstdint>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);
#undef cuGraphExecUpdate
extern "C" CUresult cuGraphExecUpdate(CUgraphExec, CUgraph, CUgraphNode *, CUgraphExecUpdateResult *);
#undef cuGraphInstantiate
extern "C" CUresult cuGraphInstantiate(CUgraphExec *, CUgraph, CUgraphNode *, char *, size_t);
extern "C" CUresult cuGraphInstantiate_v2(CUgraphExec *, CUgraph, CUgraphNode *, char *, size_t);

namespace {

// The facade connects to the execution service once per process, so every case shares this fixture.
gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");

CUcontext createContext() {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  return context;
}

CUdeviceptr allocate(size_t size) {
  CUdeviceptr device = 0;
  REQUIRE(cuMemAlloc_v2(&device, size) == CUDA_SUCCESS);
  return device;
}

std::vector<uint8_t> readDevice(CUdeviceptr device, size_t size) {
  std::vector<uint8_t> bytes(size);
  REQUIRE(cuMemcpyDtoH_v2(bytes.data(), device, size) == CUDA_SUCCESS);
  return bytes;
}

CUDA_MEMSET_NODE_PARAMS fill(CUdeviceptr device, unsigned value, size_t size) {
  return CUDA_MEMSET_NODE_PARAMS{.dst = device, .pitch = 0, .value = value, .elementSize = 1, .width = size, .height = 1};
}

CUDA_MEMCPY3D copyDevice(CUdeviceptr source, CUdeviceptr target, size_t size) {
  CUDA_MEMCPY3D copy{};
  copy.srcMemoryType = CU_MEMORYTYPE_DEVICE;
  copy.srcDevice = source;
  copy.dstMemoryType = CU_MEMORYTYPE_DEVICE;
  copy.dstDevice = target;
  copy.srcPitch = 0;
  copy.dstPitch = 0;
  copy.WidthInBytes = size;
  copy.Height = 1;
  copy.Depth = 1;
  return copy;
}

void countCall(void *userData) { ++*static_cast<std::atomic<int> *>(userData); }

} // namespace

TEST_CASE("stream capture records async memset and copy without running them", "[cuda][graph][capture]") {
  auto *const context = createContext();
  constexpr size_t size = 64;
  const auto source = allocate(size);
  const auto target = allocate(size);
  REQUIRE(cuMemsetD8_v2(source, 0, size) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD8_v2(target, 0, size) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);

  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_SUCCESS);
  CUstreamCaptureStatus status = CU_STREAM_CAPTURE_STATUS_NONE;
  REQUIRE(cuStreamIsCapturing(stream, &status) == CUDA_SUCCESS);
  CHECK(status == CU_STREAM_CAPTURE_STATUS_ACTIVE);
  REQUIRE(cuMemsetD8Async(source, 0x11, size, stream) == CUDA_SUCCESS);
  REQUIRE(cuMemcpyAsync(target, source, size, stream) == CUDA_SUCCESS);

  CUgraph graph = nullptr;
  REQUIRE(cuStreamEndCapture(stream, &graph) == CUDA_SUCCESS);
  REQUIRE(graph != nullptr);
  REQUIRE(cuStreamIsCapturing(stream, &status) == CUDA_SUCCESS);
  CHECK(status == CU_STREAM_CAPTURE_STATUS_NONE);
  CHECK(readDevice(source, size) == std::vector<uint8_t>(size, 0));
  CHECK(readDevice(target, size) == std::vector<uint8_t>(size, 0));

  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, graph, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(target, size) == std::vector<uint8_t>(size, 0x11));

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(source) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(target) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("stream capture forks and joins through events", "[cuda][graph][capture]") {
  auto *const context = createContext();
  constexpr size_t size = 32;
  const auto firstBuffer = allocate(size);
  const auto secondBuffer = allocate(size);
  const auto thirdBuffer = allocate(size);
  REQUIRE(cuMemsetD8_v2(thirdBuffer, 0, size) == CUDA_SUCCESS);
  CUstream first = nullptr;
  CUstream second = nullptr;
  REQUIRE(cuStreamCreate(&first, 0) == CUDA_SUCCESS);
  REQUIRE(cuStreamCreate(&second, 0) == CUDA_SUCCESS);
  CUevent fork = nullptr;
  CUevent join = nullptr;
  REQUIRE(cuEventCreate(&fork, 0) == CUDA_SUCCESS);
  REQUIRE(cuEventCreate(&join, 0) == CUDA_SUCCESS);

  REQUIRE(cuStreamBeginCapture_v2(first, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD8Async(firstBuffer, 0x10, size, first) == CUDA_SUCCESS);
  REQUIRE(cuEventRecord(fork, first) == CUDA_SUCCESS);
  REQUIRE(cuStreamWaitEvent(second, fork, 0) == CUDA_SUCCESS);
  CUstreamCaptureStatus status = CU_STREAM_CAPTURE_STATUS_NONE;
  REQUIRE(cuStreamIsCapturing(second, &status) == CUDA_SUCCESS);
  CHECK(status == CU_STREAM_CAPTURE_STATUS_ACTIVE);
  REQUIRE(cuMemsetD8Async(secondBuffer, 0x20, size, second) == CUDA_SUCCESS);
  REQUIRE(cuEventRecord(join, second) == CUDA_SUCCESS);
  REQUIRE(cuStreamWaitEvent(first, join, 0) == CUDA_SUCCESS);
  REQUIRE(cuMemcpyAsync(thirdBuffer, firstBuffer, size, first) == CUDA_SUCCESS);

  CUgraph graph = nullptr;
  REQUIRE(cuStreamEndCapture(first, &graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamIsCapturing(second, &status) == CUDA_SUCCESS);
  CHECK(status == CU_STREAM_CAPTURE_STATUS_NONE);

  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, graph, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, first) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(first) == CUDA_SUCCESS);
  CHECK(readDevice(firstBuffer, size) == std::vector<uint8_t>(size, 0x10));
  CHECK(readDevice(secondBuffer, size) == std::vector<uint8_t>(size, 0x20));
  CHECK(readDevice(thirdBuffer, size) == std::vector<uint8_t>(size, 0x10));

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuEventDestroy_v2(fork) == CUDA_SUCCESS);
  REQUIRE(cuEventDestroy_v2(join) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(first) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(second) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(firstBuffer) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(secondBuffer) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(thirdBuffer) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("explicit graph runs nodes in dependency order", "[cuda][graph][explicit]") {
  auto *const context = createContext();
  constexpr size_t size = 48;
  const auto source = allocate(size);
  const auto target = allocate(size);
  REQUIRE(cuMemsetD8_v2(source, 0, size) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD8_v2(target, 0, size) == CUDA_SUCCESS);

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode nodes[3] = {};
  const auto firstSet = fill(source, 0x22, size);
  REQUIRE(cuGraphAddMemsetNode(&nodes[0], graph, nullptr, 0, &firstSet, nullptr) == CUDA_SUCCESS);
  const auto copy = copyDevice(source, target, size);
  REQUIRE(cuGraphAddMemcpyNode(&nodes[1], graph, &nodes[0], 1, &copy, nullptr) == CUDA_SUCCESS);
  const auto secondSet = fill(source, 0x44, size);
  REQUIRE(cuGraphAddMemsetNode(&nodes[2], graph, &nodes[1], 1, &secondSet, nullptr) == CUDA_SUCCESS);

  CUgraphNode roots[4] = {};
  size_t rootCount = 4;
  REQUIRE(cuGraphGetRootNodes(graph, roots, &rootCount) == CUDA_SUCCESS);
  CHECK(rootCount == 1);
  CHECK(roots[0] == nodes[0]);

  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, graph, 0) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(source, size) == std::vector<uint8_t>(size, 0x44));
  CHECK(readDevice(target, size) == std::vector<uint8_t>(size, 0x22));

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(source) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(target) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("graph dependencies reject cycles, self edges and foreign nodes", "[cuda][graph][dependencies]") {
  auto *const context = createContext();
  CUgraph graph = nullptr;
  CUgraph other = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphCreate(&other, 0) == CUDA_SUCCESS);
  CUgraphNode first = nullptr;
  CUgraphNode second = nullptr;
  CUgraphNode foreign = nullptr;
  REQUIRE(cuGraphAddEmptyNode(&first, graph, nullptr, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphAddEmptyNode(&second, graph, nullptr, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphAddEmptyNode(&foreign, other, nullptr, 0) == CUDA_SUCCESS);

  const CUgraphEdgeData edge{};
  CHECK(cuGraphAddDependencies_v2(graph, &first, &second, &edge, 1) == CUDA_ERROR_NOT_SUPPORTED);
  REQUIRE(cuGraphAddDependencies_v2(graph, &first, &second, nullptr, 1) == CUDA_SUCCESS);
  CHECK(cuGraphAddDependencies_v2(graph, &second, &first, nullptr, 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuGraphAddDependencies_v2(graph, &first, &first, nullptr, 1) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuGraphAddDependencies_v2(graph, &foreign, &second, nullptr, 1) == CUDA_ERROR_INVALID_HANDLE);

  CUgraphNode roots[4] = {};
  size_t rootCount = 4;
  REQUIRE(cuGraphGetRootNodes(graph, roots, &rootCount) == CUDA_SUCCESS);
  CHECK(rootCount == 1);
  CHECK(roots[0] == first);

  REQUIRE(cuGraphDestroy(other) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("host node runs its callback on each launch", "[cuda][graph][host]") {
  auto *const context = createContext();
  std::atomic<int> calls = 0;
  CUDA_HOST_NODE_PARAMS params{};
  params.fn = countCall;
  params.userData = &calls;

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode node = nullptr;
  REQUIRE(cuGraphAddHostNode(&node, graph, nullptr, 0, &params) == CUDA_SUCCESS);
  const CUDA_HOST_NODE_PARAMS empty{};
  CHECK(cuGraphAddHostNode(&node, graph, nullptr, 0, &empty) == CUDA_ERROR_INVALID_VALUE);

  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, graph, 0) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(calls.load() == 2);

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("graph exec update accepts same topology and rejects a changed one", "[cuda][graph][update]") {
  auto *const context = createContext();
  constexpr size_t size = 16;
  const auto buffer = allocate(size);
  REQUIRE(cuMemsetD8_v2(buffer, 0, size) == CUDA_SUCCESS);

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode node = nullptr;
  const auto initial = fill(buffer, 0x31, size);
  REQUIRE(cuGraphAddMemsetNode(&node, graph, nullptr, 0, &initial, nullptr) == CUDA_SUCCESS);
  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, graph, 0) == CUDA_SUCCESS);

  CUgraph updated = nullptr;
  REQUIRE(cuGraphCreate(&updated, 0) == CUDA_SUCCESS);
  const auto replacement = fill(buffer, 0x52, size);
  REQUIRE(cuGraphAddMemsetNode(&node, updated, nullptr, 0, &replacement, nullptr) == CUDA_SUCCESS);
  CUgraphExecUpdateResultInfo info{};
  REQUIRE(cuGraphExecUpdate_v2(exec, updated, &info) == CUDA_SUCCESS);
  CHECK(info.result == CU_GRAPH_EXEC_UPDATE_SUCCESS);

  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(buffer, size) == std::vector<uint8_t>(size, 0x52));

  CUgraph reshaped = nullptr;
  REQUIRE(cuGraphCreate(&reshaped, 0) == CUDA_SUCCESS);
  CUgraphNode extra = nullptr;
  REQUIRE(cuGraphAddEmptyNode(&extra, reshaped, nullptr, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphAddMemsetNode(&node, reshaped, nullptr, 0, &replacement, nullptr) == CUDA_SUCCESS);
  CUgraphExecUpdateResultInfo rejected{};
  CHECK(cuGraphExecUpdate_v2(exec, reshaped, &rejected) == CUDA_ERROR_GRAPH_EXEC_UPDATE_FAILURE);
  CHECK(rejected.result == CU_GRAPH_EXEC_UPDATE_ERROR_TOPOLOGY_CHANGED);
  CUgraphNode legacyNode = extra;
  CUgraphExecUpdateResult legacy = CU_GRAPH_EXEC_UPDATE_SUCCESS;
  CHECK(cuGraphExecUpdate(exec, reshaped, &legacyNode, &legacy) == CUDA_ERROR_GRAPH_EXEC_UPDATE_FAILURE);
  CHECK(legacy == CU_GRAPH_EXEC_UPDATE_ERROR_TOPOLOGY_CHANGED);
  CHECK(legacyNode == nullptr);

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(updated) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(reshaped) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(buffer) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("capture misuse and invalid arguments return driver errors", "[cuda][graph][capture][errors]") {
  auto *const context = createContext();
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUgraph graph = nullptr;

  CHECK(cuStreamBeginCapture_v2(stream, static_cast<CUstreamCaptureMode>(99)) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuStreamBeginCapture_v2(CU_STREAM_LEGACY, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED);
  CHECK(cuStreamEndCapture(stream, &graph) == CUDA_ERROR_INVALID_VALUE);

  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_THREAD_LOCAL) == CUDA_SUCCESS);
  CHECK(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED);
  CHECK(cuStreamEndCapture(stream, nullptr) == CUDA_ERROR_INVALID_VALUE);
  REQUIRE(cuStreamEndCapture(stream, &graph) == CUDA_SUCCESS);
  REQUIRE(graph != nullptr);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);

  graph = nullptr;
  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_RELAXED) == CUDA_SUCCESS);
  REQUIRE(cuStreamEndCapture(stream, &graph) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);

  CHECK(cuGraphInstantiateWithFlags(nullptr, nullptr, 0) == CUDA_ERROR_INVALID_VALUE);
  CUgraph empty = nullptr;
  REQUIRE(cuGraphCreate(&empty, 0) == CUDA_SUCCESS);
  CUgraphExec exec = nullptr;
  CHECK(cuGraphInstantiateWithFlags(&exec, empty, 1) == CUDA_ERROR_NOT_SUPPORTED);
  REQUIRE(cuGraphDestroy(empty) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("graph clone copies nodes and dependencies independently of the original", "[cuda][graph][clone]") {
  auto *const context = createContext();
  constexpr size_t size = 32;
  const auto source = allocate(size);
  const auto target = allocate(size);
  REQUIRE(cuMemsetD8_v2(source, 0, size) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD8_v2(target, 0, size) == CUDA_SUCCESS);

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode nodes[3] = {};
  const auto firstSet = fill(source, 0x33, size);
  REQUIRE(cuGraphAddMemsetNode(&nodes[0], graph, nullptr, 0, &firstSet, nullptr) == CUDA_SUCCESS);
  const auto copy = copyDevice(source, target, size);
  REQUIRE(cuGraphAddMemcpyNode(&nodes[1], graph, &nodes[0], 1, &copy, nullptr) == CUDA_SUCCESS);
  REQUIRE(cuGraphAddEmptyNode(&nodes[2], graph, &nodes[1], 1) == CUDA_SUCCESS);

  CUgraph clone = nullptr;
  CHECK(cuGraphClone(nullptr, graph) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuGraphClone(&clone, nullptr) == CUDA_ERROR_INVALID_VALUE);
  REQUIRE(cuGraphClone(&clone, graph) == CUDA_SUCCESS);
  REQUIRE(clone != nullptr);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);

  CUgraphNode roots[4] = {};
  size_t rootCount = 4;
  REQUIRE(cuGraphGetRootNodes(clone, roots, &rootCount) == CUDA_SUCCESS);
  CHECK(rootCount == 1);

  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, clone, 0) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(target, size) == std::vector<uint8_t>(size, 0x33));

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(clone) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(source) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(target) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("legacy instantiate entry points forward to graph instantiation", "[cuda][graph][instantiate]") {
  auto *const context = createContext();
  constexpr size_t size = 16;
  const auto target = allocate(size);
  REQUIRE(cuMemsetD8_v2(target, 0, size) == CUDA_SUCCESS);

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode node = nullptr;
  const auto set = fill(target, 0x55, size);
  REQUIRE(cuGraphAddMemsetNode(&node, graph, nullptr, 0, &set, nullptr) == CUDA_SUCCESS);

  char log[64] = {};
  CUgraphNode errorNode = nullptr;
  CUgraphExec exec = nullptr;
  CHECK(cuGraphInstantiate(nullptr, nullptr, &errorNode, log, sizeof log) == CUDA_ERROR_INVALID_VALUE);
  CHECK(log[0] == '\0');
  CHECK(errorNode == nullptr);

  log[0] = 'x';
  REQUIRE(cuGraphInstantiate(&exec, graph, &errorNode, log, sizeof log) == CUDA_SUCCESS);
  CHECK(errorNode == nullptr);
  CUgraphExec second = nullptr;
  REQUIRE(cuGraphInstantiate_v2(&second, graph, nullptr, nullptr, 0) == CUDA_SUCCESS);

  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(target, size) == std::vector<uint8_t>(size, 0x55));

  REQUIRE(cuGraphExecDestroy(second) == CUDA_SUCCESS);
  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(target) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
