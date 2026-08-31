#include "provider.hpp"

#include "unified_suite/runner.hpp"

int main(int argc, char **argv) {
  const unified_suite::CatalogProvider provider{
      unified_suite::program::algorithm_cases,
      unified_suite::program::blas_cases,
      unified_suite::program::edge_case_cases,
      unified_suite::program::stress_cases,
  };
  return unified_suite::main(argc, argv, provider);
}
