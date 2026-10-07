#pragma once

#include <algorithm>
#include <cstdint>
#include <iterator>
#include <map>
#include <string>
#include <string_view>

namespace conformance::driver {

// Audited payload gaps in the pinned vendor packages. New versions and untested
// shapes run normally: a failed launch must never become a capability skip.
inline std::string_view suiteBlasSkip(std::string_view backend, std::string_view architecture, std::string_view code,
                                      std::string_view library, std::string_view program, std::string_view identity) {
  if (program != "unified") return {};
  const auto operation =
      identity.starts_with("blas/f32/compact/") ? identity.substr(std::string_view("blas/f32/compact/").size()) : std::string_view{};
  const bool gemm = operation == "gemm" || operation == "gemm_nt" || operation == "gemm_tn" || operation == "gemm_tt"
                    || identity == "edge/blas/f32/compact/beta_zero_gemm" || identity == "edge/blas/f32/compact/padded_gemm";
  const bool gemv = operation == "gemv" || operation == "gemv_transpose";
  if (backend == "cuda" && code == "ptx") {
    if (library == "libcublas.so.11.11.3.6" && (architecture == "sm_35" || architecture == "sm_50" || architecture == "sm_60")
        && (gemm || (architecture == "sm_35" && operation == "gemm_strided_batched")))
      return "cuBLAS 11.11.3.6: selected kernel has no PTX payload; SASS remains supported";
    if (library == "libcublas.so.13.6.0.2") {
      const bool allGemm = architecture == "sm_80" || architecture == "sm_86" || architecture == "sm_87" || architecture == "sm_89"
                           || architecture == "sm_90" || architecture == "sm_120";
      const bool transposedGemm = architecture == "sm_100" || architecture == "sm_103";
      if ((allGemm && (gemm || gemv)) || (transposedGemm && (operation == "gemm_tn" || operation == "gemm_tt" || gemv)))
        return "cuBLAS 13.6.0.2: selected kernel has no PTX payload; SASS remains supported";
    }
  }
  if (backend == "hip" && library == "librocblas.so.5.1.70101"
      && (architecture == "gfx900" || architecture == "gfx906" || architecture == "gfx90c" || architecture == "gfx1010"
          || architecture == "gfx1011" || architecture == "gfx1012" || architecture == "gfx1013")
      && (operation == "asum" || operation == "axpy" || operation == "copy" || operation == "dot" || gemv || operation == "nrm2"
          || operation == "scal" || identity == "edge/blas/f32/compact/negative_stride_axpy"
          || identity == "regressions/rocblas/gfx1036/f16/all"))
    return "rocBLAS 5.1.70101: selected kernel has no payload for this architecture";
  return {};
}

inline bool suiteUsesCatchSharding(std::string_view program) { return !program.starts_with("opencl_cts_"); }

inline std::string_view suiteTimeoutEnvironment(std::string_view program) {
  if (program == "vulkan_cts") return "CONFORMANCE_VULKAN_CTS_TIMEOUT";
  return program.starts_with("opencl_cts_") ? std::string_view("CONFORMANCE_OPENCL_CTS_TIMEOUT") : std::string_view{};
}

inline uint32_t suiteTimeout(std::string_view program, uint32_t seconds, bool isolated) {
  return isolated && program == "sycl_cts_handler" ? std::max(seconds, uint32_t{CONFORMANCE_SYCL_HANDLER_TIMEOUT_SECONDS}) : seconds;
}

inline void applySuiteIsolatedEnvironment(std::map<std::string, std::string> &environment, std::string_view backend) {
  if (backend == "vulkan") {
    environment.try_emplace("CONFORMANCE_VULKAN_CTS_TIMEOUT", "600");
    return;
  }
  if (backend != "opencl") return;
  environment.try_emplace("CONFORMANCE_OPENCL_CTS_ELEMENTS", "1024");
  environment.try_emplace("CONFORMANCE_OPENCL_CTS_WIMPY", "1");
  environment.try_emplace("CONFORMANCE_OPENCL_CTS_TIMEOUT", CONFORMANCE_OPENCL_CTS_TIMEOUT_SECONDS);
}

// Suite knobs a machine profile or the invoking environment may set.
inline constexpr std::string_view SuiteEnvironmentKeys[]{
    "CONFORMANCE_VULKAN_CTS_TIMEOUT", "CONFORMANCE_OPENCL_CTS_ELEMENTS",          "CONFORMANCE_OPENCL_CTS_TIMEOUT",
    "CONFORMANCE_OPENCL_CTS_WIMPY",   "CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATIONS", "CONFORMANCE_OPENCL_CTS_ATOMIC_THREADS"};

inline bool suiteEnvironmentKey(std::string_view key) {
  return std::find(std::begin(SuiteEnvironmentKeys), std::end(SuiteEnvironmentKeys), key) != std::end(SuiteEnvironmentKeys);
}

} // namespace conformance::driver
