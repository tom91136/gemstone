if(NOT HIP_INCLUDE_DIR OR NOT EXISTS "${HIP_INCLUDE_DIR}/hip/hip_runtime_api.h")
  message(FATAL_ERROR "HIP_INCLUDE_DIR must point at the pinned HIP headers containing hip/hip_runtime_api.h")
endif()
add_library(gemstone_hip_runtime SHARED
  src/runtime.cpp
  ../generated/hip/gemstone_hip_unsupported.cpp
)
set_target_properties(gemstone_hip_runtime PROPERTIES
  OUTPUT_NAME "amdhip64"
  SOVERSION 7
  BUILD_RPATH "$ORIGIN"
  INSTALL_RPATH "$ORIGIN")
target_include_directories(gemstone_hip_runtime PRIVATE
  ${HIP_INCLUDE_DIR}
  ${HSA_INCLUDE_DIR}
  ../hsa/include
  ../generated/hip)
target_compile_features(gemstone_hip_runtime PRIVATE cxx_std_17)
target_compile_definitions(gemstone_hip_runtime PRIVATE __HIP_PLATFORM_AMD__=1)
target_compile_options(gemstone_hip_runtime PRIVATE -Wall -Wextra -Werror -Wno-pedantic -Wno-attributes)
# A vendor libhsa-runtime64.so.1 loads before LD_PRELOAD and its SONAME would capture the HSA provider's internal calls,
# so HIP embeds a private provider and binds to it with -Bsymbolic.
target_link_libraries(gemstone_hip_runtime PRIVATE
  gemstone_zstd
  gemstone_hsa_runtime_embedded
  gemstone_hsa_core)
target_link_options(gemstone_hip_runtime PRIVATE "-Wl,-Bsymbolic")
gemstone_require_abi_implementations(
  TARGET gemstone_hip_runtime
  PLATFORM hip)

if(BUILD_TESTING)
  gemstone_memory_client(hip gemstone_hip_runtime)
  add_executable(gemstone_hip_fatbinary_exit_test test/fatbinary_exit.cpp)
  target_include_directories(gemstone_hip_fatbinary_exit_test PRIVATE ${HIP_INCLUDE_DIR})
  target_compile_definitions(gemstone_hip_fatbinary_exit_test PRIVATE __HIP_PLATFORM_AMD__=1)
  target_link_libraries(gemstone_hip_fatbinary_exit_test PRIVATE gemstone_hip_runtime)
  add_test(NAME hip/fatbinary-exit-lifetime COMMAND gemstone_hip_fatbinary_exit_test)
  gemstone_set_tests_properties(hip/fatbinary-exit-lifetime PROPERTIES TIMEOUT 10)
  add_executable(gemstone_hip_runtime_test test/hip_runtime_test.cpp)
  target_include_directories(gemstone_hip_runtime_test PRIVATE ${HIP_INCLUDE_DIR})
  target_compile_definitions(gemstone_hip_runtime_test PRIVATE __HIP_PLATFORM_AMD__=1)
  target_link_libraries(gemstone_hip_runtime_test PRIVATE gemstone_hip_runtime)
  gemstone_test_target(gemstone_hip_runtime_test)
  if(CMAKE_CXX_COMPILER_ID MATCHES "Clang|GNU")
    target_compile_options(gemstone_hip_runtime_test PRIVATE -Wno-attributes -Wno-pedantic -Wno-deprecated-declarations)
  endif()
  gemstone_discover_tests(gemstone_hip_runtime_test
    TEST_PREFIX "hip/"
    PROPERTIES TIMEOUT 60)
endif()
