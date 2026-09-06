#ifndef UNIFIED_SUITE_ROCM_RED_CASES_HPP
#define UNIFIED_SUITE_ROCM_RED_CASES_HPP

// Focused rocThrust semantic probes derived from the historical gfx1036 red
// inventory.  These are kept separate from the portable algorithm catalog:
// the operations below are rocThrust overload/iterator forms, rather than
// another spelling of the already-covered C++ algorithm semantics.

#include "unified_suite/algorithms.hpp"
#include "unified_suite/config.hpp"
#include "unified_suite/result.hpp"

#include <thrust/binary_search.h>
#include <thrust/copy.h>
#include <thrust/detail/tuple_algorithms.h>
#include <thrust/device_vector.h>
#include <thrust/gather.h>
#include <thrust/iterator/permutation_iterator.h>
#include <thrust/iterator/transform_input_output_iterator.h>
#include <thrust/iterator/transform_output_iterator.h>
#include <thrust/iterator/zip_iterator.h>
#include <thrust/iterator/transform_iterator.h>
#include <thrust/pair.h>
#include <thrust/sequence.h>
#include <thrust/set_operations.h>
#include <thrust/sort.h>
#include <thrust/transform.h>
#include <thrust/tuple.h>

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <numeric>
#include <sstream>
#include <stdexcept>
#include <string>
#include <tuple>
#include <utility>
#include <vector>

#if defined(__CUDACC__) || defined(__HIPCC__)
#define UNIFIED_SUITE_ROCM_RED_HOST_DEVICE __host__ __device__
#else
#define UNIFIED_SUITE_ROCM_RED_HOST_DEVICE
#endif

