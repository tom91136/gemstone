#include <math.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

/*
 * cuBLAS is a host-side library.  Its CUDA 13 binary normally discovers a
 * hardware-specific dispatch catalogue and refuses to initialise when the
 * driver cannot load its private cubins.  Gemstone exposes this small,
 * versioned compatibility surface from the CUDA provider instead.  It keeps
 * the vendor ABI and device allocations intact, but executes the BLAS
 * operations over the ordinary driver copy RPCs and performs the arithmetic
 * in the provider process.
 */

typedef int32_t GemstoneCublasStatus;
typedef struct GemstoneCublasHandle {
  int32_t pointer_mode;
  void *stream;
} *GemstoneCublasHandle;

enum {
  GEMSTONE_CUBLAS_SUCCESS = 0,
  GEMSTONE_CUBLAS_NOT_INITIALIZED = 1,
  GEMSTONE_CUBLAS_ALLOC_FAILED = 3,
  GEMSTONE_CUBLAS_INVALID_VALUE = 7,
  GEMSTONE_CUBLAS_NOT_SUPPORTED = 15
};

enum {
  GEMSTONE_CUBLAS_OP_N = 0,
  GEMSTONE_CUBLAS_OP_T = 1,
  GEMSTONE_CUBLAS_OP_C = 2
};

extern int32_t cuInit(unsigned int flags);
extern int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size);
extern int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size);

static int gemstone_cublas_device_pointer(const void *pointer) {
  const uintptr_t value = (uintptr_t)pointer;
  return value >= UINT64_C(0x100000000) && value < UINT64_C(0x1000000000);
}

static GemstoneCublasStatus gemstone_cublas_copy_in(void *host, const void *pointer, size_t size) {
  if (host == NULL || pointer == NULL) return GEMSTONE_CUBLAS_INVALID_VALUE;
  if (gemstone_cublas_device_pointer(pointer))
    return cuMemcpyDtoH_v2(host, (uint64_t)(uintptr_t)pointer, size);
  memcpy(host, pointer, size);
  return GEMSTONE_CUBLAS_SUCCESS;
}

static GemstoneCublasStatus gemstone_cublas_copy_out(void *pointer, const void *host, size_t size) {
  if (pointer == NULL || host == NULL) return GEMSTONE_CUBLAS_INVALID_VALUE;
  if (gemstone_cublas_device_pointer(pointer))
    return cuMemcpyHtoD_v2((uint64_t)(uintptr_t)pointer, host, size);
  memcpy(pointer, host, size);
  return GEMSTONE_CUBLAS_SUCCESS;
}

static size_t gemstone_cublas_span(int count, int increment) {
  if (count <= 0) return 0;
  const size_t step = (size_t)(increment < 0 ? -increment : increment);
  return 1 + ((size_t)count - 1) * step;
}

static size_t gemstone_cublas_index(int index, int count, int increment) {
  if (increment < 0) return ((size_t)count - 1 - (size_t)index) * (size_t)(-increment);
  return (size_t)index * (size_t)increment;
}

static GemstoneCublasStatus gemstone_cublas_valid_vector(
    GemstoneCublasHandle handle,
    int count,
    int increment) {
  if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;
  if (count < 0 || increment == 0) return GEMSTONE_CUBLAS_INVALID_VALUE;
  return GEMSTONE_CUBLAS_SUCCESS;
}

int cublasInit(void) { return cuInit(0); }

int cublasCreate_v2(GemstoneCublasHandle *handle) {
  if (handle == NULL) return GEMSTONE_CUBLAS_INVALID_VALUE;
  const int32_t status = cuInit(0);
  if (status != GEMSTONE_CUBLAS_SUCCESS) return status;
  *handle = (GemstoneCublasHandle)calloc(1, sizeof(**handle));
  return *handle == NULL ? GEMSTONE_CUBLAS_ALLOC_FAILED : GEMSTONE_CUBLAS_SUCCESS;
}

int cublasDestroy_v2(GemstoneCublasHandle handle) {
  if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;
  free(handle);
  return GEMSTONE_CUBLAS_SUCCESS;
}

int cublasGetVersion_v2(GemstoneCublasHandle handle, int *version) {
  if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;
  if (version == NULL) return GEMSTONE_CUBLAS_INVALID_VALUE;
  *version = 13000;
  return GEMSTONE_CUBLAS_SUCCESS;
}

