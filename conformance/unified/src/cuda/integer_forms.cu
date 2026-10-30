#include <algorithm>
#include <cstdint>
#include <cstdlib>
#include <stdexcept>
#include <string_view>
#include <utility>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;

// Integer forms ptxas emits on the cuda-sass routes beside the common ones: saturating and packing conversions (I2I.SAT,
// I2IP), sums of absolute differences (Kepler's ISAD), dot products (IDP), Kepler's SIMD video instructions for the
// __v* intrinsics, predicate-heavy code whose logic ptxas folds into PLOP3 and LOP3 with predicate operands, and the
// carry-less products of PTX 9.3 (CLMAD). References follow the PTX ISA 13.3 and CUDA math API definitions.
namespace {

const std::vector<uint32_t> Words = {0x00000000U, 0x00000001U, 0x0000007fU, 0x00000080U, 0x000000ffU, 0x00000100U, 0x00007fffU, 0x00008000U,
                                     0x0000ffffU, 0x00010000U, 0x7fffffffU, 0x80000000U, 0xffffff80U, 0xffff8000U, 0xffffffffU, 0x01020304U,
                                     0x807f80ffU, 0x12345678U, 0xdeadbeefU, 0xfffffff9U, 0x00000009U, 0x0000001fU};

int64_t clampTo(int64_t value, int64_t minimum, int64_t maximum) { return std::min(std::max(value, minimum), maximum); }

uint32_t lane(uint32_t word, int index, int bits) { return (word >> (index * bits)) & ((1U << bits) - 1U); }

int32_t signedLane(uint32_t word, int index, int bits) {
  const uint32_t field = lane(word, index, bits);
  return (field >> (bits - 1)) ? static_cast<int32_t>(field) - (1 << bits) : static_cast<int32_t>(field);
}

constexpr uint32_t ConversionCount = 14;

__global__ void conversions(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  const uint32_t c = a ^ (b << 7) ^ 0x5a5aa5a5U;
  uint32_t *o = out + (static_cast<size_t>(i) * ConversionCount);
  uint32_t d = 0;
  asm("cvt.sat.s8.s32 %0, %1;" : "=r"(d) : "r"(a));
  o[0] = d;
  asm("cvt.sat.u8.s32 %0, %1;" : "=r"(d) : "r"(a));
  o[1] = d;
  asm("cvt.sat.s16.s32 %0, %1;" : "=r"(d) : "r"(a));
  o[2] = d;
  asm("cvt.sat.u16.s32 %0, %1;" : "=r"(d) : "r"(a));
  o[3] = d;
  o[4] = __sad(static_cast<int>(a), static_cast<int>(b), c);
  o[5] = __usad(a, b, c);
#if __CUDA_ARCH__ >= 610
  o[6] = static_cast<uint32_t>(__dp4a(static_cast<int>(a), static_cast<int>(b), static_cast<int>(c)));
  o[7] = __dp4a(a, b, c);
  o[8] = static_cast<uint32_t>(__dp2a_lo(static_cast<int>(a), static_cast<int>(b), static_cast<int>(c)));
  o[9] = __dp2a_hi(a, b, c);
#else
  for (uint32_t k = 6; k < 10; ++k)
    o[k] = 0xcafef00dU;
#endif
#if __CUDA_ARCH__ >= 720
  asm("cvt.pack.sat.u8.s32.b32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[10] = d;
  asm("cvt.pack.sat.s16.s32 %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[11] = d;
  asm("cvt.pack.sat.s4.s32.b32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[12] = d;
  asm("cvt.pack.sat.u2.s32.b32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[13] = d;
#else
  for (uint32_t k = 10; k < ConversionCount; ++k)
    o[k] = 0xcafef00dU;
#endif
}

uint32_t pack(int64_t high, int64_t low, uint32_t rest, int bits, bool isSigned) {
  const int64_t lowest = isSigned ? -(int64_t{1} << (bits - 1)) : 0;
  const int64_t highest = isSigned ? (int64_t{1} << (bits - 1)) - 1 : (int64_t{1} << bits) - 1;
  const uint32_t mask = (1U << bits) - 1U;
  const uint32_t packed = (static_cast<uint32_t>(clampTo(high, lowest, highest)) & mask) << bits
                          | (static_cast<uint32_t>(clampTo(low, lowest, highest)) & mask);
  return bits == 16 ? packed : packed | (rest << (2 * bits));
}

int32_t dot(uint32_t a, uint32_t b, int lanes, bool aSigned, bool bSigned, int offset, int bits) {
  return iota(0, lanes) | sum_by([&](int k) {
           const int32_t x = aSigned ? signedLane(a, k, bits) : static_cast<int32_t>(lane(a, k, bits));
           const int32_t y = bSigned ? signedLane(b, k + offset, 8) : static_cast<int32_t>(lane(b, k + offset, 8));
           return x * y;
         });
}

std::vector<uint32_t> conversionReference(uint32_t a, uint32_t b, bool products, bool packing) {
  const uint32_t c = a ^ (b << 7) ^ 0x5a5aa5a5U;
  const int64_t sa = static_cast<int32_t>(a);
  const int64_t sb = static_cast<int32_t>(b);
  std::vector<uint32_t> r(ConversionCount);
  r[0] = static_cast<uint32_t>(static_cast<int32_t>(clampTo(sa, -128, 127)));
  r[1] = static_cast<uint32_t>(clampTo(sa, 0, 255));
  r[2] = static_cast<uint32_t>(static_cast<int32_t>(clampTo(sa, -32768, 32767)));
  r[3] = static_cast<uint32_t>(clampTo(sa, 0, 65535));
  r[4] = static_cast<uint32_t>(std::llabs(sa - sb)) + c;
  r[5] = (a > b ? a - b : b - a) + c;
  if (products) {
    r[6] = static_cast<uint32_t>(dot(a, b, 4, true, true, 0, 8) + static_cast<int32_t>(c));
    r[7] = static_cast<uint32_t>(dot(a, b, 4, false, false, 0, 8)) + c;
    r[8] = static_cast<uint32_t>(dot(a, b, 2, true, true, 0, 16) + static_cast<int32_t>(c));
    r[9] = static_cast<uint32_t>(dot(a, b, 2, false, false, 2, 16)) + c;
  } else {
    std::fill(r.begin() + 6, r.begin() + 10, 0xcafef00dU);
  }
  if (packing) {
    r[10] = pack(sa, sb, c, 8, false);
    r[11] = pack(sa, sb, 0, 16, true);
    r[12] = pack(sa, sb, c, 4, true);
    r[13] = pack(sa, sb, c, 2, false);
  } else {
    std::fill(r.begin() + 10, r.end(), 0xcafef00dU);
  }
  return r;
}

constexpr uint32_t SimdCount = 12;

__global__ void simd(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  uint32_t *o = out + (static_cast<size_t>(i) * SimdCount);
  o[0] = __vadd2(a, b);
  o[1] = __vaddus2(a, b);
  o[2] = __vaddss4(a, b);
  o[3] = __vsub4(a, b);
  o[4] = __vhaddu2(a, b);
  o[5] = __vabsdiffs2(a, b);
  o[6] = __vsadu2(a, b);
  o[7] = __vmaxu2(a, b);
  o[8] = __vmins4(a, b);
  o[9] = __vcmpgtu2(a, b);
  o[10] = __vsetles4(a, b);
  o[11] = __vabsdiffu4(a, b);
}

std::vector<uint32_t> simdReference(uint32_t a, uint32_t b) {
  auto map = [&](int lanes, auto f) {
    const int bits = 32 / lanes;
    return iota(0, lanes) | fold_left(0U, [&](uint32_t result, int k) {
             return result | ((static_cast<uint32_t>(f(k, bits)) & ((1U << bits) - 1U)) << (k * bits));
           });
  };
  auto u = [&](uint32_t w, int k, int bits) { return static_cast<int64_t>(lane(w, k, bits)); };
  auto s = [&](uint32_t w, int k, int bits) { return static_cast<int64_t>(signedLane(w, k, bits)); };
  std::vector<uint32_t> r(SimdCount);
  r[0] = map(2, [&](int k, int bits) { return u(a, k, bits) + u(b, k, bits); });
  r[1] = map(2, [&](int k, int bits) { return clampTo(u(a, k, bits) + u(b, k, bits), 0, 0xffff); });
  r[2] = map(4, [&](int k, int bits) { return clampTo(s(a, k, bits) + s(b, k, bits), -128, 127); });
  r[3] = map(4, [&](int k, int bits) { return u(a, k, bits) - u(b, k, bits); });
  r[4] = map(2, [&](int k, int bits) { return (u(a, k, bits) + u(b, k, bits)) >> 1; });
  r[5] = map(2, [&](int k, int bits) { return std::llabs(s(a, k, bits) - s(b, k, bits)); });
  r[6] = static_cast<uint32_t>(std::llabs(u(a, 0, 16) - u(b, 0, 16)) + std::llabs(u(a, 1, 16) - u(b, 1, 16)));
  r[7] = map(2, [&](int k, int bits) { return std::max(u(a, k, bits), u(b, k, bits)); });
  r[8] = map(4, [&](int k, int bits) { return std::min(s(a, k, bits), s(b, k, bits)); });
  r[9] = map(2, [&](int k, int bits) { return u(a, k, bits) > u(b, k, bits) ? 0xffff : 0; });
  r[10] = map(4, [&](int k, int bits) { return s(a, k, bits) <= s(b, k, bits) ? 1 : 0; });
  r[11] = map(4, [&](int k, int bits) { return std::llabs(u(a, k, bits) - u(b, k, bits)); });
  return r;
}

// Twelve flags live across an unrolled loop, so ptxas combines them with PLOP3 and LOP3 predicate operands.
__global__ void predicates(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t t = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (t >= count) return;
  bool p[12];
  for (int i = 0; i < 12; ++i)
    p[i] = ((in[(t + i) % count] >> (i % 5)) & 3U) > static_cast<uint32_t>(i % 3);
  uint32_t r = 0;
#pragma unroll
  for (int k = 0; k < 48; ++k) {
    r = (r * 3U) + static_cast<uint32_t>(p[k % 12] ^ p[(k * 5) % 12]) + static_cast<uint32_t>(p[(k * 7) % 12] && !p[(k + 1) % 12]);
    if (p[(k * 11) % 12] || (p[k % 12] && p[(k + 3) % 12])) r ^= static_cast<uint32_t>(k);
  }
  out[t] = r;
}

uint32_t predicateReference(const std::vector<uint32_t> &in, uint32_t t) {
  const auto count = static_cast<uint32_t>(in.size());
  bool p[12];
  for (int i = 0; i < 12; ++i)
    p[i] = ((in[(t + i) % count] >> (i % 5)) & 3U) > static_cast<uint32_t>(i % 3);
  uint32_t r = 0;
  for (int k = 0; k < 48; ++k) {
    r = (r * 3U) + static_cast<uint32_t>(p[k % 12] ^ p[(k * 5) % 12]) + static_cast<uint32_t>(p[(k * 7) % 12] && !p[(k + 1) % 12]);
    if (p[(k * 11) % 12] || (p[k % 12] && p[(k + 3) % 12])) r ^= static_cast<uint32_t>(k);
  }
  return r;
}

constexpr uint32_t CarrylessCount = 4;

// clmad needs PTX ISA 9.3, which nvcc 13.3 emits and clang does not; `supported` reports whether it was compiled.
__global__ void carryless([[maybe_unused]] const uint32_t *in, [[maybe_unused]] uint64_t *out, [[maybe_unused]] uint32_t *supported,
                          uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 800 && __CUDACC_VER_MAJOR__ >= 13 && !defined(__clang__)
  const uint64_t a = static_cast<uint64_t>(in[i / count]) << 32 | in[i % count], b = a * 0x9e3779b97f4a7c15ull ^ 0x87u, c = ~a >> 3;
  uint64_t *o = out + i * CarrylessCount;
  uint64_t d;
  asm("clmad.lo.u64 %0, %1, %2, %3;" : "=l"(d) : "l"(a), "l"(b), "l"(c));
  o[0] = d;
  asm("clmad.hi.u64 %0, %1, %2, %3;" : "=l"(d) : "l"(a), "l"(b), "l"(c));
  o[1] = d;
  asm("clmad.lo.u64 %0, %1, 0x87, 0;" : "=l"(d) : "l"(a));
  o[2] = d;
  asm("clmad.hi.u64 %0, %1, %2, 5;" : "=l"(d) : "l"(a), "l"(b));
  o[3] = d;
  if (i == 0) *supported = 1;
#endif
}

std::vector<uint64_t> carrylessReference(uint32_t high, uint32_t low) {
  const uint64_t a = static_cast<uint64_t>(high) << 32 | low;
  const uint64_t b = (a * 0x9e3779b97f4a7c15ULL) ^ 0x87U;
  const uint64_t c = ~a >> 3;
  const auto product = [](uint64_t x, uint64_t y) {
    uint64_t lo = 0;
    uint64_t hi = 0;
    for (int bit = 0; bit < 64; ++bit)
      if ((y >> bit) & 1U) {
        lo ^= x << bit;
        if (bit) hi ^= x >> (64 - bit);
      }
    return std::pair{lo, hi};
  };
  const auto [lo, hi] = product(a, b);
  return {lo ^ c, hi ^ c, product(a, 0x87U).first, hi ^ 5U};
}

__global__ void architecture(uint32_t *out) {
#ifdef __CUDA_ARCH__
  *out = __CUDA_ARCH__ / 10;
#else
  *out = 0;
#endif
}

uint32_t compiled() {
  DeviceBuffer<uint32_t> out{std::vector<uint32_t>(1)};
  architecture<<<1, 1>>>(out.data);
  finish("architecture");
  return out.download()[0];
}

template <typename Kernel, typename Reference> void pairs(std::string_view name, uint32_t fields, Kernel kernel, Reference reference) {
  const auto count = static_cast<uint32_t>(Words.size());
  DeviceBuffer<uint32_t> in(Words);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * fields));
  kernel<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count);
  finish(name);
  const auto actual = out.download();
  for (uint32_t i = 0; i < count * count; ++i) {
    const auto expected = reference(Words[i / count], Words[i % count]);
    for (uint32_t field = 0; field < fields; ++field)
      if (actual[(i * fields) + field] != expected[field])
        throw std::runtime_error(fmt::format("{} {} of {:#x} and {:#x}: expected {:#x}, got {:#x}", name, field, Words[i / count],
                                             Words[i % count], expected[field], actual[(i * fields) + field]));
  }
}

} // namespace

TEST_CASE("integer/cuda/conversions", "[unified][integer]") {
  const uint32_t architecture = compiled();
  pairs("conversions", ConversionCount, conversions,
        [&](uint32_t a, uint32_t b) { return conversionReference(a, b, architecture >= 61, architecture >= 72); });
}

TEST_CASE("integer/cuda/simd", "[unified][integer]") { pairs("simd", SimdCount, simd, simdReference); }

TEST_CASE("integer/cuda/predicates", "[unified][integer]") {
  const auto in = iota(0U, 256U) | map([](uint32_t i) { return (i * 0x9e3779b9U) ^ (i >> 3); }) | to_vector();
  const auto count = static_cast<uint32_t>(in.size());
  DeviceBuffer<uint32_t> input(in);
  DeviceBuffer<uint32_t> out{std::vector<uint32_t>(count)};
  predicates<<<(count + 63) / 64, 64>>>(input.data, out.data, count);
  finish("predicates");
  const auto actual = out.download();
  for (uint32_t t = 0; t < count; ++t)
    if (actual[t] != predicateReference(in, t))
      throw std::runtime_error(fmt::format("predicates {}: expected {:#x}, got {:#x}", t, predicateReference(in, t), actual[t]));
}

TEST_CASE("integer/cuda/carryless", "[unified][integer]") {
  const auto count = static_cast<uint32_t>(Words.size());
  DeviceBuffer<uint32_t> in(Words);
  DeviceBuffer<uint32_t> supported{std::vector<uint32_t>(1)};
  DeviceBuffer<uint64_t> out{std::vector<uint64_t>(static_cast<size_t>(count) * count * CarrylessCount)};
  carryless<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, supported.data, count);
  finish("carryless");
  if (supported.download()[0] == 0) SKIP("clmad requires compute capability 8.0 and PTX ISA 9.3");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count * count; ++i) {
    const auto expected = carrylessReference(Words[i / count], Words[i % count]);
    for (uint32_t field = 0; field < CarrylessCount; ++field)
      if (actual[(i * CarrylessCount) + field] != expected[field])
        throw std::runtime_error(fmt::format("carryless {} of {:#x} and {:#x}: expected {:#x}, got {:#x}", field, Words[i / count],
                                             Words[i % count], expected[field], actual[(i * CarrylessCount) + field]));
  }
}
