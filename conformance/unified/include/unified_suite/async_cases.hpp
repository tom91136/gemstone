#ifndef UNIFIED_SUITE_ASYNC_CASES_HPP
#define UNIFIED_SUITE_ASYNC_CASES_HPP

#include "unified_suite/algorithms.hpp"
#include "unified_suite/config.hpp"
#include "unified_suite/edge_cases.hpp"
#include "unified_suite/result.hpp"

#include <algorithm>
#include <cstddef>
#include <numeric>
#include <string>
#include <string_view>
#include <vector>

namespace unified_suite::async_cases {

// These operations intentionally describe observable dependency semantics, not a
// particular runtime object.  A vendor adapter may use a CUDA/HIP stream,
// SYCL queue/event, or a host future as the implementation vehicle.
#define UNIFIED_SUITE_ASYNC_OPERATIONS(X)                                                                    \
  X(copy, "copy")                                                                                            \
  X(reduce, "reduce")                                                                                        \
  X(scan, "scan")                                                                                            \
  X(sort, "sort")                                                                                            \
  X(future_chain, "future-chain")                                                                            \
  X(event_chain, "event-chain")                                                                              \
  X(interleaved_streams, "interleaved-streams")

enum class Operation {
#define UNIFIED_SUITE_ASYNC_ENUM(identifier, spelling) identifier,
  UNIFIED_SUITE_ASYNC_OPERATIONS(UNIFIED_SUITE_ASYNC_ENUM)
#undef UNIFIED_SUITE_ASYNC_ENUM
};

struct OperationSpec {
  Operation operation;
  std::string_view name;
};

inline constexpr OperationSpec operation_specs[] = {
#define UNIFIED_SUITE_ASYNC_SPEC(identifier, spelling) {Operation::identifier, spelling},
    UNIFIED_SUITE_ASYNC_OPERATIONS(UNIFIED_SUITE_ASYNC_SPEC)
#undef UNIFIED_SUITE_ASYNC_SPEC
};

inline constexpr Operation operations[] = {
#define UNIFIED_SUITE_ASYNC_VALUE(identifier, spelling) Operation::identifier,
    UNIFIED_SUITE_ASYNC_OPERATIONS(UNIFIED_SUITE_ASYNC_VALUE)
#undef UNIFIED_SUITE_ASYNC_VALUE
};

inline constexpr std::string_view name(Operation operation) {
  for (const auto &spec : operation_specs)
    if (spec.operation == operation)
      return spec.name;
  return "unknown";
}

#undef UNIFIED_SUITE_ASYNC_OPERATIONS

namespace detail {

template <class T> std::vector<T> input_values(std::size_t size) {
  std::vector<T> result(size);
  for (std::size_t index = 0; index < size; ++index)
    result[index] = edge_cases::ValueAt<T>{}(index + 3);
  return result;
}

template <class T> std::vector<T> reference_scan(const std::vector<T> &input) {
  std::vector<T> result(input.size());
  std::inclusive_scan(input.begin(), input.end(), result.begin(), algorithms::Plus<T>{});
  return result;
}

template <class T> std::vector<T> reference_future_chain(const std::vector<T> &input) {
  std::vector<T> result(input.size());
  std::transform(input.begin(), input.end(), result.begin(),
                 [](T value) { return algorithms::Square<T>{}(algorithms::Affine<T>{}(value)); });
  return result;
}

template <class T> std::vector<T> reference_event_chain(const std::vector<T> &input) {
  std::vector<T> result(input.size());
  std::transform(input.begin(), input.end(), result.begin(), [](T value) {
    return algorithms::Combine<T>{}(algorithms::Affine<T>{}(value), algorithms::Square<T>{}(value));
  });
  return result;
}

template <class T> std::vector<T> reference_interleaved(const std::vector<T> &input) {
  std::vector<T> result(input.size());
  for (std::size_t index = 0; index < input.size(); ++index)
    result[index] =
        index % 2 == 0 ? algorithms::Affine<T>{}(input[index]) : algorithms::Square<T>{}(input[index]);
  return result;
}

template <class T> std::vector<T> reference_sort(std::vector<T> input) {
  std::sort(input.begin(), input.end());
  return input;
}

template <class T> T reference_reduce(const std::vector<T> &input) {
  return std::accumulate(input.begin(), input.end(), T{}, algorithms::Plus<T>{});
}

template <class T, class Backend>
void execute(Backend &backend, Operation operation, const std::vector<T> &input) {
  if (!backend.template supports<T>(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for this type/device");

  switch (operation) {
  case Operation::copy:
    algorithms::detail::require_equal(backend.template async_copy<T>(input), input, "async-copy");
    return;
  case Operation::reduce:
    algorithms::detail::require_equal(backend.template async_reduce<T>(input), reference_reduce(input),
                                      "async-reduce");
    return;
  case Operation::scan:
    algorithms::detail::require_equal(backend.template async_scan<T>(input), reference_scan(input),
                                      "async-scan");
    return;
  case Operation::sort:
    algorithms::detail::require_equal(backend.template async_sort<T>(input), reference_sort(input),
                                      "async-sort");
    return;
  case Operation::future_chain:
    algorithms::detail::require_equal(backend.template async_future_chain<T>(input),
                                      reference_future_chain(input), "async-future-chain");
    return;
  case Operation::event_chain:
    algorithms::detail::require_equal(backend.template async_event_chain<T>(input),
                                      reference_event_chain(input), "async-event-chain");
    return;
  case Operation::interleaved_streams:
    algorithms::detail::require_equal(backend.template async_interleaved_streams<T>(input),
                                      reference_interleaved(input), "async-interleaved-streams");
    return;
  }
}

template <class Backend, ScalarType Kind> Case typed_case(Operation operation, std::size_t size) {
  using T = typename algorithms::Native<Kind>::type;
  return {"async/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/n" +
              std::to_string(size),
          [operation, size] {
            return capture([&] {
              Backend backend;
              execute<T>(backend, operation, input_values<T>(size));
            });
          }};
}

template <class Backend>
void append_type(std::vector<Case> &result, ScalarType type, const Configuration &configuration) {
  for (const auto operation : operations)
    for (const auto size : configuration.edge_case_sizes)
      switch (type) {
      case ScalarType::i8:
        result.push_back(typed_case<Backend, ScalarType::i8>(operation, size));
        break;
      case ScalarType::i32:
        result.push_back(typed_case<Backend, ScalarType::i32>(operation, size));
        break;
      case ScalarType::i64:
        result.push_back(typed_case<Backend, ScalarType::i64>(operation, size));
        break;
      case ScalarType::f32:
        result.push_back(typed_case<Backend, ScalarType::f32>(operation, size));
        break;
      case ScalarType::f64:
        result.push_back(typed_case<Backend, ScalarType::f64>(operation, size));
        break;
      case ScalarType::f16:
        result.push_back({"async/" + std::string(name(operation)) + "/f16/n" + std::to_string(size),
                          [] { return Result::skip("f16 async cases are not instantiated"); }});
        break;
      }
}

} // namespace detail

template <class Backend> std::vector<Case> cases(const Configuration &configuration) {
  std::vector<Case> result;
  for (const auto type : configuration.edge_algorithm_types)
    detail::append_type<Backend>(result, type, configuration);
  return result;
}

} // namespace unified_suite::async_cases

#endif
