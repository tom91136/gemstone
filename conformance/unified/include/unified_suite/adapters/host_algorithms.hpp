#ifndef UNIFIED_SUITE_ADAPTERS_HOST_ALGORITHMS_HPP
#define UNIFIED_SUITE_ADAPTERS_HOST_ALGORITHMS_HPP

#include "unified_suite/algorithms.hpp"

#include <algorithm>
#include <cstddef>
#include <memory>
#include <numeric>
#include <random>
#include <utility>
#include <vector>

namespace unified_suite::adapters {

class HostAlgorithms {
public:
  template <class T> bool supports(algorithms::Operation) const { return true; }

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

  template <class T> std::vector<T> copy_n(const std::vector<T> &input, std::size_t count) {
    std::vector<T> output(count);
    std::copy_n(input.begin(), count, output.begin());
    return output;
  }

  template <class T> std::vector<T> uninitialized_copy(const std::vector<T> &input) {
    if (input.empty())
      return {};
    std::allocator<T> allocator;
    T *storage = allocator.allocate(input.size());
    std::uninitialized_copy(input.begin(), input.end(), storage);
    std::vector<T> output(storage, storage + input.size());
    std::destroy_n(storage, input.size());
    allocator.deallocate(storage, input.size());
    return output;
  }

  template <class T> std::vector<T> uninitialized_fill(std::size_t size, T value) {
    if (size == 0)
      return {};
    std::allocator<T> allocator;
    T *storage = allocator.allocate(size);
    std::uninitialized_fill(storage, storage + size, value);
    std::vector<T> output(storage, storage + size);
    std::destroy_n(storage, size);
    allocator.deallocate(storage, size);
    return output;
  }

  template <class T> std::vector<T> fill(std::size_t size, T value) {
    std::vector<T> output(size);
    std::fill(output.begin(), output.end(), value);
    return output;
  }

