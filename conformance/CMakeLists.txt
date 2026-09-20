cmake_minimum_required(VERSION 3.25)

project(gpu_conformance VERSION 0.1.0 LANGUAGES C CXX)

include(CTest)
include(FetchContent)

# Keep compiler output independent of the checkout and build directory. This
# also applies to the project-owned adapters compiled inside staged CTS and
# miniapp source trees.
foreach(_conformance_language IN ITEMS C CXX HIP)
  add_compile_options(
    "$<$<COMPILE_LANG_AND_ID:${_conformance_language},GNU,Clang,AppleClang,IntelLLVM>:-ffile-prefix-map=${CMAKE_SOURCE_DIR}=.>"
    "$<$<COMPILE_LANG_AND_ID:${_conformance_language},GNU,Clang,AppleClang,IntelLLVM>:-ffile-prefix-map=${CMAKE_BINARY_DIR}=.build>")
endforeach()
add_compile_options(
  "$<$<COMPILE_LANG_AND_ID:CUDA,NVIDIA>:-Xcompiler=-ffile-prefix-map=${CMAKE_SOURCE_DIR}=.>"
  "$<$<COMPILE_LANG_AND_ID:CUDA,NVIDIA>:-Xcompiler=-ffile-prefix-map=${CMAKE_BINARY_DIR}=.build>")
if(UNIX)
  foreach(_conformance_language IN ITEMS C CXX CUDA HIP)
    set(CMAKE_${_conformance_language}_ARCHIVE_CREATE
      "<CMAKE_AR> qcD <TARGET> <LINK_FLAGS> <OBJECTS>")
    set(CMAKE_${_conformance_language}_ARCHIVE_APPEND
      "<CMAKE_AR> qD <TARGET> <LINK_FLAGS> <OBJECTS>")
    set(CMAKE_${_conformance_language}_ARCHIVE_FINISH "<CMAKE_RANLIB> -D <TARGET>")
  endforeach()
endif()

# Toolchain drivers add SDK directories to build RPATHs (notably ROCm's
# OpenMP offload driver). Installed artifacts carry their runtime closure, so
# retaining those host-specific paths would make the bundle non-relocatable.
set(CMAKE_INSTALL_REMOVE_ENVIRONMENT_RPATH TRUE)

set(CONFORMANCE_BACKEND "" CACHE STRING
    "Backend artifact to build (cuda, hip, hsa, opencl, sycl, or openacc)")
set_property(CACHE CONFORMANCE_BACKEND PROPERTY STRINGS cuda hip hsa opencl sycl openacc)
if(CONFORMANCE_BACKEND AND NOT CONFORMANCE_BACKEND MATCHES "^(cuda|hip|hsa|opencl|sycl|openacc)$")
  message(FATAL_ERROR "Unsupported CONFORMANCE_BACKEND: ${CONFORMANCE_BACKEND}")
endif()

option(CONFORMANCE_BUILD_UNIFIED "Build the project-owned unified suite" ON)
option(CONFORMANCE_BUILD_CTS "Build the CTS appropriate for the selected backend" ON)
option(CONFORMANCE_BUILD_MINIAPPS "Build the selected miniapp matrix" ON)
option(CONFORMANCE_HIP_ENABLE_ROCBLAS "Build rocBLAS-backed HIP cases" ON)
if((CONFORMANCE_BUILD_UNIFIED OR CONFORMANCE_BUILD_CTS OR CONFORMANCE_BUILD_MINIAPPS)
    AND NOT CONFORMANCE_BACKEND)
  message(FATAL_ERROR
    "CONFORMANCE_BACKEND is required when building conformance suites")
endif()
set(CMAKE_CUDA_ARCHITECTURES "75" CACHE STRING "CUDA architectures for unified and miniapps")
set(CMAKE_HIP_ARCHITECTURES "gfx908" CACHE STRING "AMDGPU architectures for unified, HSA, and miniapps")
set(CONFORMANCE_MINIAPP_MODELS "" CACHE STRING
    "Optional semicolon-separated app:model miniapp selection")
if(CONFORMANCE_BACKEND MATCHES "^(cuda|openacc)$")
  list(LENGTH CMAKE_CUDA_ARCHITECTURES conformance_architecture_count)
elseif(CONFORMANCE_BACKEND MATCHES "^(hip|hsa)$")
  list(LENGTH CMAKE_HIP_ARCHITECTURES conformance_architecture_count)
