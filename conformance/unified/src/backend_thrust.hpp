#pragma once

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <iterator>
#include <stdexcept>
#include <string>
#include <string_view>
#include <type_traits>
#include <vector>

#include <aspartame/vector.hpp>
#include <aspartame/view.hpp>
#include <thrust/adjacent_difference.h>
#include <thrust/binary_search.h>
#include <thrust/copy.h>
#include <thrust/count.h>
#include <thrust/device_malloc.h>
#include <thrust/device_vector.h>
#include <thrust/equal.h>
#include <thrust/execution_policy.h>
#include <thrust/extrema.h>
#include <thrust/fill.h>
#include <thrust/find.h>
#include <thrust/for_each.h>
#include <thrust/functional.h>
#include <thrust/gather.h>
#include <thrust/generate.h>
#include <thrust/inner_product.h>
#include <thrust/iterator/counting_iterator.h>
#include <thrust/iterator/discard_iterator.h>
#include <thrust/iterator/permutation_iterator.h>
#include <thrust/iterator/transform_iterator.h>
#include <thrust/iterator/zip_iterator.h>
#include <thrust/logical.h>
#include <thrust/merge.h>
#include <thrust/mismatch.h>
#include <thrust/pair.h>
#include <thrust/partition.h>
#include <thrust/random.h>
#include <thrust/reduce.h>
#include <thrust/remove.h>
#include <thrust/replace.h>
#include <thrust/reverse.h>
#include <thrust/scan.h>
#include <thrust/scatter.h>
#include <thrust/set_operations.h>
#include <thrust/shuffle.h>
#include <thrust/sort.h>
#include <thrust/swap.h>
#include <thrust/transform.h>
#include <thrust/transform_reduce.h>
#include <thrust/transform_scan.h>
#include <thrust/tuple.h>
#include <thrust/uninitialized_copy.h>
#include <thrust/uninitialized_fill.h>
#include <thrust/unique.h>
#include <thrust/version.h>

#include "algorithm_cases.hpp"
#include "async_cases.hpp"
#include "edge_cases.hpp"
#include <fmt/format.h>

#if !defined(__HIPCC__) && THRUST_VERSION >= 300000
  #include <cuda/std/functional>
#endif

#ifdef __HIPCC__
  #include <hip/hip_runtime.h>
  #include <thrust/system/hip/execution_policy.h>
#else
  #include <cuda_runtime_api.h>
  #include <thrust/system/cuda/execution_policy.h>
#endif

#if __has_include(<thrust/async/reduce.h>) && __has_include(<thrust/async/scan.h>)
  #define UNIFIED_HAS_THRUST_ASYNC 1
  #include <thrust/async/reduce.h>
  #include <thrust/async/scan.h>
#else
  #define UNIFIED_HAS_THRUST_ASYNC 0
#endif

// CCCL 2.8 marks the whole thrust::async namespace deprecated while the conformance
// cases still exercise it
#if defined(__clang__) || defined(__GNUC__)
  #define UNIFIED_THRUST_ASYNC_BEGIN _Pragma("GCC diagnostic push") _Pragma("GCC diagnostic ignored \"-Wdeprecated-declarations\"")
  #define UNIFIED_THRUST_ASYNC_END _Pragma("GCC diagnostic pop")
#else
  #define UNIFIED_THRUST_ASYNC_BEGIN
  #define UNIFIED_THRUST_ASYNC_END
#endif

#ifdef __HIPCC__
  #include <rocprim/block/block_scan.hpp>
#else
  #include <cub/block/block_scan.cuh>
#endif

#ifdef UNIFIED_API_THRUST_DEVICE_FUNCTIONS
  #include <catch2/catch_test_macros.hpp>
  #ifndef __HIPCC__
    #include <cuda_runtime.h>
  #endif
#endif

#if defined(__HIPCC__) && defined(UNIFIED_API_ROCTHRUST)
  #include <thrust/advance.h>
  #include <thrust/async/for_each.h>
  #include <thrust/complex.h>
  #include <thrust/detail/tuple_algorithms.h>
  #include <thrust/device_ptr.h>
  #include <thrust/device_reference.h>
  #include <thrust/distance.h>
  #include <thrust/iterator/constant_iterator.h>
  #include <thrust/iterator/reverse_iterator.h>
  #include <thrust/iterator/tabulate_output_iterator.h>
  #include <thrust/iterator/transform_input_output_iterator.h>
  #include <thrust/iterator/transform_output_iterator.h>
  #include <thrust/memory.h>
  #include <thrust/sequence.h>
  #include <thrust/universal_vector.h>
  #include <thrust/zip_function.h>
#endif

