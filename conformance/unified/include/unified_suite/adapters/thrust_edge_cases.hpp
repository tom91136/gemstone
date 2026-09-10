#ifndef UNIFIED_SUITE_ADAPTERS_THRUST_EDGE_CASES_HPP
#define UNIFIED_SUITE_ADAPTERS_THRUST_EDGE_CASES_HPP

#include "unified_suite/edge_cases.hpp"

#include <thrust/device_vector.h>
#include <thrust/copy.h>
#include <thrust/device_malloc.h>
#include <thrust/device_free.h>
#include <thrust/adjacent_difference.h>
#include <thrust/execution_policy.h>
#include <thrust/functional.h>
#include <thrust/iterator/counting_iterator.h>
#include <thrust/iterator/permutation_iterator.h>
#include <thrust/iterator/transform_iterator.h>
#include <thrust/iterator/zip_iterator.h>
#include <thrust/reduce.h>
#include <thrust/scan.h>
#include <thrust/sort.h>
#include <thrust/transform.h>

#if defined(__HIPCC__)
#include <hip/hip_runtime.h>
#include <rocprim/block/block_scan.hpp>
#include <thrust/system/hip/execution_policy.h>
#include <thrust/tuple.h>
#else
#include <cub/block/block_scan.cuh>
#include <cuda/std/tuple>
#include <cuda_runtime_api.h>
#include <thrust/system/cuda/execution_policy.h>
#endif

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <iterator>
#include <limits>
#include <stdexcept>
#include <string>
#include <vector>

namespace unified_suite::adapters {

namespace thrust_edge_detail {

struct SetExpectedFlagForEach {
  unsigned long long expected;
  bool *flag;

  __device__ void operator()(unsigned long long value) const {
    if (value == expected)
      *flag = true;
  }
};

struct DetectWrongDifference {
  using value_type = long long;
  using difference_type = std::ptrdiff_t;
  using pointer = DetectWrongDifference *;
  using reference = DetectWrongDifference;
  using iterator_category = std::output_iterator_tag;

  bool *flag;

  __host__ __device__ DetectWrongDifference operator++() const { return *this; }
  __host__ __device__ DetectWrongDifference operator*() const { return *this; }
  template <class Difference>
  __host__ __device__ DetectWrongDifference operator+(Difference) const { return *this; }
  template <class Index>
  __host__ __device__ DetectWrongDifference operator[](Index) const { return *this; }

