include("${CMAKE_CURRENT_LIST_DIR}/../interpreters/Environment.cmake")
include("${CMAKE_CURRENT_LIST_DIR}/../cmake/ContainerBuild.cmake")

# Extracts a pinned interpreter once per cache, from the verified dependency archive.
function(conformance_interpreter_archive_stamp language output)
  if(language STREQUAL "python")
    set(directory "cpython-${conformance_python_release}")
    set(archive "${CONFORMANCE_DEPENDENCY_DIRECTORY}/cpython/cpython-3.12.16.tar.gz")
    set(archive_variable PYTHON_ARCHIVE)
    set(directory_variable PYTHON_DIRECTORY)
    set(mode python)
  else()
    set(directory "julia-${conformance_julia_release}")
    set(archive "${CONFORMANCE_DEPENDENCY_DIRECTORY}/julia/julia-${conformance_julia_release}.tar.gz")
    set(archive_variable JULIA_ARCHIVE)
    set(directory_variable JULIA_DIRECTORY)
    set(mode julia)
  endif()
  set(stamp "${CONFORMANCE_INTERPRETER_DIRECTORY}/${directory}/.ready")
  if(NOT EXISTS "${stamp}")
    get_property(known GLOBAL PROPERTY CONFORMANCE_INTERPRETER_ARCHIVES)
    if(NOT directory IN_LIST known)
      set(dependencies "${CONFORMANCE_SOURCE_DIRECTORY}/interpreters/bootstrap.cmake")
      if(conformance_dependency_stamp)
        list(APPEND dependencies "${conformance_dependency_stamp}")
      else()
        list(APPEND dependencies "${archive}")
      endif()
      add_custom_command(OUTPUT "${stamp}"
        COMMAND "${CMAKE_COMMAND}"
          "-DCACHE=${CONFORMANCE_INTERPRETER_DIRECTORY}"
          "-DMODE=${mode}"
          "-D${archive_variable}=${archive}"
          "-D${directory_variable}=${directory}"
          -P "${CONFORMANCE_SOURCE_DIRECTORY}/interpreters/bootstrap.cmake"
        DEPENDS ${dependencies}
        COMMENT "Extracting ${directory}"
        VERBATIM)
      set_property(GLOBAL APPEND PROPERTY CONFORMANCE_INTERPRETER_ARCHIVES "${directory}")
    endif()
  endif()
  set(${output} "${stamp}" PARENT_SCOPE)
endfunction()

# Builds one interpreter environment in the route's container image. The cache is mounted at its host path because the
# payloads (Julia depots, bytecode) record absolute paths.
function(conformance_interpreter_environment package image image_stamp output)
  conformance_interpreter_environment_directory("${package}" directory)
  conformance_interpreter_environment_key("${package}" key)
  set(stamp "${directory}/.complete")
  if(NOT EXISTS "${stamp}")
    get_property(known GLOBAL PROPERTY CONFORMANCE_INTERPRETER_ENVIRONMENTS)
    if(NOT directory IN_LIST known)
      set(language "${conformance_interpreter_package_${package}_language}")
      conformance_interpreter_archive_stamp("${language}" interpreter_stamp)
      set(environment "/source/interpreters/${conformance_interpreter_package_${package}_environment}")
      set(arguments "${CONFORMANCE_INTERPRETER_DIRECTORY}" "${package}" "${key}" "${environment}")
      if(language STREQUAL "python")
        list(APPEND arguments "${conformance_interpreter_package_${package}_python_directory}")
        list(APPEND arguments "${CONFORMANCE_OFFLINE_BINARY}")
      else()
        list(APPEND arguments "julia-${conformance_julia_release}" "${CONFORMANCE_OFFLINE_BINARY}")
        list(APPEND arguments "/source/interpreters/julia/warmup-${package}.jl")
      endif()
      set(network bridge)
      if(CONFORMANCE_OFFLINE)
        set(network none)
      endif()
      container_command(command
        EXECUTABLE "${CONTAINER_EXECUTABLE}"
        IMAGE "${image}"
        SOURCE "${CONFORMANCE_SOURCE_DIRECTORY}"
        SOURCE_TARGET /source
        INTERPRETERS "${CONFORMANCE_INTERPRETER_DIRECTORY}"
        NETWORK "${network}"
        IDENTITY "${identity}"
        COMMAND sh "/source/interpreters/install-${language}.sh" ${arguments})
      add_custom_command(OUTPUT "${stamp}"
        COMMAND "${CMAKE_COMMAND}" -E make_directory "${CONFORMANCE_INTERPRETER_DIRECTORY}" "${directory}"
        COMMAND ${command}
        DEPENDS "${interpreter_stamp}" "${image_stamp}"
          "${CONFORMANCE_SOURCE_DIRECTORY}/interpreters/Packages.cmake"
          "${CONFORMANCE_SOURCE_DIRECTORY}/interpreters/install-${language}.sh"
        COMMENT "Building ${package} interpreter environment in ${image}"
        JOB_POOL container_build
        VERBATIM)
      set_property(GLOBAL APPEND PROPERTY CONFORMANCE_INTERPRETER_ENVIRONMENTS "${directory}")
    endif()
  endif()
  set(${output} "${stamp}" PARENT_SCOPE)
endfunction()
