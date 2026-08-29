# SPDX-License-Identifier: Apache-2.0
if(NOT DEFINED NM OR NOT DEFINED LIBRARY OR NOT DEFINED MANIFEST)
  message(FATAL_ERROR "NM, LIBRARY, and MANIFEST are required")
endif()

execute_process(
  COMMAND "${NM}" -D --defined-only "${LIBRARY}"
  RESULT_VARIABLE nm_status
  OUTPUT_VARIABLE nm_output
  ERROR_VARIABLE nm_error
)
if(NOT nm_status EQUAL 0)
  message(FATAL_ERROR "nm failed: ${nm_error}")
endif()

file(STRINGS "${MANIFEST}" expected)
set(actual)
string(REPLACE "\n" ";" nm_lines "${nm_output}")
foreach(line IN LISTS nm_lines)
  if(line MATCHES "[ \t](hsaKmt[A-Za-z0-9_]+)$")
    list(APPEND actual "${CMAKE_MATCH_1}")
  endif()
endforeach()
list(SORT expected)
list(SORT actual)
if(NOT "${actual}" STREQUAL "${expected}")
  message(FATAL_ERROR
    "libhsakmt symbol mismatch\nexpected: ${expected}\nactual: ${actual}")
endif()
