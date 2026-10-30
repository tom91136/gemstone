#include <algorithm>
#include <cmath>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

#include "cuda/support.cuh"
#include <catch2/catch_test_macros.hpp>
#include <cuda_fp16.h>
#include <cuda_runtime_api.h>

#include "test_support.hpp"

// Packed-half comparisons, selects and arithmetic with immediate, swizzled and negated sources (HSETP2, HSET2, HADD2,
// HMUL2, HFMA2, HMNMX2), half rounding and widening (FRND.F16, HADD2.F32), directed binary32 arithmetic (FADD.RZ,
// FMUL.RP, FFMA.RM), conversions between binary64, binary32 and binary16 (F2F, F2FP) and binary32 extrema (FMNMX) as
// ptxas emits them for the cuda_fp16.h intrinsics and the CUDA math functions.
namespace {

std::string hex(uint64_t value) {
  char text[24];
  std::snprintf(text, sizeof(text), "%#llx", static_cast<unsigned long long>(value));
  return text;
}

uint16_t bitsOf(__half value) { return __half_raw(value).x; }

__half halfOf(uint16_t bits) {
  __half_raw raw{};
  raw.x = bits;
  return raw;
}

using unified::support::bitsFloat;
using unified::support::floatBits;

double halfValue(uint32_t pair, int shift) { return static_cast<double>(__half2float(halfOf(static_cast<uint16_t>(pair >> shift)))); }

// binary16 halves rounded to nearest even from an exact double; NaN is the canonical 0x7fff
uint16_t toHalf(double value) {
  if (std::isnan(value)) return 0x7fffU;
  const float single = static_cast<float>(value);
  // a double that is not exactly a float would round twice through float
  if (static_cast<double>(single) != value) {
    const double magnitude = std::fabs(value);
    const int exponent = std::max(std::ilogb(magnitude), -14);
    const double quantum = std::ldexp(1.0, exponent - 10);
    const double rounded = std::nearbyint(value / quantum) * quantum;
    return bitsOf(__float2half_rn(static_cast<float>(rounded)));
  }
  return bitsOf(__float2half_rn(single));
}

uint32_t pair(uint16_t high, uint16_t low) { return static_cast<uint32_t>(high) << 16 | low; }

const std::vector<uint32_t> HalfInputs = {0x3c004000U, 0x40003c00U, 0x7e00bc00U, 0x80000000U, 0x7c00fc00U, 0x3e004080U,
                                          0xc2003800U, 0x00010400U, 0x7bfffbffU, 0x3555b555U, 0x4900c900U, 0x1419e3d0U,
                                          0xfc007e00U, 0x00008000U, 0x3c003c00U, 0x4248c248U};

constexpr uint32_t HalfCount = 12;

__global__ void halves(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
#if !defined(__CUDA_ARCH__) || __CUDA_ARCH__ >= 530
  __half2_raw rawX;
  __half2_raw rawY;
  const uint32_t a = in[i / count];
  const uint32_t b = in[i % count];
  std::memcpy(&rawX, &a, sizeof(a));
  std::memcpy(&rawY, &b, sizeof(b));
  const __half2 x = rawX;
  const __half2 y = rawY;
  uint32_t *o = out + (static_cast<size_t>(i) * HalfCount);
  auto bits = [](__half2_raw value) {
    uint32_t result = 0;
    std::memcpy(&result, &value, sizeof(result));
    return result;
  };
  o[0] = __hblt2(x, y) | __hbgeu2(x, y) << 1 | __hbeq2(x, y) << 2 | __hbne2(x, y) << 3;
  o[1] = __hlt(__low2half(x), __high2half(y)) | __hgeu(__high2half(x), __float2half(1.5F)) << 1 | __hisnan(__low2half(x)) << 2
         | __hne(__habs(__low2half(x)), __low2half(y)) << 3;
  o[2] = bits(__hle2(x, y));
  o[3] = bits(__hgtu2(x, y));
  o[4] = bits(__hadd2(x, __floats2half2_rn(1.3F, -2.7F)));
  o[5] = bits(__hmul2(__lowhigh2highlow(x), __low2half2(y)));
  o[6] = bits(__hfma2(x, __floats2half2_rn(2.1F, 7.5F), y));
  o[7] = bits(__hsub2(__high2half2(x), __hneg2(y)));
  o[8] = bits(__hadd2_sat(x, y));
  o[9] = bits(__hfma2_sat(x, y, __floats2half2_rn(0.25F, -0.25F)));
  o[10] =
      static_cast<uint32_t>(__half_as_ushort(hrint(__low2half(x)))) | static_cast<uint32_t>(__half_as_ushort(htrunc(__high2half(x)))) << 16;
  o[11] = __float_as_uint(__high2float(x) + __low2float(y));
#else
  __trap();
#endif
}

uint16_t compareBits(bool holds) { return holds ? 0x3c00U : 0U; }

std::vector<uint32_t> halvesReference(uint32_t a, uint32_t b) {
  const double xl = halfValue(a, 0);
  const double xh = halfValue(a, 16);
  const double yl = halfValue(b, 0);
  const double yh = halfValue(b, 16);
  auto unordered = [](double p, double q) { return std::isnan(p) || std::isnan(q); };
  const bool lt = xl < yl && xh < yh;
  const bool geu = (unordered(xl, yl) || xl >= yl) && (unordered(xh, yh) || xh >= yh);
  const bool eq = xl == yl && xh == yh;
  const bool ne = xl != yl && xh != yh && !unordered(xl, yl) && !unordered(xh, yh);
  const double lowAbs = std::fabs(xl);
  auto saturate = [](double value) { return std::isnan(value) || value <= 0.0 ? 0.0 : std::min(value, 1.0); };
  auto add = [](double p, double q) { return toHalf(p + q); };
  auto mul = [](double p, double q) { return toHalf(p * q); };
  // the fused products of these inputs are exact in binary64
  auto fma = [](double p, double q, double r) { return toHalf((p * q) + r); };
  const double c13 = __half2float(__float2half_rn(1.3F));
  const double c27 = __half2float(__float2half_rn(-2.7F));
  const double c21 = __half2float(__float2half_rn(2.1F));
  const double c75 = __half2float(__float2half_rn(7.5F));
  auto rint = [](double value) { return toHalf(std::nearbyint(value)); };
  auto trunc = [](double value) { return toHalf(std::trunc(value)); };
  return {static_cast<uint32_t>(lt) | static_cast<uint32_t>(geu) << 1 | static_cast<uint32_t>(eq) << 2 | static_cast<uint32_t>(ne) << 3,
          static_cast<uint32_t>(xl < yh) | static_cast<uint32_t>(std::isnan(xh) || xh >= 1.5) << 1
              | static_cast<uint32_t>(std::isnan(xl)) << 2 | static_cast<uint32_t>(lowAbs != yl && !unordered(lowAbs, yl)) << 3,
          pair(compareBits(xh <= yh), compareBits(xl <= yl)),
          pair(compareBits(unordered(xh, yh) || xh > yh), compareBits(unordered(xl, yl) || xl > yl)),
          pair(add(xh, c27), add(xl, c13)),
          pair(mul(xl, yl), mul(xh, yl)),
          pair(fma(xh, c75, yh), fma(xl, c21, yl)),
          pair(add(xh, yh), add(xh, yl)),
          pair(toHalf(saturate(halfValue(add(xh, yh), 0))), toHalf(saturate(halfValue(add(xl, yl), 0)))),
          pair(toHalf(saturate(halfValue(fma(xh, yh, -0.25), 0))), toHalf(saturate(halfValue(fma(xl, yl, 0.25), 0)))),
          pair(trunc(xh), rint(xl)),
          floatBits(static_cast<float>(xh) + static_cast<float>(yl))};
}

} // namespace