int cublasSetPointerMode_v2(GemstoneCublasHandle handle, int mode) {
  if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;
  if (mode < 0 || mode > 1) return GEMSTONE_CUBLAS_INVALID_VALUE;
  handle->pointer_mode = mode;
  return GEMSTONE_CUBLAS_SUCCESS;
}

int cublasGetPointerMode_v2(GemstoneCublasHandle handle, int *mode) {
  if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;
  if (mode == NULL) return GEMSTONE_CUBLAS_INVALID_VALUE;
  *mode = handle->pointer_mode;
  return GEMSTONE_CUBLAS_SUCCESS;
}

int cublasSetStream_v2(GemstoneCublasHandle handle, void *stream) {
  if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;
  handle->stream = stream;
  return GEMSTONE_CUBLAS_SUCCESS;
}

int cublasGetStream_v2(GemstoneCublasHandle handle, void **stream) {
  if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;
  if (stream == NULL) return GEMSTONE_CUBLAS_INVALID_VALUE;
  *stream = handle->stream;
  return GEMSTONE_CUBLAS_SUCCESS;
}

#define GEMSTONE_CUBLAS_AXPY(type, name)                                                   \
  int name(                                                                                    \
      GemstoneCublasHandle handle,                                                           \
      int count,                                                                              \
      const type *alpha,                                                                      \
      const type *x,                                                                          \
      int increment_x,                                                                        \
      type *y,                                                                                \
      int increment_y) {                                                                      \
    GemstoneCublasStatus status = gemstone_cublas_valid_vector(handle, count, increment_x); \
    if (status != GEMSTONE_CUBLAS_SUCCESS || increment_y == 0)                              \
      return status != GEMSTONE_CUBLAS_SUCCESS ? status : GEMSTONE_CUBLAS_INVALID_VALUE;    \
    if (count == 0) return GEMSTONE_CUBLAS_SUCCESS;                                         \
    const size_t x_size = gemstone_cublas_span(count, increment_x);                         \
    const size_t y_size = gemstone_cublas_span(count, increment_y);                         \
    type *x_host = (type *)malloc(x_size * sizeof(type));                                   \
    type *y_host = (type *)malloc(y_size * sizeof(type));                                   \
    if (x_host == NULL || y_host == NULL) {                                                 \
      free(x_host);                                                                          \
      free(y_host);                                                                          \
      return GEMSTONE_CUBLAS_ALLOC_FAILED;                                                   \
    }                                                                                       \
    status = gemstone_cublas_copy_in(x_host, x, x_size * sizeof(type));                      \
    if (status == GEMSTONE_CUBLAS_SUCCESS)                                                   \
      status = gemstone_cublas_copy_in(y_host, y, y_size * sizeof(type));                   \
    if (status == GEMSTONE_CUBLAS_SUCCESS) {                                                 \
      const type scale = alpha == NULL ? (type)0 : *alpha;                                  \
      for (int index = 0; index < count; ++index)                                            \
        y_host[gemstone_cublas_index(index, count, increment_y)] +=                         \
            scale * x_host[gemstone_cublas_index(index, count, increment_x)];               \
      status = gemstone_cublas_copy_out(y, y_host, y_size * sizeof(type));                  \
    }                                                                                       \
    free(x_host);                                                                            \
    free(y_host);                                                                            \
    return status;                                                                           \
  }

GEMSTONE_CUBLAS_AXPY(float, cublasSaxpy_v2)
GEMSTONE_CUBLAS_AXPY(double, cublasDaxpy_v2)

#define GEMSTONE_CUBLAS_SCAL(type, name)                                                   \
  int name(GemstoneCublasHandle handle, int count, const type *alpha, type *x, int increment) { \
    GemstoneCublasStatus status = gemstone_cublas_valid_vector(handle, count, increment); \
    if (status != GEMSTONE_CUBLAS_SUCCESS) return status;                                  \
    if (count == 0) return GEMSTONE_CUBLAS_SUCCESS;                                        \
    const size_t size = gemstone_cublas_span(count, increment);                             \
    type *host = (type *)malloc(size * sizeof(type));                                      \
    if (host == NULL) return GEMSTONE_CUBLAS_ALLOC_FAILED;                                 \
    status = gemstone_cublas_copy_in(host, x, size * sizeof(type));                        \
    if (status == GEMSTONE_CUBLAS_SUCCESS) {                                               \
      const type scale = alpha == NULL ? (type)0 : *alpha;                                 \
      for (int index = 0; index < count; ++index)                                          \
        host[gemstone_cublas_index(index, count, increment)] *= scale;                      \
      status = gemstone_cublas_copy_out(x, host, size * sizeof(type));                     \
    }                                                                                      \
    free(host);                                                                             \
    return status;                                                                          \
  }

