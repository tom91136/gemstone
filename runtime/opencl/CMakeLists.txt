cmake_minimum_required(VERSION 3.25)

project(gemstone_opencl_icd LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

set(OPENCL_INCLUDE_DIR "" CACHE PATH "Directory containing the Khronos OpenCL headers")
if(OPENCL_INCLUDE_DIR STREQUAL "" AND EXISTS "/opt/intel/oneapi/compiler/2026.1/include/CL/cl.h")
  set(OPENCL_INCLUDE_DIR "/opt/intel/oneapi/compiler/2026.1/include")
endif()
if(NOT EXISTS "${OPENCL_INCLUDE_DIR}/CL/cl.h" OR NOT EXISTS "${OPENCL_INCLUDE_DIR}/CL/cl_icd.h")
  include(FetchContent)
  FetchContent_Declare(opencl_headers
    URL https://github.com/KhronosGroup/OpenCL-Headers/archive/refs/tags/v2025.07.22.tar.gz
    SOURCE_SUBDIR gemstone-no-cmake-project
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
  )
  FetchContent_MakeAvailable(opencl_headers)
  set(OPENCL_INCLUDE_DIR "${opencl_headers_SOURCE_DIR}")
endif()

add_library(gemstone_opencl SHARED
  src/gemstone_opencl_icd.cpp
  ../../native/execution/gemstone_execution_rpc.cpp
)
target_include_directories(gemstone_opencl PRIVATE
  "${OPENCL_INCLUDE_DIR}"
  ../../native/execution
)
target_compile_definitions(gemstone_opencl PRIVATE CL_TARGET_OPENCL_VERSION=300)
target_compile_options(gemstone_opencl PRIVATE -Wall -Wextra -Wpedantic -Werror -Wno-unused-parameter -Wno-deprecated-declarations)
target_link_libraries(gemstone_opencl PRIVATE pthread)
target_link_options(gemstone_opencl PRIVATE -Wl,-Bsymbolic)
set_target_properties(gemstone_opencl PROPERTIES
  OUTPUT_NAME OpenCL_gemstone
  VERSION 1
  SOVERSION 1
)

include(CTest)
if(BUILD_TESTING)
  add_executable(gemstone_opencl_smoke test/opencl_smoke.cpp)
  target_include_directories(gemstone_opencl_smoke PRIVATE "${OPENCL_INCLUDE_DIR}")
  target_compile_definitions(gemstone_opencl_smoke PRIVATE CL_TARGET_OPENCL_VERSION=300)
  target_compile_options(gemstone_opencl_smoke PRIVATE -UNDEBUG -Wall -Wextra -Wpedantic -Werror)
  target_link_libraries(gemstone_opencl_smoke PRIVATE gemstone_opencl)
  add_test(NAME gemstone-opencl-smoke COMMAND gemstone_opencl_smoke)
endif()
