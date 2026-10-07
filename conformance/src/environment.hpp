#pragma once
#include <algorithm>
#include <array>
#include <string_view>

#include "suite_policy.hpp"

namespace conformance {
inline constexpr std::string_view SelectionEnvironmentKeys[]{
    "CUDA_VISIBLE_DEVICES",   "CUDA_DEVICE_ORDER",      "HIP_VISIBLE_DEVICES",
    "ROCR_VISIBLE_DEVICES",   "GPU_DEVICE_ORDINAL",     "ONEAPI_DEVICE_SELECTOR",
    "SYCL_DEVICE_FILTER",     "SYCL_DEVICE_ALLOWLIST",  "CL_PLATFORM_INDEX",
    "CL_DEVICE_INDEX",        "CL_DEVICE_TYPE",         "CONFORMANCE_OPENCL_DEVICE_INDEX",
    "CONFORMANCE_DEVICE",     "OMP_DEFAULT_DEVICE",     "ACC_DEVICE_NUM",
    "LZT_DEFAULT_DRIVER_IDX", "LZT_DEFAULT_DEVICE_IDX", "CONFORMANCE_VULKAN_DEVICE_ID"};
inline bool selectionEnvironmentKey(std::string_view key) {
  return std::find(std::begin(SelectionEnvironmentKeys), std::end(SelectionEnvironmentKeys), key) != std::end(SelectionEnvironmentKeys);
}
inline bool reservedEnvironmentKey(std::string_view key) {
  if (driver::suiteEnvironmentKey(key)) return false;
  // CUDA code selection is an explicit machine-route setting, like suite-level
  // knobs, and is consumed by the independent unified fixtures as well as the
  // launched backend.
  if (key == "CONFORMANCE_CUDA_CODE") return false;
  if (key.starts_with("CONFORMANCE_") || selectionEnvironmentKey(key)) return true;
  constexpr std::string_view keys[]{"PATH",
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
                                    "CMAKE_PREFIX_PATH"};
  return std::find(std::begin(keys), std::end(keys), key) != std::end(keys);
}
inline bool validEnvironmentEntry(std::string_view key, std::string_view value) {
  if (key.empty() || value.find('\0') != std::string_view::npos) return false;
  for (size_t index = 0; index < key.size(); ++index) {
    const auto c = key[index];
    if (c != '_' && !(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z') && !(index != 0 && c >= '0' && c <= '9')) return false;
  }
  return true;
}
} // namespace conformance
