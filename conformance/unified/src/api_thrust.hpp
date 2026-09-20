#pragma once

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <iterator>
#include <stdexcept>
#include <string>
#include <type_traits>
#include <vector>

#include <thrust/adjacent_difference.h>
#include <thrust/binary_search.h>
#include <thrust/copy.h>
#include <thrust/count.h>
#include <thrust/device_free.h>
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

#include "template_algorithm.hpp"
#include "template_async.hpp"
#include "template_edge.hpp"

#if defined(__HIPCC__)
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

#if defined(__HIPCC__)
  #include <rocprim/block/block_scan.hpp>
#else
  #include <cub/block/block_scan.cuh>
#endif

#if defined(UNIFIED_API_THRUST_DEVICE_FUNCTIONS)
  #include <catch2/catch_test_macros.hpp>
  #if !defined(__HIPCC__)
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
#if defined(__HIPCC__)
using Stream = hipStream_t;
using Event = hipEvent_t;
inline void check(hipError_t status, const char *where) {
  if (status != hipSuccess) throw std::runtime_error(std::string(where) + ": " + hipGetErrorString(status));
}
inline auto policy(Stream stream) { return thrust::hip::par.on(stream); }
inline void synchronize(Stream stream, const char *where) { check(hipStreamSynchronize(stream), where); }
inline void record(Event event, Stream stream, const char *where) { check(hipEventRecord(event, stream), where); }
inline void wait_event(Stream stream, Event event, const char *where) { check(hipStreamWaitEvent(stream, event, 0), where); }
#else
using Stream = cudaStream_t;
using Event = cudaEvent_t;
inline void check(cudaError_t status, const char *where) {
  if (status != cudaSuccess) throw std::runtime_error(std::string(where) + ": " + cudaGetErrorString(status));
}
inline auto policy(Stream stream) { return thrust::cuda::par.on(stream); }
inline void synchronize(Stream stream, const char *where) { check(cudaStreamSynchronize(stream), where); }
inline void record(Event event, Stream stream, const char *where) { check(cudaEventRecord(event, stream), where); }
inline void wait_event(Stream stream, Event event, const char *where) { check(cudaStreamWaitEvent(stream, event, 0), where); }
#endif

class StreamResource {
public:
  explicit StreamResource(const char *where) {
#if defined(__HIPCC__)
    check(hipStreamCreate(&value_), where);
#else
    check(cudaStreamCreate(&value_), where);
#endif
  }
  StreamResource(const StreamResource &) = delete;
  StreamResource &operator=(const StreamResource &) = delete;
  ~StreamResource() {
    if (value_) {
#if defined(__HIPCC__)
      (void)hipStreamDestroy(value_);
#else
      (void)cudaStreamDestroy(value_);
#endif
    }
  }
  Stream get() const { return value_; }
  void close(const char *where) {
#if defined(__HIPCC__)
    check(hipStreamDestroy(value_), where);
#else
    check(cudaStreamDestroy(value_), where);
#endif
    value_ = nullptr;
  }

private:
  Stream value_{};
};

class EventResource {
public:
  explicit EventResource(const char *where) {
#if defined(__HIPCC__)
    check(hipEventCreateWithFlags(&value_, hipEventDisableTiming), where);
#else
    check(cudaEventCreateWithFlags(&value_, cudaEventDisableTiming), where);
#endif
  }
  EventResource(const EventResource &) = delete;
  EventResource &operator=(const EventResource &) = delete;
  ~EventResource() {
    if (value_) {
#if defined(__HIPCC__)
      (void)hipEventDestroy(value_);
#else
      (void)cudaEventDestroy(value_);
#endif
    }
  }
  Event get() const { return value_; }
  void close(const char *where) {
#if defined(__HIPCC__)
    check(hipEventDestroy(value_), where);
#else
    check(cudaEventDestroy(value_), where);
#endif
    value_ = nullptr;
  }

private:
  Event value_{};
};

template <class T> std::vector<T> host(const thrust::device_vector<T> &input) {
  std::vector<T> result(input.size());
  if (!input.empty()) thrust::copy(input.begin(), input.end(), result.data());
  return result;
}
} // namespace unified::backend::thrust_support

namespace unified::backend {
namespace thrust_algorithm_detail {
template <class T> __global__ void legacy_device_dispatch_kernel(const T *first, T *output, size_t size) {
  // The old vendor suites exercise Thrust from inside a device-launched
  // kernel. This is intentionally distinct from the ordinary host dispatch
  // used by the portable cases: it catches nested execution/iterator ABI
  // regressions while remaining a small, deterministic fixture.
  thrust::adjacent_difference(thrust::device, first, first + size, output);
}

#if defined(__HIPCC__)
inline bool uses_wave32() {
  int device = 0;
  hipDeviceProp_t properties{};
  return hipGetDevice(&device) == hipSuccess && hipGetDeviceProperties(&properties, device) == hipSuccess && properties.warpSize == 32;
}

inline void check_legacy(hipError_t status, const char *where) {
  if (status != hipSuccess) throw std::runtime_error(std::string(where) + ": " + hipGetErrorString(status));
}
#else
inline void check_legacy(cudaError_t status, const char *where) {
  if (status != cudaSuccess) throw std::runtime_error(std::string(where) + ": " + cudaGetErrorString(status));
}
#endif
} // namespace thrust_algorithm_detail

class ThrustAlgorithms {
public:
  template <class T> bool supports_copy(size_t) const { return true; }

  template <class T> bool supports_copy_if(size_t) const { return true; }

  template <class T> bool supports_copy_n(size_t) const { return true; }

  template <class T> bool supports_uninitialized_copy(size_t) const { return true; }

  template <class T> bool supports_uninitialized_fill(size_t) const { return true; }

  template <class T> bool supports_fill(size_t) const { return true; }

  template <class T> bool supports_generate(size_t) const { return true; }

  template <class T> bool supports_for_each(size_t) const { return true; }

  template <class T> bool supports_transform_unary(size_t) const { return true; }

  template <class T> bool supports_transform_binary(size_t) const { return true; }

  template <class T> bool supports_replace_if(size_t) const { return true; }

  template <class T> bool supports_replace_copy_if(size_t) const { return true; }

  template <class T> bool supports_remove_if(size_t) const { return true; }

  template <class T> bool supports_remove_copy_if(size_t) const { return true; }

  template <class T> bool supports_reverse(size_t) const { return true; }

  template <class T> bool supports_reverse_copy(size_t) const { return true; }

  template <class T> bool supports_shuffle(size_t) const { return true; }

  template <class T> bool supports_swap_ranges(size_t) const { return true; }

  template <class T> bool supports_reduce(size_t) const { return true; }

  template <class T> bool supports_transform_reduce(size_t) const { return true; }

  template <class T> bool supports_inner_product(size_t) const { return true; }

  template <class T> bool supports_inclusive_scan(size_t) const { return true; }

  template <class T> bool supports_exclusive_scan(size_t) const { return true; }

  template <class T> bool supports_transform_inclusive_scan(size_t) const { return true; }

  template <class T> bool supports_transform_exclusive_scan(size_t) const { return true; }

  template <class T> bool supports_adjacent_difference(size_t) const { return true; }

