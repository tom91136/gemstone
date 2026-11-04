#include <algorithm>
#include <cstddef>
#include <functional>
#include <utility>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>

namespace dpl = oneapi::dpl;

namespace {
constexpr std::size_t n = 1000;

std::vector<int> scrambled(std::size_t size) {
  std::vector<int> v(size);
  for (std::size_t i = 0; i < size; ++i)
    v[i] = static_cast<int>((i * 7919) % 1009) - 500;
  return v;
}
} // namespace

TEST_CASE("onedpl sort ascending", "[sycl][onedpl]") {
  auto v = scrambled(n);
  auto expected = v;
  dpl::sort(dpl::execution::dpcpp_default, v.begin(), v.end());
  std::sort(expected.begin(), expected.end());
  REQUIRE(v == expected);
}

TEST_CASE("onedpl sort descending", "[sycl][onedpl]") {
  auto v = scrambled(n);
  auto expected = v;
  dpl::sort(dpl::execution::dpcpp_default, v.begin(), v.end(), std::greater<>{});
  std::sort(expected.begin(), expected.end(), std::greater<>{});
  REQUIRE(v == expected);
}

TEST_CASE("onedpl stable_sort pairs by key", "[sycl][onedpl]") {
  const auto keys = scrambled(n);
  std::vector<std::pair<int, std::size_t>> v(n);
  for (std::size_t i = 0; i < n; ++i)
    v[i] = {keys[i] % 16, i};
  auto expected = v;
  const auto byKey = [](const auto &a, const auto &b) { return a.first < b.first; };
  dpl::stable_sort(dpl::execution::dpcpp_default, v.begin(), v.end(), byKey);
  std::stable_sort(expected.begin(), expected.end(), byKey);
  REQUIRE(v == expected);
}
