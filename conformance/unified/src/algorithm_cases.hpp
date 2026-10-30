#pragma once
#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <numeric>
#include <vector>

#include <aspartame/optional.hpp>
#include <aspartame/vector.hpp>
#include <aspartame/view.hpp>
#include <catch2/catch_test_macros.hpp>

#include "test_support.hpp"

namespace unified::algorithms {
using support::Affine;
using support::Combine;
using support::Constant;
using support::Greater;
using support::Increment;
using support::IsEven;
using support::KeyValues;
using support::Less;
using support::Partitioned;
using support::Plus;
using support::Square;
using support::TwoPositions;
using support::TwoRanges;

using support::duplicateKeys;
using support::sortedValues;

template <class T> inline std::vector<T> values(size_t size, size_t salt = 0) {
  using namespace aspartame;
  return iota(size_t{0}, size)
         | map([&](size_t index) { return static_cast<T>(static_cast<int64_t>(((index * 17) + (salt * 11)) % 23) - 11); }) | to_vector();
}

template <class T> inline std::vector<T> groupedKeys(size_t size) {
  using namespace aspartame;
  return iota(size_t{0}, size) | map([](size_t index) {
           const auto group = index / 3;
           return static_cast<T>(group);
         })
         | to_vector();
}

inline std::vector<int32_t> cyclicValues(size_t size) {
  using namespace aspartame;
  return iota(size_t{0}, size) | map([](size_t index) { return static_cast<int32_t>((index % 4) + 1); }) | to_vector();
}

template <class T> inline std::vector<T> referenceMerge(const std::vector<T> &left, const std::vector<T> &right) {
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

template <class T> inline std::vector<T> referenceSetUnion(const std::vector<T> &left, const std::vector<T> &right) {
  std::vector<T> result;
  size_t a = 0;
  size_t b = 0;
  while (a < left.size() || b < right.size()) {
    if (a == left.size()) {
      result.push_back(right[b++]);
    } else if (b == right.size()) {
      result.push_back(left[a++]);
    } else if (left[a] < right[b]) {
      result.push_back(left[a++]);
    } else if (right[b] < left[a]) {
      result.push_back(right[b++]);
    } else {
      result.push_back(left[a]);
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T> inline std::vector<T> referenceSetIntersection(const std::vector<T> &left, const std::vector<T> &right) {
  std::vector<T> result;
  size_t a = 0;
  size_t b = 0;
  while (a < left.size() && b < right.size()) {
    if (left[a] < right[b]) {
      ++a;
    } else if (right[b] < left[a]) {
      ++b;
    } else {
      result.push_back(left[a]);
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T, class Backend> void copy(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  REQUIRE(backend.copy(input) == input);
}

template <class T, class Backend> void copyIf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto expected = input ^ filter(IsEven<T>{});
  REQUIRE(backend.copyIf(input) == expected);
}

template <class T, class Backend> void copyN(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto count = size / 2;
  REQUIRE(backend.copyN(input, count) == (std::vector<T>(input.begin(), input.begin() + count)));
}

template <class T, class Backend> void uninitializedCopy(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  REQUIRE(backend.uninitializedCopy(input) == input);
}

template <class T, class Backend> void uninitializedFill(Backend &backend, size_t size) {
  REQUIRE(backend.template uninitializedFill<T>(size, T{7}) == std::vector<T>(size, T{7}));
}

template <class T, class Backend> void fill(Backend &backend, size_t size) {
  REQUIRE(backend.template fill<T>(size, T{7}) == std::vector<T>(size, T{7}));
}

template <class T, class Backend> void generate(Backend &backend, size_t size) {
  REQUIRE(backend.template generate<T>(size) == std::vector<T>(size, T{7}));
}

template <class T, class Backend> void forEach(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  auto expected = input;
  for (auto &value : expected)
    Increment<T>{}(value);
  REQUIRE(backend.forEach(input) == expected);
}

template <class T, class Backend> void transformUnary(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto expected = input ^ map(Affine<T>{});
  REQUIRE(backend.transformUnary(input) == expected);
}

template <class T, class Backend> void transformBinary(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto right = values<T>(size, 7);
  const auto expected = input | zip(right) | map(Combine<T>{}) | to_vector();
  REQUIRE(backend.transformBinary(input, right) == expected);
}

template <class T, class Backend> void replaceIf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto expected = input ^ map([](T value) { return IsEven<T>{}(value) ? T{9} : value; });
  const auto actual = backend.replaceIf(input, T{9});
  REQUIRE(actual == expected);
}

template <class T, class Backend> void replaceCopyIf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto expected = input ^ map([](T value) { return IsEven<T>{}(value) ? T{9} : value; });
  const auto actual = backend.replaceCopyIf(input, T{9});
  REQUIRE(actual == expected);
}

template <class T, class Backend> void removeIf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto expected = input ^ filter([](T value) { return !IsEven<T>{}(value); });
  const auto actual = backend.removeIf(input);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void removeCopyIf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto expected = input ^ filter([](T value) { return !IsEven<T>{}(value); });
  const auto actual = backend.removeCopyIf(input);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void reverse(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  REQUIRE(backend.reverse(input) == (input ^ aspartame::reverse()));
}

template <class T, class Backend> void reverseCopy(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  REQUIRE(backend.reverseCopy(input) == (input ^ aspartame::reverse()));
}

template <class T, class Backend> void shuffle(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto expected = input ^ aspartame::sort();
  const auto actual = backend.shuffle(input) ^ aspartame::sort();
  REQUIRE(actual == expected);
}

template <class T, class Backend> void swapRanges(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto right = values<T>(size, 7);
  const auto actual = backend.swapRanges(input, right);
  REQUIRE(actual.first == right);
  REQUIRE(actual.second == input);
}

template <class T, class Backend> void reduce(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.reduce(input) == (input ^ fold_left(T{}, Plus<T>{})));
}

template <class T, class Backend> void transformReduce(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.transformReduce(input) == (input | map(Square<T>{}) | fold_left(T{}, Plus<T>{})));
}

template <class T, class Backend> void innerProduct(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto right = values<T>(size, 7);
  const auto expected = input | zip(right) | map([](T a, T b) { return static_cast<T>(a * b); }) | fold_left(T{}, Plus<T>{});
  REQUIRE(backend.innerProduct(input, right) == expected);
}

template <class T, class Backend> void inclusiveScan(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.inclusiveScan(input) == (input | scan_left(T{}, Plus<T>{}) | tail() | to_vector()));
}

template <class T, class Backend> void exclusiveScan(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.exclusiveScan(input) == (input ^ scan_left(T{}, Plus<T>{}) ^ init()));
}

template <class T, class Backend> void transformInclusiveScan(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.transformInclusiveScan(input) == (input | map(Square<T>{}) | scan_left(T{}, Plus<T>{}) | tail() | to_vector()));
}

