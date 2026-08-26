#include "gemsi_cuda_rpc.inc"
#include "gemsi_protocol.h"
#include "gemsi_rpc_transport.h"

#include <limits.h>
#include <pthread.h>
#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

static const uint64_t gemsi_cuCtxCreate_v2_high = UINT64_C(0xe6eb5473c0a097e3);
static const uint64_t gemsi_cuCtxCreate_v2_low = UINT64_C(0xa17c8d63cec75a44);
static const uint64_t gemsi_cuCtxDestroy_v2_high = UINT64_C(0xb574aacc9b35ee6a);
static const uint64_t gemsi_cuCtxDestroy_v2_low = UINT64_C(0x8135b883071cc261);
static const uint64_t gemsi_cuCtxSynchronize_high = UINT64_C(0xe21f80c194a589d4);
static const uint64_t gemsi_cuCtxSynchronize_low = UINT64_C(0x4af681a7ef75bb10);
static const uint64_t gemsi_cuDeviceGet_high = UINT64_C(0xc91170009b2ba6c9);
static const uint64_t gemsi_cuDeviceGet_low = UINT64_C(0x263d0d32e04e06a7);
static const uint64_t gemsi_cuDeviceGetCount_high = UINT64_C(0x831f6fffdf16f0d4);
static const uint64_t gemsi_cuDeviceGetCount_low = UINT64_C(0x3c498239d16d9867);
static const uint64_t gemsi_cuDriverGetVersion_high = UINT64_C(0xe584ec56943a425d);
static const uint64_t gemsi_cuDriverGetVersion_low = UINT64_C(0x3c512e60803234f9);
static const uint64_t gemsi_cuInit_high = UINT64_C(0x3c1688958f08660e);
static const uint64_t gemsi_cuInit_low = UINT64_C(0x1c2a40d0799c268c);
static const uint64_t gemsi_cuLaunchKernel_high = UINT64_C(0x0541d9142559d437);
static const uint64_t gemsi_cuLaunchKernel_low = UINT64_C(0x4cd345cbce10e2df);
static const uint64_t gemsi_cuMemAlloc_v2_high = UINT64_C(0x7077a5441f532f0e);
static const uint64_t gemsi_cuMemAlloc_v2_low = UINT64_C(0x677e154cf996113d);
static const uint64_t gemsi_cuMemFree_v2_high = UINT64_C(0xb5fdc569871ec54e);
static const uint64_t gemsi_cuMemFree_v2_low = UINT64_C(0x418d186e2d8efa3a);
static const uint64_t gemsi_cuMemcpyDtoD_v2_high = UINT64_C(0xe27c11476bbea751);
static const uint64_t gemsi_cuMemcpyDtoD_v2_low = UINT64_C(0x681167b2aac69936);
static const uint64_t gemsi_cuMemcpyDtoH_v2_high = UINT64_C(0x0cc3708077b8e76a);
static const uint64_t gemsi_cuMemcpyDtoH_v2_low = UINT64_C(0xaa0bf9a6432ca141);
static const uint64_t gemsi_cuMemcpyHtoD_v2_high = UINT64_C(0x9a8f59036790ef69);
static const uint64_t gemsi_cuMemcpyHtoD_v2_low = UINT64_C(0x4808ed612739b8fa);
static const uint64_t gemsi_cuMemsetD8_v2_high = UINT64_C(0x2c1383458eec311a);
static const uint64_t gemsi_cuMemsetD8_v2_low = UINT64_C(0x73e621c93fd80644);
static const uint64_t gemsi_cuModuleGetFunction_high = UINT64_C(0x4b6b65a1226b0723);
static const uint64_t gemsi_cuModuleGetFunction_low = UINT64_C(0xc3ac0d7e32b1d4b5);
static const uint64_t gemsi_cuModuleLoadData_high = UINT64_C(0xfef489c6c628b458);
static const uint64_t gemsi_cuModuleLoadData_low = UINT64_C(0x480e5cb6fe964d3f);
static const uint64_t gemsi_cuModuleUnload_high = UINT64_C(0xb94c365c6c5c3902);
static const uint64_t gemsi_cuModuleUnload_low = UINT64_C(0x2a1466600700709a);

