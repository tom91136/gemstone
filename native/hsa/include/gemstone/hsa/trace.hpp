#pragma once

#include <string_view>

#include <gemstone/trace.hpp>

namespace gemstone::hsa {

inline bool traceEnabled(std::string_view category) { return gemstone::traceEnabled("hsa", category); }

} // namespace gemstone::hsa
