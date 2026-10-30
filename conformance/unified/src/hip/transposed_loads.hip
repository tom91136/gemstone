#include <cstdint>
#include <stdexcept>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>
#include <hip/hip_runtime.h>

#include "support.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

using namespace aspartame;
using namespace hip_support;

// The transposing loads hipcc emits from builtins: gfx950's ds_read_b64_tr_b16, _b8 and _b4 and ds_read_b96_tr_b6,
// and wave32 GFX12's global_load_tr_b128 and _b64. Each lane reads its own bytes and takes the elements the lane map
// routes to it from the lanes of its group; the references restate CK's and Triton's CDNA4 maps and TensileLite's
// GFX12 ones.
namespace {

// Byte `index` of the 16 lane `lane` reads.
__host__ __device__ uint8_t pattern(uint32_t lane, uint32_t index) { return static_cast<uint8_t>((37U * lane) + (11U * index) + 5U); }

// `bits` bits from bit `at` of the bytes lane `lane` reads.
uint32_t field(uint32_t lane, uint32_t at, uint32_t bits) {
  uint32_t window = 0;
  for (uint32_t offset = 0; offset < 4; ++offset)
    window |= static_cast<uint32_t>(pattern(lane, (at / 8) + offset)) << (8 * offset);
  return (window >> (at % 8)) & ((1U << bits) - 1U);
}

// The dwords lane `lane` holds after a load of `elements` elements of `bits`, element `e` from where `origin` says.
template <typename Origin> std::vector<uint32_t> expected(uint32_t lane, uint32_t bits, uint32_t elements, Origin origin) {
  std::vector<uint32_t> words(((bits * elements) + 31) / 32);
  for (uint32_t e = 0; e < elements; ++e) {
    const auto [source, index] = origin(lane, e);
    const uint64_t value = field(source, index * bits, bits);
    const uint32_t at = e * bits;
    words[at / 32] |= static_cast<uint32_t>(value << (at % 32));
    if ((at % 32) + bits > 32) words[(at / 32) + 1] |= static_cast<uint32_t>(value >> (32 - (at % 32)));
  }
  return words;
}

void compare(std::string_view name, const std::vector<uint32_t> &actual, uint32_t lanes, uint32_t stride, uint32_t first, uint32_t bits,
             uint32_t elements, std::pair<uint32_t, uint32_t> (*origin)(uint32_t, uint32_t)) {
  for (uint32_t lane = 0; lane < lanes; ++lane) {
    const auto words = expected(lane, bits, elements, origin);
    for (uint32_t word = 0; word < words.size(); ++word)
      if (actual[(lane * stride) + first + word] != words[word])
        throw std::runtime_error(fmt::format("{} dword {} of lane {}: {:#x} instead of {:#x}", name, word, lane,
                                             actual[(lane * stride) + first + word], words[word]));
  }
}

using Shorts4 = short __attribute__((ext_vector_type(4)));
using Ints2 = int __attribute__((ext_vector_type(2)));
using Ints3 = int __attribute__((ext_vector_type(3)));

constexpr uint32_t LocalResults = 9;

__global__ void localTransposes([[maybe_unused]] uint32_t *out, [[maybe_unused]] uint32_t *supported) {
#ifdef __gfx950__
  const uint32_t lane = threadIdx.x;
  __shared__ __attribute__((aligned(16))) uint8_t lds[64 * 16];
  for (uint32_t index = 0; index < 16; ++index)
    lds[(lane * 16) + index] = pattern(lane, index);
  __syncthreads();
  using Halves = __attribute__((address_space(3))) Shorts4 *;
  using Pair = __attribute__((address_space(3))) Ints2 *;
  using Triple = __attribute__((address_space(3))) Ints3 *;
  const auto at = reinterpret_cast<uintptr_t>(lds + (static_cast<size_t>(lane) * 16));
  const Shorts4 halves = __builtin_amdgcn_ds_read_tr16_b64_v4i16(reinterpret_cast<Halves>(at));
  const Ints2 bytes = __builtin_amdgcn_ds_read_tr8_b64_v2i32(reinterpret_cast<Pair>(at));
  const Ints2 nibbles = __builtin_amdgcn_ds_read_tr4_b64_v2i32(reinterpret_cast<Pair>(at));
  const Ints3 sixes = __builtin_amdgcn_ds_read_tr6_b96_v3i32(reinterpret_cast<Triple>(at));
  uint32_t *o = out + (static_cast<size_t>(lane) * LocalResults);
  o[0] = static_cast<uint16_t>(halves.x) | (static_cast<uint32_t>(static_cast<uint16_t>(halves.y)) << 16);
  o[1] = static_cast<uint16_t>(halves.z) | (static_cast<uint32_t>(static_cast<uint16_t>(halves.w)) << 16);
  o[2] = static_cast<uint32_t>(bytes.x);
  o[3] = static_cast<uint32_t>(bytes.y);
  o[4] = static_cast<uint32_t>(nibbles.x);
  o[5] = static_cast<uint32_t>(nibbles.y);
  o[6] = static_cast<uint32_t>(sixes.x);
  o[7] = static_cast<uint32_t>(sixes.y);
  o[8] = static_cast<uint32_t>(sixes.z);
  if (lane == 0) *supported = 1;
#endif
}

} // namespace