TEST_CASE("floating/cuda/halves", "[unified][floating]") {
  int device = 0;
  int major = 0;
  int minor = 0;
  check(cudaGetDevice(&device), "device");
  check(cudaDeviceGetAttribute(&major, cudaDevAttrComputeCapabilityMajor, device), "compute capability");
  check(cudaDeviceGetAttribute(&minor, cudaDevAttrComputeCapabilityMinor, device), "compute capability");
  if ((major * 10) + minor < 53) SKIP("packed half arithmetic requires compute capability 5.3");
  const uint32_t count = static_cast<uint32_t>(HalfInputs.size());
  DeviceBuffer<uint32_t> in(HalfInputs);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * HalfCount));
  halves<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count);
  finish("halves");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count * count; ++i) {
    const auto expected = halvesReference(HalfInputs[i / count], HalfInputs[i % count]);
    for (size_t field = 0; field < expected.size(); ++field) {
      const uint32_t got = actual[(static_cast<size_t>(i) * HalfCount) + field];
      // any NaN half matches any NaN half
      bool same = got == expected[field];
      if (!same && field >= 4 && field <= 10) {
        same = true;
        for (int shift = 0; shift < 32; shift += 16) {
          const uint16_t p = static_cast<uint16_t>(got >> shift);
          const uint16_t q = static_cast<uint16_t>(expected[field] >> shift);
          same = same && (p == q || ((p & 0x7fffU) > 0x7c00U && (q & 0x7fffU) > 0x7c00U));
        }
      }
      if (!same && field == 11) same = std::isnan(bitsFloat(got)) && std::isnan(bitsFloat(expected[field]));
      if (!same)
        throw std::runtime_error("halves " + std::to_string(field) + " of " + hex(HalfInputs[i / count]) + " and "
                                 + hex(HalfInputs[i % count]) + ": expected " + hex(expected[field]) + ", got " + hex(got));
    }
  }
}

