cmake_minimum_required(VERSION 3.25)
project(gemstone-native LANGUAGES C CXX)

include(CTest)
include(FetchContent)

option(GEMSTONE_ENABLE_CUDA "Build the CUDA facade" ON)
option(GEMSTONE_ENABLE_HSA "Build the HSA and KMT facades" ON)
option(GEMSTONE_ENABLE_HIP "Build the HIP facade" ON)
option(GEMSTONE_ENABLE_OPENCL "Build the OpenCL facade" ON)
option(GEMSTONE_ENABLE_VULKAN "Build the Vulkan compute facade" ON)
option(GEMSTONE_ENABLE_LEVEL_ZERO "Build the Level Zero facade" ON)

if(GEMSTONE_ENABLE_CUDA OR GEMSTONE_ENABLE_HSA OR GEMSTONE_ENABLE_HIP OR
   GEMSTONE_ENABLE_OPENCL OR GEMSTONE_ENABLE_LEVEL_ZERO)
  include(cmake/Aspartame.cmake)
endif()

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

# Keep compiler output independent of the checkout and build directory. GCC's
# and Clang's file-prefix map covers debug paths and file-name macros.
foreach(_gemstone_language IN ITEMS CXX HIP)
  add_compile_options(
    "$<$<COMPILE_LANG_AND_ID:${_gemstone_language},GNU,Clang,AppleClang,IntelLLVM>:-ffile-prefix-map=${CMAKE_SOURCE_DIR}=.>"
    "$<$<COMPILE_LANG_AND_ID:${_gemstone_language},GNU,Clang,AppleClang,IntelLLVM>:-ffile-prefix-map=${CMAKE_BINARY_DIR}=.build>")
endforeach()
add_compile_options(
  "$<$<COMPILE_LANG_AND_ID:CUDA,NVIDIA>:-Xcompiler=-ffile-prefix-map=${CMAKE_SOURCE_DIR}=.>"
  "$<$<COMPILE_LANG_AND_ID:CUDA,NVIDIA>:-Xcompiler=-ffile-prefix-map=${CMAKE_BINARY_DIR}=.build>")
if(UNIX)
  foreach(_gemstone_language IN ITEMS CXX CUDA HIP)
    set(CMAKE_${_gemstone_language}_ARCHIVE_CREATE
      "<CMAKE_AR> qcD <TARGET> <LINK_FLAGS> <OBJECTS>")
    set(CMAKE_${_gemstone_language}_ARCHIVE_APPEND
      "<CMAKE_AR> qD <TARGET> <LINK_FLAGS> <OBJECTS>")
    set(CMAKE_${_gemstone_language}_ARCHIVE_FINISH "<CMAKE_RANLIB> -D <TARGET>")
  endforeach()
endif()

# Every facade shares one build directory; keep libraries and native test
# executables at its root so sbt can expose one location to JVM server tests.
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
      "${CMAKE_CURRENT_FUNCTION_LIST_DIR}/test")
    target_compile_options("${target}" PRIVATE
      "$<$<COMPILE_LANG_AND_ID:CXX,Clang,GNU>:-Wall;-Wextra;-Wpedantic;-Werror>")
  endfunction()

  function(gemstone_discover_tests target)
    catch_discover_tests("${target}" ${ARGN})
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
  if(APPLE)
    target_link_options("${ARG_TARGET}" PRIVATE "LINKER:-undefined,error")
  elseif(UNIX)
    target_link_options("${ARG_TARGET}" PRIVATE "LINKER:-z,defs" "LINKER:--exclude-libs,ALL")
  endif()
endfunction()

# Consumers invoke the artifact builder; it owns fixture sources and toolchains.
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
      foreach(name IN ITEMS kernel atomic fence contended)
        list(APPEND outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/${name}.ptx")
      endforeach()
    elseif(backend STREQUAL "hsa")
      set(outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/kernel.hsaco")
    else()
      set(outputs "${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}/kernel.spv")
    endif()
    # Invoke the independent build every time so its own dependency graph sees
    # changes to its inputs. Unchanged artifacts remain a Ninja no-op.
    add_custom_target(gemstone_memory_${backend}_image
      COMMAND "${CMAKE_COMMAND}" -S "${CONFORMANCE_ARTIFACT_PROJECT}" -B "${build}" -G Ninja
        -DCONFORMANCE_ARTIFACT_KIND=fixture -DCONFORMANCE_FIXTURE=memory
        "-DCONFORMANCE_BACKEND=${backend}" "-DCONFORMANCE_ARCHITECTURE=${architecture}"
        "-DCONFORMANCE_ARTIFACT_DIRECTORY=${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}"
        "-DCONTAINER_EXECUTABLE=${CONTAINER_EXECUTABLE}" "-DROCM_VERSION=${ROCM_VERSION}"
      COMMAND "${CMAKE_COMMAND}" --build "${build}" --target artifacts --parallel 1
      BYPRODUCTS ${outputs} JOB_POOL container_build VERBATIM)
  endfunction()

  function(gemstone_memory_client api provider)
    add_executable(gemstone_memory_${api} test/memory.cpp)
    target_compile_features(gemstone_memory_${api} PRIVATE cxx_std_20)
    target_include_directories(gemstone_memory_${api} PRIVATE "$<TARGET_PROPERTY:${provider},INCLUDE_DIRECTORIES>")
    target_compile_definitions(gemstone_memory_${api} PRIVATE GEMSTONE_MEMORY_IMAGE_DIRECTORY="${GEMSTONE_MEMORY_FIXTURE_DIRECTORY}")
    if(api STREQUAL "levelzero")
      target_link_libraries(gemstone_memory_${api} PRIVATE ${LEVEL_ZERO_LOADER_LIBRARY})
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
      target_compile_options(gemstone_memory_${api} PRIVATE -Wno-attributes)
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
