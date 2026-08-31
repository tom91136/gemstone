#ifndef UNIFIED_SUITE_ADAPTERS_HOST_EDGE_CASES_HPP
#define UNIFIED_SUITE_ADAPTERS_HOST_EDGE_CASES_HPP

#include "unified_suite/edge_cases.hpp"

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <numeric>
#include <vector>

namespace unified_suite::adapters {

class HostEdgeCases {
public:
  template <class T> bool supports(edge_cases::Operation) const { return true; }

  template <class T> std::int64_t composed_iterator_reduce(std::size_t size) {
    std::int64_t result = 0;
    for (std::size_t index = 0; index < size; ++index)
      result += edge_cases::WidePairValue<T>{}(edge_cases::ValueAt<T>{}(size - index - 1),
                                               edge_cases::ValueAt<T>{}(index));
    return result;
  }

  template <class T> std::uint64_t biased_index_checksum(std::uint64_t base, std::size_t size) {
    std::uint64_t result = 0;
    for (std::size_t index = 0; index < size; ++index)
      result ^= edge_cases::BiasedHash<T>{}(base + index);
    return result;
  }

  template <class T>
  std::vector<T> segmented_inclusive_scan(const std::vector<std::int32_t> &keys,
                                          const std::vector<T> &input) {
    auto output = input;
    for (std::size_t index = 1; index < output.size(); ++index)
      if (keys[index] == keys[index - 1])
        output[index] = algorithms::Plus<T>{}(output[index - 1], input[index]);
    return output;
  }

  template <class T>
  std::vector<T> inplace_inclusive_scan(const std::vector<T> &storage, std::size_t offset,
                                        std::size_t count) {
    auto output = storage;
    if (count != 0)
      std::inclusive_scan(output.begin() + static_cast<std::ptrdiff_t>(offset),
                          output.begin() + static_cast<std::ptrdiff_t>(offset + count),
                          output.begin() + static_cast<std::ptrdiff_t>(offset), algorithms::Plus<T>{});
    return output;
  }

  template <class T>
  algorithms::KeyValues<T> stable_sort_payload(const std::vector<T> &keys,
                                               const std::vector<std::int32_t> &payload) {
    std::vector<std::size_t> order(keys.size());
    std::iota(order.begin(), order.end(), 0);
    std::stable_sort(order.begin(), order.end(),
                     [&](auto left, auto right) { return keys[left] < keys[right]; });
    algorithms::KeyValues<T> result;
    result.keys.reserve(keys.size());
    result.values.reserve(keys.size());
    for (const auto index : order) {
      result.keys.push_back(keys[index]);
      result.values.push_back(payload[index]);
    }
    return result;
  }

  template <class T> std::vector<T> dependency_diamond(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform(input.begin(), input.end(), output.begin(), [](T value) {
      return algorithms::Combine<T>{}(algorithms::Affine<T>{}(value), algorithms::Square<T>{}(value));
    });
    return output;
  }

  template <class T> std::vector<T> numeric_edge_roundtrip(const std::vector<T> &input) { return input; }

  template <class T>
  std::vector<T> primitive_block_scan(const std::vector<T> &input, std::size_t block_size) {
    auto output = input;
    for (std::size_t index = 0; index < output.size(); ++index)
      if (index % block_size != 0)
        output[index] = algorithms::Plus<T>{}(output[index - 1], input[index]);
    return output;
  }

  template <class T> std::vector<T> runtime_error_recover(const std::vector<T> &input) {
    try {
      throw std::bad_alloc();
    } catch (const std::bad_alloc &) {
      return input;
    }
  }
};

} // namespace unified_suite::adapters

#endif
