add_library(gemstone_vulkan SHARED src/standalone.cpp src/unsupported.cpp ../spirv/specialization.cpp)
target_compile_features(gemstone_vulkan PRIVATE cxx_std_20)
target_compile_options(gemstone_vulkan PRIVATE -Wall -Wextra -Wpedantic -Werror)
target_include_directories(gemstone_vulkan PRIVATE ../spirv)
target_link_libraries(gemstone_vulkan PRIVATE gemstone_execution_client)
gemstone_require_abi_implementations(TARGET gemstone_vulkan PLATFORM vulkan FORCE_INCLUDE)
set_target_properties(gemstone_vulkan PROPERTIES
  OUTPUT_NAME vulkan
  VERSION 1
  SOVERSION 1
  LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/vulkan"
  RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/vulkan")

if(BUILD_TESTING)
  add_executable(gemstone_vulkan_runtime_test test/vulkan_runtime_test.cpp)
  gemstone_test_target(gemstone_vulkan_runtime_test)
  target_link_libraries(gemstone_vulkan_runtime_test PRIVATE gemstone_vulkan)
  gemstone_discover_tests(gemstone_vulkan_runtime_test)
endif()
