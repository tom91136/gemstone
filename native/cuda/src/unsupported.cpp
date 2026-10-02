#include <cstring>
#include <string_view>

#include <cuda.h>
#include <cuda_runtime_api.h>

#include <gemstone/export.h>

extern "C" {

GEMSTONE_INTERNAL_API CUresult CUDAAPI unsupportedDriver() { return CUDA_ERROR_NOT_SUPPORTED; }

GEMSTONE_INTERNAL_API cudaError_t CUDARTAPI unsupportedRuntime() { return cudaErrorNotSupported; }

cudaChannelFormatDesc CUDARTAPI cudaCreateChannelDesc(int, int, int, int, cudaChannelFormatKind) { return {}; }

#if defined(__GNUC__) && !defined(__clang__)
  #pragma GCC diagnostic push
  #pragma GCC diagnostic ignored "-Wattribute-alias"
#endif

#define GEMSTONE_CUDA_DRIVER_UNSUPPORTED(name)                                                                                             \
  using unsupported_type_##name = decltype(name);                                                                                          \
  extern unsupported_type_##name unsupported_alias_##name __asm__(#name) __attribute__((alias("unsupportedDriver"), nothrow));
#define GEMSTONE_CUDA_RUNTIME_UNSUPPORTED(name)                                                                                            \
  using unsupported_type_##name = decltype(name);                                                                                          \
  extern unsupported_type_##name unsupported_alias_##name __asm__(#name) __attribute__((alias("unsupportedRuntime"), nothrow));

#define GEMSTONE_CUDA_DRIVER_LEGACY_UNSUPPORTED(name, current)                                                                             \
  using unsupported_type_##name = decltype(current);                                                                                       \
  extern unsupported_type_##name unsupported_alias_##name __asm__(#name) __attribute__((alias("unsupportedDriver"), nothrow));
#include "unsupported.inc"

#undef GEMSTONE_CUDA_DRIVER_LEGACY_UNSUPPORTED

#undef GEMSTONE_CUDA_DRIVER_UNSUPPORTED
#undef GEMSTONE_CUDA_RUNTIME_UNSUPPORTED

// Symbol availability and execution support are separate: known facade APIs
// resolve normally, then return NOT_SUPPORTED when called. The explicit
// inventory is shared with the exported aliases so the two views cannot drift.
GEMSTONE_INTERNAL_API void *gemstoneCudaUnsupportedDriverSymbol(const char *symbol) {
  const auto matches = [symbol](std::string_view name) {
    if (name == symbol) return true;
    const auto suffix = name.rfind("_v");
    return suffix != std::string_view::npos && suffix + 2 < name.size()
           && name.substr(suffix + 2).find_first_not_of("0123456789") == std::string_view::npos && name.substr(0, suffix) == symbol;
  };
#define GEMSTONE_CUDA_DRIVER_UNSUPPORTED(name)                                                                                             \
  if (matches(#name)) return reinterpret_cast<void *>(&unsupported_alias_##name);
#define GEMSTONE_CUDA_RUNTIME_UNSUPPORTED(name)
#define GEMSTONE_CUDA_DRIVER_LEGACY_UNSUPPORTED(name, current)                                                                             \
  if (matches(#name)) return reinterpret_cast<void *>(&unsupported_alias_##name);
#include "unsupported.inc"

#undef GEMSTONE_CUDA_DRIVER_LEGACY_UNSUPPORTED
#undef GEMSTONE_CUDA_DRIVER_UNSUPPORTED
#undef GEMSTONE_CUDA_RUNTIME_UNSUPPORTED
  return nullptr;
}

#if defined(__GNUC__) && !defined(__clang__)
  #pragma GCC diagnostic pop
#endif

} // extern "C"
