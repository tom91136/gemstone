#include "test_support.hpp"

#define UNIFIED_API_ROCTHRUST 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_ROCTHRUST
#include <cstddef>
#include <cstdint>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

#include <magic_enum/magic_enum.hpp>

template <>
constexpr magic_enum::customize::customize_t
magic_enum::customize::enum_name<unified::rocthrust_support::SetKind>(unified::rocthrust_support::SetKind value) noexcept {
  switch (value) {
    case unified::rocthrust_support::SetKind::UnionSet: return "union_set";
    case unified::rocthrust_support::SetKind::Intersection: return "intersection";
    case unified::rocthrust_support::SetKind::Difference: return "difference";
    case unified::rocthrust_support::SetKind::SymmetricDifference: return "symmetric_difference";
  }
  return invalid_tag;
}

using namespace aspartame;
using namespace unified::rocthrust_support;

namespace {
template <class Value> inline void checkSetByKey(SetKind kind, bool descending) {
  const auto base = descending ? descendingKeys<Value>() : ascendingKeys<Value>();
  auto left = descendingKeyValues(base, 100);
  auto right = descendingKeyValues(base, 200);
  // Offset the second range while retaining sortedness.  This gives every
  // operation unmatched keys and runs with multiplicity greater than one.
  if (descending) {
    right.keys = {Value{7}, Value{7},  Value{4},  Value{3},  Value{2},  Value{2},
                  Value{1}, Value{-1}, Value{-1}, Value{-4}, Value{-5}, Value{-5}};
    right.values = payload(right.keys.size(), 200);
  } else {
    right.keys = {Value{-5}, Value{-4}, Value{-1}, Value{-1}, Value{1}, Value{2},
                  Value{2},  Value{3},  Value{4},  Value{4},  Value{7}, Value{7}};
    right.values = payload(right.keys.size(), 200);
  }
  const auto expected = referenceSetByKey(left, right, kind, descending);
  {
    INFO(fmt::format("set-{}{}", magic_enum::enum_name(kind), descending ? "-by-key-descending" : "-by-key"));
    REQUIRE(thrustSetByKey(left, right, kind, descending) == expected);
  }
}

template <class Value> inline void checkSetDescending(SetKind kind) {
  const std::vector<Value> left{Value{7}, Value{7}, Value{4}, Value{2}, Value{2}, Value{0}, Value{-1}, Value{-1}};
  const std::vector<Value> right{Value{7}, Value{3}, Value{3}, Value{2}, Value{0}, Value{-2}};
  const auto less = unified::algorithms::Greater<Value>{};
  std::vector<Value> expected(left.size() + right.size());
  auto expectedEnd = expected.begin();
  if (kind == SetKind::UnionSet) expectedEnd = std::set_union(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else if (kind == SetKind::Intersection)
    expectedEnd = std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else if (kind == SetKind::Difference)
    expectedEnd = std::set_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else expectedEnd = std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  expected.erase(expectedEnd, expected.end());

  const thrust::device_vector<Value> a(left.begin(), left.end());
  const thrust::device_vector<Value> b(right.begin(), right.end());
  thrust::device_vector<Value> output(left.size() + right.size());
  auto end = output.begin();
  if (kind == SetKind::UnionSet) end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else if (kind == SetKind::Intersection) end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else if (kind == SetKind::Difference) end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  output.resize(static_cast<size_t>(end - output.begin()));
  {
    INFO(fmt::format("set-{}-descending", magic_enum::enum_name(kind)));
    REQUIRE(std::vector<Value>(output.begin(), output.end()) == expected);
  }
}

template <class Value> inline void checkPairSet(SetKind kind) {
  const auto left = pairValues(ascendingKeys<Value>(), 100);
  auto right = pairValues(ascendingKeys<Value>(), 200);
  right.erase(right.begin() + 1);
  right.emplace_back(Value{-4}, 201);
  std::stable_sort(right.begin(), right.end(), PairKeyLess<Value>{});
  const auto expected = pairExpected(left, right, kind);
  const auto actual = thrustSetPairs(left, right, kind);
  {
    INFO(fmt::format("set-{}-key-value", magic_enum::enum_name(kind)));
    REQUIRE(actual == expected);
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/set-operations-duplicate-heavy/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> left{int32_t{0}, int32_t{0}, int32_t{0}, int32_t{0}, int32_t{1}, int32_t{1},
                                  int32_t{3}, int32_t{3}, int32_t{3}, int32_t{3}, int32_t{3}};
  const std::vector<int32_t> right{int32_t{0}, int32_t{0}, int32_t{1}, int32_t{1}, int32_t{1},
                                   int32_t{2}, int32_t{3}, int32_t{3}, int32_t{3}, int32_t{4}};
  const std::vector<int32_t> expectedUnion{int32_t{0}, int32_t{0}, int32_t{0}, int32_t{0}, int32_t{1}, int32_t{1}, int32_t{1},
                                           int32_t{2}, int32_t{3}, int32_t{3}, int32_t{3}, int32_t{3}, int32_t{3}, int32_t{4}};
  const std::vector<int32_t> expectedIntersection{int32_t{0}, int32_t{0}, int32_t{1}, int32_t{1}, int32_t{3}, int32_t{3}, int32_t{3}};
  const std::vector<int32_t> expectedDifference{int32_t{0}, int32_t{0}, int32_t{3}, int32_t{3}};
  const std::vector<int32_t> expectedSymmetric{int32_t{0}, int32_t{0}, int32_t{1}, int32_t{2}, int32_t{3}, int32_t{3}, int32_t{4}};
  const auto run = [&](SetKind kind, const std::vector<int32_t> &expected) {
    const thrust::device_vector<int32_t> a(left.begin(), left.end());
    const thrust::device_vector<int32_t> b(right.begin(), right.end());
    thrust::device_vector<int32_t> output(left.size() + right.size());
    auto end = output.begin();
    if (kind == SetKind::UnionSet) end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else if (kind == SetKind::Intersection) end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else if (kind == SetKind::Difference) end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    {
      INFO(fmt::format("set-{}-duplicate-heavy", magic_enum::enum_name(kind)));
      REQUIRE(std::vector<int32_t>(output.begin(), output.end()) == expected);
    }
  };
  run(SetKind::UnionSet, expectedUnion);
  run(SetKind::Intersection, expectedIntersection);
  run(SetKind::Difference, expectedDifference);
  run(SetKind::SymmetricDifference, expectedSymmetric);
}

TEST_CASE("algorithms/roc-thrust-semantic/binary-search-descending/i32/n11", "[unified][rocthrust][compact]") {
  const auto sorted = descendingKeys<int32_t>();
  const thrust::device_vector<int32_t> values(sorted.begin(), sorted.end());
  {
    INFO("binary-search-descending: present value not found");
    REQUIRE(thrust::binary_search(values.begin(), values.end(), int32_t{2}, unified::algorithms::Greater<int32_t>{}));
  }
  {
    INFO("binary-search-descending: absent value found");
    REQUIRE(!thrust::binary_search(values.begin(), values.end(), int32_t{6}, unified::algorithms::Greater<int32_t>{}));
  }
}

namespace {
template <class Value> inline void checkBinarySearchVector(bool descending) {
  auto sorted = ascendingKeys<Value>();
  if (descending) sorted ^= reverse();
  const std::vector<Value> needles{Value{-6}, Value{-5}, Value{-2}, Value{-1}, Value{2}, Value{6}, Value{7}};
  const thrust::device_vector<Value> values(sorted.begin(), sorted.end());
  const thrust::device_vector<Value> queries(needles.begin(), needles.end());
  thrust::device_vector<int32_t> output(needles.size());
  if (descending)
    thrust::binary_search(values.begin(), values.end(), queries.begin(), queries.end(), output.begin(),
                          unified::algorithms::Greater<Value>{});
  else thrust::binary_search(values.begin(), values.end(), queries.begin(), queries.end(), output.begin());
  const auto expected = needles ^ map([&](Value needle) {
                          const bool found =
                              descending ? std::binary_search(sorted.begin(), sorted.end(), needle, unified::algorithms::Greater<Value>{})
                                         : std::binary_search(sorted.begin(), sorted.end(), needle, unified::algorithms::Less<Value>{});
                          return found ? 1 : 0;
                        });
  {
    INFO((descending ? "binary-search-vector-descending" : "binary-search-vector"));
    REQUIRE(std::vector<int32_t>(output.begin(), output.end()) == expected);
  }
}

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/gather-repeated/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> input{int32_t{11}, int32_t{22}, int32_t{33}, int32_t{44}, int32_t{55}, int32_t{66}, int32_t{77}, int32_t{88}};
  const std::vector<size_t> indices{7, 2, 2, 0, 6, 3, 7, 1};
  const thrust::device_vector<int32_t> source(input.begin(), input.end());
  const thrust::device_vector<size_t> positions(indices.begin(), indices.end());
  thrust::device_vector<int32_t> output(indices.size());
  thrust::gather(positions.begin(), positions.end(), source.begin(), output.begin());
  const auto expected = indices ^ map([&](size_t index) { return input[index]; });
  {
    INFO("gather-repeated");
    REQUIRE(std::vector<int32_t>(output.begin(), output.end()) == expected);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/permutation-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> input{int32_t{13}, int32_t{21}, int32_t{34}, int32_t{55}, int32_t{89}, int32_t{144}};
  const std::vector<size_t> indices{4, 1, 5, 1, 3, 0, 2};
  const thrust::device_vector<int32_t> source(input.begin(), input.end());
  const thrust::device_vector<size_t> positions(indices.begin(), indices.end());
  const auto first = thrust::make_permutation_iterator(source.begin(), positions.begin());
  thrust::device_vector<int32_t> output(indices.size());
  thrust::copy(first, first + static_cast<ptrdiff_t>(indices.size()), output.begin());
  const auto expected = indices ^ map([&](size_t index) { return input[index]; });
  {
    INFO("permutation-iterator");
    REQUIRE(std::vector<int32_t>(output.begin(), output.end()) == expected);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> keys{int32_t{-3}, int32_t{0}, int32_t{4}, int32_t{9}};
  const std::vector<int32_t> values{7, 2, 8, 1};
  const thrust::device_vector<int32_t> deviceKeys(keys.begin(), keys.end());
  const thrust::device_vector<int32_t> deviceValues(values.begin(), values.end());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(deviceKeys.begin(), deviceValues.begin()));
  const auto last = first + static_cast<ptrdiff_t>(keys.size());
  thrust::device_vector<int64_t> output(keys.size());
  thrust::transform(first, last, output.begin(), TupleCode<int32_t>{});
  const auto expected = keys ^ zip(values) ^ map([](int32_t key, int32_t value) { return (static_cast<int64_t>(key) * 1000) + value; });
  {
    INFO("zip-iterator");
    REQUIRE(std::vector<int64_t>(output.begin(), output.end()) == expected);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-sort/i32/n11", "[unified][rocthrust][compact]") {
  std::vector<int32_t> keys{int32_t{3}, int32_t{-1}, int32_t{3}, int32_t{0}, int32_t{-1}};
  std::vector<int32_t> values{30, 11, 31, 2, 10};
  thrust::device_vector<int32_t> deviceKeys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> deviceValues(values.begin(), values.end());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(deviceKeys.begin(), deviceValues.begin()));
  thrust::sort(first, first + static_cast<ptrdiff_t>(keys.size()), TupleLess<int32_t>{});
  const auto actual =
      iota(size_t{0}, keys.size()) | map([&](size_t index) { return Pair<int32_t>(deviceKeys[index], deviceValues[index]); }) | to_vector();
  const auto expected =
      keys ^ zip(values) ^ map([](int32_t key, int32_t value) { return Pair<int32_t>(key, value); }) ^ sort(PairLess<int32_t>{});
  {
    INFO("zip-iterator-sort");
    REQUIRE(actual == expected);
  }
}

namespace {
template <class Value> struct TupleSquare {
  UNIFIED_ROCTHRUST_HOST_DEVICE Value operator()(Value value) const { return value * value; }
};

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/tuple-transform/i32/n11", "[unified][rocthrust][compact]") {
  const auto input = std::make_tuple(int32_t{-3}, int32_t{2}, int32_t{5});
  const auto actual = thrust::tuple_transform(input, TupleSquare<int32_t>{});
  {
    INFO("tuple-transform[0] mismatch");
    REQUIRE(std::get<0>(actual) == int32_t{9});
  }
  {
    INFO("tuple-transform[1] mismatch");
    REQUIRE(std::get<1>(actual) == int32_t{4});
  }
  {
    INFO("tuple-transform[2] mismatch");
    REQUIRE(std::get<2>(actual) == int32_t{25});
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/tuple-sort/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<int32_t, int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(int32_t{3}, 30), thrust::make_tuple(int32_t{-1}, 11),
                                 thrust::make_tuple(int32_t{3}, 31), thrust::make_tuple(int32_t{0}, 2),
                                 thrust::make_tuple(int32_t{-1}, 10)};
  thrust::device_vector<Tuple> values(input.begin(), input.end());
  thrust::sort(values.begin(), values.end(), TupleLess<int32_t>{});
  const std::vector<Tuple> actual(values.begin(), values.end());
  const std::vector<Tuple> expected{thrust::make_tuple(int32_t{-1}, 10), thrust::make_tuple(int32_t{-1}, 11),
                                    thrust::make_tuple(int32_t{0}, 2), thrust::make_tuple(int32_t{3}, 30),
                                    thrust::make_tuple(int32_t{3}, 31)};
  {
    INFO("tuple-sort: result size mismatch");
    REQUIRE(actual.size() == expected.size());
  }
  for (size_t index = 0; index < expected.size(); ++index) {
    {
      INFO(fmt::format("tuple-sort.key[{}] mismatch", index));
      REQUIRE(thrust::get<0>(actual[index]) == thrust::get<0>(expected[index]));
    }
    {
      INFO(fmt::format("tuple-sort.value[{}] mismatch", index));
      REQUIRE(thrust::get<1>(actual[index]) == thrust::get<1>(expected[index]));
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/pair-sort/i32/n11", "[unified][rocthrust][compact]") {
  auto expected = pairValues(ascendingKeys<int32_t>(), 300);
  std::reverse(expected.begin(), expected.end());
  const auto original = expected;
  thrust::device_vector<Pair<int32_t>> values(original.begin(), original.end());
  thrust::sort(values.begin(), values.end(), PairLess<int32_t>{});
  std::sort(expected.begin(), expected.end(), PairLess<int32_t>{});
  {
    INFO("pair-sort");
    REQUIRE(hostPairs(values, values.size()) == expected);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/pair-sort-by-key/i32/n11", "[unified][rocthrust][compact]") {
  // Keep keys unique here so the plain (unstable) sort-by-key overload has a
  // deterministic payload association to validate.
  const std::vector<int32_t> keys{int32_t{9}, int32_t{1}, int32_t{8}, int32_t{2}, int32_t{7}, int32_t{3},
                                  int32_t{6}, int32_t{4}, int32_t{5}, int32_t{0}, int32_t{-1}};
  const auto values = payload(keys.size(), 400);
  thrust::device_vector<int32_t> deviceKeys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> deviceValues(values.begin(), values.end());
  thrust::sort_by_key(deviceKeys.begin(), deviceKeys.end(), deviceValues.begin(), unified::algorithms::Less<int32_t>{});
  const std::vector<int32_t> actualKeys(deviceKeys.begin(), deviceKeys.end());
  const std::vector<int32_t> actualValues(deviceValues.begin(), deviceValues.end());
  {
    INFO("pair-sort-by-key: keys are not sorted");
    REQUIRE(std::is_sorted(actualKeys.begin(), actualKeys.end()));
  }
  const auto expected = keys ^ zip(values) ^ sort();
  for (size_t index = 0; index < expected.size(); ++index) {
    {
      INFO("pair-sort-by-key: key association mismatch");
      REQUIRE(actualKeys[index] == expected[index].first);
    }
    {
      INFO("pair-sort-by-key: value association mismatch");
      REQUIRE(actualValues[index] == expected[index].second);
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/pair-scan/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<Pair<int32_t>> input{{int32_t{1}, 2}, {int32_t{-3}, 4}, {int32_t{5}, -1}, {int32_t{2}, 7}};
  const thrust::device_vector<Pair<int32_t>> source(input.begin(), input.end());
  thrust::device_vector<Pair<int32_t>> output(input.size());
  thrust::inclusive_scan(source.begin(), source.end(), output.begin(), PairPlus<int32_t>{});
  const auto expected = input | scan_left(Pair<int32_t>{int32_t{}, 0}, PairPlus<int32_t>{}) | drop(1) | to_vector();
  {
    INFO("pair-scan");
    REQUIRE((std::vector<Pair<int32_t>>(output.begin(), output.end())) == expected);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/pair-reduce/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<Pair<int32_t>> input{{int32_t{1}, 2}, {int32_t{-3}, 4}, {int32_t{5}, -1}, {int32_t{2}, 7}};
  const thrust::device_vector<Pair<int32_t>> source(input.begin(), input.end());
  const auto actual = thrust::reduce(source.begin(), source.end(), Pair<int32_t>{int32_t{}, 0}, PairPlus<int32_t>{});
  const auto expected = input ^ fold_left(Pair<int32_t>{int32_t{}, 0}, PairPlus<int32_t>{});
  {
    INFO("pair-reduce mismatch");
    REQUIRE((actual.first == expected.first && actual.second == expected.second));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/tuple-scan/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<int32_t, int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(int32_t{1}, 2), thrust::make_tuple(int32_t{-3}, 4), thrust::make_tuple(int32_t{5}, -1),
                                 thrust::make_tuple(int32_t{2}, 7)};
  const thrust::device_vector<Tuple> source(input.begin(), input.end());
  thrust::device_vector<Tuple> output(input.size());
  thrust::inclusive_scan(source.begin(), source.end(), output.begin(), TuplePlus<int32_t>{});
  const std::vector<Tuple> actual(output.begin(), output.end());
  int32_t first = int32_t{};
  int32_t second = 0;
  for (size_t index = 0; index < input.size(); ++index) {
    first += thrust::get<0>(input[index]);
    second += thrust::get<1>(input[index]);
    {
      INFO(fmt::format("tuple-scan mismatch at {}", index));
      REQUIRE((thrust::get<0>(actual[index]) == first && thrust::get<1>(actual[index]) == second));
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/tuple-reduce/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<int32_t, int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(int32_t{1}, 2), thrust::make_tuple(int32_t{-3}, 4), thrust::make_tuple(int32_t{5}, -1),
                                 thrust::make_tuple(int32_t{2}, 7)};
  const thrust::device_vector<Tuple> source(input.begin(), input.end());
  const auto actual = thrust::reduce(source.begin(), source.end(), thrust::make_tuple(int32_t{}, 0), TuplePlus<int32_t>{});
  int32_t expectedFirst = int32_t{};
  int32_t expectedSecond = 0;
  for (const auto value : input) {
    expectedFirst += thrust::get<0>(value);
    expectedSecond += thrust::get<1>(value);
  }
  {
    INFO("tuple-reduce mismatch");
    REQUIRE((thrust::get<0>(actual) == expectedFirst && thrust::get<1>(actual) == expectedSecond));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-scan/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> keys{int32_t{1}, int32_t{-3}, int32_t{5}, int32_t{2}};
  const std::vector<int32_t> values{2, 4, -1, 7};
  const thrust::device_vector<int32_t> deviceKeys(keys.begin(), keys.end());
  const thrust::device_vector<int32_t> deviceValues(values.begin(), values.end());
  using Tuple = thrust::tuple<int32_t, int32_t>;
  thrust::device_vector<Tuple> output(keys.size());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(deviceKeys.begin(), deviceValues.begin()));
  thrust::inclusive_scan(first, first + static_cast<ptrdiff_t>(keys.size()), output.begin(), TuplePlus<int32_t>{});
  const std::vector<Tuple> actual(output.begin(), output.end());
  int32_t expectedKey = int32_t{};
  int32_t expectedValue = 0;
  for (size_t index = 0; index < keys.size(); ++index) {
    expectedKey += keys[index];
    expectedValue += values[index];
    {
      INFO(fmt::format("zip-iterator-scan mismatch at {}", index));
      REQUIRE((thrust::get<0>(actual[index]) == expectedKey && thrust::get<1>(actual[index]) == expectedValue));
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/transform-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> input{int32_t{-3}, int32_t{-1}, int32_t{2}, int32_t{5}, int32_t{7}};
  const thrust::device_vector<int32_t> source(input.begin(), input.end());
  thrust::device_vector<int32_t> output(input.size());
  const auto first = thrust::make_transform_iterator(source.begin(), Square<int32_t>{});
  thrust::copy(first, first + static_cast<ptrdiff_t>(input.size()), output.begin());
  const auto expected = input ^ map([](int32_t value) { return value * value; });
  {
    INFO("transform-iterator");
    REQUIRE(std::vector<int32_t>(output.begin(), output.end()) == expected);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/transform-output-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> input{int32_t{-3}, int32_t{-1}, int32_t{2}, int32_t{5}, int32_t{7}};
  const thrust::device_vector<int32_t> source(input.begin(), input.end());
  thrust::device_vector<int32_t> output(input.size());
  const auto destination = thrust::make_transform_output_iterator(output.begin(), Square<int32_t>{});
  thrust::copy(source.begin(), source.end(), destination);
  const auto expected = input ^ map([](int32_t value) { return value * value; });
  {
    INFO("transform-output-iterator");
    REQUIRE(std::vector<int32_t>(output.begin(), output.end()) == expected);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/transform-input-output-iterator/i32/n11", "[unified][rocthrust][compact]") {
  constexpr size_t size = 5;
  thrust::device_vector<int32_t> values(size, int32_t{});
  const auto io = thrust::make_transform_input_output_iterator(values.begin(), Negate<int32_t>{}, Square<int32_t>{});
  const auto first = thrust::make_counting_iterator<int32_t>(int32_t{1});
  thrust::copy(first, first + static_cast<ptrdiff_t>(size), io);
  for (size_t index = 0; index < size; ++index) {
    INFO(fmt::format("transform-input-output write mismatch at {}", index));
    REQUIRE(std::cmp_equal(static_cast<int32_t>(values[index]), (index + 1) * (index + 1)));
  }
  thrust::device_vector<int32_t> readback(size);
  thrust::copy(io, io + static_cast<ptrdiff_t>(size), readback.begin());
  for (size_t index = 0; index < size; ++index) {
    INFO(fmt::format("transform-input-output read mismatch at {}", index));
    REQUIRE(readback[index] == -values[index]);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-sort-by-key/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> keys{int32_t{9}, int32_t{1}, int32_t{8}, int32_t{2}, int32_t{7}};
  const std::vector<int32_t> values{90, 10, 80, 20, 70};
  const std::vector<int32_t> tags{9, 1, 8, 2, 7};
  thrust::device_vector<int32_t> deviceKeys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> deviceValues(values.begin(), values.end());
  thrust::device_vector<int32_t> deviceTags(tags.begin(), tags.end());
  const auto zipped = thrust::make_zip_iterator(thrust::make_tuple(deviceValues.begin(), deviceTags.begin()));
  thrust::sort_by_key(deviceKeys.begin(), deviceKeys.end(), zipped);
  for (size_t index = 0; index < keys.size(); ++index) {
    const auto key = deviceKeys[index];
    INFO(fmt::format("zip-iterator-sort-by-key association mismatch at {}", index));
    REQUIRE((deviceValues[index] == static_cast<int32_t>(key * 10) && deviceTags[index] == static_cast<int32_t>(key)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/set-union-by-key/i32/n11", "[unified][rocthrust][compact]") {
  checkSetByKey<int32_t>(SetKind::UnionSet, false);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-intersection-by-key/i32/n11", "[unified][rocthrust][compact]") {
  checkSetByKey<int32_t>(SetKind::Intersection, false);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-difference-by-key/i32/n11", "[unified][rocthrust][compact]") {
  checkSetByKey<int32_t>(SetKind::Difference, false);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-symmetric-difference-by-key/i32/n11", "[unified][rocthrust][compact]") {
  checkSetByKey<int32_t>(SetKind::SymmetricDifference, false);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-union-by-key-descending/i32/n11", "[unified][rocthrust][compact]") {
  checkSetByKey<int32_t>(SetKind::UnionSet, true);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-intersection-by-key-descending/i32/n11", "[unified][rocthrust][compact]") {
  checkSetByKey<int32_t>(SetKind::Intersection, true);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-difference-by-key-descending/i32/n11", "[unified][rocthrust][compact]") {
  checkSetByKey<int32_t>(SetKind::Difference, true);
}

TEST_CASE("algorithms/roc-thrust-semantic/"
          "set-symmetric-difference-by-key-descending/i32/n11",
          "[unified][rocthrust][compact]") {
  checkSetByKey<int32_t>(SetKind::SymmetricDifference, true);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-union-descending/i32/n11", "[unified][rocthrust][compact]") {
  checkSetDescending<int32_t>(SetKind::UnionSet);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-intersection-descending/i32/n11", "[unified][rocthrust][compact]") {
  checkSetDescending<int32_t>(SetKind::Intersection);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-difference-descending/i32/n11", "[unified][rocthrust][compact]") {
  checkSetDescending<int32_t>(SetKind::Difference);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-symmetric-difference-descending/"
          "i32/n11",
          "[unified][rocthrust][compact]") {
  checkSetDescending<int32_t>(SetKind::SymmetricDifference);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-union-key-value/i32/n11", "[unified][rocthrust][compact]") {
  checkPairSet<int32_t>(SetKind::UnionSet);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-intersection-key-value/i32/n11", "[unified][rocthrust][compact]") {
  checkPairSet<int32_t>(SetKind::Intersection);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-difference-key-value/i32/n11", "[unified][rocthrust][compact]") {
  checkPairSet<int32_t>(SetKind::Difference);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-symmetric-difference-key-value/i32/n11", "[unified][rocthrust][compact]") {
  checkPairSet<int32_t>(SetKind::SymmetricDifference);
}

TEST_CASE("algorithms/roc-thrust-semantic/binary-search-vector/i32/n11", "[unified][rocthrust][compact]") {
  checkBinarySearchVector<int32_t>(false);
}

TEST_CASE("algorithms/roc-thrust-semantic/binary-search-vector-descending/i32/n11", "[unified][rocthrust][compact]") {
  checkBinarySearchVector<int32_t>(true);
}
