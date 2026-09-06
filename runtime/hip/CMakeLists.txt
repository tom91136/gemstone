# SPDX-License-Identifier: Apache-2.0
cmake_minimum_required(VERSION 3.25)
project(gemstone-runtime-hip LANGUAGES CXX)

# HIP is deliberately built on top of the public HSA provider. Keeping the
# two providers in one CMake build gives the launcher a pair of ABI-correct
# DSOs while ensuring the HIP path exercises the same HSA path as a native HSA
# client.
set(GEMSTONE_HIP_BUILD_TESTING OFF)
if(BUILD_TESTING)
  set(GEMSTONE_HIP_BUILD_TESTING ON)
endif()
set(BUILD_TESTING OFF CACHE BOOL "Disable nested HSA tests" FORCE)
add_subdirectory(../hsa hsa-runtime)
set(BUILD_TESTING ${GEMSTONE_HIP_BUILD_TESTING} CACHE BOOL "Build HIP tests" FORCE)

find_path(HIP_INCLUDE_DIR hip/hip_runtime_api.h
  PATHS /usr/include /usr/local/include
  REQUIRED)
find_library(ZSTD_LIBRARY zstd REQUIRED)

# Keep the HIP drop-in self-contained.  A vendor HIP binary normally brings
# `libhsa-runtime64.so.1` into the process before LD_PRELOAD is considered;
# linking our HSA facade as a shared dependency would therefore resolve the
# provider's internal calls against that vendor SONAME.  Embedding the
# provider implementation avoids the collision and keeps HIP and direct HSA
# on the same virtual runtime.
add_library(gemstone_hip_runtime SHARED
  src/hip_runtime.cpp
  ../hsa/src/hsa_runtime.cpp
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
  ../../native/execution)
target_compile_features(gemstone_hip_runtime PRIVATE cxx_std_17)
target_compile_definitions(gemstone_hip_runtime PRIVATE __HIP_PLATFORM_AMD__=1)
target_compile_options(gemstone_hip_runtime PRIVATE -Wall -Wextra -Werror -Wno-pedantic)
target_link_libraries(gemstone_hip_runtime PRIVATE gemstone_hsa_doorbell ${ZSTD_LIBRARY})
target_link_options(gemstone_hip_runtime PRIVATE
  "-Wl,-Bsymbolic"
  "-Wl,--version-script=${CMAKE_CURRENT_SOURCE_DIR}/abi/amdhip64.map")

include(CTest)
if(BUILD_TESTING)
  add_executable(gemstone_hip_runtime_test tests/hip_runtime_test.cpp)
  target_include_directories(gemstone_hip_runtime_test PRIVATE ${HIP_INCLUDE_DIR} include)
  target_compile_definitions(gemstone_hip_runtime_test PRIVATE __HIP_PLATFORM_AMD__=1)
  target_link_libraries(gemstone_hip_runtime_test PRIVATE gemstone_hip_runtime)
  add_test(NAME gemstone-hip-runtime COMMAND gemstone_hip_runtime_test)
endif()
