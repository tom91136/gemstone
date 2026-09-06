cmake_minimum_required(VERSION 3.25)

project(unified_suite VERSION 0.1.0 LANGUAGES CXX)

include(CTest)
include(GNUInstallDirs)
include(CMakePackageConfigHelpers)

option(UNIFIED_SUITE_BUILD_HOST "Build the dependency-free host reference executable" ON)
option(UNIFIED_SUITE_BUILD_CUDA "Build the CUDA Thrust + cuBLAS executable" OFF)
option(UNIFIED_SUITE_BUILD_HIP "Build the HIP rocThrust + rocBLAS executable" OFF)
option(UNIFIED_SUITE_BUILD_SYCL "Build the oneDPL + oneMKL executable" OFF)
option(UNIFIED_SUITE_SYCL_ENABLE_FP64_EMULATION
       "Enable Intel Arc FP64 emulation variables for SYCL CTest cases" ON)
option(UNIFIED_SUITE_INSTALL_RUNNERS
       "Install the conformance executables in addition to the header-only package" ON)
set(UNIFIED_SUITE_CTEST_SHARDS 8 CACHE STRING "Number of CTest shards per integration executable")
set(UNIFIED_SUITE_CTEST_TIMEOUT 120 CACHE STRING "Timeout in seconds for each CTest shard")
set(UNIFIED_SUITE_CTEST_PROFILE "compact" CACHE STRING
    "CTest workload profile (compact for fast checks, full for the complete parameter matrix)")
set_property(CACHE UNIFIED_SUITE_CTEST_PROFILE PROPERTY STRINGS compact full)

if(UNIFIED_SUITE_CTEST_PROFILE STREQUAL "full")
  # The full profile is deliberately explicit and reproducible.  It keeps the
  # compact profile suitable for local iteration while allowing CI and release
  # qualification to exercise every compiled scalar family and the boundary
  # sizes used by the unified catalog.
  set(_UNIFIED_DEFAULT_ALGORITHM_SIZES "0,1,7,15,31,32,33,63,64,65,127,128,129,257,1023,4096")
  # oneDPL's SPIR-V CPU-emulator path remains practical through n1024 for
  # linear algorithms. n4096 is retained as an explicit diagnostic because
  # cumulative large dispatches can exhaust the emulated Level Zero device.
  set(_UNIFIED_DEFAULT_SYCL_ALGORITHM_SIZES "0,1,7,15,31,32,33,63,64,65,127,128,129,257,1023,1024")
  set(_UNIFIED_DEFAULT_BLAS_SIZES "1,2,7,31,32,64,257,1024")
  set(_UNIFIED_DEFAULT_EDGE_CASE_SIZES "0,1,2,7,15,31,32,33,63,64,257,1023,1024")
  set(_UNIFIED_DEFAULT_EDGE_BLAS_SIZES "${_UNIFIED_DEFAULT_EDGE_CASE_SIZES}")
  set(_UNIFIED_DEFAULT_SYCL_EDGE_BLAS_SIZES "0,1,2,7,15,31,32,33,63,64,257")
  set(_UNIFIED_DEFAULT_STRESS_SIZES "257,4096,16384")
  set(_UNIFIED_DEFAULT_STRESS_ITERATIONS "8")
  set(_UNIFIED_DEFAULT_MATRIX_SHAPES "1x1x1,2x2x3,3x5x7,7x5x9,17x13x19")
  set(_UNIFIED_DEFAULT_ALGORITHM_TYPES "i8,i32,i64,f32,f64,f16")
  set(_UNIFIED_DEFAULT_BLAS_TYPES "i8,f32,f64,f16")
  set(_UNIFIED_DEFAULT_EDGE_ALGORITHM_TYPES "i8,i32,i64,f32,f64,f16")
  set(_UNIFIED_DEFAULT_EDGE_BLAS_TYPES "i8,f32,f64,f16")
  set(_UNIFIED_DEFAULT_STRESS_TYPES "i8,i32,i64,f32,f64")
