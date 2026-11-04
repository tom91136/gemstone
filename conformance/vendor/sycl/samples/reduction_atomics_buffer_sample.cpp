#include <catch2/catch_test_macros.hpp>

#include "sample_run.hpp"

int conformanceSampleMain();

TEST_CASE("reduction-atomics-buffer", "[sycl][sample]") {
  const auto [rc, output] = runSample({"reduction-atomics-buffer"}, conformanceSampleMain);
  REQUIRE(rc == 0);
  REQUIRE(output.find("Sum = 523776") != std::string::npos);
}
