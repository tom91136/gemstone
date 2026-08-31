#include "gemstone_cuda_rpc.inc"
#include "gemstone_status.h"
#include "gemstone_cuda_compat.h"
#include "gemstone_rpc_transport.h"

#include <pthread.h>
#include <stddef.h>
#include <stdint.h>
#include <stdlib.h>
#include <string.h>

static const uint64_t gemstone___cudaGemstoneManagedRegister_high = UINT64_C(0xc6207aea8d73f5ba);
static const uint64_t gemstone___cudaGemstoneManagedRegister_low = UINT64_C(0x2db4cd6e3a364b1a);
static const uint64_t gemstone___cudaGemstoneManagedUnregister_high = UINT64_C(0x93e5439ad242c3c0);
static const uint64_t gemstone___cudaGemstoneManagedUnregister_low = UINT64_C(0x8e20904fec96004c);
static const uint64_t gemstone___cudaGemstoneRegisterGlobal_high = UINT64_C(0xb47a93542158aaec);
static const uint64_t gemstone___cudaGemstoneRegisterGlobal_low = UINT64_C(0x918af76c69f9b8e1);
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
static const uint64_t gemstone_cuLaunchHostFunc_high = UINT64_C(0xced40f65d252b18e);
static const uint64_t gemstone_cuLaunchHostFunc_low = UINT64_C(0xdc2b29f9f2e965b6);
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
static const uint64_t gemstone_cuModuleGetGlobal_v2_high = UINT64_C(0x8c4cb4f6691184b3);
static const uint64_t gemstone_cuModuleGetGlobal_v2_low = UINT64_C(0x31216cd8d1c8d868);
static const uint64_t gemstone_cuModuleLoadData_high = UINT64_C(0xfef489c6c628b458);
static const uint64_t gemstone_cuModuleLoadData_low = UINT64_C(0x480e5cb6fe964d3f);
static const uint64_t gemstone_cuModuleUnload_high = UINT64_C(0xb94c365c6c5c3902);
static const uint64_t gemstone_cuModuleUnload_low = UINT64_C(0x2a1466600700709a);
static const uint64_t gemstone_cuStreamAddCallback_high = UINT64_C(0x589d21fb33f2aca5);
static const uint64_t gemstone_cuStreamAddCallback_low = UINT64_C(0xbe72b9cfe9179b7a);

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