namespace unified_suite::rocm_red_cases {

namespace detail {

template <class T> using Pair = thrust::pair<T, std::int32_t>;

template <class T> struct PairLess {
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE bool operator()(const Pair<T> &left, const Pair<T> &right) const {
    return left.first < right.first || (!(right.first < left.first) && left.second < right.second);
  }
};

// rocThrust's key/value set-operation overloads compare keys only.  The value
// iterator supplies payloads associated with the selected key; it is not part
// of set membership or equivalence.
template <class T> struct PairKeyLess {
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE bool operator()(const Pair<T> &left, const Pair<T> &right) const {
    return left.first < right.first;
  }
};

template <class T> struct TupleLess {
  template <class LeftTuple, class RightTuple>
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE bool operator()(const LeftTuple &left, const RightTuple &right) const {
    const auto left_key = thrust::get<0>(left);
    const auto right_key = thrust::get<0>(right);
    const auto left_value = thrust::get<1>(left);
    const auto right_value = thrust::get<1>(right);
    return left_key < right_key || (!(right_key < left_key) && left_value < right_value);
  }
};

template <class T> struct TupleCode {
  template <class Tuple>
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE std::int64_t operator()(const Tuple &value) const {
    return static_cast<std::int64_t>(thrust::get<0>(value)) * 1000 +
           static_cast<std::int64_t>(thrust::get<1>(value));
  }
};

template <class T> struct PairPlus {
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE Pair<T> operator()(const Pair<T> &left, const Pair<T> &right) const {
    return {left.first + right.first, left.second + right.second};
  }
};

template <class T> struct TuplePlus {
  template <class LeftTuple, class RightTuple>
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE auto operator()(const LeftTuple &left, const RightTuple &right) const {
    return thrust::make_tuple(thrust::get<0>(left) + thrust::get<0>(right),
                              thrust::get<1>(left) + thrust::get<1>(right));
  }
};

template <class T> struct Square {
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE T operator()(T value) const { return value * value; }
};

template <class T> struct Negate {
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE T operator()(T value) const { return -value; }
};

enum class SetKind { union_set, intersection, difference, symmetric_difference };

template <class T> struct KeyValues {
  std::vector<T> keys;
  std::vector<std::int32_t> values;
};

template <class T> inline void require(bool condition, const std::string &message) {
  if (!condition)
    throw std::runtime_error(message);
}

template <class T>
inline void require_equal(const std::vector<T> &actual, const std::vector<T> &expected,
                          const std::string &where) {
  require<T>(actual.size() == expected.size(),
             where + ": result size mismatch (actual=" + std::to_string(actual.size()) +
                 ", expected=" + std::to_string(expected.size()) + ")");
  for (std::size_t index = 0; index < expected.size(); ++index)
    require<T>(actual[index] == expected[index], where + "[" + std::to_string(index) + "] mismatch");
}

template <class T>
inline void require_equal(const KeyValues<T> &actual, const KeyValues<T> &expected,
                          const std::string &where) {
  require_equal(actual.keys, expected.keys, where + ".keys");
  require_equal(actual.values, expected.values, where + ".values");
}

template <class T>
inline void require_pair_equal(const std::vector<Pair<T>> &actual, const std::vector<Pair<T>> &expected,
                               const std::string &where) {
  require<T>(actual.size() == expected.size(),
             where + ": result size mismatch (actual=" + std::to_string(actual.size()) +
                 ", expected=" + std::to_string(expected.size()) + ")");
  for (std::size_t index = 0; index < expected.size(); ++index) {
    require<T>(actual[index].first == expected[index].first,
               where + ".key[" + std::to_string(index) + "] mismatch");
    require<T>(actual[index].second == expected[index].second,
               where + ".value[" + std::to_string(index) + "] mismatch");
  }
}

template <class T> inline std::vector<T> ascending_keys() {
  return {T{-5}, T{-5}, T{-3}, T{-1}, T{-1}, T{0}, T{2}, T{2}, T{2}, T{4}, T{7}};
}

template <class T> inline std::vector<T> descending_keys() {
  auto result = ascending_keys<T>();
  std::reverse(result.begin(), result.end());
  return result;
}

inline std::vector<std::int32_t> payload(std::size_t count, std::int32_t base) {
  std::vector<std::int32_t> result(count);
  for (std::size_t index = 0; index < count; ++index)
    result[index] = base + static_cast<std::int32_t>(index);
  return result;
}

template <class T> inline KeyValues<T> descending_key_values(const std::vector<T> &keys, std::int32_t base) {
  return {keys, payload(keys.size(), base)};
}

template <class T>
inline KeyValues<T> reference_set_by_key(const KeyValues<T> &left, const KeyValues<T> &right, SetKind kind,
                                         bool descending) {
  KeyValues<T> result;
  const auto less = [descending](const T &a, const T &b) { return descending ? b < a : a < b; };
  std::size_t a = 0;
  std::size_t b = 0;
  while (a < left.keys.size() || b < right.keys.size()) {
    if (a == left.keys.size()) {
      if (kind == SetKind::union_set || kind == SetKind::symmetric_difference) {
        result.keys.push_back(right.keys[b]);
        result.values.push_back(right.values[b]);
      }
      ++b;
    } else if (b == right.keys.size()) {
      if (kind != SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
    } else if (less(left.keys[a], right.keys[b])) {
      if (kind != SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
    } else if (less(right.keys[b], left.keys[a])) {
      if (kind == SetKind::union_set || kind == SetKind::symmetric_difference) {
        result.keys.push_back(right.keys[b]);
        result.values.push_back(right.values[b]);
      }
      ++b;
    } else {
      if (kind == SetKind::union_set || kind == SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T>
inline KeyValues<T> thrust_set_by_key(const KeyValues<T> &left, const KeyValues<T> &right, SetKind kind,
                                      bool descending) {
  const thrust::device_vector<T> left_keys(left.keys.begin(), left.keys.end());
  const thrust::device_vector<T> right_keys(right.keys.begin(), right.keys.end());
  const thrust::device_vector<std::int32_t> left_values(left.values.begin(), left.values.end());
  const thrust::device_vector<std::int32_t> right_values(right.values.begin(), right.values.end());
  thrust::device_vector<T> output_keys(left.keys.size() + right.keys.size());
  thrust::device_vector<std::int32_t> output_values(left.values.size() + right.values.size());

  thrust::pair<typename thrust::device_vector<T>::iterator,
               typename thrust::device_vector<std::int32_t>::iterator>
      ends;
  if (kind == SetKind::union_set) {
    ends = descending ? thrust::set_union_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(),
                                                 right_keys.end(), left_values.begin(), right_values.begin(),
                                                 output_keys.begin(), output_values.begin(),
                                                 algorithms::Greater<T>{})
                      : thrust::set_union_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(),
                                                 right_keys.end(), left_values.begin(), right_values.begin(),
                                                 output_keys.begin(), output_values.begin());
  } else if (kind == SetKind::intersection) {
    ends = descending
               ? thrust::set_intersection_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(),
                                                 right_keys.end(), left_values.begin(), output_keys.begin(),
                                                 output_values.begin(), algorithms::Greater<T>{})
               : thrust::set_intersection_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(),
                                                 right_keys.end(), left_values.begin(), output_keys.begin(),
                                                 output_values.begin());
  } else if (kind == SetKind::difference) {
    ends = descending
               ? thrust::set_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(),
                                               right_keys.end(), left_values.begin(), right_values.begin(),
                                               output_keys.begin(), output_values.begin(),
                                               algorithms::Greater<T>{})
               : thrust::set_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(),
                                               right_keys.end(), left_values.begin(), right_values.begin(),
                                               output_keys.begin(), output_values.begin());
  } else {
    ends = descending ? thrust::set_symmetric_difference_by_key(
                            left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                            left_values.begin(), right_values.begin(), output_keys.begin(),
                            output_values.begin(), algorithms::Greater<T>{})
                      : thrust::set_symmetric_difference_by_key(left_keys.begin(), left_keys.end(),
                                                                right_keys.begin(), right_keys.end(),
                                                                left_values.begin(), right_values.begin(),
                                                                output_keys.begin(), output_values.begin());
  }
  output_keys.resize(static_cast<std::size_t>(ends.first - output_keys.begin()));
  output_values.resize(static_cast<std::size_t>(ends.second - output_values.begin()));
  return {std::vector<T>(output_keys.begin(), output_keys.end()),
          std::vector<std::int32_t>(output_values.begin(), output_values.end())};
}

template <class T> inline std::vector<Pair<T>> pair_values(const std::vector<T> &keys, std::int32_t base) {
  std::vector<Pair<T>> result;
  const auto values = payload(keys.size(), base);
  result.reserve(keys.size());
  for (std::size_t index = 0; index < keys.size(); ++index)
    result.emplace_back(keys[index], values[index]);
  return result;
}

template <class T>
inline std::vector<Pair<T>> host_pairs(const thrust::device_vector<Pair<T>> &values, std::size_t count) {
  return {values.begin(), values.begin() + static_cast<std::ptrdiff_t>(count)};
}

template <class T>
inline std::vector<Pair<T>> thrust_set_pairs(const std::vector<Pair<T>> &left,
                                             const std::vector<Pair<T>> &right, SetKind kind) {
  const thrust::device_vector<Pair<T>> a(left.begin(), left.end());
  const thrust::device_vector<Pair<T>> b(right.begin(), right.end());
  thrust::device_vector<Pair<T>> output(left.size() + right.size());
  auto end = output.begin();
  if (kind == SetKind::union_set)
    end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::intersection)
    end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::difference)
    end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else
    end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(),
                                           PairKeyLess<T>{});
  return host_pairs(output, static_cast<std::size_t>(end - output.begin()));
}

