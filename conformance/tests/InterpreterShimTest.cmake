cmake_minimum_required(VERSION 3.25)
get_filename_component(interpreters "${CMAKE_CURRENT_LIST_DIR}/../interpreters" ABSOLUTE)
include("${CMAKE_CURRENT_LIST_DIR}/../../native/cmake/HipBundleFamilies.cmake")
string(REPLACE ";" " " GEMSTONE_HIP_BUNDLE_FAMILIES "${gemstone_hip_bundle_families}")

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
  set(extra)
  if(ARGC GREATER 3)
    list(APPEND extra "CONFORMANCE_TARGET_ARCH=${ARGV3}")
  endif()
  if(ARGC GREATER 4)
    list(APPEND extra "ROCBLAS_TENSILE_LIBPATH=${ARGV4}")
  endif()
  execute_process(
    COMMAND "${CMAKE_COMMAND}" -E env
      "CONFORMANCE_INTERPRETER_DIRECTORY=${directory}/base"
      "LD_PRELOAD=${preload}"
      "LD_LIBRARY_PATH=${library_path}"
      ${extra}
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
file(WRITE "${directory}/vendor/librocsparse.so.1" "hipv4-amdgcn-amd-amdhsa--gfx1030\n")

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

set(tensile "${directory}/tensile")
file(MAKE_DIRECTORY "${tensile}")
file(WRITE "${tensile}/TensileLibrary_lazy_gfx908.dat" "")
set(rocblas_entry "ROCM_ENTRY=librocblas.so.5->${directory}/vendor/librocblas.so.5")

shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1"
  arguments "${directory}/vendor" gfx803 "${tensile}")
if("${rocblas_entry}" IN_LIST arguments)
  fail("rocBLAS without a Tensile payload for gfx803 must stay hidden:\n${arguments}")
endif()
if(NOT "ROCM_ENTRY=libamdhip64.so.7->${directory}/native/libamdhip64.so.7" IN_LIST arguments)
  fail("hiding rocBLAS must keep the HIP runtime visible:\n${arguments}")
endif()

shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1"
  arguments "${directory}/vendor" gfx908 "${tensile}")
if(NOT "${rocblas_entry}" IN_LIST arguments)
  fail("an architecture with a Tensile payload must expose rocBLAS:\n${arguments}")
endif()

set(rocsparse_entry "ROCM_ENTRY=librocsparse.so.1->${directory}/vendor/librocsparse.so.1")

file(WRITE "${directory}/vendor/librocsparse.so.1" "hipv4-amdgcn-amd-amdhsa--gfx1010\n")
shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1"
  arguments "${directory}/vendor" gfx1011)
if("${rocsparse_entry}" IN_LIST arguments)
  fail("a gfx1010 bundle must not serve gfx1011:\n${arguments}")
endif()

file(WRITE "${directory}/vendor/librocsparse.so.1" "hipv4-amdgcn-amd-amdhsa--gfx1101\n")
shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1"
  arguments "${directory}/vendor" gfx1102)
if("${rocsparse_entry}" IN_LIST arguments)
  fail("a gfx1101 bundle must not serve gfx1102:\n${arguments}")
endif()

file(WRITE "${directory}/vendor/librocsparse.so.1" "hipv4-amdgcn-amd-amdhsa--gfx1030\n")
shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1"
  arguments "${directory}/vendor" gfx1011)
if("${rocsparse_entry}" IN_LIST arguments)
  fail("rocSPARSE without a code object for gfx1011 must stay hidden:\n${arguments}")
endif()
if(NOT "ROCM_ENTRY=libamdhip64.so.7->${directory}/native/libamdhip64.so.7" IN_LIST arguments)
  fail("hiding rocSPARSE must keep the HIP runtime visible:\n${arguments}")
endif()

foreach(architecture IN ITEMS gfx1030 gfx1036)
  shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1"
    arguments "${directory}/vendor" "${architecture}")
  if(NOT "${rocsparse_entry}" IN_LIST arguments)
    fail("an architecture whose family has a rocSPARSE code object must expose it:\n${arguments}")
  endif()
endforeach()

file(WRITE "${directory}/vendor/librocsparse.so.1" "hipv4-amdgcn-amd-amdhsa--gfx950\nhipv4-amdgcn-amd-amdhsa--gfx1030\n")
shim_arguments("${directory}/native/libamdhip64.so.7:${directory}/native/libhsa-runtime64.so.1"
  arguments "${directory}/vendor" gfx1036)
if(NOT "${rocsparse_entry}" IN_LIST arguments)
  fail("several bundles in one library must keep the canonical family image visible:\n${arguments}")
endif()

file(REMOVE_RECURSE "${directory}")
