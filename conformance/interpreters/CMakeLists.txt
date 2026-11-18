include("${CMAKE_CURRENT_LIST_DIR}/Environment.cmake")

if(CONFORMANCE_INTERPRETER_DIRECTORY STREQUAL "")
  message(FATAL_ERROR "CONFORMANCE_INTERPRETER_PACKAGES requires CONFORMANCE_INTERPRETER_DIRECTORY")
endif()
foreach(package IN LISTS CONFORMANCE_INTERPRETER_PACKAGES)
  if(NOT package IN_LIST conformance_interpreter_packages)
    message(FATAL_ERROR "Unknown interpreter package: ${package}")
  endif()
endforeach()

set(fragment "${CMAKE_CURRENT_BINARY_DIR}/interpreter-test-programs.txt")
file(WRITE "${fragment}" "")
set(languages)
foreach(package IN LISTS CONFORMANCE_INTERPRETER_PACKAGES)
  set(language "${conformance_interpreter_package_${package}_language}")
  list(APPEND languages "${language}")
  conformance_interpreter_environment_key("${package}" key)
  foreach(program IN LISTS conformance_interpreter_package_${package}_programs)
    set(script "${conformance_interpreter_program_${program}_script}")
    set(arguments "${conformance_interpreter_program_${program}_arguments}")
    set(timeout "${conformance_interpreter_program_${program}_timeout}")
    if(language STREQUAL "python")
      set(template shim-python.sh.in)
    else()
      set(template shim-julia.sh.in)
    endif()
    set(PACKAGE "${package}")
    set(KEY "${key}")
    set(SCRIPT "${script}")
    set(ARGUMENTS "${arguments}")
    set(PROJECT "${conformance_interpreter_program_${program}_project}")
    set(JULIA "${conformance_julia_release}")
    configure_file("${CMAKE_CURRENT_LIST_DIR}/${template}" "${CMAKE_CURRENT_BINARY_DIR}/${program}" @ONLY)
    install(PROGRAMS "${CMAKE_CURRENT_BINARY_DIR}/${program}" DESTINATION . COMPONENT architecture)
    file(APPEND "${fragment}" "${program}\t${timeout}\tcases\n")
  endforeach()
endforeach()
list(REMOVE_DUPLICATES languages)

install(FILES "${fragment}" DESTINATION . COMPONENT architecture)

if("python" IN_LIST languages)
  install(FILES
    "${CMAKE_CURRENT_LIST_DIR}/python/numba_cases.py"
    "${CMAKE_CURRENT_LIST_DIR}/python/numba_cases_pytest.py"
    "${CMAKE_CURRENT_LIST_DIR}/python/pytest.ini"
    DESTINATION data/interpreters/python COMPONENT architecture)
endif()
if("julia" IN_LIST languages)
  install(FILES
    "${CMAKE_CURRENT_LIST_DIR}/julia/babelstream.jl"
    "${CMAKE_CURRENT_LIST_DIR}/julia/package_tests.jl"
    "${CMAKE_CURRENT_LIST_DIR}/julia/listing.jl"
    DESTINATION data/interpreters/julia COMPONENT architecture)
  set(babelstream "${DEPENDENCY_CACHE}/babelstream")
  if(NOT EXISTS "${babelstream}/src/julia/JuliaStream.jl/src")
    message(FATAL_ERROR "The BabelStream Julia source is missing from ${babelstream}")
  endif()
  install(DIRECTORY "${babelstream}/src/julia/JuliaStream.jl/src/"
    DESTINATION data/interpreters/julia/babelstream/src COMPONENT architecture)
  install(FILES "${babelstream}/LICENSE"
    DESTINATION licences RENAME BabelStream-julia.txt COMPONENT support)
endif()
