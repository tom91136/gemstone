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

add_library(gemstone_vulkan SHARED src/standalone.cpp src/unsupported.cpp)
target_compile_features(gemstone_vulkan PRIVATE cxx_std_20)
target_compile_options(gemstone_vulkan PRIVATE -Wall -Wextra -Wpedantic -Werror)
target_include_directories(gemstone_vulkan PRIVATE "${VULKAN_INCLUDE_DIR}")
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
  target_include_directories(gemstone_vulkan_runtime_test PRIVATE "${VULKAN_INCLUDE_DIR}")
  gemstone_test_target(gemstone_vulkan_runtime_test)
  target_link_libraries(gemstone_vulkan_runtime_test PRIVATE gemstone_vulkan)
  gemstone_discover_tests(gemstone_vulkan_runtime_test)
endif()
