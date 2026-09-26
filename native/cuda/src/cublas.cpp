#include <cstring>
#include <memory>
#include <new>
#include <optional>
#include <vector>

#include <stdint.h>

#include "gemstone_cuda_constants.hpp"
#include "gemstone_execution_rpc.hpp"
#include <gemstone/export.h>

/*
 * cuBLAS is a host-side library. Its CUDA 13 binary normally discovers a
 * hardware-specific dispatch catalogue and refuses to initialise when the
 * driver cannot load its private cubins. Gemstone exposes this small,
 * versioned compatibility surface instead: the provider keeps the vendor ABI
 * and device allocations intact, resolves each operand to its server-side
 * allocation, and forwards the operation to the execution service.
 */

extern "C" int32_t cuInit(unsigned int flags);
extern "C" int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size);
extern "C" int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size);
extern "C" int32_t gemstoneCudaPointerBinding(uint64_t pointer, uint64_t *allocation, uint64_t *offset) noexcept;
extern "C" int32_t gemstoneCudaBlas(const gemstone::execution::BlasRequest &request, gemstone::execution::BlasResult &result) noexcept;

namespace {

using namespace gemstone::cuda::api;
using namespace gemstone::execution;

// CUBLAS_VERSION from the pinned cublas_api.h (13.6.0).
constexpr int32_t CublasVersion = 130600;

using Status = int32_t;
struct CublasHandleState {
  int32_t pointerMode = 0;
  void *stream = nullptr;
};
using CublasHandle = CublasHandleState *;

struct CublasHalf {
  uint16_t bits;
};

template <class T> BlasElement elementOf();
template <> BlasElement elementOf<float>() { return BlasElement::F32; }
template <> BlasElement elementOf<double>() { return BlasElement::F64; }
template <> BlasElement elementOf<CublasHalf>() { return BlasElement::F16; }

/* Scalar location follows the handle mode, never the pointer's numeric range. */
Status scalarIn(CublasHandle handle, void *host, const void *pointer, size_t size) {
  if (!pointer) return CUBLAS_STATUS_INVALID_VALUE;
  if (handle->pointerMode == CUBLAS_POINTER_MODE_DEVICE)
    return cuMemcpyDtoH_v2(host, static_cast<uint64_t>(reinterpret_cast<uintptr_t>(pointer)), size);
  std::memcpy(host, pointer, size);
  return CUBLAS_STATUS_SUCCESS;
}

Status scalarOut(CublasHandle handle, void *pointer, const void *host, size_t size) {
  if (!pointer) return CUBLAS_STATUS_INVALID_VALUE;
  if (handle->pointerMode == CUBLAS_POINTER_MODE_DEVICE)
    return cuMemcpyHtoD_v2(static_cast<uint64_t>(reinterpret_cast<uintptr_t>(pointer)), host, size);
  std::memcpy(pointer, host, size);
  return CUBLAS_STATUS_SUCCESS;
}

std::optional<std::vector<uint8_t>> readScalar(CublasHandle handle, const void *pointer, size_t size) {
  if (!pointer) return std::nullopt;
  std::vector<uint8_t> bytes(size);
  if (scalarIn(handle, bytes.data(), pointer, size) != CUBLAS_STATUS_SUCCESS) return std::nullopt;
  return bytes;
}

Status writeScalar(CublasHandle handle, void *pointer, const std::vector<uint8_t> &bytes) {
  if (!pointer) return CUBLAS_STATUS_INVALID_VALUE;
  return scalarOut(handle, pointer, bytes.data(), bytes.size());
}

std::optional<BlasVector> vectorOf(const void *pointer, int increment) {
  if (!pointer) return std::nullopt;
  uint64_t allocation = 0;
  uint64_t offset = 0;
  if (gemstoneCudaPointerBinding(reinterpret_cast<uint64_t>(pointer), &allocation, &offset) != 0) return std::nullopt;
  return BlasVector{AllocationId{static_cast<int64_t>(allocation)}, LongPositive0{static_cast<int64_t>(offset)}, increment};
}

std::optional<BlasMatrix> matrixOf(const void *pointer, int leading) {
  if (!pointer) return std::nullopt;
  uint64_t allocation = 0;
  uint64_t offset = 0;
  if (gemstoneCudaPointerBinding(reinterpret_cast<uint64_t>(pointer), &allocation, &offset) != 0) return std::nullopt;
  return BlasMatrix{AllocationId{static_cast<int64_t>(allocation)}, LongPositive0{static_cast<int64_t>(offset)}, leading};
}

Status forward(const BlasRequest &request, BlasResult &result) {
  return gemstoneCudaBlas(request, result) == CUDA_SUCCESS ? CUBLAS_STATUS_SUCCESS : CUBLAS_STATUS_EXECUTION_FAILED;
}

Status validVector(CublasHandle handle, int count, int increment) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (count < 0 || increment == 0) return CUBLAS_STATUS_INVALID_VALUE;
  return CUBLAS_STATUS_SUCCESS;
}

