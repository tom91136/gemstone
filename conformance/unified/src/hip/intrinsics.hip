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

// Device intrinsics and source patterns whose AMDGPU instructions hipcc emits directly, each checked against a host
// reference: media builtins, saturating arithmetic, half division, lane permutation, dynamic vector indexing,
// sub-dword arithmetic, LDS append counters, packed atomics, uniform float arithmetic and 64-bit DPP.
namespace {

// Address-space conversions have no named-cast spelling.
template <int Space, class T> __device__ inline __attribute__((address_space(Space))) T *inAddressSpace(T *pointer) {
  using Pointer = __attribute__((address_space(Space))) T *;
  return (Pointer)pointer; // NOLINT(google-readability-casting,cppcoreguidelines-pro-type-cstyle-cast,modernize-avoid-c-style-cast)
}

template <typename T> void expectEqual(const std::vector<T> &actual, const std::vector<T> &expected, std::string_view name) {
  if (actual.size() != expected.size())
    throw std::runtime_error(fmt::format("{}: {} results, expected {}", name, actual.size(), expected.size()));
  if (const auto at = expected ^ zip(actual) ^ index_where_maybe([](const T &want, const T &got) { return got != want; }))
    throw std::runtime_error(fmt::format("{} mismatch at {}: expected {:#x}, got {:#x}", name, *at, static_cast<uint64_t>(expected[*at]),
                                         static_cast<uint64_t>(actual[*at])));
}

using unified::support::floatBits;

uint16_t halfBits(float value) { return __half_as_ushort(__float2half_rn(value)); }

int waveLanes() {
  int device = 0;
  check(hipGetDevice(&device), "device");
  hipDeviceProp_t properties{};
  check(hipGetDeviceProperties(&properties, device), "device properties");
  return properties.warpSize;
}

const std::vector<uint32_t> Words = {0x00000000U, 0x01020304U, 0x04030201U, 0x7f80ff01U, 0x80017f00U, 0xffff0001U,
                                     0x00020003U, 0xfffffffbU, 0x12345678U, 0xdeadbeefU, 0x7fffffffU, 0x80000000U,
                                     0x0000ffffU, 0x55aa55aaU, 0xa5a5a5a5U, 0x00ff00ffU};

// Each triple a cube direction; the faces, signs and the tie between equal major axes all appear.
const std::vector<float> Directions = {1.0F,  -3.0F, 2.0F,  -4.0F, 1.0F,  2.0F,  0.5F,   0.25F, -3.0F, 2.0F,  2.0F,  1.0F,
                                       -1.0F, -1.0F, -1.0F, 0.7F,  -0.2F, 0.1F,  -0.3F,  0.9F,  0.45F, 3.0F,  -2.0F, 3.0F,
                                       0.0F,  0.0F,  1.0F,  -0.6F, 0.6F,  -0.5F, 1.25F,  -1.5F, 0.75F, -2.0F, -0.5F, 0.25F,
                                       0.1F,  0.2F,  0.3F,  6.0F,  -7.0F, 0.5F,  -0.25F, 0.0F,  0.0F,  0.8F,  -0.8F, 0.4F};

// Rounding cases for the byte conversion: ties to even, saturation and NaN-free extremes.
const std::vector<float> Bytes = {0.0F,  1.5F,  2.5F,   3.5F,   200.6F, 254.5F, 255.5F, 300.0F,
                                  -1.0F, 0.49F, 127.5F, 128.5F, 1.0e9F, -0.0F,  7.7F,   99.99F};

constexpr uint32_t MediaResults = 21;

// __builtin_amdgcn_lerp, sad_u8, msad_u8, qsad_pk_u16_u8, mqsad_pk_u16_u8, mqsad_u32_u8, the cube builtins,
// cvt_pknorm_i16/u16, cvt_pk_i16/u16, cvt_pk_u8_f32 and cvt_off_f32_i4, one VOP3 instruction each.
__global__ void media(const uint32_t *a, const uint32_t *b, const uint32_t *c, const uint64_t *w, const float *f, const float *byteValues,
                      uint32_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
  using Quad = unsigned int __attribute__((ext_vector_type(4)));
  uint32_t *o = out + (static_cast<size_t>(i) * MediaResults);
  const uint64_t other = w[(i + 1) % count];
  o[0] = __builtin_amdgcn_lerp(a[i], b[i], c[i]);
  o[1] = __builtin_amdgcn_sad_u8(a[i], b[i], c[i]);
  o[2] = __builtin_amdgcn_msad_u8(a[i], b[i], c[i]);
  const uint64_t qsad = __builtin_amdgcn_qsad_pk_u16_u8(w[i], b[i], other);
  const uint64_t mqsad = __builtin_amdgcn_mqsad_pk_u16_u8(w[i], b[i], other);
  o[3] = static_cast<uint32_t>(qsad);
  o[4] = static_cast<uint32_t>(qsad >> 32);
  o[5] = static_cast<uint32_t>(mqsad);
  o[6] = static_cast<uint32_t>(mqsad >> 32);
  const Quad accumulators = {c[i], a[i], b[i], c[i] ^ a[i]};
  const Quad sums = __builtin_amdgcn_mqsad_u32_u8(w[i], b[i], accumulators);
  o[7] = sums.x;
  o[8] = sums.y;
  o[9] = sums.z;
  o[10] = sums.w;
  const size_t at = static_cast<size_t>(i) * 3;
  const float x = f[at];
  const float y = f[at + 1];
  const float z = f[at + 2];
  o[11] = __float_as_uint(__builtin_amdgcn_cubeid(x, y, z));
  o[12] = __float_as_uint(__builtin_amdgcn_cubesc(x, y, z));
  o[13] = __float_as_uint(__builtin_amdgcn_cubetc(x, y, z));
  o[14] = __float_as_uint(__builtin_amdgcn_cubema(x, y, z));
  o[15] = __builtin_bit_cast(uint32_t, __builtin_amdgcn_cvt_pknorm_i16(x, y));
  o[16] = __builtin_bit_cast(uint32_t, __builtin_amdgcn_cvt_pknorm_u16(x, y));
  o[17] = __builtin_bit_cast(uint32_t, __builtin_amdgcn_cvt_pk_i16(static_cast<int>(a[i]), static_cast<int>(b[i])));
  o[18] = __builtin_bit_cast(uint32_t, __builtin_amdgcn_cvt_pk_u16(a[i], b[i]));
  o[19] = __builtin_amdgcn_cvt_pk_u8_f32(byteValues[i], i & 3U, c[i]);
  o[20] = __float_as_uint(__builtin_amdgcn_cvt_off_f32_i4(static_cast<int>(a[i])));
}

uint32_t byteOf(uint64_t value, int index) { return static_cast<uint32_t>(value >> (index * 8)) & 0xffU; }

uint32_t differences(uint64_t window, uint32_t reference, bool masked) {
  uint32_t sum = 0;
  for (int index = 0; index < 4; ++index) {
    const uint32_t x = byteOf(window, index);
    const uint32_t y = byteOf(reference, index);
    if (!masked || y != 0) sum += x > y ? x - y : y - x;
  }
  return sum;
}

uint64_t quadSad(uint64_t source, uint32_t reference, uint64_t accumulator, bool masked) {
  uint64_t result = 0;
  for (int index = 0; index < 4; ++index) {
    const uint64_t sum = differences(source >> (index * 8), reference, masked) + ((accumulator >> (index * 16)) & 0xffffU);
    result |= (sum & 0xffffU) << (index * 16);
  }
  return result;
}

// RDNA ISA V_CUBEID_F32 and its siblings: the major axis is z where |z| is at least |x| and |y|, then y where |y| is
// at least |x|, then x.
float cube(int which, float x, float y, float z) {
  const bool zMajor = std::fabs(z) >= std::fabs(x) && std::fabs(z) >= std::fabs(y);
  const bool yMajor = !zMajor && std::fabs(y) >= std::fabs(x);
  if (zMajor) {
    switch (which) {
      case 0: return z < 0 ? 5.0F : 4.0F;
      case 1: return z < 0 ? -x : x;
      case 2: return -y;
      default: return 2.0F * z;
    }
  }
  if (yMajor) {
    switch (which) {
      case 0: return y < 0 ? 3.0F : 2.0F;
      case 1: return x;
      case 2: return y < 0 ? -z : z;
      default: return 2.0F * y;
    }
  }
  switch (which) {
    case 0: return x < 0 ? 1.0F : 0.0F;
    case 1: return x < 0 ? z : -z;
    case 2: return -y;
    default: return 2.0F * x;
  }
}

uint32_t signedNorm(float value) {
  return static_cast<uint32_t>(static_cast<int32_t>(std::nearbyint(std::fmax(-1.0, std::fmin(1.0, value)) * 32767.0))) & 0xffffU;
}
uint32_t unsignedNorm(float value) { return static_cast<uint32_t>(std::nearbyint(std::fmax(0.0, std::fmin(1.0, value)) * 65535.0)); }
uint32_t saturate16(int64_t value, int64_t low, int64_t high) {
  return static_cast<uint32_t>(std::max(low, std::min(high, value))) & 0xffffU;
}

std::vector<uint32_t> mediaReference(uint32_t i, const std::vector<uint32_t> &a, const std::vector<uint32_t> &b,
                                     const std::vector<uint32_t> &c, const std::vector<uint64_t> &w) {
  const uint32_t count = static_cast<uint32_t>(a.size());
  std::vector<uint32_t> o(MediaResults);
  for (int index = 0; index < 4; ++index)
    o[0] |= ((byteOf(a[i], index) + byteOf(b[i], index) + ((c[i] >> (index * 8)) & 1U)) >> 1) << (index * 8);
  o[1] = c[i] + differences(a[i], b[i], false);
  o[2] = c[i] + differences(a[i], b[i], true);
  const uint64_t other = w[(i + 1) % count];
  const uint64_t qsad = quadSad(w[i], b[i], other, false);
  const uint64_t mqsad = quadSad(w[i], b[i], other, true);
  o[3] = static_cast<uint32_t>(qsad);
  o[4] = static_cast<uint32_t>(qsad >> 32);
  o[5] = static_cast<uint32_t>(mqsad);
  o[6] = static_cast<uint32_t>(mqsad >> 32);
  const uint32_t accumulators[4] = {c[i], a[i], b[i], c[i] ^ a[i]};
  for (int index = 0; index < 4; ++index)
    o[7 + index] = accumulators[index] + differences(w[i] >> (index * 8), b[i], true);
  const size_t at = static_cast<size_t>(i) * 3;
  const float x = Directions[at];
  const float y = Directions[at + 1];
  const float z = Directions[at + 2];
  for (int which = 0; which < 4; ++which)
    o[11 + which] = floatBits(cube(which, x, y, z));
  o[15] = signedNorm(x) | signedNorm(y) << 16;
  o[16] = unsignedNorm(x) | unsignedNorm(y) << 16;
  o[17] = saturate16(static_cast<int32_t>(a[i]), INT16_MIN, INT16_MAX) | saturate16(static_cast<int32_t>(b[i]), INT16_MIN, INT16_MAX) << 16;
  o[18] = saturate16(a[i], 0, UINT16_MAX) | saturate16(b[i], 0, UINT16_MAX) << 16;
  const uint32_t byte = static_cast<uint32_t>(std::nearbyint(std::fmax(0.0, std::fmin(255.0, Bytes[i]))));
  const uint32_t shift = (i & 3U) * 8U;
  o[19] = (c[i] & ~(0xffU << shift)) | (byte << shift);
  o[20] = floatBits(static_cast<float>((static_cast<int32_t>(a[i] << 28)) >> 28) / 16.0F);
  return o;
}

} // namespace