template <class T, class Backend> void transformExclusiveScan(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.transformExclusiveScan(input) == (input ^ map(Square<T>{}) ^ scan_left(T{}, Plus<T>{}) ^ init()));
}

template <class T, class Backend> void adjacentDifference(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  std::vector<T> expected(size);
  if (!input.empty()) expected[0] = input[0];
  for (size_t index = 1; index < size; ++index)
    expected[index] = static_cast<T>(input[index] - input[index - 1]);
  REQUIRE(backend.adjacentDifference(input) == expected);
}

template <class T, class Backend> void sort(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  REQUIRE(backend.sort(input) == (input ^ aspartame::sort()));
}

template <class T, class Backend> void stableSort(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  auto expected = input;
  std::stable_sort(expected.begin(), expected.end());
  REQUIRE(backend.stableSort(input) == expected);
}

template <class T, class Backend> void sortDescending(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  REQUIRE(backend.sortDescending(input) == (input ^ aspartame::sort(Greater<T>{})));
}

template <class T, class Backend> void sortByKey(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto keys = values<T>(size, 4) | zip_with_index()
                    | map([&](T key, size_t index) { return static_cast<T>((key * static_cast<T>(size + 1)) + static_cast<T>(index)); })
                    | to_vector();
  const auto actual = backend.sortByKey(keys, support::payload(size));
  REQUIRE((actual.keys ^ is_sorted()));
  REQUIRE_THAT(actual.keys, unified::KeyPermutation(actual.values, keys));
}

