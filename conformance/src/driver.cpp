#include "driver.hpp"

#include <algorithm>
#include <charconv>
#include <fstream>
#include <iostream>
#include <limits>
#include <memory>
#include <ranges>
#include <set>
#include <stdexcept>
#include <thread>
#include <tuple>

#include <aspartame/all.hpp>
#include <fmt/format.h>
#include <glaze/json.hpp>
#include <glaze/toml.hpp>

#include "conformance/device.hpp"
#include "environment.hpp"
#include "suite_policy.hpp"

#include "device_lock.hpp"

namespace conformance::driver {
using namespace aspartame;
namespace fs = std::filesystem;
using namespace std::chrono_literals;
struct ArtifactMetadata {
  std::string backend, architecture, support;
};
struct TestProgram {
  std::string name;
  fs::path path;
  uint32_t timeout_seconds;
};
struct CatchCase {
  std::string name;
};
struct CatchListing {
  std::vector<CatchCase> tests;
};
struct CatchReport {
  uint32_t version = 0;
  CatchListing listings;
};

namespace {
bool valid_identifier(std::string_view value) {
  return !value.empty() && std::ranges::all_of(value, [](char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-';
  });
}
void validate_run_options(RunOptions &c) {
  if (c.profiles.empty() || c.artifacts.empty() || c.output.empty())
    throw std::invalid_argument("run requires --profiles, --artifacts and --output");
  if (!valid_identifier(c.workload) || c.workload == "." || c.workload == "..") throw std::invalid_argument("invalid workload name");
  if (c.isolated_launcher && c.launcher.empty()) throw std::invalid_argument("--isolated-launcher requires --launcher -- command ...");
  c.profiles = fs::absolute(c.profiles).lexically_normal();
  c.artifacts = fs::absolute(c.artifacts).lexically_normal();
  c.output = fs::absolute(c.output).lexically_normal();
}
} // namespace
int32_t configured_jobs(std::optional<std::string_view> configured, uint32_t hardware_concurrency) {
  if (!configured || configured->empty()) return static_cast<int32_t>(std::max(1u, hardware_concurrency));
  uint32_t result = 0;
  const auto [end, error] = std::from_chars(configured->data(), configured->data() + configured->size(), result);
  if (error != std::errc{} || end != configured->data() + configured->size() || result == 0 || result > INT32_MAX)
    throw std::invalid_argument("CONFORMANCE_JOBS must be a positive 32-bit integer");
  return static_cast<int32_t>(result);
}
std::vector<std::string> catch_case_names(std::string_view json, std::string_view program) {
  CatchReport inventory;
  constexpr auto options = [] {
    auto o = glz::opts{};
    o.error_on_unknown_keys = false;
    return o;
  }();
  if (const auto error = glz::read<options>(inventory, json); error)
    throw std::runtime_error("invalid Catch JSON inventory from " + std::string(program) + ": " + glz::format_error(error, json));
  if (inventory.version != 1) throw std::runtime_error("unsupported Catch JSON inventory version from " + std::string(program));
  std::vector<std::string> result;
  std::set<std::string> names;
  for (auto &test : inventory.listings.tests) {
    if (test.name.empty() || test.name.find('\0') != std::string::npos || !names.insert(test.name).second)
      throw std::runtime_error("invalid or duplicate Catch test from " + std::string(program));
    result.push_back(std::move(test.name));
  }
  return result;
}
std::vector<std::string> catch_shard_arguments(const std::vector<std::string> &specs, uint32_t index, uint32_t count) {
  if (count == 0 || index >= count) throw std::invalid_argument("invalid Catch shard");
  return concat(std::vector<std::string>{"--order", "lex", "--shard-count", fmt::to_string(count), "--shard-index", fmt::to_string(index)},
                specs);
}
std::vector<std::string> launcher_arguments(bool enabled, std::span<char *const> arguments) {
  if (!enabled) return {};
  const auto separator = std::ranges::find(arguments, std::string_view("--"));
  if (separator == arguments.end() || separator + 1 == arguments.end())
    throw std::invalid_argument("--launcher requires -- command [arguments ...]");
  return {separator + 1, arguments.end()};
}
std::vector<profile::ResolvedTarget> unique_routes(const std::vector<profile::ResolvedTarget> &targets, bool isolated) {
  std::vector<profile::ResolvedTarget> result;
  std::set<std::pair<std::string, std::optional<std::string>>> seen;
  for (const auto &target : targets) {
    // An isolated launcher replaces the native provider and device. Its route
    // is the artifact ABI, not the machine-specific selector, environment, or
    // lock policy used to reach the physical device.
    if (!isolated || seen.emplace(target.backend, target.architecture).second) result.push_back(target);
    else std::cout << "conformance: identical isolated route " << target.id << " omitted before discovery\n";
  }
  return result;
}
bool valid_artifact_key(std::string_view value) {
  return !value.empty() && valid_identifier(value) && value.front() != '.' && value.front() != '_' && value.front() != '-';
}
fs::path artifact_directory(const RunOptions &configuration, const conformance::profile::ResolvedTarget &target) {
  auto result = configuration.artifacts / target.backend;
  if (target.architecture) result /= *target.architecture;
  return result;
}
ArtifactMetadata validate_artifact(const fs::path &artifact, const conformance::profile::ResolvedTarget &target) {
  const auto file = artifact / "artifact.toml";
  ArtifactMetadata metadata;
  std::string buffer;
  if (const auto error = glz::read_file_toml(metadata, file.string(), buffer); error)
    throw std::runtime_error("cannot read conformance artifact metadata " + file.string() + ":\n" + glz::format_error(error, buffer));
  const auto architecture = target.architecture.value_or("");
  if (metadata.backend != target.backend || metadata.architecture != architecture)
    throw std::runtime_error("conformance artifact metadata does not match requested " + target.backend + ": " + file.string());
  if (!metadata.support.empty() && !valid_artifact_key(metadata.support))
    throw std::runtime_error("conformance artifact has an invalid support key: " + file.string());
  return metadata;
}
fs::path support_directory(const RunOptions &configuration, const conformance::profile::ResolvedTarget &target,
                      const ArtifactMetadata &metadata) {
  if (target.architecture) return configuration.artifacts / target.backend / (metadata.support.empty() ? "support" : metadata.support);
  return artifact_directory(configuration, target);
}

std::vector<std::string> launched(const std::vector<std::string> &launcher, std::vector<std::string> command) {
  if (launcher.empty()) return command;
  auto result = launcher;
  result.emplace_back("--");
  result.insert(result.end(), std::make_move_iterator(command.begin()), std::make_move_iterator(command.end()));
  return result;
}
Workload load_workload(const RunOptions &configuration) {
  const auto file = configuration.profiles.parent_path() / "workloads" / (configuration.workload + ".toml");
  if (!fs::is_regular_file(file)) throw std::invalid_argument("conformance workload does not exist: " + file.string());
  Workload workload;
  std::string buffer;
  if (const auto error = glz::read_file_toml(workload, file.string(), buffer); error)
    throw std::invalid_argument("cannot load conformance workload " + file.string() + ":\n" + glz::format_error(error, buffer));
  if (workload.specs.empty()) throw std::invalid_argument("conformance workload needs at least one Catch test spec");
  return workload;
}
uint32_t parse_timeout(std::string_view value, const fs::path &manifest) {
  constexpr uint64_t maximum_timeout_seconds = 24 * 60 * 60;
  uint64_t result = 0;
  const auto [end, error] = std::from_chars(value.data(), value.data() + value.size(), result);
  if (error != std::errc{} || end != value.data() + value.size() || result == 0 || result > maximum_timeout_seconds)
    throw std::runtime_error(fmt::format("invalid timeout in {}", manifest.string()));
  return static_cast<uint32_t>(result);
}
std::vector<TestProgram> test_programs(const fs::path &artifact) {
  const auto manifest = artifact / "test-programs.txt";
  std::ifstream input(manifest);
  if (!input) throw std::runtime_error("artifact has no test program manifest: " + manifest.string());
  std::vector<TestProgram> result;
  std::set<std::string> names;
  std::string line;
  while (std::getline(input, line)) {
    if (!line.empty() && line.back() == '\r') line.pop_back();
    const auto separator = line.find('\t');
    if (separator == std::string::npos) throw std::runtime_error("invalid test program manifest: " + manifest.string());
    auto name = line.substr(0, separator);
    if (!valid_identifier(name) || !names.insert(name).second) throw std::runtime_error("invalid test program name: " + name);
#if defined(_WIN32)
    auto path = artifact / (name + ".exe");
#else
    auto path = artifact / name;
#endif
    if (!fs::is_regular_file(path)) throw std::runtime_error("missing conformance test program: " + path.string());
    result.push_back({std::move(name), std::move(path), parse_timeout(std::string_view(line).substr(separator + 1), manifest)});
  }
  if (result.empty()) throw std::runtime_error("artifact contains no test programs: " + artifact.string());
  return result;
}
namespace {
std::string bracket_quote(std::string_view value) {
  for (size_t equals = 1;; ++equals) {
    const std::string delimiter(equals, '=');
    if (value.find("]" + delimiter + "]") == std::string_view::npos)
      return "[" + delimiter + "[" + std::string(value) + "]" + delimiter + "]";
  }
}
} // namespace
void write_execution_config(const fs::path &path, const ExecutionConfig &configuration) {
  std::string buffer;
  if (const auto error = glz::write_file_json(configuration, path.string(), buffer); error)
    throw std::runtime_error("cannot write execution configuration " + path.string() + ": " + glz::format_error(error, buffer));
}
ExecutionConfig read_execution_config(const fs::path &path) {
  ExecutionConfig result;
  std::string buffer;
  if (const auto error = glz::read_file_json(result, path.string(), buffer); error)
    throw std::runtime_error("cannot read execution configuration " + path.string() + ": " + glz::format_error(error, buffer));
  if (!conformance::valid_physical_device_key(result.device)) throw std::runtime_error("invalid physical device in " + path.string());
  return result;
}

std::vector<std::string> configured_skips(const std::map<std::string, std::string> &environment) {
  const auto found = environment.find("CONFORMANCE_SKIPS");
  if (found == environment.end()) return {};
  auto result = split(found->second, '\n');
  std::erase(result, std::string{});
  return result;
}
void apply_device_probe(ExecutionConfig &configuration, const DeviceProbeResult &probe) {
  if (!valid_physical_device_key(probe.device)) throw std::runtime_error("invalid device probe identity");
  if (probe.environment.empty()) throw std::runtime_error("device probe returned no execution selection");
  for (const auto &[key, value] : probe.environment) {
    if (!selection_environment_key(key) || value.find('\0') != std::string::npos)
      throw std::runtime_error("unexpected device probe environment key: " + key);
    configuration.environment[key] = value;
  }
  configuration.device = probe.device;
  configuration.environment["CONFORMANCE_PHYSICAL_DEVICE"] = probe.device;
}
DeviceProbeResult probe_device(const RunOptions &configuration, const fs::path &artifact,
                         const std::map<std::string, std::string> &environment, const std::string &id) {
  auto probe = artifact / "conformance-device";
#ifdef _WIN32
  probe += ".exe";
#endif
  const auto result = run_process(launched(configuration.launcher, {probe.string()}), artifact, environment, 2min,
                                  fmt::format("[{} device] ", id), true);
  if (result.status != 0)
    throw std::runtime_error("device probe failed for " + id + " (including runtime/selector verification): " + result.error);
  DeviceProbeResult parsed;
  if (const auto error = glz::read_json(parsed, result.output); error)
    throw std::runtime_error("invalid device probe JSON for " + id + ": " + glz::format_error(error, result.output));
  return parsed;
}
std::vector<ScheduledCase> discover_cases(const RunOptions &configuration, const Workload &workload, const fs::path &artifact,
                           const std::map<std::string, std::string> &environment, const std::string &id) {
  std::vector<ScheduledCase> tests;
  for (const auto &program : test_programs(artifact)) {
    if (std::ranges::find(workload.exclude_programs, program.name) != workload.exclude_programs.end()) continue;
    auto command = std::vector<std::string>{program.path.string(), "--list-tests", "--reporter", "json", "--order", "lex"};
    command.insert(command.end(), workload.specs.begin(), workload.specs.end());
    const auto listing = run_process(launched(configuration.launcher, std::move(command)), artifact, environment, 10min,
                                     fmt::format("[{}/{} discovery] ", id, program.name), true);
    if (listing.status != 0)
      throw std::runtime_error(fmt::format("Catch discovery failed for {}/{}: {}", id, program.name, listing.error));
    auto names = catch_case_names(listing.output, program.name);
    if (names.size() > UINT32_MAX) throw std::runtime_error("Catch inventory is too large");
    const auto count = static_cast<uint32_t>(names.size());
    auto timeout = program.timeout_seconds;
    if (const auto key = suite_timeout_environment(program.name); !key.empty()) {
      if (const auto found = environment.find(std::string(key)); found != environment.end())
        timeout = parse_timeout(found->second, artifact / "test-programs.txt");
    }
    for (uint32_t index = 0; index < count; ++index)
      tests.push_back({std::move(names[index]), program.name, timeout, index, count});
  }
  if (tests.empty()) throw std::runtime_error("conformance target selected no tests: " + id);
  return tests;
}
TargetExecution prepare_target(const RunOptions &configuration, const Workload &workload, const profile::ResolvedTarget &target,
                              const fs::path &run_directory) {
  const auto artifact = artifact_directory(configuration, target);
  const auto metadata = validate_artifact(artifact, target);
  const auto support = support_directory(configuration, target, metadata);
  if (!fs::is_directory(support)) throw std::runtime_error("missing artifact support: " + support.string());
  auto environment = base_environment();
  for (const auto &[key, value] : target.environment)
    environment[key] = value;
  environment["CONFORMANCE_TARGET_BACKEND"] = target.backend;
  environment["CONFORMANCE_TARGET_ARCHITECTURE"] = target.architecture.value_or("");
  if (configuration.isolated_launcher) apply_suite_isolated_environment(environment, target.backend);
  prepare_runtime_environment(environment, artifact, support, target.backend);
  const auto reports = run_directory / target.id;
  fs::create_directories(reports);
  ExecutionConfig exec{artifact.string(),
                         reports.string(),
                         {},
                         environment,
                         configuration.launcher,
                         configuration.isolated_launcher ? std::vector<std::string>{} : configured_skips(environment),
                         workload.specs,
                         target.deviceLock && !configuration.isolated_launcher};
  apply_device_probe(exec, probe_device(configuration, artifact, environment, target.id));
  // Re-probe with the exact child environment: selection must survive visibility remapping.
  const auto verified = probe_device(configuration, artifact, exec.environment, target.id);
  if (verified.device != exec.device) throw std::runtime_error("device selection changed after propagation for " + target.id);
  auto tests = discover_cases(configuration, workload, artifact, exec.environment, target.id);
  const auto path = run_directory / "ctest/targets" / (target.id + ".json");
  write_execution_config(path, exec);
  return {target.id, target.backend, exec.deviceLock ? exec.device : "", path, std::move(tests)};
}
void materialize_ctest(const fs::path &directory, const fs::path &driver, const std::vector<TargetExecution> &targets) {
  std::ofstream file(directory / "CTestTestfile.cmake", std::ios::binary | std::ios::trunc);
  if (!file) throw std::runtime_error("cannot write CTest inventory");
  file << "cmake_policy(VERSION 3.25)\n";
  std::map<std::pair<uint32_t, std::string>, std::vector<std::string>> properties;
  size_t remaining = 0;
  for (const auto &target : targets)
    remaining += target.tests.size();
  for (size_t i = 0; remaining; ++i)
    for (const auto &target : targets) {
      if (i >= target.tests.size()) continue;
      const auto &test = target.tests[i];
      const auto name = bracket_quote(target.id + "/" + test.program + "/" + test.identity);
      file << "add_test(" << name << ' ' << bracket_quote(driver.string()) << " exec --config "
           << bracket_quote(target.configuration.string()) << " --program " << bracket_quote(test.program) << " --test "
           << bracket_quote(test.identity) << " --timeout " << test.timeout_seconds << " --index " << test.index << " --count "
           << test.count << ")\n";
      properties[{test.timeout_seconds, target.device}].push_back(name);
      --remaining;
    }
  for (const auto &[property, names] : properties) {
    const auto &[timeout, device] = property;
    file << "set_tests_properties(";
    for (const auto &name : names)
      file << name << ' ';
    file << "PROPERTIES SKIP_RETURN_CODE 4 TIMEOUT " << (timeout + 615);
    if (!device.empty()) file << " RESOURCE_LOCK " << bracket_quote(device);
    file << ")\n";
  }
  file.close();
  if (!file) throw std::runtime_error("cannot finish CTest inventory");
}
int32_t execute_case(const fs::path &path, const ScheduledCase &test) {
  const auto config = read_execution_config(path);
  if (!valid_identifier(test.program) || test.program == "." || test.program == "..") throw std::invalid_argument("invalid test program");
  if (std::ranges::find(config.skips, test.program) != config.skips.end()
      || std::ranges::find(config.skips, test.identity) != config.skips.end()) {
    std::cout << "SKIP " << test.identity << ": machine profile\n";
    return 4;
  }
  const bool sharded = suite_uses_catch_sharding(test.program);
  auto arguments = catch_shard_arguments(config.specs, sharded ? test.index : 0, sharded ? test.count : 1);
  auto program = fs::path(config.directory) / test.program;
#ifdef _WIN32
  program += ".exe";
#endif
  arguments.insert(arguments.begin(), program.string());
  const auto report = fs::path(config.report_directory) / test.program / fmt::to_string(test.index);
  fs::create_directories(report);
  auto environment = config.environment;
  environment["CONFORMANCE_TEST_IDENTITY"] = test.identity;
  environment["CONFORMANCE_REPORT_DIRECTORY"] = report.string();
  std::unique_ptr<DeviceLock> lock;
  if (config.deviceLock) lock = std::make_unique<DeviceLock>(physical_device_from_key(config.device), 10min);
  const auto result =
      run_process(launched(config.launcher, std::move(arguments)), report, environment, std::chrono::seconds(test.timeout_seconds));
  return result.status;
}
int32_t run(RunOptions configuration, const fs::path &driver) {
  validate_run_options(configuration);
  const auto resolved_profile = profile::resolve_profile(configuration.profiles, configuration.machine);
  const auto workload = load_workload(configuration);
  const auto root = configuration.output / resolved_profile.name;
  fs::create_directories(root);
  fs::path reports;
  for (uint64_t attempt = 0;; ++attempt) {
    reports = root / fmt::format("run-{}-{}", std::chrono::system_clock::now().time_since_epoch().count(), attempt);
    if (fs::create_directory(reports)) break;
  }
  const auto ctest_directory = reports / "ctest";
  fs::create_directories(ctest_directory / "targets");
  std::vector<TargetExecution> prepared;
  for (const auto &target :
       unique_routes(profile::resolve_targets(resolved_profile, configuration.selectors), configuration.isolated_launcher)) {
    auto value = prepare_target(configuration, workload, target, reports);
    std::cout << "conformance target " << value.id << " resolved to " << value.device << " with " << value.tests.size() << " test(s)\n";
    prepared.push_back(std::move(value));
  }
  if (prepared.empty()) throw std::runtime_error("no conformance targets selected");
  materialize_ctest(ctest_directory, driver, prepared);
  const auto *jobs = std::getenv("CONFORMANCE_JOBS");
  const auto count =
      configured_jobs(jobs && *jobs ? std::optional<std::string_view>(jobs) : std::nullopt, std::thread::hardware_concurrency());
  std::cout << "conformance reports: " << reports << '\n';
  return run_process({configuration.ctest.string(), "--test-dir", ctest_directory.string(), "--parallel", fmt::to_string(count),
                      "--output-on-failure", "--output-junit", (reports / "ctest.xml").string()},
                     ctest_directory, base_environment(), 24h)
      .status;
}
} // namespace conformance::driver
