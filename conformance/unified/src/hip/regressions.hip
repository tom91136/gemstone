#define UNIFIED_API_THRUST_DEVICE_FUNCTIONS 1
#include "backend_thrust.hpp"
#undef UNIFIED_API_THRUST_DEVICE_FUNCTIONS
#include <catch2/catch_template_test_macros.hpp>

TEMPLATE_TEST_CASE_SIG("runtime/hip/device-function", "[unified][runtime][compact]", ((bool Indirect), Indirect), false, true) {
  unified::gpu_calls::deviceFunction<Indirect>();
}

#if defined(CONFORMANCE_HIP_HAS_ROCBLAS)
  #include "backend_blas.hpp"

TEST_CASE("regressions/rocblas/gfx1036/f16/all", "[unified][regression][f16]") {
  unified::backend::Rocblas<unified::ScalarType::F16> backend;
  unified::blas::axpy<unified::ScalarType::F16>(backend, 8);
}
#endif