  __device__ void operator=(long long difference) const {
    if (difference != 1)
      *flag = false;
  }
};

template <class T> struct TupleWidePairValue {
  template <class Tuple> __host__ __device__ std::int64_t operator()(const Tuple &value) const {
#if defined(__HIPCC__)
    return edge_cases::WidePairValue<T>{}(thrust::get<0>(value), thrust::get<1>(value));
#else
    return edge_cases::WidePairValue<T>{}(cuda::std::get<0>(value), cuda::std::get<1>(value));
#endif
  }
};

#if defined(__HIPCC__)
using Stream = hipStream_t;
using Event = hipEvent_t;
inline void check(hipError_t status, const char *where) {
  if (status != hipSuccess)
    throw std::runtime_error(std::string(where) + ": " + hipGetErrorString(status));
}
inline auto policy(Stream stream) { return thrust::hip::par.on(stream); }
class StreamResource {
public:
  explicit StreamResource(const char *where) { check(hipStreamCreate(&value_), where); }
  StreamResource(const StreamResource &) = delete;
  StreamResource &operator=(const StreamResource &) = delete;
  ~StreamResource() {
    if (value_)
      (void)hipStreamDestroy(value_);
  }
  Stream get() const { return value_; }
  void close(const char *where) {
    check(hipStreamDestroy(value_), where);
    value_ = nullptr;
  }

private:
  Stream value_{};
};
class EventResource {
public:
  explicit EventResource(const char *where) {
    check(hipEventCreateWithFlags(&value_, hipEventDisableTiming), where);
  }
  EventResource(const EventResource &) = delete;
  EventResource &operator=(const EventResource &) = delete;
  ~EventResource() {
    if (value_)
      (void)hipEventDestroy(value_);
  }
  Event get() const { return value_; }
  void close(const char *where) {
    check(hipEventDestroy(value_), where);
    value_ = nullptr;
  }

private:
  Event value_{};
};
template <class T, unsigned BlockSize>
__global__ void block_scan_kernel(const T *input, T *output, std::size_t size) {
  using Scan = rocprim::block_scan<T, BlockSize>;
  __shared__ typename Scan::storage_type storage;
  const auto index = static_cast<std::size_t>(blockIdx.x) * BlockSize + threadIdx.x;
  T value = index < size ? input[index] : T{};
  Scan().inclusive_scan(value, value, storage);
  if (index < size)
    output[index] = value;
}
#else
using Stream = cudaStream_t;
using Event = cudaEvent_t;
inline void check(cudaError_t status, const char *where) {
  if (status != cudaSuccess)
    throw std::runtime_error(std::string(where) + ": " + cudaGetErrorString(status));
}
inline auto policy(Stream stream) { return thrust::cuda::par.on(stream); }
class StreamResource {
public:
  explicit StreamResource(const char *where) { check(cudaStreamCreate(&value_), where); }
  StreamResource(const StreamResource &) = delete;
  StreamResource &operator=(const StreamResource &) = delete;
  ~StreamResource() {
    if (value_)
      (void)cudaStreamDestroy(value_);
  }
  Stream get() const { return value_; }
  void close(const char *where) {
    check(cudaStreamDestroy(value_), where);
    value_ = nullptr;
  }

private:
  Stream value_{};
};
class EventResource {
public:
  explicit EventResource(const char *where) {
    check(cudaEventCreateWithFlags(&value_, cudaEventDisableTiming), where);
  }
  EventResource(const EventResource &) = delete;
  EventResource &operator=(const EventResource &) = delete;
  ~EventResource() {
    if (value_)
      (void)cudaEventDestroy(value_);
  }
  Event get() const { return value_; }
  void close(const char *where) {
    check(cudaEventDestroy(value_), where);
    value_ = nullptr;
  }

private:
  Event value_{};
};
template <class T, unsigned BlockSize>
__global__ void block_scan_kernel(const T *input, T *output, std::size_t size) {
  using Scan = cub::BlockScan<T, BlockSize>;
  __shared__ typename Scan::TempStorage storage;
  const auto index = static_cast<std::size_t>(blockIdx.x) * BlockSize + threadIdx.x;
  T value = index < size ? input[index] : T{};
  Scan(storage).InclusiveSum(value, value);
  if (index < size)
    output[index] = value;
}
#endif

template <class T> std::vector<T> host(const thrust::device_vector<T> &input) {
  std::vector<T> result(input.size());
  if (!input.empty()) thrust::copy(input.begin(), input.end(), result.data());
  return result;
}

} // namespace thrust_edge_detail

class ThrustEdgeCases {
public:
  template <class T> bool supports(edge_cases::Operation) const { return true; }

  // These two probes are the semantic form of Thrust's
  // Test*WithBigIndexes fixtures.  They intentionally use counting
  // iterators, so the test does not allocate a 2^33-element buffer; the
  // vendor implementation must still walk the complete 64-bit range and
  // update one device-side flag.
  void for_each_big_indexes(unsigned magnitude) {
    if (magnitude >= 63)
      throw std::invalid_argument("big-index magnitude must be below 63");
    const auto begin = thrust::make_counting_iterator<unsigned long long>(0);
    const auto end = begin + (1ULL << magnitude);
    if (end - begin != (1LL << magnitude))
      throw std::runtime_error("for-each big-index distance mismatch");
    auto flag = thrust::device_malloc<bool>(1);
    *flag = false;
    thrust::for_each(thrust::device, begin, end,
                     thrust_edge_detail::SetExpectedFlagForEach{(1ULL << magnitude) - 1,
                                                                  thrust::raw_pointer_cast(flag)});
    const bool observed = *flag;
    thrust::device_free(flag);
    if (!observed)
      throw std::runtime_error("for-each big-index terminal value was not visited");
  }

