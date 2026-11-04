#include <catch2/catch_test_macros.hpp>

#include "sample_run.hpp"

int conformanceSampleMain(int, char **);

TEST_CASE("matrix-mul-mkl-float", "[sycl][onemkl]") {
  const auto [rc, output] = runSample({"matrix-mul-mkl", "float", "256"}, conformanceSampleMain);
  REQUIRE(rc == 0);
  REQUIRE(output.find("gemm  passes.") != std::string::npos);
}
