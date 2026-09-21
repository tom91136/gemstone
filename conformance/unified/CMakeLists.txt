if(TARGET conformance_unit_tests)
  target_sources(conformance_unit_tests PRIVATE
    tests/reference_algorithms.cpp
    tests/reference_blas.cpp
    tests/reference_edge.cpp
    tests/async_test.cpp
    tests/cases_test.cpp)
  target_include_directories(conformance_unit_tests PRIVATE src tests)
endif()

if(NOT CONFORMANCE_BUILD_UNIFIED)
  return()
endif()

add_executable(unified src/main.cpp)
target_include_directories(unified PRIVATE src)
target_link_libraries(unified PRIVATE Catch2::Catch2 conformance_device magic_enum::magic_enum fmt::fmt-header-only)
target_compile_definitions(unified PRIVATE CONFORMANCE_BACKEND_NAME="${CONFORMANCE_BACKEND}")
set_target_properties(unified PROPERTIES
  CXX_STANDARD 20
  CXX_STANDARD_REQUIRED YES
  CXX_EXTENSIONS NO
  OUTPUT_NAME unified)

if(APPLE)
  set_target_properties(unified PROPERTIES INSTALL_RPATH "@loader_path/lib")
elseif(UNIX)
  set_target_properties(unified PROPERTIES INSTALL_RPATH "$ORIGIN/lib")
endif()

if(CONFORMANCE_BACKEND STREQUAL "opencl")
  find_package(OpenCL REQUIRED)
  target_sources(unified PRIVATE src/opencl/runtime.cpp)
  target_link_libraries(unified PRIVATE OpenCL::OpenCL)

