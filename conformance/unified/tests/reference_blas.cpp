
#include <algorithm>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <memory>
#include <numeric>
#include <random>
#include <utility>
#include <vector>

#include <catch2/catch_template_test_macros.hpp>
#include <catch2/generators/catch_generators_adapters.hpp>
#include <catch2/generators/catch_generators_range.hpp>

#include "algorithm_cases.hpp"
#include "async_cases.hpp"
#include "blas_cases.hpp"
#include "capability_defaults.hpp"
#include "edge_cases.hpp"
#include "reference_async.hpp"
#include "stress_cases.hpp"
#include <magic_enum/magic_enum.hpp>

namespace unified::test {
template <ScalarType Kind> class ReferenceBlas {
public:
  struct Buffer {
    std::vector<long double> values;
  };

  bool supports_axpy() const { return true; }

  bool supports_scal() const { return true; }

  bool supports_copy() const { return true; }

  bool supports_dot() const { return true; }

  bool supports_nrm2() const { return true; }

  bool supports_asum() const { return true; }

  bool supports_gemv() const { return true; }

  bool supports_gemm() const { return true; }

  bool supports_gemm_strided_batched() const { return true; }
  Buffer allocate(size_t size) { return {std::vector<long double>(size)}; }
  void upload(Buffer &buffer, const std::vector<long double> &host) { buffer.values = host; }
  std::vector<long double> download(const Buffer &buffer, size_t size) {
    return {buffer.values.begin(), buffer.values.begin() + static_cast<ptrdiff_t>(size)};
  }
  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      y.values[index(size, i, incy)] += alpha * x.values[index(size, i, incx)];
  }
  void scal(int n, long double alpha, Buffer &x, int incx) {
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      x.values[index(size, i, incx)] *= alpha;
  }
  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      y.values[index(size, i, incy)] = x.values[index(size, i, incx)];
  }
  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    long double result = 0;
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      result += x.values[index(size, i, incx)] * y.values[index(size, i, incy)];
    return result;
  }
  long double nrm2(int n, Buffer &x, int incx) {
    long double result = 0;
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      result += x.values[index(size, i, incx)] * x.values[index(size, i, incx)];
    return std::sqrt(result);
  }
  long double asum(int n, Buffer &x, int incx) {
    long double result = 0;
    const auto size = static_cast<size_t>(n);
    for (size_t i = 0; i < size; ++i)
      result += std::fabs(x.values[index(size, i, incx)]);
    return result;
  }
  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx, long double beta,
            Buffer &y, int incy) {
    const auto initial = y.values;
    const auto rows = static_cast<size_t>(m);
    const auto columns = static_cast<size_t>(n);
    const auto leading_dimension = static_cast<size_t>(lda);
    const auto output_size = transpose == blas::Transpose::none ? rows : columns;
    const auto input_size = transpose == blas::Transpose::none ? columns : rows;
    for (size_t output = 0; output < output_size; ++output) {
      long double sum = 0;
      for (size_t input = 0; input < input_size; ++input) {
        const auto row = transpose == blas::Transpose::none ? output : input;
        const auto column = transpose == blas::Transpose::none ? input : output;
        sum += a.values[row + column * leading_dimension] * x.values[index(input_size, input, incx)];
      }
      const auto output_index = index(output_size, output, incy);
      y.values[output_index] = alpha * sum + beta * initial[output_index];
    }
  }
  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    const auto initial = c.values;
    const auto rows = static_cast<size_t>(m);
    const auto columns = static_cast<size_t>(n);
    const auto inner_size = static_cast<size_t>(k);
    const auto leading_a = static_cast<size_t>(lda);
    const auto leading_b = static_cast<size_t>(ldb);
    const auto leading_c = static_cast<size_t>(ldc);
    for (size_t column = 0; column < columns; ++column)
      for (size_t row = 0; row < rows; ++row) {
        long double sum = 0;
        for (size_t inner = 0; inner < inner_size; ++inner)
          sum += a.values[transpose_a == blas::Transpose::none ? row + inner * leading_a : inner + row * leading_a]
                 * b.values[transpose_b == blas::Transpose::none ? inner + column * leading_b : column + inner * leading_b];
        const auto output = row + column * leading_c;
        c.values[output] = beta == 0 ? alpha * sum : alpha * sum + beta * initial[output];
      }
  }
  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a,
                            int lda, size_t stride_a, Buffer &b, int ldb, size_t stride_b, long double beta, Buffer &c, int ldc,
                            size_t stride_c, int batch_count) {
    const auto initial = c.values;
    const auto batches = static_cast<size_t>(batch_count);
    const auto rows = static_cast<size_t>(m);
    const auto columns = static_cast<size_t>(n);
    const auto inner_size = static_cast<size_t>(k);
    const auto leading_a = static_cast<size_t>(lda);
    const auto leading_b = static_cast<size_t>(ldb);
    const auto leading_c = static_cast<size_t>(ldc);
    for (size_t batch = 0; batch < batches; ++batch)
      for (size_t column = 0; column < columns; ++column)
        for (size_t row = 0; row < rows; ++row) {
          long double sum = 0;
          for (size_t inner = 0; inner < inner_size; ++inner) {
            const auto a_index = transpose_a == blas::Transpose::none ? row + inner * leading_a : inner + row * leading_a;
            const auto b_index = transpose_b == blas::Transpose::none ? inner + column * leading_b : column + inner * leading_b;
            sum += a.values[batch * stride_a + a_index] * b.values[batch * stride_b + b_index];
          }
          const auto c_index = batch * stride_c + row + column * leading_c;
          c.values[c_index] = beta == 0 ? alpha * sum : alpha * sum + beta * initial[c_index];
        }
  }

