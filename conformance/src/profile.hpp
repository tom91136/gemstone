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
  bool deviceLock = true;
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
  bool deviceLock = true;
};

std::string local_hostname();
Profile resolve_profile(const std::filesystem::path &directory, const std::optional<std::string> &requested = std::nullopt,
                        const std::optional<std::string> &hostname = std::nullopt);
std::vector<ResolvedTarget> resolve_targets(const Profile &profile, const std::vector<std::string> &selectors);
void validate_all(const std::filesystem::path &directory);
} // namespace conformance::profile
