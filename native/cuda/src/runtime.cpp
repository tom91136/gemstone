#include <algorithm>
#include <array>
#include <atomic>
#include <charconv>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <memory>
#include <mutex>
#include <new>
#include <optional>
#include <stdexcept>
#include <string>
#include <string_view>
#include <type_traits>
#include <utility>
#include <vector>

#include <pthread.h>
#include <stdint.h>
#include <unistd.h>

#include <aspartame/optional.hpp>
#include <aspartame/string_view.hpp>
#include <aspartame/vector.hpp>

#include "gemstone_cuda_constants.hpp"
#include "gemstone_execution_rpc.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/export.h>
#include <gemstone/lz4.hpp>
#include <gemstone/zstd.hpp>

using namespace aspartame;
using namespace gemstone::cuda::api;
using gemstone::readNative;

namespace {

struct FatBinaryWrapper {
  int32_t magic;
  int32_t version;
  const void *data;
  void *filename;
};

constexpr size_t MaximumImageSize = 64u * 1024u * 1024u;

using HostCallback = void (*)(void *);
using StreamCallback = void (*)(void *, int32_t, void *);

std::optional<int32_t> parsePtxTarget(std::string_view target) {
  target = target ^ drop_while([](char value) { return value == ' ' || value == '\t' || value == '\r'; });
  if (target ^ starts_with("compute")) target = target ^ strip_prefix("compute") ^ strip_prefix("_");
  else if (target ^ starts_with("sm")) target = target ^ strip_prefix("sm") ^ strip_prefix("_");
  else return std::nullopt;

  const auto digits = target ^ take_while([](char value) { return value >= '0' && value <= '9'; });
  int32_t version = 0;
  const auto parsed = std::from_chars(digits.data(), digits.data() + digits.size(), version);
  return !digits.empty() && parsed.ec == std::errc{} ? std::optional{version} : std::nullopt;
}

std::optional<int32_t> configuredPtxVersion() {
  const char *value = getenv("GEMSTONE_CUDA_TARGET");
  if (!value || *value == '\0') value = getenv("GEMSTONE_CUDA_TARGET_ARCH");
  return value ? parsePtxTarget(value) : std::nullopt;
}

std::optional<std::pair<int32_t, int32_t>> computeCapability() {
  return configuredPtxVersion() ^ map([](int32_t target) { return std::pair{target / 10, target % 10}; })
         ^ filter([](int32_t major, int32_t) { return major > 0; });
}

std::optional<int32_t> registeredPtxVersion(const void *function);

thread_local std::vector<char> decompressedPtx;
thread_local std::vector<char> lz4DecompressedPtx;
thread_local std::string combinedPtx;

std::optional<std::string_view> decompressPtx(const void *source, size_t sourceSize) {
  const auto size = gemstone::decompressZstdFrame(decompressedPtx, source, sourceSize, MaximumImageSize);
  return size ? std::optional{std::string_view(decompressedPtx.data(), *size)} : std::nullopt;
}

std::optional<std::string_view> decompressLz4(const void *source, size_t sourceSize, size_t destinationSize) {
  if (destinationSize > MaximumImageSize) return std::nullopt;
  const auto size = gemstone::decompressLz4(lz4DecompressedPtx, source, sourceSize, destinationSize);
  return size ? std::optional{std::string_view(lz4DecompressedPtx.data(), *size)} : std::nullopt;
}

bool isPtxHeader(std::string_view line) {
  const auto directive = line ^ trim_leading();
  return directive ^ starts_with(".version") || directive ^ starts_with(".target") || directive ^ starts_with(".address_size");
}

bool appendPtx(std::string_view source) {
  try {
    const bool first = combinedPtx.empty();
    const std::string text =
        first ? std::string(source) : source ^ lines() ^ filter([](std::string_view line) { return !isPtxHeader(line); }) ^ mk_string("\n");
    const size_t separator = first || text.empty() ? 0 : 1;
    if (combinedPtx.size() > MaximumImageSize - separator || text.size() > MaximumImageSize - separator - combinedPtx.size()) return false;
    if (separator) combinedPtx.push_back('\n');
    combinedPtx += text;
    return true;
  } catch (const std::exception &) {
    return false;
  }
}

std::optional<std::string_view> findPtxText(std::string_view payload) {
  const auto search = payload ^ take(128);
  const auto prefix = search.find(".version");
  if (prefix == std::string_view::npos) return std::nullopt;
  const size_t after = prefix + sizeof(".version") - 1;
  if (after < payload.size() && payload[after] != ' ' && payload[after] != '\t' && payload[after] != '\r' && payload[after] != '\n')
    return std::nullopt;
  const auto text = payload.substr(prefix);
  return text.substr(0, text.find('\0'));
}

bool appendFatbinPtx(const uint8_t *data) {
  if (!data || readNative<uint32_t>(data) != UINT32_C(0xba55ed50)) return false;
  const uint16_t outer_header_size = readNative<uint16_t>(data + 6);
  const uint64_t content_size = readNative<uint64_t>(data + 8);
  if (outer_header_size < 16 || content_size > MaximumImageSize) return false;
  uint64_t total_size = static_cast<uint64_t>(outer_header_size) + content_size;
  uint64_t offset = outer_header_size;
  /* nvcc can wrap the actual fatbin in an empty outer header followed by a
   * second header carrying the real payload size.  The payload records begin
   * after that inner header, not immediately after the outer one. */
  if (content_size == 0 && readNative<uint32_t>(data + offset) == UINT32_C(0xba55ed50)) {
    const uint16_t inner_header_size = readNative<uint16_t>(data + offset + 6);
    const uint64_t inner_content_size = readNative<uint64_t>(data + offset + 8);
    if (inner_header_size < 16 || inner_content_size > MaximumImageSize) return false;
    offset += inner_header_size;
    total_size = offset + inner_content_size;
  }
  while (offset <= total_size && total_size - offset >= 64) {
    const uint8_t *entry = data + offset;
    const uint16_t kind = readNative<uint16_t>(entry);
    const uint32_t header_size = readNative<uint32_t>(entry + 4);
    const uint32_t payload_size = readNative<uint32_t>(entry + 8);
    const uint32_t compressed_size = readNative<uint32_t>(entry + 16);
    const uint64_t flags = readNative<uint64_t>(entry + 40);
    const uint64_t uncompressed_payload = readNative<uint64_t>(entry + 56);
    const uint32_t stored_size = payload_size > compressed_size ? payload_size : compressed_size;
    if (header_size < 64 || static_cast<uint64_t>(header_size) > total_size - offset
        || static_cast<uint64_t>(stored_size) > total_size - offset - header_size)
      return false;
    const auto payload = std::string_view(reinterpret_cast<const char *>(entry + header_size), stored_size);
    std::optional<std::string_view> content = payload;
    if ((flags & UINT64_C(0x2000)) != 0) {
      content = decompressLz4(payload.data(), compressed_size,
                              uncompressed_payload == 0 ? payload_size : static_cast<size_t>(uncompressed_payload));
    } else if ((flags & UINT64_C(0x8000)) != 0) {
      content = decompressPtx(payload.data(), compressed_size);
    }
    if ((kind & 1u) != 0 && content)
      if (const auto ptx = findPtxText(*content); ptx && !appendPtx(*ptx)) return false;
    offset += static_cast<uint64_t>(header_size) + stored_size;
  }
  return true;
}

const char *findUncompressedPtx(const void *fat_binary) {
  if (!fat_binary) return nullptr;
  const FatBinaryWrapper *wrapper = static_cast<const FatBinaryWrapper *>(fat_binary);
  const int is_wrapper = wrapper->magic == INT32_C(0x466243b1);
  const uint8_t *data = is_wrapper ? static_cast<const uint8_t *>(wrapper->data) : static_cast<const uint8_t *>(fat_binary);
  if (!data) return nullptr;
  if (readNative<uint32_t>(data) != UINT32_C(0xba55ed50)) return nullptr;
  combinedPtx.clear();
  if (!appendFatbinPtx(data)) {
    combinedPtx.clear();
    return nullptr;
  }
  /* Relocatable device code is kept by nvcc in a null-terminated array of
   * fatbin pointers stored in the wrapper's fourth field.  The ordinary
   * .nv_fatbin image only contains registration/runtime stubs; include the
   * prelinked images as well so the driver-facing module contains the actual
   * Thrust/CUB entry points. */
  if (is_wrapper && wrapper->version >= 2 && wrapper->filename) {
    const void *const *prelinked = static_cast<const void *const *>(wrapper->filename);
    for (size_t index = 0; index < 64 && prelinked[index]; ++index) {
      const uint8_t *candidate = static_cast<const uint8_t *>(prelinked[index]);
      if (readNative<uint32_t>(candidate) != UINT32_C(0xba55ed50) || !appendFatbinPtx(candidate)) {
        combinedPtx.clear();
        return nullptr;
      }
    }
  }
  return combinedPtx.empty() ? nullptr : combinedPtx.c_str();
}

} // namespace

extern "C" int32_t cuMemAlloc_v2(uint64_t *pointer, size_t size) noexcept;
extern "C" int32_t cuMemFree_v2(uint64_t pointer) noexcept;
extern "C" int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size) noexcept;
extern "C" int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size) noexcept;
extern "C" int32_t cuModuleGetGlobal_v2(uint64_t *pointer, size_t *bytes, void *module, const char *name) noexcept;

namespace {

int32_t managedRegisterRemote(uint64_t pointer, size_t size, int hostMemory) noexcept;
int32_t managedUnregisterRemote(uint64_t pointer) noexcept;
extern thread_local void *currentContext;
void *activeContext();
int32_t managedSynchronizeFromDevice();
bool systemPagingEnabled() noexcept;
thread_local void *contextStack[64];
thread_local size_t contextStackSize = 0;

/* Allocation metadata is populated by successful driver RPC lifecycle hooks and
 * local managed/host allocation paths, never inferred from virtual addresses. */
struct PointerMetadata {
  uint64_t base;
  uint64_t size;
  uint64_t id;
  void *context;
  void *module;
  bool host;
  bool managed;
};
std::mutex pointerMutex;
std::vector<PointerMetadata> pointers;
std::mutex contextMutex;
std::vector<void *> contexts;

int32_t trackContext(void *context) {
  if (!context) return CUDA_ERROR_INVALID_VALUE;
  std::lock_guard lock{contextMutex};
  if (contexts ^ contains(context)) return CUDA_ERROR_INVALID_VALUE;
  try {
    contexts.push_back(context);
  } catch (const std::exception &) {
    return CUDA_ERROR_UNKNOWN;
  }
  return CUDA_SUCCESS;
}

bool contextIsLive(void *context) {
  if (!context) return 0;
  std::lock_guard lock{contextMutex};
  return contexts ^ contains(context);
}

int32_t activateCreatedContext(void *context) {
  if (!contextIsLive(context)) return CUDA_ERROR_INVALID_VALUE;
  if (contextStackSize == 64) return CUDA_ERROR_UNKNOWN;
  contextStack[contextStackSize++] = currentContext;
  currentContext = context;
  return CUDA_SUCCESS;
}

int32_t trackPointer(uint64_t pointer, size_t size, uint64_t allocationId, void *module, bool host, bool managed) {
  if (pointer == 0 || size == 0 || allocationId == 0 || size > UINT64_MAX - pointer) return CUDA_ERROR_INVALID_VALUE;
  std::lock_guard lock{pointerMutex};
  for (const auto &p : pointers) {
    if (p.base == pointer && p.size == size && p.module == module) return CUDA_SUCCESS;
    if (pointer < p.base + p.size && p.base < pointer + size) return CUDA_ERROR_INVALID_VALUE;
  }
  try {
    pointers.push_back(PointerMetadata{pointer, size, allocationId, activeContext(), module, host, managed});
  } catch (const std::exception &) {
    return CUDA_ERROR_UNKNOWN;
  }
  return CUDA_SUCCESS;
}

enum class PointerOwner { Device, Module, Context };

void removePointers(uint64_t pointer, void *owner, PointerOwner byOwner) {
  std::lock_guard lock{pointerMutex};
  pointers ^= filter([=](const auto &p) {
    return !((byOwner == PointerOwner::Device && p.base == pointer) || (byOwner == PointerOwner::Module && p.module == owner)
             || (byOwner == PointerOwner::Context && p.context == owner));
  });
}
void untrackDevice(uint64_t pointer) { removePointers(pointer, nullptr, PointerOwner::Device); }
void untrackModule(void *module) {
  if (module) removePointers(0, module, PointerOwner::Module);
}
void untrackContext(void *context) {
  removePointers(0, context, PointerOwner::Context);
  {
    std::lock_guard lock{contextMutex};
    contexts ^= filter([context](const void *candidate) { return candidate != context; });
  }
  size_t output = 0;
  for (size_t index = 0; index < contextStackSize; ++index)
    if (contextStack[index] != context) contextStack[output++] = contextStack[index];
  contextStackSize = output;
  if (currentContext == context) currentContext = output == 0 ? nullptr : contextStack[--contextStackSize];
}

std::optional<PointerMetadata> pointerMetadata(uint64_t pointer) {
  std::lock_guard lock{pointerMutex};
  for (const auto &p : pointers)
    if (pointer >= p.base && pointer - p.base < p.size) return p;
  return std::nullopt;
}

struct PointerBinding {
  uint64_t allocation;
  uint64_t offset;
  uint64_t remaining;
};

std::optional<PointerBinding> pointerBinding(uint64_t pointer) {
  return pointerMetadata(pointer) ^ map([pointer](const PointerMetadata &metadata) {
           const auto offset = pointer - metadata.base;
           return PointerBinding{metadata.id, offset, metadata.size - offset};
         });
}

std::optional<PointerBinding> pointerRelocationBinding(uint64_t pointer) {
  std::lock_guard lock{pointerMutex};
  for (const auto &metadata : pointers) {
    if (pointer >= metadata.base && pointer - metadata.base <= metadata.size) {
      const auto offset = pointer - metadata.base;
      return PointerBinding{metadata.id, offset, metadata.size - offset};
    }
  }
  return std::nullopt;
}

int32_t pointerGetAttribute(void *data, int32_t attribute, uint64_t pointer) {
  if (!data || pointer == 0) return CUDA_ERROR_INVALID_VALUE;
  const auto found = pointerMetadata(pointer);
  if (!found) return CUDA_ERROR_INVALID_VALUE;
  const auto &metadata = *found;
  switch (attribute) {
    case CU_POINTER_ATTRIBUTE_CONTEXT: *static_cast<void **>(data) = metadata.context; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_MEMORY_TYPE: *static_cast<uint32_t *>(data) = metadata.host ? 1U : 2U; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_DEVICE_POINTER:
      if (!currentContext) return 201; /* CUDA_ERROR_INVALID_CONTEXT */
      *static_cast<uint64_t *>(data) = pointer;
      return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_HOST_POINTER:
      if (!metadata.host && !metadata.managed) return CUDA_ERROR_INVALID_VALUE;
      *static_cast<uint64_t *>(data) = pointer;
      return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_SYNC_MEMOPS:
    case CU_POINTER_ATTRIBUTE_IS_MANAGED:
    case CU_POINTER_ATTRIBUTE_DEVICE_ORDINAL:
    case CU_POINTER_ATTRIBUTE_MAPPED:
    case CU_POINTER_ATTRIBUTE_ALLOWED_HANDLE_TYPES:
    case CU_POINTER_ATTRIBUTE_ACCESS_FLAGS:
    case CU_POINTER_ATTRIBUTE_IS_HW_DECOMPRESS_CAPABLE:
      *static_cast<uint32_t *>(data) = attribute == CU_POINTER_ATTRIBUTE_MAPPED         ? 1U
                                       : attribute == CU_POINTER_ATTRIBUTE_IS_MANAGED   ? static_cast<uint32_t>(metadata.managed)
                                       : attribute == CU_POINTER_ATTRIBUTE_ACCESS_FLAGS ? 3U
                                                                                        : 0U;
      return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_BUFFER_ID:
    case CU_POINTER_ATTRIBUTE_RANGE_START_ADDR:
    case CU_POINTER_ATTRIBUTE_RANGE_SIZE:
    case CU_POINTER_ATTRIBUTE_MAPPING_SIZE:
    case CU_POINTER_ATTRIBUTE_MAPPING_BASE_ADDR:
    case CU_POINTER_ATTRIBUTE_MEMORY_BLOCK_ID:
      *static_cast<uint64_t *>(data) =
          attribute == CU_POINTER_ATTRIBUTE_RANGE_START_ADDR || attribute == CU_POINTER_ATTRIBUTE_MAPPING_BASE_ADDR ? metadata.base
          : attribute == CU_POINTER_ATTRIBUTE_RANGE_SIZE || attribute == CU_POINTER_ATTRIBUTE_MAPPING_SIZE          ? metadata.size
                                                                                                                    : metadata.id;
      return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_MEMPOOL_HANDLE: *static_cast<void **>(data) = nullptr; return CUDA_SUCCESS;
    default: return CUDA_ERROR_INVALID_VALUE;
  }
}

int32_t pointerGetAttributes(uint32_t count, int32_t *attributes, void **data, uint64_t pointer) {
  if (count != 0 && (!attributes || !data)) return CUDA_ERROR_INVALID_VALUE;
  const auto found = pointerMetadata(pointer);
  for (uint32_t index = 0; index < count; ++index) {
    if (!data[index]) return CUDA_ERROR_INVALID_VALUE;
    if (!found || (attributes[index] == CU_POINTER_ATTRIBUTE_HOST_POINTER && !found->host && !found->managed)) {
      switch (attributes[index]) {
        case 1:
        case 4:
        case 17: *static_cast<void **>(data[index]) = nullptr; break;
        case 3:
        case 7:
        case 11:
        case 12:
        case 18:
        case 19:
        case 20: *static_cast<uint64_t *>(data[index]) = 0; break;
        case 2:
        case 6:
        case 8:
        case 9:
        case 13:
        case 14:
        case 16:
        case 21: *static_cast<uint32_t *>(data[index]) = 0; break;
        default: return CUDA_ERROR_INVALID_VALUE;
      }
      continue;
    }
    const int32_t status = pointerGetAttribute(data[index], attributes[index], pointer);
    if (status != CUDA_SUCCESS) return status;
  }
  return CUDA_SUCCESS;
}

} // namespace

extern "C" int32_t cuPointerGetAttribute(void *data, int32_t attribute, uint64_t pointer) {
  return pointerGetAttribute(data, attribute, pointer);
}

extern "C" int32_t cuPointerGetAttributes(uint32_t count, int32_t *attributes, void **data, uint64_t pointer) {
  return pointerGetAttributes(count, attributes, data, pointer);
}

namespace {

struct MirroredAllocation {
  uint64_t pointer;
  size_t size;
  std::unique_ptr<unsigned char[]> host;
};

std::mutex mirroredMutex;
std::vector<std::unique_ptr<MirroredAllocation>> mirroredAllocations;
bool managedDeviceDirty = false;
thread_local bool managedSyncToDeviceActive = false;
thread_local bool managedSyncFromDeviceActive = false;

int32_t mirroredAllocate(uint64_t *pointer, size_t size, bool hostMemory) {
  if (!pointer) return CUDA_ERROR_INVALID_VALUE;
  const size_t allocation_size = size == 0 ? 1 : size;
  auto host = std::unique_ptr<unsigned char[]>{new (std::nothrow) unsigned char[allocation_size]};
  if (!host) return CUDA_ERROR_UNKNOWN;
  const uint64_t address = reinterpret_cast<uintptr_t>(host.get());
  const int32_t registration = managedRegisterRemote(address, allocation_size, hostMemory);
  if (registration != CUDA_SUCCESS) {
    return registration;
  }
  auto entry = std::unique_ptr<MirroredAllocation>{new (std::nothrow) MirroredAllocation{}};
  if (!entry) {
    (void)managedUnregisterRemote(address);
    return CUDA_ERROR_UNKNOWN;
  }
  entry->pointer = address;
  entry->size = allocation_size;
  entry->host = std::move(host);
  try {
    std::lock_guard lock{mirroredMutex};
    mirroredAllocations.push_back(std::move(entry));
  } catch (const std::exception &) {
    (void)managedUnregisterRemote(address);
    return CUDA_ERROR_UNKNOWN;
  }
  *pointer = address;
  return CUDA_SUCCESS;
}

int32_t managedAllocate(uint64_t *pointer, size_t size) { return mirroredAllocate(pointer, size, false); }

std::optional<int32_t> freeManaged(uint64_t pointer) {
  std::unique_ptr<MirroredAllocation> entry;
  int32_t status = CUDA_SUCCESS;
  {
    std::lock_guard lock{mirroredMutex};
    const auto cursor = std::find_if(mirroredAllocations.begin(), mirroredAllocations.end(),
                                     [=](const auto &allocation) { return allocation->pointer == pointer; });
    if (cursor == mirroredAllocations.end()) return std::nullopt;
    status = managedUnregisterRemote(pointer);
    if (status != CUDA_SUCCESS) return status;
    entry = std::move(*cursor);
    mirroredAllocations.erase(cursor);
  }
  untrackDevice(pointer);
  return status;
}

int32_t managedSynchronizeToDevice() {
  // A preceding device launch may have updated a managed allocation.  Bring
  // that state back to the host before the next launch copies host-visible
  // managed bytes into the emulator, otherwise stale host bytes overwrite the
  // device result between dependent kernels.
  int32_t status = managedDeviceDirty ? managedSynchronizeFromDevice() : CUDA_SUCCESS;
  if (status != CUDA_SUCCESS) return status;
  managedSyncToDeviceActive = true;
  {
    std::lock_guard lock{mirroredMutex};
    for (const auto &entry : mirroredAllocations) {
      if (status != CUDA_SUCCESS) break;
      status = cuMemcpyHtoD_v2(entry->pointer, entry->host.get(), entry->size);
    }
  }
  managedSyncToDeviceActive = false;
  return status;
}

bool copyManagedToHost(void *destination, uint64_t pointer, size_t size) {
  if (managedSyncFromDeviceActive) return false;
  bool handled = false;
  std::lock_guard lock{mirroredMutex};
  for (const auto &entry : mirroredAllocations) {
    const uint64_t offset = pointer - entry->pointer;
    if (pointer >= entry->pointer && offset <= entry->size && size <= entry->size - offset) {
      if (!managedDeviceDirty) {
        if (size != 0) memcpy(destination, entry->host.get() + offset, size);
        handled = true;
      }
      break;
    }
  }
  return handled;
}

bool copyManagedFromHost(uint64_t pointer, const void *source, size_t size) {
  if (managedSyncToDeviceActive) return false;
  bool handled = false;
  std::lock_guard lock{mirroredMutex};
  for (const auto &entry : mirroredAllocations) {
    const uint64_t offset = pointer - entry->pointer;
    if (pointer >= entry->pointer && offset <= entry->size && size <= entry->size - offset) {
      if (size != 0) memmove(entry->host.get() + offset, source, size);
      handled = true;
      break;
    }
  }
  return handled;
}

void markManagedDeviceDirty() {
  std::lock_guard lock{mirroredMutex};
  managedDeviceDirty = true;
}

int32_t managedSynchronizeFromDevice() {
  int32_t status = CUDA_SUCCESS;
  std::lock_guard lock{mirroredMutex};
  if (managedDeviceDirty) {
    managedSyncFromDeviceActive = true;
    for (const auto &entry : mirroredAllocations) {
      if (status != CUDA_SUCCESS) break;
      status = cuMemcpyDtoH_v2(entry->host.get(), entry->pointer, entry->size);
    }
    managedSyncFromDeviceActive = false;
    if (status == CUDA_SUCCESS) managedDeviceDirty = false;
  }
  return status;
}

} // namespace

extern "C" int32_t cuMemAllocManaged(uint64_t *pointer, size_t size, uint32_t flags) {
  (void)flags;
  return managedAllocate(pointer, size);
}

extern "C" int32_t cuMemAllocHost_v2(void **pointer, size_t size) {
  return mirroredAllocate(reinterpret_cast<uint64_t *>(pointer), size, 1);
}

extern "C" int32_t cuMemAllocHost(void **pointer, size_t size) { return cuMemAllocHost_v2(pointer, size); }

extern "C" int32_t cuMemFreeHost(void *pointer) {
  return freeManaged(reinterpret_cast<uintptr_t>(pointer)).value_or(CUDA_ERROR_INVALID_VALUE);
}

extern "C" int32_t cuMemHostAlloc(void **pointer, size_t size, uint32_t flags) {
  (void)flags;
  return cuMemAllocHost_v2(pointer, size);
}

