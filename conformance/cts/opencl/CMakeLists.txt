find_package(OpenCL REQUIRED)
find_program(opencl_cts_spirv_as spirv-as REQUIRED)

# Keep the catalogue useful without compiling the multi-day numerical, image,
# vector, and exhaustive conversion categories.
set(opencl_cts_categories
  api atomics basic buffers c11_atomics commonfns compiler computeinfo events contractions device_partition
  device_timer generic_address_space geometrics mem_host_flags multiples
  non_uniform_work_group pipes printf profiling relationals spirv_new subgroups svm thread_dimensions workgroups)
conformance_fetch_url(opencl_cts opencl_cts_source)
conformance_fetch_url(opencl_opencl_headers opencl_headers)
conformance_fetch_url(opencl_spirv_headers spirv_headers)

set(staged_source "${CMAKE_CURRENT_BINARY_DIR}/source")
file(REMOVE_RECURSE "${staged_source}")
file(COPY "${opencl_cts_source}/" DESTINATION "${staged_source}")
file(COPY_FILE "${CMAKE_CURRENT_SOURCE_DIR}/adapter.cpp" "${staged_source}/conformance_adapter.cpp")

# The upstream imagedim cases do not currently honor the harness's wimpy
# mode, making one CPU-emulator case take hours while still exercising the
# same dimension combinations. Keep the full CTS unchanged and bound only its
# explicitly reduced workload.
set(image_dimensions "${staged_source}/test_conformance/basic/test_imagedim.cpp")
file(READ "${image_dimensions}" image_dimensions_content)
set(image_dimensions_include [=[#include "testBase.h"
]=])
string(FIND "${image_dimensions_content}" "${image_dimensions_include}" image_dimensions_include_patch)
if(image_dimensions_include_patch EQUAL -1)
  message(FATAL_ERROR "Pinned OpenCL CTS imagedim include integration point changed")
endif()
string(REPLACE "${image_dimensions_include}" [=[#include "testBase.h"
#include "harness/parseParameters.h"
]=] image_dimensions_content "${image_dimensions_content}")
set(image_dimensions_before [=[    max_img_width = std::min(max_image2d_width, max_img_dim);
    max_img_height = std::min(max_image2d_height, max_img_dim);
]=])
set(image_dimensions_after [=[    max_img_width = std::min(max_image2d_width, max_img_dim);
    max_img_height = std::min(max_image2d_height, max_img_dim);
    if (gWimpyMode)
    {
        max_img_width = std::min(max_img_width, size_t{ 512 });
        max_img_height = std::min(max_img_height, size_t{ 512 });
    }
]=])
string(FIND "${image_dimensions_content}" "${image_dimensions_before}" image_dimensions_patch)
if(image_dimensions_patch EQUAL -1)
  message(FATAL_ERROR "Pinned OpenCL CTS imagedim integration point changed")
endif()
string(REPLACE "${image_dimensions_before}" "${image_dimensions_after}"
  image_dimensions_content "${image_dimensions_content}")
file(WRITE "${image_dimensions}" "${image_dimensions_content}")

set(common "${staged_source}/test_conformance/CMakeCommon.txt")
file(READ "${common}" common_content)
set(before [=[add_executable(${${MODULE_NAME}_OUT} ${${MODULE_NAME}_SOURCES})]=])
string(FIND "${common_content}" "${before}" integration_point)
if(integration_point EQUAL -1)
  message(FATAL_ERROR "Pinned OpenCL CTS integration point changed in ${common}")
endif()
set(after [=[add_executable(${${MODULE_NAME}_OUT} ${${MODULE_NAME}_SOURCES} ${CLConform_SOURCE_DIR}/conformance_adapter.cpp)
target_compile_definitions(${${MODULE_NAME}_OUT} PRIVATE main=conformance_upstream_main CONFORMANCE_CTS_CATEGORY="${MODULE_NAME_LOWER}")
target_link_libraries(${${MODULE_NAME}_OUT} Catch2::Catch2 conformance_device)
set_target_properties(${${MODULE_NAME}_OUT} PROPERTIES OUTPUT_NAME "opencl_cts_${MODULE_NAME_LOWER}")]=])
string(REPLACE "${before}" "${after}" common_content "${common_content}")
file(WRITE "${common}" "${common_content}")

set(CL_INCLUDE_DIR "${opencl_headers}")
get_filename_component(CL_LIB_DIR "${OpenCL_LIBRARY}" DIRECTORY)
set(SPIRV_INCLUDE_DIR "${spirv_headers}")
set(OPENCL_LIBRARIES "${OpenCL_LIBRARY}")
add_subdirectory("${staged_source}" build EXCLUDE_FROM_ALL)

set(opencl_cts_targets)
foreach(category IN LISTS opencl_cts_categories)
  list(APPEND opencl_cts_targets "test_${category}")
  if(APPLE)
    set(program_rpath "@loader_path/lib")
  elseif(UNIX)
    set(program_rpath "$ORIGIN/lib")
  endif()
  set_target_properties("test_${category}" PROPERTIES
    BUILD_RPATH "${program_rpath}" INSTALL_RPATH "${program_rpath}")
  # The pinned CTS harness emits an explanatory #warning in imageHelpers.cpp;
  # leave every other diagnostic in place.
  target_compile_options("test_${category}" PRIVATE
    "$<$<COMPILE_LANG_AND_ID:CXX,GNU,Clang,AppleClang>:-Wno-cpp>")
  if(category STREQUAL "thread_dimensions")
    set(category_timeout 1800)
  elseif(category STREQUAL "multiples")
    set(category_timeout 600)
  else()
    set(category_timeout 120)
  endif()
  conformance_register_test_program("test_${category}" ${category_timeout})
endforeach()

install(DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/build/test_conformance/compiler/spirv_bin"
  "${CMAKE_CURRENT_BINARY_DIR}/build/test_conformance/spirv_new/spirv_bin"
  "${staged_source}/test_conformance/compiler/includeTestDirectory"
  "${staged_source}/test_conformance/compiler/secondIncludeTestDirectory"
  DESTINATION data COMPONENT support OPTIONAL)
install(FILES "${opencl_cts_source}/LICENSE.txt"
  DESTINATION licenses RENAME OpenCL-CTS.txt COMPONENT support)
install(FILES "${opencl_headers}/LICENSE"
  DESTINATION licenses RENAME OpenCL-Headers.txt COMPONENT support)
install(FILES "${spirv_headers}/LICENSE"
  DESTINATION licenses RENAME SPIRV-Headers.txt COMPONENT support)

add_custom_target(conformance_cts_artifact DEPENDS ${opencl_cts_targets})
