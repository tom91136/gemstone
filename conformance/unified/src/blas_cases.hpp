#pragma once
#include <algorithm>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <limits>
#include <string>
#include <utility>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "test_support.hpp"
#include <magic_enum/magic_enum.hpp>

namespace unified::blas {
enum class Transpose { none, transpose };

template <ScalarType> struct NumericTraits;
template <> struct NumericTraits<ScalarType::f16> {
  static constexpr long double tolerance = 3.0e-2L;
};
template <> struct NumericTraits<ScalarType::f32> {
  static constexpr long double tolerance = 3.0e-4L;
};
template <> struct NumericTraits<ScalarType::f64> {
  static constexpr long double tolerance = 3.0e-12L;
};
template <> struct NumericTraits<ScalarType::i8> {
  static constexpr long double tolerance = 0.0L;
};

inline uint16_t float_to_half_bits(float value) {
  uint32_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  const uint32_t sign = (bits >> 16) & 0x8000u;
  const uint32_t exponent = (bits >> 23) & 0xffu;
  const uint32_t mantissa = bits & 0x7fffffu;
  if (exponent == 0xffu) return static_cast<uint16_t>(sign | (mantissa == 0 ? 0x7c00u : 0x7e00u));
  const int32_t half_exponent = static_cast<int32_t>(exponent) - 127 + 15;
  if (half_exponent >= 31) return static_cast<uint16_t>(sign | 0x7c00u);
  if (half_exponent <= 0) {
    if (half_exponent < -10) return static_cast<uint16_t>(sign);
    const uint32_t hidden = mantissa | 0x800000u;
    const int32_t shift = 14 - half_exponent;
    uint32_t rounded = hidden >> shift;
    const uint32_t remainder = hidden & ((1u << shift) - 1u);
    const uint32_t halfway = 1u << (shift - 1);
    if (remainder > halfway || (remainder == halfway && (rounded & 1u))) ++rounded;
    return static_cast<uint16_t>(sign | rounded);
  }
  uint32_t rounded = mantissa >> 13;
  const uint32_t remainder = mantissa & 0x1fffu;
  if (remainder > 0x1000u || (remainder == 0x1000u && (rounded & 1u))) ++rounded;
  if (rounded == 0x400u) {
    rounded = 0;
    if (half_exponent + 1 >= 31) return static_cast<uint16_t>(sign | 0x7c00u);
    return static_cast<uint16_t>(sign | ((half_exponent + 1) << 10));
  }
  return static_cast<uint16_t>(sign | (static_cast<uint32_t>(half_exponent) << 10) | rounded);
}

inline float half_bits_to_float(uint16_t value) {
  const uint32_t sign = static_cast<uint32_t>(value & 0x8000u) << 16;
  uint32_t exponent = (value >> 10) & 0x1fu;
  uint32_t mantissa = value & 0x3ffu;
  uint32_t bits = sign;
  if (exponent == 0) {
    if (mantissa != 0) {
      exponent = 127 - 15 + 1;
      while ((mantissa & 0x400u) == 0) {
        mantissa <<= 1;
        --exponent;
      }
      mantissa &= 0x3ffu;
      bits |= exponent << 23;
    }
    bits |= mantissa << 13;
  } else if (exponent == 0x1fu) {
    bits |= 0x7f800000u | (mantissa << 13);
  } else {
    bits |= (exponent + 127 - 15) << 23 | (mantissa << 13);
  }
  float result = 0.0f;
  std::memcpy(&result, &bits, sizeof(bits));
  return result;
}

inline std::vector<long double> values(size_t size, size_t salt) {
  std::vector<long double> result(size);
  for (size_t index = 0; index < size; ++index) {
    const auto value = static_cast<int64_t>((index * 17 + salt * 5) % 23) - 11;
    result[index] = static_cast<long double>(value) / 8.0L;
  }
  return result;
}

inline size_t strided_size(size_t n, size_t increment) {
  return n == 0 ? 0
                : unified::support::checked_add(1, unified::support::checked_multiply(n - 1, increment, "strided size"), "strided size");
}

inline size_t vector_index(size_t n, size_t position, int increment) {
  if (increment < 0) return unified::support::checked_multiply(n - position - 1, static_cast<size_t>(-increment), "negative vector index");
  return unified::support::checked_multiply(position, static_cast<size_t>(increment), "vector index");
}

inline size_t matrix_storage_size(size_t leading_dimension, size_t columns) {
  return unified::support::checked_multiply(leading_dimension, columns, "matrix storage");
}

inline std::string transpose_name(Transpose transpose_a, Transpose transpose_b) {
  std::string result;
  result += magic_enum::enum_name(transpose_a).front();
  result += magic_enum::enum_name(transpose_b).front();
  return result;
}

inline constexpr std::pair<Transpose, Transpose> gemm_transpose_variants[] = {
    {Transpose::none, Transpose::none},
    {Transpose::none, Transpose::transpose},
    {Transpose::transpose, Transpose::none},
    {Transpose::transpose, Transpose::transpose},
};

inline constexpr size_t level1_status_size = 0;

template <ScalarType Kind, class Backend>
void axpy(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports_axpy()) SKIP("axpy is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto x_host = values(strided_size(n, static_cast<size_t>(std::abs(incx))), unified::support::checked_add(n, 1, "salt"));
  const auto y_host = values(strided_size(n, static_cast<size_t>(std::abs(incy))), unified::support::checked_add(n, 7, "salt"));
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(x, x_host);
  backend.upload(y, y_host);

  backend.axpy(unified::support::checked_int(n, "AXPY size"), 0.75L, x, incx, y, incy);
  auto expected = y_host;
  for (size_t index = 0; index < n; ++index)
    expected[vector_index(n, index, incy)] += 0.75L * x_host[vector_index(n, index, incx)];
  std::vector<bool> active(y_host.size(), false);
  for (size_t index = 0; index < n; ++index)
    active[vector_index(n, index, incy)] = true;
  {
    INFO(where + "/axpy");
    REQUIRE_THAT((backend.download(y, y_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void scal(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports_scal()) SKIP("scal is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto x_host = values(strided_size(n, static_cast<size_t>(std::abs(incx))), unified::support::checked_add(n, 1, "salt"));
  const auto y_host = values(strided_size(n, static_cast<size_t>(std::abs(incy))), unified::support::checked_add(n, 7, "salt"));
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(x, x_host);
  backend.upload(y, y_host);

  backend.scal(unified::support::checked_int(n, "SCAL size"), -0.5L, x, incx);
  auto expected = x_host;
  for (size_t index = 0; index < n; ++index)
    expected[vector_index(n, index, incx)] *= -0.5L;
  std::vector<bool> active(x_host.size(), false);
  for (size_t index = 0; index < n; ++index)
    active[vector_index(n, index, incx)] = true;
  {
    INFO(where + "/scal");
    REQUIRE_THAT((backend.download(x, x_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void copy(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports_copy()) SKIP("copy is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto x_host = values(strided_size(n, static_cast<size_t>(std::abs(incx))), unified::support::checked_add(n, 1, "salt"));
  const auto y_host = values(strided_size(n, static_cast<size_t>(std::abs(incy))), unified::support::checked_add(n, 7, "salt"));
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(x, x_host);
  backend.upload(y, y_host);

  backend.copy(unified::support::checked_int(n, "COPY size"), x, incx, y, incy);
  auto expected = y_host;
  for (size_t index = 0; index < n; ++index)
    expected[vector_index(n, index, incy)] = x_host[vector_index(n, index, incx)];
  std::vector<bool> active(y_host.size(), false);
  for (size_t index = 0; index < n; ++index)
    active[vector_index(n, index, incy)] = true;
  {
    INFO(where + "/copy");
    REQUIRE_THAT((backend.download(y, y_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void dot(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports_dot()) SKIP("dot is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto x_host = values(strided_size(n, static_cast<size_t>(std::abs(incx))), unified::support::checked_add(n, 1, "salt"));
  const auto y_host = values(strided_size(n, static_cast<size_t>(std::abs(incy))), unified::support::checked_add(n, 7, "salt"));
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(x, x_host);
  backend.upload(y, y_host);

  long double expected = 0.0L;
  for (size_t index = 0; index < n; ++index)
    expected += x_host[vector_index(n, index, incx)] * y_host[vector_index(n, index, incy)];
  {
    INFO(where + "/dot");
    REQUIRE_THAT((backend.dot(unified::support::checked_int(n, "DOT size"), x, incx, y, incy)),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected));
  }
}

template <ScalarType Kind, class Backend>
void nrm2(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports_nrm2()) SKIP("nrm2 is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto x_host = values(strided_size(n, static_cast<size_t>(std::abs(incx))), unified::support::checked_add(n, 1, "salt"));
  const auto y_host = values(strided_size(n, static_cast<size_t>(std::abs(incy))), unified::support::checked_add(n, 7, "salt"));
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(x, x_host);
  backend.upload(y, y_host);

  long double expected = 0.0L;
  for (size_t index = 0; index < n; ++index)
    expected += x_host[vector_index(n, index, incx)] * x_host[vector_index(n, index, incx)];
  {
    INFO(where + "/nrm2");
    REQUIRE_THAT((backend.nrm2(unified::support::checked_int(n, "NRM2 size"), x, incx)),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, std::sqrt(expected)));
  }
}

template <ScalarType Kind, class Backend>
void asum(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports_asum()) SKIP("asum is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto x_host = values(strided_size(n, static_cast<size_t>(std::abs(incx))), unified::support::checked_add(n, 1, "salt"));
  const auto y_host = values(strided_size(n, static_cast<size_t>(std::abs(incy))), unified::support::checked_add(n, 7, "salt"));
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(x, x_host);
  backend.upload(y, y_host);

  long double expected = 0.0L;
  for (size_t index = 0; index < n; ++index)
    expected += std::fabs(x_host[vector_index(n, index, incx)]);
  {
    INFO(where + "/asum");
    REQUIRE_THAT((backend.asum(unified::support::checked_int(n, "ASUM size"), x, incx)),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected));
  }
}

template <ScalarType Kind, class Backend> void gemv(Backend &backend, const MatrixShape &shape, Transpose transpose = Transpose::none) {
  if (!backend.supports_gemv()) SKIP("gemv is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checked_int(shape.m, "GEMV M");
  const auto n = unified::support::checked_int(shape.n, "GEMV N");
  const int lda = m;
  const auto a_host = values(unified::support::checked_multiply(shape.m, shape.n, "GEMV A"), 3);
  const auto x_size = transpose == Transpose::none ? shape.n : shape.m;
  const auto y_size = transpose == Transpose::none ? shape.m : shape.n;
  const auto x_host = values(x_size, 5);
  const auto y_host = values(y_size, 7);
  auto a = backend.allocate(a_host.size());
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(a, a_host);
  backend.upload(x, x_host);
  backend.upload(y, y_host);
  backend.gemv(transpose, m, n, 1.25L, a, lda, x, 1, -0.5L, y, 1);
  auto expected = y_host;
  for (size_t output = 0; output < y_size; ++output) {
    long double sum = 0.0L;
    for (size_t input = 0; input < x_size; ++input) {
      const auto row = transpose == Transpose::none ? output : input;
      const auto column = transpose == Transpose::none ? input : output;
      sum += a_host[row + column * shape.m] * x_host[input];
    }
    expected[output] = 1.25L * sum - 0.5L * y_host[output];
  }
  {
    INFO("gemv");
    REQUIRE_THAT((backend.download(y, y_host.size())), unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected));
  }
}

template <ScalarType Kind, class Backend>
void gemv_padded(Backend &backend, const MatrixShape &shape, Transpose transpose = Transpose::none) {
  if (!backend.supports_gemv()) SKIP("gemv is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checked_int(shape.m, "GEMV M");
  const auto n = unified::support::checked_int(shape.n, "GEMV N");
  const auto lda_size = unified::support::checked_add(shape.m, 3, "GEMV padded LDA");
  const auto lda = unified::support::checked_int(lda_size, "GEMV padded LDA");
  constexpr int incx = 2;
  constexpr int incy = 3;
  const auto x_size = transpose == Transpose::none ? shape.n : shape.m;
  const auto y_size = transpose == Transpose::none ? shape.m : shape.n;
  const auto a_host = values(matrix_storage_size(lda_size, shape.n), 103);
  const auto x_host = values(strided_size(x_size, incx), 107);
  const auto y_host = values(strided_size(y_size, incy), 109);
  auto a = backend.allocate(a_host.size());
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(a, a_host);
  backend.upload(x, x_host);
  backend.upload(y, y_host);
  backend.gemv(transpose, m, n, 1.25L, a, lda, x, incx, -0.5L, y, incy);
  auto expected = y_host;
  for (size_t output = 0; output < y_size; ++output) {
    long double sum = 0.0L;
    for (size_t input = 0; input < x_size; ++input) {
      const auto row = transpose == Transpose::none ? output : input;
      const auto column = transpose == Transpose::none ? input : output;
      sum += a_host[row + column * lda_size] * x_host[vector_index(x_size, input, incx)];
    }
    const auto output_index = vector_index(y_size, output, incy);
    expected[output_index] = 1.25L * sum - 0.5L * y_host[output_index];
  }
  std::vector<bool> active(y_host.size(), false);
  for (size_t output = 0; output < y_size; ++output)
    active[vector_index(y_size, output, incy)] = true;
  {
    INFO("gemv/padded");
    REQUIRE_THAT((backend.download(y, y_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void gemm(Backend &backend, const MatrixShape &shape, Transpose transpose_a = Transpose::none, Transpose transpose_b = Transpose::none) {
  if (!backend.supports_gemm()) SKIP("gemm is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checked_int(shape.m, "GEMM M");
  const auto n = unified::support::checked_int(shape.n, "GEMM N");
  const auto k = unified::support::checked_int(shape.k, "GEMM K");
  const auto lda_size = transpose_a == Transpose::none ? shape.m : shape.k;
  const auto ldb_size = transpose_b == Transpose::none ? shape.k : shape.n;
  const auto lda = unified::support::checked_int(lda_size, "GEMM LDA");
  const auto ldb = unified::support::checked_int(ldb_size, "GEMM LDB");
  const int ldc = m;
  const auto a_size = unified::support::checked_multiply(shape.m, shape.k, "GEMM A");
  const auto b_size = unified::support::checked_multiply(shape.k, shape.n, "GEMM B");
  const auto c_size = unified::support::checked_multiply(shape.m, shape.n, "GEMM C");
  const auto batch_count = size_t{1};
  const auto a_host = values(unified::support::checked_multiply(a_size, batch_count, "GEMM batches A"), 11);
  const auto b_host = values(unified::support::checked_multiply(b_size, batch_count, "GEMM batches B"), 13);
  const auto c_host = values(unified::support::checked_multiply(c_size, batch_count, "GEMM batches C"), 17);
  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  backend.gemm(transpose_a, transpose_b, m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
  auto expected = c_host;
  for (size_t batch = 0; batch < batch_count; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto a_index = transpose_a == Transpose::none ? row + inner * lda_size : inner + row * lda_size;
          const auto b_index = transpose_b == Transpose::none ? inner + column * ldb_size : column + inner * ldb_size;
          sum += a_host[batch * a_size + a_index] * b_host[batch * b_size + b_index];
        }
        const auto c_index = batch * c_size + row + column * shape.m;
        expected[c_index] = 0.75L * sum - 0.25L * c_host[c_index];
      }
  {
    INFO("gemm");
    REQUIRE_THAT((backend.download(c, c_host.size())), unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected));
  }
}

template <ScalarType Kind, class Backend>
void gemm_strided_batched(Backend &backend, const MatrixShape &shape, Transpose transpose_a = Transpose::none,
                          Transpose transpose_b = Transpose::none) {
  if (!backend.supports_gemm_strided_batched()) SKIP("gemm_strided_batched is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checked_int(shape.m, "GEMM M");
  const auto n = unified::support::checked_int(shape.n, "GEMM N");
  const auto k = unified::support::checked_int(shape.k, "GEMM K");
  const auto lda_size = transpose_a == Transpose::none ? shape.m : shape.k;
  const auto ldb_size = transpose_b == Transpose::none ? shape.k : shape.n;
  const auto lda = unified::support::checked_int(lda_size, "GEMM LDA");
  const auto ldb = unified::support::checked_int(ldb_size, "GEMM LDB");
  const int ldc = m;
  const auto a_size = unified::support::checked_multiply(shape.m, shape.k, "GEMM A");
  const auto b_size = unified::support::checked_multiply(shape.k, shape.n, "GEMM B");
  const auto c_size = unified::support::checked_multiply(shape.m, shape.n, "GEMM C");
  const auto batch_count = size_t{3};
  const auto a_host = values(unified::support::checked_multiply(a_size, batch_count, "GEMM batches A"), 11);
  const auto b_host = values(unified::support::checked_multiply(b_size, batch_count, "GEMM batches B"), 13);
  const auto c_host = values(unified::support::checked_multiply(c_size, batch_count, "GEMM batches C"), 17);
  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  backend.gemm_strided_batched(transpose_a, transpose_b, m, n, k, 0.75L, a, lda, a_size, b, ldb, b_size, -0.25L, c, ldc, c_size,
                               unified::support::checked_int(batch_count, "GEMM batch count"));
  auto expected = c_host;
  for (size_t batch = 0; batch < batch_count; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto a_index = transpose_a == Transpose::none ? row + inner * lda_size : inner + row * lda_size;
          const auto b_index = transpose_b == Transpose::none ? inner + column * ldb_size : column + inner * ldb_size;
          sum += a_host[batch * a_size + a_index] * b_host[batch * b_size + b_index];
        }
        const auto c_index = batch * c_size + row + column * shape.m;
        expected[c_index] = 0.75L * sum - 0.25L * c_host[c_index];
      }
  {
    INFO("gemm_strided_batched");
    REQUIRE_THAT((backend.download(c, c_host.size())), unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected));
  }
}

template <ScalarType Kind, class Backend>
void gemm_padded(Backend &backend, const MatrixShape &shape, Transpose transpose_a = Transpose::none,
                 Transpose transpose_b = Transpose::none) {
  if (!backend.supports_gemm()) SKIP("gemm is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checked_int(shape.m, "GEMM M");
  const auto n = unified::support::checked_int(shape.n, "GEMM N");
  const auto k = unified::support::checked_int(shape.k, "GEMM K");
  const auto lda_size = unified::support::checked_add(transpose_a == Transpose::none ? shape.m : shape.k, 2, "GEMM padded LDA");
  const auto ldb_size = unified::support::checked_add(transpose_b == Transpose::none ? shape.k : shape.n, 3, "GEMM padded LDB");
  const auto ldc_size = unified::support::checked_add(shape.m, 4, "GEMM padded LDC");
  const auto lda = unified::support::checked_int(lda_size, "GEMM padded LDA");
  const auto ldb = unified::support::checked_int(ldb_size, "GEMM padded LDB");
  const auto ldc = unified::support::checked_int(ldc_size, "GEMM padded LDC");
  const auto a_columns = transpose_a == Transpose::none ? shape.k : shape.m;
  const auto b_columns = transpose_b == Transpose::none ? shape.n : shape.k;
  const auto a_matrix_size = matrix_storage_size(lda_size, a_columns);
  const auto b_matrix_size = matrix_storage_size(ldb_size, b_columns);
  const auto c_matrix_size = matrix_storage_size(ldc_size, shape.n);
  const auto batches = size_t{1};
  const auto stride_a = unified::support::checked_add(a_matrix_size, 5, "GEMM A stride");
  const auto stride_b = unified::support::checked_add(b_matrix_size, 7, "GEMM B stride");
  const auto stride_c = unified::support::checked_add(c_matrix_size, 11, "GEMM C stride");
  const auto a_host = values(unified::support::checked_multiply(stride_a, batches, "GEMM padded A"), 113);
  const auto b_host = values(unified::support::checked_multiply(stride_b, batches, "GEMM padded B"), 127);
  const auto c_host = values(unified::support::checked_multiply(stride_c, batches, "GEMM padded C"), 131);
  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  backend.gemm(transpose_a, transpose_b, m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
  auto expected = c_host;
  std::vector<bool> active(c_host.size(), false);
  for (size_t batch = 0; batch < batches; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto a_index = transpose_a == Transpose::none ? row + inner * lda_size : inner + row * lda_size;
          const auto b_index = transpose_b == Transpose::none ? inner + column * ldb_size : column + inner * ldb_size;
          sum += a_host[batch * stride_a + a_index] * b_host[batch * stride_b + b_index];
        }
        const auto c_index = batch * stride_c + row + column * ldc_size;
        expected[c_index] = 0.75L * sum - 0.25L * c_host[c_index];
        active[c_index] = true;
      }
  const auto suffix = "gemm/" + transpose_name(transpose_a, transpose_b) + "/padded";
  {
    INFO(suffix);
    REQUIRE_THAT((backend.download(c, c_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void gemm_strided_batched_padded(Backend &backend, const MatrixShape &shape, Transpose transpose_a = Transpose::none,
                                 Transpose transpose_b = Transpose::none) {
  if (!backend.supports_gemm_strided_batched()) SKIP("gemm_strided_batched is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checked_int(shape.m, "GEMM M");
  const auto n = unified::support::checked_int(shape.n, "GEMM N");
  const auto k = unified::support::checked_int(shape.k, "GEMM K");
  const auto lda_size = unified::support::checked_add(transpose_a == Transpose::none ? shape.m : shape.k, 2, "GEMM padded LDA");
  const auto ldb_size = unified::support::checked_add(transpose_b == Transpose::none ? shape.k : shape.n, 3, "GEMM padded LDB");
  const auto ldc_size = unified::support::checked_add(shape.m, 4, "GEMM padded LDC");
  const auto lda = unified::support::checked_int(lda_size, "GEMM padded LDA");
  const auto ldb = unified::support::checked_int(ldb_size, "GEMM padded LDB");
  const auto ldc = unified::support::checked_int(ldc_size, "GEMM padded LDC");
  const auto a_columns = transpose_a == Transpose::none ? shape.k : shape.m;
  const auto b_columns = transpose_b == Transpose::none ? shape.n : shape.k;
  const auto a_matrix_size = matrix_storage_size(lda_size, a_columns);
  const auto b_matrix_size = matrix_storage_size(ldb_size, b_columns);
  const auto c_matrix_size = matrix_storage_size(ldc_size, shape.n);
  constexpr size_t batch_count = 3;
  const auto batches = batch_count;
  const auto stride_a = unified::support::checked_add(a_matrix_size, 5, "GEMM A stride");
  const auto stride_b = unified::support::checked_add(b_matrix_size, 7, "GEMM B stride");
  const auto stride_c = unified::support::checked_add(c_matrix_size, 11, "GEMM C stride");
  const auto a_host = values(unified::support::checked_multiply(stride_a, batches, "GEMM padded A"), 113);
  const auto b_host = values(unified::support::checked_multiply(stride_b, batches, "GEMM padded B"), 127);
  const auto c_host = values(unified::support::checked_multiply(stride_c, batches, "GEMM padded C"), 131);
  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  backend.gemm_strided_batched(transpose_a, transpose_b, m, n, k, 0.75L, a, lda, stride_a, b, ldb, stride_b, -0.25L, c, ldc, stride_c,
                               unified::support::checked_int(batch_count, "GEMM batch count"));
  auto expected = c_host;
  std::vector<bool> active(c_host.size(), false);
  for (size_t batch = 0; batch < batches; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto a_index = transpose_a == Transpose::none ? row + inner * lda_size : inner + row * lda_size;
          const auto b_index = transpose_b == Transpose::none ? inner + column * ldb_size : column + inner * ldb_size;
          sum += a_host[batch * stride_a + a_index] * b_host[batch * stride_b + b_index];
        }
        const auto c_index = batch * stride_c + row + column * ldc_size;
        expected[c_index] = 0.75L * sum - 0.25L * c_host[c_index];
        active[c_index] = true;
      }
  const auto suffix = "gemm_strided_batched/" + transpose_name(transpose_a, transpose_b) + "/padded";
  {
    INFO(suffix);
    REQUIRE_THAT((backend.download(c, c_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
}
template <ScalarType Kind, class Backend> void gemv_transpose(Backend &backend, const MatrixShape &shape) {
  INFO("gemv_transpose");
  gemv<Kind>(backend, shape, Transpose::transpose);
}

template <ScalarType Kind, class Backend> void gemv_transpose_padded(Backend &backend, const MatrixShape &shape) {
  INFO("gemv_transpose_padded");
  gemv_padded<Kind>(backend, shape, Transpose::transpose);
}

template <ScalarType Kind, class Backend> void gemm_nt(Backend &backend, const MatrixShape &shape) {
  INFO("gemm_nt");
  gemm<Kind>(backend, shape, Transpose::none, Transpose::transpose);
}

template <ScalarType Kind, class Backend> void gemm_nt_padded(Backend &backend, const MatrixShape &shape) {
  INFO("gemm_nt_padded");
  gemm_padded<Kind>(backend, shape, Transpose::none, Transpose::transpose);
}

template <ScalarType Kind, class Backend> void gemm_tn(Backend &backend, const MatrixShape &shape) {
  INFO("gemm_tn");
  gemm<Kind>(backend, shape, Transpose::transpose, Transpose::none);
}

template <ScalarType Kind, class Backend> void gemm_tn_padded(Backend &backend, const MatrixShape &shape) {
  INFO("gemm_tn_padded");
  gemm_padded<Kind>(backend, shape, Transpose::transpose, Transpose::none);
}

template <ScalarType Kind, class Backend> void gemm_tt(Backend &backend, const MatrixShape &shape) {
  INFO("gemm_tt");
  gemm<Kind>(backend, shape, Transpose::transpose, Transpose::transpose);
}

template <ScalarType Kind, class Backend> void gemm_tt_padded(Backend &backend, const MatrixShape &shape) {
  INFO("gemm_tt_padded");
  gemm_padded<Kind>(backend, shape, Transpose::transpose, Transpose::transpose);
}
} // namespace unified::blas