template <class T>
inline std::vector<Pair<T>> pair_expected(std::vector<Pair<T>> left, std::vector<Pair<T>> right,
                                          SetKind kind) {
  std::stable_sort(left.begin(), left.end(), PairKeyLess<T>{});
  std::stable_sort(right.begin(), right.end(), PairKeyLess<T>{});
  std::vector<Pair<T>> result(left.size() + right.size());
  auto end = result.begin();
  if (kind == SetKind::union_set)
    end = std::set_union(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::intersection)
    end = std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), result.begin(),
                                PairKeyLess<T>{});
  else if (kind == SetKind::difference)
    end = std::set_difference(left.begin(), left.end(), right.begin(), right.end(), result.begin(),
                              PairKeyLess<T>{});
  else
    end = std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), result.begin(),
                                        PairKeyLess<T>{});
  result.erase(end, result.end());
  return result;
}

template <class T> inline std::string set_name(SetKind kind) {
  switch (kind) {
  case SetKind::union_set:
    return "union";
  case SetKind::intersection:
    return "intersection";
  case SetKind::difference:
    return "difference";
  case SetKind::symmetric_difference:
    return "symmetric-difference";
  }
  return "unknown";
}

template <class T> inline void check_set_by_key(SetKind kind, bool descending) {
  const auto base = descending ? descending_keys<T>() : ascending_keys<T>();
  auto left = descending_key_values(base, 100);
  auto right = descending_key_values(base, 200);
  // Offset the second range while retaining sortedness.  This gives every
  // operation unmatched keys and runs with multiplicity greater than one.
  if (descending) {
    right.keys = {T{7}, T{7}, T{4}, T{3}, T{2}, T{2}, T{1}, T{-1}, T{-1}, T{-4}, T{-5}, T{-5}};
    right.values = payload(right.keys.size(), 200);
  } else {
    right.keys = {T{-5}, T{-4}, T{-1}, T{-1}, T{1}, T{2}, T{2}, T{3}, T{4}, T{4}, T{7}, T{7}};
    right.values = payload(right.keys.size(), 200);
  }
  const auto expected = reference_set_by_key(left, right, kind, descending);
  require_equal(thrust_set_by_key(left, right, kind, descending), expected,
                "set-" + set_name<T>(kind) + (descending ? "-by-key-descending" : "-by-key"));
}

template <class T> inline void check_set_descending(SetKind kind) {
  const std::vector<T> left{T{7}, T{7}, T{4}, T{2}, T{2}, T{0}, T{-1}, T{-1}};
  const std::vector<T> right{T{7}, T{3}, T{3}, T{2}, T{0}, T{-2}};
  const auto less = algorithms::Greater<T>{};
  std::vector<T> expected(left.size() + right.size());
  auto expected_end = expected.begin();
  if (kind == SetKind::union_set)
    expected_end = std::set_union(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else if (kind == SetKind::intersection)
    expected_end = std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else if (kind == SetKind::difference)
    expected_end = std::set_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else
    expected_end = std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(),
                                                 expected.begin(), less);
  expected.erase(expected_end, expected.end());

  const thrust::device_vector<T> a(left.begin(), left.end());
  const thrust::device_vector<T> b(right.begin(), right.end());
  thrust::device_vector<T> output(left.size() + right.size());
  auto end = output.begin();
  if (kind == SetKind::union_set)
    end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else if (kind == SetKind::intersection)
    end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else if (kind == SetKind::difference)
    end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else
    end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  output.resize(static_cast<std::size_t>(end - output.begin()));
  require_equal(std::vector<T>(output.begin(), output.end()), expected,
                "set-" + set_name<T>(kind) + "-descending");
}