elseif(NOT UNIFIED_SUITE_CTEST_PROFILE STREQUAL "compact")
  message(FATAL_ERROR
          "UNIFIED_SUITE_CTEST_PROFILE must be either compact or full, got '${UNIFIED_SUITE_CTEST_PROFILE}'")
else()
  set(_UNIFIED_DEFAULT_ALGORITHM_SIZES "0,1,31,32,33,63,64,65,257")
  set(_UNIFIED_DEFAULT_SYCL_ALGORITHM_SIZES "${_UNIFIED_DEFAULT_ALGORITHM_SIZES}")
  set(_UNIFIED_DEFAULT_BLAS_SIZES "1,31,64,257")
  set(_UNIFIED_DEFAULT_EDGE_CASE_SIZES "0,1,7,31,32,33,257")
  set(_UNIFIED_DEFAULT_EDGE_BLAS_SIZES "${_UNIFIED_DEFAULT_EDGE_CASE_SIZES}")
  set(_UNIFIED_DEFAULT_SYCL_EDGE_BLAS_SIZES "${_UNIFIED_DEFAULT_EDGE_CASE_SIZES}")
  set(_UNIFIED_DEFAULT_STRESS_SIZES "257,4096")
  set(_UNIFIED_DEFAULT_STRESS_ITERATIONS "8")
  set(_UNIFIED_DEFAULT_MATRIX_SHAPES "1x1x1,2x2x3,7x5x9")
  set(_UNIFIED_DEFAULT_ALGORITHM_TYPES "i32,f32")
  set(_UNIFIED_DEFAULT_BLAS_TYPES "f32")
  set(_UNIFIED_DEFAULT_EDGE_ALGORITHM_TYPES "i32,f32")
  set(_UNIFIED_DEFAULT_EDGE_BLAS_TYPES "f32")
  set(_UNIFIED_DEFAULT_STRESS_TYPES "i32,f32")
endif()

set(UNIFIED_SUITE_ALGORITHM_SIZES "${_UNIFIED_DEFAULT_ALGORITHM_SIZES}" CACHE STRING "Algorithm sizes used by CTest")
set(UNIFIED_SUITE_SYCL_ALGORITHM_SIZES "${_UNIFIED_DEFAULT_SYCL_ALGORITHM_SIZES}" CACHE STRING
    "SYCL algorithm sizes used by CTest")
set(UNIFIED_SUITE_BLAS_SIZES "${_UNIFIED_DEFAULT_BLAS_SIZES}" CACHE STRING "Level-1 BLAS sizes used by CTest")
set(UNIFIED_SUITE_EDGE_CASE_SIZES "${_UNIFIED_DEFAULT_EDGE_CASE_SIZES}" CACHE STRING "Semantic edge-case sizes used by CTest")
set(UNIFIED_SUITE_EDGE_BLAS_SIZES "${_UNIFIED_DEFAULT_EDGE_BLAS_SIZES}" CACHE STRING "Edge BLAS sizes used by CTest")
set(UNIFIED_SUITE_SYCL_EDGE_BLAS_SIZES "${_UNIFIED_DEFAULT_SYCL_EDGE_BLAS_SIZES}" CACHE STRING
    "SYCL edge BLAS sizes used by CTest")
