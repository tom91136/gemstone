# Pinned vendor tools for the discovery cases. They are fetched into the build
# tree and never installed into the conformance package.
set(GEMSTONE_TOOL_DIRECTORY "${CMAKE_BINARY_DIR}/tools" CACHE PATH "Downloaded vendor tools for the native tests")
set(GEMSTONE_SYCL_LS "" CACHE FILEPATH "sycl-ls from the pinned oneAPI toolkit")
set(GEMSTONE_SYCL_LIBRARY_DIRECTORY "" CACHE PATH "oneAPI runtime libraries for sycl-ls")

if(NOT GEMSTONE_SYCL_LS AND EXISTS "/opt/intel/oneapi/compiler/2026.1/bin/sycl-ls")
  set(GEMSTONE_SYCL_LS "/opt/intel/oneapi/compiler/2026.1/bin/sycl-ls" CACHE FILEPATH "sycl-ls from the pinned oneAPI toolkit" FORCE)
endif()
if(NOT GEMSTONE_SYCL_LIBRARY_DIRECTORY AND EXISTS "/opt/intel/oneapi/compiler/2026.1/lib" AND EXISTS "/opt/intel/oneapi/umf/1.2/lib")
  set(GEMSTONE_SYCL_LIBRARY_DIRECTORY "/opt/intel/oneapi/compiler/2026.1/lib;/opt/intel/oneapi/umf/1.2/lib" CACHE PATH "oneAPI runtime libraries for sycl-ls" FORCE)
endif()

function(gemstone_fetch_deb name url sha256)
  set(archive "${GEMSTONE_TOOL_DIRECTORY}/cache/${name}.deb")
  set(root "${GEMSTONE_TOOL_DIRECTORY}/${name}")
  if(EXISTS "${root}/.complete")
    set("${name}_ROOT" "${root}" PARENT_SCOPE)
    return()
  endif()
  file(MAKE_DIRECTORY "${GEMSTONE_TOOL_DIRECTORY}/cache")
  if(EXISTS "${archive}")
    file(SHA256 "${archive}" actual)
    if(NOT actual STREQUAL sha256)
      file(REMOVE "${archive}")
    endif()
  endif()
  if(NOT EXISTS "${archive}")
    file(DOWNLOAD "${url}" "${archive}" EXPECTED_HASH "SHA256=${sha256}" STATUS status TLS_VERIFY ON)
    list(GET status 0 code)
    if(NOT code EQUAL 0)
      list(GET status 1 message)
      message(FATAL_ERROR "cannot download ${name}: ${message}")
    endif()
  endif()
  file(REMOVE_RECURSE "${root}")
  file(MAKE_DIRECTORY "${root}")
  execute_process(COMMAND dpkg-deb -x "${archive}" "${root}" RESULT_VARIABLE result)
  if(NOT result EQUAL 0)
    message(FATAL_ERROR "cannot extract ${name}")
  endif()
  file(TOUCH "${root}/.complete")
  set("${name}_ROOT" "${root}" PARENT_SCOPE)
endfunction()