template <class T> Status axpy(CublasHandle handle, int count, const T *alpha, const T *x, int increment_x, T *y, int increment_y) {
  Status status = validVector(handle, count, increment_x);
  if (status != CUBLAS_STATUS_SUCCESS || increment_y == 0) return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;
  if (count == 0) return CUBLAS_STATUS_SUCCESS;
  const auto xv = vectorOf(x, increment_x);
  const auto yv = vectorOf(y, increment_y);
  const auto scale = readScalar(handle, alpha, sizeof(T));
  if (!xv || !yv || !scale) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::Axpy;
  request.element = elementOf<T>();
  request.count = count;
  request.alpha = *scale;
  request.x = xv;
  request.y = yv;
  BlasResult result{};
  return forward(request, result);
}

template <class T> Status scal(CublasHandle handle, int count, const T *alpha, T *x, int increment) {
  Status status = validVector(handle, count, increment);
  if (status != CUBLAS_STATUS_SUCCESS) return status;
  if (count == 0) return CUBLAS_STATUS_SUCCESS;
  const auto xv = vectorOf(x, increment);
  const auto scale = readScalar(handle, alpha, sizeof(T));
  if (!xv || !scale) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::Scal;
  request.element = elementOf<T>();
  request.count = count;
  request.alpha = *scale;
  request.x = xv;
  BlasResult result{};
  return forward(request, result);
}

template <class T> Status copy(CublasHandle handle, int count, const T *x, int increment_x, T *y, int increment_y) {
  Status status = validVector(handle, count, increment_x);
  if (status != CUBLAS_STATUS_SUCCESS || increment_y == 0) return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;
  if (count == 0) return CUBLAS_STATUS_SUCCESS;
  const auto xv = vectorOf(x, increment_x);
  const auto yv = vectorOf(y, increment_y);
  if (!xv || !yv) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::Copy;
  request.element = elementOf<T>();
  request.count = count;
  request.x = xv;
  request.y = yv;
  BlasResult result{};
  return forward(request, result);
}

template <class T> Status dot(CublasHandle handle, int count, const T *x, int increment_x, const T *y, int increment_y, T *result) {
  Status status = validVector(handle, count, increment_x);
  if (status != CUBLAS_STATUS_SUCCESS || increment_y == 0) return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;
  if (!result) return CUBLAS_STATUS_INVALID_VALUE;
  const auto xv = vectorOf(x, increment_x);
  const auto yv = vectorOf(y, increment_y);
  if (!xv || !yv) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::Dot;
  request.element = elementOf<T>();
  request.count = count;
  request.x = xv;
  request.y = yv;
  BlasResult response{};
  status = forward(request, response);
  return status == CUBLAS_STATUS_SUCCESS ? writeScalar(handle, result, response.scalar) : status;
}

template <class T> Status norm(CublasHandle handle, int count, const T *x, int increment, T *result) {
  Status status = validVector(handle, count, increment);
  if (status != CUBLAS_STATUS_SUCCESS || !result) return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;
  const auto xv = vectorOf(x, increment);
  if (!xv) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::Nrm2;
  request.element = elementOf<T>();
  request.count = count;
  request.x = xv;
  BlasResult response{};
  status = forward(request, response);
  return status == CUBLAS_STATUS_SUCCESS ? writeScalar(handle, result, response.scalar) : status;
}

template <class T> Status absoluteSum(CublasHandle handle, int count, const T *x, int increment, T *result) {
  Status status = validVector(handle, count, increment);
  if (status != CUBLAS_STATUS_SUCCESS || !result) return status != CUBLAS_STATUS_SUCCESS ? status : CUBLAS_STATUS_INVALID_VALUE;
  const auto xv = vectorOf(x, increment);
  if (!xv) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::Asum;
  request.element = elementOf<T>();
  request.count = count;
  request.x = xv;
  BlasResult response{};
  status = forward(request, response);
  return status == CUBLAS_STATUS_SUCCESS ? writeScalar(handle, result, response.scalar) : status;
}