set(UNIFIED_SUITE_STRESS_SIZES "${_UNIFIED_DEFAULT_STRESS_SIZES}" CACHE STRING "Bounded stress sizes used by CTest")
set(UNIFIED_SUITE_STRESS_ITERATIONS "${_UNIFIED_DEFAULT_STRESS_ITERATIONS}" CACHE STRING "Bounded stress repetitions used by CTest")
set(UNIFIED_SUITE_MATRIX_SHAPES "${_UNIFIED_DEFAULT_MATRIX_SHAPES}" CACHE STRING "GEMV/GEMM shapes used by CTest")
set(UNIFIED_SUITE_ALGORITHM_TYPES "${_UNIFIED_DEFAULT_ALGORITHM_TYPES}" CACHE STRING "Algorithm scalar types used by CTest")
set(UNIFIED_SUITE_BLAS_TYPES "${_UNIFIED_DEFAULT_BLAS_TYPES}" CACHE STRING "BLAS scalar types used by CTest")
set(UNIFIED_SUITE_EDGE_ALGORITHM_TYPES "${_UNIFIED_DEFAULT_EDGE_ALGORITHM_TYPES}" CACHE STRING "Edge algorithm scalar types used by CTest")
set(UNIFIED_SUITE_EDGE_BLAS_TYPES "${_UNIFIED_DEFAULT_EDGE_BLAS_TYPES}" CACHE STRING "Edge BLAS scalar types used by CTest")
set(UNIFIED_SUITE_STRESS_TYPES "${_UNIFIED_DEFAULT_STRESS_TYPES}" CACHE STRING "Bounded stress scalar types used by CTest")

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

function(unified_suite_register_typed_shards target backend suite case_prefix scalar_types type_option category edge_case_sizes)
  if(NOT BUILD_TESTING)
    return()
  endif()
  if(UNIFIED_SUITE_CTEST_SHARDS LESS 1)
    message(FATAL_ERROR "UNIFIED_SUITE_CTEST_SHARDS must be positive")
  endif()
  # Cache variables use comma-separated command-line spelling.  Keep one
  # scalar family per CTest process so the largest HIP profile cannot put all
  # type-specific kernels behind one 120-second timeout.  The runner still
  # owns stable hash partitioning within each typed catalog.
  set(_unified_suite_algorithm_sizes "${UNIFIED_SUITE_ALGORITHM_SIZES}")
  if(backend STREQUAL "sycl")
    set(_unified_suite_algorithm_sizes "${UNIFIED_SUITE_SYCL_ALGORITHM_SIZES}")
  endif()
  string(REPLACE "," ";" _unified_suite_scalar_types "${scalar_types}")
  math(EXPR last_shard "${UNIFIED_SUITE_CTEST_SHARDS} - 1")
  foreach(scalar_type IN LISTS _unified_suite_scalar_types)
    foreach(shard RANGE 0 ${last_shard})
      set(test_name "${backend}/${suite}/${category}/${scalar_type}/shard-${shard}-of-${UNIFIED_SUITE_CTEST_SHARDS}")
      add_test(
        NAME "${test_name}"
        COMMAND $<TARGET_FILE:${target}>
          --suite=${suite}
          --algorithm-sizes=${_unified_suite_algorithm_sizes}
          --blas-sizes=${UNIFIED_SUITE_BLAS_SIZES}
          --edge-case-sizes=${edge_case_sizes}
          --stress-sizes=${UNIFIED_SUITE_STRESS_SIZES}
          --stress-iterations=${UNIFIED_SUITE_STRESS_ITERATIONS}
          --matrix-shapes=${UNIFIED_SUITE_MATRIX_SHAPES}
          --algorithm-types=${UNIFIED_SUITE_ALGORITHM_TYPES}
          --blas-types=${UNIFIED_SUITE_BLAS_TYPES}
          --edge-algorithm-types=${UNIFIED_SUITE_EDGE_ALGORITHM_TYPES}
          --edge-blas-types=${UNIFIED_SUITE_EDGE_BLAS_TYPES}
          --stress-types=${UNIFIED_SUITE_STRESS_TYPES}
          --${type_option}=${scalar_type}
          --filter=^${case_prefix}/.*${scalar_type}/
          --shard-index=${shard}
          --shard-count=${UNIFIED_SUITE_CTEST_SHARDS}
          --quiet-pass
          --allow-empty-shard
          --allow-all-skipped
      )
      set_tests_properties(
        "${test_name}"
        PROPERTIES
          LABELS "unified-suite;${backend};${suite};${category};${scalar_type}"
          TIMEOUT "${UNIFIED_SUITE_CTEST_TIMEOUT}"
      )
      if(backend STREQUAL "sycl" AND UNIFIED_SUITE_SYCL_ENABLE_FP64_EMULATION)
        set_property(
          TEST "${test_name}"
          APPEND PROPERTY ENVIRONMENT
            "OverrideDefaultFP64Settings=1"
            "IGC_EnableDPEmulation=1"
        )
      endif()
    endforeach()
  endforeach()
