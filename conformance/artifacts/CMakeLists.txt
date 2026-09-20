cmake_minimum_required(VERSION 3.25)

project(conformance_artifacts LANGUAGES NONE)

if(NOT CMAKE_GENERATOR MATCHES "^Ninja")
  message(FATAL_ERROR "The conformance artifact matrix requires Ninja to enforce serial container builds")
endif()

set(CONFORMANCE_TARGET_SET default CACHE STRING "Named conformance artifact target set")
set(CONTAINER_EXECUTABLE podman CACHE STRING "OCI container build executable")
set(ROCM_VERSION 7.1.1 CACHE STRING "ROCm toolchain version")
get_filename_component(CONFORMANCE_SOURCE_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
set(DEFAULT_ARTIFACT_DIRECTORY "${CMAKE_BINARY_DIR}/output")
set(CONFORMANCE_ARTIFACT_DIRECTORY "${DEFAULT_ARTIFACT_DIRECTORY}" CACHE PATH "Conformance artifact output directory")

if(NOT CONFORMANCE_TARGET_SET MATCHES "^[A-Za-z0-9_-]+$")
  message(FATAL_ERROR "Invalid CONFORMANCE_TARGET_SET: ${CONFORMANCE_TARGET_SET}")
endif()
if(NOT ROCM_VERSION MATCHES "^[0-9]+\\.[0-9]+\\.[0-9]+$")
  message(FATAL_ERROR "Invalid ROCm version: ${ROCM_VERSION}")
endif()
set(target_set "${CMAKE_CURRENT_LIST_DIR}/targets/${CONFORMANCE_TARGET_SET}.cmake")
if(NOT EXISTS "${target_set}")
  message(FATAL_ERROR "Unknown conformance target set: ${CONFORMANCE_TARGET_SET}")
endif()
include("${target_set}")

set(cuda_11_8_architectures sm_35 sm_50 sm_60 sm_70)
set(cuda_13_3_architectures sm_75 sm_80 sm_86 sm_87 sm_89 sm_90 sm_100 sm_103 sm_120)
set(cuda_11_8_image
  "docker.io/nvidia/cuda@sha256:94fd755736cb58979173d491504f0b573247b1745250249415b07fefc738e41f")
set(cuda_13_3_image
  "docker.io/nvidia/cuda@sha256:a535b42e387b5ea69f3be73e14e79eb979913b622bc61b21c8000830b4965ea4")

function(cuda_toolchain architecture image_output package_output support_output)
  if(architecture IN_LIST cuda_11_8_architectures)
    set(image "${cuda_11_8_image}")
    set(package "11-8")
    set(support "support-11.8")
  elseif(architecture IN_LIST cuda_13_3_architectures)
    set(image "${cuda_13_3_image}")
    set(package "13-3")
    set(support "support-13.3")
  else()
    message(FATAL_ERROR "No CUDA toolchain supports architecture ${architecture}")
  endif()
  set(${image_output} "${image}" PARENT_SCOPE)
  set(${package_output} "${package}" PARENT_SCOPE)
  set(${support_output} "${support}" PARENT_SCOPE)
endfunction()

foreach(variable IN ITEMS CONFORMANCE_BACKENDS CUDA_ARCHITECTURES AMDGPU_ARCHITECTURES)
  string(REPLACE "," ";" "${variable}" "${${variable}}")
  list(REMOVE_DUPLICATES "${variable}")
endforeach()

set(supported_backends cuda hip hsa opencl sycl openacc)
foreach(backend IN LISTS CONFORMANCE_BACKENDS)
  if(NOT backend IN_LIST supported_backends)
    message(FATAL_ERROR "Unsupported conformance backend: ${backend}")
  endif()
endforeach()
if("cuda" IN_LIST CONFORMANCE_BACKENDS OR "openacc" IN_LIST CONFORMANCE_BACKENDS)
  if(NOT CUDA_ARCHITECTURES)
    message(FATAL_ERROR "CUDA_ARCHITECTURES is required by the selected backends")
  endif()
endif()
if("hip" IN_LIST CONFORMANCE_BACKENDS OR "hsa" IN_LIST CONFORMANCE_BACKENDS)
  if(NOT AMDGPU_ARCHITECTURES)
    message(FATAL_ERROR "AMDGPU_ARCHITECTURES is required by the selected backends")
  endif()
endif()

file(GLOB_RECURSE artifact_inputs CONFIGURE_DEPENDS "${CONFORMANCE_SOURCE_DIRECTORY}/*")
list(FILTER artifact_inputs EXCLUDE REGEX "/(artifacts|build|profiles|target|tests|workloads)/")
list(FILTER artifact_inputs EXCLUDE REGEX "/README\\.md$")
set(artifact_scripts
  "${CMAKE_CURRENT_LIST_DIR}/Assemble.cmake"
  "${CMAKE_CURRENT_LIST_DIR}/Verify.cmake")

set_property(GLOBAL PROPERTY JOB_POOLS container_build=1)
set(artifact_outputs)

string(SHA256 toolchain_namespace "${CONFORMANCE_SOURCE_DIRECTORY}")
string(SUBSTRING "${toolchain_namespace}" 0 12 toolchain_namespace)

execute_process(COMMAND id -u OUTPUT_VARIABLE container_uid OUTPUT_STRIP_TRAILING_WHITESPACE)
execute_process(COMMAND id -g OUTPUT_VARIABLE container_gid OUTPUT_STRIP_TRAILING_WHITESPACE)
if(CONTAINER_EXECUTABLE MATCHES "(^|/)podman$")
  set(container_identity --userns keep-id --user "${container_uid}:${container_gid}")
else()
  set(container_identity --user "${container_uid}:${container_gid}")
endif()

function(toolchain backend architecture image_output dependency_output support_output)
  set(build_arguments)
  set(support "support")
  if(backend STREQUAL "cuda")
    cuda_toolchain("${architecture}" cuda_image cuda_package support)
    set(key "cuda-${cuda_package}")
    list(APPEND build_arguments
      --build-arg "CUDA_IMAGE=${cuda_image}"
      --build-arg "CUDA_PACKAGE_VERSION=${cuda_package}")
    set(tag "localhost/gemstone-conformance-${toolchain_namespace}-cuda:${cuda_package}")
  else()
    set(key "${backend}")
    set(tag "localhost/gemstone-conformance-${toolchain_namespace}-${backend}:latest")
    if(backend STREQUAL "hip" OR backend STREQUAL "hsa")
      list(APPEND build_arguments --build-arg "ROCM_VERSION=${ROCM_VERSION}")
    endif()
  endif()

  set(stamp "${CMAKE_BINARY_DIR}/toolchains/${key}")
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
      VERBATIM
    )
    add_custom_target("${target}" DEPENDS "${stamp}")
  endif()

  set(${image_output} "${tag}" PARENT_SCOPE)
  set(${dependency_output} "${stamp}" PARENT_SCOPE)
  set(${support_output} "${support}" PARENT_SCOPE)
