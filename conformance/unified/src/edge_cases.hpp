#pragma once
#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <iterator>
#include <limits>
#include <numeric>
#include <string>
#include <type_traits>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "blas_cases.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

namespace unified::edge {
using support::BiasedHash;
using support::Identity;
using support::ReverseIndex;
using support::ValueAt;
using support::WidePairValue;
using support::Xor64;

inline constexpr uint64_t BiasedBase = static_cast<uint64_t>(0x100000005);
inline constexpr size_t GuardSize = 4;
inline constexpr size_t PrimitiveBlockSize = 32;

template <class T> inline std::vector<T> values(size_t size) { return support::edgeValues<T>(size); }

template <class T> inline std::vector<T> alternatingValues(size_t size) {
  std::vector<T> result(size);
  for (size_t index = 0; index < size; ++index)
    result[index] = static_cast<T>(index % 2 == 0 ? 1 : -1);
  return result;
}

inline std::vector<int32_t> segmentsSingletonKeys(size_t size) {
  std::vector<int32_t> result(size);

  std::iota(result.begin(), result.end(), 0);

  return result;
}
inline std::vector<int32_t> segmentsGiantKeys(size_t size) {
  std::vector<int32_t> result(size);

  std::fill(result.begin(), result.end(), 0);

  return result;
}
inline std::vector<int32_t> segmentsBoundaryKeys(size_t size) {
  std::vector<int32_t> result(size);

  constexpr size_t runs[] = {1, 31, 32, 33, 7};
  size_t begin = 0;
  int32_t key = 0;
  size_t run = 0;
  while (begin < size) {
    const auto end = std::min(size, begin + runs[run % std::size(runs)]);
    std::fill(result.begin() + static_cast<ptrdiff_t>(begin), result.begin() + static_cast<ptrdiff_t>(end), key++);
    begin = end;
    ++run;
  }

  return result;
}

template <class T> inline std::vector<T> referenceSegmentedScan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
  auto output = input;
  for (size_t index = 1; index < input.size(); ++index)
    if (keys[index] == keys[index - 1]) output[index] = support::Plus<T>{}(output[index - 1], input[index]);
  return output;
}

template <class T> inline T canary() {
  if constexpr (std::is_floating_point_v<T>) return static_cast<T>(123.5);
  else return static_cast<T>(107);
}

template <class T> inline bool bitEqual(T left, T right) {
  static_assert(std::is_trivially_copyable_v<T>);
  return std::memcmp(&left, &right, sizeof(T)) == 0;
}

template <class T> inline std::vector<T> numericEdges(size_t size) {
  std::vector<T> seeds;
  if constexpr (std::is_floating_point_v<T>) {
    seeds = {T{0},
             -T{0},
             std::numeric_limits<T>::denorm_min(),
             -std::numeric_limits<T>::denorm_min(),
             std::numeric_limits<T>::min(),
             -std::numeric_limits<T>::min(),
             std::numeric_limits<T>::infinity(),
             -std::numeric_limits<T>::infinity(),
             std::numeric_limits<T>::quiet_NaN()};
  } else {
    seeds = {T{0}, T{1}, static_cast<T>(-1), std::numeric_limits<T>::min(), std::numeric_limits<T>::max()};
  }
  std::vector<T> result(size);
  for (size_t index = 0; index < size; ++index)
    result[index] = seeds[index % seeds.size()];
  return result;
}

template <class T, class Backend> void composedIteratorReduce(Backend &backend, size_t size) {
  int64_t expected = 0;
  for (size_t index = 0; index < size; ++index)
    expected += WidePairValue<T>{}(ValueAt<T>{}(size - index - 1), ValueAt<T>{}(index));
  REQUIRE(backend.template composedIteratorReduce<T>(size) == expected);
}

template <class T, class Backend> void biasedIndex64(Backend &backend, size_t size) {
  uint64_t expected = 0;
  for (size_t index = 0; index < size; ++index)
    expected ^= BiasedHash<T>{}(BiasedBase + index);
  REQUIRE(backend.template biasedIndexChecksum<T>(BiasedBase, size) == expected);
}

template <class T, class Backend> void segmentsSingleton(Backend &backend, size_t size) {
  const auto keys = segmentsSingletonKeys(size);
  const auto input = alternatingValues<T>(size);
  REQUIRE(backend.segmentedInclusiveScan(keys, input) == referenceSegmentedScan(keys, input));
}

template <class T, class Backend> void segmentsGiant(Backend &backend, size_t size) {
  const auto keys = segmentsGiantKeys(size);
  const auto input = alternatingValues<T>(size);
  REQUIRE(backend.segmentedInclusiveScan(keys, input) == referenceSegmentedScan(keys, input));
}

template <class T, class Backend> void segmentsBoundary(Backend &backend, size_t size) {
  const auto keys = segmentsBoundaryKeys(size);
  const auto input = alternatingValues<T>(size);
  REQUIRE(backend.segmentedInclusiveScan(keys, input) == referenceSegmentedScan(keys, input));
}

