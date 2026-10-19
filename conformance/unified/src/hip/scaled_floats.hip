#include <array>
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
#include <hip/hip_runtime.h>

#include "support.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;
using namespace hip_support;

// gfx950's narrow-float and lane instructions hipcc emits from builtins, each checked against a host reference: the
// V_CVT_SCALEF32 conversions between binary32 or binary16 and the OCP 8-, 6- and 4-bit formats, scaled by an E8M0
// exponent; V_PRNG_B32, V_ASHR_PK_I8_I32 and V_CVT_SR_BF16_F32; and V_PERMLANE16_SWAP and V_PERMLANE32_SWAP. The
// narrowed values lie on the target format's grid once scaled, apart from the saturating and overflowing ones, so no
// result depends on rounding or the stochastic seed.
namespace {

using unified::support::floatBits;

// An OCP small float: E4M3 has NaN alone at S.1111.111, E5M2 follows IEEE-754, and E2M3, E3M2 and E2M1 are all finite.
struct Format {
  int exponent;
  int fraction;
  bool infinite;
  bool nanOnly;

  [[nodiscard]] int width() const { return 1 + exponent + fraction; }
  [[nodiscard]] int bias() const { return (1 << (exponent - 1)) - 1; }

  [[nodiscard]] double decode(uint32_t code) const {
    code &= (1U << width()) - 1U;
    const uint32_t sign = 1U << (width() - 1);
    const int field = static_cast<int>((code & ~sign) >> fraction);
    const int mantissa = static_cast<int>(code) & ((1 << fraction) - 1);
    if (nanOnly && (code & ~sign) == (sign - 1U)) return std::numeric_limits<double>::quiet_NaN();
    if (infinite && field == (1 << exponent) - 1) {
      if (mantissa != 0) return std::numeric_limits<double>::quiet_NaN();
      return (code & sign) != 0 ? -std::numeric_limits<double>::infinity() : std::numeric_limits<double>::infinity();
    }
    const double magnitude = field == 0 ? std::ldexp(static_cast<double>(mantissa), 1 - bias() - fraction)
                                        : std::ldexp(static_cast<double>((1 << fraction) | mantissa), field - bias() - fraction);
    return (code & sign) != 0 ? -magnitude : magnitude;
  }