static void gemsi_put_u32(uint8_t *destination, uint32_t value) {
  destination[0] = (uint8_t)(value >> 24);
  destination[1] = (uint8_t)(value >> 16);
  destination[2] = (uint8_t)(value >> 8);
  destination[3] = (uint8_t)value;
}

static void gemsi_put_u64(uint8_t *destination, uint64_t value) {
  gemsi_put_u32(destination, (uint32_t)(value >> 32));
  gemsi_put_u32(destination + 4, (uint32_t)value);
}

static uint32_t gemsi_get_u32(const uint8_t *source) {
  return ((uint32_t)source[0] << 24) | ((uint32_t)source[1] << 16) | ((uint32_t)source[2] << 8) | (uint32_t)source[3];
}

static uint64_t gemsi_get_u64(const uint8_t *source) {
  return ((uint64_t)gemsi_get_u32(source) << 32) | gemsi_get_u32(source + 4);
}

static size_t gemsi_bounded_string_length(const char *value, size_t limit) {
  size_t size = 0;
  while (size < limit && value[size] != '\0') ++size;
  return size;
}

typedef struct GemsiKernelSignature {
  uint64_t function;
  uint64_t module;
  uint32_t count;
  uint8_t *widths;
  struct GemsiKernelSignature *next;
} GemsiKernelSignature;

static pthread_mutex_t gemsi_kernel_mutex = PTHREAD_MUTEX_INITIALIZER;
static GemsiKernelSignature *gemsi_kernel_signatures = NULL;

static int gemsi_store_kernel_signature(
    uint64_t function,
    uint64_t module,
    const uint8_t *widths,
    uint32_t count) {
  GemsiKernelSignature *entry = (GemsiKernelSignature *)calloc(1, sizeof(*entry));
  if (entry == NULL) return -1;
  if (count != 0) {
    entry->widths = (uint8_t *)malloc(count);
    if (entry->widths == NULL) { free(entry); return -1; }
    memcpy(entry->widths, widths, count);
  }
  entry->function = function;
  entry->module = module;
  entry->count = count;
  pthread_mutex_lock(&gemsi_kernel_mutex);
  entry->next = gemsi_kernel_signatures;
  gemsi_kernel_signatures = entry;
  pthread_mutex_unlock(&gemsi_kernel_mutex);
  return 0;
}

static void gemsi_forget_module(uint64_t module) {
  pthread_mutex_lock(&gemsi_kernel_mutex);
  GemsiKernelSignature **cursor = &gemsi_kernel_signatures;
  while (*cursor != NULL) {
    GemsiKernelSignature *entry = *cursor;
    if (entry->module == module) {
      *cursor = entry->next;
      free(entry->widths);
      free(entry);
    } else cursor = &entry->next;
  }
  pthread_mutex_unlock(&gemsi_kernel_mutex);
}

static int32_t gemsi_call(
    uint64_t high,
    uint64_t low,
    const char *name,
    const uint8_t *payload,
    uint32_t payload_size,
    GemsiRpcBuffer *response) {
  int32_t status = GEMSI_STATUS_INITIALIZATION_ERROR;
  if (gemsi_rpc_call(high, low, name, payload, payload_size, &status, response) != 0) return GEMSI_STATUS_INITIALIZATION_ERROR;
  if (status != GEMSI_STATUS_SUCCESS) gemsi_rpc_buffer_release(response);
  return status;
}

static int32_t gemsi_expect_unit(int32_t status, GemsiRpcBuffer *response) {
  if (status == GEMSI_STATUS_SUCCESS && response->size != 0) status = GEMSI_STATUS_UNKNOWN;
  gemsi_rpc_buffer_release(response);
  return status;
}

