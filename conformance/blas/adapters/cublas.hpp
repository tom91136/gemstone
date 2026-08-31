#ifndef GEMSTONE_CONFORMANCE_CUBLAS_ADAPTER_HPP
#define GEMSTONE_CONFORMANCE_CUBLAS_ADAPTER_HPP

#include "gemstone/conformance/blas_smoke.hpp"
#include <cuda_fp16.h>
#include <cuda_runtime_api.h>
#include <cublas_v2.h>

#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <vector>

namespace gemstone::conformance::blas {

template <NumericType Kind> struct CublasNative;
template <> struct CublasNative<NumericType::half> { using type = __half; };
template <> struct CublasNative<NumericType::f32>  { using type = float; };
template <> struct CublasNative<NumericType::f64>  { using type = double; };
template <> struct CublasNative<NumericType::i8>   { using type = int8_t; };

template <NumericType Kind> class CublasBackend {
public:
  using Scalar = typename CublasNative<Kind>::type;

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
        (void)cudaFree(data);
        data = nullptr;
        size = 0;
      }
    }
  };

  static constexpr bool supports(Operation operation) {
    switch (operation) {
    case Operation::axpy: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::scal: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::copy: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::dot:  return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::nrm2: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::asum: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::gemv: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::gemm: return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    }
    return false;
  }

  CublasBackend() {
    check(cublasCreate(&handle_), "cublasCreate");
    check(cublasSetPointerMode(handle_, CUBLAS_POINTER_MODE_HOST), "cublasSetPointerMode");
  }

  CublasBackend(const CublasBackend &) = delete;
  CublasBackend &operator=(const CublasBackend &) = delete;

  ~CublasBackend() {
    if (handle_ != nullptr) (void)cublasDestroy(handle_);
  }

  Buffer allocate(std::size_t elements) {
    Scalar *data = nullptr;
    checkCuda(cudaMalloc(reinterpret_cast<void **>(&data), elements * sizeof(Scalar)), "cudaMalloc");
    return Buffer(data, elements);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    requireSize(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (std::size_t i = 0; i < host.size(); ++i) converted[i] = fromHost(host[i]);
    checkCuda(cudaMemcpy(buffer.data, converted.data(), converted.size() * sizeof(Scalar),
                         cudaMemcpyHostToDevice), "cudaMemcpy H2D");
  }

  std::vector<long double> download(const Buffer &buffer, std::size_t elements) {
    requireSize(buffer, elements);
    std::vector<Scalar> converted(elements);
    checkCuda(cudaMemcpy(converted.data(), buffer.data, elements * sizeof(Scalar),
                         cudaMemcpyDeviceToHost), "cudaMemcpy D2H");
    std::vector<long double> host(elements);
    for (std::size_t i = 0; i < elements; ++i) host[i] = toHost(converted[i]);
    return host;
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::f32) {
      const float a = static_cast<float>(alpha);
      check(cublasSaxpy(handle_, n, &a, x.data, incx, y.data, incy), "Saxpy");
    } else if constexpr (Kind == NumericType::f64) {
      const double a = static_cast<double>(alpha);
      check(cublasDaxpy(handle_, n, &a, x.data, incx, y.data, incy), "Daxpy");
    } else {
      unsupported("axpy");
    }
    synchronize();
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == NumericType::f32) {
      const float a = static_cast<float>(alpha);
      check(cublasSscal(handle_, n, &a, x.data, incx), "Sscal");
    } else if constexpr (Kind == NumericType::f64) {
      const double a = static_cast<double>(alpha);
      check(cublasDscal(handle_, n, &a, x.data, incx), "Dscal");
    } else {
      unsupported("scal");
    }
    synchronize();
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::f32)
      check(cublasScopy(handle_, n, x.data, incx, y.data, incy), "Scopy");
    else if constexpr (Kind == NumericType::f64)
      check(cublasDcopy(handle_, n, x.data, incx, y.data, incy), "Dcopy");
    else
      unsupported("copy");
    synchronize();
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == NumericType::f32)
      check(cublasSdot(handle_, n, x.data, incx, y.data, incy, &result), "Sdot");
    else if constexpr (Kind == NumericType::f64)
      check(cublasDdot(handle_, n, x.data, incx, y.data, incy, &result), "Ddot");
    else
      unsupported("dot");
    synchronize();
    return toHost(result);
  }

  long double nrm2(int n, Buffer &x, int incx) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == NumericType::f32)
      check(cublasSnrm2(handle_, n, x.data, incx, &result), "Snrm2");
    else if constexpr (Kind == NumericType::f64)
      check(cublasDnrm2(handle_, n, x.data, incx, &result), "Dnrm2");
    else
      unsupported("nrm2");
    synchronize();
    return toHost(result);
  }

  long double asum(int n, Buffer &x, int incx) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == NumericType::f32)
      check(cublasSasum(handle_, n, x.data, incx, &result), "Sasum");
    else if constexpr (Kind == NumericType::f64)
      check(cublasDasum(handle_, n, x.data, incx, &result), "Dasum");
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
      check(cublasSgemv(handle_, CUBLAS_OP_N, m, n, &a_scalar, a.data, lda, x.data,
                        incx, &b_scalar, y.data, incy), "Sgemv");
    } else if constexpr (Kind == NumericType::f64) {
      const double a_scalar = static_cast<double>(alpha);
      const double b_scalar = static_cast<double>(beta);
      check(cublasDgemv(handle_, CUBLAS_OP_N, m, n, &a_scalar, a.data, lda, x.data,
                        incx, &b_scalar, y.data, incy), "Dgemv");
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
      check(cublasHgemm(handle_, CUBLAS_OP_N, CUBLAS_OP_N, m, n, k, &a_scalar,
                        a.data, lda, b.data, ldb, &b_scalar, c.data, ldc), "Hgemm");
    } else if constexpr (Kind == NumericType::f32) {
      const float a_scalar = static_cast<float>(alpha);
      const float b_scalar = static_cast<float>(beta);
      check(cublasSgemm(handle_, CUBLAS_OP_N, CUBLAS_OP_N, m, n, k, &a_scalar,
                        a.data, lda, b.data, ldb, &b_scalar, c.data, ldc), "Sgemm");
    } else if constexpr (Kind == NumericType::f64) {
      const double a_scalar = static_cast<double>(alpha);
      const double b_scalar = static_cast<double>(beta);
      check(cublasDgemm(handle_, CUBLAS_OP_N, CUBLAS_OP_N, m, n, k, &a_scalar,
                        a.data, lda, b.data, ldb, &b_scalar, c.data, ldc), "Dgemm");
    } else {
      unsupported("gemm");
    }
    synchronize();
  }

