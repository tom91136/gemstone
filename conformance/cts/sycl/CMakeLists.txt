# These categories are comparatively small and cover fundamental runtime
# objects. Generated type matrices and exhaustive upstream mode are omitted.
set(sycl_cts_categories
  item range accessor_basic atomic_fence bit_cast buffer context device device_event device_selector error event
  exception_handling function_objects group h_item handler id is_device_copyable
  host_accessor kernel kernel_args language local_accessor namespace nd_item nd_range platform pointers
  property queue sub_group sycl_external usm)

find_program(sycl_compiler NAMES icpx clang++ REQUIRED)
if(NOT "${CMAKE_CXX_COMPILER}" STREQUAL "${sycl_compiler}")
  message(FATAL_ERROR "SYCL CTS must use the project C++ compiler: ${CMAKE_CXX_COMPILER}")
endif()
conformance_fetch_url(conformance_sycl_cts sycl_cts_source)
conformance_fetch_url(conformance_sycl_cts_opencl_headers sycl_cts_opencl_headers_source)

set(staged_source "${CMAKE_CURRENT_BINARY_DIR}/source")
file(REMOVE_RECURSE "${staged_source}")
file(COPY "${sycl_cts_source}/" DESTINATION "${staged_source}")
file(MAKE_DIRECTORY "${staged_source}/vendor/OpenCL-Headers")
file(COPY "${sycl_cts_opencl_headers_source}/"
  DESTINATION "${staged_source}/vendor/OpenCL-Headers")

# The conformance project already provides Catch2. Keep the CTS from adding its
# vendored copy and creating duplicate Catch2 and Catch2WithMain targets.
set(sycl_cts_cmake "${staged_source}/CMakeLists.txt")
file(READ "${sycl_cts_cmake}" sycl_cts_cmake_content)
set(vendored_catch "add_submodule_directory(vendor/Catch2)")
string(FIND "${sycl_cts_cmake_content}" "${vendored_catch}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS Catch2 integration point changed")
endif()
string(REPLACE "${vendored_catch}" "" sycl_cts_cmake_content "${sycl_cts_cmake_content}")
file(WRITE "${sycl_cts_cmake}" "${sycl_cts_cmake_content}")

# The pinned CTS predates the SYCL 2020 names shipped by current oneAPI.
set(device_info "${staged_source}/tests/device/device_info.cpp")
file(READ "${device_info}" device_info_content)
foreach(width IN ITEMS preferred native)
  set(previous "${width}_vector_width_long_long")
  set(current "${width}_vector_width_long")
  string(FIND "${device_info_content}" "${previous}" integration_point)
  if(integration_point EQUAL -1)
    message(FATAL_ERROR "Pinned SYCL CTS ${previous} integration point changed")
  endif()
  string(REPLACE "${previous}" "${current}" device_info_content "${device_info_content}")
endforeach()
file(WRITE "${device_info}" "${device_info_content}")

