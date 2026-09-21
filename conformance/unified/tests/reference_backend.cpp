
#include <algorithm>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <memory>
#include <numeric>
#include <random>
#include <utility>
#include <vector>

#include <catch2/catch_template_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "reference_async.hpp"
#include "template_algorithm.hpp"
#include "template_async.hpp"
#include "template_blas.hpp"
#include "template_edge.hpp"
#include "template_stress.hpp"
#include <magic_enum/magic_enum.hpp>

namespace unified::test {
class ReferenceAlgorithms {
public:
  template <class T> bool supports_copy(size_t) const { return true; }

  template <class T> bool supports_copy_if(size_t) const { return true; }

  template <class T> bool supports_copy_n(size_t) const { return true; }

  template <class T> bool supports_uninitialized_copy(size_t) const { return true; }

  template <class T> bool supports_uninitialized_fill(size_t) const { return true; }

  template <class T> bool supports_fill(size_t) const { return true; }

  template <class T> bool supports_generate(size_t) const { return true; }

  template <class T> bool supports_for_each(size_t) const { return true; }

  template <class T> bool supports_transform_unary(size_t) const { return true; }

  template <class T> bool supports_transform_binary(size_t) const { return true; }

  template <class T> bool supports_replace_if(size_t) const { return true; }

  template <class T> bool supports_replace_copy_if(size_t) const { return true; }

  template <class T> bool supports_remove_if(size_t) const { return true; }

  template <class T> bool supports_remove_copy_if(size_t) const { return true; }

  template <class T> bool supports_reverse(size_t) const { return true; }

  template <class T> bool supports_reverse_copy(size_t) const { return true; }

  template <class T> bool supports_shuffle(size_t) const { return true; }

  template <class T> bool supports_swap_ranges(size_t) const { return true; }

  template <class T> bool supports_reduce(size_t) const { return true; }

  template <class T> bool supports_transform_reduce(size_t) const { return true; }

  template <class T> bool supports_inner_product(size_t) const { return true; }

  template <class T> bool supports_inclusive_scan(size_t) const { return true; }

  template <class T> bool supports_exclusive_scan(size_t) const { return true; }

  template <class T> bool supports_transform_inclusive_scan(size_t) const { return true; }

  template <class T> bool supports_transform_exclusive_scan(size_t) const { return true; }

  template <class T> bool supports_adjacent_difference(size_t) const { return true; }

  template <class T> bool supports_sort(size_t) const { return true; }

  template <class T> bool supports_stable_sort(size_t) const { return true; }

  template <class T> bool supports_sort_descending(size_t) const { return true; }

  template <class T> bool supports_sort_by_key(size_t) const { return true; }

  template <class T> bool supports_stable_sort_by_key(size_t) const { return true; }

  template <class T> bool supports_unique(size_t) const { return true; }

  template <class T> bool supports_unique_copy(size_t) const { return true; }

  template <class T> bool supports_count(size_t) const { return true; }

  template <class T> bool supports_count_if(size_t) const { return true; }

  template <class T> bool supports_find(size_t) const { return true; }

  template <class T> bool supports_find_if(size_t) const { return true; }

  template <class T> bool supports_all_of(size_t) const { return true; }

  template <class T> bool supports_any_of(size_t) const { return true; }

  template <class T> bool supports_none_of(size_t) const { return true; }

  template <class T> bool supports_equal(size_t) const { return true; }

  template <class T> bool supports_mismatch(size_t) const { return true; }

  template <class T> bool supports_lower_bound(size_t) const { return true; }

  template <class T> bool supports_upper_bound(size_t) const { return true; }

  template <class T> bool supports_binary_search(size_t) const { return true; }

  template <class T> bool supports_equal_range(size_t) const { return true; }

  template <class T> bool supports_merge(size_t) const { return true; }

  template <class T> bool supports_partition(size_t) const { return true; }

  template <class T> bool supports_stable_partition(size_t) const { return true; }

  template <class T> bool supports_partition_copy(size_t) const { return true; }

  template <class T> bool supports_is_partitioned(size_t) const { return true; }

  template <class T> bool supports_is_partitioned_false(size_t) const { return true; }

  template <class T> bool supports_partition_point(size_t) const { return true; }

  template <class T> bool supports_set_union(size_t) const { return true; }

