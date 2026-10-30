#include <algorithm>
#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#if !defined(__CUDA_ARCH__) || __CUDA_ARCH__ >= 700
  #include <cuda/atomic>
#endif
#include "cuda/support.cuh"
#include <cuda_runtime_api.h>

#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;

// Atomics of every operation and width on global, shared and generic addresses, scoped and ordered atomics, named
// block barriers with counts, mbarrier phases, and loads and stores of every width and cache operator, as ordinary CUDA
// C++ compiles them.
namespace {

template <typename T> void expect(const T &actual, const T &expected, std::string_view what) {
  if (actual != expected) throw std::runtime_error(fmt::format("{}: expected {}, got {}", what, expected, actual));
}

constexpr uint32_t Blocks = 4, Threads = 128, Total = Blocks * Threads;

// Word slots of the integer counters, in global memory and in each block's shared copy.
constexpr uint32_t Add = 0, Sub = 1, MinS = 2, MaxS = 3, And = 4, Or = 5, Xor = 6, Exch = 7, IncWrap = 8, DecWrap = 9, MinU = 10, MaxU = 11,
                   Cas = 12, Ones = 13, Slots = 14;
// Doubleword slots.
constexpr uint32_t WAdd = 0, WMinU = 1, WMaxU = 2, WAnd = 3, WOr = 4, WXor = 5, WExch = 6, WCas = 7, WMinS = 8, WMaxS = 9, WSlots = 10;

__host__ __device__ inline int32_t spread(uint32_t i) { return static_cast<int32_t>(i * 37U % 101U) - 50; }
__host__ __device__ inline uint32_t hash(uint32_t i) { return i * 2654435761U; }
__host__ __device__ inline uint64_t hash64(uint32_t i) { return (static_cast<uint64_t>(hash(i)) << 29) ^ i; }

std::vector<uint32_t> initialWords() {
  std::vector<uint32_t> words(Slots, 0U);
  words[MinS] = 0x7fffffffU;
  words[MaxS] = 0x80000000U;
  words[And] = 0xffffffffU;
  words[MinU] = 0xffffffffU;
  return words;
}

std::vector<uint64_t> initialDoubles() {
  std::vector<uint64_t> words(WSlots, 0U);
  words[WMinU] = ~0ULL;
  words[WAnd] = ~0ULL;
  words[WMinS] = 0x7fffffffffffffffULL;
  words[WMaxS] = 0x8000000000000000ULL;
  return words;
}

// `index` counts the threads that contend, `value` is this thread's operand; the results that are known only as a set
// go to `fetched`.
__device__ void integerOps(uint32_t *w, unsigned long long *d, uint32_t index, uint32_t *fetched) {
  atomicAdd(reinterpret_cast<int *>(&w[Add]), static_cast<int>(index + 1));
  atomicSub(reinterpret_cast<int *>(&w[Sub]), 1);
  atomicMin(reinterpret_cast<int *>(&w[MinS]), spread(index));
  atomicMax(reinterpret_cast<int *>(&w[MaxS]), spread(index));
  atomicAnd(&w[And], ~(1U << (index % 31U)));
  atomicOr(&w[Or], 1U << (index % 32U));
  atomicXor(&w[Xor], index);
  atomicExch(&w[Exch], index);
  atomicInc(&w[IncWrap], 9U);
  atomicDec(&w[DecWrap], 9U);
  atomicMin(&w[MinU], hash(index));
  atomicMax(&w[MaxU], hash(index));
  uint32_t observed = w[Cas];
  for (uint32_t assumed = observed; (observed = atomicCAS(&w[Cas], assumed, assumed + 2U)) != assumed;)
    assumed = observed;
  fetched[0] = atomicAdd(&w[Ones], 1U);
  atomicAdd(&d[WAdd], static_cast<unsigned long long>(index) << 30);
  unsigned long long previous = d[WCas];
  for (unsigned long long assumed = previous; (previous = atomicCAS(&d[WCas], assumed, assumed + 0x100000001ULL)) != assumed;)
    assumed = previous;
  fetched[1] = static_cast<uint32_t>(atomicExch(&d[WExch], static_cast<unsigned long long>(index) << 32 | 1U) >> 32);
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 350
  atomicMin(&d[WMinU], static_cast<unsigned long long>(hash64(index)));
  atomicMax(&d[WMaxU], static_cast<unsigned long long>(hash64(index)));
  atomicAnd(&d[WAnd], ~(1ULL << (index % 61U)));
  atomicOr(&d[WOr], 1ULL << (index % 64U));
  atomicXor(&d[WXor], static_cast<unsigned long long>(hash64(index)));
  atomicMin(reinterpret_cast<long long *>(&d[WMinS]), static_cast<long long>(spread(index)) * 0x100000000LL);
  atomicMax(reinterpret_cast<long long *>(&d[WMaxS]), static_cast<long long>(spread(index)) * 0x100000000LL);
#endif
}

__global__ void integers(uint32_t *global, unsigned long long *globalWide, uint32_t *blockWords, unsigned long long *blockWide,
                         uint32_t *fetched) {
  __shared__ uint32_t words[Slots];
  __shared__ unsigned long long wide[WSlots];
  if (threadIdx.x < Slots) words[threadIdx.x] = global[Slots + threadIdx.x];
  if (threadIdx.x < WSlots) wide[threadIdx.x] = globalWide[WSlots + threadIdx.x];
  __syncthreads();
  const uint32_t thread = (blockIdx.x * blockDim.x) + threadIdx.x;
  integerOps(global, globalWide, thread, &fetched[static_cast<size_t>(thread) * 4]);
  integerOps(words, wide, threadIdx.x, &fetched[(thread * 4) + 2]);
  __syncthreads();
  if (threadIdx.x < Slots) blockWords[(blockIdx.x * Slots) + threadIdx.x] = words[threadIdx.x];
  if (threadIdx.x < WSlots) blockWide[(blockIdx.x * WSlots) + threadIdx.x] = wide[threadIdx.x];
}

std::vector<uint32_t> expectedWords(uint32_t count, uint32_t base) {
  std::vector<uint32_t> words = initialWords();
  for (uint32_t i = base; i < base + count; ++i) {
    words[Add] += i + 1U;
    words[Sub] -= 1U;
    words[MinS] = static_cast<uint32_t>(std::min(static_cast<int32_t>(words[MinS]), spread(i)));
    words[MaxS] = static_cast<uint32_t>(std::max(static_cast<int32_t>(words[MaxS]), spread(i)));
    words[And] &= ~(1U << (i % 31U));
    words[Or] |= 1U << (i % 32U);
    words[Xor] ^= i;
    words[IncWrap] = words[IncWrap] >= 9U ? 0U : words[IncWrap] + 1U;
    words[DecWrap] = (words[DecWrap] == 0U || words[DecWrap] > 9U) ? 9U : words[DecWrap] - 1U;
    words[MinU] = std::min(words[MinU], hash(i));
    words[MaxU] = std::max(words[MaxU], hash(i));
    words[Cas] += 2U;
    words[Ones] += 1U;
  }
  return words;
}

std::vector<uint64_t> expectedDoubles(uint32_t count, uint32_t base, bool full) {
  std::vector<uint64_t> words = initialDoubles();
  for (uint32_t i = base; i < base + count; ++i) {
    words[WAdd] += static_cast<uint64_t>(i) << 30;
    words[WCas] += 0x100000001ULL;
    if (!full) continue;
    words[WMinU] = std::min(words[WMinU], hash64(i));
    words[WMaxU] = std::max(words[WMaxU], hash64(i));
    words[WAnd] &= ~(1ULL << (i % 61U));
    words[WOr] |= 1ULL << (i % 64U);
    words[WXor] ^= hash64(i);
    const int64_t scaled = static_cast<int64_t>(spread(i)) * 0x100000000LL;
    words[WMinS] = static_cast<uint64_t>(std::min(static_cast<int64_t>(words[WMinS]), scaled));
    words[WMaxS] = static_cast<uint64_t>(std::max(static_cast<int64_t>(words[WMaxS]), scaled));
  }
  if (!full) {
    const auto initial = initialDoubles();
    for (uint32_t slot : {WMinU, WMaxU, WAnd, WOr, WXor, WMinS, WMaxS})
      words[slot] = initial[slot];
  }
  return words;
}

void expectCounters(const uint32_t *words, const uint64_t *wide, uint32_t count, uint32_t base, uint32_t exchanged, bool full,
                    std::string_view where) {
  const auto expected = expectedWords(count, base);
  for (uint32_t slot = 0; slot < Slots; ++slot)
    if (slot != Exch) expect(words[slot], expected[slot], fmt::format("{} word {}", where, slot));
  if (words[Exch] < base || words[Exch] >= base + count) throw std::runtime_error(fmt::format("{} exchange {}", where, words[Exch]));
  const auto doubles = expectedDoubles(count, base, full);
  for (uint32_t slot = 0; slot < WSlots; ++slot)
    if (slot != WExch) expect(wide[slot], doubles[slot], fmt::format("{} doubleword {}", where, slot));
  if ((wide[WExch] & 0xffffffffU) != 1U || (wide[WExch] >> 32) < base || (wide[WExch] >> 32) >= base + count)
    throw std::runtime_error(fmt::format("{} doubleword exchange {:#x}", where, wide[WExch]));
  (void)exchanged;
}

// Each value in `values` once, for the fetches of a counter every thread increments.
void expectPermutation(std::vector<uint32_t> values, uint32_t base, std::string_view what) {
  values ^= sort();
  for (size_t i = 0; i < values.size(); ++i)
    if (values[i] != base + i) throw std::runtime_error(fmt::format("{}: fetch {} is {}", what, i, values[i]));
}

__global__ void architecture([[maybe_unused]] uint32_t *out) {
#ifdef __CUDA_ARCH__
  *out = __CUDA_ARCH__ / 10;
#endif
}

uint32_t compiled() {
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>{0U});
  architecture<<<1, 1>>>(out.data);
  finish("architecture");
  return out.download()[0];
}

} // namespace

