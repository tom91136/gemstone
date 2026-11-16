cmake_minimum_required(VERSION 3.25)

find_program(julia julia)
if(NOT julia)
  message("julia is not installed; the listing tests need it")
  return()
endif()
execute_process(
  COMMAND "${CMAKE_COMMAND}" -E env JULIA_PKG_OFFLINE=true "${julia}" --startup-file=no "${CMAKE_CURRENT_LIST_DIR}/package_tests_test.jl"
  RESULT_VARIABLE result)
if(NOT result EQUAL 0)
  message(FATAL_ERROR "package_tests_test.jl failed")
endif()
