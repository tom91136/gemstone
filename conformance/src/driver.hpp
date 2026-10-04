#pragma once

#include <chrono>
#include <cstddef>
#include <cstdint>
#include <filesystem>
#include <functional>
#include <iosfwd>
#include <map>
#include <optional>
#include <span>
#include <string>
#include <string_view>
#include <vector>

#include "conformance/device.hpp"

#include "profile.hpp"

namespace conformance::driver {
/// CTest return code for an unsupported or profile-skipped case.
inline constexpr int32_t SkipReturnCode = 4;
/// Upper bound accepted by the `--timeout` option, in seconds.
inline constexpr uint64_t MaximumTimeoutSeconds = 24 * 60 * 60;
/// Extra CTest timeout granted for device-lock waiting, process teardown and reporting.
inline constexpr uint32_t TimeoutMarginSeconds = 615;

struct RunOptions {
  std::string workload = "compact";
  std::filesystem::path artifacts, output, profiles, workloads;
  std::filesystem::path ctest = "ctest";
  std::optional<std::string> machine;
  std::vector<std::string> selectors, launcher;
  bool isolated_launcher = false;
  bool direct = false;
};
struct Workload {
  std::vector<std::string> specs{"*"};
  // Discovery-time program-name globs. Empty selects all; exact exclusions win.
  std::vector<std::string> include_programs;
  // Full Google Test names or globs for the Level Zero CTS. Empty selects all.
  std::vector<std::string> levelzero_cases;
  // Explicit dEQP selection; empty requires an explicit mustpass manifest.
  std::vector<std::string> vulkan_cases;
  // Artifact-relative official CTS manifest. Empty vulkan_cases selects the
  // whole manifest; otherwise selectors are checked against that manifest.
  std::string vulkan_mustpass;
  // Discovery-time exclusion: these programs are never enumerated, so they
  // apply under --isolated-launcher too. Machine-specific profile skips are
  // execution-time and are intentionally disabled there.
  std::vector<std::string> exclude_programs;
};
struct ExecutionConfig {
  std::string directory, report_directory, device;
  std::map<std::string, std::string> environment;
  std::vector<std::string> launcher, skips, specs;
  bool deviceLock = true;
  std::string skipReason;
  std::vector<std::string> routeSkips;
};
struct ScheduledCase {
  std::string identity, program;
  uint32_t timeout_seconds, index, count;
};
struct TargetExecution {
  std::string id, backend, device;
  std::filesystem::path configuration;
  std::vector<ScheduledCase> tests;
};
using DeviceProbeResult = conformance::DeviceProbeResult;
struct ProcessResult {
  int32_t status;
  std::string output, error;
};

int32_t configured_jobs(std::optional<std::string_view> configured, uint32_t hardware_concurrency);
std::vector<ScheduledCase> discover_cases(const RunOptions &configuration, const Workload &workload, const std::filesystem::path &artifact,
                                          const std::map<std::string, std::string> &environment, const std::string &id,
                                          const std::filesystem::path &reports);
std::vector<std::string> catch_case_names(std::string_view json, std::string_view program);
std::vector<std::string> deqp_case_selections(const std::vector<std::string> &patterns);
std::optional<std::string> deqp_exact_case_list(const std::vector<std::string> &patterns);
std::vector<std::string> deqp_mustpass_cases(const std::filesystem::path &root, const std::filesystem::path &manifest,
                                             const std::vector<std::string> &patterns);
std::vector<std::string> deqp_case_names(std::string_view listing);
std::vector<std::string> deqp_case_names(std::istream &input);
Workload load_workload(const RunOptions &configuration);
int32_t deqp_result(std::string_view qpa, std::string_view identity);
std::vector<std::string> gtest_case_names(std::string_view listing, std::string_view program);
std::vector<std::string> catch_shard_arguments(const std::vector<std::string> &specs, uint32_t index, uint32_t count);
std::vector<std::string> launcher_arguments(bool enabled, std::span<char *const> arguments);
std::vector<profile::ResolvedTarget> unique_routes(const std::vector<profile::ResolvedTarget> &targets, bool isolated);
std::map<std::string, std::string> base_environment();
void prepare_runtime_environment(std::map<std::string, std::string> &environment, const std::filesystem::path &artifact,
                                 const std::filesystem::path &support, std::string_view backend);
void apply_device_probe(ExecutionConfig &configuration, const DeviceProbeResult &probe);
void materialize_ctest(const std::filesystem::path &directory, const std::filesystem::path &driver,
                       const std::vector<TargetExecution> &targets);
ProcessResult run_process(const std::vector<std::string> &command, const std::filesystem::path &directory,
                          const std::map<std::string, std::string> &environment, std::chrono::milliseconds timeout,
                          const std::string &prefix = {}, bool capture_output = false, std::ostream *output_log = nullptr,
                          const std::function<std::string(std::string_view)> &format_line = {});
int32_t execute_case(const std::filesystem::path &config, const ScheduledCase &test);
int32_t run(RunOptions configuration, const std::filesystem::path &driver);
std::filesystem::path executable_path(const char *argument);
} // namespace conformance::driver