template <class T> inline void check_pair_set(SetKind kind) {
  const auto left = pair_values(ascending_keys<T>(), 100);
  auto right = pair_values(ascending_keys<T>(), 200);
  right.erase(right.begin() + 1);
  right.emplace_back(T{-4}, 201);
  std::stable_sort(right.begin(), right.end(), PairKeyLess<T>{});
  const auto expected = pair_expected(left, right, kind);
  const auto actual = thrust_set_pairs(left, right, kind);
  require_pair_equal(actual, expected, "set-" + set_name<T>(kind) + "-key-value");
}

template <class T> inline void check_duplicate_heavy_sets() {
  const std::vector<T> left{T{0}, T{0}, T{0}, T{0}, T{1}, T{1}, T{3}, T{3}, T{3}, T{3}, T{3}};
  const std::vector<T> right{T{0}, T{0}, T{1}, T{1}, T{1}, T{2}, T{3}, T{3}, T{3}, T{4}};
  const std::vector<T> expected_union{T{0}, T{0}, T{0}, T{0}, T{1}, T{1}, T{1},
                                      T{2}, T{3}, T{3}, T{3}, T{3}, T{3}, T{4}};
  const std::vector<T> expected_intersection{T{0}, T{0}, T{1}, T{1}, T{3}, T{3}, T{3}};
  const std::vector<T> expected_difference{T{0}, T{0}, T{3}, T{3}};
  const std::vector<T> expected_symmetric{T{0}, T{0}, T{1}, T{2}, T{3}, T{3}, T{4}};
  const auto run = [&](SetKind kind, const std::vector<T> &expected) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size() + right.size());
    auto end = output.begin();
    if (kind == SetKind::union_set)
      end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else if (kind == SetKind::intersection)
      end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else if (kind == SetKind::difference)
      end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else
      end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<std::size_t>(end - output.begin()));
    require_equal(std::vector<T>(output.begin(), output.end()), expected,
                  "set-" + set_name<T>(kind) + "-duplicate-heavy");
  };
  run(SetKind::union_set, expected_union);
  run(SetKind::intersection, expected_intersection);
  run(SetKind::difference, expected_difference);
  run(SetKind::symmetric_difference, expected_symmetric);
}

template <class T> inline void check_binary_search_descending() {
  const auto sorted = descending_keys<T>();
  const thrust::device_vector<T> values(sorted.begin(), sorted.end());
  require<T>(thrust::binary_search(values.begin(), values.end(), T{2}, algorithms::Greater<T>{}),
             "binary-search-descending: present value not found");
  require<T>(!thrust::binary_search(values.begin(), values.end(), T{6}, algorithms::Greater<T>{}),
             "binary-search-descending: absent value found");
}

template <class T> inline void check_binary_search_vector(bool descending) {
  auto sorted = ascending_keys<T>();
  if (descending)
    std::reverse(sorted.begin(), sorted.end());
  const std::vector<T> needles{T{-6}, T{-5}, T{-2}, T{-1}, T{2}, T{6}, T{7}};
  const thrust::device_vector<T> values(sorted.begin(), sorted.end());
  const thrust::device_vector<T> queries(needles.begin(), needles.end());
  thrust::device_vector<int> output(needles.size());
  if (descending)
    thrust::binary_search(values.begin(), values.end(), queries.begin(), queries.end(), output.begin(),
                          algorithms::Greater<T>{});
  else
    thrust::binary_search(values.begin(), values.end(), queries.begin(), queries.end(), output.begin());
  std::vector<int> expected;
  expected.reserve(needles.size());
  for (const auto needle : needles) {
    const bool found =
        descending ? std::binary_search(sorted.begin(), sorted.end(), needle, algorithms::Greater<T>{})
                   : std::binary_search(sorted.begin(), sorted.end(), needle, algorithms::Less<T>{});
    expected.push_back(found ? 1 : 0);
  }
  require_equal(std::vector<int>(output.begin(), output.end()), expected,
                descending ? "binary-search-vector-descending" : "binary-search-vector");
}

template <class T> inline void check_gather_repeated() {
  const std::vector<T> input{T{11}, T{22}, T{33}, T{44}, T{55}, T{66}, T{77}, T{88}};
  const std::vector<std::size_t> indices{7, 2, 2, 0, 6, 3, 7, 1};
  const thrust::device_vector<T> source(input.begin(), input.end());
  const thrust::device_vector<std::size_t> map(indices.begin(), indices.end());
  thrust::device_vector<T> output(indices.size());
  thrust::gather(map.begin(), map.end(), source.begin(), output.begin());
  std::vector<T> expected;
  for (const auto index : indices)
    expected.push_back(input[index]);
  require_equal(std::vector<T>(output.begin(), output.end()), expected, "gather-repeated");
}

template <class T> inline void check_permutation_iterator() {
  const std::vector<T> input{T{13}, T{21}, T{34}, T{55}, T{89}, T{144}};
  const std::vector<std::size_t> indices{4, 1, 5, 1, 3, 0, 2};
  const thrust::device_vector<T> source(input.begin(), input.end());
  const thrust::device_vector<std::size_t> map(indices.begin(), indices.end());
  const auto first = thrust::make_permutation_iterator(source.begin(), map.begin());
  thrust::device_vector<T> output(indices.size());
  thrust::copy(first, first + static_cast<std::ptrdiff_t>(indices.size()), output.begin());
  std::vector<T> expected;
  for (const auto index : indices)
    expected.push_back(input[index]);
  require_equal(std::vector<T>(output.begin(), output.end()), expected, "permutation-iterator");
}

