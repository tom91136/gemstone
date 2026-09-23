#include "driver.hpp"

#include <chrono>
#include <cstdlib>
#include <fstream>
#include <sstream>
#include <thread>

#include <catch2/catch_test_macros.hpp>
#include <glaze/json.hpp>

#include "conformance/device.hpp"

#include "device_lock.hpp"
#include "environment.hpp"
#if !defined(_WIN32)
  #include <csignal>

  #include <unistd.h>
#endif

namespace {
namespace fs = std::filesystem;
using namespace conformance::driver;
using namespace std::chrono_literals;
struct TemporaryDirectory {
  fs::path path =
      fs::temp_directory_path() / fmt::format("conformance-test-{}", std::chrono::steady_clock::now().time_since_epoch().count());
  TemporaryDirectory() { fs::create_directories(path); }
  ~TemporaryDirectory() {
    std::error_code error;
    fs::remove_all(path, error);
  }
};
const fs::path fixture = CONFORMANCE_FIXTURE;
const fs::path driver = CONFORMANCE_DRIVER;
std::string read(const fs::path &path) {
  std::ifstream file(path);
  return {std::istreambuf_iterator<char>(file), {}};
}
std::vector<std::string> list_fixture() {
  return catch_case_names(run_process({fixture.string(), "--list-tests", "--reporter", "json", "--order", "lex", "[fixture]"},
                                      fixture.parent_path(), base_environment(), 10s, {}, true)
                              .output,
                          "fixture");
}
} // namespace

TEST_CASE("Catch JSON inventories preserve names and reject malformed input", "[driver]") {
  const auto names = list_fixture();
  REQUIRE(names.size() == 2);
  CHECK(names[0].find(", bracket[quoted] star* backslash\\ semi; quote\" unicode-π") != std::string::npos);
  CHECK_THROWS(catch_case_names("<Name>not JSON</Name>", "bad"));
  CHECK_THROWS(catch_case_names(R"({"version":9,"listings":{"tests":[]}})", "bad"));
  CHECK_THROWS(catch_case_names(R"({"version":1,"listings":{"tests":[{"name":"same"},{"name":"same"}]}})", "duplicate"));
  CHECK(catch_case_names(R"({"version":1,"listings":{"tests":[]}})", "empty selection").empty());
}

TEST_CASE("native shards select exact names including Catch metacharacters", "[driver]") {
  const auto names = list_fixture();
  REQUIRE(names.size() == 2);
  for (uint32_t index = 0; index < names.size(); ++index) {
    auto arguments = catch_shard_arguments({"[fixture]"}, index, static_cast<uint32_t>(names.size()));
    arguments.insert(arguments.begin(), fixture.string());
    const auto result = run_process(arguments, fixture.parent_path(), base_environment(), 10s, {}, true);
    CHECK(result.status == (index == 0 ? 0 : 4));
  }
  CHECK_THROWS(catch_shard_arguments({}, 1, 1));
  CHECK_THROWS(catch_shard_arguments({}, 0, 0));
}

TEST_CASE("CTest generation groups 52000 cases by timeout", "[driver]") {
  TemporaryDirectory directory;
  TargetExecution target{"route", "opencl", "pci-0123456789abcdef0123456789abcdef", directory.path / "route.json", {}};
  for (uint32_t i = 0; i < 52000; ++i)
    target.tests.push_back({fmt::format("case/{}", i), "fixture", i % 2 ? 120U : 300U, i, 52000});
  const auto start = std::chrono::steady_clock::now();
  materialize_ctest(directory.path, driver, {target});
  const auto output = read(directory.path / "CTestTestfile.cmake");
  size_t count = 0;
  for (size_t at = 0; (at = output.find("set_tests_properties(", at)) != std::string::npos; ++at)
    ++count;
  CHECK(count == 2);
  CHECK(output.find("SKIP_RETURN_CODE 4") != std::string::npos);
  CHECK(output.find("RESOURCE_LOCK [=[pci-0123456789abcdef0123456789abcdef]=]") != std::string::npos);
  CHECK(std::chrono::steady_clock::now() - start < 10s);
  const auto listing = run_process({"ctest", "--test-dir", directory.path.string(), "--show-only=json-v1"}, directory.path,
                                   base_environment(), 30s, {}, true);
  CHECK(listing.status == 0);
  CHECK(listing.output.find("case/51999") != std::string::npos);
}

