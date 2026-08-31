#ifndef UNIFIED_SUITE_ADAPTERS_CUBLAS_HPP
#define UNIFIED_SUITE_ADAPTERS_CUBLAS_HPP

#include "unified_suite/blas.hpp"

#include <cublas_v2.h>
#include <cuda_fp16.h>
#include <cuda_runtime_api.h>

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

namespace unified_suite::adapters {

template <ScalarType> struct CublasNative;
template <> struct CublasNative<ScalarType::f16> {
  using type = __half;
};
template <> struct CublasNative<ScalarType::f32> {
  using type = float;
};
template <> struct CublasNative<ScalarType::f64> {
  using type = double;
};
template <> struct CublasNative<ScalarType::i8> {
  using type = std::int8_t;
};

template <ScalarType Kind> class Cublas {
public:
  using Scalar = typename CublasNative<Kind>::type;

  class Buffer {
  public:
    Buffer() = default;
    Buffer(Scalar *data, std::size_t size) : data_(data), size_(size) {}
    Buffer(const Buffer &) = delete;
    Buffer &operator=(const Buffer &) = delete;
    Buffer(Buffer &&other) noexcept : data_(std::exchange(other.data_, nullptr)), size_(other.size_) {}
    Buffer &operator=(Buffer &&other) noexcept {
      if (this != &other) {
        release();
        data_ = std::exchange(other.data_, nullptr);
        size_ = other.size_;
      }
      return *this;
    }
    ~Buffer() { release(); }

    Scalar *data() { return data_; }
    const Scalar *data() const { return data_; }
    std::size_t size() const { return size_; }

  private:
    Scalar *data_{};
    std::size_t size_{};
    void release() {
      if (data_)
        (void)cudaFree(data_);
      data_ = nullptr;
    }
  };

  Cublas() {
    check(cublasCreate(&handle_), "cublasCreate");
    try {
      check(cublasSetPointerMode(handle_, CUBLAS_POINTER_MODE_HOST), "cublasSetPointerMode");
    } catch (...) {
      (void)cublasDestroy(handle_);
      handle_ = nullptr;
      throw;
    }
  }
  Cublas(const Cublas &) = delete;
  Cublas &operator=(const Cublas &) = delete;
  ~Cublas() {
    if (handle_)
      (void)cublasDestroy(handle_);
  }

  bool supports(blas::Operation operation) const {
    switch (operation) {
    case blas::Operation::gemm:
    case blas::Operation::gemm_nt:
    case blas::Operation::gemm_tn:
    case blas::Operation::gemm_tt:
    case blas::Operation::gemm_strided_batched:
      if constexpr (Kind == ScalarType::f16) {
        int device = 0;
        cudaDeviceProp properties{};
        return cudaGetDevice(&device) == cudaSuccess &&
               cudaGetDeviceProperties(&properties, device) == cudaSuccess &&
               (properties.major > 5 || (properties.major == 5 && properties.minor >= 3));
      }
      return Kind == ScalarType::f32 || Kind == ScalarType::f64;
    case blas::Operation::axpy:
    case blas::Operation::scal:
    case blas::Operation::copy:
    case blas::Operation::dot:
    case blas::Operation::nrm2:
    case blas::Operation::asum:
    case blas::Operation::gemv:
    case blas::Operation::gemv_transpose:
      return Kind == ScalarType::f32 || Kind == ScalarType::f64;
    }
    return false;
  }

  Buffer allocate(std::size_t size) {
    Scalar *data = nullptr;
    check_cuda(cudaMalloc(reinterpret_cast<void **>(&data),
                          unified_suite::detail::checked_multiply(std::max<std::size_t>(size, 1),
                                                                  sizeof(Scalar), "cuBLAS allocation")),
               "cudaMalloc");
    return Buffer(data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    require_size(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (std::size_t index = 0; index < host.size(); ++index)
      converted[index] = from_host(host[index]);
    if (!converted.empty())
      check_cuda(cudaMemcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar),
                            cudaMemcpyHostToDevice),
                 "cudaMemcpy H2D");
  }

  std::vector<long double> download(const Buffer &buffer, std::size_t size) {
    require_size(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty())
      check_cuda(cudaMemcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar),
                            cudaMemcpyDeviceToHost),
                 "cudaMemcpy D2H");
    std::vector<long double> result(size);
    for (std::size_t index = 0; index < size; ++index)
      result[index] = to_host(converted[index]);
    return result;
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32) {
      const float value = static_cast<float>(alpha);
      check(cublasSaxpy(handle_, n, &value, x.data(), incx, y.data(), incy), "cublasSaxpy");
    } else if constexpr (Kind == ScalarType::f64) {
      const double value = static_cast<double>(alpha);
      check(cublasDaxpy(handle_, n, &value, x.data(), incx, y.data(), incy), "cublasDaxpy");
    } else
      unsupported("axpy");
    synchronize();
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f32) {
      const float value = static_cast<float>(alpha);
      check(cublasSscal(handle_, n, &value, x.data(), incx), "cublasSscal");
    } else if constexpr (Kind == ScalarType::f64) {
      const double value = static_cast<double>(alpha);
      check(cublasDscal(handle_, n, &value, x.data(), incx), "cublasDscal");
    } else
      unsupported("scal");
    synchronize();
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32)
      check(cublasScopy(handle_, n, x.data(), incx, y.data(), incy), "cublasScopy");
    else if constexpr (Kind == ScalarType::f64)
      check(cublasDcopy(handle_, n, x.data(), incx, y.data(), incy), "cublasDcopy");
    else
      unsupported("copy");
    synchronize();
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32)
      check(cublasSdot(handle_, n, x.data(), incx, y.data(), incy, &result), "cublasSdot");
    else if constexpr (Kind == ScalarType::f64)
      check(cublasDdot(handle_, n, x.data(), incx, y.data(), incy, &result), "cublasDdot");
    else
      unsupported("dot");
    synchronize();
    return to_host(result);
  }

  long double nrm2(int n, Buffer &x, int incx) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32)
      check(cublasSnrm2(handle_, n, x.data(), incx, &result), "cublasSnrm2");
    else if constexpr (Kind == ScalarType::f64)
      check(cublasDnrm2(handle_, n, x.data(), incx, &result), "cublasDnrm2");
    else
      unsupported("nrm2");
    synchronize();
    return to_host(result);
  }

  long double asum(int n, Buffer &x, int incx) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32)
      check(cublasSasum(handle_, n, x.data(), incx, &result), "cublasSasum");
    else if constexpr (Kind == ScalarType::f64)
      check(cublasDasum(handle_, n, x.data(), incx, &result), "cublasDasum");
    else
      unsupported("asum");
    synchronize();
    return to_host(result);
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x,
            int incx, long double beta, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(cublasSgemv(handle_, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(),
                        incy),
            "cublasSgemv");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(cublasDgemv(handle_, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(),
                        incy),
            "cublasDgemv");
    } else
      unsupported("gemv");
    synchronize();
  }

  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha,
            Buffer &a, int lda, Buffer &b, int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::f16) {
      const auto av = from_host(alpha), bv = from_host(beta);
      check(cublasHgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                        b.data(), ldb, &bv, c.data(), ldc),
            "cublasHgemm");
    } else if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(cublasSgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                        b.data(), ldb, &bv, c.data(), ldc),
            "cublasSgemm");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(cublasDgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                        b.data(), ldb, &bv, c.data(), ldc),
            "cublasDgemm");
    } else
      unsupported("gemm");
    synchronize();
  }

  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k,
                            long double alpha, Buffer &a, int lda, std::size_t stride_a, Buffer &b, int ldb,
                            std::size_t stride_b, long double beta, Buffer &c, int ldc, std::size_t stride_c,
                            int batch_count) {
    const auto sa = static_cast<long long>(stride_a);
    const auto sb = static_cast<long long>(stride_b);
    const auto sc = static_cast<long long>(stride_c);
    if constexpr (Kind == ScalarType::f16) {
      const auto av = from_host(alpha), bv = from_host(beta);
      check(cublasHgemmStridedBatched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av,
                                      a.data(), lda, sa, b.data(), ldb, sb, &bv, c.data(), ldc, sc,
                                      batch_count),
            "cublasHgemmStridedBatched");
    } else if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(cublasSgemmStridedBatched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av,
                                      a.data(), lda, sa, b.data(), ldb, sb, &bv, c.data(), ldc, sc,
                                      batch_count),
            "cublasSgemmStridedBatched");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(cublasDgemmStridedBatched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av,
                                      a.data(), lda, sa, b.data(), ldb, sb, &bv, c.data(), ldc, sc,
                                      batch_count),
            "cublasDgemmStridedBatched");
    } else
      unsupported("gemm-strided-batched");
    synchronize();
  }

