#include <cmath>
#include <cstring>
#include <memory>
#include <new>

#include <stdint.h>

#include "gemstone_cuda_constants.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/export.h>

/*
 * cuBLAS is a host-side library.  Its CUDA 13 binary normally discovers a
 * hardware-specific dispatch catalogue and refuses to initialise when the
 * driver cannot load its private cubins.  Gemstone exposes this small,
 * versioned compatibility surface from the CUDA provider instead.  It keeps
 * the vendor ABI and device allocations intact, but executes the BLAS
 * operations over the ordinary driver copy RPCs and performs the arithmetic
 * in the provider process.
 */

extern "C" int32_t cuInit(unsigned int flags);
extern "C" int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size);
extern "C" int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size);

namespace {

using namespace gemstone::cuda::api;
using gemstone::readNative;

using Status = int32_t;
struct CublasHandleState {
  int32_t pointerMode = 0;
  void *stream = nullptr;
};
using CublasHandle = CublasHandleState *;

Status copyIn(void *host, const void *pointer, size_t size) {
  if (!host || !pointer) return CUBLAS_STATUS_INVALID_VALUE;
  return cuMemcpyDtoH_v2(host, static_cast<uint64_t>(reinterpret_cast<uintptr_t>(pointer)), size);
}

Status copyOut(void *pointer, const void *host, size_t size) {
  if (!pointer || !host) return CUBLAS_STATUS_INVALID_VALUE;
  return cuMemcpyHtoD_v2(static_cast<uint64_t>(reinterpret_cast<uintptr_t>(pointer)), host, size);
}

/* Scalar location follows the handle mode, never the pointer's numeric range. */
Status scalarIn(CublasHandle handle, void *host, const void *pointer, size_t size) {
  if (!pointer) return CUBLAS_STATUS_INVALID_VALUE;
  if (handle->pointerMode == 1) return cuMemcpyDtoH_v2(host, static_cast<uint64_t>(reinterpret_cast<uintptr_t>(pointer)), size);
  memcpy(host, pointer, size);
  return CUBLAS_STATUS_SUCCESS;
}

Status scalarOut(CublasHandle handle, void *pointer, const void *host, size_t size) {
  if (!pointer) return CUBLAS_STATUS_INVALID_VALUE;
  if (handle->pointerMode == 1) return cuMemcpyHtoD_v2(static_cast<uint64_t>(reinterpret_cast<uintptr_t>(pointer)), host, size);
  memcpy(pointer, host, size);
  return CUBLAS_STATUS_SUCCESS;
}

size_t span(int count, int increment) {
  if (count <= 0) return 0;
  const size_t step = static_cast<size_t>(increment < 0 ? -increment : increment);
  return 1 + (static_cast<size_t>(count) - 1) * step;
}

size_t indexOf(int index, int count, int increment) {
  if (increment < 0) return (static_cast<size_t>(count) - 1 - static_cast<size_t>(index)) * static_cast<size_t>(-increment);
  return static_cast<size_t>(index) * static_cast<size_t>(increment);
}

Status validVector(CublasHandle handle, int count, int increment) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (count < 0 || increment == 0) return CUBLAS_STATUS_INVALID_VALUE;
  return CUBLAS_STATUS_SUCCESS;
}

} // namespace

extern "C" GEMSTONE_PUBLIC_API int cublasInit() { return cuInit(0); }

extern "C" GEMSTONE_PUBLIC_API int cublasCreate_v2(CublasHandle *handle) {
  if (!handle) return CUBLAS_STATUS_INVALID_VALUE;
  const int32_t status = cuInit(0);
  if (status != CUBLAS_STATUS_SUCCESS) return status;
  *handle = new (std::nothrow) CublasHandleState{};
  return !*handle ? CUBLAS_STATUS_ALLOC_FAILED : CUBLAS_STATUS_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int cublasDestroy_v2(CublasHandle handle) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  delete handle;
  return CUBLAS_STATUS_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int cublasGetVersion_v2(CublasHandle handle, int *version) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (!version) return CUBLAS_STATUS_INVALID_VALUE;
  *version = 13000;
  return CUBLAS_STATUS_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int cublasSetPointerMode_v2(CublasHandle handle, int mode) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (mode < 0 || mode > 1) return CUBLAS_STATUS_INVALID_VALUE;
  handle->pointerMode = mode;
  return CUBLAS_STATUS_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int cublasGetPointerMode_v2(CublasHandle handle, int *mode) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (!mode) return CUBLAS_STATUS_INVALID_VALUE;
  *mode = handle->pointerMode;
  return CUBLAS_STATUS_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int cublasSetStream_v2(CublasHandle handle, void *stream) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  handle->stream = stream;
  return CUBLAS_STATUS_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int cublasGetStream_v2(CublasHandle handle, void **stream) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (!stream) return CUBLAS_STATUS_INVALID_VALUE;
  *stream = handle->stream;
  return CUBLAS_STATUS_SUCCESS;
}