  template <class T> bool supports_sort(size_t size) const {
#if defined(__HIPCC__)
    if constexpr (std::is_same_v<T, int32_t>)
      if (thrust_algorithm_detail::uses_wave32() && size == 4096) return false;
#endif
    return true;
  }

  template <class T> bool supports_stable_sort(size_t size) const { return supports_sort<T>(size); }

  template <class T> bool supports_sort_descending(size_t size) const { return supports_sort<T>(size); }

  template <class T> bool supports_sort_by_key(size_t) const { return true; }

  template <class T> bool supports_stable_sort_by_key(size_t) const { return true; }

  template <class T> bool supports_unique(size_t) const { return true; }

  template <class T> bool supports_unique_copy(size_t) const { return true; }

  template <class T> bool supports_count(size_t) const { return true; }

  template <class T> bool supports_count_if(size_t) const { return true; }

  template <class T> bool supports_find(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries fail for 32-bit values on wave32.
#if defined(__HIPCC__)
    if constexpr (sizeof(T) == 4)
      if (thrust_algorithm_detail::uses_wave32() && size >= 31) return false;
#endif
    return true;
  }

  template <class T> bool supports_find_if(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries return a bad first result on wave32.
#if defined(__HIPCC__)
    if (thrust_algorithm_detail::uses_wave32() && sizeof(T) >= 4 && size >= 1) return false;
#endif
    return true;
  }

  template <class T> bool supports_all_of(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries fail for 32-bit values on wave32.
#if defined(__HIPCC__)
    if constexpr (sizeof(T) == 4)
      if (thrust_algorithm_detail::uses_wave32() && size >= 31) return false;
#endif
    return true;
  }

  template <class T> bool supports_any_of(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries return a bad first result on wave32.
#if defined(__HIPCC__)
    if (thrust_algorithm_detail::uses_wave32() && sizeof(T) >= 4 && size >= 1) return false;
#endif
    return true;
  }

  template <class T> bool supports_none_of(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries return a bad first result on wave32.
#if defined(__HIPCC__)
    if (thrust_algorithm_detail::uses_wave32() && sizeof(T) >= 4 && size >= 1) return false;
#endif
    return true;
  }

  template <class T> bool supports_equal(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries return a bad first result on wave32.
#if defined(__HIPCC__)
    if (thrust_algorithm_detail::uses_wave32() && sizeof(T) >= 4 && size >= 1) return false;
#endif
    return true;
  }

  template <class T> bool supports_mismatch(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries return a bad first result on wave32.
#if defined(__HIPCC__)
    if (thrust_algorithm_detail::uses_wave32() && sizeof(T) >= 4 && size >= 1) return false;
#endif
    return true;
  }

  template <class T> bool supports_lower_bound(size_t) const { return true; }

  template <class T> bool supports_upper_bound(size_t) const { return true; }

  template <class T> bool supports_binary_search(size_t) const { return true; }

  template <class T> bool supports_equal_range(size_t) const { return true; }

  template <class T> bool supports_merge(size_t) const { return true; }

  template <class T> bool supports_partition(size_t) const { return true; }

  template <class T> bool supports_stable_partition(size_t) const { return true; }

  template <class T> bool supports_partition_copy(size_t) const { return true; }

  template <class T> bool supports_is_partitioned(size_t) const { return true; }

  template <class T> bool supports_is_partitioned_false(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries return a bad first result on wave32.
#if defined(__HIPCC__)
    if (thrust_algorithm_detail::uses_wave32() && sizeof(T) >= 4 && size >= 1) return false;
#endif
    return true;
  }

  template <class T> bool supports_partition_point(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries fail for 32-bit values on wave32.
#if defined(__HIPCC__)
    if constexpr (sizeof(T) == 4)
      if (thrust_algorithm_detail::uses_wave32() && size >= 31) return false;
#endif
    return true;
  }

  template <class T> bool supports_set_union(size_t) const { return true; }

  template <class T> bool supports_set_intersection(size_t) const { return true; }

  template <class T> bool supports_set_difference(size_t) const { return true; }

  template <class T> bool supports_set_symmetric_difference(size_t) const { return true; }

  template <class T> bool supports_min_element(size_t) const { return true; }

  template <class T> bool supports_max_element(size_t) const { return true; }

  template <class T> bool supports_minmax_element(size_t) const { return true; }

  template <class T> bool supports_is_sorted(size_t) const { return true; }

  template <class T> bool supports_is_sorted_false(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries return a bad first result on wave32.
#if defined(__HIPCC__)
    if (thrust_algorithm_detail::uses_wave32() && sizeof(T) >= 4 && size >= 1) return false;
#endif
    return true;
  }

  template <class T> bool supports_is_sorted_until(size_t size) const {
// rocThrust/rocPRIM 4.2 scalar queries return a bad first result on wave32.
#if defined(__HIPCC__)
    if (thrust_algorithm_detail::uses_wave32() && sizeof(T) >= 4 && size >= 1) return false;
#endif
    return true;
  }

  template <class T> bool supports_rotate(size_t) const { return false; }

  template <class T> bool supports_rotate_copy(size_t) const { return false; }

  template <class T> bool supports_includes(size_t) const { return false; }

  template <class T> bool supports_includes_false(size_t) const { return false; }

  template <class T> bool supports_gather(size_t) const { return true; }

  template <class T> bool supports_scatter(size_t) const { return true; }

  template <class T> bool supports_reduce_by_key(size_t) const { return true; }

  template <class T> bool supports_inclusive_scan_by_key(size_t) const { return true; }

  template <class T> bool supports_exclusive_scan_by_key(size_t) const { return true; }

  template <class T> bool supports_unique_by_key(size_t) const { return true; }

  template <class T> bool supports_merge_by_key(size_t) const { return true; }

  template <class T> bool supports_partial_sort(size_t) const { return false; }

  template <class T> bool supports_nth_element(size_t) const { return false; }

  template <class T> bool supports_inplace_merge(size_t) const { return false; }