  void adjacent_difference_big_indexes(unsigned magnitude) {
    if (magnitude >= 63)
      throw std::invalid_argument("big-index magnitude must be below 63");
    const auto begin = thrust::make_counting_iterator<long long>(1);
    const auto end = begin + (1LL << magnitude);
    if (end - begin != (1LL << magnitude))
      throw std::runtime_error("adjacent-difference big-index distance mismatch");
    auto flag = thrust::device_malloc<bool>(1);
    *flag = true;
    thrust::adjacent_difference(thrust::device, begin, end,
                                thrust_edge_detail::DetectWrongDifference{thrust::raw_pointer_cast(flag)});
    const bool observed = *flag;
    thrust::device_free(flag);
    if (!observed)
      throw std::runtime_error("adjacent-difference big-index produced a wrong difference");
  }

  template <class T> std::int64_t composed_iterator_reduce(std::size_t size) {
    const auto indices = thrust::make_counting_iterator<std::size_t>(0);
    const auto source = thrust::make_transform_iterator(indices, edge_cases::ValueAt<T>{});
    const auto reverse_indices = thrust::make_transform_iterator(indices, edge_cases::ReverseIndex{size});
    const auto reverse = thrust::make_permutation_iterator(source, reverse_indices);
#if defined(__HIPCC__)
    const auto zipped = thrust::make_zip_iterator(thrust::make_tuple(reverse, source));
#else
    const auto zipped = thrust::make_zip_iterator(reverse, source);
#endif
    return thrust::transform_reduce(zipped, zipped + size, thrust_edge_detail::TupleWidePairValue<T>{},
                                    std::int64_t{}, thrust::plus<std::int64_t>{});
  }

  template <class T> std::uint64_t biased_index_checksum(std::uint64_t base, std::size_t size) {
    const auto begin = thrust::make_counting_iterator<std::uint64_t>(base);
    return thrust::transform_reduce(begin, begin + size, edge_cases::BiasedHash<T>{}, std::uint64_t{},
                                    edge_cases::Xor64{});
  }

  template <class T>
  std::vector<T> segmented_inclusive_scan(const std::vector<std::int32_t> &keys,
                                          const std::vector<T> &input) {
    const thrust::device_vector<std::int32_t> device_keys(keys.begin(), keys.end());
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::inclusive_scan_by_key(device_keys.begin(), device_keys.end(), source.begin(), output.begin());
    return thrust_edge_detail::host(output);
  }

  template <class T>
  std::vector<T> inplace_inclusive_scan(const std::vector<T> &storage, std::size_t offset,
                                        std::size_t count) {
    thrust::device_vector<T> output(storage.begin(), storage.end());
    thrust::inclusive_scan(output.begin() + static_cast<std::ptrdiff_t>(offset),
                           output.begin() + static_cast<std::ptrdiff_t>(offset + count),
                           output.begin() + static_cast<std::ptrdiff_t>(offset));
    return thrust_edge_detail::host(output);
  }

  template <class T>
  algorithms::KeyValues<T> stable_sort_payload(const std::vector<T> &keys,
                                               const std::vector<std::int32_t> &payload) {
    thrust::device_vector<T> device_keys(keys.begin(), keys.end());
    thrust::device_vector<std::int32_t> device_payload(payload.begin(), payload.end());
    thrust::stable_sort_by_key(device_keys.begin(), device_keys.end(), device_payload.begin());
    return {thrust_edge_detail::host(device_keys), thrust_edge_detail::host(device_payload)};
  }

