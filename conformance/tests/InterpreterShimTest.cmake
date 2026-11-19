cmake_minimum_required(VERSION 3.25)
get_filename_component(interpreters "${CMAKE_CURRENT_LIST_DIR}/../interpreters" ABSOLUTE)

execute_process(COMMAND mktemp -d /tmp/conformance-interpreter-shim-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(fail text)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "${text}")
endfunction()

set(PACKAGE julia-amdgpu)
set(KEY 0123456789abcdef)
set(SCRIPT julia/babelstream.jl)
set(ARGUMENTS amdgpu)
set(PROJECT babelstream)
set(JULIA 1.13.4)
configure_file("${interpreters}/shim-julia.sh.in" "${directory}/shim" @ONLY)
file(CHMOD "${directory}/shim" PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)
file(MAKE_DIRECTORY "${directory}/base/julia-1.13.4/bin" "${directory}/native" "${directory}/other" "${directory}/vendor")
file(WRITE "${directory}/base/julia-1.13.4/bin/julia"
  "#!/bin/sh\necho \"LD_LIBRARY_PATH=\${LD_LIBRARY_PATH-}\"\nif [ -n \"\${ROCM_PATH-}\" ]; then echo \"ROCM_PATH=\$ROCM_PATH\"; for entry in \"\$ROCM_PATH\"/*; do echo \"ROCM_ENTRY=\$(basename \"\$entry\")->\$(readlink \"\$entry\")\"; done; fi\nprintf '%s\\n' \"$@\"\n")
file(CHMOD "${directory}/base/julia-1.13.4/bin/julia" PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)

function(shim_arguments preload output)
  set(library_path "")
  if(ARGC GREATER 2)
    set(library_path "${ARGV2}")
  endif()
  execute_process(
    COMMAND "${CMAKE_COMMAND}" -E env
      "CONFORMANCE_INTERPRETER_DIRECTORY=${directory}/base"
      "LD_PRELOAD=${preload}"
      "LD_LIBRARY_PATH=${library_path}"
      "${directory}/shim"
    OUTPUT_VARIABLE text RESULT_VARIABLE result ERROR_VARIABLE error)
  if(NOT result EQUAL 0)
    fail("shim failed: ${error}")
  endif()
  string(REPLACE "\n" ";" lines "${text}")
  set(${output} "${lines}" PARENT_SCOPE)
endfunction()

file(WRITE "${directory}/native/libamdhip64.so.7" "")
file(WRITE "${directory}/native/libhsa-runtime64.so.1" "")
file(WRITE "${directory}/vendor/libamdhip64.so.7" "")
file(WRITE "${directory}/vendor/librocblas.so.5" "")

function(require_entries arguments context)
  foreach(entry IN ITEMS "libamdhip64.so.7->${directory}/native/libamdhip64.so.7" "libhsa-runtime64.so.1->${directory}/native/libhsa-runtime64.so.1"
      "librocblas.so.5->${directory}/vendor/librocblas.so.5")
    if(NOT "ROCM_ENTRY=${entry}" IN_LIST arguments)
      fail("${context} must expose ${entry} through ROCM_PATH:\n${arguments}")
    endif()
  endforeach()
  foreach(argument IN LISTS arguments)
    if(argument MATCHES "^ROCM_PATH=(.*)$" AND EXISTS "${CMAKE_MATCH_1}")
      fail("${context} must remove its ROCM_PATH directory ${CMAKE_MATCH_1}")
    endif()
  endforeach()
endfunction()

shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1" arguments "${directory}/vendor")
require_entries("${arguments}" "a HIP preload")

shim_arguments("${directory}/other/libcuda.so.1:${directory}/native/libhsa-runtime64.so.1" arguments "${directory}/native:${directory}/vendor")
require_entries("${arguments}" "a HIP library on the library path")

set(julia_libraries "${directory}/base/julia-1.13.4/lib/julia")
shim_arguments("${directory}/other/libcuda.so.1" arguments "${directory}/other")
if(NOT "LD_LIBRARY_PATH=${julia_libraries}:${directory}/other" IN_LIST arguments)
  fail("the interpreter libraries must precede the library path:\n${arguments}")
endif()

shim_arguments("${directory}/other/libcuda.so.1" arguments)
if(NOT "LD_LIBRARY_PATH=${julia_libraries}" IN_LIST arguments)
  fail("an empty library path must not gain an empty entry:\n${arguments}")
endif()

shim_arguments("${directory}/other/libcuda.so.1" arguments "${directory}/other")
foreach(argument IN LISTS arguments)
  if(argument MATCHES "^ROCM_PATH=")
    fail("ROCM_PATH must stay unset without a HIP library: ${argument}")
  endif()
endforeach()

shim_arguments("${directory}/other/libcuda.so.1" arguments)
foreach(argument IN LISTS arguments)
  if(argument MATCHES "^ROCM_PATH=")
    fail("ROCM_PATH must stay unset without a HIP preload: ${argument}")
  endif()
endforeach()

file(REMOVE_RECURSE "${directory}")
