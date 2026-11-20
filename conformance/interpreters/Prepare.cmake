cmake_minimum_required(VERSION 3.25)

if(NOT DEFINED CONFORMANCE OR CONFORMANCE STREQUAL "")
  get_filename_component(CONFORMANCE "${CMAKE_CURRENT_LIST_DIR}/.." ABSOLUTE)
endif()

include("${CMAKE_CURRENT_LIST_DIR}/Environment.cmake")

set(selected_packages)
foreach(prog IN LISTS PROGRAMS)
  if(prog IN_LIST conformance_interpreter_programs)
    conformance_interpreter_package_of_program("${prog}" package)
    if(package AND NOT package IN_LIST selected_packages)
      list(APPEND selected_packages "${package}")
    endif()
  endif()
endforeach()

if(MODE STREQUAL "query")
  execute_process(COMMAND "${CMAKE_COMMAND}" -E echo "${selected_packages}")
  return()
endif()

if(MODE STREQUAL "prebuilt")
  file(GLOB_RECURSE records "${ARTIFACTS}/interpreter-keys.txt")
  set(problems)
  foreach(record IN LISTS records)
    file(STRINGS "${record}" entries)
    foreach(entry IN LISTS entries)
      string(REPLACE "\t" ";" fields "${entry}")
      list(GET fields 0 package)
      list(GET fields 1 recorded)
      conformance_interpreter_environment_key("${package}" current)
      if(NOT recorded STREQUAL current)
        list(APPEND problems "${package} in ${record} was built for ${recorded}, the current environment is ${current}")
      endif()
    endforeach()
  endforeach()
  file(GLOB_RECURSE manifests "${ARTIFACTS}/test-programs.txt")
  foreach(manifest IN LISTS manifests)
    get_filename_component(tree "${manifest}" DIRECTORY)
    if(EXISTS "${tree}/interpreter-keys.txt")
      continue()
    endif()
    file(STRINGS "${manifest}" entries)
    foreach(entry IN LISTS entries)
      string(REPLACE "\t" ";" fields "${entry}")
      list(GET fields 0 program)
      if(program IN_LIST conformance_interpreter_programs)
        list(APPEND problems "${tree} has interpreter programs but no interpreter-keys.txt, so its environment is unknown")
        break()
      endif()
    endforeach()
  endforeach()
  if(problems)
    list(JOIN problems "\n  " details)
    message(FATAL_ERROR "prebuilt interpreter packages are not verified for this environment:\n  ${details}\n"
      "rebuild the artifacts for this environment or drop --prebuilt")
  endif()
  return()
endif()

if(NOT MODE STREQUAL "interpreter" AND NOT selected_packages)
  message(STATUS "No interpreter packages required for selected programs")
  return()
endif()

if(NOT DEFINED CONFORMANCE_INTERPRETER_DIRECTORY OR CONFORMANCE_INTERPRETER_DIRECTORY STREQUAL "")
  if(DEFINED ENV{CONFORMANCE_INTERPRETER_DIRECTORY} AND NOT "$ENV{CONFORMANCE_INTERPRETER_DIRECTORY}" STREQUAL "")
    set(CONFORMANCE_INTERPRETER_DIRECTORY "$ENV{CONFORMANCE_INTERPRETER_DIRECTORY}")
  else()
    get_filename_component(root "${CONFORMANCE}/.." ABSOLUTE)
    set(CONFORMANCE_INTERPRETER_DIRECTORY "${root}/target/conformance/interpreters")
  endif()
endif()

if(NOT DEFINED CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY OR CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY STREQUAL "")
  if(DEFINED ENV{CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY} AND NOT "$ENV{CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY}" STREQUAL "")
    set(CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY "$ENV{CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY}")
  else()
    set(CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY "${CONFORMANCE_INTERPRETER_DIRECTORY}/archives")
  endif()
endif()

set(offline 0)
if(DEFINED ENV{CONFORMANCE_INTERPRETER_OFFLINE} AND "$ENV{CONFORMANCE_INTERPRETER_OFFLINE}" STREQUAL "1")
  set(offline 1)
endif()

string(TIMESTAMP prepare_start_seconds "%s")

file(MAKE_DIRECTORY "${CONFORMANCE_INTERPRETER_DIRECTORY}" "${CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY}")

