#include <cmath>
#include <cstdint>
#include <cstring>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "support.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;
using namespace hip_support;

// The GFX11 and GFX12 dot products hipcc emits from builtins, each checked against a host reference: v_dot4_i32_iu8
// and v_dot8_i32_iu4, whose NEG_LO bits sign each source's elements, v_dot2_f16_f16 and v_dot2_bf16_bf16, and GFX12's
// v_dot4_f32 of OCP 8-bit floats. Floating-point inputs keep every product and sum exact, which gfx1100 to gfx1103 and gfx1150
// to gfx1153 lose to their fixed-point accumulator.
namespace {

constexpr uint32_t IntegerResults = 4;

// __builtin_amdgcn_sudot4 and sudot8: v_dot4_i32_iu8 and v_dot8_i32_iu4 with neg_lo naming the signed sources, the
// clamp saturating the signed sum.
__global__ void integerDots([[maybe_unused]] const uint32_t *a, [[maybe_unused]] const uint32_t *b, [[maybe_unused]] const int32_t *c,
                            [[maybe_unused]] int32_t *out, [[maybe_unused]] uint32_t *supported, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
#if defined(__GFX11__) || defined(__GFX12__)
  int32_t *o = out + i * IntegerResults;
  const int x = static_cast<int>(a[i]), y = static_cast<int>(b[i]);
  o[0] = __builtin_amdgcn_sudot4(true, x, false, y, c[i], false);
  o[1] = __builtin_amdgcn_sudot4(false, x, true, y, c[i], true);
  o[2] = __builtin_amdgcn_sudot8(true, x, true, y, c[i], false);
  o[3] = __builtin_amdgcn_sudot8(false, x, false, y, c[i], true);
  if (i == 0) *supported = 1;
#endif
}

int64_t element(uint32_t packed, int width, int index, bool sign) {
  const uint32_t mask = (1U << width) - 1U;
  const uint32_t value = (packed >> (index * width)) & mask;
  return sign && (value >> (width - 1)) != 0 ? static_cast<int64_t>(value) - (int64_t{1} << width) : value;
}

int32_t integerDot(uint32_t a, uint32_t b, int32_t c, int width, bool signA, bool signB, bool clamp) {
  int64_t sum = iota(0, 32 / width) | fold_left(int64_t{c}, [&](int64_t acc, int index) {
                  return acc + (element(a, width, index, signA) * element(b, width, index, signB));
                });
  if (clamp) sum = std::max<int64_t>(INT32_MIN, std::min<int64_t>(INT32_MAX, sum));
  return static_cast<int32_t>(static_cast<uint32_t>(sum));
}

} // namespace

TEST_CASE("intrinsics/hip/mixed-sign-integer-dots", "[unified][intrinsics]") {
  constexpr uint32_t count = 64;
  const auto a = iota(0U, count) | map([](uint32_t i) { return 0x9e3779b9U * (i + 1U); }) | to_vector();
  const auto b = iota(0U, count) | map([](uint32_t i) { return (0x85ebca6bU * (i + 7U)) ^ (i << 13); }) | to_vector();
  const auto c = iota(0U, count) | map([](uint32_t i) -> int32_t {
                   if (i % 4 == 0) return INT32_MAX - static_cast<int32_t>(i);
                   if (i % 4 == 1) return INT32_MIN + static_cast<int32_t>(i);
                   return static_cast<int32_t>(i * 977U) - 30000;
                 })
                 | to_vector();
  DeviceBuffer<uint32_t> x(a);
  DeviceBuffer<uint32_t> y(b);
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  DeviceBuffer<int32_t> z(c);
  DeviceBuffer<int32_t> out(std::vector<int32_t>(static_cast<size_t>(count) * IntegerResults));
  integerDots<<<1, count>>>(x.data, y.data, z.data, out.data, supported.data, count);
  finish("mixed-sign integer dots");
  if (supported.download()[0] == 0) SKIP("mixed-sign integer dots need GFX11 or GFX12");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count; ++i) {
    const int32_t expected[IntegerResults] = {
        integerDot(a[i], b[i], c[i], 8, true, false, false), integerDot(a[i], b[i], c[i], 8, false, true, true),
        integerDot(a[i], b[i], c[i], 4, true, true, false), integerDot(a[i], b[i], c[i], 4, false, false, true)};
    for (uint32_t result = 0; result < IntegerResults; ++result)
      if (actual[(i * IntegerResults) + result] != expected[result])
        throw std::runtime_error(fmt::format("integer dot {} of element {}: {} instead of {}", result, i,
                                             actual[(i * IntegerResults) + result], expected[result]));
  }
}