  // The code of a value on the grid.
  [[nodiscard]] uint32_t encode(double value) const {
    if (const auto code = iota(0U, 1U << width()) | find([&](uint32_t candidate) {
                            return decode(candidate) == value && std::signbit(decode(candidate)) == std::signbit(value);
                          }))
      return *code;
    throw std::runtime_error(fmt::format("{} is not on the grid", value));
  }
};

const Format E4M3{.exponent = 4, .fraction = 3, .infinite = false, .nanOnly = true},
    E5M2{.exponent = 5, .fraction = 2, .infinite = true, .nanOnly = false},
    E2M3{.exponent = 2, .fraction = 3, .infinite = false, .nanOnly = false},
    E2M1{.exponent = 2, .fraction = 1, .infinite = false, .nanOnly = false};

constexpr uint32_t Results = 16;

using Floats2 = float __attribute__((ext_vector_type(2)));
using Shorts2 = short __attribute__((ext_vector_type(2)));
using Halves2 = _Float16 __attribute__((ext_vector_type(2)));
using Bfloats2 = __bf16 __attribute__((ext_vector_type(2)));

// Each lane converts its codes and values by its power-of-two scale.
__global__ void scaledConversions(const uint32_t *codes, const float *values, const float *scales, uint32_t *out, uint32_t *supported,
                                  uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
#ifdef __gfx950__
  uint32_t *o = out + (static_cast<size_t>(i) * Results);
  const uint32_t code = codes[i];
  const float scale = scales[i];
  o[0] = __float_as_uint(__builtin_amdgcn_cvt_scalef32_f32_fp8(static_cast<int>(code), scale, 1));
  o[1] = __float_as_uint(__builtin_amdgcn_cvt_scalef32_f32_bf8(static_cast<int>(code), scale, 2));
  const Floats2 nibbles = __builtin_amdgcn_cvt_scalef32_pk_f32_fp4(code, scale, 3);
  o[2] = __float_as_uint(nibbles.x);
  o[3] = __float_as_uint(nibbles.y);
  const Shorts2 packed = __builtin_amdgcn_cvt_scalef32_pk_fp8_f32((Shorts2){0x1234, 0x5678}, values[static_cast<size_t>(i) * 4],
                                                                  values[(static_cast<size_t>(i) * 4) + 1], scale, false);
  o[4] = static_cast<uint16_t>(packed.x) | (static_cast<uint32_t>(static_cast<uint16_t>(packed.y)) << 16);
  o[5] = __builtin_amdgcn_cvt_scalef32_pk_fp4_f32(0x12345678U, values[(static_cast<size_t>(i) * 4) + 2],
                                                  values[(static_cast<size_t>(i) * 4) + 3], scale, 1);
  const Halves2 widened = __builtin_amdgcn_cvt_scalef32_pk_f16_fp8(code, scale, false);
  o[6] = __builtin_bit_cast(uint32_t, widened);
  o[7] = static_cast<uint32_t>(
      __builtin_amdgcn_cvt_scalef32_sr_fp8_f32(0x12345678, values[static_cast<size_t>(i) * 4], code * 2654435761U, scale, 2));
  o[8] = __builtin_amdgcn_prng_b32(code);
  o[9] = __builtin_amdgcn_ashr_pk_i8_i32(code, code * 77U, i % 32U);
  const Bfloats2 old = __builtin_bit_cast(Bfloats2, 0x56781234U);
  o[10] = __builtin_bit_cast(uint32_t, __builtin_amdgcn_cvt_sr_bf16_f32(old, values[static_cast<size_t>(i) * 4], code, true));
  if (i == 0) *supported = 1;
#endif
}

uint32_t halfBits(double value) { return static_cast<uint32_t>(__builtin_bit_cast(uint16_t, static_cast<_Float16>(value))); }

} // namespace

