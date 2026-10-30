#include "test_support.hpp"

#define UNIFIED_API_ROCTHRUST 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_ROCTHRUST
#include <cstddef>
#include <cstdint>

#include <aspartame/all.hpp>
#include <catch2/catch_test_macros.hpp>

using namespace aspartame;
using namespace unified::rocthrust_support;

namespace {
// rocThrust `testing/zip_function` port: unpacked calls over zip iterators
// compared against the tuple base case, on both host and device.

struct SumThree {
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()(int32_t left, int32_t middle, int32_t right) const { return left + middle + right; }
};

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/zip-function/i32/n11", "[unified][rocthrust][compact]") {
  const size_t count = 5;
  const auto left = iota(size_t{0}, count) | map([](size_t index) { return static_cast<int32_t>(index) + int32_t{1}; }) | to_vector();
  const auto middle = iota(size_t{0}, count) | map([](size_t index) { return static_cast<int32_t>(2 * index); }) | to_vector();
  const auto right = iota(size_t{0}, count) | map([](size_t index) { return -static_cast<int32_t>(index); }) | to_vector();
  const auto expected =
      iota(size_t{0}, count) | map([&](size_t index) { return left[index] + middle[index] + right[index]; }) | to_vector();
  const thrust::device_vector<int32_t> deviceLeft(left.begin(), left.end());
  const thrust::device_vector<int32_t> deviceMiddle(middle.begin(), middle.end());
  const thrust::device_vector<int32_t> deviceRight(right.begin(), right.end());
  thrust::device_vector<int32_t> output(count);

  const auto first = thrust::make_zip_iterator(thrust::make_tuple(deviceLeft.begin(), deviceMiddle.begin(), deviceRight.begin()));
  const auto last = first + static_cast<ptrdiff_t>(count);
  thrust::transform(first, last, output.begin(), thrust::make_zip_function(SumThree{}));
  {
    INFO("zip-function");
    REQUIRE(std::vector<int32_t>(output.begin(), output.end()) == expected);
  }
  const auto hostCall = thrust::make_zip_function(SumThree{})(thrust::make_tuple(int32_t{1}, int32_t{2}, int32_t{3}));
  {
    INFO("zip-function: direct call mismatch");
    REQUIRE(hostCall == int32_t{6});
  }
}

namespace {
struct SelectEqualPairs {
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const thrust::tuple<uint32_t, uint32_t> &pair, const float &) const {
    return thrust::get<0>(pair) == thrust::get<1>(pair);
  }
};

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/zip-function-remove-if/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<uint32_t> a{0, 0, 2, 0};
  thrust::device_vector<uint32_t> b{0, 2, 2, 2};
  thrust::device_vector<float> c{88.0F, 88.0F, 89.0F, 89.0F};
  const auto first =
      thrust::make_zip_iterator(thrust::make_tuple(thrust::make_zip_iterator(thrust::make_tuple(a.begin(), b.begin())), c.begin()));
  const auto end = thrust::remove_if(first, first + 4, thrust::make_zip_function(SelectEqualPairs{}));
  const auto count = static_cast<size_t>(thrust::distance(first, end));
  {
    INFO("zip-function-remove-if: kept count mismatch");
    REQUIRE(count == 2);
  }
  a.resize(count);
  b.resize(count);
  c.resize(count);
  std::vector<float> expected{88.0F, 89.0F};
  {
    INFO("zip-function-remove-if");
    REQUIRE(std::vector<float>(c.begin(), c.end()) == expected);
  }
}

