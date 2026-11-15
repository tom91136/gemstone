if(NOT NVML_INCLUDE_DIR OR NOT EXISTS "${NVML_INCLUDE_DIR}/nvml.h")
  message(FATAL_ERROR "NVML_INCLUDE_DIR must point at the pinned NVML headers containing nvml.h")
endif()

add_library(gemstone_nvml SHARED
  src/runtime.cpp
  ../generated/nvml/gemstone_nvml_unsupported.cpp)
set_target_properties(gemstone_nvml PROPERTIES
  OUTPUT_NAME nvidia-ml
  SOVERSION 1
  C_VISIBILITY_PRESET default
  CXX_VISIBILITY_PRESET default)
target_include_directories(gemstone_nvml SYSTEM PRIVATE "${NVML_INCLUDE_DIR}")
target_compile_definitions(gemstone_nvml PRIVATE NVML_NO_UNVERSIONED_FUNC_DEFS=1)
gemstone_project_target(gemstone_nvml)
gemstone_require_abi_implementations(
  TARGET gemstone_nvml
  PLATFORM nvml
  EXPORT "nvml*")
