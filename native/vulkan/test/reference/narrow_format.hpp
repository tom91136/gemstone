// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <algorithm>
#include <array>
#include <cmath>
#include <cstdint>
#include <cstring>
#include <limits>
#include <optional>

#include "gemstone_image_descriptor.hpp"
#include "gemstone_spirv_constants.hpp"

#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
// Five-bit exponent formats share the same conversion, with or without a sign.
// Rounding is explicitly ties-to-even, independent of the host rounding mode.
inline double decodeSmallFloat(uint32_t bits, unsigned fractionBits, bool signedValue) {
  const auto fraction = bits & ((1u << fractionBits) - 1);
  const auto exponent = (bits >> fractionBits) & 31;
  double value = exponent == 31  ? (fraction ? std::numeric_limits<double>::quiet_NaN() : std::numeric_limits<double>::infinity())
                 : exponent == 0 ? std::ldexp(static_cast<double>(fraction), -14 - static_cast<int>(fractionBits))
                                 : std::ldexp(static_cast<double>((1u << fractionBits) + fraction),
                                              static_cast<int>(exponent) - 15 - static_cast<int>(fractionBits));
  return signedValue && (bits & (1u << (fractionBits + 5))) ? -value : value;
}
inline uint32_t encodeSmallFloat(double value, unsigned fractionBits, bool signedValue) {
  const uint32_t sign = signedValue && std::signbit(value) ? 1u << (fractionBits + 5) : 0;
  if (std::isnan(value)) return sign | (31u << fractionBits) | (1u << (fractionBits - 1));
  if (!signedValue && value < 0) return 0;
  value = std::abs(value);
  if (std::isinf(value) || value >= 65536.0) return sign | (31u << fractionBits);
  if (value == 0) return sign;
  const int exponent = std::max(-14, std::ilogb(value));
  const double scaled = std::ldexp(value, static_cast<int>(fractionBits) - exponent);
  const auto integral = static_cast<uint32_t>(scaled);
  const double remainder = scaled - integral;
  const uint32_t rounded = integral + (remainder > 0.5 || (remainder == 0.5 && (integral & 1)));
  return sign | ((static_cast<uint32_t>(exponent + 14) << fractionBits) + rounded);
}

