#include "gemstone/hsa/target.hpp"

#include <cstring>

#include <catch2/catch_test_macros.hpp>

#include <gemstone/test_support.hpp>
#include <gemstone/trace.hpp>

#include <environment_keys.hpp>

TEST_CASE("HSA target and trace environment", "[hsa]") {
  {
    const gemstone_test::Environment trace(gemstone::environment::TraceList, nullptr);
    REQUIRE((!gemstone::traceEnabled("hsa", "api")));
  }
  {
    const gemstone_test::Environment trace(gemstone::environment::TraceList, "hsa.api,hsa.packets");
    REQUIRE((gemstone::traceEnabled("hsa", "api")));
    REQUIRE((gemstone::traceEnabled("hsa", "packets")));
    REQUIRE((!gemstone::traceEnabled("hsa", "memory")));
  }
  const gemstone_test::Environment trace(gemstone::environment::TraceList, "all");
  REQUIRE((gemstone::traceEnabled("hsa", "loaders")));
  const gemstone_test::Environment defaultTarget(gemstone::environment::TargetArch, nullptr);
  REQUIRE((std::strcmp(gemstone::hsa::targetArch(), "gfx900") == 0));
  const auto defaultVersion = gemstone::hsa::targetVersion();
  REQUIRE((defaultVersion.major == 9 && defaultVersion.minor == 0));

  struct Expected {
    const char *name{};
    gemstone::hsa::TargetVersion version;
  };
  constexpr Expected targets[] = {
      {.name = "gfx600", .version = {.major = 6, .minor = 0}},   {.name = "gfx700", .version = {.major = 7, .minor = 0}},
      {.name = "gfx803", .version = {.major = 8, .minor = 0}},   {.name = "gfx900", .version = {.major = 9, .minor = 0}},
      {.name = "gfx1010", .version = {.major = 10, .minor = 1}}, {.name = "gfx1100", .version = {.major = 11, .minor = 0}},
      {.name = "gfx1200", .version = {.major = 12, .minor = 0}},
  };
  for (const auto &expected : targets) {
    const gemstone_test::Environment selected(gemstone::environment::TargetArch, expected.name);
    REQUIRE((std::strcmp(gemstone::hsa::targetArch(), expected.name) == 0));
    const auto version = gemstone::hsa::targetVersion();
    REQUIRE((version.major == expected.version.major && version.minor == expected.version.minor));
  }
  const gemstone_test::Environment emptyTarget(gemstone::environment::TargetArch, "");
  REQUIRE((std::strcmp(gemstone::hsa::targetArch(), "gfx900") == 0));
}