namespace unified::backend::thrust_support {
#if !defined(__HIPCC__) && THRUST_VERSION >= 300000
template <class T> using Plus = cuda::std::plus<T>;
#else
template <class T> using Plus = thrust::plus<T>;
#endif
#ifdef __HIPCC__
using Stream = hipStream_t;
using Event = hipEvent_t;
inline void check(hipError_t status, const char *where) {
  if (status != hipSuccess) throw std::runtime_error(std::string(where) + ": " + hipGetErrorString(status));
}
inline auto policy(Stream stream) { return thrust::hip::par.on(stream); }
inline void synchronise(Stream stream, const char *where) { check(hipStreamSynchronize(stream), where); }
inline void record(Event event, Stream stream, const char *where) { check(hipEventRecord(event, stream), where); }
inline void waitEvent(Stream stream, Event event, const char *where) { check(hipStreamWaitEvent(stream, event, 0), where); }
#else
using Stream = cudaStream_t;
using Event = cudaEvent_t;
inline void check(cudaError_t status, const char *where) {
  if (status != cudaSuccess) throw std::runtime_error(std::string(where) + ": " + cudaGetErrorString(status));
}
inline auto policy(Stream stream) { return thrust::cuda::par.on(stream); }
inline void synchronise(Stream stream, const char *where) { check(cudaStreamSynchronize(stream), where); }
inline void record(Event event, Stream stream, const char *where) { check(cudaEventRecord(event, stream), where); }
inline void waitEvent(Stream stream, Event event, const char *where) { check(cudaStreamWaitEvent(stream, event, 0), where); }
#endif

class StreamResource {
public:
  explicit StreamResource(const char *where) {
#ifdef __HIPCC__
    check(hipStreamCreate(&handle), where);
#else
    check(cudaStreamCreate(&handle), where);
#endif
  }
  StreamResource(const StreamResource &) = delete;
  StreamResource &operator=(const StreamResource &) = delete;
  ~StreamResource() {
    if (handle) {
#ifdef __HIPCC__
      (void)hipStreamDestroy(handle);
#else
      (void)cudaStreamDestroy(handle);
#endif
    }
  }
  [[nodiscard]] Stream get() const { return handle; }
  void close(const char *where) {
#ifdef __HIPCC__
    check(hipStreamDestroy(handle), where);
#else
    check(cudaStreamDestroy(handle), where);
#endif
    handle = nullptr;
  }

private:
  Stream handle{};
};

class EventResource {
public:
  explicit EventResource(const char *where) {
#ifdef __HIPCC__
    check(hipEventCreateWithFlags(&handle, hipEventDisableTiming), where);
#else
    check(cudaEventCreateWithFlags(&handle, cudaEventDisableTiming), where);
#endif
  }
  EventResource(const EventResource &) = delete;
  EventResource &operator=(const EventResource &) = delete;
  ~EventResource() {
    if (handle) {
#ifdef __HIPCC__
      (void)hipEventDestroy(handle);
#else
      (void)cudaEventDestroy(handle);
#endif
    }
  }
  [[nodiscard]] Event get() const { return handle; }
  void close(const char *where) {
#ifdef __HIPCC__
    check(hipEventDestroy(handle), where);
#else
    check(cudaEventDestroy(handle), where);
#endif
    handle = nullptr;
  }

private:
  Event handle{};
};

template <class T> std::vector<T> host(const thrust::device_vector<T> &input) {
  std::vector<T> result(input.size());
  if (!input.empty()) thrust::copy(input.begin(), input.end(), result.data());
  return result;
}
} // namespace unified::backend::thrust_support

namespace unified::backend {
using thrust_support::host;

class ThrustAlgorithms {
public:
  template <class T> [[nodiscard]] bool supports(std::string_view, size_t) const { return true; }

