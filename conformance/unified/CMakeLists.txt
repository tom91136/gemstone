cmake_minimum_required(VERSION 3.25)

project(unified_suite VERSION 0.1.0 LANGUAGES CXX)

include(CTest)
include(GNUInstallDirs)
include(CMakePackageConfigHelpers)

option(UNIFIED_SUITE_BUILD_HOST "Build the dependency-free host reference executable" ON)
option(UNIFIED_SUITE_BUILD_CUDA "Build the CUDA Thrust + cuBLAS executable" OFF)
option(UNIFIED_SUITE_BUILD_HIP "Build the HIP rocThrust + rocBLAS executable" OFF)
option(UNIFIED_SUITE_BUILD_SYCL "Build the oneDPL + oneMKL executable" OFF)
option(UNIFIED_SUITE_BUILD_OPENCL "Build direct OpenCL runtime probes" OFF)
option(UNIFIED_SUITE_BUILD_HSA "Build direct HSA runtime probes" OFF)
set(UNIFIED_SUITE_HSA_ARCHITECTURE "gfx900" CACHE STRING "Target for the embedded HSA code object")

option(UNIFIED_SUITE_SYCL_ENABLE_FP64_EMULATION
       "Enable Intel Arc FP64 emulation variables for SYCL CTest cases" ON)
option(UNIFIED_SUITE_INSTALL_RUNNERS
       "Install the conformance executables in addition to the header-only package" ON)
set(UNIFIED_SUITE_CTEST_TIMEOUT 120 CACHE STRING "Timeout in seconds for each CTest case")
set(UNIFIED_SUITE_CTEST_PROFILE "compact" CACHE STRING
    "CTest workload profile (compact for fast checks, full for the complete parameter matrix)")
set_property(CACHE UNIFIED_SUITE_CTEST_PROFILE PROPERTY STRINGS compact full)

if(NOT UNIFIED_SUITE_CTEST_PROFILE MATCHES "^(compact|full)$")
  message(FATAL_ERROR "UNIFIED_SUITE_CTEST_PROFILE must be compact or full")
endif()

foreach(retired IN ITEMS SYCL_ALGORITHM_SIZES SYCL_EDGE_BLAS_SIZES CTEST_SHARDS)
  if(DEFINED UNIFIED_SUITE_${retired})
    message(FATAL_ERROR "UNIFIED_SUITE_${retired} is retired; remove it from the cache and use the common matrix settings")
  endif()
endforeach()

# Empty cache values inherit the runner's profile. CMake owns discovery, not a second copy of profile data.
foreach(parameter IN ITEMS ALGORITHM_SIZES BLAS_SIZES EDGE_CASE_SIZES EDGE_BLAS_SIZES STRESS_SIZES
    STRESS_ITERATIONS MATRIX_SHAPES ALGORITHM_TYPES BLAS_TYPES EDGE_ALGORITHM_TYPES EDGE_BLAS_TYPES STRESS_TYPES)
  set(UNIFIED_SUITE_${parameter} "" CACHE STRING "Optional ${parameter} override for every backend")
endforeach()

# Installed runners are deliberately relocatable.  The container build copies
# vendor user-space libraries into <prefix>/lib; the host loader then resolves
# them relative to each runner instead of consulting the build container's
# absolute compiler paths.  libc and kernel/driver libraries remain host
# responsibilities, as they must for both a physical oracle and Gemstone.
set(CMAKE_INSTALL_RPATH "$ORIGIN/../lib")
set(CMAKE_INSTALL_RPATH_USE_LINK_PATH FALSE)

set(_unified_suite_install_runners)

add_library(unified_suite INTERFACE)
add_library(unified_suite::unified_suite ALIAS unified_suite)
target_compile_features(unified_suite INTERFACE cxx_std_20)
target_include_directories(
  unified_suite
  INTERFACE
    $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
    $<INSTALL_INTERFACE:${CMAKE_INSTALL_INCLUDEDIR}>
)

