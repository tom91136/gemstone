#include <catch2/catch_test_macros.hpp>

#include "sample_run.hpp"

int conformanceSampleMain(int, char **);

TEST_CASE("fourier-correlation-1d-usm", "[sycl][onemkl]") {
  const auto [rc, output] = runSample({"fcorr-1d-usm", "1024"}, conformanceSampleMain);
  REQUIRE(rc == 0);
  REQUIRE(output.find("Max difference between naive and Fourier-based calculations") != std::string::npos);
}
