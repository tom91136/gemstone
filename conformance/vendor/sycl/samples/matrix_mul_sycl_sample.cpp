#include <catch2/catch_test_macros.hpp>

#include "sample_run.hpp"

int conformanceSampleMain();

TEST_CASE("matrix-mul-sycl", "[sycl][sample]") {
  const auto [rc, output] = runSample({"matrix-mul-sycl"}, conformanceSampleMain);
  REQUIRE(rc == 0);
  REQUIRE(output.find("Success - The results are correct!") != std::string::npos);
}