TEST_CASE("intrinsics/hip/media", "[unified][intrinsics]") {
  const uint32_t count = static_cast<uint32_t>(Words.size());
  std::vector<uint32_t> a(Words);
  const auto b = iota(0U, count) | map([&](uint32_t i) { return Words[((i * 7) + 3) % count]; }) | to_vector();
  const auto c = iota(0U, count) | map([&](uint32_t i) { return Words[((i * 5) + 1) % count] & 0x00ffffffU; }) | to_vector();
  const auto w = iota(0U, count)
                 | map([&](uint32_t i) { return static_cast<uint64_t>(a[i]) | static_cast<uint64_t>(Words[(i + 3) % count]) << 32; })
                 | to_vector();
  DeviceBuffer<uint32_t> da(a);
  DeviceBuffer<uint32_t> db(b);
  DeviceBuffer<uint32_t> dc(c);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * MediaResults));
  DeviceBuffer<uint64_t> dw(w);
  DeviceBuffer<float> f(Directions);
  DeviceBuffer<float> byteValues(Bytes);
  media<<<1, count>>>(da.data, db.data, dc.data, dw.data, f.data, byteValues.data, out.data, count);
  finish("media");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count; ++i) {
    const auto row = actual.begin() + static_cast<ptrdiff_t>(i) * MediaResults;
    expectEqual(std::vector<uint32_t>(row, row + MediaResults), mediaReference(i, a, b, c, w), fmt::format("media {}", i));
  }
}