elseif(CONFORMANCE_BACKEND STREQUAL "hsa")
  list(LENGTH CMAKE_HIP_ARCHITECTURES hsa_architecture_count)
  if(NOT hsa_architecture_count EQUAL 1)
    message(FATAL_ERROR "The HSA code-object backend requires exactly one CMAKE_HIP_ARCHITECTURES value")
  endif()
  find_path(HSA_INCLUDE_DIR hsa/hsa.h PATHS /opt/rocm/include REQUIRED)
  find_library(HSA_LIBRARY hsa-runtime64 PATHS /opt/rocm/lib REQUIRED)
  find_program(AMDGPU_CLANG clang PATHS /opt/rocm/llvm/bin NO_DEFAULT_PATH REQUIRED)
  add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco
    COMMAND ${AMDGPU_CLANG} -x cl -target amdgcn-amd-amdhsa
      -mcpu=${CMAKE_HIP_ARCHITECTURES} -cl-std=CL2.0 -mcode-object-version=4
      -ffile-prefix-map=${CMAKE_SOURCE_DIR}=.
      -ffile-prefix-map=${CMAKE_BINARY_DIR}=.build
      -Xclang -finclude-default-header ${CMAKE_CURRENT_SOURCE_DIR}/src/vector_copy.cl
      -o ${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco
    DEPENDS src/vector_copy.cl VERBATIM)
  add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/dispatch_image.hpp
    COMMAND ${CMAKE_COMMAND}
      -DINPUT=${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco
      -DOUTPUT=${CMAKE_CURRENT_BINARY_DIR}/dispatch_image.hpp
      -P ${CMAKE_CURRENT_SOURCE_DIR}/cmake/EmbedBinary.cmake
    DEPENDS ${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco
      ${CMAKE_CURRENT_SOURCE_DIR}/cmake/EmbedBinary.cmake VERBATIM)
  target_sources(unified PRIVATE src/hsa/runtime.cpp
    ${CMAKE_CURRENT_BINARY_DIR}/dispatch_image.hpp)
  target_include_directories(unified PRIVATE ${HSA_INCLUDE_DIR} ${CMAKE_CURRENT_BINARY_DIR})
  target_compile_definitions(unified PRIVATE
    CONFORMANCE_DEVICE_TARGETS="${CMAKE_HIP_ARCHITECTURES}")
  target_link_libraries(unified PRIVATE ${HSA_LIBRARY})

elseif(CONFORMANCE_BACKEND STREQUAL "cuda")
  enable_language(CUDA)
  find_package(CUDAToolkit REQUIRED)
  target_compile_definitions(unified PRIVATE UNIFIED_API_CUDA=1)
  target_sources(unified PRIVATE
    src/cuda/algorithms_i8.cu
    src/cuda/algorithms_i32.cu
    src/cuda/algorithms_i64.cu
    src/cuda/algorithms_f32.cu
    src/cuda/algorithms_f64.cu
    src/cuda/blas.cu
    src/cuda/edge.cu
    src/cuda/async.cu
    src/cuda/stress.cu
    src/cuda/regressions.cu)
  target_link_libraries(unified PRIVATE CUDA::cudart_static CUDA::cublas)
  string(REPLACE ";" "," cuda_device_targets "${CMAKE_CUDA_ARCHITECTURES}")
  target_compile_definitions(unified PRIVATE CONFORMANCE_DEVICE_TARGETS="${cuda_device_targets}")
  set_target_properties(unified PROPERTIES
    CUDA_STANDARD 17
    CUDA_STANDARD_REQUIRED YES
    CUDA_EXTENSIONS NO
    CUDA_RUNTIME_LIBRARY Static)

elseif(CONFORMANCE_BACKEND STREQUAL "hip")
  enable_language(HIP)
  target_compile_definitions(unified PRIVATE UNIFIED_API_HIP=1)
  set(_unified_rocm_hints "${CMAKE_HIP_COMPILER_ROCM_ROOT}" "$ENV{ROCM_PATH}" /opt/rocm)
  if(CONFORMANCE_HIP_ENABLE_ROCBLAS)
    find_path(ROCBLAS_INCLUDE_DIR rocblas/rocblas.h
      HINTS ${_unified_rocm_hints} PATH_SUFFIXES include NO_DEFAULT_PATH REQUIRED)
    find_library(ROCBLAS_LIBRARY rocblas
      HINTS ${_unified_rocm_hints} PATH_SUFFIXES lib lib64 NO_DEFAULT_PATH REQUIRED)
    get_filename_component(ROCBLAS_LIBRARY_DIR "${ROCBLAS_LIBRARY}" DIRECTORY)
    conformance_runtime_plugins("${ROCBLAS_LIBRARY}")
    set(ROCBLAS_TENSILE_LIBRARY_DIR "${ROCBLAS_LIBRARY_DIR}/rocblas/library")
    set(ROCBLAS_TENSILE_AVAILABLE YES)
    foreach(hip_architecture IN LISTS CMAKE_HIP_ARCHITECTURES)
      string(REGEX REPLACE ":.*$" "" hip_architecture_base "${hip_architecture}")
      file(GLOB rocblas_tensile_code_objects
        "${ROCBLAS_TENSILE_LIBRARY_DIR}/Kernels.so-*-${hip_architecture_base}*.hsaco")
      if(NOT EXISTS "${ROCBLAS_TENSILE_LIBRARY_DIR}/TensileLibrary_lazy_${hip_architecture_base}.dat"
          OR NOT rocblas_tensile_code_objects)
        set(ROCBLAS_TENSILE_AVAILABLE NO)
        message(STATUS
          "rocBLAS has no Tensile kernels for ${hip_architecture_base}; HIP GEMM cases will skip")
      endif()
    endforeach()
  endif()
  target_sources(unified PRIVATE
    src/hip/algorithms_i8.hip
    src/hip/algorithms_i32.hip
    src/hip/algorithms_i64.hip
    src/hip/algorithms_f32.hip
    src/hip/algorithms_f64.hip
    src/hip/rocthrust_semantic_0.hip
    src/hip/rocthrust_semantic_1.hip
    src/hip/rocthrust_semantic_2.hip
    src/hip/rocthrust_semantic_3.hip
    src/hip/blas.hip
    src/hip/edge.hip
    src/hip/edge_stress_i8.hip
    src/hip/edge_stress_i32.hip
    src/hip/edge_stress_i64.hip
    src/hip/edge_stress_f32.hip
    src/hip/edge_stress_f64.hip
    src/hip/async.hip
    src/hip/regressions.hip)
  target_compile_definitions(unified PRIVATE __HIP_PLATFORM_AMD__=1)
  string(REPLACE ";" "," hip_device_targets "${CMAKE_HIP_ARCHITECTURES}")
  target_compile_definitions(unified PRIVATE CONFORMANCE_DEVICE_TARGETS="${hip_device_targets}")
  if(CONFORMANCE_HIP_ENABLE_ROCBLAS)
    target_compile_definitions(unified PRIVATE CONFORMANCE_HIP_HAS_ROCBLAS=1)
    if(ROCBLAS_TENSILE_AVAILABLE)
      target_compile_definitions(unified PRIVATE CONFORMANCE_HIP_HAS_TENSILE=1)
    endif()
    target_include_directories(unified PRIVATE "${ROCBLAS_INCLUDE_DIR}")
    target_compile_options(unified PRIVATE "$<$<COMPILE_LANGUAGE:HIP>:-I${ROCBLAS_INCLUDE_DIR}>")
    # amdclang++'s implicit --hip-link search must see the selected ROCm tree
    # before any distribution compatibility runtime.
    target_link_options(unified PRIVATE "-L${ROCBLAS_LIBRARY_DIR}")
    target_link_libraries(unified PRIVATE "${ROCBLAS_LIBRARY}")
  endif()
  set_target_properties(unified PROPERTIES
    HIP_STANDARD 20
    HIP_STANDARD_REQUIRED YES
    HIP_EXTENSIONS NO)
  if(CONFORMANCE_HIP_ENABLE_ROCBLAS)
    install(DIRECTORY /opt/rocm/lib/rocblas/library DESTINATION lib/rocblas
      COMPONENT support OPTIONAL PATTERN "TensileManifest.txt" EXCLUDE)
  endif()

elseif(CONFORMANCE_BACKEND STREQUAL "sycl")
  target_compile_definitions(unified PRIVATE UNIFIED_API_SYCL=1)
  if(NOT CMAKE_CXX_COMPILER_ID MATCHES "IntelLLVM|Clang")
    message(WARNING "The SYCL target normally requires Intel oneAPI icpx")
  endif()
  target_sources(unified PRIVATE
    src/sycl/algorithms_i8.cpp
    src/sycl/algorithms_i32.cpp
    src/sycl/algorithms_i64.cpp
    src/sycl/algorithms_f32.cpp
    src/sycl/algorithms_f64.cpp
    src/sycl/blas.cpp
    src/sycl/edge.cpp
    src/sycl/async.cpp
    src/sycl/stress.cpp
    src/sycl/runtime.cpp)
  target_link_libraries(unified PRIVATE
    mkl_sycl_blas
    mkl_intel_lp64
    mkl_intel_thread
    mkl_core
    iomp5
    pthread
    m
    dl)
  target_compile_options(unified PRIVATE -fsycl -fsycl-device-code-split=per_kernel)
  target_link_options(unified PRIVATE -fsycl -fsycl-device-code-split=per_kernel)

else()
  message(FATAL_ERROR "Unsupported unified backend: ${CONFORMANCE_BACKEND}")
endif()

conformance_register_test_program(unified 120)

if(CONFORMANCE_BACKEND MATCHES "^(cuda|hip)$")
  install(FILES licenses/gpuocelot.txt
    DESTINATION licenses/unified COMPONENT support)
endif()