// rocThrust `testing/zip_iterator_reduce{,_by_key}` port; the upstream probe
// reduces over zipped ranges and mirrors host versus device dispatch.

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-reduce/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<int32_t, int32_t>;
  const std::vector<int32_t> keys{int32_t{1}, int32_t{-3}, int32_t{5}, int32_t{2}, int32_t{0}, int32_t{-2}};
  const std::vector<int32_t> values{2, 4, -1, 7, 10, -3};
  thrust::device_vector<int32_t> deviceKeys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> deviceValues(values.begin(), values.end());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(deviceKeys.begin(), deviceValues.begin()));
  const auto last = thrust::make_zip_iterator(thrust::make_tuple(deviceKeys.end(), deviceValues.end()));
  const Tuple actual = thrust::reduce(thrust::device, first, last, thrust::make_tuple(int32_t{}, int32_t{}), TuplePlus<int32_t>{});
  const int32_t expectedKey = keys ^ sum();
  const int32_t expectedValue = values ^ sum();
  {
    INFO(fmt::format("zip-iterator-reduce mismatch (actual={},{}; expected={},{})", thrust::get<0>(actual), thrust::get<1>(actual),
                     expectedKey, expectedValue));
    REQUIRE((thrust::get<0>(actual) == expectedKey && thrust::get<1>(actual) == expectedValue));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-reduce-by-key/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<int32_t, int32_t>;
  const std::vector<int32_t> keys{int32_t{1}, int32_t{1}, int32_t{2}, int32_t{2}, int32_t{1}, int32_t{3}};
  const std::vector<int32_t> firstValues{int32_t{2}, int32_t{-1}, int32_t{7}, int32_t{10}, int32_t{5}, int32_t{-3}};
  const std::vector<int32_t> secondValues{0, 1, 2, 3, 4, 5};
  thrust::device_vector<int32_t> deviceKeys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> deviceFirst(firstValues.begin(), firstValues.end());
  thrust::device_vector<int32_t> deviceSecond(secondValues.begin(), secondValues.end());
  thrust::device_vector<int32_t> outputKeys(keys.size());
  thrust::device_vector<int32_t> outputFirst(keys.size());
  thrust::device_vector<int32_t> outputSecond(keys.size());
  const auto bundledValues = thrust::make_zip_iterator(thrust::make_tuple(deviceFirst.begin(), deviceSecond.begin()));
  const auto bundledOutputs = thrust::make_zip_iterator(thrust::make_tuple(outputFirst.begin(), outputSecond.begin()));
  const auto ends = thrust::reduce_by_key(deviceKeys.begin(), deviceKeys.end(), bundledValues, outputKeys.begin(), bundledOutputs,
                                          thrust::equal_to<int32_t>{}, TuplePlus<int32_t>{});
  const size_t count = static_cast<size_t>(ends.first - outputKeys.begin());
  // Run-length reference over the contiguous key runs.
  std::vector<int32_t> expectedKeys;
  std::vector<Tuple> expectedValues;
  for (size_t index = 0; index < keys.size(); ++index) {
    if (index == 0 || keys[index] != keys[index - 1]) {
      expectedKeys.push_back(keys[index]);
      expectedValues.emplace_back(firstValues[index], secondValues[index]);
    } else {
      thrust::get<0>(expectedValues.back()) += firstValues[index];
      thrust::get<1>(expectedValues.back()) += secondValues[index];
    }
  }
  {
    INFO("zip-iterator-reduce-by-key: run count mismatch");
    REQUIRE(count == expectedKeys.size());
  }
  for (size_t index = 0; index < count; ++index) {
    {
      INFO(fmt::format("zip-iterator-reduce-by-key.key[{}] mismatch", index));
      REQUIRE(outputKeys[index] == expectedKeys[index]);
    }
    {
      INFO(fmt::format("zip-iterator-reduce-by-key.first[{}] mismatch", index));
      REQUIRE(outputFirst[index] == thrust::get<0>(expectedValues[index]));
    }
    {
      INFO(fmt::format("zip-iterator-reduce-by-key.second[{}] mismatch", index));
      REQUIRE(outputSecond[index] == thrust::get<1>(expectedValues[index]));
    }
  }
}

// rocThrust `testing/{constant,counting,discard}_iterator` port. `testing/generate_const_iterators`
// is a compile-time negative check (ASSERT_STATIC_ASSERT) and is carried as an explicit skip.

