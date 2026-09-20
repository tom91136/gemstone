#pragma once

#include <cstdlib>
#include <string_view>

#include <aspartame/string_view.hpp>
#include <aspartame/vector.hpp>

namespace gemstone {

inline bool traceEnabled(const char *variable, std::string_view category) {
  using namespace aspartame;
  const char *value = std::getenv(variable);
  if (!value) return false;
  return std::string_view(value) ^ split(',') ^ exists([category](std::string_view entry) {
           const auto token = entry ^ trim();
           return token == category || token == "all";
         });
}

} // namespace gemstone