private:
  static size_t index(size_t count, size_t position, int increment) {
    const auto stride = static_cast<size_t>(increment < 0 ? -static_cast<ptrdiff_t>(increment) : increment);
    return (increment < 0 ? count - 1 - position : position) * stride;
  }
};
} // namespace unified::test

using unified::ScalarType;
#define DECLARE_REFERENCE_BLAS_F32_LEVEL1(name, check)                                                                                     \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1(name, check)                                                                              \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
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

#define DECLARE_REFERENCE_BLAS_F32_GEMV(name, check, padded_check)                                                                         \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_REFERENCE_BLAS_F32_GEMM(name, check, padded_check, transpose_a, transpose_b)                                               \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_REFERENCE_BLAS_F32_BATCHED_GEMM(name)                                                                                      \
  TEST_CASE("reference/blas/f32/" name, "[unit][unified][blas][f32][compact]") {                                                           \
    constexpr auto kind = unified::ScalarType::f32;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_REFERENCE_BLAS_F32_LEVEL1("scal", unified::blas::scal)
DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_REFERENCE_BLAS_F32_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_REFERENCE_BLAS_F32_LEVEL1("asum", unified::blas::asum)
DECLARE_REFERENCE_BLAS_F32_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_REFERENCE_BLAS_F32_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_REFERENCE_BLAS_F32_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F32_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F32_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F32_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F32_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_REFERENCE_BLAS_F32_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F32_SIGNED_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F32_GEMV
#undef DECLARE_REFERENCE_BLAS_F32_GEMM
#undef DECLARE_REFERENCE_BLAS_F32_BATCHED_GEMM

#define DECLARE_REFERENCE_BLAS_F64_LEVEL1(name, check)                                                                                     \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1(name, check)                                                                              \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
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

#define DECLARE_REFERENCE_BLAS_F64_GEMV(name, check, padded_check)                                                                         \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_REFERENCE_BLAS_F64_GEMM(name, check, padded_check, transpose_a, transpose_b)                                               \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_REFERENCE_BLAS_F64_BATCHED_GEMM(name)                                                                                      \
  TEST_CASE("reference/blas/f64/" name, "[unit][unified][blas][f64]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f64;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_REFERENCE_BLAS_F64_LEVEL1("scal", unified::blas::scal)
DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_REFERENCE_BLAS_F64_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_REFERENCE_BLAS_F64_LEVEL1("asum", unified::blas::asum)
DECLARE_REFERENCE_BLAS_F64_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_REFERENCE_BLAS_F64_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_REFERENCE_BLAS_F64_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F64_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F64_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F64_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F64_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_REFERENCE_BLAS_F64_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F64_SIGNED_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F64_GEMV
#undef DECLARE_REFERENCE_BLAS_F64_GEMM
#undef DECLARE_REFERENCE_BLAS_F64_BATCHED_GEMM

#define DECLARE_REFERENCE_BLAS_F16_LEVEL1(name, check)                                                                                     \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1(name, check)                                                                              \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
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

#define DECLARE_REFERENCE_BLAS_F16_GEMV(name, check, padded_check)                                                                         \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_REFERENCE_BLAS_F16_GEMM(name, check, padded_check, transpose_a, transpose_b)                                               \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_REFERENCE_BLAS_F16_BATCHED_GEMM(name)                                                                                      \
  TEST_CASE("reference/blas/f16/" name, "[unit][unified][blas][f16]") {                                                                    \
    constexpr auto kind = unified::ScalarType::f16;                                                                                        \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_REFERENCE_BLAS_F16_LEVEL1("scal", unified::blas::scal)
DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_REFERENCE_BLAS_F16_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_REFERENCE_BLAS_F16_LEVEL1("asum", unified::blas::asum)
DECLARE_REFERENCE_BLAS_F16_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_REFERENCE_BLAS_F16_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_REFERENCE_BLAS_F16_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F16_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F16_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_F16_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                                unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_F16_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_REFERENCE_BLAS_F16_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F16_SIGNED_LEVEL1
#undef DECLARE_REFERENCE_BLAS_F16_GEMV
#undef DECLARE_REFERENCE_BLAS_F16_GEMM
#undef DECLARE_REFERENCE_BLAS_F16_BATCHED_GEMM

#define DECLARE_REFERENCE_BLAS_I8_LEVEL1(name, check)                                                                                      \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto size = GENERATE(Catch::Generators::from_range(unified::blas_sizes));                                                        \
    CAPTURE(size);                                                                                                                         \
    SECTION("strided") { check<kind>(backend, size); }                                                                                     \
    SECTION("contiguous") { check<kind>(backend, size, 1, 1, "contiguous"); }                                                              \
    SECTION("zero size status") { check<kind>(backend, unified::blas::level1_status_size, 1, 1, "status"); }                               \
  }

#define DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1(name, check)                                                                               \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
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

#define DECLARE_REFERENCE_BLAS_I8_GEMV(name, check, padded_check)                                                                          \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") { padded_check<kind>(backend, shape); }                                                                              \
  }

#define DECLARE_REFERENCE_BLAS_I8_GEMM(name, check, padded_check, transpose_a, transpose_b)                                                \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { check<kind>(backend, shape); }                                                                                     \
    SECTION("padded") {                                                                                                                    \
      CAPTURE(std::string(magic_enum::enum_name(transpose_a)), std::string(magic_enum::enum_name(transpose_b)));                           \
      padded_check<kind>(backend, shape);                                                                                                  \
    }                                                                                                                                      \
  }

