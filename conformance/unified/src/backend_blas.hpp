#pragma once

#include <fmt/format.h>
#include "test_support.hpp"
#include "blas_cases.hpp"

#if defined(UNIFIED_API_CUDA)

  #include <algorithm>
  #include <cstdint>
  #include <stdexcept>
  #include <string>
  #include <vector>

  #include <cublas_v2.h>
  #include <cuda_fp16.h>
  #include <cuda_runtime_api.h>

namespace unified::backend {
struct CudaBufferMemory {
  template <class T> static void release(T *data) noexcept { (void)cudaFree(data); }
};

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
  using type = int8_t;
};

template <ScalarType Kind> class Cublas {
public:
  using Scalar = typename CublasNative<Kind>::type;
  using Buffer = blas_detail::DeviceBuffer<Scalar, CudaBufferMemory>;

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
    if (handle_) (void)cublasDestroy(handle_);
  }

  bool supports_axpy() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_scal() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_copy() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_dot() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_nrm2() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_asum() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_gemv() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_gemm() const {
    if constexpr (Kind == ScalarType::f16) {
      int device = 0;
      cudaDeviceProp properties{};
      return cudaGetDevice(&device) == cudaSuccess && cudaGetDeviceProperties(&properties, device) == cudaSuccess
             && (properties.major > 5 || (properties.major == 5 && properties.minor >= 3));
    }
    return Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_gemm_strided_batched() const {
    if constexpr (Kind == ScalarType::f16) {
      int device = 0;
      cudaDeviceProp properties{};
      return cudaGetDevice(&device) == cudaSuccess && cudaGetDeviceProperties(&properties, device) == cudaSuccess
             && (properties.major > 5 || (properties.major == 5 && properties.minor >= 3));
    }
    return Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  Buffer allocate(size_t size) {
    Scalar *data = nullptr;
    check_cuda(cudaMalloc(reinterpret_cast<void **>(&data),
                          unified::support::checked_multiply(std::max<size_t>(size, 1), sizeof(Scalar), "cuBLAS allocation")),
               "cudaMalloc");
    return Buffer(data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    require_size(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (size_t index = 0; index < host.size(); ++index)
      converted[index] = from_host(host[index]);
    if (!converted.empty())
      check_cuda(cudaMemcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar), cudaMemcpyHostToDevice), "cudaMemcpy H2D");
  }

  std::vector<long double> download(const Buffer &buffer, size_t size) {
    require_size(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty())
      check_cuda(cudaMemcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar), cudaMemcpyDeviceToHost), "cudaMemcpy D2H");
    std::vector<long double> result(size);
    for (size_t index = 0; index < size; ++index)
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
    } else unsupported("axpy");
    synchronize();
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f32) {
      const float value = static_cast<float>(alpha);
      check(cublasSscal(handle_, n, &value, x.data(), incx), "cublasSscal");
    } else if constexpr (Kind == ScalarType::f64) {
      const double value = static_cast<double>(alpha);
      check(cublasDscal(handle_, n, &value, x.data(), incx), "cublasDscal");
    } else unsupported("scal");
    synchronize();
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32) check(cublasScopy(handle_, n, x.data(), incx, y.data(), incy), "cublasScopy");
    else if constexpr (Kind == ScalarType::f64) check(cublasDcopy(handle_, n, x.data(), incx, y.data(), incy), "cublasDcopy");
    else unsupported("copy");
    synchronize();
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32) check(cublasSdot(handle_, n, x.data(), incx, y.data(), incy, &result), "cublasSdot");
    else if constexpr (Kind == ScalarType::f64) check(cublasDdot(handle_, n, x.data(), incx, y.data(), incy, &result), "cublasDdot");
    else unsupported("dot");
    synchronize();
    return to_host(result);
  }

  long double nrm2(int n, Buffer &x, int incx) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32) check(cublasSnrm2(handle_, n, x.data(), incx, &result), "cublasSnrm2");
    else if constexpr (Kind == ScalarType::f64) check(cublasDnrm2(handle_, n, x.data(), incx, &result), "cublasDnrm2");
    else unsupported("nrm2");
    synchronize();
    return to_host(result);
  }

  long double asum(int n, Buffer &x, int incx) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32) check(cublasSasum(handle_, n, x.data(), incx, &result), "cublasSasum");
    else if constexpr (Kind == ScalarType::f64) check(cublasDasum(handle_, n, x.data(), incx, &result), "cublasDasum");
    else unsupported("asum");
    synchronize();
    return to_host(result);
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx, long double beta,
            Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(cublasSgemv(handle_, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(), incy), "cublasSgemv");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(cublasDgemv(handle_, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(), incy), "cublasDgemv");
    } else unsupported("gemv");
    synchronize();
  }

  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::f16) {
      const auto av = from_host(alpha), bv = from_host(beta);
      check(cublasHgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "cublasHgemm");
    } else if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(cublasSgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "cublasSgemm");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(cublasDgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "cublasDgemm");
    } else unsupported("gemm");
    synchronize();
  }

  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a,
                            int lda, size_t stride_a, Buffer &b, int ldb, size_t stride_b, long double beta, Buffer &c, int ldc,
                            size_t stride_c, int batch_count) {
    const auto sa = static_cast<int64_t>(stride_a);
    const auto sb = static_cast<int64_t>(stride_b);
    const auto sc = static_cast<int64_t>(stride_c);
    if constexpr (Kind == ScalarType::f16) {
      const auto av = from_host(alpha), bv = from_host(beta);
      check(cublasHgemmStridedBatched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, sa, b.data(), ldb, sb,
                                      &bv, c.data(), ldc, sc, batch_count),
            "cublasHgemmStridedBatched");
    } else if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(cublasSgemmStridedBatched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, sa, b.data(), ldb, sb,
                                      &bv, c.data(), ldc, sc, batch_count),
            "cublasSgemmStridedBatched");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(cublasDgemmStridedBatched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, sa, b.data(), ldb, sb,
                                      &bv, c.data(), ldc, sc, batch_count),
            "cublasDgemmStridedBatched");
    } else unsupported("gemm-strided-batched");
    synchronize();
  }

