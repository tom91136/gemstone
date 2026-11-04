#include <algorithm>
#include <cstdint>
#include <stdexcept>
#include <string_view>
#include <vector>

#include <cooperative_groups.h>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <cuda_runtime_api.h>

#include "test_support.hpp"
#include <cooperative_groups/reduce.h>
#include <fmt/format.h>

using namespace aspartame;
namespace cg = cooperative_groups;

// Divergence and reconvergence, nested loops with break and continue, recursion, indirect calls and switch jump
// tables: SSY/SYNC, PBK/BRK, CAL/PRET/RET and BRX before Volta, BSSY/BSYNC/BREAK, CALL/RET and BRX after it.
namespace {

constexpr uint32_t Blocks = 2;
constexpr uint32_t Threads = 96;
constexpr uint32_t Count = Blocks * Threads;
constexpr uint32_t Full = 0xffffffffU;

std::vector<uint32_t> inputs() {
  std::vector<uint32_t> values(Count);
  uint32_t state = 0x2545f491U;
  for (uint32_t index = 0; index < Count; ++index) {
    state ^= state << 13;
    state ^= state >> 17;
    state ^= state << 5;
    values[index] = state;
  }
  return values;
}

void compare(const std::vector<uint32_t> &actual, const std::vector<uint32_t> &expected, std::string_view name) {
  for (size_t index = 0; index < expected.size(); ++index)
    if (actual[index] != expected[index])
      throw std::runtime_error(fmt::format("{} element {}: expected {:#x}, got {:#x}", name, index, expected[index], actual[index]));
}

__host__ __device__ uint32_t branches(uint32_t value, uint32_t neighbour) {
  uint32_t result = 0;
  if (value & 1U) {
    result = (value * 3U) + 1U;
    if (value & 2U) result ^= 0x55U;
    else result += neighbour;
  } else if (value & 4U) {
    result = value >> 1;
    for (uint32_t index = 0; index < (value >> 3 & 7U); ++index)
      result = (result * 5U) + index;
  } else {
    result = ~value;
  }
  return result;
}

// Each lane's partner reads the value its own path produced, so the shuffle must follow reconvergence.
__global__ void diverge(const uint32_t *in, uint32_t *out) {
  const uint32_t thread = (blockIdx.x * blockDim.x) + threadIdx.x;
  const uint32_t result = branches(in[thread], in[(thread + 1) % Count]);
  out[static_cast<size_t>(thread) * 2] = result + __shfl_xor_sync(Full, result, 1);
  out[(thread * 2) + 1] = __ballot_sync(Full, static_cast<int>(result & 1U));
}

std::vector<uint32_t> divergeExpected(const std::vector<uint32_t> &in) {
  const auto results = iota(0U, Count) | map([&](uint32_t thread) { return branches(in[thread], in[(thread + 1) % Count]); }) | to_vector();
  std::vector<uint32_t> expected(static_cast<size_t>(Count) * 2);
  for (uint32_t thread = 0; thread < Count; ++thread) {
    const uint32_t base = thread & ~31U;
    const auto ballot =
        iota(0U, 32U) | fold_left(0U, [&](uint32_t bits, uint32_t lane) { return bits | ((results[base + lane] & 1U) << lane); });
    expected[static_cast<size_t>(thread) * 2] = results[thread] + results[thread ^ 1U];
    expected[(thread * 2) + 1] = ballot;
  }
  return expected;
}

} // namespace

TEST_CASE("control/cuda/divergence", "[unified][control]") {
  const auto in = inputs();
  DeviceBuffer<uint32_t> input(in);
  DeviceBuffer<uint32_t> output(std::vector<uint32_t>(static_cast<size_t>(Count) * 2, 0U));
  diverge<<<Blocks, Threads>>>(input.data, output.data);
  finish("divergence");
  compare(output.download(), divergeExpected(in), "divergence");
}

