if(NOT EXISTS "${OPENCL_INCLUDE_DIR}/CL/cl.h" OR NOT EXISTS "${OPENCL_INCLUDE_DIR}/CL/cl_icd.h")
  message(FATAL_ERROR "OPENCL_INCLUDE_DIR must point at the pinned OpenCL headers")
endif()

add_library(gemstone_opencl SHARED
  src/compiler.cpp
  src/icd.cpp
  ../generated/opencl/gemstone_opencl_unsupported.cpp)
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

  add_executable(gemstone_opencl_process_exit test/process_exit.cpp)
  target_include_directories(gemstone_opencl_process_exit PRIVATE "${OPENCL_INCLUDE_DIR}")
  target_compile_definitions(gemstone_opencl_process_exit PRIVATE CL_TARGET_OPENCL_VERSION=300)
  target_compile_features(gemstone_opencl_process_exit PRIVATE cxx_std_20)
  target_link_libraries(gemstone_opencl_process_exit PRIVATE gemstone_opencl)
  add_test(NAME opencl/process-exit COMMAND gemstone_opencl_process_exit)
  set_tests_properties(opencl/process-exit PROPERTIES TIMEOUT 10)
endif()
