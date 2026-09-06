#ifndef UNIFIED_SUITE_HIP_ASYNC_CASES_HPP
#define UNIFIED_SUITE_HIP_ASYNC_CASES_HPP

#include "unified_suite/algorithms.hpp"
#include "unified_suite/async_cases.hpp"
#include "unified_suite/config.hpp"
#include "unified_suite/result.hpp"

#include <algorithm>
#include <cstddef>
#include <numeric>
#include <string>
#include <string_view>
#include <vector>

#if defined(__CUDACC__) || defined(__HIPCC__)
#define UNIFIED_SUITE_HIP_ASYNC_HOST_DEVICE __host__ __device__
#else
#define UNIFIED_SUITE_HIP_ASYNC_HOST_DEVICE
#endif

namespace unified_suite::hip_async_cases {

// These are intentionally kept out of the common async catalog.  The direct
// thrust::async scan API is a HIP/rocThrust coverage target here; CUDA and SYCL
// retain their existing, portable async lane.
enum class Operation {
  inclusive_counting,
  exclusive_counting,
  inclusive_discard,
  exclusive_discard,
  inclusive_stateful,
  exclusive_stateful,
};

struct OperationSpec {
  Operation operation;
  std::string_view name;
};

inline constexpr OperationSpec operation_specs[] = {
    {Operation::inclusive_counting, "inclusive-scan/counting-iterator"},
    {Operation::exclusive_counting, "exclusive-scan/counting-iterator"},
    {Operation::inclusive_discard, "inclusive-scan/discard-output"},
    {Operation::exclusive_discard, "exclusive-scan/discard-output"},
    {Operation::inclusive_stateful, "inclusive-scan/stateful-operator"},
    {Operation::exclusive_stateful, "exclusive-scan/stateful-operator"},
};

inline constexpr Operation operations[] = {
    Operation::inclusive_counting, Operation::exclusive_counting, Operation::inclusive_discard,
    Operation::exclusive_discard,  Operation::inclusive_stateful, Operation::exclusive_stateful,
};

inline constexpr std::size_t maximum_size = 33;

inline constexpr std::string_view name(Operation operation) {
  for (const auto &spec : operation_specs)
    if (spec.operation == operation)
      return spec.name;
  return "unknown";
}

namespace detail {

template <class T> std::vector<T> counting_values(std::size_t size) {
  std::vector<T> result(size);
  for (std::size_t index = 0; index < size; ++index)
    result[index] = static_cast<T>(index);
  return result;
}

template <class T> std::vector<T> counting_reference(std::size_t size, bool exclusive) {
  const auto input = counting_values<T>(size);
  std::vector<T> result(size);
  if (exclusive)
    std::exclusive_scan(input.begin(), input.end(), result.begin(), T{}, algorithms::Plus<T>{});
  else
    std::inclusive_scan(input.begin(), input.end(), result.begin(), algorithms::Plus<T>{});
  return result;
}

template <class T> struct StatefulOperator {
  T offset;

  UNIFIED_SUITE_HIP_ASYNC_HOST_DEVICE T operator()(T left, T right) const { return left + right + offset; }
};

template <class T> std::vector<T> stateful_reference(std::size_t size, bool exclusive) {
  const auto input = async_cases::detail::input_values<T>(size);
  std::vector<T> result(size);
  const StatefulOperator<T> operation{T{2}};
  if (exclusive)
    std::exclusive_scan(input.begin(), input.end(), result.begin(), T{42}, operation);
  else
    std::inclusive_scan(input.begin(), input.end(), result.begin(), operation);
  return result;
}

template <class T, class Backend> void execute(Backend &backend, Operation operation, std::size_t size) {
  if (!backend.template supports<T>(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for this HIP async type/device");

  switch (operation) {
  case Operation::inclusive_counting:
    algorithms::detail::require_equal(backend.template async_inclusive_scan_counting<T>(size),
                                      counting_reference<T>(size, false),
                                      "hip-async-inclusive-scan-counting-iterator");
    return;
  case Operation::exclusive_counting:
    algorithms::detail::require_equal(backend.template async_exclusive_scan_counting<T>(size),
                                      counting_reference<T>(size, true),
                                      "hip-async-exclusive-scan-counting-iterator");
    return;
  case Operation::inclusive_discard:
    backend.template async_inclusive_scan_discard<T>(async_cases::detail::input_values<T>(size));
    return;
  case Operation::exclusive_discard:
    backend.template async_exclusive_scan_discard<T>(async_cases::detail::input_values<T>(size));
    return;
  case Operation::inclusive_stateful:
    algorithms::detail::require_equal(backend.template async_inclusive_scan_stateful<T>(size),
                                      stateful_reference<T>(size, false),
                                      "hip-async-inclusive-scan-stateful-operator");
    return;
  case Operation::exclusive_stateful:
    algorithms::detail::require_equal(backend.template async_exclusive_scan_stateful<T>(size),
                                      stateful_reference<T>(size, true),
                                      "hip-async-exclusive-scan-stateful-operator");
    return;
  }
}

template <class Backend, ScalarType Kind> Case typed_case(Operation operation, std::size_t size) {
  using T = typename algorithms::Native<Kind>::type;
  return {"async/hip/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/n" +
              std::to_string(size),
          [operation, size] {
            return capture([&] {
              Backend backend;
              execute<T>(backend, operation, size);
            });
          }};
}

template <class Backend>
void append_type(std::vector<Case> &result, ScalarType type, const Configuration &configuration) {
  for (const auto operation : operations)
    for (const auto size : configuration.edge_case_sizes) {
      if (size > maximum_size)
        continue;
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
        result.push_back({"async/hip/" + std::string(name(operation)) + "/f16/n" + std::to_string(size),
                          [] { return Result::skip("f16 HIP async scan cases are not instantiated"); }});
        break;
      }
    }
}

} // namespace detail

template <class Backend> std::vector<Case> cases(const Configuration &configuration) {
  std::vector<Case> result;
  for (const auto type : configuration.edge_algorithm_types)
    detail::append_type<Backend>(result, type, configuration);
  return result;
}

} // namespace unified_suite::hip_async_cases

#undef UNIFIED_SUITE_HIP_ASYNC_HOST_DEVICE

#endif
