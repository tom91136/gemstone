#include <catch2/catch_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "api_blas.hpp"
#include <magic_enum/magic_enum.hpp>

using unified::ScalarType;
#define DECLARE_BLAS_F32_COMPACT_LEVEL1(name, check)                                                                                       \
  TEST_CASE("blas/f32/compact/" name, "[unified][compact-matrix][blas][f32]") {                                                            \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_blas_sizes));                                                \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_F32_COMPACT_SIGNED_LEVEL1(name, check)                                                                                \
  TEST_CASE("blas/f32/compact/" name, "[unified][compact-matrix][blas][f32]") {                                                            \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::compact_blas_sizes));                                                \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_F32_COMPACT_GEMV(name, check, padded_check)                                                                           \
  TEST_CASE("blas/f32/compact/" name, "[unified][compact-matrix][blas][f32]") {                                                            \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::compact_matrix_shapes));                                            \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_BLAS_F32_COMPACT_GEMM(name, check, padded_check, transpose_a, transpose_b)                                                 \
  TEST_CASE("blas/f32/compact/" name, "[unified][compact-matrix][blas][f32]") {                                                            \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::compact_matrix_shapes));                                            \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_BLAS_F32_COMPACT_BATCHED_GEMM(name)                                                                                        \
  TEST_CASE("blas/f32/compact/" name, "[unified][compact-matrix][blas][f32]") {                                                            \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::compact_matrix_shapes));                                            \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_BLAS_F32_COMPACT_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_BLAS_F32_COMPACT_LEVEL1("scal", unified::blas::scal)
DECLARE_BLAS_F32_COMPACT_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_BLAS_F32_COMPACT_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_BLAS_F32_COMPACT_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_BLAS_F32_COMPACT_LEVEL1("asum", unified::blas::asum)
DECLARE_BLAS_F32_COMPACT_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_BLAS_F32_COMPACT_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_BLAS_F32_COMPACT_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                              unified::blas::Transpose::none)
DECLARE_BLAS_F32_COMPACT_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                              unified::blas::Transpose::transpose)
DECLARE_BLAS_F32_COMPACT_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                              unified::blas::Transpose::none)
DECLARE_BLAS_F32_COMPACT_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                              unified::blas::Transpose::transpose)
DECLARE_BLAS_F32_COMPACT_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_BLAS_F32_COMPACT_LEVEL1
#undef DECLARE_BLAS_F32_COMPACT_SIGNED_LEVEL1
#undef DECLARE_BLAS_F32_COMPACT_GEMV
#undef DECLARE_BLAS_F32_COMPACT_GEMM
#undef DECLARE_BLAS_F32_COMPACT_BATCHED_GEMM

#define DECLARE_BLAS_F32_FULL_LEVEL1(name, check)                                                                                          \
  TEST_CASE("blas/f32/full/" name, "[unified][full-matrix][blas][f32]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_F32_FULL_SIGNED_LEVEL1(name, check)                                                                                   \
  TEST_CASE("blas/f32/full/" name, "[unified][full-matrix][blas][f32]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_F32_FULL_GEMV(name, check, padded_check)                                                                              \
  TEST_CASE("blas/f32/full/" name, "[unified][full-matrix][blas][f32]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_BLAS_F32_FULL_GEMM(name, check, padded_check, transpose_a, transpose_b)                                                    \
  TEST_CASE("blas/f32/full/" name, "[unified][full-matrix][blas][f32]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_BLAS_F32_FULL_BATCHED_GEMM(name)                                                                                           \
  TEST_CASE("blas/f32/full/" name, "[unified][full-matrix][blas][f32]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_BLAS_F32_FULL_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_BLAS_F32_FULL_LEVEL1("scal", unified::blas::scal)
DECLARE_BLAS_F32_FULL_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_BLAS_F32_FULL_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_BLAS_F32_FULL_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_BLAS_F32_FULL_LEVEL1("asum", unified::blas::asum)
DECLARE_BLAS_F32_FULL_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_BLAS_F32_FULL_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_BLAS_F32_FULL_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                           unified::blas::Transpose::none)
DECLARE_BLAS_F32_FULL_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                           unified::blas::Transpose::transpose)
DECLARE_BLAS_F32_FULL_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                           unified::blas::Transpose::none)
DECLARE_BLAS_F32_FULL_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                           unified::blas::Transpose::transpose)
DECLARE_BLAS_F32_FULL_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_BLAS_F32_FULL_LEVEL1
#undef DECLARE_BLAS_F32_FULL_SIGNED_LEVEL1
#undef DECLARE_BLAS_F32_FULL_GEMV
#undef DECLARE_BLAS_F32_FULL_GEMM
#undef DECLARE_BLAS_F32_FULL_BATCHED_GEMM