#define GEMSTONE_CUBLAS_AXPY(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *alpha, const type *x, int increment_x, type *y, int increment_y) {       \
    Status status = validVector(handle, count, increment_x);                                                                               \
    if (status != CUBLAS_STATUS_SUCCESS || increment_y == 0)                                                                               \
      return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;                                                       \
    if (count == 0) return CUBLAS_STATUS_SUCCESS;                                                                                          \
    const size_t x_size = span(count, increment_x);                                                                                        \
    const size_t y_size = span(count, increment_y);                                                                                        \
    auto x_host = std::unique_ptr<type[]>{new (std::nothrow) type[x_size]};                                                                \
    auto y_host = std::unique_ptr<type[]>{new (std::nothrow) type[y_size]};                                                                \
    if (!x_host || !y_host) {                                                                                                              \
      return CUBLAS_STATUS_ALLOC_FAILED;                                                                                                   \
    }                                                                                                                                      \
    status = copyIn(x_host.get(), x, x_size * sizeof(type));                                                                               \
    if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(y_host.get(), y, y_size * sizeof(type));                                          \
    type scale = static_cast<type>(0);                                                                                                     \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarIn(handle, &scale, alpha, sizeof(scale));                                          \
    if (status == CUBLAS_STATUS_SUCCESS) {                                                                                                 \
      for (int index = 0; index < count; ++index)                                                                                          \
        y_host[indexOf(index, count, increment_y)] += scale * x_host[indexOf(index, count, increment_x)];                                  \
      status = copyOut(y, y_host.get(), y_size * sizeof(type));                                                                            \
    }                                                                                                                                      \
    return status;                                                                                                                         \
  }

GEMSTONE_CUBLAS_AXPY(float, cublasSaxpy_v2)
GEMSTONE_CUBLAS_AXPY(double, cublasDaxpy_v2)

#define GEMSTONE_CUBLAS_SCAL(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *alpha, type *x, int increment) {                                         \
    Status status = validVector(handle, count, increment);                                                                                 \
    if (status != CUBLAS_STATUS_SUCCESS) return status;                                                                                    \
    if (count == 0) return CUBLAS_STATUS_SUCCESS;                                                                                          \
    const size_t size = span(count, increment);                                                                                            \
    auto host = std::unique_ptr<type[]>{new (std::nothrow) type[size]};                                                                    \
    if (!host) return CUBLAS_STATUS_ALLOC_FAILED;                                                                                          \
    status = copyIn(host.get(), x, size * sizeof(type));                                                                                   \
    type scale = static_cast<type>(0);                                                                                                     \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarIn(handle, &scale, alpha, sizeof(scale));                                          \
    if (status == CUBLAS_STATUS_SUCCESS) {                                                                                                 \
      for (int index = 0; index < count; ++index)                                                                                          \
        host[indexOf(index, count, increment)] *= scale;                                                                                   \
      status = copyOut(x, host.get(), size * sizeof(type));                                                                                \
    }                                                                                                                                      \
    return status;                                                                                                                         \
  }

GEMSTONE_CUBLAS_SCAL(float, cublasSscal_v2)
GEMSTONE_CUBLAS_SCAL(double, cublasDscal_v2)

