#include <algorithm>
#include <limits>

#include <catch2/catch_test_macros.hpp>

#include "support.hpp"
#include "template_blas.hpp"

TEST_CASE("byte reference values remain sorted", "[unit][unified]") {
  const auto values = unified::support::sorted_values<int8_t>(4096, 3);
  CHECK(std::is_sorted(values.begin(), values.end()));
}

TEST_CASE("BLAS matcher preserves inactive canaries exactly", "[unit][unified]") {
  const unified::WithinTolerance expected(0.0003L, std::vector<long double>{1.0L, 77.0L}, {true, false});
  CHECK(expected.match(std::vector<long double>{1.0001L, 77.0L}));
  CHECK_FALSE(expected.match(std::vector<long double>{1.0L, 77.01L}));
  CHECK_FALSE(expected.match(std::vector<long double>{1.0L}));
  CHECK_FALSE(expected.match(std::vector<long double>{std::numeric_limits<long double>::quiet_NaN(), 77.0L}));
}

TEST_CASE("key permutation matcher rejects lost or repeated payloads", "[unit][unified]") {
  const std::vector<int32_t> keys{3, 3}, source{3, 3}, duplicate{0, 0}, valid{1, 0};
  CHECK_FALSE(unified::KeyPermutation(duplicate, source).match(keys));
  CHECK(unified::KeyPermutation(valid, source).match(keys));
}

TEST_CASE("BLAS dimensions reject integer overflow", "[unit][unified]") {
  CHECK_THROWS_AS(unified::support::checked_add(SIZE_MAX, 1, "size"), std::invalid_argument);
  CHECK_THROWS_AS(unified::support::checked_multiply(SIZE_MAX, 2, "size"), std::invalid_argument);
  CHECK_THROWS_AS(unified::support::checked_int(static_cast<size_t>(INT_MAX) + 1, "size"), std::invalid_argument);
}
