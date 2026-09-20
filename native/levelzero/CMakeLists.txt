set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

set(LEVEL_ZERO_INCLUDE_DIR "" CACHE PATH "Directory containing ze_api.h and ze_ddi.h")
if(NOT EXISTS "${LEVEL_ZERO_INCLUDE_DIR}/ze_ddi.h")
  include(FetchContent)
  FetchContent_Declare(level_zero_headers
    URL https://github.com/oneapi-src/level-zero/archive/refs/tags/v1.33.1.tar.gz
    URL_HASH SHA256=e0274918e10d788041aa79e166699d0975de9adf1b0b650ee02c123f81f0533f
    SOURCE_SUBDIR gemstone-no-cmake-project
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
  )
  FetchContent_MakeAvailable(level_zero_headers)
  set(LEVEL_ZERO_INCLUDE_DIR "${level_zero_headers_SOURCE_DIR}/include")
endif()

add_library(gemstone_levelzero SHARED src/driver.cpp)
target_include_directories(gemstone_levelzero
  PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}" ../generated/levelzero
)
target_compile_options(gemstone_levelzero PRIVATE
  -Wall -Wextra -Wpedantic -Werror
  -Wno-unused-parameter
)
target_link_libraries(gemstone_levelzero PRIVATE gemstone_execution_client)
gemstone_require_abi_implementations(
  TARGET gemstone_levelzero
  PLATFORM levelzero
  FORCE_INCLUDE)
set_target_properties(gemstone_levelzero PROPERTIES
  OUTPUT_NAME ze_gemstone
  VERSION 1
  SOVERSION 1
  CXX_VISIBILITY_PRESET hidden
  VISIBILITY_INLINES_HIDDEN YES
)

if(BUILD_TESTING)
  find_library(LEVEL_ZERO_LOADER_LIBRARY NAMES ze_loader libze_loader.so.1)
  if(NOT LEVEL_ZERO_LOADER_LIBRARY)
    message(STATUS "Level Zero loader unavailable; loader-facing component tests are not applicable")
  else()
  add_executable(gemstone_levelzero_smoke test/levelzero_smoke.cpp)
  target_include_directories(gemstone_levelzero_smoke PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}")
  target_link_libraries(gemstone_levelzero_smoke PRIVATE "${LEVEL_ZERO_LOADER_LIBRARY}")
  gemstone_test_target(gemstone_levelzero_smoke)
  gemstone_discover_tests(gemstone_levelzero_smoke TEST_PREFIX "level-zero/"
    PROPERTIES ENVIRONMENT "ZE_ENABLE_ALT_DRIVERS=$<TARGET_FILE:gemstone_levelzero>;ZE_ENABLE_LOADER_DEBUG_TRACE=0")

  add_executable(gemstone_levelzero_rpc_test test/execution_rpc_test.cpp)
  target_include_directories(gemstone_levelzero_rpc_test PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}")
  target_link_libraries(gemstone_levelzero_rpc_test PRIVATE "${LEVEL_ZERO_LOADER_LIBRARY}" pthread)
  gemstone_test_target(gemstone_levelzero_rpc_test)
  gemstone_discover_tests(gemstone_levelzero_rpc_test TEST_PREFIX "level-zero/"
    PROPERTIES TIMEOUT 30
    ENVIRONMENT "ZE_ENABLE_ALT_DRIVERS=$<TARGET_FILE:gemstone_levelzero>;ZE_ENABLE_LOADER_DEBUG_TRACE=0")
  endif()
endif()
