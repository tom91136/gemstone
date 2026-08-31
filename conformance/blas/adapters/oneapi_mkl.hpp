#ifndef GEMSTONE_CONFORMANCE_ONEAPI_MKL_ADAPTER_HPP
#define GEMSTONE_CONFORMANCE_ONEAPI_MKL_ADAPTER_HPP

#include "gemstone/conformance/blas_smoke.hpp"
#include <sycl/sycl.hpp>
#include <oneapi/mkl/blas.hpp>
#include <oneapi/mkl/exceptions.hpp>

#include <cstddef>
#include <cstdint>
#include <stdexcept>
#include <string>
#include <vector>

namespace gemstone::conformance::blas {

template <NumericType Kind> struct OneapiNative;
template <> struct OneapiNative<NumericType::half> { using type = sycl::half; };
template <> struct OneapiNative<NumericType::f32>  { using type = float; };
template <> struct OneapiNative<NumericType::f64>  { using type = double; };
template <> struct OneapiNative<NumericType::i8>   { using type = int8_t; };

template <NumericType Kind> class OneapiMklBackend {
public:
  using Scalar = typename OneapiNative<Kind>::type;

  struct Buffer {
    sycl::queue *queue = nullptr;
    Scalar *data = nullptr;
    std::size_t size = 0;

    Buffer() = default;
    Buffer(sycl::queue *owner, Scalar *value, std::size_t elements)
        : queue(owner), data(value), size(elements) {}
    Buffer(const Buffer &) = delete;
    Buffer &operator=(const Buffer &) = delete;
    Buffer(Buffer &&other) noexcept
        : queue(other.queue), data(other.data), size(other.size) {
      other.queue = nullptr;
      other.data = nullptr;
      other.size = 0;
    }
    Buffer &operator=(Buffer &&other) noexcept {
      if (this != &other) {
        release();
        queue = other.queue;
        data = other.data;
        size = other.size;
        other.queue = nullptr;
        other.data = nullptr;
        other.size = 0;
      }
      return *this;
    }
    ~Buffer() { release(); }

  private:
    void release() {
      if (data != nullptr) {
        sycl::free(data, *queue);
        data = nullptr;
        queue = nullptr;
        size = 0;
      }
    }
  };

  bool supports(Operation operation) const {
    if constexpr (Kind == NumericType::half) {
      if (!queue_.get_device().has(sycl::aspect::fp16)) return false;
    } else if constexpr (Kind == NumericType::f64) {
      if (!queue_.get_device().has(sycl::aspect::fp64)) return false;
    } else if constexpr (Kind == NumericType::i8) {
      return false;
    }
    switch (operation) {
    case Operation::axpy: return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::scal: return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::copy: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::dot:  return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::nrm2: return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::asum: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::gemv: return Kind == NumericType::f32 || Kind == NumericType::f64;
    case Operation::gemm: return Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64;
    }
    return false;
  }

  OneapiMklBackend() : queue_(sycl::gpu_selector_v) {}

  Buffer allocate(std::size_t elements) {
    return Buffer(&queue_, sycl::malloc_device<Scalar>(elements, queue_), elements);
  }

  void upload(Buffer &buffer, const std::vector<long double> &host) {
    requireSize(buffer, host.size());
    std::vector<Scalar> converted(host.size());
    for (std::size_t i = 0; i < host.size(); ++i) converted[i] = fromHost(host[i]);
    queue_.memcpy(buffer.data, converted.data(), converted.size() * sizeof(Scalar)).wait();
  }

  std::vector<long double> download(const Buffer &buffer, std::size_t elements) {
    requireSize(buffer, elements);
    std::vector<Scalar> converted(elements);
    queue_.memcpy(converted.data(), buffer.data, elements * sizeof(Scalar)).wait();
    std::vector<long double> host(elements);
    for (std::size_t i = 0; i < elements; ++i) host[i] = toHost(converted[i]);
    return host;
  }

  void axpy(int n, long double alpha, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64)
      wait_for_blas(oneapi::mkl::blas::column_major::axpy(
          queue_, n, fromHost(alpha), x.data, incx, y.data, incy), "axpy");
    else
      unsupported("axpy");
  }

