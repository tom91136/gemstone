#pragma once

#include <filesystem>
#include <map>
#include <optional>
#include <string>
#include <vector>

namespace conformance::profile {

struct ProfileTarget {
  std::optional<std::string> name;
  std::string backend;
  std::optional<std::string> architecture;
  std::optional<std::string> device;
  std::map<std::string, std::string> environment;
  std::vector<std::string> skips;
  std::string compiler = "vendor";
};

struct Profile {
  std::string name;
  std::vector<ProfileTarget> targets;
  std::map<std::string, std::string> environment;
  // Execution-time skips, reported as SKIP. Disabled under --isolated-launcher
  // because the launcher replaces this machine's physical provider.
  std::vector<std::string> skips;
};

struct ResolvedTarget {
  std::string id;
  std::string backend;
  std::optional<std::string> architecture;
  std::map<std::string, std::string> environment;
  std::vector<std::string> skips;
  std::string compiler = "vendor";
};

Profile resolveProfile(const std::filesystem::path &directory, const std::optional<std::string> &requested = std::nullopt,
                       const std::optional<std::string> &hostname = std::nullopt);
std::vector<ResolvedTarget> resolveTargets(const Profile &profile, const std::vector<std::string> &selectors);
std::vector<ResolvedTarget> directTargets(const std::vector<std::string> &routes);
void validateAll(const std::filesystem::path &directory);
} // namespace conformance::profile