GEMSTONE_CUBLAS_SCAL(float, cublasSscal_v2)
GEMSTONE_CUBLAS_SCAL(double, cublasDscal_v2)

#define GEMSTONE_CUBLAS_COPY(type, name)                                                   \
  int name(                                                                                 \
      GemstoneCublasHandle handle, int count, const type *x, int increment_x, type *y, int increment_y) { \
    GemstoneCublasStatus status = gemstone_cublas_valid_vector(handle, count, increment_x); \
    if (status != GEMSTONE_CUBLAS_SUCCESS || increment_y == 0)                             \
      return status != GEMSTONE_CUBLAS_SUCCESS ? status : GEMSTONE_CUBLAS_INVALID_VALUE;   \
    if (count == 0) return GEMSTONE_CUBLAS_SUCCESS;                                        \
    const size_t x_size = gemstone_cublas_span(count, increment_x);                        \
    const size_t y_size = gemstone_cublas_span(count, increment_y);                        \
    type *x_host = (type *)malloc(x_size * sizeof(type));                                  \
    type *y_host = (type *)malloc(y_size * sizeof(type));                                  \
    if (x_host == NULL || y_host == NULL) {                                                \
      free(x_host);                                                                         \
      free(y_host);                                                                         \
      return GEMSTONE_CUBLAS_ALLOC_FAILED;                                                  \
    }                                                                                      \
    status = gemstone_cublas_copy_in(x_host, x, x_size * sizeof(type));                     \
    if (status == GEMSTONE_CUBLAS_SUCCESS)                                                  \
      status = gemstone_cublas_copy_in(y_host, y, y_size * sizeof(type));                  \
    if (status == GEMSTONE_CUBLAS_SUCCESS) {                                                \
      for (int index = 0; index < count; ++index)                                           \
        y_host[gemstone_cublas_index(index, count, increment_y)] =                         \
            x_host[gemstone_cublas_index(index, count, increment_x)];                      \
      status = gemstone_cublas_copy_out(y, y_host, y_size * sizeof(type));                 \
    }                                                                                      \
    free(x_host);                                                                           \
    free(y_host);                                                                           \
    return status;                                                                          \
  }

GEMSTONE_CUBLAS_COPY(float, cublasScopy_v2)
GEMSTONE_CUBLAS_COPY(double, cublasDcopy_v2)

#define GEMSTONE_CUBLAS_DOT(type, name)                                                    \
  int name(                                                                                 \
      GemstoneCublasHandle handle, int count, const type *x, int increment_x,                \
      const type *y, int increment_y, type *result) {                                       \
    GemstoneCublasStatus status = gemstone_cublas_valid_vector(handle, count, increment_x); \
    if (status != GEMSTONE_CUBLAS_SUCCESS || increment_y == 0)                             \
      return status != GEMSTONE_CUBLAS_SUCCESS ? status : GEMSTONE_CUBLAS_INVALID_VALUE;   \
    if (result == NULL) return GEMSTONE_CUBLAS_INVALID_VALUE;                              \
    type value = (type)0;                                                                  \
    if (count > 0) {                                                                        \
      const size_t x_size = gemstone_cublas_span(count, increment_x);                      \
      const size_t y_size = gemstone_cublas_span(count, increment_y);                      \
      type *x_host = (type *)malloc(x_size * sizeof(type));                                \
      type *y_host = (type *)malloc(y_size * sizeof(type));                                \
      if (x_host == NULL || y_host == NULL) {                                              \
        free(x_host);                                                                       \
        free(y_host);                                                                       \
        return GEMSTONE_CUBLAS_ALLOC_FAILED;                                                \
      }                                                                                     \
      status = gemstone_cublas_copy_in(x_host, x, x_size * sizeof(type));                  \
      if (status == GEMSTONE_CUBLAS_SUCCESS)                                               \
        status = gemstone_cublas_copy_in(y_host, y, y_size * sizeof(type));                \
      if (status == GEMSTONE_CUBLAS_SUCCESS)                                               \
        for (int index = 0; index < count; ++index)                                        \
          value += x_host[gemstone_cublas_index(index, count, increment_x)] *              \
                   y_host[gemstone_cublas_index(index, count, increment_y)];                \
      free(x_host);                                                                         \
      free(y_host);                                                                         \
    }                                                                                       \
    if (status == GEMSTONE_CUBLAS_SUCCESS) status = gemstone_cublas_copy_out(result, &value, sizeof(value)); \
    return status;                                                                          \
  }