namespace {
// __builtin_elementwise_add_sat and _sub_sat: v_add_i32 and v_sub_i32 with clamp on GFX9, v_add_nc_i32 and
// v_sub_nc_i32 with clamp from GFX10; the unsigned sum takes v_add_u32 or v_add_nc_u32 with clamp.
__global__ void saturating(const int32_t *a, const int32_t *b, int32_t *out, uint32_t count) {
  const size_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= static_cast<size_t>(count) * count) return;
  const int32_t x = a[i / count];
  const int32_t y = b[i % count];
  out[i * 3] = __builtin_elementwise_add_sat(x, y);
  out[(i * 3) + 1] = __builtin_elementwise_sub_sat(x, y);
  out[(i * 3) + 2] = static_cast<int32_t>(__builtin_elementwise_add_sat(static_cast<uint32_t>(x), static_cast<uint32_t>(y)));
}

} // namespace

TEST_CASE("intrinsics/hip/saturating-arithmetic", "[unified][intrinsics]") {
  const std::vector<int32_t> values = {0, 1, -1, 7, -7, 1000000, -1000000, INT32_MAX, INT32_MIN, INT32_MAX - 1, INT32_MIN + 1, 0x40000000};
  const uint32_t count = static_cast<uint32_t>(values.size());
  DeviceBuffer<int32_t> a(values);
  DeviceBuffer<int32_t> b(values);
  DeviceBuffer<int32_t> out(std::vector<int32_t>(static_cast<size_t>(count) * count * 3));
  saturating<<<((count * count) + 63) / 64, 64>>>(a.data, b.data, out.data, count);
  finish("saturating arithmetic");
  const auto expected =
      values ^ flat_map([&](int32_t x) {
        return values ^ flat_map([&](int32_t y) {
                 const int64_t sum = static_cast<int64_t>(x) + y;
                 const int64_t difference = static_cast<int64_t>(x) - y;
                 const uint64_t unsignedSum = static_cast<uint64_t>(static_cast<uint32_t>(x)) + static_cast<uint32_t>(y);
                 return std::vector<int32_t>{static_cast<int32_t>(std::max<int64_t>(INT32_MIN, std::min<int64_t>(INT32_MAX, sum))),
                                             static_cast<int32_t>(std::max<int64_t>(INT32_MIN, std::min<int64_t>(INT32_MAX, difference))),
                                             static_cast<int32_t>(static_cast<uint32_t>(std::min<uint64_t>(UINT32_MAX, unsignedSum)))};
               });
      });
  expectEqual(out.download(), expected, "saturating arithmetic");
}

namespace {
// __half division ends in v_div_fixup_f16, which signs infinities, zeros and NaN from the operands.
__global__ void halfDivision(const uint16_t *a, const uint16_t *b, uint16_t *out, uint32_t count) {
  const uint32_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
  out[i] = __half_as_ushort(__ushort_as_half(a[i]) / __ushort_as_half(b[i]));
}

} // namespace

