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
  set_tests_properties(gemstone-opencl-smoke PROPERTIES TIMEOUT 30)
endif()

if(BUILD_TESTING)
  add_executable(gemstone_opencl_bounded_scheduler test/bounded_scheduler.cpp)
  target_include_directories(gemstone_opencl_bounded_scheduler PRIVATE "${OPENCL_INCLUDE_DIR}")
  target_compile_definitions(gemstone_opencl_bounded_scheduler PRIVATE CL_TARGET_OPENCL_VERSION=300)
  target_compile_options(gemstone_opencl_bounded_scheduler PRIVATE -UNDEBUG -Wall -Wextra -Wpedantic -Werror)
  target_link_libraries(gemstone_opencl_bounded_scheduler PRIVATE gemstone_opencl)
  add_test(NAME gemstone-opencl-bounded-scheduler COMMAND gemstone_opencl_bounded_scheduler)
  set_tests_properties(gemstone-opencl-bounded-scheduler PROPERTIES TIMEOUT 10)

  add_executable(gemstone_opencl_deferred test/deferred.cpp)
  target_include_directories(gemstone_opencl_deferred PRIVATE "${OPENCL_INCLUDE_DIR}")
  target_compile_definitions(gemstone_opencl_deferred PRIVATE CL_TARGET_OPENCL_VERSION=300)
  target_compile_options(gemstone_opencl_deferred PRIVATE -UNDEBUG -Wall -Wextra -Wpedantic -Werror)
  target_link_libraries(gemstone_opencl_deferred PRIVATE gemstone_opencl)
  foreach(boundary completion snapshot consecutive dependency-error out-of-order transfer remote-error error-no-event teardown validation
      command-copy command-fill command-read-rect command-write-rect command-copy-rect command-map command-unmap
      command-svm-copy command-svm-fill command-svm-map command-svm-unmap command-marker command-barrier
      command-migrate command-empty-kernel command-marker-ooo command-barrier-ooo
      command-map-error command-unmap-error command-svm-map-error command-svm-unmap-error command-intel-copy command-intel-fill command-intel-advise command-copy-release command-blocking-rect-ooo command-blocking-svm-ooo command-map-twice)
    add_test(NAME gemstone-opencl-${boundary} COMMAND gemstone_opencl_deferred ${boundary})
    set_tests_properties(gemstone-opencl-${boundary} PROPERTIES TIMEOUT 8)
  endforeach()
endif()
