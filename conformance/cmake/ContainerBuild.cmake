# Generic OCI container command construction for conformance and benchmark builds.
include_guard(GLOBAL)

# conformance_container_command(<out>
#   EXECUTABLE <container-exe> IMAGE <image>
#   [SOURCE <dir>] [SOURCE_TARGET <container-dir>] [ARTIFACTS <dir>]
#   [DEPENDENCIES <dir>] [WORK <dir>]
#   [NETWORK <mode>] [CPU_SHARES <n>] [IDENTITY <flags>]
#   COMMAND <argv...>)
# Produces the argv list for `run --rm` with the stable mount layout used by
# the artifact scripts: source at /source/conformance by default, output at
# /output, the dependency cache at /conformance-deps, and the persistent build
# tree at /work.
function(conformance_container_command output)
  cmake_parse_arguments(ARG "" "EXECUTABLE;IMAGE;SOURCE;SOURCE_TARGET;ARTIFACTS;DEPENDENCIES;WORK;NETWORK;CPU_SHARES;IDENTITY"
    "COMMAND" ${ARGN})
  if(NOT ARG_EXECUTABLE OR NOT ARG_IMAGE)
    message(FATAL_ERROR "conformance_container_command requires EXECUTABLE and IMAGE")
  endif()
  set(command "${ARG_EXECUTABLE}" run --rm --security-opt label=disable)
  if(ARG_NETWORK)
    list(APPEND command "--network=${ARG_NETWORK}")
  endif()
  if(ARG_CPU_SHARES)
    list(APPEND command --cpu-shares "${ARG_CPU_SHARES}")
  endif()
  if(ARG_IDENTITY)
    separate_arguments(identity NATIVE_COMMAND "${ARG_IDENTITY}")
    list(APPEND command ${identity})
  endif()
  if(ARG_SOURCE)
    if(ARG_SOURCE_TARGET)
      set(source_target "${ARG_SOURCE_TARGET}")
    else()
      set(source_target "/source/conformance")
    endif()
    list(APPEND command --mount "type=bind,source=${ARG_SOURCE},target=${source_target},readonly")
  endif()
  if(ARG_ARTIFACTS)
    list(APPEND command --mount "type=bind,source=${ARG_ARTIFACTS},target=/output")
  endif()
  if(ARG_DEPENDENCIES)
    list(APPEND command --mount "type=bind,source=${ARG_DEPENDENCIES},target=/conformance-deps,readonly")
  endif()
  if(ARG_WORK)
    list(APPEND command --mount "type=bind,source=${ARG_WORK},target=/work")
  endif()
  list(APPEND command "${ARG_IMAGE}")
  list(APPEND command ${ARG_COMMAND})
  set(${output} "${CMAKE_COMMAND};-P;${CMAKE_CURRENT_FUNCTION_LIST_DIR}/../artifacts/Jobserver.cmake;--;container;${command}" PARENT_SCOPE)
endfunction()
