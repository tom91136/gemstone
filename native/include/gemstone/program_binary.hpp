#pragma once

#include <cstdint>
#include <cstring>
#include <limits>
#include <optional>
#include <string>
#include <vector>

#include <gemstone/bytes.hpp>

namespace gemstone {

// The OpenCL program binary, also a Level Zero native module: oneMKL builds through OpenCL and loads the result in Level Zero.
inline constexpr uint64_t ProgramBinaryMagic = uint64_t{0x47454d4f434c4231};
inline constexpr size_t ProgramBinaryHeaderSize = 32;

struct ProgramBinary {
  uint32_t type;
  std::string source;
  std::vector<uint8_t> payload;
};

[[nodiscard]] inline std::vector<uint8_t> encodeProgramBinary(uint32_t type, const std::string &source,
                                                              const std::vector<uint8_t> &payload) {
  if (source.size() > std::numeric_limits<uint64_t>::max() - ProgramBinaryHeaderSize
      || payload.size() > std::numeric_limits<uint64_t>::max() - ProgramBinaryHeaderSize - source.size())
    return {};
  std::vector<uint8_t> result(ProgramBinaryHeaderSize + source.size() + payload.size());
  writeNative(result.data(), 0, ProgramBinaryMagic);
  writeNative(result.data(), 8, type);
  writeNative(result.data(), 16, uint64_t{source.size()});
  writeNative(result.data(), 24, uint64_t{payload.size()});
  if (!source.empty()) std::memcpy(result.data() + ProgramBinaryHeaderSize, source.data(), source.size());
  if (!payload.empty()) std::memcpy(result.data() + ProgramBinaryHeaderSize + source.size(), payload.data(), payload.size());
  return result;
}

[[nodiscard]] inline std::optional<ProgramBinary> decodeProgramBinary(const uint8_t *bytes, size_t size) {
  if (!bytes || size < ProgramBinaryHeaderSize || readNative<uint64_t>(bytes) != ProgramBinaryMagic) return std::nullopt;
  const auto sourceSize = readNative<uint64_t>(bytes, 16);
  const auto payloadSize = readNative<uint64_t>(bytes, 24);
  if (sourceSize > size - ProgramBinaryHeaderSize || payloadSize != size - ProgramBinaryHeaderSize - sourceSize) return std::nullopt;
  const auto *source = bytes + ProgramBinaryHeaderSize;
  const auto *payload = source + sourceSize;
  return ProgramBinary{.type = readNative<uint32_t>(bytes, 8),
                       .source = std::string(reinterpret_cast<const char *>(source), static_cast<size_t>(sourceSize)),
                       .payload = std::vector<uint8_t>(payload, payload + payloadSize)};
}

} // namespace gemstone