template <class T, class Backend> void stableSortByKey(Backend &backend, size_t size) {
  const auto keys = duplicateKeys<T>(size);
  const auto actual = backend.stableSortByKey(keys, support::payload(size));
  REQUIRE((actual.keys ^ aspartame::is_sorted()));
  REQUIRE_THAT(actual.keys, unified::KeyPermutation(actual.values, keys));
  for (size_t index = 0; index < size; ++index) {
    if (index != 0 && actual.keys[index] == actual.keys[index - 1]) REQUIRE(actual.values[index - 1] < actual.values[index]);
  }
}

template <class T, class Backend> void unique(Backend &backend, size_t size) {
  const auto repeated = duplicateKeys<T>(size) ^ aspartame::sort();
  auto expected = repeated;
  expected.erase(std::unique(expected.begin(), expected.end()), expected.end());
  REQUIRE(backend.unique(repeated) == expected);
}

template <class T, class Backend> void uniqueCopy(Backend &backend, size_t size) {
  const auto repeated = duplicateKeys<T>(size) ^ aspartame::sort();
  auto expected = repeated;
  expected.erase(std::unique(expected.begin(), expected.end()), expected.end());
  REQUIRE(backend.uniqueCopy(repeated) == expected);
}

template <class T, class Backend> void count(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const T needle = T{1};
  const auto expected = static_cast<size_t>(std::count(input.begin(), input.end(), needle));
  REQUIRE(backend.count(input, needle) == expected);
}

template <class T, class Backend> void countIf(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  REQUIRE(backend.countIf(input) == (input ^ aspartame::count(IsEven<T>{})));
}

template <class T, class Backend> void find(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const T needle = T{1};
  const auto expected = input ^ index_of_maybe(needle) ^ get_or_else(input.size());
  REQUIRE(backend.find(input, needle) == expected);
}

template <class T, class Backend> void findIf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.findIf(input) == (input ^ index_where_maybe(IsEven<T>{}) ^ get_or_else(size)));
}

template <class T, class Backend> void allOf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.allOf(input) == (input ^ forall(IsEven<T>{})));
}

template <class T, class Backend> void anyOf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.anyOf(input) == (input ^ exists(IsEven<T>{})));
}

template <class T, class Backend> void noneOf(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  REQUIRE(backend.noneOf(input) == (input ^ none_match(IsEven<T>{})));
}

template <class T, class Backend> void equal(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  auto right = input;
  if (!right.empty()) right.back() = static_cast<T>(right.back() + T{1});
  REQUIRE(backend.equal(input, right) == (input == right));
}

template <class T, class Backend> void mismatch(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  auto right = input;
  if (!right.empty()) right[size / 2] = static_cast<T>(right[size / 2] + T{1});
  const auto expectedPair = std::mismatch(input.begin(), input.end(), right.begin());
  const TwoPositions expected{.first = static_cast<size_t>(expectedPair.first - input.begin()),
                              .second = static_cast<size_t>(expectedPair.second - right.begin())};
  REQUIRE(backend.mismatch(input, right) == expected);
}

template <class T, class Backend> void lowerBound(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto sorted = input ^ aspartame::sort();
  const T needle = T{1};
  const auto expected = sorted ^ index_where_maybe([&](T value) { return !(value < needle); }) ^ get_or_else(sorted.size());
  REQUIRE(backend.lowerBound(sorted, needle) == expected);
}

template <class T, class Backend> void upperBound(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto sorted = input ^ aspartame::sort();
  const T needle = T{1};
  const auto expected = static_cast<size_t>(std::upper_bound(sorted.begin(), sorted.end(), needle) - sorted.begin());
  REQUIRE(backend.upperBound(sorted, needle) == expected);
}