#define DECLARE_BLAS_F64_FULL_LEVEL1(name, check)                                                                                          \
  TEST_CASE("blas/f64/full/" name, "[unified][full-matrix][blas][f64]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_F64_FULL_SIGNED_LEVEL1(name, check)                                                                                   \
  TEST_CASE("blas/f64/full/" name, "[unified][full-matrix][blas][f64]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_F64_FULL_GEMV(name, check, padded_check)                                                                              \
  TEST_CASE("blas/f64/full/" name, "[unified][full-matrix][blas][f64]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_BLAS_F64_FULL_GEMM(name, check, padded_check, transpose_a, transpose_b)                                                    \
  TEST_CASE("blas/f64/full/" name, "[unified][full-matrix][blas][f64]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_BLAS_F64_FULL_BATCHED_GEMM(name)                                                                                           \
  TEST_CASE("blas/f64/full/" name, "[unified][full-matrix][blas][f64]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_BLAS_F64_FULL_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_BLAS_F64_FULL_LEVEL1("scal", unified::blas::scal)
DECLARE_BLAS_F64_FULL_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_BLAS_F64_FULL_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_BLAS_F64_FULL_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_BLAS_F64_FULL_LEVEL1("asum", unified::blas::asum)
DECLARE_BLAS_F64_FULL_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_BLAS_F64_FULL_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_BLAS_F64_FULL_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                           unified::blas::Transpose::none)
DECLARE_BLAS_F64_FULL_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                           unified::blas::Transpose::transpose)
DECLARE_BLAS_F64_FULL_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                           unified::blas::Transpose::none)
DECLARE_BLAS_F64_FULL_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                           unified::blas::Transpose::transpose)
DECLARE_BLAS_F64_FULL_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_BLAS_F64_FULL_LEVEL1
#undef DECLARE_BLAS_F64_FULL_SIGNED_LEVEL1
#undef DECLARE_BLAS_F64_FULL_GEMV
#undef DECLARE_BLAS_F64_FULL_GEMM
#undef DECLARE_BLAS_F64_FULL_BATCHED_GEMM

