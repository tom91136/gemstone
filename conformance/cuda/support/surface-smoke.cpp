#include <cstdio>
#include <cstdint>
#include <cstdlib>

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

  using ComputeCapability = std::int32_t (*)(std::int32_t *, std::int32_t *, std::int32_t);
  auto compute_capability = reinterpret_cast<ComputeCapability>(dlsym(library, "cuDeviceComputeCapability"));
  if (compute_capability == nullptr) {
    std::fprintf(stderr, "missing target-contract symbol cuDeviceComputeCapability\n");
    ++missing;
  } else {
    struct TargetCase {
      const char *value;
      std::int32_t major;
      std::int32_t minor;
    } cases[] = {
        {"sm_75", 7, 5}, {"compute_89", 8, 9}, {"sm89", 8, 9}, {"compute90", 9, 0}};
    for (const TargetCase &target : cases) {
      if (setenv("GEMSTONE_CUDA_TARGET_ARCH", target.value, 1) != 0) {
        std::fprintf(stderr, "cannot set target contract %s\n", target.value);
        ++missing;
        continue;
      }
      std::int32_t major = -1;
      std::int32_t minor = -1;
      const std::int32_t status = compute_capability(&major, &minor, 0);
      if (status != 0 || major != target.major || minor != target.minor) {
        std::fprintf(
            stderr,
            "target contract %s returned status=%d capability=%d.%d, expected %d.%d\n",
            target.value,
            status,
            major,
            minor,
            target.major,
            target.minor);
        ++missing;
      }
    }
  }

  dlclose(library);
  if (missing != 0) return 1;
  std::printf("all %d pinned CUDA Driver API symbols are present\n", DriverApiCount);
  return 0;
}
