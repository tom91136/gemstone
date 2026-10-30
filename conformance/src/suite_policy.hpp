#pragma once

#include <algorithm>
#include <array>
#include <cstdint>
#include <map>
#include <string>
#include <string_view>

#include "cuda_architecture.hpp"
#include <aspartame/all.hpp>

#include <conformance/environment_keys.hpp>

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
    // cuBLAS selects kernels by the device's compute capability, which suffixed routes share with their base.
    const auto cuda = CudaArchitecture::parse(architecture);
    const int sm = cuda ? cuda->capability : 0;
    if (library == "libcublas.so.11.11.3.6" && (sm == 35 || sm == 50 || sm == 60)
        && (gemm || (sm == 35 && operation == "gemm_strided_batched")))
      return "cuBLAS 11.11.3.6: selected kernel has no PTX payload; SASS remains supported";
    if (library == "libcublas.so.13.6.0.2") {
      const bool allGemm = sm == 80 || sm == 86 || sm == 87 || sm == 89 || sm == 90 || sm == 120;
      const bool transposedGemm = sm == 100 || sm == 103;
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
  if (program == "vulkan_cts") return environment::VulkanCtsTimeoutSeconds;
  return program.starts_with("opencl_cts_") ? std::string_view(environment::OpenclCtsTimeoutSeconds) : std::string_view{};
}

inline uint32_t suiteTimeout(std::string_view program, uint32_t seconds, bool isolated) {
  return isolated && program == "sycl_cts_handler" ? std::max(seconds, uint32_t{CONFORMANCE_SYCL_HANDLER_TIMEOUT_SECONDS}) : seconds;
}

inline void applySuiteIsolatedEnvironment(std::map<std::string, std::string> &environment, std::string_view backend) {
  if (backend == "vulkan") {
    environment.try_emplace(environment::VulkanCtsTimeoutSeconds, "600");
    return;
  }
  if (backend != "opencl") return;
  environment.try_emplace(environment::OpenclCtsElementCount, "1024");
  environment.try_emplace(environment::OpenclCtsWimpyOn, "1");
  environment.try_emplace(environment::OpenclCtsTimeoutSeconds, CONFORMANCE_OPENCL_ISOLATED_TIMEOUT_SECONDS);
}

// Suite knobs a machine profile or the invoking environment may set.
inline constexpr auto SuiteEnvironmentKeys = std::to_array<std::string_view>(
    {environment::VulkanCtsTimeoutSeconds, environment::OpenclCtsElementCount, environment::OpenclCtsTimeoutSeconds,
     environment::OpenclCtsWimpyOn, environment::OpenclCtsAtomicIterationCount, environment::OpenclCtsAtomicThreadCount});

inline bool suiteEnvironmentKey(std::string_view key) {
  using namespace aspartame;
  return SuiteEnvironmentKeys ^ contains(key);
}

} // namespace conformance::driver
