#ifndef UNIFIED_SUITE_ADAPTERS_ONEMKL_HPP
#define UNIFIED_SUITE_ADAPTERS_ONEMKL_HPP

#include "unified_suite/adapters/sycl_support.hpp"
#include "unified_suite/blas.hpp"

#include <oneapi/mkl/blas.hpp>
#include <oneapi/mkl/exceptions.hpp>
#include <sycl/sycl.hpp>

#include <algorithm>
#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <utility>
#include <vector>

namespace unified_suite::adapters {

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
  using type = std::int8_t;
};

template <ScalarType Kind> class OneMkl {
public:
  using Scalar = typename OneMklNative<Kind>::type;

  class Buffer {
  public:
    Buffer() = default;
    Buffer(sycl::queue *queue, Scalar *data, std::size_t size) : queue_(queue), data_(data), size_(size) {}
    Buffer(const Buffer &) = delete;
    Buffer &operator=(const Buffer &) = delete;
    Buffer(Buffer &&other) noexcept
        : queue_(std::exchange(other.queue_, nullptr)), data_(std::exchange(other.data_, nullptr)),
          size_(other.size_) {}
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
    std::size_t size() const { return size_; }

  private:
    sycl::queue *queue_{};
    Scalar *data_{};
    std::size_t size_{};
    void release() {
      if (data_)
        sycl::free(data_, *queue_);
      data_ = nullptr;
      queue_ = nullptr;
    }
  };

  OneMkl() : queue_(sycl::gpu_selector_v, sycl::property::queue::in_order{}) {}

  bool supports(blas::Operation operation) const {
    if constexpr (Kind == ScalarType::f16)
      if (!queue_.get_device().has(sycl::aspect::fp16))
        return false;
    if constexpr (Kind == ScalarType::f64)
      if (!sycl_support::supports_fp64(queue_.get_device()))
        return false;
    if constexpr (Kind == ScalarType::i8)
      return false;
    switch (operation) {
    case blas::Operation::axpy:
    case blas::Operation::scal:
    case blas::Operation::dot:
    case blas::Operation::nrm2:
    case blas::Operation::gemm:
    case blas::Operation::gemm_nt:
    case blas::Operation::gemm_tn:
    case blas::Operation::gemm_tt:
    case blas::Operation::gemm_strided_batched:
      return Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64;
    case blas::Operation::copy:
    case blas::Operation::asum:
    case blas::Operation::gemv:
    case blas::Operation::gemv_transpose:
      return Kind == ScalarType::f32 || Kind == ScalarType::f64;
    }
    return false;
  }

  Buffer allocate(std::size_t size) {
    const auto count = std::max<std::size_t>(size, 1);
    (void)unified_suite::detail::checked_multiply(count, sizeof(Scalar), "oneMKL allocation");
    auto *data = sycl::malloc_device<Scalar>(count, queue_);
    if (!data)
      throw std::bad_alloc();
    return Buffer(&queue_, data, size);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    require_size(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (std::size_t index = 0; index < host.size(); ++index)
      converted[index] = from_host(host[index]);
    if (!converted.empty())
      queue_.memcpy(buffer.data(), converted.data(), converted.size() * sizeof(Scalar)).wait_and_throw();
  }

  std::vector<long double> download(const Buffer &buffer, std::size_t size) {
    require_size(buffer, size);
    std::vector<Scalar> converted(size);
    if (!converted.empty())
      queue_.memcpy(converted.data(), buffer.data(), converted.size() * sizeof(Scalar)).wait_and_throw();
    std::vector<long double> result(size);
    for (std::size_t index = 0; index < size; ++index)
      result[index] = to_host(converted[index]);
    return result;
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::axpy(queue_, n, from_host(alpha), x.data(), incx, y.data(), incy),
           "oneMKL axpy");
    else
      unsupported("axpy");
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::scal(queue_, n, from_host(alpha), x.data(), incx), "oneMKL scal");
    else
      unsupported("scal");
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::copy(queue_, n, x.data(), incx, y.data(), incy), "oneMKL copy");
    else
      unsupported("copy");
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64) {
      auto *result = shared_result();
      try {
        wait(oneapi::mkl::blas::column_major::dot(queue_, n, x.data(), incx, y.data(), incy, result),
             "oneMKL dot");
        const auto value = to_host(*result);
        sycl::free(result, queue_);
        return value;
      } catch (...) {
        sycl::free(result, queue_);
        throw;
      }
    } else
      unsupported("dot");
  }

  long double nrm2(int n, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64) {
      auto *result = shared_result();
      try {
        wait(oneapi::mkl::blas::column_major::nrm2(queue_, n, x.data(), incx, result), "oneMKL nrm2");
        const auto value = to_host(*result);
        sycl::free(result, queue_);
        return value;
      } catch (...) {
        sycl::free(result, queue_);
        throw;
      }
    } else
      unsupported("nrm2");
  }

  long double asum(int n, Buffer &x, int incx) {
    if constexpr (Kind == ScalarType::f32 || Kind == ScalarType::f64) {
      auto *result = shared_result();
      try {
        wait(oneapi::mkl::blas::column_major::asum(queue_, n, x.data(), incx, result), "oneMKL asum");
        const auto value = to_host(*result);
        sycl::free(result, queue_);
        return value;
      } catch (...) {
        sycl::free(result, queue_);
        throw;
      }
    } else
      unsupported("asum");
  }

  void gemv(blas::Transpose transpose, int m, int n, long double alpha, Buffer &a, int lda, Buffer &x,
            int incx, long double beta, Buffer &y, int incy) {
    if constexpr (Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::gemv(queue_, native(transpose), m, n, from_host(alpha), a.data(),
                                                 lda, x.data(), incx, from_host(beta), y.data(), incy),
           "oneMKL gemv");
    else
      unsupported("gemv");
  }

  void gemm(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k, long double alpha,
            Buffer &a, int lda, Buffer &b, int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::gemm(queue_, native(transpose_a), native(transpose_b), m, n, k,
                                                 from_host(alpha), a.data(), lda, b.data(), ldb,
                                                 from_host(beta), c.data(), ldc),
           "oneMKL gemm");
    else
      unsupported("gemm");
  }

  void gemm_strided_batched(blas::Transpose transpose_a, blas::Transpose transpose_b, int m, int n, int k,
                            long double alpha, Buffer &a, int lda, std::size_t stride_a, Buffer &b, int ldb,
                            std::size_t stride_b, long double beta, Buffer &c, int ldc, std::size_t stride_c,
                            int batch_count) {
    if constexpr (Kind == ScalarType::f16 || Kind == ScalarType::f32 || Kind == ScalarType::f64)
      wait(oneapi::mkl::blas::column_major::gemm_batch(
               queue_, native(transpose_a), native(transpose_b), m, n, k, from_host(alpha), a.data(), lda,
               static_cast<std::int64_t>(stride_a), b.data(), ldb, static_cast<std::int64_t>(stride_b),
               from_host(beta), c.data(), ldc, static_cast<std::int64_t>(stride_c), batch_count),
           "oneMKL gemm_batch");
    else
      unsupported("gemm-strided-batched");
  }

