cmake_minimum_required(VERSION 3.25)

get_filename_component(conformance "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
set(prepare_script "${conformance}/interpreters/Prepare.cmake")

if(NOT EXISTS "${prepare_script}")
  message(FATAL_ERROR "Missing interpreter preparation script: ${prepare_script}")
endif()

function(fail text)
  message(FATAL_ERROR "${text}")
endfunction()

function(check_mapping programs expected)
  execute_process(
    COMMAND "${CMAKE_COMMAND}"
      "-DCONFORMANCE=${conformance}"
      "-DMODE=query"
      "-DPROGRAMS=${programs}"
      -P "${prepare_script}"
    OUTPUT_VARIABLE output
    ERROR_VARIABLE error
    RESULT_VARIABLE status
    OUTPUT_STRIP_TRAILING_WHITESPACE
  )
  if(NOT status EQUAL 0)
    fail("Query failed for programs '${programs}': ${error}")
  endif()
  set(actual "${output}")
  if(NOT actual STREQUAL expected)
    fail("Mapping mismatch for programs '${programs}': expected '${expected}', got '${actual}'")
  endif()
endfunction()

check_mapping("python_numba" "numba")
check_mapping("julia_babelstream_cuda" "julia-cuda")
check_mapping("julia_tests_cuda" "julia-cuda")
check_mapping("python_numba;julia_babelstream_cuda" "numba;julia-cuda")
check_mapping("julia_babelstream_amdgpu;julia_tests_amdgpu" "julia-amdgpu")
check_mapping("julia_babelstream_oneapi;julia_tests_oneapi" "julia-oneapi")
check_mapping("other_program;native_test" "")
check_mapping("python_numba;other_program" "numba")
check_mapping("python_numba;julia_babelstream_cuda;julia_babelstream_amdgpu;julia_babelstream_oneapi" "numba;julia-cuda;julia-amdgpu;julia-oneapi")
check_mapping("" "")
