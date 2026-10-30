#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <string_view>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_bf16.h>
#include <cuda_fp16.h>

#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;

// atomicAdd on __half, __half2, __nv_bfloat16 and __nv_bfloat162, whose headers emit atom.add.noftz.{f16,f16x2,bf16,bf16x2}
// on generic addresses (or a compare-and-swap loop below sm_90 for bfloat16), and integer and float atomics that reach
// shared memory through generic addresses.
namespace {

constexpr uint32_t Blocks = 2, Threads = 128;

// Bit 0: binary16 atomics compiled in (sm_70 and later), bit 1: bfloat16 atomics (sm_80 and later).
__global__ void support(uint32_t *flags) {
  uint32_t value = 0;
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
  value |= 1U;
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800
  value |= 2U;
#endif
  *flags = value;
}

uint32_t supported() {
  DeviceBuffer<uint32_t> flags(std::vector<uint32_t>{0U});
  support<<<1, 1>>>(flags.data);
  finish("support");
  return flags.download()[0];
}

// Host references from the conversions CUDA 11.8 also offers on the host.
uint32_t halfBits(float value) { return __half_raw(__float2half_rn(value)).x; }
uint32_t bfloat(float value) { return __nv_bfloat16_raw(__float2bfloat16_rn(value)).x; }
uint32_t halves(float low, float high) { return halfBits(low) | halfBits(high) << 16; }
uint32_t bfloats(float low, float high) { return bfloat(low) | bfloat(high) << 16; }

// Counters: [0] __half2, [1] __nv_bfloat162, [2] __half in the low half, [3] __nv_bfloat16 in the low half.
__device__ __noinline__ void accumulate([[maybe_unused]] uint32_t *counters, [[maybe_unused]] uint32_t *previous) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
  const __half2_raw old = atomicAdd(reinterpret_cast<__half2 *>(&counters[0]), __halves2half2(__float2half(1.0F), __float2half(0.5F)));
  uint32_t word = 0;
  memcpy(&word, &old, sizeof(word));
  *previous = word;
  atomicAdd(reinterpret_cast<__half *>(&counters[2]), __float2half(1.0F));
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800
  atomicAdd(reinterpret_cast<__nv_bfloat162 *>(&counters[1]), __floats2bfloat162_rn(1.0F, 0.25F));
  atomicAdd(reinterpret_cast<__nv_bfloat16 *>(&counters[3]), __float2bfloat16(1.0F));
#endif
}

__global__ void halves(uint32_t *global, uint32_t *perBlock, uint32_t *previous) {
  __shared__ uint32_t local[4];
  if (threadIdx.x < 4) local[threadIdx.x] = 0;
  __syncthreads();
  const uint32_t thread = (blockIdx.x * blockDim.x) + threadIdx.x;
  accumulate(global, &previous[thread]);
  accumulate(local, &previous[(Blocks * Threads) + thread]);
  __syncthreads();
  if (threadIdx.x < 4) perBlock[(blockIdx.x * 4) + threadIdx.x] = local[threadIdx.x];
}

} // namespace

TEST_CASE("atomics/cuda/half-precision", "[unified][atomics]") {
  const uint32_t flags = supported();
  if (!(flags & 1U)) SKIP("binary16 atomicAdd requires compute capability 7.0");
  DeviceBuffer<uint32_t> global(std::vector<uint32_t>(4));
  DeviceBuffer<uint32_t> perBlock(std::vector<uint32_t>(static_cast<size_t>(Blocks) * 4));
  DeviceBuffer<uint32_t> previous(std::vector<uint32_t>(2 * static_cast<size_t>(Blocks) * Threads));
  halves<<<Blocks, Threads>>>(global.data, perBlock.data, previous.data);
  finish("half-precision atomics");
  const auto total = global.download();
  const auto blocks = perBlock.download();
  const auto olds = previous.download();
  const auto expect = [](uint32_t actual, uint32_t expected, std::string_view what) {
    if (actual != expected) throw std::runtime_error(fmt::format("{}: expected {:#x}, got {:#x}", what, expected, actual));
  };
  const uint32_t all = Blocks * Threads;
  expect(total[0], halves(static_cast<float>(all), 0.5F * all), "global __half2");
  expect(total[2] & 0xffffU, halfBits(static_cast<float>(all)), "global __half");
  for (size_t block = 0; block < Blocks; ++block) {
    expect(blocks[block * 4], halves(static_cast<float>(Threads), 0.5F * Threads), fmt::format("shared __half2 of block {}", block));
    expect(blocks[(block * 4) + 2] & 0xffffU, halfBits(static_cast<float>(Threads)), fmt::format("shared __half {}", block));
  }
  // Each fetch is one of the running totals, each exactly once.
  std::vector<uint32_t> counts(all);
  for (uint32_t thread = 0; thread < all; ++thread) {
    __half_raw raw;
    raw.x = static_cast<unsigned short>(olds[thread] & 0xffffU);
    const float value = __half2float(__half(raw));
    const uint32_t step = static_cast<uint32_t>(value);
    if (static_cast<float>(step) != value || step >= all || counts[step]++ != 0)
      throw std::runtime_error(fmt::format("global __half2 fetch {} of thread {} is not a distinct running total", value, thread));
  }
  if (flags & 2U) {
    expect(total[1], bfloats(static_cast<float>(all), 0.25F * all), "global __nv_bfloat162");
    expect(total[3] & 0xffffU, bfloat(static_cast<float>(all)), "global __nv_bfloat16");
    for (uint32_t block = 0; block < Blocks; ++block) {
      expect(blocks[(block * 4) + 1], bfloats(static_cast<float>(Threads), 0.25F * Threads),
             fmt::format("shared __nv_bfloat162 of block {}", block));
      expect(blocks[(block * 4) + 3] & 0xffffU, bfloat(static_cast<float>(Threads)),
             fmt::format("shared __nv_bfloat16 of block {}", block));
    }
  }
}

