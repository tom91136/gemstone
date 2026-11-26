cmake_minimum_required(VERSION 3.25)

if(NOT DEFINED CONFORMANCE_HIP_BUNDLE_FAMILIES OR CONFORMANCE_HIP_BUNDLE_FAMILIES STREQUAL "")
  set(CONFORMANCE_HIP_BUNDLE_FAMILIES gfx1030,gfx1100)
endif()

execute_process(COMMAND mktemp -d /tmp/conformance-shim-boundary-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

get_filename_component(conformance "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
file(COPY "${conformance}" DESTINATION "${directory}")
set(source "${directory}/conformance")
file(MAKE_DIRECTORY "${directory}/deps/babelstream/src/julia/JuliaStream.jl/src")
file(WRITE "${directory}/deps/babelstream/LICENSE" "")

set(driver [=[
cmake_minimum_required(VERSION 3.25)
project(conformance_shim_boundary NONE)
set(CONFORMANCE_INTERPRETER_DIRECTORY "@directory@/interpreters")
set(CONFORMANCE_INTERPRETER_PACKAGES julia-amdgpu)
set(DEPENDENCY_CACHE "@directory@/deps")
set(CONFORMANCE_HIP_BUNDLE_FAMILIES "@families@")
add_subdirectory("@source@/interpreters" shim)
]=])
set(families "${CONFORMANCE_HIP_BUNDLE_FAMILIES}")
string(CONFIGURE "${driver}" driver @ONLY)
file(MAKE_DIRECTORY "${directory}/driver")
file(WRITE "${directory}/driver/CMakeLists.txt" "${driver}")

execute_process(
  COMMAND "${CMAKE_COMMAND}" -S "${directory}/driver" -B "${directory}/build" --log-level=WARNING
  RESULT_VARIABLE result OUTPUT_VARIABLE output ERROR_VARIABLE error)
if(NOT result EQUAL 0)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "configuring the interpreter shim with only conformance/ visible failed:\n${output}\n${error}")
endif()

string(REPLACE "," " " expected "${CONFORMANCE_HIP_BUNDLE_FAMILIES}")
file(READ "${directory}/build/shim/julia_tests_amdgpu" shim)
string(FIND "${shim}" "case \" ${expected} \" in" position)
if(position LESS 0)
  file(REMOVE_RECURSE "${directory}")
  message(FATAL_ERROR "the generated shim did not receive the host-supplied families:\n${shim}")
endif()

file(REMOVE_RECURSE "${directory}")
