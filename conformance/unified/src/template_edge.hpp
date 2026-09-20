#pragma once
#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <iterator>
#include <limits>
#include <numeric>
#include <sstream>
#include <string>
#include <type_traits>
#include <vector>

#include <catch2/catch_test_macros.hpp>

#include "support.hpp"
#include "template_blas.hpp"

namespace unified::edge {
using support::BiasedHash;
using support::Identity;
using support::ReverseIndex;
using support::ValueAt;
using support::WidePairValue;
using support::Xor64;

inline constexpr uint64_t biased_base = UINT64_C(0x100000005);
inline constexpr size_t guard_size = 4;
inline constexpr size_t primitive_block_size = 32;

template <class T> inline std::vector<T> values(size_t size) { return support::edge_values<T>(size); }

template <class T> inline std::vector<T> alternating_values(size_t size) {
  std::vector<T> result(size);
  for (size_t index = 0; index < size; ++index)
    result[index] = static_cast<T>(index % 2 == 0 ? 1 : -1);
  return result;
}

inline std::vector<int32_t> segments_singleton_keys(size_t size) {
  std::vector<int32_t> result(size);

  std::iota(result.begin(), result.end(), 0);

  return result;
}
inline std::vector<int32_t> segments_giant_keys(size_t size) {
  std::vector<int32_t> result(size);

  std::fill(result.begin(), result.end(), 0);

  return result;
}
inline std::vector<int32_t> segments_boundary_keys(size_t size) {
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

template <class T> inline std::vector<T> reference_segmented_scan(const std::vector<int32_t> &keys, const std::vector<T> &input) {
  auto output = input;
  for (size_t index = 1; index < input.size(); ++index)
    if (keys[index] == keys[index - 1]) output[index] = support::Plus<T>{}(output[index - 1], input[index]);
  return output;
}

template <class T> inline T canary() {
  if constexpr (std::is_floating_point_v<T>) return static_cast<T>(123.5);
  else return static_cast<T>(107);
}

template <class T> inline bool bit_equal(T left, T right) {
  static_assert(std::is_trivially_copyable_v<T>);
  return std::memcmp(&left, &right, sizeof(T)) == 0;
}

template <class T> inline std::vector<T> numeric_edges(size_t size) {
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

template <class T, class Backend> void composed_iterator_reduce(Backend &backend, size_t size) {
  if (!backend.template supports_composed_iterator_reduce<T>()) SKIP("composed_iterator_reduce is unsupported for this type/device");

  int64_t expected = 0;
  for (size_t index = 0; index < size; ++index)
    expected += WidePairValue<T>{}(ValueAt<T>{}(size - index - 1), ValueAt<T>{}(index));
  {
    INFO("composed-iterator-reduce");
    REQUIRE(((backend.template composed_iterator_reduce<T>(size)) == (expected)));
  }
}

template <class T, class Backend> void biased_index_64(Backend &backend, size_t size) {
  if (!backend.template supports_biased_index_64<T>()) SKIP("biased_index_64 is unsupported for this type/device");

  uint64_t expected = 0;
  for (size_t index = 0; index < size; ++index)
    expected ^= BiasedHash<T>{}(biased_base + index);
  {
    INFO("biased-index-64");
    REQUIRE(((backend.template biased_index_checksum<T>(biased_base, size)) == (expected)));
  }
}

template <class T, class Backend> void segments_singleton(Backend &backend, size_t size) {
  if (!backend.template supports_segments_singleton<T>()) SKIP("segments_singleton is unsupported for this type/device");

  const auto keys = segments_singleton_keys(size);
  const auto input = alternating_values<T>(size);
  {
    INFO("segments_singleton");
    REQUIRE(((backend.segmented_inclusive_scan(keys, input)) == (reference_segmented_scan(keys, input))));
  }
}

template <class T, class Backend> void segments_giant(Backend &backend, size_t size) {
  if (!backend.template supports_segments_giant<T>()) SKIP("segments_giant is unsupported for this type/device");

  const auto keys = segments_giant_keys(size);
  const auto input = alternating_values<T>(size);
  {
    INFO("segments_giant");
    REQUIRE(((backend.segmented_inclusive_scan(keys, input)) == (reference_segmented_scan(keys, input))));
  }
}

template <class T, class Backend> void segments_boundary(Backend &backend, size_t size) {
  if (!backend.template supports_segments_boundary<T>()) SKIP("segments_boundary is unsupported for this type/device");

  const auto keys = segments_boundary_keys(size);
  const auto input = alternating_values<T>(size);
  {
    INFO("segments_boundary");
    REQUIRE(((backend.segmented_inclusive_scan(keys, input)) == (reference_segmented_scan(keys, input))));
  }
}

template <class T, class Backend> void inplace_scan_canary(Backend &backend, size_t size) {
  if (!backend.template supports_inplace_scan_canary<T>()) SKIP("inplace_scan_canary is unsupported for this type/device");

  std::vector<T> storage(size + guard_size * 2, canary<T>());
  const auto input = alternating_values<T>(size);
  std::copy(input.begin(), input.end(), storage.begin() + static_cast<ptrdiff_t>(guard_size));
  auto expected = storage;
  for (size_t index = 1; index < size; ++index)
    expected[guard_size + index] = support::Plus<T>{}(expected[guard_size + index - 1], input[index]);
  {
    INFO("inplace-scan-canary");
    REQUIRE(((backend.inplace_inclusive_scan(storage, guard_size, size)) == (expected)));
  }
}

template <class T, class Backend> void stable_sort_payload(Backend &backend, size_t size) {
  if (!backend.template supports_stable_sort_payload<T>()) SKIP("stable_sort_payload is unsupported for this type/device");

  std::vector<T> keys(size);
  std::vector<int32_t> payload(size);
  for (size_t index = 0; index < size; ++index) {
    keys[index] = static_cast<T>((index * 7 + 3) % 5);
    payload[index] = static_cast<int32_t>(index);
  }
  const auto actual = backend.stable_sort_payload(keys, payload);
  {
    INFO("stable-sort-payload: unsorted keys");
    REQUIRE((std::is_sorted(actual.keys.begin(), actual.keys.end())));
  }
  for (size_t index = 0; index < actual.keys.size(); ++index) {
    const auto source = static_cast<size_t>(actual.values[index]);
    {
      INFO("stable-sort-payload: payload out of range");
      REQUIRE((source < size));
    }
    if (!(actual.keys[index] == keys[source])) {
      std::ostringstream message;
      message << "stable-sort-payload: association lost at index " << index << ": payload " << actual.values[index] << " expected key "
              << +keys[source] << ", got " << +actual.keys[index];
      {
        INFO(message.str());
        REQUIRE((false));
      }
    }
    if (index != 0 && actual.keys[index] == actual.keys[index - 1]) {
      INFO("stable-sort-payload: unstable order");
      REQUIRE((actual.values[index - 1] < actual.values[index]));
    }
  }
}

template <class T, class Backend> void dependency_diamond(Backend &backend, size_t size) {
  if (!backend.template supports_dependency_diamond<T>()) SKIP("dependency_diamond is unsupported for this type/device");

  const auto input = values<T>(size);
  auto expected = input;
  for (size_t index = 0; index < size; ++index)
    expected[index] = support::Combine<T>{}(support::Affine<T>{}(input[index]), support::Square<T>{}(input[index]));
  {
    INFO("dependency-diamond");
    REQUIRE(((backend.dependency_diamond(input)) == (expected)));
  }
}

template <class T, class Backend> void numeric_edge_roundtrip(Backend &backend, size_t size) {
  if (!backend.template supports_numeric_edge_roundtrip<T>()) SKIP("numeric_edge_roundtrip is unsupported for this type/device");

  const auto input = numeric_edges<T>(size);
  {
    INFO("numeric-edge-roundtrip");
    REQUIRE_THAT(backend.numeric_edge_roundtrip(input), unified::BitwiseEqual(input));
  }
}

template <class T, class Backend> void primitive_block_scan(Backend &backend, size_t size) {
  if (!backend.template supports_primitive_block_scan<T>()) SKIP("primitive_block_scan is unsupported for this type/device");

  const auto input = alternating_values<T>(size);
  auto expected = input;
  for (size_t index = 0; index < size; ++index)
    if (index % primitive_block_size != 0) expected[index] = support::Plus<T>{}(expected[index - 1], input[index]);
  {
    INFO("primitive-block-scan");
    REQUIRE(((backend.primitive_block_scan(input, primitive_block_size)) == (expected)));
  }
}

template <class T, class Backend> void runtime_error_recover(Backend &backend, size_t size) {
  if (!backend.template supports_runtime_error_recover<T>()) SKIP("runtime_error_recover is unsupported for this type/device");

  const auto input = values<T>(size);
  {
    INFO("runtime-error-recover");
    REQUIRE(((backend.runtime_error_recover(input)) == (input)));
  }
}

template <ScalarType Kind, class Backend> void negative_stride_axpy(Backend &backend, size_t size) {
  if (!backend.supports_axpy()) SKIP("negative-stride-axpy is unsupported for this type/device");
  constexpr int incx = -2;
  constexpr int incy = -3;
  const auto x_host = blas::values(blas::strided_size(size, -incx), unified::support::checked_add(size, 19, "salt"));
  const auto y_host = blas::values(blas::strided_size(size, -incy), unified::support::checked_add(size, 23, "salt"));
  auto x = backend.allocate(x_host.size());
  auto y = backend.allocate(y_host.size());
  backend.upload(x, x_host);
  backend.upload(y, y_host);
  backend.axpy(unified::support::checked_int(size, "negative-stride AXPY size"), 0.75L, x, incx, y, incy);
  auto expected = y_host;
  std::vector<bool> active(y_host.size(), false);
  for (size_t index = 0; index < size; ++index) {
    const auto x_index = (size - index - 1) * static_cast<size_t>(-incx);
    const auto y_index = (size - index - 1) * static_cast<size_t>(-incy);
    expected[y_index] += 0.75L * x_host[x_index];
    active[y_index] = true;
  }
  {
    INFO("negative-stride-axpy");
    REQUIRE_THAT((backend.download(y, y_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
  return;
}

template <ScalarType Kind, class Backend> void padded_gemm(Backend &backend, size_t size) {
  if (size == 0) {
    SUCCEED("padded_gemm is not applicable to an empty matrix");
    return;
  }
  if (!backend.supports_gemm()) SKIP("padded_gemm is unsupported for this type/device");

  const auto m = size;
  const auto n = unified::support::checked_add(size / 2, 1, "edge GEMM N");
  const auto k = unified::support::checked_add(size / 3, 1, "edge GEMM K");
  const auto lda = unified::support::checked_add(m, 3, "edge GEMM LDA");
  const auto ldb = unified::support::checked_add(k, 5, "edge GEMM LDB");
  const auto ldc = unified::support::checked_add(m, 7, "edge GEMM LDC");
  const long double padding = 77.0L;
  std::vector<long double> a_host(unified::support::checked_multiply(lda, k, "edge GEMM A"), padding);
  std::vector<long double> b_host(unified::support::checked_multiply(ldb, n, "edge GEMM B"), padding);
  std::vector<long double> c_host(unified::support::checked_multiply(ldc, n, "edge GEMM C"), padding);
  for (size_t inner = 0; inner < k; ++inner)
    for (size_t row = 0; row < m; ++row)
      a_host[row + inner * lda] = static_cast<long double>(static_cast<int64_t>((row + inner * 3) % 11) - 5) / 8.0L;
  for (size_t column = 0; column < n; ++column)
    for (size_t inner = 0; inner < k; ++inner)
      b_host[inner + column * ldb] = static_cast<long double>(static_cast<int64_t>((inner * 5 + column) % 13) - 6) / 8.0L;
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row)
      c_host[row + column * ldc] = static_cast<long double>(static_cast<int64_t>((row * 7 + column) % 17) - 8) / 8.0L;

  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  const auto alpha = 0.75L;
  const auto beta = -0.25L;
  backend.gemm(blas::Transpose::none, blas::Transpose::none, unified::support::checked_int(m, "edge GEMM M"),
               unified::support::checked_int(n, "edge GEMM N"), unified::support::checked_int(k, "edge GEMM K"), alpha, a,
               unified::support::checked_int(lda, "edge GEMM LDA"), b, unified::support::checked_int(ldb, "edge GEMM LDB"), beta, c,
               unified::support::checked_int(ldc, "edge GEMM LDC"));
  auto expected = c_host;
  std::vector<bool> active(c_host.size(), false);
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row) {
      active[row + column * ldc] = true;
      long double sum = 0.0L;
      for (size_t inner = 0; inner < k; ++inner)
        sum += a_host[row + inner * lda] * b_host[inner + column * ldb];
      expected[row + column * ldc] = alpha * sum + beta * c_host[row + column * ldc];
    }
  {
    INFO("padded_gemm");
    REQUIRE_THAT((backend.download(c, c_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
}

template <ScalarType Kind, class Backend> void beta_zero_gemm(Backend &backend, size_t size) {
  if (size == 0) {
    SUCCEED("beta_zero_gemm is not applicable to an empty matrix");
    return;
  }
  if (!backend.supports_gemm()) SKIP("beta_zero_gemm is unsupported for this type/device");

  const auto m = size;
  const auto n = unified::support::checked_add(size / 2, 1, "edge GEMM N");
  const auto k = unified::support::checked_add(size / 3, 1, "edge GEMM K");
  const auto lda = unified::support::checked_add(m, 3, "edge GEMM LDA");
  const auto ldb = unified::support::checked_add(k, 5, "edge GEMM LDB");
  const auto ldc = unified::support::checked_add(m, 7, "edge GEMM LDC");
  const long double padding = 77.0L;
  std::vector<long double> a_host(unified::support::checked_multiply(lda, k, "edge GEMM A"), padding);
  std::vector<long double> b_host(unified::support::checked_multiply(ldb, n, "edge GEMM B"), padding);
  std::vector<long double> c_host(unified::support::checked_multiply(ldc, n, "edge GEMM C"), padding);
  for (size_t inner = 0; inner < k; ++inner)
    for (size_t row = 0; row < m; ++row)
      a_host[row + inner * lda] = static_cast<long double>(static_cast<int64_t>((row + inner * 3) % 11) - 5) / 8.0L;
  for (size_t column = 0; column < n; ++column)
    for (size_t inner = 0; inner < k; ++inner)
      b_host[inner + column * ldb] = static_cast<long double>(static_cast<int64_t>((inner * 5 + column) % 13) - 6) / 8.0L;
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row)
      c_host[row + column * ldc] = static_cast<long double>(static_cast<int64_t>((row * 7 + column) % 17) - 8) / 8.0L;

  const auto nan = std::numeric_limits<long double>::quiet_NaN();
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row)
      c_host[row + column * ldc] = nan;

  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  const auto alpha = 0.75L;
  const auto beta = 0.0L;
  backend.gemm(blas::Transpose::none, blas::Transpose::none, unified::support::checked_int(m, "edge GEMM M"),
               unified::support::checked_int(n, "edge GEMM N"), unified::support::checked_int(k, "edge GEMM K"), alpha, a,
               unified::support::checked_int(lda, "edge GEMM LDA"), b, unified::support::checked_int(ldb, "edge GEMM LDB"), beta, c,
               unified::support::checked_int(ldc, "edge GEMM LDC"));
  auto expected = c_host;
  std::vector<bool> active(c_host.size(), false);
  for (size_t column = 0; column < n; ++column)
    for (size_t row = 0; row < m; ++row) {
      active[row + column * ldc] = true;
      long double sum = 0.0L;
      for (size_t inner = 0; inner < k; ++inner)
        sum += a_host[row + inner * lda] * b_host[inner + column * ldb];
      expected[row + column * ldc] = alpha * sum;
    }
  {
    INFO("beta_zero_gemm");
    REQUIRE_THAT((backend.download(c, c_host.size())),
                 unified::WithinTolerance(unified::blas::NumericTraits<Kind>::tolerance, expected, active));
  }
}
} // namespace unified::edge
