#pragma once

#include <charconv>
#include <cstdlib>
#include <limits>
#include <optional>
#include <string>
#include <string_view>

#include <environment_keys.hpp>

namespace gemstone::environment {

inline const char *value(const char *name) { return std::getenv(name); }
inline bool present(const char *name) { return value(name) != nullptr; }

inline std::optional<bool> boolean(std::string_view raw) {
  std::string lower(raw);
  for (char &c : lower)
    if (c >= 'A' && c <= 'Z') c = static_cast<char>(c + ('a' - 'A'));
  if (lower == "1" || lower == "true" || lower == "on") return true;
  if (lower == "0" || lower == "false" || lower == "off") return false;
  return std::nullopt;
}

inline bool enabled(const char *name, bool fallback = false) {
  const auto *raw = value(name);
  return raw ? boolean(raw).value_or(fallback) : fallback;
}

inline std::optional<uint64_t> positiveInteger(std::string_view raw) {
  if (raw.empty()) return std::nullopt;
  uint64_t parsed = 0;
  const auto result = std::from_chars(raw.data(), raw.data() + raw.size(), parsed, 10);
  if (result.ec != std::errc{} || result.ptr != raw.data() + raw.size() || parsed == 0) return std::nullopt;
  return parsed;
}

/** Positive decimal only, including an explicit bound for the consumer's representation. */
inline uint64_t unsignedValue(const char *name, uint64_t fallback, uint64_t maximum = std::numeric_limits<uint64_t>::max()) {
  const auto *raw = value(name);
  const auto parsed = raw ? positiveInteger(raw) : std::nullopt;
  return parsed && *parsed <= maximum ? *parsed : fallback;
}

inline int rpcTimeoutSeconds() {
  return static_cast<int>(unsignedValue(RpcTimeoutSeconds, RpcTimeoutSecondsDefault, std::numeric_limits<int>::max() / 1000));
}

inline int hsaPagingTimeoutSeconds() {
  return static_cast<int>(unsignedValue(HsaPagingTimeoutSeconds, rpcTimeoutSeconds(), std::numeric_limits<int>::max() / 1000));
}

} // namespace gemstone::environment
