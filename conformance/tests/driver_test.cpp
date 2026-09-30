#include "driver.hpp"

#include <chrono>
#include <cstdlib>
#include <fstream>
#include <sstream>
#include <thread>

#include <catch2/catch_test_case_info.hpp>
#include <catch2/catch_test_macros.hpp>
#include <catch2/interfaces/catch_interfaces_registry_hub.hpp>
#include <catch2/internal/catch_test_registry.hpp>
#include <catch2/internal/catch_test_spec_parser.hpp>
#include <catch2/matchers/catch_matchers.hpp>
#include <glaze/json.hpp>
#include <glaze/toml.hpp>

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

TEST_CASE("workload program globs select discovery without changing native case names", "[driver]") {
  TemporaryDirectory directory;
  const std::vector<std::string> programs{"alpha_cts_one", "alpha_cts_two", "beta_cts_three", "unrelated"};
  std::ofstream manifest(directory.path / "test-programs.txt");
  for (const auto &program : programs) {
    fs::copy_file(fixture, directory.path / (program + fixture.extension().string()));
    manifest << program << "\t10\n";
  }
  manifest.close();
  RunOptions options;
  Workload workload;
  REQUIRE(!glz::read_toml(workload, std::string(R"(specs = ['[fixture]']
include_programs = ['alpha_cts_*']
)")));
  std::vector<std::string> expectedPrograms{"alpha_cts_one", "alpha_cts_two"};
  SECTION("empty inclusion preserves existing all-program discovery") {
    workload.include_programs.clear();
    expectedPrograms = programs;
  }
  SECTION("a glob rejects unrelated programs") {}
  SECTION("multiple patterns and question marks match program names") {
    workload.include_programs = {"alpha_cts_?ne", "beta_cts_*"};
    expectedPrograms = {"alpha_cts_one", "beta_cts_three"};
  }
  SECTION("exact exclusions take precedence over matching inclusion") {
    workload.exclude_programs = {"alpha_cts_two"};
    expectedPrograms = {"alpha_cts_one"};
  }
  SECTION("exclusions retain their existing exact-name semantics") { workload.exclude_programs = {"alpha_cts_*"}; }
  SECTION("an unmatched inclusion reports an empty selection") {
    workload.include_programs = {"absent_*"};
    CHECK(discover_cases(options, workload, directory.path, base_environment(), "fixture").empty());
    return;
  }
  const auto tests = discover_cases(options, workload, directory.path, base_environment(), "fixture");
  const auto names = list_fixture();
  REQUIRE(tests.size() == expectedPrograms.size() * names.size());
  for (size_t index = 0; index < tests.size(); ++index) {
    CHECK(tests[index].program == expectedPrograms[index / names.size()]);
    CHECK(tests[index].identity == names[index % names.size()]);
    CHECK(tests[index].count == names.size());
    CHECK(tests[index].index == index % names.size());
  }
}

TEST_CASE("workloads omit unrelated targets and reject an entirely empty selection", "[driver]") {
  TemporaryDirectory directory;
  RunOptions options;
  options.profiles = directory.path / "profiles";
  options.artifacts = directory.path / "artifacts";
  options.output = directory.path / "reports";
  options.machine = "fixture";
  fs::create_directories(options.profiles);
  fs::create_directories(directory.path / "workloads");
  std::ofstream(options.profiles / "fixture.toml") << R"([[targets]]
backend = "opencl"
deviceLock = false
[[targets]]
backend = "sycl"
deviceLock = false
)";
  for (const auto &backend : {"opencl", "sycl"}) {
    const auto artifact = options.artifacts / backend;
    fs::create_directories(artifact);
    std::ofstream(artifact / "artifact.toml") << "backend = '" << backend << "'\n";
    std::ofstream(artifact / "test-programs.txt") << backend << "_cts_fixture\t10\n";
    fs::copy_file(fixture, artifact / (std::string(backend) + "_cts_fixture" + fixture.extension().string()));
    fs::copy_file(fixture, artifact / ("conformance-device" + fixture.extension().string()));
  }
  std::string inclusion;
  bool empty = false;
  SECTION("compact and full style workloads select all backends") {}
  SECTION("SYCL-only workload needs no selector or unrelated device probe") {
    inclusion = "include_programs = ['sycl_cts_*']\n";
    fs::remove(options.artifacts / "opencl" / ("conformance-device" + fixture.extension().string()));
  }
  SECTION("OpenCL-only workload needs no selector") { inclusion = "include_programs = ['opencl_cts_*']\n"; }
  SECTION("explicit target selection still narrows the run") { options.selectors = {"sycl"}; }
  SECTION("no program matches anywhere is an error") {
    inclusion = "include_programs = ['absent_*']\n";
    empty = true;
  }
  SECTION("no cases match anywhere is an error") { empty = true; }
  std::ofstream(directory.path / "workloads/compact.toml")
      << inclusion << (empty && inclusion.empty() ? "specs = ['[absent]']\n" : "specs = ['[pass]']\n");
  if (empty) {
    CHECK_THROWS_WITH(run(options, driver), "conformance workload selected no tests: compact");
  } else {
    CHECK(run(options, driver) == 0);
    for (const auto &report : fs::directory_iterator(options.output / "fixture")) {
      const auto inventory = read(report.path() / "ctest/CTestTestfile.cmake");
      CHECK((inventory.find("opencl_cts_fixture") != std::string::npos)
            == (options.selectors.empty() && inclusion.find("sycl_cts") == std::string::npos));
      CHECK((inventory.find("sycl_cts_fixture") != std::string::npos) == (inclusion.find("opencl_cts") == std::string::npos));
    }
  }
}

TEST_CASE("bounded workloads exclude CUDA regressions that walk billions of indices", "[driver][workload-budget]") {
  for (const auto &name : {"compact", "full"}) {
    DYNAMIC_SECTION(name) {
      Workload workload;
      const auto file = fs::path(CONFORMANCE_PROFILE_DIRECTORY).parent_path() / "workloads" / (std::string(name) + ".toml");
      REQUIRE(!glz::read_toml(workload, read(file)));
      Catch::TestSpecParser parser(Catch::getRegistryHub().getTagAliasRegistry());
      for (const auto &spec : workload.specs)
        parser.parse(spec);
      const auto selection = parser.testSpec();
      for (const auto &test :
           {"regressions/cuda-thrust/for-each-with-big-indexes", "regressions/cuda-thrust/adjacent-difference-with-big-indexes"}) {
        CAPTURE(test);
        const Catch::TestCaseInfo info("", {test, "[unified][regression]"}, {__FILE__, __LINE__});
        CHECK_FALSE(selection.matches(info));
      }
      const Catch::TestCaseInfo shortCase("", {"stress/i8/full/dependency_diamond", "[unified]"}, {__FILE__, __LINE__});
      CHECK(selection.matches(shortCase));
    }
  }
}

TEST_CASE("Google Test inventories retain parameterized cases and reject duplicates", "[driver]") {
  const auto names = gtest_case_names("Driver.\n  Opens\n  DISABLED_Old\nParam/Copy.\n  Case/0  # GetParam() = 8\n  Case/1\n", "levelzero");
  CHECK(names == std::vector<std::string>{"Driver.Opens", "Param/Copy.Case/0", "Param/Copy.Case/1"});
  CHECK_THROWS(gtest_case_names("  Orphan\n", "levelzero"));
  CHECK_THROWS(gtest_case_names("Driver.\n  Duplicate\n  Duplicate\n", "levelzero"));
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
  materialize_ctest(directory.path, driver, {target});
  const auto output = read(directory.path / "CTestTestfile.cmake");
  size_t count = 0;
  for (size_t at = 0; (at = output.find("set_tests_properties(", at)) != std::string::npos; ++at)
    ++count;
  CHECK(count == 2);
  CHECK(output.find("SKIP_RETURN_CODE 4") != std::string::npos);
  CHECK(output.find("RESOURCE_LOCK [=[pci-0123456789abcdef0123456789abcdef]=]") != std::string::npos);
  CHECK(output.find("case/51999") != std::string::npos);
}

TEST_CASE("CTest reads generated timeout groups and device locks", "[driver]") {
  TemporaryDirectory directory;
  TargetExecution target{"route", "opencl", "pci-0123456789abcdef0123456789abcdef", directory.path / "route.json", {}};
  target.tests = {{"first", "fixture", 120U, 0, 3}, {"second", "fixture", 300U, 1, 3}, {"third", "fixture", 120U, 2, 3}};
  materialize_ctest(directory.path, driver, {target});
  const auto listing = run_process({"ctest", "--test-dir", directory.path.string(), "--show-only=json-v1"}, directory.path,
                                   base_environment(), 30s, {}, true);
  INFO(listing.output);
  REQUIRE(listing.status == 0);
  // CTest, rather than just our generated text, must accept both property groups.
  const auto parsed = glz::read_json<glz::generic>(listing.output);
  REQUIRE(parsed.has_value());
  const auto &tests = parsed->at("tests").get_array();
  REQUIRE(tests.size() == 3);
  for (size_t i = 0; i < tests.size(); ++i) {
    bool timeout = false, lock = false, skip = false;
    for (const auto &property : tests[i].at("properties").get_array()) {
      const auto &name = property.at("name").get_string();
      if (name == "TIMEOUT") timeout = property.at("value").get_number() == (i == 1 ? 300 : 120) + TimeoutMarginSeconds;
      if (name == "RESOURCE_LOCK") lock = property.at("value").get_array().at(0).get_string() == target.device;
      if (name == "SKIP_RETURN_CODE") skip = property.at("value").get_number() == SkipReturnCode;
    }
    CHECK(timeout);
    CHECK(lock);
    CHECK(skip);
  }
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
  CHECK_FALSE(isolated[0].deviceLock);
  CHECK_FALSE(isolated[1].deviceLock);
  const auto physical = unique_routes(targets, false);
  REQUIRE(physical.size() == 6);
  CHECK(physical[0].deviceLock);
  CHECK_FALSE(physical[5].deviceLock);
}

TEST_CASE("failed tests retain both streams in full and print a bounded diagnostic excerpt", "[driver]") {
  TemporaryDirectory directory;
  ExecutionConfig config{fixture.parent_path().string(),
                         directory.path.string(),
                         conformance::physical_device_key(conformance::make_pci_device(1, 0, 0, 0)),
                         base_environment(),
                         {},
                         {},
                         {"[noisy]"},
                         false};
  const auto file = directory.path / "config.json";
  std::string buffer;
  REQUIRE(!glz::write_file_json(config, file.string(), buffer));
  const auto result = run_process({driver.string(), "exec", "--config", file.string(), "--program", fixture.stem().string(), "--test",
                                   "noisy failure", "--timeout", "10"},
                                  directory.path, base_environment(), 30s, {}, true);
  INFO(result.error);
  INFO(result.output);
  CHECK(result.status != 0);
  CHECK(result.output.size() < 20000);
  CHECK(result.output.find("first diagnostic") != std::string::npos);
  CHECK(result.output.find("last diagnostic") != std::string::npos);
  CHECK(result.output.find("expected noisy failure") != std::string::npos);
  CHECK(result.output.find("full output:") != std::string::npos);
  const auto recorded = read(directory.path / fixture.stem() / "0/output.log");
  CHECK(recorded.size() > 60000);
  CHECK(recorded.find("first diagnostic") != std::string::npos);
  CHECK(recorded.find("last diagnostic") != std::string::npos);
  CHECK(recorded.find("stderr diagnostic") != std::string::npos);
}

TEST_CASE("execution preserves skips environment and distinct report paths", "[driver]") {
  TemporaryDirectory directory;
  const auto names = list_fixture();
  ExecutionConfig config{fixture.parent_path().string(),
                         directory.path.string(),
                         conformance::physical_device_key(conformance::make_pci_device(1, 0, 0, 0)),
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
  CHECK(execute_case(file, {names[1], fixture.stem().string(), 10, 1, 2}) == SkipReturnCode);
  CHECK(fs::is_regular_file(directory.path / fixture.stem() / "0/result.txt"));
  CHECK(fs::is_directory(directory.path / fixture.stem() / "1"));
  config.skips = {names[0]};
  REQUIRE(!glz::write_file_json(config, file.string(), buffer));
  CHECK(execute_case(file, {names[0], fixture.stem().string(), 10, 0, 2}) == SkipReturnCode);
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
  apply_device_probe(config, {conformance::physical_device_key(conformance::make_pci_device(2, 0, 0, 0)), {{"CL_DEVICE_INDEX", "2"}}});
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

TEST_CASE("CTS workload settings reach the isolated child environment", "[driver]") {
  for (const auto &[key, value] : std::map<std::string, std::string>{{"CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS", "37"},
                                                                     {"CONFORMANCE_OPENCL_CTS_ATOMIC_THREADS", "64"},
                                                                     {"CONFORMANCE_OPENCL_CTS_ELEMENTS", "16384"},
                                                                     {"CONFORMANCE_OPENCL_CTS_WIMPY", "0"},
                                                                     {"CONFORMANCE_OPENCL_CTS_TIMEOUT", "30"}}) {
    const auto *old = std::getenv(key.c_str());
    const auto saved = old ? std::optional<std::string>(old) : std::nullopt;
    REQUIRE(setenv(key.c_str(), value.c_str(), 1) == 0);
    auto environment = base_environment();
    if (saved) REQUIRE(setenv(key.c_str(), saved->c_str(), 1) == 0);
    else REQUIRE(unsetenv(key.c_str()) == 0);
    apply_suite_isolated_environment(environment, "opencl");
    CHECK(environment.at(key) == value);
  }
}

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
  const auto device = conformance::make_pci_device(static_cast<uint32_t>(getpid()), 0, 0, 0);
  conformance::DeviceLock lock(device);
  const auto start = std::chrono::steady_clock::now();
  CHECK_THROWS(conformance::DeviceLock(device, 30ms));
  CHECK(std::chrono::steady_clock::now() - start < 1s);
}
#endif

TEST_CASE("isolated launchers preserve configured device selection", "[driver]") {
  for (const auto *backend : {"cuda", "hip", "hsa", "opencl", "levelzero", "sycl"}) {
    std::map<std::string, std::string> environment{{"CONFORMANCE_DEVICE", "*chosen device*"}};
    apply_suite_isolated_environment(environment, backend);
    CHECK(environment.at("CONFORMANCE_DEVICE") == "*chosen device*");
  }
}

TEST_CASE("only isolated SYCL handler matrices receive the longer timeout", "[driver]") {
  CHECK(suite_timeout("sycl_cts_handler", 120, true) == CONFORMANCE_SYCL_HANDLER_TIMEOUT_SECONDS);
  CHECK(suite_timeout("sycl_cts_handler", 120, false) == 120);
  CHECK(suite_timeout("sycl_cts_handler", 900, true) == 900);
  CHECK(suite_timeout("sycl_cts_buffer", 120, true) == 120);
}

TEST_CASE("isolated OpenCL launchers preserve explicit CTS workload settings", "[driver]") {
  std::map<std::string, std::string> environment{{"CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS", "37"},
                                                 {"CONFORMANCE_OPENCL_CTS_ATOMIC_THREADS", "64"},
                                                 {"CONFORMANCE_OPENCL_CTS_ELEMENTS", "16384"},
                                                 {"CONFORMANCE_OPENCL_CTS_WIMPY", "0"},
                                                 {"CONFORMANCE_OPENCL_CTS_TIMEOUT", "30"}};
  const auto configured = environment;
  apply_suite_isolated_environment(environment, "opencl");
  CHECK(environment == configured);

  environment["CONFORMANCE_OPENCL_CTS_ELEMENTS"] = "";
  apply_suite_isolated_environment(environment, "opencl");
  CHECK(environment.at("CONFORMANCE_OPENCL_CTS_ELEMENTS").empty());
}
