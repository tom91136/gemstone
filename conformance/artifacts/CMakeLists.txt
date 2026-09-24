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

if(NOT ROCM_VERSION MATCHES "^[0-9]+\\.[0-9]+\\.[0-9]+$")
  message(FATAL_ERROR "Invalid ROCm version: ${ROCM_VERSION}")
endif()

# Public artifact entry points. Consumers invoke this project rather than
# importing its CMake helpers or reaching into suite source directories.
set(CONFORMANCE_ARTIFACT_KIND suite CACHE STRING "Artifact kind: suite, fixture, or command")
set_property(CACHE CONFORMANCE_ARTIFACT_KIND PROPERTY STRINGS suite fixture command)
if(NOT CONFORMANCE_ARTIFACT_KIND MATCHES "^(suite|fixture|command)$")
  message(FATAL_ERROR "Unknown artifact kind: ${CONFORMANCE_ARTIFACT_KIND}")
endif()
if(NOT CONFORMANCE_ARTIFACT_KIND STREQUAL "suite")
  set(CONFORMANCE_BACKEND "" CACHE STRING "Compiler toolchain for the artifact")
  set(CONFORMANCE_ARCHITECTURE "" CACHE STRING "Target architecture, when required")
  set_property(GLOBAL PROPERTY JOB_POOLS "container_build=1")
  include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ToolchainImages.cmake")
  include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ContainerBuild.cmake")
  file(MAKE_DIRECTORY "${CONFORMANCE_ARTIFACT_DIRECTORY}")
  execute_process(COMMAND id -u OUTPUT_VARIABLE uid OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)
  execute_process(COMMAND id -g OUTPUT_VARIABLE gid OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)
  if(CONTAINER_EXECUTABLE MATCHES "(^|/)podman$")
    set(identity "--userns keep-id --user ${uid}:${gid}")
  else()
    set(identity "--user ${uid}:${gid}")
  endif()

  if(CONFORMANCE_ARTIFACT_KIND STREQUAL "fixture")
    set(CONFORMANCE_FIXTURE "" CACHE STRING "Fixture family: memory or vector-copy")
    if(CONFORMANCE_FIXTURE STREQUAL "memory" AND CONFORMANCE_BACKEND STREQUAL "cuda")
      foreach(name IN ITEMS kernel atomic fence contended)
        set(output "${CONFORMANCE_ARTIFACT_DIRECTORY}/${name}.ptx")
        add_custom_command(OUTPUT "${output}"
          COMMAND "${CMAKE_COMMAND}" -E copy_if_different
            "${CONFORMANCE_SOURCE_DIRECTORY}/unified/src/cuda/${name}.ptx" "${output}"
          DEPENDS "${CONFORMANCE_SOURCE_DIRECTORY}/unified/src/cuda/${name}.ptx" VERBATIM)
        list(APPEND outputs "${output}")
      endforeach()
      add_custom_target(artifacts ALL DEPENDS ${outputs})
      return()
    endif()
    if(NOT CONFORMANCE_BACKEND MATCHES "^(hsa|opencl)$")
      message(FATAL_ERROR "Compiled fixtures require the hsa or opencl backend")
    endif()
    if(CONFORMANCE_FIXTURE STREQUAL "memory")
      set(source "${CONFORMANCE_SOURCE_DIRECTORY}/unified/src/memory_kernel.cl")
      set(filename kernel)
      set(options -O1)
      if(CONFORMANCE_BACKEND STREQUAL "hsa")
        # Preserve individual work-item atomics in the fixture.
        list(APPEND options -nogpulib -mllvm -amdgpu-atomic-optimizer-strategy=None)
      endif()
    elseif(CONFORMANCE_FIXTURE STREQUAL "vector-copy" AND CONFORMANCE_BACKEND STREQUAL "hsa")
      set(source "${CONFORMANCE_SOURCE_DIRECTORY}/unified/src/hsa/vector_copy.cl")
      set(filename vector-copy)
      set(options)
    else()
      message(FATAL_ERROR "Unsupported fixture/backend pair: ${CONFORMANCE_FIXTURE}/${CONFORMANCE_BACKEND}")
    endif()
    if(CONFORMANCE_BACKEND STREQUAL "hsa")
      string(APPEND filename .hsaco)
    else()
      string(APPEND filename .spv)
    endif()
    conformance_toolchain("${CONFORMANCE_BACKEND}" "${CONFORMANCE_ARCHITECTURE}" image toolchain_stamp unused_support)
    file(RELATIVE_PATH source_relative "${CONFORMANCE_SOURCE_DIRECTORY}" "${source}")
    conformance_opencl_commands("${CONFORMANCE_BACKEND}" "${CONFORMANCE_ARCHITECTURE}"
      "/source/conformance/${source_relative}" "/output/${filename}" compile translate ${options})
    set(commands)
    foreach(step IN ITEMS compile translate)
      if(${step})
        conformance_container_command(command
          EXECUTABLE "${CONTAINER_EXECUTABLE}" IMAGE "${image}"
          SOURCE "${CONFORMANCE_SOURCE_DIRECTORY}" ARTIFACTS "${CONFORMANCE_ARTIFACT_DIRECTORY}"
          NETWORK none IDENTITY "${identity}" COMMAND ${${step}})
        list(APPEND commands COMMAND ${command})
      endif()
    endforeach()
    set(byproducts)
    if(translate)
      list(APPEND byproducts "${CONFORMANCE_ARTIFACT_DIRECTORY}/${filename}.bc")
    endif()
    add_custom_command(OUTPUT "${CONFORMANCE_ARTIFACT_DIRECTORY}/${filename}"
      ${commands} BYPRODUCTS ${byproducts}
      DEPENDS "${source}" "${toolchain_stamp}" "${CMAKE_CURRENT_LIST_FILE}"
        "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ToolchainImages.cmake"
        "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ContainerBuild.cmake"
      JOB_POOL container_build VERBATIM)
    add_custom_target(artifacts ALL DEPENDS "${CONFORMANCE_ARTIFACT_DIRECTORY}/${filename}")
  else()
    # The supplied recipe owns compilation. It runs with read-only input at
    # /source and writable artifacts at /output, using the pinned toolchain PATH.
    foreach(required IN ITEMS CONFORMANCE_COMMAND_SOURCE CONFORMANCE_COMMAND_SCRIPT CONFORMANCE_COMMAND_OUTPUTS)
      if(NOT DEFINED ${required} OR "${${required}}" STREQUAL "")
        message(FATAL_ERROR "${required} is required for command artifacts")
      endif()
    endforeach()
    if(NOT CONFORMANCE_BACKEND MATCHES "^(cuda|hip|hsa|opencl|sycl|openacc)$")
      message(FATAL_ERROR "Unsupported command toolchain: ${CONFORMANCE_BACKEND}")
    endif()
    conformance_toolchain("${CONFORMANCE_BACKEND}" "${CONFORMANCE_ARCHITECTURE}" image toolchain_stamp unused_support)
    configure_file("${CONFORMANCE_COMMAND_SCRIPT}" "${CMAKE_BINARY_DIR}/command.cmake" COPYONLY)
    set(outputs)
    foreach(output IN LISTS CONFORMANCE_COMMAND_OUTPUTS)
      if(IS_ABSOLUTE "${output}" OR output MATCHES "(^|/)\\.\\.(/|$)")
        message(FATAL_ERROR "Command outputs must be relative to the artifact directory")
      endif()
      list(APPEND outputs "${CONFORMANCE_ARTIFACT_DIRECTORY}/${output}")
    endforeach()
    set(inputs)
    foreach(input IN LISTS CONFORMANCE_COMMAND_INPUTS)
      list(APPEND inputs "${CONFORMANCE_COMMAND_SOURCE}/${input}")
    endforeach()
    conformance_container_command(command
      EXECUTABLE "${CONTAINER_EXECUTABLE}" IMAGE "${image}"
      SOURCE "${CONFORMANCE_COMMAND_SOURCE}" SOURCE_TARGET /source
      ARTIFACTS "${CONFORMANCE_ARTIFACT_DIRECTORY}" WORK "${CMAKE_BINARY_DIR}"
      NETWORK none IDENTITY "${identity}"
      COMMAND cmake ${CONFORMANCE_COMMAND_ARGUMENTS} -P /work/command.cmake)
    set(commands COMMAND ${command})
    set(bundle_inputs)
    if(CONFORMANCE_RUNTIME_DIRECTORY)
      if(NOT CONFORMANCE_BACKEND STREQUAL "sycl" OR IS_ABSOLUTE "${CONFORMANCE_RUNTIME_DIRECTORY}"
          OR CONFORMANCE_RUNTIME_DIRECTORY MATCHES "(^|/)\\.\\.(/|$)")
        message(FATAL_ERROR "Runtime bundling requires sycl and an artifact-relative runtime directory")
      endif()
      configure_file("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/RuntimeDependencies.cmake"
        "${CMAKE_BINARY_DIR}/RuntimeDependencies.cmake" COPYONLY)
      set(executables)
      foreach(output IN LISTS CONFORMANCE_COMMAND_OUTPUTS)
        list(APPEND executables "/output/${output}")
      endforeach()
      file(WRITE "${CMAKE_BINARY_DIR}/bundle.cmake"
        "include(/work/RuntimeDependencies.cmake)\n"
        "set(executables [==[${executables}]==])\n"
        "set(destination [==[/output/${CONFORMANCE_RUNTIME_DIRECTORY}]==])\n")
      file(APPEND "${CMAKE_BINARY_DIR}/bundle.cmake" [=[
file(GLOB directories /opt/intel/oneapi/compiler/*/lib /opt/intel/oneapi/umf/*/lib)
file(GLOB plugins /opt/intel/oneapi/compiler/*/lib/libur_adapter*.so*
  /opt/intel/oneapi/compiler/*/lib/libur_loader.so*)
bundle_runtime_dependencies(DESTINATION "${destination}"
  EXECUTABLE_PATTERNS ${executables} SEARCH_DIRECTORIES ${directories} PLUGINS ${plugins}
  EXCLUDE_REGEXES "ld-linux.*" "libc\\.so.*" "libm\\.so.*" "libpthread\\.so.*"
    "libdl\\.so.*" "librt\\.so.*" "libgcc_s\\.so.*" "libstdc\\+\\+\\.so.*")
file(TOUCH "${destination}/.complete")
]=])
      conformance_container_command(bundle
        EXECUTABLE "${CONTAINER_EXECUTABLE}" IMAGE "${image}"
        ARTIFACTS "${CONFORMANCE_ARTIFACT_DIRECTORY}" WORK "${CMAKE_BINARY_DIR}"
        NETWORK none IDENTITY "${identity}" COMMAND cmake -P /work/bundle.cmake)
      list(APPEND commands COMMAND ${bundle})
      list(APPEND outputs "${CONFORMANCE_ARTIFACT_DIRECTORY}/${CONFORMANCE_RUNTIME_DIRECTORY}/.complete")
      list(APPEND bundle_inputs "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/RuntimeDependencies.cmake")
    endif()
    add_custom_command(OUTPUT ${outputs}
      ${commands}
      DEPENDS ${inputs} ${bundle_inputs} "${CONFORMANCE_COMMAND_SCRIPT}" "${toolchain_stamp}"
        "${CMAKE_CURRENT_LIST_FILE}" "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ContainerBuild.cmake"
      JOB_POOL container_build VERBATIM)
    add_custom_target(artifacts ALL DEPENDS ${outputs})
  endif()
  return()
endif()

if(NOT CONFORMANCE_TARGET_SET MATCHES "^[A-Za-z0-9_-]+$")
  message(FATAL_ERROR "Invalid CONFORMANCE_TARGET_SET: ${CONFORMANCE_TARGET_SET}")
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
