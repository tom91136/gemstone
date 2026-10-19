#include <algorithm>
#include <array>
#include <cmath>
#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <vector>

#include "cuda/support.cuh"
#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;

// Valid PTX that neither nvcc nor clang emit for ordinary source but inline assembly uses: the scalar video
// instructions with their selectors, saturation, secondary operations and merges, set and setp with a Boolean operator,
// mul24, mad24, slct, testp, and the mad.hi and mad.wide forms. References follow the PTX ISA 13.3 pseudocode.
namespace {

const std::vector<uint32_t> Words = {0x00000000U, 0x00000001U, 0x7fffffffU, 0x80000000U, 0xffffffffU, 0x01020304U,
                                     0x807f80ffU, 0x7f017f80U, 0x0000ffffU, 0xffff0000U, 0x12345678U, 0xdeadbeefU,
                                     0x00000021U, 0x0000001fU, 0x00800001U, 0xff7fffffU};

uint32_t addend(uint32_t a, uint32_t b) { return (a * 0x9e3779b9U) ^ (b >> 3) ^ 0x5a5a00a5U; }

enum class Select { Word, B0, B1, B2, B3, H0, H1 };

struct Span {
  int offset;
  int bits;
};

Span span(Select select) {
  switch (select) {
    case Select::Word: break;
    case Select::B0: return {0, 8};
    case Select::B1: return {8, 8};
    case Select::B2: return {16, 8};
    case Select::B3: return {24, 8};
    case Select::H0: return {0, 16};
    case Select::H1: return {16, 16};
  }
  return {0, 32};
}

int64_t part(uint32_t word, Select select, bool isSigned) {
  const auto [offset, bits] = span(select);
  const uint64_t field = (static_cast<uint64_t>(word) >> offset) & ((uint64_t{1} << bits) - 1U);
  return isSigned && (field >> (bits - 1)) ? static_cast<int64_t>(field) - (int64_t{1} << bits) : static_cast<int64_t>(field);
}

enum class Operation { Add, Sub, AbsDiff, Min, Max, Shl, Shr, Lt, Ne, Ge };
enum class Secondary { Plain, Add, Min, Max };

struct Scalar {
  Operation operation;
  Select a, b, merge;
  bool dSigned, aSigned, bSigned, sat, wrap;
  Secondary secondary;
};

// The s34 intermediate, clamped to the destination part where saturating, then combined or merged with c.
uint32_t scalarVideo(const Scalar &s, uint32_t a, uint32_t b, uint32_t c) {
  const int64_t ta = part(a, s.a, s.aSigned);
  const bool shift = s.operation == Operation::Shl || s.operation == Operation::Shr;
  int64_t tb = part(b, s.b, shift ? false : s.bSigned);
  if (shift) tb = s.wrap ? (tb & 0x1f) : std::min<int64_t>(tb, 32);
  int64_t tmp = 0;
  switch (s.operation) {
    case Operation::Add: tmp = ta + tb; break;
    case Operation::Sub: tmp = ta - tb; break;
    case Operation::AbsDiff: tmp = ta > tb ? ta - tb : tb - ta; break;
    case Operation::Min: tmp = std::min(ta, tb); break;
    case Operation::Max: tmp = std::max(ta, tb); break;
    case Operation::Shl: tmp = static_cast<int64_t>(static_cast<uint64_t>(ta) << tb); break;
    case Operation::Shr: tmp = ta >> tb; break;
    case Operation::Lt: tmp = ta < tb; break;
    case Operation::Ne: tmp = ta != tb; break;
    case Operation::Ge: tmp = ta >= tb; break;
  }
  tmp = static_cast<int64_t>(static_cast<uint64_t>(tmp) << 30) >> 30;
  const auto [offset, bits] = span(s.merge);
  if (s.sat) {
    const int64_t low = s.dSigned ? -(int64_t{1} << (bits - 1)) : 0;
    const int64_t high = s.dSigned ? (int64_t{1} << (bits - 1)) - 1 : (int64_t{1} << bits) - 1;
    tmp = std::clamp(tmp, low, high);
  }
  if (s.merge != Select::Word) {
    const uint32_t mask = (bits == 16 ? 0xffffU : 0xffU) << offset;
    return ((static_cast<uint32_t>(tmp) << offset) & mask) | (c & ~mask);
  }
  const int64_t tc = s.dSigned ? static_cast<int64_t>(static_cast<int32_t>(c)) : static_cast<int64_t>(c);
  switch (s.secondary) {
    case Secondary::Add: tmp += tc; break;
    case Secondary::Min: tmp = std::min(tmp, tc); break;
    case Secondary::Max: tmp = std::max(tmp, tc); break;
    case Secondary::Plain: break;
  }
  return static_cast<uint32_t>(tmp);
}

struct Mad {
  Select a, b;
  bool aSigned, bSigned, negateProduct, negateC, plusOne, sat;
  int shift;
};

uint32_t videoMad(const Mad &m, uint32_t a, uint32_t b, uint32_t c) {
  const __int128 ta = part(a, m.a, m.aSigned);
  const __int128 tb = part(b, m.b, m.bSigned);
  const bool signedFinal = m.aSigned || m.bSigned || m.negateProduct || m.negateC;
  __int128 tmp = ta * tb;
  int lsb = 0;
  if (m.plusOne) lsb = 1;
  else if (m.negateProduct) tmp = ~tmp, lsb = 1;
  else if (m.negateC) c = ~c, lsb = 1;
  const __int128 c128 = signedFinal ? static_cast<__int128>(static_cast<int32_t>(c)) : static_cast<__int128>(c);
  tmp = tmp + c128 + lsb;
  const uint64_t result = static_cast<uint64_t>(tmp >> m.shift);
  if (!m.sat) return static_cast<uint32_t>(result);
  if (signedFinal) return static_cast<uint32_t>(std::clamp<int64_t>(static_cast<int64_t>(result), INT32_MIN, INT32_MAX));
  return static_cast<uint32_t>(std::min<uint64_t>(result, UINT32_MAX));
}

const std::array<Scalar, 18> Scalars = {{
    {Operation::Add, Select::Word, Select::Word, Select::Word, false, false, false, false, false, Secondary::Plain},
    {Operation::Add, Select::H1, Select::B2, Select::Word, true, true, true, true, false, Secondary::Plain},
    {Operation::Add, Select::B0, Select::B3, Select::Word, false, false, false, false, false, Secondary::Add},
    {Operation::Sub, Select::H0, Select::H1, Select::Word, true, false, true, false, false, Secondary::Min},
    {Operation::AbsDiff, Select::Word, Select::B1, Select::Word, false, false, false, false, false, Secondary::Max},
    {Operation::Min, Select::Word, Select::Word, Select::Word, true, true, true, false, false, Secondary::Plain},
    {Operation::Max, Select::B1, Select::H1, Select::Word, false, false, true, true, false, Secondary::Plain},
    {Operation::Add, Select::Word, Select::Word, Select::B1, false, false, false, false, false, Secondary::Plain},
    {Operation::Add, Select::H1, Select::H0, Select::H0, false, false, false, true, false, Secondary::Plain},
    {Operation::AbsDiff, Select::B0, Select::B2, Select::H1, true, true, true, true, false, Secondary::Plain},
    {Operation::Min, Select::Word, Select::Word, Select::Word, true, true, true, true, false, Secondary::Add},
    {Operation::Max, Select::Word, Select::Word, Select::Word, true, false, true, false, false, Secondary::Max},
    {Operation::Shl, Select::Word, Select::Word, Select::Word, false, false, false, false, false, Secondary::Plain},
    {Operation::Shr, Select::Word, Select::Word, Select::Word, true, true, false, false, true, Secondary::Plain},
    {Operation::Shr, Select::Word, Select::B0, Select::Word, false, false, false, false, false, Secondary::Add},
    {Operation::Shl, Select::H0, Select::B0, Select::Word, true, true, false, true, true, Secondary::Plain},
    {Operation::Lt, Select::Word, Select::Word, Select::Word, false, false, true, false, false, Secondary::Plain},
    {Operation::Ne, Select::B2, Select::B2, Select::Word, false, false, false, false, false, Secondary::Add},
}};

const std::array<Mad, 6> Mads = {{
    {Select::H0, Select::B1, true, false, false, false, false, false, 0},
    {Select::H1, Select::H0, false, false, true, false, false, true, 15},
    {Select::H0, Select::H0, false, false, false, false, false, false, 7},
    {Select::Word, Select::Word, true, true, false, true, false, true, 0},
    {Select::B0, Select::B0, false, false, false, false, true, false, 0},
    {Select::H1, Select::H1, false, false, false, false, false, true, 0},
}};

constexpr uint32_t ScalarCount = 19, MadCount = 6;

// From sm_70 ptxas emulates these instructions: it applies a secondary operation to the 32-bit intermediate, clamps a
// signed merged result as unsigned, sign extends the vmad addend whatever the types and multiplies unsigned vmad words
// as signed. The inputs keep to where that agrees with the PTX ISA.
__global__ void video(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  const uint32_t c = (a * 0x9e3779b9U) ^ (b >> 3) ^ 0x5a5a00a5U;
  uint32_t *o = out + (static_cast<size_t>(i) * (ScalarCount + MadCount));
  uint32_t d = 0;
  asm("vadd.u32.u32.u32 %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[0] = d;
  asm("vadd.s32.s32.s32.sat %0, %1.h1, %2.b2;" : "=r"(d) : "r"(a), "r"(b));
  o[1] = d;
  asm("vadd.u32.u32.u32.add %0, %1.b0, %2.b3, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[2] = d;
  asm("vsub.s32.u32.s32.min %0, %1.h0, %2.h1, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[3] = d;
  asm("vabsdiff.u32.u32.u32.max %0, %1, %2.b1, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[4] = d;
  asm("vmin.s32.s32.s32 %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[5] = d;
  asm("vmax.u32.u32.s32.sat %0, %1.b1, %2.h1;" : "=r"(d) : "r"(a), "r"(b));
  o[6] = d;
  d = c;
  asm("vadd.u32.u32.u32 %0.b1, %1, %2, %0;" : "+r"(d) : "r"(a), "r"(b));
  o[7] = d;
  d = c;
  asm("vadd.u32.u32.u32.sat %0.h0, %1.h1, %2.h0, %0;" : "+r"(d) : "r"(a), "r"(b));
  o[8] = d;
  asm("vmin.s32.s32.s32.sat.add %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[10] = d;
  asm("vmax.s32.u32.s32.max %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[11] = d;
  asm("vshl.u32.u32.u32.clamp %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[12] = d;
  asm("vshr.s32.s32.u32.wrap %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[13] = d;
  asm("vshr.u32.u32.u32.clamp.add %0, %1, %2.b0, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[14] = d;
  asm("vshl.s32.s32.u32.sat.wrap %0, %1.h0, %2.b0;" : "=r"(d) : "r"(a), "r"(b));
  o[15] = d;
  asm("vset.u32.s32.lt %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[16] = d;
  asm("vset.u32.u32.ne.add %0, %1.b2, %2.b2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[17] = d;
  asm("vmad.s32.s32.u32 %0, %1.h0, %2.b1, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[19] = d;
  asm("vmad.u32.u32.u32.sat.shr15 %0, -%1.h1, %2.h0, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[20] = d;
  asm("vmad.u32.u32.u32.shr7 %0, %1.h0, %2.h0, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c & 0x7fffffffU));
  o[21] = d;
  asm("vmad.s32.s32.s32.sat %0, %1, %2, -%3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[22] = d;
  asm("vmad.u32.u32.u32.po %0, %1.b0, %2.b0, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c & 0x7fffffffU));
  o[23] = d;
  asm("vmad.u32.u32.u32.sat %0, %1.h1, %2.h1, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c & 0x7fffffffU));
  o[24] = d;
}

// From sm_70 ptxas 11.8 merges the upper half of a scalar video result into .h1 rather than its lower half, as an RTX
// 4060 Ti shows for sm_89, so those builds merge into byte 2 instead and report it through `byteMerged`.
#if defined(__CUDA_ARCH__) && __CUDA_ARCH__ >= 700 && CUDART_VERSION < 12000
  #define UPPER_MERGE "b2"
#else
  #define UPPER_MERGE "h1"
#endif

__global__ void upperMerges(const uint32_t *in, uint32_t *out, uint32_t count, uint32_t *byteMerged) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  const uint32_t c = (a * 0x9e3779b9U) ^ (b >> 3) ^ 0x5a5a00a5U;
  uint32_t *o = out + (static_cast<size_t>(i) * (ScalarCount + MadCount));
  uint32_t d = c;
  asm("vabsdiff.s32.s32.s32.sat %0." UPPER_MERGE ", %1.b0, %2.b2, %0;" : "+r"(d) : "r"(a), "r"(b));
  o[9] = d;
  d = c;
  asm("vset.s32.s32.ge %0." UPPER_MERGE ", %1, %2, %0;" : "+r"(d) : "r"(a), "r"(b));
  o[18] = d;
  if (i == 0) *byteMerged = UPPER_MERGE[0] == 'b';
}

std::vector<uint32_t> videoReference(uint32_t a, uint32_t b, bool byteMerged) {
  const uint32_t c = addend(a, b);
  const auto scalars = Scalars ^ map([&](Scalar s) {
                         if (byteMerged && s.merge == Select::H1) s.merge = Select::B2;
                         return scalarVideo(s, a, b, c);
                       });
  const auto mads = Mads ^ map([&](const Mad &m) {
                      return videoMad(m, a, b, m.aSigned || m.bSigned || m.negateProduct || m.negateC ? c : c & 0x7fffffffU);
                    });
  const auto ge = scalarVideo(
      {Operation::Ge, Select::Word, Select::Word, byteMerged ? Select::B2 : Select::H1, false, true, true, false, false, Secondary::Plain},
      a, b, c);
  return scalars ^ append(ge) ^ concat(mads);
}

} // namespace

TEST_CASE("video/cuda/scalar", "[unified][video]") {
  const uint32_t count = static_cast<uint32_t>(Words.size());
  constexpr uint32_t fields = ScalarCount + MadCount;
  DeviceBuffer<uint32_t> in(Words);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * fields));
  DeviceBuffer<uint32_t> byteMerged(std::vector<uint32_t>(1));
  video<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count);
  upperMerges<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count, byteMerged.data);
  finish("scalar video");
  const auto actual = out.download();
  const bool merged = byteMerged.download()[0] != 0;
  for (uint32_t i = 0; i < count * count; ++i) {
    const auto expected = videoReference(Words[i / count], Words[i % count], merged);
    for (size_t field = 0; field < expected.size(); ++field)
      if (actual[(static_cast<size_t>(i) * fields) + field] != expected[field])
        throw std::runtime_error(fmt::format("scalar video {} of {:#x} and {:#x}: expected {:#x}, got {:#x}", field, Words[i / count],
                                             Words[i % count], expected[field], actual[(static_cast<size_t>(i) * fields) + field]));
  }
}

namespace {
constexpr uint32_t IntegerCount = 20;

__global__ void integer(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  const uint32_t c = (a * 0x9e3779b9U) ^ (b >> 3) ^ 0x5a5a00a5U;
  const uint64_t wide = (static_cast<uint64_t>(c) << 32) | (a ^ b);
  const uint64_t la = (static_cast<uint64_t>(a) << 32) | b;
  const uint64_t lb = (static_cast<uint64_t>(b) << 32) | c;
  uint32_t *o = out + (static_cast<size_t>(i) * IntegerCount);
  uint32_t d = 0;
  uint64_t w = 0;
  asm("mul24.lo.s32 %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[0] = d;
  asm("mul24.lo.u32 %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[1] = d;
  asm("mul24.hi.s32 %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[2] = d;
  asm("mul24.hi.u32 %0, %1, %2;" : "=r"(d) : "r"(a), "r"(b));
  o[3] = d;
  asm("mad24.lo.s32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[4] = d;
  asm("mad24.hi.u32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[5] = d;
  asm("mad24.hi.sat.s32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[6] = d;
  asm("mad.hi.u32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[7] = d;
  asm("mad.hi.s32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[8] = d;
  asm("mad.hi.u64 %0, %1, %2, %3;" : "=l"(w) : "l"(la), "l"(lb), "l"(wide));
  o[12] = static_cast<uint32_t>(w);
  o[13] = static_cast<uint32_t>(w >> 32);
  asm("mad.hi.s64 %0, %1, %2, %3;" : "=l"(w) : "l"(la), "l"(lb), "l"(wide));
  o[14] = static_cast<uint32_t>(w);
  o[15] = static_cast<uint32_t>(w >> 32);
  asm("{ .reg .b16 x, y; cvt.u16.u32 x, %1; cvt.u16.u32 y, %2; mad.wide.s16 %0, x, y, %3; }" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[16] = d;
  asm("{ .reg .b16 x, y, z, r; cvt.u16.u32 x, %1; cvt.u16.u32 y, %2; cvt.u16.u32 z, %3; mad.hi.u16 r, x, y, z; "
      "cvt.u32.u16 %0, r; }"
      : "=r"(d)
      : "r"(a), "r"(b), "r"(c));
  o[17] = d;
  asm("slct.s32.s32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  o[18] = d;
  asm("slct.b32.s32 %0, %1, %2, %3;" : "=r"(d) : "r"(c), "r"(b), "r"(a));
  o[19] = d;
}

// ptxas 13.3 folds mad.hi.sat.s32 into a mad.wide.s32 of the same operands and loses the saturation.
__global__ void highSaturated(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  const uint32_t c = (a * 0x9e3779b9U) ^ (b >> 3) ^ 0x5a5a00a5U;
  uint32_t d = 0;
  asm("mad.hi.sat.s32 %0, %1, %2, %3;" : "=r"(d) : "r"(a), "r"(b), "r"(c));
  out[(i * IntegerCount) + 9] = d;
}

// ptxas 11.8 miscompiles mad.wide.s32 and a mad.hi of the same operands that share a kernel, as an RTX 4060 Ti shows
// for sm_89.
__global__ void wideSigned(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  const uint32_t c = (a * 0x9e3779b9U) ^ (b >> 3) ^ 0x5a5a00a5U;
  const uint64_t wide = (static_cast<uint64_t>(c) << 32) | (a ^ b);
  uint64_t w = 0;
  asm("mad.wide.s32 %0, %1, %2, %3;" : "=l"(w) : "r"(a), "r"(b), "l"(wide));
  out[(i * IntegerCount) + 10] = static_cast<uint32_t>(w);
  out[(i * IntegerCount) + 11] = static_cast<uint32_t>(w >> 32);
}

int64_t narrow(uint32_t value, bool isSigned) {
  return isSigned ? static_cast<int64_t>(static_cast<int32_t>(value << 8) >> 8) : static_cast<int64_t>(value & 0xffffffU);
}

std::vector<uint32_t> integerReference(uint32_t a, uint32_t b) {
  const uint32_t c = addend(a, b);
  const uint64_t wide = (static_cast<uint64_t>(c) << 32) | (a ^ b);
  const uint64_t la = (static_cast<uint64_t>(a) << 32) | b;
  const uint64_t lb = (static_cast<uint64_t>(b) << 32) | c;
  const int64_t ps = narrow(a, true) * narrow(b, true);
  const int64_t pu = narrow(a, false) * narrow(b, false);
  const int64_t s32 = static_cast<int64_t>(static_cast<int32_t>(a)) * static_cast<int32_t>(b);
  const uint64_t u32 = static_cast<uint64_t>(a) * b;
  const int64_t highSum = static_cast<int64_t>(static_cast<int32_t>(static_cast<uint32_t>(s32 >> 32))) + static_cast<int32_t>(c);
  const uint64_t madWide = static_cast<uint64_t>(s32) + wide;
  const unsigned __int128 hu = static_cast<unsigned __int128>(la) * lb;
  const __int128 hs = static_cast<__int128>(static_cast<int64_t>(la)) * static_cast<int64_t>(lb);
  const uint64_t madHiU64 = static_cast<uint64_t>(hu >> 64) + wide;
  const uint64_t madHiS64 = static_cast<uint64_t>(hs >> 64) + wide;
  const uint32_t madWideS16 = static_cast<uint32_t>(static_cast<int32_t>(static_cast<int16_t>(a)) * static_cast<int16_t>(b)) + c;
  const uint32_t madHiU16 = static_cast<uint16_t>(((a & 0xffffU) * (b & 0xffffU) >> 16) + c);
  return {static_cast<uint32_t>(ps),
          static_cast<uint32_t>(pu),
          static_cast<uint32_t>(ps >> 16),
          static_cast<uint32_t>(pu >> 16),
          static_cast<uint32_t>(ps) + c,
          static_cast<uint32_t>(pu >> 16) + c,
          static_cast<uint32_t>(std::clamp<int64_t>(
              static_cast<int32_t>(static_cast<uint32_t>(ps >> 16)) + static_cast<int64_t>(static_cast<int32_t>(c)), INT32_MIN, INT32_MAX)),
          static_cast<uint32_t>(u32 >> 32) + c,
          static_cast<uint32_t>(s32 >> 32) + c,
          static_cast<uint32_t>(std::clamp<int64_t>(highSum, INT32_MIN, INT32_MAX)),
          static_cast<uint32_t>(madWide),
          static_cast<uint32_t>(madWide >> 32),
          static_cast<uint32_t>(madHiU64),
          static_cast<uint32_t>(madHiU64 >> 32),
          static_cast<uint32_t>(madHiS64),
          static_cast<uint32_t>(madHiS64 >> 32),
          madWideS16,
          madHiU16,
          static_cast<int32_t>(c) >= 0 ? a : b,
          static_cast<int32_t>(a) >= 0 ? c : b};
}

} // namespace

TEST_CASE("video/cuda/integer", "[unified][video]") {
  const uint32_t count = static_cast<uint32_t>(Words.size());
  DeviceBuffer<uint32_t> in(Words);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * IntegerCount));
  integer<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count);
  highSaturated<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count);
  wideSigned<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count);
  finish("integer");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count * count; ++i) {
    const auto expected = integerReference(Words[i / count], Words[i % count]);
    for (size_t field = 0; field < expected.size(); ++field)
      if (actual[(static_cast<size_t>(i) * IntegerCount) + field] != expected[field])
        throw std::runtime_error(fmt::format("integer {} of {:#x} and {:#x}: expected {:#x}, got {:#x}", field, Words[i / count],
                                             Words[i % count], expected[field], actual[(static_cast<size_t>(i) * IntegerCount) + field]));
  }
}

namespace {
const std::vector<uint32_t> Floats = {0x00000000U, 0x80000000U, 0x3f800000U, 0xbf800000U, 0x00000001U, 0x807fffffU,
                                      0x00800000U, 0x7f7fffffU, 0x7f800000U, 0xff800000U, 0x7fc00000U, 0xffc00001U};

constexpr uint32_t FloatCount = 22;

__global__ void floating(const uint32_t *in, const uint32_t *words, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  const float x = __uint_as_float(a);
  const float y = __uint_as_float(b);
  const double wx = static_cast<double>(x);
  const uint32_t ia = words[i / count % 16];
  const uint32_t ib = words[(i + 5) % 16];
  uint32_t *o = out + (static_cast<size_t>(i) * FloatCount);
  uint32_t d = 0;
  asm("{ .reg .pred p; testp.finite.f32 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "f"(x));
  o[0] = d;
  asm("{ .reg .pred p; testp.infinite.f32 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "f"(x));
  o[1] = d;
  asm("{ .reg .pred p; testp.number.f32 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "f"(x));
  o[2] = d;
  asm("{ .reg .pred p; testp.notanumber.f32 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "f"(x));
  o[3] = d;
  asm("{ .reg .pred p; testp.normal.f32 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "f"(x));
  o[4] = d;
  asm("{ .reg .pred p; testp.subnormal.f32 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "f"(x));
  o[5] = d;
  asm("{ .reg .pred p; testp.normal.f64 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "d"(wx));
  o[6] = d;
  asm("{ .reg .pred p; testp.notanumber.f64 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "d"(wx));
  o[7] = d;
  asm("slct.u32.f32 %0, %1, %2, %3;" : "=r"(d) : "r"(ia), "r"(ib), "f"(x));
  o[8] = d;
  asm("slct.ftz.u32.f32 %0, %1, %2, %3;" : "=r"(d) : "r"(ia), "r"(ib), "f"(x));
  o[9] = d;
  asm("{ .reg .pred p; setp.gt.u32 p, %3, 7; set.lt.and.u32.s32 %0, %1, %2, p; }" : "=r"(d) : "r"(ia), "r"(ib), "r"(a & 15U));
  o[10] = d;
  float f = NAN;
  asm("{ .reg .pred p; setp.gt.u32 p, %3, 7; set.ltu.or.f32.f32 %0, %1, %2, !p; }" : "=f"(f) : "f"(x), "f"(y), "r"(b & 15U));
  o[11] = __float_as_uint(f);
  asm("{ .reg .pred p; setp.gt.u32 p, %3, 7; set.ge.xor.s32.f32 %0, %1, %2, p; }" : "=r"(d) : "f"(x), "f"(y), "r"(a & 15U));
  o[12] = d;
  asm("{ .reg .pred p; setp.gt.u32 p, %3, 7; set.num.and.ftz.u32.f32 %0, %1, %2, p; }" : "=r"(d) : "f"(x), "f"(y), "r"(b & 15U));
  o[13] = d;
  asm("{ .reg .pred p, q; setp.gt.u32 p, %3, 7; setp.hi.and.u32 q, %1, %2, !p; selp.u32 %0, 1, 0, q; }"
      : "=r"(d)
      : "r"(ia), "r"(ib), "r"(a & 15U));
  o[14] = d;
  asm("{ .reg .pred p, q; setp.gt.u32 p, %3, 7; setp.equ.or.f32 q, %1, %2, p; selp.u32 %0, 1, 0, q; }"
      : "=r"(d)
      : "f"(x), "f"(y), "r"(b & 15U));
  o[15] = d;
  asm("{ .reg .pred p, q; setp.gt.u32 p, %3, 7; setp.ls.xor.u32 q, %1, %2, p; selp.u32 %0, 1, 0, q; }"
      : "=r"(d)
      : "r"(ia), "r"(ib), "r"(a & 15U));
  o[16] = d;
  asm("set.lo.u32.u32 %0, %1, %2;" : "=r"(d) : "r"(ia), "r"(ib));
  o[17] = d;
  asm("set.hs.u32.u32 %0, %1, %2;" : "=r"(d) : "r"(ia), "r"(ib));
  o[18] = d;
  asm("{ .reg .pred p; testp.subnormal.f64 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "d"(wx * 0x1p-1000));
  o[19] = d;
  asm("{ .reg .pred p; testp.finite.f64 p, %1; selp.u32 %0, 1, 0, p; }" : "=r"(d) : "d"(wx * 0x1p1000 * 0x1p100));
  o[20] = d;
  asm("{ .reg .pred p; setp.gt.u32 p, %3, 7; set.neu.and.f32.f64 %0, %1, %2, p; }"
      : "=f"(f)
      : "d"(wx), "d"(static_cast<double>(y)), "r"(a & 15U));
  o[21] = __float_as_uint(f);
}

using unified::support::bitsFloat;
using unified::support::doubleBits;
using unified::support::isNan;
uint32_t flushed(uint32_t bits) { return (bits & 0x7f800000U) == 0 ? bits & 0x80000000U : bits; }

std::vector<uint32_t> floatReference(uint32_t a, uint32_t b, uint32_t ia, uint32_t ib) {
  const float x = bitsFloat(a);
  const float y = bitsFloat(b);
  const uint32_t magnitude = a & 0x7fffffffU;
  const uint32_t exponent = a & 0x7f800000U;
  const bool nan = isNan(a);
  const bool p = (a & 15U) > 7;
  const bool q = (b & 15U) > 7;
  const auto truth = [](bool value) { return value ? 0xffffffffU : 0U; };
  const auto one = [](bool value) { return value ? 0x3f800000U : 0U; };
  const double wide = static_cast<double>(x);
  const double tiny = wide * 0x1p-1000;
  const double huge = wide * 0x1p1000 * 0x1p100;
  const uint64_t tinyBits = doubleBits(tiny);
  const bool tinySubnormal = (tinyBits & 0x7ff0000000000000ULL) == 0 && (tinyBits & 0x000fffffffffffffULL) != 0;
  const bool slct = !nan && (!(a >> 31) || magnitude == 0);
  const bool slctFtz = !nan && (!(a >> 31) || (flushed(a) & 0x7fffffffU) == 0);
  const bool lessUnordered = isNan(a) || isNan(b) || x < y;
  const bool num = !isNan(flushed(a)) && !isNan(flushed(b));
  const bool hi = ia > ib;
  const bool ls = ia <= ib;
  const bool equalUnordered = isNan(a) || isNan(b) || x == y;
  const bool notEqualUnordered = isNan(a) || isNan(b) || wide != static_cast<double>(y);
  return {!nan && magnitude != 0x7f800000U,
          magnitude == 0x7f800000U,
          !nan,
          nan,
          !nan && magnitude != 0x7f800000U && (exponent != 0 || magnitude == 0),
          exponent == 0 && magnitude != 0,
          !nan && magnitude != 0x7f800000U,
          nan,
          slct ? ia : ib,
          slctFtz ? ia : ib,
          truth((static_cast<int32_t>(ia) < static_cast<int32_t>(ib)) && p),
          one(lessUnordered || !q),
          truth((!isNan(a) && !isNan(b) && x >= y) != p),
          truth(num && q),
          hi && !p,
          equalUnordered || q,
          ls != p,
          truth(ia < ib),
          truth(ia >= ib),
          tinySubnormal,
          !std::isnan(huge) && !std::isinf(huge),
          one(notEqualUnordered && p)};
}

} // namespace

TEST_CASE("video/cuda/floating", "[unified][video]") {
  const uint32_t count = static_cast<uint32_t>(Floats.size());
  DeviceBuffer<uint32_t> in(Floats);
  DeviceBuffer<uint32_t> integers(Words);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * FloatCount));
  floating<<<((count * count) + 63) / 64, 64>>>(in.data, integers.data, out.data, count);
  finish("floating");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count * count; ++i) {
    const auto expected = floatReference(Floats[i / count], Floats[i % count], Words[i / count % 16], Words[(i + 5) % 16]);
    for (size_t field = 0; field < expected.size(); ++field)
      if (actual[(static_cast<size_t>(i) * FloatCount) + field] != expected[field])
        throw std::runtime_error(fmt::format("floating {} of {:#x} and {:#x}: expected {:#x}, got {:#x}", field, Floats[i / count],
                                             Floats[i % count], expected[field], actual[(static_cast<size_t>(i) * FloatCount) + field]));
  }
}