GEMSTONE_CUBLAS_DOT(float, cublasSdot_v2)
GEMSTONE_CUBLAS_DOT(double, cublasDdot_v2)

#define GEMSTONE_CUBLAS_NRM2(type, name)                                                   \
  int name(GemstoneCublasHandle handle, int count, const type *x, int increment, type *result) { \
    GemstoneCublasStatus status = gemstone_cublas_valid_vector(handle, count, increment);  \
    if (status != GEMSTONE_CUBLAS_SUCCESS || result == NULL)                              \
      return status != GEMSTONE_CUBLAS_SUCCESS ? status : GEMSTONE_CUBLAS_INVALID_VALUE;  \
    type value = (type)0;                                                                  \
    if (count > 0) {                                                                        \
      const size_t size = gemstone_cublas_span(count, increment);                          \
      type *host = (type *)malloc(size * sizeof(type));                                    \
      if (host == NULL) return GEMSTONE_CUBLAS_ALLOC_FAILED;                               \
      status = gemstone_cublas_copy_in(host, x, size * sizeof(type));                     \
      if (status == GEMSTONE_CUBLAS_SUCCESS) {                                             \
        long double sum = 0;                                                               \
        for (int index = 0; index < count; ++index) {                                       \
          const long double value_at = host[gemstone_cublas_index(index, count, increment)]; \
          sum += value_at * value_at;                                                      \
        }                                                                                  \
        value = (type)sqrt(sum);                                                           \
      }                                                                                    \
      free(host);                                                                          \
    }                                                                                       \
    if (status == GEMSTONE_CUBLAS_SUCCESS) status = gemstone_cublas_copy_out(result, &value, sizeof(value)); \
    return status;                                                                          \
  }

GEMSTONE_CUBLAS_NRM2(float, cublasSnrm2_v2)
GEMSTONE_CUBLAS_NRM2(double, cublasDnrm2_v2)

#define GEMSTONE_CUBLAS_ASUM(type, name)                                                   \
  int name(GemstoneCublasHandle handle, int count, const type *x, int increment, type *result) { \
    GemstoneCublasStatus status = gemstone_cublas_valid_vector(handle, count, increment);  \
    if (status != GEMSTONE_CUBLAS_SUCCESS || result == NULL)                              \
      return status != GEMSTONE_CUBLAS_SUCCESS ? status : GEMSTONE_CUBLAS_INVALID_VALUE;  \
    type value = (type)0;                                                                  \
    if (count > 0) {                                                                        \
      const size_t size = gemstone_cublas_span(count, increment);                          \
      type *host = (type *)malloc(size * sizeof(type));                                    \
      if (host == NULL) return GEMSTONE_CUBLAS_ALLOC_FAILED;                               \
      status = gemstone_cublas_copy_in(host, x, size * sizeof(type));                     \
      if (status == GEMSTONE_CUBLAS_SUCCESS)                                               \
        for (int index = 0; index < count; ++index)                                        \
          value += (type)fabs((double)host[gemstone_cublas_index(index, count, increment)]); \
      free(host);                                                                          \
    }                                                                                       \
    if (status == GEMSTONE_CUBLAS_SUCCESS) status = gemstone_cublas_copy_out(result, &value, sizeof(value)); \
    return status;                                                                          \
  }

GEMSTONE_CUBLAS_ASUM(float, cublasSasum_v2)
GEMSTONE_CUBLAS_ASUM(double, cublasDasum_v2)

