#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <iterator>
#include <memory>
#include <numeric>
#include <random>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_template_test_macros.hpp>

#include "cases/algorithms.inl"

using namespace aspartame;

namespace unified::test { namespace {
class ReferenceAlgorithms {
public:
  template <class T> std::vector<T> copy(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::copy(input.begin(), input.end(), output.begin());
    return output;
  }

  template <class T> std::vector<T> copyIf(const std::vector<T> &input) { return input ^ filter(algorithms::IsEven<T>{}); }

  template <class T> std::vector<T> copyN(const std::vector<T> &input, size_t count) {
    std::vector<T> output(count);
    std::copy_n(input.begin(), count, output.begin());
    return output;
  }

  template <class T> std::vector<T> uninitializedCopy(const std::vector<T> &input) {
    if (input.empty()) return {};
    std::allocator<T> allocator;
    T *storage = allocator.allocate(input.size());
    std::uninitialized_copy(input.begin(), input.end(), storage);
    std::vector<T> output(storage, storage + input.size());
    std::destroy_n(storage, input.size());
    allocator.deallocate(storage, input.size());
    return output;
  }

  template <class T> std::vector<T> uninitializedFill(size_t size, T value) {
    if (size == 0) return {};
    std::allocator<T> allocator;
    T *storage = allocator.allocate(size);
    std::uninitialized_fill(storage, storage + size, value);
    std::vector<T> output(storage, storage + size);
    std::destroy_n(storage, size);
    allocator.deallocate(storage, size);
    return output;
  }

  template <class T> std::vector<T> fill(size_t size, T value) {
    std::vector<T> output(size);
    std::fill(output.begin(), output.end(), value);
    return output;
  }

  template <class T> std::vector<T> generate(size_t size) {
    std::vector<T> output(size);
    std::generate(output.begin(), output.end(), algorithms::Constant<T>{});
    return output;
  }

  template <class T> std::vector<T> forEach(const std::vector<T> &input) {
    auto output = input;
    std::for_each(output.begin(), output.end(), algorithms::Increment<T>{});
    return output;
  }

  template <class T> std::vector<T> transformUnary(const std::vector<T> &input) { return input ^ map(algorithms::Affine<T>{}); }

  template <class T> std::vector<T> transformBinary(const std::vector<T> &left, const std::vector<T> &right) {
    return left ^ zip(right) ^ map(algorithms::Combine<T>{});
  }

  template <class T> std::vector<T> replaceIf(const std::vector<T> &input, T replacement) {
    return input ^ map([&](T value) { return algorithms::IsEven<T>{}(value) ? replacement : value; });
  }

  template <class T> std::vector<T> replaceCopyIf(const std::vector<T> &input, T replacement) {
    return input ^ map([&](T value) { return algorithms::IsEven<T>{}(value) ? replacement : value; });
  }

  template <class T> std::vector<T> removeIf(const std::vector<T> &input) {
    return input ^ filter([](T value) { return !algorithms::IsEven<T>{}(value); });
  }

  template <class T> std::vector<T> removeCopyIf(const std::vector<T> &input) {
    return input ^ filter([](T value) { return !algorithms::IsEven<T>{}(value); });
  }

  template <class T> algorithms::TwoRanges<T> swapRanges(const std::vector<T> &left, const std::vector<T> &right) {
    auto a = left;
    auto b = right;
    std::swap_ranges(a.begin(), a.end(), b.begin());
    return {std::move(a), std::move(b)};
  }

  template <class T> T reduce(const std::vector<T> &input) { return input ^ fold_left(T{}, algorithms::Plus<T>{}); }

  template <class T> T transformReduce(const std::vector<T> &input) {
    return input ^ map(algorithms::Square<T>{}) ^ fold_left(T{}, algorithms::Plus<T>{});
  }

  template <class T> T innerProduct(const std::vector<T> &left, const std::vector<T> &right) {
    return std::inner_product(left.begin(), left.end(), right.begin(), T{});
  }

  template <class T> std::vector<T> inclusiveScan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::inclusive_scan(input.begin(), input.end(), output.begin(), algorithms::Plus<T>{});
    return output;
  }

