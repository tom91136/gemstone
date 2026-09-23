#pragma once

#include <chrono>
#include <cstddef>
#include <cstdint>
#include <filesystem>
#include <map>
#include <optional>
#include <span>
#include <string>
#include <string_view>
#include <vector>

#include "conformance/device.hpp"

#include "profile.hpp"

namespace conformance::driver {
struct RunOptions {
  std::string workload = "compact";
  std::filesystem::path artifacts, output, profiles;
  std::filesystem::path ctest = "ctest";
  std::optional<std::string> machine;
  std::vector<std::string> selectors, launcher;
  bool isolated_launcher = false;
};
struct Workload {
  std::vector<std::string> specs{"*"};
  // Full Google Test names or globs for the Level Zero CTS. Empty selects all.
  std::vector<std::string> levelzero_cases;
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
std::vector<std::string> catch_case_names(std::string_view json, std::string_view program);
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
                          const std::string &prefix = {}, bool capture_output = false);
int32_t execute_case(const std::filesystem::path &config, const ScheduledCase &test);
int32_t run(RunOptions configuration, const std::filesystem::path &driver);
std::filesystem::path executable_path(const char *argument);
} // namespace conformance::driver