template <class T, class Backend> void inplaceScanCanary(Backend &backend, size_t size) {
  std::vector<T> storage(size + GuardSize * 2, canary<T>());
  const auto input = alternatingValues<T>(size);
  std::copy(input.begin(), input.end(), storage.begin() + static_cast<ptrdiff_t>(GuardSize));
  auto expected = storage;
  for (size_t index = 1; index < size; ++index)
    expected[GuardSize + index] = support::Plus<T>{}(expected[GuardSize + index - 1], input[index]);
  REQUIRE(backend.inplaceInclusiveScan(storage, GuardSize, size) == expected);
}

template <class T, class Backend> void stableSortPayload(Backend &backend, size_t size) {
  std::vector<T> keys(size);
  std::vector<int32_t> payload(size);
  for (size_t index = 0; index < size; ++index) {
    keys[index] = static_cast<T>((index * 7 + 3) % 5);
    payload[index] = static_cast<int32_t>(index);
  }
  const auto actual = backend.stableSortPayload(keys, payload);
  REQUIRE(std::is_sorted(actual.keys.begin(), actual.keys.end()));
  for (size_t index = 0; index < actual.keys.size(); ++index) {
    const auto source = static_cast<size_t>(actual.values[index]);
    REQUIRE(source < size);
    if (!(actual.keys[index] == keys[source]))
      FAIL(fmt::format("stable-sort-payload: association lost at index {}: payload {} expected key {}, got {}", index, actual.values[index],
                       +keys[source], +actual.keys[index]));
    if (index != 0 && actual.keys[index] == actual.keys[index - 1]) REQUIRE(actual.values[index - 1] < actual.values[index]);
  }
}

template <class T, class Backend> void dependencyDiamond(Backend &backend, size_t size) {
  const auto input = values<T>(size);
  auto expected = input;
  for (size_t index = 0; index < size; ++index)
    expected[index] = support::Combine<T>{}(support::Affine<T>{}(input[index]), support::Square<T>{}(input[index]));
  REQUIRE(backend.dependencyDiamond(input) == expected);
}

template <class T, class Backend> void numericEdgeRoundtrip(Backend &backend, size_t size) {
  const auto input = numericEdges<T>(size);
  REQUIRE_THAT(backend.numericEdgeRoundtrip(input), unified::BitwiseEqual(input));
}

template <class T, class Backend> void primitiveBlockScan(Backend &backend, size_t size) {
  const auto input = alternatingValues<T>(size);
  auto expected = input;
  for (size_t index = 0; index < size; ++index)
    if (index % PrimitiveBlockSize != 0) expected[index] = support::Plus<T>{}(expected[index - 1], input[index]);
  const auto actual = backend.primitiveBlockScan(input, PrimitiveBlockSize);
  REQUIRE(actual == expected);
}

template <class T, class Backend> void runtimeErrorRecover(Backend &backend, size_t size) {
  const auto input = values<T>(size);
  const auto actual = backend.runtimeErrorRecover(input);
  REQUIRE(actual == input);
}