  template <class T> bool supports_set_intersection(size_t) const { return true; }

  template <class T> bool supports_set_difference(size_t) const { return true; }

  template <class T> bool supports_set_symmetric_difference(size_t) const { return true; }

  template <class T> bool supports_min_element(size_t) const { return true; }

  template <class T> bool supports_max_element(size_t) const { return true; }

  template <class T> bool supports_minmax_element(size_t) const { return true; }

  template <class T> bool supports_is_sorted(size_t) const { return true; }

  template <class T> bool supports_is_sorted_false(size_t) const { return true; }

  template <class T> bool supports_is_sorted_until(size_t) const { return true; }

  template <class T> bool supports_rotate(size_t) const { return true; }

  template <class T> bool supports_rotate_copy(size_t) const { return true; }

  template <class T> bool supports_includes(size_t) const { return true; }

  template <class T> bool supports_includes_false(size_t) const { return true; }

  template <class T> bool supports_gather(size_t) const { return true; }

  template <class T> bool supports_scatter(size_t) const { return true; }

  template <class T> bool supports_reduce_by_key(size_t) const { return true; }

  template <class T> bool supports_inclusive_scan_by_key(size_t) const { return true; }

  template <class T> bool supports_exclusive_scan_by_key(size_t) const { return true; }

  template <class T> bool supports_unique_by_key(size_t) const { return true; }

  template <class T> bool supports_merge_by_key(size_t) const { return true; }

  template <class T> bool supports_partial_sort(size_t) const { return true; }

  template <class T> bool supports_nth_element(size_t) const { return true; }

  template <class T> bool supports_inplace_merge(size_t) const { return true; }

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
    std::vector<T> output(indices.size());
    for (size_t index = 0; index < indices.size(); ++index)
      output[index] = input[indices[index]];
    return output;
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
    algorithms::KeyValues<T> result{std::vector<T>(keys.size()), std::vector<int32_t>(values.size())};
    for (size_t index = 0; index < order.size(); ++index) {
      result.keys[index] = keys[order[index]];
      result.values[index] = values[order[index]];
    }
    return result;
  }
};
} // namespace unified::test