endfunction()

function(unified_suite_register_shards target backend)
  # Each process receives one suite/category and one scalar type.  The
  # existing shard count remains the number of stable hash partitions within
  # that slice, preserving deterministic coverage while bounding per-process
  # work for the expanded HIP catalog.
  set(_unified_edge_blas_sizes "${UNIFIED_SUITE_EDGE_BLAS_SIZES}")
  if(backend STREQUAL "sycl")
    set(_unified_edge_blas_sizes "${UNIFIED_SUITE_SYCL_EDGE_BLAS_SIZES}")
  endif()
  unified_suite_register_typed_shards(
    ${target} ${backend} algorithms algorithms "${UNIFIED_SUITE_ALGORITHM_TYPES}" algorithm-types algorithm
    "${UNIFIED_SUITE_EDGE_CASE_SIZES}"
  )
  unified_suite_register_typed_shards(
    ${target} ${backend} blas blas "${UNIFIED_SUITE_BLAS_TYPES}" blas-types blas "${UNIFIED_SUITE_EDGE_CASE_SIZES}"
  )
  unified_suite_register_typed_shards(
    ${target} ${backend} edge-cases edge-cases/algorithms "${UNIFIED_SUITE_EDGE_ALGORITHM_TYPES}"
    edge-algorithm-types algorithms "${UNIFIED_SUITE_EDGE_CASE_SIZES}"
  )
  unified_suite_register_typed_shards(
    ${target} ${backend} edge-cases edge-cases/blas "${UNIFIED_SUITE_EDGE_BLAS_TYPES}" edge-blas-types blas
    "${_unified_edge_blas_sizes}"
  )
  unified_suite_register_typed_shards(
    ${target} ${backend} async async "${UNIFIED_SUITE_EDGE_ALGORITHM_TYPES}" edge-algorithm-types async
    "${UNIFIED_SUITE_EDGE_CASE_SIZES}"
  )
  unified_suite_register_typed_shards(
    ${target} ${backend} stress stress "${UNIFIED_SUITE_STRESS_TYPES}" stress-types stress
    "${UNIFIED_SUITE_EDGE_CASE_SIZES}"
  )
endfunction()

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
  unified_suite_register_shards(unified_suite_host host)
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
  unified_suite_register_shards(unified_suite_cuda cuda)
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
  set(
    _unified_suite_hip_sources
    programs/hip/algorithms_i8.hip
    programs/hip/algorithms_i32.hip
    programs/hip/algorithms_i64.hip
    programs/hip/algorithms_f32.hip
    programs/hip/algorithms_f64.hip
    programs/hip/blas.hip
    programs/hip/edge_cases.hip
    programs/hip/async.hip
    programs/hip/stress.hip
  )
  add_executable(
    unified_suite_hip programs/main.cpp programs/algorithm_catalog.cpp ${_unified_suite_hip_sources}
  )
  set_source_files_properties(${_unified_suite_hip_sources} PROPERTIES LANGUAGE HIP)
  target_link_libraries(unified_suite_hip PRIVATE unified_suite::unified_suite)
  if(TARGET roc::rocblas)
    target_link_libraries(unified_suite_hip PRIVATE roc::rocblas)
  elseif(TARGET rocblas)
    target_link_libraries(unified_suite_hip PRIVATE rocblas)
  else()
    message(FATAL_ERROR "rocBLAS package did not define roc::rocblas or rocblas")
  endif()
  set_target_properties(
    unified_suite_hip
    PROPERTIES HIP_STANDARD 20 HIP_STANDARD_REQUIRED YES HIP_EXTENSIONS NO
  )
  list(APPEND _unified_suite_install_runners unified_suite_hip)
  unified_suite_register_shards(unified_suite_hip hip)
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
  unified_suite_register_shards(unified_suite_sycl sycl)
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