function(ensure_interpreter language)
  if(language STREQUAL "python")
    set(directory "cpython-${conformance_python_release}")
    set(archive_name "${conformance_python_archive}")
    set(url "${conformance_python_url}")
    set(sha256 "${conformance_python_sha256}")
    set(mode python)
    set(archive_var PYTHON_ARCHIVE)
    set(dir_var PYTHON_DIRECTORY)
  else()
    set(directory "julia-${conformance_julia_release}")
    set(archive_name "${conformance_julia_archive}")
    set(url "${conformance_julia_url}")
    set(sha256 "${conformance_julia_sha256}")
    set(mode julia)
    set(archive_var JULIA_ARCHIVE)
    set(dir_var JULIA_DIRECTORY)
  endif()
  set(ready "${CONFORMANCE_INTERPRETER_DIRECTORY}/${directory}/.ready")
  if(EXISTS "${ready}")
    return()
  endif()
  set(archive "${CONFORMANCE_INTERPRETER_ARCHIVE_DIRECTORY}/${archive_name}")
  set(needs_fetch TRUE)
  if(EXISTS "${archive}")
    file(SHA256 "${archive}" digest)
    if(digest STREQUAL sha256)
      set(needs_fetch FALSE)
    endif()
  endif()
  if(needs_fetch)
    if(offline EQUAL 1)
      message(FATAL_ERROR "Offline mode forbids downloading missing interpreter archive: ${archive_name}")
    endif()
    message(STATUS "Downloading ${url} -> ${archive}")
    file(DOWNLOAD "${url}" "${archive}" EXPECTED_HASH SHA256=${sha256} STATUS dl_status)
    list(GET dl_status 0 dl_code)
    list(GET dl_status 1 dl_message)
    if(NOT dl_code EQUAL 0)
      file(REMOVE "${archive}")
      message(FATAL_ERROR "Failed to download ${url}: ${dl_message}")
    endif()
  endif()
  message(STATUS "Extracting ${directory}")
  execute_process(
    COMMAND "${CMAKE_COMMAND}"
      "-DCACHE=${CONFORMANCE_INTERPRETER_DIRECTORY}"
      "-DMODE=${mode}"
      "-D${archive_var}=${archive}"
      "-D${dir_var}=${directory}"
      -P "${CMAKE_CURRENT_LIST_DIR}/bootstrap.cmake"
    COMMAND_ERROR_IS_FATAL ANY
  )
endfunction()

if(MODE STREQUAL "interpreter")
  ensure_interpreter("${LANGUAGE}")
  if(LANGUAGE STREQUAL "python")
    set(executable "${CONFORMANCE_INTERPRETER_DIRECTORY}/cpython-${conformance_python_release}/bin/python3")
  else()
    set(executable "${CONFORMANCE_INTERPRETER_DIRECTORY}/julia-${conformance_julia_release}/bin/julia")
  endif()
  execute_process(COMMAND "${CMAKE_COMMAND}" -E echo "${executable}")
  return()
endif()

foreach(package IN LISTS selected_packages)
  conformance_interpreter_environment_key("${package}" key)
  set(target "${CONFORMANCE_INTERPRETER_DIRECTORY}/${package}-${key}")
  if(EXISTS "${target}/.complete")
    message(STATUS "Reusing interpreter environment: ${package}-${key}")
    continue()
  endif()

  set(language "${conformance_interpreter_package_${package}_language}")
  ensure_interpreter("${language}")

  message(STATUS "Building interpreter environment: ${package}-${key}")
  set(environment "${CMAKE_CURRENT_LIST_DIR}/${conformance_interpreter_package_${package}_environment}")
  if(language STREQUAL "python")
    execute_process(
      COMMAND sh "${CMAKE_CURRENT_LIST_DIR}/install-python.sh"
        "${CONFORMANCE_INTERPRETER_DIRECTORY}"
        "${package}"
        "${key}"
        "${environment}"
        "cpython-${conformance_python_release}"
        "${offline}"
      COMMAND_ERROR_IS_FATAL ANY
    )
  else()
    set(warmup "${CMAKE_CURRENT_LIST_DIR}/julia/warmup-${package}.jl")
    execute_process(
      COMMAND sh "${CMAKE_CURRENT_LIST_DIR}/install-julia.sh"
        "${CONFORMANCE_INTERPRETER_DIRECTORY}"
        "${package}"
        "${key}"
        "${environment}"
        "julia-${conformance_julia_release}"
        "${offline}"
        "${warmup}"
      COMMAND_ERROR_IS_FATAL ANY
    )
  endif()
endforeach()

string(TIMESTAMP prepare_end_seconds "%s")
math(EXPR prepare_elapsed_seconds "${prepare_end_seconds} - ${prepare_start_seconds}")
message(STATUS "Interpreter preparation completed in ${prepare_elapsed_seconds} s")
