#pragma once

#include <array>
#include <cstddef>
#include <cstdint>
#include <optional>
#include <string_view>

namespace gemstone::hsa::images {

/** The resource descriptor layouts, as the VM decodes them per target generation. */
enum class Generation { GFX8, GFX9, GFX10, GFX103, GFX11, GFX12 };

Generation generation(std::string_view target);

/** The texel layouts compute images and typed buffers take, by their channel widths from x. */
enum class Layout { X8, X16, X8Y8, X32, X16Y16, X8Y8Z8W8, X32Y32, X16Y16Z16W16, X32Y32Z32W32 };

/** The numeric format values GFX6-GFX9 use; the unified tables of later generations are derived from them. */
enum class Number : uint32_t { Unorm = 0, Snorm = 1, Uint = 4, Sint = 5, Float = 7 };

struct Format {
  Layout layout;
  Number number;
  uint32_t bytes;
  uint32_t channels;
  uint32_t channelType;  // hsa_ext_image_channel_type_t, which HIP's surface functions read back
  uint32_t channelOrder; // hsa_ext_image_channel_order_t
};

enum class Kind { Signed, Unsigned, Float };

/** A HIP channel format description: each channel's bits, zero where absent, and how they read. */
struct Channels {
  int x, y, z, w;
  Kind kind;
};

/** The format a channel description names, normalised integers read as floats when `normalised`. */
std::optional<Format> format(const Channels &desc, bool normalised);

/** The format an hsa_ext_image_channel_type_t and hsa_ext_image_channel_order_t name: R, RG or RGBA channels. */
std::optional<Format> format(uint32_t channelType, uint32_t channelOrder);

/**
 * The bytes of a texel of `format` holding `components`, x to w: four floats for a normalised or float format and four
 * integers for an integer one. Normalised channels round half away from zero, integers saturate and halves round to
 * nearest even.
 */
std::array<uint8_t, 16> texel(const Format &format, const std::array<uint32_t, 4> &components);

/** SQ_RSRC_IMG_* resource types. */
enum class ImageType : uint32_t { Line = 8, Plane = 9, Volume = 10, Lines = 12, Planes = 13 };

/** Rows of a linear image start 256-byte aligned, as the VM addresses them. */
constexpr size_t RowAlignment = 256;

constexpr size_t rowPitch(size_t width, uint32_t bytes) { return ((width * bytes) + RowAlignment - 1) / RowAlignment * RowAlignment; }

/**
 * The eight-dword descriptor of a linear image at `base`, which must be 256-byte aligned, `depth` its depth or its
 * layer count, `pitch` its row pitch in bytes, and `levels` its mip levels, laid out as `level` places them.
 */
std::array<uint32_t, 8> image(Generation generation, uint64_t base, const Format &format, ImageType type, size_t width, size_t height,
                              size_t depth, size_t pitch, uint32_t levels = 1);

/** Mip level `index` of a linear image: its dimensions, depth or layer count, row pitch, and byte offset from the base. */
struct Level {
  size_t width, height, depth, pitch, offset;
};

/**
 * Level `index` of a linear image of level 0 `width` by `height` by `depth` (or layers), as the VM addresses levels:
 * each level's rows 256-byte aligned, its slices one after another, and each level 256-byte aligned after the last;
 * level `levels` gives the bytes the image spans.
 */
Level level(const Format &format, ImageType type, size_t width, size_t height, size_t depth, uint32_t index);

/** The most mip levels an image of these dimensions has: down to a texel in each dimension. */
uint32_t levels(ImageType type, size_t width, size_t height, size_t depth);

/** The four-dword descriptor of `records` elements of `format` at `base`, indexed by element. */
std::array<uint32_t, 4> buffer(Generation generation, uint64_t base, const Format &format, size_t records);

} // namespace gemstone::hsa::images
