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

include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ToolchainImages.cmake")

foreach(variable IN ITEMS CONFORMANCE_BACKENDS CUDA_ARCHITECTURES AMDGPU_ARCHITECTURES)
  string(REPLACE "," ";" "${variable}" "${${variable}}")
  list(REMOVE_DUPLICATES "${variable}")
endforeach()

set(supported_backends cuda hip hsa opencl sycl levelzero openacc)
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

# Container steps run concurrently with equal CPU shares, so a lone artifact
# uses the whole machine while several share it (elastic scheduling). Bound the
# value by memory, not cores: each toolchain build can need several GiB.
set(CONFORMANCE_ARTIFACT_PARALLELISM 1 CACHE STRING
  "Number of artifact container builds to run concurrently")
set_property(GLOBAL PROPERTY JOB_POOLS "container_build=${CONFORMANCE_ARTIFACT_PARALLELISM}")
set(artifact_outputs)

# Shared, toolchain-independent source cache for every fetched dependency.
set(CONFORMANCE_DEPENDENCY_DIRECTORY "${CMAKE_BINARY_DIR}/deps" CACHE PATH
  "Shared source cache for conformance dependencies")
option(CONFORMANCE_OFFLINE "Require cached dependencies and disable network fetches" OFF)
# Persistent per-artifact build trees; empty disables reuse.
set(CONFORMANCE_WORK_DIRECTORY "${CMAKE_BINARY_DIR}/work" CACHE PATH
  "Persistent per-artifact build directory")
option(CONFORMANCE_CLEAN_WORK "Discard persistent artifact build trees first" OFF)

include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/DependencyCache.cmake")
include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/Dependencies.cmake")
include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ContainerBuild.cmake")

set(conformance_dependency_stamp "${CONFORMANCE_DEPENDENCY_DIRECTORY}/.conformance-populated")
if(CONFORMANCE_OFFLINE)
  conformance_dependency_cache_ready("${CONFORMANCE_DEPENDENCY_SOURCES}" "${CONFORMANCE_DEPENDENCY_DIRECTORY}"
    conformance_dependencies_ready)
  if(NOT conformance_dependencies_ready)
    message(FATAL_ERROR
      "CONFORMANCE_OFFLINE is set but the dependency cache is incomplete: ${CONFORMANCE_DEPENDENCY_DIRECTORY}")
  endif()
else()
  add_custom_command(
    OUTPUT "${conformance_dependency_stamp}"
    COMMAND "${CMAKE_COMMAND}" -E make_directory "${CONFORMANCE_DEPENDENCY_DIRECTORY}"
    COMMAND "${CMAKE_COMMAND}" "-DCONFORMANCE_DEPENDENCY_CACHE=${CONFORMANCE_DEPENDENCY_DIRECTORY}"
      -P "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/PopulateDependencies.cmake"
    COMMAND "${CMAKE_COMMAND}" -E touch "${conformance_dependency_stamp}"
    DEPENDS "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/Dependencies.cmake"
      "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/PopulateDependencies.cmake"
    COMMENT "Populating conformance dependency cache"
    VERBATIM)
  add_custom_target(conformance-dependencies DEPENDS "${conformance_dependency_stamp}")
endif()
if(CONFORMANCE_CLEAN_WORK AND EXISTS "${CONFORMANCE_WORK_DIRECTORY}")
  file(REMOVE_RECURSE "${CONFORMANCE_WORK_DIRECTORY}")
endif()

execute_process(COMMAND id -u OUTPUT_VARIABLE container_uid OUTPUT_STRIP_TRAILING_WHITESPACE)
execute_process(COMMAND id -g OUTPUT_VARIABLE container_gid OUTPUT_STRIP_TRAILING_WHITESPACE)
if(CONTAINER_EXECUTABLE MATCHES "(^|/)podman$")
  set(container_identity "--userns keep-id --user ${container_uid}:${container_gid}")
else()
  set(container_identity "--user ${container_uid}:${container_gid}")
endif()

function(artifact_arguments backend architecture output_mode result)
  set(arguments
    "-DBACKEND=${backend}"
    "-DCONFORMANCE_OUTPUT=${output_mode}"
    "-DROCM_VERSION=${ROCM_VERSION}")
  if(backend STREQUAL "cuda")
    conformance_cuda_toolchain("${architecture}" unused_image unused_package support)
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

