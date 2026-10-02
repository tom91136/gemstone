set(miniapp_supported_cuda cuda)
set(miniapp_supported_hip hip omp-target kokkos)
set(miniapp_supported_opencl ocl)
set(miniapp_supported_sycl sycl sycl-usm sycl-acc)
set(miniapp_supported_openacc acc)
set(miniapp_supported_vulkan vulkan)
if(CONFORMANCE_BACKEND MATCHES "^(cuda|openacc)$")
  list(LENGTH CMAKE_CUDA_ARCHITECTURES architecture_count)
  if(NOT architecture_count EQUAL 1)
    message(FATAL_ERROR "CUDA and OpenACC miniapps require exactly one architecture")
  endif()
endif()

function(conformance_select_miniapp_models app supported output)
  set_property(GLOBAL APPEND PROPERTY CONFORMANCE_MINIAPP_NAMES "${app}")
  set(selected)
  if(CONFORMANCE_MINIAPP_MODELS STREQUAL "")
    foreach(model IN LISTS miniapp_${app}_models)
      if(model IN_LIST miniapp_supported_${CONFORMANCE_BACKEND})
        list(APPEND selected "${model}")
      endif()
    endforeach()
  else()
    foreach(specification IN LISTS CONFORMANCE_MINIAPP_MODELS)
      string(REPLACE ":" ";" fields "${specification}")
      list(LENGTH fields field_count)
      if(NOT field_count EQUAL 2)
        message(FATAL_ERROR "Invalid miniapp specification: ${specification}")
      endif()
      list(GET fields 0 requested_app)
      list(GET fields 1 requested_model)
      if(requested_app STREQUAL app)
        list(APPEND selected "${requested_model}")
      endif()
    endforeach()
  endif()
  foreach(model IN LISTS selected)
    if(NOT model IN_LIST supported)
      message(FATAL_ERROR "${app} does not provide the ${model} model")
    endif()
    if(NOT model IN_LIST miniapp_supported_${CONFORMANCE_BACKEND})
      message(FATAL_ERROR "${app}:${model} is not a supported ${CONFORMANCE_BACKEND} model")
    endif()
  endforeach()
  list(REMOVE_DUPLICATES selected)
  set(${output} "${selected}" PARENT_SCOPE)
endfunction()

function(conformance_register_miniapp app)
  conformance_select_miniapp_models("${app}" "${miniapp_${app}_models}"
    selected_models)
  if(NOT selected_models)
    set("miniapp_${app}_enabled" OFF PARENT_SCOPE)
    return()
  endif()
  set(source_variable "miniapp_${app}_source")
  conformance_fetch_url("conformance_${app}" "${source_variable}")
  foreach(model IN LISTS selected_models)
    conformance_add_miniapp("${app}" "${model}")
  endforeach()
  if(NOT "${miniapp_${app}_license}" STREQUAL "")
    install(FILES "${${source_variable}}/${miniapp_${app}_license}"
      DESTINATION licenses RENAME "${miniapp_${app}_license_name}"
      COMPONENT support)
  endif()
  set("${source_variable}" "${${source_variable}}" PARENT_SCOPE)
  set("miniapp_${app}_enabled" ON PARENT_SCOPE)
  set(miniapp_targets "${miniapp_targets}" PARENT_SCOPE)
endfunction()

function(conformance_fetch_kokkos output)
  get_property(source GLOBAL PROPERTY CONFORMANCE_KOKKOS_SOURCE)
  if(NOT source)
    conformance_fetch_url(conformance_kokkos source)
    set_property(GLOBAL PROPERTY CONFORMANCE_KOKKOS_SOURCE "${source}")
    install(FILES "${source}/LICENSE"
      DESTINATION licenses RENAME Kokkos.txt COMPONENT support)
  endif()
  set(${output} "${source}" PARENT_SCOPE)
endfunction()