template <class T, class Backend> void binarySearch(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto sorted = input ^ aspartame::sort();
  const T needle = T{1};
  REQUIRE(backend.binarySearch(sorted, needle) == std::binary_search(sorted.begin(), sorted.end(), needle));
}

template <class T, class Backend> void equalRange(Backend &backend, size_t size) {
  const auto sorted = duplicateKeys<T>(size) ^ aspartame::sort();
  const T needle = T{3};
  const auto expectedPair = std::equal_range(sorted.begin(), sorted.end(), needle);
  const TwoPositions expected{.first = static_cast<size_t>(expectedPair.first - sorted.begin()),
                              .second = static_cast<size_t>(expectedPair.second - sorted.begin())};
  REQUIRE(backend.equalRange(sorted, needle) == expected);
}

template <class T, class Backend> void merge(Backend &backend, size_t size) {
  const auto left = sortedValues<T>((size + 1) / 2, 2);
  const auto right = sortedValues<T>(size / 2, 3);
  REQUIRE(backend.merge(left, right) == referenceMerge(left, right));
}

template <class T, class Backend> void partition(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto actual = backend.partition(input);
  REQUIRE(actual.boundary <= actual.values.size());
  for (size_t index = 0; index < actual.boundary; ++index)
    REQUIRE(IsEven<T>{}(actual.values[index]));
  for (size_t index = actual.boundary; index < actual.values.size(); ++index)
    REQUIRE(!IsEven<T>{}(actual.values[index]));
  REQUIRE((actual.values ^ aspartame::sort()) == (input ^ aspartame::sort()));
}

template <class T, class Backend> void stablePartition(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto actual = backend.stablePartition(input);
  const auto [selected, rejected] = input ^ aspartame::partition(IsEven<T>{});
  REQUIRE(actual.values == (selected ^ concat(rejected)));
  REQUIRE(actual.boundary == selected.size());
}

template <class T, class Backend> void partitionCopy(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto [selected, rejected] = input ^ aspartame::partition(IsEven<T>{});
  const auto actual = backend.partitionCopy(input);
  REQUIRE(actual.first == selected);
  REQUIRE(actual.second == rejected);
}

template <class T, class Backend> void isPartitioned(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  auto partitioned = input;
  std::partition(partitioned.begin(), partitioned.end(), IsEven<T>{});
  REQUIRE(backend.isPartitioned(partitioned) == true);
}

template <class T, class Backend> void isPartitionedFalse(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  if (size < 3) {
    SUCCEED("is-partitioned-false is not applicable below three values");
    return;
  }
  auto notPartitioned = input;
  notPartitioned[0] = T{2};
  notPartitioned[1] = T{1};
  notPartitioned[2] = T{4};
  REQUIRE(backend.isPartitioned(notPartitioned) == false);
}

template <class T, class Backend> void partitionPoint(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  auto partitioned = input;
  const auto point = std::partition(partitioned.begin(), partitioned.end(), IsEven<T>{});
  const auto actual = backend.partitionPoint(partitioned);
  REQUIRE(actual == static_cast<size_t>(point - partitioned.begin()));
}

template <class T, class Backend> void setUnion(Backend &backend, size_t size) {
  const auto left = sortedValues<T>((size + 1) / 2, 2);
  const auto right = sortedValues<T>(size / 2, 3);
  REQUIRE(backend.setUnion(left, right) == referenceSetUnion(left, right));
}

template <class T, class Backend> void setIntersection(Backend &backend, size_t size) {
  const auto left = sortedValues<T>((size + 1) / 2, 2);
  const auto right = sortedValues<T>(size / 2, 3);
  REQUIRE(backend.setIntersection(left, right) == referenceSetIntersection(left, right));
}

