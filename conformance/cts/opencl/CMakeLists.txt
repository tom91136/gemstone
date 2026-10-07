find_package(OpenCL REQUIRED)

# Keep the catalogue useful without compiling the multi-day numerical, image,
# and exhaustive conversion categories. Workloads select bounded integer, half,
# and vector cases within the compiled categories.
set(opencl_cts_categories
  api atomics basic buffers c11_atomics commonfns compiler computeinfo events contractions device_partition
  device_timer generic_address_space geometrics half integer_ops mem_host_flags multiples
  non_uniform_work_group pipes printf profiling relationals spirv_new subgroups svm thread_dimensions vectors workgroups)
FetchContent_MakeAvailable(opencl_cts opencl_headers opencl_spirv_headers)

set(staged_source "${CMAKE_CURRENT_BINARY_DIR}/source")
set(staging "${staged_source}.staging")
stage_source("${opencl_cts_SOURCE_DIR}" "${staging}")
file(COPY_FILE "${CMAKE_CURRENT_SOURCE_DIR}/adapter.cpp" "${staging}/conformance_adapter.cpp")

# Wrap upstream entry points for Catch discovery and reporting.
patch_exact("${staging}/test_conformance/CMakeCommon.txt"
  [=[add_executable(${${MODULE_NAME}_OUT} ${${MODULE_NAME}_SOURCES})]=]
  [=[add_executable(${${MODULE_NAME}_OUT} ${${MODULE_NAME}_SOURCES} ${CLConform_SOURCE_DIR}/conformance_adapter.cpp)
target_compile_definitions(${${MODULE_NAME}_OUT} PRIVATE main=conformance_upstream_main CONFORMANCE_CTS_CATEGORY="${MODULE_NAME_LOWER}")
target_link_libraries(${${MODULE_NAME}_OUT} Catch2::Catch2 conformance_device)
set_target_properties(${${MODULE_NAME}_OUT} PROPERTIES OUTPUT_NAME "opencl_cts_${MODULE_NAME_LOWER}")]=])
commit_staged_source("${staging}" "${staged_source}")

set(CL_INCLUDE_DIR "${opencl_headers_SOURCE_DIR}")
get_filename_component(CL_LIB_DIR "${OpenCL_LIBRARY}" DIRECTORY)
set(SPIRV_INCLUDE_DIR "${opencl_spirv_headers_SOURCE_DIR}")
set(OPENCL_LIBRARIES "${OpenCL_LIBRARY}")
add_subdirectory("${staged_source}" build EXCLUDE_FROM_ALL)

set(opencl_cts_targets)
foreach(category IN LISTS opencl_cts_categories)
  list(APPEND opencl_cts_targets "test_${category}")
  # The pinned CTS harness emits an explanatory #warning in imageHelpers.cpp;
  # leave every other diagnostic in place.
  target_compile_options("test_${category}" PRIVATE
    "$<$<COMPILE_LANG_AND_ID:CXX,GNU,Clang,AppleClang>:-Wno-cpp>")
  if(category STREQUAL "thread_dimensions")
    set(category_timeout ${CONFORMANCE_TIMEOUT_OPENCL_THREAD_DIMENSIONS})
  elseif(category STREQUAL "multiples")
    set(category_timeout ${CONFORMANCE_TIMEOUT_OPENCL_MULTIPLES})
  else()
    set(category_timeout ${CONFORMANCE_TIMEOUT_OPENCL_DEFAULT})
  endif()
  conformance_register_test_program("test_${category}" ${category_timeout})
endforeach()

install(DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/build/test_conformance/compiler/spirv_bin"
  "${CMAKE_CURRENT_BINARY_DIR}/build/test_conformance/spirv_new/spirv_bin"
  "${staged_source}/test_conformance/compiler/includeTestDirectory"
  "${staged_source}/test_conformance/compiler/secondIncludeTestDirectory"
  DESTINATION data COMPONENT support OPTIONAL)
install(FILES "${opencl_cts_SOURCE_DIR}/LICENSE.txt"
  DESTINATION licences RENAME OpenCL-CTS.txt COMPONENT support)
install(FILES "${opencl_headers_SOURCE_DIR}/LICENSE"
  DESTINATION licences RENAME OpenCL-Headers.txt COMPONENT support)
install(FILES "${opencl_spirv_headers_SOURCE_DIR}/LICENSE"
  DESTINATION licences RENAME SPIRV-Headers.txt COMPONENT support)

add_custom_target(conformance_cts_artifact DEPENDS ${opencl_cts_targets})
