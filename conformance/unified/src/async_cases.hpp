#pragma once
#include <cstddef>
#include <numeric>
#include <vector>

#include <aspartame/vector.hpp>
#include <aspartame/view.hpp>
#include <catch2/catch_test_macros.hpp>

#include "test_support.hpp"

namespace unified::async {
// These operations intentionally describe observable dependency semantics, not a
// particular runtime object.  A vendor backend may use a CUDA/HIP stream,
// SYCL queue/event, or a host future as the implementation vehicle.
template <class T> std::vector<T> referenceScan(const std::vector<T> &input) {
  using namespace aspartame;
  return input | scan_left(T{}, support::Plus<T>{}) | tail() | to_vector();
}

template <class T> std::vector<T> referenceFutureChain(const std::vector<T> &input) {
  using namespace aspartame;
  return input ^ map([](T value) { return support::Square<T>{}(support::Affine<T>{}(value)); });
}

template <class T> std::vector<T> referenceEventChain(const std::vector<T> &input) {
  using namespace aspartame;
  return input ^ map([](T value) { return support::Combine<T>{}(support::Affine<T>{}(value), support::Square<T>{}(value)); });
}

template <class T> std::vector<T> referenceInterleaved(const std::vector<T> &input) {
  using namespace aspartame;
  return input | zip_with_index()
         | map([](T value, size_t index) { return index % 2 == 0 ? support::Affine<T>{}(value) : support::Square<T>{}(value); })
         | to_vector();
}

template <class T> std::vector<T> referenceSort(std::vector<T> input) {
  input ^= aspartame::sort();
  return input;
}

template <class T> T referenceReduce(const std::vector<T> &input) {
  using namespace aspartame;
  return input ^ fold_left(T{}, support::Plus<T>{});
}

template <class T, class Backend> void copy(Backend &backend, size_t size) {
  const auto input = support::asyncInputValues<T>(size);
  REQUIRE(backend.template asyncCopy<T>(input) == input);
}

template <class T, class Backend> void reduce(Backend &backend, size_t size) {
  const auto input = support::asyncInputValues<T>(size);
  REQUIRE(backend.template asyncReduce<T>(input) == referenceReduce(input));
}

template <class T, class Backend> void scan(Backend &backend, size_t size) {
  const auto input = support::asyncInputValues<T>(size);
  REQUIRE(backend.template asyncScan<T>(input) == referenceScan(input));
}

template <class T, class Backend> void sort(Backend &backend, size_t size) {
  const auto input = support::asyncInputValues<T>(size);
  REQUIRE(backend.template asyncSort<T>(input) == referenceSort(input));
}

template <class T, class Backend> void futureChain(Backend &backend, size_t size) {
  const auto input = support::asyncInputValues<T>(size);
  REQUIRE(backend.template asyncFutureChain<T>(input) == referenceFutureChain(input));
}

template <class T, class Backend> void eventChain(Backend &backend, size_t size) {
  const auto input = support::asyncInputValues<T>(size);
  REQUIRE(backend.template asyncEventChain<T>(input) == referenceEventChain(input));
}

template <class T, class Backend> void interleavedStreams(Backend &backend, size_t size) {
  const auto input = support::asyncInputValues<T>(size);
  REQUIRE(backend.template asyncInterleavedStreams<T>(input) == referenceInterleaved(input));
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
inline constexpr size_t MaximumSize = 33;

template <class T> struct StatefulOperator {
  T offset;

  UNIFIED_HIP_ASYNC_HOST_DEVICE T operator()(T left, T right) const { return static_cast<T>(left + right + offset); }
};

template <class T> std::vector<T> countingValues(size_t size) {
  using namespace aspartame;
  return iota(size_t{0}, size) | map([](size_t index) { return static_cast<T>(index); }) | to_vector();
}

template <class T> std::vector<T> countingReference(size_t size, bool exclusive) {
  const auto input = countingValues<T>(size);
  std::vector<T> result(size);
  if (exclusive) std::exclusive_scan(input.begin(), input.end(), result.begin(), T{}, support::Plus<T>{});
  else std::inclusive_scan(input.begin(), input.end(), result.begin(), support::Plus<T>{});
  return result;
}

template <class T> std::vector<T> statefulReference(size_t size, bool exclusive) {
  const auto input = support::asyncInputValues<T>(size);
  std::vector<T> result(size);
  const StatefulOperator<T> operation{T{2}};
  if (exclusive) std::exclusive_scan(input.begin(), input.end(), result.begin(), T{42}, operation);
  else std::inclusive_scan(input.begin(), input.end(), result.begin(), operation);
  return result;
}

template <class T, class Backend> void inclusiveCounting(Backend &backend, size_t size) {
  REQUIRE(backend.template asyncInclusiveScanCounting<T>(size) == countingReference<T>(size, false));
}

template <class T, class Backend> void exclusiveCounting(Backend &backend, size_t size) {
  REQUIRE(backend.template asyncExclusiveScanCounting<T>(size) == countingReference<T>(size, true));
}

template <class T, class Backend> void inclusiveDiscard(Backend &backend, size_t size) {
  backend.template asyncInclusiveScanDiscard<T>(support::asyncInputValues<T>(size));
}

template <class T, class Backend> void exclusiveDiscard(Backend &backend, size_t size) {
  backend.template asyncExclusiveScanDiscard<T>(support::asyncInputValues<T>(size));
}

template <class T, class Backend> void inclusiveStateful(Backend &backend, size_t size) {
  REQUIRE(backend.template asyncInclusiveScanStateful<T>(size) == statefulReference<T>(size, false));
}

template <class T, class Backend> void exclusiveStateful(Backend &backend, size_t size) {
  REQUIRE(backend.template asyncExclusiveScanStateful<T>(size) == statefulReference<T>(size, true));
}
} // namespace unified::hip_async
#undef UNIFIED_HIP_ASYNC_HOST_DEVICE
