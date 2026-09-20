#ifndef GEMSTONE_PROCESS_IDENTITY_HPP
#define GEMSTONE_PROCESS_IDENTITY_HPP

#include <array>
#include <charconv>
#include <cstdio>
#include <memory>
#include <string_view>
#include <system_error>

#include <stdint.h>

namespace gemstone::execution {
namespace detail {

struct FileCloser {
  void operator()(std::FILE *file) const noexcept { std::fclose(file); }
};

inline uint64_t processStartTime(std::string_view stat) {
  const auto commandEnd = stat.rfind(')');
  if (commandEnd == std::string_view::npos || commandEnd + 2 >= stat.size()) return 0;

  auto cursor = commandEnd + 2;
  for (int field = 3; field < 22; ++field) {
    cursor = stat.find(' ', cursor);
    if (cursor == std::string_view::npos) return 0;
    ++cursor;
  }

  const auto end = stat.find(' ', cursor);
  const auto value = stat.substr(cursor, end == std::string_view::npos ? stat.size() - cursor : end - cursor);
  uint64_t result = 0;
  const auto parsed = std::from_chars(value.data(), value.data() + value.size(), result);
  return parsed.ec == std::errc{} && parsed.ptr == value.data() + value.size() ? result : 0;
}

} // namespace detail

// PID plus Linux process start time is stable across DSOs and changes after PID reuse.
inline uint64_t processIncarnation() {
#ifdef __linux__
  const std::unique_ptr<std::FILE, detail::FileCloser> stat(std::fopen("/proc/self/stat", "r"));
  if (!stat) return 0;
  std::array<char, 4096> text{};
  if (!std::fgets(text.data(), static_cast<int>(text.size()), stat.get())) return 0;
  return detail::processStartTime(text.data());
#else
  // No stable cross-DSO process-start identity: callers must fail closed.
  return 0;
#endif
}

} // namespace gemstone::execution

#endif