private:
  cublasHandle_t handle_{};

  static cublasOperation_t native(blas::Transpose transpose) {
    return transpose == blas::Transpose::none ? CUBLAS_OP_N : CUBLAS_OP_T;
  }

  static Scalar from_host(long double value) {
    if constexpr (Kind == ScalarType::f16)
      return __float2half(static_cast<float>(value));
    else
      return static_cast<Scalar>(value);
  }
  static long double to_host(Scalar value) {
    if constexpr (Kind == ScalarType::f16)
      return static_cast<long double>(__half2float(value));
    else
      return static_cast<long double>(value);
  }
  static void require_size(const Buffer &buffer, std::size_t size) {
    if (buffer.size() < size)
      throw std::runtime_error("buffer is smaller than transfer");
  }
  [[noreturn]] static void unsupported(const char *operation) {
    throw Unsupported(std::string(operation) + " is unsupported by cuBLAS for this type");
  }
  static void check(cublasStatus_t status, const char *operation) {
    if (status == CUBLAS_STATUS_NOT_SUPPORTED || status == CUBLAS_STATUS_ARCH_MISMATCH)
      throw Unsupported(std::string(operation) + " is unsupported by this device");
    if (status != CUBLAS_STATUS_SUCCESS)
      throw std::runtime_error(std::string(operation) + " failed with status " + std::to_string(status));
  }
  static void check_cuda(cudaError_t status, const char *operation) {
    if (status != cudaSuccess)
      throw std::runtime_error(std::string(operation) + ": " + cudaGetErrorString(status));
  }
  static void synchronize() { check_cuda(cudaDeviceSynchronize(), "cudaDeviceSynchronize"); }
};

} // namespace unified_suite::adapters

#endif
