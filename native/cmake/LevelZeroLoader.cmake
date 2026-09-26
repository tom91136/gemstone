include_guard(GLOBAL)

include(FetchContent)

# Builds the pinned oneAPI Level Zero loader from source. The gemstone facade
# implements only the driver DDI; the public ze_* entry points live in the
# loader, so the levelzero clients and component tests must not depend on a
# host-provided libze_loader. The build runs in its own tree because the
# upstream project is top-level only and would otherwise leak global state.
function(gemstone_add_level_zero_loader)
  if(TARGET ze_loader)
    return()
  endif()
  if(NOT LEVEL_ZERO_SOURCE_URL OR NOT LEVEL_ZERO_SOURCE_SHA256)
    message(FATAL_ERROR "LEVEL_ZERO_SOURCE_URL and LEVEL_ZERO_SOURCE_SHA256 must be supplied by the build")
  endif()
  FetchContent_Declare(level_zero_loader_source
    URL "${LEVEL_ZERO_SOURCE_URL}"
    URL_HASH "SHA256=${LEVEL_ZERO_SOURCE_SHA256}"
    SOURCE_SUBDIR gemstone-no-cmake-project
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE)
  FetchContent_MakeAvailable(level_zero_loader_source)

  set(source "${level_zero_loader_source_SOURCE_DIR}")
  set(build "${CMAKE_BINARY_DIR}/level-zero-loader")
  set(loader "${CMAKE_BINARY_DIR}/libze_loader.so.1")
  add_custom_command(
    OUTPUT "${loader}"
    COMMAND "${CMAKE_COMMAND}" -S "${source}" -B "${build}" -G Ninja
      "-DCMAKE_MAKE_PROGRAM=${CMAKE_MAKE_PROGRAM}"
      "-DCMAKE_BUILD_TYPE=Release"
      "-DBUILD_STATIC=OFF"
      "-DBUILD_L0_LOADER_TESTS=OFF"
      "-DCMAKE_C_COMPILER=${CMAKE_C_COMPILER}"
      "-DCMAKE_CXX_COMPILER=${CMAKE_CXX_COMPILER}"
    COMMAND "${CMAKE_COMMAND}" --build "${build}" --target ze_loader --parallel
    COMMAND "${CMAKE_COMMAND}" -E copy_if_different "${build}/lib/libze_loader.so.1" "${loader}"
    DEPENDS "${source}/CMakeLists.txt"
    COMMENT "Building the pinned oneAPI Level Zero loader"
    VERBATIM)
  add_custom_target(level_zero_loader DEPENDS "${loader}")

  add_library(ze_loader SHARED IMPORTED GLOBAL)
  set_target_properties(ze_loader PROPERTIES
    IMPORTED_LOCATION "${loader}"
    INTERFACE_INCLUDE_DIRECTORIES "${source}/include")
endfunction()
