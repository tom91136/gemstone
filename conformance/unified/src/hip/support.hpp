#pragma once
#include <algorithm>
#include <bit>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <hip/hip_runtime.h>

#include <fmt/format.h>

namespace hip_support {

inline void check(hipError_t error, std::string_view operation) {
  if (error != hipSuccess) throw std::runtime_error(std::string(operation) + ": " + hipGetErrorString(error));
}

template <typename T> struct DeviceBuffer {
  T *data = nullptr;
  size_t size = 0;
  explicit DeviceBuffer(const std::vector<T> &host) : size(host.size()) {
    check(hipMalloc(reinterpret_cast<void **>(&data), std::max<size_t>(size, 1) * sizeof(T)), "allocation");
    if (size != 0) check(hipMemcpy(data, host.data(), size * sizeof(T), hipMemcpyHostToDevice), "upload");
  }
  DeviceBuffer(const DeviceBuffer &) = delete;
  DeviceBuffer &operator=(const DeviceBuffer &) = delete;
  ~DeviceBuffer() { (void)hipFree(data); }
  [[nodiscard]] std::vector<T> download() const {
    std::vector<T> host(size);
    if (size != 0) check(hipMemcpy(host.data(), data, size * sizeof(T), hipMemcpyDeviceToHost), "download");
    return host;
  }
};

inline void finish(std::string_view name) {
  check(hipGetLastError(), fmt::format("{} launch", name));
  check(hipDeviceSynchronize(), fmt::format("{} synchronize", name));
}

inline std::string architecture() {
  int ordinal{};
  check(hipGetDevice(&ordinal), "hipGetDevice");
  hipDeviceProp_t properties{};
  check(hipGetDeviceProperties(&properties, ordinal), "hipGetDeviceProperties");
  return properties.gcnArchName;
}

/** Whether the V_DOT2 forms and WMMA sum their products through a fixed-point accumulator: gfx1100 to gfx1103 and gfx1150 to gfx1153. */
inline bool alignedDots(std::string_view architecture) { return architecture.starts_with("gfx110") || architecture.starts_with("gfx115"); }

enum class DotResult { Single, Half, BFloat16 };

inline uint16_t halfBitsOf(double value) {
  if (value == 0) return 0;
  const uint16_t sign = value < 0 ? 0x8000 : 0;
  const double magnitude = std::fabs(value);
  int exponent = std::max(std::ilogb(magnitude), -14);
  double significand = std::nearbyint(std::ldexp(magnitude, 10 - exponent));
  if (significand >= 2048) {
    significand /= 2;
    exponent += 1;
  }
  if (exponent > 15) return sign | 0x7c00;
  const auto fraction = static_cast<uint32_t>(significand) & 0x3ff;
  return static_cast<uint16_t>(sign | ((significand >= 1024 ? exponent + 15 : 0) << 10) | fraction);
}

inline double halfValueOf(uint16_t bits) {
  const int exponent = (bits >> 10) & 0x1f;
  const double fraction = bits & 0x3ff;
  const double magnitude = exponent == 0 ? std::ldexp(fraction, -24) : std::ldexp(fraction + 1024, exponent - 25);
  return bits & 0x8000 ? -magnitude : magnitude;
}

/**
 * The value of a V_DOT2 on finite normal operands as gfx1100 to gfx1103 and gfx1150 to gfx1153 compute it: the terms
 * align to a grid 26 bits below the exponent of the largest non-zero one, each truncated toward zero, and those whose
 * sign differs from the addend's add as their one's complement, losing a unit of the grid each. The product of two
 * elements takes two more than the sum of their exponents. The sum rounds to nearest even in the result's format, or
 * for bfloat16 to binary32 and then toward zero.
 */
inline double alignedDot(double left0, double right0, double left1, double right1, double addend, DotResult result) {
  struct Term {
    bool negative;
    double magnitude;
    int exponent;
  };
  const auto product = [](double left, double right) {
    return Term{std::signbit(left) != std::signbit(right), std::fabs(left * right),
                left == 0 || right == 0 ? 0 : std::ilogb(left) + std::ilogb(right) + 2};
  };
  const Term terms[3] = {
      product(left0, right0), product(left1, right1), {std::signbit(addend), std::fabs(addend), addend == 0 ? 0 : std::ilogb(addend)}};
  bool any = false;
  int leading = 0;
  for (const auto &term : terms)
    if (term.magnitude != 0) {
      leading = any ? std::max(leading, term.exponent) : term.exponent;
      any = true;
    }
  if (!any) return 0;
  const int grid = leading - 26;
  int64_t sum = 0;
  for (const auto &term : terms) {
    const auto aligned = static_cast<int64_t>(std::floor(std::ldexp(term.magnitude, -grid)));
    sum += term.negative == terms[2].negative ? aligned : ~aligned;
  }
  const double value = std::ldexp(static_cast<double>(terms[2].negative ? -sum : sum), grid);
  switch (result) {
    case DotResult::Half: return halfValueOf(halfBitsOf(value));
    case DotResult::BFloat16: return std::bit_cast<float>(std::bit_cast<uint32_t>(static_cast<float>(value)) & 0xffff0000U);
    default: return static_cast<float>(value);
  }
}

} // namespace hip_support
