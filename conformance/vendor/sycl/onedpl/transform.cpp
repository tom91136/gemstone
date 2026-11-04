#include <algorithm>
#include <cstddef>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>

namespace dpl = oneapi::dpl;

namespace {
constexpr std::size_t n = 1000;

std::vector<int> iotaVector(std::size_t size) {
  std::vector<int> v(size);
  for (std::size_t i = 0; i < size; ++i)
    v[i] = static_cast<int>(i) - 500;
  return v;
}
} // namespace

TEST_CASE("onedpl transform unary", "[sycl][onedpl]") {
  const auto in = iotaVector(n);
  std::vector<int> out(n), expected(n);
  dpl::transform(dpl::execution::dpcpp_default, in.begin(), in.end(), out.begin(), [](int x) { return x * 2 + 1; });
  std::transform(in.begin(), in.end(), expected.begin(), [](int x) { return x * 2 + 1; });
  REQUIRE(out == expected);
}

TEST_CASE("onedpl transform binary", "[sycl][onedpl]") {
  const auto a = iotaVector(n);
  auto b = iotaVector(n);
  std::reverse(b.begin(), b.end());
  std::vector<int> out(n), expected(n);
  dpl::transform(dpl::execution::dpcpp_default, a.begin(), a.end(), b.begin(), out.begin(), [](int x, int y) { return x - y; });
  std::transform(a.begin(), a.end(), b.begin(), expected.begin(), [](int x, int y) { return x - y; });
  REQUIRE(out == expected);
}

TEST_CASE("onedpl transform in place", "[sycl][onedpl]") {
  auto v = iotaVector(n);
  auto expected = v;
  dpl::transform(dpl::execution::dpcpp_default, v.begin(), v.end(), v.begin(), [](int x) { return x < 0 ? -x : x; });
  std::transform(expected.begin(), expected.end(), expected.begin(), [](int x) { return x < 0 ? -x : x; });
  REQUIRE(v == expected);
}
