#include "memory.hpp"

#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "memory_cases.hpp"

namespace {
using namespace memory_patterns;
// Prerequisites apply only to the selected allocation and operation.
uint64_t prerequisites(const std::string &kind, bool atomic, bool wide) {
  auto selected = conformance::device_selection().native_handle;
  int ordinal{};
  check(hipGetDevice(&ordinal));
  hipDeviceProp_t properties{};
  check(hipGetDeviceProperties(&properties, ordinal));
  if (!properties.unifiedAddressing) SKIP("shared pointer graphs require unified addressing");
  if (kind == "managed" && !properties.managedMemory) SKIP("managed allocations unavailable");
  if (kind != "managed" && !properties.canMapHostMemory) SKIP("mapped host allocations unavailable");
  if (atomic && (!properties.hostNativeAtomicSupported || (kind == "managed" && !properties.concurrentManagedAccess)))
    SKIP("allocation lacks concurrent host/device atomics");
  return selected;
}
void execute(const char *kind, unsigned scenario) {
  const bool wide = scenario >= 11;
  const auto selected = prerequisites(kind, scenario >= 6, wide);
  Backend backend(kind, selected);
  runScenario(backend, scenario);
}
} // namespace

#define UNIFIED_MEMORY_ALLOCATION "managed"
#define UNIFIED_MEMORY_TAGS ""
#include "cases/memory.inl"

#undef UNIFIED_MEMORY_ALLOCATION
#undef UNIFIED_MEMORY_TAGS
#define UNIFIED_MEMORY_ALLOCATION "host"
#define UNIFIED_MEMORY_TAGS "[full-matrix]"
#include "cases/memory.inl"

#undef UNIFIED_MEMORY_ALLOCATION
#undef UNIFIED_MEMORY_TAGS
#define UNIFIED_MEMORY_ALLOCATION "registered"
#define UNIFIED_MEMORY_TAGS "[full-matrix]"
#include "cases/memory.inl"
