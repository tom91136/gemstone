cmake_minimum_required(VERSION 3.25)

execute_process(COMMAND mktemp -d /tmp/offload-key-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)
set(CONFORMANCE_SOURCE_DIRECTORY "${directory}")
include("${CMAKE_CURRENT_LIST_DIR}/../artifacts/Toolchains.cmake")
file(READ "${CMAKE_CURRENT_LIST_DIR}/../Containerfile" containerfile)

function(key_after from to output)
  string(FIND "${containerfile}" "${from}" at)
  if(at EQUAL -1)
    message(FATAL_ERROR "the Containerfile has no '${from}'")
  endif()
  string(REPLACE "${from}" "${to}" edited "${containerfile}")
  file(WRITE "${CONFORMANCE_SOURCE_DIRECTORY}/Containerfile" "${edited}")
  conformance_offload_key(key)
  set(${output} "${key}" PARENT_SCOPE)
endfunction()

key_after("FROM ubuntu-build-base AS llvm-offload\n" "FROM ubuntu-build-base AS llvm-offload\n" original)

key_after("FROM jobserver-ninja AS cmake-release\n" "FROM jobserver-ninja AS cmake-release\nRUN true\n" unrelated)
if(NOT unrelated STREQUAL original)
  message(FATAL_ERROR "editing cmake-release changed the llvm-offload key")
endif()

key_after("FROM network-config AS ubuntu-build-base\n" "FROM network-config AS ubuntu-build-base\nRUN true\n" ancestor)
if(ancestor STREQUAL original)
  message(FATAL_ERROR "editing ubuntu-build-base kept the llvm-offload key")
endif()

key_after("FROM ubuntu-build-base AS llvm-offload\n" "FROM ubuntu-build-base AS llvm-offload\nRUN true\n" own)
if(own STREQUAL original)
  message(FATAL_ERROR "editing llvm-offload kept its key")
endif()

file(REMOVE_RECURSE "${directory}")
