// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <algorithm>
#include <bit>
#include <cstdint>
#include <cstring>
#include <optional>

#include "gemstone_image_descriptor.hpp"
#include "gemstone_spirv_constants.hpp"

#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
struct IntegerImageFormat {
  uint32_t components, componentBytes, spirv;
  bool isSigned;
  bool packed = false;
  uint32_t texelBytes() const { return packed ? 4 : components * componentBytes; }
  uint32_t read(const uint8_t *pixel, uint32_t lane) const {
    if (lane >= components) return lane == 3 ? 1u : 0u;
    uint32_t value = 0;
    if (packed) {
      std::memcpy(&value, pixel, 4);
      return (value >> (lane * 10)) & (lane == 3 ? 3u : 1023u);
    }
    std::memcpy(&value, pixel + lane * componentBytes, componentBytes);
    if (isSigned && componentBytes < 4) {
      const uint32_t sign = 1u << (componentBytes * 8 - 1);
      value = (value ^ sign) - sign;
    }
    return value;
  }
  void write(uint8_t *pixel, uint32_t lane, uint32_t value) const {
    if (lane >= components) return;
    if (packed) {
      uint32_t bits;
      std::memcpy(&bits, pixel, 4);
      const uint32_t limit = lane == 3 ? 3u : 1023u, shift = lane * 10;
      bits = (bits & ~(limit << shift)) | (std::min(value, limit) << shift);
      std::memcpy(pixel, &bits, 4);
      return;
    }
    if (componentBytes < 4) {
      const uint32_t bits = componentBytes * 8;
      if (isSigned) value = static_cast<uint32_t>(std::clamp(std::bit_cast<int32_t>(value), -(1 << (bits - 1)), (1 << (bits - 1)) - 1));
      else value = std::min(value, (1u << bits) - 1);
    }
    std::memcpy(pixel + lane * componentBytes, &value, componentBytes);
  }
};
inline std::optional<IntegerImageFormat> integerImageFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_A2B10G10R10_UINT_PACK32: return IntegerImageFormat{4, 4, spirv::ImageFormat::Rgb10a2ui, false, true};
    case VK_FORMAT_R8_UINT: return IntegerImageFormat{1, 1, spirv::ImageFormat::R8ui, false};
    case VK_FORMAT_R8_SINT: return IntegerImageFormat{1, 1, spirv::ImageFormat::R8i, true};
    case VK_FORMAT_R8G8_UINT: return IntegerImageFormat{2, 1, spirv::ImageFormat::Rg8ui, false};
    case VK_FORMAT_R8G8_SINT: return IntegerImageFormat{2, 1, spirv::ImageFormat::Rg8i, true};
    case VK_FORMAT_A8B8G8R8_UINT_PACK32:
    case VK_FORMAT_R8G8B8A8_UINT: return IntegerImageFormat{4, 1, spirv::ImageFormat::Rgba8ui, false};
    case VK_FORMAT_A8B8G8R8_SINT_PACK32:
    case VK_FORMAT_R8G8B8A8_SINT: return IntegerImageFormat{4, 1, spirv::ImageFormat::Rgba8i, true};
    case VK_FORMAT_R16_UINT: return IntegerImageFormat{1, 2, spirv::ImageFormat::R16ui, false};
    case VK_FORMAT_R16_SINT: return IntegerImageFormat{1, 2, spirv::ImageFormat::R16i, true};
    case VK_FORMAT_R16G16_UINT: return IntegerImageFormat{2, 2, spirv::ImageFormat::Rg16ui, false};
    case VK_FORMAT_R16G16_SINT: return IntegerImageFormat{2, 2, spirv::ImageFormat::Rg16i, true};
    case VK_FORMAT_R16G16B16A16_UINT: return IntegerImageFormat{4, 2, spirv::ImageFormat::Rgba16ui, false};
    case VK_FORMAT_R16G16B16A16_SINT: return IntegerImageFormat{4, 2, spirv::ImageFormat::Rgba16i, true};
    case VK_FORMAT_R32_UINT: return IntegerImageFormat{1, 4, spirv::ImageFormat::R32ui, false};
    case VK_FORMAT_R32_SINT: return IntegerImageFormat{1, 4, spirv::ImageFormat::R32i, true};
    case VK_FORMAT_R32G32_UINT: return IntegerImageFormat{2, 4, spirv::ImageFormat::Rg32ui, false};
    case VK_FORMAT_R32G32_SINT: return IntegerImageFormat{2, 4, spirv::ImageFormat::Rg32i, true};
    case VK_FORMAT_R32G32B32A32_UINT: return IntegerImageFormat{4, 4, spirv::ImageFormat::Rgba32ui, false};
    case VK_FORMAT_R32G32B32A32_SINT: return IntegerImageFormat{4, 4, spirv::ImageFormat::Rgba32i, true};
    default: return std::nullopt;
  }
}
} // namespace gemstone::vulkan
