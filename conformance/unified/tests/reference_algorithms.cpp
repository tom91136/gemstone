
#include <algorithm>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <memory>
#include <numeric>
#include <random>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_template_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "algorithm_cases.hpp"
#include "async_cases.hpp"
#include "blas_cases.hpp"
#include "capability_defaults.hpp"
#include "edge_cases.hpp"
#include "reference_async.hpp"
#include "stress_cases.hpp"
#include <magic_enum/magic_enum.hpp>

using namespace aspartame;

namespace unified::test {
class ReferenceAlgorithms : public unified::backend::AlgorithmCapabilities<ReferenceAlgorithms> {
public:
  template <class T> bool capability() const { return true; }

  template <class T> std::vector<T> copy(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::copy(input.begin(), input.end(), output.begin());
    return output;
  }

  template <class T> std::vector<T> copy_if(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    const auto end = std::copy_if(input.begin(), input.end(), output.begin(), algorithms::IsEven<T>{});
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> copy_n(const std::vector<T> &input, size_t count) {
    std::vector<T> output(count);
    std::copy_n(input.begin(), count, output.begin());
    return output;
  }

  template <class T> std::vector<T> uninitialized_copy(const std::vector<T> &input) {
    if (input.empty()) return {};
    std::allocator<T> allocator;
    T *storage = allocator.allocate(input.size());
    std::uninitialized_copy(input.begin(), input.end(), storage);
    std::vector<T> output(storage, storage + input.size());
    std::destroy_n(storage, input.size());
    allocator.deallocate(storage, input.size());
    return output;
  }

  template <class T> std::vector<T> uninitialized_fill(size_t size, T value) {
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

  template <class T> std::vector<T> for_each(const std::vector<T> &input) {
    auto output = input;
    std::for_each(output.begin(), output.end(), algorithms::Increment<T>{});
    return output;
  }

  template <class T> std::vector<T> transform_unary(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform(input.begin(), input.end(), output.begin(), algorithms::Affine<T>{});
    return output;
  }

  template <class T> std::vector<T> transform_binary(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size());
    std::transform(left.begin(), left.end(), right.begin(), output.begin(), algorithms::Combine<T>{});
    return output;
  }

  template <class T> std::vector<T> replace_if(const std::vector<T> &input, T replacement) {
    auto output = input;
    std::replace_if(output.begin(), output.end(), algorithms::IsEven<T>{}, replacement);
    return output;
  }

  template <class T> std::vector<T> replace_copy_if(const std::vector<T> &input, T replacement) {
    std::vector<T> output(input.size());
    std::replace_copy_if(input.begin(), input.end(), output.begin(), algorithms::IsEven<T>{}, replacement);
    return output;
  }

  template <class T> std::vector<T> remove_if(const std::vector<T> &input) {
    auto output = input;
    output.erase(std::remove_if(output.begin(), output.end(), algorithms::IsEven<T>{}), output.end());
    return output;
  }

  template <class T> std::vector<T> remove_copy_if(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    const auto end = std::remove_copy_if(input.begin(), input.end(), output.begin(), algorithms::IsEven<T>{});
    output.erase(end, output.end());
    return output;
  }

  template <class T> algorithms::TwoRanges<T> swap_ranges(const std::vector<T> &left, const std::vector<T> &right) {
    auto a = left;
    auto b = right;
    std::swap_ranges(a.begin(), a.end(), b.begin());
    return {std::move(a), std::move(b)};
  }

  template <class T> T reduce(const std::vector<T> &input) {
    return std::accumulate(input.begin(), input.end(), T{}, algorithms::Plus<T>{});
  }

  template <class T> T transform_reduce(const std::vector<T> &input) {
    T result{};
    for (const auto value : input)
      result = algorithms::Plus<T>{}(result, algorithms::Square<T>{}(value));
    return result;
  }

  template <class T> T inner_product(const std::vector<T> &left, const std::vector<T> &right) {
    return std::inner_product(left.begin(), left.end(), right.begin(), T{});
  }

  template <class T> std::vector<T> inclusive_scan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::inclusive_scan(input.begin(), input.end(), output.begin(), algorithms::Plus<T>{});
    return output;
  }

