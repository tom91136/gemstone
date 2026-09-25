find_package(Threads REQUIRED)

add_library(gemstone_execution_client STATIC
  ../generated/rpc/gemstone_execution_rpc.cpp
  completion.cpp)
set_target_properties(gemstone_execution_client PROPERTIES
  POSITION_INDEPENDENT_CODE ON
  CXX_VISIBILITY_PRESET hidden
  VISIBILITY_INLINES_HIDDEN YES)
target_include_directories(gemstone_execution_client PUBLIC
  "${CMAKE_CURRENT_SOURCE_DIR}"
  "${CMAKE_CURRENT_SOURCE_DIR}/../generated/rpc")
target_compile_features(gemstone_execution_client PUBLIC cxx_std_17)
target_link_libraries(gemstone_execution_client PUBLIC Threads::Threads PRIVATE fmt::fmt-header-only)
if(CMAKE_CXX_COMPILER_ID MATCHES "Clang|GNU")
  target_compile_options(gemstone_execution_client PRIVATE -Wall -Wextra -Wpedantic -Werror)
endif()

if(BUILD_TESTING)
  foreach(test_name IN ITEMS execution_rpc_smoke execution_rpc_session_loss request_bounds process_identity memory_ranges command_queue)
    if(test_name MATCHES "^execution_rpc_")
      set(test_target "gemstone_${test_name}_client")
    else()
      set(test_target "gemstone_${test_name}_test")
    endif()
    add_executable("${test_target}" "test/${test_name}.cpp")
    target_link_libraries("${test_target}" PRIVATE gemstone_execution_client)
    gemstone_test_target("${test_target}")
    if(test_name STREQUAL "request_bounds" OR test_name STREQUAL "process_identity" OR test_name STREQUAL "memory_ranges" OR test_name STREQUAL "command_queue")
      gemstone_discover_tests("${test_target}" TEST_PREFIX "rpc/")
    endif()
  endforeach()
endif()
