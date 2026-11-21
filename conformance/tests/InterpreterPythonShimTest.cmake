cmake_minimum_required(VERSION 3.25)
get_filename_component(interpreters "${CMAKE_CURRENT_LIST_DIR}/../interpreters" ABSOLUTE)

execute_process(COMMAND mktemp -d /tmp/conformance-python-shim-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(fail text)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "${text}")
endfunction()

set(PACKAGE numba)
set(KEY 0123456789abcdef)
set(SCRIPT python/numba_cases.py)
configure_file("${interpreters}/shim-python.sh.in" "${directory}/shim" @ONLY)
file(CHMOD "${directory}/shim" PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)
file(MAKE_DIRECTORY "${directory}/base/numba-${KEY}/bin" "${directory}/base/numba-${KEY}/lib")
file(WRITE "${directory}/base/numba-${KEY}/bin/python"
  "#!/bin/sh\nprintf 'PYTHONPATH=%s\\n' \"\${PYTHONPATH-unset}\"\nprintf 'CUDA_HOME=%s\\n' \"\${CUDA_HOME-}\"\nprintf '%s\\n' \"$@\"\n")
file(CHMOD "${directory}/base/numba-${KEY}/bin/python" PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)

execute_process(
  COMMAND "${CMAKE_COMMAND}" -E env --unset=PYTHONPATH
    "CONFORMANCE_INTERPRETER_DIRECTORY=${directory}/base"
    "${directory}/shim" --case smoke/vector_add
  OUTPUT_VARIABLE text RESULT_VARIABLE result ERROR_VARIABLE error)
if(NOT result EQUAL 0)
  fail("shim failed: ${error}")
endif()
string(REPLACE "\n" ";" lines "${text}")

if(NOT "PYTHONPATH=unset" IN_LIST lines)
  fail("the environment must not set PYTHONPATH, so that its .pth files are processed:\n${lines}")
endif()
if(NOT "CUDA_HOME=${directory}/base/numba-${KEY}/cuda" IN_LIST lines)
  fail("CUDA_HOME must point at the environment's cuda directory:\n${lines}")
endif()
if(NOT "--case" IN_LIST lines OR NOT "smoke/vector_add" IN_LIST lines)
  fail("the shim must forward its arguments to the program:\n${lines}")
endif()

file(CREATE_LINK "${directory}/base" "${directory}/linked" SYMBOLIC)
execute_process(
  COMMAND "${CMAKE_COMMAND}" -E env --unset=PYTHONPATH
    "CONFORMANCE_INTERPRETER_DIRECTORY=${directory}/linked"
    "${directory}/shim" --case smoke/vector_add
  OUTPUT_VARIABLE text RESULT_VARIABLE result ERROR_VARIABLE error)
if(NOT result EQUAL 0)
  fail("shim through a symlinked cache failed: ${error}")
endif()
string(REPLACE "\n" ";" lines "${text}")
if(NOT "CUDA_HOME=${directory}/base/numba-${KEY}/cuda" IN_LIST lines)
  fail("a symlinked cache must reach the interpreter as its real path:\n${lines}")
endif()

file(REMOVE_RECURSE "${directory}")