private:
  cublasHandle_t handle_{};

  static cublasOperation_t native(blas::Transpose transpose) { return transpose == blas::Transpose::none ? CUBLAS_OP_N : CUBLAS_OP_T; }

  static Scalar from_host(long double value) {
    if constexpr (Kind == ScalarType::f16) return __float2half(static_cast<float>(value));
    else return static_cast<Scalar>(value);
  }
  static long double to_host(Scalar value) {
    if constexpr (Kind == ScalarType::f16) return static_cast<long double>(__half2float(value));
    else return static_cast<long double>(value);
  }
  static void require_size(const Buffer &buffer, size_t size) {
    if (buffer.size() < size) throw std::runtime_error("buffer is smaller than transfer");
  }
  [[noreturn]] static void unsupported(const char *operation) { SKIP(std::string(operation) + " is unsupported by cuBLAS for this type"); }
  static void check(cublasStatus_t status, const char *operation) {
    if (status == CUBLAS_STATUS_NOT_SUPPORTED || status == CUBLAS_STATUS_ARCH_MISMATCH)
      SKIP(std::string(operation) + " is unsupported by this device");
    if (status != CUBLAS_STATUS_SUCCESS)
      throw std::runtime_error(fmt::format("{} failed with status {}", operation, static_cast<int>(status)));
  }
  static void check_cuda(cudaError_t status, const char *operation) {
    if (status != cudaSuccess) throw std::runtime_error(std::string(operation) + ": " + cudaGetErrorString(status));
  }
  static void synchronize() { check_cuda(cudaDeviceSynchronize(), "cudaDeviceSynchronize"); }
};

template <ScalarType Kind> using Blas = Cublas<Kind>;
} // namespace unified::backend

#elif defined(UNIFIED_API_HIP)

  #include <hip/hip_runtime.h>
  #include <rocblas/rocblas.h>