  template <class T> std::vector<T> generate(std::size_t size) {
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

  template <class T>
  std::vector<T> transform_binary(const std::vector<T> &left, const std::vector<T> &right) {
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

  template <class T>
  algorithms::TwoRanges<T> swap_ranges(const std::vector<T> &left, const std::vector<T> &right) {
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
    std::transform_inclusive_scan(input.begin(), input.end(), output.begin(), algorithms::Plus<T>{},
                                  algorithms::Square<T>{});
    return output;
  }

  template <class T> std::vector<T> transform_exclusive_scan(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform_exclusive_scan(input.begin(), input.end(), output.begin(), T{}, algorithms::Plus<T>{},
                                  algorithms::Square<T>{});
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

  template <class T>
  algorithms::KeyValues<T> sort_by_key(const std::vector<T> &keys, const std::vector<std::int32_t> &values) {
    return order_by_key(keys, values, false);
  }

  template <class T>
  algorithms::KeyValues<T> stable_sort_by_key(const std::vector<T> &keys,
                                              const std::vector<std::int32_t> &values) {
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

  template <class T> std::size_t count(const std::vector<T> &input, T value) {
    return static_cast<std::size_t>(std::count(input.begin(), input.end(), value));
  }

  template <class T> std::size_t count_if(const std::vector<T> &input) {
    return static_cast<std::size_t>(std::count_if(input.begin(), input.end(), algorithms::IsEven<T>{}));
  }

  template <class T> std::size_t find_if(const std::vector<T> &input) {
    return static_cast<std::size_t>(
        std::distance(input.begin(), std::find_if(input.begin(), input.end(), algorithms::IsEven<T>{})));
  }

  template <class T> std::size_t find(const std::vector<T> &input, T value) {
    return static_cast<std::size_t>(
        std::distance(input.begin(), std::find(input.begin(), input.end(), value)));
  }

  template <class T> bool all_of(const std::vector<T> &input) {
    return std::all_of(input.begin(), input.end(), algorithms::IsEven<T>{});
  }

  template <class T> bool any_of(const std::vector<T> &input) {
    return std::any_of(input.begin(), input.end(), algorithms::IsEven<T>{});
  }

  template <class T> bool none_of(const std::vector<T> &input) {
    return std::none_of(input.begin(), input.end(), algorithms::IsEven<T>{});
  }

  template <class T> bool equal(const std::vector<T> &left, const std::vector<T> &right) {
    return std::equal(left.begin(), left.end(), right.begin());
  }

  template <class T>
  algorithms::TwoPositions mismatch(const std::vector<T> &left, const std::vector<T> &right) {
    const auto result = std::mismatch(left.begin(), left.end(), right.begin());
    return {static_cast<std::size_t>(result.first - left.begin()),
            static_cast<std::size_t>(result.second - right.begin())};
  }

  template <class T> std::size_t lower_bound(const std::vector<T> &input, T value) {
    return static_cast<std::size_t>(
        std::distance(input.begin(), std::lower_bound(input.begin(), input.end(), value)));
  }

  template <class T> std::size_t upper_bound(const std::vector<T> &input, T value) {
    return static_cast<std::size_t>(
        std::distance(input.begin(), std::upper_bound(input.begin(), input.end(), value)));
  }

  template <class T> bool binary_search(const std::vector<T> &input, T value) {
    return std::binary_search(input.begin(), input.end(), value);
  }

  template <class T> algorithms::TwoPositions equal_range(const std::vector<T> &input, T value) {
    const auto result = std::equal_range(input.begin(), input.end(), value);
    return {static_cast<std::size_t>(result.first - input.begin()),
            static_cast<std::size_t>(result.second - input.begin())};
  }

  template <class T> std::vector<T> merge(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size() + right.size());
    std::merge(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    return output;
  }

  template <class T> algorithms::Partitioned<T> partition(const std::vector<T> &input) {
    auto output = input;
    const auto boundary = std::partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    const auto position = static_cast<std::size_t>(std::distance(output.begin(), boundary));
    return {std::move(output), position};
  }

  template <class T> algorithms::Partitioned<T> stable_partition(const std::vector<T> &input) {
    auto output = input;
    const auto boundary = std::stable_partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    const auto position = static_cast<std::size_t>(boundary - output.begin());
    return {std::move(output), position};
  }

  template <class T> algorithms::TwoRanges<T> partition_copy(const std::vector<T> &input) {
    std::vector<T> selected(input.size());
    std::vector<T> rejected(input.size());
    const auto ends = std::partition_copy(input.begin(), input.end(), selected.begin(), rejected.begin(),
                                          algorithms::IsEven<T>{});
    selected.erase(ends.first, selected.end());
    rejected.erase(ends.second, rejected.end());
    return {std::move(selected), std::move(rejected)};
  }

  template <class T> bool is_partitioned(const std::vector<T> &input) {
    return std::is_partitioned(input.begin(), input.end(), algorithms::IsEven<T>{});
  }

  template <class T> std::size_t partition_point(const std::vector<T> &input) {
    return static_cast<std::size_t>(
        std::partition_point(input.begin(), input.end(), algorithms::IsEven<T>{}) - input.begin());
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

  template <class T>
  std::vector<T> set_intersection(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(std::min(left.size(), right.size()));
    const auto end =
        std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T> std::vector<T> set_difference(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size());
    const auto end =
        std::set_difference(left.begin(), left.end(), right.begin(), right.end(), output.begin());
    output.erase(end, output.end());
    return output;
  }

  template <class T>
  std::vector<T> set_symmetric_difference(const std::vector<T> &left, const std::vector<T> &right) {
    std::vector<T> output(left.size() + right.size());
    const auto end =
        std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), output.begin());
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

  template <class T> std::size_t min_element(const std::vector<T> &input) {
    return static_cast<std::size_t>(std::min_element(input.begin(), input.end()) - input.begin());
  }

  template <class T> std::size_t max_element(const std::vector<T> &input) {
    return static_cast<std::size_t>(std::max_element(input.begin(), input.end()) - input.begin());
  }

  template <class T> algorithms::TwoPositions minmax_element(const std::vector<T> &input) {
    const auto result = std::minmax_element(input.begin(), input.end());
    return {static_cast<std::size_t>(result.first - input.begin()),
            static_cast<std::size_t>(result.second - input.begin())};
  }

  template <class T> bool is_sorted(const std::vector<T> &input) {
    return std::is_sorted(input.begin(), input.end());
  }

  template <class T> std::size_t is_sorted_until(const std::vector<T> &input) {
    return static_cast<std::size_t>(std::is_sorted_until(input.begin(), input.end()) - input.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &input, std::size_t middle) {
    auto output = input;
    std::rotate(output.begin(), output.begin() + static_cast<std::ptrdiff_t>(middle), output.end());
    return output;
  }

  template <class T> std::vector<T> rotate_copy(const std::vector<T> &input, std::size_t middle) {
    std::vector<T> output(input.size());
    std::rotate_copy(input.begin(), input.begin() + static_cast<std::ptrdiff_t>(middle), input.end(),
                     output.begin());
    return output;
  }

  template <class T> bool includes(const std::vector<T> &left, const std::vector<T> &right) {
    return std::includes(left.begin(), left.end(), right.begin(), right.end());
  }

  template <class T>
  std::vector<T> gather(const std::vector<T> &input, const std::vector<std::size_t> &indices) {
    std::vector<T> output(indices.size());
    for (std::size_t index = 0; index < indices.size(); ++index)
      output[index] = input[indices[index]];
    return output;
  }

  template <class T>
  std::vector<T> scatter(const std::vector<T> &input, const std::vector<std::size_t> &indices) {
    std::vector<T> output(input.size());
    for (std::size_t index = 0; index < indices.size(); ++index)
      output[indices[index]] = input[index];
    return output;
  }

  template <class T>
  algorithms::KeyValues<T> reduce_by_key(const std::vector<T> &keys,
                                         const std::vector<std::int32_t> &values) {
    algorithms::KeyValues<T> output;
    for (std::size_t index = 0; index < keys.size(); ++index) {
      if (output.keys.empty() || output.keys.back() != keys[index]) {
        output.keys.push_back(keys[index]);
        output.values.push_back(values[index]);
      } else {
        output.values.back() += values[index];
      }
    }
    return output;
  }

  template <class T>
  algorithms::KeyValues<T> inclusive_scan_by_key(const std::vector<T> &keys,
                                                 const std::vector<std::int32_t> &values) {
    algorithms::KeyValues<T> output{keys, values};
    for (std::size_t index = 1; index < keys.size(); ++index)
      if (keys[index] == keys[index - 1])
        output.values[index] += output.values[index - 1];
    return output;
  }

  template <class T>
  algorithms::KeyValues<T> exclusive_scan_by_key(const std::vector<T> &keys,
                                                 const std::vector<std::int32_t> &values) {
    algorithms::KeyValues<T> output{keys, std::vector<std::int32_t>(values.size())};
    std::int32_t running = 0;
    for (std::size_t index = 0; index < keys.size(); ++index) {
      if (index == 0 || keys[index] != keys[index - 1])
        running = 0;
      output.values[index] = running;
      running += values[index];
    }
    return output;
  }

  template <class T>
  algorithms::KeyValues<T> unique_by_key(const std::vector<T> &keys,
                                         const std::vector<std::int32_t> &values) {
    algorithms::KeyValues<T> output;
    for (std::size_t index = 0; index < keys.size(); ++index)
      if (index == 0 || keys[index] != keys[index - 1]) {
        output.keys.push_back(keys[index]);
        output.values.push_back(values[index]);
      }
    return output;
  }

  template <class T>
  algorithms::KeyValues<T> merge_by_key(const algorithms::KeyValues<T> &left,
                                        const algorithms::KeyValues<T> &right) {
    algorithms::KeyValues<T> output;
    std::size_t a = 0;
    std::size_t b = 0;
    while (a < left.keys.size() || b < right.keys.size()) {
      const bool take_left =
          b == right.keys.size() || (a < left.keys.size() && !(right.keys[b] < left.keys[a]));
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

  template <class T> std::vector<T> partial_sort(const std::vector<T> &input, std::size_t middle) {
    auto output = input;
    std::partial_sort(output.begin(), output.begin() + static_cast<std::ptrdiff_t>(middle), output.end());
    return output;
  }

  template <class T> std::vector<T> nth_element(const std::vector<T> &input, std::size_t nth) {
    auto output = input;
    if (!output.empty())
      std::nth_element(output.begin(), output.begin() + static_cast<std::ptrdiff_t>(nth), output.end());
    return output;
  }

  template <class T> std::vector<T> inplace_merge(const std::vector<T> &input, std::size_t middle) {
    auto output = input;
    std::inplace_merge(output.begin(), output.begin() + static_cast<std::ptrdiff_t>(middle), output.end());
    return output;
  }

private:
  template <class T>
  static algorithms::KeyValues<T> order_by_key(const std::vector<T> &keys,
                                               const std::vector<std::int32_t> &values, bool stable) {
    std::vector<std::size_t> order(keys.size());
    std::iota(order.begin(), order.end(), 0);
    const auto compare = [&](std::size_t left, std::size_t right) { return keys[left] < keys[right]; };
    if (stable)
      std::stable_sort(order.begin(), order.end(), compare);
    else
      std::sort(order.begin(), order.end(), compare);
    algorithms::KeyValues<T> result{std::vector<T>(keys.size()), std::vector<std::int32_t>(values.size())};
    for (std::size_t index = 0; index < order.size(); ++index) {
      result.keys[index] = keys[order[index]];
      result.values[index] = values[order[index]];
    }
    return result;
  }
};

} // namespace unified_suite::adapters

#endif
