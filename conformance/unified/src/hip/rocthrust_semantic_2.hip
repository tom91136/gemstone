#include "test_support.hpp"

#define UNIFIED_API_ROCTHRUST 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_ROCTHRUST
#include <cstddef>
#include <cstdint>

#include <catch2/catch_test_macros.hpp>

using namespace unified::rocthrust_support;
using TestType = int32_t;

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

struct SumThree {
  UNIFIED_ROCTHRUST_HOST_DEVICE int32_t operator()(int32_t left, int32_t middle, int32_t right) const { return left + middle + right; }
};

TEST_CASE("algorithms/roc-thrust-semantic/zip-function/i32/n11", "[unified][rocthrust][compact]") {
  const size_t count = 5;
  std::vector<TestType> left(count), middle(count), right(count), expected(count);
  for (size_t index = 0; index < count; ++index) {
    left[index] = static_cast<TestType>(index) + TestType{1};
    middle[index] = static_cast<TestType>(2 * index);
    right[index] = -static_cast<TestType>(index);
    expected[index] = left[index] + middle[index] + right[index];
  }
  const thrust::device_vector<TestType> device_left(left.begin(), left.end());
  const thrust::device_vector<TestType> device_middle(middle.begin(), middle.end());
  const thrust::device_vector<TestType> device_right(right.begin(), right.end());
  thrust::device_vector<TestType> output(count);

  const auto first = thrust::make_zip_iterator(thrust::make_tuple(device_left.begin(), device_middle.begin(), device_right.begin()));
  const auto last = first + static_cast<ptrdiff_t>(count);
  thrust::transform(first, last, output.begin(), thrust::make_zip_function(SumThree{}));
  {
    INFO("zip-function");
    REQUIRE(((std::vector<TestType>(output.begin(), output.end())) == (expected)));
  }
  const auto host_call = thrust::make_zip_function(SumThree{})(thrust::make_tuple(TestType{1}, TestType{2}, TestType{3}));
  {
    INFO("zip-function: direct call mismatch");
    REQUIRE((host_call == TestType{6}));
  }
}

struct SelectEqualPairs {
  UNIFIED_ROCTHRUST_HOST_DEVICE bool operator()(const thrust::tuple<uint32_t, uint32_t> &pair, const float &) const {
    return thrust::get<0>(pair) == thrust::get<1>(pair);
  }
};

TEST_CASE("algorithms/roc-thrust-semantic/zip-function-remove-if/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<uint32_t> a{0, 0, 2, 0};
  thrust::device_vector<uint32_t> b{0, 2, 2, 2};
  thrust::device_vector<float> c{88.0f, 88.0f, 89.0f, 89.0f};
  const auto first =
      thrust::make_zip_iterator(thrust::make_tuple(thrust::make_zip_iterator(thrust::make_tuple(a.begin(), b.begin())), c.begin()));
  const auto end = thrust::remove_if(first, first + 4, thrust::make_zip_function(SelectEqualPairs{}));
  const auto count = static_cast<size_t>(thrust::distance(first, end));
  {
    INFO("zip-function-remove-if: kept count mismatch");
    REQUIRE((count == 2));
  }
  a.resize(count);
  b.resize(count);
  c.resize(count);
  std::vector<float> expected{88.0f, 89.0f};
  {
    INFO("zip-function-remove-if");
    REQUIRE(((std::vector<float>(c.begin(), c.end())) == (expected)));
  }
}