TEST_CASE("atomics/cuda/integer-operations", "[unified][atomics]") {
  const bool full = compiled() >= 35;
  // the first copy is the global counters, the second initialises each block's shared counters
  const auto words = initialWords() ^ concat(initialWords());
  const auto doubles = initialDoubles() ^ concat(initialDoubles());
  DeviceBuffer<uint32_t> global(words);
  DeviceBuffer<uint32_t> blockWords(std::vector<uint32_t>(static_cast<size_t>(Blocks) * Slots));
  DeviceBuffer<uint32_t> fetched(std::vector<uint32_t>(static_cast<size_t>(Total) * 4));
  DeviceBuffer<unsigned long long> globalWide(std::vector<unsigned long long>(doubles.begin(), doubles.end()));
  DeviceBuffer<unsigned long long> blockWide(std::vector<unsigned long long>(static_cast<size_t>(Blocks) * WSlots));
  integers<<<Blocks, Threads>>>(global.data, globalWide.data, blockWords.data, blockWide.data, fetched.data);
  finish("integer atomics");
  const auto g = global.download();
  const auto gw = globalWide.download();
  const auto b = blockWords.download();
  const auto bw = blockWide.download();
  const auto f = fetched.download();
  const std::vector<uint64_t> gw64(gw.begin(), gw.end());
  const std::vector<uint64_t> bw64(bw.begin(), bw.end());
  expectCounters(g.data(), gw64.data(), Total, 0, 0, full, "global");
  for (size_t block = 0; block < Blocks; ++block)
    expectCounters(&b[block * Slots], &bw64[block * WSlots], Threads, 0, 0, full, fmt::format("block {}", block));
  expectPermutation(iota(size_t{0}, size_t{Total}) | map([&](size_t thread) { return f[thread * 4]; }) | to_vector(), 0,
                    "global unit increments");
  for (uint32_t block = 0; block < Blocks; ++block) {
    const auto ones = iota(0U, Threads) | map([&](uint32_t thread) { return f[(((block * Threads) + thread) * 4) + 2]; }) | to_vector();
    expectPermutation(ones, 0, fmt::format("block {} unit increments", block));
  }
  // The exchanges hand each thread's value on exactly once: every fetch but one is another thread's index.
  auto globalExchanged = iota(0U, Total) | map([&](uint32_t thread) { return f[(thread * 4) + 1]; }) | to_vector();
  globalExchanged.push_back(static_cast<uint32_t>(gw64[WExch] >> 32));
  globalExchanged ^= sort();
  expect(globalExchanged[0], 0U, "first exchanged doubleword");
  for (uint32_t i = 1; i <= Total; ++i)
    expect(globalExchanged[i], i - 1, fmt::format("exchanged doubleword {}", i));
}

