#pragma once

#include <filesystem>
#include <map>
#include <optional>
#include <string>
#include <vector>

namespace conformance::profile {

struct Target {
  std::optional<std::string> name;
  std::string backend;
  std::optional<std::string> architecture;
  std::optional<std::string> device;
  std::map<std::string, std::string> environment;
  std::vector<std::string> skips;
  bool deviceLock = true;
};

struct Profile {
  std::vector<Target> targets;
  std::map<std::string, std::string> environment;
  std::vector<std::string> skips;
};

struct Resolved {
  std::string name;
  Profile profile;
};

struct PlanTarget {
  std::string id;
  std::string backend;
  std::optional<std::string> architecture;
  std::map<std::string, std::string> environment;
  bool deviceLock = true;
};

std::string local_hostname();
Resolved resolve(const std::filesystem::path &directory, const std::optional<std::string> &requested = std::nullopt,
                 const std::optional<std::string> &hostname = std::nullopt);
std::vector<PlanTarget> make_plan(const Resolved &resolved, const std::vector<std::string> &selectors);
void validate_all(const std::filesystem::path &directory);
} // namespace conformance::profile