static int32_t gemsi_expect_u32(int32_t status, GemsiRpcBuffer *response, uint32_t *value) {
  if (status == GEMSI_STATUS_SUCCESS) {
    if (response->size != 4 || value == NULL) status = GEMSI_STATUS_UNKNOWN;
    else *value = gemsi_get_u32(response->data);
  }
  gemsi_rpc_buffer_release(response);
  return status;
}

static int32_t gemsi_expect_u64(int32_t status, GemsiRpcBuffer *response, uint64_t *value) {
  if (status == GEMSI_STATUS_SUCCESS) {
    if (response->size != 8 || value == NULL) status = GEMSI_STATUS_UNKNOWN;
    else *value = gemsi_get_u64(response->data);
  }
  gemsi_rpc_buffer_release(response);
  return status;
}

int32_t cuInit(uint32_t flags) {
  uint8_t payload[4];
  gemsi_put_u32(payload, flags);
  GemsiRpcBuffer response = {0};
  return gemsi_expect_unit(gemsi_call(gemsi_cuInit_high, gemsi_cuInit_low, "cuInit", payload, sizeof(payload), &response), &response);
}

int32_t cuDriverGetVersion(int32_t *version) {
  if (version == NULL) return GEMSI_STATUS_INVALID_VALUE;
  GemsiRpcBuffer response = {0};
  return gemsi_expect_u32(gemsi_call(gemsi_cuDriverGetVersion_high, gemsi_cuDriverGetVersion_low, "cuDriverGetVersion", NULL, 0, &response), &response, (uint32_t *)version);
}

int32_t cuDeviceGetCount(int32_t *count) {
  if (count == NULL) return GEMSI_STATUS_INVALID_VALUE;
  GemsiRpcBuffer response = {0};
  return gemsi_expect_u32(gemsi_call(gemsi_cuDeviceGetCount_high, gemsi_cuDeviceGetCount_low, "cuDeviceGetCount", NULL, 0, &response), &response, (uint32_t *)count);
}

int32_t cuDeviceGet(int32_t *device, int32_t ordinal) {
  if (device == NULL) return GEMSI_STATUS_INVALID_VALUE;
  uint8_t payload[4];
  gemsi_put_u32(payload, (uint32_t)ordinal);
  GemsiRpcBuffer response = {0};
  return gemsi_expect_u32(gemsi_call(gemsi_cuDeviceGet_high, gemsi_cuDeviceGet_low, "cuDeviceGet", payload, sizeof(payload), &response), &response, (uint32_t *)device);
}

int32_t cuCtxCreate_v2(void **context, uint32_t flags, int32_t device) {
  if (context == NULL) return GEMSI_STATUS_INVALID_VALUE;
  uint8_t payload[8];
  gemsi_put_u32(payload, flags);
  gemsi_put_u32(payload + 4, (uint32_t)device);
  uint64_t handle = 0;
  GemsiRpcBuffer response = {0};
  const int32_t status = gemsi_expect_u64(gemsi_call(gemsi_cuCtxCreate_v2_high, gemsi_cuCtxCreate_v2_low, "cuCtxCreate_v2", payload, sizeof(payload), &response), &response, &handle);
  if (status == GEMSI_STATUS_SUCCESS) *context = (void *)(uintptr_t)handle;
  return status;
}

int32_t cuCtxDestroy_v2(void *context) {
  uint8_t payload[8];
  gemsi_put_u64(payload, (uint64_t)(uintptr_t)context);
  GemsiRpcBuffer response = {0};
  return gemsi_expect_unit(gemsi_call(gemsi_cuCtxDestroy_v2_high, gemsi_cuCtxDestroy_v2_low, "cuCtxDestroy_v2", payload, sizeof(payload), &response), &response);
}

int32_t cuCtxSynchronize(void) {
  GemsiRpcBuffer response = {0};
  return gemsi_expect_unit(gemsi_call(gemsi_cuCtxSynchronize_high, gemsi_cuCtxSynchronize_low, "cuCtxSynchronize", NULL, 0, &response), &response);
}

