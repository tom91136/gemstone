if(NOT EXISTS "${VULKAN_INCLUDE_DIR}/vulkan/vulkan.h")
  message(FATAL_ERROR "VULKAN_INCLUDE_DIR must point at the pinned Vulkan headers")
endif()

add_library(gemstone_vulkan SHARED
  src/device.cpp
  src/command_buffer.cpp
  src/objects.cpp
  src/resources.cpp
  src/execute.cpp
  src/graphics.cpp
  src/state.cpp
  src/handles.cpp
  src/queue.cpp
  src/query.cpp
  src/host_allocator.cpp
  src/descriptor.cpp
  src/physical_device.cpp
  src/entrypoints.cpp
  src/shader.cpp
  src/submission.cpp
  src/capabilities.cpp
  ../generated/vulkan/gemstone_vulkan_unsupported.cpp)
target_compile_features(gemstone_vulkan PRIVATE cxx_std_20)
target_compile_options(gemstone_vulkan PRIVATE -Wall -Wextra -Wpedantic -Werror)
target_include_directories(gemstone_vulkan PRIVATE "${VULKAN_INCLUDE_DIR}" "${CMAKE_CURRENT_SOURCE_DIR}/../generated/spirv")
target_link_libraries(gemstone_vulkan PRIVATE gemstone_execution_client)
gemstone_require_abi_implementations(TARGET gemstone_vulkan PLATFORM vulkan FORCE_INCLUDE)
set_target_properties(gemstone_vulkan PROPERTIES
  CXX_VISIBILITY_PRESET hidden
  VISIBILITY_INLINES_HIDDEN YES
  OUTPUT_NAME vulkan
  VERSION 1
  SOVERSION 1
  LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/vulkan"
  RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/vulkan")

if(BUILD_TESTING)
  add_library(gemstone_etc_reference STATIC third_party/swiftshader/ETC_Decoder.cpp)
  target_compile_features(gemstone_etc_reference PRIVATE cxx_std_20)
  target_compile_options(gemstone_etc_reference PRIVATE -Wno-pedantic)
  add_executable(gemstone_vulkan_runtime_test test/vulkan_runtime_test.cpp src/descriptor.cpp)
  target_include_directories(gemstone_vulkan_runtime_test PRIVATE "${VULKAN_INCLUDE_DIR}" "${CMAKE_CURRENT_SOURCE_DIR}/../generated/spirv")
  add_executable(gemstone_graphics_reference test/graphics_reference.cpp)
  target_compile_features(gemstone_graphics_reference PRIVATE cxx_std_20)
  target_include_directories(gemstone_graphics_reference PRIVATE "${VULKAN_INCLUDE_DIR}" "${CMAKE_CURRENT_SOURCE_DIR}/../generated/spirv")
  target_link_libraries(gemstone_graphics_reference PRIVATE gemstone_execution_client gemstone_etc_reference)
  gemstone_test_target(gemstone_vulkan_runtime_test)
  target_link_libraries(gemstone_vulkan_runtime_test PRIVATE gemstone_vulkan gemstone_execution_client)
  gemstone_discover_tests(gemstone_vulkan_runtime_test TEST_SPEC "~[graphics-execution]")
  add_executable(gemstone_vulkan_process_exit test/process_exit.cpp)
  target_include_directories(gemstone_vulkan_process_exit PRIVATE "${VULKAN_INCLUDE_DIR}")
  target_link_libraries(gemstone_vulkan_process_exit PRIVATE gemstone_vulkan)
  add_test(NAME vulkan/process-exit COMMAND gemstone_vulkan_process_exit)
  set_tests_properties(vulkan/process-exit PROPERTIES TIMEOUT 10)
endif()

target_compile_options(gemstone_vulkan PRIVATE
  "SHELL:-include ${CMAKE_CURRENT_SOURCE_DIR}/src/host_allocator_redirect.hpp")
# GCC 13 crashes demangling the unmangled asm labels when it matches new and delete
if(CMAKE_CXX_COMPILER_ID STREQUAL "GNU" AND CMAKE_CXX_COMPILER_VERSION VERSION_LESS 14)
  target_compile_options(gemstone_vulkan PRIVATE -Wno-mismatched-new-delete -Wno-mismatched-dealloc -Wno-free-nonheap-object)
endif()
set_source_files_properties(src/host_allocator.cpp PROPERTIES
  COMPILE_DEFINITIONS GEMSTONE_VULKAN_HOST_IMPLEMENTATION)
target_link_options(gemstone_vulkan PRIVATE -Wl,-Bsymbolic-functions)