  template <class T> std::vector<T> copy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::copy(source.begin(), source.end(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> copy_if(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    const auto end = thrust::copy_if(source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> copy_n(const std::vector<T> &input, size_t count) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(count);
    thrust::copy_n(source.begin(), count, output.begin());
    return host(output);
  }

  template <class T> std::vector<T> uninitialized_copy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::uninitialized_copy(source.begin(), source.end(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> uninitialized_fill(size_t size, T value) {
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

  template <class T> std::vector<T> for_each(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::for_each(output.begin(), output.end(), algorithms::Increment<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transform_unary(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform(source.begin(), source.end(), output.begin(), algorithms::Affine<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transform_binary(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size());
    thrust::transform(a.begin(), a.end(), b.begin(), output.begin(), algorithms::Combine<T>{});
    return host(output);
  }

  template <class T> std::vector<T> replace_if(const std::vector<T> &input, T replacement) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::replace_if(output.begin(), output.end(), algorithms::IsEven<T>{}, replacement);
    return host(output);
  }

  template <class T> std::vector<T> replace_copy_if(const std::vector<T> &input, T replacement) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::replace_copy_if(source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{}, replacement);
    return host(output);
  }

  template <class T> std::vector<T> remove_if(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto end = thrust::remove_if(output.begin(), output.end(), algorithms::IsEven<T>{});
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> remove_copy_if(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    const auto end = thrust::remove_copy_if(source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> algorithms::TwoRanges<T> swap_ranges(const std::vector<T> &left, const std::vector<T> &right) {
    thrust::device_vector<T> a(left.begin(), left.end());
    thrust::device_vector<T> b(right.begin(), right.end());
    thrust::swap_ranges(a.begin(), a.end(), b.begin());
    return {host(a), host(b)};
  }

  template <class T> T reduce(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::reduce(source.begin(), source.end(), T{}, algorithms::Plus<T>{});
  }

  template <class T> T transform_reduce(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::transform_reduce(source.begin(), source.end(), algorithms::Square<T>{}, T{}, algorithms::Plus<T>{});
  }

  template <class T> T inner_product(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    return thrust::inner_product(a.begin(), a.end(), b.begin(), T{});
  }

  template <class T> std::vector<T> inclusive_scan(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::inclusive_scan(source.begin(), source.end(), output.begin(), algorithms::Plus<T>{});
    return host(output);
  }

  template <class T> std::vector<T> exclusive_scan(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::exclusive_scan(source.begin(), source.end(), output.begin(), T{}, algorithms::Plus<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transform_inclusive_scan(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform_inclusive_scan(source.begin(), source.end(), output.begin(), algorithms::Square<T>{}, algorithms::Plus<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transform_exclusive_scan(const std::vector<T> &input) {
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

  template <class T> std::vector<T> stable_sort(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::stable_sort(output.begin(), output.end(), algorithms::Less<T>{});
    return host(output);
  }

  template <class T> std::vector<T> sort_descending(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    thrust::sort(output.begin(), output.end(), algorithms::Greater<T>{});
    return host(output);
  }

  template <class T> algorithms::KeyValues<T> sort_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    thrust::device_vector<int32_t> device_values(values.begin(), values.end());
    thrust::sort_by_key(device_keys.begin(), device_keys.end(), device_values.begin(), algorithms::Less<T>{});
    return {host(device_keys), host(device_values)};
  }

  template <class T> algorithms::KeyValues<T> stable_sort_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    thrust::device_vector<int32_t> device_values(values.begin(), values.end());
    thrust::stable_sort_by_key(device_keys.begin(), device_keys.end(), device_values.begin(), algorithms::Less<T>{});
    return {host(device_keys), host(device_values)};
  }

  template <class T> std::vector<T> unique(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto end = thrust::unique(output.begin(), output.end());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> unique_copy(const std::vector<T> &input) {
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

  template <class T> size_t count_if(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::count_if(source.begin(), source.end(), algorithms::IsEven<T>{}));
  }

  template <class T> size_t find_if(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::find_if(source.begin(), source.end(), algorithms::IsEven<T>{}) - source.begin());
  }

  template <class T> size_t find(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::find(source.begin(), source.end(), value) - source.begin());
  }

  template <class T> bool all_of(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::all_of(source.begin(), source.end(), algorithms::IsEven<T>{});
  }

  template <class T> bool any_of(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::any_of(source.begin(), source.end(), algorithms::IsEven<T>{});
  }

  template <class T> bool none_of(const std::vector<T> &input) {
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
    return {static_cast<size_t>(result.first - a.begin()), static_cast<size_t>(result.second - b.begin())};
  }

  template <class T> size_t lower_bound(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::lower_bound(source.begin(), source.end(), value) - source.begin());
  }

  template <class T> size_t upper_bound(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::upper_bound(source.begin(), source.end(), value) - source.begin());
  }

  template <class T> bool binary_search(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::binary_search(source.begin(), source.end(), value);
  }

  template <class T> algorithms::TwoPositions equal_range(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const auto result = thrust::equal_range(source.begin(), source.end(), value);
    return {static_cast<size_t>(result.first - source.begin()), static_cast<size_t>(result.second - source.begin())};
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

  template <class T> algorithms::Partitioned<T> stable_partition(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto boundary = thrust::stable_partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    return {host(output), static_cast<size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::TwoRanges<T> partition_copy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> selected(input.size());
    thrust::device_vector<T> rejected(input.size());
    const auto ends = thrust::partition_copy(source.begin(), source.end(), selected.begin(), rejected.begin(), algorithms::IsEven<T>{});
    selected.resize(static_cast<size_t>(ends.first - selected.begin()));
    rejected.resize(static_cast<size_t>(ends.second - rejected.begin()));
    return {host(selected), host(rejected)};
  }

  template <class T> bool is_partitioned(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::is_partitioned(source.begin(), source.end(), algorithms::IsEven<T>{});
  }

  template <class T> size_t partition_point(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::partition_point(source.begin(), source.end(), algorithms::IsEven<T>{}) - source.begin());
  }

  template <class T> std::vector<T> adjacent_difference(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::adjacent_difference(source.begin(), source.end(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> legacy_device_dispatch(size_t size) {
    const auto input = support::algorithm_values<T>(size, 1);
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(size);
    thrust_algorithm_detail::legacy_device_dispatch_kernel<<<1, 1>>>(thrust::raw_pointer_cast(source.data()),
                                                                     thrust::raw_pointer_cast(output.data()), size);
#if defined(__HIPCC__)
    thrust_algorithm_detail::check_legacy(hipGetLastError(), "legacy device dispatch launch");
    thrust_algorithm_detail::check_legacy(hipDeviceSynchronize(), "legacy device dispatch synchronize");
#else
    thrust_algorithm_detail::check_legacy(cudaGetLastError(), "legacy device dispatch launch");
    thrust_algorithm_detail::check_legacy(cudaDeviceSynchronize(), "legacy device dispatch synchronize");
#endif
    return host(output);
  }

  template <class T> std::vector<T> set_union(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size() + right.size());
    const auto end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> set_intersection(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(std::min(left.size(), right.size()));
    const auto end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> set_difference(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size());
    const auto end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> set_symmetric_difference(const std::vector<T> &left, const std::vector<T> &right) {
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

  template <class T> std::vector<T> reverse_copy(const std::vector<T> &input) {
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

  template <class T> size_t min_element(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::min_element(source.begin(), source.end()) - source.begin());
  }

  template <class T> size_t max_element(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::max_element(source.begin(), source.end()) - source.begin());
  }

  template <class T> algorithms::TwoPositions minmax_element(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const auto result = thrust::minmax_element(source.begin(), source.end());
    return {static_cast<size_t>(result.first - source.begin()), static_cast<size_t>(result.second - source.begin())};
  }

  template <class T> bool is_sorted(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::is_sorted(source.begin(), source.end());
  }

  template <class T> size_t is_sorted_until(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<size_t>(thrust::is_sorted_until(source.begin(), source.end()) - source.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &, size_t) { SKIP("rotate is not a Thrust API"); }

  template <class T> std::vector<T> rotate_copy(const std::vector<T> &, size_t) { SKIP("rotate-copy is not a Thrust API"); }

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

  template <class T> algorithms::KeyValues<T> reduce_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    const thrust::device_vector<T> source_keys(keys.begin(), keys.end());
    const thrust::device_vector<int32_t> source_values(values.begin(), values.end());
    thrust::device_vector<T> output_keys(keys.size());
    thrust::device_vector<int32_t> output_values(values.size());
    const auto ends =
        thrust::reduce_by_key(source_keys.begin(), source_keys.end(), source_values.begin(), output_keys.begin(), output_values.begin());
    output_keys.resize(static_cast<size_t>(ends.first - output_keys.begin()));
    output_values.resize(static_cast<size_t>(ends.second - output_values.begin()));
    return {host(output_keys), host(output_values)};
  }

  template <class T> algorithms::KeyValues<T> inclusive_scan_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    const thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    const thrust::device_vector<int32_t> source(values.begin(), values.end());
    thrust::device_vector<int32_t> output(values.size());
    thrust::inclusive_scan_by_key(device_keys.begin(), device_keys.end(), source.begin(), output.begin());
    return {keys, host(output)};
  }

  template <class T> algorithms::KeyValues<T> exclusive_scan_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    const thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    const thrust::device_vector<int32_t> source(values.begin(), values.end());
    thrust::device_vector<int32_t> output(values.size());
    thrust::exclusive_scan_by_key(device_keys.begin(), device_keys.end(), source.begin(), output.begin(), 0);
    return {keys, host(output)};
  }

  template <class T> algorithms::KeyValues<T> unique_by_key(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    thrust::device_vector<T> output_keys(keys.begin(), keys.end());
    thrust::device_vector<int32_t> output_values(values.begin(), values.end());
    const auto ends = thrust::unique_by_key(output_keys.begin(), output_keys.end(), output_values.begin());
    output_keys.resize(static_cast<size_t>(ends.first - output_keys.begin()));
    output_values.resize(static_cast<size_t>(ends.second - output_values.begin()));
    return {host(output_keys), host(output_values)};
  }

  template <class T> algorithms::KeyValues<T> merge_by_key(const algorithms::KeyValues<T> &left, const algorithms::KeyValues<T> &right) {
    const thrust::device_vector<T> left_keys(left.keys.begin(), left.keys.end());
    const thrust::device_vector<T> right_keys(right.keys.begin(), right.keys.end());
    const thrust::device_vector<int32_t> left_values(left.values.begin(), left.values.end());
    const thrust::device_vector<int32_t> right_values(right.values.begin(), right.values.end());
    thrust::device_vector<T> output_keys(left.keys.size() + right.keys.size());
    thrust::device_vector<int32_t> output_values(left.values.size() + right.values.size());
    thrust::merge_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                         right_values.begin(), output_keys.begin(), output_values.begin());
    return {host(output_keys), host(output_values)};
  }

  template <class T> std::vector<T> partial_sort(const std::vector<T> &, size_t) { SKIP("partial-sort is not a Thrust API"); }

  template <class T> std::vector<T> nth_element(const std::vector<T> &, size_t) { SKIP("nth-element is not a Thrust API"); }

  template <class T> std::vector<T> inplace_merge(const std::vector<T> &, size_t) { SKIP("inplace-merge is not a Thrust API"); }

private:
  template <class T> static std::vector<T> host(const thrust::device_vector<T> &input) {
    std::vector<T> result(input.size());
    if (!input.empty()) thrust::copy(input.begin(), input.end(), result.data());
    return result;
  }
};
} // namespace unified::backend

namespace unified::backend {
// Thrust's stream execution policies are the common CUDA/rocThrust async
// surface. Every method waits for the queued work (with either stream
// synchronization or a future completion), so dependency ordering is part of
// the assertion.
class ThrustAsync {
public:
  template <class T> bool supports_copy() const { return true; }

  template <class T> bool supports_reduce() const { return true; }

  template <class T> bool supports_scan() const { return true; }

  template <class T> bool supports_sort() const { return true; }

  template <class T> bool supports_future_chain() const { return true; }

  template <class T> bool supports_event_chain() const { return true; }

  template <class T> bool supports_interleaved_streams() const { return true; }

  template <class T> bool supports_inclusive_counting() const {
#if defined(__HIPCC__)
    return true;
#else
    return false;
#endif
  }

  template <class T> bool supports_exclusive_counting() const {
#if defined(__HIPCC__)
    return true;
#else
    return false;
#endif
  }

  template <class T> bool supports_inclusive_discard() const {
#if defined(__HIPCC__)
    return true;
#else
    return false;
#endif
  }

  template <class T> bool supports_exclusive_discard() const {
#if defined(__HIPCC__)
    return true;
#else
    return false;
#endif
  }

  template <class T> bool supports_inclusive_stateful() const {
#if defined(__HIPCC__)
    return true;
#else
    return false;
#endif
  }

  template <class T> bool supports_exclusive_stateful() const {
#if defined(__HIPCC__)
    return true;
#else
    return false;
#endif
  }

  template <class T> std::vector<T> async_copy(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust_support::StreamResource stream("async copy stream create");
    thrust::copy(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin());
    thrust_support::synchronize(stream.get(), "async copy synchronize");
    return thrust_support::host(output);
  }

  template <class T> T async_reduce(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_support::StreamResource stream("async reduce stream create");
    // Unlike the ordinary stream-policy overload, this is the vendor async
    // API: reduce returns a device future and get() is the completion point.
#if UNIFIED_HAS_THRUST_ASYNC
    auto result = thrust::async::reduce(thrust_support::policy(stream.get()), source.begin(), source.end(), T{}, thrust::plus<T>{});
    return result.get();
#else
    return thrust::reduce(thrust_support::policy(stream.get()), source.begin(), source.end(), T{}, thrust::plus<T>{});
#endif
  }

  template <class T> std::vector<T> async_scan(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust_support::StreamResource stream("async scan stream create");
    thrust::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(), thrust::plus<T>{});
    thrust_support::synchronize(stream.get(), "async scan synchronize");
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> async_inclusive_scan_counting(size_t size) {
    thrust::device_vector<T> output(size);
    const auto first = thrust::make_counting_iterator<T>(T{0});
    const auto last = first + static_cast<ptrdiff_t>(size);
    thrust_support::StreamResource stream("HIP async inclusive counting stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    auto completion = thrust::async::inclusive_scan(thrust_support::policy(stream.get()), first, last, output.begin(), thrust::plus<T>{});
    completion.wait();
#else
    thrust::inclusive_scan(thrust_support::policy(stream.get()), first, last, output.begin(), thrust::plus<T>{});
    thrust_support::synchronize(stream.get(), "HIP async inclusive counting synchronize");
#endif
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> async_exclusive_scan_counting(size_t size) {
    thrust::device_vector<T> output(size);
    const auto first = thrust::make_counting_iterator<T>(T{0});
    const auto last = first + static_cast<ptrdiff_t>(size);
    thrust_support::StreamResource stream("HIP async exclusive counting stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    auto completion =
        thrust::async::exclusive_scan(thrust_support::policy(stream.get()), first, last, output.begin(), T{}, thrust::plus<T>{});
    completion.wait();
#else
    thrust::exclusive_scan(thrust_support::policy(stream.get()), first, last, output.begin(), T{}, thrust::plus<T>{});
    thrust_support::synchronize(stream.get(), "HIP async exclusive counting synchronize");
#endif
    return thrust_support::host(output);
  }

  template <class T> void async_inclusive_scan_discard(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_support::StreamResource stream("HIP async inclusive discard stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    auto completion = thrust::async::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(),
                                                    thrust::make_discard_iterator(), thrust::plus<T>{});
    completion.wait();
#else
    thrust::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), thrust::make_discard_iterator(),
                           thrust::plus<T>{});
    thrust_support::synchronize(stream.get(), "HIP async inclusive discard synchronize");
#endif
  }

  template <class T> void async_exclusive_scan_discard(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_support::StreamResource stream("HIP async exclusive discard stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    auto completion = thrust::async::exclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(),
                                                    thrust::make_discard_iterator(), T{}, thrust::plus<T>{});
    completion.wait();
#else
    thrust::exclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), thrust::make_discard_iterator(), T{},
                           thrust::plus<T>{});
    thrust_support::synchronize(stream.get(), "HIP async exclusive discard synchronize");
#endif
  }

  template <class T> std::vector<T> async_inclusive_scan_stateful(size_t size) {
    const auto input = support::async_input_values<T>(size);
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(size);
    thrust_support::StreamResource stream("HIP async inclusive stateful stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    auto completion = thrust::async::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(),
                                                    hip_async::StatefulOperator<T>{T{2}});
    completion.wait();
#else
    thrust::inclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(),
                           hip_async::StatefulOperator<T>{T{2}});
    thrust_support::synchronize(stream.get(), "HIP async inclusive stateful synchronize");
#endif
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> async_exclusive_scan_stateful(size_t size) {
    const auto input = support::async_input_values<T>(size);
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(size);
    thrust_support::StreamResource stream("HIP async exclusive stateful stream create");
#if UNIFIED_HAS_THRUST_ASYNC
    auto completion = thrust::async::exclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(),
                                                    T{42}, hip_async::StatefulOperator<T>{T{2}});
    completion.wait();
#else
    thrust::exclusive_scan(thrust_support::policy(stream.get()), source.begin(), source.end(), output.begin(), T{42},
                           hip_async::StatefulOperator<T>{T{2}});
    thrust_support::synchronize(stream.get(), "HIP async exclusive stateful synchronize");
#endif
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> async_sort(const std::vector<T> &input) {
    thrust::device_vector<T> values(input.begin(), input.end());
    thrust_support::StreamResource stream("async sort stream create");
    thrust::sort(thrust_support::policy(stream.get()), values.begin(), values.end());
    thrust_support::synchronize(stream.get(), "async sort synchronize");
    return thrust_support::host(values);
  }

  template <class T> std::vector<T> async_future_chain(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> first(input.size());
    thrust::device_vector<T> output(input.size());
    thrust_support::StreamResource stream("async future-chain stream create");
    thrust::transform(thrust_support::policy(stream.get()), source.begin(), source.end(), first.begin(), support::Affine<T>{});
    // The second operation is the continuation.  Stream ordering is the
    // device equivalent of a future continuation and does not require a host
    // round-trip between stages.
    thrust::transform(thrust_support::policy(stream.get()), first.begin(), first.end(), output.begin(), support::Square<T>{});
    thrust_support::synchronize(stream.get(), "async future-chain synchronize");
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> async_event_chain(const std::vector<T> &input) {
    if (input.empty()) return {};
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> left(input.size());
    thrust::device_vector<T> right(input.size());
    thrust::device_vector<T> output(input.size());
    thrust_support::StreamResource left_stream("async event left stream create");
    thrust_support::StreamResource right_stream("async event right stream create");
    thrust_support::StreamResource join_stream("async event join stream create");
    thrust_support::EventResource left_ready("async event left create");
    thrust_support::EventResource right_ready("async event right create");
    thrust::transform(thrust_support::policy(left_stream.get()), source.begin(), source.end(), left.begin(), support::Affine<T>{});
    thrust::transform(thrust_support::policy(right_stream.get()), source.begin(), source.end(), right.begin(), support::Square<T>{});
    thrust_support::record(left_ready.get(), left_stream.get(), "async event left record");
    thrust_support::record(right_ready.get(), right_stream.get(), "async event right record");
    thrust_support::wait_event(join_stream.get(), left_ready.get(), "async event left wait");
    thrust_support::wait_event(join_stream.get(), right_ready.get(), "async event right wait");
    thrust::transform(thrust_support::policy(join_stream.get()), left.begin(), left.end(), right.begin(), output.begin(),
                      support::Combine<T>{});
    thrust_support::synchronize(join_stream.get(), "async event join synchronize");
    return thrust_support::host(output);
  }

  template <class T> std::vector<T> async_interleaved_streams(const std::vector<T> &input) {
    std::vector<T> even;
    std::vector<T> odd;
    even.reserve((input.size() + 1) / 2);
    odd.reserve(input.size() / 2);
    for (size_t index = 0; index < input.size(); ++index)
      (index % 2 == 0 ? even : odd).push_back(input[index]);

    thrust::device_vector<T> even_device(even.begin(), even.end());
    thrust::device_vector<T> odd_device(odd.begin(), odd.end());
    thrust_support::StreamResource even_stream("async interleaved even stream create");
    thrust_support::StreamResource odd_stream("async interleaved odd stream create");
    thrust::transform(thrust_support::policy(even_stream.get()), even_device.begin(), even_device.end(), even_device.begin(),
                      support::Affine<T>{});
    thrust::transform(thrust_support::policy(odd_stream.get()), odd_device.begin(), odd_device.end(), odd_device.begin(),
                      support::Square<T>{});
    thrust_support::synchronize(even_stream.get(), "async interleaved even synchronize");
    thrust_support::synchronize(odd_stream.get(), "async interleaved odd synchronize");
    const auto transformed_even = thrust_support::host(even_device);
    const auto transformed_odd = thrust_support::host(odd_device);
    std::vector<T> output(input.size());
    size_t even_index = 0;
    size_t odd_index = 0;
    for (size_t index = 0; index < output.size(); ++index)
      output[index] = index % 2 == 0 ? transformed_even[even_index++] : transformed_odd[odd_index++];
    return output;
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

template <class T> struct TupleWidePairValue {
  template <class Tuple> __host__ __device__ int64_t operator()(const Tuple &value) const {
    return edge::WidePairValue<T>{}(thrust::get<0>(value), thrust::get<1>(value));
  }
};

#if defined(__HIPCC__)
template <class T, uint32_t BlockSize> __global__ void block_scan_kernel(const T *input, T *output, size_t size) {
  using Scan = rocprim::block_scan<T, BlockSize>;
  __shared__ typename Scan::storage_type storage;
  const auto index = static_cast<size_t>(blockIdx.x) * BlockSize + threadIdx.x;
  T value = index < size ? input[index] : T{};
  Scan().inclusive_scan(value, value, storage);
  if (index < size) output[index] = value;
}
#else
template <class T, uint32_t BlockSize> __global__ void block_scan_kernel(const T *input, T *output, size_t size) {
  using Scan = cub::BlockScan<T, BlockSize>;
  __shared__ typename Scan::TempStorage storage;
  const auto index = static_cast<size_t>(blockIdx.x) * BlockSize + threadIdx.x;
  T value = index < size ? input[index] : T{};
  Scan(storage).InclusiveSum(value, value);
  if (index < size) output[index] = value;
}
#endif
} // namespace thrust_edge_detail

class ThrustEdgeCases {
public:
  template <class T> bool supports_composed_iterator_reduce() const { return true; }

  template <class T> bool supports_biased_index_64() const { return true; }

  template <class T> bool supports_segments_singleton() const { return true; }

  template <class T> bool supports_segments_giant() const { return true; }

  template <class T> bool supports_segments_boundary() const { return true; }

  template <class T> bool supports_inplace_scan_canary() const { return true; }

  template <class T> bool supports_stable_sort_payload() const { return true; }

  template <class T> bool supports_dependency_diamond() const { return true; }

  template <class T> bool supports_numeric_edge_roundtrip() const { return true; }

  template <class T> bool supports_primitive_block_scan() const { return true; }

  template <class T> bool supports_runtime_error_recover() const { return true; }

  // These two probes are the semantic form of Thrust's
  // Test*WithBigIndexes fixtures.  They intentionally use counting
  // iterators, so the test does not allocate a 2^33-element buffer; the
  // vendor implementation must still walk the complete 64-bit range and
  // update one device-side flag.
  void for_each_big_indexes(uint32_t magnitude) {
    if (magnitude >= 63) throw std::invalid_argument("big-index magnitude must be below 63");
    const auto begin = thrust::make_counting_iterator<uint64_t>(0);
    const auto end = begin + (1ULL << magnitude);
    if (end - begin != (1LL << magnitude)) throw std::runtime_error("for-each big-index distance mismatch");
    auto flag = thrust::device_malloc<bool>(1);
    *flag = false;
    thrust::for_each(thrust::device, begin, end,
                     thrust_edge_detail::SetExpectedFlagForEach{(1ULL << magnitude) - 1, thrust::raw_pointer_cast(flag)});
    const bool observed = *flag;
    thrust::device_free(flag);
    if (!observed) throw std::runtime_error("for-each big-index terminal value was not visited");
  }

  void adjacent_difference_big_indexes(uint32_t magnitude) {
    if (magnitude >= 63) throw std::invalid_argument("big-index magnitude must be below 63");
    const auto begin = thrust::make_counting_iterator<int64_t>(1);
    const auto end = begin + (1LL << magnitude);
    if (end - begin != (1LL << magnitude)) throw std::runtime_error("adjacent-difference big-index distance mismatch");
    auto flag = thrust::device_malloc<bool>(1);
    *flag = true;
    thrust::adjacent_difference(thrust::device, begin, end, thrust_edge_detail::DetectWrongDifference{thrust::raw_pointer_cast(flag)});
    const bool observed = *flag;
    thrust::device_free(flag);
    if (!observed) throw std::runtime_error("adjacent-difference big-index produced a wrong difference");
  }

  template <class T> int64_t composed_iterator_reduce(size_t size) {
    const auto indices = thrust::make_counting_iterator<size_t>(0);
    const auto source = thrust::make_transform_iterator(indices, edge::ValueAt<T>{});
    const auto reverse_indices = thrust::make_transform_iterator(indices, edge::ReverseIndex{size});
    const auto reverse = thrust::make_permutation_iterator(source, reverse_indices);
    const auto zipped = thrust::make_zip_iterator(thrust::make_tuple(reverse, source));
    return thrust::transform_reduce(zipped, zipped + size, thrust_edge_detail::TupleWidePairValue<T>{}, int64_t{}, thrust::plus<int64_t>{});
  }

  template <class T> uint64_t biased_index_checksum(uint64_t base, size_t size) {
    const auto begin = thrust::make_counting_iterator<uint64_t>(base);
    return thrust::transform_reduce(begin, begin + size, edge::BiasedHash<T>{}, uint64_t{}, edge::Xor64{});
  }

  template <class T> std::vector<T> segmented_inclusive_scan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
    const thrust::device_vector<int32_t> device_keys(keys.begin(), keys.end());
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::inclusive_scan_by_key(device_keys.begin(), device_keys.end(), source.begin(), output.begin());
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> inplace_inclusive_scan(const std::vector<T> &storage, size_t offset, size_t count) {
    thrust::device_vector<T> output(storage.begin(), storage.end());
    thrust::inclusive_scan(output.begin() + static_cast<ptrdiff_t>(offset), output.begin() + static_cast<ptrdiff_t>(offset + count),
                           output.begin() + static_cast<ptrdiff_t>(offset));
    return thrust_edge_detail::host(output);
  }

  template <class T> support::KeyValues<T> stable_sort_payload(const std::vector<T> &keys, const std::vector<int32_t> &payload) {
    thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    thrust::device_vector<int32_t> device_payload(payload.begin(), payload.end());
    thrust::stable_sort_by_key(device_keys.begin(), device_keys.end(), device_payload.begin());
    return {thrust_edge_detail::host(device_keys), thrust_edge_detail::host(device_payload)};
  }

  template <class T> std::vector<T> dependency_diamond(const std::vector<T> &input) {
    if (input.empty()) return {};
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> left(input.size()), right(input.size()), output(input.size());
    thrust_edge_detail::StreamResource stream_left("stream create left");
    thrust_edge_detail::StreamResource stream_right("stream create right");
    thrust_edge_detail::StreamResource stream_join("stream create join");
    thrust_edge_detail::EventResource left_ready("event create left");
    thrust_edge_detail::EventResource right_ready("event create right");
#if defined(__HIPCC__)
    thrust::transform(thrust_edge_detail::policy(stream_left.get()), source.begin(), source.end(), left.begin(), support::Affine<T>{});
    thrust::transform(thrust_edge_detail::policy(stream_right.get()), source.begin(), source.end(), right.begin(), support::Square<T>{});
    thrust_support::record(left_ready.get(), stream_left.get(), "hipEventRecord left");
    thrust_support::record(right_ready.get(), stream_right.get(), "hipEventRecord right");
    thrust_support::wait_event(stream_join.get(), left_ready.get(), "hipStreamWaitEvent left");
    thrust_support::wait_event(stream_join.get(), right_ready.get(), "hipStreamWaitEvent right");
    thrust::transform(thrust_edge_detail::policy(stream_join.get()), left.begin(), left.end(), right.begin(), output.begin(),
                      support::Combine<T>{});
    thrust_support::synchronize(stream_join.get(), "hipStreamSynchronize join");
#else
    thrust::transform(thrust_edge_detail::policy(stream_left.get()), source.begin(), source.end(), left.begin(), support::Affine<T>{});
    thrust::transform(thrust_edge_detail::policy(stream_right.get()), source.begin(), source.end(), right.begin(), support::Square<T>{});
    thrust_support::record(left_ready.get(), stream_left.get(), "cudaEventRecord left");
    thrust_support::record(right_ready.get(), stream_right.get(), "cudaEventRecord right");
    thrust_support::wait_event(stream_join.get(), left_ready.get(), "cudaStreamWaitEvent left");
    thrust_support::wait_event(stream_join.get(), right_ready.get(), "cudaStreamWaitEvent right");
    thrust::transform(thrust_edge_detail::policy(stream_join.get()), left.begin(), left.end(), right.begin(), output.begin(),
                      support::Combine<T>{});
    thrust_support::synchronize(stream_join.get(), "cudaStreamSynchronize join");
#endif
    left_ready.close("event destroy left");
    right_ready.close("event destroy right");
    stream_left.close("stream destroy left");
    stream_right.close("stream destroy right");
    stream_join.close("stream destroy join");
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> numeric_edge_roundtrip(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform(source.begin(), source.end(), output.begin(), edge::Identity<T>{});
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> primitive_block_scan(const std::vector<T> &input, size_t block_size) {
    if (block_size != 32) SKIP("primitive block scan currently requires a block size of 32");
    if (input.empty()) return {};
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    constexpr uint32_t block = 32;
    const auto grid = static_cast<uint32_t>((input.size() + block - 1) / block);
#if defined(__HIPCC__)
    hipLaunchKernelGGL((thrust_edge_detail::block_scan_kernel<T, block>), dim3(grid), dim3(block), 0, 0,
                       thrust::raw_pointer_cast(source.data()), thrust::raw_pointer_cast(output.data()), input.size());
    thrust_edge_detail::check(hipGetLastError(), "rocPRIM block scan launch");
    thrust_edge_detail::check(hipDeviceSynchronize(), "rocPRIM block scan synchronize");
#else
    thrust_edge_detail::block_scan_kernel<T, block>
        <<<grid, block>>>(thrust::raw_pointer_cast(source.data()), thrust::raw_pointer_cast(output.data()), input.size());
    thrust_edge_detail::check(cudaGetLastError(), "CUB block scan launch");
    thrust_edge_detail::check(cudaDeviceSynchronize(), "CUB block scan synchronize");
#endif
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> runtime_error_recover(const std::vector<T> &input) {
#if defined(__HIPCC__)
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

#if defined(UNIFIED_API_THRUST_DEVICE_FUNCTIONS)

namespace unified::gpu_calls {
  #if defined(__HIPCC__)
inline constexpr auto backend = "hip";
inline constexpr auto success = hipSuccess;
inline auto last_error() { return hipGetLastError(); }
inline auto error_string(hipError_t status) { return hipGetErrorString(status); }
  #else
inline constexpr auto backend = "cuda";
inline constexpr auto success = cudaSuccess;
inline auto last_error() { return cudaGetLastError(); }
inline auto error_string(cudaError_t status) { return cudaGetErrorString(status); }
  #endif
// Focused ports of GPUOcelot's direct/indirect function-call checks.
// Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech.
// SPDX-License-Identifier: BSD-3-Clause; see licenses/gpuocelot.txt.
__device__ __noinline__ float square(float value) { return value * value; }
__device__ __noinline__ float twice(float value) { return value * 2; }
__device__ __noinline__ float thrice(float value) { return value * 3; }
__device__ __noinline__ float four_times(float value) { return value * 4; }
__device__ __noinline__ float five_times(float value) { return value * 5; }

template <bool Indirect> __global__ void apply_device_function(float *values, size_t size) {
  const size_t index = threadIdx.x + static_cast<size_t>(blockIdx.x) * blockDim.x;
  if (index >= size) return;
  const float input = values[index + 1];
  if constexpr (Indirect) {
    float (*function)(float) = nullptr;
    switch (index & 3) {
      case 0: function = &twice; break;
      case 1: function = &thrice; break;
      case 2: function = &four_times; break;
      default: function = &five_times; break;
    }
    values[index + 1] = function(input);
  } else {
    values[index + 1] = square(input);
  }
}

template <bool Indirect> void device_function() {
  constexpr size_t size = 257;
  constexpr float canary = -12345.0f;
  std::vector<float> values(size + 2, canary);
  for (size_t index = 0; index < size; ++index)
    values[index + 1] = (static_cast<float>(index) - 128.0f) * 0.25f;
  thrust::device_vector<float> device(values);
  apply_device_function<Indirect><<<3, 128>>>(thrust::raw_pointer_cast(device.data()), size);
  if (const auto error = last_error(); error != success)
    throw std::runtime_error(std::string("device function launch: ") + error_string(error));
  thrust::copy(device.begin(), device.end(), values.begin());
  if (values.front() != canary || values.back() != canary) throw std::runtime_error("device function overwrote a boundary canary");
  for (size_t index = 0; index < size; ++index) {
    const float input = (static_cast<float>(index) - 128.0f) * 0.25f;
    const float expected = Indirect ? input * static_cast<float>(2 + (index & 3)) : input * input;
    if (values[index + 1] != expected) throw std::runtime_error("device function result mismatch at lane " + std::to_string(index));
  }
}
} // namespace unified::gpu_calls

#endif

#if defined(__HIPCC__) && defined(UNIFIED_API_ROCTHRUST)
// Focused rocThrust semantic probes derived from the historical gfx1036 red
// cases. These are kept separate from the portable algorithm cases:
// the operations below are rocThrust overload/iterator forms, rather than
// another spelling of the already-covered C++ algorithm semantics.

  #if defined(__CUDACC__) || defined(__HIPCC__)
    #define UNIFIED_ROCTHRUST_HOST_DEVICE __host__ __device__
  #else
    #define UNIFIED_ROCTHRUST_HOST_DEVICE
  #endif

namespace unified::rocthrust_support {
template <class T> using Pair = thrust::pair<T, int32_t>;

template <class T> struct PairLess {
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const Pair<T> &left, const Pair<T> &right) const {
    return left.first < right.first || (!(right.first < left.first) && left.second < right.second);
  }
};

// rocThrust's key/value set-operation overloads compare keys only.  The value
// iterator supplies payloads associated with the selected key; it is not part
// of set membership or equivalence.
template <class T> struct PairKeyLess {
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const Pair<T> &left, const Pair<T> &right) const { return left.first < right.first; }
};

template <class T> struct TupleLess {
  template <class LeftTuple, class RightTuple>
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const LeftTuple &left, const RightTuple &right) const {
    const auto left_key = thrust::get<0>(left);
    const auto right_key = thrust::get<0>(right);
    const auto left_value = thrust::get<1>(left);
    const auto right_value = thrust::get<1>(right);
    return left_key < right_key || (!(right_key < left_key) && left_value < right_value);
  }
};

template <class T> struct TupleCode {
  template <class Tuple> UNIFIED_ROCTHRUST_HOST_DEVICE int64_t operator()(const Tuple &value) const {
    return static_cast<int64_t>(thrust::get<0>(value)) * 1000 + static_cast<int64_t>(thrust::get<1>(value));
  }
};

template <class T> struct PairPlus {
  UNIFIED_ROCTHRUST_HOST_DEVICE Pair<T> operator()(const Pair<T> &left, const Pair<T> &right) const {
    return {left.first + right.first, left.second + right.second};
  }
};

template <class T> struct TuplePlus {
  using Tuple = thrust::tuple<T, int32_t>;

  UNIFIED_ROCTHRUST_HOST_DEVICE Tuple operator()(Tuple left, Tuple right) const {
    return thrust::make_tuple(thrust::get<0>(left) + thrust::get<0>(right), thrust::get<1>(left) + thrust::get<1>(right));
  }
};

template <class T> struct Square {
  UNIFIED_ROCTHRUST_HOST_DEVICE T operator()(T value) const { return value * value; }
};

template <class T> struct Negate {
  UNIFIED_ROCTHRUST_HOST_DEVICE T operator()(T value) const { return -value; }
};

enum class SetKind { union_set, intersection, difference, symmetric_difference };

template <class T> struct KeyValues {
  std::vector<T> keys;
  std::vector<int32_t> values;
  friend bool operator==(const KeyValues &a, const KeyValues &b) { return a.keys == b.keys && a.values == b.values; }
};

template <class T> inline std::vector<T> ascending_keys() {
  return {T{-5}, T{-5}, T{-3}, T{-1}, T{-1}, T{0}, T{2}, T{2}, T{2}, T{4}, T{7}};
}

template <class T> inline std::vector<T> descending_keys() {
  auto result = ascending_keys<T>();
  std::reverse(result.begin(), result.end());
  return result;
}

inline std::vector<int32_t> payload(size_t count, int32_t base) {
  std::vector<int32_t> result(count);
  for (size_t index = 0; index < count; ++index)
    result[index] = base + static_cast<int32_t>(index);
  return result;
}

template <class T> inline KeyValues<T> descending_key_values(const std::vector<T> &keys, int32_t base) {
  return {keys, payload(keys.size(), base)};
}

template <class T>
inline KeyValues<T> reference_set_by_key(const KeyValues<T> &left, const KeyValues<T> &right, SetKind kind, bool descending) {
  KeyValues<T> result;
  const auto less = [descending](const T &a, const T &b) { return descending ? b < a : a < b; };
  size_t a = 0;
  size_t b = 0;
  while (a < left.keys.size() || b < right.keys.size()) {
    if (a == left.keys.size()) {
      if (kind == SetKind::union_set || kind == SetKind::symmetric_difference) {
        result.keys.push_back(right.keys[b]);
        result.values.push_back(right.values[b]);
      }
      ++b;
    } else if (b == right.keys.size()) {
      if (kind != SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
    } else if (less(left.keys[a], right.keys[b])) {
      if (kind != SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
    } else if (less(right.keys[b], left.keys[a])) {
      if (kind == SetKind::union_set || kind == SetKind::symmetric_difference) {
        result.keys.push_back(right.keys[b]);
        result.values.push_back(right.values[b]);
      }
      ++b;
    } else {
      if (kind == SetKind::union_set || kind == SetKind::intersection) {
        result.keys.push_back(left.keys[a]);
        result.values.push_back(left.values[a]);
      }
      ++a;
      ++b;
    }
  }
  return result;
}

template <class T>
inline KeyValues<T> thrust_set_by_key(const KeyValues<T> &left, const KeyValues<T> &right, SetKind kind, bool descending) {
  const thrust::device_vector<T> left_keys(left.keys.begin(), left.keys.end());
  const thrust::device_vector<T> right_keys(right.keys.begin(), right.keys.end());
  const thrust::device_vector<int32_t> left_values(left.values.begin(), left.values.end());
  const thrust::device_vector<int32_t> right_values(right.values.begin(), right.values.end());
  thrust::device_vector<T> output_keys(left.keys.size() + right.keys.size());
  thrust::device_vector<int32_t> output_values(left.values.size() + right.values.size());

  thrust::pair<typename thrust::device_vector<T>::iterator, typename thrust::device_vector<int32_t>::iterator> ends;
  if (kind == SetKind::union_set) {
    ends = descending
               ? thrust::set_union_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                                          right_values.begin(), output_keys.begin(), output_values.begin(), algorithms::Greater<T>{})
               : thrust::set_union_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                                          right_values.begin(), output_keys.begin(), output_values.begin());
  } else if (kind == SetKind::intersection) {
    ends = descending
               ? thrust::set_intersection_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                                                 left_values.begin(), output_keys.begin(), output_values.begin(), algorithms::Greater<T>{})
               : thrust::set_intersection_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                                                 left_values.begin(), output_keys.begin(), output_values.begin());
  } else if (kind == SetKind::difference) {
    ends =
        descending
            ? thrust::set_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                                            right_values.begin(), output_keys.begin(), output_values.begin(), algorithms::Greater<T>{})
            : thrust::set_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(), left_values.begin(),
                                            right_values.begin(), output_keys.begin(), output_values.begin());
  } else {
    ends = descending ? thrust::set_symmetric_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                                                                left_values.begin(), right_values.begin(), output_keys.begin(),
                                                                output_values.begin(), algorithms::Greater<T>{})
                      : thrust::set_symmetric_difference_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                                                                left_values.begin(), right_values.begin(), output_keys.begin(),
                                                                output_values.begin());
  }
  output_keys.resize(static_cast<size_t>(ends.first - output_keys.begin()));
  output_values.resize(static_cast<size_t>(ends.second - output_values.begin()));
  return {std::vector<T>(output_keys.begin(), output_keys.end()), std::vector<int32_t>(output_values.begin(), output_values.end())};
}

template <class T> inline std::vector<Pair<T>> pair_values(const std::vector<T> &keys, int32_t base) {
  std::vector<Pair<T>> result;
  const auto values = payload(keys.size(), base);
  result.reserve(keys.size());
  for (size_t index = 0; index < keys.size(); ++index)
    result.emplace_back(keys[index], values[index]);
  return result;
}

template <class T> inline std::vector<Pair<T>> host_pairs(const thrust::device_vector<Pair<T>> &values, size_t count) {
  return {values.begin(), values.begin() + static_cast<ptrdiff_t>(count)};
}

template <class T>
inline std::vector<Pair<T>> thrust_set_pairs(const std::vector<Pair<T>> &left, const std::vector<Pair<T>> &right, SetKind kind) {
  const thrust::device_vector<Pair<T>> a(left.begin(), left.end());
  const thrust::device_vector<Pair<T>> b(right.begin(), right.end());
  thrust::device_vector<Pair<T>> output(left.size() + right.size());
  auto end = output.begin();
  if (kind == SetKind::union_set) end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::intersection)
    end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::difference)
    end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  else end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), PairKeyLess<T>{});
  return host_pairs(output, static_cast<size_t>(end - output.begin()));
}

template <class T> inline std::vector<Pair<T>> pair_expected(std::vector<Pair<T>> left, std::vector<Pair<T>> right, SetKind kind) {
  std::stable_sort(left.begin(), left.end(), PairKeyLess<T>{});
  std::stable_sort(right.begin(), right.end(), PairKeyLess<T>{});
  std::vector<Pair<T>> result(left.size() + right.size());
  auto end = result.begin();
  if (kind == SetKind::union_set)
    end = std::set_union(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::intersection)
    end = std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  else if (kind == SetKind::difference)
    end = std::set_difference(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  else end = std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), result.begin(), PairKeyLess<T>{});
  result.erase(end, result.end());
  return result;
}
} // namespace unified::rocthrust_support

#endif