TEST_CASE("algorithms/roc-thrust-semantic/constant-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const auto constant = thrust::make_constant_iterator(int32_t{13});
  thrust::device_vector<int32_t> output(5, int32_t{0});
  thrust::copy(constant, constant + 5, output.begin());
  {
    INFO("constant-iterator: forwarded values");
    const auto &deviceValues = output;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end())
            == std::vector<int32_t>{int32_t{13}, int32_t{13}, int32_t{13}, int32_t{13}, int32_t{13}});
  }
  const auto offsetConstant = thrust::make_constant_iterator(int32_t{7}, 3);
  {
    INFO("constant-iterator: value mismatch");
    REQUIRE(*offsetConstant == int32_t{7});
  }
  {
    INFO("constant-iterator: distance mismatch");
    REQUIRE(thrust::distance(offsetConstant, offsetConstant + 11) == 11);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/counting-iterator/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<int32_t> output(5, int32_t{0});
  const auto first = thrust::make_counting_iterator<int32_t>(int32_t{1});
  const auto last = first + 5;
  thrust::copy(first, last, output.begin());
  {
    INFO("counting-iterator: incremented values");
    const auto &deviceValues = output;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end())
            == std::vector<int32_t>{int32_t{1}, int32_t{2}, int32_t{3}, int32_t{4}, int32_t{5}});
  }
  thrust::fill(output.begin(), output.end(), int32_t{0});
  thrust::transform(first, last, output.begin(), Negate<int32_t>{});
  {
    INFO("counting-iterator: negated values");
    const auto &deviceValues = output;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end())
            == std::vector<int32_t>{int32_t{-1}, int32_t{-2}, int32_t{-3}, int32_t{-4}, int32_t{-5}});
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/discard-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<int32_t> source{int32_t{4}, int32_t{2}, int32_t{9}, int32_t{1}, int32_t{6}};
  const thrust::device_vector<int32_t> deviceSource(source.begin(), source.end());
  thrust::device_vector<int32_t> output(5, int32_t{0});
  // The zip keeps the payload side alive while the discard lane drops writes.
  const auto sourceZipped = thrust::make_zip_iterator(thrust::make_tuple(deviceSource.begin(), thrust::make_constant_iterator(int32_t{0})));
  const auto outputZipped = thrust::make_zip_iterator(thrust::make_tuple(output.begin(), thrust::make_discard_iterator()));
  thrust::copy(sourceZipped, sourceZipped + 5, outputZipped);
  {
    INFO("discard-iterator: payload side mismatch");
    const auto &deviceValues = output;
    REQUIRE(std::vector<int32_t>(deviceValues.begin(), deviceValues.end()) == source);
  }
  *thrust::make_discard_iterator() = int32_t{7};
  auto advanced = thrust::make_discard_iterator();
  advanced += 9;
  {
    INFO("discard-iterator: arithmetic mismatch");
    REQUIRE(advanced - thrust::make_discard_iterator() == 9);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/generate-const-iterators/i32/n11", "[unified][rocthrust][compact]") {
  // The upstream requirement is a compile-time negative check
  // (ASSERT_STATIC_ASSERT that generate over const iterators rejects), so there
  // is no device-side or host-side value a runtime case could verify here.
  SKIP("generate-const-iterators: upstream requirement is a compile-time negative "
       "check");
}

// rocThrust `testing/pair.cu` and `testing/pair_transform` port: pair
// construction/comparison semantics plus device transforms over pairs.

