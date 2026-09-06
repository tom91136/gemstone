#ifndef UNIFIED_SUITE_ADAPTERS_THRUST_ASYNC_HPP
#define UNIFIED_SUITE_ADAPTERS_THRUST_ASYNC_HPP

#include "unified_suite/adapters/thrust_edge_cases.hpp"
#include "unified_suite/async_cases.hpp"
#include "unified_suite/hip_async_cases.hpp"

#if __has_include(<thrust/async/reduce.h>) && __has_include(<thrust/async/scan.h>)
#define UNIFIED_SUITE_HAS_THRUST_ASYNC 1
#include <thrust/async/reduce.h>
#include <thrust/async/scan.h>
#else
#define UNIFIED_SUITE_HAS_THRUST_ASYNC 0
#endif
#include <thrust/copy.h>
#include <thrust/iterator/discard_iterator.h>
#include <thrust/reduce.h>
#include <thrust/scan.h>
#include <thrust/sort.h>
#include <thrust/transform.h>

#include <cstddef>
#include <vector>

namespace unified_suite::adapters {

// Thrust's stream execution policies are the common CUDA/rocThrust async
// surface. Every method waits for the queued work (with either stream
// synchronization or a future completion), so dependency ordering is part of
// the assertion.
class ThrustAsync {
public:
  template <class T> bool supports(async_cases::Operation) const { return true; }

  template <class T> bool supports(hip_async_cases::Operation) const {
#if defined(__HIPCC__)
    return true;
#else
    return false;
#endif
  }

  template <class T> std::vector<T> async_copy(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust_edge_detail::StreamResource stream("async copy stream create");
    thrust::copy(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(), output.begin());
    synchronize(stream.get(), "async copy synchronize");
    return thrust_edge_detail::host(output);
  }

  template <class T> T async_reduce(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_edge_detail::StreamResource stream("async reduce stream create");
    // Unlike the ordinary stream-policy overload, this is the vendor async
    // API: reduce returns a device future and get() is the completion point.
#if UNIFIED_SUITE_HAS_THRUST_ASYNC
    auto result = thrust::async::reduce(thrust_edge_detail::policy(stream.get()), source.begin(),
                                        source.end(), T{}, thrust::plus<T>{});
    return result.get();
#else
    return thrust::reduce(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(), T{}, thrust::plus<T>{});
#endif
  }

  template <class T> std::vector<T> async_scan(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(input.size());
    thrust_edge_detail::StreamResource stream("async scan stream create");
    thrust::inclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(),
                           output.begin(), thrust::plus<T>{});
    synchronize(stream.get(), "async scan synchronize");
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> async_inclusive_scan_counting(std::size_t size) {
    thrust::device_vector<T> output(size);
    const auto first = thrust::make_counting_iterator<T>(T{0});
    const auto last = first + static_cast<std::ptrdiff_t>(size);
    thrust_edge_detail::StreamResource stream("HIP async inclusive counting stream create");
#if UNIFIED_SUITE_HAS_THRUST_ASYNC
    auto completion = thrust::async::inclusive_scan(thrust_edge_detail::policy(stream.get()), first, last,
                                                    output.begin(), thrust::plus<T>{});
    completion.wait();
#else
    thrust::inclusive_scan(thrust_edge_detail::policy(stream.get()), first, last, output.begin(), thrust::plus<T>{});
    synchronize(stream.get(), "HIP async inclusive counting synchronize");
#endif
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> async_exclusive_scan_counting(std::size_t size) {
    thrust::device_vector<T> output(size);
    const auto first = thrust::make_counting_iterator<T>(T{0});
    const auto last = first + static_cast<std::ptrdiff_t>(size);
    thrust_edge_detail::StreamResource stream("HIP async exclusive counting stream create");
#if UNIFIED_SUITE_HAS_THRUST_ASYNC
    auto completion = thrust::async::exclusive_scan(thrust_edge_detail::policy(stream.get()), first, last,
                                                    output.begin(), T{}, thrust::plus<T>{});
    completion.wait();
#else
    thrust::exclusive_scan(thrust_edge_detail::policy(stream.get()), first, last, output.begin(), T{},
                           thrust::plus<T>{});
    synchronize(stream.get(), "HIP async exclusive counting synchronize");
#endif
    return thrust_edge_detail::host(output);
  }

  template <class T> void async_inclusive_scan_discard(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_edge_detail::StreamResource stream("HIP async inclusive discard stream create");
#if UNIFIED_SUITE_HAS_THRUST_ASYNC
    auto completion =
        thrust::async::inclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(),
                                      thrust::make_discard_iterator(), thrust::plus<T>{});
    completion.wait();
#else
    thrust::inclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(),
                           thrust::make_discard_iterator(), thrust::plus<T>{});
    synchronize(stream.get(), "HIP async inclusive discard synchronize");
#endif
  }

