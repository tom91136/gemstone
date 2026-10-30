cmake_minimum_required(VERSION 3.25)
project(gemstone-native LANGUAGES C CXX)

if((CMAKE_CXX_COMPILER_ID STREQUAL "GNU" AND CMAKE_CXX_COMPILER_VERSION VERSION_LESS 13)
    OR (CMAKE_CXX_COMPILER_ID STREQUAL "Clang" AND CMAKE_CXX_COMPILER_VERSION VERSION_LESS 19))
  message(FATAL_ERROR "C++23 and glaze need GCC 13 or clang 19 or newer, found ${CMAKE_CXX_COMPILER_ID} ${CMAKE_CXX_COMPILER_VERSION}")
endif()

# Per target rather than directory-wide, so fetched dependencies keep their own standard and warnings
function(gemstone_project_target target)
  set_target_properties("${target}" PROPERTIES CXX_STANDARD 23 CXX_STANDARD_REQUIRED ON CXX_EXTENSIONS OFF)
  target_compile_options("${target}" PRIVATE
    "$<$<COMPILE_LANG_AND_ID:CXX,GNU,Clang>:-Wall;-Wextra;-Wpedantic;-Werror>"
    "$<$<COMPILE_LANG_AND_ID:CXX,Clang>:-Wno-overlength-strings>")
endfunction()

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/generated")

include(CTest)
include(FetchContent)

option(GEMSTONE_ENABLE_CUDA "Build the CUDA facade" ON)
option(GEMSTONE_ENABLE_HSA "Build the HSA and KMT facades" ON)
option(GEMSTONE_ENABLE_HIP "Build the HIP facade" ON)
option(GEMSTONE_ENABLE_OPENCL "Build the OpenCL facade" ON)
option(GEMSTONE_ENABLE_VULKAN "Build the Vulkan compute facade" ON)
option(GEMSTONE_ENABLE_LEVEL_ZERO "Build the Level Zero facade" ON)

include(cmake/Aspartame.cmake)
include(cmake/Fmt.cmake)
include(cmake/Glaze.cmake)
include_directories(SYSTEM "${aspartame_SOURCE_DIR}/src" "${glaze_SOURCE_DIR}/include")

if(GEMSTONE_ENABLE_CUDA OR GEMSTONE_ENABLE_HIP)
  set(ZSTD_BUILD_STATIC ON CACHE BOOL "" FORCE)
  set(ZSTD_BUILD_SHARED OFF CACHE BOOL "" FORCE)
  set(ZSTD_BUILD_COMPRESSION OFF CACHE BOOL "" FORCE)
  set(ZSTD_BUILD_DECOMPRESSION ON CACHE BOOL "" FORCE)
  set(ZSTD_BUILD_DICTBUILDER OFF CACHE BOOL "" FORCE)
  set(ZSTD_BUILD_DEPRECATED OFF CACHE BOOL "" FORCE)
  set(ZSTD_BUILD_PROGRAMS OFF CACHE BOOL "" FORCE)
  set(ZSTD_BUILD_TESTS OFF CACHE BOOL "" FORCE)
  set(ZSTD_BUILD_CONTRIB OFF CACHE BOOL "" FORCE)
  set(ZSTD_LEGACY_SUPPORT OFF CACHE BOOL "" FORCE)
  set(ZSTD_MULTITHREAD_SUPPORT OFF CACHE BOOL "" FORCE)
  set(ZSTDLIB_VISIBLE hidden CACHE STRING "" FORCE)
  set(ZSTDERRORLIB_VISIBLE hidden CACHE STRING "" FORCE)
  set(ZSTDLIB_STATIC_API hidden CACHE STRING "" FORCE)
  FetchContent_Declare(zstd
    URL https://github.com/facebook/zstd/releases/download/v1.5.7/zstd-1.5.7.tar.gz
    URL_HASH SHA256=eb33e51f49a15e023950cd7825ca74a4a2b43db8354825ac24fc1b7ee09e6fa3
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
    SOURCE_SUBDIR build/cmake)
  FetchContent_MakeAvailable(zstd)
  set_target_properties(libzstd_static PROPERTIES
    C_VISIBILITY_PRESET hidden
    POSITION_INDEPENDENT_CODE ON)
  add_library(gemstone_zstd INTERFACE)
  target_link_libraries(gemstone_zstd INTERFACE libzstd_static)
