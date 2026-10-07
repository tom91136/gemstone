#pragma once

#include <algorithm>
#include <cstddef>
#include <future>
#include <numeric>
#include <utility>
#include <vector>

#include "async_cases.hpp"

namespace unified::test {
// The unit-test adapter is deliberately asynchronous so the semantic cases
// exercise dependency handling without requiring a vendor SDK or device.
class ReferenceAsync {
public:
  template <class T> std::vector<T> asyncCopy(const std::vector<T> &input) {
    return std::async(std::launch::async, [input] { return input; }).get();
  }

  template <class T> T asyncReduce(const std::vector<T> &input) {
    return std::async(std::launch::async, [input] { return std::accumulate(input.begin(), input.end(), T{}, support::Plus<T>{}); }).get();
  }

  template <class T> std::vector<T> asyncScan(const std::vector<T> &input) {
    return std::async(std::launch::async,
                      [input] {
                        std::vector<T> output(input.size());
                        std::inclusive_scan(input.begin(), input.end(), output.begin(), support::Plus<T>{});
                        return output;
                      })
        .get();
  }

  template <class T> std::vector<T> asyncInclusiveScanCounting(size_t size) {
    return std::async(std::launch::async, [size] { return hip_async::countingReference<T>(size, false); }).get();
  }

  template <class T> std::vector<T> asyncExclusiveScanCounting(size_t size) {
    return std::async(std::launch::async, [size] { return hip_async::countingReference<T>(size, true); }).get();
  }

  template <class T> void asyncInclusiveScanDiscard(const std::vector<T> &input) {
    std::async(std::launch::async, [input] {
      std::vector<T> output(input.size());
      std::inclusive_scan(input.begin(), input.end(), output.begin(), support::Plus<T>{});
    }).get();
  }

  template <class T> void asyncExclusiveScanDiscard(const std::vector<T> &input) {
    std::async(std::launch::async, [input] {
      std::vector<T> output(input.size());
      std::exclusive_scan(input.begin(), input.end(), output.begin(), T{}, support::Plus<T>{});
    }).get();
  }

  template <class T> std::vector<T> asyncInclusiveScanStateful(size_t size) {
    return std::async(std::launch::async, [size] { return hip_async::statefulReference<T>(size, false); }).get();
  }

  template <class T> std::vector<T> asyncExclusiveScanStateful(size_t size) {
    return std::async(std::launch::async, [size] { return hip_async::statefulReference<T>(size, true); }).get();
  }

  template <class T> std::vector<T> asyncSort(const std::vector<T> &input) {
    return std::async(std::launch::async,
                      [input] {
                        std::vector<T> output = input;
                        std::sort(output.begin(), output.end());
                        return output;
                      })
        .get();
  }

  template <class T> std::vector<T> asyncFutureChain(const std::vector<T> &input) {
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

  template <class T> std::vector<T> asyncEventChain(const std::vector<T> &input) {
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
      const auto leftValues = left.get();
      const auto rightValues = right.get();
      std::vector<T> output(leftValues.size());
      std::transform(leftValues.begin(), leftValues.end(), rightValues.begin(), output.begin(), support::Combine<T>{});
      return output;
    });
    return join.get();
  }

  template <class T> std::vector<T> asyncInterleavedStreams(const std::vector<T> &input) {
    std::vector<T> even;
    std::vector<T> odd;
    even.reserve((input.size() + 1) / 2);
    odd.reserve(input.size() / 2);
    for (size_t index = 0; index < input.size(); ++index)
      (index % 2 == 0 ? even : odd).push_back(input[index]);

    auto evenFuture = std::async(std::launch::async, [even = std::move(even)]() mutable {
      std::transform(even.begin(), even.end(), even.begin(), support::Affine<T>{});
      return even;
    });
    auto oddFuture = std::async(std::launch::async, [odd = std::move(odd)]() mutable {
      std::transform(odd.begin(), odd.end(), odd.begin(), support::Square<T>{});
      return odd;
    });
    const auto transformedEven = evenFuture.get();
    const auto transformedOdd = oddFuture.get();
    std::vector<T> output(input.size());
    size_t evenIndex = 0;
    size_t oddIndex = 0;
    for (size_t index = 0; index < output.size(); ++index)
      output[index] = index % 2 == 0 ? transformedEven[evenIndex++] : transformedOdd[oddIndex++];
    return output;
  }
};
} // namespace unified::test
