/* Generated from Gemstone TextureDescriptorABI. Regenerate with: sbt codegen */
#pragma once

#include <cstdint>

namespace gemstone::texture::Layout {
namespace Table {
inline constexpr uint64_t BaseAddress = 0x3f0000000000ull;
inline constexpr uint64_t EntryBytes = 0x40ull;
inline constexpr uint64_t Entries = 0x10000ull;
} // namespace Table

namespace Field {
inline constexpr uint32_t Base = 0u;
inline constexpr uint32_t Pitch = 8u;
inline constexpr uint32_t SlicePitch = 16u;
inline constexpr uint32_t Width = 24u;
inline constexpr uint32_t Height = 28u;
inline constexpr uint32_t Depth = 32u;
inline constexpr uint32_t Object = 36u;
inline constexpr uint32_t Dimensions = 37u;
inline constexpr uint32_t ChannelKind = 38u;
inline constexpr uint32_t Flags = 39u;
inline constexpr uint32_t BitsX = 40u;
inline constexpr uint32_t BitsY = 41u;
inline constexpr uint32_t BitsZ = 42u;
inline constexpr uint32_t BitsW = 43u;
inline constexpr uint32_t AddressX = 44u;
inline constexpr uint32_t AddressY = 45u;
inline constexpr uint32_t AddressZ = 46u;
inline constexpr uint32_t Filter = 47u;
inline constexpr uint32_t BorderX = 48u;
inline constexpr uint32_t BorderY = 52u;
inline constexpr uint32_t BorderZ = 56u;
inline constexpr uint32_t BorderW = 60u;
} // namespace Field

namespace Flag {
inline constexpr uint32_t NormalisedCoordinates = 1u;
inline constexpr uint32_t Srgb = 2u;
inline constexpr uint32_t NormalisedRead = 4u;
inline constexpr uint32_t ResourceShift = 3u;
inline constexpr uint32_t ResourceMask = 3u;
} // namespace Flag

namespace KeplerTable {
inline constexpr uint64_t BaseAddress = 0x7e00000000ull;
} // namespace KeplerTable

namespace Object {
inline constexpr uint32_t Free = 0u;
inline constexpr uint32_t Texture = 1u;
inline constexpr uint32_t Surface = 2u;
} // namespace Object

namespace Channel {
inline constexpr uint32_t Signed = 0u;
inline constexpr uint32_t Unsigned = 1u;
inline constexpr uint32_t Float = 2u;
inline constexpr uint32_t UnsignedNormalised = 3u;
inline constexpr uint32_t SignedNormalised = 4u;
} // namespace Channel

namespace Address {
inline constexpr uint32_t Wrap = 0u;
inline constexpr uint32_t Clamp = 1u;
inline constexpr uint32_t Mirror = 2u;
inline constexpr uint32_t Border = 3u;
} // namespace Address

namespace Filter {
inline constexpr uint32_t Point = 0u;
inline constexpr uint32_t Linear = 1u;
} // namespace Filter

namespace Resource {
inline constexpr uint32_t Array = 0u;
inline constexpr uint32_t MipmappedArray = 1u;
inline constexpr uint32_t Linear = 2u;
inline constexpr uint32_t Pitch2D = 3u;
} // namespace Resource

namespace Kepler {
inline constexpr uint32_t Base = 0u;
inline constexpr uint32_t Format = 4u;
inline constexpr uint32_t LimitX = 8u;
inline constexpr uint32_t Pitch = 12u;
inline constexpr uint32_t LimitY = 16u;
inline constexpr uint32_t LimitZ = 24u;
inline constexpr uint32_t Height = 28u;
inline constexpr uint32_t Bytes = 32u;
inline constexpr uint32_t Alignment = 256u;
inline constexpr uint32_t MaxPitch = 65280u;
inline constexpr uint32_t MaxRows = 65535u;
inline constexpr uint32_t MaxBytes = 65536u;
} // namespace Kepler
} // namespace gemstone::texture::Layout
