/* Generated from the device presets and the Architecture table. Regenerate with: sbt codegen */
#pragma once
#include <cstdint>
#include <optional>
#include <string_view>

namespace gemstone {
struct DevicePreset {
  std::string_view name;
  std::string_view target;
  int32_t multiprocessors;
  int32_t boostMhz;
  int64_t memoryBytes;
  int32_t memoryMTps;
  int32_t busWidthBits;
  int32_t l2Bytes;
};

inline constexpr DevicePreset devicePresets[] = {
  {"GeForce GTX 1080", "sm_61", 20, 1733, 8589934592, 10000, 256, 2097152},
  {"GeForce GTX 980", "sm_52", 16, 1216, 4294967296, 7000, 256, 2097152},
  {"GeForce RTX 2080", "sm_75", 46, 1710, 8589934592, 14000, 256, 4194304},
  {"GeForce RTX 5080", "sm_120", 84, 2617, 17179869184, 30000, 256, 67108864},
  {"Quadro GV100", "sm_70", 80, 1627, 34359738368, 1696, 4096, 6291456},
  {"Radeon RX 9070 XT", "gfx1201", 64, 2970, 17179869184, 20100, 256, 8388608},
};

inline std::optional<DevicePreset> devicePreset(std::string_view name) noexcept {
  for (const auto& preset : devicePresets) {
    if (preset.name == name) return preset;
  }
  return std::nullopt;
}
} // namespace gemstone
