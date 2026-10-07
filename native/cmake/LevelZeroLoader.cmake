include_guard(GLOBAL)

function(gemstone_add_level_zero_loader)
  if(TARGET ze_loader)
    return()
  endif()
  if(NOT EXISTS "${LEVEL_ZERO_SOURCE_DIR}/CMakeLists.txt")
    message(FATAL_ERROR "LEVEL_ZERO_SOURCE_DIR must point at the pinned Level Zero loader source")
  endif()
  set(source "${LEVEL_ZERO_SOURCE_DIR}")
  set(build "${CMAKE_BINARY_DIR}/level-zero-loader")
  set(loader "${CMAKE_BINARY_DIR}/libze_loader.so.1")
  set(tracing "${CMAKE_BINARY_DIR}/libze_tracing_layer.so.1")
  add_custom_command(
    OUTPUT "${loader}" "${tracing}"
    COMMAND "${CMAKE_COMMAND}" -S "${source}" -B "${build}" -G Ninja -DCMAKE_MESSAGE_LOG_LEVEL=WARNING
      "-DCMAKE_MAKE_PROGRAM=${CMAKE_MAKE_PROGRAM}"
      "-DCMAKE_BUILD_TYPE=Release"
      "-DBUILD_STATIC=OFF"
      "-DBUILD_L0_LOADER_TESTS=OFF"
      "-DCMAKE_BUILD_RPATH=$ORIGIN"
      "-DCMAKE_C_COMPILER=${CMAKE_C_COMPILER}"
      "-DCMAKE_CXX_COMPILER=${CMAKE_CXX_COMPILER}"
    COMMAND "${CMAKE_COMMAND}" --build "${build}" --target ze_loader ze_tracing_layer --parallel -- --quiet
    COMMAND "${CMAKE_COMMAND}" -E copy_if_different "${build}/lib/libze_loader.so.1" "${loader}"
    COMMAND "${CMAKE_COMMAND}" -E copy_if_different "${build}/lib/libze_tracing_layer.so.1" "${tracing}"
    DEPENDS "${source}/CMakeLists.txt" "${CMAKE_CURRENT_FUNCTION_LIST_FILE}"
    COMMENT "Building the pinned oneAPI Level Zero loader"
    VERBATIM)
  add_custom_target(level_zero_loader DEPENDS "${loader}" "${tracing}")

  add_library(ze_loader SHARED IMPORTED GLOBAL)
  set_target_properties(ze_loader PROPERTIES
    IMPORTED_LOCATION "${loader}"
    INTERFACE_INCLUDE_DIRECTORIES "${source}/include")
endfunction()
