/* Generated from the Architecture table. Regenerate with: sbt codegen */
#pragma once
#include <cstdint>
#include <optional>
#include <string_view>

namespace gemstone {
struct TargetMemory {
  int32_t busWidthBits;
  int32_t l2Bytes;
};

inline std::optional<TargetMemory> targetMemory(std::string_view target) noexcept {
  if (target == "gfx1200") return TargetMemory{.busWidthBits = 128, .l2Bytes = 4194304};
  if (target == "gfx1201") return TargetMemory{.busWidthBits = 256, .l2Bytes = 8388608};
  if (target == "sm_100") return TargetMemory{.busWidthBits = 8192, .l2Bytes = 62914560};
  if (target == "sm_120") return TargetMemory{.busWidthBits = 256, .l2Bytes = 67108864};
  if (target == "sm_35") return TargetMemory{.busWidthBits = 384, .l2Bytes = 1572864};
  if (target == "sm_52") return TargetMemory{.busWidthBits = 256, .l2Bytes = 2097152};
  if (target == "sm_61") return TargetMemory{.busWidthBits = 256, .l2Bytes = 2097152};
  if (target == "sm_70") return TargetMemory{.busWidthBits = 4096, .l2Bytes = 6291456};
  if (target == "sm_75") return TargetMemory{.busWidthBits = 256, .l2Bytes = 4194304};
  if (target == "sm_89") return TargetMemory{.busWidthBits = 128, .l2Bytes = 33554432};
  return std::nullopt;
}
} // namespace gemstone
