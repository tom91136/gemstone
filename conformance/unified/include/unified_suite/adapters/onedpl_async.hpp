#ifndef UNIFIED_SUITE_ADAPTERS_ONEDPL_ASYNC_HPP
#define UNIFIED_SUITE_ADAPTERS_ONEDPL_ASYNC_HPP

#include "unified_suite/adapters/onedpl_algorithms.hpp"
#include "unified_suite/adapters/sycl_support.hpp"
#include "unified_suite/async_cases.hpp"

#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>
#include <oneapi/dpl/numeric>
#include <sycl/sycl.hpp>

#include <algorithm>
#include <cstddef>
#include <numeric>
#include <type_traits>
#include <vector>

namespace unified_suite::adapters {

namespace onedpl_async_detail {
template <class T, int Id> class Kernel;
template <class T, int Id> class FutureStage;
template <class T, int Id> class EventStage;
template <class T, int Id> class InterleavedStage;
} // namespace onedpl_async_detail

class OneDPLAsync {
public:
  OneDPLAsync() : queue_(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool supports(async_cases::Operation) const {
    return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device());
  }

  template <class T> std::vector<T> async_copy(const std::vector<T> &input) {
    if (input.empty())
      return {};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    auto event = queue_.memcpy(output.begin(), source.begin(), input.size() * sizeof(T));
    event.wait_and_throw();
    return output.host();
  }

  template <class T> T async_reduce(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    const auto result =
        oneapi::dpl::reduce(policy<T, 0>(), source.begin(), source.end(), T{}, algorithms::Plus<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> async_scan(const std::vector<T> &input) {
    if (input.empty())
      return {};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    oneapi::dpl::inclusive_scan(policy<T, 1>(), source.begin(), source.end(), output.begin(),
                                algorithms::Plus<T>{});
    queue_.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> async_sort(const std::vector<T> &input) {
    if (input.empty())
      return {};
    onedpl_detail::Shared<T> source(queue_, input);
    oneapi::dpl::sort(policy<T, 2>(), source.begin(), source.end());
    queue_.wait_and_throw();
    return source.host();
  }

  template <class T> std::vector<T> async_future_chain(const std::vector<T> &input) {
    if (input.empty())
      return {};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> first(queue_, input.size());
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto count = input.size();
    const auto *source_data = source.begin();
    auto *first_data = first.begin();
    auto *output_data = output.begin();
    const auto first_event = queue_.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::FutureStage<T, 0>>(
          count, [=](sycl::id<1> index) { first_data[index] = algorithms::Affine<T>{}(source_data[index]); });
    });
    auto second_event = queue_.submit([&](sycl::handler &handler) {
      handler.depends_on(first_event);
      handler.parallel_for<onedpl_async_detail::FutureStage<T, 1>>(
          count, [=](sycl::id<1> index) { output_data[index] = algorithms::Square<T>{}(first_data[index]); });
    });
    second_event.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> async_event_chain(const std::vector<T> &input) {
    if (input.empty())
      return {};
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
          count, [=](sycl::id<1> index) { left_data[index] = algorithms::Affine<T>{}(source_data[index]); });
    });
    const auto right_event = right_queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::EventStage<T, 1>>(
          count, [=](sycl::id<1> index) { right_data[index] = algorithms::Square<T>{}(source_data[index]); });
    });
    auto join_event = join_queue.submit([&](sycl::handler &handler) {
      handler.depends_on({left_event, right_event});
      handler.parallel_for<onedpl_async_detail::EventStage<T, 2>>(count, [=](sycl::id<1> index) {
        output_data[index] = algorithms::Combine<T>{}(left_data[index], right_data[index]);
      });
    });
    join_event.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> async_interleaved_streams(const std::vector<T> &input) {
    std::vector<T> even;
    std::vector<T> odd;
    even.reserve((input.size() + 1) / 2);
    odd.reserve(input.size() / 2);
    for (std::size_t index = 0; index < input.size(); ++index)
      (index % 2 == 0 ? even : odd).push_back(input[index]);
    if (input.empty())
      return {};

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
          even_count, [=](sycl::id<1> index) { even_ptr[index] = algorithms::Affine<T>{}(even_ptr[index]); });
    });
    even_event.wait_and_throw();
    if (odd_count != 0) {
      auto odd_event = odd_queue.submit([&](sycl::handler &handler) {
        handler.parallel_for<onedpl_async_detail::InterleavedStage<T, 1>>(
            odd_count, [=](sycl::id<1> index) { odd_ptr[index] = algorithms::Square<T>{}(odd_ptr[index]); });
      });
      odd_event.wait_and_throw();
    }
    const auto transformed_even = even_data.host();
    const auto transformed_odd = odd_data.host();
    std::vector<T> output(input.size());
    std::size_t even_index = 0;
    std::size_t odd_index = 0;
    for (std::size_t index = 0; index < output.size(); ++index)
      output[index] = index % 2 == 0 ? transformed_even[even_index++] : transformed_odd[odd_index++];
    return output;
  }

private:
  sycl::queue queue_;

  template <class T, int Id> auto policy() {
    return oneapi::dpl::execution::make_device_policy<onedpl_async_detail::Kernel<T, Id>>(queue_);
  }
};

} // namespace unified_suite::adapters

#endif
