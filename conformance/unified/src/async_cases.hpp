#pragma once
#include <algorithm>
#include <cstddef>
#include <numeric>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "test_support.hpp"

namespace unified::async {
// These operations intentionally describe observable dependency semantics, not a
// particular runtime object.  A vendor backend may use a CUDA/HIP stream,
// SYCL queue/event, or a host future as the implementation vehicle.
template <class T> std::vector<T> reference_scan(const std::vector<T> &input) {
  std::vector<T> result(input.size());
  std::inclusive_scan(input.begin(), input.end(), result.begin(), support::Plus<T>{});
  return result;
}

template <class T> std::vector<T> reference_future_chain(const std::vector<T> &input) {
  std::vector<T> result(input.size());
  std::transform(input.begin(), input.end(), result.begin(), [](T value) { return support::Square<T>{}(support::Affine<T>{}(value)); });
  return result;
}

template <class T> std::vector<T> reference_event_chain(const std::vector<T> &input) {
  std::vector<T> result(input.size());
  std::transform(input.begin(), input.end(), result.begin(),
                 [](T value) { return support::Combine<T>{}(support::Affine<T>{}(value), support::Square<T>{}(value)); });
  return result;
}

template <class T> std::vector<T> reference_interleaved(const std::vector<T> &input) {
  std::vector<T> result(input.size());
  for (size_t index = 0; index < input.size(); ++index)
    result[index] = index % 2 == 0 ? support::Affine<T>{}(input[index]) : support::Square<T>{}(input[index]);
  return result;
}

template <class T> std::vector<T> reference_sort(std::vector<T> input) {
  std::sort(input.begin(), input.end());
  return input;
}

template <class T> T reference_reduce(const std::vector<T> &input) {
  return std::accumulate(input.begin(), input.end(), T{}, support::Plus<T>{});
}

template <class T, class Backend> void copy(Backend &backend, const std::vector<T> &input) {
  if (!backend.template supports_copy<T>()) SKIP("copy is unsupported for this type/device");

  INFO("async-copy");
  REQUIRE(((backend.template async_copy<T>(input)) == (input)));
}

template <class T, class Backend> void reduce(Backend &backend, const std::vector<T> &input) {
  if (!backend.template supports_reduce<T>()) SKIP("reduce is unsupported for this type/device");

  INFO("async-reduce");
  REQUIRE(((backend.template async_reduce<T>(input)) == (reference_reduce(input))));
}

template <class T, class Backend> void scan(Backend &backend, const std::vector<T> &input) {
  if (!backend.template supports_scan<T>()) SKIP("scan is unsupported for this type/device");

  INFO("async-scan");
  REQUIRE(((backend.template async_scan<T>(input)) == (reference_scan(input))));
}

template <class T, class Backend> void sort(Backend &backend, const std::vector<T> &input) {
  if (!backend.template supports_sort<T>()) SKIP("sort is unsupported for this type/device");

  INFO("async-sort");
  REQUIRE(((backend.template async_sort<T>(input)) == (reference_sort(input))));
}

template <class T, class Backend> void future_chain(Backend &backend, const std::vector<T> &input) {
  if (!backend.template supports_future_chain<T>()) SKIP("future_chain is unsupported for this type/device");

  INFO("async-future-chain");
  REQUIRE(((backend.template async_future_chain<T>(input)) == (reference_future_chain(input))));
}

template <class T, class Backend> void event_chain(Backend &backend, const std::vector<T> &input) {
  if (!backend.template supports_event_chain<T>()) SKIP("event_chain is unsupported for this type/device");

  INFO("async-event-chain");
  REQUIRE(((backend.template async_event_chain<T>(input)) == (reference_event_chain(input))));
}

template <class T, class Backend> void interleaved_streams(Backend &backend, const std::vector<T> &input) {
  if (!backend.template supports_interleaved_streams<T>()) SKIP("interleaved_streams is unsupported for this type/device");

  INFO("async-interleaved-streams");
  REQUIRE(((backend.template async_interleaved_streams<T>(input)) == (reference_interleaved(input))));
}
} // namespace unified::async

