#include <algorithm>
#include <cstddef>
#include <functional>
#include <numeric>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <oneapi/dpl/execution>
#include <oneapi/dpl/numeric>

namespace dpl = oneapi::dpl;

namespace {
constexpr std::size_t n = 1000;

std::vector<int> ramp(std::size_t size) {
  std::vector<int> v(size);
  for (std::size_t i = 0; i < size; ++i)
    v[i] = static_cast<int>(i % 53) - 20;
  return v;
}
} // namespace

TEST_CASE("onedpl inclusive_scan sum", "[sycl][onedpl]") {
  const auto in = ramp(n);
  std::vector<int> out(n), expected(n);
  dpl::inclusive_scan(dpl::execution::dpcpp_default, in.begin(), in.end(), out.begin(), std::plus<>{});
  std::inclusive_scan(in.begin(), in.end(), expected.begin(), std::plus<>{});
  REQUIRE(out == expected);
}

TEST_CASE("onedpl exclusive_scan sum", "[sycl][onedpl]") {
  const auto in = ramp(n);
  std::vector<int> out(n), expected(n);
  dpl::exclusive_scan(dpl::execution::dpcpp_default, in.begin(), in.end(), out.begin(), 7, std::plus<>{});
  std::exclusive_scan(in.begin(), in.end(), expected.begin(), 7, std::plus<>{});
  REQUIRE(out == expected);
}

TEST_CASE("onedpl inclusive_scan max", "[sycl][onedpl]") {
  const auto in = ramp(n);
  std::vector<int> out(n), expected(n);
  const auto maxOp = [](int a, int b) { return a > b ? a : b; };
  dpl::inclusive_scan(dpl::execution::dpcpp_default, in.begin(), in.end(), out.begin(), maxOp);
  std::inclusive_scan(in.begin(), in.end(), expected.begin(), maxOp);
  REQUIRE(out == expected);
}