  template <class T> std::vector<T> exclusive_scan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::exclusive_scan(input.begin(), input.end(), output.begin(), T{}, algorithms::Plus<T>{});
    return output;
  }

  template <class T> std::vector<T> transform_inclusive_scan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform_inclusive_scan(input.begin(), input.end(), output.begin(), algorithms::Plus<T>{}, algorithms::Square<T>{});
    return output;
  }

  template <class T> std::vector<T> transform_exclusive_scan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform_exclusive_scan(input.begin(), input.end(), output.begin(), T{}, algorithms::Plus<T>{}, algorithms::Square<T>{});
    return output;
  }

  template <class T> std::vector<T> sort(const std::vector<T> &input) {
    auto output = input;
    std::sort(output.begin(), output.end());
    return output;
  }

  template <class T> std::vector<T> stable_sort(const std::vector<T> &input) {
    auto output = input;
    std::stable_sort(output.begin(), output.end());
    return output;
  }

  template <class T> std::vector<T> sort_descending(const std::vector<T> &input) {
    auto output = input;
    std::sort(output.begin(), output.end(), algorithms::Greater<T>{});
    return output;
  }

  template <class T> algorithms::KeyValues<T> sort_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    return order_by_key(keys, values, false);
  }

  template <class T> algorithms::KeyValues<T> stable_sort_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    return order_by_key(keys, values, true);
  }

  template <class T> std::vector<T> unique(const std::vector<T> &input) {
    auto output = input;
    output.erase(std::unique(output.begin(), output.end()), output.end());
    return output;
  }

  template <class T> std::vector<T> unique_copy(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    const auto end = std::unique_copy(input.begin(), input.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> size_t count(const std::vector<T> &input, T value) {
    return static_cast<size_t>(std::count(input.begin(), input.end(), value));
  }

  template <class T> size_t count_if(const std::vector<T> &input) {
    return static_cast<size_t>(std::count_if(input.begin(), input.end(), algorithms::IsEven<T>{}));
  }

  template <class T> size_t find_if(const std::vector<T> &input) {
    return static_cast<size_t>(std::distance(input.begin(), std::find_if(input.begin(), input.end(), algorithms::IsEven<T>{})));
  }

  template <class T> size_t find(const std::vector<T> &input, T value) {
    return static_cast<size_t>(std::distance(input.begin(), std::find(input.begin(), input.end(), value)));
  }

  template <class T> bool all_of(const std::vector<T> &input) { return std::all_of(input.begin(), input.end(), algorithms::IsEven<T>{}); }

  template <class T> bool any_of(const std::vector<T> &input) { return std::any_of(input.begin(), input.end(), algorithms::IsEven<T>{}); }

  template <class T> bool none_of(const std::vector<T> &input) { return std::none_of(input.begin(), input.end(), algorithms::IsEven<T>{}); }

  template <class T> bool equal(const std::vector<T> &left, const std::vector<T> &right) {
    return std::equal(left.begin(), left.end(), right.begin());
  }

  template <class T> algorithms::TwoPositions mismatch(const std::vector<T> &left, const std::vector<T> &right) {
    const auto result = std::mismatch(left.begin(), left.end(), right.begin());
    return {static_cast<size_t>(result.first - left.begin()), static_cast<size_t>(result.second - right.begin())};
  }

  template <class T> size_t lower_bound(const std::vector<T> &input, T value) {
    return static_cast<size_t>(std::distance(input.begin(), std::lower_bound(input.begin(), input.end(), value)));
  }

  template <class T> size_t upper_bound(const std::vector<T> &input, T value) {
    return static_cast<size_t>(std::distance(input.begin(), std::upper_bound(input.begin(), input.end(), value)));
  }

  template <class T> bool binary_search(const std::vector<T> &input, T value) {
    return std::binary_search(input.begin(), input.end(), value);
  }

  template <class T> algorithms::TwoPositions equal_range(const std::vector<T> &input, T value) {
    const auto result = std::equal_range(input.begin(), input.end(), value);
    return {static_cast<size_t>(result.first - input.begin()), static_cast<size_t>(result.second - input.begin())};
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

  template <class T> algorithms::Partitioned<T> stable_partition(const std::vector<T> &input) {
    auto output = input;
    const auto boundary = std::stable_partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    const auto position = static_cast<size_t>(boundary - output.begin());
    return {std::move(output), position};
  }

  template <class T> algorithms::TwoRanges<T> partition_copy(const std::vector<T> &input) {
    std::vector<T> selected(input.size());
    std::vector<T> rejected(input.size());
    const auto ends = std::partition_copy(input.begin(), input.end(), selected.begin(), rejected.begin(), algorithms::IsEven<T>{});
    selected.erase(ends.first, selected.end());
    rejected.erase(ends.second, rejected.end());
    return {std::move(selected), std::move(rejected)};
  }

  template <class T> bool is_partitioned(const std::vector<T> &input) {
    return std::is_partitioned(input.begin(), input.end(), algorithms::IsEven<T>{});
  }

  template <class T> size_t partition_point(const std::vector<T> &input) {
    return static_cast<size_t>(std::partition_point(input.begin(), input.end(), algorithms::IsEven<T>{}) - input.begin());
  }

  template <class T> std::vector<T> adjacent_difference(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::adjacent_difference(input.begin(), input.end(), output.begin());
    return output;
  }

  template <class T> std::vector<T> set_union(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size() + right.size());
    const auto end = std::set_union(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> set_intersection(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(std::min(left.size(), right.size()));
    const auto end = std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> set_difference(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size());
    const auto end = std::set_difference(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> set_symmetric_difference(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size() + right.size());
    const auto end = std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> reverse(const std::vector<T> &input) {
    auto output = input;
    std::reverse(output.begin(), output.end());
    return output;
  }

  template <class T> std::vector<T> reverse_copy(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::reverse_copy(input.begin(), input.end(), output.begin());
    return output;
  }

  template <class T> std::vector<T> shuffle(const std::vector<T> &input) {
    auto output = input;
    std::mt19937 generator(17);
    std::shuffle(output.begin(), output.end(), generator);
    return output;
  }

  template <class T> size_t min_element(const std::vector<T> &input) {
    return static_cast<size_t>(std::min_element(input.begin(), input.end()) - input.begin());
  }

  template <class T> size_t max_element(const std::vector<T> &input) {
    return static_cast<size_t>(std::max_element(input.begin(), input.end()) - input.begin());
  }

  template <class T> algorithms::TwoPositions minmax_element(const std::vector<T> &input) {
    const auto result = std::minmax_element(input.begin(), input.end());
    return {static_cast<size_t>(result.first - input.begin()), static_cast<size_t>(result.second - input.begin())};
  }

  template <class T> bool is_sorted(const std::vector<T> &input) { return std::is_sorted(input.begin(), input.end()); }

  template <class T> size_t is_sorted_until(const std::vector<T> &input) {
    return static_cast<size_t>(std::is_sorted_until(input.begin(), input.end()) - input.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &input, size_t middle) {
    auto output = input;
    std::rotate(output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return output;
  }

  template <class T> std::vector<T> rotate_copy(const std::vector<T> &input, size_t middle) {
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

  template <class T> algorithms::KeyValues<T> reduce_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
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

  template <class T> algorithms::KeyValues<T> inclusive_scan_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    algorithms::KeyValues<T> output{keys, values};
    for (size_t index = 1; index < keys.size(); ++index)
      if (keys[index] == keys[index - 1]) output.values[index] += output.values[index - 1];
    return output;
  }

  template <class T> algorithms::KeyValues<T> exclusive_scan_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    algorithms::KeyValues<T> output{keys, std::vector<int32_t>(values.size())};
    int32_t running = 0;
    for (size_t index = 0; index < keys.size(); ++index) {
      if (index == 0 || keys[index] != keys[index - 1]) running = 0;
      output.values[index] = running;
      running += values[index];
    }
    return output;
  }

  template <class T> algorithms::KeyValues<T> unique_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    algorithms::KeyValues<T> output;
    for (size_t index = 0; index < keys.size(); ++index)
      if (index == 0 || keys[index] != keys[index - 1]) {
        output.keys.push_back(keys[index]);
        output.values.push_back(values[index]);
      }
    return output;
  }

  template <class T> algorithms::KeyValues<T> merge_by_key(const algorithms::KeyValues<T> &left, const algorithms::KeyValues<T> &right) {
    algorithms::KeyValues<T> output;
    size_t a = 0;
    size_t b = 0;
    while (a < left.keys.size() || b < right.keys.size()) {
      const bool take_left = b == right.keys.size() || (a < left.keys.size() && !(right.keys[b] < left.keys[a]));
      if (take_left) {
        output.keys.push_back(left.keys[a]);
        output.values.push_back(left.values[a++]);
      } else {
        output.keys.push_back(right.keys[b]);
        output.values.push_back(right.values[b++]);
      }
    }
    return output;
  }

  template <class T> std::vector<T> partial_sort(const std::vector<T> &input, size_t middle) {
    auto output = input;
    std::partial_sort(output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return output;
  }

  template <class T> std::vector<T> nth_element(const std::vector<T> &input, size_t nth) {
    auto output = input;
    if (!output.empty()) std::nth_element(output.begin(), output.begin() + static_cast<ptrdiff_t>(nth), output.end());
    return output;
  }

  template <class T> std::vector<T> inplace_merge(const std::vector<T> &input, size_t middle) {
    auto output = input;
    std::inplace_merge(output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return output;
  }

private:
  template <class T>
  static algorithms::KeyValues<T> order_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values, bool stable) {
    std::vector<size_t> order(keys.size());
    std::iota(order.begin(), order.end(), 0);
    const auto compare = [&](size_t left, size_t right) { return keys[left] < keys[right]; };
    if (stable) std::stable_sort(order.begin(), order.end(), compare);
    else std::sort(order.begin(), order.end(), compare);
    auto [result_keys, result_values] = order ^ map([&](size_t index) { return std::pair{keys[index], values[index]}; }) ^ unzip();
    return {std::move(result_keys), std::move(result_values)};
  }
};
} // namespace unified::test

#define DECLARE_REFERENCE_ALGORITHMS(name, check)                                                                                          \
  TEMPLATE_TEST_CASE("reference algorithms/" name, "[unit][unified]", int8_t, int32_t, int64_t, float, double) {                           \
    const auto size = GENERATE(0, 1, 7, 33, 257);                                                                                          \
    CAPTURE(size);                                                                                                                         \
    unified::test::ReferenceAlgorithms backend;                                                                                            \
    check<TestType>(backend, size);                                                                                                        \
  }

DECLARE_REFERENCE_ALGORITHMS("copy", unified::algorithms::copy)
DECLARE_REFERENCE_ALGORITHMS("copy_if", unified::algorithms::copy_if)
DECLARE_REFERENCE_ALGORITHMS("copy_n", unified::algorithms::copy_n)
DECLARE_REFERENCE_ALGORITHMS("uninitialized_copy", unified::algorithms::uninitialized_copy)
DECLARE_REFERENCE_ALGORITHMS("uninitialized_fill", unified::algorithms::uninitialized_fill)
DECLARE_REFERENCE_ALGORITHMS("fill", unified::algorithms::fill)
DECLARE_REFERENCE_ALGORITHMS("generate", unified::algorithms::generate)
DECLARE_REFERENCE_ALGORITHMS("for_each", unified::algorithms::for_each)
DECLARE_REFERENCE_ALGORITHMS("transform_unary", unified::algorithms::transform_unary)
DECLARE_REFERENCE_ALGORITHMS("transform_binary", unified::algorithms::transform_binary)
DECLARE_REFERENCE_ALGORITHMS("replace_if", unified::algorithms::replace_if)
DECLARE_REFERENCE_ALGORITHMS("replace_copy_if", unified::algorithms::replace_copy_if)
DECLARE_REFERENCE_ALGORITHMS("remove_if", unified::algorithms::remove_if)
DECLARE_REFERENCE_ALGORITHMS("remove_copy_if", unified::algorithms::remove_copy_if)
DECLARE_REFERENCE_ALGORITHMS("reverse", unified::algorithms::reverse)
DECLARE_REFERENCE_ALGORITHMS("reverse_copy", unified::algorithms::reverse_copy)
DECLARE_REFERENCE_ALGORITHMS("shuffle", unified::algorithms::shuffle)
DECLARE_REFERENCE_ALGORITHMS("swap_ranges", unified::algorithms::swap_ranges)
DECLARE_REFERENCE_ALGORITHMS("reduce", unified::algorithms::reduce)
DECLARE_REFERENCE_ALGORITHMS("transform_reduce", unified::algorithms::transform_reduce)
DECLARE_REFERENCE_ALGORITHMS("inner_product", unified::algorithms::inner_product)
DECLARE_REFERENCE_ALGORITHMS("inclusive_scan", unified::algorithms::inclusive_scan)
DECLARE_REFERENCE_ALGORITHMS("exclusive_scan", unified::algorithms::exclusive_scan)
DECLARE_REFERENCE_ALGORITHMS("transform_inclusive_scan", unified::algorithms::transform_inclusive_scan)
DECLARE_REFERENCE_ALGORITHMS("transform_exclusive_scan", unified::algorithms::transform_exclusive_scan)
DECLARE_REFERENCE_ALGORITHMS("adjacent_difference", unified::algorithms::adjacent_difference)
DECLARE_REFERENCE_ALGORITHMS("sort", unified::algorithms::sort)
DECLARE_REFERENCE_ALGORITHMS("stable_sort", unified::algorithms::stable_sort)
DECLARE_REFERENCE_ALGORITHMS("sort_descending", unified::algorithms::sort_descending)
DECLARE_REFERENCE_ALGORITHMS("sort_by_key", unified::algorithms::sort_by_key)
DECLARE_REFERENCE_ALGORITHMS("stable_sort_by_key", unified::algorithms::stable_sort_by_key)
DECLARE_REFERENCE_ALGORITHMS("unique", unified::algorithms::unique)
DECLARE_REFERENCE_ALGORITHMS("unique_copy", unified::algorithms::unique_copy)
DECLARE_REFERENCE_ALGORITHMS("count", unified::algorithms::count)
DECLARE_REFERENCE_ALGORITHMS("count_if", unified::algorithms::count_if)
DECLARE_REFERENCE_ALGORITHMS("find", unified::algorithms::find)
DECLARE_REFERENCE_ALGORITHMS("find_if", unified::algorithms::find_if)
DECLARE_REFERENCE_ALGORITHMS("all_of", unified::algorithms::all_of)
DECLARE_REFERENCE_ALGORITHMS("any_of", unified::algorithms::any_of)
DECLARE_REFERENCE_ALGORITHMS("none_of", unified::algorithms::none_of)
DECLARE_REFERENCE_ALGORITHMS("equal", unified::algorithms::equal)
DECLARE_REFERENCE_ALGORITHMS("mismatch", unified::algorithms::mismatch)
DECLARE_REFERENCE_ALGORITHMS("lower_bound", unified::algorithms::lower_bound)
DECLARE_REFERENCE_ALGORITHMS("upper_bound", unified::algorithms::upper_bound)
DECLARE_REFERENCE_ALGORITHMS("binary_search", unified::algorithms::binary_search)
DECLARE_REFERENCE_ALGORITHMS("equal_range", unified::algorithms::equal_range)
DECLARE_REFERENCE_ALGORITHMS("merge", unified::algorithms::merge)
DECLARE_REFERENCE_ALGORITHMS("partition", unified::algorithms::partition)
DECLARE_REFERENCE_ALGORITHMS("stable_partition", unified::algorithms::stable_partition)
DECLARE_REFERENCE_ALGORITHMS("partition_copy", unified::algorithms::partition_copy)
DECLARE_REFERENCE_ALGORITHMS("is_partitioned", unified::algorithms::is_partitioned)
DECLARE_REFERENCE_ALGORITHMS("is_partitioned_false", unified::algorithms::is_partitioned_false)
DECLARE_REFERENCE_ALGORITHMS("partition_point", unified::algorithms::partition_point)
DECLARE_REFERENCE_ALGORITHMS("set_union", unified::algorithms::set_union)
DECLARE_REFERENCE_ALGORITHMS("set_intersection", unified::algorithms::set_intersection)
DECLARE_REFERENCE_ALGORITHMS("set_difference", unified::algorithms::set_difference)
DECLARE_REFERENCE_ALGORITHMS("set_symmetric_difference", unified::algorithms::set_symmetric_difference)
DECLARE_REFERENCE_ALGORITHMS("min_element", unified::algorithms::min_element)
DECLARE_REFERENCE_ALGORITHMS("max_element", unified::algorithms::max_element)
DECLARE_REFERENCE_ALGORITHMS("minmax_element", unified::algorithms::minmax_element)
DECLARE_REFERENCE_ALGORITHMS("is_sorted", unified::algorithms::is_sorted)
DECLARE_REFERENCE_ALGORITHMS("is_sorted_false", unified::algorithms::is_sorted_false)
DECLARE_REFERENCE_ALGORITHMS("is_sorted_until", unified::algorithms::is_sorted_until)
DECLARE_REFERENCE_ALGORITHMS("rotate", unified::algorithms::rotate)
DECLARE_REFERENCE_ALGORITHMS("rotate_copy", unified::algorithms::rotate_copy)
DECLARE_REFERENCE_ALGORITHMS("includes", unified::algorithms::includes)
DECLARE_REFERENCE_ALGORITHMS("includes_false", unified::algorithms::includes_false)
DECLARE_REFERENCE_ALGORITHMS("gather", unified::algorithms::gather)
DECLARE_REFERENCE_ALGORITHMS("scatter", unified::algorithms::scatter)
DECLARE_REFERENCE_ALGORITHMS("reduce_by_key", unified::algorithms::reduce_by_key)
DECLARE_REFERENCE_ALGORITHMS("inclusive_scan_by_key", unified::algorithms::inclusive_scan_by_key)
DECLARE_REFERENCE_ALGORITHMS("exclusive_scan_by_key", unified::algorithms::exclusive_scan_by_key)
DECLARE_REFERENCE_ALGORITHMS("unique_by_key", unified::algorithms::unique_by_key)
DECLARE_REFERENCE_ALGORITHMS("merge_by_key", unified::algorithms::merge_by_key)
DECLARE_REFERENCE_ALGORITHMS("partial_sort", unified::algorithms::partial_sort)
DECLARE_REFERENCE_ALGORITHMS("nth_element", unified::algorithms::nth_element)
DECLARE_REFERENCE_ALGORITHMS("inplace_merge", unified::algorithms::inplace_merge)

#undef DECLARE_REFERENCE_ALGORITHMS