template <class T> inline void check_zip_iterator() {
  const std::vector<T> keys{T{-3}, T{0}, T{4}, T{9}};
  const std::vector<std::int32_t> values{7, 2, 8, 1};
  const thrust::device_vector<T> device_keys(keys.begin(), keys.end());
  const thrust::device_vector<std::int32_t> device_values(values.begin(), values.end());
  const auto first =
      thrust::make_zip_iterator(thrust::make_tuple(device_keys.begin(), device_values.begin()));
  const auto last = first + static_cast<std::ptrdiff_t>(keys.size());
  thrust::device_vector<std::int64_t> output(keys.size());
  thrust::transform(first, last, output.begin(), TupleCode<T>{});
  std::vector<std::int64_t> expected;
  for (std::size_t index = 0; index < keys.size(); ++index)
    expected.push_back(static_cast<std::int64_t>(keys[index]) * 1000 + values[index]);
  require_equal(std::vector<std::int64_t>(output.begin(), output.end()), expected, "zip-iterator");
}

template <class T> inline void check_zip_sort() {
  std::vector<T> keys{T{3}, T{-1}, T{3}, T{0}, T{-1}};
  std::vector<std::int32_t> values{30, 11, 31, 2, 10};
  thrust::device_vector<T> device_keys(keys.begin(), keys.end());
  thrust::device_vector<std::int32_t> device_values(values.begin(), values.end());
  const auto first =
      thrust::make_zip_iterator(thrust::make_tuple(device_keys.begin(), device_values.begin()));
  thrust::sort(first, first + static_cast<std::ptrdiff_t>(keys.size()), TupleLess<T>{});
  std::vector<Pair<T>> actual;
  for (std::size_t index = 0; index < keys.size(); ++index)
    actual.emplace_back(device_keys[index], device_values[index]);
  std::vector<Pair<T>> expected;
  for (std::size_t index = 0; index < keys.size(); ++index)
    expected.emplace_back(keys[index], values[index]);
  std::sort(expected.begin(), expected.end(), PairLess<T>{});
  require_pair_equal(actual, expected, "zip-iterator-sort");
}

template <class T> struct TupleSquare {
  UNIFIED_SUITE_ROCM_RED_HOST_DEVICE T operator()(T value) const { return value * value; }
};

template <class T> inline void check_tuple_transform() {
  const auto input = std::make_tuple(T{-3}, T{2}, T{5});
  const auto actual = thrust::tuple_transform(input, TupleSquare<T>{});
  require<T>(std::get<0>(actual) == T{9}, "tuple-transform[0] mismatch");
  require<T>(std::get<1>(actual) == T{4}, "tuple-transform[1] mismatch");
  require<T>(std::get<2>(actual) == T{25}, "tuple-transform[2] mismatch");
}

template <class T> inline void check_tuple_sort() {
  using Tuple = thrust::tuple<T, std::int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(T{3}, 30), thrust::make_tuple(T{-1}, 11),
                                 thrust::make_tuple(T{3}, 31), thrust::make_tuple(T{0}, 2),
                                 thrust::make_tuple(T{-1}, 10)};
  thrust::device_vector<Tuple> values(input.begin(), input.end());
  thrust::sort(values.begin(), values.end(), TupleLess<T>{});
  const std::vector<Tuple> actual(values.begin(), values.end());
  const std::vector<Tuple> expected{thrust::make_tuple(T{-1}, 10), thrust::make_tuple(T{-1}, 11),
                                    thrust::make_tuple(T{0}, 2), thrust::make_tuple(T{3}, 30),
                                    thrust::make_tuple(T{3}, 31)};
  require<T>(actual.size() == expected.size(), "tuple-sort: result size mismatch");
  for (std::size_t index = 0; index < expected.size(); ++index) {
    require<T>(thrust::get<0>(actual[index]) == thrust::get<0>(expected[index]),
               "tuple-sort.key[" + std::to_string(index) + "] mismatch");
    require<T>(thrust::get<1>(actual[index]) == thrust::get<1>(expected[index]),
               "tuple-sort.value[" + std::to_string(index) + "] mismatch");
  }
}

template <class T> inline void check_pair_sort() {
  auto expected = pair_values(ascending_keys<T>(), 300);
  std::reverse(expected.begin(), expected.end());
  const auto original = expected;
  thrust::device_vector<Pair<T>> values(original.begin(), original.end());
  thrust::sort(values.begin(), values.end(), PairLess<T>{});
  std::sort(expected.begin(), expected.end(), PairLess<T>{});
  require_pair_equal(host_pairs(values, values.size()), expected, "pair-sort");
}