int32_t cuMemAlloc_v2(uint64_t *pointer, size_t size) {
  if (pointer == NULL) return GEMSI_STATUS_INVALID_VALUE;
  uint8_t payload[8];
  gemsi_put_u64(payload, (uint64_t)size);
  GemsiRpcBuffer response = {0};
  return gemsi_expect_u64(gemsi_call(gemsi_cuMemAlloc_v2_high, gemsi_cuMemAlloc_v2_low, "cuMemAlloc_v2", payload, sizeof(payload), &response), &response, pointer);
}

int32_t cuMemFree_v2(uint64_t pointer) {
  uint8_t payload[8];
  gemsi_put_u64(payload, pointer);
  GemsiRpcBuffer response = {0};
  return gemsi_expect_unit(gemsi_call(gemsi_cuMemFree_v2_high, gemsi_cuMemFree_v2_low, "cuMemFree_v2", payload, sizeof(payload), &response), &response);
}

int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size) {
  if ((source == NULL && size != 0) || size > GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE - 12) return GEMSI_STATUS_INVALID_VALUE;
  const uint32_t payload_size = (uint32_t)size + 12;
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) return GEMSI_STATUS_UNKNOWN;
  gemsi_put_u64(payload, destination);
  gemsi_put_u32(payload + 8, (uint32_t)size);
  if (size != 0) memcpy(payload + 12, source, size);
  GemsiRpcBuffer response = {0};
  const int32_t status = gemsi_expect_unit(gemsi_call(gemsi_cuMemcpyHtoD_v2_high, gemsi_cuMemcpyHtoD_v2_low, "cuMemcpyHtoD_v2", payload, payload_size, &response), &response);
  free(payload);
  return status;
}

int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size) {
  if ((destination == NULL && size != 0) || size > GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE - 4) return GEMSI_STATUS_INVALID_VALUE;
  uint8_t payload[16];
  gemsi_put_u64(payload, source);
  gemsi_put_u64(payload + 8, (uint64_t)size);
  GemsiRpcBuffer response = {0};
  int32_t status = gemsi_call(gemsi_cuMemcpyDtoH_v2_high, gemsi_cuMemcpyDtoH_v2_low, "cuMemcpyDtoH_v2", payload, sizeof(payload), &response);
  if (status == GEMSI_STATUS_SUCCESS) {
    if (response.size < 4 || gemsi_get_u32(response.data) != size || response.size != size + 4) status = GEMSI_STATUS_UNKNOWN;
    else if (size != 0) memcpy(destination, response.data + 4, size);
  }
  gemsi_rpc_buffer_release(&response);
  return status;
}

int32_t cuMemcpyDtoD_v2(uint64_t destination, uint64_t source, size_t size) {
  uint8_t payload[24];
  gemsi_put_u64(payload, destination);
  gemsi_put_u64(payload + 8, source);
  gemsi_put_u64(payload + 16, (uint64_t)size);
  GemsiRpcBuffer response = {0};
  return gemsi_expect_unit(gemsi_call(gemsi_cuMemcpyDtoD_v2_high, gemsi_cuMemcpyDtoD_v2_low, "cuMemcpyDtoD_v2", payload, sizeof(payload), &response), &response);
}

int32_t cuMemsetD8_v2(uint64_t destination, uint8_t value, size_t count) {
  uint8_t payload[17];
  gemsi_put_u64(payload, destination);
  payload[8] = value;
  gemsi_put_u64(payload + 9, (uint64_t)count);
  GemsiRpcBuffer response = {0};
  return gemsi_expect_unit(gemsi_call(gemsi_cuMemsetD8_v2_high, gemsi_cuMemsetD8_v2_low, "cuMemsetD8_v2", payload, sizeof(payload), &response), &response);
}

