#include <cstdio>

#include <dlfcn.h>

#include "gemstone_cuda_driver_surface.inc"

#define COUNT_DRIVER_SYMBOL(symbol) +1
enum { DriverApiCount = 0 GEMSTONE_CUDA_DRIVER_API(COUNT_DRIVER_SYMBOL) };
#undef COUNT_DRIVER_SYMBOL

int main() {
  void *library = dlopen("libcuda.so.1", RTLD_NOW | RTLD_LOCAL);
  if (library == nullptr) {
    std::fprintf(stderr, "cannot load libcuda.so.1: %s\n", dlerror());
    return 1;
  }

  int missing = 0;
#define CHECK_DRIVER_SYMBOL(symbol)                                                                                                        \
  do {                                                                                                                                     \
    if (dlsym(library, #symbol) == nullptr) {                                                                                              \
      std::fprintf(stderr, "missing Driver symbol %s\n", #symbol);                                                                         \
      ++missing;                                                                                                                           \
    }                                                                                                                                      \
  } while (false);
  GEMSTONE_CUDA_DRIVER_API(CHECK_DRIVER_SYMBOL)
#undef CHECK_DRIVER_SYMBOL

  dlclose(library);
  if (missing != 0) return 1;
  std::printf("all %d pinned CUDA Driver API symbols are present\n", DriverApiCount);
  return 0;
}