endfunction()

function(artifact_arguments backend architecture output_mode result)
  set(arguments
    "-DBACKEND=${backend}"
    "-DCONFORMANCE_OUTPUT=${output_mode}"
    "-DROCM_VERSION=${ROCM_VERSION}")
  if(backend STREQUAL "cuda")
    cuda_toolchain("${architecture}" unused_image unused_package support)
    list(APPEND arguments
      "-DCUDA_ARCHITECTURES=${architecture}"
      "-DCONFORMANCE_SUPPORT_KEY=${support}")
  elseif(backend STREQUAL "openacc")
    list(APPEND arguments "-DCUDA_ARCHITECTURES=${architecture}")
  elseif(backend STREQUAL "hip" OR backend STREQUAL "hsa")
    list(APPEND arguments "-DAMDGPU_ARCHITECTURES=${architecture}")
    if(backend STREQUAL "hip" AND architecture STREQUAL "gfx803")
      list(APPEND arguments -DHIP_ENABLE_ROCBLAS=OFF)
    endif()
  endif()
  set(${result} "${arguments}" PARENT_SCOPE)
endfunction()

function(add_support_artifact backend architecture)
  toolchain("${backend}" "${architecture}" image toolchain_stamp support)
  artifact_arguments("${backend}" "${architecture}" both arguments)
  set(support_directory "${CONFORMANCE_ARTIFACT_DIRECTORY}/${backend}/${support}")
  set(overlay_directory "${CONFORMANCE_ARTIFACT_DIRECTORY}/${backend}/${architecture}")
  set(executable "${support_directory}/support.stamp")
  set(metadata "${overlay_directory}/artifact.toml")
  add_custom_command(
    OUTPUT "${executable}" "${metadata}"
    COMMAND "${CMAKE_COMMAND}" -E make_directory "${CONFORMANCE_ARTIFACT_DIRECTORY}"
    COMMAND "${CONTAINER_EXECUTABLE}" run --rm --security-opt label=disable ${container_identity}
      --mount "type=bind,source=${CONFORMANCE_SOURCE_DIRECTORY},target=/source/conformance,readonly"
      --mount "type=bind,source=${CONFORMANCE_ARTIFACT_DIRECTORY},target=/output"
      "${image}" cmake ${arguments}
      -P /source/conformance/artifacts/Assemble.cmake
    DEPENDS ${artifact_inputs} ${artifact_scripts} "${toolchain_stamp}"
    COMMENT "Building ${backend} ${architecture} conformance support and overlay"
    JOB_POOL container_build
    VERBATIM
  )
  string(REPLACE "." "-" support_target "${support}")
  add_custom_target("${backend}-${support_target}" DEPENDS "${executable}")
  add_custom_target("${backend}-${architecture}" DEPENDS "${metadata}")
  list(APPEND artifact_outputs "${executable}" "${metadata}")
  set(artifact_outputs "${artifact_outputs}" PARENT_SCOPE)
endfunction()