namespace unified::test {
template <ScalarType Kind> class ReferenceBlas {
public:
  struct Buffer {
    std::vector<long double> values;
  };

  bool supports_axpy() const { return true; }

  bool supports_scal() const { return true; }

  bool supports_copy() const { return true; }

  bool supports_dot() const { return true; }

  bool supports_nrm2() const { return true; }

  bool supports_asum() const { return true; }

  bool supports_gemv() const { return true; }

  bool supports_gemm() const { return true; }

  bool supports_gemm_strided_batched() const { return true; }
  Buffer allocate(size_t size) { return {std::vector<long double>(size)}; }
  void upload(Buffer &buffer, const std::vector<long double> &host) { buffer.values = host; }
  std::vector<long double> download(const Buffer &buffer, size_t size) {
    return {buffer.values.begin(), buffer.values.begin() + static_cast<ptrdiff_t>(size)};
  }
  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      y.values[index(size, i, incy)] += alpha * x.values[index(size, i, incx)];
  }
  void scal(int n, long double alpha, Buffer &x, int incx) {
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      x.values[index(size, i, incx)] *= alpha;
  }
  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      y.values[index(size, i, incy)] = x.values[index(size, i, incx)];
  }
  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    long double result = 0;
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      result += x.values[index(size, i, incx)] * y.values[index(size, i, incy)];
    return result;
  }
  long double nrm2(int n, Buffer &x, int incx) {
    long double result = 0;
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      result += x.values[index(size, i, incx)] * x.values[index(size, i, incx)];
    return std::sqrt(result);
  }
  long double asum(int n, Buffer &x, int incx) {
    long double result = 0;
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      result += std::fabs(x.values[index(size, i, incx)]);
    return result;
  }
  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx, long double beta,
            Buffer &y, int incy) {
    const auto initial = y.values;
    const auto rows = static_cast<size_t>(m);
    const auto columns = static_cast<size_t>(n);
    const auto leading_dimension = static_cast<size_t>(lda);
    const auto output_size = transpose == blas::Transpose::none ? rows : columns;
    const auto input_size = transpose == blas::Transpose::none ? columns : rows;
    for (size_t output = 0; output < output_size; ++output) {
      long double sum = 0;
      for (size_t input = 0; input < input_size; ++input) {
        const auto row = transpose == blas::Transpose::none ? output : input;
        const auto column = transpose == blas::Transpose::none ? input : output;
        sum += a.values[row + column * leading_dimension] * x.values[index(input_size, input, incx)];
      }
      const auto output_index = index(output_size, output, incy);
      y.values[output_index] = alpha * sum + beta * initial[output_index];
    }
  }
  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    const auto initial = c.values;
    const auto rows = static_cast<size_t>(m);
    const auto columns = static_cast<size_t>(n);
    const auto inner_size = static_cast<size_t>(k);
    const auto leading_a = static_cast<size_t>(lda);
    const auto leading_b = static_cast<size_t>(ldb);
    const auto leading_c = static_cast<size_t>(ldc);
    for (size_t column = 0; column < columns; ++column)
      for (size_t row = 0; row < rows; ++row) {
        long double sum = 0;
        for (size_t inner = 0; inner < inner_size; ++inner)
          sum += a.values[transpose_a == blas::Transpose::none ? row + inner * leading_a : inner + row * leading_a]
                 * b.values[transpose_b == blas::Transpose::none ? inner + column * leading_b : column + inner * leading_b];
        const auto output = row + column * leading_c;
        c.values[output] = beta == 0 ? alpha * sum : alpha * sum + beta * initial[output];
      }
  }
  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a,
                            int lda, size_t stride_a, Buffer &b, int ldb, size_t stride_b, long double beta, Buffer &c, int ldc,
                            size_t stride_c, int batch_count) {
    const auto initial = c.values;
    const auto batches = static_cast<size_t>(batch_count);
    const auto rows = static_cast<size_t>(m);
    const auto columns = static_cast<size_t>(n);
    const auto inner_size = static_cast<size_t>(k);
    const auto leading_a = static_cast<size_t>(lda);
    const auto leading_b = static_cast<size_t>(ldb);
    const auto leading_c = static_cast<size_t>(ldc);
    for (size_t batch = 0; batch < batches; ++batch)
      for (size_t column = 0; column < columns; ++column)
        for (size_t row = 0; row < rows; ++row) {
          long double sum = 0;
          for (size_t inner = 0; inner < inner_size; ++inner) {
            const auto a_index = transpose_a == blas::Transpose::none ? row + inner * leading_a : inner + row * leading_a;
            const auto b_index = transpose_b == blas::Transpose::none ? inner + column * leading_b : column + inner * leading_b;
            sum += a.values[batch * stride_a + a_index] * b.values[batch * stride_b + b_index];
          }
          const auto c_index = batch * stride_c + row + column * leading_c;
          c.values[c_index] = beta == 0 ? alpha * sum : alpha * sum + beta * initial[c_index];
        }
  }

private:
  static size_t index(size_t count, size_t position, int increment) {
    const auto stride = static_cast<size_t>(increment < 0 ? -static_cast<ptrdiff_t>(increment) : increment);
    return (increment < 0 ? count - 1 - position : position) * stride;
  }
};
} // namespace unified::test

namespace unified::test {
class ReferenceEdgeCases {
public:
  template <class T> bool supports_composed_iterator_reduce() const { return true; }

  template <class T> bool supports_biased_index_64() const { return true; }

  template <class T> bool supports_segments_singleton() const { return true; }

  template <class T> bool supports_segments_giant() const { return true; }

  template <class T> bool supports_segments_boundary() const { return true; }

  template <class T> bool supports_inplace_scan_canary() const { return true; }

  template <class T> bool supports_stable_sort_payload() const { return true; }

  template <class T> bool supports_dependency_diamond() const { return true; }

  template <class T> bool supports_numeric_edge_roundtrip() const { return true; }

  template <class T> bool supports_primitive_block_scan() const { return true; }

  template <class T> bool supports_runtime_error_recover() const { return true; }

  template <class T> int64_t composed_iterator_reduce(size_t size) {
    int64_t result = 0;
    for (size_t index = 0; index < size; ++index)
      result += edge::WidePairValue<T>{}(edge::ValueAt<T>{}(size - index - 1), edge::ValueAt<T>{}(index));
    return result;
  }

