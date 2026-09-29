set(CMAKE_CXX_STANDARD 23)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

set(LEVEL_ZERO_INCLUDE_DIR "" CACHE PATH "Directory containing ze_api.h and ze_ddi.h")
if(NOT EXISTS "${LEVEL_ZERO_INCLUDE_DIR}/ze_ddi.h")
  include(FetchContent)
  if(NOT LEVEL_ZERO_SOURCE_URL OR NOT LEVEL_ZERO_SOURCE_SHA256)
    message(FATAL_ERROR "LEVEL_ZERO_SOURCE_URL and LEVEL_ZERO_SOURCE_SHA256 must be supplied by the build")
  endif()
  FetchContent_Declare(level_zero_headers
    URL "${LEVEL_ZERO_SOURCE_URL}"
    URL_HASH "SHA256=${LEVEL_ZERO_SOURCE_SHA256}"
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
  include("${CMAKE_CURRENT_LIST_DIR}/../cmake/LevelZeroLoader.cmake")
  gemstone_add_level_zero_loader()

  gemstone_memory_client(levelzero gemstone_levelzero)
  set_target_properties(gemstone_memory_levelzero PROPERTIES BUILD_RPATH "$ORIGIN")

  add_executable(gemstone_levelzero_smoke test/levelzero_smoke.cpp)
  target_include_directories(gemstone_levelzero_smoke PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}")
  target_link_libraries(gemstone_levelzero_smoke PRIVATE ze_loader)
  add_dependencies(gemstone_levelzero_smoke level_zero_loader)
  set_target_properties(gemstone_levelzero_smoke PROPERTIES BUILD_RPATH "$ORIGIN")
  gemstone_test_target(gemstone_levelzero_smoke)
  gemstone_discover_tests(gemstone_levelzero_smoke TEST_PREFIX "level-zero/" DISCOVERY_MODE PRE_TEST
    PROPERTIES ENVIRONMENT "ZE_ENABLE_ALT_DRIVERS=$<TARGET_FILE:gemstone_levelzero>")

  add_executable(gemstone_levelzero_rpc_test test/execution_rpc_test.cpp)
  target_include_directories(gemstone_levelzero_rpc_test PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}")
  target_link_libraries(gemstone_levelzero_rpc_test PRIVATE ze_loader pthread)
  add_dependencies(gemstone_levelzero_rpc_test level_zero_loader)
  set_target_properties(gemstone_levelzero_rpc_test PROPERTIES BUILD_RPATH "$ORIGIN")
  gemstone_test_target(gemstone_levelzero_rpc_test)
  gemstone_discover_tests(gemstone_levelzero_rpc_test TEST_PREFIX "level-zero/" DISCOVERY_MODE PRE_TEST
    PROPERTIES TIMEOUT 30
    ENVIRONMENT "ZE_ENABLE_ALT_DRIVERS=$<TARGET_FILE:gemstone_levelzero>")
endif()