namespace {
// Floating-point additions whose every partial sum is exact, and binary16 compare-and-swap.
__global__ void floats(float *globalFloat, [[maybe_unused]] double *globalDouble, uint32_t *halves, float *blockFloat,
                       double *blockDouble) {
  __shared__ float f;
  __shared__ double d;
  __shared__ unsigned short h[2];
  if (threadIdx.x == 0) {
    f = 0.0F;
    d = 0.0;
    h[0] = h[1] = 0;
  }
  __syncthreads();
  atomicAdd(globalFloat, 0.25F);
  atomicAdd(&f, 0.5F);
  const float before = atomicAdd(&globalFloat[1], 1.0F);
  if (before != static_cast<float>(static_cast<int>(before))) atomicAdd(&globalFloat[2], 1.0F);
  atomicExch(&globalFloat[3], 2.5F);
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 600
  atomicAdd(globalDouble, 0.125);
  atomicAdd(&d, 0.75);
#endif
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
  unsigned short *counter = reinterpret_cast<unsigned short *>(halves);
  unsigned short seen = counter[0];
  for (unsigned short assumed = seen; (seen = atomicCAS(counter, assumed, static_cast<unsigned short>(assumed + 1))) != assumed;)
    assumed = seen;
  seen = h[1];
  for (unsigned short assumed = seen; (seen = atomicCAS(&h[1], assumed, static_cast<unsigned short>(assumed + 3))) != assumed;)
    assumed = seen;
#endif
  __syncthreads();
  if (threadIdx.x == 0) {
    blockFloat[blockIdx.x] = f;
    blockDouble[blockIdx.x] = d;
    halves[1 + blockIdx.x] = h[1];
  }
}

} // namespace

