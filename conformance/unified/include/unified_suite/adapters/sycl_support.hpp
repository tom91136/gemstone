#ifndef UNIFIED_SUITE_ADAPTERS_SYCL_SUPPORT_HPP
#define UNIFIED_SUITE_ADAPTERS_SYCL_SUPPORT_HPP

#include <sycl/sycl.hpp>

#include <cstdlib>
#include <string>
#include <string_view>

namespace unified_suite::adapters::sycl_support {

inline bool enabled(std::string_view variable) {
  const auto *value = std::getenv(std::string(variable).c_str());
  return value != nullptr && std::string_view(value) == "1";
}

inline bool supports_fp64(const sycl::device &device) {
  return device.has(sycl::aspect::fp64) ||
         (enabled("OverrideDefaultFP64Settings") && enabled("IGC_EnableDPEmulation"));
}

} // namespace unified_suite::adapters::sycl_support

#endif
