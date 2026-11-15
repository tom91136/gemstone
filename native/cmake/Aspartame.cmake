include_guard(GLOBAL)

include(FetchContent)

set(ASPARTAME_ENABLE_TEST OFF CACHE BOOL "" FORCE)
set(ASPARTAME_ENABLE_BENCHMARK OFF CACHE BOOL "" FORCE)
FetchContent_Declare(aspartame
  URL https://github.com/tom91136/Aspartame/archive/298876536d4403d9fd3d0d9960a9396aa738f5ac.tar.gz
  URL_HASH SHA256=c5784265a9db9f8dae5414115892ccba0845657b1f57d26aa0d4934c1b0e65bb
  DOWNLOAD_EXTRACT_TIMESTAMP TRUE
)
FetchContent_MakeAvailable(aspartame)