TEST_CASE("intrinsics/hip/local-transposed-loads", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 64;
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(lanes) * LocalResults));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  localTransposes<<<1, lanes>>>(out.data, supported.data);
  finish("local transposed loads");
  if (supported.download()[0] == 0) SKIP("LDS transposed loads need gfx950");
  const auto actual = out.download();
  // within each 16 lanes: tr16 takes four rows of a column of halves, tr8 eight rows of a column of bytes, and tr4 and
  // tr6 transpose all 16 lanes
  compare("tr16", actual, lanes, LocalResults, 0, 16, 4,
          [](uint32_t lane, uint32_t e) { return std::pair((lane & ~15U) + (4U * e) + ((lane & 15U) >> 2), lane & 3U); });
  compare("tr8", actual, lanes, LocalResults, 2, 8, 8,
          [](uint32_t lane, uint32_t e) { return std::pair((lane & ~15U) + (2U * e) + ((lane & 15U) >> 3), lane & 7U); });
  compare("tr4", actual, lanes, LocalResults, 4, 4, 16, [](uint32_t lane, uint32_t e) { return std::pair((lane & ~15U) + e, lane & 15U); });
  compare("tr6", actual, lanes, LocalResults, 6, 6, 16, [](uint32_t lane, uint32_t e) { return std::pair((lane & ~15U) + e, lane & 15U); });
}

namespace {
using Shorts8 = short __attribute__((ext_vector_type(8)));

constexpr uint32_t GlobalResults = 6;

__global__ void globalTransposes([[maybe_unused]] const uint8_t *data, [[maybe_unused]] uint32_t *out,
                                 [[maybe_unused]] uint32_t *supported) {
#ifdef __GFX12__
  const uint32_t lane = threadIdx.x;
  using Halves = __attribute__((address_space(1))) Shorts8 *;
  using Pair = __attribute__((address_space(1))) Ints2 *;
  const auto at = reinterpret_cast<uintptr_t>(data + lane * 16);
  const Shorts8 halves = __builtin_amdgcn_global_load_tr_b128_v8i16(reinterpret_cast<Halves>(at));
  const Ints2 bytes = __builtin_amdgcn_global_load_tr_b64_v2i32(reinterpret_cast<Pair>(at));
  uint32_t *o = out + lane * GlobalResults;
  for (int word = 0; word < 4; ++word)
    o[word] = static_cast<uint16_t>(halves[2 * word]) | (static_cast<uint32_t>(static_cast<uint16_t>(halves[2 * word + 1])) << 16);
  o[4] = static_cast<uint32_t>(bytes.x);
  o[5] = static_cast<uint32_t>(bytes.y);
  if (lane == 0) *supported = 1;
#endif
}

} // namespace

TEST_CASE("intrinsics/hip/global-transposed-loads", "[unified][intrinsics]") {
  constexpr uint32_t lanes = 32;
  const auto bytes =
      iota(0U, lanes)
      | flat_map([](uint32_t lane) { return iota(0U, 16U) | map([&](uint32_t index) { return pattern(lane, index); }) | to_vector(); })
      | to_vector();
  DeviceBuffer<uint8_t> data(bytes);
  DeviceBuffer<uint32_t> out(std::vector<uint32_t>(static_cast<size_t>(lanes) * GlobalResults));
  DeviceBuffer<uint32_t> supported(std::vector<uint32_t>(1));
  globalTransposes<<<1, lanes>>>(data.data, out.data, supported.data);
  finish("global transposed loads");
  if (supported.download()[0] == 0) SKIP("global transposed loads need wave32 GFX12");
  const auto actual = out.download();
  // b128 transposes the halves of each eight lanes; b64 gives each lane of 16 a byte column from two blocks of four rows
  compare("b128", actual, lanes, GlobalResults, 0, 16, 8, [](uint32_t lane, uint32_t e) { return std::pair((lane & ~7U) + e, lane & 7U); });
  compare("b64", actual, lanes, GlobalResults, 4, 8, 8, [](uint32_t lane, uint32_t e) {
    return std::pair((lane & ~15U) + (8U * (e >> 2)) + (4U * ((lane >> 3) & 1U)) + (e & 3U), lane & 7U);
  });
}