template <class T, class Backend> void setDifference(Backend &backend, size_t size) {
  const auto left = sortedValues<T>((size + 1) / 2, 2);
  const auto right = sortedValues<T>(size / 2, 3);
  std::vector<T> expected(left.size());
  expected.erase(std::set_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin()), expected.end());
  REQUIRE(backend.setDifference(left, right) == expected);
}

template <class T, class Backend> void setSymmetricDifference(Backend &backend, size_t size) {
  const auto left = sortedValues<T>((size + 1) / 2, 2);
  const auto right = sortedValues<T>(size / 2, 3);
  std::vector<T> expected(left.size() + right.size());
  expected.erase(std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin()), expected.end());
  REQUIRE(backend.setSymmetricDifference(left, right) == expected);
}

template <class T, class Backend> void minElement(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  auto extrema = input;
  if (!extrema.empty()) extrema[size / 3] = T{-100};
  REQUIRE(backend.minElement(extrema) == (extrema ^ index_of_min() ^ get_or_else(size)));
}

template <class T, class Backend> void maxElement(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  auto extrema = input;
  if (!extrema.empty()) extrema[(size * 2) / 3] = T{100};
  REQUIRE(backend.maxElement(extrema) == (extrema ^ index_of_max() ^ get_or_else(size)));
}

template <class T, class Backend> void minmaxElement(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  auto extrema = input;
  if (extrema.size() == 1) {
    extrema[0] = T{0};
  } else if (!extrema.empty()) {
    extrema[size / 3] = T{-100};
    extrema[(size * 2) / 3] = T{100};
  }
  const auto pair = std::minmax_element(extrema.begin(), extrema.end());
  const TwoPositions expected{.first = static_cast<size_t>(pair.first - extrema.begin()),
                              .second = static_cast<size_t>(pair.second - extrema.begin())};
  REQUIRE(backend.minmaxElement(extrema) == expected);
}

template <class T, class Backend> void isSorted(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  REQUIRE(backend.isSorted(input ^ aspartame::sort()) == true);
}

template <class T, class Backend> void isSortedFalse(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  if (size < 2) {
    SUCCEED("is-sorted-false is not applicable below two values");
    return;
  }
  auto notSorted = input;
  notSorted[0] = T{1};
  notSorted[1] = T{0};
  REQUIRE(backend.isSorted(notSorted) == false);
}

template <class T, class Backend> void isSortedUntil(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  auto nearly = input ^ aspartame::sort();
  if (nearly.size() > 2) std::swap(nearly[nearly.size() / 2], nearly.back());
  const auto expected = static_cast<size_t>(std::is_sorted_until(nearly.begin(), nearly.end()) - nearly.begin());
  REQUIRE(backend.isSortedUntil(nearly) == expected);
}