namespace {
__host__ __device__ uint32_t nested(uint32_t value) {
  uint32_t total = 0;
  for (uint32_t outer = 0; outer < 8U + (value & 15U); ++outer) {
    if ((value >> (outer & 31U)) & 1U) continue;
    for (uint32_t inner = 0; inner < 12U; ++inner) {
      const uint32_t mixed = value ^ ((outer * 31U) + (inner * 7U));
      if (mixed % 11U == 0U) break;
      if (mixed & 1U) continue;
      total += mixed >> 4;
      if (total % 97U == 5U) goto done;
    }
    if (total > (value | 1U) * 13U) break;
    total = (total * 3U) + outer;
  }
done:
  return total;
}

__global__ void loops(const uint32_t *in, uint32_t *out) {
  const uint32_t thread = (blockIdx.x * blockDim.x) + threadIdx.x;
  const uint32_t total = nested(in[thread]);
  out[static_cast<size_t>(thread) * 2] = total;
  out[(thread * 2) + 1] = __ballot_sync(Full, static_cast<int>(total & 1U));
}

std::vector<uint32_t> loopsExpected(const std::vector<uint32_t> &in) {
  const auto totals = iota(0U, Count) | map([&](uint32_t thread) { return nested(in[thread]); }) | to_vector();
  std::vector<uint32_t> expected(static_cast<size_t>(Count) * 2);
  for (uint32_t thread = 0; thread < Count; ++thread) {
    const auto ballot =
        iota(0U, 32U) | fold_left(0U, [&](uint32_t bits, uint32_t lane) { return bits | ((totals[(thread & ~31U) + lane] & 1U) << lane); });
    expected[static_cast<size_t>(thread) * 2] = totals[thread];
    expected[(thread * 2) + 1] = ballot;
  }
  return expected;
}

} // namespace

TEST_CASE("control/cuda/loops", "[unified][control]") {
  const auto in = inputs();
  DeviceBuffer<uint32_t> input(in);
  DeviceBuffer<uint32_t> output(std::vector<uint32_t>(static_cast<size_t>(Count) * 2, 0U));
  loops<<<Blocks, Threads>>>(input.data, output.data);
  finish("loops");
  compare(output.download(), loopsExpected(in), "loops");
}

namespace {
__host__ __device__ __noinline__ uint32_t descend(uint32_t depth, uint32_t salt) {
  if (depth < 2U) return depth + salt;
  if (depth & 1U) return (descend(depth - 1U, salt + 1U) * 3U) + 1U;
  return descend(depth / 2U, salt ^ depth) + descend(depth - 2U, salt);
}

// Lanes recurse to different depths from inside a divergent branch. Nothing exchanges values after it: a callee that
// saves and reuses the caller's convergence barrier register releases the lanes waiting on it, so on hardware the warp
// is not reconverged when the call returns.
__global__ void recursion(const uint32_t *in, uint32_t *out) {
  const uint32_t thread = (blockIdx.x * blockDim.x) + threadIdx.x;
  const uint32_t value = in[thread];
  uint32_t result = value;
  if (value & 3U) result = descend(value % 14U, value >> 24);
  out[thread] = result;
}

std::vector<uint32_t> recursionExpected(const std::vector<uint32_t> &in) {
  return iota(0U, Count) | map([&](uint32_t thread) { return in[thread] & 3U ? descend(in[thread] % 14U, in[thread] >> 24) : in[thread]; })
         | to_vector();
}

} // namespace

TEST_CASE("control/cuda/recursion", "[unified][control]") {
  check(cudaDeviceSetLimit(cudaLimitStackSize, 4096), "stack limit");
  const auto in = inputs();
  DeviceBuffer<uint32_t> input(in);
  DeviceBuffer<uint32_t> output(std::vector<uint32_t>(Count, 0U));
  recursion<<<Blocks, Threads>>>(input.data, output.data);
  finish("recursion");
  compare(output.download(), recursionExpected(in), "recursion");
}

namespace {
using Operation = uint32_t (*)(uint32_t);

__host__ __device__ __noinline__ uint32_t rotate(uint32_t value) { return value << 7 | value >> 25; }
__host__ __device__ __noinline__ uint32_t scramble(uint32_t value) { return (value ^ 0x9e3779b9U) * 0x85ebca6bU; }
__host__ __device__ __noinline__ uint32_t steps(uint32_t value) {
  uint32_t count = 0;
  for (uint32_t current = (value % 1000U) + 1U; current != 1U && count < 200U; ++count)
    current = current & 1U ? (current * 3U) + 1U : current / 2U;
  return count;
}
__host__ __device__ __noinline__ uint32_t twice(uint32_t value) { return rotate(scramble(value)); }

__device__ Operation deviceOperations[] = {rotate, scramble, steps, twice};
const Operation HostOperations[] = {rotate, scramble, steps, twice};

// Each lane calls through a pointer it selects, twice, the second in a branch only some lanes take.
__global__ void indirect(const uint32_t *in, uint32_t *out) {
  const uint32_t thread = (blockIdx.x * blockDim.x) + threadIdx.x;
  const uint32_t value = in[thread];
  uint32_t result = deviceOperations[value & 3U](value);
  if (value & 4U) result += deviceOperations[(value >> 3) & 3U](result);
  out[thread] = result;
}

std::vector<uint32_t> indirectExpected(const std::vector<uint32_t> &in) {
  return iota(0U, Count) | map([&](uint32_t thread) {
           const uint32_t value = in[thread];
           uint32_t result = HostOperations[value & 3U](value);
           if (value & 4U) result += HostOperations[(value >> 3) & 3U](result);
           return result;
         })
         | to_vector();
}

} // namespace

