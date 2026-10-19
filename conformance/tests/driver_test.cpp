#include "driver.hpp"

#include <algorithm>
#include <array>
#include <cerrno>
#include <chrono>
#include <csignal>
#include <cstdint>
#include <cstdlib>
#include <ctime>
#include <filesystem>
#include <fstream>
#include <future>
#include <iostream>
#include <iterator>
#include <list>
#include <map>
#include <mutex>
#include <optional>
#include <span>
#include <sstream>
#include <stdexcept>
#include <string>
#include <system_error>
#include <thread>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_test_case_info.hpp>
#include <catch2/catch_test_macros.hpp>
#include <catch2/interfaces/catch_interfaces_registry_hub.hpp>
#include <catch2/internal/catch_test_registry.hpp>
#include <catch2/internal/catch_test_spec_parser.hpp>
#include <catch2/matchers/catch_matchers.hpp>
#include <glaze/json.hpp>
#include <glaze/toml.hpp>

#include "conformance/device.hpp"

#include "device_selector.hpp"
#include "environment.hpp"
#include "profile.hpp"
#include "scoped_environment.hpp"
#include "suite_policy.hpp"

namespace {
namespace fs = std::filesystem;
using namespace conformance::driver;
using namespace aspartame;
using namespace std::chrono_literals;
struct TemporaryDirectory {
  fs::path path =
      fs::temp_directory_path() / fmt::format("conformance-test-{}", std::chrono::steady_clock::now().time_since_epoch().count());
  TemporaryDirectory() { fs::create_directories(path); }
  TemporaryDirectory(const TemporaryDirectory &) = delete;
  TemporaryDirectory &operator=(const TemporaryDirectory &) = delete;
  ~TemporaryDirectory() {
    std::error_code error;
    fs::remove_all(path, error);
  }
};
const fs::path Fixture = CONFORMANCE_FIXTURE;
const fs::path Driver = CONFORMANCE_DRIVER;
std::string read(const fs::path &path) {
  std::ifstream file(path);
  return {std::istreambuf_iterator<char>(file), {}};
}
fs::path fixtureArtifact(const fs::path &directory) {
  fs::create_directories(directory);
  fs::copy_file(Fixture, directory / Fixture.filename());
  std::ofstream(directory / "test-programs.txt") << Fixture.stem().string() << "\t10\tcatch2\n";
  return directory;
}
bool exited(int32_t pid) {
  for (size_t attempt = 0; attempt < 50; ++attempt) {
    if (kill(pid, 0) != 0) return true;
    const auto status = read(fs::path("/proc") / fmt::to_string(pid) / "stat");
    const auto end = status.rfind(')');
    if (end != std::string::npos && status.substr(end + 2, 1) == "Z") return true;
    std::this_thread::sleep_for(20ms);
  }
  return false;
}
std::vector<std::string> listFixture() {
  return catchCaseNames(runProcess({Fixture.string(), "--list-tests", "--reporter", "json", "--order", "lex", "[fixture]"},
                                   Fixture.parent_path(), baseEnvironment(), 10s, {}, true)
                            .output,
                        "fixture");
}
} // namespace

TEST_CASE("Catch JSON inventories preserve names and reject malformed input", "[driver]") {
  const auto names = listFixture();
  REQUIRE(names.size() == 2);
  CHECK(names[0].contains(", bracket[quoted] star* backslash\\ semi; quote\" unicode-π"));
  CHECK_THROWS(catchCaseNames("<Name>not JSON</Name>", "bad"));
  CHECK_THROWS(catchCaseNames(R"({"version":9,"listings":{"tests":[]}})", "bad"));
  CHECK_THROWS(catchCaseNames(R"({"version":1,"listings":{"tests":[{"name":"same"},{"name":"same"}]}})", "duplicate"));
  CHECK(catchCaseNames(R"({"version":1,"listings":{"tests":[]}})", "empty selection").empty());
}

