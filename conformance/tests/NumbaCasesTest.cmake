cmake_minimum_required(VERSION 3.25)

find_program(python python3 REQUIRED)
execute_process(
  COMMAND "${python}" "${CMAKE_CURRENT_LIST_DIR}/numba_cases_test.py"
  RESULT_VARIABLE result)
if(NOT result EQUAL 0)
  message(FATAL_ERROR "numba_cases_test.py failed")
endif()
