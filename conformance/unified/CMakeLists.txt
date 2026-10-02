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

include("${CMAKE_CURRENT_LIST_DIR}/../cmake/ToolchainImages.cmake")

add_executable(unified src/main.cpp)
target_include_directories(unified PRIVATE src)
target_link_libraries(unified PRIVATE Catch2::Catch2 conformance_device magic_enum::magic_enum fmt::fmt-header-only)
target_compile_definitions(unified PRIVATE
  CONFORMANCE_BACKEND_NAME="${CONFORMANCE_BACKEND}")
target_link_libraries(unified PRIVATE ${CMAKE_DL_LIBS})
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
  target_compile_definitions(unified PRIVATE UNIFIED_API_OPENCL=1)
  find_package(OpenCL REQUIRED)
  target_sources(unified PRIVATE src/opencl/runtime.cpp src/opencl/memory.cpp)
  target_link_libraries(unified PRIVATE OpenCL::OpenCL)

elseif(CONFORMANCE_BACKEND STREQUAL "hsa")
  target_compile_definitions(unified PRIVATE UNIFIED_API_HSA=1)
  list(LENGTH CMAKE_HIP_ARCHITECTURES hsa_architecture_count)
  if(NOT hsa_architecture_count EQUAL 1)
    message(FATAL_ERROR "The HSA code-object backend requires exactly one CMAKE_HIP_ARCHITECTURES value")
  endif()
  find_path(HSA_INCLUDE_DIR hsa/hsa.h PATHS /opt/rocm/include REQUIRED)
  find_library(HSA_LIBRARY hsa-runtime64 PATHS /opt/rocm/lib REQUIRED)
  conformance_opencl_commands(hsa "${CMAKE_HIP_ARCHITECTURES}"
    "${CMAKE_CURRENT_SOURCE_DIR}/src/hsa/vector_copy.cl" "${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco"
    dispatch_compile unused_translate
    "-ffile-prefix-map=${CMAKE_SOURCE_DIR}=." "-ffile-prefix-map=${CMAKE_BINARY_DIR}=.build")
  add_custom_command(OUTPUT "${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco"
    COMMAND ${dispatch_compile}
    DEPENDS src/hsa/vector_copy.cl "${CMAKE_CURRENT_LIST_DIR}/../cmake/ToolchainImages.cmake" VERBATIM)
  add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/dispatch_image.hpp
    COMMAND ${CMAKE_COMMAND}
      -DINPUT=${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco
      -DOUTPUT=${CMAKE_CURRENT_BINARY_DIR}/dispatch_image.hpp
      -P ${CMAKE_CURRENT_SOURCE_DIR}/cmake/EmbedBinary.cmake
    DEPENDS ${CMAKE_CURRENT_BINARY_DIR}/dispatch.hsaco
      ${CMAKE_CURRENT_SOURCE_DIR}/cmake/EmbedBinary.cmake VERBATIM)
  target_sources(unified PRIVATE src/hsa/runtime.cpp src/hsa/memory.cpp
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
    src/cuda/memory.cpp
    src/cuda/algorithms_i8.cu
    src/cuda/algorithms_i32.cu
    src/cuda/algorithms_i64.cu
    src/cuda/algorithms_f32.cu
    src/cuda/algorithms_f64.cu
    src/cuda/blas.cu
    src/cuda/tensor.cu
    src/cuda/mma884.cu
    src/cuda/edge.cu
    src/cuda/async.cu
    src/cuda/stress.cu
    src/cuda/regressions.cu)
  target_link_libraries(unified PRIVATE CUDA::cudart_static CUDA::cublas)
  string(REPLACE ";" "," cuda_device_targets "${conformance_cuda_architecture}")
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
    src/hip/memory.cpp
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
    src/sycl/memory.cpp
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

