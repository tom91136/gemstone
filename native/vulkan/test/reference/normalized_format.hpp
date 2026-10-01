// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <algorithm>
#include <array>
#include <cmath>
#include <cstdint>
#include <optional>

#include "gemstone_image_descriptor.hpp"
#include "gemstone_spirv_constants.hpp"

#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
struct NormalizedImageFormat {
  uint32_t components, spirv;
  bool isSigned = false, srgb = false, bgra = false;
  uint32_t channel(uint32_t lane) const { return bgra && lane < 3 ? 2 - lane : lane; }
  double read(const uint8_t *pixel, uint32_t lane) const {
    if (lane >= components) return lane == 3 ? 1.0 : 0.0;
    const auto value = pixel[channel(lane)];
    if (isSigned) return std::max(-1.0, static_cast<double>(value < 128 ? value : static_cast<int>(value) - 256) / 127.0);
    if (srgb && lane < 3) {
      static const auto table = [] {
        std::array<double, 256> result{};
        for (unsigned i = 0; i < result.size(); ++i) {
          const double encoded = i / 255.0;
          result[i] = encoded <= 0.04045 ? encoded / 12.92 : std::pow((encoded + 0.055) / 1.055, 2.4);
        }
        return result;
      }();
      return table[value];
    }
    return value / 255.0;
  }
  void write(uint8_t *pixel, uint32_t lane, double value) const {
    if (lane >= components) return;
    value = std::isnan(value) ? 0.0 : std::clamp(value, isSigned ? -1.0 : 0.0, 1.0);
    if (srgb && lane < 3) value = value <= 0.0031308 ? 12.92 * value : 1.055 * std::pow(value, 1.0 / 2.4) - 0.055;
    pixel[channel(lane)] = static_cast<uint8_t>(std::lround(value * (isSigned ? 127.0 : 255.0)));
  }
};
inline std::optional<NormalizedImageFormat> normalizedImageFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_R8_UNORM: return NormalizedImageFormat{1, spirv::ImageFormat::R8};
    case VK_FORMAT_R8_SNORM: return NormalizedImageFormat{1, spirv::ImageFormat::R8Snorm, true};
    case VK_FORMAT_R8G8_UNORM: return NormalizedImageFormat{2, spirv::ImageFormat::Rg8};
    case VK_FORMAT_R8G8_SNORM: return NormalizedImageFormat{2, spirv::ImageFormat::Rg8Snorm, true};
    case VK_FORMAT_R8G8B8A8_UNORM:
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32: return NormalizedImageFormat{4, spirv::ImageFormat::Rgba8};
    case VK_FORMAT_R8G8B8A8_SNORM:
    case VK_FORMAT_A8B8G8R8_SNORM_PACK32: return NormalizedImageFormat{4, spirv::ImageFormat::Rgba8Snorm, true};
    case VK_FORMAT_R8G8B8A8_SRGB:
    case VK_FORMAT_A8B8G8R8_SRGB_PACK32: return NormalizedImageFormat{4, spirv::ImageDescriptor::Format::Rgba8Srgb, false, true};
    case VK_FORMAT_B8G8R8A8_UNORM: return NormalizedImageFormat{4, spirv::ImageDescriptor::Format::Bgra8Unorm, false, false, true};
    case VK_FORMAT_B8G8R8A8_SRGB: return NormalizedImageFormat{4, spirv::ImageDescriptor::Format::Bgra8Srgb, false, true, true};
    default: return std::nullopt;
  }
}
} // namespace gemstone::vulkan
