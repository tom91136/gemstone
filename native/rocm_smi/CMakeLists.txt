if(NOT ROCM_SMI_INCLUDE_DIR OR NOT EXISTS "${ROCM_SMI_INCLUDE_DIR}/rocm_smi/rocm_smi.h")
  message(FATAL_ERROR "ROCM_SMI_INCLUDE_DIR must point at the pinned ROCm SMI headers containing rocm_smi/rocm_smi.h")
endif()

add_library(gemstone_rocm_smi SHARED
  src/runtime.cpp
  ../generated/rocm_smi/gemstone_rocm_smi_unsupported.cpp)
set_target_properties(gemstone_rocm_smi PROPERTIES
  OUTPUT_NAME rocm_smi64
  SOVERSION 1
  C_VISIBILITY_PRESET default
  CXX_VISIBILITY_PRESET default)
target_include_directories(gemstone_rocm_smi SYSTEM PRIVATE "${ROCM_SMI_INCLUDE_DIR}")
gemstone_project_target(gemstone_rocm_smi)
gemstone_require_abi_implementations(
  TARGET gemstone_rocm_smi
  PLATFORM rocm_smi
  EXPORT "rsmi_*")