#define GEMSTONE_CUBLAS_GEMV(type, name)                                                   \
  int name(                                                                                 \
      GemstoneCublasHandle handle, int transpose, int rows, int columns,                    \
      const type *alpha, const type *a, int lda, const type *x, int increment_x,            \
      const type *beta, type *y, int increment_y) {                                          \
    if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;                             \
    if (rows < 0 || columns < 0 ||                                                         \
        lda < rows ||                                                                      \
        increment_x == 0 || increment_y == 0)                                               \
      return GEMSTONE_CUBLAS_INVALID_VALUE;                                                 \
    const int x_count = transpose == GEMSTONE_CUBLAS_OP_N ? columns : rows;                \
    const int y_count = transpose == GEMSTONE_CUBLAS_OP_N ? rows : columns;                \
    const size_t a_size = (size_t)lda * (size_t)columns;                                  \
    const size_t x_size = gemstone_cublas_span(x_count, increment_x);                       \
    const size_t y_size = gemstone_cublas_span(y_count, increment_y);                       \
    type *a_host = (type *)malloc(a_size * sizeof(type));                                  \
    type *x_host = (type *)malloc(x_size * sizeof(type));                                  \
    type *y_host = (type *)malloc(y_size * sizeof(type));                                  \
    if (a_host == NULL || x_host == NULL || y_host == NULL) {                              \
      free(a_host);                                                                         \
      free(x_host);                                                                         \
      free(y_host);                                                                         \
      return GEMSTONE_CUBLAS_ALLOC_FAILED;                                                  \
    }                                                                                       \
    GemstoneCublasStatus status = gemstone_cublas_copy_in(a_host, a, a_size * sizeof(type)); \
    if (status == GEMSTONE_CUBLAS_SUCCESS)                                                  \
      status = gemstone_cublas_copy_in(x_host, x, x_size * sizeof(type));                  \
    if (status == GEMSTONE_CUBLAS_SUCCESS)                                                  \
      status = gemstone_cublas_copy_in(y_host, y, y_size * sizeof(type));                  \
    if (status == GEMSTONE_CUBLAS_SUCCESS) {                                                \
      const type scale_a = alpha == NULL ? (type)0 : *alpha;                               \
      const type scale_b = beta == NULL ? (type)0 : *beta;                                 \
      for (int row = 0; row < y_count; ++row) {                                             \
        type sum = (type)0;                                                                 \
        for (int column = 0; column < x_count; ++column) {                                 \
          const type value = transpose == GEMSTONE_CUBLAS_OP_N                           \
                                 ? a_host[row + column * lda]                              \
                                 : a_host[column + row * lda];                              \
          sum += value * x_host[gemstone_cublas_index(column, x_count, increment_x)];      \
        }                                                                                   \
        const size_t output = gemstone_cublas_index(row, y_count, increment_y);             \
        y_host[output] = scale_a * sum +                                                   \
                         (scale_b == (type)0 ? (type)0 : scale_b * y_host[output]);         \
      }                                                                                     \
      status = gemstone_cublas_copy_out(y, y_host, y_size * sizeof(type));                 \
    }                                                                                       \
    free(a_host);                                                                           \
    free(x_host);                                                                           \
    free(y_host);                                                                           \
    return status;                                                                          \
  }

GEMSTONE_CUBLAS_GEMV(float, cublasSgemv_v2)
GEMSTONE_CUBLAS_GEMV(double, cublasDgemv_v2)

typedef struct GemstoneCublasHalf {
  uint16_t bits;
} GemstoneCublasHalf;

static float gemstone_cublas_half_to_float(uint16_t bits) {
  const uint32_t sign = (uint32_t)(bits >> 15) << 31;
  const uint32_t exponent = (bits >> 10) & 0x1f;
  const uint32_t mantissa = bits & 0x3ff;
  if (exponent == 0) {
    if (mantissa == 0) {
      uint32_t result = sign;
      float value;
      memcpy(&value, &result, sizeof(value));
      return value;
    }
    const float value = ldexpf((float)mantissa / 1024.0f, -14);
    return (sign == 0 ? 1.0f : -1.0f) * value;
  }
  if (exponent == 0x1f) {
    const uint32_t result = sign | 0x7f800000U | (mantissa << 13);
    float value;
    memcpy(&value, &result, sizeof(value));
    return value;
  }
  const float value = ldexpf(1.0f + (float)mantissa / 1024.0f, (int)exponent - 15);
  return (sign == 0 ? 1.0f : -1.0f) * value;
}

