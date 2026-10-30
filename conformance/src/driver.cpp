#include "driver.hpp"

#include <algorithm>
#include <atomic>
#include <charconv>
#include <chrono>
#include <cmath>
#include <condition_variable>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <exception>
#include <filesystem>
#include <fstream>
#include <iostream>
#include <iterator>
#include <limits>
#include <map>
#include <mutex>
#include <optional>
#include <regex>
#include <set>
#include <span>
#include <stdexcept>
#include <string>
#include <string_view>
#include <syncstream>
#include <system_error>
#include <thread>
#include <tuple>
#include <utility>
#include <vector>

#ifdef __linux__
  #include <sched.h>
#elifdef _WIN32
  #include <bit>

  #ifndef NOMINMAX
    #define NOMINMAX
  #endif
  #include <windows.h>
#endif

#include <aspartame/all.hpp>
#include <glaze/json.hpp>
#include <glaze/toml.hpp>

#include "conformance/device.hpp"
#include "conformance/environment_keys.hpp"

#include "device_selector.hpp"
#include "environment.hpp"
#include "profile.hpp"
#include "suite_policy.hpp"
#include <fmt/format.h>

namespace conformance::driver {
using namespace aspartame;
namespace fs = std::filesystem;
using namespace std::chrono_literals;
// Program timeouts are recorded for a reference host; emulated work
// parallelises across host cores, so a host with fewer cores gets more time and
// one with more keeps the recorded budget.
inline constexpr uint32_t TimeoutReferenceCores = 32;
namespace {
struct TestProgram {
  std::string name;
  fs::path path;
  uint32_t timeoutSeconds;
  std::string format;
};
bool validIdentifier(std::string_view value) {
  return !value.empty() && value ^ forall([](char c) {
                             return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '_'
                                    || c == '-';
                           });
}
// A single path component: an identifier that is not a directory reference.
bool validName(std::string_view value) { return validIdentifier(value) && value != "." && value != ".."; }
void validateRunOptions(RunOptions &c) {
  if (c.artifacts.empty() || c.output.empty()) throw std::invalid_argument("run requires --artifacts and --output");
  if (c.direct) {
    if (!c.profiles.empty() || c.machine) throw std::invalid_argument("--direct cannot use --profiles or --machine");
    if (c.workloads.empty()) throw std::invalid_argument("--direct requires --workloads");
  } else if (c.profiles.empty()) {
    throw std::invalid_argument("profile runs require --profiles");
  }
  if (!validName(c.workload)) throw std::invalid_argument("invalid workload name");
  if (c.isolatedLauncher && c.launcher.empty()) throw std::invalid_argument("--isolated-launcher requires --launcher -- command ...");
  if (!c.profiles.empty()) c.profiles = fs::absolute(c.profiles).lexically_normal();
  if (c.workloads.empty()) c.workloads = c.profiles.parent_path() / "workloads";
  c.workloads = fs::absolute(c.workloads).lexically_normal();
  c.artifacts = fs::absolute(c.artifacts).lexically_normal();
  c.output = fs::absolute(c.output).lexically_normal();
}
uint32_t positiveSetting(const char *name, uint32_t fallback) {
  const auto *raw = std::getenv(name);
  if (!raw || !*raw) return fallback;
  const std::string_view value(raw);
  uint32_t result = 0;
  const auto [end, error] = std::from_chars(value.data(), value.data() + value.size(), result);
  if (error != std::errc{} || end != value.data() + value.size() || result == 0)
    throw std::invalid_argument(fmt::format("{} must be a positive 32-bit integer, not '{}'", name, value));
  return result;
}
double positiveScale(const char *name) {
  const auto *raw = std::getenv(name);
  if (!raw || !*raw) return 1.0;
  const std::string_view value(raw);
  const auto dot = std::min(value.find('.'), value.size());
  const auto fraction = value.substr(std::min(dot + 1, value.size()));
  uint32_t whole = 0;
  uint32_t thousandths = 0;
  const auto parsed = [](std::string_view digits, uint32_t &out) {
    const auto [end, error] = std::from_chars(digits.data(), digits.data() + digits.size(), out);
    return !digits.empty() && error == std::errc{} && end == digits.data() + digits.size();
  };
  const bool valid = parsed(value.substr(0, dot), whole) && (dot == value.size() || (fraction.size() <= 3 && parsed(fraction, thousandths)))
                     && (whole > 0 || thousandths > 0);
  if (!valid) throw std::invalid_argument(fmt::format("{} must be a positive number with at most three decimals, not '{}'", name, value));
  for (auto digits = fraction.size(); digits < 3; ++digits)
    thousandths *= 10;
  return static_cast<double>(whole) + static_cast<double>(thousandths) / 1000.0;
}
} // namespace
uint32_t availableThreadCount() {
#ifdef __linux__
  if (cpu_set_t set; sched_getaffinity(0, sizeof(set), &set) == 0) return static_cast<uint32_t>(std::max(1, CPU_COUNT(&set)));
#elifdef _WIN32
  // Both masks are zero once a process spans processor groups.
  if (DWORD_PTR process = 0, system = 0; GetProcessAffinityMask(GetCurrentProcess(), &process, &system) && process != 0)
    return static_cast<uint32_t>(std::popcount(process));
#endif
  return std::max(1U, std::thread::hardware_concurrency());
}
uint32_t scaledTimeout(uint32_t seconds, double scale, uint32_t threads) {
  const auto factor = std::clamp(static_cast<double>(TimeoutReferenceCores) / static_cast<double>(std::max(1U, threads)), 1.0, 4.0);
  return static_cast<uint32_t>(
      std::min(std::round(static_cast<double>(seconds) * scale * factor), static_cast<double>(std::numeric_limits<uint32_t>::max())));
}
int32_t configuredJobs(std::string_view configured, uint32_t hardwareConcurrency) {
  if (configured.empty()) return static_cast<int32_t>(std::max(1U, hardwareConcurrency));
  uint32_t result = 0;
  const auto [end, error] = std::from_chars(configured.data(), configured.data() + configured.size(), result);
  if (error != std::errc{} || end != configured.data() + configured.size() || result == 0 || result > INT32_MAX)
    throw std::invalid_argument("CONFORMANCE_JOB_COUNT must be a positive 32-bit integer");
  return static_cast<int32_t>(result);
}
std::pair<uint32_t, uint32_t> caseShard(std::string_view shard) {
  const auto field = [&](std::string_view value) {
    uint32_t result = 0;
    const auto [end, error] = std::from_chars(value.data(), value.data() + value.size(), result);
    if (value.empty() || error != std::errc{} || end != value.data() + value.size())
      throw std::invalid_argument("CONFORMANCE_CASE_SHARD must be <index>/<count>");
    return result;
  };
  const auto separator = shard.find('/');
  if (separator == std::string_view::npos) throw std::invalid_argument("CONFORMANCE_CASE_SHARD must be <index>/<count>");
  const auto index = field(shard.substr(0, separator));
  const auto count = field(shard.substr(separator + 1));
  if (index >= count) throw std::invalid_argument("CONFORMANCE_CASE_SHARD index must be below its count");
  return {index, count};
}
void selectCaseShard(std::vector<TargetExecution> &targets, std::string_view shard) {
  const auto [index, count] = caseShard(shard);
  size_t ordinal = 0;
  for (auto &target : targets) {
    std::vector<ScheduledCase> selected;
    for (auto &test : target.tests)
      if (ordinal++ % count == index) selected.push_back(std::move(test));
    target.tests = std::move(selected);
  }
  std::erase_if(targets, [](const TargetExecution &target) { return target.tests.empty(); });
}
std::vector<std::string> catchCaseNames(std::string_view json, std::string_view program) {
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
    if (test.name.empty() || test.name.contains('\0') || !names.insert(test.name).second)
      throw std::runtime_error("invalid or duplicate Catch test from " + std::string(program));
    result.push_back(std::move(test.name));
  }
  return result;
}
std::vector<std::string> deqpCaseNames(std::istream &input) {
  std::set<std::string> names;
  std::string line;
  while (std::getline(input, line)) {
    if (!line.empty() && line.back() == '\r') line.pop_back();
    if (!line.starts_with("TEST: ")) continue;
    auto name = line.substr(6);
    if (!name.starts_with("dEQP-VK.") || !validIdentifier(name) || !names.insert(name).second)
      throw std::runtime_error("invalid or duplicate dEQP case: " + name);
  }
  if (!input.eof()) throw std::runtime_error("cannot read dEQP inventory");
  if (names.empty()) throw std::runtime_error("empty dEQP inventory");
  return names ^ to_vector();
}
DeqpResult deqpResult(std::string_view qpa, std::string_view identity) {
  // QPA is a framed stream of XML documents. A successful process exit also
  // covers zero tests and NotSupported, so require one complete matching case.
  const std::string begin = "#beginTestCaseResult " + std::string(identity) + "\n";
  const auto first = qpa.find("#beginTestCaseResult ");
  const auto end = qpa.find("#endTestCaseResult", first);
  if (first == std::string_view::npos || !qpa.substr(first).starts_with(begin) || end == std::string_view::npos
      || qpa.find("#beginTestCaseResult ", first + 1) != std::string_view::npos
      || qpa.find("#endTestCaseResult", end + 1) != std::string_view::npos)
    throw std::runtime_error("missing, incomplete, or mismatched dEQP result");
  const std::string document(qpa.substr(first + begin.size(), end - first - begin.size()));
  const auto header = document.find("<TestCaseResult ");
  const auto headerEnd = document.find('>', header);
  if (header == std::string::npos || headerEnd == std::string::npos
      || !document.substr(header, headerEnd - header).contains("CasePath=\"" + std::string(identity) + "\""))
    throw std::runtime_error("mismatched dEQP XML case");
  static const std::regex result(R"re(<Result StatusCode="([A-Za-z]+)">[^<]*</Result>\s*</TestCaseResult>\s*$)re");
  std::smatch match;
  if (!std::regex_search(document, match, result) || document.find("<Result ") != static_cast<size_t>(match.position()))
    throw std::runtime_error("invalid dEQP terminal result");
  auto status = match[1].str();
  // Vulkan CTS explicitly permits quality and compatibility warnings. The
  // original status stays in the QPA and case log for separate reporting.
  if (status == "Pass" || status == "QualityWarning" || status == "CompatibilityWarning")
    return {.statusCode = std::move(status), .status = 0};
  if (status == "NotSupported") return {.statusCode = std::move(status), .status = SkipReturnCode};
  // Do not apply waivers implicitly.
  return {.statusCode = std::move(status), .status = 1};
}
std::vector<std::string> gtestCaseNames(std::string_view listing, std::string_view program) {
  std::vector<std::string> result;
  std::set<std::string> unique;
  std::string suite;
  while (!listing.empty()) {
    const auto end = listing.find('\n');
    auto line = listing.substr(0, end);
    if (!line.empty() && line.back() == '\r') line.remove_suffix(1);
    if (end == std::string_view::npos) listing = {};
    else listing.remove_prefix(end + 1);
    if (line.empty()) continue;
    if (line.starts_with("  ")) {
      if (suite.empty()) throw std::runtime_error("Google Test case without suite from " + std::string(program));
      line.remove_prefix(2);
      line = line.substr(0, line.find_first_of(" \t#"));
      if (line.empty()) throw std::runtime_error("empty Google Test case from " + std::string(program));
      const auto name = suite + std::string(line);
      if (!unique.insert(name).second) throw std::runtime_error("duplicate Google Test case from " + std::string(program));
      if (!name.contains("DISABLED_")) result.push_back(name);
    } else {
      if (line.back() != '.') throw std::runtime_error("invalid Google Test suite from " + std::string(program));
      suite = line;
    }
  }
  return result;
}
std::vector<std::string> catchShardArguments(const std::vector<std::string> &specs, uint32_t index, uint32_t count) {
  if (count == 0 || index >= count) throw std::invalid_argument("invalid Catch shard");
  return concat(std::vector<std::string>{"--order", "lex", "--shard-count", fmt::to_string(count), "--shard-index", fmt::to_string(index)},
                specs);
}
std::vector<std::string> launcherArguments(std::span<char *const> arguments) {
  auto command = std::vector<std::string>(arguments.begin(), arguments.end())
                 ^ drop_while([](const std::string &argument) { return argument != "--"; }) ^ drop(1);
  if (command.empty()) throw std::invalid_argument("--launcher requires -- command [arguments ...]");
  return command;
}
std::vector<profile::ResolvedTarget> uniqueRoutes(const std::vector<profile::ResolvedTarget> &targets, bool isolated) {
  std::vector<profile::ResolvedTarget> result;
  std::set<std::tuple<std::string, std::optional<std::string>, std::string, std::string>> seen;
  for (const auto &target : targets) {
    // An isolated launcher replaces the native provider and physical device.
    // Distinct CUDA code-mode routes still select different runtime paths.
    const auto mode = target.backend == "cuda"
                          ? target.environment ^ get_maybe(std::string(environment::CudaCodeMode)) ^ get_or_else(std::string{})
                          : std::string{};
    if (!isolated || seen.emplace(target.backend, target.architecture, mode, target.compiler).second) result.push_back(target);
    else std::cout << "conformance: identical isolated route " << target.id << " omitted before discovery\n";
  }
  return result;
}
static bool validArtifactKey(std::string_view value) {
  return !value.empty() && validIdentifier(value) && value.front() != '.' && value.front() != '_' && value.front() != '-';
}
static fs::path artifactDirectory(const RunOptions &configuration, const conformance::profile::ResolvedTarget &target) {
  auto result = configuration.artifacts / target.backend;
  if (target.compiler != "vendor") result /= target.compiler;
  if (target.architecture) result /= *target.architecture;
  return result;
}
static ArtifactMetadata validateArtifact(const fs::path &artifact, const conformance::profile::ResolvedTarget &target) {
  const auto file = artifact / "artifact.toml";
  ArtifactMetadata metadata;
  std::string buffer;
  if (const auto error = glz::read_file_toml(metadata, file.string(), buffer); error)
    throw std::runtime_error("cannot read conformance artifact metadata " + file.string() + ":\n" + glz::format_error(error, buffer));
  const auto architecture = target.architecture.value_or("");
  if (metadata.backend != target.backend || metadata.architecture != architecture || metadata.compiler != target.compiler)
    throw std::runtime_error("conformance artifact metadata does not match requested " + target.backend + ": " + file.string());
  if (!metadata.support.empty() && !validArtifactKey(metadata.support))
    throw std::runtime_error("conformance artifact has an invalid support key: " + file.string());
  return metadata;
}
static fs::path supportDirectory(const RunOptions &configuration, const conformance::profile::ResolvedTarget &target,
                                 const ArtifactMetadata &metadata) {
  if (target.architecture) return configuration.artifacts / target.backend / (metadata.support.empty() ? "support" : metadata.support);
  return artifactDirectory(configuration, target);
}

