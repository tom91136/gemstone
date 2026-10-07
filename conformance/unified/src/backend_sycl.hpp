#pragma once

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <new>
#include <numeric>
#include <stdexcept>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>
#include <oneapi/dpl/iterator>
#include <oneapi/dpl/memory>
#include <oneapi/dpl/numeric>
#include <sycl/sycl.hpp>

#include "algorithm_cases.hpp"
#include "edge_cases.hpp"

namespace unified::backend::sycl_support {
template <class T, size_t Id> class Kernel;

template <class T> class Shared {
public:
  Shared(sycl::queue &queue, size_t size) : queue(&queue), length(size) {
    storage = sycl::malloc_device<T>(std::max<size_t>(size, 1), queue);
    if (!storage) throw std::bad_alloc();
  }

  Shared(sycl::queue &queue, const std::vector<T> &input) : Shared(queue, input.size()) {
    if (!input.empty()) queue.memcpy(storage, input.data(), input.size() * sizeof(T)).wait_and_throw();
  }

  Shared(const Shared &) = delete;
  Shared &operator=(const Shared &) = delete;
  Shared(Shared &&) = delete;
  Shared &operator=(Shared &&) = delete;
  ~Shared() { sycl::free(storage, *queue); }

  T *begin() { return storage; }
  const T *begin() const { return storage; }
  T *end() { return storage + length; }
  const T *end() const { return storage + length; }
  size_t size() const { return length; }
  std::vector<T> host(size_t count) const {
    if (count > length) throw std::out_of_range("SYCL download exceeds allocation");
    std::vector<T> result(count);
    if (count != 0) queue->memcpy(result.data(), storage, count * sizeof(T)).wait_and_throw();
    return result;
  }
  std::vector<T> host() const { return host(length); }

private:
  sycl::queue *queue;
  T *storage{};
  size_t length{};
};

inline bool enabled(std::string_view variable) {
  const auto *value = std::getenv(std::string(variable).c_str());
  return value != nullptr && std::string_view(value) == "1";
}

inline bool supportsFp64(const sycl::device &device) {
  return device.has(sycl::aspect::fp64) || (enabled("OverrideDefaultFP64Settings") && enabled("IGC_EnableDPEmulation"));
}
} // namespace unified::backend::sycl_support

