#pragma once

#include <cstdint>
#include <utility>

#include "blas_cases.hpp"
#include "cases/grid.inl"

// Shared by the device backends and the host reference: PREFIX and TAGS name the cases, BACKEND is a template over
// ScalarType, SIZES and SHAPES are the level 1 and matrix generators.
#define UNIFIED_BLAS_LEVEL1(PREFIX, TAGS, KIND, BACKEND, SIZES, SHAPES, name)                                                              \
  TEST_CASE(PREFIX #name, TAGS) {                                                                                                          \
    constexpr auto kind = unified::ScalarType::KIND;                                                                                       \
    BACKEND<kind> backend;                                                                                                                 \
    const auto size = GENERATE(Catch::Generators::from_range(SIZES));                                                                      \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { unified::blas::name<kind>(backend, size); }                                                                       \
    SECTION("contiguous") { unified::blas::name<kind>(backend, size, 1, 1, "contiguous"); }                                                \
    SECTION("zero size status") { unified::blas::name<kind>(backend, unified::blas::Level1StatusSize, 1, 1, "status"); }                   \
  }

#define UNIFIED_BLAS_SIGNED_LEVEL1(PREFIX, TAGS, KIND, BACKEND, SIZES, SHAPES, name)                                                       \
  TEST_CASE(PREFIX #name, TAGS) {                                                                                                          \
    constexpr auto kind = unified::ScalarType::KIND;                                                                                       \
    BACKEND<kind> backend;                                                                                                                 \
    const auto size = GENERATE(Catch::Generators::from_range(SIZES));                                                                      \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { unified::blas::name<kind>(backend, size); }                                                                       \
    SECTION("contiguous") { unified::blas::name<kind>(backend, size, 1, 1, "contiguous"); }                                                \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { unified::blas::name<kind>(backend, size, increments.first, increments.second, "negative stride"); }   \
    SECTION("zero size status") { unified::blas::name<kind>(backend, unified::blas::Level1StatusSize, 1, 1, "status"); }                   \
  }

#define UNIFIED_BLAS_GEMV(PREFIX, TAGS, KIND, BACKEND, SIZES, SHAPES, name, TRANSPOSE)                                                     \
  TEST_CASE(PREFIX name, TAGS) {                                                                                                           \
    constexpr auto kind = unified::ScalarType::KIND;                                                                                       \
    BACKEND<kind> backend;                                                                                                                 \
    const auto shape = GENERATE(Catch::Generators::from_range(SHAPES));                                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemv<kind>(backend, shape, unified::blas::Transpose::TRANSPOSE); }                                  \
    SECTION("padded") { unified::blas::gemvPadded<kind>(backend, shape, unified::blas::Transpose::TRANSPOSE); }                            \
  }

#define UNIFIED_BLAS_GEMM(PREFIX, TAGS, KIND, BACKEND, SIZES, SHAPES, name, TRANSPOSE_A, TRANSPOSE_B)                                      \
  TEST_CASE(PREFIX name, TAGS) {                                                                                                           \
    constexpr auto kind = unified::ScalarType::KIND;                                                                                       \
    BACKEND<kind> backend;                                                                                                                 \
    const auto shape = GENERATE(Catch::Generators::from_range(SHAPES));                                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") {                                                                                                                    \
      unified::blas::gemm<kind>(backend, shape, unified::blas::Transpose::TRANSPOSE_A, unified::blas::Transpose::TRANSPOSE_B);             \
    }                                                                                                                                      \
    SECTION("padded") {                                                                                                                    \
      unified::blas::gemmPadded<kind>(backend, shape, unified::blas::Transpose::TRANSPOSE_A, unified::blas::Transpose::TRANSPOSE_B);       \
    }                                                                                                                                      \
  }

#define UNIFIED_BLAS_GEMM_STRIDED_BATCHED(PREFIX, TAGS, KIND, BACKEND, SIZES, SHAPES)                                                      \
  TEST_CASE(PREFIX "gemm_strided_batched", TAGS) {                                                                                         \
    constexpr auto kind = unified::ScalarType::KIND;                                                                                       \
    BACKEND<kind> backend;                                                                                                                 \
    const auto shape = GENERATE(Catch::Generators::from_range(SHAPES));                                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemmStridedBatched<kind>(backend, shape); }                                                         \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::GemmTransposeVariants));                               \
      unified::blas::gemmStridedBatchedPadded<kind>(backend, shape, transposes.first, transposes.second);                                  \
    }                                                                                                                                      \
  }

#define UNIFIED_BLAS_CASES(...)                                                                                                            \
  UNIFIED_BLAS_SIGNED_LEVEL1(__VA_ARGS__, axpy)                                                                                            \
  UNIFIED_BLAS_LEVEL1(__VA_ARGS__, scal)                                                                                                   \
  UNIFIED_BLAS_SIGNED_LEVEL1(__VA_ARGS__, copy)                                                                                            \
  UNIFIED_BLAS_SIGNED_LEVEL1(__VA_ARGS__, dot)                                                                                             \
  UNIFIED_BLAS_LEVEL1(__VA_ARGS__, nrm2)                                                                                                   \
  UNIFIED_BLAS_LEVEL1(__VA_ARGS__, asum)                                                                                                   \
  UNIFIED_BLAS_GEMV(__VA_ARGS__, "gemv", None)                                                                                             \
  UNIFIED_BLAS_GEMV(__VA_ARGS__, "gemv_transpose", Transposed)                                                                             \
  UNIFIED_BLAS_GEMM(__VA_ARGS__, "gemm", None, None)                                                                                       \
  UNIFIED_BLAS_GEMM(__VA_ARGS__, "gemm_nt", None, Transposed)                                                                              \
  UNIFIED_BLAS_GEMM(__VA_ARGS__, "gemm_tn", Transposed, None)                                                                              \
  UNIFIED_BLAS_GEMM(__VA_ARGS__, "gemm_tt", Transposed, Transposed)                                                                        \
  UNIFIED_BLAS_GEMM_STRIDED_BATCHED(__VA_ARGS__)

#define UNIFIED_BLAS(KIND, TYPE, MATRIX)                                                                                                   \
  UNIFIED_BLAS_CASES("blas/" #TYPE "/" #MATRIX "/", "[unified][" #MATRIX "-matrix][blas][" #TYPE "]", KIND, unified::backend::Blas,        \
                     unified::MATRIX::BlasSizes, unified::MATRIX::MatrixShapes)
