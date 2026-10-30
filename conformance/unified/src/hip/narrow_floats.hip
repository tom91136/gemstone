#include <cmath>
#include <cstdint>
#include <cstring>
#include <limits>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <hip/hip_fp16.h>
#include <hip/hip_runtime.h>

#include "support.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;
using namespace hip_support;

// Narrow-float arithmetic hipcc emits from builtins and casts, each checked against a host reference: the 8-bit float
// conversions of gfx942 (FNUZ formats) and of gfx950 and GFX12 (OCP formats), bfloat16 dot products, row lane permutes
// that each lane selects for itself, and the mixed-precision FMA of half products.
namespace {

using unified::support::bitsFloat;
using unified::support::floatBits;

// An 8-bit float format: OCP E4M3 has NaN alone, OCP E5M2 infinities too, and the FNUZ formats' one NaN is 0x80.
struct Format {
  int fraction;
  int bias;
  bool fnuz;

  [[nodiscard]] int top() const { return 0x7f >> fraction; }

  [[nodiscard]] double decode(uint32_t code) const {
    code &= 0xffU;
    if (fnuz && code == 0x80U) return std::numeric_limits<double>::quiet_NaN();
    const int exponent = static_cast<int>(code & 0x7fU) >> fraction;
    const int mantissa = static_cast<int>(code) & ((1 << fraction) - 1);
    double value = NAN;
    if (!fnuz && fraction == 3 && (code & 0x7fU) == 0x7fU) return std::numeric_limits<double>::quiet_NaN();
    if (!fnuz && fraction == 2 && exponent == top())
      value = mantissa == 0 ? std::numeric_limits<double>::infinity() : std::numeric_limits<double>::quiet_NaN();
    else if (exponent == 0) value = std::ldexp(static_cast<double>(mantissa), 1 - bias - fraction);
    else value = std::ldexp(static_cast<double>((1 << fraction) | mantissa), exponent - bias - fraction);
    return (code & 0x80U) != 0 && !std::isnan(value) ? -value : value;
  }

  [[nodiscard]] double largest() const {
    const int exponent = !fnuz && fraction == 2 ? top() - 1 : top();
    const int mantissa = !fnuz && fraction == 3 ? (1 << fraction) - 2 : (1 << fraction) - 1;
    return std::ldexp(static_cast<double>((1 << fraction) | mantissa), exponent - bias - fraction);
  }

  // Without MODE.FP16_OVFL, a magnitude beyond the largest becomes infinity where the format has one, else NaN.
  [[nodiscard]] uint32_t overflow(bool negative) const {
    if (fnuz) return 0x80U;
    if (fraction == 2) return (negative ? 0x80U : 0U) | 0x7cU;
    return (negative ? 0x80U : 0U) | 0x7fU;
  }

  // Rounds to nearest even, or toward zero when `truncate`.
  [[nodiscard]] uint32_t encode(double value, bool truncate) const {
    if (std::isnan(value)) {
      if (fnuz) return 0x80U;
      return std::signbit(value) ? 0xffU : 0x7fU;
    }
    const bool negative = std::signbit(value);
    const double magnitude = std::fabs(value);
    if (magnitude > largest()) return overflow(negative);
    const int minimum = 1 - bias;
    const int exponent = std::max(magnitude == 0.0 ? minimum : std::ilogb(magnitude), minimum);
    const double scaled = std::ldexp(magnitude, fraction - exponent);
    double integral = truncate ? std::floor(scaled) : std::nearbyint(scaled);
    int adjusted = exponent;
    if (integral >= static_cast<double>(2 << fraction)) {
      integral /= 2.0;
      adjusted += 1;
    }
    const uint32_t mantissa = static_cast<uint32_t>(integral);
    uint32_t code = mantissa >= (1U << fraction)
                        ? (static_cast<uint32_t>(adjusted + bias) << fraction) | (mantissa & ((1U << fraction) - 1U))
                        : mantissa;
    if (code == 0 && fnuz) return 0;
    return (negative ? 0x80U : 0U) | code;
  }

