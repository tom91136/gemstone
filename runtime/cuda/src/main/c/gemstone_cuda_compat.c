#include "gemstone_cuda_compat.h"
#include "gemstone_status.h"
#include "gemstone_rpc_transport.h"

#include <dlfcn.h>
#include <limits.h>
#include <stdint.h>
#include <pthread.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

typedef struct GemstoneFatBinaryWrapper {
  int32_t magic;
  int32_t version;
  const void *data;
  void *filename;
} GemstoneFatBinaryWrapper;

static int32_t gemstone_ptx_target_version(const char *ptx) {
  if (ptx == NULL) return 0;
  const char *cursor = ptx;
  while ((cursor = strstr(cursor, ".target")) != NULL) {
    const char *token = cursor + sizeof(".target") - 1;
    while (*token == ' ' || *token == '\t' || *token == '\r') ++token;
    if (strncmp(token, "sm_", 3) == 0) token += 3;
    else if (strncmp(token, "compute_", 8) == 0) token += 8;
    else {
      ++cursor;
      continue;
    }
    if (*token < '0' || *token > '9') {
      ++cursor;
      continue;
    }
    int32_t value = 0;
    while (*token >= '0' && *token <= '9') {
      if (value > (INT32_MAX - (*token - '0')) / 10) return 0;
      value = value * 10 + (*token - '0');
      ++token;
    }
    return value;
  }
  return 0;
}

static int32_t gemstone_configured_ptx_version(void) {
  const char *value = getenv("GEMSTONE_CUDA_TARGET_ARCH");
  if (value == NULL) return 0;
  if (strncmp(value, "compute_", 8) == 0) value += 8;
  else if (strncmp(value, "sm_", 3) == 0) value += 3;
  else return 0;
  if (*value < '0' || *value > '9') return 0;
  int32_t version = 0;
  while (*value >= '0' && *value <= '9') {
    if (version > (INT32_MAX - (*value - '0')) / 10) return 0;
    version = version * 10 + (*value - '0');
    ++value;
  }
  return version;
}

static int32_t gemstone_registered_ptx_version(const void *function);

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

typedef unsigned long long (*GemstoneZstdFrameContentSize)(const void *, size_t);
typedef size_t (*GemstoneZstdFindFrameCompressedSize)(const void *, size_t);
typedef size_t (*GemstoneZstdDecompress)(void *, size_t, const void *, size_t);
typedef unsigned (*GemstoneZstdIsError)(size_t);
typedef int (*GemstoneLz4DecompressSafe)(const char *, char *, int, int);

static pthread_once_t gemstone_zstd_once = PTHREAD_ONCE_INIT;
static void *gemstone_zstd_library = NULL;
static GemstoneZstdFrameContentSize gemstone_zstd_frame_size = NULL;
static GemstoneZstdFindFrameCompressedSize gemstone_zstd_compressed_size = NULL;
static GemstoneZstdDecompress gemstone_zstd_decompress = NULL;
static GemstoneZstdIsError gemstone_zstd_is_error = NULL;
static pthread_once_t gemstone_lz4_once = PTHREAD_ONCE_INIT;
static void *gemstone_lz4_library = NULL;
static GemstoneLz4DecompressSafe gemstone_lz4_decompress = NULL;

static _Thread_local char *gemstone_decompressed_ptx = NULL;
static _Thread_local char *gemstone_lz4_decompressed_ptx = NULL;
static _Thread_local char *gemstone_combined_ptx = NULL;

static void gemstone_load_zstd(void) {
  gemstone_zstd_library = dlopen("libzstd.so.1", RTLD_LAZY | RTLD_LOCAL);
  if (gemstone_zstd_library == NULL) return;
  gemstone_zstd_frame_size =
      (GemstoneZstdFrameContentSize)dlsym(gemstone_zstd_library, "ZSTD_getFrameContentSize");
  gemstone_zstd_compressed_size =
      (GemstoneZstdFindFrameCompressedSize)dlsym(gemstone_zstd_library, "ZSTD_findFrameCompressedSize");
  gemstone_zstd_decompress =
      (GemstoneZstdDecompress)dlsym(gemstone_zstd_library, "ZSTD_decompress");
  gemstone_zstd_is_error = (GemstoneZstdIsError)dlsym(gemstone_zstd_library, "ZSTD_isError");
  if (gemstone_zstd_frame_size == NULL || gemstone_zstd_compressed_size == NULL ||
      gemstone_zstd_decompress == NULL || gemstone_zstd_is_error == NULL) {
    dlclose(gemstone_zstd_library);
    gemstone_zstd_library = NULL;
    gemstone_zstd_frame_size = NULL;
    gemstone_zstd_compressed_size = NULL;
    gemstone_zstd_decompress = NULL;
    gemstone_zstd_is_error = NULL;
  }
}

static void gemstone_load_lz4(void) {
  gemstone_lz4_library = dlopen("liblz4.so.1", RTLD_LAZY | RTLD_LOCAL);
  if (gemstone_lz4_library == NULL) return;
  gemstone_lz4_decompress =
      (GemstoneLz4DecompressSafe)dlsym(gemstone_lz4_library, "LZ4_decompress_safe");
  if (gemstone_lz4_decompress == NULL) {
    dlclose(gemstone_lz4_library);
    gemstone_lz4_library = NULL;
  }
}

static const char *gemstone_decompress_ptx(const void *source, size_t source_size) {
  pthread_once(&gemstone_zstd_once, gemstone_load_zstd);
  if (gemstone_zstd_library == NULL) return NULL;
  const unsigned long long size = gemstone_zstd_frame_size(source, source_size);
  const size_t compressed = gemstone_zstd_compressed_size(source, source_size);
  if (size == ULLONG_MAX || size == ULLONG_MAX - 1 || size == 0 || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 1 ||
      gemstone_zstd_is_error(compressed) || compressed > source_size) {
    return NULL;
  }
  free(gemstone_decompressed_ptx);
  gemstone_decompressed_ptx = (char *)malloc((size_t)size + 1);
  if (gemstone_decompressed_ptx == NULL) return NULL;
  const size_t result = gemstone_zstd_decompress(gemstone_decompressed_ptx, (size_t)size, source, compressed);
  if (gemstone_zstd_is_error(result) || result != (size_t)size) {
    free(gemstone_decompressed_ptx);
    gemstone_decompressed_ptx = NULL;
    return NULL;
  }
  gemstone_decompressed_ptx[size] = '\0';
  return gemstone_decompressed_ptx;
}

static const char *gemstone_decompress_lz4(
    const void *source,
    size_t source_size,
    size_t destination_size,
    size_t *result_size) {
  if (source == NULL || result_size == NULL || source_size > INT_MAX || destination_size == 0 ||
      destination_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 1 || destination_size > INT_MAX) return NULL;
  pthread_once(&gemstone_lz4_once, gemstone_load_lz4);
  if (gemstone_lz4_library == NULL) return NULL;
  char *next = (char *)realloc(gemstone_lz4_decompressed_ptx, destination_size + 1);
  if (next == NULL) return NULL;
  gemstone_lz4_decompressed_ptx = next;
  const int result = gemstone_lz4_decompress(
      (const char *)source,
      gemstone_lz4_decompressed_ptx,
      (int)source_size,
      (int)destination_size);
  if (result < 0) return NULL;
  gemstone_lz4_decompressed_ptx[result] = '\0';
  *result_size = (size_t)result;
  return gemstone_lz4_decompressed_ptx;
}

static int gemstone_append_ptx(const char *source, size_t source_size, size_t *combined_size) {
  const size_t separator = *combined_size == 0 ? 0 : 1;
  if (*combined_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 1 - separator ||
      source_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 1 - separator - *combined_size) return 0;
  const size_t next_size = *combined_size + separator + source_size;
  char *next = (char *)realloc(gemstone_combined_ptx, next_size + 1);
  if (next == NULL) return 0;
  gemstone_combined_ptx = next;
  size_t write_offset = *combined_size + separator;
  if (separator != 0) gemstone_combined_ptx[*combined_size] = '\n';
  if (*combined_size == 0) {
    memcpy(gemstone_combined_ptx + write_offset, source, source_size);
    write_offset += source_size;
  } else {
    size_t read_offset = 0;
    while (read_offset < source_size) {
      const size_t remaining = source_size - read_offset;
      const char *line = source + read_offset;
      const char *line_end = (const char *)memchr(line, '\n', remaining);
      const size_t line_size = line_end == NULL ? remaining : (size_t)(line_end - line) + 1;
      const char *first = line;
      while (first < line + line_size && (*first == ' ' || *first == '\t' || *first == '\r')) ++first;
      const size_t first_size = (size_t)(line + line_size - first);
      const int version_header = first_size >= 8 && memcmp(first, ".version", 8) == 0 &&
          (first_size == 8 || first[8] == ' ' || first[8] == '\t' || first[8] == '\r' || first[8] == '\n');
      const int target_header = first_size >= 7 && memcmp(first, ".target", 7) == 0 &&
          (first_size == 7 || first[7] == ' ' || first[7] == '\t' || first[7] == '\r' || first[7] == '\n');
      const int address_size_header = first_size >= 13 && memcmp(first, ".address_size", 13) == 0 &&
          (first_size == 13 || first[13] == ' ' || first[13] == '\t' || first[13] == '\r' || first[13] == '\n');
      const int header = version_header || target_header || address_size_header;
      if (!header) {
        memcpy(gemstone_combined_ptx + write_offset, line, line_size);
        write_offset += line_size;
      }
      read_offset += line_size;
    }
  }
  gemstone_combined_ptx[write_offset] = '\0';
  *combined_size = write_offset;
  return 1;
}

static int gemstone_find_ptx_text(
    const char *payload,
    size_t payload_size,
    const char **source,
    size_t *source_size) {
  if (payload == NULL || source == NULL || source_size == NULL) return 0;
  const size_t search_size = payload_size < 128 ? payload_size : 128;
  for (size_t prefix = 0; prefix + 8 <= search_size; ++prefix) {
    if (memcmp(payload + prefix, ".version", 8) != 0) continue;
    if (prefix + 8 < payload_size && payload[prefix + 8] != ' ' && payload[prefix + 8] != '\t' &&
        payload[prefix + 8] != '\r' && payload[prefix + 8] != '\n') continue;
    size_t length = prefix;
    while (length < payload_size && payload[length] != '\0') ++length;
    const size_t text_size = length - prefix;
    /* libcudart submits header-only PTX fatbins during lazy initialization.
     * Preserve them as valid empty modules; requiring an entry or global here
     * would make an ordinary runtime allocation fail before user code loads. */
    *source = payload + prefix;
    *source_size = text_size;
    return 1;
  }
  return 0;
}

static int gemstone_append_fatbin_ptx(const uint8_t *data, size_t *combined_size) {
  if (data == NULL || gemstone_read_native_u32(data) != UINT32_C(0xba55ed50)) return 0;
  const uint16_t outer_header_size = gemstone_read_native_u16(data + 6);
  const uint64_t content_size = gemstone_read_native_u64(data + 8);
  if (outer_header_size < 16 || content_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE) return 0;
  uint64_t total_size = (uint64_t)outer_header_size + content_size;
  uint64_t offset = outer_header_size;
  /* nvcc can wrap the actual fatbin in an empty outer header followed by a
   * second header carrying the real payload size.  The payload records begin
   * after that inner header, not immediately after the outer one. */
  if (content_size == 0 && gemstone_read_native_u32(data + offset) == UINT32_C(0xba55ed50)) {
    const uint16_t inner_header_size = gemstone_read_native_u16(data + offset + 6);
    const uint64_t inner_content_size = gemstone_read_native_u64(data + offset + 8);
    if (inner_header_size < 16 || inner_content_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE) return 0;
    offset += inner_header_size;
    total_size = offset + inner_content_size;
  }
  while (offset <= total_size && total_size - offset >= 64) {
    const uint8_t *entry = data + offset;
    const uint16_t kind = gemstone_read_native_u16(entry);
    const uint32_t header_size = gemstone_read_native_u32(entry + 4);
    const uint32_t payload_size = gemstone_read_native_u32(entry + 8);
    const uint32_t compressed_size = gemstone_read_native_u32(entry + 16);
    const uint64_t flags = gemstone_read_native_u64(entry + 40);
    const uint64_t uncompressed_payload = gemstone_read_native_u64(entry + 56);
    const uint32_t stored_size = payload_size > compressed_size ? payload_size : compressed_size;
    if (header_size < 64 || (uint64_t)header_size > total_size - offset ||
        (uint64_t)stored_size > total_size - offset - header_size) return 0;
    const char *payload = (const char *)(entry + header_size);
    size_t content_size = stored_size;
    const char *content = payload;
    if ((flags & UINT64_C(0x2000)) != 0) {
      content = gemstone_decompress_lz4(
          payload,
          compressed_size,
          uncompressed_payload == 0 ? payload_size : (size_t)uncompressed_payload,
          &content_size);
    } else if ((flags & UINT64_C(0x8000)) != 0) {
      content = gemstone_decompress_ptx(payload, compressed_size);
      if (content != NULL) content_size = strlen(content);
    }
    const char *ptx = NULL;
    size_t ptx_size = 0;
    if ((kind & 1u) != 0 && content != NULL && gemstone_find_ptx_text(content, content_size, &ptx, &ptx_size) &&
        !gemstone_append_ptx(ptx, ptx_size, combined_size)) return 0;
    offset += (uint64_t)header_size + stored_size;
  }
  return 1;
}

const char *gemstone_cuda_find_uncompressed_ptx(const void *fat_binary) {
  if (fat_binary == NULL) return NULL;
  const GemstoneFatBinaryWrapper *wrapper = (const GemstoneFatBinaryWrapper *)fat_binary;
  const int is_wrapper = wrapper->magic == INT32_C(0x466243b1);
  const uint8_t *data = is_wrapper
      ? (const uint8_t *)wrapper->data : (const uint8_t *)fat_binary;
  if (data == NULL) return NULL;
  if (gemstone_read_native_u32(data) != UINT32_C(0xba55ed50)) return NULL;
  free(gemstone_combined_ptx);
  gemstone_combined_ptx = NULL;
  size_t combined_size = 0;
  if (!gemstone_append_fatbin_ptx(data, &combined_size)) {
    free(gemstone_combined_ptx);
    gemstone_combined_ptx = NULL;
    return NULL;
  }
  /* Relocatable device code is kept by nvcc in a null-terminated array of
   * fatbin pointers stored in the wrapper's fourth field.  The ordinary
   * .nv_fatbin image only contains registration/runtime stubs; include the
   * prelinked images as well so the driver-facing module contains the actual
   * Thrust/CUB entry points. */
  if (is_wrapper && wrapper->version >= 2 && wrapper->filename != NULL) {
    const void *const *prelinked = (const void *const *)wrapper->filename;
    for (size_t index = 0; index < 64 && prelinked[index] != NULL; ++index) {
      const uint8_t *candidate = (const uint8_t *)prelinked[index];
      if (gemstone_read_native_u32(candidate) != UINT32_C(0xba55ed50) ||
          !gemstone_append_fatbin_ptx(candidate, &combined_size)) {
        free(gemstone_combined_ptx);
        gemstone_combined_ptx = NULL;
        return NULL;
      }
    }
  }
  return combined_size == 0 ? NULL : gemstone_combined_ptx;
}

extern int32_t cuMemAlloc_v2(uint64_t *pointer, size_t size);
extern int32_t cuMemFree_v2(uint64_t pointer);
extern int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size);
extern int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size);
extern int32_t cuModuleGetGlobal_v2(uint64_t *pointer, size_t *bytes, void *module, const char *name);
extern int32_t gemstone_managed_register_remote(uint64_t pointer, size_t size);
extern int32_t gemstone_managed_unregister_remote(uint64_t pointer);

typedef struct GemstoneMirroredAllocation {
  uint64_t pointer;
  size_t size;
  struct GemstoneMirroredAllocation *next;
} GemstoneMirroredAllocation;

static pthread_mutex_t gemstone_mirrored_mutex = PTHREAD_MUTEX_INITIALIZER;
static GemstoneMirroredAllocation *gemstone_mirrored_allocations = NULL;
static int gemstone_managed_device_dirty = 0;
static _Thread_local int gemstone_managed_sync_to_device_active = 0;
static _Thread_local int gemstone_managed_sync_from_device_active = 0;