function(patch_exact path before after)
  file(READ "${path}" content)
  string(FIND "${content}" "${before}" location)
  if(location EQUAL -1)
    message(FATAL_ERROR "Pinned miniapp integration point changed in ${path}")
  endif()
  string(REPLACE "${before}" "${after}" content "${content}")
  file(WRITE "${path}" "${content}")
endfunction()

# Kokkos uses these family prefixes to select wave32 versus wave64.
function(kokkos_architecture output)
  list(GET CMAKE_HIP_ARCHITECTURES 0 architecture)
  if(architecture MATCHES "^gfx1")
    string(REPLACE "gfx" "NAVI" arch "${architecture}")
  else()
    string(REPLACE "gfx" "VEGA" arch "${architecture}")
  endif()
  string(TOUPPER "${arch}" arch)
  set(${output} "${arch}" PARENT_SCOPE)
endfunction()

function(stage_miniapp app model destination)
  file(REMOVE_RECURSE "${destination}")
  file(COPY "${miniapp_${app}_source}/" DESTINATION "${destination}")
  file(COPY_FILE "${PROJECT_SOURCE_DIR}/src/upstream_test.cpp" "${destination}/conformance_test.cpp")
  set(cmake_file "${destination}/CMakeLists.txt")
  file(READ "${cmake_file}" cmake_content)
  string(REGEX REPLACE
    "# setup git_watcher[^\n]*\nset\\(PRE_CONFIGURE_FILE[^\n]*\nset\\(POST_CONFIGURE_FILE[^\n]*\n[\n ]*set\\(GIT_FAIL_IF_NONZERO_EXIT[^\n]*\ninclude\\([^\n]*git_watcher[^\n]*\nadd_dependencies\\([^\n]*check_git[^\n]*\n"
    "" cmake_content "${cmake_content}")
  file(WRITE "${cmake_file}" "${cmake_content}")
  if(model STREQUAL "cuda")
    patch_exact("${destination}/src/cuda/model.cmake"
      "cmake_policy(SET CMP0104 OLD)" "cmake_policy(SET CMP0104 NEW)")
  endif()
  cmake_language(CALL "miniapp_${app}_patch" "${model}" "${destination}")
  if(model STREQUAL "kokkos")
    conformance_fetch_kokkos(kokkos_source)
    file(COPY "${kokkos_source}/" DESTINATION "${destination}/.kokkos")
    patch_exact("${destination}/src/kokkos/model.cmake"
      [=[        add_subdirectory(${KOKKOS_IN_TREE} ${CMAKE_BINARY_DIR}/kokkos)]=]
      [=[        if(NOT TARGET Kokkos::kokkos)
            add_subdirectory(${KOKKOS_IN_TREE} ${CMAKE_BINARY_DIR}/kokkos)
        endif()]=])
    patch_exact("${destination}/.kokkos/core/src/HIP/Kokkos_HIP_Instance.cpp"
      "hipProp.gcnArch" "hipProp.gcnArchName")
    kokkos_architecture(kokkos_arch)
    patch_exact("${destination}/.kokkos/cmake/kokkos_arch.cmake"
      [=[LIST(APPEND SUPPORTED_AMD_GPUS       MI200    MI100    MI50/60  RX7900XTX  V620/W6800)
LIST(APPEND SUPPORTED_AMD_ARCHS      VEGA90A  VEGA908  VEGA906  NAVI1100   NAVI1030)
LIST(APPEND CORRESPONDING_AMD_FLAGS  gfx90a   gfx908   gfx906   gfx1100    gfx1030)]=]
      "LIST(APPEND SUPPORTED_AMD_GPUS ${CMAKE_HIP_ARCHITECTURES})\nLIST(APPEND SUPPORTED_AMD_ARCHS ${kokkos_arch})\nLIST(APPEND CORRESPONDING_AMD_FLAGS ${CMAKE_HIP_ARCHITECTURES})")
    set(config "${destination}/.kokkos/cmake/KokkosCore_config.h.in")
    file(STRINGS "${config}" existing REGEX "^#cmakedefine KOKKOS_ARCH_${kokkos_arch}$")
    if(NOT existing)
      file(APPEND "${config}" "\n#cmakedefine KOKKOS_ARCH_${kokkos_arch}\n")
    endif()
  endif()
  set(primary_model_${CONFORMANCE_BACKEND} "${CONFORMANCE_BACKEND}")
  set(primary_model_opencl ocl)
  set(primary_model_openacc acc)
  if(model STREQUAL "${primary_model_${CONFORMANCE_BACKEND}}")
    set(program_name "${app}")
  else()
    string(REPLACE "-" "_" program_name "${app}_${model}")
  endif()
  set(program_target "conformance_test_${program_name}")
  patch_exact("${cmake_file}" "${miniapp_${app}_executable}"
    "set(EXE_NAME ${program_target})\n${miniapp_${app}_catch_executable}")
  patch_exact("${cmake_file}" [=[install(TARGETS ${EXE_NAME} DESTINATION bin)]=] "")
  file(READ "${cmake_file}" cmake_content)
  string(REPLACE "add_custom_target(extract_compile_commands "
    "add_custom_target(extract_compile_commands_${program_name} " cmake_content "${cmake_content}")
  string(REPLACE [=[add_dependencies(${EXE_NAME} extract_compile_commands)]=]
    "add_dependencies(\${EXE_NAME} extract_compile_commands_${program_name})" cmake_content "${cmake_content}")
  file(WRITE "${cmake_file}" "${cmake_content}")
  file(APPEND "${cmake_file}" "\n"
    "set_source_files_properties(${miniapp_${app}_main} PROPERTIES COMPILE_DEFINITIONS main=conformance_upstream_main)\n"
    "target_include_directories(\${EXE_NAME} PRIVATE \"\${CMAKE_CURRENT_SOURCE_DIR}\")\n"
    "set_target_properties(\${EXE_NAME} PROPERTIES OUTPUT_NAME \"${program_name}\")\n"
    "set(CONFORMANCE_MINIAPP_TARGET \"\${EXE_NAME}\" PARENT_SCOPE)\n")