private:
  cublasHandle_t handle_ = nullptr;

  static Scalar fromHost(long double value) {
    if constexpr (Kind == NumericType::half)
      return __float2half(static_cast<float>(value));
    else
      return static_cast<Scalar>(value);
  }

  static long double toHost(const Scalar &value) {
    if constexpr (Kind == NumericType::half)
      return static_cast<long double>(__half2float(value));
    else
      return static_cast<long double>(value);
  }

  static void requireSize(const Buffer &buffer, std::size_t elements) {
    if (buffer.size < elements) throw std::runtime_error("buffer is smaller than the requested transfer");
  }

  static void unsupported(const char *operation) {
    throw detail::UnsupportedOperation(std::string(operation) + " is not supported for this numeric type");
  }

  static void checkCuda(cudaError_t status, const char *operation) {
    if (status != cudaSuccess)
      throw std::runtime_error(std::string(operation) + ": " + cudaGetErrorString(status));
  }

  static void check(cublasStatus_t status, const char *operation) {
    if (status == CUBLAS_STATUS_NOT_SUPPORTED || status == CUBLAS_STATUS_ARCH_MISMATCH)
      throw detail::UnsupportedOperation(std::string(operation) + " is not supported by this device");
    if (status != CUBLAS_STATUS_SUCCESS)
      throw std::runtime_error(std::string(operation) + " failed with cuBLAS status " +
                               std::to_string(static_cast<int>(status)));
  }

  static void synchronize() { checkCuda(cudaDeviceSynchronize(), "cudaDeviceSynchronize"); }
};

} // namespace gemstone::conformance::blas

#endif
