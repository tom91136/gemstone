#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "api_blas.hpp"
#include "template_edge.hpp"

#define DECLARE_EDGE_BLAS_F32_COMPACT(name, check)                                                                                         \
  TEST_CASE("edge/blas/f32/compact/" name, "[unified][compact-matrix][edge][blas][f32]") {                                                 \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_edge_sizes));                                                \
    CAPTURE(size);                                                                                                                         \
    unified::backend::OneMkl<unified::ScalarType::f32> backend;                                                                            \
    check<unified::ScalarType::f32>(backend, size);                                                                                        \
  }

DECLARE_EDGE_BLAS_F32_COMPACT("negative_stride_axpy", unified::edge::negative_stride_axpy)
DECLARE_EDGE_BLAS_F32_COMPACT("padded_gemm", unified::edge::padded_gemm)
DECLARE_EDGE_BLAS_F32_COMPACT("beta_zero_gemm", unified::edge::beta_zero_gemm)

#undef DECLARE_EDGE_BLAS_F32_COMPACT

#define DECLARE_EDGE_BLAS_F32_FULL(name, check)                                                                                            \
  TEST_CASE("edge/blas/f32/full/" name, "[unified][full-matrix][edge][blas][f32]") {                                                       \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::OneMkl<unified::ScalarType::f32> backend;                                                                            \
    check<unified::ScalarType::f32>(backend, size);                                                                                        \
  }

DECLARE_EDGE_BLAS_F32_FULL("negative_stride_axpy", unified::edge::negative_stride_axpy)
DECLARE_EDGE_BLAS_F32_FULL("padded_gemm", unified::edge::padded_gemm)
DECLARE_EDGE_BLAS_F32_FULL("beta_zero_gemm", unified::edge::beta_zero_gemm)

#undef DECLARE_EDGE_BLAS_F32_FULL

#define DECLARE_EDGE_BLAS_F64_FULL(name, check)                                                                                            \
  TEST_CASE("edge/blas/f64/full/" name, "[unified][full-matrix][edge][blas][f64]") {                                                       \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::OneMkl<unified::ScalarType::f64> backend;                                                                            \
    check<unified::ScalarType::f64>(backend, size);                                                                                        \
  }

DECLARE_EDGE_BLAS_F64_FULL("negative_stride_axpy", unified::edge::negative_stride_axpy)
DECLARE_EDGE_BLAS_F64_FULL("padded_gemm", unified::edge::padded_gemm)
DECLARE_EDGE_BLAS_F64_FULL("beta_zero_gemm", unified::edge::beta_zero_gemm)

#undef DECLARE_EDGE_BLAS_F64_FULL

#define DECLARE_EDGE_BLAS_F16_FULL(name, check)                                                                                            \
  TEST_CASE("edge/blas/f16/full/" name, "[unified][full-matrix][edge][blas][f16]") {                                                       \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::OneMkl<unified::ScalarType::f16> backend;                                                                            \
    check<unified::ScalarType::f16>(backend, size);                                                                                        \
  }

DECLARE_EDGE_BLAS_F16_FULL("negative_stride_axpy", unified::edge::negative_stride_axpy)
DECLARE_EDGE_BLAS_F16_FULL("padded_gemm", unified::edge::padded_gemm)
DECLARE_EDGE_BLAS_F16_FULL("beta_zero_gemm", unified::edge::beta_zero_gemm)

#undef DECLARE_EDGE_BLAS_F16_FULL

#define DECLARE_EDGE_BLAS_I8_FULL(name, check)                                                                                             \
  TEST_CASE("edge/blas/i8/full/" name, "[unified][full-matrix][edge][blas][i8]") {                                                         \
    const auto size = GENERATE(Catch::Generators::from_range(unified::edge_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    unified::backend::OneMkl<unified::ScalarType::i8> backend;                                                                             \
    check<unified::ScalarType::i8>(backend, size);                                                                                         \
  }

DECLARE_EDGE_BLAS_I8_FULL("negative_stride_axpy", unified::edge::negative_stride_axpy)
DECLARE_EDGE_BLAS_I8_FULL("padded_gemm", unified::edge::padded_gemm)
DECLARE_EDGE_BLAS_I8_FULL("beta_zero_gemm", unified::edge::beta_zero_gemm)

#undef DECLARE_EDGE_BLAS_I8_FULL
