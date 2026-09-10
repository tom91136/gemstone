#ifndef UNIFIED_SUITE_CONFIG_HPP
#define UNIFIED_SUITE_CONFIG_HPP

#include <algorithm>
#include <charconv>
#include <climits>
#include <cstddef>
#include <cstdint>
#include <limits>
#include <optional>
#include <sstream>
#include <stdexcept>
#include <string>
#include <string_view>
#include <vector>

namespace unified_suite {

enum class ScalarType { i8, i32, i64, f16, f32, f64 };

inline constexpr std::string_view name(ScalarType type) {
  switch (type) {
  case ScalarType::i8:
    return "i8";
  case ScalarType::i32:
    return "i32";
  case ScalarType::i64:
    return "i64";
  case ScalarType::f16:
    return "f16";
  case ScalarType::f32:
    return "f32";
  case ScalarType::f64:
    return "f64";
  }
  return "unknown";
}

inline std::optional<ScalarType> parse_scalar_type(std::string_view value) {
  if (value == "i8" || value == "int8")
    return ScalarType::i8;
  if (value == "i32" || value == "int32")
    return ScalarType::i32;
  if (value == "i64" || value == "int64")
    return ScalarType::i64;
  if (value == "f16" || value == "half")
    return ScalarType::f16;
  if (value == "f32" || value == "float")
    return ScalarType::f32;
  if (value == "f64" || value == "double")
    return ScalarType::f64;
  return std::nullopt;
}

struct MatrixShape {
  std::size_t m;
  std::size_t n;
  std::size_t k;

