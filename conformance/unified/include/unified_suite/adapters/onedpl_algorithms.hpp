#ifndef UNIFIED_SUITE_ADAPTERS_ONEDPL_ALGORITHMS_HPP
#define UNIFIED_SUITE_ADAPTERS_ONEDPL_ALGORITHMS_HPP

#include "unified_suite/adapters/sycl_support.hpp"
#include "unified_suite/algorithms.hpp"

#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>
#include <oneapi/dpl/numeric>
#include <sycl/sycl.hpp>

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <numeric>
#include <utility>
#include <vector>

namespace unified_suite::adapters {

namespace onedpl_detail {
template <class T, int Id> class Kernel;

template <class T> class Shared {
public:
  Shared(sycl::queue &queue, std::size_t size) : queue_(&queue), size_(size) {
    data_ = sycl::malloc_shared<T>(std::max<std::size_t>(size, 1), queue);
    if (!data_)
      throw std::bad_alloc();
  }

  Shared(sycl::queue &queue, const std::vector<T> &input) : Shared(queue, input.size()) {
    std::copy(input.begin(), input.end(), data_);
  }

  Shared(const Shared &) = delete;
  Shared &operator=(const Shared &) = delete;
  Shared(Shared &&) = delete;
  Shared &operator=(Shared &&) = delete;
  ~Shared() { sycl::free(data_, *queue_); }

  T *begin() { return data_; }
  const T *begin() const { return data_; }
  T *end() { return data_ + size_; }
  const T *end() const { return data_ + size_; }
  std::size_t size() const { return size_; }
  std::vector<T> host(std::size_t count) const { return {data_, data_ + count}; }
  std::vector<T> host() const { return host(size_); }

private:
  sycl::queue *queue_;
  T *data_{};
  std::size_t size_{};
};
} // namespace onedpl_detail

class OneDPLAlgorithms {
public:
  OneDPLAlgorithms() : queue_(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool supports(algorithms::Operation operation) const {
    if (operation == algorithms::Operation::gather || operation == algorithms::Operation::scatter ||
        operation == algorithms::Operation::unique_by_key ||
        operation == algorithms::Operation::merge_by_key ||
        operation == algorithms::Operation::partition_point)
      return false;
    return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device());
  }

