#include "test_support.hpp"

#define UNIFIED_API_ROCTHRUST 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_ROCTHRUST
#include <cstddef>
#include <cstdint>

#include <catch2/catch_test_macros.hpp>

#include <magic_enum/magic_enum.hpp>

using namespace unified::rocthrust_support;
using TestType = int32_t;

template <class Value> inline void check_set_by_key(SetKind kind, bool descending) {
  const auto base = descending ? descending_keys<Value>() : ascending_keys<Value>();
  auto left = descending_key_values(base, 100);
  auto right = descending_key_values(base, 200);
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
  const auto expected = reference_set_by_key(left, right, kind, descending);
  {
    INFO(fmt::format("set-{}{}", magic_enum::enum_name(kind), descending ? "-by-key-descending" : "-by-key"));
    REQUIRE(((thrust_set_by_key(left, right, kind, descending)) == (expected)));
  }
}

template <class Value> inline void check_set_descending(SetKind kind) {
  const std::vector<Value> left{Value{7}, Value{7}, Value{4}, Value{2}, Value{2}, Value{0}, Value{-1}, Value{-1}};
  const std::vector<Value> right{Value{7}, Value{3}, Value{3}, Value{2}, Value{0}, Value{-2}};
  const auto less = unified::algorithms::Greater<Value>{};
  std::vector<Value> expected(left.size() + right.size());
  auto expected_end = expected.begin();
  if (kind == SetKind::union_set)
    expected_end = std::set_union(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else if (kind == SetKind::intersection)
    expected_end = std::set_intersection(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else if (kind == SetKind::difference)
    expected_end = std::set_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  else expected_end = std::set_symmetric_difference(left.begin(), left.end(), right.begin(), right.end(), expected.begin(), less);
  expected.erase(expected_end, expected.end());

  const thrust::device_vector<Value> a(left.begin(), left.end());
  const thrust::device_vector<Value> b(right.begin(), right.end());
  thrust::device_vector<Value> output(left.size() + right.size());
  auto end = output.begin();
  if (kind == SetKind::union_set) end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else if (kind == SetKind::intersection) end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else if (kind == SetKind::difference) end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  else end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin(), less);
  output.resize(static_cast<size_t>(end - output.begin()));
  {
    INFO(fmt::format("set-{}-descending", magic_enum::enum_name(kind)));
    REQUIRE(((std::vector<Value>(output.begin(), output.end())) == (expected)));
  }
}

template <class Value> inline void check_pair_set(SetKind kind) {
  const auto left = pair_values(ascending_keys<Value>(), 100);
  auto right = pair_values(ascending_keys<Value>(), 200);
  right.erase(right.begin() + 1);
  right.emplace_back(Value{-4}, 201);
  std::stable_sort(right.begin(), right.end(), PairKeyLess<Value>{});
  const auto expected = pair_expected(left, right, kind);
  const auto actual = thrust_set_pairs(left, right, kind);
  {
    INFO(fmt::format("set-{}-key-value", magic_enum::enum_name(kind)));
    REQUIRE(((actual) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/set-operations-duplicate-heavy/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> left{TestType{0}, TestType{0}, TestType{0}, TestType{0}, TestType{1}, TestType{1},
                                   TestType{3}, TestType{3}, TestType{3}, TestType{3}, TestType{3}};
  const std::vector<TestType> right{TestType{0}, TestType{0}, TestType{1}, TestType{1}, TestType{1},
                                    TestType{2}, TestType{3}, TestType{3}, TestType{3}, TestType{4}};
  const std::vector<TestType> expected_union{TestType{0}, TestType{0}, TestType{0}, TestType{0}, TestType{1}, TestType{1}, TestType{1},
                                             TestType{2}, TestType{3}, TestType{3}, TestType{3}, TestType{3}, TestType{3}, TestType{4}};
  const std::vector<TestType> expected_intersection{TestType{0}, TestType{0}, TestType{1}, TestType{1},
                                                    TestType{3}, TestType{3}, TestType{3}};
  const std::vector<TestType> expected_difference{TestType{0}, TestType{0}, TestType{3}, TestType{3}};
  const std::vector<TestType> expected_symmetric{TestType{0}, TestType{0}, TestType{1}, TestType{2}, TestType{3}, TestType{3}, TestType{4}};
  const auto run = [&](SetKind kind, const std::vector<TestType> &expected) {
    const thrust::device_vector<TestType> a(left.begin(), left.end());
    const thrust::device_vector<TestType> b(right.begin(), right.end());
    thrust::device_vector<TestType> output(left.size() + right.size());
    auto end = output.begin();
    if (kind == SetKind::union_set) end = thrust::set_union(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else if (kind == SetKind::intersection) end = thrust::set_intersection(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else if (kind == SetKind::difference) end = thrust::set_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    else end = thrust::set_symmetric_difference(a.begin(), a.end(), b.begin(), b.end(), output.begin());
    output.resize(static_cast<size_t>(end - output.begin()));
    {
      INFO(fmt::format("set-{}-duplicate-heavy", magic_enum::enum_name(kind)));
      REQUIRE(((std::vector<TestType>(output.begin(), output.end())) == (expected)));
    }
  };
  run(SetKind::union_set, expected_union);
  run(SetKind::intersection, expected_intersection);
  run(SetKind::difference, expected_difference);
  run(SetKind::symmetric_difference, expected_symmetric);
}

TEST_CASE("algorithms/roc-thrust-semantic/binary-search-descending/i32/n11", "[unified][rocthrust][compact]") {
  const auto sorted = descending_keys<TestType>();
  const thrust::device_vector<TestType> values(sorted.begin(), sorted.end());
  {
    INFO("binary-search-descending: present value not found");
    REQUIRE((thrust::binary_search(values.begin(), values.end(), TestType{2}, unified::algorithms::Greater<TestType>{})));
  }
  {
    INFO("binary-search-descending: absent value found");
    REQUIRE((!thrust::binary_search(values.begin(), values.end(), TestType{6}, unified::algorithms::Greater<TestType>{})));
  }
}

template <class Value> inline void check_binary_search_vector(bool descending) {
  auto sorted = ascending_keys<Value>();
  if (descending) std::reverse(sorted.begin(), sorted.end());
  const std::vector<Value> needles{Value{-6}, Value{-5}, Value{-2}, Value{-1}, Value{2}, Value{6}, Value{7}};
  const thrust::device_vector<Value> values(sorted.begin(), sorted.end());
  const thrust::device_vector<Value> queries(needles.begin(), needles.end());
  thrust::device_vector<int32_t> output(needles.size());
  if (descending)
    thrust::binary_search(values.begin(), values.end(), queries.begin(), queries.end(), output.begin(),
                          unified::algorithms::Greater<Value>{});
  else thrust::binary_search(values.begin(), values.end(), queries.begin(), queries.end(), output.begin());
  std::vector<int32_t> expected;
  expected.reserve(needles.size());
  for (const auto needle : needles) {
    const bool found = descending ? std::binary_search(sorted.begin(), sorted.end(), needle, unified::algorithms::Greater<Value>{})
                                  : std::binary_search(sorted.begin(), sorted.end(), needle, unified::algorithms::Less<Value>{});
    expected.push_back(found ? 1 : 0);
  }
  {
    INFO((descending ? "binary-search-vector-descending" : "binary-search-vector"));
    REQUIRE(((std::vector<int32_t>(output.begin(), output.end())) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/gather-repeated/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> input{TestType{11}, TestType{22}, TestType{33}, TestType{44},
                                    TestType{55}, TestType{66}, TestType{77}, TestType{88}};
  const std::vector<size_t> indices{7, 2, 2, 0, 6, 3, 7, 1};
  const thrust::device_vector<TestType> source(input.begin(), input.end());
  const thrust::device_vector<size_t> map(indices.begin(), indices.end());
  thrust::device_vector<TestType> output(indices.size());
  thrust::gather(map.begin(), map.end(), source.begin(), output.begin());
  std::vector<TestType> expected;
  for (const auto index : indices)
    expected.push_back(input[index]);
  {
    INFO("gather-repeated");
    REQUIRE(((std::vector<TestType>(output.begin(), output.end())) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/permutation-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> input{TestType{13}, TestType{21}, TestType{34}, TestType{55}, TestType{89}, TestType{144}};
  const std::vector<size_t> indices{4, 1, 5, 1, 3, 0, 2};
  const thrust::device_vector<TestType> source(input.begin(), input.end());
  const thrust::device_vector<size_t> map(indices.begin(), indices.end());
  const auto first = thrust::make_permutation_iterator(source.begin(), map.begin());
  thrust::device_vector<TestType> output(indices.size());
  thrust::copy(first, first + static_cast<ptrdiff_t>(indices.size()), output.begin());
  std::vector<TestType> expected;
  for (const auto index : indices)
    expected.push_back(input[index]);
  {
    INFO("permutation-iterator");
    REQUIRE(((std::vector<TestType>(output.begin(), output.end())) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> keys{TestType{-3}, TestType{0}, TestType{4}, TestType{9}};
  const std::vector<int32_t> values{7, 2, 8, 1};
  const thrust::device_vector<TestType> device_keys(keys.begin(), keys.end());
  const thrust::device_vector<int32_t> device_values(values.begin(), values.end());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(device_keys.begin(), device_values.begin()));
  const auto last = first + static_cast<ptrdiff_t>(keys.size());
  thrust::device_vector<int64_t> output(keys.size());
  thrust::transform(first, last, output.begin(), TupleCode<TestType>{});
  std::vector<int64_t> expected;
  for (size_t index = 0; index < keys.size(); ++index)
    expected.push_back(static_cast<int64_t>(keys[index]) * 1000 + values[index]);
  {
    INFO("zip-iterator");
    REQUIRE(((std::vector<int64_t>(output.begin(), output.end())) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-sort/i32/n11", "[unified][rocthrust][compact]") {
  std::vector<TestType> keys{TestType{3}, TestType{-1}, TestType{3}, TestType{0}, TestType{-1}};
  std::vector<int32_t> values{30, 11, 31, 2, 10};
  thrust::device_vector<TestType> device_keys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> device_values(values.begin(), values.end());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(device_keys.begin(), device_values.begin()));
  thrust::sort(first, first + static_cast<ptrdiff_t>(keys.size()), TupleLess<TestType>{});
  std::vector<Pair<TestType>> actual;
  for (size_t index = 0; index < keys.size(); ++index)
    actual.emplace_back(device_keys[index], device_values[index]);
  std::vector<Pair<TestType>> expected;
  for (size_t index = 0; index < keys.size(); ++index)
    expected.emplace_back(keys[index], values[index]);
  std::sort(expected.begin(), expected.end(), PairLess<TestType>{});
  {
    INFO("zip-iterator-sort");
    REQUIRE(((actual) == (expected)));
  }
}

template <class Value> struct TupleSquare {
  UNIFIED_ROCTHRUST_HOST_DEVICE Value operator()(Value value) const { return value * value; }
};

TEST_CASE("algorithms/roc-thrust-semantic/tuple-transform/i32/n11", "[unified][rocthrust][compact]") {
  const auto input = std::make_tuple(TestType{-3}, TestType{2}, TestType{5});
  const auto actual = thrust::tuple_transform(input, TupleSquare<TestType>{});
  {
    INFO("tuple-transform[0] mismatch");
    REQUIRE((std::get<0>(actual) == TestType{9}));
  }
  {
    INFO("tuple-transform[1] mismatch");
    REQUIRE((std::get<1>(actual) == TestType{4}));
  }
  {
    INFO("tuple-transform[2] mismatch");
    REQUIRE((std::get<2>(actual) == TestType{25}));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/tuple-sort/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<TestType, int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(TestType{3}, 30), thrust::make_tuple(TestType{-1}, 11),
                                 thrust::make_tuple(TestType{3}, 31), thrust::make_tuple(TestType{0}, 2),
                                 thrust::make_tuple(TestType{-1}, 10)};
  thrust::device_vector<Tuple> values(input.begin(), input.end());
  thrust::sort(values.begin(), values.end(), TupleLess<TestType>{});
  const std::vector<Tuple> actual(values.begin(), values.end());
  const std::vector<Tuple> expected{thrust::make_tuple(TestType{-1}, 10), thrust::make_tuple(TestType{-1}, 11),
                                    thrust::make_tuple(TestType{0}, 2), thrust::make_tuple(TestType{3}, 30),
                                    thrust::make_tuple(TestType{3}, 31)};
  {
    INFO("tuple-sort: result size mismatch");
    REQUIRE((actual.size() == expected.size()));
  }
  for (size_t index = 0; index < expected.size(); ++index) {
    {
      INFO(fmt::format("tuple-sort.key[{}] mismatch", index));
      REQUIRE((thrust::get<0>(actual[index]) == thrust::get<0>(expected[index])));
    }
    {
      INFO(fmt::format("tuple-sort.value[{}] mismatch", index));
      REQUIRE((thrust::get<1>(actual[index]) == thrust::get<1>(expected[index])));
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/pair-sort/i32/n11", "[unified][rocthrust][compact]") {
  auto expected = pair_values(ascending_keys<TestType>(), 300);
  std::reverse(expected.begin(), expected.end());
  const auto original = expected;
  thrust::device_vector<Pair<TestType>> values(original.begin(), original.end());
  thrust::sort(values.begin(), values.end(), PairLess<TestType>{});
  std::sort(expected.begin(), expected.end(), PairLess<TestType>{});
  {
    INFO("pair-sort");
    REQUIRE(((host_pairs(values, values.size())) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/pair-sort-by-key/i32/n11", "[unified][rocthrust][compact]") {
  // Keep keys unique here so the plain (unstable) sort-by-key overload has a
  // deterministic payload association to validate.
  const std::vector<TestType> keys{TestType{9}, TestType{1}, TestType{8}, TestType{2}, TestType{7}, TestType{3},
                                   TestType{6}, TestType{4}, TestType{5}, TestType{0}, TestType{-1}};
  const auto values = payload(keys.size(), 400);
  thrust::device_vector<TestType> device_keys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> device_values(values.begin(), values.end());
  thrust::sort_by_key(device_keys.begin(), device_keys.end(), device_values.begin(), unified::algorithms::Less<TestType>{});
  const std::vector<TestType> actual_keys(device_keys.begin(), device_keys.end());
  const std::vector<int32_t> actual_values(device_values.begin(), device_values.end());
  {
    INFO("pair-sort-by-key: keys are not sorted");
    REQUIRE((std::is_sorted(actual_keys.begin(), actual_keys.end())));
  }
  std::vector<std::pair<TestType, int32_t>> expected;
  for (size_t index = 0; index < keys.size(); ++index)
    expected.emplace_back(keys[index], values[index]);
  std::sort(expected.begin(), expected.end());
  for (size_t index = 0; index < expected.size(); ++index) {
    {
      INFO("pair-sort-by-key: key association mismatch");
      REQUIRE((actual_keys[index] == expected[index].first));
    }
    {
      INFO("pair-sort-by-key: value association mismatch");
      REQUIRE((actual_values[index] == expected[index].second));
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/pair-scan/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<Pair<TestType>> input{{TestType{1}, 2}, {TestType{-3}, 4}, {TestType{5}, -1}, {TestType{2}, 7}};
  const thrust::device_vector<Pair<TestType>> source(input.begin(), input.end());
  thrust::device_vector<Pair<TestType>> output(input.size());
  thrust::inclusive_scan(source.begin(), source.end(), output.begin(), PairPlus<TestType>{});
  std::vector<Pair<TestType>> expected(input.size());
  Pair<TestType> running{TestType{}, 0};
  for (size_t index = 0; index < input.size(); ++index) {
    running = PairPlus<TestType>{}(running, input[index]);
    expected[index] = running;
  }
  {
    INFO("pair-scan");
    REQUIRE(((std::vector<Pair<TestType>>(output.begin(), output.end())) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/pair-reduce/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<Pair<TestType>> input{{TestType{1}, 2}, {TestType{-3}, 4}, {TestType{5}, -1}, {TestType{2}, 7}};
  const thrust::device_vector<Pair<TestType>> source(input.begin(), input.end());
  const auto actual = thrust::reduce(source.begin(), source.end(), Pair<TestType>{TestType{}, 0}, PairPlus<TestType>{});
  Pair<TestType> expected{TestType{}, 0};
  for (const auto value : input)
    expected = PairPlus<TestType>{}(expected, value);
  {
    INFO("pair-reduce mismatch");
    REQUIRE((actual.first == expected.first && actual.second == expected.second));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/tuple-scan/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<TestType, int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(TestType{1}, 2), thrust::make_tuple(TestType{-3}, 4),
                                 thrust::make_tuple(TestType{5}, -1), thrust::make_tuple(TestType{2}, 7)};
  const thrust::device_vector<Tuple> source(input.begin(), input.end());
  thrust::device_vector<Tuple> output(input.size());
  thrust::inclusive_scan(source.begin(), source.end(), output.begin(), TuplePlus<TestType>{});
  const std::vector<Tuple> actual(output.begin(), output.end());
  TestType first = TestType{};
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
  using Tuple = thrust::tuple<TestType, int32_t>;
  const std::vector<Tuple> input{thrust::make_tuple(TestType{1}, 2), thrust::make_tuple(TestType{-3}, 4),
                                 thrust::make_tuple(TestType{5}, -1), thrust::make_tuple(TestType{2}, 7)};
  const thrust::device_vector<Tuple> source(input.begin(), input.end());
  const auto actual = thrust::reduce(source.begin(), source.end(), thrust::make_tuple(TestType{}, 0), TuplePlus<TestType>{});
  TestType expected_first = TestType{};
  int32_t expected_second = 0;
  for (const auto value : input) {
    expected_first += thrust::get<0>(value);
    expected_second += thrust::get<1>(value);
  }
  {
    INFO("tuple-reduce mismatch");
    REQUIRE((thrust::get<0>(actual) == expected_first && thrust::get<1>(actual) == expected_second));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-scan/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> keys{TestType{1}, TestType{-3}, TestType{5}, TestType{2}};
  const std::vector<int32_t> values{2, 4, -1, 7};
  const thrust::device_vector<TestType> device_keys(keys.begin(), keys.end());
  const thrust::device_vector<int32_t> device_values(values.begin(), values.end());
  using Tuple = thrust::tuple<TestType, int32_t>;
  thrust::device_vector<Tuple> output(keys.size());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(device_keys.begin(), device_values.begin()));
  thrust::inclusive_scan(first, first + static_cast<ptrdiff_t>(keys.size()), output.begin(), TuplePlus<TestType>{});
  const std::vector<Tuple> actual(output.begin(), output.end());
  TestType expected_key = TestType{};
  int32_t expected_value = 0;
  for (size_t index = 0; index < keys.size(); ++index) {
    expected_key += keys[index];
    expected_value += values[index];
    {
      INFO(fmt::format("zip-iterator-scan mismatch at {}", index));
      REQUIRE((thrust::get<0>(actual[index]) == expected_key && thrust::get<1>(actual[index]) == expected_value));
    }
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/transform-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> input{TestType{-3}, TestType{-1}, TestType{2}, TestType{5}, TestType{7}};
  const thrust::device_vector<TestType> source(input.begin(), input.end());
  thrust::device_vector<TestType> output(input.size());
  const auto first = thrust::make_transform_iterator(source.begin(), Square<TestType>{});
  thrust::copy(first, first + static_cast<ptrdiff_t>(input.size()), output.begin());
  std::vector<TestType> expected;
  for (const auto value : input)
    expected.push_back(value * value);
  {
    INFO("transform-iterator");
    REQUIRE(((std::vector<TestType>(output.begin(), output.end())) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/transform-output-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> input{TestType{-3}, TestType{-1}, TestType{2}, TestType{5}, TestType{7}};
  const thrust::device_vector<TestType> source(input.begin(), input.end());
  thrust::device_vector<TestType> output(input.size());
  const auto destination = thrust::make_transform_output_iterator(output.begin(), Square<TestType>{});
  thrust::copy(source.begin(), source.end(), destination);
  std::vector<TestType> expected;
  for (const auto value : input)
    expected.push_back(value * value);
  {
    INFO("transform-output-iterator");
    REQUIRE(((std::vector<TestType>(output.begin(), output.end())) == (expected)));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/transform-input-output-iterator/i32/n11", "[unified][rocthrust][compact]") {
  constexpr size_t size = 5;
  thrust::device_vector<TestType> values(size, TestType{});
  const auto io = thrust::make_transform_input_output_iterator(values.begin(), Negate<TestType>{}, Square<TestType>{});
  const auto first = thrust::make_counting_iterator<TestType>(TestType{1});
  thrust::copy(first, first + static_cast<ptrdiff_t>(size), io);
  for (size_t index = 0; index < size; ++index) {
    INFO(fmt::format("transform-input-output write mismatch at {}", index));
    REQUIRE((values[index] == static_cast<TestType>((index + 1) * (index + 1))));
  }
  thrust::device_vector<TestType> readback(size);
  thrust::copy(io, io + static_cast<ptrdiff_t>(size), readback.begin());
  for (size_t index = 0; index < size; ++index) {
    INFO(fmt::format("transform-input-output read mismatch at {}", index));
    REQUIRE((readback[index] == -values[index]));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-sort-by-key/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> keys{TestType{9}, TestType{1}, TestType{8}, TestType{2}, TestType{7}};
  const std::vector<int32_t> values{90, 10, 80, 20, 70};
  const std::vector<int32_t> tags{9, 1, 8, 2, 7};
  thrust::device_vector<TestType> device_keys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> device_values(values.begin(), values.end());
  thrust::device_vector<int32_t> device_tags(tags.begin(), tags.end());
  const auto zipped = thrust::make_zip_iterator(thrust::make_tuple(device_values.begin(), device_tags.begin()));
  thrust::sort_by_key(device_keys.begin(), device_keys.end(), zipped);
  for (size_t index = 0; index < keys.size(); ++index) {
    const auto key = device_keys[index];
    INFO(fmt::format("zip-iterator-sort-by-key association mismatch at {}", index));
    REQUIRE((device_values[index] == static_cast<int32_t>(key * 10) && device_tags[index] == static_cast<int32_t>(key)));
  }
}

// ---------------------------------------------------------------------------
// The device-side random semantics of rocThrust's `testing/random`, decomposed
// into one case per engine characteristic and one per distribution
// characteristic so a single regression names the exact instantiation.
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// rocThrust `testing/unittest_static_assert` port.  The upstream case depends
// on a device kernel publishing through a pointer that the host installed in a
// `__device__` global with hipMemcpyToSymbol, so that capability is the
// semantic unit here rather than the static-assert macro itself.
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// rocThrust `testing/vector{,_insert}` port: device_vector insert/erase/
// resize semantics, each step mirrored through an exact std::vector
// reference.
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// rocThrust `testing/dereference` port: plain iterators and device pointers
// dereferenced inside device code through a hand-rolled copy kernel, since a
// library-side copy would dispatch instead of exercising the iterator's own
// operator*.  `testing/device_delete.cu` itself is marked KNOWN_FAILURE
// upstream, so only this brand of device dereference is live.
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// rocThrust `testing/device_ptr` and `testing/device_reference` port.
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// rocThrust `testing/zip_function` port: unpacked calls over zip iterators
// compared against the tuple base case, on both host and device.
// ---------------------------------------------------------------------------

TEST_CASE("algorithms/roc-thrust-semantic/set-union-by-key/i32/n11", "[unified][rocthrust][compact]") {
  check_set_by_key<TestType>(SetKind::union_set, false);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-intersection-by-key/i32/n11", "[unified][rocthrust][compact]") {
  check_set_by_key<TestType>(SetKind::intersection, false);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-difference-by-key/i32/n11", "[unified][rocthrust][compact]") {
  check_set_by_key<TestType>(SetKind::difference, false);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-symmetric-difference-by-key/i32/n11", "[unified][rocthrust][compact]") {
  check_set_by_key<TestType>(SetKind::symmetric_difference, false);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-union-by-key-descending/i32/n11", "[unified][rocthrust][compact]") {
  check_set_by_key<TestType>(SetKind::union_set, true);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-intersection-by-key-descending/i32/n11", "[unified][rocthrust][compact]") {
  check_set_by_key<TestType>(SetKind::intersection, true);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-difference-by-key-descending/i32/n11", "[unified][rocthrust][compact]") {
  check_set_by_key<TestType>(SetKind::difference, true);
}

TEST_CASE("algorithms/roc-thrust-semantic/"
          "set-symmetric-difference-by-key-descending/i32/n11",
          "[unified][rocthrust][compact]") {
  check_set_by_key<TestType>(SetKind::symmetric_difference, true);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-union-descending/i32/n11", "[unified][rocthrust][compact]") {
  check_set_descending<TestType>(SetKind::union_set);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-intersection-descending/i32/n11", "[unified][rocthrust][compact]") {
  check_set_descending<TestType>(SetKind::intersection);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-difference-descending/i32/n11", "[unified][rocthrust][compact]") {
  check_set_descending<TestType>(SetKind::difference);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-symmetric-difference-descending/"
          "i32/n11",
          "[unified][rocthrust][compact]") {
  check_set_descending<TestType>(SetKind::symmetric_difference);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-union-key-value/i32/n11", "[unified][rocthrust][compact]") {
  check_pair_set<TestType>(SetKind::union_set);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-intersection-key-value/i32/n11", "[unified][rocthrust][compact]") {
  check_pair_set<TestType>(SetKind::intersection);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-difference-key-value/i32/n11", "[unified][rocthrust][compact]") {
  check_pair_set<TestType>(SetKind::difference);
}

TEST_CASE("algorithms/roc-thrust-semantic/set-symmetric-difference-key-value/i32/n11", "[unified][rocthrust][compact]") {
  check_pair_set<TestType>(SetKind::symmetric_difference);
}

TEST_CASE("algorithms/roc-thrust-semantic/binary-search-vector/i32/n11", "[unified][rocthrust][compact]") {
  check_binary_search_vector<TestType>(false);
}

TEST_CASE("algorithms/roc-thrust-semantic/binary-search-vector-descending/i32/n11", "[unified][rocthrust][compact]") {
  check_binary_search_vector<TestType>(true);
}
