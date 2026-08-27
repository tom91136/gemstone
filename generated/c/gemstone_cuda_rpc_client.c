#include "gemstone_cuda_rpc.inc"
#include "gemstone_protocol.h"
#include "gemstone_rpc_transport.h"

#include <limits.h>
#include <pthread.h>
#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

static const uint64_t gemstone___cudaGemstoneManagedRegister_high = UINT64_C(0xc6207aea8d73f5ba);
static const uint64_t gemstone___cudaGemstoneManagedRegister_low = UINT64_C(0x2db4cd6e3a364b1a);
static const uint64_t gemstone___cudaGemstoneManagedUnregister_high = UINT64_C(0x93e5439ad242c3c0);
static const uint64_t gemstone___cudaGemstoneManagedUnregister_low = UINT64_C(0x8e20904fec96004c);
static const uint64_t gemstone_cuCtxCreate_v2_high = UINT64_C(0xe6eb5473c0a097e3);
static const uint64_t gemstone_cuCtxCreate_v2_low = UINT64_C(0xa17c8d63cec75a44);
static const uint64_t gemstone_cuCtxDestroy_v2_high = UINT64_C(0xb574aacc9b35ee6a);
static const uint64_t gemstone_cuCtxDestroy_v2_low = UINT64_C(0x8135b883071cc261);
static const uint64_t gemstone_cuCtxSynchronize_high = UINT64_C(0xe21f80c194a589d4);
static const uint64_t gemstone_cuCtxSynchronize_low = UINT64_C(0x4af681a7ef75bb10);
static const uint64_t gemstone_cuDeviceGet_high = UINT64_C(0xc91170009b2ba6c9);
static const uint64_t gemstone_cuDeviceGet_low = UINT64_C(0x263d0d32e04e06a7);
static const uint64_t gemstone_cuDeviceGetCount_high = UINT64_C(0x831f6fffdf16f0d4);
static const uint64_t gemstone_cuDeviceGetCount_low = UINT64_C(0x3c498239d16d9867);
static const uint64_t gemstone_cuDriverGetVersion_high = UINT64_C(0xe584ec56943a425d);
static const uint64_t gemstone_cuDriverGetVersion_low = UINT64_C(0x3c512e60803234f9);
static const uint64_t gemstone_cuInit_high = UINT64_C(0x3c1688958f08660e);
static const uint64_t gemstone_cuInit_low = UINT64_C(0x1c2a40d0799c268c);
static const uint64_t gemstone_cuLaunchKernel_high = UINT64_C(0x0541d9142559d437);
static const uint64_t gemstone_cuLaunchKernel_low = UINT64_C(0x4cd345cbce10e2df);
static const uint64_t gemstone_cuMemAlloc_v2_high = UINT64_C(0x7077a5441f532f0e);
static const uint64_t gemstone_cuMemAlloc_v2_low = UINT64_C(0x677e154cf996113d);
static const uint64_t gemstone_cuMemFree_v2_high = UINT64_C(0xb5fdc569871ec54e);
static const uint64_t gemstone_cuMemFree_v2_low = UINT64_C(0x418d186e2d8efa3a);
static const uint64_t gemstone_cuMemcpyDtoD_v2_high = UINT64_C(0xe27c11476bbea751);
static const uint64_t gemstone_cuMemcpyDtoD_v2_low = UINT64_C(0x681167b2aac69936);
static const uint64_t gemstone_cuMemcpyDtoH_v2_high = UINT64_C(0x0cc3708077b8e76a);
static const uint64_t gemstone_cuMemcpyDtoH_v2_low = UINT64_C(0xaa0bf9a6432ca141);
static const uint64_t gemstone_cuMemcpyHtoD_v2_high = UINT64_C(0x9a8f59036790ef69);
static const uint64_t gemstone_cuMemcpyHtoD_v2_low = UINT64_C(0x4808ed612739b8fa);
static const uint64_t gemstone_cuMemsetD8_v2_high = UINT64_C(0x2c1383458eec311a);
static const uint64_t gemstone_cuMemsetD8_v2_low = UINT64_C(0x73e621c93fd80644);
static const uint64_t gemstone_cuModuleGetFunction_high = UINT64_C(0x4b6b65a1226b0723);
static const uint64_t gemstone_cuModuleGetFunction_low = UINT64_C(0xc3ac0d7e32b1d4b5);
static const uint64_t gemstone_cuModuleLoadData_high = UINT64_C(0xfef489c6c628b458);
static const uint64_t gemstone_cuModuleLoadData_low = UINT64_C(0x480e5cb6fe964d3f);
static const uint64_t gemstone_cuModuleUnload_high = UINT64_C(0xb94c365c6c5c3902);
static const uint64_t gemstone_cuModuleUnload_low = UINT64_C(0x2a1466600700709a);

static void gemstone_put_u32(uint8_t *destination, uint32_t value) {
  destination[0] = (uint8_t)(value >> 24);
  destination[1] = (uint8_t)(value >> 16);
  destination[2] = (uint8_t)(value >> 8);
  destination[3] = (uint8_t)value;
}