#define GEMSTONE_CUBLAS_COPY(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *x, int increment_x, type *y, int increment_y) {                          \
    Status status = validVector(handle, count, increment_x);                                                                               \
    if (status != CUBLAS_STATUS_SUCCESS || increment_y == 0)                                                                               \
      return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;                                                       \
    if (count == 0) return CUBLAS_STATUS_SUCCESS;                                                                                          \
    const size_t x_size = span(count, increment_x);                                                                                        \
    const size_t y_size = span(count, increment_y);                                                                                        \
    auto x_host = std::unique_ptr<type[]>{new (std::nothrow) type[x_size]};                                                                \
    auto y_host = std::unique_ptr<type[]>{new (std::nothrow) type[y_size]};                                                                \
    if (!x_host || !y_host) {                                                                                                              \
      return CUBLAS_STATUS_ALLOC_FAILED;                                                                                                   \
    }                                                                                                                                      \
    status = copyIn(x_host.get(), x, x_size * sizeof(type));                                                                               \
    if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(y_host.get(), y, y_size * sizeof(type));                                          \
    if (status == CUBLAS_STATUS_SUCCESS) {                                                                                                 \
      for (int index = 0; index < count; ++index)                                                                                          \
        y_host[indexOf(index, count, increment_y)] = x_host[indexOf(index, count, increment_x)];                                           \
      status = copyOut(y, y_host.get(), y_size * sizeof(type));                                                                            \
    }                                                                                                                                      \
    return status;                                                                                                                         \
  }

GEMSTONE_CUBLAS_COPY(float, cublasScopy_v2)
GEMSTONE_CUBLAS_COPY(double, cublasDcopy_v2)

#define GEMSTONE_CUBLAS_DOT(type, name)                                                                                                    \
  extern "C" int name(CublasHandle handle, int count, const type *x, int increment_x, const type *y, int increment_y, type *result) {      \
    Status status = validVector(handle, count, increment_x);                                                                               \
    if (status != CUBLAS_STATUS_SUCCESS || increment_y == 0)                                                                               \
      return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;                                                       \
    if (!result) return CUBLAS_STATUS_INVALID_VALUE;                                                                                       \
    type value = static_cast<type>(0);                                                                                                     \
    if (count > 0) {                                                                                                                       \
      const size_t x_size = span(count, increment_x);                                                                                      \
      const size_t y_size = span(count, increment_y);                                                                                      \
      auto x_host = std::unique_ptr<type[]>{new (std::nothrow) type[x_size]};                                                              \
      auto y_host = std::unique_ptr<type[]>{new (std::nothrow) type[y_size]};                                                              \
      if (!x_host || !y_host) {                                                                                                            \
        return CUBLAS_STATUS_ALLOC_FAILED;                                                                                                 \
      }                                                                                                                                    \
      status = copyIn(x_host.get(), x, x_size * sizeof(type));                                                                             \
      if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(y_host.get(), y, y_size * sizeof(type));                                        \
      if (status == CUBLAS_STATUS_SUCCESS)                                                                                                 \
        for (int index = 0; index < count; ++index)                                                                                        \
          value += x_host[indexOf(index, count, increment_x)] * y_host[indexOf(index, count, increment_y)];                                \
    }                                                                                                                                      \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarOut(handle, result, &value, sizeof(value));                                        \
    return status;                                                                                                                         \
  }

GEMSTONE_CUBLAS_DOT(float, cublasSdot_v2)
GEMSTONE_CUBLAS_DOT(double, cublasDdot_v2)

#define GEMSTONE_CUBLAS_NRM2(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *x, int increment, type *result) {                                        \
    Status status = validVector(handle, count, increment);                                                                                 \
    if (status != CUBLAS_STATUS_SUCCESS || !result) return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;         \
    type value = static_cast<type>(0);                                                                                                     \
    if (count > 0) {                                                                                                                       \
      const size_t size = span(count, increment);                                                                                          \
      auto host = std::unique_ptr<type[]>{new (std::nothrow) type[size]};                                                                  \
      if (!host) return CUBLAS_STATUS_ALLOC_FAILED;                                                                                        \
      status = copyIn(host.get(), x, size * sizeof(type));                                                                                 \
      if (status == CUBLAS_STATUS_SUCCESS) {                                                                                               \
        long double sum = 0;                                                                                                               \
        for (int index = 0; index < count; ++index) {                                                                                      \
          const long double value_at = host[indexOf(index, count, increment)];                                                             \
          sum += value_at * value_at;                                                                                                      \
        }                                                                                                                                  \
        value = static_cast<type>(sqrt(sum));                                                                                              \
      }                                                                                                                                    \
    }                                                                                                                                      \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarOut(handle, result, &value, sizeof(value));                                        \
    return status;                                                                                                                         \
  }

