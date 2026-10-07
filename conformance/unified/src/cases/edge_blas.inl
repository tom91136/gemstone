#pragma once

#include "cases/grid.inl"
#include "edge_cases.hpp"

#define UNIFIED_EDGE_BLAS_CASE(name, function, SIZES, KIND, TYPE, MATRIX)                                                                  \
  TEST_CASE("edge/blas/" #TYPE "/" #MATRIX "/" #name, "[unified][" #MATRIX "-matrix][edge][blas][" #TYPE "]") {                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::MATRIX::SIZES));                                                     \
    CAPTURE(size);                                                                                                                         \
    unified::backend::Blas<unified::ScalarType::KIND> backend;                                                                             \
    unified::edge::function<unified::ScalarType::KIND>(backend, size);                                                                     \
  }

#define UNIFIED_EDGE_BLAS(KIND, TYPE, MATRIX)                                                                                              \
  UNIFIED_EDGE_BLAS_CASE(negative_stride_axpy, negativeStrideAxpy, EdgeSizes, KIND, TYPE, MATRIX)                                          \
  UNIFIED_EDGE_BLAS_CASE(padded_gemm, paddedGemm, GemmEdgeSizes, KIND, TYPE, MATRIX)                                                       \
  UNIFIED_EDGE_BLAS_CASE(beta_zero_gemm, betaZeroGemm, GemmEdgeSizes, KIND, TYPE, MATRIX)
