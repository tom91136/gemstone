if(NOT EXISTS "${LEVEL_ZERO_INCLUDE_DIR}/ze_ddi.h")
  message(FATAL_ERROR "LEVEL_ZERO_INCLUDE_DIR must point at the pinned Level Zero headers")
endif()

add_library(gemstone_levelzero SHARED src/driver.cpp)
target_include_directories(gemstone_levelzero SYSTEM PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}")
target_include_directories(gemstone_levelzero PRIVATE ../generated/levelzero)
gemstone_project_target(gemstone_levelzero)
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
  target_include_directories(gemstone_levelzero_smoke SYSTEM PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}")
  target_link_libraries(gemstone_levelzero_smoke PRIVATE ze_loader)
  add_dependencies(gemstone_levelzero_smoke level_zero_loader)
  set_target_properties(gemstone_levelzero_smoke PROPERTIES BUILD_RPATH "$ORIGIN")
  gemstone_test_target(gemstone_levelzero_smoke)
  gemstone_discover_tests(gemstone_levelzero_smoke TEST_PREFIX "level-zero/" DISCOVERY_MODE PRE_TEST
    PROPERTIES ENVIRONMENT "ZE_ENABLE_ALT_DRIVERS=$<TARGET_FILE:gemstone_levelzero>")

  add_executable(gemstone_levelzero_rpc_test test/execution_rpc_test.cpp)
  target_include_directories(gemstone_levelzero_rpc_test SYSTEM PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}")
  target_link_libraries(gemstone_levelzero_rpc_test PRIVATE ze_loader pthread)
  add_dependencies(gemstone_levelzero_rpc_test level_zero_loader)
  set_target_properties(gemstone_levelzero_rpc_test PROPERTIES BUILD_RPATH "$ORIGIN")
  gemstone_test_target(gemstone_levelzero_rpc_test)
  gemstone_discover_tests(gemstone_levelzero_rpc_test TEST_PREFIX "level-zero/" DISCOVERY_MODE PRE_TEST
    PROPERTIES TIMEOUT 30
    ENVIRONMENT "ZE_ENABLE_ALT_DRIVERS=$<TARGET_FILE:gemstone_levelzero>")

  add_executable(gemstone_levelzero_process_exit test/process_exit.cpp)
  gemstone_project_target(gemstone_levelzero_process_exit)
  target_include_directories(gemstone_levelzero_process_exit SYSTEM PRIVATE "${LEVEL_ZERO_INCLUDE_DIR}")
  target_compile_definitions(gemstone_levelzero_process_exit PRIVATE GEMSTONE_LEVEL_ZERO_DRIVER="$<TARGET_FILE:gemstone_levelzero>")
  target_link_libraries(gemstone_levelzero_process_exit PRIVATE ze_loader ${CMAKE_DL_LIBS})
  add_dependencies(gemstone_levelzero_process_exit level_zero_loader gemstone_levelzero)
  set_target_properties(gemstone_levelzero_process_exit PROPERTIES BUILD_RPATH "$ORIGIN")
  add_test(NAME level-zero/process-exit COMMAND gemstone_levelzero_process_exit)
  gemstone_set_tests_properties(level-zero/process-exit PROPERTIES TIMEOUT 10
    ENVIRONMENT "ZE_ENABLE_ALT_DRIVERS=$<TARGET_FILE:gemstone_levelzero>")
endif()
