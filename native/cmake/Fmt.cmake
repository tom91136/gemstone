include_guard(GLOBAL)

include(FetchContent)

set(FMT_INSTALL OFF CACHE BOOL "" FORCE)
set(FMT_TEST OFF CACHE BOOL "" FORCE)
set(FMT_DOC OFF CACHE BOOL "" FORCE)
FetchContent_Declare(fmt
  URL https://github.com/fmtlib/fmt/archive/refs/tags/11.2.0.tar.gz
  URL_HASH SHA256=bc23066d87ab3168f27cef3e97d545fa63314f5c79df5ea444d41d56f962c6af
  DOWNLOAD_EXTRACT_TIMESTAMP TRUE
)
FetchContent_MakeAvailable(fmt)
