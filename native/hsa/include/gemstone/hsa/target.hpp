#pragma once

#include <optional>
#include <string>
#include <string_view>

#include <gemstone/environment.hpp>
#include <gemstone_amdgpu_target.hpp>

#include <environment_keys.hpp>

namespace gemstone::hsa {

inline constexpr char DefaultTarget[] = "gfx900";

/** The target presented by the virtual HSA/ HIP providers. */
inline const char *targetArch() noexcept {
  if (const char *value = gemstone::environment::value(gemstone::environment::TargetArch)) return value;
  // preset targets are string literals, so data() is NUL-terminated
  if (const auto device = gemstone::environment::selectedDevicePreset()) return device->target.data();
  return DefaultTarget;
}

/** The selected device preset when it is the card of `target`. */
inline std::optional<DevicePreset> targetPreset(std::string_view target) {
  const auto device = gemstone::environment::selectedDevicePreset();
  if (device && device->target == target) return device;
  return std::nullopt;
}

struct TargetVersion {
  unsigned major = 0;
  unsigned minor = 0;
};

/**
 * Return the display version used by HIP's major/minor fields and hsakmt's
 * engine version. This is intentionally a small presentation helper; code
 * object compatibility is determined by the ELF target and the ISA decoder.
 */
inline TargetVersion targetVersion(const char *target) noexcept {
  if (target == nullptr || target[0] != 'g' || target[1] != 'f' || target[2] != 'x' || target[3] < '0' || target[3] > '9') return {};

  const unsigned first = static_cast<unsigned>(target[3] - '0');
  if ((first == 1U || first == 2U) && target[4] >= '0' && target[4] <= '9') {
    return TargetVersion{.major = (first * 10U) + static_cast<unsigned>(target[4] - '0'),
                         .minor = target[5] >= '0' && target[5] <= '9' ? static_cast<unsigned>(target[5] - '0') : 0U};
  }
  return TargetVersion{.major = first, .minor = target[4] >= '0' && target[4] <= '9' ? static_cast<unsigned>(target[4] - '0') : 0U};
}

inline unsigned waveSize() noexcept { return gemstone::amdgpu::defaultWaveSize(targetArch()); }

inline TargetVersion targetVersion() noexcept { return targetVersion(targetArch()); }

inline std::optional<std::string> invalidEnvironment() {
  if (auto problem = gemstone::environment::invalid()) return problem;
  if (targetVersion(targetArch()).major != 0) return std::nullopt;
  if (const char *value = gemstone::environment::value(gemstone::environment::TargetArch))
    return gemstone::environment::rejected(gemstone::environment::TargetArch, value, "a gfxNNN target");
  return gemstone::environment::rejected(gemstone::environment::Device, gemstone::environment::value(gemstone::environment::Device),
                                         "a device preset with a gfxNNN target");
}

} // namespace gemstone::hsa
