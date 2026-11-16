if(NOT AMD_SMI_INCLUDE_DIR OR NOT EXISTS "${AMD_SMI_INCLUDE_DIR}/amd_smi/amdsmi.h")
  message(FATAL_ERROR "AMD_SMI_INCLUDE_DIR must point at the pinned AMD SMI headers containing amd_smi/amdsmi.h")
endif()

add_library(gemstone_amd_smi SHARED
  src/runtime.cpp
  ../generated/amd_smi/gemstone_amd_smi_unsupported.cpp)
set_target_properties(gemstone_amd_smi PROPERTIES
  OUTPUT_NAME amd_smi
  SOVERSION 26
  C_VISIBILITY_PRESET default
  CXX_VISIBILITY_PRESET default)
target_include_directories(gemstone_amd_smi SYSTEM PRIVATE "${AMD_SMI_INCLUDE_DIR}")
gemstone_project_target(gemstone_amd_smi)
gemstone_require_abi_implementations(
  TARGET gemstone_amd_smi
  PLATFORM amd_smi
  EXPORT "amdsmi_*")