TEST_CASE("atomics/cuda/floating-operations", "[unified][atomics]") {
  const uint32_t architecture = compiled();
  DeviceBuffer<float> globalFloat(std::vector<float>(4));
  DeviceBuffer<float> blockFloat(std::vector<float>(Blocks, 0));
  DeviceBuffer<double> globalDouble(std::vector<double>(1));
  DeviceBuffer<double> blockDouble(std::vector<double>(Blocks, 0));
  DeviceBuffer<uint32_t> halves(std::vector<uint32_t>(1 + Blocks));
  floats<<<Blocks, Threads>>>(globalFloat.data, globalDouble.data, halves.data, blockFloat.data, blockDouble.data);
  finish("floating atomics");
  const auto gf = globalFloat.download();
  expect(gf[0], 0.25F * Total, "global float sum");
  expect(gf[1], static_cast<float>(Total), "global float count");
  expect(gf[2], 0.0F, "fractional float fetches");
  expect(gf[3], 2.5F, "float exchange");
  for (float sum : blockFloat.download())
    expect(sum, 0.5F * Threads, "shared float sum");
  if (architecture >= 60) {
    expect(globalDouble.download()[0], 0.125 * Total, "global double sum");
    for (double sum : blockDouble.download())
      expect(sum, 0.75 * Threads, "shared double sum");
  }
  if (architecture >= 70) {
    const auto h = halves.download();
    expect(h[0] & 0xffffU, Total, "global binary16 slot compare-and-swap");
    for (uint32_t block = 0; block < Blocks; ++block)
      expect(h[1 + block], 3U * Threads, "shared binary16 slot compare-and-swap");
  }
}

namespace {
// cuda::atomic_ref at each scope and memory order, the scoped atomic intrinsics and fences.
__global__ void scoped([[maybe_unused]] int *global, [[maybe_unused]] unsigned long long *wide, [[maybe_unused]] int *out) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
  __shared__ int local[4];
  if (threadIdx.x < 4) local[threadIdx.x] = threadIdx.x == 2 ? 1 << 30 : 0;
  __syncthreads();
  cuda::atomic_ref<int, cuda::thread_scope_device> device(global[0]);
  cuda::atomic_ref<int, cuda::thread_scope_system> system(global[1]);
  cuda::atomic_ref<int, cuda::thread_scope_block> block(local[0]);
  cuda::atomic_ref<int, cuda::thread_scope_block> lowest(local[2]);
  cuda::atomic_ref<unsigned long long, cuda::thread_scope_device> doubleword(wide[0]);
  const int t = static_cast<int>(threadIdx.x);
  device.fetch_add(1, cuda::memory_order_relaxed);
  device.fetch_add(2, cuda::memory_order_acq_rel);
  device.fetch_sub(1, cuda::memory_order_seq_cst);
  system.fetch_or(1 << (t % 16), cuda::memory_order_release);
  block.fetch_add(t, cuda::memory_order_acquire);
  lowest.fetch_min(t + 7, cuda::memory_order_relaxed);
  doubleword.fetch_add(1ULL << 33, cuda::memory_order_relaxed);
  atomicAdd_block(&local[1], 2);
  atomicAdd_system(&global[2], 3);
  atomicExch_block(&local[3], 9);
  cuda::atomic_thread_fence(cuda::memory_order_seq_cst, cuda::thread_scope_device);
  cuda::atomic_thread_fence(cuda::memory_order_acq_rel, cuda::thread_scope_block);
  __threadfence();
  __threadfence_block();
  __threadfence_system();
  int expected = 0;
  cuda::atomic_ref<int, cuda::thread_scope_device> claim(global[3]);
  const bool won = claim.compare_exchange_strong(expected, t + 1, cuda::memory_order_acq_rel);
  if (won) cuda::atomic_ref<int, cuda::thread_scope_device>(global[4]).store(t + 1, cuda::memory_order_release);
  __syncthreads();
  if (t == 0) {
    out[static_cast<size_t>(blockIdx.x) * 4] = block.load(cuda::memory_order_acquire);
    out[(blockIdx.x * 4) + 1] = local[1];
    out[(blockIdx.x * 4) + 2] = lowest.load();
    out[(blockIdx.x * 4) + 3] = local[3];
  }
#endif
}

} // namespace

