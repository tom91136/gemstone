#ifndef UNIFIED_SUITE_STRESS_HPP
#define UNIFIED_SUITE_STRESS_HPP

#include "unified_suite/config.hpp"
#include "unified_suite/edge_cases.hpp"
#include "unified_suite/result.hpp"

#include <cstddef>
#include <string>
#include <vector>

namespace unified_suite::stress {

enum class Operation { dependency_diamond, primitive_block_scan, numeric_roundtrip };

inline constexpr Operation operations[] = {Operation::dependency_diamond, Operation::primitive_block_scan,
                                           Operation::numeric_roundtrip};

inline constexpr std::string_view name(Operation operation) {
  switch (operation) {
  case Operation::dependency_diamond:
    return "dependency-diamond";
  case Operation::primitive_block_scan:
    return "primitive-block-scan";
  case Operation::numeric_roundtrip:
    return "numeric-roundtrip";
  }
  return "unknown";
}

namespace detail {

template <class T, class Backend>
void execute(Backend &backend, Operation operation, std::size_t size, std::size_t iterations) {
  const auto edge_operation = [operation] {
    switch (operation) {
    case Operation::dependency_diamond:
      return edge_cases::Operation::dependency_diamond;
    case Operation::primitive_block_scan:
      return edge_cases::Operation::primitive_block_scan;
    case Operation::numeric_roundtrip:
      return edge_cases::Operation::numeric_edge_roundtrip;
    }
    return edge_cases::Operation::numeric_edge_roundtrip;
  }();
  for (std::size_t iteration = 0; iteration < iterations; ++iteration)
    edge_cases::detail::execute<T>(backend, edge_operation, size);
}

template <class Backend, ScalarType Kind>
Case stress_case(Operation operation, std::size_t size, std::size_t iterations) {
  using T = typename algorithms::Native<Kind>::type;
  return {"stress/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/n" +
              std::to_string(size) + "/iterations" + std::to_string(iterations),
          [operation, size, iterations] {
            return capture([&] {
              Backend backend;
              execute<T>(backend, operation, size, iterations);
            });
          }};
}

template <class Backend>
void append_type(std::vector<Case> &result, ScalarType type, const Configuration &configuration) {
  for (const auto operation : operations)
    for (const auto size : configuration.stress_sizes)
      switch (type) {
      case ScalarType::i8:
        result.push_back(
            stress_case<Backend, ScalarType::i8>(operation, size, configuration.stress_iterations));
        break;
      case ScalarType::i32:
        result.push_back(
            stress_case<Backend, ScalarType::i32>(operation, size, configuration.stress_iterations));
        break;
      case ScalarType::i64:
        result.push_back(
            stress_case<Backend, ScalarType::i64>(operation, size, configuration.stress_iterations));
        break;
      case ScalarType::f32:
        result.push_back(
            stress_case<Backend, ScalarType::f32>(operation, size, configuration.stress_iterations));
        break;
      case ScalarType::f64:
        result.push_back(
            stress_case<Backend, ScalarType::f64>(operation, size, configuration.stress_iterations));
        break;
      case ScalarType::f16:
        result.push_back({"stress/" + std::string(name(operation)) + "/f16/n" + std::to_string(size) +
                              "/iterations" + std::to_string(configuration.stress_iterations),
                          [] { return Result::skip("f16 stress cases are not instantiated"); }});
        break;
      }
}

} // namespace detail

template <class Backend> std::vector<Case> cases(const Configuration &configuration) {
  std::vector<Case> result;
  for (const auto type : configuration.stress_types)
    detail::append_type<Backend>(result, type, configuration);
  return result;
}

} // namespace unified_suite::stress

#endif
