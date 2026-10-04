#pragma once

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <new>
#include <numeric>
#include <stdexcept>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>
#include <oneapi/dpl/iterator>
#include <oneapi/dpl/memory>
#include <oneapi/dpl/numeric>
#include <sycl/sycl.hpp>

#include "algorithm_cases.hpp"
#include "capability_defaults.hpp"
#include "edge_cases.hpp"

namespace unified::backend::sycl_support {
template <class T, size_t Id> class Kernel;

template <class T> class Shared {
public:
  Shared(sycl::queue &queue, size_t size) : queue_(&queue), size_(size) {
    data_ = sycl::malloc_device<T>(std::max<size_t>(size, 1), queue);
    if (!data_) throw std::bad_alloc();
  }

  Shared(sycl::queue &queue, const std::vector<T> &input) : Shared(queue, input.size()) {
    if (!input.empty()) queue.memcpy(data_, input.data(), input.size() * sizeof(T)).wait_and_throw();
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
  size_t size() const { return size_; }
  std::vector<T> host(size_t count) const {
    if (count > size_) throw std::out_of_range("SYCL download exceeds allocation");
    std::vector<T> result(count);
    if (count != 0) queue_->memcpy(result.data(), data_, count * sizeof(T)).wait_and_throw();
    return result;
  }
  std::vector<T> host() const { return host(size_); }

private:
  sycl::queue *queue_;
  T *data_{};
  size_t size_{};
};

inline bool enabled(std::string_view variable) {
  const auto *value = std::getenv(std::string(variable).c_str());
  return value != nullptr && std::string_view(value) == "1";
}

inline bool supports_fp64(const sycl::device &device) {
  return device.has(sycl::aspect::fp64) || (enabled("OverrideDefaultFP64Settings") && enabled("IGC_EnableDPEmulation"));
}
} // namespace unified::backend::sycl_support

namespace unified::backend {
namespace onedpl_detail = sycl_support;

class OneDPLAlgorithms : public AlgorithmCapabilities<OneDPLAlgorithms> {
public:
  OneDPLAlgorithms() : queue_(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool capability() const { return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device()); }

  template <class T> bool supports_shuffle(size_t) const { return false; }

  template <class T> bool supports_partition_point(size_t) const { return false; }

  template <class T> bool supports_set_intersection(size_t size) const { return supports_policy_input<T>(size); }

  template <class T> bool supports_set_difference(size_t size) const { return supports_policy_input<T>(size); }

  template <class T> bool supports_set_symmetric_difference(size_t size) const { return supports_policy_input<T>(size); }

  template <class T> bool supports_gather(size_t) const { return false; }

  template <class T> bool supports_scatter(size_t) const { return false; }

  template <class T> bool supports_unique_by_key(size_t) const { return false; }

  template <class T> bool supports_merge_by_key(size_t) const { return false; }

  template <class T> bool supports_partial_sort(size_t size) const { return supports_policy_input<T>(size); }

  template <class T> bool supports_nth_element(size_t size) const { return supports_policy_input<T>(size); }

