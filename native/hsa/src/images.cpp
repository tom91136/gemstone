#include "gemstone/hsa/images.hpp"

#include <algorithm>
#include <array>
#include <cmath>
#include <cstdint>
#include <cstring>
#include <optional>
#include <string_view>

namespace gemstone::hsa::images {

namespace {

constexpr uint32_t SelectZero = 0, SelectOne = 1, SelectX = 4, SelectY = 5, SelectZ = 6, SelectW = 7;

/** Destination selects that read missing channels as zero and a missing w as one. */
uint32_t selects(uint32_t channels) {
  const uint32_t y = channels >= 2 ? SelectY : SelectZero;
  const uint32_t z = channels >= 4 ? SelectZ : SelectZero;
  const uint32_t w = channels >= 4 ? SelectW : SelectOne;
  return SelectX | (y << 3) | (z << 6) | (w << 9);
}

bool wide(Layout layout) { return layout == Layout::X32 || layout == Layout::X32Y32 || layout == Layout::X32Y32Z32W32; }

uint32_t legacyData(Layout layout) {
  switch (layout) {
    case Layout::X8: return 1;
    case Layout::X16: return 2;
    case Layout::X8Y8: return 3;
    case Layout::X32: return 4;
    case Layout::X16Y16: return 5;
    case Layout::X8Y8Z8W8: return 10;
    case Layout::X32Y32: return 11;
    case Layout::X16Y16Z16W16: return 12;
    case Layout::X32Y32Z32W32: return 14;
  }
  return 0;
}

/** The unified format of GFX10, or of GFX11 and GFX12 when `eleven`. */
uint32_t unified(const Format &format, bool eleven) {
  uint32_t first = 0;
  switch (format.layout) {
    case Layout::X8: first = 1; break;
    case Layout::X16: first = 7; break;
    case Layout::X8Y8: first = 14; break;
    case Layout::X32: first = 20; break;
    case Layout::X16Y16: first = 23; break;
    case Layout::X8Y8Z8W8: first = eleven ? 42 : 56; break;
    case Layout::X32Y32: first = eleven ? 48 : 62; break;
    case Layout::X16Y16Z16W16: first = eleven ? 51 : 65; break;
    case Layout::X32Y32Z32W32: first = eleven ? 61 : 75; break;
  }
  if (wide(format.layout)) {
    switch (format.number) {
      case Number::Sint: return first + 1;
      case Number::Float: return first + 2;
      default: return first;
    }
  }
  // the integer kinds in order, then float after the scaled and integer kinds
  return first + (format.number == Number::Float ? 6 : static_cast<uint32_t>(format.number));
}

uint32_t typeOf(Number number, uint32_t bits) {
  switch (number) {
    case Number::Snorm: return bits == 8 ? 0 : 1;
    case Number::Unorm: return bits == 8 ? 2 : 3;
    case Number::Sint:
      if (bits == 8) return 8;
      return bits == 16 ? 9 : 10;
    case Number::Uint:
      if (bits == 8) return 11;
      return bits == 16 ? 12 : 13;
    case Number::Float: return bits == 16 ? 14 : 15;
  }
  return 0;
}

uint32_t channelCount(const Channels &desc) {
  if (desc.w != 0) return 4;
  if (desc.z != 0) return 3;
  return desc.y != 0 ? 2 : 1;
}

template <typename T> T byChannels(uint32_t channels, T one, T two, T four) {
  if (channels == 1) return one;
  return channels == 2 ? two : four;
}

int orderChannels(uint32_t channelOrder) {
  switch (channelOrder) {
    case 1: return 1;
    case 3: return 2;
    case 8: return 4;
    default: return 0;
  }
}

} // namespace

Generation generation(std::string_view target) {
  if (target.size() < 5 || !target.starts_with("gfx")) return Generation::GFX9;
  if (target[3] == '8') return Generation::GFX8;
  if (target[3] == '9') return Generation::GFX9;
  if (target.substr(3, 2) == "10") return target.size() > 5 && target[5] >= '3' ? Generation::GFX103 : Generation::GFX10;
  if (target.substr(3, 2) == "11") return Generation::GFX11;
  return Generation::GFX12;
}

std::optional<Format> format(const Channels &desc, bool normalised) {
  const int bits = desc.x;
  const uint32_t channels = channelCount(desc);
  if ((desc.y != 0 && desc.y != bits) || (desc.z != 0 && desc.z != bits) || (desc.w != 0 && desc.w != bits) || channels == 3)
    return std::nullopt;
  Number number{};
  switch (desc.kind) {
    case Kind::Signed: number = normalised && bits != 32 ? Number::Snorm : Number::Sint; break;
    case Kind::Unsigned: number = normalised && bits != 32 ? Number::Unorm : Number::Uint; break;
    case Kind::Float:
      if (bits != 16 && bits != 32) return std::nullopt;
      number = Number::Float;
      break;
  }
  Layout layout{};
  switch (bits) {
    case 8: layout = byChannels(channels, Layout::X8, Layout::X8Y8, Layout::X8Y8Z8W8); break;
    case 16: layout = byChannels(channels, Layout::X16, Layout::X16Y16, Layout::X16Y16Z16W16); break;
    case 32: layout = byChannels(channels, Layout::X32, Layout::X32Y32, Layout::X32Y32Z32W32); break;
    default: return std::nullopt;
  }
  if (number == Number::Float && bits == 8) return std::nullopt;
  const uint32_t order = byChannels(channels, 1U, 3U, 8U);
  return Format{.layout = layout,
                .number = number,
                .bytes = static_cast<uint32_t>(bits) / 8 * channels,
                .channels = channels,
                .channelType = typeOf(number, static_cast<uint32_t>(bits)),
                .channelOrder = order};
}

std::optional<Format> format(uint32_t channelType, uint32_t channelOrder) {
  struct Type {
    int bits;
    Kind kind;
    bool normalised;
  };
  std::optional<Type> type;
  switch (channelType) {
    case 0: type = Type{.bits = 8, .kind = Kind::Signed, .normalised = true}; break;
    case 1: type = Type{.bits = 16, .kind = Kind::Signed, .normalised = true}; break;
    case 2: type = Type{.bits = 8, .kind = Kind::Unsigned, .normalised = true}; break;
    case 3: type = Type{.bits = 16, .kind = Kind::Unsigned, .normalised = true}; break;
    case 8: type = Type{.bits = 8, .kind = Kind::Signed, .normalised = false}; break;
    case 9: type = Type{.bits = 16, .kind = Kind::Signed, .normalised = false}; break;
    case 10: type = Type{.bits = 32, .kind = Kind::Signed, .normalised = false}; break;
    case 11: type = Type{.bits = 8, .kind = Kind::Unsigned, .normalised = false}; break;
    case 12: type = Type{.bits = 16, .kind = Kind::Unsigned, .normalised = false}; break;
    case 13: type = Type{.bits = 32, .kind = Kind::Unsigned, .normalised = false}; break;
    case 14: type = Type{.bits = 16, .kind = Kind::Float, .normalised = false}; break;
    case 15: type = Type{.bits = 32, .kind = Kind::Float, .normalised = false}; break;
    default: return std::nullopt;
  }
  const int channels = orderChannels(channelOrder);
  if (channels == 0) return std::nullopt;
  const int bits = type->bits;
  return format(
      Channels{.x = bits, .y = channels >= 2 ? bits : 0, .z = channels >= 4 ? bits : 0, .w = channels >= 4 ? bits : 0, .kind = type->kind},
      type->normalised);
}

namespace {

uint32_t bitsOf(float value) {
  uint32_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  return bits;
}

float floatOf(uint32_t bits) {
  float value = 0;
  std::memcpy(&value, &bits, sizeof(value));
  return value;
}

/** `value` in binary16, rounded to nearest even. */
uint16_t half(float value) {
  const uint32_t bits = bitsOf(value);
  const uint32_t sign = (bits >> 16) & 0x8000U;
  const uint32_t magnitude = bits & 0x7fffffffU;
  if (magnitude > 0x7f800000U) return static_cast<uint16_t>(sign | 0x7e00U);
  if (magnitude >= 0x47800000U) return static_cast<uint16_t>(sign | 0x7c00U);
  if (magnitude < 0x38800000U) {
    // subnormal halves count units of 2^-24
    const float scaled = std::nearbyint(std::fabs(value) * 16777216.0F);
    return static_cast<uint16_t>(sign | static_cast<uint32_t>(scaled));
  }
  const uint32_t rounded = magnitude + 0xfffU + ((magnitude >> 13) & 1U);
  return static_cast<uint16_t>(sign | ((rounded - 0x38000000U) >> 13));
}

uint64_t channel(const Format &format, int bits, uint32_t component) {
  const uint64_t mask = bits == 32 ? 0xffffffffULL : (1ULL << bits) - 1;
  switch (format.number) {
    case Number::Unorm: {
      const float value = floatOf(component);
      const float clamped = std::isnan(value) ? 0.0F : std::clamp(value, 0.0F, 1.0F);
      return static_cast<uint64_t>(std::round(static_cast<double>(clamped) * static_cast<double>(mask)));
    }
    case Number::Snorm: {
      const float value = floatOf(component);
      const float clamped = std::isnan(value) ? 0.0F : std::clamp(value, -1.0F, 1.0F);
      const auto scaled = static_cast<int64_t>(std::round(static_cast<double>(clamped) * static_cast<double>(mask >> 1)));
      return static_cast<uint64_t>(scaled) & mask;
    }
    case Number::Uint: return std::min<uint64_t>(component, mask);
    case Number::Sint: {
      const int64_t largest = static_cast<int64_t>(mask >> 1);
      return static_cast<uint64_t>(std::clamp<int64_t>(static_cast<int32_t>(component), -largest - 1, largest)) & mask;
    }
    case Number::Float: return bits == 16 ? half(floatOf(component)) : component;
  }
  return 0;
}

} // namespace

std::array<uint8_t, 16> texel(const Format &format, const std::array<uint32_t, 4> &components) {
  const int bits = static_cast<int>(format.bytes * 8 / format.channels);
  std::array<uint8_t, 16> bytes{};
  for (uint32_t index = 0; index < format.channels; ++index) {
    const uint64_t value = channel(format, bits, components[index]);
    for (int byte = 0; byte < bits / 8; ++byte)
      bytes[(index * static_cast<uint32_t>(bits / 8)) + static_cast<uint32_t>(byte)] = static_cast<uint8_t>(value >> (8 * byte));
  }
  return bytes;
}

std::array<uint32_t, 8> image(Generation generation, uint64_t base, const Format &format, ImageType type, size_t width, size_t height,
                              size_t depth, size_t pitch, uint32_t levels) {
  const uint64_t address = base >> 8;
  const uint32_t w = static_cast<uint32_t>(width - 1);
  const uint32_t h = static_cast<uint32_t>(height - 1);
  const uint32_t d = static_cast<uint32_t>(depth - 1);
  const uint32_t pitchTexels = static_cast<uint32_t>((pitch / format.bytes) - 1);
  const uint32_t kind = static_cast<uint32_t>(type);
  const bool plane = type == ImageType::Line || type == ImageType::Plane;
  std::array<uint32_t, 8> words{};
  words[0] = static_cast<uint32_t>(address);
  words[1] = static_cast<uint32_t>(address >> 32) & 0xffU;
  const uint32_t last = levels > 1 ? levels - 1 : 0;
  words[3] = selects(format.channels) | (kind << 28) | (last << (generation == Generation::GFX12 ? 15 : 16));
  switch (generation) {
    case Generation::GFX8:
    case Generation::GFX9: {
      words[1] |= (legacyData(format.layout) << 20) | (static_cast<uint32_t>(format.number) << 26);
      words[2] = w | (h << 14);
      if (generation == Generation::GFX8) {
        // TILING_INDEX 8 is ARRAY_LINEAR_ALIGNED
        words[3] |= 8U << 20;
        words[4] = (type == ImageType::Volume ? d : 0U) | (pitchTexels << 13);
        words[5] = plane || type == ImageType::Volume ? 0U : d << 13;
      } else {
        words[4] = d | (pitchTexels << 13);
      }
      break;
    }
    case Generation::GFX10:
    case Generation::GFX103:
    case Generation::GFX11: {
      words[1] |= (unified(format, generation == Generation::GFX11) << 20) | ((w & 3U) << 30);
      words[2] = (w >> 2) | (h << 14);
      // RDNA2 and RDNA3 read a one- or two-dimensional linear image's pitch from DEPTH and PITCH_MSB
      words[4] = generation != Generation::GFX10 && plane ? (pitchTexels & 0x1fffU) | ((pitchTexels >> 13) & 1U) << 13 : d;
      break;
    }
    case Generation::GFX12: {
      words[1] |= (unified(format, true) << 17) | ((w & 3U) << 30);
      words[2] = (w >> 2) | (h << 14);
      words[4] = plane ? (pitchTexels & 0x3fffU) | ((pitchTexels >> 14) & 3U) << 14 : d;
      break;
    }
  }
  return words;
}

Level level(const Format &format, ImageType type, size_t width, size_t height, size_t depth, uint32_t index) {
  const bool line = type == ImageType::Line || type == ImageType::Lines;
  const bool layered = type == ImageType::Lines || type == ImageType::Planes;
  const auto at = [&](uint32_t level) {
    const size_t levelWidth = std::max<size_t>(1, width >> level);
    const size_t levelHeight = line ? 1 : std::max<size_t>(1, height >> level);
    size_t levelDepth = 1;
    if (type == ImageType::Volume) levelDepth = std::max<size_t>(1, depth >> level);
    else if (layered) levelDepth = depth;
    return Level{.width = levelWidth, .height = levelHeight, .depth = levelDepth, .pitch = rowPitch(levelWidth, format.bytes), .offset = 0};
  };
  size_t offset = 0;
  for (uint32_t previous = 0; previous < index; ++previous) {
    const Level found = at(previous);
    offset = (offset + (found.pitch * found.height * found.depth) + RowAlignment - 1) / RowAlignment * RowAlignment;
  }
  Level found = at(index);
  found.offset = offset;
  return found;
}

uint32_t levels(ImageType type, size_t width, size_t height, size_t depth) {
  size_t largest = width;
  if (type != ImageType::Line && type != ImageType::Lines) largest = std::max(largest, height);
  if (type == ImageType::Volume) largest = std::max(largest, depth);
  uint32_t count = 1;
  while (largest > 1) {
    largest >>= 1;
    ++count;
  }
  return count;
}

std::array<uint32_t, 4> buffer(Generation generation, uint64_t base, const Format &format, size_t records) {
  std::array<uint32_t, 4> words{};
  words[0] = static_cast<uint32_t>(base);
  words[1] = (static_cast<uint32_t>(base >> 32) & 0xffffU) | (format.bytes << 16);
  words[3] = selects(format.channels);
  switch (generation) {
    case Generation::GFX8:
      // GFX8 counts unswizzled records in bytes
      words[2] = static_cast<uint32_t>(records * format.bytes);
      words[3] |= (static_cast<uint32_t>(format.number) << 12) | (legacyData(format.layout) << 15);
      break;
    case Generation::GFX9:
      words[2] = static_cast<uint32_t>(records);
      words[3] |= (static_cast<uint32_t>(format.number) << 12) | (legacyData(format.layout) << 15);
      break;
    case Generation::GFX10:
    case Generation::GFX103:
      // OOB_SELECT 0 checks the index and the element; RESOURCE_LEVEL is 1 on GFX10
      words[2] = static_cast<uint32_t>(records);
      words[3] |= (unified(format, false) << 12) | (1U << 24);
      break;
    case Generation::GFX11:
    case Generation::GFX12:
      words[2] = static_cast<uint32_t>(records);
      words[3] |= unified(format, true) << 12;
      break;
  }
  return words;
}

} // namespace gemstone::hsa::images
