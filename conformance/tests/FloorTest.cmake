cmake_minimum_required(VERSION 3.25)
get_filename_component(artifacts "${CMAKE_CURRENT_LIST_DIR}/../artifacts" ABSOLUTE)
find_program(READELF readelf REQUIRED)
find_program(C_COMPILER NAMES cc gcc clang REQUIRED)

execute_process(COMMAND mktemp -d /tmp/conformance-floor-test-XXXXXX
  OUTPUT_VARIABLE directory OUTPUT_STRIP_TRAILING_WHITESPACE COMMAND_ERROR_IS_FATAL ANY)

function(run)
  execute_process(COMMAND ${ARGN} COMMAND_ERROR_IS_FATAL ANY)
endfunction()

file(WRITE "${directory}/stub.c"
  "int f_base(void) { return 0; }\n"
  "int f_28(void) { return 0; }\n"
  "int f_34(void) { return 0; }\n"
  "int g_25(void) { return 0; }\n"
  "int g_32(void) { return 0; }\n")
file(WRITE "${directory}/libc-sysroot.map"
  "GLIBC_2.2.5 { global: f_base; local: *; };\n"
  "GLIBC_2.28 { global: f_28; } GLIBC_2.2.5;\n")
file(WRITE "${directory}/libc-host.map"
  "GLIBC_2.2.5 { global: f_base; local: *; };\n"
  "GLIBC_2.28 { global: f_28; } GLIBC_2.2.5;\n"
  "GLIBC_2.34 { global: f_34; } GLIBC_2.28;\n")
file(WRITE "${directory}/libstdcxx-sysroot.map"
  "GLIBCXX_3.4.25 { global: g_25; local: *; };\n")
file(WRITE "${directory}/libstdcxx-host.map"
  "GLIBCXX_3.4.25 { global: g_25; };\n"
  "GLIBCXX_3.4.32 { global: g_32; } GLIBCXX_3.4.25;\n")

foreach(side IN ITEMS sysroot host)
  file(MAKE_DIRECTORY "${directory}/${side}/lib64")
  run("${C_COMPILER}" -shared -fPIC -Wl,-soname,libc.so.6
    -Wl,--version-script=${directory}/libc-${side}.map
    -o "${directory}/${side}/lib64/libc.so.6" "${directory}/stub.c")
  run("${C_COMPILER}" -shared -fPIC -Wl,-soname,libstdc++.so.6
    -Wl,--version-script=${directory}/libstdcxx-${side}.map
    -o "${directory}/${side}/lib64/libstdc++.so.6" "${directory}/stub.c")
endforeach()

file(WRITE "${directory}/ok.c" "int f_28(void); int g_25(void); int ok(void) { return f_28() + g_25(); }\n")
file(WRITE "${directory}/newer.c" "int f_34(void); int g_32(void); int newer(void) { return f_34() + g_32(); }\n")
file(MAKE_DIRECTORY "${directory}/ok" "${directory}/newer")
run("${C_COMPILER}" -shared -fPIC -o "${directory}/ok/ok.so" "${directory}/ok.c"
  "${directory}/sysroot/lib64/libc.so.6" "${directory}/sysroot/lib64/libstdc++.so.6")
run("${C_COMPILER}" -shared -fPIC -o "${directory}/newer/newer.so" "${directory}/newer.c"
  "${directory}/host/lib64/libc.so.6" "${directory}/host/lib64/libstdc++.so.6")

file(WRITE "${directory}/probe.cmake"
  "include(\"${artifacts}/Floor.cmake\")\n"
  "conformance_floor(\"\${ROOT}\" \"\${SYSROOT}\" glibc glibcxx)\n"
  "message(STATUS \"floor glibc=\${glibc} glibcxx=\${glibcxx}\")\n")

function(probe root sysroot result_var output_var)
  execute_process(COMMAND "${CMAKE_COMMAND}" "-DREADELF=${READELF}" "-DROOT=${root}"
    "-DSYSROOT=${sysroot}" -P "${directory}/probe.cmake"
    RESULT_VARIABLE result OUTPUT_VARIABLE output ERROR_VARIABLE error)
  set(${result_var} "${result}" PARENT_SCOPE)
  set(${output_var} "${output}${error}" PARENT_SCOPE)
endfunction()

probe("${directory}/ok" "${directory}/sysroot" result output)
if(NOT result EQUAL 0 OR NOT output MATCHES "floor glibc=2.28 glibcxx=3.4.25")
  message(FATAL_ERROR "sysroot-built route rejected or misrecorded:\n${output}")
endif()

probe("${directory}/newer" "${directory}/sysroot" result output)
if(result EQUAL 0)
  message(FATAL_ERROR "route newer than the sysroot passed the floor check")
endif()
if(NOT output MATCHES "GLIBC_2\\.34" OR NOT output MATCHES "GLIBCXX_3\\.4\\.32")
  message(FATAL_ERROR "floor failure does not name the newer versions:\n${output}")
endif()

probe("${directory}/newer" "" result output)
if(NOT result EQUAL 0 OR NOT output MATCHES "floor glibc=2.34 glibcxx=3.4.32")
  message(FATAL_ERROR "vendor route not reported without a sysroot:\n${output}")
endif()

file(REMOVE_RECURSE "${directory}")
