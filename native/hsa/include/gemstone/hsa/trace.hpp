#pragma once

#include <string_view>

#include <gemstone/trace.hpp>

namespace gemstone::hsa {

inline bool traceEnabled(std::string_view category) { return gemstone::traceEnabled("GEMSTONE_HSA_TRACE", category); }

} // namespace gemstone::hsa