  // The seed's high bits add to the dropped significand bits, wrapping within the significand, then the sum truncates.
  [[nodiscard]] uint32_t stochastic(float value, uint32_t seed) const {
    const uint32_t raw = floatBits(value);
    if ((raw & 0x7f800000U) == 0x7f800000U) return encode(value, true);
    const int dropped = 23 - fraction;
    const uint32_t mantissa = ((raw & 0x7fffffU) + (seed >> (32 - dropped))) & 0x7fffffU;
    return encode(bitsFloat((raw & 0xff800000U) | mantissa), true);
  }
};

constexpr uint32_t EightBitResults = 8;

using Floats2 = float __attribute__((ext_vector_type(2)));

// __builtin_amdgcn_cvt_f32_fp8 and _bf8 (v_cvt_f32_fp8 with an SDWA or byte_sel byte), cvt_pk_f32_fp8, cvt_pk_fp8_f32,
// cvt_pk_bf8_f32 and cvt_sr_fp8_f32 and _bf8, which merge into a previous dword.
__global__ void eightBit([[maybe_unused]] const float *values, [[maybe_unused]] const uint32_t *codes,
                         [[maybe_unused]] const uint32_t *seeds, [[maybe_unused]] uint32_t *out, [[maybe_unused]] uint32_t *format,
                         uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
#if defined(__gfx942__) || defined(__gfx950__) || defined(__GFX12__)
  uint32_t *o = out + (static_cast<size_t>(i) * EightBitResults);
  const int code = static_cast<int>(codes[i]);
  o[0] = __float_as_uint(__builtin_amdgcn_cvt_f32_fp8(code, 1));
  o[1] = __float_as_uint(__builtin_amdgcn_cvt_f32_bf8(code, 2));
  const Floats2 pair = __builtin_amdgcn_cvt_pk_f32_fp8(code, true);
  o[2] = __float_as_uint(pair.x);
  o[3] = __float_as_uint(pair.y);
  o[4] = static_cast<uint32_t>(
      __builtin_amdgcn_cvt_pk_fp8_f32(values[static_cast<size_t>(i) * 2], values[(static_cast<size_t>(i) * 2) + 1], 0x12345678, false));
  o[5] = static_cast<uint32_t>(
      __builtin_amdgcn_cvt_pk_bf8_f32(values[static_cast<size_t>(i) * 2], values[(static_cast<size_t>(i) * 2) + 1], 0x12345678, true));
  o[6] =
      static_cast<uint32_t>(__builtin_amdgcn_cvt_sr_fp8_f32(values[static_cast<size_t>(i) * 2], static_cast<int>(seeds[i]), 0x12345678, 1));
  o[7] = static_cast<uint32_t>(
      __builtin_amdgcn_cvt_sr_bf8_f32(values[(static_cast<size_t>(i) * 2) + 1], static_cast<int>(seeds[i]), 0x12345678, 3));
  #ifdef __gfx942__
  if (i == 0) *format = 1;
  #else
  if (i == 0) *format = 2;
  #endif
#endif
}

bool sameFloat(uint32_t actual, double expected) {
  if (std::isnan(expected)) return std::isnan(bitsFloat(actual));
  return actual == floatBits(static_cast<float>(expected));
}

} // namespace

