#pragma once

#include <string_view>

#include <aspartame/vector.hpp>

#include "blas_cases.hpp"
#include "test_support.hpp"
#include <fmt/format.h>

#ifdef UNIFIED_API_CUDA

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
template <> struct CublasNative<ScalarType::F16> {
  using type = __half;
};
template <> struct CublasNative<ScalarType::F32> {
  using type = float;
};
template <> struct CublasNative<ScalarType::F64> {
  using type = double;
};

template <ScalarType Kind> class Cublas {
public:
  using Scalar = typename CublasNative<Kind>::type;
  using Buffer = blas_detail::DeviceBuffer<Scalar, CudaBufferMemory>;

  Cublas() {
    check(cublasCreate(&handle), "cublasCreate");
    try {
      check(cublasSetPointerMode(handle, CUBLAS_POINTER_MODE_HOST), "cublasSetPointerMode");
    } catch (...) {
      (void)cublasDestroy(handle);
      handle = nullptr;
      throw;
    }
  }
  Cublas(const Cublas &) = delete;
  Cublas &operator=(const Cublas &) = delete;
  ~Cublas() {
    if (handle) (void)cublasDestroy(handle);
  }

  [[nodiscard]] bool supports(std::string_view operation) const {
    if constexpr (Kind == ScalarType::F16) {
      int device = 0;
      cudaDeviceProp properties{};
      return (operation == "gemm" || operation == "gemm_strided_batched") && cudaGetDevice(&device) == cudaSuccess
             && cudaGetDeviceProperties(&properties, device) == cudaSuccess
             && (properties.major > 5 || (properties.major == 5 && properties.minor >= 3));
    }
    return true;
  }

  Buffer allocate(size_t size) {
    Scalar *data = nullptr;
    checkCuda(cudaMalloc(reinterpret_cast<void **>(&data),
                         unified::support::checkedMultiply(std::max<size_t>(size, 1), sizeof(Scalar), "cuBLAS allocation")),
              "cudaMalloc");
    return Buffer(data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    using namespace aspartame;
    blas_detail::requireSize(buffer, host.size());
    const auto converted = host ^ map([](long double value) { return fromHost(value); });
    if (!converted.empty())
      checkCuda(cudaMemcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar), cudaMemcpyHostToDevice), "cudaMemcpy H2D");
  }

  std::vector<long double> download(const Buffer &buffer, size_t size) {
    using namespace aspartame;
    blas_detail::requireSize(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty())
      checkCuda(cudaMemcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar), cudaMemcpyDeviceToHost), "cudaMemcpy D2H");
    return converted ^ map([](const Scalar &value) { return toHost(value); });
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F32) {
      const float value = static_cast<float>(alpha);
      check(cublasSaxpy(handle, n, &value, x.data(), incx, y.data(), incy), "cublasSaxpy");
    } else if constexpr (Kind == ScalarType::F64) {
      const double value = static_cast<double>(alpha);
      check(cublasDaxpy(handle, n, &value, x.data(), incx, y.data(), incy), "cublasDaxpy");
    } else {
      unsupported("axpy");
    }
    synchronise();
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::F32) {
      const float value = static_cast<float>(alpha);
      check(cublasSscal(handle, n, &value, x.data(), incx), "cublasSscal");
    } else if constexpr (Kind == ScalarType::F64) {
      const double value = static_cast<double>(alpha);
      check(cublasDscal(handle, n, &value, x.data(), incx), "cublasDscal");
    } else {
      unsupported("scal");
    }
    synchronise();
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F32) check(cublasScopy(handle, n, x.data(), incx, y.data(), incy), "cublasScopy");
    else if constexpr (Kind == ScalarType::F64) check(cublasDcopy(handle, n, x.data(), incx, y.data(), incy), "cublasDcopy");
    else unsupported("copy");
    synchronise();
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == ScalarType::F32) check(cublasSdot(handle, n, x.data(), incx, y.data(), incy, &result), "cublasSdot");
    else if constexpr (Kind == ScalarType::F64) check(cublasDdot(handle, n, x.data(), incx, y.data(), incy, &result), "cublasDdot");
    else unsupported("dot");
    synchronise();
    return toHost(result);
  }

  long double nrm2(int n, Buffer &x, int incx) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == ScalarType::F32) check(cublasSnrm2(handle, n, x.data(), incx, &result), "cublasSnrm2");
    else if constexpr (Kind == ScalarType::F64) check(cublasDnrm2(handle, n, x.data(), incx, &result), "cublasDnrm2");
    else unsupported("nrm2");
    synchronise();
    return toHost(result);
  }

  long double asum(int n, Buffer &x, int incx) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == ScalarType::F32) check(cublasSasum(handle, n, x.data(), incx, &result), "cublasSasum");
    else if constexpr (Kind == ScalarType::F64) check(cublasDasum(handle, n, x.data(), incx, &result), "cublasDasum");
    else unsupported("asum");
    synchronise();
    return toHost(result);
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx, long double beta,
            Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F32) {
      const float av = static_cast<float>(alpha);
      const float bv = static_cast<float>(beta);
      check(cublasSgemv(handle, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(), incy), "cublasSgemv");
    } else if constexpr (Kind == ScalarType::F64) {
      const double av = static_cast<double>(alpha);
      const double bv = static_cast<double>(beta);
      check(cublasDgemv(handle, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(), incy), "cublasDgemv");
    } else {
      unsupported("gemv");
    }
    synchronise();
  }

  void gemm(blas::Transpose transposeA, blas::Transpose transposeB, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::F16) {
      const auto av = fromHost(alpha);
      const auto bv = fromHost(beta);
      check(cublasHgemm(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "cublasHgemm");
    } else if constexpr (Kind == ScalarType::F32) {
      const float av = static_cast<float>(alpha);
      const float bv = static_cast<float>(beta);
      check(cublasSgemm(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "cublasSgemm");
    } else if constexpr (Kind == ScalarType::F64) {
      const double av = static_cast<double>(alpha);
      const double bv = static_cast<double>(beta);
      check(cublasDgemm(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "cublasDgemm");
    } else {
      unsupported("gemm");
    }
    synchronise();
  }

  void gemmStridedBatched(blas::Transpose transposeA, blas::Transpose transposeB, int m, int n, int k, long double alpha, Buffer &a,
                          int lda, size_t strideA, Buffer &b, int ldb, size_t strideB, long double beta, Buffer &c, int ldc, size_t strideC,
                          int batchCount) {
    const auto sa = static_cast<int64_t>(strideA);
    const auto sb = static_cast<int64_t>(strideB);
    const auto sc = static_cast<int64_t>(strideC);
    if constexpr (Kind == ScalarType::F16) {
      const auto av = fromHost(alpha);
      const auto bv = fromHost(beta);
      check(cublasHgemmStridedBatched(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, sa, b.data(), ldb, sb,
                                      &bv, c.data(), ldc, sc, batchCount),
            "cublasHgemmStridedBatched");
    } else if constexpr (Kind == ScalarType::F32) {
      const float av = static_cast<float>(alpha);
      const float bv = static_cast<float>(beta);
      check(cublasSgemmStridedBatched(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, sa, b.data(), ldb, sb,
                                      &bv, c.data(), ldc, sc, batchCount),
            "cublasSgemmStridedBatched");
    } else if constexpr (Kind == ScalarType::F64) {
      const double av = static_cast<double>(alpha);
      const double bv = static_cast<double>(beta);
      check(cublasDgemmStridedBatched(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, sa, b.data(), ldb, sb,
                                      &bv, c.data(), ldc, sc, batchCount),
            "cublasDgemmStridedBatched");
    } else {
      unsupported("gemm-strided-batched");
    }
    synchronise();
  }

private:
  cublasHandle_t handle{};

  static cublasOperation_t native(blas::Transpose transpose) { return transpose == blas::Transpose::None ? CUBLAS_OP_N : CUBLAS_OP_T; }

  static Scalar fromHost(long double value) {
    if constexpr (Kind == ScalarType::F16) return __float2half(static_cast<float>(value));
    else return static_cast<Scalar>(value);
  }
  static long double toHost(Scalar value) {
    if constexpr (Kind == ScalarType::F16) return static_cast<long double>(__half2float(value));
    else return static_cast<long double>(value);
  }
  [[noreturn]] static void unsupported(const char *operation) { SKIP(std::string(operation) + " is unsupported by cuBLAS for this type"); }
  static void check(cublasStatus_t status, const char *operation) {
    if (status == CUBLAS_STATUS_NOT_SUPPORTED || status == CUBLAS_STATUS_ARCH_MISMATCH)
      SKIP(std::string(operation) + " is unsupported by this device");
    if (status != CUBLAS_STATUS_SUCCESS)
      throw std::runtime_error(fmt::format("{} failed with status {}", operation, static_cast<int>(status)));
  }
  static void checkCuda(cudaError_t status, const char *operation) {
    if (status != cudaSuccess) throw std::runtime_error(std::string(operation) + ": " + cudaGetErrorString(status));
  }
  static void synchronise() { checkCuda(cudaDeviceSynchronize(), "cudaDeviceSynchronize"); }
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
template <> struct RocblasNative<ScalarType::F16> {
  using type = rocblas_half;
};
template <> struct RocblasNative<ScalarType::F32> {
  using type = float;
};
template <> struct RocblasNative<ScalarType::F64> {
  using type = double;
};

template <ScalarType Kind> class Rocblas {
public:
  using Scalar = RocblasNative<Kind>::type;
  using Buffer = blas_detail::DeviceBuffer<Scalar, HipBufferMemory>;

  Rocblas() { check(rocblas_create_handle(&handle), "rocblas_create_handle"); }
  Rocblas(const Rocblas &) = delete;
  Rocblas &operator=(const Rocblas &) = delete;
  ~Rocblas() {
    if (handle) (void)rocblas_destroy_handle(handle);
  }

  [[nodiscard]] bool supports(std::string_view operation) const {
    if (operation == "gemm" || operation == "gemm_strided_batched") {
  #ifdef CONFORMANCE_HIP_HAS_TENSILE
      return true;
  #else
      return false;
  #endif
    }
    return Kind != ScalarType::F16 || operation == "axpy" || operation == "dot";
  }

  Buffer allocate(size_t size) {
    Scalar *data = nullptr;
    checkHip(hipMalloc(reinterpret_cast<void **>(&data),
                       unified::support::checkedMultiply(std::max<size_t>(size, 1), sizeof(Scalar), "rocBLAS allocation")),
             "hipMalloc");
    return Buffer(data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    using namespace aspartame;
    blas_detail::requireSize(buffer, host.size());
    const auto converted = host ^ map([](long double value) { return fromHost(value); });
    if (!converted.empty())
      checkHip(hipMemcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar), hipMemcpyHostToDevice), "hipMemcpy H2D");
  }

  std::vector<long double> download(const Buffer &buffer, size_t size) {
    using namespace aspartame;
    blas_detail::requireSize(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty())
      checkHip(hipMemcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar), hipMemcpyDeviceToHost), "hipMemcpy D2H");
    return converted ^ map([](const Scalar &value) { return toHost(value); });
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F16) {
      const auto value = fromHost(alpha);
      check(rocblas_haxpy(handle, n, &value, x.data(), incx, y.data(), incy), "rocblas_haxpy");
    } else if constexpr (Kind == ScalarType::F32) {
      const float value = static_cast<float>(alpha);
      check(rocblas_saxpy(handle, n, &value, x.data(), incx, y.data(), incy), "rocblas_saxpy");
    } else if constexpr (Kind == ScalarType::F64) {
      const double value = static_cast<double>(alpha);
      check(rocblas_daxpy(handle, n, &value, x.data(), incx, y.data(), incy), "rocblas_daxpy");
    } else {
      unsupported("axpy");
    }
    synchronise();
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::F32) {
      const float value = static_cast<float>(alpha);
      check(rocblas_sscal(handle, n, &value, x.data(), incx), "rocblas_sscal");
    } else if constexpr (Kind == ScalarType::F64) {
      const double value = static_cast<double>(alpha);
      check(rocblas_dscal(handle, n, &value, x.data(), incx), "rocblas_dscal");
    } else {
      unsupported("scal");
    }
    synchronise();
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F32) check(rocblas_scopy(handle, n, x.data(), incx, y.data(), incy), "rocblas_scopy");
    else if constexpr (Kind == ScalarType::F64) check(rocblas_dcopy(handle, n, x.data(), incx, y.data(), incy), "rocblas_dcopy");
    else unsupported("copy");
    synchronise();
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == ScalarType::F16) check(rocblas_hdot(handle, n, x.data(), incx, y.data(), incy, &result), "rocblas_hdot");
    else if constexpr (Kind == ScalarType::F32) check(rocblas_sdot(handle, n, x.data(), incx, y.data(), incy, &result), "rocblas_sdot");
    else if constexpr (Kind == ScalarType::F64) check(rocblas_ddot(handle, n, x.data(), incx, y.data(), incy, &result), "rocblas_ddot");
    else unsupported("dot");
    synchronise();
    return toHost(result);
  }

  long double nrm2(int n, Buffer &x, int incx) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == ScalarType::F32) check(rocblas_snrm2(handle, n, x.data(), incx, &result), "rocblas_snrm2");
    else if constexpr (Kind == ScalarType::F64) check(rocblas_dnrm2(handle, n, x.data(), incx, &result), "rocblas_dnrm2");
    else unsupported("nrm2");
    synchronise();
    return toHost(result);
  }

  long double asum(int n, Buffer &x, int incx) {
    Scalar result = fromHost(0.0L);
    if constexpr (Kind == ScalarType::F32) check(rocblas_sasum(handle, n, x.data(), incx, &result), "rocblas_sasum");
    else if constexpr (Kind == ScalarType::F64) check(rocblas_dasum(handle, n, x.data(), incx, &result), "rocblas_dasum");
    else unsupported("asum");
    synchronise();
    return toHost(result);
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx, long double beta,
            Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F32) {
      const float av = static_cast<float>(alpha);
      const float bv = static_cast<float>(beta);
      check(rocblas_sgemv(handle, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(), incy), "rocblas_sgemv");
    } else if constexpr (Kind == ScalarType::F64) {
      const double av = static_cast<double>(alpha);
      const double bv = static_cast<double>(beta);
      check(rocblas_dgemv(handle, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(), incy), "rocblas_dgemv");
    } else {
      unsupported("gemv");
    }
    synchronise();
  }

  void gemm(blas::Transpose transposeA, blas::Transpose transposeB, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::F16) {
      const auto av = fromHost(alpha);
      const auto bv = fromHost(beta);
      check(rocblas_hgemm(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "rocblas_hgemm");
    } else if constexpr (Kind == ScalarType::F32) {
      const float av = static_cast<float>(alpha);
      const float bv = static_cast<float>(beta);
      check(rocblas_sgemm(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "rocblas_sgemm");
    } else if constexpr (Kind == ScalarType::F64) {
      const double av = static_cast<double>(alpha);
      const double bv = static_cast<double>(beta);
      check(rocblas_dgemm(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda, b.data(), ldb, &bv, c.data(), ldc),
            "rocblas_dgemm");
    } else {
      unsupported("gemm");
    }
    synchronise();
  }

  void gemmStridedBatched(blas::Transpose transposeA, blas::Transpose transposeB, int m, int n, int k, long double alpha, Buffer &a,
                          int lda, size_t strideA, Buffer &b, int ldb, size_t strideB, long double beta, Buffer &c, int ldc, size_t strideC,
                          int batchCount) {
    if constexpr (Kind == ScalarType::F16) {
      const auto av = fromHost(alpha);
      const auto bv = fromHost(beta);
      check(rocblas_hgemm_strided_batched(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda,
                                          static_cast<rocblas_stride>(strideA), b.data(), ldb, static_cast<rocblas_stride>(strideB), &bv,
                                          c.data(), ldc, static_cast<rocblas_stride>(strideC), batchCount),
            "rocblas_hgemm_strided_batched");
    } else if constexpr (Kind == ScalarType::F32) {
      const float av = static_cast<float>(alpha);
      const float bv = static_cast<float>(beta);
      check(rocblas_sgemm_strided_batched(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda,
                                          static_cast<rocblas_stride>(strideA), b.data(), ldb, static_cast<rocblas_stride>(strideB), &bv,
                                          c.data(), ldc, static_cast<rocblas_stride>(strideC), batchCount),
            "rocblas_sgemm_strided_batched");
    } else if constexpr (Kind == ScalarType::F64) {
      const double av = static_cast<double>(alpha);
      const double bv = static_cast<double>(beta);
      check(rocblas_dgemm_strided_batched(handle, native(transposeA), native(transposeB), m, n, k, &av, a.data(), lda,
                                          static_cast<rocblas_stride>(strideA), b.data(), ldb, static_cast<rocblas_stride>(strideB), &bv,
                                          c.data(), ldc, static_cast<rocblas_stride>(strideC), batchCount),
            "rocblas_dgemm_strided_batched");
    } else {
      unsupported("gemm-strided-batched");
    }
    synchronise();
  }

private:
  rocblas_handle handle{};

  static rocblas_operation native(blas::Transpose transpose) {
    return transpose == blas::Transpose::None ? rocblas_operation_none : rocblas_operation_transpose;
  }

  static Scalar fromHost(long double value) {
    if constexpr (Kind == ScalarType::F16) {
  #ifdef ROCM_USE_FLOAT16
      return static_cast<Scalar>(static_cast<float>(value));
  #else
      Scalar result{};
      result.data = blas::floatToHalfBits(static_cast<float>(value));
      return result;
  #endif
    } else {
      return static_cast<Scalar>(value);
    }
  }
  static long double toHost(const Scalar &value) {
    if constexpr (Kind == ScalarType::F16) {
  #ifdef ROCM_USE_FLOAT16
      return static_cast<long double>(static_cast<float>(value));
  #else
      return static_cast<long double>(blas::halfBitsToFloat(value.data));
  #endif
    } else {
      return static_cast<long double>(value);
    }
  }
  [[noreturn]] static void unsupported(const char *operation) { SKIP(std::string(operation) + " is unsupported by rocBLAS for this type"); }
  static void checkHip(hipError_t status, const char *operation) {
    if (status != hipSuccess) throw std::runtime_error(std::string(operation) + ": " + hipGetErrorString(status));
  }
  static void check(rocblas_status status, const char *operation) {
    if (status == rocblas_status_not_implemented || status == rocblas_status_arch_mismatch)
      SKIP(std::string(operation) + " is unsupported by this device");
    if (status != rocblas_status_success)
      throw std::runtime_error(fmt::format("{} failed with status {}", operation, static_cast<int>(status)));
  }
  static void synchronise() { checkHip(hipDeviceSynchronize(), "hipDeviceSynchronize"); }
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
template <> struct OneMklNative<ScalarType::F16> {
  using type = sycl::half;
};
template <> struct OneMklNative<ScalarType::F32> {
  using type = float;
};
template <> struct OneMklNative<ScalarType::F64> {
  using type = double;
};

template <ScalarType Kind> class OneMkl {
public:
  using Scalar = OneMklNative<Kind>::type;

  class Buffer {
  public:
    Buffer() = default;
    Buffer(sycl::queue *queue, Scalar *data, size_t size) : queue(queue), storage(data), length(size) {}
    Buffer(const Buffer &) = delete;
    Buffer &operator=(const Buffer &) = delete;
    Buffer(Buffer &&other) noexcept
        : queue(std::exchange(other.queue, nullptr)), storage(std::exchange(other.storage, nullptr)), length(other.length) {}
    Buffer &operator=(Buffer &&other) noexcept {
      if (this != &other) {
        release();
        queue = std::exchange(other.queue, nullptr);
        storage = std::exchange(other.storage, nullptr);
        length = other.length;
      }
      return *this;
    }
    ~Buffer() { release(); }
    Scalar *data() { return storage; }
    [[nodiscard]] const Scalar *data() const { return storage; }
    [[nodiscard]] size_t size() const { return length; }

  private:
    sycl::queue *queue{};
    Scalar *storage{};
    size_t length{};
    void release() {
      if (storage) sycl::free(storage, *queue);
      storage = nullptr;
      queue = nullptr;
    }
  };

  OneMkl() : queue(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  [[nodiscard]] bool supports(std::string_view operation) const {
    if constexpr (Kind == ScalarType::F16)
      return queue.get_device().has(sycl::aspect::fp16) && operation != "copy" && operation != "asum" && operation != "gemv";
    if constexpr (Kind == ScalarType::F64) return sycl_support::supportsFp64(queue.get_device());
    return true;
  }

  Buffer allocate(size_t size) {
    const auto count = std::max<size_t>(size, 1);
    (void)unified::support::checkedMultiply(count, sizeof(Scalar), "oneMKL allocation");
    auto *data = sycl::malloc_device<Scalar>(count, queue);
    if (!data) throw std::bad_alloc();
    return Buffer(&queue, data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    using namespace aspartame;
    blas_detail::requireSize(buffer, host.size());
    const auto converted = host ^ map([](long double value) { return fromHost(value); });
    if (!converted.empty()) queue.memcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar)).wait_and_throw();
  }

  std::vector<long double> download(const Buffer &buffer, size_t size) {
    using namespace aspartame;
    blas_detail::requireSize(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty()) queue.memcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar)).wait_and_throw();
    return converted ^ map([](const Scalar &value) { return toHost(value); });
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F16 || Kind == ScalarType::F32 || Kind == ScalarType::F64)
      wait(oneapi::mkl::blas::column_major::axpy(queue, n, fromHost(alpha), x.data(), incx, y.data(), incy), "oneMKL axpy");
    else unsupported("axpy");
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::F16 || Kind == ScalarType::F32 || Kind == ScalarType::F64)
      wait(oneapi::mkl::blas::column_major::scal(queue, n, fromHost(alpha), x.data(), incx), "oneMKL scal");
    else unsupported("scal");
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F32 || Kind == ScalarType::F64)
      wait(oneapi::mkl::blas::column_major::copy(queue, n, x.data(), incx, y.data(), incy), "oneMKL copy");
    else unsupported("copy");
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F16 || Kind == ScalarType::F32 || Kind == ScalarType::F64) {
      return scalarResult(
          [&](Scalar *result) { return oneapi::mkl::blas::column_major::dot(queue, n, x.data(), incx, y.data(), incy, result); },
          "oneMKL dot");
    } else {
      unsupported("dot");
    }
  }

  long double nrm2(int n, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::F16 || Kind == ScalarType::F32 || Kind == ScalarType::F64) {
      return scalarResult([&](Scalar *result) { return oneapi::mkl::blas::column_major::nrm2(queue, n, x.data(), incx, result); },
                          "oneMKL nrm2");
    } else {
      unsupported("nrm2");
    }
  }

  long double asum(int n, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::F32 || Kind == ScalarType::F64) {
      return scalarResult([&](Scalar *result) { return oneapi::mkl::blas::column_major::asum(queue, n, x.data(), incx, result); },
                          "oneMKL asum");
    } else {
      unsupported("asum");
    }
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx, long double beta,
            Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::F32 || Kind == ScalarType::F64)
      wait(oneapi::mkl::blas::column_major::gemv(queue, native(transpose), m, n, fromHost(alpha), a.data(), lda, x.data(), incx,
                                                 fromHost(beta), y.data(), incy),
           "oneMKL gemv");
    else unsupported("gemv");
  }

  void gemm(blas::Transpose transposeA, blas::Transpose transposeB, int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::F16 || Kind == ScalarType::F32 || Kind == ScalarType::F64)
      wait(oneapi::mkl::blas::column_major::gemm(queue, native(transposeA), native(transposeB), m, n, k, fromHost(alpha), a.data(), lda,
                                                 b.data(), ldb, fromHost(beta), c.data(), ldc),
           "oneMKL gemm");
    else unsupported("gemm");
  }

  void gemmStridedBatched(blas::Transpose transposeA, blas::Transpose transposeB, int m, int n, int k, long double alpha, Buffer &a,
                          int lda, size_t strideA, Buffer &b, int ldb, size_t strideB, long double beta, Buffer &c, int ldc, size_t strideC,
                          int batchCount) {
    if constexpr (Kind == ScalarType::F16 || Kind == ScalarType::F32 || Kind == ScalarType::F64)
      wait(oneapi::mkl::blas::column_major::gemm_batch(queue, native(transposeA), native(transposeB), m, n, k, fromHost(alpha), a.data(),
                                                       lda, static_cast<int64_t>(strideA), b.data(), ldb, static_cast<int64_t>(strideB),
                                                       fromHost(beta), c.data(), ldc, static_cast<int64_t>(strideC), batchCount),
           "oneMKL gemm_batch");
    else unsupported("gemm-strided-batched");
  }

private:
  sycl::queue queue;

  static oneapi::mkl::transpose native(blas::Transpose transpose) {
    return transpose == blas::Transpose::None ? oneapi::mkl::transpose::nontrans : oneapi::mkl::transpose::trans;
  }

  static Scalar fromHost(long double value) { return static_cast<Scalar>(value); }
  static long double toHost(const Scalar &value) {
    if constexpr (Kind == ScalarType::F16) return static_cast<long double>(static_cast<float>(value));
    else return static_cast<long double>(value);
  }
  template <class Launch> long double scalarResult(Launch launch, const char *operation) {
    auto *result = sycl::malloc_device<Scalar>(1, queue);
    if (!result) throw std::bad_alloc();
    try {
      wait(launch(result), operation);
      Scalar value{};
      queue.memcpy(&value, result, sizeof(value)).wait_and_throw();
      sycl::free(result, queue);
      return toHost(value);
    } catch (...) {
      sycl::free(result, queue);
      throw;
    }
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
