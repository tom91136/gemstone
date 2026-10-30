#pragma once
#include <array>
#include <charconv>
#include <cstddef>
#include <cstdint>
#include <optional>
#include <string>
#include <string_view>

#include <aspartame/all.hpp>

#include <conformance/environment_keys.hpp>

#include "suite_policy.hpp"

namespace conformance {
inline constexpr auto SelectionEnvironmentKeys = std::to_array<std::string_view>(
    {"CUDA_VISIBLE_DEVICES", "CUDA_DEVICE_ORDER", "HIP_VISIBLE_DEVICES", "ROCR_VISIBLE_DEVICES", "GPU_DEVICE_ORDINAL",
     "ONEAPI_DEVICE_SELECTOR", "SYCL_DEVICE_FILTER", "SYCL_DEVICE_ALLOWLIST", "CL_PLATFORM_INDEX", "CL_DEVICE_INDEX", "CL_DEVICE_TYPE",
     environment::OpenclDeviceIndex, environment::DevicePattern, "OMP_DEFAULT_DEVICE", "ACC_DEVICE_NUM", "LZT_DEFAULT_DRIVER_IDX",
     "LZT_DEFAULT_DEVICE_IDX", environment::VulkanDeviceId});
inline bool selectionEnvironmentKey(std::string_view key) {
  using namespace aspartame;
  return SelectionEnvironmentKeys ^ contains(key);
}
inline bool reservedEnvironmentKey(std::string_view key) {
  using namespace aspartame;
  if (driver::suiteEnvironmentKey(key)) return false;
  // CUDA code selection is an explicit machine-route setting, like suite-level
  // knobs, and is consumed by the independent unified fixtures as well as the
  // launched backend.
  if (key == environment::CudaCodeMode) return false;
  if (key.starts_with("CONFORMANCE_") || selectionEnvironmentKey(key)) return true;
  constexpr auto keys = std::to_array<std::string_view>({"PATH",
                                                         "LD_LIBRARY_PATH",
                                                         "LD_PRELOAD",
                                                         "LD_AUDIT",
                                                         "UR_ADAPTERS_FORCE_LOAD",
                                                         "ROCBLAS_TENSILE_LIBPATH",
                                                         "AMDGPU_ASIC_ID_TABLE_PATHS",
                                                         "CUDA_HOME",
                                                         "CUDA_PATH",
                                                         "CUDA_ROOT",
                                                         "CUDA_TOOLKIT_ROOT_DIR",
                                                         "HIP_PATH",
                                                         "ROCM_PATH",
                                                         "ROCM_HOME",
                                                         "ONEAPI_ROOT",
                                                         "ONEAPI_HOME",
                                                         "DPCPP_HOME",
                                                         "INTEL_ONEAPI_ROOT",
                                                         "CPATH",
                                                         "LIBRARY_PATH",
                                                         "CMAKE_PREFIX_PATH"});
  return keys ^ contains(key);
}
inline bool validEnvironmentEntry(std::string_view key, std::string_view value) {
  using namespace aspartame;
  return !key.empty() && !value.contains('\0') && (key ^ zip_with_index() ^ forall([](char c, size_t index) {
                                                     return c == '_' || (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                                                            || (index != 0 && c >= '0' && c <= '9');
                                                   }));
}
// Suite settings come from the process or a machine profile and are checked before any target runs.
inline std::optional<std::string> invalidSuiteSetting(std::string_view key, std::string_view value) {
  if (value.empty()) return std::nullopt;
  const auto rejected = [&](std::string_view expected) {
    return std::optional{std::string(key) + " must be " + std::string(expected) + ", not '" + std::string(value) + "'"};
  };
  if (key == environment::OpenclCtsWimpyOn)
    return environment::boolean(value) ? std::nullopt : rejected("one of 1 true on y yes 0 false off n no");
  if (key == environment::CudaCodeMode) return value == "ptx" || value == "sass" ? std::nullopt : rejected("one of ptx sass");
  if (!driver::suiteEnvironmentKey(key)) return std::nullopt;
  uint64_t parsed = 0;
  const auto [end, error] = std::from_chars(value.data(), value.data() + value.size(), parsed);
  return error == std::errc{} && end == value.data() + value.size() && parsed > 0 ? std::nullopt : rejected("a positive integer");
}
} // namespace conformance