function(add_architecture_artifact backend architecture)
  set(name "${backend}-${architecture}")
  set(directory "${CONFORMANCE_ARTIFACT_DIRECTORY}/${backend}/${architecture}")
  set(metadata "${directory}/artifact.toml")
  toolchain("${backend}" "${architecture}" image toolchain_stamp support)
  artifact_arguments("${backend}" "${architecture}" overlay arguments)
  set(support_executable "${CONFORMANCE_ARTIFACT_DIRECTORY}/${backend}/${support}/support.stamp")

  add_custom_command(
    OUTPUT "${metadata}"
    COMMAND "${CMAKE_COMMAND}" -E make_directory "${CONFORMANCE_ARTIFACT_DIRECTORY}"
    COMMAND "${CONTAINER_EXECUTABLE}" run --rm --security-opt label=disable ${container_identity}
      --mount "type=bind,source=${CONFORMANCE_SOURCE_DIRECTORY},target=/source/conformance,readonly"
      --mount "type=bind,source=${CONFORMANCE_ARTIFACT_DIRECTORY},target=/output"
      "${image}" cmake ${arguments}
      -P /source/conformance/artifacts/Assemble.cmake
    DEPENDS ${artifact_inputs} ${artifact_scripts} "${toolchain_stamp}" "${support_executable}"
    COMMENT "Building ${backend} ${architecture} conformance overlay"
    JOB_POOL container_build
    VERBATIM
  )
  add_custom_target("${name}" DEPENDS "${metadata}")
  list(APPEND artifact_outputs "${metadata}")
  set(artifact_outputs "${artifact_outputs}" PARENT_SCOPE)
endfunction()

function(add_artifact backend)
  set(directory "${CONFORMANCE_ARTIFACT_DIRECTORY}/${backend}")
  set(metadata "${directory}/artifact.toml")
  toolchain("${backend}" "" image toolchain_stamp unused_support)
  artifact_arguments("${backend}" "" artifact arguments)
  add_custom_command(
    OUTPUT "${metadata}"
    COMMAND "${CMAKE_COMMAND}" -E make_directory "${CONFORMANCE_ARTIFACT_DIRECTORY}"
    COMMAND "${CONTAINER_EXECUTABLE}" run --rm --security-opt label=disable ${container_identity}
      --mount "type=bind,source=${CONFORMANCE_SOURCE_DIRECTORY},target=/source/conformance,readonly"
      --mount "type=bind,source=${CONFORMANCE_ARTIFACT_DIRECTORY},target=/output"
      "${image}" cmake ${arguments}
      -P /source/conformance/artifacts/Assemble.cmake
    DEPENDS ${artifact_inputs} ${artifact_scripts} "${toolchain_stamp}"
    COMMENT "Building ${backend} conformance artifact"
    JOB_POOL container_build
    VERBATIM
  )
  add_custom_target("${backend}" DEPENDS "${metadata}")
  list(APPEND artifact_outputs "${metadata}")
  set(artifact_outputs "${artifact_outputs}" PARENT_SCOPE)
endfunction()

foreach(backend IN LISTS CONFORMANCE_BACKENDS)
  if(backend STREQUAL "cuda")
    set(cuda_supports)
    foreach(architecture IN LISTS CUDA_ARCHITECTURES)
      if(NOT architecture MATCHES "^sm_[0-9]+$")
        message(FATAL_ERROR "Invalid CUDA architecture: ${architecture}")
      endif()
      cuda_toolchain("${architecture}" unused_image unused_package support)
      if(NOT support IN_LIST cuda_supports)
        add_support_artifact("${backend}" "${architecture}")
        list(APPEND cuda_supports "${support}")
      else()
        add_architecture_artifact("${backend}" "${architecture}")
      endif()
    endforeach()
  elseif(backend STREQUAL "openacc")
    list(GET CUDA_ARCHITECTURES 0 support_architecture)
    add_support_artifact("${backend}" "${support_architecture}")
    foreach(architecture IN LISTS CUDA_ARCHITECTURES)
      if(NOT architecture MATCHES "^sm_[0-9]+$")
        message(FATAL_ERROR "Invalid CUDA architecture: ${architecture}")
      endif()
      if(NOT "${architecture}" STREQUAL "${support_architecture}")
        add_architecture_artifact("${backend}" "${architecture}")
      endif()
    endforeach()
  elseif(backend STREQUAL "hip" OR backend STREQUAL "hsa")
    list(GET AMDGPU_ARCHITECTURES 0 support_architecture)
    if(backend STREQUAL "hip" AND "gfx908" IN_LIST AMDGPU_ARCHITECTURES)
      # gfx803 has no matching rocBLAS payload in current ROCm releases. Build
      # the shared superset on a supported target and keep gfx803's overlay
      # free of rocBLAS cases.
      set(support_architecture gfx908)
    endif()
    add_support_artifact("${backend}" "${support_architecture}")
    foreach(architecture IN LISTS AMDGPU_ARCHITECTURES)
      if(NOT architecture MATCHES "^gfx[0-9a-f]+$")
        message(FATAL_ERROR "Invalid AMDGPU architecture: ${architecture}")
      endif()
      if(NOT "${architecture}" STREQUAL "${support_architecture}")
        add_architecture_artifact("${backend}" "${architecture}")
      endif()
    endforeach()
  else()
    add_artifact("${backend}")
  endif()
endforeach()

add_custom_target(artifacts ALL DEPENDS ${artifact_outputs})
