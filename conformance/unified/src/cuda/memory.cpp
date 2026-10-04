#include "memory.hpp"

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include "conformance/device.hpp"

#include "memory_cases.hpp"

namespace {
using namespace memory_patterns;
// Prerequisites apply only to the selected allocation and operation.
uint64_t prerequisites(const std::string &kind, bool atomic) {
  auto selected = conformance::device_selection().native_handle;
  CUdevice ordinal{};
  check(cuCtxGetDevice(&ordinal));
  selected = ordinal;
  auto attribute = [&](CUdevice_attribute name) {
    int value{};
    check(cuDeviceGetAttribute(&value, name, ordinal));
    return value;
  };
  const auto architecture =
      attribute(CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MAJOR) * 10 + attribute(CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MINOR);
  if (architecture < 35) SKIP("memory access fixture requires sm_35 or newer");
  if (atomic && architecture < 70) SKIP("acquire/release memory fixtures require sm_70 or newer");
  if (!attribute(CU_DEVICE_ATTRIBUTE_UNIFIED_ADDRESSING)) SKIP("shared pointer graphs require unified addressing");
  if (kind == "managed" && !attribute(CU_DEVICE_ATTRIBUTE_MANAGED_MEMORY)) SKIP("managed allocations unavailable");
  if (kind != "managed" && !attribute(CU_DEVICE_ATTRIBUTE_CAN_MAP_HOST_MEMORY)) SKIP("mapped host allocations unavailable");
  if (atomic
      && (!attribute(CU_DEVICE_ATTRIBUTE_HOST_NATIVE_ATOMIC_SUPPORTED)
          || (kind == "managed" && !attribute(CU_DEVICE_ATTRIBUTE_CONCURRENT_MANAGED_ACCESS))))
    SKIP("allocation lacks concurrent host/device atomics");
  return selected;
}
void execute(const char *kind, unsigned scenario) {
  const auto selected = prerequisites(kind, scenario >= 6);
  const unsigned operation = scenario >= 7 ? (scenario - 7) % 4 : 4;
  Backend backend(kind, selected,
                  scenario == 6    ? "exchange.ptx"
                  : operation == 1 ? "fence.ptx"
                  : operation == 2 ? "contended.ptx"
                  : operation < 4  ? "atomic.ptx"
                                   : "kernel.ptx");
  runScenario(backend, scenario);
}
} // namespace

#define UNIFIED_MEMORY_ALLOCATION "managed"
#define UNIFIED_MEMORY_TAGS ""
#include "cases/memory.inl"

#undef UNIFIED_MEMORY_ALLOCATION
#undef UNIFIED_MEMORY_TAGS
#define UNIFIED_MEMORY_ALLOCATION "pinned"
#define UNIFIED_MEMORY_TAGS "[full-matrix]"
#include "cases/memory.inl"

#undef UNIFIED_MEMORY_ALLOCATION
#undef UNIFIED_MEMORY_TAGS
#define UNIFIED_MEMORY_ALLOCATION "registered"
#define UNIFIED_MEMORY_TAGS "[full-matrix]"
#include "cases/memory.inl"
