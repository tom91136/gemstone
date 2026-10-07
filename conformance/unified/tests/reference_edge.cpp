#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <new>
#include <numeric>
#include <utility>
#include <vector>

#include <aspartame/all.hpp>
#include <catch2/catch_template_test_macros.hpp>

#include "cases/edge_stress.inl"

using namespace aspartame;

namespace unified::test {
class ReferenceEdgeCases {
public:
  template <class T> int64_t composedIteratorReduce(size_t size) {
    int64_t result = 0;
    for (size_t index = 0; index < size; ++index)
      result += edge::WidePairValue<T>{}(edge::ValueAt<T>{}(size - index - 1), edge::ValueAt<T>{}(index));
    return result;
  }

  template <class T> uint64_t biasedIndexChecksum(uint64_t base, size_t size) {
    uint64_t result = 0;
    for (size_t index = 0; index < size; ++index)
      result ^= edge::BiasedHash<T>{}(base + index);
    return result;
  }

  template <class T> std::vector<T> segmentedInclusiveScan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
    auto output = input;
    for (size_t index = 1; index < output.size(); ++index)
      if (keys[index] == keys[index - 1]) output[index] = support::Plus<T>{}(output[index - 1], input[index]);
    return output;
  }

  template <class T> std::vector<T> inplaceInclusiveScan(const std::vector<T> &storage, size_t offset, size_t count) {
    auto output = storage;
    if (count != 0)
      std::inclusive_scan(output.begin() + static_cast<ptrdiff_t>(offset), output.begin() + static_cast<ptrdiff_t>(offset + count),
                          output.begin() + static_cast<ptrdiff_t>(offset), support::Plus<T>{});
    return output;
  }

  template <class T> support::KeyValues<T> stableSortPayload(const std::vector<T> &keys, const std::vector<int32_t> &payload) {
    std::vector<size_t> order(keys.size());
    std::iota(order.begin(), order.end(), 0);
    std::stable_sort(order.begin(), order.end(), [&](auto left, auto right) { return keys[left] < keys[right]; });
    auto [result_keys, result_values] = order ^ map([&](size_t index) { return std::pair{keys[index], payload[index]}; }) ^ unzip();
    return {std::move(result_keys), std::move(result_values)};
  }

  template <class T> std::vector<T> dependencyDiamond(const std::vector<T> &input) {
    std::vector<T> output(input.size());
    std::transform(input.begin(), input.end(), output.begin(),
                   [](T value) { return support::Combine<T>{}(support::Affine<T>{}(value), support::Square<T>{}(value)); });
    return output;
  }

  template <class T> std::vector<T> numericEdgeRoundtrip(const std::vector<T> &input) { return input; }

  template <class T> std::vector<T> primitiveBlockScan(const std::vector<T> &input, size_t blockSize) {
    auto output = input;
    for (size_t index = 0; index < output.size(); ++index)
      if (index % blockSize != 0) output[index] = support::Plus<T>{}(output[index - 1], input[index]);
    return output;
  }

  template <class T> std::vector<T> runtimeErrorRecover(const std::vector<T> &input) {
    try {
      throw std::bad_alloc();
    } catch (const std::bad_alloc &) {
      return input;
    }
  }
};
} // namespace unified::test

#define REFERENCE_EDGE_CASE(name, function, ...)                                                                                           \
  TEMPLATE_TEST_CASE("reference edge cases/" #name, "[unit][unified]", int8_t, int32_t, int64_t, float, double) {                          \
    const auto size = GENERATE(0, 1, 7, 33, 257);                                                                                          \
    CAPTURE(size);                                                                                                                         \
    unified::test::ReferenceEdgeCases backend;                                                                                             \
    unified::edge::function<TestType>(backend, size);                                                                                      \
  }

UNIFIED_EDGE_CASES(REFERENCE_EDGE_CASE)