namespace unified::backend {
struct HipBufferMemory {
  template <class T> static void release(T *data) noexcept { (void)hipFree(data); }
};

template <ScalarType> struct RocblasNative;
template <> struct RocblasNative<ScalarType::f16> {
  using type = rocblas_half;
};
template <> struct RocblasNative<ScalarType::f32> {
  using type = float;
};
template <> struct RocblasNative<ScalarType::f64> {
  using type = double;
};
template <> struct RocblasNative<ScalarType::i8> {
  using type = int8_t;
};

template <ScalarType Kind> class Rocblas {
public:
  using Scalar = typename RocblasNative<Kind>::type;
  using Buffer = blas_detail::DeviceBuffer<Scalar, HipBufferMemory>;

  Rocblas() { check(rocblas_create_handle(&handle_), "rocblas_create_handle"); }
  Rocblas(const Rocblas &) = delete;
  Rocblas &operator=(const Rocblas &) = delete;
  ~Rocblas() {
    if (handle_) (void)rocblas_destroy_handle(handle_);
  }

  bool supports_axpy() const { return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_scal() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_copy() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_dot() const { return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_nrm2() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_asum() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_gemv() const { return Kind == ScalarType::f32 || Kind == ScalarType::f64; }

  bool supports_gemm() const {
  #if defined(CONFORMANCE_HIP_HAS_TENSILE)
    return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
  #else
    return false;
  #endif
  }

  bool supports_gemm_strided_batched() const {
  #if defined(CONFORMANCE_HIP_HAS_TENSILE)
    return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
  #else
    return false;
  #endif
  }

  Buffer allocate(size_t size) {
    Scalar *data = nullptr;
    check_hip(hipMalloc(reinterpret_cast<void **>(&data),
                        unified::support::checked_multiply(std::max<size_t>(size, 1), sizeof(Scalar), "rocBLAS allocation")),
              "hipMalloc");
    return Buffer(data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    require_size(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (size_t index = 0; index < host.size(); ++index)
      converted[index] = from_host(host[index]);
    if (!converted.empty())
      check_hip(hipMemcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar), hipMemcpyHostToDevice), "hipMemcpy H2D");
  }

  std::vector<long double> download(const Buffer &buffer, size_t size) {
    require_size(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty())
      check_hip(hipMemcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar), hipMemcpyDeviceToHost), "hipMemcpy D2H");
    std::vector<long double> result(size);
    for (size_t index = 0; index < size; ++index)
      result[index] = to_host(converted[index]);
    return result;
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f16) {
      const auto value = from_host(alpha);
      check(rocblas_haxpy(handle_, n, &value, x.data(), incx, y.data(), incy), "rocblas_haxpy");
    } else if constexpr (Kind == ScalarType::f32) {
      const float value = static_cast<float>(alpha);
      check(rocblas_saxpy(handle_, n, &value, x.data(), incx, y.data(), incy), "rocblas_saxpy");
    } else if constexpr (Kind == ScalarType::f64) {
      const double value = static_cast<double>(alpha);
      check(rocblas_daxpy(handle_, n, &value, x.data(), incx, y.data(), incy), "rocblas_daxpy");
    } else unsupported("axpy");
    synchronize();
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f32) {
      const float value = static_cast<float>(alpha);
      check(rocblas_sscal(handle_, n, &value, x.data(), incx), "rocblas_sscal");
    } else if constexpr (Kind == ScalarType::f64) {
      const double value = static_cast<double>(alpha);
      check(rocblas_dscal(handle_, n, &value, x.data(), incx), "rocblas_dscal");
    } else unsupported("scal");
    synchronize();
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32) check(rocblas_scopy(handle_, n, x.data(), incx, y.data(), incy), "rocblas_scopy");
    else if constexpr (Kind == ScalarType::f64) check(rocblas_dcopy(handle_, n, x.data(), incx, y.data(), incy), "rocblas_dcopy");
    else unsupported("copy");
    synchronize();
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f16) check(rocblas_hdot(handle_, n, x.data(), incx, y.data(), incy, &result), "rocblas_hdot");
    else if constexpr (Kind == ScalarType::f32) check(rocblas_sdot(handle_, n, x.data(), incx, y.data(), incy, &result), "rocblas_sdot");
    else if constexpr (Kind == ScalarType::f64) check(rocblas_ddot(handle_, n, x.data(), incx, y.data(), incy, &result), "rocblas_ddot");
    else unsupported("dot");
    synchronize();
    return to_host(result);
  }

  long double nrm2(int n, Buffer &x, int incx) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32) check(rocblas_snrm2(handle_, n, x.data(), incx, &result), "rocblas_snrm2");
    else if constexpr (Kind == ScalarType::f64) check(rocblas_dnrm2(handle_, n, x.data(), incx, &result), "rocblas_dnrm2");
    else unsupported("nrm2");
    synchronize();
    return to_host(result);
  }

  long double asum(int n, Buffer &x, int incx) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32) check(rocblas_sasum(handle_, n, x.data(), incx, &result), "rocblas_sasum");
    else if constexpr (Kind == ScalarType::f64) check(rocblas_dasum(handle_, n, x.data(), incx, &result), "rocblas_dasum");
    else unsupported("asum");
    synchronize();
    return to_host(result);
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx, long double beta,
            Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(rocblas_sgemv(handle_, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(), incy), "rocblas_sgemv");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(rocblas_dgemv(handle_, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(), incy), "rocblas_dgemv");
    } else unsupported("gemv");
    synchronize();
  }

  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::f16) {
      const auto av = from_host(alpha), bv = from_host(beta);
      check(
          rocblas_hgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
          "rocblas_hgemm");
    } else if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(
          rocblas_sgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
          "rocblas_sgemm");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(
          rocblas_dgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
          "rocblas_dgemm");
    } else unsupported("gemm");
    synchronize();
  }

  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a,
                            int lda, size_t stride_a, Buffer &b, int ldb, size_t stride_b, long double beta, Buffer &c, int ldc,
                            size_t stride_c, int batch_count) {
    if constexpr (Kind == ScalarType::f16) {
      const auto av = from_host(alpha), bv = from_host(beta);
      check(rocblas_hgemm_strided_batched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                                          static_cast<rocblas_stride>(stride_a), b.data(), ldb, static_cast<rocblas_stride>(stride_b), &bv,
                                          c.data(), ldc, static_cast<rocblas_stride>(stride_c), batch_count),
            "rocblas_hgemm_strided_batched");
    } else if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(rocblas_sgemm_strided_batched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                                          static_cast<rocblas_stride>(stride_a), b.data(), ldb, static_cast<rocblas_stride>(stride_b), &bv,
                                          c.data(), ldc, static_cast<rocblas_stride>(stride_c), batch_count),
            "rocblas_sgemm_strided_batched");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(rocblas_dgemm_strided_batched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                                          static_cast<rocblas_stride>(stride_a), b.data(), ldb, static_cast<rocblas_stride>(stride_b), &bv,
                                          c.data(), ldc, static_cast<rocblas_stride>(stride_c), batch_count),
            "rocblas_dgemm_strided_batched");
    } else unsupported("gemm-strided-batched");
    synchronize();
  }