enum class NarrowEncoding { Unorm, Half, Ufloat, SharedExponent };
struct NarrowImageFormat {
  uint32_t components, bytes, spirv;
  NarrowEncoding encoding;
  std::array<unsigned, 4> widths{}, shifts{};
  bool floating() const { return encoding != NarrowEncoding::Unorm; }
  bool attachment() const { return encoding != NarrowEncoding::SharedExponent; }
  double read(const uint8_t *pixel, unsigned lane) const {
    if (lane >= components) return lane == 3 ? 1.0 : 0.0;
    uint32_t bits = 0;
    if (encoding == NarrowEncoding::Half) {
      std::memcpy(&bits, pixel + lane * 2, 2);
      return decodeSmallFloat(bits, 10, true);
    }
    std::memcpy(&bits, pixel, bytes);
    if (encoding == NarrowEncoding::SharedExponent)
      return std::ldexp(static_cast<double>((bits >> (lane * 9)) & 511), static_cast<int>(bits >> 27) - 24);
    bits = (bits >> shifts[lane]) & ((1u << widths[lane]) - 1);
    return encoding == NarrowEncoding::Ufloat ? decodeSmallFloat(bits, widths[lane] - 5, false)
                                              : static_cast<double>(bits) / ((1u << widths[lane]) - 1);
  }
  void write(uint8_t *pixel, const float *values, uint32_t mask = 15) const {
    if (encoding == NarrowEncoding::SharedExponent) {
      // Shared exponents couple all channels. This format is transfer/sample only.
      std::array<double, 3> channels{};
      for (unsigned lane = 0; lane < 3; ++lane)
        channels[lane] = std::isnan(values[lane]) ? 0.0 : std::clamp(static_cast<double>(values[lane]), 0.0, 65408.0);
      const double maximum = *std::max_element(channels.begin(), channels.end());
      int exponent = maximum == 0 ? 0 : std::max(0, std::ilogb(maximum) + 16);
      if (std::floor(std::ldexp(maximum, 24 - exponent) + 0.5) == 512) ++exponent;
      uint32_t bits = static_cast<uint32_t>(exponent) << 27;
      for (unsigned lane = 0; lane < 3; ++lane)
        bits |= static_cast<uint32_t>(std::floor(std::ldexp(channels[lane], 24 - exponent) + 0.5)) << (lane * 9);
      std::memcpy(pixel, &bits, 4);
      return;
    }
    uint32_t packed = 0;
    if (encoding != NarrowEncoding::Half) std::memcpy(&packed, pixel, bytes);
    for (unsigned lane = 0; lane < components; ++lane) {
      if (!(mask & (1u << lane))) continue;
      if (encoding == NarrowEncoding::Half) {
        const auto bits = encodeSmallFloat(values[lane], 10, true);
        std::memcpy(pixel + lane * 2, &bits, 2);
      } else {
        const uint32_t limit = (1u << widths[lane]) - 1;
        const uint32_t bits =
            encoding == NarrowEncoding::Ufloat
                ? encodeSmallFloat(values[lane], widths[lane] - 5, false)
                : static_cast<uint32_t>(
                      std::lround((std::isnan(values[lane]) ? 0.0 : std::clamp(static_cast<double>(values[lane]), 0.0, 1.0)) * limit));
        packed = (packed & ~(limit << shifts[lane])) | (bits << shifts[lane]);
      }
    }
    if (encoding != NarrowEncoding::Half) std::memcpy(pixel, &packed, bytes);
  }
};
inline std::optional<NarrowImageFormat> narrowImageFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_B4G4R4A4_UNORM_PACK16:
      return NarrowImageFormat{4, 2, spirv::ImageDescriptor::Format::B4G4R4A4Unorm, NarrowEncoding::Unorm, {4, 4, 4, 4}, {4, 8, 12, 0}};
    case VK_FORMAT_R5G6B5_UNORM_PACK16:
      return NarrowImageFormat{3, 2, spirv::ImageDescriptor::Format::R5G6B5Unorm, NarrowEncoding::Unorm, {5, 6, 5, 0}, {11, 5, 0, 0}};
    case VK_FORMAT_A1R5G5B5_UNORM_PACK16:
      return NarrowImageFormat{4, 2, spirv::ImageDescriptor::Format::A1R5G5B5Unorm, NarrowEncoding::Unorm, {5, 5, 5, 1}, {10, 5, 0, 15}};
    case VK_FORMAT_A2B10G10R10_UNORM_PACK32:
      return NarrowImageFormat{4, 4, spirv::ImageFormat::Rgb10A2, NarrowEncoding::Unorm, {10, 10, 10, 2}, {0, 10, 20, 30}};
    case VK_FORMAT_R16_SFLOAT: return NarrowImageFormat{1, 2, spirv::ImageFormat::R16f, NarrowEncoding::Half};
    case VK_FORMAT_R16G16_SFLOAT: return NarrowImageFormat{2, 4, spirv::ImageFormat::Rg16f, NarrowEncoding::Half};
    case VK_FORMAT_R16G16B16A16_SFLOAT: return NarrowImageFormat{4, 8, spirv::ImageFormat::Rgba16f, NarrowEncoding::Half};
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32:
      return NarrowImageFormat{3, 4, spirv::ImageFormat::R11fG11fB10f, NarrowEncoding::Ufloat, {11, 11, 10, 0}, {0, 11, 22, 0}};
    case VK_FORMAT_E5B9G9R9_UFLOAT_PACK32:
      return NarrowImageFormat{3, 4, spirv::ImageDescriptor::Format::E5B9G9R9Ufloat, NarrowEncoding::SharedExponent};
    default: return std::nullopt;
  }
}
} // namespace gemstone::vulkan
