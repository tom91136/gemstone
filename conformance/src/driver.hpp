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

#include <glaze/core/meta.hpp>
#include <glaze/util/key_transformers.hpp>

#include "conformance/device.hpp"

#include "profile.hpp"

namespace conformance::driver {
/// Exit status of an unsupported or profile-skipped case.
inline constexpr int32_t SkipReturnCode = 4;
/// Upper bound accepted for a test program timeout, in seconds.
inline constexpr uint64_t MaximumTimeoutSeconds = 24 * 60 * 60;

struct RunOptions {
  std::string workload = "compact";
  std::filesystem::path artifacts, output, profiles, workloads;
  std::optional<std::string> machine;
  std::vector<std::string> selectors, launcher;
  bool isolatedLauncher = false;
  bool direct = false;
};
struct Workload {
  std::vector<std::string> specs{"*"};
  // Discovery-time program-name globs. Empty selects all; exact exclusions win.
  std::vector<std::string> includePrograms;
  // Full Google Test names or globs for the Level Zero CTS. Empty selects all.
  std::vector<std::string> levelzeroCases;
  // Explicit dEQP selection; empty requires an explicit mustpass manifest.
  std::vector<std::string> vulkanCases;
  // Artifact-relative official CTS manifest. Empty vulkan_cases selects the
  // whole manifest; otherwise selectors are checked against that manifest.
  std::string vulkanMustpass;
  // Discovery-time exclusion: these programs are never enumerated, so they
  // apply under --isolated-launcher too. Machine-specific profile skips are
  // execution-time and are intentionally disabled there.
  std::vector<std::string> excludePrograms;
};
struct ExecutionConfig {
  std::string directory, reportDirectory, device;
  std::map<std::string, std::string> environment;
  std::vector<std::string> launcher, skips, specs;
  bool deviceLock = true;
};
struct ScheduledCase {
  std::string identity, program;
  uint32_t timeoutSeconds, index, count;
};
struct TargetExecution {
  std::string id;
  ExecutionConfig config;
  std::vector<ScheduledCase> tests;
};
struct ProcessResult {
  int32_t status;
  std::string output, error;
  bool timedOut = false;
};
struct CaseResult {
  int32_t status = 0;
  // The full record and its one-line summary for failure reports.
  std::string output, message;
  std::chrono::duration<double> elapsed{};
};
struct DeqpResult {
  std::string statusCode;
  int32_t status;
};
struct CaseReport {
  std::string name;
  CaseResult result;
};
using CaseExecutor = std::function<CaseResult(const ExecutionConfig &, const ScheduledCase &)>;
// While any instance lives, SIGINT and SIGTERM cancel child processes instead of terminating the driver.
class Interruption {
public:
  Interruption();
  ~Interruption();
  Interruption(const Interruption &) = delete;
  Interruption &operator=(const Interruption &) = delete;
  static int32_t signal();
};

int32_t configuredJobs(std::string_view configured, uint32_t hardwareConcurrency);
uint32_t scaledTimeout(uint32_t seconds, uint32_t hardwareConcurrency);
std::vector<ScheduledCase> discoverCases(const RunOptions &configuration, const Workload &workload, const std::filesystem::path &artifact,
                                         const std::map<std::string, std::string> &environment, const std::string &id,
                                         const std::filesystem::path &reports);
std::vector<std::string> catchCaseNames(std::string_view json, std::string_view program);
std::string deqpExactCaseList(const std::vector<std::string> &names);
std::vector<std::string> deqpMustpassCases(const std::filesystem::path &root, const std::filesystem::path &manifest,
                                           const std::vector<std::string> &patterns);
std::vector<std::string> deqpCaseNames(std::istream &input);
Workload loadWorkload(const RunOptions &configuration);
DeqpResult deqpResult(std::string_view qpa, std::string_view identity);
std::vector<std::string> gtestCaseNames(std::string_view listing, std::string_view program);
std::vector<std::string> catchShardArguments(const std::vector<std::string> &specs, uint32_t index, uint32_t count);
std::vector<std::string> launcherArguments(std::span<char *const> arguments);
std::vector<profile::ResolvedTarget> uniqueRoutes(const std::vector<profile::ResolvedTarget> &targets, bool isolated);
std::map<std::string, std::string> baseEnvironment();
void prepareRuntimeEnvironment(std::map<std::string, std::string> &environment, const std::filesystem::path &artifact,
                               const std::filesystem::path &support, std::string_view backend);
void applyDeviceProbe(ExecutionConfig &configuration, const conformance::DeviceProbeResult &probe);
std::optional<TargetExecution> prepareTarget(const RunOptions &configuration, const Workload &workload,
                                             const profile::ResolvedTarget &target, const std::filesystem::path &runDirectory);
std::vector<CaseReport> runCases(const std::vector<TargetExecution> &targets, uint32_t jobs, const CaseExecutor &execute);
ProcessResult runProcess(const std::vector<std::string> &command, const std::filesystem::path &directory,
                         const std::map<std::string, std::string> &environment, std::chrono::milliseconds timeout,
                         const std::string &prefix = {}, bool captureOutput = false, std::ostream *outputLog = nullptr);
CaseResult executeCase(const ExecutionConfig &config, const ScheduledCase &test);
int32_t run(RunOptions configuration);
} // namespace conformance::driver

// Workload TOML keys are snake_case.
template <> struct glz::meta<conformance::driver::Workload> : glz::snake_case {};