static void gemstone_put_u64(uint8_t *destination, uint64_t value) {
  gemstone_put_u32(destination, (uint32_t)(value >> 32));
  gemstone_put_u32(destination + 4, (uint32_t)value);
}

static uint32_t gemstone_get_u32(const uint8_t *source) {
  return ((uint32_t)source[0] << 24) | ((uint32_t)source[1] << 16) | ((uint32_t)source[2] << 8) | (uint32_t)source[3];
}

static uint64_t gemstone_get_u64(const uint8_t *source) {
  return ((uint64_t)gemstone_get_u32(source) << 32) | gemstone_get_u32(source + 4);
}

static size_t gemstone_bounded_string_length(const char *value, size_t limit) {
  size_t size = 0;
  while (size < limit && value[size] != '\0') ++size;
  return size;
}

typedef struct GemstoneKernelSignature {
  uint64_t function;
  uint64_t module;
  uint32_t count;
  uint8_t *widths;
  struct GemstoneKernelSignature *next;
} GemstoneKernelSignature;

static pthread_mutex_t gemstone_kernel_mutex = PTHREAD_MUTEX_INITIALIZER;
static GemstoneKernelSignature *gemstone_kernel_signatures = NULL;

static int gemstone_store_kernel_signature(
    uint64_t function,
    uint64_t module,
    const uint8_t *widths,
    uint32_t count) {
  GemstoneKernelSignature *entry = (GemstoneKernelSignature *)calloc(1, sizeof(*entry));
  if (entry == NULL) return -1;
  if (count != 0) {
    entry->widths = (uint8_t *)malloc(count);
    if (entry->widths == NULL) { free(entry); return -1; }
    memcpy(entry->widths, widths, count);
  }
  entry->function = function;
  entry->module = module;
  entry->count = count;
  pthread_mutex_lock(&gemstone_kernel_mutex);
  entry->next = gemstone_kernel_signatures;
  gemstone_kernel_signatures = entry;
  pthread_mutex_unlock(&gemstone_kernel_mutex);
  return 0;
}

static void gemstone_forget_module(uint64_t module) {
  pthread_mutex_lock(&gemstone_kernel_mutex);
  GemstoneKernelSignature **cursor = &gemstone_kernel_signatures;
  while (*cursor != NULL) {
    GemstoneKernelSignature *entry = *cursor;
    if (entry->module == module) {
      *cursor = entry->next;
      free(entry->widths);
      free(entry);
    } else cursor = &entry->next;
  }
  pthread_mutex_unlock(&gemstone_kernel_mutex);
}

static int32_t gemstone_call(
    uint64_t high,
    uint64_t low,
    const char *name,
    const uint8_t *payload,
    uint32_t payload_size,
    GemstoneRpcBuffer *response) {
  int32_t status = GEMSTONE_STATUS_INITIALIZATION_ERROR;
  if (gemstone_rpc_call(high, low, name, payload, payload_size, &status, response) != 0) return GEMSTONE_STATUS_INITIALIZATION_ERROR;
  if (status != GEMSTONE_STATUS_SUCCESS) gemstone_rpc_buffer_release(response);
  return status;
}

static int32_t gemstone_expect_unit(int32_t status, GemstoneRpcBuffer *response) {
  if (status == GEMSTONE_STATUS_SUCCESS && response->size != 0) status = GEMSTONE_STATUS_UNKNOWN;
  gemstone_rpc_buffer_release(response);
  return status;
}

static int32_t gemstone_expect_u32(int32_t status, GemstoneRpcBuffer *response, uint32_t *value) {
  if (status == GEMSTONE_STATUS_SUCCESS) {
    if (response->size != 4 || value == NULL) status = GEMSTONE_STATUS_UNKNOWN;
    else *value = gemstone_get_u32(response->data);
  }
  gemstone_rpc_buffer_release(response);
  return status;
}

static int32_t gemstone_expect_u64(int32_t status, GemstoneRpcBuffer *response, uint64_t *value) {
  if (status == GEMSTONE_STATUS_SUCCESS) {
    if (response->size != 8 || value == NULL) status = GEMSTONE_STATUS_UNKNOWN;
    else *value = gemstone_get_u64(response->data);
  }
  gemstone_rpc_buffer_release(response);
  return status;
}

int32_t cuInit(uint32_t flags) {
  uint8_t payload[4];
  gemstone_put_u32(payload, flags);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(gemstone_call(gemstone_cuInit_high, gemstone_cuInit_low, "cuInit", payload, sizeof(payload), &response), &response);
}