TEST_CASE("isolated routes deduplicate physical variants by artifact ABI", "[driver]") {
  using conformance::profile::ResolvedTarget;
  ResolvedTarget first{"first", "hip", "gfx1030", {{"CONFORMANCE_DEVICE", "a"}, {"VALUE", "1"}}, true};
  auto duplicate = first;
  duplicate.id = "duplicate";
  auto selector = first;
  selector.id = "device";
  selector.environment["CONFORMANCE_DEVICE"] = "b";
  auto environment = first;
  environment.id = "environment";
  environment.environment["VALUE"] = "2";
  auto architecture = first;
  architecture.id = "architecture";
  architecture.architecture = "gfx1150";
  auto locking = first;
  locking.id = "locking";
  locking.deviceLock = false;
  const std::vector<ResolvedTarget> targets{first, duplicate, selector, environment, architecture, locking};
  const auto isolated = unique_routes(targets, true);
  REQUIRE(isolated.size() == 2);
  CHECK(isolated[0].id == "first");
  CHECK(isolated[1].id == "architecture");
  CHECK(unique_routes(targets, false).size() == 6);
}

TEST_CASE("execution preserves skips environment and distinct report paths", "[driver]") {
  TemporaryDirectory directory;
  const auto names = list_fixture();
  ExecutionConfig config{fixture.parent_path().string(),
                         directory.path.string(),
                         conformance::physical_device_key(conformance::make_registry_device("fixture")),
                         base_environment(),
                         {},
                         {},
                         {"[fixture]"},
                         false};
  config.environment["EXPECTED_ENVIRONMENT"] = "value with spaces";
  config.environment["PROPAGATED_ENVIRONMENT"] = "value with spaces";
  config.environment["CONFORMANCE_TARGET_ARCHITECTURE"] = "";
  std::string buffer;
  const auto file = directory.path / "config.json";
  REQUIRE(!glz::write_file_json(config, file.string(), buffer));
  CHECK(execute_case(file, {names[0], fixture.stem().string(), 10, 0, 2}) == 0);
  CHECK(execute_case(file, {names[1], fixture.stem().string(), 10, 1, 2}) == 4);
  CHECK(fs::is_regular_file(directory.path / fixture.stem() / "0/result.txt"));
  CHECK(fs::is_directory(directory.path / fixture.stem() / "1"));
  config.skips = {names[0]};
  REQUIRE(!glz::write_file_json(config, file.string(), buffer));
  CHECK(execute_case(file, {names[0], fixture.stem().string(), 10, 0, 2}) == 4);
  CHECK_FALSE(fs::exists(directory.path / "skips.log"));
}

TEST_CASE("runtime environment reserves owned keys and excludes inherited SDK paths", "[driver]") {
  TemporaryDirectory directory;
  auto env = base_environment();
  env["LD_LIBRARY_PATH"] = "/host/sdk/lib";
  env["CUDA_HOME"] = "/host/sdk";
  env["ROCBLAS_TENSILE_LIBPATH"] = "stale";
  prepare_runtime_environment(env, directory.path / "artifact", directory.path / "support", "opencl");
  CHECK_FALSE(env.contains("CUDA_HOME"));
  CHECK_FALSE(env.contains("ROCBLAS_TENSILE_LIBPATH"));
#if defined(__linux__)
  CHECK(env.at("LD_LIBRARY_PATH") == (directory.path / "artifact/lib").string() + ":" + (directory.path / "support/lib").string());
#endif
  for (auto key :
       {"CONFORMANCE_TARGET_ARCHITECTURE", "CONFORMANCE_REPORT_DIRECTORY", "CONFORMANCE_TEST_IDENTITY", "CONFORMANCE_PHYSICAL_DEVICE",
        "CUDA_VISIBLE_DEVICES", "HIP_VISIBLE_DEVICES", "ROCR_VISIBLE_DEVICES", "CL_DEVICE_INDEX", "ONEAPI_DEVICE_SELECTOR"})
    CHECK(conformance::reserved_environment_key(key));
  CHECK_FALSE(conformance::valid_environment_entry("BAD=KEY", "value"));
  CHECK_FALSE(conformance::valid_environment_entry("KEY", std::string("bad\0value", 9)));
  ExecutionConfig config;
  apply_device_probe(config, {conformance::physical_device_key(conformance::make_registry_device("selected")), {{"CL_DEVICE_INDEX", "2"}}});
  CHECK(config.environment.at("CL_DEVICE_INDEX") == "2");
  CHECK(config.environment.at("CONFORMANCE_PHYSICAL_DEVICE") == config.device);
  CHECK_THROWS(apply_device_probe(config, {config.device, {{"LD_PRELOAD", "unexpected"}}}));
}

