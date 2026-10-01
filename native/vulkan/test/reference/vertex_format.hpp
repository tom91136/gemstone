// Frozen pre-migration reference, used only by differential tests.
#pragma once

#include <algorithm>
#include <array>
#include <bit>
#include <cmath>
#include <cstdint>
#include <cstring>
#include <optional>

#include "gemstone_execution_rpc.hpp"

#include <vulkan/vulkan.h>

namespace gemstone::vulkan {
using VertexEncoding = gemstone::execution::GraphicsVertexEncoding;
struct VertexFormat {
  VertexEncoding encoding;
  std::array<uint8_t, 4> widths;
  uint8_t components, bytes;
  bool bgra = false;
};
inline std::optional<VertexFormat> vertexFormat(VkFormat format) {
  const auto uniform = [](VertexEncoding encoding, uint8_t bits, uint8_t components, bool bgra = false) {
    return VertexFormat{encoding, {bits, bits, bits, bits}, components, static_cast<uint8_t>(bits / 8 * components), bgra};
  };
  const auto packed = [](VertexEncoding encoding, bool bgra) { return VertexFormat{encoding, {10, 10, 10, 2}, 4, 4, bgra}; };
#define GEMSTONE_VERTEX_NORMALIZED(PREFIX, BITS, LANES, BGRA)                                                                              \
  case VK_FORMAT_##PREFIX##_UNORM: return uniform(VertexEncoding::Unorm, BITS, LANES, BGRA);                                               \
  case VK_FORMAT_##PREFIX##_SNORM: return uniform(VertexEncoding::Snorm, BITS, LANES, BGRA);                                               \
  case VK_FORMAT_##PREFIX##_UINT: return uniform(VertexEncoding::Uint, BITS, LANES, BGRA);                                                 \
  case VK_FORMAT_##PREFIX##_SINT: return uniform(VertexEncoding::Sint, BITS, LANES, BGRA);                                                 \
  case VK_FORMAT_##PREFIX##_USCALED: return uniform(VertexEncoding::Uscaled, BITS, LANES, BGRA);                                           \
  case VK_FORMAT_##PREFIX##_SSCALED: return uniform(VertexEncoding::Sscaled, BITS, LANES, BGRA)
#define GEMSTONE_VERTEX_32(PREFIX, LANES)                                                                                                  \
  case VK_FORMAT_##PREFIX##_SFLOAT: return uniform(VertexEncoding::Float, 32, LANES);                                                      \
  case VK_FORMAT_##PREFIX##_UINT: return uniform(VertexEncoding::Uint, 32, LANES);                                                         \
  case VK_FORMAT_##PREFIX##_SINT: return uniform(VertexEncoding::Sint, 32, LANES)
#define GEMSTONE_VERTEX_PACKED(PREFIX, BGRA)                                                                                               \
  case VK_FORMAT_##PREFIX##_UNORM_PACK32: return packed(VertexEncoding::Unorm, BGRA);                                                      \
  case VK_FORMAT_##PREFIX##_SNORM_PACK32: return packed(VertexEncoding::Snorm, BGRA);                                                      \
  case VK_FORMAT_##PREFIX##_UINT_PACK32: return packed(VertexEncoding::Uint, BGRA);                                                        \
  case VK_FORMAT_##PREFIX##_SINT_PACK32: return packed(VertexEncoding::Sint, BGRA);                                                        \
  case VK_FORMAT_##PREFIX##_USCALED_PACK32: return packed(VertexEncoding::Uscaled, BGRA);                                                  \
  case VK_FORMAT_##PREFIX##_SSCALED_PACK32: return packed(VertexEncoding::Sscaled, BGRA)
  switch (format) {
    GEMSTONE_VERTEX_NORMALIZED(R8, 8, 1, false);
    GEMSTONE_VERTEX_NORMALIZED(R8G8, 8, 2, false);
    GEMSTONE_VERTEX_NORMALIZED(R8G8B8, 8, 3, false);
    GEMSTONE_VERTEX_NORMALIZED(R8G8B8A8, 8, 4, false);
    GEMSTONE_VERTEX_NORMALIZED(B8G8R8, 8, 3, true);
    GEMSTONE_VERTEX_NORMALIZED(B8G8R8A8, 8, 4, true);
    GEMSTONE_VERTEX_NORMALIZED(R16, 16, 1, false);
    GEMSTONE_VERTEX_NORMALIZED(R16G16, 16, 2, false);
    GEMSTONE_VERTEX_NORMALIZED(R16G16B16, 16, 3, false);
    GEMSTONE_VERTEX_NORMALIZED(R16G16B16A16, 16, 4, false);
    GEMSTONE_VERTEX_32(R32, 1);
    GEMSTONE_VERTEX_32(R32G32, 2);
    GEMSTONE_VERTEX_32(R32G32B32, 3);
    GEMSTONE_VERTEX_32(R32G32B32A32, 4);
    GEMSTONE_VERTEX_PACKED(A2B10G10R10, false);
    GEMSTONE_VERTEX_PACKED(A2R10G10B10, true);
    case VK_FORMAT_R16_SFLOAT: return uniform(VertexEncoding::Float, 16, 1);
    case VK_FORMAT_R16G16_SFLOAT: return uniform(VertexEncoding::Float, 16, 2);
    case VK_FORMAT_R16G16B16_SFLOAT: return uniform(VertexEncoding::Float, 16, 3);
    case VK_FORMAT_R16G16B16A16_SFLOAT: return uniform(VertexEncoding::Float, 16, 4);
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32: return uniform(VertexEncoding::Unorm, 8, 4);
    case VK_FORMAT_A8B8G8R8_SNORM_PACK32: return uniform(VertexEncoding::Snorm, 8, 4);
    case VK_FORMAT_A8B8G8R8_UINT_PACK32: return uniform(VertexEncoding::Uint, 8, 4);
    case VK_FORMAT_A8B8G8R8_SINT_PACK32: return uniform(VertexEncoding::Sint, 8, 4);
    case VK_FORMAT_A8B8G8R8_USCALED_PACK32: return uniform(VertexEncoding::Uscaled, 8, 4);
    case VK_FORMAT_A8B8G8R8_SSCALED_PACK32: return uniform(VertexEncoding::Sscaled, 8, 4);
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32: return VertexFormat{VertexEncoding::Ufloat, {11, 11, 10, 0}, 3, 4};
    default: return std::nullopt;
  }
#undef GEMSTONE_VERTEX_NORMALIZED
#undef GEMSTONE_VERTEX_32
#undef GEMSTONE_VERTEX_PACKED
}

// Decode the buffer's representation into the shader's 32-bit scalar lanes.
// Missing components are (0,0,0,1), with an integer one for integer attributes.
inline std::array<uint32_t, 4> decodeVertex(const VertexFormat &format, const uint8_t *source) {
  const bool integer = format.encoding == VertexEncoding::Uint || format.encoding == VertexEncoding::Sint;
  std::array<uint32_t, 4> result{0, 0, 0, integer ? 1u : 0x3f800000u};
  uint32_t bitOffset = 0;
  for (uint32_t lane = 0; lane < format.components; ++lane) {
    const uint32_t width = format.widths[lane];
    uint32_t bits = 0;
    if (width % 8 == 0) std::memcpy(&bits, source + bitOffset / 8, width / 8);
    else {
      std::memcpy(&bits, source, 4);
      bits = (bits >> bitOffset) & ((1u << width) - 1);
    }
    bitOffset += width;
    uint32_t decoded = bits;
    const auto signedValue = [&] {
      if (width == 32) return std::bit_cast<int32_t>(bits);
      const uint32_t sign = 1u << (width - 1);
      return static_cast<int32_t>(bits ^ sign) - static_cast<int32_t>(sign);
    };
    const auto asBits = [](float value) { return std::bit_cast<uint32_t>(value); };
    switch (format.encoding) {
      case VertexEncoding::Uint: break;
      case VertexEncoding::Sint: decoded = static_cast<uint32_t>(signedValue()); break;
      case VertexEncoding::Unorm: decoded = asBits(static_cast<float>(bits) / ((1u << width) - 1)); break;
      case VertexEncoding::Snorm: decoded = asBits(std::max(-1.0f, static_cast<float>(signedValue()) / ((1u << (width - 1)) - 1))); break;
      case VertexEncoding::Uscaled: decoded = asBits(static_cast<float>(bits)); break;
      case VertexEncoding::Sscaled: decoded = asBits(static_cast<float>(signedValue())); break;
      case VertexEncoding::Float:
      case VertexEncoding::Ufloat: {
        if (width == 32) break;
        const uint32_t fractionBits = format.encoding == VertexEncoding::Float ? 10 : width - 5;
        const uint32_t fraction = bits & ((1u << fractionBits) - 1);
        const uint32_t exponent = (bits >> fractionBits) & 31;
        const uint32_t sign = format.encoding == VertexEncoding::Float ? (bits & 0x8000u) << 16 : 0;
        if (exponent == 0) decoded = asBits(std::ldexp(static_cast<float>(fraction), -14 - static_cast<int>(fractionBits))) | sign;
        else if (exponent == 31) decoded = sign | 0x7f800000u | (fraction << (23 - fractionBits));
        else decoded = sign | ((exponent + 112) << 23) | (fraction << (23 - fractionBits));
        break;
      }
    }
    result[format.bgra && lane < 3 ? 2 - lane : lane] = decoded;
  }
  return result;
}
// Robust vertex fetches choose zero source bytes for any attribute outside the
// bound buffer. Check each term before adding so an overflowing address cannot
// wrap back into the buffer. Input extraction still supplies missing components.
inline std::array<uint32_t, 4> fetchVertex(const VertexFormat &format, const uint8_t *data, uint64_t size, uint64_t bindingOffset,
                                           uint32_t stride, uint32_t index, uint32_t attributeOffset) {
  const uint64_t element = static_cast<uint64_t>(stride) * index;
  if (bindingOffset <= size) {
    const auto remaining = size - bindingOffset;
    if (element <= remaining && attributeOffset <= remaining - element && format.bytes <= remaining - element - attributeOffset)
      return decodeVertex(format, data + bindingOffset + element + attributeOffset);
  }
  constexpr std::array<uint8_t, 16> zero{};
  return decodeVertex(format, zero.data());
}
} // namespace gemstone::vulkan
