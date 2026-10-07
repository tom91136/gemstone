#include <cmath>
#include <cstddef>
#include <string_view>
#include <vector>

#include "cases/blas.inl"

namespace unified::test {
template <ScalarType Kind> class ReferenceBlas {
public:
  struct Buffer {
    std::vector<long double> values;
  };

  bool supports(std::string_view) const { return true; }
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
    const auto leadingDimension = static_cast<size_t>(lda);
    const auto outputSize = transpose == blas::Transpose::None ? rows : columns;
    const auto inputSize = transpose == blas::Transpose::None ? columns : rows;
    for (size_t output = 0; output < outputSize; ++output) {
      long double sum = 0;
      for (size_t input = 0; input < inputSize; ++input) {
        const auto row = transpose == blas::Transpose::None ? output : input;
        const auto column = transpose == blas::Transpose::None ? input : output;
        sum += a.values[row + column * leadingDimension] * x.values[index(inputSize, input, incx)];
      }
      const auto outputIndex = index(outputSize, output, incy);
      y.values[outputIndex] = alpha * sum + beta * initial[outputIndex];
    }
  }
  void gemm(blas::Transpose transposeA, blas::Transpose transposeB, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    const auto initial = c.values;
    const auto rows = static_cast<size_t>(m);
    const auto columns = static_cast<size_t>(n);
    const auto innerSize = static_cast<size_t>(k);
    const auto leadingA = static_cast<size_t>(lda);
    const auto leadingB = static_cast<size_t>(ldb);
    const auto leadingC = static_cast<size_t>(ldc);
    for (size_t column = 0; column < columns; ++column)
      for (size_t row = 0; row < rows; ++row) {
        long double sum = 0;
        for (size_t inner = 0; inner < innerSize; ++inner)
          sum += a.values[transposeA == blas::Transpose::None ? row + inner * leadingA : inner + row * leadingA]
                 * b.values[transposeB == blas::Transpose::None ? inner + column * leadingB : column + inner * leadingB];
        const auto output = row + column * leadingC;
        c.values[output] = beta == 0 ? alpha * sum : alpha * sum + beta * initial[output];
      }
  }
  void gemmStridedBatched(blas::Transpose transposeA, blas::Transpose transposeB, int m, int n, int k, long double alpha, Buffer &a,
                          int lda, size_t strideA, Buffer &b, int ldb, size_t strideB, long double beta, Buffer &c, int ldc, size_t strideC,
                          int batchCount) {
    const auto initial = c.values;
    const auto batches = static_cast<size_t>(batchCount);
    const auto rows = static_cast<size_t>(m);
    const auto columns = static_cast<size_t>(n);
    const auto innerSize = static_cast<size_t>(k);
    const auto leadingA = static_cast<size_t>(lda);
    const auto leadingB = static_cast<size_t>(ldb);
    const auto leadingC = static_cast<size_t>(ldc);
    for (size_t batch = 0; batch < batches; ++batch)
      for (size_t column = 0; column < columns; ++column)
        for (size_t row = 0; row < rows; ++row) {
          long double sum = 0;
          for (size_t inner = 0; inner < innerSize; ++inner) {
            const auto aIndex = transposeA == blas::Transpose::None ? row + inner * leadingA : inner + row * leadingA;
            const auto bIndex = transposeB == blas::Transpose::None ? inner + column * leadingB : column + inner * leadingB;
            sum += a.values[batch * strideA + aIndex] * b.values[batch * strideB + bIndex];
          }
          const auto cIndex = batch * strideC + row + column * leadingC;
          c.values[cIndex] = beta == 0 ? alpha * sum : alpha * sum + beta * initial[cIndex];
        }
  }

private:
  static size_t index(size_t count, size_t position, int increment) {
    const auto stride = static_cast<size_t>(increment < 0 ? -static_cast<ptrdiff_t>(increment) : increment);
    return (increment < 0 ? count - 1 - position : position) * stride;
  }
};
} // namespace unified::test

UNIFIED_BLAS_CASES("reference/blas/f32/", "[unit][unified][blas][f32][compact]", F32, unified::test::ReferenceBlas,
                   unified::full::BlasSizes, unified::full::MatrixShapes)
UNIFIED_BLAS_CASES("reference/blas/f64/", "[unit][unified][blas][f64]", F64, unified::test::ReferenceBlas, unified::full::BlasSizes,
                   unified::full::MatrixShapes)
UNIFIED_BLAS_CASES("reference/blas/f16/", "[unit][unified][blas][f16]", F16, unified::test::ReferenceBlas, unified::full::BlasSizes,
                   unified::full::MatrixShapes)
