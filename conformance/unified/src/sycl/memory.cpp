#include "memory.hpp"

#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "memory_cases.hpp"

namespace {
using namespace memory_patterns;
// Prerequisites apply only to the selected allocation and operation.
void prerequisites(const std::string &kind, bool atomic, bool wide) {
  (void)conformance::deviceSelection();
  const sycl::device device{sycl::gpu_selector_v};
  if (wide && !device.has(sycl::aspect::atomic64)) SKIP("64-bit atomics unavailable");
  if (!device.has(kind == "host" ? sycl::aspect::usm_host_allocations : sycl::aspect::usm_shared_allocations))
    SKIP("USM allocation unavailable");
  if (atomic && !device.has(kind == "host" ? sycl::aspect::usm_atomic_host_allocations : sycl::aspect::usm_atomic_shared_allocations))
    SKIP("USM allocation lacks concurrent atomics");
}
void execute(const char *kind, unsigned scenario) {
  prerequisites(kind, scenario >= 6, scenario >= 11);
  Backend backend(kind);
  runScenario(backend, scenario);
}
} // namespace

#include "cases/memory.inl"

UNIFIED_MEMORY_CASES("shared", "")
UNIFIED_MEMORY_CASES("host", "[full-matrix]")
