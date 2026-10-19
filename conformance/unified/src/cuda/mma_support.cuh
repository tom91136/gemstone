#pragma once

#include <cstdint>
#include <cstring>
#include <random>
#include <stdexcept>
#include <vector>

#include "cuda/support.cuh"
#include "test_support.hpp"

#include <fmt/format.h>

// Warp-level matrix products checked against exact host references: each case picks element values whose products and
// sums every documented accumulation keeps exactly, so the reference is the exact result encoded in D's type.
namespace {

// Encodings of exactly representable values.
inline uint32_t floatBits(double value) { return unified::support::floatBits(static_cast<float>(value)); }
using unified::support::doubleBits;

// A value of a binary format with `exponent` and `fraction` bits and the given bias, which must be exact.
inline uint32_t encode(double value, int exponentBits, int fractionBits, int bias) {
  if (value == 0) return 0;
  const uint32_t sign = value < 0 ? 1U << (exponentBits + fractionBits) : 0U;
  double magnitude = value < 0 ? -value : value;
  int exponent = 0;
  while (magnitude >= 2)
    magnitude /= 2, ++exponent;
  while (magnitude < 1 && exponent > 1 - bias)
    magnitude *= 2, --exponent;
  const bool normal = magnitude >= 1;
  const double scaled = (normal ? magnitude - 1 : magnitude) * static_cast<double>(1U << fractionBits);
  const auto fraction = static_cast<uint32_t>(scaled);
  if (static_cast<double>(fraction) != scaled) throw std::invalid_argument(fmt::format("{} is not exact", value));
  const uint32_t field = normal ? static_cast<uint32_t>(exponent + bias) : 0U;
  return sign | (field << fractionBits) | fraction;
}
inline uint32_t half(double value) { return encode(value, 5, 10, 15); }
inline uint32_t bfloat(double value) { return encode(value, 8, 7, 127); }
inline uint32_t e4m3(double value) { return encode(value, 4, 3, 7); }
inline uint32_t e5m2(double value) { return encode(value, 5, 2, 15); }

// Small multiples of a power of two, which every format here holds exactly.
inline std::vector<double> values(std::mt19937 &random, size_t count, int range, int scale) {
  std::uniform_int_distribution<int> pick(-range, range);
  std::vector<double> result(count);
  for (auto &value : result)
    value = pick(random) * (scale >= 0 ? static_cast<double>(1 << scale) : 1.0 / static_cast<double>(1 << -scale));
  return result;
}

// Fragment coordinates of the mma layouts other than m8n8k4 with 16-bit elements, for a lane's element `index`;
// `chunk` is the elements a register packs below 32 bits, otherwise 1.
struct Coordinate {
  int row;
  int column;
};
__host__ __device__ inline Coordinate coordinateA(int lane, int index, int chunk) {
  return {(lane / 4) + (8 * ((index / chunk) % 2)), (lane % 4 * chunk) + (index % chunk) + (index / (2 * chunk) * (4 * chunk))};
}
__host__ __device__ inline Coordinate coordinateB(int lane, int index, int chunk) {
  return {(lane % 4 * chunk) + (index % chunk) + (index / chunk * (4 * chunk)), lane / 4};
}
__host__ __device__ inline Coordinate coordinateC(int lane, int index) {
  return {(lane / 4) + (8 * (index / 2)), (lane % 4 * 2) + (index % 2)};
}

struct Nibble {
  int lane;
  int offset;
};

} // namespace
