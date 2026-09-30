# Standard SYCL categories, including their generated type matrices.
# Vendor extensions and backend interoperability are separate upstream options.
set(sycl_cts_categories
  accessor_basic accessor_generic accessor_legacy accessor_placeholder address_space atomic atomic_fence
  atomic_ref atomic_ref_stress bit_cast buffer context device device_event device_selector error event
  exception_handling exceptions full_feature_set function_objects group group_functions h_item handler
  header hierarchical host_accessor host_task id image image_accessor invoke is_device_copyable item kernel
  kernel_args kernel_bundle language local_accessor marray_arithmetic_assignment marray_arithmetic_binary
  marray_basic marray_bitwise marray_pre_post marray_relational math_builtin_api multi_ptr namespace nd_item
  nd_range optional_kernel_features platform pointers property queue range reduction scalars spec_constants
  stream sub_group sycl_external usm vector_alias vector_api vector_constructors vector_deduction_guides
  vector_load_store vector_operators vector_swizzle_assignment vector_swizzles)

find_program(sycl_compiler NAMES icpx clang++ REQUIRED)
if(NOT "${CMAKE_CXX_COMPILER}" STREQUAL "${sycl_compiler}")
  message(FATAL_ERROR "SYCL CTS must use the project C++ compiler: ${CMAKE_CXX_COMPILER}")
endif()
conformance_fetch_url(conformance_sycl_cts sycl_cts_source)
conformance_fetch_url(conformance_sycl_cts_opencl_headers sycl_cts_opencl_headers_source)

# Reapply adaptations to pristine inputs, then preserve unchanged build inputs.
set(staged_source "${CMAKE_CURRENT_BINARY_DIR}/source-prepared")
file(REMOVE_RECURSE "${staged_source}")
file(COPY "${sycl_cts_source}/" DESTINATION "${staged_source}")
file(MAKE_DIRECTORY "${staged_source}/vendor/OpenCL-Headers")
file(COPY "${sycl_cts_opencl_headers_source}/"
  DESTINATION "${staged_source}/vendor/OpenCL-Headers")

# Reuse project Catch2 to avoid duplicate targets.
set(sycl_cts_cmake "${staged_source}/CMakeLists.txt")
file(READ "${sycl_cts_cmake}" sycl_cts_cmake_content)
set(vendored_catch "add_submodule_directory(vendor/Catch2)")
string(FIND "${sycl_cts_cmake_content}" "${vendored_catch}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS Catch2 integration point changed")
endif()
string(REPLACE "${vendored_catch}" "" sycl_cts_cmake_content "${sycl_cts_cmake_content}")
file(WRITE "${sycl_cts_cmake}" "${sycl_cts_cmake_content}")

