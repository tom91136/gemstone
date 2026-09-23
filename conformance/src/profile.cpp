#include "profile.hpp"

#include <algorithm>
#include <array>
#include <cstddef>
#include <set>
#include <stdexcept>
#include <string_view>

#include <aspartame/all.hpp>
#include <glaze/toml.hpp>

#include "environment.hpp"
#include <fmt/format.h>
#include <fmt/ranges.h>

#if defined(_WIN32)
  #define WIN32_LEAN_AND_MEAN
  #include <windows.h>
#else
  #include <unistd.h>
#endif

namespace conformance::profile {
using namespace aspartame;
namespace {

constexpr std::array<std::string_view, 6> supported_backends{"cuda", "hip", "hsa", "opencl", "sycl", "openacc"};

std::string target_id(const ProfileTarget &target) { return target.name.value_or(target.backend); }

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
  if (!profile_name(value)) throw std::invalid_argument(fmt::format("invalid conformance machine profile name: {}", value));
  return value;
}

void validate_skips(std::string_view name, std::string_view scope, const std::vector<std::string> &skips) {
  std::set<std::string> unique;
  for (const auto &skip : skips) {
    if (skip.empty() || skip.find_first_of("\r\n") != std::string::npos)
      throw std::invalid_argument(fmt::format("machine profile {} contains an invalid {} test skip identity", name, scope));
    if (!unique.insert(skip).second)
      throw std::invalid_argument(fmt::format("machine profile {} repeats {} test skip identities", name, scope));
  }
}

void validate(std::string_view name, const Profile &profile) {
  if (profile.targets.empty()) throw std::invalid_argument(fmt::format("machine profile {} must declare at least one target", name));

  std::map<std::string, std::vector<const ProfileTarget *>> by_backend;
  std::set<std::string> ids;
  for (const auto &target : profile.targets) {
    if (!supported(target.backend))
      throw std::invalid_argument(fmt::format("machine profile {} has unknown backend: {}", name, target.backend));
    const auto id = target_id(target);
    if (!profile_name(id)) throw std::invalid_argument(fmt::format("machine profile {} has invalid target name: {}", name, id));
    if (!ids.insert(id).second) throw std::invalid_argument(fmt::format("machine profile {} repeats target name: {}", name, id));
    by_backend[target.backend].push_back(&target);

    if (target.backend == "cuda" || target.backend == "openacc") {
      if (!target.architecture || !architecture(*target.architecture, "sm_", false))
        throw std::invalid_argument(fmt::format("machine profile {} requires an sm_ architecture for {}", name, target.backend));
    } else if (target.backend == "hip" || target.backend == "hsa") {
      if (!target.architecture || !architecture(*target.architecture, "gfx", true))
        throw std::invalid_argument(fmt::format("machine profile {} requires a gfx architecture for {}", name, target.backend));
    } else if (target.architecture) {
      throw std::invalid_argument(fmt::format("machine profile {} cannot set an architecture for {}", name, target.backend));
    }
    for (const auto &[key, value] : target.environment) {
      if (!valid_environment_entry(key, value))
        throw std::invalid_argument(fmt::format("machine profile {} has a blank environment variable for {}", name, target.backend));
      if (reserved(key)) throw std::invalid_argument(fmt::format("machine profile environment must not define reserved variable: {}", key));
    }
    validate_skips(name, "target " + id, target.skips);
  }

  for (const auto &[backend, targets] : by_backend) {
    if (targets.size() > 1) {
      if (std::ranges::any_of(targets, [](const ProfileTarget *target) { return !target->name; }))
        throw std::invalid_argument(fmt::format("machine profile {} requires explicit names for repeated backend: {}", name, backend));
    }
  }
  for (const auto &[key, value] : profile.environment) {
    if (!valid_environment_entry(key, value))
      throw std::invalid_argument(fmt::format("machine profile {} has a blank environment variable", name));
    if (reserved(key)) throw std::invalid_argument(fmt::format("machine profile environment must not define reserved variable: {}", key));
  }
  validate_skips(name, "global", profile.skips);
}

Profile load(const std::filesystem::path &file) {
  std::string buffer;
  Profile profile;
  if (const auto error = glz::read_file_toml(profile, file.string(), buffer); error)
    throw std::invalid_argument(
        fmt::format("cannot load conformance machine profile {}:\n{}", file.string(), glz::format_error(error, buffer)));
  return profile;
}

std::string join(const std::vector<std::string> &values, std::string_view separator) {
  return fmt::format("{}", fmt::join(values, separator));
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

Profile resolve_profile(const std::filesystem::path &directory, const std::optional<std::string> &requested,
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
  profile.name = name;
  return profile;
}

std::vector<ResolvedTarget> resolve_targets(const Profile &profile, const std::vector<std::string> &selectors) {
  if (std::ranges::find(selectors, "all") != selectors.end() && !(selectors.size() == 1 && selectors.front() == "all"))
    throw std::invalid_argument("all cannot be combined with target names");

  std::set<std::string> known;
  for (const auto &target : profile.targets) {
    known.insert(target.backend);
    known.insert(target_id(target));
  }
  for (const auto &selector : selectors) {
    if (!known.contains(selector) && selector != "all") {
      const auto ids = profile.targets ^ map([](const ProfileTarget &target) { return target_id(target); });
      throw std::invalid_argument(fmt::format("machine profile {} provides targets: {}", profile.name, join(ids, ", ")));
    }
  }

  const auto all = selectors.empty() || selectors == std::vector<std::string>{"all"};
  return profile.targets | filter([&](const ProfileTarget &target) {
           if (all) return true;
           const auto id = target_id(target);
           return std::ranges::find(selectors, target.backend) != selectors.end() || std::ranges::find(selectors, id) != selectors.end();
         })
         | map([&](const ProfileTarget &target) {
             const auto id = target_id(target);
             ResolvedTarget planned{id, target.backend, target.architecture, profile.environment, target.deviceLock};
             for (const auto &[key, value] : target.environment)
               planned.environment[key] = value;
             planned.environment["CONFORMANCE_DEVICE"] = target.device.value_or("");
             planned.environment["CONFORMANCE_SKIPS"] = join(concat(profile.skips, target.skips) ^ distinct(), "\n");
             return planned;
           })
         | to_vector();
}

void validate_all(const std::filesystem::path &directory) {
  if (!std::filesystem::is_directory(directory))
    throw std::invalid_argument(fmt::format("conformance profile directory does not exist: {}", directory.string()));
  size_t count = 0;
  for (const auto &entry : std::filesystem::directory_iterator(directory)) {
    if (!entry.is_regular_file() || entry.path().extension() != ".toml") continue;
    const auto name = normalize_name(entry.path().filename().string());
    auto profile = load(entry.path());
    validate(name, profile);
    ++count;
  }
  if (count == 0) throw std::invalid_argument(fmt::format("conformance profile directory contains no .toml files: {}", directory.string()));
}

} // namespace conformance::profile
