#include "memory.hpp"

#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "memory_cases.hpp"

namespace {
using namespace memory_patterns;
// Prerequisites apply only to the selected allocation and operation.
uint64_t prerequisites(const std::string &kind, bool atomic, bool wide) {
  auto selected = conformance::device_selection().native_handle;
  const sycl::device device{sycl::gpu_selector_v};
  if (wide && !device.has(sycl::aspect::atomic64)) SKIP("64-bit atomics unavailable");
  if (!device.has(kind == "host" ? sycl::aspect::usm_host_allocations : sycl::aspect::usm_shared_allocations))
    SKIP("USM allocation unavailable");
  if (atomic && !device.has(kind == "host" ? sycl::aspect::usm_atomic_host_allocations : sycl::aspect::usm_atomic_shared_allocations))
    SKIP("USM allocation lacks concurrent atomics");
  return selected;
}
void execute(const char *kind, unsigned scenario) {
  const bool wide = scenario >= 11;
  const auto selected = prerequisites(kind, scenario >= 6, wide);
  Backend backend(kind, selected);
  runScenario(backend, scenario);
}
} // namespace

#define UNIFIED_MEMORY_ALLOCATION "shared"
#define UNIFIED_MEMORY_TAGS ""
#include "cases/memory.inl"

#undef UNIFIED_MEMORY_ALLOCATION
#undef UNIFIED_MEMORY_TAGS
#define UNIFIED_MEMORY_ALLOCATION "host"
#define UNIFIED_MEMORY_TAGS "[full-matrix]"
#include "cases/memory.inl"
