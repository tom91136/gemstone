#ifndef GEMSTONE_CONFORMANCE_BLAS_SMOKE_HPP
#define GEMSTONE_CONFORMANCE_BLAS_SMOKE_HPP

#include <algorithm>
#include <cmath>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <iostream>
#include <stdexcept>
#include <string>
#include <vector>

namespace gemstone::conformance::blas {

enum class NumericType { half, f32, f64, i8 };

inline bool parse_numeric_type(const std::string &value, NumericType &type) {
  if (value == "half" || value == "f16") type = NumericType::half;
  else if (value == "float" || value == "f32") type = NumericType::f32;
  else if (value == "double" || value == "f64") type = NumericType::f64;
  else if (value == "int8" || value == "i8") type = NumericType::i8;
  else return false;
  return true;
}

inline const char *numeric_type_name(NumericType type) {
  switch (type) {
  case NumericType::half: return "f16";
  case NumericType::f32:  return "f32";
  case NumericType::f64:  return "f64";
  case NumericType::i8:   return "i8";
  }
  return "unknown";
}

template <NumericType> struct NumericTraits;

template <> struct NumericTraits<NumericType::half> {
  static constexpr const char *name = "f16";
  static constexpr long double tolerance = 3.0e-2L;
};

template <> struct NumericTraits<NumericType::f32> {
  static constexpr const char *name = "f32";
  static constexpr long double tolerance = 3.0e-4L;
};

template <> struct NumericTraits<NumericType::f64> {
  static constexpr const char *name = "f64";
  static constexpr long double tolerance = 3.0e-12L;
};

template <> struct NumericTraits<NumericType::i8> {
  static constexpr const char *name = "i8";
  static constexpr long double tolerance = 0.0L;
};

enum class Coverage { all, level1, level2, level3 };

inline bool parse_coverage(const std::string &value, Coverage &coverage) {
  if (value == "all") coverage = Coverage::all;
  else if (value == "level1") coverage = Coverage::level1;
  else if (value == "level2") coverage = Coverage::level2;
  else if (value == "level3") coverage = Coverage::level3;
  else return false;
  return true;
}

inline const char *coverage_name(Coverage coverage) {
  switch (coverage) {
  case Coverage::all: return "all";
  case Coverage::level1: return "level1";
  case Coverage::level2: return "level2";
  case Coverage::level3: return "level3";
  }
  return "unknown";
}

enum class Operation { axpy, scal, copy, dot, nrm2, asum, gemv, gemm };

inline const char *operation_name(Operation operation) {
  switch (operation) {
  case Operation::axpy: return "axpy";
  case Operation::scal: return "scal";
  case Operation::copy: return "copy";
  case Operation::dot:  return "dot";
  case Operation::nrm2: return "nrm2";
  case Operation::asum: return "asum";
  case Operation::gemv: return "gemv";
  case Operation::gemm: return "gemm";
  }
  return "unknown";
}

inline bool coverage_contains(Coverage coverage, Operation operation) {
  if (coverage == Coverage::all) return true;
  if (coverage == Coverage::level1) {
    return operation == Operation::axpy || operation == Operation::scal ||
           operation == Operation::copy || operation == Operation::dot ||
           operation == Operation::nrm2 || operation == Operation::asum;
  }
  if (coverage == Coverage::level2) return operation == Operation::gemv;
  return operation == Operation::gemm;
}

namespace detail {

class UnsupportedOperation : public std::runtime_error {
public:
  explicit UnsupportedOperation(const std::string &message) : std::runtime_error(message) {}
};

/* IEEE binary16 conversion used by adapters whose public half type is a
 * storage-only struct. */
inline uint16_t float_to_half_bits(float value) {
  uint32_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  const uint32_t sign = (bits >> 16) & 0x8000u;
  const uint32_t exponent = (bits >> 23) & 0xffu;
  const uint32_t mantissa = bits & 0x7fffffu;
  if (exponent == 0xffu) {
    if (mantissa == 0) return static_cast<uint16_t>(sign | 0x7c00u);
    return static_cast<uint16_t>(sign | 0x7e00u);
  }
  const int half_exponent = static_cast<int>(exponent) - 127 + 15;
  if (half_exponent >= 31) return static_cast<uint16_t>(sign | 0x7c00u);
  if (half_exponent <= 0) {
    if (half_exponent < -10) return static_cast<uint16_t>(sign);
    const uint32_t hidden = mantissa | 0x800000u;
    const int shift = 14 - half_exponent;
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
  std::memcpy(&result, &bits, sizeof(result));
  return result;
}

inline void require(bool condition, const std::string &message) {
  if (!condition) throw std::runtime_error(message);
}

inline int matrix_size() {
  const char *raw = std::getenv("GEMSTONE_BLAS_MATRIX_SIZE");
  if (raw == nullptr || raw[0] == '\0') return 0;
  const std::string text(raw);
  std::size_t consumed = 0;
  int size = 0;
  try {
    size = std::stoi(text, &consumed);
  } catch (const std::exception &) {
    throw std::runtime_error("GEMSTONE_BLAS_MATRIX_SIZE must be an integer from 2 through 128");
  }
  require(consumed == text.size() && size >= 2 && size <= 128,
          "GEMSTONE_BLAS_MATRIX_SIZE must be an integer from 2 through 128");
  return size;
}

template <NumericType Kind>
inline void close(long double actual, long double expected, const std::string &where) {
  const long double tolerance = NumericTraits<Kind>::tolerance *
                                std::max(1.0L, std::fabs(expected));
  require(std::isfinite(actual), where + " produced a non-finite value");
  require(std::fabs(actual - expected) <= tolerance,
          where + " expected " + std::to_string(static_cast<double>(expected)) +
              " but got " + std::to_string(static_cast<double>(actual)));
}

template <NumericType Kind>
inline void close_vector(const std::vector<long double> &actual,
                         const std::vector<long double> &expected,
                         const std::string &where) {
  require(actual.size() == expected.size(), where + " changed the buffer size");
  for (std::size_t i = 0; i < expected.size(); ++i)
    close<Kind>(actual[i], expected[i], where + "[" + std::to_string(i) + "]");
}

inline std::vector<long double> values(std::size_t elements, int salt) {
  std::vector<long double> result(elements);
  for (std::size_t i = 0; i < elements; ++i) {
    const int pattern = static_cast<int>((i * 17 + salt * 5) % 23) - 11;
    result[i] = static_cast<long double>(pattern) / 8.0L;
  }
  return result;
}

inline std::size_t strided_elements(int n, int increment) {
  return n == 0 ? 0 : 1u + static_cast<std::size_t>(n - 1) * increment;
}

} // namespace detail

/*
 * The backend contract is value-oriented so native half and mixed-precision
 * types stay inside adapters:
 *
 *   using Buffer = ...;
 *   static constexpr bool supports(Operation);
 *   Buffer allocate(std::size_t);
 *   void upload(Buffer &, const std::vector<long double> &);
 *   std::vector<long double> download(const Buffer &, std::size_t);
 *   void axpy/scal/copy(..., long double alpha, ...);
 *   long double dot/nrm2/asum(...);
 *   void gemv/gemm(..., long double alpha/beta, ...);
 */
template <class Backend, NumericType Kind> class Smoke {
public:
  explicit Smoke(Backend &backend) : backend_(backend), matrix_size_(detail::matrix_size()) {}

  void run(std::ostream &output, Coverage coverage = Coverage::all) {
    std::vector<const char *> skipped;
    int executed = 0;
    run_if(coverage, Operation::axpy, skipped, executed, [&] { run_axpy(64); });
    run_if(coverage, Operation::scal, skipped, executed, [&] { run_scal(64); });
    run_if(coverage, Operation::copy, skipped, executed, [&] { run_copy(64); });
    run_if(coverage, Operation::dot, skipped, executed, [&] { run_dot(64); });
    run_if(coverage, Operation::nrm2, skipped, executed, [&] { run_nrm2(64); });
    run_if(coverage, Operation::asum, skipped, executed, [&] { run_asum(64); });
    run_if(coverage, Operation::gemv, skipped, executed, [&] {
      run_gemv(matrix_size_ == 0 ? 3 : matrix_size_, matrix_size_ == 0 ? 2 : matrix_size_);
    });
    run_if(coverage, Operation::gemm, skipped, executed, [&] {
      run_gemm(matrix_size_ == 0 ? 2 : matrix_size_, matrix_size_ == 0 ? 2 : matrix_size_,
               matrix_size_ == 0 ? 3 : matrix_size_);
    });

    output << "blas smoke: " << (executed == 0 ? "skip" : "pass")
           << " (" << NumericTraits<Kind>::name << ", coverage="
           << coverage_name(coverage) << ", matrix="
           << (matrix_size_ == 0 ? "tiny" : std::to_string(matrix_size_))
           << ", operations=" << executed;
    if (!skipped.empty()) {
      output << ", skipped=";
      for (std::size_t i = 0; i < skipped.size(); ++i) {
        if (i != 0) output << ',';
        output << skipped[i];
      }
    }
    output << ")\n";
  }

private:
  using Buffer = typename Backend::Buffer;
  Backend &backend_;
  int matrix_size_;

  template <class Function>
  void run_if(Coverage coverage, Operation operation,
              std::vector<const char *> &skipped, int &executed,
              Function &&function) {
    if (!coverage_contains(coverage, operation)) return;
    if (!backend_.supports(operation)) {
      skipped.push_back(operation_name(operation));
      return;
    }
    try {
      function();
      ++executed;
    } catch (const detail::UnsupportedOperation &) {
      // Device-specific BLAS support can be narrower than the header/API
      // surface (for example FP64 or FP16 on an older accelerator).
      skipped.push_back(operation_name(operation));
    }
  }

  void run_axpy(int n) {
    constexpr int incx = 2;
    constexpr int incy = 3;
    const auto x_host = detail::values(detail::strided_elements(n, incx), n + 1);
    const auto y_host = detail::values(detail::strided_elements(n, incy), n + 7);
    Buffer x = backend_.allocate(x_host.size());
    Buffer y = backend_.allocate(y_host.size());
    backend_.upload(x, x_host);
    backend_.upload(y, y_host);
    backend_.axpy(n, 0.75L, x, incx, y, incy);
    auto expected = y_host;
    for (int i = 0; i < n; ++i)
      expected[static_cast<std::size_t>(i) * incy] +=
          0.75L * x_host[static_cast<std::size_t>(i) * incx];
    detail::close_vector<Kind>(backend_.download(y, y_host.size()), expected,
                                "axpy/n=" + std::to_string(n));
  }

  void run_scal(int n) {
    constexpr int incx = 2;
    const auto x_host = detail::values(detail::strided_elements(n, incx), n + 1);
    Buffer x = backend_.allocate(x_host.size());
    backend_.upload(x, x_host);
    backend_.scal(n, -0.5L, x, incx);
    auto expected = x_host;
    for (int i = 0; i < n; ++i)
      expected[static_cast<std::size_t>(i) * incx] *= -0.5L;
    detail::close_vector<Kind>(backend_.download(x, x_host.size()), expected,
                                "scal/n=" + std::to_string(n));
  }

  void run_copy(int n) {
    constexpr int incx = 2;
    constexpr int incy = 3;
    const auto x_host = detail::values(detail::strided_elements(n, incx), n + 1);
    const auto y_host = detail::values(detail::strided_elements(n, incy), n + 7);
    Buffer x = backend_.allocate(x_host.size());
    Buffer y = backend_.allocate(y_host.size());
    backend_.upload(x, x_host);
    backend_.upload(y, y_host);
    backend_.copy(n, x, incx, y, incy);
    auto expected = y_host;
    for (int i = 0; i < n; ++i)
      expected[static_cast<std::size_t>(i) * incy] =
          x_host[static_cast<std::size_t>(i) * incx];
    detail::close_vector<Kind>(backend_.download(y, y_host.size()), expected,
                                "copy/n=" + std::to_string(n));
  }

  void run_dot(int n) {
    constexpr int incx = 2;
    constexpr int incy = 3;
    const auto x_host = detail::values(detail::strided_elements(n, incx), n + 1);
    const auto y_host = detail::values(detail::strided_elements(n, incy), n + 7);
    long double expected = 0.0L;
    for (int i = 0; i < n; ++i)
      expected += x_host[static_cast<std::size_t>(i) * incx] *
                  y_host[static_cast<std::size_t>(i) * incy];
    Buffer x = backend_.allocate(x_host.size());
    Buffer y = backend_.allocate(y_host.size());
    backend_.upload(x, x_host);
    backend_.upload(y, y_host);
    detail::close<Kind>(backend_.dot(n, x, incx, y, incy), expected,
                        "dot/n=" + std::to_string(n));
  }

  void run_nrm2(int n) {
    constexpr int incx = 2;
    const auto x_host = detail::values(detail::strided_elements(n, incx), n + 1);
    long double expected_squared = 0.0L;
    for (int i = 0; i < n; ++i) {
      const long double value = x_host[static_cast<std::size_t>(i) * incx];
      expected_squared += value * value;
    }
    Buffer x = backend_.allocate(x_host.size());
    backend_.upload(x, x_host);
    detail::close<Kind>(backend_.nrm2(n, x, incx), std::sqrt(expected_squared),
                        "nrm2/n=" + std::to_string(n));
  }

  void run_asum(int n) {
    constexpr int incx = 2;
    const auto x_host = detail::values(detail::strided_elements(n, incx), n + 1);
    long double expected = 0.0L;
    for (int i = 0; i < n; ++i)
      expected += std::fabs(x_host[static_cast<std::size_t>(i) * incx]);
    Buffer x = backend_.allocate(x_host.size());
    backend_.upload(x, x_host);
    detail::close<Kind>(backend_.asum(n, x, incx), expected,
                        "asum/n=" + std::to_string(n));
  }

  void run_gemv(int m, int n) {
    const int lda = m;
    const auto a_host = matrix_size_ == 0
                            ? std::vector<long double>{1.0L, 2.0L, 3.0L, 4.0L, 5.0L, 6.0L}
                            : detail::values(static_cast<std::size_t>(m) * n, 31);
    const auto x_host = matrix_size_ == 0 ? std::vector<long double>{0.5L, -1.25L}
                                          : detail::values(n, 37);
    const auto y_host = matrix_size_ == 0 ? std::vector<long double>{1.0L, -2.0L, 3.0L}
                                          : detail::values(m, 41);
    Buffer a = backend_.allocate(a_host.size());
    Buffer x = backend_.allocate(x_host.size());
    Buffer y = backend_.allocate(y_host.size());
    backend_.upload(a, a_host);
    backend_.upload(x, x_host);
    backend_.upload(y, y_host);
    backend_.gemv(m, n, 1.25L, a, lda, x, 1, -0.5L, y, 1);
    auto expected = y_host;
    for (int row = 0; row < m; ++row) {
      long double sum = 0.0L;
      for (int col = 0; col < n; ++col)
        sum += a_host[static_cast<std::size_t>(row + col * lda)] * x_host[col];
      expected[row] = 1.25L * sum - 0.5L * y_host[row];
    }
    detail::close_vector<Kind>(backend_.download(y, y_host.size()), expected,
                                "gemv/" + std::to_string(m) + "x" + std::to_string(n));
  }

  void run_gemm(int m, int n, int k) {
    const int lda = m;
    const int ldb = k;
    const int ldc = m;
    const auto a_host = matrix_size_ == 0
                            ? std::vector<long double>{1.0L, 2.0L, 3.0L, 4.0L, 5.0L, 6.0L}
                            : detail::values(static_cast<std::size_t>(m) * k, 43);
    const auto b_host = matrix_size_ == 0
                            ? std::vector<long double>{0.5L, -1.0L, 1.5L, 2.0L, -0.25L, 0.75L}
                            : detail::values(static_cast<std::size_t>(k) * n, 47);
    const auto c_host = matrix_size_ == 0
                            ? std::vector<long double>{1.0L, -2.0L, 3.0L, 0.5L}
                            : detail::values(static_cast<std::size_t>(m) * n, 53);
    Buffer a = backend_.allocate(a_host.size());
    Buffer b = backend_.allocate(b_host.size());
    Buffer c = backend_.allocate(c_host.size());
    backend_.upload(a, a_host);
    backend_.upload(b, b_host);
    backend_.upload(c, c_host);
    backend_.gemm(m, n, k, 0.75L, a, lda, b, ldb, -0.25L, c, ldc);
    auto expected = c_host;
    for (int col = 0; col < n; ++col) {
      for (int row = 0; row < m; ++row) {
        long double sum = 0.0L;
        for (int inner = 0; inner < k; ++inner)
          sum += a_host[static_cast<std::size_t>(row + inner * lda)] *
                 b_host[static_cast<std::size_t>(inner + col * ldb)];
        expected[static_cast<std::size_t>(row + col * ldc)] =
            0.75L * sum - 0.25L * c_host[static_cast<std::size_t>(row + col * ldc)];
      }
    }
    detail::close_vector<Kind>(backend_.download(c, c_host.size()), expected,
                                "gemm/" + std::to_string(m) + "x" + std::to_string(n) + "x" +
                                    std::to_string(k));
  }
};

template <class Backend, NumericType Kind>
int run_smoke(Backend &backend, std::ostream &output = std::cout,
              Coverage coverage = Coverage::all) {
  try {
    Smoke<Backend, Kind>(backend).run(output, coverage);
    return 0;
  } catch (const std::exception &error) {
    output << "blas smoke: FAIL (" << NumericTraits<Kind>::name << "): "
           << error.what() << '\n';
    return 1;
  }
}

template <template <NumericType> class Backend>
int run_selected(NumericType type, Coverage coverage,
                 std::ostream &output = std::cout) {
  switch (type) {
  case NumericType::half: {
    Backend<NumericType::half> backend;
    return run_smoke<Backend<NumericType::half>, NumericType::half>(backend, output, coverage);
  }
  case NumericType::f32: {
    Backend<NumericType::f32> backend;
    return run_smoke<Backend<NumericType::f32>, NumericType::f32>(backend, output, coverage);
  }
  case NumericType::f64: {
    Backend<NumericType::f64> backend;
    return run_smoke<Backend<NumericType::f64>, NumericType::f64>(backend, output, coverage);
  }
  case NumericType::i8: {
    Backend<NumericType::i8> backend;
    return run_smoke<Backend<NumericType::i8>, NumericType::i8>(backend, output, coverage);
  }
  }
  return 2;
}

} // namespace gemstone::conformance::blas

#endif
