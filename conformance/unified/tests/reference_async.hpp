#pragma once

#include <cstddef>
#include <future>
#include <numeric>
#include <utility>
#include <vector>

#include <aspartame/vector.hpp>
#include <aspartame/view.hpp>

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
    using namespace aspartame;
    return std::async(std::launch::async, [input] { return input ^ fold_left(T{}, support::Plus<T>{}); }).get();
  }

  template <class T> std::vector<T> asyncScan(const std::vector<T> &input) {
    using namespace aspartame;
    return std::async(std::launch::async, [input] { return input | scan_left(T{}, support::Plus<T>{}) | tail() | to_vector(); }).get();
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
    using namespace aspartame;
    return std::async(std::launch::async, [input] { return input ^ sort(); }).get();
  }

  template <class T> std::vector<T> asyncFutureChain(const std::vector<T> &input) {
    using namespace aspartame;
    auto first = std::async(std::launch::async, [input] { return input ^ map(support::Affine<T>{}); });
    auto second = std::async(std::launch::async, [first = std::move(first)]() mutable { return first.get() ^ map(support::Square<T>{}); });
    return second.get();
  }

  template <class T> std::vector<T> asyncEventChain(const std::vector<T> &input) {
    using namespace aspartame;
    auto left = std::async(std::launch::async, [input] { return input ^ map(support::Affine<T>{}); });
    auto right = std::async(std::launch::async, [input] { return input ^ map(support::Square<T>{}); });
    auto join = std::async(std::launch::async, [left = std::move(left), right = std::move(right)]() mutable {
      return left.get() | zip(right.get()) | map(support::Combine<T>{}) | to_vector();
    });
    return join.get();
  }

  template <class T> std::vector<T> asyncInterleavedStreams(const std::vector<T> &input) {
    using namespace aspartame;
    auto evenFuture = std::async(std::launch::async, [even = input ^ stride(2)] { return even ^ map(support::Affine<T>{}); });
    auto oddFuture =
        std::async(std::launch::async, [odd = input | drop(1) | stride(2) | to_vector()] { return odd ^ map(support::Square<T>{}); });
    const auto transformedEven = evenFuture.get();
    const auto transformedOdd = oddFuture.get();
    return iota(size_t{0}, input.size())
           | map([&](size_t index) { return index % 2 == 0 ? transformedEven[index / 2] : transformedOdd[index / 2]; }) | to_vector();
  }
};
} // namespace unified::test