#if defined(__CUDACC__) || defined(__HIPCC__)
  #define UNIFIED_HIP_ASYNC_HOST_DEVICE __host__ __device__
#else
  #define UNIFIED_HIP_ASYNC_HOST_DEVICE
#endif

namespace unified::hip_async {
// These are intentionally kept out of the common async cases. The direct
// thrust::async scan API is a HIP/rocThrust coverage target here; CUDA and SYCL
// retain their existing, portable async lane.
inline constexpr size_t maximum_size = 33;

template <class T> struct StatefulOperator {
  T offset;

  UNIFIED_HIP_ASYNC_HOST_DEVICE T operator()(T left, T right) const { return static_cast<T>(left + right + offset); }
};

template <class T> std::vector<T> counting_values(size_t size) {
  std::vector<T> result(size);
  for (size_t index = 0; index < size; ++index)
    result[index] = static_cast<T>(index);
  return result;
}

template <class T> std::vector<T> counting_reference(size_t size, bool exclusive) {
  const auto input = counting_values<T>(size);
  std::vector<T> result(size);
  if (exclusive) std::exclusive_scan(input.begin(), input.end(), result.begin(), T{}, support::Plus<T>{});
  else std::inclusive_scan(input.begin(), input.end(), result.begin(), support::Plus<T>{});
  return result;
}

template <class T> std::vector<T> stateful_reference(size_t size, bool exclusive) {
  const auto input = support::async_input_values<T>(size);
  std::vector<T> result(size);
  const StatefulOperator<T> operation{T{2}};
  if (exclusive) std::exclusive_scan(input.begin(), input.end(), result.begin(), T{42}, operation);
  else std::inclusive_scan(input.begin(), input.end(), result.begin(), operation);
  return result;
}

template <class T, class Backend> void inclusive_counting(Backend &backend, size_t size) {
  if (!backend.template supports_inclusive_counting<T>()) SKIP("inclusive_counting is unsupported for this HIP async type/device");

  INFO("hip-async-inclusive-scan-counting-iterator");
  REQUIRE(((backend.template async_inclusive_scan_counting<T>(size)) == (counting_reference<T>(size, false))));
}

template <class T, class Backend> void exclusive_counting(Backend &backend, size_t size) {
  if (!backend.template supports_exclusive_counting<T>()) SKIP("exclusive_counting is unsupported for this HIP async type/device");

  INFO("hip-async-exclusive-scan-counting-iterator");
  REQUIRE(((backend.template async_exclusive_scan_counting<T>(size)) == (counting_reference<T>(size, true))));
}

template <class T, class Backend> void inclusive_discard(Backend &backend, size_t size) {
  if (!backend.template supports_inclusive_discard<T>()) SKIP("inclusive_discard is unsupported for this HIP async type/device");

  backend.template async_inclusive_scan_discard<T>(support::async_input_values<T>(size));
}

template <class T, class Backend> void exclusive_discard(Backend &backend, size_t size) {
  if (!backend.template supports_exclusive_discard<T>()) SKIP("exclusive_discard is unsupported for this HIP async type/device");

  backend.template async_exclusive_scan_discard<T>(support::async_input_values<T>(size));
}

template <class T, class Backend> void inclusive_stateful(Backend &backend, size_t size) {
  if (!backend.template supports_inclusive_stateful<T>()) SKIP("inclusive_stateful is unsupported for this HIP async type/device");

  INFO("hip-async-inclusive-scan-stateful-operator");
  REQUIRE(((backend.template async_inclusive_scan_stateful<T>(size)) == (stateful_reference<T>(size, false))));
}

template <class T, class Backend> void exclusive_stateful(Backend &backend, size_t size) {
  if (!backend.template supports_exclusive_stateful<T>()) SKIP("exclusive_stateful is unsupported for this HIP async type/device");

  INFO("hip-async-exclusive-scan-stateful-operator");
  REQUIRE(((backend.template async_exclusive_scan_stateful<T>(size)) == (stateful_reference<T>(size, true))));
}
} // namespace unified::hip_async
#undef UNIFIED_HIP_ASYNC_HOST_DEVICE