TEST_CASE("atomics/cuda/scoped-and-ordered", "[unified][atomics]") {
  if (compiled() < 70) SKIP("cuda::atomic_ref requires compute capability 7.0");
#if defined(__clang__) && CUDART_VERSION < 12030
  SKIP("CCCL before CUDA 12.3 reads an undefined __cuda_is_local result under clang");
#endif
  DeviceBuffer<int> global(std::vector<int>(5));
  DeviceBuffer<int> out(std::vector<int>(static_cast<size_t>(Blocks) * 4));
  DeviceBuffer<unsigned long long> wide(std::vector<unsigned long long>(1));
  scoped<<<Blocks, Threads>>>(global.data, wide.data, out.data);
  finish("scoped atomics");
  const auto g = global.download();
  expect(g[0], static_cast<int>(2 * Total), "device-scope sum");
  expect(g[1], 0xffff, "system-scope or");
  expect(g[2], static_cast<int>(3 * Total), "system atomicAdd");
  if (g[3] < 1 || g[3] > static_cast<int>(Threads) || g[4] != g[3])
    throw std::runtime_error(fmt::format("compare-exchange claim {} published {}", g[3], g[4]));
  expect(wide.download()[0], static_cast<unsigned long long>(Total) << 33, "doubleword sum");
  const auto o = out.download();
  for (size_t block = 0; block < Blocks; ++block) {
    expect(o[block * 4], static_cast<int>(Threads * (Threads - 1) / 2), "block-scope sum");
    expect(o[(block * 4) + 1], static_cast<int>(2 * Threads), "atomicAdd_block");
    expect(o[(block * 4) + 2], 7, "block-scope minimum");
    expect(o[(block * 4) + 3], 9, "atomicExch_block");
  }
}

namespace {
// Warps 0 and 1 produce for warps 2 and 3 through barrier 1 (bar.arrive and bar.sync with a count), each half then
// meets on its own barrier, every thread meets on barriers named by registers, and the reductions count and combine
// predicates on numbered barriers.
__global__ void barriers(const uint32_t *in, uint32_t *out, uint32_t count) {
  __shared__ uint32_t staged[Threads];
  __shared__ uint32_t halves[2];
  const uint32_t t = threadIdx.x;
  if (t < 2) halves[t] = 0;
  __syncthreads();
  if (t < 64) {
    staged[t] = in[(blockIdx.x * Threads) + t] * 3U;
    asm volatile("bar.arrive 1, 128;" ::: "memory");
  } else {
    asm volatile("bar.sync 1, 128;" ::: "memory");
    out[(blockIdx.x * Threads) + t] = staged[t - 64] + 1U;
  }
  const uint32_t half = t / 64U;
  atomicAdd(&halves[half], 1U);
  if (half == 0) asm volatile("bar.sync 2, 64;" ::: "memory");
  else asm volatile("bar.sync 3, 64;" ::: "memory");
  if (t % 64U == 0) out[(Blocks * Threads) + (blockIdx.x * 2) + half] = halves[half];
  const uint32_t id = 4U + (count & 1U);
  asm volatile("bar.sync %0, %1;" ::"r"(id), "r"(count) : "memory");
  uint32_t odd{};
  uint32_t all{};
  uint32_t any{};
  asm volatile("{ .reg .pred p; setp.ne.u32 p, %1, 0; bar.red.popc.u32 %0, 6, %2, p; }"
               : "=r"(odd)
               : "r"(in[(blockIdx.x * Threads) + t] & 1U), "r"(count));
  asm volatile("{ .reg .pred p, q; setp.ne.u32 p, %1, 0; bar.red.and.pred q, 7, p; selp.u32 %0, 1, 0, q; }"
               : "=r"(all)
               : "r"(t < 1000U ? 1U : 0U));
  asm volatile("{ .reg .pred p, q; setp.ne.u32 p, %1, 0; bar.red.or.pred q, 8, %2, p; selp.u32 %0, 1, 0, q; }"
               : "=r"(any)
               : "r"(t == 77U ? 1U : 0U), "r"(count));
  const uint32_t popc = __syncthreads_count(in[(blockIdx.x * Threads) + t] > 50U);
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
  asm volatile("barrier.sync.aligned 9, 128;" ::: "memory");
#endif
  if (t < 64) out[(Blocks * Threads) + (Blocks * 2) + (blockIdx.x * Threads) + t] = odd + (all << 8) + (any << 9) + (popc << 10);
}

} // namespace