function(unified_suite_register_catalog target backend suite category case_filter edge_case_sizes)
  if(NOT BUILD_TESTING)
    return()
  endif()
  set(arguments "--profile=${UNIFIED_SUITE_CTEST_PROFILE}" "--suite=${suite}" "--filter=${case_filter}" "--quiet-pass")
  foreach(parameter IN ITEMS ALGORITHM_SIZES BLAS_SIZES STRESS_SIZES STRESS_ITERATIONS MATRIX_SHAPES
      ALGORITHM_TYPES BLAS_TYPES EDGE_ALGORITHM_TYPES EDGE_BLAS_TYPES STRESS_TYPES)
    if(NOT "${UNIFIED_SUITE_${parameter}}" STREQUAL "")
      string(TOLOWER "${parameter}" option)
      string(REPLACE "_" "-" option "${option}")
      list(APPEND arguments "--${option}=${UNIFIED_SUITE_${parameter}}")
    endif()
  endforeach()
  if(NOT "${edge_case_sizes}" STREQUAL "")
    list(APPEND arguments "--edge-case-sizes=${edge_case_sizes}")
  endif()
  set(DISCOVER_ARGUMENTS "")
  foreach(argument IN LISTS arguments)
    string(APPEND DISCOVER_ARGUMENTS " [==[${argument}]==]")
  endforeach()
  set(DISCOVER_FIXED_PROBE FALSE)
  if(category STREQUAL "runtime")
    set(DISCOVER_FIXED_PROBE TRUE)
  endif()
  set(DISCOVER_TARGET "${target}")
  set(DISCOVER_BACKEND "${backend}")
  set(DISCOVER_SUITE "${suite}")
  set(DISCOVER_ID "${backend}/${suite}/${category}")
  set(fragment "${CMAKE_CURRENT_BINARY_DIR}/${target}-${suite}-${category}")
  configure_file(tools/discover-tests.cmake.in "${fragment}.configured.cmake" @ONLY)
  file(GENERATE OUTPUT "${fragment}.cmake" INPUT "${fragment}.configured.cmake")
  set_property(DIRECTORY APPEND PROPERTY TEST_INCLUDE_FILES "${fragment}.cmake")
endfunction()

