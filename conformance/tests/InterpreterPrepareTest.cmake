cmake_minimum_required(VERSION 3.25)

get_filename_component(conformance "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
set(prepare_script "${conformance}/interpreters/Prepare.cmake")
set(workloads "${conformance}/workloads")

if(NOT EXISTS "${prepare_script}")
  message(FATAL_ERROR "Missing interpreter preparation script: ${prepare_script}")
endif()

function(fail text)
  message(FATAL_ERROR "${text}")
endfunction()

function(check_selection routes workload expected)
  execute_process(
    COMMAND "${CMAKE_COMMAND}"
      "-DCONFORMANCE=${conformance}"
      "-DMODE=query"
      "-DROUTES=${routes}"
      "-DWORKLOAD=${workload}"
      "-DWORKLOADS=${workloads}"
      -P "${prepare_script}"
    OUTPUT_VARIABLE output
    ERROR_VARIABLE error
    RESULT_VARIABLE status
    OUTPUT_STRIP_TRAILING_WHITESPACE
  )
  if(NOT status EQUAL 0)
    fail("Query failed for routes '${routes}' workload '${workload}': ${error}")
  endif()
  set(actual "${output}")
  if(NOT actual STREQUAL expected)
    fail("Selection mismatch for routes '${routes}' workload '${workload}': expected '${expected}', got '${actual}'")
  endif()
endfunction()

check_selection("cuda/clang-sm_90a" "interpreters-bounded" "numba;julia-cuda")
check_selection("hip/clang-gfx950" "interpreters-bounded" "julia-amdgpu")
check_selection("sycl-shard-0" "interpreters-bounded" "julia-oneapi")
check_selection("opencl/clang" "interpreters-bounded" "")
check_selection("vulkan/vendor" "interpreters-bounded" "")
check_selection("cuda/clang-sm_90a" "opencl-cts-bounded" "")
check_selection("cuda/clang-sm_90a" "xlong" "numba;julia-cuda")
check_selection("all" "interpreters-bounded" "numba;julia-cuda;julia-amdgpu;julia-oneapi")
