cmake_minimum_required(VERSION 3.25)
get_filename_component(artifacts "${CMAKE_CURRENT_LIST_DIR}/../artifacts" ABSOLUTE)
execute_process(COMMAND mktemp -d /tmp/conformance-jobserver-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(expect_status success)
  set(command)
  math(EXPR last "${ARGC} - 1")
  foreach(index RANGE 1 ${last})
    string(REPLACE ";" "\\;" argument "${ARGV${index}}")
    list(APPEND command "${argument}")
  endforeach()
  execute_process(COMMAND ${command} RESULT_VARIABLE status OUTPUT_VARIABLE output ERROR_VARIABLE error TIMEOUT 30)
  if((success AND NOT status STREQUAL "0") OR (NOT success AND status STREQUAL "0"))
    message(FATAL_ERROR "Unexpected status ${status}; fixtures: ${directory}\n${output}\n${error}")
  endif()
endfunction()

set(fake "${directory}/oci")
file(WRITE "${fake}" "#!/bin/sh\nprintf '%s\\n' \"$@\" > '${directory}/arguments'\nprintf '[1/1] fixture\\n'\n")
file(CHMOD "${fake}" PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)
set(container "${CMAKE_COMMAND}" -P "${artifacts}/Jobserver.cmake" -- container "${fake}" run image)
expect_status(TRUE "${CMAKE_COMMAND}" -E env --unset=CONFORMANCE_JOBSERVER_DIRECTORY
  --unset=CMAKE_BUILD_PARALLEL_LEVEL ${container})
file(READ "${directory}/arguments" args)
if(NOT args MATCHES "CMAKE_BUILD_PARALLEL_LEVEL=4")
  message(FATAL_ERROR "Missing bounded direct-build default")
endif()
expect_status(TRUE "${CMAKE_COMMAND}" -E env --unset=CONFORMANCE_JOBSERVER_DIRECTORY
  CMAKE_BUILD_PARALLEL_LEVEL=7 ${container})
file(READ "${directory}/arguments" args)
if(NOT args MATCHES "CMAKE_BUILD_PARALLEL_LEVEL=7")
  message(FATAL_ERROR "Lost explicit direct-build budget")
endif()
expect_status(FALSE "${CMAKE_COMMAND}" -E env --unset=CONFORMANCE_JOBSERVER_DIRECTORY
  CMAKE_BUILD_PARALLEL_LEVEL=0 ${container})
expect_status(FALSE "${CMAKE_COMMAND}" -E env "CONFORMANCE_JOBSERVER_DIRECTORY=${directory}"
  MAKEFLAGS= CONFORMANCE_JOBSERVER_SERIAL=0 ${container})
execute_process(COMMAND mkfifo "${directory}/fifo" COMMAND_ERROR_IS_FATAL ANY)
expect_status(TRUE "${CMAKE_COMMAND}" -E env "CONFORMANCE_JOBSERVER_DIRECTORY=${directory}"
  "MAKEFLAGS=-j4 --jobserver-auth=fifo:${directory}/fifo" CONFORMANCE_JOBSERVER_SERIAL=0 ${container})
file(READ "${directory}/arguments" args)
if(NOT args MATCHES "MAKEFLAGS=-j4 --jobserver-auth=fifo:" OR
    NOT args MATCHES "CMAKE_BUILD_PARALLEL_LEVEL=\n")
  message(FATAL_ERROR "Shared container lost authentication or inherited a numeric override")
endif()

expect_status(TRUE "${CMAKE_COMMAND}" -E env "CONFORMANCE_JOBSERVER_DIRECTORY=${directory}"
  MAKEFLAGS= CONFORMANCE_JOBSERVER_SERIAL=1 ${container} "literal ' $ ; value")
file(READ "${directory}/arguments" args)
if(NOT args MATCHES "CMAKE_BUILD_PARALLEL_LEVEL=1\n" OR
    NOT args MATCHES "literal ' [$] ; value\n")
  message(FATAL_ERROR "Serial container lost its budget or argument boundaries")
endif()

file(WRITE "${directory}/old-ninja" "#!/bin/sh\necho 1.11.1\n")
file(CHMOD "${directory}/old-ninja" PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)
expect_status(FALSE sh "${artifacts}/jobserver.sh" --ninja "${directory}/old-ninja" -- true)

# Real Make -> outer Ninja -> inner Ninja, with a separate artifact pool.
find_program(ninja NAMES ninja REQUIRED)
file(WRITE "${directory}/worker.sh"
  "#!/bin/sh\nprintf '%s\\n' + >> \"$1\"\nsleep .1\nprintf '%s\\n' - >> \"$1\"\n")
foreach(name IN ITEMS a b c)
  set(graph "rule work\n  command = sh '${directory}/worker.sh' '${directory}/events'\n")
  foreach(i RANGE 1 8)
    string(APPEND graph "build ${name}${i}: work\n")
  endforeach()
  file(WRITE "${directory}/${name}.ninja" "${graph}")
endforeach()
file(WRITE "${directory}/build.ninja"
  "pool artifacts\n  depth = 2\nrule inner\n  command = '${ninja}' -f $in\n  pool = artifacts\nbuild a: inner a.ninja\nbuild b: inner b.ninja\nbuild c: inner c.ninja\n")
expect_status(TRUE sh "${artifacts}/jobserver.sh" --jobs 4 -- "${ninja}" -C "${directory}")
file(STRINGS "${directory}/events" events)
set(active 0)
set(peak 0)
foreach(event IN LISTS events)
  if(event STREQUAL "+")
    math(EXPR active "${active} + 1")
  else()
    math(EXPR active "${active} - 1")
  endif()
  if(active GREATER peak)
    set(peak ${active})
  endif()
endforeach()
list(LENGTH events count)
if(NOT active EQUAL 0 OR peak GREATER 4 OR peak LESS 2 OR NOT count EQUAL 48)
  message(FATAL_ERROR "Invalid nested budget: active=${active}, peak=${peak}, events=${count}")
endif()

# Arguments must survive shell and Make quoting without expansion.
expect_status(TRUE sh "${artifacts}/jobserver.sh" --jobs 2 -- "${CMAKE_COMMAND}"
  -E touch "${directory}/literal ' $ ; name")
if(NOT EXISTS "${directory}/literal ' $ ; name")
  message(FATAL_ERROR "Build argument quoting changed the filename")
endif()
file(REMOVE_RECURSE "${directory}")
message(STATUS "Jobserver configuration, forwarding, quoting and nested scheduling passed")
