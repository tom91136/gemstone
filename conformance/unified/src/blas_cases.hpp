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

#include <aspartame/vector.hpp>
#include <aspartame/view.hpp>
#include <catch2/catch_test_macros.hpp>

#include "test_support.hpp"
#include <magic_enum/magic_enum.hpp>

namespace unified::blas {
enum class Transpose { None, Transposed };
} // namespace unified::blas

// Case names and skip messages spell these values in lowercase.
template <>
constexpr magic_enum::customize::customize_t magic_enum::customize::enum_name<unified::ScalarType>(unified::ScalarType value) noexcept {
  switch (value) {
    case unified::ScalarType::F16: return "f16";
    case unified::ScalarType::F32: return "f32";
    case unified::ScalarType::F64: return "f64";
  }
  return invalid_tag;
}
template <>
constexpr magic_enum::customize::customize_t
magic_enum::customize::enum_name<unified::blas::Transpose>(unified::blas::Transpose value) noexcept {
  switch (value) {
    case unified::blas::Transpose::None: return "none";
    case unified::blas::Transpose::Transposed: return "transpose";
  }
  return invalid_tag;
}

namespace unified::blas {

template <ScalarType> struct NumericTraits;
template <> struct NumericTraits<ScalarType::F16> {
  static constexpr long double Tolerance = 3.0e-2L;
};
template <> struct NumericTraits<ScalarType::F32> {
  static constexpr long double Tolerance = 3.0e-4L;
};
template <> struct NumericTraits<ScalarType::F64> {
  static constexpr long double Tolerance = 3.0e-12L;
};

inline uint16_t floatToHalfBits(float value) {
  const uint32_t bits = support::floatBits(value);
  const uint32_t sign = (bits >> 16) & 0x8000U;
  const uint32_t exponent = (bits >> 23) & 0xffU;
  const uint32_t mantissa = bits & 0x7fffffU;
  if (exponent == 0xffU) return static_cast<uint16_t>(sign | (mantissa == 0 ? 0x7c00U : 0x7e00U));
  const int32_t halfExponent = static_cast<int32_t>(exponent) - 127 + 15;
  if (halfExponent >= 31) return static_cast<uint16_t>(sign | 0x7c00U);
  if (halfExponent <= 0) {
    if (halfExponent < -10) return static_cast<uint16_t>(sign);
    const uint32_t hidden = mantissa | 0x800000U;
    const int32_t shift = 14 - halfExponent;
    uint32_t rounded = hidden >> shift;
    const uint32_t remainder = hidden & ((1U << shift) - 1U);
    const uint32_t halfway = 1U << (shift - 1);
    if (remainder > halfway || (remainder == halfway && (rounded & 1U))) ++rounded;
    return static_cast<uint16_t>(sign | rounded);
  }
  uint32_t rounded = mantissa >> 13;
  const uint32_t remainder = mantissa & 0x1fffU;
  if (remainder > 0x1000U || (remainder == 0x1000U && (rounded & 1U))) ++rounded;
  if (rounded == 0x400U) {
    rounded = 0;
    if (halfExponent + 1 >= 31) return static_cast<uint16_t>(sign | 0x7c00U);
    return static_cast<uint16_t>(sign | ((halfExponent + 1) << 10));
  }
  return static_cast<uint16_t>(sign | (static_cast<uint32_t>(halfExponent) << 10) | rounded);
}

inline float halfBitsToFloat(uint16_t value) {
  const uint32_t sign = static_cast<uint32_t>(value & 0x8000U) << 16;
  uint32_t exponent = (value >> 10) & 0x1fU;
  uint32_t mantissa = value & 0x3ffU;
  uint32_t bits = sign;
  if (exponent == 0) {
    if (mantissa != 0) {
      exponent = 127 - 15 + 1;
      while ((mantissa & 0x400U) == 0) {
        mantissa <<= 1;
        --exponent;
      }
      mantissa &= 0x3ffU;
      bits |= exponent << 23;
    }
    bits |= mantissa << 13;
  } else if (exponent == 0x1fU) {
    bits |= 0x7f800000U | (mantissa << 13);
  } else {
    bits |= (exponent + 127 - 15) << 23 | (mantissa << 13);
  }
  return support::bitsFloat(bits);
}

inline std::vector<long double> values(size_t size, size_t salt) {
  using namespace aspartame;
  return iota(size_t{0}, size) | map([&](size_t index) {
           const auto value = static_cast<int64_t>(((index * 17) + (salt * 5)) % 23) - 11;
           return static_cast<long double>(value) / 8.0L;
         })
         | to_vector();
}

inline size_t stridedSize(size_t n, size_t increment) {
  return n == 0 ? 0 : support::checkedAdd(1, support::checkedMultiply(n - 1, increment, "strided size"), "strided size");
}

inline size_t vectorIndex(size_t n, size_t position, int increment) {
  if (increment < 0) return support::checkedMultiply(n - position - 1, static_cast<size_t>(-increment), "negative vector index");
  return support::checkedMultiply(position, static_cast<size_t>(increment), "vector index");
}

inline size_t matrixStorageSize(size_t leadingDimension, size_t columns) {
  return support::checkedMultiply(leadingDimension, columns, "matrix storage");
}

inline std::vector<bool> stridedMask(size_t storage, size_t n, int increment) {
  std::vector<bool> active(storage, false);
  for (size_t index = 0; index < n; ++index)
    active[vectorIndex(n, index, increment)] = true;
  return active;
}

inline std::string transposeName(Transpose transposeA, Transpose transposeB) {
  std::string result;
  result += magic_enum::enum_name(transposeA).front();
  result += magic_enum::enum_name(transposeB).front();
  return result;
}

inline constexpr std::pair<Transpose, Transpose> GemmTransposeVariants[] = {
    {Transpose::None, Transpose::None},
    {Transpose::None, Transpose::Transposed},
    {Transpose::Transposed, Transpose::None},
    {Transpose::Transposed, Transpose::Transposed},
};

inline constexpr size_t Level1StatusSize = 0;

template <class Buffer> struct Level1Operands {
  std::vector<long double> xHost, yHost;
  Buffer x, y;
};

template <class Backend> auto level1Operands(Backend &backend, size_t n, int incx, int incy) {
  auto xHost = values(stridedSize(n, static_cast<size_t>(std::abs(incx))), support::checkedAdd(n, 1, "salt"));
  auto yHost = values(stridedSize(n, static_cast<size_t>(std::abs(incy))), support::checkedAdd(n, 7, "salt"));
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(x, xHost);
  backend.upload(y, yHost);
  return Level1Operands<decltype(x)>{std::move(xHost), std::move(yHost), std::move(x), std::move(y)};
}

template <ScalarType Kind, class Backend>
void axpy(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("axpy")) SKIP("axpy is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  auto op = level1Operands(backend, n, incx, incy);

  backend.axpy(support::checkedInt(n, "AXPY size"), 0.75L, op.x, incx, op.y, incy);
  auto expected = op.yHost;
  for (size_t index = 0; index < n; ++index)
    expected[vectorIndex(n, index, incy)] += 0.75L * op.xHost[vectorIndex(n, index, incx)];
  const auto active = stridedMask(op.yHost.size(), n, incy);
  {
    INFO(where + "/axpy");
    REQUIRE_THAT((backend.download(op.y, op.yHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void scal(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("scal")) SKIP("scal is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  auto op = level1Operands(backend, n, incx, incy);

  backend.scal(support::checkedInt(n, "SCAL size"), -0.5L, op.x, incx);
  auto expected = op.xHost;
  for (size_t index = 0; index < n; ++index)
    expected[vectorIndex(n, index, incx)] *= -0.5L;
  const auto active = stridedMask(op.xHost.size(), n, incx);
  {
    INFO(where + "/scal");
    REQUIRE_THAT((backend.download(op.x, op.xHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void copy(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("copy")) SKIP("copy is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  auto op = level1Operands(backend, n, incx, incy);

  backend.copy(support::checkedInt(n, "COPY size"), op.x, incx, op.y, incy);
  auto expected = op.yHost;
  for (size_t index = 0; index < n; ++index)
    expected[vectorIndex(n, index, incy)] = op.xHost[vectorIndex(n, index, incx)];
  const auto active = stridedMask(op.yHost.size(), n, incy);
  {
    INFO(where + "/copy");
    REQUIRE_THAT((backend.download(op.y, op.yHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void dot(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  using namespace aspartame;
  if (!backend.supports("dot")) SKIP("dot is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  auto op = level1Operands(backend, n, incx, incy);

  const auto expected = iota(size_t{0}, n) | sum_by([&](size_t index) {
                          return op.xHost[vectorIndex(n, index, incx)] * op.yHost[vectorIndex(n, index, incy)];
                        });
  {
    INFO(where + "/dot");
    REQUIRE_THAT((backend.dot(support::checkedInt(n, "DOT size"), op.x, incx, op.y, incy)),
                 WithinTolerance(NumericTraits<Kind>::Tolerance, expected));
  }
}

template <ScalarType Kind, class Backend>
void nrm2(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  using namespace aspartame;
  if (!backend.supports("nrm2")) SKIP("nrm2 is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  auto op = level1Operands(backend, n, incx, incy);

  const auto expected = iota(size_t{0}, n) | sum_by([&](size_t index) {
                          return op.xHost[vectorIndex(n, index, incx)] * op.xHost[vectorIndex(n, index, incx)];
                        });
  {
    INFO(where + "/nrm2");
    REQUIRE_THAT((backend.nrm2(support::checkedInt(n, "NRM2 size"), op.x, incx)),
                 WithinTolerance(NumericTraits<Kind>::Tolerance, std::sqrt(expected)));
  }
}

template <ScalarType Kind, class Backend>
void asum(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  using namespace aspartame;
  if (!backend.supports("asum")) SKIP("asum is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  auto op = level1Operands(backend, n, incx, incy);

  const auto expected = iota(size_t{0}, n) | sum_by([&](size_t index) { return std::fabs(op.xHost[vectorIndex(n, index, incx)]); });
  {
    INFO(where + "/asum");
    REQUIRE_THAT((backend.asum(support::checkedInt(n, "ASUM size"), op.x, incx)),
                 WithinTolerance(NumericTraits<Kind>::Tolerance, expected));
  }
}

inline std::vector<long double> gemvReference(Transpose transpose, size_t xSize, size_t ySize, size_t lda, int incx, int incy,
                                              const std::vector<long double> &aHost, const std::vector<long double> &xHost,
                                              const std::vector<long double> &yHost) {
  auto expected = yHost;
  for (size_t output = 0; output < ySize; ++output) {
    long double sum = 0.0L;
    for (size_t input = 0; input < xSize; ++input) {
      const auto row = transpose == Transpose::None ? output : input;
      const auto column = transpose == Transpose::None ? input : output;
      sum += aHost[row + (column * lda)] * xHost[vectorIndex(xSize, input, incx)];
    }
    const auto outputIndex = vectorIndex(ySize, output, incy);
    expected[outputIndex] = (1.25L * sum) - (0.5L * yHost[outputIndex]);
  }
  return expected;
}

template <ScalarType Kind, class Backend> void gemv(Backend &backend, const MatrixShape &shape, Transpose transpose = Transpose::None) {
  if (!backend.supports("gemv")) SKIP("gemv is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = support::checkedInt(shape.m, "GEMV M");
  const auto n = support::checkedInt(shape.n, "GEMV N");
  const int lda = m;
  const auto aHost = values(support::checkedMultiply(shape.m, shape.n, "GEMV A"), 3);
  const auto xSize = transpose == Transpose::None ? shape.n : shape.m;
  const auto ySize = transpose == Transpose::None ? shape.m : shape.n;
  const auto xHost = values(xSize, 5);
  const auto yHost = values(ySize, 7);
  auto a = backend.allocate(aHost.size());
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(a, aHost);
  backend.upload(x, xHost);
  backend.upload(y, yHost);
  backend.gemv(transpose, m, n, 1.25L, a, lda, x, 1, -0.5L, y, 1);
  const auto expected = gemvReference(transpose, xSize, ySize, shape.m, 1, 1, aHost, xHost, yHost);
  REQUIRE_THAT((backend.download(y, yHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected));
}

template <ScalarType Kind, class Backend>
void gemvPadded(Backend &backend, const MatrixShape &shape, Transpose transpose = Transpose::None) {
  if (!backend.supports("gemv")) SKIP("gemv is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = support::checkedInt(shape.m, "GEMV M");
  const auto n = support::checkedInt(shape.n, "GEMV N");
  const auto ldaSize = support::checkedAdd(shape.m, 3, "GEMV padded LDA");
  const auto lda = support::checkedInt(ldaSize, "GEMV padded LDA");
  constexpr int incx = 2;
  constexpr int incy = 3;
  const auto xSize = transpose == Transpose::None ? shape.n : shape.m;
  const auto ySize = transpose == Transpose::None ? shape.m : shape.n;
  const auto aHost = values(matrixStorageSize(ldaSize, shape.n), 103);
  const auto xHost = values(stridedSize(xSize, incx), 107);
  const auto yHost = values(stridedSize(ySize, incy), 109);
  auto a = backend.allocate(aHost.size());
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(a, aHost);
  backend.upload(x, xHost);
  backend.upload(y, yHost);
  backend.gemv(transpose, m, n, 1.25L, a, lda, x, incx, -0.5L, y, incy);
  const auto expected = gemvReference(transpose, xSize, ySize, ldaSize, incx, incy, aHost, xHost, yHost);
  const auto active = stridedMask(yHost.size(), ySize, incy);
  REQUIRE_THAT((backend.download(y, yHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected, active));
}

struct GemmLayout {
  size_t lda, ldb, ldc;
  size_t strideA, strideB, strideC;
};

inline GemmLayout gemmLayout(const MatrixShape &shape, Transpose transposeA, Transpose transposeB) {
  return {.lda = transposeA == Transpose::None ? shape.m : shape.k,
          .ldb = transposeB == Transpose::None ? shape.k : shape.n,
          .ldc = shape.m,
          .strideA = support::checkedMultiply(shape.m, shape.k, "GEMM A"),
          .strideB = support::checkedMultiply(shape.k, shape.n, "GEMM B"),
          .strideC = support::checkedMultiply(shape.m, shape.n, "GEMM C")};
}

inline GemmLayout paddedGemmLayout(const MatrixShape &shape, Transpose transposeA, Transpose transposeB) {
  const auto lda = support::checkedAdd(transposeA == Transpose::None ? shape.m : shape.k, 2, "GEMM padded LDA");
  const auto ldb = support::checkedAdd(transposeB == Transpose::None ? shape.k : shape.n, 3, "GEMM padded LDB");
  const auto ldc = support::checkedAdd(shape.m, 4, "GEMM padded LDC");
  const auto aColumns = transposeA == Transpose::None ? shape.k : shape.m;
  const auto bColumns = transposeB == Transpose::None ? shape.n : shape.k;
  return {.lda = lda,
          .ldb = ldb,
          .ldc = ldc,
          .strideA = support::checkedAdd(matrixStorageSize(lda, aColumns), 5, "GEMM A stride"),
          .strideB = support::checkedAdd(matrixStorageSize(ldb, bColumns), 7, "GEMM B stride"),
          .strideC = support::checkedAdd(matrixStorageSize(ldc, shape.n), 11, "GEMM C stride")};
}

struct GemmExpected {
  std::vector<long double> values;
  std::vector<bool> active;
};

inline GemmExpected gemmReference(const MatrixShape &shape, Transpose transposeA, Transpose transposeB, const GemmLayout &layout,
                                  size_t batches, const std::vector<long double> &aHost, const std::vector<long double> &bHost,
                                  const std::vector<long double> &cHost) {
  GemmExpected result{.values = cHost, .active = std::vector<bool>(cHost.size(), false)};
  for (size_t batch = 0; batch < batches; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto aIndex = transposeA == Transpose::None ? row + (inner * layout.lda) : inner + (row * layout.lda);
          const auto bIndex = transposeB == Transpose::None ? inner + (column * layout.ldb) : column + (inner * layout.ldb);
          sum += aHost[(batch * layout.strideA) + aIndex] * bHost[(batch * layout.strideB) + bIndex];
        }
        const auto cIndex = (batch * layout.strideC) + row + (column * layout.ldc);
        result.values[cIndex] = (0.75L * sum) - (0.25L * cHost[cIndex]);
        result.active[cIndex] = true;
      }
  return result;
}

template <ScalarType Kind, class Backend>
void gemm(Backend &backend, const MatrixShape &shape, Transpose transposeA = Transpose::None, Transpose transposeB = Transpose::None) {
  if (!backend.supports("gemm")) SKIP("gemm is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = support::checkedInt(shape.m, "GEMM M");
  const auto n = support::checkedInt(shape.n, "GEMM N");
  const auto k = support::checkedInt(shape.k, "GEMM K");
  const auto layout = gemmLayout(shape, transposeA, transposeB);
  const auto lda = support::checkedInt(layout.lda, "GEMM LDA");
  const auto ldb = support::checkedInt(layout.ldb, "GEMM LDB");
  const int ldc = m;
  const auto batchCount = size_t{1};
  const auto aHost = values(support::checkedMultiply(layout.strideA, batchCount, "GEMM batches A"), 11);
  const auto bHost = values(support::checkedMultiply(layout.strideB, batchCount, "GEMM batches B"), 13);
  const auto cHost = values(support::checkedMultiply(layout.strideC, batchCount, "GEMM batches C"), 17);
  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  backend.gemm(transposeA, transposeB, m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
  const auto expected = gemmReference(shape, transposeA, transposeB, layout, batchCount, aHost, bHost, cHost);
  REQUIRE_THAT((backend.download(c, cHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected.values));
}

template <ScalarType Kind, class Backend>
void gemmStridedBatched(Backend &backend, const MatrixShape &shape, Transpose transposeA = Transpose::None,
                        Transpose transposeB = Transpose::None) {
  if (!backend.supports("gemm_strided_batched"))
    SKIP("gemm_strided_batched is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = support::checkedInt(shape.m, "GEMM M");
  const auto n = support::checkedInt(shape.n, "GEMM N");
  const auto k = support::checkedInt(shape.k, "GEMM K");
  const auto layout = gemmLayout(shape, transposeA, transposeB);
  const auto lda = support::checkedInt(layout.lda, "GEMM LDA");
  const auto ldb = support::checkedInt(layout.ldb, "GEMM LDB");
  const int ldc = m;
  const auto batchCount = size_t{3};
  const auto aHost = values(support::checkedMultiply(layout.strideA, batchCount, "GEMM batches A"), 11);
  const auto bHost = values(support::checkedMultiply(layout.strideB, batchCount, "GEMM batches B"), 13);
  const auto cHost = values(support::checkedMultiply(layout.strideC, batchCount, "GEMM batches C"), 17);
  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  backend.gemmStridedBatched(transposeA, transposeB, m, n, k, 0.75L, a, lda, layout.strideA, b, ldb, layout.strideB, -0.25L, c, ldc,
                             layout.strideC, support::checkedInt(batchCount, "GEMM batch count"));
  const auto expected = gemmReference(shape, transposeA, transposeB, layout, batchCount, aHost, bHost, cHost);
  REQUIRE_THAT((backend.download(c, cHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected.values));
}

template <ScalarType Kind, class Backend>
void gemmPadded(Backend &backend, const MatrixShape &shape, Transpose transposeA = Transpose::None,
                Transpose transposeB = Transpose::None) {
  if (!backend.supports("gemm")) SKIP("gemm is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = support::checkedInt(shape.m, "GEMM M");
  const auto n = support::checkedInt(shape.n, "GEMM N");
  const auto k = support::checkedInt(shape.k, "GEMM K");
  const auto layout = paddedGemmLayout(shape, transposeA, transposeB);
  const auto lda = support::checkedInt(layout.lda, "GEMM padded LDA");
  const auto ldb = support::checkedInt(layout.ldb, "GEMM padded LDB");
  const auto ldc = support::checkedInt(layout.ldc, "GEMM padded LDC");
  const auto batches = size_t{1};
  const auto aHost = values(support::checkedMultiply(layout.strideA, batches, "GEMM padded A"), 113);
  const auto bHost = values(support::checkedMultiply(layout.strideB, batches, "GEMM padded B"), 127);
  const auto cHost = values(support::checkedMultiply(layout.strideC, batches, "GEMM padded C"), 131);
  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  backend.gemm(transposeA, transposeB, m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
  const auto expected = gemmReference(shape, transposeA, transposeB, layout, batches, aHost, bHost, cHost);
  const auto suffix = "gemm/" + transposeName(transposeA, transposeB) + "/padded";
  {
    INFO(suffix);
    REQUIRE_THAT((backend.download(c, cHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected.values, expected.active));
  }
}

template <ScalarType Kind, class Backend>
void gemmStridedBatchedPadded(Backend &backend, const MatrixShape &shape, Transpose transposeA = Transpose::None,
                              Transpose transposeB = Transpose::None) {
  if (!backend.supports("gemm_strided_batched"))
    SKIP("gemm_strided_batched is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = support::checkedInt(shape.m, "GEMM M");
  const auto n = support::checkedInt(shape.n, "GEMM N");
  const auto k = support::checkedInt(shape.k, "GEMM K");
  const auto layout = paddedGemmLayout(shape, transposeA, transposeB);
  const auto lda = support::checkedInt(layout.lda, "GEMM padded LDA");
  const auto ldb = support::checkedInt(layout.ldb, "GEMM padded LDB");
  const auto ldc = support::checkedInt(layout.ldc, "GEMM padded LDC");
  constexpr size_t batches = 3;
  const auto aHost = values(support::checkedMultiply(layout.strideA, batches, "GEMM padded A"), 113);
  const auto bHost = values(support::checkedMultiply(layout.strideB, batches, "GEMM padded B"), 127);
  const auto cHost = values(support::checkedMultiply(layout.strideC, batches, "GEMM padded C"), 131);
  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  backend.gemmStridedBatched(transposeA, transposeB, m, n, k, 0.75L, a, lda, layout.strideA, b, ldb, layout.strideB, -0.25L, c, ldc,
                             layout.strideC, support::checkedInt(batches, "GEMM batch count"));
  const auto expected = gemmReference(shape, transposeA, transposeB, layout, batches, aHost, bHost, cHost);
  const auto suffix = "gemm_strided_batched/" + transposeName(transposeA, transposeB) + "/padded";
  {
    INFO(suffix);
    REQUIRE_THAT((backend.download(c, cHost.size())), WithinTolerance(NumericTraits<Kind>::Tolerance, expected.values, expected.active));
  }
}
} // namespace unified::blas