  template <class T> std::vector<T> exclusiveScan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::exclusive_scan(input.begin(), input.end(), output.begin(), T{}, algorithms::Plus<T>{});
    return output;
  }

  template <class T> std::vector<T> transformInclusiveScan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform_inclusive_scan(input.begin(), input.end(), output.begin(), algorithms::Plus<T>{}, algorithms::Square<T>{});
    return output;
  }

  template <class T> std::vector<T> transformExclusiveScan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform_exclusive_scan(input.begin(), input.end(), output.begin(), T{}, algorithms::Plus<T>{}, algorithms::Square<T>{});
    return output;
  }

  template <class T> std::vector<T> sort(const std::vector<T> &input) {
    auto output = input;
    std::sort(output.begin(), output.end());
    return output;
  }

  template <class T> std::vector<T> stableSort(const std::vector<T> &input) {
    auto output = input;
    std::stable_sort(output.begin(), output.end());
    return output;
  }

  template <class T> std::vector<T> sortDescending(const std::vector<T> &input) {
    auto output = input;
    std::sort(output.begin(), output.end(), algorithms::Greater<T>{});
    return output;
  }

  template <class T> algorithms::KeyValues<T> sortByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    return orderByKey(keys, values, false);
  }

  template <class T> algorithms::KeyValues<T> stableSortByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    return orderByKey(keys, values, true);
  }

  template <class T> std::vector<T> unique(const std::vector<T> &input) {
    auto output = input;
    output.erase(std::unique(output.begin(), output.end()), output.end());
    return output;
  }

  template <class T> std::vector<T> uniqueCopy(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    const auto end = std::unique_copy(input.begin(), input.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> size_t count(const std::vector<T> &input, T value) {
    return static_cast<size_t>(std::count(input.begin(), input.end(), value));
  }

  template <class T> size_t countIf(const std::vector<T> &input) { return input ^ aspartame::count(algorithms::IsEven<T>{}); }

  template <class T> size_t findIf(const std::vector<T> &input) {
    return input ^ index_where_maybe(algorithms::IsEven<T>{}) ^ get_or_else(input.size());
  }

  template <class T> size_t find(const std::vector<T> &input, T value) { return input ^ index_of_maybe(value) ^ get_or_else(input.size()); }

  template <class T> bool allOf(const std::vector<T> &input) { return input ^ forall(algorithms::IsEven<T>{}); }

  template <class T> bool anyOf(const std::vector<T> &input) { return input ^ exists(algorithms::IsEven<T>{}); }

  template <class T> bool noneOf(const std::vector<T> &input) { return input ^ none_match(algorithms::IsEven<T>{}); }

  template <class T> bool equal(const std::vector<T> &left, const std::vector<T> &right) {
    return std::equal(left.begin(), left.end(), right.begin());
  }

  template <class T> algorithms::TwoPositions mismatch(const std::vector<T> &left, const std::vector<T> &right) {
    const auto result = std::mismatch(left.begin(), left.end(), right.begin());
    return {.first = static_cast<size_t>(result.first - left.begin()), .second = static_cast<size_t>(result.second - right.begin())};
  }

  template <class T> size_t lowerBound(const std::vector<T> &input, T value) {
    return static_cast<size_t>(std::distance(input.begin(), std::lower_bound(input.begin(), input.end(), value)));
  }

  template <class T> size_t upperBound(const std::vector<T> &input, T value) {
    return static_cast<size_t>(std::distance(input.begin(), std::upper_bound(input.begin(), input.end(), value)));
  }

  template <class T> bool binarySearch(const std::vector<T> &input, T value) {
    return std::binary_search(input.begin(), input.end(), value);
  }

  template <class T> algorithms::TwoPositions equalRange(const std::vector<T> &input, T value) {
    const auto result = std::equal_range(input.begin(), input.end(), value);
    return {.first = static_cast<size_t>(result.first - input.begin()), .second = static_cast<size_t>(result.second - input.begin())};
  }

  template <class T> std::vector<T> merge(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size() + right.size());
    std::merge(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    return output;
  }

  template <class T> algorithms::Partitioned<T> partition(const std::vector<T> &input) {
    auto output = input;
    const auto boundary = std::partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    const auto position = static_cast<size_t>(std::distance(output.begin(), boundary));
    return {std::move(output), position};
  }

  template <class T> algorithms::Partitioned<T> stablePartition(const std::vector<T> &input) {
    auto output = input;
    const auto boundary = std::stable_partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    const auto position = static_cast<size_t>(boundary - output.begin());
    return {std::move(output), position};
  }

  template <class T> algorithms::TwoRanges<T> partitionCopy(const std::vector<T> &input) {
    std::vector<T> selected(input.size());
    std::vector<T> rejected(input.size());
    const auto ends = std::partition_copy(input.begin(), input.end(), selected.begin(), rejected.begin(), algorithms::IsEven<T>{});
    selected.erase(ends.first, selected.end());
    rejected.erase(ends.second, rejected.end());
    return {std::move(selected), std::move(rejected)};
  }

  template <class T> bool isPartitioned(const std::vector<T> &input) {
    return std::is_partitioned(input.begin(), input.end(), algorithms::IsEven<T>{});
  }

  template <class T> size_t partitionPoint(const std::vector<T> &input) {
    return static_cast<size_t>(std::partition_point(input.begin(), input.end(), algorithms::IsEven<T>{}) - input.begin());
  }

  template <class T> std::vector<T> adjacentDifference(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::adjacent_difference(input.begin(), input.end(), output.begin());
    return output;
  }

  template <class T> std::vector<T> setUnion(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size() + right.size());
    const auto end = std::set_union(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> setIntersection(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(std::min(left.size(), right.size()));
    const auto end = std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> setDifference(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size());
    const auto end = std::set_difference(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> setSymmetricDifference(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size() + right.size());
    const auto end = std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> reverse(const std::vector<T> &input) { return input ^ aspartame::reverse(); }

  template <class T> std::vector<T> reverseCopy(const std::vector<T> &input) { return input ^ aspartame::reverse(); }

  template <class T> std::vector<T> shuffle(const std::vector<T> &input) {
    auto output = input;
    std::mt19937 generator(17);
    std::shuffle(output.begin(), output.end(), generator);
    return output;
  }

  template <class T> size_t minElement(const std::vector<T> &input) { return input ^ index_of_min() ^ get_or_else(size_t{0}); }

  template <class T> size_t maxElement(const std::vector<T> &input) { return input ^ index_of_max() ^ get_or_else(size_t{0}); }

  template <class T> algorithms::TwoPositions minmaxElement(const std::vector<T> &input) {
    const auto result = std::minmax_element(input.begin(), input.end());
    return {.first = static_cast<size_t>(result.first - input.begin()), .second = static_cast<size_t>(result.second - input.begin())};
  }

  template <class T> bool isSorted(const std::vector<T> &input) { return input ^ is_sorted(); }

  template <class T> size_t isSortedUntil(const std::vector<T> &input) {
    return static_cast<size_t>(std::is_sorted_until(input.begin(), input.end()) - input.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &input, size_t middle) {
    auto output = input;
    std::rotate(output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return output;
  }

  template <class T> std::vector<T> rotateCopy(const std::vector<T> &input, size_t middle) {
    std::vector<T> output(input.size());
    std::rotate_copy(input.begin(), input.begin() + static_cast<ptrdiff_t>(middle), input.end(), output.begin());
    return output;
  }

  template <class T> bool includes(const std::vector<T> &left, const std::vector<T> &right) {
    return std::includes(left.begin(), left.end(), right.begin(), right.end());
  }

  template <class T> std::vector<T> gather(const std::vector<T> &input, const std::vector<size_t> &indices) {
    return indices ^ map([&](size_t index) { return input[index]; });
  }

  template <class T> std::vector<T> scatter(const std::vector<T> &input, const std::vector<size_t> &indices) {
    std::vector<T> output(input.size());
    for (size_t index = 0; index < indices.size(); ++index)
      output[indices[index]] = input[index];
    return output;
  }

  template <class T> algorithms::KeyValues<T> reduceByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    algorithms::KeyValues<T> output;
    for (size_t index = 0; index < keys.size(); ++index) {
      if (output.keys.empty() || output.keys.back() != keys[index]) {
        output.keys.push_back(keys[index]);
        output.values.push_back(values[index]);
      } else {
        output.values.back() += values[index];
      }
    }
    return output;
  }

  template <class T> algorithms::KeyValues<T> inclusiveScanByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    algorithms::KeyValues<T> output{keys, values};
    for (size_t index = 1; index < keys.size(); ++index)
      if (keys[index] == keys[index - 1]) output.values[index] += output.values[index - 1];
    return output;
  }

  template <class T> algorithms::KeyValues<T> exclusiveScanByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    algorithms::KeyValues<T> output{keys, std::vector<int32_t>(values.size())};
    int32_t running = 0;
    for (size_t index = 0; index < keys.size(); ++index) {
      if (index == 0 || keys[index] != keys[index - 1]) running = 0;
      output.values[index] = running;
      running += values[index];
    }
    return output;
  }

  template <class T> algorithms::KeyValues<T> uniqueByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    algorithms::KeyValues<T> output;
    for (size_t index = 0; index < keys.size(); ++index)
      if (index == 0 || keys[index] != keys[index - 1]) {
        output.keys.push_back(keys[index]);
        output.values.push_back(values[index]);
      }
    return output;
  }

  template <class T> algorithms::KeyValues<T> mergeByKey(const algorithms::KeyValues<T> &left, const algorithms::KeyValues<T> &right) {
    algorithms::KeyValues<T> output;
    size_t a = 0;
    size_t b = 0;
    while (a < left.keys.size() || b < right.keys.size()) {
      const bool takeLeft = b == right.keys.size() || (a < left.keys.size() && !(right.keys[b] < left.keys[a]));
      if (takeLeft) {
        output.keys.push_back(left.keys[a]);
        output.values.push_back(left.values[a++]);
      } else {
        output.keys.push_back(right.keys[b]);
        output.values.push_back(right.values[b++]);
      }
    }
    return output;
  }

  template <class T> std::vector<T> partialSort(const std::vector<T> &input, size_t middle) {
    auto output = input;
    std::partial_sort(output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return output;
  }

  template <class T> std::vector<T> nthElement(const std::vector<T> &input, size_t nth) {
    auto output = input;
    if (!output.empty()) std::nth_element(output.begin(), output.begin() + static_cast<ptrdiff_t>(nth), output.end());
    return output;
  }

  template <class T> std::vector<T> inplaceMerge(const std::vector<T> &input, size_t middle) {
    auto output = input;
    std::inplace_merge(output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return output;
  }

private:
  template <class T>
  static algorithms::KeyValues<T> orderByKey(const std::vector<T> &keys, const std::vector<int32_t> &values, bool stable) {
    std::vector<size_t> order(keys.size());
    std::iota(order.begin(), order.end(), 0);
    const auto compare = [&](size_t left, size_t right) { return keys[left] < keys[right]; };
    if (stable) std::stable_sort(order.begin(), order.end(), compare);
    else std::sort(order.begin(), order.end(), compare);
    auto [result_keys, result_values] = order ^ map([&](size_t index) { return std::pair{keys[index], values[index]}; }) ^ unzip();
    return {std::move(result_keys), std::move(result_values)};
  }
};
}} // namespace unified::test

#define REFERENCE_ALGORITHM(name, function, ...)                                                                                           \
  TEMPLATE_TEST_CASE("reference algorithms/" #name, "[unit][unified]", int8_t, int32_t, int64_t, float, double) {                          \
    const auto size = GENERATE(0, 1, 7, 33, 257);                                                                                          \
    CAPTURE(size);                                                                                                                         \
    unified::test::ReferenceAlgorithms backend;                                                                                            \
    unified::algorithms::function<TestType>(backend, size);                                                                                \
  }

UNIFIED_ALGORITHM_CASES(REFERENCE_ALGORITHM)