TEST_CASE("intrinsics/hip/half-division", "[unified][intrinsics]") {
  const float infinity = std::numeric_limits<float>::infinity();
  const std::vector<std::pair<float, float>> pairs = {{6.0F, 3.0F},         {1.0F, 4.0F},      {7.0F, -2.0F},    {-0.5F, 0.25F},
                                                      {1.0F, 0.0F},         {-1.0F, 0.0F},     {1.0F, -0.0F},    {0.0F, 5.0F},
                                                      {-0.0F, 5.0F},        {0.0F, 0.0F},      {infinity, 2.0F}, {2.0F, infinity},
                                                      {infinity, infinity}, {-infinity, 3.0F}, {65504.0F, 0.5F}, {3.0F, 1024.0F}};
  const auto a = pairs ^ map([](float x, float) { return halfBits(x); });
  const auto b = pairs ^ map([](float, float y) { return halfBits(y); });
  const auto expected = pairs ^ map([](float x, float y) { return halfBits(x / y); });
  const uint32_t count = static_cast<uint32_t>(pairs.size());
  DeviceBuffer<uint16_t> da(a);
  DeviceBuffer<uint16_t> db(b);
  DeviceBuffer<uint16_t> out{std::vector<uint16_t>(count)};
  halfDivision<<<1, count>>>(da.data, db.data, out.data, count);
  finish("half division");
  const auto actual = out.download();
  for (uint32_t i = 0; i < count; ++i) {
    const bool nan = std::isnan(pairs[i].first / pairs[i].second);
    const bool actualNan = (actual[i] & 0x7c00U) == 0x7c00U && (actual[i] & 0x3ffU) != 0U;
    if (nan ? !actualNan : actual[i] != expected[i])
      throw std::runtime_error(
          fmt::format("half division {} / {}: expected {:#x}, got {:#x}", pairs[i].first, pairs[i].second, expected[i], actual[i]));
  }
}

namespace {
// __builtin_amdgcn_permlane16 and _permlanex16 (GFX10 onwards) and __builtin_amdgcn_permlane64 (GFX11 onwards); the
// selectors reverse each row of 16 lanes.
__global__ void lanePermutation(uint32_t *out, uint32_t *supported) {
  const uint32_t lane = threadIdx.x;
  [[maybe_unused]] const uint32_t value = (lane * 10U) + 1U;
  uint32_t row = 0;
  uint32_t crossed = 0;
  uint32_t half = 0;
  uint32_t level = 0;
#if defined(__GFX10__) || defined(__GFX11__) || defined(__GFX12__)
  row = __builtin_amdgcn_permlane16(0u, value, 0x89abcdefu, 0x01234567u, false, false);
  crossed = __builtin_amdgcn_permlanex16(0u, value, 0x89abcdefu, 0x01234567u, false, false);
  level = 1;
#endif
#if defined(__GFX11__) || defined(__GFX12__)
  half = __builtin_amdgcn_permlane64(value);
  level = 2;
#endif
  uint32_t *o = out + (static_cast<size_t>(lane) * 3);
  o[0] = row;
  o[1] = crossed;
  o[2] = half;
  if (lane == 0) *supported = level;
}

} // namespace

TEST_CASE("intrinsics/hip/lane-permutation", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 64;
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(lanes) * 3));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  lanePermutation<<<1, lanes>>>(out.data, supported.data);
  finish("lane permutation");
  const uint32_t level = supported.download()[0];
  if (level == 0) SKIP("V_PERMLANE16 needs GFX10 or later");
  const uint32_t wave = static_cast<uint32_t>(waveLanes());
  const auto actual = out.download();
  for (uint32_t lane = 0; lane < lanes; ++lane) {
    const uint32_t reversed = (lane & ~15U) + 15U - (lane & 15U);
    const uint32_t other = (((lane & ~15U) ^ 16U) + 15U - (lane & 15U));
    const uint32_t swapped = wave == 64 ? lane ^ 32U : lane;
    const auto row = actual.begin() + static_cast<ptrdiff_t>(lane) * 3;
    expectEqual(std::vector<uint32_t>(row, row + 3), {(reversed * 10U) + 1U, (other * 10U) + 1U, level >= 2 ? (swapped * 10U) + 1U : 0U},
                fmt::format("lane {}", lane));
  }
}

namespace {
// A dynamic index into a vector held in registers: s_set_gpr_idx_on around v_mov_b32 on GFX9, v_movrels_b32 and
// v_movreld_b32 elsewhere.
using Floats16 = float __attribute__((ext_vector_type(16)));

__global__ void dynamicIndex(const Floats16 *in, float *out, Floats16 *written, int k) {
  Floats16 v = in[threadIdx.x];
  out[threadIdx.x] = v[k & 15];
  out[threadIdx.x + 64] = v[threadIdx.x & 15];
  v[(k + static_cast<int>(threadIdx.x)) & 15] = 3.0F;
  written[threadIdx.x] = v;
}

} // namespace

TEST_CASE("intrinsics/hip/dynamic-index", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 64;
  constexpr int k = 5;
  const auto inputs =
      iota(size_t{0}, static_cast<size_t>(lanes) * 16) | map([](size_t i) { return static_cast<float>(i) * 0.5F; }) | to_vector();
  Floats16 *in = nullptr;
  Floats16 *written = nullptr;
  check(hipMalloc(reinterpret_cast<void **>(&in), inputs.size() * sizeof(float)), "allocation");
  check(hipMalloc(reinterpret_cast<void **>(&written), inputs.size() * sizeof(float)), "allocation");
  check(hipMemcpy(in, inputs.data(), inputs.size() * sizeof(float), hipMemcpyHostToDevice), "upload");
  DeviceBuffer<float> out(std::vector<float>(static_cast<size_t>(lanes) * 2));
  dynamicIndex<<<1, lanes>>>(in, out.data, written, k);
  finish("dynamic index");
  std::vector<float> stored(inputs.size());
  check(hipMemcpy(stored.data(), written, stored.size() * sizeof(float), hipMemcpyDeviceToHost), "download");
  (void)hipFree(in);
  (void)hipFree(written);
  const auto actual = out.download();
  for (uint32_t lane = 0; lane < lanes; ++lane) {
    if (actual[lane] != inputs[(lane * 16) + k] || actual[lane + 64] != inputs[(lane * 16) + (lane & 15)])
      throw std::runtime_error(fmt::format("dynamic index read at lane {}", lane));
    for (uint32_t element = 0; element < 16; ++element) {
      const float expected = element == ((k + lane) & 15U) ? 3.0F : inputs[(lane * 16) + element];
      if (stored[(lane * 16) + element] != expected)
        throw std::runtime_error(fmt::format("dynamic index write at lane {} element {}", lane, element));
    }
  }
}

