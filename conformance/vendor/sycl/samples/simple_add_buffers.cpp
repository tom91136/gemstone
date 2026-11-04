#include <catch2/catch_test_macros.hpp>

#include "sample_run.hpp"

int conformanceSampleMain();

TEST_CASE("simple-add-buffers", "[sycl][sample]") {
  const auto [rc, output] = runSample({"simple-add-buffers"}, conformanceSampleMain);
  REQUIRE(rc == 0);
  REQUIRE(output.find("Successfully completed on device.") != std::string::npos);
}
