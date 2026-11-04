#include <algorithm>
#include <vector>

#include "support.hpp"

namespace unified::openacc {
namespace {
template <class T> std::vector<T> pattern(T (*f)(int)) {
  std::vector<T> result(Count);
  for (int i = 0; i < Count; ++i)
    result[sz(i)] = f(i);
  return result;
}

template <class T> T smallValue(int i) { return static_cast<T>((i % 7) + 1); }
template <class T> T signedValue(int i) { return static_cast<T>(((i * 37) % 61) - 30); }
template <class T> T powerValue(int i) { return (i % 8) == 0 ? T{2} : T{1}; }
template <class T> T oneValue(int) { return T{1}; }
template <class T> T zeroValue(int) { return T{0}; }
template <class T> T lastZeroValue(int i) { return i == Count - 1 ? T{0} : T{1}; }
template <class T> T lastOneValue(int i) { return i == Count - 1 ? T{1} : T{0}; }

template <class T> T sum(const T *p, T init) {
  T s = init;
#pragma acc parallel loop reduction(+ : s) copyin(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    s += p[i];
  return s;
}

template <class T> T sumKernels(const T *p) {
  T s = T{0};
#pragma acc kernels loop reduction(+ : s) copyin(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    s += p[i];
  return s;
}

template <class T> T product(const T *p) {
  T s = T{1};
#pragma acc parallel loop reduction(* : s) copyin(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    s *= p[i];
  return s;
}

template <class T> T maximum(const T *p) {
  T s = p[0];
#pragma acc parallel loop reduction(max : s) copyin(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    s = p[i] > s ? p[i] : s;
  return s;
}

template <class T> T minimum(const T *p) {
  T s = p[0];
#pragma acc parallel loop reduction(min : s) copyin(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    s = p[i] < s ? p[i] : s;
  return s;
}

template <class T> int logicalAnd(const T *p) {
  int s = 1;
#pragma acc parallel loop reduction(&& : s) copyin(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    s = s && (p[i] != T{0});
  return s;
}

template <class T> int logicalOr(const T *p) {
  int s = 0;
#pragma acc parallel loop reduction(|| : s) copyin(p[0 : Count])
  for (int i = 0; i < Count; ++i)
    s = s || (p[i] != T{0});
  return s;
}

template <class T> T nestedSum(const T *p) {
  constexpr int rows = 8;
  constexpr int columns = 8;
  T s = T{0};
#pragma acc parallel loop collapse(2) reduction(+ : s) copyin(p[0 : rows * columns])
  for (int r = 0; r < rows; ++r)
    for (int c = 0; c < columns; ++c)
      s += p[(r * columns) + c];
  return s;
}

template <class T> T gangVectorSum(const T *p) {
  T s = T{0};
#pragma acc parallel num_gangs(4) vector_length(16) reduction(+ : s) copyin(p[0 : Count])
  {
#pragma acc loop gang vector reduction(+ : s)
    for (int i = 0; i < Count; ++i)
      s += p[i];
  }
  return s;
}
} // namespace

TEMPLATE_TEST_CASE("openacc/reduction/sum", "[unified][openacc][reduction]", int, float, double) {
  const auto values = pattern<TestType>(smallValue<TestType>);
  TestType expected = 0;
  for (const auto v : values)
    expected += v;
  REQUIRE(sum<TestType>(values.data(), TestType{0}) == expected);
  REQUIRE(sum<TestType>(values.data(), TestType{100}) == expected + TestType{100});
  REQUIRE(sumKernels<TestType>(values.data()) == expected);
  REQUIRE(nestedSum<TestType>(values.data()) == std::accumulate(values.begin(), values.begin() + 64, TestType{0}));
  REQUIRE(gangVectorSum<TestType>(values.data()) == expected);
}

TEMPLATE_TEST_CASE("openacc/reduction/product", "[unified][openacc][reduction]", int, float, double) {
  const auto values = pattern<TestType>(powerValue<TestType>);
  REQUIRE(product<TestType>(values.data()) == TestType{256});
}

TEMPLATE_TEST_CASE("openacc/reduction/max", "[unified][openacc][reduction]", int, float, double) {
  const auto values = pattern<TestType>(signedValue<TestType>);
  REQUIRE(maximum<TestType>(values.data()) == *std::max_element(values.begin(), values.end()));
}

TEMPLATE_TEST_CASE("openacc/reduction/min", "[unified][openacc][reduction]", int, float, double) {
  const auto values = pattern<TestType>(signedValue<TestType>);
  REQUIRE(minimum<TestType>(values.data()) == *std::min_element(values.begin(), values.end()));
}

TEMPLATE_TEST_CASE("openacc/reduction/logical-and", "[unified][openacc][reduction]", int, float, double) {
  const auto ones = pattern<TestType>(oneValue<TestType>);
  const auto lastZero = pattern<TestType>(lastZeroValue<TestType>);
  REQUIRE(logicalAnd<TestType>(ones.data()) == 1);
  REQUIRE(logicalAnd<TestType>(lastZero.data()) == 0);
}

TEMPLATE_TEST_CASE("openacc/reduction/logical-or", "[unified][openacc][reduction]", int, float, double) {
  const auto zeros = pattern<TestType>(zeroValue<TestType>);
  const auto lastOne = pattern<TestType>(lastOneValue<TestType>);
  REQUIRE(logicalOr<TestType>(zeros.data()) == 0);
  REQUIRE(logicalOr<TestType>(lastOne.data()) == 1);
}

TEMPLATE_TEST_CASE("openacc/reduction/sum-fractional", "[unified][openacc][reduction]", float, double) {
  constexpr int items = 1000;
  std::vector<TestType> values(items);
  TestType expected = 0;
  for (int i = 0; i < items; ++i) {
    values[sz(i)] = static_cast<TestType>(i) * TestType{0.1};
    expected += values[sz(i)];
  }
  const TestType *p = values.data();
  TestType s = 0;
#pragma acc parallel loop reduction(+ : s) copyin(p[0 : items])
  for (int i = 0; i < items; ++i)
    s += p[i];
  const TestType tolerance = std::is_same_v<TestType, float> ? TestType{1e-3} : TestType{1e-9};
  REQUIRE(std::fabs((s - expected) / expected) <= tolerance);
}
} // namespace unified::openacc