int32_t cuModuleLoadData(void **module, const void *image) {
  if (module == NULL || image == NULL) return GEMSI_STATUS_INVALID_VALUE;
  const size_t size = gemsi_bounded_string_length((const char *)image, GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE);
  if (size == GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE || size > GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE - 4) {
    return GEMSI_STATUS_INVALID_VALUE;
  }
  const uint32_t payload_size = (uint32_t)size + 4;
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) return GEMSI_STATUS_UNKNOWN;
  gemsi_put_u32(payload, (uint32_t)size);
  memcpy(payload + 4, image, size);
  uint64_t handle = 0;
  GemsiRpcBuffer response = {0};
  const int32_t status = gemsi_expect_u64(
      gemsi_call(gemsi_cuModuleLoadData_high, gemsi_cuModuleLoadData_low, "cuModuleLoadData", payload, payload_size, &response),
      &response,
      &handle);
  free(payload);
  if (status == GEMSI_STATUS_SUCCESS) *module = (void *)(uintptr_t)handle;
  return status;
}

int32_t cuModuleGetFunction(void **function, void *module, const char *name) {
  if (function == NULL || module == NULL || name == NULL) return GEMSI_STATUS_INVALID_VALUE;
  const size_t name_size = gemsi_bounded_string_length(name, 4096);
  if (name_size == 0 || name_size == 4096) return GEMSI_STATUS_INVALID_VALUE;
  const uint32_t payload_size = (uint32_t)name_size + 12;
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) return GEMSI_STATUS_UNKNOWN;
  gemsi_put_u64(payload, (uint64_t)(uintptr_t)module);
  gemsi_put_u32(payload + 8, (uint32_t)name_size);
  memcpy(payload + 12, name, name_size);
  GemsiRpcBuffer response = {0};
  int32_t status = gemsi_call(
      gemsi_cuModuleGetFunction_high,
      gemsi_cuModuleGetFunction_low,
      "cuModuleGetFunction",
      payload,
      payload_size,
      &response);
  free(payload);
  if (status == GEMSI_STATUS_SUCCESS) {
    if (response.size < 12) status = GEMSI_STATUS_UNKNOWN;
    else {
      const uint64_t handle = gemsi_get_u64(response.data);
      const uint32_t count = gemsi_get_u32(response.data + 8);
      if (count > 4096 || response.size != 12 + count ||
          gemsi_store_kernel_signature(handle, (uint64_t)(uintptr_t)module, response.data + 12, count) != 0) {
        status = GEMSI_STATUS_UNKNOWN;
      } else *function = (void *)(uintptr_t)handle;
    }
  }
  gemsi_rpc_buffer_release(&response);
  return status;
}

int32_t cuModuleUnload(void *module) {
  if (module == NULL) return GEMSI_STATUS_INVALID_VALUE;
  uint8_t payload[8];
  gemsi_put_u64(payload, (uint64_t)(uintptr_t)module);
  GemsiRpcBuffer response = {0};
  const int32_t status = gemsi_expect_unit(
      gemsi_call(gemsi_cuModuleUnload_high, gemsi_cuModuleUnload_low, "cuModuleUnload", payload, sizeof(payload), &response),
      &response);
  if (status == GEMSI_STATUS_SUCCESS) gemsi_forget_module((uint64_t)(uintptr_t)module);
  return status;
}