private:
  rocblas_handle handle_{};

  static rocblas_operation native(blas::Transpose transpose) {
    return transpose == blas::Transpose::none ? rocblas_operation_none : rocblas_operation_transpose;
  }

  static Scalar from_host(long double value) {
    if constexpr (Kind == ScalarType::f16) {
  #if defined(ROCM_USE_FLOAT16)
      return static_cast<Scalar>(static_cast<float>(value));
  #else
      Scalar result{};
      result.data = blas::float_to_half_bits(static_cast<float>(value));
      return result;
  #endif
    } else return static_cast<Scalar>(value);
  }
  static long double to_host(const Scalar &value) {
    if constexpr (Kind == ScalarType::f16) {
  #if defined(ROCM_USE_FLOAT16)
      return static_cast<long double>(static_cast<float>(value));
  #else
      return static_cast<long double>(blas::half_bits_to_float(value.data));
  #endif
    } else return static_cast<long double>(value);
  }
  static void require_size(const Buffer &buffer, size_t size) {
    if (buffer.size() < size) throw std::runtime_error("buffer is smaller than transfer");
  }
  [[noreturn]] static void unsupported(const char *operation) { SKIP(std::string(operation) + " is unsupported by rocBLAS for this type"); }
  static void check_hip(hipError_t status, const char *operation) {
    if (status != hipSuccess) throw std::runtime_error(std::string(operation) + ": " + hipGetErrorString(status));
  }
  static void check(rocblas_status status, const char *operation) {
    if (status == rocblas_status_not_implemented || status == rocblas_status_arch_mismatch)
      SKIP(std::string(operation) + " is unsupported by this device");
    if (status != rocblas_status_success)
      throw std::runtime_error(fmt::format("{} failed with status {}", operation, static_cast<int>(status)));
  }
  static void synchronize() { check_hip(hipDeviceSynchronize(), "hipDeviceSynchronize"); }
};

