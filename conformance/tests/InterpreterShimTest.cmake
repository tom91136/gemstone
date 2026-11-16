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
file(MAKE_DIRECTORY "${directory}/base/julia-1.13.4/bin" "${directory}/native" "${directory}/other")
file(WRITE "${directory}/base/julia-1.13.4/bin/julia"
  "#!/bin/sh\nif [ -n \"\${ROCM_PATH-}\" ]; then echo \"ROCM_PATH=\$ROCM_PATH\"; fi\nprintf '%s\\n' \"$@\"\n")
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

shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1" arguments)
if(NOT "ROCM_PATH=${directory}/native" IN_LIST arguments)
  fail("a HIP preload must set ROCM_PATH to its directory:\n${arguments}")
endif()

file(WRITE "${directory}/native/libamdhip64.so.7" "")
shim_arguments("${directory}/other/libcuda.so.1:${directory}/native/libhsa-runtime64.so.1" arguments "${directory}/other:${directory}/native")
if(NOT "ROCM_PATH=${directory}/native" IN_LIST arguments)
  fail("a HIP library on the library path must set ROCM_PATH to its directory:\n${arguments}")
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
