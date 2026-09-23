find_path(HIP_INCLUDE_DIR hip/hip_runtime_api.h
  PATHS /usr/include /usr/local/include
  REQUIRED)
# Keep the HIP drop-in self-contained.  A vendor HIP binary normally brings
# `libhsa-runtime64.so.1` into the process before LD_PRELOAD is considered;
# linking our HSA facade as a shared dependency would therefore resolve the
# provider's internal calls against that vendor SONAME.  Embedding the
# provider implementation avoids the collision and keeps HIP and direct HSA
# on the same virtual runtime.
add_library(gemstone_hip_runtime SHARED
  src/runtime.cpp
  src/unsupported.cpp
)
set_target_properties(gemstone_hip_runtime PROPERTIES
  OUTPUT_NAME "amdhip64"
  SOVERSION 7
  BUILD_RPATH "$ORIGIN"
  INSTALL_RPATH "$ORIGIN")
target_include_directories(gemstone_hip_runtime PRIVATE
  ${HIP_INCLUDE_DIR}
  ${HSA_INCLUDE_DIR}
  include
  ../hsa/include
  ../generated/hip)
target_compile_features(gemstone_hip_runtime PRIVATE cxx_std_17)
target_compile_definitions(gemstone_hip_runtime PRIVATE __HIP_PLATFORM_AMD__=1)
target_compile_options(gemstone_hip_runtime PRIVATE -Wall -Wextra -Werror -Wno-pedantic -Wno-attributes)
set_source_files_properties(src/unsupported.cpp PROPERTIES
  COMPILE_OPTIONS "-Wno-deprecated-declarations")
target_link_libraries(gemstone_hip_runtime PRIVATE
  gemstone_zstd
  gemstone_hsa_runtime_embedded
  gemstone_hsa_core
  Aspartame::Aspartame)
target_link_options(gemstone_hip_runtime PRIVATE "-Wl,-Bsymbolic")
gemstone_require_abi_implementations(
  TARGET gemstone_hip_runtime
  PLATFORM hip)

if(BUILD_TESTING)
  add_executable(gemstone_hip_runtime_test test/hip_runtime_test.cpp)
  target_include_directories(gemstone_hip_runtime_test PRIVATE ${HIP_INCLUDE_DIR} include)
  target_compile_definitions(gemstone_hip_runtime_test PRIVATE __HIP_PLATFORM_AMD__=1)
  target_link_libraries(gemstone_hip_runtime_test PRIVATE gemstone_hip_runtime)
  gemstone_test_target(gemstone_hip_runtime_test)
  if(CMAKE_CXX_COMPILER_ID MATCHES "Clang|GNU")
    # Current upstream HIP headers place GNU attributes before extern "C".
    target_compile_options(gemstone_hip_runtime_test PRIVATE -Wno-attributes -Wno-pedantic -Wno-deprecated-declarations)
  endif()
  gemstone_discover_tests(gemstone_hip_runtime_test
    TEST_PREFIX "hip/"
    PROPERTIES TIMEOUT 60)
endif()
