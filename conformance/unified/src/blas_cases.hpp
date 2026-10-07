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
  uint32_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  const uint32_t sign = (bits >> 16) & 0x8000u;
  const uint32_t exponent = (bits >> 23) & 0xffu;
  const uint32_t mantissa = bits & 0x7fffffu;
  if (exponent == 0xffu) return static_cast<uint16_t>(sign | (mantissa == 0 ? 0x7c00u : 0x7e00u));
  const int32_t halfExponent = static_cast<int32_t>(exponent) - 127 + 15;
  if (halfExponent >= 31) return static_cast<uint16_t>(sign | 0x7c00u);
  if (halfExponent <= 0) {
    if (halfExponent < -10) return static_cast<uint16_t>(sign);
    const uint32_t hidden = mantissa | 0x800000u;
    const int32_t shift = 14 - halfExponent;
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
    if (halfExponent + 1 >= 31) return static_cast<uint16_t>(sign | 0x7c00u);
    return static_cast<uint16_t>(sign | ((halfExponent + 1) << 10));
  }
  return static_cast<uint16_t>(sign | (static_cast<uint32_t>(halfExponent) << 10) | rounded);
}

inline float halfBitsToFloat(uint16_t value) {
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

inline size_t stridedSize(size_t n, size_t increment) {
  return n == 0 ? 0 : unified::support::checkedAdd(1, unified::support::checkedMultiply(n - 1, increment, "strided size"), "strided size");
}

inline size_t vectorIndex(size_t n, size_t position, int increment) {
  if (increment < 0) return unified::support::checkedMultiply(n - position - 1, static_cast<size_t>(-increment), "negative vector index");
  return unified::support::checkedMultiply(position, static_cast<size_t>(increment), "vector index");
}

inline size_t matrixStorageSize(size_t leadingDimension, size_t columns) {
  return unified::support::checkedMultiply(leadingDimension, columns, "matrix storage");
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

template <ScalarType Kind, class Backend>
void axpy(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("axpy")) SKIP("axpy is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto xHost = values(stridedSize(n, static_cast<size_t>(std::abs(incx))), unified::support::checkedAdd(n, 1, "salt"));
  const auto yHost = values(stridedSize(n, static_cast<size_t>(std::abs(incy))), unified::support::checkedAdd(n, 7, "salt"));
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(x, xHost);
  backend.upload(y, yHost);

  backend.axpy(unified::support::checkedInt(n, "AXPY size"), 0.75L, x, incx, y, incy);
  auto expected = yHost;
  for (size_t index = 0; index < n; ++index)
    expected[vectorIndex(n, index, incy)] += 0.75L * xHost[vectorIndex(n, index, incx)];
  std::vector<bool> active(yHost.size(), false);
  for (size_t index = 0; index < n; ++index)
    active[vectorIndex(n, index, incy)] = true;
  {
    INFO(where + "/axpy");
    REQUIRE_THAT((backend.download(y, yHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void scal(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("scal")) SKIP("scal is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto xHost = values(stridedSize(n, static_cast<size_t>(std::abs(incx))), unified::support::checkedAdd(n, 1, "salt"));
  const auto yHost = values(stridedSize(n, static_cast<size_t>(std::abs(incy))), unified::support::checkedAdd(n, 7, "salt"));
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(x, xHost);
  backend.upload(y, yHost);

  backend.scal(unified::support::checkedInt(n, "SCAL size"), -0.5L, x, incx);
  auto expected = xHost;
  for (size_t index = 0; index < n; ++index)
    expected[vectorIndex(n, index, incx)] *= -0.5L;
  std::vector<bool> active(xHost.size(), false);
  for (size_t index = 0; index < n; ++index)
    active[vectorIndex(n, index, incx)] = true;
  {
    INFO(where + "/scal");
    REQUIRE_THAT((backend.download(x, xHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void copy(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("copy")) SKIP("copy is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto xHost = values(stridedSize(n, static_cast<size_t>(std::abs(incx))), unified::support::checkedAdd(n, 1, "salt"));
  const auto yHost = values(stridedSize(n, static_cast<size_t>(std::abs(incy))), unified::support::checkedAdd(n, 7, "salt"));
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(x, xHost);
  backend.upload(y, yHost);

  backend.copy(unified::support::checkedInt(n, "COPY size"), x, incx, y, incy);
  auto expected = yHost;
  for (size_t index = 0; index < n; ++index)
    expected[vectorIndex(n, index, incy)] = xHost[vectorIndex(n, index, incx)];
  std::vector<bool> active(yHost.size(), false);
  for (size_t index = 0; index < n; ++index)
    active[vectorIndex(n, index, incy)] = true;
  {
    INFO(where + "/copy");
    REQUIRE_THAT((backend.download(y, yHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void dot(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("dot")) SKIP("dot is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto xHost = values(stridedSize(n, static_cast<size_t>(std::abs(incx))), unified::support::checkedAdd(n, 1, "salt"));
  const auto yHost = values(stridedSize(n, static_cast<size_t>(std::abs(incy))), unified::support::checkedAdd(n, 7, "salt"));
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(x, xHost);
  backend.upload(y, yHost);

  long double expected = 0.0L;
  for (size_t index = 0; index < n; ++index)
    expected += xHost[vectorIndex(n, index, incx)] * yHost[vectorIndex(n, index, incy)];
  {
    INFO(where + "/dot");
    REQUIRE_THAT((backend.dot(unified::support::checkedInt(n, "DOT size"), x, incx, y, incy)),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected));
  }
}

template <ScalarType Kind, class Backend>
void nrm2(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("nrm2")) SKIP("nrm2 is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto xHost = values(stridedSize(n, static_cast<size_t>(std::abs(incx))), unified::support::checkedAdd(n, 1, "salt"));
  const auto yHost = values(stridedSize(n, static_cast<size_t>(std::abs(incy))), unified::support::checkedAdd(n, 7, "salt"));
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(x, xHost);
  backend.upload(y, yHost);

  long double expected = 0.0L;
  for (size_t index = 0; index < n; ++index)
    expected += xHost[vectorIndex(n, index, incx)] * xHost[vectorIndex(n, index, incx)];
  {
    INFO(where + "/nrm2");
    REQUIRE_THAT((backend.nrm2(unified::support::checkedInt(n, "NRM2 size"), x, incx)),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, std::sqrt(expected)));
  }
}

template <ScalarType Kind, class Backend>
void asum(Backend &backend, size_t n, int incx = 2, int incy = 3, const std::string &where = "strided") {
  if (!backend.supports("asum")) SKIP("asum is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto xHost = values(stridedSize(n, static_cast<size_t>(std::abs(incx))), unified::support::checkedAdd(n, 1, "salt"));
  const auto yHost = values(stridedSize(n, static_cast<size_t>(std::abs(incy))), unified::support::checkedAdd(n, 7, "salt"));
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(x, xHost);
  backend.upload(y, yHost);

  long double expected = 0.0L;
  for (size_t index = 0; index < n; ++index)
    expected += std::fabs(xHost[vectorIndex(n, index, incx)]);
  {
    INFO(where + "/asum");
    REQUIRE_THAT((backend.asum(unified::support::checkedInt(n, "ASUM size"), x, incx)),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected));
  }
}

template <ScalarType Kind, class Backend> void gemv(Backend &backend, const MatrixShape &shape, Transpose transpose = Transpose::None) {
  if (!backend.supports("gemv")) SKIP("gemv is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checkedInt(shape.m, "GEMV M");
  const auto n = unified::support::checkedInt(shape.n, "GEMV N");
  const int lda = m;
  const auto aHost = values(unified::support::checkedMultiply(shape.m, shape.n, "GEMV A"), 3);
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
  auto expected = yHost;
  for (size_t output = 0; output < ySize; ++output) {
    long double sum = 0.0L;
    for (size_t input = 0; input < xSize; ++input) {
      const auto row = transpose == Transpose::None ? output : input;
      const auto column = transpose == Transpose::None ? input : output;
      sum += aHost[row + column * shape.m] * xHost[input];
    }
    expected[output] = 1.25L * sum - 0.5L * yHost[output];
  }
  REQUIRE_THAT((backend.download(y, yHost.size())), unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected));
}

template <ScalarType Kind, class Backend>
void gemvPadded(Backend &backend, const MatrixShape &shape, Transpose transpose = Transpose::None) {
  if (!backend.supports("gemv")) SKIP("gemv is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checkedInt(shape.m, "GEMV M");
  const auto n = unified::support::checkedInt(shape.n, "GEMV N");
  const auto ldaSize = unified::support::checkedAdd(shape.m, 3, "GEMV padded LDA");
  const auto lda = unified::support::checkedInt(ldaSize, "GEMV padded LDA");
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
  auto expected = yHost;
  for (size_t output = 0; output < ySize; ++output) {
    long double sum = 0.0L;
    for (size_t input = 0; input < xSize; ++input) {
      const auto row = transpose == Transpose::None ? output : input;
      const auto column = transpose == Transpose::None ? input : output;
      sum += aHost[row + column * ldaSize] * xHost[vectorIndex(xSize, input, incx)];
    }
    const auto outputIndex = vectorIndex(ySize, output, incy);
    expected[outputIndex] = 1.25L * sum - 0.5L * yHost[outputIndex];
  }
  std::vector<bool> active(yHost.size(), false);
  for (size_t output = 0; output < ySize; ++output)
    active[vectorIndex(ySize, output, incy)] = true;
  {
    REQUIRE_THAT((backend.download(y, yHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void gemm(Backend &backend, const MatrixShape &shape, Transpose transposeA = Transpose::None, Transpose transposeB = Transpose::None) {
  if (!backend.supports("gemm")) SKIP("gemm is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checkedInt(shape.m, "GEMM M");
  const auto n = unified::support::checkedInt(shape.n, "GEMM N");
  const auto k = unified::support::checkedInt(shape.k, "GEMM K");
  const auto ldaSize = transposeA == Transpose::None ? shape.m : shape.k;
  const auto ldbSize = transposeB == Transpose::None ? shape.k : shape.n;
  const auto lda = unified::support::checkedInt(ldaSize, "GEMM LDA");
  const auto ldb = unified::support::checkedInt(ldbSize, "GEMM LDB");
  const int ldc = m;
  const auto aSize = unified::support::checkedMultiply(shape.m, shape.k, "GEMM A");
  const auto bSize = unified::support::checkedMultiply(shape.k, shape.n, "GEMM B");
  const auto cSize = unified::support::checkedMultiply(shape.m, shape.n, "GEMM C");
  const auto batchCount = size_t{1};
  const auto aHost = values(unified::support::checkedMultiply(aSize, batchCount, "GEMM batches A"), 11);
  const auto bHost = values(unified::support::checkedMultiply(bSize, batchCount, "GEMM batches B"), 13);
  const auto cHost = values(unified::support::checkedMultiply(cSize, batchCount, "GEMM batches C"), 17);
  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  backend.gemm(transposeA, transposeB, m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
  auto expected = cHost;
  for (size_t batch = 0; batch < batchCount; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto aIndex = transposeA == Transpose::None ? row + inner * ldaSize : inner + row * ldaSize;
          const auto bIndex = transposeB == Transpose::None ? inner + column * ldbSize : column + inner * ldbSize;
          sum += aHost[batch * aSize + aIndex] * bHost[batch * bSize + bIndex];
        }
        const auto cIndex = batch * cSize + row + column * shape.m;
        expected[cIndex] = 0.75L * sum - 0.25L * cHost[cIndex];
      }
  REQUIRE_THAT((backend.download(c, cHost.size())), unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected));
}

template <ScalarType Kind, class Backend>
void gemmStridedBatched(Backend &backend, const MatrixShape &shape, Transpose transposeA = Transpose::None,
                        Transpose transposeB = Transpose::None) {
  if (!backend.supports("gemm_strided_batched"))
    SKIP("gemm_strided_batched is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checkedInt(shape.m, "GEMM M");
  const auto n = unified::support::checkedInt(shape.n, "GEMM N");
  const auto k = unified::support::checkedInt(shape.k, "GEMM K");
  const auto ldaSize = transposeA == Transpose::None ? shape.m : shape.k;
  const auto ldbSize = transposeB == Transpose::None ? shape.k : shape.n;
  const auto lda = unified::support::checkedInt(ldaSize, "GEMM LDA");
  const auto ldb = unified::support::checkedInt(ldbSize, "GEMM LDB");
  const int ldc = m;
  const auto aSize = unified::support::checkedMultiply(shape.m, shape.k, "GEMM A");
  const auto bSize = unified::support::checkedMultiply(shape.k, shape.n, "GEMM B");
  const auto cSize = unified::support::checkedMultiply(shape.m, shape.n, "GEMM C");
  const auto batchCount = size_t{3};
  const auto aHost = values(unified::support::checkedMultiply(aSize, batchCount, "GEMM batches A"), 11);
  const auto bHost = values(unified::support::checkedMultiply(bSize, batchCount, "GEMM batches B"), 13);
  const auto cHost = values(unified::support::checkedMultiply(cSize, batchCount, "GEMM batches C"), 17);
  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  backend.gemmStridedBatched(transposeA, transposeB, m, n, k, 0.75L, a, lda, aSize, b, ldb, bSize, -0.25L, c, ldc, cSize,
                             unified::support::checkedInt(batchCount, "GEMM batch count"));
  auto expected = cHost;
  for (size_t batch = 0; batch < batchCount; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto aIndex = transposeA == Transpose::None ? row + inner * ldaSize : inner + row * ldaSize;
          const auto bIndex = transposeB == Transpose::None ? inner + column * ldbSize : column + inner * ldbSize;
          sum += aHost[batch * aSize + aIndex] * bHost[batch * bSize + bIndex];
        }
        const auto cIndex = batch * cSize + row + column * shape.m;
        expected[cIndex] = 0.75L * sum - 0.25L * cHost[cIndex];
      }
  REQUIRE_THAT((backend.download(c, cHost.size())), unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected));
}

template <ScalarType Kind, class Backend>
void gemmPadded(Backend &backend, const MatrixShape &shape, Transpose transposeA = Transpose::None,
                Transpose transposeB = Transpose::None) {
  if (!backend.supports("gemm")) SKIP("gemm is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checkedInt(shape.m, "GEMM M");
  const auto n = unified::support::checkedInt(shape.n, "GEMM N");
  const auto k = unified::support::checkedInt(shape.k, "GEMM K");
  const auto ldaSize = unified::support::checkedAdd(transposeA == Transpose::None ? shape.m : shape.k, 2, "GEMM padded LDA");
  const auto ldbSize = unified::support::checkedAdd(transposeB == Transpose::None ? shape.k : shape.n, 3, "GEMM padded LDB");
  const auto ldcSize = unified::support::checkedAdd(shape.m, 4, "GEMM padded LDC");
  const auto lda = unified::support::checkedInt(ldaSize, "GEMM padded LDA");
  const auto ldb = unified::support::checkedInt(ldbSize, "GEMM padded LDB");
  const auto ldc = unified::support::checkedInt(ldcSize, "GEMM padded LDC");
  const auto aColumns = transposeA == Transpose::None ? shape.k : shape.m;
  const auto bColumns = transposeB == Transpose::None ? shape.n : shape.k;
  const auto aMatrixSize = matrixStorageSize(ldaSize, aColumns);
  const auto bMatrixSize = matrixStorageSize(ldbSize, bColumns);
  const auto cMatrixSize = matrixStorageSize(ldcSize, shape.n);
  const auto batches = size_t{1};
  const auto strideA = unified::support::checkedAdd(aMatrixSize, 5, "GEMM A stride");
  const auto strideB = unified::support::checkedAdd(bMatrixSize, 7, "GEMM B stride");
  const auto strideC = unified::support::checkedAdd(cMatrixSize, 11, "GEMM C stride");
  const auto aHost = values(unified::support::checkedMultiply(strideA, batches, "GEMM padded A"), 113);
  const auto bHost = values(unified::support::checkedMultiply(strideB, batches, "GEMM padded B"), 127);
  const auto cHost = values(unified::support::checkedMultiply(strideC, batches, "GEMM padded C"), 131);
  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  backend.gemm(transposeA, transposeB, m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
  auto expected = cHost;
  std::vector<bool> active(cHost.size(), false);
  for (size_t batch = 0; batch < batches; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto aIndex = transposeA == Transpose::None ? row + inner * ldaSize : inner + row * ldaSize;
          const auto bIndex = transposeB == Transpose::None ? inner + column * ldbSize : column + inner * ldbSize;
          sum += aHost[batch * strideA + aIndex] * bHost[batch * strideB + bIndex];
        }
        const auto cIndex = batch * strideC + row + column * ldcSize;
        expected[cIndex] = 0.75L * sum - 0.25L * cHost[cIndex];
        active[cIndex] = true;
      }
  const auto suffix = "gemm/" + transposeName(transposeA, transposeB) + "/padded";
  {
    INFO(suffix);
    REQUIRE_THAT((backend.download(c, cHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend>
void gemmStridedBatchedPadded(Backend &backend, const MatrixShape &shape, Transpose transposeA = Transpose::None,
                              Transpose transposeB = Transpose::None) {
  if (!backend.supports("gemm_strided_batched"))
    SKIP("gemm_strided_batched is unsupported for " + std::string(magic_enum::enum_name(Kind)));
  const auto m = unified::support::checkedInt(shape.m, "GEMM M");
  const auto n = unified::support::checkedInt(shape.n, "GEMM N");
  const auto k = unified::support::checkedInt(shape.k, "GEMM K");
  const auto ldaSize = unified::support::checkedAdd(transposeA == Transpose::None ? shape.m : shape.k, 2, "GEMM padded LDA");
  const auto ldbSize = unified::support::checkedAdd(transposeB == Transpose::None ? shape.k : shape.n, 3, "GEMM padded LDB");
  const auto ldcSize = unified::support::checkedAdd(shape.m, 4, "GEMM padded LDC");
  const auto lda = unified::support::checkedInt(ldaSize, "GEMM padded LDA");
  const auto ldb = unified::support::checkedInt(ldbSize, "GEMM padded LDB");
  const auto ldc = unified::support::checkedInt(ldcSize, "GEMM padded LDC");
  const auto aColumns = transposeA == Transpose::None ? shape.k : shape.m;
  const auto bColumns = transposeB == Transpose::None ? shape.n : shape.k;
  const auto aMatrixSize = matrixStorageSize(ldaSize, aColumns);
  const auto bMatrixSize = matrixStorageSize(ldbSize, bColumns);
  const auto cMatrixSize = matrixStorageSize(ldcSize, shape.n);
  constexpr size_t batchCount = 3;
  const auto batches = batchCount;
  const auto strideA = unified::support::checkedAdd(aMatrixSize, 5, "GEMM A stride");
  const auto strideB = unified::support::checkedAdd(bMatrixSize, 7, "GEMM B stride");
  const auto strideC = unified::support::checkedAdd(cMatrixSize, 11, "GEMM C stride");
  const auto aHost = values(unified::support::checkedMultiply(strideA, batches, "GEMM padded A"), 113);
  const auto bHost = values(unified::support::checkedMultiply(strideB, batches, "GEMM padded B"), 127);
  const auto cHost = values(unified::support::checkedMultiply(strideC, batches, "GEMM padded C"), 131);
  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  backend.gemmStridedBatched(transposeA, transposeB, m, n, k, 0.75L, a, lda, strideA, b, ldb, strideB, -0.25L, c, ldc, strideC,
                             unified::support::checkedInt(batchCount, "GEMM batch count"));
  auto expected = cHost;
  std::vector<bool> active(cHost.size(), false);
  for (size_t batch = 0; batch < batches; ++batch)
    for (size_t column = 0; column < shape.n; ++column)
      for (size_t row = 0; row < shape.m; ++row) {
        long double sum = 0.0L;
        for (size_t inner = 0; inner < shape.k; ++inner) {
          const auto aIndex = transposeA == Transpose::None ? row + inner * ldaSize : inner + row * ldaSize;
          const auto bIndex = transposeB == Transpose::None ? inner + column * ldbSize : column + inner * ldbSize;
          sum += aHost[batch * strideA + aIndex] * bHost[batch * strideB + bIndex];
        }
        const auto cIndex = batch * strideC + row + column * ldcSize;
        expected[cIndex] = 0.75L * sum - 0.25L * cHost[cIndex];
        active[cIndex] = true;
      }
  const auto suffix = "gemm_strided_batched/" + transposeName(transposeA, transposeB) + "/padded";
  {
    INFO(suffix);
    REQUIRE_THAT((backend.download(c, cHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}
} // namespace unified::blas