private:
  sycl::queue queue_;

  static oneapi::mkl::transpose native(blas::Transpose transpose) {
    return transpose == blas::Transpose::none ? oneapi::mkl::transpose::nontrans
                                              : oneapi::mkl::transpose::trans;
  }

  static Scalar from_host(long double value) { return static_cast<Scalar>(value); }
  static long double to_host(const Scalar &value) {
    if constexpr (Kind == ScalarType::f16)
      return static_cast<long double>(static_cast<float>(value));
    else
      return static_cast<long double>(value);
  }
  Scalar *shared_result() {
    auto *result = sycl::malloc_shared<Scalar>(1, queue_);
    if (!result)
      throw std::bad_alloc();
    *result = from_host(0.0L);
    return result;
  }
  static void require_size(const Buffer &buffer, std::size_t size) {
    if (buffer.size() < size)
      throw std::runtime_error("buffer is smaller than transfer");
  }
  [[noreturn]] static void unsupported(const char *operation) {
    throw Unsupported(std::string(operation) + " is unsupported by oneMKL for this type");
  }
  template <class Event> static void wait(Event event, const char *operation) {
    try {
      event.wait_and_throw();
    } catch (const oneapi::mkl::unimplemented &) {
      throw Unsupported(std::string(operation) + " is not implemented on this device");
    } catch (const oneapi::mkl::unsupported_device &) {
      throw Unsupported(std::string(operation) + " is unsupported on this device");
    }
  }
};

} // namespace unified_suite::adapters

#endif
