#include <cstddef>
#include <iterator>
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

size_t limit(CUlimit id) {
  size_t value = 0;
  REQUIRE(cuCtxGetLimit(&value, id) == CUDA_SUCCESS);
  return value;
}

size_t heapLimit() { return limit(CU_LIMIT_MALLOC_HEAP_SIZE); }

CUfunc_cache cacheConfig() {
  auto config = static_cast<CUfunc_cache>(-1);
  REQUIRE(cuCtxGetCacheConfig(&config) == CUDA_SUCCESS);
  return config;
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

TEST_CASE("CUDA cache config defaults to prefer none and reads back each accepted value", "[cuda][limits]") {
  auto *const context = createContext();
  CHECK(cacheConfig() == CU_FUNC_CACHE_PREFER_NONE);
  for (const auto config : {CU_FUNC_CACHE_PREFER_NONE, CU_FUNC_CACHE_PREFER_SHARED, CU_FUNC_CACHE_PREFER_L1, CU_FUNC_CACHE_PREFER_EQUAL}) {
    REQUIRE(cuCtxSetCacheConfig(config) == CUDA_SUCCESS);
    CHECK(cacheConfig() == config);
  }
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA cache config rejects values outside its enum and keeps the stored value", "[cuda][limits]") {
  auto *const context = createContext();
  REQUIRE(cuCtxSetCacheConfig(CU_FUNC_CACHE_PREFER_L1) == CUDA_SUCCESS);
  CHECK(cuCtxSetCacheConfig(static_cast<CUfunc_cache>(4)) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuCtxSetCacheConfig(static_cast<CUfunc_cache>(-1)) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cacheConfig() == CU_FUNC_CACHE_PREFER_L1);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA cache config is per context", "[cuda][limits]") {
  auto *const first = createContext();
  REQUIRE(cuCtxSetCacheConfig(CU_FUNC_CACHE_PREFER_L1) == CUDA_SUCCESS);
  auto *const second = createContext();
  CHECK(cacheConfig() == CU_FUNC_CACHE_PREFER_NONE);
  REQUIRE(cuCtxSetCacheConfig(CU_FUNC_CACHE_PREFER_EQUAL) == CUDA_SUCCESS);
  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  CHECK(cacheConfig() == CU_FUNC_CACHE_PREFER_L1);
  CHECK(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("CUDA shared memory config reads four byte banks and accepts the first three values", "[cuda][limits]") {
  auto *const context = createContext();
  CUsharedconfig config = CU_SHARED_MEM_CONFIG_DEFAULT_BANK_SIZE;
  REQUIRE(cuCtxGetSharedMemConfig(&config) == CUDA_SUCCESS);
  CHECK(config == CU_SHARED_MEM_CONFIG_FOUR_BYTE_BANK_SIZE);
  for (const auto accepted : {0, 1, 2}) {
    REQUIRE(cuCtxSetSharedMemConfig(static_cast<CUsharedconfig>(accepted)) == CUDA_SUCCESS);
    REQUIRE(cuCtxGetSharedMemConfig(&config) == CUDA_SUCCESS);
    CHECK(config == CU_SHARED_MEM_CONFIG_FOUR_BYTE_BANK_SIZE);
  }
  for (const auto rejected : {3, -1, 4}) {
    CHECK(cuCtxSetSharedMemConfig(static_cast<CUsharedconfig>(rejected)) == CUDA_ERROR_INVALID_VALUE);
  }
  REQUIRE(cuCtxGetSharedMemConfig(&config) == CUDA_SUCCESS);
  CHECK(config == CU_SHARED_MEM_CONFIG_FOUR_BYTE_BANK_SIZE);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA context limits default to the values the driver reports", "[cuda][limits]") {
  auto *const context = createContext();
  CHECK(limit(CU_LIMIT_STACK_SIZE) == 1024);
  CHECK(limit(CU_LIMIT_PRINTF_FIFO_SIZE) == 1671168);
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH) == 2);
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_PENDING_LAUNCH_COUNT) == 2048);
  CHECK(limit(CU_LIMIT_MAX_L2_FETCH_GRANULARITY) == 64);
  CHECK(limit(CU_LIMIT_PERSISTING_L2_CACHE_SIZE) == 6291456);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA context limits read back the value set", "[cuda][limits]") {
  auto *const context = createContext();
  REQUIRE(cuCtxSetLimit(CU_LIMIT_STACK_SIZE, 2048) == CUDA_SUCCESS);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_PRINTF_FIFO_SIZE, size_t{2} << 20) == CUDA_SUCCESS);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_DEV_RUNTIME_PENDING_LAUNCH_COUNT, 4096) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_STACK_SIZE) == 2048);
  CHECK(limit(CU_LIMIT_PRINTF_FIFO_SIZE) == (size_t{2} << 20));
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_PENDING_LAUNCH_COUNT) == 4096);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA query-only context limits report the driver's values and reject sets", "[cuda][limits]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  CHECK(limit(CU_LIMIT_SHMEM_SIZE) == 101376);
  CHECK(limit(CU_LIMIT_CIG_ENABLED) == 0);
  CHECK(cuCtxSetLimit(CU_LIMIT_SHMEM_SIZE, 1) == CUDA_ERROR_NOT_PERMITTED);
  CHECK(cuCtxSetLimit(CU_LIMIT_CIG_ENABLED, 1) == CUDA_ERROR_NOT_PERMITTED);
  CHECK(limit(CU_LIMIT_SHMEM_SIZE) == 101376);
  CHECK(limit(CU_LIMIT_CIG_ENABLED) == 0);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA context limits replay the probe sweep of the RTX 4060 Ti driver", "[cuda][limits]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  constexpr size_t defaults[] = {1024, 1671168, 8388608, 2, 2048, 64, 6291456, 101376, 0};
  for (size_t id = 0; id < std::size(defaults); ++id) {
    CHECK(limit(static_cast<CUlimit>(id)) == defaults[id]);
  }
  size_t value = 0;
  CHECK(cuCtxGetLimit(&value, static_cast<CUlimit>(9)) == CUDA_ERROR_UNSUPPORTED_LIMIT);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_STACK_SIZE, 4096) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_STACK_SIZE) == 4096);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_MALLOC_HEAP_SIZE, DefaultHeap) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_MALLOC_HEAP_SIZE) == DefaultHeap);
  CHECK(cuCtxSetLimit(static_cast<CUlimit>(99), 1) == CUDA_ERROR_INVALID_VALUE);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH, 1) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH) == 1);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH, 3) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH) == 3);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA context limits replay the probe's shared memory and pending launch sequence", "[cuda][limits]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  CHECK(limit(CU_LIMIT_SHMEM_SIZE) == 101376);
  CHECK(cuCtxSetLimit(CU_LIMIT_SHMEM_SIZE, 1) == CUDA_ERROR_NOT_PERMITTED);
  CHECK(cuCtxSetLimit(CU_LIMIT_CIG_ENABLED, 1) == CUDA_ERROR_NOT_PERMITTED);
  CHECK(limit(CU_LIMIT_CIG_ENABLED) == 0);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_STACK_SIZE, 0) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_STACK_SIZE) == 0);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_STACK_SIZE, 1024) == CUDA_SUCCESS);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_DEV_RUNTIME_PENDING_LAUNCH_COUNT, 1) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_PENDING_LAUNCH_COUNT) == 32);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA shared memory attributes come from the selected target's description", "[cuda][limits]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_89");
  auto *const context = createContext();
  int optin = 0;
  int perSm = 0;
  REQUIRE(cudaDeviceGetAttribute(&optin, cudaDevAttrMaxSharedMemoryPerBlockOptin, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&perSm, cudaDevAttrMaxSharedMemoryPerMultiprocessor, 0) == cudaSuccess);
  CHECK(optin == 101376);
  CHECK(perSm == 102400);
  CHECK(limit(CU_LIMIT_SHMEM_SIZE) == static_cast<size_t>(optin));
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA shared memory attributes follow a smaller target's description", "[cuda][limits]") {
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_75");
  auto *const context = createContext();
  int optin = 0;
  int perSm = 0;
  REQUIRE(cudaDeviceGetAttribute(&optin, cudaDevAttrMaxSharedMemoryPerBlockOptin, 0) == cudaSuccess);
  REQUIRE(cudaDeviceGetAttribute(&perSm, cudaDevAttrMaxSharedMemoryPerMultiprocessor, 0) == cudaSuccess);
  CHECK(optin == 65536);
  CHECK(perSm == 65536);
  CHECK(limit(CU_LIMIT_SHMEM_SIZE) == static_cast<size_t>(optin));
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA stack and device sync depth limits are per context", "[cuda][limits]") {
  auto *const first = createContext();
  REQUIRE(cuCtxSetLimit(CU_LIMIT_STACK_SIZE, 2048) == CUDA_SUCCESS);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH, 5) == CUDA_SUCCESS);
  auto *const second = createContext();
  CHECK(limit(CU_LIMIT_STACK_SIZE) == 1024);
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH) == 2);
  REQUIRE(cuCtxSetLimit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH, 7) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH) == 7);
  REQUIRE(cuCtxSetCurrent(first) == CUDA_SUCCESS);
  CHECK(limit(CU_LIMIT_STACK_SIZE) == 2048);
  CHECK(limit(CU_LIMIT_DEV_RUNTIME_SYNC_DEPTH) == 5);
  CHECK(cuCtxDestroy_v2(second) == CUDA_SUCCESS);
  CHECK(cuCtxDestroy_v2(first) == CUDA_SUCCESS);
}

