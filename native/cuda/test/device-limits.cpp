#include <cstddef>
#include <string>

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include <environment_keys.hpp>

extern "C" CUresult cuCtxCreate_v2(CUcontext *context, unsigned flags, CUdevice device);

namespace {

gemstone_test::RpcFixture fixture;
const std::string Endpoint = fixture.endpoint();
const gemstone_test::Environment RpcEndpoint(gemstone::environment::RpcEndpoint, Endpoint.c_str());
const gemstone_test::Environment RpcClient(gemstone::environment::RpcClientId, "42");

constexpr size_t DefaultHeap = size_t{8} << 20;

CUcontext createContext() {
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  CUcontext context = nullptr;
  REQUIRE(cuCtxCreate_v2(&context, 0, 0) == CUDA_SUCCESS);
  return context;
}

size_t heapLimit() {
  size_t value = 0;
  REQUIRE(cuCtxGetLimit(&value, CU_LIMIT_MALLOC_HEAP_SIZE) == CUDA_SUCCESS);
  return value;
}

} // namespace

TEST_CASE("CUDA reports the bus width and L2 size of its target's reference card", "[cuda][limits]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  int bus = 0;
  int l2 = 0;
  REQUIRE(cudaDeviceGetAttribute(&bus, cudaDevAttrGlobalMemoryBusWidth, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&l2, cudaDevAttrL2CacheSize, 0) == cudaSuccess);
  CHECK(bus == 128);
  CHECK(l2 == 32 * 1024 * 1024);
}

TEST_CASE("CUDA keeps its fixed bus width and L2 size for a target with no reference card", "[cuda][limits]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_37");
  int bus = 0;
  int l2 = 0;
  REQUIRE(cudaDeviceGetAttribute(&bus, cudaDevAttrGlobalMemoryBusWidth, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&l2, cudaDevAttrL2CacheSize, 0) == cudaSuccess);
  CHECK(bus == 128);
  CHECK(l2 == 4 * 1024 * 1024);
}

TEST_CASE("CUDA malloc heap limit defaults to 8 MiB and follows cuCtxSetLimit", "[cuda][limits]") {
  auto *const context = createContext();
  CHECK(heapLimit() == DefaultHeap);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_MALLOC_HEAP_SIZE, size_t{64} << 20) == CUDA_SUCCESS);
  CHECK(heapLimit() == (size_t{64} << 20));
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA malloc heap limit is per context", "[cuda][limits]") {
  auto *const first = createContext();
  REQUIRE(cuCtxSetLimit(CU_LIMIT_MALLOC_HEAP_SIZE, size_t{1} << 20) == CUDA_SUCCESS);
  auto *const second = createContext();
  CHECK(heapLimit() == DefaultHeap);
  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  CHECK(heapLimit() == (size_t{1} << 20));
  CHECK(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("CUDA malloc heap limit beyond the session allocation size is out of memory", "[cuda][limits]") {
  auto *const context = createContext();
  CHECK(cuCtxSetLimit(CU_LIMIT_MALLOC_HEAP_SIZE, size_t{4} << 30) == CUDA_ERROR_OUT_OF_MEMORY);
  CHECK(heapLimit() == DefaultHeap);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA runtime device limits reach the context limit", "[cuda][limits]") {
  size_t value = 0;
  REQUIRE(cudaDeviceGetLimit(&value, cudaLimitMallocHeapSize) == cudaSuccess);
  CHECK(value == DefaultHeap);
  REQUIRE(cudaDeviceSetLimit(cudaLimitMallocHeapSize, size_t{32} << 20) == cudaSuccess);
  REQUIRE(cudaDeviceGetLimit(&value, cudaLimitMallocHeapSize) == cudaSuccess);
  CHECK(value == (size_t{32} << 20));
  CHECK(cudaDeviceGetLimit(nullptr, cudaLimitMallocHeapSize) == cudaErrorInvalidValue);
}

TEST_CASE("CUDA reports the multiprocessors, clocks, memory, bus and L2 of the selected device preset", "[cuda][limits]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "GeForce RTX 5080");
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_120");
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  int multiprocessors = 0;
  int clockRate = 0;
  int memoryClockRate = 0;
  int bus = 0;
  int l2 = 0;
  REQUIRE(cudaDeviceGetAttribute(&multiprocessors, cudaDevAttrMultiProcessorCount, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&clockRate, cudaDevAttrClockRate, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&memoryClockRate, cudaDevAttrMemoryClockRate, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&bus, cudaDevAttrGlobalMemoryBusWidth, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&l2, cudaDevAttrL2CacheSize, 0) == cudaSuccess);
  CHECK(multiprocessors == 84);
  CHECK(clockRate == 2617000);
  CHECK(memoryClockRate == 15000000);
  CHECK(bus == 256);
  CHECK(l2 == 64 * 1024 * 1024);

  size_t bytes = 0;
  REQUIRE(cuDeviceTotalMem_v2(&bytes, 0) == CUDA_SUCCESS);
  CHECK(bytes == size_t{16} << 30);

  cudaDeviceProp properties{};
  REQUIRE(cudaGetDeviceProperties(&properties, 0) == cudaSuccess);
  CHECK(properties.multiProcessorCount == 84);
  CHECK(properties.totalGlobalMem == size_t{16} << 30);
}

TEST_CASE("CUDA device preset selects its target when GEMSTONE_TARGET_ARCH is unset", "[cuda][limits]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "GeForce RTX 5080");
  REQUIRE(cuInit(0) == CUDA_SUCCESS);
  int major = 0;
  int minor = 0;
  int multiprocessors = 0;
  REQUIRE(cudaDeviceGetAttribute(&major, cudaDevAttrComputeCapabilityMajor, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&minor, cudaDevAttrComputeCapabilityMinor, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&multiprocessors, cudaDevAttrMultiProcessorCount, 0) == cudaSuccess);
  CHECK(major == 12);
  CHECK(minor == 0);
  CHECK(multiprocessors == 84);
}

TEST_CASE("CUDA rejects an unknown device preset at initialisation", "[cuda][limits]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "GeForce RTX 9999");
  CHECK(cuInit(0) == CUDA_ERROR_NOT_INITIALIZED);
}

TEST_CASE("CUDA rejects a target that conflicts with the selected device preset", "[cuda][limits]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "GeForce RTX 5080");
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  CHECK(cuInit(0) == CUDA_ERROR_NOT_INITIALIZED);
}