TEST_CASE("intrinsics/hip/scaled-narrow-floats", "[unified][intrinsics]") {
  constexpr uint32_t count = 64;
  // E4M3 codes in byte 1, E5M2 codes in byte 2 and two E2M1 codes in byte 3, none NaN or infinite
  const auto codes = iota(0U, count) | map([](uint32_t i) {
                       const uint32_t fp8 = ((i * 29U) + 3U) % 0x7eU;
                       const uint32_t bf8 = (((i * 13U) + 5U) % 0x7bU) | ((i & 2U) << 6);
                       return (i * 7U) | (fp8 << 8) | (bf8 << 16) | (((i * 3U) & 0xffU) << 24);
                     })
                     | to_vector();
  const auto scales = iota(0U, count) | map([](uint32_t i) { return std::ldexp(1.0F, static_cast<int>(i % 5) - 2); }) | to_vector();
  const auto values =
      iota(0U, count) | flat_map([&](uint32_t i) {
        return std::array<float, 4>{static_cast<float>(E4M3.decode(((i * 11U) + 9U) % 0x7eU) * scales[i]),
                                    i % 8 == 0 ? 1000.0F * scales[i]
                                               : static_cast<float>(E4M3.decode((((i * 5U) + 1U) % 0x7eU) | 0x80U) * scales[i]),
                                    static_cast<float>(E2M1.decode(i % 16U) * scales[i]),
                                    i % 8 == 3 ? 100.0F * scales[i] : static_cast<float>(E2M1.decode((i * 3U) % 16U) * scales[i])};
      })
      | to_vector();
  DeviceBuffer<uint32_t> codeBuffer(codes);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * Results));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  DeviceBuffer<float> valueBuffer(values);
  DeviceBuffer<float> scaleBuffer(scales);
  scaledConversions<<<1, count>>>(codeBuffer.data, valueBuffer.data, scaleBuffer.data, out.data, supported.data, count);
  finish("scaled conversions");
  if (supported.download()[0] == 0) SKIP("scaled conversions need gfx950");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count; ++i) {
    const uint32_t *o = actual.data() + (static_cast<size_t>(i) * Results);
    const float *v = values.data() + (static_cast<size_t>(i) * 4);
    const uint32_t code = codes[i];
    const double scale = scales[i];
    const auto narrow8 = [&](float value) -> uint32_t {
      const double scaled = value / scale;
      if (std::fabs(scaled) > 448.0) return std::signbit(scaled) ? 0xffU : 0x7fU;
      return E4M3.encode(scaled);
    };
    const auto narrow4 = [&](float value) -> uint32_t {
      const double scaled = value / scale;
      if (std::fabs(scaled) > 6.0) return std::signbit(scaled) ? 0xfU : 0x7U;
      return E2M1.encode(scaled);
    };
    const auto saturated = [&](int value) { return static_cast<uint32_t>(std::max(-128, std::min(127, value)) & 0xff); };
    const uint32_t shift = i % 32U;
    const uint32_t expected[11] = {floatBits(static_cast<float>(E4M3.decode(code >> 8) * scale)),
                                   floatBits(static_cast<float>(E5M2.decode(code >> 16) * scale)),
                                   floatBits(static_cast<float>(E2M1.decode(code >> 24) * scale)),
                                   floatBits(static_cast<float>(E2M1.decode(code >> 28) * scale)),
                                   0x56780000U | narrow8(v[0]) | (narrow8(v[1]) << 8),
                                   (0x12345678U & ~0xff00U) | ((narrow4(v[2]) | (narrow4(v[3]) << 4)) << 8),
                                   halfBits(E4M3.decode(code) * scale) | (halfBits(E4M3.decode(code >> 8) * scale) << 16),
                                   (0x12345678U & ~0xff0000U) | (narrow8(v[0]) << 16),
                                   ((code << 1) ^ ((code >> 31) != 0 ? 197U : 0U)),
                                   saturated(static_cast<int32_t>(code) >> shift)
                                       | (saturated(static_cast<int32_t>(code * 77U) >> shift) << 8),
                                   0x1234U | (floatBits(v[0]) & 0xffff0000U)};
    for (uint32_t result = 0; result < 11; ++result)
      if (o[result] != expected[result])
        throw std::runtime_error(fmt::format("scaled result {} of lane {} (code {:#x}, scale {}): {:#x} instead of {:#x}", result, i, code,
                                             scale, o[result], expected[result]));
  }
}

namespace {
using Floats16 = float __attribute__((ext_vector_type(16)));
using Floats32 = float __attribute__((ext_vector_type(32)));
using Uints6 = unsigned __attribute__((ext_vector_type(6)));

// __builtin_amdgcn_cvt_scalef32_2xpk16_fp6_f32 packs SRC0's and SRC1's elements in turn into six dwords, and
// cvt_scalef32_pk32_f32_fp6 widens them back.
__global__ void sixBitBlocks(const float *values, const float *scales, uint32_t *packed, float *widened, uint32_t *supported) {
  const uint32_t i = threadIdx.x;
#ifdef __gfx950__
  Floats16 a;
  Floats16 b;
  for (int e = 0; e < 16; ++e) {
    a[e] = values[(i * 32) + e];
    b[e] = values[(i * 32) + 16 + e];
  }
  const Uints6 codes = __builtin_amdgcn_cvt_scalef32_2xpk16_fp6_f32(a, b, scales[i]);
  for (int word = 0; word < 6; ++word)
    packed[(i * 6) + word] = codes[word];
  const Floats32 back = __builtin_amdgcn_cvt_scalef32_pk32_f32_fp6(codes, scales[i]);
  for (int e = 0; e < 32; ++e)
    widened[(i * 32) + e] = back[e];
  if (i == 0) *supported = 1;
#endif
}

} // namespace

