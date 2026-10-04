cmake_minimum_required(VERSION 3.25)
get_filename_component(artifacts "${CMAKE_CURRENT_LIST_DIR}/../artifacts" ABSOLUTE)
execute_process(COMMAND mktemp -d /tmp/conformance-targets-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

foreach(target_set IN ITEMS default complete legacy-gfx803)
  execute_process(COMMAND "${CMAKE_COMMAND}" "-DCONFORMANCE_TARGET_SET=${target_set}" -P "${artifacts}/Targets.cmake"
    OUTPUT_VARIABLE routes OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)
  string(REPLACE "\n" ";" routes "${routes}")
  set(unique_routes ${routes})
  list(REMOVE_DUPLICATES unique_routes)
  if(NOT unique_routes STREQUAL routes)
    message(FATAL_ERROR "Duplicate routes in ${target_set}")
  endif()

  # Configuration only: no container builds, downloads or GPU required.
  set(build "${directory}/${target_set}")
  execute_process(COMMAND "${CMAKE_COMMAND}" -S "${artifacts}" -B "${build}" -G Ninja
    "-DCONFORMANCE_TARGET_SET=${target_set}" -DROCM_VERSION=7.1.1
    OUTPUT_VARIABLE output ERROR_VARIABLE error RESULT_VARIABLE status)
  if(NOT status STREQUAL "0")
    message(FATAL_ERROR "Matrix configuration failed: ${output}\n${error}")
  endif()
  file(READ "${build}/build.ninja" graph)
  foreach(route IN LISTS routes)
    # CUDA code modes share one artifact containing both PTX and SASS.
    string(REGEX REPLACE "^cuda-(ptx|sass)/" "cuda/" artifact_route "${route}")
    string(REGEX REPLACE "^([^/]+)/([^/]+)/(clang|gcc)$" "\\1/\\3/\\2" artifact_route "${artifact_route}")
    # Compiler variants have independent artifact and runtime directories.
    string(FIND "${graph}" "build output/${artifact_route}/artifact.toml " rule)
    if(rule EQUAL -1)
      message(FATAL_ERROR "Exported route ${route} has no matching artifact output")
    endif()
  endforeach()
  if(target_set STREQUAL "complete")
    foreach(route IN ITEMS cuda-ptx/sm_35 cuda-sass/sm_35 cuda-ptx/sm_120 cuda-sass/sm_120
        hip/gfx90c hip/gfx1201 hsa/gfx803 hsa/gfx1201 vulkan)
      if(NOT route IN_LIST routes)
        message(FATAL_ERROR "Complete matrix is missing ${route}")
      endif()
    endforeach()
    foreach(route IN ITEMS cuda-ptx/sm_35/clang cuda-sass/sm_120/clang cuda-ptx/sm_80/gcc hsa/gfx906/gcc hsa/gfx908/gcc hsa/gfx1103/gcc)
      if(NOT route IN_LIST routes)
        message(FATAL_ERROR "Complete matrix is missing compiler route ${route}")
      endif()
    endforeach()
    foreach(route IN ITEMS cuda-sass/sm_80/gcc cuda-ptx/sm_120/gcc hsa/gfx803/gcc hsa/gfx1201/gcc)
      if(route IN_LIST routes)
        message(FATAL_ERROR "Unsupported compiler route exported: ${route}")
      endif()
    endforeach()
    foreach(version IN ITEMS 11.8 13.3)
      string(FIND "${graph}" "output/cuda/support-${version}/support.stamp" support)
      if(support EQUAL -1)
        message(FATAL_ERROR "Missing CUDA ${version} support bundle")
      endif()
    endforeach()
  endif()
endforeach()

execute_process(COMMAND "${CMAKE_COMMAND}" -DCONFORMANCE_BACKENDS=cuda,hsa
  -DCUDA_ARCHITECTURES=sm_80 -DAMDGPU_ARCHITECTURES=gfx906
  -DCONFORMANCE_COMPILERS=clang,gcc -P "${artifacts}/Targets.cmake"
  OUTPUT_VARIABLE compiler_routes COMMAND_ERROR_IS_FATAL ANY)
if(NOT compiler_routes MATCHES "cuda-ptx/sm_80/clang" OR NOT compiler_routes MATCHES "hsa/gfx906/gcc"
    OR compiler_routes MATCHES "cuda-ptx/sm_80\\n")
  message(FATAL_ERROR "Comma-separated compiler selection was not preserved: ${compiler_routes}")
endif()
execute_process(COMMAND "${CMAKE_COMMAND}" -DCONFORMANCE_TARGET_SET=default
  -DCONFORMANCE_BACKENDS=vulkan -DCONFORMANCE_COMPILERS=gcc -P "${artifacts}/Targets.cmake"
  OUTPUT_QUIET ERROR_QUIET RESULT_VARIABLE status)
if(status STREQUAL "0")
  message(FATAL_ERROR "Empty compiler/backend matrix was accepted")
endif()

foreach(backend IN ITEMS cuda hsa)
  execute_process(COMMAND "${CMAKE_COMMAND}"
    "-DCONFORMANCE_BACKENDS=${backend}" -DCONFORMANCE_COMPILERS=gcc
    -DCUDA_ARCHITECTURES=sm_89 -DAMDGPU_ARCHITECTURES=gfx1150 -P "${artifacts}/Targets.cmake"
    OUTPUT_QUIET ERROR_VARIABLE error RESULT_VARIABLE status)
  if(status STREQUAL "0" OR NOT error MATCHES "No supported backend/architecture/compiler combinations")
    message(FATAL_ERROR "Unsupported GCC ${backend} architecture was not rejected: ${error}")
  endif()
endforeach()
execute_process(COMMAND "${CMAKE_COMMAND}"
  -DCONFORMANCE_BACKENDS=cuda -DCONFORMANCE_COMPILERS=gcc -DCUDA_ARCHITECTURES=sm_80,sm_89
  -P "${artifacts}/Targets.cmake" OUTPUT_VARIABLE mixed_routes OUTPUT_STRIP_TRAILING_WHITESPACE
  COMMAND_ERROR_IS_FATAL ANY)
if(NOT mixed_routes STREQUAL "cuda-ptx/sm_80/gcc")
  message(FATAL_ERROR "GCC architecture intersection or PTX-only selection is incorrect: ${mixed_routes}")
endif()

execute_process(COMMAND "${CMAKE_COMMAND}" -DCONFORMANCE_TARGET_SET=missing -P "${artifacts}/Targets.cmake"
  OUTPUT_QUIET ERROR_QUIET RESULT_VARIABLE status)
if(status STREQUAL "0")
  message(FATAL_ERROR "Unknown target set was accepted")
endif()
file(REMOVE_RECURSE "${directory}")