namespace {
using Halves2 = _Float16 __attribute__((ext_vector_type(2)));
using Shorts2 = short __attribute__((ext_vector_type(2)));

// __builtin_amdgcn_fdot2_f16_f16 and fdot2_bf16_bf16: v_dot2_f16_f16 and v_dot2_bf16_bf16. Small multiples of a
// quarter keep the products and sums exact in both formats.
__global__ void halfDots([[maybe_unused]] const float *a, [[maybe_unused]] const float *b, [[maybe_unused]] const float *c,
                         [[maybe_unused]] uint32_t *out, [[maybe_unused]] uint32_t *supported, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
#if defined(__GFX11__) || defined(__GFX12__)
  const Halves2 x = {static_cast<_Float16>(a[2 * i]), static_cast<_Float16>(a[2 * i + 1])};
  const Halves2 y = {static_cast<_Float16>(b[2 * i]), static_cast<_Float16>(b[2 * i + 1])};
  const _Float16 h = __builtin_amdgcn_fdot2_f16_f16(x, y, static_cast<_Float16>(c[i]));
  out[2 * i] = static_cast<uint32_t>(__builtin_bit_cast(uint16_t, h));
  const auto bfloat = [](float value) { return static_cast<short>(__float_as_uint(value) >> 16); };
  const Shorts2 u = {bfloat(a[2 * i]), bfloat(a[2 * i + 1])}, v = {bfloat(b[2 * i]), bfloat(b[2 * i + 1])};
  out[2 * i + 1] = static_cast<uint16_t>(__builtin_amdgcn_fdot2_bf16_bf16(u, v, bfloat(c[i])));
  if (i == 0) *supported = 1;
#endif
}

uint32_t halfBits(float value) { return static_cast<uint32_t>(__builtin_bit_cast(uint16_t, static_cast<_Float16>(value))); }

uint32_t bfloatBits(float value) { return unified::support::floatBits(value) >> 16; }

} // namespace

TEST_CASE("intrinsics/hip/half-dots", "[unified][intrinsics]") {
  constexpr uint32_t count = 64;
  std::vector<float> a(static_cast<size_t>(count) * 2);
  std::vector<float> b(static_cast<size_t>(count) * 2);
  std::vector<float> c(count);
  for (size_t i = 0; i < count; ++i) {
    a[2 * i] = static_cast<float>(static_cast<int>(i % 9) - 4) * 0.25F;
    a[(2 * i) + 1] = static_cast<float>(i % 5) * 0.5F;
    b[2 * i] = static_cast<float>(static_cast<int>(i % 7) - 3);
    b[(2 * i) + 1] = static_cast<float>(i % 3) + 0.25F;
    c[i] = static_cast<float>(static_cast<int>(i % 13) - 6) * 0.125F;
  }
  DeviceBuffer<float> x(a);
  DeviceBuffer<float> y(b);
  DeviceBuffer<float> z(c);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * 2));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  halfDots<<<1, count>>>(x.data, y.data, z.data, out.data, supported.data, count);
  finish("half dots");
  if (supported.download()[0] == 0) SKIP("half dots need GFX11 or GFX12");
  const auto actual = out.download();
  const bool aligned = alignedDots(architecture());
  for (size_t i = 0; i < count; ++i) {
    const float exact = (a[2 * i] * b[2 * i]) + (a[(2 * i) + 1] * b[(2 * i) + 1]) + c[i];
    const auto summed = [&](DotResult result) { return alignedDot(a[2 * i], b[2 * i], a[(2 * i) + 1], b[(2 * i) + 1], c[i], result); };
    const uint32_t wantedHalf = aligned ? halfBitsOf(summed(DotResult::Half)) : halfBits(exact);
    const uint32_t wantedBfloat = aligned ? bfloatBits(static_cast<float>(summed(DotResult::BFloat16))) : bfloatBits(exact);
    if (actual[2 * i] != wantedHalf || actual[(2 * i) + 1] != wantedBfloat)
      throw std::runtime_error(fmt::format("half dot {}: {:#x} and {:#x} for {}", i, actual[2 * i], actual[(2 * i) + 1], exact));
  }
}

