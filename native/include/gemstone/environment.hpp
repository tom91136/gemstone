#pragma once

#include <array>
#include <charconv>
#include <cstdio>
#include <cstdlib>
#include <limits>
#include <mutex>
#include <optional>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <aspartame/array.hpp>
#include <aspartame/vector.hpp>

#include <environment_keys.hpp>

namespace gemstone::environment {

inline const char *value(const char *name) {
  const char *raw = std::getenv(name);
  return raw && raw[0] != '\0' ? raw : nullptr;
}
inline bool present(const char *name) { return value(name) != nullptr; }

inline std::optional<bool> boolean(std::string_view raw) {
  using namespace aspartame;
  constexpr std::array<std::string_view, 5> truthy = {"1", "true", "on", "y", "yes"};
  constexpr std::array<std::string_view, 5> falsy = {"0", "false", "off", "n", "no"};
  std::string lower(raw);
  for (char &c : lower)
    if (c >= 'A' && c <= 'Z') c = static_cast<char>(c + ('a' - 'A'));
  if (truthy ^ contains(std::string_view(lower))) return true;
  if (falsy ^ contains(std::string_view(lower))) return false;
  return std::nullopt;
}

/** Whether any comma-separated, blank-trimmed token of `name` satisfies `matches`. */
template <class Matches> bool listed(const char *name, Matches matches) {
  const char *raw = value(name);
  if (!raw) return false;
  for (std::string_view remaining(raw);;) {
    const auto comma = remaining.find(',');
    auto token = remaining.substr(0, comma);
    const auto first = token.find_first_not_of(" \t");
    token = first == std::string_view::npos ? std::string_view{} : token.substr(first, token.find_last_not_of(" \t") - first + 1);
    if (matches(token)) return true;
    if (comma == std::string_view::npos) return false;
    remaining.remove_prefix(comma + 1);
  }
}

inline bool enabled(const char *name) {
  const auto *raw = value(name);
  return raw && boolean(raw).value_or(false);
}

inline std::optional<uint64_t> positiveInteger(std::string_view raw) {
  if (raw.empty()) return std::nullopt;
  uint64_t parsed = 0;
  const auto result = std::from_chars(raw.data(), raw.data() + raw.size(), parsed, 10);
  if (result.ec != std::errc{} || result.ptr != raw.data() + raw.size() || parsed == 0) return std::nullopt;
  return parsed;
}

inline constexpr std::string_view PositiveThousandthsExpected = "a positive number with at most three decimals";

/** A positive decimal with at most three fractional digits, in thousandths so that CMake can share it exactly. */
inline std::optional<uint64_t> positiveThousandths(std::string_view raw) {
  const auto dot = raw.find('.');
  const auto whole = raw.substr(0, dot);
  const auto fraction = dot == std::string_view::npos ? std::string_view{} : raw.substr(dot + 1);
  if (whole.empty() || whole.size() > 9 || (dot != std::string_view::npos && (fraction.empty() || fraction.size() > 3))) return std::nullopt;
  std::string digits(whole);
  digits.append(fraction).append(3 - fraction.size(), '0');
  return positiveInteger(digits);
}

/** Positive decimal only, including an explicit bound for the consumer's representation. */
inline uint64_t unsignedValue(const char *name, uint64_t fallback, uint64_t maximum = std::numeric_limits<uint64_t>::max()) {
  const auto *raw = value(name);
  const auto parsed = raw ? positiveInteger(raw) : std::nullopt;
  return parsed && *parsed <= maximum ? *parsed : fallback;
}

// Timeouts become signed 32-bit millisecond counts.
inline constexpr uint64_t TimeoutSecondsMaximum = std::numeric_limits<int>::max() / 1000;

inline int rpcTimeoutSeconds() {
  return static_cast<int>(unsignedValue(RpcTimeoutSeconds, RpcTimeoutSecondsDefault, TimeoutSecondsMaximum));
}

inline int hsaPagingTimeoutSeconds() {
  return static_cast<int>(unsignedValue(HsaPagingTimeoutSeconds, rpcTimeoutSeconds(), TimeoutSecondsMaximum));
}

inline std::string rejected(const char *name, const char *raw, std::string_view expected) {
  return std::string(name) + " must be " + std::string(expected) + ", not '" + raw + "'";
}

inline std::optional<std::string> unlisted(const char *name, const std::vector<std::string_view> &choices) {
  using namespace aspartame;
  const char *raw = value(name);
  if (!raw || choices ^ contains(std::string_view(raw))) return std::nullopt;
  return rejected(name, raw, "one of " + (choices ^ mk_string(" ")));
}

/** The first malformed setting shared by every shim; each shim adds the settings only it reads. */
inline std::optional<std::string> invalid() {
  for (const char *name : {OpenclScanBufferPointersOn, SystemPagingOn, VulkanValidateAllocatorsOn})
    if (const char *raw = value(name); raw && !boolean(raw)) return rejected(name, raw, "one of 1 true on y yes 0 false off n no");
  const std::pair<const char *, uint64_t> integers[] = {{RpcClientId, std::numeric_limits<int64_t>::max()},
                                                        {RpcTimeoutSeconds, TimeoutSecondsMaximum},
                                                        {HsaPagingTimeoutSeconds, TimeoutSecondsMaximum},
                                                        {ThreadCount, std::numeric_limits<uint32_t>::max()}};
  for (const auto &[name, maximum] : integers)
    if (const char *raw = value(name)) {
      const auto parsed = positiveInteger(raw);
      if (!parsed || *parsed > maximum) return rejected(name, raw, "a positive integer up to " + std::to_string(maximum));
    }
  if (const char *raw = value(TimeoutScale); raw && !positiveThousandths(raw))
    return rejected(TimeoutScale, raw, PositiveThousandthsExpected);
  if (auto problem = unlisted(CudaCodeMode, {"ptx", "sass"})) return problem;
  if (auto problem = unlisted(ProfileName, {"amd", "nvidia", "intel", "llvmpipe", "rusticl-amd", "rusticl-intel", "rusticl-llvmpipe"}))
    return problem;
  return unlisted(VulkanApiVersion, {"1.0", "1.2"});
}

/** Shims call this from their initialisation entry points and fail to initialise when it returns false. */
inline bool accepted(const std::optional<std::string> &problem) {
  if (!problem) return true;
  static std::once_flag reported;
  std::call_once(reported, [&] { std::fprintf(stderr, "Gemstone: %s\n", problem->c_str()); });
  return false;
}

} // namespace gemstone::environment