  template <class T> void async_exclusive_scan_discard(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust_edge_detail::StreamResource stream("HIP async exclusive discard stream create");
#if UNIFIED_SUITE_HAS_THRUST_ASYNC
    auto completion =
        thrust::async::exclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(),
                                      thrust::make_discard_iterator(), T{}, thrust::plus<T>{});
    completion.wait();
#else
    thrust::exclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(),
                           thrust::make_discard_iterator(), T{}, thrust::plus<T>{});
    synchronize(stream.get(), "HIP async exclusive discard synchronize");
#endif
  }

  template <class T> std::vector<T> async_inclusive_scan_stateful(std::size_t size) {
    const auto input = async_cases::detail::input_values<T>(size);
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(size);
    thrust_edge_detail::StreamResource stream("HIP async inclusive stateful stream create");
#if UNIFIED_SUITE_HAS_THRUST_ASYNC
    auto completion =
        thrust::async::inclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(),
                                      output.begin(), hip_async_cases::detail::StatefulOperator<T>{T{2}});
    completion.wait();
#else
    thrust::inclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(), output.begin(),
                           hip_async_cases::detail::StatefulOperator<T>{T{2}});
    synchronize(stream.get(), "HIP async inclusive stateful synchronize");
#endif
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> async_exclusive_scan_stateful(std::size_t size) {
    const auto input = async_cases::detail::input_values<T>(size);
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> output(size);
    thrust_edge_detail::StreamResource stream("HIP async exclusive stateful stream create");
#if UNIFIED_SUITE_HAS_THRUST_ASYNC
    auto completion = thrust::async::exclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(),
                                                    source.end(), output.begin(), T{42},
                                                    hip_async_cases::detail::StatefulOperator<T>{T{2}});
    completion.wait();
#else
    thrust::exclusive_scan(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(), output.begin(), T{42},
                           hip_async_cases::detail::StatefulOperator<T>{T{2}});
    synchronize(stream.get(), "HIP async exclusive stateful synchronize");
