#include "conformance/profile.hpp"

#include <chrono>
#include <filesystem>
#include <fstream>
#include <string>

#include <catch2/catch_test_macros.hpp>

namespace {

class TemporaryDirectory {
public:
  TemporaryDirectory()
      : path_(std::filesystem::temp_directory_path()
              / ("conformance-profile-test-" + std::to_string(std::chrono::steady_clock::now().time_since_epoch().count()))) {
    std::filesystem::create_directories(path_);
  }

  ~TemporaryDirectory() noexcept {
    std::error_code error;
    std::filesystem::remove_all(path_, error);
  }

  const std::filesystem::path &path() const { return path_; }

private:
  std::filesystem::path path_;
};

} // namespace

TEST_CASE("machine profiles preserve target settings", "[profile]") {
  TemporaryDirectory directory;
  std::ofstream(directory.path() / "local-machine.toml") << R"(
environment = { SHARED = "global" }
skips = ["global/case"]

[[targets]]
name = "first-opencl"
backend = "opencl"
device = "*first*"
environment = { SHARED = "target", LOCAL = "yes" }
skips = ["local/case"]

[[targets]]
name = "second-opencl"
backend = "opencl"
device = "*second*"
)";

  const auto resolved = conformance::profile::resolve(directory.path(), std::nullopt, "local-machine");
  REQUIRE(resolved.name == "local-machine");

  const auto tests = conformance::profile::make_plan(resolved, {});
  REQUIRE(tests.size() == 2);
  CHECK(tests[0].environment.at("SHARED") == "target");
  CHECK(tests[0].environment.at("CONFORMANCE_DEVICE") == "*first*");
  CHECK(tests[0].environment.at("CONFORMANCE_SKIPS") == "global/case\nlocal/case");

  const auto selected = conformance::profile::make_plan(resolved, {"second-opencl"});
  REQUIRE(selected.size() == 1);
  CHECK(selected.front().id == "second-opencl");
  CHECK_FALSE(selected.front().architecture);
  CHECK(selected.front().environment.at("CONFORMANCE_DEVICE") == "*second*");
}

TEST_CASE("machine profiles do not invent a fallback target", "[profile]") {
  TemporaryDirectory directory;
  CHECK_THROWS_AS(conformance::profile::resolve(directory.path(), std::nullopt, "missing-machine"), std::invalid_argument);
}

TEST_CASE("machine profiles may select multiple architectures of one backend", "[profile]") {
  TemporaryDirectory directory;
  std::ofstream(directory.path() / "multi.toml") << R"(
[[targets]]
name = "older"
backend = "cuda"
architecture = "sm_89"

[[targets]]
name = "newer"
backend = "cuda"
architecture = "sm_90"
)";

  const auto resolved = conformance::profile::resolve(directory.path(), "multi");
  const auto targets = conformance::profile::make_plan(resolved, {});
  REQUIRE(targets.size() == 2);
  CHECK(targets[0].architecture == "sm_89");
  CHECK(targets[1].architecture == "sm_90");
}

TEST_CASE("machine profiles reject the retired host backend", "[profile]") {
  TemporaryDirectory directory;
  std::ofstream(directory.path() / "invalid.toml") << R"(
[[targets]]
backend = "host"
)";
  CHECK_THROWS_AS(conformance::profile::resolve(directory.path(), "invalid"), std::invalid_argument);
}

TEST_CASE("all shipped machine profiles are valid", "[profile]") {
  REQUIRE_NOTHROW(conformance::profile::validate_all(std::filesystem::path(CONFORMANCE_PROFILE_DIRECTORY)));
}