endfunction()

function(configure_kokkos stage)
  # Keep Kokkos host and device objects compatible with the compiler runtime.
  set(CMAKE_POSITION_INDEPENDENT_CODE ON CACHE BOOL "" FORCE)
  set(KOKKOS_IN_TREE "${stage}/.kokkos" CACHE PATH "" FORCE)
  set(Kokkos_ENABLE_HIP ON CACHE BOOL "" FORCE)
  set(Kokkos_ENABLE_HIP_RELOCATABLE_DEVICE_CODE OFF CACHE BOOL "" FORCE)
  foreach(kokkos_arch IN ITEMS
      VEGA803 VEGA90A VEGA908 VEGA906 VEGA942 VEGA950
      NAVI1030 NAVI1100 NAVI1150 NAVI1200 NAVI1201)
    set(Kokkos_ARCH_${kokkos_arch} OFF CACHE BOOL "" FORCE)
  endforeach()
  kokkos_architecture(kokkos_arch)
  set(Kokkos_ARCH_${kokkos_arch} ON CACHE BOOL "" FORCE)
  execute_process(COMMAND "${CMAKE_CXX_COMPILER}" --print-resource-dir
    RESULT_VARIABLE resource_status OUTPUT_VARIABLE resource_directory
    OUTPUT_STRIP_TRAILING_WHITESPACE ERROR_QUIET)
  if(NOT resource_status EQUAL 0)
    message(FATAL_ERROR "Kokkos requires a Clang-compatible C++ compiler resource directory")
  endif()
  find_file(clang_rt_library libclang_rt.builtins-x86_64.a
    HINTS "${resource_directory}/lib/linux" NO_DEFAULT_PATH)
  if(NOT clang_rt_library)
    message(FATAL_ERROR "Kokkos requires ROCm's libclang_rt.builtins-x86_64.a")
  endif()
  set(CLANG_RT_LIBRARY "${clang_rt_library}" CACHE FILEPATH "" FORCE)