namespace {
// A generic pointer chosen at run time, so the compiler cannot specialise the atomics to one state space.
__device__ __noinline__ void contend(uint32_t *counters, float *sum, uint32_t value) {
  atomicAdd(&counters[0], value);
  atomicMax(&counters[1], value);
  atomicMin(&counters[2], value);
  atomicOr(&counters[3], 1U << (value & 31U));
  uint32_t observed = counters[4];
  for (uint32_t assumed = observed + 1U; (observed = atomicCAS(&counters[4], assumed - 1U, assumed)) != assumed - 1U;)
    assumed = observed + 1U;
  atomicAdd(sum, 0.25F);
  atomicExch(&counters[5], 7U);
}

__global__ void generic(uint32_t *global, float *globalSum, uint32_t *out) {
  __shared__ uint32_t counters[6];
  __shared__ float sum;
  if (threadIdx.x == 0) {
    for (uint32_t index = 0; index < 6; ++index)
      counters[index] = index == 2 ? 0xffffffffU : 0U;
    sum = 0.0F;
  }
  __syncthreads();
  const bool local = (blockIdx.x & 1U) == 0;
  contend(local ? counters : global, local ? &sum : globalSum, threadIdx.x + 1U);
  __syncthreads();
  if (threadIdx.x < 6) out[(blockIdx.x * 7) + threadIdx.x] = counters[threadIdx.x];
  if (threadIdx.x == 6) memcpy(&out[(blockIdx.x * 7) + 6], &sum, sizeof(float));
}

} // namespace

TEST_CASE("atomics/cuda/generic-shared", "[unified][atomics]") {
  constexpr uint32_t blocks = 4;
  DeviceBuffer<uint32_t> global(std::vector<uint32_t>{0U, 0U, 0xffffffffU, 0U, 0U, 0U});
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(blocks) * 7));
  DeviceBuffer<float> sum(std::vector<float>{0.0F});
  generic<<<blocks, Threads>>>(global.data, sum.data, out.data);
  finish("generic atomics");
  const auto mask = iota(1U, Threads + 1) | fold_left(0U, [](uint32_t bits, uint32_t value) { return bits | (1U << (value & 31U)); });
  const std::vector<uint32_t> expected = {Threads * (Threads + 1) / 2, Threads, 1U, mask, Threads, 7U};
  const auto shared = out.download();
  for (size_t block = 0; block < blocks; block += 2) {
    for (size_t index = 0; index < expected.size(); ++index)
      if (shared[(block * 7) + index] != expected[index])
        throw std::runtime_error(fmt::format("shared counter {} of block {}: expected {:#x}, got {:#x}", index, block, expected[index],
                                             shared[(block * 7) + index]));
    const float value = unified::support::bitsFloat(shared[(block * 7) + 6]);
    if (value != 0.25F * Threads) throw std::runtime_error(fmt::format("shared float sum of block {}: {}", block, value));
  }
  const auto counters = global.download();
  constexpr uint32_t globalBlocks = blocks / 2;
  const std::vector<uint32_t> combined = {expected[0] * globalBlocks, Threads, 1U, mask, Threads * globalBlocks, 7U};
  for (size_t index = 0; index < combined.size(); ++index)
    if (counters[index] != combined[index])
      throw std::runtime_error(fmt::format("global counter {}: expected {:#x}, got {:#x}", index, combined[index], counters[index]));
  if (sum.download()[0] != 0.25F * Threads * globalBlocks) throw std::runtime_error("global float sum mismatch");
}
