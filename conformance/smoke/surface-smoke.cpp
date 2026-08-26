#include <cstdio>

#include <dlfcn.h>

#include "gemsi_cuda_driver_surface.inc"

#define COUNT_DRIVER_SYMBOL(symbol) +1
enum { DriverApiCount = 0 GEMSI_CUDA_DRIVER_API(COUNT_DRIVER_SYMBOL) };
#undef COUNT_DRIVER_SYMBOL
static_assert(DriverApiCount == 144);

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
  GEMSI_CUDA_DRIVER_API(CHECK_DRIVER_SYMBOL)
#undef CHECK_DRIVER_SYMBOL

  dlclose(library);
  if (missing != 0) return 1;
  std::puts("all 144 GPUOcelot Driver API symbols are present");
  return 0;
}
