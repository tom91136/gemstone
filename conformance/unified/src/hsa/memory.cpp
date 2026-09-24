#include "memory.hpp"

#include <catch2/catch_test_macros.hpp>

#include "conformance/device.hpp"

#include "memory_cases.hpp"

namespace {
using namespace memory_patterns;
void execute(const char *kind, unsigned scenario) {
  const auto selected = conformance::device_selection().native_handle;
  Backend backend(kind, selected);
  if (std::string(kind) == "pool") {
    if (!backend.pool.handle) SKIP("fine-grained host pool unavailable");
  } else {
    hsa_region_global_flag_t flags{};
    check(hsa_region_get_info(backend.region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags));
    if (!(flags & HSA_REGION_GLOBAL_FLAG_FINE_GRAINED)) SKIP("fine-grained host region unavailable");
  }
  runScenario(backend, scenario);
}
} // namespace

#define UNIFIED_MEMORY_ALLOCATION "region"
#define UNIFIED_MEMORY_TAGS ""
#include "cases/memory.inl"

#undef UNIFIED_MEMORY_ALLOCATION
#undef UNIFIED_MEMORY_TAGS
#define UNIFIED_MEMORY_ALLOCATION "pool"
#define UNIFIED_MEMORY_TAGS "[full-matrix]"
#include "cases/memory.inl"

#undef UNIFIED_MEMORY_ALLOCATION
#undef UNIFIED_MEMORY_TAGS
#define UNIFIED_MEMORY_ALLOCATION "locked"
#define UNIFIED_MEMORY_TAGS "[full-matrix]"
#include "cases/memory.inl"
