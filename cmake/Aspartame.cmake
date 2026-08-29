include_guard(GLOBAL)

include(FetchContent)

set(ASPARTAME_ENABLE_TEST OFF CACHE BOOL "" FORCE)
set(ASPARTAME_ENABLE_BENCHMARK OFF CACHE BOOL "" FORCE)
FetchContent_Declare(aspartame
  URL https://github.com/tom91136/Aspartame/archive/350653d33df03386c22b0c0baece22ea48ddfef1.tar.gz
  URL_HASH SHA256=32a68e79f35f1bde31abaf3df9df7428e7cbc98081ce6340adc2c27a16be81bc
  DOWNLOAD_EXTRACT_TIMESTAMP TRUE
)
FetchContent_MakeAvailable(aspartame)