namespace {
// Byte and short arithmetic the SDWA peephole folds into selected sources and destinations on GFX8 to GFX10.
using Bytes4 = unsigned char __attribute__((ext_vector_type(4)));
using Chars4 = signed char __attribute__((ext_vector_type(4)));
using Shorts2 = short __attribute__((ext_vector_type(2)));

__global__ void subDword(const uint32_t *in, uint32_t *out, float *floats, uint32_t count) {
  const size_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= static_cast<size_t>(count) * count) return;
  const uint32_t x = in[i / count];
  const uint32_t y = in[i % count];
  const Bytes4 ux = __builtin_bit_cast(Bytes4, x);
  const Bytes4 uy = __builtin_bit_cast(Bytes4, y);
  const Chars4 cx = __builtin_bit_cast(Chars4, x);
  const Chars4 cy = __builtin_bit_cast(Chars4, y);
  const Shorts2 sx = __builtin_bit_cast(Shorts2, x);
  const Shorts2 sy = __builtin_bit_cast(Shorts2, y);
  const Bytes4 u = (ux > uy ? ux : uy) + (ux * uy) - (ux >> 1);
  const Chars4 c = (cx < cy ? cx : cy) + (cx >> 2) + (cx * cy);
  const Shorts2 s = (sx > sy ? sx : sy) + (sx >> 3) + (sx * sy);
  out[i * 4] = __builtin_bit_cast(uint32_t, u);
  out[(i * 4) + 1] = __builtin_bit_cast(uint32_t, c);
  out[(i * 4) + 2] = __builtin_bit_cast(uint32_t, s);
  out[(i * 4) + 3] = static_cast<uint32_t>(max(static_cast<int>(static_cast<short>(x >> 16)), static_cast<int>(static_cast<short>(x)))
                                           + min((x >> 8) & 0xffU, x & 0xffU));
  floats[i * 2] = (static_cast<float>(y >> 24) * 0.5F) + fabsf(static_cast<float>(static_cast<int16_t>(x >> 16)))
                  - static_cast<float>(static_cast<uint16_t>(x));
  floats[(i * 2) + 1] = fminf(fmaxf(static_cast<float>(static_cast<uint8_t>(y >> 8)) * 0.25F, 0.0F), 1.0F);
}

} // namespace

TEST_CASE("intrinsics/hip/sub-dword", "[unified][intrinsics]") {
  const uint32_t count = static_cast<uint32_t>(Words.size());
  DeviceBuffer<uint32_t> in(Words);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(count) * count * 4));
  DeviceBuffer<float> floats(std::vector<float>(static_cast<size_t>(count) * count * 2));
  subDword<<<((count * count) + 63) / 64, 64>>>(in.data, out.data, floats.data, count);
  finish("sub-dword");
  const auto actual = out.download();
  const auto actualFloats = floats.download();
  for (uint32_t i = 0; i < count * count; ++i) {
    const uint32_t x = Words[i / count];
    const uint32_t y = Words[i % count];
    uint32_t u = 0;
    uint32_t c = 0;
    uint32_t s = 0;
    for (int index = 0; index < 4; ++index) {
      const uint32_t a = byteOf(x, index);
      const uint32_t b = byteOf(y, index);
      u |= ((std::max(a, b) + (a * b) - (a >> 1)) & 0xffU) << (index * 8);
      const int32_t p = static_cast<int8_t>(a);
      const int32_t q = static_cast<int8_t>(b);
      c |= (static_cast<uint32_t>(std::min(p, q) + (p >> 2) + (p * q)) & 0xffU) << (index * 8);
    }
    for (int index = 0; index < 2; ++index) {
      const int32_t p = static_cast<int16_t>(x >> (index * 16));
      const int32_t q = static_cast<int16_t>(y >> (index * 16));
      s |= (static_cast<uint32_t>(std::max(p, q) + (p >> 3) + (p * q)) & 0xffffU) << (index * 16);
    }
    const uint32_t mixed =
        static_cast<uint32_t>(std::max(static_cast<int>(static_cast<int16_t>(x >> 16)), static_cast<int>(static_cast<int16_t>(x)))
                              + static_cast<int>(std::min((x >> 8) & 0xffU, x & 0xffU)));
    const auto row = actual.begin() + static_cast<ptrdiff_t>(i) * 4;
    expectEqual(std::vector<uint32_t>(row, row + 4), {u, c, s, mixed}, fmt::format("sub-dword {}", i));
    const float first = (static_cast<float>(y >> 24) * 0.5F) + std::fabs(static_cast<float>(static_cast<int16_t>(x >> 16)))
                        - static_cast<float>(static_cast<uint16_t>(x));
    const float second = std::fmin(std::fmax(static_cast<float>(static_cast<uint8_t>(y >> 8)) * 0.25F, 0.0F), 1.0F);
    if (actualFloats[static_cast<size_t>(i) * 2] != first || actualFloats[(static_cast<size_t>(i) * 2) + 1] != second)
      throw std::runtime_error(fmt::format("sub-dword floats {}", i));
  }
}

