#include "gemstone/conformance/blas_smoke.hpp"
#include "oneapi_mkl.hpp"

int main(int argc, char **argv) {
  gemstone::conformance::blas::Coverage coverage =
      gemstone::conformance::blas::Coverage::all;
  gemstone::conformance::blas::NumericType type =
      gemstone::conformance::blas::NumericType::f32;
  if (argc == 2) {
    if (!gemstone::conformance::blas::parse_coverage(argv[1], coverage) &&
        !gemstone::conformance::blas::parse_numeric_type(argv[1], type)) {
      std::cerr << "usage: oneapi_mkl_smoke [all|level1|level2|level3] [f16|f32|f64|i8]\n";
      return 2;
    }
  } else if (argc == 3) {
    if (!gemstone::conformance::blas::parse_coverage(argv[1], coverage) ||
        !gemstone::conformance::blas::parse_numeric_type(argv[2], type)) {
      std::cerr << "usage: oneapi_mkl_smoke [all|level1|level2|level3] [f16|f32|f64|i8]\n";
      return 2;
    }
  } else if (argc > 3) {
    std::cerr << "usage: oneapi_mkl_smoke [all|level1|level2|level3] [f16|f32|f64|i8]\n";
    return 2;
  }
  return gemstone::conformance::blas::run_selected<gemstone::conformance::blas::OneapiMklBackend>(
      type, coverage, std::cout);
}