function(unified_suite_register_runtime target backend)
  unified_suite_register_catalog(${target} ${backend} edge-cases runtime "^edge-cases/runtime/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
endfunction()

function(unified_suite_register_cases target backend)
  if(NOT backend STREQUAL "host")
    unified_suite_register_runtime(${target} ${backend})
  endif()
  set(edge_blas_sizes "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  if(NOT "${UNIFIED_SUITE_EDGE_BLAS_SIZES}" STREQUAL "")
    set(edge_blas_sizes "${UNIFIED_SUITE_EDGE_BLAS_SIZES}")
  endif()
  unified_suite_register_catalog(${target} ${backend} algorithms algorithms "^algorithms/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  unified_suite_register_catalog(${target} ${backend} blas blas "^blas/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  unified_suite_register_catalog(${target} ${backend} edge-cases algorithms "^edge-cases/algorithms/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  unified_suite_register_catalog(${target} ${backend} edge-cases blas "^edge-cases/blas/" "${edge_blas_sizes}")
  unified_suite_register_catalog(${target} ${backend} async async "^async/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  unified_suite_register_catalog(${target} ${backend} stress stress "^stress/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
endfunction()

if(UNIFIED_SUITE_BUILD_OPENCL)
  find_package(OpenCL REQUIRED)
  add_executable(unified_suite_opencl programs/runtime_main.cpp programs/opencl/edge_cases.cpp)
  target_link_libraries(unified_suite_opencl PRIVATE unified_suite::unified_suite OpenCL::OpenCL)
  list(APPEND _unified_suite_install_runners unified_suite_opencl)
  unified_suite_register_runtime(unified_suite_opencl opencl)
endif()

if(UNIFIED_SUITE_BUILD_HSA)
  find_path(HSA_INCLUDE_DIR hsa/hsa.h PATHS /opt/rocm/include REQUIRED)
  find_library(HSA_LIBRARY hsa-runtime64 PATHS /opt/rocm/lib REQUIRED)
  find_program(AMDGPU_CLANG clang PATHS /opt/rocm/llvm/bin NO_DEFAULT_PATH REQUIRED)
  find_package(Python3 COMPONENTS Interpreter REQUIRED)
  add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco
    COMMAND ${AMDGPU_CLANG} -x cl -target amdgcn-amd-amdhsa
      -mcpu=${UNIFIED_SUITE_HSA_ARCHITECTURE} -cl-std=CL2.0 -mcode-object-version=4
      -Xclang -finclude-default-header ${CMAKE_CURRENT_SOURCE_DIR}/programs/hsa/vector_copy.cl
      -o ${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco
    DEPENDS programs/hsa/vector_copy.cl VERBATIM)
  add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/dispatch_image.hpp
    COMMAND ${Python3_EXECUTABLE} ${CMAKE_CURRENT_SOURCE_DIR}/tools/embed-binary.py
      ${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco ${CMAKE_CURRENT_BINARY_DIR}/dispatch_image.hpp
    DEPENDS ${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco tools/embed-binary.py VERBATIM)
  add_executable(unified_suite_hsa programs/runtime_main.cpp programs/hsa/edge_cases.cpp
    ${CMAKE_CURRENT_BINARY_DIR}/dispatch_image.hpp)
  target_include_directories(unified_suite_hsa PRIVATE ${HSA_INCLUDE_DIR} ${CMAKE_CURRENT_BINARY_DIR})
  target_link_libraries(unified_suite_hsa PRIVATE unified_suite::unified_suite ${HSA_LIBRARY})
  list(APPEND _unified_suite_install_runners unified_suite_hsa)
  unified_suite_register_runtime(unified_suite_hsa hsa)
endif()

if(UNIFIED_SUITE_BUILD_HOST)
  add_executable(
    unified_suite_host
    programs/main.cpp
    programs/algorithm_catalog.cpp
    programs/host/algorithms_i8.cpp
    programs/host/algorithms_i32.cpp
    programs/host/algorithms_i64.cpp
    programs/host/algorithms_f32.cpp
    programs/host/algorithms_f64.cpp
    programs/host/blas.cpp
    programs/host/edge_cases.cpp
    programs/host/async.cpp
    programs/host/stress.cpp
  )
  target_link_libraries(unified_suite_host PRIVATE unified_suite::unified_suite)
  list(APPEND _unified_suite_install_runners unified_suite_host)
  unified_suite_register_cases(unified_suite_host host)
  if(BUILD_TESTING)
    add_executable(unified_suite_catalog_test tests/catalog.cpp)
    target_link_libraries(unified_suite_catalog_test PRIVATE unified_suite::unified_suite)
    list(APPEND _unified_suite_install_runners unified_suite_catalog_test)
    add_test(NAME host/catalog-contract COMMAND unified_suite_catalog_test)
    set_tests_properties(host/catalog-contract PROPERTIES LABELS "unified-suite;host;catalog")
    add_executable(unified_suite_runner_contract_test tests/runner_contract.cpp)
    target_link_libraries(unified_suite_runner_contract_test PRIVATE unified_suite::unified_suite)
    list(APPEND _unified_suite_install_runners unified_suite_runner_contract_test)
    add_test(NAME host/runner-contract COMMAND unified_suite_runner_contract_test)
    set_tests_properties(host/runner-contract PROPERTIES LABELS "unified-suite;host;runner")
    add_executable(unified_suite_async_catalog_test tests/async_catalog.cpp)
    target_link_libraries(unified_suite_async_catalog_test PRIVATE unified_suite::unified_suite)
    list(APPEND _unified_suite_install_runners unified_suite_async_catalog_test)
    add_test(NAME host/async-catalog COMMAND unified_suite_async_catalog_test)
    set_tests_properties(host/async-catalog PROPERTIES LABELS "unified-suite;host;async")
  endif()
endif()

if(UNIFIED_SUITE_BUILD_CUDA)
  enable_language(CUDA)
  find_package(CUDAToolkit REQUIRED)
  add_executable(
    unified_suite_cuda
    programs/main.cpp
    programs/algorithm_catalog.cpp
    programs/cuda/algorithms_i8.cu
    programs/cuda/algorithms_i32.cu
    programs/cuda/algorithms_i64.cu
    programs/cuda/algorithms_f32.cu
    programs/cuda/algorithms_f64.cu
    programs/cuda/blas.cu
    programs/cuda/edge_cases.cu
    programs/cuda/async.cu
    programs/cuda/stress.cu
  )
  target_link_libraries(
    unified_suite_cuda
    PRIVATE unified_suite::unified_suite CUDA::cudart_static CUDA::cublas
  )
  set_target_properties(
    unified_suite_cuda
    PROPERTIES
      CUDA_STANDARD 20
      CUDA_STANDARD_REQUIRED YES
      CUDA_EXTENSIONS NO
      CUDA_RUNTIME_LIBRARY Static
  )
  list(APPEND _unified_suite_install_runners unified_suite_cuda)
  unified_suite_register_cases(unified_suite_cuda cuda)
endif()

if(UNIFIED_SUITE_BUILD_HIP)
  enable_language(HIP)
  set(_unified_suite_rocm_hints "${CMAKE_HIP_COMPILER_ROCM_ROOT}" "$ENV{ROCM_PATH}" /opt/rocm)
  find_package(
    hip CONFIG REQUIRED
    HINTS ${_unified_suite_rocm_hints}
    PATH_SUFFIXES lib/cmake/hip
  )
  find_package(
    rocblas CONFIG REQUIRED
    HINTS ${_unified_suite_rocm_hints}
    PATH_SUFFIXES lib/cmake/rocblas
  )
  function(unified_suite_add_hip_partition target partition)
    add_executable(${target} programs/partition_main.cpp ${ARGN})
    set_source_files_properties(programs/partition_main.cpp ${ARGN} PROPERTIES LANGUAGE HIP)
    target_compile_definitions(${target} PRIVATE UNIFIED_SUITE_PARTITION=${partition})
    target_link_libraries(${target} PRIVATE unified_suite::unified_suite)
    if(TARGET roc::rocblas)
      target_link_libraries(${target} PRIVATE roc::rocblas)
    elseif(TARGET rocblas)
      target_link_libraries(${target} PRIVATE rocblas)
    else()
      message(FATAL_ERROR "rocBLAS package did not define roc::rocblas or rocblas")
    endif()
    set_target_properties(${target} PROPERTIES HIP_STANDARD 20 HIP_STANDARD_REQUIRED YES HIP_EXTENSIONS NO)
    set(_unified_suite_install_runners ${_unified_suite_install_runners} ${target} PARENT_SCOPE)
  endfunction()

  unified_suite_add_hip_partition(unified_suite_hip_algorithms_i8 1 programs/hip/algorithms_i8.hip)
  unified_suite_add_hip_partition(unified_suite_hip_algorithms_i32 2 programs/hip/algorithms_i32.hip)
  unified_suite_add_hip_partition(unified_suite_hip_algorithms_i64 3 programs/hip/algorithms_i64.hip)
  unified_suite_add_hip_partition(unified_suite_hip_algorithms_f16 4)
  unified_suite_add_hip_partition(unified_suite_hip_algorithms_f32 5 programs/hip/algorithms_f32.hip)
  unified_suite_add_hip_partition(unified_suite_hip_algorithms_f64 6 programs/hip/algorithms_f64.hip)
  unified_suite_add_hip_partition(unified_suite_hip_blas 7 programs/hip/blas.hip)
  unified_suite_add_hip_partition(unified_suite_hip 8 programs/hip/edge_cases.hip)
  unified_suite_add_hip_partition(unified_suite_hip_stress 9 programs/hip/stress.hip)
  unified_suite_add_hip_partition(unified_suite_hip_async 10 programs/hip/async.hip)

  foreach(type IN ITEMS i8 i32 i64 f16 f32 f64)
    unified_suite_register_catalog(unified_suite_hip_algorithms_${type} hip algorithms algorithms
      "^algorithms/[^/]*/${type}/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  endforeach()
  unified_suite_register_catalog(unified_suite_hip_algorithms_i32 hip algorithms semantic
    "^algorithms/roc-thrust-semantic/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  unified_suite_register_catalog(unified_suite_hip_blas hip blas blas "^blas/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  unified_suite_register_runtime(unified_suite_hip hip)
  unified_suite_register_catalog(unified_suite_hip hip edge-cases algorithms "^edge-cases/algorithms/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  set(_unified_suite_hip_edge_blas_sizes "${UNIFIED_SUITE_EDGE_BLAS_SIZES}")
  if("${_unified_suite_hip_edge_blas_sizes}" STREQUAL "")
    set(_unified_suite_hip_edge_blas_sizes "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  endif()
  unified_suite_register_catalog(unified_suite_hip hip edge-cases blas "^edge-cases/blas/" "${_unified_suite_hip_edge_blas_sizes}")
  unified_suite_register_catalog(unified_suite_hip_async hip async async "^async/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
  unified_suite_register_catalog(unified_suite_hip_stress hip stress stress "^stress/" "${UNIFIED_SUITE_EDGE_CASE_SIZES}")
endif()

if(UNIFIED_SUITE_BUILD_SYCL)
  if(NOT CMAKE_CXX_COMPILER_ID MATCHES "IntelLLVM|Clang")
    message(WARNING "The SYCL target normally requires Intel oneAPI icpx")
  endif()
  add_executable(
    unified_suite_sycl
    programs/main.cpp
    programs/algorithm_catalog.cpp
    programs/sycl/algorithms_i8.cpp
    programs/sycl/algorithms_i32.cpp
    programs/sycl/algorithms_i64.cpp
    programs/sycl/algorithms_f32.cpp
    programs/sycl/algorithms_f64.cpp
    programs/sycl/blas.cpp
    programs/sycl/edge_cases.cpp
    programs/sycl/async.cpp
    programs/sycl/stress.cpp
  )
  target_link_libraries(
    unified_suite_sycl
    PRIVATE
      unified_suite::unified_suite
      mkl_sycl_blas
      mkl_intel_lp64
      mkl_intel_thread
      mkl_core
      iomp5
      pthread
      m
      dl
  )
  target_compile_options(unified_suite_sycl PRIVATE -fsycl -fsycl-device-code-split=per_kernel)
  target_link_options(unified_suite_sycl PRIVATE -fsycl -fsycl-device-code-split=per_kernel)
  list(APPEND _unified_suite_install_runners unified_suite_sycl)
  unified_suite_register_cases(unified_suite_sycl sycl)
endif()

install(DIRECTORY include/ DESTINATION ${CMAKE_INSTALL_INCLUDEDIR})
if(UNIFIED_SUITE_INSTALL_RUNNERS AND _unified_suite_install_runners)
  install(TARGETS ${_unified_suite_install_runners} RUNTIME DESTINATION ${CMAKE_INSTALL_BINDIR})
endif()
install(TARGETS unified_suite EXPORT unified_suiteTargets)
install(
  EXPORT unified_suiteTargets
  FILE unified_suiteTargets.cmake
  NAMESPACE unified_suite::
  DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/unified_suite
)
configure_package_config_file(
  cmake/unified_suiteConfig.cmake.in
  "${CMAKE_CURRENT_BINARY_DIR}/unified_suiteConfig.cmake"
  INSTALL_DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/unified_suite
)
write_basic_package_version_file(
  "${CMAKE_CURRENT_BINARY_DIR}/unified_suiteConfigVersion.cmake"
  VERSION ${PROJECT_VERSION}
  COMPATIBILITY SameMajorVersion
)
install(
  FILES
    "${CMAKE_CURRENT_BINARY_DIR}/unified_suiteConfig.cmake"
    "${CMAKE_CURRENT_BINARY_DIR}/unified_suiteConfigVersion.cmake"
  DESTINATION ${CMAKE_INSTALL_LIBDIR}/cmake/unified_suite
)

install(FILES programs/cuda/LICENSE.gpuocelot DESTINATION share/licenses/unified_suite)

install(FILES programs/hsa/LICENSE-ROCR-NCSA DESTINATION share/licenses/unified_suite)