namespace {
const std::vector<uint32_t> SingleInputs = {0x00000000U, 0x80000000U, 0x3f800000U, 0xbf800000U, 0x33800001U, 0xb3800001U,
                                            0x00000001U, 0x807fffffU, 0x7f7fffffU, 0x7f800000U, 0xff800000U, 0x7fc00000U,
                                            0x40490fdbU, 0xc0490fdbU, 0x3eaaaaabU, 0x4b800001U};

constexpr uint32_t SingleCount = 10;

__global__ void singles(const uint32_t *in, uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count * count) return;
  const float x = __uint_as_float(in[i / count]);
  const float y = __uint_as_float(in[i % count]);
  uint32_t *o = out + (static_cast<size_t>(i) * SingleCount);
  o[0] = __float_as_uint(__fadd_rz(x, y));
  o[1] = __float_as_uint(__fadd_rd(x, y));
  o[2] = __float_as_uint(__fmul_ru(x, y));
  o[3] = __float_as_uint(__fmaf_rd(x, y, 1.0F));
  o[4] = __float_as_uint(fmaxf(x, y));
  o[5] = __float_as_uint(fminf(fminf(x, y), 0.5F));
  const double wide = static_cast<double>(x) * static_cast<double>(y);
  o[6] = __float_as_uint(__double2float_rz(wide));
  o[7] = __float_as_uint(__double2float_ru(wide));
  o[8] = static_cast<uint32_t>(__half_as_ushort(__float2half_rz(x))) | static_cast<uint32_t>(__half_as_ushort(__float2half_rd(y))) << 16;
  o[9] = __float_as_uint(rintf(x) + truncf(y));
}

// The float that `sum + error`, held exactly as a binary64 value and a smaller correction, rounds to toward zero
// (mode 0), down (1) or up (2).
uint32_t directed(double sum, double error, int mode) {
  if (std::isnan(sum)) return 0x7fffffffU;
  if (sum == 0.0 && error == 0.0) return floatBits(static_cast<float>(sum));
  const float nearest = static_cast<float>(sum);
  const double residual = (sum - static_cast<double>(nearest)) + error;
  if (residual == 0.0 || std::isinf(sum)) return floatBits(nearest);
  const float below = residual > 0.0 ? nearest : std::nextafter(nearest, -INFINITY);
  const float above = residual < 0.0 ? nearest : std::nextafter(nearest, INFINITY);
  switch (mode) {
    case 0: return floatBits(std::fabs(below) < std::fabs(above) ? below : above);
    case 1: return floatBits(below);
    default: return floatBits(above);
  }
}

