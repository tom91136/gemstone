#include "profile.hpp"

#include <chrono>
#include <filesystem>
#include <fstream>
#include <string>

#include <catch2/catch_test_macros.hpp>

#include <fmt/format.h>

namespace {

class TemporaryDirectory {
public:
  TemporaryDirectory()
      : path_(std::filesystem::temp_directory_path()
              / fmt::format("conformance-profile-test-{}", std::chrono::steady_clock::now().time_since_epoch().count())) {
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
routeSkips = ["blas/*"]
skipReason = "strict PTX excludes SASS-only vendor images"

[[targets]]
name = "second-opencl"
backend = "opencl"
device = "*second*"
)";

  const auto resolved = conformance::profile::resolve_profile(directory.path(), std::nullopt, "local-machine");
  REQUIRE(resolved.name == "local-machine");

  const auto tests = conformance::profile::resolve_targets(resolved, {});
  REQUIRE(tests.size() == 2);
  CHECK(tests[0].environment.at("SHARED") == "target");
  CHECK(tests[0].environment.at("CONFORMANCE_DEVICE") == "*first*");
  CHECK(tests[0].environment.at("CONFORMANCE_SKIPS") == "global/case\nlocal/case");
  CHECK(tests[0].routeSkips == std::vector<std::string>{"blas/*"});
  CHECK(tests[0].skipReason == "strict PTX excludes SASS-only vendor images");