else()
  set(conformance_architecture_count 1)
endif()
if(NOT conformance_architecture_count EQUAL 1)
  message(FATAL_ERROR "Each conformance build must target exactly one architecture")
endif()

# Subtrees declare only runtime libraries that dependency scanning cannot find.
# The root install step owns the generic closure and relocation mechanism.
function(conformance_runtime_search_directory directory)
  set_property(GLOBAL APPEND PROPERTY CONFORMANCE_RUNTIME_SEARCH_DIRECTORIES "${directory}")
endfunction()

function(conformance_runtime_plugins)
  foreach(pattern IN LISTS ARGN)
    file(GLOB matches "${pattern}")
    list(FILTER matches INCLUDE REGEX "\\.(dll|dylib|so)(\\.[0-9]+)*$")
    set_property(GLOBAL APPEND PROPERTY CONFORMANCE_RUNTIME_PLUGINS ${matches})
  endforeach()
endfunction()

function(conformance_runtime_prefer filename path_regex)
  set_property(GLOBAL APPEND PROPERTY CONFORMANCE_RUNTIME_PREFERENCES
    "${filename}|${path_regex}")
endfunction()

# Fetch immutable source archives without letting source-only dependencies add
# their own targets to the build before the owning subtree stages them.
function(conformance_fetch_url name url sha256 output_variable)
  FetchContent_Declare(${name}
    URL "${url}" URL_HASH "SHA256=${sha256}"
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
    SOURCE_SUBDIR .conformance-source-only)
  FetchContent_MakeAvailable(${name})
  set(${output_variable} "${${name}_SOURCE_DIR}" PARENT_SCOPE)
endfunction()

set(conformance_artifact_targets)

set(glaze_INSTALL OFF CACHE BOOL "" FORCE)
FetchContent_Declare(glaze
  URL https://github.com/stephenberry/glaze/archive/refs/tags/v7.9.1.tar.gz
  URL_HASH SHA256=d6dee391276f5375672c35d06058e4fd8f1f30f62bae163a004b3bd13a4e2ae3
  DOWNLOAD_EXTRACT_TIMESTAMP TRUE)
FetchContent_MakeAvailable(glaze)

