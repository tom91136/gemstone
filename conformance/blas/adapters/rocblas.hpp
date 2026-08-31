#ifndef GEMSTONE_CONFORMANCE_ROCBLAS_ADAPTER_HPP
#define GEMSTONE_CONFORMANCE_ROCBLAS_ADAPTER_HPP

#include "gemstone/conformance/blas_smoke.hpp"
#include <hip/hip_runtime.h>
#include <rocblas.h>

#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <vector>

namespace gemstone::conformance::blas {

template <NumericType Kind> struct RocblasNative;
template <> struct RocblasNative<NumericType::half> { using type = rocblas_half; };
template <> struct RocblasNative<NumericType::f32>  { using type = float; };
template <> struct RocblasNative<NumericType::f64>  { using type = double; };
template <> struct RocblasNative<NumericType::i8>   { using type = int8_t; };

template <NumericType Kind> class RocblasBackend {
public:
  using Scalar = typename RocblasNative<Kind>::type;

  struct Buffer {
    Scalar *data = nullptr;
    std::size_t size = 0;

    Buffer() = default;
    Buffer(Scalar *value, std::size_t elements) : data(value), size(elements) {}
    Buffer(const Buffer &) = delete;
    Buffer &operator=(const Buffer &) = delete;
    Buffer(Buffer &&other) noexcept : data(other.data), size(other.size) {
      other.data = nullptr;
      other.size = 0;
    }
    Buffer &operator=(Buffer &&other) noexcept {
      if (this != &other) {
        release();
        data = other.data;
        size = other.size;
        other.data = nullptr;
        other.size = 0;
      }
      return *this;
    }
    ~Buffer() { release(); }

  private:
    void release() {
      if (data != nullptr) {
        (void)hipFree(data);
        data = nullptr;
        size = 0;
      }
    }
  };

  static constexpr bool supports(Operation operation) {
    switch (operation) {
    case Operation::axpy: return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::scal: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::copy: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::dot:  return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::nrm2: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::asum: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::gemv: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::gemm: return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    }
    return false;
  }

  RocblasBackend() {
    check(rocblas_create_handle(&handle_), "rocblas_create_handle");
    check(rocblas_set_pointer_mode(handle_, rocblas_pointer_mode_host),
          "rocblas_set_pointer_mode");
  }

  RocblasBackend(const RocblasBackend &) = delete;
  RocblasBackend &operator=(const RocblasBackend &) = delete;

  ~RocblasBackend() {
    if (handle_ != nullptr) (void)rocblas_destroy_handle(handle_);
  }

  Buffer allocate(std::size_t elements) {
    Scalar *data = nullptr;
    checkHip(hipMalloc(reinterpret_cast<void **>(&data), elements * sizeof(Scalar)),
             "hipMalloc");
    return Buffer(data, elements);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    requireSize(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (std::size_t i = 0; i < host.size(); ++i) converted[i] = fromHost(host[i]);
    checkHip(hipMemcpy(buffer.data, converted.data(), converted.size() * sizeof(Scalar),
                       hipMemcpyHostToDevice),
             "hipMemcpy H2D");
  }

  std::vector<long double> download(const Buffer &buffer, std::size_t elements) {
    requireSize(buffer, elements);
    std::vector<Scalar> converted(elements);
    checkHip(hipMemcpy(converted.data(), buffer.data, elements * sizeof(Scalar),
                       hipMemcpyDeviceToHost),
             "hipMemcpy D2H");
    std::vector<long double> host(elements);
    for (std::size_t i = 0; i < elements; ++i) host[i] = toHost(converted[i]);
    return host;
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::half) {
      const Scalar a = fromHost(alpha);
      check(rocblas_haxpy(handle_, n, &a, x.data, incx, y.data, incy), "haxpy");
    } else if constexpr (Kind == NumericType::f32) {
      const float a = static_cast<float>(alpha);
      check(rocblas_saxpy(handle_, n, &a, x.data, incx, y.data, incy), "saxpy");
    } else if constexpr (Kind == NumericType::f64) {
      const double a = static_cast<double>(alpha);
      check(rocblas_daxpy(handle_, n, &a, x.data, incx, y.data, incy), "daxpy");
    } else {
      unsupported("axpy");
    }
    synchronize();
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == NumericType::f32) {
      const float a = static_cast<float>(alpha);
      check(rocblas_sscal(handle_, n, &a, x.data, incx), "sscal");
    } else if constexpr (Kind == NumericType::f64) {
      const double a = static_cast<double>(alpha);
      check(rocblas_dscal(handle_, n, &a, x.data, incx), "dscal");
    } else {
      unsupported("scal");
    }
    synchronize();
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::f32)
      check(rocblas_scopy(handle_, n, x.data, incx, y.data, incy), "scopy");
    else if constexpr (Kind == NumericType::f64)
      check(rocblas_dcopy(handle_, n, x.data, incx, y.data, incy), "dcopy");
    else
      unsupported("copy");
    synchronize();
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == NumericType::half)
      check(rocblas_hdot(handle_, n, x.data, incx, y.data, incy, &result), "hdot");
    else if constexpr (Kind == NumericType::f32)
      check(rocblas_sdot(handle_, n, x.data, incx, y.data, incy, &result), "sdot");
    else if constexpr (Kind == NumericType::f64)
      check(rocblas_ddot(handle_, n, x.data, incx, y.data, incy, &result), "ddot");
    else
      unsupported("dot");
    synchronize();
    return toHost(result);
  }

  long double nrm2(int n, Buffer &x, int incx) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == NumericType::f32)
      check(rocblas_snrm2(handle_, n, x.data, incx, &result), "snrm2");
    else if constexpr (Kind == NumericType::f64)
      check(rocblas_dnrm2(handle_, n, x.data, incx, &result), "dnrm2");
    else
      unsupported("nrm2");
    synchronize();
    return toHost(result);
  }

  long double asum(int n, Buffer &x, int incx) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == NumericType::f32)
      check(rocblas_sasum(handle_, n, x.data, incx, &result), "sasum");
    else if constexpr (Kind == NumericType::f64)
      check(rocblas_dasum(handle_, n, x.data, incx, &result), "dasum");
    else
      unsupported("asum");
    synchronize();
    return toHost(result);
  }

  void gemv(int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx,
            long double beta, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::f32) {
      const float a_scalar = static_cast<float>(alpha);
      const float b_scalar = static_cast<float>(beta);
      check(rocblas_sgemv(handle_, rocblas_operation_none, m, n, &a_scalar, a.data,
                          lda, x.data, incx, &b_scalar, y.data, incy), "sgemv");
    } else if constexpr (Kind == NumericType::f64) {
      const double a_scalar = static_cast<double>(alpha);
      const double b_scalar = static_cast<double>(beta);
      check(rocblas_dgemv(handle_, rocblas_operation_none, m, n, &a_scalar, a.data,
                          lda, x.data, incx, &b_scalar, y.data, incy), "dgemv");
    } else {
      unsupported("gemv");
    }
    synchronize();
  }

  void gemm(int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == NumericType::half) {
      const Scalar a_scalar = fromHost(alpha);
      const Scalar b_scalar = fromHost(beta);
      check(rocblas_hgemm(handle_, rocblas_operation_none, rocblas_operation_none,
                          m, n, k, &a_scalar, a.data, lda, b.data, ldb, &b_scalar,
                          c.data, ldc), "hgemm");
    } else if constexpr (Kind == NumericType::f32) {
      const float a_scalar = static_cast<float>(alpha);
      const float b_scalar = static_cast<float>(beta);
      check(rocblas_sgemm(handle_, rocblas_operation_none, rocblas_operation_none,
                          m, n, k, &a_scalar, a.data, lda, b.data, ldb, &b_scalar,
                          c.data, ldc), "sgemm");
    } else if constexpr (Kind == NumericType::f64) {
      const double a_scalar = static_cast<double>(alpha);
      const double b_scalar = static_cast<double>(beta);
      check(rocblas_dgemm(handle_, rocblas_operation_none, rocblas_operation_none,
                          m, n, k, &a_scalar, a.data, lda, b.data, ldb, &b_scalar,
                          c.data, ldc), "dgemm");
    } else {
      unsupported("gemm");
    }
    synchronize();
  }