template <class T> inline void check_pair_sort_by_key() {
  // Keep keys unique here so the plain (unstable) sort-by-key overload has a
  // deterministic payload association to validate.
  const std::vector<T> keys{T{9}, T{1}, T{8}, T{2}, T{7}, T{3}, T{6}, T{4}, T{5}, T{0}, T{-1}};
  const auto values = payload(keys.size(), 400);
  thrust::device_vector<T> device_keys(keys.begin(), keys.end());
  thrust::device_vector<std::int32_t> device_values(values.begin(), values.end());
  thrust::sort_by_key(device_keys.begin(), device_keys.end(), device_values.begin(), algorithms::Less<T>{});
  const std::vector<T> actual_keys(device_keys.begin(), device_keys.end());
  const std::vector<std::int32_t> actual_values(device_values.begin(), device_values.end());
  require<T>(std::is_sorted(actual_keys.begin(), actual_keys.end()), "pair-sort-by-key: keys are not sorted");
  std::vector<std::pair<T, std::int32_t>> expected;
  for (std::size_t index = 0; index < keys.size(); ++index)
    expected.emplace_back(keys[index], values[index]);
  std::sort(expected.begin(), expected.end());
  for (std::size_t index = 0; index < expected.size(); ++index) {
    require<T>(actual_keys[index] == expected[index].first, "pair-sort-by-key: key association mismatch");
    require<T>(actual_values[index] == expected[index].second,
               "pair-sort-by-key: value association mismatch");
  }
}

template <class T> inline void check_pair_scan() {
  const std::vector<Pair<T>> input{{T{1}, 2}, {T{-3}, 4}, {T{5}, -1}, {T{2}, 7}};
  const thrust::device_vector<Pair<T>> source(input.begin(), input.end());
  thrust::device_vector<Pair<T>> output(input.size());
  thrust::inclusive_scan(source.begin(), source.end(), output.begin(), PairPlus<T>{});
  std::vector<Pair<T>> expected(input.size());
  Pair<T> running{T{}, 0};
  for (std::size_t index = 0; index < input.size(); ++index) {
    running = PairPlus<T>{}(running, input[index]);
    expected[index] = running;
  }
  require_pair_equal(std::vector<Pair<T>>(output.begin(), output.end()), expected, "pair-scan");
}

template <class T> inline void check_pair_reduce() {
  const std::vector<Pair<T>> input{{T{1}, 2}, {T{-3}, 4}, {T{5}, -1}, {T{2}, 7}};
  const thrust::device_vector<Pair<T>> source(input.begin(), input.end());
  const auto actual = thrust::reduce(source.begin(), source.end(), Pair<T>{T{}, 0}, PairPlus<T>{});
  Pair<T> expected{T{}, 0};
  for (const auto value : input)
    expected = PairPlus<T>{}(expected, value);
  require<T>(actual.first == expected.first && actual.second == expected.second, "pair-reduce mismatch");
}

template <class T> inline void check_tuple_scan() {
  using Tuple = thrust::tuple<T, std::int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(T{1}, 2), thrust::make_tuple(T{-3}, 4),
                                 thrust::make_tuple(T{5}, -1), thrust::make_tuple(T{2}, 7)};
  const thrust::device_vector<Tuple> source(input.begin(), input.end());
  thrust::device_vector<Tuple> output(input.size());
  thrust::inclusive_scan(source.begin(), source.end(), output.begin(), TuplePlus<T>{});
  const std::vector<Tuple> actual(output.begin(), output.end());
  T first = T{};
  std::int32_t second = 0;
  for (std::size_t index = 0; index < input.size(); ++index) {
    first += thrust::get<0>(input[index]);
    second += thrust::get<1>(input[index]);
    require<T>(thrust::get<0>(actual[index]) == first && thrust::get<1>(actual[index]) == second,
               "tuple-scan mismatch at " + std::to_string(index));
  }
}

template <class T> inline void check_tuple_reduce() {
  using Tuple = thrust::tuple<T, std::int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(T{1}, 2), thrust::make_tuple(T{-3}, 4),
                                 thrust::make_tuple(T{5}, -1), thrust::make_tuple(T{2}, 7)};
  const thrust::device_vector<Tuple> source(input.begin(), input.end());
  const auto actual = thrust::reduce(source.begin(), source.end(), thrust::make_tuple(T{}, 0), TuplePlus<T>{});
  T expected_first = T{};
  std::int32_t expected_second = 0;
  for (const auto value : input) {
    expected_first += thrust::get<0>(value);
    expected_second += thrust::get<1>(value);
  }
  require<T>(thrust::get<0>(actual) == expected_first && thrust::get<1>(actual) == expected_second,
             "tuple-reduce mismatch");
}

