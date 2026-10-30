#pragma once

#include <array>
#include <optional>
#include <string_view>

#include <aspartame/array.hpp>
#include <aspartame/string_view.hpp>

namespace conformance::environment {
inline constexpr char CaseShard[] = "CONFORMANCE_CASE_SHARD";
inline constexpr char CudaCodeMode[] = "CONFORMANCE_CUDA_CODE_MODE";
inline constexpr char DevicePattern[] = "CONFORMANCE_DEVICE_PATTERN";
inline constexpr char JobCount[] = "CONFORMANCE_JOB_COUNT";
inline constexpr char OpenclCtsAtomicIterationCount[] = "CONFORMANCE_OPENCL_CTS_ATOMIC_ITERATION_COUNT";
inline constexpr char OpenclCtsAtomicThreadCount[] = "CONFORMANCE_OPENCL_CTS_ATOMIC_THREAD_COUNT";
inline constexpr char OpenclCtsElementCount[] = "CONFORMANCE_OPENCL_CTS_ELEMENT_COUNT";
inline constexpr char OpenclCtsTimeoutSeconds[] = "CONFORMANCE_OPENCL_CTS_TIMEOUT_SECONDS";
inline constexpr char OpenclCtsWimpyOn[] = "CONFORMANCE_OPENCL_CTS_WIMPY_ON";
inline constexpr char OpenclDeviceIndex[] = "CONFORMANCE_OPENCL_DEVICE_INDEX";
inline constexpr char PhysicalDeviceName[] = "CONFORMANCE_PHYSICAL_DEVICE_NAME";
inline constexpr char ReportDir[] = "CONFORMANCE_REPORT_DIR";
inline constexpr char RuntimeInputDir[] = "CONFORMANCE_RUNTIME_INPUT_DIR";
inline constexpr char SupportDir[] = "CONFORMANCE_SUPPORT_DIR";
inline constexpr char TargetArch[] = "CONFORMANCE_TARGET_ARCH";
inline constexpr char TargetBackendName[] = "CONFORMANCE_TARGET_BACKEND_NAME";
inline constexpr char TestName[] = "CONFORMANCE_TEST_NAME";
inline constexpr char ThreadCount[] = "CONFORMANCE_THREAD_COUNT";
inline constexpr char TimeoutScale[] = "CONFORMANCE_TIMEOUT_SCALE";
inline constexpr char VulkanCtsTimeoutSeconds[] = "CONFORMANCE_VULKAN_CTS_TIMEOUT_SECONDS";
inline constexpr char VulkanDeviceId[] = "CONFORMANCE_VULKAN_DEVICE_ID";

// Artifact build options, also taken from the environment by drivers that configure the build.
inline constexpr char ShardRouteList[] = "CONFORMANCE_SHARD_ROUTE_LIST";
inline constexpr char SyclShardIndex[] = "CONFORMANCE_SYCL_SHARD_INDEX";

// CMake truth values, matching Gemstone.
inline std::optional<bool> boolean(std::string_view value) {
  using namespace aspartame;
  using namespace std::string_view_literals;
  constexpr auto truthy = std::array{"1"sv, "true"sv, "on"sv, "y"sv, "yes"sv};
  constexpr auto falsy = std::array{"0"sv, "false"sv, "off"sv, "n"sv, "no"sv};
  const auto matches = [&](std::string_view name) { return name ^ equals_ignore_case(value); };
  if (truthy ^ exists(matches)) return true;
  if (falsy ^ exists(matches)) return false;
  return std::nullopt;
}
} // namespace conformance::environment
