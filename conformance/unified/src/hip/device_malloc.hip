#include <cstdint>
#include <set>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "support.hpp"

using namespace hip_support;

namespace {

constexpr unsigned count = 64;
constexpr unsigned words = 16;
constexpr uint32_t failed = 0xffffffffu;
constexpr size_t defaultHeap = size_t{8} << 20;

__device__ unsigned globalThread() { return blockIdx.x * blockDim.x + threadIdx.x; }

__global__ void allocateFillSum(uint32_t *sums) {
  const unsigned tid = globalThread();
  auto *block = static_cast<uint32_t *>(malloc(words * sizeof(uint32_t)));
  if (!block) {
    sums[tid] = failed;
    return;
  }
  for (unsigned i = 0; i < words; ++i)
    block[i] = tid * words + i;
  uint32_t sum = 0;
  for (unsigned i = 0; i < words; ++i)
    sum += block[i];
  free(block);
  sums[tid] = sum;
}

__global__ void produce(uint32_t **slots) {
  const unsigned tid = globalThread();
  auto *block = static_cast<uint32_t *>(malloc(words * sizeof(uint32_t)));
  if (block)
    for (unsigned i = 0; i < words; ++i)
      block[i] = tid * 1000 + i;
  slots[tid] = block;
}

__global__ void consume(uint32_t **slots, uint32_t *sums) {
  const unsigned tid = globalThread();
  uint32_t *block = slots[tid];
  uint32_t sum = failed;
  if (block) {
    sum = 0;
    for (unsigned i = 0; i < words; ++i)
      sum += block[i];
    free(block);
  }
  slots[tid] = nullptr;
  sums[tid] = sum;
}

struct Tally {
  uint32_t value;
  uint32_t *destroyed;
  __device__ Tally(uint32_t seed, uint32_t *counter) : value(seed * 3 + 1), destroyed(counter) {}
  __device__ ~Tally() { atomicAdd(destroyed, 1u); }
};

__global__ void newDelete(uint32_t *results, uint32_t *destroyed) {
  const unsigned tid = globalThread();
  auto *tally = new Tally(tid, destroyed);
  results[tid] = tally ? tally->value : failed;
  delete tally;
}

__global__ void overlap(uint32_t *failures) {
  const unsigned tid = globalThread();
  auto *block = static_cast<uint32_t *>(malloc(words * sizeof(uint32_t)));
  if (block)
    for (unsigned i = 0; i < words; ++i)
      block[i] = tid;
  __syncthreads();
  uint32_t wrong = block ? 0 : 1;
  if (block)
    for (unsigned i = 0; i < words; ++i)
      wrong += block[i] != tid;
  if (wrong) atomicAdd(failures, wrong);
  __syncthreads();
  free(block);
}

__global__ void oversized(uint32_t *results, size_t size) {
  void *large = malloc(size);
  void *small = malloc(16);
  results[0] = large == nullptr;
  results[1] = small != nullptr;
  free(small);
}

std::vector<uint32_t> expectedSums() {
  std::vector<uint32_t> sums(count);
  for (unsigned tid = 0; tid < count; ++tid)
    sums[tid] = words * tid * words + words * (words - 1) / 2;
  return sums;
}

size_t heapLimit() {
  size_t value = 0;
  check(hipDeviceGetLimit(&value, hipLimitMallocHeapSize), "get heap limit");
  return value;
}

} // namespace

TEST_CASE("runtime/hip/device_malloc/malloc-free-within-launch", "[unified][runtime][memory]") {
  DeviceBuffer<uint32_t> sums(std::vector<uint32_t>(count, 0));
  allocateFillSum<<<1, count>>>(sums.data);
  finish("kernel");
  REQUIRE(sums.download() == expectedSums());
}

TEST_CASE("runtime/hip/device_malloc/block-outlives-launch", "[unified][runtime][memory]") {
  DeviceBuffer<uint32_t *> slots(std::vector<uint32_t *>(count, nullptr));
  DeviceBuffer<uint32_t> sums(std::vector<uint32_t>(count, 0));
  for (unsigned round = 0; round < 2; ++round) {
    produce<<<1, count>>>(slots.data);
    finish("produce");
    std::set<uint32_t *> distinct;
    for (uint32_t *block : slots.download()) {
      REQUIRE(block != nullptr);
      distinct.insert(block);
    }
    REQUIRE(distinct.size() == count);

    consume<<<1, count>>>(slots.data, sums.data);
    finish("consume");
    std::vector<uint32_t> expected(count);
    for (unsigned tid = 0; tid < count; ++tid)
      expected[tid] = tid * 1000 * words + words * (words - 1) / 2;
    REQUIRE(sums.download() == expected);
  }
}

TEST_CASE("runtime/hip/device_malloc/new-delete-object", "[unified][runtime][memory]") {
  DeviceBuffer<uint32_t> results(std::vector<uint32_t>(count, 0));
  DeviceBuffer<uint32_t> destroyed(std::vector<uint32_t>(1, 0));
  newDelete<<<1, count>>>(results.data, destroyed.data);
  finish("kernel");
  std::vector<uint32_t> expected(count);
  for (unsigned tid = 0; tid < count; ++tid)
    expected[tid] = tid * 3 + 1;
  REQUIRE(results.download() == expected);
  REQUIRE(destroyed.download() == std::vector<uint32_t>(1, count));
}

TEST_CASE("runtime/hip/device_malloc/concurrent-threads", "[unified][runtime][memory]") {
  DeviceBuffer<uint32_t> failures(std::vector<uint32_t>(1, 0));
  overlap<<<2, 64>>>(failures.data);
  finish("kernel");
  REQUIRE(failures.download() == std::vector<uint32_t>(1, 0));
}

TEST_CASE("runtime/hip/device_malloc/heap-limit-set-get", "[unified][runtime][memory]") {
  REQUIRE(heapLimit() == defaultHeap);
  check(hipDeviceSetLimit(hipLimitMallocHeapSize, size_t{16} << 20), "set");
  REQUIRE(heapLimit() == (size_t{16} << 20));
  hipDeviceProp_t properties{};
  check(hipGetDeviceProperties(&properties, 0), "properties");
  REQUIRE(hipDeviceSetLimit(hipLimitMallocHeapSize, properties.totalGlobalMem) == hipErrorInvalidValue);
  (void)hipGetLastError();
  REQUIRE(heapLimit() == (size_t{16} << 20));
  check(hipDeviceSetLimit(hipLimitMallocHeapSize, defaultHeap), "restore");
}

TEST_CASE("runtime/hip/device_malloc/null-for-oversized-request", "[unified][runtime][memory]") {
  DeviceBuffer<uint32_t> results(std::vector<uint32_t>(2, 0));
  oversized<<<1, 1>>>(results.data, size_t{1} << 40);
  finish("kernel");
  REQUIRE(results.download() == std::vector<uint32_t>{1, 1});
}

TEST_CASE("runtime/hip/device_malloc/limit-set-after-use", "[unified][runtime][memory]") {
  DeviceBuffer<uint32_t> sums(std::vector<uint32_t>(count, 0));
  allocateFillSum<<<1, count>>>(sums.data);
  finish("kernel");
  REQUIRE(sums.download() == expectedSums());

  check(hipDeviceSetLimit(hipLimitMallocHeapSize, size_t{24} << 20), "set after use");
  REQUIRE(heapLimit() == (size_t{24} << 20));

  allocateFillSum<<<1, count>>>(sums.data);
  finish("second");
  REQUIRE(sums.download() == expectedSums());
  check(hipDeviceSetLimit(hipLimitMallocHeapSize, defaultHeap), "restore");
}