elseif(CONFORMANCE_BACKEND STREQUAL "levelzero")
  target_compile_definitions(unified PRIVATE UNIFIED_API_LEVELZERO=1)
  target_sources(unified PRIVATE src/levelzero/memory.cpp)
  target_link_libraries(unified PRIVATE conformance_levelzero)

else()
  message(FATAL_ERROR "Unsupported unified backend: ${CONFORMANCE_BACKEND}")
endif()

# Embed memory kernels just like the HSA dispatch image above. SYCL embeds its
# lambda kernels through the normal compiler pipeline.
if(NOT CONFORMANCE_BACKEND STREQUAL "sycl")
  set(memory_source "${CMAKE_CURRENT_SOURCE_DIR}/src")
  set(memory_output "${CMAKE_CURRENT_BINARY_DIR}/memory")
  file(MAKE_DIRECTORY "${memory_output}")
  if(CONFORMANCE_BACKEND STREQUAL "cuda")
    set(memory_image "${memory_source}/cuda/kernel.ptx")
    find_program(conformance_cuda_ptxas ptxas HINTS "${CUDAToolkit_BIN_DIR}" REQUIRED)
    set(memory_cubin_architecture "${conformance_cuda_architecture}")
    if(memory_cubin_architecture LESS 80)
      set(memory_cubin_architecture 80)
    endif()
    foreach(fixture IN ITEMS kernel atomic fence contended)
      set(cubin "${memory_output}/${fixture}.cubin")
      set(header "${memory_output}/${fixture}_sass_image.hpp")
      add_custom_command(OUTPUT "${cubin}"
        COMMAND "${conformance_cuda_ptxas}" "-arch=sm_${memory_cubin_architecture}"
          "${memory_source}/cuda/${fixture}.ptx" -o "${cubin}"
        DEPENDS "${memory_source}/cuda/${fixture}.ptx" VERBATIM)
      add_custom_command(OUTPUT "${header}"
        COMMAND "${CMAKE_COMMAND}" "-DINPUT=${cubin}" "-DOUTPUT=${header}"
          "-DSYMBOL=${fixture}_sass_image"
          -P "${CMAKE_CURRENT_SOURCE_DIR}/cmake/EmbedBinary.cmake"
        DEPENDS "${cubin}" cmake/EmbedBinary.cmake VERBATIM)
      target_sources(unified PRIVATE "${header}")
    endforeach()
    set(mma884_ptx "${memory_source}/cuda/mma884.ptx")
    set(mma884_ptx_header "${memory_output}/mma884_ptx_image.hpp")
    set(mma884_cubin "${memory_output}/mma884.cubin")
    set(mma884_sass_header "${memory_output}/mma884_sass_image.hpp")
    set(mma884_cubin_architecture "${conformance_cuda_architecture}")
    if(mma884_cubin_architecture LESS 70)
      set(mma884_cubin_architecture 70)
    endif()
    add_custom_command(OUTPUT "${mma884_ptx_header}"
      COMMAND "${CMAKE_COMMAND}" "-DINPUT=${mma884_ptx}" "-DOUTPUT=${mma884_ptx_header}"
        "-DSYMBOL=mma884_ptx_image" -P "${CMAKE_CURRENT_SOURCE_DIR}/cmake/EmbedBinary.cmake"
      DEPENDS "${mma884_ptx}" cmake/EmbedBinary.cmake VERBATIM)
    add_custom_command(OUTPUT "${mma884_cubin}"
      COMMAND "${conformance_cuda_ptxas}" "-arch=sm_${mma884_cubin_architecture}" "${mma884_ptx}" -o "${mma884_cubin}"
      DEPENDS "${mma884_ptx}" VERBATIM)
    add_custom_command(OUTPUT "${mma884_sass_header}"
      COMMAND "${CMAKE_COMMAND}" "-DINPUT=${mma884_cubin}" "-DOUTPUT=${mma884_sass_header}"
        "-DSYMBOL=mma884_sass_image" -P "${CMAKE_CURRENT_SOURCE_DIR}/cmake/EmbedBinary.cmake"
      DEPENDS "${mma884_cubin}" cmake/EmbedBinary.cmake VERBATIM)
    target_sources(unified PRIVATE "${mma884_ptx_header}" "${mma884_sass_header}")
  else()
    set(memory_options -O1)
    if(CONFORMANCE_BACKEND MATCHES "^(hip|hsa)$")
      set(memory_backend hsa)
      set(memory_image "${memory_output}/kernel.hsaco")
      # Retain one atomic operation per work-item, matching the native fixtures.
      list(APPEND memory_options -nogpulib -mllvm -amdgpu-atomic-optimizer-strategy=None)
      if(CONFORMANCE_BACKEND STREQUAL "hip")
        target_include_directories(unified PRIVATE "${CONFORMANCE_HIP_INCLUDE}")
      endif()
    else()
      list(APPEND memory_options -DUNIFIED_MEMORY_ATOMIC32=1)
      set(memory_backend opencl)
      set(memory_image "${memory_output}/kernel.spv")
    endif()
    conformance_opencl_commands("${memory_backend}" "${CMAKE_HIP_ARCHITECTURES}"
      "${memory_source}/memory_kernel.cl" "${memory_image}" memory_compile memory_translate ${memory_options})
    set(memory_commands COMMAND ${memory_compile})
    set(memory_byproducts)
    if(memory_translate)
      list(APPEND memory_commands COMMAND ${memory_translate})
      list(APPEND memory_byproducts "${memory_image}.bc")
    endif()
    add_custom_command(OUTPUT "${memory_image}"
      ${memory_commands}
      BYPRODUCTS ${memory_byproducts}
      DEPENDS "${memory_source}/memory_kernel.cl" "${CMAKE_CURRENT_LIST_DIR}/../cmake/ToolchainImages.cmake" VERBATIM)
  endif()
  set(memory_images memory)
  if(CONFORMANCE_BACKEND STREQUAL "cuda")
    foreach(fixture IN ITEMS atomic fence contended)
      list(APPEND memory_images "${fixture}")
      set(${fixture}_image "${memory_source}/cuda/${fixture}.ptx")
    endforeach()
  elseif(CONFORMANCE_BACKEND MATCHES "^(opencl|levelzero)$")
    set(memory64_image "${memory_output}/kernel64.spv")
    conformance_opencl_commands(opencl "" "${memory_source}/memory_kernel.cl" "${memory64_image}"
      memory64_compile memory64_translate -O1)
    add_custom_command(OUTPUT "${memory64_image}"
      COMMAND ${memory64_compile} COMMAND ${memory64_translate}
      BYPRODUCTS "${memory64_image}.bc"
      DEPENDS "${memory_source}/memory_kernel.cl" "${CMAKE_CURRENT_LIST_DIR}/../cmake/ToolchainImages.cmake" VERBATIM)
    list(APPEND memory_images memory64)
  endif()
  foreach(fixture IN LISTS memory_images)
    add_custom_command(OUTPUT "${memory_output}/${fixture}_image.hpp"
      COMMAND "${CMAKE_COMMAND}" "-DINPUT=${${fixture}_image}"
        "-DOUTPUT=${memory_output}/${fixture}_image.hpp" "-DSYMBOL=${fixture}_image"
        -P "${CMAKE_CURRENT_SOURCE_DIR}/cmake/EmbedBinary.cmake"
      DEPENDS "${${fixture}_image}" cmake/EmbedBinary.cmake VERBATIM)
    target_sources(unified PRIVATE "${memory_output}/${fixture}_image.hpp")
  endforeach()
  target_include_directories(unified PRIVATE "${memory_output}")
endif()

conformance_register_test_program(unified ${CONFORMANCE_TIMEOUT_UNIFIED})

if(CONFORMANCE_BACKEND MATCHES "^(cuda|hip)$")
  install(FILES licenses/gpuocelot.txt
    DESTINATION licenses/unified COMPONENT support)
endif()
