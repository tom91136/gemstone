#include <catch2/catch_test_macros.hpp>

#include "sample_run.hpp"

int conformanceSampleMain(int argc, char **argv);

TEST_CASE("vector-add-buffers", "[sycl][sample]") {
  const auto [rc, output] = runSample({"vector-add-buffers", "1024"}, conformanceSampleMain);
  REQUIRE(rc == 0);
  REQUIRE(output.find("Vector add successfully completed on device.") != std::string::npos);
}
