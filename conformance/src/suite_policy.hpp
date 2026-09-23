#pragma once

#include <map>
#include <string>
#include <string_view>

namespace conformance::driver {

// Execution policy for programs that are not plain Catch executables. Keeping
// these conventions in one place stops one suite's quirks leaking into the
// generic discovery and execution paths.
inline bool suite_uses_catch_sharding(std::string_view program) {
  return !program.starts_with("opencl_cts_") && !program.starts_with("levelzero_cts_");
}

inline std::string_view suite_timeout_environment(std::string_view program) {
  return program.starts_with("opencl_cts_") ? std::string_view("CONFORMANCE_OPENCL_CTS_TIMEOUT") : std::string_view{};
}

inline void apply_suite_isolated_environment(std::map<std::string, std::string> &environment, std::string_view backend) {
  if (backend == "levelzero") {
    environment["CONFORMANCE_DEVICE"] = "*Gemstone Virtual SPIR-V GPU*";
    return;
  }
  if (backend != "opencl") return;
  environment["CONFORMANCE_OPENCL_CTS_ELEMENTS"] = "1024";
  environment["CONFORMANCE_OPENCL_CTS_WIMPY"] = "1";
  environment["CONFORMANCE_OPENCL_CTS_TIMEOUT"] = "600";
}

inline bool suite_environment_key(std::string_view key) {
  return key == "CONFORMANCE_OPENCL_CTS_ELEMENTS" || key == "CONFORMANCE_OPENCL_CTS_TIMEOUT" || key == "CONFORMANCE_OPENCL_CTS_WIMPY";
}

} // namespace conformance::driver
