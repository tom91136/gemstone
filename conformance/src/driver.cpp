#include "driver.hpp"

#include <algorithm>
#include <charconv>
#include <fstream>
#include <iostream>
#include <limits>
#include <memory>
#include <ranges>
#include <regex>
#include <set>
#include <sstream>
#include <stdexcept>
#include <thread>
#include <tuple>

#include <aspartame/all.hpp>
#include <glaze/json.hpp>
#include <glaze/toml.hpp>

#include "conformance/device.hpp"

#include "device_lock.hpp"
#include "device_selector.hpp"
#include "environment.hpp"
#include "suite_policy.hpp"
#include <fmt/format.h>

namespace conformance::driver {
using namespace aspartame;
namespace fs = std::filesystem;
using namespace std::chrono_literals;
// Program timeouts are recorded for a reference host; emulated work
// parallelises across host cores, so a host with fewer cores gets more time and
// one with more keeps at least the recorded budget.
inline constexpr uint32_t TimeoutReferenceCores = 32;
uint32_t scaled_timeout(uint32_t seconds) {
  const auto cores = static_cast<uint32_t>(std::max(1u, std::thread::hardware_concurrency()));
  const auto scale = std::clamp(static_cast<double>(TimeoutReferenceCores) / static_cast<double>(cores), 0.5, 4.0);
  return std::max(1u, static_cast<uint32_t>(static_cast<double>(seconds) * scale + 0.5));
}
struct ArtifactMetadata {
  std::string backend, architecture, support;
};
struct TestProgram {
  std::string name;
  fs::path path;
  uint32_t timeout_seconds;
  std::string format;
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
struct WorkloadFields {
  std::string extends;
  std::optional<std::vector<std::string>> specs, include_programs, levelzero_cases, vulkan_cases, exclude_programs;
  std::optional<std::string> vulkan_mustpass;
};
bool valid_identifier(std::string_view value) {
  return !value.empty() && std::ranges::all_of(value, [](char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') || c == '.' || c == '_' || c == '-';
  });
}
void validate_run_options(RunOptions &c) {
  if (c.artifacts.empty() || c.output.empty()) throw std::invalid_argument("run requires --artifacts and --output");
  if (c.direct) {
    if (!c.profiles.empty() || c.machine) throw std::invalid_argument("--direct cannot use --profiles or --machine");
    if (c.workloads.empty()) throw std::invalid_argument("--direct requires --workloads");
  } else if (c.profiles.empty()) throw std::invalid_argument("profile runs require --profiles");
  if (!valid_identifier(c.workload) || c.workload == "." || c.workload == "..") throw std::invalid_argument("invalid workload name");
  if (c.isolated_launcher && c.launcher.empty()) throw std::invalid_argument("--isolated-launcher requires --launcher -- command ...");
  if (!c.profiles.empty()) c.profiles = fs::absolute(c.profiles).lexically_normal();
  if (c.workloads.empty()) c.workloads = c.profiles.parent_path() / "workloads";
  c.workloads = fs::absolute(c.workloads).lexically_normal();
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
std::vector<std::string> deqp_case_names(std::string_view listing) {
  std::istringstream input{std::string(listing)};
  return deqp_case_names(input);
}
std::vector<std::string> deqp_case_names(std::istream &input) {
  std::set<std::string> names;
  std::string line;
  while (std::getline(input, line)) {
    if (!line.empty() && line.back() == '\r') line.pop_back();
    if (!line.starts_with("TEST: ")) continue;
    auto name = line.substr(6);
    if (!name.starts_with("dEQP-VK.") || !valid_identifier(name) || !names.insert(name).second)
      throw std::runtime_error("invalid or duplicate dEQP case: " + name);
  }
  if (!input.eof()) throw std::runtime_error("cannot read dEQP inventory");
  if (names.empty()) throw std::runtime_error("empty dEQP inventory");
  return {names.begin(), names.end()};
}
int32_t deqp_result(std::string_view qpa, std::string_view identity) {
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
      || document.substr(header, headerEnd - header).find("CasePath=\"" + std::string(identity) + "\"") == std::string::npos)
    throw std::runtime_error("mismatched dEQP XML case");
  static const std::regex result(R"re(<Result StatusCode="([A-Za-z]+)">[^<]*</Result>\s*</TestCaseResult>\s*$)re");
  std::smatch match;
  if (!std::regex_search(document, match, result) || document.find("<Result ") != static_cast<size_t>(match.position()))
    throw std::runtime_error("invalid dEQP terminal result");
  const auto status = match[1].str();
  std::cout << "dEQP " << identity << ": " << status << '\n';
  // Vulkan CTS explicitly permits quality and compatibility warnings. The
  // original status stays in the QPA and console output for separate reporting.
  if (status == "Pass" || status == "QualityWarning" || status == "CompatibilityWarning") return 0;
  if (status == "NotSupported") return 4;
  // Do not apply waivers implicitly.
  return 1;
}
std::vector<std::string> gtest_case_names(std::string_view listing, std::string_view program) {
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
      if (name.find("DISABLED_") == std::string::npos) result.push_back(name);
    } else {
      if (line.back() != '.') throw std::runtime_error("invalid Google Test suite from " + std::string(program));
      suite = line;
    }
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
    if (!isolated || seen.emplace(target.backend, target.architecture).second) {
      result.push_back(target);
      if (isolated) result.back().deviceLock = false;
    } else std::cout << "conformance: identical isolated route " << target.id << " omitted before discovery\n";
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
  std::set<std::string> active;
  const auto read = [&](auto &&self, const std::string &name) -> Workload {
    if (!valid_identifier(name) || name == "." || name == "..") throw std::invalid_argument("invalid workload name: " + name);
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
    replace(workload.include_programs, fields.include_programs);
    replace(workload.levelzero_cases, fields.levelzero_cases);
    replace(workload.vulkan_cases, fields.vulkan_cases);
    replace(workload.vulkan_mustpass, fields.vulkan_mustpass);
    replace(workload.exclude_programs, fields.exclude_programs);
    active.erase(name);
    return workload;
  };
  auto workload = read(read, configuration.workload);
  if (workload.specs.empty()) throw std::invalid_argument("conformance workload needs at least one Catch test spec");
  return workload;
}
uint32_t parse_timeout(std::string_view value, const fs::path &manifest) {

  uint64_t result = 0;
  const auto [end, error] = std::from_chars(value.data(), value.data() + value.size(), result);
  if (error != std::errc{} || end != value.data() + value.size() || result == 0 || result > MaximumTimeoutSeconds)
    throw std::runtime_error(fmt::format("invalid timeout in {}", manifest.string()));
  return static_cast<uint32_t>(result);
}
std::vector<TestProgram> test_programs(const fs::path &artifact, const Workload &workload) {
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
    const auto formatAt = line.find('\t', separator + 1);
    if (formatAt == std::string::npos) throw std::runtime_error("missing test program format: " + manifest.string());
    const auto format = line.substr(formatAt + 1);
    if (format != "catch2" && format != "gtest" && format != "deqp") throw std::runtime_error("invalid test program format");
    result.push_back({std::move(name), std::move(path),
                      parse_timeout(std::string_view(line).substr(separator + 1, formatAt - separator - 1), manifest), format});
  }
  if (result.empty()) throw std::runtime_error("artifact contains no test programs: " + artifact.string());
  std::erase_if(result, [&](const TestProgram &program) {
    return std::ranges::find(workload.exclude_programs, program.name) != workload.exclude_programs.end()
           || (!workload.include_programs.empty() && std::ranges::none_of(workload.include_programs, [&](const std::string &pattern) {
                return glob_matches(pattern.c_str(), program.name.c_str());
              }));
  });
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
  const auto result =
      run_process(launched(configuration.launcher, {probe.string()}), artifact, environment, 2min, fmt::format("[{} device] ", id), true);
  if (result.status != 0)
    throw std::runtime_error("device probe failed for " + id + " (including runtime/selector verification): " + result.error);
  DeviceProbeResult parsed;
  if (const auto error = glz::read_json(parsed, result.output); error)
    throw std::runtime_error("invalid device probe JSON for " + id + ": " + glz::format_error(error, result.output));
  return parsed;
}
std::vector<std::string> deqp_case_selections(const std::vector<std::string> &patterns) {
  constexpr size_t limit = 16 * 1024;
  if (patterns.empty()) throw std::runtime_error("Vulkan CTS requires an explicit vulkan_cases selection");
  std::vector<std::string> batches;
  std::string batch;
  for (const auto &pattern : patterns) {
    if (pattern.empty() || pattern.size() > limit) throw std::runtime_error("invalid Vulkan CTS selector length");
    if (!batch.empty() && batch.size() + 1 + pattern.size() > limit) {
      batches.push_back(std::move(batch));
      batch.clear();
    }
    if (!batch.empty()) batch += ',';
    batch += pattern;
  }
  if (!batch.empty()) batches.push_back(std::move(batch));
  return batches;
}
std::optional<std::string> deqp_exact_case_list(const std::vector<std::string> &patterns) {
  if (patterns.empty()) throw std::runtime_error("Vulkan CTS requires an explicit vulkan_cases selection");
  for (const auto &pattern : patterns)
    if (pattern.empty() || pattern.size() > 16 * 1024) throw std::runtime_error("invalid Vulkan CTS selector length");
  // The CTS file parser accepts literal paths, not wildcard expressions. Restrict
  // file contents to path characters so a selector cannot introduce another line.
  if (!std::ranges::all_of(patterns, valid_identifier)) return std::nullopt;
  const std::set<std::string> names(patterns.begin(), patterns.end());
  std::string result;
  for (const auto &name : names)
    result += name + '\n';
  return result;
}
std::vector<std::string> deqp_mustpass_cases(const fs::path &root, const fs::path &manifest, const std::vector<std::string> &patterns) {
  const auto base = fs::canonical(root);
  std::set<fs::path> active;
  std::set<std::string> selected, literals, matched;
  std::vector<std::string> globs;
  for (const auto &pattern : patterns) {
    if (pattern.empty()) throw std::runtime_error("empty Vulkan CTS selector");
    if (valid_identifier(pattern)) literals.insert(pattern);
    else globs.push_back(pattern);
  }
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
      if (!valid_identifier(line)) throw std::runtime_error("invalid Vulkan mustpass case: " + line);
      bool keep = patterns.empty();
      if (literals.contains(line)) {
        matched.insert(line);
        keep = true;
      }
      for (const auto &pattern : globs)
        if (glob_matches(pattern.c_str(), line.c_str())) {
          matched.insert(pattern);
          keep = true;
        }
      if (keep && !selected.insert(line).second) throw std::runtime_error("duplicate Vulkan mustpass case: " + line);
    }
    if (!file.eof()) throw std::runtime_error("cannot read Vulkan mustpass manifest: " + path.string());
    active.erase(path);
  };
  read(read, manifest);
  for (const auto &pattern : patterns)
    if (!matched.contains(pattern)) throw std::runtime_error("Vulkan CTS selector matches no mustpass cases: " + pattern);
  if (selected.empty()) throw std::runtime_error("empty Vulkan mustpass selection");
  return {selected.begin(), selected.end()};
}
std::vector<ScheduledCase> discover_cases(const RunOptions &configuration, const Workload &workload, const fs::path &artifact,
                                          const std::map<std::string, std::string> &environment, const std::string &id,
                                          const fs::path &reports) {
  std::vector<ScheduledCase> tests;
  for (const auto &program : test_programs(artifact, workload)) {
    const bool gtest = program.format == "gtest";
    const bool deqp = program.format == "deqp";
    auto command = gtest ? std::vector<std::string>{program.path.string(), "--gtest_list_tests"}
                         : std::vector<std::string>{program.path.string(), "--list-tests", "--reporter", "json", "--order", "lex"};
    std::vector<std::vector<std::string>> discoveryCommands;
    bool exactDeqpSelection = false;
    auto vulkanCases = workload.vulkan_cases;
    if (deqp && !workload.vulkan_mustpass.empty())
      vulkanCases =
          deqp_mustpass_cases(fs::path(environment.at("CONFORMANCE_SUPPORT_ROOT")) / "data/vulkan", workload.vulkan_mustpass, vulkanCases);
    if (deqp) {
      if (vulkanCases.empty()) throw std::runtime_error("Vulkan CTS requires an explicit vulkan_cases selection");
      std::vector<std::string> selections;
      std::vector<std::string> literals, patterns;
      for (const auto &selection : vulkanCases)
        (valid_identifier(selection) ? literals : patterns).push_back(selection);
      exactDeqpSelection = patterns.empty();
      if (!literals.empty()) {
        const auto path = reports / (program.name + "-discovery-cases.txt");
        std::ofstream file(path, std::ios::binary | std::ios::trunc);
        file << *deqp_exact_case_list(literals);
        file.close();
        if (!file) throw std::runtime_error("cannot write Vulkan CTS selection: " + path.string());
        selections.push_back("--deqp-caselist-file=" + path.string());
      }
      if (!patterns.empty())
        for (const auto &selection : deqp_case_selections(patterns))
          selections.push_back("--deqp-case=" + selection);
      for (const auto &selection : selections)
        discoveryCommands.push_back(
            {program.path.string(), "--deqp-runmode=stdout-caselist", "--deqp-shadercache=disable", selection,
             "--deqp-archive-dir=" + (fs::path(environment.at("CONFORMANCE_SUPPORT_ROOT")) / "data/vulkan").string()});
    } else {
      if (!gtest) command.insert(command.end(), workload.specs.begin(), workload.specs.end());
      discoveryCommands.push_back(std::move(command));
    }
    std::vector<std::string> names;
    std::set<std::string> discovered;
    for (auto &discovery : discoveryCommands) {
      // dEQP inventories can exceed hundreds of megabytes. Spool them to disk
      // and parse line by line instead of keeping a capped process capture.
      const auto listingPath = reports / (program.name + "-discovery.log");
      std::ofstream log;
      if (deqp) {
        log.open(listingPath, std::ios::binary | std::ios::trunc);
        if (!log) throw std::runtime_error("cannot write discovery log: " + listingPath.string());
      }
      const auto listing = run_process(launched(configuration.launcher, std::move(discovery)), artifact, environment, 10min,
                                       fmt::format("[{}/{} discovery] ", id, program.name), !deqp, deqp ? &log : nullptr);
      if (deqp) {
        log.close();
        if (!log) throw std::runtime_error("cannot write discovery log: " + listingPath.string());
      }
      if (listing.status != 0)
        throw std::runtime_error(fmt::format("test discovery failed for {}/{} (exit {}): {}", id, program.name, listing.status,
                                             deqp ? listingPath.string() : listing.error));
      std::ifstream input;
      if (deqp) input.open(listingPath);
      const auto batch = deqp    ? deqp_case_names(input)
                         : gtest ? gtest_case_names(listing.output, program.name)
                                 : catch_case_names(listing.output, program.name);
      for (const auto &name : batch)
        if (discovered.insert(name).second) names.push_back(name);
    }
    if (deqp) {
      if (exactDeqpSelection) {
        const std::set<std::string> selected(vulkanCases.begin(), vulkanCases.end());
        for (const auto &name : selected)
          if (!discovered.contains(name)) throw std::runtime_error("Vulkan CTS selector matches no cases: " + name);
        std::erase_if(names, [&](const auto &name) { return !selected.contains(name); });
      } else {
        for (const auto &pattern : vulkanCases)
          if (std::ranges::none_of(names, [&](const auto &name) { return glob_matches(pattern.c_str(), name.c_str()); }))
            throw std::runtime_error("Vulkan CTS selector matches no cases: " + pattern);
        std::erase_if(names, [&](const auto &name) {
          return std::ranges::none_of(vulkanCases, [&](const auto &pattern) { return glob_matches(pattern.c_str(), name.c_str()); });
        });
      }
    }
    if (gtest && !workload.levelzero_cases.empty())
      std::erase_if(names, [&](const std::string &name) {
        return std::ranges::none_of(workload.levelzero_cases,
                                    [&](const std::string &pattern) { return glob_matches(pattern.c_str(), name.c_str()); });
      });
    if (names.size() > UINT32_MAX) throw std::runtime_error("test inventory is too large");
    const auto count = static_cast<uint32_t>(names.size());
    auto timeout = scaled_timeout(suite_timeout(program.name, program.timeout_seconds, configuration.isolated_launcher));
    if (const auto key = suite_timeout_environment(program.name); !key.empty()) {
      if (const auto found = environment.find(std::string(key)); found != environment.end())
        timeout = scaled_timeout(parse_timeout(found->second, artifact / "test-programs.txt"));
    }
    for (uint32_t index = 0; index < count; ++index)
      tests.push_back({std::move(names[index]), program.name, timeout, index, count});
  }
  return tests;
}
std::optional<TargetExecution> prepare_target(const RunOptions &configuration, const Workload &workload,
                                              const profile::ResolvedTarget &target, const fs::path &run_directory) {
  const auto artifact = artifact_directory(configuration, target);
  const auto metadata = validate_artifact(artifact, target);
  // Program filters can exclude a backend without loading its runtime or probing a device.
  if (test_programs(artifact, workload).empty()) return std::nullopt;
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
                       target.deviceLock};
  apply_device_probe(exec, probe_device(configuration, artifact, environment, target.id));
  // Re-probe with the exact child environment: selection must survive visibility remapping.
  const auto verified = probe_device(configuration, artifact, exec.environment, target.id);
  if (verified.device != exec.device) throw std::runtime_error("device selection changed after propagation for " + target.id);
  auto tests = discover_cases(configuration, workload, artifact, exec.environment, target.id, reports);
  if (tests.empty()) return std::nullopt;
  const auto path = run_directory / "ctest/targets" / (target.id + ".json");
  write_execution_config(path, exec);
  return TargetExecution{target.id, target.backend, exec.deviceLock ? exec.device : "", path, std::move(tests)};
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
    file << "PROPERTIES SKIP_RETURN_CODE " << SkipReturnCode << " TIMEOUT " << (timeout + TimeoutMarginSeconds);
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
    return SkipReturnCode;
  }
  const auto programs = test_programs(config.directory, Workload{});
  const auto selected = std::ranges::find(programs, test.program, &TestProgram::name);
  if (selected == programs.end()) throw std::runtime_error("test program absent from manifest");
  const bool gtest = selected->format == "gtest";
  const bool deqp = selected->format == "deqp";
  const auto &program = selected->path;
  std::vector<std::string> arguments;
  const auto report = fs::path(config.report_directory) / test.program / fmt::to_string(test.index);
  fs::create_directories(report);
  auto environment = config.environment;
  if (deqp) {
    arguments = {program.string(),
                 "--deqp-case=" + test.identity,
                 "--deqp-log-filename=" + (report / "result.qpa").string(),
                 "--deqp-archive-dir=" + (fs::path(environment.at("CONFORMANCE_SUPPORT_ROOT")) / "data/vulkan").string(),
                 "--deqp-log-images=disable",
                 "--deqp-log-shader-sources=disable",
                 "--deqp-shadercache=disable",
                 "--deqp-vk-device-id=" + environment.at("CONFORMANCE_VULKAN_DEVICE_ID")};
    fs::remove(report / "result.qpa");
  } else {
    if (gtest) arguments = {"--gtest_filter=" + test.identity};
    else {
      const bool sharded = suite_uses_catch_sharding(test.program);
      arguments = catch_shard_arguments(config.specs, sharded ? test.index : 0, sharded ? test.count : 1);
    }
    arguments.insert(arguments.begin(), program.string());
  }
  environment["CONFORMANCE_TEST_IDENTITY"] = test.identity;
  environment["CONFORMANCE_REPORT_DIRECTORY"] = report.string();
  std::unique_ptr<DeviceLock> lock;
  if (config.deviceLock) lock = std::make_unique<DeviceLock>(physical_device_from_key(config.device), 10min);
  const auto output_path = report / "output.log";
  std::ofstream output(output_path, std::ios::binary);
  output.exceptions(std::ios::failbit | std::ios::badbit);
  std::cout << "conformance: full output: " << output_path << std::endl;
  // Save both streams as they arrive, including output preceding a timeout or crash.
  const auto result = run_process(launched(config.launcher, std::move(arguments)), gtest ? fs::path(config.directory) : report, environment,
                                  std::chrono::seconds(test.timeout_seconds), {}, false, &output);
  output.close();
  constexpr size_t ExcerptBytes = 8192;
  const auto size = fs::file_size(output_path);
  std::ifstream recorded(output_path, std::ios::binary);
  std::string excerpt(static_cast<size_t>(std::min<uintmax_t>(size, ExcerptBytes * 2)), '\0');
  if (size <= ExcerptBytes * 2) {
    recorded.read(excerpt.data(), static_cast<std::streamsize>(excerpt.size()));
    std::cout << excerpt;
  } else {
    recorded.read(excerpt.data(), ExcerptBytes);
    recorded.seekg(-static_cast<std::streamoff>(ExcerptBytes), std::ios::end);
    recorded.read(excerpt.data() + ExcerptBytes, ExcerptBytes);
    std::cout << std::string_view(excerpt).substr(0, ExcerptBytes) << "\n... output abbreviated ...\n"
              << std::string_view(excerpt).substr(ExcerptBytes);
  }
  if (result.status != 0 || size > ExcerptBytes * 2) std::cout << "\nconformance: exit status " << result.status << '\n';
  if (result.status != 0 || !deqp) return result.status;
  std::ifstream qpa(report / "result.qpa", std::ios::binary);
  return deqp_result(std::string(std::istreambuf_iterator<char>(qpa), {}), test.identity);
}
int32_t run(RunOptions configuration, const fs::path &driver) {
  validate_run_options(configuration);
  std::vector<profile::ResolvedTarget> targets;
  std::string name = "direct";
  if (configuration.direct) targets = profile::direct_targets(configuration.selectors);
  else {
    const auto resolved = profile::resolve_profile(configuration.profiles, configuration.machine);
    name = resolved.name;
    targets = unique_routes(profile::resolve_targets(resolved, configuration.selectors), configuration.isolated_launcher);
  }
  // Validate the complete requested inventory before probing or scheduling any target.
  for (const auto &target : targets)
    (void)validate_artifact(artifact_directory(configuration, target), target);
  const auto workload = load_workload(configuration);
  const auto root = configuration.output / name;
  fs::create_directories(root);
  fs::path reports;
  for (uint64_t attempt = 0;; ++attempt) {
    reports = root / fmt::format("run-{}-{}", std::chrono::system_clock::now().time_since_epoch().count(), attempt);
    if (fs::create_directory(reports)) break;
  }
  const auto ctest_directory = reports / "ctest";
  fs::create_directories(ctest_directory / "targets");
  std::vector<TargetExecution> prepared;
  std::cout << "conformance execution: "
            << (configuration.isolated_launcher  ? "isolated launcher"
                : configuration.launcher.empty() ? "direct runtime"
                                                 : "launcher")
            << "; targets: " << (configuration.direct ? "explicit routes" : name) << '\n';
  for (const auto &target : targets) {
    auto value = prepare_target(configuration, workload, target, reports);
    if (!value) {
      std::cout << "conformance target " << target.id << " omitted: no tests match workload " << configuration.workload << '\n';
      continue;
    }
    std::cout << "conformance target " << value->id;
    if (!value->device.empty()) std::cout << " resolved to " << value->device;
    std::cout << " with " << value->tests.size() << " test(s)\n";
    prepared.push_back(std::move(*value));
  }
  if (prepared.empty()) throw std::runtime_error("conformance workload selected no tests: " + configuration.workload);
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