namespace unified::backend {
namespace onedpl_detail = sycl_support;

class OneDPLAlgorithms {
public:
  OneDPLAlgorithms() : queue(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool supports(std::string_view operation, size_t size) const {
    if (sizeof(T) == 4 && size >= 31 && arcPolicyFaults
        && (operation == "set_intersection" || operation == "set_difference" || operation == "set_symmetric_difference"
            || operation == "partial_sort" || operation == "nth_element"))
      return false;
    return !std::is_same_v<T, double> || sycl_support::supportsFp64(queue.get_device());
  }

  template <class T> std::vector<T> copy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::copy(policy<T, 0>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> copyIf(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    const auto end = std::copy_if(policy<T, 20>(), source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> copyN(const std::vector<T> &input, size_t count) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, count);
    std::copy_n(policy<T, 21>(), source.begin(), count, output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> uninitializedCopy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::uninitialized_copy(policy<T, 68>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> uninitializedFill(size_t size, T value) {
    onedpl_detail::Shared<T> output(queue, size);
    std::uninitialized_fill(policy<T, 69>(), output.begin(), output.end(), value);
    return finish(output);
  }

  template <class T> std::vector<T> fill(size_t size, T value) {
    onedpl_detail::Shared<T> output(queue, size);
    std::fill(policy<T, 22>(), output.begin(), output.end(), value);
    return finish(output);
  }

  template <class T> std::vector<T> generate(size_t size) {
    onedpl_detail::Shared<T> output(queue, size);
    std::generate(policy<T, 23>(), output.begin(), output.end(), algorithms::Constant<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> forEach(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    std::for_each(policy<T, 24>(), output.begin(), output.end(), algorithms::Increment<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transformUnary(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::transform(policy<T, 1>(), source.begin(), source.end(), output.begin(), algorithms::Affine<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transformBinary(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    onedpl_detail::Shared<T> output(queue, left.size());
    std::transform(policy<T, 2>(), a.begin(), a.end(), b.begin(), output.begin(), algorithms::Combine<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> replaceIf(const std::vector<T> &input, T replacement) {
    onedpl_detail::Shared<T> output(queue, input);
    std::replace_if(policy<T, 25>(), output.begin(), output.end(), algorithms::IsEven<T>{}, replacement);
    return finish(output);
  }

  template <class T> std::vector<T> replaceCopyIf(const std::vector<T> &input, T replacement) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::replace_copy_if(policy<T, 26>(), source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{}, replacement);
    return finish(output);
  }

  template <class T> std::vector<T> removeIf(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    const auto end = std::remove_if(policy<T, 27>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> removeCopyIf(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    const auto end = std::remove_copy_if(policy<T, 28>(), source.begin(), source.end(), output.begin(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> algorithms::TwoRanges<T> swapRanges(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    std::swap_ranges(policy<T, 29>(), a.begin(), a.end(), b.begin());
    queue.wait_and_throw();
    return {a.host(), b.host()};
  }

  template <class T> T reduce(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::reduce(policy<T, 3>(), source.begin(), source.end(), T{}, algorithms::Plus<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> T transformReduce(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result =
        std::transform_reduce(policy<T, 4>(), source.begin(), source.end(), T{}, algorithms::Plus<T>{}, algorithms::Square<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> T innerProduct(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    const auto result = std::transform_reduce(policy<T, 30>(), a.begin(), a.end(), b.begin(), T{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> inclusiveScan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::inclusive_scan(policy<T, 5>(), source.begin(), source.end(), output.begin(), algorithms::Plus<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> exclusiveScan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::exclusive_scan(policy<T, 6>(), source.begin(), source.end(), output.begin(), T{}, algorithms::Plus<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transformInclusiveScan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::transform_inclusive_scan(policy<T, 31>(), source.begin(), source.end(), output.begin(), algorithms::Plus<T>{},
                                  algorithms::Square<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> transformExclusiveScan(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::transform_exclusive_scan(policy<T, 32>(), source.begin(), source.end(), output.begin(), T{}, algorithms::Plus<T>{},
                                  algorithms::Square<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> sort(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    std::sort(policy<T, 7>(), output.begin(), output.end(), algorithms::Less<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> stableSort(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    std::stable_sort(policy<T, 33>(), output.begin(), output.end(), algorithms::Less<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> sortDescending(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    std::sort(policy<T, 34>(), output.begin(), output.end(), algorithms::Greater<T>{});
    return finish(output);
  }

  template <class T> algorithms::KeyValues<T> sortByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> deviceKeys(queue, keys);
    onedpl_detail::Shared<int32_t> deviceValues(queue, values);
    oneapi::dpl::sort_by_key(policy<T, 8>(), deviceKeys.begin(), deviceKeys.end(), deviceValues.begin(), algorithms::Less<T>{});
    queue.wait_and_throw();
    return {deviceKeys.host(), deviceValues.host()};
  }

  template <class T> algorithms::KeyValues<T> stableSortByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> deviceKeys(queue, keys);
    onedpl_detail::Shared<int32_t> deviceValues(queue, values);
    oneapi::dpl::stable_sort_by_key(policy<T, 9>(), deviceKeys.begin(), deviceKeys.end(), deviceValues.begin(), algorithms::Less<T>{});
    queue.wait_and_throw();
    return {deviceKeys.host(), deviceValues.host()};
  }

  template <class T> std::vector<T> unique(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    const auto end = std::unique(policy<T, 10>(), output.begin(), output.end());
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> uniqueCopy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    const auto end = std::unique_copy(policy<T, 35>(), source.begin(), source.end(), output.begin());
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> size_t count(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::count(policy<T, 36>(), source.begin(), source.end(), value);
    queue.wait_and_throw();
    return static_cast<size_t>(result);
  }

  template <class T> size_t countIf(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::count_if(policy<T, 11>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return static_cast<size_t>(result);
  }

  template <class T> size_t findIf(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::find_if(policy<T, 12>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> size_t find(const std::vector<T> &input, T value) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::find(policy<T, 37>(), source.begin(), source.end(), value);
    queue.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> bool allOf(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::all_of(policy<T, 38>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> bool anyOf(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::any_of(policy<T, 39>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> bool noneOf(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::none_of(policy<T, 40>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> bool equal(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    const auto result = std::equal(policy<T, 41>(), a.begin(), a.end(), b.begin());
    queue.wait_and_throw();
    return result;
  }

  template <class T> algorithms::TwoPositions mismatch(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    const auto result = std::mismatch(policy<T, 42>(), a.begin(), a.end(), b.begin());
    queue.wait_and_throw();
    return {static_cast<size_t>(result.first - a.begin()), static_cast<size_t>(result.second - b.begin())};
  }

  template <class T> size_t lowerBound(const std::vector<T> &input, T value) {
    // oneDPL leaves vector-search output untouched for an empty haystack.
    if (input.empty()) return 0;
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> needles(queue, std::vector<T>{value});
    onedpl_detail::Shared<size_t> results(queue, 1);
    oneapi::dpl::lower_bound(policy<T, 13>(), source.begin(), source.end(), needles.begin(), needles.end(), results.begin(),
                             algorithms::Less<T>{});
    queue.wait_and_throw();
    return results.host(1)[0];
  }

  template <class T> size_t upperBound(const std::vector<T> &input, T value) {
    // oneDPL leaves vector-search output untouched for an empty haystack.
    if (input.empty()) return 0;
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> needles(queue, std::vector<T>{value});
    onedpl_detail::Shared<size_t> results(queue, 1);
    oneapi::dpl::upper_bound(policy<T, 43>(), source.begin(), source.end(), needles.begin(), needles.end(), results.begin(),
                             algorithms::Less<T>{});
    queue.wait_and_throw();
    return results.host(1)[0];
  }

  template <class T> bool binarySearch(const std::vector<T> &input, T value) {
    // oneDPL leaves vector-search output untouched for an empty haystack.
    if (input.empty()) return false;
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> needles(queue, std::vector<T>{value});
    onedpl_detail::Shared<uint8_t> results(queue, 1);
    oneapi::dpl::binary_search(policy<T, 44>(), source.begin(), source.end(), needles.begin(), needles.end(), results.begin(),
                               algorithms::Less<T>{});
    queue.wait_and_throw();
    return results.host(1)[0] != 0;
  }

  template <class T> algorithms::TwoPositions equalRange(const std::vector<T> &input, T value) {
    // oneDPL leaves vector-search output untouched for an empty haystack.
    if (input.empty()) return {0, 0};
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> needles(queue, std::vector<T>{value});
    onedpl_detail::Shared<size_t> lower(queue, 1);
    onedpl_detail::Shared<size_t> upper(queue, 1);
    oneapi::dpl::lower_bound(policy<T, 45>(), source.begin(), source.end(), needles.begin(), needles.end(), lower.begin(),
                             algorithms::Less<T>{});
    oneapi::dpl::upper_bound(policy<T, 46>(), source.begin(), source.end(), needles.begin(), needles.end(), upper.begin(),
                             algorithms::Less<T>{});
    queue.wait_and_throw();
    return {lower.host(1)[0], upper.host(1)[0]};
  }

  template <class T> std::vector<T> merge(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    onedpl_detail::Shared<T> output(queue, left.size() + right.size());
    std::merge(policy<T, 14>(), a.begin(), a.end(), b.begin(), b.end(), output.begin(), algorithms::Less<T>{});
    return finish(output);
  }

  template <class T> algorithms::Partitioned<T> partition(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    const auto boundary = std::partition(policy<T, 15>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return {output.host(), static_cast<size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::Partitioned<T> stablePartition(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    const auto boundary = std::stable_partition(policy<T, 47>(), output.begin(), output.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return {output.host(), static_cast<size_t>(boundary - output.begin())};
  }

  template <class T> algorithms::TwoRanges<T> partitionCopy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> selected(queue, input.size());
    onedpl_detail::Shared<T> rejected(queue, input.size());
    const auto ends =
        std::partition_copy(policy<T, 48>(), source.begin(), source.end(), selected.begin(), rejected.begin(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return {selected.host(static_cast<size_t>(ends.first - selected.begin())),
            rejected.host(static_cast<size_t>(ends.second - rejected.begin()))};
  }

  template <class T> bool isPartitioned(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::is_partitioned(policy<T, 49>(), source.begin(), source.end(), algorithms::IsEven<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> size_t partitionPoint(const std::vector<T> &) { SKIP("partition-point has no oneDPL execution-policy overload"); }

  template <class T> std::vector<T> adjacentDifference(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::adjacent_difference(policy<T, 16>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> setUnion(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    onedpl_detail::Shared<T> output(queue, left.size() + right.size());
    const auto end = std::set_union(policy<T, 17>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> setIntersection(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    onedpl_detail::Shared<T> output(queue, std::min(left.size(), right.size()));
    const auto end = std::set_intersection(policy<T, 18>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> setDifference(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    onedpl_detail::Shared<T> output(queue, left.size());
    const auto end = std::set_difference(policy<T, 51>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> setSymmetricDifference(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    onedpl_detail::Shared<T> output(queue, left.size() + right.size());
    const auto end = std::set_symmetric_difference(policy<T, 52>(), a.begin(), a.end(), b.begin(), b.end(), output.begin());
    queue.wait_and_throw();
    return output.host(static_cast<size_t>(end - output.begin()));
  }

  template <class T> std::vector<T> reverse(const std::vector<T> &input) {
    onedpl_detail::Shared<T> output(queue, input);
    std::reverse(policy<T, 19>(), output.begin(), output.end());
    return finish(output);
  }

  template <class T> std::vector<T> reverseCopy(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::reverse_copy(policy<T, 53>(), source.begin(), source.end(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> shuffle(const std::vector<T> &) { SKIP("shuffle has no oneDPL execution-policy overload"); }

  template <class T> size_t minElement(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::min_element(policy<T, 54>(), source.begin(), source.end());
    queue.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> size_t maxElement(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::max_element(policy<T, 55>(), source.begin(), source.end());
    queue.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> algorithms::TwoPositions minmaxElement(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::minmax_element(policy<T, 56>(), source.begin(), source.end());
    queue.wait_and_throw();
    return {static_cast<size_t>(result.first - source.begin()), static_cast<size_t>(result.second - source.begin())};
  }

  template <class T> bool isSorted(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::is_sorted(policy<T, 57>(), source.begin(), source.end());
    queue.wait_and_throw();
    return result;
  }

  template <class T> size_t isSortedUntil(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = std::is_sorted_until(policy<T, 58>(), source.begin(), source.end());
    queue.wait_and_throw();
    return static_cast<size_t>(result - source.begin());
  }

  template <class T> std::vector<T> rotate(const std::vector<T> &input, size_t middle) {
    onedpl_detail::Shared<T> output(queue, input);
    std::rotate(policy<T, 59>(), output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return finish(output);
  }

  template <class T> std::vector<T> rotateCopy(const std::vector<T> &input, size_t middle) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    std::rotate_copy(policy<T, 60>(), source.begin(), source.begin() + static_cast<ptrdiff_t>(middle), source.end(), output.begin());
    return finish(output);
  }

  template <class T> bool includes(const std::vector<T> &left, const std::vector<T> &right) {
    onedpl_detail::Shared<T> a(queue, left);
    onedpl_detail::Shared<T> b(queue, right);
    const auto result = std::includes(policy<T, 61>(), a.begin(), a.end(), b.begin(), b.end());
    queue.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> gather(const std::vector<T> &, const std::vector<size_t> &) {
    SKIP("gather is not a oneDPL algorithm API");
  }

  template <class T> std::vector<T> scatter(const std::vector<T> &, const std::vector<size_t> &) {
    SKIP("scatter is not a oneDPL algorithm API");
  }

  template <class T> algorithms::KeyValues<T> reduceByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> sourceKeys(queue, keys);
    onedpl_detail::Shared<int32_t> sourceValues(queue, values);
    onedpl_detail::Shared<T> outputKeys(queue, keys.size());
    onedpl_detail::Shared<int32_t> outputValues(queue, values.size());
    const auto ends = oneapi::dpl::reduce_by_segment(policy<T, 65>(), sourceKeys.begin(), sourceKeys.end(), sourceValues.begin(),
                                                     outputKeys.begin(), outputValues.begin());
    queue.wait_and_throw();
    return {outputKeys.host(static_cast<size_t>(ends.first - outputKeys.begin())),
            outputValues.host(static_cast<size_t>(ends.second - outputValues.begin()))};
  }

  template <class T> algorithms::KeyValues<T> inclusiveScanByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> deviceKeys(queue, keys);
    onedpl_detail::Shared<int32_t> source(queue, values);
    onedpl_detail::Shared<int32_t> output(queue, values.size());
    oneapi::dpl::inclusive_scan_by_segment(policy<T, 66>(), deviceKeys.begin(), deviceKeys.end(), source.begin(), output.begin());
    queue.wait_and_throw();
    return {keys, output.host()};
  }

  template <class T> algorithms::KeyValues<T> exclusiveScanByKey(const std::vector<T> &keys, const std::vector<int32_t> &values) {
    onedpl_detail::Shared<T> deviceKeys(queue, keys);
    onedpl_detail::Shared<int32_t> source(queue, values);
    // Every output must be written, including zero-valued segment starts.
    onedpl_detail::Shared<int32_t> output(queue, std::vector<int32_t>(values.size(), std::numeric_limits<int32_t>::min()));
    oneapi::dpl::exclusive_scan_by_segment(policy<T, 67>(), deviceKeys.begin(), deviceKeys.end(), source.begin(), output.begin(),
                                           int32_t{0});
    queue.wait_and_throw();
    return {keys, output.host()};
  }

  template <class T> algorithms::KeyValues<T> uniqueByKey(const std::vector<T> &, const std::vector<int32_t> &) {
    SKIP("unique-by-key is not a oneDPL algorithm API");
  }

  template <class T> algorithms::KeyValues<T> mergeByKey(const algorithms::KeyValues<T> &, const algorithms::KeyValues<T> &) {
    SKIP("merge-by-key is not a oneDPL algorithm API");
  }

  template <class T> std::vector<T> partialSort(const std::vector<T> &input, size_t middle) {
    onedpl_detail::Shared<T> output(queue, input);
    std::partial_sort(policy<T, 62>(), output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return finish(output);
  }

  template <class T> std::vector<T> nthElement(const std::vector<T> &input, size_t nth) {
    onedpl_detail::Shared<T> output(queue, input);
    if (!input.empty()) std::nth_element(policy<T, 63>(), output.begin(), output.begin() + static_cast<ptrdiff_t>(nth), output.end());
    return finish(output);
  }

  template <class T> std::vector<T> inplaceMerge(const std::vector<T> &input, size_t middle) {
    onedpl_detail::Shared<T> output(queue, input);
    std::inplace_merge(policy<T, 64>(), output.begin(), output.begin() + static_cast<ptrdiff_t>(middle), output.end());
    return finish(output);
  }

private:
  sycl::queue queue;
  // Restrict the observed oneDPL faults to the tested Arc A750 device.
  const bool arcPolicyFaults =
      queue.get_backend() == sycl::backend::ext_oneapi_level_zero
      && queue.get_device().get_info<sycl::info::device::name>().find("Intel(R) Arc(TM) A750") != std::string::npos;

  template <class T, size_t Id> auto policy() { return oneapi::dpl::execution::make_device_policy<onedpl_detail::Kernel<T, Id>>(queue); }

  template <class T> std::vector<T> finish(const onedpl_detail::Shared<T> &output) {
    queue.wait_and_throw();
    return output.host();
  }
};
} // namespace unified::backend

namespace unified::backend {

namespace onedpl_edge_detail {
template <class T, size_t Id> class Kernel;
template <class T, size_t Id> class DiamondLeft;
template <class T, size_t Id> class DiamondRight;
template <class T, size_t Id> class DiamondJoin;
template <class T, size_t Id> class BlockScan;
} // namespace onedpl_edge_detail

class OneDPLEdgeCases {
public:
  OneDPLEdgeCases() : queue(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool supports(std::string_view, size_t) const {
    return !std::is_same_v<T, double> || sycl_support::supportsFp64(queue.get_device());
  }

  template <class T> int64_t composedIteratorReduce(size_t size) {
    const oneapi::dpl::counting_iterator<size_t> indices(0);
    const auto source = oneapi::dpl::make_transform_iterator(indices, edge::ValueAt<T>{});
    const auto reverse = oneapi::dpl::make_permutation_iterator(source, edge::ReverseIndex{size});
    const auto result = oneapi::dpl::transform_reduce(policy<T, 0>(), reverse, reverse + static_cast<ptrdiff_t>(size), source, int64_t{},
                                                      std::plus<int64_t>{}, edge::WidePairValue<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> uint64_t biasedIndexChecksum(uint64_t base, size_t size) {
    const oneapi::dpl::counting_iterator<uint64_t> begin(base);
    const auto result =
        oneapi::dpl::transform_reduce(policy<T, 1>(), begin, begin + size, uint64_t{}, edge::Xor64{}, edge::BiasedHash<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> segmentedInclusiveScan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
    onedpl_detail::Shared<int32_t> deviceKeys(queue, keys);
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    oneapi::dpl::inclusive_scan_by_segment(policy<T, 2>(), deviceKeys.begin(), deviceKeys.end(), source.begin(), output.begin());
    return finish(output);
  }

  template <class T> std::vector<T> inplaceInclusiveScan(const std::vector<T> &storage, size_t offset, size_t count) {
    onedpl_detail::Shared<T> output(queue, storage);
    oneapi::dpl::inclusive_scan(policy<T, 3>(), output.begin() + static_cast<ptrdiff_t>(offset),
                                output.begin() + static_cast<ptrdiff_t>(offset + count), output.begin() + static_cast<ptrdiff_t>(offset));
    return finish(output);
  }

  template <class T> support::KeyValues<T> stableSortPayload(const std::vector<T> &keys, const std::vector<int32_t> &payload) {
    onedpl_detail::Shared<T> deviceKeys(queue, keys);
    onedpl_detail::Shared<int32_t> devicePayload(queue, payload);
    oneapi::dpl::stable_sort_by_key(policy<T, 4>(), deviceKeys.begin(), deviceKeys.end(), devicePayload.begin());
    queue.wait_and_throw();
    return {deviceKeys.host(), devicePayload.host()};
  }

  template <class T> std::vector<T> dependencyDiamond(const std::vector<T> &input) {
    if (input.empty()) return {};
    const auto device = queue.get_device();
    const auto context = queue.get_context();
    sycl::queue leftQueue(context, device);
    sycl::queue rightQueue(context, device);
    sycl::queue joinQueue(context, device);
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> left(queue, input.size());
    onedpl_detail::Shared<T> right(queue, input.size());
    onedpl_detail::Shared<T> output(queue, input.size());
    const auto count = input.size();
    const auto *sourceData = source.begin();
    auto *leftData = left.begin();
    auto *rightData = right.begin();
    auto *outputData = output.begin();
    const auto leftEvent = leftQueue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::DiamondLeft<T, 0>>(
          count, [=](sycl::id<1> index) { leftData[index] = support::Affine<T>{}(sourceData[index]); });
    });
    const auto rightEvent = rightQueue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::DiamondRight<T, 0>>(
          count, [=](sycl::id<1> index) { rightData[index] = support::Square<T>{}(sourceData[index]); });
    });
    joinQueue.submit([&](sycl::handler &handler) {
      handler.depends_on({leftEvent, rightEvent});
      handler.parallel_for<onedpl_edge_detail::DiamondJoin<T, 0>>(
          count, [=](sycl::id<1> index) { outputData[index] = support::Combine<T>{}(leftData[index], rightData[index]); });
    });
    joinQueue.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> numericEdgeRoundtrip(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    oneapi::dpl::transform(policy<T, 5>(), source.begin(), source.end(), output.begin(), edge::Identity<T>{});
    return finish(output);
  }

  template <class T> std::vector<T> primitiveBlockScan(const std::vector<T> &input, size_t blockSize) {
    if (blockSize != 32) SKIP("SYCL group scan currently requires a block size of 32");
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    const auto global = ((input.size() + blockSize - 1) / blockSize) * blockSize;
    const auto size = input.size();
    const auto *sourceData = source.begin();
    auto *outputData = output.begin();
    queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_edge_detail::BlockScan<T, 0>>(sycl::nd_range<1>(global, blockSize), [=](sycl::nd_item<1> item) {
        const auto index = item.get_global_linear_id();
        const auto value = index < size ? sourceData[index] : T{};
        const auto scanned = sycl::inclusive_scan_over_group(item.get_group(), value, sycl::plus<T>{});
        if (index < size) outputData[index] = scanned;
      });
    });
    return finish(output);
  }

  template <class T> std::vector<T> runtimeErrorRecover(const std::vector<T> &) {
    SKIP("oneDPL does not expose an equivalent recoverable runtime-error path");
  }

private:
  sycl::queue queue;

  template <class T, size_t Id> auto policy() {
    return oneapi::dpl::execution::make_device_policy<onedpl_edge_detail::Kernel<T, Id>>(queue);
  }

  template <class T> std::vector<T> finish(const onedpl_detail::Shared<T> &output) {
    queue.wait_and_throw();
    return output.host();
  }
};
namespace onedpl_async_detail {
template <class T, size_t Id> class Kernel;
template <class T, size_t Id> class FutureStage;
template <class T, size_t Id> class EventStage;
template <class T, size_t Id> class InterleavedStage;
} // namespace onedpl_async_detail

class OneDPLAsync {
public:
  OneDPLAsync() : queue(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  template <class T> bool supports(std::string_view, size_t) const {
    return !std::is_same_v<T, double> || sycl_support::supportsFp64(queue.get_device());
  }

  template <class T> std::vector<T> asyncCopy(const std::vector<T> &input) {
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    auto event = queue.memcpy(output.begin(), source.begin(), input.size() * sizeof(T));
    event.wait_and_throw();
    return output.host();
  }

  template <class T> T asyncReduce(const std::vector<T> &input) {
    onedpl_detail::Shared<T> source(queue, input);
    const auto result = oneapi::dpl::reduce(policy<T, 0>(), source.begin(), source.end(), T{}, support::Plus<T>{});
    queue.wait_and_throw();
    return result;
  }

  template <class T> std::vector<T> asyncScan(const std::vector<T> &input) {
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> output(queue, input.size());
    oneapi::dpl::inclusive_scan(policy<T, 1>(), source.begin(), source.end(), output.begin(), support::Plus<T>{});
    queue.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> asyncSort(const std::vector<T> &input) {
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue, input);
    oneapi::dpl::sort(policy<T, 2>(), source.begin(), source.end());
    queue.wait_and_throw();
    return source.host();
  }

  template <class T> std::vector<T> asyncFutureChain(const std::vector<T> &input) {
    if (input.empty()) return {};
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> first(queue, input.size());
    onedpl_detail::Shared<T> output(queue, input.size());
    const auto count = input.size();
    const auto *sourceData = source.begin();
    auto *firstData = first.begin();
    auto *outputData = output.begin();
    const auto firstEvent = queue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::FutureStage<T, 0>>(
          count, [=](sycl::id<1> index) { firstData[index] = support::Affine<T>{}(sourceData[index]); });
    });
    auto secondEvent = queue.submit([&](sycl::handler &handler) {
      handler.depends_on(firstEvent);
      handler.parallel_for<onedpl_async_detail::FutureStage<T, 1>>(
          count, [=](sycl::id<1> index) { outputData[index] = support::Square<T>{}(firstData[index]); });
    });
    secondEvent.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> asyncEventChain(const std::vector<T> &input) {
    if (input.empty()) return {};
    const auto device = queue.get_device();
    const auto context = queue.get_context();
    sycl::queue leftQueue(context, device);
    sycl::queue rightQueue(context, device);
    sycl::queue joinQueue(context, device);
    onedpl_detail::Shared<T> source(queue, input);
    onedpl_detail::Shared<T> left(queue, input.size());
    onedpl_detail::Shared<T> right(queue, input.size());
    onedpl_detail::Shared<T> output(queue, input.size());
    const auto count = input.size();
    const auto *sourceData = source.begin();
    auto *leftData = left.begin();
    auto *rightData = right.begin();
    auto *outputData = output.begin();
    const auto leftEvent = leftQueue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::EventStage<T, 0>>(
          count, [=](sycl::id<1> index) { leftData[index] = support::Affine<T>{}(sourceData[index]); });
    });
    const auto rightEvent = rightQueue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::EventStage<T, 1>>(
          count, [=](sycl::id<1> index) { rightData[index] = support::Square<T>{}(sourceData[index]); });
    });
    auto joinEvent = joinQueue.submit([&](sycl::handler &handler) {
      handler.depends_on({leftEvent, rightEvent});
      handler.parallel_for<onedpl_async_detail::EventStage<T, 2>>(
          count, [=](sycl::id<1> index) { outputData[index] = support::Combine<T>{}(leftData[index], rightData[index]); });
    });
    joinEvent.wait_and_throw();
    return output.host();
  }

  template <class T> std::vector<T> asyncInterleavedStreams(const std::vector<T> &input) {
    std::vector<T> even;
    std::vector<T> odd;
    even.reserve((input.size() + 1) / 2);
    odd.reserve(input.size() / 2);
    for (size_t index = 0; index < input.size(); ++index)
      (index % 2 == 0 ? even : odd).push_back(input[index]);
    if (input.empty()) return {};

    const auto device = queue.get_device();
    const auto context = queue.get_context();
    sycl::queue evenQueue(context, device);
    sycl::queue oddQueue(context, device);
    onedpl_detail::Shared<T> evenData(queue, even);
    onedpl_detail::Shared<T> oddData(queue, odd);
    const auto evenCount = even.size();
    const auto oddCount = odd.size();
    auto *evenPtr = evenData.begin();
    auto *oddPtr = oddData.begin();
    auto evenEvent = evenQueue.submit([&](sycl::handler &handler) {
      handler.parallel_for<onedpl_async_detail::InterleavedStage<T, 0>>(
          evenCount, [=](sycl::id<1> index) { evenPtr[index] = support::Affine<T>{}(evenPtr[index]); });
    });
    evenEvent.wait_and_throw();
    if (oddCount != 0) {
      auto oddEvent = oddQueue.submit([&](sycl::handler &handler) {
        handler.parallel_for<onedpl_async_detail::InterleavedStage<T, 1>>(
            oddCount, [=](sycl::id<1> index) { oddPtr[index] = support::Square<T>{}(oddPtr[index]); });
      });
      oddEvent.wait_and_throw();
    }
    const auto transformedEven = evenData.host();
    const auto transformedOdd = oddData.host();
    std::vector<T> output(input.size());
    size_t evenIndex = 0;
    size_t oddIndex = 0;
    for (size_t index = 0; index < output.size(); ++index)
      output[index] = index % 2 == 0 ? transformedEven[evenIndex++] : transformedOdd[oddIndex++];
    return output;
  }

private:
  sycl::queue queue;

  template <class T, size_t Id> auto policy() {
    return oneapi::dpl::execution::make_device_policy<onedpl_async_detail::Kernel<T, Id>>(queue);
  }
};
} // namespace unified::backend

namespace unified::backend {
using Algorithms = OneDPLAlgorithms;
using EdgeCases = OneDPLEdgeCases;
using Async = OneDPLAsync;
} // namespace unified::backend