template <class T>
Status gemv(CublasHandle handle, int transpose, int rows, int columns, const T *alpha, const T *a, int lda, const T *x, int increment_x,
            const T *beta, T *y, int increment_y) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (rows < 0 || columns < 0 || lda < rows || increment_x == 0 || increment_y == 0) return CUBLAS_STATUS_INVALID_VALUE;
  const auto av = matrixOf(a, lda);
  const auto xv = vectorOf(x, increment_x);
  const auto yv = vectorOf(y, increment_y);
  const auto scaleA = readScalar(handle, alpha, sizeof(T));
  const auto scaleB = readScalar(handle, beta, sizeof(T));
  if (!av || !xv || !yv || !scaleA || !scaleB) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::Gemv;
  request.element = elementOf<T>();
  request.rows = rows;
  request.columns = columns;
  request.transposeA = transpose != CUBLAS_OP_N;
  request.alpha = *scaleA;
  request.beta = *scaleB;
  request.a = av;
  request.x = xv;
  request.y = yv;
  BlasResult result{};
  return forward(request, result);
}

template <class T>
Status gemm(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k, const T *alpha, const T *a, int lda, const T *b,
            int ldb, const T *beta, T *c, int ldc) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (m < 0 || n < 0 || k < 0 || lda < (transpose_a == CUBLAS_OP_N ? m : k) || ldb < (transpose_b == CUBLAS_OP_N ? k : n) || ldc < m)
    return CUBLAS_STATUS_INVALID_VALUE;
  const auto av = matrixOf(a, lda);
  const auto bv = matrixOf(b, ldb);
  const auto cv = matrixOf(c, ldc);
  const auto scaleA = readScalar(handle, alpha, sizeof(T));
  const auto scaleB = readScalar(handle, beta, sizeof(T));
  if (!av || !bv || !cv || !scaleA || !scaleB) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::Gemm;
  request.element = elementOf<T>();
  request.rows = m;
  request.columns = n;
  request.inner = k;
  request.transposeA = transpose_a != CUBLAS_OP_N;
  request.transposeB = transpose_b != CUBLAS_OP_N;
  request.alpha = *scaleA;
  request.beta = *scaleB;
  request.a = av;
  request.b = bv;
  request.c = cv;
  BlasResult result{};
  return forward(request, result);
}

template <class T>
Status gemmStridedBatched(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k, const T *alpha, const T *a, int lda,
                          long long stride_a, const T *b, int ldb, long long stride_b, const T *beta, T *c, int ldc, long long stride_c,
                          int batch_count) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (batch_count < 0) return CUBLAS_STATUS_INVALID_VALUE;
  if (m < 0 || n < 0 || k < 0 || lda < (transpose_a == CUBLAS_OP_N ? m : k) || ldb < (transpose_b == CUBLAS_OP_N ? k : n) || ldc < m)
    return CUBLAS_STATUS_INVALID_VALUE;
  const auto av = matrixOf(a, lda);
  const auto bv = matrixOf(b, ldb);
  const auto cv = matrixOf(c, ldc);
  const auto scaleA = readScalar(handle, alpha, sizeof(T));
  const auto scaleB = readScalar(handle, beta, sizeof(T));
  if (!av || !bv || !cv || !scaleA || !scaleB) return CUBLAS_STATUS_INVALID_VALUE;
  BlasRequest request{};
  request.operation = BlasOperation::GemmStridedBatched;
  request.element = elementOf<T>();
  request.rows = m;
  request.columns = n;
  request.inner = k;
  request.transposeA = transpose_a != CUBLAS_OP_N;
  request.transposeB = transpose_b != CUBLAS_OP_N;
  request.alpha = *scaleA;
  request.beta = *scaleB;
  request.a = av;
  request.b = bv;
  request.c = cv;
  request.strideA = static_cast<int64_t>(stride_a);
  request.strideB = static_cast<int64_t>(stride_b);
  request.strideC = static_cast<int64_t>(stride_c);
  request.batch = batch_count;
  BlasResult result{};
  return forward(request, result);
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
  *version = CublasVersion;
  return CUBLAS_STATUS_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int cublasSetPointerMode_v2(CublasHandle handle, int mode) {
  if (!handle) return CUBLAS_STATUS_NOT_INITIALIZED;
  if (mode != CUBLAS_POINTER_MODE_HOST && mode != CUBLAS_POINTER_MODE_DEVICE) return CUBLAS_STATUS_INVALID_VALUE;
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
    return axpy(handle, count, alpha, x, increment_x, y, increment_y);                                                                     \
  }

GEMSTONE_CUBLAS_AXPY(float, cublasSaxpy_v2)
GEMSTONE_CUBLAS_AXPY(double, cublasDaxpy_v2)

#define GEMSTONE_CUBLAS_SCAL(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *alpha, type *x, int increment) {                                         \
    return scal(handle, count, alpha, x, increment);                                                                                       \
  }