namespace {
// __builtin_amdgcn_ds_append and _ds_consume: each wave moves the LDS counter by its active lanes and every lane
// receives the count before. Hardware addresses the counter by the instruction offset alone, so the counter is the
// first LDS dword, which LLVM's M0 operand cannot move.
__global__ void appendConsume(uint32_t *out, uint32_t *counter, bool consume) {
  __shared__ int local[1];
  if (threadIdx.x == 0) local[0] = static_cast<int>(*counter);
  __syncthreads();
  const int count =
      consume ? __builtin_amdgcn_ds_consume(inAddressSpace<3>(&local[0])) : __builtin_amdgcn_ds_append(inAddressSpace<3>(&local[0]));
  out[threadIdx.x] = static_cast<uint32_t>(count);
  __syncthreads();
  if (threadIdx.x == 0) *counter = static_cast<uint32_t>(local[0]);
}

} // namespace

TEST_CASE("intrinsics/hip/append-consume", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 128;
  const uint32_t wave = static_cast<uint32_t>(waveLanes());
  for (const bool consume : {false, true}) {
    const uint32_t start = consume ? 1000U : 40U;
    DeviceBuffer<uint32_t> out(std::vector<uint32_t>(lanes, 0U));
    DeviceBuffer<uint32_t> counter(std::vector<uint32_t>(1, start));
    appendConsume<<<1, lanes>>>(out.data, counter.data, consume);
    finish("append and consume");
    const std::string name = consume ? "consume" : "append";
    expectEqual(counter.download(), {consume ? start - lanes : start + lanes}, name);
    const auto actual = out.download();
    std::vector<uint32_t> counts;
    for (uint32_t first = 0; first < lanes; first += wave) {
      for (uint32_t lane = first; lane < first + wave; ++lane)
        if (actual[lane] != actual[first]) throw std::runtime_error(fmt::format("{}: lane {} saw another count than its wave", name, lane));
      counts.push_back(actual[first]);
    }
    counts ^= sort();
    for (uint32_t index = 0; index < counts.size(); ++index) {
      const uint32_t expected = consume ? start - lanes + ((index + 1) * wave) : start + (index * wave);
      if (counts[index] != expected)
        throw std::runtime_error(fmt::format("{}: wave {} counted {}, expected {}", name, index, counts[index], expected));
    }
  }
}

namespace {
// unsafeAtomicAdd on __half2 (global_atomic_pk_add_f16 on GFX90A, GFX94x, GFX950 and GFX12), and the bfloat16 and LDS
// packed adds through their builtins where the targets have them.
__global__ void packedAtomics(__half2 *halves, [[maybe_unused]] uint32_t *bfloats, uint32_t *shared, uint32_t *supported) {
  __shared__ uint32_t local[4];
  if (threadIdx.x < 4) local[threadIdx.x] = 0;
  __syncthreads();
  unsafeAtomicAdd(&halves[threadIdx.x & 3], __halves2half2(__float2half(0.5F), __float2half(0.25F)));
  uint32_t level = 0;
#if defined(__gfx942__) || defined(__gfx950__) || defined(__GFX12__)
  using Shorts = short __attribute__((ext_vector_type(2)));
  using Float16x2 = _Float16 __attribute__((ext_vector_type(2)));
  __builtin_amdgcn_global_atomic_fadd_v2bf16(inAddressSpace<1>(reinterpret_cast<Shorts *>(&bfloats[threadIdx.x & 3])),
                                             Shorts{0x3f00, 0x3e80});
  __builtin_amdgcn_ds_atomic_fadd_v2f16(inAddressSpace<3>(reinterpret_cast<Float16x2 *>(&local[threadIdx.x & 3])),
                                        Float16x2{_Float16{0.5F}, _Float16{0.25F}});
  level = 1;
#endif
  __syncthreads();
  if (threadIdx.x < 4) shared[threadIdx.x] = local[threadIdx.x];
  if (threadIdx.x == 0) *supported = level;
}

} // namespace

TEST_CASE("intrinsics/hip/packed-atomics", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 128;
  DeviceBuffer<__half2> halves(std::vector<__half2>(4, __halves2half2(__float2half(0.0F), __float2half(0.0F))));
  DeviceBuffer<uint32_t> bfloats(std::vector<uint32_t>(4));
  DeviceBuffer<uint32_t> shared(std::vector<uint32_t>(4));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  packedAtomics<<<1, lanes>>>(halves.data, bfloats.data, shared.data, supported.data);
  finish("packed atomics");
  const uint32_t sums = static_cast<uint32_t>(halfBits(16.0F)) | static_cast<uint32_t>(halfBits(8.0F)) << 16;
  const auto packed = halves.download() ^ map([](const __half2 &value) {
                        uint32_t bits = 0;
                        std::memcpy(&bits, &value, sizeof(bits));
                        return bits;
                      });
  expectEqual(packed, std::vector<uint32_t>(4, sums), "packed half adds");
  if (supported.download()[0] != 0) {
    expectEqual(bfloats.download(), std::vector<uint32_t>(4, 0x41004180U), "packed bfloat16 adds");
    expectEqual(shared.download(), std::vector<uint32_t>(4, sums), "packed LDS half adds");
  }
}