TEST_CASE("Fire CLI validates options and preserves raw launcher arguments", "[driver]") {
  char program[] = "conformance", run[] = "run", flag[] = "--launcher", separator[] = "--", command[] = "launcher";
  char space[] = "two words", dash[] = "--child-option";
  char *raw[]{program, run, flag, separator, command, space, dash};
  CHECK(launcher_arguments(true, std::span<char *const>(raw, 7)) == std::vector<std::string>{"launcher", "two words", "--child-option"});
  CHECK(launcher_arguments(false, std::span<char *const>(raw, 7)).empty());
  auto env = base_environment();
  const auto help = run_process({driver.string(), "--help"}, fixture.parent_path(), env, 10s, {}, true);
  CHECK(help.status == 0);
  CHECK(help.error.find("--isolated-launcher") != std::string::npos);
  CHECK(run_process({driver.string(), "run", "--unknown-option"}, fixture.parent_path(), env, 10s, {}, true).status != 0);
  CHECK(run_process({driver.string(), "run", "--launcher"}, fixture.parent_path(), env, 10s, {}, true).status != 0);
}

TEST_CASE("driver job count and physical keys reject invalid values", "[driver]") {
  CHECK(configured_jobs(std::nullopt, 0) == 1);
  CHECK(configured_jobs("7", 12) == 7);
  for (auto invalid : {"0", "-1", "1x", "4294967296"})
    CHECK_THROWS(configured_jobs(invalid, 1));
  CHECK_FALSE(conformance::valid_physical_device_key("pci-xyz"));
  CHECK(conformance::valid_physical_device_key("pci-0123456789abcdef0123456789abcdef"));
}
#if !defined(_WIN32)

TEST_CASE("timeout kills a TERM-resistant process tree within a fixed bound", "[driver][process]") {
  const auto start = std::chrono::steady_clock::now();
  const auto result = run_process({fixture.string(), "--process-tree"}, fixture.parent_path(), base_environment(), 250ms, {}, true);
  CHECK(result.status == 124);
  CHECK(std::chrono::steady_clock::now() - start < 5s);
  const auto pid = static_cast<pid_t>(std::stoi(result.output));
  bool alive = true;
  for (size_t attempt = 0; attempt < 50 && alive; ++attempt) {
    alive = kill(pid, 0) == 0;
  #if defined(__linux__)
    const auto status = read(fs::path("/proc") / fmt::to_string(pid) / "stat");
    const auto end = status.rfind(')');
    if (end != std::string::npos && status.substr(end + 2, 1) == "Z") alive = false;
  #endif
    if (alive) std::this_thread::sleep_for(20ms);
  }
  CHECK_FALSE(alive);
}

TEST_CASE("device lock acquisition has a deadline", "[driver][process]") {
  const auto device = conformance::make_registry_device(fmt::format("deadline{}", getpid()));
  conformance::DeviceLock lock(device);
  const auto start = std::chrono::steady_clock::now();
  CHECK_THROWS(conformance::DeviceLock(device, 30ms));
  CHECK(std::chrono::steady_clock::now() - start < 1s);
}
#endif