int32_t cuLaunchKernel(
    void *function,
    uint32_t grid_x,
    uint32_t grid_y,
    uint32_t grid_z,
    uint32_t block_x,
    uint32_t block_y,
    uint32_t block_z,
    uint32_t shared_memory_bytes,
    void *stream,
    void **kernel_params,
    void **extra) {
  if (function == NULL || grid_x == 0 || grid_y == 0 || grid_z == 0 ||
      block_x == 0 || block_y == 0 || block_z == 0 || stream != NULL || extra != NULL) {
    return GEMSI_STATUS_INVALID_VALUE;
  }
  pthread_mutex_lock(&gemsi_kernel_mutex);
  GemsiKernelSignature *signature = gemsi_kernel_signatures;
  while (signature != NULL && signature->function != (uint64_t)(uintptr_t)function) signature = signature->next;
  if (signature == NULL || (signature->count != 0 && kernel_params == NULL)) {
    pthread_mutex_unlock(&gemsi_kernel_mutex);
    return GEMSI_STATUS_INVALID_VALUE;
  }
  size_t payload_size = 40;
  for (uint32_t index = 0; index < signature->count; ++index) {
    const uint8_t width = signature->widths[index];
    if ((width != 1 && width != 2 && width != 4 && width != 8) || kernel_params[index] == NULL ||
        payload_size > GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE - 4 - width) {
      pthread_mutex_unlock(&gemsi_kernel_mutex);
      return GEMSI_STATUS_INVALID_VALUE;
    }
    payload_size += 4 + width;
  }
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) { pthread_mutex_unlock(&gemsi_kernel_mutex); return GEMSI_STATUS_UNKNOWN; }
  gemsi_put_u64(payload, (uint64_t)(uintptr_t)function);
  gemsi_put_u32(payload + 8, grid_x);
  gemsi_put_u32(payload + 12, grid_y);
  gemsi_put_u32(payload + 16, grid_z);
  gemsi_put_u32(payload + 20, block_x);
  gemsi_put_u32(payload + 24, block_y);
  gemsi_put_u32(payload + 28, block_z);
  gemsi_put_u32(payload + 32, shared_memory_bytes);
  gemsi_put_u32(payload + 36, signature->count);
  size_t offset = 40;
  for (uint32_t index = 0; index < signature->count; ++index) {
    const uint8_t width = signature->widths[index];
    gemsi_put_u32(payload + offset, width);
    memcpy(payload + offset + 4, kernel_params[index], width);
    offset += 4 + width;
  }
  pthread_mutex_unlock(&gemsi_kernel_mutex);
  GemsiRpcBuffer response = {0};
  const int32_t status = gemsi_expect_unit(
      gemsi_call(gemsi_cuLaunchKernel_high, gemsi_cuLaunchKernel_low, "cuLaunchKernel", payload, (uint32_t)payload_size, &response),
      &response);
  free(payload);
  return status;
}

#if defined(__GNUC__)
__attribute__((weak))
#endif
int32_t cudaGetDeviceCount(int32_t *count) {
  return cuDeviceGetCount(count);
}

int32_t cudaMalloc(void **pointer, size_t size) {
  if (pointer == NULL) return GEMSI_STATUS_INVALID_VALUE;
  uint64_t device_pointer = 0;
  const int32_t status = cuMemAlloc_v2(&device_pointer, size);
  if (status == GEMSI_STATUS_SUCCESS) *pointer = (void *)(uintptr_t)device_pointer;
  return status;
}

int32_t cudaFree(void *pointer) {
  return cuMemFree_v2((uint64_t)(uintptr_t)pointer);
}

int32_t cudaMemcpy(void *destination, const void *source, size_t size, int32_t kind) {
  switch (kind) {
    case 1:
      return cuMemcpyHtoD_v2((uint64_t)(uintptr_t)destination, source, size);
    case 2:
      return cuMemcpyDtoH_v2(destination, (uint64_t)(uintptr_t)source, size);
    case 3:
      return cuMemcpyDtoD_v2((uint64_t)(uintptr_t)destination, (uint64_t)(uintptr_t)source, size);
    default:
      return GEMSI_STATUS_INVALID_VALUE;
  }
}

const char *cudaGetErrorString(int32_t status) {
  switch (status) {
    case GEMSI_STATUS_SUCCESS: return "success";
    case GEMSI_STATUS_INVALID_VALUE: return "invalid value";
    case GEMSI_STATUS_NOT_SUPPORTED: return "not supported";
    case GEMSI_STATUS_INITIALIZATION_ERROR: return "initialization error";
    default: return "Gemstone CUDA error";
  }
}

static void *gemsi_fat_binary_token = NULL;

void **__cudaRegisterFatBinary(void *fat_binary) {
  gemsi_fat_binary_token = fat_binary;
  return &gemsi_fat_binary_token;
}

void __cudaRegisterFatBinaryEnd(void **handle) {
  (void)handle;
}

void __cudaUnregisterFatBinary(void **handle) {
  if (handle == &gemsi_fat_binary_token) gemsi_fat_binary_token = NULL;
}