namespace {
// Uniform float and half arithmetic, which GFX11.5 and GFX12 compute with SALU float instructions (s_add_f16,
// s_ceil_f32, s_cvt_f32_i32 and the like) and other targets with VALU ones.
__global__ void uniformFloats(float a, float b, int n, uint32_t u, __half h, __half g, float *out, int32_t *integers, uint16_t *halves) {
  out[0] = floorf(a);
  out[1] = ceilf(b);
  out[2] = truncf(a * 3.0F);
  out[3] = rintf(b * 0.5F);
  out[4] = fminf(a, b);
  out[5] = fmaxf(a, b);
  out[6] = static_cast<float>(n);
  out[7] = static_cast<float>(u);
  out[8] = (a * b) + static_cast<float>(n);
  out[9] = __half2float(h) * a;
  integers[0] = static_cast<int32_t>(a * b);
  integers[1] = static_cast<int32_t>(static_cast<uint32_t>(a - b));
  halves[0] = __half_as_ushort(__hadd(h, g));
  halves[1] = __half_as_ushort(__hmul(h, g));
  halves[2] = __half_as_ushort(__hsub(h, g));
  halves[3] = __half_as_ushort(__hmax(h, g));
  halves[4] = __half_as_ushort(__hmin(h, g));
  halves[5] = __half_as_ushort(hceil(h));
  halves[6] = __half_as_ushort(hfloor(g));
  halves[7] = __half_as_ushort(htrunc(__hneg(h)));
  halves[8] = __half_as_ushort(hrint(__hadd(g, g)));
  halves[9] = __half_as_ushort(__float2half(a));
}

} // namespace

TEST_CASE("intrinsics/hip/uniform-floats", "[unified][intrinsics]") {
  const float a = 2.75F;
  const float b = -1.5F;
  const int n = 7;
  const uint32_t u = 3000000000U;
  const float h = 1.5F;
  const float g = 0.25F;
  DeviceBuffer<float> out(std::vector<float>(10));
  DeviceBuffer<int32_t> integers(std::vector<int32_t>(2));
  DeviceBuffer<uint16_t> halves(std::vector<uint16_t>(10));
  uniformFloats<<<1, 1>>>(a, b, n, u, __float2half(h), __float2half(g), out.data, integers.data, halves.data);
  finish("uniform floats");
  const std::vector<float> expected = {
      std::floor(a),   std::ceil(b),          std::trunc(a * 3.0F),  std::nearbyint(b * 0.5F),        std::fmin(a, b),
      std::fmax(a, b), static_cast<float>(n), static_cast<float>(u), (a * b) + static_cast<float>(n), h * a};
  const auto actual = out.download();
  for (size_t i = 0; i < expected.size(); ++i)
    if (floatBits(actual[i]) != floatBits(expected[i]))
      throw std::runtime_error(fmt::format("uniform float {}: expected {}, got {}", i, expected[i], actual[i]));
  expectEqual(integers.download(), {static_cast<int32_t>(a * b), static_cast<int32_t>(static_cast<uint32_t>(a - b))},
              "uniform conversions");
  expectEqual(halves.download(),
              {halfBits(h + g), halfBits(h * g), halfBits(h - g), halfBits(std::fmax(h, g)), halfBits(std::fmin(h, g)),
               halfBits(std::ceil(h)), halfBits(std::floor(g)), halfBits(std::trunc(-h)), halfBits(std::nearbyint(g + g)), halfBits(a)},
              "uniform halves");
}

namespace {
// __builtin_amdgcn_update_dpp on a double with row_newbcast:1, which GFX90A routes as two 32-bit DPP moves and GFX94x
// and GFX950 as v_mov_b64_dpp.
__global__ void broadcastPairs(const double *in, double *out, [[maybe_unused]] uint32_t *supported) {
  double value = in[threadIdx.x];
#if defined(__gfx90a__) || defined(__gfx942__) || defined(__gfx950__)
  value = __builtin_amdgcn_update_dpp(0.0, value, 0x151, 0xf, 0xf, false);
  if (threadIdx.x == 0) *supported = 1;
#endif
  out[threadIdx.x] = value;
}

} // namespace

TEST_CASE("intrinsics/hip/dpp-pairs", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 64;
  const auto inputs = iota(0U, lanes) | map([](uint32_t lane) { return lane + 0.125; }) | to_vector();
  DeviceBuffer<double> in(inputs);
  DeviceBuffer<double> out{std::vector<double>(lanes)};
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  broadcastPairs<<<1, lanes>>>(in.data, out.data, supported.data);
  finish("dpp pairs");
  if (supported.download()[0] == 0) SKIP("row_newbcast needs GFX90A, GFX94x or GFX950");
  const auto actual = out.download();
  for (uint32_t lane = 0; lane < lanes; ++lane)
    if (actual[lane] != inputs[(lane & ~15U) + 1U]) throw std::runtime_error(fmt::format("dpp pair at lane {}: {}", lane, actual[lane]));
}

