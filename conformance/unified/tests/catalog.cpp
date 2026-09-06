#include "unified_suite/adapters/host_algorithms.hpp"
#include "unified_suite/adapters/host_blas.hpp"
#include "unified_suite/adapters/host_edge_cases.hpp"
#include "unified_suite/algorithms.hpp"
#include "unified_suite/legacy_reds.hpp"
#include "unified_suite/runner.hpp"

#include <array>
#include <algorithm>
#include <cstdint>
#include <set>
#include <string>

static_assert(std::size(unified_suite::algorithms::operations) == 77,
              "update the catalog contract when adding or removing algorithm forms");
static_assert(std::size(unified_suite::algorithms::operation_specs) ==
              std::size(unified_suite::algorithms::operations));
static_assert(std::size(unified_suite::async_cases::operations) == 7);
static_assert(std::size(unified_suite::async_cases::operation_specs) ==
              std::size(unified_suite::async_cases::operations));
static_assert(std::size(unified_suite::edge_cases::operations) == 11);
static_assert(std::size(unified_suite::edge_cases::blas_operations) == 3);
static_assert(std::size(unified_suite::stress::operations) == 3);
static_assert(std::size(unified_suite::blas::matrix_operations) == 7);
static_assert(std::size(unified_suite::blas::level1_negative_stride_operations) == 3);
static_assert(std::size(unified_suite::legacy_reds::cuda_thrust) == 0);
static_assert(std::size(unified_suite::legacy_reds::cuda_thrust_fixed) == 48);
static_assert(std::size(unified_suite::legacy_reds::roc_thrust) == 0);
static_assert(std::size(unified_suite::legacy_reds::roc_thrust_fixed) == 50);
static_assert(std::size(unified_suite::legacy_reds::roc_historical) == 317);
static_assert(std::size(unified_suite::legacy_reds::deferred) == 72);

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

  const auto byte_sorted = unified_suite::algorithms::detail::sorted_values<std::int8_t>(257, 3);
  if (!std::is_sorted(byte_sorted.begin(), byte_sorted.end()))
    return 7;

  options.configuration.blas_sizes = {1};
  options.configuration.matrix_shapes = {{2, 2, 3}};
  options.configuration.blas_types = {unified_suite::ScalarType::f32};
  options.suite = unified_suite::SuiteSelection::blas;
  const auto blas_cases =
      unified_suite::catalog<unified_suite::adapters::HostAlgorithms, unified_suite::adapters::HostBlas,
                             unified_suite::adapters::HostEdgeCases>(options);
  // Six Level-1 operations have compact, contiguous and zero-length status
  // forms. The two-vector operations additionally cover all-negative and both
  // mixed-sign stride layouts. The one matrix shape has seven compact forms,
  // two padded GEMV forms and four padded transpose/batched GEMM forms.
  if (blas_cases.size() != 44)
    return 2;
  std::set<std::string> blas_identities;
  for (const auto &test_case : blas_cases)
    blas_identities.insert(test_case.name);
  for (const auto &identity :
       {std::string("blas/axpy/f32/n1/negative-stride"), std::string("blas/axpy/f32/n1/x-negative"),
        std::string("blas/copy/f32/n1/y-negative"), std::string("blas/status/dot/f32/n0"),
        std::string("blas/gemv-transpose/f32/2x2x3/padded"),
        std::string("blas/gemm-strided-batched/f32/2x2x3/tt/padded")})
    if (!blas_identities.contains(identity))
      return 3;
  for (const auto &identity : {std::string("blas/scal/f32/n1/negative-stride"),
                               std::string("blas/nrm2/f32/n1/negative-stride"),
                               std::string("blas/asum/f32/n1/negative-stride")})
    if (blas_identities.contains(identity))
      return 6;

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
    return 4;

  options.configuration.stress_sizes = {257, 4096};
  options.configuration.stress_types = {unified_suite::ScalarType::i32, unified_suite::ScalarType::f32};
  options.suite = unified_suite::SuiteSelection::stress;
  const auto stress_cases =
      unified_suite::catalog<unified_suite::adapters::HostAlgorithms, unified_suite::adapters::HostBlas,
                             unified_suite::adapters::HostEdgeCases>(options);
  const auto expected_stress = std::size(unified_suite::stress::operations) * 2 * 2;
  return stress_cases.size() == expected_stress ? 0 : 5;
}