// ---------------------------------------------------------------------------
// rocThrust `testing/zip_iterator_reduce{,_by_key}` port; the upstream probe
// reduces over zipped ranges and mirrors host versus device dispatch.
// ---------------------------------------------------------------------------

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-reduce/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<TestType, int32_t>;
  const std::vector<TestType> keys{TestType{1}, TestType{-3}, TestType{5}, TestType{2}, TestType{0}, TestType{-2}};
  const std::vector<int32_t> values{2, 4, -1, 7, 10, -3};
  thrust::device_vector<TestType> device_keys(keys.begin(), keys.end());
  thrust::device_vector<int32_t> device_values(values.begin(), values.end());
  const auto first = thrust::make_zip_iterator(thrust::make_tuple(device_keys.begin(), device_values.begin()));
  const auto last = thrust::make_zip_iterator(thrust::make_tuple(device_keys.end(), device_values.end()));
  const Tuple actual = thrust::reduce(thrust::device, first, last, thrust::make_tuple(TestType{}, int32_t{}), TuplePlus<TestType>{});
  TestType expected_key = TestType{};
  int32_t expected_value = 0;
  for (size_t index = 0; index < keys.size(); ++index) {
    expected_key += keys[index];
    expected_value += values[index];
  }
  {
    INFO(fmt::format("zip-iterator-reduce mismatch (actual={},{}; expected={},{})", thrust::get<0>(actual), thrust::get<1>(actual),
                     expected_key, expected_value));
    REQUIRE((thrust::get<0>(actual) == expected_key && thrust::get<1>(actual) == expected_value));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/zip-iterator-reduce-by-key/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<TestType, int32_t>;
  const std::vector<TestType> keys{TestType{1}, TestType{1}, TestType{2}, TestType{2}, TestType{1}, TestType{3}};
  const std::vector<TestType> first_values{TestType{2}, TestType{-1}, TestType{7}, TestType{10}, TestType{5}, TestType{-3}};
  const std::vector<int32_t> second_values{0, 1, 2, 3, 4, 5};
  thrust::device_vector<TestType> device_keys(keys.begin(), keys.end());
  thrust::device_vector<TestType> device_first(first_values.begin(), first_values.end());
  thrust::device_vector<int32_t> device_second(second_values.begin(), second_values.end());
  thrust::device_vector<TestType> output_keys(keys.size());
  thrust::device_vector<TestType> output_first(keys.size());
  thrust::device_vector<int32_t> output_second(keys.size());
  const auto bundled_values = thrust::make_zip_iterator(thrust::make_tuple(device_first.begin(), device_second.begin()));
  const auto bundled_outputs = thrust::make_zip_iterator(thrust::make_tuple(output_first.begin(), output_second.begin()));
  const auto ends = thrust::reduce_by_key(device_keys.begin(), device_keys.end(), bundled_values, output_keys.begin(), bundled_outputs,
                                          thrust::equal_to<TestType>{}, TuplePlus<TestType>{});
  const size_t count = static_cast<size_t>(ends.first - output_keys.begin());
  // Run-length reference over the contiguous key runs.
  std::vector<TestType> expected_keys;
  std::vector<Tuple> expected_values;
  for (size_t index = 0; index < keys.size(); ++index) {
    if (index == 0 || keys[index] != keys[index - 1]) {
      expected_keys.push_back(keys[index]);
      expected_values.emplace_back(first_values[index], second_values[index]);
    } else {
      thrust::get<0>(expected_values.back()) += first_values[index];
      thrust::get<1>(expected_values.back()) += second_values[index];
    }
  }
  {
    INFO("zip-iterator-reduce-by-key: run count mismatch");
    REQUIRE((count == expected_keys.size()));
  }
  for (size_t index = 0; index < count; ++index) {
    {
      INFO(fmt::format("zip-iterator-reduce-by-key.key[{}] mismatch", index));
      REQUIRE((output_keys[index] == expected_keys[index]));
    }
    {
      INFO(fmt::format("zip-iterator-reduce-by-key.first[{}] mismatch", index));
      REQUIRE((output_first[index] == thrust::get<0>(expected_values[index])));
    }
    {
      INFO(fmt::format("zip-iterator-reduce-by-key.second[{}] mismatch", index));
      REQUIRE((output_second[index] == thrust::get<1>(expected_values[index])));
    }
  }
}

// ---------------------------------------------------------------------------
// rocThrust `testing/sort_permutation_iterator` port: the strided range packs a
// permutation iterator over a counting/transform layer, and sorting that slice
// has to leave the untouched lanes untouched.
// ---------------------------------------------------------------------------