private:
  rocblas_handle handle_ = nullptr;

  static Scalar fromHost(long double value) {
    if constexpr (Kind == NumericType::half) {
#if defined(ROCM_USE_FLOAT16)
      return static_cast<Scalar>(static_cast<float>(value));
#else
      Scalar result{};
      result.data = detail::float_to_half_bits(static_cast<float>(value));
      return result;
#endif
    } else {
      return static_cast<Scalar>(value);
    }
  }

  static long double toHost(const Scalar &value) {
    if constexpr (Kind == NumericType::half) {
#if defined(ROCM_USE_FLOAT16)
      return static_cast<long double>(static_cast<float>(value));
#else
      return static_cast<long double>(detail::half_bits_to_float(value.data));
#endif
    } else {
      return static_cast<long double>(value);
    }
  }

  static void requireSize(const Buffer &buffer, std::size_t elements) {
    if (buffer.size < elements) throw std::runtime_error("buffer is smaller than the requested transfer");
  }

  static void unsupported(const char *operation) {
    throw detail::UnsupportedOperation(std::string(operation) + " is not supported for this numeric type");
  }

  static void checkHip(hipError_t status, const char *operation) {
    if (status != hipSuccess)
      throw std::runtime_error(std::string(operation) + ": " + hipGetErrorString(status));
  }

  static void check(rocblas_status status, const char *operation) {
    if (status == rocblas_status_not_implemented || status == rocblas_status_arch_mismatch)
      throw detail::UnsupportedOperation(std::string(operation) + " is not supported by this device");
    if (status != rocblas_status_success)
      throw std::runtime_error(std::string(operation) + " failed with rocBLAS status " +
                               std::to_string(static_cast<int>(status)));
  }

  static void synchronize() { checkHip(hipDeviceSynchronize(), "hipDeviceSynchronize"); }
};

} // namespace gemstone::conformance::blas

#endif