GEMSTONE_CUBLAS_NRM2(float, cublasSnrm2_v2)
GEMSTONE_CUBLAS_NRM2(double, cublasDnrm2_v2)

#define GEMSTONE_CUBLAS_ASUM(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *x, int increment, type *result) {                                        \
    Status status = validVector(handle, count, increment);                                                                                 \
    if (status != CUBLAS_STATUS_SUCCESS || !result) return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;         \
    type value = static_cast<type>(0);                                                                                                     \
    if (count > 0) {                                                                                                                       \
      const size_t size = span(count, increment);                                                                                          \
      auto host = std::unique_ptr<type[]>{new (std::nothrow) type[size]};                                                                  \
      if (!host) return CUBLAS_STATUS_ALLOC_FAILED;                                                                                        \
      status = copyIn(host.get(), x, size * sizeof(type));                                                                                 \
      if (status == CUBLAS_STATUS_SUCCESS)                                                                                                 \
        for (int index = 0; index < count; ++index)                                                                                        \
          value += static_cast<type>(fabs(static_cast<double>(host[indexOf(index, count, increment)])));                                   \
    }                                                                                                                                      \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarOut(handle, result, &value, sizeof(value));                                        \
    return status;                                                                                                                         \
  }

GEMSTONE_CUBLAS_ASUM(float, cublasSasum_v2)
GEMSTONE_CUBLAS_ASUM(double, cublasDasum_v2)

#define GEMSTONE_CUBLAS_GEMV(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int transpose, int rows, int columns, const type *alpha, const type *a, int lda, const type *x, \
                      int increment_x, const type *beta, type *y, int increment_y) {                                                       \
    if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;                                                                                     \
    if (rows < 0 || columns < 0 || lda < rows || increment_x == 0 || increment_y == 0) return CUBLAS_STATUS_INVALID_VALUE;                 \
    const int x_count = transpose == CUBLAS_OP_N ? columns : rows;                                                                         \
    const int y_count = transpose == CUBLAS_OP_N ? rows : columns;                                                                         \
    const size_t a_size = static_cast<size_t>(lda) * static_cast<size_t>(columns);                                                         \
    const size_t x_size = span(x_count, increment_x);                                                                                      \
    const size_t y_size = span(y_count, increment_y);                                                                                      \
    auto a_host = std::unique_ptr<type[]>{new (std::nothrow) type[a_size]};                                                                \
    auto x_host = std::unique_ptr<type[]>{new (std::nothrow) type[x_size]};                                                                \
    auto y_host = std::unique_ptr<type[]>{new (std::nothrow) type[y_size]};                                                                \
    if (!a_host || !x_host || !y_host) {                                                                                                   \
      return CUBLAS_STATUS_ALLOC_FAILED;                                                                                                   \
    }                                                                                                                                      \
    Status status = copyIn(a_host.get(), a, a_size * sizeof(type));                                                                        \
    if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(x_host.get(), x, x_size * sizeof(type));                                          \
    if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(y_host.get(), y, y_size * sizeof(type));                                          \
    type scale_a = static_cast<type>(0), scale_b = static_cast<type>(0);                                                                   \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarIn(handle, &scale_a, alpha, sizeof(scale_a));                                      \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarIn(handle, &scale_b, beta, sizeof(scale_b));                                       \
    if (status == CUBLAS_STATUS_SUCCESS) {                                                                                                 \
      for (int row = 0; row < y_count; ++row) {                                                                                            \
        type sum = static_cast<type>(0);                                                                                                   \
        for (int column = 0; column < x_count; ++column) {                                                                                 \
          const type value = transpose == CUBLAS_OP_N ? a_host[row + column * lda] : a_host[column + row * lda];                           \
          sum += value * x_host[indexOf(column, x_count, increment_x)];                                                                    \
        }                                                                                                                                  \
        const size_t output = indexOf(row, y_count, increment_y);                                                                          \
        y_host[output] = scale_a * sum + (scale_b == static_cast<type>(0) ? static_cast<type>(0) : scale_b * y_host[output]);              \
      }                                                                                                                                    \
      status = copyOut(y, y_host.get(), y_size * sizeof(type));                                                                            \
    }                                                                                                                                      \
    return status;                                                                                                                         \
  }

