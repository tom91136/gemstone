set(OPENCL_INCLUDE_DIR "" CACHE PATH "Directory containing the Khronos OpenCL headers")
if(OPENCL_INCLUDE_DIR STREQUAL "" AND EXISTS "/opt/intel/oneapi/compiler/2026.1/include/CL/cl.h")
  set(OPENCL_INCLUDE_DIR "/opt/intel/oneapi/compiler/2026.1/include")
endif()
if(NOT EXISTS "${OPENCL_INCLUDE_DIR}/CL/cl.h" OR NOT EXISTS "${OPENCL_INCLUDE_DIR}/CL/cl_icd.h")
  include(FetchContent)
  FetchContent_Declare(opencl_headers
    URL https://github.com/KhronosGroup/OpenCL-Headers/archive/refs/tags/v2026.05.29.tar.gz
    URL_HASH SHA256=d9e6c48357de5002da11ce45de600e0c3ffe6ab4f628a3b9fe2b38603161658a
    SOURCE_SUBDIR gemstone-no-cmake-project
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
  )
  FetchContent_MakeAvailable(opencl_headers)
  set(OPENCL_INCLUDE_DIR "${opencl_headers_SOURCE_DIR}")
endif()

add_library(gemstone_opencl SHARED
  src/compiler.cpp
  src/icd.cpp
  ../spirv/specialization.cpp
  src/unsupported.cpp)
target_include_directories(gemstone_opencl PRIVATE
  "${OPENCL_INCLUDE_DIR}"
  "${CMAKE_CURRENT_SOURCE_DIR}/../generated/opencl"
)
target_compile_definitions(gemstone_opencl PRIVATE CL_TARGET_OPENCL_VERSION=300)
target_compile_features(gemstone_opencl PRIVATE cxx_std_20)
target_compile_options(gemstone_opencl PRIVATE -Wall -Wextra -Wpedantic -Werror -Wno-unused-parameter -Wno-deprecated-declarations)
target_link_libraries(gemstone_opencl PRIVATE gemstone_execution_client Aspartame::Aspartame)
target_link_options(gemstone_opencl PRIVATE -Wl,-Bsymbolic)
gemstone_require_abi_implementations(
  TARGET gemstone_opencl
  PLATFORM opencl
  FORCE_INCLUDE)
set_target_properties(gemstone_opencl PROPERTIES
  OUTPUT_NAME OpenCL_gemstone
  VERSION 1
  SOVERSION 1
)

if(BUILD_TESTING)
  gemstone_memory_client(opencl gemstone_opencl)
  function(gemstone_opencl_test target source)
    add_executable("${target}" "${source}")
    target_include_directories("${target}" PRIVATE "${OPENCL_INCLUDE_DIR}")
    target_compile_definitions("${target}" PRIVATE CL_TARGET_OPENCL_VERSION=300)
    target_compile_features("${target}" PRIVATE cxx_std_20)
    gemstone_test_target("${target}")
    target_link_libraries("${target}" PRIVATE gemstone_opencl ${ARGN})
  endfunction()

  gemstone_opencl_test(gemstone_opencl_smoke test/opencl_smoke.cpp)
  target_compile_definitions(gemstone_opencl_smoke PRIVATE GEMSTONE_OPENCL_TEST_SOURCE_DIR="${CMAKE_CURRENT_SOURCE_DIR}/test")
  gemstone_discover_tests(gemstone_opencl_smoke TEST_PREFIX "opencl/" PROPERTIES TIMEOUT 30)

  gemstone_opencl_test(gemstone_opencl_bounded_scheduler test/bounded_scheduler.cpp)
  gemstone_discover_tests(gemstone_opencl_bounded_scheduler TEST_PREFIX "opencl/" PROPERTIES TIMEOUT 10)

  gemstone_opencl_test(gemstone_opencl_deferred test/deferred.cpp)
  gemstone_discover_tests(gemstone_opencl_deferred TEST_PREFIX "opencl/" PROPERTIES TIMEOUT 8)

  set(sycl_client "${CMAKE_BINARY_DIR}/gemstone_memory_sycl")
  set(sycl_runtime "${CMAKE_BINARY_DIR}/opencl/memory-sycl/lib")
  set(sycl_build "${CMAKE_BINARY_DIR}/artifact-builds/memory-sycl")
  file(GENERATE OUTPUT "${CMAKE_BINARY_DIR}/gemstone_memory_sycl.library-path" CONTENT "${sycl_runtime}")
  file(GENERATE OUTPUT "${CMAKE_CURRENT_BINARY_DIR}/memory-sycl.cmake" CONTENT [=[
execute_process(COMMAND icpx -std=c++20 -fsycl -fsycl-device-code-split=per_kernel
  -ffile-prefix-map=/source=. /source/native/opencl/test/memory_sycl.cpp
  -o /output/gemstone_memory_sycl COMMAND_ERROR_IS_FATAL ANY)
]=])
  add_custom_target(gemstone_memory_sycl_client ALL
    COMMAND "${CMAKE_COMMAND}" -S "${CONFORMANCE_ARTIFACT_PROJECT}" -B "${sycl_build}" -G Ninja
      -DCONFORMANCE_ARTIFACT_KIND=command -DCONFORMANCE_BACKEND=sycl
      "-DCONFORMANCE_ARTIFACT_DIRECTORY=${CMAKE_BINARY_DIR}"
      "-DCONFORMANCE_COMMAND_SOURCE=${REPOSITORY_DIRECTORY}"
      "-DCONFORMANCE_COMMAND_SCRIPT=${CMAKE_CURRENT_BINARY_DIR}/memory-sycl.cmake"
      "-DCONFORMANCE_COMMAND_INPUTS=native/opencl/test/memory_sycl.cpp;native/rpc/test/memory_patterns.hpp;native/rpc/test/memory_test.hpp"
      -DCONFORMANCE_COMMAND_OUTPUTS=gemstone_memory_sycl
      -DCONFORMANCE_RUNTIME_DIRECTORY=opencl/memory-sycl/lib
      "-DCONTAINER_EXECUTABLE=${CONTAINER_EXECUTABLE}" "-DROCM_VERSION=${ROCM_VERSION}"
    COMMAND "${CMAKE_COMMAND}" --build "${sycl_build}" --target artifacts --parallel 1
    BYPRODUCTS "${sycl_client}" "${sycl_runtime}/.complete"
    JOB_POOL container_build VERBATIM)
endif()