endif()

if(GEMSTONE_ENABLE_CUDA)
  set(LZ4_BUNDLED_MODE ON CACHE BOOL "" FORCE)
  set(LZ4_BUILD_CLI OFF CACHE BOOL "" FORCE)
  set(LZ4_POSITION_INDEPENDENT_LIB ON CACHE BOOL "" FORCE)
  FetchContent_Declare(lz4
    URL https://github.com/lz4/lz4/archive/refs/tags/v1.10.0.tar.gz
    URL_HASH SHA256=537512904744b35e232912055ccf8ec66d768639ff3abe5788d90d792ec5f48b
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
    SOURCE_SUBDIR build/cmake)
  FetchContent_MakeAvailable(lz4)
  set_target_properties(lz4_static PROPERTIES
    C_VISIBILITY_PRESET hidden
    POSITION_INDEPENDENT_CODE ON)
  add_library(gemstone_lz4 INTERFACE)
  target_link_libraries(gemstone_lz4 INTERFACE lz4_static)
endif()

if(GEMSTONE_ENABLE_HIP AND NOT GEMSTONE_ENABLE_HSA)
  message(FATAL_ERROR "GEMSTONE_ENABLE_HIP requires GEMSTONE_ENABLE_HSA")
endif()

foreach(_gemstone_language IN ITEMS CXX HIP)
  add_compile_options(
    "$<$<COMPILE_LANG_AND_ID:${_gemstone_language},GNU,Clang,IntelLLVM>:-ffile-prefix-map=${CMAKE_SOURCE_DIR}=.>"
    "$<$<COMPILE_LANG_AND_ID:${_gemstone_language},GNU,Clang,IntelLLVM>:-ffile-prefix-map=${CMAKE_BINARY_DIR}=.build>")
endforeach()
add_compile_options(
  "$<$<COMPILE_LANG_AND_ID:CUDA,NVIDIA>:-Xcompiler=-ffile-prefix-map=${CMAKE_SOURCE_DIR}=.>"
  "$<$<COMPILE_LANG_AND_ID:CUDA,NVIDIA>:-Xcompiler=-ffile-prefix-map=${CMAKE_BINARY_DIR}=.build>")
foreach(_gemstone_language IN ITEMS CXX CUDA HIP)
  set(CMAKE_${_gemstone_language}_ARCHIVE_CREATE
    "<CMAKE_AR> qcD <TARGET> <LINK_FLAGS> <OBJECTS>")
  set(CMAKE_${_gemstone_language}_ARCHIVE_APPEND
    "<CMAKE_AR> qD <TARGET> <LINK_FLAGS> <OBJECTS>")
  set(CMAKE_${_gemstone_language}_ARCHIVE_FINISH "<CMAKE_RANLIB> -D <TARGET>")