TEST_CASE("barriers/cuda/named-and-counted", "[unified][barriers]") {
  const auto input = iota(0U, Total) | map([](uint32_t i) { return (i * 7919U) % 101U; }) | to_vector();
  DeviceBuffer<uint32_t> in(input);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(Total + (Blocks * 2) + Total));
  barriers<<<Blocks, Threads>>>(in.data, out.data, Threads);
  finish("named barriers");
  const auto actual = out.download();
  for (uint32_t block = 0; block < Blocks; ++block) {
    const auto values = input ^ slice(size_t{block} * Threads, (size_t{block} + 1) * Threads);
    const auto odd = values ^ sum_by([](uint32_t value) { return value & 1U; });
    const auto large = static_cast<uint32_t>(values ^ count([](uint32_t value) { return value > 50U; }));
    for (uint32_t t = 64; t < Threads; ++t)
      expect(actual[(block * Threads) + t], (input[(block * Threads) + t - 64] * 3U) + 1U,
             fmt::format("consumer {} of block {}", t, block));
    for (uint32_t half = 0; half < 2; ++half)
      expect(actual[Total + (block * 2) + half], 64U, fmt::format("half {} of block {}", half, block));
    for (uint32_t t = 0; t < 64; ++t)
      expect(actual[Total + (Blocks * 2) + (block * Threads) + t], odd + (1U << 8) + (1U << 9) + (large << 10),
             fmt::format("reductions of thread {} of block {}", t, block));
  }
}

namespace {
// An mbarrier that each thread arrives on once directly and once when its cp.async copy lands, over three phases.
__global__ void phases([[maybe_unused]] const uint32_t *in, [[maybe_unused]] uint32_t *out) {
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800
  __shared__ alignas(8) unsigned long long barrier;
  __shared__ uint32_t staged[Threads];
  const uint32_t t = threadIdx.x;
  const uint32_t word = static_cast<uint32_t>(__cvta_generic_to_shared(&barrier));
  const uint32_t slot = static_cast<uint32_t>(__cvta_generic_to_shared(&staged[t]));
  const auto testWait = [](uint32_t address, unsigned long long token) {
    uint32_t done = 0;
    asm volatile("{ .reg .pred p; mbarrier.test_wait.shared.b64 p, [%1], %2; selp.u32 %0, 1, 0, p; }"
                 : "=r"(done)
                 : "r"(address), "l"(token)
                 : "memory");
    return done;
  };
  if (t == 0) asm volatile("mbarrier.init.shared.b64 [%0], %1;" ::"r"(word), "r"(2U * Threads));
  __syncthreads();
  uint32_t total = 0;
  for (uint32_t phase = 0; phase < 3; ++phase) {
    asm volatile("cp.async.ca.shared.global [%0], [%1], 4;" ::"r"(slot), "l"(&in[((phase * Threads) + t) % (3 * Threads)]) : "memory");
    asm volatile("cp.async.mbarrier.arrive.noinc.shared.b64 [%0];" ::"r"(word) : "memory");
    unsigned long long token = 0;
    asm volatile("mbarrier.arrive.shared.b64 %0, [%1];" : "=l"(token) : "r"(word) : "memory");
    uint32_t done = 0;
    while (!done)
      done = testWait(word, token);
    total += staged[(t + 1) % Threads];
    __syncthreads();
  }
  out[t] = total;
#endif
}

} // namespace