endfunction()

function(configure_miniapp app model stage target_output)
  string(REPLACE "-" "_" model_key "${model}")
  set(build_model "${model}")
  if(DEFINED miniapp_${app}_${model_key}_upstream_model)
    set(build_model "${miniapp_${app}_${model_key}_upstream_model}")
  endif()
  set(MODEL "${build_model}" CACHE STRING "" FORCE)
  set(USAGE OFF CACHE BOOL "" FORCE)
  set(ENABLE_MPI OFF CACHE BOOL "" FORCE)
  set(ENABLE_PROFILING OFF CACHE BOOL "" FORCE)
  set(CXX_EXTRA_FLAGS -O2 CACHE STRING "" FORCE)
  set(CXX_EXTRA_LIBRARIES "" CACHE STRING "" FORCE)
  set(OFFLOAD OFF CACHE BOOL "" FORCE)
  set(OFFLOAD_FLAGS "" CACHE STRING "" FORCE)
  if(CONFORMANCE_BACKEND STREQUAL "cuda")
    # Release mode already supplies the model's optimization level. A second
    # value is passed through nvcc and produces an incompatible-redefinition warning.
    set(CXX_EXTRA_FLAGS "" CACHE STRING "" FORCE)
    set(CUDA_ARCH "sm_${conformance_cuda_architecture}" CACHE STRING "" FORCE)
    set(CMAKE_CUDA_RUNTIME_LIBRARY Static)
  elseif(CONFORMANCE_BACKEND STREQUAL "hip")
    set(hip_flags -O2)
    foreach(architecture IN LISTS CMAKE_HIP_ARCHITECTURES)
      string(APPEND hip_flags " --offload-arch=${architecture}")
    endforeach()
    if(model STREQUAL "omp-target")
      list(GET CMAKE_HIP_ARCHITECTURES 0 offload_architecture)
      set(OFFLOAD ON CACHE BOOL "" FORCE)
      set(OFFLOAD_FLAGS "-fopenmp=libomp -fopenmp-targets=amdgcn-amd-amdhsa -Xopenmp-target=amdgcn-amd-amdhsa -march=${offload_architecture}"
        CACHE STRING "" FORCE)
    else()
      set(CXX_EXTRA_FLAGS "${hip_flags}" CACHE STRING "" FORCE)
      set(CXX_EXTRA_LIBRARIES amdhip64 CACHE STRING "" FORCE)
    endif()
  elseif(CONFORMANCE_BACKEND STREQUAL "sycl")
    set(SYCL_COMPILER ONEAPI-ICPX CACHE STRING "" FORCE)
  elseif(CONFORMANCE_BACKEND STREQUAL "opencl")
    find_package(OpenCL REQUIRED)
    set(OpenCL_LIBRARY "${OpenCL_LIBRARY}" CACHE FILEPATH "" FORCE)
  elseif(CONFORMANCE_BACKEND STREQUAL "openacc")
    set(TARGET_DEVICE gpu CACHE STRING "" FORCE)
    set(CUDA_ARCH "cc${conformance_cuda_architecture}" CACHE STRING "" FORCE)
  endif()
  set(configure_callback "miniapp_${app}_configure")
  if(COMMAND "${configure_callback}")
    cmake_language(CALL "${configure_callback}" "${model}")
  endif()
  if(model STREQUAL "kokkos")
    configure_kokkos("${stage}")
  endif()
  unset(CONFORMANCE_MINIAPP_TARGET)
  add_subdirectory("${stage}" "build/${app}-${model}" EXCLUDE_FROM_ALL)
  if(NOT TARGET "${CONFORMANCE_MINIAPP_TARGET}")
    message(FATAL_ERROR "${app}/${model} did not define its conformance test target")
  endif()
  # The Catch adapter uses C++17. Some upstream miniapps, notably
  # BabelStream, otherwise force the complete target back to C++14.
  target_compile_features("${CONFORMANCE_MINIAPP_TARGET}" PRIVATE cxx_std_17)
  target_link_libraries("${CONFORMANCE_MINIAPP_TARGET}" PRIVATE Catch2::Catch2WithMain conformance_device)
  # The miniapp sources and Kokkos are vendored third-party code; silence their
  # diagnostics so only project code is checked.
  target_compile_options("${CONFORMANCE_MINIAPP_TARGET}" PRIVATE
    "$<$<COMPILE_LANG_AND_ID:CXX,GNU>:-Wno-unused-result;-Wno-unused-variable;-Wno-maybe-uninitialized>"
    "$<$<COMPILE_LANG_AND_ID:CXX,Clang,AppleClang,IntelLLVM>:-Wno-pass-failed>"
    "$<$<COMPILE_LANG_AND_ID:CXX,Clang,AppleClang,IntelLLVM>:-Wno-vla-cxx-extension>"
    "$<$<COMPILE_LANG_AND_ID:CXX,Clang,AppleClang,IntelLLVM>:-Wno-inconsistent-missing-override>"
    "$<$<COMPILE_LANG_AND_ID:CXX,Clang,AppleClang,IntelLLVM>:-Wno-unused-result>"
    "$<$<COMPILE_LANG_AND_ID:CXX,Clang,AppleClang,IntelLLVM>:-Wno-unused-variable>"
    "$<$<COMPILE_LANG_AND_ID:CXX,Clang,AppleClang>:-Wno-nan-infinity-disabled>"
    "$<$<COMPILE_LANG_AND_ID:CXX,Clang,AppleClang>:-Wno-sometimes-uninitialized>"
    "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM>:-Wno-deprecated-declarations>"
    "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM>:-Wno-deprecated-attributes>"
    "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM>:-Wno-sycl-strict>"
    "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM>:-Wno-user-defined-warnings>"
    "$<$<COMPILE_LANG_AND_ID:CXX,IntelLLVM>:-Wno-cpp>")
  # Device LTO passes (notably OpenMP offload) report the same pass-failed
  # remark through ld.lld.
  target_link_options("${CONFORMANCE_MINIAPP_TARGET}" PRIVATE
    "$<$<CXX_COMPILER_ID:Clang,AppleClang>:-Wno-pass-failed>")
  if(CONFORMANCE_BACKEND STREQUAL "hip" AND model MATCHES "^(hip|kokkos)$")
    target_compile_options("${CONFORMANCE_MINIAPP_TARGET}" PRIVATE
      "$<$<COMPILE_LANGUAGE:CXX>:-x;hip>")
    target_link_options("${CONFORMANCE_MINIAPP_TARGET}" PRIVATE --hip-link)
  endif()
  if(CONFORMANCE_BACKEND STREQUAL "opencl")
    target_compile_definitions("${CONFORMANCE_MINIAPP_TARGET}" PRIVATE CONFORMANCE_OPENCL_FLAT_DEVICE_ARGUMENT=1)
  endif()
  if(CONFORMANCE_BACKEND STREQUAL "sycl" AND app MATCHES "^(cloverleaf|tealeaf)$")
    target_compile_definitions("${CONFORMANCE_MINIAPP_TARGET}" PRIVATE CONFORMANCE_SYCL_FP64_PROBE=1)
  endif()
  set(${target_output} "${CONFORMANCE_MINIAPP_TARGET}" PARENT_SCOPE)
