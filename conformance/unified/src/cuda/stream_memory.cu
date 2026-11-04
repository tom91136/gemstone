#include <cstdint>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime.h>

using namespace aspartame;

namespace {

int currentDevice() {
  int device = 0;
  check(cudaGetDevice(&device), "device");
  return device;
}

bool memoryPoolsSupported(int device) {
  int supported = 0;
  check(cudaDeviceGetAttribute(&supported, cudaDevAttrMemoryPoolsSupported, device), "memory pools attribute");
  return supported != 0;
}

} // namespace

TEST_CASE("runtime/cuda/stream-ordered-allocation", "[unified][runtime][async]") {
  const int device = currentDevice();
  if (!memoryPoolsSupported(device)) SKIP("the device has no stream-ordered memory allocator");
  constexpr size_t count = 64;
  constexpr size_t bytes = count * sizeof(uint32_t);
  const auto values = iota(size_t{0}, count) | map([](size_t i) { return static_cast<uint32_t>(0x4000 + (i * 7)); }) | to_vector();

  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  void *first = nullptr;
  void *second = nullptr;
  void *pooled = nullptr;
  check(cudaMallocAsync(&first, bytes, stream), "cudaMallocAsync");
  check(cudaMallocAsync(&second, bytes, stream), "cudaMallocAsync second");
  cudaMemPool_t pool = nullptr;
  check(cudaDeviceGetDefaultMemPool(&pool, device), "default memory pool");
  check(cudaMallocFromPoolAsync(&pooled, bytes, pool, stream), "cudaMallocFromPoolAsync");
  check(cudaMemcpyAsync(first, values.data(), bytes, cudaMemcpyHostToDevice, stream), "upload");
  check(cudaMemcpyAsync(second, first, bytes, cudaMemcpyDeviceToDevice, stream), "device copy");
  check(cudaMemcpyAsync(pooled, second, bytes, cudaMemcpyDeviceToDevice, stream), "pool copy");
  std::vector<uint32_t> actual(count, 0);
  check(cudaMemcpyAsync(actual.data(), pooled, bytes, cudaMemcpyDeviceToHost, stream), "download");
  check(cudaFreeAsync(first, stream), "free first");
  check(cudaFreeAsync(second, stream), "free second");
  check(cudaFreeAsync(pooled, stream), "free pooled");
  check(cudaStreamSynchronize(stream), "stream synchronize");
  check(cudaStreamDestroy(stream), "stream destroy");
  expectEqual(actual, values, "stream-ordered round trip");
}

TEST_CASE("runtime/cuda/memory-pools", "[unified][runtime][async]") {
  const int device = currentDevice();
  if (!memoryPoolsSupported(device)) SKIP("the device has no stream-ordered memory allocator");
  cudaMemPoolProps props{};
  props.allocType = cudaMemAllocationTypePinned;
  props.handleTypes = cudaMemHandleTypeNone;
  props.location.type = cudaMemLocationTypeDevice;
  props.location.id = device;
  cudaMemPool_t pool = nullptr;
  check(cudaMemPoolCreate(&pool, &props), "pool create");

  uint64_t threshold = ~uint64_t{0}; // finite thresholds let the driver release the whole backing chunk on free
  check(cudaMemPoolSetAttribute(pool, cudaMemPoolAttrReleaseThreshold, &threshold), "set release threshold");
  uint64_t observed = 0;
  check(cudaMemPoolGetAttribute(pool, cudaMemPoolAttrReleaseThreshold, &observed), "get release threshold");
  REQUIRE(observed == threshold);

  constexpr size_t bytes = size_t{256} << 10;
  cudaStream_t stream = nullptr;
  check(cudaStreamCreate(&stream), "stream");
  void *block = nullptr;
  check(cudaMallocFromPoolAsync(&block, bytes, pool, stream), "pool allocation");
  check(cudaStreamSynchronize(stream), "stream synchronize");
  uint64_t used = 0;
  check(cudaMemPoolGetAttribute(pool, cudaMemPoolAttrUsedMemCurrent, &used), "used memory");
  REQUIRE(used >= bytes);

  check(cudaFreeAsync(block, stream), "pool free");
  check(cudaStreamSynchronize(stream), "stream synchronize");
  uint64_t reserved = 0;
  check(cudaMemPoolGetAttribute(pool, cudaMemPoolAttrReservedMemCurrent, &reserved), "reserved memory");
  REQUIRE(reserved >= bytes);
  uint64_t highWater = 0;
  check(cudaMemPoolGetAttribute(pool, cudaMemPoolAttrUsedMemHigh, &highWater), "used high water");
  REQUIRE(highWater >= bytes);
  uint64_t usedAfterFree = 0;
  check(cudaMemPoolGetAttribute(pool, cudaMemPoolAttrUsedMemCurrent, &usedAfterFree), "used memory after free");
  REQUIRE(usedAfterFree == 0);

  check(cudaMemPoolTrimTo(pool, 0), "trim");
  uint64_t reservedAfterTrim = 0;
  check(cudaMemPoolGetAttribute(pool, cudaMemPoolAttrReservedMemCurrent, &reservedAfterTrim), "reserved memory after trim");
  REQUIRE(reservedAfterTrim == 0);

  cudaMemPool_t defaultPool = nullptr;
  check(cudaDeviceGetDefaultMemPool(&defaultPool, device), "default memory pool");
  REQUIRE(defaultPool != pool);

  check(cudaStreamDestroy(stream), "stream destroy");
  check(cudaMemPoolDestroy(pool), "pool destroy");
}
