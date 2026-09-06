#ifndef UNIFIED_SUITE_ADAPTERS_THRUST_ALGORITHMS_HPP
#define UNIFIED_SUITE_ADAPTERS_THRUST_ALGORITHMS_HPP

#include "unified_suite/algorithms.hpp"

#include <thrust/adjacent_difference.h>
#include <thrust/binary_search.h>
#include <thrust/copy.h>
#include <thrust/count.h>
#include <thrust/device_vector.h>
#include <thrust/equal.h>
#include <thrust/execution_policy.h>
#include <thrust/extrema.h>
#include <thrust/fill.h>
#include <thrust/find.h>
#include <thrust/for_each.h>
#include <thrust/gather.h>
#include <thrust/generate.h>
#include <thrust/inner_product.h>
#include <thrust/logical.h>
#include <thrust/merge.h>
#include <thrust/mismatch.h>
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
#include <thrust/uninitialized_copy.h>
#include <thrust/uninitialized_fill.h>
#include <thrust/unique.h>

#if defined(__HIPCC__)
#include <hip/hip_runtime.h>
#else
#include <cuda_runtime_api.h>
#endif

#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <vector>

namespace unified_suite::adapters {

namespace thrust_algorithm_detail {

template <class T>
__global__ void legacy_device_dispatch_kernel(const T *first, T *output, std::size_t size) {
  // The old vendor suites exercise Thrust from inside a device-launched
  // kernel. This is intentionally distinct from the ordinary host dispatch
  // used by the portable catalog: it catches nested execution/iterator ABI
  // regressions while remaining a small, deterministic fixture.
  thrust::adjacent_difference(thrust::device, first, first + size, output);
}

#if defined(__HIPCC__)
inline void check_legacy(hipError_t status, const char *where) {
  if (status != hipSuccess)
    throw std::runtime_error(std::string(where) + ": " + hipGetErrorString(status));
}
#else
inline void check_legacy(cudaError_t status, const char *where) {
  if (status != cudaSuccess)
    throw std::runtime_error(std::string(where) + ": " + cudaGetErrorString(status));
}
#endif

} // namespace thrust_algorithm_detail

class ThrustAlgorithms {
public:
  template <class T> bool supports(algorithms::Operation operation) const {
    return operation != algorithms::Operation::partial_sort &&
           operation != algorithms::Operation::nth_element &&
           operation != algorithms::Operation::inplace_merge && operation != algorithms::Operation::rotate &&
           operation != algorithms::Operation::rotate_copy && operation != algorithms::Operation::includes;
  }

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
    output.resize(static_cast<std::size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> copy_n(const std::vector<T> &input, std::size_t count) {
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

  template <class T> std::vector<T> uninitialized_fill(std::size_t size, T value) {
    thrust::device_vector<T> output(size);
    thrust::uninitialized_fill(output.begin(), output.end(), value);
    return host(output);
  }

  template <class T> std::vector<T> fill(std::size_t size, T value) {
    thrust::device_vector<T> output(size);
    thrust::fill(output.begin(), output.end(), value);
    return host(output);
  }

  template <class T> std::vector<T> generate(std::size_t size) {
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

  template <class T>
  std::vector<T> transform_binary(const std::vector<T> &left, const std::vector<T> &right) {
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
    thrust::replace_copy_if(source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{},
                            replacement);
    return host(output);
  }

  template <class T> std::vector<T> remove_if(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto end = thrust::remove_if(output.begin(), output.end(), algorithms::IsEven<T>{});
    output.resize(static_cast<std::size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> remove_copy_if(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    const auto end =
        thrust::remove_copy_if(source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    output.resize(static_cast<std::size_t>(end - output.begin()));
    return host(output);
  }

  template <class T>
  algorithms::TwoRanges<T> swap_ranges(const std::vector<T> &left, const std::vector<T> &right) {
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
    return thrust::transform_reduce(source.begin(), source.end(), algorithms::Square<T>{}, T{},
                                    algorithms::Plus<T>{});
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
    thrust::transform_inclusive_scan(source.begin(), source.end(), output.begin(), algorithms::Square<T>{},
                                     algorithms::Plus<T>{});
    return host(output);
  }

  template <class T> std::vector<T> transform_exclusive_scan(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform_exclusive_scan(source.begin(), source.end(), output.begin(), algorithms::Square<T>{},
                                     T{}, algorithms::Plus<T>{});
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

  template <class T>
  algorithms::KeyValues<T> sort_by_key(const std::vector<T> &keys, const std::vector<std::int32_t> &values) {
    thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    thrust::device_vector<std::int32_t> device_values(values.begin(), values.end());
    thrust::sort_by_key(device_keys.begin(), device_keys.end(), device_values.begin(), algorithms::Less<T>{});
    return {host(device_keys), host(device_values)};
  }

  template <class T>
  algorithms::KeyValues<T> stable_sort_by_key(const std::vector<T> &keys,
                                              const std::vector<std::int32_t> &values) {
    thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    thrust::device_vector<std::int32_t> device_values(values.begin(), values.end());
    thrust::stable_sort_by_key(device_keys.begin(), device_keys.end(), device_values.begin(),
                               algorithms::Less<T>{});
    return {host(device_keys), host(device_values)};
  }

  template <class T> std::vector<T> unique(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto end = thrust::unique(output.begin(), output.end());
    output.resize(static_cast<std::size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> unique_copy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    const auto end = thrust::unique_copy(source.begin(), source.end(), output.begin());
    output.resize(static_cast<std::size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::size_t count(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::count(source.begin(), source.end(), value));
  }

  template <class T> std::size_t count_if(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::count_if(source.begin(), source.end(), algorithms::IsEven<T>{}));
  }

  template <class T> std::size_t find_if(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::find_if(source.begin(), source.end(), algorithms::IsEven<T>{}) -
                                    source.begin());
  }

  template <class T> std::size_t find(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::find(source.begin(), source.end(), value) - source.begin());
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

  template <class T>
  algorithms::TwoPositions mismatch(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    const auto result = thrust::mismatch(a.begin(), a.end(), b.begin());
    return {static_cast<std::size_t>(result.first - a.begin()),
            static_cast<std::size_t>(result.second - b.begin())};
  }

  template <class T> std::size_t lower_bound(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::lower_bound(source.begin(), source.end(), value) -
                                    source.begin());
  }

  template <class T> std::size_t upper_bound(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::upper_bound(source.begin(), source.end(), value) -
                                    source.begin());
  }

  template <class T> bool binary_search(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::binary_search(source.begin(), source.end(), value);
  }

  template <class T> algorithms::TwoPositions equal_range(const std::vector<T> &input, T value) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const auto result = thrust::equal_range(source.begin(), source.end(), value);
    return {static_cast<std::size_t>(result.first - source.begin()),
            static_cast<std::size_t>(result.second - source.begin())};
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
    return {host(output), static_cast<std::size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::Partitioned<T> stable_partition(const std::vector<T> &input) {
    thrust::device_vector<T> output(input.begin(), input.end());
    const auto boundary = thrust::stable_partition(output.begin(), output.end(), algorithms::IsEven<T>{});
    return {host(output), static_cast<std::size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::TwoRanges<T> partition_copy(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> selected(input.size());
    thrust::device_vector<T> rejected(input.size());
    const auto ends = thrust::partition_copy(source.begin(), source.end(), selected.begin(), rejected.begin(),
                                             algorithms::IsEven<T>{});
    selected.resize(static_cast<std::size_t>(ends.first - selected.begin()));
    rejected.resize(static_cast<std::size_t>(ends.second - rejected.begin()));
    return {host(selected), host(rejected)};
  }

  template <class T> bool is_partitioned(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::is_partitioned(source.begin(), source.end(), algorithms::IsEven<T>{});
  }

  template <class T> std::size_t partition_point(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(
        thrust::partition_point(source.begin(), source.end(), algorithms::IsEven<T>{}) - source.begin());
  }

  template <class T> std::vector<T> adjacent_difference(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::adjacent_difference(source.begin(), source.end(), output.begin());
    return host(output);
  }

  template <class T> std::vector<T> legacy_device_dispatch(std::size_t size) {
    const auto input = algorithms::detail::values<T>(size, 1);
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(size);
    thrust_algorithm_detail::legacy_device_dispatch_kernel<<<1, 1>>>(
        thrust::raw_pointer_cast(source.data()), thrust::raw_pointer_cast(output.data()), size);
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
    output.resize(static_cast<std::size_t>(end - output.begin()));
    return host(output);
  }

  template <class T>
  std::vector<T> set_intersection(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(std::min(left.size(), right.size()));
    const auto end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<std::size_t>(end - output.begin()));
    return host(output);
  }

  template <class T> std::vector<T> set_difference(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size());
    const auto end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<std::size_t>(end - output.begin()));
    return host(output);
  }

  template <class T>
  std::vector<T> set_symmetric_difference(const std::vector<T> &left, const std::vector<T> &right) {
    const thrust::device_vector<T> a(left.begin(), left.end());
    const thrust::device_vector<T> b(right.begin(), right.end());
    thrust::device_vector<T> output(left.size() + right.size());
    const auto end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<std::size_t>(end - output.begin()));
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

  template <class T> std::size_t min_element(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::min_element(source.begin(), source.end()) - source.begin());
  }

  template <class T> std::size_t max_element(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::max_element(source.begin(), source.end()) - source.begin());
  }

  template <class T> algorithms::TwoPositions minmax_element(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const auto result = thrust::minmax_element(source.begin(), source.end());
    return {static_cast<std::size_t>(result.first - source.begin()),
            static_cast<std::size_t>(result.second - source.begin())};
  }

  template <class T> bool is_sorted(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return thrust::is_sorted(source.begin(), source.end());
  }

  template <class T> std::size_t is_sorted_until(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    return static_cast<std::size_t>(thrust::is_sorted_until(source.begin(), source.end()) - source.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &, std::size_t) {
    throw Unsupported("rotate is not a Thrust API");
  }

  template <class T> std::vector<T> rotate_copy(const std::vector<T> &, std::size_t) {
    throw Unsupported("rotate-copy is not a Thrust API");
  }

  template <class T> bool includes(const std::vector<T> &, const std::vector<T> &) {
    throw Unsupported("includes is not a Thrust API");
  }

  template <class T>
  std::vector<T> gather(const std::vector<T> &input, const std::vector<std::size_t> &indices) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const thrust::device_vector<std::size_t> map(indices.begin(), indices.end());
    thrust::device_vector<T> output(indices.size());
    thrust::gather(map.begin(), map.end(), source.begin(), output.begin());
    return host(output);
  }

  template <class T>
  std::vector<T> scatter(const std::vector<T> &input, const std::vector<std::size_t> &indices) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    const thrust::device_vector<std::size_t> map(indices.begin(), indices.end());
    thrust::device_vector<T> output(input.size());
    thrust::scatter(source.begin(), source.end(), map.begin(), output.begin());
    return host(output);
  }

  template <class T>
  algorithms::KeyValues<T> reduce_by_key(const std::vector<T> &keys,
                                         const std::vector<std::int32_t> &values) {
    const thrust::device_vector<T> source_keys(keys.begin(), keys.end());
    const thrust::device_vector<std::int32_t> source_values(values.begin(), values.end());
    thrust::device_vector<T> output_keys(keys.size());
    thrust::device_vector<std::int32_t> output_values(values.size());
    const auto ends = thrust::reduce_by_key(source_keys.begin(), source_keys.end(), source_values.begin(),
                                            output_keys.begin(), output_values.begin());
    output_keys.resize(static_cast<std::size_t>(ends.first - output_keys.begin()));
    output_values.resize(static_cast<std::size_t>(ends.second - output_values.begin()));
    return {host(output_keys), host(output_values)};
  }

  template <class T>
  algorithms::KeyValues<T> inclusive_scan_by_key(const std::vector<T> &keys,
                                                 const std::vector<std::int32_t> &values) {
    const thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    const thrust::device_vector<std::int32_t> source(values.begin(), values.end());
    thrust::device_vector<std::int32_t> output(values.size());
    thrust::inclusive_scan_by_key(device_keys.begin(), device_keys.end(), source.begin(), output.begin());
    return {keys, host(output)};
  }

  template <class T>
  algorithms::KeyValues<T> exclusive_scan_by_key(const std::vector<T> &keys,
                                                 const std::vector<std::int32_t> &values) {
    const thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    const thrust::device_vector<std::int32_t> source(values.begin(), values.end());
    thrust::device_vector<std::int32_t> output(values.size());
    thrust::exclusive_scan_by_key(device_keys.begin(), device_keys.end(), source.begin(), output.begin(), 0);
    return {keys, host(output)};
  }

  template <class T>
  algorithms::KeyValues<T> unique_by_key(const std::vector<T> &keys,
                                         const std::vector<std::int32_t> &values) {
    thrust::device_vector<T> output_keys(keys.begin(), keys.end());
    thrust::device_vector<std::int32_t> output_values(values.begin(), values.end());
    const auto ends = thrust::unique_by_key(output_keys.begin(), output_keys.end(), output_values.begin());
    output_keys.resize(static_cast<std::size_t>(ends.first - output_keys.begin()));
    output_values.resize(static_cast<std::size_t>(ends.second - output_values.begin()));
    return {host(output_keys), host(output_values)};
  }

  template <class T>
  algorithms::KeyValues<T> merge_by_key(const algorithms::KeyValues<T> &left,
                                        const algorithms::KeyValues<T> &right) {
    const thrust::device_vector<T> left_keys(left.keys.begin(), left.keys.end());
    const thrust::device_vector<T> right_keys(right.keys.begin(), right.keys.end());
    const thrust::device_vector<std::int32_t> left_values(left.values.begin(), left.values.end());
    const thrust::device_vector<std::int32_t> right_values(right.values.begin(), right.values.end());
    thrust::device_vector<T> output_keys(left.keys.size() + right.keys.size());
    thrust::device_vector<std::int32_t> output_values(left.values.size() + right.values.size());
    thrust::merge_by_key(left_keys.begin(), left_keys.end(), right_keys.begin(), right_keys.end(),
                         left_values.begin(), right_values.begin(), output_keys.begin(),
                         output_values.begin());
    return {host(output_keys), host(output_values)};
  }

  template <class T> std::vector<T> partial_sort(const std::vector<T> &, std::size_t) {
    throw Unsupported("partial-sort is not a Thrust API");
  }

  template <class T> std::vector<T> nth_element(const std::vector<T> &, std::size_t) {
    throw Unsupported("nth-element is not a Thrust API");
  }

  template <class T> std::vector<T> inplace_merge(const std::vector<T> &, std::size_t) {
    throw Unsupported("inplace-merge is not a Thrust API");
  }

private:
  template <class T> static std::vector<T> host(const thrust::device_vector<T> &input) {
    return std::vector<T>(input.begin(), input.end());
  }
};

} // namespace unified_suite::adapters

#endif