endfunction()

function(configure_miniapp_test app model stage target)
  cmake_language(CALL "miniapp_${app}_test_arguments" "${model}" arguments)
  set(content "#pragma once

#include <string>
#include <vector>

inline constexpr char conformance_test_identity[] = \"miniapps/${app}/${model}\";
inline constexpr char conformance_test_labels[] = \"gpu-conformance;miniapps;${CONFORMANCE_BACKEND};requires-execution\";

inline std::vector<std::string> conformance_test_arguments() {
  return {")
  foreach(argument IN LISTS arguments)
    string(REPLACE "\\" "\\\\" argument "${argument}")
    string(REPLACE "\"" "\\\"" argument "${argument}")
    string(APPEND content "\n    \"${argument}\",")
  endforeach()
  string(APPEND content "\n  };\n}\n")
  file(WRITE "${stage}/conformance_test_config.hpp" "${content}")
endfunction()

function(conformance_add_miniapp app model)
  set(stage "${CMAKE_CURRENT_BINARY_DIR}/sources/${app}-${model}")
  stage_miniapp("${app}" "${model}" "${stage}")
  configure_miniapp("${app}" "${model}" "${stage}" target)
  if(APPLE)
    set(program_rpath "@loader_path/lib")
  elseif(UNIX)
    set(program_rpath "$ORIGIN/lib")
  endif()
  set_target_properties("${target}" PROPERTIES
    BUILD_RPATH "${program_rpath}"
    INSTALL_RPATH "${program_rpath}"
    INSTALL_REMOVE_ENVIRONMENT_RPATH TRUE)
  configure_miniapp_test("${app}" "${model}" "${stage}" "${target}")
  if(model STREQUAL "omp-target")
    get_target_property(target_definitions "${target}" COMPILE_DEFINITIONS)
    foreach(definition IN LISTS miniapp_${app}_openmp_definitions)
      if(NOT definition IN_LIST target_definitions)
        message(FATAL_ERROR "${app}: OpenMP target lacks ${definition}")
      endif()
    endforeach()
    list(GET CMAKE_HIP_ARCHITECTURES 0 offload_architecture)
    set(validation_target "${target}_validate_offload")
    add_custom_target("${validation_target}"
      COMMAND "${CMAKE_COMMAND}" "-DEXECUTABLE=$<TARGET_FILE:${target}>"
        "-DARCHITECTURE=${offload_architecture}"
        -P "${CMAKE_CURRENT_LIST_DIR}/ValidateOpenMPTarget.cmake"
      DEPENDS "${target}" VERBATIM)
    list(APPEND miniapp_targets "${validation_target}")
  endif()
  conformance_register_test_program("${target}" ${CONFORMANCE_TIMEOUT_MINIAPP})
  list(APPEND miniapp_targets "${target}")
  set(miniapp_targets "${miniapp_targets}" PARENT_SCOPE)
endfunction()

set(miniapp_targets)

# This explicit list is the miniapp product selection. Remove or comment an
# include to omit that application and all of its targets from the project.
include(babelstream.cmake)
include(minibude.cmake)
include(cloverleaf.cmake)
include(tealeaf.cmake)
include(ggml.cmake)
include(imgui.cmake)

get_property(miniapp_names GLOBAL PROPERTY CONFORMANCE_MINIAPP_NAMES)
if(NOT CONFORMANCE_MINIAPP_MODELS STREQUAL "")
  foreach(specification IN LISTS CONFORMANCE_MINIAPP_MODELS)
    string(REPLACE ":" ";" fields "${specification}")
    list(GET fields 0 app)
    if(NOT app IN_LIST miniapp_names)
      message(FATAL_ERROR "Unknown miniapp: ${app}")
    endif()
  endforeach()
endif()
if(NOT miniapp_targets)
  message(FATAL_ERROR "The ${CONFORMANCE_BACKEND} backend selected no miniapp targets")
endif()

add_custom_target(conformance_miniapps_artifact DEPENDS ${miniapp_targets})
