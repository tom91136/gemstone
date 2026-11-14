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
inline std::string targetArch() {
  if (const auto value = gemstone::environment::value(gemstone::environment::TargetArch)) return *value;
  if (const auto device = gemstone::environment::selectedDevicePreset()) return std::string(device->target);
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
inline TargetVersion targetVersion(std::string_view target) noexcept {
  if (target.size() < 4 || target[0] != 'g' || target[1] != 'f' || target[2] != 'x' || target[3] < '0' || target[3] > '9') return {};

  const unsigned first = static_cast<unsigned>(target[3] - '0');
  if ((first == 1U || first == 2U) && target.size() > 4 && target[4] >= '0' && target[4] <= '9') {
    return TargetVersion{.major = (first * 10U) + static_cast<unsigned>(target[4] - '0'),
                         .minor = target.size() > 5 && target[5] >= '0' && target[5] <= '9' ? static_cast<unsigned>(target[5] - '0') : 0U};
  }
  return TargetVersion{.major = first,
                       .minor = target.size() > 4 && target[4] >= '0' && target[4] <= '9' ? static_cast<unsigned>(target[4] - '0') : 0U};
}

inline unsigned waveSize() { return gemstone::amdgpu::defaultWaveSize(targetArch()); }

inline TargetVersion targetVersion() { return targetVersion(targetArch()); }

inline std::optional<std::string> invalidEnvironment() {
  if (auto problem = gemstone::environment::invalid()) return problem;
  if (targetVersion(targetArch()).major != 0) return std::nullopt;
  if (const auto target = gemstone::environment::value(gemstone::environment::TargetArch))
    return gemstone::environment::rejected(gemstone::environment::TargetArch, *target, "a gfxNNN target");
  if (const auto device = gemstone::environment::value(gemstone::environment::Device))
    return gemstone::environment::rejected(gemstone::environment::Device, *device, "a device preset with a gfxNNN target");
  return std::nullopt;
}

} // namespace gemstone::hsa