static int gemstone_is_probably_textual_ptx(const void *image) {
  const unsigned char first = *(const unsigned char *)image;
  return first == '\0' || first == '.' || first == '/' || first == ' ' ||
      first == '\t' || first == '\r' || first == '\n';
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

_Thread_local void *gemstone_current_context = NULL;

int32_t cuCtxCreate_v2(void **context, uint32_t flags, int32_t device) {
  if (context == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[8];
  gemstone_put_u32(payload, flags);
  gemstone_put_u32(payload + 4, (uint32_t)device);
  uint64_t handle = 0;
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_u64(gemstone_call(gemstone_cuCtxCreate_v2_high, gemstone_cuCtxCreate_v2_low, "cuCtxCreate_v2", payload, sizeof(payload), &response), &response, &handle);
  if (status == GEMSTONE_STATUS_SUCCESS) {
    *context = (void *)(uintptr_t)handle;
    gemstone_current_context = *context;
  }
  return status;
}

#if defined(_WIN32)
#define GEMSTONE_CUDA_API __stdcall
#else
#define GEMSTONE_CUDA_API
#endif

int32_t GEMSTONE_CUDA_API gemstone_cuCtxCreate_v4(
    void **context,
    void *parameters,
    uint32_t flags,
    int32_t device) {
  (void)parameters;
  return cuCtxCreate_v2(context, flags, device);
}

int32_t cuCtxDestroy_v2(void *context) {
  uint8_t payload[8];
  gemstone_put_u64(payload, (uint64_t)(uintptr_t)context);
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(gemstone_call(gemstone_cuCtxDestroy_v2_high, gemstone_cuCtxDestroy_v2_low, "cuCtxDestroy_v2", payload, sizeof(payload), &response), &response);
  if (status == GEMSTONE_STATUS_SUCCESS && context == gemstone_current_context) gemstone_current_context = NULL;
  return status;
}

int32_t cuCtxSynchronize(void) {
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(
      gemstone_call(gemstone_cuCtxSynchronize_high, gemstone_cuCtxSynchronize_low, "cuCtxSynchronize", NULL, 0, &response),
      &response);
  if (status == GEMSTONE_STATUS_SUCCESS) return gemstone_cuda_managed_synchronize_from_device();
  return status;
}

int32_t cuLaunchHostFunc(void *stream, GemstoneHostCallback callback, void *user_data) {
  const uint64_t callback_id = gemstone_rpc_register_host_callback(callback, user_data);
  if (callback_id == 0) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[24];
  gemstone_put_u64(payload, (uint64_t)(uintptr_t)stream);
  gemstone_put_u64(payload + 8, callback_id);
  gemstone_put_u64(payload + 16, callback_id);
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(
      gemstone_call(gemstone_cuLaunchHostFunc_high, gemstone_cuLaunchHostFunc_low, "cuLaunchHostFunc", payload, sizeof(payload), &response),
      &response);
  if (status != GEMSTONE_STATUS_SUCCESS) gemstone_rpc_unregister_callback(callback_id);
  return status;
}

int32_t cuStreamAddCallback(
    void *stream,
    GemstoneStreamCallback callback,
    void *user_data,
    uint32_t flags) {
  const uint64_t callback_id = gemstone_rpc_register_stream_callback(callback, user_data);
  if (callback_id == 0) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[28];
  gemstone_put_u64(payload, (uint64_t)(uintptr_t)stream);
  gemstone_put_u64(payload + 8, callback_id);
  gemstone_put_u64(payload + 16, callback_id);
  gemstone_put_u32(payload + 24, flags);
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(
      gemstone_call(gemstone_cuStreamAddCallback_high, gemstone_cuStreamAddCallback_low, "cuStreamAddCallback", payload, sizeof(payload), &response),
      &response);
  if (status != GEMSTONE_STATUS_SUCCESS) gemstone_rpc_unregister_callback(callback_id);
  return status;
}

int32_t cuMemAlloc_v2(uint64_t *pointer, size_t size) {
  if (pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint8_t payload[8];
  gemstone_put_u64(payload, (uint64_t)size);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_u64(gemstone_call(gemstone_cuMemAlloc_v2_high, gemstone_cuMemAlloc_v2_low, "cuMemAlloc_v2", payload, sizeof(payload), &response), &response, pointer);
}

int32_t cuMemFree_v2(uint64_t pointer) {
  if (pointer == 0) return GEMSTONE_STATUS_SUCCESS;
  int found = 0;
  const int32_t managed_status = gemstone_cuda_managed_free(pointer, &found);
  if (found) return managed_status;
  uint8_t payload[8];
  gemstone_put_u64(payload, pointer);
  GemstoneRpcBuffer response = {0};
  return gemstone_expect_unit(gemstone_call(gemstone_cuMemFree_v2_high, gemstone_cuMemFree_v2_low, "cuMemFree_v2", payload, sizeof(payload), &response), &response);
}

int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size) {
  if ((source == NULL && size != 0) || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 20) return GEMSTONE_STATUS_INVALID_VALUE;
  const uint32_t payload_size = (uint32_t)size + 20;
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
  gemstone_put_u64(payload, destination);
  gemstone_put_u32(payload + 8, (uint32_t)size);
  if (size != 0) memcpy(payload + 12, source, size);
  gemstone_put_u64(payload + 12 + size, (uint64_t)size);
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(gemstone_call(gemstone_cuMemcpyHtoD_v2_high, gemstone_cuMemcpyHtoD_v2_low, "cuMemcpyHtoD_v2", payload, payload_size, &response), &response);
  free(payload);
  if (status == GEMSTONE_STATUS_SUCCESS) gemstone_cuda_managed_copy_from_host(destination, source, size);
  return status;
}

int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size) {
  if ((destination == NULL && size != 0) || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 4) return GEMSTONE_STATUS_INVALID_VALUE;
  if (gemstone_cuda_managed_copy_to_host(destination, source, size)) return GEMSTONE_STATUS_SUCCESS;
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

int32_t gemstone_managed_register_remote(uint64_t pointer, size_t size) {
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

int32_t gemstone_managed_unregister_remote(uint64_t pointer) {
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

int32_t gemstone_register_global_remote(
    uint64_t module,
    const char *name,
    const void *initial,
    size_t size) {
  if (module == 0 || name == NULL || (initial == NULL && size != 0)) return GEMSTONE_STATUS_INVALID_VALUE;
  const size_t name_size = gemstone_bounded_string_length(name, 4096);
  if (name_size == 0 || name_size == 4096 ||
      size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 16 - name_size) return GEMSTONE_STATUS_INVALID_VALUE;
  const uint32_t payload_size = (uint32_t)(16 + name_size + size);
  uint8_t *payload = (uint8_t *)malloc(payload_size);
  if (payload == NULL) return GEMSTONE_STATUS_UNKNOWN;
  gemstone_put_u64(payload, module);
  gemstone_put_u32(payload + 8, (uint32_t)name_size);
  memcpy(payload + 12, name, name_size);
  gemstone_put_u32(payload + 12 + name_size, (uint32_t)size);
  if (size != 0) memcpy(payload + 16 + name_size, initial, size);
  GemstoneRpcBuffer response = {0};
  const int32_t status = gemstone_expect_unit(
      gemstone_call(
          gemstone___cudaGemstoneRegisterGlobal_high,
          gemstone___cudaGemstoneRegisterGlobal_low,
          "__cudaGemstoneRegisterGlobal",
          payload,
          payload_size,
          &response),
      &response);
  free(payload);
  return status;
}

int32_t cuModuleLoadData(void **module, const void *image) {
  if (module == NULL || image == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  if (!gemstone_is_probably_textual_ptx(image)) {
    const char *ptx = gemstone_cuda_find_uncompressed_ptx(image);
    if (ptx != NULL) image = ptx;
  }
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

int32_t cuModuleGetGlobal_v2(uint64_t *pointer, size_t *bytes, void *module, const char *name) {
  if (pointer == NULL || module == NULL || name == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  size_t ignored_bytes = 0;
  if (bytes == NULL) bytes = &ignored_bytes;
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
      gemstone_cuModuleGetGlobal_v2_high,
      gemstone_cuModuleGetGlobal_v2_low,
      "cuModuleGetGlobal_v2",
      payload,
      payload_size,
      &response);
  free(payload);
  if (status == GEMSTONE_STATUS_SUCCESS) {
    if (response.size != 16) status = GEMSTONE_STATUS_UNKNOWN;
    else {
      *pointer = gemstone_get_u64(response.data);
      *bytes = (size_t)gemstone_get_u64(response.data + 8);
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
  int32_t managed_status = gemstone_cuda_managed_synchronize_to_device();
  if (managed_status != GEMSTONE_STATUS_SUCCESS) return managed_status;
  (void)stream;
  pthread_mutex_lock(&gemstone_kernel_mutex);
  GemstoneKernelSignature *signature = gemstone_kernel_signatures;
  while (signature != NULL && signature->function != (uint64_t)(uintptr_t)function) signature = signature->next;
  if (signature == NULL || (signature->count != 0 && kernel_params == NULL)) {
    pthread_mutex_unlock(&gemstone_kernel_mutex);
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  size_t payload_size = 49;
  for (uint32_t index = 0; index < signature->count; ++index) {
    const uint32_t width = signature->widths[index];
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
  gemstone_put_u64(payload + 36, (uint64_t)(uintptr_t)stream);
  gemstone_put_u32(payload + 44, signature->count);
  size_t offset = 48;
  for (uint32_t index = 0; index < signature->count; ++index) {
    const uint32_t width = signature->widths[index];
    gemstone_put_u32(payload + offset, width);
    memcpy(payload + offset + 4, kernel_params[index], width);
    offset += 4 + width;
  }
  payload[offset] = 0;
  pthread_mutex_unlock(&gemstone_kernel_mutex);
  GemstoneRpcBuffer response = {0};
  int32_t status = gemstone_expect_unit(
      gemstone_call(gemstone_cuLaunchKernel_high, gemstone_cuLaunchKernel_low, "cuLaunchKernel", payload, (uint32_t)payload_size, &response),
      &response);
  free(payload);
  if (status == GEMSTONE_STATUS_SUCCESS) {
    gemstone_cuda_managed_mark_device_dirty();
    status = gemstone_cuda_managed_synchronize_from_device();
  }
  return status;
}