GEMSTONE_CUBLAS_GEMV(float, cublasSgemv_v2)
GEMSTONE_CUBLAS_GEMV(double, cublasDgemv_v2)

namespace {

struct CublasHalf {
  uint16_t bits;
};

float halfToFloat(uint16_t bits) {
  const uint32_t sign = static_cast<uint32_t>(bits >> 15) << 31;
  const uint32_t exponent = (bits >> 10) & 0x1f;
  const uint32_t mantissa = bits & 0x3ff;
  if (exponent == 0) {
    if (mantissa == 0) {
      return readNative<float>(&sign);
    }
    const float value = ldexpf(static_cast<float>(mantissa) / 1024.0f, -14);
    return (sign == 0 ? 1.0f : -1.0f) * value;
  }
  if (exponent == 0x1f) {
    const uint32_t result = sign | 0x7f800000U | (mantissa << 13);
    return readNative<float>(&result);
  }
  const float value = ldexpf(1.0f + static_cast<float>(mantissa) / 1024.0f, static_cast<int>(exponent) - 15);
  return (sign == 0 ? 1.0f : -1.0f) * value;
}

uint16_t floatToHalf(float value) {
  const uint32_t bits = readNative<uint32_t>(&value);
  const uint16_t sign = static_cast<uint16_t>((bits >> 16) & 0x8000U);
  const int exponent = static_cast<int>((bits >> 23) & 0xffU) - 127 + 15;
  const uint32_t mantissa = bits & 0x7fffffU;
  if (exponent <= 0) {
    if (exponent < -10) return sign;
    const uint32_t source = mantissa | 0x800000U;
    const unsigned shift = static_cast<unsigned>(14 - exponent);
    const uint32_t shifted = source >> shift;
    const uint32_t remainder = source & ((UINT32_C(1) << shift) - 1U);
    const uint32_t halfway = UINT32_C(1) << (shift - 1U);
    return static_cast<uint16_t>(sign | static_cast<uint16_t>(shifted + (remainder > halfway || (remainder == halfway && (shifted & 1U)))));
  }
  if (exponent >= 31) {
    if ((bits & 0x7fffffffU) > 0x7f800000U) return static_cast<uint16_t>(sign | 0x7e00U);
    return static_cast<uint16_t>(sign | 0x7c00U);
  }
  uint32_t rounded = mantissa + 0x1000U;
  int rounded_exponent = exponent;
  if (rounded & 0x800000U) {
    rounded = 0;
    ++rounded_exponent;
  }
  if (rounded_exponent >= 31) return static_cast<uint16_t>(sign | 0x7c00U);
  return static_cast<uint16_t>(sign | (static_cast<uint16_t>(rounded_exponent) << 10) | static_cast<uint16_t>(rounded >> 13));
}

Status hgemm(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k, const CublasHalf *alpha, const CublasHalf *a,
             int lda, const CublasHalf *b, int ldb, const CublasHalf *beta, CublasHalf *c, int ldc) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (m < 0 || n < 0 || k < 0 || lda < (transpose_a == CUBLAS_OP_N ? m : k) || ldb < (transpose_b == CUBLAS_OP_N ? k : n) || ldc < m)
    return CUBLAS_STATUS_INVALID_VALUE;
  const size_t a_size = static_cast<size_t>(lda) * static_cast<size_t>(transpose_a == CUBLAS_OP_N ? k : m);
  const size_t b_size = static_cast<size_t>(ldb) * static_cast<size_t>(transpose_b == CUBLAS_OP_N ? n : k);
  const size_t c_size = static_cast<size_t>(ldc) * static_cast<size_t>(n);
  auto a_host = std::unique_ptr<CublasHalf[]>{new (std::nothrow) CublasHalf[a_size]};
  auto b_host = std::unique_ptr<CublasHalf[]>{new (std::nothrow) CublasHalf[b_size]};
  auto c_host = std::unique_ptr<CublasHalf[]>{new (std::nothrow) CublasHalf[c_size]};
  if (!a_host || !b_host || !c_host) {
    return CUBLAS_STATUS_ALLOC_FAILED;
  }
  Status status = copyIn(a_host.get(), a, a_size * sizeof(CublasHalf));
  if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(b_host.get(), b, b_size * sizeof(CublasHalf));
  if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(c_host.get(), c, c_size * sizeof(CublasHalf));
  CublasHalf scalar_a = {0}, scalar_b = {0};
  if (status == CUBLAS_STATUS_SUCCESS) status = scalarIn(handle, &scalar_a, alpha, sizeof(scalar_a));
  if (status == CUBLAS_STATUS_SUCCESS) status = scalarIn(handle, &scalar_b, beta, sizeof(scalar_b));
  if (status == CUBLAS_STATUS_SUCCESS) {
    const float scale_a = halfToFloat(scalar_a.bits);
    const float scale_b = halfToFloat(scalar_b.bits);
    for (int column = 0; column < n; ++column)
      for (int row = 0; row < m; ++row) {
        float sum = 0.0f;
        for (int inner = 0; inner < k; ++inner) {
          const CublasHalf value_a = transpose_a == CUBLAS_OP_N ? a_host[row + inner * lda] : a_host[inner + row * lda];
          const CublasHalf value_b = transpose_b == CUBLAS_OP_N ? b_host[inner + column * ldb] : b_host[column + inner * ldb];
          sum += halfToFloat(value_a.bits) * halfToFloat(value_b.bits);
        }
        const float existing = halfToFloat(c_host[row + column * ldc].bits);
        c_host[row + column * ldc].bits = floatToHalf(scale_a * sum + (scale_b == 0.0f ? 0.0f : scale_b * existing));
      }
    status = copyOut(c, c_host.get(), c_size * sizeof(CublasHalf));
  }
  return status;
}

} // namespace

