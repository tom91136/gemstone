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
#include <utility>
#include <vector>

#include <glaze/core/meta.hpp>
#include <glaze/util/key_transformers.hpp>

#include "conformance/device.hpp"

#include "profile.hpp"

namespace conformance::driver {
/// Exit status of an unsupported or profile-skipped case.
inline constexpr int32_t SkipReturnCode = 4;
/// Upper bound accepted for a test program timeout, in seconds.
inline constexpr uint64_t MaximumTimeoutSeconds = uint64_t{24} * 60 * 60;
/// Bound for the JIT warm-up of one test program, in seconds before scaling.
inline constexpr uint32_t JitWarmupSeconds = 2 * 60 * 60;
/// Largest driver JIT cache the CUDA driver accepts.
inline constexpr uint64_t JitCacheMaximumBytes = uint64_t{4} << 30;

uint32_t availableThreadCount();
struct RunOptions {
  std::string workload = "short";
  std::filesystem::path artifacts, output, profiles, workloads;
  std::optional<std::string> machine;
  std::vector<std::string> selectors, launcher;
  bool isolatedLauncher = false;
  bool direct = false;
  bool dryRun = false;
  bool listPrograms = false;
  uint32_t threads = availableThreadCount();
  double timeoutScale = 1.0;
};
struct Workload {
  std::vector<std::string> specs{"*"};
  // Discovery-time program-name globs. Empty selects all; exclusions win.
  std::vector<std::string> includePrograms;
  // Program globs taken in from other workloads; selected even when excluded or outside includePrograms.
  std::vector<std::string> admitPrograms;
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
  // Case-identity globs dropped after discovery, in every program.
  std::vector<std::string> excludeCases;
  // Case-identity globs kept after discovery. Empty keeps every discovered case.
  std::vector<std::string> includeCases;
  std::map<std::string, std::vector<std::string>> programArguments;
};
struct ArtifactMetadata {
  std::string backend, architecture, support, code;
  std::string compiler = "vendor";
  std::string glibc, glibcxx;
  std::vector<std::string> supplements;
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
struct WorkloadFields {
  std::string extends;
  std::optional<std::vector<std::string>> specs, includePrograms, levelzeroCases, vulkanCases, excludePrograms, excludeCases;
  // Appended to the inherited or replaced list of the same name.
  std::optional<std::vector<std::string>> addIncludePrograms, addLevelzeroCases, addVulkanCases, addExcludePrograms, addExcludeCases,
      addIncludeCases;
  std::optional<std::vector<std::string>> includeCases;
  std::optional<std::vector<std::string>> admitPrograms;
  // Workloads whose include_programs and program_arguments this one takes in.
  std::optional<std::vector<std::string>> addWorkloads;
  std::optional<std::string> vulkanMustpass;
  std::optional<std::map<std::string, std::vector<std::string>>> programArguments;
};
struct ExecutionConfig {
  std::string directory, reportDirectory, device;
  std::map<std::string, std::string> environment;
  std::vector<std::string> launcher, skips, specs;
  bool deviceLock = true;
  std::vector<std::string> jitPrograms;
  // Programs whose warm-up failed, with the reason; their cases fail without running.
  std::map<std::string, std::string> jitFailures;
  std::map<std::string, std::vector<std::string>> programArguments;
  // Working directory for a program whose relative data paths need the source layout.
  std::map<std::string, std::string> programDirectories;
};
struct DiscoveryFailure {
  std::string message, classification;
};
struct ScheduledCase {
  std::string identity, program;
  uint32_t timeoutSeconds, index, count;
  std::optional<DiscoveryFailure> discoveryFailure = std::nullopt;
  // Cases of one program with the same group run in one process.
  std::optional<std::string> group = std::nullopt;
  // Memory the running case needs, in megabytes; 0 takes an even share of the run's budget.
  uint32_t memoryMegabytes = 0;
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
  // The full record and its one-line summary for failure and skip reports.
  std::string output, message;
  // HeCBench-only outcome derived from the run's output.
  std::string classification;
  std::chrono::duration<double> elapsed{};
  bool notRun = false;
};
struct DeqpResult {
  std::string statusCode;
  int32_t status;
};
enum class HecbenchOutcome { Pass, Fail, Completion, TimeLimit };
HecbenchOutcome classifyHecbenchOutput(std::string_view output, bool timedOut);
std::string_view hecbenchOutcomeName(HecbenchOutcome outcome);
struct CaseReport {
  std::string name;
  CaseResult result;
};
using CaseExecutor = std::function<CaseResult(const ExecutionConfig &, const ScheduledCase &)>;
using CaseSink = std::function<void(size_t position, CaseResult result)>;
using BatchExecutor = std::function<void(const ExecutionConfig &, std::span<const ScheduledCase>, const CaseSink &)>;
// Sees every output line of a process; a returned value replaces the time the process has left.
using LineWatcher = std::function<std::optional<std::chrono::milliseconds>(std::string_view line)>;
struct ListedCase {
  std::string name, group;
};
// While any instance lives, SIGINT and SIGTERM cancel child processes instead of terminating the driver.
class Interruption {
public:
  explicit Interruption(std::optional<std::chrono::steady_clock::time_point> deadline = std::nullopt);
  ~Interruption();
  Interruption(const Interruption &) = delete;
  Interruption &operator=(const Interruption &) = delete;
  static int32_t signal();
  static bool stopRequested();
  static bool takeStoppedProcess();
};

int32_t configuredJobs(std::string_view configured, uint32_t hardwareConcurrency);
// Keeps every count-th case, from the index-th, of the cases of all targets in order, and drops targets left without cases.
std::pair<uint32_t, uint32_t> caseShard(std::string_view shard);
void selectCaseShard(std::vector<TargetExecution> &targets, std::string_view shard);
uint32_t scaledTimeout(uint32_t seconds, double scale, uint32_t threads);
std::vector<ScheduledCase> discoverCases(const RunOptions &configuration, const Workload &workload, const std::filesystem::path &artifact,
                                         const std::map<std::string, std::string> &environment, const std::string &id,
                                         const std::filesystem::path &reports);
std::vector<std::string> catchCaseNames(std::string_view json, std::string_view program);
std::vector<ListedCase> listedCases(std::string_view listing, std::string_view program);
std::string caseClassification(std::string_view output);
std::string deqpExactCaseList(const std::vector<std::string> &names);
std::vector<std::string> deqpMustpassCases(const std::filesystem::path &root, const std::filesystem::path &manifest,
                                           const std::vector<std::string> &patterns);
std::vector<std::string> deqpCaseNames(std::istream &input);
Workload loadWorkload(const RunOptions &configuration);
bool programSelected(const Workload &workload, std::string_view program);
DeqpResult deqpResult(std::string_view qpa, std::string_view identity);
std::vector<std::string> gtestCaseNames(std::string_view listing, std::string_view program);
bool gtestSkipped(std::string_view report);
std::string gtestSkipReason(std::string_view report);
std::string catchSkipReason(std::string_view output);
bool gtestCaseSelected(std::string_view program, const std::string &name, const std::vector<std::string> &levelzeroCases);
std::string catchSpec(const std::vector<std::string> &specs);
std::vector<std::string> catchShardArguments(const std::vector<std::string> &specs, uint32_t index, uint32_t count);
std::vector<std::string> launcherArguments(std::span<char *const> arguments);
std::vector<profile::ResolvedTarget> uniqueRoutes(const std::vector<profile::ResolvedTarget> &targets, bool isolated);
std::map<std::string, std::string> baseEnvironment();
void prepareRuntimeEnvironment(std::map<std::string, std::string> &environment, const std::filesystem::path &artifact,
                               const std::filesystem::path &support, const std::vector<std::filesystem::path> &supplements,
                               std::string_view backend);
void applyJitCacheEnvironment(std::map<std::string, std::string> &environment, const std::filesystem::path &cache);
std::vector<std::string> jitWarmupPrograms(const ArtifactMetadata &metadata, bool isolatedLauncher,
                                           const std::vector<ScheduledCase> &tests);
void applyDeviceProbe(ExecutionConfig &configuration, const conformance::DeviceProbeResult &probe);
std::optional<TargetExecution> prepareTarget(const RunOptions &configuration, const Workload &workload,
                                             const profile::ResolvedTarget &target, const std::filesystem::path &runDirectory);
void warmUpJit(std::vector<TargetExecution> &targets, uint32_t jobs, double timeoutScale, const CaseExecutor &execute);
std::vector<CaseReport> runCases(const std::vector<TargetExecution> &targets, uint32_t jobs, const CaseExecutor &execute,
                                 const BatchExecutor &executeBatch = {}, uint64_t memoryBudgetMegabytes = 0);
ProcessResult runProcess(const std::vector<std::string> &command, const std::filesystem::path &directory,
                         const std::map<std::string, std::string> &environment, std::chrono::milliseconds timeout,
                         const std::string &prefix = {}, bool captureOutput = false, std::ostream *outputLog = nullptr,
                         const LineWatcher &watcher = {});
CaseResult executeCase(const ExecutionConfig &config, const ScheduledCase &test);
void executeBatch(const ExecutionConfig &config, std::span<const ScheduledCase> tests, const CaseSink &sink);
int32_t run(RunOptions configuration);
} // namespace conformance::driver

// Workload TOML keys are snake_case.
template <> struct glz::meta<conformance::driver::Workload> : glz::snake_case {};
template <> struct glz::meta<conformance::driver::WorkloadFields> : glz::snake_case {};