  friend bool operator==(const MatrixShape &, const MatrixShape &) = default;
};

inline std::string name(const MatrixShape &shape) {
  return std::to_string(shape.m) + "x" + std::to_string(shape.n) + "x" + std::to_string(shape.k);
}

struct Configuration {
  // Additional self-contained probes retained from fixed emulator defects.
  bool include_regressions = false;
  std::vector<std::size_t> algorithm_sizes{0, 1, 31, 32, 33, 63, 64, 65, 257};
  std::vector<std::size_t> blas_sizes{1, 31, 64, 257};
  std::vector<std::size_t> edge_case_sizes{0, 1, 7, 31, 32, 33, 257};
  std::vector<std::size_t> stress_sizes{257, 4096};
  std::vector<MatrixShape> matrix_shapes{{1, 1, 1}, {2, 2, 3}, {7, 5, 9}};
  std::vector<ScalarType> algorithm_types{ScalarType::i32, ScalarType::f32};
  std::vector<ScalarType> blas_types{ScalarType::f32};
  std::vector<ScalarType> edge_algorithm_types{ScalarType::i32, ScalarType::f32};
  std::vector<ScalarType> edge_blas_types{ScalarType::f32};
  std::vector<ScalarType> stress_types{ScalarType::i32, ScalarType::f32};
  std::size_t stress_iterations = 8;
};

// Profile data lives with the runner so direct execution and CTest use the same matrix.
inline Configuration configuration_for_profile(std::string_view profile) {
  Configuration result;
  if (profile == "compact")
    return result;
  if (profile != "full")
    throw std::invalid_argument("unknown workload profile: " + std::string(profile));
  result.algorithm_sizes = {0, 1, 7, 15, 31, 32, 33, 63, 64, 65, 127, 128, 129, 257, 1023, 1024, 4096};
  result.blas_sizes = {1, 2, 7, 31, 32, 64, 257, 1024};
  result.edge_case_sizes = {0, 1, 2, 7, 15, 31, 32, 33, 63, 64, 257, 1023, 1024};
  result.stress_sizes = {257, 4096, 16384};
  result.matrix_shapes = {{1, 1, 1}, {2, 2, 3}, {3, 5, 7}, {7, 5, 9}, {17, 13, 19}};
  result.algorithm_types = {ScalarType::i8,  ScalarType::i32, ScalarType::i64,
                            ScalarType::f32, ScalarType::f64, ScalarType::f16};
  result.blas_types = {ScalarType::i8, ScalarType::f32, ScalarType::f64, ScalarType::f16};
  result.edge_algorithm_types = result.algorithm_types;
  result.edge_blas_types = result.blas_types;
  result.stress_types = {ScalarType::i8, ScalarType::i32, ScalarType::i64, ScalarType::f32, ScalarType::f64};
  return result;
}

namespace detail {

inline std::vector<std::string> split(std::string_view value, char delimiter) {
  std::vector<std::string> result;
  std::size_t begin = 0;
  while (begin <= value.size()) {
    const auto end = value.find(delimiter, begin);
    const auto token =
        value.substr(begin, end == std::string_view::npos ? value.size() - begin : end - begin);
    if (token.empty())
      throw std::invalid_argument("empty value in list: " + std::string(value));
    result.emplace_back(token);
    if (end == std::string_view::npos)
      break;
    begin = end + 1;
  }
  return result;
}

inline std::size_t parse_size(std::string_view value) {
  if (value.empty() || !std::all_of(value.begin(), value.end(),
                                    [](char character) { return character >= '0' && character <= '9'; }))
    throw std::invalid_argument("invalid size: " + std::string(value));
  std::size_t result = 0;
  const auto [end, error] = std::from_chars(value.data(), value.data() + value.size(), result);
  if (error != std::errc{} || end != value.data() + value.size())
    throw std::invalid_argument("invalid size: " + std::string(value));
  return result;
}

inline std::size_t checked_add(std::size_t left, std::size_t right, std::string_view where) {
  if (right > std::numeric_limits<std::size_t>::max() - left)
    throw std::invalid_argument(std::string(where) + " overflows size_t");
  return left + right;
}

inline std::size_t checked_multiply(std::size_t left, std::size_t right, std::string_view where) {
  if (left != 0 && right > std::numeric_limits<std::size_t>::max() / left)
    throw std::invalid_argument(std::string(where) + " overflows size_t");
  return left * right;
}

inline int checked_int(std::size_t value, std::string_view where) {
  if (value > static_cast<std::size_t>(INT_MAX))
    throw std::invalid_argument(std::string(where) + " exceeds the vendor BLAS integer ABI");
  return static_cast<int>(value);
}

template <class T> inline void stable_unique(std::vector<T> &values) {
  std::vector<T> result;
  result.reserve(values.size());
  for (const auto &value : values)
    if (std::find(result.begin(), result.end(), value) == result.end())
      result.push_back(value);
  values = std::move(result);
}

} // namespace detail

inline std::vector<std::size_t> parse_sizes(std::string_view value) {
  std::vector<std::size_t> result;
  for (const auto &token : detail::split(value, ','))
    result.push_back(detail::parse_size(token));
  detail::stable_unique(result);
  return result;
}

inline std::vector<ScalarType> parse_scalar_types(std::string_view value) {
  std::vector<ScalarType> result;
  for (const auto &token : detail::split(value, ',')) {
    const auto parsed = parse_scalar_type(token);
    if (!parsed)
      throw std::invalid_argument("unknown scalar type: " + token);
    result.push_back(*parsed);
  }
  detail::stable_unique(result);
  return result;
}

inline MatrixShape parse_matrix_shape(std::string_view value) {
  const auto parts = detail::split(value, 'x');
  if (parts.size() != 3)
    throw std::invalid_argument("matrix shape must be MxNxK: " + std::string(value));
  const MatrixShape result{detail::parse_size(parts[0]), detail::parse_size(parts[1]),
                           detail::parse_size(parts[2])};
  if (result.m == 0 || result.n == 0 || result.k == 0)
    throw std::invalid_argument("matrix dimensions must be non-zero: " + std::string(value));
  (void)detail::checked_int(result.m, "matrix M");
  (void)detail::checked_int(result.n, "matrix N");
  (void)detail::checked_int(result.k, "matrix K");
  (void)detail::checked_multiply(result.m, result.n, "matrix M*N");
  (void)detail::checked_multiply(result.m, result.k, "matrix M*K");
  (void)detail::checked_multiply(result.k, result.n, "matrix K*N");
  return result;
}

inline std::vector<MatrixShape> parse_matrix_shapes(std::string_view value) {
  std::vector<MatrixShape> result;
  for (const auto &token : detail::split(value, ','))
    result.push_back(parse_matrix_shape(token));
  detail::stable_unique(result);
  return result;
}

inline void validate_configuration(const Configuration &configuration) {
  const auto require_iterator_size = [](const std::vector<std::size_t> &sizes, std::string_view suite) {
    for (const auto size : sizes)
      if (size > static_cast<std::size_t>(PTRDIFF_MAX))
        throw std::invalid_argument(std::string(suite) + " size exceeds PTRDIFF_MAX");
  };
  require_iterator_size(configuration.algorithm_sizes, "algorithm");
  require_iterator_size(configuration.stress_sizes, "stress");
  for (const auto size : configuration.blas_sizes)
    (void)detail::checked_int(size, "BLAS size");
  for (const auto size : configuration.edge_case_sizes) {
    (void)detail::checked_int(size, "edge-case size");
    (void)detail::checked_int(detail::checked_add(size, 7, "edge-case leading dimension"),
                              "edge-case leading dimension");
  }
  for (const auto shape : configuration.matrix_shapes) {
    (void)detail::checked_int(shape.m, "matrix M");
    (void)detail::checked_int(shape.n, "matrix N");
    (void)detail::checked_int(shape.k, "matrix K");
    (void)detail::checked_multiply(shape.m, shape.n, "matrix M*N");
    (void)detail::checked_multiply(shape.m, shape.k, "matrix M*K");
    (void)detail::checked_multiply(shape.k, shape.n, "matrix K*N");
  }
  if (configuration.stress_iterations == 0)
    throw std::invalid_argument("stress iterations must be positive");
}

} // namespace unified_suite

#endif