TEST_CASE("control/cuda/indirect-calls", "[unified][control]") {
  const auto in = inputs();
  DeviceBuffer<uint32_t> input(in);
  DeviceBuffer<uint32_t> output(std::vector<uint32_t>(Count, 0U));
  indirect<<<Blocks, Threads>>>(input.data, output.data);
  finish("indirect calls");
  compare(output.download(), indirectExpected(in), "indirect calls");
}

namespace {
constexpr uint32_t Rounds = 3;

// Signed selectors with a default keep ptxas lowering the switch to BRX through a constant-bank table.
__host__ __device__ int32_t select(int32_t selector, int32_t value, const int32_t *table) {
  switch (selector) {
    case 0: return value + 13;
    case 1: return table[value & 63] * 3;
    case 2: return value ^ table[1];
    case 3: return 99;
    case 4: return table[(value >> 8) & 63] + 1;
    case 5: return value - table[0];
    case 6: return 1234;
    case 7: return table[value & 31] << 2;
    default: return -value;
  }
}

// Lanes take different cases of a jump table on each round.
__global__ void jumpTable(const int32_t *selectors, const int32_t *table, int32_t *out) {
  const uint32_t thread = (blockIdx.x * blockDim.x) + threadIdx.x;
  int32_t value = table[thread & 63U];
  for (uint32_t round = 0; round < Rounds; ++round)
    value = select(selectors[(thread * Rounds) + round], value, table);
  out[thread] = value;
}

} // namespace

TEST_CASE("control/cuda/jump-table", "[unified][control]") {
  const auto in = inputs();
  const auto selectors = iota(0U, Count * Rounds)
                         | map([&](uint32_t index) { return static_cast<int32_t>((in[index % Count] >> (index / Count * 8U)) % 10U) - 1; })
                         | to_vector();
  const auto entries = iota(0U, 64U) | map([&](uint32_t index) { return static_cast<int32_t>(in[index] >> 4); }) | to_vector();
  DeviceBuffer<int32_t> selector(selectors);
  DeviceBuffer<int32_t> table(entries);
  DeviceBuffer<int32_t> output(std::vector<int32_t>(Count, 0));
  jumpTable<<<Blocks, Threads>>>(selector.data, table.data, output.data);
  finish("jump table");
  const auto actual = output.download();
  for (uint32_t thread = 0; thread < Count; ++thread) {
    const auto value = iota(0U, Rounds) | fold_left(entries[thread & 63U], [&](int32_t current, uint32_t round) {
                         return select(selectors[(thread * Rounds) + round], current, entries.data());
                       });
    if (actual[thread] != value)
      throw std::runtime_error(fmt::format("jump table element {}: expected {}, got {}", thread, value, actual[thread]));
  }
}

namespace {
__host__ __device__ bool exits(uint32_t value) { return (value & 0x18U) == 0U; }

uint32_t survivors(const std::vector<uint32_t> &in, uint32_t thread) {
  return iota(0U, 32U) | fold_left(0U, [&](uint32_t mask, uint32_t lane) {
           return mask | (static_cast<uint32_t>(!exits(in[(thread & ~31U) + lane])) << lane);
         });
}

// Lanes that return early leave the rest to synchronise and exchange among themselves; exited lanes named in a
// full mask do not take part.
__global__ void earlyExit(const uint32_t *in, uint32_t *out) {
  const uint32_t thread = (blockIdx.x * blockDim.x) + threadIdx.x;
  const uint32_t value = in[thread];
  if (exits(value)) return;
  uint32_t mask = 0;
  for (uint32_t lane = 0; lane < 32; ++lane)
    mask |= static_cast<uint32_t>(!exits(in[(thread & ~31U) + lane])) << lane;
  __syncwarp();
  out[thread] = value ^ __shfl_sync(Full, value * 7U, __ffs(static_cast<int>(mask)) - 1);
}

} // namespace