TEST_CASE("barriers/cuda/mbarrier-phases", "[unified][barriers]") {
  if (compiled() < 80) SKIP("cp.async and mbarrier require compute capability 8.0");
  const auto input = iota(0U, 3 * Threads) | map([](uint32_t i) { return (i * 31U) + 7U; }) | to_vector();
  DeviceBuffer<uint32_t> in(input);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(Threads, 0));
  phases<<<1, Threads>>>(in.data, out.data);
  finish("mbarrier phases");
  const auto actual = out.download();
  for (uint32_t t = 0; t < Threads; ++t) {
    const auto total =
        iota(0U, 3U) | sum_by([&](uint32_t phase) { return input[((phase * Threads) + ((t + 1) % Threads)) % (3 * Threads)]; });
    expect(actual[t], total, fmt::format("thread {}", t));
  }
}

namespace {
__constant__ int32_t table[64];

// clang's CUDA headers lack these cache-operator intrinsics
__device__ int32_t loadCa(const int32_t *p) {
  int32_t v = 0;
  asm volatile("ld.global.ca.s32 %0, [%1];" : "=r"(v) : "l"(p));
  return v;
}
__device__ int32_t loadLu(const int32_t *p) {
  int32_t v = 0;
  asm volatile("ld.global.lu.s32 %0, [%1];" : "=r"(v) : "l"(p));
  return v;
}
__device__ void storeWb(int32_t *p, int32_t v) { asm volatile("st.global.wb.s32 [%0], %1;" ::"l"(p), "r"(v) : "memory"); }
__device__ void storeCg(int32_t *p, int32_t v) { asm volatile("st.global.cg.s32 [%0], %1;" ::"l"(p), "r"(v) : "memory"); }
__device__ void storeCs(int32_t *p, int32_t v) { asm volatile("st.global.cs.s32 [%0], %1;" ::"l"(p), "r"(v) : "memory"); }
__host__ __device__ inline int32_t addressSpaceBits(uint32_t t) {
  if (t % 3 == 0) return 2;
  return t % 3 == 1 ? 1 : 4;
}

// Loads and stores of every width and signedness through global, shared, local and generic addresses, with the cache
// operators, volatile accesses, constant memory under uniform and divergent indices, and address-space queries.
__global__ void widths(const uint8_t *in, uint8_t *out, uint32_t n) {
  __shared__ alignas(16) uint8_t bytes[Threads * 16];
  int32_t scratch[16];
  const uint32_t t = threadIdx.x;
  const uint8_t *row = in + (static_cast<size_t>(t) * 16);
  const int4 vector = *reinterpret_cast<const int4 *>(row);
  int64_t sum = static_cast<int8_t>(row[1]) + row[2] + *reinterpret_cast<const int16_t *>(row + 4)
                + *reinterpret_cast<const uint16_t *>(row + 6) + *reinterpret_cast<const int32_t *>(row + 8)
                + *reinterpret_cast<const int64_t *>(row + 8) + vector.x + vector.w;
  sum += __ldg(reinterpret_cast<const int32_t *>(row)) + __ldcg(reinterpret_cast<const int32_t *>(row) + 1)
         + loadCa(reinterpret_cast<const int32_t *>(row) + 2) + __ldcs(reinterpret_cast<const int32_t *>(row) + 3)
         + loadLu(reinterpret_cast<const int32_t *>(row) + 1) + __ldcv(reinterpret_cast<const int32_t *>(row) + 2);
  *reinterpret_cast<int4 *>(&bytes[static_cast<size_t>(t) * 16]) = vector;
  bytes[(t * 16) + 1] = static_cast<uint8_t>(sum);
  __syncthreads();
  const uint32_t other = (t + n) % Threads;
  sum += static_cast<int8_t>(bytes[(other * 16) + 1]) + *reinterpret_cast<int16_t *>(&bytes[(other * 16) + 4])
         + *reinterpret_cast<uint16_t *>(&bytes[(other * 16) + 6]) + *reinterpret_cast<int64_t *>(&bytes[(other * 16) + 8]);
  for (uint32_t i = 0; i < 16; ++i)
    scratch[i] = static_cast<int32_t>((i * n) + t);
  sum += scratch[(t + n) % 16] + table[n % 64] + table[t % 64];
  const uint8_t *generic = reinterpret_cast<const uint8_t *>(scratch);
  if (t % 3 == 0) generic = &bytes[static_cast<size_t>(other) * 16];
  else if (t % 3 == 1) generic = row;
  sum += *reinterpret_cast<const int32_t *>(generic + 4);
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
  sum += __isGlobal(generic) + (2 * __isShared(generic)) + (4 * __isLocal(generic));
#else
  sum += addressSpaceBits(t);
#endif
  uint8_t *target = out + (static_cast<size_t>(t) * 32);
  target[0] = static_cast<uint8_t>(sum);
  *reinterpret_cast<uint16_t *>(target + 2) = static_cast<uint16_t>(sum >> 8);
  storeWb(reinterpret_cast<int32_t *>(target + 4), static_cast<int32_t>(sum));
  storeCg(reinterpret_cast<int32_t *>(target + 8), static_cast<int32_t>(sum >> 3));
  storeCs(reinterpret_cast<int32_t *>(target + 12), static_cast<int32_t>(sum >> 5));
  *reinterpret_cast<int4 *>(target + 16) = make_int4(vector.w, vector.z, vector.y, static_cast<int32_t>(sum));
  volatile int32_t *last = reinterpret_cast<volatile int32_t *>(target + 28);
  __stwt(reinterpret_cast<int32_t *>(target + 28), 0);
  *last = *last + static_cast<int32_t>(sum >> 1);
}

} // namespace