if(BUILD_TESTING)
  gemstone_fetch_deb(rocminfo
    "https://repo.radeon.com/rocm/apt/7.1.1/pool/main/r/rocminfo/rocminfo_1.0.0.70101-38%7E24.04_amd64.deb"
    "3c5d9f908c0c3b03cec68b71feb40c27bf06161b06e9d0cbbca10836a806322e")
  gemstone_fetch_deb(clinfo
    "http://archive.ubuntu.com/ubuntu/pool/universe/c/clinfo/clinfo_3.0.23.01.25-1build1_amd64.deb"
    "d1375897a30ba98eafdaba5a00715c1b3f9e5797174d04377e39828b69b10fc9")
  gemstone_fetch_deb(vulkaninfo
    "http://archive.ubuntu.com/ubuntu/pool/universe/v/vulkan-tools/vulkan-tools_1.3.275.0+dfsg1-1_amd64.deb"
    "994e429b5a4550b46c5bd9a938af1f60c4aa4890e08f661603b9518a8df824d1")
  gemstone_fetch_deb(nvidia-smi
    "https://developer.download.nvidia.com/compute/cuda/repos/ubuntu2404/x86_64/nvidia-utils-580_580.65.06-0ubuntu1_amd64.deb"
    "5af89f7fe38be8cbff553245b45d1b31a8ac0e2a073e16990046bf2464360d85")
  gemstone_fetch_deb(nvtop
    "http://archive.ubuntu.com/ubuntu/pool/universe/n/nvtop/nvtop_3.0.2-1_amd64.deb"
    "70431cfc6c6ae819ef77617d980670970855364e68b68377158a6ad837f45ee7")
  gemstone_fetch_deb(rocm-smi
    "https://repo.radeon.com/rocm/apt/7.1.1/pool/main/r/rocm-smi-lib/rocm-smi-lib_7.8.0.70101-38%7E24.04_amd64.deb"
    "090855a6f74931d9eb8f31e1040cf07d1482bc153ee2621ecdf6d0a57f7307a8")
  gemstone_fetch_deb(amd-smi
    "https://repo.radeon.com/rocm/apt/7.1.1/pool/main/a/amd-smi-lib/amd-smi-lib_26.2.0.70101-38%7E24.04_amd64.deb"
    "58580c1e9a32090273bfa7b74974649ada0a0a6118f6e1b159f4a35664ac0cda")
  foreach(dependency IN ITEMS
      "libx11-6|pool/main/libx/libx11/libx11-6_1.8.7-1build1_amd64.deb|397f84347476a3c5786b39f3ff6f0f82866eb3d8be6d2ad3efeadf019efe5b80"
      "libxcb1|pool/main/libx/libxcb/libxcb1_1.15-1ubuntu2_amd64.deb|e1c6611d11ad7398326f1bf028afc34c3b14c51d917a3426b966ed4b9687fa58"
      "libxau6|pool/main/libx/libxau/libxau6_1.0.9-1build6_amd64.deb|e40d29f1d1a62393bacaedebe0da3d9006084152a9f7e5e029293f08ce1c5c80"
      "libxdmcp6|pool/main/libx/libxdmcp/libxdmcp6_1.1.3-0ubuntu6_amd64.deb|bcd336fce11ce2a45f34d0f95e6980af22529f22147e8f98c156e5cee8ee42bb"
      "libwayland-client0|pool/main/w/wayland/libwayland-client0_1.22.0-2.1build1_amd64.deb|6af0aed41d75149bea22fa468f01eb058ffe3e35ef07ff2f13fb88a90387881d"
      "libffi8|pool/main/libf/libffi/libffi8_3.4.6-1build1_amd64.deb|637e6a7744de08cd331a41f4efd0d24e6ea9064843dea9d1c6ca87bdb5f038a2"
      "libbsd0|pool/main/libb/libbsd/libbsd0_0.12.1-1build1_amd64.deb|8e97b7d541416512a81e7845c5f21910344c9c5c610893cf265d3298e0caad6b"
      "libmd0|pool/main/libm/libmd/libmd0_1.1.0-2build1_amd64.deb|77f8cdbf03a43656bbf6604e18bd16834fc8bf2c949d4fbdf24ac297869b667a"
      "libdrm2|pool/main/libd/libdrm/libdrm2_2.4.120-2build1_amd64.deb|f5fb4e7ce17921cc466fb7911abf91495ffb181b36772f68e2e82cb621703112"
      "libdrm-amdgpu1|pool/main/libd/libdrm/libdrm-amdgpu1_2.4.120-2build1_amd64.deb|e149d4daea33f58853b8013fd6c24888429ce7716a4b26d1a1f45181b5a4e73e"
      "libelf1t64|pool/main/e/elfutils/libelf1t64_0.190-1.1build4_amd64.deb|b277e52769302778bd052376ac6687b52954b6605dd5f781bff8631e3504d58f"
      "libnuma1|pool/main/n/numactl/libnuma1_2.0.18-1build1_amd64.deb|508daa855e99959acaa945e6a89d218e0be6b5727fd28773580942ff37cf5805")
    string(REPLACE "|" ";" fields "${dependency}")
    list(GET fields 0 name)
    list(GET fields 1 file)
    list(GET fields 2 sha)
    gemstone_fetch_deb("${name}" "http://archive.ubuntu.com/ubuntu/${file}" "${sha}")
  endforeach()

  file(MAKE_DIRECTORY "${GEMSTONE_TOOL_DIRECTORY}/lib")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/libOpenCL_gemstone.so.1" "${GEMSTONE_TOOL_DIRECTORY}/lib/libOpenCL.so.1")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/libnvidia-ml.so.1" "${GEMSTONE_TOOL_DIRECTORY}/lib/libnvidia-ml.so.1")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/libnvidia-ml.so.1" "${GEMSTONE_TOOL_DIRECTORY}/lib/libnvidia-ml.so")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/librocm_smi64.so.1" "${GEMSTONE_TOOL_DIRECTORY}/lib/librocm_smi64.so.1")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/librocm_smi64.so.1" "${GEMSTONE_TOOL_DIRECTORY}/lib/librocm_smi64.so")
  file(MAKE_DIRECTORY "${rocm-smi_ROOT}/opt/rocm-7.1.1/lib")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/librocm_smi64.so.1" "${rocm-smi_ROOT}/opt/rocm-7.1.1/lib/librocm_smi64.so.1")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/librocm_smi64.so.1" "${rocm-smi_ROOT}/opt/rocm-7.1.1/lib/librocm_smi64.so")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/libamd_smi.so.26" "${GEMSTONE_TOOL_DIRECTORY}/lib/libamd_smi.so.26")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/libamd_smi.so.26" "${GEMSTONE_TOOL_DIRECTORY}/lib/libamd_smi.so")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E create_symlink
    "${CMAKE_BINARY_DIR}/libamd_smi.so.26" "${amd-smi_ROOT}/opt/rocm-7.1.1/share/amd_smi/amdsmi/libamd_smi.so")

  set(GEMSTONE_TOOL_ROCMINFO "${rocminfo_ROOT}/opt/rocm-7.1.1/bin/rocminfo")
  set(GEMSTONE_TOOL_CLINFO "${clinfo_ROOT}/usr/bin/clinfo")
  set(GEMSTONE_TOOL_VULKANINFO "${vulkaninfo_ROOT}/usr/bin/vulkaninfo")
  set(GEMSTONE_TOOL_SYCL_LS "${GEMSTONE_SYCL_LS}")
  set(GEMSTONE_TOOL_NVIDIA_SMI "${nvidia-smi_ROOT}/usr/bin/nvidia-smi")
  set(GEMSTONE_TOOL_NVTOP "${nvtop_ROOT}/usr/bin/nvtop")
  set(GEMSTONE_TOOL_ROCM_SMI "${rocm-smi_ROOT}/opt/rocm-7.1.1/bin/rocm-smi")
  set(GEMSTONE_TOOL_AMD_SMI "${amd-smi_ROOT}/opt/rocm-7.1.1/bin/amd-smi")

  set(GEMSTONE_TOOL_LIBRARY_DIRECTORIES
    "${CMAKE_BINARY_DIR}"
    "${CMAKE_BINARY_DIR}/vulkan"
    "${GEMSTONE_TOOL_DIRECTORY}/lib")
  if(GEMSTONE_SYCL_LIBRARY_DIRECTORY)
    string(REPLACE ":" ";" _gemstone_sycl_libs "${GEMSTONE_SYCL_LIBRARY_DIRECTORY}")
    list(APPEND GEMSTONE_TOOL_LIBRARY_DIRECTORIES ${_gemstone_sycl_libs})
  endif()
  foreach(dependency IN ITEMS libx11-6 libxcb1 libxau6 libxdmcp6 libwayland-client0 libffi8 libbsd0 libmd0 libdrm2
      libdrm-amdgpu1 libelf1t64 libnuma1)
    list(APPEND GEMSTONE_TOOL_LIBRARY_DIRECTORIES "${${dependency}_ROOT}/usr/lib/x86_64-linux-gnu")
  endforeach()
  list(FILTER GEMSTONE_TOOL_LIBRARY_DIRECTORIES EXCLUDE REGEX "^$")
  list(JOIN GEMSTONE_TOOL_LIBRARY_DIRECTORIES ":" GEMSTONE_TOOL_LIBRARY_PATH)

  set(GEMSTONE_OPENCL_LIBRARY "${CMAKE_BINARY_DIR}/libOpenCL_gemstone.so.1")
  set(GEMSTONE_LEVEL_ZERO_LIBRARY "${CMAKE_BINARY_DIR}/libze_gemstone.so.1")
endif()