TEST_CASE("algorithms/roc-thrust-semantic/pair-manipulation/i32/n11", "[unified][rocthrust][compact]") {
  using P = thrust::pair<int32_t, int32_t>;
  P empty;
  {
    INFO("pair-manipulation: default ctor value mismatch");
    REQUIRE((empty.first == int32_t{} && empty.second == int32_t{}));
  }
  empty.first = int32_t{1};
  empty.second = int32_t{2};
  const P copy(empty);
  {
    INFO("pair-manipulation: copy constructor mismatch");
    REQUIRE(copy == P{int32_t{1}, int32_t{2}});
  }
  const std::pair<int32_t, int32_t> standardCopy(copy.first, copy.second);
  {
    INFO("pair-manipulation: conversion to std::pair mismatch");
    REQUIRE((standardCopy.first == int32_t{1} && standardCopy.second == int32_t{2}));
  }
  const P fromPair{int32_t{6}, int32_t{7}};
  {
    INFO("pair-manipulation: thrust::get mismatch");
    REQUIRE((thrust::get<0>(fromPair) == int32_t{6} && thrust::get<1>(fromPair) == int32_t{7}));
  }
  P assigned;
  assigned = thrust::make_pair(int32_t{8}, int32_t{9});
  {
    INFO("pair-manipulation: assignment mismatch");
    REQUIRE(assigned == P{int32_t{8}, int32_t{9}});
  }
  const P left{int32_t{}, int32_t{1}};
  P right{int32_t{2}, int32_t{1}};
  {
    INFO("pair-manipulation: first-field ordering mismatch");
    REQUIRE((left < right && left != right));
  }
  const P matched{int32_t{}, int32_t{1}};
  {
    INFO("pair-manipulation: equality mismatch");
    REQUIRE(left == matched);
  }
  const thrust::device_vector<P> stored{left, thrust::pair<int32_t, int32_t>{int32_t{2}, int32_t{3}}};
  const std::vector<P> readback(stored.begin(), stored.end());
  {
    INFO("pair-manipulation: device roundtrip mismatch");
    REQUIRE((readback.size() == 2 && readback[0] == P{int32_t{}, int32_t{1}} && readback[1] == P{int32_t{2}, int32_t{3}}));
  }
}

namespace {
struct PairFromValues {
  template <class X, class Y> UNIFIED_ROCTHRUST_HOST_DEVICE thrust::pair<X, Y> operator()(const X &left, const Y &right) const {
    return thrust::make_pair(left, right);
  }
};

template <class P> struct PairSelfAdd {
  UNIFIED_ROCTHRUST_HOST_DEVICE P operator()(const P &left, const P &right) const {
    return thrust::make_pair(left.first + right.first, left.second + right.second);
  }
};

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/pair-transform/i32/n11", "[unified][rocthrust][compact]") {
  using P = thrust::pair<int32_t, int32_t>;
  const std::vector<int32_t> first{int32_t{3}, int32_t{-1}, int32_t{2}, int32_t{5}};
  const std::vector<int32_t> second{int32_t{-2}, int32_t{4}, int32_t{1}, int32_t{0}};
  const thrust::device_vector<int32_t> deviceFirst(first.begin(), first.end());
  const thrust::device_vector<int32_t> deviceSecond(second.begin(), second.end());
  thrust::device_vector<P> devicePairs(first.size());
  thrust::transform(deviceFirst.begin(), deviceFirst.end(), deviceSecond.begin(), devicePairs.begin(), PairFromValues{});
  thrust::transform(devicePairs.begin(), devicePairs.end(), devicePairs.begin(), devicePairs.begin(), PairSelfAdd<P>{});
  const auto expected = first | zip(second) | map([](int32_t head, int32_t tail) { return P(head + head, tail + tail); }) | to_vector();
  const auto actual = iota(size_t{0}, first.size()) | map([&](size_t index) -> P { return devicePairs[index]; }) | to_vector();
  {
    INFO("pair-transform: field association mismatch");
    REQUIRE(actual == expected);
  }
}

// rocThrust `testing/tuple.cu` and `testing/tuple_algorithms` port.

