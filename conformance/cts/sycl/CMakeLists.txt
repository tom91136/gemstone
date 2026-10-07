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
FetchContent_MakeAvailable(sycl_cts sycl_cts_opencl_headers)

set(staged_source "${CMAKE_CURRENT_BINARY_DIR}/source")
set(staging "${staged_source}.staging")
stage_source("${sycl_cts_SOURCE_DIR}" "${staging}")
file(MAKE_DIRECTORY "${staging}/vendor/OpenCL-Headers")
file(COPY "${sycl_cts_opencl_headers_SOURCE_DIR}/" DESTINATION "${staging}/vendor/OpenCL-Headers")

# Reuse project Catch2 to avoid duplicate targets.
patch_exact("${staging}/CMakeLists.txt" "add_submodule_directory(vendor/Catch2)" "")

# Keep legacy class names alive for Catch2 StringRef storage.
set(legacy_proxy "${staging}/util/proxy.h")
patch_exact("${legacy_proxy}" [[    Catch::AutoReg(]] [[    m_class_name = "__SYCL_CTS_LEGACY_TEST__" + std::to_string(next_legacy_test_id++);
    Catch::AutoReg(]])
patch_exact("${legacy_proxy}" [[        "__SYCL_CTS_LEGACY_TEST__" + std::to_string(next_legacy_test_id++),]]
  [[        m_class_name,]])
patch_exact("${legacy_proxy}" [[  test_base::info m_info;]] [[  test_base::info m_info;
  std::string m_class_name;]])

# Remove obsolete descriptors; the standard long descriptors are already tested.
foreach(width IN ITEMS preferred native)
  patch_exact("${staging}/tests/device/device_info.cpp"
    "#if !SYCL_CTS_COMPILING_WITH_SIMSYCL\n    check_get_info_param<sycl::info::device::${width}_vector_width_long_long,\n                         uint32_t>(dev);\n#endif"
    "")
endforeach()

set(queue_properties "${staging}/tests/queue/queue_properties.cpp")
# Correct an invalid relative include in the pinned CTS source.
patch_exact("${queue_properties}" [[#include "../../../util/sycl_exceptions.h"]] [[#include "../../util/sycl_exceptions.h"]])
# Free the leaked flag after existing synchronisation and assertion.
patch_exact("${queue_properties}" [[  CHECK(result);
}

void check_in_order(sycl::queue& queue) {]] [[  CHECK(result);
  sycl::free(data_changed, queue);
}

void check_in_order(sycl::queue& queue) {]])

# The pinned buffer tests have the same invalid relative include.
patch_exact("${staging}/tests/buffer/buffer_api_common.h"
  [[#include "../../../util/sycl_exceptions.h"]] [[#include "../../util/sycl_exceptions.h"]])

# Distinguish core and atomic64 cases for unique discovery identities.
patch_exact("${staging}/tests/usm/usm_atomic_access_core.cpp"
  [[TEST_CASE("Tests for usm atomics with atomic64 support", "[usm][atomic]")]]
  [[TEST_CASE("Tests for usm atomics with core types", "[usm][atomic]")]])

# Free leaked flags after existing synchronisation/checks; preserve ordering tests.
set(invoke_memory "${staging}/tests/invoke/execution_and_memory_models.cpp")
patch_exact("${invoke_memory}" [[      queue.wait_and_throw();
    }

    INFO(
        "Check that requisites for second command with overlapping sub-buffer "]] [[      queue.wait_and_throw();
      sycl::free(pflag, queue);
    }

    INFO(
        "Check that requisites for second command with overlapping sub-buffer "]])
patch_exact("${invoke_memory}" [[      CHECK(*pflag == 42);]] [[      CHECK(*pflag == 42);
      sycl::free(pflag, queue);]])

# Precision variants share names upstream, but discovery requires unique names.
foreach(operation IN ITEMS permute shift broadcast)
  if(operation STREQUAL broadcast)
    set(case_names "Group broadcast" "Sub-group broadcast and select")
  else()
    set(case_names "Group and sub-group ${operation}")
  endif()
  foreach(precision IN ITEMS fp16 fp64)
    foreach(case_name IN LISTS case_names)
      patch_exact("${staging}/tests/group_functions/group_${operation}_${precision}.cpp"
        "TEMPLATE_TEST_CASE_SIG(\"${case_name}\"," "TEMPLATE_TEST_CASE_SIG(\"${case_name} ${precision}\",")
    endforeach()
  endforeach()
endforeach()

# Name and collect upstream executables for artifact registration.
patch_exact("${staging}/cmake/AdaptDPCPP.cmake"
  [=[add_executable(${exe_name} $<TARGET_OBJECTS:${object_lib_name}>)]=]
  [=[add_executable(${exe_name} $<TARGET_OBJECTS:${object_lib_name}>)
string(REGEX REPLACE "^test_" "" conformance_category "${exe_name}")
set_target_properties(${exe_name} PROPERTIES OUTPUT_NAME "sycl_cts_${conformance_category}")
set_property(GLOBAL APPEND PROPERTY CONFORMANCE_SYCL_CTS_TARGETS "${exe_name}")]=])

commit_staged_source("${staging}" "${staged_source}")

file(GLOB children RELATIVE "${sycl_cts_SOURCE_DIR}/tests" "${sycl_cts_SOURCE_DIR}/tests/*")
set(excluded)
foreach(child IN LISTS children)
  if(IS_DIRECTORY "${sycl_cts_SOURCE_DIR}/tests/${child}" AND
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
  if(TARGET "${target}_objects")
    # The upstream SYCL CTS is vendored third-party code; silence its
    # diagnostics so only project code is checked.
    target_compile_options("${target}_objects" PRIVATE
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-deprecated-declarations>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-deprecated-attributes>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-sycl-strict>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-user-defined-warnings>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-return-type>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-cpp>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-constant-logical-operand>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-deprecated-literal-operator>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-c++23-lambda-attributes>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM>:-Wno-sycl-aspect-mismatch>"
      "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM,Clang,AppleClang>:-Wno-implicit-const-int-float-conversion>")
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
  DESTINATION licences RENAME SYCL-CTS.txt COMPONENT support)
install(FILES "${staged_source}/vendor/OpenCL-Headers/LICENSE"
  DESTINATION licences RENAME OpenCL-Headers.txt COMPONENT support)

add_custom_target(conformance_cts_artifact DEPENDS ${sycl_cts_targets})