#endif
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> async_sort(const std::vector<T> &input) {
    thrust::device_vector<T> values(input.begin(), input.end());
    thrust_edge_detail::StreamResource stream("async sort stream create");
    thrust::sort(thrust_edge_detail::policy(stream.get()), values.begin(), values.end());
    synchronize(stream.get(), "async sort synchronize");
    return thrust_edge_detail::host(values);
  }

  template <class T> std::vector<T> async_future_chain(const std::vector<T> &input) {
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> first(input.size());
    thrust::device_vector<T> output(input.size());
    thrust_edge_detail::StreamResource stream("async future-chain stream create");
    thrust::transform(thrust_edge_detail::policy(stream.get()), source.begin(), source.end(), first.begin(),
                      algorithms::Affine<T>{});
    // The second operation is the continuation.  Stream ordering is the
    // device equivalent of a future continuation and does not require a host
    // round-trip between stages.
    thrust::transform(thrust_edge_detail::policy(stream.get()), first.begin(), first.end(), output.begin(),
                      algorithms::Square<T>{});
    synchronize(stream.get(), "async future-chain synchronize");
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> async_event_chain(const std::vector<T> &input) {
    if (input.empty())
      return {};
    thrust::device_vector<T> source(input.begin(), input.end());
    thrust::device_vector<T> left(input.size());
    thrust::device_vector<T> right(input.size());
    thrust::device_vector<T> output(input.size());
    thrust_edge_detail::StreamResource left_stream("async event left stream create");
    thrust_edge_detail::StreamResource right_stream("async event right stream create");
    thrust_edge_detail::StreamResource join_stream("async event join stream create");
    thrust_edge_detail::EventResource left_ready("async event left create");
    thrust_edge_detail::EventResource right_ready("async event right create");
    thrust::transform(thrust_edge_detail::policy(left_stream.get()), source.begin(), source.end(),
                      left.begin(), algorithms::Affine<T>{});
    thrust::transform(thrust_edge_detail::policy(right_stream.get()), source.begin(), source.end(),
                      right.begin(), algorithms::Square<T>{});
    record(left_ready.get(), left_stream.get(), "async event left record");
    record(right_ready.get(), right_stream.get(), "async event right record");
    wait_event(join_stream.get(), left_ready.get(), "async event left wait");
    wait_event(join_stream.get(), right_ready.get(), "async event right wait");
    thrust::transform(thrust_edge_detail::policy(join_stream.get()), left.begin(), left.end(), right.begin(),
                      output.begin(), algorithms::Combine<T>{});
    synchronize(join_stream.get(), "async event join synchronize");
    return thrust_edge_detail::host(output);
  }

  template <class T> std::vector<T> async_interleaved_streams(const std::vector<T> &input) {
    std::vector<T> even;
    std::vector<T> odd;
    even.reserve((input.size() + 1) / 2);
    odd.reserve(input.size() / 2);
    for (std::size_t index = 0; index < input.size(); ++index)
      (index % 2 == 0 ? even : odd).push_back(input[index]);

    thrust::device_vector<T> even_device(even.begin(), even.end());
    thrust::device_vector<T> odd_device(odd.begin(), odd.end());
    thrust_edge_detail::StreamResource even_stream("async interleaved even stream create");
    thrust_edge_detail::StreamResource odd_stream("async interleaved odd stream create");
    thrust::transform(thrust_edge_detail::policy(even_stream.get()), even_device.begin(), even_device.end(),
                      even_device.begin(), algorithms::Affine<T>{});
    thrust::transform(thrust_edge_detail::policy(odd_stream.get()), odd_device.begin(), odd_device.end(),
                      odd_device.begin(), algorithms::Square<T>{});
    synchronize(even_stream.get(), "async interleaved even synchronize");
    synchronize(odd_stream.get(), "async interleaved odd synchronize");
    const auto transformed_even = thrust_edge_detail::host(even_device);
    const auto transformed_odd = thrust_edge_detail::host(odd_device);
    std::vector<T> output(input.size());
    std::size_t even_index = 0;
    std::size_t odd_index = 0;
    for (std::size_t index = 0; index < output.size(); ++index)
      output[index] = index % 2 == 0 ? transformed_even[even_index++] : transformed_odd[odd_index++];
    return output;
  }

private:
#if defined(__HIPCC__)
  static void synchronize(hipStream_t stream, const char *where) {
    thrust_edge_detail::check(hipStreamSynchronize(stream), where);
  }
  static void record(hipEvent_t event, hipStream_t stream, const char *where) {
    thrust_edge_detail::check(hipEventRecord(event, stream), where);
  }
  static void wait_event(hipStream_t stream, hipEvent_t event, const char *where) {
    thrust_edge_detail::check(hipStreamWaitEvent(stream, event, 0), where);
  }
#else
  static void synchronize(cudaStream_t stream, const char *where) {
    thrust_edge_detail::check(cudaStreamSynchronize(stream), where);
  }
  static void record(cudaEvent_t event, cudaStream_t stream, const char *where) {
    thrust_edge_detail::check(cudaEventRecord(event, stream), where);
  }
  static void wait_event(cudaStream_t stream, cudaEvent_t event, const char *where) {
    thrust_edge_detail::check(cudaStreamWaitEvent(stream, event, 0), where);
  }
#endif
};

} // namespace unified_suite::adapters

#undef UNIFIED_SUITE_HAS_THRUST_ASYNC

#endif