namespace {
// An OCP 8-bit float: E4M3 with NaN alone, E5M2 with infinities too.
double eightBit(uint32_t code, bool e5m2) {
  code &= 0xffU;
  const int fraction = e5m2 ? 2 : 3;
  const int bias = e5m2 ? 15 : 7;
  const int exponent = static_cast<int>(code & 0x7fU) >> fraction;
  const int mantissa = static_cast<int>(code) & ((1 << fraction) - 1);
  const double magnitude = exponent == 0 ? std::ldexp(static_cast<double>(mantissa), 1 - bias - fraction)
                                         : std::ldexp(static_cast<double>((1 << fraction) | mantissa), exponent - bias - fraction);
  return (code & 0x80U) != 0 ? -magnitude : magnitude;
}

// __builtin_amdgcn_dot4_f32_fp8_bf8 and its siblings: v_dot4_f32_*8_*8. The codes keep clear of NaN and infinity, and
// their products and sums stay exact in binary32.
__global__ void eightBitDots([[maybe_unused]] const uint32_t *a, [[maybe_unused]] const uint32_t *b, [[maybe_unused]] const float *c,
                             [[maybe_unused]] float *out, [[maybe_unused]] uint32_t *supported, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
#ifdef __GFX12__
  const int x = static_cast<int>(a[i]), y = static_cast<int>(b[i]);
  out[4 * i] = __builtin_amdgcn_dot4_f32_fp8_bf8(x, y, c[i]);
  out[4 * i + 1] = __builtin_amdgcn_dot4_f32_bf8_fp8(x, y, c[i]);
  out[4 * i + 2] = __builtin_amdgcn_dot4_f32_fp8_fp8(x, y, c[i]);
  out[4 * i + 3] = __builtin_amdgcn_dot4_f32_bf8_bf8(x, y, c[i]);
  if (i == 0) *supported = 1;
#endif
}

} // namespace

TEST_CASE("intrinsics/hip/eight-bit-float-dots", "[unified][intrinsics]") {
  constexpr uint32_t count = 64;
  // codes whose exponent field stays within 3..12 of E5M2's and E4M3's ranges, so no element is special
  const auto code = [](uint32_t seed) { return ((((seed * 37U) + 11U) % 40U) + 24U) | ((seed & 1U) << 7); };
  const auto a = iota(0U, count) | map([&](uint32_t i) { return code(i) | (code(i + 3) << 8) | (code(i + 5) << 16) | (code(i + 9) << 24); })
                 | to_vector();
  const auto b = iota(0U, count)
                 | map([&](uint32_t i) { return code(i + 1) | (code(i + 2) << 8) | (code(i + 7) << 16) | (code(i + 11) << 24); })
                 | to_vector();
  const auto c = iota(0U, count) | map([](uint32_t i) { return static_cast<float>(static_cast<int>(i % 11) - 5) * 0.5F; }) | to_vector();
  DeviceBuffer<uint32_t> x(a);
  DeviceBuffer<uint32_t> y(b);
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  DeviceBuffer<float> z(c);
  DeviceBuffer<float> out(std::vector<float>(static_cast<size_t>(count) * 4));
  eightBitDots<<<1, count>>>(x.data, y.data, z.data, out.data, supported.data, count);
  finish("eight-bit float dots");
  if (supported.download()[0] == 0) SKIP("8-bit float dots need GFX12");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count; ++i)
    for (uint32_t form = 0; form < 4; ++form) {
      const bool e5m2A = form == 1 || form == 3;
      const bool e5m2B = form == 0 || form == 3;
      const double sum = iota(0, 4) | fold_left(static_cast<double>(c[i]), [&](double acc, int index) {
                           return acc + (eightBit(a[i] >> (8 * index), e5m2A) * eightBit(b[i] >> (8 * index), e5m2B));
                         });
      if (actual[(4 * i) + form] != static_cast<float>(sum))
        throw std::runtime_error(fmt::format("8-bit float dot {} of element {}: {} instead of {}", form, i, actual[(4 * i) + form], sum));
    }
}
