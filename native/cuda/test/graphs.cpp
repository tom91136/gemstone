#include <algorithm>
#include <atomic>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <fstream>
#include <iterator>
#include <string>
#include <thread>
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

constexpr char EmptyKernel[] = R"PTX(.version 8.4
.target sm_89
.address_size 64

.visible .entry noop()
{
  ret;
}
)PTX";

constexpr char ParameterKernel[] = R"PTX(.version 8.4
.target sm_89
.address_size 64

.visible .entry with_param(.param .u32 value)
{
  ret;
}
)PTX";

CUfunction loadKernel(const char *image, const char *name) {
  CUmodule module = nullptr;
  REQUIRE(cuModuleLoadData(&module, image) == CUDA_SUCCESS);
  CUfunction function = nullptr;
  REQUIRE(cuModuleGetFunction(&function, module, name) == CUDA_SUCCESS);
  return function;
}

CUgraphNodeType nodeType(CUgraphNode node) {
  CUgraphNodeType type{};
  CHECK(cuGraphNodeGetType(node, &type) == CUDA_SUCCESS);
  return type;
}

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

TEST_CASE("graph instantiates with auto free on launch and relaunches", "[cuda][graph][instantiate]") {
  auto *const context = createContext();
  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode node = nullptr;
  REQUIRE(cuGraphAddEmptyNode(&node, graph, nullptr, 0) == CUDA_SUCCESS);

  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, graph, CUDA_GRAPH_INSTANTIATE_FLAG_AUTO_FREE_ON_LAUNCH) == CUDA_SUCCESS);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("graph node type reports the kind of each explicit node", "[cuda][graph][node-type]") {
  auto *const context = createContext();
  constexpr size_t size = 16;
  const auto buffer = allocate(size);
  const auto other = allocate(size);
  fixture.kernelMetadata(0, 0, {});
  auto *const function = loadKernel(EmptyKernel, "noop");
  std::atomic<int> calls = 0;

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUDA_HOST_NODE_PARAMS hostParams{};
  hostParams.fn = countCall;
  hostParams.userData = &calls;
  CUgraphNode host = nullptr;
  REQUIRE(cuGraphAddHostNode(&host, graph, nullptr, 0, &hostParams) == CUDA_SUCCESS);
  CUgraphNode empty = nullptr;
  REQUIRE(cuGraphAddEmptyNode(&empty, graph, nullptr, 0) == CUDA_SUCCESS);
  CUDA_KERNEL_NODE_PARAMS kernelParams{};
  kernelParams.func = function;
  kernelParams.gridDimX = 1;
  kernelParams.gridDimY = 1;
  kernelParams.gridDimZ = 1;
  kernelParams.blockDimX = 1;
  kernelParams.blockDimY = 1;
  kernelParams.blockDimZ = 1;
  CUgraphNode kernel = nullptr;
  REQUIRE(cuGraphAddKernelNode(&kernel, graph, nullptr, 0, &kernelParams) == CUDA_SUCCESS);
  const auto set = fill(buffer, 0x7, size);
  CUgraphNode setNode = nullptr;
  REQUIRE(cuGraphAddMemsetNode(&setNode, graph, nullptr, 0, &set, nullptr) == CUDA_SUCCESS);
  const auto copy = copyDevice(buffer, other, size);
  CUgraphNode copyNode = nullptr;
  REQUIRE(cuGraphAddMemcpyNode(&copyNode, graph, nullptr, 0, &copy, nullptr) == CUDA_SUCCESS);

  CHECK(nodeType(host) == CU_GRAPH_NODE_TYPE_HOST);
  CHECK(nodeType(empty) == CU_GRAPH_NODE_TYPE_EMPTY);
  CHECK(nodeType(kernel) == CU_GRAPH_NODE_TYPE_KERNEL);
  CHECK(nodeType(setNode) == CU_GRAPH_NODE_TYPE_MEMSET);
  CHECK(nodeType(copyNode) == CU_GRAPH_NODE_TYPE_MEMCPY);
  CUgraphNodeType type = CU_GRAPH_NODE_TYPE_EMPTY;
  CHECK(cuGraphNodeGetType(nullptr, &type) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuGraphNodeGetType(empty, nullptr) == CUDA_ERROR_INVALID_VALUE);

  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(buffer) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(other) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("captured nodes keep their memset and memcpy kinds", "[cuda][graph][capture][node-type]") {
  auto *const context = createContext();
  constexpr size_t size = 32;
  const auto source = allocate(size);
  const auto target = allocate(size);
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);

  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_SUCCESS);
  REQUIRE(cuMemsetD8Async(source, 0x11, size, stream) == CUDA_SUCCESS);
  REQUIRE(cuMemcpyAsync(target, source, size, stream) == CUDA_SUCCESS);
  CUgraph graph = nullptr;
  REQUIRE(cuStreamEndCapture(stream, &graph) == CUDA_SUCCESS);

  size_t count = 0;
  REQUIRE(cuGraphGetNodes(graph, nullptr, &count) == CUDA_SUCCESS);
  REQUIRE(count == 2);
  std::vector<CUgraphNode> nodes(count);
  REQUIRE(cuGraphGetNodes(graph, nodes.data(), &count) == CUDA_SUCCESS);
  std::vector<CUgraphNodeType> types;
  types.reserve(nodes.size());
  for (auto *const node : nodes)
    types.push_back(nodeType(node));
  CHECK(std::count(types.begin(), types.end(), CU_GRAPH_NODE_TYPE_MEMSET) == 1);
  CHECK(std::count(types.begin(), types.end(), CU_GRAPH_NODE_TYPE_MEMCPY) == 1);

  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(source) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(target) == CUDA_SUCCESS);
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
  CHECK(cuGraphInstantiateWithFlags(&exec, empty, CUDA_GRAPH_INSTANTIATE_FLAG_UPLOAD) == CUDA_ERROR_NOT_SUPPORTED);
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

