
#include <algorithm>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <memory>
#include <numeric>
#include <random>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_template_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "algorithm_cases.hpp"
#include "async_cases.hpp"
#include "blas_cases.hpp"
#include "capability_defaults.hpp"
#include "edge_cases.hpp"
#include "reference_async.hpp"
#include "stress_cases.hpp"
#include <magic_enum/magic_enum.hpp>

using namespace aspartame;

namespace unified::test {
class ReferenceEdgeCases : public unified::backend::EdgeCapabilities<ReferenceEdgeCases> {
public:
  template <class T> bool capability() const { return true; }

  template <class T> int64_t composed_iterator_reduce(size_t size) {
    int64_t result = 0;
    for (size_t index = 0; index < size; ++index)
      result += edge::WidePairValue<T>{}(edge::ValueAt<T>{}(size - index - 1), edge::ValueAt<T>{}(index));
    return result;
  }

  template <class T> uint64_t biased_index_checksum(uint64_t base, size_t size) {
    uint64_t result = 0;
    for (size_t index = 0; index < size; ++index)
      result ^= edge::BiasedHash<T>{}(base + index);
    return result;
  }

  template <class T> std::vector<T> segmented_inclusive_scan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
    auto output = input;
    for (size_t index = 1; index < output.size(); ++index)
      if (keys[index] == keys[index - 1]) output[index] = support::Plus<T>{}(output[index - 1], input[index]);
    return output;
  }

  template <class T> std::vector<T> inplace_inclusive_scan(const std::vector<T> &storage, size_t offset, size_t count) {
    auto output = storage;
    if (count != 0)
      std::inclusive_scan(output.begin() + static_cast<ptrdiff_t>(offset), output.begin() + static_cast<ptrdiff_t>(offset + count),
                          output.begin() + static_cast<ptrdiff_t>(offset), support::Plus<T>{});
    return output;
  }

  template <class T> support::KeyValues<T> stable_sort_payload(const std::vector<T> &keys, const std::vector<int32_t> &payload) {
    std::vector<size_t> order(keys.size());
    std::iota(order.begin(), order.end(), 0);
    std::stable_sort(order.begin(), order.end(), [&](auto left, auto right) { return keys[left] < keys[right]; });
    auto [result_keys, result_values] = order ^ map([&](size_t index) { return std::pair{keys[index], payload[index]}; }) ^ unzip();
    return {std::move(result_keys), std::move(result_values)};
  }

  template <class T> std::vector<T> dependency_diamond(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform(input.begin(), input.end(), output.begin(),
                   [](T value) { return support::Combine<T>{}(support::Affine<T>{}(value), support::Square<T>{}(value)); });
    return output;
  }

  template <class T> std::vector<T> numeric_edge_roundtrip(const std::vector<T> &input) { return input; }

  template <class T> std::vector<T> primitive_block_scan(const std::vector<T> &input, size_t block_size) {
    auto output = input;
    for (size_t index = 0; index < output.size(); ++index)
      if (index % block_size != 0) output[index] = support::Plus<T>{}(output[index - 1], input[index]);
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
} // namespace unified::test

#define DECLARE_REFERENCE_EDGE_CASES(name, check)                                                                                          \
  TEMPLATE_TEST_CASE("reference edge cases/" name, "[unit][unified]", int8_t, int32_t, int64_t, float, double) {                           \
    const auto size = GENERATE(0, 1, 7, 33, 257);                                                                                          \
    CAPTURE(size);                                                                                                                         \
    unified::test::ReferenceEdgeCases backend;                                                                                             \
    check<TestType>(backend, size);                                                                                                        \
  }

DECLARE_REFERENCE_EDGE_CASES("composed_iterator_reduce", unified::edge::composed_iterator_reduce)
DECLARE_REFERENCE_EDGE_CASES("biased_index_64", unified::edge::biased_index_64)
DECLARE_REFERENCE_EDGE_CASES("segments_singleton", unified::edge::segments_singleton)
DECLARE_REFERENCE_EDGE_CASES("segments_giant", unified::edge::segments_giant)
DECLARE_REFERENCE_EDGE_CASES("segments_boundary", unified::edge::segments_boundary)
DECLARE_REFERENCE_EDGE_CASES("inplace_scan_canary", unified::edge::inplace_scan_canary)
DECLARE_REFERENCE_EDGE_CASES("stable_sort_payload", unified::edge::stable_sort_payload)
DECLARE_REFERENCE_EDGE_CASES("dependency_diamond", unified::edge::dependency_diamond)
DECLARE_REFERENCE_EDGE_CASES("numeric_edge_roundtrip", unified::edge::numeric_edge_roundtrip)
DECLARE_REFERENCE_EDGE_CASES("primitive_block_scan", unified::edge::primitive_block_scan)
DECLARE_REFERENCE_EDGE_CASES("runtime_error_recover", unified::edge::runtime_error_recover)

#undef DECLARE_REFERENCE_EDGE_CASES
