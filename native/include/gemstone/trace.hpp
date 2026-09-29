#pragma once

#include <string_view>

#include <gemstone/environment.hpp>

#include <environment_keys.hpp>

namespace gemstone {

namespace detail {

inline std::string_view trimTraceToken(std::string_view value) {
  while (!value.empty() && (value.front() == ' ' || value.front() == '\t'))
    value.remove_prefix(1);
  while (!value.empty() && (value.back() == ' ' || value.back() == '\t'))
    value.remove_suffix(1);
  return value;
}

inline bool traceTokenMatches(std::string_view token, std::string_view scope, std::string_view category) {
  if (token == "all" || token == scope) return true;
  if (category.empty() || token.size() != scope.size() + category.size() + 1) return false;
  return token.substr(0, scope.size()) == scope && token[scope.size()] == '.' && token.substr(scope.size() + 1) == category;
}

} // namespace detail

// GEMSTONE_TRACE is a comma-separated list of categories. A token equal to the
// scope enables every category within it; "all" enables everything.
inline bool traceEnabled(std::string_view scope, std::string_view category) {
  const char *value = environment::value(environment::Trace);
  if (!value) return false;
  std::string_view remaining(value);
  while (true) {
    const auto comma = remaining.find(',');
    if (detail::traceTokenMatches(detail::trimTraceToken(remaining.substr(0, comma)), scope, category)) return true;
    if (comma == std::string_view::npos) return false;
    remaining.remove_prefix(comma + 1);
  }
}

inline bool traceEnabled(std::string_view scope) { return traceEnabled(scope, {}); }

} // namespace gemstone
