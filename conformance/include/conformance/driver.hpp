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

#include "conformance/profile.hpp"

namespace conformance::driver {
struct Configuration {
  std::string workload = "compact";
  std::filesystem::path artifacts, output, profiles;
  std::filesystem::path ctest = "ctest";
  std::optional<std::string> machine;
  std::vector<std::string> selectors, launcher;
  bool isolated_launcher = false;
};
struct Workload {
  std::vector<std::string> specs{"*"};
  std::vector<std::string> exclude_programs;
};
struct ExecConfiguration {
  std::string directory, report_directory, device;
  std::map<std::string, std::string> environment;
  std::vector<std::string> launcher, skips, specs;
  bool deviceLock = true;
};
struct Test {
  std::string identity, program;
  uint32_t timeout_seconds, index, count;
};
struct PreparedTarget {
  std::string id, backend, device;
  std::filesystem::path configuration;
  std::vector<Test> tests;
};
struct ProbeResult {
  std::string device;
  std::map<std::string, std::string> environment;
};
struct RunResult {
  int32_t status;
  std::string output, error;
};

void validate_configuration(Configuration &configuration);
int32_t configured_jobs(std::optional<std::string_view> configured, uint32_t hardware_concurrency);
std::vector<std::string> catch_tests(std::string_view json, std::string_view program);
std::vector<std::string> catch_arguments(const std::vector<std::string> &specs, uint32_t index, uint32_t count);
std::vector<std::string> launcher_arguments(bool enabled, std::span<char *const> arguments);
std::string bracket_quote(std::string_view value);
std::vector<profile::PlanTarget> unique_routes(const std::vector<profile::PlanTarget> &targets, bool isolated);
std::map<std::string, std::string> base_environment();
void prepare_runtime_environment(std::map<std::string, std::string> &environment, const std::filesystem::path &artifact,
                                 const std::filesystem::path &support, std::string_view backend);
void apply_probe(ExecConfiguration &configuration, const ProbeResult &probe);
void materialize_ctest(const std::filesystem::path &directory, const std::filesystem::path &driver,
                       const std::vector<PreparedTarget> &targets);
RunResult run_process(const std::vector<std::string> &command, const std::filesystem::path &directory,
                      const std::map<std::string, std::string> &environment, std::chrono::milliseconds timeout,
                      const std::string &prefix = {}, bool capture_output = false);
int32_t execute(const std::filesystem::path &config, const Test &test);
int32_t run(Configuration configuration, const std::filesystem::path &driver);
std::filesystem::path executable_path(const char *argument);
} // namespace conformance::driver