  template <class T> std::vector<T> copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::copy(policy<T, 0>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> copy_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto end =
        std::copy_if(policy<T, 20>(), source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> copy_n(const std::vector<T> &input, std::size_t count) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, count);
    std::copy_n(policy<T, 21>(), source.begin(), count, output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> fill(std::size_t size, T value) {
    onedpl_detail::Shared<T> output(queue_, size);
    std::fill(policy<T, 22>(), output.begin(), output.end(), value);
    return finish(output);
  }

  template <class T> std::vector<T> generate(std::size_t size) {
    onedpl_detail::Shared<T> output(queue_, size);
    std::generate(policy<T, 23>(), output.begin(), output.end(), algorithms::Constant<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> for_each(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::for_each(policy<T, 24>(), output.begin(), output.end(), algorithms::Increment<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transform_unary(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::transform(policy<T, 1>(), source.begin(), source.end(), output.begin(), algorithms::Affine<T>{});
    return finish(output);
  }

  template <class T>
  std::vector<T> transform_binary(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, left.size());
    std::transform(policy<T, 2>(), a.begin(), a.end(), b.begin(), output.begin(), algorithms::Combine<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> replace_if(const std::vector<T> &input, T replacement) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::replace_if(policy<T, 25>(), output.begin(), output.end(), algorithms::IsEven<T>{}, replacement);
    return finish(output);
  }

  template <class T> std::vector<T> replace_copy_if(const std::vector<T> &input, T replacement) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::replace_copy_if(policy<T, 26>(), source.begin(), source.end(), output.begin(),
                         algorithms::IsEven<T>{}, replacement);
    return finish(output);
  }

  template <class T> std::vector<T> remove_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    const auto end = std::remove_if(policy<T, 27>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> remove_copy_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto end = std::remove_copy_if(policy<T, 28>(), source.begin(), source.end(), output.begin(),
                                         algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T>
  algorithms::TwoRanges<T> swap_ranges(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    std::swap_ranges(policy<T, 29>(), a.begin(), a.end(), b.begin());
    queue_.wait_and_throw();
    return {a.host(), b.host()};
  }

  template <class T> T reduce(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::reduce(policy<T, 3>(), source.begin(), source.end(), T{}, algorithms::Plus<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> T transform_reduce(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::transform_reduce(policy<T, 4>(), source.begin(), source.end(), T{},
                                              algorithms::Plus<T>{}, algorithms::Square<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> T inner_product(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    const auto result = std::transform_reduce(policy<T, 30>(), a.begin(), a.end(), b.begin(), T{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> inclusive_scan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::inclusive_scan(policy<T, 5>(), source.begin(), source.end(), output.begin(), algorithms::Plus<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> exclusive_scan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::exclusive_scan(policy<T, 6>(), source.begin(), source.end(), output.begin(), T{},
                        algorithms::Plus<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transform_inclusive_scan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::transform_inclusive_scan(policy<T, 31>(), source.begin(), source.end(), output.begin(),
                                  algorithms::Plus<T>{}, algorithms::Square<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transform_exclusive_scan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::transform_exclusive_scan(policy<T, 32>(), source.begin(), source.end(), output.begin(), T{},
                                  algorithms::Plus<T>{}, algorithms::Square<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> sort(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::sort(policy<T, 7>(), output.begin(), output.end(), algorithms::Less<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> stable_sort(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::stable_sort(policy<T, 33>(), output.begin(), output.end(), algorithms::Less<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> sort_descending(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::sort(policy<T, 34>(), output.begin(), output.end(), algorithms::Greater<T>{});
    return finish(output);
  }

  template <class T>
  algorithms::KeyValues<T> sort_by_key(const std::vector<T> &keys, const std::vector<std::int32_t> &values) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<std::int32_t> device_values(queue_, values);
    oneapi::dpl::sort_by_key(policy<T, 8>(), device_keys.begin(), device_keys.end(), device_values.begin(),
                             algorithms::Less<T>{});
    queue_.wait_and_throw();
    return {device_keys.host(), device_values.host()};
  }

  template <class T>
  algorithms::KeyValues<T> stable_sort_by_key(const std::vector<T> &keys,
                                              const std::vector<std::int32_t> &values) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<std::int32_t> device_values(queue_, values);
    oneapi::dpl::stable_sort_by_key(policy<T, 9>(), device_keys.begin(), device_keys.end(),
                                    device_values.begin(), algorithms::Less<T>{});
    queue_.wait_and_throw();
    return {device_keys.host(), device_values.host()};
  }

  template <class T> std::vector<T> unique(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    const auto end = std::unique(policy<T, 10>(), output.begin(), output.end());
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> unique_copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto end = std::unique_copy(policy<T, 35>(), source.begin(), source.end(), output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T> std::size_t count(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::count(policy<T, 36>(), source.begin(), source.end(), value);
    queue_.wait_and_throw();
    return static_cast<std::size_t>(result);
  }

  template <class T> std::size_t count_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::count_if(policy<T, 11>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return static_cast<std::size_t>(result);
  }

  template <class T> std::size_t find_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::find_if(policy<T, 12>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return static_cast<std::size_t>(result - source.begin());
  }

  template <class T> std::size_t find(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::find(policy<T, 37>(), source.begin(), source.end(), value);
    queue_.wait_and_throw();
    return static_cast<std::size_t>(result - source.begin());
  }

  template <class T> bool all_of(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::all_of(policy<T, 38>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> bool any_of(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::any_of(policy<T, 39>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> bool none_of(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::none_of(policy<T, 40>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> bool equal(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    const auto result = std::equal(policy<T, 41>(), a.begin(), a.end(), b.begin());
    queue_.wait_and_throw();
    return result;
  }

  template <class T>
  algorithms::TwoPositions mismatch(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    const auto result = std::mismatch(policy<T, 42>(), a.begin(), a.end(), b.begin());
    queue_.wait_and_throw();
    return {static_cast<std::size_t>(result.first - a.begin()),
            static_cast<std::size_t>(result.second - b.begin())};
  }

  template <class T> std::size_t lower_bound(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> needles(queue_, std::vector<T>{value});
    onedpl_detail::Shared<std::size_t> results(queue_, 1);
    oneapi::dpl::lower_bound(policy<T, 13>(), source.begin(), source.end(), needles.begin(), needles.end(),
                             results.begin(), algorithms::Less<T>{});
    queue_.wait_and_throw();
    return static_cast<std::size_t>(results.begin()[0]);
  }

  template <class T> std::size_t upper_bound(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> needles(queue_, std::vector<T>{value});
    onedpl_detail::Shared<std::size_t> results(queue_, 1);
    oneapi::dpl::upper_bound(policy<T, 43>(), source.begin(), source.end(), needles.begin(), needles.end(),
                             results.begin(), algorithms::Less<T>{});
    queue_.wait_and_throw();
    return static_cast<std::size_t>(results.begin()[0]);
  }

  template <class T> bool binary_search(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> needles(queue_, std::vector<T>{value});
    onedpl_detail::Shared<std::uint8_t> results(queue_, 1);
    oneapi::dpl::binary_search(policy<T, 44>(), source.begin(), source.end(), needles.begin(), needles.end(),
                               results.begin(), algorithms::Less<T>{});
    queue_.wait_and_throw();
    return results.begin()[0] != 0;
  }

  template <class T> algorithms::TwoPositions equal_range(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> needles(queue_, std::vector<T>{value});
    onedpl_detail::Shared<std::size_t> lower(queue_, 1);
    onedpl_detail::Shared<std::size_t> upper(queue_, 1);
    oneapi::dpl::lower_bound(policy<T, 45>(), source.begin(), source.end(), needles.begin(), needles.end(),
                             lower.begin(), algorithms::Less<T>{});
    oneapi::dpl::upper_bound(policy<T, 46>(), source.begin(), source.end(), needles.begin(), needles.end(),
                             upper.begin(), algorithms::Less<T>{});
    queue_.wait_and_throw();
    return {static_cast<std::size_t>(lower.begin()[0]), static_cast<std::size_t>(upper.begin()[0])};
  }

  template <class T> std::vector<T> merge(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, left.size() + right.size());
    std::merge(policy<T, 14>(), a.begin(), a.end(), b.begin(), b.end(), output.begin(),
               algorithms::Less<T>{});
    return finish(output);
  }

  template <class T> algorithms::Partitioned<T> partition(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    const auto boundary =
        std::partition(policy<T, 15>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return {output.host(), static_cast<std::size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::Partitioned<T> stable_partition(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    const auto boundary =
        std::stable_partition(policy<T, 47>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return {output.host(), static_cast<std::size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::TwoRanges<T> partition_copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> selected(queue_, input.size());
    onedpl_detail::Shared<T> rejected(queue_, input.size());
    const auto ends = std::partition_copy(policy<T, 48>(), source.begin(), source.end(), selected.begin(),
                                          rejected.begin(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return {selected.host(static_cast<std::size_t>(ends.first - selected.begin())),
            rejected.host(static_cast<std::size_t>(ends.second - rejected.begin()))};
  }

  template <class T> bool is_partitioned(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result =
        std::is_partitioned(policy<T, 49>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::size_t partition_point(const std::vector<T> &) {
    throw Unsupported("partition-point has no oneDPL execution-policy overload");
  }

  template <class T> std::vector<T> adjacent_difference(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::adjacent_difference(policy<T, 16>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> set_union(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, left.size() + right.size());
    const auto end = std::set_union(policy<T, 17>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T>
  std::vector<T> set_intersection(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, std::min(left.size(), right.size()));
    const auto end =
        std::set_intersection(policy<T, 18>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> set_difference(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, left.size());
    const auto end =
        std::set_difference(policy<T, 51>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T>
  std::vector<T> set_symmetric_difference(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, left.size() + right.size());
    const auto end = std::set_symmetric_difference(policy<T, 52>(), a.begin(), a.end(), b.begin(), b.end(),
                                                   output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<std::size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> reverse(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::reverse(policy<T, 19>(), output.begin(), output.end());
    return finish(output);
  }

  template <class T> std::vector<T> reverse_copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::reverse_copy(policy<T, 53>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::size_t min_element(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::min_element(policy<T, 54>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return static_cast<std::size_t>(result - source.begin());
  }

  template <class T> std::size_t max_element(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::max_element(policy<T, 55>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return static_cast<std::size_t>(result - source.begin());
  }

  template <class T> algorithms::TwoPositions minmax_element(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::minmax_element(policy<T, 56>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return {static_cast<std::size_t>(result.first - source.begin()),
            static_cast<std::size_t>(result.second - source.begin())};
  }

  template <class T> bool is_sorted(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::is_sorted(policy<T, 57>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::size_t is_sorted_until(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::is_sorted_until(policy<T, 58>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return static_cast<std::size_t>(result - source.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &input, std::size_t middle) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::rotate(policy<T, 59>(), output.begin(), output.begin() + static_cast<std::ptrdiff_t>(middle),
                output.end());
    return finish(output);
  }

  template <class T> std::vector<T> rotate_copy(const std::vector<T> &input, std::size_t middle) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::rotate_copy(policy<T, 60>(), source.begin(), source.begin() + static_cast<std::ptrdiff_t>(middle),
                     source.end(), output.begin());
    return finish(output);
  }

  template <class T> bool includes(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    const auto result = std::includes(policy<T, 61>(), a.begin(), a.end(), b.begin(), b.end());
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> gather(const std::vector<T> &, const std::vector<std::size_t> &) {
    throw Unsupported("gather is not a oneDPL algorithm API");
  }

  template <class T> std::vector<T> scatter(const std::vector<T> &, const std::vector<std::size_t> &) {
    throw Unsupported("scatter is not a oneDPL algorithm API");
  }

  template <class T>
  algorithms::KeyValues<T> reduce_by_key(const std::vector<T> &keys,
                                         const std::vector<std::int32_t> &values) {
    onedpl_detail::Shared<T> source_keys(queue_, keys);
    onedpl_detail::Shared<std::int32_t> source_values(queue_, values);
    onedpl_detail::Shared<T> output_keys(queue_, keys.size());
    onedpl_detail::Shared<std::int32_t> output_values(queue_, values.size());
    const auto ends =
        oneapi::dpl::reduce_by_segment(policy<T, 65>(), source_keys.begin(), source_keys.end(),
                                       source_values.begin(), output_keys.begin(), output_values.begin());
    queue_.wait_and_throw();
    return {output_keys.host(static_cast<std::size_t>(ends.first - output_keys.begin())),
            output_values.host(static_cast<std::size_t>(ends.second - output_values.begin()))};
  }

  template <class T>
  algorithms::KeyValues<T> inclusive_scan_by_key(const std::vector<T> &keys,
                                                 const std::vector<std::int32_t> &values) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<std::int32_t> source(queue_, values);
    onedpl_detail::Shared<std::int32_t> output(queue_, values.size());
    oneapi::dpl::inclusive_scan_by_segment(policy<T, 66>(), device_keys.begin(), device_keys.end(),
                                           source.begin(), output.begin());
    queue_.wait_and_throw();
    return {keys, output.host()};
  }

  template <class T>
  algorithms::KeyValues<T> exclusive_scan_by_key(const std::vector<T> &keys,
                                                 const std::vector<std::int32_t> &values) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<std::int32_t> source(queue_, values);
    onedpl_detail::Shared<std::int32_t> output(queue_, values.size());
    oneapi::dpl::exclusive_scan_by_segment(policy<T, 67>(), device_keys.begin(), device_keys.end(),
                                           source.begin(), output.begin(), std::int32_t{0});
    queue_.wait_and_throw();
    return {keys, output.host()};
  }

  template <class T>
  algorithms::KeyValues<T> unique_by_key(const std::vector<T> &, const std::vector<std::int32_t> &) {
    throw Unsupported("unique-by-key is not a oneDPL algorithm API");
  }

  template <class T>
  algorithms::KeyValues<T> merge_by_key(const algorithms::KeyValues<T> &, const algorithms::KeyValues<T> &) {
    throw Unsupported("merge-by-key is not a oneDPL algorithm API");
  }

  template <class T> std::vector<T> partial_sort(const std::vector<T> &input, std::size_t middle) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::partial_sort(policy<T, 62>(), output.begin(), output.begin() + static_cast<std::ptrdiff_t>(middle),
                      output.end());
    return finish(output);
  }

  template <class T> std::vector<T> nth_element(const std::vector<T> &input, std::size_t nth) {
    onedpl_detail::Shared<T> output(queue_, input);
    if (!input.empty())
      std::nth_element(policy<T, 63>(), output.begin(), output.begin() + static_cast<std::ptrdiff_t>(nth),
                       output.end());
    return finish(output);
  }

  template <class T> std::vector<T> inplace_merge(const std::vector<T> &input, std::size_t middle) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::inplace_merge(policy<T, 64>(), output.begin(), output.begin() + static_cast<std::ptrdiff_t>(middle),
                       output.end());
    return finish(output);
  }

private:
  sycl::queue queue_;

  template <class T, int Id> auto policy() {
    return oneapi::dpl::execution::make_device_policy<onedpl_detail::Kernel<T, Id>>(queue_);
  }

  template <class T> std::vector<T> finish(const onedpl_detail::Shared<T> &output) {
    queue_.wait_and_throw();
    return output.host();
  }
};

} // namespace unified_suite::adapters

#endif