TEST_CASE("intrinsics/hip/eight-bit-floats", "[unified][intrinsics]") {
  const float nan = std::numeric_limits<float>::quiet_NaN();
  const float infinity = std::numeric_limits<float>::infinity();
  const std::vector<float> values = {1.0F,     -2.0F,     0.4375F, 3.25F,   240.0F,   250.0F, 448.0F,     460.0F,
                                     1.0e-3F,  -1.0e-6F,  0.0F,    -0.0F,   57344.0F, 1.0e5F, nan,        2.5F,
                                     infinity, -infinity, 1.0625F, 1.1875F, -7.5F,    0.125F, 0.0078125F, 1.5e-5F,
                                     96.0F,    -100.0F,   13.0F,   0.33F,   6.0F,     -0.5F,  31.0F,      0.75F};
  const uint32_t count = static_cast<uint32_t>(values.size() / 2);
  const auto codes =
      iota(0U, count) | map([](uint32_t i) {
        return (0x01000000U * (i * 37U % 256U)) + (0x00010000U * (i * 91U % 256U)) + (0x00000100U * (i * 13U % 256U)) + (i * 7U % 256U);
      })
      | to_vector();
  const auto seeds = iota(0U, count) | map([](uint32_t i) {
                       if (i % 3 == 0) return 0U;
                       if (i % 3 == 1) return 0xffffffffU;
                       return 0x9e3779b9U * (i + 1U);
                     })
                     | to_vector();
  DeviceBuffer<float> in(values);
  DeviceBuffer<uint32_t> codeBuffer(codes);
  DeviceBuffer<uint32_t> seedBuffer(seeds);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * EightBitResults));
  DeviceBuffer<uint32_t> format(std::vector<uint32_t>(1));
  eightBit<<<1, count>>>(in.data, codeBuffer.data, seedBuffer.data, out.data, format.data, count);
  finish("eight-bit floats");
  const uint32_t kind = format.download()[0];
  if (kind == 0) SKIP("8-bit float conversions need gfx942, gfx950 or GFX12");
  const bool fnuz = kind == 1;
  const Format fp8{.fraction = 3, .bias = fnuz ? 8 : 7, .fnuz = fnuz};
  const Format bf8{.fraction = 2, .bias = fnuz ? 16 : 15, .fnuz = fnuz};
  const auto actual = out.download();
  for (size_t i = 0; i < count; ++i) {
    const uint32_t *o = actual.data() + (i * EightBitResults);
    const auto failed = [&](int result) {
      return std::runtime_error(fmt::format("eight-bit result {} of element {} (code {:#x}, values {}, {}): {:#x}", result, i, codes[i],
                                            values[2 * i], values[(2 * i) + 1], o[result]));
    };
    if (!sameFloat(o[0], fp8.decode(codes[i] >> 8))) throw failed(0);
    if (!sameFloat(o[1], bf8.decode(codes[i] >> 16))) throw failed(1);
    if (!sameFloat(o[2], fp8.decode(codes[i] >> 16))) throw failed(2);
    if (!sameFloat(o[3], fp8.decode(codes[i] >> 24))) throw failed(3);
    const uint32_t low = fp8.encode(values[2 * i], false) | (fp8.encode(values[(2 * i) + 1], false) << 8);
    if (o[4] != (0x12340000U | low)) throw failed(4);
    const uint32_t high = bf8.encode(values[2 * i], false) | (bf8.encode(values[(2 * i) + 1], false) << 8);
    if (o[5] != (0x00005678U | (high << 16))) throw failed(5);
    if (o[6] != (0x12340078U | (fp8.stochastic(values[2 * i], seeds[i]) << 8))) throw failed(6);
    if (o[7] != (0x00345678U | (bf8.stochastic(values[(2 * i) + 1], seeds[i]) << 24))) throw failed(7);
  }
}

namespace {
using Bfloats2 = __bf16 __attribute__((ext_vector_type(2)));

// __builtin_amdgcn_fdot2_f32_bf16: v_dot2c_f32_bf16_e32 on gfx950, v_dot2_f32_bf16 on GFX11 and GFX12. Small integers
// keep every product and sum exact, which gfx1100 to gfx1103 and gfx1150 to gfx1153 lose to their fixed-point accumulator.
__global__ void bfloatDot([[maybe_unused]] const float *a, [[maybe_unused]] const float *b, [[maybe_unused]] float *out,
                          [[maybe_unused]] uint32_t *supported, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
#if defined(__gfx950__) || defined(__GFX11__) || defined(__GFX12__)
  const Bfloats2 x = {static_cast<__bf16>(a[static_cast<size_t>(i) * 2]), static_cast<__bf16>(a[(static_cast<size_t>(i) * 2) + 1])};
  const Bfloats2 y = {static_cast<__bf16>(b[static_cast<size_t>(i) * 2]), static_cast<__bf16>(b[(static_cast<size_t>(i) * 2) + 1])};
  out[i] = __builtin_amdgcn_fdot2_f32_bf16(x, y, out[i], false);
  if (i == 0) *supported = 1;
#endif
}

} // namespace

