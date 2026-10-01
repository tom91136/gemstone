/* Generated from Gemstone ImageDescriptorABI. Regenerate with: sbt codegen */
#pragma once

#include <cstdint>

namespace gemstone::spirv::ImageDescriptor {
namespace Format {
inline constexpr uint32_t D16Unorm = 1000u;
inline constexpr uint32_t Bgra8Unorm = 1001u;
inline constexpr uint32_t D32SfloatS8Depth = 1002u;
inline constexpr uint32_t S8Uint = 1003u;
inline constexpr uint32_t D32SfloatS8Stencil = 1004u;
inline constexpr uint32_t Rgba8Srgb = 1005u;
inline constexpr uint32_t Bgra8Srgb = 1006u;
inline constexpr uint32_t B4G4R4A4Unorm = 1007u;
inline constexpr uint32_t R5G6B5Unorm = 1008u;
inline constexpr uint32_t A1R5G5B5Unorm = 1009u;
inline constexpr uint32_t E5B9G9R9Ufloat = 1010u;
inline constexpr uint32_t DecodedRgb32f = 1011u;
} // namespace Format

namespace Flags {
inline constexpr uint32_t LevelTable = 2147483648u;
inline constexpr uint32_t Volume = 1073741824u;
inline constexpr uint32_t Swizzle = 536870912u;
inline constexpr uint32_t FormatMask = 536870911u;
} // namespace Flags

namespace SamplerWord {
inline constexpr uint32_t MinFilter = 0u;
inline constexpr uint32_t MagFilter = 1u;
inline constexpr uint32_t AddressU = 2u;
inline constexpr uint32_t AddressV = 3u;
inline constexpr uint32_t Unnormalized = 4u;
inline constexpr uint32_t CompareEnable = 5u;
inline constexpr uint32_t CompareOp = 6u;
inline constexpr uint32_t BorderColor = 7u;
inline constexpr uint32_t MipLodBias = 8u;
inline constexpr uint32_t MinLod = 9u;
inline constexpr uint32_t MaxLod = 10u;
inline constexpr uint32_t MipmapMode = 11u;
inline constexpr uint32_t AddressW = 12u;
inline constexpr uint32_t Count = 13u;
} // namespace SamplerWord

namespace Layout {
inline constexpr uint32_t Width = 0u;
inline constexpr uint32_t Height = 4u;
inline constexpr uint32_t Slices = 8u;
inline constexpr uint32_t Format = 12u;
inline constexpr uint32_t Texels = 16u;
inline constexpr uint32_t TexelBytes = 24u;
inline constexpr uint32_t Levels = 32u;
inline constexpr uint32_t Samples = 36u;
inline constexpr uint32_t LevelTable = 40u;
inline constexpr uint32_t LevelBytes = 16u;
inline constexpr uint32_t LevelWidth = 0u;
inline constexpr uint32_t LevelHeight = 4u;
inline constexpr uint32_t LevelOffset = 8u;
inline constexpr uint32_t SwizzleBytes = 16u;
inline constexpr uint32_t InlineHeaderBytes = 16u;
} // namespace Layout

namespace CompareOp {
inline constexpr uint32_t Never = 0u;
inline constexpr uint32_t Less = 1u;
inline constexpr uint32_t Equal = 2u;
inline constexpr uint32_t LessOrEqual = 3u;
inline constexpr uint32_t Greater = 4u;
inline constexpr uint32_t NotEqual = 5u;
inline constexpr uint32_t GreaterOrEqual = 6u;
inline constexpr uint32_t Always = 7u;
} // namespace CompareOp

namespace Filter {
inline constexpr uint32_t Nearest = 0u;
inline constexpr uint32_t Linear = 1u;
} // namespace Filter

namespace AddressMode {
inline constexpr uint32_t Repeat = 0u;
inline constexpr uint32_t MirroredRepeat = 1u;
inline constexpr uint32_t ClampToEdge = 2u;
inline constexpr uint32_t ClampToBorder = 3u;
inline constexpr uint32_t MirrorClampToEdge = 4u;
} // namespace AddressMode

namespace MipmapMode {
inline constexpr uint32_t Nearest = 0u;
inline constexpr uint32_t Linear = 1u;
} // namespace MipmapMode

namespace BorderColor {
inline constexpr uint32_t FloatTransparentBlack = 0u;
inline constexpr uint32_t IntTransparentBlack = 1u;
inline constexpr uint32_t FloatOpaqueBlack = 2u;
inline constexpr uint32_t IntOpaqueBlack = 3u;
inline constexpr uint32_t FloatOpaqueWhite = 4u;
inline constexpr uint32_t IntOpaqueWhite = 5u;
} // namespace BorderColor

namespace Swizzle {
inline constexpr uint32_t Identity = 0u;
inline constexpr uint32_t Zero = 1u;
inline constexpr uint32_t One = 2u;
inline constexpr uint32_t R = 3u;
inline constexpr uint32_t G = 4u;
inline constexpr uint32_t B = 5u;
inline constexpr uint32_t A = 6u;
} // namespace Swizzle
} // namespace gemstone::spirv::ImageDescriptor