int32_t gemstone_cuda_managed_allocate(uint64_t *pointer, size_t size) {
  if (pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const size_t allocation_size = size == 0 ? 1 : size;
  void *host = malloc(allocation_size);
  if (host == NULL) return GEMSTONE_STATUS_UNKNOWN;
  const uint64_t address = (uint64_t)(uintptr_t)host;
  const int32_t registration = gemstone_managed_register_remote(address, allocation_size);
  if (registration != GEMSTONE_STATUS_SUCCESS) {
    free(host);
    return registration;
  }
  GemstoneMirroredAllocation *entry = (GemstoneMirroredAllocation *)calloc(1, sizeof(*entry));
  if (entry == NULL) {
    (void)gemstone_managed_unregister_remote(address);
    free(host);
    return GEMSTONE_STATUS_UNKNOWN;
  }
  entry->pointer = address;
  entry->size = allocation_size;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  entry->next = gemstone_mirrored_allocations;
  gemstone_mirrored_allocations = entry;
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  *pointer = address;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t gemstone_cuda_managed_free(uint64_t pointer, int *found) {
  if (found == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *found = 0;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  GemstoneMirroredAllocation **cursor = &gemstone_mirrored_allocations;
  while (*cursor != NULL && (*cursor)->pointer != pointer) cursor = &(*cursor)->next;
  if (*cursor == NULL) {
    pthread_mutex_unlock(&gemstone_mirrored_mutex);
    return GEMSTONE_STATUS_SUCCESS;
  }
  GemstoneMirroredAllocation *entry = *cursor;
  *cursor = entry->next;
  *found = 1;
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  const int32_t status = gemstone_managed_unregister_remote(pointer);
  free((void *)(uintptr_t)pointer);
  free(entry);
  return status;
}

int32_t gemstone_cuda_managed_synchronize_to_device(void) {
  // A preceding device launch may have updated a managed allocation.  Bring
  // that state back to the host before the next launch copies host-visible
  // managed bytes into the emulator, otherwise stale host bytes overwrite the
  // device result between dependent kernels.
  int32_t status = gemstone_managed_device_dirty
      ? gemstone_cuda_managed_synchronize_from_device()
      : GEMSTONE_STATUS_SUCCESS;
  if (status != GEMSTONE_STATUS_SUCCESS) return status;
  gemstone_managed_sync_to_device_active = 1;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  for (GemstoneMirroredAllocation *entry = gemstone_mirrored_allocations;
       entry != NULL && status == GEMSTONE_STATUS_SUCCESS;
       entry = entry->next) {
    status = cuMemcpyHtoD_v2(entry->pointer, (const void *)(uintptr_t)entry->pointer, entry->size);
  }
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  gemstone_managed_sync_to_device_active = 0;
  return status;
}

int gemstone_cuda_managed_copy_to_host(void *destination, uint64_t pointer, size_t size) {
  if (gemstone_managed_sync_from_device_active) return 0;
  int handled = 0;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  for (GemstoneMirroredAllocation *entry = gemstone_mirrored_allocations;
       entry != NULL && !handled;
       entry = entry->next) {
    const uint64_t offset = pointer - entry->pointer;
    if (pointer >= entry->pointer && offset <= entry->size && size <= entry->size - offset) {
      if (!gemstone_managed_device_dirty) {
        if (size != 0) memcpy(destination, (const void *)(uintptr_t)pointer, size);
        handled = 1;
      }
    }
  }
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  return handled;
}

int gemstone_cuda_managed_copy_from_host(uint64_t pointer, const void *source, size_t size) {
  if (gemstone_managed_sync_to_device_active) return 0;
  int handled = 0;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  for (GemstoneMirroredAllocation *entry = gemstone_mirrored_allocations;
       entry != NULL && !handled;
       entry = entry->next) {
    const uint64_t offset = pointer - entry->pointer;
    if (pointer >= entry->pointer && offset <= entry->size && size <= entry->size - offset) {
      if (size != 0) {
        memmove((void *)(uintptr_t)pointer, source, size);
      }
      handled = 1;
    }
  }
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  return handled;
}

void gemstone_cuda_managed_mark_device_dirty(void) {
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  gemstone_managed_device_dirty = 1;
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
}

int32_t gemstone_cuda_managed_synchronize_from_device(void) {
  int32_t status = GEMSTONE_STATUS_SUCCESS;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  if (gemstone_managed_device_dirty) {
    gemstone_managed_sync_from_device_active = 1;
    for (GemstoneMirroredAllocation *entry = gemstone_mirrored_allocations;
       entry != NULL && status == GEMSTONE_STATUS_SUCCESS;
       entry = entry->next) {
      status = cuMemcpyDtoH_v2((void *)(uintptr_t)entry->pointer, entry->pointer, entry->size);
    }
    gemstone_managed_sync_from_device_active = 0;
    if (status == GEMSTONE_STATUS_SUCCESS) gemstone_managed_device_dirty = 0;
  }
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  return status;
}

int32_t cuMemAllocManaged(uint64_t *pointer, size_t size, uint32_t flags) {
  (void)flags;
  return gemstone_cuda_managed_allocate(pointer, size);
}

int32_t cuMemAllocHost_v2(void **pointer, size_t size) {
  return gemstone_cuda_managed_allocate((uint64_t *)pointer, size);
}

int32_t cuMemAllocHost(void **pointer, size_t size) {
  return cuMemAllocHost_v2(pointer, size);
}

int32_t cuMemFreeHost(void *pointer) {
  int found = 0;
  const int32_t status = gemstone_cuda_managed_free((uint64_t)(uintptr_t)pointer, &found);
  return found ? status : GEMSTONE_STATUS_INVALID_VALUE;
}

int32_t cuMemHostAlloc(void **pointer, size_t size, uint32_t flags) {
  (void)flags;
  return cuMemAllocHost_v2(pointer, size);
}

int32_t cuMemHostGetDevicePointer_v2(uint64_t *pointer, void *host_pointer, uint32_t flags) {
  (void)flags;
  if (pointer == NULL || host_pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  pthread_mutex_lock(&gemstone_mirrored_mutex);
  GemstoneMirroredAllocation *entry = gemstone_mirrored_allocations;
  while (entry != NULL && entry->pointer != (uint64_t)(uintptr_t)host_pointer) entry = entry->next;
  if (entry != NULL) *pointer = entry->pointer;
  pthread_mutex_unlock(&gemstone_mirrored_mutex);
  return entry == NULL ? GEMSTONE_STATUS_INVALID_VALUE : GEMSTONE_STATUS_SUCCESS;
}

int32_t cuMemHostGetFlags(uint32_t *flags, void *host_pointer) {
  if (flags == NULL || host_pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint64_t device_pointer = 0;
  const int32_t status = cuMemHostGetDevicePointer_v2(&device_pointer, host_pointer, 0);
  if (status == GEMSTONE_STATUS_SUCCESS) *flags = 0;
  return status;
}

enum {
  GEMSTONE_CUDA_MEMORYTYPE_HOST = 1,
  GEMSTONE_CUDA_MEMORYTYPE_DEVICE = 2,
  GEMSTONE_CUDA_MEMORYTYPE_ARRAY = 3
};

typedef struct GemstoneCudaArray {
  uint64_t device;
  size_t width;
  size_t height;
  size_t depth;
  size_t pitch;
  uint32_t format;
  uint32_t channels;
  uint32_t flags;
} GemstoneCudaArray;

typedef struct GemstoneCudaArrayDescriptor {
  size_t width;
  size_t height;
  uint32_t format;
  uint32_t channels;
} GemstoneCudaArrayDescriptor;

typedef struct GemstoneCudaArray3DDescriptor {
  size_t width;
  size_t height;
  size_t depth;
  uint32_t format;
  uint32_t channels;
  uint32_t flags;
} GemstoneCudaArray3DDescriptor;

typedef struct GemstoneCudaMemcpy2D {
  size_t src_x_in_bytes;
  size_t src_y;
  uint32_t src_memory_type;
  const void *src_host;
  uint64_t src_device;
  void *src_array;
  size_t src_pitch;
  size_t dst_x_in_bytes;
  size_t dst_y;
  uint32_t dst_memory_type;
  void *dst_host;
  uint64_t dst_device;
  void *dst_array;
  size_t dst_pitch;
  size_t width_in_bytes;
  size_t height;
} GemstoneCudaMemcpy2D;

typedef struct GemstoneCudaMemcpy3D {
  size_t src_x_in_bytes;
  size_t src_y;
  size_t src_z;
  size_t src_lod;
  uint32_t src_memory_type;
  const void *src_host;
  uint64_t src_device;
  void *src_array;
  void *src_reserved;
  size_t src_pitch;
  size_t src_height;
  size_t dst_x_in_bytes;
  size_t dst_y;
  size_t dst_z;
  size_t dst_lod;
  uint32_t dst_memory_type;
  void *dst_host;
  uint64_t dst_device;
  void *dst_array;
  void *dst_reserved;
  size_t dst_pitch;
  size_t dst_height;
  size_t width_in_bytes;
  size_t height;
  size_t depth;
} GemstoneCudaMemcpy3D;

typedef struct GemstoneCudaArrayDescriptorV1 {
  uint32_t width;
  uint32_t height;
  uint32_t format;
  uint32_t channels;
} GemstoneCudaArrayDescriptorV1;

typedef struct GemstoneCudaArray3DDescriptorV1 {
  uint32_t width;
  uint32_t height;
  uint32_t depth;
  uint32_t format;
  uint32_t channels;
  uint32_t flags;
} GemstoneCudaArray3DDescriptorV1;

typedef struct GemstoneCudaMemcpy2DV1 {
  uint32_t src_x_in_bytes;
  uint32_t src_y;
  uint32_t src_memory_type;
  const void *src_host;
  uint32_t src_device;
  void *src_array;
  uint32_t src_pitch;
  uint32_t dst_x_in_bytes;
  uint32_t dst_y;
  uint32_t dst_memory_type;
  void *dst_host;
  uint32_t dst_device;
  void *dst_array;
  uint32_t dst_pitch;
  uint32_t width_in_bytes;
  uint32_t height;
} GemstoneCudaMemcpy2DV1;

typedef struct GemstoneCudaMemcpy3DV1 {
  uint32_t src_x_in_bytes;
  uint32_t src_y;
  uint32_t src_z;
  uint32_t src_lod;
  uint32_t src_memory_type;
  const void *src_host;
  uint32_t src_device;
  void *src_array;
  void *src_reserved;
  uint32_t src_pitch;
  uint32_t src_height;
  uint32_t dst_x_in_bytes;
  uint32_t dst_y;
  uint32_t dst_z;
  uint32_t dst_lod;
  uint32_t dst_memory_type;
  void *dst_host;
  uint32_t dst_device;
  void *dst_array;
  void *dst_reserved;
  uint32_t dst_pitch;
  uint32_t dst_height;
  uint32_t width_in_bytes;
  uint32_t height;
  uint32_t depth;
} GemstoneCudaMemcpy3DV1;

static int gemstone_size_add(size_t left, size_t right, size_t *result) {
  if (right > SIZE_MAX - left) return 0;
  *result = left + right;
  return 1;
}

static int gemstone_size_mul(size_t left, size_t right, size_t *result) {
  if (left != 0 && right > SIZE_MAX / left) return 0;
  *result = left * right;
  return 1;
}

static size_t gemstone_format_bytes(uint32_t format) {
  switch (format) {
    case 0x01:
    case 0x08:
      return 1;
    case 0x02:
    case 0x09:
    case 0x10:
      return 2;
    case 0x03:
    case 0x0a:
    case 0x20:
      return 4;
    default:
      return 0;
  }
}

static int gemstone_array_allocation(
    size_t width,
    size_t height,
    size_t depth,
    size_t element_bytes,
    uint32_t channels,
    size_t *pitch,
    size_t *bytes) {
  size_t row_bytes = 0;
  size_t rows = 0;
  if (width == 0 || height == 0 || depth == 0 || channels == 0 || element_bytes == 0) return 0;
  if (!gemstone_size_mul(width, element_bytes, &row_bytes) ||
      !gemstone_size_mul(row_bytes, (size_t)channels, &row_bytes) ||
      !gemstone_size_mul(height, depth, &rows) ||
      !gemstone_size_mul(row_bytes, rows, bytes)) {
    return 0;
  }
  *pitch = row_bytes;
  return 1;
}

static int32_t gemstone_array_create(
    void **handle,
    size_t width,
    size_t height,
    size_t depth,
    uint32_t format,
    uint32_t channels,
    uint32_t flags) {
  if (handle == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const size_t actual_depth = depth == 0 ? 1 : depth;
  const size_t element_bytes = gemstone_format_bytes(format);
  size_t pitch = 0;
  size_t bytes = 0;
  if (!gemstone_array_allocation(width, height, actual_depth, element_bytes, channels, &pitch, &bytes)) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  GemstoneCudaArray *array = (GemstoneCudaArray *)calloc(1, sizeof(*array));
  if (array == NULL) return GEMSTONE_STATUS_UNKNOWN;
  const int32_t status = cuMemAlloc_v2(&array->device, bytes);
  if (status != GEMSTONE_STATUS_SUCCESS) {
    free(array);
    return status;
  }
  array->width = width;
  array->height = height;
  array->depth = actual_depth;
  array->pitch = pitch;
  array->format = format;
  array->channels = channels;
  array->flags = flags;
  *handle = array;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuArray3DCreate_v2(void **handle, const void *descriptor) {
  if (descriptor == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaArray3DDescriptor *value = (const GemstoneCudaArray3DDescriptor *)descriptor;
  return gemstone_array_create(handle, value->width, value->height, value->depth, value->format, value->channels, value->flags);
}

int32_t cuArrayCreate_v2(void **handle, const void *descriptor) {
  if (descriptor == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaArrayDescriptor *value = (const GemstoneCudaArrayDescriptor *)descriptor;
  return gemstone_array_create(handle, value->width, value->height, 1, value->format, value->channels, 0);
}

int32_t cuArrayDestroy(void *handle) {
  if (handle == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneCudaArray *array = (GemstoneCudaArray *)handle;
  const int32_t status = cuMemFree_v2(array->device);
  if (status == GEMSTONE_STATUS_SUCCESS) free(array);
  return status;
}

int32_t cuArray3DGetDescriptor_v2(void *descriptor, void *handle) {
  if (descriptor == NULL || handle == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaArray *array = (const GemstoneCudaArray *)handle;
  GemstoneCudaArray3DDescriptor *value = (GemstoneCudaArray3DDescriptor *)descriptor;
  value->width = array->width;
  value->height = array->height;
  value->depth = array->depth;
  value->format = array->format;
  value->channels = array->channels;
  value->flags = array->flags;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuArrayGetDescriptor_v2(void *descriptor, void *handle) {
  if (descriptor == NULL || handle == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaArray *array = (const GemstoneCudaArray *)handle;
  GemstoneCudaArrayDescriptor *value = (GemstoneCudaArrayDescriptor *)descriptor;
  value->width = array->width;
  value->height = array->height;
  value->format = array->format;
  value->channels = array->channels;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuArray3DCreate(void **handle, const void *descriptor) {
  if (descriptor == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaArray3DDescriptorV1 *value = (const GemstoneCudaArray3DDescriptorV1 *)descriptor;
  return gemstone_array_create(handle, value->width, value->height, value->depth,
                                value->format, value->channels, value->flags);
}

int32_t cuArray3DGetDescriptor(void *descriptor, void *handle) {
  if (descriptor == NULL || handle == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaArray *array = (const GemstoneCudaArray *)handle;
  if (array->width > UINT32_MAX || array->height > UINT32_MAX || array->depth > UINT32_MAX) {
    return GEMSTONE_STATUS_NOT_SUPPORTED;
  }
  GemstoneCudaArray3DDescriptorV1 *value = (GemstoneCudaArray3DDescriptorV1 *)descriptor;
  value->width = (uint32_t)array->width;
  value->height = (uint32_t)array->height;
  value->depth = (uint32_t)array->depth;
  value->format = array->format;
  value->channels = array->channels;
  value->flags = array->flags;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuArrayCreate(void **handle, const void *descriptor) {
  if (descriptor == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaArrayDescriptorV1 *value = (const GemstoneCudaArrayDescriptorV1 *)descriptor;
  return gemstone_array_create(handle, value->width, value->height, 1, value->format, value->channels, 0);
}

int32_t cuArrayGetDescriptor(void *descriptor, void *handle) {
  if (descriptor == NULL || handle == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaArray *array = (const GemstoneCudaArray *)handle;
  if (array->width > UINT32_MAX || array->height > UINT32_MAX) return GEMSTONE_STATUS_NOT_SUPPORTED;
  GemstoneCudaArrayDescriptorV1 *value = (GemstoneCudaArrayDescriptorV1 *)descriptor;
  value->width = (uint32_t)array->width;
  value->height = (uint32_t)array->height;
  value->format = array->format;
  value->channels = array->channels;
  return GEMSTONE_STATUS_SUCCESS;
}

static int gemstone_copy_offset(size_t y, size_t pitch, size_t x, size_t *offset) {
  return gemstone_size_mul(y, pitch, offset) && gemstone_size_add(*offset, x, offset);
}

static int gemstone_array_bounds(
    const GemstoneCudaArray *array,
    size_t x,
    size_t y,
    size_t width,
    size_t height,
    size_t *base) {
  size_t end = 0;
  if (!gemstone_size_add(x, width, &end) || end > array->pitch ||
      !gemstone_size_add(y, height, &end) || end > array->height) return 0;
  return gemstone_copy_offset(y, array->pitch, x, base);
}

static int gemstone_linear_bounds(
    size_t x,
    size_t y,
    size_t pitch,
    size_t width,
    size_t height,
    size_t *base) {
  size_t end = 0;
  if (pitch < x || !gemstone_size_add(x, width, &end) || end > pitch) return 0;
  if (!gemstone_copy_offset(y, pitch, x, &end)) return 0;
  if (height > 0) {
    if (!gemstone_size_add(y, height - 1, &end) || !gemstone_size_mul(end, pitch, &end) ||
        !gemstone_size_add(end, x, &end) || !gemstone_size_add(end, width, &end)) return 0;
  }
  return gemstone_copy_offset(y, pitch, x, base);
}

static int32_t gemstone_copy_rows(const GemstoneCudaMemcpy2D *copy) {
  if (copy == NULL || copy->width_in_bytes == 0 || copy->height == 0) return GEMSTONE_STATUS_SUCCESS;
  if ((copy->src_memory_type != GEMSTONE_CUDA_MEMORYTYPE_HOST &&
       copy->src_memory_type != GEMSTONE_CUDA_MEMORYTYPE_DEVICE &&
       copy->src_memory_type != GEMSTONE_CUDA_MEMORYTYPE_ARRAY) ||
      (copy->dst_memory_type != GEMSTONE_CUDA_MEMORYTYPE_HOST &&
       copy->dst_memory_type != GEMSTONE_CUDA_MEMORYTYPE_DEVICE &&
       copy->dst_memory_type != GEMSTONE_CUDA_MEMORYTYPE_ARRAY)) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }

  const GemstoneCudaArray *src_array = (const GemstoneCudaArray *)copy->src_array;
  const GemstoneCudaArray *dst_array = (const GemstoneCudaArray *)copy->dst_array;
  const size_t src_row_pitch = copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY
      ? (src_array == NULL ? 0 : src_array->pitch) : copy->src_pitch;
  const size_t dst_row_pitch = copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY
      ? (dst_array == NULL ? 0 : dst_array->pitch) : copy->dst_pitch;
  size_t src_base = 0;
  size_t dst_base = 0;
  if (copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_HOST ||
      copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_DEVICE) {
    if (!gemstone_linear_bounds(copy->src_x_in_bytes, copy->src_y, copy->src_pitch,
                                copy->width_in_bytes, copy->height, &src_base)) {
      return GEMSTONE_STATUS_INVALID_VALUE;
    }
  } else if (src_array == NULL || !gemstone_array_bounds(src_array, copy->src_x_in_bytes, copy->src_y,
                                                          copy->width_in_bytes, copy->height, &src_base)) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  if (copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_HOST ||
      copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_DEVICE) {
    if (!gemstone_linear_bounds(copy->dst_x_in_bytes, copy->dst_y, copy->dst_pitch,
                                copy->width_in_bytes, copy->height, &dst_base)) {
      return GEMSTONE_STATUS_INVALID_VALUE;
    }
  } else if (dst_array == NULL || !gemstone_array_bounds(dst_array, copy->dst_x_in_bytes, copy->dst_y,
                                                          copy->width_in_bytes, copy->height, &dst_base)) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }

  unsigned char *row = (unsigned char *)malloc(copy->width_in_bytes);
  if (row == NULL) return GEMSTONE_STATUS_UNKNOWN;
  int32_t status = GEMSTONE_STATUS_SUCCESS;
  for (size_t y = 0; y < copy->height && status == GEMSTONE_STATUS_SUCCESS; ++y) {
    size_t src_offset = 0;
    size_t dst_offset = 0;
    if (!gemstone_size_mul(y, src_row_pitch, &src_offset) ||
        !gemstone_size_add(src_offset, src_base, &src_offset) ||
        !gemstone_size_mul(y, dst_row_pitch, &dst_offset) ||
        !gemstone_size_add(dst_offset, dst_base, &dst_offset)) {
      status = GEMSTONE_STATUS_INVALID_VALUE;
      break;
    }
    if (copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_HOST) {
      if (copy->src_host == NULL) { status = GEMSTONE_STATUS_INVALID_VALUE; break; }
      memcpy(row, (const unsigned char *)copy->src_host + src_offset, copy->width_in_bytes);
    } else {
      const uint64_t source = copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY
          ? src_array->device + (uint64_t)src_offset : copy->src_device + (uint64_t)src_offset;
      status = cuMemcpyDtoH_v2(row, source, copy->width_in_bytes);
      if (status != GEMSTONE_STATUS_SUCCESS) break;
    }
    if (copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_HOST) {
      if (copy->dst_host == NULL) { status = GEMSTONE_STATUS_INVALID_VALUE; break; }
      memcpy((unsigned char *)copy->dst_host + dst_offset, row, copy->width_in_bytes);
    } else {
      const uint64_t destination = copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY
          ? dst_array->device + (uint64_t)dst_offset : copy->dst_device + (uint64_t)dst_offset;
      status = cuMemcpyHtoD_v2(destination, row, copy->width_in_bytes);
    }
  }
  free(row);
  return status;
}

int32_t cuMemcpy2D_v2(const void *copy) {
  return gemstone_copy_rows((const GemstoneCudaMemcpy2D *)copy);
}

int32_t cuMemcpy2DUnaligned_v2(const void *copy) {
  return gemstone_copy_rows((const GemstoneCudaMemcpy2D *)copy);
}

int32_t cuMemcpy2DAsync_v2(const void *copy, void *stream) {
  (void)stream;
  return gemstone_copy_rows((const GemstoneCudaMemcpy2D *)copy);
}

static GemstoneCudaMemcpy2D gemstone_expand_copy2d(const GemstoneCudaMemcpy2DV1 *copy) {
  return (GemstoneCudaMemcpy2D){
    copy->src_x_in_bytes, copy->src_y, copy->src_memory_type, copy->src_host,
    copy->src_device, copy->src_array, copy->src_pitch,
    copy->dst_x_in_bytes, copy->dst_y, copy->dst_memory_type, copy->dst_host,
    copy->dst_device, copy->dst_array, copy->dst_pitch,
    copy->width_in_bytes, copy->height
  };
}

int32_t cuMemcpy2D(const void *copy) {
  if (copy == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaMemcpy2D expanded = gemstone_expand_copy2d((const GemstoneCudaMemcpy2DV1 *)copy);
  return gemstone_copy_rows(&expanded);
}

int32_t cuMemcpy2DUnaligned(const void *copy) {
  return cuMemcpy2D(copy);
}

int32_t cuMemcpy2DAsync(const void *copy, void *stream) {
  (void)stream;
  return cuMemcpy2D(copy);
}

static int gemstone_volume_offset(
    size_t z,
    size_t y,
    size_t slice_pitch,
    size_t row_pitch,
    size_t x,
    size_t *offset) {
  return gemstone_size_mul(z, slice_pitch, offset) &&
      gemstone_size_mul(y, row_pitch, &slice_pitch) &&
      gemstone_size_add(*offset, slice_pitch, offset) &&
      gemstone_size_add(*offset, x, offset);
}

static int gemstone_volume_bounds(
    size_t x,
    size_t y,
    size_t z,
    size_t row_pitch,
    size_t slice_pitch,
    size_t width,
    size_t height,
    size_t depth,
    size_t *base) {
  size_t end = 0;
  if (row_pitch < x || !gemstone_size_add(x, width, &end) || end > row_pitch ||
      !gemstone_volume_offset(z, y, slice_pitch, row_pitch, x, &end)) return 0;
  if (depth > 0 && (!gemstone_size_add(z, depth - 1, &end) ||
                    !gemstone_size_mul(end, slice_pitch, &end) ||
                    !gemstone_size_add(end, y, &end) ||
                    !gemstone_size_mul(end, row_pitch, &end) ||
                    !gemstone_size_add(end, x, &end) ||
                    !gemstone_size_add(end, width, &end))) return 0;
  if (height > 0 && (!gemstone_size_add(y, height - 1, &end) ||
                    !gemstone_size_mul(end, row_pitch, &end) ||
                    !gemstone_size_add(end, x, &end) ||
                    !gemstone_size_add(end, width, &end))) return 0;
  return gemstone_volume_offset(z, y, slice_pitch, row_pitch, x, base);
}

static int gemstone_volume_array_bounds(
    const GemstoneCudaArray *array,
    size_t x,
    size_t y,
    size_t z,
    size_t width,
    size_t height,
    size_t depth,
    size_t *base) {
  size_t end = 0;
  size_t slice_pitch = 0;
  return gemstone_size_add(x, width, &end) && end <= array->pitch &&
      gemstone_size_add(y, height, &end) && end <= array->height &&
      gemstone_size_add(z, depth, &end) && end <= array->depth &&
      gemstone_size_mul(array->pitch, array->height, &slice_pitch) &&
      gemstone_volume_offset(z, y, slice_pitch, array->pitch, x, base);
}

static int32_t gemstone_copy_volume(const GemstoneCudaMemcpy3D *copy) {
  if (copy == NULL || copy->width_in_bytes == 0 || copy->height == 0 || copy->depth == 0) {
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (copy->src_reserved != NULL || copy->dst_reserved != NULL ||
      (copy->src_memory_type != GEMSTONE_CUDA_MEMORYTYPE_HOST &&
       copy->src_memory_type != GEMSTONE_CUDA_MEMORYTYPE_DEVICE &&
       copy->src_memory_type != GEMSTONE_CUDA_MEMORYTYPE_ARRAY) ||
      (copy->dst_memory_type != GEMSTONE_CUDA_MEMORYTYPE_HOST &&
       copy->dst_memory_type != GEMSTONE_CUDA_MEMORYTYPE_DEVICE &&
       copy->dst_memory_type != GEMSTONE_CUDA_MEMORYTYPE_ARRAY)) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  const GemstoneCudaArray *src_array = (const GemstoneCudaArray *)copy->src_array;
  const GemstoneCudaArray *dst_array = (const GemstoneCudaArray *)copy->dst_array;
  const size_t src_row_pitch = copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY
      ? (src_array == NULL ? 0 : src_array->pitch) : copy->src_pitch;
  const size_t dst_row_pitch = copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY
      ? (dst_array == NULL ? 0 : dst_array->pitch) : copy->dst_pitch;
  const size_t src_height = copy->src_height == 0 ? copy->height : copy->src_height;
  const size_t dst_height = copy->dst_height == 0 ? copy->height : copy->dst_height;
  size_t src_slice_pitch = 0;
  size_t dst_slice_pitch = 0;
  size_t src_base = 0;
  size_t dst_base = 0;
  if (copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY) {
    if (src_array == NULL || !gemstone_volume_array_bounds(src_array, copy->src_x_in_bytes, copy->src_y,
                                                            copy->src_z, copy->width_in_bytes, copy->height,
                                                            copy->depth, &src_base) ||
        !gemstone_size_mul(src_array->pitch, src_array->height, &src_slice_pitch)) return GEMSTONE_STATUS_INVALID_VALUE;
  } else if (!gemstone_size_mul(src_row_pitch, src_height, &src_slice_pitch) ||
             !gemstone_volume_bounds(copy->src_x_in_bytes, copy->src_y, copy->src_z, src_row_pitch,
                                     src_slice_pitch, copy->width_in_bytes, copy->height, copy->depth,
                                     &src_base)) return GEMSTONE_STATUS_INVALID_VALUE;
  if (copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY) {
    if (dst_array == NULL || !gemstone_volume_array_bounds(dst_array, copy->dst_x_in_bytes, copy->dst_y,
                                                            copy->dst_z, copy->width_in_bytes, copy->height,
                                                            copy->depth, &dst_base) ||
        !gemstone_size_mul(dst_array->pitch, dst_array->height, &dst_slice_pitch)) return GEMSTONE_STATUS_INVALID_VALUE;
  } else if (!gemstone_size_mul(dst_row_pitch, dst_height, &dst_slice_pitch) ||
             !gemstone_volume_bounds(copy->dst_x_in_bytes, copy->dst_y, copy->dst_z, dst_row_pitch,
                                     dst_slice_pitch, copy->width_in_bytes, copy->height, copy->depth,
                                     &dst_base)) return GEMSTONE_STATUS_INVALID_VALUE;

  unsigned char *row = (unsigned char *)malloc(copy->width_in_bytes);
  if (row == NULL) return GEMSTONE_STATUS_UNKNOWN;
  int32_t status = GEMSTONE_STATUS_SUCCESS;
  for (size_t z = 0; z < copy->depth && status == GEMSTONE_STATUS_SUCCESS; ++z) {
    for (size_t y = 0; y < copy->height && status == GEMSTONE_STATUS_SUCCESS; ++y) {
      size_t src_offset = 0;
      size_t dst_offset = 0;
      if (!gemstone_volume_offset(z, y, src_slice_pitch, src_row_pitch, src_base, &src_offset) ||
          !gemstone_volume_offset(z, y, dst_slice_pitch, dst_row_pitch, dst_base, &dst_offset)) {
        status = GEMSTONE_STATUS_INVALID_VALUE;
        break;
      }
      if (copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_HOST) {
        if (copy->src_host == NULL) { status = GEMSTONE_STATUS_INVALID_VALUE; break; }
        memcpy(row, (const unsigned char *)copy->src_host + src_offset, copy->width_in_bytes);
      } else {
        const uint64_t source = copy->src_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY
            ? src_array->device + (uint64_t)src_offset : copy->src_device + (uint64_t)src_offset;
        status = cuMemcpyDtoH_v2(row, source, copy->width_in_bytes);
        if (status != GEMSTONE_STATUS_SUCCESS) break;
      }
      if (copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_HOST) {
        if (copy->dst_host == NULL) { status = GEMSTONE_STATUS_INVALID_VALUE; break; }
        memcpy((unsigned char *)copy->dst_host + dst_offset, row, copy->width_in_bytes);
      } else {
        const uint64_t destination = copy->dst_memory_type == GEMSTONE_CUDA_MEMORYTYPE_ARRAY
            ? dst_array->device + (uint64_t)dst_offset : copy->dst_device + (uint64_t)dst_offset;
        status = cuMemcpyHtoD_v2(destination, row, copy->width_in_bytes);
      }
    }
  }
  free(row);
  return status;
}

int32_t cuMemcpy3D_v2(const void *copy) {
  return gemstone_copy_volume((const GemstoneCudaMemcpy3D *)copy);
}

int32_t cuMemcpy3DAsync_v2(const void *copy, void *stream) {
  (void)stream;
  return gemstone_copy_volume((const GemstoneCudaMemcpy3D *)copy);
}

static GemstoneCudaMemcpy3D gemstone_expand_copy3d(const GemstoneCudaMemcpy3DV1 *copy) {
  return (GemstoneCudaMemcpy3D){
    copy->src_x_in_bytes, copy->src_y, copy->src_z, copy->src_lod, copy->src_memory_type,
    copy->src_host, copy->src_device, copy->src_array, copy->src_reserved, copy->src_pitch, copy->src_height,
    copy->dst_x_in_bytes, copy->dst_y, copy->dst_z, copy->dst_lod, copy->dst_memory_type,
    copy->dst_host, copy->dst_device, copy->dst_array, copy->dst_reserved, copy->dst_pitch, copy->dst_height,
    copy->width_in_bytes, copy->height, copy->depth
  };
}

int32_t cuMemcpy3D(const void *copy) {
  if (copy == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const GemstoneCudaMemcpy3D expanded = gemstone_expand_copy3d((const GemstoneCudaMemcpy3DV1 *)copy);
  return gemstone_copy_volume(&expanded);
}

int32_t cuMemcpy3DAsync(const void *copy, void *stream) {
  (void)stream;
  return cuMemcpy3D(copy);
}

int32_t cuKernelGetFunction(void **function, void *kernel) {
  if (function == NULL || kernel == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *function = kernel;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuLibraryGetGlobal(uint64_t *pointer, size_t *bytes, void *library, const char *name) {
  return cuModuleGetGlobal_v2(pointer, bytes, library, name);
}

int32_t cuModuleGetGlobal(uint32_t *pointer, uint32_t *bytes, void *module, const char *name) {
  if (pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  uint64_t address = 0;
  size_t size = 0;
  const int32_t status = cuModuleGetGlobal_v2(&address, &size, module, name);
  if (status != GEMSTONE_STATUS_SUCCESS) return status;
  if (address > UINT32_MAX || size > UINT32_MAX) return GEMSTONE_STATUS_NOT_SUPPORTED;
  *pointer = (uint32_t)address;
  if (bytes != NULL) *bytes = (uint32_t)size;
  return GEMSTONE_STATUS_SUCCESS;
}

enum {
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_MAX_THREADS_PER_BLOCK = 0,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES = 1,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_CONST_SIZE_BYTES = 2,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_LOCAL_SIZE_BYTES = 3,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_NUM_REGS = 4,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_PTX_VERSION = 5,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_BINARY_VERSION = 6,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_CACHE_MODE_CA = 7,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_MAX_DYNAMIC_SHARED_SIZE_BYTES = 8,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_PREFERRED_SHARED_MEMORY_CARVEOUT = 9,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_CLUSTER_SIZE_MUST_BE_SET = 10,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH = 11,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_HEIGHT = 12,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_DEPTH = 13,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_NON_PORTABLE_CLUSTER_SIZE_ALLOWED = 14,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_CLUSTER_SCHEDULING_POLICY_PREFERENCE = 15,
  GEMSTONE_CUDA_FUNC_ATTRIBUTE_DEVICE_NODE_UPDATE_SUPPORTED = 16
};

int32_t cuFuncGetAttribute(int32_t *value, int32_t attribute, void *function) {
  if (value == NULL || function == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  switch (attribute) {
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_MAX_THREADS_PER_BLOCK:
      *value = 1024;
      return GEMSTONE_STATUS_SUCCESS;
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_PTX_VERSION:
      *value = gemstone_registered_ptx_version(function);
      if (*value == 0) *value = 75;
      return GEMSTONE_STATUS_SUCCESS;
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_BINARY_VERSION:
      *value = gemstone_registered_ptx_version(function);
      if (*value == 0) *value = 75;
      return GEMSTONE_STATUS_SUCCESS;
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_MAX_DYNAMIC_SHARED_SIZE_BYTES:
      *value = 49152;
      return GEMSTONE_STATUS_SUCCESS;
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_CONST_SIZE_BYTES:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_LOCAL_SIZE_BYTES:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_NUM_REGS:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_CACHE_MODE_CA:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_PREFERRED_SHARED_MEMORY_CARVEOUT:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_CLUSTER_SIZE_MUST_BE_SET:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_HEIGHT:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_DEPTH:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_NON_PORTABLE_CLUSTER_SIZE_ALLOWED:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_CLUSTER_SCHEDULING_POLICY_PREFERENCE:
    case GEMSTONE_CUDA_FUNC_ATTRIBUTE_DEVICE_NODE_UPDATE_SUPPORTED:
      *value = 0;
      return GEMSTONE_STATUS_SUCCESS;
    default:
      return GEMSTONE_STATUS_INVALID_VALUE;
  }
}

int32_t cuKernelGetAttribute(int32_t *value, int32_t attribute, void *kernel, int32_t device) {
  (void)device;
  return cuFuncGetAttribute(value, attribute, kernel);
}

/*
 * The CUDA runtime uses this driver entry while CUB is selecting a reduction
 * plan.  There is no useful device occupancy to query for the JVM executor,
 * but a conservative synthetic value keeps that host-side planning path
 * usable without putting planner policy into the generated RPC client.
 */
static int32_t gemstone_occupancy_max_blocks(int32_t *blocks, int32_t block_size) {
  if (blocks == NULL || block_size <= 0 || block_size > 1024) return GEMSTONE_STATUS_INVALID_VALUE;
  *blocks = 1024 / block_size;
  if (*blocks == 0) *blocks = 1;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuOccupancyMaxActiveBlocksPerMultiprocessor(
    int32_t *blocks, void *function, int32_t block_size, size_t dynamic_shared_memory) {
  (void)function;
  (void)dynamic_shared_memory;
  return gemstone_occupancy_max_blocks(blocks, block_size);
}

int32_t cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(
    int32_t *blocks, void *function, int32_t block_size, size_t dynamic_shared_memory, uint32_t flags) {
  (void)function;
  (void)dynamic_shared_memory;
  (void)flags;
  return gemstone_occupancy_max_blocks(blocks, block_size);
}

#if defined(_WIN32)
#define GEMSTONE_CUDA_API __stdcall
#else
#define GEMSTONE_CUDA_API
#endif

extern int32_t cuInit(uint32_t flags);
extern int32_t cuDriverGetVersion(int32_t *version);
extern int32_t cuDeviceGetCount(int32_t *count);
extern int32_t cuDeviceGet(int32_t *device, int32_t ordinal);
extern int32_t cuCtxCreate_v2(void **context, uint32_t flags, int32_t device);
extern int32_t cuCtxDestroy_v2(void *context);
extern int32_t cuCtxSynchronize(void);
extern int32_t cuLaunchHostFunc(void *stream, void *callback, void *user_data);
extern int32_t cuStreamAddCallback(void *stream, void *callback, void *user_data, uint32_t flags);
extern int32_t cuMemcpyDtoD_v2(uint64_t destination, uint64_t source, size_t size);
extern int32_t cuMemsetD8_v2(uint64_t destination, uint8_t value, size_t count);
extern int32_t cuModuleLoadData(void **module, const void *image);
extern int32_t cuModuleGetFunction(void **function, void *module, const char *name);
extern int32_t cuModuleUnload(void *module);
extern int32_t gemstone_cuCtxCreate_v4(void **context, void *parameters, uint32_t flags, int32_t device);
extern int32_t cuLaunchKernel(
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
    void **extra);

/* The CUDA driver exposes the per-thread-default-stream spelling through
 * cuGetProcAddress even though it has the same ABI as cuLaunchKernel.  The
 * static CUDA runtime uses that spelling for host-launched kernels. */
int32_t cuLaunchKernel_ptsz(
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
  return cuLaunchKernel(
      function,
      grid_x,
      grid_y,
      grid_z,
      block_x,
      block_y,
      block_z,
      shared_memory_bytes,
      stream,
      kernel_params,
      extra);
}

typedef struct GemstoneLinkState {
  char *image;
  size_t size;
} GemstoneLinkState;

static int32_t gemstone_load_module_image(void **module, const void *image) {
  if (module == NULL || image == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  const char *ptx = gemstone_cuda_find_uncompressed_ptx(image);
  return cuModuleLoadData(module, ptx == NULL ? image : ptx);
}

int32_t cuModuleLoadDataEx(
    void **module,
    const void *image,
    uint32_t option_count,
    int32_t *options,
    void **option_values) {
  (void)option_count;
  (void)options;
  (void)option_values;
  return gemstone_load_module_image(module, image);
}

int32_t cuModuleLoadFatBinary(void **module, const void *fat_binary) {
  return gemstone_load_module_image(module, fat_binary);
}

static int gemstone_link_append(GemstoneLinkState *state, const char *source, size_t source_size) {
  const size_t separator = state->size == 0 ? 0 : 1;
  if (state->size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 1 - separator ||
      source_size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE - 1 - separator - state->size) return 0;
  const size_t next_size = state->size + separator + source_size;
  char *next = (char *)realloc(state->image, next_size + 1);
  if (next == NULL) return 0;
  state->image = next;
  if (separator != 0) state->image[state->size] = '\n';
  memcpy(state->image + state->size + separator, source, source_size);
  state->image[next_size] = '\0';
  state->size = next_size;
  return 1;
}

int32_t cuLinkCreate_v2(
    uint32_t option_count,
    int32_t *options,
    void **option_values,
    void **state_out) {
  (void)option_count;
  (void)options;
  (void)option_values;
  if (state_out == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneLinkState *state = (GemstoneLinkState *)calloc(1, sizeof(*state));
  if (state == NULL) return GEMSTONE_STATUS_UNKNOWN;
  *state_out = state;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuLinkAddData_v2(
    void *state_pointer,
    int32_t input_type,
    void *data,
    size_t size,
    const char *name,
    uint32_t option_count,
    int32_t *options,
    void **option_values) {
  (void)input_type;
  (void)name;
  (void)option_count;
  (void)options;
  (void)option_values;
  if (state_pointer == NULL || data == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneLinkState *state = (GemstoneLinkState *)state_pointer;
  const char *ptx = gemstone_cuda_find_uncompressed_ptx(data);
  if (ptx != NULL) {
    if (!gemstone_link_append(state, ptx, strlen(ptx))) return GEMSTONE_STATUS_UNKNOWN;
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (size == 0 || size > GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE) return GEMSTONE_STATUS_NOT_SUPPORTED;
  if (!gemstone_link_append(state, (const char *)data, size)) return GEMSTONE_STATUS_UNKNOWN;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuLinkComplete(void *state_pointer, void **image, size_t *size) {
  if (state_pointer == NULL || image == NULL || size == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneLinkState *state = (GemstoneLinkState *)state_pointer;
  if (state->size == 0) return GEMSTONE_STATUS_INVALID_VALUE;
  *image = state->image;
  *size = state->size;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuLinkDestroy(void *state_pointer) {
  if (state_pointer == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneLinkState *state = (GemstoneLinkState *)state_pointer;
  free(state->image);
  free(state);
  return GEMSTONE_STATUS_SUCCESS;
}

extern _Thread_local void *gemstone_current_context;

static int32_t gemstone_cuLibraryLoadData(
    void **library,
    const void *code,
    int32_t *jit_options,
    void **jit_option_values,
    uint32_t num_jit_options,
    int32_t *library_options,
    void **library_option_values,
    uint32_t num_library_options) {
  (void)jit_options;
  (void)jit_option_values;
  (void)num_jit_options;
  (void)library_options;
  (void)library_option_values;
  (void)num_library_options;
  const char *ptx = gemstone_cuda_find_uncompressed_ptx(code);
  return cuModuleLoadData(library, ptx == NULL ? code : ptx);
}

static int32_t gemstone_cuLibraryGetKernel(void **kernel, void *library, const char *name) {
  return cuModuleGetFunction(kernel, library, name);
}

static int32_t gemstone_cuLibraryGetModule(void **module, void *library) {
  if (module == NULL || library == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *module = library;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuLibraryUnload(void *library) {
  return cuModuleUnload(library);
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
  const int32_t mirrored_status = gemstone_cuda_managed_free((uint64_t)(uintptr_t)pointer, &found);
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
    case 9: *value = 65536; break;
    case 10: *value = 32; break;
    case 11: *value = 2147483647; break;
    case 12: *value = 65536; break;
    case 13: *value = 1500000; break;
    case 14: *value = 256; break;
    case 15: *value = 1; break;
    case 16: *value = 1; break;
    case 17: *value = 0; break;
    case 18: *value = 0; break;
    case 19: *value = 1; break;
    case 20: *value = 0; break;
    case 21: *value = 131072; break;
    case 22: *value = 131072; break;
    case 23: *value = 65536; break;
    case 24: *value = 16384; break;
    case 25: *value = 16384; break;
    case 26: *value = 16384; break;
    case 27: *value = 32768; break;
    case 28: *value = 32768; break;
    case 29: *value = 2048; break;
    case 30: *value = 256; break;
    case 31: *value = 1; break;
    case 32: *value = 0; break;
    case 33: *value = 1; break;
    case 34: *value = 1; break;
    case 35: *value = 0; break;
    case 36: *value = 1500000; break;
    case 37: *value = 128; break;
    case 38: *value = 4194304; break;
    case 39: *value = 1536; break;
    case 40: *value = 2; break;
    case 41: *value = 1; break;
    case 42: *value = 32768; break;
    case 43: *value = 2048; break;
    case 45: *value = 32768; break;
    case 46: *value = 32768; break;
    case 47: *value = 16384; break;
    case 48: *value = 16384; break;
    case 49: *value = 16384; break;
    case 50: *value = 0; break;
    case 51: *value = 32; break;
    case 52: *value = 32768; break;
    case 53: *value = 32768; break;
    case 54: *value = 2048; break;
    case 55: *value = 16384; break;
    case 56: *value = 16384; break;
    case 57: *value = 16384; break;
    case 58: *value = 16384; break;
    case 59: *value = 16384; break;
    case 60: *value = 16384; break;
    case 61: *value = 16384; break;
    case 62: *value = 2048; break;
    case 63: *value = 16384; break;
    case 64: *value = 16384; break;
    case 65: *value = 2048; break;
    case 66: *value = 32768; break;
    case 67: *value = 32768; break;
    case 68: *value = 2048; break;
    case 69: *value = 134217728; break;
    case 70: *value = 131072; break;
    case 71: *value = 65536; break;
    case 72: *value = 2147483647; break;
    case 73: *value = 32768; break;
    case 74: *value = 32768; break;
    case 75: *value = 7; break;
    case 76: *value = 5; break;
    case 77: *value = 32768; break;
    case 78: *value = 1; break;
    case 79: *value = 1; break;
    case 80: *value = 1; break;
    case 81: *value = 49152; break;
    case 82: *value = 65536; break;
    case 83: *value = 1; break;
    case 84: *value = 0; break;
    case 85: *value = 0; break;
    case 86: *value = 0; break;
    case 87: *value = 32; break;
    case 88: *value = 0; break;
    case 89: *value = 1; break;
    case 90: *value = 1; break;
    case 91: *value = 1; break;
    case 95: *value = 1; break;
    case 96: *value = 0; break;
    case 97: *value = 98304; break;
    case 100: *value = 0; break;
    case 101: *value = 0; break;
    case 108: *value = 4194304; break;
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
  value->ptx_version = gemstone_registered_ptx_version(function);
  value->binary_version = value->ptx_version;
  if (value->ptx_version == 0) {
    value->ptx_version = 75;
    value->binary_version = 75;
  }
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
  return gemstone_cuda_managed_allocate((uint64_t *)pointer, size);
}

int32_t cudaFreeHost(void *pointer) {
  int found = 0;
  const int32_t status = gemstone_cuda_managed_free((uint64_t)(uintptr_t)pointer, &found);
  return found ? status : GEMSTONE_STATUS_INVALID_VALUE;
}

int32_t cudaMallocManaged(void **pointer, size_t size, uint32_t flags) {
  (void)flags;
  return gemstone_cuda_managed_allocate((uint64_t *)pointer, size);
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

typedef struct GemstoneFatBinary {
  void *token;
  void *module;
  int32_t ptx_version;
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

static int32_t gemstone_registered_ptx_version(const void *function) {
  int32_t version = gemstone_configured_ptx_version();
  pthread_mutex_lock(&gemstone_registration_mutex);
  if (version == 0) {
    for (GemstoneRegisteredFunction *entry = gemstone_registered_functions; entry != NULL; entry = entry->next) {
      if (entry->host_function == function || entry->device_function == function) {
        version = entry->fat_binary->ptx_version;
        break;
      }
    }
    if (version == 0 && gemstone_fat_binaries != NULL) version = gemstone_fat_binaries->ptx_version;
  }
  pthread_mutex_unlock(&gemstone_registration_mutex);
  return version;
}

void **GEMSTONE_CUDA_API gemstone_cudaRegisterFatBinary(void *fat_binary) {
  GemstoneFatBinary *entry = (GemstoneFatBinary *)calloc(1, sizeof(*entry));
  if (entry == NULL) return NULL;
  entry->token = entry;
  const char *ptx = gemstone_cuda_find_uncompressed_ptx(fat_binary);
  entry->ptx_version = gemstone_ptx_target_version(ptx);
  if (ptx != NULL && cuModuleLoadData(&entry->module, ptx) != GEMSTONE_STATUS_SUCCESS) entry->module = NULL;
  pthread_mutex_lock(&gemstone_registration_mutex);
  entry->next = gemstone_fat_binaries;
  gemstone_fat_binaries = entry;
  pthread_mutex_unlock(&gemstone_registration_mutex);
  return &entry->token;
}

void GEMSTONE_CUDA_API gemstone_cudaRegisterFatBinaryEnd(void **handle) {
  (void)handle;
}

void GEMSTONE_CUDA_API gemstone_cudaRegisterFunction(
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

void GEMSTONE_CUDA_API gemstone_cudaRegisterVar(
    void **handle,
    char *host_variable,
    char *device_address,
    const char *device_name,
    int32_t external,
    size_t size,
    int32_t constant,
    int32_t global) {
  (void)device_address;
  (void)external;
  (void)constant;
  (void)global;
  if (handle == NULL || *handle == NULL || device_name == NULL || size == 0) return;
  GemstoneFatBinary *fat = (GemstoneFatBinary *)*handle;
  if (fat->module == NULL) return;
  const void *initial = host_variable;
  unsigned char *zeros = NULL;
  if (initial == NULL) {
    zeros = (unsigned char *)calloc(1, size);
    if (zeros == NULL) return;
    initial = zeros;
  }
  (void)gemstone_register_global_remote(
      (uint64_t)(uintptr_t)fat->module,
      device_name,
      initial,
      size);
  free(zeros);
}

void GEMSTONE_CUDA_API gemstone_cudaUnregisterFatBinary(void **handle) {
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

int32_t GEMSTONE_CUDA_API gemstone_cudaGetKernel(void **kernel, const void *host_function) {
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

unsigned GEMSTONE_CUDA_API gemstone_cudaPushCallConfiguration(
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

int32_t GEMSTONE_CUDA_API gemstone_cudaPopCallConfiguration(
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

int32_t GEMSTONE_CUDA_API gemstone_cudaLaunchKernel(
    void *kernel,
    GemstoneDim3 grid,
    GemstoneDim3 block,
    void **arguments,
    size_t shared_memory,
    void *stream) {
  const int32_t status = cuLaunchKernel(
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
  gemstone_last_error = status;
  return status;
}

static int32_t gemstone_cuDeviceGetAttribute(int32_t *value, int32_t attribute, int32_t device) {
  return cudaDeviceGetAttribute(value, attribute, device);
}

static int32_t gemstone_cuDeviceGetName(char *name, int32_t length, int32_t device) {
  static const char device_name[] = "Gemstone PTX emulator";
  if (name == NULL || length <= 0 || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  const size_t source_size = sizeof(device_name) - 1;
  const size_t copy_size = source_size < (size_t)length - 1 ? source_size : (size_t)length - 1;
  memcpy(name, device_name, copy_size);
  name[copy_size] = '\0';
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDeviceTotalMem_v2(uint64_t *bytes, int32_t device) {
  if (bytes == NULL || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  *bytes = UINT64_C(8) * 1024 * 1024 * 1024;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDeviceComputeCapability(int32_t *major, int32_t *minor, int32_t device) {
  if (major == NULL || minor == NULL || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  *major = 7;
  *minor = 5;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxGetCurrent(void **context) {
  if (context == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *context = gemstone_current_context;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxGetDevice(int32_t *device) {
  if (device == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *device = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxGetDevice_v2(int32_t *device, void *context) {
  (void)context;
  return gemstone_cuCtxGetDevice(device);
}

static int32_t gemstone_cuCtxSetCurrent(void *context) {
  gemstone_current_context = context;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDevicePrimaryCtxRetain(void **context, int32_t device) {
  void *previous_context = gemstone_current_context;
  const int32_t status = cuCtxCreate_v2(context, 0, device);
  gemstone_current_context = previous_context;
  return status;
}

static int32_t gemstone_cuDevicePrimaryCtxRelease_v2(int32_t device) {
  return device == 0 ? GEMSTONE_STATUS_SUCCESS : GEMSTONE_STATUS_INVALID_VALUE;
}

static int32_t gemstone_cuDevicePrimaryCtxGetState(int32_t device, uint32_t *flags, int32_t *active) {
  if (device != 0 || flags == NULL || active == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *flags = 0;
  *active = gemstone_current_context == NULL ? 0 : 1;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDevicePrimaryCtxSetFlags_v2(int32_t device, uint32_t flags) {
  (void)flags;
  return device == 0 ? GEMSTONE_STATUS_SUCCESS : GEMSTONE_STATUS_INVALID_VALUE;
}

static int32_t gemstone_cuDeviceGetByPCIBusId(int32_t *device, const char *bus_id) {
  if (device == NULL || bus_id == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  if (strcmp(bus_id, "0000:01:00.0") != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  *device = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDeviceGetTexture1DLinearMaxWidth(
    size_t *width,
    int32_t format,
    uint32_t channels,
    int32_t device) {
  (void)format;
  (void)channels;
  if (width == NULL || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  *width = 134217728;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDeviceGetP2PAttribute(
    int32_t *value,
    int32_t attribute,
    int32_t source,
    int32_t destination) {
  if (value == NULL || source != 0 || destination != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  *value = attribute == 1 ? 1 : 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDeviceCanAccessPeer(int32_t *value, int32_t source, int32_t destination) {
  if (value == NULL || source != 0 || destination != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  *value = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxGetCacheConfig(int32_t *config) {
  if (config == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *config = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxSetCacheConfig(int32_t config) {
  (void)config;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxGetSharedMemConfig(int32_t *config) {
  if (config == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *config = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxSetSharedMemConfig(int32_t config) {
  (void)config;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxGetStreamPriorityRange(int32_t *least, int32_t *greatest) {
  if (least == NULL || greatest == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *least = 0;
  *greatest = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxResetPersistingL2Cache(void) {
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDevicePrimaryCtxReset_v2(int32_t device) {
  return device == 0 ? GEMSTONE_STATUS_SUCCESS : GEMSTONE_STATUS_INVALID_VALUE;
}

static int32_t gemstone_cuDeviceGetUuid(void *uuid, int32_t device);
static int32_t gemstone_cuDeviceGetPCIBusId(char *bus_id, int32_t length, int32_t device);
static int32_t gemstone_cuCtxGetFlags(uint32_t *flags);
static int32_t gemstone_cuCtxGetApiVersion(void *context, uint32_t *version);
static int32_t gemstone_cuCtxGetLimit(size_t *value, int32_t limit);
static int32_t gemstone_cuCtxSetLimit(int32_t limit, size_t value);
static int32_t gemstone_cuCtxDetach(void *context);
static int32_t gemstone_cuCtxSynchronize_v2(void *context);
static int32_t gemstone_cuDeviceGetDefaultMemPool(void **pool, int32_t device);
static int32_t gemstone_cuDeviceGetMemPool(void **pool, int32_t device);
static int32_t gemstone_cuDeviceSetMemPool(int32_t device, void *pool);
static int32_t gemstone_cuMemGetDefaultMemPool(void **pool, void *location, int32_t allocation_type);
static int32_t gemstone_cuMemGetMemPool(void **pool, void *location, int32_t allocation_type);
static int32_t gemstone_cuMemSetMemPool(void *location, int32_t allocation_type, void *pool);
static int32_t gemstone_cuMemGetInfo_v2(size_t *free_bytes, size_t *total_bytes);
int32_t gemstone_cuGetExportTable(const void **table, const void *table_id);
int32_t gemstone_cuGetProcAddress_v2(
    const char *symbol,
    void **pfn,
    int32_t cuda_version,
    uint64_t flags,
    int32_t *symbol_status);

/* Native CUDA libraries discover these Driver entry points with dlsym. Keep
 * the generated ABI-presence stubs weak, but provide the same emulated
 * implementations through the ordinary symbol path as well as cuGetProcAddress. */
int32_t cuDeviceGetAttribute(int32_t *value, int32_t attribute, int32_t device) {
  return gemstone_cuDeviceGetAttribute(value, attribute, device);
}

int32_t cuDeviceGetName(char *name, int32_t length, int32_t device) {
  return gemstone_cuDeviceGetName(name, length, device);
}

int32_t cuDeviceTotalMem_v2(uint64_t *bytes, int32_t device) {
  return gemstone_cuDeviceTotalMem_v2(bytes, device);
}

int32_t cuDeviceComputeCapability(int32_t *major, int32_t *minor, int32_t device) {
  return gemstone_cuDeviceComputeCapability(major, minor, device);
}

int32_t cuDeviceTotalMem(uint64_t *bytes, int32_t device) {
  return gemstone_cuDeviceTotalMem_v2(bytes, device);
}

int32_t cuDeviceGetByPCIBusId(int32_t *device, const char *bus_id) {
  return gemstone_cuDeviceGetByPCIBusId(device, bus_id);
}

int32_t cuDeviceGetTexture1DLinearMaxWidth(size_t *width, int32_t format, uint32_t channels, int32_t device) {
  return gemstone_cuDeviceGetTexture1DLinearMaxWidth(width, format, channels, device);
}

int32_t cuDeviceGetP2PAttribute(int32_t *value, int32_t attribute, int32_t source, int32_t destination) {
  return gemstone_cuDeviceGetP2PAttribute(value, attribute, source, destination);
}

int32_t cuDeviceCanAccessPeer(int32_t *value, int32_t source, int32_t destination) {
  return gemstone_cuDeviceCanAccessPeer(value, source, destination);
}

typedef struct GemstoneLegacyDeviceProperties {
  int32_t max_threads_per_block;
  int32_t max_threads_dim[3];
  int32_t max_grid_size[3];
  int32_t shared_memory_per_block;
  int32_t total_constant_memory;
  int32_t simd_width;
  int32_t memory_pitch;
  int32_t registers_per_block;
  int32_t clock_rate;
  int32_t texture_alignment;
} GemstoneLegacyDeviceProperties;

int32_t cuDeviceGetProperties(void *properties, int32_t device) {
  if (properties == NULL || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  GemstoneLegacyDeviceProperties *value = (GemstoneLegacyDeviceProperties *)properties;
  value->max_threads_per_block = 1024;
  value->max_threads_dim[0] = 1024;
  value->max_threads_dim[1] = 1024;
  value->max_threads_dim[2] = 64;
  value->max_grid_size[0] = INT32_MAX;
  value->max_grid_size[1] = 65535;
  value->max_grid_size[2] = 65535;
  value->shared_memory_per_block = 49152;
  value->total_constant_memory = 65536;
  value->simd_width = 32;
  value->memory_pitch = INT32_MAX;
  value->registers_per_block = 65536;
  value->clock_rate = 1500000;
  value->texture_alignment = 256;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuDeviceGetUuid_v2(void *uuid, int32_t device) {
  return gemstone_cuDeviceGetUuid(uuid, device);
}

int32_t cuDeviceGetUuid(void *uuid, int32_t device) {
  return gemstone_cuDeviceGetUuid(uuid, device);
}

int32_t cuDeviceGetPCIBusId(char *bus_id, int32_t length, int32_t device) {
  return gemstone_cuDeviceGetPCIBusId(bus_id, length, device);
}

int32_t cuCtxGetCurrent(void **context) {
  return gemstone_cuCtxGetCurrent(context);
}

int32_t cuCtxGetDevice(int32_t *device) {
  return gemstone_cuCtxGetDevice(device);
}

int32_t cuCtxGetDevice_v2(int32_t *device, void *context) {
  return gemstone_cuCtxGetDevice_v2(device, context);
}

int32_t cuCtxSetCurrent(void *context) {
  return gemstone_cuCtxSetCurrent(context);
}

int32_t cuCtxCreate(void **context, uint32_t flags, int32_t device) {
  return cuCtxCreate_v2(context, flags, device);
}

int32_t cuDevicePrimaryCtxRetain(void **context, int32_t device) {
  return gemstone_cuDevicePrimaryCtxRetain(context, device);
}

int32_t cuDevicePrimaryCtxRelease_v2(int32_t device) {
  return gemstone_cuDevicePrimaryCtxRelease_v2(device);
}

int32_t cuDevicePrimaryCtxGetState(int32_t device, uint32_t *flags, int32_t *active) {
  return gemstone_cuDevicePrimaryCtxGetState(device, flags, active);
}

int32_t cuDevicePrimaryCtxSetFlags_v2(int32_t device, uint32_t flags) {
  return gemstone_cuDevicePrimaryCtxSetFlags_v2(device, flags);
}

int32_t cuDevicePrimaryCtxRelease(int32_t device) {
  return gemstone_cuDevicePrimaryCtxRelease_v2(device);
}

int32_t cuDevicePrimaryCtxReset_v2(int32_t device) {
  return gemstone_cuDevicePrimaryCtxReset_v2(device);
}

int32_t cuDevicePrimaryCtxReset(int32_t device) {
  return gemstone_cuDevicePrimaryCtxReset_v2(device);
}

int32_t cuDevicePrimaryCtxSetFlags(int32_t device, uint32_t flags) {
  return gemstone_cuDevicePrimaryCtxSetFlags_v2(device, flags);
}

int32_t cuCtxGetCacheConfig(int32_t *config) {
  return gemstone_cuCtxGetCacheConfig(config);
}

int32_t cuCtxSetCacheConfig(int32_t config) {
  return gemstone_cuCtxSetCacheConfig(config);
}

int32_t cuCtxGetSharedMemConfig(int32_t *config) {
  return gemstone_cuCtxGetSharedMemConfig(config);
}

int32_t cuCtxSetSharedMemConfig(int32_t config) {
  return gemstone_cuCtxSetSharedMemConfig(config);
}

int32_t cuCtxGetStreamPriorityRange(int32_t *least, int32_t *greatest) {
  return gemstone_cuCtxGetStreamPriorityRange(least, greatest);
}

int32_t cuCtxResetPersistingL2Cache(void) {
  return gemstone_cuCtxResetPersistingL2Cache();
}

int32_t cuCtxGetFlags(uint32_t *flags) {
  return gemstone_cuCtxGetFlags(flags);
}

int32_t cuCtxGetApiVersion(void *context, uint32_t *version) {
  return gemstone_cuCtxGetApiVersion(context, version);
}

int32_t cuCtxGetLimit(size_t *value, int32_t limit) {
  return gemstone_cuCtxGetLimit(value, limit);
}

int32_t cuCtxSetLimit(int32_t limit, size_t value) {
  return gemstone_cuCtxSetLimit(limit, value);
}

int32_t cuCtxDetach(void *context) {
  return gemstone_cuCtxDetach(context);
}

int32_t cuCtxSynchronize_v2(void *context) {
  return gemstone_cuCtxSynchronize_v2(context);
}

int32_t cuDeviceGetDefaultMemPool(void **pool, int32_t device) {
  return gemstone_cuDeviceGetDefaultMemPool(pool, device);
}

int32_t cuDeviceGetMemPool(void **pool, int32_t device) {
  return gemstone_cuDeviceGetMemPool(pool, device);
}

int32_t cuDeviceSetMemPool(int32_t device, void *pool) {
  return gemstone_cuDeviceSetMemPool(device, pool);
}

int32_t cuMemGetDefaultMemPool(void **pool, void *location, int32_t allocation_type) {
  return gemstone_cuMemGetDefaultMemPool(pool, location, allocation_type);
}

int32_t cuMemGetMemPool(void **pool, void *location, int32_t allocation_type) {
  return gemstone_cuMemGetMemPool(pool, location, allocation_type);
}

int32_t cuMemSetMemPool(void *location, int32_t allocation_type, void *pool) {
  return gemstone_cuMemSetMemPool(location, allocation_type, pool);
}

int32_t cuMemGetInfo_v2(size_t *free_bytes, size_t *total_bytes) {
  return gemstone_cuMemGetInfo_v2(free_bytes, total_bytes);
}

int32_t cuFlushGPUDirectRDMAWrites(int32_t target, int32_t scope) {
  (void)target;
  (void)scope;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t GEMSTONE_CUDA_API cuGetProcAddress(
    const char *symbol,
    void **pfn,
    int32_t cuda_version,
    uint64_t flags) {
  return gemstone_cuGetProcAddress_v2(symbol, pfn, cuda_version, flags, NULL);
}

static int32_t gemstone_cuDeviceGetDefaultMemPool(void **pool, int32_t device) {
  if (pool == NULL || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  *pool = (void *)(uintptr_t)UINT64_C(0x300000000);
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDeviceGetMemPool(void **pool, int32_t device) {
  return gemstone_cuDeviceGetDefaultMemPool(pool, device);
}

static int32_t gemstone_cuDeviceSetMemPool(int32_t device, void *pool) {
  (void)pool;
  return device == 0 ? GEMSTONE_STATUS_SUCCESS : GEMSTONE_STATUS_INVALID_VALUE;
}

static int32_t gemstone_cuMemGetDefaultMemPool(void **pool, void *location, int32_t allocation_type) {
  (void)location;
  (void)allocation_type;
  if (pool == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *pool = (void *)(uintptr_t)UINT64_C(0x300000000);
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuMemGetMemPool(void **pool, void *location, int32_t allocation_type) {
  return gemstone_cuMemGetDefaultMemPool(pool, location, allocation_type);
}

static int32_t gemstone_cuMemSetMemPool(void *location, int32_t allocation_type, void *pool) {
  (void)location;
  (void)allocation_type;
  (void)pool;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuMemAllocAsync(uint64_t *pointer, size_t size, void *stream) {
  (void)stream;
  return cuMemAlloc_v2(pointer, size);
}

static int32_t gemstone_cuMemAllocFromPoolAsync(uint64_t *pointer, size_t size, void *pool, void *stream) {
  (void)pool;
  (void)stream;
  return cuMemAlloc_v2(pointer, size);
}

static int32_t gemstone_cuMemFreeAsync(uint64_t pointer, void *stream) {
  (void)stream;
  return cuMemFree_v2(pointer);
}

static int32_t gemstone_cuMemAllocPitch_v2(
    uint64_t *pointer,
    size_t *pitch,
    size_t width,
    size_t height,
    uint32_t element_size) {
  (void)element_size;
  if (pointer == NULL || pitch == NULL || width == 0 || height == 0 || height > SIZE_MAX / width) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  *pitch = width;
  return cuMemAlloc_v2(pointer, width * height);
}

static int32_t gemstone_cuMemGetInfo_v2(size_t *free_bytes, size_t *total_bytes) {
  if (free_bytes == NULL || total_bytes == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *total_bytes = (size_t)UINT64_C(8) * 1024 * 1024 * 1024;
  *free_bytes = *total_bytes;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuMemAllocManaged(uint64_t *pointer, size_t size, uint32_t flags) {
  (void)flags;
  return gemstone_cuda_managed_allocate(pointer, size);
}

static int32_t gemstone_cuCtxGetFlags(uint32_t *flags) {
  if (flags == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *flags = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxGetApiVersion(void *context, uint32_t *version) {
  (void)context;
  if (version == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *version = 13030;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxGetLimit(size_t *value, int32_t limit) {
  (void)limit;
  if (value == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *value = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxSetLimit(int32_t limit, size_t value) {
  (void)limit;
  (void)value;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuCtxDetach(void *context) {
  return cuCtxDestroy_v2(context);
}

static int32_t gemstone_cuCtxSynchronize_v2(void *context) {
  (void)context;
  return cuCtxSynchronize();
}
static int32_t gemstone_cuDeviceGetUuid(void *uuid, int32_t device) {
  if (uuid == NULL || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  static const uint8_t virtual_uuid[16] = {
    0x47, 0x45, 0x4d, 0x53, 0x54, 0x4f, 0x4e, 0x45,
    0x2d, 0x43, 0x55, 0x44, 0x41, 0x2d, 0x30, 0x31
  };
  memcpy(uuid, virtual_uuid, sizeof(virtual_uuid));
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDeviceGetPCIBusId(char *bus_id, int32_t length, int32_t device) {
  static const char value[] = "0000:01:00.0";
  if (bus_id == NULL || length <= 0 || device != 0) return GEMSTONE_STATUS_INVALID_VALUE;
  const size_t source_size = sizeof(value) - 1;
  const size_t copy_size = source_size < (size_t)length - 1 ? source_size : (size_t)length - 1;
  memcpy(bus_id, value, copy_size);
  bus_id[copy_size] = '\0';
  return GEMSTONE_STATUS_SUCCESS;
}

static const char *gemstone_cuGetErrorString(int32_t status) {
  return cudaGetErrorString(status);
}

static const char *gemstone_cuGetErrorName(int32_t status) {
  return cudaGetErrorName(status);
}

static int32_t gemstone_cuMemcpyHtoDAsync_v2(uint64_t destination, const void *source, size_t size, void *stream) {
  (void)stream;
  return cuMemcpyHtoD_v2(destination, source, size);
}

static int32_t gemstone_cuMemcpyDtoHAsync_v2(void *destination, uint64_t source, size_t size, void *stream) {
  (void)stream;
  return cuMemcpyDtoH_v2(destination, source, size);
}

static int32_t gemstone_cuMemcpyDtoDAsync_v2(uint64_t destination, uint64_t source, size_t size, void *stream) {
  (void)stream;
  return cuMemcpyDtoD_v2(destination, source, size);
}

static int32_t gemstone_cuMemsetD8Async(uint64_t destination, uint8_t value, size_t count, void *stream) {
  (void)stream;
  return cuMemsetD8_v2(destination, value, count);
}

static int32_t gemstone_cuStreamSynchronize(void *stream) {
  (void)stream;
  return cuCtxSynchronize();
}

static int32_t gemstone_cuStreamQuery(void *stream) {
  (void)stream;
  return GEMSTONE_STATUS_SUCCESS;
}

static uint64_t gemstone_next_event = UINT64_C(0x500000000);

static int32_t gemstone_cuEventCreate(void **event, uint32_t flags) {
  (void)flags;
  if (event == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *event = (void *)(uintptr_t)__atomic_fetch_add(&gemstone_next_event, UINT64_C(1), __ATOMIC_RELAXED);
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuEventDestroy(void *event) {
  (void)event;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuEventElapsedTime(float *milliseconds, void *start, void *end) {
  (void)start;
  (void)end;
  if (milliseconds == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *milliseconds = 0.0f;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuEventQuery(void *event) {
  (void)event;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuEventRecord(void *event, void *stream) {
  (void)event;
  (void)stream;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuEventRecordWithFlags(void *event, void *stream, uint32_t flags) {
  (void)flags;
  return gemstone_cuEventRecord(event, stream);
}

static int32_t gemstone_cuEventSynchronize(void *event) {
  (void)event;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuStreamWaitEvent(void *stream, void *event, uint32_t flags) {
  (void)stream;
  (void)event;
  (void)flags;
  return GEMSTONE_STATUS_SUCCESS;
}

static uint64_t gemstone_next_stream = UINT64_C(0x400000000);

int32_t cuStreamCreate(void **stream, uint32_t flags) {
  (void)flags;
  if (stream == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *stream = (void *)(uintptr_t)__atomic_fetch_add(&gemstone_next_stream, UINT64_C(1), __ATOMIC_RELAXED);
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuStreamCreateWithPriority(void **stream, uint32_t flags, int32_t priority) {
  (void)priority;
  return cuStreamCreate(stream, flags);
}

int32_t cuStreamDestroy_v2(void *stream) {
  (void)stream;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuStreamDestroy(void *stream) {
  return cuStreamDestroy_v2(stream);
}

static uint64_t gemstone_mem_pool_release_threshold = 0;

int32_t cuMemPoolCreate(void **pool, const void *properties) {
  (void)properties;
  if (pool == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *pool = (void *)(uintptr_t)UINT64_C(0x300000000);
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuMemPoolDestroy(void *pool) {
  (void)pool;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuMemPoolGetAttribute(void *pool, int32_t attribute, void *value) {
  (void)pool;
  if (value == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  if (attribute == 0) *(uint64_t *)value = gemstone_mem_pool_release_threshold;
  else *(uint64_t *)value = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuMemPoolSetAttribute(void *pool, int32_t attribute, void *value) {
  (void)pool;
  if (value == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  if (attribute == 0) gemstone_mem_pool_release_threshold = *(const uint64_t *)value;
  return GEMSTONE_STATUS_SUCCESS;
}

int32_t cuMemPoolTrimTo(void *pool, size_t minimum_bytes_to_keep) {
  (void)pool;
  (void)minimum_bytes_to_keep;
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_cuDevSmResourceSplit(
    void *result,
    uint32_t group_count,
    const void *input,
    void *remainder,
    uint32_t flags,
    void *group_params) {
  (void)result;
  (void)group_count;
  (void)input;
  (void)remainder;
  (void)flags;
  (void)group_params;
  return GEMSTONE_STATUS_NOT_SUPPORTED;
}

/* CUDA runtime's private interface table (CUDART_INTERFACE, 13 entries). */
static int32_t gemstone_cudart_load_module_image(void **module, const void *fatbin, int allow_empty) {
  const char *ptx = gemstone_cuda_find_uncompressed_ptx(fatbin);
  if (ptx == NULL && !allow_empty) return GEMSTONE_STATUS_NOT_SUPPORTED;
  return cuModuleLoadData(module, ptx == NULL ? "" : ptx);
}

static int32_t gemstone_cudart_get_module_from_cubin(void **module, const void *fatbin_wrapper) {
  if (module == NULL) return GEMSTONE_STATUS_NOT_SUPPORTED;
  return gemstone_cudart_load_module_image(module, fatbin_wrapper, 0);
}

static int32_t gemstone_cudart_interface_fn2(void **context, int32_t device) {
  return gemstone_cuDevicePrimaryCtxRetain(context, device);
}

static int32_t gemstone_cudart_get_module_from_cubin_ext1(
    void **module,
    const void *fatbin_wrapper,
    void *arg3,
    void *arg4,
    uint32_t arg5) {
  (void)arg3;
  (void)arg4;
  (void)arg5;
  if (module == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  /* The CUDA runtime asks the driver to materialize header-only fatbin
   * records while bootstrapping libraries.  There is no executable PTX in
   * these records; keep a valid empty module handle so a later lookup can
   * report the ordinary missing-entry result. */
  return gemstone_cudart_load_module_image(module, fatbin_wrapper, 1);
}

static void gemstone_cudart_interface_fn7(uintptr_t arg1) {
  (void)arg1;
}

static int32_t gemstone_cudart_get_module_from_cubin_ext2(
    const void *fatbin_header,
    void **module,
    void *arg3,
    void *arg4,
    uint32_t arg5) {
  (void)arg3;
  (void)arg4;
  (void)arg5;
  if (module == NULL) return GEMSTONE_STATUS_NOT_SUPPORTED;
  return gemstone_cudart_load_module_image(module, fatbin_header, 0);
}

static int32_t gemstone_cudart_load_compilers(void) {
  return GEMSTONE_STATUS_SUCCESS;
}

static const unsigned char gemstone_cudart_interface_id[16] = {
  0x6b, 0xd5, 0xfb, 0x6c, 0x5b, 0xf4, 0xe7, 0x4a,
  0x89, 0x87, 0xd9, 0x39, 0x12, 0xfd, 0x9d, 0xf9
};
static const unsigned char gemstone_integrity_check_id[16] = {
  0xd4, 0x08, 0x20, 0x55, 0xbd, 0xe6, 0x70, 0x4b,
  0x8d, 0x34, 0xba, 0x12, 0x3c, 0x66, 0xe1, 0xf2
};
static const unsigned char gemstone_tools_runtime_callback_hooks_id[16] = {
  0xa0, 0x94, 0x79, 0x8c, 0x2e, 0x74, 0x2e, 0x74,
  0x93, 0xf2, 0x08, 0x00, 0x20, 0x0c, 0x0a, 0x66
};
static const unsigned char gemstone_tools_tls_id[16] = {
  0x42, 0xd8, 0x5a, 0x81, 0x23, 0xf6, 0xcb, 0x47,
  0x82, 0x98, 0xf6, 0xe7, 0x8a, 0x3a, 0xec, 0xdc
};
static const unsigned char gemstone_context_local_storage_id[16] = {
  0xc6, 0x93, 0x33, 0x6e, 0x11, 0x21, 0xdf, 0x11,
  0xa8, 0xc3, 0x68, 0xf3, 0x55, 0xd8, 0x95, 0x93
};
static const unsigned char gemstone_context_checks_id[16] = {
  0x26, 0x3e, 0x88, 0x60, 0x7c, 0xd2, 0x61, 0x43,
  0x92, 0xf6, 0xbb, 0xd5, 0x00, 0x6d, 0xfa, 0x7e
};
static const unsigned char gemstone_driver_metadata_interface_id[16] = {
  0x21, 0x31, 0x8c, 0x60, 0x97, 0x14, 0x32, 0x48,
  0x8c, 0xa6, 0x41, 0xff, 0x73, 0x24, 0xc8, 0xf2
};

static uint32_t gemstone_tools_runtime_callback_buffer1[1024];
static uint32_t gemstone_tools_runtime_callback_buffer2[14];

static void gemstone_tools_runtime_callback_get_buffer1(void **pointer, size_t *size) {
  if (pointer != NULL) *pointer = gemstone_tools_runtime_callback_buffer1;
  if (size != NULL) *size = 1024;
}

static void gemstone_tools_runtime_callback_get_buffer2(void **pointer, size_t *size) {
  if (pointer != NULL) *pointer = gemstone_tools_runtime_callback_buffer2;
  if (size != NULL) *size = 14;
}

static const void *gemstone_cudart_interface_table[13] = {
  (const void *)(uintptr_t)(13 * sizeof(void *)),
  (const void *)(uintptr_t)gemstone_cudart_get_module_from_cubin,
  (const void *)(uintptr_t)gemstone_cudart_interface_fn2,
  NULL,
  NULL,
  NULL,
  (const void *)(uintptr_t)gemstone_cudart_get_module_from_cubin_ext1,
  (const void *)(uintptr_t)gemstone_cudart_interface_fn7,
  (const void *)(uintptr_t)gemstone_cudart_get_module_from_cubin_ext2,
  NULL,
  NULL,
  NULL,
  (const void *)(uintptr_t)gemstone_cudart_load_compilers
};

/* CUDA libraries use this private table for a small typed query on their
 * opaque driver metadata.  It is a driver service, not a cuBLAS shim. */
static int32_t gemstone_driver_metadata_query(void *object, uint64_t *result) {
  if (result == NULL) return GEMSTONE_STATUS_UNKNOWN;
  *result = 0;
  if (object == NULL) return GEMSTONE_STATUS_SUCCESS;
  uint32_t kind = 0;
  memcpy(&kind, object, sizeof(kind));
  if (kind == 0) {
    uint32_t value = 0;
    memcpy(&value, (const unsigned char *)object + 0x60, sizeof(value));
    *result = value;
  } else if (kind == 1) {
    memcpy(result, (const unsigned char *)object + 0x60, sizeof(*result));
  }
  return GEMSTONE_STATUS_SUCCESS;
}

static const void *gemstone_driver_metadata_interface_table[96] = {
  [0] = (const void *)(uintptr_t)0x300,
  [4] = (const void *)(uintptr_t)gemstone_driver_metadata_query
};

static const uint8_t gemstone_integrity_mixing_table[256] = {
  0x29, 0x2e, 0x43, 0xc9, 0xa2, 0xd8, 0x7c, 0x01, 0x3d, 0x36, 0x54, 0xa1, 0xec, 0xf0, 0x06, 0x13,
  0x62, 0xa7, 0x05, 0xf3, 0xc0, 0xc7, 0x73, 0x8c, 0x98, 0x93, 0x2b, 0xd9, 0xbc, 0x4c, 0x82, 0xca,
  0x1e, 0x9b, 0x57, 0x3c, 0xfd, 0xd4, 0xe0, 0x16, 0x67, 0x42, 0x6f, 0x18, 0x8a, 0x17, 0xe5, 0x12,
  0xbe, 0x4e, 0xc4, 0xd6, 0xda, 0x9e, 0xde, 0x49, 0xa0, 0xfb, 0xf5, 0x8e, 0xbb, 0x2f, 0xee, 0x7a,
  0xa9, 0x68, 0x79, 0x91, 0x15, 0xb2, 0x07, 0x3f, 0x94, 0xc2, 0x10, 0x89, 0x0b, 0x22, 0x5f, 0x21,
  0x80, 0x7f, 0x5d, 0x9a, 0x5a, 0x90, 0x32, 0x27, 0x35, 0x3e, 0xcc, 0xe7, 0xbf, 0xf7, 0x97, 0x03,
  0xff, 0x19, 0x30, 0xb3, 0x48, 0xa5, 0xb5, 0xd1, 0xd7, 0x5e, 0x92, 0x2a, 0xac, 0x56, 0xaa, 0xc6,
  0x4f, 0xb8, 0x38, 0xd2, 0x96, 0xa4, 0x7d, 0xb6, 0x76, 0xfc, 0x6b, 0xe2, 0x9c, 0x74, 0x04, 0xf1,
  0x45, 0x9d, 0x70, 0x59, 0x64, 0x71, 0x87, 0x20, 0x86, 0x5b, 0xcf, 0x65, 0xe6, 0x2d, 0xa8, 0x02,
  0x1b, 0x60, 0x25, 0xad, 0xae, 0xb0, 0xb9, 0xf6, 0x1c, 0x46, 0x61, 0x69, 0x34, 0x40, 0x7e, 0x0f,
  0x55, 0x47, 0xa3, 0x23, 0xdd, 0x51, 0xaf, 0x3a, 0xc3, 0x5c, 0xf9, 0xce, 0xba, 0xc5, 0xea, 0x26,
  0x2c, 0x53, 0x0d, 0x6e, 0x85, 0x28, 0x84, 0x09, 0xd3, 0xdf, 0xcd, 0xf4, 0x41, 0x81, 0x4d, 0x52,
  0x6a, 0xdc, 0x37, 0xc8, 0x6c, 0xc1, 0xab, 0xfa, 0x24, 0xe1, 0x7b, 0x08, 0x0c, 0xbd, 0xb1, 0x4a,
  0x78, 0x88, 0x95, 0x8b, 0xe3, 0x63, 0xe8, 0x6d, 0xe9, 0xcb, 0xd5, 0xfe, 0x3b, 0x00, 0x1d, 0x39,
  0xf2, 0xef, 0xb7, 0x0e, 0x66, 0x58, 0xd0, 0xe4, 0xa6, 0x77, 0x72, 0xf8, 0xeb, 0x75, 0x4b, 0x0a,
  0x31, 0x44, 0x50, 0xb4, 0x8f, 0xed, 0x1f, 0x1a, 0xdb, 0x99, 0x8d, 0x33, 0x9f, 0x11, 0x83, 0x14
};

static void gemstone_integrity_single_pass(uint8_t *value, uint8_t input) {
  uint8_t position = value[0x40];
  value[(size_t)position + 0x10] = input;
  const uint8_t next_position = (uint8_t)((position + 1) & 0x0f);
  value[(size_t)position + 0x20] = value[position] ^ input;
  const uint8_t mixed = gemstone_integrity_mixing_table[(uint8_t)(input ^ value[0x41])];
  value[(size_t)position + 0x30] = mixed ^ value[(size_t)position + 0x30];
  value[0x41] = value[(size_t)position + 0x30];
  value[0x40] = next_position;
  if (next_position != 0) return;
  uint8_t accumulator = 0x29;
  uint8_t counter = 0;
  for (;;) {
    accumulator ^= value[0];
    value[0] = accumulator;
    for (size_t index = 1; index < 0x30; ++index) {
      accumulator = value[index] ^ gemstone_integrity_mixing_table[accumulator];
      value[index] = accumulator;
    }
    accumulator = (uint8_t)(accumulator + counter);
    ++counter;
    if (counter == 0x12) break;
    accumulator = gemstone_integrity_mixing_table[accumulator];
  }
}

static void gemstone_integrity_hash_bytes(
    uint8_t *value,
    const void *input,
    size_t size,
    uint8_t xor_mask) {
  const uint8_t *bytes = (const uint8_t *)input;
  for (size_t index = 0; index < size; ++index) {
    gemstone_integrity_single_pass(value, (uint8_t)(bytes[index] ^ xor_mask));
  }
}

typedef struct GemstoneIntegrityInput {
  uint32_t driver_version;
  uint32_t version;
  uint32_t current_process;
  uint32_t current_thread;
  const void *cudart_table;
  const void *integrity_table;
  const void *integrity_function;
  uint64_t unix_seconds;
} GemstoneIntegrityInput;

typedef struct GemstoneIntegrityDevice {
  uint8_t uuid[16];
  int32_t pci_domain;
  int32_t pci_bus;
  int32_t pci_device;
} GemstoneIntegrityDevice;

static const void *gemstone_integrity_check_table[3];

static void gemstone_integrity_zero_result(uint8_t *value) {
  memset(value, 0, 16);
  memset(value + 48, 0, 18);
}

static void gemstone_integrity_hash(
    uint32_t version,
    uint64_t unix_seconds,
    uint32_t driver_version,
    const void *integrity_table,
    const void *integrity_function,
    uint8_t output[16]) {
  static const uint8_t pass1_result[16] = {
    0x14, 0x6a, 0xdd, 0xae, 0x53, 0xa9, 0xa7, 0x52,
    0xaa, 0x08, 0x41, 0x36, 0x0b, 0xf5, 0x5a, 0x9f
  };
  uint8_t value[66] = {0};
  gemstone_integrity_hash_bytes(value, pass1_result, sizeof(pass1_result), 0x36);
  const GemstoneIntegrityInput input = {
    driver_version,
    version,
    (uint32_t)getpid(),
    (uint32_t)(uintptr_t)pthread_self(),
    gemstone_cudart_interface_table,
    integrity_table,
    integrity_function,
    unix_seconds
  };
  gemstone_integrity_hash_bytes(value, &input, sizeof(input), 0);
  const GemstoneIntegrityDevice device = {
    {0x47, 0x45, 0x4d, 0x53, 0x54, 0x4f, 0x4e, 0x45, 0x2d, 0x43, 0x55, 0x44, 0x41, 0x2d, 0x30, 0x31},
    0,
    1,
    1
  };
  gemstone_integrity_hash_bytes(value, &device, sizeof(device), 0);
  uint8_t pass5_result[16];
  uint8_t padding = (uint8_t)(16 - value[64]);
  for (uint8_t index = 0; index < padding; ++index) gemstone_integrity_single_pass(value, padding);
  for (uint8_t *cursor = value + 48; cursor < value + 64; ++cursor) {
    gemstone_integrity_single_pass(value, *cursor);
  }
  memcpy(pass5_result, value, sizeof(pass5_result));
  gemstone_integrity_zero_result(value);
  gemstone_integrity_hash_bytes(value, pass1_result, sizeof(pass1_result), 0x5c);
  gemstone_integrity_hash_bytes(value, pass5_result, sizeof(pass5_result), 0);
  padding = (uint8_t)(16 - value[64]);
  for (uint8_t index = 0; index < padding; ++index) gemstone_integrity_single_pass(value, padding);
  for (uint8_t *cursor = value + 48; cursor < value + 64; ++cursor) {
    gemstone_integrity_single_pass(value, *cursor);
  }
  memcpy(output, value, sizeof(pass5_result));
}

static int32_t gemstone_integrity_check(
    uint32_t version,
    uint64_t unix_seconds,
    uint64_t *result) {
  if (result == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  if (version % 10 == 0) {
    result[0] = UINT64_C(0x3341181c03cb675c);
    result[1] = UINT64_C(0x8ed383aa1f4cd1e8);
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (version % 10 == 1) {
    result[0] = UINT64_C(0x1841181c03cb675c);
    result[1] = UINT64_C(0x8ed383aa1f4cd1e8);
    return GEMSTONE_STATUS_SUCCESS;
  }
  gemstone_integrity_hash(version, unix_seconds, 13030, gemstone_integrity_check_table, gemstone_integrity_check, (uint8_t *)result);
  return GEMSTONE_STATUS_SUCCESS;
}

static const void *gemstone_integrity_check_table[3] = {
  (const void *)(uintptr_t)(3 * sizeof(void *)),
  (const void *)(uintptr_t)gemstone_integrity_check,
  NULL
};

static const void *gemstone_tools_runtime_callback_hooks_table[7] = {
  (const void *)(uintptr_t)(7 * sizeof(void *)),
  NULL,
  (const void *)(uintptr_t)gemstone_tools_runtime_callback_get_buffer1,
  NULL,
  NULL,
  NULL,
  (const void *)(uintptr_t)gemstone_tools_runtime_callback_get_buffer2
};

static int32_t gemstone_tools_tls_get(
    void **value,
    void *arg2,
    const void *table,
    void *arg4,
    void *arg5,
    void *arg6) {
  (void)arg2;
  (void)table;
  (void)arg4;
  (void)arg5;
  (void)arg6;
  if (value == NULL) return GEMSTONE_STATUS_UNKNOWN;
  *value = NULL;
  return GEMSTONE_STATUS_SUCCESS;
}

static const void *gemstone_tools_tls_table[4] = {
  (const void *)(uintptr_t)(4 * sizeof(void *)),
  NULL,
  (const void *)(uintptr_t)gemstone_tools_tls_get,
  NULL
};

typedef void (*GemstoneContextStorageDestructor)(void *, void *, void *);
typedef struct GemstoneContextStorage {
  void *context;
  void *key;
  void *value;
  GemstoneContextStorageDestructor destructor;
} GemstoneContextStorage;
#define GEMSTONE_CUDA_STATUS_INVALID_HANDLE 400
#define GEMSTONE_CUDA_STATUS_INVALID_CONTEXT 201
static GemstoneContextStorage gemstone_context_storage[64];

static void *gemstone_storage_context(void *context) {
  return context == NULL ? gemstone_current_context : context;
}

static int32_t gemstone_context_local_storage_put(
    void *context,
    void *key,
    void *value,
    GemstoneContextStorageDestructor destructor) {
  context = gemstone_storage_context(context);
  if (context == NULL || key == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  for (size_t index = 0; index < sizeof(gemstone_context_storage) / sizeof(gemstone_context_storage[0]); ++index) {
    GemstoneContextStorage *entry = &gemstone_context_storage[index];
    if (entry->context == context && entry->key == key) {
      entry->value = value;
      entry->destructor = destructor;
      return GEMSTONE_STATUS_SUCCESS;
    }
  }
  for (size_t index = 0; index < sizeof(gemstone_context_storage) / sizeof(gemstone_context_storage[0]); ++index) {
    GemstoneContextStorage *entry = &gemstone_context_storage[index];
    if (entry->context == NULL) {
      entry->context = context;
      entry->key = key;
      entry->value = value;
      entry->destructor = destructor;
      return GEMSTONE_STATUS_SUCCESS;
    }
  }
  return GEMSTONE_STATUS_UNKNOWN;
}

static int32_t gemstone_context_local_storage_delete(void *context, void *key) {
  context = gemstone_storage_context(context);
  if (context == NULL || key == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  for (size_t index = 0; index < sizeof(gemstone_context_storage) / sizeof(gemstone_context_storage[0]); ++index) {
    GemstoneContextStorage *entry = &gemstone_context_storage[index];
    if (entry->context == context && entry->key == key) {
      memset(entry, 0, sizeof(*entry));
      return GEMSTONE_STATUS_SUCCESS;
    }
  }
  return GEMSTONE_STATUS_SUCCESS;
}

static int32_t gemstone_context_local_storage_get(void **value, void *context, void *key) {
  context = gemstone_storage_context(context);
  if (value == NULL || key == NULL) {
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  if (context == NULL) return GEMSTONE_CUDA_STATUS_INVALID_CONTEXT;
  for (size_t index = 0; index < sizeof(gemstone_context_storage) / sizeof(gemstone_context_storage[0]); ++index) {
    GemstoneContextStorage *entry = &gemstone_context_storage[index];
    if (entry->context == context && entry->key == key) {
      *value = entry->value;
      return GEMSTONE_STATUS_SUCCESS;
    }
  }
  return GEMSTONE_CUDA_STATUS_INVALID_HANDLE;
}

static const void *gemstone_context_local_storage_table[4] = {
  (const void *)(uintptr_t)gemstone_context_local_storage_put,
  (const void *)(uintptr_t)gemstone_context_local_storage_delete,
  (const void *)(uintptr_t)gemstone_context_local_storage_get,
  NULL
};

static int32_t gemstone_context_check(
    void *context,
    uint32_t *result1,
    const void **result2) {
  (void)context;
  (void)result2;
  if (result1 == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  *result1 = 0;
  return GEMSTONE_STATUS_SUCCESS;
}

static uint32_t gemstone_context_check_fn3(void) {
  return 0;
}

static const void *gemstone_context_checks_table[4] = {
  (const void *)(uintptr_t)(4 * sizeof(void *)),
  NULL,
  (const void *)(uintptr_t)gemstone_context_check,
  (const void *)(uintptr_t)gemstone_context_check_fn3
};

int32_t gemstone_cuGetExportTable(const void **table, const void *table_id) {
  if (table == NULL || table_id == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  if (memcmp(table_id, gemstone_cudart_interface_id, sizeof(gemstone_cudart_interface_id)) == 0) {
    *table = gemstone_cudart_interface_table;
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (memcmp(table_id, gemstone_integrity_check_id, sizeof(gemstone_integrity_check_id)) == 0) {
    *table = gemstone_integrity_check_table;
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (memcmp(table_id, gemstone_tools_runtime_callback_hooks_id, sizeof(gemstone_tools_runtime_callback_hooks_id)) == 0) {
    *table = gemstone_tools_runtime_callback_hooks_table;
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (memcmp(table_id, gemstone_tools_tls_id, sizeof(gemstone_tools_tls_id)) == 0) {
    *table = gemstone_tools_tls_table;
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (memcmp(table_id, gemstone_context_local_storage_id, sizeof(gemstone_context_local_storage_id)) == 0) {
    *table = gemstone_context_local_storage_table;
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (memcmp(table_id, gemstone_context_checks_id, sizeof(gemstone_context_checks_id)) == 0) {
    *table = gemstone_context_checks_table;
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (memcmp(table_id, gemstone_driver_metadata_interface_id, sizeof(gemstone_driver_metadata_interface_id)) == 0) {
    *table = gemstone_driver_metadata_interface_table;
    return GEMSTONE_STATUS_SUCCESS;
  }
  return GEMSTONE_STATUS_INVALID_VALUE;
}

int32_t GEMSTONE_CUDA_API gemstone_cuGetProcAddress_v2(
    const char *symbol,
    void **pfn,
    int32_t cuda_version,
    uint64_t flags,
    int32_t *symbol_status) {
  (void)flags;
  if (symbol == NULL || pfn == NULL) return GEMSTONE_STATUS_INVALID_VALUE;
  if (strcmp(symbol, "cuLaunchKernel") == 0) {
    *pfn = (void *)(uintptr_t)cuLaunchKernel;
    if (symbol_status != NULL) *symbol_status = 0;
    return GEMSTONE_STATUS_SUCCESS;
  }
  if (strcmp(symbol, "cuLaunchKernel_ptsz") == 0) {
    *pfn = (void *)(uintptr_t)cuLaunchKernel_ptsz;
    if (symbol_status != NULL) *symbol_status = 0;
    return GEMSTONE_STATUS_SUCCESS;
  }
#define GEMSTONE_CUDA_PROC(name, function) \
  if (strcmp(symbol, name) == 0) { \
    *pfn = (void *)(uintptr_t)(function); \
    if (symbol_status != NULL) *symbol_status = 0; \
    return GEMSTONE_STATUS_SUCCESS; \
  }
#define GEMSTONE_CUDA_VERSIONED_PROC(name, legacy, modern) \
  if (strcmp(symbol, name) == 0) { \
    if (cuda_version > 0 && cuda_version < 3020) *pfn = (void *)(uintptr_t)(legacy); \
    else *pfn = (void *)(uintptr_t)(modern); \
    if (symbol_status != NULL) *symbol_status = 0; \
    return GEMSTONE_STATUS_SUCCESS; \
  }
  GEMSTONE_CUDA_PROC("cuInit", cuInit)
  GEMSTONE_CUDA_PROC("cuDriverGetVersion", cuDriverGetVersion)
  GEMSTONE_CUDA_PROC("cuDeviceGetCount", cuDeviceGetCount)
  GEMSTONE_CUDA_PROC("cuDeviceGet", cuDeviceGet)
  GEMSTONE_CUDA_PROC("cuDeviceTotalMem", cuDeviceTotalMem)
  GEMSTONE_CUDA_PROC("cuDeviceTotalMem_v2", cuDeviceTotalMem_v2)
  GEMSTONE_CUDA_PROC("cuArray3DCreate_v2", cuArray3DCreate_v2)
  GEMSTONE_CUDA_PROC("cuArray3DGetDescriptor_v2", cuArray3DGetDescriptor_v2)
  GEMSTONE_CUDA_PROC("cuArrayCreate_v2", cuArrayCreate_v2)
  GEMSTONE_CUDA_PROC("cuArrayDestroy", cuArrayDestroy)
  GEMSTONE_CUDA_PROC("cuArrayGetDescriptor_v2", cuArrayGetDescriptor_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuArray3DCreate", cuArray3DCreate, cuArray3DCreate_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuArray3DGetDescriptor", cuArray3DGetDescriptor, cuArray3DGetDescriptor_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuArrayCreate", cuArrayCreate, cuArrayCreate_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuArrayGetDescriptor", cuArrayGetDescriptor, cuArrayGetDescriptor_v2)
  GEMSTONE_CUDA_PROC("cuDeviceGetAttribute", gemstone_cuDeviceGetAttribute)
  GEMSTONE_CUDA_PROC("cuDeviceGetByPCIBusId", gemstone_cuDeviceGetByPCIBusId)
  GEMSTONE_CUDA_PROC("cuDeviceGetName", gemstone_cuDeviceGetName)
  GEMSTONE_CUDA_PROC("cuDeviceGetP2PAttribute", gemstone_cuDeviceGetP2PAttribute)
  GEMSTONE_CUDA_PROC("cuDeviceCanAccessPeer", gemstone_cuDeviceCanAccessPeer)
  GEMSTONE_CUDA_PROC("cuDeviceGetTexture1DLinearMaxWidth", gemstone_cuDeviceGetTexture1DLinearMaxWidth)
  GEMSTONE_CUDA_PROC("cuDeviceGetDefaultMemPool", gemstone_cuDeviceGetDefaultMemPool)
  GEMSTONE_CUDA_PROC("cuDeviceGetMemPool", gemstone_cuDeviceGetMemPool)
  GEMSTONE_CUDA_PROC("cuDeviceSetMemPool", gemstone_cuDeviceSetMemPool)
  GEMSTONE_CUDA_PROC("cuMemGetDefaultMemPool", gemstone_cuMemGetDefaultMemPool)
  GEMSTONE_CUDA_PROC("cuMemGetMemPool", gemstone_cuMemGetMemPool)
  GEMSTONE_CUDA_PROC("cuMemSetMemPool", gemstone_cuMemSetMemPool)
  GEMSTONE_CUDA_PROC("cuDeviceComputeCapability", gemstone_cuDeviceComputeCapability)
  GEMSTONE_CUDA_PROC("cuDeviceGetUuid", gemstone_cuDeviceGetUuid)
  GEMSTONE_CUDA_PROC("cuDeviceGetUuid_v2", gemstone_cuDeviceGetUuid)
  GEMSTONE_CUDA_PROC("cuDeviceGetPCIBusId", gemstone_cuDeviceGetPCIBusId)
  GEMSTONE_CUDA_PROC("cuCtxCreate", cuCtxCreate)
  GEMSTONE_CUDA_PROC("cuCtxCreate_v2", cuCtxCreate_v2)
  GEMSTONE_CUDA_PROC("cuCtxCreate_v4", gemstone_cuCtxCreate_v4)
  GEMSTONE_CUDA_PROC("cuCtxDestroy", cuCtxDestroy_v2)
  GEMSTONE_CUDA_PROC("cuCtxDestroy_v2", cuCtxDestroy_v2)
  GEMSTONE_CUDA_PROC("cuCtxSynchronize", cuCtxSynchronize)
  GEMSTONE_CUDA_PROC("cuCtxSynchronize_v2", gemstone_cuCtxSynchronize_v2)
  GEMSTONE_CUDA_PROC("cuCtxGetCacheConfig", gemstone_cuCtxGetCacheConfig)
  GEMSTONE_CUDA_PROC("cuCtxSetCacheConfig", gemstone_cuCtxSetCacheConfig)
  GEMSTONE_CUDA_PROC("cuCtxGetSharedMemConfig", gemstone_cuCtxGetSharedMemConfig)
  GEMSTONE_CUDA_PROC("cuCtxSetSharedMemConfig", gemstone_cuCtxSetSharedMemConfig)
  GEMSTONE_CUDA_PROC("cuCtxGetStreamPriorityRange", gemstone_cuCtxGetStreamPriorityRange)
  GEMSTONE_CUDA_PROC("cuCtxResetPersistingL2Cache", gemstone_cuCtxResetPersistingL2Cache)
  GEMSTONE_CUDA_PROC("cuCtxGetFlags", gemstone_cuCtxGetFlags)
  GEMSTONE_CUDA_PROC("cuCtxGetApiVersion", gemstone_cuCtxGetApiVersion)
  GEMSTONE_CUDA_PROC("cuCtxGetLimit", gemstone_cuCtxGetLimit)
  GEMSTONE_CUDA_PROC("cuCtxSetLimit", gemstone_cuCtxSetLimit)
  GEMSTONE_CUDA_PROC("cuCtxDetach", gemstone_cuCtxDetach)
  GEMSTONE_CUDA_PROC("cuCtxGetCurrent", gemstone_cuCtxGetCurrent)
  GEMSTONE_CUDA_PROC("cuCtxGetDevice", gemstone_cuCtxGetDevice)
  GEMSTONE_CUDA_PROC("cuCtxGetDevice_v2", gemstone_cuCtxGetDevice_v2)
  GEMSTONE_CUDA_PROC("cuCtxSetCurrent", gemstone_cuCtxSetCurrent)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxRetain", gemstone_cuDevicePrimaryCtxRetain)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxRelease", gemstone_cuDevicePrimaryCtxRelease_v2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxRelease_v2", gemstone_cuDevicePrimaryCtxRelease_v2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxReset", gemstone_cuDevicePrimaryCtxReset_v2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxReset_v2", gemstone_cuDevicePrimaryCtxReset_v2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxGetState", gemstone_cuDevicePrimaryCtxGetState)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxSetFlags", gemstone_cuDevicePrimaryCtxSetFlags_v2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxSetFlags_v2", gemstone_cuDevicePrimaryCtxSetFlags_v2)
  GEMSTONE_CUDA_PROC("cuMemAlloc", cuMemAlloc_v2)
  GEMSTONE_CUDA_PROC("cuMemAlloc_v2", cuMemAlloc_v2)
  GEMSTONE_CUDA_PROC("cuMemFree", cuMemFree_v2)
  GEMSTONE_CUDA_PROC("cuMemFree_v2", cuMemFree_v2)
  GEMSTONE_CUDA_PROC("cuMemAllocManaged", gemstone_cuMemAllocManaged)
  GEMSTONE_CUDA_PROC("cuMemAllocAsync", gemstone_cuMemAllocAsync)
  GEMSTONE_CUDA_PROC("cuMemAllocFromPoolAsync", gemstone_cuMemAllocFromPoolAsync)
  GEMSTONE_CUDA_PROC("cuMemFreeAsync", gemstone_cuMemFreeAsync)
  GEMSTONE_CUDA_PROC("cuMemPoolCreate", cuMemPoolCreate)
  GEMSTONE_CUDA_PROC("cuMemPoolDestroy", cuMemPoolDestroy)
  GEMSTONE_CUDA_PROC("cuMemPoolGetAttribute", cuMemPoolGetAttribute)
  GEMSTONE_CUDA_PROC("cuMemPoolSetAttribute", cuMemPoolSetAttribute)
  GEMSTONE_CUDA_PROC("cuMemPoolTrimTo", cuMemPoolTrimTo)
  GEMSTONE_CUDA_PROC("cuMemAllocPitch", gemstone_cuMemAllocPitch_v2)
  GEMSTONE_CUDA_PROC("cuMemAllocPitch_v2", gemstone_cuMemAllocPitch_v2)
  GEMSTONE_CUDA_PROC("cuMemGetInfo", gemstone_cuMemGetInfo_v2)
  GEMSTONE_CUDA_PROC("cuMemGetInfo_v2", gemstone_cuMemGetInfo_v2)
  GEMSTONE_CUDA_PROC("cuFlushGPUDirectRDMAWrites", cuFlushGPUDirectRDMAWrites)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoD", cuMemcpyHtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoD_v2", cuMemcpyHtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoH", cuMemcpyDtoH_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoH_v2", cuMemcpyDtoH_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoD", cuMemcpyDtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoD_v2", cuMemcpyDtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpy2D_v2", cuMemcpy2D_v2)
  GEMSTONE_CUDA_PROC("cuMemcpy2DUnaligned_v2", cuMemcpy2DUnaligned_v2)
  GEMSTONE_CUDA_PROC("cuMemcpy2DAsync_v2", cuMemcpy2DAsync_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuMemcpy2D", cuMemcpy2D, cuMemcpy2D_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuMemcpy2DUnaligned", cuMemcpy2DUnaligned, cuMemcpy2DUnaligned_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuMemcpy2DAsync", cuMemcpy2DAsync, cuMemcpy2DAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpy3D_v2", cuMemcpy3D_v2)
  GEMSTONE_CUDA_PROC("cuMemcpy3DAsync_v2", cuMemcpy3DAsync_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuMemcpy3D", cuMemcpy3D, cuMemcpy3D_v2)
  GEMSTONE_CUDA_VERSIONED_PROC("cuMemcpy3DAsync", cuMemcpy3DAsync, cuMemcpy3DAsync_v2)
  GEMSTONE_CUDA_PROC("cuFuncGetAttribute", cuFuncGetAttribute)
  GEMSTONE_CUDA_PROC("cuKernelGetAttribute", cuKernelGetAttribute)
  GEMSTONE_CUDA_PROC("cuOccupancyMaxActiveBlocksPerMultiprocessor", cuOccupancyMaxActiveBlocksPerMultiprocessor)
  GEMSTONE_CUDA_PROC("cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags", cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoDAsync", gemstone_cuMemcpyHtoDAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoDAsync_v2", gemstone_cuMemcpyHtoDAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoHAsync", gemstone_cuMemcpyDtoHAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoHAsync_v2", gemstone_cuMemcpyDtoHAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoDAsync", gemstone_cuMemcpyDtoDAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoDAsync_v2", gemstone_cuMemcpyDtoDAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemsetD8", cuMemsetD8_v2)
  GEMSTONE_CUDA_PROC("cuMemsetD8_v2", cuMemsetD8_v2)
  GEMSTONE_CUDA_PROC("cuMemsetD8Async", gemstone_cuMemsetD8Async)
  GEMSTONE_CUDA_PROC("cuStreamSynchronize", gemstone_cuStreamSynchronize)
  GEMSTONE_CUDA_PROC("cuStreamQuery", gemstone_cuStreamQuery)
  GEMSTONE_CUDA_PROC("cuEventCreate", gemstone_cuEventCreate)
  GEMSTONE_CUDA_PROC("cuEventDestroy", gemstone_cuEventDestroy)
  GEMSTONE_CUDA_PROC("cuEventElapsedTime", gemstone_cuEventElapsedTime)
  GEMSTONE_CUDA_PROC("cuEventQuery", gemstone_cuEventQuery)
  GEMSTONE_CUDA_PROC("cuEventRecord", gemstone_cuEventRecord)
  GEMSTONE_CUDA_PROC("cuEventRecordWithFlags", gemstone_cuEventRecordWithFlags)
  GEMSTONE_CUDA_PROC("cuEventSynchronize", gemstone_cuEventSynchronize)
  GEMSTONE_CUDA_PROC("cuStreamWaitEvent", gemstone_cuStreamWaitEvent)
  GEMSTONE_CUDA_PROC("cuStreamCreate", cuStreamCreate)
  GEMSTONE_CUDA_PROC("cuStreamCreateWithPriority", cuStreamCreateWithPriority)
  GEMSTONE_CUDA_PROC("cuStreamDestroy", cuStreamDestroy)
  GEMSTONE_CUDA_PROC("cuStreamDestroy_v2", cuStreamDestroy_v2)
  GEMSTONE_CUDA_PROC("cuStreamAddCallback", cuStreamAddCallback)
  GEMSTONE_CUDA_PROC("cuLaunchHostFunc", cuLaunchHostFunc)
  GEMSTONE_CUDA_PROC("cuModuleLoadData", cuModuleLoadData)
  GEMSTONE_CUDA_PROC("cuModuleGetFunction", cuModuleGetFunction)
  GEMSTONE_CUDA_VERSIONED_PROC("cuModuleGetGlobal", cuModuleGetGlobal, cuModuleGetGlobal_v2)
  GEMSTONE_CUDA_PROC("cuModuleGetGlobal_v2", cuModuleGetGlobal_v2)
  GEMSTONE_CUDA_PROC("cuModuleUnload", cuModuleUnload)
  GEMSTONE_CUDA_PROC("cuLibraryLoadData", gemstone_cuLibraryLoadData)
  GEMSTONE_CUDA_PROC("cuLibraryGetKernel", gemstone_cuLibraryGetKernel)
  GEMSTONE_CUDA_PROC("cuKernelGetFunction", cuKernelGetFunction)
  GEMSTONE_CUDA_PROC("cuLibraryGetGlobal", cuLibraryGetGlobal)
  GEMSTONE_CUDA_PROC("cuLibraryGetModule", gemstone_cuLibraryGetModule)
  GEMSTONE_CUDA_PROC("cuLibraryUnload", gemstone_cuLibraryUnload)
  GEMSTONE_CUDA_PROC("cuLaunchKernel", cuLaunchKernel)
  GEMSTONE_CUDA_PROC("cuLaunchKernel_ptsz", cuLaunchKernel_ptsz)
  GEMSTONE_CUDA_PROC("cuGetErrorString", gemstone_cuGetErrorString)
  GEMSTONE_CUDA_PROC("cuGetErrorName", gemstone_cuGetErrorName)
  GEMSTONE_CUDA_PROC("cuGetExportTable", gemstone_cuGetExportTable)
  GEMSTONE_CUDA_PROC("cuDevSmResourceSplit", gemstone_cuDevSmResourceSplit)
  GEMSTONE_CUDA_PROC("cuGetProcAddress", cuGetProcAddress)
  GEMSTONE_CUDA_PROC("cuGetProcAddress_v2", gemstone_cuGetProcAddress_v2)
#undef GEMSTONE_CUDA_PROC
#undef GEMSTONE_CUDA_VERSIONED_PROC
  *pfn = NULL;
  if (symbol_status != NULL) *symbol_status = 1;
  return GEMSTONE_STATUS_NOT_FOUND;
}