  template <class T> uint64_t biased_index_checksum(uint64_t base, size_t size) {
    uint64_t result = 0;
    for (size_t index = 0; index < size; ++index)
      result ^= edge::BiasedHash<T>{}(base + index);
    return result;
  }

  template <class T> std::vector<T> segmented_inclusive_scan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
    auto output = input;
    for (size_t index = 1; index < output.size(); ++index)
      if (keys[index] == keys[index - 1]) output[index] = support::Plus<T>{}(output[index - 1], input[index]);
    return output;
  }

  template <class T> std::vector<T> inplace_inclusive_scan(const std::vector<T> &storage, size_t offset, size_t count) {
    auto output = storage;
    if (count != 0)
      std::inclusive_scan(output.begin() + static_cast<ptrdiff_t>(offset), output.begin() + static_cast<ptrdiff_t>(offset + count),
                          output.begin() + static_cast<ptrdiff_t>(offset), support::Plus<T>{});
    return output;
  }

  template <class T> support::KeyValues<T> stable_sort_payload(const std::vector<T> &keys, const std::vector<int32_t> &payload) {
    std::vector<size_t> order(keys.size());
    std::iota(order.begin(), order.end(), 0);
    std::stable_sort(order.begin(), order.end(), [&](auto left, auto right) { return keys[left] < keys[right]; });
    support::KeyValues<T> result;
    result.keys.reserve(keys.size());
    result.values.reserve(keys.size());
    for (const auto index : order) {
      result.keys.push_back(keys[index]);
      result.values.push_back(payload[index]);
    }
    return result;
  }

  template <class T> std::vector<T> dependency_diamond(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform(input.begin(), input.end(), output.begin(),
                   [](T value) { return support::Combine<T>{}(support::Affine<T>{}(value), support::Square<T>{}(value)); });
    return output;
  }

  template <class T> std::vector<T> numeric_edge_roundtrip(const std::vector<T> &input) { return input; }

  template <class T> std::vector<T> primitive_block_scan(const std::vector<T> &input, size_t block_size) {
    auto output = input;
    for (size_t index = 0; index < output.size(); ++index)
      if (index % block_size != 0) output[index] = support::Plus<T>{}(output[index - 1], input[index]);
    return output;
  }