extern "C" int32_t cuMemHostGetDevicePointer_v2(uint64_t *pointer, void *host_pointer, uint32_t flags) {
  (void)flags;
  if (!pointer || !host_pointer) return CUDA_ERROR_INVALID_VALUE;
  std::lock_guard lock{mirroredMutex};
  const auto entry = std::find_if(mirroredAllocations.begin(), mirroredAllocations.end(),
                                  [=](const auto &allocation) { return allocation->pointer == reinterpret_cast<uintptr_t>(host_pointer); });
  if (entry == mirroredAllocations.end()) return CUDA_ERROR_INVALID_VALUE;
  *pointer = (*entry)->pointer;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuMemHostGetFlags(uint32_t *flags, void *host_pointer) {
  if (!flags || !host_pointer) return CUDA_ERROR_INVALID_VALUE;
  uint64_t device_pointer = 0;
  const int32_t status = cuMemHostGetDevicePointer_v2(&device_pointer, host_pointer, 0);
  if (status == CUDA_SUCCESS) *flags = 0;
  return status;
}

namespace {

struct CudaArray {
  uint64_t device;
  size_t width;
  size_t height;
  size_t depth;
  size_t pitch;
  uint32_t format;
  uint32_t channels;
  uint32_t flags;
};

struct CudaArrayDescriptor {
  size_t width;
  size_t height;
  uint32_t format;
  uint32_t channels;
};

struct CudaArray3DDescriptor {
  size_t width;
  size_t height;
  size_t depth;
  uint32_t format;
  uint32_t channels;
  uint32_t flags;
};

struct CudaMemcpy2D {
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
};

struct CudaMemcpy3D {
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
};

struct CudaArrayDescriptorV1 {
  uint32_t width;
  uint32_t height;
  uint32_t format;
  uint32_t channels;
};

struct CudaArray3DDescriptorV1 {
  uint32_t width;
  uint32_t height;
  uint32_t depth;
  uint32_t format;
  uint32_t channels;
  uint32_t flags;
};

struct CudaMemcpy2DV1 {
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
};

struct CudaMemcpy3DV1 {
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
};

bool sizeAdd(size_t left, size_t right, size_t *result) {
  if (right > SIZE_MAX - left) return 0;
  *result = left + right;
  return 1;
}

bool sizeMul(size_t left, size_t right, size_t *result) {
  if (left != 0 && right > SIZE_MAX / left) return 0;
  *result = left * right;
  return 1;
}

size_t formatBytes(uint32_t format) {
  switch (format) {
    case 0x01:
    case 0x08: return 1;
    case 0x02:
    case 0x09:
    case 0x10: return 2;
    case 0x03:
    case 0x0a:
    case 0x20: return 4;
    default: return 0;
  }
}

bool arrayAllocation(size_t width, size_t height, size_t depth, size_t elementBytes, uint32_t channels, size_t *pitch, size_t *bytes) {
  size_t row_bytes = 0;
  size_t rows = 0;
  if (width == 0 || height == 0 || depth == 0 || channels == 0 || elementBytes == 0) return false;
  if (!sizeMul(width, elementBytes, &row_bytes) || !sizeMul(row_bytes, static_cast<size_t>(channels), &row_bytes)
      || !sizeMul(height, depth, &rows) || !sizeMul(row_bytes, rows, bytes)) {
    return false;
  }
  *pitch = row_bytes;
  return true;
}

int32_t arrayCreate(void **handle, size_t width, size_t height, size_t depth, uint32_t format, uint32_t channels, uint32_t flags) {
  if (!handle) return CUDA_ERROR_INVALID_VALUE;
  const size_t actual_depth = depth == 0 ? 1 : depth;
  const size_t element_bytes = formatBytes(format);
  size_t pitch = 0;
  size_t bytes = 0;
  if (!arrayAllocation(width, height, actual_depth, element_bytes, channels, &pitch, &bytes)) {
    return CUDA_ERROR_INVALID_VALUE;
  }
  auto array = std::unique_ptr<CudaArray>{new (std::nothrow) CudaArray{}};
  if (!array) return CUDA_ERROR_UNKNOWN;
  const int32_t status = cuMemAlloc_v2(&array->device, bytes);
  if (status != CUDA_SUCCESS) return status;
  array->width = width;
  array->height = height;
  array->depth = actual_depth;
  array->pitch = pitch;
  array->format = format;
  array->channels = channels;
  array->flags = flags;
  *handle = array.release();
  return CUDA_SUCCESS;
}

} // namespace

extern "C" int32_t cuArray3DCreate_v2(void **handle, const void *descriptor) {
  if (!descriptor) return CUDA_ERROR_INVALID_VALUE;
  const auto *value = static_cast<const CudaArray3DDescriptor *>(descriptor);
  return arrayCreate(handle, value->width, value->height, value->depth, value->format, value->channels, value->flags);
}

extern "C" int32_t cuArrayCreate_v2(void **handle, const void *descriptor) {
  if (!descriptor) return CUDA_ERROR_INVALID_VALUE;
  const auto *value = static_cast<const CudaArrayDescriptor *>(descriptor);
  return arrayCreate(handle, value->width, value->height, 1, value->format, value->channels, 0);
}

extern "C" int32_t cuArrayDestroy(void *handle) {
  if (!handle) return CUDA_ERROR_INVALID_VALUE;
  auto *array = static_cast<CudaArray *>(handle);
  const int32_t status = cuMemFree_v2(array->device);
  if (status == CUDA_SUCCESS) delete array;
  return status;
}

extern "C" int32_t cuArray3DGetDescriptor_v2(void *descriptor, void *handle) {
  if (!descriptor || !handle) return CUDA_ERROR_INVALID_VALUE;
  const auto *array = static_cast<const CudaArray *>(handle);
  auto *value = static_cast<CudaArray3DDescriptor *>(descriptor);
  value->width = array->width;
  value->height = array->height;
  value->depth = array->depth;
  value->format = array->format;
  value->channels = array->channels;
  value->flags = array->flags;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuArrayGetDescriptor_v2(void *descriptor, void *handle) {
  if (!descriptor || !handle) return CUDA_ERROR_INVALID_VALUE;
  const auto *array = static_cast<const CudaArray *>(handle);
  auto *value = static_cast<CudaArrayDescriptor *>(descriptor);
  value->width = array->width;
  value->height = array->height;
  value->format = array->format;
  value->channels = array->channels;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuArray3DCreate(void **handle, const void *descriptor) {
  if (!descriptor) return CUDA_ERROR_INVALID_VALUE;
  const auto *value = static_cast<const CudaArray3DDescriptorV1 *>(descriptor);
  return arrayCreate(handle, value->width, value->height, value->depth, value->format, value->channels, value->flags);
}

extern "C" int32_t cuArray3DGetDescriptor(void *descriptor, void *handle) {
  if (!descriptor || !handle) return CUDA_ERROR_INVALID_VALUE;
  const auto *array = static_cast<const CudaArray *>(handle);
  if (array->width > UINT32_MAX || array->height > UINT32_MAX || array->depth > UINT32_MAX) {
    return CUDA_ERROR_NOT_SUPPORTED;
  }
  auto *value = static_cast<CudaArray3DDescriptorV1 *>(descriptor);
  value->width = static_cast<uint32_t>(array->width);
  value->height = static_cast<uint32_t>(array->height);
  value->depth = static_cast<uint32_t>(array->depth);
  value->format = array->format;
  value->channels = array->channels;
  value->flags = array->flags;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuArrayCreate(void **handle, const void *descriptor) {
  if (!descriptor) return CUDA_ERROR_INVALID_VALUE;
  const auto *value = static_cast<const CudaArrayDescriptorV1 *>(descriptor);
  return arrayCreate(handle, value->width, value->height, 1, value->format, value->channels, 0);
}

extern "C" int32_t cuArrayGetDescriptor(void *descriptor, void *handle) {
  if (!descriptor || !handle) return CUDA_ERROR_INVALID_VALUE;
  const auto *array = static_cast<const CudaArray *>(handle);
  if (array->width > UINT32_MAX || array->height > UINT32_MAX) return CUDA_ERROR_NOT_SUPPORTED;
  auto *value = static_cast<CudaArrayDescriptorV1 *>(descriptor);
  value->width = static_cast<uint32_t>(array->width);
  value->height = static_cast<uint32_t>(array->height);
  value->format = array->format;
  value->channels = array->channels;
  return CUDA_SUCCESS;
}

namespace {

bool copyOffset(size_t y, size_t pitch, size_t x, size_t *offset) { return sizeMul(y, pitch, offset) && sizeAdd(*offset, x, offset); }

bool arrayBounds(const CudaArray *array, size_t x, size_t y, size_t width, size_t height, size_t *base) {
  size_t end = 0;
  if (!sizeAdd(x, width, &end) || end > array->pitch || !sizeAdd(y, height, &end) || end > array->height) return 0;
  return copyOffset(y, array->pitch, x, base);
}

bool linearBounds(size_t x, size_t y, size_t pitch, size_t width, size_t height, size_t *base) {
  size_t end = 0;
  if (pitch < x || !sizeAdd(x, width, &end) || end > pitch) return 0;
  if (!copyOffset(y, pitch, x, &end)) return 0;
  if (height > 0) {
    if (!sizeAdd(y, height - 1, &end) || !sizeMul(end, pitch, &end) || !sizeAdd(end, x, &end) || !sizeAdd(end, width, &end)) return 0;
  }
  return copyOffset(y, pitch, x, base);
}

int32_t copyRows(const CudaMemcpy2D *copy) {
  if (!copy || copy->width_in_bytes == 0 || copy->height == 0) return CUDA_SUCCESS;
  if ((copy->src_memory_type != CU_MEMORYTYPE_HOST && copy->src_memory_type != CU_MEMORYTYPE_DEVICE
       && copy->src_memory_type != CU_MEMORYTYPE_ARRAY)
      || (copy->dst_memory_type != CU_MEMORYTYPE_HOST && copy->dst_memory_type != CU_MEMORYTYPE_DEVICE
          && copy->dst_memory_type != CU_MEMORYTYPE_ARRAY)) {
    return CUDA_ERROR_INVALID_VALUE;
  }

  const CudaArray *src_array = static_cast<const CudaArray *>(copy->src_array);
  const CudaArray *dst_array = static_cast<const CudaArray *>(copy->dst_array);
  const size_t src_row_pitch = copy->src_memory_type == CU_MEMORYTYPE_ARRAY ? (!src_array ? 0 : src_array->pitch) : copy->src_pitch;
  const size_t dst_row_pitch = copy->dst_memory_type == CU_MEMORYTYPE_ARRAY ? (!dst_array ? 0 : dst_array->pitch) : copy->dst_pitch;
  size_t src_base = 0;
  size_t dst_base = 0;
  if (copy->src_memory_type == CU_MEMORYTYPE_HOST || copy->src_memory_type == CU_MEMORYTYPE_DEVICE) {
    if (!linearBounds(copy->src_x_in_bytes, copy->src_y, copy->src_pitch, copy->width_in_bytes, copy->height, &src_base)) {
      return CUDA_ERROR_INVALID_VALUE;
    }
  } else if (!src_array || !arrayBounds(src_array, copy->src_x_in_bytes, copy->src_y, copy->width_in_bytes, copy->height, &src_base)) {
    return CUDA_ERROR_INVALID_VALUE;
  }
  if (copy->dst_memory_type == CU_MEMORYTYPE_HOST || copy->dst_memory_type == CU_MEMORYTYPE_DEVICE) {
    if (!linearBounds(copy->dst_x_in_bytes, copy->dst_y, copy->dst_pitch, copy->width_in_bytes, copy->height, &dst_base)) {
      return CUDA_ERROR_INVALID_VALUE;
    }
  } else if (!dst_array || !arrayBounds(dst_array, copy->dst_x_in_bytes, copy->dst_y, copy->width_in_bytes, copy->height, &dst_base)) {
    return CUDA_ERROR_INVALID_VALUE;
  }

  auto row = std::unique_ptr<unsigned char[]>{new (std::nothrow) unsigned char[copy->width_in_bytes]};
  if (!row) return CUDA_ERROR_UNKNOWN;
  int32_t status = CUDA_SUCCESS;
  for (size_t y = 0; y < copy->height && status == CUDA_SUCCESS; ++y) {
    size_t src_offset = 0;
    size_t dst_offset = 0;
    if (!sizeMul(y, src_row_pitch, &src_offset) || !sizeAdd(src_offset, src_base, &src_offset) || !sizeMul(y, dst_row_pitch, &dst_offset)
        || !sizeAdd(dst_offset, dst_base, &dst_offset)) {
      status = CUDA_ERROR_INVALID_VALUE;
      break;
    }
    if (copy->src_memory_type == CU_MEMORYTYPE_HOST) {
      if (!copy->src_host) {
        status = CUDA_ERROR_INVALID_VALUE;
        break;
      }
      memcpy(row.get(), static_cast<const unsigned char *>(copy->src_host) + src_offset, copy->width_in_bytes);
    } else {
      const uint64_t source = copy->src_memory_type == CU_MEMORYTYPE_ARRAY ? src_array->device + static_cast<uint64_t>(src_offset)
                                                                           : copy->src_device + static_cast<uint64_t>(src_offset);
      status = cuMemcpyDtoH_v2(row.get(), source, copy->width_in_bytes);
      if (status != CUDA_SUCCESS) break;
    }
    if (copy->dst_memory_type == CU_MEMORYTYPE_HOST) {
      if (!copy->dst_host) {
        status = CUDA_ERROR_INVALID_VALUE;
        break;
      }
      memcpy(static_cast<unsigned char *>(copy->dst_host) + dst_offset, row.get(), copy->width_in_bytes);
    } else {
      const uint64_t destination = copy->dst_memory_type == CU_MEMORYTYPE_ARRAY ? dst_array->device + static_cast<uint64_t>(dst_offset)
                                                                                : copy->dst_device + static_cast<uint64_t>(dst_offset);
      status = cuMemcpyHtoD_v2(destination, row.get(), copy->width_in_bytes);
    }
  }
  return status;
}

} // namespace

extern "C" int32_t cuMemcpy2D_v2(const void *copy) { return copyRows(static_cast<const CudaMemcpy2D *>(copy)); }

extern "C" int32_t cuMemcpy2DUnaligned_v2(const void *copy) { return copyRows(static_cast<const CudaMemcpy2D *>(copy)); }

extern "C" int32_t cuMemcpy2DAsync_v2(const void *copy, void *stream) {
  (void)stream;
  return copyRows(static_cast<const CudaMemcpy2D *>(copy));
}

namespace {

CudaMemcpy2D expandCopy2d(const CudaMemcpy2DV1 *copy) {
  return (CudaMemcpy2D){copy->src_x_in_bytes, copy->src_y,           copy->src_memory_type, copy->src_host,
                        copy->src_device,     copy->src_array,       copy->src_pitch,       copy->dst_x_in_bytes,
                        copy->dst_y,          copy->dst_memory_type, copy->dst_host,        copy->dst_device,
                        copy->dst_array,      copy->dst_pitch,       copy->width_in_bytes,  copy->height};
}

} // namespace

extern "C" int32_t cuMemcpy2D(const void *copy) {
  if (!copy) return CUDA_ERROR_INVALID_VALUE;
  const CudaMemcpy2D expanded = expandCopy2d(static_cast<const CudaMemcpy2DV1 *>(copy));
  return copyRows(&expanded);
}

extern "C" int32_t cuMemcpy2DUnaligned(const void *copy) { return cuMemcpy2D(copy); }

extern "C" int32_t cuMemcpy2DAsync(const void *copy, void *stream) {
  (void)stream;
  return cuMemcpy2D(copy);
}

namespace {

bool volumeOffset(size_t z, size_t y, size_t slicePitch, size_t rowPitch, size_t x, size_t *offset) {
  return sizeMul(z, slicePitch, offset) && sizeMul(y, rowPitch, &slicePitch) && sizeAdd(*offset, slicePitch, offset)
         && sizeAdd(*offset, x, offset);
}

bool volumeBounds(size_t x, size_t y, size_t z, size_t rowPitch, size_t slicePitch, size_t width, size_t height, size_t depth,
                  size_t *base) {
  size_t end = 0;
  if (rowPitch < x || !sizeAdd(x, width, &end) || end > rowPitch || !volumeOffset(z, y, slicePitch, rowPitch, x, &end)) return false;
  if (depth > 0
      && (!sizeAdd(z, depth - 1, &end) || !sizeMul(end, slicePitch, &end) || !sizeAdd(end, y, &end) || !sizeMul(end, rowPitch, &end)
          || !sizeAdd(end, x, &end) || !sizeAdd(end, width, &end)))
    return 0;
  if (height > 0
      && (!sizeAdd(y, height - 1, &end) || !sizeMul(end, rowPitch, &end) || !sizeAdd(end, x, &end) || !sizeAdd(end, width, &end)))
    return 0;
  return volumeOffset(z, y, slicePitch, rowPitch, x, base);
}

bool volumeArrayBounds(const CudaArray *array, size_t x, size_t y, size_t z, size_t width, size_t height, size_t depth, size_t *base) {
  size_t end = 0;
  size_t slicePitch = 0;
  return sizeAdd(x, width, &end) && end <= array->pitch && sizeAdd(y, height, &end) && end <= array->height && sizeAdd(z, depth, &end)
         && end <= array->depth && sizeMul(array->pitch, array->height, &slicePitch)
         && volumeOffset(z, y, slicePitch, array->pitch, x, base);
}

int32_t copyVolume(const CudaMemcpy3D *copy) {
  if (!copy || copy->width_in_bytes == 0 || copy->height == 0 || copy->depth == 0) {
    return CUDA_SUCCESS;
  }
  if (copy->src_reserved || copy->dst_reserved
      || (copy->src_memory_type != CU_MEMORYTYPE_HOST && copy->src_memory_type != CU_MEMORYTYPE_DEVICE
          && copy->src_memory_type != CU_MEMORYTYPE_ARRAY)
      || (copy->dst_memory_type != CU_MEMORYTYPE_HOST && copy->dst_memory_type != CU_MEMORYTYPE_DEVICE
          && copy->dst_memory_type != CU_MEMORYTYPE_ARRAY)) {
    return CUDA_ERROR_INVALID_VALUE;
  }
  const CudaArray *src_array = static_cast<const CudaArray *>(copy->src_array);
  const CudaArray *dst_array = static_cast<const CudaArray *>(copy->dst_array);
  const size_t src_row_pitch = copy->src_memory_type == CU_MEMORYTYPE_ARRAY ? (!src_array ? 0 : src_array->pitch) : copy->src_pitch;
  const size_t dst_row_pitch = copy->dst_memory_type == CU_MEMORYTYPE_ARRAY ? (!dst_array ? 0 : dst_array->pitch) : copy->dst_pitch;
  const size_t src_height = copy->src_height == 0 ? copy->height : copy->src_height;
  const size_t dst_height = copy->dst_height == 0 ? copy->height : copy->dst_height;
  size_t src_slice_pitch = 0;
  size_t dst_slice_pitch = 0;
  size_t src_base = 0;
  size_t dst_base = 0;
  if (copy->src_memory_type == CU_MEMORYTYPE_ARRAY) {
    if (!src_array
        || !volumeArrayBounds(src_array, copy->src_x_in_bytes, copy->src_y, copy->src_z, copy->width_in_bytes, copy->height, copy->depth,
                              &src_base)
        || !sizeMul(src_array->pitch, src_array->height, &src_slice_pitch))
      return CUDA_ERROR_INVALID_VALUE;
  } else if (!sizeMul(src_row_pitch, src_height, &src_slice_pitch)
             || !volumeBounds(copy->src_x_in_bytes, copy->src_y, copy->src_z, src_row_pitch, src_slice_pitch, copy->width_in_bytes,
                              copy->height, copy->depth, &src_base))
    return CUDA_ERROR_INVALID_VALUE;
  if (copy->dst_memory_type == CU_MEMORYTYPE_ARRAY) {
    if (!dst_array
        || !volumeArrayBounds(dst_array, copy->dst_x_in_bytes, copy->dst_y, copy->dst_z, copy->width_in_bytes, copy->height, copy->depth,
                              &dst_base)
        || !sizeMul(dst_array->pitch, dst_array->height, &dst_slice_pitch))
      return CUDA_ERROR_INVALID_VALUE;
  } else if (!sizeMul(dst_row_pitch, dst_height, &dst_slice_pitch)
             || !volumeBounds(copy->dst_x_in_bytes, copy->dst_y, copy->dst_z, dst_row_pitch, dst_slice_pitch, copy->width_in_bytes,
                              copy->height, copy->depth, &dst_base))
    return CUDA_ERROR_INVALID_VALUE;

  auto row = std::unique_ptr<unsigned char[]>{new (std::nothrow) unsigned char[copy->width_in_bytes]};
  if (!row) return CUDA_ERROR_UNKNOWN;
  int32_t status = CUDA_SUCCESS;
  for (size_t z = 0; z < copy->depth && status == CUDA_SUCCESS; ++z) {
    for (size_t y = 0; y < copy->height && status == CUDA_SUCCESS; ++y) {
      size_t src_offset = 0;
      size_t dst_offset = 0;
      if (!volumeOffset(z, y, src_slice_pitch, src_row_pitch, src_base, &src_offset)
          || !volumeOffset(z, y, dst_slice_pitch, dst_row_pitch, dst_base, &dst_offset)) {
        status = CUDA_ERROR_INVALID_VALUE;
        break;
      }
      if (copy->src_memory_type == CU_MEMORYTYPE_HOST) {
        if (!copy->src_host) {
          status = CUDA_ERROR_INVALID_VALUE;
          break;
        }
        memcpy(row.get(), static_cast<const unsigned char *>(copy->src_host) + src_offset, copy->width_in_bytes);
      } else {
        const uint64_t source = copy->src_memory_type == CU_MEMORYTYPE_ARRAY ? src_array->device + static_cast<uint64_t>(src_offset)
                                                                             : copy->src_device + static_cast<uint64_t>(src_offset);
        status = cuMemcpyDtoH_v2(row.get(), source, copy->width_in_bytes);
        if (status != CUDA_SUCCESS) break;
      }
      if (copy->dst_memory_type == CU_MEMORYTYPE_HOST) {
        if (!copy->dst_host) {
          status = CUDA_ERROR_INVALID_VALUE;
          break;
        }
        memcpy(static_cast<unsigned char *>(copy->dst_host) + dst_offset, row.get(), copy->width_in_bytes);
      } else {
        const uint64_t destination = copy->dst_memory_type == CU_MEMORYTYPE_ARRAY ? dst_array->device + static_cast<uint64_t>(dst_offset)
                                                                                  : copy->dst_device + static_cast<uint64_t>(dst_offset);
        status = cuMemcpyHtoD_v2(destination, row.get(), copy->width_in_bytes);
      }
    }
  }
  return status;
}

} // namespace

extern "C" int32_t cuMemcpy3D_v2(const void *copy) { return copyVolume(static_cast<const CudaMemcpy3D *>(copy)); }

extern "C" int32_t cuMemcpy3DAsync_v2(const void *copy, void *stream) {
  (void)stream;
  return copyVolume(static_cast<const CudaMemcpy3D *>(copy));
}

namespace {

CudaMemcpy3D expandCopy3d(const CudaMemcpy3DV1 *copy) {
  return (CudaMemcpy3D){copy->src_x_in_bytes,  copy->src_y,          copy->src_z,          copy->src_lod,      copy->src_memory_type,
                        copy->src_host,        copy->src_device,     copy->src_array,      copy->src_reserved, copy->src_pitch,
                        copy->src_height,      copy->dst_x_in_bytes, copy->dst_y,          copy->dst_z,        copy->dst_lod,
                        copy->dst_memory_type, copy->dst_host,       copy->dst_device,     copy->dst_array,    copy->dst_reserved,
                        copy->dst_pitch,       copy->dst_height,     copy->width_in_bytes, copy->height,       copy->depth};
}

} // namespace

extern "C" int32_t cuMemcpy3D(const void *copy) {
  if (!copy) return CUDA_ERROR_INVALID_VALUE;
  const CudaMemcpy3D expanded = expandCopy3d(static_cast<const CudaMemcpy3DV1 *>(copy));
  return copyVolume(&expanded);
}

extern "C" int32_t cuMemcpy3DAsync(const void *copy, void *stream) {
  (void)stream;
  return cuMemcpy3D(copy);
}

extern "C" int32_t cuKernelGetFunction(void **function, void *kernel) {
  if (!function || !kernel) return CUDA_ERROR_INVALID_VALUE;
  *function = kernel;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuLibraryGetGlobal(uint64_t *pointer, size_t *bytes, void *library, const char *name) {
  return cuModuleGetGlobal_v2(pointer, bytes, library, name);
}

extern "C" int32_t cuModuleGetGlobal(uint32_t *pointer, uint32_t *bytes, void *module, const char *name) {
  if (!pointer) return CUDA_ERROR_INVALID_VALUE;
  uint64_t address = 0;
  size_t size = 0;
  const int32_t status = cuModuleGetGlobal_v2(&address, &size, module, name);
  if (status != CUDA_SUCCESS) return status;
  if (address > UINT32_MAX || size > UINT32_MAX) return CUDA_ERROR_NOT_SUPPORTED;
  *pointer = static_cast<uint32_t>(address);
  if (bytes) *bytes = static_cast<uint32_t>(size);
  return CUDA_SUCCESS;
}

extern "C" int32_t cuFuncGetAttribute(int32_t *value, int32_t attribute, void *function) {
  if (!value || !function) return CUDA_ERROR_INVALID_VALUE;
  switch (attribute) {
    case CU_FUNC_ATTRIBUTE_MAX_THREADS_PER_BLOCK: *value = 1024; return CUDA_SUCCESS;
    case CU_FUNC_ATTRIBUTE_PTX_VERSION:
    case CU_FUNC_ATTRIBUTE_BINARY_VERSION: {
      const auto version = registeredPtxVersion(function);
      if (!version) return CUDA_ERROR_NOT_FOUND;
      *value = *version;
      return CUDA_SUCCESS;
    }
    case CU_FUNC_ATTRIBUTE_MAX_DYNAMIC_SHARED_SIZE_BYTES: *value = 49152; return CUDA_SUCCESS;
    case CU_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES:
    case CU_FUNC_ATTRIBUTE_CONST_SIZE_BYTES:
    case CU_FUNC_ATTRIBUTE_LOCAL_SIZE_BYTES:
    case CU_FUNC_ATTRIBUTE_NUM_REGS:
    case CU_FUNC_ATTRIBUTE_CACHE_MODE_CA:
    case CU_FUNC_ATTRIBUTE_PREFERRED_SHARED_MEMORY_CARVEOUT:
    case CU_FUNC_ATTRIBUTE_CLUSTER_SIZE_MUST_BE_SET:
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH:
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_HEIGHT:
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_DEPTH:
    case CU_FUNC_ATTRIBUTE_NON_PORTABLE_CLUSTER_SIZE_ALLOWED:
    case CU_FUNC_ATTRIBUTE_CLUSTER_SCHEDULING_POLICY_PREFERENCE:
    case CU_FUNC_ATTRIBUTE_DEVICE_NODE_UPDATE_SUPPORTED: *value = 0; return CUDA_SUCCESS;
    default: return CUDA_ERROR_INVALID_VALUE;
  }
}

extern "C" int32_t cuKernelGetAttribute(int32_t *value, int32_t attribute, void *kernel, int32_t device) {
  (void)device;
  return cuFuncGetAttribute(value, attribute, kernel);
}

/* Kernel tuning attributes are advisory for the interpreter.  Accept the
 * setters so vendor libraries can configure their generated kernels without
 * turning an otherwise executable launch into NOT_SUPPORTED. */
extern "C" int32_t cuFuncSetAttribute(void *function, int32_t attribute, int32_t value) {
  (void)attribute;
  (void)value;
  return !function ? CUDA_ERROR_INVALID_VALUE : CUDA_SUCCESS;
}

extern "C" int32_t cuFuncSetCacheConfig(void *function, int32_t config) {
  (void)config;
  return !function ? CUDA_ERROR_INVALID_VALUE : CUDA_SUCCESS;
}

extern "C" int32_t cuFuncSetSharedMemConfig(void *function, int32_t config) {
  (void)config;
  return !function ? CUDA_ERROR_INVALID_VALUE : CUDA_SUCCESS;
}

/*
 * The CUDA runtime uses this driver entry while CUB is selecting a reduction
 * plan.  There is no useful device occupancy to query for the JVM executor,
 * but a conservative synthetic value keeps that host-side planning path
 * usable without putting planner policy into the generated RPC client.
 */
namespace {

int32_t occupancyMaxBlocks(int32_t *blocks, int32_t blockSize) {
  if (!blocks || blockSize <= 0 || blockSize > 1024) return CUDA_ERROR_INVALID_VALUE;
  *blocks = 1024 / blockSize;
  if (*blocks == 0) *blocks = 1;
  return CUDA_SUCCESS;
}

} // namespace

extern "C" int32_t cuOccupancyMaxActiveBlocksPerMultiprocessor(int32_t *blocks, void *function, int32_t block_size,
                                                               size_t dynamic_shared_memory) {
  (void)function;
  (void)dynamic_shared_memory;
  return occupancyMaxBlocks(blocks, block_size);
}

extern "C" int32_t cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(int32_t *blocks, void *function, int32_t block_size,
                                                                        size_t dynamic_shared_memory, uint32_t flags) {
  (void)function;
  (void)dynamic_shared_memory;
  (void)flags;
  return occupancyMaxBlocks(blocks, block_size);
}

#if defined(_WIN32)
  #define GEMSTONE_CUDA_API __stdcall
#else
  #define GEMSTONE_CUDA_API
#endif

extern "C" int32_t cuInit(uint32_t flags) noexcept;
extern "C" int32_t cuDriverGetVersion(int32_t *version) noexcept;
extern "C" int32_t cuDeviceGetCount(int32_t *count) noexcept;
extern "C" int32_t cuDeviceGet(int32_t *device, int32_t ordinal) noexcept;
extern "C" int32_t cuCtxCreate_v2(void **context, uint32_t flags, int32_t device) noexcept;
extern "C" int32_t cuCtxDestroy_v2(void *context) noexcept;
extern "C" int32_t cuCtxSynchronize() noexcept;
extern "C" int32_t cuLaunchHostFunc(void *stream, HostCallback callback, void *user_data) noexcept;
extern "C" int32_t cuStreamAddCallback(void *stream, StreamCallback callback, void *user_data, uint32_t flags) noexcept;
extern "C" int32_t cuMemcpyDtoD_v2(uint64_t destination, uint64_t source, size_t size) noexcept;
extern "C" int32_t cuMemsetD8_v2(uint64_t destination, uint8_t value, size_t count) noexcept;
extern "C" int32_t cuModuleLoadData(void **module, const void *image) noexcept;
extern "C" int32_t cuModuleLoad(void **module, const char *filename);
extern "C" int32_t cuModuleGetLoadingMode(int32_t *mode);

namespace {

int32_t moduleGetTexRef(void **reference, void *module, const char *name) {
  if (!reference || !module || !name) return CUDA_ERROR_INVALID_VALUE;
  *reference = module;
  return CUDA_SUCCESS;
}

int32_t moduleGetSurfRef(void **reference, void *module, const char *name) {
  if (!reference || !module || !name) return CUDA_ERROR_INVALID_VALUE;
  *reference = module;
  return CUDA_SUCCESS;
}

} // namespace
extern "C" int32_t cuModuleGetFunction(void **function, void *module, const char *name) noexcept;
extern "C" int32_t cuModuleUnload(void *module) noexcept;
extern "C" int32_t cuCtxCreate_v4(void **context, void *parameters, uint32_t flags, int32_t device) noexcept;
extern "C" int32_t cuLaunchKernel(void *function, uint32_t grid_x, uint32_t grid_y, uint32_t grid_z, uint32_t block_x, uint32_t block_y,
                                  uint32_t block_z, uint32_t shared_memory_bytes, void *stream, void **kernel_params,
                                  void **extra) noexcept;

/* The CUDA driver exposes the per-thread-default-stream spelling through
 * cuGetProcAddress even though it has the same ABI as cuLaunchKernel.  The
 * static CUDA runtime uses that spelling for host-launched kernels. */
extern "C" int32_t cuLaunchKernel_ptsz(void *function, uint32_t grid_x, uint32_t grid_y, uint32_t grid_z, uint32_t block_x,
                                       uint32_t block_y, uint32_t block_z, uint32_t shared_memory_bytes, void *stream, void **kernel_params,
                                       void **extra) {
  return cuLaunchKernel(function, grid_x, grid_y, grid_z, block_x, block_y, block_z, shared_memory_bytes, stream, kernel_params, extra);
}

/* CUDA 11.6 added an extensible launch entry point.  The runtime only uses
 * the fixed geometry fields for ordinary kernels; attributes are optional
 * hints that have no equivalent in the emulator.  Keep this declaration
 * private so the compatibility object does not depend on a particular CUDA
 * header revision; the generated implementation check supplies the pinned vendor
 * declaration at the ABI-validation boundary. */
struct CudaLaunchConfig {
  uint32_t grid_x;
  uint32_t grid_y;
  uint32_t grid_z;
  uint32_t block_x;
  uint32_t block_y;
  uint32_t block_z;
  uint32_t shared_memory_bytes;
  void *stream;
  void *attributes;
  uint32_t attribute_count;
};

extern "C" int32_t cuLaunchKernelEx(const CudaLaunchConfig *config, void *function, void **kernel_params, void **extra) {
  if (!config) return CUDA_ERROR_INVALID_VALUE;
  return cuLaunchKernel(function, config->grid_x, config->grid_y, config->grid_z, config->block_x, config->block_y, config->block_z,
                        config->shared_memory_bytes, config->stream, kernel_params, extra);
}

namespace {

struct LinkState {
  std::vector<char> image;
  size_t size = 0;
};

int32_t loadModuleImage(void **module, const void *image) {
  if (!module || !image) return CUDA_ERROR_INVALID_VALUE;
  const char *ptx = findUncompressedPtx(image);
  return cuModuleLoadData(module, !ptx ? image : ptx);
}

} // namespace

extern "C" int32_t cuModuleLoadDataEx(void **module, const void *image, uint32_t option_count, int32_t *options, void **option_values) {
  (void)option_count;
  (void)options;
  (void)option_values;
  return loadModuleImage(module, image);
}

extern "C" int32_t cuModuleLoadFatBinary(void **module, const void *fat_binary) { return loadModuleImage(module, fat_binary); }

namespace {

bool linkAppend(LinkState *state, const char *source, size_t sourceSize) {
  const size_t separator = state->size == 0 ? 0 : 1;
  if (state->size > MaximumImageSize - 1 - separator || sourceSize > MaximumImageSize - 1 - separator - state->size) return false;
  const size_t nextSize = state->size + separator + sourceSize;
  if (!gemstone::tryResize(state->image, nextSize + 1)) return false;
  if (separator != 0) state->image[state->size] = '\n';
  memcpy(state->image.data() + state->size + separator, source, sourceSize);
  state->image[nextSize] = '\0';
  state->size = nextSize;
  return true;
}

} // namespace

extern "C" int32_t cuLinkCreate_v2(uint32_t option_count, int32_t *options, void **option_values, void **state_out) {
  (void)option_count;
  (void)options;
  (void)option_values;
  if (!state_out) return CUDA_ERROR_INVALID_VALUE;
  auto state = std::unique_ptr<LinkState>{new (std::nothrow) LinkState};
  if (!state) return CUDA_ERROR_UNKNOWN;
  *state_out = state.release();
  return CUDA_SUCCESS;
}

extern "C" int32_t cuLinkAddData_v2(void *state_pointer, int32_t input_type, void *data, size_t size, const char *name,
                                    uint32_t option_count, int32_t *options, void **option_values) {
  (void)input_type;
  (void)name;
  (void)option_count;
  (void)options;
  (void)option_values;
  if (!state_pointer || !data) return CUDA_ERROR_INVALID_VALUE;
  auto *state = static_cast<LinkState *>(state_pointer);
  const char *ptx = findUncompressedPtx(data);
  if (ptx) {
    if (!linkAppend(state, ptx, strlen(ptx))) return CUDA_ERROR_UNKNOWN;
    return CUDA_SUCCESS;
  }
  if (size == 0 || size > MaximumImageSize) return CUDA_ERROR_NOT_SUPPORTED;
  if (!linkAppend(state, static_cast<const char *>(data), size)) return CUDA_ERROR_UNKNOWN;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuLinkComplete(void *state_pointer, void **image, size_t *size) {
  if (!state_pointer || !image || !size) return CUDA_ERROR_INVALID_VALUE;
  auto *state = static_cast<LinkState *>(state_pointer);
  if (state->size == 0) return CUDA_ERROR_INVALID_VALUE;
  *image = state->image.data();
  *size = state->size;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuLinkDestroy(void *state_pointer) {
  if (!state_pointer) return CUDA_ERROR_INVALID_VALUE;
  auto *state = static_cast<LinkState *>(state_pointer);
  delete state;
  return CUDA_SUCCESS;
}

namespace {

int32_t libraryLoadData(void **library, const void *code, int32_t *jit_options, void **jit_option_values, uint32_t num_jit_options,
                        int32_t *library_options, void **library_option_values, uint32_t num_library_options) {
  (void)jit_options;
  (void)jit_option_values;
  (void)num_jit_options;
  (void)library_options;
  (void)library_option_values;
  (void)num_library_options;
  const char *ptx = findUncompressedPtx(code);
  return cuModuleLoadData(library, !ptx ? code : ptx);
}

int32_t libraryGetKernel(void **kernel, void *library, const char *name) { return cuModuleGetFunction(kernel, library, name); }

int32_t libraryGetModule(void **module, void *library) {
  if (!module || !library) return CUDA_ERROR_INVALID_VALUE;
  *module = library;
  return CUDA_SUCCESS;
}

int32_t libraryUnload(void *library) { return cuModuleUnload(library); }

} // namespace

/* cuBLAS probes every internal library for managed globals while it builds
 * its dispatch catalogue.  A missing global is a normal lookup miss, not an
 * unsupported driver operation; returning NOT_SUPPORTED (801) makes cuBLAS
 * abort its catalogue build with CUDA_ERROR_SYSTEM_DRIVER_MISMATCH. */
extern "C" int32_t cuLibraryGetManaged(uint64_t *device_pointer, size_t *bytes, void *library, const char *name) {
  (void)library;
  (void)name;
  if (!device_pointer || !bytes) return CUDA_ERROR_INVALID_VALUE;
  *device_pointer = 0;
  *bytes = 0;
  return CUDA_ERROR_NOT_FOUND;
}

extern "C" int32_t cudaGetDeviceCount(int32_t *count) { return cuDeviceGetCount(count); }

namespace {

int32_t devicePrimaryCtxRetain(void **context, int32_t device);
int32_t ctxSetCurrent(void *context);
int32_t ensureRuntimeContext() {
  if (currentContext) return CUDA_SUCCESS;
  void *context = nullptr;
  const int32_t status = devicePrimaryCtxRetain(&context, 0);
  return status == CUDA_SUCCESS ? ctxSetCurrent(context) : status;
}

thread_local int32_t lastError = CUDA_SUCCESS;

} // namespace

extern "C" int32_t cudaMalloc(void **pointer, size_t size) {
  if (!pointer) return CUDA_ERROR_INVALID_VALUE;
  const int32_t initialized = ensureRuntimeContext();
  if (initialized != CUDA_SUCCESS) return initialized;
  uint64_t device_pointer = 0;
  const int32_t status = cuMemAlloc_v2(&device_pointer, size);
  if (status == CUDA_SUCCESS) *pointer = reinterpret_cast<void *>(device_pointer);
  return status;
}

extern "C" int32_t cudaFree(void *pointer) {
  const auto managed = freeManaged(reinterpret_cast<uintptr_t>(pointer));
  return managed ? *managed : cuMemFree_v2(reinterpret_cast<uintptr_t>(pointer));
}

extern "C" int32_t cudaMemcpy(void *destination, const void *source, size_t size, int32_t kind) {
  switch (kind) {
    case 1: return cuMemcpyHtoD_v2(reinterpret_cast<uintptr_t>(destination), source, size);
    case 2: return cuMemcpyDtoH_v2(destination, reinterpret_cast<uintptr_t>(source), size);
    case 3: return cuMemcpyDtoD_v2(reinterpret_cast<uintptr_t>(destination), reinterpret_cast<uintptr_t>(source), size);
    default: return CUDA_ERROR_INVALID_VALUE;
  }
}

extern "C" const char *cudaGetErrorString(int32_t status) {
  switch (status) {
    case CUDA_SUCCESS: return "success";
    case CUDA_ERROR_INVALID_VALUE: return "invalid value";
    case CUDA_ERROR_NOT_SUPPORTED: return "not supported";
    case CUDA_ERROR_NOT_INITIALIZED: return "initialization error";
    default: return "Gemstone CUDA error";
  }
}

extern "C" const char *cudaGetErrorName(int32_t status) {
  switch (status) {
    case CUDA_SUCCESS: return "cudaSuccess";
    case CUDA_ERROR_INVALID_VALUE: return "cudaErrorInvalidValue";
    case CUDA_ERROR_NOT_SUPPORTED: return "cudaErrorNotSupported";
    case CUDA_ERROR_NOT_INITIALIZED: return "cudaErrorInitializationError";
    default: return "cudaErrorUnknown";
  }
}

extern "C" int32_t cudaPeekAtLastError() { return lastError; }

extern "C" int32_t cudaGetLastError() {
  const int32_t status = lastError;
  lastError = CUDA_SUCCESS;
  return status;
}

extern "C" int32_t cudaGetDevice(int32_t *device) {
  if (!device) return CUDA_ERROR_INVALID_VALUE;
  *device = 0;
  return CUDA_SUCCESS;
}

extern "C" int32_t cudaSetDevice(int32_t device) { return device == 0 ? CUDA_SUCCESS : CUDA_ERROR_INVALID_VALUE; }

extern "C" int32_t cudaDriverGetVersion(int32_t *version) { return cuDriverGetVersion(version); }

extern "C" int32_t cudaRuntimeGetVersion(int32_t *version) {
  if (!version) return CUDA_ERROR_INVALID_VALUE;
  *version = 13030;
  return CUDA_SUCCESS;
}

extern "C" int32_t cudaDeviceSynchronize() { return cuCtxSynchronize(); }

extern "C" int32_t cudaStreamSynchronize(void *stream) {
  (void)stream;
  return cuCtxSynchronize();
}

extern "C" int32_t cudaDeviceGetAttribute(int32_t *value, int32_t attribute, int32_t device) {
  if (!value || device != 0) return CUDA_ERROR_INVALID_VALUE;
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
    case 75: {
      const auto capability = computeCapability();
      if (!capability) return CUDA_ERROR_NOT_INITIALIZED;
      *value = capability->first;
      break;
    }
    case 76: {
      const auto capability = computeCapability();
      if (!capability) return CUDA_ERROR_NOT_INITIALIZED;
      *value = capability->second;
      break;
    }
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
    case 88: *value = systemPagingEnabled() ? 1 : 0; break;
    case 89: *value = 1; break;
    case 90: *value = 1; break;
    case 91: *value = 1; break;
    case 95: *value = 1; break;
    case 96: *value = 0; break;
    case 97: *value = 98304; break;
    case 100: *value = 0; break;
    case 101: *value = 0; break;
    case 108: *value = 4194304; break;
    case 109: *value = 4096; break;
    case 115: *value = 1; break;
    case 106: *value = 32; break;
    default: *value = 0; break;
  }
  return CUDA_SUCCESS;
}

namespace {

struct DevicePropertiesPrefix {
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
};

} // namespace

extern "C" int32_t cudaGetDeviceProperties(void *properties, int32_t device) {
  if (!properties || device != 0) return CUDA_ERROR_INVALID_VALUE;
  auto *value = static_cast<DevicePropertiesPrefix *>(properties);
  *value = {};
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
  const auto capability = computeCapability();
  if (!capability) return CUDA_ERROR_NOT_INITIALIZED;
  value->major = capability->first;
  value->minor = capability->second;
  value->texture_alignment = 256;
  value->texture_pitch_alignment = 32;
  value->multiprocessor_count = 1;
  return CUDA_SUCCESS;
}

namespace {

struct FunctionAttributesPrefix {
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
};

} // namespace

extern "C" int32_t cudaFuncGetAttributes(void *attributes, const void *function) {
  if (!attributes || !function) return CUDA_ERROR_INVALID_VALUE;
  auto *value = static_cast<FunctionAttributesPrefix *>(attributes);
  *value = {};
  value->max_threads_per_block = 1024;
  const auto version = registeredPtxVersion(function);
  if (!version) return CUDA_ERROR_NOT_FOUND;
  value->ptx_version = *version;
  value->binary_version = *version;
  value->max_dynamic_shared_size_bytes = 49152;
  return CUDA_SUCCESS;
}

extern "C" int32_t cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(int32_t *blocks, const void *function, int32_t block_size,
                                                                          size_t dynamic_shared_memory, uint32_t flags) {
  (void)function;
  (void)dynamic_shared_memory;
  (void)flags;
  if (!blocks || block_size <= 0 || block_size > 1024) return CUDA_ERROR_INVALID_VALUE;
  *blocks = 1024 / block_size;
  if (*blocks == 0) *blocks = 1;
  return CUDA_SUCCESS;
}

extern "C" int32_t cudaMallocHost(void **pointer, size_t size) {
  const int32_t initialized = ensureRuntimeContext();
  if (initialized != CUDA_SUCCESS) return initialized;
  return mirroredAllocate(reinterpret_cast<uint64_t *>(pointer), size, true);
}

extern "C" int32_t cudaHostAlloc(void **pointer, size_t size, uint32_t flags) {
  return (flags & ~UINT32_C(7)) == 0 ? cudaMallocHost(pointer, size) : CUDA_ERROR_INVALID_VALUE;
}

extern "C" int32_t cudaFreeHost(void *pointer) {
  return freeManaged(reinterpret_cast<uintptr_t>(pointer)).value_or(CUDA_ERROR_INVALID_VALUE);
}

extern "C" int32_t cudaMallocManaged(void **pointer, size_t size, uint32_t flags) {
  (void)flags;
  const int32_t initialized = ensureRuntimeContext();
  if (initialized != CUDA_SUCCESS) return initialized;
  return mirroredAllocate(reinterpret_cast<uint64_t *>(pointer), size, 1);
}

extern "C" int32_t cudaMemcpyAsync(void *destination, const void *source, size_t size, int32_t kind, void *stream) {
  (void)stream;
  return cudaMemcpy(destination, source, size, kind);
}

namespace {

struct FatBinary {
  void *token = nullptr;
  void *module = nullptr;
  std::string ptx;
  std::optional<int32_t> ptx_version;
};

struct RegisteredVariable {
  const void *initial = nullptr;
  std::string device_name;
  size_t size = 0;
  FatBinary *fat_binary = nullptr;
};

struct RegisteredFunction {
  const void *host_function = nullptr;
  void *device_function = nullptr;
  std::string device_name;
  FatBinary *fat_binary = nullptr;
};

struct Dim3 {
  uint32_t x;
  uint32_t y;
  uint32_t z;
};

struct LaunchConfiguration {
  Dim3 grid;
  Dim3 block;
  size_t shared_memory;
  void *stream;
  int valid;
};

std::mutex registrationMutex;
std::vector<std::unique_ptr<FatBinary>> fatBinaries;
std::vector<std::unique_ptr<RegisteredFunction>> registeredFunctions;
std::vector<std::unique_ptr<RegisteredVariable>> registeredVariables;
thread_local LaunchConfiguration launchConfiguration{};

int32_t registerGlobalRemote(uint64_t module, const char *name, const void *initial, size_t size);

int32_t loadFatBinary(FatBinary &fat) {
  if (fat.module) return CUDA_SUCCESS;
  const int32_t initialized = ensureRuntimeContext();
  if (initialized != CUDA_SUCCESS) return initialized;
  if (fat.ptx.empty()) return CUDA_ERROR_NOT_SUPPORTED;

  int32_t status = cuModuleLoadData(&fat.module, fat.ptx.c_str());
  if (status != CUDA_SUCCESS) return status;
  for (const auto &variable : registeredVariables) {
    if (variable->fat_binary != &fat) continue;
    std::unique_ptr<unsigned char[]> zeros;
    const void *initial = variable->initial;
    if (!initial) {
      zeros.reset(new (std::nothrow) unsigned char[variable->size]{});
      if (!zeros) {
        status = CUDA_ERROR_UNKNOWN;
        break;
      }
      initial = zeros.get();
    }
    status = registerGlobalRemote(reinterpret_cast<uintptr_t>(fat.module), variable->device_name.c_str(), initial, variable->size);
    if (status != CUDA_SUCCESS) break;
  }
  if (status != CUDA_SUCCESS) {
    (void)cuModuleUnload(fat.module);
    fat.module = nullptr;
  }
  return status;
}

std::optional<int32_t> registeredPtxVersion(const void *function) {
  auto version = configuredPtxVersion();
  std::lock_guard lock{registrationMutex};
  if (!version) {
    for (const auto &entry : registeredFunctions) {
      if (entry->host_function == function || entry->device_function == function) {
        version = entry->fat_binary->ptx_version;
        break;
      }
    }
    if (!version && !fatBinaries.empty()) version = fatBinaries.front()->ptx_version;
  }
  return version;
}

} // namespace

extern "C" GEMSTONE_PUBLIC_API void **GEMSTONE_CUDA_API __cudaRegisterFatBinary(void *fat_binary) {
  auto entry = std::unique_ptr<FatBinary>{new (std::nothrow) FatBinary{}};
  if (!entry) return nullptr;
  entry->token = entry.get();
  const char *ptx = findUncompressedPtx(fat_binary);
  try {
    if (ptx) entry->ptx = ptx;
    entry->ptx_version = ptx ? std::string_view{ptx} ^ split(".target") ^ drop(1) ^ collect_first(parsePtxTarget) : std::nullopt;
  } catch (const std::exception &) {
    return nullptr;
  }
  void **token = &entry->token;
  try {
    std::lock_guard lock{registrationMutex};
    fatBinaries.push_back(std::move(entry));
  } catch (const std::exception &) {
    if (entry && entry->module) (void)cuModuleUnload(entry->module);
    return nullptr;
  }
  return token;
}

extern "C" GEMSTONE_PUBLIC_API void GEMSTONE_CUDA_API __cudaRegisterFatBinaryEnd(void **) {}

extern "C" GEMSTONE_PUBLIC_API void GEMSTONE_CUDA_API __cudaRegisterFunction(void **handle, const char *host_function,
                                                                             char *device_function, const char *device_name,
                                                                             int32_t thread_limit, void *thread_id, void *block_id,
                                                                             void *block_dimension, void *grid_dimension,
                                                                             int32_t *warp_size) {
  (void)device_function;
  (void)thread_limit;
  (void)thread_id;
  (void)block_id;
  (void)block_dimension;
  (void)grid_dimension;
  (void)warp_size;
  if (!handle || !*handle || !host_function || !device_name) return;
  auto *fat = static_cast<FatBinary *>(*handle);
  auto entry = std::unique_ptr<RegisteredFunction>{new (std::nothrow) RegisteredFunction{}};
  if (!entry) return;
  try {
    entry->device_name = device_name;
  } catch (const std::exception &) {
    return;
  }
  entry->host_function = host_function;
  entry->fat_binary = fat;
  try {
    std::lock_guard lock{registrationMutex};
    registeredFunctions.push_back(std::move(entry));
  } catch (const std::exception &) {
  }
}

extern "C" GEMSTONE_PUBLIC_API void GEMSTONE_CUDA_API __cudaRegisterVar(void **handle, char *host_variable, char *device_address,
                                                                        const char *device_name, int32_t external, size_t size,
                                                                        int32_t constant, int32_t global) {
  (void)device_address;
  (void)external;
  (void)constant;
  (void)global;
  if (!handle || !*handle || !device_name || size == 0) return;
  auto *fat = static_cast<FatBinary *>(*handle);
  auto entry = std::unique_ptr<RegisteredVariable>{new (std::nothrow) RegisteredVariable{}};
  if (!entry) return;
  try {
    entry->device_name = device_name;
  } catch (const std::exception &) {
    return;
  }
  entry->initial = host_variable;
  entry->size = size;
  entry->fat_binary = fat;
  try {
    std::lock_guard lock{registrationMutex};
    registeredVariables.push_back(std::move(entry));
  } catch (const std::exception &) {
  }
}

extern "C" GEMSTONE_PUBLIC_API void GEMSTONE_CUDA_API __cudaUnregisterFatBinary(void **handle) {
  if (!handle || !*handle) return;
  auto *fat = static_cast<FatBinary *>(*handle);
  std::unique_ptr<FatBinary> removed;
  {
    std::lock_guard lock{registrationMutex};
    registeredFunctions ^= filter([fat](const auto &entry) { return entry->fat_binary != fat; });
    registeredVariables ^= filter([fat](const auto &entry) { return entry->fat_binary != fat; });
    const auto cursor = std::find_if(fatBinaries.begin(), fatBinaries.end(), [=](const auto &entry) { return entry.get() == fat; });
    if (cursor == fatBinaries.end()) return;
    removed = std::move(*cursor);
    fatBinaries.erase(cursor);
  }
  if (removed->module) (void)cuModuleUnload(removed->module);
  removed->token = nullptr;
}

extern "C" GEMSTONE_PUBLIC_API int32_t GEMSTONE_CUDA_API __cudaGetKernel(void **kernel, const void *host_function) {
  if (!kernel || !host_function) return CUDA_ERROR_INVALID_VALUE;
  std::lock_guard lock{registrationMutex};
  const auto cursor = std::find_if(registeredFunctions.begin(), registeredFunctions.end(),
                                   [=](const auto &entry) { return entry->host_function == host_function; });
  int32_t status = CUDA_ERROR_INVALID_VALUE;
  if (cursor != registeredFunctions.end()) {
    auto &entry = **cursor;
    status = loadFatBinary(*entry.fat_binary);
    if (status == CUDA_SUCCESS) {
      status = entry.device_function ? CUDA_SUCCESS
                                     : cuModuleGetFunction(&entry.device_function, entry.fat_binary->module, entry.device_name.c_str());
    }
    if (status == CUDA_SUCCESS) *kernel = entry.device_function;
  }
  return status;
}

extern "C" GEMSTONE_PUBLIC_API unsigned GEMSTONE_CUDA_API __cudaPushCallConfiguration(Dim3 grid, Dim3 block, size_t shared_memory,
                                                                                      void *stream) {
  launchConfiguration.grid = grid;
  launchConfiguration.block = block;
  launchConfiguration.shared_memory = shared_memory;
  launchConfiguration.stream = stream;
  launchConfiguration.valid = 1;
  return CUDA_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int32_t GEMSTONE_CUDA_API __cudaPopCallConfiguration(Dim3 *grid, Dim3 *block, size_t *shared_memory,
                                                                                    void *stream) {
  if (!launchConfiguration.valid || !grid || !block || !shared_memory || !stream) return CUDA_ERROR_INVALID_VALUE;
  *grid = launchConfiguration.grid;
  *block = launchConfiguration.block;
  *shared_memory = launchConfiguration.shared_memory;
  *static_cast<void **>(stream) = launchConfiguration.stream;
  launchConfiguration.valid = 0;
  return CUDA_SUCCESS;
}

extern "C" GEMSTONE_PUBLIC_API int32_t GEMSTONE_CUDA_API __cudaLaunchKernel(void *kernel, Dim3 grid, Dim3 block, void **arguments,
                                                                            size_t shared_memory, void *stream) {
  const int32_t status = cuLaunchKernel(kernel, grid.x, grid.y, grid.z, block.x, block.y, block.z, static_cast<uint32_t>(shared_memory),
                                        stream, arguments, nullptr);
  lastError = status;
  return status;
}

namespace {

int32_t deviceGetAttribute(int32_t *value, int32_t attribute, int32_t device) { return cudaDeviceGetAttribute(value, attribute, device); }

int32_t deviceGetName(char *name, int32_t length, int32_t device) {
  static const char device_name[] = "Gemstone PTX emulator";
  if (!name || length <= 0 || device != 0) return CUDA_ERROR_INVALID_VALUE;
  const size_t source_size = sizeof(device_name) - 1;
  const size_t copy_size = source_size < static_cast<size_t>(length) - 1 ? source_size : static_cast<size_t>(length) - 1;
  memcpy(name, device_name, copy_size);
  name[copy_size] = '\0';
  return CUDA_SUCCESS;
}

int32_t deviceTotalMemV2(uint64_t *bytes, int32_t device) {
  if (!bytes || device != 0) return CUDA_ERROR_INVALID_VALUE;
  *bytes = UINT64_C(8) * 1024 * 1024 * 1024;
  return CUDA_SUCCESS;
}

int32_t deviceComputeCapability(int32_t *major, int32_t *minor, int32_t device) {
  if (!major || !minor || device != 0) return CUDA_ERROR_INVALID_VALUE;
  const auto capability = computeCapability();
  if (!capability) return CUDA_ERROR_NOT_INITIALIZED;
  *major = capability->first;
  *minor = capability->second;
  return CUDA_SUCCESS;
}

int32_t ctxGetCurrent(void **context) {
  if (!context) return CUDA_ERROR_INVALID_VALUE;
  *context = currentContext;
  return CUDA_SUCCESS;
}

int32_t ctxGetDevice(int32_t *device) {
  if (!device) return CUDA_ERROR_INVALID_VALUE;
  *device = 0;
  return CUDA_SUCCESS;
}

int32_t ctxGetDeviceV2(int32_t *device, void *context) {
  (void)context;
  return ctxGetDevice(device);
}

int32_t ctxSetCurrent(void *context) {
  if (context && !contextIsLive(context)) return CUDA_ERROR_INVALID_VALUE;
  currentContext = context;
  return CUDA_SUCCESS;
}

/* Older CUDA libraries still resolve the unversioned push/pop spellings
 * directly.  Keep both spellings functional; the generated ABI stubs only
 * cover the v2 names and return NOT_SUPPORTED. */
int32_t ctxPushCurrent(void *context) {
  if (!contextIsLive(context)) return CUDA_ERROR_INVALID_VALUE;
  if (contextStackSize == 64) return CUDA_ERROR_UNKNOWN;
  contextStack[contextStackSize++] = currentContext;
  currentContext = context;
  return CUDA_SUCCESS;
}

int32_t ctxPopCurrent(void **context) {
  if (!context) return CUDA_ERROR_INVALID_VALUE;
  *context = currentContext;
  currentContext = contextStackSize == 0 ? nullptr : contextStack[--contextStackSize];
  return CUDA_SUCCESS;
}

std::mutex primaryContextMutex;
void *primaryContext = nullptr;
uint32_t primaryContextReferences = 0;
std::atomic_uintptr_t implicitContext{0};

void *activeContext() {
  return currentContext ? currentContext : reinterpret_cast<void *>(implicitContext.load(std::memory_order_acquire));
}

int32_t devicePrimaryCtxRetain(void **context, int32_t device) {
  if (!context || device != 0) return CUDA_ERROR_INVALID_VALUE;
  std::lock_guard lock{primaryContextMutex};
  int32_t status = CUDA_SUCCESS;
  if (primaryContext && !contextIsLive(primaryContext)) {
    primaryContext = nullptr;
    primaryContextReferences = 0;
  }
  if (!primaryContext) {
    status = cuCtxCreate_v2(&primaryContext, 0, device);
    if (status == CUDA_SUCCESS) {
      void *created = nullptr;
      status = ctxPopCurrent(&created);
    }
  }
  if (status == CUDA_SUCCESS) {
    if (primaryContextReferences == UINT32_MAX) status = CUDA_ERROR_UNKNOWN;
    else {
      ++primaryContextReferences;
      *context = primaryContext;
      implicitContext.store(reinterpret_cast<uintptr_t>(primaryContext), std::memory_order_release);
    }
  }
  return status;
}

int32_t devicePrimaryCtxReleaseV2(int32_t device) {
  if (device != 0) return CUDA_ERROR_INVALID_VALUE;
  std::lock_guard lock{primaryContextMutex};
  int32_t status = CUDA_SUCCESS;
  if (!primaryContext || primaryContextReferences == 0) status = CUDA_ERROR_INVALID_VALUE;
  else if (--primaryContextReferences == 0) {
    void *context = primaryContext;
    primaryContext = nullptr;
    implicitContext.store(0, std::memory_order_release);
    status = cuCtxDestroy_v2(context);
  }
  return status;
}

int32_t devicePrimaryCtxGetState(int32_t device, uint32_t *flags, int32_t *active) {
  if (device != 0 || !flags || !active) return CUDA_ERROR_INVALID_VALUE;
  *flags = 0;
  std::lock_guard lock{primaryContextMutex};
  *active = primaryContextReferences == 0 ? 0 : 1;
  return CUDA_SUCCESS;
}

int32_t devicePrimaryCtxSetFlagsV2(int32_t device, uint32_t flags) {
  (void)flags;
  return device == 0 ? CUDA_SUCCESS : CUDA_ERROR_INVALID_VALUE;
}

int32_t deviceGetByPciBusId(int32_t *device, const char *busId) {
  if (!device || !busId) return CUDA_ERROR_INVALID_VALUE;
  if (strcmp(busId, "0000:01:00.0") != 0) return CUDA_ERROR_INVALID_VALUE;
  *device = 0;
  return CUDA_SUCCESS;
}

int32_t deviceGetTexture1DLinearMaxWidth(size_t *width, int32_t format, uint32_t channels, int32_t device) {
  (void)format;
  (void)channels;
  if (!width || device != 0) return CUDA_ERROR_INVALID_VALUE;
  *width = 134217728;
  return CUDA_SUCCESS;
}

int32_t deviceGetP2PAttribute(int32_t *value, int32_t attribute, int32_t source, int32_t destination) {
  if (!value || source != 0 || destination != 0) return CUDA_ERROR_INVALID_VALUE;
  *value = attribute == 1 ? 1 : 0;
  return CUDA_SUCCESS;
}

int32_t deviceCanAccessPeer(int32_t *value, int32_t source, int32_t destination) {
  if (!value || source != 0 || destination != 0) return CUDA_ERROR_INVALID_VALUE;
  *value = 0;
  return CUDA_SUCCESS;
}

} // namespace

/* CUDA libraries probe these capability queries while constructing their
 * dispatch tables.  The emulator deliberately advertises no host/P2P atomic
 * operations, but a successful query is required so a consumer can make that
 * decision itself rather than treating the driver as uninitialised. */
extern "C" int32_t cuDeviceGetHostAtomicCapabilities(uint32_t *capabilities, const void *operations, uint32_t count, int32_t device) {
  (void)operations;
  if (!capabilities || device != 0) return CUDA_ERROR_INVALID_VALUE;
  std::fill_n(capabilities, static_cast<size_t>(count), 0U);
  return CUDA_SUCCESS;
}

extern "C" int32_t cuDeviceGetP2PAtomicCapabilities(uint32_t *capabilities, const void *operations, uint32_t count, int32_t source,
                                                    int32_t destination) {
  (void)operations;
  if (!capabilities || source != 0 || destination != 0) return CUDA_ERROR_INVALID_VALUE;
  std::fill_n(capabilities, static_cast<size_t>(count), 0U);
  return CUDA_SUCCESS;
}

namespace {

int32_t ctxGetCacheConfig(int32_t *config) {
  if (!config) return CUDA_ERROR_INVALID_VALUE;
  *config = 0;
  return CUDA_SUCCESS;
}

int32_t ctxSetCacheConfig(int32_t config) {
  (void)config;
  return CUDA_SUCCESS;
}

int32_t ctxGetSharedMemConfig(int32_t *config) {
  if (!config) return CUDA_ERROR_INVALID_VALUE;
  *config = 0;
  return CUDA_SUCCESS;
}

int32_t ctxSetSharedMemConfig(int32_t config) {
  (void)config;
  return CUDA_SUCCESS;
}

int32_t ctxGetStreamPriorityRange(int32_t *least, int32_t *greatest) {
  if (!least || !greatest) return CUDA_ERROR_INVALID_VALUE;
  *least = 0;
  *greatest = 0;
  return CUDA_SUCCESS;
}

int32_t ctxResetPersistingL2Cache() { return CUDA_SUCCESS; }

int32_t devicePrimaryCtxResetV2(int32_t device) { return device == 0 ? CUDA_SUCCESS : CUDA_ERROR_INVALID_VALUE; }

int32_t deviceGetUuid(void *uuid, int32_t device);
int32_t deviceGetPciBusId(char *busId, int32_t length, int32_t device);
int32_t ctxGetFlags(uint32_t *flags);
int32_t ctxGetApiVersion(void *context, uint32_t *version);
int32_t ctxGetLimit(size_t *value, int32_t limit);
int32_t ctxSetLimit(int32_t limit, size_t value);
int32_t ctxDetach(void *context);
int32_t ctxSynchronizeV2(void *context);
int32_t deviceGetDefaultMemPool(void **pool, int32_t device);
int32_t deviceGetMemPool(void **pool, int32_t device);
int32_t deviceSetMemPool(int32_t device, void *pool);
int32_t memGetDefaultMemPool(void **pool, void *location, int32_t allocationType);
int32_t memGetMemPool(void **pool, void *location, int32_t allocationType);
int32_t memSetMemPool(void *location, int32_t allocationType, void *pool);
int32_t memGetInfoV2(size_t *freeBytes, size_t *totalBytes);

} // namespace
extern "C" int32_t cuGetExportTable(const void **table, const void *table_id);
extern "C" int32_t cuGetProcAddress_v2(const char *symbol, void **pfn, int32_t cuda_version, uint64_t flags, int32_t *symbol_status);

/* Native CUDA libraries discover these Driver entry points with dlsym. Provide
 * the same emulated implementations through the ordinary symbol path as well
 * as cuGetProcAddress. */
extern "C" int32_t cuDeviceGetAttribute(int32_t *value, int32_t attribute, int32_t device) {
  return deviceGetAttribute(value, attribute, device);
}

extern "C" int32_t cuDeviceGetName(char *name, int32_t length, int32_t device) { return deviceGetName(name, length, device); }

extern "C" int32_t cuDeviceTotalMem_v2(uint64_t *bytes, int32_t device) { return deviceTotalMemV2(bytes, device); }

extern "C" int32_t cuDeviceComputeCapability(int32_t *major, int32_t *minor, int32_t device) {
  return deviceComputeCapability(major, minor, device);
}

extern "C" int32_t cuDeviceTotalMem(uint32_t *bytes, int32_t device) {
  if (!bytes || device != 0) return CUDA_ERROR_INVALID_VALUE;
  *bytes = UINT32_MAX;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuDeviceGetByPCIBusId(int32_t *device, const char *busId) { return deviceGetByPciBusId(device, busId); }

extern "C" int32_t cuDeviceGetTexture1DLinearMaxWidth(size_t *width, int32_t format, uint32_t channels, int32_t device) {
  return deviceGetTexture1DLinearMaxWidth(width, format, channels, device);
}

extern "C" int32_t cuDeviceGetP2PAttribute(int32_t *value, int32_t attribute, int32_t source, int32_t destination) {
  return deviceGetP2PAttribute(value, attribute, source, destination);
}

extern "C" int32_t cuDeviceCanAccessPeer(int32_t *value, int32_t source, int32_t destination) {
  return deviceCanAccessPeer(value, source, destination);
}

namespace {

struct LegacyDeviceProperties {
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
};

} // namespace

extern "C" int32_t cuDeviceGetProperties(void *properties, int32_t device) {
  if (!properties || device != 0) return CUDA_ERROR_INVALID_VALUE;
  auto *value = static_cast<LegacyDeviceProperties *>(properties);
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
  return CUDA_SUCCESS;
}

extern "C" int32_t cuDeviceGetUuid_v2(void *uuid, int32_t device) { return deviceGetUuid(uuid, device); }

extern "C" int32_t cuDeviceGetUuid(void *uuid, int32_t device) { return deviceGetUuid(uuid, device); }

extern "C" int32_t cuDeviceGetPCIBusId(char *busId, int32_t length, int32_t device) { return deviceGetPciBusId(busId, length, device); }

extern "C" int32_t cuCtxGetCurrent(void **context) { return ctxGetCurrent(context); }

extern "C" int32_t cuCtxGetDevice(int32_t *device) { return ctxGetDevice(device); }

extern "C" int32_t cuCtxGetDevice_v2(int32_t *device, void *context) { return ctxGetDeviceV2(device, context); }

extern "C" int32_t cuCtxSetCurrent(void *context) { return ctxSetCurrent(context); }

extern "C" int32_t cuCtxPushCurrent(void *context) { return ctxPushCurrent(context); }

extern "C" int32_t cuCtxPushCurrent_v2(void *context) { return ctxPushCurrent(context); }

extern "C" int32_t cuCtxPopCurrent(void **context) { return ctxPopCurrent(context); }

extern "C" int32_t cuCtxPopCurrent_v2(void **context) { return ctxPopCurrent(context); }

extern "C" int32_t cuCtxCreate(void **context, uint32_t flags, int32_t device) { return cuCtxCreate_v2(context, flags, device); }

extern "C" int32_t cuDevicePrimaryCtxRetain(void **context, int32_t device) { return devicePrimaryCtxRetain(context, device); }

extern "C" int32_t cuDevicePrimaryCtxRelease_v2(int32_t device) { return devicePrimaryCtxReleaseV2(device); }

extern "C" int32_t cuDevicePrimaryCtxGetState(int32_t device, uint32_t *flags, int32_t *active) {
  return devicePrimaryCtxGetState(device, flags, active);
}

extern "C" int32_t cuDevicePrimaryCtxSetFlags_v2(int32_t device, uint32_t flags) { return devicePrimaryCtxSetFlagsV2(device, flags); }

extern "C" int32_t cuDevicePrimaryCtxRelease(int32_t device) { return devicePrimaryCtxReleaseV2(device); }

extern "C" int32_t cuDevicePrimaryCtxReset_v2(int32_t device) { return devicePrimaryCtxResetV2(device); }

extern "C" int32_t cuDevicePrimaryCtxReset(int32_t device) { return devicePrimaryCtxResetV2(device); }

extern "C" int32_t cuDevicePrimaryCtxSetFlags(int32_t device, uint32_t flags) { return devicePrimaryCtxSetFlagsV2(device, flags); }

extern "C" int32_t cuCtxGetCacheConfig(int32_t *config) { return ctxGetCacheConfig(config); }

extern "C" int32_t cuCtxSetCacheConfig(int32_t config) { return ctxSetCacheConfig(config); }

extern "C" int32_t cuCtxGetSharedMemConfig(int32_t *config) { return ctxGetSharedMemConfig(config); }

extern "C" int32_t cuCtxSetSharedMemConfig(int32_t config) { return ctxSetSharedMemConfig(config); }

extern "C" int32_t cuCtxGetStreamPriorityRange(int32_t *least, int32_t *greatest) { return ctxGetStreamPriorityRange(least, greatest); }

extern "C" int32_t cuCtxResetPersistingL2Cache() { return ctxResetPersistingL2Cache(); }

extern "C" int32_t cuCtxGetFlags(uint32_t *flags) { return ctxGetFlags(flags); }

extern "C" int32_t cuCtxGetApiVersion(void *context, uint32_t *version) { return ctxGetApiVersion(context, version); }

extern "C" int32_t cuCtxGetLimit(size_t *value, int32_t limit) { return ctxGetLimit(value, limit); }

extern "C" int32_t cuCtxSetLimit(int32_t limit, size_t value) { return ctxSetLimit(limit, value); }

extern "C" int32_t cuCtxDetach(void *context) { return ctxDetach(context); }

extern "C" int32_t cuCtxSynchronize_v2(void *context) { return ctxSynchronizeV2(context); }

extern "C" int32_t cuDeviceGetDefaultMemPool(void **pool, int32_t device) { return deviceGetDefaultMemPool(pool, device); }

extern "C" int32_t cuDeviceGetMemPool(void **pool, int32_t device) { return deviceGetMemPool(pool, device); }

extern "C" int32_t cuDeviceSetMemPool(int32_t device, void *pool) { return deviceSetMemPool(device, pool); }

extern "C" int32_t cuMemGetDefaultMemPool(void **pool, void *location, int32_t allocation_type) {
  return memGetDefaultMemPool(pool, location, allocation_type);
}

extern "C" int32_t cuMemGetMemPool(void **pool, void *location, int32_t allocation_type) {
  return memGetMemPool(pool, location, allocation_type);
}

extern "C" int32_t cuMemSetMemPool(void *location, int32_t allocation_type, void *pool) {
  return memSetMemPool(location, allocation_type, pool);
}

extern "C" int32_t cuMemGetInfo_v2(size_t *freeBytes, size_t *totalBytes) { return memGetInfoV2(freeBytes, totalBytes); }

extern "C" int32_t cuFlushGPUDirectRDMAWrites(int32_t target, int32_t scope) {
  (void)target;
  (void)scope;
  return CUDA_SUCCESS;
}

extern "C" int32_t GEMSTONE_CUDA_API cuGetProcAddress(const char *symbol, void **pfn, int32_t cuda_version, uint64_t flags) {
  return cuGetProcAddress_v2(symbol, pfn, cuda_version, flags, nullptr);
}

namespace {

int32_t deviceGetDefaultMemPool(void **pool, int32_t device) {
  if (!pool || device != 0) return CUDA_ERROR_INVALID_VALUE;
  *pool = reinterpret_cast<void *>(static_cast<uintptr_t>(UINT64_C(0x300000000)));
  return CUDA_SUCCESS;
}

int32_t deviceGetMemPool(void **pool, int32_t device) { return deviceGetDefaultMemPool(pool, device); }

int32_t deviceSetMemPool(int32_t device, void *pool) {
  (void)pool;
  return device == 0 ? CUDA_SUCCESS : CUDA_ERROR_INVALID_VALUE;
}

int32_t memGetDefaultMemPool(void **pool, void *location, int32_t allocationType) {
  (void)location;
  (void)allocationType;
  if (!pool) return CUDA_ERROR_INVALID_VALUE;
  *pool = reinterpret_cast<void *>(static_cast<uintptr_t>(UINT64_C(0x300000000)));
  return CUDA_SUCCESS;
}

int32_t memGetMemPool(void **pool, void *location, int32_t allocationType) { return memGetDefaultMemPool(pool, location, allocationType); }

int32_t memSetMemPool(void *location, int32_t allocationType, void *pool) {
  (void)location;
  (void)allocationType;
  (void)pool;
  return CUDA_SUCCESS;
}

int32_t memAllocAsync(uint64_t *pointer, size_t size, void *stream) {
  (void)stream;
  return cuMemAlloc_v2(pointer, size);
}

int32_t memAllocFromPoolAsync(uint64_t *pointer, size_t size, void *pool, void *stream) {
  (void)pool;
  (void)stream;
  return cuMemAlloc_v2(pointer, size);
}

int32_t memFreeAsync(uint64_t pointer, void *stream) {
  (void)stream;
  return cuMemFree_v2(pointer);
}

int32_t memAllocPitchV2(uint64_t *pointer, size_t *pitch, size_t width, size_t height, uint32_t elementSize) {
  (void)elementSize;
  if (!pointer || !pitch || width == 0 || height == 0 || height > SIZE_MAX / width) {
    return CUDA_ERROR_INVALID_VALUE;
  }
  *pitch = width;
  return cuMemAlloc_v2(pointer, width * height);
}

int32_t memGetInfoV2(size_t *freeBytes, size_t *totalBytes) {
  if (!freeBytes || !totalBytes) return CUDA_ERROR_INVALID_VALUE;
  *totalBytes = static_cast<size_t>(UINT64_C(8)) * 1024 * 1024 * 1024;
  *freeBytes = *totalBytes;
  return CUDA_SUCCESS;
}

int32_t memAllocManaged(uint64_t *pointer, size_t size, uint32_t flags) {
  (void)flags;
  return managedAllocate(pointer, size);
}

int32_t ctxGetFlags(uint32_t *flags) {
  if (!flags) return CUDA_ERROR_INVALID_VALUE;
  *flags = 0;
  return CUDA_SUCCESS;
}

int32_t ctxGetApiVersion(void *context, uint32_t *version) {
  (void)context;
  if (!version) return CUDA_ERROR_INVALID_VALUE;
  *version = 13030;
  return CUDA_SUCCESS;
}

int32_t ctxGetLimit(size_t *value, int32_t limit) {
  (void)limit;
  if (!value) return CUDA_ERROR_INVALID_VALUE;
  *value = 0;
  return CUDA_SUCCESS;
}

int32_t ctxSetLimit(int32_t limit, size_t value) {
  (void)limit;
  (void)value;
  return CUDA_SUCCESS;
}

int32_t ctxDetach(void *context) { return cuCtxDestroy_v2(context); }

int32_t ctxSynchronizeV2(void *context) {
  (void)context;
  return cuCtxSynchronize();
}
int32_t deviceGetUuid(void *uuid, int32_t device) {
  if (!uuid || device != 0) return CUDA_ERROR_INVALID_VALUE;
  static const uint8_t virtual_uuid[16] = {0x47, 0x45, 0x4d, 0x53, 0x54, 0x4f, 0x4e, 0x45, 0x2d, 0x43, 0x55, 0x44, 0x41, 0x2d, 0x30, 0x31};
  memcpy(uuid, virtual_uuid, sizeof(virtual_uuid));
  return CUDA_SUCCESS;
}

int32_t deviceGetPciBusId(char *busId, int32_t length, int32_t device) {
  static const char value[] = "0000:01:00.0";
  if (!busId || length <= 0 || device != 0) return CUDA_ERROR_INVALID_VALUE;
  const size_t source_size = sizeof(value) - 1;
  const size_t copy_size = source_size < static_cast<size_t>(length) - 1 ? source_size : static_cast<size_t>(length) - 1;
  memcpy(busId, value, copy_size);
  busId[copy_size] = '\0';
  return CUDA_SUCCESS;
}

int32_t getErrorString(int32_t status, const char **message) {
  if (!message) return CUDA_ERROR_INVALID_VALUE;
  switch (status) {
    case CUDA_SUCCESS: *message = "no error"; break;
    case CUDA_ERROR_INVALID_VALUE: *message = "invalid argument"; break;
    case CUDA_ERROR_NOT_INITIALIZED: *message = "initialization error"; break;
    case CUDA_ERROR_NOT_FOUND: *message = "named symbol not found"; break;
    case CUDA_ERROR_NOT_SUPPORTED: *message = "operation not supported"; break;
    case CUDA_ERROR_UNKNOWN: *message = "unknown error"; break;
    default: return CUDA_ERROR_INVALID_VALUE;
  }
  return CUDA_SUCCESS;
}

} // namespace

/* cuBLAS obtains these through dlsym while constructing its driver dispatch
 * table, so the ordinary ABI symbols must carry the same semantics as the
 * cuGetProcAddress entries above. */
extern "C" int32_t cuGetErrorString(int32_t status, const char **message) { return getErrorString(status, message); }

namespace {

int32_t getErrorName(int32_t status, const char **name) {
  if (!name) return CUDA_ERROR_INVALID_VALUE;
  switch (status) {
    case CUDA_SUCCESS: *name = "CUDA_SUCCESS"; break;
    case CUDA_ERROR_INVALID_VALUE: *name = "CUDA_ERROR_INVALID_VALUE"; break;
    case CUDA_ERROR_NOT_INITIALIZED: *name = "CUDA_ERROR_NOT_INITIALIZED"; break;
    case CUDA_ERROR_NOT_FOUND: *name = "CUDA_ERROR_NOT_FOUND"; break;
    case CUDA_ERROR_NOT_SUPPORTED: *name = "CUDA_ERROR_NOT_SUPPORTED"; break;
    case CUDA_ERROR_UNKNOWN: *name = "CUDA_ERROR_UNKNOWN"; break;
    default: return CUDA_ERROR_INVALID_VALUE;
  }
  return CUDA_SUCCESS;
}

} // namespace

extern "C" int32_t cuGetErrorName(int32_t status, const char **name) { return getErrorName(status, name); }

namespace {

int32_t memcpyHtoDAsyncV2(uint64_t destination, const void *source, size_t size, void *stream) {
  (void)stream;
  return cuMemcpyHtoD_v2(destination, source, size);
}

int32_t memcpyDtoHAsyncV2(void *destination, uint64_t source, size_t size, void *stream) {
  (void)stream;
  return cuMemcpyDtoH_v2(destination, source, size);
}

int32_t memcpyDtoDAsyncV2(uint64_t destination, uint64_t source, size_t size, void *stream) {
  (void)stream;
  return cuMemcpyDtoD_v2(destination, source, size);
}

/* Generic copies use the same allocation metadata as the pointer attributes. */
bool devicePointer(uint64_t pointer) {
  const auto metadata = pointerMetadata(pointer);
  return metadata && !metadata->host;
}

} // namespace
extern "C" int32_t cuMemcpy(uint64_t destination, uint64_t source, size_t size) {
  const int destination_device = devicePointer(destination);
  const int source_device = devicePointer(source);
  if (destination_device && !source_device) return cuMemcpyHtoD_v2(destination, reinterpret_cast<const void *>(source), size);
  if (!destination_device && source_device) return cuMemcpyDtoH_v2(reinterpret_cast<void *>(destination), source, size);
  return cuMemcpyDtoD_v2(destination, source, size);
}

extern "C" int32_t cuMemcpy_ptds(uint64_t destination, uint64_t source, size_t size) { return cuMemcpy(destination, source, size); }

extern "C" int32_t cuMemcpyAsync(uint64_t destination, uint64_t source, size_t size, void *stream) {
  const int destination_device = devicePointer(destination);
  const int source_device = devicePointer(source);
  if (destination_device && !source_device) return memcpyHtoDAsyncV2(destination, reinterpret_cast<const void *>(source), size, stream);
  if (!destination_device && source_device) return memcpyDtoHAsyncV2(reinterpret_cast<void *>(destination), source, size, stream);
  return memcpyDtoDAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemcpyAsync_ptsz(uint64_t destination, uint64_t source, size_t size, void *stream) {
  return cuMemcpyAsync(destination, source, size, stream);
}

/* CUDA 13's runtime uses these stream-specialized spellings for ordinary
 * copies.  They are ABI aliases, not distinct stream implementations: the
 * emulator currently executes every operation synchronously on its logical
 * default stream.  Export real symbols as well as registering them below so
 * both dlsym and cuGetProcAddress callers take the same path. */
extern "C" int32_t cuMemcpyHtoD_ptds(uint64_t destination, const void *source, size_t size) {
  return cuMemcpyHtoD_v2(destination, source, size);
}

extern "C" int32_t cuMemcpyHtoD_v2_ptds(uint64_t destination, const void *source, size_t size) {
  return cuMemcpyHtoD_v2(destination, source, size);
}

extern "C" int32_t cuMemcpyDtoH_ptds(void *destination, uint64_t source, size_t size) { return cuMemcpyDtoH_v2(destination, source, size); }

extern "C" int32_t cuMemcpyDtoH_v2_ptds(void *destination, uint64_t source, size_t size) {
  return cuMemcpyDtoH_v2(destination, source, size);
}

extern "C" int32_t cuMemcpyDtoD_ptds(uint64_t destination, uint64_t source, size_t size) {
  return cuMemcpyDtoD_v2(destination, source, size);
}

extern "C" int32_t cuMemcpyDtoD_v2_ptds(uint64_t destination, uint64_t source, size_t size) {
  return cuMemcpyDtoD_v2(destination, source, size);
}

extern "C" int32_t cuMemcpyHtoDAsync_ptsz(uint64_t destination, const void *source, size_t size, void *stream) {
  return memcpyHtoDAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemcpyHtoDAsync_v2_ptsz(uint64_t destination, const void *source, size_t size, void *stream) {
  return memcpyHtoDAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemcpyDtoHAsync_ptsz(void *destination, uint64_t source, size_t size, void *stream) {
  return memcpyDtoHAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemcpyDtoHAsync_v2_ptsz(void *destination, uint64_t source, size_t size, void *stream) {
  return memcpyDtoHAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemcpyDtoDAsync_ptsz(uint64_t destination, uint64_t source, size_t size, void *stream) {
  return memcpyDtoDAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemcpyDtoDAsync_v2_ptsz(uint64_t destination, uint64_t source, size_t size, void *stream) {
  return memcpyDtoDAsyncV2(destination, source, size, stream);
}

namespace {

int32_t memsetD8Async(uint64_t destination, uint8_t value, size_t count, void *stream) {
  (void)stream;
  return cuMemsetD8_v2(destination, value, count);
}

} // namespace

extern "C" int32_t cuMemsetD8_ptds(uint64_t destination, uint8_t value, size_t count) { return cuMemsetD8_v2(destination, value, count); }

extern "C" int32_t cuMemsetD8_v2_ptds(uint64_t destination, uint8_t value, size_t count) {
  return cuMemsetD8_v2(destination, value, count);
}

extern "C" int32_t cuMemsetD8Async_ptsz(uint64_t destination, uint8_t value, size_t count, void *stream) {
  return memsetD8Async(destination, value, count, stream);
}

namespace {

int32_t synchronizeStreamCallbacks(void *stream) noexcept;

int32_t streamSynchronize(void *stream) { return synchronizeStreamCallbacks(stream); }

int32_t streamQuery(void *stream) {
  (void)stream;
  return CUDA_SUCCESS;
}

std::atomic_uint64_t nextEvent{UINT64_C(0x500000000)};

int32_t eventCreate(void **event, uint32_t flags) {
  (void)flags;
  if (!event) return CUDA_ERROR_INVALID_VALUE;
  *event = reinterpret_cast<void *>(static_cast<uintptr_t>(nextEvent.fetch_add(UINT64_C(1), std::memory_order_relaxed)));
  return CUDA_SUCCESS;
}

int32_t eventDestroy(void *event) {
  (void)event;
  return CUDA_SUCCESS;
}

int32_t eventElapsedTime(float *milliseconds, void *start, void *end) {
  (void)start;
  (void)end;
  if (!milliseconds) return CUDA_ERROR_INVALID_VALUE;
  *milliseconds = 0.0f;
  return CUDA_SUCCESS;
}

int32_t eventQuery(void *event) {
  (void)event;
  return CUDA_SUCCESS;
}

int32_t eventRecord(void *event, void *stream) {
  (void)event;
  (void)stream;
  return CUDA_SUCCESS;
}

int32_t eventRecordWithFlags(void *event, void *stream, uint32_t flags) {
  (void)flags;
  return eventRecord(event, stream);
}

int32_t eventSynchronize(void *event) {
  (void)event;
  return CUDA_SUCCESS;
}

int32_t streamWaitEvent(void *stream, void *event, uint32_t flags) {
  (void)stream;
  (void)event;
  (void)flags;
  return CUDA_SUCCESS;
}

std::atomic_uint64_t nextStream{UINT64_C(0x400000000)};

} // namespace

extern "C" int32_t cuStreamCreate(void **stream, uint32_t flags) {
  (void)flags;
  if (!stream) return CUDA_ERROR_INVALID_VALUE;
  *stream = reinterpret_cast<void *>(static_cast<uintptr_t>(nextStream.fetch_add(UINT64_C(1), std::memory_order_relaxed)));
  return CUDA_SUCCESS;
}

extern "C" int32_t cuStreamCreateWithPriority(void **stream, uint32_t flags, int32_t priority) {
  (void)priority;
  return cuStreamCreate(stream, flags);
}

extern "C" int32_t cuStreamDestroy_v2(void *stream) {
  (void)stream;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuStreamDestroy(void *stream) { return cuStreamDestroy_v2(stream); }

extern "C" int32_t cudaStreamCreate(void **stream) { return cuStreamCreate(stream, 0); }

extern "C" int32_t cudaStreamCreateWithFlags(void **stream, uint32_t flags) {
  return (flags & ~UINT32_C(1)) == 0 ? cuStreamCreate(stream, flags) : CUDA_ERROR_INVALID_VALUE;
}

extern "C" int32_t cudaStreamCreateWithPriority(void **stream, uint32_t flags, int32_t priority) {
  return (flags & ~UINT32_C(1)) == 0 ? cuStreamCreateWithPriority(stream, flags, priority) : CUDA_ERROR_INVALID_VALUE;
}

extern "C" int32_t cudaStreamDestroy(void *stream) { return cuStreamDestroy(stream); }

extern "C" int32_t cudaStreamQuery(void *stream) { return streamQuery(stream); }

extern "C" int32_t cudaStreamWaitEvent(void *stream, void *event, uint32_t flags) {
  return flags == 0 ? streamWaitEvent(stream, event, flags) : CUDA_ERROR_INVALID_VALUE;
}

extern "C" int32_t cudaEventCreate(void **event) { return eventCreate(event, 0); }

extern "C" int32_t cudaEventCreateWithFlags(void **event, uint32_t flags) { return eventCreate(event, flags); }

extern "C" int32_t cudaEventDestroy(void *event) { return eventDestroy(event); }

extern "C" int32_t cudaEventElapsedTime(float *milliseconds, void *start, void *end) { return eventElapsedTime(milliseconds, start, end); }

extern "C" int32_t cudaEventQuery(void *event) { return eventQuery(event); }

extern "C" int32_t cudaEventRecord(void *event, void *stream) { return eventRecord(event, stream); }

extern "C" int32_t cudaEventRecordWithFlags(void *event, void *stream, uint32_t flags) {
  return eventRecordWithFlags(event, stream, flags);
}

extern "C" int32_t cudaEventSynchronize(void *event) { return eventSynchronize(event); }

namespace {

uint64_t memPoolReleaseThreshold = 0;

} // namespace

extern "C" int32_t cuMemPoolCreate(void **pool, const void *properties) {
  (void)properties;
  if (!pool) return CUDA_ERROR_INVALID_VALUE;
  *pool = reinterpret_cast<void *>(static_cast<uintptr_t>(UINT64_C(0x300000000)));
  return CUDA_SUCCESS;
}

extern "C" int32_t cuMemPoolDestroy(void *pool) {
  (void)pool;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuMemPoolGetAttribute(void *pool, int32_t attribute, void *value) {
  (void)pool;
  if (!value) return CUDA_ERROR_INVALID_VALUE;
  if (attribute == 0) *static_cast<uint64_t *>(value) = memPoolReleaseThreshold;
  else *static_cast<uint64_t *>(value) = 0;
  return CUDA_SUCCESS;
}

extern "C" int32_t cuMemPoolSetAttribute(void *pool, int32_t attribute, void *value) {
  (void)pool;
  if (!value) return CUDA_ERROR_INVALID_VALUE;
  if (attribute == 0) memPoolReleaseThreshold = *static_cast<const uint64_t *>(value);
  return CUDA_SUCCESS;
}

extern "C" int32_t cuMemPoolTrimTo(void *pool, size_t minimum_bytes_to_keep) {
  (void)pool;
  (void)minimum_bytes_to_keep;
  return CUDA_SUCCESS;
}

namespace {

int32_t devSmResourceSplit(void *result, uint32_t group_count, const void *input, void *remainder, uint32_t flags, void *group_params) {
  (void)result;
  (void)group_count;
  (void)input;
  (void)remainder;
  (void)flags;
  (void)group_params;
  return CUDA_ERROR_NOT_SUPPORTED;
}

/* CUDA runtime's private interface table (CUDART_INTERFACE, 13 entries). */
int32_t cudartLoadModuleImage(void **module, const void *fatbin, int allow_empty) {
  const char *ptx = findUncompressedPtx(fatbin);
  if (!ptx && !allow_empty) return CUDA_ERROR_NOT_SUPPORTED;
  return cuModuleLoadData(module, !ptx ? "" : ptx);
}

int32_t cudartGetModuleFromCubin(void **module, const void *fatbin_wrapper) {
  if (!module) return CUDA_ERROR_NOT_SUPPORTED;
  return cudartLoadModuleImage(module, fatbin_wrapper, 0);
}

int32_t cudartInterfaceFn2(void **context, int32_t device) { return devicePrimaryCtxRetain(context, device); }

int32_t cudartGetModuleFromCubinExt1(void **module, const void *fatbin_wrapper, void *arg3, void *arg4, uint32_t arg5) {
  (void)arg3;
  (void)arg4;
  (void)arg5;
  if (!module) return CUDA_ERROR_INVALID_VALUE;
  /* The CUDA runtime asks the driver to materialize header-only fatbin
   * records while bootstrapping libraries.  There is no executable PTX in
   * these records; keep a valid empty module handle so a later lookup can
   * report the ordinary missing-entry result. */
  return cudartLoadModuleImage(module, fatbin_wrapper, 1);
}

void cudartInterfaceFn7(uintptr_t) {}

int32_t cudartGetModuleFromCubinExt2(const void *fatbin_header, void **module, void *arg3, void *arg4, uint32_t arg5) {
  (void)arg3;
  (void)arg4;
  (void)arg5;
  if (!module) return CUDA_ERROR_NOT_SUPPORTED;
  return cudartLoadModuleImage(module, fatbin_header, 0);
}

int32_t cudartLoadCompilers() { return CUDA_SUCCESS; }

const unsigned char cudartInterfaceId[16] = {0x6b, 0xd5, 0xfb, 0x6c, 0x5b, 0xf4, 0xe7, 0x4a,
                                             0x89, 0x87, 0xd9, 0x39, 0x12, 0xfd, 0x9d, 0xf9};
const unsigned char cudartLoggingId[16] = {0xf8, 0xcf, 0xf9, 0x51, 0x21, 0x46, 0x8b, 0x4e, 0xb9, 0xe2, 0xfb, 0x46, 0x9e, 0x7c, 0x0d, 0xd9};
const unsigned char cudartHandshakeCheckId[16] = {0xd4, 0x08, 0x20, 0x55, 0xbd, 0xe6, 0x70, 0x4b,
                                                  0x8d, 0x34, 0xba, 0x12, 0x3c, 0x66, 0xe1, 0xf2};
const unsigned char toolsRuntimeCallbackHooksId[16] = {0xa0, 0x94, 0x79, 0x8c, 0x2e, 0x74, 0x2e, 0x74,
                                                       0x93, 0xf2, 0x08, 0x00, 0x20, 0x0c, 0x0a, 0x66};
const unsigned char toolsTlsId[16] = {0x42, 0xd8, 0x5a, 0x81, 0x23, 0xf6, 0xcb, 0x47, 0x82, 0x98, 0xf6, 0xe7, 0x8a, 0x3a, 0xec, 0xdc};
const unsigned char contextLocalStorageId[16] = {0xc6, 0x93, 0x33, 0x6e, 0x11, 0x21, 0xdf, 0x11,
                                                 0xa8, 0xc3, 0x68, 0xf3, 0x55, 0xd8, 0x95, 0x93};
const unsigned char contextChecksId[16] = {0x26, 0x3e, 0x88, 0x60, 0x7c, 0xd2, 0x61, 0x43, 0x92, 0xf6, 0xbb, 0xd5, 0x00, 0x6d, 0xfa, 0x7e};
const unsigned char driverMetadataInterfaceId[16] = {0x21, 0x31, 0x8c, 0x60, 0x97, 0x14, 0x32, 0x48,
                                                     0x8c, 0xa6, 0x41, 0xff, 0x73, 0x24, 0xc8, 0xf2};

uint32_t toolsRuntimeCallbackBuffer1[1024];
uint32_t toolsRuntimeCallbackBuffer2[14];

void toolsRuntimeCallbackGetBuffer1(void **pointer, size_t *size) {
  if (pointer) *pointer = toolsRuntimeCallbackBuffer1;
  if (size) *size = 1024;
}

void toolsRuntimeCallbackGetBuffer2(void **pointer, size_t *size) {
  if (pointer) *pointer = toolsRuntimeCallbackBuffer2;
  if (size) *size = 14;
}

const void *cudartInterfaceTable[13] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(13 * sizeof(void *))),
                                        reinterpret_cast<const void *>(cudartGetModuleFromCubin),
                                        reinterpret_cast<const void *>(cudartInterfaceFn2),
                                        nullptr,
                                        nullptr,
                                        nullptr,
                                        reinterpret_cast<const void *>(cudartGetModuleFromCubinExt1),
                                        reinterpret_cast<const void *>(cudartInterfaceFn7),
                                        reinterpret_cast<const void *>(cudartGetModuleFromCubinExt2),
                                        nullptr,
                                        nullptr,
                                        nullptr,
                                        reinterpret_cast<const void *>(cudartLoadCompilers)};

void cudartLog(const void *, int32_t, const char *, void *) {}

void cudartSetLogMode(uint32_t) {}

const void *cudartLoggingTable[3] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(3 * sizeof(void *))),
                                     reinterpret_cast<const void *>(cudartLog), reinterpret_cast<const void *>(cudartSetLogMode)};

/* CUDA libraries use this private table for a small typed query on their
 * opaque driver metadata.  It is a driver service, not a cuBLAS shim. */
int32_t driverMetadataQuery(void *object, uint64_t *result) {
  if (!result) return CUDA_ERROR_UNKNOWN;
  *result = 0;
  if (!object) return CUDA_SUCCESS;
  const auto kind = readNative<uint32_t>(object);
  if (kind == 0) {
    *result = readNative<uint32_t>(object, 0x60);
  } else if (kind == 1) {
    *result = readNative<uint64_t>(object, 0x60);
  }
  return CUDA_SUCCESS;
}

const std::array<const void *, 96> driverMetadataInterfaceTable = [] {
  std::array<const void *, 96> table{};
  table[0] = reinterpret_cast<const void *>(uintptr_t{0x300});
  table[4] = reinterpret_cast<const void *>(reinterpret_cast<uintptr_t>(driverMetadataQuery));
  return table;
}();

/* libcudart's private export-table handshake is an ABI compatibility transform,
 * not a Gemstone security or content-addressing mechanism. */
const uint8_t cudartHandshakeMixingTable[256] = {
    0x29, 0x2e, 0x43, 0xc9, 0xa2, 0xd8, 0x7c, 0x01, 0x3d, 0x36, 0x54, 0xa1, 0xec, 0xf0, 0x06, 0x13, 0x62, 0xa7, 0x05, 0xf3, 0xc0, 0xc7,
    0x73, 0x8c, 0x98, 0x93, 0x2b, 0xd9, 0xbc, 0x4c, 0x82, 0xca, 0x1e, 0x9b, 0x57, 0x3c, 0xfd, 0xd4, 0xe0, 0x16, 0x67, 0x42, 0x6f, 0x18,
    0x8a, 0x17, 0xe5, 0x12, 0xbe, 0x4e, 0xc4, 0xd6, 0xda, 0x9e, 0xde, 0x49, 0xa0, 0xfb, 0xf5, 0x8e, 0xbb, 0x2f, 0xee, 0x7a, 0xa9, 0x68,
    0x79, 0x91, 0x15, 0xb2, 0x07, 0x3f, 0x94, 0xc2, 0x10, 0x89, 0x0b, 0x22, 0x5f, 0x21, 0x80, 0x7f, 0x5d, 0x9a, 0x5a, 0x90, 0x32, 0x27,
    0x35, 0x3e, 0xcc, 0xe7, 0xbf, 0xf7, 0x97, 0x03, 0xff, 0x19, 0x30, 0xb3, 0x48, 0xa5, 0xb5, 0xd1, 0xd7, 0x5e, 0x92, 0x2a, 0xac, 0x56,
    0xaa, 0xc6, 0x4f, 0xb8, 0x38, 0xd2, 0x96, 0xa4, 0x7d, 0xb6, 0x76, 0xfc, 0x6b, 0xe2, 0x9c, 0x74, 0x04, 0xf1, 0x45, 0x9d, 0x70, 0x59,
    0x64, 0x71, 0x87, 0x20, 0x86, 0x5b, 0xcf, 0x65, 0xe6, 0x2d, 0xa8, 0x02, 0x1b, 0x60, 0x25, 0xad, 0xae, 0xb0, 0xb9, 0xf6, 0x1c, 0x46,
    0x61, 0x69, 0x34, 0x40, 0x7e, 0x0f, 0x55, 0x47, 0xa3, 0x23, 0xdd, 0x51, 0xaf, 0x3a, 0xc3, 0x5c, 0xf9, 0xce, 0xba, 0xc5, 0xea, 0x26,
    0x2c, 0x53, 0x0d, 0x6e, 0x85, 0x28, 0x84, 0x09, 0xd3, 0xdf, 0xcd, 0xf4, 0x41, 0x81, 0x4d, 0x52, 0x6a, 0xdc, 0x37, 0xc8, 0x6c, 0xc1,
    0xab, 0xfa, 0x24, 0xe1, 0x7b, 0x08, 0x0c, 0xbd, 0xb1, 0x4a, 0x78, 0x88, 0x95, 0x8b, 0xe3, 0x63, 0xe8, 0x6d, 0xe9, 0xcb, 0xd5, 0xfe,
    0x3b, 0x00, 0x1d, 0x39, 0xf2, 0xef, 0xb7, 0x0e, 0x66, 0x58, 0xd0, 0xe4, 0xa6, 0x77, 0x72, 0xf8, 0xeb, 0x75, 0x4b, 0x0a, 0x31, 0x44,
    0x50, 0xb4, 0x8f, 0xed, 0x1f, 0x1a, 0xdb, 0x99, 0x8d, 0x33, 0x9f, 0x11, 0x83, 0x14};

void cudartHandshakeSinglePass(uint8_t *value, uint8_t input) {
  uint8_t position = value[0x40];
  value[static_cast<size_t>(position) + 0x10] = input;
  const uint8_t next_position = static_cast<uint8_t>((position + 1) & 0x0f);
  value[static_cast<size_t>(position) + 0x20] = value[position] ^ input;
  const uint8_t mixed = cudartHandshakeMixingTable[static_cast<uint8_t>(input ^ value[0x41])];
  value[static_cast<size_t>(position) + 0x30] = mixed ^ value[static_cast<size_t>(position) + 0x30];
  value[0x41] = value[static_cast<size_t>(position) + 0x30];
  value[0x40] = next_position;
  if (next_position != 0) return;
  uint8_t accumulator = 0x29;
  uint8_t counter = 0;
  for (;;) {
    accumulator ^= value[0];
    value[0] = accumulator;
    for (size_t index = 1; index < 0x30; ++index) {
      accumulator = value[index] ^ cudartHandshakeMixingTable[accumulator];
      value[index] = accumulator;
    }
    accumulator = static_cast<uint8_t>(accumulator + counter);
    ++counter;
    if (counter == 0x12) break;
    accumulator = cudartHandshakeMixingTable[accumulator];
  }
}

void cudartHandshakeMixBytes(uint8_t *value, const void *input, size_t size, uint8_t xor_mask) {
  const uint8_t *bytes = static_cast<const uint8_t *>(input);
  for (size_t index = 0; index < size; ++index) {
    cudartHandshakeSinglePass(value, static_cast<uint8_t>(bytes[index] ^ xor_mask));
  }
}

struct CudartHandshakeInput {
  uint32_t driver_version;
  uint32_t version;
  uint32_t current_process;
  uint32_t current_thread;
  const void *cudart_table;
  const void *handshake_table;
  const void *handshake_function;
  uint64_t unix_seconds;
};

struct CudartHandshakeDevice {
  uint8_t uuid[16];
  int32_t pci_domain;
  int32_t pci_bus;
  int32_t pci_device;
};

extern const void *cudartHandshakeCheckTable[3];

void cudartHandshakeZeroResult(uint8_t *value) {
  std::fill_n(value, 16, uint8_t{0});
  std::fill_n(value + 48, 18, uint8_t{0});
}

void cudartHandshakeTransform(uint32_t version, uint64_t unix_seconds, uint32_t driver_version, const void *handshake_table,
                              const void *handshake_function, uint8_t output[16]) {
  static const uint8_t pass1_result[16] = {0x14, 0x6a, 0xdd, 0xae, 0x53, 0xa9, 0xa7, 0x52, 0xaa, 0x08, 0x41, 0x36, 0x0b, 0xf5, 0x5a, 0x9f};
  uint8_t value[66] = {0};
  cudartHandshakeMixBytes(value, pass1_result, sizeof(pass1_result), 0x36);
  const CudartHandshakeInput input = {driver_version,
                                      version,
                                      static_cast<uint32_t>(getpid()),
                                      static_cast<uint32_t>(static_cast<uintptr_t>(pthread_self())),
                                      cudartInterfaceTable,
                                      handshake_table,
                                      handshake_function,
                                      unix_seconds};
  cudartHandshakeMixBytes(value, &input, sizeof(input), 0);
  const CudartHandshakeDevice device = {
      {0x47, 0x45, 0x4d, 0x53, 0x54, 0x4f, 0x4e, 0x45, 0x2d, 0x43, 0x55, 0x44, 0x41, 0x2d, 0x30, 0x31}, 0, 1, 1};
  cudartHandshakeMixBytes(value, &device, sizeof(device), 0);
  uint8_t pass5_result[16];
  uint8_t padding = static_cast<uint8_t>(16 - value[64]);
  for (uint8_t index = 0; index < padding; ++index)
    cudartHandshakeSinglePass(value, padding);
  for (uint8_t *cursor = value + 48; cursor < value + 64; ++cursor) {
    cudartHandshakeSinglePass(value, *cursor);
  }
  memcpy(pass5_result, value, sizeof(pass5_result));
  cudartHandshakeZeroResult(value);
  cudartHandshakeMixBytes(value, pass1_result, sizeof(pass1_result), 0x5c);
  cudartHandshakeMixBytes(value, pass5_result, sizeof(pass5_result), 0);
  padding = static_cast<uint8_t>(16 - value[64]);
  for (uint8_t index = 0; index < padding; ++index)
    cudartHandshakeSinglePass(value, padding);
  for (uint8_t *cursor = value + 48; cursor < value + 64; ++cursor) {
    cudartHandshakeSinglePass(value, *cursor);
  }
  memcpy(output, value, sizeof(pass5_result));
}

int32_t cudartHandshakeCheck(uint32_t version, uint64_t unix_seconds, uint64_t *result) {
  if (!result) return CUDA_ERROR_INVALID_VALUE;
  if (version % 10 == 0) {
    result[0] = UINT64_C(0x3341181c03cb675c);
    result[1] = UINT64_C(0x8ed383aa1f4cd1e8);
    return CUDA_SUCCESS;
  }
  if (version % 10 == 1) {
    result[0] = UINT64_C(0x1841181c03cb675c);
    result[1] = UINT64_C(0x8ed383aa1f4cd1e8);
    return CUDA_SUCCESS;
  }
  cudartHandshakeTransform(version, unix_seconds, 13030, cudartHandshakeCheckTable,
                           reinterpret_cast<const void *>(reinterpret_cast<uintptr_t>(cudartHandshakeCheck)),
                           reinterpret_cast<uint8_t *>(result));
  return CUDA_SUCCESS;
}

const void *cudartHandshakeCheckTable[3] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(3 * sizeof(void *))),
                                            reinterpret_cast<const void *>(cudartHandshakeCheck), nullptr};

const void *toolsRuntimeCallbackHooksTable[7] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(7 * sizeof(void *))),
                                                 nullptr,
                                                 reinterpret_cast<const void *>(toolsRuntimeCallbackGetBuffer1),
                                                 nullptr,
                                                 nullptr,
                                                 nullptr,
                                                 reinterpret_cast<const void *>(toolsRuntimeCallbackGetBuffer2)};

int32_t toolsTlsGet(void **value, void *arg2, const void *table, void *arg4, void *arg5, void *arg6) {
  (void)arg2;
  (void)table;
  (void)arg4;
  (void)arg5;
  (void)arg6;
  if (!value) return CUDA_ERROR_UNKNOWN;
  *value = nullptr;
  return CUDA_SUCCESS;
}

const void *toolsTlsTable[4] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(4 * sizeof(void *))), nullptr,
                                reinterpret_cast<const void *>(toolsTlsGet), nullptr};

using ContextStorageDestructor = void (*)(void *, void *, void *);
struct ContextStorage {
  void *context;
  void *key;
  void *value;
  ContextStorageDestructor destructor;
};
ContextStorage contextStorage[64];

void *storageContext(void *context) { return !context ? currentContext : context; }

int32_t contextLocalStoragePut(void *context, void *key, void *value, ContextStorageDestructor destructor) {
  context = storageContext(context);
  if (!context || !key) return CUDA_ERROR_INVALID_VALUE;
  for (size_t index = 0; index < sizeof(contextStorage) / sizeof(contextStorage[0]); ++index) {
    ContextStorage *entry = &contextStorage[index];
    if (entry->context == context && entry->key == key) {
      entry->value = value;
      entry->destructor = destructor;
      return CUDA_SUCCESS;
    }
  }
  for (size_t index = 0; index < sizeof(contextStorage) / sizeof(contextStorage[0]); ++index) {
    ContextStorage *entry = &contextStorage[index];
    if (!entry->context) {
      entry->context = context;
      entry->key = key;
      entry->value = value;
      entry->destructor = destructor;
      return CUDA_SUCCESS;
    }
  }
  return CUDA_ERROR_UNKNOWN;
}

int32_t contextLocalStorageDelete(void *context, void *key) {
  context = storageContext(context);
  if (!context || !key) return CUDA_ERROR_INVALID_VALUE;
  for (size_t index = 0; index < sizeof(contextStorage) / sizeof(contextStorage[0]); ++index) {
    ContextStorage *entry = &contextStorage[index];
    if (entry->context == context && entry->key == key) {
      *entry = {};
      return CUDA_SUCCESS;
    }
  }
  return CUDA_SUCCESS;
}

int32_t contextLocalStorageGet(void **value, void *context, void *key) {
  context = storageContext(context);
  if (!value || !key) {
    return CUDA_ERROR_INVALID_VALUE;
  }
  if (!context) return CUDA_ERROR_INVALID_CONTEXT;
  for (size_t index = 0; index < sizeof(contextStorage) / sizeof(contextStorage[0]); ++index) {
    ContextStorage *entry = &contextStorage[index];
    if (entry->context == context && entry->key == key) {
      *value = entry->value;
      return CUDA_SUCCESS;
    }
  }
  return CUDA_ERROR_INVALID_HANDLE;
}

const void *contextLocalStorageTable[4] = {reinterpret_cast<const void *>(contextLocalStoragePut),
                                           reinterpret_cast<const void *>(contextLocalStorageDelete),
                                           reinterpret_cast<const void *>(contextLocalStorageGet), nullptr};

int32_t contextCheck(void *context, uint32_t *result1, const void **result2) {
  (void)context;
  (void)result2;
  if (!result1) return CUDA_ERROR_INVALID_VALUE;
  *result1 = 0;
  return CUDA_SUCCESS;
}

uint32_t contextCheckFn3() { return 0; }

const void *contextChecksTable[4] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(4 * sizeof(void *))), nullptr,
                                     reinterpret_cast<const void *>(contextCheck), reinterpret_cast<const void *>(contextCheckFn3)};

} // namespace

/* These entry points are consumed through both cuGetProcAddress and ordinary
 * dynamic symbol lookup. Keep the implementation details internal, but expose
 * one canonical ABI function so both lookup paths have identical behavior. */
extern "C" int32_t cuMemAllocAsync(uint64_t *pointer, size_t size, void *stream) { return memAllocAsync(pointer, size, stream); }

extern "C" int32_t cuMemAllocFromPoolAsync(uint64_t *pointer, size_t size, void *pool, void *stream) {
  return memAllocFromPoolAsync(pointer, size, pool, stream);
}

extern "C" int32_t cuMemFreeAsync(uint64_t pointer, void *stream) { return memFreeAsync(pointer, stream); }

extern "C" int32_t cuMemAllocPitch_v2(uint64_t *pointer, size_t *pitch, size_t width, size_t height, uint32_t element_size) {
  return memAllocPitchV2(pointer, pitch, width, height, element_size);
}

extern "C" int32_t cuMemcpyHtoDAsync_v2(uint64_t destination, const void *source, size_t size, void *stream) {
  return memcpyHtoDAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemcpyDtoHAsync_v2(void *destination, uint64_t source, size_t size, void *stream) {
  return memcpyDtoHAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemcpyDtoDAsync_v2(uint64_t destination, uint64_t source, size_t size, void *stream) {
  return memcpyDtoDAsyncV2(destination, source, size, stream);
}

extern "C" int32_t cuMemsetD8Async(uint64_t destination, uint8_t value, size_t count, void *stream) {
  return memsetD8Async(destination, value, count, stream);
}

extern "C" int32_t cuStreamSynchronize(void *stream) { return streamSynchronize(stream); }

extern "C" int32_t cuStreamQuery(void *stream) { return streamQuery(stream); }

extern "C" int32_t cuEventCreate(void **event, uint32_t flags) { return eventCreate(event, flags); }

extern "C" int32_t cuEventDestroy_v2(void *event) { return eventDestroy(event); }

extern "C" int32_t cuEventElapsedTime_v2(float *milliseconds, void *start, void *end) { return eventElapsedTime(milliseconds, start, end); }

extern "C" int32_t cuEventQuery(void *event) { return eventQuery(event); }

extern "C" int32_t cuEventRecord(void *event, void *stream) { return eventRecord(event, stream); }

extern "C" int32_t cuEventRecordWithFlags(void *event, void *stream, uint32_t flags) { return eventRecordWithFlags(event, stream, flags); }

extern "C" int32_t cuEventSynchronize(void *event) { return eventSynchronize(event); }

extern "C" int32_t cuStreamWaitEvent(void *stream, void *event, uint32_t flags) { return streamWaitEvent(stream, event, flags); }

extern "C" int32_t cuModuleGetTexRef(void **reference, void *module, const char *name) { return moduleGetTexRef(reference, module, name); }

extern "C" int32_t cuModuleGetSurfRef(void **reference, void *module, const char *name) {
  return moduleGetSurfRef(reference, module, name);
}

extern "C" int32_t cuModuleGetLoadingMode(int32_t *mode) {
  if (!mode) return CUDA_ERROR_INVALID_VALUE;
  *mode = 1; // CU_MODULE_EAGER_LOADING
  return CUDA_SUCCESS;
}

extern "C" int32_t cuLibraryLoadData(void **library, const void *code, int32_t *jit_options, void **jit_option_values,
                                     uint32_t num_jit_options, int32_t *library_options, void **library_option_values,
                                     uint32_t num_library_options) {
  return libraryLoadData(library, code, jit_options, jit_option_values, num_jit_options, library_options, library_option_values,
                         num_library_options);
}

extern "C" int32_t cuLibraryGetKernel(void **kernel, void *library, const char *name) { return libraryGetKernel(kernel, library, name); }

extern "C" int32_t cuLibraryGetModule(void **module, void *library) { return libraryGetModule(module, library); }

extern "C" int32_t cuLibraryUnload(void *library) { return libraryUnload(library); }

extern "C" int32_t cuDevSmResourceSplit(void *result, uint32_t group_count, const void *input, void *remainder, uint32_t flags,
                                        void *group_params) {
  return devSmResourceSplit(result, group_count, input, remainder, flags, group_params);
}

extern "C" GEMSTONE_PUBLIC_API int32_t cuGetExportTable(const void **table, const void *table_id) {
  if (!table || !table_id) return CUDA_ERROR_INVALID_VALUE;
  if (memcmp(table_id, cudartLoggingId, sizeof(cudartLoggingId)) == 0) {
    *table = cudartLoggingTable;
    return CUDA_SUCCESS;
  }
  if (memcmp(table_id, cudartInterfaceId, sizeof(cudartInterfaceId)) == 0) {
    *table = cudartInterfaceTable;
    return CUDA_SUCCESS;
  }
  if (memcmp(table_id, cudartHandshakeCheckId, sizeof(cudartHandshakeCheckId)) == 0) {
    *table = cudartHandshakeCheckTable;
    return CUDA_SUCCESS;
  }
  if (memcmp(table_id, toolsRuntimeCallbackHooksId, sizeof(toolsRuntimeCallbackHooksId)) == 0) {
    *table = toolsRuntimeCallbackHooksTable;
    return CUDA_SUCCESS;
  }
  if (memcmp(table_id, toolsTlsId, sizeof(toolsTlsId)) == 0) {
    *table = toolsTlsTable;
    return CUDA_SUCCESS;
  }
  if (memcmp(table_id, contextLocalStorageId, sizeof(contextLocalStorageId)) == 0) {
    *table = contextLocalStorageTable;
    return CUDA_SUCCESS;
  }
  if (memcmp(table_id, contextChecksId, sizeof(contextChecksId)) == 0) {
    *table = contextChecksTable;
    return CUDA_SUCCESS;
  }
  if (memcmp(table_id, driverMetadataInterfaceId, sizeof(driverMetadataInterfaceId)) == 0) {
    *table = driverMetadataInterfaceTable.data();
    return CUDA_SUCCESS;
  }
  return CUDA_ERROR_INVALID_VALUE;
}

extern "C" GEMSTONE_PUBLIC_API int32_t GEMSTONE_CUDA_API cuGetProcAddress_v2(const char *symbol, void **pfn, int32_t cuda_version,
                                                                             uint64_t flags, int32_t *symbol_status) {
  (void)flags;
  if (!symbol || !pfn) return CUDA_ERROR_INVALID_VALUE;
  /* CUDA's loader uses an empty spelling as a harmless capability probe.
   * Match the vendor driver: it is a successful lookup with no function,
   * rather than CUDA_ERROR_NOT_FOUND. */
  if (symbol[0] == '\0') {
    *pfn = nullptr;
    if (symbol_status) *symbol_status = 1;
    return CUDA_SUCCESS;
  }
  if (strcmp(symbol, "cuLaunchKernel") == 0) {
    *pfn = reinterpret_cast<void *>(cuLaunchKernel);
    if (symbol_status) *symbol_status = 0;
    return CUDA_SUCCESS;
  }
  if (strcmp(symbol, "cuLaunchKernel_ptsz") == 0) {
    *pfn = reinterpret_cast<void *>(cuLaunchKernel_ptsz);
    if (symbol_status) *symbol_status = 0;
    return CUDA_SUCCESS;
  }
#define GEMSTONE_CUDA_PROC(name, function)                                                                                                 \
  if (strcmp(symbol, name) == 0) {                                                                                                         \
    *pfn = reinterpret_cast<void *>(function);                                                                                             \
    if (symbol_status) *symbol_status = 0;                                                                                                 \
    return CUDA_SUCCESS;                                                                                                                   \
  }
#define GEMSTONE_CUDA_VERSIONED_PROC(name, legacy, modern)                                                                                 \
  if (strcmp(symbol, name) == 0) {                                                                                                         \
    if (cuda_version > 0 && cuda_version < 3020) *pfn = reinterpret_cast<void *>(legacy);                                                  \
    else *pfn = reinterpret_cast<void *>(modern);                                                                                          \
    if (symbol_status) *symbol_status = 0;                                                                                                 \
    return CUDA_SUCCESS;                                                                                                                   \
  }
  GEMSTONE_CUDA_PROC("cuInit", cuInit)
  GEMSTONE_CUDA_PROC("cuDriverGetVersion", cuDriverGetVersion)
  GEMSTONE_CUDA_PROC("cuDeviceGetCount", cuDeviceGetCount)
  GEMSTONE_CUDA_PROC("cuDeviceGet", cuDeviceGet)
  GEMSTONE_CUDA_VERSIONED_PROC("cuDeviceTotalMem", cuDeviceTotalMem, cuDeviceTotalMem_v2)
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
  GEMSTONE_CUDA_PROC("cuDeviceGetAttribute", deviceGetAttribute)
  GEMSTONE_CUDA_PROC("cuDeviceGetByPCIBusId", deviceGetByPciBusId)
  GEMSTONE_CUDA_PROC("cuDeviceGetName", deviceGetName)
  GEMSTONE_CUDA_PROC("cuDeviceGetP2PAttribute", deviceGetP2PAttribute)
  GEMSTONE_CUDA_PROC("cuDeviceCanAccessPeer", deviceCanAccessPeer)
  GEMSTONE_CUDA_PROC("cuDeviceGetHostAtomicCapabilities", cuDeviceGetHostAtomicCapabilities)
  GEMSTONE_CUDA_PROC("cuDeviceGetP2PAtomicCapabilities", cuDeviceGetP2PAtomicCapabilities)
  GEMSTONE_CUDA_PROC("cuDeviceGetTexture1DLinearMaxWidth", deviceGetTexture1DLinearMaxWidth)
  GEMSTONE_CUDA_PROC("cuDeviceGetDefaultMemPool", deviceGetDefaultMemPool)
  GEMSTONE_CUDA_PROC("cuDeviceGetMemPool", deviceGetMemPool)
  GEMSTONE_CUDA_PROC("cuDeviceSetMemPool", deviceSetMemPool)
  GEMSTONE_CUDA_PROC("cuMemGetDefaultMemPool", memGetDefaultMemPool)
  GEMSTONE_CUDA_PROC("cuMemGetMemPool", memGetMemPool)
  GEMSTONE_CUDA_PROC("cuMemSetMemPool", memSetMemPool)
  GEMSTONE_CUDA_PROC("cuDeviceComputeCapability", deviceComputeCapability)
  GEMSTONE_CUDA_PROC("cuDeviceGetUuid", deviceGetUuid)
  GEMSTONE_CUDA_PROC("cuDeviceGetUuid_v2", deviceGetUuid)
  GEMSTONE_CUDA_PROC("cuDeviceGetPCIBusId", deviceGetPciBusId)
  GEMSTONE_CUDA_PROC("cuCtxCreate", cuCtxCreate)
  GEMSTONE_CUDA_PROC("cuCtxCreate_v2", cuCtxCreate_v2)
  GEMSTONE_CUDA_PROC("cuCtxCreate_v4", cuCtxCreate_v4)
  GEMSTONE_CUDA_PROC("cuCtxDestroy", cuCtxDestroy_v2)
  GEMSTONE_CUDA_PROC("cuCtxDestroy_v2", cuCtxDestroy_v2)
  GEMSTONE_CUDA_PROC("cuCtxSynchronize", cuCtxSynchronize)
  GEMSTONE_CUDA_PROC("cuCtxSynchronize_v2", ctxSynchronizeV2)
  GEMSTONE_CUDA_PROC("cuCtxGetCacheConfig", ctxGetCacheConfig)
  GEMSTONE_CUDA_PROC("cuCtxSetCacheConfig", ctxSetCacheConfig)
  GEMSTONE_CUDA_PROC("cuCtxGetSharedMemConfig", ctxGetSharedMemConfig)
  GEMSTONE_CUDA_PROC("cuCtxSetSharedMemConfig", ctxSetSharedMemConfig)
  GEMSTONE_CUDA_PROC("cuCtxGetStreamPriorityRange", ctxGetStreamPriorityRange)
  GEMSTONE_CUDA_PROC("cuCtxResetPersistingL2Cache", ctxResetPersistingL2Cache)
  GEMSTONE_CUDA_PROC("cuCtxGetFlags", ctxGetFlags)
  GEMSTONE_CUDA_PROC("cuCtxGetApiVersion", ctxGetApiVersion)
  GEMSTONE_CUDA_PROC("cuCtxGetLimit", ctxGetLimit)
  GEMSTONE_CUDA_PROC("cuCtxSetLimit", ctxSetLimit)
  GEMSTONE_CUDA_PROC("cuCtxDetach", ctxDetach)
  GEMSTONE_CUDA_PROC("cuCtxGetCurrent", ctxGetCurrent)
  GEMSTONE_CUDA_PROC("cuCtxGetDevice", ctxGetDevice)
  GEMSTONE_CUDA_PROC("cuCtxGetDevice_v2", ctxGetDeviceV2)
  GEMSTONE_CUDA_PROC("cuCtxSetCurrent", ctxSetCurrent)
  GEMSTONE_CUDA_PROC("cuCtxPushCurrent", ctxPushCurrent)
  GEMSTONE_CUDA_PROC("cuCtxPushCurrent_v2", cuCtxPushCurrent_v2)
  GEMSTONE_CUDA_PROC("cuCtxPopCurrent", ctxPopCurrent)
  GEMSTONE_CUDA_PROC("cuCtxPopCurrent_v2", cuCtxPopCurrent_v2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxRetain", devicePrimaryCtxRetain)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxRelease", devicePrimaryCtxReleaseV2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxRelease_v2", devicePrimaryCtxReleaseV2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxReset", devicePrimaryCtxResetV2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxReset_v2", devicePrimaryCtxResetV2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxGetState", devicePrimaryCtxGetState)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxSetFlags", devicePrimaryCtxSetFlagsV2)
  GEMSTONE_CUDA_PROC("cuDevicePrimaryCtxSetFlags_v2", devicePrimaryCtxSetFlagsV2)
  GEMSTONE_CUDA_PROC("cuMemAlloc", cuMemAlloc_v2)
  GEMSTONE_CUDA_PROC("cuMemAlloc_v2", cuMemAlloc_v2)
  GEMSTONE_CUDA_PROC("cuMemFree", cuMemFree_v2)
  GEMSTONE_CUDA_PROC("cuMemFree_v2", cuMemFree_v2)
  GEMSTONE_CUDA_PROC("cuMemAllocManaged", memAllocManaged)
  GEMSTONE_CUDA_PROC("cuMemAllocHost", cuMemAllocHost)
  GEMSTONE_CUDA_PROC("cuMemAllocHost_v2", cuMemAllocHost_v2)
  GEMSTONE_CUDA_PROC("cuMemFreeHost", cuMemFreeHost)
  GEMSTONE_CUDA_PROC("cuMemHostAlloc", cuMemHostAlloc)
  GEMSTONE_CUDA_PROC("cuMemHostGetDevicePointer", cuMemHostGetDevicePointer_v2)
  GEMSTONE_CUDA_PROC("cuMemHostGetDevicePointer_v2", cuMemHostGetDevicePointer_v2)
  GEMSTONE_CUDA_PROC("cuMemHostGetFlags", cuMemHostGetFlags)
  GEMSTONE_CUDA_PROC("cuMemAllocAsync", cuMemAllocAsync)
  GEMSTONE_CUDA_PROC("cuMemAllocFromPoolAsync", cuMemAllocFromPoolAsync)
  GEMSTONE_CUDA_PROC("cuMemFreeAsync", cuMemFreeAsync)
  GEMSTONE_CUDA_PROC("cuMemPoolCreate", cuMemPoolCreate)
  GEMSTONE_CUDA_PROC("cuMemPoolDestroy", cuMemPoolDestroy)
  GEMSTONE_CUDA_PROC("cuMemPoolGetAttribute", cuMemPoolGetAttribute)
  GEMSTONE_CUDA_PROC("cuMemPoolSetAttribute", cuMemPoolSetAttribute)
  GEMSTONE_CUDA_PROC("cuMemPoolTrimTo", cuMemPoolTrimTo)
  GEMSTONE_CUDA_PROC("cuMemAllocPitch", cuMemAllocPitch_v2)
  GEMSTONE_CUDA_PROC("cuMemAllocPitch_v2", cuMemAllocPitch_v2)
  GEMSTONE_CUDA_PROC("cuMemGetInfo", memGetInfoV2)
  GEMSTONE_CUDA_PROC("cuMemGetInfo_v2", memGetInfoV2)
  GEMSTONE_CUDA_PROC("cuPointerGetAttribute", cuPointerGetAttribute)
  GEMSTONE_CUDA_PROC("cuPointerGetAttributes", cuPointerGetAttributes)
  GEMSTONE_CUDA_PROC("cuFlushGPUDirectRDMAWrites", cuFlushGPUDirectRDMAWrites)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoD", cuMemcpyHtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoD_v2", cuMemcpyHtoD_v2)
  /* CUDA 13's runtime resolves the per-thread-default-stream spelling for
   * synchronous copies through cuGetProcAddress.  The _ptds entry points have
   * the same ABI and semantics as their ordinary driver counterparts; leaving
   * them absent makes cudart translate the lookup miss into
   * cudaErrorCallRequiresNewerDriver before it ever issues the RPC. */
  GEMSTONE_CUDA_PROC("cuMemcpyHtoD_ptds", cuMemcpyHtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoD_v2_ptds", cuMemcpyHtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoH", cuMemcpyDtoH_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoH_v2", cuMemcpyDtoH_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoH_ptds", cuMemcpyDtoH_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoH_v2_ptds", cuMemcpyDtoH_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoD", cuMemcpyDtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoD_v2", cuMemcpyDtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoD_ptds", cuMemcpyDtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoD_v2_ptds", cuMemcpyDtoD_v2)
  GEMSTONE_CUDA_PROC("cuMemcpy", cuMemcpy)
  GEMSTONE_CUDA_PROC("cuMemcpy_ptds", cuMemcpy_ptds)
  GEMSTONE_CUDA_PROC("cuMemcpyAsync", cuMemcpyAsync)
  GEMSTONE_CUDA_PROC("cuMemcpyAsync_ptsz", cuMemcpyAsync_ptsz)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoDAsync_ptsz", memcpyHtoDAsyncV2)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoDAsync_v2_ptsz", memcpyHtoDAsyncV2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoHAsync_ptsz", memcpyDtoHAsyncV2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoHAsync_v2_ptsz", memcpyDtoHAsyncV2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoDAsync_ptsz", memcpyDtoDAsyncV2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoDAsync_v2_ptsz", memcpyDtoDAsyncV2)
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
  GEMSTONE_CUDA_PROC("cuFuncSetAttribute", cuFuncSetAttribute)
  GEMSTONE_CUDA_PROC("cuFuncSetCacheConfig", cuFuncSetCacheConfig)
  GEMSTONE_CUDA_PROC("cuFuncSetSharedMemConfig", cuFuncSetSharedMemConfig)
  GEMSTONE_CUDA_PROC("cuKernelGetAttribute", cuKernelGetAttribute)
  GEMSTONE_CUDA_PROC("cuOccupancyMaxActiveBlocksPerMultiprocessor", cuOccupancyMaxActiveBlocksPerMultiprocessor)
  GEMSTONE_CUDA_PROC("cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags", cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoDAsync", cuMemcpyHtoDAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyHtoDAsync_v2", cuMemcpyHtoDAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoHAsync", cuMemcpyDtoHAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoHAsync_v2", cuMemcpyDtoHAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoDAsync", cuMemcpyDtoDAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemcpyDtoDAsync_v2", cuMemcpyDtoDAsync_v2)
  GEMSTONE_CUDA_PROC("cuMemsetD8", cuMemsetD8_v2)
  GEMSTONE_CUDA_PROC("cuMemsetD8_v2", cuMemsetD8_v2)
  GEMSTONE_CUDA_PROC("cuMemsetD8_ptds", cuMemsetD8_v2)
  GEMSTONE_CUDA_PROC("cuMemsetD8_v2_ptds", cuMemsetD8_v2)
  GEMSTONE_CUDA_PROC("cuMemsetD8Async", cuMemsetD8Async)
  GEMSTONE_CUDA_PROC("cuStreamSynchronize", cuStreamSynchronize)
  GEMSTONE_CUDA_PROC("cuStreamQuery", cuStreamQuery)
  GEMSTONE_CUDA_PROC("cuEventCreate", cuEventCreate)
  GEMSTONE_CUDA_PROC("cuEventDestroy", cuEventDestroy_v2)
  GEMSTONE_CUDA_PROC("cuEventElapsedTime", cuEventElapsedTime_v2)
  GEMSTONE_CUDA_PROC("cuEventQuery", cuEventQuery)
  GEMSTONE_CUDA_PROC("cuEventRecord", cuEventRecord)
  GEMSTONE_CUDA_PROC("cuEventRecordWithFlags", cuEventRecordWithFlags)
  GEMSTONE_CUDA_PROC("cuEventSynchronize", cuEventSynchronize)
  GEMSTONE_CUDA_PROC("cuStreamWaitEvent", cuStreamWaitEvent)
  GEMSTONE_CUDA_PROC("cuStreamCreate", cuStreamCreate)
  GEMSTONE_CUDA_PROC("cuStreamCreateWithPriority", cuStreamCreateWithPriority)
  GEMSTONE_CUDA_PROC("cuStreamDestroy", cuStreamDestroy)
  GEMSTONE_CUDA_PROC("cuStreamDestroy_v2", cuStreamDestroy_v2)
  GEMSTONE_CUDA_PROC("cuStreamAddCallback", cuStreamAddCallback)
  GEMSTONE_CUDA_PROC("cuLaunchHostFunc", cuLaunchHostFunc)
  GEMSTONE_CUDA_PROC("cuModuleLoadData", cuModuleLoadData)
  GEMSTONE_CUDA_PROC("cuModuleLoadDataEx", cuModuleLoadDataEx)
  GEMSTONE_CUDA_PROC("cuModuleLoadFatBinary", cuModuleLoadFatBinary)
  GEMSTONE_CUDA_PROC("cuModuleLoad", cuModuleLoad)
  GEMSTONE_CUDA_PROC("cuModuleGetFunction", cuModuleGetFunction)
  GEMSTONE_CUDA_PROC("cuModuleGetTexRef", cuModuleGetTexRef)
  GEMSTONE_CUDA_PROC("cuModuleGetSurfRef", cuModuleGetSurfRef)
  GEMSTONE_CUDA_PROC("cuModuleGetLoadingMode", cuModuleGetLoadingMode)
  GEMSTONE_CUDA_VERSIONED_PROC("cuModuleGetGlobal", cuModuleGetGlobal, cuModuleGetGlobal_v2)
  GEMSTONE_CUDA_PROC("cuModuleGetGlobal_v2", cuModuleGetGlobal_v2)
  GEMSTONE_CUDA_PROC("cuModuleUnload", cuModuleUnload)
  GEMSTONE_CUDA_PROC("cuLibraryLoadData", cuLibraryLoadData)
  GEMSTONE_CUDA_PROC("cuLibraryGetKernel", cuLibraryGetKernel)
  GEMSTONE_CUDA_PROC("cuKernelGetFunction", cuKernelGetFunction)
  GEMSTONE_CUDA_PROC("cuLibraryGetGlobal", cuLibraryGetGlobal)
  GEMSTONE_CUDA_PROC("cuLibraryGetModule", cuLibraryGetModule)
  GEMSTONE_CUDA_PROC("cuLibraryGetManaged", cuLibraryGetManaged)
  GEMSTONE_CUDA_PROC("cuLibraryUnload", cuLibraryUnload)
  GEMSTONE_CUDA_PROC("cuLaunchKernel", cuLaunchKernel)
  GEMSTONE_CUDA_PROC("cuLaunchKernel_ptsz", cuLaunchKernel_ptsz)
  GEMSTONE_CUDA_PROC("cuLaunchKernelEx", cuLaunchKernelEx)
  GEMSTONE_CUDA_PROC("cuGetErrorString", getErrorString)
  GEMSTONE_CUDA_PROC("cuGetErrorName", getErrorName)
  GEMSTONE_CUDA_PROC("cuGetExportTable", cuGetExportTable)
  GEMSTONE_CUDA_PROC("cuDevSmResourceSplit", cuDevSmResourceSplit)
  GEMSTONE_CUDA_PROC("cuGetProcAddress", cuGetProcAddress)
  GEMSTONE_CUDA_PROC("cuGetProcAddress_v2", cuGetProcAddress_v2)
#undef GEMSTONE_CUDA_PROC
#undef GEMSTONE_CUDA_VERSIONED_PROC
  *pfn = nullptr;
  if (symbol_status) *symbol_status = 1;
  return CUDA_ERROR_NOT_FOUND;
}

using namespace gemstone::execution;

namespace {

using namespace aspartame;

thread_local void *currentContext = nullptr;

struct Global {
  std::string name;
  uint64_t address{};
  uint64_t allocation{};
  size_t size{};
};

struct Module {
  uint64_t program{};
  void *context{};
  std::mutex mutex;
  std::vector<std::unique_ptr<Global>> globals;
};

struct Function {
  Module *module{};
  std::string name;
  std::vector<KernelParameterMetadata> parameters;
};

struct Allocation {
  uint64_t address{};
};

std::once_flag clientOnce;
std::mutex stateMutex;
std::unique_ptr<Client> client;
std::vector<std::unique_ptr<Module>> modules;
std::vector<std::unique_ptr<Function>> functions;
std::vector<Allocation> allocations;
std::atomic<uint64_t> nextContext{1};
uint64_t nextDeviceAddress = UINT64_C(0x0000400000000000);

int32_t executionReady() noexcept {
  try {
    std::call_once(clientOnce, [] { client = Client::fromEnvironment(); });
    return client ? CUDA_SUCCESS : CUDA_ERROR_NOT_INITIALIZED;
  } catch (...) {
    return CUDA_ERROR_NOT_INITIALIZED;
  }
}

bool registerAllocation(size_t size, AllocationKind kind, uint64_t &id) noexcept {
  try {
    auto result = client->call(RegisterAllocation{kind, static_cast<int64_t>(size)});
    if (!result) return false;
    id = static_cast<uint64_t>(result.value().value);
    return true;
  } catch (...) {
    return false;
  }
}

bool registerAddress(uint64_t id, uint64_t address) noexcept {
  try {
    return static_cast<bool>(
        client->call(RegisterAllocationAddress{AllocationId{static_cast<int64_t>(id)}, GuestAddress{static_cast<int64_t>(address)}}));
  } catch (...) {
    return false;
  }
}

bool releaseAllocation(uint64_t id) noexcept {
  try {
    return static_cast<bool>(client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(id)}}));
  } catch (...) {
    return false;
  }
}

bool writeAllocation(uint64_t id, uint64_t offset, const void *bytes, size_t size) noexcept {
  try {
    constexpr size_t ChunkLimit = MaximumPayload / 2;
    const auto *source = static_cast<const uint8_t *>(bytes);
    while (size != 0) {
      const auto chunk = std::min(size, ChunkLimit);
      if (!client->call(WriteAllocation{AllocationId{static_cast<int64_t>(id)}, static_cast<int64_t>(offset),
                                        std::vector<uint8_t>(source, source + chunk)}))
        return false;
      source += chunk;
      offset += chunk;
      size -= chunk;
    }
    return true;
  } catch (...) {
    return false;
  }
}

bool readAllocation(uint64_t id, uint64_t offset, void *bytes, size_t size) noexcept {
  try {
    constexpr size_t ChunkLimit = MaximumPayload / 2;
    auto *destination = static_cast<uint8_t *>(bytes);
    while (size != 0) {
      const auto chunk = std::min(size, ChunkLimit);
      auto result =
          client->call(ReadAllocation{AllocationId{static_cast<int64_t>(id)}, static_cast<int64_t>(offset), static_cast<int32_t>(chunk)});
      if (!result || result.value().size() != chunk) return false;
      std::memcpy(destination, result.value().data(), chunk);
      destination += chunk;
      offset += chunk;
      size -= chunk;
    }
    return true;
  } catch (...) {
    return false;
  }
}

bool systemPagingEnabled() noexcept { return std::getenv("GEMSTONE_CUDA_SYSTEM_PAGING") != nullptr; }

struct HostPagingLease {
  uint64_t base = 0;
  size_t size = 0;
  uint64_t id = 0;
  bool writable = false;
  std::vector<uint8_t> original;

  ~HostPagingLease() {
    if (id) (void)releaseAllocation(id);
  }

  bool applyDeviceChanges() const {
    std::vector<uint8_t> device(size);
    if (!readAllocation(id, 0, device.data(), size)) return false;
    auto *host = reinterpret_cast<uint8_t *>(base);
    for (size_t index = 0; index < size; ++index)
      if (device[index] != original[index]) {
        if (!writable) return false;
        host[index] = device[index];
      }
    return true;
  }
};

std::unique_ptr<HostPagingLease> mapHostAddress(uint64_t pointer, bool skipExecutable = false) {
  const bool trace = std::getenv("GEMSTONE_CUDA_PAGING_TRACE") != nullptr;
  auto *maps = std::fopen("/proc/self/maps", "r");
  if (!maps) return nullptr;
  uint64_t base = 0;
  size_t size = 0;
  bool writable = false;
  char line[1024];
  while (std::fgets(line, sizeof(line), maps)) {
    unsigned long long begin = 0, end = 0;
    char permissions[5] = {};
    if (std::sscanf(line, "%llx-%llx %4s", &begin, &end, permissions) != 3) continue;
    if (pointer >= begin && pointer < end && permissions[0] == 'r' && (!skipExecutable || permissions[2] != 'x')
        && end - begin <= size_t(64) * 1024 * 1024) {
      base = begin;
      size = static_cast<size_t>(end - begin);
      writable = permissions[1] == 'w';
      break;
    }
  }
  std::fclose(maps);
  if (!base || !size) return nullptr;
  uint64_t end = base + size;
  {
    std::lock_guard lock(pointerMutex);
    for (const auto &tracked : pointers) {
      const auto tracked_end = tracked.base + tracked.size;
      if (tracked_end <= pointer && tracked_end > base) base = tracked_end;
      if (tracked.base > pointer && tracked.base < end) end = tracked.base;
    }
  }
  if (end <= base || pointer < base || pointer >= end) return nullptr;
  size = static_cast<size_t>(end - base);
  uint64_t id = 0;
  if (!registerAllocation(size, AllocationKind::Host, id)) {
    if (trace)
      std::fprintf(stderr, "Gemstone CUDA host map register failed pointer=%p base=%p size=%zu\n", reinterpret_cast<void *>(pointer),
                   reinterpret_cast<void *>(base), size);
    return nullptr;
  }
  if (!registerAddress(id, base)) {
    if (trace)
      std::fprintf(stderr, "Gemstone CUDA host map alias failed pointer=%p base=%p size=%zu\n", reinterpret_cast<void *>(pointer),
                   reinterpret_cast<void *>(base), size);
    (void)releaseAllocation(id);
    return nullptr;
  }
  if (trace)
    std::fprintf(stderr, "Gemstone CUDA host map pointer=%p base=%p size=%zu writable=%d id=%llu\n", reinterpret_cast<void *>(pointer),
                 reinterpret_cast<void *>(base), size, writable ? 1 : 0, static_cast<unsigned long long>(id));
  auto lease = std::make_unique<HostPagingLease>();
  lease->base = base;
  lease->size = size;
  lease->id = id;
  lease->writable = writable;
  lease->original.assign(reinterpret_cast<const uint8_t *>(base), reinterpret_cast<const uint8_t *>(base) + size);
  if (!writeAllocation(id, 0, lease->original.data(), size)) return nullptr;
  return lease;
}

void mapMainExecutable(std::vector<std::unique_ptr<HostPagingLease>> &pages) {
  char executable[4096] = {};
  const auto length = readlink("/proc/self/exe", executable, sizeof(executable) - 1);
  if (length <= 0) return;
  executable[length] = '\0';
  auto *maps = std::fopen("/proc/self/maps", "r");
  if (!maps) return;
  char line[8192];
  uint64_t preceding_executable_end = 0;
  while (std::fgets(line, sizeof(line), maps) && pages.size() < 96) {
    unsigned long long begin = 0, end = 0;
    char permissions[5] = {};
    if (std::sscanf(line, "%llx-%llx %4s", &begin, &end, permissions) != 3 || permissions[0] != 'r') continue;
    const bool executable_segment = std::strstr(line, executable) != nullptr;
    const bool adjacent_data = preceding_executable_end == begin && !std::strchr(line, '/') && !std::strchr(line, '[');
    if (!executable_segment && !adjacent_data) {
      preceding_executable_end = 0;
      continue;
    }
    preceding_executable_end = end;
    if (std::any_of(pages.begin(), pages.end(), [begin](const auto &page) { return page->base == begin; })) continue;
    auto page = mapHostAddress(begin);
    if (page) pages.push_back(std::move(page));
  }
  std::fclose(maps);
}

void mapWritableAnonymous(std::vector<std::unique_ptr<HostPagingLease>> &pages) {
  auto *maps = std::fopen("/proc/self/maps", "r");
  if (!maps) return;
  char line[8192];
  while (std::fgets(line, sizeof(line), maps) && pages.size() < 128) {
    unsigned long long begin = 0, end = 0;
    char permissions[5] = {};
    if (std::sscanf(line, "%llx-%llx %4s", &begin, &end, permissions) != 3 || permissions[0] != 'r' || permissions[1] != 'w'
        || permissions[2] == 'x' || end - begin > size_t(1) * 1024 * 1024
        || (std::strchr(line, '/') && !std::strstr(line, "[heap]") && !std::strstr(line, "[stack]")))
      continue;
    if (std::any_of(pages.begin(), pages.end(),
                    [begin](const auto &page) { return begin >= page->base && begin - page->base < page->size; }))
      continue;
    auto page = mapHostAddress(begin);
    if (page) pages.push_back(std::move(page));
  }
  std::fclose(maps);
}

std::optional<PointerBinding> mapReachableHostAddress(uint64_t pointer, std::vector<std::unique_ptr<HostPagingLease>> &pages) {
  const auto findPage = [&pages](uint64_t address) -> HostPagingLease * {
    const auto found = std::find_if(pages.begin(), pages.end(),
                                    [address](const auto &page) { return address >= page->base && address - page->base < page->size; });
    return found == pages.end() ? nullptr : found->get();
  };
  HostPagingLease *root = findPage(pointer);
  if (!root) {
    auto page = mapHostAddress(pointer);
    if (!page) return std::nullopt;
    root = page.get();
    pages.push_back(std::move(page));
  }
  mapMainExecutable(pages);
  mapWritableAnonymous(pages);
  std::vector<uint64_t> pending{pointer};
  for (size_t current = 0; current < pending.size() && current < 128; ++current) {
    const auto address = pending[current];
    const auto *page = findPage(address);
    if (!page) continue;
    const size_t start = static_cast<size_t>(address - page->base);
    const size_t length = std::min<size_t>(16384, page->size - start);
    for (size_t offset = 0; offset + sizeof(uint64_t) <= length && pages.size() < 128; offset += sizeof(uint64_t)) {
      uint64_t candidate = 0;
      std::memcpy(&candidate, page->original.data() + start + offset, sizeof(candidate));
      if (candidate < 4096 || findPage(candidate) || pointerBinding(candidate)) continue;
      auto next = mapHostAddress(candidate, true);
      if (next) {
        pages.push_back(std::move(next));
        pending.push_back(candidate);
      }
    }
  }
  if (std::getenv("GEMSTONE_CUDA_PAGING_TRACE"))
    std::fprintf(stderr, "Gemstone CUDA host graph root=%p maps=%zu\n", reinterpret_cast<void *>(pointer), pages.size());
  return PointerBinding{root->id, pointer - root->base, root->size - (pointer - root->base)};
}

Module *findModule(void *handle) {
  const std::lock_guard lock(stateMutex);
  const auto entry = std::find_if(modules.begin(), modules.end(), [handle](const auto &module) { return module.get() == handle; });
  return entry == modules.end() ? nullptr : entry->get();
}

Function *findFunction(void *handle) {
  const std::lock_guard lock(stateMutex);
  const auto entry = std::find_if(functions.begin(), functions.end(), [handle](const auto &function) { return function.get() == handle; });
  return entry == functions.end() ? nullptr : entry->get();
}

bool releaseStorage(uint64_t address) noexcept {
  try {
    const std::lock_guard lock(stateMutex);
    const auto entry =
        std::find_if(allocations.begin(), allocations.end(), [address](const auto &allocation) { return allocation.address == address; });
    if (entry == allocations.end()) return false;
    allocations.erase(entry);
    return true;
  } catch (...) {
    return false;
  }
}

} // namespace

extern "C" {

int32_t cuInit(uint32_t flags) noexcept {
  if (flags != 0) return CUDA_ERROR_INVALID_VALUE;
  const int32_t ready = executionReady();
  if (ready != CUDA_SUCCESS) return ready;
  try {
    return client->call(Ping{}) ? CUDA_SUCCESS : CUDA_ERROR_NOT_INITIALIZED;
  } catch (...) {
    return CUDA_ERROR_NOT_INITIALIZED;
  }
}

int32_t cuDriverGetVersion(int32_t *version) noexcept {
  if (!version) return CUDA_ERROR_INVALID_VALUE;
  *version = 13030;
  return CUDA_SUCCESS;
}

int32_t cuDeviceGetCount(int32_t *count) noexcept {
  if (!count) return CUDA_ERROR_INVALID_VALUE;
  *count = 1;
  return CUDA_SUCCESS;
}

int32_t cuDeviceGet(int32_t *device, int32_t ordinal) noexcept {
  if (!device) return CUDA_ERROR_INVALID_VALUE;
  if (ordinal != 0) return 101;
  *device = 0;
  return CUDA_SUCCESS;
}

int32_t cuCtxDestroy_v2(void *context) noexcept;

int32_t cuCtxCreate_v2(void **context, uint32_t flags, int32_t device) noexcept {
  if (!context || device != 0) return CUDA_ERROR_INVALID_VALUE;
  (void)flags;
  const uint64_t handle = nextContext.fetch_add(1);
  if (handle == 0) return CUDA_ERROR_UNKNOWN;
  *context = reinterpret_cast<void *>(static_cast<uintptr_t>(handle));
  int32_t status = trackContext(*context);
  if (status == CUDA_SUCCESS) status = activateCreatedContext(*context);
  if (status != CUDA_SUCCESS) {
    untrackContext(*context);
    *context = nullptr;
  }
  return status;
}

GEMSTONE_PUBLIC_API int32_t cuCtxCreate_v4(void **context, void *parameters, uint32_t flags, int32_t device) noexcept {
  (void)parameters;
  return cuCtxCreate_v2(context, flags, device);
}

} // extern "C"

namespace {

int32_t synchronizeStreamCallbacks(void *stream) noexcept {
  (void)stream;
  void *context = activeContext();
  if (!contextIsLive(context)) return 201;
  return managedSynchronizeFromDevice();
}

} // namespace

extern "C" {

int32_t cuCtxDestroy_v2(void *context) noexcept {
  if (!contextIsLive(context)) return 201;
  untrackContext(context);
  return CUDA_SUCCESS;
}

int32_t cuCtxSynchronize() noexcept {
  void *context = activeContext();
  if (!contextIsLive(context)) return 201;
  return managedSynchronizeFromDevice();
}

} // extern "C"

namespace {

int32_t enqueueCallback(void *stream, HostCallback host, StreamCallback streamCallback, void *userData) noexcept {
  void *context = activeContext();
  if (!contextIsLive(context) || (!host && !streamCallback)) return CUDA_ERROR_INVALID_VALUE;
  try {
    // Device work is completed by each emulated launch/copy before this call.
    // Report that completion now so callers waiting on callbacks can progress.
    if (host) host(userData);
    else streamCallback(stream, CUDA_SUCCESS, userData);
    return CUDA_SUCCESS;
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

} // namespace

extern "C" {

int32_t cuLaunchHostFunc(void *stream, HostCallback callback, void *user_data) noexcept {
  return enqueueCallback(stream, callback, nullptr, user_data);
}

int32_t cuStreamAddCallback(void *stream, StreamCallback callback, void *user_data, uint32_t flags) noexcept {
  if (flags != 0) return CUDA_ERROR_INVALID_VALUE;
  return enqueueCallback(stream, nullptr, callback, user_data);
}

} // extern "C"

namespace {

int32_t allocate(uint64_t *pointer, size_t size, void *module) noexcept {
  if (!pointer || !contextIsLive(activeContext())) return CUDA_ERROR_INVALID_VALUE;
  if (executionReady() != CUDA_SUCCESS) return CUDA_ERROR_NOT_INITIALIZED;

  const size_t allocationSize = size == 0 ? 1 : size;
  if (allocationSize > UINT64_MAX - 255) return CUDA_ERROR_INVALID_VALUE;
  const uint64_t span = (static_cast<uint64_t>(allocationSize) + 255) & ~UINT64_C(255);
  uint64_t address = 0;
  {
    const std::lock_guard lock(stateMutex);
    if (nextDeviceAddress > static_cast<uint64_t>(std::numeric_limits<int64_t>::max()) - span) return CUDA_ERROR_UNKNOWN;
    address = nextDeviceAddress;
    nextDeviceAddress += span;
  }

  uint64_t id = 0;
  int32_t status = registerAllocation(allocationSize, AllocationKind::Device, id) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
  if (status == CUDA_SUCCESS) status = trackPointer(address, allocationSize, id, module, false, false);
  if (status == CUDA_SUCCESS && !registerAddress(id, address)) status = CUDA_ERROR_UNKNOWN;
  if (status == CUDA_SUCCESS) {
    try {
      const std::lock_guard lock(stateMutex);
      allocations.push_back(Allocation{address});
    } catch (...) {
      status = CUDA_ERROR_UNKNOWN;
    }
  }
  if (status != CUDA_SUCCESS) {
    if (id != 0) (void)releaseAllocation(id);
    untrackDevice(address);
    return status;
  }
  *pointer = address;
  return CUDA_SUCCESS;
}

} // namespace

extern "C" {

int32_t cuMemAlloc_v2(uint64_t *pointer, size_t size) noexcept { return allocate(pointer, size, nullptr); }

int32_t cuMemFree_v2(uint64_t pointer) noexcept {
  if (pointer == 0) return CUDA_SUCCESS;
  if (const auto managed = freeManaged(pointer)) return *managed;
  const auto binding = pointerBinding(pointer);
  if (!binding || binding->offset != 0) return CUDA_ERROR_INVALID_VALUE;
  if (!releaseAllocation(binding->allocation)) return CUDA_ERROR_UNKNOWN;
  untrackDevice(pointer);
  return releaseStorage(pointer) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
}

int32_t cuMemcpyHtoD_v2(uint64_t destination, const void *source, size_t size) noexcept {
  if (!source && size != 0) return CUDA_ERROR_INVALID_VALUE;
  const auto binding = pointerBinding(destination);
  if (!binding || size > binding->remaining) return CUDA_ERROR_INVALID_VALUE;
  const int32_t status = writeAllocation(binding->allocation, binding->offset, source, size) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
  if (status == CUDA_SUCCESS) copyManagedFromHost(destination, source, size);
  return status;
}

int32_t cuMemcpyDtoH_v2(void *destination, uint64_t source, size_t size) noexcept {
  if (!destination && size != 0) return CUDA_ERROR_INVALID_VALUE;
  if (copyManagedToHost(destination, source, size)) return CUDA_SUCCESS;
  const auto binding = pointerBinding(source);
  if (!binding || size > binding->remaining) return CUDA_ERROR_INVALID_VALUE;
  return readAllocation(binding->allocation, binding->offset, destination, size) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
}

int32_t cuMemcpyDtoD_v2(uint64_t destination, uint64_t source, size_t size) noexcept {
  try {
    std::vector<uint8_t> buffer(size == 0 ? 1 : size);
    int32_t status = cuMemcpyDtoH_v2(buffer.data(), source, size);
    if (status == CUDA_SUCCESS) status = cuMemcpyHtoD_v2(destination, buffer.data(), size);
    return status;
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

int32_t cuMemsetD8_v2(uint64_t destination, uint8_t value, size_t count) noexcept {
  if (count > std::numeric_limits<uint32_t>::max()) return CUDA_ERROR_INVALID_VALUE;
  try {
    std::vector<uint8_t> buffer(count, value);
    return cuMemcpyHtoD_v2(destination, buffer.data(), count);
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

} // extern "C"

namespace {

int32_t managedRegisterRemote(uint64_t pointer, size_t size, int hostMemory) noexcept {
  if (pointer == 0 || size == 0 || executionReady() != CUDA_SUCCESS) return CUDA_ERROR_INVALID_VALUE;
  uint64_t id = 0;
  int32_t status = registerAllocation(size, AllocationKind::Managed, id) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
  if (status == CUDA_SUCCESS && !registerAddress(id, pointer)) status = CUDA_ERROR_UNKNOWN;
  if (status == CUDA_SUCCESS) status = trackPointer(pointer, size, id, nullptr, hostMemory != 0, hostMemory == 0);
  if (status != CUDA_SUCCESS && id != 0) (void)releaseAllocation(id);
  return status;
}

int32_t managedUnregisterRemote(uint64_t pointer) noexcept {
  const auto binding = pointerBinding(pointer);
  if (!binding || binding->offset != 0) return CUDA_ERROR_INVALID_VALUE;
  return releaseAllocation(binding->allocation) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
}

} // namespace

extern "C" {

int32_t cuModuleLoadData(void **module, const void *image) noexcept {
  uint64_t loadedProgram = 0;
  bool hasLoadedProgram = false;
  try {
    void *context = activeContext();
    if (!module || !image || !contextIsLive(context)) return CUDA_ERROR_INVALID_VALUE;
    if (executionReady() != CUDA_SUCCESS) return CUDA_ERROR_NOT_INITIALIZED;

    const auto first = *static_cast<const unsigned char *>(image);
    if (first != '.' && first != '/' && first != ' ' && first != '\n' && first != '\t' && first != '\r') {
      image = findUncompressedPtx(image);
      if (!image) return CUDA_ERROR_NOT_SUPPORTED;
    }

    const auto *imageText = static_cast<const char *>(image);
    const auto *imageEnd = std::find(imageText, imageText + MaximumPayload, '\0');
    const auto size = static_cast<size_t>(imageEnd - imageText);
    if (size == MaximumPayload || size > std::numeric_limits<uint32_t>::max()) return CUDA_ERROR_INVALID_VALUE;

    auto entry = std::make_unique<Module>();
    entry->context = context;
    const auto *imageBytes = reinterpret_cast<const uint8_t *>(imageText);
    auto loaded = client->call(LoadProgram{"ptx-text", std::vector<uint8_t>(imageBytes, imageBytes + size)});
    if (!loaded) return CUDA_ERROR_UNKNOWN;

    entry->program = static_cast<uint64_t>(loaded.value().value);
    loadedProgram = entry->program;
    hasLoadedProgram = true;
    auto *handle = entry.get();
    {
      const std::lock_guard lock(stateMutex);
      modules.push_back(std::move(entry));
    }
    // Compiler-generated tables can be referenced by kernels without a host
    // cuModuleGetGlobal call. Bind every declared global before any launch.
    auto catalog = client->call(DescribeProgram{ProgramId{static_cast<int64_t>(loadedProgram)}});
    if (!catalog) {
      (void)cuModuleUnload(handle);
      return CUDA_ERROR_UNKNOWN;
    }
    for (const auto &symbol : catalog.value().globals) {
      uint64_t address = 0;
      const int32_t status = cuModuleGetGlobal_v2(&address, nullptr, handle, symbol.name.value.c_str());
      if (status != CUDA_SUCCESS) {
        (void)cuModuleUnload(handle);
        return status;
      }
    }
    *module = handle;
    return CUDA_SUCCESS;
  } catch (...) {
    if (hasLoadedProgram) {
      try {
        (void)client->call(UnloadProgram{ProgramId{static_cast<int64_t>(loadedProgram)}});
      } catch (...) {
      }
    }
    return CUDA_ERROR_UNKNOWN;
  }
}

int32_t cuModuleGetFunction(void **function, void *module, const char *name) noexcept {
  try {
    if (!function || !name) return CUDA_ERROR_INVALID_VALUE;
    Module *owner = findModule(module);
    if (!owner) return 201;
    {
      const std::lock_guard lock(stateMutex);
      const auto existing = std::find_if(functions.begin(), functions.end(),
                                         [owner, name](const auto &entry) { return entry->module == owner && entry->name == name; });
      if (existing != functions.end()) {
        *function = existing->get();
        return CUDA_SUCCESS;
      }
    }

    auto described = client->call(DescribeKernel{ProgramId{static_cast<int64_t>(owner->program)}, name});
    if (!described) return CUDA_ERROR_NOT_FOUND;

    auto entry = std::make_unique<Function>();
    entry->module = owner;
    entry->name = name;
    entry->parameters.reserve(described.value().parameters.size());
    for (const auto &parameter : described.value().parameters)
      entry->parameters.push_back(parameter);

    auto *handle = entry.get();
    {
      const std::lock_guard lock(stateMutex);
      functions.push_back(std::move(entry));
    }
    *function = handle;
    return CUDA_SUCCESS;
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

int32_t cuModuleGetGlobal_v2(uint64_t *pointer, size_t *bytes, void *module, const char *name) noexcept {
  uint64_t address = 0;
  try {
    if ((!pointer && !bytes) || !name) return CUDA_ERROR_INVALID_VALUE;
    Module *owner = findModule(module);
    if (!owner) return 201;

    const std::lock_guard lock(owner->mutex);
    const auto existing =
        std::find_if(owner->globals.begin(), owner->globals.end(), [name](const auto &global) { return global->name == name; });
    if (existing != owner->globals.end()) {
      if (pointer) *pointer = (*existing)->address;
      if (bytes) *bytes = (*existing)->size;
      return CUDA_SUCCESS;
    }

    auto described = client->call(DescribeGlobal{ProgramId{static_cast<int64_t>(owner->program)}, name});
    if (!described) return CUDA_ERROR_NOT_FOUND;

    const auto size = static_cast<uint64_t>(described.value().size.value);
    const auto &initial = described.value().initial;
    auto global = std::make_unique<Global>();
    global->name = name;

    int32_t status = allocate(&address, static_cast<size_t>(size), module);
    if (status == CUDA_SUCCESS && !initial.empty()) status = cuMemcpyHtoD_v2(address, initial.data(), initial.size());

    const auto binding = pointerBinding(address);
    if (status == CUDA_SUCCESS
        && (!binding
            || !client->call(BindProgramGlobal{ProgramId{static_cast<int64_t>(owner->program)}, name,
                                               AllocationId{static_cast<int64_t>(binding->allocation)}, 0})))
      status = CUDA_ERROR_UNKNOWN;
    if (status != CUDA_SUCCESS) {
      if (address != 0) (void)cuMemFree_v2(address);
      return status;
    }

    global->address = address;
    global->allocation = binding->allocation;
    global->size = static_cast<size_t>(size);
    owner->globals.push_back(std::move(global));
    if (pointer) *pointer = address;
    if (bytes) *bytes = static_cast<size_t>(size);
    return CUDA_SUCCESS;
  } catch (...) {
    if (address != 0) (void)cuMemFree_v2(address);
    return CUDA_ERROR_UNKNOWN;
  }
}

} // extern "C"

namespace {

int32_t registerGlobalRemote(uint64_t module, const char *name, const void *initial, size_t size) {
  uint64_t address = 0;
  size_t declared = 0;
  int32_t status = cuModuleGetGlobal_v2(&address, &declared, reinterpret_cast<void *>(static_cast<uintptr_t>(module)), name);
  if (status == CUDA_SUCCESS && size > declared) status = CUDA_ERROR_INVALID_VALUE;
  if (status == CUDA_SUCCESS && size != 0) status = cuMemcpyHtoD_v2(address, initial, size);
  return status;
}

} // namespace

extern "C" {

int32_t cuModuleUnload(void *module) noexcept {
  try {
    Module *owner = findModule(module);
    if (!owner) return CUDA_ERROR_INVALID_VALUE;
    if (!client->call(UnloadProgram{ProgramId{static_cast<int64_t>(owner->program)}})) return CUDA_ERROR_UNKNOWN;

    std::unique_ptr<Module> removed;
    {
      const std::lock_guard lock(stateMutex);
      functions ^= filter([owner](const auto &entry) { return entry->module != owner; });
      const auto entry = std::find_if(modules.begin(), modules.end(), [owner](const auto &candidate) { return candidate.get() == owner; });
      if (entry != modules.end()) {
        removed = std::move(*entry);
        modules.erase(entry);
      }
    }
    if (!removed) return CUDA_ERROR_INVALID_VALUE;

    untrackModule(module);
    for (const auto &global : removed->globals) {
      (void)releaseAllocation(global->allocation);
      (void)releaseStorage(global->address);
    }
    return CUDA_SUCCESS;
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

int32_t cuLaunchKernel(void *function, uint32_t grid_x, uint32_t grid_y, uint32_t grid_z, uint32_t block_x, uint32_t block_y,
                       uint32_t block_z, uint32_t shared_memory_bytes, void *stream, void **kernel_params, void **extra) noexcept {
  CompletionId completion{};
  bool launched_completion = false;
  try {
    if (!function || grid_x == 0 || grid_y == 0 || grid_z == 0 || block_x == 0 || block_y == 0 || block_z == 0 || extra)
      return CUDA_ERROR_INVALID_VALUE;
    (void)stream;
    const uint64_t global_x = static_cast<uint64_t>(grid_x) * block_x;
    const uint64_t global_y = static_cast<uint64_t>(grid_y) * block_y;
    const uint64_t global_z = static_cast<uint64_t>(grid_z) * block_z;
    constexpr auto maximum_dimension = static_cast<uint64_t>(std::numeric_limits<int32_t>::max());
    constexpr auto maximum_global = static_cast<uint64_t>(std::numeric_limits<int64_t>::max());
    if (grid_x > maximum_dimension || grid_y > maximum_dimension || grid_z > maximum_dimension || block_x > maximum_dimension
        || block_y > maximum_dimension || block_z > maximum_dimension || global_x > maximum_global || global_y > maximum_global
        || global_z > maximum_global || shared_memory_bytes > maximum_dimension)
      return CUDA_ERROR_INVALID_VALUE;

    Function *entry = findFunction(function);
    if (!entry || (!entry->parameters.empty() && !kernel_params)) return CUDA_ERROR_INVALID_VALUE;
    int32_t status = managedSynchronizeToDevice();
    if (status != CUDA_SUCCESS) return status;

    std::vector<gemstone::execution::KernelArgument> arguments;
    arguments.reserve(entry->parameters.size());
    std::vector<PointerRelocation> relocations;
    std::vector<std::unique_ptr<HostPagingLease>> hostPages;
    for (size_t index = 0; index < entry->parameters.size(); ++index) {
      const auto &parameter = entry->parameters[index];
      const auto width = static_cast<uint32_t>(parameter.width.value);
      if (!kernel_params[index]) {
        status = CUDA_ERROR_INVALID_VALUE;
        break;
      }
      uint64_t pointer = 0;
      if (parameter.kind == KernelParameterKind::Allocation && width == sizeof(pointer))
        std::memcpy(&pointer, kernel_params[index], sizeof(pointer));
      auto binding = parameter.kind == KernelParameterKind::Allocation ? pointerBinding(pointer) : std::nullopt;
      if (!binding && parameter.kind == KernelParameterKind::Allocation && width == sizeof(pointer) && systemPagingEnabled())
        binding = mapReachableHostAddress(pointer, hostPages);
      if (width == sizeof(pointer) && binding) {
        arguments.emplace_back(
            KernelArgumentAllocation{AllocationId{static_cast<int64_t>(binding->allocation)}, static_cast<int64_t>(binding->offset)});
      } else {
        const auto *bytes = static_cast<const uint8_t *>(kernel_params[index]);
        arguments.emplace_back(KernelArgumentScalar{std::vector<uint8_t>(bytes, bytes + width)});
        if (parameter.containsPointers && width >= sizeof(uint64_t)) {
          for (size_t offset = 0; offset <= width - sizeof(uint64_t); offset += sizeof(uint32_t)) {
            const auto candidate = readNative<uint64_t>(bytes, offset);
            const auto target = pointerRelocationBinding(candidate);
            if (target) {
              relocations.push_back({RelocationSourceArgument{IntPositive0{static_cast<int32_t>(index)}},
                                     LongPositive0{static_cast<int64_t>(offset)}, AllocationId{static_cast<int64_t>(target->allocation)},
                                     LongPositive0{static_cast<int64_t>(target->offset)}});
            }
          }
        }
      }
    }

    if (status == CUDA_SUCCESS) {
      auto launched = client->call(
          Launch{ProgramId{static_cast<int64_t>(entry->module->program)}, entry->name,
                 Dimensions{static_cast<int32_t>(block_x), static_cast<int32_t>(block_y), static_cast<int32_t>(block_z)},
                 Dimensions{static_cast<int32_t>(grid_x), static_cast<int32_t>(grid_y), static_cast<int32_t>(grid_z)},
                 GlobalDimensions{static_cast<int64_t>(global_x), static_cast<int64_t>(global_y), static_cast<int64_t>(global_z)}, 3,
                 Coordinates{}, 0, static_cast<int32_t>(shared_memory_bytes), std::move(arguments), std::move(relocations)});
      if (launched) {
        completion = launched.value();
        launched_completion = true;
      } else {
        if (std::getenv("GEMSTONE_TRACE")) std::fprintf(stderr, "Gemstone CUDA launch failed: %s\n", launched.error().detail.c_str());
        status = 700;
      }
    }
    if (status == CUDA_SUCCESS && !client->call(AwaitCompletion{completion})) {
      if (std::getenv("GEMSTONE_TRACE")) std::fprintf(stderr, "Gemstone CUDA execution failed: %s\n", client->error().c_str());
      if (std::getenv("GEMSTONE_CUDA_PAGING_TRACE")) {
        const auto error = client->error();
        const char *address_text = std::strstr(error.c_str(), ": 0x");
        if (address_text) {
          const auto address = std::strtoull(address_text + 2, nullptr, 16);
          if (auto *maps = std::fopen("/proc/self/maps", "r")) {
            char line[8192];
            while (std::fgets(line, sizeof(line), maps)) {
              unsigned long long begin = 0, end = 0;
              if (std::sscanf(line, "%llx-%llx", &begin, &end) == 2 && address >= begin && address < end) {
                std::fprintf(stderr, "Gemstone CUDA missing host mapping: %s", line);
                break;
              }
            }
            std::fclose(maps);
          }
        }
      }
      status = 700;
    }
    if (launched_completion) {
      const bool released = static_cast<bool>(client->call(ReleaseCompletion{completion}));
      launched_completion = false;
      if (!released) status = 700;
    }
    if (status == CUDA_SUCCESS) {
      for (const auto &page : hostPages)
        if (!page->applyDeviceChanges()) {
          status = 700;
          break;
        }
    }
    if (status == CUDA_SUCCESS) {
      markManagedDeviceDirty();
      status = managedSynchronizeFromDevice();
    }
    return status;
  } catch (...) {
    if (launched_completion) {
      try {
        (void)client->call(ReleaseCompletion{completion});
      } catch (...) {
      }
    }
    return CUDA_ERROR_UNKNOWN;
  }
}

} // extern "C"
