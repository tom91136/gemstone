#include "gemstone/hsa/target.hpp"

#include <cstdlib>
#include <cstring>

#include <catch2/catch_test_macros.hpp>

#include <gemstone/trace.hpp>

#include <environment_keys.hpp>

TEST_CASE("HSA target and trace environment", "[hsa]") {
  unsetenv(gemstone::environment::Trace);
  REQUIRE((!gemstone::traceEnabled("hsa", "api")));
  REQUIRE((setenv(gemstone::environment::Trace, "hsa.api,hsa.packets", 1) == 0));
  REQUIRE((gemstone::traceEnabled("hsa", "api")));
  REQUIRE((gemstone::traceEnabled("hsa", "packets")));
  REQUIRE((!gemstone::traceEnabled("hsa", "memory")));
  REQUIRE((setenv(gemstone::environment::Trace, "all", 1) == 0));
  REQUIRE((gemstone::traceEnabled("hsa", "loaders")));
  unsetenv(gemstone::environment::TargetArch);
  REQUIRE((std::strcmp(gemstone::hsa::targetArch(), "gfx900") == 0));
  const auto defaultVersion = gemstone::hsa::targetVersion();
  REQUIRE((defaultVersion.major == 9 && defaultVersion.minor == 0));

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