TEST_CASE("algorithms/roc-thrust-semantic/constant-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const auto constant = thrust::make_constant_iterator(TestType{13});
  thrust::device_vector<TestType> output(5, TestType{0});
  thrust::copy(constant, constant + 5, output.begin());
  {
    INFO("constant-iterator: forwarded values");
    const auto &device_values = output;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
            == (std::vector<TestType>{TestType{13}, TestType{13}, TestType{13}, TestType{13}, TestType{13}}));
  }
  const auto offset_constant = thrust::make_constant_iterator(TestType{7}, 3);
  {
    INFO("constant-iterator: value mismatch");
    REQUIRE((*offset_constant == TestType{7}));
  }
  {
    INFO("constant-iterator: distance mismatch");
    REQUIRE((thrust::distance(offset_constant, offset_constant + 11) == 11));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/counting-iterator/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<TestType> output(5, TestType{0});
  const auto first = thrust::make_counting_iterator<TestType>(TestType{1});
  const auto last = first + 5;
  thrust::copy(first, last, output.begin());
  {
    INFO("counting-iterator: incremented values");
    const auto &device_values = output;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
            == (std::vector<TestType>{TestType{1}, TestType{2}, TestType{3}, TestType{4}, TestType{5}}));
  }
  thrust::fill(output.begin(), output.end(), TestType{0});
  thrust::transform(first, last, output.begin(), Negate<TestType>{});
  {
    INFO("counting-iterator: negated values");
    const auto &device_values = output;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end()))
            == (std::vector<TestType>{TestType{-1}, TestType{-2}, TestType{-3}, TestType{-4}, TestType{-5}}));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/discard-iterator/i32/n11", "[unified][rocthrust][compact]") {
  const std::vector<TestType> source{TestType{4}, TestType{2}, TestType{9}, TestType{1}, TestType{6}};
  const thrust::device_vector<TestType> device_source(source.begin(), source.end());
  thrust::device_vector<TestType> output(5, TestType{0});
  // The zip keeps the payload side alive while the discard lane drops writes.
  const auto source_zipped =
      thrust::make_zip_iterator(thrust::make_tuple(device_source.begin(), thrust::make_constant_iterator(TestType{0})));
  const auto output_zipped = thrust::make_zip_iterator(thrust::make_tuple(output.begin(), thrust::make_discard_iterator()));
  thrust::copy(source_zipped, source_zipped + 5, output_zipped);
  {
    INFO("discard-iterator: payload side mismatch");
    const auto &device_values = output;
    REQUIRE((std::vector<TestType>(device_values.begin(), device_values.end())) == (source));
  }
  *thrust::make_discard_iterator() = TestType{7};
  auto advanced = thrust::make_discard_iterator();
  advanced += 9;
  {
    INFO("discard-iterator: arithmetic mismatch");
    REQUIRE((advanced - thrust::make_discard_iterator() == 9));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/generate-const-iterators/i32/n11", "[unified][rocthrust][compact]") {
  // The upstream requirement is a compile-time negative check
  // (ASSERT_STATIC_ASSERT that generate over const iterators rejects), so there
  // is no device-side or host-side value a runtime case could verify here.
  SKIP("generate-const-iterators: upstream requirement is a compile-time negative "
       "check");
}

// ---------------------------------------------------------------------------
// rocThrust `testing/pair.cu` and `testing/pair_transform` port: pair
// construction/comparison semantics plus device transforms over pairs.
// ---------------------------------------------------------------------------

