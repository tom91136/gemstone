#include "profile.hpp"

#include <algorithm>
#include <array>
#include <cstddef>
#include <set>
#include <stdexcept>
#include <string_view>

#include <unistd.h>

#include <aspartame/all.hpp>
#include <glaze/toml.hpp>

#include "environment.hpp"
#include <fmt/format.h>
#include <fmt/ranges.h>

namespace conformance::profile {
using namespace aspartame;
namespace {

constexpr std::array<std::string_view, 8> SupportedBackends{"cuda", "hip", "hsa", "opencl", "sycl", "levelzero", "vulkan", "openacc"};

std::string targetId(const ProfileTarget &target) { return target.name.value_or(target.backend); }

bool supported(std::string_view backend) { return std::ranges::find(SupportedBackends, backend) != SupportedBackends.end(); }

bool profileName(std::string_view value) {
  const auto isAlphaNumeric = [](char character) {
    return (character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9');
  };
  if (value.empty() || !isAlphaNumeric(value.front())) return false;
  return std::ranges::all_of(value.substr(1), [&](char character) {
    return isAlphaNumeric(character) || character == '.' || character == '_' || character == '-';
  });
}

bool architecture(std::string_view value, std::string_view prefix, bool hexadecimal) {
  if (!value.starts_with(prefix) || value.size() == prefix.size()) return false;
  return std::ranges::all_of(value.substr(prefix.size()), [hexadecimal](char character) {
    return (character >= '0' && character <= '9') || (hexadecimal && character >= 'a' && character <= 'f');
  });
}

void validateRoute(std::string_view backend, const std::optional<std::string> &arch) {
  if (!supported(backend)) throw std::invalid_argument(fmt::format("unknown backend: {}", backend));
  if (backend == "cuda" || backend == "openacc") {
    if (!arch || !architecture(*arch, "sm_", false)) throw std::invalid_argument(fmt::format("{} requires an sm_ architecture", backend));
  } else if (backend == "hip" || backend == "hsa") {
    if (!arch || !architecture(*arch, "gfx", true)) throw std::invalid_argument(fmt::format("{} requires a gfx architecture", backend));
  } else if (arch) throw std::invalid_argument(fmt::format("{} cannot set an architecture", backend));
}

void validateCompiler(std::string_view backend, std::string_view compiler) {
  if (compiler == "vendor") return;
  if (compiler == "clang" && backend == "cuda") return;
  if (compiler == "gcc" && (backend == "cuda" || backend == "hsa")) return;
  throw std::invalid_argument(fmt::format("unsupported compiler {} for backend {}", compiler, backend));
}

// CONFORMANCE_CUDA_CODE is the single CUDA code-mode setting; the driver JIT policy follows from it.
void applyCudaCode(ResolvedTarget &target) {
  if (target.backend != "cuda") return;
  auto code = target.environment.find("CONFORMANCE_CUDA_CODE");
  if (target.compiler == "gcc") {
    if (code != target.environment.end() && code->second != "ptx") throw std::invalid_argument("GCC CUDA offload requires PTX execution");
    code = target.environment.insert_or_assign("CONFORMANCE_CUDA_CODE", "ptx").first;
  }
  if (code == target.environment.end()) return;
  if (code->second != "ptx" && code->second != "sass") throw std::invalid_argument("CONFORMANCE_CUDA_CODE must be ptx or sass");
  const bool ptx = code->second == "ptx";
  target.environment["CUDA_FORCE_PTX_JIT"] = ptx ? "1" : "0";
  target.environment["CUDA_DISABLE_PTX_JIT"] = ptx ? "0" : "1";
}

std::string normaliseName(std::string value) {
  constexpr std::string_view suffix = ".toml";
  if (value.ends_with(suffix)) value.resize(value.size() - suffix.size());
  if (!profileName(value)) throw std::invalid_argument(fmt::format("invalid conformance machine profile name: {}", value));
  return value;
}

void validateSkips(std::string_view name, std::string_view scope, const std::vector<std::string> &skips) {
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

  std::map<std::string, std::vector<const ProfileTarget *>> byBackend;
  std::set<std::string> ids;
  for (const auto &target : profile.targets) {
    const auto id = targetId(target);
    if (!profileName(id)) throw std::invalid_argument(fmt::format("machine profile {} has invalid target name: {}", name, id));
    if (!ids.insert(id).second) throw std::invalid_argument(fmt::format("machine profile {} repeats target name: {}", name, id));
    byBackend[target.backend].push_back(&target);

    validateRoute(target.backend, target.architecture);
    validateCompiler(target.backend, target.compiler);
    for (const auto &[key, value] : target.environment) {
      if (!validEnvironmentEntry(key, value))
        throw std::invalid_argument(fmt::format("machine profile {} has a blank environment variable for {}", name, target.backend));
      if (reservedEnvironmentKey(key))
        throw std::invalid_argument(fmt::format("machine profile environment must not define reserved variable: {}", key));
    }
    validateSkips(name, "target " + id, target.skips);
  }

  for (const auto &[backend, targets] : byBackend) {
    if (targets.size() > 1) {
      if (std::ranges::any_of(targets, [](const ProfileTarget *target) { return !target->name; }))
        throw std::invalid_argument(fmt::format("machine profile {} requires explicit names for repeated backend: {}", name, backend));
    }
  }
  for (const auto &[key, value] : profile.environment) {
    if (!validEnvironmentEntry(key, value))
      throw std::invalid_argument(fmt::format("machine profile {} has a blank environment variable", name));
    if (reservedEnvironmentKey(key))
      throw std::invalid_argument(fmt::format("machine profile environment must not define reserved variable: {}", key));
  }
  validateSkips(name, "global", profile.skips);
}

Profile load(const std::filesystem::path &file) {
  std::string buffer;
  Profile profile;
  if (const auto error = glz::read_file_toml(profile, file.string(), buffer); error)
    throw std::invalid_argument(
        fmt::format("cannot load conformance machine profile {}:\n{}", file.string(), glz::format_error(error, buffer)));
  return profile;
}

std::string localHostname() {
  if (const char *configured = std::getenv("HOSTNAME"); configured && *configured)
    return std::string(configured).substr(0, std::string(configured).find('.'));
  std::array<char, 256> result{};
  if (gethostname(result.data(), result.size()) != 0) throw std::runtime_error("cannot determine the local hostname");
  result.back() = '\0';
  const std::string hostname(result.data());
  return hostname.substr(0, hostname.find('.'));
}

} // namespace

Profile resolveProfile(const std::filesystem::path &directory, const std::optional<std::string> &requested,
                       const std::optional<std::string> &hostname) {
  const auto name = normaliseName(requested ? *requested : hostname.value_or(localHostname()));
  const auto file = std::filesystem::absolute(directory / (name + ".toml")).lexically_normal();
  if (!std::filesystem::is_regular_file(file)) {
    const auto source = requested ? "requested" : "local-machine";
    throw std::invalid_argument(std::string(source) + " conformance machine profile does not exist: " + file.string());
  }
  auto profile = load(file);
  validate(name, profile);
  profile.name = name;
  return profile;
}

std::vector<ResolvedTarget> resolveTargets(const Profile &profile, const std::vector<std::string> &selectors) {
  const auto matches = [](const ProfileTarget &target, const std::string &selector) {
    return selector == target.backend || selector == targetId(target);
  };
  for (const auto &selector : selectors)
    if (std::ranges::none_of(profile.targets, [&](const ProfileTarget &target) { return matches(target, selector); }))
      throw std::invalid_argument(
          fmt::format("machine profile {} provides targets: {}", profile.name,
                      fmt::join(profile.targets ^ map([](const ProfileTarget &target) { return targetId(target); }), ", ")));
  const auto selected = [&](const ProfileTarget &target) {
    return selectors.empty() || std::ranges::any_of(selectors, [&](const std::string &selector) { return matches(target, selector); });
  };
  return profile.targets | filter(selected) | map([&](const ProfileTarget &target) {
           ResolvedTarget resolved{
               targetId(target), target.backend, target.architecture, profile.environment, concat(profile.skips, target.skips) ^ distinct(),
               target.compiler};
           for (const auto &[key, value] : target.environment)
             resolved.environment[key] = value;
           applyCudaCode(resolved);
           resolved.environment["CONFORMANCE_DEVICE"] = target.device.value_or("");
           return resolved;
         })
         | to_vector();
}

std::vector<ResolvedTarget> directTargets(const std::vector<std::string> &routes) {
  if (routes.empty()) throw std::invalid_argument("--direct requires explicit backend[/architecture[/compiler]] targets");
  std::vector<ResolvedTarget> result;
  std::set<std::string> seen;
  const auto append = [&](ResolvedTarget target) {
    if (!seen.insert(target.id).second) throw std::invalid_argument("duplicate direct target: " + target.id);
    applyCudaCode(target);
    result.push_back(std::move(target));
  };
  for (const auto &route : routes) {
    const auto slash = route.find('/');
    const auto selector = route.substr(0, slash);
    const auto backend = selector == "cuda-ptx" || selector == "cuda-sass" ? "cuda" : selector;
    const auto compilerSlash = slash == std::string::npos ? std::string::npos : route.find('/', slash + 1);
    const auto arch = slash == std::string::npos ? std::nullopt : std::optional(route.substr(slash + 1, compilerSlash - slash - 1));
    const auto compiler = compilerSlash == std::string::npos ? "vendor" : route.substr(compilerSlash + 1);
    validateRoute(backend, arch);
    validateCompiler(backend, compiler);
    const auto suffix = compiler == "vendor" ? "" : "-" + compiler;
    if (compiler == "gcc" && selector == "cuda-sass") throw std::invalid_argument("GCC CUDA offload requires PTX execution");
    if (backend == "cuda") {
      for (const auto *mode : {"ptx", "sass"}) {
        const auto name = std::string("cuda-") + mode;
        if (selector != "cuda" && selector != name) continue;
        if (compiler == "gcc" && std::string_view(mode) != "ptx") continue;
        append({name + "-" + *arch + suffix, backend, arch, {{"CONFORMANCE_CUDA_CODE", mode}}, {}, compiler});
      }
    } else append({backend + (arch ? "-" + *arch : "") + suffix, backend, arch, {}, {}, compiler});
  }
  return result;
}

void validateAll(const std::filesystem::path &directory) {
  if (!std::filesystem::is_directory(directory))
    throw std::invalid_argument(fmt::format("conformance profile directory does not exist: {}", directory.string()));
  size_t count = 0;
  for (const auto &entry : std::filesystem::directory_iterator(directory)) {
    if (!entry.is_regular_file() || entry.path().extension() != ".toml") continue;
    const auto name = normaliseName(entry.path().filename().string());
    auto profile = load(entry.path());
    validate(name, profile);
    ++count;
  }
  if (count == 0) throw std::invalid_argument(fmt::format("conformance profile directory contains no .toml files: {}", directory.string()));
}

} // namespace conformance::profile
