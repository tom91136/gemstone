cmake_minimum_required(VERSION 3.25)
get_filename_component(interpreters "${CMAKE_CURRENT_LIST_DIR}/../interpreters" ABSOLUTE)
set(project "${interpreters}/julia/cuda")
set(pin "${project}/LocalPreferences.toml")

execute_process(COMMAND mktemp -d /tmp/conformance-cuda-runtime-pin-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(fail text)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "${text}")
endfunction()

if(NOT EXISTS "${pin}")
  fail("julia-cuda must pin the CUDA runtime: missing ${pin}")
endif()
file(READ "${pin}" preferences)
string(REGEX MATCH "\\[CUDA_Runtime_jll\\][^[]*version = \"([0-9.]+)\"" matched "${preferences}")
if(NOT matched)
  fail("${pin} must set [CUDA_Runtime_jll] version")
endif()
set(runtime "${CMAKE_MATCH_1}")

file(READ "${interpreters}/python/requirements-numba.txt" requirements)
string(REGEX MATCH "nvidia-nvvm==([0-9]+\\.[0-9]+)\\.[0-9]+" matched "${requirements}")
if(NOT matched)
  fail("requirements-numba.txt must pin nvidia-nvvm")
endif()
set(nvvm "${CMAKE_MATCH_1}")
if(NOT runtime STREQUAL nvvm)
  fail("CUDA_Runtime_jll ${runtime} differs from the nvidia-nvvm ${nvvm} pin in requirements-numba.txt")
endif()

foreach(name IN ITEMS babelstream tests)
  file(READ "${project}/${name}/Project.toml" project_toml)
  string(REGEX MATCH "\\[deps\\][^[]*CUDA_Runtime_jll = \"76a88914-d11a-5bdc-97e0-2f5a05c973a2\"" matched "${project_toml}")
  if(NOT matched)
    fail("the ${name} environment must list CUDA_Runtime_jll under [deps], Julia ignores preferences of indirect dependencies")
  endif()
endforeach()

file(WRITE "${directory}/cache/julia-1.13.4/bin/julia" "#!/bin/sh\nexit 0\n")
file(CHMOD "${directory}/cache/julia-1.13.4/bin/julia" PERMISSIONS OWNER_READ OWNER_WRITE OWNER_EXECUTE)
execute_process(
  COMMAND sh "${interpreters}/install-julia.sh" "${directory}/cache" julia-cuda 0123456789abcdef
    "${project}" julia-1.13.4 0 "${interpreters}/julia/warmup-julia-cuda.jl"
  OUTPUT_QUIET ERROR_VARIABLE error RESULT_VARIABLE result)
if(NOT result EQUAL 0)
  fail("install-julia.sh failed: ${error}")
endif()

foreach(name IN ITEMS babelstream tests)
  set(copied "${directory}/cache/julia-cuda-0123456789abcdef/environment/${name}/LocalPreferences.toml")
  if(NOT EXISTS "${copied}")
    fail("install-julia.sh must copy LocalPreferences.toml into the ${name} environment")
  endif()
  file(READ "${copied}" copied_text)
  if(NOT copied_text STREQUAL preferences)
    fail("the ${name} environment must get the pinned LocalPreferences.toml unchanged")
  endif()
endforeach()

file(REMOVE_RECURSE "${directory}")
