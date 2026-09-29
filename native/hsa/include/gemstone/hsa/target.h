#pragma once

#include <gemstone/environment.hpp>

#include <environment_keys.hpp>

namespace gemstone::hsa {

inline constexpr char defaultTarget[] = "gfx900";

/** The target presented by the virtual HSA/ HIP providers. */
inline const char *targetArch() noexcept {
  const char *value = gemstone::environment::value(gemstone::environment::TargetArch);
  return value != nullptr && value[0] != '\0' ? value : defaultTarget;
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
    return TargetVersion{first * 10U + static_cast<unsigned>(target[4] - '0'),
                         target[5] >= '0' && target[5] <= '9' ? static_cast<unsigned>(target[5] - '0') : 0U};
  }
  return TargetVersion{first, target[4] >= '0' && target[4] <= '9' ? static_cast<unsigned>(target[4] - '0') : 0U};
}

inline TargetVersion targetVersion() noexcept { return targetVersion(targetArch()); }

} // namespace gemstone::hsa
