cmake_minimum_required(VERSION 3.25)
get_filename_component(conformance "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
set(verify_script "${conformance}/artifacts/Verify.cmake")

execute_process(COMMAND mktemp -d /tmp/conformance-verify-relocation-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(fail text)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "${text}")
endfunction()

set(artifact "${directory}/artifact")
file(MAKE_DIRECTORY "${artifact}")
file(WRITE "${artifact}/test-programs.txt" "python_numba\t900\tcases\t2048\n")
file(WRITE "${artifact}/python_numba" "#!/bin/sh\nexit 1\n")
file(CHMOD "${artifact}/python_numba" FILE_PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)

execute_process(COMMAND "${CMAKE_COMMAND}" "-DARTIFACT=${artifact}" -P "${verify_script}"
  OUTPUT_VARIABLE output ERROR_VARIABLE error RESULT_VARIABLE status OUTPUT_STRIP_TRAILING_WHITESPACE)
if(NOT status EQUAL 0)
  fail("relocation verify rejected an interpreter case with a memory column: ${output}${error}")
endif()
if(NOT "${output}${error}" MATCHES "Relocation verified")
  fail("relocation verify exited without verifying the artifact: ${output}${error}")
endif()

file(REMOVE_RECURSE "${directory}")
