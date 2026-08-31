#ifndef UNIFIED_SUITE_ADAPTERS_ONEDPL_EDGE_CASES_HPP
#define UNIFIED_SUITE_ADAPTERS_ONEDPL_EDGE_CASES_HPP

#include "unified_suite/adapters/onedpl_algorithms.hpp"
#include "unified_suite/adapters/sycl_support.hpp"
#include "unified_suite/edge_cases.hpp"

#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>
#include <oneapi/dpl/iterator>
#include <oneapi/dpl/numeric>
#include <sycl/sycl.hpp>

#include <cstddef>
#include <cstdint>
#include <vector>

namespace unified_suite::adapters {

namespace onedpl_edge_detail {
template <class T, int Id> class Kernel;
template <class T, int Id> class DiamondLeft;
template <class T, int Id> class DiamondRight;
template <class T, int Id> class DiamondJoin;
template <class T, int Id> class BlockScan;
} // namespace onedpl_edge_detail

class OneDPLEdgeCases {
public:
  OneDPLEdgeCases() : queue_(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool supports(edge_cases::Operation operation) const {
    if (operation == edge_cases::Operation::runtime_error_recover)
      return false;
    return !std::is_same_v<T, double> || sycl_support::supports_fp64(queue_.get_device());
  }

  template <class T> std::int64_t composed_iterator_reduce(std::size_t size) {
    const oneapi::dpl::counting_iterator<std::size_t> indices(0);
    const auto source = oneapi::dpl::make_transform_iterator(indices, edge_cases::ValueAt<T>{});
    const auto reverse = oneapi::dpl::make_permutation_iterator(source, edge_cases::ReverseIndex{size});
    const auto result = oneapi::dpl::transform_reduce(
        policy<T, 0>(), reverse, reverse + static_cast<std::ptrdiff_t>(size), source, std::int64_t{},
        std::plus<std::int64_t>{}, edge_cases::WidePairValue<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T> std::uint64_t biased_index_checksum(std::uint64_t base, std::size_t size) {
    const oneapi::dpl::counting_iterator<std::uint64_t> begin(base);
    const auto result = oneapi::dpl::transform_reduce(policy<T, 1>(), begin, begin + size, std::uint64_t{},
                                                      edge_cases::Xor64{}, edge_cases::BiasedHash<T>{});
    queue_.wait_and_throw();
    return result;
  }

  template <class T>
  std::vector<T> segmented_inclusive_scan(const std::vector<std::int32_t> &keys,
                                          const std::vector<T> &input) {
    onedpl_detail::Shared<std::int32_t> device_keys(queue_, keys);
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    oneapi::dpl::inclusive_scan_by_segment(policy<T, 2>(), device_keys.begin(), device_keys.end(),
                                           source.begin(), output.begin());
    return finish(output);
  }

  template <class T>
  std::vector<T> inplace_inclusive_scan(const std::vector<T> &storage, std::size_t offset,
                                        std::size_t count) {
    onedpl_detail::Shared<T> output(queue_, storage);
    oneapi::dpl::inclusive_scan(policy<T, 3>(), output.begin() + static_cast<std::ptrdiff_t>(offset),
                                output.begin() + static_cast<std::ptrdiff_t>(offset + count),
                                output.begin() + static_cast<std::ptrdiff_t>(offset));
    return finish(output);
  }

  template <class T>
  algorithms::KeyValues<T> stable_sort_payload(const std::vector<T> &keys,
                                               const std::vector<std::int32_t> &payload) {
    onedpl_detail::Shared<T> device_keys(queue_, keys);
    onedpl_detail::Shared<std::int32_t> device_payload(queue_, payload);
    oneapi::dpl::stable_sort_by_key(policy<T, 4>(), device_keys.begin(), device_keys.end(),
                                    device_payload.begin());
    queue_.wait_and_throw();
    return {device_keys.host(), device_payload.host()};
  }

  template <class T> std::vector<T> dependency_diamond(const std::vector<T> &input) {
    if (input.empty())
      return {};
    const auto device = queue_.get_device();
    const auto context = queue_.get_context();
    sycl::queue left_queue(context, device);
    sycl::queue right_queue(context, device);
    sycl::queue join_queue(context, device);
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> left(queue_, input.size());
    onedpl_detail::Shared<T> right(queue_, input.size());
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto count = input.size();
    const auto *source_data = source.begin();
    auto *left_data = left.begin();
    auto *right_data = right.begin();
    auto *output_data = output.begin();
    const auto left_event = left_queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::DiamondLeft<T, 0>>(
          count, [=](sycl::id<1> index) { left_data[index] = algorithms::Affine<T>{}(source_data[index]); });
    });
    const auto right_event = right_queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::DiamondRight<T, 0>>(
          count, [=](sycl::id<1> index) { right_data[index] = algorithms::Square<T>{}(source_data[index]); });
    });
    join_queue.submit([&](sycl::handler &handler) {
      handler.depends_on({left_event, right_event});
      handler.parallel_for<onedpl_edge_detail::DiamondJoin<T, 0>>(count, [=](sycl::id<1> index) {
        output_data[index] = algorithms::Combine<T>{}(left_data[index], right_data[index]);
      });
    });
    join_queue.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> numeric_edge_roundtrip(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    oneapi::dpl::transform(policy<T, 5>(), source.begin(), source.end(), output.begin(),
                           edge_cases::Identity<T>{});
    return finish(output);
  }

  template <class T>
  std::vector<T> primitive_block_scan(const std::vector<T> &input, std::size_t block_size) {
    if (block_size != 32)
      throw Unsupported("SYCL group scan currently requires a block size of 32");
    if (input.empty())
      return {};
    onedpl_detail::Shared<T> source(queue_, input);
    onedpl_detail::Shared<T> output(queue_, input.size());
    const auto global = ((input.size() + block_size - 1) / block_size) * block_size;
    const auto size = input.size();
    const auto *source_data = source.begin();
    auto *output_data = output.begin();
    queue_.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::BlockScan<T, 0>>(
          sycl::nd_range<1>(global, block_size), [=](sycl::nd_item<1> item) {
            const auto index = item.get_global_linear_id();
            const auto value = index < size ? source_data[index] : T{};
            const auto scanned = sycl::inclusive_scan_over_group(item.get_group(), value, sycl::plus<T>{});
            if (index < size)
              output_data[index] = scanned;
          });
    });
    return finish(output);
  }

  template <class T> std::vector<T> runtime_error_recover(const std::vector<T> &) {
    throw Unsupported("oneDPL does not expose an equivalent recoverable runtime-error path");
  }

private:
  sycl::queue queue_;

  template <class T, int Id> auto policy() {
    return oneapi::dpl::execution::make_device_policy<onedpl_edge_detail::Kernel<T, Id>>(queue_);
  }

  template <class T> std::vector<T> finish(const onedpl_detail::Shared<T> &output) {
    queue_.wait_and_throw();
    return output.host();
  }
};

} // namespace unified_suite::adapters

#endif