# Correct an invalid relative include in the pinned CTS source.
set(queue_properties "${staged_source}/tests/queue/queue_properties.cpp")
file(READ "${queue_properties}" queue_properties_content)
set(previous [[#include "../../../util/sycl_exceptions.h"]])
set(current [[#include "../../util/sycl_exceptions.h"]])
string(FIND "${queue_properties_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS queue properties integration point changed")
endif()
string(REPLACE "${previous}" "${current}" queue_properties_content "${queue_properties_content}")
file(WRITE "${queue_properties}" "${queue_properties_content}")

# The pinned buffer tests have the same invalid relative include.
set(buffer_api "${staged_source}/tests/buffer/buffer_api_common.h")
file(READ "${buffer_api}" buffer_api_content)
set(previous [[#include "../../../util/sycl_exceptions.h"]])
set(current [[#include "../../util/sycl_exceptions.h"]])
string(FIND "${buffer_api_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS buffer API integration point changed")
endif()
string(REPLACE "${previous}" "${current}" buffer_api_content "${buffer_api_content}")
file(WRITE "${buffer_api}" "${buffer_api_content}")

# The pinned USM CTS repeats the atomic64 case name for the core type case.
# Catch2 can execute both, but the conformance inventory requires unique names.
set(usm_atomic_core "${staged_source}/tests/usm/usm_atomic_access_core.cpp")
file(READ "${usm_atomic_core}" usm_atomic_core_content)
set(previous [[TEST_CASE("Tests for usm atomics with atomic64 support", "[usm][atomic]")]])
set(current [[TEST_CASE("Tests for usm atomics with core types", "[usm][atomic]")]])
string(FIND "${usm_atomic_core_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS USM atomic case integration point changed")
endif()
string(REPLACE "${previous}" "${current}" usm_atomic_core_content "${usm_atomic_core_content}")
file(WRITE "${usm_atomic_core}" "${usm_atomic_core_content}")

set(adapter "${staged_source}/cmake/AdaptDPCPP.cmake")
file(READ "${adapter}" adapter_content)
set(before [=[add_executable(${exe_name} $<TARGET_OBJECTS:${object_lib_name}>)]=])
string(FIND "${adapter_content}" "${before}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS integration point changed in ${adapter}")
endif()
set(after [=[add_executable(${exe_name} $<TARGET_OBJECTS:${object_lib_name}>)
string(REGEX REPLACE "^test_" "" conformance_category "${exe_name}")
set_target_properties(${exe_name} PROPERTIES OUTPUT_NAME "sycl_cts_${conformance_category}")]=])
string(REPLACE "${before}" "${after}" adapter_content "${adapter_content}")
file(WRITE "${adapter}" "${adapter_content}")

file(GLOB children RELATIVE "${sycl_cts_source}/tests" "${sycl_cts_source}/tests/*")
set(excluded)
foreach(child IN LISTS children)
  if(IS_DIRECTORY "${sycl_cts_source}/tests/${child}" AND
      NOT child STREQUAL common AND NOT child IN_LIST sycl_cts_categories)
    list(APPEND excluded "${child}")
  endif()
endforeach()
list(JOIN excluded "\n" excluded_text)
file(WRITE "${CMAKE_CURRENT_BINARY_DIR}/excluded.txt" "${excluded_text}\n")

set(SYCL_IMPLEMENTATION DPCPP CACHE STRING "" FORCE)
set(DPCPP_COMPILER "${sycl_compiler}" CACHE FILEPATH "" FORCE)
set(DPCPP_TARGET_TRIPLES spir64 CACHE STRING "" FORCE)
set(SYCL_CTS_ENABLE_FULL_CONFORMANCE OFF CACHE BOOL "" FORCE)
set(SYCL_CTS_ENABLE_OPENCL_INTEROP_TESTS OFF CACHE BOOL "" FORCE)
set(SYCL_CTS_EXCLUDE_TEST_CATEGORIES "${CMAKE_CURRENT_BINARY_DIR}/excluded.txt" CACHE FILEPATH "" FORCE)
add_subdirectory("${staged_source}" build EXCLUDE_FROM_ALL)

set(sycl_cts_targets)
foreach(category IN LISTS sycl_cts_categories)
  list(APPEND sycl_cts_targets "test_${category}")
  if(APPLE)
    set(program_rpath "@loader_path/lib")
  elseif(UNIX)
    set(program_rpath "$ORIGIN/lib")
  endif()
  set_target_properties("test_${category}" PROPERTIES
    BUILD_RPATH "${program_rpath}" INSTALL_RPATH "${program_rpath}")
  if(TARGET "test_${category}_objects")
    # The upstream SYCL CTS is vendored third-party code; silence its
    # diagnostics so only project code is checked.
    target_compile_options("test_${category}_objects" PRIVATE
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-deprecated-declarations>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-deprecated-attributes>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-sycl-strict>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-user-defined-warnings>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-return-type>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-cpp>")
  endif()
  conformance_register_test_program("test_${category}" ${CONFORMANCE_TIMEOUT_CTS})
endforeach()

install(FILES "${staged_source}/LICENSE"
  DESTINATION licenses RENAME SYCL-CTS.txt COMPONENT support)
install(FILES "${staged_source}/vendor/OpenCL-Headers/LICENSE"
  DESTINATION licenses RENAME OpenCL-Headers.txt COMPONENT support)

add_custom_target(conformance_cts_artifact DEPENDS ${sycl_cts_targets})
