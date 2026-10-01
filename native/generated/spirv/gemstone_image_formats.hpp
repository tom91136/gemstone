/* Generated from ImageFormats. Regenerate with: sbt codegen */
#pragma once
#include <array>
#include <cstdint>
#include <optional>
#include <vulkan/vulkan.h>
#include "gemstone_execution_rpc.hpp"
namespace gemstone::vulkan {
inline std::optional<execution::GraphicsFormat> graphicsFormat(VkFormat format, VkImageAspectFlags aspects = 0) {
  switch (format) {
    case VK_FORMAT_R32_SFLOAT: return execution::GraphicsFormat::R32f;
    case VK_FORMAT_R32G32_SFLOAT: return execution::GraphicsFormat::Rg32f;
    case VK_FORMAT_R32G32B32A32_SFLOAT: return execution::GraphicsFormat::Rgba32f;
    case VK_FORMAT_A2B10G10R10_UINT_PACK32: return execution::GraphicsFormat::Rgb10a2ui;
    case VK_FORMAT_R8_UINT: return execution::GraphicsFormat::R8ui;
    case VK_FORMAT_R8_SINT: return execution::GraphicsFormat::R8i;
    case VK_FORMAT_R8G8_UINT: return execution::GraphicsFormat::Rg8ui;
    case VK_FORMAT_R8G8_SINT: return execution::GraphicsFormat::Rg8i;
    case VK_FORMAT_A8B8G8R8_UINT_PACK32:
    case VK_FORMAT_R8G8B8A8_UINT: return execution::GraphicsFormat::Rgba8ui;
    case VK_FORMAT_A8B8G8R8_SINT_PACK32:
    case VK_FORMAT_R8G8B8A8_SINT: return execution::GraphicsFormat::Rgba8i;
    case VK_FORMAT_R16_UINT: return execution::GraphicsFormat::R16ui;
    case VK_FORMAT_R16_SINT: return execution::GraphicsFormat::R16i;
    case VK_FORMAT_R16G16_UINT: return execution::GraphicsFormat::Rg16ui;
    case VK_FORMAT_R16G16_SINT: return execution::GraphicsFormat::Rg16i;
    case VK_FORMAT_R16G16B16A16_UINT: return execution::GraphicsFormat::Rgba16ui;
    case VK_FORMAT_R16G16B16A16_SINT: return execution::GraphicsFormat::Rgba16i;
    case VK_FORMAT_R32_UINT: return execution::GraphicsFormat::R32ui;
    case VK_FORMAT_R32_SINT: return execution::GraphicsFormat::R32i;
    case VK_FORMAT_R32G32_UINT: return execution::GraphicsFormat::Rg32ui;
    case VK_FORMAT_R32G32_SINT: return execution::GraphicsFormat::Rg32i;
    case VK_FORMAT_R32G32B32A32_UINT: return execution::GraphicsFormat::Rgba32ui;
    case VK_FORMAT_R32G32B32A32_SINT: return execution::GraphicsFormat::Rgba32i;
    case VK_FORMAT_R8_UNORM: return execution::GraphicsFormat::R8;
    case VK_FORMAT_R8_SNORM: return execution::GraphicsFormat::R8Snorm;
    case VK_FORMAT_R8G8_UNORM: return execution::GraphicsFormat::Rg8;
    case VK_FORMAT_R8G8_SNORM: return execution::GraphicsFormat::Rg8Snorm;
    case VK_FORMAT_R8G8B8A8_UNORM:
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32: return execution::GraphicsFormat::Rgba8;
    case VK_FORMAT_R8G8B8A8_SNORM:
    case VK_FORMAT_A8B8G8R8_SNORM_PACK32: return execution::GraphicsFormat::Rgba8Snorm;
    case VK_FORMAT_R8G8B8A8_SRGB:
    case VK_FORMAT_A8B8G8R8_SRGB_PACK32: return execution::GraphicsFormat::Rgba8Srgb;
    case VK_FORMAT_B8G8R8A8_UNORM: return execution::GraphicsFormat::Bgra8Unorm;
    case VK_FORMAT_B8G8R8A8_SRGB: return execution::GraphicsFormat::Bgra8Srgb;
    case VK_FORMAT_B4G4R4A4_UNORM_PACK16: return execution::GraphicsFormat::B4G4R4A4Unorm;
    case VK_FORMAT_R5G6B5_UNORM_PACK16: return execution::GraphicsFormat::R5G6B5Unorm;
    case VK_FORMAT_A1R5G5B5_UNORM_PACK16: return execution::GraphicsFormat::A1R5G5B5Unorm;
    case VK_FORMAT_A2B10G10R10_UNORM_PACK32: return execution::GraphicsFormat::Rgb10A2;
    case VK_FORMAT_R16_SFLOAT: return execution::GraphicsFormat::R16f;
    case VK_FORMAT_R16G16_SFLOAT: return execution::GraphicsFormat::Rg16f;
    case VK_FORMAT_R16G16B16A16_SFLOAT: return execution::GraphicsFormat::Rgba16f;
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32: return execution::GraphicsFormat::R11fG11fB10f;
    case VK_FORMAT_E5B9G9R9_UFLOAT_PACK32: return execution::GraphicsFormat::E5B9G9R9Ufloat;
    case VK_FORMAT_D16_UNORM: return execution::GraphicsFormat::D16Unorm;
    case VK_FORMAT_D32_SFLOAT: return execution::GraphicsFormat::D32Sfloat;
    case VK_FORMAT_S8_UINT: return execution::GraphicsFormat::S8Uint;
    case VK_FORMAT_D32_SFLOAT_S8_UINT: return aspects == VK_IMAGE_ASPECT_STENCIL_BIT ? execution::GraphicsFormat::D32SfloatS8Stencil : execution::GraphicsFormat::D32SfloatS8Depth;
    case VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK: return execution::GraphicsFormat::Etc2R8g8b8UnormBlock;
    case VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK: return execution::GraphicsFormat::Etc2R8g8b8SrgbBlock;
    case VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK: return execution::GraphicsFormat::Etc2R8g8b8a1UnormBlock;
    case VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK: return execution::GraphicsFormat::Etc2R8g8b8a1SrgbBlock;
    case VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK: return execution::GraphicsFormat::Etc2R8g8b8a8UnormBlock;
    case VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK: return execution::GraphicsFormat::Etc2R8g8b8a8SrgbBlock;
    case VK_FORMAT_EAC_R11_UNORM_BLOCK: return execution::GraphicsFormat::EacR11UnormBlock;
    case VK_FORMAT_EAC_R11_SNORM_BLOCK: return execution::GraphicsFormat::EacR11SnormBlock;
    case VK_FORMAT_EAC_R11G11_UNORM_BLOCK: return execution::GraphicsFormat::EacR11g11UnormBlock;
    case VK_FORMAT_EAC_R11G11_SNORM_BLOCK: return execution::GraphicsFormat::EacR11g11SnormBlock;
    default: return std::nullopt;
  }
}
inline uint32_t texelSize(VkFormat format) {
  switch (format) {
    case VK_FORMAT_R8_UINT:
    case VK_FORMAT_R8_SINT:
    case VK_FORMAT_R8_UNORM:
    case VK_FORMAT_R8_SNORM:
    case VK_FORMAT_S8_UINT: return 1;
    case VK_FORMAT_R32G32B32A32_SFLOAT:
    case VK_FORMAT_R32G32B32A32_UINT:
    case VK_FORMAT_R32G32B32A32_SINT:
    case VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK:
    case VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK:
    case VK_FORMAT_EAC_R11G11_UNORM_BLOCK:
    case VK_FORMAT_EAC_R11G11_SNORM_BLOCK: return 16;
    case VK_FORMAT_R8G8_UINT:
    case VK_FORMAT_R8G8_SINT:
    case VK_FORMAT_R16_UINT:
    case VK_FORMAT_R16_SINT:
    case VK_FORMAT_R8G8_UNORM:
    case VK_FORMAT_R8G8_SNORM:
    case VK_FORMAT_B4G4R4A4_UNORM_PACK16:
    case VK_FORMAT_R5G6B5_UNORM_PACK16:
    case VK_FORMAT_A1R5G5B5_UNORM_PACK16:
    case VK_FORMAT_R16_SFLOAT:
    case VK_FORMAT_D16_UNORM: return 2;
    case VK_FORMAT_R32_SFLOAT:
    case VK_FORMAT_A2B10G10R10_UINT_PACK32:
    case VK_FORMAT_A8B8G8R8_UINT_PACK32:
    case VK_FORMAT_R8G8B8A8_UINT:
    case VK_FORMAT_A8B8G8R8_SINT_PACK32:
    case VK_FORMAT_R8G8B8A8_SINT:
    case VK_FORMAT_R16G16_UINT:
    case VK_FORMAT_R16G16_SINT:
    case VK_FORMAT_R32_UINT:
    case VK_FORMAT_R32_SINT:
    case VK_FORMAT_R8G8B8A8_UNORM:
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32:
    case VK_FORMAT_R8G8B8A8_SNORM:
    case VK_FORMAT_A8B8G8R8_SNORM_PACK32:
    case VK_FORMAT_R8G8B8A8_SRGB:
    case VK_FORMAT_A8B8G8R8_SRGB_PACK32:
    case VK_FORMAT_B8G8R8A8_UNORM:
    case VK_FORMAT_B8G8R8A8_SRGB:
    case VK_FORMAT_A2B10G10R10_UNORM_PACK32:
    case VK_FORMAT_R16G16_SFLOAT:
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32:
    case VK_FORMAT_E5B9G9R9_UFLOAT_PACK32:
    case VK_FORMAT_D32_SFLOAT: return 4;
    case VK_FORMAT_R32G32_SFLOAT:
    case VK_FORMAT_R16G16B16A16_UINT:
    case VK_FORMAT_R16G16B16A16_SINT:
    case VK_FORMAT_R32G32_UINT:
    case VK_FORMAT_R32G32_SINT:
    case VK_FORMAT_R16G16B16A16_SFLOAT:
    case VK_FORMAT_D32_SFLOAT_S8_UINT:
    case VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK:
    case VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK:
    case VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK:
    case VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK:
    case VK_FORMAT_EAC_R11_UNORM_BLOCK:
    case VK_FORMAT_EAC_R11_SNORM_BLOCK: return 8;
    default: return 0;
  }
}
inline bool depthFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_D16_UNORM:
    case VK_FORMAT_D32_SFLOAT:
    case VK_FORMAT_D32_SFLOAT_S8_UINT: return true;
    default: return false;
  }
}
inline bool stencilFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_S8_UINT:
    case VK_FORMAT_D32_SFLOAT_S8_UINT: return true;
    default: return false;
  }
}
using VertexEncoding = execution::GraphicsVertexEncoding;
struct VertexFormat {
  VertexEncoding encoding;
  std::array<uint8_t, 4> widths;
  uint8_t components, bytes;
  bool bgra;
};
inline std::optional<VertexFormat> vertexFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_R8_UNORM: return VertexFormat{VertexEncoding::Unorm, {8, 8, 8, 8}, 1, 1, false};
    case VK_FORMAT_R8_SNORM: return VertexFormat{VertexEncoding::Snorm, {8, 8, 8, 8}, 1, 1, false};
    case VK_FORMAT_R8_UINT: return VertexFormat{VertexEncoding::Uint, {8, 8, 8, 8}, 1, 1, false};
    case VK_FORMAT_R8_SINT: return VertexFormat{VertexEncoding::Sint, {8, 8, 8, 8}, 1, 1, false};
    case VK_FORMAT_R8_USCALED: return VertexFormat{VertexEncoding::Uscaled, {8, 8, 8, 8}, 1, 1, false};
    case VK_FORMAT_R8_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {8, 8, 8, 8}, 1, 1, false};
    case VK_FORMAT_R8G8_UNORM: return VertexFormat{VertexEncoding::Unorm, {8, 8, 8, 8}, 2, 2, false};
    case VK_FORMAT_R8G8_SNORM: return VertexFormat{VertexEncoding::Snorm, {8, 8, 8, 8}, 2, 2, false};
    case VK_FORMAT_R8G8_UINT: return VertexFormat{VertexEncoding::Uint, {8, 8, 8, 8}, 2, 2, false};
    case VK_FORMAT_R8G8_SINT: return VertexFormat{VertexEncoding::Sint, {8, 8, 8, 8}, 2, 2, false};
    case VK_FORMAT_R8G8_USCALED: return VertexFormat{VertexEncoding::Uscaled, {8, 8, 8, 8}, 2, 2, false};
    case VK_FORMAT_R8G8_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {8, 8, 8, 8}, 2, 2, false};
    case VK_FORMAT_R8G8B8_UNORM: return VertexFormat{VertexEncoding::Unorm, {8, 8, 8, 8}, 3, 3, false};
    case VK_FORMAT_R8G8B8_SNORM: return VertexFormat{VertexEncoding::Snorm, {8, 8, 8, 8}, 3, 3, false};
    case VK_FORMAT_R8G8B8_UINT: return VertexFormat{VertexEncoding::Uint, {8, 8, 8, 8}, 3, 3, false};
    case VK_FORMAT_R8G8B8_SINT: return VertexFormat{VertexEncoding::Sint, {8, 8, 8, 8}, 3, 3, false};
    case VK_FORMAT_R8G8B8_USCALED: return VertexFormat{VertexEncoding::Uscaled, {8, 8, 8, 8}, 3, 3, false};
    case VK_FORMAT_R8G8B8_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {8, 8, 8, 8}, 3, 3, false};
    case VK_FORMAT_R8G8B8A8_UNORM: return VertexFormat{VertexEncoding::Unorm, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_R8G8B8A8_SNORM: return VertexFormat{VertexEncoding::Snorm, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_R8G8B8A8_UINT: return VertexFormat{VertexEncoding::Uint, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_R8G8B8A8_SINT: return VertexFormat{VertexEncoding::Sint, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_R8G8B8A8_USCALED: return VertexFormat{VertexEncoding::Uscaled, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_R8G8B8A8_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_R16_UNORM: return VertexFormat{VertexEncoding::Unorm, {16, 16, 16, 16}, 1, 2, false};
    case VK_FORMAT_R16_SNORM: return VertexFormat{VertexEncoding::Snorm, {16, 16, 16, 16}, 1, 2, false};
    case VK_FORMAT_R16_UINT: return VertexFormat{VertexEncoding::Uint, {16, 16, 16, 16}, 1, 2, false};
    case VK_FORMAT_R16_SINT: return VertexFormat{VertexEncoding::Sint, {16, 16, 16, 16}, 1, 2, false};
    case VK_FORMAT_R16_USCALED: return VertexFormat{VertexEncoding::Uscaled, {16, 16, 16, 16}, 1, 2, false};
    case VK_FORMAT_R16_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {16, 16, 16, 16}, 1, 2, false};
    case VK_FORMAT_R16_SFLOAT: return VertexFormat{VertexEncoding::Float, {16, 16, 16, 16}, 1, 2, false};
    case VK_FORMAT_R16G16_UNORM: return VertexFormat{VertexEncoding::Unorm, {16, 16, 16, 16}, 2, 4, false};
    case VK_FORMAT_R16G16_SNORM: return VertexFormat{VertexEncoding::Snorm, {16, 16, 16, 16}, 2, 4, false};
    case VK_FORMAT_R16G16_UINT: return VertexFormat{VertexEncoding::Uint, {16, 16, 16, 16}, 2, 4, false};
    case VK_FORMAT_R16G16_SINT: return VertexFormat{VertexEncoding::Sint, {16, 16, 16, 16}, 2, 4, false};
    case VK_FORMAT_R16G16_USCALED: return VertexFormat{VertexEncoding::Uscaled, {16, 16, 16, 16}, 2, 4, false};
    case VK_FORMAT_R16G16_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {16, 16, 16, 16}, 2, 4, false};
    case VK_FORMAT_R16G16_SFLOAT: return VertexFormat{VertexEncoding::Float, {16, 16, 16, 16}, 2, 4, false};
    case VK_FORMAT_R16G16B16_UNORM: return VertexFormat{VertexEncoding::Unorm, {16, 16, 16, 16}, 3, 6, false};
    case VK_FORMAT_R16G16B16_SNORM: return VertexFormat{VertexEncoding::Snorm, {16, 16, 16, 16}, 3, 6, false};
    case VK_FORMAT_R16G16B16_UINT: return VertexFormat{VertexEncoding::Uint, {16, 16, 16, 16}, 3, 6, false};
    case VK_FORMAT_R16G16B16_SINT: return VertexFormat{VertexEncoding::Sint, {16, 16, 16, 16}, 3, 6, false};
    case VK_FORMAT_R16G16B16_USCALED: return VertexFormat{VertexEncoding::Uscaled, {16, 16, 16, 16}, 3, 6, false};
    case VK_FORMAT_R16G16B16_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {16, 16, 16, 16}, 3, 6, false};
    case VK_FORMAT_R16G16B16_SFLOAT: return VertexFormat{VertexEncoding::Float, {16, 16, 16, 16}, 3, 6, false};
    case VK_FORMAT_R16G16B16A16_UNORM: return VertexFormat{VertexEncoding::Unorm, {16, 16, 16, 16}, 4, 8, false};
    case VK_FORMAT_R16G16B16A16_SNORM: return VertexFormat{VertexEncoding::Snorm, {16, 16, 16, 16}, 4, 8, false};
    case VK_FORMAT_R16G16B16A16_UINT: return VertexFormat{VertexEncoding::Uint, {16, 16, 16, 16}, 4, 8, false};
    case VK_FORMAT_R16G16B16A16_SINT: return VertexFormat{VertexEncoding::Sint, {16, 16, 16, 16}, 4, 8, false};
    case VK_FORMAT_R16G16B16A16_USCALED: return VertexFormat{VertexEncoding::Uscaled, {16, 16, 16, 16}, 4, 8, false};
    case VK_FORMAT_R16G16B16A16_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {16, 16, 16, 16}, 4, 8, false};
    case VK_FORMAT_R16G16B16A16_SFLOAT: return VertexFormat{VertexEncoding::Float, {16, 16, 16, 16}, 4, 8, false};
    case VK_FORMAT_R32_SFLOAT: return VertexFormat{VertexEncoding::Float, {32, 32, 32, 32}, 1, 4, false};
    case VK_FORMAT_R32_UINT: return VertexFormat{VertexEncoding::Uint, {32, 32, 32, 32}, 1, 4, false};
    case VK_FORMAT_R32_SINT: return VertexFormat{VertexEncoding::Sint, {32, 32, 32, 32}, 1, 4, false};
    case VK_FORMAT_R32G32_SFLOAT: return VertexFormat{VertexEncoding::Float, {32, 32, 32, 32}, 2, 8, false};
    case VK_FORMAT_R32G32_UINT: return VertexFormat{VertexEncoding::Uint, {32, 32, 32, 32}, 2, 8, false};
    case VK_FORMAT_R32G32_SINT: return VertexFormat{VertexEncoding::Sint, {32, 32, 32, 32}, 2, 8, false};
    case VK_FORMAT_R32G32B32_SFLOAT: return VertexFormat{VertexEncoding::Float, {32, 32, 32, 32}, 3, 12, false};
    case VK_FORMAT_R32G32B32_UINT: return VertexFormat{VertexEncoding::Uint, {32, 32, 32, 32}, 3, 12, false};
    case VK_FORMAT_R32G32B32_SINT: return VertexFormat{VertexEncoding::Sint, {32, 32, 32, 32}, 3, 12, false};
    case VK_FORMAT_R32G32B32A32_SFLOAT: return VertexFormat{VertexEncoding::Float, {32, 32, 32, 32}, 4, 16, false};
    case VK_FORMAT_R32G32B32A32_UINT: return VertexFormat{VertexEncoding::Uint, {32, 32, 32, 32}, 4, 16, false};
    case VK_FORMAT_R32G32B32A32_SINT: return VertexFormat{VertexEncoding::Sint, {32, 32, 32, 32}, 4, 16, false};
    case VK_FORMAT_B8G8R8_UNORM: return VertexFormat{VertexEncoding::Unorm, {8, 8, 8, 8}, 3, 3, true};
    case VK_FORMAT_B8G8R8_SNORM: return VertexFormat{VertexEncoding::Snorm, {8, 8, 8, 8}, 3, 3, true};
    case VK_FORMAT_B8G8R8_UINT: return VertexFormat{VertexEncoding::Uint, {8, 8, 8, 8}, 3, 3, true};
    case VK_FORMAT_B8G8R8_SINT: return VertexFormat{VertexEncoding::Sint, {8, 8, 8, 8}, 3, 3, true};
    case VK_FORMAT_B8G8R8_USCALED: return VertexFormat{VertexEncoding::Uscaled, {8, 8, 8, 8}, 3, 3, true};
    case VK_FORMAT_B8G8R8_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {8, 8, 8, 8}, 3, 3, true};
    case VK_FORMAT_B8G8R8A8_UNORM: return VertexFormat{VertexEncoding::Unorm, {8, 8, 8, 8}, 4, 4, true};
    case VK_FORMAT_B8G8R8A8_SNORM: return VertexFormat{VertexEncoding::Snorm, {8, 8, 8, 8}, 4, 4, true};
    case VK_FORMAT_B8G8R8A8_UINT: return VertexFormat{VertexEncoding::Uint, {8, 8, 8, 8}, 4, 4, true};
    case VK_FORMAT_B8G8R8A8_SINT: return VertexFormat{VertexEncoding::Sint, {8, 8, 8, 8}, 4, 4, true};
    case VK_FORMAT_B8G8R8A8_USCALED: return VertexFormat{VertexEncoding::Uscaled, {8, 8, 8, 8}, 4, 4, true};
    case VK_FORMAT_B8G8R8A8_SSCALED: return VertexFormat{VertexEncoding::Sscaled, {8, 8, 8, 8}, 4, 4, true};
    case VK_FORMAT_A2B10G10R10_UNORM_PACK32: return VertexFormat{VertexEncoding::Unorm, {10, 10, 10, 2}, 4, 4, false};
    case VK_FORMAT_A2B10G10R10_SNORM_PACK32: return VertexFormat{VertexEncoding::Snorm, {10, 10, 10, 2}, 4, 4, false};
    case VK_FORMAT_A2B10G10R10_UINT_PACK32: return VertexFormat{VertexEncoding::Uint, {10, 10, 10, 2}, 4, 4, false};
    case VK_FORMAT_A2B10G10R10_SINT_PACK32: return VertexFormat{VertexEncoding::Sint, {10, 10, 10, 2}, 4, 4, false};
    case VK_FORMAT_A2B10G10R10_USCALED_PACK32: return VertexFormat{VertexEncoding::Uscaled, {10, 10, 10, 2}, 4, 4, false};
    case VK_FORMAT_A2B10G10R10_SSCALED_PACK32: return VertexFormat{VertexEncoding::Sscaled, {10, 10, 10, 2}, 4, 4, false};
    case VK_FORMAT_A2R10G10B10_UNORM_PACK32: return VertexFormat{VertexEncoding::Unorm, {10, 10, 10, 2}, 4, 4, true};
    case VK_FORMAT_A2R10G10B10_SNORM_PACK32: return VertexFormat{VertexEncoding::Snorm, {10, 10, 10, 2}, 4, 4, true};
    case VK_FORMAT_A2R10G10B10_UINT_PACK32: return VertexFormat{VertexEncoding::Uint, {10, 10, 10, 2}, 4, 4, true};
    case VK_FORMAT_A2R10G10B10_SINT_PACK32: return VertexFormat{VertexEncoding::Sint, {10, 10, 10, 2}, 4, 4, true};
    case VK_FORMAT_A2R10G10B10_USCALED_PACK32: return VertexFormat{VertexEncoding::Uscaled, {10, 10, 10, 2}, 4, 4, true};
    case VK_FORMAT_A2R10G10B10_SSCALED_PACK32: return VertexFormat{VertexEncoding::Sscaled, {10, 10, 10, 2}, 4, 4, true};
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32: return VertexFormat{VertexEncoding::Unorm, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_A8B8G8R8_SNORM_PACK32: return VertexFormat{VertexEncoding::Snorm, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_A8B8G8R8_UINT_PACK32: return VertexFormat{VertexEncoding::Uint, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_A8B8G8R8_SINT_PACK32: return VertexFormat{VertexEncoding::Sint, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_A8B8G8R8_USCALED_PACK32: return VertexFormat{VertexEncoding::Uscaled, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_A8B8G8R8_SSCALED_PACK32: return VertexFormat{VertexEncoding::Sscaled, {8, 8, 8, 8}, 4, 4, false};
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32: return VertexFormat{VertexEncoding::Ufloat, {11, 11, 10, 0}, 3, 4, false};
    default: return std::nullopt;
  }
}
struct CompressedImageFormat {
  uint32_t compatibility;
  static uint32_t blocks(uint32_t size) { return size / 4 + (size % 4 != 0); }
};
inline std::optional<CompressedImageFormat> compressedImageFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_ETC2_R8G8B8_UNORM_BLOCK: return CompressedImageFormat{1};
    case VK_FORMAT_ETC2_R8G8B8_SRGB_BLOCK: return CompressedImageFormat{1};
    case VK_FORMAT_ETC2_R8G8B8A1_UNORM_BLOCK: return CompressedImageFormat{2};
    case VK_FORMAT_ETC2_R8G8B8A1_SRGB_BLOCK: return CompressedImageFormat{2};
    case VK_FORMAT_ETC2_R8G8B8A8_UNORM_BLOCK: return CompressedImageFormat{3};
    case VK_FORMAT_ETC2_R8G8B8A8_SRGB_BLOCK: return CompressedImageFormat{3};
    case VK_FORMAT_EAC_R11_UNORM_BLOCK: return CompressedImageFormat{4};
    case VK_FORMAT_EAC_R11_SNORM_BLOCK: return CompressedImageFormat{4};
    case VK_FORMAT_EAC_R11G11_UNORM_BLOCK: return CompressedImageFormat{5};
    case VK_FORMAT_EAC_R11G11_SNORM_BLOCK: return CompressedImageFormat{5};
    default: return std::nullopt;
  }
}
inline bool colorFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_R32_SFLOAT:
    case VK_FORMAT_R32G32_SFLOAT:
    case VK_FORMAT_R32G32B32A32_SFLOAT:
    case VK_FORMAT_A2B10G10R10_UINT_PACK32:
    case VK_FORMAT_R8_UINT:
    case VK_FORMAT_R8_SINT:
    case VK_FORMAT_R8G8_UINT:
    case VK_FORMAT_R8G8_SINT:
    case VK_FORMAT_A8B8G8R8_UINT_PACK32:
    case VK_FORMAT_R8G8B8A8_UINT:
    case VK_FORMAT_A8B8G8R8_SINT_PACK32:
    case VK_FORMAT_R8G8B8A8_SINT:
    case VK_FORMAT_R16_UINT:
    case VK_FORMAT_R16_SINT:
    case VK_FORMAT_R16G16_UINT:
    case VK_FORMAT_R16G16_SINT:
    case VK_FORMAT_R16G16B16A16_UINT:
    case VK_FORMAT_R16G16B16A16_SINT:
    case VK_FORMAT_R32_UINT:
    case VK_FORMAT_R32_SINT:
    case VK_FORMAT_R32G32_UINT:
    case VK_FORMAT_R32G32_SINT:
    case VK_FORMAT_R32G32B32A32_UINT:
    case VK_FORMAT_R32G32B32A32_SINT:
    case VK_FORMAT_R8_UNORM:
    case VK_FORMAT_R8_SNORM:
    case VK_FORMAT_R8G8_UNORM:
    case VK_FORMAT_R8G8_SNORM:
    case VK_FORMAT_R8G8B8A8_UNORM:
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32:
    case VK_FORMAT_R8G8B8A8_SNORM:
    case VK_FORMAT_A8B8G8R8_SNORM_PACK32:
    case VK_FORMAT_R8G8B8A8_SRGB:
    case VK_FORMAT_A8B8G8R8_SRGB_PACK32:
    case VK_FORMAT_B8G8R8A8_UNORM:
    case VK_FORMAT_B8G8R8A8_SRGB:
    case VK_FORMAT_B4G4R4A4_UNORM_PACK16:
    case VK_FORMAT_R5G6B5_UNORM_PACK16:
    case VK_FORMAT_A1R5G5B5_UNORM_PACK16:
    case VK_FORMAT_A2B10G10R10_UNORM_PACK32:
    case VK_FORMAT_R16_SFLOAT:
    case VK_FORMAT_R16G16_SFLOAT:
    case VK_FORMAT_R16G16B16A16_SFLOAT:
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32:
    case VK_FORMAT_E5B9G9R9_UFLOAT_PACK32: return true;
    default: return false;
  }
}
inline bool integerColorFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_A2B10G10R10_UINT_PACK32:
    case VK_FORMAT_R8_UINT:
    case VK_FORMAT_R8_SINT:
    case VK_FORMAT_R8G8_UINT:
    case VK_FORMAT_R8G8_SINT:
    case VK_FORMAT_A8B8G8R8_UINT_PACK32:
    case VK_FORMAT_R8G8B8A8_UINT:
    case VK_FORMAT_A8B8G8R8_SINT_PACK32:
    case VK_FORMAT_R8G8B8A8_SINT:
    case VK_FORMAT_R16_UINT:
    case VK_FORMAT_R16_SINT:
    case VK_FORMAT_R16G16_UINT:
    case VK_FORMAT_R16G16_SINT:
    case VK_FORMAT_R16G16B16A16_UINT:
    case VK_FORMAT_R16G16B16A16_SINT:
    case VK_FORMAT_R32_UINT:
    case VK_FORMAT_R32_SINT:
    case VK_FORMAT_R32G32_UINT:
    case VK_FORMAT_R32G32_SINT:
    case VK_FORMAT_R32G32B32A32_UINT:
    case VK_FORMAT_R32G32B32A32_SINT: return true;
    default: return false;
  }
}
inline bool colorAttachmentFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_R32_SFLOAT:
    case VK_FORMAT_R32G32_SFLOAT:
    case VK_FORMAT_R32G32B32A32_SFLOAT:
    case VK_FORMAT_A2B10G10R10_UINT_PACK32:
    case VK_FORMAT_R8_UINT:
    case VK_FORMAT_R8_SINT:
    case VK_FORMAT_R8G8_UINT:
    case VK_FORMAT_R8G8_SINT:
    case VK_FORMAT_A8B8G8R8_UINT_PACK32:
    case VK_FORMAT_R8G8B8A8_UINT:
    case VK_FORMAT_A8B8G8R8_SINT_PACK32:
    case VK_FORMAT_R8G8B8A8_SINT:
    case VK_FORMAT_R16_UINT:
    case VK_FORMAT_R16_SINT:
    case VK_FORMAT_R16G16_UINT:
    case VK_FORMAT_R16G16_SINT:
    case VK_FORMAT_R16G16B16A16_UINT:
    case VK_FORMAT_R16G16B16A16_SINT:
    case VK_FORMAT_R32_UINT:
    case VK_FORMAT_R32_SINT:
    case VK_FORMAT_R32G32_UINT:
    case VK_FORMAT_R32G32_SINT:
    case VK_FORMAT_R32G32B32A32_UINT:
    case VK_FORMAT_R32G32B32A32_SINT:
    case VK_FORMAT_R8_UNORM:
    case VK_FORMAT_R8_SNORM:
    case VK_FORMAT_R8G8_UNORM:
    case VK_FORMAT_R8G8_SNORM:
    case VK_FORMAT_R8G8B8A8_UNORM:
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32:
    case VK_FORMAT_R8G8B8A8_SNORM:
    case VK_FORMAT_A8B8G8R8_SNORM_PACK32:
    case VK_FORMAT_R8G8B8A8_SRGB:
    case VK_FORMAT_A8B8G8R8_SRGB_PACK32:
    case VK_FORMAT_B8G8R8A8_UNORM:
    case VK_FORMAT_B8G8R8A8_SRGB:
    case VK_FORMAT_B4G4R4A4_UNORM_PACK16:
    case VK_FORMAT_R5G6B5_UNORM_PACK16:
    case VK_FORMAT_A1R5G5B5_UNORM_PACK16:
    case VK_FORMAT_A2B10G10R10_UNORM_PACK32:
    case VK_FORMAT_R16_SFLOAT:
    case VK_FORMAT_R16G16_SFLOAT:
    case VK_FORMAT_R16G16B16A16_SFLOAT:
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32: return true;
    default: return false;
  }
}
inline bool blendColorFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_R32_SFLOAT:
    case VK_FORMAT_R32G32_SFLOAT:
    case VK_FORMAT_R32G32B32A32_SFLOAT:
    case VK_FORMAT_R8_UNORM:
    case VK_FORMAT_R8G8_UNORM:
    case VK_FORMAT_R8G8B8A8_UNORM:
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32:
    case VK_FORMAT_R8G8B8A8_SRGB:
    case VK_FORMAT_A8B8G8R8_SRGB_PACK32:
    case VK_FORMAT_B8G8R8A8_UNORM:
    case VK_FORMAT_B8G8R8A8_SRGB:
    case VK_FORMAT_B4G4R4A4_UNORM_PACK16:
    case VK_FORMAT_R5G6B5_UNORM_PACK16:
    case VK_FORMAT_A1R5G5B5_UNORM_PACK16:
    case VK_FORMAT_A2B10G10R10_UNORM_PACK32:
    case VK_FORMAT_R16_SFLOAT:
    case VK_FORMAT_R16G16_SFLOAT:
    case VK_FORMAT_R16G16B16A16_SFLOAT:
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32: return true;
    default: return false;
  }
}
inline bool storageColorFormat(VkFormat format) {
  switch (format) {
    case VK_FORMAT_R32_SFLOAT:
    case VK_FORMAT_R32G32_SFLOAT:
    case VK_FORMAT_R32G32B32A32_SFLOAT:
    case VK_FORMAT_A2B10G10R10_UINT_PACK32:
    case VK_FORMAT_R8_UINT:
    case VK_FORMAT_R8_SINT:
    case VK_FORMAT_R8G8_UINT:
    case VK_FORMAT_R8G8_SINT:
    case VK_FORMAT_A8B8G8R8_UINT_PACK32:
    case VK_FORMAT_R8G8B8A8_UINT:
    case VK_FORMAT_A8B8G8R8_SINT_PACK32:
    case VK_FORMAT_R8G8B8A8_SINT:
    case VK_FORMAT_R16_UINT:
    case VK_FORMAT_R16_SINT:
    case VK_FORMAT_R16G16_UINT:
    case VK_FORMAT_R16G16_SINT:
    case VK_FORMAT_R16G16B16A16_UINT:
    case VK_FORMAT_R16G16B16A16_SINT:
    case VK_FORMAT_R32_UINT:
    case VK_FORMAT_R32_SINT:
    case VK_FORMAT_R32G32_UINT:
    case VK_FORMAT_R32G32_SINT:
    case VK_FORMAT_R32G32B32A32_UINT:
    case VK_FORMAT_R32G32B32A32_SINT:
    case VK_FORMAT_R8_UNORM:
    case VK_FORMAT_R8_SNORM:
    case VK_FORMAT_R8G8_UNORM:
    case VK_FORMAT_R8G8_SNORM:
    case VK_FORMAT_R8G8B8A8_UNORM:
    case VK_FORMAT_A8B8G8R8_UNORM_PACK32:
    case VK_FORMAT_R8G8B8A8_SNORM:
    case VK_FORMAT_A8B8G8R8_SNORM_PACK32:
    case VK_FORMAT_A2B10G10R10_UNORM_PACK32:
    case VK_FORMAT_R16_SFLOAT:
    case VK_FORMAT_R16G16_SFLOAT:
    case VK_FORMAT_R16G16B16A16_SFLOAT:
    case VK_FORMAT_B10G11R11_UFLOAT_PACK32: return true;
    default: return false;
  }
}
} // namespace gemstone::vulkan