TEST_CASE("intrinsics/hip/bfloat16-dot", "[unified][intrinsics]") {
  constexpr uint32_t count = 64;
  std::vector<float> a(static_cast<size_t>(count) * 2);
  std::vector<float> b(static_cast<size_t>(count) * 2);
  std::vector<float> c(count);
  for (size_t i = 0; i < count; ++i) {
    a[2 * i] = static_cast<float>(static_cast<int>(i % 9) - 4);
    a[(2 * i) + 1] = static_cast<float>(i % 5) * 0.5F;
    b[2 * i] = static_cast<float>(static_cast<int>(i % 7) - 3) * 2.0F;
    b[(2 * i) + 1] = static_cast<float>(i % 3) + 0.25F;
    c[i] = static_cast<float>(i) * 0.125F;
  }
  DeviceBuffer<float> x(a);
  DeviceBuffer<float> y(b);
  DeviceBuffer<float> out(c);
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  bfloatDot<<<1, count>>>(x.data, y.data, out.data, supported.data, count);
  finish("bfloat16 dot");
  if (supported.download()[0] == 0) SKIP("bfloat16 dot products need gfx950, GFX11 or GFX12");
  const auto actual = out.download();
  const bool aligned = alignedDots(architecture());
  for (size_t i = 0; i < count; ++i) {
    const float expected = aligned
                               ? static_cast<float>(alignedDot(a[2 * i], b[2 * i], a[(2 * i) + 1], b[(2 * i) + 1], c[i], DotResult::Single))
                               : (a[2 * i] * b[2 * i]) + (a[(2 * i) + 1] * b[(2 * i) + 1]) + c[i];
    if (actual[i] != expected) throw std::runtime_error(fmt::format("bfloat16 dot {}: {} instead of {}", i, actual[i], expected));
  }
}

namespace {
// __builtin_amdgcn_permlane16_var and permlanex16_var: v_permlane16_var_b32 and v_permlanex16_var_b32 on GFX12, each
// lane's source lane in its own VGPR.
__global__ void variablePermutes(uint32_t *out, [[maybe_unused]] uint32_t *supported) {
  const uint32_t lane = threadIdx.x;
  uint32_t row = 0;
  uint32_t crossed = 0;
#ifdef __GFX12__
  const uint32_t selector = (lane * 7u + 3u) & 15u;
  row = __builtin_amdgcn_permlane16_var(0u, lane * 10u + 1u, selector, false, false);
  crossed = __builtin_amdgcn_permlanex16_var(0u, lane * 10u + 1u, selector, false, false);
  if (lane == 0) *supported = 1;
#endif
  uint32_t *o = out + (static_cast<size_t>(lane) * 2);
  o[0] = row;
  o[1] = crossed;
}

} // namespace

TEST_CASE("intrinsics/hip/variable-lane-permutes", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 32;
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(lanes) * 2));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  variablePermutes<<<1, lanes>>>(out.data, supported.data);
  finish("variable lane permutes");
  if (supported.download()[0] == 0) SKIP("variable lane permutes need GFX12");
  const auto actual = out.download();
  for (uint32_t lane = 0; lane < lanes; ++lane) {
    const uint32_t selector = ((lane * 7U) + 3U) & 15U;
    const uint32_t row = (lane & ~15U) | selector;
    const uint32_t crossed = ((lane & ~15U) ^ 16U) | selector;
    const size_t at = static_cast<size_t>(lane) * 2;
    if (actual[at] != (row * 10U) + 1U || actual[at + 1] != (crossed * 10U) + 1U)
      throw std::runtime_error(fmt::format("lane {}: {} and {}", lane, actual[at], actual[at + 1]));
  }
}

namespace {
// `(float)h * (float)h + f`, which hipcc fuses into v_fma_mix_f32 on gfx906, gfx90a, gfx942, gfx950 and RDNA; the
// product of two halves is exact in binary32, so one rounding gives the host's.
__global__ void mixedFma(const __half *a, const __half *b, const float *c, float *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i < count) out[i] = (__half2float(a[i]) * __half2float(b[i])) + c[i];
}

} // namespace

TEST_CASE("intrinsics/hip/mixed-precision-fma", "[unified][intrinsics]") {
  constexpr uint32_t count = 64;
  const auto a = iota(0U, count) | map([](uint32_t i) { return __float2half((static_cast<float>(i) * 0.37F) - 7.0F); }) | to_vector();
  const auto b = iota(0U, count) | map([](uint32_t i) { return __float2half(1.0F / (1.0F + static_cast<float>(i))); }) | to_vector();
  const auto c = iota(0U, count) | map([](uint32_t i) { return (static_cast<float>(i % 11) * 1.0e-3F) - 0.004F; }) | to_vector();
  DeviceBuffer<__half> x(a);
  DeviceBuffer<__half> y(b);
  DeviceBuffer<float> z(c);
  DeviceBuffer<float> out{std::vector<float>(count)};
  mixedFma<<<1, count>>>(x.data, y.data, z.data, out.data, count);
  finish("mixed-precision fma");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count; ++i) {
    const float expected = std::fma(__half2float(a[i]), __half2float(b[i]), c[i]);
    if (actual[i] != expected) throw std::runtime_error(fmt::format("mixed fma {}: {} instead of {}", i, actual[i], expected));
  }
}
