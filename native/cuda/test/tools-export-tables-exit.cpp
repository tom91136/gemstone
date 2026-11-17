#include <array>
#include <cstdint>
#include <cstdlib>

#include <dlfcn.h>

#include <cuda.h>

namespace {
using ExportTableQuery = CUresult (*)(const void **, const void *);
using DriverStateQuery = CUresult (*)(int *);

constexpr std::array<unsigned char, 16> ToolsDriverStateId{0xd7, 0xbd, 0x8a, 0xf8, 0xe7, 0xac, 0xca, 0x4a,
                                                           0xbd, 0xbc, 0xfb, 0x1f, 0x1b, 0x3f, 0xcf, 0x33};
constexpr std::array<unsigned char, 16> ToolsCallbackRegistryId{0x7c, 0xb6, 0x3f, 0x9b, 0x9c, 0xa4, 0x8e, 0x42,
                                                                0xa4, 0x84, 0xbe, 0xb6, 0x6e, 0x6a, 0xed, 0x7f};
constexpr int DriverStateDeinitialised = 1;
constexpr int DriverStateUninitialised = 2;

ExportTableQuery query = nullptr;
DriverStateQuery state = nullptr;

// Registered before the driver is loaded, so it runs after the driver's own teardown.
void afterDriverTeardown() {
  if (!query || !state) return;
  const void *table = reinterpret_cast<const void *>(uintptr_t{0x1234});
  int value = -1;
  const bool refused = query(&table, ToolsDriverStateId.data()) == CUDA_ERROR_DEINITIALIZED
                       && query(&table, ToolsCallbackRegistryId.data()) == CUDA_ERROR_DEINITIALIZED
                       && table == reinterpret_cast<const void *>(uintptr_t{0x1234});
  if (!refused || state(&value) != CUDA_SUCCESS || value != DriverStateDeinitialised) std::_Exit(EXIT_FAILURE);
}

[[maybe_unused]] const int TeardownCheckRegistered = std::atexit(afterDriverTeardown);
} // namespace

int main() {
  void *const driver = dlopen("libcuda.so.1", RTLD_NOW | RTLD_GLOBAL);
  if (!driver) return EXIT_FAILURE;
  query = reinterpret_cast<ExportTableQuery>(dlsym(driver, "cuGetExportTable"));
  if (!query) return EXIT_FAILURE;
  const void *table = nullptr;
  if (query(&table, ToolsDriverStateId.data()) != CUDA_SUCCESS || !table) return EXIT_FAILURE;
  state = reinterpret_cast<DriverStateQuery>(reinterpret_cast<uintptr_t>(static_cast<const void *const *>(table)[3]));
  int value = -1;
  return state && state(&value) == CUDA_SUCCESS && value == DriverStateUninitialised ? EXIT_SUCCESS : EXIT_FAILURE;
}