#define DECLARE_REFERENCE_BLAS_I8_BATCHED_GEMM(name)                                                                                       \
  TEST_CASE("reference/blas/i8/" name, "[unit][unified][blas][i8]") {                                                                      \
    constexpr auto kind = unified::ScalarType::i8;                                                                                         \
    unified::test::ReferenceBlas<kind> backend;                                                                                            \
    const auto shape = GENERATE(Catch::Generators::from_range(unified::matrix_shapes));                                                    \
    CAPTURE(shape.m, shape.n, shape.k);                                                                                                    \
    SECTION("matrix") { unified::blas::gemm_strided_batched<kind>(backend, shape); }                                                       \
    SECTION("padded") {                                                                                                                    \
      const auto transposes = GENERATE(Catch::Generators::from_range(unified::blas::gemm_transpose_variants));                             \
      CAPTURE(std::string(magic_enum::enum_name(transposes.first)), std::string(magic_enum::enum_name(transposes.second)));                \
      unified::blas::gemm_strided_batched_padded<kind>(backend, shape, transposes.first, transposes.second);                               \
    }                                                                                                                                      \
  }

DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1("axpy", unified::blas::axpy)
DECLARE_REFERENCE_BLAS_I8_LEVEL1("scal", unified::blas::scal)
DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1("copy", unified::blas::copy)
DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1("dot", unified::blas::dot)
DECLARE_REFERENCE_BLAS_I8_LEVEL1("nrm2", unified::blas::nrm2)
DECLARE_REFERENCE_BLAS_I8_LEVEL1("asum", unified::blas::asum)
DECLARE_REFERENCE_BLAS_I8_GEMV("gemv", unified::blas::gemv, unified::blas::gemv_padded)
DECLARE_REFERENCE_BLAS_I8_GEMV("gemv_transpose", unified::blas::gemv_transpose, unified::blas::gemv_transpose_padded)
DECLARE_REFERENCE_BLAS_I8_GEMM("gemm", unified::blas::gemm, unified::blas::gemm_padded, unified::blas::Transpose::none,
                               unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_I8_GEMM("gemm_nt", unified::blas::gemm_nt, unified::blas::gemm_nt_padded, unified::blas::Transpose::none,
                               unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_I8_GEMM("gemm_tn", unified::blas::gemm_tn, unified::blas::gemm_tn_padded, unified::blas::Transpose::transpose,
                               unified::blas::Transpose::none)
DECLARE_REFERENCE_BLAS_I8_GEMM("gemm_tt", unified::blas::gemm_tt, unified::blas::gemm_tt_padded, unified::blas::Transpose::transpose,
                               unified::blas::Transpose::transpose)
DECLARE_REFERENCE_BLAS_I8_BATCHED_GEMM("gemm_strided_batched")

#undef DECLARE_REFERENCE_BLAS_I8_LEVEL1
#undef DECLARE_REFERENCE_BLAS_I8_SIGNED_LEVEL1
#undef DECLARE_REFERENCE_BLAS_I8_GEMV
#undef DECLARE_REFERENCE_BLAS_I8_GEMM
#undef DECLARE_REFERENCE_BLAS_I8_BATCHED_GEMM
