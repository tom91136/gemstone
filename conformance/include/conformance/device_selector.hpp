#pragma once

#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <stdexcept>
#include <string>
#include <vector>

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
  std::string diagnostic = matches.empty() ? "matched no devices; discovered: " : "is ambiguous; matched: ";
  if (matches.empty()) {
    for (size_t index = 0; index < descriptions.size(); ++index) {
      if (index) diagnostic += ", ";
      diagnostic += descriptions[index];
    }
  } else {
    for (size_t offset = 0; offset < matches.size(); ++offset) {
      if (offset) diagnostic += ", ";
      diagnostic += descriptions[matches[offset]];
    }
  }
  throw std::runtime_error("device selector '" + std::string(configured) + "' " + diagnostic);
}

inline std::string pci_description(uint32_t domain, uint32_t bus, uint32_t device, uint32_t function) {
  static constexpr char hex[] = "0123456789abcdef";
  const auto append = [&](std::string &result, uint32_t value, size_t digits) {
    while (digits != 0) {
      --digits;
      result += hex[(value >> (digits * 4)) & 0xf];
    }
  };
  std::string result;
  append(result, domain, 4);
  result += ':';
  append(result, bus, 2);
  result += ':';
  append(result, device, 2);
  result += '.';
  append(result, function, 1);
  return result;
}

} // namespace conformance