  template <class T> std::vector<T> runtime_error_recover(const std::vector<T> &input) {
    try {
      throw std::bad_alloc();
    } catch (const std::bad_alloc &) {
      return input;
    }
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

#define DECLARE_REFERENCE_EDGE_CASES(name, check)                                                                                          \
  TEMPLATE_TEST_CASE("reference edge cases/" name, "[unit][unified]", int8_t, int32_t, int64_t, float, double) {                           \
    const auto size = GENERATE(0, 1, 7, 33, 257);                                                                                          \
    CAPTURE(size);                                                                                                                         \
    unified::test::ReferenceEdgeCases backend;                                                                                             \
    check<TestType>(backend, size);                                                                                                        \
  }

DECLARE_REFERENCE_EDGE_CASES("composed_iterator_reduce", unified::edge::composed_iterator_reduce)
DECLARE_REFERENCE_EDGE_CASES("biased_index_64", unified::edge::biased_index_64)
DECLARE_REFERENCE_EDGE_CASES("segments_singleton", unified::edge::segments_singleton)
DECLARE_REFERENCE_EDGE_CASES("segments_giant", unified::edge::segments_giant)
DECLARE_REFERENCE_EDGE_CASES("segments_boundary", unified::edge::segments_boundary)
DECLARE_REFERENCE_EDGE_CASES("inplace_scan_canary", unified::edge::inplace_scan_canary)
DECLARE_REFERENCE_EDGE_CASES("stable_sort_payload", unified::edge::stable_sort_payload)
DECLARE_REFERENCE_EDGE_CASES("dependency_diamond", unified::edge::dependency_diamond)
DECLARE_REFERENCE_EDGE_CASES("numeric_edge_roundtrip", unified::edge::numeric_edge_roundtrip)
DECLARE_REFERENCE_EDGE_CASES("primitive_block_scan", unified::edge::primitive_block_scan)
DECLARE_REFERENCE_EDGE_CASES("runtime_error_recover", unified::edge::runtime_error_recover)

#undef DECLARE_REFERENCE_EDGE_CASES

using unified::ScalarType;
#define DECLARE_REFERENCE_BLAS_F32_LEVEL1(name, check)                                                                                     \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1(name, check)                                                                              \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F32_GEMV(name, check, padded_check)                                                                         \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_REFERENCE_BLAS_F32_GEMM(name, check, padded_check, transpose_a, transpose_b)                                               \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_REFERENCE_BLAS_F32_BATCHED_GEMM(name)                                                                                      \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_REFERENCE_BLAS_F32_LEVEL1("scal", unified::blas::scal)
DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_REFERENCE_BLAS_F32_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_REFERENCE_BLAS_F32_LEVEL1("asum", unified::blas::asum)
DECLARE_REFERENCE_BLAS_F32_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_REFERENCE_BLAS_F32_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_REFERENCE_BLAS_F32_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F32_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F32_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F32_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F32_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_REFERENCE_BLAS_F32_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F32_GEMV
#undef DECLARE_REFERENCE_BLAS_F32_GEMM
#undef DECLARE_REFERENCE_BLAS_F32_BATCHED_GEMM

#define DECLARE_REFERENCE_BLAS_F64_LEVEL1(name, check)                                                                                     \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1(name, check)                                                                              \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F64_GEMV(name, check, padded_check)                                                                         \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_REFERENCE_BLAS_F64_GEMM(name, check, padded_check, transpose_a, transpose_b)                                               \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_REFERENCE_BLAS_F64_BATCHED_GEMM(name)                                                                                      \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_REFERENCE_BLAS_F64_LEVEL1("scal", unified::blas::scal)
DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_REFERENCE_BLAS_F64_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_REFERENCE_BLAS_F64_LEVEL1("asum", unified::blas::asum)
DECLARE_REFERENCE_BLAS_F64_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_REFERENCE_BLAS_F64_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_REFERENCE_BLAS_F64_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F64_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F64_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F64_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F64_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_REFERENCE_BLAS_F64_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F64_GEMV
#undef DECLARE_REFERENCE_BLAS_F64_GEMM
#undef DECLARE_REFERENCE_BLAS_F64_BATCHED_GEMM

#define DECLARE_REFERENCE_BLAS_F16_LEVEL1(name, check)                                                                                     \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1(name, check)                                                                              \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F16_GEMV(name, check, padded_check)                                                                         \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_REFERENCE_BLAS_F16_GEMM(name, check, padded_check, transpose_a, transpose_b)                                               \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_REFERENCE_BLAS_F16_BATCHED_GEMM(name)                                                                                      \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_REFERENCE_BLAS_F16_LEVEL1("scal", unified::blas::scal)
DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_REFERENCE_BLAS_F16_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_REFERENCE_BLAS_F16_LEVEL1("asum", unified::blas::asum)
DECLARE_REFERENCE_BLAS_F16_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_REFERENCE_BLAS_F16_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_REFERENCE_BLAS_F16_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F16_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F16_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F16_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F16_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_REFERENCE_BLAS_F16_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F16_GEMV
#undef DECLARE_REFERENCE_BLAS_F16_GEMM
#undef DECLARE_REFERENCE_BLAS_F16_BATCHED_GEMM

#define DECLARE_REFERENCE_BLAS_I8_LEVEL1(name, check)                                                                                      \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1(name, check)                                                                               \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_I8_GEMV(name, check, padded_check)                                                                          \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_REFERENCE_BLAS_I8_GEMM(name, check, padded_check, transpose_a, transpose_b)                                                \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_REFERENCE_BLAS_I8_BATCHED_GEMM(name)                                                                                       \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_REFERENCE_BLAS_I8_LEVEL1("scal", unified::blas::scal)
DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_REFERENCE_BLAS_I8_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_REFERENCE_BLAS_I8_LEVEL1("asum", unified::blas::asum)
DECLARE_REFERENCE_BLAS_I8_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_REFERENCE_BLAS_I8_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_REFERENCE_BLAS_I8_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                               unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_I8_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                               unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_I8_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                               unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_I8_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                               unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_I8_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_REFERENCE_BLAS_I8_LEVEL1
#undef DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1
#undef DECLARE_REFERENCE_BLAS_I8_GEMV
#undef DECLARE_REFERENCE_BLAS_I8_GEMM
#undef DECLARE_REFERENCE_BLAS_I8_BATCHED_GEMM
