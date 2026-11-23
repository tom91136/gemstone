#pragma once

#include <charconv>
#include <cstddef>
#include <cstdint>
#include <optional>
#include <string_view>
#include <system_error>
#include <utility>

#include <gemstone/environment.hpp>

namespace gemstone::device_identity {

inline constexpr std::string_view FallbackName = "Gemstone PTX emulator";
inline constexpr int32_t FallbackMultiprocessors = 4;
inline constexpr int32_t FallbackClockRateKhz = 1500000;
inline constexpr int32_t FallbackMemoryClockRateKhz = 1500000;
inline constexpr size_t FallbackMemoryBytes = uint64_t{8} * 1024 * 1024 * 1024;

inline std::optional<int> targetVersion(std::string_view target) noexcept {
  while (!target.empty() && (target.front() == ' ' || target.front() == '\t' || target.front() == '\r')) target.remove_prefix(1);
  if (target.starts_with("compute")) {
    target.remove_prefix(sizeof("compute") - 1);
    if (target.starts_with('_')) target.remove_prefix(1);
  } else if (target.starts_with("sm")) {
    target.remove_prefix(sizeof("sm") - 1);
    if (target.starts_with('_')) target.remove_prefix(1);
  } else {
    return std::nullopt;
  }
  int version = 0;
  const auto parsed = std::from_chars(target.data(), target.data() + target.size(), version);
  return parsed.ec == std::errc{} ? std::optional{version} : std::nullopt;
}

inline std::optional<std::pair<int, int>> capability() noexcept {
  const auto target = environment::targetArch();
  const auto version = target ? targetVersion(*target) : std::nullopt;
  return version && *version >= 10 ? std::optional{std::pair{*version / 10, *version % 10}} : std::nullopt;
}

inline std::pair<int, int> computeCapability() noexcept { return capability().value_or(std::pair{8, 0}); }

inline std::string_view name() noexcept {
  if (const auto preset = environment::selectedDevicePreset()) return preset->name;
  return FallbackName;
}

struct Spec {
  int32_t multiprocessors;
  int32_t clockRateKhz;
  int32_t memoryClockRateKhz;
  size_t totalGlobalMemoryBytes;
};

inline Spec spec() noexcept {
  if (const auto preset = environment::selectedDevicePreset())
    return {.multiprocessors = preset->multiprocessors,
            .clockRateKhz = preset->boostMhz * 1000,
            .memoryClockRateKhz = preset->memoryMTps * 500,
            .totalGlobalMemoryBytes = static_cast<size_t>(preset->memoryBytes)};
  return {.multiprocessors = FallbackMultiprocessors,
          .clockRateKhz = FallbackClockRateKhz,
          .memoryClockRateKhz = FallbackMemoryClockRateKhz,
          .totalGlobalMemoryBytes = FallbackMemoryBytes};
}

} // namespace gemstone::device_identity