template <ScalarType Kind, class Backend> void negativeStrideAxpy(Backend &backend, size_t size) {
  if (!backend.supports("axpy")) SKIP("negative-stride-axpy is unsupported for this type/device");
  constexpr int incx = -2;
  constexpr int incy = -3;
  const auto xHost = blas::values(blas::stridedSize(size, -incx), unified::support::checkedAdd(size, 19, "salt"));
  const auto yHost = blas::values(blas::stridedSize(size, -incy), unified::support::checkedAdd(size, 23, "salt"));
  auto x = backend.allocate(xHost.size());
  auto y = backend.allocate(yHost.size());
  backend.upload(x, xHost);
  backend.upload(y, yHost);
  backend.axpy(unified::support::checkedInt(size, "negative-stride AXPY size"), 0.75L, x, incx, y, incy);
  auto expected = yHost;
  std::vector<bool> active(yHost.size(), false);
  for (size_t index = 0; index < size; ++index) {
    const auto xIndex = (size - index - 1) * static_cast<size_t>(-incx);
    const auto yIndex = (size - index - 1) * static_cast<size_t>(-incy);
    expected[yIndex] += 0.75L * xHost[xIndex];
    active[yIndex] = true;
  }
  {
    REQUIRE_THAT((backend.download(y, yHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend> void paddedGemm(Backend &backend, size_t size) {
  if (size == 0) {
    SUCCEED("padded_gemm is not applicable to an empty matrix");
    return;
  }
  if (!backend.supports("gemm")) SKIP("padded_gemm is unsupported for this type/device");

  const auto m = size;
  const auto n = unified::support::checkedAdd(size / 2, 1, "edge GEMM N");
  const auto k = unified::support::checkedAdd(size / 3, 1, "edge GEMM K");
  const auto lda = unified::support::checkedAdd(m, 3, "edge GEMM LDA");
  const auto ldb = unified::support::checkedAdd(k, 5, "edge GEMM LDB");
  const auto ldc = unified::support::checkedAdd(m, 7, "edge GEMM LDC");
  const long double padding = 77.0L;
  std::vector<long double> aHost(unified::support::checkedMultiply(lda, k, "edge GEMM A"), padding);
  std::vector<long double> bHost(unified::support::checkedMultiply(ldb, n, "edge GEMM B"), padding);
  std::vector<long double> cHost(unified::support::checkedMultiply(ldc, n, "edge GEMM C"), padding);
  for (size_t inner = 0; inner < k; ++inner)
    for (size_t row = 0; row < m; ++row)
      aHost[row + inner * lda] = static_cast<long double>(static_cast<int64_t>((row + inner * 3) % 11) - 5) / 8.0L;
  for (size_t column = 0; column < n; ++column)
    for (size_t inner = 0; inner < k; ++inner)
      bHost[inner + column * ldb] = static_cast<long double>(static_cast<int64_t>((inner * 5 + column) % 13) - 6) / 8.0L;
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row)
      cHost[row + column * ldc] = static_cast<long double>(static_cast<int64_t>((row * 7 + column) % 17) - 8) / 8.0L;

  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  const auto alpha = 0.75L;
  const auto beta = -0.25L;
  backend.gemm(blas::Transpose::None, blas::Transpose::None, unified::support::checkedInt(m, "edge GEMM M"),
               unified::support::checkedInt(n, "edge GEMM N"), unified::support::checkedInt(k, "edge GEMM K"), alpha, a,
               unified::support::checkedInt(lda, "edge GEMM LDA"), b, unified::support::checkedInt(ldb, "edge GEMM LDB"), beta, c,
               unified::support::checkedInt(ldc, "edge GEMM LDC"));
  auto expected = cHost;
  std::vector<bool> active(cHost.size(), false);
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row) {
      active[row + column * ldc] = true;
      long double sum = 0.0L;
      for (size_t inner = 0; inner < k; ++inner)
        sum += aHost[row + inner * lda] * bHost[inner + column * ldb];
      expected[row + column * ldc] = alpha * sum + beta * cHost[row + column * ldc];
    }
  {
    REQUIRE_THAT((backend.download(c, cHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend> void betaZeroGemm(Backend &backend, size_t size) {
  if (size == 0) {
    SUCCEED("beta_zero_gemm is not applicable to an empty matrix");
    return;
  }
  if (!backend.supports("gemm")) SKIP("beta_zero_gemm is unsupported for this type/device");

  const auto m = size;
  const auto n = unified::support::checkedAdd(size / 2, 1, "edge GEMM N");
  const auto k = unified::support::checkedAdd(size / 3, 1, "edge GEMM K");
  const auto lda = unified::support::checkedAdd(m, 3, "edge GEMM LDA");
  const auto ldb = unified::support::checkedAdd(k, 5, "edge GEMM LDB");
  const auto ldc = unified::support::checkedAdd(m, 7, "edge GEMM LDC");
  const long double padding = 77.0L;
  std::vector<long double> aHost(unified::support::checkedMultiply(lda, k, "edge GEMM A"), padding);
  std::vector<long double> bHost(unified::support::checkedMultiply(ldb, n, "edge GEMM B"), padding);
  std::vector<long double> cHost(unified::support::checkedMultiply(ldc, n, "edge GEMM C"), padding);
  for (size_t inner = 0; inner < k; ++inner)
    for (size_t row = 0; row < m; ++row)
      aHost[row + inner * lda] = static_cast<long double>(static_cast<int64_t>((row + inner * 3) % 11) - 5) / 8.0L;
  for (size_t column = 0; column < n; ++column)
    for (size_t inner = 0; inner < k; ++inner)
      bHost[inner + column * ldb] = static_cast<long double>(static_cast<int64_t>((inner * 5 + column) % 13) - 6) / 8.0L;
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row)
      cHost[row + column * ldc] = static_cast<long double>(static_cast<int64_t>((row * 7 + column) % 17) - 8) / 8.0L;

  const auto nan = std::numeric_limits<long double>::quiet_NaN();
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row)
      cHost[row + column * ldc] = nan;

  auto a = backend.allocate(aHost.size());
  auto b = backend.allocate(bHost.size());
  auto c = backend.allocate(cHost.size());
  backend.upload(a, aHost);
  backend.upload(b, bHost);
  backend.upload(c, cHost);
  const auto alpha = 0.75L;
  const auto beta = 0.0L;
  backend.gemm(blas::Transpose::None, blas::Transpose::None, unified::support::checkedInt(m, "edge GEMM M"),
               unified::support::checkedInt(n, "edge GEMM N"), unified::support::checkedInt(k, "edge GEMM K"), alpha, a,
               unified::support::checkedInt(lda, "edge GEMM LDA"), b, unified::support::checkedInt(ldb, "edge GEMM LDB"), beta, c,
               unified::support::checkedInt(ldc, "edge GEMM LDC"));
  auto expected = cHost;
  std::vector<bool> active(cHost.size(), false);
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row) {
      active[row + column * ldc] = true;
      long double sum = 0.0L;
      for (size_t inner = 0; inner < k; ++inner)
        sum += aHost[row + inner * lda] * bHost[inner + column * ldb];
      expected[row + column * ldc] = alpha * sum;
    }
  {
    REQUIRE_THAT((backend.download(c, cHost.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::Tolerance, expected, active));
  }
}
} // namespace unified::edge