TEST_CASE("CUDA context limit CIG shared memory fallback is unsupported", "[cuda][limits]") {
  auto *const context = createContext();
  size_t value = 0;
  CHECK(cuCtxGetLimit(&value, CU_LIMIT_CIG_SHMEM_FALLBACK_ENABLED) == CUDA_ERROR_UNSUPPORTED_LIMIT);
  CHECK(cuCtxSetLimit(CU_LIMIT_CIG_SHMEM_FALLBACK_ENABLED, 1) == CUDA_ERROR_UNSUPPORTED_LIMIT);
  CHECK(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}

TEST_CASE("CUDA context limit ids beyond the driver's enum are invalid values", "[cuda][limits]") {
  auto *const context = createContext();
  const auto unknown = static_cast<CUlimit>(99);
  size_t value = 0;
  CHECK(cuCtxGetLimit(&value, unknown) == CUDA_ERROR_INVALID_VALUE);
  CHECK(cuCtxSetLimit(unknown, 1) == CUDA_ERROR_INVALID_VALUE);
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

  char name[64]{};
  REQUIRE(cuDeviceGetName(name, sizeof(name), 0) == CUDA_SUCCESS);
  CHECK(std::string(name) == "GeForce RTX 5080");

  cudaDeviceProp properties{};
  REQUIRE(cudaGetDeviceProperties(&properties, 0) == cudaSuccess);
  CHECK(std::string(properties.name) == "GeForce RTX 5080");
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

TEST_CASE("CUDA occupancy calculator scales grid size with selected device preset multiprocessors", "[cuda][limits]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "GeForce RTX 5080");
  const gemstone_test::Environment target(gemstone::environment::TargetArch, "sm_120");
  auto *const context = createContext();
  CUmodule module = nullptr;
  const std::string ptx = ".version 9.3\n.target sm_120\n.address_size 64\n.visible .entry kern() { ret; }\n";
  REQUIRE(cuModuleLoadData(&module, ptx.c_str()) == CUDA_SUCCESS);
  CUfunction function = nullptr;
  REQUIRE(cuModuleGetFunction(&function, module, "kern") == CUDA_SUCCESS);
  int minGridSize = 0;
  int blockSize = 0;
  REQUIRE(cuOccupancyMaxPotentialBlockSize(&minGridSize, &blockSize, function, nullptr, 0, 0) == CUDA_SUCCESS);
  CHECK(blockSize > 0);
  CHECK(minGridSize > 0);
  CHECK(minGridSize % 84 == 0);
  REQUIRE(cuModuleUnload(module) == CUDA_SUCCESS);
  REQUIRE(cuCtxDestroy_v2(context) == CUDA_SUCCESS);
}
