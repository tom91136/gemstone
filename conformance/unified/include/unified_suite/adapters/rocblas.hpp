#ifndef UNIFIED_SUITE_ADAPTERS_ROCBLAS_HPP
#define UNIFIED_SUITE_ADAPTERS_ROCBLAS_HPP

#include "unified_suite/blas.hpp"

#include <hip/hip_runtime.h>
#include <rocblas/rocblas.h>

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

namespace unified_suite::adapters {

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
  using type = std::int8_t;
};

template <ScalarType Kind> class Rocblas {
public:
  using Scalar = typename RocblasNative<Kind>::type;

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
        (void)hipFree(data_);
      data_ = nullptr;
    }
  };

  Rocblas() { check(rocblas_create_handle(&handle_), "rocblas_create_handle"); }
  Rocblas(const Rocblas &) = delete;
  Rocblas &operator=(const Rocblas &) = delete;
  ~Rocblas() {
    if (handle_)
      (void)rocblas_destroy_handle(handle_);
  }

  bool supports(blas::Operation operation) const {
    switch (operation) {
    case blas::Operation::axpy:
    case blas::Operation::dot:
    case blas::Operation::gemm:
    case blas::Operation::gemm_nt:
    case blas::Operation::gemm_tn:
    case blas::Operation::gemm_tt:
    case blas::Operation::gemm_strided_batched:
      return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
    case blas::Operation::scal:
    case blas::Operation::copy:
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
    check_hip(hipMalloc(reinterpret_cast<void **>(&data),
                        unified_suite::detail::checked_multiply(std::max<std::size_t>(size, 1),
                                                                sizeof(Scalar), "rocBLAS allocation")),
              "hipMalloc");
    return Buffer(data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    require_size(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (std::size_t index = 0; index < host.size(); ++index)
      converted[index] = from_host(host[index]);
    if (!converted.empty())
      check_hip(hipMemcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar),
                          hipMemcpyHostToDevice),
                "hipMemcpy H2D");
  }

  std::vector<long double> download(const Buffer &buffer, std::size_t size) {
    require_size(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty())
      check_hip(hipMemcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar),
                          hipMemcpyDeviceToHost),
                "hipMemcpy D2H");
    std::vector<long double> result(size);
    for (std::size_t index = 0; index < size; ++index)
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
    } else
      unsupported("axpy");
    synchronize();
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f32) {
      const float value = static_cast<float>(alpha);
      check(rocblas_sscal(handle_, n, &value, x.data(), incx), "rocblas_sscal");
    } else if constexpr (Kind == ScalarType::f64) {
      const double value = static_cast<double>(alpha);
      check(rocblas_dscal(handle_, n, &value, x.data(), incx), "rocblas_dscal");
    } else
      unsupported("scal");
    synchronize();
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32)
      check(rocblas_scopy(handle_, n, x.data(), incx, y.data(), incy), "rocblas_scopy");
    else if constexpr (Kind == ScalarType::f64)
      check(rocblas_dcopy(handle_, n, x.data(), incx, y.data(), incy), "rocblas_dcopy");
    else
      unsupported("copy");
    synchronize();
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f16)
      check(rocblas_hdot(handle_, n, x.data(), incx, y.data(), incy, &result), "rocblas_hdot");
    else if constexpr (Kind == ScalarType::f32)
      check(rocblas_sdot(handle_, n, x.data(), incx, y.data(), incy, &result), "rocblas_sdot");
    else if constexpr (Kind == ScalarType::f64)
      check(rocblas_ddot(handle_, n, x.data(), incx, y.data(), incy, &result), "rocblas_ddot");
    else
      unsupported("dot");
    synchronize();
    return to_host(result);
  }

  long double nrm2(int n, Buffer &x, int incx) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32)
      check(rocblas_snrm2(handle_, n, x.data(), incx, &result), "rocblas_snrm2");
    else if constexpr (Kind == ScalarType::f64)
      check(rocblas_dnrm2(handle_, n, x.data(), incx, &result), "rocblas_dnrm2");
    else
      unsupported("nrm2");
    synchronize();
    return to_host(result);
  }

  long double asum(int n, Buffer &x, int incx) {
    Scalar result = from_host(0.0L);
    if constexpr (Kind == ScalarType::f32)
      check(rocblas_sasum(handle_, n, x.data(), incx, &result), "rocblas_sasum");
    else if constexpr (Kind == ScalarType::f64)
      check(rocblas_dasum(handle_, n, x.data(), incx, &result), "rocblas_dasum");
    else
      unsupported("asum");
    synchronize();
    return to_host(result);
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x,
            int incx, long double beta, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(rocblas_sgemv(handle_, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(),
                          incy),
            "rocblas_sgemv");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(rocblas_dgemv(handle_, native(transpose), m, n, &av, a.data(), lda, x.data(), incx, &bv, y.data(),
                          incy),
            "rocblas_dgemv");
    } else
      unsupported("gemv");
    synchronize();
  }

  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha,
            Buffer &a, int lda, Buffer &b, int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::f16) {
      const auto av = from_host(alpha), bv = from_host(beta);
      check(rocblas_hgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                          b.data(), ldb, &bv, c.data(), ldc),
            "rocblas_hgemm");
    } else if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(rocblas_sgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                          b.data(), ldb, &bv, c.data(), ldc),
            "rocblas_sgemm");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(rocblas_dgemm(handle_, native(transpose_a), native(transpose_b), m, n, k, &av, a.data(), lda,
                          b.data(), ldb, &bv, c.data(), ldc),
            "rocblas_dgemm");
    } else
      unsupported("gemm");
    synchronize();
  }

  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k,
                            long double alpha, Buffer &a, int lda, std::size_t stride_a, Buffer &b, int ldb,
                            std::size_t stride_b, long double beta, Buffer &c, int ldc, std::size_t stride_c,
                            int batch_count) {
    if constexpr (Kind == ScalarType::f16) {
      const auto av = from_host(alpha), bv = from_host(beta);
      check(rocblas_hgemm_strided_batched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av,
                                          a.data(), lda, static_cast<rocblas_stride>(stride_a), b.data(), ldb,
                                          static_cast<rocblas_stride>(stride_b), &bv, c.data(), ldc,
                                          static_cast<rocblas_stride>(stride_c), batch_count),
            "rocblas_hgemm_strided_batched");
    } else if constexpr (Kind == ScalarType::f32) {
      const float av = static_cast<float>(alpha), bv = static_cast<float>(beta);
      check(rocblas_sgemm_strided_batched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av,
                                          a.data(), lda, static_cast<rocblas_stride>(stride_a), b.data(), ldb,
                                          static_cast<rocblas_stride>(stride_b), &bv, c.data(), ldc,
                                          static_cast<rocblas_stride>(stride_c), batch_count),
            "rocblas_sgemm_strided_batched");
    } else if constexpr (Kind == ScalarType::f64) {
      const double av = static_cast<double>(alpha), bv = static_cast<double>(beta);
      check(rocblas_dgemm_strided_batched(handle_, native(transpose_a), native(transpose_b), m, n, k, &av,
                                          a.data(), lda, static_cast<rocblas_stride>(stride_a), b.data(), ldb,
                                          static_cast<rocblas_stride>(stride_b), &bv, c.data(), ldc,
                                          static_cast<rocblas_stride>(stride_c), batch_count),
            "rocblas_dgemm_strided_batched");
    } else
      unsupported("gemm-strided-batched");
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
      result.data = blas::detail::float_to_half_bits(static_cast<float>(value));
      return result;
