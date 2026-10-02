#include "driver.hpp"

#include <cerrno>
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
fs::path fixture_artifact(const fs::path &directory) {
  fs::create_directories(directory);
  fs::copy_file(fixture, directory / fixture.filename());
  std::ofstream(directory / "test-programs.txt") << fixture.stem().string() << "\t10\tcatch2\n";
  return directory;
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
    manifest << program << "\t10\tcatch2\n";
  }
  manifest.close();
  RunOptions options;
  Workload workload;
  REQUIRE(!glz::read_toml(workload, std::string(R"(specs = ['[fixture]']
include_programs = ['alpha_cts_*']
)")));
  std::vector<std::string> expectedPrograms{"alpha_cts_one", "alpha_cts_two"};
  SECTION("a missing format is rejected before discovery") {
    std::ofstream(directory.path / "test-programs.txt") << "alpha_cts_one\t10\n";
    CHECK_THROWS_WITH(discover_cases(options, workload, directory.path, base_environment(), "fixture", directory.path / "reports"),
                      "missing test program format: " + (directory.path / "test-programs.txt").string());
    return;
  }
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
    CHECK(discover_cases(options, workload, directory.path, base_environment(), "fixture", directory.path / "reports").empty());
    return;
  }
  const auto tests = discover_cases(options, workload, directory.path, base_environment(), "fixture", directory.path / "reports");
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
    std::ofstream(artifact / "test-programs.txt") << backend << "_cts_fixture\t10\tcatch2\n";
    fs::copy_file(fixture, artifact / (std::string(backend) + "_cts_fixture" + fixture.extension().string()));
    fs::copy_file(fixture, artifact / ("conformance-device" + fixture.extension().string()));
  }
  std::string inclusion;
  bool empty = false;
  SECTION("compact and full style workloads select all backends") {}
  SECTION("direct execution needs no profiles or hostname and schedules every explicit route") {
    fs::remove_all(options.profiles);
    options.profiles.clear();
    options.machine.reset();
    options.workloads = directory.path / "workloads";
    options.direct = true;
    options.selectors = {"opencl", "sycl"};
  }
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
    for (const auto &report : fs::directory_iterator(options.output / (options.direct ? "direct" : "fixture"))) {
      const auto inventory = read(report.path() / "ctest/CTestTestfile.cmake");
      CHECK((inventory.find("opencl_cts_fixture") != std::string::npos)
            == ((options.direct || options.selectors.empty()) && inclusion.find("sycl_cts") == std::string::npos));
      CHECK((inventory.find("sycl_cts_fixture") != std::string::npos) == (inclusion.find("opencl_cts") == std::string::npos));
    }
  }
}

