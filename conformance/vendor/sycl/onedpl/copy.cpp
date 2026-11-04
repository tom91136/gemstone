#include <algorithm>
#include <cstddef>
#include <iterator>
#include <vector>

#include <catch2/catch_test_macros.hpp>
#include <oneapi/dpl/algorithm>
#include <oneapi/dpl/execution>

namespace dpl = oneapi::dpl;

namespace {
constexpr std::size_t n = 1000;

std::vector<int> ramp(std::size_t size) {
  std::vector<int> v(size);
  for (std::size_t i = 0; i < size; ++i)
    v[i] = static_cast<int>(i);
  return v;
}
} // namespace

TEST_CASE("onedpl copy", "[sycl][onedpl]") {
  const auto in = ramp(n);
  std::vector<int> out(n), expected(n);
  dpl::copy(dpl::execution::dpcpp_default, in.begin(), in.end(), out.begin());
  std::copy(in.begin(), in.end(), expected.begin());
  REQUIRE(out == expected);
}

TEST_CASE("onedpl copy_if multiples of three", "[sycl][onedpl]") {
  const auto in = ramp(n);
  const auto pred = [](int x) { return x % 3 == 0; };
  std::vector<int> out(n);
  const auto last = dpl::copy_if(dpl::execution::dpcpp_default, in.begin(), in.end(), out.begin(), pred);
  std::vector<int> expected;
  std::copy_if(in.begin(), in.end(), std::back_inserter(expected), pred);
  REQUIRE(static_cast<std::size_t>(last - out.begin()) == expected.size());
  REQUIRE(std::equal(out.begin(), last, expected.begin(), expected.end()));
}

TEST_CASE("onedpl count_if", "[sycl][onedpl]") {
  const auto in = ramp(n);
  const auto pred = [](int x) { return x % 7 == 1; };
  const auto result = dpl::count_if(dpl::execution::dpcpp_default, in.begin(), in.end(), pred);
  REQUIRE(result == std::count_if(in.begin(), in.end(), pred));
}