#endif
    } else
      return static_cast<Scalar>(value);
  }
  static long double to_host(const Scalar &value) {
    if constexpr (Kind == ScalarType::f16) {
#if defined(ROCM_USE_FLOAT16)
      return static_cast<long double>(static_cast<float>(value));
#else
      return static_cast<long double>(blas::detail::half_bits_to_float(value.data));
#endif
    } else
      return static_cast<long double>(value);
  }
  static void require_size(const Buffer &buffer, std::size_t size) {
    if (buffer.size() < size)
      throw std::runtime_error("buffer is smaller than transfer");
  }
  [[noreturn]] static void unsupported(const char *operation) {
    throw Unsupported(std::string(operation) + " is unsupported by rocBLAS for this type");
  }
  static void check_hip(hipError_t status, const char *operation) {
    if (status != hipSuccess)
      throw std::runtime_error(std::string(operation) + ": " + hipGetErrorString(status));
  }
  static void check(rocblas_status status, const char *operation) {
    if (status == rocblas_status_not_implemented || status == rocblas_status_arch_mismatch)
      throw Unsupported(std::string(operation) + " is unsupported by this device");
    if (status != rocblas_status_success)
      throw std::runtime_error(std::string(operation) + " failed with status " +
                               std::to_string(static_cast<int>(status)));
  }
  static void synchronize() { check_hip(hipDeviceSynchronize(), "hipDeviceSynchronize"); }
};

} // namespace unified_suite::adapters

#endif