template <ScalarType Kind> using Blas = Rocblas<Kind>;
} // namespace unified::backend

#elif defined(UNIFIED_API_SYCL)

  #include <oneapi/mkl/blas.hpp>
  #include <oneapi/mkl/exceptions.hpp>
  #include <sycl/sycl.hpp>

  #include "backend_sycl.hpp"

namespace unified::backend {
template <ScalarType> struct OneMklNative;
template <> struct OneMklNative<ScalarType::f16> {
  using type = sycl::half;
};
template <> struct OneMklNative<ScalarType::f32> {
  using type = float;
};
template <> struct OneMklNative<ScalarType::f64> {
  using type = double;
};
template <> struct OneMklNative<ScalarType::i8> {
  using type = int8_t;
};

template <ScalarType Kind> class OneMkl {
public:
  using Scalar = typename OneMklNative<Kind>::type;

  class Buffer {
  public:
    Buffer() = default;
    Buffer(sycl::queue *queue, Scalar *data, size_t size) : queue_(queue), data_(data), size_(size) {}
    Buffer(const Buffer &) = delete;
    Buffer &operator=(const Buffer &) = delete;
    Buffer(Buffer &&other) noexcept
        : queue_(std::exchange(other.queue_, nullptr)), data_(std::exchange(other.data_, nullptr)), size_(other.size_) {}
    Buffer &operator=(Buffer &&other) noexcept {
      if (this != &other) {
        release();
        queue_ = std::exchange(other.queue_, nullptr);
        data_ = std::exchange(other.data_, nullptr);
        size_ = other.size_;
      }
      return *this;
    }
    ~Buffer() { release(); }
    Scalar *data() { return data_; }
    const Scalar *data() const { return data_; }
    size_t size() const { return size_; }

  private:
    sycl::queue *queue_{};
    Scalar *data_{};
    size_t size_{};
    void release() {
      if (data_) sycl::free(data_, *queue_);
      data_ = nullptr;
      queue_ = nullptr;
    }
  };

  OneMkl() : queue_(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  bool supports_axpy() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_scal() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_copy() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_dot() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_nrm2() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_asum() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_gemv() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_gemm() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  bool supports_gemm_strided_batched() const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device())) return false;
    if constexpr (Kind == ScalarType::i8) return false;
    return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
  }

  Buffer allocate(size_t size) {
    const auto count = std::max<size_t>(size, 1);
    (void)unified::support::checked_multiply(count, sizeof(Scalar), "oneMKL allocation");
    auto *data = sycl::malloc_device<Scalar>(count, queue_);
    if (!data) throw std::bad_alloc();
    return Buffer(&queue_, data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    require_size(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (size_t index = 0; index < host.size(); ++index)
      converted[index] = from_host(host[index]);
    if (!converted.empty()) queue_.memcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar)).wait_and_throw();
  }

  std::vector<long double> download(const Buffer &buffer, size_t size) {
    require_size(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty()) queue_.memcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar)).wait_and_throw();
    std::vector<long double> result(size);
    for (size_t index = 0; index < size; ++index)
      result[index] = to_host(converted[index]);
    return result;
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::axpy(queue_, n, from_host(alpha), x.data(), incx, y.data(), incy), "oneMKL axpy");
    else unsupported("axpy");
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::scal(queue_, n, from_host(alpha), x.data(), incx), "oneMKL scal");
    else unsupported("scal");
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::copy(queue_, n, x.data(), incx, y.data(), incy), "oneMKL copy");
    else unsupported("copy");
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64) {
      auto *result = device_result();
      try {
        wait(oneapi::mkl::blas::column_major::dot(queue_, n, x.data(), incx, y.data(), incy, result), "oneMKL dot");
        const auto value = download_result(result);
        sycl::free(result, queue_);
        return value;
      } catch (...) {
        sycl::free(result, queue_);
        throw;
      }
    } else unsupported("dot");
  }

  long double nrm2(int n, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64) {
      auto *result = device_result();
      try {
        wait(oneapi::mkl::blas::column_major::nrm2(queue_, n, x.data(), incx, result), "oneMKL nrm2");
        const auto value = download_result(result);
        sycl::free(result, queue_);
        return value;
      } catch (...) {
        sycl::free(result, queue_);
        throw;
      }
    } else unsupported("nrm2");
  }

  long double asum(int n, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f32 || Kind == ScalarType::f64) {
      auto *result = device_result();
      try {
        wait(oneapi::mkl::blas::column_major::asum(queue_, n, x.data(), incx, result), "oneMKL asum");
        const auto value = download_result(result);
        sycl::free(result, queue_);
        return value;
      } catch (...) {
        sycl::free(result, queue_);
        throw;
      }
    } else unsupported("asum");
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx, long double beta,
            Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::gemv(queue_, native(transpose), m, n, from_host(alpha), a.data(), lda, x.data(), incx,
                                                 from_host(beta), y.data(), incy),
           "oneMKL gemv");
    else unsupported("gemv");
  }

  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::gemm(queue_, native(transpose_a), native(transpose_b), m, n, k, from_host(alpha), a.data(), lda,
                                                 b.data(), ldb, from_host(beta), c.data(), ldc),
           "oneMKL gemm");
    else unsupported("gemm");
  }

  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha, Buffer &a,
                            int lda, size_t stride_a, Buffer &b, int ldb, size_t stride_b, long double beta, Buffer &c, int ldc,
                            size_t stride_c, int batch_count) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::gemm_batch(
               queue_, native(transpose_a), native(transpose_b), m, n, k, from_host(alpha), a.data(), lda, static_cast<int64_t>(stride_a),
               b.data(), ldb, static_cast<int64_t>(stride_b), from_host(beta), c.data(), ldc, static_cast<int64_t>(stride_c), batch_count),
           "oneMKL gemm_batch");
    else unsupported("gemm-strided-batched");
  }

