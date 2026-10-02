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
    # Assemble.cmake installs metadata at backend/architecture, not backend-architecture.
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
    foreach(version IN ITEMS 11.8 13.3)
      string(FIND "${graph}" "output/cuda/support-${version}/support.stamp" support)
      if(support EQUAL -1)
        message(FATAL_ERROR "Missing CUDA ${version} support bundle")
      endif()
    endforeach()
  endif()
endforeach()

execute_process(COMMAND "${CMAKE_COMMAND}" -DCONFORMANCE_TARGET_SET=missing -P "${artifacts}/Targets.cmake"
  OUTPUT_QUIET ERROR_QUIET RESULT_VARIABLE status)
if(status STREQUAL "0")
  message(FATAL_ERROR "Unknown target set was accepted")
endif()
file(REMOVE_RECURSE "${directory}")
