#include "provider.hpp"

#include "unified_suite/algorithms.hpp"
#include "unified_suite/runner.hpp"

namespace {
std::vector<unified_suite::Case> empty(const unified_suite::Configuration &) { return {}; }
}

int main(int argc, char **argv) {
  using namespace unified_suite;
#if UNIFIED_SUITE_PARTITION == 1
  const CatalogProvider provider{program::algorithm_i8_cases, empty, empty, empty, empty};
#elif UNIFIED_SUITE_PARTITION == 2
  const CatalogProvider provider{program::algorithm_i32_cases, empty, empty, empty, empty};
#elif UNIFIED_SUITE_PARTITION == 3
  const CatalogProvider provider{program::algorithm_i64_cases, empty, empty, empty, empty};
#elif UNIFIED_SUITE_PARTITION == 4
  const CatalogProvider provider{algorithms::f16_cases, empty, empty, empty, empty};
#elif UNIFIED_SUITE_PARTITION == 5
  const CatalogProvider provider{program::algorithm_f32_cases, empty, empty, empty, empty};
#elif UNIFIED_SUITE_PARTITION == 6
  const CatalogProvider provider{program::algorithm_f64_cases, empty, empty, empty, empty};
#elif UNIFIED_SUITE_PARTITION == 7
  const CatalogProvider provider{empty, program::blas_cases, empty, empty, empty};
#elif UNIFIED_SUITE_PARTITION == 8
  const CatalogProvider provider{empty, empty, program::edge_case_cases, empty, empty};
#elif UNIFIED_SUITE_PARTITION == 9
  const CatalogProvider provider{empty, empty, empty, program::stress_cases, empty};
#elif UNIFIED_SUITE_PARTITION == 10
  const CatalogProvider provider{empty, empty, empty, empty, program::async_cases};
#else
#error "unknown unified-suite partition"
#endif
  return unified_suite::main(argc, argv, provider);
}