TEST_CASE("algorithms/roc-thrust-semantic/tuple-construction/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<int32_t, int32_t, int32_t, int32_t, int32_t>;
  const Tuple zero;
  {
    INFO("tuple-construction: default value mismatch");
    REQUIRE(zero == thrust::make_tuple(int32_t{}, int32_t{}, int32_t{}, int32_t{}, int32_t{}));
  }
  const Tuple filled{int32_t{1}, int32_t{2}, int32_t{3}, int32_t{4}, int32_t{5}};
  {
    INFO("tuple-construction: element access mismatch");
    REQUIRE((thrust::get<0>(filled) == int32_t{1} && thrust::get<2>(filled) == int32_t{3} && thrust::get<4>(filled) == int32_t{5}));
  }
  const Tuple other{1, 2, 3, 4, 5};
  {
    INFO("tuple-construction: equality mismatch");
    REQUIRE(filled == other);
  }
  const Tuple less{int32_t{}, int32_t{}, int32_t{}, int32_t{}, int32_t{}};
  const Tuple more{int32_t{}, int32_t{}, int32_t{1}, int32_t{}, int32_t{}};
  {
    INFO("tuple-construction: strict ordering mismatch");
    REQUIRE(less < more);
  }
  {
    INFO("tuple-construction: mixed ordering mismatch");
    REQUIRE((more >= less && !(more <= less)));
  }
  Tuple swapLeft = filled;
  Tuple swapRight = thrust::make_tuple(int32_t{5}, int32_t{4}, int32_t{3}, int32_t{2}, int32_t{1});
  thrust::swap(swapLeft, swapRight);
  {
    INFO("tuple-construction: swap mismatch");
    REQUIRE((thrust::get<0>(swapLeft) == int32_t{5} && thrust::get<4>(swapLeft) == int32_t{1}));
  }
  const thrust::device_vector<thrust::tuple<int32_t, int32_t>> stored{thrust::make_tuple(int32_t{7}, int32_t{8}),
                                                                      thrust::make_tuple(int32_t{-1}, int32_t{2})};
  const std::vector<thrust::tuple<int32_t, int32_t>> readback(stored.begin(), stored.end());
  {
    INFO("tuple-construction: device roundtrip mismatch");
    REQUIRE((readback.size() == 2 && thrust::get<0>(readback[0]) == int32_t{7} && thrust::get<1>(readback[1]) == int32_t{2}));
  }
}

namespace {
struct TupleSquareInPlace {
  template <class Value> UNIFIED_ROCTHRUST_HOST_DEVICE void operator()(Value &value) const { value = value * value; }
};

} // namespace

TEST_CASE("algorithms/roc-thrust-semantic/tuple-algorithms/i32/n11", "[unified][rocthrust][compact]") {
  const auto subsetSource = std::make_tuple(int32_t{0}, int32_t{2}, 3.14);
  const auto subsetTarget = thrust::tuple_subset(subsetSource, thrust::index_sequence<2, 0>{});
  {
    INFO("tuple-algorithms: tuple_subset ordering mismatch");
    REQUIRE((std::get<0>(subsetTarget) == 3.14 && std::get<1>(subsetTarget) == 0));
  }
  auto squared = std::make_tuple(int32_t{0}, int32_t{2}, 3.14);
  thrust::tuple_for_each(squared, TupleSquareInPlace{});
  {
    INFO("tuple-algorithms: integer squares mismatch");
    REQUIRE((std::get<0>(squared) == 0 && std::get<1>(squared) == 4));
  }
  {
    INFO("tuple-algorithms: real squares mismatch");
    REQUIRE(std::fabs(std::get<2>(squared) - 9.8596) < 1e-9);
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/advance-distance/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<int32_t> values(10);
  thrust::sequence(values.begin(), values.end());
  auto iter = values.begin();
  thrust::advance(iter, 1);
  {
    INFO("advance-distance: first advance value mismatch");
    REQUIRE(*iter == int32_t{1});
  }
  thrust::advance(iter, 8);
  {
    INFO("advance-distance: forward advance value mismatch");
    REQUIRE(*iter == int32_t{9});
  }
  thrust::advance(iter, -4);
  {
    INFO("advance-distance: backward advance value mismatch");
    REQUIRE(*iter == int32_t{5});
  }
  const auto nextOne = thrust::next(values.begin());
  const auto nearEnd = thrust::next(values.begin(), 8);
  const auto prior = thrust::prev(values.end());
  {
    INFO("advance-distance: next/prev mismatch");
    REQUIRE((*nextOne == int32_t{1} && *nearEnd == int32_t{8} && *prior == int32_t{9}));
  }
  {
    INFO("advance-distance: full distance mismatch");
    REQUIRE(thrust::distance(values.begin(), values.end()) == 10);
  }
  {
    INFO("advance-distance: zero distance mismatch");
    REQUIRE(thrust::distance(values.begin(), values.begin()) == 0);
  }
}