TEST_CASE("thread capture mode exchange returns the previous mode and starts as global", "[cuda][graph][capture][mode]") {
  auto *const context = createContext();
  CHECK(cuThreadExchangeStreamCaptureMode(nullptr) == CUDA_ERROR_INVALID_VALUE);
  auto mode = CU_STREAM_CAPTURE_MODE_RELAXED;
  REQUIRE(cuThreadExchangeStreamCaptureMode(&mode) == CUDA_SUCCESS);
  CHECK(mode == CU_STREAM_CAPTURE_MODE_GLOBAL);
  mode = CU_STREAM_CAPTURE_MODE_THREAD_LOCAL;
  REQUIRE(cuThreadExchangeStreamCaptureMode(&mode) == CUDA_SUCCESS);
  CHECK(mode == CU_STREAM_CAPTURE_MODE_RELAXED);

  CUstreamCaptureMode other = CU_STREAM_CAPTURE_MODE_THREAD_LOCAL;
  CUresult exchanged = CUDA_ERROR_UNKNOWN;
  std::thread([&] { exchanged = cuThreadExchangeStreamCaptureMode(&other); }).join();
  CHECK(exchanged == CUDA_SUCCESS);
  CHECK(other == CU_STREAM_CAPTURE_MODE_GLOBAL);

  mode = CU_STREAM_CAPTURE_MODE_GLOBAL;
  REQUIRE(cuThreadExchangeStreamCaptureMode(&mode) == CUDA_SUCCESS);
  CHECK(mode == CU_STREAM_CAPTURE_MODE_THREAD_LOCAL);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

namespace {

struct UnsafeCalls {
  CUresult allocate = CUDA_SUCCESS;
  CUresult free = CUDA_SUCCESS;
  CUresult allocateHost = CUDA_SUCCESS;
};

UnsafeCalls callUnsafe(CUcontext context, CUstreamCaptureMode threadMode, CUdeviceptr victim) {
  (void)cuCtxSetCurrent(context);
  (void)cuThreadExchangeStreamCaptureMode(&threadMode);
  UnsafeCalls result;
  CUdeviceptr device = 0;
  result.allocate = cuMemAlloc_v2(&device, 256);
  if (result.allocate == CUDA_SUCCESS) (void)cuMemFree_v2(device);
  result.free = cuMemFree_v2(victim);
  void *host = nullptr;
  result.allocateHost = cuMemAllocHost_v2(&host, 256);
  if (result.allocateHost == CUDA_SUCCESS) (void)cuMemFreeHost(host);
  return result;
}

} // namespace

TEST_CASE("capture modes decide which threads may make capture-unsafe calls", "[cuda][graph][capture][mode]") {
  auto *const context = createContext();
  constexpr auto global = CU_STREAM_CAPTURE_MODE_GLOBAL;
  constexpr auto threadLocal = CU_STREAM_CAPTURE_MODE_THREAD_LOCAL;
  constexpr auto relaxed = CU_STREAM_CAPTURE_MODE_RELAXED;
  struct Row {
    CUstreamCaptureMode capture;
    CUstreamCaptureMode thread;
    bool sameThread;
    bool prohibited;
  };
  // measured on a CUDA 13.5 driver: cuMemAlloc, cuMemFree and cuMemAllocHost all return CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED
  const auto make = [](CUstreamCaptureMode owner, CUstreamCaptureMode caller, bool sameThread, bool prohibited) {
    return Row{.capture = owner, .thread = caller, .sameThread = sameThread, .prohibited = prohibited};
  };
  const Row rows[] = {
      make(global, global, true, true),        make(global, threadLocal, true, true),        make(global, relaxed, true, false),
      make(threadLocal, global, true, true),   make(threadLocal, threadLocal, true, true),   make(threadLocal, relaxed, true, false),
      make(relaxed, global, true, false),      make(relaxed, threadLocal, true, false),      make(relaxed, relaxed, true, false),
      make(global, global, false, true),       make(global, threadLocal, false, false),      make(global, relaxed, false, false),
      make(threadLocal, global, false, false), make(threadLocal, threadLocal, false, false), make(threadLocal, relaxed, false, false),
      make(relaxed, global, false, false),     make(relaxed, threadLocal, false, false),     make(relaxed, relaxed, false, false),
  };
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  for (const auto &row : rows) {
    DYNAMIC_SECTION("capture " << row.capture << " thread " << row.thread << (row.sameThread ? " same" : " other")) {
      const auto victim = allocate(256);
      REQUIRE(cuStreamBeginCapture_v2(stream, row.capture) == CUDA_SUCCESS);
      UnsafeCalls calls;
      if (row.sameThread) calls = callUnsafe(context, row.thread, victim);
      else std::thread([&] { calls = callUnsafe(context, row.thread, victim); }).join();
      const auto expected = row.prohibited ? CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED : CUDA_SUCCESS;
      CHECK(calls.allocate == expected);
      CHECK(calls.free == expected);
      CHECK(calls.allocateHost == expected);
      CUgraph graph = nullptr;
      CHECK(cuStreamEndCapture(stream, &graph) == (row.prohibited ? CUDA_ERROR_STREAM_CAPTURE_INVALIDATED : CUDA_SUCCESS));
      if (graph) REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
      if (row.sameThread) {
        CUstreamCaptureMode restore = CU_STREAM_CAPTURE_MODE_GLOBAL;
        REQUIRE(cuThreadExchangeStreamCaptureMode(&restore) == CUDA_SUCCESS);
      }
      if (calls.free != CUDA_SUCCESS) REQUIRE(cuMemFree_v2(victim) == CUDA_SUCCESS);
    }
  }
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("graph instantiates with explicit parameters and optional upload", "[cuda][graph][instantiate]") {
  auto *const context = createContext();
  constexpr size_t size = 16;
  const auto target = allocate(size);
  REQUIRE(cuMemsetD8_v2(target, 0, size) == CUDA_SUCCESS);

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode node = nullptr;
  const auto set = fill(target, 0x63, size);
  REQUIRE(cuGraphAddMemsetNode(&node, graph, nullptr, 0, &set, nullptr) == CUDA_SUCCESS);

  CUstream upload = nullptr;
  REQUIRE(cuStreamCreate(&upload, 0) == CUDA_SUCCESS);
  CUDA_GRAPH_INSTANTIATE_PARAMS params{};
  params.hUploadStream = upload;
  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithParams(&exec, graph, &params) == CUDA_SUCCESS);
  CHECK(params.result_out == CUDA_GRAPH_INSTANTIATE_SUCCESS);
  CHECK(params.hErrNode_out == nullptr);

  CUgraphExec uploaded = nullptr;
  params.flags = CUDA_GRAPH_INSTANTIATE_FLAG_UPLOAD;
  REQUIRE(cuGraphInstantiateWithParams(&uploaded, graph, &params) == CUDA_SUCCESS);
  CHECK(params.result_out == CUDA_GRAPH_INSTANTIATE_SUCCESS);
  CHECK(params.hErrNode_out == nullptr);

  CUgraphExec prioritised = nullptr;
  params.flags = CUDA_GRAPH_INSTANTIATE_FLAG_USE_NODE_PRIORITY;
  REQUIRE(cuGraphInstantiateWithParams(&prioritised, graph, &params) == CUDA_SUCCESS);
  CHECK(params.result_out == CUDA_GRAPH_INSTANTIATE_SUCCESS);
  REQUIRE(cuGraphExecDestroy(prioritised) == CUDA_SUCCESS);
  REQUIRE(cuGraphInstantiateWithFlags(&prioritised, graph, CUDA_GRAPH_INSTANTIATE_FLAG_USE_NODE_PRIORITY) == CUDA_SUCCESS);
  REQUIRE(cuGraphExecDestroy(prioritised) == CUDA_SUCCESS);

  CHECK(cuGraphInstantiateWithParams(nullptr, graph, &params) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuGraphInstantiateWithParams(&exec, nullptr, &params) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuGraphInstantiateWithParams(&exec, graph, nullptr) == CUDA_ERROR_INVALID_VALUE);

  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(target, size) == std::vector<uint8_t>(size, 0x63));

  REQUIRE(cuGraphExecDestroy(uploaded) == CUDA_SUCCESS);
  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(upload) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(target) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("stream capture appends to an existing graph with dependencies", "[cuda][graph][capture]") {
  auto *const context = createContext();
  constexpr size_t size = 16;
  const auto target = allocate(size);
  REQUIRE(cuMemsetD8_v2(target, 0, size) == CUDA_SUCCESS);

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode root = nullptr;
  REQUIRE(cuGraphAddEmptyNode(&root, graph, nullptr, 0) == CUDA_SUCCESS);

  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuStreamBeginCaptureToGraph(stream, graph, &root, nullptr, 1, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_SUCCESS);
  CHECK(cuStreamBeginCaptureToGraph(stream, graph, nullptr, nullptr, 0, CU_STREAM_CAPTURE_MODE_GLOBAL)
        == CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED);
  REQUIRE(cuMemsetD8Async(target, 0x71, size, stream) == CUDA_SUCCESS);
  CUgraph returned = nullptr;
  REQUIRE(cuStreamEndCapture(stream, &returned) == CUDA_SUCCESS);
  CHECK(returned == graph);

  size_t count = 0;
  REQUIRE(cuGraphGetNodes(graph, nullptr, &count) == CUDA_SUCCESS);
  CHECK(count == 2);
  std::vector<CUgraphNode> nodes(count);
  REQUIRE(cuGraphGetNodes(graph, nodes.data(), &count) == CUDA_SUCCESS);
  CHECK(nodes[0] == root);
  size_t roots = 0;
  REQUIRE(cuGraphGetRootNodes(graph, nullptr, &roots) == CUDA_SUCCESS);
  CHECK(roots == 1);

  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, graph, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphLaunch(exec, stream) == CUDA_SUCCESS);
  REQUIRE(cuStreamSynchronize(stream) == CUDA_SUCCESS);
  CHECK(readDevice(target, size) == std::vector<uint8_t>(size, 0x71));

  CHECK(cuStreamBeginCaptureToGraph(stream, graph, nullptr, nullptr, 0, static_cast<CUstreamCaptureMode>(99)) == CUDA_ERROR_INVALID_VALUE);
  CUgraphNode foreign = nullptr;
  CUgraph other = nullptr;
  REQUIRE(cuGraphCreate(&other, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphAddEmptyNode(&foreign, other, nullptr, 0) == CUDA_SUCCESS);
  CHECK(cuStreamBeginCaptureToGraph(stream, graph, &foreign, nullptr, 1, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_ERROR_INVALID_HANDLE);

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(other) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuMemFree_v2(target) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("graph upload validates its arguments", "[cuda][graph][upload]") {
  auto *const context = createContext();
  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode node = nullptr;
  REQUIRE(cuGraphAddEmptyNode(&node, graph, nullptr, 0) == CUDA_SUCCESS);
  CUgraphExec exec = nullptr;
  REQUIRE(cuGraphInstantiateWithFlags(&exec, graph, 0) == CUDA_SUCCESS);

  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphUpload(exec, stream) == CUDA_SUCCESS);
  CHECK(cuGraphUpload(nullptr, stream) == CUDA_ERROR_INVALID_VALUE);

  REQUIRE(cuGraphExecDestroy(exec) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("graph memory attributes report no graph allocations", "[cuda][graph][memory]") {
  auto *const context = createContext();
  CUdevice device = 0;
  REQUIRE(cuDeviceGet(&device, 0) == CUDA_SUCCESS);
  for (const auto attribute : {CU_GRAPH_MEM_ATTR_USED_MEM_CURRENT, CU_GRAPH_MEM_ATTR_USED_MEM_HIGH, CU_GRAPH_MEM_ATTR_RESERVED_MEM_CURRENT,
                               CU_GRAPH_MEM_ATTR_RESERVED_MEM_HIGH}) {
    cuuint64_t value = 42;
    REQUIRE(cuDeviceGetGraphMemAttribute(device, attribute, &value) == CUDA_SUCCESS);
    CHECK(value == 0);
  }
  cuuint64_t value = 42;
  CHECK(cuDeviceGetGraphMemAttribute(device, CU_GRAPH_MEM_ATTR_USED_MEM_CURRENT, nullptr) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuDeviceGetGraphMemAttribute(-1, CU_GRAPH_MEM_ATTR_USED_MEM_CURRENT, &value) == CUDA_ERROR_INVALID_DEVICE);
  CHECK(cuDeviceGetGraphMemAttribute(device, static_cast<CUgraphMem_attribute>(99), &value) == CUDA_ERROR_INVALID_VALUE);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("graph debug print writes a DOT description", "[cuda][graph][debug]") {
  auto *const context = createContext();
  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode first = nullptr;
  CUgraphNode second = nullptr;
  REQUIRE(cuGraphAddEmptyNode(&first, graph, nullptr, 0) == CUDA_SUCCESS);
  REQUIRE(cuGraphAddEmptyNode(&second, graph, &first, 1) == CUDA_SUCCESS);

  const char path[] = "gemstone-graph-debug.dot";
  REQUIRE(cuGraphDebugDotPrint(graph, path, 0) == CUDA_SUCCESS);
  std::ifstream output(path);
  const std::string content((std::istreambuf_iterator<char>(output)), std::istreambuf_iterator<char>());
  CHECK(content.contains("digraph"));
  CHECK(content.contains('}'));
  REQUIRE(cuGraphDebugDotPrint(graph, path, CU_GRAPH_DEBUG_DOT_FLAGS_VERBOSE) == CUDA_SUCCESS);
  CHECK(cuGraphDebugDotPrint(graph, nullptr, 0) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuGraphDebugDotPrint(nullptr, path, 0) == CUDA_ERROR_INVALID_VALUE);
  std::remove(path);

  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("event synchronization invalidates a capture", "[cuda][graph][capture][event]") {
  auto *const context = createContext();
  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  CUevent event = nullptr;
  REQUIRE(cuEventCreate(&event, 0) == CUDA_SUCCESS);

  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_SUCCESS);
  REQUIRE(cuEventRecord(event, stream) == CUDA_SUCCESS);
  CHECK(cuEventSynchronize(event) == CUDA_ERROR_STREAM_CAPTURE_UNSUPPORTED);
  CUgraph graph = nullptr;
  CHECK(cuStreamEndCapture(stream, &graph) == CUDA_ERROR_STREAM_CAPTURE_INVALIDATED);

  CUstreamCaptureStatus status = CU_STREAM_CAPTURE_STATUS_ACTIVE;
  REQUIRE(cuStreamIsCapturing(stream, &status) == CUDA_SUCCESS);
  CHECK(status == CU_STREAM_CAPTURE_STATUS_NONE);

  REQUIRE(cuEventDestroy_v2(event) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("kernel nodes keep their launch parameters", "[cuda][graph][kernel-params]") {
  auto *const context = createContext();
  fixture.kernelMetadata(4, 1, {{.width = 4, .offset = 0, .kind = 1, .containsPointers = 0}});
  auto *const function = loadKernel(ParameterKernel, "with_param");
  int argument = 42;
  void *arguments[] = {&argument};

  CUDA_KERNEL_NODE_PARAMS params{};
  params.func = function;
  params.gridDimX = 2;
  params.gridDimY = 3;
  params.gridDimZ = 4;
  params.blockDimX = 5;
  params.blockDimY = 6;
  params.blockDimZ = 7;
  params.sharedMemBytes = 16;
  params.kernelParams = arguments;

  CUgraph graph = nullptr;
  REQUIRE(cuGraphCreate(&graph, 0) == CUDA_SUCCESS);
  CUgraphNode node = nullptr;
  REQUIRE(cuGraphAddKernelNode(&node, graph, nullptr, 0, &params) == CUDA_SUCCESS);

  CUDA_KERNEL_NODE_PARAMS readback{};
  REQUIRE(cuGraphKernelNodeGetParams_v2(node, &readback) == CUDA_SUCCESS);
  CHECK(readback.func == function);
  CHECK(readback.gridDimX == 2);
  CHECK(readback.gridDimY == 3);
  CHECK(readback.gridDimZ == 4);
  CHECK(readback.blockDimX == 5);
  CHECK(readback.blockDimY == 6);
  CHECK(readback.blockDimZ == 7);
  CHECK(readback.sharedMemBytes == 16);
  REQUIRE(readback.kernelParams != nullptr);
  CHECK(*static_cast<int *>(readback.kernelParams[0]) == 42);

  argument = 7;
  REQUIRE(cuGraphKernelNodeGetParams_v2(node, &readback) == CUDA_SUCCESS);
  CHECK(*static_cast<int *>(readback.kernelParams[0]) == 42);
  CHECK(cuGraphKernelNodeGetParams_v2(nullptr, &readback) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuGraphKernelNodeGetParams_v2(node, nullptr) == CUDA_ERROR_INVALID_VALUE);

  CUstream stream = nullptr;
  REQUIRE(cuStreamCreate(&stream, 0) == CUDA_SUCCESS);
  REQUIRE(cuStreamBeginCapture_v2(stream, CU_STREAM_CAPTURE_MODE_GLOBAL) == CUDA_SUCCESS);
  argument = 42;
  REQUIRE(cuLaunchKernel(function, 3, 1, 1, 4, 1, 1, 8, stream, arguments, nullptr) == CUDA_SUCCESS);
  CUgraph captured = nullptr;
  REQUIRE(cuStreamEndCapture(stream, &captured) == CUDA_SUCCESS);
  size_t count = 0;
  REQUIRE(cuGraphGetNodes(captured, nullptr, &count) == CUDA_SUCCESS);
  REQUIRE(count == 1);
  CUgraphNode capturedNode = nullptr;
  REQUIRE(cuGraphGetNodes(captured, &capturedNode, &count) == CUDA_SUCCESS);
  REQUIRE(cuGraphKernelNodeGetParams_v2(capturedNode, &readback) == CUDA_SUCCESS);
  CHECK(readback.gridDimX == 3);
  CHECK(readback.blockDimX == 4);
  CHECK(readback.sharedMemBytes == 8);
  CHECK(*static_cast<int *>(readback.kernelParams[0]) == 42);

  REQUIRE(cuGraphDestroy(captured) == CUDA_SUCCESS);
  REQUIRE(cuStreamDestroy_v2(stream) == CUDA_SUCCESS);
  REQUIRE(cuGraphDestroy(graph) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