TEST_CASE("workload program globs select discovery without changing native case names", "[driver]") {
  TemporaryDirectory const directory;
  const std::vector<std::string> programs{"alpha_cts_one", "alpha_cts_two", "beta_cts_three", "unrelated"};
  std::ofstream manifest(directory.path / "test-programs.txt");
  for (const auto &program : programs) {
    fs::copy_file(Fixture, directory.path / (program + Fixture.extension().string()));
    manifest << program << "\t10\tcatch2\n";
  }
  manifest.close();
  RunOptions const options;
  Workload workload;
  REQUIRE(!glz::read_toml(workload, std::string(R"(specs = ['[fixture]']
include_programs = ['alpha_cts_*']
)")));
  std::vector<std::string> expectedPrograms{"alpha_cts_one", "alpha_cts_two"};
  SECTION("a missing format is rejected before discovery") {
    std::ofstream(directory.path / "test-programs.txt") << "alpha_cts_one\t10\n";
    CHECK_THROWS_WITH(discoverCases(options, workload, directory.path, baseEnvironment(), "fixture", directory.path / "reports"),
                      "missing test program format: " + (directory.path / "test-programs.txt").string());
    return;
  }
  SECTION("empty inclusion preserves existing all-program discovery") {
    workload.includePrograms.clear();
    expectedPrograms = programs;
  }
  SECTION("a glob rejects unrelated programs") {}
  SECTION("multiple patterns and question marks match program names") {
    workload.includePrograms = {"alpha_cts_?ne", "beta_cts_*"};
    expectedPrograms = {"alpha_cts_one", "beta_cts_three"};
  }
  SECTION("exact exclusions take precedence over matching inclusion") {
    workload.excludePrograms = {"alpha_cts_two"};
    expectedPrograms = {"alpha_cts_one"};
  }
  SECTION("exclusions retain their existing exact-name semantics") { workload.excludePrograms = {"alpha_cts_*"}; }
  SECTION("an unmatched inclusion reports an empty selection") {
    workload.includePrograms = {"absent_*"};
    CHECK(discoverCases(options, workload, directory.path, baseEnvironment(), "fixture", directory.path / "reports").empty());
    return;
  }
  const auto tests = discoverCases(options, workload, directory.path, baseEnvironment(), "fixture", directory.path / "reports");
  const auto names = listFixture();
  REQUIRE(tests.size() == expectedPrograms.size() * names.size());
  for (size_t index = 0; index < tests.size(); ++index) {
    CHECK(tests[index].program == expectedPrograms[index / names.size()]);
    CHECK(tests[index].identity == names[index % names.size()]);
    CHECK(tests[index].count == names.size());
    CHECK(tests[index].index == index % names.size());
  }
}

TEST_CASE("workloads omit unrelated targets and reject an entirely empty selection", "[driver]") {
  TemporaryDirectory const directory;
  RunOptions options;
  options.profiles = directory.path / "profiles";
  options.artifacts = directory.path / "artifacts";
  options.output = directory.path / "reports";
  options.machine = "fixture";
  fs::create_directories(options.profiles);
  fs::create_directories(directory.path / "workloads");
  std::ofstream(options.profiles / "fixture.toml") << R"([[targets]]
backend = "opencl"
[[targets]]
backend = "sycl"
)";
  for (const auto &backend : {"opencl", "sycl"}) {
    const auto artifact = options.artifacts / backend;
    fs::create_directories(artifact);
    std::ofstream(artifact / "artifact.toml") << "backend = '" << backend << "'\n";
    std::ofstream(artifact / "test-programs.txt") << backend << "_cts_fixture\t10\tcatch2\n";
    fs::copy_file(Fixture, artifact / (std::string(backend) + "_cts_fixture" + Fixture.extension().string()));
    fs::copy_file(Fixture, artifact / ("conformance-device" + Fixture.extension().string()));
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
  SECTION("isolated launchers schedule routes without device locks") {
    options.isolatedLauncher = true;
    options.launcher = {"env"};
  }
  SECTION("SYCL-only workload needs no selector or unrelated device probe") {
    inclusion = "include_programs = ['sycl_cts_*']\n";
    fs::remove(options.artifacts / "opencl" / ("conformance-device" + Fixture.extension().string()));
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
    CHECK_THROWS_WITH(run(options), "conformance workload selected no tests: compact");
  } else {
    CHECK(run(options) == 0);
    for (const auto &report : fs::directory_iterator(options.output / (options.direct ? "direct" : "fixture"))) {
      const auto junit = read(report.path() / "junit.xml");
      CHECK(junit.contains("opencl_cts_fixture") == ((options.direct || options.selectors.empty()) && !inclusion.contains("sycl_cts")));
      CHECK(junit.contains("sycl_cts_fixture") == (!inclusion.contains("opencl_cts")));
    }
    if (options.workloads.empty()) options.workloads = directory.path / "workloads";
    const auto *const backend = !inclusion.contains("opencl_cts") ? "sycl" : "opencl";
    const auto target =
        prepareTarget(options, loadWorkload(options), {.id = backend, .backend = backend, .architecture = std::nullopt, .environment = {}},
                      directory.path / "probe");
    if (!target) FAIL("probe target was omitted");
    CHECK(target->config.deviceLock == !(options.direct || options.isolatedLauncher));
    CHECK(target->config.device == "pci-0123456789abcdef0123456789abcdef");
  }
}

TEST_CASE("case results report passes, failures and skips by full name", "[driver]") {
  TemporaryDirectory const directory;
  const auto artifact = fixtureArtifact(directory.path / "artifacts/opencl");
  const auto workloads = directory.path / "workloads";
  const auto reports = directory.path / "reports";
  const auto longProgram = std::string(80, '.') + std::string(100, 'x');
  fs::copy_file(Fixture, artifact / (longProgram + Fixture.extension().string()));
  fs::copy_file(Fixture, artifact / ("conformance-device" + Fixture.extension().string()));
  std::ofstream(artifact / "test-programs.txt", std::ios::app) << longProgram << "\t10\tcatch2\n";
  std::ofstream(artifact / "artifact.toml") << "backend = 'opencl'\n";
  fs::create_directories(workloads);
  std::ofstream(workloads / "compact.toml") << "specs = ['[fixture],[failure]']\n";
  auto environment = baseEnvironment();
  environment["CONFORMANCE_JOBS"] = "2";
  const auto result = runProcess({Driver.string(), "run", "--direct", "--artifacts", artifact.parent_path().string(), "--output",
                                  reports.string(), "--workloads", workloads.string(), "opencl"},
                                 directory.path, environment, 20s, {}, true);
  INFO(result.output);
  INFO(result.error);
  REQUIRE(result.status != 0);
  CHECK(result.output.contains("Passed"));
  CHECK(result.output.contains("***Failed"));
  CHECK(result.output.contains("Skipped"));
  CHECK(result.output.contains("full output:"));
  CHECK(result.output.contains("opencl/" + longProgram + "/not selected failure"));
  for (const auto &report : fs::directory_iterator(reports / "direct")) {
    const auto junit = read(report.path() / "junit.xml");
    CHECK(junit.contains("opencl/" + longProgram + "/not selected failure"));
    bool selected = false;
    for (const auto &entry : fs::recursive_directory_iterator(report.path()))
      if (entry.path().filename() == "output.log" && read(entry.path()).contains("wrong shard or spec selected")) selected = true;
    CHECK(selected);
    CHECK(junit.contains("quote&quot; unicode"));
    CHECK(junit.contains("<skipped/>"));
    CHECK(!read(report.path() / "compact.toml").contains("extends"));
    RunOptions resolved;
    resolved.workloads = report.path();
    const auto workload = loadWorkload(resolved);
    CHECK(workload.specs == std::vector<std::string>{"[fixture],[failure]"});
    CHECK(workload.excludePrograms.empty());
  }
}

TEST_CASE("parallel discovery reports progress before probes finish and preserves inventory order", "[driver][process]") {
  TemporaryDirectory directory;
  const auto artifacts = directory.path / "artifacts";
  const auto workloads = directory.path / "workloads";
  const auto reports = directory.path / "reports";
  fs::create_directories(workloads);
  std::ofstream(workloads / "compact.toml") << "specs = ['[pass]']\n";
  for (const auto *backend : {"opencl", "sycl"}) {
    const auto artifact = fixtureArtifact(artifacts / backend);
    fs::copy_file(Fixture, artifact / ("conformance-device" + Fixture.extension().string()));
    std::ofstream(artifact / "artifact.toml") << "backend = '" << backend << "'\n";
    std::ofstream(artifact / "probe-barrier") << "wait for test release";
  }
  auto environment = baseEnvironment();
  environment["CONFORMANCE_JOBS"] = "2";
  int interruption = 0;
  bool fail = false;
  SECTION("both routes complete") {}
  SECTION("a failed probe prevents case execution") {
    fail = true;
    std::ofstream(artifacts / "sycl/probe-failure") << "fail";
  }
  SECTION("SIGTERM stops every active probe") { interruption = SIGTERM; }
  SECTION("SIGINT stops every active probe") { interruption = SIGINT; }
  const auto output = directory.path / "driver.log";
  std::ofstream log(output);
  auto running = std::async(std::launch::async, [&] {
    return runProcess({Driver.string(), "run", "--direct", "--artifacts", artifacts.string(), "--output", reports.string(), "--workloads",
                       workloads.string(), "opencl", "sycl"},
                      directory.path, environment, 20s, {}, false, &log);
  });
  // The probes block until released, so progress observed here precedes their completion.
  const auto deadline = std::chrono::steady_clock::now() + 5s;
  const auto reported = [&](const std::string &progress) {
    return progress.contains("conformance discovering opencl") && progress.contains("conformance discovering sycl");
  };
  while ((!fs::exists(artifacts / "opencl/probe-ready") || !fs::exists(artifacts / "sycl/probe-ready") || !reported(read(output)))
         && std::chrono::steady_clock::now() < deadline)
    std::this_thread::sleep_for(5ms);
  CHECK(fs::exists(artifacts / "opencl/probe-ready"));
  CHECK(fs::exists(artifacts / "sycl/probe-ready"));
  CHECK(reported(read(output)));
  if (interruption) {
    int32_t parent = 0;
    int32_t first = 0;
    int32_t second = 0;
    std::ifstream(artifacts / "opencl/probe-pids") >> parent >> first;
    std::ifstream(artifacts / "sycl/probe-pids") >> parent >> second;
    REQUIRE(parent > 0);
    REQUIRE(first > 0);
    REQUIRE(second > 0);
    REQUIRE(kill(parent, interruption) == 0);
    CHECK(running.wait_for(5s) == std::future_status::ready);
    CHECK(running.get().status == 128 + interruption);
    CHECK(kill(first, 0) == -1);
    CHECK(kill(second, 0) == -1);
    return;
  }
  for (const auto *backend : {"opencl", "sycl"})
    std::ofstream(artifacts / backend / "probe-release") << "go";
  const auto result = running.get();
  INFO(read(output));
  REQUIRE((result.status == 0) == !fail);
  for (const auto &report : fs::directory_iterator(reports / "direct")) {
    if (fail) {
      CHECK_FALSE(fs::exists(report.path() / "junit.xml"));
      continue;
    }
    const auto junit = read(report.path() / "junit.xml");
    CHECK(junit.find("opencl/") < junit.find("sycl/"));
  }
}

TEST_CASE("bounded workloads exclude CUDA regressions that walk billions of indices", "[driver][workload-budget]") {
  for (const auto &name : {"compact", "full"}) {
    DYNAMIC_SECTION(name) {
      RunOptions options;
      options.workloads = fs::path(CONFORMANCE_PROFILE_DIRECTORY).parent_path() / "workloads";
      options.workload = name;
      const auto workload = loadWorkload(options);
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

TEST_CASE("full includes the same bounded CTS selections as standalone workloads", "[driver][workload-budget]") {
  RunOptions options;
  options.workloads = fs::path(CONFORMANCE_PROFILE_DIRECTORY).parent_path() / "workloads";
  options.workload = "full";
  const auto full = loadWorkload(options);
  REQUIRE(full.specs.size() == 1);
  for (const auto *name : {"opencl-cts-bounded", "sycl-cts-bounded", "vulkan-cts-bounded"}) {
    options.workload = name;
    const auto bounded = loadWorkload(options);
    REQUIRE(bounded.specs.size() == 1);
    CHECK(full.specs.front().starts_with(bounded.specs.front()));
    CHECK(full.excludePrograms == bounded.excludePrograms);
    CHECK(full.vulkanCases == bounded.vulkanCases);
  }
  options.workload = "compact";
  const auto compact = loadWorkload(options);
  for (const auto &name : compact.vulkanCases)
    CHECK(full.vulkanCases ^ contains(name));
}

TEST_CASE("iteration workloads retain every miniapp", "[driver][workload-budget]") {
  for (const auto *name : {"compact", "full", "miniapps"}) {
    DYNAMIC_SECTION(name) {
      RunOptions options;
      options.workloads = fs::path(CONFORMANCE_PROFILE_DIRECTORY).parent_path() / "workloads";
      options.workload = name;
      const auto workload = loadWorkload(options);
      Catch::TestSpecParser parser(Catch::getRegistryHub().getTagAliasRegistry());
      for (const auto &spec : workload.specs)
        parser.parse(spec);
      const auto selection = parser.testSpec();
      for (const auto *app : {"babelstream", "minibude", "cloverleaf", "tealeaf"}) {
        CAPTURE(app);
        for (const auto &program :
             {std::string(app), fmt::format("{}_omp_target", app), fmt::format("{}_kokkos", app), fmt::format("{}_sycl_acc", app)}) {
          CAPTURE(program);
          CHECK(workload.excludePrograms ^ none_match([&](const auto &pattern) { return conformance::globMatches(pattern, program); }));
        }
        CHECK((workload.includePrograms.empty()
               || workload.includePrograms ^ exists([&](const auto &pattern) { return conformance::globMatches(pattern, app); })));
        const auto identity = fmt::format("miniapps/{}/omp-target", app);
        const Catch::TestCaseInfo info("", {identity, "[miniapp]"}, {__FILE__, __LINE__});
        CHECK(selection.matches(info));
      }
      if (std::string_view(name) == "miniapps") {
        for (const auto *app : {"ggml", "imgui"}) {
          const auto program = fmt::format("miniapp_{}_vulkan", app);
          CHECK(workload.includePrograms ^ exists([&](const auto &pattern) { return conformance::globMatches(pattern, program); }));
          const auto identity = fmt::format("miniapps/{}/vulkan/example", app);
          const Catch::TestCaseInfo info("", {identity, "[miniapps]"}, {__FILE__, __LINE__});
          CHECK(selection.matches(info));
        }
        const Catch::TestCaseInfo unrelated("", {"core/copy", "[unified]"}, {__FILE__, __LINE__});
        CHECK_FALSE(selection.matches(unrelated));
      }
    }
  }
}

TEST_CASE("Google Test inventories retain parameterised cases and reject duplicates", "[driver]") {
  const auto names = gtestCaseNames("Driver.\n  Opens\n  DISABLED_Old\nParam/Copy.\n  Case/0  # GetParam() = 8\n  Case/1\n", "levelzero");
  CHECK(names == std::vector<std::string>{"Driver.Opens", "Param/Copy.Case/0", "Param/Copy.Case/1"});
  CHECK_THROWS(gtestCaseNames("  Orphan\n", "levelzero"));
  CHECK_THROWS(gtestCaseNames("Driver.\n  Duplicate\n  Duplicate\n", "levelzero"));
}

TEST_CASE("native shards select exact names including Catch metacharacters", "[driver]") {
  const auto names = listFixture();
  REQUIRE(names.size() == 2);
  for (uint32_t index = 0; index < names.size(); ++index) {
    auto arguments = catchShardArguments({"[fixture]"}, index, static_cast<uint32_t>(names.size()));
    arguments.insert(arguments.begin(), Fixture.string());
    const auto result = runProcess(arguments, Fixture.parent_path(), baseEnvironment(), 10s, {}, true);
    CHECK(result.status == (index == 0 ? 0 : 4));
  }
  CHECK_THROWS(catchShardArguments({}, 1, 1));
  CHECK_THROWS(catchShardArguments({}, 0, 0));
}

TEST_CASE("cases on one physical device run one at a time while other devices run concurrently", "[driver]") {
  const auto target = [](std::string id, std::string device, bool lock) {
    TargetExecution result{.id = std::move(id), .config = {}, .tests = {}};
    result.config.device = std::move(device);
    result.config.deviceLock = lock;
    for (uint32_t index = 0; index < 4; ++index)
      result.tests.push_back(
          {.identity = fmt::format("case{}", index), .program = "fixture", .timeoutSeconds = 10, .index = index, .count = 4});
    return result;
  };
  const std::vector<TargetExecution> targets{target("first", "pci-a", true), target("second", "pci-a", true),
                                             target("other", "pci-b", true), target("cpu", "unknown", true),
                                             target("unlocked", "pci-a", false)};
  std::mutex mutex;
  std::map<std::string, int32_t> active;
  int32_t running = 0;
  int32_t peak = 0;
  bool exclusive = true;
  const auto reports = runCases(targets, 8, [&](const ExecutionConfig &config, const ScheduledCase &test) {
    {
      const std::scoped_lock lock(mutex);
      peak = std::max(peak, ++running);
      if (config.deviceLock) {
        int32_t locked = 0;
        for (const auto &[device, count] : active)
          locked += count;
        exclusive = exclusive && active[config.device] == 0 && active["unknown"] == 0 && (config.device != "unknown" || locked == 0);
        ++active[config.device];
      }
    }
    std::this_thread::sleep_for(20ms);
    {
      const std::scoped_lock lock(mutex);
      --running;
      if (config.deviceLock) --active[config.device];
    }
    if (!config.deviceLock && test.index == 3) throw std::runtime_error("executor failure");
    return CaseResult{.status = config.deviceLock ? 0 : SkipReturnCode, .output = {}};
  });
  CHECK(exclusive);
  CHECK(peak >= 2);
  REQUIRE(reports.size() == 20);
  CHECK(reports[0].name == "first/fixture/case0");
  CHECK(reports[4].name == "second/fixture/case0");
  CHECK(reports[18].result.status == SkipReturnCode);
  CHECK(reports[19].result.status == 2);
  CHECK(reports[19].result.output.contains("executor failure"));
}

TEST_CASE("a hung case is killed with its process tree on timeout or interruption", "[driver][process]") {
  TemporaryDirectory directory;
  const auto artifact = directory.path / "artifacts/opencl";
  fs::create_directories(artifact);
  fs::copy_file(Fixture, artifact / Fixture.filename());
  fs::copy_file(Fixture, artifact / ("conformance-device" + Fixture.extension().string()));
  std::ofstream(artifact / "artifact.toml") << "backend = 'opencl'\n";
  const auto workloads = directory.path / "workloads";
  fs::create_directories(workloads);
  std::ofstream(workloads / "compact.toml") << "specs = ['[hang],[pass]']\n";
  const auto reports = directory.path / "reports";
  const auto pids = [&]() -> std::optional<std::array<int32_t, 3>> {
    if (!fs::exists(reports)) return std::nullopt;
    for (const auto &entry : fs::recursive_directory_iterator(reports))
      if (entry.path().filename() == "hang.pids") {
        std::array<int32_t, 3> result{};
        std::ifstream(entry.path()) >> result[0] >> result[1] >> result[2];
        return result;
      }
    return std::nullopt;
  };
  SECTION("timeout") {
    std::ofstream(artifact / "test-programs.txt") << Fixture.stem().string() << "\t1\tcatch2\n";
    RunOptions options;
    options.direct = true;
    options.artifacts = artifact.parent_path();
    options.output = reports;
    options.workloads = workloads;
    options.selectors = {"opencl"};
    CHECK(run(options) == 1);
    const auto hung = pids();
    if (!hung) FAIL("hung process ids were not recorded");
    CHECK(exited((*hung)[1]));
    CHECK(exited((*hung)[2]));
    for (const auto &report : fs::directory_iterator(reports / "direct")) {
      const auto junit = read(report.path() / "junit.xml");
      CHECK(junit.contains("hung case\" classname=\"opencl\""));
      CHECK(junit.contains("<failure message=\"timed out after 1 s (exit status 124)\"/>"));
      CHECK(junit.contains("failures=\"1\""));
      bool recorded = false;
      for (const auto &entry : fs::recursive_directory_iterator(report.path()))
        if (entry.path().filename() == "output.log" && read(entry.path()).contains("process tree timed out after 1 s")) recorded = true;
      CHECK(recorded);
    }
  }
  SECTION("SIGTERM") {
    std::ofstream(artifact / "test-programs.txt") << Fixture.stem().string() << "\t600\tcatch2\n";
    auto running = std::async(std::launch::async, [&] {
      return runProcess({Driver.string(), "run", "--direct", "--artifacts", artifact.parent_path().string(), "--output", reports.string(),
                         "--workloads", workloads.string(), "opencl"},
                        directory.path, baseEnvironment(), 30s, {}, true);
    });
    const auto deadline = std::chrono::steady_clock::now() + 10s;
    auto hung = pids();
    while (!hung && std::chrono::steady_clock::now() < deadline) {
      std::this_thread::sleep_for(10ms);
      hung = pids();
    }
    if (!hung) FAIL("hung process ids were not recorded");
    const auto start = std::chrono::steady_clock::now();
    REQUIRE(kill((*hung)[0], SIGTERM) == 0);
    const auto result = running.get();
    INFO(result.output);
    CHECK(result.status == 128 + SIGTERM);
    CHECK(std::chrono::steady_clock::now() - start < 5s);
    CHECK(exited((*hung)[1]));
    CHECK(exited((*hung)[2]));
  }
}

TEST_CASE("isolated routes deduplicate physical variants by artifact ABI", "[driver]") {
  using conformance::profile::ResolvedTarget;
  ResolvedTarget first{
      .id = "first", .backend = "hip", .architecture = "gfx1030", .environment = {{"CONFORMANCE_DEVICE", "a"}, {"VALUE", "1"}}};
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
  ResolvedTarget ptx{.id = "cuda-ptx", .backend = "cuda", .architecture = "sm_89", .environment = {{"CONFORMANCE_CUDA_CODE", "ptx"}}};
  ResolvedTarget sass{.id = "cuda-sass", .backend = "cuda", .architecture = "sm_89", .environment = {{"CONFORMANCE_CUDA_CODE", "sass"}}};
  auto clang = ptx;
  clang.id = "cuda-ptx-clang";
  clang.compiler = "clang";
  const std::vector<ResolvedTarget> targets{first, duplicate, selector, environment, architecture, ptx, sass, clang};
  std::vector<std::string> isolated;
  for (const auto &target : uniqueRoutes(targets, true))
    isolated.push_back(target.id);
  CHECK(isolated == std::vector<std::string>{"first", "architecture", "cuda-ptx", "cuda-sass", "cuda-ptx-clang"});
  CHECK(uniqueRoutes(targets, false).size() == targets.size());
}

TEST_CASE("failed tests retain both streams in full and report only the output path", "[driver]") {
  TemporaryDirectory const directory;
  const auto artifact = fixtureArtifact(directory.path / "artifact");
  ExecutionConfig const config{.directory = artifact.string(),
                               .reportDirectory = directory.path.string(),
                               .device = conformance::physicalDeviceKey(conformance::makePciDevice(1, 0, 0, 0)),
                               .environment = baseEnvironment(),
                               .launcher = {},
                               .skips = {},
                               .specs = {"[noisy]"},
                               .deviceLock = false};
  const auto result =
      executeCase(config, {.identity = "noisy failure", .program = Fixture.stem().string(), .timeoutSeconds = 10, .index = 0, .count = 1});
  INFO(result.output);
  CHECK(result.status != 0);
  CHECK(result.output.size() < 1000);
  CHECK(!result.output.contains("first diagnostic"));
  CHECK(result.output.contains("full output:"));
  const auto recorded = read(directory.path / Fixture.stem() / "0/output.log");
  CHECK(recorded.size() > 60000);
  CHECK(recorded.contains("first diagnostic"));
  CHECK(recorded.contains("last diagnostic"));
  CHECK(recorded.contains("stderr diagnostic"));
}

TEST_CASE("execution preserves skips environment and distinct report paths", "[driver]") {
  TemporaryDirectory const directory;
  const auto artifact = fixtureArtifact(directory.path / "artifact");
  const auto names = listFixture();
  ExecutionConfig config{.directory = artifact.string(),
                         .reportDirectory = directory.path.string(),
                         .device = conformance::physicalDeviceKey(conformance::makePciDevice(1, 0, 0, 0)),
                         .environment = baseEnvironment(),
                         .launcher = {},
                         .skips = {},
                         .specs = {"[fixture]"},
                         .deviceLock = false};
  config.environment["EXPECTED_ENVIRONMENT"] = "value with spaces";
  config.environment["PROPAGATED_ENVIRONMENT"] = "value with spaces";
  config.environment["CONFORMANCE_TARGET_ARCHITECTURE"] = "";
  CHECK(executeCase(config, {names[0], Fixture.stem().string(), 10, 0, 2}).status == 0);
  CHECK(executeCase(config, {names[1], Fixture.stem().string(), 10, 1, 2}).status == SkipReturnCode);
  CHECK(fs::is_regular_file(directory.path / Fixture.stem() / "0/result.txt"));
  CHECK(fs::is_directory(directory.path / Fixture.stem() / "1"));
  config.skips = {names[0]};
  CHECK(executeCase(config, {names[0], Fixture.stem().string(), 10, 0, 2}).status == SkipReturnCode);
  config.skips = {"blas/*", "edge/blas/*"};
  CHECK(executeCase(config, {"blas/f32/compact/asum", "unified", 10, 0, 2}).status == SkipReturnCode);
  CHECK(executeCase(config, {"edge/blas/f32/compact/gemm", "unified", 10, 0, 2}).status == SkipReturnCode);
  CHECK_FALSE(fs::exists(directory.path / "skips.log"));
}

TEST_CASE("runtime environment reserves owned keys and excludes inherited SDK paths", "[driver]") {
  TemporaryDirectory const directory;
  auto env = baseEnvironment();
  env["LD_LIBRARY_PATH"] = "/host/sdk/lib";
  env["CUDA_HOME"] = "/host/sdk";
  env["ROCBLAS_TENSILE_LIBPATH"] = "stale";
  env["OMP_TARGET_OFFLOAD"] = "DISABLED";
  prepareRuntimeEnvironment(env, directory.path / "artifact", directory.path / "support", "opencl");
  CHECK(env.at("OMP_TARGET_OFFLOAD") == "MANDATORY");
  env["CONFORMANCE_VULKAN_CTS_TIMEOUT"] = "7200";
  prepareRuntimeEnvironment(env, directory.path / "artifact", directory.path / "support", "vulkan");
  applySuiteIsolatedEnvironment(env, "vulkan");
  CHECK(env.at("CONFORMANCE_VULKAN_CTS_TIMEOUT") == "7200");
  CHECK_FALSE(env.contains("CONFORMANCE_DEVICE"));
  CHECK_FALSE(env.contains("CUDA_HOME"));
  CHECK_FALSE(env.contains("ROCBLAS_TENSILE_LIBPATH"));
  CHECK(env.at("LD_LIBRARY_PATH") == (directory.path / "artifact/lib").string() + ":" + (directory.path / "support/lib").string());
  for (const auto *key :
       {"CONFORMANCE_TARGET_ARCHITECTURE", "CONFORMANCE_REPORT_DIRECTORY", "CONFORMANCE_TEST_IDENTITY", "CONFORMANCE_PHYSICAL_DEVICE",
        "CUDA_VISIBLE_DEVICES", "HIP_VISIBLE_DEVICES", "ROCR_VISIBLE_DEVICES", "CL_DEVICE_INDEX", "ONEAPI_DEVICE_SELECTOR"})
    CHECK(conformance::reservedEnvironmentKey(key));
  CHECK_FALSE(conformance::reservedEnvironmentKey("CONFORMANCE_CUDA_CODE"));
  CHECK_FALSE(conformance::validEnvironmentEntry("BAD=KEY", "value"));
  CHECK_FALSE(conformance::validEnvironmentEntry("KEY", std::string("bad\0value", 9)));
  ExecutionConfig config;
  applyDeviceProbe(config, {.device = conformance::physicalDeviceKey(conformance::makePciDevice(2, 0, 0, 0)),
                            .environment = {{"CL_DEVICE_INDEX", "2"}}});
  CHECK(config.environment.at("CL_DEVICE_INDEX") == "2");
  CHECK(config.environment.at("CONFORMANCE_PHYSICAL_DEVICE") == config.device);
  CHECK_THROWS(applyDeviceProbe(config, {config.device, {{"LD_PRELOAD", "unexpected"}}}));
}

TEST_CASE("Fire CLI validates options and preserves raw launcher arguments", "[driver]") {
  char program[] = "conformance";
  char run[] = "run";
  char flag[] = "--launcher";
  char separator[] = "--";
  char command[] = "launcher";
  char space[] = "two words";
  char dash[] = "--child-option";
  char *raw[]{program, run, flag, separator, command, space, dash};
  CHECK(launcherArguments(std::span<char *const>(raw, 7)) == std::vector<std::string>{"launcher", "two words", "--child-option"});
  auto env = baseEnvironment();
  CHECK(runProcess({Driver.string(), "run", "--unknown-option"}, Fixture.parent_path(), env, 10s, {}, true).status != 0);
  CHECK(runProcess({Driver.string(), "run", "--launcher"}, Fixture.parent_path(), env, 10s, {}, true).status != 0);
}

TEST_CASE("direct CLI rejects profile options and reports missing artifacts without profile lookup", "[driver]") {
  TemporaryDirectory directory;
  const auto invoke = [&](std::vector<std::string> extra) {
    std::vector<std::string> args{Driver.string(),
                                  "run",
                                  "--direct",
                                  "--artifacts",
                                  (directory.path / "artifacts").string(),
                                  "--output",
                                  (directory.path / "output").string(),
                                  "--workloads",
                                  (directory.path / "workloads").string()};
    args.insert(args.end(), extra.begin(), extra.end());
    return runProcess(args, directory.path, baseEnvironment(), 10s, {}, true);
  };
  for (const auto &flag : {"--machine", "--profiles"}) {
    const auto result = invoke({flag, "nonexistent", "vulkan"});
    CHECK(result.status != 0);
    CHECK(result.error.contains("--direct cannot use"));
  }
  const auto empty = invoke({});
  CHECK(empty.status != 0);
  CHECK(empty.error.contains("requires explicit"));
  const auto missing = invoke({"vulkan"});
  CHECK(missing.status != 0);
  CHECK(missing.error.contains("artifact metadata"));
  CHECK(missing.error.contains("vulkan"));
  CHECK_FALSE(fs::exists(directory.path / "output"));
}

TEST_CASE("physical device keys use canonical encoding", "[driver]") {
  CHECK(conformance::physicalDeviceKey(conformance::makePciDevice(0x12345678, 0x9a, 0xbc, 0xde)) == "pci-123456789abcde000000000000000000");
  CHECK(conformance::physicalDeviceKey({conformance::PhysicalDeviceScheme::Unknown, {}}) == "unknown");
  CHECK(conformance::validPhysicalDeviceKey("unknown"));
  CHECK_FALSE(conformance::validPhysicalDeviceKey("reg-00000000000000000000000000000000"));
  CHECK_FALSE(conformance::validPhysicalDeviceKey("host-00000000000000000000000000000000"));
}

TEST_CASE("device selector supports glob patterns", "[driver]") {
  CHECK(conformance::globMatches("*Arc?A750*", "Intel Arc A750 Graphics"));
  CHECK_FALSE(conformance::globMatches("*AMD*", "Intel Arc A750 Graphics"));
  CHECK(conformance::globMatches("", ""));
  CHECK_FALSE(conformance::globMatches("", "a"));
  CHECK(conformance::globMatches("*", ""));
  CHECK(conformance::globMatches("**a**b**", "xaxbx"));
  CHECK(conformance::globMatches("a*b*c", "abbcbc"));
  CHECK_FALSE(conformance::globMatches("a*b*c", "abcb"));
  CHECK(conformance::globMatches("?*?", "ab"));
  CHECK_FALSE(conformance::globMatches("?*?", "a"));
  CHECK_FALSE(conformance::globMatches("a?", "a"));
  CHECK(conformance::globMatches("a?c", "a*c"));
  CHECK_FALSE(conformance::globMatches("a*c", "A-C"));
}

TEST_CASE("driver job count and physical keys reject invalid values", "[driver]") {
  CHECK(configuredJobs("", 0) == 1);
  CHECK(configuredJobs("7", 12) == 7);
  for (const auto *invalid : {"0", "-1", "1x", "4294967296"})
    CHECK_THROWS(configuredJobs(invalid, 1));
  CHECK_FALSE(conformance::validPhysicalDeviceKey("pci-xyz"));
  CHECK(conformance::validPhysicalDeviceKey("pci-0123456789abcdef0123456789abcdef"));
}

TEST_CASE("emulated timeouts grow on smaller hosts and never drop below the recorded budget", "[driver]") {
  CHECK(scaledTimeout(60, 32) == 60);
  CHECK(scaledTimeout(60, 64) == 60);
  CHECK(scaledTimeout(60, 256) == 60);
  CHECK(scaledTimeout(60, 16) == 120);
  CHECK(scaledTimeout(60, 1) == 240);
  CHECK(scaledTimeout(60, 0) == 240);
  CHECK(scaledTimeout(1, 24) == 1);
}

TEST_CASE("timeout kills a TERM-resistant process tree within a fixed bound", "[driver][process]") {
  const auto start = std::chrono::steady_clock::now();
  const auto result = runProcess({Fixture.string(), "--process-tree"}, Fixture.parent_path(), baseEnvironment(), 250ms, {}, true);
  CHECK(result.status == 124);
  CHECK(std::chrono::steady_clock::now() - start < 5s);
  CHECK(exited(static_cast<int32_t>(std::stoi(result.output))));
}

TEST_CASE("a concurrent timeout leaves another child's output and exit status intact", "[driver][process]") {
  auto stalled = std::async(std::launch::async, [&] {
    return runProcess({Fixture.string(), "--process-tree"}, Fixture.parent_path(), baseEnvironment(), 100ms, {}, true);
  });
  auto inventory = std::async(std::launch::async, [&] { return listFixture(); });
  CHECK(inventory.get().size() == 2);
  CHECK(stalled.get().status == 124);
  CHECK(listFixture().size() == 2);
}

TEST_CASE("only isolated SYCL handler matrices receive the longer timeout", "[driver]") {
  CHECK(suiteTimeout("sycl_cts_handler", 120, true) == CONFORMANCE_SYCL_HANDLER_TIMEOUT_SECONDS);
  CHECK(suiteTimeout("sycl_cts_handler", 120, false) == 120);
  CHECK(suiteTimeout("sycl_cts_handler", 900, true) == 900);
  CHECK(suiteTimeout("sycl_cts_buffer", 120, true) == 120);
}

TEST_CASE("isolated OpenCL launchers preserve inherited CTS workload settings", "[driver]") {
  const std::map<std::string, std::string> configured{{"CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS", "37"},
                                                      {"CONFORMANCE_OPENCL_CTS_ATOMIC_THREADS", "64"},
                                                      {"CONFORMANCE_OPENCL_CTS_ELEMENTS", "16384"},
                                                      {"CONFORMANCE_OPENCL_CTS_WIMPY", "0"},
                                                      {"CONFORMANCE_OPENCL_CTS_TIMEOUT", "30"}};
  auto environment = [&] {
    std::list<conformance_test::Environment> overrides;
    for (const auto &[key, value] : configured)
      overrides.emplace_back(key.c_str(), value.c_str());
    return baseEnvironment();
  }();
  applySuiteIsolatedEnvironment(environment, "opencl");
  for (const auto &[key, value] : configured)
    CHECK(environment.at(key) == value);

  environment["CONFORMANCE_OPENCL_CTS_ELEMENTS"] = "";
  applySuiteIsolatedEnvironment(environment, "opencl");
  CHECK(environment.at("CONFORMANCE_OPENCL_CTS_ELEMENTS").empty());
}

TEST_CASE("dEQP inventories and QPA results preserve failures and skips", "[driver]") {
  const auto names = [](std::string listing) {
    std::istringstream input(std::move(listing));
    return deqpCaseNames(input);
  };
  CHECK(names("GROUP: dEQP-VK.api\nTEST: dEQP-VK.api.test\n") == std::vector<std::string>{"dEQP-VK.api.test"});
  CHECK_THROWS(names("GROUP: dEQP-VK.api\n"));
  CHECK_THROWS(names("TEST: dEQP-VK.a\nTEST: dEQP-VK.a\n"));
  const auto qpa = [](const std::string &status) {
    return "#beginTestCaseResult dEQP-VK.a\n<TestCaseResult CasePath=\"dEQP-VK.a\" Version=\"0.3.4\">"
           "<Result StatusCode=\""
           + status + "\">details</Result></TestCaseResult>\n#endTestCaseResult\n";
  };
  CHECK(deqpResult(qpa("Pass"), "dEQP-VK.a").status == 0);
  CHECK(deqpResult(qpa("Fail"), "dEQP-VK.a").status == 1);
  CHECK(deqpResult(qpa("NotSupported"), "dEQP-VK.a").status == 4);
  CHECK(deqpResult(qpa("QualityWarning"), "dEQP-VK.a").status == 0);
  CHECK(deqpResult(qpa("QualityWarning"), "dEQP-VK.a").statusCode == "QualityWarning");
  CHECK(deqpResult(qpa("CompatibilityWarning"), "dEQP-VK.a").status == 0);
  CHECK(deqpResult(qpa("Waiver"), "dEQP-VK.a").status == 1);
  CHECK_THROWS(deqpResult(qpa("Pass"), "dEQP-VK.b"));
  CHECK_THROWS(deqpResult(qpa("Pass") + qpa("Pass"), "dEQP-VK.a"));
  CHECK_THROWS(deqpResult("", "dEQP-VK.a"));
  auto truncated = qpa("Pass");
  truncated.erase(truncated.find("#endTestCaseResult"));
  CHECK_THROWS(deqpResult(truncated, "dEQP-VK.a"));
}

TEST_CASE("dEQP statuses go to the case log and failure record instead of the console", "[driver]") {
  TemporaryDirectory const directory;
  const auto artifact = directory.path / "artifact";
  fs::create_directories(artifact);
  fs::copy_file(Fixture, artifact / Fixture.filename());
  std::ofstream(artifact / "test-programs.txt") << Fixture.stem().string() << "\t10\tdeqp\n";
  auto environment = baseEnvironment();
  environment["CONFORMANCE_SUPPORT_ROOT"] = directory.path.string();
  environment["CONFORMANCE_VULKAN_DEVICE_ID"] = "1";
  const ExecutionConfig config{.directory = artifact.string(),
                               .reportDirectory = directory.path.string(),
                               .device = {},
                               .environment = environment,
                               .launcher = {},
                               .skips = {},
                               .specs = {"*"},
                               .deviceLock = false};
  std::ostringstream const console;
  auto *const standard = std::cout.rdbuf(console.rdbuf());
  const auto passed =
      executeCase(config, {.identity = "dEQP-VK.pass", .program = Fixture.stem().string(), .timeoutSeconds = 10, .index = 0, .count = 2});
  const auto failed =
      executeCase(config, {.identity = "dEQP-VK.fail", .program = Fixture.stem().string(), .timeoutSeconds = 10, .index = 1, .count = 2});
  std::cout.rdbuf(standard);
  INFO(failed.output);
  CHECK(console.str().empty());
  CHECK(passed.status == 0);
  CHECK(failed.status == 1);
  CHECK(read(directory.path / Fixture.stem() / "0/output.log").contains("dEQP dEQP-VK.pass: Pass"));
  CHECK(read(directory.path / Fixture.stem() / "1/output.log").contains("dEQP dEQP-VK.fail: Fail"));
  CHECK(failed.output.contains("dEQP dEQP-VK.fail: Fail"));
  CHECK(failed.output.contains("full output:"));
}

TEST_CASE("Vulkan exact case files deduplicate literals and reject patterns") {
  CHECK(deqpExactCaseList({"dEQP-VK.b", "dEQP-VK.a", "dEQP-VK.b"}) == "dEQP-VK.a\ndEQP-VK.b\n");
  for (const auto &invalid : {"dEQP-VK.a.*", "dEQP-VK.a?", "dEQP-VK.a\ndEQP-VK.b", "dEQP-VK.a,dEQP-VK.b", ""})
    CHECK_THROWS(deqpExactCaseList({invalid}));
  CHECK_THROWS(deqpExactCaseList({}));
}

TEST_CASE("Vulkan mustpass manifests retain official cases and diagnose invalid selections") {
  TemporaryDirectory const directory;
  const auto root = directory.path / "data";
  fs::create_directories(root / "mustpass/groups");
  std::ofstream(root / "mustpass/default.txt") << "# Pinned inventory\ngroups/a.txt\ngroups/b.txt\n";
  std::ofstream(root / "mustpass/groups/a.txt") << "dEQP-VK.a.one\ndEQP-VK.a.two\n";
  std::ofstream(root / "mustpass/groups/b.txt") << "dEQP-VK.b.one\n";
  const std::vector<std::string> all{"dEQP-VK.a.one", "dEQP-VK.a.two", "dEQP-VK.b.one"};
  CHECK(deqpMustpassCases(root, "mustpass/default.txt", {}) == all);
  CHECK(deqpMustpassCases(root, "mustpass/default.txt", {"dEQP-VK.a.*", "dEQP-VK.b.one"}) == all);
  CHECK(deqpMustpassCases(root, "mustpass/default.txt", {"dEQP-VK.a.one", "dEQP-VK.a.one"}) == std::vector<std::string>{"dEQP-VK.a.one"});
  CHECK_THROWS(deqpMustpassCases(root, "mustpass/default.txt", {"dEQP-VK.missing.*"}));
  CHECK_THROWS(deqpMustpassCases(root, "mustpass/default.txt", {"dEQP-VK.a.one", ""}));
  std::ofstream(root / "mustpass/groups/b.txt") << "dEQP-VK.a.one\n";
  CHECK_THROWS(deqpMustpassCases(root, "mustpass/default.txt", {}));
  std::ofstream(root / "mustpass/groups/b.txt") << "../default.txt\n";
  CHECK_THROWS(deqpMustpassCases(root, "mustpass/default.txt", {}));
  std::ofstream(directory.path / "outside.txt") << "dEQP-VK.outside\n";
  CHECK_THROWS(deqpMustpassCases(root, "../outside.txt", {}));
  CHECK_THROWS(deqpMustpassCases(root, directory.path / "outside.txt", {}));
  std::ofstream(root / "mustpass/groups/b.txt") << "dEQP-VK.invalid.*\n";
  CHECK_THROWS(deqpMustpassCases(root, "mustpass/default.txt", {}));
}

TEST_CASE("workload inheritance replaces explicit fields and rejects invalid ancestry", "[driver]") {
  TemporaryDirectory const directory;
  std::ofstream(directory.path / "base.toml") << "specs = ['[fixture]']\ninclude_programs = ['alpha*']\nvulkan_cases = ['dEQP-VK.a']\n";
  std::ofstream(directory.path / "child.toml") << "extends = 'base'\ninclude_programs = []\n";
  RunOptions options;
  options.workloads = directory.path;
  options.workload = "child";
  const auto child = loadWorkload(options);
  CHECK(child.specs == std::vector<std::string>{"[fixture]"});
  CHECK(child.includePrograms.empty());
  CHECK(child.vulkanCases == std::vector<std::string>{"dEQP-VK.a"});
  std::ofstream(directory.path / "base.toml") << "extends = 'child'\n";
  CHECK_THROWS(loadWorkload(options));
  std::ofstream(directory.path / "base.toml") << "extends = '../outside'\n";
  CHECK_THROWS(loadWorkload(options));
  std::ofstream(directory.path / "base.toml") << "extends = 'absent'\n";
  CHECK_THROWS(loadWorkload(options));
}

TEST_CASE("large discovery logs preserve late cases and capped captures fail explicitly", "[driver]") {
  TemporaryDirectory const directory;
  const auto path = directory.path / "inventory.log";
  std::ofstream log(path);
  const std::vector<std::string> command{Fixture.string(), "--large-inventory"};
  REQUIRE(runProcess(command, directory.path, baseEnvironment(), 30s, {}, false, &log).status == 0);
  log.close();
  INFO("inventory log: " << std::error_code(errno, std::generic_category()).message());
  REQUIRE(log.good());
  std::ifstream input(path);
  CHECK(deqpCaseNames(input) == std::vector<std::string>{"dEQP-VK.last.case"});
  CHECK_THROWS_WITH(runProcess(command, directory.path, baseEnvironment(), 30s, {}, true),
                    "process output exceeded the 128 MiB capture limit: " + Fixture.string());
}

TEST_CASE("vendor BLAS payload policy preserves available operations and unknown packages", "[driver]") {
  const auto cuda = [](std::string_view architecture, std::string_view operation, std::string_view code = "ptx",
                       std::string_view library = "libcublas.so.13.6.0.2") {
    return suiteBlasSkip("cuda", architecture, code, library, "unified", operation);
  };
  CHECK_FALSE(cuda("sm_89", "blas/f32/compact/gemm").empty());
  CHECK(cuda("sm_89", "blas/f32/compact/gemm", "sass").empty());
  CHECK(cuda("sm_89", "blas/f32/compact/gemm", "ptx", "libcublas.so.13.7.0.0").empty());
  CHECK(cuda("sm_89", "blas/f32/full/gemm").empty());
  CHECK(cuda("sm_89", "blas/f64/compact/gemm").empty());
  CHECK(cuda("sm_89", "blas/f32/compact/axpy").empty());
  CHECK(cuda("sm_89", "blas/f32/compact/gemm_strided_batched").empty());
  CHECK(cuda("sm_100", "blas/f32/compact/gemm_nt").empty());
  CHECK_FALSE(cuda("sm_100", "blas/f32/compact/gemm_tn").empty());
  CHECK_FALSE(cuda("sm_90a", "blas/f32/compact/gemm").empty());
  CHECK_FALSE(cuda("sm_103f", "blas/f32/compact/gemm_tn").empty());
  CHECK(cuda("sm_120a", "blas/f32/compact/gemm", "sass").empty());
  CHECK(cuda("sm_70", "blas/f32/compact/gemm", "ptx", "libcublas.so.11.11.3.6").empty());
  CHECK(cuda("sm_50", "blas/f32/compact/gemm_strided_batched", "ptx", "libcublas.so.11.11.3.6").empty());
  CHECK_FALSE(cuda("sm_35", "blas/f32/compact/gemm_strided_batched", "ptx", "libcublas.so.11.11.3.6").empty());
  CHECK_FALSE(suiteBlasSkip("hip", "gfx906", "", "librocblas.so.5.1.70101", "unified", "blas/f32/compact/axpy").empty());
  CHECK(suiteBlasSkip("hip", "gfx906", "", "librocblas.so.5.1.70101", "unified", "blas/f32/compact/gemm").empty());
  CHECK(suiteBlasSkip("hip", "gfx908", "", "librocblas.so.5.1.70101", "unified", "blas/f32/compact/axpy").empty());
  CHECK(suiteBlasSkip("hip", "gfx906", "", "librocblas.so.6", "unified", "blas/f32/compact/axpy").empty());
  CHECK(suiteBlasSkip("cuda", "sm_89", "ptx", "libcublas.so.13.6.0.2", "another-program", "blas/f32/compact/gemm").empty());
}

TEST_CASE("vendor BLAS skips require the actual staged library and apply with or without a launcher", "[driver]") {
  TemporaryDirectory const directory;
  const auto support = directory.path / "support";
  const auto artifact = directory.path / "artifact";
  fs::create_directories(support / "lib");
  fs::create_directories(artifact / "lib");
  const auto version = support / "lib/libcublas.so.13.6.0.2";
  std::ofstream(version) << "fixture";
  fs::create_symlink(version.filename(), support / "lib/libcublas.so");
  ExecutionConfig config;
  config.directory = artifact.string();
  config.device = conformance::physicalDeviceKey(conformance::makePciDevice(1, 0, 0, 0));
  config.environment = {{"CONFORMANCE_TARGET_BACKEND", "cuda"},
                        {"CONFORMANCE_TARGET_ARCHITECTURE", "sm_89"},
                        {"CONFORMANCE_CUDA_CODE", "ptx"},
                        {"CONFORMANCE_SUPPORT_ROOT", support.string()}};
  const ScheduledCase test{.identity = "blas/f32/compact/gemm", .program = "unified", .timeoutSeconds = 10, .index = 0, .count = 1};
  const auto execute = [&] { return executeCase(config, test).status; };
  CHECK(execute() == SkipReturnCode);
  config.launcher = {"must-not-be-launched"};
  CHECK(execute() == SkipReturnCode);
  SECTION("a missing library remains an error") { fs::remove(version); }
  SECTION("an unknown package remains an error") {
    fs::remove(support / "lib/libcublas.so");
    const auto unknown = support / "lib/libcublas.so.99";
    std::ofstream(unknown) << "fixture";
    fs::create_symlink(unknown.filename(), support / "lib/libcublas.so");
  }
  SECTION("an artifact-local library takes precedence over shared support") {
    std::ofstream(artifact / "lib/libcublas.so") << "different package";
  }
  SECTION("SASS keeps executing") { config.environment["CONFORMANCE_CUDA_CODE"] = "sass"; }
  CHECK_THROWS(execute());
}

TEST_CASE("compiler artifacts require matching metadata and backend support", "[driver]") {
  TemporaryDirectory const directory;
  RunOptions options;
  options.direct = true;
  options.artifacts = directory.path / "artifacts";
  options.output = directory.path / "reports";
  options.workloads = directory.path / "workloads";
  options.selectors = {"cuda-ptx/sm_80/gcc"};
  fs::create_directories(options.workloads);
  std::ofstream(options.workloads / "compact.toml") << "specs = ['[skip]']\n";
  const auto root = options.artifacts / "cuda/gcc";
  const auto artifact = fixtureArtifact(root / "sm_80");
  fs::copy_file(Fixture, artifact / ("conformance-device" + Fixture.extension().string()));
  std::string support = "support";
  std::string compiler = "compiler = 'gcc'\n";
  bool mismatch = false;
  SECTION("matching compiler selects its support directory") {}
  SECTION("SDK-specific support is shared at the backend root") { support = "support-11.8"; }
  SECTION("absent compiler means vendor and cannot satisfy GCC") {
    compiler.clear();
    mismatch = true;
  }
  SECTION("different compiler cannot satisfy GCC") {
    compiler = "compiler = 'clang'\n";
    mismatch = true;
  }
  SECTION("invalid compiler is rejected") {
    compiler = "compiler = '../gcc'\n";
    mismatch = true;
  }
  fs::create_directories(options.artifacts / "cuda" / support / "lib");
  std::ofstream(artifact / "artifact.toml") << "backend = 'cuda'\narchitecture = 'sm_80'\nsupport = '" << support << "'\n" << compiler;
  if (mismatch) {
    CHECK_THROWS_WITH(run(options),
                      "conformance artifact metadata does not match requested cuda: " + (artifact / "artifact.toml").string());
    CHECK_FALSE(fs::exists(options.output));
  } else {
    CHECK(run(options) == 0);
    for (const auto &entry : fs::directory_iterator(options.output / "direct")) {
      const auto config = read(entry.path() / "cuda-ptx-sm_80-gcc/environment.txt");
      CHECK(config.contains((options.artifacts / "cuda" / support / "lib").string()));
      CHECK(config.contains("MANDATORY"));
    }
  }
}
