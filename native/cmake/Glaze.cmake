include_guard(GLOBAL)

include(FetchContent)

set(glaze_INSTALL OFF CACHE BOOL "" FORCE)
FetchContent_Declare(glaze
  URL https://github.com/stephenberry/glaze/archive/refs/tags/v7.9.1.tar.gz
  URL_HASH SHA256=d6dee391276f5375672c35d06058e4fd8f1f30f62bae163a004b3bd13a4e2ae3
  DOWNLOAD_EXTRACT_TIMESTAMP TRUE
)
FetchContent_MakeAvailable(glaze)

# Glaze is header-only third-party code; keep its diagnostics out of -Werror.
set_property(TARGET glaze_glaze APPEND PROPERTY INTERFACE_SYSTEM_INCLUDE_DIRECTORIES
  "$<TARGET_PROPERTY:glaze_glaze,INTERFACE_INCLUDE_DIRECTORIES>")