TEST_CASE("intrinsics/hip/scaled-six-bit-blocks", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 64;
  const auto scales = iota(0U, lanes) | map([](uint32_t i) { return std::ldexp(1.0F, static_cast<int>(i % 7) - 3); }) | to_vector();
  const auto values = iota(0U, lanes) | flat_map([&](uint32_t i) {
                        return iota(0U, 32U)
                               | map([&](uint32_t e) { return static_cast<float>(E2M3.decode(((i * 17U) + (e * 5U)) % 64U) * scales[i]); })
                               | to_vector();
                      })
                      | to_vector();
  DeviceBuffer<float> valueBuffer(values);
  DeviceBuffer<float> scaleBuffer(scales);
  DeviceBuffer<float> widened(std::vector<float>(static_cast<size_t>(lanes) * 32));
  DeviceBuffer<uint32_t> packed(std::vector<uint32_t>(static_cast<size_t>(lanes) * 6));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  sixBitBlocks<<<1, lanes>>>(valueBuffer.data, scaleBuffer.data, packed.data, widened.data, supported.data);
  finish("scaled six-bit blocks");
  if (supported.download()[0] == 0) SKIP("scaled six-bit conversions need gfx950");
  const auto codes = packed.download();
  const auto back = widened.download();
  for (uint32_t i = 0; i < lanes; ++i) {
    for (uint32_t p = 0; p < 32; ++p) {
      // pass p's elements: SRC0's element p/2 at bit 12 * (p/2), SRC1's six bits above
      const uint32_t element = ((p & 1U) * 16U) + (p / 2U);
      const uint32_t at = 6U * p;
      const uint64_t window =
          codes[(i * 6) + (at / 32)] | ((at / 32) + 1 < 6 ? static_cast<uint64_t>(codes[(i * 6) + (at / 32) + 1]) << 32 : 0);
      const uint32_t code = static_cast<uint32_t>(window >> (at % 32)) & 63U;
      const uint32_t expected = E2M3.encode(values[(i * 32) + element] / scales[i]);
      if (code != expected)
        throw std::runtime_error(fmt::format("six-bit code {} of lane {}: {:#x} instead of {:#x}", p, i, code, expected));
      if (back[(i * 32) + p] != values[(i * 32) + element])
        throw std::runtime_error(
            fmt::format("six-bit element {} of lane {}: {} instead of {}", p, i, back[(i * 32) + p], values[(i * 32) + element]));
    }
  }
}

namespace {
using Uints2 = unsigned __attribute__((ext_vector_type(2)));

// __builtin_amdgcn_permlane16_swap and permlane32_swap: v_permlane16_swap_b32 and v_permlane32_swap_b32, the first
// operand's lanes below each 16- or 32-lane boundary trading places with the second's above it.
__global__ void laneSwaps(uint32_t *out, uint32_t *supported) {
  const uint32_t lane = threadIdx.x;
#ifdef __gfx950__
  const Uints2 rows = __builtin_amdgcn_permlane16_swap(1000U + lane, 2000U + lane, false, false);
  const Uints2 halves = __builtin_amdgcn_permlane32_swap(1000U + lane, 2000U + lane, false, false);
  out[static_cast<size_t>(lane) * 4] = rows.x;
  out[(static_cast<size_t>(lane) * 4) + 1] = rows.y;
  out[(static_cast<size_t>(lane) * 4) + 2] = halves.x;
  out[(static_cast<size_t>(lane) * 4) + 3] = halves.y;
  if (lane == 0) *supported = 1;
#endif
}

} // namespace

TEST_CASE("intrinsics/hip/lane-swaps", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 64;
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(lanes) * 4));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  laneSwaps<<<1, lanes>>>(out.data, supported.data);
  finish("lane swaps");
  if (supported.download()[0] == 0) SKIP("lane swaps need gfx950");
  const auto actual = out.download();
  for (uint32_t lane = 0; lane < lanes; ++lane) {
    // the builtin returns the new first operand, then the new second
    const uint32_t expected[4] = {(lane & 16U) == 0 ? 1000U + lane : 2000U + lane - 16U,
                                  (lane & 16U) == 0 ? 1000U + lane + 16U : 2000U + lane, lane < 32 ? 1000U + lane : 2000U + lane - 32U,
                                  lane < 32 ? 1000U + lane + 32U : 2000U + lane};
    for (uint32_t result = 0; result < 4; ++result)
      if (actual[(lane * 4) + result] != expected[result])
        throw std::runtime_error(
            fmt::format("lane swap {} of lane {}: {} instead of {}", result, lane, actual[(lane * 4) + result], expected[result]));
  }
}
