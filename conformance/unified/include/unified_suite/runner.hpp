#ifndef UNIFIED_SUITE_RUNNER_HPP
#define UNIFIED_SUITE_RUNNER_HPP

#include "unified_suite/algorithms.hpp"
#include "unified_suite/async_cases.hpp"
#include "unified_suite/blas.hpp"
#include "unified_suite/config.hpp"
#include "unified_suite/edge_cases.hpp"
#include "unified_suite/result.hpp"
#include "unified_suite/stress.hpp"

#include <algorithm>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <exception>
#include <iostream>
#include <iterator>
#include <optional>
#include <regex>
#include <set>
#include <string>
#include <string_view>
#include <vector>

namespace unified_suite {

enum class SuiteSelection { all, algorithms, blas, edge_cases, async, stress };

struct RunOptions {
  Configuration configuration;
  SuiteSelection suite = SuiteSelection::all;
  bool list = false;
  bool fail_fast = false;
  bool quiet_pass = false;
  bool timing = false;
  bool expect_reds = false;
  bool allow_all_skipped = false;
  std::optional<std::string> exact_case;
  std::optional<std::regex> filter;
};

using CaseBuilder = std::vector<Case> (*)(const Configuration &);

struct CatalogProvider {
  CaseBuilder algorithms;
  CaseBuilder blas;
  CaseBuilder edge_cases;
  CaseBuilder stress;
  CaseBuilder async = nullptr;
};

inline void print_usage(std::ostream &output, std::string_view executable) {
  output << "usage: " << executable << " [options]\n"
         << "  --list                              list selected stable case names\n"
         << "  --case=NAME                         run one exact case\n"
         << "  --filter=REGEX                      select case names by regular expression\n"
         << "  --suite=all|algorithms|blas|edge-cases|async|stress\n"
         << "                                      select a suite\n"
         << "  --profile=compact|full              select shared workload defaults\n"
         << "  --sizes=N,N,...                     set sizes for every suite\n"
         << "  --algorithm-sizes=N,N,...           set algorithm sizes\n"
         << "  --blas-sizes=N,N,...                set level-1 BLAS sizes\n"
         << "  --edge-case-sizes=N,N,...           set semantic edge-case sizes\n"
         << "  --stress-sizes=N,N,...              set bounded stress sizes\n"
         << "  --stress-iterations=N               set bounded repetition count\n"
         << "  --matrix-shapes=MxNxK,...           set GEMV/GEMM problem shapes\n"
         << "  --types=TYPE,...                    set scalar types for every suite\n"
         << "  --algorithm-types=TYPE,...          set algorithm scalar types\n"
         << "  --blas-types=TYPE,...               set BLAS scalar types\n"
         << "  --edge-case-types=TYPE,...          set semantic edge-case scalar types\n"
         << "  --edge-algorithm-types=TYPE,...     set edge algorithm scalar types\n"
         << "  --edge-blas-types=TYPE,...          set edge BLAS scalar types\n"
         << "  --stress-types=TYPE,...             set bounded stress scalar types\n"
         << "  --include-regressions               include additional self-contained regression probes\n"
         << "  --fail-fast                         stop after the first failure\n"
         << "  --quiet-pass                        print only skips, failures and summary\n"
         << "  --timing                            print per-case wall-clock timing\n"
         << "  --expect-reds                       require expected regression cases to fail\n"
         << "  --allow-all-skipped                 permit a selected run with zero passes\n"
         << "  --help                              show this help\n"
         << "types: i8,i32,i64,f16,f32,f64\n";
}

namespace detail {

inline std::string_view value(std::string_view argument, std::string_view option) {
  const auto prefix = std::string(option) + "=";
  if (!argument.starts_with(prefix))
    throw std::invalid_argument("expected " + prefix + "VALUE");
  return argument.substr(prefix.size());
}

inline std::size_t option_size(std::string_view argument, std::string_view option) {
  return unified_suite::detail::parse_size(value(argument, option));
}

inline bool selected(const Case &test_case, const RunOptions &options) {
  if (options.exact_case && test_case.name != *options.exact_case)
    return false;
  if (options.filter && !std::regex_search(test_case.name, *options.filter))
    return false;
  return true;
}

template <class AlgorithmBackend> std::vector<Case> algorithm_cases(const Configuration &configuration) {
  return algorithms::cases<AlgorithmBackend>(configuration);
}

template <template <ScalarType> class BlasBackend>
std::vector<Case> blas_cases(const Configuration &configuration) {
  return blas::cases<BlasBackend>(configuration);
}

template <class EdgeCaseBackend, template <ScalarType> class BlasBackend>
std::vector<Case> edge_case_cases(const Configuration &configuration) {
  return edge_cases::cases<EdgeCaseBackend, BlasBackend>(configuration);
}

template <class EdgeCaseBackend> std::vector<Case> stress_cases(const Configuration &configuration) {
  return stress::cases<EdgeCaseBackend>(configuration);
}

} // namespace detail

inline std::optional<RunOptions> parse_options(int argc, char **argv, std::ostream &output,
                                               std::ostream &errors) {
  RunOptions result;
  try {
    // Apply the selected profile first, so explicit overrides win in either argv order.
    for (int index = 1; index < argc; ++index) {
      const std::string_view argument(argv[index]);
      if (argument.starts_with("--profile="))
        result.configuration = configuration_for_profile(detail::value(argument, "--profile"));
    }
    for (int index = 1; index < argc; ++index) {
      const std::string_view argument(argv[index]);
      if (argument == "--help") {
        print_usage(output, argv[0]);
        return std::nullopt;
      }
      if (argument.starts_with("--profile="))
        continue;
      if (argument == "--list")
        result.list = true;
      else if (argument == "--fail-fast")
        result.fail_fast = true;
      else if (argument == "--quiet-pass")
        result.quiet_pass = true;
      else if (argument == "--timing")
        result.timing = true;
      else if (argument == "--expect-reds")
        result.expect_reds = true;
      else if (argument == "--include-regressions")
        result.configuration.include_regressions = true;
      else if (argument == "--allow-all-skipped")
        result.allow_all_skipped = true;
      else if (argument.starts_with("--case="))
        result.exact_case = std::string(detail::value(argument, "--case"));
      else if (argument.starts_with("--filter="))
        result.filter.emplace(std::string(detail::value(argument, "--filter")));
      else if (argument.starts_with("--suite=")) {
        const auto selected = detail::value(argument, "--suite");
        if (selected == "all")
          result.suite = SuiteSelection::all;
        else if (selected == "algorithms")
          result.suite = SuiteSelection::algorithms;
        else if (selected == "blas")
          result.suite = SuiteSelection::blas;
        else if (selected == "edge-cases")
          result.suite = SuiteSelection::edge_cases;
        else if (selected == "async")
          result.suite = SuiteSelection::async;
        else if (selected == "stress")
          result.suite = SuiteSelection::stress;
        else
          throw std::invalid_argument("unknown suite: " + std::string(selected));
      } else if (argument.starts_with("--sizes=")) {
        const auto sizes = parse_sizes(detail::value(argument, "--sizes"));
        result.configuration.algorithm_sizes = sizes;
        result.configuration.blas_sizes = sizes;
        result.configuration.edge_case_sizes = sizes;
        result.configuration.stress_sizes = sizes;
      } else if (argument.starts_with("--algorithm-sizes="))
        result.configuration.algorithm_sizes = parse_sizes(detail::value(argument, "--algorithm-sizes"));
      else if (argument.starts_with("--blas-sizes="))
        result.configuration.blas_sizes = parse_sizes(detail::value(argument, "--blas-sizes"));
      else if (argument.starts_with("--edge-case-sizes="))
        result.configuration.edge_case_sizes = parse_sizes(detail::value(argument, "--edge-case-sizes"));
      else if (argument.starts_with("--stress-sizes="))
        result.configuration.stress_sizes = parse_sizes(detail::value(argument, "--stress-sizes"));
      else if (argument.starts_with("--stress-iterations="))
        result.configuration.stress_iterations = detail::option_size(argument, "--stress-iterations");
      else if (argument.starts_with("--matrix-shapes="))
        result.configuration.matrix_shapes = parse_matrix_shapes(detail::value(argument, "--matrix-shapes"));
      else if (argument.starts_with("--types=")) {
        const auto types = parse_scalar_types(detail::value(argument, "--types"));
        result.configuration.algorithm_types = types;
        result.configuration.blas_types = types;
        result.configuration.edge_algorithm_types = types;
        result.configuration.edge_blas_types = types;
        result.configuration.stress_types = types;
      } else if (argument.starts_with("--algorithm-types="))
        result.configuration.algorithm_types =
            parse_scalar_types(detail::value(argument, "--algorithm-types"));
      else if (argument.starts_with("--blas-types="))
        result.configuration.blas_types = parse_scalar_types(detail::value(argument, "--blas-types"));
      else if (argument.starts_with("--edge-case-types="))
        result.configuration.edge_algorithm_types = result.configuration.edge_blas_types =
            parse_scalar_types(detail::value(argument, "--edge-case-types"));
      else if (argument.starts_with("--edge-algorithm-types="))
        result.configuration.edge_algorithm_types =
            parse_scalar_types(detail::value(argument, "--edge-algorithm-types"));
      else if (argument.starts_with("--edge-blas-types="))
        result.configuration.edge_blas_types =
            parse_scalar_types(detail::value(argument, "--edge-blas-types"));
      else if (argument.starts_with("--stress-types="))
        result.configuration.stress_types = parse_scalar_types(detail::value(argument, "--stress-types"));
      else
        throw std::invalid_argument("unknown option: " + std::string(argument));
    }
    validate_configuration(result.configuration);
    return result;
  } catch (const std::exception &error) {
    errors << "configuration error: " << error.what() << '\n';
    print_usage(errors, argv[0]);
    return std::nullopt;
  }
}

inline std::vector<Case> catalog(const RunOptions &options, const CatalogProvider &provider) {
  std::vector<Case> result;
  if (options.suite == SuiteSelection::all || options.suite == SuiteSelection::algorithms) {
    auto algorithm_cases = provider.algorithms(options.configuration);
    result.insert(result.end(), std::make_move_iterator(algorithm_cases.begin()),
                  std::make_move_iterator(algorithm_cases.end()));
  }
  if (options.suite == SuiteSelection::all || options.suite == SuiteSelection::blas) {
    auto blas_cases = provider.blas(options.configuration);
    result.insert(result.end(), std::make_move_iterator(blas_cases.begin()),
                  std::make_move_iterator(blas_cases.end()));
  }
  if (options.suite == SuiteSelection::all || options.suite == SuiteSelection::edge_cases) {
    auto edge_case_cases = provider.edge_cases(options.configuration);
    result.insert(result.end(), std::make_move_iterator(edge_case_cases.begin()),
                  std::make_move_iterator(edge_case_cases.end()));
  }
  if (options.suite == SuiteSelection::all || options.suite == SuiteSelection::async) {
    if (provider.async == nullptr) {
      if (options.suite == SuiteSelection::async)
        throw std::logic_error("async suite was requested but this provider has no async catalog");
    } else {
      auto async_cases = provider.async(options.configuration);
      result.insert(result.end(), std::make_move_iterator(async_cases.begin()),
                    std::make_move_iterator(async_cases.end()));
    }
  }
  if (options.suite == SuiteSelection::all || options.suite == SuiteSelection::stress) {
    auto stress_cases = provider.stress(options.configuration);
    result.insert(result.end(), std::make_move_iterator(stress_cases.begin()),
                  std::make_move_iterator(stress_cases.end()));
  }
  std::set<std::string> identities;
  for (const auto &test_case : result)
    if (!identities.insert(test_case.name).second)
      throw std::logic_error("duplicate case identity: " + test_case.name);
  return result;
}

template <class AlgorithmBackend, template <ScalarType> class BlasBackend, class EdgeCaseBackend>
constexpr CatalogProvider catalog_provider() {
  return {detail::algorithm_cases<AlgorithmBackend>, detail::blas_cases<BlasBackend>,
          detail::edge_case_cases<EdgeCaseBackend, BlasBackend>, detail::stress_cases<EdgeCaseBackend>};
}

template <class AlgorithmBackend, template <ScalarType> class BlasBackend, class EdgeCaseBackend>
std::vector<Case> catalog(const RunOptions &options) {
  return catalog(options, catalog_provider<AlgorithmBackend, BlasBackend, EdgeCaseBackend>());
}

inline int run(const RunOptions &options, const CatalogProvider &provider, std::ostream &output = std::cout,
               std::ostream &errors = std::cerr) {
  try {
    validate_configuration(options.configuration);
  } catch (const std::invalid_argument &error) {
    errors << "configuration error: " << error.what() << '\n';
    return 2;
  }
  const auto all = catalog(options, provider);
  std::vector<const Case *> selected;
  for (const auto &test_case : all)
    if (detail::selected(test_case, options))
      selected.push_back(&test_case);

  if (options.list) {
    for (const auto *test_case : selected)
      output << test_case->name << '\n';
    return 0;
  }
  if (selected.empty()) {
    errors << "no cases selected\n";
    return 2;
  }

  std::size_t passed = 0;
  std::size_t skipped = 0;
  std::size_t failed = 0;
  std::size_t expected_failed = 0;
  std::size_t unexpected_pass = 0;
  std::size_t unexpected_skip = 0;
  std::size_t executed = 0;
  std::chrono::microseconds total_case_time(0);
  std::chrono::microseconds longest_case_time(0);
  const auto run_started = std::chrono::steady_clock::now();
  for (const auto *test_case : selected) {
    const auto case_started = std::chrono::steady_clock::now();
    const auto result = test_case->execute();
    const auto case_time = std::chrono::duration_cast<std::chrono::microseconds>(
        std::chrono::steady_clock::now() - case_started);
    ++executed;
    total_case_time += case_time;
    longest_case_time = std::max(longest_case_time, case_time);
    const bool expected_red = options.expect_reds && test_case->expected_failure;
    const bool xfail = expected_red && result.status == Status::fail;
    const bool xpass = expected_red && result.status == Status::pass;
    const bool xskip = expected_red && result.status == Status::skip;
    if (xfail)
      ++expected_failed;
    else if (xpass || xskip) {
      if (xpass)
        ++unexpected_pass;
      else
        ++unexpected_skip;
      ++failed;
    } else if (result.status == Status::pass)
      ++passed;
    else if (result.status == Status::skip)
      ++skipped;
    else
      ++failed;
    if (result.status != Status::pass || xpass || xskip || !options.quiet_pass) {
      output << (xfail ? "XFAIL" : (xpass ? "XPASS" : (xskip ? "XSKIP" : name(result.status)))) << ' '
             << test_case->name;
      if (!result.message.empty())
        output << ": " << result.message;
      output << '\n';
      output.flush();
    }
    if (options.timing) {
      output << "TIMING " << test_case->name << " elapsed_us=" << case_time.count() << '\n';
      output.flush();
    }
    if (failed != 0 && options.fail_fast)
      break;
  }
  output << "SUMMARY pass=" << passed << " skip=" << skipped << " fail=" << failed
         << " xfail=" << expected_failed << " xpass=" << unexpected_pass << " xskip=" << unexpected_skip
         << " selected=" << selected.size() << '\n';
  if (options.timing) {
    const auto elapsed =
        std::chrono::duration_cast<std::chrono::microseconds>(std::chrono::steady_clock::now() - run_started);
    output << "TIMING_SUMMARY elapsed_us=" << elapsed.count() << " executed=" << executed
           << " total_case_us=" << total_case_time.count() << " max_case_us=" << longest_case_time.count()
           << '\n';
  }
  if (failed != 0)
    return 1;
  if (options.exact_case && executed == 1 && skipped == 1)
    return 77;
  if (passed == 0 && expected_failed == 0 && !options.allow_all_skipped) {
    errors << "no selected case passed; use --allow-all-skipped only for an intentional capability probe\n";
    return 1;
  }
  return 0;
}

template <class AlgorithmBackend, template <ScalarType> class BlasBackend, class EdgeCaseBackend>
int run(const RunOptions &options, std::ostream &output = std::cout, std::ostream &errors = std::cerr) {
  return run(options, catalog_provider<AlgorithmBackend, BlasBackend, EdgeCaseBackend>(), output, errors);
}

inline int main(int argc, char **argv, const CatalogProvider &provider) {
  bool requested_help = false;
  for (int index = 1; index < argc; ++index)
    requested_help = requested_help || std::string_view(argv[index]) == "--help";
  const auto options = parse_options(argc, argv, std::cout, std::cerr);
  if (!options)
    return requested_help ? 0 : 2;
  try {
    return run(*options, provider);
  } catch (const std::exception &error) {
    std::cerr << "fatal: " << error.what() << '\n';
    return 2;
  }
}

template <class AlgorithmBackend, template <ScalarType> class BlasBackend, class EdgeCaseBackend>
int main(int argc, char **argv) {
  return main(argc, argv, catalog_provider<AlgorithmBackend, BlasBackend, EdgeCaseBackend>());
}

} // namespace unified_suite

#endif
