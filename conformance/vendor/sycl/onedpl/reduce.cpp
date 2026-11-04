#include <algorithm>
#include <cstddef>
#include <functional>
#include <limits>
#include <numeric>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>
#include <oneapi/dpl/numeric>

namespace dpl = oneapi::dpl;

namespace {
constexpr std::size_t n = 1000;

std::vector<int> ramp(std::size_t size) {
  std::vector<int> v(size);
  for (std::size_t i = 0; i < size; ++i)
    v[i] = static_cast<int>(i % 97) - 40;
  return v;
}
} // namespace

TEST_CASE("onedpl reduce sum", "[sycl][onedpl]") {
  const auto v = ramp(n);
  const auto result = dpl::reduce(dpl::execution::dpcpp_default, v.begin(), v.end(), 0);
  REQUIRE(result == std::accumulate(v.begin(), v.end(), 0));
}

TEST_CASE("onedpl reduce max", "[sycl][onedpl]") {
  const auto v = ramp(n);
  const auto result = dpl::reduce(dpl::execution::dpcpp_default, v.begin(), v.end(), std::numeric_limits<int>::min(),
                                  [](int a, int b) { return a > b ? a : b; });
  REQUIRE(result == *std::max_element(v.begin(), v.end()));
}

TEST_CASE("onedpl transform_reduce dot product", "[sycl][onedpl]") {
  const auto a = ramp(n);
  auto b = ramp(n);
  std::rotate(b.begin(), b.begin() + 13, b.end());
  const auto result =
      dpl::transform_reduce(dpl::execution::dpcpp_default, a.begin(), a.end(), b.begin(), 0, std::plus<>{}, std::multiplies<>{});
  REQUIRE(result == std::inner_product(a.begin(), a.end(), b.begin(), 0));
}