static uint16_t gemstone_cublas_float_to_half(float value) {
  uint32_t bits = 0;
  memcpy(&bits, &value, sizeof(bits));
  const uint16_t sign = (uint16_t)((bits >> 16) & 0x8000U);
  const int exponent = (int)((bits >> 23) & 0xffU) - 127 + 15;
  const uint32_t mantissa = bits & 0x7fffffU;
  if (exponent <= 0) {
    if (exponent < -10) return sign;
    const uint32_t source = mantissa | 0x800000U;
    const unsigned shift = (unsigned)(14 - exponent);
    const uint32_t shifted = source >> shift;
    const uint32_t remainder = source & ((UINT32_C(1) << shift) - 1U);
    const uint32_t halfway = UINT32_C(1) << (shift - 1U);
    return (uint16_t)(sign | (uint16_t)(shifted + (remainder > halfway || (remainder == halfway && (shifted & 1U)))));
  }
  if (exponent >= 31) {
    if ((bits & 0x7fffffffU) > 0x7f800000U) return (uint16_t)(sign | 0x7e00U);
    return (uint16_t)(sign | 0x7c00U);
  }
  uint32_t rounded = mantissa + 0x1000U;
  int rounded_exponent = exponent;
  if (rounded & 0x800000U) {
    rounded = 0;
    ++rounded_exponent;
  }
  if (rounded_exponent >= 31) return (uint16_t)(sign | 0x7c00U);
  return (uint16_t)(sign | ((uint16_t)rounded_exponent << 10) | (uint16_t)(rounded >> 13));
}

static GemstoneCublasStatus gemstone_cublas_hgemm(
    GemstoneCublasHandle handle,
    int transpose_a,
    int transpose_b,
    int m,
    int n,
    int k,
    const GemstoneCublasHalf *alpha,
    const GemstoneCublasHalf *a,
    int lda,
    const GemstoneCublasHalf *b,
    int ldb,
    const GemstoneCublasHalf *beta,
    GemstoneCublasHalf *c,
    int ldc) {
  if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;
  if (m < 0 || n < 0 || k < 0 ||
      lda < (transpose_a == GEMSTONE_CUBLAS_OP_N ? m : k) ||
      ldb < (transpose_b == GEMSTONE_CUBLAS_OP_N ? k : n) || ldc < m)
    return GEMSTONE_CUBLAS_INVALID_VALUE;
  const size_t a_size = (size_t)lda * (size_t)(transpose_a == GEMSTONE_CUBLAS_OP_N ? k : m);
  const size_t b_size = (size_t)ldb * (size_t)(transpose_b == GEMSTONE_CUBLAS_OP_N ? n : k);
  const size_t c_size = (size_t)ldc * (size_t)n;
  GemstoneCublasHalf *a_host = (GemstoneCublasHalf *)malloc(a_size * sizeof(*a_host));
  GemstoneCublasHalf *b_host = (GemstoneCublasHalf *)malloc(b_size * sizeof(*b_host));
  GemstoneCublasHalf *c_host = (GemstoneCublasHalf *)malloc(c_size * sizeof(*c_host));
  if (a_host == NULL || b_host == NULL || c_host == NULL) {
    free(a_host);
    free(b_host);
    free(c_host);
    return GEMSTONE_CUBLAS_ALLOC_FAILED;
  }
  GemstoneCublasStatus status = gemstone_cublas_copy_in(a_host, a, a_size * sizeof(*a_host));
  if (status == GEMSTONE_CUBLAS_SUCCESS) status = gemstone_cublas_copy_in(b_host, b, b_size * sizeof(*b_host));
  if (status == GEMSTONE_CUBLAS_SUCCESS) status = gemstone_cublas_copy_in(c_host, c, c_size * sizeof(*c_host));
  if (status == GEMSTONE_CUBLAS_SUCCESS) {
    const float scale_a = alpha == NULL ? 0.0f : gemstone_cublas_half_to_float(alpha->bits);
    const float scale_b = beta == NULL ? 0.0f : gemstone_cublas_half_to_float(beta->bits);
    for (int column = 0; column < n; ++column)
      for (int row = 0; row < m; ++row) {
        float sum = 0.0f;
        for (int inner = 0; inner < k; ++inner) {
          const GemstoneCublasHalf value_a = transpose_a == GEMSTONE_CUBLAS_OP_N
                                                  ? a_host[row + inner * lda]
                                                  : a_host[inner + row * lda];
          const GemstoneCublasHalf value_b = transpose_b == GEMSTONE_CUBLAS_OP_N
                                                  ? b_host[inner + column * ldb]
                                                  : b_host[column + inner * ldb];
          sum += gemstone_cublas_half_to_float(value_a.bits) * gemstone_cublas_half_to_float(value_b.bits);
        }
        const float existing = gemstone_cublas_half_to_float(c_host[row + column * ldc].bits);
        c_host[row + column * ldc].bits = gemstone_cublas_float_to_half(
            scale_a * sum + (scale_b == 0.0f ? 0.0f : scale_b * existing));
      }
    status = gemstone_cublas_copy_out(c, c_host, c_size * sizeof(*c_host));
  }
  free(a_host);
  free(b_host);
  free(c_host);
  return status;
}