#define DECLARE_BLAS_F16_FULL_LEVEL1(name, check)                                                                                          \
  TEST_CASE("blas/f16/full/" name, "[unified][full-matrix][blas][f16]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_F16_FULL_SIGNED_LEVEL1(name, check)                                                                                   \
  TEST_CASE("blas/f16/full/" name, "[unified][full-matrix][blas][f16]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_F16_FULL_GEMV(name, check, padded_check)                                                                              \
  TEST_CASE("blas/f16/full/" name, "[unified][full-matrix][blas][f16]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_BLAS_F16_FULL_GEMM(name, check, padded_check, transpose_a, transpose_b)                                                    \
  TEST_CASE("blas/f16/full/" name, "[unified][full-matrix][blas][f16]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_BLAS_F16_FULL_BATCHED_GEMM(name)                                                                                           \
  TEST_CASE("blas/f16/full/" name, "[unified][full-matrix][blas][f16]") {                                                                  \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_BLAS_F16_FULL_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_BLAS_F16_FULL_LEVEL1("scal", unified::blas::scal)
DECLARE_BLAS_F16_FULL_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_BLAS_F16_FULL_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_BLAS_F16_FULL_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_BLAS_F16_FULL_LEVEL1("asum", unified::blas::asum)
DECLARE_BLAS_F16_FULL_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_BLAS_F16_FULL_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_BLAS_F16_FULL_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                           unified::blas::Transpose::none)
DECLARE_BLAS_F16_FULL_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                           unified::blas::Transpose::transpose)
DECLARE_BLAS_F16_FULL_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                           unified::blas::Transpose::none)
DECLARE_BLAS_F16_FULL_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                           unified::blas::Transpose::transpose)
DECLARE_BLAS_F16_FULL_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_BLAS_F16_FULL_LEVEL1
#undef DECLARE_BLAS_F16_FULL_SIGNED_LEVEL1
#undef DECLARE_BLAS_F16_FULL_GEMV
#undef DECLARE_BLAS_F16_FULL_GEMM
#undef DECLARE_BLAS_F16_FULL_BATCHED_GEMM

#define DECLARE_BLAS_I8_FULL_LEVEL1(name, check)                                                                                           \
  TEST_CASE("blas/i8/full/" name, "[unified][full-matrix][blas][i8]") {                                                                    \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_I8_FULL_SIGNED_LEVEL1(name, check)                                                                                    \
  TEST_CASE("blas/i8/full/" name, "[unified][full-matrix][blas][i8]") {                                                                    \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    /* Only the two-vector routines share the negative-increment rule. */                                                                  \
    const auto increments =                                                                                                                \
        GENERATE(std::pair<int32_t, int32_t>{-2, -3}, std::pair<int32_t, int32_t>{-2, 3}, std::pair<int32_t, int32_t>{2, -3});             \
    CAPTURE(increments.first, increments.second);                                                                                          \
    SECTION("negative increments") { check<kind>(backend, size, increments.first, increments.second, "negative stride"); }                 \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_BLAS_I8_FULL_GEMV(name, check, padded_check)                                                                               \
  TEST_CASE("blas/i8/full/" name, "[unified][full-matrix][blas][i8]") {                                                                    \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_BLAS_I8_FULL_GEMM(name, check, padded_check, transpose_a, transpose_b)                                                     \
  TEST_CASE("blas/i8/full/" name, "[unified][full-matrix][blas][i8]") {                                                                    \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_BLAS_I8_FULL_BATCHED_GEMM(name)                                                                                            \
  TEST_CASE("blas/i8/full/" name, "[unified][full-matrix][blas][i8]") {                                                                    \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::backend::Cublas<kind> backend;                                                                                                \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_BLAS_I8_FULL_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_BLAS_I8_FULL_LEVEL1("scal", unified::blas::scal)
DECLARE_BLAS_I8_FULL_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_BLAS_I8_FULL_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_BLAS_I8_FULL_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_BLAS_I8_FULL_LEVEL1("asum", unified::blas::asum)
DECLARE_BLAS_I8_FULL_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_BLAS_I8_FULL_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_BLAS_I8_FULL_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                          unified::blas::Transpose::none)
DECLARE_BLAS_I8_FULL_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                          unified::blas::Transpose::transpose)
DECLARE_BLAS_I8_FULL_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                          unified::blas::Transpose::none)
DECLARE_BLAS_I8_FULL_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                          unified::blas::Transpose::transpose)
DECLARE_BLAS_I8_FULL_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_BLAS_I8_FULL_LEVEL1
#undef DECLARE_BLAS_I8_FULL_SIGNED_LEVEL1
#undef DECLARE_BLAS_I8_FULL_GEMV
#undef DECLARE_BLAS_I8_FULL_GEMM
#undef DECLARE_BLAS_I8_FULL_BATCHED_GEMM
