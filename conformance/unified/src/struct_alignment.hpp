#pragma once
#include <algorithm>
#include <array>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

#include "test_support.hpp"
#include <fmt/format.h>

// miniBUDE-shaped records under every layout a compiler can give them. Each backend places an array of one layout at
// the worst legal misalignment of a raw byte allocation, reads the fields through member expressions only, and writes
// every even element back in place. The shared words, bytes and reference below define what every backend must reproduce.
namespace unified::struct_alignment {

struct __attribute__((packed)) Packed {
  float x, y, z;
  int type;
};
struct __attribute__((packed, aligned(2))) Packed2 {
  float x, y, z;
  int type;
};
struct __attribute__((packed, aligned(4))) Packed4 {
  float x, y, z;
  int type;
};
struct __attribute__((packed, aligned(8))) Packed8 {
  float x, y, z;
  int type;
};
struct Natural {
  float x, y, z;
  int type;
};
struct __attribute__((aligned(16))) Aligned16 {
  float x, y, z;
  int type;
};
struct __attribute__((packed)) MixedPacked {
  char tag;
  float x, y, z;
  int type;
};
struct MixedNatural {
  char tag;
  float x, y, z;
  int type;
};

static_assert(alignof(Packed) == 1 && sizeof(Packed) == 16, "packed layout");
static_assert(alignof(Packed2) == 2 && sizeof(Packed2) == 16, "packed aligned(2) layout");
static_assert(alignof(Packed4) == 4 && sizeof(Packed4) == 16, "packed aligned(4) layout");
static_assert(alignof(Packed8) == 8 && sizeof(Packed8) == 16, "packed aligned(8) layout");
static_assert(alignof(Natural) == 4 && sizeof(Natural) == 16, "natural layout");
static_assert(alignof(Aligned16) == 16 && sizeof(Aligned16) == 16, "aligned(16) layout");
static_assert(alignof(MixedPacked) == 1 && sizeof(MixedPacked) == 17, "mixed packed layout");
static_assert(alignof(MixedNatural) == 4 && sizeof(MixedNatural) == 20, "mixed natural layout");
static_assert(offsetof(MixedPacked, x) == 1 && offsetof(MixedPacked, y) == 5 && offsetof(MixedPacked, z) == 9
                  && offsetof(MixedPacked, type) == 13,
              "mixed packed offsets");
static_assert(offsetof(MixedNatural, x) == 4 && offsetof(MixedNatural, y) == 8 && offsetof(MixedNatural, z) == 12
                  && offsetof(MixedNatural, type) == 16,
              "mixed natural offsets");

template <class T> inline constexpr bool Tagged = false;
template <> inline constexpr bool Tagged<MixedPacked> = true;
template <> inline constexpr bool Tagged<MixedNatural> = true;

inline constexpr uint32_t Count = 300;
// Output words per element: transformed x, y, z, raw x, y, z bits, type, tag.
inline constexpr uint32_t Words = 8;
// An array lives alignof(T) bytes into the allocation, so its address is a multiple of alignof(T) but not of twice that.
inline constexpr size_t BaseAlignment = 32;
inline constexpr size_t Guard = 16;
inline constexpr uint8_t Fill = 0xa5;

inline constexpr float R00 = 0.36F, R01 = 0.48F, R02 = -0.8F;
inline constexpr float R10 = -0.8F, R11 = 0.6F, R12 = 0.0078125F;
inline constexpr float R20 = 0.48F, R21 = 0.64F, R22 = 0.6F;
inline constexpr float T0 = 1.5F, T1 = -2.25F, T2 = 0.75F;
inline constexpr uint32_t TagMask = 0x5a;

inline std::string poseOptions() {
  using namespace aspartame;
  const std::array<std::pair<std::string_view, float>, 12> coefficients{{{"R00", R00},
                                                                         {"R01", R01},
                                                                         {"R02", R02},
                                                                         {"R10", R10},
                                                                         {"R11", R11},
                                                                         {"R12", R12},
                                                                         {"R20", R20},
                                                                         {"R21", R21},
                                                                         {"R22", R22},
                                                                         {"T0", T0},
                                                                         {"T1", T1},
                                                                         {"T2", T2}}};
  return coefficients | map([](std::string_view name, float value) { return fmt::format("-DPOSE_{}={:a}F", name, value); })
         | append(fmt::format("-DPOSE_TAG_MASK={}u", TagMask)) | mk_string(" ");
}

struct Element {
  char tag;
  float x, y, z;
  int32_t type;
};

struct Pose {
  float x, y, z;
};

inline Pose pose(float x, float y, float z) {
  return {.x = std::fma(R00, x, std::fma(R01, y, std::fma(R02, z, T0))),
          .y = std::fma(R10, x, std::fma(R11, y, std::fma(R12, z, T1))),
          .z = std::fma(R20, x, std::fma(R21, y, std::fma(R22, z, T2)))};
}

inline Element element(uint32_t i) {
  const auto coordinate = [&](uint32_t salt) {
    const uint32_t mixed = ((i + 1) * 2654435761U) ^ (salt * 40503U);
    return (static_cast<float>((mixed >> 8) & 0xffffU) - 32768.0F) / 97.0F;
  };
  return {.tag = static_cast<char>((i * 37U) + 0x81U),
          .x = coordinate(1),
          .y = coordinate(2),
          .z = coordinate(3),
          .type = static_cast<int32_t>((i + 1) * 0x9e3779b1U)};
}

template <class T> struct Fixture {
  static constexpr size_t Offset = alignof(T);
  std::vector<uint8_t> input;
  std::vector<uint8_t> expected;
  std::vector<uint32_t> words;
};

template <class T> void put(std::vector<uint8_t> &image, uint32_t i, const Element &e) {
  const size_t at = Fixture<T>::Offset + (static_cast<size_t>(i) * sizeof(T));
  if constexpr (Tagged<T>) std::memcpy(&image[at + offsetof(T, tag)], &e.tag, sizeof(e.tag));
  std::memcpy(&image[at + offsetof(T, x)], &e.x, sizeof(e.x));
  std::memcpy(&image[at + offsetof(T, y)], &e.y, sizeof(e.y));
  std::memcpy(&image[at + offsetof(T, z)], &e.z, sizeof(e.z));
  std::memcpy(&image[at + offsetof(T, type)], &e.type, sizeof(e.type));
}

template <class T> Fixture<T> fixture() {
  Fixture<T> f;
  f.input.assign(Fixture<T>::Offset + (static_cast<size_t>(Count) * sizeof(T)) + Guard, Fill);
  f.expected = f.input;
  f.words.resize(static_cast<size_t>(Count) * Words);
  for (uint32_t i = 0; i < Count; ++i) {
    const Element e = element(i);
    const Pose p = pose(e.x, e.y, e.z);
    put<T>(f.input, i, e);
    if (i % 2 == 0)
      put<T>(f.expected, i, {static_cast<char>(static_cast<uint32_t>(static_cast<uint8_t>(e.tag)) ^ TagMask), p.x, p.y, p.z, ~e.type});
    else put<T>(f.expected, i, e);
    const std::array<uint32_t, Words> row{
        support::floatBits(p.x),       support::floatBits(p.y),
        support::floatBits(p.z),       support::floatBits(e.x),
        support::floatBits(e.y),       support::floatBits(e.z),
        static_cast<uint32_t>(e.type), Tagged<T> ? static_cast<uint32_t>(static_cast<uint8_t>(e.tag)) : 0U};
    std::copy(row.begin(), row.end(), f.words.begin() + (static_cast<ptrdiff_t>(i) * Words));
  }
  return f;
}

inline constexpr std::array<std::string_view, Words> WordNames{"transformed x", "transformed y", "transformed z", "raw x bits",
                                                               "raw y bits",    "raw z bits",    "type",          "tag"};

template <class T> std::string locate(size_t at) {
  constexpr size_t offset = Fixture<T>::Offset;
  if (at < offset) return fmt::format("leading guard byte {}", at);
  const size_t relative = at - offset;
  if (relative >= static_cast<size_t>(Count) * sizeof(T)) return fmt::format("trailing guard byte {}", relative - (Count * sizeof(T)));
  return fmt::format("element {} byte {}", relative / sizeof(T), relative % sizeof(T));
}

template <class T> void verify(const Fixture<T> &f, const std::vector<uint8_t> &image, const std::vector<uint32_t> &words) {
  using namespace aspartame;
  REQUIRE(words.size() == f.words.size());
  REQUIRE(image.size() == f.expected.size());
  for (uint32_t i = 0; i < Count; ++i)
    for (uint32_t w = 0; w < Words; ++w) {
      const size_t at = (static_cast<size_t>(i) * Words) + w;
      INFO(fmt::format("element {} {}", i, WordNames[w]));
      CHECK(words[at] == f.words[at]);
    }
  constexpr size_t reported = 16;
  const auto mismatches =
      iota(size_t{0}, image.size()) | filter([&](size_t at) { return image[at] != f.expected[at]; }) | take(reported) | to_vector();
  CHECK(mismatches.empty());
  for (const size_t at : mismatches) {
    INFO(fmt::format("stored-back image: {}", locate<T>(at)));
    CHECK(image[at] == f.expected[at]);
  }
}

} // namespace unified::struct_alignment
