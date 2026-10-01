#pragma once

#include <algorithm>
#include <cstdint>
#include <map>
#include <string>
#include <string_view>

// Supplied by CMake from conformance/cmake/TestTimeouts.cmake.
#ifndef CONFORMANCE_OPENCL_CTS_TIMEOUT_SECONDS
  #define CONFORMANCE_OPENCL_CTS_TIMEOUT_SECONDS "600"
#endif

namespace conformance::driver {

// Execution policy for programs that are not plain Catch executables. Keeping
// these conventions in one place stops one suite's quirks leaking into the
// generic discovery and execution paths.
inline bool suite_uses_catch_sharding(std::string_view program) { return !program.starts_with("opencl_cts_"); }

inline std::string_view suite_timeout_environment(std::string_view program) {
  if (program == "vulkan_cts") return "CONFORMANCE_VULKAN_CTS_TIMEOUT";
  return program.starts_with("opencl_cts_") ? std::string_view("CONFORMANCE_OPENCL_CTS_TIMEOUT") : std::string_view{};
}

inline uint32_t suite_timeout(std::string_view program, uint32_t seconds, bool isolated) {
  return isolated && program == "sycl_cts_handler" ? std::max(seconds, uint32_t{CONFORMANCE_SYCL_HANDLER_TIMEOUT_SECONDS}) : seconds;
}

inline void apply_suite_isolated_environment(std::map<std::string, std::string> &environment, std::string_view backend) {
  if (backend == "vulkan") {
    environment.try_emplace("CONFORMANCE_VULKAN_CTS_TIMEOUT", "600");
    return;
  }
  if (backend != "opencl") return;
  environment.try_emplace("CONFORMANCE_OPENCL_CTS_ELEMENTS", "1024");
  environment.try_emplace("CONFORMANCE_OPENCL_CTS_WIMPY", "1");
  environment.try_emplace("CONFORMANCE_OPENCL_CTS_TIMEOUT", CONFORMANCE_OPENCL_CTS_TIMEOUT_SECONDS);
}

inline bool suite_environment_key(std::string_view key) {
  return key == "CONFORMANCE_VULKAN_CTS_TIMEOUT" || key == "CONFORMANCE_OPENCL_CTS_ELEMENTS" || key == "CONFORMANCE_OPENCL_CTS_TIMEOUT"
         || key == "CONFORMANCE_OPENCL_CTS_WIMPY" || key == "CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS"
         || key == "CONFORMANCE_OPENCL_CTS_ATOMIC_THREADS";
}

} // namespace conformance::driver
