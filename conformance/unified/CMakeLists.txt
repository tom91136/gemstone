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
set(UNIFIED_SUITE_CTEST_SHARDS 8 CACHE STRING "Number of CTest shards per integration executable")
set(UNIFIED_SUITE_CTEST_TIMEOUT 1800 CACHE STRING "Timeout in seconds for each CTest shard")
set(UNIFIED_SUITE_ALGORITHM_SIZES "0,1,31,32,33,63,64,65,257" CACHE STRING "Algorithm sizes used by CTest")
set(UNIFIED_SUITE_BLAS_SIZES "1,31,64,257" CACHE STRING "Level-1 BLAS sizes used by CTest")
set(UNIFIED_SUITE_EDGE_CASE_SIZES "0,1,7,31,32,33,257" CACHE STRING "Semantic edge-case sizes used by CTest")
set(UNIFIED_SUITE_STRESS_SIZES "257,4096" CACHE STRING "Bounded stress sizes used by CTest")
set(UNIFIED_SUITE_STRESS_ITERATIONS "8" CACHE STRING "Bounded stress repetitions used by CTest")
set(UNIFIED_SUITE_MATRIX_SHAPES "2x2x3,7x5x9" CACHE STRING "GEMV/GEMM shapes used by CTest")
set(UNIFIED_SUITE_ALGORITHM_TYPES "i32,f32" CACHE STRING "Algorithm scalar types used by CTest")
set(UNIFIED_SUITE_BLAS_TYPES "f32" CACHE STRING "BLAS scalar types used by CTest")
set(UNIFIED_SUITE_EDGE_ALGORITHM_TYPES "i32,f32" CACHE STRING "Edge algorithm scalar types used by CTest")
set(UNIFIED_SUITE_EDGE_BLAS_TYPES "f32" CACHE STRING "Edge BLAS scalar types used by CTest")
set(UNIFIED_SUITE_STRESS_TYPES "i32,f32" CACHE STRING "Bounded stress scalar types used by CTest")

add_library(unified_suite INTERFACE)
add_library(unified_suite::unified_suite ALIAS unified_suite)
target_compile_features(unified_suite INTERFACE cxx_std_20)
target_include_directories(
  unified_suite
  INTERFACE
    $<BUILD_INTERFACE:${CMAKE_CURRENT_SOURCE_DIR}/include>
    $<INSTALL_INTERFACE:${CMAKE_INSTALL_INCLUDEDIR}>
)

function(unified_suite_register_shards target backend)
  if(NOT BUILD_TESTING)
    return()
  endif()
  if(UNIFIED_SUITE_CTEST_SHARDS LESS 1)
    message(FATAL_ERROR "UNIFIED_SUITE_CTEST_SHARDS must be positive")
  endif()
  math(EXPR last_shard "${UNIFIED_SUITE_CTEST_SHARDS} - 1")
  foreach(suite algorithms blas edge-cases stress)
    foreach(shard RANGE 0 ${last_shard})
      add_test(
        NAME "${backend}/${suite}/shard-${shard}-of-${UNIFIED_SUITE_CTEST_SHARDS}"
        COMMAND $<TARGET_FILE:${target}>
          --suite=${suite}
          --algorithm-sizes=${UNIFIED_SUITE_ALGORITHM_SIZES}
          --blas-sizes=${UNIFIED_SUITE_BLAS_SIZES}
          --edge-case-sizes=${UNIFIED_SUITE_EDGE_CASE_SIZES}
          --stress-sizes=${UNIFIED_SUITE_STRESS_SIZES}
          --stress-iterations=${UNIFIED_SUITE_STRESS_ITERATIONS}
          --matrix-shapes=${UNIFIED_SUITE_MATRIX_SHAPES}
          --algorithm-types=${UNIFIED_SUITE_ALGORITHM_TYPES}
          --blas-types=${UNIFIED_SUITE_BLAS_TYPES}
          --edge-algorithm-types=${UNIFIED_SUITE_EDGE_ALGORITHM_TYPES}
          --edge-blas-types=${UNIFIED_SUITE_EDGE_BLAS_TYPES}
          --stress-types=${UNIFIED_SUITE_STRESS_TYPES}
          --shard-index=${shard}
          --shard-count=${UNIFIED_SUITE_CTEST_SHARDS}
          --quiet-pass
          --allow-empty-shard
      )
      set_tests_properties(
        "${backend}/${suite}/shard-${shard}-of-${UNIFIED_SUITE_CTEST_SHARDS}"
        PROPERTIES
          LABELS "unified-suite;${backend};${suite}"
          TIMEOUT "${UNIFIED_SUITE_CTEST_TIMEOUT}"
      )
      if(backend STREQUAL "sycl" AND UNIFIED_SUITE_SYCL_ENABLE_FP64_EMULATION)
        set_property(
          TEST "${backend}/${suite}/shard-${shard}-of-${UNIFIED_SUITE_CTEST_SHARDS}"
          APPEND PROPERTY ENVIRONMENT
            "OverrideDefaultFP64Settings=1"
            "IGC_EnableDPEmulation=1"
        )
      endif()
    endforeach()
  endforeach()
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
    programs/host/stress.cpp
  )
  target_link_libraries(unified_suite_host PRIVATE unified_suite::unified_suite)
  unified_suite_register_shards(unified_suite_host host)
  if(BUILD_TESTING)
    add_executable(unified_suite_catalog_test tests/catalog.cpp)
    target_link_libraries(unified_suite_catalog_test PRIVATE unified_suite::unified_suite)
    add_test(NAME host/catalog-contract COMMAND unified_suite_catalog_test)
    set_tests_properties(host/catalog-contract PROPERTIES LABELS "unified-suite;host;catalog")
    add_executable(unified_suite_runner_contract_test tests/runner_contract.cpp)
    target_link_libraries(unified_suite_runner_contract_test PRIVATE unified_suite::unified_suite)
    add_test(NAME host/runner-contract COMMAND unified_suite_runner_contract_test)
    set_tests_properties(host/runner-contract PROPERTIES LABELS "unified-suite;host;runner")
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
  unified_suite_register_shards(unified_suite_sycl sycl)
endif()

install(DIRECTORY include/ DESTINATION ${CMAKE_INSTALL_INCLUDEDIR})
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
