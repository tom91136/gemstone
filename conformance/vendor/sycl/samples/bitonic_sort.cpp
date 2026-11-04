#include <catch2/catch_test_macros.hpp>

#include "sample_run.hpp"

int conformanceSampleMain(int argc, char **argv);

TEST_CASE("bitonic-sort", "[sycl][sample]") {
  const auto [rc, output] = runSample({"bitonic-sort", "10", "1"}, conformanceSampleMain);
  REQUIRE(rc == 0);
  REQUIRE(output.find("Success!") != std::string::npos);
}
