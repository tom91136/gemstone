set(VULKAN_INCLUDE_DIR "" CACHE PATH "Directory containing vulkan/vulkan.h")
if(NOT EXISTS "${VULKAN_INCLUDE_DIR}/vulkan/vulkan.h")
  include(FetchContent)
  if(NOT VULKAN_HEADERS_SOURCE_URL OR NOT VULKAN_HEADERS_SOURCE_SHA256)
    message(FATAL_ERROR "VULKAN_HEADERS_SOURCE_URL and VULKAN_HEADERS_SOURCE_SHA256 must be supplied by the build")
  endif()
  FetchContent_Declare(vulkan_headers
    URL "${VULKAN_HEADERS_SOURCE_URL}"
    URL_HASH "SHA256=${VULKAN_HEADERS_SOURCE_SHA256}"
    SOURCE_SUBDIR gemstone-no-cmake-project
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE)
  FetchContent_MakeAvailable(vulkan_headers)
  set(VULKAN_INCLUDE_DIR "${vulkan_headers_SOURCE_DIR}/include")
endif()

add_library(gemstone_vulkan SHARED
  src/device.cpp
  src/command_buffer.cpp
  src/objects.cpp
  src/resources.cpp
  src/graphics_commands.cpp
  src/graphics.cpp
  src/state.cpp
  src/handles.cpp
  src/queue.cpp
  src/query.cpp
  src/host_allocator.cpp
  src/descriptor.cpp
  src/physical_device.cpp
  src/standalone.cpp
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
  add_executable(gemstone_vulkan_runtime_test test/vulkan_runtime_test.cpp)
  target_include_directories(gemstone_vulkan_runtime_test PRIVATE "${VULKAN_INCLUDE_DIR}" "${CMAKE_CURRENT_SOURCE_DIR}/../generated/spirv")
  # Keep generated layouts separate from the frozen codecs' overlapping C++ names.
  add_executable(gemstone_vulkan_formats_test test/image_formats.cpp)
  target_include_directories(gemstone_vulkan_formats_test PRIVATE "${VULKAN_INCLUDE_DIR}" "${CMAKE_CURRENT_SOURCE_DIR}/../generated/spirv")
  target_link_libraries(gemstone_vulkan_formats_test PRIVATE gemstone_execution_client)
  gemstone_test_target(gemstone_vulkan_formats_test)
  gemstone_discover_tests(gemstone_vulkan_formats_test)
  add_executable(gemstone_graphics_reference test/graphics_reference.cpp)
  target_compile_features(gemstone_graphics_reference PRIVATE cxx_std_20)
  target_include_directories(gemstone_graphics_reference PRIVATE "${VULKAN_INCLUDE_DIR}" "${CMAKE_CURRENT_SOURCE_DIR}/../generated/spirv")
  target_link_libraries(gemstone_graphics_reference PRIVATE gemstone_execution_client gemstone_etc_reference)
  gemstone_test_target(gemstone_vulkan_runtime_test)
  target_link_libraries(gemstone_vulkan_runtime_test PRIVATE gemstone_vulkan gemstone_execution_client gemstone_etc_reference)
  gemstone_discover_tests(gemstone_vulkan_runtime_test TEST_SPEC "~[graphics-execution]")
endif()

# Redirect implementation allocations at compile time, including STL container
# storage, without exposing a process-wide operator new replacement.
target_compile_options(gemstone_vulkan PRIVATE
  "SHELL:-include ${CMAKE_CURRENT_SOURCE_DIR}/src/host_allocator_redirect.hpp")
set_source_files_properties(src/host_allocator.cpp PROPERTIES
  COMPILE_DEFINITIONS GEMSTONE_VULKAN_HOST_IMPLEMENTATION)
# Keep exported template definitions local on ELF; Mach-O uses two-level lookup.
if(UNIX AND NOT APPLE)
  target_link_options(gemstone_vulkan PRIVATE -Wl,-Bsymbolic-functions)
endif()