function(add_artifact backend architecture output)
  if(NOT output MATCHES "^(both|overlay|artifact)$")
    message(FATAL_ERROR "Invalid artifact output mode: ${output}")
  endif()
  set(name "${backend}")
  if(architecture)
    set(name "${backend}-${architecture}")
  endif()
  set(directory "${CONFORMANCE_ARTIFACT_DIRECTORY}/${name}")
  set(metadata "${directory}/artifact.toml")
  conformance_toolchain("${backend}" "${architecture}" image toolchain_stamp support)
  artifact_arguments("${backend}" "${architecture}" "${output}" arguments)

  list(APPEND arguments
    "-DCONFORMANCE_DEPENDENCY_CACHE=/conformance-deps"
    "-DCONFORMANCE_OFFLINE=ON"
    "-DCONFORMANCE_WORK_DIRECTORY=/work")
  set(work_directory "${CONFORMANCE_WORK_DIRECTORY}/${name}")

  set(dependencies ${artifact_inputs} ${artifact_scripts} "${toolchain_stamp}")
  if(NOT CONFORMANCE_OFFLINE)
    list(APPEND dependencies "${conformance_dependency_stamp}")
  endif()
  set(outputs "${metadata}")
  if(architecture)
    set(support_executable "${CONFORMANCE_ARTIFACT_DIRECTORY}/${backend}/${support}/support.stamp")
    if(output STREQUAL "both")
      list(APPEND outputs "${support_executable}")
    else()
      list(APPEND dependencies "${support_executable}")
    endif()
  endif()

  conformance_container_command(container_command
    EXECUTABLE "${CONTAINER_EXECUTABLE}"
    IMAGE "${image}"
    SOURCE "${CONFORMANCE_SOURCE_DIRECTORY}"
    ARTIFACTS "${CONFORMANCE_ARTIFACT_DIRECTORY}"
    DEPENDENCIES "${CONFORMANCE_DEPENDENCY_DIRECTORY}"
    WORK "${work_directory}"
    NETWORK none
    CPU_SHARES 1024
    IDENTITY "${container_identity}"
    COMMAND cmake ${arguments} -P /source/conformance/artifacts/Assemble.cmake)

  add_custom_command(
    OUTPUT ${outputs}
    COMMAND "${CMAKE_COMMAND}" -E make_directory "${CONFORMANCE_ARTIFACT_DIRECTORY}" "${work_directory}"
    COMMAND ${container_command}
    DEPENDS ${dependencies}
    COMMENT "Building ${name} conformance artifact"
    JOB_POOL container_build
    VERBATIM)
  add_custom_target("${name}" DEPENDS "${metadata}")
  if(architecture AND output STREQUAL "both")
    string(REPLACE "." "-" support_target "${support}")
    add_custom_target("${backend}-${support_target}" DEPENDS "${support_executable}")
  endif()
  list(APPEND artifact_outputs ${outputs})
  set(artifact_outputs "${artifact_outputs}" PARENT_SCOPE)
endfunction()

foreach(backend IN LISTS CONFORMANCE_BACKENDS)
  if(backend STREQUAL "cuda")
    set(cuda_supports)
    foreach(architecture IN LISTS CUDA_ARCHITECTURES)
      if(NOT architecture MATCHES "^sm_[0-9]+$")
        message(FATAL_ERROR "Invalid CUDA architecture: ${architecture}")
      endif()
      conformance_cuda_toolchain("${architecture}" unused_image unused_package support)
      if(NOT support IN_LIST cuda_supports)
        add_artifact("${backend}" "${architecture}" both)
        list(APPEND cuda_supports "${support}")
      else()
        add_artifact("${backend}" "${architecture}" overlay)
      endif()
    endforeach()
  elseif(backend STREQUAL "openacc")
    list(GET CUDA_ARCHITECTURES 0 support_architecture)
    foreach(architecture IN LISTS CUDA_ARCHITECTURES)
      if(NOT architecture MATCHES "^sm_[0-9]+$")
        message(FATAL_ERROR "Invalid CUDA architecture: ${architecture}")
      endif()
      if("${architecture}" STREQUAL "${support_architecture}")
        add_artifact("${backend}" "${architecture}" both)
      else()
        add_artifact("${backend}" "${architecture}" overlay)
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
    foreach(architecture IN LISTS AMDGPU_ARCHITECTURES)
      if(NOT architecture MATCHES "^gfx[0-9a-f]+$")
        message(FATAL_ERROR "Invalid AMDGPU architecture: ${architecture}")
      endif()
      if("${architecture}" STREQUAL "${support_architecture}")
        add_artifact("${backend}" "${architecture}" both)
      else()
        add_artifact("${backend}" "${architecture}" overlay)
      endif()
    endforeach()
  else()
    add_artifact("${backend}" "" artifact)
  endif()
endforeach()

add_custom_target(artifacts ALL DEPENDS ${artifact_outputs})