static std::vector<std::string> launched(const std::vector<std::string> &launcher, std::vector<std::string> command) {
  if (launcher.empty()) return command;
  auto result = launcher;
  result.emplace_back("--");
  result.insert(result.end(), std::make_move_iterator(command.begin()), std::make_move_iterator(command.end()));
  return result;
}
Workload loadWorkload(const RunOptions &configuration) {
  std::set<std::string> active;
  const auto read = [&](auto &&self, const std::string &name) -> Workload {
    if (!validName(name)) throw std::invalid_argument("invalid workload name: " + name);
    if (!active.insert(name).second) throw std::invalid_argument("cyclic workload inheritance: " + name);
    const auto file = configuration.workloads / (name + ".toml");
    if (!fs::is_regular_file(file)) throw std::invalid_argument("conformance workload does not exist: " + file.string());
    std::string buffer;
    WorkloadFields fields;
    if (const auto error = glz::read_file_toml(fields, file.string(), buffer); error)
      throw std::invalid_argument("cannot load conformance workload " + file.string() + ":\n" + glz::format_error(error, buffer));
    auto workload = fields.extends.empty() ? Workload{} : self(self, fields.extends);
    const auto replace = [](auto &destination, auto &source) {
      if (source) destination = std::move(*source);
    };
    replace(workload.specs, fields.specs);
    replace(workload.includePrograms, fields.includePrograms);
    replace(workload.levelzeroCases, fields.levelzeroCases);
    replace(workload.vulkanCases, fields.vulkanCases);
    replace(workload.vulkanMustpass, fields.vulkanMustpass);
    replace(workload.excludePrograms, fields.excludePrograms);
    active.erase(name);
    return workload;
  };
  auto workload = read(read, configuration.workload);
  if (workload.specs.empty()) throw std::invalid_argument("conformance workload needs at least one Catch test spec");
  return workload;
}
static uint32_t parseTimeout(std::string_view value, const fs::path &manifest) {
  uint64_t result = 0;
  const auto [end, error] = std::from_chars(value.data(), value.data() + value.size(), result);
  if (error != std::errc{} || end != value.data() + value.size() || result == 0 || result > MaximumTimeoutSeconds)
    throw std::runtime_error(fmt::format("invalid timeout in {}", manifest.string()));
  return static_cast<uint32_t>(result);
}
static std::vector<TestProgram> testPrograms(const fs::path &artifact, const Workload &workload) {
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
    if (!validIdentifier(name) || !names.insert(name).second) throw std::runtime_error("invalid test program name: " + name);
    auto path = artifact / name;
    if (!fs::is_regular_file(path)) throw std::runtime_error("missing conformance test program: " + path.string());
    const auto formatAt = line.find('\t', separator + 1);
    if (formatAt == std::string::npos) throw std::runtime_error("missing test program format: " + manifest.string());
    const auto format = line.substr(formatAt + 1);
    if (format != "catch2" && format != "gtest" && format != "deqp") throw std::runtime_error("invalid test program format");
    result.push_back({.name = std::move(name),
                      .path = std::move(path),
                      .timeoutSeconds = parseTimeout(std::string_view(line).substr(separator + 1, formatAt - separator - 1), manifest),
                      .format = format});
  }
  if (result.empty()) throw std::runtime_error("artifact contains no test programs: " + artifact.string());
  std::erase_if(result, [&](const TestProgram &program) {
    return workload.excludePrograms ^ contains(program.name)
           || (!workload.includePrograms.empty()
               && workload.includePrograms ^ none_match([&](const std::string &pattern) { return globMatches(pattern, program.name); }));
  });
  return result;
}
void applyDeviceProbe(ExecutionConfig &configuration, const conformance::DeviceProbeResult &probe) {
  if (!validPhysicalDeviceKey(probe.device)) throw std::runtime_error("invalid device probe identity");
  if (probe.environment.empty()) throw std::runtime_error("device probe returned no execution selection");
  for (const auto &[key, value] : probe.environment) {
    if (!selectionEnvironmentKey(key) || value.contains('\0')) throw std::runtime_error("unexpected device probe environment key: " + key);
    configuration.environment[key] = value;
  }
  configuration.device = probe.device;
  configuration.environment[environment::PhysicalDeviceName] = probe.device;
}
static conformance::DeviceProbeResult probeDevice(const RunOptions &configuration, const fs::path &artifact,
                                                  const std::map<std::string, std::string> &environment, const std::string &id) {
  const auto probe = artifact / "conformance-device";
  const auto result =
      runProcess(launched(configuration.launcher, {probe.string()}), artifact, environment, 2min, fmt::format("[{} device] ", id), true);
  if (result.status != 0)
    throw std::runtime_error("device probe failed for " + id + " (including runtime/selector verification): " + result.error);
  conformance::DeviceProbeResult parsed;
  if (const auto error = glz::read_json(parsed, result.output); error)
    throw std::runtime_error("invalid device probe JSON for " + id + ": " + glz::format_error(error, result.output));
  return parsed;
}
std::string deqpExactCaseList(const std::vector<std::string> &names) {
  if (names.empty()) throw std::runtime_error("Vulkan CTS requires an explicit vulkan_cases selection");
  // The CTS file parser accepts literal paths, not wildcard expressions. Restrict
  // file contents to path characters so a selector cannot introduce another line.
  if (const auto invalid = names ^ find([](const std::string &name) { return !validIdentifier(name); }))
    throw std::runtime_error("Vulkan CTS selectors without vulkan_mustpass must be literal case names: " + *invalid);
  return names ^ to<std::set>() ^ mk_string("", "\n", "\n");
}
std::vector<std::string> deqpMustpassCases(const fs::path &root, const fs::path &manifest, const std::vector<std::string> &patterns) {
  const auto base = fs::canonical(root);
  std::set<fs::path> active;
  std::set<std::string> selected;
  std::set<std::string> matched;
  if (patterns ^ exists([](const std::string &pattern) { return pattern.empty(); })) throw std::runtime_error("empty Vulkan CTS selector");
  const auto [literalPatterns, globs] = patterns ^ partition([](const std::string &pattern) { return validIdentifier(pattern); });
  const auto literals = literalPatterns ^ to<std::set>();
  const auto read = [&](auto &self, const fs::path &relative) -> void {
    if (relative.is_absolute()) throw std::runtime_error("Vulkan mustpass paths must be artifact-relative");
    const auto path = fs::canonical(base / relative);
    const auto within = path.lexically_relative(base);
    if (within.empty() || *within.begin() == "..") throw std::runtime_error("Vulkan mustpass path escapes artifact data");
    if (!active.insert(path).second) throw std::runtime_error("cyclic Vulkan mustpass manifest: " + path.string());
    std::ifstream file(path);
    if (!file) throw std::runtime_error("cannot read Vulkan mustpass manifest: " + path.string());
    std::string line;
    while (std::getline(file, line)) {
      const auto begin = line.find_first_not_of(" \t\r");
      if (begin == std::string::npos || line[begin] == '#') continue;
      line = line.substr(begin, line.find_last_not_of(" \t\r") - begin + 1);
      if (!line.starts_with("dEQP-VK.")) {
        self(self, within.parent_path() / line);
        continue;
      }
      if (!validIdentifier(line)) throw std::runtime_error("invalid Vulkan mustpass case: " + line);
      bool keep = patterns.empty();
      if (literals.contains(line)) {
        matched.insert(line);
        keep = true;
      }
      for (const auto &pattern : globs)
        if (globMatches(pattern, line)) {
          matched.insert(pattern);
          keep = true;
        }
      if (keep && !selected.insert(line).second) throw std::runtime_error("duplicate Vulkan mustpass case: " + line);
    }
    if (!file.eof()) throw std::runtime_error("cannot read Vulkan mustpass manifest: " + path.string());
    active.erase(path);
  };
  read(read, manifest);
  if (const auto unmatched = patterns ^ find([&](const std::string &pattern) { return !matched.contains(pattern); }))
    throw std::runtime_error("Vulkan CTS selector matches no mustpass cases: " + *unmatched);
  if (selected.empty()) throw std::runtime_error("empty Vulkan mustpass selection");
  return selected ^ to_vector();
}
std::vector<ScheduledCase> discoverCases(const RunOptions &configuration, const Workload &workload, const fs::path &artifact,
                                         const std::map<std::string, std::string> &environment, const std::string &id,
                                         const fs::path &reports) {
  std::vector<ScheduledCase> tests;
  for (const auto &program : testPrograms(artifact, workload)) {
    const bool gtest = program.format == "gtest";
    const bool deqp = program.format == "deqp";
    auto command = gtest ? std::vector<std::string>{program.path.string(), "--gtest_list_tests"}
                         : std::vector<std::string>{program.path.string(), "--list-tests", "--reporter", "json", "--order", "lex"};
    std::vector<std::string> vulkanCases;
    if (deqp) {
      const auto archive = fs::path(environment.at(environment::SupportDir)) / "data/vulkan";
      vulkanCases = workload.vulkanMustpass.empty() ? workload.vulkanCases
                                                    : deqpMustpassCases(archive, workload.vulkanMustpass, workload.vulkanCases);
      const auto path = reports / (program.name + "-discovery-cases.txt");
      std::ofstream file(path, std::ios::binary | std::ios::trunc);
      file << deqpExactCaseList(vulkanCases);
      file.close();
      if (!file) throw std::runtime_error("cannot write Vulkan CTS selection: " + path.string());
      command = {program.path.string(), "--deqp-runmode=stdout-caselist", "--deqp-shadercache=disable",
                 "--deqp-caselist-file=" + path.string(), "--deqp-archive-dir=" + archive.string()};
    } else if (!gtest) {
      command.insert(command.end(), workload.specs.begin(), workload.specs.end());
    }
    // dEQP inventories can exceed hundreds of megabytes. Spool them to disk
    // and parse line by line instead of keeping a capped process capture.
    const auto listingPath = reports / (program.name + "-discovery.log");
    std::ofstream log;
    if (deqp) {
      log.open(listingPath, std::ios::binary | std::ios::trunc);
      if (!log) throw std::runtime_error("cannot write discovery log: " + listingPath.string());
    }
    const auto listing = runProcess(launched(configuration.launcher, std::move(command)), artifact, environment, 10min,
                                    fmt::format("[{}/{} discovery] ", id, program.name), !deqp, deqp ? &log : nullptr);
    if (deqp) {
      log.close();
      if (!log) throw std::runtime_error("cannot write discovery log: " + listingPath.string());
    }
    if (listing.status != 0)
      throw std::runtime_error(fmt::format("test discovery failed for {}/{} (exit {}): {}", id, program.name, listing.status,
                                           deqp ? listingPath.string() : listing.error));
    std::vector<std::string> names;
    if (deqp) {
      std::ifstream input(listingPath);
      names = deqpCaseNames(input);
    } else if (gtest) {
      names = gtestCaseNames(listing.output, program.name);
    } else {
      names = catchCaseNames(listing.output, program.name);
    }
    if (deqp) {
      const auto selected = vulkanCases ^ to<std::set>();
      const auto discovered = names ^ to<std::set>();
      if (const auto missing = selected ^ find([&](const std::string &name) { return !discovered.contains(name); }))
        throw std::runtime_error("Vulkan CTS selector matches no cases: " + *missing);
      std::erase_if(names, [&](const auto &name) { return !selected.contains(name); });
    }
    if (gtest && !workload.levelzeroCases.empty())
      std::erase_if(names, [&](const std::string &name) {
        return workload.levelzeroCases ^ none_match([&](const std::string &pattern) { return globMatches(pattern, name); });
      });
    if (names.size() > UINT32_MAX) throw std::runtime_error("test inventory is too large");
    const auto count = static_cast<uint32_t>(names.size());
    // Physical device throughput does not depend on host cores.
    const auto budget = [&](uint32_t seconds) {
      return scaledTimeout(seconds, configuration.timeoutScale,
                           configuration.isolatedLauncher ? configuration.threads : TimeoutReferenceCores);
    };
    const auto key = suiteTimeoutEnvironment(program.name);
    const auto timeout =
        (key.empty() ? std::nullopt : environment ^ get_maybe(std::string(key)))
        ^ map([&](const std::string &configured) { return budget(parseTimeout(configured, artifact / "test-programs.txt")); })
        ^ get_or_else(budget(suiteTimeout(program.name, program.timeoutSeconds, configuration.isolatedLauncher)));
    for (uint32_t index = 0; index < count; ++index)
      tests.push_back(
          {.identity = std::move(names[index]), .program = program.name, .timeoutSeconds = timeout, .index = index, .count = count});
  }
  return tests;
}
std::optional<TargetExecution> prepareTarget(const RunOptions &configuration, const Workload &workload,
                                             const profile::ResolvedTarget &target, const fs::path &runDirectory) {
  const auto artifact = artifactDirectory(configuration, target);
  const auto metadata = validateArtifact(artifact, target);
  // Program filters can exclude a backend without loading its runtime or probing a device.
  if (testPrograms(artifact, workload).empty()) return std::nullopt;
  const auto support = supportDirectory(configuration, target, metadata);
  if (!fs::is_directory(support)) throw std::runtime_error("missing artifact support: " + support.string());
  auto environment = baseEnvironment();
  for (const auto &[key, value] : target.environment)
    environment[key] = value;
  environment[environment::TargetBackendName] = target.backend;
  environment[environment::TargetArch] = target.architecture.value_or("");
  if (configuration.isolatedLauncher) applySuiteIsolatedEnvironment(environment, target.backend);
  prepareRuntimeEnvironment(environment, artifact, support, target.backend);
  const auto reports = runDirectory / target.id;
  fs::create_directories(reports);
  ExecutionConfig exec{.directory = artifact.string(),
                       .reportDirectory = reports.string(),
                       .device = {},
                       .environment = environment,
                       .launcher = configuration.launcher,
                       .skips = configuration.isolatedLauncher ? std::vector<std::string>{} : target.skips,
                       .specs = workload.specs,
                       .deviceLock = !configuration.direct && !configuration.isolatedLauncher};
  applyDeviceProbe(exec, probeDevice(configuration, artifact, environment, target.id));
  // Re-probe with the exact child environment: selection must survive visibility remapping.
  const auto verified = probeDevice(configuration, artifact, exec.environment, target.id);
  if (verified.device != exec.device) throw std::runtime_error("device selection changed after propagation for " + target.id);
  auto tests = discoverCases(configuration, workload, artifact, exec.environment, target.id, reports);
  if (tests.empty()) return std::nullopt;
  std::ofstream record(reports / "environment.txt", std::ios::binary | std::ios::trunc);
  for (const auto &[key, value] : exec.environment)
    record << key << '=' << value << '\n';
  record.close();
  if (!record) throw std::runtime_error("cannot record the environment of " + target.id);
  return TargetExecution{.id = target.id, .config = std::move(exec), .tests = std::move(tests)};
}
CaseResult executeCase(const ExecutionConfig &config, const ScheduledCase &test) {
  if (!validName(test.program)) throw std::invalid_argument("invalid test program");
  if (config.skips
      ^ exists([&](const std::string &pattern) { return globMatches(pattern, test.program) || globMatches(pattern, test.identity); }))
    return {.status = SkipReturnCode, .output = "SKIP " + test.identity + ": machine profile\n"};
  const auto environmentValue = [&](const char *key) -> std::string_view {
    const auto entry = config.environment.find(key);
    return entry == config.environment.end() ? std::string_view{} : entry->second;
  };
  const auto backend = environmentValue(environment::TargetBackendName);
  const auto support = environmentValue(environment::SupportDir);
  if (test.program == "unified" && !support.empty() && (backend == "cuda" || backend == "hip")) {
    const auto *const libraryName = backend == "cuda" ? "libcublas.so" : "librocblas.so";
    // Match the same artifact-first search order as the runtime environment.
    const auto reason =
        std::vector{fs::path(config.directory), fs::path(support)} ^ map([&](const fs::path &root) { return root / "lib" / libraryName; })
        ^ find([](const fs::path &library) { return fs::is_regular_file(library); }) ^ map([&](const fs::path &library) {
            return suiteBlasSkip(backend, environmentValue(environment::TargetArch), environmentValue(environment::CudaCodeMode),
                                 fs::canonical(library).filename().string(), test.program, test.identity);
          })
        ^ filter([](std::string_view skip) { return !skip.empty(); });
    if (reason) return {.status = SkipReturnCode, .output = fmt::format("SKIP {}: {}\n", test.identity, *reason)};
  }
  const auto programs = testPrograms(config.directory, Workload{});
  const auto selected = programs ^ find_cref([&](const TestProgram &candidate) { return candidate.name == test.program; });
  if (!selected) throw std::runtime_error("test program absent from manifest");
  const bool gtest = selected->get().format == "gtest";
  const bool deqp = selected->get().format == "deqp";
  const auto &program = selected->get().path;
  std::vector<std::string> arguments;
  const auto report = fs::path(config.reportDirectory) / test.program / fmt::to_string(test.index);
  fs::create_directories(report);
  auto environment = config.environment;
  if (deqp) {
    arguments = {program.string(),
                 "--deqp-case=" + test.identity,
                 "--deqp-log-filename=" + (report / "result.qpa").string(),
                 "--deqp-archive-dir=" + (fs::path(environment.at(environment::SupportDir)) / "data/vulkan").string(),
                 "--deqp-log-images=disable",
                 "--deqp-log-shader-sources=disable",
                 "--deqp-shadercache=disable",
                 "--deqp-vk-device-id=" + environment.at(environment::VulkanDeviceId)};
    fs::remove(report / "result.qpa");
  } else {
    if (gtest) {
      arguments = {"--gtest_filter=" + test.identity};
    } else {
      const bool sharded = suiteUsesCatchSharding(test.program);
      arguments = catchShardArguments(config.specs, sharded ? test.index : 0, sharded ? test.count : 1);
    }
    arguments.insert(arguments.begin(), program.string());
  }
  environment[environment::TestName] = test.identity;
  environment[environment::ReportDir] = report.string();
  const auto outputPath = report / "output.log";
  std::ofstream output(outputPath, std::ios::binary);
  output.exceptions(std::ios::failbit | std::ios::badbit);
  // Save both streams as they arrive, including output preceding a timeout or crash.
  const auto process = runProcess(launched(config.launcher, std::move(arguments)), gtest ? fs::path(config.directory) : report, environment,
                                  std::chrono::seconds(test.timeoutSeconds), "[" + test.identity + "] ", false, &output);
  auto status = process.status;
  std::string record;
  if (status == 0 && deqp) {
    std::ifstream qpa(report / "result.qpa", std::ios::binary);
    const auto result = deqpResult(std::string(std::istreambuf_iterator<char>(qpa), {}), test.identity);
    record = fmt::format("dEQP {}: {}\n", test.identity, result.statusCode);
    output << record;
    status = result.status;
  }
  output.close();
  auto message = process.timedOut ? fmt::format("timed out after {} s (exit status {})", test.timeoutSeconds, status)
                                  : fmt::format("exit status {}", status);
  record += fmt::format("conformance: {}, full output: {}\n", message, outputPath.string());
  return {.status = status, .output = std::move(record), .message = std::move(message)};
}
namespace {
bool skipped(const CaseResult &result) { return result.status == SkipReturnCode; }
bool failed(const CaseResult &result) { return result.status != 0 && !skipped(result); }
const char *resultLabel(const CaseResult &result) {
  if (result.status == 0) return "Passed";
  return skipped(result) ? "Skipped" : "***Failed";
}
std::string failureMessage(const CaseResult &result) {
  return result.message.empty() ? fmt::format("exit status {}", result.status) : result.message;
}
std::string xmlEscaped(std::string_view value) {
  std::string result;
  result.reserve(value.size());
  for (const char c : value)
    switch (c) {
      case '&': result += "&amp;"; break;
      case '<': result += "&lt;"; break;
      case '>': result += "&gt;"; break;
      case '"': result += "&quot;"; break;
      default: result += static_cast<unsigned char>(c) < 0x20 && c != '\t' && c != '\n' && c != '\r' ? '?' : c;
    }
  return result;
}
void writeJunit(const fs::path &path, const std::vector<CaseReport> &reports) {
  const auto failures = reports ^ count([](const CaseReport &report) { return failed(report.result); });
  const auto skips = reports ^ count([](const CaseReport &report) { return skipped(report.result); });
  const auto seconds = reports ^ fold_left(0.0, [](double sum, const CaseReport &report) { return sum + report.result.elapsed.count(); });
  std::ofstream file(path, std::ios::binary | std::ios::trunc);
  file << fmt::format("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                      "<testsuite name=\"conformance\" tests=\"{}\" failures=\"{}\" skipped=\"{}\" time=\"{:.3f}\">\n",
                      reports.size(), failures, skips, seconds);
  for (const auto &[name, result] : reports) {
    file << fmt::format(R"(  <testcase name="{}" classname="{}" time="{:.3f}")", xmlEscaped(name),
                        xmlEscaped(name.substr(0, name.find('/'))), result.elapsed.count());
    if (result.status == 0) {
      file << "/>\n";
      continue;
    }
    file << ">\n    "
         << (skipped(result) ? std::string("<skipped/>") : fmt::format("<failure message=\"{}\"/>", xmlEscaped(failureMessage(result))))
         << "\n    <system-out>" << xmlEscaped(result.output) << "</system-out>\n  </testcase>\n";
  }
  file << "</testsuite>\n";
  file.close();
  if (!file) throw std::runtime_error("cannot write JUnit report " + path.string());
}
} // namespace
std::vector<CaseReport> runCases(const std::vector<TargetExecution> &targets, uint32_t jobs, const CaseExecutor &execute) {
  const Interruption interruption;
  const auto total = targets ^ fold_left(size_t{0}, [](size_t sum, const TargetExecution &target) { return sum + target.tests.size(); });
  auto results = targets ^ map([](const TargetExecution &target) { return std::vector<std::optional<CaseResult>>(target.tests.size()); });
  // Cases on one physical device run one at a time; a device without an identity excludes every identified one.
  const auto resource = [](const TargetExecution &target) {
    return target.config.deviceLock ? std::string_view(target.config.device) : std::string_view{};
  };
  std::set<std::string_view> busy;
  const auto available = [&](std::string_view device) {
    return device.empty() || (!busy.contains("unknown") && (device == "unknown" ? busy.empty() : !busy.contains(device)));
  };
  std::vector<size_t> next(targets.size(), 0);
  size_t cursor = 0;
  size_t started = 0;
  size_t finished = 0;
  // Interleave targets so every route makes progress from the start.
  const auto claim = [&]() -> std::optional<std::pair<size_t, size_t>> {
    for (size_t step = 0; step < targets.size(); ++step) {
      const auto index = (cursor + step) % targets.size();
      const auto device = resource(targets[index]);
      if (next[index] == targets[index].tests.size() || !available(device)) continue;
      if (!device.empty()) busy.insert(device);
      cursor = index + 1;
      ++started;
      return std::pair{index, next[index]++};
    }
    return std::nullopt;
  };
  std::mutex mutex;
  std::condition_variable released;
  const auto width = fmt::to_string(total).size();
  {
    std::vector<std::jthread> workers;
    for (size_t worker = 0; worker < std::min<size_t>(jobs, total); ++worker)
      workers.emplace_back([&] {
        std::unique_lock lock(mutex);
        for (;;) {
          std::optional<std::pair<size_t, size_t>> claimed;
          // The signal handler cannot notify, so waiting polls for interruption.
          while (started < total && !Interruption::signal() && !(claimed = claim()))
            released.wait_for(lock, 100ms);
          if (!claimed) return;
          const auto [index, position] = *claimed;
          const auto &target = targets[index];
          const auto &test = target.tests[position];
          lock.unlock();
          const auto begin = std::chrono::steady_clock::now();
          CaseResult result;
          try {
            result = execute(target.config, test);
          } catch (const std::exception &error) {
            result = {.status = 2, .output = fmt::format("conformance: {}\n", error.what())};
          }
          result.elapsed = std::chrono::steady_clock::now() - begin;
          lock.lock();
          if (const auto device = resource(target); !device.empty()) busy.erase(device);
          std::osyncstream message(std::cout);
          message << fmt::format("[{:>{}}/{}] {}/{}/{} ... {} {:.2f} s\n", ++finished, width, total, target.id, test.program, test.identity,
                                 resultLabel(result), result.elapsed.count());
          if (failed(result)) message << result.output;
          message << std::flush;
          results[index][position] = std::move(result);
          released.notify_all();
        }
      });
  }
  std::vector<CaseReport> reports;
  for (size_t index = 0; index < targets.size(); ++index)
    for (size_t position = 0; position < targets[index].tests.size(); ++position)
      if (auto &result = results[index][position]) {
        const auto &test = targets[index].tests[position];
        reports.push_back({.name = fmt::format("{}/{}/{}", targets[index].id, test.program, test.identity), .result = std::move(*result)});
      }
  return reports;
}
int32_t run(RunOptions configuration) {
  validateRunOptions(configuration);
  configuration.threads = positiveSetting(environment::ThreadCount, availableThreadCount());
  configuration.timeoutScale = positiveScale(environment::TimeoutScale);
  const auto *jobs = std::getenv(environment::JobCount);
  const auto jobCount = configuredJobs(jobs ? jobs : "", configuration.threads);
  const auto *shard = std::getenv(environment::CaseShard);
  if (shard && *shard) (void)caseShard(shard);
  auto settings = baseEnvironment();
  if (const auto *mode = std::getenv(environment::CudaCodeMode)) settings[environment::CudaCodeMode] = mode;
  for (const auto &[key, value] : settings)
    if (const auto problem = invalidSuiteSetting(key, value)) throw std::invalid_argument(*problem);
  std::vector<profile::ResolvedTarget> targets;
  std::string name = "direct";
  if (configuration.direct) {
    targets = profile::directTargets(configuration.selectors);
  } else {
    const auto resolved = profile::resolveProfile(configuration.profiles, configuration.machine);
    name = resolved.name;
    targets = uniqueRoutes(profile::resolveTargets(resolved, configuration.selectors), configuration.isolatedLauncher);
  }
  // Validate the complete requested inventory before probing or scheduling any target.
  for (const auto &target : targets)
    (void)validateArtifact(artifactDirectory(configuration, target), target);
  const auto workload = loadWorkload(configuration);
  const auto root = configuration.output / name;
  fs::create_directories(root);
  fs::path reports;
  for (uint64_t attempt = 0;; ++attempt) {
    reports = root / fmt::format("run-{}-{}", std::chrono::system_clock::now().time_since_epoch().count(), attempt);
    if (fs::create_directory(reports)) break;
  }
  if (std::string buffer; glz::write_file_toml(workload, (reports / (configuration.workload + ".toml")).string(), buffer))
    throw std::runtime_error("cannot write resolved workload to " + reports.string());
  const auto workers = std::min(static_cast<size_t>(jobCount), targets.size());
  const auto started = std::chrono::steady_clock::now();
  std::cout << "conformance reports: " << reports << '\n';
  std::cout << "conformance workload: " << configuration.workload << '\n';
  std::cout << "conformance discovery: " << targets.size() << " routes, " << workers << " workers\n" << std::flush;
  const Interruption interruption;
  std::vector<std::optional<TargetExecution>> discovered(targets.size());
  std::atomic<size_t> next{0};
  std::atomic<bool> aborted{false};
  std::mutex failureMutex;
  std::exception_ptr failure;
  {
    std::vector<std::jthread> threads;
    threads.reserve(workers);
    for (size_t worker = 0; worker < workers; ++worker) {
      threads.emplace_back([&] {
        while (!aborted.load() && !Interruption::signal()) {
          const auto index = next.fetch_add(1);
          if (index >= targets.size()) break;
          const auto &target = targets[index];
          try {
            std::osyncstream(std::cout) << "conformance discovering " << target.id << '\n' << std::flush;
            auto value = prepareTarget(configuration, workload, target, reports);
            if (value) {
              std::osyncstream message(std::cout);
              message << "conformance target " << value->id;
              if (value->config.deviceLock) message << " resolved to " << value->config.device;
              message << " with " << value->tests.size() << " test(s)\n" << std::flush;
            } else {
              std::osyncstream(std::cout) << "conformance target " << target.id << " omitted: no tests match workload "
                                          << configuration.workload << '\n'
                                          << std::flush;
            }
            discovered[index] = std::move(value);
          } catch (...) {
            const std::scoped_lock lock(failureMutex);
            if (!failure) failure = std::current_exception();
            aborted.store(true);
          }
        }
      });
    }
  }
  if (const auto signal = Interruption::signal()) return 128 + signal;
  if (failure) std::rethrow_exception(failure);
  std::vector<TargetExecution> prepared;
  for (auto &value : discovered)
    if (value) prepared.push_back(std::move(*value));
  // Neighbouring cases of a suite often share a cost, so interleaving spreads them across the shards of a route.
  if (shard && *shard) {
    selectCaseShard(prepared, shard);
    std::cout << "conformance case shard: " << shard << '\n';
  }
  std::cout << "conformance discovery completed in " << std::chrono::duration<double>(std::chrono::steady_clock::now() - started).count()
            << "s\n"
            << std::flush;
  if (prepared.empty()) throw std::runtime_error("conformance workload selected no tests: " + configuration.workload);
  const auto executed = std::chrono::steady_clock::now();
  const auto results = runCases(prepared, static_cast<uint32_t>(jobCount), executeCase);
  writeJunit(reports / "junit.xml", results);
  const auto total = prepared ^ fold_left(size_t{0}, [](size_t sum, const TargetExecution &target) { return sum + target.tests.size(); });
  const auto skips = results ^ count([](const CaseReport &report) { return skipped(report.result); });
  const auto failures = results | filter([](const CaseReport &report) { return failed(report.result); })
                        | map([](const CaseReport &report) { return &report; }) | to_vector();
  std::cout << fmt::format("conformance: {} passed, {} skipped, {} failed, {} not run of {} cases in {:.1f} s\n",
                           results.size() - static_cast<size_t>(skips) - failures.size(), skips, failures.size(), total - results.size(),
                           total, std::chrono::duration<double>(std::chrono::steady_clock::now() - executed).count());
  for (const auto *report : failures)
    std::cout << "  failed: " << report->name << " (" << failureMessage(report->result) << ")\n";
  std::cout << "conformance JUnit report: " << (reports / "junit.xml").string() << '\n';
  if (const auto signal = Interruption::signal()) return 128 + signal;
  return failures.empty() && results.size() == total ? 0 : 1;
}
} // namespace conformance::driver
