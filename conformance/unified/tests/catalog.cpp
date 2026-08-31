#include "unified_suite/adapters/host_algorithms.hpp"
#include "unified_suite/adapters/host_blas.hpp"
#include "unified_suite/adapters/host_edge_cases.hpp"
#include "unified_suite/algorithms.hpp"
#include "unified_suite/runner.hpp"

#include <array>
#include <set>
#include <string>

static_assert(std::size(unified_suite::algorithms::operations) == 74,
              "update the catalog contract when adding or removing algorithm forms");
static_assert(std::size(unified_suite::algorithms::operation_specs) ==
              std::size(unified_suite::algorithms::operations));
static_assert(std::size(unified_suite::edge_cases::operations) == 11);
static_assert(std::size(unified_suite::edge_cases::blas_operations) == 3);
static_assert(std::size(unified_suite::stress::operations) == 3);
static_assert(std::size(unified_suite::blas::matrix_operations) == 7);

int main() {
  unified_suite::RunOptions options;
  options.configuration.algorithm_sizes = {0, 1, 33};
  options.configuration.algorithm_types = {unified_suite::ScalarType::i32};
  options.suite = unified_suite::SuiteSelection::algorithms;

  const auto algorithm_cases =
      unified_suite::catalog<unified_suite::adapters::HostAlgorithms, unified_suite::adapters::HostBlas,
                             unified_suite::adapters::HostEdgeCases>(options);
  std::set<std::string> identities;
  for (const auto &test_case : algorithm_cases)
    identities.insert(test_case.name);

  const auto expected_algorithms = std::size(unified_suite::algorithms::operations) * 3;
  if (algorithm_cases.size() != expected_algorithms || identities.size() != expected_algorithms)
    return 1;

  options.configuration.algorithm_types = {unified_suite::ScalarType::i32, unified_suite::ScalarType::f32};
  const auto i32_algorithm_cases =
      unified_suite::algorithms::cases_for<unified_suite::adapters::HostAlgorithms,
                                           unified_suite::ScalarType::i32>(options.configuration);
  if (i32_algorithm_cases.size() != expected_algorithms)
    return 1;
  for (const auto &test_case : i32_algorithm_cases)
    if (test_case.name.find("/i32/") == std::string::npos)
      return 1;

  options.configuration.edge_case_sizes = {0, 33};
  options.configuration.edge_algorithm_types = {unified_suite::ScalarType::i32,
                                                unified_suite::ScalarType::f32};
  options.configuration.edge_blas_types = {unified_suite::ScalarType::f32};
  options.suite = unified_suite::SuiteSelection::edge_cases;
  const auto edge_cases =
      unified_suite::catalog<unified_suite::adapters::HostAlgorithms, unified_suite::adapters::HostBlas,
                             unified_suite::adapters::HostEdgeCases>(options);
  const auto expected_edge_cases = std::size(unified_suite::edge_cases::operations) * 2 * 2 +
                                   std::size(unified_suite::edge_cases::blas_operations) * 2;
  if (edge_cases.size() != expected_edge_cases)
    return 1;

  options.configuration.stress_sizes = {257, 4096};
  options.configuration.stress_types = {unified_suite::ScalarType::i32, unified_suite::ScalarType::f32};
  options.suite = unified_suite::SuiteSelection::stress;
  const auto stress_cases =
      unified_suite::catalog<unified_suite::adapters::HostAlgorithms, unified_suite::adapters::HostBlas,
                             unified_suite::adapters::HostEdgeCases>(options);
  const auto expected_stress = std::size(unified_suite::stress::operations) * 2 * 2;
  return stress_cases.size() == expected_stress ? 0 : 1;
}
