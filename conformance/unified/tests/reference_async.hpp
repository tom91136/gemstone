#pragma once

#include <algorithm>
#include <cstddef>
#include <future>
#include <numeric>
#include <utility>
#include <vector>

#include "template_async.hpp"

namespace unified::test {
// The unit-test adapter is deliberately asynchronous so the semantic cases
// exercise dependency handling without requiring a vendor SDK or device.
class ReferenceAsync {
public:
  template <class T> bool supports_copy() const { return true; }

  template <class T> bool supports_reduce() const { return true; }

  template <class T> bool supports_scan() const { return true; }

  template <class T> bool supports_sort() const { return true; }

  template <class T> bool supports_future_chain() const { return true; }

  template <class T> bool supports_event_chain() const { return true; }

  template <class T> bool supports_interleaved_streams() const { return true; }
  template <class T> bool supports_inclusive_counting() const { return true; }

  template <class T> bool supports_exclusive_counting() const { return true; }

  template <class T> bool supports_inclusive_discard() const { return true; }

  template <class T> bool supports_exclusive_discard() const { return true; }

  template <class T> bool supports_inclusive_stateful() const { return true; }

  template <class T> bool supports_exclusive_stateful() const { return true; }

  template <class T> std::vector<T> async_copy(const std::vector<T> &input) {
    return std::async(std::launch::async, [input] { return input; }).get();
  }

  template <class T> T async_reduce(const std::vector<T> &input) {
    return std::async(std::launch::async, [input] { return std::accumulate(input.begin(), input.end(), T{}, support::Plus<T>{}); }).get();
  }

  template <class T> std::vector<T> async_scan(const std::vector<T> &input) {
    return std::async(std::launch::async,
                      [input] {
                        std::vector<T> output(input.size());
                        std::inclusive_scan(input.begin(), input.end(), output.begin(), support::Plus<T>{});
                        return output;
                      })
        .get();
  }

  template <class T> std::vector<T> async_inclusive_scan_counting(size_t size) {
    return std::async(std::launch::async, [size] { return hip_async::counting_reference<T>(size, false); }).get();
  }

  template <class T> std::vector<T> async_exclusive_scan_counting(size_t size) {
    return std::async(std::launch::async, [size] { return hip_async::counting_reference<T>(size, true); }).get();
  }

  template <class T> void async_inclusive_scan_discard(const std::vector<T> &input) {
    std::async(std::launch::async, [input] {
      std::vector<T> output(input.size());
      std::inclusive_scan(input.begin(), input.end(), output.begin(), support::Plus<T>{});
    }).get();
  }

  template <class T> void async_exclusive_scan_discard(const std::vector<T> &input) {
    std::async(std::launch::async, [input] {
      std::vector<T> output(input.size());
      std::exclusive_scan(input.begin(), input.end(), output.begin(), T{}, support::Plus<T>{});
    }).get();
  }

  template <class T> std::vector<T> async_inclusive_scan_stateful(size_t size) {
    return std::async(std::launch::async, [size] { return hip_async::stateful_reference<T>(size, false); }).get();
  }

  template <class T> std::vector<T> async_exclusive_scan_stateful(size_t size) {
    return std::async(std::launch::async, [size] { return hip_async::stateful_reference<T>(size, true); }).get();
  }

  template <class T> std::vector<T> async_sort(const std::vector<T> &input) {
    return std::async(std::launch::async,
                      [input] {
                        std::vector<T> output = input;
                        std::sort(output.begin(), output.end());
                        return output;
                      })
        .get();
  }

  template <class T> std::vector<T> async_future_chain(const std::vector<T> &input) {
    auto first = std::async(std::launch::async, [input] {
      std::vector<T> output(input.size());
      std::transform(input.begin(), input.end(), output.begin(), support::Affine<T>{});
      return output;
    });
    auto second = std::async(std::launch::async, [first = std::move(first)]() mutable {
      const auto source = first.get();
      std::vector<T> output(source.size());
      std::transform(source.begin(), source.end(), output.begin(), support::Square<T>{});
      return output;
    });
    return second.get();
  }

  template <class T> std::vector<T> async_event_chain(const std::vector<T> &input) {
    auto left = std::async(std::launch::async, [input] {
      std::vector<T> output(input.size());
      std::transform(input.begin(), input.end(), output.begin(), support::Affine<T>{});
      return output;
    });
    auto right = std::async(std::launch::async, [input] {
      std::vector<T> output(input.size());
      std::transform(input.begin(), input.end(), output.begin(), support::Square<T>{});
      return output;
    });
    auto join = std::async(std::launch::async, [left = std::move(left), right = std::move(right)]() mutable {
      const auto left_values = left.get();
      const auto right_values = right.get();
      std::vector<T> output(left_values.size());
      std::transform(left_values.begin(), left_values.end(), right_values.begin(), output.begin(), support::Combine<T>{});
      return output;
    });
    return join.get();
  }

  template <class T> std::vector<T> async_interleaved_streams(const std::vector<T> &input) {
    std::vector<T> even;
    std::vector<T> odd;
    even.reserve((input.size() + 1) / 2);
    odd.reserve(input.size() / 2);
    for (size_t index = 0; index < input.size(); ++index)
      (index % 2 == 0 ? even : odd).push_back(input[index]);

    auto even_future = std::async(std::launch::async, [even = std::move(even)]() mutable {
      std::transform(even.begin(), even.end(), even.begin(), support::Affine<T>{});
      return even;
    });
    auto odd_future = std::async(std::launch::async, [odd = std::move(odd)]() mutable {
      std::transform(odd.begin(), odd.end(), odd.begin(), support::Square<T>{});
      return odd;
    });
    const auto transformed_even = even_future.get();
    const auto transformed_odd = odd_future.get();
    std::vector<T> output(input.size());
    size_t even_index = 0;
    size_t odd_index = 0;
    for (size_t index = 0; index < output.size(); ++index)
      output[index] = index % 2 == 0 ? transformed_even[even_index++] : transformed_odd[odd_index++];
    return output;
  }
};
} // namespace unified::test
