cmake_minimum_required(VERSION 3.25)
get_filename_component(conformance "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
set(verify_script "${conformance}/artifacts/Verify.cmake")

execute_process(COMMAND mktemp -d /tmp/conformance-verify-cudadevrt-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(fail text)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "${text}")
endfunction()

function(fake_artifact artifact cooperative)
  file(MAKE_DIRECTORY "${artifact}")
  file(WRITE "${artifact}/test-programs.txt" "unified\t3600\tcatch2\n")
  if(cooperative)
    set(cases "{\"name\": \"runtime/cuda/cooperative/grid-sync-publishes-every-block\", \"tags\": [\"cooperative\", \"runtime\", \"unified\"]}")
  else()
    set(cases "{\"name\": \"integer/cuda/carryless\", \"tags\": [\"integer\", \"unified\"]}")
  endif()
  file(WRITE "${artifact}/unified"
    "#!/bin/sh\nprintf '{\\n  \"version\": 1,\\n  \"listings\": {\\n    \"tests\": [\\n      ${cases}\\n    ]\\n  }\\n}\\n'\n")
  file(CHMOD "${artifact}/unified" FILE_PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)
endfunction()

# Each case gets its own temporary root so a previous relocation copy cannot stand in for the artifact under test.
function(run_verify name cooperative library expect_failure expected_text)
  set(root "${directory}/${name}")
  set(artifact "${root}/cuda-ptx/sm_89")
  set(support "${root}/cuda/support-13.3")
  file(MAKE_DIRECTORY "${support}/lib" "${root}/tmp")
  fake_artifact("${artifact}" ${cooperative})
  if(library)
    file(WRITE "${support}/lib/libcudadevrt.a" "device runtime\n")
  endif()
  set(ENV{TMPDIR} "${root}/tmp")
  execute_process(COMMAND "${CMAKE_COMMAND}" "-DARTIFACT=${artifact}" "-DSUPPORT=${support}" -P "${verify_script}"
    OUTPUT_VARIABLE output ERROR_VARIABLE error RESULT_VARIABLE status)
  if(expect_failure AND status EQUAL 0)
    fail("relocation verify accepted an artifact listing cooperative cases without libcudadevrt.a: ${output}${error}")
  endif()
  if(NOT expect_failure AND NOT status EQUAL 0)
    fail("relocation verify rejected a closure that ships libcudadevrt.a: ${output}${error}")
  endif()
  if(expected_text AND NOT "${output}${error}" MATCHES "${expected_text}")
    fail("relocation verify did not report ${expected_text}: ${output}${error}")
  endif()
endfunction()

run_verify(missing TRUE FALSE TRUE "libcudadevrt\\.a")
run_verify(shipped TRUE TRUE FALSE "")
run_verify(unrelated FALSE FALSE FALSE "")

file(REMOVE_RECURSE "${directory}")