endforeach()

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}")
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}")
set(GEMSTONE_NATIVE_INCLUDE_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/include")

if(BUILD_TESTING)
  set(CATCH_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
  set(CATCH_BUILD_EXTRA_TESTS OFF CACHE BOOL "" FORCE)
  set(CATCH_ENABLE_REPRODUCIBLE_BUILD ON CACHE BOOL "" FORCE)
  set(CATCH_INSTALL_DOCS OFF CACHE BOOL "" FORCE)
  set(CATCH_INSTALL_EXTRAS OFF CACHE BOOL "" FORCE)
  FetchContent_Declare(Catch2
    URL https://github.com/catchorg/Catch2/archive/refs/tags/v3.15.3.tar.gz
    URL_HASH SHA256=b0299ae552918220a7a6e21e7de5b714777f4e8c883fb70c4bb23fe01df8c6e3
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE)
  FetchContent_MakeAvailable(Catch2)
  list(APPEND CMAKE_MODULE_PATH "${catch2_SOURCE_DIR}/extras")
  include(Catch)

  function(gemstone_test_target target)
    target_link_libraries("${target}" PRIVATE Catch2::Catch2WithMain)
    target_include_directories("${target}" PRIVATE
      "${GEMSTONE_NATIVE_INCLUDE_DIRECTORY}"
      "${CMAKE_CURRENT_FUNCTION_LIST_DIR}/generated"
      "${CMAKE_CURRENT_FUNCTION_LIST_DIR}/test")
    gemstone_project_target("${target}")
  endfunction()

  # Matches gemstone_test::timeoutFactor, fixed when the build configures.
  if(NOT "$ENV{GEMSTONE_THREAD_COUNT}" STREQUAL "" AND NOT "$ENV{GEMSTONE_THREAD_COUNT}" MATCHES "^0*[1-9][0-9]*$")
    message(FATAL_ERROR "GEMSTONE_THREAD_COUNT must be a positive integer, not '$ENV{GEMSTONE_THREAD_COUNT}'")
  endif()
  set(gemstone_timeout_scale "$ENV{GEMSTONE_TIMEOUT_SCALE}")
  if(gemstone_timeout_scale STREQUAL "")
    set(gemstone_timeout_scale 1)
  endif()
  if(gemstone_timeout_scale MATCHES "^([0-9]+)(\\.([0-9]+))?$")
    set(gemstone_scale_whole "${CMAKE_MATCH_1}")
    set(gemstone_scale_fraction "${CMAKE_MATCH_3}")
    string(LENGTH "${gemstone_scale_whole}" gemstone_scale_whole_digits)
    string(LENGTH "${gemstone_scale_fraction}" gemstone_scale_fraction_digits)
  endif()
  if(NOT DEFINED gemstone_scale_whole OR gemstone_scale_whole_digits GREATER 9 OR gemstone_scale_fraction_digits GREATER 3)
    set(gemstone_timeout_thousandths 0)
  else()
    string(SUBSTRING "${gemstone_scale_fraction}000" 0 3 gemstone_scale_fraction)
    math(EXPR gemstone_timeout_thousandths "${gemstone_scale_whole} * 1000 + ${gemstone_scale_fraction}")
  endif()
  if(gemstone_timeout_thousandths EQUAL 0)
    message(FATAL_ERROR "GEMSTONE_TIMEOUT_SCALE must be a positive number with at most three decimals, not '${gemstone_timeout_scale}'")
  endif()
  set(gemstone_thread_count "$ENV{GEMSTONE_THREAD_COUNT}")
  if(gemstone_thread_count STREQUAL "")
    find_program(GEMSTONE_NPROC nproc)
    if(GEMSTONE_NPROC)
      # nproc counts the affinity mask, as the tests do, but also obeys these
      execute_process(COMMAND "${CMAKE_COMMAND}" -E env --unset=OMP_NUM_THREADS --unset=OMP_THREAD_LIMIT "${GEMSTONE_NPROC}"
                      OUTPUT_VARIABLE gemstone_thread_count OUTPUT_STRIP_TRAILING_WHITESPACE)
    else()
      cmake_host_system_information(RESULT gemstone_thread_count QUERY NUMBER_OF_LOGICAL_CORES)
    endif()
  endif()
  math(EXPR gemstone_timeout_permille "32000 / ${gemstone_thread_count}")
  if(gemstone_timeout_permille LESS 1000)
    set(gemstone_timeout_permille 1000)
  elseif(gemstone_timeout_permille GREATER 4000)
    set(gemstone_timeout_permille 4000)
  endif()
  math(EXPR GEMSTONE_TIMEOUT_PERMILLE "(${gemstone_timeout_permille} * ${gemstone_timeout_thousandths} + 999) / 1000")

  function(gemstone_scale_timeouts result)
    set(arguments "")
    set(timeout FALSE)
    foreach(argument IN LISTS ARGN)
      if(timeout)
        math(EXPR argument "(${argument} * ${GEMSTONE_TIMEOUT_PERMILLE} + 999) / 1000")
      endif()
      string(COMPARE EQUAL "${argument}" TIMEOUT timeout)
      list(APPEND arguments "${argument}")
    endforeach()
    set("${result}" "${arguments}" PARENT_SCOPE)
  endfunction()

  function(gemstone_discover_tests target)
    gemstone_scale_timeouts(arguments ${ARGN})
    catch_discover_tests("${target}" ${arguments})
  endfunction()

  function(gemstone_set_tests_properties)
    gemstone_scale_timeouts(arguments ${ARGN})
    set_tests_properties(${arguments})
  endfunction()
endif()

function(gemstone_require_abi_implementations)
  cmake_parse_arguments(ARG "FORCE_INCLUDE" "TARGET;PLATFORM" "" ${ARGN})
  if(NOT ARG_TARGET OR NOT ARG_PLATFORM OR ARG_UNPARSED_ARGUMENTS)
    message(FATAL_ERROR
      "gemstone_require_abi_implementations requires TARGET and PLATFORM")
  endif()

  set(generated_directory "${CMAKE_CURRENT_FUNCTION_LIST_DIR}/generated/${ARG_PLATFORM}")
  set(implementation_check "${generated_directory}/gemstone_${ARG_PLATFORM}_impl.cpp")
  target_sources("${ARG_TARGET}" PRIVATE "${implementation_check}")
  target_include_directories("${ARG_TARGET}" PRIVATE
    "${GEMSTONE_NATIVE_INCLUDE_DIRECTORY}"
    "${generated_directory}")
  set_target_properties("${ARG_TARGET}" PROPERTIES
    C_VISIBILITY_PRESET hidden
    CXX_VISIBILITY_PRESET hidden
    VISIBILITY_INLINES_HIDDEN YES)
  if(CMAKE_CXX_COMPILER_ID MATCHES "Clang|GNU")
    set_source_files_properties("${implementation_check}" PROPERTIES
      COMPILE_OPTIONS "-Wno-deprecated-declarations")
  endif()
  if(ARG_FORCE_INCLUDE)
    set(public_header "${generated_directory}/gemstone_${ARG_PLATFORM}_api.h")
    target_compile_options("${ARG_TARGET}" PRIVATE
      "$<$<COMPILE_LANG_AND_ID:CXX,Clang,GNU>:SHELL:-include ${public_header}>")
  endif()
  target_link_options("${ARG_TARGET}" PRIVATE "LINKER:-z,defs" "LINKER:--exclude-libs,ALL")
endfunction()

if(BUILD_TESTING)
  get_filename_component(REPOSITORY_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/.." ABSOLUTE)
  set(CONFORMANCE_ARTIFACT_PROJECT "${REPOSITORY_DIRECTORY}/conformance/artifacts" CACHE PATH
    "Independent conformance artifact build entry point")
  set(CONTAINER_EXECUTABLE podman CACHE STRING "OCI container executable")
  set(ROCM_VERSION 7.1.1 CACHE STRING "ROCm toolchain version")
  set(GEMSTONE_MEMORY_FIXTURE_DIRECTORY "${CMAKE_BINARY_DIR}/rpc/memory-fixtures")
  set_property(GLOBAL APPEND PROPERTY JOB_POOLS "container_build=1")

  function(gemstone_memory_image backend architecture)
    if(TARGET gemstone_memory_${backend}_image)
      return()
    endif()
    set(build "${CMAKE_BINARY_DIR}/artifact-builds/memory-${backend}")
    if(backend STREQUAL "cuda")
      set(outputs)
      foreach(name IN ITEMS kernel exchange atomic fence contended)
        list(APPEND outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/${name}.ptx")
      endforeach()
    elseif(backend STREQUAL "hsa")
      set(outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/kernel.hsaco")
    else()
      set(outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/kernel.spv")
    endif()
    add_custom_target(gemstone_memory_${backend}_image
      COMMAND "${CMAKE_COMMAND}" -S "${CONFORMANCE_ARTIFACT_PROJECT}" -B "${build}" -G Ninja --log-level=WARNING
        -DCONFORMANCE_ARTIFACT_KIND=fixture -DCONFORMANCE_FIXTURE=memory
        "-DCONFORMANCE_BACKEND=${backend}" "-DCONFORMANCE_ARCHITECTURE=${architecture}"
        "-DCONFORMANCE_ARTIFACT_DIRECTORY=${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}"
        "-DCONTAINER_EXECUTABLE=${CONTAINER_EXECUTABLE}" "-DROCM_VERSION=${ROCM_VERSION}"
      COMMAND "${CMAKE_COMMAND}" --build "${build}" --target artifacts --parallel 1 -- --quiet
      BYPRODUCTS ${outputs} JOB_POOL container_build VERBATIM)
  endfunction()

  function(gemstone_memory_client api provider)
    add_executable(gemstone_memory_${api} test/memory.cpp)
    gemstone_project_target(gemstone_memory_${api})
    target_include_directories(gemstone_memory_${api} SYSTEM PRIVATE "$<TARGET_PROPERTY:${provider},INCLUDE_DIRECTORIES>")
    target_include_directories(gemstone_memory_${api} PRIVATE "${CMAKE_CURRENT_FUNCTION_LIST_DIR}/test")
    target_compile_definitions(gemstone_memory_${api} PRIVATE GEMSTONE_MEMORY_IMAGE_DIRECTORY="${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}")
    if(api STREQUAL "levelzero")
      target_link_libraries(gemstone_memory_${api} PRIVATE ze_loader)
      add_dependencies(gemstone_memory_${api} level_zero_loader)
    else()
      target_link_libraries(gemstone_memory_${api} PRIVATE ${provider} ${CMAKE_DL_LIBS})
    endif()
    if(api STREQUAL "cuda")
      gemstone_memory_image(cuda "")
      add_dependencies(gemstone_memory_${api} gemstone_memory_cuda_image)
    elseif(api MATCHES "^(hip|hsa)$")
      gemstone_memory_image(hsa gfx900)
      add_dependencies(gemstone_memory_${api} gemstone_memory_hsa_image)
    else()
      gemstone_memory_image(opencl "")
      add_dependencies(gemstone_memory_${api} gemstone_memory_opencl_image)
    endif()
    if(api STREQUAL "hip")
      target_compile_definitions(gemstone_memory_${api} PRIVATE __HIP_PLATFORM_AMD__=1)
    elseif(api STREQUAL "opencl")
      target_compile_definitions(gemstone_memory_${api} PRIVATE CL_TARGET_OPENCL_VERSION=300)
    endif()
  endfunction()
endif()

add_subdirectory(rpc)

set(_gemstone_install_targets)
if(GEMSTONE_ENABLE_CUDA)
  add_subdirectory(cuda)
  list(APPEND _gemstone_install_targets gemstone_cuda)
endif()
if(GEMSTONE_ENABLE_HSA)
  add_subdirectory(hsa)
  list(APPEND _gemstone_install_targets gemstone_hsa_runtime gemstone_hsakmt)
endif()
if(GEMSTONE_ENABLE_HIP)
  add_subdirectory(hip)
  list(APPEND _gemstone_install_targets gemstone_hip_runtime)
endif()
if(GEMSTONE_ENABLE_OPENCL)
  add_subdirectory(opencl)
  list(APPEND _gemstone_install_targets gemstone_opencl)
endif()
if(GEMSTONE_ENABLE_VULKAN)
  add_subdirectory(vulkan)
  install(TARGETS gemstone_vulkan
    LIBRARY DESTINATION lib/vulkan
    RUNTIME DESTINATION lib/vulkan)
endif()
if(GEMSTONE_ENABLE_LEVEL_ZERO)
  add_subdirectory(levelzero)
  list(APPEND _gemstone_install_targets gemstone_levelzero)
endif()

if(_gemstone_install_targets)
  install(TARGETS ${_gemstone_install_targets} LIBRARY DESTINATION lib)
endif()