extern "C" GEMSTONE_PUBLIC_API int cublasHgemm(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k,
                                               const CublasHalf *alpha, const CublasHalf *a, int lda, const CublasHalf *b, int ldb,
                                               const CublasHalf *beta, CublasHalf *c, int ldc) {
  return hgemm(handle, transpose_a, transpose_b, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
}

extern "C" GEMSTONE_PUBLIC_API int cublasHgemmStridedBatched(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k,
                                                             const CublasHalf *alpha, const CublasHalf *a, int lda, long long stride_a,
                                                             const CublasHalf *b, int ldb, long long stride_b, const CublasHalf *beta,
                                                             CublasHalf *c, int ldc, long long stride_c, int batch_count) {
  if (batch_count < 0) return CUBLAS_STATUS_INVALID_VALUE;
  for (int batch = 0; batch < batch_count; ++batch) {
    const auto *a_batch =
        reinterpret_cast<const CublasHalf *>(reinterpret_cast<uintptr_t>(a) + static_cast<uintptr_t>(batch * stride_a * 2));
    const auto *b_batch =
        reinterpret_cast<const CublasHalf *>(reinterpret_cast<uintptr_t>(b) + static_cast<uintptr_t>(batch * stride_b * 2));
    auto *c_batch = reinterpret_cast<CublasHalf *>(reinterpret_cast<uintptr_t>(c) + static_cast<uintptr_t>(batch * stride_c * 2));
    const Status status = hgemm(handle, transpose_a, transpose_b, m, n, k, alpha, a_batch, lda, b_batch, ldb, beta, c_batch, ldc);
    if (status != CUBLAS_STATUS_SUCCESS) return status;
  }
  return CUBLAS_STATUS_SUCCESS;
}

#define GEMSTONE_CUBLAS_GEMM(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k, const type *alpha, const type *a,        \
                      int lda, const type *b, int ldb, const type *beta, type *c, int ldc) {                                               \
    if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;                                                                                     \
    if (m < 0 || n < 0 || k < 0 || lda < (transpose_a == CUBLAS_OP_N ? m : k) || ldb < (transpose_b == CUBLAS_OP_N ? k : n) || ldc < m)    \
      return CUBLAS_STATUS_INVALID_VALUE;                                                                                                  \
    const size_t a_size = static_cast<size_t>(lda) * (transpose_a == CUBLAS_OP_N ? static_cast<size_t>(k) : static_cast<size_t>(m));       \
    const size_t b_size = static_cast<size_t>(ldb) * (transpose_b == CUBLAS_OP_N ? static_cast<size_t>(n) : static_cast<size_t>(k));       \
    const size_t c_size = static_cast<size_t>(ldc) * static_cast<size_t>(n);                                                               \
    auto a_host = std::unique_ptr<type[]>{new (std::nothrow) type[a_size]};                                                                \
    auto b_host = std::unique_ptr<type[]>{new (std::nothrow) type[b_size]};                                                                \
    auto c_host = std::unique_ptr<type[]>{new (std::nothrow) type[c_size]};                                                                \
    if (!a_host || !b_host || !c_host) {                                                                                                   \
      return CUBLAS_STATUS_ALLOC_FAILED;                                                                                                   \
    }                                                                                                                                      \
    Status status = copyIn(a_host.get(), a, a_size * sizeof(type));                                                                        \
    if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(b_host.get(), b, b_size * sizeof(type));                                          \
    if (status == CUBLAS_STATUS_SUCCESS) status = copyIn(c_host.get(), c, c_size * sizeof(type));                                          \
    type scale_a = static_cast<type>(0), scale_b = static_cast<type>(0);                                                                   \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarIn(handle, &scale_a, alpha, sizeof(scale_a));                                      \
    if (status == CUBLAS_STATUS_SUCCESS) status = scalarIn(handle, &scale_b, beta, sizeof(scale_b));                                       \
    if (status == CUBLAS_STATUS_SUCCESS) {                                                                                                 \
      for (int column = 0; column < n; ++column)                                                                                           \
        for (int row = 0; row < m; ++row) {                                                                                                \
          type sum = static_cast<type>(0);                                                                                                 \
          for (int inner = 0; inner < k; ++inner) {                                                                                        \
            const type value_a = transpose_a == CUBLAS_OP_N ? a_host[row + inner * lda] : a_host[inner + row * lda];                       \
            const type value_b = transpose_b == CUBLAS_OP_N ? b_host[inner + column * ldb] : b_host[column + inner * ldb];                 \
            sum += value_a * value_b;                                                                                                      \
          }                                                                                                                                \
          c_host[row + column * ldc] =                                                                                                     \
              scale_a * sum + (scale_b == static_cast<type>(0) ? static_cast<type>(0) : scale_b * c_host[row + column * ldc]);             \
        }                                                                                                                                  \
      status = copyOut(c, c_host.get(), c_size * sizeof(type));                                                                            \
    }                                                                                                                                      \
    return status;                                                                                                                         \
  }

