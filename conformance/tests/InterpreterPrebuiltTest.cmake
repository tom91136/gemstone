cmake_minimum_required(VERSION 3.25)
get_filename_component(conformance "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
set(prepare_script "${conformance}/interpreters/Prepare.cmake")

execute_process(COMMAND mktemp -d /tmp/conformance-interpreter-prebuilt-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(fail text)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "${text}")
endfunction()

include("${conformance}/interpreters/Environment.cmake")
conformance_interpreter_environment_key(julia-amdgpu current)

set(amdgpu_programs "julia_babelstream_amdgpu\t900\tcases\njulia_tests_amdgpu\t450\tcases\n")

file(MAKE_DIRECTORY "${directory}/current/hip/amdgpu" "${directory}/stale/hip/amdgpu"
  "${directory}/unrecorded/hip/amdgpu" "${directory}/plain/hip/amdgpu")
file(WRITE "${directory}/current/hip/amdgpu/interpreter-keys.txt" "julia-amdgpu\t${current}\n")
file(WRITE "${directory}/current/hip/amdgpu/test-programs.txt" "${amdgpu_programs}")
file(WRITE "${directory}/stale/hip/amdgpu/interpreter-keys.txt" "julia-amdgpu\t0123456789abcdef\n")
file(WRITE "${directory}/stale/hip/amdgpu/test-programs.txt" "${amdgpu_programs}")
file(WRITE "${directory}/unrecorded/hip/amdgpu/test-programs.txt" "${amdgpu_programs}")
file(WRITE "${directory}/plain/hip/amdgpu/test-programs.txt" "hecbench_demo\t2\thecbench\n")

function(run_prebuilt artifacts output status)
  execute_process(
    COMMAND "${CMAKE_COMMAND}"
      "-DCONFORMANCE=${conformance}"
      "-DCONFORMANCE_INTERPRETER_DIRECTORY=${directory}/depots"
      "-DMODE=prebuilt"
      "-DARTIFACTS=${artifacts}"
      -P "${prepare_script}"
    OUTPUT_VARIABLE out
    ERROR_VARIABLE err
    RESULT_VARIABLE code
    OUTPUT_STRIP_TRAILING_WHITESPACE
  )
  set(${output} "${out}${err}" PARENT_SCOPE)
  set(${status} "${code}" PARENT_SCOPE)
endfunction()

run_prebuilt("${directory}/current" output status)
if(NOT status EQUAL 0)
  fail("Prebuilt record for the current environment was rejected: ${output}")
endif()

run_prebuilt("${directory}/stale" output status)
if(status EQUAL 0)
  fail("Prebuilt record for a different environment key was accepted")
endif()
if(NOT output MATCHES "julia-amdgpu" OR NOT output MATCHES "0123456789abcdef")
  fail("Prebuilt mismatch does not name the package and recorded key: ${output}")
endif()
if(EXISTS "${directory}/depots")
  fail("Prebuilt mismatch created the interpreter directory")
endif()

run_prebuilt("${directory}/unrecorded" output status)
if(status EQUAL 0)
  fail("Prebuilt tree with interpreter programs and no interpreter-keys.txt was accepted")
endif()
if(NOT output MATCHES "${directory}/unrecorded" OR NOT output MATCHES "interpreter-keys.txt" OR NOT output MATCHES "rebuild")
  fail("Unrecorded prebuilt tree does not name the missing record and ask for a rebuild: ${output}")
endif()

run_prebuilt("${directory}/plain" output status)
if(NOT status EQUAL 0)
  fail("Prebuilt tree without interpreter programs was rejected: ${output}")
endif()

file(REMOVE_RECURSE "${directory}")