TEST_CASE("algorithms/roc-thrust-semantic/pair-manipulation/i32/n11", "[unified][rocthrust][compact]") {
  using P = thrust::pair<TestType, TestType>;
  P empty;
  {
    INFO("pair-manipulation: default ctor value mismatch");
    REQUIRE((empty.first == TestType{} && empty.second == TestType{}));
  }
  empty.first = TestType{1};
  empty.second = TestType{2};
  const P copy(empty);
  {
    INFO("pair-manipulation: copy constructor mismatch");
    REQUIRE((copy == P{TestType{1}, TestType{2}}));
  }
  const std::pair<TestType, TestType> standard_copy(copy.first, copy.second);
  {
    INFO("pair-manipulation: conversion to std::pair mismatch");
    REQUIRE((standard_copy.first == TestType{1} && standard_copy.second == TestType{2}));
  }
  const P from_pair{TestType{6}, TestType{7}};
  {
    INFO("pair-manipulation: thrust::get mismatch");
    REQUIRE((thrust::get<0>(from_pair) == TestType{6} && thrust::get<1>(from_pair) == TestType{7}));
  }
  P assigned;
  assigned = thrust::make_pair(TestType{8}, TestType{9});
  {
    INFO("pair-manipulation: assignment mismatch");
    REQUIRE((assigned == P{TestType{8}, TestType{9}}));
  }
  const P left{TestType{}, TestType{1}};
  P right{TestType{2}, TestType{1}};
  {
    INFO("pair-manipulation: first-field ordering mismatch");
    REQUIRE((left < right && left != right));
  }
  const P matched{TestType{}, TestType{1}};
  {
    INFO("pair-manipulation: equality mismatch");
    REQUIRE((left == matched));
  }
  const thrust::device_vector<P> stored{left, thrust::pair<TestType, TestType>{TestType{2}, TestType{3}}};
  const std::vector<P> readback(stored.begin(), stored.end());
  {
    INFO("pair-manipulation: device roundtrip mismatch");
    REQUIRE((readback.size() == 2 && readback[0] == P{TestType{}, TestType{1}} && readback[1] == P{TestType{2}, TestType{3}}));
  }
}

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

TEST_CASE("algorithms/roc-thrust-semantic/pair-transform/i32/n11", "[unified][rocthrust][compact]") {
  using P = thrust::pair<TestType, TestType>;
  const std::vector<TestType> first{TestType{3}, TestType{-1}, TestType{2}, TestType{5}};
  const std::vector<TestType> second{TestType{-2}, TestType{4}, TestType{1}, TestType{0}};
  const thrust::device_vector<TestType> device_first(first.begin(), first.end());
  const thrust::device_vector<TestType> device_second(second.begin(), second.end());
  thrust::device_vector<P> device_pairs(first.size());
  thrust::transform(device_first.begin(), device_first.end(), device_second.begin(), device_pairs.begin(), PairFromValues{});
  thrust::transform(device_pairs.begin(), device_pairs.end(), device_pairs.begin(), device_pairs.begin(), PairSelfAdd<P>{});
  std::vector<P> expected(first.size());
  for (size_t index = 0; index < first.size(); ++index) {
    const std::pair<TestType, TestType> pair(first[index], second[index]);
    expected[index].first = pair.first + pair.first;
    expected[index].second = pair.second + pair.second;
  }
  std::vector<P> actual;
  for (size_t index = 0; index < first.size(); ++index) {
    const P element = device_pairs[index];
    actual.push_back(element);
  }
  {
    INFO("pair-transform: field association mismatch");
    REQUIRE((actual == expected));
  }
}

// ---------------------------------------------------------------------------
// rocThrust `testing/tuple.cu` and `testing/tuple_algorithms` port.
// ---------------------------------------------------------------------------

TEST_CASE("algorithms/roc-thrust-semantic/tuple-construction/i32/n11", "[unified][rocthrust][compact]") {
  using Tuple = thrust::tuple<TestType, TestType, TestType, TestType, TestType>;
  const Tuple zero;
  {
    INFO("tuple-construction: default value mismatch");
    REQUIRE((zero == thrust::make_tuple(TestType{}, TestType{}, TestType{}, TestType{}, TestType{})));
  }
  const Tuple filled{TestType{1}, TestType{2}, TestType{3}, TestType{4}, TestType{5}};
  {
    INFO("tuple-construction: element access mismatch");
    REQUIRE((thrust::get<0>(filled) == TestType{1} && thrust::get<2>(filled) == TestType{3} && thrust::get<4>(filled) == TestType{5}));
  }
  const Tuple other{1, 2, 3, 4, 5};
  {
    INFO("tuple-construction: equality mismatch");
    REQUIRE((filled == other));
  }
  const Tuple less{TestType{}, TestType{}, TestType{}, TestType{}, TestType{}};
  const Tuple more{TestType{}, TestType{}, TestType{1}, TestType{}, TestType{}};
  {
    INFO("tuple-construction: strict ordering mismatch");
    REQUIRE((less < more));
  }
  {
    INFO("tuple-construction: mixed ordering mismatch");
    REQUIRE((more >= less && !(more <= less)));
  }
  Tuple swap_left = filled;
  Tuple swap_right = thrust::make_tuple(TestType{5}, TestType{4}, TestType{3}, TestType{2}, TestType{1});
  thrust::swap(swap_left, swap_right);
  {
    INFO("tuple-construction: swap mismatch");
    REQUIRE((thrust::get<0>(swap_left) == TestType{5} && thrust::get<4>(swap_left) == TestType{1}));
  }
  const thrust::device_vector<thrust::tuple<TestType, TestType>> stored{thrust::make_tuple(TestType{7}, TestType{8}),
                                                                        thrust::make_tuple(TestType{-1}, TestType{2})};
  const std::vector<thrust::tuple<TestType, TestType>> readback(stored.begin(), stored.end());
  {
    INFO("tuple-construction: device roundtrip mismatch");
    REQUIRE((readback.size() == 2 && thrust::get<0>(readback[0]) == TestType{7} && thrust::get<1>(readback[1]) == TestType{2}));
  }
}

