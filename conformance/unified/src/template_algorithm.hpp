#pragma once
#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <iterator>
#include <numeric>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "support.hpp"

namespace unified::algorithms {
using support::Affine;
using support::Combine;
using support::Constant;
using support::Greater;
using support::Increment;
using support::IsEven;
using support::KeyValues;
using support::Less;
using support::Native;
using support::Partitioned;
using support::Plus;
using support::Square;
using support::TwoPositions;
using support::TwoRanges;

using support::duplicate_keys;
using support::sorted_values;

template <class T> inline std::vector<T> values(size_t size, size_t salt = 0) { return support::algorithm_values<T>(size, salt); }

template <class T> inline std::vector<T> reference_merge(const std::vector<T> &left, const std::vector<T> &right) {
  std::vector<T> result;
  result.reserve(left.size() + right.size());
  size_t a = 0;
  size_t b = 0;
  while (a < left.size() || b < right.size()) {
    if (b == right.size() || (a < left.size() && !(right[b] < left[a]))) result.push_back(left[a++]);
    else result.push_back(right[b++]);
  }
  return result;
}

template <class T> inline std::vector<T> reference_set_union(const std::vector<T> &left, const std::vector<T> &right) {
  std::vector<T> result;
  size_t a = 0;
  size_t b = 0;
  while (a < left.size() || b < right.size()) {
    if (a == left.size()) result.push_back(right[b++]);
    else if (b == right.size()) result.push_back(left[a++]);
    else if (left[a] < right[b]) result.push_back(left[a++]);
    else if (right[b] < left[a]) result.push_back(right[b++]);
    else {
      result.push_back(left[a]);
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T> inline std::vector<T> reference_set_intersection(const std::vector<T> &left, const std::vector<T> &right) {
  std::vector<T> result;
  size_t a = 0;
  size_t b = 0;
  while (a < left.size() && b < right.size()) {
    if (left[a] < right[b]) ++a;
    else if (right[b] < left[a]) ++b;
    else {
      result.push_back(left[a]);
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T, class Backend> void copy(Backend &backend, size_t size) {
  if (!backend.template supports_copy<T>(size)) SKIP("copy is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  INFO("copy");
  REQUIRE(((backend.copy(input)) == (input)));
}

template <class T, class Backend> void copy_if(Backend &backend, size_t size) {
  if (!backend.template supports_copy_if<T>(size)) SKIP("copy_if is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  std::vector<T> expected;
  std::copy_if(input.begin(), input.end(), std::back_inserter(expected), IsEven<T>{});
  {
    INFO("copy-if");
    REQUIRE(((backend.copy_if(input)) == (expected)));
  }
}

template <class T, class Backend> void copy_n(Backend &backend, size_t size) {
  if (!backend.template supports_copy_n<T>(size)) SKIP("copy_n is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto count = size / 2;
  {
    INFO("copy-n");
    REQUIRE(((backend.copy_n(input, count)) == (std::vector<T>(input.begin(), input.begin() + count))));
  }
}

template <class T, class Backend> void uninitialized_copy(Backend &backend, size_t size) {
  if (!backend.template supports_uninitialized_copy<T>(size)) SKIP("uninitialized_copy is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  INFO("uninitialized-copy");
  REQUIRE(((backend.uninitialized_copy(input)) == (input)));
}

template <class T, class Backend> void uninitialized_fill(Backend &backend, size_t size) {
  if (!backend.template supports_uninitialized_fill<T>(size)) SKIP("uninitialized_fill is unsupported for this type/device");

  INFO("uninitialized-fill");
  REQUIRE(((backend.template uninitialized_fill<T>(size, T{7})) == (std::vector<T>(size, T{7}))));
}

template <class T, class Backend> void fill(Backend &backend, size_t size) {
  if (!backend.template supports_fill<T>(size)) SKIP("fill is unsupported for this type/device");

  INFO("fill");
  REQUIRE(((backend.template fill<T>(size, T{7})) == (std::vector<T>(size, T{7}))));
}

template <class T, class Backend> void generate(Backend &backend, size_t size) {
  if (!backend.template supports_generate<T>(size)) SKIP("generate is unsupported for this type/device");

  INFO("generate");
  REQUIRE(((backend.template generate<T>(size)) == (std::vector<T>(size, T{7}))));
}

template <class T, class Backend> void for_each(Backend &backend, size_t size) {
  if (!backend.template supports_for_each<T>(size)) SKIP("for_each is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  for (auto &value : expected)
    Increment<T>{}(value);
  {
    INFO("for-each");
    REQUIRE(((backend.for_each(input)) == (expected)));
  }
}

template <class T, class Backend> void transform_unary(Backend &backend, size_t size) {
  if (!backend.template supports_transform_unary<T>(size)) SKIP("transform_unary is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  for (auto &value : expected)
    value = Affine<T>{}(value);
  {
    INFO("transform-unary");
    REQUIRE(((backend.transform_unary(input)) == (expected)));
  }
}

template <class T, class Backend> void transform_binary(Backend &backend, size_t size) {
  if (!backend.template supports_transform_binary<T>(size)) SKIP("transform_binary is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto right = values<T>(size, 7);
  auto expected = input;
  for (size_t index = 0; index < size; ++index)
    expected[index] = Combine<T>{}(input[index], right[index]);
  {
    INFO("transform-binary");
    REQUIRE(((backend.transform_binary(input, right)) == (expected)));
  }
}

template <class T, class Backend> void replace_if(Backend &backend, size_t size) {
  if (!backend.template supports_replace_if<T>(size)) SKIP("replace_if is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  std::replace_if(expected.begin(), expected.end(), IsEven<T>{}, T{9});
  const auto actual = backend.replace_if(input, T{9});
  {
    INFO("replace_if");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void replace_copy_if(Backend &backend, size_t size) {
  if (!backend.template supports_replace_copy_if<T>(size)) SKIP("replace_copy_if is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  std::replace_if(expected.begin(), expected.end(), IsEven<T>{}, T{9});
  const auto actual = backend.replace_copy_if(input, T{9});
  {
    INFO("replace_copy_if");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void remove_if(Backend &backend, size_t size) {
  if (!backend.template supports_remove_if<T>(size)) SKIP("remove_if is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  expected.erase(std::remove_if(expected.begin(), expected.end(), IsEven<T>{}), expected.end());
  const auto actual = backend.remove_if(input);
  {
    INFO("remove_if");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void remove_copy_if(Backend &backend, size_t size) {
  if (!backend.template supports_remove_copy_if<T>(size)) SKIP("remove_copy_if is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  expected.erase(std::remove_if(expected.begin(), expected.end(), IsEven<T>{}), expected.end());
  const auto actual = backend.remove_copy_if(input);
  {
    INFO("remove_copy_if");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void reverse(Backend &backend, size_t size) {
  if (!backend.template supports_reverse<T>(size)) SKIP("reverse is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  std::reverse(expected.begin(), expected.end());
  {
    INFO("reverse");
    REQUIRE(((backend.reverse(input)) == (expected)));
  }
}

template <class T, class Backend> void reverse_copy(Backend &backend, size_t size) {
  if (!backend.template supports_reverse_copy<T>(size)) SKIP("reverse_copy is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  std::reverse(expected.begin(), expected.end());
  {
    INFO("reverse-copy");
    REQUIRE(((backend.reverse_copy(input)) == (expected)));
  }
}

template <class T, class Backend> void shuffle(Backend &backend, size_t size) {
  if (!backend.template supports_shuffle<T>(size)) SKIP("shuffle is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  auto actual = backend.shuffle(input);
  std::sort(expected.begin(), expected.end());
  std::sort(actual.begin(), actual.end());
  {
    INFO("shuffle.permutation");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void swap_ranges(Backend &backend, size_t size) {
  if (!backend.template supports_swap_ranges<T>(size)) SKIP("swap_ranges is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto right = values<T>(size, 7);
  const auto actual = backend.swap_ranges(input, right);
  {
    INFO("swap-ranges.first");
    REQUIRE(((actual.first) == (right)));
  }
  {
    INFO("swap-ranges.second");
    REQUIRE(((actual.second) == (input)));
  }
}

template <class T, class Backend> void reduce(Backend &backend, size_t size) {
  if (!backend.template supports_reduce<T>(size)) SKIP("reduce is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  T expected{};
  for (const auto value : input)
    expected = Plus<T>{}(expected, value);
  {
    INFO("reduce");
    REQUIRE(((backend.reduce(input)) == (expected)));
  }
}

template <class T, class Backend> void transform_reduce(Backend &backend, size_t size) {
  if (!backend.template supports_transform_reduce<T>(size)) SKIP("transform_reduce is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  T expected{};
  for (const auto value : input)
    expected = Plus<T>{}(expected, Square<T>{}(value));
  {
    INFO("transform-reduce");
    REQUIRE(((backend.transform_reduce(input)) == (expected)));
  }
}

template <class T, class Backend> void inner_product(Backend &backend, size_t size) {
  if (!backend.template supports_inner_product<T>(size)) SKIP("inner_product is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto right = values<T>(size, 7);
  T expected{};
  for (size_t index = 0; index < size; ++index)
    expected = Plus<T>{}(expected, static_cast<T>(input[index] * right[index]));
  {
    INFO("inner-product");
    REQUIRE(((backend.inner_product(input, right)) == (expected)));
  }
}

template <class T, class Backend> void inclusive_scan(Backend &backend, size_t size) {
  if (!backend.template supports_inclusive_scan<T>(size)) SKIP("inclusive_scan is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  for (size_t index = 1; index < size; ++index)
    expected[index] = Plus<T>{}(expected[index - 1], input[index]);
  {
    INFO("inclusive-scan");
    REQUIRE(((backend.inclusive_scan(input)) == (expected)));
  }
}

template <class T, class Backend> void exclusive_scan(Backend &backend, size_t size) {
  if (!backend.template supports_exclusive_scan<T>(size)) SKIP("exclusive_scan is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  std::vector<T> expected(size);
  T running{};
  for (size_t index = 0; index < size; ++index) {
    expected[index] = running;
    running = Plus<T>{}(running, input[index]);
  }
  {
    INFO("exclusive-scan");
    REQUIRE(((backend.exclusive_scan(input)) == (expected)));
  }
}

template <class T, class Backend> void transform_inclusive_scan(Backend &backend, size_t size) {
  if (!backend.template supports_transform_inclusive_scan<T>(size)) SKIP("transform_inclusive_scan is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  std::vector<T> expected(size);
  T running{};
  for (size_t index = 0; index < size; ++index) {
    running = Plus<T>{}(running, Square<T>{}(input[index]));
    expected[index] = running;
  }
  {
    INFO("transform-inclusive-scan");
    REQUIRE(((backend.transform_inclusive_scan(input)) == (expected)));
  }
}

template <class T, class Backend> void transform_exclusive_scan(Backend &backend, size_t size) {
  if (!backend.template supports_transform_exclusive_scan<T>(size)) SKIP("transform_exclusive_scan is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  std::vector<T> expected(size);
  T running{};
  for (size_t index = 0; index < size; ++index) {
    expected[index] = running;
    running = Plus<T>{}(running, Square<T>{}(input[index]));
  }
  {
    INFO("transform-exclusive-scan");
    REQUIRE(((backend.transform_exclusive_scan(input)) == (expected)));
  }
}

template <class T, class Backend> void adjacent_difference(Backend &backend, size_t size) {
  if (!backend.template supports_adjacent_difference<T>(size)) SKIP("adjacent_difference is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  std::vector<T> expected(size);
  if (!input.empty()) expected[0] = input[0];
  for (size_t index = 1; index < size; ++index)
    expected[index] = static_cast<T>(input[index] - input[index - 1]);
  {
    INFO("adjacent-difference");
    REQUIRE(((backend.adjacent_difference(input)) == (expected)));
  }
}

template <class T, class Backend> void sort(Backend &backend, size_t size) {
  if (!backend.template supports_sort<T>(size)) SKIP("sort is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  std::sort(expected.begin(), expected.end());
  {
    INFO("sort");
    REQUIRE(((backend.sort(input)) == (expected)));
  }
}

template <class T, class Backend> void stable_sort(Backend &backend, size_t size) {
  if (!backend.template supports_stable_sort<T>(size)) SKIP("stable_sort is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  std::stable_sort(expected.begin(), expected.end());
  {
    INFO("stable-sort");
    REQUIRE(((backend.stable_sort(input)) == (expected)));
  }
}

template <class T, class Backend> void sort_descending(Backend &backend, size_t size) {
  if (!backend.template supports_sort_descending<T>(size)) SKIP("sort_descending is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto expected = input;
  std::sort(expected.begin(), expected.end(), Greater<T>{});
  {
    INFO("sort-descending");
    REQUIRE(((backend.sort_descending(input)) == (expected)));
  }
}

template <class T, class Backend> void sort_by_key(Backend &backend, size_t size) {
  if (!backend.template supports_sort_by_key<T>(size)) SKIP("sort_by_key is unsupported for this type/device");

  auto keys = values<T>(size, 4);
  for (size_t index = 0; index < keys.size(); ++index)
    keys[index] = static_cast<T>(keys[index] * static_cast<T>(size + 1) + static_cast<T>(index));
  std::vector<int32_t> payload(size);
  std::iota(payload.begin(), payload.end(), 0);
  const auto actual = backend.sort_by_key(keys, payload);
  {
    INFO("sort-by-key: keys are not sorted");
    REQUIRE((std::is_sorted(actual.keys.begin(), actual.keys.end())));
  }
  {
    INFO("sort-by-key");
    REQUIRE_THAT(actual.keys, unified::KeyPermutation(actual.values, keys));
  }
}

template <class T, class Backend> void stable_sort_by_key(Backend &backend, size_t size) {
  if (!backend.template supports_stable_sort_by_key<T>(size)) SKIP("stable_sort_by_key is unsupported for this type/device");

  const auto keys = duplicate_keys<T>(size);
  std::vector<int32_t> payload(size);
  std::iota(payload.begin(), payload.end(), 0);
  const auto actual = backend.stable_sort_by_key(keys, payload);
  {
    INFO("stable-sort-by-key: keys are not sorted");
    REQUIRE((std::is_sorted(actual.keys.begin(), actual.keys.end())));
  }
  {
    INFO("stable-sort-by-key");
    REQUIRE_THAT(actual.keys, unified::KeyPermutation(actual.values, keys));
  }
  for (size_t index = 0; index < size; ++index) {
    if (index != 0 && actual.keys[index] == actual.keys[index - 1]) {
      INFO("stable-sort-by-key: unstable equal keys");
      REQUIRE((actual.values[index - 1] < actual.values[index]));
    }
  }
}

template <class T, class Backend> void unique(Backend &backend, size_t size) {
  if (!backend.template supports_unique<T>(size)) SKIP("unique is unsupported for this type/device");

  auto repeated = duplicate_keys<T>(size);
  std::sort(repeated.begin(), repeated.end());
  auto expected = repeated;
  expected.erase(std::unique(expected.begin(), expected.end()), expected.end());
  {
    INFO("unique");
    REQUIRE(((backend.unique(repeated)) == (expected)));
  }
}

template <class T, class Backend> void unique_copy(Backend &backend, size_t size) {
  if (!backend.template supports_unique_copy<T>(size)) SKIP("unique_copy is unsupported for this type/device");

  auto repeated = duplicate_keys<T>(size);
  std::sort(repeated.begin(), repeated.end());
  auto expected = repeated;
  expected.erase(std::unique(expected.begin(), expected.end()), expected.end());
  {
    INFO("unique-copy");
    REQUIRE(((backend.unique_copy(repeated)) == (expected)));
  }
}

template <class T, class Backend> void count(Backend &backend, size_t size) {
  if (!backend.template supports_count<T>(size)) SKIP("count is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const T needle = T{1};
  const auto expected = static_cast<size_t>(std::count(input.begin(), input.end(), needle));
  {
    INFO("count");
    REQUIRE(((backend.count(input, needle)) == (expected)));
  }
}

template <class T, class Backend> void count_if(Backend &backend, size_t size) {
  if (!backend.template supports_count_if<T>(size)) SKIP("count_if is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  size_t expected = 0;
  for (const auto value : input)
    if (IsEven<T>{}(value)) ++expected;
  {
    INFO("count-if");
    REQUIRE(((backend.count_if(input)) == (expected)));
  }
}

template <class T, class Backend> void find(Backend &backend, size_t size) {
  if (!backend.template supports_find<T>(size)) SKIP("find is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const T needle = T{1};
  const auto expected = static_cast<size_t>(std::find(input.begin(), input.end(), needle) - input.begin());
  {
    INFO("find");
    REQUIRE(((backend.find(input, needle)) == (expected)));
  }
}

template <class T, class Backend> void find_if(Backend &backend, size_t size) {
  if (!backend.template supports_find_if<T>(size)) SKIP("find_if is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  size_t expected = size;
  for (size_t index = 0; index < size; ++index)
    if (IsEven<T>{}(input[index])) {
      expected = index;
      break;
    }
  {
    INFO("find-if");
    REQUIRE(((backend.find_if(input)) == (expected)));
  }
}

template <class T, class Backend> void all_of(Backend &backend, size_t size) {
  if (!backend.template supports_all_of<T>(size)) SKIP("all_of is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  INFO("all-of");
  REQUIRE(((backend.all_of(input)) == (std::all_of(input.begin(), input.end(), IsEven<T>{}))));
}

template <class T, class Backend> void any_of(Backend &backend, size_t size) {
  if (!backend.template supports_any_of<T>(size)) SKIP("any_of is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  INFO("any-of");
  REQUIRE(((backend.any_of(input)) == (std::any_of(input.begin(), input.end(), IsEven<T>{}))));
}

template <class T, class Backend> void none_of(Backend &backend, size_t size) {
  if (!backend.template supports_none_of<T>(size)) SKIP("none_of is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  INFO("none-of");
  REQUIRE(((backend.none_of(input)) == (std::none_of(input.begin(), input.end(), IsEven<T>{}))));
}

template <class T, class Backend> void equal(Backend &backend, size_t size) {
  if (!backend.template supports_equal<T>(size)) SKIP("equal is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto right = input;
  if (!right.empty()) right.back() = static_cast<T>(right.back() + T{1});
  {
    INFO("equal");
    REQUIRE(((backend.equal(input, right)) == (input == right)));
  }
}

template <class T, class Backend> void mismatch(Backend &backend, size_t size) {
  if (!backend.template supports_mismatch<T>(size)) SKIP("mismatch is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto right = input;
  if (!right.empty()) right[size / 2] = static_cast<T>(right[size / 2] + T{1});
  const auto expected_pair = std::mismatch(input.begin(), input.end(), right.begin());
  const TwoPositions expected{static_cast<size_t>(expected_pair.first - input.begin()),
                              static_cast<size_t>(expected_pair.second - right.begin())};
  {
    INFO("mismatch");
    REQUIRE(((backend.mismatch(input, right)) == (expected)));
  }
}

template <class T, class Backend> void lower_bound(Backend &backend, size_t size) {
  if (!backend.template supports_lower_bound<T>(size)) SKIP("lower_bound is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto sorted = input;
  std::sort(sorted.begin(), sorted.end());
  const T needle = T{1};
  size_t expected = 0;
  while (expected < sorted.size() && sorted[expected] < needle)
    ++expected;
  {
    INFO("lower-bound");
    REQUIRE(((backend.lower_bound(sorted, needle)) == (expected)));
  }
}

template <class T, class Backend> void upper_bound(Backend &backend, size_t size) {
  if (!backend.template supports_upper_bound<T>(size)) SKIP("upper_bound is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto sorted = input;
  std::sort(sorted.begin(), sorted.end());
  const T needle = T{1};
  const auto expected = static_cast<size_t>(std::upper_bound(sorted.begin(), sorted.end(), needle) - sorted.begin());
  {
    INFO("upper-bound");
    REQUIRE(((backend.upper_bound(sorted, needle)) == (expected)));
  }
}

template <class T, class Backend> void binary_search(Backend &backend, size_t size) {
  if (!backend.template supports_binary_search<T>(size)) SKIP("binary_search is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto sorted = input;
  std::sort(sorted.begin(), sorted.end());
  const T needle = T{1};
  {
    INFO("binary-search");
    REQUIRE(((backend.binary_search(sorted, needle)) == (std::binary_search(sorted.begin(), sorted.end(), needle))));
  }
}

template <class T, class Backend> void equal_range(Backend &backend, size_t size) {
  if (!backend.template supports_equal_range<T>(size)) SKIP("equal_range is unsupported for this type/device");

  auto sorted = duplicate_keys<T>(size);
  std::sort(sorted.begin(), sorted.end());
  const T needle = T{3};
  const auto expected_pair = std::equal_range(sorted.begin(), sorted.end(), needle);
  const TwoPositions expected{static_cast<size_t>(expected_pair.first - sorted.begin()),
                              static_cast<size_t>(expected_pair.second - sorted.begin())};
  {
    INFO("equal-range");
    REQUIRE(((backend.equal_range(sorted, needle)) == (expected)));
  }
}

template <class T, class Backend> void merge(Backend &backend, size_t size) {
  if (!backend.template supports_merge<T>(size)) SKIP("merge is unsupported for this type/device");

  const auto left = sorted_values<T>((size + 1) / 2, 2);
  const auto right = sorted_values<T>(size / 2, 3);
  {
    INFO("merge");
    REQUIRE(((backend.merge(left, right)) == (reference_merge(left, right))));
  }
}

template <class T, class Backend> void partition(Backend &backend, size_t size) {
  if (!backend.template supports_partition<T>(size)) SKIP("partition is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto actual = backend.partition(input);
  {
    INFO("partition: invalid boundary");
    REQUIRE((actual.boundary <= actual.values.size()));
  }
  for (size_t index = 0; index < actual.boundary; ++index) {
    INFO("partition: false element before boundary");
    REQUIRE((IsEven<T>{}(actual.values[index])));
  }
  for (size_t index = actual.boundary; index < actual.values.size(); ++index) {
    INFO("partition: true element after boundary");
    REQUIRE((!IsEven<T>{}(actual.values[index])));
  }
  auto expected_values = input;
  auto actual_values = actual.values;
  std::sort(expected_values.begin(), expected_values.end());
  std::sort(actual_values.begin(), actual_values.end());
  {
    INFO("partition: permutation");
    REQUIRE(((actual_values) == (expected_values)));
  }
}

template <class T, class Backend> void stable_partition(Backend &backend, size_t size) {
  if (!backend.template supports_stable_partition<T>(size)) SKIP("stable_partition is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto actual = backend.stable_partition(input);
  std::vector<T> expected = input;
  const auto boundary = std::stable_partition(expected.begin(), expected.end(), IsEven<T>{});
  {
    INFO("stable-partition.values");
    REQUIRE(((actual.values) == (expected)));
  }
  {
    INFO("stable-partition.boundary");
    REQUIRE(((actual.boundary) == (static_cast<size_t>(boundary - expected.begin()))));
  }
}

template <class T, class Backend> void partition_copy(Backend &backend, size_t size) {
  if (!backend.template supports_partition_copy<T>(size)) SKIP("partition_copy is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  std::vector<T> selected;
  std::vector<T> rejected;
  for (const auto value : input)
    (IsEven<T>{}(value) ? selected : rejected).push_back(value);
  const auto actual = backend.partition_copy(input);
  {
    INFO("partition-copy.selected");
    REQUIRE(((actual.first) == (selected)));
  }
  {
    INFO("partition-copy.rejected");
    REQUIRE(((actual.second) == (rejected)));
  }
}

template <class T, class Backend> void is_partitioned(Backend &backend, size_t size) {
  if (!backend.template supports_is_partitioned<T>(size)) SKIP("is_partitioned is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto partitioned = input;
  std::partition(partitioned.begin(), partitioned.end(), IsEven<T>{});
  {
    INFO("is-partitioned");
    REQUIRE(((backend.is_partitioned(partitioned)) == (true)));
  }
}

template <class T, class Backend> void is_partitioned_false(Backend &backend, size_t size) {
  if (!backend.template supports_is_partitioned_false<T>(size)) SKIP("is_partitioned_false is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  if (size < 3) {
    SUCCEED("is-partitioned-false is not applicable below three values");
    return;
  }
  auto not_partitioned = input;
  not_partitioned[0] = T{2};
  not_partitioned[1] = T{1};
  not_partitioned[2] = T{4};
  {
    INFO("is-partitioned-false");
    REQUIRE(((backend.is_partitioned(not_partitioned)) == (false)));
  }
}

template <class T, class Backend> void partition_point(Backend &backend, size_t size) {
  if (!backend.template supports_partition_point<T>(size)) SKIP("partition_point is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto partitioned = input;
  const auto point = std::partition(partitioned.begin(), partitioned.end(), IsEven<T>{});
  {
    INFO("partition-point");
    REQUIRE(((backend.partition_point(partitioned)) == (static_cast<size_t>(point - partitioned.begin()))));
  }
}

template <class T, class Backend> void set_union(Backend &backend, size_t size) {
  if (!backend.template supports_set_union<T>(size)) SKIP("set_union is unsupported for this type/device");

  const auto left = sorted_values<T>((size + 1) / 2, 2);
  const auto right = sorted_values<T>(size / 2, 3);
  {
    INFO("set-union");
    REQUIRE(((backend.set_union(left, right)) == (reference_set_union(left, right))));
  }
}

template <class T, class Backend> void set_intersection(Backend &backend, size_t size) {
  if (!backend.template supports_set_intersection<T>(size)) SKIP("set_intersection is unsupported for this type/device");

  const auto left = sorted_values<T>((size + 1) / 2, 2);
  const auto right = sorted_values<T>(size / 2, 3);
  {
    INFO("set-intersection");
    REQUIRE(((backend.set_intersection(left, right)) == (reference_set_intersection(left, right))));
  }
}

template <class T, class Backend> void set_difference(Backend &backend, size_t size) {
  if (!backend.template supports_set_difference<T>(size)) SKIP("set_difference is unsupported for this type/device");

  const auto left = sorted_values<T>((size + 1) / 2, 2);
  const auto right = sorted_values<T>(size / 2, 3);
  std::vector<T> expected(left.size());
  expected.erase(std::set_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin()), expected.end());
  {
    INFO("set-difference");
    REQUIRE(((backend.set_difference(left, right)) == (expected)));
  }
}

template <class T, class Backend> void set_symmetric_difference(Backend &backend, size_t size) {
  if (!backend.template supports_set_symmetric_difference<T>(size)) SKIP("set_symmetric_difference is unsupported for this type/device");

  const auto left = sorted_values<T>((size + 1) / 2, 2);
  const auto right = sorted_values<T>(size / 2, 3);
  std::vector<T> expected(left.size() + right.size());
  expected.erase(std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin()), expected.end());
  {
    INFO("set-symmetric-difference");
    REQUIRE(((backend.set_symmetric_difference(left, right)) == (expected)));
  }
}

template <class T, class Backend> void min_element(Backend &backend, size_t size) {
  if (!backend.template supports_min_element<T>(size)) SKIP("min_element is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto extrema = input;
  if (!extrema.empty()) extrema[size / 3] = T{-100};
  const auto expected = extrema.empty() ? size : static_cast<size_t>(std::min_element(extrema.begin(), extrema.end()) - extrema.begin());
  {
    INFO("min-element");
    REQUIRE(((backend.min_element(extrema)) == (expected)));
  }
}

template <class T, class Backend> void max_element(Backend &backend, size_t size) {
  if (!backend.template supports_max_element<T>(size)) SKIP("max_element is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto extrema = input;
  if (!extrema.empty()) extrema[(size * 2) / 3] = T{100};
  const auto expected = extrema.empty() ? size : static_cast<size_t>(std::max_element(extrema.begin(), extrema.end()) - extrema.begin());
  {
    INFO("max-element");
    REQUIRE(((backend.max_element(extrema)) == (expected)));
  }
}

template <class T, class Backend> void minmax_element(Backend &backend, size_t size) {
  if (!backend.template supports_minmax_element<T>(size)) SKIP("minmax_element is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto extrema = input;
  if (extrema.size() == 1) extrema[0] = T{0};
  else if (!extrema.empty()) {
    extrema[size / 3] = T{-100};
    extrema[(size * 2) / 3] = T{100};
  }
  const auto pair = std::minmax_element(extrema.begin(), extrema.end());
  const TwoPositions expected{static_cast<size_t>(pair.first - extrema.begin()), static_cast<size_t>(pair.second - extrema.begin())};
  {
    INFO("minmax-element");
    REQUIRE(((backend.minmax_element(extrema)) == (expected)));
  }
}

template <class T, class Backend> void is_sorted(Backend &backend, size_t size) {
  if (!backend.template supports_is_sorted<T>(size)) SKIP("is_sorted is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto sorted = input;
  std::sort(sorted.begin(), sorted.end());
  {
    INFO("is-sorted");
    REQUIRE(((backend.is_sorted(sorted)) == (true)));
  }
}

template <class T, class Backend> void is_sorted_false(Backend &backend, size_t size) {
  if (!backend.template supports_is_sorted_false<T>(size)) SKIP("is_sorted_false is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  if (size < 2) {
    SUCCEED("is-sorted-false is not applicable below two values");
    return;
  }
  auto not_sorted = input;
  not_sorted[0] = T{1};
  not_sorted[1] = T{0};
  {
    INFO("is-sorted-false");
    REQUIRE(((backend.is_sorted(not_sorted)) == (false)));
  }
}

template <class T, class Backend> void is_sorted_until(Backend &backend, size_t size) {
  if (!backend.template supports_is_sorted_until<T>(size)) SKIP("is_sorted_until is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto nearly = input;
  std::sort(nearly.begin(), nearly.end());
  if (nearly.size() > 2) std::swap(nearly[nearly.size() / 2], nearly.back());
  const auto expected = static_cast<size_t>(std::is_sorted_until(nearly.begin(), nearly.end()) - nearly.begin());
  {
    INFO("is-sorted-until");
    REQUIRE(((backend.is_sorted_until(nearly)) == (expected)));
  }
}

template <class T, class Backend> void rotate(Backend &backend, size_t size) {
  if (!backend.template supports_rotate<T>(size)) SKIP("rotate is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto middle = size / 3;
  auto expected = input;
  std::rotate(expected.begin(), expected.begin() + static_cast<ptrdiff_t>(middle), expected.end());
  const auto actual = backend.rotate(input, middle);
  {
    INFO("rotate");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void rotate_copy(Backend &backend, size_t size) {
  if (!backend.template supports_rotate_copy<T>(size)) SKIP("rotate_copy is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto middle = size / 3;
  auto expected = input;
  std::rotate(expected.begin(), expected.begin() + static_cast<ptrdiff_t>(middle), expected.end());
  const auto actual = backend.rotate_copy(input, middle);
  {
    INFO("rotate_copy");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void includes(Backend &backend, size_t size) {
  if (!backend.template supports_includes<T>(size)) SKIP("includes is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto left = input;
  std::sort(left.begin(), left.end());
  std::vector<T> right;
  for (size_t index = 0; index < left.size(); index += 3)
    right.push_back(left[index]);
  {
    INFO("includes");
    REQUIRE(((backend.includes(left, right)) == (true)));
  }
}

template <class T, class Backend> void includes_false(Backend &backend, size_t size) {
  if (!backend.template supports_includes_false<T>(size)) SKIP("includes_false is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  auto left = input;
  std::sort(left.begin(), left.end());
  const std::vector<T> right{T{100}};
  {
    INFO("includes-false");
    REQUIRE(((backend.includes(left, right)) == (false)));
  }
}

template <class T, class Backend> void gather(Backend &backend, size_t size) {
  if (!backend.template supports_gather<T>(size)) SKIP("gather is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  std::vector<size_t> indices(size);
  for (size_t index = 0; index < size; ++index)
    indices[index] = size - index - 1;
  auto expected = input;
  std::reverse(expected.begin(), expected.end());
  const auto actual = backend.gather(input, indices);
  {
    INFO("gather");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void scatter(Backend &backend, size_t size) {
  if (!backend.template supports_scatter<T>(size)) SKIP("scatter is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  std::vector<size_t> indices(size);
  for (size_t index = 0; index < size; ++index)
    indices[index] = size - index - 1;
  auto expected = input;
  std::reverse(expected.begin(), expected.end());
  const auto actual = backend.scatter(input, indices);
  {
    INFO("scatter");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void reduce_by_key(Backend &backend, size_t size) {
  if (!backend.template supports_reduce_by_key<T>(size)) SKIP("reduce_by_key is unsupported for this type/device");

  std::vector<T> keys(size);
  std::vector<int32_t> values(size);
  for (size_t index = 0; index < size; ++index) {
    keys[index] = static_cast<T>(index / 3);
    values[index] = static_cast<int32_t>(index % 4 + 1);
  }
  KeyValues<T> expected;

  for (size_t index = 0; index < size; ++index) {
    if (expected.keys.empty() || expected.keys.back() != keys[index]) {
      expected.keys.push_back(keys[index]);
      expected.values.push_back(values[index]);
    } else {
      expected.values.back() += values[index];
    }
  }

  KeyValues<T> actual;
  actual = backend.reduce_by_key(keys, values);
  {
    INFO("reduce_by_key");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void inclusive_scan_by_key(Backend &backend, size_t size) {
  if (!backend.template supports_inclusive_scan_by_key<T>(size)) SKIP("inclusive_scan_by_key is unsupported for this type/device");

  std::vector<T> keys(size);
  std::vector<int32_t> values(size);
  for (size_t index = 0; index < size; ++index) {
    keys[index] = static_cast<T>(index / 3);
    values[index] = static_cast<int32_t>(index % 4 + 1);
  }
  KeyValues<T> expected;

  expected = {keys, values};
  for (size_t index = 1; index < size; ++index)
    if (keys[index] == keys[index - 1]) expected.values[index] += expected.values[index - 1];

  KeyValues<T> actual;
  actual = backend.inclusive_scan_by_key(keys, values);
  {
    INFO("inclusive_scan_by_key");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void exclusive_scan_by_key(Backend &backend, size_t size) {
  if (!backend.template supports_exclusive_scan_by_key<T>(size)) SKIP("exclusive_scan_by_key is unsupported for this type/device");

  std::vector<T> keys(size);
  std::vector<int32_t> values(size);
  for (size_t index = 0; index < size; ++index) {
    keys[index] = static_cast<T>(index / 3);
    values[index] = static_cast<int32_t>(index % 4 + 1);
  }
  KeyValues<T> expected;

  expected = {keys, std::vector<int32_t>(size)};
  int32_t running = 0;
  for (size_t index = 0; index < size; ++index) {
    if (index == 0 || keys[index] != keys[index - 1]) running = 0;
    expected.values[index] = running;
    running += values[index];
  }

  KeyValues<T> actual;
  actual = backend.exclusive_scan_by_key(keys, values);
  {
    INFO("exclusive_scan_by_key");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void unique_by_key(Backend &backend, size_t size) {
  if (!backend.template supports_unique_by_key<T>(size)) SKIP("unique_by_key is unsupported for this type/device");

  std::vector<T> keys(size);
  std::vector<int32_t> values(size);
  for (size_t index = 0; index < size; ++index) {
    keys[index] = static_cast<T>(index / 3);
    values[index] = static_cast<int32_t>(index % 4 + 1);
  }
  KeyValues<T> expected;

  for (size_t index = 0; index < size; ++index)
    if (index == 0 || keys[index] != keys[index - 1]) {
      expected.keys.push_back(keys[index]);
      expected.values.push_back(values[index]);
    }

  KeyValues<T> actual;
  actual = backend.unique_by_key(keys, values);
  {
    INFO("unique_by_key");
    REQUIRE(((actual) == (expected)));
  }
}

template <class T, class Backend> void merge_by_key(Backend &backend, size_t size) {
  if (!backend.template supports_merge_by_key<T>(size)) SKIP("merge_by_key is unsupported for this type/device");

  KeyValues<T> left{sorted_values<T>((size + 1) / 2, 2), {}};
  KeyValues<T> right{sorted_values<T>(size / 2, 3), {}};
  left.values.resize(left.keys.size());
  right.values.resize(right.keys.size());
  for (size_t index = 0; index < left.values.size(); ++index)
    left.values[index] = static_cast<int32_t>(index + 100);
  for (size_t index = 0; index < right.values.size(); ++index)
    right.values[index] = static_cast<int32_t>(index + 200);
  KeyValues<T> expected;
  size_t a = 0;
  size_t b = 0;
  while (a < left.keys.size() || b < right.keys.size()) {
    const bool take_left = b == right.keys.size() || (a < left.keys.size() && !(right.keys[b] < left.keys[a]));
    if (take_left) {
      expected.keys.push_back(left.keys[a]);
      expected.values.push_back(left.values[a++]);
    } else {
      expected.keys.push_back(right.keys[b]);
      expected.values.push_back(right.values[b++]);
    }
  }
  {
    INFO("merge-by-key");
    REQUIRE(((backend.merge_by_key(left, right)) == (expected)));
  }
}

template <class T, class Backend> void partial_sort(Backend &backend, size_t size) {
  if (!backend.template supports_partial_sort<T>(size)) SKIP("partial_sort is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto middle = size / 2;
  const auto actual = backend.partial_sort(input, middle);
  auto fully_sorted = input;
  std::sort(fully_sorted.begin(), fully_sorted.end());
  {
    INFO("partial-sort: prefix mismatch");
    REQUIRE((std::equal(actual.begin(), actual.begin() + static_cast<ptrdiff_t>(middle), fully_sorted.begin())));
  }
  auto permutation = actual;
  std::sort(permutation.begin(), permutation.end());
  {
    INFO("partial-sort: permutation");
    REQUIRE(((permutation) == (fully_sorted)));
  }
}

template <class T, class Backend> void nth_element(Backend &backend, size_t size) {
  if (!backend.template supports_nth_element<T>(size)) SKIP("nth_element is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto nth = size / 2;
  const auto actual = backend.nth_element(input, nth);
  auto fully_sorted = input;
  std::sort(fully_sorted.begin(), fully_sorted.end());
  if (!actual.empty()) {
    INFO("nth-element.value");
    REQUIRE(((actual[nth]) == (fully_sorted[nth])));
  }
  auto permutation = actual;
  std::sort(permutation.begin(), permutation.end());
  {
    INFO("nth-element: permutation");
    REQUIRE(((permutation) == (fully_sorted)));
  }
}

template <class T, class Backend> void inplace_merge(Backend &backend, size_t size) {
  if (!backend.template supports_inplace_merge<T>(size)) SKIP("inplace_merge is unsupported for this type/device");

  const auto input = values<T>(size, 1);

  const auto middle = size / 2;
  auto halves = input;
  std::sort(halves.begin(), halves.begin() + static_cast<ptrdiff_t>(middle));
  std::sort(halves.begin() + static_cast<ptrdiff_t>(middle), halves.end());
  auto expected = halves;
  std::inplace_merge(expected.begin(), expected.begin() + static_cast<ptrdiff_t>(middle), expected.end());
  {
    INFO("inplace-merge");
    REQUIRE(((backend.inplace_merge(halves, middle)) == (expected)));
  }
}
} // namespace unified::algorithms