int32_t cuDriverGetVersion(int32_t *version) {
  if (version == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_u32(gemstone_call(gemstone_cuDriverGetVersion_high, gemstone_cuDriverGetVersion_low, "cuDriverGetVersion", NULL, 0, &response), &response, (uint32_t *)version);
}

int32_t cuDeviceGetCount(int32_t *count) {
  if (count == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_u32(gemstone_call(gemstone_cuDeviceGetCount_high, gemstone_cuDeviceGetCount_low, "cuDeviceGetCount", NULL, 0, &response), &response, (uint32_t *)count);
}

int32_t cuDeviceGet(int32_t *device, int32_t ordinal) {
  if (device == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[4];
  gemstone_put_u32(payload, (uint32_t)ordinal);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_u32(gemstone_call(gemstone_cuDeviceGet_high, gemstone_cuDeviceGet_low, "cuDeviceGet", payload, sizeof(payload), &response), &response, (uint32_t *)device);
}

int32_t cuCtxCreate_v2(void **context, uint32_t flags, int32_t device) {
  if (context == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[8];
  gemstone_put_u32(payload, flags);
  gemstone_put_u32(payload + 4, (uint32_t)device);
  uint64_t handle = 0;
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_u64(gemstone_call(gemstone_cuCtxCreate_v2_high, gemstone_cuCtxCreate_v2_low, "cuCtxCreate_v2", payload, sizeof(payload), &response), &response, &handle);
  if (status == GEMSTONE_STATUS_SUCCESS) *context = (void *)(uintptr_t)handle;
  return status;
}

int32_t cuCtxDestroy_v2(void *context) {
  uint8_t payload[8];
  gemstone_put_u64(payload, (uint64_t)(uintptr_t)context);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(gemstone_call(gemstone_cuCtxDestroy_v2_high, gemstone_cuCtxDestroy_v2_low, "cuCtxDestroy_v2", payload, sizeof(payload), &response), &response);
}

int32_t cuCtxSynchronize(void) {
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(gemstone_call(gemstone_cuCtxSynchronize_high, gemstone_cuCtxSynchronize_low, "cuCtxSynchronize", NULL, 0, &response), &response);
}

int32_t cuMemAlloc_v2(uint64_t *pointer, size_t size) {
  if (pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[8];
  gemstone_put_u64(payload, (uint64_t)size);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_u64(gemstone_call(gemstone_cuMemAlloc_v2_high, gemstone_cuMemAlloc_v2_low, "cuMemAlloc_v2", payload, sizeof(payload), &response), &response, pointer);
}

int32_t cuMemFree_v2(uint64_t pointer) {
  uint8_t payload[8];
  gemstone_put_u64(payload, pointer);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(gemstone_call(gemstone_cuMemFree_v2_high, gemstone_cuMemFree_v2_low, "cuMemFree_v2", payload, sizeof(payload), &response), &response);
}

int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size) {
  if ((source == NULL && size != 0) || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 12) return GEMSTONE_STATUS_INVALID_VALUE;
  const uint32_t payload_size = (uint32_t)size + 12;
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
  gemstone_put_u64(payload, destination);
  gemstone_put_u32(payload + 8, (uint32_t)size);
  if (size != 0) memcpy(payload + 12, source, size);
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(gemstone_call(gemstone_cuMemcpyHtoD_v2_high, gemstone_cuMemcpyHtoD_v2_low, "cuMemcpyHtoD_v2", payload, payload_size, &response), &response);
  free(payload);
  return status;
}

int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size) {
  if ((destination == NULL && size != 0) || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 4) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[16];
  gemstone_put_u64(payload, source);
  gemstone_put_u64(payload + 8, (uint64_t)size);
  GemstoneRpcBuffer response = {0};
  int32_t status = gemstone_call(gemstone_cuMemcpyDtoH_v2_high, gemstone_cuMemcpyDtoH_v2_low, "cuMemcpyDtoH_v2", payload, sizeof(payload), &response);
  if (status == GEMSTONE_STATUS_SUCCESS) {
    if (response.size < 4 || gemstone_get_u32(response.data) != size || response.size != size + 4) status = GEMSTONE_STATUS_UNKNOWN;
    else if (size != 0) memcpy(destination, response.data + 4, size);
  }
  gemstone_rpc_buffer_release(&response);
  return status;
}

int32_t cuMemcpyDtoD_v2(uint64_t destination, uint64_t source, size_t size) {
  uint8_t payload[24];
  gemstone_put_u64(payload, destination);
  gemstone_put_u64(payload + 8, source);
  gemstone_put_u64(payload + 16, (uint64_t)size);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(gemstone_call(gemstone_cuMemcpyDtoD_v2_high, gemstone_cuMemcpyDtoD_v2_low, "cuMemcpyDtoD_v2", payload, sizeof(payload), &response), &response);
}

int32_t cuMemsetD8_v2(uint64_t destination, uint8_t value, size_t count) {
  uint8_t payload[17];
  gemstone_put_u64(payload, destination);
  payload[8] = value;
  gemstone_put_u64(payload + 9, (uint64_t)count);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(gemstone_call(gemstone_cuMemsetD8_v2_high, gemstone_cuMemsetD8_v2_low, "cuMemsetD8_v2", payload, sizeof(payload), &response), &response);
}

static int32_t gemstone_managed_register_remote(uint64_t pointer, size_t size) {
  uint8_t payload[16];
  gemstone_put_u64(payload, pointer);
  gemstone_put_u64(payload + 8, (uint64_t)size);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(
      gemstone_call(
          gemstone___cudaGemstoneManagedRegister_high,
          gemstone___cudaGemstoneManagedRegister_low,
          "__cudaGemstoneManagedRegister",
          payload,
          sizeof(payload),
          &response),
      &response);
}

static int32_t gemstone_managed_unregister_remote(uint64_t pointer) {
  uint8_t payload[8];
  gemstone_put_u64(payload, pointer);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(
      gemstone_call(
          gemstone___cudaGemstoneManagedUnregister_high,
          gemstone___cudaGemstoneManagedUnregister_low,
          "__cudaGemstoneManagedUnregister",
          payload,
          sizeof(payload),
          &response),
      &response);
}

int32_t cuModuleLoadData(void **module, const void *image) {
  if (module == NULL || image == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const size_t size = gemstone_bounded_string_length((const char *)image, GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE);
  if (size == GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 4) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  const uint32_t payload_size = (uint32_t)size + 4;
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
  gemstone_put_u32(payload, (uint32_t)size);
  memcpy(payload + 4, image, size);
  uint64_t handle = 0;
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_u64(
      gemstone_call(gemstone_cuModuleLoadData_high, gemstone_cuModuleLoadData_low, "cuModuleLoadData", payload, payload_size, &response),
      &response,
      &handle);
  free(payload);
  if (status == GEMSTONE_STATUS_SUCCESS) *module = (void *)(uintptr_t)handle;
  return status;
}

int32_t cuModuleGetFunction(void **function, void *module, const char *name) {
  if (function == NULL || module == NULL || name == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const size_t name_size = gemstone_bounded_string_length(name, 4096);
  if (name_size == 0 || name_size == 4096) return GEMSTONE_STATUS_INVALID_VALUE;
  const uint32_t payload_size = (uint32_t)name_size + 12;
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
  gemstone_put_u64(payload, (uint64_t)(uintptr_t)module);
  gemstone_put_u32(payload + 8, (uint32_t)name_size);
  memcpy(payload + 12, name, name_size);
  GemstoneRpcBuffer response = {0};
  int32_t status = gemstone_call(
      gemstone_cuModuleGetFunction_high,
      gemstone_cuModuleGetFunction_low,
      "cuModuleGetFunction",
      payload,
      payload_size,
      &response);
  free(payload);
  if (status == GEMSTONE_STATUS_SUCCESS) {
    if (response.size < 12) status = GEMSTONE_STATUS_UNKNOWN;
    else {
      const uint64_t handle = gemstone_get_u64(response.data);
      const uint32_t count = gemstone_get_u32(response.data + 8);
      if (count > 4096 || response.size != 12 + count ||
          gemstone_store_kernel_signature(handle, (uint64_t)(uintptr_t)module, response.data + 12, count) != 0) {
        status = GEMSTONE_STATUS_UNKNOWN;
      } else *function = (void *)(uintptr_t)handle;
    }
  }
  gemstone_rpc_buffer_release(&response);
  return status;
}

int32_t cuModuleUnload(void *module) {
  if (module == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[8];
  gemstone_put_u64(payload, (uint64_t)(uintptr_t)module);
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(
      gemstone_call(gemstone_cuModuleUnload_high, gemstone_cuModuleUnload_low, "cuModuleUnload", payload, sizeof(payload), &response),
      &response);
  if (status == GEMSTONE_STATUS_SUCCESS) gemstone_forget_module((uint64_t)(uintptr_t)module);
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
      block_x == 0 || block_y == 0 || block_z == 0 || extra != NULL) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  (void)stream;
  pthread_mutex_lock(&gemstone_kernel_mutex);
  GemstoneKernelSignature *signature = gemstone_kernel_signatures;
  while (signature != NULL && signature->function != (uint64_t)(uintptr_t)function) signature = signature->next;
  if (signature == NULL || (signature->count != 0 && kernel_params == NULL)) {
    pthread_mutex_unlock(&gemstone_kernel_mutex);
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  size_t payload_size = 40;
  for (uint32_t index = 0; index < signature->count; ++index) {
    const uint8_t width = signature->widths[index];
    if (width == 0 || kernel_params[index] == NULL ||
        payload_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 4 - width) {
      pthread_mutex_unlock(&gemstone_kernel_mutex);
      return GEMSTONE_STATUS_INVALID_VALUE;
    }
    payload_size += 4 + width;
  }
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) { pthread_mutex_unlock(&gemstone_kernel_mutex); return GEMSTONE_STATUS_UNKNOWN; }
  gemstone_put_u64(payload, (uint64_t)(uintptr_t)function);
  gemstone_put_u32(payload + 8, grid_x);
  gemstone_put_u32(payload + 12, grid_y);
  gemstone_put_u32(payload + 16, grid_z);
  gemstone_put_u32(payload + 20, block_x);
  gemstone_put_u32(payload + 24, block_y);
  gemstone_put_u32(payload + 28, block_z);
  gemstone_put_u32(payload + 32, shared_memory_bytes);
  gemstone_put_u32(payload + 36, signature->count);
  size_t offset = 40;
  for (uint32_t index = 0; index < signature->count; ++index) {
    const uint8_t width = signature->widths[index];
    gemstone_put_u32(payload + offset, width);
    memcpy(payload + offset + 4, kernel_params[index], width);
    offset += 4 + width;
  }
  pthread_mutex_unlock(&gemstone_kernel_mutex);
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(
      gemstone_call(gemstone_cuLaunchKernel_high, gemstone_cuLaunchKernel_low, "cuLaunchKernel", payload, (uint32_t)payload_size, &response),
      &response);
  free(payload);
  return status;
}

typedef struct GemstoneMirroredAllocation {
  void *host;
  size_t size;
  struct GemstoneMirroredAllocation *next;
} GemstoneMirroredAllocation;

static pthread_mutex_t gemstone_mirrored_mutex = PTHREAD_MUTEX_INITIALIZER;
static GemstoneMirroredAllocation *gemstone_mirrored_allocations = NULL;

static int32_t gemstone_mirrored_allocate(void **pointer, size_t size) {
  if (pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  void *host = malloc(size == 0 ? 1 : size);
  if (host == NULL) return GEMSTONE_STATUS_UNKNOWN;
  const int32_t status = gemstone_managed_register_remote((uint64_t)(uintptr_t)host, size == 0 ? 1 : size);
  if (status != GEMSTONE_STATUS_SUCCESS) {
    free(host);
    return status;
  }
  GemstoneMirroredAllocation *entry = (GemstoneMirroredAllocation *)calloc(1, sizeof(*entry));
  if (entry == NULL) {
    (void)gemstone_managed_unregister_remote((uint64_t)(uintptr_t)host);
    free(host);
    return GEMSTONE_STATUS_UNKNOWN;
  }
  entry->host = host;
  entry->size = size == 0 ? 1 : size;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  entry->next = gemstone_mirrored_allocations;
  gemstone_mirrored_allocations = entry;
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  *pointer = host;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_mirrored_free(void *pointer, int *found) {
  *found = 0;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  GemstoneMirroredAllocation **cursor = &gemstone_mirrored_allocations;
  while (*cursor != NULL && (*cursor)->host != pointer) cursor = &(*cursor)->next;
  if (*cursor == NULL) {
    pthread_mutex_unlock(&gemstone_mirrored_mutex);
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  GemstoneMirroredAllocation *entry = *cursor;
  *cursor = entry->next;
  *found = 1;
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  const int32_t status = gemstone_managed_unregister_remote((uint64_t)(uintptr_t)entry->host);
  free(entry->host);
  free(entry);
  return status;
}

static int32_t gemstone_mirrored_synchronize_to_device(void) {
  int32_t status = GEMSTONE_STATUS_SUCCESS;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  for (GemstoneMirroredAllocation *entry = gemstone_mirrored_allocations;
       entry != NULL && status == GEMSTONE_STATUS_SUCCESS;
       entry = entry->next) {
    status = cuMemcpyHtoD_v2((uint64_t)(uintptr_t)entry->host, entry->host, entry->size);
  }
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  return status;
}

static int32_t gemstone_mirrored_synchronize_from_device(void) {
  int32_t status = GEMSTONE_STATUS_SUCCESS;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  for (GemstoneMirroredAllocation *entry = gemstone_mirrored_allocations;
       entry != NULL && status == GEMSTONE_STATUS_SUCCESS;
       entry = entry->next) {
    status = cuMemcpyDtoH_v2(entry->host, (uint64_t)(uintptr_t)entry->host, entry->size);
  }
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  return status;
}

#if defined(__GNUC__)
__attribute__((weak))
#endif
int32_t cudaGetDeviceCount(int32_t *count) {
  return cuDeviceGetCount(count);
}

int32_t cudaMalloc(void **pointer, size_t size) {
  if (pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint64_t device_pointer = 0;
  const int32_t status = cuMemAlloc_v2(&device_pointer, size);
  if (status == GEMSTONE_STATUS_SUCCESS) *pointer = (void *)(uintptr_t)device_pointer;
  return status;
}

int32_t cudaFree(void *pointer) {
  int found = 0;
  const int32_t mirrored_status = gemstone_mirrored_free(pointer, &found);
  return found ? mirrored_status : cuMemFree_v2((uint64_t)(uintptr_t)pointer);
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
      return GEMSTONE_STATUS_INVALID_VALUE;
  }
}

const char *cudaGetErrorString(int32_t status) {
  switch (status) {
    case GEMSTONE_STATUS_SUCCESS: return "success";
    case GEMSTONE_STATUS_INVALID_VALUE: return "invalid value";
    case GEMSTONE_STATUS_NOT_SUPPORTED: return "not supported";
    case GEMSTONE_STATUS_INITIALIZATION_ERROR: return "initialization error";
    default: return "Gemstone CUDA error";
  }
}

const char *cudaGetErrorName(int32_t status) {
  switch (status) {
    case GEMSTONE_STATUS_SUCCESS: return "cudaSuccess";
    case GEMSTONE_STATUS_INVALID_VALUE: return "cudaErrorInvalidValue";
    case GEMSTONE_STATUS_NOT_SUPPORTED: return "cudaErrorNotSupported";
    case GEMSTONE_STATUS_INITIALIZATION_ERROR: return "cudaErrorInitializationError";
    default: return "cudaErrorUnknown";
  }
}

static _Thread_local int32_t gemstone_last_error = GEMSTONE_STATUS_SUCCESS;

int32_t cudaPeekAtLastError(void) {
  return gemstone_last_error;
}

int32_t cudaGetLastError(void) {
  const int32_t status = gemstone_last_error;
  gemstone_last_error = GEMSTONE_STATUS_SUCCESS;
  return status;
}

int32_t cudaGetDevice(int32_t *device) {
  if (device == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *device = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cudaSetDevice(int32_t device) {
  return device == 0 ? GEMSTONE_STATUS_SUCCESS : GEMSTONE_STATUS_INVALID_VALUE;
}

int32_t cudaDeviceSynchronize(void) {
  return cuCtxSynchronize();
}

int32_t cudaStreamSynchronize(void *stream) {
  (void)stream;
  return cuCtxSynchronize();
}

int32_t cudaDeviceGetAttribute(int32_t *value, int32_t attribute, int32_t device) {
  if (value == NULL || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  switch (attribute) {
    case 1: *value = 1024; break;
    case 2: *value = 1024; break;
    case 3: *value = 1024; break;
    case 4: *value = 64; break;
    case 5: *value = 2147483647; break;
    case 6: *value = 65535; break;
    case 7: *value = 65535; break;
    case 8: *value = 49152; break;
    case 10: *value = 32; break;
    case 16: *value = 1; break;
    case 39: *value = 1024; break;
    case 41: *value = 1; break;
    case 75: *value = 7; break;
    case 76: *value = 5; break;
    case 81: *value = 49152; break;
    case 83: *value = 1; break;
    case 89: *value = 1; break;
    case 95: *value = 1; break;
    case 106: *value = 32; break;
    default: *value = 0; break;
  }
  return GEMSTONE_STATUS_SUCCESS;
}

typedef struct GemstoneDevicePropertiesPrefix {
  char name[256];
  uint8_t uuid[16];
  char luid[8];
  uint32_t luid_device_node_mask;
  size_t total_global_memory;
  size_t shared_memory_per_block;
  int32_t registers_per_block;
  int32_t warp_size;
  size_t memory_pitch;
  int32_t max_threads_per_block;
  int32_t max_threads_dimension[3];
  int32_t max_grid_size[3];
  size_t total_constant_memory;
  int32_t major;
  int32_t minor;
  size_t texture_alignment;
  size_t texture_pitch_alignment;
  int32_t multiprocessor_count;
} GemstoneDevicePropertiesPrefix;

int32_t cudaGetDeviceProperties(void *properties, int32_t device) {
  if (properties == NULL || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneDevicePropertiesPrefix *value = (GemstoneDevicePropertiesPrefix *)properties;
  memset(value, 0, sizeof(*value));
  memcpy(value->name, "Gemstone PTX emulator", sizeof("Gemstone PTX emulator"));
  value->total_global_memory = UINT64_C(8) * 1024 * 1024 * 1024;
  value->shared_memory_per_block = 49152;
  value->registers_per_block = 65536;
  value->warp_size = 32;
  value->memory_pitch = SIZE_MAX;
  value->max_threads_per_block = 1024;
  value->max_threads_dimension[0] = 1024;
  value->max_threads_dimension[1] = 1024;
  value->max_threads_dimension[2] = 64;
  value->max_grid_size[0] = INT_MAX;
  value->max_grid_size[1] = 65535;
  value->max_grid_size[2] = 65535;
  value->total_constant_memory = 65536;
  value->major = 7;
  value->minor = 5;
  value->texture_alignment = 256;
  value->texture_pitch_alignment = 32;
  value->multiprocessor_count = 1;
  return GEMSTONE_STATUS_SUCCESS;
}

typedef struct GemstoneFunctionAttributesPrefix {
  size_t shared_size_bytes;
  size_t constant_size_bytes;
  size_t local_size_bytes;
  int32_t max_threads_per_block;
  int32_t register_count;
  int32_t ptx_version;
  int32_t binary_version;
  int32_t cache_mode_ca;
  int32_t max_dynamic_shared_size_bytes;
  int32_t preferred_shared_memory_carveout;
} GemstoneFunctionAttributesPrefix;

int32_t cudaFuncGetAttributes(void *attributes, const void *function) {
  if (attributes == NULL || function == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneFunctionAttributesPrefix *value = (GemstoneFunctionAttributesPrefix *)attributes;
  memset(value, 0, sizeof(*value));
  value->max_threads_per_block = 1024;
  value->ptx_version = 92;
  value->binary_version = 75;
  value->max_dynamic_shared_size_bytes = 49152;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
    int32_t *blocks,
    const void *function,
    int32_t block_size,
    size_t dynamic_shared_memory,
    uint32_t flags) {
  (void)function;
  (void)dynamic_shared_memory;
  (void)flags;
  if (blocks == NULL || block_size <= 0 || block_size > 1024) return GEMSTONE_STATUS_INVALID_VALUE;
  *blocks = 1024 / block_size;
  if (*blocks == 0) *blocks = 1;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cudaMallocHost(void **pointer, size_t size) {
  return gemstone_mirrored_allocate(pointer, size);
}

int32_t cudaFreeHost(void *pointer) {
  int found = 0;
  const int32_t status = gemstone_mirrored_free(pointer, &found);
  return found ? status : GEMSTONE_STATUS_INVALID_VALUE;
}

int32_t cudaMallocManaged(void **pointer, size_t size, uint32_t flags) {
  (void)flags;
  return gemstone_mirrored_allocate(pointer, size);
}

int32_t cudaMemcpyAsync(
    void *destination,
    const void *source,
    size_t size,
    int32_t kind,
    void *stream) {
  (void)stream;
  return cudaMemcpy(destination, source, size, kind);
}

typedef struct GemstoneFatBinaryWrapper {
  int32_t magic;
  int32_t version;
  const void *data;
  void *filename;
} GemstoneFatBinaryWrapper;

typedef struct GemstoneFatBinary {
  void *token;
  void *module;
  struct GemstoneFatBinary *next;
} GemstoneFatBinary;

typedef struct GemstoneRegisteredFunction {
  const void *host_function;
  void *device_function;
  char *device_name;
  GemstoneFatBinary *fat_binary;
  struct GemstoneRegisteredFunction *next;
} GemstoneRegisteredFunction;

typedef struct GemstoneDim3 {
  uint32_t x;
  uint32_t y;
  uint32_t z;
} GemstoneDim3;

typedef struct GemstoneLaunchConfiguration {
  GemstoneDim3 grid;
  GemstoneDim3 block;
  size_t shared_memory;
  void *stream;
  int valid;
} GemstoneLaunchConfiguration;

static pthread_mutex_t gemstone_registration_mutex = PTHREAD_MUTEX_INITIALIZER;
static GemstoneFatBinary *gemstone_fat_binaries = NULL;
static GemstoneRegisteredFunction *gemstone_registered_functions = NULL;
static _Thread_local GemstoneLaunchConfiguration gemstone_launch_configuration = {0};

static uint16_t gemstone_read_native_u16(const uint8_t *source) {
  uint16_t value;
  memcpy(&value, source, sizeof(value));
  return value;
}

static uint32_t gemstone_read_native_u32(const uint8_t *source) {
  uint32_t value;
  memcpy(&value, source, sizeof(value));
  return value;
}

static uint64_t gemstone_read_native_u64(const uint8_t *source) {
  uint64_t value;
  memcpy(&value, source, sizeof(value));
  return value;
}

static const char *gemstone_find_uncompressed_ptx(const void *fat_binary) {
  if (fat_binary == NULL) return NULL;
  const GemstoneFatBinaryWrapper *wrapper = (const GemstoneFatBinaryWrapper *)fat_binary;
  if (wrapper->magic != INT32_C(0x466243b1) || wrapper->data == NULL) return NULL;
  const uint8_t *data = (const uint8_t *)wrapper->data;
  if (gemstone_read_native_u32(data) != UINT32_C(0xba55ed50)) return NULL;
  const uint16_t outer_header_size = gemstone_read_native_u16(data + 6);
  const uint64_t content_size = gemstone_read_native_u64(data + 8);
  if (outer_header_size < 16 || content_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE) return NULL;
  const uint64_t total_size = (uint64_t)outer_header_size + content_size;
  uint64_t offset = outer_header_size;
  while (offset <= total_size && total_size - offset >= 16) {
    const uint8_t *entry = data + offset;
    const uint16_t kind = gemstone_read_native_u16(entry);
    const uint32_t header_size = gemstone_read_native_u32(entry + 4);
    const uint64_t payload_size = gemstone_read_native_u64(entry + 8);
    if (header_size < 16 || (uint64_t)header_size > total_size - offset ||
        payload_size > total_size - offset - header_size) return NULL;
    const char *payload = (const char *)(entry + header_size);
    size_t prefix = 0;
    while (prefix < payload_size &&
        (payload[prefix] == ' ' || payload[prefix] == '\t' || payload[prefix] == '\r' || payload[prefix] == '\n')) ++prefix;
    if ((kind & 1u) != 0 && payload_size - prefix >= 9 &&
        memcmp(payload + prefix, ".version", 8) == 0 && memchr(payload + prefix, '\0', payload_size - prefix) != NULL) {
      return payload + prefix;
    }
    offset += (uint64_t)header_size + payload_size;
  }
  return NULL;
}

void **__cudaRegisterFatBinary(void *fat_binary) {
  GemstoneFatBinary *entry = (GemstoneFatBinary *)calloc(1, sizeof(*entry));
  if (entry == NULL) return NULL;
  entry->token = entry;
  const char *ptx = gemstone_find_uncompressed_ptx(fat_binary);
  if (ptx != NULL && cuModuleLoadData(&entry->module, ptx) != GEMSTONE_STATUS_SUCCESS) entry->module = NULL;
  pthread_mutex_lock(&gemstone_registration_mutex);
  entry->next = gemstone_fat_binaries;
  gemstone_fat_binaries = entry;
  pthread_mutex_unlock(&gemstone_registration_mutex);
  return &entry->token;
}

void __cudaRegisterFatBinaryEnd(void **handle) {
  (void)handle;
}

void __cudaRegisterFunction(
    void **handle,
    const char *host_function,
    char *device_function,
    const char *device_name,
    int32_t thread_limit,
    void *thread_id,
    void *block_id,
    void *block_dimension,
    void *grid_dimension,
    int32_t *warp_size) {
  (void)device_function;
  (void)thread_limit;
  (void)thread_id;
  (void)block_id;
  (void)block_dimension;
  (void)grid_dimension;
  (void)warp_size;
  if (handle == NULL || *handle == NULL || host_function == NULL || device_name == NULL) return;
  GemstoneFatBinary *fat = (GemstoneFatBinary *)*handle;
  GemstoneRegisteredFunction *entry = (GemstoneRegisteredFunction *)calloc(1, sizeof(*entry));
  if (entry == NULL) return;
  const size_t name_size = strlen(device_name);
  entry->device_name = (char *)malloc(name_size + 1);
  if (entry->device_name == NULL) {
    free(entry);
    return;
  }
  memcpy(entry->device_name, device_name, name_size + 1);
  entry->host_function = host_function;
  entry->fat_binary = fat;
  pthread_mutex_lock(&gemstone_registration_mutex);
  entry->next = gemstone_registered_functions;
  gemstone_registered_functions = entry;
  pthread_mutex_unlock(&gemstone_registration_mutex);
}

void __cudaRegisterVar(
    void **handle,
    char *host_variable,
    char *device_address,
    const char *device_name,
    int32_t external,
    size_t size,
    int32_t constant,
    int32_t global) {
  (void)handle;
  (void)host_variable;
  (void)device_address;
  (void)device_name;
  (void)external;
  (void)size;
  (void)constant;
  (void)global;
}

void __cudaUnregisterFatBinary(void **handle) {
  if (handle == NULL || *handle == NULL) return;
  GemstoneFatBinary *fat = (GemstoneFatBinary *)*handle;
  pthread_mutex_lock(&gemstone_registration_mutex);
  GemstoneRegisteredFunction **function_cursor = &gemstone_registered_functions;
  while (*function_cursor != NULL) {
    GemstoneRegisteredFunction *entry = *function_cursor;
    if (entry->fat_binary == fat) {
      *function_cursor = entry->next;
      free(entry->device_name);
      free(entry);
    } else function_cursor = &entry->next;
  }
  GemstoneFatBinary **fat_cursor = &gemstone_fat_binaries;
  while (*fat_cursor != NULL && *fat_cursor != fat) fat_cursor = &(*fat_cursor)->next;
  if (*fat_cursor == fat) *fat_cursor = fat->next;
  pthread_mutex_unlock(&gemstone_registration_mutex);
  if (fat->module != NULL) (void)cuModuleUnload(fat->module);
  fat->token = NULL;
  free(fat);
}

int32_t __cudaGetKernel(void **kernel, const void *host_function) {
  if (kernel == NULL || host_function == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  pthread_mutex_lock(&gemstone_registration_mutex);
  GemstoneRegisteredFunction *entry = gemstone_registered_functions;
  while (entry != NULL && entry->host_function != host_function) entry = entry->next;
  int32_t status = GEMSTONE_STATUS_INVALID_VALUE;
  if (entry != NULL && entry->fat_binary->module != NULL) {
    if (entry->device_function == NULL) {
      status = cuModuleGetFunction(&entry->device_function, entry->fat_binary->module, entry->device_name);
    } else status = GEMSTONE_STATUS_SUCCESS;
    if (status == GEMSTONE_STATUS_SUCCESS) *kernel = entry->device_function;
  }
  pthread_mutex_unlock(&gemstone_registration_mutex);
  return status;
}

unsigned __cudaPushCallConfiguration(
    GemstoneDim3 grid,
    GemstoneDim3 block,
    size_t shared_memory,
    void *stream) {
  gemstone_launch_configuration.grid = grid;
  gemstone_launch_configuration.block = block;
  gemstone_launch_configuration.shared_memory = shared_memory;
  gemstone_launch_configuration.stream = stream;
  gemstone_launch_configuration.valid = 1;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t __cudaPopCallConfiguration(
    GemstoneDim3 *grid,
    GemstoneDim3 *block,
    size_t *shared_memory,
    void *stream) {
  if (!gemstone_launch_configuration.valid || grid == NULL || block == NULL ||
      shared_memory == NULL || stream == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *grid = gemstone_launch_configuration.grid;
  *block = gemstone_launch_configuration.block;
  *shared_memory = gemstone_launch_configuration.shared_memory;
  *(void **)stream = gemstone_launch_configuration.stream;
  gemstone_launch_configuration.valid = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t __cudaLaunchKernel(
    void *kernel,
    GemstoneDim3 grid,
    GemstoneDim3 block,
    void **arguments,
    size_t shared_memory,
    void *stream) {
  int32_t status = gemstone_mirrored_synchronize_to_device();
  if (status == GEMSTONE_STATUS_SUCCESS) status = cuLaunchKernel(
      kernel,
      grid.x,
      grid.y,
      grid.z,
      block.x,
      block.y,
      block.z,
      (uint32_t)shared_memory,
      stream,
      arguments,
      NULL);
  if (status == GEMSTONE_STATUS_SUCCESS) status = gemstone_mirrored_synchronize_from_device();
  gemstone_last_error = status;
  return status;
}