struct TupleSquareInPlace {
  template <class Value> UNIFIED_ROCTHRUST_HOST_DEVICE void operator()(Value &value) const { value = value * value; }
};

TEST_CASE("algorithms/roc-thrust-semantic/tuple-algorithms/i32/n11", "[unified][rocthrust][compact]") {
  const auto subset_source = std::make_tuple(int32_t{0}, int32_t{2}, 3.14);
  const auto subset_target = thrust::tuple_subset(subset_source, thrust::index_sequence<2, 0>{});
  {
    INFO("tuple-algorithms: tuple_subset ordering mismatch");
    REQUIRE((std::get<0>(subset_target) == 3.14 && std::get<1>(subset_target) == 0));
  }
  auto squared = std::make_tuple(int32_t{0}, int32_t{2}, 3.14);
  thrust::tuple_for_each(squared, TupleSquareInPlace{});
  {
    INFO("tuple-algorithms: integer squares mismatch");
    REQUIRE((std::get<0>(squared) == 0 && std::get<1>(squared) == 4));
  }
  {
    INFO("tuple-algorithms: real squares mismatch");
    REQUIRE((std::fabs(std::get<2>(squared) - 9.8596) < 1e-9));
  }
}

TEST_CASE("algorithms/roc-thrust-semantic/advance-distance/i32/n11", "[unified][rocthrust][compact]") {
  thrust::device_vector<TestType> values(10);
  thrust::sequence(values.begin(), values.end());
  auto iter = values.begin();
  thrust::advance(iter, 1);
  {
    INFO("advance-distance: first advance value mismatch");
    REQUIRE((*iter == TestType{1}));
  }
  thrust::advance(iter, 8);
  {
    INFO("advance-distance: forward advance value mismatch");
    REQUIRE((*iter == TestType{9}));
  }
  thrust::advance(iter, -4);
  {
    INFO("advance-distance: backward advance value mismatch");
    REQUIRE((*iter == TestType{5}));
  }
  const auto next_one = thrust::next(values.begin());
  const auto near_end = thrust::next(values.begin(), 8);
  const auto prior = thrust::prev(values.end());
  {
    INFO("advance-distance: next/prev mismatch");
    REQUIRE((*next_one == TestType{1} && *near_end == TestType{8} && *prior == TestType{9}));
  }
  {
    INFO("advance-distance: full distance mismatch");
    REQUIRE((thrust::distance(values.begin(), values.end()) == 10));
  }
  {
    INFO("advance-distance: zero distance mismatch");
    REQUIRE((thrust::distance(values.begin(), values.begin()) == 0));
  }
}

// ---------------------------------------------------------------------------
// rocThrust `testing/out_of_memory_recovery.cu` port.  The upstream test
// assumes a real allocation-failure path integrated through HIP and the
// preceding device_vector destructors running during the failed helper
// allocation; under the emulator no deterministic allocation failure can be
// forced, so the case is a documented skip.
// ---------------------------------------------------------------------------