int cublasHgemm(
    GemstoneCublasHandle handle,
    int transpose_a,
    int transpose_b,
    int m,
    int n,
    int k,
    const GemstoneCublasHalf *alpha,
    const GemstoneCublasHalf *a,
    int lda,
    const GemstoneCublasHalf *b,
    int ldb,
    const GemstoneCublasHalf *beta,
    GemstoneCublasHalf *c,
    int ldc) {
  return gemstone_cublas_hgemm(handle, transpose_a, transpose_b, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
}

int cublasHgemmStridedBatched(
    GemstoneCublasHandle handle,
    int transpose_a,
    int transpose_b,
    int m,
    int n,
    int k,
    const GemstoneCublasHalf *alpha,
    const GemstoneCublasHalf *a,
    int lda,
    long long stride_a,
    const GemstoneCublasHalf *b,
    int ldb,
    long long stride_b,
    const GemstoneCublasHalf *beta,
    GemstoneCublasHalf *c,
    int ldc,
    long long stride_c,
    int batch_count) {
  if (batch_count < 0) return GEMSTONE_CUBLAS_INVALID_VALUE;
  for (int batch = 0; batch < batch_count; ++batch) {
    const GemstoneCublasHalf *a_batch = (const GemstoneCublasHalf *)((uintptr_t)a + (uintptr_t)(batch * stride_a * 2));
    const GemstoneCublasHalf *b_batch = (const GemstoneCublasHalf *)((uintptr_t)b + (uintptr_t)(batch * stride_b * 2));
    GemstoneCublasHalf *c_batch = (GemstoneCublasHalf *)((uintptr_t)c + (uintptr_t)(batch * stride_c * 2));
    const GemstoneCublasStatus status = gemstone_cublas_hgemm(handle, transpose_a, transpose_b, m, n, k, alpha, a_batch,
                                                               lda, b_batch, ldb, beta, c_batch, ldc);
    if (status != GEMSTONE_CUBLAS_SUCCESS) return status;
  }
  return GEMSTONE_CUBLAS_SUCCESS;
}

#define GEMSTONE_CUBLAS_GEMM(type, name)                                                   \
  int name(                                                                                 \
      GemstoneCublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k,  \
      const type *alpha, const type *a, int lda, const type *b, int ldb,                    \
      const type *beta, type *c, int ldc) {                                                  \
    if (handle == NULL) return GEMSTONE_CUBLAS_NOT_INITIALIZED;                            \
    if (m < 0 || n < 0 || k < 0 ||                                                        \
        lda < (transpose_a == GEMSTONE_CUBLAS_OP_N ? m : k) ||                             \
        ldb < (transpose_b == GEMSTONE_CUBLAS_OP_N ? k : n) || ldc < m)                    \
      return GEMSTONE_CUBLAS_INVALID_VALUE;                                                \
    const size_t a_size = (size_t)lda * (transpose_a == GEMSTONE_CUBLAS_OP_N ? (size_t)k : (size_t)m); \
    const size_t b_size = (size_t)ldb * (transpose_b == GEMSTONE_CUBLAS_OP_N ? (size_t)n : (size_t)k); \
    const size_t c_size = (size_t)ldc * (size_t)n;                                          \
    type *a_host = (type *)malloc(a_size * sizeof(type));                                  \
    type *b_host = (type *)malloc(b_size * sizeof(type));                                  \
    type *c_host = (type *)malloc(c_size * sizeof(type));                                  \
    if (a_host == NULL || b_host == NULL || c_host == NULL) {                              \
      free(a_host);                                                                         \
      free(b_host);                                                                         \
      free(c_host);                                                                         \
      return GEMSTONE_CUBLAS_ALLOC_FAILED;                                                  \
    }                                                                                       \
    GemstoneCublasStatus status = gemstone_cublas_copy_in(a_host, a, a_size * sizeof(type)); \
    if (status == GEMSTONE_CUBLAS_SUCCESS) status = gemstone_cublas_copy_in(b_host, b, b_size * sizeof(type)); \
    if (status == GEMSTONE_CUBLAS_SUCCESS) status = gemstone_cublas_copy_in(c_host, c, c_size * sizeof(type)); \
    if (status == GEMSTONE_CUBLAS_SUCCESS) {                                               \
      const type scale_a = alpha == NULL ? (type)0 : *alpha;                               \
      const type scale_b = beta == NULL ? (type)0 : *beta;                                 \
      for (int column = 0; column < n; ++column)                                           \
        for (int row = 0; row < m; ++row) {                                                 \
          type sum = (type)0;                                                              \
          for (int inner = 0; inner < k; ++inner) {                                         \
            const type value_a = transpose_a == GEMSTONE_CUBLAS_OP_N                    \
                                     ? a_host[row + inner * lda]                           \
                                     : a_host[inner + row * lda];                           \
            const type value_b = transpose_b == GEMSTONE_CUBLAS_OP_N                    \
                                     ? b_host[inner + column * ldb]                        \
                                     : b_host[column + inner * ldb];                       \
            sum += value_a * value_b;                                                      \
          }                                                                                \
          c_host[row + column * ldc] = scale_a * sum +                                    \
                                       (scale_b == (type)0 ? (type)0 :                     \
                                        scale_b * c_host[row + column * ldc]);              \
        }                                                                                  \
      status = gemstone_cublas_copy_out(c, c_host, c_size * sizeof(type));                 \
    }                                                                                       \
    free(a_host);                                                                           \
    free(b_host);                                                                           \
    free(c_host);                                                                           \
    return status;                                                                          \
  }

GEMSTONE_CUBLAS_GEMM(float, cublasSgemm_v2)
GEMSTONE_CUBLAS_GEMM(double, cublasDgemm_v2)

#define GEMSTONE_CUBLAS_GEMM_BATCHED(type, name, gemm_name)                                \
  int name(                                                                                 \
      GemstoneCublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k,  \
      const type *alpha, const type *a, int lda, long long stride_a,                        \
      const type *b, int ldb, long long stride_b, const type *beta, type *c, int ldc,        \
      long long stride_c, int batch_count) {                                                 \
    if (batch_count < 0) return GEMSTONE_CUBLAS_INVALID_VALUE;                             \
    for (int batch = 0; batch < batch_count; ++batch) {                                    \
      const uintptr_t a_address = (uintptr_t)a + (uintptr_t)batch * (uintptr_t)stride_a * sizeof(type); \
      const uintptr_t b_address = (uintptr_t)b + (uintptr_t)batch * (uintptr_t)stride_b * sizeof(type); \
      const uintptr_t c_address = (uintptr_t)c + (uintptr_t)batch * (uintptr_t)stride_c * sizeof(type); \
      const int status = gemm_name(                                                        \
          handle, transpose_a, transpose_b, m, n, k, alpha, (const type *)a_address, lda, \
          (const type *)b_address, ldb, beta, (type *)c_address, ldc);                     \
      if (status != GEMSTONE_CUBLAS_SUCCESS) return status;                                \
    }                                                                                       \
    return GEMSTONE_CUBLAS_SUCCESS;                                                        \
  }

GEMSTONE_CUBLAS_GEMM_BATCHED(float, cublasSgemmStridedBatched, cublasSgemm_v2)
GEMSTONE_CUBLAS_GEMM_BATCHED(double, cublasDgemmStridedBatched, cublasDgemm_v2)
