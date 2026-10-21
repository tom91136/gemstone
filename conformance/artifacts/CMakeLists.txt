cmake_minimum_required(VERSION 3.25)

project(conformance_artifacts LANGUAGES NONE)

if(NOT CMAKE_GENERATOR MATCHES "^Ninja")
  message(FATAL_ERROR "The conformance artifact matrix requires Ninja job pools")
endif()

option(CONFORMANCE_CUDA_CUBIN_ONLY "Compile CUDA kernels without embedded PTX" OFF)
set(CONFORMANCE_TARGET_SET default CACHE STRING "Named conformance artifact target set")
set(CONTAINER_EXECUTABLE podman CACHE STRING "OCI container build executable")
if(NOT ROCM_VERSION)
  message(FATAL_ERROR "ROCM_VERSION is required; pass -DROCM_VERSION=<major.minor.patch>")
endif()
get_filename_component(CONFORMANCE_SOURCE_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
set(CONFORMANCE_ARTIFACT_DIRECTORY "${CMAKE_BINARY_DIR}/output" CACHE PATH "Conformance artifact output directory")

if(NOT ROCM_VERSION MATCHES "^[0-9]+\\.[0-9]+\\.[0-9]+$")
  message(FATAL_ERROR "Invalid ROCm version: ${ROCM_VERSION}")
endif()
include("${CMAKE_CURRENT_LIST_DIR}/Toolchains.cmake")
container_identity(identity "${CONTAINER_EXECUTABLE}")

set(CONFORMANCE_ARTIFACT_KIND suite CACHE STRING "Artifact kind: suite, fixture, or command")
set_property(CACHE CONFORMANCE_ARTIFACT_KIND PROPERTY STRINGS suite fixture command)
if(NOT CONFORMANCE_ARTIFACT_KIND MATCHES "^(suite|fixture|command)$")
  message(FATAL_ERROR "Unknown artifact kind: ${CONFORMANCE_ARTIFACT_KIND}")
endif()
if(NOT CONFORMANCE_ARTIFACT_KIND STREQUAL "suite")
  set(CONFORMANCE_BACKEND "" CACHE STRING "Compiler toolchain for the artifact")
  set(CONFORMANCE_ARCHITECTURE "" CACHE STRING "Target architecture, when required")
  set_property(GLOBAL PROPERTY JOB_POOLS "container_build=1")
  file(MAKE_DIRECTORY "${CONFORMANCE_ARTIFACT_DIRECTORY}")

  if(CONFORMANCE_ARTIFACT_KIND STREQUAL "fixture")
    set(CONFORMANCE_FIXTURE "" CACHE STRING "Fixture family: memory or vector-copy")
    if(CONFORMANCE_FIXTURE STREQUAL "memory" AND CONFORMANCE_BACKEND STREQUAL "cuda")
      foreach(name IN ITEMS kernel exchange atomic fence contended)
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
      "/source/conformance/${source_relative}" "/output/${filename}" compile TRANSLATE translate ${options})
    set(commands)
    foreach(step IN ITEMS compile translate)
      if(${step})
        container_command(command
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
        "${CMAKE_CURRENT_LIST_DIR}/Toolchains.cmake"
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
    container_command(command
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
  EXECUTABLE_PATTERNS ${executables} SEARCH_DIRECTORIES ${directories} PLUGINS ${plugins})
file(TOUCH "${destination}/.complete")
]=])
      container_command(bundle
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

include("${CMAKE_CURRENT_LIST_DIR}/Targets.cmake")

file(GLOB_RECURSE artifact_inputs CONFIGURE_DEPENDS
  RELATIVE "${CONFORMANCE_SOURCE_DIRECTORY}" "${CONFORMANCE_SOURCE_DIRECTORY}/*")
list(FILTER artifact_inputs EXCLUDE REGEX "(^|/)(artifacts|build|profiles|target|tests|workloads)/")
list(FILTER artifact_inputs EXCLUDE REGEX "(^|/)README\\.md$")
set(artifact_scripts
  "${CMAKE_CURRENT_LIST_DIR}/Assemble.cmake"
  "${CMAKE_CURRENT_LIST_DIR}/Toolchains.cmake"
  "${CMAKE_CURRENT_LIST_DIR}/Verify.cmake")

cmake_host_system_information(RESULT conformance_cores QUERY NUMBER_OF_LOGICAL_CORES)
math(EXPR conformance_parallelism "${conformance_cores} / 8")
if(conformance_parallelism LESS 1)
  set(conformance_parallelism 1)
elseif(conformance_parallelism GREATER 4)
  set(conformance_parallelism 4)
endif()
set(CONFORMANCE_ARTIFACT_PARALLELISM ${conformance_parallelism} CACHE STRING
  "Number of artifact container builds to run concurrently")
set_property(GLOBAL PROPERTY JOB_POOLS "container_build=${CONFORMANCE_ARTIFACT_PARALLELISM}")
set(artifact_outputs)

set(CONFORMANCE_DEPENDENCY_DIRECTORY "${CMAKE_BINARY_DIR}/deps" CACHE PATH
  "Shared source cache for conformance dependencies")
option(CONFORMANCE_OFFLINE "Require cached dependencies and disable network fetches" OFF)
set(CONFORMANCE_CCACHE_DIRECTORY "${CMAKE_BINARY_DIR}/ccache" CACHE PATH
  "Compiler cache shared by all artifact containers")
file(MAKE_DIRECTORY "${CONFORMANCE_CCACHE_DIRECTORY}")
set(CONFORMANCE_WORK_DIRECTORY "${CMAKE_BINARY_DIR}/work" CACHE PATH
  "Persistent per-artifact build directory")
option(CONFORMANCE_KEEP_WORK "Keep artifact build trees for incremental rebuilds" ON)
option(SYCL_CTS_ENABLE_FULL_CONFORMANCE "Build the complete upstream SYCL CTS type and operation matrices" OFF)
set(CONFORMANCE_SHARD_ROUTES "" CACHE STRING "Route selectors of a CI shard, whose own routes build its support closures")
set(CONFORMANCE_SYCL_SHARD "" CACHE STRING "Build only this zero-based shard of the SYCL CTS, into sycl-shard-<n>")
if(NOT CONFORMANCE_SYCL_SHARD MATCHES "^[0-9]*$")
  message(FATAL_ERROR "Invalid CONFORMANCE_SYCL_SHARD: ${CONFORMANCE_SYCL_SHARD}")
endif()
set(CONFORMANCE_LINT_CUDA_ARCHITECTURE sm_90a CACHE STRING "Device architecture the CUDA unified sources are linted for")
set(CONFORMANCE_LINT_AMDGPU_ARCHITECTURE gfx950 CACHE STRING "Device architecture the HIP unified sources are linted for")

include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/DependencyCache.cmake")
set(dependency_manifest "${CONFORMANCE_SOURCE_DIRECTORY}/dependencies.cmake")
set(conformance_dependency_stamp "${CONFORMANCE_DEPENDENCY_DIRECTORY}/.populated")
if(CONFORMANCE_OFFLINE)
  set(DEPENDENCY_CACHE "${CONFORMANCE_DEPENDENCY_DIRECTORY}")
  set(DEPENDENCY_CACHE_MODE verify)
  include("${dependency_manifest}")
else()
  add_custom_command(
    OUTPUT "${conformance_dependency_stamp}"
    COMMAND "${CMAKE_COMMAND}" "-DMANIFEST=${dependency_manifest}" "-DDEPENDENCY_CACHE=${CONFORMANCE_DEPENDENCY_DIRECTORY}"
      -P "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/DependencyCache.cmake"
    COMMAND "${CMAKE_COMMAND}" -E touch "${conformance_dependency_stamp}"
    DEPENDS "${dependency_manifest}" "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/DependencyCache.cmake"
    COMMENT "Populating conformance dependency cache"
    VERBATIM)
endif()

# GFX8 has no rocBLAS, so its programs need less of the HIP closure than the others do.
function(partial_closure backend architecture result)
  if(backend STREQUAL "hip" AND architecture STREQUAL "gfx803")
    set(${result} ON PARENT_SCOPE)
  else()
    set(${result} OFF PARENT_SCOPE)
  endif()
endfunction()

function(artifact_arguments backend architecture compiler output_mode result)
  set(arguments
    "-DBACKEND=${backend}"
    "-DCONFORMANCE_COMPILER=${compiler}"
    "-DCONFORMANCE_OUTPUT=${output_mode}"
    "-DROCM_VERSION=${ROCM_VERSION}"
    "-DCONFORMANCE_KEEP_WORK=${CONFORMANCE_KEEP_WORK}")
  if(backend STREQUAL "cuda")
    list(APPEND arguments
      "-DCUDA_ARCHITECTURES=${architecture}"
      "-DCONFORMANCE_CUDA_CUBIN_ONLY=${CONFORMANCE_CUDA_CUBIN_ONLY}")
  elseif(backend STREQUAL "openacc")
    list(APPEND arguments "-DCUDA_ARCHITECTURES=${architecture}"
      "-DCONFORMANCE_CUDA_CUBIN_ONLY=${CONFORMANCE_CUDA_CUBIN_ONLY}")
  elseif(backend STREQUAL "sycl")
    list(APPEND arguments "-DSYCL_CTS_ENABLE_FULL_CONFORMANCE=${SYCL_CTS_ENABLE_FULL_CONFORMANCE}")
    if(NOT CONFORMANCE_SYCL_SHARD STREQUAL "")
      list(APPEND arguments "-DSYCL_CTS_SHARD=${CONFORMANCE_SYCL_SHARD}")
    endif()
    # shard 0 carries the suites outside the CTS
    if(CONFORMANCE_SYCL_SHARD MATCHES "^[1-9]")
      list(APPEND arguments -DBUILD_UNIFIED=OFF -DBUILD_MINIAPPS=OFF)
    endif()
  elseif(backend STREQUAL "hip" OR backend STREQUAL "hsa")
    list(APPEND arguments "-DAMDGPU_ARCHITECTURES=${architecture}")
    partial_closure("${backend}" "${architecture}" partial)
    if(partial)
      list(APPEND arguments -DHIP_ENABLE_ROCBLAS=OFF)
    endif()
  endif()
  if(compiler STREQUAL "gcc")
    list(APPEND arguments -DBUILD_UNIFIED=OFF -DBUILD_CTS=OFF)
  endif()
  set(${result} "${arguments}" PARENT_SCOPE)
endfunction()

function(add_artifact backend architecture compiler output)
  if(NOT output MATCHES "^(both|overlay|artifact)$")
    message(FATAL_ERROR "Invalid artifact output mode: ${output}")
  endif()
  set(base "${backend}")
  if(NOT compiler STREQUAL "vendor")
    string(APPEND base "/${compiler}")
  endif()
  string(REPLACE "/" "-" name "${base}")
  if(architecture)
    string(APPEND name "-${architecture}")
  endif()
  set(directory "${CONFORMANCE_ARTIFACT_DIRECTORY}/${base}")
  if(architecture)
    string(APPEND directory "/${architecture}")
  elseif(backend STREQUAL "sycl" AND NOT CONFORMANCE_SYCL_SHARD STREQUAL "")
    string(APPEND directory "-shard-${CONFORMANCE_SYCL_SHARD}")
  endif()
  set(metadata "${directory}/artifact.toml")
  conformance_toolchain("${backend}" "${architecture}" image toolchain_stamp support "${compiler}")
  artifact_arguments("${backend}" "${architecture}" "${compiler}" "${output}" arguments)

  list(APPEND arguments
    "-DCONFORMANCE_SUPPORT_KEY=${support}"
    "-DCONFORMANCE_DEPENDENCY_CACHE=/conformance-deps"
    "-DCONFORMANCE_OFFLINE=ON"
    "-DCONFORMANCE_WORK_DIRECTORY=/work")
  set(work_directory "${CONFORMANCE_WORK_DIRECTORY}/${name}")

  # CTS directories and unified backend implementations belong only to their
  # backend. Everything else remains shared, including headers used across
  # backends and CMake files that select sources or package runtime libraries.
  set(dependencies ${artifact_scripts} "${toolchain_stamp}")
  foreach(input IN LISTS artifact_inputs)
    if(input MATCHES "^cts/([^/]+)/" AND NOT CMAKE_MATCH_1 STREQUAL backend)
      continue()
    endif()
    if(input MATCHES "^unified/")
      if(backend MATCHES "^(vulkan|openacc)$")
        continue()
      endif()
      if(input MATCHES "^unified/src/([^/]+)/")
        if(CMAKE_MATCH_1 IN_LIST supported_backends AND NOT CMAKE_MATCH_1 STREQUAL backend)
          continue()
        endif()
      endif()
    endif()
    if(input MATCHES "^miniapps/" AND backend MATCHES "^(hsa|levelzero)$" AND NOT compiler STREQUAL "gcc")
      continue()
    endif()
    list(APPEND dependencies "${CONFORMANCE_SOURCE_DIRECTORY}/${input}")
  endforeach()
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

  container_command(container_command
    EXECUTABLE "${CONTAINER_EXECUTABLE}"
    IMAGE "${image}"
    SOURCE "${CONFORMANCE_SOURCE_DIRECTORY}"
    ARTIFACTS "${CONFORMANCE_ARTIFACT_DIRECTORY}"
    DEPENDENCIES "${CONFORMANCE_DEPENDENCY_DIRECTORY}"
    CCACHE "${CONFORMANCE_CCACHE_DIRECTORY}"
    WORK "${work_directory}"
    NETWORK none
    CPU_SHARES 1024
    IDENTITY "${identity}"
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

function(route_fields route backend_output architecture_output compiler_output)
  string(REPLACE "/" ";" fields "${route}")
  list(GET fields 0 backend)
  list(LENGTH fields field_count)
  set(architecture "")
  set(compiler vendor)
  if(field_count GREATER 1)
    list(GET fields 1 architecture)
  endif()
  if(field_count GREATER 2)
    list(GET fields 2 compiler)
  endif()
  set(${backend_output} "${backend}" PARENT_SCOPE)
  set(${architecture_output} "${architecture}" PARENT_SCOPE)
  set(${compiler_output} "${compiler}" PARENT_SCOPE)
endfunction()

# A support closure is built by one route of its key and shared by the rest. The first route that can hold all of it
# builds it, so a CI shard naming its routes in CONFORMANCE_SHARD_ROUTES builds the closure from its own routes rather
# than rebuilding another shard's.
string(REPLACE " " ";" shard_selectors "${CONFORMANCE_SHARD_ROUTES}")
set(owned_supports)
set(support_owners)
foreach(rank RANGE 2)
  foreach(route IN LISTS conformance_routes)
    route_fields("${route}" backend architecture compiler)
    if(NOT architecture)
      continue()
    endif()
    partial_closure("${backend}" "${architecture}" partial)
    set(selected OFF)
    foreach(selector IN LISTS shard_selectors)
      if(route STREQUAL selector OR route MATCHES "^${selector}/")
        set(selected ON)
      endif()
    endforeach()
    if((rank EQUAL 0 AND (partial OR NOT selected)) OR (rank EQUAL 1 AND partial))
      continue()
    endif()
    conformance_toolchain("${backend}" "${architecture}" unused_image unused_stamp support "${compiler}")
    if(NOT "${backend}/${support}" IN_LIST owned_supports)
      list(APPEND owned_supports "${backend}/${support}")
      list(APPEND support_owners "${route}")
    endif()
  endforeach()
endforeach()

foreach(route IN LISTS conformance_routes)
  route_fields("${route}" backend architecture compiler)
  if(NOT architecture)
    set(output artifact)
  elseif(route IN_LIST support_owners)
    set(output both)
  else()
    set(output overlay)
  endif()
  add_artifact("${backend}" "${architecture}" "${compiler}" "${output}")
endforeach()

add_custom_target(artifacts ALL DEPENDS ${artifact_outputs})

# The repository root is mounted so clang-tidy finds the shared .clang-tidy above /source/conformance.
function(add_lint backend architecture compiler)
  conformance_toolchain("${backend}" "${architecture}" image toolchain_stamp support "${compiler}" LINT)
  artifact_arguments("${backend}" "${architecture}" "${compiler}" lint arguments)
  list(APPEND arguments
    -DBUILD_CTS=OFF
    -DBUILD_MINIAPPS=OFF
    "-DCLANG_TIDY_VERSION=${conformance_clang_version}"
    "-DCONFORMANCE_SUPPORT_KEY=${support}"
    "-DCONFORMANCE_DEPENDENCY_CACHE=/conformance-deps"
    "-DCONFORMANCE_OFFLINE=ON"
    "-DCONFORMANCE_WORK_DIRECTORY=/work")
  set(name "lint-${backend}")
  if(NOT compiler STREQUAL "vendor")
    string(APPEND name "-${compiler}")
  endif()
  if(architecture)
    string(APPEND name "-${architecture}")
  endif()
  set(work_directory "${CONFORMANCE_WORK_DIRECTORY}/${name}")
  set(dependencies "${toolchain_stamp}")
  if(NOT CONFORMANCE_OFFLINE)
    list(APPEND dependencies "${conformance_dependency_stamp}")
  endif()
  get_filename_component(repository "${CONFORMANCE_SOURCE_DIRECTORY}/.." ABSOLUTE)
  container_command(container_command
    EXECUTABLE "${CONTAINER_EXECUTABLE}"
    IMAGE "${image}"
    SOURCE "${repository}"
    SOURCE_TARGET /source
    DEPENDENCIES "${CONFORMANCE_DEPENDENCY_DIRECTORY}"
    WORK "${work_directory}"
    NETWORK none
    IDENTITY "${identity}"
    COMMAND cmake ${arguments} -P /source/conformance/artifacts/Assemble.cmake)
  add_custom_target("${name}"
    COMMAND "${CMAKE_COMMAND}" -E make_directory "${work_directory}"
    COMMAND ${container_command}
    DEPENDS ${dependencies}
    COMMENT "Linting ${backend} unified sources"
    JOB_POOL container_build
    VERBATIM)
  set_property(GLOBAL APPEND PROPERTY CONFORMANCE_LINT_TARGETS "${name}")
endfunction()

add_lint(cuda "${CONFORMANCE_LINT_CUDA_ARCHITECTURE}" clang)
add_lint(hip "${CONFORMANCE_LINT_AMDGPU_ARCHITECTURE}" vendor)
add_lint(hsa "${CONFORMANCE_LINT_AMDGPU_ARCHITECTURE}" vendor)
add_lint(opencl "" vendor)
add_lint(levelzero "" vendor)
add_lint(sycl "" vendor)
get_property(lint_targets GLOBAL PROPERTY CONFORMANCE_LINT_TARGETS)
add_custom_target(lint DEPENDS ${lint_targets})

container_prune_target(prune-images EXECUTABLE "${CONTAINER_EXECUTABLE}")