GEMSTONE_CUBLAS_GEMM(float, cublasSgemm_v2)
GEMSTONE_CUBLAS_GEMM(double, cublasDgemm_v2)

#define GEMSTONE_CUBLAS_GEMM_BATCHED(type, name, gemm_name)                                                                                \
  extern "C" int name(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k, const type *alpha, const type *a,        \
                      int lda, long long stride_a, const type *b, int ldb, long long stride_b, const type *beta, type *c, int ldc,         \
                      long long stride_c, int batch_count) {                                                                               \
    if (batch_count < 0) return CUBLAS_STATUS_INVALID_VALUE;                                                                               \
    for (int batch = 0; batch < batch_count; ++batch) {                                                                                    \
      const uintptr_t a_address =                                                                                                          \
          reinterpret_cast<uintptr_t>(a) + static_cast<uintptr_t>(batch) * static_cast<uintptr_t>(stride_a) * sizeof(type);                \
      const uintptr_t b_address =                                                                                                          \
          reinterpret_cast<uintptr_t>(b) + static_cast<uintptr_t>(batch) * static_cast<uintptr_t>(stride_b) * sizeof(type);                \
      const uintptr_t c_address =                                                                                                          \
          reinterpret_cast<uintptr_t>(c) + static_cast<uintptr_t>(batch) * static_cast<uintptr_t>(stride_c) * sizeof(type);                \
      const int status = gemm_name(handle, transpose_a, transpose_b, m, n, k, alpha, reinterpret_cast<const type *>(a_address), lda,       \
                                   reinterpret_cast<const type *>(b_address), ldb, beta, reinterpret_cast<type *>(c_address), ldc);        \
      if (status != CUBLAS_STATUS_SUCCESS) return status;                                                                                  \
    }                                                                                                                                      \
    return CUBLAS_STATUS_SUCCESS;                                                                                                          \
  }

GEMSTONE_CUBLAS_GEMM_BATCHED(float, cublasSgemmStridedBatched, cublasSgemm_v2)
GEMSTONE_CUBLAS_GEMM_BATCHED(double, cublasDgemmStridedBatched, cublasDgemm_v2)
