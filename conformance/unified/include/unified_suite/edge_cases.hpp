#ifndef UNIFIED_SUITE_EDGE_CASES_HPP
#define UNIFIED_SUITE_EDGE_CASES_HPP

#include "unified_suite/algorithms.hpp"
#include "unified_suite/blas.hpp"
#include "unified_suite/config.hpp"
#include "unified_suite/result.hpp"

#include <algorithm>
#include <bit>
#include <cmath>
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

#if defined(__CUDACC__) || defined(__HIPCC__)
#define UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE __host__ __device__
#else
#define UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE
#endif

namespace unified_suite::edge_cases {

#define UNIFIED_SUITE_EDGE_CASE_OPERATIONS(X)                                                                \
  X(composed_iterator_reduce, "composed-iterator-reduce")                                                    \
  X(biased_index_64, "biased-index-64")                                                                      \
  X(segments_singleton, "segments-singleton")                                                                \
  X(segments_giant, "segments-giant")                                                                        \
  X(segments_boundary, "segments-boundary")                                                                  \
  X(inplace_scan_canary, "inplace-scan-canary")                                                              \
  X(stable_sort_payload, "stable-sort-payload")                                                              \
  X(dependency_diamond, "dependency-diamond")                                                                \
  X(numeric_edge_roundtrip, "numeric-edge-roundtrip")                                                        \
  X(primitive_block_scan, "primitive-block-scan")                                                            \
  X(runtime_error_recover, "runtime-error-recover")

enum class Operation {
#define UNIFIED_SUITE_EDGE_CASE_ENUM(identifier, spelling) identifier,
  UNIFIED_SUITE_EDGE_CASE_OPERATIONS(UNIFIED_SUITE_EDGE_CASE_ENUM)
#undef UNIFIED_SUITE_EDGE_CASE_ENUM
};

struct OperationSpec {
  Operation operation;
  std::string_view name;
};

inline constexpr OperationSpec operation_specs[] = {
#define UNIFIED_SUITE_EDGE_CASE_SPEC(identifier, spelling) {Operation::identifier, spelling},
    UNIFIED_SUITE_EDGE_CASE_OPERATIONS(UNIFIED_SUITE_EDGE_CASE_SPEC)
#undef UNIFIED_SUITE_EDGE_CASE_SPEC
};

inline constexpr Operation operations[] = {
#define UNIFIED_SUITE_EDGE_CASE_VALUE(identifier, spelling) Operation::identifier,
    UNIFIED_SUITE_EDGE_CASE_OPERATIONS(UNIFIED_SUITE_EDGE_CASE_VALUE)
#undef UNIFIED_SUITE_EDGE_CASE_VALUE
};

inline constexpr std::string_view name(Operation operation) {
  for (const auto &spec : operation_specs)
    if (spec.operation == operation)
      return spec.name;
  return "unknown";
}

#undef UNIFIED_SUITE_EDGE_CASE_OPERATIONS

enum class BlasOperation { negative_stride_axpy, padded_gemm, beta_zero_gemm };

inline constexpr BlasOperation blas_operations[] = {
    BlasOperation::negative_stride_axpy, BlasOperation::padded_gemm, BlasOperation::beta_zero_gemm};

inline constexpr std::string_view name(BlasOperation operation) {
  switch (operation) {
  case BlasOperation::negative_stride_axpy:
    return "negative-stride-axpy";
  case BlasOperation::padded_gemm:
    return "padded-gemm";
  case BlasOperation::beta_zero_gemm:
    return "beta-zero-gemm";
  }
  return "unknown";
}

template <class T> struct ValueAt {
  UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE T operator()(std::uint64_t index) const {
    return static_cast<T>(static_cast<std::int64_t>((index * 7 + 5) % 17) - 8);
  }
};

struct ReverseIndex {
  std::size_t size;
  UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE std::size_t operator()(std::size_t index) const {
    return size - index - 1;
  }
};

template <class T> struct WidePairValue {
  UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE std::int64_t operator()(T left, T right) const {
    const auto a = static_cast<std::int64_t>(left);
    const auto b = static_cast<std::int64_t>(right);
    return a * 3 + b * 5 + a * b * 7;
  }
};

template <class T> struct BiasedHash {
  UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE std::uint64_t operator()(std::uint64_t index) const {
    auto value = index ^ (UINT64_C(0x9e3779b97f4a7c15) + sizeof(T));
    value ^= value >> 30;
    value *= UINT64_C(0xbf58476d1ce4e5b9);
    value ^= value >> 27;
    value *= UINT64_C(0x94d049bb133111eb);
    return value ^ (value >> 31);
  }
};

struct Xor64 {
  UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE std::uint64_t operator()(std::uint64_t left,
                                                                std::uint64_t right) const {
    return left ^ right;
  }
};

template <class T> struct Identity {
  UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE T operator()(T value) const { return value; }
};

namespace detail {

inline constexpr std::uint64_t biased_base = UINT64_C(0x100000005);
inline constexpr std::size_t guard_size = 4;
inline constexpr std::size_t primitive_block_size = 32;

inline void require(bool condition, const std::string &message) {
  if (!condition)
    throw std::runtime_error(message);
}

template <class T> inline std::vector<T> values(std::size_t size) {
  std::vector<T> result(size);
  for (std::size_t index = 0; index < size; ++index)
    result[index] = ValueAt<T>{}(index);
  return result;
}

template <class T> inline std::vector<T> alternating_values(std::size_t size) {
  std::vector<T> result(size);
  for (std::size_t index = 0; index < size; ++index)
    result[index] = static_cast<T>(index % 2 == 0 ? 1 : -1);
  return result;
}

inline std::vector<std::int32_t> segment_keys(Operation operation, std::size_t size) {
  std::vector<std::int32_t> result(size);
  if (operation == Operation::segments_singleton) {
    std::iota(result.begin(), result.end(), 0);
  } else if (operation == Operation::segments_giant) {
    std::fill(result.begin(), result.end(), 0);
  } else {
    constexpr std::size_t runs[] = {1, 31, 32, 33, 7};
    std::size_t begin = 0;
    std::int32_t key = 0;
    std::size_t run = 0;
    while (begin < size) {
      const auto end = std::min(size, begin + runs[run % std::size(runs)]);
      std::fill(result.begin() + static_cast<std::ptrdiff_t>(begin),
                result.begin() + static_cast<std::ptrdiff_t>(end), key++);
      begin = end;
      ++run;
    }
  }
  return result;
}

template <class T>
inline std::vector<T> reference_segmented_scan(const std::vector<std::int32_t> &keys,
                                               const std::vector<T> &input) {
  auto output = input;
  for (std::size_t index = 1; index < input.size(); ++index)
    if (keys[index] == keys[index - 1])
      output[index] = algorithms::Plus<T>{}(output[index - 1], input[index]);
  return output;
}

template <class T> inline T canary() {
  if constexpr (std::is_floating_point_v<T>)
    return static_cast<T>(123.5);
  else
    return static_cast<T>(107);
}

template <class T> inline bool bit_equal(T left, T right) {
  if constexpr (sizeof(T) == sizeof(std::uint64_t))
    return std::bit_cast<std::uint64_t>(left) == std::bit_cast<std::uint64_t>(right);
  else if constexpr (sizeof(T) == sizeof(std::uint32_t))
    return std::bit_cast<std::uint32_t>(left) == std::bit_cast<std::uint32_t>(right);
  else if constexpr (sizeof(T) == sizeof(std::uint16_t))
    return std::bit_cast<std::uint16_t>(left) == std::bit_cast<std::uint16_t>(right);
  else
    return std::bit_cast<std::uint8_t>(left) == std::bit_cast<std::uint8_t>(right);
}

template <class T> inline std::vector<T> numeric_edges(std::size_t size) {
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
  for (std::size_t index = 0; index < size; ++index)
    result[index] = seeds[index % seeds.size()];
  return result;
}

template <class T>
inline void require_bits(const std::vector<T> &actual, const std::vector<T> &expected,
                         const std::string &where) {
  require(actual.size() == expected.size(), where + ": result size mismatch");
  for (std::size_t index = 0; index < expected.size(); ++index)
    require(bit_equal(actual[index], expected[index]), where + ": bit mismatch at " + std::to_string(index));
}

template <class T>
inline void require_values(const std::vector<T> &actual, const std::vector<T> &expected,
                           const std::string &where) {
  algorithms::detail::require_equal(actual, expected, where);
}

template <class T, class Backend> void execute(Backend &backend, Operation operation, std::size_t size) {
  if (!backend.template supports<T>(operation))
    throw Unsupported(std::string(name(operation)) + " is unsupported for this type/device");

  switch (operation) {
  case Operation::composed_iterator_reduce: {
    std::int64_t expected = 0;
    for (std::size_t index = 0; index < size; ++index)
      expected += WidePairValue<T>{}(ValueAt<T>{}(size - index - 1), ValueAt<T>{}(index));
    algorithms::detail::require_equal(backend.template composed_iterator_reduce<T>(size), expected,
                                      "composed-iterator-reduce");
    return;
  }
  case Operation::biased_index_64: {
    std::uint64_t expected = 0;
    for (std::size_t index = 0; index < size; ++index)
      expected ^= BiasedHash<T>{}(biased_base + index);
    algorithms::detail::require_equal(backend.template biased_index_checksum<T>(biased_base, size), expected,
                                      "biased-index-64");
    return;
  }
  case Operation::segments_singleton:
  case Operation::segments_giant:
  case Operation::segments_boundary: {
    const auto keys = segment_keys(operation, size);
    const auto input = alternating_values<T>(size);
    require_values(backend.segmented_inclusive_scan(keys, input), reference_segmented_scan(keys, input),
                   std::string(name(operation)));
    return;
  }
  case Operation::inplace_scan_canary: {
    std::vector<T> storage(size + guard_size * 2, canary<T>());
    const auto input = alternating_values<T>(size);
    std::copy(input.begin(), input.end(), storage.begin() + static_cast<std::ptrdiff_t>(guard_size));
    auto expected = storage;
    for (std::size_t index = 1; index < size; ++index)
      expected[guard_size + index] = algorithms::Plus<T>{}(expected[guard_size + index - 1], input[index]);
    require_values(backend.inplace_inclusive_scan(storage, guard_size, size), expected,
                   "inplace-scan-canary");
    return;
  }
  case Operation::stable_sort_payload: {
    std::vector<T> keys(size);
    std::vector<std::int32_t> payload(size);
    for (std::size_t index = 0; index < size; ++index) {
      keys[index] = static_cast<T>((index * 7 + 3) % 5);
      payload[index] = static_cast<std::int32_t>(index);
    }
    const auto actual = backend.stable_sort_payload(keys, payload);
    require(std::is_sorted(actual.keys.begin(), actual.keys.end()), "stable-sort-payload: unsorted keys");
    for (std::size_t index = 0; index < actual.keys.size(); ++index) {
      const auto source = static_cast<std::size_t>(actual.values[index]);
      require(source < size, "stable-sort-payload: payload out of range");
      if (!(actual.keys[index] == keys[source])) {
        std::ostringstream message;
        message << "stable-sort-payload: association lost at index " << index << ": payload "
                << actual.values[index] << " expected key " << +keys[source] << ", got " << +actual.keys[index];
        require(false, message.str());
      }
      if (index != 0 && actual.keys[index] == actual.keys[index - 1])
        require(actual.values[index - 1] < actual.values[index], "stable-sort-payload: unstable order");
    }
    return;
  }
  case Operation::dependency_diamond: {
    const auto input = values<T>(size);
    auto expected = input;
    for (std::size_t index = 0; index < size; ++index)
      expected[index] = algorithms::Combine<T>{}(algorithms::Affine<T>{}(input[index]),
                                                 algorithms::Square<T>{}(input[index]));
    require_values(backend.dependency_diamond(input), expected, "dependency-diamond");
    return;
  }
  case Operation::numeric_edge_roundtrip: {
    const auto input = numeric_edges<T>(size);
    require_bits(backend.numeric_edge_roundtrip(input), input, "numeric-edge-roundtrip");
    return;
  }
  case Operation::primitive_block_scan: {
    const auto input = alternating_values<T>(size);
    auto expected = input;
    for (std::size_t index = 0; index < size; ++index)
      if (index % primitive_block_size != 0)
        expected[index] = algorithms::Plus<T>{}(expected[index - 1], input[index]);
    require_values(backend.primitive_block_scan(input, primitive_block_size), expected,
                   "primitive-block-scan");
    return;
  }
  case Operation::runtime_error_recover: {
    const auto input = values<T>(size);
    require_values(backend.runtime_error_recover(input), input, "runtime-error-recover");
    return;
  }
  }
}

template <class Backend, ScalarType Kind> Case algorithm_case(Operation operation, std::size_t size) {
  using T = typename algorithms::Native<Kind>::type;
  return {"edge-cases/algorithms/" + std::string(name(operation)) + "/" +
              std::string(unified_suite::name(Kind)) + "/n" + std::to_string(size),
          [operation, size] {
            return capture([&] {
              Backend backend;
              execute<T>(backend, operation, size);
            });
          }};
}

template <class Backend>
void append_algorithm_type(std::vector<Case> &result, ScalarType type, const Configuration &configuration) {
  for (const auto operation : operations)
    for (const auto size : configuration.edge_case_sizes)
      switch (type) {
      case ScalarType::i8:
        result.push_back(algorithm_case<Backend, ScalarType::i8>(operation, size));
        break;
      case ScalarType::i32:
        result.push_back(algorithm_case<Backend, ScalarType::i32>(operation, size));
        break;
      case ScalarType::i64:
        result.push_back(algorithm_case<Backend, ScalarType::i64>(operation, size));
        break;
      case ScalarType::f32:
        result.push_back(algorithm_case<Backend, ScalarType::f32>(operation, size));
        break;
      case ScalarType::f64:
        result.push_back(algorithm_case<Backend, ScalarType::f64>(operation, size));
        break;
      case ScalarType::f16:
        result.push_back(
            {"edge-cases/algorithms/" + std::string(name(operation)) + "/f16/n" + std::to_string(size),
             [] { return Result::skip("f16 edge cases are not instantiated"); }});
        break;
      }
}

template <ScalarType Kind, class Backend>
void execute_blas(Backend &backend, BlasOperation operation, std::size_t size) {
  if (operation == BlasOperation::negative_stride_axpy) {
    if (!backend.supports(blas::Operation::axpy))
      throw Unsupported("negative-stride-axpy is unsupported for this type/device");
    constexpr int incx = -2;
    constexpr int incy = -3;
    const auto x_host = blas::detail::values(blas::detail::strided_size(size, -incx),
                                             unified_suite::detail::checked_add(size, 19, "salt"));
    const auto y_host = blas::detail::values(blas::detail::strided_size(size, -incy),
                                             unified_suite::detail::checked_add(size, 23, "salt"));
    auto x = backend.allocate(x_host.size());
    auto y = backend.allocate(y_host.size());
    backend.upload(x, x_host);
    backend.upload(y, y_host);
    backend.axpy(unified_suite::detail::checked_int(size, "negative-stride AXPY size"), 0.75L, x, incx, y,
                 incy);
    auto expected = y_host;
    std::vector<bool> active(y_host.size(), false);
    for (std::size_t index = 0; index < size; ++index) {
      const auto x_index = (size - index - 1) * static_cast<std::size_t>(-incx);
      const auto y_index = (size - index - 1) * static_cast<std::size_t>(-incy);
      expected[y_index] += 0.75L * x_host[x_index];
      active[y_index] = true;
    }
    blas::detail::require_close_active<Kind>(backend.download(y, y_host.size()), expected, active,
                                             "negative-stride-axpy");
    return;
  }

  if (size == 0)
    throw Unsupported(std::string(name(operation)) + " requires a non-empty matrix");
  if (!backend.supports(blas::Operation::gemm))
    throw Unsupported(std::string(name(operation)) + " is unsupported for this type/device");

  const auto m = size;
  const auto n = unified_suite::detail::checked_add(size / 2, 1, "edge GEMM N");
  const auto k = unified_suite::detail::checked_add(size / 3, 1, "edge GEMM K");
  const auto lda = unified_suite::detail::checked_add(m, 3, "edge GEMM LDA");
  const auto ldb = unified_suite::detail::checked_add(k, 5, "edge GEMM LDB");
  const auto ldc = unified_suite::detail::checked_add(m, 7, "edge GEMM LDC");
  const long double padding = 77.0L;
  std::vector<long double> a_host(unified_suite::detail::checked_multiply(lda, k, "edge GEMM A"), padding);
  std::vector<long double> b_host(unified_suite::detail::checked_multiply(ldb, n, "edge GEMM B"), padding);
  std::vector<long double> c_host(unified_suite::detail::checked_multiply(ldc, n, "edge GEMM C"), padding);
  for (std::size_t inner = 0; inner < k; ++inner)
    for (std::size_t row = 0; row < m; ++row)
      a_host[row + inner * lda] =
          static_cast<long double>(static_cast<std::int64_t>((row + inner * 3) % 11) - 5) / 8.0L;
  for (std::size_t column = 0; column < n; ++column)
    for (std::size_t inner = 0; inner < k; ++inner)
      b_host[inner + column * ldb] =
          static_cast<long double>(static_cast<std::int64_t>((inner * 5 + column) % 13) - 6) / 8.0L;
  for (std::size_t column = 0; column < n; ++column)
    for (std::size_t row = 0; row < m; ++row)
      c_host[row + column * ldc] =
          static_cast<long double>(static_cast<std::int64_t>((row * 7 + column) % 17) - 8) / 8.0L;

  if (operation == BlasOperation::beta_zero_gemm) {
    const auto nan = std::numeric_limits<long double>::quiet_NaN();
    for (std::size_t column = 0; column < n; ++column)
      for (std::size_t row = 0; row < m; ++row)
        c_host[row + column * ldc] = nan;
  }

  auto a = backend.allocate(a_host.size());
  auto b = backend.allocate(b_host.size());
  auto c = backend.allocate(c_host.size());
  backend.upload(a, a_host);
  backend.upload(b, b_host);
  backend.upload(c, c_host);
  const auto alpha = 0.75L;
  const auto beta = operation == BlasOperation::beta_zero_gemm ? 0.0L : -0.25L;
  backend.gemm(blas::Transpose::none, blas::Transpose::none,
               unified_suite::detail::checked_int(m, "edge GEMM M"),
               unified_suite::detail::checked_int(n, "edge GEMM N"),
               unified_suite::detail::checked_int(k, "edge GEMM K"), alpha, a,
               unified_suite::detail::checked_int(lda, "edge GEMM LDA"), b,
               unified_suite::detail::checked_int(ldb, "edge GEMM LDB"), beta, c,
               unified_suite::detail::checked_int(ldc, "edge GEMM LDC"));
  auto expected = c_host;
  std::vector<bool> active(c_host.size(), false);
  if (operation == BlasOperation::padded_gemm || operation == BlasOperation::beta_zero_gemm)
    for (std::size_t column = 0; column < n; ++column)
      for (std::size_t row = 0; row < m; ++row) {
        active[row + column * ldc] = true;
        long double sum = 0.0L;
        for (std::size_t inner = 0; inner < k; ++inner)
          sum += a_host[row + inner * lda] * b_host[inner + column * ldb];
        expected[row + column * ldc] = operation == BlasOperation::beta_zero_gemm
                                           ? alpha * sum
                                           : alpha * sum + beta * c_host[row + column * ldc];
      }
  blas::detail::require_close_active<Kind>(backend.download(c, c_host.size()), expected, active,
                                           std::string(name(operation)));
}

template <template <ScalarType> class Backend, ScalarType Kind>
Case blas_case(BlasOperation operation, std::size_t size) {
  return {"edge-cases/blas/" + std::string(name(operation)) + "/" + std::string(unified_suite::name(Kind)) +
              "/n" + std::to_string(size),
          [operation, size] {
            return capture([&] {
              Backend<Kind> backend;
              execute_blas<Kind>(backend, operation, size);
            });
          }};
}

template <template <ScalarType> class Backend>
void append_blas_type(std::vector<Case> &result, ScalarType type, const Configuration &configuration) {
  for (const auto operation : blas_operations)
    for (const auto size : configuration.edge_case_sizes)
      switch (type) {
      case ScalarType::i8:
        result.push_back(blas_case<Backend, ScalarType::i8>(operation, size));
        break;
      case ScalarType::f16:
        result.push_back(blas_case<Backend, ScalarType::f16>(operation, size));
        break;
      case ScalarType::f32:
        result.push_back(blas_case<Backend, ScalarType::f32>(operation, size));
        break;
      case ScalarType::f64:
        result.push_back(blas_case<Backend, ScalarType::f64>(operation, size));
        break;
      case ScalarType::i32:
      case ScalarType::i64:
        result.push_back(
            {"edge-cases/blas/" + std::string(name(operation)) + "/" +
                 std::string(unified_suite::name(type)) + "/n" + std::to_string(size),
             [] { return Result::skip("this BLAS edge-case scalar type is not instantiated"); }});
        break;
      }
}

} // namespace detail

template <class AlgorithmBackend, template <ScalarType> class BlasBackend>
std::vector<Case> cases(const Configuration &configuration) {
  std::vector<Case> result;
  for (const auto type : configuration.edge_algorithm_types)
    detail::append_algorithm_type<AlgorithmBackend>(result, type, configuration);
  for (const auto type : configuration.edge_blas_types)
    detail::append_blas_type<BlasBackend>(result, type, configuration);
  return result;
}

} // namespace unified_suite::edge_cases

#undef UNIFIED_SUITE_EDGE_CASES_HOST_DEVICE

#endif
