#pragma once

#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

#include <aspartame/all.hpp>

#include <conformance/environment_keys.hpp>

#include <fmt/format.h>

namespace conformance {

inline bool globMatches(std::string_view pattern, std::string_view value) {
  using namespace aspartame;
  return value ^ glob_matches(pattern);
}

inline size_t selectDevice(const std::vector<std::string> &descriptions) {
  using namespace aspartame;
  if (descriptions.empty()) throw std::runtime_error("no GPU devices were discovered");
  const char *configured = std::getenv(environment::DevicePattern);
  if (!configured || !*configured) return 0;
  const auto matches = descriptions ^ zip_with_index()
                       ^ filter([&](const std::string &description, size_t) { return globMatches(configured, description); });
  if (matches.size() == 1) return matches.front().second;
  if (matches.empty())
    throw std::runtime_error(
        fmt::format("device selector '{}' matched no devices; discovered: {}", configured, descriptions ^ mk_string(", ")));
  throw std::runtime_error(
      fmt::format("device selector '{}' is ambiguous; matched: {}", configured,
                  matches ^ map([](const std::string &description, size_t) { return description; }) ^ mk_string(", ")));
}

inline std::string pciDescription(uint32_t domain, uint32_t bus, uint32_t device, uint32_t function) {
  return fmt::format("{:04x}:{:02x}:{:02x}.{:x}", domain, bus, device, function);
}

} // namespace conformance
