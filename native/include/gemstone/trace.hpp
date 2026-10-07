#pragma once

#include <string_view>

#include <gemstone/environment.hpp>

#include <environment_keys.hpp>

namespace gemstone {

// GEMSTONE_TRACE is a comma-separated list of categories. A token equal to the
// scope enables every category within it; "all" enables everything.
inline bool traceEnabled(std::string_view scope, std::string_view category) {
  return environment::listed(environment::Trace, [&](std::string_view token) {
    if (token == "all" || token == scope) return true;
    return !category.empty() && token.size() == scope.size() + category.size() + 1 && token.starts_with(scope) && token[scope.size()] == '.'
           && token.ends_with(category);
  });
}

inline bool traceEnabled(std::string_view scope) { return traceEnabled(scope, {}); }

} // namespace gemstone
