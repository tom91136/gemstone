#include "unified_suite/adapters/host_algorithms.hpp"
#include "unified_suite/adapters/host_blas.hpp"
#include "unified_suite/adapters/host_edge_cases.hpp"
#include "unified_suite/runner.hpp"

#include <limits>
#include <sstream>
#include <stdexcept>
#include <string>
#include <vector>

namespace {

std::size_t algorithm_builds = 0;
std::size_t blas_builds = 0;
std::size_t edge_case_builds = 0;
std::size_t stress_builds = 0;
std::size_t async_builds = 0;

std::vector<unified_suite::Case> algorithm_cases(const unified_suite::Configuration &) {
  ++algorithm_builds;
  return {{"algorithms/provider-contract", [] { return unified_suite::Result::pass(); }}};
}

std::vector<unified_suite::Case> blas_cases(const unified_suite::Configuration &) {
  ++blas_builds;
  return {{"blas/provider-contract", [] { return unified_suite::Result::pass(); }}};
}

std::vector<unified_suite::Case> edge_case_cases(const unified_suite::Configuration &) {
  ++edge_case_builds;
  return {{"edge-cases/provider-contract", [] { return unified_suite::Result::pass(); }}};
}

std::vector<unified_suite::Case> stress_cases(const unified_suite::Configuration &) {
  ++stress_builds;
  return {{"stress/provider-contract", [] { return unified_suite::Result::pass(); }}};
}

std::vector<unified_suite::Case> async_cases(const unified_suite::Configuration &) {
  ++async_builds;
  return {{"async/provider-contract", [] { return unified_suite::Result::pass(); }}};
}

std::vector<unified_suite::Case> red_contract_cases(const unified_suite::Configuration &) {
  return {{"regressions/contract/fails", [] { return unified_suite::Result::fail("known emulator defect"); },
           true},
          {"regressions/contract/passes", [] { return unified_suite::Result::pass(); }, true}};
}

template <class Operation> bool throws_invalid_argument(Operation operation) {
  try {
    operation();
    return false;
  } catch (const std::invalid_argument &) {
    return true;
  }
}

template <class Operation> bool throws_exception(Operation operation) {
  try {
    operation();
    return false;
  } catch (const std::exception &) {
    return true;
  }
}

int run(unified_suite::RunOptions options) {
  std::ostringstream output;
  std::ostringstream errors;
  return unified_suite::run<unified_suite::adapters::HostAlgorithms, unified_suite::adapters::HostBlas,
                            unified_suite::adapters::HostEdgeCases>(options, output, errors);
}

} // namespace

