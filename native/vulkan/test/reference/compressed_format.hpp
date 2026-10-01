// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <algorithm>
#include <array>
#include <cstdint>
#include <optional>

#include "gemstone_image_descriptor.hpp"
#include "gemstone_spirv_constants.hpp"

#include "../../third_party/swiftshader/ETC_Decoder.hpp"
#include "normalized_format.hpp"
#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
struct CompressedImageFormat {
  uint32_t bytes, components, compatibility;
  ETC_Decoder::InputType input;
  bool srgb = false, eac = false, isSigned = false;
  uint32_t shaderFormat() const {
    return components == 1   ? spirv::ImageFormat::R32f
           : components == 2 ? spirv::ImageFormat::Rg32f
           : components == 3 ? spirv::ImageDescriptor::Format::DecodedRgb32f
                             : spirv::ImageFormat::Rgba32f;
  }
  // ETC2/EAC blocks contain 4x4 texels, including partial edge blocks.
  static uint32_t blocks(uint32_t size) { return size / 4 + (size % 4 != 0); }
};
inline std::optional<CompressedImageFormat> compressedImageFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK: return CompressedImageFormat{8, 3, 1, ETC_Decoder::ETC_RGB};
    case VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK: return CompressedImageFormat{8, 3, 1, ETC_Decoder::ETC_RGB, true};
    case VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK: return CompressedImageFormat{8, 4, 2, ETC_Decoder::ETC_RGB_PUNCHTHROUGH_ALPHA};
    case VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK: return CompressedImageFormat{8, 4, 2, ETC_Decoder::ETC_RGB_PUNCHTHROUGH_ALPHA, true};
    case VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK: return CompressedImageFormat{16, 4, 3, ETC_Decoder::ETC_RGBA};
    case VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK: return CompressedImageFormat{16, 4, 3, ETC_Decoder::ETC_RGBA, true};
    case VK_FORMAT_EAC_R11_UNORM_BLOCK: return CompressedImageFormat{8, 1, 4, ETC_Decoder::ETC_R_UNSIGNED, false, true};
    case VK_FORMAT_EAC_R11_SNORM_BLOCK: return CompressedImageFormat{8, 1, 4, ETC_Decoder::ETC_R_SIGNED, false, true, true};
    case VK_FORMAT_EAC_R11G11_UNORM_BLOCK: return CompressedImageFormat{16, 2, 5, ETC_Decoder::ETC_RG_UNSIGNED, false, true};
    case VK_FORMAT_EAC_R11G11_SNORM_BLOCK: return CompressedImageFormat{16, 2, 5, ETC_Decoder::ETC_RG_SIGNED, false, true, true};
    default: return std::nullopt;
  }
}

// Output is linear RGBA, with exact 11-bit EAC normalization and default absent channels.
inline std::array<float, 64> decodeCompressedBlock(const CompressedImageFormat &format, const uint8_t *source) {
  std::array<float, 64> result{};
  for (unsigned pixel = 0; pixel < 16; ++pixel)
    result[pixel * 4 + 3] = 1.0f;
  if (!format.eac) {
    std::array<uint8_t, 64> bgra{};
    ETC_Decoder::Decode(source, bgra.data(), 4, 4, 16, 4, format.input);
    const NormalizedImageFormat layout{4, 0, false, format.srgb, true};
    for (unsigned pixel = 0; pixel < 16; ++pixel)
      for (unsigned lane = 0; lane < format.components; ++lane)
        result[pixel * 4 + lane] = static_cast<float>(layout.read(bgra.data() + pixel * 4, lane));
    return result;
  }
  static constexpr int modifiers[16][8] = {
      {-3, -6, -9, -15, 2, 5, 8, 14}, {-3, -7, -10, -13, 2, 6, 9, 12}, {-2, -5, -8, -13, 1, 4, 7, 12}, {-2, -4, -6, -13, 1, 3, 5, 12},
      {-3, -6, -8, -12, 2, 5, 7, 11}, {-3, -7, -9, -11, 2, 6, 8, 10},  {-4, -7, -8, -11, 3, 6, 7, 10}, {-3, -5, -8, -11, 2, 4, 7, 10},
      {-2, -6, -8, -10, 1, 5, 7, 9},  {-2, -5, -8, -10, 1, 4, 7, 9},   {-2, -4, -8, -10, 1, 3, 7, 9},  {-2, -5, -7, -10, 1, 4, 6, 9},
      {-3, -4, -7, -10, 2, 3, 6, 9},  {-1, -2, -3, -10, 0, 1, 2, 9},   {-4, -6, -8, -9, 3, 5, 7, 8},   {-3, -5, -7, -9, 2, 4, 6, 8}};
  for (unsigned lane = 0; lane < format.components; ++lane) {
    const auto *block = source + lane * 8;
    int base = block[0];
    if (format.isSigned && base >= 128) base -= 256;
    if (format.isSigned && base == -128) base = -127;
    const int multiplier = (block[1] >> 4) ? (block[1] >> 4) * 8 : 1;
    uint64_t selectors = 0;
    for (unsigned byte = 2; byte < 8; ++byte)
      selectors = (selectors << 8) | block[byte];
    for (unsigned y = 0; y < 4; ++y)
      for (unsigned x = 0; x < 4; ++x) {
        const auto selector = (selectors >> (45 - (x * 4 + y) * 3)) & 7;
        const int value = base * 8 + (format.isSigned ? 0 : 4) + modifiers[block[1] & 15][selector] * multiplier;
        result[(y * 4 + x) * 4 + lane] = format.isSigned ? std::clamp(value, -1023, 1023) / 1023.0f : std::clamp(value, 0, 2047) / 2047.0f;
      }
  }
  return result;
}
} // namespace gemstone::vulkan
