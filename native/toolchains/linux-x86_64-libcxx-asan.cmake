include("${CMAKE_CURRENT_LIST_DIR}/linux-x86_64-libcxx.cmake")

set(_gemstone_sanitizer_flags
  "-fsanitize=address,shift,signed-integer-overflow,bounds"
  "-fno-sanitize-recover=all"
  "-fno-omit-frame-pointer")
string(JOIN " " _gemstone_sanitizer_flags ${_gemstone_sanitizer_flags})
set(CMAKE_C_FLAGS_INIT "${CMAKE_C_FLAGS_INIT} ${_gemstone_sanitizer_flags}")
set(CMAKE_CXX_FLAGS_INIT "${CMAKE_CXX_FLAGS_INIT} ${_gemstone_sanitizer_flags}")
set(CMAKE_C_FLAGS_RELEASE_INIT "-O1 -g -DNDEBUG")
set(CMAKE_CXX_FLAGS_RELEASE_INIT "-O1 -g -DNDEBUG")
foreach(_gemstone_linker IN ITEMS EXE SHARED MODULE)
  set(CMAKE_${_gemstone_linker}_LINKER_FLAGS_INIT
    "${CMAKE_${_gemstone_linker}_LINKER_FLAGS_INIT} -shared-libsan -frtlib-add-rpath")
endforeach()

# The shared runtime is not in the loader's path, and a test that preloads instrumented drivers must put it first.
execute_process(
  COMMAND "${CMAKE_C_COMPILER}" -print-file-name=libclang_rt.asan.so
  OUTPUT_VARIABLE GEMSTONE_SANITIZER_PRELOAD
  OUTPUT_STRIP_TRAILING_WHITESPACE)
if(NOT IS_ABSOLUTE "${GEMSTONE_SANITIZER_PRELOAD}")
  message(FATAL_ERROR "clang does not provide libclang_rt.asan.so")
endif()
