#define UNIFIED_API_THRUST_DEVICE_FUNCTIONS 1
#include "api_thrust.hpp"
#undef UNIFIED_API_THRUST_DEVICE_FUNCTIONS
#include <catch2/catch_template_test_macros.hpp>

TEMPLATE_TEST_CASE_SIG("runtime/hip/device-function", "[unified][runtime][compact]", ((bool indirect), indirect), false, true) {
  unified::gpu_calls::device_function<indirect>();
}

#if defined(CONFORMANCE_HIP_HAS_ROCBLAS)
  #include "api_blas.hpp"

TEST_CASE("regressions/rocblas/gfx1036/f16/all", "[unified][regression][f16]") {
  unified::backend::Rocblas<unified::ScalarType::f16> backend;
  unified::blas::axpy<unified::ScalarType::f16>(backend, 8);
}
#endif