  const auto selected = conformance::profile::resolve_targets(resolved, {"second-opencl"});
  REQUIRE(selected.size() == 1);
  CHECK(selected.front().id == "second-opencl");
  CHECK_FALSE(selected.front().architecture);
  CHECK(selected.front().environment.at("CONFORMANCE_DEVICE") == "*second*");
}

TEST_CASE("machine profiles do not invent a fallback target", "[profile]") {
  TemporaryDirectory directory;
  CHECK_THROWS_AS(conformance::profile::resolve_profile(directory.path(), std::nullopt, "missing-machine"), std::invalid_argument);
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

  const auto resolved = conformance::profile::resolve_profile(directory.path(), "multi");
  const auto targets = conformance::profile::resolve_targets(resolved, {});
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
  CHECK_THROWS_AS(conformance::profile::resolve_profile(directory.path(), "invalid"), std::invalid_argument);
}

TEST_CASE("all shipped machine profiles are valid", "[profile]") {
  REQUIRE_NOTHROW(conformance::profile::validate_all(std::filesystem::path(CONFORMANCE_PROFILE_DIRECTORY)));
}

TEST_CASE("direct routes are explicit and contain no machine policy", "[profile]") {
  using conformance::profile::direct_targets;
  const auto targets = direct_targets({"cuda/sm_89", "hip/gfx1030", "hsa/gfx1030", "opencl", "sycl", "levelzero", "vulkan"});
  REQUIRE(targets.size() == 8);
  CHECK(targets.front().id == "cuda-ptx-sm_89");
  CHECK(targets.front().architecture == "sm_89");
  CHECK(targets.back().backend == "vulkan");
  for (const auto &target : targets) {
    if (target.backend != "cuda") CHECK(target.environment.empty());
    CHECK_FALSE(target.deviceLock);
  }
  CHECK_THROWS(direct_targets({}));
  for (const auto &route : {"all", "cuda", "cuda-ptx", "cuda-sass", "cuda-sass/gfx1030", "cuda-auto/sm_89", "hip", "hsa/sm_89",
                            "cuda/../../escape", "vulkan/gfx1030", "unknown"})
    CHECK_THROWS(direct_targets({route}));
  CHECK_THROWS(direct_targets({"vulkan", "vulkan"}));
  CHECK(direct_targets({"cuda/sm_89", "cuda/sm_80"}).size() == 4);
}

TEST_CASE("direct CUDA routes select strict PTX and SASS independently from the same artifact", "[profile]") {
  using conformance::profile::direct_targets;
  const auto targets = direct_targets({"cuda-ptx/sm_89", "cuda-sass/sm_89"});
  REQUIRE(targets.size() == 2);
  for (size_t i = 0; i < targets.size(); ++i) {
    const auto &target = targets[i];
    const auto mode = i == 0 ? "ptx" : "sass";
    CHECK(target.id == std::string("cuda-") + mode + "-sm_89");
    CHECK(target.backend == "cuda");
    CHECK(target.architecture == "sm_89");
    CHECK(target.environment.at("CONFORMANCE_CUDA_CODE") == mode);
    CHECK(target.environment.at("CUDA_FORCE_PTX_JIT") == (i == 0 ? "1" : "0"));
    CHECK(target.environment.at("CUDA_DISABLE_PTX_JIT") == (i == 0 ? "0" : "1"));
    CHECK(target.routeSkips.empty());
    CHECK_FALSE(target.deviceLock);
  }
  const auto both = direct_targets({"cuda/sm_89"});
  REQUIRE(both.size() == targets.size());
  for (size_t i = 0; i < targets.size(); ++i) {
    CHECK(both[i].id == targets[i].id);
    CHECK(both[i].environment == targets[i].environment);
  }
  CHECK_THROWS(direct_targets({"cuda/sm_89", "cuda-ptx/sm_89"}));
  CHECK_THROWS(direct_targets({"cuda-sass/sm_89", "cuda-sass/sm_89"}));
}

TEST_CASE("compiler routes preserve code modes and reject unsupported combinations", "[profile]") {
  using conformance::profile::direct_targets;
  const auto targets = direct_targets({"cuda/sm_80", "cuda/sm_80/clang", "cuda/sm_80/gcc", "hsa/gfx906/gcc"});
  REQUIRE(targets.size() == 6);
  CHECK(targets[0].compiler == "vendor");
  CHECK(targets[2].id == "cuda-ptx-sm_80-clang");
  CHECK(targets[3].id == "cuda-sass-sm_80-clang");
  CHECK(targets[2].compiler == "clang");
  CHECK(targets[3].environment.at("CONFORMANCE_CUDA_CODE") == "sass");
  CHECK(targets[4].id == "cuda-ptx-sm_80-gcc");
  CHECK(targets[4].compiler == "gcc");
  CHECK(targets[4].backend == "cuda");
  CHECK(targets[4].environment.at("CONFORMANCE_CUDA_CODE") == "ptx");
  CHECK(targets[5].backend == "hsa");
  for (const auto &route : {"cuda/sm_80/", "cuda/sm_80/../clang", "cuda/sm_80/unknown", "hip/gfx906/gcc", "hsa/gfx906/clang",
                            "cuda-sass/sm_80/gcc", "opencl//gcc"})
    CHECK_THROWS(direct_targets({route}));
  CHECK_THROWS(direct_targets({"cuda-ptx/sm_80", "cuda-ptx/sm_80/vendor"}));
  CHECK_THROWS(direct_targets({"cuda/sm_80/gcc", "cuda-ptx/sm_80/gcc"}));
}

TEST_CASE("machine compiler selection requires a compatible backend and code mode", "[profile]") {
  TemporaryDirectory directory;
  std::string compiler = "gcc", backend = "cuda", architecture = "sm_80", mode;
  bool invalid = false;
  SECTION("GCC CUDA selects PTX") {}
  SECTION("GCC supports HSA") {
    backend = "hsa";
    architecture = "gfx906";
  }
  SECTION("GCC rejects SASS execution") {
    mode = "CONFORMANCE_CUDA_CODE = 'sass'";
    invalid = true;
  }
  SECTION("blank compiler is rejected") {
    compiler.clear();
    invalid = true;
  }
  SECTION("path traversal compiler is rejected") {
    compiler = "../gcc";
    invalid = true;
  }
  SECTION("Clang HSA is not declared supported") {
    compiler = "clang";
    backend = "hsa";
    architecture = "gfx906";
    invalid = true;
  }
  std::ofstream(directory.path() / "compiler.toml") << "[[targets]]\nbackend = '" << backend << "'\narchitecture = '" << architecture
                                                    << "'\ncompiler = '" << compiler << "'\nenvironment = { " << mode << " }\n";
  const auto resolve = [&] {
    return conformance::profile::resolve_targets(conformance::profile::resolve_profile(directory.path(), "compiler"), {});
  };
  if (invalid) CHECK_THROWS(resolve());
  else {
    const auto targets = resolve();
    REQUIRE(targets.size() == 1);
    CHECK(targets[0].compiler == "gcc");
    if (backend == "cuda") {
      CHECK(targets[0].environment.at("CONFORMANCE_CUDA_CODE") == "ptx");
      CHECK(targets[0].environment.at("CUDA_FORCE_PTX_JIT") == "1");
    }
  }
}

TEST_CASE("profile selectors preserve all compilers by backend and narrow by target name", "[profile]") {
  TemporaryDirectory directory;
  std::ofstream(directory.path() / "compilers.toml") << R"(
[[targets]]
name = "cuda-vendor"
backend = "cuda"
architecture = "sm_80"
[[targets]]
name = "cuda-clang"
backend = "cuda"
architecture = "sm_80"
compiler = "clang"
[[targets]]
name = "cuda-gcc"
backend = "cuda"
architecture = "sm_80"
compiler = "gcc"
)";
  const auto profile = conformance::profile::resolve_profile(directory.path(), "compilers");
  const auto all = conformance::profile::resolve_targets(profile, {"cuda"});
  REQUIRE(all.size() == 3);
  CHECK(all[0].compiler == "vendor");
  CHECK(all[1].compiler == "clang");
  CHECK(all[2].compiler == "gcc");
  const auto selected = conformance::profile::resolve_targets(profile, {"cuda-clang"});
  REQUIRE(selected.size() == 1);
  CHECK(selected[0].compiler == "clang");
  CHECK_FALSE(selected[0].environment.contains("OMP_TARGET_OFFLOAD"));
}