  template <class T> std::vector<T> copy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::copy(source.begin(), source.end(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> copyIf(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    const auto end = thrust::copy_if(source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> copyN(const std::vector<T> &input, size_t count) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(count);
    thrust::copy_n(source.begin(), count, output.begin());
    return host(output);
  }

  template <class T> std::vector<T> uninitializedCopy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::uninitialized_copy(source.begin(), source.end(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> uninitializedFill(size_t size, T value) {
    thrust::device_vector<T> output(size);
    thrust::uninitialized_fill(output.begin(), output.end(), value);
    return host(output);
  }

  template <class T> std::vector<T> fill(size_t size, T value) {
    thrust::device_vector<T> output(size);
    thrust::fill(output.begin(), output.end(), value);
    return host(output);
  }

  template <class T> std::vector<T> generate(size_t size) {
    thrust::device_vector<T> output(size);
    thrust::generate(output.begin(), output.end(), algorithms::Constant<T>{});
    return host(output);
  }

  template <class T> std::vector<T> forEach(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::for_each(output.begin(), output.end(), algorithms::Increment<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transformUnary(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform(source.begin(), source.end(), output.begin(), algorithms::Affine<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transformBinary(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size());
    thrust::transform(a.begin(), a.end(), b.begin(), output.begin(), algorithms::Combine<T>{});
    return host(output);
  }

  template <class T> std::vector<T> replaceIf(const std::vector<T> &input, T replacement) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::replace_if(output.begin(), output.end(), algorithms::IsEven<T>{}, replacement);
    return host(output);
  }

  template <class T> std::vector<T> replaceCopyIf(const std::vector<T> &input, T replacement) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::replace_copy_if(source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{}, replacement);
    return host(output);
  }

  template <class T> std::vector<T> removeIf(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto end = thrust::remove_if(output.begin(), output.end(), algorithms::IsEven<T>{});
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> removeCopyIf(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    const auto end = thrust::remove_copy_if(source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> algorithms::TwoRanges<T> swapRanges(const std::vector<T> &left, const std::vector<T> &right) {
    thrust::device_vector<T> a(left.begin(), left.end());
    thrust::device_vector<T> b(right.begin(), right.end());
    thrust::swap_ranges(a.begin(), a.end(), b.begin());
    return {host(a), host(b)};
  }

  template <class T> T reduce(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::reduce(source.begin(), source.end(), T{}, algorithms::Plus<T>{});
  }

  template <class T> T transformReduce(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::transform_reduce(source.begin(), source.end(), algorithms::Square<T>{}, T{}, algorithms::Plus<T>{});
  }

  template <class T> T innerProduct(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    return thrust::inner_product(a.begin(), a.end(), b.begin(), T{});
  }

  template <class T> std::vector<T> inclusiveScan(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::inclusive_scan(source.begin(), source.end(), output.begin(), algorithms::Plus<T>{});
    return host(output);
  }

  template <class T> std::vector<T> exclusiveScan(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::exclusive_scan(source.begin(), source.end(), output.begin(), T{}, algorithms::Plus<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transformInclusiveScan(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform_inclusive_scan(source.begin(), source.end(), output.begin(), algorithms::Square<T>{}, algorithms::Plus<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transformExclusiveScan(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform_exclusive_scan(source.begin(), source.end(), output.begin(), algorithms::Square<T>{}, T{}, algorithms::Plus<T>{});
    return host(output);
  }

  template <class T> std::vector<T> sort(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::sort(output.begin(), output.end(), algorithms::Less<T>{});
    return host(output);
  }

  template <class T> std::vector<T> stableSort(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::stable_sort(output.begin(), output.end(), algorithms::Less<T>{});
    return host(output);
  }

  template <class T> std::vector<T> sortDescending(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::sort(output.begin(), output.end(), algorithms::Greater<T>{});
    return host(output);
  }

  template <class T> algorithms::KeyValues<T> sortByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    thrust::device_vector<T> deviceKeys(keys.begin(), keys.end());
    thrust::device_vector<int32_t> deviceValues(values.begin(), values.end());
    thrust::sort_by_key(deviceKeys.begin(), deviceKeys.end(), deviceValues.begin(), algorithms::Less<T>{});
    return {host(deviceKeys), host(deviceValues)};
  }

  template <class T> algorithms::KeyValues<T> stableSortByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    thrust::device_vector<T> deviceKeys(keys.begin(), keys.end());
    thrust::device_vector<int32_t> deviceValues(values.begin(), values.end());
    thrust::stable_sort_by_key(deviceKeys.begin(), deviceKeys.end(), deviceValues.begin(), algorithms::Less<T>{});
    return {host(deviceKeys), host(deviceValues)};
  }

  template <class T> std::vector<T> unique(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto end = thrust::unique(output.begin(), output.end());
#ifdef __HIPCC__
    thrust_support::check(hipDeviceSynchronize(), "rocThrust unique synchronise");
#endif
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> uniqueCopy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    const auto end = thrust::unique_copy(source.begin(), source.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> size_t count(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::count(source.begin(), source.end(), value));
  }

  template <class T> size_t countIf(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::count_if(source.begin(), source.end(), algorithms::IsEven<T>{}));
  }

  template <class T> size_t findIf(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::find_if(source.begin(), source.end(), algorithms::IsEven<T>{}) - source.begin());
  }

  template <class T> size_t find(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::find(source.begin(), source.end(), value) - source.begin());
  }

  template <class T> bool allOf(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::all_of(source.begin(), source.end(), algorithms::IsEven<T>{});
  }

  template <class T> bool anyOf(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::any_of(source.begin(), source.end(), algorithms::IsEven<T>{});
  }

  template <class T> bool noneOf(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::none_of(source.begin(), source.end(), algorithms::IsEven<T>{});
  }

  template <class T> bool equal(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    return thrust::equal(a.begin(), a.end(), b.begin());
  }

  template <class T> algorithms::TwoPositions mismatch(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    const auto result = thrust::mismatch(a.begin(), a.end(), b.begin());
    return {.first = static_cast<size_t>(result.first - a.begin()), .second = static_cast<size_t>(result.second - b.begin())};
  }

  template <class T> size_t lowerBound(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::lower_bound(source.begin(), source.end(), value) - source.begin());
  }

  template <class T> size_t upperBound(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::upper_bound(source.begin(), source.end(), value) - source.begin());
  }

  template <class T> bool binarySearch(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::binary_search(source.begin(), source.end(), value);
  }

  template <class T> algorithms::TwoPositions equalRange(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const auto result = thrust::equal_range(source.begin(), source.end(), value);
    return {.first = static_cast<size_t>(result.first - source.begin()), .second = static_cast<size_t>(result.second - source.begin())};
  }

  template <class T> std::vector<T> merge(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size() + right.size());
    thrust::merge(a.begin(), a.end(), b.begin(), b.end(), output.begin(), algorithms::Less<T>{});
    return host(output);
  }

  template <class T> algorithms::Partitioned<T> partition(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto boundary = thrust::partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    return {host(output), static_cast<size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::Partitioned<T> stablePartition(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto boundary = thrust::stable_partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    return {host(output), static_cast<size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::TwoRanges<T> partitionCopy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> selected(input.size());
    thrust::device_vector<T> rejected(input.size());
    const auto ends = thrust::partition_copy(source.begin(), source.end(), selected.begin(), rejected.begin(), algorithms::IsEven<T>{});
    selected.resize(static_cast<size_t>(ends.first - selected.begin()));
    rejected.resize(static_cast<size_t>(ends.second - rejected.begin()));
    return {host(selected), host(rejected)};
  }

  template <class T> bool isPartitioned(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::is_partitioned(source.begin(), source.end(), algorithms::IsEven<T>{});
  }

  template <class T> size_t partitionPoint(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::partition_point(source.begin(), source.end(), algorithms::IsEven<T>{}) - source.begin());
  }

  template <class T> std::vector<T> adjacentDifference(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::adjacent_difference(source.begin(), source.end(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> setUnion(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size() + right.size());
    const auto end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> setIntersection(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(std::min(left.size(), right.size()));
    const auto end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> setDifference(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size());
    const auto end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> setSymmetricDifference(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size() + right.size());
    const auto end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> reverse(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::reverse(output.begin(), output.end());
    return host(output);
  }

  template <class T> std::vector<T> reverseCopy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::reverse_copy(source.begin(), source.end(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> shuffle(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::default_random_engine generator(17);
    thrust::shuffle(output.begin(), output.end(), generator);
    return host(output);
  }

  template <class T> size_t minElement(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::min_element(source.begin(), source.end()) - source.begin());
  }

  template <class T> size_t maxElement(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::max_element(source.begin(), source.end()) - source.begin());
  }

  template <class T> algorithms::TwoPositions minmaxElement(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const auto result = thrust::minmax_element(source.begin(), source.end());
    return {.first = static_cast<size_t>(result.first - source.begin()), .second = static_cast<size_t>(result.second - source.begin())};
  }

  template <class T> bool isSorted(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::is_sorted(source.begin(), source.end());
  }

  template <class T> size_t isSortedUntil(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::is_sorted_until(source.begin(), source.end()) - source.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &, size_t) { SKIP("rotate is not a Thrust API"); }

  template <class T> std::vector<T> rotateCopy(const std::vector<T> &, size_t) { SKIP("rotate-copy is not a Thrust API"); }

  template <class T> bool includes(const std::vector<T> &, const std::vector<T> &) { SKIP("includes is not a Thrust API"); }

  template <class T> std::vector<T> gather(const std::vector<T> &input, const std::vector<size_t> &indices) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const thrust::device_vector<size_t> map(indices.begin(), indices.end());
    thrust::device_vector<T> output(indices.size());
    thrust::gather(map.begin(), map.end(), source.begin(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> scatter(const std::vector<T> &input, const std::vector<size_t> &indices) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const thrust::device_vector<size_t> map(indices.begin(), indices.end());
    thrust::device_vector<T> output(input.size());
    thrust::scatter(source.begin(), source.end(), map.begin(), output.begin());
    return host(output);
  }

  template <class T> algorithms::KeyValues<T> reduceByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    const thrust::device_vector<T> sourceKeys(keys.begin(), keys.end());
    const thrust::device_vector<int32_t> sourceValues(values.begin(), values.end());
    thrust::device_vector<T> outputKeys(keys.size());
    thrust::device_vector<int32_t> outputValues(values.size());
    const auto ends =
        thrust::reduce_by_key(sourceKeys.begin(), sourceKeys.end(), sourceValues.begin(), outputKeys.begin(), outputValues.begin());
    outputKeys.resize(static_cast<size_t>(ends.first - outputKeys.begin()));
    outputValues.resize(static_cast<size_t>(ends.second - outputValues.begin()));
    return {host(outputKeys), host(outputValues)};
  }

  template <class T> algorithms::KeyValues<T> inclusiveScanByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    const thrust::device_vector<T> deviceKeys(keys.begin(), keys.end());
    const thrust::device_vector<int32_t> source(values.begin(), values.end());
    thrust::device_vector<int32_t> output(values.size());
    thrust::inclusive_scan_by_key(deviceKeys.begin(), deviceKeys.end(), source.begin(), output.begin());
    return {keys, host(output)};
  }

  template <class T> algorithms::KeyValues<T> exclusiveScanByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    const thrust::device_vector<T> deviceKeys(keys.begin(), keys.end());
    const thrust::device_vector<int32_t> source(values.begin(), values.end());
    thrust::device_vector<int32_t> output(values.size());
    thrust::exclusive_scan_by_key(deviceKeys.begin(), deviceKeys.end(), source.begin(), output.begin(), 0);
    return {keys, host(output)};
  }

  template <class T> algorithms::KeyValues<T> uniqueByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    thrust::device_vector<T> outputKeys(keys.begin(), keys.end());
    thrust::device_vector<int32_t> outputValues(values.begin(), values.end());
    const auto ends = thrust::unique_by_key(outputKeys.begin(), outputKeys.end(), outputValues.begin());
    outputKeys.resize(static_cast<size_t>(ends.first - outputKeys.begin()));
    outputValues.resize(static_cast<size_t>(ends.second - outputValues.begin()));
    return {host(outputKeys), host(outputValues)};
  }

  template <class T> algorithms::KeyValues<T> mergeByKey(const algorithms::KeyValues<T> &left, const algorithms::KeyValues<T> &right) {
    const thrust::device_vector<T> leftKeys(left.keys.begin(), left.keys.end());
    const thrust::device_vector<T> rightKeys(right.keys.begin(), right.keys.end());
    const thrust::device_vector<int32_t> leftValues(left.values.begin(), left.values.end());
    const thrust::device_vector<int32_t> rightValues(right.values.begin(), right.values.end());
    thrust::device_vector<T> outputKeys(left.keys.size() + right.keys.size());
    thrust::device_vector<int32_t> outputValues(left.values.size() + right.values.size());
    thrust::merge_by_key(leftKeys.begin(), leftKeys.end(), rightKeys.begin(), rightKeys.end(), leftValues.begin(), rightValues.begin(),
                         outputKeys.begin(), outputValues.begin());
    return {host(outputKeys), host(outputValues)};
  }

  template <class T> std::vector<T> partialSort(const std::vector<T> &, size_t) { SKIP("partial-sort is not a Thrust API"); }

  template <class T> std::vector<T> nthElement(const std::vector<T> &, size_t) { SKIP("nth-element is not a Thrust API"); }

  template <class T> std::vector<T> inplaceMerge(const std::vector<T> &, size_t) { SKIP("inplace-merge is not a Thrust API"); }
};
} // namespace unified::backend

namespace unified::backend {
// Thrust's stream execution policies are the common CUDA/rocThrust async
// surface. Every method waits for the queued work (with either stream
// synchronisation or a future completion), so dependency ordering is part of
// the assertion.
class ThrustAsync {
public:
  template <class T> [[nodiscard]] bool supports(std::string_view, size_t) const { return true; }

  template <class T> std::vector<T> asyncCopy(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust_support::StreamResource stream("async copy stream create");
    thrust::copy(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin());
    thrust_support::synchronise(stream.get(), "async copy synchronise");
    return thrust_support::host(output);
  }

  template <class T> T asyncReduce(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_support::StreamResource stream("async reduce stream create");
    // Unlike the ordinary stream-policy overload, this is the vendor async
    // API: reduce returns a device future and get() is the completion point.
#if UNIFIED_HAS_THRUST_ASYNC
    UNIFIED_THRUST_ASYNC_BEGIN
    auto result = thrust::async::reduce(thrust_support::policy(stream.get()), source.begin(), source.end(), T{}, thrust_support::Plus<T>{});
    UNIFIED_THRUST_ASYNC_END
    return result.get();
#else
    return thrust::reduce(thrust_support::policy(stream.get()), source.begin(), source.end(), T{}, thrust_support::Plus<T>{});
#endif
  }

  template <class T> std::vector<T> asyncScan(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust_support::StreamResource stream("async scan stream create");
    thrust::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(), thrust_support::Plus<T>{});
    thrust_support::synchronise(stream.get(), "async scan synchronise");
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> asyncInclusiveScanCounting(size_t size) {
    thrust::device_vector<T> output(size);
    const auto first = thrust::make_counting_iterator<T>(T{0});
    const auto last = first + static_cast<ptrdiff_t>(size);
    thrust_support::StreamResource stream("HIP async inclusive counting stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    UNIFIED_THRUST_ASYNC_BEGIN
    auto completion =
        thrust::async::inclusive_scan(thrust_support::policy(stream.get()), first, last, output.begin(), thrust_support::Plus<T>{});
    UNIFIED_THRUST_ASYNC_END
    completion.wait();
#else
    thrust::inclusive_scan(thrust_support::policy(stream.get()), first, last, output.begin(), thrust_support::Plus<T>{});
    thrust_support::synchronise(stream.get(), "HIP async inclusive counting synchronise");
#endif
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> asyncExclusiveScanCounting(size_t size) {
    thrust::device_vector<T> output(size);
    const auto first = thrust::make_counting_iterator<T>(T{0});
    const auto last = first + static_cast<ptrdiff_t>(size);
    thrust_support::StreamResource stream("HIP async exclusive counting stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    UNIFIED_THRUST_ASYNC_BEGIN
    auto completion =
        thrust::async::exclusive_scan(thrust_support::policy(stream.get()), first, last, output.begin(), T{}, thrust_support::Plus<T>{});
    UNIFIED_THRUST_ASYNC_END
    completion.wait();
#else
    thrust::exclusive_scan(thrust_support::policy(stream.get()), first, last, output.begin(), T{}, thrust_support::Plus<T>{});
    thrust_support::synchronise(stream.get(), "HIP async exclusive counting synchronise");
#endif
    return thrust_support::host(output);
  }

  template <class T> void asyncInclusiveScanDiscard(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_support::StreamResource stream("HIP async inclusive discard stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    UNIFIED_THRUST_ASYNC_BEGIN
    auto completion = thrust::async::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(),
                                                    thrust::make_discard_iterator(), thrust_support::Plus<T>{});
    UNIFIED_THRUST_ASYNC_END
    completion.wait();
#else
    thrust::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), thrust::make_discard_iterator(),
                           thrust_support::Plus<T>{});
    thrust_support::synchronise(stream.get(), "HIP async inclusive discard synchronise");
#endif
  }

  template <class T> void asyncExclusiveScanDiscard(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_support::StreamResource stream("HIP async exclusive discard stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    UNIFIED_THRUST_ASYNC_BEGIN
    auto completion = thrust::async::exclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(),
                                                    thrust::make_discard_iterator(), T{}, thrust_support::Plus<T>{});
    UNIFIED_THRUST_ASYNC_END
    completion.wait();
#else
    thrust::exclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), thrust::make_discard_iterator(), T{},
                           thrust_support::Plus<T>{});
    thrust_support::synchronise(stream.get(), "HIP async exclusive discard synchronise");
#endif
  }

  template <class T> std::vector<T> asyncInclusiveScanStateful(size_t size) {
    const auto input = support::asyncInputValues<T>(size);
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(size);
    thrust_support::StreamResource stream("HIP async inclusive stateful stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    UNIFIED_THRUST_ASYNC_BEGIN
    auto completion = thrust::async::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(),
                                                    hip_async::StatefulOperator<T>{T{2}});
    UNIFIED_THRUST_ASYNC_END
    completion.wait();
#else
    thrust::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(),
                           hip_async::StatefulOperator<T>{T{2}});
    thrust_support::synchronise(stream.get(), "HIP async inclusive stateful synchronise");
#endif
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> asyncExclusiveScanStateful(size_t size) {
    const auto input = support::asyncInputValues<T>(size);
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(size);
    thrust_support::StreamResource stream("HIP async exclusive stateful stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    UNIFIED_THRUST_ASYNC_BEGIN
    auto completion = thrust::async::exclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(),
                                                    T{42}, hip_async::StatefulOperator<T>{T{2}});
    UNIFIED_THRUST_ASYNC_END
    completion.wait();
#else
    thrust::exclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(), T{42},
                           hip_async::StatefulOperator<T>{T{2}});
    thrust_support::synchronise(stream.get(), "HIP async exclusive stateful synchronise");
#endif
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> asyncSort(const std::vector<T> &input) {
    thrust::device_vector<T> values(input.begin(), input.end());
    thrust_support::StreamResource stream("async sort stream create");
    thrust::sort(thrust_support::policy(stream.get()), values.begin(), values.end());
    thrust_support::synchronise(stream.get(), "async sort synchronise");
    return thrust_support::host(values);
  }

  template <class T> std::vector<T> asyncFutureChain(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> first(input.size());
    thrust::device_vector<T> output(input.size());
    thrust_support::StreamResource stream("async future-chain stream create");
    thrust::transform(thrust_support::policy(stream.get()), source.begin(), source.end(), first.begin(), support::Affine<T>{});
    // The second operation is the continuation.  Stream ordering is the
    // device equivalent of a future continuation and does not require a host
    // round-trip between stages.
    thrust::transform(thrust_support::policy(stream.get()), first.begin(), first.end(), output.begin(), support::Square<T>{});
    thrust_support::synchronise(stream.get(), "async future-chain synchronise");
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> asyncEventChain(const std::vector<T> &input) {
    if (input.empty()) return {};
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> left(input.size());
    thrust::device_vector<T> right(input.size());
    thrust::device_vector<T> output(input.size());
    thrust_support::StreamResource leftStream("async event left stream create");
    thrust_support::StreamResource rightStream("async event right stream create");
    thrust_support::StreamResource joinStream("async event join stream create");
    thrust_support::EventResource leftReady("async event left create");
    thrust_support::EventResource rightReady("async event right create");
    thrust::transform(thrust_support::policy(leftStream.get()), source.begin(), source.end(), left.begin(), support::Affine<T>{});
    thrust::transform(thrust_support::policy(rightStream.get()), source.begin(), source.end(), right.begin(), support::Square<T>{});
    thrust_support::record(leftReady.get(), leftStream.get(), "async event left record");
    thrust_support::record(rightReady.get(), rightStream.get(), "async event right record");
    thrust_support::waitEvent(joinStream.get(), leftReady.get(), "async event left wait");
    thrust_support::waitEvent(joinStream.get(), rightReady.get(), "async event right wait");
    thrust::transform(thrust_support::policy(joinStream.get()), left.begin(), left.end(), right.begin(), output.begin(),
                      support::Combine<T>{});
    thrust_support::synchronise(joinStream.get(), "async event join synchronise");
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> asyncInterleavedStreams(const std::vector<T> &input) {
    using namespace aspartame;
    const auto even = input ^ stride(2);
    const auto odd = input | drop(1) | stride(2) | to_vector();

    thrust::device_vector<T> evenDevice(even.begin(), even.end());
    thrust::device_vector<T> oddDevice(odd.begin(), odd.end());
    thrust_support::StreamResource evenStream("async interleaved even stream create");
    thrust_support::StreamResource oddStream("async interleaved odd stream create");
    thrust::transform(thrust_support::policy(evenStream.get()), evenDevice.begin(), evenDevice.end(), evenDevice.begin(),
                      support::Affine<T>{});
    thrust::transform(thrust_support::policy(oddStream.get()), oddDevice.begin(), oddDevice.end(), oddDevice.begin(), support::Square<T>{});
    thrust_support::synchronise(evenStream.get(), "async interleaved even synchronise");
    thrust_support::synchronise(oddStream.get(), "async interleaved odd synchronise");
    const auto transformedEven = thrust_support::host(evenDevice);
    const auto transformedOdd = thrust_support::host(oddDevice);
    return iota(size_t{0}, input.size())
           | map([&](size_t index) { return index % 2 == 0 ? transformedEven[index / 2] : transformedOdd[index / 2]; }) | to_vector();
  }
};
} // namespace unified::backend

#undef UNIFIED_HAS_THRUST_ASYNC

namespace unified::backend {
namespace thrust_edge_detail {
using thrust_support::check;
using thrust_support::EventResource;
using thrust_support::host;
using thrust_support::policy;
using thrust_support::StreamResource;

struct SetExpectedFlagForEach {
  uint64_t expected;
  bool *flag;

  __device__ void operator()(uint64_t value) const {
    if (value == expected) *flag = true;
  }
};

struct DetectWrongDifference {
  using value_type = int64_t;
  using difference_type = ptrdiff_t;
  using pointer = DetectWrongDifference *;
  using reference = DetectWrongDifference;
  using iterator_category = std::output_iterator_tag;

  bool *flag;

  __host__ __device__ DetectWrongDifference operator++() const { return *this; }
  __host__ __device__ DetectWrongDifference operator*() const { return *this; }
  template <class Difference> __host__ __device__ DetectWrongDifference operator+(Difference) const { return *this; }
  template <class Index> __host__ __device__ DetectWrongDifference operator[](Index) const { return *this; }

  __device__ void operator=(int64_t difference) const {
    if (difference != 1) *flag = false;
  }
};

// Frees the single device flag even when the Thrust call throws.
class DeviceFlag {
public:
  DeviceFlag() : pointer(thrust::device_malloc<bool>(1)) {}
  ~DeviceFlag() {
#ifdef __HIPCC__
    (void)hipFree(raw());
#else
    (void)cudaFree(raw());
#endif
  }
  DeviceFlag(const DeviceFlag &) = delete;
  DeviceFlag &operator=(const DeviceFlag &) = delete;

  auto operator*() const { return *pointer; }
  [[nodiscard]] bool *raw() const { return thrust::raw_pointer_cast(pointer); }

private:
  thrust::device_ptr<bool> pointer;
};

template <class T> struct TupleWidePairValue {
  template <class Tuple> __host__ __device__ int64_t operator()(const Tuple &value) const {
    return edge::WidePairValue<T>{}(thrust::get<0>(value), thrust::get<1>(value));
  }
};

#ifdef __HIPCC__
template <class T, uint32_t BlockSize> __global__ void blockScanKernel(const T *input, T *output, size_t size) {
  using Scan = rocprim::block_scan<T, BlockSize>;
  __shared__ typename Scan::storage_type storage;
  const auto index = (static_cast<size_t>(blockIdx.x) * BlockSize) + threadIdx.x;
  T value = index < size ? input[index] : T{};
  Scan().inclusive_scan(value, value, storage);
  if (index < size) output[index] = value;
}
#else
template <class T, uint32_t BlockSize> __global__ void blockScanKernel(const T *input, T *output, size_t size) {
  using Scan = cub::BlockScan<T, BlockSize>;
  __shared__ typename Scan::TempStorage storage;
  const auto index = (static_cast<size_t>(blockIdx.x) * BlockSize) + threadIdx.x;
  T value = index < size ? input[index] : T{};
  Scan(storage).InclusiveSum(value, value);
  if (index < size) output[index] = value;
}
#endif
} // namespace thrust_edge_detail

class ThrustEdgeCases {
public:
  template <class T> [[nodiscard]] bool supports(std::string_view, size_t) const { return true; }

  // These two probes are the semantic form of Thrust's
  // Test*WithBigIndexes fixtures.  They intentionally use counting
  // iterators, so the test does not allocate a 2^33-element buffer; the
  // vendor implementation must still walk the complete 64-bit range and
  // update one device-side flag.
  void forEachBigIndexes(uint32_t magnitude) {
    if (magnitude >= 63) throw std::invalid_argument("big-index magnitude must be below 63");
    const auto begin = thrust::make_counting_iterator<uint64_t>(0);
    const auto end = begin + (1LL << magnitude);
    if (end - begin != (1LL << magnitude)) throw std::runtime_error("for-each big-index distance mismatch");
    thrust_edge_detail::DeviceFlag flag;
    *flag = false;
    thrust::for_each(thrust::device, begin, end,
                     thrust_edge_detail::SetExpectedFlagForEach{.expected = (1ULL << magnitude) - 1, .flag = flag.raw()});
    const bool observed = *flag;
    if (!observed) throw std::runtime_error("for-each big-index terminal value was not visited");
  }

  void adjacentDifferenceBigIndexes(uint32_t magnitude) {
    if (magnitude >= 63) throw std::invalid_argument("big-index magnitude must be below 63");
    const auto begin = thrust::make_counting_iterator<int64_t>(1);
    const auto end = begin + (1LL << magnitude);
    if (end - begin != (1LL << magnitude)) throw std::runtime_error("adjacent-difference big-index distance mismatch");
    thrust_edge_detail::DeviceFlag flag;
    *flag = true;
    thrust::adjacent_difference(thrust::device, begin, end, thrust_edge_detail::DetectWrongDifference{flag.raw()});
    const bool observed = *flag;
    if (!observed) throw std::runtime_error("adjacent-difference big-index produced a wrong difference");
  }

  template <class T> int64_t composedIteratorReduce(size_t size) {
    const auto indices = thrust::make_counting_iterator<size_t>(0);
    const auto source = thrust::make_transform_iterator(indices, edge::ValueAt<T>{});
    const auto reverseIndices = thrust::make_transform_iterator(indices, edge::ReverseIndex{size});
    const auto reverse = thrust::make_permutation_iterator(source, reverseIndices);
    const auto zipped = thrust::make_zip_iterator(thrust::make_tuple(reverse, source));
    return thrust::transform_reduce(zipped, zipped + size, thrust_edge_detail::TupleWidePairValue<T>{}, int64_t{},
                                    thrust_support::Plus<int64_t>{});
  }

  template <class T> uint64_t biasedIndexChecksum(uint64_t base, size_t size) {
    const auto begin = thrust::make_counting_iterator<uint64_t>(base);
    return thrust::transform_reduce(begin, begin + static_cast<int64_t>(size), edge::BiasedHash<T>{}, uint64_t{}, edge::Xor64{});
  }

  template <class T> std::vector<T> segmentedInclusiveScan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
    const thrust::device_vector<int32_t> deviceKeys(keys.begin(), keys.end());
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::inclusive_scan_by_key(deviceKeys.begin(), deviceKeys.end(), source.begin(), output.begin());
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> inplaceInclusiveScan(const std::vector<T> &storage, size_t offset, size_t count) {
    thrust::device_vector<T> output(storage.begin(), storage.end());
    thrust::inclusive_scan(output.begin() + static_cast<ptrdiff_t>(offset), output.begin() + static_cast<ptrdiff_t>(offset + count),
                           output.begin() + static_cast<ptrdiff_t>(offset));
    return thrust_edge_detail::host(output);
  }

  template <class T> support::KeyValues<T> stableSortPayload(const std::vector<T> &keys, const std::vector<int32_t> &payload) {
    thrust::device_vector<T> deviceKeys(keys.begin(), keys.end());
    thrust::device_vector<int32_t> devicePayload(payload.begin(), payload.end());
    thrust::stable_sort_by_key(deviceKeys.begin(), deviceKeys.end(), devicePayload.begin());
    return {thrust_edge_detail::host(deviceKeys), thrust_edge_detail::host(devicePayload)};
  }

  template <class T> std::vector<T> dependencyDiamond(const std::vector<T> &input) {
    if (input.empty()) return {};
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> left(input.size());
    thrust::device_vector<T> right(input.size());
    thrust::device_vector<T> output(input.size());
    thrust_edge_detail::StreamResource streamLeft("stream create left");
    thrust_edge_detail::StreamResource streamRight("stream create right");
    thrust_edge_detail::StreamResource streamJoin("stream create join");
    thrust_edge_detail::EventResource leftReady("event create left");
    thrust_edge_detail::EventResource rightReady("event create right");
    thrust::transform(thrust_edge_detail::policy(streamLeft.get()), source.begin(), source.end(), left.begin(), support::Affine<T>{});
    thrust::transform(thrust_edge_detail::policy(streamRight.get()), source.begin(), source.end(), right.begin(), support::Square<T>{});
    thrust_support::record(leftReady.get(), streamLeft.get(), "event record left");
    thrust_support::record(rightReady.get(), streamRight.get(), "event record right");
    thrust_support::waitEvent(streamJoin.get(), leftReady.get(), "stream wait event left");
    thrust_support::waitEvent(streamJoin.get(), rightReady.get(), "stream wait event right");
    thrust::transform(thrust_edge_detail::policy(streamJoin.get()), left.begin(), left.end(), right.begin(), output.begin(),
                      support::Combine<T>{});
    thrust_support::synchronise(streamJoin.get(), "stream synchronise join");
    leftReady.close("event destroy left");
    rightReady.close("event destroy right");
    streamLeft.close("stream destroy left");
    streamRight.close("stream destroy right");
    streamJoin.close("stream destroy join");
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> numericEdgeRoundtrip(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform(source.begin(), source.end(), output.begin(), edge::Identity<T>{});
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> primitiveBlockScan(const std::vector<T> &input, size_t blockSize) {
    if (blockSize != 32) SKIP("primitive block scan currently requires a block size of 32");
    if (input.empty()) return {};
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    constexpr uint32_t block = 32;
    const auto grid = static_cast<uint32_t>((input.size() + block - 1) / block);
#ifdef __HIPCC__
    hipLaunchKernelGGL((thrust_edge_detail::blockScanKernel<T, block>), dim3(grid), dim3(block), 0, 0,
                       thrust::raw_pointer_cast(source.data()), thrust::raw_pointer_cast(output.data()), input.size());
    thrust_edge_detail::check(hipGetLastError(), "rocPRIM block scan launch");
    thrust_edge_detail::check(hipDeviceSynchronize(), "rocPRIM block scan synchronise");
#else
    thrust_edge_detail::blockScanKernel<T, block>
        <<<grid, block>>>(thrust::raw_pointer_cast(source.data()), thrust::raw_pointer_cast(output.data()), input.size());
    thrust_edge_detail::check(cudaGetLastError(), "CUB block scan launch");
    thrust_edge_detail::check(cudaDeviceSynchronize(), "CUB block scan synchronise");
#endif
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> runtimeErrorRecover(const std::vector<T> &input) {
#ifdef __HIPCC__
    const auto failure = hipMalloc(nullptr, sizeof(T));
    if (failure == hipSuccess) throw std::runtime_error("invalid HIP allocation unexpectedly succeeded");
    (void)hipGetLastError();
#else
    const auto failure = cudaMalloc(nullptr, sizeof(T));
    if (failure == cudaSuccess) throw std::runtime_error("invalid CUDA allocation unexpectedly succeeded");
    (void)cudaGetLastError();
#endif
    const thrust::device_vector<T> recovered(input.begin(), input.end());
    return thrust_edge_detail::host(recovered);
  }
};
} // namespace unified::backend

#ifdef UNIFIED_API_THRUST_DEVICE_FUNCTIONS

namespace unified::gpu_calls {
  #ifdef __HIPCC__
inline constexpr auto BackendName = "hip";
inline constexpr auto Success = hipSuccess;
inline auto lastError() { return hipGetLastError(); }
inline auto errorString(hipError_t status) { return hipGetErrorString(status); }
  #else
inline constexpr auto BackendName = "cuda";
inline constexpr auto Success = cudaSuccess;
inline auto lastError() { return cudaGetLastError(); }
inline auto errorString(cudaError_t status) { return cudaGetErrorString(status); }
  #endif
// Focused ports of GPUOcelot's direct/indirect function-call checks.
// Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
// SPDX-License-Identifier: BSD-3-Clause; see licences/gpuocelot.txt.
inline __device__ __noinline__ float square(float value) { return value * value; }
inline __device__ __noinline__ float twice(float value) { return value * 2; }
inline __device__ __noinline__ float thrice(float value) { return value * 3; }
inline __device__ __noinline__ float fourTimes(float value) { return value * 4; }
inline __device__ __noinline__ float fiveTimes(float value) { return value * 5; }

template <bool Indirect> __global__ void applyDeviceFunction(float *values, size_t size) {
  const size_t index = threadIdx.x + (static_cast<size_t>(blockIdx.x) * blockDim.x);
  if (index >= size) return;
  const float input = values[index + 1];
  if constexpr (Indirect) {
    float (*function)(float) = nullptr;
    switch (index & 3) {
      case 0: function = &twice; break;
      case 1: function = &thrice; break;
      case 2: function = &fourTimes; break;
      default: function = &fiveTimes; break;
    }
    values[index + 1] = function(input);
  } else {
    values[index + 1] = square(input);
  }
}

template <bool Indirect> void deviceFunction() {
  constexpr size_t size = 257;
  constexpr float canary = -12345.0F;
  std::vector<float> values(size + 2, canary);
  for (size_t index = 0; index < size; ++index)
    values[index + 1] = (static_cast<float>(index) - 128.0F) * 0.25F;
  thrust::device_vector<float> device(values);
  applyDeviceFunction<Indirect><<<3, 128>>>(thrust::raw_pointer_cast(device.data()), size);
  if (const auto error = lastError(); error != Success)
    throw std::runtime_error(std::string("device function launch: ") + errorString(error));
  thrust::copy(device.begin(), device.end(), values.begin());
  if (values.front() != canary || values.back() != canary) throw std::runtime_error("device function overwrote a boundary canary");
  for (size_t index = 0; index < size; ++index) {
    const float input = (static_cast<float>(index) - 128.0F) * 0.25F;
    const float expected = Indirect ? input * static_cast<float>(2 + (index & 3)) : input * input;
    if (values[index + 1] != expected) throw std::runtime_error(fmt::format("device function result mismatch at lane {}", index));
  }
}
} // namespace unified::gpu_calls

#endif

#if defined(__HIPCC__) && defined(UNIFIED_API_ROCTHRUST)
  #include "rocthrust_support.hpp"
#endif

namespace unified::backend {
using Algorithms = ThrustAlgorithms;
using EdgeCases = ThrustEdgeCases;
using Async = ThrustAsync;
} // namespace unified::backend
