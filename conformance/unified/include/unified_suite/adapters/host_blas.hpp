#ifndef UNIFIED_SUITE_ADAPTERS_HOST_BLAS_HPP
#define UNIFIED_SUITE_ADAPTERS_HOST_BLAS_HPP

#include "unified_suite/blas.hpp"

#include <cmath>
#include <cstddef>
#include <vector>

namespace unified_suite::adapters {

template <ScalarType Kind> class HostBlas {
public:
  struct Buffer {
    std::vector<long double> values;
  };

  bool supports(blas::Operation) const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }
  Buffer allocate(std::size_t size) { return {std::vector<long double>(size)}; }
  void upload(Buffer &buffer, const std::vector<long double> &host) { buffer.values = host; }
  std::vector<long double> download(const Buffer &buffer, std::size_t size) {
    return {buffer.values.begin(), buffer.values.begin() + static_cast<std::ptrdiff_t>(size)};
  }
  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    for (int i = 0; i < n; ++i)
      y.values[index(n, i, incy)] += alpha * x.values[index(n, i, incx)];
  }
  void scal(int n, long double alpha, Buffer &x, int incx) {
    for (int i = 0; i < n; ++i)
      x.values[index(n, i, incx)] *= alpha;
  }
  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    for (int i = 0; i < n; ++i)
      y.values[index(n, i, incy)] = x.values[index(n, i, incx)];
  }
  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    long double result = 0;
    for (int i = 0; i < n; ++i)
      result += x.values[index(n, i, incx)] * y.values[index(n, i, incy)];
    return result;
  }
  long double nrm2(int n, Buffer &x, int incx) {
    long double result = 0;
    for (int i = 0; i < n; ++i)
      result += x.values[index(n, i, incx)] * x.values[index(n, i, incx)];
    return std::sqrt(result);
  }
  long double asum(int n, Buffer &x, int incx) {
    long double result = 0;
    for (int i = 0; i < n; ++i)
      result += std::fabs(x.values[index(n, i, incx)]);
    return result;
  }
  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x,
            int incx, long double beta, Buffer &y, int incy) {
    const auto initial = y.values;
    const auto output_size = transpose == blas::Transpose::none ? m : n;
    const auto input_size = transpose == blas::Transpose::none ? n : m;
    for (int output = 0; output < output_size; ++output) {
      long double sum = 0;
      for (int input = 0; input < input_size; ++input) {
        const auto row = transpose == blas::Transpose::none ? output : input;
        const auto column = transpose == blas::Transpose::none ? input : output;
        sum += a.values[row + column * lda] * x.values[input * incx];
      }
      y.values[output * incy] = alpha * sum + beta * initial[output * incy];
    }
  }
  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha,
            Buffer &a, int lda, Buffer &b, int ldb, long double beta, Buffer &c, int ldc) {
    const auto initial = c.values;
    for (int column = 0; column < n; ++column)
      for (int row = 0; row < m; ++row) {
        long double sum = 0;
        for (int inner = 0; inner < k; ++inner)
          sum += a.values[transpose_a == blas::Transpose::none ? row + inner * lda : inner + row * lda] *
                 b.values[transpose_b == blas::Transpose::none ? inner + column * ldb : column + inner * ldb];
        c.values[row + column * ldc] =
            beta == 0 ? alpha * sum : alpha * sum + beta * initial[row + column * ldc];
      }
  }
  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k,
                            long double alpha, Buffer &a, int lda, std::size_t stride_a, Buffer &b, int ldb,
                            std::size_t stride_b, long double beta, Buffer &c, int ldc, std::size_t stride_c,
                            int batch_count) {
    const auto initial = c.values;
    for (int batch = 0; batch < batch_count; ++batch)
      for (int column = 0; column < n; ++column)
        for (int row = 0; row < m; ++row) {
          long double sum = 0;
          for (int inner = 0; inner < k; ++inner) {
            const auto a_index = transpose_a == blas::Transpose::none ? row + inner * lda : inner + row * lda;
            const auto b_index =
                transpose_b == blas::Transpose::none ? inner + column * ldb : column + inner * ldb;
            sum += a.values[batch * stride_a + a_index] * b.values[batch * stride_b + b_index];
          }
          const auto c_index = batch * stride_c + row + column * ldc;
          c.values[c_index] = beta == 0 ? alpha * sum : alpha * sum + beta * initial[c_index];
        }
  }

private:
  static std::size_t index(int count, int position, int increment) {
    const auto start = increment < 0 ? (count - 1) * -increment : 0;
    return static_cast<std::size_t>(start + position * increment);
  }
};

} // namespace unified_suite::adapters

#endif