// The exact sum of two binary64 values as a rounded sum and its error.
std::pair<double, double> twoSum(double p, double q, int mode) {
  // zeros of one sign keep it; an exact cancellation is -0 rounding down and +0 otherwise
  if (p == 0.0 && q == 0.0 && std::signbit(p) == std::signbit(q)) return {p, 0.0};
  if (p == -q && std::isfinite(p)) return {mode == 1 ? -0.0 : 0.0, 0.0};
  const double sum = p + q;
  if (!std::isfinite(sum)) return {sum, 0.0};
  const double virtualQ = sum - p;
  const double virtualP = sum - virtualQ;
  return {sum, (p - virtualP) + (q - virtualQ)};
}

uint16_t halfDirected(double value, int mode) {
  if (std::isnan(value)) return 0x7fffU;
  const uint16_t nearest = toHalf(value);
  const double back = halfValue(nearest, 0);
  if (back == value) return nearest;
  const bool belowValue = back < value;
  // the neighbour on the other side of the value
  auto step = [](uint16_t bits, bool up) -> uint16_t {
    const bool negative = bits & 0x8000U;
    if ((bits & 0x7fffU) == 0) return up ? 0x0001U : 0x8001U;
    return (up != negative) ? bits + 1 : bits - 1;
  };
  const uint16_t other = step(nearest, belowValue);
  const uint16_t below = belowValue ? nearest : other;
  const uint16_t above = belowValue ? other : nearest;
  if (mode == 0) return std::fabs(halfValue(below, 0)) < std::fabs(halfValue(above, 0)) ? below : above;
  return mode == 1 ? below : above;
}

uint32_t fminNumber(float p, float q) {
  if (std::isnan(p) && std::isnan(q)) return 0x7fffffffU;
  if (std::isnan(p)) return floatBits(q);
  if (std::isnan(q)) return floatBits(p);
  if (p == 0.0F && q == 0.0F) return floatBits(std::signbit(p) ? p : q);
  return floatBits(p < q ? p : q);
}

uint32_t fmaxNumber(float p, float q) {
  if (std::isnan(p) && std::isnan(q)) return 0x7fffffffU;
  if (std::isnan(p)) return floatBits(q);
  if (std::isnan(q)) return floatBits(p);
  if (p == 0.0F && q == 0.0F) return floatBits(std::signbit(p) ? q : p);
  return floatBits(p > q ? p : q);
}

std::vector<uint32_t> singlesReference(uint32_t a, uint32_t b) {
  const float x = bitsFloat(a);
  const float y = bitsFloat(b);
  // binary32 products are exact in binary64, and two-sum keeps the rest of each sum
  const auto [truncated, truncatedError] = twoSum(static_cast<double>(x), static_cast<double>(y), 0);
  const auto [lowered, loweredError] = twoSum(static_cast<double>(x), static_cast<double>(y), 1);
  const double product = static_cast<double>(x) * static_cast<double>(y);
  const auto [fused, fusedError] = twoSum(product, 1.0, 1);
  return {directed(truncated, truncatedError, 0),
          directed(lowered, loweredError, 1),
          directed(product, 0.0, 2),
          directed(fused, fusedError, 1),
          fmaxNumber(x, y),
          fminNumber(bitsFloat(fminNumber(x, y)), 0.5F),
          directed(product, 0.0, 0),
          directed(product, 0.0, 2),
          static_cast<uint32_t>(halfDirected(x, 0)) | static_cast<uint32_t>(halfDirected(y, 1)) << 16,
          floatBits(std::nearbyint(x) + std::trunc(y))};
}

} // namespace

TEST_CASE("floating/cuda/singles", "[unified][floating]") {
  const uint32_t count = static_cast<uint32_t>(SingleInputs.size());
  DeviceBuffer<uint32_t> in(SingleInputs);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * SingleCount));
  singles<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, count);
  finish("singles");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count * count; ++i) {
    const uint32_t a = SingleInputs[i / count];
    const uint32_t b = SingleInputs[i % count];
    const auto expected = singlesReference(a, b);
    for (size_t field = 0; field < expected.size(); ++field) {
      const uint32_t got = actual[(static_cast<size_t>(i) * SingleCount) + field];
      const bool same = got == expected[field] || (field != 8 && std::isnan(bitsFloat(got)) && std::isnan(bitsFloat(expected[field])));
      if (!same)
        throw std::runtime_error("singles " + std::to_string(field) + " of " + hex(a) + " and " + hex(b) + ": expected "
                                 + hex(expected[field]) + ", got " + hex(got));
    }
  }
}
