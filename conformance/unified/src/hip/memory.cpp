#include "memory.hpp"

#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "memory_cases.hpp"

namespace {
using namespace memory_patterns;
// Prerequisites apply only to the selected allocation and operation.
void prerequisites(const std::string &kind, bool atomic) {
  (void)conformance::deviceSelection();
  int ordinal{};
  check(hipGetDevice(&ordinal));
  hipDeviceProp_t properties{};
  check(hipGetDeviceProperties(&properties, ordinal));
  if (!properties.unifiedAddressing) SKIP("shared pointer graphs require unified addressing");
  if (kind == "managed" && !properties.managedMemory) SKIP("managed allocations unavailable");
  if (kind != "managed" && !properties.canMapHostMemory) SKIP("mapped host allocations unavailable");
  if (atomic && (!properties.hostNativeAtomicSupported || (kind == "managed" && !properties.concurrentManagedAccess)))
    SKIP("allocation lacks concurrent host/device atomics");
}
void execute(const char *kind, unsigned scenario) {
  prerequisites(kind, scenario >= 6);
  Backend backend(kind);
  runScenario(backend, scenario);
}
} // namespace

#include "cases/memory.inl"

UNIFIED_MEMORY_CASES("managed", "")
UNIFIED_MEMORY_CASES("host", "[full-matrix]")
UNIFIED_MEMORY_CASES("registered", "[full-matrix]")