int main() {
  const auto full = unified_suite::configuration_for_profile("full");
  if (std::find(full.algorithm_sizes.begin(), full.algorithm_sizes.end(), 1024) ==
          full.algorithm_sizes.end() ||
      std::find(full.algorithm_sizes.begin(), full.algorithm_sizes.end(), 4096) ==
          full.algorithm_sizes.end() ||
      full.edge_case_sizes.back() != 1024 || full.stress_sizes.back() != 16384)
    return 20;
  if (!throws_invalid_argument([] { (void)unified_suite::configuration_for_profile("unknown"); }))
    return 21;
  for (bool profile_first : {false, true}) {
    char executable[] = "runner", profile[] = "--profile=full", sizes[] = "--sizes=16384";
    char *args[] = {executable, profile_first ? profile : sizes, profile_first ? sizes : profile};
    std::ostringstream out, errors;
    const auto options = unified_suite::parse_options(3, args, out, errors);
    if (!options || options->configuration.algorithm_sizes != std::vector<std::size_t>{16384} ||
        options->configuration.edge_case_sizes != std::vector<std::size_t>{16384} ||
        options->configuration.stress_sizes != std::vector<std::size_t>{16384})
      return 22;
  }
  if (!throws_invalid_argument([] { (void)unified_suite::parse_sizes("-1"); }))
    return 1;
  if (!throws_exception([] { (void)unified_suite::parse_matrix_shapes("18446744073709551615x2x1"); }))
    return 2;

  const auto first = unified_suite::edge_cases::ValueAt<std::int32_t>{}(0);
  bool diverse = false;
  for (std::size_t index = 1; index < 17; ++index)
    diverse = diverse || unified_suite::edge_cases::ValueAt<std::int32_t>{}(index) != first;
  if (!diverse)
    return 3;

  std::int64_t reversed = 0;
  std::int64_t unreversed = 0;
  constexpr std::size_t composed_size = 7;
  for (std::size_t index = 0; index < composed_size; ++index) {
    const auto value = unified_suite::edge_cases::ValueAt<std::int32_t>{}(index);
    const auto reverse = unified_suite::edge_cases::ValueAt<std::int32_t>{}(composed_size - index - 1);
    reversed += unified_suite::edge_cases::WidePairValue<std::int32_t>{}(reverse, value);
    unreversed += unified_suite::edge_cases::WidePairValue<std::int32_t>{}(value, value);
  }
  if (reversed == unreversed)
    return 4;

  if (!throws_exception([] {
        unified_suite::blas::detail::require_close_active<unified_suite::ScalarType::f32>(
            {1.0L, 77.01L}, {1.0L, 77.0L}, {true, false}, "inactive-canary");
      }))
    return 5;
  if (!throws_exception([] {
        unified_suite::algorithms::detail::require_key_permutation(
            std::vector<std::int32_t>{3, 3}, std::vector<std::int32_t>{0, 0}, std::vector<std::int32_t>{3, 3},
            "duplicate-payload");
      }))
    return 6;

  unified_suite::RunOptions missing_case;
  missing_case.exact_case = "not-a-case";
  if (run(missing_case) != 2)
    return 7;

  unified_suite::RunOptions all_skipped;
  all_skipped.suite = unified_suite::SuiteSelection::algorithms;
  all_skipped.configuration.algorithm_sizes = {1};
  all_skipped.configuration.algorithm_types = {unified_suite::ScalarType::f16};
  if (run(all_skipped) == 0)
    return 8;
  all_skipped.allow_all_skipped = true;
  if (run(all_skipped) != 0)
    return 9;

  all_skipped.exact_case = "algorithms/copy/f16/n1";
  if (run(all_skipped) != 77)
    return 10;

  const unified_suite::CatalogProvider red_provider{red_contract_cases, red_contract_cases,
                                                    red_contract_cases, red_contract_cases};
  unified_suite::RunOptions red_options;
  red_options.suite = unified_suite::SuiteSelection::algorithms;
  red_options.filter = std::regex("^regressions/contract/fails$");
  std::ostringstream red_output;
  std::ostringstream red_errors;
  if (unified_suite::run(red_options, red_provider, red_output, red_errors) == 0)
    return 11;
  red_options.expect_reds = true;
  if (unified_suite::run(red_options, red_provider, red_output, red_errors) != 0)
    return 12;
  red_options.filter = std::regex("^regressions/contract/passes$");
  if (unified_suite::run(red_options, red_provider, red_output, red_errors) == 0)
    return 13;

  char executable[] = "runner";
  char include_reds[] = "--include-regressions";
  char expect_reds[] = "--expect-reds";
  char *red_arguments[] = {executable, include_reds, expect_reds};
  std::ostringstream parse_output;
  std::ostringstream parse_errors;
  const auto parsed = unified_suite::parse_options(3, red_arguments, parse_output, parse_errors);
  if (!parsed || !parsed->configuration.include_regressions || !parsed->expect_reds)
    return 14;

  char timing[] = "--timing";
  char *timing_arguments[] = {executable, timing};
  const auto timing_parsed = unified_suite::parse_options(2, timing_arguments, parse_output, parse_errors);
  if (!timing_parsed || !timing_parsed->timing)
    return 15;

  const unified_suite::CatalogProvider provider{algorithm_cases, blas_cases, edge_case_cases, stress_cases};
  unified_suite::RunOptions provider_options;
  provider_options.suite = unified_suite::SuiteSelection::blas;
  const auto provided = unified_suite::catalog(provider_options, provider);
  if (provided.size() != 1 || provided.front().name != "blas/provider-contract")
    return 16;
  const unified_suite::CatalogProvider async_provider{algorithm_cases, blas_cases, edge_case_cases,
                                                      stress_cases, async_cases};
  unified_suite::RunOptions async_options;
  async_options.suite = unified_suite::SuiteSelection::async;
  const auto provided_async = unified_suite::catalog(async_options, async_provider);
  if (provided_async.size() != 1 || provided_async.front().name != "async/provider-contract")
    return 16;
  if (algorithm_builds != 0 || blas_builds != 1 || edge_case_builds != 0 || stress_builds != 0 ||
      async_builds != 1)
    return 17;
  return 0;
}
