#pragma once
#include <algorithm>
#include <array>
#include <string_view>

namespace conformance {
inline constexpr std::string_view selection_environment_keys[]{
    "CUDA_VISIBLE_DEVICES", "CUDA_DEVICE_ORDER",      "HIP_VISIBLE_DEVICES", "ROCR_VISIBLE_DEVICES",
    "GPU_DEVICE_ORDINAL",   "ONEAPI_DEVICE_SELECTOR", "SYCL_DEVICE_FILTER",  "SYCL_DEVICE_ALLOWLIST",
    "CL_PLATFORM_INDEX",    "CL_DEVICE_INDEX",        "CL_DEVICE_TYPE",      "CONFORMANCE_OPENCL_DEVICE_INDEX",
    "CONFORMANCE_DEVICE",   "OMP_DEFAULT_DEVICE",     "ACC_DEVICE_NUM"};
inline bool selection_environment_key(std::string_view key) {
  return std::find(std::begin(selection_environment_keys), std::end(selection_environment_keys), key)
         != std::end(selection_environment_keys);
}
inline bool reserved_environment_key(std::string_view key) {
  if (key == "CONFORMANCE_OPENCL_CTS_ELEMENTS" || key == "CONFORMANCE_OPENCL_CTS_TIMEOUT" || key == "CONFORMANCE_OPENCL_CTS_WIMPY")
    return false;
  if (key.starts_with("CONFORMANCE_") || selection_environment_key(key)) return true;
  constexpr std::string_view keys[]{"PATH",
                                    "LD_LIBRARY_PATH",
                                    "LD_PRELOAD",
                                    "LD_AUDIT",
                                    "DYLD_LIBRARY_PATH",
                                    "DYLD_FALLBACK_LIBRARY_PATH",
                                    "DYLD_INSERT_LIBRARIES",
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
inline bool valid_environment_entry(std::string_view key, std::string_view value) {
  if (key.empty() || value.find('\0') != std::string_view::npos) return false;
  for (size_t index = 0; index < key.size(); ++index) {
    const auto c = key[index];
    if (c != '_' && !(c >= 'A' && c <= 'Z') && !(c >= 'a' && c <= 'z') && !(index != 0 && c >= '0' && c <= '9')) return false;
  }
  return true;
}
} // namespace conformance