TEST_CASE("control/cuda/early-exit", "[unified][control]") {
  const auto in = inputs();
  DeviceBuffer<uint32_t> input(in);
  DeviceBuffer<uint32_t> output(std::vector<uint32_t>(Count, 0U));
  earlyExit<<<Blocks, Threads>>>(input.data, output.data);
  finish("early exit");
  std::vector<uint32_t> expected(Count);
  for (uint32_t thread = 0; thread < Count; ++thread) {
    const uint32_t mask = survivors(in, thread);
    if (exits(in[thread]) || mask == 0U) continue;
    uint32_t leader = 0;
    while (!(mask >> leader & 1U))
      ++leader;
    expected[thread] = in[thread] ^ (in[(thread & ~31U) + leader] * 7U);
  }
  compare(output.download(), expected, "early exit");
}

namespace {
constexpr uint32_t Half = 0x0000ffffU;
constexpr uint32_t Predecessors = 8;
constexpr uint32_t Sentinel = 0x0000deadU;
constexpr uint32_t GlobalFlag = 0x40000000U;
constexpr uint32_t ValueMask = 0x3fffffffU;

enum class Shape { Full, HalfReturned, HalfLive, Diverged, Repeated };

__host__ __device__ constexpr bool halfWarp(Shape shape) { return shape == Shape::HalfReturned || shape == Shape::HalfLive; }

// Lane t flags predecessor t % 9, so lanes 8, 17 and 26 never see a flag and walk every predecessor.
std::vector<uint32_t> lookbackInput() {
  std::vector<uint32_t> values(static_cast<size_t>(Predecessors) * 32);
  for (uint32_t block = 0; block < Predecessors; ++block)
    for (uint32_t lane = 0; lane < 32; ++lane)
      values[(block * 32) + lane] = (block == lane % 9U ? GlobalFlag : 0U) | (lane + block + 1);
  return values;
}

// Decoupled look-back over the predecessors of one warp, with the ballot shape of CUB onesweep. Each round
// starts from the full warp or the lower half, and the mask carries only lanes that have not seen a flag.
template <Shape S> __global__ void lookback(const uint32_t *table, uint32_t *out) {
  const uint32_t lane = threadIdx.x;
  if constexpr (S == Shape::HalfReturned) {
    if (lane >= 16U) return;
  }
  if constexpr (S == Shape::HalfLive) {
    if (lane >= 16U) {
      for (uint32_t k = 0; k < 40U; ++k)
        (void)*static_cast<const volatile uint32_t *>(table + ((k & 7U) * 32U) + lane);
      return;
    }
  }
  uint32_t sum = 0;
  uint32_t mask = 0;
  for (uint32_t round = 0; round < (S == Shape::Repeated ? 2U : 1U); ++round) {
    sum = 0;
    mask = halfWarp(S) ? Half : Full;
    for (uint32_t block = Predecessors; block-- > 0;) {
      const uint32_t *slot = table + (block * 32U) + lane;
      uint32_t value = 0;
      do {
        __threadfence_block();
        value = *static_cast<const volatile uint32_t *>(slot);
      } while (value == 0U);
      sum += value & ValueMask;
      if constexpr (S == Shape::Diverged) {
        for (uint32_t k = 0; k < lane % 5U; ++k)
          asm volatile("" ::: "memory");
      }
      mask = __ballot_sync(mask, (value & GlobalFlag) == 0U);
      if (value & GlobalFlag) break;
    }
  }
  out[lane] = sum;
  out[32 + lane] = mask;
}

std::vector<uint32_t> lookbackExpected(Shape shape) {
  const uint32_t active = halfWarp(shape) ? Half : Full;
  std::vector<uint32_t> expected(64, Sentinel);
  for (uint32_t lane = 0; lane < 32; ++lane) {
    if (!(active >> lane & 1U)) continue;
    const uint32_t stop = lane % 9U < Predecessors ? lane % 9U : 0U;
    uint32_t sum = 0;
    for (uint32_t block = Predecessors; block-- > stop;)
      sum += lane + block + 1;
    expected[lane] = sum;
    expected[32 + lane] = iota(0U, 32U) | fold_left(0U, [&](uint32_t mask, uint32_t other) {
                            const uint32_t flag = other % 9U;
                            return mask | (static_cast<uint32_t>((flag < stop || flag == Predecessors) && (active >> other & 1U)) << other);
                          });
  }
  return expected;
}

template <Shape S> void runLookback(std::string_view name) {
  const auto table = lookbackInput();
  DeviceBuffer<uint32_t> input(table);
  DeviceBuffer<uint32_t> output(std::vector<uint32_t>(64, Sentinel));
  lookback<S><<<1, 32>>>(input.data, output.data);
  finish(name);
  compare(output.download(), lookbackExpected(S), name);
}

template <bool Divergent> __host__ __device__ constexpr bool participates(uint32_t lane) { return !Divergent || lane % 3U != 2U; }

template <bool Divergent> __host__ __device__ constexpr uint32_t labelOf(uint32_t lane) {
  return Divergent ? (lane / 4U) % 3U : (lane * 7U) % 5U;
}

std::vector<uint32_t> labeledInput() {
  std::vector<uint32_t> values(32);
  for (uint32_t lane = 0; lane < 32; ++lane)
    values[lane] = (lane * 37U + 11U) % 101U;
  return values;
}

// Divergent participants leave every third lane out, so the partition sees a sparse active mask.
template <bool Divergent> __global__ void labeled(const uint32_t *values, uint32_t *out) {
  const uint32_t lane = threadIdx.x;
  const uint32_t value = values[lane];
  uint32_t reduced = Sentinel;
  uint32_t next = Sentinel;
  uint32_t odd = Sentinel;
  uint32_t peers = Sentinel;
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700
  if (participates<Divergent>(lane)) {
    const uint32_t label = labelOf<Divergent>(lane);
    auto group = cg::labeled_partition(cg::coalesced_threads(), label);
    reduced = cg::reduce(group, value, cg::greater<uint32_t>());
    next = group.shfl(value, (group.thread_rank() + 1) % group.size());
    peers = __match_any_sync(__activemask(), label);
    odd = __ballot_sync(peers, value & 1U);
  }
#else
  (void)value;
#endif
  out[lane] = reduced;
  out[32 + lane] = next;
  out[64 + lane] = odd;
  out[96 + lane] = peers;
}

template <bool Divergent> std::vector<uint32_t> labeledExpected(const std::vector<uint32_t> &values) {
  std::vector<uint32_t> expected(128, Sentinel);
  for (uint32_t lane = 0; lane < 32; ++lane) {
    if (!participates<Divergent>(lane)) continue;
    std::vector<uint32_t> members;
    for (uint32_t other = 0; other < 32; ++other)
      if (participates<Divergent>(other) && labelOf<Divergent>(other) == labelOf<Divergent>(lane)) members.push_back(other);
    const size_t rank = std::find(members.begin(), members.end(), lane) - members.begin();
    uint32_t reduced = 0;
    uint32_t odd = 0;
    uint32_t peers = 0;
    for (uint32_t member : members) {
      reduced = std::max(reduced, values[member]);
      if (values[member] & 1U) odd |= 1U << member;
      peers |= 1U << member;
    }
    expected[lane] = reduced;
    expected[32 + lane] = values[members[(rank + 1) % members.size()]];
    expected[64 + lane] = odd;
    expected[96 + lane] = peers;
  }
  return expected;
}

template <bool Divergent> void runLabeled(std::string_view name) {
  if (capability() < 70) SKIP("labeled_partition requires compute capability 7.0");
  const auto values = labeledInput();
  DeviceBuffer<uint32_t> input(values);
  DeviceBuffer<uint32_t> output(std::vector<uint32_t>(128, Sentinel));
  labeled<Divergent><<<1, 32>>>(input.data, output.data);
  finish(name);
  compare(output.download(), labeledExpected<Divergent>(values), name);
}

} // namespace

TEST_CASE("control/cuda/lookback-full", "[unified][control]") { runLookback<Shape::Full>("lookback full"); }

TEST_CASE("control/cuda/lookback-half-returned", "[unified][control]") { runLookback<Shape::HalfReturned>("lookback half returned"); }

TEST_CASE("control/cuda/lookback-half-live", "[unified][control]") { runLookback<Shape::HalfLive>("lookback half live"); }

TEST_CASE("control/cuda/lookback-diverged", "[unified][control]") { runLookback<Shape::Diverged>("lookback diverged"); }

TEST_CASE("control/cuda/lookback-repeated", "[unified][control]") { runLookback<Shape::Repeated>("lookback repeated"); }

TEST_CASE("control/cuda/labeled-partition-full", "[unified][control]") { runLabeled<false>("labeled partition full"); }

TEST_CASE("control/cuda/labeled-partition-divergent", "[unified][control]") { runLabeled<true>("labeled partition divergent"); }