# Keep legacy class names alive for Catch2 StringRef storage.
set(legacy_proxy "${staged_source}/util/proxy.h")
file(READ "${legacy_proxy}" legacy_proxy_content)
set(previous [[    Catch::AutoReg(]])
set(current [[    m_class_name = "__SYCL_CTS_LEGACY_TEST__" + std::to_string(next_legacy_test_id++);
    Catch::AutoReg(]])
string(FIND "${legacy_proxy_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS legacy registration integration point changed")
endif()
string(REPLACE "${previous}" "${current}" legacy_proxy_content "${legacy_proxy_content}")
set(previous [[        "__SYCL_CTS_LEGACY_TEST__" + std::to_string(next_legacy_test_id++),]])
set(current [[        m_class_name,]])
string(FIND "${legacy_proxy_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS legacy class name integration point changed")
endif()
string(REPLACE "${previous}" "${current}" legacy_proxy_content "${legacy_proxy_content}")
set(previous [[  test_base::info m_info;]])
set(current [[  test_base::info m_info;
  std::string m_class_name;]])
string(FIND "${legacy_proxy_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS legacy proxy storage integration point changed")
endif()
string(REPLACE "${previous}" "${current}" legacy_proxy_content "${legacy_proxy_content}")
file(WRITE "${legacy_proxy}" "${legacy_proxy_content}")

# Remove obsolete descriptors; the standard long descriptors are already tested.
set(device_info "${staged_source}/tests/device/device_info.cpp")
file(READ "${device_info}" device_info_content)
foreach(width IN ITEMS preferred native)
  set(previous "#if !SYCL_CTS_COMPILING_WITH_SIMSYCL\n    check_get_info_param<sycl::info::device::${width}_vector_width_long_long,\n                         uint32_t>(dev);\n#endif")
  string(FIND "${device_info_content}" "${previous}" integration_point)
  if(integration_point EQUAL -1)
    message(FATAL_ERROR "Pinned SYCL CTS ${width} obsolete descriptor integration point changed")
  endif()
  string(REPLACE "${previous}" "" device_info_content "${device_info_content}")
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
# Free the leaked flag after existing synchronization and assertion.
set(previous [[  CHECK(result);
}

void check_in_order(sycl::queue& queue) {]])
set(current [[  CHECK(result);
  sycl::free(data_changed, queue);
}

void check_in_order(sycl::queue& queue) {]])
string(FIND "${queue_properties_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS in-order flag cleanup integration point changed")
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

# Distinguish core and atomic64 cases for unique discovery identities.
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

# Free leaked flags after existing synchronization/checks; preserve ordering tests.
set(invoke_memory "${staged_source}/tests/invoke/execution_and_memory_models.cpp")
file(READ "${invoke_memory}" invoke_memory_content)
set(previous [[      queue.wait_and_throw();
    }

    INFO(
        "Check that requisites for second command with overlapping sub-buffer "]])
set(current [[      queue.wait_and_throw();
      sycl::free(pflag, queue);
    }

    INFO(
        "Check that requisites for second command with overlapping sub-buffer "]])
string(FIND "${invoke_memory_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS sub-buffer flag cleanup integration point changed")
endif()
string(REPLACE "${previous}" "${current}" invoke_memory_content "${invoke_memory_content}")
set(previous [[      CHECK(*pflag == 42);]])
set(current [[      CHECK(*pflag == 42);
      sycl::free(pflag, queue);]])
string(FIND "${invoke_memory_content}" "${previous}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS host-accessor flag cleanup integration point changed")
endif()
string(REPLACE "${previous}" "${current}" invoke_memory_content "${invoke_memory_content}")
file(WRITE "${invoke_memory}" "${invoke_memory_content}")

# Precision variants share names upstream, but discovery requires unique names.
foreach(operation IN ITEMS permute shift broadcast)
  if(operation STREQUAL broadcast)
    set(case_names "Group broadcast" "Sub-group broadcast and select")
  else()
    set(case_names "Group and sub-group ${operation}")
  endif()
  foreach(precision IN ITEMS fp16 fp64)
    set(group_test "${staged_source}/tests/group_functions/group_${operation}_${precision}.cpp")
    file(READ "${group_test}" group_test_content)
    foreach(case_name IN LISTS case_names)
      set(previous "TEMPLATE_TEST_CASE_SIG(\"${case_name}\",")
      set(current "TEMPLATE_TEST_CASE_SIG(\"${case_name} ${precision}\",")
      string(FIND "${group_test_content}" "${previous}" integration_point)
      if(integration_point EQUAL -1)
        message(FATAL_ERROR "Pinned SYCL CTS ${operation} ${precision} case integration point changed")
      endif()
      string(REPLACE "${previous}" "${current}" group_test_content "${group_test_content}")
    endforeach()
    file(WRITE "${group_test}" "${group_test_content}")
  endforeach()
endforeach()

# Name and collect upstream executables for artifact registration.
set(adapter "${staged_source}/cmake/AdaptDPCPP.cmake")
file(READ "${adapter}" adapter_content)
set(before [=[add_executable(${exe_name} $<TARGET_OBJECTS:${object_lib_name}>)]=])
string(FIND "${adapter_content}" "${before}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned SYCL CTS integration point changed in ${adapter}")
endif()
set(after [=[add_executable(${exe_name} $<TARGET_OBJECTS:${object_lib_name}>)
string(REGEX REPLACE "^test_" "" conformance_category "${exe_name}")
set_target_properties(${exe_name} PROPERTIES OUTPUT_NAME "sycl_cts_${conformance_category}")
set_property(GLOBAL APPEND PROPERTY CONFORMANCE_SYCL_CTS_TARGETS "${exe_name}")]=])
string(REPLACE "${before}" "${after}" adapter_content "${adapter_content}")
file(WRITE "${adapter}" "${adapter_content}")

set(stable_source "${CMAKE_CURRENT_BINARY_DIR}/source")
file(GLOB_RECURSE previous_entries LIST_DIRECTORIES true
  RELATIVE "${stable_source}" "${stable_source}/*")
foreach(entry IN LISTS previous_entries)
  if(NOT EXISTS "${staged_source}/${entry}" OR
      (IS_DIRECTORY "${stable_source}/${entry}" AND NOT IS_DIRECTORY "${staged_source}/${entry}") OR
      (NOT IS_DIRECTORY "${stable_source}/${entry}" AND IS_DIRECTORY "${staged_source}/${entry}"))
    file(REMOVE_RECURSE "${stable_source}/${entry}")
  endif()
endforeach()
file(GLOB_RECURSE prepared_entries LIST_DIRECTORIES true
  RELATIVE "${staged_source}" "${staged_source}/*")
foreach(entry IN LISTS prepared_entries)
  if(IS_DIRECTORY "${staged_source}/${entry}")
    file(MAKE_DIRECTORY "${stable_source}/${entry}")
  else()
    get_filename_component(parent "${stable_source}/${entry}" DIRECTORY)
    file(MAKE_DIRECTORY "${parent}")
    file(COPY_FILE "${staged_source}/${entry}" "${stable_source}/${entry}" ONLY_IF_DIFFERENT)
  endif()
endforeach()
file(REMOVE_RECURSE "${staged_source}")
set(staged_source "${stable_source}")

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
set(SYCL_CTS_ENABLE_FULL_CONFORMANCE OFF CACHE BOOL
  "Enable the complete upstream SYCL CTS type and operation matrices")
set(SYCL_CTS_ENABLE_OPENCL_INTEROP_TESTS OFF CACHE BOOL "" FORCE)
set(SYCL_CTS_EXCLUDE_TEST_CATEGORIES "${CMAKE_CURRENT_BINARY_DIR}/excluded.txt" CACHE FILEPATH "" FORCE)
set_property(GLOBAL PROPERTY CONFORMANCE_SYCL_CTS_TARGETS "")
add_subdirectory("${staged_source}" build EXCLUDE_FROM_ALL)

# Host/device atomic stress requires std::atomic_ref. The upstream DPCPP
# adapter forwards executable compile features to its object library.
target_compile_features(test_atomic_ref_stress PRIVATE cxx_std_20)

get_property(sycl_cts_targets GLOBAL PROPERTY CONFORMANCE_SYCL_CTS_TARGETS)
foreach(target IN LISTS sycl_cts_targets)
  if(APPLE)
    set(program_rpath "@loader_path/lib")
  elseif(UNIX)
    set(program_rpath "$ORIGIN/lib")
  endif()
  set_target_properties("${target}" PROPERTIES
    BUILD_RPATH "${program_rpath}" INSTALL_RPATH "${program_rpath}")
  if(TARGET "${target}_objects")
    # The upstream SYCL CTS is vendored third-party code; silence its
    # diagnostics so only project code is checked.
    target_compile_options("${target}_objects" PRIVATE
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-deprecated-declarations>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-deprecated-attributes>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-sycl-strict>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-user-defined-warnings>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-return-type>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-cpp>")
  endif()
  set(timeout ${CONFORMANCE_TIMEOUT_CTS})
  if(SYCL_CTS_ENABLE_FULL_CONFORMANCE)
    set(timeout ${CONFORMANCE_TIMEOUT_SYCL_FULL})
    if(target STREQUAL "test_handler")
      set(timeout ${CONFORMANCE_TIMEOUT_SYCL_FULL_HANDLER})
    endif()
  endif()
  conformance_register_test_program("${target}" ${timeout})
endforeach()

install(FILES "${staged_source}/LICENSE"
  DESTINATION licenses RENAME SYCL-CTS.txt COMPONENT support)
install(FILES "${staged_source}/vendor/OpenCL-Headers/LICENSE"
  DESTINATION licenses RENAME OpenCL-Headers.txt COMPONENT support)

add_custom_target(conformance_cts_artifact DEPENDS ${sycl_cts_targets})
