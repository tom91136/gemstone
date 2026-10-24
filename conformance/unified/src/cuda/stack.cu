#include <cstdint>
#include <stdexcept>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime_api.h>

#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;

// alloca in device code: nvcc and clang emit PTX alloca, and stacksave and stackrestore around an inlined callee that
// allocates; ptxas lowers them to arithmetic on the stack pointer.
namespace {

// PTX alloca requires sm_52, and nvcc supports it from CUDA 12.
#if defined(__clang__) || (defined(__CUDACC_VER_MAJOR__) && __CUDACC_VER_MAJOR__ >= 12)
  #define CUDA_STACK_ALLOCA 1
#endif

__global__ void support(uint32_t *flag) {
#if defined(CUDA_STACK_ALLOCA) && defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 520
  *flag = 1U;
#else
  *flag = 0U;
#endif
}

bool supported() {
  DeviceBuffer<uint32_t> flag(std::vector<uint32_t>{0U});
  support<<<1, 1>>>(flag.data);
  finish("support");
  return flag.download()[0] != 0U;
}

constexpr uint32_t Threads = 64;

uint32_t words(uint32_t thread) { return 4U + ((thread * 7U) % 29U); }

#if defined(CUDA_STACK_ALLOCA) && defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 520
// The callee reads the allocation through a generic pointer.
__device__ __noinline__ uint32_t weigh(const uint32_t *values, uint32_t count) {
  uint32_t sum = 0;
  for (uint32_t index = 0; index < count; ++index)
    sum += values[index] * (index + 1U);
  return sum;
}
#endif

__global__ void dynamic(const uint32_t *in, uint32_t *out) {
#if defined(CUDA_STACK_ALLOCA) && defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 520
  const uint32_t count = 4U + ((threadIdx.x * 7U) % 29U);
  auto *first = static_cast<uint32_t *>(__builtin_alloca(count * sizeof(uint32_t)));
  auto *second = static_cast<uint64_t *>(__builtin_alloca((count + 1U) * sizeof(uint64_t)));
  for (uint32_t index = 0; index < count; ++index) {
    first[index] = in[(threadIdx.x + index) % Threads] ^ index;
    second[index] = static_cast<uint64_t>(first[index]) << 32;
  }
  second[count] = reinterpret_cast<uintptr_t>(first) & 7U;
  uint64_t high = 0;
  for (uint32_t index = 0; index < count; ++index)
    high += second[index] >> 32;
  out[static_cast<size_t>(threadIdx.x) * 3] = weigh(first, count);
  out[(threadIdx.x * 3) + 1] = static_cast<uint32_t>(high);
  out[(threadIdx.x * 3) + 2] = static_cast<uint32_t>(second[count]);
#endif
}

uint32_t dynamicWeight(const std::vector<uint32_t> &in, uint32_t thread) {
  return iota(0U, words(thread)) | sum_by([&](uint32_t index) { return (in[(thread + index) % Threads] ^ index) * (index + 1U); });
}

uint32_t dynamicHigh(const std::vector<uint32_t> &in, uint32_t thread) {
  return iota(0U, words(thread)) | sum_by([&](uint32_t index) { return in[(thread + index) % Threads] ^ index; });
}

} // namespace

TEST_CASE("stack/cuda/alloca", "[unified][stack]") {
  if (!supported()) SKIP("alloca requires sm_52 and CUDA 12 or clang");
  check(cudaDeviceSetLimit(cudaLimitStackSize, 8192), "stack limit");
  const auto inputs = iota(0U, Threads) | map([](uint32_t index) { return index * 0x9e3779b9U; }) | to_vector();
  DeviceBuffer<uint32_t> in(inputs);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(Threads) * 3));
  dynamic<<<2, Threads>>>(in.data, out.data);
  finish("dynamic allocation");
  const auto actual = out.download();
  for (uint32_t thread = 0; thread < Threads; ++thread) {
    const std::vector<uint32_t> expected = {dynamicWeight(inputs, thread), dynamicHigh(inputs, thread), 0U};
    for (uint32_t field = 0; field < 3; ++field)
      if (actual[(thread * 3) + field] != expected[field])
        throw std::runtime_error(
            fmt::format("alloca {} of thread {}: expected {:#x}, got {:#x}", field, thread, expected[field], actual[(thread * 3) + field]));
  }
}

namespace {
// Inlined into the loop, each round's allocation is released with stackrestore before the next.
__device__ __forceinline__ uint32_t scratch(const uint32_t *in, uint32_t count, uint32_t salt) {
#if defined(CUDA_STACK_ALLOCA) && defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 520
  #ifdef __clang__
  // ptxas merges clang's 8-byte local stores into 16-byte ones, which fault on hardware below 16-byte alignment.
  auto *values = static_cast<uint32_t *>(__builtin_alloca_with_align(count * sizeof(uint32_t), 128));
  #else
  auto *values = static_cast<uint32_t *>(__builtin_alloca(count * sizeof(uint32_t)));
  #endif
  for (uint32_t index = 0; index < count; ++index)
    values[index] = in[index] ^ salt;
  uint32_t sum = 0;
  for (uint32_t index = 0; index < count; ++index)
    sum += values[count - 1U - index] * (index + 1U);
  return sum;
#else
  return 0U;
#endif
}

__global__ void scoped(const uint32_t *in, uint32_t *out, uint32_t rounds) {
  uint32_t total = 0;
  for (uint32_t round = 0; round < rounds; ++round)
    total += scratch(in, 8U + ((threadIdx.x + round) % 9U), round);
  out[threadIdx.x] = total;
}

} // namespace

TEST_CASE("stack/cuda/scoped", "[unified][stack]") {
  if (!supported()) SKIP("alloca requires sm_52 and CUDA 12 or clang");
  check(cudaDeviceSetLimit(cudaLimitStackSize, 8192), "stack limit");
  constexpr uint32_t rounds = 200;
  const auto inputs = iota(0U, 16U) | map([](uint32_t index) { return (index * 0x01000193U) + 5U; }) | to_vector();
  DeviceBuffer<uint32_t> in(inputs);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(Threads, 0U));
  scoped<<<1, Threads>>>(in.data, out.data, rounds);
  finish("scoped allocation");
  const auto actual = out.download();
  for (uint32_t thread = 0; thread < Threads; ++thread) {
    const auto total =
        iota(0U, rounds) | sum_by([&](uint32_t round) {
          const uint32_t count = 8U + ((thread + round) % 9U);
          return iota(0U, count) | sum_by([&](uint32_t index) { return (inputs[count - 1U - index] ^ round) * (index + 1U); });
        });
    if (actual[thread] != total)
      throw std::runtime_error(fmt::format("scoped alloca of thread {}: expected {:#x}, got {:#x}", thread, total, actual[thread]));
  }
}
