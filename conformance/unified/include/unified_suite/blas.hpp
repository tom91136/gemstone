#ifndef UNIFIED_SUITE_BLAS_HPP
#define UNIFIED_SUITE_BLAS_HPP

#include "unified_suite/config.hpp"
#include "unified_suite/result.hpp"

#include <algorithm>
#include <array>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <limits>
#include <string>
#include <utility>
#include <vector>

namespace unified_suite::blas {

enum class Transpose { none, transpose };

enum class Operation {
  axpy,
  scal,
  copy,
  dot,
  nrm2,
  asum,
  gemv,
  gemv_transpose,
  gemm,
  gemm_nt,
  gemm_tn,
  gemm_tt,
  gemm_strided_batched
};

inline constexpr std::string_view name(Operation operation) {
  switch (operation) {
  case Operation::axpy:
    return "axpy";
  case Operation::scal:
    return "scal";
  case Operation::copy:
    return "copy";
  case Operation::dot:
    return "dot";
  case Operation::nrm2:
    return "nrm2";
  case Operation::asum:
    return "asum";
  case Operation::gemv:
    return "gemv";
  case Operation::gemv_transpose:
    return "gemv-transpose";
  case Operation::gemm:
    return "gemm";
  case Operation::gemm_nt:
    return "gemm-nt";
  case Operation::gemm_tn:
    return "gemm-tn";
  case Operation::gemm_tt:
    return "gemm-tt";
  case Operation::gemm_strided_batched:
    return "gemm-strided-batched";
  }
  return "unknown";
}

inline constexpr Operation level1_operations[] = {Operation::axpy, Operation::scal, Operation::copy,
                                                  Operation::dot,  Operation::nrm2, Operation::asum};

// BLAS permits negative increments for the two-vector routines. rocBLAS
// treats a non-positive increment as a quick return for the single-vector
// SCAL/NRM2/ASUM routines, so keep those operations on positive layouts and
// exercise both independent signs only where the common contract is valid.
inline constexpr Operation level1_negative_stride_operations[] = {Operation::axpy, Operation::copy,
                                                                   Operation::dot};

constexpr bool has_negative_stride_contract(Operation operation) {
  for (const auto candidate : level1_negative_stride_operations)
    if (candidate == operation)
      return true;
  return false;
}

inline constexpr Operation matrix_operations[] = {Operation::gemv,
                                                  Operation::gemv_transpose,
                                                  Operation::gemm,
                                                  Operation::gemm_nt,
                                                  Operation::gemm_tn,
                                                  Operation::gemm_tt,
                                                  Operation::gemm_strided_batched};

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

namespace detail {

inline std::uint16_t float_to_half_bits(float value) {
  std::uint32_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  const std::uint32_t sign = (bits >> 16) & 0x8000u;
  const std::uint32_t exponent = (bits >> 23) & 0xffu;
  const std::uint32_t mantissa = bits & 0x7fffffu;
  if (exponent == 0xffu)
    return static_cast<std::uint16_t>(sign | (mantissa == 0 ? 0x7c00u : 0x7e00u));
  const int half_exponent = static_cast<int>(exponent) - 127 + 15;
  if (half_exponent >= 31)
    return static_cast<std::uint16_t>(sign | 0x7c00u);
  if (half_exponent <= 0) {
    if (half_exponent < -10)
      return static_cast<std::uint16_t>(sign);
    const std::uint32_t hidden = mantissa | 0x800000u;
    const int shift = 14 - half_exponent;
    std::uint32_t rounded = hidden >> shift;
    const std::uint32_t remainder = hidden & ((1u << shift) - 1u);
    const std::uint32_t halfway = 1u << (shift - 1);
    if (remainder > halfway || (remainder == halfway && (rounded & 1u)))
      ++rounded;
    return static_cast<std::uint16_t>(sign | rounded);
  }
  std::uint32_t rounded = mantissa >> 13;
  const std::uint32_t remainder = mantissa & 0x1fffu;
  if (remainder > 0x1000u || (remainder == 0x1000u && (rounded & 1u)))
    ++rounded;
  if (rounded == 0x400u) {
    rounded = 0;
    if (half_exponent + 1 >= 31)
      return static_cast<std::uint16_t>(sign | 0x7c00u);
    return static_cast<std::uint16_t>(sign | ((half_exponent + 1) << 10));
  }
  return static_cast<std::uint16_t>(sign | (static_cast<std::uint32_t>(half_exponent) << 10) | rounded);
}

inline float half_bits_to_float(std::uint16_t value) {
  const std::uint32_t sign = static_cast<std::uint32_t>(value & 0x8000u) << 16;
  std::uint32_t exponent = (value >> 10) & 0x1fu;
  std::uint32_t mantissa = value & 0x3ffu;
  std::uint32_t bits = sign;
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

inline void require(bool condition, const std::string &message) {
  if (!condition)
    throw std::runtime_error(message);
}

template <ScalarType Kind>
inline void require_close(long double actual, long double expected, const std::string &where) {
  const auto tolerance = NumericTraits<Kind>::tolerance * std::max(1.0L, std::fabs(expected));
  require(std::isfinite(actual), where + ": non-finite result");
  require(std::fabs(actual - expected) <= tolerance,
          where + ": expected " + std::to_string(static_cast<double>(expected)) + ", got " +
              std::to_string(static_cast<double>(actual)));
}

template <ScalarType Kind>
inline void require_close(const std::vector<long double> &actual, const std::vector<long double> &expected,
                          const std::string &where) {
  require(actual.size() == expected.size(), where + ": result size mismatch");
  for (std::size_t index = 0; index < expected.size(); ++index)
    require_close<Kind>(actual[index], expected[index], where + "[" + std::to_string(index) + "]");
}

template <ScalarType Kind>
inline void require_close_active(const std::vector<long double> &actual,
                                 const std::vector<long double> &expected, const std::vector<bool> &active,
                                 const std::string &where) {
  require(actual.size() == expected.size(), where + ": result size mismatch");
  require(active.size() == expected.size(), where + ": active-mask size mismatch");
  for (std::size_t index = 0; index < expected.size(); ++index) {
    const auto position = where + "[" + std::to_string(index) + "]";
    if (active[index])
      require_close<Kind>(actual[index], expected[index], position);
    else
      require(actual[index] == expected[index], position + ": inactive storage changed from " +
                                                    std::to_string(static_cast<double>(expected[index])) +
                                                    " to " +
                                                    std::to_string(static_cast<double>(actual[index])));
  }
}

inline std::vector<long double> values(std::size_t size, std::size_t salt) {
  std::vector<long double> result(size);
  for (std::size_t index = 0; index < size; ++index) {
    const auto value = static_cast<std::int64_t>((index * 17 + salt * 5) % 23) - 11;
    result[index] = static_cast<long double>(value) / 8.0L;
  }
  return result;
}

inline std::size_t strided_size(std::size_t n, std::size_t increment) {
  return n == 0 ? 0
                : unified_suite::detail::checked_add(
                      1, unified_suite::detail::checked_multiply(n - 1, increment, "strided size"),
                      "strided size");
}

inline std::size_t vector_index(std::size_t n, std::size_t position, int increment) {
  if (increment < 0)
    return unified_suite::detail::checked_multiply(n - position - 1, static_cast<std::size_t>(-increment),
                                                   "negative vector index");
  return unified_suite::detail::checked_multiply(position, static_cast<std::size_t>(increment),
                                                 "vector index");
}

inline std::size_t matrix_storage_size(std::size_t leading_dimension, std::size_t columns) {
  return unified_suite::detail::checked_multiply(leading_dimension, columns, "matrix storage");
}

inline std::string transpose_name(Transpose transpose_a, Transpose transpose_b) {
  if (transpose_a == Transpose::none && transpose_b == Transpose::none)
    return "nn";
  if (transpose_a == Transpose::none && transpose_b == Transpose::transpose)
    return "nt";
  if (transpose_a == Transpose::transpose && transpose_b == Transpose::none)
    return "tn";
  return "tt";
}

inline constexpr std::pair<Transpose, Transpose> gemm_transpose_variants[] = {
    {Transpose::none, Transpose::none},
    {Transpose::none, Transpose::transpose},
    {Transpose::transpose, Transpose::none},
    {Transpose::transpose, Transpose::transpose},
};

inline constexpr int level1_status_size = 0;

template <ScalarType Kind, class Backend>
void execute_level1_layout(Backend &backend, Operation operation, std::size_t n, int incx, int incy,
                           const std::string &where) {
  if (!backend.supports(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for " +
                      std::string(unified_suite::name(Kind)));
  const auto x_host = values(strided_size(n, static_cast<std::size_t>(std::abs(incx))),
                             unified_suite::detail::checked_add(n, 1, "salt"));
  const auto y_host = values(strided_size(n, static_cast<std::size_t>(std::abs(incy))),
                             unified_suite::detail::checked_add(n, 7, "salt"));
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(x, x_host);
  backend.upload(y, y_host);
  switch (operation) {
  case Operation::axpy: {
    backend.axpy(unified_suite::detail::checked_int(n, "AXPY size"), 0.75L, x, incx, y, incy);
    auto expected = y_host;
    for (std::size_t index = 0; index < n; ++index)
      expected[vector_index(n, index, incy)] += 0.75L * x_host[vector_index(n, index, incx)];
    std::vector<bool> active(y_host.size(), false);
    for (std::size_t index = 0; index < n; ++index)
      active[vector_index(n, index, incy)] = true;
    require_close_active<Kind>(backend.download(y, y_host.size()), expected, active, where + "/axpy");
    return;
  }
  case Operation::scal: {
    backend.scal(unified_suite::detail::checked_int(n, "SCAL size"), -0.5L, x, incx);
    auto expected = x_host;
    for (std::size_t index = 0; index < n; ++index)
      expected[vector_index(n, index, incx)] *= -0.5L;
    std::vector<bool> active(x_host.size(), false);
    for (std::size_t index = 0; index < n; ++index)
      active[vector_index(n, index, incx)] = true;
    require_close_active<Kind>(backend.download(x, x_host.size()), expected, active, where + "/scal");
    return;
  }
  case Operation::copy: {
    backend.copy(unified_suite::detail::checked_int(n, "COPY size"), x, incx, y, incy);
    auto expected = y_host;
    for (std::size_t index = 0; index < n; ++index)
      expected[vector_index(n, index, incy)] = x_host[vector_index(n, index, incx)];
    std::vector<bool> active(y_host.size(), false);
    for (std::size_t index = 0; index < n; ++index)
      active[vector_index(n, index, incy)] = true;
    require_close_active<Kind>(backend.download(y, y_host.size()), expected, active, where + "/copy");
    return;
  }
  case Operation::dot: {
    long double expected = 0.0L;
    for (std::size_t index = 0; index < n; ++index)
      expected += x_host[vector_index(n, index, incx)] * y_host[vector_index(n, index, incy)];
    require_close<Kind>(backend.dot(unified_suite::detail::checked_int(n, "DOT size"), x, incx, y, incy),
                        expected, where + "/dot");
    return;
  }
  case Operation::nrm2: {
    long double expected = 0.0L;
    for (std::size_t index = 0; index < n; ++index)
      expected += x_host[vector_index(n, index, incx)] * x_host[vector_index(n, index, incx)];
    require_close<Kind>(backend.nrm2(unified_suite::detail::checked_int(n, "NRM2 size"), x, incx),
                        std::sqrt(expected), where + "/nrm2");
    return;
  }
  case Operation::asum: {
    long double expected = 0.0L;
    for (std::size_t index = 0; index < n; ++index)
      expected += std::fabs(x_host[vector_index(n, index, incx)]);
    require_close<Kind>(backend.asum(unified_suite::detail::checked_int(n, "ASUM size"), x, incx), expected,
                        where + "/asum");
    return;
  }
  default:
    throw std::logic_error("non-level-1 BLAS operation");
  }
}

template <ScalarType Kind, class Backend>
void execute_level1(Backend &backend, Operation operation, std::size_t n) {
  execute_level1_layout<Kind>(backend, operation, n, 2, 3, std::string(name(operation)) + "/strided");
}

template <ScalarType Kind, class Backend> void execute_level1_status(Backend &backend, Operation operation) {
  execute_level1_layout<Kind>(backend, operation, level1_status_size, 1, 1,
                              std::string(name(operation)) + "/status");
}

template <ScalarType Kind, class Backend>
void execute_gemv(Backend &backend, Operation operation, const MatrixShape &shape) {
  if (!backend.supports(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for " +
                      std::string(unified_suite::name(Kind)));
  const auto transpose = operation == Operation::gemv_transpose ? Transpose::transpose : Transpose::none;
  const auto m = unified_suite::detail::checked_int(shape.m, "GEMV M");
  const auto n = unified_suite::detail::checked_int(shape.n, "GEMV N");
  const int lda = m;
  const auto a_host = values(unified_suite::detail::checked_multiply(shape.m, shape.n, "GEMV A"), 3);
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
  for (std::size_t output = 0; output < y_size; ++output) {
    long double sum = 0.0L;
    for (std::size_t input = 0; input < x_size; ++input) {
      const auto row = transpose == Transpose::none ? output : input;
      const auto column = transpose == Transpose::none ? input : output;
      sum += a_host[row + column * shape.m] * x_host[input];
    }
    expected[output] = 1.25L * sum - 0.5L * y_host[output];
  }
  require_close<Kind>(backend.download(y, y_host.size()), expected, std::string(name(operation)));
}

template <ScalarType Kind, class Backend>
void execute_gemv_padded(Backend &backend, Operation operation, const MatrixShape &shape) {
  if (!backend.supports(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for " +
                      std::string(unified_suite::name(Kind)));
  const auto transpose = operation == Operation::gemv_transpose ? Transpose::transpose : Transpose::none;
  const auto m = unified_suite::detail::checked_int(shape.m, "GEMV M");
  const auto n = unified_suite::detail::checked_int(shape.n, "GEMV N");
  const auto lda_size = unified_suite::detail::checked_add(shape.m, 3, "GEMV padded LDA");
  const auto lda = unified_suite::detail::checked_int(lda_size, "GEMV padded LDA");
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
  for (std::size_t output = 0; output < y_size; ++output) {
    long double sum = 0.0L;
    for (std::size_t input = 0; input < x_size; ++input) {
      const auto row = transpose == Transpose::none ? output : input;
      const auto column = transpose == Transpose::none ? input : output;
      sum += a_host[row + column * lda_size] * x_host[vector_index(x_size, input, incx)];
    }
    const auto output_index = vector_index(y_size, output, incy);
    expected[output_index] = 1.25L * sum - 0.5L * y_host[output_index];
  }
  std::vector<bool> active(y_host.size(), false);
  for (std::size_t output = 0; output < y_size; ++output)
    active[vector_index(y_size, output, incy)] = true;
  require_close_active<Kind>(backend.download(y, y_host.size()), expected, active,
                             std::string(name(operation)) + "/padded");
}

inline std::pair<Transpose, Transpose> gemm_transposes(Operation operation) {
  switch (operation) {
  case Operation::gemm_nt:
    return {Transpose::none, Transpose::transpose};
  case Operation::gemm_tn:
    return {Transpose::transpose, Transpose::none};
  case Operation::gemm_tt:
    return {Transpose::transpose, Transpose::transpose};
  default:
    return {Transpose::none, Transpose::none};
  }
}

template <ScalarType Kind, class Backend>
void execute_gemm(Backend &backend, Operation operation, const MatrixShape &shape) {
  if (!backend.supports(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for " +
                      std::string(unified_suite::name(Kind)));
  const auto [transpose_a, transpose_b] = gemm_transposes(operation);
  const auto m = unified_suite::detail::checked_int(shape.m, "GEMM M");
  const auto n = unified_suite::detail::checked_int(shape.n, "GEMM N");
  const auto k = unified_suite::detail::checked_int(shape.k, "GEMM K");
  const auto lda_size = transpose_a == Transpose::none ? shape.m : shape.k;
  const auto ldb_size = transpose_b == Transpose::none ? shape.k : shape.n;
  const auto lda = unified_suite::detail::checked_int(lda_size, "GEMM LDA");
  const auto ldb = unified_suite::detail::checked_int(ldb_size, "GEMM LDB");
  const int ldc = m;
  const auto a_size = unified_suite::detail::checked_multiply(shape.m, shape.k, "GEMM A");
  const auto b_size = unified_suite::detail::checked_multiply(shape.k, shape.n, "GEMM B");
  const auto c_size = unified_suite::detail::checked_multiply(shape.m, shape.n, "GEMM C");
  const auto batch_count = operation == Operation::gemm_strided_batched ? std::size_t{3} : std::size_t{1};
  const auto a_host =
      values(unified_suite::detail::checked_multiply(a_size, batch_count, "GEMM batches A"), 11);
  const auto b_host =
      values(unified_suite::detail::checked_multiply(b_size, batch_count, "GEMM batches B"), 13);
  const auto c_host =
      values(unified_suite::detail::checked_multiply(c_size, batch_count, "GEMM batches C"), 17);
  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  if (operation == Operation::gemm_strided_batched)
    backend.gemm_strided_batched(transpose_a, transpose_b, m, n, k, 0.75L, a, lda, a_size, b, ldb, b_size,
                                 -0.25L, c, ldc, c_size,
                                 unified_suite::detail::checked_int(batch_count, "GEMM batch count"));
  else
    backend.gemm(transpose_a, transpose_b, m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
  auto expected = c_host;
  for (std::size_t batch = 0; batch < batch_count; ++batch)
    for (std::size_t column = 0; column < shape.n; ++column)
      for (std::size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (std::size_t inner = 0; inner < shape.k; ++inner) {
          const auto a_index =
              transpose_a == Transpose::none ? row + inner * lda_size : inner + row * lda_size;
          const auto b_index =
              transpose_b == Transpose::none ? inner + column * ldb_size : column + inner * ldb_size;
          sum += a_host[batch * a_size + a_index] * b_host[batch * b_size + b_index];
        }
        const auto c_index = batch * c_size + row + column * shape.m;
        expected[c_index] = 0.75L * sum - 0.25L * c_host[c_index];
      }
  require_close<Kind>(backend.download(c, c_host.size()), expected, std::string(name(operation)));
}

template <ScalarType Kind, class Backend>
void execute_gemm_padded(Backend &backend, Operation operation, const MatrixShape &shape,
                         Transpose transpose_a, Transpose transpose_b) {
  if (!backend.supports(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for " +
                      std::string(unified_suite::name(Kind)));
  const auto m = unified_suite::detail::checked_int(shape.m, "GEMM M");
  const auto n = unified_suite::detail::checked_int(shape.n, "GEMM N");
  const auto k = unified_suite::detail::checked_int(shape.k, "GEMM K");
  const auto lda_size = unified_suite::detail::checked_add(transpose_a == Transpose::none ? shape.m : shape.k,
                                                           2, "GEMM padded LDA");
  const auto ldb_size = unified_suite::detail::checked_add(transpose_b == Transpose::none ? shape.k : shape.n,
                                                           3, "GEMM padded LDB");
  const auto ldc_size = unified_suite::detail::checked_add(shape.m, 4, "GEMM padded LDC");
  const auto lda = unified_suite::detail::checked_int(lda_size, "GEMM padded LDA");
  const auto ldb = unified_suite::detail::checked_int(ldb_size, "GEMM padded LDB");
  const auto ldc = unified_suite::detail::checked_int(ldc_size, "GEMM padded LDC");
  const auto a_columns = transpose_a == Transpose::none ? shape.k : shape.m;
  const auto b_columns = transpose_b == Transpose::none ? shape.n : shape.k;
  const auto a_matrix_size = matrix_storage_size(lda_size, a_columns);
  const auto b_matrix_size = matrix_storage_size(ldb_size, b_columns);
  const auto c_matrix_size = matrix_storage_size(ldc_size, shape.n);
  const bool batched = operation == Operation::gemm_strided_batched;
  constexpr std::size_t batch_count = 3;
  const auto batches = batched ? batch_count : std::size_t{1};
  const auto stride_a = unified_suite::detail::checked_add(a_matrix_size, 5, "GEMM A stride");
  const auto stride_b = unified_suite::detail::checked_add(b_matrix_size, 7, "GEMM B stride");
  const auto stride_c = unified_suite::detail::checked_add(c_matrix_size, 11, "GEMM C stride");
  const auto a_host =
      values(unified_suite::detail::checked_multiply(stride_a, batches, "GEMM padded A"), 113);
  const auto b_host =
      values(unified_suite::detail::checked_multiply(stride_b, batches, "GEMM padded B"), 127);
  const auto c_host =
      values(unified_suite::detail::checked_multiply(stride_c, batches, "GEMM padded C"), 131);
  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  if (batched)
    backend.gemm_strided_batched(transpose_a, transpose_b, m, n, k, 0.75L, a, lda, stride_a, b, ldb, stride_b,
                                 -0.25L, c, ldc, stride_c,
                                 unified_suite::detail::checked_int(batch_count, "GEMM batch count"));
  else
    backend.gemm(transpose_a, transpose_b, m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
  auto expected = c_host;
  std::vector<bool> active(c_host.size(), false);
  for (std::size_t batch = 0; batch < batches; ++batch)
    for (std::size_t column = 0; column < shape.n; ++column)
      for (std::size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (std::size_t inner = 0; inner < shape.k; ++inner) {
          const auto a_index =
              transpose_a == Transpose::none ? row + inner * lda_size : inner + row * lda_size;
          const auto b_index =
              transpose_b == Transpose::none ? inner + column * ldb_size : column + inner * ldb_size;
          sum += a_host[batch * stride_a + a_index] * b_host[batch * stride_b + b_index];
        }
        const auto c_index = batch * stride_c + row + column * ldc_size;
        expected[c_index] = 0.75L * sum - 0.25L * c_host[c_index];
        active[c_index] = true;
      }
  const auto suffix =
      std::string(name(operation)) + "/" + transpose_name(transpose_a, transpose_b) + "/padded";
  require_close_active<Kind>(backend.download(c, c_host.size()), expected, active, suffix);
}

template <template <ScalarType> class Backend, ScalarType Kind>
Case level1_case(Operation operation, std::size_t size) {
  return {"blas/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/n" +
              std::to_string(size),
          [operation, size] {
            return capture([&] {
              Backend<Kind> backend;
              execute_level1<Kind>(backend, operation, size);
            });
          }};
}

template <template <ScalarType> class Backend, ScalarType Kind>
Case level1_layout_case(Operation operation, std::size_t size, int incx, int incy, std::string_view layout) {
  return {"blas/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/n" +
              std::to_string(size) + "/" + std::string(layout),
          [operation, size, incx, incy, layout] {
            return capture([&] {
              Backend<Kind> backend;
              detail::execute_level1_layout<Kind>(backend, operation, size, incx, incy,
                                                  std::string(name(operation)) + "/" + std::string(layout));
            });
          }};
}

template <template <ScalarType> class Backend, ScalarType Kind> Case level1_status_case(Operation operation) {
  return {"blas/status/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) +
              "/n0",
          [operation] {
            return capture([&] {
              Backend<Kind> backend;
              detail::execute_level1_status<Kind>(backend, operation);
            });
          }};
}

template <template <ScalarType> class Backend, ScalarType Kind>
Case matrix_case(Operation operation, MatrixShape shape) {
  return {"blas/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/" +
              unified_suite::name(shape),
          [operation, shape] {
            return capture([&] {
              Backend<Kind> backend;
              if (operation == Operation::gemv || operation == Operation::gemv_transpose)
                execute_gemv<Kind>(backend, operation, shape);
              else
                execute_gemm<Kind>(backend, operation, shape);
            });
          }};
}

template <template <ScalarType> class Backend, ScalarType Kind>
Case gemv_padded_case(Operation operation, MatrixShape shape) {
  return {"blas/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/" +
              unified_suite::name(shape) + "/padded",
          [operation, shape] {
            return capture([&] {
              Backend<Kind> backend;
              detail::execute_gemv_padded<Kind>(backend, operation, shape);
            });
          }};
}

template <template <ScalarType> class Backend, ScalarType Kind>
Case gemm_padded_case(Operation operation, MatrixShape shape, Transpose transpose_a, Transpose transpose_b) {
  return {"blas/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) + "/" +
              unified_suite::name(shape) + "/" + transpose_name(transpose_a, transpose_b) + "/padded",
          [operation, shape, transpose_a, transpose_b] {
            return capture([&] {
              Backend<Kind> backend;
              detail::execute_gemm_padded<Kind>(backend, operation, shape, transpose_a, transpose_b);
            });
          }};
}

template <template <ScalarType> class Backend, ScalarType Kind>
void append_supported_type(std::vector<Case> &result, const Configuration &configuration) {
  for (const auto operation : level1_operations)
    for (const auto size : configuration.blas_sizes) {
      result.push_back(level1_case<Backend, Kind>(operation, size));
      result.push_back(level1_layout_case<Backend, Kind>(operation, size, 1, 1, "contiguous"));
      if (has_negative_stride_contract(operation)) {
        result.push_back(level1_layout_case<Backend, Kind>(operation, size, -2, -3, "negative-stride"));
        result.push_back(level1_layout_case<Backend, Kind>(operation, size, -2, 3, "x-negative"));
        result.push_back(level1_layout_case<Backend, Kind>(operation, size, 2, -3, "y-negative"));
      }
    }
  for (const auto operation : level1_operations)
    result.push_back(level1_status_case<Backend, Kind>(operation));
  for (const auto shape : configuration.matrix_shapes)
    for (const auto operation : matrix_operations) {
      result.push_back(matrix_case<Backend, Kind>(operation, shape));
      if (operation == Operation::gemv || operation == Operation::gemv_transpose)
        result.push_back(gemv_padded_case<Backend, Kind>(operation, shape));
      else if (operation == Operation::gemm || operation == Operation::gemm_nt ||
               operation == Operation::gemm_tn || operation == Operation::gemm_tt)
        result.push_back(gemm_padded_case<Backend, Kind>(operation, shape, gemm_transposes(operation).first,
                                                         gemm_transposes(operation).second));
      else if (operation == Operation::gemm_strided_batched)
        for (const auto [transpose_a, transpose_b] : gemm_transpose_variants)
          result.push_back(gemm_padded_case<Backend, Kind>(operation, shape, transpose_a, transpose_b));
    }
}

template <template <ScalarType> class Backend>
void append_type(std::vector<Case> &result, ScalarType type, const Configuration &configuration) {
  switch (type) {
  case ScalarType::i8:
    append_supported_type<Backend, ScalarType::i8>(result, configuration);
    break;
  case ScalarType::f16:
    append_supported_type<Backend, ScalarType::f16>(result, configuration);
    break;
  case ScalarType::f32:
    append_supported_type<Backend, ScalarType::f32>(result, configuration);
    break;
  case ScalarType::f64:
    append_supported_type<Backend, ScalarType::f64>(result, configuration);
    break;
  case ScalarType::i32:
  case ScalarType::i64:
    for (const auto operation : level1_operations)
      for (const auto size : configuration.blas_sizes) {
        result.push_back({"blas/" + std::string(name(operation)) + "/" +
                              std::string(unified_suite::name(type)) + "/n" + std::to_string(size),
                          [] { return Result::skip("this BLAS scalar type is not instantiated"); }});
        result.push_back({"blas/" + std::string(name(operation)) + "/" +
                              std::string(unified_suite::name(type)) + "/n" + std::to_string(size) +
                              "/contiguous",
                          [] { return Result::skip("this BLAS scalar type is not instantiated"); }});
        if (has_negative_stride_contract(operation)) {
          for (const auto layout : std::array<std::string_view, 3>{"negative-stride", "x-negative", "y-negative"})
            result.push_back({"blas/" + std::string(name(operation)) + "/" +
                                  std::string(unified_suite::name(type)) + "/n" + std::to_string(size) + "/" +
                                  std::string(layout),
                              [] { return Result::skip("this BLAS scalar type is not instantiated"); }});
        }
      }
    for (const auto operation : level1_operations)
      result.push_back({"blas/status/" + std::string(name(operation)) + "/" +
                            std::string(unified_suite::name(type)) + "/n0",
                        [] { return Result::skip("this BLAS scalar type is not instantiated"); }});
    for (const auto shape : configuration.matrix_shapes)
      for (const auto operation : matrix_operations) {
        result.push_back({"blas/" + std::string(name(operation)) + "/" +
                              std::string(unified_suite::name(type)) + "/" + unified_suite::name(shape),
                          [] { return Result::skip("this BLAS scalar type is not instantiated"); }});
        if (operation == Operation::gemv || operation == Operation::gemv_transpose)
          result.push_back({"blas/" + std::string(name(operation)) + "/" +
                                std::string(unified_suite::name(type)) + "/" + unified_suite::name(shape) +
                                "/padded",
                            [] { return Result::skip("this BLAS scalar type is not instantiated"); }});
        else if (operation == Operation::gemm || operation == Operation::gemm_nt ||
                 operation == Operation::gemm_tn || operation == Operation::gemm_tt)
          result.push_back(
              {"blas/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(type)) + "/" +
                   unified_suite::name(shape) + "/" +
                   transpose_name(gemm_transposes(operation).first, gemm_transposes(operation).second) +
                   "/padded",
               [] { return Result::skip("this BLAS scalar type is not instantiated"); }});
        else if (operation == Operation::gemm_strided_batched)
          for (const auto [transpose_a, transpose_b] : gemm_transpose_variants)
            result.push_back({"blas/" + std::string(name(operation)) + "/" +
                                  std::string(unified_suite::name(type)) + "/" + unified_suite::name(shape) +
                                  "/" + transpose_name(transpose_a, transpose_b) + "/padded",
                              [] { return Result::skip("this BLAS scalar type is not instantiated"); }});
      }
    break;
  }
}

} // namespace detail

template <template <ScalarType> class Backend> std::vector<Case> cases(const Configuration &configuration) {
  std::vector<Case> result;
  for (const auto type : configuration.blas_types)
    detail::append_type<Backend>(result, type, configuration);
  return result;
}

} // namespace unified_suite::blas

#endif
