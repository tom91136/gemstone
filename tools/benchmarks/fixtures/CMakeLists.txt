cmake_minimum_required(VERSION 3.25)
project(gemstone_benchmark_fixtures LANGUAGES NONE)

if(NOT CMAKE_GENERATOR MATCHES "^Ninja")
  message(FATAL_ERROR "Benchmark fixture generation requires Ninja")
endif()

get_filename_component(REPOSITORY_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/../../.." ABSOLUTE)
set(CONFORMANCE_ARTIFACT_PROJECT "${REPOSITORY_DIRECTORY}/conformance/artifacts" CACHE PATH
  "Independent conformance artifact build entry point")
set(CONTAINER_EXECUTABLE podman CACHE STRING "OCI container executable")
if(NOT ROCM_VERSION)
  message(FATAL_ERROR "ROCM_VERSION is required; pass -DROCM_VERSION=<major.minor.patch>")
endif()
set(BENCHMARK_FIXTURE_OUTPUT_DIRECTORY
  "${REPOSITORY_DIRECTORY}/tools/benchmarks/target/generated-fixtures" CACHE PATH
  "Generated benchmark fixture directory")
set_property(GLOBAL PROPERTY JOB_POOLS "container_build=1")
file(MAKE_DIRECTORY "${BENCHMARK_FIXTURE_OUTPUT_DIRECTORY}")

function(add_benchmark_fixture name toolchain architecture source filename)
  set(build "${CMAKE_BINARY_DIR}/artifact-builds/${name}")
  set(artifact_directory "${BENCHMARK_FIXTURE_OUTPUT_DIRECTORY}")
  if(name STREQUAL "hsa")
    set(artifact_directory "${build}/output")
    set(arguments -DCONFORMANCE_ARTIFACT_KIND=fixture -DCONFORMANCE_FIXTURE=vector-copy)
    set(copy COMMAND "${CMAKE_COMMAND}" -E copy_if_different
      "${artifact_directory}/vector-copy.hsaco" "${BENCHMARK_FIXTURE_OUTPUT_DIRECTORY}/${filename}")
  else()
    set(arguments -DCONFORMANCE_ARTIFACT_KIND=command
      "-DCONFORMANCE_COMMAND_SOURCE=${REPOSITORY_DIRECTORY}"
      "-DCONFORMANCE_COMMAND_SCRIPT=${CMAKE_CURRENT_SOURCE_DIR}/Generate.cmake"
      "-DCONFORMANCE_COMMAND_ARGUMENTS=-DFIXTURE=${name}\;-DARCHITECTURE=${architecture}"
      "-DCONFORMANCE_COMMAND_INPUTS=tools/benchmarks/fixtures/${source}"
      "-DCONFORMANCE_COMMAND_OUTPUTS=${filename}")
    set(copy)
  endif()
  add_custom_target(${name}
    COMMAND "${CMAKE_COMMAND}" -S "${CONFORMANCE_ARTIFACT_PROJECT}" -B "${build}" -G Ninja --log-level=WARNING
      ${arguments} "-DCONFORMANCE_BACKEND=${toolchain}" "-DCONFORMANCE_ARCHITECTURE=${architecture}"
      "-DCONFORMANCE_ARTIFACT_DIRECTORY=${artifact_directory}"
      "-DCONTAINER_EXECUTABLE=${CONTAINER_EXECUTABLE}" "-DROCM_VERSION=${ROCM_VERSION}"
    COMMAND "${CMAKE_COMMAND}" --build "${build}" --target artifacts --parallel 1 -- --quiet
    ${copy}
    BYPRODUCTS "${BENCHMARK_FIXTURE_OUTPUT_DIRECTORY}/${filename}"
    JOB_POOL container_build VERBATIM)
endfunction()

add_benchmark_fixture(cuda cuda sm_86 cloverleaf-flux.cu cloverleaf-flux.ptx)
add_benchmark_fixture(hip hip gfx1030 cloverleaf-flux.cu cloverleaf-flux.hsaco)
add_benchmark_fixture(hsa hsa gfx1030 "" hsa-vector-copy.hsaco)
add_benchmark_fixture(opencl sycl "" opencl-cts-async-copy.cl opencl-cts-async-copy.spv)
add_custom_target(fixtures ALL DEPENDS cuda hip hsa opencl)
