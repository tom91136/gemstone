#pragma once

#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <stdexcept>
#include <string>
#include <vector>

#include <fmt/format.h>
#include <fmt/ranges.h>

namespace conformance {

inline bool glob_matches(const char *pattern, const char *value) {
  const char *star = nullptr;
  const char *retry = nullptr;
  while (*value) {
    if (*pattern == '?' || *pattern == *value) {
      ++pattern;
      ++value;
    } else if (*pattern == '*') {
      star = pattern++;
      retry = value;
    } else if (star) {
      pattern = star + 1;
      value = ++retry;
    } else {
      return false;
    }
  }
  while (*pattern == '*')
    ++pattern;
  return *pattern == '\0';
}

inline size_t select_device(const std::vector<std::string> &descriptions) {
  if (descriptions.empty()) throw std::runtime_error("no GPU devices were discovered");
  const char *configured = std::getenv("CONFORMANCE_DEVICE");
  if (!configured || !*configured) return 0;
  std::vector<size_t> matches;
  for (size_t index = 0; index < descriptions.size(); ++index)
    if (glob_matches(configured, descriptions[index].c_str())) matches.push_back(index);
  if (matches.size() == 1) return matches.front();
  if (matches.empty())
    throw std::runtime_error(
        fmt::format("device selector '{}' matched no devices; discovered: {}", configured, fmt::join(descriptions, ", ")));
  std::vector<std::string> matched;
  matched.reserve(matches.size());
  for (const auto index : matches)
    matched.push_back(descriptions[index]);
  throw std::runtime_error(fmt::format("device selector '{}' is ambiguous; matched: {}", configured, fmt::join(matched, ", ")));
}

inline std::string pci_description(uint32_t domain, uint32_t bus, uint32_t device, uint32_t function) {
  return fmt::format("{:04x}:{:02x}:{:02x}.{:x}", domain, bus, device, function);
}

} // namespace conformance