  template <class T> std::vector<T> copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::copy(policy<T, 0>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> copy_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto end = std::copy_if(policy<T, 20>(), source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> copy_n(const std::vector<T> &input, size_t count) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, count);
    std::copy_n(policy<T, 21>(), source.begin(), count, output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> uninitialized_copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::uninitialized_copy(policy<T, 68>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> uninitialized_fill(size_t size, T value) {
    onedpl_detail::Shared<T> output(queue_, size);
    std::uninitialized_fill(policy<T, 69>(), output.begin(), output.end(), value);
    return finish(output);
  }

  template <class T> std::vector<T> fill(size_t size, T value) {
    onedpl_detail::Shared<T> output(queue_, size);
    std::fill(policy<T, 22>(), output.begin(), output.end(), value);
    return finish(output);
  }

  template <class T> std::vector<T> generate(size_t size) {
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

  template <class T> std::vector<T> transform_binary(const std::vector<T> &left, const std::vector<T> &right) {
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
    std::replace_copy_if(policy<T, 26>(), source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{}, replacement);
    return finish(output);
  }

  template <class T> std::vector<T> remove_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    const auto end = std::remove_if(policy<T, 27>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> remove_copy_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto end = std::remove_copy_if(policy<T, 28>(), source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> algorithms::TwoRanges<T> swap_ranges(const std::vector<T> &left, const std::vector<T> &right) {
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
    const auto result =
        std::transform_reduce(policy<T, 4>(), source.begin(), source.end(), T{}, algorithms::Plus<T>{}, algorithms::Square<T>{});
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
    std::exclusive_scan(policy<T, 6>(), source.begin(), source.end(), output.begin(), T{}, algorithms::Plus<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transform_inclusive_scan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::transform_inclusive_scan(policy<T, 31>(), source.begin(), source.end(), output.begin(), algorithms::Plus<T>{},
                                  algorithms::Square<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transform_exclusive_scan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::transform_exclusive_scan(policy<T, 32>(), source.begin(), source.end(), output.begin(), T{}, algorithms::Plus<T>{},
                                  algorithms::Square<T>{});
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

  template <class T> algorithms::KeyValues<T> sort_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<int32_t> device_values(queue_, values);
    oneapi::dpl::sort_by_key(policy<T, 8>(), device_keys.begin(), device_keys.end(), device_values.begin(), algorithms::Less<T>{});
    queue_.wait_and_throw();
    return {device_keys.host(), device_values.host()};
  }

  template <class T> algorithms::KeyValues<T> stable_sort_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<int32_t> device_values(queue_, values);
    oneapi::dpl::stable_sort_by_key(policy<T, 9>(), device_keys.begin(), device_keys.end(), device_values.begin(), algorithms::Less<T>{});
    queue_.wait_and_throw();
    return {device_keys.host(), device_values.host()};
  }

  template <class T> std::vector<T> unique(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    const auto end = std::unique(policy<T, 10>(), output.begin(), output.end());
    queue_.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> unique_copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto end = std::unique_copy(policy<T, 35>(), source.begin(), source.end(), output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> size_t count(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::count(policy<T, 36>(), source.begin(), source.end(), value);
    queue_.wait_and_throw();
    return static_cast<size_t>(result);
  }

  template <class T> size_t count_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::count_if(policy<T, 11>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return static_cast<size_t>(result);
  }

  template <class T> size_t find_if(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::find_if(policy<T, 12>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> size_t find(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::find(policy<T, 37>(), source.begin(), source.end(), value);
    queue_.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
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

  template <class T> algorithms::TwoPositions mismatch(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    const auto result = std::mismatch(policy<T, 42>(), a.begin(), a.end(), b.begin());
    queue_.wait_and_throw();
    return {static_cast<size_t>(result.first - a.begin()), static_cast<size_t>(result.second - b.begin())};
  }

  template <class T> size_t lower_bound(const std::vector<T> &input, T value) {
    // oneDPL leaves vector-search output untouched for an empty haystack.
    if (input.empty()) return 0;
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> needles(queue_, std::vector<T>{value});
    onedpl_detail::Shared<size_t> results(queue_, 1);
    oneapi::dpl::lower_bound(policy<T, 13>(), source.begin(), source.end(), needles.begin(), needles.end(), results.begin(),
                             algorithms::Less<T>{});
    queue_.wait_and_throw();
    return results.host(1)[0];
  }

  template <class T> size_t upper_bound(const std::vector<T> &input, T value) {
    // oneDPL leaves vector-search output untouched for an empty haystack.
    if (input.empty()) return 0;
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> needles(queue_, std::vector<T>{value});
    onedpl_detail::Shared<size_t> results(queue_, 1);
    oneapi::dpl::upper_bound(policy<T, 43>(), source.begin(), source.end(), needles.begin(), needles.end(), results.begin(),
                             algorithms::Less<T>{});
    queue_.wait_and_throw();
    return results.host(1)[0];
  }

  template <class T> bool binary_search(const std::vector<T> &input, T value) {
    // oneDPL leaves vector-search output untouched for an empty haystack.
    if (input.empty()) return false;
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> needles(queue_, std::vector<T>{value});
    onedpl_detail::Shared<uint8_t> results(queue_, 1);
    oneapi::dpl::binary_search(policy<T, 44>(), source.begin(), source.end(), needles.begin(), needles.end(), results.begin(),
                               algorithms::Less<T>{});
    queue_.wait_and_throw();
    return results.host(1)[0] != 0;
  }

  template <class T> algorithms::TwoPositions equal_range(const std::vector<T> &input, T value) {
    // oneDPL leaves vector-search output untouched for an empty haystack.
    if (input.empty()) return {0, 0};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> needles(queue_, std::vector<T>{value});
    onedpl_detail::Shared<size_t> lower(queue_, 1);
    onedpl_detail::Shared<size_t> upper(queue_, 1);
    oneapi::dpl::lower_bound(policy<T, 45>(), source.begin(), source.end(), needles.begin(), needles.end(), lower.begin(),
                             algorithms::Less<T>{});
    oneapi::dpl::upper_bound(policy<T, 46>(), source.begin(), source.end(), needles.begin(), needles.end(), upper.begin(),
                             algorithms::Less<T>{});
    queue_.wait_and_throw();
    return {lower.host(1)[0], upper.host(1)[0]};
  }

  template <class T> std::vector<T> merge(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, left.size() + right.size());
    std::merge(policy<T, 14>(), a.begin(), a.end(), b.begin(), b.end(), output.begin(), algorithms::Less<T>{});
    return finish(output);
  }

  template <class T> algorithms::Partitioned<T> partition(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    const auto boundary = std::partition(policy<T, 15>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return {output.host(), static_cast<size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::Partitioned<T> stable_partition(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue_, input);
    const auto boundary = std::stable_partition(policy<T, 47>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return {output.host(), static_cast<size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::TwoRanges<T> partition_copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> selected(queue_, input.size());
    onedpl_detail::Shared<T> rejected(queue_, input.size());
    const auto ends =
        std::partition_copy(policy<T, 48>(), source.begin(), source.end(), selected.begin(), rejected.begin(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return {selected.host(static_cast<size_t>(ends.first - selected.begin())),
            rejected.host(static_cast<size_t>(ends.second - rejected.begin()))};
  }

  template <class T> bool is_partitioned(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::is_partitioned(policy<T, 49>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> size_t partition_point(const std::vector<T> &) { SKIP("partition-point has no oneDPL execution-policy overload"); }

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
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> set_intersection(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, std::min(left.size(), right.size()));
    const auto end = std::set_intersection(policy<T, 18>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> set_difference(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, left.size());
    const auto end = std::set_difference(policy<T, 51>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> set_symmetric_difference(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    onedpl_detail::Shared<T> output(queue_, left.size() + right.size());
    const auto end = std::set_symmetric_difference(policy<T, 52>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue_.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
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

  template <class T> std::vector<T> shuffle(const std::vector<T> &) { SKIP("shuffle has no oneDPL execution-policy overload"); }

  template <class T> size_t min_element(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::min_element(policy<T, 54>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> size_t max_element(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::max_element(policy<T, 55>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> algorithms::TwoPositions minmax_element(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::minmax_element(policy<T, 56>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return {static_cast<size_t>(result.first - source.begin()), static_cast<size_t>(result.second - source.begin())};
  }

  template <class T> bool is_sorted(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::is_sorted(policy<T, 57>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return result;
  }

  template <class T> size_t is_sorted_until(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = std::is_sorted_until(policy<T, 58>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &input, size_t middle) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::rotate(policy<T, 59>(), output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return finish(output);
  }

  template <class T> std::vector<T> rotate_copy(const std::vector<T> &input, size_t middle) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    std::rotate_copy(policy<T, 60>(), source.begin(), source.begin() + static_cast<ptrdiff_t>(middle), source.end(), output.begin());
    return finish(output);
  }

  template <class T> bool includes(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue_, left);
    onedpl_detail::Shared<T> b(queue_, right);
    const auto result = std::includes(policy<T, 61>(), a.begin(), a.end(), b.begin(), b.end());
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> gather(const std::vector<T> &, const std::vector<size_t> &) {
    SKIP("gather is not a oneDPL algorithm API");
  }

  template <class T> std::vector<T> scatter(const std::vector<T> &, const std::vector<size_t> &) {
    SKIP("scatter is not a oneDPL algorithm API");
  }

  template <class T> algorithms::KeyValues<T> reduce_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> source_keys(queue_, keys);
    onedpl_detail::Shared<int32_t> source_values(queue_, values);
    onedpl_detail::Shared<T> output_keys(queue_, keys.size());
    onedpl_detail::Shared<int32_t> output_values(queue_, values.size());
    const auto ends = oneapi::dpl::reduce_by_segment(policy<T, 65>(), source_keys.begin(), source_keys.end(), source_values.begin(),
                                                     output_keys.begin(), output_values.begin());
    queue_.wait_and_throw();
    return {output_keys.host(static_cast<size_t>(ends.first - output_keys.begin())),
            output_values.host(static_cast<size_t>(ends.second - output_values.begin()))};
  }

  template <class T> algorithms::KeyValues<T> inclusive_scan_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<int32_t> source(queue_, values);
    onedpl_detail::Shared<int32_t> output(queue_, values.size());
    oneapi::dpl::inclusive_scan_by_segment(policy<T, 66>(), device_keys.begin(), device_keys.end(), source.begin(), output.begin());
    queue_.wait_and_throw();
    return {keys, output.host()};
  }

  template <class T> algorithms::KeyValues<T> exclusive_scan_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<int32_t> source(queue_, values);
    // Every output must be written, including zero-valued segment starts.
    onedpl_detail::Shared<int32_t> output(queue_, std::vector<int32_t>(values.size(), std::numeric_limits<int32_t>::min()));
    oneapi::dpl::exclusive_scan_by_segment(policy<T, 67>(), device_keys.begin(), device_keys.end(), source.begin(), output.begin(),
                                           int32_t{0});
    queue_.wait_and_throw();
    return {keys, output.host()};
  }

  template <class T> algorithms::KeyValues<T> unique_by_key(const std::vector<T> &, const std::vector<int32_t> &) {
    SKIP("unique-by-key is not a oneDPL algorithm API");
  }

  template <class T> algorithms::KeyValues<T> merge_by_key(const algorithms::KeyValues<T> &, const algorithms::KeyValues<T> &) {
    SKIP("merge-by-key is not a oneDPL algorithm API");
  }

  template <class T> std::vector<T> partial_sort(const std::vector<T> &input, size_t middle) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::partial_sort(policy<T, 62>(), output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return finish(output);
  }

  template <class T> std::vector<T> nth_element(const std::vector<T> &input, size_t nth) {
    onedpl_detail::Shared<T> output(queue_, input);
    if (!input.empty()) std::nth_element(policy<T, 63>(), output.begin(), output.begin() + static_cast<ptrdiff_t>(nth), output.end());
    return finish(output);
  }

  template <class T> std::vector<T> inplace_merge(const std::vector<T> &input, size_t middle) {
    onedpl_detail::Shared<T> output(queue_, input);
    std::inplace_merge(policy<T, 64>(), output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return finish(output);
  }

private:
  sycl::queue queue_;
  // Restrict the observed oneDPL faults to the tested Arc A750 device.
  const bool arcPolicyFaults_ =
      queue_.get_backend() == sycl::backend::ext_oneapi_level_zero
      && queue_.get_device().get_info<sycl::info::device::name>().find("Intel(R) Arc(TM) A750") != std::string::npos;

  template <class T> bool supports_policy_input(size_t size) const {
    return !(sizeof(T) == 4 && size >= 31 && arcPolicyFaults_) && capability<T>();
  }

  template <class T, size_t Id> auto policy() { return oneapi::dpl::execution::make_device_policy<onedpl_detail::Kernel<T, Id>>(queue_); }

  template <class T> std::vector<T> finish(const onedpl_detail::Shared<T> &output) {
    queue_.wait_and_throw();
    return output.host();
  }
};
} // namespace unified::backend

namespace unified::backend {

namespace onedpl_edge_detail {
template <class T, size_t Id> class Kernel;
template <class T, size_t Id> class DiamondLeft;
template <class T, size_t Id> class DiamondRight;
template <class T, size_t Id> class DiamondJoin;
template <class T, size_t Id> class BlockScan;
} // namespace onedpl_edge_detail

class OneDPLEdgeCases : public EdgeCapabilities<OneDPLEdgeCases> {
public:
  OneDPLEdgeCases() : queue_(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool capability() const { return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device()); }

  template <class T> bool supports_runtime_error_recover() const { return false; }

  template <class T> int64_t composed_iterator_reduce(size_t size) {
    const oneapi::dpl::counting_iterator<size_t> indices(0);
    const auto source = oneapi::dpl::make_transform_iterator(indices, edge::ValueAt<T>{});
    const auto reverse = oneapi::dpl::make_permutation_iterator(source, edge::ReverseIndex{size});
    const auto result = oneapi::dpl::transform_reduce(policy<T, 0>(), reverse, reverse + static_cast<ptrdiff_t>(size), source, int64_t{},
                                                      std::plus<int64_t>{}, edge::WidePairValue<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> uint64_t biased_index_checksum(uint64_t base, size_t size) {
    const oneapi::dpl::counting_iterator<uint64_t> begin(base);
    const auto result =
        oneapi::dpl::transform_reduce(policy<T, 1>(), begin, begin + size, uint64_t{}, edge::Xor64{}, edge::BiasedHash<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> segmented_inclusive_scan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
    onedpl_detail::Shared<int32_t> device_keys(queue_, keys);
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    oneapi::dpl::inclusive_scan_by_segment(policy<T, 2>(), device_keys.begin(), device_keys.end(), source.begin(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> inplace_inclusive_scan(const std::vector<T> &storage, size_t offset, size_t count) {
    onedpl_detail::Shared<T> output(queue_, storage);
    oneapi::dpl::inclusive_scan(policy<T, 3>(), output.begin() + static_cast<ptrdiff_t>(offset),
                                output.begin() + static_cast<ptrdiff_t>(offset + count), output.begin() + static_cast<ptrdiff_t>(offset));
    return finish(output);
  }

  template <class T> support::KeyValues<T> stable_sort_payload(const std::vector<T> &keys, const std::vector<int32_t> &payload) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<int32_t> device_payload(queue_, payload);
    oneapi::dpl::stable_sort_by_key(policy<T, 4>(), device_keys.begin(), device_keys.end(), device_payload.begin());
    queue_.wait_and_throw();
    return {device_keys.host(), device_payload.host()};
  }

  template <class T> std::vector<T> dependency_diamond(const std::vector<T> &input) {
    if (input.empty()) return {};
    const auto device = queue_.get_device();
    const auto context = queue_.get_context();
    sycl::queue left_queue(context, device);
    sycl::queue right_queue(context, device);
    sycl::queue join_queue(context, device);
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> left(queue_, input.size());
    onedpl_detail::Shared<T> right(queue_, input.size());
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto count = input.size();
    const auto *source_data = source.begin();
    auto *left_data = left.begin();
    auto *right_data = right.begin();
    auto *output_data = output.begin();
    const auto left_event = left_queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::DiamondLeft<T, 0>>(
          count, [=](sycl::id<1> index) { left_data[index] = support::Affine<T>{}(source_data[index]); });
    });
    const auto right_event = right_queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::DiamondRight<T, 0>>(
          count, [=](sycl::id<1> index) { right_data[index] = support::Square<T>{}(source_data[index]); });
    });
    join_queue.submit([&](sycl::handler &handler) {
      handler.depends_on({left_event, right_event});
      handler.parallel_for<onedpl_edge_detail::DiamondJoin<T, 0>>(
          count, [=](sycl::id<1> index) { output_data[index] = support::Combine<T>{}(left_data[index], right_data[index]); });
    });
    join_queue.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> numeric_edge_roundtrip(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    oneapi::dpl::transform(policy<T, 5>(), source.begin(), source.end(), output.begin(), edge::Identity<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> primitive_block_scan(const std::vector<T> &input, size_t block_size) {
    if (block_size != 32) SKIP("SYCL group scan currently requires a block size of 32");
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto global = ((input.size() + block_size - 1) / block_size) * block_size;
    const auto size = input.size();
    const auto *source_data = source.begin();
    auto *output_data = output.begin();
    queue_.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::BlockScan<T, 0>>(sycl::nd_range<1>(global, block_size), [=](sycl::nd_item<1> item) {
        const auto index = item.get_global_linear_id();
        const auto value = index < size ? source_data[index] : T{};
        const auto scanned = sycl::inclusive_scan_over_group(item.get_group(), value, sycl::plus<T>{});
        if (index < size) output_data[index] = scanned;
      });
    });
    return finish(output);
  }

  template <class T> std::vector<T> runtime_error_recover(const std::vector<T> &) {
    SKIP("oneDPL does not expose an equivalent recoverable runtime-error path");
  }

private:
  sycl::queue queue_;

  template <class T, size_t Id> auto policy() {
    return oneapi::dpl::execution::make_device_policy<onedpl_edge_detail::Kernel<T, Id>>(queue_);
  }

  template <class T> std::vector<T> finish(const onedpl_detail::Shared<T> &output) {
    queue_.wait_and_throw();
    return output.host();
  }
};
namespace onedpl_async_detail {
template <class T, size_t Id> class Kernel;
template <class T, size_t Id> class FutureStage;
template <class T, size_t Id> class EventStage;
template <class T, size_t Id> class InterleavedStage;
} // namespace onedpl_async_detail

class OneDPLAsync {
public:
  OneDPLAsync() : queue_(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool supports_copy() const { return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device()); }

  template <class T> bool supports_reduce() const { return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device()); }

  template <class T> bool supports_scan() const { return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device()); }

  template <class T> bool supports_sort() const { return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device()); }

  template <class T> bool supports_future_chain() const {
    return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device());
  }

  template <class T> bool supports_event_chain() const {
    return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device());
  }

  template <class T> bool supports_interleaved_streams() const {
    return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device());
  }

  template <class T> std::vector<T> async_copy(const std::vector<T> &input) {
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    auto event = queue_.memcpy(output.begin(), source.begin(), input.size() * sizeof(T));
    event.wait_and_throw();
    return output.host();
  }

  template <class T> T async_reduce(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result = oneapi::dpl::reduce(policy<T, 0>(), source.begin(), source.end(), T{}, support::Plus<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> async_scan(const std::vector<T> &input) {
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    oneapi::dpl::inclusive_scan(policy<T, 1>(), source.begin(), source.end(), output.begin(), support::Plus<T>{});
    queue_.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> async_sort(const std::vector<T> &input) {
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue_, input);
    oneapi::dpl::sort(policy<T, 2>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return source.host();
  }

  template <class T> std::vector<T> async_future_chain(const std::vector<T> &input) {
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> first(queue_, input.size());
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto count = input.size();
    const auto *source_data = source.begin();
    auto *first_data = first.begin();
    auto *output_data = output.begin();
    const auto first_event = queue_.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::FutureStage<T, 0>>(
          count, [=](sycl::id<1> index) { first_data[index] = support::Affine<T>{}(source_data[index]); });
    });
    auto second_event = queue_.submit([&](sycl::handler &handler) {
      handler.depends_on(first_event);
      handler.parallel_for<onedpl_async_detail::FutureStage<T, 1>>(
          count, [=](sycl::id<1> index) { output_data[index] = support::Square<T>{}(first_data[index]); });
    });
    second_event.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> async_event_chain(const std::vector<T> &input) {
    if (input.empty()) return {};
    const auto device = queue_.get_device();
    const auto context = queue_.get_context();
    sycl::queue left_queue(context, device);
    sycl::queue right_queue(context, device);
    sycl::queue join_queue(context, device);
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> left(queue_, input.size());
    onedpl_detail::Shared<T> right(queue_, input.size());
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto count = input.size();
    const auto *source_data = source.begin();
    auto *left_data = left.begin();
    auto *right_data = right.begin();
    auto *output_data = output.begin();
    const auto left_event = left_queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::EventStage<T, 0>>(
          count, [=](sycl::id<1> index) { left_data[index] = support::Affine<T>{}(source_data[index]); });
    });
    const auto right_event = right_queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::EventStage<T, 1>>(
          count, [=](sycl::id<1> index) { right_data[index] = support::Square<T>{}(source_data[index]); });
    });
    auto join_event = join_queue.submit([&](sycl::handler &handler) {
      handler.depends_on({left_event, right_event});
      handler.parallel_for<onedpl_async_detail::EventStage<T, 2>>(
          count, [=](sycl::id<1> index) { output_data[index] = support::Combine<T>{}(left_data[index], right_data[index]); });
    });
    join_event.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> async_interleaved_streams(const std::vector<T> &input) {
    std::vector<T> even;
    std::vector<T> odd;
    even.reserve((input.size() + 1) / 2);
    odd.reserve(input.size() / 2);
    for (size_t index = 0; index < input.size(); ++index)
      (index % 2 == 0 ? even : odd).push_back(input[index]);
    if (input.empty()) return {};

    const auto device = queue_.get_device();
    const auto context = queue_.get_context();
    sycl::queue even_queue(context, device);
    sycl::queue odd_queue(context, device);
    onedpl_detail::Shared<T> even_data(queue_, even);
    onedpl_detail::Shared<T> odd_data(queue_, odd);
    const auto even_count = even.size();
    const auto odd_count = odd.size();
    auto *even_ptr = even_data.begin();
    auto *odd_ptr = odd_data.begin();
    auto even_event = even_queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::InterleavedStage<T, 0>>(
          even_count, [=](sycl::id<1> index) { even_ptr[index] = support::Affine<T>{}(even_ptr[index]); });
    });
    even_event.wait_and_throw();
    if (odd_count != 0) {
      auto odd_event = odd_queue.submit([&](sycl::handler &handler) {
        handler.parallel_for<onedpl_async_detail::InterleavedStage<T, 1>>(
            odd_count, [=](sycl::id<1> index) { odd_ptr[index] = support::Square<T>{}(odd_ptr[index]); });
      });
      odd_event.wait_and_throw();
    }
    const auto transformed_even = even_data.host();
    const auto transformed_odd = odd_data.host();
    std::vector<T> output(input.size());
    size_t even_index = 0;
    size_t odd_index = 0;
    for (size_t index = 0; index < output.size(); ++index)
      output[index] = index % 2 == 0 ? transformed_even[even_index++] : transformed_odd[odd_index++];
    return output;
  }

private:
  sycl::queue queue_;

  template <class T, size_t Id> auto policy() {
    return oneapi::dpl::execution::make_device_policy<onedpl_async_detail::Kernel<T, Id>>(queue_);
  }
};
} // namespace unified::backend

namespace unified::backend {
using Algorithms = OneDPLAlgorithms;
using EdgeCases = OneDPLEdgeCases;
using Async = OneDPLAsync;
} // namespace unified::backend