template <class T, class Backend> void rotate(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto middle = size / 3;
  auto expected = input;
  std::rotate(expected.begin(), expected.begin() + static_cast<ptrdiff_t>(middle), expected.end());
  const auto actual = backend.rotate(input, middle);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void rotateCopy(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto middle = size / 3;
  auto expected = input;
  std::rotate(expected.begin(), expected.begin() + static_cast<ptrdiff_t>(middle), expected.end());
  const auto actual = backend.rotateCopy(input, middle);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void includes(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto left = input ^ aspartame::sort();
  const auto right = left ^ stride(3);
  const bool actual = backend.includes(left, right);
  REQUIRE(actual);
}

template <class T, class Backend> void includesFalse(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto left = input ^ aspartame::sort();
  const std::vector<T> right{T{100}};
  const bool actual = backend.includes(left, right);
  REQUIRE_FALSE(actual);
}

template <class T, class Backend> void gather(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto indices = iota(size_t{0}, size) | map(support::ReverseIndex{size}) | to_vector();
  const auto actual = backend.gather(input, indices);
  REQUIRE(actual == (input ^ aspartame::reverse()));
}

template <class T, class Backend> void scatter(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto input = values<T>(size, 1);

  const auto indices = iota(size_t{0}, size) | map(support::ReverseIndex{size}) | to_vector();
  const auto actual = backend.scatter(input, indices);
  REQUIRE(actual == (input ^ aspartame::reverse()));
}

template <class T, class Backend> void reduceByKey(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto keys = groupedKeys<T>(size);
  const auto values = cyclicValues(size);
  KeyValues<T> expected;

  for (size_t index = 0; index < size; ++index) {
    if (expected.keys.empty() || expected.keys.back() != keys[index]) {
      expected.keys.push_back(keys[index]);
      expected.values.push_back(values[index]);
    } else {
      expected.values.back() += values[index];
    }
  }

  const auto actual = backend.reduceByKey(keys, values);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void inclusiveScanByKey(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto keys = groupedKeys<T>(size);
  const auto values = cyclicValues(size);
  KeyValues<T> expected;

  expected = {keys, values};
  for (size_t index = 1; index < size; ++index)
    if (keys[index] == keys[index - 1]) expected.values[index] += expected.values[index - 1];

  const auto actual = backend.inclusiveScanByKey(keys, values);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void exclusiveScanByKey(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto keys = groupedKeys<T>(size);
  const auto values = cyclicValues(size);
  KeyValues<T> expected;

  expected = {keys, std::vector<int32_t>(size)};
  int32_t running = 0;
  for (size_t index = 0; index < size; ++index) {
    if (index == 0 || keys[index] != keys[index - 1]) running = 0;
    expected.values[index] = running;
    running += values[index];
  }

  const auto actual = backend.exclusiveScanByKey(keys, values);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void uniqueByKey(Backend &backend, size_t size) {
  using namespace aspartame;
  const auto keys = groupedKeys<T>(size);
  const auto values = cyclicValues(size);
  KeyValues<T> expected;

  for (size_t index = 0; index < size; ++index)
    if (index == 0 || keys[index] != keys[index - 1]) {
      expected.keys.push_back(keys[index]);
      expected.values.push_back(values[index]);
    }

  const auto actual = backend.uniqueByKey(keys, values);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void mergeByKey(Backend &backend, size_t size) {
  KeyValues<T> left{sortedValues<T>((size + 1) / 2, 2), {}};
  KeyValues<T> right{sortedValues<T>(size / 2, 3), {}};
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
    const bool takeLeft = b == right.keys.size() || (a < left.keys.size() && !(right.keys[b] < left.keys[a]));
    if (takeLeft) {
      expected.keys.push_back(left.keys[a]);
      expected.values.push_back(left.values[a++]);
    } else {
      expected.keys.push_back(right.keys[b]);
      expected.values.push_back(right.values[b++]);
    }
  }
  const auto actual = backend.mergeByKey(left, right);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void partialSort(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto middle = size / 2;
  const auto actual = backend.partialSort(input, middle);
  const auto fullySorted = input ^ aspartame::sort();
  REQUIRE(std::equal(actual.begin(), actual.begin() + static_cast<ptrdiff_t>(middle), fullySorted.begin()));
  REQUIRE((actual ^ aspartame::sort()) == fullySorted);
}

template <class T, class Backend> void nthElement(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto nth = size / 2;
  const auto actual = backend.nthElement(input, nth);
  const auto fullySorted = input ^ aspartame::sort();
  if (!actual.empty()) REQUIRE(actual[nth] == fullySorted[nth]);
  REQUIRE((actual ^ aspartame::sort()) == fullySorted);
}

template <class T, class Backend> void inplaceMerge(Backend &backend, size_t size) {
  const auto input = values<T>(size, 1);

  const auto middle = size / 2;
  auto halves = input;
  std::sort(halves.begin(), halves.begin() + static_cast<ptrdiff_t>(middle));
  std::sort(halves.begin() + static_cast<ptrdiff_t>(middle), halves.end());
  auto expected = halves;
  std::inplace_merge(expected.begin(), expected.begin() + static_cast<ptrdiff_t>(middle), expected.end());
  const auto actual = backend.inplaceMerge(halves, middle);
  REQUIRE(actual == expected);
}
} // namespace unified::algorithms
