#include "memory.hpp"

#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "memory_cases.hpp"

namespace {
using namespace memory_patterns;
// Prerequisites apply only to the selected allocation and operation.
uint64_t prerequisites(const std::string &kind, bool atomic, bool wide) {
  const auto selected = conformance::deviceSelection().nativeHandle;
  ze_device_memory_access_properties_t properties{ZE_STRUCTURE_TYPE_DEVICE_MEMORY_ACCESS_PROPERTIES};
  check(zeDeviceGetMemoryAccessProperties(reinterpret_cast<ze_device_handle_t>(selected), &properties));
  if (wide) {
    ze_device_module_properties_t module{ZE_STRUCTURE_TYPE_DEVICE_MODULE_PROPERTIES};
    check(zeDeviceGetModuleProperties(reinterpret_cast<ze_device_handle_t>(selected), &module));
    if (!(module.flags & ZE_DEVICE_MODULE_FLAG_INT64_ATOMICS)) SKIP("64-bit atomics unavailable");
  }
  auto flags = kind == "host" ? properties.hostAllocCapabilities : properties.sharedSingleDeviceAllocCapabilities;
  const auto needed =
      ZE_MEMORY_ACCESS_CAP_FLAG_RW
      | (atomic ? ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT_ATOMIC | ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT | ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC
                : 0);
  if ((flags & needed) != needed) SKIP("allocation lacks required memory access capabilities");
  return selected;
}
void execute(const char *kind, unsigned scenario) {
  const bool wide = scenario >= 11;
  const auto selected = prerequisites(kind, scenario >= 6, wide);
  Backend backend(kind, selected, wide ? "kernel64.spv" : "kernel.spv");
  runScenario(backend, scenario);
}
} // namespace

#include "cases/memory.inl"

UNIFIED_MEMORY_CASES("shared", "")
UNIFIED_MEMORY_CASES("host", "[full-matrix]")
