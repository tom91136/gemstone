#pragma once

#include <algorithm>
#include <array>
#include <cstdint>
#include <map>
#include <string>
#include <string_view>

#include <aspartame/all.hpp>

#include <conformance/environment_keys.hpp>

namespace conformance::driver {

// Audited payload gaps in the pinned vendor packages. New versions and untested
// shapes run normally: a failed launch must never become a capability skip.
inline std::string_view suiteBlasSkip(std::string_view backend, std::string_view architecture, std::string_view library,
                                      std::string_view program, std::string_view identity) {
  if (program != "unified") return {};
  const auto operation =
      identity.starts_with("blas/f32/compact/") ? identity.substr(std::string_view("blas/f32/compact/").size()) : std::string_view{};
  const bool gemv = operation == "gemv" || operation == "gemv_transpose";
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
inline constexpr auto SuiteEnvironmentKeys =
    std::to_array<std::string_view>({environment::DeviceCount, environment::VulkanCtsTimeoutSeconds, environment::OpenclCtsElementCount,
                                     environment::OpenclCtsTimeoutSeconds, environment::OpenclCtsWimpyOn,
                                     environment::OpenclCtsAtomicIterationCount, environment::OpenclCtsAtomicThreadCount});

inline bool suiteEnvironmentKey(std::string_view key) {
  using namespace aspartame;
  return SuiteEnvironmentKeys ^ contains(key);
}

inline constexpr auto LevelzeroMultiDeviceCases = std::to_array<std::string_view>({
    "TestAffinity.GivenAffinityMaskSetWhenGettingDevicesThenCorrectNumberOfDevicesReturnedInAllCases",
    "TestAffinity.GivenSpecificAffinityMaskSetWhenGettingDevicesThenCorrectNumberOfDevicesReturned",
    "TestAffinity.GivenAffinityMaskSetWhenGettingSubDevicesThenCorrectNumberOfSubDevicesReturned",
    "TestDeviceHierarchy.GivenDeviceHierarchyFlatThenGivenAffinityMaskSetWhenGettingDevicesThenCorrectNumberOfDevicesReturnedInAllCases",
    "TestDeviceHierarchy."
    "GivenDeviceHierarchyCombinedThenGivenAffinityMaskSetWhenGettingDevicesThenCorrectNumberOfDevicesReturnedInAllCases",
    "TestDeviceHierarchy."
    "GivenDeviceHierarchyCompositeThenGivenAffinityMaskSetWhenGettingDevicesThenCorrectNumberOfDevicesReturnedInAllCases",
    "TestDeviceHierarchy.GivenDeviceHierarchyFlatThenWhenGettingDevicesThenCorrectNumberOfDevicesReturnedFromRootAndSubDevices",
    "TestDeviceHierarchy.GivenDeviceHierarchyCombinedThenWhenGettingDevicesThenCorrectNumberOfDevicesReturnedFromRootAndSubDevices",
    "TestDeviceHierarchy.GivenDeviceHierarchyCompositeThenWhenGettingDevicesThenCorrectNumberOfDevicesReturnedFromRootAndSubDevices",
    "zeP2PContextCopyTests.GivenMultipleDevicesInDifferentContextsWhenAttemptingToUseMemoryThenMemoryIsAccessible",
});

inline constexpr auto VendorHipMultiDeviceCases = std::to_array<std::string_view>({
    "vendor/hip/rocm-examples/HIP-Basic/multi_gpu_data_transfer",
    "vendor/hip/rocm-examples/Libraries/rocFFT/multi_gpu",
});

// Device count for one case, empty when the case runs with the suite count.
inline std::string_view suiteCaseDeviceCount(std::string_view program, std::string_view identity) {
  using namespace aspartame;
  if (program.starts_with("levelzero_") && LevelzeroMultiDeviceCases ^ contains(identity)) return "2";
  if (program == "vendor_hip_examples" && VendorHipMultiDeviceCases ^ contains(identity)) return "2";
  return {};
}

// Timing-bound cases. They run with no other case on the machine.
inline constexpr auto LevelzeroExclusivePrograms =
    std::to_array<std::string_view>({"levelzero_cts_test_cmdqueue", "levelzero_cts_test_cmdlist", "levelzero_cts_test_event"});
inline constexpr auto LevelzeroExclusiveCases = std::to_array<std::string_view>({"SyncTimeoutParams/*"});

inline bool suiteExclusiveCase(std::string_view program, std::string_view identity) {
  using namespace aspartame;
  return LevelzeroExclusivePrograms ^ contains(program)
         && (LevelzeroExclusiveCases ^ exists([&](std::string_view pattern) { return identity ^ glob_matches(pattern); }));
}

} // namespace conformance::driver