TEST_CASE("bounded workloads exclude CUDA regressions that walk billions of indices", "[driver][workload-budget]") {
  for (const auto &name : {"compact", "full"}) {
    DYNAMIC_SECTION(name) {
      RunOptions options;
      options.workloads = fs::path(CONFORMANCE_PROFILE_DIRECTORY).parent_path() / "workloads";
      options.workload = name;
      const auto workload = load_workload(options);
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

TEST_CASE("isolated CUDA PTX and SASS routes remain distinct", "[driver]") {
  using conformance::profile::ResolvedTarget;
  ResolvedTarget ptx{"cuda-ptx", "cuda", "sm_89", {{"CONFORMANCE_CUDA_CODE", "ptx"}}, true};
  ResolvedTarget sass{"cuda-sass", "cuda", "sm_89", {{"CONFORMANCE_CUDA_CODE", "sass"}}, true};
  const auto isolated = unique_routes({ptx, sass}, true);
  REQUIRE(isolated.size() == 2);
  CHECK(isolated[0].id == "cuda-ptx");
  CHECK(isolated[1].id == "cuda-sass");
  CHECK_FALSE(isolated[0].deviceLock);
  CHECK_FALSE(isolated[1].deviceLock);
}

TEST_CASE("failed tests retain both streams in full and print a bounded diagnostic excerpt", "[driver]") {
  TemporaryDirectory directory;
  const auto artifact = fixture_artifact(directory.path / "artifact");
  ExecutionConfig config{artifact.string(),
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
  const auto artifact = fixture_artifact(directory.path / "artifact");
  const auto names = list_fixture();
  ExecutionConfig config{artifact.string(),
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
  config.skips = {"*"};
  config.skipReason = "strict route excludes this fixture";
  REQUIRE(!glz::write_file_json(config, file.string(), buffer));
  CHECK(execute_case(file, {names[0], fixture.stem().string(), 10, 0, 2}) == SkipReturnCode);
  config.skips.clear();
  config.routeSkips = {"blas/*", "edge/blas/*"};
  REQUIRE(!glz::write_file_json(config, file.string(), buffer));
  CHECK(execute_case(file, {"blas/f32/compact/asum", "unified", 10, 0, 2}) == SkipReturnCode);
  CHECK(execute_case(file, {"edge/blas/f32/compact/gemm", "unified", 10, 0, 2}) == SkipReturnCode);
  CHECK_FALSE(fs::exists(directory.path / "skips.log"));
}

TEST_CASE("runtime environment reserves owned keys and excludes inherited SDK paths", "[driver]") {
  TemporaryDirectory directory;
  auto env = base_environment();
  env["LD_LIBRARY_PATH"] = "/host/sdk/lib";
  env["CUDA_HOME"] = "/host/sdk";
  env["ROCBLAS_TENSILE_LIBPATH"] = "stale";
  prepare_runtime_environment(env, directory.path / "artifact", directory.path / "support", "opencl");
  env["CONFORMANCE_VULKAN_CTS_TIMEOUT"] = "7200";
  prepare_runtime_environment(env, directory.path / "artifact", directory.path / "support", "vulkan");
  apply_suite_isolated_environment(env, "vulkan");
  CHECK(env.at("CONFORMANCE_VULKAN_CTS_TIMEOUT") == "7200");
  CHECK_FALSE(env.contains("CONFORMANCE_DEVICE"));
  CHECK_FALSE(env.contains("CUDA_HOME"));
  CHECK_FALSE(env.contains("ROCBLAS_TENSILE_LIBPATH"));
#if defined(__linux__)
  CHECK(env.at("LD_LIBRARY_PATH") == (directory.path / "artifact/lib").string() + ":" + (directory.path / "support/lib").string());
#endif
  for (auto key :
       {"CONFORMANCE_TARGET_ARCHITECTURE", "CONFORMANCE_REPORT_DIRECTORY", "CONFORMANCE_TEST_IDENTITY", "CONFORMANCE_PHYSICAL_DEVICE",
        "CUDA_VISIBLE_DEVICES", "HIP_VISIBLE_DEVICES", "ROCR_VISIBLE_DEVICES", "CL_DEVICE_INDEX", "ONEAPI_DEVICE_SELECTOR"})
    CHECK(conformance::reserved_environment_key(key));
  CHECK_FALSE(conformance::reserved_environment_key("CONFORMANCE_CUDA_CODE"));
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

TEST_CASE("direct CLI rejects profile options and reports missing artifacts without profile lookup", "[driver]") {
  TemporaryDirectory directory;
  const auto invoke = [&](std::vector<std::string> extra) {
    std::vector<std::string> args{driver.string(),
                                  "run",
                                  "--direct",
                                  "--artifacts",
                                  (directory.path / "artifacts").string(),
                                  "--output",
                                  (directory.path / "output").string(),
                                  "--workloads",
                                  (directory.path / "workloads").string()};
    args.insert(args.end(), extra.begin(), extra.end());
    return run_process(args, directory.path, base_environment(), 10s, {}, true);
  };
  for (const auto &flag : {"--machine", "--profiles"}) {
    const auto result = invoke({flag, "nonexistent", "vulkan"});
    CHECK(result.status != 0);
    CHECK(result.error.find("--direct cannot use") != std::string::npos);
  }
  const auto empty = invoke({});
  CHECK(empty.status != 0);
  CHECK(empty.error.find("requires explicit") != std::string::npos);
  const auto missing = invoke({"vulkan"});
  CHECK(missing.status != 0);
  CHECK(missing.error.find("artifact metadata") != std::string::npos);
  CHECK(missing.error.find("vulkan") != std::string::npos);
  CHECK_FALSE(fs::exists(directory.path / "output"));
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

TEST_CASE("dEQP inventories and QPA results preserve failures and skips", "[driver]") {
  CHECK(deqp_case_names("GROUP: dEQP-VK.api\nTEST: dEQP-VK.api.test\n") == std::vector<std::string>{"dEQP-VK.api.test"});
  CHECK_THROWS(deqp_case_names("GROUP: dEQP-VK.api\n"));
  CHECK_THROWS(deqp_case_names("TEST: dEQP-VK.a\nTEST: dEQP-VK.a\n"));
  const auto qpa = [](const std::string &status) {
    return "#beginTestCaseResult dEQP-VK.a\n<TestCaseResult CasePath=\"dEQP-VK.a\" Version=\"0.3.4\">"
           "<Result StatusCode=\""
           + status + "\">details</Result></TestCaseResult>\n#endTestCaseResult\n";
  };
  CHECK(deqp_result(qpa("Pass"), "dEQP-VK.a") == 0);
  CHECK(deqp_result(qpa("Fail"), "dEQP-VK.a") == 1);
  CHECK(deqp_result(qpa("NotSupported"), "dEQP-VK.a") == 4);
  CHECK(deqp_result(qpa("QualityWarning"), "dEQP-VK.a") == 0);
  CHECK(deqp_result(qpa("CompatibilityWarning"), "dEQP-VK.a") == 0);
  CHECK(deqp_result(qpa("Waiver"), "dEQP-VK.a") == 1);
  CHECK_THROWS(deqp_result(qpa("Pass"), "dEQP-VK.b"));
  CHECK_THROWS(deqp_result(qpa("Pass") + qpa("Pass"), "dEQP-VK.a"));
  CHECK_THROWS(deqp_result("", "dEQP-VK.a"));
  auto truncated = qpa("Pass");
  truncated.erase(truncated.find("#endTestCaseResult"));
  CHECK_THROWS(deqp_result(truncated, "dEQP-VK.a"));
}

TEST_CASE("Vulkan discovery bounds large selector lists without truncating cases") {
  std::vector<std::string> patterns;
  std::string expected;
  for (unsigned i = 0; i < 3000; ++i) {
    patterns.push_back("dEQP-VK." + std::string(100, 'x') + "." + std::to_string(i));
    if (!expected.empty()) expected += ',';
    expected += patterns.back();
  }
  const auto batches = deqp_case_selections(patterns);
  REQUIRE(batches.size() > 1);
  std::string actual;
  for (const auto &batch : batches) {
    CHECK(batch.size() <= 16 * 1024);
    if (!actual.empty()) actual += ',';
    actual += batch;
  }
  CHECK(actual == expected);
  CHECK(deqp_case_selections({"dEQP-VK.a.*", "dEQP-VK.b"}) == std::vector<std::string>{"dEQP-VK.a.*,dEQP-VK.b"});
  CHECK_THROWS(deqp_case_selections({}));
  CHECK_THROWS(deqp_case_selections({""}));
  CHECK_THROWS(deqp_case_selections({std::string(16385, 'a')}));
  const auto file = deqp_exact_case_list(patterns);
  REQUIRE(file.has_value());
  CHECK(std::count(file->begin(), file->end(), '\n') == 3000);
  CHECK(file->find(patterns.front() + '\n') != std::string::npos);
  CHECK(file->find(patterns.back() + '\n') != std::string::npos);
}

TEST_CASE("Vulkan exact case files deduplicate literals and leave patterns on the command line") {
  CHECK(deqp_exact_case_list({"dEQP-VK.b", "dEQP-VK.a", "dEQP-VK.b"}) == "dEQP-VK.a\ndEQP-VK.b\n");
  CHECK_FALSE(deqp_exact_case_list({"dEQP-VK.a.*", "dEQP-VK.b"}).has_value());
  CHECK_FALSE(deqp_exact_case_list({"dEQP-VK.a?"}).has_value());
  CHECK_FALSE(deqp_exact_case_list({"dEQP-VK.a\ndEQP-VK.b"}).has_value());
  CHECK_FALSE(deqp_exact_case_list({"dEQP-VK.a,dEQP-VK.b"}).has_value());
  CHECK_THROWS(deqp_exact_case_list({}));
  CHECK_THROWS(deqp_exact_case_list({"dEQP-VK.a.*", ""}));
  CHECK_THROWS(deqp_exact_case_list({std::string(16385, 'a')}));
}

TEST_CASE("Vulkan mustpass manifests retain official cases and diagnose invalid selections") {
  TemporaryDirectory directory;
  const auto root = directory.path / "data";
  fs::create_directories(root / "mustpass/groups");
  std::ofstream(root / "mustpass/default.txt") << "# Pinned inventory\ngroups/a.txt\ngroups/b.txt\n";
  std::ofstream(root / "mustpass/groups/a.txt") << "dEQP-VK.a.one\ndEQP-VK.a.two\n";
  std::ofstream(root / "mustpass/groups/b.txt") << "dEQP-VK.b.one\n";
  const std::vector<std::string> all{"dEQP-VK.a.one", "dEQP-VK.a.two", "dEQP-VK.b.one"};
  CHECK(deqp_mustpass_cases(root, "mustpass/default.txt", {}) == all);
  CHECK(deqp_mustpass_cases(root, "mustpass/default.txt", {"dEQP-VK.a.*", "dEQP-VK.b.one"}) == all);
  CHECK(deqp_mustpass_cases(root, "mustpass/default.txt", {"dEQP-VK.a.one", "dEQP-VK.a.one"}) == std::vector<std::string>{"dEQP-VK.a.one"});
  CHECK_THROWS(deqp_mustpass_cases(root, "mustpass/default.txt", {"dEQP-VK.missing.*"}));
  CHECK_THROWS(deqp_mustpass_cases(root, "mustpass/default.txt", {"dEQP-VK.a.one", ""}));
  std::ofstream(root / "mustpass/groups/b.txt") << "dEQP-VK.a.one\n";
  CHECK_THROWS(deqp_mustpass_cases(root, "mustpass/default.txt", {}));
  std::ofstream(root / "mustpass/groups/b.txt") << "../default.txt\n";
  CHECK_THROWS(deqp_mustpass_cases(root, "mustpass/default.txt", {}));
  std::ofstream(directory.path / "outside.txt") << "dEQP-VK.outside\n";
  CHECK_THROWS(deqp_mustpass_cases(root, "../outside.txt", {}));
  CHECK_THROWS(deqp_mustpass_cases(root, directory.path / "outside.txt", {}));
  std::ofstream(root / "mustpass/groups/b.txt") << "dEQP-VK.invalid.*\n";
  CHECK_THROWS(deqp_mustpass_cases(root, "mustpass/default.txt", {}));
}

TEST_CASE("workload inheritance replaces explicit fields and rejects invalid ancestry", "[driver]") {
  TemporaryDirectory directory;
  std::ofstream(directory.path / "base.toml") << "specs = ['[fixture]']\ninclude_programs = ['alpha*']\nvulkan_cases = ['dEQP-VK.a']\n";
  std::ofstream(directory.path / "child.toml") << "extends = 'base'\ninclude_programs = []\n";
  RunOptions options;
  options.workloads = directory.path;
  options.workload = "child";
  const auto child = load_workload(options);
  CHECK(child.specs == std::vector<std::string>{"[fixture]"});
  CHECK(child.include_programs.empty());
  CHECK(child.vulkan_cases == std::vector<std::string>{"dEQP-VK.a"});
  std::ofstream(directory.path / "base.toml") << "extends = 'child'\n";
  CHECK_THROWS(load_workload(options));
  std::ofstream(directory.path / "base.toml") << "extends = '../outside'\n";
  CHECK_THROWS(load_workload(options));
  std::ofstream(directory.path / "base.toml") << "extends = 'absent'\n";
  CHECK_THROWS(load_workload(options));
}

TEST_CASE("full includes the same bounded CTS selections as standalone workloads", "[driver][workload-budget]") {
  RunOptions options;
  options.workloads = fs::path(CONFORMANCE_PROFILE_DIRECTORY).parent_path() / "workloads";
  options.workload = "full";
  const auto full = load_workload(options);
  REQUIRE(full.include_programs.empty());
  for (const auto *name : {"opencl-cts-bounded", "sycl-cts-bounded", "vulkan-cts-bounded"}) {
    options.workload = name;
    const auto bounded = load_workload(options);
    CHECK(full.exclude_programs == bounded.exclude_programs);
    REQUIRE(bounded.specs.size() == 1);
    REQUIRE(full.specs.size() == 1);
    CHECK(full.specs.front().starts_with(bounded.specs.front()));
    CHECK(full.vulkan_cases == bounded.vulkan_cases);
  }
  options.workload = "compact";
  const auto compact = load_workload(options);
  for (const auto &name : compact.vulkan_cases)
    CHECK(std::find(full.vulkan_cases.begin(), full.vulkan_cases.end(), name) != full.vulkan_cases.end());
}

TEST_CASE("large discovery logs preserve late cases and capped captures fail explicitly", "[driver]") {
  TemporaryDirectory directory;
  const auto path = directory.path / "inventory.log";
  std::ofstream log(path);
  const std::vector<std::string> command{fixture.string(), "--large-inventory"};
  REQUIRE(run_process(command, directory.path, base_environment(), 30s, {}, false, &log).status == 0);
  log.close();
  INFO("inventory log: " << std::error_code(errno, std::generic_category()).message());
  REQUIRE(log.good());
  std::ifstream input(path);
  CHECK(deqp_case_names(input) == std::vector<std::string>{"dEQP-VK.last.case"});
  CHECK_THROWS_WITH(run_process(command, directory.path, base_environment(), 30s, {}, true),
                    "process output exceeded the 128 MiB capture limit: " + fixture.string());
}
