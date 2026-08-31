// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/target.h"

#include <cassert>
#include <cstdlib>
#include <cstring>

int main() {
  unsetenv("GEMSTONE_AMDGPU_TARGET");
  assert(std::strcmp(gemstone::hsa::target_arch(), "gfx900") == 0);
  const auto default_version = gemstone::hsa::target_version();
  assert(default_version.major == 9 && default_version.minor == 0);

  struct Expected {
    const char *name;
    gemstone::hsa::TargetVersion version;
  };
  constexpr Expected targets[] = {
      {"gfx600", {6, 0}},   {"gfx700", {7, 0}},   {"gfx803", {8, 0}},   {"gfx900", {9, 0}},
      {"gfx1010", {10, 1}}, {"gfx1100", {11, 0}}, {"gfx1200", {12, 0}},
  };
  for (const auto &expected : targets) {
    assert(setenv("GEMSTONE_AMDGPU_TARGET", expected.name, 1) == 0);
    assert(std::strcmp(gemstone::hsa::target_arch(), expected.name) == 0);
    const auto version = gemstone::hsa::target_version();
    assert(version.major == expected.version.major && version.minor == expected.version.minor);
  }
  assert(setenv("GEMSTONE_AMDGPU_TARGET", "", 1) == 0);
  assert(std::strcmp(gemstone::hsa::target_arch(), "gfx900") == 0);
  return 0;
}
