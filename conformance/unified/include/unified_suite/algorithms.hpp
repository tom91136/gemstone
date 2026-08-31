#ifndef UNIFIED_SUITE_ALGORITHMS_HPP
#define UNIFIED_SUITE_ALGORITHMS_HPP

#include "unified_suite/config.hpp"
#include "unified_suite/result.hpp"

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <iterator>
#include <numeric>
#include <sstream>
#include <string>
#include <type_traits>
#include <utility>
#include <vector>

#if defined(__CUDACC__) || defined(__HIPCC__)
#define UNIFIED_SUITE_HOST_DEVICE __host__ __device__
#else
#define UNIFIED_SUITE_HOST_DEVICE
#endif

namespace unified_suite::algorithms {

#define UNIFIED_SUITE_ALGORITHM_OPERATIONS(X)                                                                \
  X(copy, "copy")                                                                                            \
  X(copy_if, "copy-if")                                                                                      \
  X(copy_n, "copy-n")                                                                                        \
  X(fill, "fill")                                                                                            \
  X(generate, "generate")                                                                                    \
  X(for_each, "for-each")                                                                                    \
  X(transform_unary, "transform-unary")                                                                      \
  X(transform_binary, "transform-binary")                                                                    \
  X(replace_if, "replace-if")                                                                                \
  X(replace_copy_if, "replace-copy-if")                                                                      \
  X(remove_if, "remove-if")                                                                                  \
  X(remove_copy_if, "remove-copy-if")                                                                        \
  X(reverse, "reverse")                                                                                      \
  X(reverse_copy, "reverse-copy")                                                                            \
  X(swap_ranges, "swap-ranges")                                                                              \
  X(reduce, "reduce")                                                                                        \
  X(transform_reduce, "transform-reduce")                                                                    \
  X(inner_product, "inner-product")                                                                          \
  X(inclusive_scan, "inclusive-scan")                                                                        \
  X(exclusive_scan, "exclusive-scan")                                                                        \
  X(transform_inclusive_scan, "transform-inclusive-scan")                                                    \
  X(transform_exclusive_scan, "transform-exclusive-scan")                                                    \
  X(adjacent_difference, "adjacent-difference")                                                              \
  X(sort, "sort")                                                                                            \
  X(stable_sort, "stable-sort")                                                                              \
  X(sort_descending, "sort-descending")                                                                      \
  X(sort_by_key, "sort-by-key")                                                                              \
  X(stable_sort_by_key, "stable-sort-by-key")                                                                \
  X(unique, "unique")                                                                                        \
  X(unique_copy, "unique-copy")                                                                              \
  X(count, "count")                                                                                          \
  X(count_if, "count-if")                                                                                    \
  X(find, "find")                                                                                            \
  X(find_if, "find-if")                                                                                      \
  X(all_of, "all-of")                                                                                        \
  X(any_of, "any-of")                                                                                        \
  X(none_of, "none-of")                                                                                      \
  X(equal, "equal")                                                                                          \
  X(mismatch, "mismatch")                                                                                    \
  X(lower_bound, "lower-bound")                                                                              \
  X(upper_bound, "upper-bound")                                                                              \
  X(binary_search, "binary-search")                                                                          \
  X(equal_range, "equal-range")                                                                              \
  X(merge, "merge")                                                                                          \
  X(partition, "partition")                                                                                  \
  X(stable_partition, "stable-partition")                                                                    \
  X(partition_copy, "partition-copy")                                                                        \
  X(is_partitioned, "is-partitioned")                                                                        \
  X(is_partitioned_false, "is-partitioned-false")                                                            \
  X(partition_point, "partition-point")                                                                      \
  X(set_union, "set-union")                                                                                  \
  X(set_intersection, "set-intersection")                                                                    \
  X(set_difference, "set-difference")                                                                        \
  X(set_symmetric_difference, "set-symmetric-difference")                                                    \
  X(min_element, "min-element")                                                                              \
  X(max_element, "max-element")                                                                              \
  X(minmax_element, "minmax-element")                                                                        \
  X(is_sorted, "is-sorted")                                                                                  \
  X(is_sorted_false, "is-sorted-false")                                                                      \
  X(is_sorted_until, "is-sorted-until")                                                                      \
  X(rotate, "rotate")                                                                                        \
  X(rotate_copy, "rotate-copy")                                                                              \
  X(includes, "includes")                                                                                    \
  X(includes_false, "includes-false")                                                                        \
  X(gather, "gather")                                                                                        \
  X(scatter, "scatter")                                                                                      \
  X(reduce_by_key, "reduce-by-key")                                                                          \
  X(inclusive_scan_by_key, "inclusive-scan-by-key")                                                          \
  X(exclusive_scan_by_key, "exclusive-scan-by-key")                                                          \
  X(unique_by_key, "unique-by-key")                                                                          \
  X(merge_by_key, "merge-by-key")                                                                            \
  X(partial_sort, "partial-sort")                                                                            \
  X(nth_element, "nth-element")                                                                              \
  X(inplace_merge, "inplace-merge")

enum class Operation {
#define UNIFIED_SUITE_OPERATION_ENUM(identifier, spelling) identifier,
  UNIFIED_SUITE_ALGORITHM_OPERATIONS(UNIFIED_SUITE_OPERATION_ENUM)
#undef UNIFIED_SUITE_OPERATION_ENUM
};

struct OperationSpec {
  Operation operation;
  std::string_view name;
};

inline constexpr OperationSpec operation_specs[] = {
#define UNIFIED_SUITE_OPERATION_SPEC(identifier, spelling) {Operation::identifier, spelling},
    UNIFIED_SUITE_ALGORITHM_OPERATIONS(UNIFIED_SUITE_OPERATION_SPEC)
#undef UNIFIED_SUITE_OPERATION_SPEC
};

inline constexpr Operation operations[] = {
#define UNIFIED_SUITE_OPERATION_VALUE(identifier, spelling) Operation::identifier,
    UNIFIED_SUITE_ALGORITHM_OPERATIONS(UNIFIED_SUITE_OPERATION_VALUE)
#undef UNIFIED_SUITE_OPERATION_VALUE
};

inline constexpr std::string_view name(Operation operation) {
  for (const auto &spec : operation_specs)
    if (spec.operation == operation)
      return spec.name;
  return "unknown";
}

#undef UNIFIED_SUITE_ALGORITHM_OPERATIONS

template <ScalarType> struct Native;
template <> struct Native<ScalarType::i8> {
  using type = std::int8_t;
};
template <> struct Native<ScalarType::i32> {
  using type = std::int32_t;
};
template <> struct Native<ScalarType::i64> {
  using type = std::int64_t;
};
template <> struct Native<ScalarType::f32> {
  using type = float;
};
template <> struct Native<ScalarType::f64> {
  using type = double;
};

template <class T> struct Affine {
  UNIFIED_SUITE_HOST_DEVICE T operator()(T value) const { return static_cast<T>(value * T{3} - T{2}); }
};

template <class T> struct Combine {
  UNIFIED_SUITE_HOST_DEVICE T operator()(T left, T right) const {
    return static_cast<T>(left * T{2} + right * T{5});
  }
};

template <class T> struct Square {
  UNIFIED_SUITE_HOST_DEVICE T operator()(T value) const { return static_cast<T>(value * value); }
};

template <class T> struct Increment {
  UNIFIED_SUITE_HOST_DEVICE void operator()(T &value) const { value = static_cast<T>(value + T{1}); }
};

template <class T> struct Constant {
  UNIFIED_SUITE_HOST_DEVICE T operator()() const { return T{7}; }
};

template <class T> struct Plus {
  UNIFIED_SUITE_HOST_DEVICE T operator()(T left, T right) const { return static_cast<T>(left + right); }
};

template <class T> struct IsEven {
  UNIFIED_SUITE_HOST_DEVICE bool operator()(T value) const {
    return static_cast<std::int64_t>(value) % 2 == 0;
  }
};

template <class T> struct Less {
  UNIFIED_SUITE_HOST_DEVICE bool operator()(T left, T right) const { return left < right; }
};

template <class T> struct Greater {
  UNIFIED_SUITE_HOST_DEVICE bool operator()(T left, T right) const { return right < left; }
};

template <class T> struct Partitioned {
  std::vector<T> values;
  std::size_t boundary;
};

template <class T> struct KeyValues {
  std::vector<T> keys;
  std::vector<std::int32_t> values;
};

template <class T> struct TwoRanges {
  std::vector<T> first;
  std::vector<T> second;
};

struct TwoPositions {
  std::size_t first;
  std::size_t second;
};

namespace detail {

inline void require(bool condition, const std::string &message) {
  if (!condition)
    throw std::runtime_error(message);
}

template <class T> inline bool equal(T actual, T expected) { return actual == expected; }

template <class T> inline void require_equal(T actual, T expected, const std::string &where) {
  if (!equal(actual, expected)) {
    std::ostringstream message;
    message << where << ": expected " << +expected << ", got " << +actual;
    throw std::runtime_error(message.str());
  }
}

template <class T>
inline void require_equal(const std::vector<T> &actual, const std::vector<T> &expected,
                          const std::string &where) {
  require(actual.size() == expected.size(), where + ": result size mismatch");
  for (std::size_t index = 0; index < expected.size(); ++index)
    require_equal(actual[index], expected[index], where + "[" + std::to_string(index) + "]");
}

template <class T>
inline void require_equal(const KeyValues<T> &actual, const KeyValues<T> &expected,
                          const std::string &where) {
  require_equal(actual.keys, expected.keys, where + ".keys");
  require_equal(actual.values, expected.values, where + ".values");
}

template <class T>
inline void require_key_permutation(const std::vector<T> &actual_keys,
                                    const std::vector<std::int32_t> &actual_values,
                                    const std::vector<T> &source_keys, const std::string &where) {
  require(actual_keys.size() == source_keys.size(), where + ": key count mismatch");
  require(actual_values.size() == source_keys.size(), where + ": payload count mismatch");
  std::vector<bool> seen(source_keys.size(), false);
  for (std::size_t index = 0; index < actual_values.size(); ++index) {
    const auto payload = actual_values[index];
    require(payload >= 0, where + ": negative payload");
    const auto source = static_cast<std::size_t>(payload);
    require(source < source_keys.size(), where + ": payload out of range");
    require(!seen[source], where + ": duplicate payload " + std::to_string(source));
    seen[source] = true;
    require_equal(actual_keys[index], source_keys[source], where + ": association");
  }
}

inline void require_equal(TwoPositions actual, TwoPositions expected, const std::string &where) {
  require_equal(actual.first, expected.first, where + ".first");
  require_equal(actual.second, expected.second, where + ".second");
}

template <class T> inline std::vector<T> values(std::size_t size, std::size_t salt = 0) {
  std::vector<T> result(size);
  for (std::size_t index = 0; index < size; ++index) {
    const auto value = static_cast<std::int64_t>((index * 17 + salt * 11) % 23) - 11;
    result[index] = static_cast<T>(value);
  }
  return result;
}

template <class T> inline std::vector<T> duplicate_keys(std::size_t size) {
  std::vector<T> result(size);
  for (std::size_t index = 0; index < size; ++index)
    result[index] = static_cast<T>((index * 7 + 3) % 11);
  return result;
}

template <class T> inline std::vector<T> sorted_values(std::size_t size, std::size_t stride) {
  std::vector<T> result(size);
  for (std::size_t index = 0; index < size; ++index)
    result[index] = static_cast<T>((index * stride) / 2);
  return result;
}

template <class T>
inline std::vector<T> reference_merge(const std::vector<T> &left, const std::vector<T> &right) {
  std::vector<T> result;
  result.reserve(left.size() + right.size());
  std::size_t a = 0;
  std::size_t b = 0;
  while (a < left.size() || b < right.size()) {
    if (b == right.size() || (a < left.size() && !(right[b] < left[a])))
      result.push_back(left[a++]);
    else
      result.push_back(right[b++]);
  }
  return result;
}

template <class T>
inline std::vector<T> reference_set_union(const std::vector<T> &left, const std::vector<T> &right) {
  std::vector<T> result;
  std::size_t a = 0;
  std::size_t b = 0;
  while (a < left.size() || b < right.size()) {
    if (a == left.size())
      result.push_back(right[b++]);
    else if (b == right.size())
      result.push_back(left[a++]);
    else if (left[a] < right[b])
      result.push_back(left[a++]);
    else if (right[b] < left[a])
      result.push_back(right[b++]);
    else {
      result.push_back(left[a]);
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T>
inline std::vector<T> reference_set_intersection(const std::vector<T> &left, const std::vector<T> &right) {
  std::vector<T> result;
  std::size_t a = 0;
  std::size_t b = 0;
  while (a < left.size() && b < right.size()) {
    if (left[a] < right[b])
      ++a;
    else if (right[b] < left[a])
      ++b;
    else {
      result.push_back(left[a]);
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T, class Backend> void execute(Backend &backend, Operation operation, std::size_t size) {
  if (!backend.template supports<T>(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for this type/device");

  const auto input = values<T>(size, 1);
  switch (operation) {
  case Operation::copy:
    require_equal(backend.copy(input), input, "copy");
    return;
  case Operation::copy_if: {
    std::vector<T> expected;
    std::copy_if(input.begin(), input.end(), std::back_inserter(expected), IsEven<T>{});
    require_equal(backend.copy_if(input), expected, "copy-if");
    return;
  }
  case Operation::copy_n: {
    const auto count = size / 2;
    require_equal(backend.copy_n(input, count), std::vector<T>(input.begin(), input.begin() + count),
                  "copy-n");
    return;
  }
  case Operation::fill:
    require_equal(backend.template fill<T>(size, T{7}), std::vector<T>(size, T{7}), "fill");
    return;
  case Operation::generate:
    require_equal(backend.template generate<T>(size), std::vector<T>(size, T{7}), "generate");
    return;
  case Operation::for_each: {
    auto expected = input;
    for (auto &value : expected)
      Increment<T>{}(value);
    require_equal(backend.for_each(input), expected, "for-each");
    return;
  }
  case Operation::transform_unary: {
    auto expected = input;
    for (auto &value : expected)
      value = Affine<T>{}(value);
    require_equal(backend.transform_unary(input), expected, "transform-unary");
    return;
  }
  case Operation::transform_binary: {
    const auto right = values<T>(size, 7);
    auto expected = input;
    for (std::size_t index = 0; index < size; ++index)
      expected[index] = Combine<T>{}(input[index], right[index]);
    require_equal(backend.transform_binary(input, right), expected, "transform-binary");
    return;
  }
  case Operation::replace_if:
  case Operation::replace_copy_if: {
    auto expected = input;
    std::replace_if(expected.begin(), expected.end(), IsEven<T>{}, T{9});
    const auto actual = operation == Operation::replace_if ? backend.replace_if(input, T{9})
                                                           : backend.replace_copy_if(input, T{9});
    require_equal(actual, expected, std::string(name(operation)));
    return;
  }
  case Operation::remove_if:
  case Operation::remove_copy_if: {
    auto expected = input;
    expected.erase(std::remove_if(expected.begin(), expected.end(), IsEven<T>{}), expected.end());
    const auto actual =
        operation == Operation::remove_if ? backend.remove_if(input) : backend.remove_copy_if(input);
    require_equal(actual, expected, std::string(name(operation)));
    return;
  }
  case Operation::swap_ranges: {
    const auto right = values<T>(size, 7);
    const auto actual = backend.swap_ranges(input, right);
    require_equal(actual.first, right, "swap-ranges.first");
    require_equal(actual.second, input, "swap-ranges.second");
    return;
  }
  case Operation::reduce: {
    T expected{};
    for (const auto value : input)
      expected = Plus<T>{}(expected, value);
    require_equal(backend.reduce(input), expected, "reduce");
    return;
  }
  case Operation::transform_reduce: {
    T expected{};
    for (const auto value : input)
      expected = Plus<T>{}(expected, Square<T>{}(value));
    require_equal(backend.transform_reduce(input), expected, "transform-reduce");
    return;
  }
  case Operation::inner_product: {
    const auto right = values<T>(size, 7);
    T expected{};
    for (std::size_t index = 0; index < size; ++index)
      expected = Plus<T>{}(expected, static_cast<T>(input[index] * right[index]));
    require_equal(backend.inner_product(input, right), expected, "inner-product");
    return;
  }
  case Operation::inclusive_scan: {
    auto expected = input;
    for (std::size_t index = 1; index < size; ++index)
      expected[index] = Plus<T>{}(expected[index - 1], input[index]);
    require_equal(backend.inclusive_scan(input), expected, "inclusive-scan");
    return;
  }
  case Operation::exclusive_scan: {
    std::vector<T> expected(size);
    T running{};
    for (std::size_t index = 0; index < size; ++index) {
      expected[index] = running;
      running = Plus<T>{}(running, input[index]);
    }
    require_equal(backend.exclusive_scan(input), expected, "exclusive-scan");
    return;
  }
  case Operation::transform_inclusive_scan: {
    std::vector<T> expected(size);
    T running{};
    for (std::size_t index = 0; index < size; ++index) {
      running = Plus<T>{}(running, Square<T>{}(input[index]));
      expected[index] = running;
    }
    require_equal(backend.transform_inclusive_scan(input), expected, "transform-inclusive-scan");
    return;
  }
  case Operation::transform_exclusive_scan: {
    std::vector<T> expected(size);
    T running{};
    for (std::size_t index = 0; index < size; ++index) {
      expected[index] = running;
      running = Plus<T>{}(running, Square<T>{}(input[index]));
    }
    require_equal(backend.transform_exclusive_scan(input), expected, "transform-exclusive-scan");
    return;
  }
  case Operation::sort: {
    auto expected = input;
    std::sort(expected.begin(), expected.end());
    require_equal(backend.sort(input), expected, "sort");
    return;
  }
  case Operation::stable_sort: {
    auto expected = input;
    std::stable_sort(expected.begin(), expected.end());
    require_equal(backend.stable_sort(input), expected, "stable-sort");
    return;
  }
  case Operation::sort_descending: {
    auto expected = input;
    std::sort(expected.begin(), expected.end(), Greater<T>{});
    require_equal(backend.sort_descending(input), expected, "sort-descending");
    return;
  }
  case Operation::sort_by_key: {
    auto keys = values<T>(size, 4);
    for (std::size_t index = 0; index < keys.size(); ++index)
      keys[index] = static_cast<T>(keys[index] * static_cast<T>(size + 1) + static_cast<T>(index));
    std::vector<std::int32_t> payload(size);
    std::iota(payload.begin(), payload.end(), 0);
    const auto actual = backend.sort_by_key(keys, payload);
    require(std::is_sorted(actual.keys.begin(), actual.keys.end()), "sort-by-key: keys are not sorted");
    require_key_permutation(actual.keys, actual.values, keys, "sort-by-key");
    return;
  }
  case Operation::stable_sort_by_key: {
    const auto keys = duplicate_keys<T>(size);
    std::vector<std::int32_t> payload(size);
    std::iota(payload.begin(), payload.end(), 0);
    const auto actual = backend.stable_sort_by_key(keys, payload);
    require(std::is_sorted(actual.keys.begin(), actual.keys.end()),
            "stable-sort-by-key: keys are not sorted");
    require_key_permutation(actual.keys, actual.values, keys, "stable-sort-by-key");
    for (std::size_t index = 0; index < size; ++index) {
      if (index != 0 && actual.keys[index] == actual.keys[index - 1])
        require(actual.values[index - 1] < actual.values[index], "stable-sort-by-key: unstable equal keys");
    }
    return;
  }
  case Operation::unique: {
    auto repeated = duplicate_keys<T>(size);
    std::sort(repeated.begin(), repeated.end());
    auto expected = repeated;
    expected.erase(std::unique(expected.begin(), expected.end()), expected.end());
    require_equal(backend.unique(repeated), expected, "unique");
    return;
  }
  case Operation::unique_copy: {
    auto repeated = duplicate_keys<T>(size);
    std::sort(repeated.begin(), repeated.end());
    auto expected = repeated;
    expected.erase(std::unique(expected.begin(), expected.end()), expected.end());
    require_equal(backend.unique_copy(repeated), expected, "unique-copy");
    return;
  }
  case Operation::count: {
    const T needle = T{1};
    const auto expected = static_cast<std::size_t>(std::count(input.begin(), input.end(), needle));
    require_equal(backend.count(input, needle), expected, "count");
    return;
  }
  case Operation::count_if: {
    std::size_t expected = 0;
    for (const auto value : input)
      if (IsEven<T>{}(value))
        ++expected;
    require_equal(backend.count_if(input), expected, "count-if");
    return;
  }
  case Operation::find_if: {
    std::size_t expected = size;
    for (std::size_t index = 0; index < size; ++index)
      if (IsEven<T>{}(input[index])) {
        expected = index;
        break;
      }
    require_equal(backend.find_if(input), expected, "find-if");
    return;
  }
  case Operation::find: {
    const T needle = T{1};
    const auto expected =
        static_cast<std::size_t>(std::find(input.begin(), input.end(), needle) - input.begin());
    require_equal(backend.find(input, needle), expected, "find");
    return;
  }
  case Operation::all_of:
    require_equal(backend.all_of(input), std::all_of(input.begin(), input.end(), IsEven<T>{}), "all-of");
    return;
  case Operation::any_of:
    require_equal(backend.any_of(input), std::any_of(input.begin(), input.end(), IsEven<T>{}), "any-of");
    return;
  case Operation::none_of:
    require_equal(backend.none_of(input), std::none_of(input.begin(), input.end(), IsEven<T>{}), "none-of");
    return;
  case Operation::equal: {
    auto right = input;
    if (!right.empty())
      right.back() = static_cast<T>(right.back() + T{1});
    require_equal(backend.equal(input, right), input == right, "equal");
    return;
  }
  case Operation::mismatch: {
    auto right = input;
    if (!right.empty())
      right[size / 2] = static_cast<T>(right[size / 2] + T{1});
    const auto expected_pair = std::mismatch(input.begin(), input.end(), right.begin());
    const TwoPositions expected{static_cast<std::size_t>(expected_pair.first - input.begin()),
                                static_cast<std::size_t>(expected_pair.second - right.begin())};
    require_equal(backend.mismatch(input, right), expected, "mismatch");
    return;
  }
  case Operation::lower_bound: {
    auto sorted = input;
    std::sort(sorted.begin(), sorted.end());
    const T needle = T{1};
    std::size_t expected = 0;
    while (expected < sorted.size() && sorted[expected] < needle)
      ++expected;
    require_equal(backend.lower_bound(sorted, needle), expected, "lower-bound");
    return;
  }
  case Operation::upper_bound: {
    auto sorted = input;
    std::sort(sorted.begin(), sorted.end());
    const T needle = T{1};
    const auto expected =
        static_cast<std::size_t>(std::upper_bound(sorted.begin(), sorted.end(), needle) - sorted.begin());
    require_equal(backend.upper_bound(sorted, needle), expected, "upper-bound");
    return;
  }
  case Operation::binary_search: {
    auto sorted = input;
    std::sort(sorted.begin(), sorted.end());
    const T needle = T{1};
    require_equal(backend.binary_search(sorted, needle),
                  std::binary_search(sorted.begin(), sorted.end(), needle), "binary-search");
    return;
  }
  case Operation::equal_range: {
    auto sorted = duplicate_keys<T>(size);
    std::sort(sorted.begin(), sorted.end());
    const T needle = T{3};
    const auto expected_pair = std::equal_range(sorted.begin(), sorted.end(), needle);
    const TwoPositions expected{static_cast<std::size_t>(expected_pair.first - sorted.begin()),
                                static_cast<std::size_t>(expected_pair.second - sorted.begin())};
    require_equal(backend.equal_range(sorted, needle), expected, "equal-range");
    return;
  }
  case Operation::merge: {
    const auto left = sorted_values<T>((size + 1) / 2, 2);
    const auto right = sorted_values<T>(size / 2, 3);
    require_equal(backend.merge(left, right), reference_merge(left, right), "merge");
    return;
  }
  case Operation::partition: {
    const auto actual = backend.partition(input);
    require(actual.boundary <= actual.values.size(), "partition: invalid boundary");
    for (std::size_t index = 0; index < actual.boundary; ++index)
      require(IsEven<T>{}(actual.values[index]), "partition: false element before boundary");
    for (std::size_t index = actual.boundary; index < actual.values.size(); ++index)
      require(!IsEven<T>{}(actual.values[index]), "partition: true element after boundary");
    auto expected_values = input;
    auto actual_values = actual.values;
    std::sort(expected_values.begin(), expected_values.end());
    std::sort(actual_values.begin(), actual_values.end());
    require_equal(actual_values, expected_values, "partition: permutation");
    return;
  }
  case Operation::stable_partition: {
    const auto actual = backend.stable_partition(input);
    std::vector<T> expected = input;
    const auto boundary = std::stable_partition(expected.begin(), expected.end(), IsEven<T>{});
    require_equal(actual.values, expected, "stable-partition.values");
    require_equal(actual.boundary, static_cast<std::size_t>(boundary - expected.begin()),
                  "stable-partition.boundary");
    return;
  }
  case Operation::partition_copy: {
    std::vector<T> selected;
    std::vector<T> rejected;
    for (const auto value : input)
      (IsEven<T>{}(value) ? selected : rejected).push_back(value);
    const auto actual = backend.partition_copy(input);
    require_equal(actual.first, selected, "partition-copy.selected");
    require_equal(actual.second, rejected, "partition-copy.rejected");
    return;
  }
  case Operation::is_partitioned: {
    auto partitioned = input;
    std::partition(partitioned.begin(), partitioned.end(), IsEven<T>{});
    require_equal(backend.is_partitioned(partitioned), true, "is-partitioned");
    return;
  }
  case Operation::is_partitioned_false: {
    if (size < 3)
      throw Unsupported("is-partitioned-false requires at least three values");
    auto not_partitioned = input;
    not_partitioned[0] = T{2};
    not_partitioned[1] = T{1};
    not_partitioned[2] = T{4};
    require_equal(backend.is_partitioned(not_partitioned), false, "is-partitioned-false");
    return;
  }
  case Operation::partition_point: {
    auto partitioned = input;
    const auto point = std::partition(partitioned.begin(), partitioned.end(), IsEven<T>{});
    require_equal(backend.partition_point(partitioned), static_cast<std::size_t>(point - partitioned.begin()),
                  "partition-point");
    return;
  }
  case Operation::adjacent_difference: {
    std::vector<T> expected(size);
    if (!input.empty())
      expected[0] = input[0];
    for (std::size_t index = 1; index < size; ++index)
      expected[index] = static_cast<T>(input[index] - input[index - 1]);
    require_equal(backend.adjacent_difference(input), expected, "adjacent-difference");
    return;
  }
  case Operation::set_union: {
    const auto left = sorted_values<T>((size + 1) / 2, 2);
    const auto right = sorted_values<T>(size / 2, 3);
    require_equal(backend.set_union(left, right), reference_set_union(left, right), "set-union");
    return;
  }
  case Operation::set_intersection: {
    const auto left = sorted_values<T>((size + 1) / 2, 2);
    const auto right = sorted_values<T>(size / 2, 3);
    require_equal(backend.set_intersection(left, right), reference_set_intersection(left, right),
                  "set-intersection");
    return;
  }
  case Operation::set_difference: {
    const auto left = sorted_values<T>((size + 1) / 2, 2);
    const auto right = sorted_values<T>(size / 2, 3);
    std::vector<T> expected(left.size());
    expected.erase(
        std::set_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin()),
        expected.end());
    require_equal(backend.set_difference(left, right), expected, "set-difference");
    return;
  }
  case Operation::set_symmetric_difference: {
    const auto left = sorted_values<T>((size + 1) / 2, 2);
    const auto right = sorted_values<T>(size / 2, 3);
    std::vector<T> expected(left.size() + right.size());
    expected.erase(
        std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin()),
        expected.end());
    require_equal(backend.set_symmetric_difference(left, right), expected, "set-symmetric-difference");
    return;
  }
  case Operation::reverse: {
    auto expected = input;
    std::reverse(expected.begin(), expected.end());
    require_equal(backend.reverse(input), expected, "reverse");
    return;
  }
  case Operation::reverse_copy: {
    auto expected = input;
    std::reverse(expected.begin(), expected.end());
    require_equal(backend.reverse_copy(input), expected, "reverse-copy");
    return;
  }
  case Operation::min_element: {
    auto extrema = input;
    if (!extrema.empty())
      extrema[size / 3] = T{-100};
    const auto expected =
        extrema.empty()
            ? size
            : static_cast<std::size_t>(std::min_element(extrema.begin(), extrema.end()) - extrema.begin());
    require_equal(backend.min_element(extrema), expected, "min-element");
    return;
  }
  case Operation::max_element: {
    auto extrema = input;
    if (!extrema.empty())
      extrema[(size * 2) / 3] = T{100};
    const auto expected =
        extrema.empty()
            ? size
            : static_cast<std::size_t>(std::max_element(extrema.begin(), extrema.end()) - extrema.begin());
    require_equal(backend.max_element(extrema), expected, "max-element");
    return;
  }
  case Operation::minmax_element: {
    auto extrema = input;
    if (extrema.size() == 1)
      extrema[0] = T{0};
    else if (!extrema.empty()) {
      extrema[size / 3] = T{-100};
      extrema[(size * 2) / 3] = T{100};
    }
    const auto pair = std::minmax_element(extrema.begin(), extrema.end());
    const TwoPositions expected{static_cast<std::size_t>(pair.first - extrema.begin()),
                                static_cast<std::size_t>(pair.second - extrema.begin())};
    require_equal(backend.minmax_element(extrema), expected, "minmax-element");
    return;
  }
  case Operation::is_sorted: {
    auto sorted = input;
    std::sort(sorted.begin(), sorted.end());
    require_equal(backend.is_sorted(sorted), true, "is-sorted");
    return;
  }
  case Operation::is_sorted_false: {
    if (size < 2)
      throw Unsupported("is-sorted-false requires at least two values");
    auto not_sorted = input;
    not_sorted[0] = T{1};
    not_sorted[1] = T{0};
    require_equal(backend.is_sorted(not_sorted), false, "is-sorted-false");
    return;
  }
  case Operation::is_sorted_until: {
    auto nearly = input;
    std::sort(nearly.begin(), nearly.end());
    if (nearly.size() > 2)
      std::swap(nearly[nearly.size() / 2], nearly.back());
    const auto expected =
        static_cast<std::size_t>(std::is_sorted_until(nearly.begin(), nearly.end()) - nearly.begin());
    require_equal(backend.is_sorted_until(nearly), expected, "is-sorted-until");
    return;
  }
  case Operation::rotate:
  case Operation::rotate_copy: {
    const auto middle = size / 3;
    auto expected = input;
    std::rotate(expected.begin(), expected.begin() + static_cast<std::ptrdiff_t>(middle), expected.end());
    const auto actual =
        operation == Operation::rotate ? backend.rotate(input, middle) : backend.rotate_copy(input, middle);
    require_equal(actual, expected, std::string(name(operation)));
    return;
  }
  case Operation::includes: {
    auto left = input;
    std::sort(left.begin(), left.end());
    std::vector<T> right;
    for (std::size_t index = 0; index < left.size(); index += 3)
      right.push_back(left[index]);
    require_equal(backend.includes(left, right), true, "includes");
    return;
  }
  case Operation::includes_false: {
    auto left = input;
    std::sort(left.begin(), left.end());
    const std::vector<T> right{T{100}};
    require_equal(backend.includes(left, right), false, "includes-false");
    return;
  }
  case Operation::gather:
  case Operation::scatter: {
    std::vector<std::size_t> indices(size);
    for (std::size_t index = 0; index < size; ++index)
      indices[index] = size - index - 1;
    auto expected = input;
    std::reverse(expected.begin(), expected.end());
    const auto actual =
        operation == Operation::gather ? backend.gather(input, indices) : backend.scatter(input, indices);
    require_equal(actual, expected, std::string(name(operation)));
    return;
  }
  case Operation::reduce_by_key:
  case Operation::inclusive_scan_by_key:
  case Operation::exclusive_scan_by_key:
  case Operation::unique_by_key: {
    std::vector<T> keys(size);
    std::vector<std::int32_t> values(size);
    for (std::size_t index = 0; index < size; ++index) {
      keys[index] = static_cast<T>(index / 3);
      values[index] = static_cast<std::int32_t>(index % 4 + 1);
    }
    KeyValues<T> expected;
    if (operation == Operation::reduce_by_key) {
      for (std::size_t index = 0; index < size; ++index) {
        if (expected.keys.empty() || expected.keys.back() != keys[index]) {
          expected.keys.push_back(keys[index]);
          expected.values.push_back(values[index]);
        } else {
          expected.values.back() += values[index];
        }
      }
    } else if (operation == Operation::inclusive_scan_by_key) {
      expected = {keys, values};
      for (std::size_t index = 1; index < size; ++index)
        if (keys[index] == keys[index - 1])
          expected.values[index] += expected.values[index - 1];
    } else if (operation == Operation::exclusive_scan_by_key) {
      expected = {keys, std::vector<std::int32_t>(size)};
      std::int32_t running = 0;
      for (std::size_t index = 0; index < size; ++index) {
        if (index == 0 || keys[index] != keys[index - 1])
          running = 0;
        expected.values[index] = running;
        running += values[index];
      }
    } else {
      for (std::size_t index = 0; index < size; ++index)
        if (index == 0 || keys[index] != keys[index - 1]) {
          expected.keys.push_back(keys[index]);
          expected.values.push_back(values[index]);
        }
    }
    KeyValues<T> actual;
    if (operation == Operation::reduce_by_key)
      actual = backend.reduce_by_key(keys, values);
    else if (operation == Operation::inclusive_scan_by_key)
      actual = backend.inclusive_scan_by_key(keys, values);
    else if (operation == Operation::exclusive_scan_by_key)
      actual = backend.exclusive_scan_by_key(keys, values);
    else
      actual = backend.unique_by_key(keys, values);
    require_equal(actual, expected, std::string(name(operation)));
    return;
  }
  case Operation::merge_by_key: {
    KeyValues<T> left{sorted_values<T>((size + 1) / 2, 2), {}};
    KeyValues<T> right{sorted_values<T>(size / 2, 3), {}};
    left.values.resize(left.keys.size());
    right.values.resize(right.keys.size());
    for (std::size_t index = 0; index < left.values.size(); ++index)
      left.values[index] = static_cast<std::int32_t>(index + 100);
    for (std::size_t index = 0; index < right.values.size(); ++index)
      right.values[index] = static_cast<std::int32_t>(index + 200);
    KeyValues<T> expected;
    std::size_t a = 0;
    std::size_t b = 0;
    while (a < left.keys.size() || b < right.keys.size()) {
      const bool take_left =
          b == right.keys.size() || (a < left.keys.size() && !(right.keys[b] < left.keys[a]));
      if (take_left) {
        expected.keys.push_back(left.keys[a]);
        expected.values.push_back(left.values[a++]);
      } else {
        expected.keys.push_back(right.keys[b]);
        expected.values.push_back(right.values[b++]);
      }
    }
    require_equal(backend.merge_by_key(left, right), expected, "merge-by-key");
    return;
  }
  case Operation::partial_sort: {
    const auto middle = size / 2;
    const auto actual = backend.partial_sort(input, middle);
    auto fully_sorted = input;
    std::sort(fully_sorted.begin(), fully_sorted.end());
    require(std::equal(actual.begin(), actual.begin() + static_cast<std::ptrdiff_t>(middle),
                       fully_sorted.begin()),
            "partial-sort: prefix mismatch");
    auto permutation = actual;
    std::sort(permutation.begin(), permutation.end());
    require_equal(permutation, fully_sorted, "partial-sort: permutation");
    return;
  }
  case Operation::nth_element: {
    const auto nth = size / 2;
    const auto actual = backend.nth_element(input, nth);
    auto fully_sorted = input;
    std::sort(fully_sorted.begin(), fully_sorted.end());
    if (!actual.empty())
      require_equal(actual[nth], fully_sorted[nth], "nth-element.value");
    auto permutation = actual;
    std::sort(permutation.begin(), permutation.end());
    require_equal(permutation, fully_sorted, "nth-element: permutation");
    return;
  }
  case Operation::inplace_merge: {
    const auto middle = size / 2;
    auto halves = input;
    std::sort(halves.begin(), halves.begin() + static_cast<std::ptrdiff_t>(middle));
    std::sort(halves.begin() + static_cast<std::ptrdiff_t>(middle), halves.end());
    auto expected = halves;
    std::inplace_merge(expected.begin(), expected.begin() + static_cast<std::ptrdiff_t>(middle),
                       expected.end());
    require_equal(backend.inplace_merge(halves, middle), expected, "inplace-merge");
    return;
  }
  }
}

template <class Backend, ScalarType Kind> Case make_case(Operation operation, std::size_t size) {
  using T = typename Native<Kind>::type;
  return {"algorithms/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/n" +
              std::to_string(size),
          [operation, size] {
            return capture([&] {
              Backend backend;
              execute<T>(backend, operation, size);
            });
          }};
}

template <class Backend, ScalarType Kind>
void append_type_cases(std::vector<Case> &result, const Configuration &configuration) {
  for (const auto operation : operations) {
    for (const auto size : configuration.algorithm_sizes)
      result.push_back(make_case<Backend, Kind>(operation, size));
  }
}

inline void append_f16_cases(std::vector<Case> &result, const Configuration &configuration) {
  for (const auto operation : operations) {
    for (const auto size : configuration.algorithm_sizes)
      result.push_back({"algorithms/" + std::string(name(operation)) + "/f16/n" + std::to_string(size),
                        [] { return Result::skip("f16 algorithm scenarios are not instantiated"); }});
  }
}

} // namespace detail

template <class Backend, ScalarType Kind>
  requires(Kind != ScalarType::f16)
std::vector<Case> cases_for(const Configuration &configuration) {
  std::vector<Case> result;
  if (std::find(configuration.algorithm_types.begin(), configuration.algorithm_types.end(), Kind) !=
      configuration.algorithm_types.end())
    detail::append_type_cases<Backend, Kind>(result, configuration);
  return result;
}

inline std::vector<Case> f16_cases(const Configuration &configuration) {
  std::vector<Case> result;
  if (std::find(configuration.algorithm_types.begin(), configuration.algorithm_types.end(),
                ScalarType::f16) != configuration.algorithm_types.end())
    detail::append_f16_cases(result, configuration);
  return result;
}

template <class Backend> std::vector<Case> cases(const Configuration &configuration) {
  std::vector<Case> result;
  for (const auto type : configuration.algorithm_types) {
    switch (type) {
    case ScalarType::i8:
      detail::append_type_cases<Backend, ScalarType::i8>(result, configuration);
      break;
    case ScalarType::i32:
      detail::append_type_cases<Backend, ScalarType::i32>(result, configuration);
      break;
    case ScalarType::i64:
      detail::append_type_cases<Backend, ScalarType::i64>(result, configuration);
      break;
    case ScalarType::f16:
      detail::append_f16_cases(result, configuration);
      break;
    case ScalarType::f32:
      detail::append_type_cases<Backend, ScalarType::f32>(result, configuration);
      break;
    case ScalarType::f64:
      detail::append_type_cases<Backend, ScalarType::f64>(result, configuration);
      break;
    }
  }
  return result;
}

} // namespace unified_suite::algorithms

#undef UNIFIED_SUITE_HOST_DEVICE

#endif