set(MAGIC_ENUM_OPT_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
set(MAGIC_ENUM_OPT_BUILD_TESTS OFF CACHE BOOL "" FORCE)
set(MAGIC_ENUM_OPT_INSTALL OFF CACHE BOOL "" FORCE)
FetchContent_Declare(magic_enum
  URL https://github.com/Neargye/magic_enum/archive/refs/tags/v0.9.8.tar.gz
  URL_HASH SHA256=1e54959a3f3cb675938d858603ad69d0f3f7c82439fc2bf86d7232daec2bd10e
  DOWNLOAD_EXTRACT_TIMESTAMP TRUE)
FetchContent_MakeAvailable(magic_enum)
install(FILES "${magic_enum_SOURCE_DIR}/LICENSE"
  DESTINATION licenses RENAME magic_enum.txt COMPONENT support)

if(NOT CONFORMANCE_BACKEND OR BUILD_TESTING)
  FetchContent_Declare(fire
    URL https://raw.githubusercontent.com/kongaskristjan/fire-hpp/c31b9d6593ee4edfdf9df875d006e26d9a8df879/include/fire-hpp/fire.hpp
    URL_HASH SHA256=f9b82674d0c0a7322043fe837bce895d5798d18585f0e4b1ff105418a8a9e2f8
    DOWNLOAD_NO_EXTRACT YES)
  FetchContent_MakeAvailable(fire)
  set(REPROC++ ON CACHE BOOL "" FORCE)
  set(REPROC_EXAMPLES OFF CACHE BOOL "" FORCE)
  set(REPROC_INSTALL OFF CACHE BOOL "" FORCE)
  set(REPROC_TEST OFF CACHE BOOL "" FORCE)
  FetchContent_Declare(reproc
    URL https://github.com/DaanDeMeyer/reproc/archive/refs/tags/v14.2.7.tar.gz
    URL_HASH SHA256=77914ba566356d86986db240ecdb1762dfdd55bb1e8e127d016d9c5c4c9300e8
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
    SOURCE_SUBDIR .conformance-source-only)
  FetchContent_MakeAvailable(reproc)
  include(cmake/ProcessGroups.cmake)
  conformance_patch_reproc("${reproc_SOURCE_DIR}")
  add_subdirectory("${reproc_SOURCE_DIR}" "${reproc_BINARY_DIR}" EXCLUDE_FROM_ALL)
  add_library(conformance_driver STATIC src/driver.cpp src/profile.cpp src/environment.cpp src/process.cpp)
  target_compile_features(conformance_driver PUBLIC cxx_std_23)
  target_include_directories(conformance_driver PUBLIC "${CMAKE_CURRENT_SOURCE_DIR}/include" PRIVATE src)
  target_link_libraries(conformance_driver PUBLIC glaze::glaze magic_enum::magic_enum reproc++)
  add_executable(conformance src/main.cpp)
  target_include_directories(conformance PRIVATE "${fire_SOURCE_DIR}")
  target_link_libraries(conformance PRIVATE conformance_driver)
  set_target_properties(conformance PROPERTIES RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin")
endif()

if(BUILD_TESTING OR CONFORMANCE_BUILD_UNIFIED OR CONFORMANCE_BUILD_CTS OR
    CONFORMANCE_BUILD_MINIAPPS)
  set(CATCH_BUILD_EXAMPLES OFF CACHE BOOL "" FORCE)
  set(CATCH_BUILD_EXTRA_TESTS OFF CACHE BOOL "" FORCE)
  set(CATCH_ENABLE_REPRODUCIBLE_BUILD ON CACHE BOOL "" FORCE)
  set(CATCH_INSTALL_DOCS OFF CACHE BOOL "" FORCE)
  set(CATCH_INSTALL_EXTRAS OFF CACHE BOOL "" FORCE)
  FetchContent_Declare(Catch2
    URL https://github.com/catchorg/Catch2/archive/refs/tags/v3.15.3.tar.gz
    URL_HASH SHA256=b0299ae552918220a7a6e21e7de5b714777f4e8c883fb70c4bb23fe01df8c6e3
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
    SOURCE_SUBDIR .conformance-source-only)
  FetchContent_MakeAvailable(Catch2)
  add_subdirectory("${catch2_SOURCE_DIR}" "${catch2_BINARY_DIR}" EXCLUDE_FROM_ALL)

  install(FILES "${catch2_SOURCE_DIR}/LICENSE.txt"
    DESTINATION licenses RENAME Catch2.txt COMPONENT support)

endif()

if(BUILD_TESTING)

  add_executable(conformance_fixture tests/fixture.cpp)
  target_compile_features(conformance_fixture PRIVATE cxx_std_23)
  target_link_libraries(conformance_fixture PRIVATE Catch2::Catch2)
  add_executable(conformance_unit_tests
    tests/device_lock_test.cpp
    tests/driver_test.cpp
    tests/profile_test.cpp)
  target_compile_features(conformance_unit_tests PRIVATE cxx_std_23)
  target_include_directories(conformance_unit_tests PRIVATE
    "${CMAKE_CURRENT_SOURCE_DIR}/include" "${CMAKE_CURRENT_SOURCE_DIR}/src")
  target_link_libraries(conformance_unit_tests PRIVATE
    Catch2::Catch2WithMain conformance_driver)
  target_compile_definitions(conformance_unit_tests PRIVATE
    CONFORMANCE_PROFILE_DIRECTORY="${CMAKE_CURRENT_SOURCE_DIR}/profiles"
    CONFORMANCE_FIXTURE="$<TARGET_FILE:conformance_fixture>"
    CONFORMANCE_DRIVER="$<TARGET_FILE:conformance>")
  add_dependencies(conformance_unit_tests conformance conformance_fixture)
endif()

# Every configure produces one coherent artifact. GPU compiler drivers and SDKs
# are intentionally never mixed in the same CMake build tree. Each test program
# and its device probe are built against that artifact's single SDK ABI.
if(CONFORMANCE_BACKEND)
  add_library(conformance_device STATIC src/device_runtime.cpp)
  set_target_properties(conformance_device PROPERTIES POSITION_INDEPENDENT_CODE ON)
  target_compile_features(conformance_device PRIVATE cxx_std_20)
  target_include_directories(conformance_device PUBLIC "${CMAKE_CURRENT_SOURCE_DIR}/include")
  target_link_libraries(conformance_device PUBLIC magic_enum::magic_enum)
endif()
if(CONFORMANCE_BACKEND MATCHES "^(cuda|openacc)$")
  string(REPLACE ";" "," conformance_device_targets "${CMAKE_CUDA_ARCHITECTURES}")
  target_compile_definitions(conformance_device PRIVATE
    CONFORMANCE_DEVICE_CUDA=1 CONFORMANCE_DEVICE_TARGETS="${conformance_device_targets}")
  if(CONFORMANCE_BACKEND STREQUAL "cuda")
    find_package(CUDAToolkit REQUIRED)
    get_target_property(conformance_cublas_library CUDA::cublas IMPORTED_LOCATION)
    get_filename_component(conformance_cuda_runtime_directory
      "${conformance_cublas_library}" DIRECTORY)
    target_compile_definitions(conformance_device PRIVATE CONFORMANCE_DEVICE_CUDA_DRIVER=1)
    target_link_libraries(conformance_device PUBLIC CUDA::cuda_driver)
  else()
    file(GLOB nvhpc_cuda_roots LIST_DIRECTORIES TRUE "$ENV{NVHPC_ROOT}/cuda/*")
    list(SORT nvhpc_cuda_roots COMPARE NATURAL ORDER DESCENDING)
    list(GET nvhpc_cuda_roots 0 nvhpc_cuda_root)
    find_path(CONFORMANCE_CUDA_INCLUDE cuda_runtime_api.h
      HINTS "${nvhpc_cuda_root}/include" NO_DEFAULT_PATH REQUIRED)
    find_library(CONFORMANCE_CUDART cudart
      HINTS "${nvhpc_cuda_root}/lib64" NO_DEFAULT_PATH REQUIRED)
    target_include_directories(conformance_device PRIVATE "${CONFORMANCE_CUDA_INCLUDE}")
    target_link_libraries(conformance_device PUBLIC "${CONFORMANCE_CUDART}")
  endif()
elseif(CONFORMANCE_BACKEND MATCHES "^(hip|hsa)$")
  string(REPLACE ";" "," conformance_device_targets "${CMAKE_HIP_ARCHITECTURES}")
  set(ROCM_VERSION "7.1.1" CACHE STRING "ROCm release used to build this artifact")
  if(NOT ROCM_VERSION MATCHES "^([0-9]+)\\.([0-9]+)\\.([0-9]+)$")
    message(FATAL_ERROR "ROCM_VERSION must be major.minor.patch")
  endif()
  set(rocm_major "${CMAKE_MATCH_1}")
  set(rocm_minor "${CMAKE_MATCH_2}")
  find_path(CONFORMANCE_HIP_INCLUDE hip/hip_runtime_api.h PATHS /opt/rocm/include REQUIRED)
  find_library(CONFORMANCE_HIP_RUNTIME amdhip64 PATHS /opt/rocm/lib /opt/rocm/lib64 REQUIRED)
  find_path(CONFORMANCE_HSA_INCLUDE hsa/hsa.h PATHS /opt/rocm/include REQUIRED)
  find_library(CONFORMANCE_HSA_RUNTIME hsa-runtime64 PATHS /opt/rocm/lib /opt/rocm/lib64 REQUIRED)
  string(TOUPPER "${CONFORMANCE_BACKEND}" device_backend)
  target_compile_definitions(conformance_device PRIVATE
    CONFORMANCE_DEVICE_${device_backend}=1 CONFORMANCE_DEVICE_TARGETS="${conformance_device_targets}"
    CONFORMANCE_ROCM_VERSION="${ROCM_VERSION}" CONFORMANCE_ROCM_MAJOR=${rocm_major} CONFORMANCE_ROCM_MINOR=${rocm_minor}
    __HIP_PLATFORM_AMD__=1)
  target_include_directories(conformance_device PRIVATE "${CONFORMANCE_HIP_INCLUDE}" "${CONFORMANCE_HSA_INCLUDE}")
  target_link_libraries(conformance_device PUBLIC "${CONFORMANCE_HIP_RUNTIME}" "${CONFORMANCE_HSA_RUNTIME}")
elseif(CONFORMANCE_BACKEND STREQUAL "opencl")
  find_package(OpenCL REQUIRED)
  target_compile_definitions(conformance_device PRIVATE CONFORMANCE_DEVICE_OPENCL=1)
  target_link_libraries(conformance_device PUBLIC OpenCL::OpenCL)
elseif(CONFORMANCE_BACKEND STREQUAL "sycl")
  target_compile_definitions(conformance_device PRIVATE CONFORMANCE_DEVICE_SYCL=1)
  target_compile_options(conformance_device PRIVATE -fsycl)
  target_link_options(conformance_device PUBLIC -fsycl)
endif()

if(CONFORMANCE_BACKEND)
  add_executable(conformance-device src/device_main.cpp)
  target_compile_features(conformance-device PRIVATE cxx_std_23)
  target_link_libraries(conformance-device PRIVATE conformance_device glaze::glaze)
  if(APPLE)
    set_property(TARGET conformance-device PROPERTY INSTALL_RPATH "@loader_path/lib")
  elseif(UNIX)
    set_property(TARGET conformance-device PROPERTY INSTALL_RPATH "$ORIGIN/lib")
  endif()
  install(TARGETS conformance-device RUNTIME DESTINATION . COMPONENT architecture)
  list(APPEND conformance_artifact_targets conformance-device)
endif()

# Runtime closure is a property of the backend artifact, not of an individual
# suite. Keep the plugin/search policy in one place so all suite combinations
# produce the same relocatable runtime.
if(CONFORMANCE_BACKEND STREQUAL "cuda")
  conformance_runtime_search_directory("${conformance_cuda_runtime_directory}")
  conformance_runtime_plugins(
    "${conformance_cuda_runtime_directory}/libcublas.so*"
    "${conformance_cuda_runtime_directory}/libcublasLt.so*")
elseif(CONFORMANCE_BACKEND MATCHES "^(hip|hsa)$")
  conformance_runtime_search_directory(/opt/rocm/lib)
  conformance_runtime_search_directory(/opt/rocm/lib64)
  conformance_runtime_prefer(libhsa-runtime64.so.1 "^/opt/rocm")
  conformance_runtime_plugins("${CONFORMANCE_HIP_RUNTIME}" "${CONFORMANCE_HSA_RUNTIME}")
  conformance_runtime_plugins(
    /opt/rocm/llvm/lib/libomptarget.so*
    /opt/rocm/lib/libhsa-amd-aqlprofile64.so*
    /opt/rocm/lib/librocprofiler*.so*
    /opt/rocm/lib/libroctx*.so*)
  install(FILES /usr/share/libdrm/amdgpu.ids
    DESTINATION "data/libdrm" COMPONENT support OPTIONAL)
elseif(CONFORMANCE_BACKEND STREQUAL "opencl")
  get_filename_component(conformance_opencl_runtime_directory "${OpenCL_LIBRARY}" DIRECTORY)
  conformance_runtime_search_directory("${conformance_opencl_runtime_directory}")
  conformance_runtime_plugins("${conformance_opencl_runtime_directory}/libOpenCL.so*")
elseif(CONFORMANCE_BACKEND STREQUAL "sycl")
  file(GLOB conformance_oneapi_runtime_directories
    /opt/intel/oneapi/compiler/*/lib /opt/intel/oneapi/mkl/*/lib
    /opt/intel/oneapi/tbb/*/lib /opt/intel/oneapi/umf/*/lib)
  foreach(directory IN LISTS conformance_oneapi_runtime_directories)
    conformance_runtime_search_directory("${directory}")
  endforeach()
  conformance_runtime_plugins(
    /opt/intel/oneapi/compiler/*/lib/libsycl.so*
    /opt/intel/oneapi/compiler/*/lib/libur_adapter*.so*
    /opt/intel/oneapi/compiler/*/lib/libOpenCL.so*
    /opt/intel/oneapi/compiler/*/lib/libintelocl.so*
    /opt/intel/oneapi/compiler/*/lib/libsycl-jit.so*
    /opt/intel/oneapi/compiler/*/lib/libsycl-preview.so*
    /opt/intel/oneapi/compiler/*/lib/libomptarget*.so*
    /opt/intel/oneapi/mkl/*/lib/libmkl_core.so*
    /opt/intel/oneapi/mkl/*/lib/libmkl_intel_lp64.so*
    /opt/intel/oneapi/mkl/*/lib/libmkl_intel_thread.so*
    /opt/intel/oneapi/mkl/*/lib/libmkl_sycl_blas.so*
    /opt/intel/oneapi/tbb/*/lib/libtbb.so*
    /opt/intel/oneapi/umf/*/lib/libumf.so*)
  conformance_runtime_prefer(libOpenCL.so.1 "^/opt/intel/oneapi/compiler/")
elseif(CONFORMANCE_BACKEND STREQUAL "openacc")
  file(GLOB conformance_nvhpc_runtime_directories
    /opt/nvidia/hpc_sdk/Linux_x86_64/*/compilers/lib)
  foreach(directory IN LISTS conformance_nvhpc_runtime_directories)
    conformance_runtime_search_directory("${directory}")
    conformance_runtime_plugins("${directory}/libacc*.so*" "${directory}/libnv*.so*"
      "${directory}/libpg*.so*")
  endforeach()
endif()

function(conformance_register_test_program target timeout)
  if(NOT TARGET "${target}")
    message(FATAL_ERROR "Unknown conformance test program: ${target}")
  endif()
  if(NOT "${timeout}" MATCHES "^[1-9][0-9]*$")
    message(FATAL_ERROR "Invalid timeout for conformance test program ${target}: ${timeout}")
  endif()
  get_target_property(output_name "${target}" OUTPUT_NAME)
  if(NOT output_name)
    set(output_name "${target}")
  endif()
  get_property(registered_names GLOBAL PROPERTY CONFORMANCE_TEST_PROGRAM_NAMES)
  if(output_name IN_LIST registered_names)
    message(FATAL_ERROR "Duplicate conformance test program name: ${output_name}")
  endif()
  set_target_properties("${target}" PROPERTIES
    RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/programs/${CONFORMANCE_BACKEND}")
  if(APPLE)
    set_property(TARGET "${target}" PROPERTY INSTALL_RPATH "@loader_path/lib")
  elseif(UNIX)
    set_property(TARGET "${target}" PROPERTY INSTALL_RPATH "$ORIGIN/lib")
  endif()
  install(TARGETS "${target}" RUNTIME DESTINATION . COMPONENT architecture)
  set_property(GLOBAL APPEND PROPERTY CONFORMANCE_TEST_PROGRAM_NAMES "${output_name}")
  set_property(GLOBAL APPEND PROPERTY CONFORMANCE_TEST_PROGRAMS
    "${output_name}\t${timeout}")
endfunction()
if((CONFORMANCE_BUILD_UNIFIED AND NOT CONFORMANCE_BACKEND STREQUAL "openacc") OR
    TARGET conformance_unit_tests)
  add_subdirectory(unified)
  if(TARGET unified)
    list(APPEND conformance_artifact_targets unified)
  endif()
endif()

if(CONFORMANCE_BUILD_CTS AND CONFORMANCE_BACKEND MATCHES "^(opencl|sycl)$")
  add_subdirectory("cts/${CONFORMANCE_BACKEND}")
  list(APPEND conformance_artifact_targets conformance_cts_artifact)
endif()
if(CONFORMANCE_BUILD_MINIAPPS AND
    CONFORMANCE_BACKEND MATCHES "^(cuda|hip|opencl|sycl|openacc)$")
  add_subdirectory(miniapps)
  list(APPEND conformance_artifact_targets conformance_miniapps_artifact)
endif()

get_property(conformance_test_programs GLOBAL PROPERTY CONFORMANCE_TEST_PROGRAMS)
if(conformance_test_programs)
  list(JOIN conformance_test_programs "\n" conformance_test_program_manifest)
  file(GENERATE OUTPUT "${CMAKE_CURRENT_BINARY_DIR}/test-programs.txt"
    CONTENT "${conformance_test_program_manifest}\n")
  install(FILES "${CMAKE_CURRENT_BINARY_DIR}/test-programs.txt"
    DESTINATION . COMPONENT architecture)
endif()

if(TARGET conformance_unit_tests)
  include("${catch2_SOURCE_DIR}/extras/Catch.cmake")
  catch_discover_tests(conformance_unit_tests
    TEST_PREFIX "unit/"
    PROPERTIES LABELS unit)
  add_custom_target(conformance-unit-tests
    COMMAND "${CMAKE_CTEST_COMMAND}" --test-dir "${CMAKE_BINARY_DIR}"
      --output-on-failure --label-regex unit
    DEPENDS conformance_unit_tests
    USES_TERMINAL VERBATIM)
endif()

get_property(conformance_runtime_search_directories GLOBAL
  PROPERTY CONFORMANCE_RUNTIME_SEARCH_DIRECTORIES)
list(REMOVE_DUPLICATES conformance_runtime_search_directories)
get_property(conformance_runtime_plugins GLOBAL PROPERTY CONFORMANCE_RUNTIME_PLUGINS)
get_property(conformance_runtime_preferences GLOBAL PROPERTY CONFORMANCE_RUNTIME_PREFERENCES)
list(REMOVE_DUPLICATES conformance_runtime_plugins)
list(REMOVE_DUPLICATES conformance_runtime_preferences)
if(WIN32)
  set(conformance_runtime_excludes
    "api-ms-win-.*" "ext-ms-.*" "kernel32.dll" "user32.dll" "advapi32.dll"
    "nvcuda.dll" "nvapi.*\\.dll" "ze_intel_gpu.*\\.dll")
else()
  set(conformance_runtime_excludes
    "ld-linux.*" "libc\\.so.*" "libm\\.so.*" "libpthread\\.so.*"
    "libdl\\.so.*" "librt\\.so.*" "libgcc_s\\.so.*" "libstdc\\+\\+\\.so.*"
    "libcuda\\.so.*" "libnvidia-.*" "libze_intel_gpu\\.so.*")
endif()

function(conformance_install_arguments variable output)
  set(arguments)
  foreach(value IN LISTS ${variable})
    string(APPEND arguments " [==[${value}]==]")
  endforeach()
  set(${output} "${arguments}" PARENT_SCOPE)
endfunction()
conformance_install_arguments(conformance_runtime_search_directories runtime_search_arguments)
conformance_install_arguments(conformance_runtime_plugins runtime_plugin_arguments)
conformance_install_arguments(conformance_runtime_preferences runtime_preference_arguments)
conformance_install_arguments(conformance_runtime_excludes runtime_exclude_arguments)
set(conformance_runtime_executable_patterns "\${runtime_input}/conformance-device${CMAKE_EXECUTABLE_SUFFIX}")
foreach(entry IN LISTS conformance_test_programs)
  string(REGEX REPLACE "\t.*$" "" program "${entry}")
  list(APPEND conformance_runtime_executable_patterns "\${runtime_input}/${program}${CMAKE_EXECUTABLE_SUFFIX}")
endforeach()
conformance_install_arguments(conformance_runtime_executable_patterns runtime_executable_arguments)
configure_file(cmake/RuntimeDependencies.cmake
  "${CMAKE_CURRENT_BINARY_DIR}/RuntimeDependencies.cmake" COPYONLY)
string(CONCAT runtime_install_code
  "include([==[${CMAKE_CURRENT_BINARY_DIR}/RuntimeDependencies.cmake]==])\n"
  "set(runtime_root \"\$ENV{DESTDIR}\${CMAKE_INSTALL_PREFIX}\")\n"
  "if(DEFINED ENV{CONFORMANCE_RUNTIME_INPUT} AND NOT \"\$ENV{CONFORMANCE_RUNTIME_INPUT}\" STREQUAL \"\")\n"
  "  set(runtime_input \"\$ENV{CONFORMANCE_RUNTIME_INPUT}\")\n"
  "else()\n"
  "  set(runtime_input \"\${runtime_root}\")\n"
  "endif()\n"
  "bundle_runtime_dependencies(\n"
  "  DESTINATION \"\${runtime_root}/lib\"\n"
  "  EXECUTABLE_PATTERNS${runtime_executable_arguments}\n"
  "  LIBRARY_PATTERNS \"\${runtime_input}/*.dll\" \"\${runtime_input}/*.dylib\" \"\${runtime_input}/*.so*\"\n"
  "  SEARCH_DIRECTORIES${runtime_search_arguments}\n"
  "  PLUGINS${runtime_plugin_arguments}\n"
  "  PREFERENCES${runtime_preference_arguments}\n"
  "  EXCLUDE_REGEXES${runtime_exclude_arguments})\n")
install(CODE "${runtime_install_code}" COMPONENT support)

add_custom_target(conformance-artifact ALL)
if(conformance_artifact_targets)
  add_dependencies(conformance-artifact ${conformance_artifact_targets})
endif()
