#include <catch2/catch_test_macros.hpp>

#include "sample_run.hpp"

int conformanceSampleMain();

TEST_CASE("sum-subgroup-reduce", "[sycl][sample]") {
  const auto [rc, output] = runSample({"sum-subgroup-reduce"}, conformanceSampleMain);
  REQUIRE(rc == 0);
  REQUIRE(output.find("Sum = 523776") != std::string::npos);
}