  template <class T> std::vector<T> dependency_diamond(const std::vector<T> &input) {
    using namespace thrust_edge_detail;
    if (input.empty())
      return {};
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> left(input.size()), right(input.size()), output(input.size());
    StreamResource stream_left("stream create left");
    StreamResource stream_right("stream create right");
    StreamResource stream_join("stream create join");
    EventResource left_ready("event create left");
    EventResource right_ready("event create right");
#if defined(__HIPCC__)
    thrust::transform(policy(stream_left.get()), source.begin(), source.end(), left.begin(),
                      algorithms::Affine<T>{});
    thrust::transform(policy(stream_right.get()), source.begin(), source.end(), right.begin(),
                      algorithms::Square<T>{});
    check(hipEventRecord(left_ready.get(), stream_left.get()), "hipEventRecord left");
    check(hipEventRecord(right_ready.get(), stream_right.get()), "hipEventRecord right");
    check(hipStreamWaitEvent(stream_join.get(), left_ready.get(), 0), "hipStreamWaitEvent left");
    check(hipStreamWaitEvent(stream_join.get(), right_ready.get(), 0), "hipStreamWaitEvent right");
    thrust::transform(policy(stream_join.get()), left.begin(), left.end(), right.begin(), output.begin(),
                      algorithms::Combine<T>{});
    check(hipStreamSynchronize(stream_join.get()), "hipStreamSynchronize join");
#else
    thrust::transform(policy(stream_left.get()), source.begin(), source.end(), left.begin(),
                      algorithms::Affine<T>{});
    thrust::transform(policy(stream_right.get()), source.begin(), source.end(), right.begin(),
                      algorithms::Square<T>{});
    check(cudaEventRecord(left_ready.get(), stream_left.get()), "cudaEventRecord left");
    check(cudaEventRecord(right_ready.get(), stream_right.get()), "cudaEventRecord right");
    check(cudaStreamWaitEvent(stream_join.get(), left_ready.get(), 0), "cudaStreamWaitEvent left");
    check(cudaStreamWaitEvent(stream_join.get(), right_ready.get(), 0), "cudaStreamWaitEvent right");
    thrust::transform(policy(stream_join.get()), left.begin(), left.end(), right.begin(), output.begin(),
                      algorithms::Combine<T>{});
    check(cudaStreamSynchronize(stream_join.get()), "cudaStreamSynchronize join");
#endif
    left_ready.close("event destroy left");
    right_ready.close("event destroy right");
    stream_left.close("stream destroy left");
    stream_right.close("stream destroy right");
    stream_join.close("stream destroy join");
    return host(output);
  }

  template <class T> std::vector<T> numeric_edge_roundtrip(const std::vector<T> &input) {
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust::transform(source.begin(), source.end(), output.begin(), edge_cases::Identity<T>{});
    return thrust_edge_detail::host(output);
  }

  template <class T>
  std::vector<T> primitive_block_scan(const std::vector<T> &input, std::size_t block_size) {
    if (block_size != 32)
      throw Unsupported("primitive block scan currently requires a block size of 32");
    if (input.empty())
      return {};
    const thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    constexpr unsigned block = 32;
    const auto grid = static_cast<unsigned>((input.size() + block - 1) / block);
#if defined(__HIPCC__)
    hipLaunchKernelGGL((thrust_edge_detail::block_scan_kernel<T, block>), dim3(grid), dim3(block), 0, 0,
                       thrust::raw_pointer_cast(source.data()), thrust::raw_pointer_cast(output.data()),
                       input.size());
    thrust_edge_detail::check(hipGetLastError(), "rocPRIM block scan launch");
    thrust_edge_detail::check(hipDeviceSynchronize(), "rocPRIM block scan synchronize");
#else
    thrust_edge_detail::block_scan_kernel<T, block><<<grid, block>>>(
        thrust::raw_pointer_cast(source.data()), thrust::raw_pointer_cast(output.data()), input.size());
    thrust_edge_detail::check(cudaGetLastError(), "CUB block scan launch");
    thrust_edge_detail::check(cudaDeviceSynchronize(), "CUB block scan synchronize");
#endif
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> runtime_error_recover(const std::vector<T> &input) {
#if defined(__HIPCC__)
    const auto failure = hipMalloc(nullptr, sizeof(T));
    if (failure == hipSuccess)
      throw std::runtime_error("invalid HIP allocation unexpectedly succeeded");
    (void)hipGetLastError();
#else
    const auto failure = cudaMalloc(nullptr, sizeof(T));
    if (failure == cudaSuccess)
      throw std::runtime_error("invalid CUDA allocation unexpectedly succeeded");
    (void)cudaGetLastError();
#endif
    const thrust::device_vector<T> recovered(input.begin(), input.end());
    return thrust_edge_detail::host(recovered);
  }
};

} // namespace unified_suite::adapters

#endif
