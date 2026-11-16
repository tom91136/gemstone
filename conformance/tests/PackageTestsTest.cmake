cmake_minimum_required(VERSION 3.25)

execute_process(
  COMMAND "${CMAKE_COMMAND}" -DMODE=interpreter -DLANGUAGE=julia -P "${CMAKE_CURRENT_LIST_DIR}/../interpreters/Prepare.cmake"
  OUTPUT_VARIABLE prepared OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)
string(REGEX REPLACE ".*\n" "" julia "${prepared}")
execute_process(
  COMMAND "${CMAKE_COMMAND}" -E env JULIA_PKG_OFFLINE=true "${julia}" --startup-file=no "${CMAKE_CURRENT_LIST_DIR}/package_tests_test.jl"
  RESULT_VARIABLE result)
if(NOT result EQUAL 0)
  message(FATAL_ERROR "package_tests_test.jl failed")
endif()