GEMSTONE_CUBLAS_SCAL(float, cublasSscal_v2)
GEMSTONE_CUBLAS_SCAL(double, cublasDscal_v2)

#define GEMSTONE_CUBLAS_COPY(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *x, int increment_x, type *y, int increment_y) {                          \
    return copy(handle, count, x, increment_x, y, increment_y);                                                                            \
  }

GEMSTONE_CUBLAS_COPY(float, cublasScopy_v2)
GEMSTONE_CUBLAS_COPY(double, cublasDcopy_v2)

#define GEMSTONE_CUBLAS_DOT(type, name)                                                                                                    \
  extern "C" int name(CublasHandle handle, int count, const type *x, int increment_x, const type *y, int increment_y, type *result) {      \
    return dot(handle, count, x, increment_x, y, increment_y, result);                                                                     \
  }

GEMSTONE_CUBLAS_DOT(float, cublasSdot_v2)
GEMSTONE_CUBLAS_DOT(double, cublasDdot_v2)

#define GEMSTONE_CUBLAS_NRM2(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *x, int increment, type *result) {                                        \
    return norm(handle, count, x, increment, result);                                                                                      \
  }

GEMSTONE_CUBLAS_NRM2(float, cublasSnrm2_v2)
GEMSTONE_CUBLAS_NRM2(double, cublasDnrm2_v2)

#define GEMSTONE_CUBLAS_ASUM(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int count, const type *x, int increment, type *result) {                                        \
    return absoluteSum(handle, count, x, increment, result);                                                                               \
  }

GEMSTONE_CUBLAS_ASUM(float, cublasSasum_v2)
GEMSTONE_CUBLAS_ASUM(double, cublasDasum_v2)

#define GEMSTONE_CUBLAS_GEMV(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int transpose, int rows, int columns, const type *alpha, const type *a, int lda, const type *x, \
                      int increment_x, const type *beta, type *y, int increment_y) {                                                       \
    return gemv(handle, transpose, rows, columns, alpha, a, lda, x, increment_x, beta, y, increment_y);                                    \
  }

GEMSTONE_CUBLAS_GEMV(float, cublasSgemv_v2)
GEMSTONE_CUBLAS_GEMV(double, cublasDgemv_v2)

#define GEMSTONE_CUBLAS_GEMM(type, name)                                                                                                   \
  extern "C" int name(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k, const type *alpha, const type *a,        \
                      int lda, const type *b, int ldb, const type *beta, type *c, int ldc) {                                               \
    return gemm(handle, transpose_a, transpose_b, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);                                           \
  }

GEMSTONE_CUBLAS_GEMM(float, cublasSgemm_v2)
GEMSTONE_CUBLAS_GEMM(double, cublasDgemm_v2)

extern "C" GEMSTONE_PUBLIC_API int cublasHgemm(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k,
                                               const CublasHalf *alpha, const CublasHalf *a, int lda, const CublasHalf *b, int ldb,
                                               const CublasHalf *beta, CublasHalf *c, int ldc) {
  return gemm(handle, transpose_a, transpose_b, m, n, k, alpha, a, lda, b, ldb, beta, c, ldc);
}

#define GEMSTONE_CUBLAS_GEMM_BATCHED(type, name)                                                                                           \
  extern "C" int name(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k, const type *alpha, const type *a,        \
                      int lda, long long stride_a, const type *b, int ldb, long long stride_b, const type *beta, type *c, int ldc,         \
                      long long stride_c, int batch_count) {                                                                               \
    return gemmStridedBatched(handle, transpose_a, transpose_b, m, n, k, alpha, a, lda, stride_a, b, ldb, stride_b, beta, c, ldc,          \
                              stride_c, batch_count);                                                                                      \
  }

GEMSTONE_CUBLAS_GEMM_BATCHED(float, cublasSgemmStridedBatched)
GEMSTONE_CUBLAS_GEMM_BATCHED(double, cublasDgemmStridedBatched)

extern "C" GEMSTONE_PUBLIC_API int cublasHgemmStridedBatched(CublasHandle handle, int transpose_a, int transpose_b, int m, int n, int k,
                                                             const CublasHalf *alpha, const CublasHalf *a, int lda, long long stride_a,
                                                             const CublasHalf *b, int ldb, long long stride_b, const CublasHalf *beta,
                                                             CublasHalf *c, int ldc, long long stride_c, int batch_count) {
  return gemmStridedBatched(handle, transpose_a, transpose_b, m, n, k, alpha, a, lda, stride_a, b, ldb, stride_b, beta, c, ldc, stride_c,
                            batch_count);
}