TEST_CASE("memory/cuda/widths-and-cache-operators", "[unified][memory]") {
  const auto input = iota(size_t{0}, static_cast<size_t>(Threads) * 16)
                     | map([](size_t i) { return static_cast<uint8_t>((i * 151U) + 17U); }) | to_vector();
  const auto constants = iota(0, 64) | map([](int32_t i) { return (i * i) - 300; }) | to_vector();
  check(cudaMemcpyToSymbol(table, constants.data(), sizeof(int32_t) * constants.size()), "constant upload");
  const uint32_t n = 5;
  DeviceBuffer<uint8_t> in(input);
  DeviceBuffer<uint8_t> out(std::vector<uint8_t>(static_cast<size_t>(Threads) * 32));
  widths<<<1, Threads>>>(in.data, out.data, n);
  finish("widths");
  const auto actual = out.download();
  const auto read = [](const uint8_t *at, auto value) {
    std::memcpy(&value, at, sizeof(value));
    return value;
  };
  std::vector<int64_t> partial(Threads);
  std::vector<uint8_t> shared(input);
  for (uint32_t t = 0; t < Threads; ++t) {
    const uint8_t *row = &input[static_cast<size_t>(t) * 16];
    const int32_t x = read(row, int32_t{});
    const int32_t y = read(row + 4, int32_t{});
    const int32_t z = read(row + 8, int32_t{});
    const int32_t w = read(row + 12, int32_t{});
    partial[t] =
        static_cast<int8_t>(row[1]) + row[2] + read(row + 4, int16_t{}) + read(row + 6, uint16_t{}) + z + read(row + 8, int64_t{}) + x + w;
    partial[t] += x + y + z + w + y + z;
    shared[(t * 16) + 1] = static_cast<uint8_t>(partial[t]);
  }
  for (uint32_t t = 0; t < Threads; ++t) {
    const uint32_t other = (t + n) % Threads;
    const uint8_t *row = &input[static_cast<size_t>(t) * 16];
    int64_t sum = partial[t];
    sum += static_cast<int8_t>(shared[(other * 16) + 1]) + read(&shared[(other * 16) + 4], int16_t{})
           + read(&shared[(other * 16) + 6], uint16_t{}) + read(&shared[(other * 16) + 8], int64_t{});
    sum += static_cast<int32_t>((((t + n) % 16) * n) + t) + constants[n % 64] + constants[t % 64];
    if (t % 3 == 0) sum += read(&shared[(other * 16) + 4], int32_t{});
    else if (t % 3 == 1) sum += read(row + 4, int32_t{});
    else sum += static_cast<int32_t>(n + t);
    sum += addressSpaceBits(t);
    const uint8_t *target = &actual[static_cast<size_t>(t) * 32];
    const std::string where = fmt::format("thread {}", t);
    expect(target[0], static_cast<uint8_t>(sum), where + " byte");
    expect(read(target + 2, uint16_t{}), static_cast<uint16_t>(sum >> 8), where + " halfword");
    expect(read(target + 4, int32_t{}), static_cast<int32_t>(sum), where + " write-back word");
    expect(read(target + 8, int32_t{}), static_cast<int32_t>(sum >> 3), where + " global-cached word");
    expect(read(target + 12, int32_t{}), static_cast<int32_t>(sum >> 5), where + " streaming word");
    expect(read(target + 16, int32_t{}), read(row + 12, int32_t{}), where + " vector x");
    expect(read(target + 20, int32_t{}), read(row + 8, int32_t{}), where + " vector y");
    expect(read(target + 24, int32_t{}), read(row + 4, int32_t{}), where + " vector z");
    expect(read(target + 28, int32_t{}), static_cast<int32_t>(sum >> 1), where + " volatile word");
  }
}
