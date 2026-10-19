#include "memory.hpp"

#include <catch2/catch_test_macros.hpp>
#include <cuda.h>

#include "conformance/device.hpp"

#include "memory_cases.hpp"

namespace {
using namespace memory_patterns;
// Prerequisites apply only to the selected allocation and operation.
CUdevice prerequisites(const std::string &kind, bool atomic) {
  (void)conformance::deviceSelection();
  CUdevice ordinal{};
  check(cuCtxGetDevice(&ordinal));
  auto attribute = [&](CUdevice_attribute name) {
    int value{};
    check(cuDeviceGetAttribute(&value, name, ordinal));
    return value;
  };
  const auto architecture =
      (attribute(CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MAJOR) * 10) + attribute(CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MINOR);
  if (architecture < 35) SKIP("memory access fixture requires sm_35 or newer");
  if (atomic && architecture < 70) SKIP("acquire/release memory fixtures require sm_70 or newer");
  if (!attribute(CU_DEVICE_ATTRIBUTE_UNIFIED_ADDRESSING)) SKIP("shared pointer graphs require unified addressing");
  if (kind == "managed" && !attribute(CU_DEVICE_ATTRIBUTE_MANAGED_MEMORY)) SKIP("managed allocations unavailable");
  if (kind != "managed" && !attribute(CU_DEVICE_ATTRIBUTE_CAN_MAP_HOST_MEMORY)) SKIP("mapped host allocations unavailable");
  if (atomic
      && (!attribute(CU_DEVICE_ATTRIBUTE_HOST_NATIVE_ATOMIC_SUPPORTED)
          || (kind == "managed" && !attribute(CU_DEVICE_ATTRIBUTE_CONCURRENT_MANAGED_ACCESS))))
    SKIP("allocation lacks concurrent host/device atomics");
  return ordinal;
}
const char *fixture(unsigned scenario) {
  if (scenario == 6) return "exchange.ptx";
  const unsigned operation = scenario >= 7 ? (scenario - 7) % 4 : 4;
  if (operation == 1) return "fence.ptx";
  if (operation == 2) return "contended.ptx";
  return operation < 4 ? "atomic.ptx" : "kernel.ptx";
}
void execute(const char *kind, unsigned scenario) {
  const auto device = prerequisites(kind, scenario >= 6);
  Backend backend(kind, static_cast<uint64_t>(device), fixture(scenario));
  runScenario(backend, scenario);
}
} // namespace

#include "cases/memory.inl"

UNIFIED_MEMORY_CASES("managed", "")
UNIFIED_MEMORY_CASES("pinned", "[full-matrix]")
UNIFIED_MEMORY_CASES("registered", "[full-matrix]")
