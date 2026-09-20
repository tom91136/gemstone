#include "conformance/profile.hpp"

#include <algorithm>
#include <array>
#include <cstddef>
#include <set>
#include <sstream>
#include <stdexcept>
#include <string_view>

#include <glaze/toml.hpp>

#include "conformance/environment.hpp"

#if defined(_WIN32)
  #define WIN32_LEAN_AND_MEAN
  #include <windows.h>
#else
  #include <unistd.h>
#endif

namespace conformance::profile {
namespace {

constexpr std::array<std::string_view, 6> supported_backends{"cuda", "hip", "hsa", "opencl", "sycl", "openacc"};

std::string target_id(const Target &target) { return target.name.value_or(target.backend); }

bool supported(std::string_view backend) { return std::ranges::find(supported_backends, backend) != supported_backends.end(); }

bool reserved(std::string_view key) { return reserved_environment_key(key); }

bool profile_name(std::string_view value) {
  const auto is_alpha_numeric = [](char character) {
    return (character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9');
  };
  if (value.empty() || !is_alpha_numeric(value.front())) return false;
  return std::ranges::all_of(value.substr(1), [&](char character) {
    return is_alpha_numeric(character) || character == '.' || character == '_' || character == '-';
  });
}

bool architecture(std::string_view value, std::string_view prefix, bool hexadecimal) {
  if (!value.starts_with(prefix) || value.size() == prefix.size()) return false;
  return std::ranges::all_of(value.substr(prefix.size()), [hexadecimal](char character) {
    return (character >= '0' && character <= '9') || (hexadecimal && character >= 'a' && character <= 'f');
  });
}

std::string normalize_name(std::string value) {
  constexpr std::string_view suffix = ".toml";
  if (value.ends_with(suffix)) value.resize(value.size() - suffix.size());
  if (!profile_name(value)) throw std::invalid_argument("invalid conformance machine profile name: " + value);
  return value;
}

void validate_skips(std::string_view name, std::string_view scope, const std::vector<std::string> &skips) {
  std::set<std::string> unique;
  for (const auto &skip : skips) {
    if (skip.empty() || skip.find_first_of("\r\n") != std::string::npos)
      throw std::invalid_argument("machine profile " + std::string(name) + " contains an invalid " + std::string(scope)
                                  + " test skip identity");
    if (!unique.insert(skip).second)
      throw std::invalid_argument("machine profile " + std::string(name) + " repeats " + std::string(scope) + " test skip identities");
  }
}

void validate(std::string_view name, const Profile &profile) {
  if (profile.targets.empty()) throw std::invalid_argument("machine profile " + std::string(name) + " must declare at least one target");

  std::map<std::string, std::vector<const Target *>> by_backend;
  std::set<std::string> ids;
  for (const auto &target : profile.targets) {
    if (!supported(target.backend))
      throw std::invalid_argument("machine profile " + std::string(name) + " has unknown backend: " + target.backend);
    const auto id = target_id(target);
    if (!profile_name(id)) throw std::invalid_argument("machine profile " + std::string(name) + " has invalid target name: " + id);
    if (!ids.insert(id).second) throw std::invalid_argument("machine profile " + std::string(name) + " repeats target name: " + id);
    by_backend[target.backend].push_back(&target);

    if (target.backend == "cuda" || target.backend == "openacc") {
      if (!target.architecture || !architecture(*target.architecture, "sm_", false))
        throw std::invalid_argument("machine profile " + std::string(name) + " requires an sm_ architecture for " + target.backend);
    } else if (target.backend == "hip" || target.backend == "hsa") {
      if (!target.architecture || !architecture(*target.architecture, "gfx", true))
        throw std::invalid_argument("machine profile " + std::string(name) + " requires a gfx architecture for " + target.backend);
    } else if (target.architecture) {
      throw std::invalid_argument("machine profile " + std::string(name) + " cannot set an architecture for " + target.backend);
    }
    for (const auto &[key, value] : target.environment) {
      if (!valid_environment_entry(key, value))
        throw std::invalid_argument("machine profile " + std::string(name) + " has a blank environment variable for " + target.backend);
      if (reserved(key)) throw std::invalid_argument("machine profile environment must not define reserved variable: " + key);
    }
    validate_skips(name, "target " + id, target.skips);
  }

  for (const auto &[backend, targets] : by_backend) {
    if (targets.size() > 1) {
      if (std::ranges::any_of(targets, [](const Target *target) { return !target->name; }))
        throw std::invalid_argument("machine profile " + std::string(name) + " requires explicit names for repeated backend: " + backend);
    }
  }
  for (const auto &[key, value] : profile.environment) {
    if (!valid_environment_entry(key, value))
      throw std::invalid_argument("machine profile " + std::string(name) + " has a blank environment variable");
    if (reserved(key)) throw std::invalid_argument("machine profile environment must not define reserved variable: " + key);
  }
  validate_skips(name, "global", profile.skips);
}

Profile load(const std::filesystem::path &file) {
  std::string buffer;
  Profile profile;
  if (const auto error = glz::read_file_toml(profile, file.string(), buffer); error)
    throw std::invalid_argument("cannot load conformance machine profile " + file.string() + ":\n" + glz::format_error(error, buffer));
  return profile;
}

std::string join(const std::vector<std::string> &values, std::string_view separator) {
  std::ostringstream result;
  for (size_t index = 0; index < values.size(); ++index) {
    if (index) result << separator;
    result << values[index];
  }
  return result.str();
}

} // namespace

std::string local_hostname() {
  if (const char *configured = std::getenv("HOSTNAME"); configured && *configured)
    return std::string(configured).substr(0, std::string(configured).find('.'));
#if defined(_WIN32)
  DWORD size = 0;
  GetComputerNameExA(ComputerNameDnsHostname, nullptr, &size);
  std::string result(size, '\0');
  if (!GetComputerNameExA(ComputerNameDnsHostname, result.data(), &size)) throw std::runtime_error("cannot determine the local hostname");
  result.resize(size);
  return result.substr(0, result.find('.'));
#else
  std::array<char, 256> result{};
  if (gethostname(result.data(), result.size()) != 0) throw std::runtime_error("cannot determine the local hostname");
  result.back() = '\0';
  const std::string hostname_value(result.data());
  return hostname_value.substr(0, hostname_value.find('.'));
#endif
}

Resolved resolve(const std::filesystem::path &directory, const std::optional<std::string> &requested,
                 const std::optional<std::string> &hostname) {
  std::optional<std::string> explicit_name = requested;
  std::string name;
  if (explicit_name) {
    name = normalize_name(*explicit_name);
  } else {
    name = normalize_name(hostname.value_or(local_hostname()));
  }
  const auto file = std::filesystem::absolute(directory / (name + ".toml")).lexically_normal();
  if (!std::filesystem::is_regular_file(file)) {
    const auto source = explicit_name ? "requested" : "local-machine";
    throw std::invalid_argument(std::string(source) + " conformance machine profile does not exist: " + file.string());
  }
  auto profile = load(file);
  validate(name, profile);
  return {name, std::move(profile)};
}

std::vector<PlanTarget> make_plan(const Resolved &resolved, const std::vector<std::string> &selectors) {
  if (std::ranges::find(selectors, "all") != selectors.end() && !(selectors.size() == 1 && selectors.front() == "all"))
    throw std::invalid_argument("all cannot be combined with target names");

  std::set<std::string> known;
  for (const auto &target : resolved.profile.targets) {
    known.insert(target.backend);
    known.insert(target_id(target));
  }
  for (const auto &selector : selectors) {
    if (!known.contains(selector) && selector != "all") {
      std::vector<std::string> ids;
      for (const auto &target : resolved.profile.targets)
        ids.push_back(target_id(target));
      throw std::invalid_argument("machine profile " + resolved.name + " provides targets: " + join(ids, ", "));
    }
  }

  std::vector<PlanTarget> result;
  for (const auto &target : resolved.profile.targets) {
    const auto id = target_id(target);
    if (!selectors.empty() && selectors != std::vector<std::string>{"all"}
        && std::ranges::find(selectors, target.backend) == selectors.end() && std::ranges::find(selectors, id) == selectors.end())
      continue;
    PlanTarget planned{id, target.backend, target.architecture, resolved.profile.environment, target.deviceLock};
    for (const auto &[key, value] : target.environment)
      planned.environment[key] = value;
    std::vector<std::string> skips = resolved.profile.skips;
    for (const auto &skip : target.skips)
      if (std::ranges::find(skips, skip) == skips.end()) skips.push_back(skip);
    planned.environment["CONFORMANCE_DEVICE"] = target.device.value_or("");
    planned.environment["CONFORMANCE_SKIPS"] = join(skips, "\n");
    result.push_back(std::move(planned));
  }
  return result;
}

void validate_all(const std::filesystem::path &directory) {
  if (!std::filesystem::is_directory(directory))
    throw std::invalid_argument("conformance profile directory does not exist: " + directory.string());
  size_t count = 0;
  for (const auto &entry : std::filesystem::directory_iterator(directory)) {
    if (!entry.is_regular_file() || entry.path().extension() != ".toml") continue;
    const auto name = normalize_name(entry.path().filename().string());
    auto profile = load(entry.path());
    validate(name, profile);
    ++count;
  }
  if (count == 0) throw std::invalid_argument("conformance profile directory contains no .toml files: " + directory.string());
}

} // namespace conformance::profile
