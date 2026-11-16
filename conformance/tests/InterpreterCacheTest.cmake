cmake_minimum_required(VERSION 3.25)

set(conformance_dir "${CMAKE_CURRENT_LIST_DIR}/..")
set(workflow_file "${conformance_dir}/../.github/workflows/build.yml")

function(fail text)
  message(FATAL_ERROR "${text}")
endfunction()

file(READ "${workflow_file}" workflow_content)
string(REGEX MATCHALL "path:[ \t]*[^\r\n]+" cache_paths "${workflow_content}")
foreach(entry IN LISTS cache_paths)
  string(REGEX REPLACE "^path:[ \t]*" "" cache_path "${entry}")
  if(cache_path MATCHES "interpreters" OR cache_path MATCHES "julia" OR cache_path MATCHES "cpython")
    fail("workflow cache path carries interpreter content: ${cache_path}")
  endif()
endforeach()
