# Pinned container toolchains shared by conformance artifacts and benchmark fixtures.
include_guard(GLOBAL)

set(conformance_cuda_11_8_architectures sm_35 sm_50 sm_60 sm_70)
set(conformance_cuda_13_3_architectures sm_75 sm_80 sm_86 sm_87 sm_89 sm_90 sm_100 sm_103 sm_120)
set(conformance_cuda_11_8_image
  "docker.io/nvidia/cuda@sha256:94fd755736cb58979173d491504f0b573247b1745250249415b07fefc738e41f")
set(conformance_cuda_13_3_image
  "docker.io/nvidia/cuda@sha256:a535b42e387b5ea69f3be73e14e79eb979913b622bc61b21c8000830b4965ea4")

function(conformance_cuda_toolchain architecture image_output package_output support_output)
  if(architecture IN_LIST conformance_cuda_11_8_architectures)
    set(image "${conformance_cuda_11_8_image}")
    set(package "11-8")
    set(support "support-11.8")
  elseif(architecture IN_LIST conformance_cuda_13_3_architectures)
    set(image "${conformance_cuda_13_3_image}")
    set(package "13-3")
    set(support "support-13.3")
  else()
    message(FATAL_ERROR "No CUDA toolchain supports architecture ${architecture}")
  endif()
  set(${image_output} "${image}" PARENT_SCOPE)
  set(${package_output} "${package}" PARENT_SCOPE)
  set(${support_output} "${support}" PARENT_SCOPE)
endfunction()

# Callers define CONFORMANCE_SOURCE_DIRECTORY, CONTAINER_EXECUTABLE, ROCM_VERSION,
# and the container_build Ninja job pool before requesting a toolchain target.
function(conformance_toolchain backend architecture image_output dependency_output support_output)
  set(build_arguments)
  set(support "support")
  if(backend STREQUAL "cuda")
    conformance_cuda_toolchain("${architecture}" cuda_image cuda_package support)
    set(key "cuda-${cuda_package}")
    list(APPEND build_arguments
      --build-arg "CUDA_IMAGE=${cuda_image}"
      --build-arg "CUDA_PACKAGE_VERSION=${cuda_package}")
  else()
    set(key "${backend}")
    if(backend STREQUAL "hip" OR backend STREQUAL "hsa")
      list(APPEND build_arguments --build-arg "ROCM_VERSION=${ROCM_VERSION}")
    endif()
  endif()

  string(SHA256 toolchain_namespace "${CONFORMANCE_SOURCE_DIRECTORY}")
  string(SUBSTRING "${toolchain_namespace}" 0 12 toolchain_namespace)
  if(backend STREQUAL "cuda")
    set(tag "localhost/gemstone-conformance-${toolchain_namespace}-cuda:${cuda_package}")
  else()
    set(tag "localhost/gemstone-conformance-${toolchain_namespace}-${backend}:latest")
  endif()

  set(stamp_key "${key}")
  if(backend STREQUAL "hip" OR backend STREQUAL "hsa")
    set(stamp_key "${key}-${ROCM_VERSION}")
  endif()
  set(stamp "${CMAKE_BINARY_DIR}/toolchains/${stamp_key}")
  string(MAKE_C_IDENTIFIER "toolchain-${key}" target)
  if(NOT TARGET "${target}")
    add_custom_command(
      OUTPUT "${stamp}"
      COMMAND "${CMAKE_COMMAND}" -E make_directory "${CMAKE_BINARY_DIR}/toolchains"
      COMMAND "${CONTAINER_EXECUTABLE}" build
        --file "${CONFORMANCE_SOURCE_DIRECTORY}/Containerfile"
        --target "toolchain-${backend}"
        --tag "${tag}"
        ${build_arguments}
        "${CONFORMANCE_SOURCE_DIRECTORY}"
      COMMAND "${CMAKE_COMMAND}" -E touch "${stamp}"
      DEPENDS "${CONFORMANCE_SOURCE_DIRECTORY}/Containerfile"
      COMMENT "Preparing ${backend} conformance toolchain"
      JOB_POOL container_build
      VERBATIM)
    add_custom_target("${target}" DEPENDS "${stamp}")
  endif()

  set(${image_output} "${tag}" PARENT_SCOPE)
  set(${dependency_output} "${stamp}" PARENT_SCOPE)
  set(${support_output} "${support}" PARENT_SCOPE)
endfunction()