  void scal(int n, long double alpha, Buffer &x, int incx) {
    if constexpr (Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64)
      wait_for_blas(oneapi::mkl::blas::column_major::scal(
          queue_, n, fromHost(alpha), x.data, incx), "scal");
    else
      unsupported("scal");
  }

  void copy(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::f32 || Kind == NumericType::f64)
      wait_for_blas(oneapi::mkl::blas::column_major::copy(
          queue_, n, x.data, incx, y.data, incy), "copy");
    else
      unsupported("copy");
  }

  long double dot(int n, Buffer &x, int incx, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64) {
      Scalar *result = sycl::malloc_shared<Scalar>(1, queue_);
      wait_for_blas(oneapi::mkl::blas::column_major::dot(
          queue_, n, x.data, incx, y.data, incy, result), "dot");
      const long double value = toHost(*result);
      sycl::free(result, queue_);
      return value;
    } else {
      unsupported("dot");
      return 0.0L;
    }
  }

  long double nrm2(int n, Buffer &x, int incx) {
    if constexpr (Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64) {
      Scalar *result = sycl::malloc_shared<Scalar>(1, queue_);
      wait_for_blas(oneapi::mkl::blas::column_major::nrm2(
          queue_, n, x.data, incx, result), "nrm2");
      const long double value = toHost(*result);
      sycl::free(result, queue_);
      return value;
    } else {
      unsupported("nrm2");
      return 0.0L;
    }
  }

  long double asum(int n, Buffer &x, int incx) {
    if constexpr (Kind == NumericType::f32 || Kind == NumericType::f64) {
      Scalar *result = sycl::malloc_shared<Scalar>(1, queue_);
      wait_for_blas(oneapi::mkl::blas::column_major::asum(
          queue_, n, x.data, incx, result), "asum");
      const long double value = toHost(*result);
      sycl::free(result, queue_);
      return value;
    } else {
      unsupported("asum");
      return 0.0L;
    }
  }

  void gemv(int m, int n, long double alpha, Buffer &a, int lda, Buffer &x, int incx,
            long double beta, Buffer &y, int incy) {
    if constexpr (Kind == NumericType::f32 || Kind == NumericType::f64)
      wait_for_blas(oneapi::mkl::blas::column_major::gemv(
          queue_, oneapi::mkl::transpose::nontrans, m, n, fromHost(alpha), a.data, lda,
          x.data, incx, fromHost(beta), y.data, incy), "gemv");
    else
      unsupported("gemv");
  }

  void gemm(int m, int n, int k, long double alpha, Buffer &a, int lda, Buffer &b,
            int ldb, long double beta, Buffer &c, int ldc) {
    if constexpr (Kind == NumericType::half || Kind == NumericType::f32 || Kind == NumericType::f64) {
      wait_for_blas(oneapi::mkl::blas::column_major::gemm(
          queue_, oneapi::mkl::transpose::nontrans, oneapi::mkl::transpose::nontrans,
          m, n, k, fromHost(alpha), a.data, lda, b.data, ldb, fromHost(beta), c.data, ldc)
          , "gemm");
    } else {
      unsupported("gemm");
    }
  }

private:
  sycl::queue queue_;

  template <class Event>
  static void wait_for_blas(Event event, const char *operation) {
    try {
      event.wait();
    } catch (const oneapi::mkl::unimplemented &) {
      throw detail::UnsupportedOperation(std::string(operation) + " is not supported by oneMKL on this device");
    } catch (const oneapi::mkl::unsupported_device &) {
      throw detail::UnsupportedOperation(std::string(operation) + " is not supported by this device");
    }
  }

  static Scalar fromHost(long double value) {
    if constexpr (Kind == NumericType::half)
      return Scalar(static_cast<float>(value));
    else
      return static_cast<Scalar>(value);
  }

  static long double toHost(const Scalar &value) {
    if constexpr (Kind == NumericType::half)
      return static_cast<long double>(static_cast<float>(value));
    else
      return static_cast<long double>(value);
  }

  static void requireSize(const Buffer &buffer, std::size_t elements) {
    if (buffer.size < elements) throw std::runtime_error("buffer is smaller than the requested transfer");
  }

  static void unsupported(const char *operation) {
    throw detail::UnsupportedOperation(std::string(operation) + " is not supported for this numeric type");
  }
};

} // namespace gemstone::conformance::blas

#endif
