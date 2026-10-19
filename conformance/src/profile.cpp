#include "profile.hpp"

#include <array>
#include <cstdlib>
#include <filesystem>
#include <map>
#include <optional>
#include <set>
#include <stdexcept>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <unistd.h>

#include "cuda_architecture.hpp"
#include <aspartame/all.hpp>
#include <glaze/toml.hpp>

#include "environment.hpp"
#include <fmt/format.h>

namespace conformance::profile {
using namespace aspartame;
namespace {

constexpr std::array<std::string_view, 8> SupportedBackends{"cuda", "hip", "hsa", "opencl", "sycl", "levelzero", "vulkan", "openacc"};

std::string targetId(const ProfileTarget &target) { return target.name.value_or(target.backend); }

bool supported(std::string_view backend) { return SupportedBackends ^ contains(backend); }

bool profileName(std::string_view value) {
  const auto isAlphaNumeric = [](char character) {
    return (character >= 'A' && character <= 'Z') || (character >= 'a' && character <= 'z') || (character >= '0' && character <= '9');
  };
  if (value.empty() || !isAlphaNumeric(value.front())) return false;
  return value.substr(1)
         ^ forall([&](char character) { return isAlphaNumeric(character) || character == '.' || character == '_' || character == '-'; });
}

bool gfxArchitecture(std::string_view value) {
  if (!value.starts_with("gfx") || value.size() == 3) return false;
  return value.substr(3)
         ^ forall([](char character) { return (character >= '0' && character <= '9') || (character >= 'a' && character <= 'f'); });
}

void validateRoute(std::string_view backend, const std::optional<std::string> &arch) {
  if (!supported(backend)) throw std::invalid_argument(fmt::format("unknown backend: {}", backend));
  if (backend == "cuda" || backend == "openacc") {
    if (!arch || !arch->starts_with("sm_") || !CudaArchitecture::parse(*arch))
      throw std::invalid_argument(fmt::format("{} requires an sm_ architecture", backend));
  } else if (backend == "hip" || backend == "hsa") {
    if (!arch || !gfxArchitecture(*arch)) throw std::invalid_argument(fmt::format("{} requires a gfx architecture", backend));
  } else if (arch) {
    throw std::invalid_argument(fmt::format("{} cannot set an architecture", backend));
  }
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
  const auto code = [&] { return target.environment ^ get_maybe(std::string("CONFORMANCE_CUDA_CODE")); };
  if (target.compiler == "gcc") {
    if (code() ^ exists([](const std::string &mode) { return mode != "ptx"; }))
      throw std::invalid_argument("GCC CUDA offload requires PTX execution");
    target.environment["CONFORMANCE_CUDA_CODE"] = "ptx";
  }
  code() ^ for_each([&](const std::string &mode) {
    if (mode != "ptx" && mode != "sass") throw std::invalid_argument("CONFORMANCE_CUDA_CODE must be ptx or sass");
    const bool ptx = mode == "ptx";
    target.environment["CUDA_FORCE_PTX_JIT"] = ptx ? "1" : "0";
    target.environment["CUDA_DISABLE_PTX_JIT"] = ptx ? "0" : "1";
  });
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

  if (const auto repeated = byBackend ^ find([](const std::string &, const std::vector<const ProfileTarget *> &targets) {
                              return targets.size() > 1 && targets ^ exists([](const ProfileTarget *target) { return !target->name; });
                            }))
    throw std::invalid_argument(fmt::format("machine profile {} requires explicit names for repeated backend: {}", name, repeated->first));
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
  std::string hostname;
  if (const char *configured = std::getenv("HOSTNAME"); configured && *configured) {
    hostname = configured;
  } else {
    std::array<char, 256> result{};
    if (gethostname(result.data(), result.size()) != 0) throw std::runtime_error("cannot determine the local hostname");
    result.back() = '\0';
    hostname = result.data();
  }
  return hostname.substr(0, hostname.find('.'));
}

} // namespace

Profile resolveProfile(const std::filesystem::path &directory, const std::optional<std::string> &requested,
                       const std::optional<std::string> &hostname) {
  const auto name = normaliseName(requested ? *requested : hostname.value_or(localHostname()));
  const auto file = std::filesystem::absolute(directory / (name + ".toml")).lexically_normal();
  if (!std::filesystem::is_regular_file(file)) {
    const auto *const source = requested ? "requested" : "local-machine";
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
  if (selectors ^ exists([&](const std::string &selector) {
        return profile.targets ^ none_match([&](const ProfileTarget &target) { return matches(target, selector); });
      }))
    throw std::invalid_argument(
        fmt::format("machine profile {} provides targets: {}", profile.name,
                    profile.targets ^ map([](const ProfileTarget &target) { return targetId(target); }) ^ mk_string(", ")));
  const auto selected = [&](const ProfileTarget &target) {
    return selectors.empty() || selectors ^ exists([&](const std::string &selector) { return matches(target, selector); });
  };
  return profile.targets | filter(selected) | map([&](const ProfileTarget &target) {
           ResolvedTarget resolved{.id = targetId(target),
                                   .backend = target.backend,
                                   .architecture = target.architecture,
                                   .environment = profile.environment,
                                   .skips = concat(profile.skips, target.skips) ^ distinct(),
                                   .compiler = target.compiler};
           target.environment ^ for_each([&](const std::string &key, const std::string &value) { resolved.environment[key] = value; });
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
    if (backend == "cuda" && arch) {
      for (const auto *mode : {"ptx", "sass"}) {
        const auto name = std::string("cuda-") + mode;
        if (selector != "cuda" && selector != name) continue;
        if (compiler == "gcc" && std::string_view(mode) != "ptx") continue;
        append({.id = fmt::format("{}-{}{}", name, *arch, suffix),
                .backend = backend,
                .architecture = arch,
                .environment = {{"CONFORMANCE_CUDA_CODE", mode}},
                .skips = {},
                .compiler = compiler});
      }
    } else {
      append({.id = arch ? fmt::format("{}-{}{}", backend, *arch, suffix) : backend + suffix,
              .backend = backend,
              .architecture = arch,
              .environment = {},
              .skips = {},
              .compiler = compiler});
    }
  }
  return result;
}

void validateAll(const std::filesystem::path &directory) {
  if (!std::filesystem::is_directory(directory))
    throw std::invalid_argument(fmt::format("conformance profile directory does not exist: {}", directory.string()));
  const std::vector<std::filesystem::directory_entry> entries{std::filesystem::directory_iterator(directory),
                                                              std::filesystem::directory_iterator{}};
  const auto profiles = entries ^ filter([](const std::filesystem::directory_entry &entry) {
                          return entry.is_regular_file() && entry.path().extension() == ".toml";
                        });
  profiles ^ for_each([](const std::filesystem::directory_entry &entry) {
    const auto name = normaliseName(entry.path().filename().string());
    validate(name, load(entry.path()));
  });
  if (profiles.empty())
    throw std::invalid_argument(fmt::format("conformance profile directory contains no .toml files: {}", directory.string()));
}

} // namespace conformance::profile