private:
  sycl::queue queue_;

  static oneapi::mkl::transpose native(blas::Transpose transpose) {
    return transpose == blas::Transpose::none ? oneapi::mkl::transpose::nontrans : oneapi::mkl::transpose::trans;
  }

  static Scalar from_host(long double value) { return static_cast<Scalar>(value); }
  static long double to_host(const Scalar &value) {
    if constexpr (Kind == ScalarType::f16) return static_cast<long double>(static_cast<float>(value));
    else return static_cast<long double>(value);
  }
  Scalar *device_result() {
    auto *result = sycl::malloc_device<Scalar>(1, queue_);
    if (!result) throw std::bad_alloc();
    return result;
  }
  long double download_result(const Scalar *result) {
    Scalar value{};
    queue_.memcpy(&value, result, sizeof(value)).wait_and_throw();
    return to_host(value);
  }
  static void require_size(const Buffer &buffer, size_t size) {
    if (buffer.size() < size) throw std::runtime_error("buffer is smaller than transfer");
  }
  [[noreturn]] static void unsupported(const char *operation) { SKIP(std::string(operation) + " is unsupported by oneMKL for this type"); }
  template <class Event> static void wait(Event event, const char *operation) {
    try {
      event.wait_and_throw();
    } catch (const oneapi::mkl::unimplemented &) {
      SKIP(std::string(operation) + " is not implemented on this device");
    } catch (const oneapi::mkl::unsupported_device &) {
      SKIP(std::string(operation) + " is unsupported on this device");
    }
  }
};

template <ScalarType Kind> using Blas = OneMkl<Kind>;
} // namespace unified::backend

#else
  #error "A unified BLAS API must be selected"
#endif
