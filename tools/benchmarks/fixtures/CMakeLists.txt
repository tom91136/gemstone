cmake_minimum_required(VERSION 3.25)

project(gemstone_benchmark_fixtures LANGUAGES NONE)

if(NOT CMAKE_GENERATOR MATCHES "^Ninja")
  message(FATAL_ERROR "Benchmark fixture generation requires Ninja")
endif()

get_filename_component(REPOSITORY_DIRECTORY "${CMAKE_CURRENT_LIST_DIR}/../../.." ABSOLUTE)
set(CONFORMANCE_SOURCE_DIRECTORY "${REPOSITORY_DIRECTORY}/conformance")
set(CONTAINER_EXECUTABLE podman CACHE STRING "OCI container executable")
set(ROCM_VERSION 7.1.1 CACHE STRING "ROCm toolchain version")
set(BENCHMARK_FIXTURE_OUTPUT_DIRECTORY
  "${REPOSITORY_DIRECTORY}/tools/benchmarks/target/generated-fixtures" CACHE PATH
  "Generated benchmark fixture directory")
if(NOT ROCM_VERSION MATCHES "^[0-9]+\\.[0-9]+\\.[0-9]+$")
  message(FATAL_ERROR "ROCM_VERSION must be major.minor.patch")
endif()

# Toolchain image builds can need several GiB. Keep them serial like the
# conformance artifact matrix, even when Ninja itself has more workers.
set_property(GLOBAL PROPERTY JOB_POOLS "container_build=1")
include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ContainerBuild.cmake")
include("${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ToolchainImages.cmake")

execute_process(COMMAND id -u OUTPUT_VARIABLE container_uid OUTPUT_STRIP_TRAILING_WHITESPACE)
execute_process(COMMAND id -g OUTPUT_VARIABLE container_gid OUTPUT_STRIP_TRAILING_WHITESPACE)
if(CONTAINER_EXECUTABLE MATCHES "(^|/)podman$")
  set(container_identity "--userns keep-id --user ${container_uid}:${container_gid}")
else()
  set(container_identity "--user ${container_uid}:${container_gid}")
endif()

file(MAKE_DIRECTORY "${BENCHMARK_FIXTURE_OUTPUT_DIRECTORY}")

function(add_benchmark_fixture name toolchain architecture source filename)
  conformance_toolchain("${toolchain}" "${architecture}" image toolchain_stamp unused_support)
  set(output "${BENCHMARK_FIXTURE_OUTPUT_DIRECTORY}/${filename}")
  conformance_container_command(container_command
    EXECUTABLE "${CONTAINER_EXECUTABLE}"
    IMAGE "${image}"
    SOURCE "${REPOSITORY_DIRECTORY}"
    SOURCE_TARGET /source
    ARTIFACTS "${BENCHMARK_FIXTURE_OUTPUT_DIRECTORY}"
    NETWORK none
    IDENTITY "${container_identity}"
    COMMAND cmake "-DFIXTURE=${name}" -P /source/tools/benchmarks/fixtures/Generate.cmake)
  add_custom_command(
    OUTPUT "${output}"
    COMMAND ${container_command}
    DEPENDS "${source}" "${CMAKE_CURRENT_LIST_DIR}/Generate.cmake"
      "${CMAKE_CURRENT_LIST_FILE}" "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ContainerBuild.cmake"
      "${CONFORMANCE_SOURCE_DIRECTORY}/cmake/ToolchainImages.cmake" "${toolchain_stamp}"
    COMMENT "Generating ${name} benchmark fixture"
    JOB_POOL container_build
    VERBATIM)
  add_custom_target("${name}" DEPENDS "${output}")
  set_property(GLOBAL APPEND PROPERTY BENCHMARK_FIXTURE_OUTPUTS "${output}")
endfunction()

set(fixture_sources "${REPOSITORY_DIRECTORY}/tools/benchmarks/src/main/resources")
add_benchmark_fixture(cuda cuda sm_86 "${fixture_sources}/cloverleaf-flux.cu" cloverleaf-flux.ptx)
add_benchmark_fixture(hip hip gfx1030 "${fixture_sources}/cloverleaf-flux.cu" cloverleaf-flux.hsaco)
add_benchmark_fixture(hsa hsa gfx1030 "${CONFORMANCE_SOURCE_DIRECTORY}/unified/src/vector_copy.cl" hsa-vector-copy.hsaco)
add_benchmark_fixture(opencl sycl "" "${fixture_sources}/opencl-cts-async-copy.cl" opencl-cts-async-copy.spv)

get_property(fixture_outputs GLOBAL PROPERTY BENCHMARK_FIXTURE_OUTPUTS)
add_custom_target(fixtures ALL DEPENDS ${fixture_outputs})
