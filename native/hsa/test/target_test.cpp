#include "gemstone/hsa/target.h"

#include <cstdlib>
#include <cstring>

#include <catch2/catch_test_macros.hpp>

#include "gemstone/hsa/trace.hpp"

#include <environment_keys.hpp>

TEST_CASE("HSA target and trace environment", "[hsa]") {
  unsetenv(gemstone::environment::Trace);
  REQUIRE((!gemstone::hsa::traceEnabled("api")));
  REQUIRE((setenv(gemstone::environment::Trace, "hsa.api,hsa.packets", 1) == 0));
  REQUIRE((gemstone::hsa::traceEnabled("api")));
  REQUIRE((gemstone::hsa::traceEnabled("packets")));
  REQUIRE((!gemstone::hsa::traceEnabled("memory")));
  REQUIRE((setenv(gemstone::environment::Trace, "all", 1) == 0));
  REQUIRE((gemstone::hsa::traceEnabled("loaders")));
  unsetenv(gemstone::environment::TargetArch);
  REQUIRE((std::strcmp(gemstone::hsa::targetArch(), "gfx900") == 0));
  const auto default_version = gemstone::hsa::targetVersion();
  REQUIRE((default_version.major == 9 && default_version.minor == 0));

  struct Expected {
    const char *name;
    gemstone::hsa::TargetVersion version;
  };
  constexpr Expected targets[] = {
      {"gfx600", {6, 0}},   {"gfx700", {7, 0}},   {"gfx803", {8, 0}},   {"gfx900", {9, 0}},
      {"gfx1010", {10, 1}}, {"gfx1100", {11, 0}}, {"gfx1200", {12, 0}},
  };
  for (const auto &expected : targets) {
    REQUIRE((setenv(gemstone::environment::TargetArch, expected.name, 1) == 0));
    REQUIRE((std::strcmp(gemstone::hsa::targetArch(), expected.name) == 0));
    const auto version = gemstone::hsa::targetVersion();
    REQUIRE((version.major == expected.version.major && version.minor == expected.version.minor));
  }
  REQUIRE((setenv(gemstone::environment::TargetArch, "", 1) == 0));
  REQUIRE((std::strcmp(gemstone::hsa::targetArch(), "gfx900") == 0));
}
