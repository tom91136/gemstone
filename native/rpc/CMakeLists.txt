find_package(Threads REQUIRED)

add_library(gemstone_execution_client STATIC
  ../generated/rpc/gemstone_execution_rpc.cpp
  completion.cpp
  connection.cpp)
set_target_properties(gemstone_execution_client PROPERTIES
  POSITION_INDEPENDENT_CODE ON
  CXX_VISIBILITY_PRESET hidden
  VISIBILITY_INLINES_HIDDEN YES)
target_include_directories(gemstone_execution_client PUBLIC
  "${CMAKE_CURRENT_SOURCE_DIR}"
  "${GEMSTONE_NATIVE_INCLUDE_DIRECTORY}"
  "${CMAKE_CURRENT_SOURCE_DIR}/../generated/rpc"
  "${CMAKE_CURRENT_SOURCE_DIR}/../generated")
target_link_libraries(gemstone_execution_client PUBLIC Threads::Threads glaze::glaze)
gemstone_project_target(gemstone_execution_client)

if(BUILD_TESTING)
  foreach(test_name IN ITEMS execution_rpc_smoke execution_rpc_session_loss request_bounds process_identity memory_ranges command_queue client_manager transfer environment completion)
    if(test_name MATCHES "^execution_rpc_")
      set(test_target "gemstone_${test_name}_client")
    else()
      set(test_target "gemstone_${test_name}_test")
    endif()
    add_executable("${test_target}" "test/${test_name}.cpp")
    target_link_libraries("${test_target}" PRIVATE gemstone_execution_client)
    gemstone_test_target("${test_target}")
    if(NOT test_name MATCHES "^execution_rpc_")
      gemstone_discover_tests("${test_target}" TEST_PREFIX "rpc/")
    endif()
  endforeach()
endif()