namespace {
// __builtin_elementwise_minimum and _maximum: IEEE 754-2019 operations that propagate NaN and order -0 below +0. GFX12
// emits v_minimum_f32, v_minimum_f64, v_maximum_f16, v_maximum3_f32 and v_pk_minimum_f16, GFX950 v_minimum3_f32 and
// v_pk_minimum3_f16, and earlier targets compare and select.
using Halves2 = _Float16 __attribute__((ext_vector_type(2)));

__global__ void ieeeExtrema(const float *a, const float *b, const float *c, const double *wideA, const double *wideB, float *singles,
                            double *doubles, uint16_t *halfResults, uint32_t count) {
  const size_t i = (blockIdx.x * blockDim.x) + threadIdx.x;
  if (i >= count) return;
  singles[3 * i] = __builtin_elementwise_minimum(a[i], b[i]);
  singles[(3 * i) + 1] = __builtin_elementwise_maximum(a[i], b[i]);
  singles[(3 * i) + 2] = __builtin_elementwise_maximum(__builtin_elementwise_maximum(a[i], b[i]), c[i]);
  doubles[2 * i] = __builtin_elementwise_minimum(wideA[i], wideB[i]);
  doubles[(2 * i) + 1] = __builtin_elementwise_maximum(wideA[i], wideB[i]);
  const _Float16 x = static_cast<_Float16>(a[i]);
  const _Float16 y = static_cast<_Float16>(b[i]);
  halfResults[4 * i] = __builtin_bit_cast(uint16_t, __builtin_elementwise_maximum(x, y));
  const Halves2 packed = __builtin_elementwise_minimum(Halves2{x, y}, Halves2{y, x});
  halfResults[(4 * i) + 1] = __builtin_bit_cast(uint16_t, packed.x);
  halfResults[(4 * i) + 2] = __builtin_bit_cast(uint16_t, packed.y);
  halfResults[(4 * i) + 3] =
      __builtin_bit_cast(uint16_t, __builtin_elementwise_minimum(__builtin_elementwise_minimum(x, y), static_cast<_Float16>(c[i])));
}

template <typename T> T ieeeMinimum(T x, T y) {
  if (std::isnan(x) || std::isnan(y)) return std::numeric_limits<T>::quiet_NaN();
  if (x == y) return std::signbit(x) ? x : y;
  return x < y ? x : y;
}

template <typename T> T ieeeMaximum(T x, T y) {
  if (std::isnan(x) || std::isnan(y)) return std::numeric_limits<T>::quiet_NaN();
  if (x == y) return std::signbit(x) ? y : x;
  return x > y ? x : y;
}

} // namespace

TEST_CASE("intrinsics/hip/ieee-minimum-maximum", "[unified][intrinsics]") {
  const float nan = std::numeric_limits<float>::quiet_NaN();
  const float infinity = std::numeric_limits<float>::infinity();
  const std::vector<float> values = {0.0F, -0.0F, 1.5F, -2.25F, nan, infinity, -infinity, 1024.0F};
  std::vector<float> a;
  std::vector<float> b;
  std::vector<float> c;
  for (float x : values)
    for (float y : values) {
      a.push_back(x);
      b.push_back(y);
      c.push_back(values[(a.size() * 3) % values.size()]);
    }
  const uint32_t count = static_cast<uint32_t>(a.size());
  DeviceBuffer<float> da(a);
  DeviceBuffer<float> db(b);
  DeviceBuffer<float> dc(c);
  DeviceBuffer<float> singles{std::vector<float>(static_cast<size_t>(count) * 3)};
  DeviceBuffer<double> wideA{std::vector<double>(a.begin(), a.end())};
  DeviceBuffer<double> wideB{std::vector<double>(b.begin(), b.end())};
  DeviceBuffer<double> doubles{std::vector<double>(static_cast<size_t>(count) * 2)};
  DeviceBuffer<uint16_t> halfResults{std::vector<uint16_t>(static_cast<size_t>(count) * 4)};
  ieeeExtrema<<<1, count>>>(da.data, db.data, dc.data, wideA.data, wideB.data, singles.data, doubles.data, halfResults.data, count);
  finish("ieee minimum and maximum");
  const auto actualSingles = singles.download();
  const auto actualDoubles = doubles.download();
  const auto actualHalves = halfResults.download();
  auto same = [](auto actual, auto expected) {
    return std::isnan(expected) ? std::isnan(actual) : actual == expected && std::signbit(actual) == std::signbit(expected);
  };
  auto half = [](uint16_t bits) { return __half2float(__ushort_as_half(bits)); };
  for (size_t i = 0; i < count; ++i) {
    const float x = a[i];
    const float y = b[i];
    const float z = c[i];
    const std::vector<std::pair<float, float>> singleChecks = {{actualSingles[3 * i], ieeeMinimum(x, y)},
                                                               {actualSingles[(3 * i) + 1], ieeeMaximum(x, y)},
                                                               {actualSingles[(3 * i) + 2], ieeeMaximum(ieeeMaximum(x, y), z)},
                                                               {half(actualHalves[4 * i]), ieeeMaximum(x, y)},
                                                               {half(actualHalves[(4 * i) + 1]), ieeeMinimum(x, y)},
                                                               {half(actualHalves[(4 * i) + 2]), ieeeMinimum(y, x)},
                                                               {half(actualHalves[(4 * i) + 3]), ieeeMinimum(ieeeMinimum(x, y), z)}};
    for (size_t k = 0; k < singleChecks.size(); ++k)
      if (!same(singleChecks[k].first, singleChecks[k].second))
        throw std::runtime_error(
            fmt::format("extremum {} of ({}, {}, {}): expected {}, got {}", k, x, y, z, singleChecks[k].second, singleChecks[k].first));
    const double expectedDoubles[] = {ieeeMinimum<double>(x, y), ieeeMaximum<double>(x, y)};
    for (size_t k = 0; k < 2; ++k)
      if (!same(actualDoubles[(2 * i) + k], expectedDoubles[k]))
        throw std::runtime_error(
            fmt::format("double extremum {} of ({}, {}): expected {}, got {}", k, x, y, expectedDoubles[k], actualDoubles[(2 * i) + k]));
  }
}