template <class T> inline void check_zip_scan() {
  const std::vector<T> keys{T{1}, T{-3}, T{5}, T{2}};
  const std::vector<std::int32_t> values{2, 4, -1, 7};
  const thrust::device_vector<T> device_keys(keys.begin(), keys.end());
  const thrust::device_vector<std::int32_t> device_values(values.begin(), values.end());
  using Tuple = thrust::tuple<T, std::int32_t>;
  thrust::device_vector<Tuple> output(keys.size());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(device_keys.begin(), device_values.begin()));
  thrust::inclusive_scan(first, first + static_cast<std::ptrdiff_t>(keys.size()), output.begin(), TuplePlus<T>{});
  const std::vector<Tuple> actual(output.begin(), output.end());
  T expected_key = T{};
  std::int32_t expected_value = 0;
  for (std::size_t index = 0; index < keys.size(); ++index) {
    expected_key += keys[index];
    expected_value += values[index];
    require<T>(thrust::get<0>(actual[index]) == expected_key && thrust::get<1>(actual[index]) == expected_value,
               "zip-iterator-scan mismatch at " + std::to_string(index));
  }
}

template <class T> inline void check_transform_iterator() {
  const std::vector<T> input{T{-3}, T{-1}, T{2}, T{5}, T{7}};
  const thrust::device_vector<T> source(input.begin(), input.end());
  thrust::device_vector<T> output(input.size());
  const auto first = thrust::make_transform_iterator(source.begin(), Square<T>{});
  thrust::copy(first, first + static_cast<std::ptrdiff_t>(input.size()), output.begin());
  std::vector<T> expected;
  for (const auto value : input)
    expected.push_back(value * value);
  require_equal(std::vector<T>(output.begin(), output.end()), expected, "transform-iterator");
}

template <class T> inline void check_transform_output_iterator() {
  const std::vector<T> input{T{-3}, T{-1}, T{2}, T{5}, T{7}};
  const thrust::device_vector<T> source(input.begin(), input.end());
  thrust::device_vector<T> output(input.size());
  const auto destination = thrust::make_transform_output_iterator(output.begin(), Square<T>{});
  thrust::copy(source.begin(), source.end(), destination);
  std::vector<T> expected;
  for (const auto value : input)
    expected.push_back(value * value);
  require_equal(std::vector<T>(output.begin(), output.end()), expected, "transform-output-iterator");
}

template <class T> inline void check_transform_input_output_iterator() {
  constexpr std::size_t size = 5;
  thrust::device_vector<T> values(size, T{});
  const auto io = thrust::make_transform_input_output_iterator(values.begin(), Negate<T>{}, Square<T>{});
  const auto first = thrust::make_counting_iterator<T>(T{1});
  thrust::copy(first, first + static_cast<std::ptrdiff_t>(size), io);
  for (std::size_t index = 0; index < size; ++index)
    require<T>(values[index] == static_cast<T>((index + 1) * (index + 1)),
               "transform-input-output write mismatch at " + std::to_string(index));
  thrust::device_vector<T> readback(size);
  thrust::copy(io, io + static_cast<std::ptrdiff_t>(size), readback.begin());
  for (std::size_t index = 0; index < size; ++index)
    require<T>(readback[index] == -values[index], "transform-input-output read mismatch at " + std::to_string(index));
}

template <class T> inline void check_zip_sort_by_key() {
  const std::vector<T> keys{T{9}, T{1}, T{8}, T{2}, T{7}};
  const std::vector<std::int32_t> values{90, 10, 80, 20, 70};
  const std::vector<std::int32_t> tags{9, 1, 8, 2, 7};
  thrust::device_vector<T> device_keys(keys.begin(), keys.end());
  thrust::device_vector<std::int32_t> device_values(values.begin(), values.end());
  thrust::device_vector<std::int32_t> device_tags(tags.begin(), tags.end());
  const auto zipped = thrust::make_zip_iterator(thrust::make_tuple(device_values.begin(), device_tags.begin()));
  thrust::sort_by_key(device_keys.begin(), device_keys.end(), zipped);
  for (std::size_t index = 0; index < keys.size(); ++index) {
    const auto key = device_keys[index];
    require<T>(device_values[index] == static_cast<std::int32_t>(key * 10) &&
                   device_tags[index] == static_cast<std::int32_t>(key),
               "zip-iterator-sort-by-key association mismatch at " + std::to_string(index));
  }
}

template <class T> inline void check_reduce_large() {
  constexpr std::size_t size = 4096;
  std::vector<T> input(size);
  for (std::size_t index = 0; index < size; ++index)
    input[index] = static_cast<T>(index % 17) - T{8};
  const thrust::device_vector<T> source(input.begin(), input.end());
  const auto actual = thrust::reduce(source.begin(), source.end(), T{}, thrust::plus<T>{});
  T expected = T{};
  for (const auto value : input)
    expected += value;
  require<T>(actual == expected, "reduce-large mismatch");
}

template <class T> inline void check_stable_sort_by_key_large() {
  constexpr std::size_t size = 257;
  std::vector<T> keys(size);
  std::vector<std::int32_t> values(size);
  for (std::size_t index = 0; index < size; ++index) {
    keys[index] = static_cast<T>((index * 13 + 5) % 19);
    values[index] = static_cast<std::int32_t>(index);
  }
  const thrust::device_vector<T> original_keys(keys.begin(), keys.end());
  const thrust::device_vector<std::int32_t> original_values(values.begin(), values.end());
  thrust::device_vector<T> device_keys = original_keys;
  thrust::device_vector<std::int32_t> device_values = original_values;
  thrust::stable_sort_by_key(device_keys.begin(), device_keys.end(), device_values.begin());
  T previous = device_keys.front();
  std::vector<std::int32_t> last_seen(19, -1);
  for (std::size_t index = 0; index < size; ++index) {
    const auto key = device_keys[index];
    const auto value = device_values[index];
    require<T>(index == 0 || !(key < previous), "stable-sort-large keys are not sorted");
    require<T>(value > last_seen[static_cast<std::size_t>(key)],
               "stable-sort-large equal-key order was not preserved");
    last_seen[static_cast<std::size_t>(key)] = value;
    previous = key;
  }
}

} // namespace detail

inline std::vector<Case> cases(const Configuration &) {
  using T = std::int32_t;
  std::vector<Case> result;
  const auto add = [&result](std::string operation, auto function) {
    result.push_back({"algorithms/roc-thrust-semantic/" + std::move(operation) + "/i32/n11",
                      [function] { return capture(function); }, false});
  };

  add("set-union-by-key", [] { detail::check_set_by_key<T>(detail::SetKind::union_set, false); });
  add("set-intersection-by-key", [] { detail::check_set_by_key<T>(detail::SetKind::intersection, false); });
  add("set-difference-by-key", [] { detail::check_set_by_key<T>(detail::SetKind::difference, false); });
  add("set-symmetric-difference-by-key",
      [] { detail::check_set_by_key<T>(detail::SetKind::symmetric_difference, false); });
  add("set-union-by-key-descending", [] { detail::check_set_by_key<T>(detail::SetKind::union_set, true); });
  add("set-intersection-by-key-descending",
      [] { detail::check_set_by_key<T>(detail::SetKind::intersection, true); });
  add("set-difference-by-key-descending",
      [] { detail::check_set_by_key<T>(detail::SetKind::difference, true); });
  add("set-symmetric-difference-by-key-descending",
      [] { detail::check_set_by_key<T>(detail::SetKind::symmetric_difference, true); });
  add("set-union-descending", [] { detail::check_set_descending<T>(detail::SetKind::union_set); });
  add("set-intersection-descending", [] { detail::check_set_descending<T>(detail::SetKind::intersection); });
  add("set-difference-descending", [] { detail::check_set_descending<T>(detail::SetKind::difference); });
  add("set-symmetric-difference-descending",
      [] { detail::check_set_descending<T>(detail::SetKind::symmetric_difference); });
  add("set-union-key-value", [] { detail::check_pair_set<T>(detail::SetKind::union_set); });
  add("set-intersection-key-value", [] { detail::check_pair_set<T>(detail::SetKind::intersection); });
  add("set-difference-key-value", [] { detail::check_pair_set<T>(detail::SetKind::difference); });
  add("set-symmetric-difference-key-value",
      [] { detail::check_pair_set<T>(detail::SetKind::symmetric_difference); });
  add("set-operations-duplicate-heavy", [] { detail::check_duplicate_heavy_sets<T>(); });
  add("binary-search-descending", [] { detail::check_binary_search_descending<T>(); });
  add("binary-search-vector", [] { detail::check_binary_search_vector<T>(false); });
  add("binary-search-vector-descending", [] { detail::check_binary_search_vector<T>(true); });
  add("gather-repeated", [] { detail::check_gather_repeated<T>(); });
  add("permutation-iterator", [] { detail::check_permutation_iterator<T>(); });
  add("zip-iterator", [] { detail::check_zip_iterator<T>(); });
  add("zip-iterator-sort", [] { detail::check_zip_sort<T>(); });
  add("tuple-transform", [] { detail::check_tuple_transform<T>(); });
  add("tuple-sort", [] { detail::check_tuple_sort<T>(); });
  add("pair-sort", [] { detail::check_pair_sort<T>(); });
  add("pair-sort-by-key", [] { detail::check_pair_sort_by_key<T>(); });
  add("pair-scan", [] { detail::check_pair_scan<T>(); });
  add("pair-reduce", [] { detail::check_pair_reduce<T>(); });
  add("tuple-scan", [] { detail::check_tuple_scan<T>(); });
  add("tuple-reduce", [] { detail::check_tuple_reduce<T>(); });
  add("zip-iterator-scan", [] { detail::check_zip_scan<T>(); });
  add("zip-iterator-sort-by-key", [] { detail::check_zip_sort_by_key<T>(); });
  add("transform-iterator", [] { detail::check_transform_iterator<T>(); });
  add("transform-output-iterator", [] { detail::check_transform_output_iterator<T>(); });
  add("transform-input-output-iterator", [] { detail::check_transform_input_output_iterator<T>(); });
  result.push_back({"algorithms/roc-thrust-semantic/reduce-large/i32/n4096",
                    [] { return capture([] { detail::check_reduce_large<T>(); }); }, false});
  result.push_back({"algorithms/roc-thrust-semantic/stable-sort-by-key-large/i32/n257",
                    [] { return capture([] { detail::check_stable_sort_by_key_large<T>(); }); }, false});
  return result;
}

} // namespace unified_suite::rocm_red_cases

#undef UNIFIED_SUITE_ROCM_RED_HOST_DEVICE

#endif
