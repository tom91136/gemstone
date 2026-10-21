#include <exception>
#include <system_error>

#include "gemstone/environment.hpp"
#include "gemstone/vector.hpp"

// Declares the legacy and per-thread-stream spellings this library defines next to the current entry points.
#define __CUDA_API_VERSION_INTERNAL 1

#include <algorithm>
#include <array>
#include <atomic>
#include <cctype>
#include <charconv>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <functional>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <new>
#include <optional>
#include <string>
#include <string_view>
#include <utility>
#include <vector>

#include <dlfcn.h>
#include <elf.h>
#include <pthread.h>
#include <unistd.h>

#include <aspartame/array.hpp>
#include <aspartame/optional.hpp>
#include <aspartame/string_view.hpp>
#include <aspartame/vector.hpp>
#include <aspartame/view.hpp>
#include <cuda.h>
#include <cuda_runtime_api.h>

#include "gemstone_cuda_entry_points.hpp"
#include "gemstone_execution_rpc.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/command_queue.hpp>
#include <gemstone/elf.hpp>
#include <gemstone/export.h>
#include <gemstone/lz4.hpp>
#include <gemstone/memory.hpp>
#include <gemstone/trace.hpp>
#include <gemstone/zstd.hpp>

#include "client.hpp"
#include "completion.hpp"
#include "transfer.hpp"
#include <environment_keys.hpp>

extern "C" GEMSTONE_INTERNAL_API CUresult gemstone_cuda_unsupported_driver();

using namespace aspartame;
using namespace gemstone::execution;
using gemstone::readNative;

namespace {

constexpr size_t MaximumImageSize = size_t{64} * 1024 * 1024;
// CUDA 11's library can register compressed catalogues containing a valid 75 MB sm_90 image
// while running on an older target; inspect those entries without passing them through RPC.
constexpr size_t MaximumCubinValidationSize = size_t{128} * 1024 * 1024;
constexpr char DeviceName[] = "Gemstone PTX emulator";
constexpr char DevicePciBusId[] = "0000:01:00.0";
constexpr uint8_t DeviceUuid[16] = {0x47, 0x45, 0x4d, 0x53, 0x54, 0x4f, 0x4e, 0x45, 0x2d, 0x43, 0x55, 0x44, 0x41, 0x2d, 0x30, 0x31};
constexpr size_t ContextCapacity = 64;
constexpr uint32_t FatbinMagic = 0xba55ed50;
constexpr int FatbinWrapperMagic = 0x466243b1;
constexpr uintptr_t DefaultMemoryPool = 0x300000000;
// cuda.h spells these handles as C-style casts of integers
const auto LegacyStream = reinterpret_cast<CUstream>(uintptr_t{0x1});
const auto PerThreadStream = reinterpret_cast<CUstream>(uintptr_t{0x2});
const auto LaunchParamBufferPointer = reinterpret_cast<void *>(uintptr_t{0x1});
const auto LaunchParamBufferSize = reinterpret_cast<void *>(uintptr_t{0x2});

// The emulator exposes one synthetic device. These are the limits it enforces;
// both the attribute and property queries read from this one definition so the
// two views cannot drift apart.
namespace device_limits {
constexpr size_t TotalGlobalMemoryBytes = uint64_t{8} * 1024 * 1024 * 1024;
constexpr int32_t MaxThreadsPerBlock = 1024;
constexpr int32_t MaxBlockDimX = 1024;
constexpr int32_t MaxBlockDimY = 1024;
constexpr int32_t MaxBlockDimZ = 64;
constexpr int32_t MaxGridDimX = std::numeric_limits<int32_t>::max();
constexpr int32_t MaxGridDimY = 65535;
constexpr int32_t MaxGridDimZ = 65535;
constexpr int32_t MaxPitch = std::numeric_limits<int32_t>::max();
constexpr int32_t MaxSharedMemoryPerBlock = 49152;
constexpr int32_t MaxSharedMemoryPerBlockOptin = 98304;
constexpr int32_t MaxSharedMemoryPerMultiprocessor = 98304;
constexpr int32_t TotalConstantMemory = 65536;
constexpr int32_t WarpSize = 32;
constexpr int32_t MaxRegistersPerBlock = 65536;
constexpr int32_t MaxRegistersPerMultiprocessor = 65536;
// This is virtual launch capacity, independent of CPU worker count. Libraries
// use it to size launch grids; a one-SM device also falls outside some host-side
// launch heuristics' supported configurations. Workgroups still run through the generic scheduler.
constexpr int32_t MultiprocessorCount = 4;
constexpr int32_t MaxThreadsPerMultiprocessor = 1536;
constexpr int32_t MaxBlocksPerMultiprocessor = 32;
constexpr int32_t ClockRateKhz = 1500000;
constexpr int32_t MemoryClockRateKhz = 1500000;
constexpr int32_t GlobalMemoryBusWidth = 128;
constexpr int32_t L2CacheSize = 4194304;
constexpr int32_t MaxPersistingL2CacheSize = 4194304;
constexpr int32_t MaxAccessPolicyWindowSize = 4096;
constexpr int32_t TextureAlignment = 256;
constexpr int32_t TexturePitchAlignment = 32;
constexpr int32_t SurfaceAlignment = 256;
constexpr int32_t SingleToDoublePrecisionPerfRatio = 32;
constexpr int32_t AsyncEngineCount = 2;
constexpr int32_t PciBusId = 1;
constexpr int32_t PciDeviceId = 1;
constexpr int32_t PciDomainId = 0;
constexpr int32_t MaxTexture1DWidth = 131072;
constexpr int32_t MaxTexture2DWidth = 131072;
constexpr int32_t MaxTexture2DHeight = 65536;
constexpr int32_t MaxTexture3DWidth = 16384;
constexpr int32_t MaxTexture3DHeight = 16384;
constexpr int32_t MaxTexture3DDepth = 16384;
constexpr int32_t MaxTexture2DLayeredWidth = 32768;
constexpr int32_t MaxTexture2DLayeredHeight = 32768;
constexpr int32_t MaxTexture2DLayeredLayers = 2048;
constexpr int32_t MaxTexture1DLayeredWidth = 32768;
constexpr int32_t MaxTexture1DLayeredLayers = 2048;
constexpr int32_t MaxTexture2DGatherWidth = 32768;
constexpr int32_t MaxTexture2DGatherHeight = 32768;
constexpr int32_t MaxTextureCubemapWidth = 32768;
constexpr int32_t MaxTextureCubemapLayeredWidth = 32768;
constexpr int32_t MaxTextureCubemapLayeredLayers = 2048;
constexpr int32_t MaxTexture1DLinearWidth = 134217728;
constexpr int32_t MaxTexture2DLinearWidth = 131072;
constexpr int32_t MaxTexture2DLinearHeight = 65536;
constexpr int32_t MaxTexture2DLinearPitch = std::numeric_limits<int32_t>::max();
constexpr int32_t MaxTexture2DMipmappedWidth = 32768;
constexpr int32_t MaxTexture2DMipmappedHeight = 32768;
constexpr int32_t MaxTexture1DMipmappedWidth = 32768;
constexpr int32_t MaxSurface1DWidth = 16384;
constexpr int32_t MaxSurface2DWidth = 16384;
constexpr int32_t MaxSurface2DHeight = 16384;
constexpr int32_t MaxSurface3DWidth = 16384;
constexpr int32_t MaxSurface3DHeight = 16384;
constexpr int32_t MaxSurface3DDepth = 16384;
constexpr int32_t MaxSurface1DLayeredWidth = 16384;
constexpr int32_t MaxSurface1DLayeredLayers = 2048;
constexpr int32_t MaxSurface2DLayeredWidth = 16384;
constexpr int32_t MaxSurface2DLayeredHeight = 16384;
constexpr int32_t MaxSurface2DLayeredLayers = 2048;
constexpr int32_t MaxSurfaceCubemapWidth = 32768;
constexpr int32_t MaxSurfaceCubemapLayeredWidth = 32768;
constexpr int32_t MaxSurfaceCubemapLayeredLayers = 2048;
} // namespace device_limits

// Driver and runtime status enumerations share their numeric values.
cudaError_t runtimeStatus(CUresult status) { return static_cast<cudaError_t>(status); }
CUresult driverStatus(cudaError_t status) { return static_cast<CUresult>(status); }
// A launch configuration the runtime API reports as cudaErrorInvalidConfiguration and the driver API as an invalid value.
constexpr auto InvalidConfiguration = static_cast<CUresult>(cudaErrorInvalidConfiguration);

CUresult driverLaunchStatus(CUresult status) { return status == InvalidConfiguration ? CUDA_ERROR_INVALID_VALUE : status; }

// These leave the context unusable: every later call reports the first one until the process exits.
bool stickyStatus(CUresult status) {
  switch (status) {
    case CUDA_ERROR_ILLEGAL_ADDRESS:
    case CUDA_ERROR_MISALIGNED_ADDRESS:
    case CUDA_ERROR_ILLEGAL_INSTRUCTION:
    case CUDA_ERROR_ASSERT:
    case CUDA_ERROR_LAUNCH_FAILED:
    case CUDA_ERROR_LAUNCH_TIMEOUT: return true;
    default: return false;
  }
}

std::atomic<CUresult> stickyError{CUDA_SUCCESS};

CUresult latchStatus(CUresult status) {
  if (stickyStatus(status)) {
    auto expected = CUDA_SUCCESS;
    stickyError.compare_exchange_strong(expected, status);
  }
  return status;
}

CUresult queueStatus(int32_t status) {
  const auto latched = stickyError.load();
  return latched != CUDA_SUCCESS ? latched : latchStatus(static_cast<CUresult>(status));
}

// The status a failed program load, kernel lookup or dispatch reports; `otherwise` covers failures with no closer code.
CUresult failureStatus(gemstone::execution::FailureKind kind, ProgramEncoding encoding, CUresult otherwise) {
  using gemstone::execution::FailureKind;
  switch (kind) {
    case FailureKind::InvalidRequest:
    case FailureKind::InvalidConfiguration: return CUDA_ERROR_INVALID_VALUE;
    case FailureKind::NotFound: return CUDA_ERROR_NOT_FOUND;
    case FailureKind::Unsupported: return CUDA_ERROR_NOT_SUPPORTED;
    case FailureKind::InvalidProgram: return encoding == ProgramEncoding::PTXText ? CUDA_ERROR_INVALID_PTX : CUDA_ERROR_INVALID_IMAGE;
    case FailureKind::OutOfResources: return CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES;
    case FailureKind::IllegalAddress: return CUDA_ERROR_ILLEGAL_ADDRESS;
    case FailureKind::MisalignedAddress: return CUDA_ERROR_MISALIGNED_ADDRESS;
    case FailureKind::IllegalInstruction: return CUDA_ERROR_ILLEGAL_INSTRUCTION;
    case FailureKind::Trapped: return CUDA_ERROR_LAUNCH_FAILED;
    case FailureKind::AssertionFailed: return CUDA_ERROR_ASSERT;
    case FailureKind::TimedOut: return CUDA_ERROR_LAUNCH_TIMEOUT;
    case FailureKind::Failed:
    case FailureKind::Transport: return otherwise;
  }
  return otherwise;
}

std::atomic<bool> executionShuttingDown{false};
gemstone::CommandQueue &commands();

CUresult waitCommands() { return executionShuttingDown.load() ? CUDA_ERROR_DEINITIALIZED : queueStatus(commands().waitAll()); }

CUresult enqueueCommand(CUstream stream, std::function<int32_t()> work) {
  if (const auto latched = stickyError.load(); latched != CUDA_SUCCESS) return latched;
  try {
    commands().submit(stream, std::move(work));
    return CUDA_SUCCESS;
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

std::optional<int> parsePtxTarget(std::string_view target) {
  target = target ^ drop_while([](char value) { return value == ' ' || value == '\t' || value == '\r'; });
  if (target ^ starts_with("compute")) target = target ^ strip_prefix("compute") ^ strip_prefix("_");
  else if (target ^ starts_with("sm")) target = target ^ strip_prefix("sm") ^ strip_prefix("_");
  else return std::nullopt;

  const auto digits = target ^ take_while([](char value) { return value >= '0' && value <= '9'; });
  int version = 0;
  const auto parsed = std::from_chars(digits.data(), digits.data() + digits.size(), version);
  return !digits.empty() && parsed.ec == std::errc{} ? std::optional{version} : std::nullopt;
}

std::optional<int> configuredPtxVersion() {
  const char *value = gemstone::environment::value(gemstone::environment::TargetArch);
  return value && *value != '\0' ? parsePtxTarget(value) : std::nullopt;
}

bool codeRequired(std::string_view mode) {
  const char *value = gemstone::environment::value(gemstone::environment::CudaCode);
  return value && mode == value;
}

bool ptxCodeRequired() { return codeRequired("ptx"); }

bool sassCodeRequired() { return codeRequired("sass"); }

// An unknown mode must not silently admit both encodings.
bool codeModeValid() {
  const char *value = gemstone::environment::value(gemstone::environment::CudaCode);
  if (!value || *value == '\0' || std::string_view(value) == "ptx" || std::string_view(value) == "sass") return true;
  static std::once_flag reported;
  std::call_once(reported, [value] {
    std::fprintf(stderr, "Gemstone CUDA: %s must be ptx or sass, not %s\n", gemstone::environment::CudaCode, value);
  });
  return false;
}

std::optional<std::pair<int, int>> computeCapability() {
  return configuredPtxVersion() ^ map([](int target) { return std::pair{target / 10, target % 10}; })
         ^ filter([](int major, int) { return major > 0; });
}

std::string emptyPtx() {
  return ".version 9.3\n.target sm_" + std::to_string(configuredPtxVersion().value_or(75)) + "\n.address_size 64\n";
}

bool ptxText(const void *image) {
  const auto first = *static_cast<const unsigned char *>(image);
  return first == '.' || first == '/' || first == ' ' || first == '\n' || first == '\t' || first == '\r';
}

std::optional<size_t> cubinImageSize(const void *image, size_t maximumSize = MaximumImageSize) {
  return gemstone::elfImageSize(image, maximumSize, EM_CUDA);
}

struct FatbinWrapper {
  int magic;
  int version;
  const void *data;
  const void *const *prelinked;
};

struct Fatbin {
  std::string ptx;
  std::vector<uint8_t> cubin;
  bool metadataOnly = true;
  bool entriesValid = true;
  bool incompatibleExecutable = false;
  bool compatibleExecutable = false;
};

bool isPtxHeader(std::string_view line) {
  const auto directive = line ^ trim_leading();
  return directive ^ starts_with(".version") || directive ^ starts_with(".target") || directive ^ starts_with(".address_size");
}

bool appendPtx(std::string &combined, std::string_view source) {
  try {
    const bool first = combined.empty();
    const std::string text =
        first ? std::string(source) : source ^ lines() ^ filter([](std::string_view line) { return !isPtxHeader(line); }) ^ mk_string("\n");
    const size_t separator = first || text.empty() ? 0 : 1;
    if (combined.size() > MaximumImageSize - separator || text.size() > MaximumImageSize - separator - combined.size()) return false;
    if (separator) combined.push_back('\n');
    combined += text;
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

// Libraries also contain metadata-only CUDA ELF images. They have no device storage or instructions to execute,
// and can be loaded as empty modules. Never treat an executable cubin as an empty PTX module.
bool metadataOnlyCubin(std::string_view content) {
  if (content.size() < sizeof(Elf64_Ehdr) || content.substr(0, 6) != std::string_view("\177ELF\2\1", 6)) return false;
  const auto header = readNative<Elf64_Ehdr>(content.data());
  if (header.e_machine != EM_CUDA || header.e_shnum == 0 || header.e_shentsize < sizeof(Elf64_Shdr) || header.e_shoff > content.size()
      || static_cast<uint64_t>(header.e_shnum) * header.e_shentsize > content.size() - header.e_shoff)
    return false;
  for (uint16_t index = 0; index < header.e_shnum; ++index) {
    const auto section = readNative<Elf64_Shdr>(content.data(), header.e_shoff + (static_cast<uint64_t>(index) * header.e_shentsize));
    if (section.sh_size != 0 && (section.sh_flags & (SHF_ALLOC | SHF_EXECINSTR)) != 0) return false;
  }
  return true;
}

bool appendFatbin(Fatbin &fatbin, const uint8_t *data, bool validating) {
  if (!data || readNative<uint32_t>(data) != FatbinMagic) return false;
  const uint16_t outerHeaderSize = readNative<uint16_t>(data + 6);
  const uint64_t contentSize = readNative<uint64_t>(data + 8);
  if (outerHeaderSize < 16 || contentSize > MaximumImageSize) return false;
  uint64_t totalSize = static_cast<uint64_t>(outerHeaderSize) + contentSize;
  uint64_t offset = outerHeaderSize;
  bool onlyEmptyCubins = true;
  bool sawCubin = false;
  int selectedCubinTarget = -1;
  bool selectedCubinEmpty = false;
  const auto target = configuredPtxVersion();
  // nvcc can wrap the fatbin in an empty outer header followed by an inner header carrying the payload size.
  if (contentSize == 0 && readNative<uint32_t>(data + offset) == FatbinMagic) {
    const uint16_t innerHeaderSize = readNative<uint16_t>(data + offset + 6);
    const uint64_t innerContentSize = readNative<uint64_t>(data + offset + 8);
    if (innerHeaderSize < 16 || innerContentSize > MaximumImageSize) return false;
    offset += innerHeaderSize;
    totalSize = offset + innerContentSize;
  }
  while (offset <= totalSize && totalSize - offset >= 64) {
    const uint8_t *entry = data + offset;
    const uint16_t kind = readNative<uint16_t>(entry);
    const uint32_t headerSize = readNative<uint32_t>(entry + 4);
    const uint32_t payloadSize = readNative<uint32_t>(entry + 8);
    const uint32_t compressedSize = readNative<uint32_t>(entry + 16);
    const uint64_t flags = readNative<uint64_t>(entry + 40);
    const uint64_t uncompressedPayload = readNative<uint64_t>(entry + 56);
    const uint32_t storedSize = std::max(payloadSize, compressedSize);
    if (headerSize < 64 || static_cast<uint64_t>(headerSize) > totalSize - offset
        || static_cast<uint64_t>(storedSize) > totalSize - offset - headerSize)
      return false;
    const auto payload = std::string_view(reinterpret_cast<const char *>(entry + headerSize), storedSize);
    const int cubinTarget = static_cast<int>(readNative<uint32_t>(entry + 28));
    const bool cubin = (kind & 2U) != 0;
    std::vector<char> decompressed;
    const auto decompressedView = [&](std::optional<size_t> size) {
      return size ? std::optional{std::string_view(decompressed.data(), *size)} : std::nullopt;
    };
    std::optional<std::string_view> content = payload;
    if ((flags & 0x2000) != 0) {
      const size_t limit = validating && cubin && target && cubinTarget > *target ? MaximumCubinValidationSize : MaximumImageSize;
      const size_t expanded = uncompressedPayload == 0 ? payloadSize : static_cast<size_t>(uncompressedPayload);
      content = expanded > limit ? std::nullopt
                                 : decompressedView(gemstone::decompressLz4(decompressed, payload.data(), compressedSize, expanded));
    } else if ((flags & 0x8000) != 0) {
      content = decompressedView(gemstone::decompressZstdFrame(decompressed, payload.data(), compressedSize, MaximumImageSize));
    }
    if (validating && !content) fatbin.entriesValid = false;
    if ((kind & 1U) != 0 && content)
      if (const auto ptx = findPtxText(*content)) {
        fatbin.metadataOnly = false;
        if (!appendPtx(fatbin.ptx, *ptx)) return false;
      }
    if (cubin) {
      sawCubin = true;
      const bool empty = content && metadataOnlyCubin(*content);
      onlyEmptyCubins = onlyEmptyCubins && empty;
      fatbin.metadataOnly = fatbin.metadataOnly && empty;
      if (validating) {
        if (!content || !cubinImageSize(content->data(), std::min(MaximumCubinValidationSize, content->size())))
          fatbin.entriesValid = false;
        else if (!empty) (target && cubinTarget > *target ? fatbin.incompatibleExecutable : fatbin.compatibleExecutable) = true;
      }
      // Prefer the newest image at or below the configured target. Libraries
      // also register catalogues containing only newer architectures; retain
      // their real ELF metadata and let the backend reject unsupported execution.
      const bool prefer =
          target
          && (selectedCubinTarget < 0 || (cubinTarget <= *target && (selectedCubinTarget > *target || cubinTarget > selectedCubinTarget))
              || (cubinTarget > *target && selectedCubinTarget > *target && cubinTarget < selectedCubinTarget));
      if (prefer && cubinTarget >= 35) {
        selectedCubinTarget = cubinTarget;
        selectedCubinEmpty = empty;
        if (content) fatbin.cubin.assign(content->begin(), content->end());
        else fatbin.cubin.clear();
      }
    }
    offset += static_cast<uint64_t>(headerSize) + storedSize;
  }
  while (offset < totalSize)
    if (data[offset++] != 0) return false;
  if (fatbin.ptx.empty() && sawCubin && (selectedCubinTarget >= 0 ? selectedCubinEmpty : onlyEmptyCubins))
    return appendPtx(fatbin.ptx, emptyPtx());
  return true;
}

// Validation additionally decompresses oversized cubins for newer targets so their headers can be checked.
std::optional<Fatbin> parseFatbin(const void *image, bool validating = false) {
  if (!image) return std::nullopt;
  const auto *wrapper = static_cast<const FatbinWrapper *>(image);
  const bool wrapped = wrapper->magic == FatbinWrapperMagic;
  const auto *data = static_cast<const uint8_t *>(wrapped ? wrapper->data : image);
  if (!data || readNative<uint32_t>(data) != FatbinMagic) return std::nullopt;
  Fatbin fatbin;
  if (!appendFatbin(fatbin, data, validating)) return std::nullopt;
  // Relocatable device code registers only stubs in the ordinary image; the entry points live in the
  // wrapper's null-terminated array of prelinked fatbins.
  if (wrapped && wrapper->version >= 2 && wrapper->prelinked)
    for (size_t index = 0; index < 64 && wrapper->prelinked[index]; ++index)
      if (!appendFatbin(fatbin, static_cast<const uint8_t *>(wrapper->prelinked[index]), validating)) return std::nullopt;
  return fatbin;
}

// PTX the selected code mode may execute; null when only the selected cubin applies.
const char *executablePtx(const std::optional<Fatbin> &fatbin) {
  return !fatbin || fatbin->ptx.empty() || (sassCodeRequired() && !fatbin->metadataOnly) ? nullptr : fatbin->ptx.c_str();
}

thread_local CUcontext currentContext = nullptr;
thread_local CUcontext contextStack[ContextCapacity];
thread_local size_t contextStackSize = 0;
std::mutex contextMutex;
std::vector<CUcontext> contexts;
std::atomic<uint64_t> nextContext{1};
std::mutex primaryContextMutex;
CUcontext primaryContext = nullptr;
uint32_t primaryContextReferences = 0;
std::atomic<CUcontext> implicitContext{nullptr};

CUcontext activeContext() { return currentContext ? currentContext : implicitContext.load(std::memory_order_acquire); }

bool contextIsLive(CUcontext context) {
  if (!context) return false;
  std::scoped_lock const lock{contextMutex};
  return contexts ^ contains(context);
}

CUresult trackContext(CUcontext context) {
  std::scoped_lock const lock{contextMutex};
  if (contexts ^ contains(context)) return CUDA_ERROR_INVALID_VALUE;
  try {
    contexts.push_back(context);
  } catch (const std::exception &) {
    return CUDA_ERROR_UNKNOWN;
  }
  return CUDA_SUCCESS;
}

// Allocation metadata is populated by successful driver RPC lifecycle hooks and
// local managed/host allocation paths, never inferred from virtual addresses.
struct PointerMetadata {
  CUdeviceptr base;
  uint64_t size;
  uint64_t id;
  CUcontext context;
  const void *module;
  bool host;
  bool managed;
};
std::mutex pointerMutex;
std::vector<PointerMetadata> pointers;

CUresult trackPointer(CUdeviceptr pointer, size_t size, uint64_t allocationId, const void *module, bool host, bool managed) {
  if (pointer == 0 || size == 0 || allocationId == 0 || size > UINT64_MAX - pointer) return CUDA_ERROR_INVALID_VALUE;
  std::scoped_lock const lock{pointerMutex};
  for (const auto &p : pointers) {
    if (p.base == pointer && p.size == size && p.module == module) return CUDA_SUCCESS;
    if (pointer < p.base + p.size && p.base < pointer + size) return CUDA_ERROR_INVALID_VALUE;
  }
  try {
    pointers.push_back(PointerMetadata{
        .base = pointer, .size = size, .id = allocationId, .context = activeContext(), .module = module, .host = host, .managed = managed});
  } catch (const std::exception &) {
    return CUDA_ERROR_UNKNOWN;
  }
  return CUDA_SUCCESS;
}

template <class Predicate> void untrackPointers(Predicate predicate) {
  std::scoped_lock const lock{pointerMutex};
  std::erase_if(pointers, predicate);
}

void untrackDevice(CUdeviceptr pointer) {
  untrackPointers([pointer](const PointerMetadata &p) { return p.base == pointer; });
}

void untrackContext(CUcontext context) {
  untrackPointers([context](const PointerMetadata &p) { return p.context == context; });
  {
    std::scoped_lock const lock{contextMutex};
    std::erase(contexts, context);
  }
  size_t output = 0;
  for (size_t index = 0; index < contextStackSize; ++index)
    if (contextStack[index] != context) contextStack[output++] = contextStack[index];
  contextStackSize = output;
  if (currentContext == context) currentContext = output == 0 ? nullptr : contextStack[--contextStackSize];
}

std::optional<PointerMetadata> pointerMetadata(CUdeviceptr pointer) {
  std::scoped_lock const lock{pointerMutex};
  return pointers | find([pointer](const PointerMetadata &p) { return pointer >= p.base && pointer - p.base < p.size; });
}

struct PointerBinding {
  uint64_t allocation;
  uint64_t offset;
  uint64_t remaining;
};

std::optional<PointerBinding> pointerBinding(CUdeviceptr pointer) {
  return pointerMetadata(pointer) ^ map([pointer](const PointerMetadata &metadata) {
           const auto offset = pointer - metadata.base;
           return PointerBinding{.allocation = metadata.id, .offset = offset, .remaining = metadata.size - offset};
         });
}

bool devicePointer(CUdeviceptr pointer) {
  const auto metadata = pointerMetadata(pointer);
  return metadata && !metadata->host;
}

ClientManager executionManager;
Client *client = nullptr;

CUresult executionReady() noexcept {
  if (executionShuttingDown.load()) return CUDA_ERROR_DEINITIALIZED;
  if (!codeModeValid()) return CUDA_ERROR_NOT_INITIALIZED;
  try {
    client = executionManager.ready() ? executionManager.client() : nullptr;
    return client ? CUDA_SUCCESS : CUDA_ERROR_NOT_INITIALIZED;
  } catch (...) {
    return CUDA_ERROR_NOT_INITIALIZED;
  }
}

std::optional<uint64_t> bindAllocation(size_t size, uintptr_t address, bool hostBacked) noexcept {
  try {
    if (const auto id = registerAllocation(*client, size, reinterpret_cast<const void *>(address), hostBacked))
      return static_cast<uint64_t>(id->value);
    return std::nullopt;
  } catch (...) {
    return std::nullopt;
  }
}

bool releaseAllocation(uint64_t id) noexcept {
  try {
    return static_cast<bool>(client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(id)}}));
  } catch (...) {
    return false;
  }
}

bool unloadProgram(uint64_t program) noexcept {
  try {
    return static_cast<bool>(client->call(UnloadProgram{ProgramId{static_cast<int64_t>(program)}}));
  } catch (...) {
    return false;
  }
}

bool upload(const PointerBinding &binding, const void *bytes, size_t size) noexcept {
  try {
    return uploadAllocation(*client, AllocationId{static_cast<int64_t>(binding.allocation)}, binding.offset, bytes, size, TransferChunk);
  } catch (...) {
    return false;
  }
}

bool download(const PointerBinding &binding, void *bytes, size_t size) noexcept {
  try {
    return downloadAllocation(*client, AllocationId{static_cast<int64_t>(binding.allocation)}, binding.offset, bytes, size, TransferChunk);
  } catch (...) {
    return false;
  }
}

bool systemPagingEnabled() { return gemstone::environment::enabled(gemstone::environment::SystemPaging); }

std::mutex stateMutex;
CUdeviceptr nextDeviceAddress = 0x0000400000000000;

CUresult allocate(CUdeviceptr *pointer, size_t size, const void *module) {
  if (!pointer || !contextIsLive(activeContext())) return CUDA_ERROR_INVALID_VALUE;
  if (executionReady() != CUDA_SUCCESS) return CUDA_ERROR_NOT_INITIALIZED;
  const size_t bytes = size == 0 ? 1 : size;
  if (bytes > UINT64_MAX - 255) return CUDA_ERROR_INVALID_VALUE;
  const uint64_t span = (static_cast<uint64_t>(bytes) + 255) & ~uint64_t{255};
  CUdeviceptr address = 0;
  {
    const std::scoped_lock lock(stateMutex);
    if (nextDeviceAddress > static_cast<uint64_t>(std::numeric_limits<int64_t>::max()) - span) return CUDA_ERROR_UNKNOWN;
    address = nextDeviceAddress;
    nextDeviceAddress += span;
  }
  const auto id = bindAllocation(bytes, address, false);
  if (!id) return CUDA_ERROR_UNKNOWN;
  if (const auto status = trackPointer(address, bytes, *id, module, false, false); status != CUDA_SUCCESS) {
    (void)releaseAllocation(*id);
    return status;
  }
  *pointer = address;
  return CUDA_SUCCESS;
}

// Host-backed allocations are registered with the service so kernels reach the host bytes directly.
CUresult registerMirror(uintptr_t pointer, size_t size, bool hostMemory) {
  if (pointer == 0 || size == 0 || executionReady() != CUDA_SUCCESS) return CUDA_ERROR_INVALID_VALUE;
  const auto id = bindAllocation(size, pointer, true);
  if (!id) return CUDA_ERROR_UNKNOWN;
  const auto status = trackPointer(pointer, size, *id, nullptr, hostMemory, !hostMemory);
  if (status != CUDA_SUCCESS) (void)releaseAllocation(*id);
  return status;
}

CUresult unregisterMirror(uintptr_t pointer) {
  const auto binding = pointerBinding(pointer);
  if (!binding || binding->offset != 0) return CUDA_ERROR_INVALID_VALUE;
  return releaseAllocation(binding->allocation) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
}

std::mutex mirrorMutex;
gemstone::MemoryRegistrations mirrors;
std::map<uintptr_t, std::unique_ptr<unsigned char[]>> mirrorStorage;

CUresult mirroredAllocate(void **pointer, size_t size, bool hostMemory, unsigned flags = 0) {
  if (!pointer) return CUDA_ERROR_INVALID_VALUE;
  const size_t bytes = size == 0 ? 1 : size;
  std::unique_ptr<unsigned char[]> storage{new (std::nothrow) unsigned char[bytes]};
  if (!storage) return CUDA_ERROR_UNKNOWN;
  const auto address = reinterpret_cast<uintptr_t>(storage.get());
  if (const auto status = registerMirror(address, bytes, hostMemory); status != CUDA_SUCCESS) return status;
  std::scoped_lock const lock{mirrorMutex};
  try {
    auto &slot = mirrorStorage[address];
    mirrors.add({.base = address, .size = bytes}, gemstone::MemoryOwnership::Owned, flags);
    slot = std::move(storage);
  } catch (const std::exception &) {
    mirrorStorage.erase(address);
    (void)unregisterMirror(address);
    return CUDA_ERROR_UNKNOWN;
  }
  *pointer = reinterpret_cast<void *>(address);
  return CUDA_SUCCESS;
}

// Empty when the pointer starts no host-backed registration.
std::optional<CUresult> freeMirror(uintptr_t pointer, gemstone::MemoryOwnership ownership = gemstone::MemoryOwnership::Owned) {
  std::unique_ptr<unsigned char[]> storage;
  {
    std::scoped_lock const lock{mirrorMutex};
    const auto *view = mirrors.exact(pointer);
    if (!view) return std::nullopt;
    if (view->ownership != ownership) return CUDA_ERROR_INVALID_VALUE;
    if (const auto status = unregisterMirror(pointer); status != CUDA_SUCCESS) return status;
    mirrors.remove(pointer, ownership);
    if (const auto found = mirrorStorage.find(pointer); found != mirrorStorage.end()) {
      storage = std::move(found->second);
      mirrorStorage.erase(found);
    }
  }
  untrackDevice(pointer);
  return CUDA_SUCCESS;
}

// Host-backed bytes are the allocation itself; false when `pointer` is not inside one registration.
bool copyMirrored(void *destination, const void *source, uintptr_t pointer, size_t size) {
  std::scoped_lock const lock{mirrorMutex};
  if (!mirrors.find(pointer, size)) return false;
  if (size != 0) std::memmove(destination, source, size);
  return true;
}

uint64_t memPoolReleaseThreshold = 0;

struct CudaArray {
  CUdeviceptr device;
  size_t width;
  size_t height;
  size_t depth;
  size_t pitch;
  CUarray_format format;
  unsigned channels;
  unsigned flags;
};

CudaArray *arrayOf(CUarray handle) { return reinterpret_cast<CudaArray *>(handle); }

bool sizeAdd(size_t left, size_t right, size_t *result) {
  if (right > SIZE_MAX - left) return false;
  *result = left + right;
  return true;
}

bool sizeMul(size_t left, size_t right, size_t *result) {
  if (left != 0 && right > SIZE_MAX / left) return false;
  *result = left * right;
  return true;
}

size_t formatBytes(CUarray_format format) {
  switch (format) {
    case CU_AD_FORMAT_UNSIGNED_INT8:
    case CU_AD_FORMAT_SIGNED_INT8: return 1;
    case CU_AD_FORMAT_UNSIGNED_INT16:
    case CU_AD_FORMAT_SIGNED_INT16:
    case CU_AD_FORMAT_HALF: return 2;
    case CU_AD_FORMAT_UNSIGNED_INT32:
    case CU_AD_FORMAT_SIGNED_INT32:
    case CU_AD_FORMAT_FLOAT: return 4;
    default: return 0;
  }
}

CUresult arrayCreate(CUarray *handle, size_t width, size_t height, size_t depth, CUarray_format format, unsigned channels, unsigned flags) {
  if (!handle) return CUDA_ERROR_INVALID_VALUE;
  const size_t actualDepth = depth == 0 ? 1 : depth;
  const size_t elementBytes = formatBytes(format);
  size_t pitch = 0;
  size_t rows = 0;
  size_t bytes = 0;
  if (width == 0 || height == 0 || channels == 0 || elementBytes == 0 || !sizeMul(width, elementBytes, &pitch)
      || !sizeMul(pitch, channels, &pitch) || !sizeMul(actualDepth, height, &rows) || !sizeMul(pitch, rows, &bytes))
    return CUDA_ERROR_INVALID_VALUE;
  auto array = std::unique_ptr<CudaArray>{new (std::nothrow) CudaArray{}};
  if (!array) return CUDA_ERROR_UNKNOWN;
  if (const auto status = cuMemAlloc_v2(&array->device, bytes); status != CUDA_SUCCESS) return status;
  array->width = width;
  array->height = height;
  array->depth = actualDepth;
  array->pitch = pitch;
  array->format = format;
  array->channels = channels;
  array->flags = flags;
  *handle = reinterpret_cast<CUarray>(array.release());
  return CUDA_SUCCESS;
}

template <class Descriptor> CUresult createArray(CUarray *handle, const Descriptor *descriptor) {
  if (!descriptor) return CUDA_ERROR_INVALID_VALUE;
  if constexpr (requires { descriptor->Depth; })
    return arrayCreate(handle, descriptor->Width, descriptor->Height, descriptor->Depth, descriptor->Format, descriptor->NumChannels,
                       descriptor->Flags);
  else return arrayCreate(handle, descriptor->Width, descriptor->Height, 1, descriptor->Format, descriptor->NumChannels, 0);
}

template <class Descriptor> CUresult describeArray(Descriptor *descriptor, CUarray handle) {
  if (!descriptor || !handle) return CUDA_ERROR_INVALID_VALUE;
  const auto *array = arrayOf(handle);
  using Extent = decltype(descriptor->Width);
  constexpr bool volume = requires { descriptor->Depth; };
  constexpr auto maximum = std::numeric_limits<Extent>::max();
  if (array->width > maximum || array->height > maximum || (volume && array->depth > maximum)) return CUDA_ERROR_NOT_SUPPORTED;
  descriptor->Width = static_cast<Extent>(array->width);
  descriptor->Height = static_cast<Extent>(array->height);
  descriptor->Format = array->format;
  descriptor->NumChannels = array->channels;
  if constexpr (volume) {
    descriptor->Depth = static_cast<Extent>(array->depth);
    descriptor->Flags = array->flags;
  }
  return CUDA_SUCCESS;
}

// Every copy shape runs as a one-slice-or-more volume; legacy descriptors widen into the current layout.
template <class Copy> CUDA_MEMCPY3D volumeCopy(const Copy &copy) {
  CUDA_MEMCPY3D volume{};
  volume.srcXInBytes = copy.srcXInBytes;
  volume.srcY = copy.srcY;
  volume.srcMemoryType = copy.srcMemoryType;
  volume.srcHost = copy.srcHost;
  volume.srcDevice = copy.srcDevice;
  volume.srcArray = copy.srcArray;
  volume.srcPitch = copy.srcPitch;
  volume.dstXInBytes = copy.dstXInBytes;
  volume.dstY = copy.dstY;
  volume.dstMemoryType = copy.dstMemoryType;
  volume.dstHost = copy.dstHost;
  volume.dstDevice = copy.dstDevice;
  volume.dstArray = copy.dstArray;
  volume.dstPitch = copy.dstPitch;
  volume.WidthInBytes = copy.WidthInBytes;
  volume.Height = copy.Height;
  if constexpr (requires { copy.Depth; }) {
    volume.srcZ = copy.srcZ;
    volume.srcLOD = copy.srcLOD;
    volume.reserved0 = copy.reserved0;
    volume.srcHeight = copy.srcHeight;
    volume.dstZ = copy.dstZ;
    volume.dstLOD = copy.dstLOD;
    volume.reserved1 = copy.reserved1;
    volume.dstHeight = copy.dstHeight;
    volume.Depth = copy.Depth;
  } else {
    volume.Depth = 1;
  }
  return volume;
}

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
    return false;
  if (height > 0
      && (!sizeAdd(y, height - 1, &end) || !sizeMul(end, rowPitch, &end) || !sizeAdd(end, x, &end) || !sizeAdd(end, width, &end)))
    return false;
  return volumeOffset(z, y, slicePitch, rowPitch, x, base);
}

bool volumeArrayBounds(const CudaArray *array, size_t x, size_t y, size_t z, size_t width, size_t height, size_t depth, size_t *base) {
  size_t end = 0;
  size_t slicePitch = 0;
  return sizeAdd(x, width, &end) && end <= array->pitch && sizeAdd(y, height, &end) && end <= array->height && sizeAdd(z, depth, &end)
         && end <= array->depth && sizeMul(array->pitch, array->height, &slicePitch)
         && volumeOffset(z, y, slicePitch, array->pitch, x, base);
}

struct CopySide {
  size_t rowPitch = 0;
  size_t slicePitch = 0;
  size_t base = 0;
};

std::optional<CopySide> copySide(CUmemorytype type, const CudaArray *array, size_t x, size_t y, size_t z, size_t pitch, size_t height,
                                 const CUDA_MEMCPY3D &copy) {
  CopySide side;
  if (type == CU_MEMORYTYPE_ARRAY) {
    if (!array || !volumeArrayBounds(array, x, y, z, copy.WidthInBytes, copy.Height, copy.Depth, &side.base)
        || !sizeMul(array->pitch, array->height, &side.slicePitch))
      return std::nullopt;
    side.rowPitch = array->pitch;
    return side;
  }
  if (type != CU_MEMORYTYPE_HOST && type != CU_MEMORYTYPE_DEVICE) return std::nullopt;
  side.rowPitch = pitch;
  if (!sizeMul(pitch, height == 0 ? copy.Height : height, &side.slicePitch)
      || !volumeBounds(x, y, z, pitch, side.slicePitch, copy.WidthInBytes, copy.Height, copy.Depth, &side.base))
    return std::nullopt;
  return side;
}

CUresult copyVolume(const CUDA_MEMCPY3D &copy) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  if (copy.WidthInBytes == 0 || copy.Height == 0 || copy.Depth == 0) return CUDA_SUCCESS;
  if (copy.reserved0 || copy.reserved1) return CUDA_ERROR_INVALID_VALUE;
  const auto *srcArray = arrayOf(copy.srcArray);
  const auto *dstArray = arrayOf(copy.dstArray);
  const auto source = copySide(copy.srcMemoryType, srcArray, copy.srcXInBytes, copy.srcY, copy.srcZ, copy.srcPitch, copy.srcHeight, copy);
  const auto destination =
      copySide(copy.dstMemoryType, dstArray, copy.dstXInBytes, copy.dstY, copy.dstZ, copy.dstPitch, copy.dstHeight, copy);
  if (!source || !destination) return CUDA_ERROR_INVALID_VALUE;

  auto row = std::unique_ptr<unsigned char[]>{new (std::nothrow) unsigned char[copy.WidthInBytes]};
  if (!row) return CUDA_ERROR_UNKNOWN;
  for (size_t z = 0; z < copy.Depth; ++z) {
    for (size_t y = 0; y < copy.Height; ++y) {
      size_t sourceOffset = 0;
      size_t destinationOffset = 0;
      if (!volumeOffset(z, y, source->slicePitch, source->rowPitch, source->base, &sourceOffset)
          || !volumeOffset(z, y, destination->slicePitch, destination->rowPitch, destination->base, &destinationOffset))
        return CUDA_ERROR_INVALID_VALUE;
      if (copy.srcMemoryType == CU_MEMORYTYPE_HOST) {
        if (!copy.srcHost) return CUDA_ERROR_INVALID_VALUE;
        std::memcpy(row.get(), static_cast<const unsigned char *>(copy.srcHost) + sourceOffset, copy.WidthInBytes);
      } else {
        const CUdeviceptr device = copy.srcMemoryType == CU_MEMORYTYPE_ARRAY ? srcArray->device : copy.srcDevice;
        if (const auto status = cuMemcpyDtoH_v2(row.get(), device + sourceOffset, copy.WidthInBytes); status != CUDA_SUCCESS) return status;
      }
      if (copy.dstMemoryType == CU_MEMORYTYPE_HOST) {
        if (!copy.dstHost) return CUDA_ERROR_INVALID_VALUE;
        std::memcpy(static_cast<unsigned char *>(copy.dstHost) + destinationOffset, row.get(), copy.WidthInBytes);
      } else {
        const CUdeviceptr device = copy.dstMemoryType == CU_MEMORYTYPE_ARRAY ? dstArray->device : copy.dstDevice;
        if (const auto status = cuMemcpyHtoD_v2(device + destinationOffset, row.get(), copy.WidthInBytes); status != CUDA_SUCCESS)
          return status;
      }
    }
  }
  return CUDA_SUCCESS;
}

template <class Copy> CUresult copyNow(const Copy *copy) { return copy ? copyVolume(volumeCopy(*copy)) : CUDA_ERROR_INVALID_VALUE; }

template <class Copy> CUresult copyAsync(const Copy *copy, CUstream stream) {
  if (!copy) return CUDA_ERROR_INVALID_VALUE;
  return enqueueCommand(stream, [volume = volumeCopy(*copy)] { return copyVolume(volume); });
}

struct Global {
  std::string name;
  CUdeviceptr address{};
  uint64_t allocation{};
  size_t size{};
};

struct Module {
  uint64_t program{};
  ProgramEncoding encoding = ProgramEncoding::PTXText;
  bool executable = true;
  std::mutex mutex;
  std::vector<std::unique_ptr<Global>> globals;
};

struct Function {
  // PTX has virtual registers; use one synthetic allocation unit when no
  // physical register count is present. Cubins report their compiler metadata.
  int registerCount = 1;
  std::array<int, 3> clusterDimensions{};
  Module *module{};
  std::string name;
  std::vector<Parameter> parameters;
  std::atomic<bool> built{false};
};

std::vector<std::unique_ptr<Module>> modules;
std::vector<std::unique_ptr<Function>> functions;

Module *findModule(const void *handle) {
  const std::scoped_lock lock(stateMutex);
  const auto entry = modules | find_cref([handle](const auto &module) { return module.get() == handle; });
  return entry ? entry->get().get() : nullptr;
}

Function *registeredFunction(const void *handle) {
  const auto entry = functions | find_cref([handle](const auto &function) { return function.get() == handle; });
  return entry ? entry->get().get() : nullptr;
}

Function *findFunction(const void *handle) {
  const std::scoped_lock lock(stateMutex);
  return registeredFunction(handle);
}

// A function and the cluster shape its attributes require, read under one lock.
std::pair<Function *, std::array<int, 3>> findLaunchFunction(const void *handle) {
  const std::scoped_lock lock(stateMutex);
  auto *entry = registeredFunction(handle);
  return {entry, entry ? entry->clusterDimensions : std::array<int, 3>{}};
}

// A kernel is built on its first launch, which reports a build failure the way a lazily loaded module does.
CUresult buildFunction(Function &function) {
  if (function.built.load()) return CUDA_SUCCESS;
  const auto built =
      client->call(BuildKernel{.program = ProgramId{static_cast<int64_t>(function.module->program)}, .kernelName = {function.name}});
  if (!built) {
    if (gemstone::traceEnabled("cuda")) std::fprintf(stderr, "Gemstone CUDA kernel build failed: %s\n", built.error().detail.c_str());
    return latchStatus(failureStatus(built.error().kind, function.module->encoding, CUDA_ERROR_LAUNCH_FAILED));
  }
  function.built.store(true);
  return CUDA_SUCCESS;
}

// Binding a global discards the kernels the service compiled for the module, so each builds again on its next launch.
void invalidateBuilds(const Module &module) {
  const std::scoped_lock lock(stateMutex);
  for (const auto &function : functions)
    if (function->module == &module) function->built.store(false);
}

size_t clusterAxis(CUfunction_attribute attribute) {
  if (attribute == CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH) return 0;
  if (attribute == CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_HEIGHT) return 1;
  return 2;
}

CUresult functionClusterAttribute(CUfunction function, CUfunction_attribute attribute, int *value, bool write) {
  auto *const entry = findFunction(function);
  if (!entry) return CUDA_ERROR_INVALID_HANDLE;
  const size_t axis = clusterAxis(attribute);
  const std::scoped_lock lock(stateMutex);
  if (write) entry->clusterDimensions[axis] = *value;
  else *value = entry->clusterDimensions[axis];
  return CUDA_SUCCESS;
}

CUresult loadModule(CUmodule *module, const void *image, bool allowMetadataOnly, bool allowCubinMetadata = false) noexcept {
  std::optional<uint64_t> loadedProgram;
  try {
    if (!module || !image || !contextIsLive(activeContext())) return CUDA_ERROR_INVALID_VALUE;
    if (executionReady() != CUDA_SUCCESS) return CUDA_ERROR_NOT_INITIALIZED;

    auto encoding = ProgramEncoding::PTXText;
    std::optional<size_t> binarySize;
    std::optional<Fatbin> fatbin;
    if (!ptxText(image)) {
      if (*static_cast<const unsigned char *>(image) == 0x7f) {
        if (ptxCodeRequired() && !allowCubinMetadata) return CUDA_ERROR_NOT_SUPPORTED;
        binarySize = cubinImageSize(image);
        if (!binarySize) return CUDA_ERROR_INVALID_IMAGE;
        encoding = ProgramEncoding::CUDACubin;
      } else {
        fatbin = parseFatbin(image);
        image = executablePtx(fatbin);
        if (!image && fatbin && !fatbin->cubin.empty() && (!ptxCodeRequired() || allowCubinMetadata)) {
          image = fatbin->cubin.data();
          binarySize = fatbin->cubin.size();
          encoding = ProgramEncoding::CUDACubin;
        }
      }
      if (!image) {
        if (gemstone::traceEnabled("cuda")) std::fprintf(stderr, "Gemstone CUDA module has neither PTX nor a compatible cubin\n");
        return CUDA_ERROR_NOT_SUPPORTED;
      }
    } else if (sassCodeRequired() && !allowMetadataOnly) {
      return CUDA_ERROR_NOT_SUPPORTED;
    }

    const auto *imageText = static_cast<const char *>(image);
    const auto size = binarySize ? *binarySize : static_cast<size_t>(std::find(imageText, imageText + MaximumPayload, '\0') - imageText);
    if (size == MaximumPayload || size > std::numeric_limits<uint32_t>::max()) return CUDA_ERROR_INVALID_VALUE;

    auto entry = std::make_unique<Module>();
    // cuBLAS registers catalogues that contain unused cubin-only kernels. Keep
    // their metadata available without granting execution on a strict PTX route.
    entry->encoding = encoding;
    entry->executable = encoding != ProgramEncoding::CUDACubin || !ptxCodeRequired();
    const auto *imageBytes = reinterpret_cast<const uint8_t *>(imageText);
    auto loaded = client->call(LoadProgram{.encoding = encoding, .image = std::vector<uint8_t>(imageBytes, imageBytes + size)});
    if (!loaded) {
      if (gemstone::traceEnabled("cuda")) std::fprintf(stderr, "Gemstone CUDA module load failed: %s\n", loaded.error().detail.c_str());
      return failureStatus(loaded.error().kind, encoding, CUDA_ERROR_UNKNOWN);
    }

    entry->program = static_cast<uint64_t>(loaded.value().value);
    loadedProgram = entry->program;
    auto *const handle = reinterpret_cast<CUmodule>(entry.get());
    {
      const std::scoped_lock lock(stateMutex);
      modules.push_back(std::move(entry));
    }
    // Compiler-generated tables can be referenced by kernels without a host
    // cuModuleGetGlobal call. Bind every declared global before any launch.
    auto catalogue = client->call(DescribeProgram{ProgramId{static_cast<int64_t>(*loadedProgram)}});
    if (!catalogue) {
      (void)cuModuleUnload(handle);
      return failureStatus(catalogue.error().kind, encoding, CUDA_ERROR_UNKNOWN);
    }
    for (const auto &symbol : catalogue.value().globals) {
      CUdeviceptr address = 0;
      if (const auto status = cuModuleGetGlobal_v2(&address, nullptr, handle, symbol.name.value.c_str()); status != CUDA_SUCCESS) {
        (void)cuModuleUnload(handle);
        return status;
      }
    }
    *module = handle;
    return CUDA_SUCCESS;
  } catch (...) {
    if (loadedProgram) (void)unloadProgram(*loadedProgram);
    return CUDA_ERROR_UNKNOWN;
  }
}

// Fatbins hand their executable PTX to the module loader; images without one fall back to their selected cubin.
CUresult loadModuleImage(CUmodule *module, const void *image, bool allowCubinMetadata) {
  if (!module || !image) return CUDA_ERROR_INVALID_VALUE;
  const auto fatbin = parseFatbin(image);
  if (!fatbin) return loadModule(module, image, false, allowCubinMetadata);
  const char *ptx = executablePtx(fatbin);
  return loadModule(module, ptx ? ptx : image, ptx && sassCodeRequired() && fatbin->metadataOnly, allowCubinMetadata);
}

template <class Reference> CUresult moduleReference(Reference *reference, CUmodule module, const char *name) {
  if (!reference || !module || !name) return CUDA_ERROR_INVALID_VALUE;
  *reference = reinterpret_cast<Reference>(module);
  return CUDA_SUCCESS;
}

struct LinkState {
  std::vector<char> image;
  size_t size = 0;
};

bool linkAppend(LinkState *state, const char *source, size_t sourceSize) {
  const size_t separator = state->size == 0 ? 0 : 1;
  if (state->size > MaximumImageSize - 1 - separator || sourceSize > MaximumImageSize - 1 - separator - state->size) return false;
  const size_t nextSize = state->size + separator + sourceSize;
  if (!gemstone::tryResize(state->image, nextSize + 1)) return false;
  if (separator != 0) state->image[state->size] = '\n';
  std::memcpy(state->image.data() + state->size + separator, source, sourceSize);
  state->image[nextSize] = '\0';
  state->size = nextSize;
  return true;
}

struct FatBinary {
  void *token = nullptr;
  CUmodule module = nullptr;
  std::string ptx;
  std::vector<uint8_t> cubin;
  std::optional<int> ptxVersion;
};

struct RegisteredVariable {
  const void *initial = nullptr;
  std::string deviceName;
  size_t size = 0;
  FatBinary *fatBinary = nullptr;
};

struct RegisteredFunction {
  const void *hostFunction = nullptr;
  CUfunction deviceFunction = nullptr;
  std::string deviceName;
  FatBinary *fatBinary = nullptr;
};

struct LaunchConfiguration {
  dim3 grid;
  dim3 block;
  size_t sharedMemory = 0;
  cudaStream_t stream = nullptr;
  bool valid = false;
};

std::mutex registrationMutex;
std::vector<std::unique_ptr<FatBinary>> fatBinaries;
std::vector<std::unique_ptr<RegisteredFunction>> registeredFunctions;
std::vector<std::unique_ptr<RegisteredVariable>> registeredVariables;
thread_local LaunchConfiguration launchConfiguration{};
thread_local cudaError_t lastError = cudaSuccess;

CUresult ensureRuntimeContext() {
  if (currentContext) return CUDA_SUCCESS;
  CUcontext context = nullptr;
  const auto status = cuDevicePrimaryCtxRetain(&context, 0);
  return status == CUDA_SUCCESS ? cuCtxSetCurrent(context) : status;
}

CUresult registerGlobal(CUmodule module, const char *name, const void *initial, size_t size) {
  CUdeviceptr address = 0;
  size_t declared = 0;
  auto status = cuModuleGetGlobal_v2(&address, &declared, module, name);
  if (status == CUDA_SUCCESS && size > declared) status = CUDA_ERROR_INVALID_VALUE;
  if (status == CUDA_SUCCESS && size != 0) status = cuMemcpyHtoD_v2(address, initial, size);
  return status;
}

// Callers hold registrationMutex.
CUresult loadFatBinary(FatBinary &fat) {
  if (fat.module) return CUDA_SUCCESS;
  if (const auto status = ensureRuntimeContext(); status != CUDA_SUCCESS) return status;
  if (fat.ptx.empty() && fat.cubin.empty()) return CUDA_ERROR_NOT_SUPPORTED;

  auto status = cuModuleLoadData(&fat.module, fat.ptx.empty() ? static_cast<const void *>(fat.cubin.data()) : fat.ptx.c_str());
  if (status != CUDA_SUCCESS) return status;
  for (const auto &variable : registeredVariables) {
    if (variable->fatBinary != &fat) continue;
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
    status = registerGlobal(fat.module, variable->deviceName.c_str(), initial, variable->size);
    if (status != CUDA_SUCCESS) break;
  }
  if (status != CUDA_SUCCESS) {
    (void)cuModuleUnload(fat.module);
    fat.module = nullptr;
  }
  return status;
}

std::optional<int> registeredPtxVersion(const void *function) {
  auto version = configuredPtxVersion();
  std::scoped_lock const lock{registrationMutex};
  if (!version) {
    for (const auto &entry : registeredFunctions) {
      if (entry->hostFunction == function || entry->deviceFunction == function) {
        version = entry->fatBinary->ptxVersion;
        break;
      }
    }
    if (!version && !fatBinaries.empty()) version = fatBinaries.front()->ptxVersion;
  }
  return version;
}

// Resolves a host stub registered by __cudaRegisterFunction, loading its fatbin on first use; empty when unregistered.
std::optional<CUresult> registeredKernel(const void *hostFunction, CUfunction *kernel) {
  std::scoped_lock const lock{registrationMutex};
  const auto found = registeredFunctions | find_cref([hostFunction](const auto &entry) { return entry->hostFunction == hostFunction; });
  if (!found) return std::nullopt;
  auto &entry = *found->get();
  auto status = loadFatBinary(*entry.fatBinary);
  if (status == CUDA_SUCCESS && !entry.deviceFunction)
    status = cuModuleGetFunction(&entry.deviceFunction, entry.fatBinary->module, entry.deviceName.c_str());
  if (status == CUDA_SUCCESS) *kernel = entry.deviceFunction;
  return status;
}

CUresult functionRegisterCount(const void *function, int *value) {
  CUfunction kernel = nullptr;
  const auto status = registeredKernel(function, &kernel);
  if (status && *status != CUDA_SUCCESS) return *status;
  auto *const entry = findFunction(status ? kernel : function);
  if (!entry) return CUDA_ERROR_INVALID_HANDLE;
  *value = entry->registerCount;
  return CUDA_SUCCESS;
}

CUresult occupancyMaxBlocks(int *blocks, int blockSize) {
  if (!blocks || blockSize <= 0 || blockSize > device_limits::MaxThreadsPerBlock) return CUDA_ERROR_INVALID_VALUE;
  *blocks = std::max(1, device_limits::MaxThreadsPerBlock / blockSize);
  return CUDA_SUCCESS;
}

std::array<uint32_t, 3> clusterDimension(const CUlaunchAttribute &attribute) {
  return {attribute.value.clusterDim.x, attribute.value.clusterDim.y, attribute.value.clusterDim.z};
}

CUresult copyString(char *destination, int length, std::string_view source) {
  if (!destination || length <= 0) return CUDA_ERROR_INVALID_VALUE;
  const size_t size = std::min(source.size(), static_cast<size_t>(length) - 1);
  std::memcpy(destination, source.data(), size);
  destination[size] = '\0';
  return CUDA_SUCCESS;
}

struct ErrorText {
  CUresult status;
  const char *driverName;
  const char *runtimeName;
  const char *message;
};

constexpr std::array ErrorTexts{
    ErrorText{.status = CUDA_SUCCESS, .driverName = "CUDA_SUCCESS", .runtimeName = "cudaSuccess", .message = "no error"},
    ErrorText{.status = CUDA_ERROR_INVALID_VALUE,
              .driverName = "CUDA_ERROR_INVALID_VALUE",
              .runtimeName = "cudaErrorInvalidValue",
              .message = "invalid argument"},
    ErrorText{.status = CUDA_ERROR_NOT_INITIALIZED,
              .driverName = "CUDA_ERROR_NOT_INITIALIZED",
              .runtimeName = "cudaErrorInitializationError",
              .message = "initialization error"},
    ErrorText{.status = CUDA_ERROR_NOT_FOUND,
              .driverName = "CUDA_ERROR_NOT_FOUND",
              .runtimeName = "cudaErrorSymbolNotFound",
              .message = "named symbol not found"},
    ErrorText{.status = CUDA_ERROR_NOT_SUPPORTED,
              .driverName = "CUDA_ERROR_NOT_SUPPORTED",
              .runtimeName = "cudaErrorNotSupported",
              .message = "operation not supported"},
    ErrorText{
        .status = CUDA_ERROR_UNKNOWN, .driverName = "CUDA_ERROR_UNKNOWN", .runtimeName = "cudaErrorUnknown", .message = "unknown error"},
    ErrorText{.status = InvalidConfiguration,
              .driverName = nullptr,
              .runtimeName = "cudaErrorInvalidConfiguration",
              .message = "invalid configuration argument"},
    ErrorText{.status = CUDA_ERROR_INVALID_IMAGE,
              .driverName = "CUDA_ERROR_INVALID_IMAGE",
              .runtimeName = "cudaErrorInvalidKernelImage",
              .message = "device kernel image is invalid"},
    ErrorText{.status = CUDA_ERROR_NO_BINARY_FOR_GPU,
              .driverName = "CUDA_ERROR_NO_BINARY_FOR_GPU",
              .runtimeName = "cudaErrorNoKernelImageForDevice",
              .message = "no kernel image is available for execution on the device"},
    ErrorText{.status = CUDA_ERROR_INVALID_PTX,
              .driverName = "CUDA_ERROR_INVALID_PTX",
              .runtimeName = "cudaErrorInvalidPtx",
              .message = "a PTX JIT compilation failed"},
    ErrorText{.status = CUDA_ERROR_ILLEGAL_ADDRESS,
              .driverName = "CUDA_ERROR_ILLEGAL_ADDRESS",
              .runtimeName = "cudaErrorIllegalAddress",
              .message = "an illegal memory access was encountered"},
    ErrorText{.status = CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES,
              .driverName = "CUDA_ERROR_LAUNCH_OUT_OF_RESOURCES",
              .runtimeName = "cudaErrorLaunchOutOfResources",
              .message = "too many resources requested for launch"},
    ErrorText{.status = CUDA_ERROR_LAUNCH_TIMEOUT,
              .driverName = "CUDA_ERROR_LAUNCH_TIMEOUT",
              .runtimeName = "cudaErrorLaunchTimeout",
              .message = "the launch timed out and was terminated"},
    ErrorText{.status = CUDA_ERROR_ASSERT,
              .driverName = "CUDA_ERROR_ASSERT",
              .runtimeName = "cudaErrorAssert",
              .message = "device-side assert triggered"},
    ErrorText{.status = CUDA_ERROR_ILLEGAL_INSTRUCTION,
              .driverName = "CUDA_ERROR_ILLEGAL_INSTRUCTION",
              .runtimeName = "cudaErrorIllegalInstruction",
              .message = "an illegal instruction was encountered"},
    ErrorText{.status = CUDA_ERROR_MISALIGNED_ADDRESS,
              .driverName = "CUDA_ERROR_MISALIGNED_ADDRESS",
              .runtimeName = "cudaErrorMisalignedAddress",
              .message = "misaligned address"},
    ErrorText{.status = CUDA_ERROR_LAUNCH_FAILED,
              .driverName = "CUDA_ERROR_LAUNCH_FAILED",
              .runtimeName = "cudaErrorLaunchFailure",
              .message = "unspecified launch failure"},
};

const ErrorText *errorText(int status) {
  const auto found = ErrorTexts | find_cref([status](const auto &text) { return text.status == status; });
  return found ? &found->get() : nullptr;
}

CUresult describeError(CUresult status, const char **text, const char *ErrorText::*field) {
  if (!text) return CUDA_ERROR_INVALID_VALUE;
  const auto *found = errorText(status);
  if (!found || !(found->*field)) return CUDA_ERROR_INVALID_VALUE;
  *text = found->*field;
  return CUDA_SUCCESS;
}

CUresult cudartLoadModuleImage(CUmodule *module, const void *image, bool allowEmpty) {
  const auto fatbin = parseFatbin(image, true);
  if (!fatbin || !fatbin->entriesValid) return CUDA_ERROR_NOT_SUPPORTED;
  const char *ptx = executablePtx(fatbin);
  if (ptx) return loadModule(module, ptx, sassCodeRequired() && fatbin->metadataOnly);
  if (fatbin->incompatibleExecutable && !fatbin->compatibleExecutable) return CUDA_ERROR_NO_BINARY_FOR_GPU;
  if (!fatbin->cubin.empty()) return loadModule(module, fatbin->cubin.data(), false, true);
  if (!allowEmpty || !fatbin->metadataOnly) return CUDA_ERROR_NOT_SUPPORTED;
  return loadModule(module, emptyPtx().c_str(), true);
}

CUresult cudartGetModuleFromCubin(CUmodule *module, const void *fatbinWrapper) {
  if (!module) return CUDA_ERROR_NOT_SUPPORTED;
  return cudartLoadModuleImage(module, fatbinWrapper, false);
}

// The runtime asks the driver to materialise header-only fatbin records while bootstrapping libraries; they
// load as empty modules so a later lookup reports the ordinary missing-entry result.
CUresult cudartGetModuleFromCubinExt1(CUmodule *module, const void *fatbinWrapper, void *, void *, unsigned) {
  if (!module) return CUDA_ERROR_INVALID_VALUE;
  return cudartLoadModuleImage(module, fatbinWrapper, true);
}

void cudartInterfaceFn7(uintptr_t) {}

CUresult cudartGetModuleFromCubinExt2(const void *fatbinHeader, CUmodule *module, void *, void *, unsigned) {
  if (!module) return CUDA_ERROR_NOT_SUPPORTED;
  return cudartLoadModuleImage(module, fatbinHeader, false);
}

CUresult cudartLoadCompilers() { return CUDA_SUCCESS; }

const unsigned char CudartInterfaceId[16] = {0x6b, 0xd5, 0xfb, 0x6c, 0x5b, 0xf4, 0xe7, 0x4a,
                                             0x89, 0x87, 0xd9, 0x39, 0x12, 0xfd, 0x9d, 0xf9};
const unsigned char CudartLoggingId[16] = {0xf8, 0xcf, 0xf9, 0x51, 0x21, 0x46, 0x8b, 0x4e, 0xb9, 0xe2, 0xfb, 0x46, 0x9e, 0x7c, 0x0d, 0xd9};
const unsigned char CudartHandshakeCheckId[16] = {0xd4, 0x08, 0x20, 0x55, 0xbd, 0xe6, 0x70, 0x4b,
                                                  0x8d, 0x34, 0xba, 0x12, 0x3c, 0x66, 0xe1, 0xf2};
const unsigned char ToolsRuntimeCallbackHooksId[16] = {0xa0, 0x94, 0x79, 0x8c, 0x2e, 0x74, 0x2e, 0x74,
                                                       0x93, 0xf2, 0x08, 0x00, 0x20, 0x0c, 0x0a, 0x66};
const unsigned char ToolsTlsId[16] = {0x42, 0xd8, 0x5a, 0x81, 0x23, 0xf6, 0xcb, 0x47, 0x82, 0x98, 0xf6, 0xe7, 0x8a, 0x3a, 0xec, 0xdc};
const unsigned char ContextLocalStorageId[16] = {0xc6, 0x93, 0x33, 0x6e, 0x11, 0x21, 0xdf, 0x11,
                                                 0xa8, 0xc3, 0x68, 0xf3, 0x55, 0xd8, 0x95, 0x93};
const unsigned char ContextChecksId[16] = {0x26, 0x3e, 0x88, 0x60, 0x7c, 0xd2, 0x61, 0x43, 0x92, 0xf6, 0xbb, 0xd5, 0x00, 0x6d, 0xfa, 0x7e};
const unsigned char DriverMetadataInterfaceId[16] = {0x21, 0x31, 0x8c, 0x60, 0x97, 0x14, 0x32, 0x48,
                                                     0x8c, 0xa6, 0x41, 0xff, 0x73, 0x24, 0xc8, 0xf2};

// Buffer sizes are fixed by the CUDA tools-runtime callback ABI.
constexpr size_t ToolsRuntimeCallbackBuffer1Words = 1024;
constexpr size_t ToolsRuntimeCallbackBuffer2Words = 14;

uint32_t toolsRuntimeCallbackBuffer1[ToolsRuntimeCallbackBuffer1Words];
uint32_t toolsRuntimeCallbackBuffer2[ToolsRuntimeCallbackBuffer2Words];

void toolsRuntimeCallbackGetBuffer1(void **pointer, size_t *size) {
  if (pointer) *pointer = toolsRuntimeCallbackBuffer1;
  if (size) *size = ToolsRuntimeCallbackBuffer1Words;
}

void toolsRuntimeCallbackGetBuffer2(void **pointer, size_t *size) {
  if (pointer) *pointer = toolsRuntimeCallbackBuffer2;
  if (size) *size = ToolsRuntimeCallbackBuffer2Words;
}

const void *cudartInterfaceTable[13] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(13 * sizeof(void *))),
                                        reinterpret_cast<const void *>(cudartGetModuleFromCubin),
                                        reinterpret_cast<const void *>(cuDevicePrimaryCtxRetain),
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

void cudartLog(const void *, int, const char *, void *) {}

void cudartSetLogMode(unsigned) {}

const void *cudartLoggingTable[3] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(3 * sizeof(void *))),
                                     reinterpret_cast<const void *>(cudartLog), reinterpret_cast<const void *>(cudartSetLogMode)};

// CUDA libraries use this private table for a small typed query on their opaque driver metadata.
CUresult driverMetadataQuery(void *object, uint64_t *result) {
  if (!result) return CUDA_ERROR_UNKNOWN;
  *result = 0;
  if (!object) return CUDA_SUCCESS;
  const auto kind = readNative<uint32_t>(object);
  if (kind == 0) *result = readNative<uint32_t>(object, 0x60);
  else if (kind == 1) *result = readNative<uint64_t>(object, 0x60);
  return CUDA_SUCCESS;
}

// Private driver stream queries used for per-context workspace bookkeeping.
// The facade has no separate internal stream object; its public handle is the
// canonical identity, with CUDA's special streams normalised consistently.
CUresult driverStreamHandle(void *, CUstream stream, CUstream *result, int perThread) {
  if (!result) return CUDA_ERROR_INVALID_VALUE;
  if (stream == LegacyStream) stream = nullptr;
  if (!stream && perThread) stream = PerThreadStream;
  *result = stream;
  return CUDA_SUCCESS;
}

CUresult driverStreamId(void *, CUstream stream, uint64_t *result) {
  if (!result) return CUDA_ERROR_INVALID_VALUE;
  static thread_local const unsigned char perThread = 0;
  if (stream == LegacyStream) *result = 0;
  else if (stream == PerThreadStream) *result = reinterpret_cast<uintptr_t>(&perThread);
  else *result = reinterpret_cast<uintptr_t>(stream);
  return CUDA_SUCCESS;
}

const std::array<const void *, 96> DriverMetadataInterfaceTable = [] {
  std::array<const void *, 96> table{};
  table[0] = reinterpret_cast<const void *>(uintptr_t{0x300});
  table[4] = reinterpret_cast<const void *>(driverMetadataQuery);
  table[39] = reinterpret_cast<const void *>(driverStreamId);
  table[51] = reinterpret_cast<const void *>(driverStreamHandle);
  return table;
}();

// libcudart's private export-table handshake is an ABI compatibility transform, not a security mechanism.
const uint8_t CudartHandshakeMixingTable[256] = {
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
  uint8_t const position = value[0x40];
  value[static_cast<size_t>(position) + 0x10] = input;
  const uint8_t nextPosition = static_cast<uint8_t>((position + 1) & 0x0f);
  value[static_cast<size_t>(position) + 0x20] = value[position] ^ input;
  const uint8_t mixed = CudartHandshakeMixingTable[static_cast<uint8_t>(input ^ value[0x41])];
  value[static_cast<size_t>(position) + 0x30] = mixed ^ value[static_cast<size_t>(position) + 0x30];
  value[0x41] = value[static_cast<size_t>(position) + 0x30];
  value[0x40] = nextPosition;
  if (nextPosition != 0) return;
  uint8_t accumulator = 0x29;
  uint8_t counter = 0;
  for (;;) {
    accumulator ^= value[0];
    value[0] = accumulator;
    for (size_t index = 1; index < 0x30; ++index) {
      accumulator = value[index] ^ CudartHandshakeMixingTable[accumulator];
      value[index] = accumulator;
    }
    accumulator = static_cast<uint8_t>(accumulator + counter);
    ++counter;
    if (counter == 0x12) break;
    accumulator = CudartHandshakeMixingTable[accumulator];
  }
}

void cudartHandshakeMixBytes(uint8_t *value, const void *input, size_t size, uint8_t xorMask) {
  const uint8_t *bytes = static_cast<const uint8_t *>(input);
  for (size_t index = 0; index < size; ++index) {
    cudartHandshakeSinglePass(value, static_cast<uint8_t>(bytes[index] ^ xorMask));
  }
}

struct CudartHandshakeInput {
  uint32_t driverVersion;
  uint32_t version;
  uint32_t currentProcess;
  uint32_t currentThread;
  const void *cudartTable;
  const void *handshakeTable;
  const void *handshakeFunction;
  uint64_t unixSeconds;
};

struct CudartHandshakeDevice {
  uint8_t uuid[16];
  int32_t pciDomain;
  int32_t pciBus;
  int32_t pciDevice;
};

extern const void *cudartHandshakeCheckTable[3];

void cudartHandshakeZeroResult(uint8_t *value) {
  std::fill_n(value, 16, uint8_t{0});
  std::fill_n(value + 48, 18, uint8_t{0});
}

void cudartHandshakeFinish(uint8_t *value) {
  const auto padding = static_cast<uint8_t>(16 - value[64]);
  for (uint8_t index = 0; index < padding; ++index)
    cudartHandshakeSinglePass(value, padding);
  for (uint8_t *cursor = value + 48; cursor < value + 64; ++cursor)
    cudartHandshakeSinglePass(value, *cursor);
}

void cudartHandshakeTransform(uint32_t version, uint64_t unixSeconds, const void *handshakeFunction, uint8_t output[16]) {
  static const uint8_t pass1Result[16] = {0x14, 0x6a, 0xdd, 0xae, 0x53, 0xa9, 0xa7, 0x52, 0xaa, 0x08, 0x41, 0x36, 0x0b, 0xf5, 0x5a, 0x9f};
  uint8_t value[66] = {0};
  cudartHandshakeMixBytes(value, pass1Result, sizeof(pass1Result), 0x36);
  const CudartHandshakeInput input = {.driverVersion = CUDA_VERSION,
                                      .version = version,
                                      .currentProcess = static_cast<uint32_t>(getpid()),
                                      .currentThread = static_cast<uint32_t>(static_cast<uintptr_t>(pthread_self())),
                                      .cudartTable = cudartInterfaceTable,
                                      .handshakeTable = cudartHandshakeCheckTable,
                                      .handshakeFunction = handshakeFunction,
                                      .unixSeconds = unixSeconds};
  cudartHandshakeMixBytes(value, &input, sizeof(input), 0);
  CudartHandshakeDevice device{};
  std::memcpy(device.uuid, DeviceUuid, sizeof(DeviceUuid));
  device.pciDomain = device_limits::PciDomainId;
  device.pciBus = device_limits::PciBusId;
  device.pciDevice = device_limits::PciDeviceId;
  cudartHandshakeMixBytes(value, &device, sizeof(device), 0);
  cudartHandshakeFinish(value);
  uint8_t pass5Result[16];
  std::memcpy(pass5Result, value, sizeof(pass5Result));
  cudartHandshakeZeroResult(value);
  cudartHandshakeMixBytes(value, pass1Result, sizeof(pass1Result), 0x5c);
  cudartHandshakeMixBytes(value, pass5Result, sizeof(pass5Result), 0);
  cudartHandshakeFinish(value);
  std::memcpy(output, value, sizeof(pass5Result));
}

CUresult cudartHandshakeCheck(uint32_t version, uint64_t unixSeconds, uint64_t *result) {
  if (!result) return CUDA_ERROR_INVALID_VALUE;
  if (version % 10 == 0) {
    result[0] = uint64_t{0x3341181c03cb675c};
    result[1] = uint64_t{0x8ed383aa1f4cd1e8};
    return CUDA_SUCCESS;
  }
  if (version % 10 == 1) {
    result[0] = uint64_t{0x1841181c03cb675c};
    result[1] = uint64_t{0x8ed383aa1f4cd1e8};
    return CUDA_SUCCESS;
  }
  cudartHandshakeTransform(version, unixSeconds, reinterpret_cast<const void *>(cudartHandshakeCheck), reinterpret_cast<uint8_t *>(result));
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

CUresult toolsTlsGet(void **value, void *, const void *, void *, void *, void *) {
  if (!value) return CUDA_ERROR_UNKNOWN;
  *value = nullptr;
  return CUDA_SUCCESS;
}

const void *toolsTlsTable[4] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(4 * sizeof(void *))), nullptr,
                                reinterpret_cast<const void *>(toolsTlsGet), nullptr};

using ContextStorageDestructor = void (*)(void *, void *, void *);
struct ContextStorage {
  CUcontext context;
  void *key;
  void *value;
  ContextStorageDestructor destructor;
};
ContextStorage contextStorage[ContextCapacity];

ContextStorage *findContextStorage(CUcontext context, const void *key) {
  for (auto &entry : contextStorage)
    if (entry.context == context && entry.key == key) return &entry;
  return nullptr;
}

CUresult contextLocalStoragePut(CUcontext context, void *key, void *value, ContextStorageDestructor destructor) {
  context = context ? context : currentContext;
  if (!context || !key) return CUDA_ERROR_INVALID_VALUE;
  auto *entry = findContextStorage(context, key);
  if (!entry) entry = findContextStorage(nullptr, nullptr);
  if (!entry) return CUDA_ERROR_UNKNOWN;
  *entry = {.context = context, .key = key, .value = value, .destructor = destructor};
  return CUDA_SUCCESS;
}

CUresult contextLocalStorageDelete(CUcontext context, void *key) {
  context = context ? context : currentContext;
  if (!context || !key) return CUDA_ERROR_INVALID_VALUE;
  if (auto *entry = findContextStorage(context, key)) *entry = {};
  return CUDA_SUCCESS;
}

CUresult contextLocalStorageGet(void **value, CUcontext context, void *key) {
  context = context ? context : currentContext;
  if (!value || !key) return CUDA_ERROR_INVALID_VALUE;
  if (!context) return CUDA_ERROR_INVALID_CONTEXT;
  const auto *entry = findContextStorage(context, key);
  if (!entry) return CUDA_ERROR_INVALID_HANDLE;
  *value = entry->value;
  return CUDA_SUCCESS;
}

const void *contextLocalStorageTable[4] = {reinterpret_cast<const void *>(contextLocalStoragePut),
                                           reinterpret_cast<const void *>(contextLocalStorageDelete),
                                           reinterpret_cast<const void *>(contextLocalStorageGet), nullptr};

CUresult contextCheck(CUcontext, unsigned *result, const void **) {
  if (!result) return CUDA_ERROR_INVALID_VALUE;
  *result = 0;
  return CUDA_SUCCESS;
}

unsigned contextCheckFn3() { return 0; }

const void *contextChecksTable[4] = {reinterpret_cast<const void *>(static_cast<uintptr_t>(4 * sizeof(void *))), nullptr,
                                     reinterpret_cast<const void *>(contextCheck), reinterpret_cast<const void *>(contextCheckFn3)};

struct ExportTable {
  const unsigned char *id;
  const void *table;
};

const std::array ExportTables{
    ExportTable{.id = CudartLoggingId, .table = cudartLoggingTable},
    ExportTable{.id = CudartInterfaceId, .table = cudartInterfaceTable},
    ExportTable{.id = CudartHandshakeCheckId, .table = cudartHandshakeCheckTable},
    ExportTable{.id = ToolsRuntimeCallbackHooksId, .table = toolsRuntimeCallbackHooksTable},
    ExportTable{.id = ToolsTlsId, .table = toolsTlsTable},
    ExportTable{.id = ContextLocalStorageId, .table = contextLocalStorageTable},
    ExportTable{.id = ContextChecksId, .table = contextChecksTable},
    ExportTable{.id = DriverMetadataInterfaceId, .table = DriverMetadataInterfaceTable.data()},
};

void *libraryHandle() {
  static void *const handle = [] {
    Dl_info info{};
    return dladdr(reinterpret_cast<const void *>(&libraryHandle), &info) && info.dli_fname ? dlopen(info.dli_fname, RTLD_NOW | RTLD_NOLOAD)
                                                                                           : nullptr;
  }();
  return handle;
}

void *exported(const std::string &name) { return dlsym(libraryHandle(), name.c_str()); }

bool explicitRevision(std::string_view symbol) {
  const auto suffix = symbol.rfind("_v");
  return suffix != std::string_view::npos && suffix + 2 < symbol.size()
         && symbol.substr(suffix + 2).find_first_not_of("0123456789") == std::string_view::npos;
}

struct EntryPoint {
  void *function;
  CUdriverProcAddressQueryResult status;
};

// Driver lookups resolve exported symbols, so lookup and dlsym callers share one implementation. A base name
// selects the revision introduced by `version`. Revisions differ in ABI, so another revision never stands in for
// it: a known revision without an export resolves as unsupported. A function without a vendor history selects
// its newest export. Per-thread-stream spellings without their own export resolve as the base entry point.
EntryPoint resolveEntryPoint(std::string_view symbol, int version) {
  constexpr EntryPoint missing{.function = nullptr, .status = CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND};
  if (symbol.size() < 3 || !symbol.starts_with("cu") || !std::isupper(static_cast<unsigned char>(symbol[2]))) return missing;
  auto *const unsupported = reinterpret_cast<void *>(&gemstone_cuda_unsupported_driver);
  if (symbol.ends_with("_ptds") || symbol.ends_with("_ptsz")) {
    if (void *function = exported(std::string(symbol))) return {.function = function, .status = CU_GET_PROC_ADDRESS_SUCCESS};
    return resolveEntryPoint(symbol.substr(0, symbol.size() - 5), version);
  }
  if (explicitRevision(symbol)) {
    void *function = exported(std::string(symbol));
    return function ? EntryPoint{.function = function, .status = CU_GET_PROC_ADDRESS_SUCCESS} : missing;
  }
  using gemstone::cuda::EntryPointHistories;
  const auto *const history =
      std::lower_bound(EntryPointHistories.begin(), EntryPointHistories.end(), symbol,
                       [](const gemstone::cuda::EntryPointHistory &entry, std::string_view name) { return entry.name < name; });
  const bool listed = history != EntryPointHistories.end() && history->name == symbol;
  const int revisions = listed ? static_cast<int>(history->revisions.size()) : 4;
  const int effective = version > 0 ? version : std::numeric_limits<int>::max();
  if (listed && effective < history->revisions[0]) return {.function = nullptr, .status = CU_GET_PROC_ADDRESS_VERSION_NOT_SUFFICIENT};
  int requested = listed ? 1 : revisions;
  for (int revision = 2; listed && revision <= revisions; ++revision) {
    const int since = history->revisions[revision - 1];
    if (since != 0 && since <= effective) requested = revision;
  }
  const auto revisionExport = [symbol](int revision) {
    return exported(revision == 1 ? std::string(symbol) : std::string(symbol) + "_v" + std::to_string(revision));
  };
  for (int revision = requested; revision >= 1; --revision) {
    if (void *function = revisionExport(revision)) return {.function = function, .status = CU_GET_PROC_ADDRESS_SUCCESS};
    if (listed) break;
  }
  for (int revision = 1; revision <= revisions; ++revision)
    if (revisionExport(revision)) return {.function = unsupported, .status = CU_GET_PROC_ADDRESS_SUCCESS};
  return missing;
}

// Defined after every other registry so that its workers are joined first at exit, and initialised with the
// library, before libcudart registers the exit handlers that unload its modules through this queue.
struct CommandQueueOwner {
  std::unique_ptr<gemstone::CommandQueue> queue;
  ~CommandQueueOwner() { executionShuttingDown.store(true); }
} commandQueue;
std::once_flag commandQueueOnce;

gemstone::CommandQueue &commands() {
  std::call_once(commandQueueOnce, [] { commandQueue.queue = std::make_unique<gemstone::CommandQueue>(CUDA_ERROR_UNKNOWN); });
  return *commandQueue.queue;
}

// Tensor maps in the layout the VM decodes. Fields tensormap.replace reaches sit where ptxas writes them; the mode,
// L2 promotion and im2col fields follow from byte 0x40.
namespace tensor_map {
constexpr size_t Bytes = 128;
constexpr uint32_t RankShift = 4;
constexpr uint32_t ElementShift = 7;
constexpr uint32_t InterleaveShift = 11;
constexpr uint32_t SwizzleShift = 13;
constexpr uint32_t NaNFillBit = 1U << 15;
constexpr uint32_t TF32Bit = 1U << 16;
constexpr uint32_t AtomicityShift = 19;
constexpr uint32_t SwizzleHighBit = 1U << 22;

enum class Mode : uint32_t { Tile = 0, Im2col = 1, Im2colWide = 2, Im2colWide128 = 3 };

// Packed types move in units of `values` elements, `bytes` of them in global memory and `stored` in shared memory.
struct Element {
  uint32_t code;
  uint32_t bytes;
  bool tf32;
  bool floating;
  uint32_t values = 1;
  uint32_t stored = 0;

  [[nodiscard]] bool padded() const { return stored != 0 && stored != bytes; }
  // The shared memory bytes of `count` elements.
  [[nodiscard]] uint64_t shared(uint64_t count) const { return count / values * (stored != 0 ? stored : bytes); }
};

// The hardware element codes for CUtensorMapDataType.
std::optional<Element> element(CUtensorMapDataType type) {
  switch (type) {
    case CU_TENSOR_MAP_DATA_TYPE_UINT8: return Element{.code = 0, .bytes = 1, .tf32 = false, .floating = false};
    case CU_TENSOR_MAP_DATA_TYPE_UINT16: return Element{.code = 1, .bytes = 2, .tf32 = false, .floating = false};
    case CU_TENSOR_MAP_DATA_TYPE_UINT32: return Element{.code = 2, .bytes = 4, .tf32 = false, .floating = false};
    case CU_TENSOR_MAP_DATA_TYPE_INT32: return Element{.code = 3, .bytes = 4, .tf32 = false, .floating = false};
    case CU_TENSOR_MAP_DATA_TYPE_UINT64: return Element{.code = 4, .bytes = 8, .tf32 = false, .floating = false};
    case CU_TENSOR_MAP_DATA_TYPE_INT64: return Element{.code = 5, .bytes = 8, .tf32 = false, .floating = false};
    case CU_TENSOR_MAP_DATA_TYPE_FLOAT16: return Element{.code = 6, .bytes = 2, .tf32 = false, .floating = true};
    case CU_TENSOR_MAP_DATA_TYPE_FLOAT32: return Element{.code = 7, .bytes = 4, .tf32 = false, .floating = true};
    case CU_TENSOR_MAP_DATA_TYPE_FLOAT32_FTZ: return Element{.code = 8, .bytes = 4, .tf32 = false, .floating = true};
    case CU_TENSOR_MAP_DATA_TYPE_FLOAT64: return Element{.code = 9, .bytes = 8, .tf32 = false, .floating = true};
    case CU_TENSOR_MAP_DATA_TYPE_BFLOAT16: return Element{.code = 10, .bytes = 2, .tf32 = false, .floating = true};
    case CU_TENSOR_MAP_DATA_TYPE_TFLOAT32: return Element{.code = 7, .bytes = 4, .tf32 = true, .floating = true};
    case CU_TENSOR_MAP_DATA_TYPE_TFLOAT32_FTZ: return Element{.code = 8, .bytes = 4, .tf32 = true, .floating = true};
    case CU_TENSOR_MAP_DATA_TYPE_16U4_ALIGN8B:
      return Element{.code = 11, .bytes = 1, .tf32 = false, .floating = false, .values = 2, .stored = 1};
    case CU_TENSOR_MAP_DATA_TYPE_16U4_ALIGN16B:
      return Element{.code = 12, .bytes = 8, .tf32 = false, .floating = false, .values = 16, .stored = 16};
    case CU_TENSOR_MAP_DATA_TYPE_16U6_ALIGN16B:
      return Element{.code = 13, .bytes = 12, .tf32 = false, .floating = false, .values = 16, .stored = 16};
    default: return std::nullopt;
  }
}

struct Swizzle {
  uint32_t mode;
  uint32_t atomicity;
  uint32_t span;
};

std::optional<Swizzle> swizzle(CUtensorMapSwizzle value) {
  switch (value) {
    case CU_TENSOR_MAP_SWIZZLE_NONE: return Swizzle{.mode = 0, .atomicity = 0, .span = 0};
    case CU_TENSOR_MAP_SWIZZLE_32B: return Swizzle{.mode = 1, .atomicity = 0, .span = 32};
    case CU_TENSOR_MAP_SWIZZLE_64B: return Swizzle{.mode = 2, .atomicity = 0, .span = 64};
    case CU_TENSOR_MAP_SWIZZLE_128B: return Swizzle{.mode = 3, .atomicity = 0, .span = 128};
    case CU_TENSOR_MAP_SWIZZLE_128B_ATOM_32B: return Swizzle{.mode = 3, .atomicity = 1, .span = 128};
    case CU_TENSOR_MAP_SWIZZLE_128B_ATOM_32B_FLIP_8B: return Swizzle{.mode = 3, .atomicity = 2, .span = 128};
    case CU_TENSOR_MAP_SWIZZLE_128B_ATOM_64B: return Swizzle{.mode = 3, .atomicity = 3, .span = 128};
    default: return std::nullopt;
  }
}

void put32(uint8_t *bytes, size_t offset, uint32_t value) { std::memcpy(bytes + offset, &value, sizeof(value)); }
uint32_t get32(const uint8_t *bytes, size_t offset) {
  uint32_t value = 0;
  std::memcpy(&value, bytes + offset, sizeof(value));
  return value;
}

struct Common {
  CUtensorMapDataType type;
  uint32_t rank;
  void *address;
  const cuuint64_t *dimensions;
  const cuuint64_t *strides;
  const cuuint32_t *traversal;
  CUtensorMapInterleave interleave;
  CUtensorMapSwizzle swizzle;
  CUtensorMapL2promotion promotion;
  CUtensorMapFloatOOBfill fill;
};

// Validates and writes the fields every mode shares, as cuTensorMapEncodeTiled documents their limits.
CUresult encodeCommon(CUtensorMap *map, const Common &common, uint32_t minimumRank, Mode mode, Element &chosen, Swizzle &layout) {
  if (!map || reinterpret_cast<uintptr_t>(map) % 64 != 0 || !common.address || !common.dimensions || !common.traversal)
    return CUDA_ERROR_INVALID_VALUE;
  const auto type = element(common.type);
  const auto arrangement = swizzle(common.swizzle);
  if (!type) return CUDA_ERROR_INVALID_VALUE;
  if (!arrangement || common.rank < minimumRank || common.rank > 5 || common.interleave > CU_TENSOR_MAP_INTERLEAVE_32B
      || common.promotion > CU_TENSOR_MAP_L2_PROMOTION_L2_256B || common.fill > CU_TENSOR_MAP_FLOAT_OOB_FILL_NAN_REQUEST_ZERO_FMA)
    return CUDA_ERROR_INVALID_VALUE;
  if (common.interleave != CU_TENSOR_MAP_INTERLEAVE_NONE && common.rank < 3) return CUDA_ERROR_INVALID_VALUE;
  if (common.interleave == CU_TENSOR_MAP_INTERLEAVE_32B && common.swizzle != CU_TENSOR_MAP_SWIZZLE_32B) return CUDA_ERROR_INVALID_VALUE;
  if (common.fill == CU_TENSOR_MAP_FLOAT_OOB_FILL_NAN_REQUEST_ZERO_FMA && !type->floating) return CUDA_ERROR_INVALID_VALUE;
  if (common.type == CU_TENSOR_MAP_DATA_TYPE_16U6_ALIGN16B && common.interleave != CU_TENSOR_MAP_INTERLEAVE_NONE)
    return CUDA_ERROR_INVALID_VALUE;
  const uint64_t alignment = common.interleave == CU_TENSOR_MAP_INTERLEAVE_32B || type->padded() ? 32 : 16;
  const auto address = reinterpret_cast<uint64_t>(common.address);
  if (address % alignment != 0) return CUDA_ERROR_INVALID_VALUE;
  if (!common.strides) return CUDA_ERROR_INVALID_VALUE;
  if (common.dimensions[0] % (type->padded() ? 128 : type->values) != 0) return CUDA_ERROR_INVALID_VALUE;
  for (uint32_t index = 0; index < common.rank; ++index) {
    if (common.dimensions[index] == 0 || common.dimensions[index] > (uint64_t{1} << 32)) return CUDA_ERROR_INVALID_VALUE;
    if (common.traversal[index] == 0 || common.traversal[index] > 8) return CUDA_ERROR_INVALID_VALUE;
  }
  for (uint32_t index = 0; index + 1 < common.rank; ++index)
    if (common.strides[index] % alignment != 0 || common.strides[index] >= (uint64_t{1} << 40)) return CUDA_ERROR_INVALID_VALUE;
  auto *bytes = reinterpret_cast<uint8_t *>(map);
  std::memset(bytes, 0, Bytes);
  std::memcpy(bytes, &address, sizeof(address));
  put32(bytes, 8,
        ((common.rank - 1) << RankShift) | (type->code << ElementShift) | (static_cast<uint32_t>(common.interleave) << InterleaveShift)
            | ((arrangement->mode & 3) << SwizzleShift) | (common.fill != CU_TENSOR_MAP_FLOAT_OOB_FILL_NONE ? NaNFillBit : 0)
            | (type->tf32 ? TF32Bit : 0) | (arrangement->atomicity << AtomicityShift) | (arrangement->mode > 3 ? SwizzleHighBit : 0));
  uint32_t high = 0;
  for (uint32_t index = 0; index + 1 < common.rank; ++index) {
    const uint64_t units = common.strides[index] >> 4;
    put32(bytes, 0x0c + (index * 4), static_cast<uint32_t>(units));
    high |= static_cast<uint32_t>(units >> 32) << (index * 4);
  }
  put32(bytes, 0x1c, high);
  uint32_t traversal = 0;
  for (uint32_t index = 0; index < common.rank; ++index) {
    put32(bytes, 0x20 + (index * 4), static_cast<uint32_t>(common.dimensions[index] - 1));
    traversal |= (common.traversal[index] - 1) << (index * 3);
  }
  put32(bytes, 0x34, traversal);
  put32(bytes, 0x40, static_cast<uint32_t>(mode));
  put32(bytes, 0x44, static_cast<uint32_t>(common.promotion));
  chosen = *type;
  layout = *arrangement;
  return CUDA_SUCCESS;
}
} // namespace tensor_map

} // namespace

extern "C" {

CUresult cuInit(unsigned flags) { return flags != 0 ? CUDA_ERROR_INVALID_VALUE : executionReady(); }

CUresult cuDriverGetVersion(int *version) {
  if (!version) return CUDA_ERROR_INVALID_VALUE;
  *version = CUDA_VERSION;
  return CUDA_SUCCESS;
}

CUresult cuDeviceGetCount(int *count) {
  if (!count) return CUDA_ERROR_INVALID_VALUE;
  *count = 1;
  return CUDA_SUCCESS;
}

CUresult cuDeviceGet(CUdevice *device, int ordinal) {
  if (!device) return CUDA_ERROR_INVALID_VALUE;
  if (ordinal != 0) return CUDA_ERROR_INVALID_DEVICE;
  *device = 0;
  return CUDA_SUCCESS;
}

CUresult cuDeviceGetAttribute(int *value, CUdevice_attribute attribute, CUdevice device) {
  return driverStatus(cudaDeviceGetAttribute(value, static_cast<cudaDeviceAttr>(attribute), device));
}

CUresult cuDeviceGetName(char *name, int length, CUdevice device) {
  return device == 0 ? copyString(name, length, DeviceName) : CUDA_ERROR_INVALID_VALUE;
}

CUresult cuDeviceGetPCIBusId(char *busId, int length, CUdevice device) {
  return device == 0 ? copyString(busId, length, DevicePciBusId) : CUDA_ERROR_INVALID_VALUE;
}

CUresult cuDeviceGetByPCIBusId(CUdevice *device, const char *busId) {
  if (!device || !busId || std::strcmp(busId, DevicePciBusId) != 0) return CUDA_ERROR_INVALID_VALUE;
  *device = 0;
  return CUDA_SUCCESS;
}

CUresult cuDeviceTotalMem_v2(size_t *bytes, CUdevice device) {
  if (!bytes || device != 0) return CUDA_ERROR_INVALID_VALUE;
  *bytes = device_limits::TotalGlobalMemoryBytes;
  return CUDA_SUCCESS;
}

CUresult cuDeviceTotalMem(unsigned *bytes, CUdevice device) {
  if (!bytes || device != 0) return CUDA_ERROR_INVALID_VALUE;
  *bytes = UINT32_MAX;
  return CUDA_SUCCESS;
}

CUresult cuDeviceComputeCapability(int *major, int *minor, CUdevice device) {
  if (!major || !minor || device != 0) return CUDA_ERROR_INVALID_VALUE;
  const auto capability = computeCapability();
  if (!capability) return CUDA_ERROR_NOT_INITIALIZED;
  *major = capability->first;
  *minor = capability->second;
  return CUDA_SUCCESS;
}

CUresult cuDeviceGetProperties(CUdevprop *properties, CUdevice device) {
  if (!properties || device != 0) return CUDA_ERROR_INVALID_VALUE;
  properties->maxThreadsPerBlock = device_limits::MaxThreadsPerBlock;
  properties->maxThreadsDim[0] = device_limits::MaxBlockDimX;
  properties->maxThreadsDim[1] = device_limits::MaxBlockDimY;
  properties->maxThreadsDim[2] = device_limits::MaxBlockDimZ;
  properties->maxGridSize[0] = device_limits::MaxGridDimX;
  properties->maxGridSize[1] = device_limits::MaxGridDimY;
  properties->maxGridSize[2] = device_limits::MaxGridDimZ;
  properties->sharedMemPerBlock = device_limits::MaxSharedMemoryPerBlock;
  properties->totalConstantMemory = device_limits::TotalConstantMemory;
  properties->SIMDWidth = device_limits::WarpSize;
  properties->memPitch = device_limits::MaxPitch;
  properties->regsPerBlock = device_limits::MaxRegistersPerBlock;
  properties->clockRate = device_limits::ClockRateKhz;
  properties->textureAlign = device_limits::TextureAlignment;
  return CUDA_SUCCESS;
}

CUresult cuDeviceGetUuid_v2(CUuuid *uuid, CUdevice device) {
  if (!uuid || device != 0) return CUDA_ERROR_INVALID_VALUE;
  std::memcpy(uuid->bytes, DeviceUuid, sizeof(DeviceUuid));
  return CUDA_SUCCESS;
}

CUresult cuDeviceGetTexture1DLinearMaxWidth(size_t *width, CUarray_format, unsigned, CUdevice device) {
  if (!width || device != 0) return CUDA_ERROR_INVALID_VALUE;
  *width = device_limits::MaxTexture1DLinearWidth;
  return CUDA_SUCCESS;
}

CUresult cuDeviceGetP2PAttribute(int *value, CUdevice_P2PAttribute attribute, CUdevice source, CUdevice destination) {
  if (!value || source != 0 || destination != 0) return CUDA_ERROR_INVALID_VALUE;
  *value = attribute == CU_DEVICE_P2P_ATTRIBUTE_PERFORMANCE_RANK ? 1 : 0;
  return CUDA_SUCCESS;
}

CUresult cuDeviceCanAccessPeer(int *value, CUdevice source, CUdevice destination) {
  if (!value || source != 0 || destination != 0) return CUDA_ERROR_INVALID_VALUE;
  *value = 0;
  return CUDA_SUCCESS;
}

// Libraries probe these while building dispatch tables; no atomic operation is advertised, but the query succeeds
// so the consumer makes that decision rather than treating the driver as uninitialised.
CUresult cuDeviceGetHostAtomicCapabilities(unsigned *capabilities, const CUatomicOperation *, unsigned count, CUdevice device) {
  if (!capabilities || device != 0) return CUDA_ERROR_INVALID_VALUE;
  std::fill_n(capabilities, count, 0U);
  return CUDA_SUCCESS;
}

CUresult cuDeviceGetP2PAtomicCapabilities(unsigned *capabilities, const CUatomicOperation *, unsigned count, CUdevice source,
                                          CUdevice destination) {
  if (!capabilities || source != 0 || destination != 0) return CUDA_ERROR_INVALID_VALUE;
  std::fill_n(capabilities, count, 0U);
  return CUDA_SUCCESS;
}

CUresult cuDeviceGetDefaultMemPool(CUmemoryPool *pool, CUdevice device) {
  if (!pool || device != 0) return CUDA_ERROR_INVALID_VALUE;
  *pool = reinterpret_cast<CUmemoryPool>(DefaultMemoryPool);
  return CUDA_SUCCESS;
}

CUresult cuDeviceSetMemPool(CUdevice device, CUmemoryPool) { return device == 0 ? CUDA_SUCCESS : CUDA_ERROR_INVALID_VALUE; }

CUresult cuCtxCreate_v2(CUcontext *context, unsigned, CUdevice device) {
  if (!context || device != 0) return CUDA_ERROR_INVALID_VALUE;
  const uint64_t handle = nextContext.fetch_add(1);
  if (handle == 0) return CUDA_ERROR_UNKNOWN;
  *context = reinterpret_cast<CUcontext>(static_cast<uintptr_t>(handle));
  auto status = trackContext(*context);
  if (status == CUDA_SUCCESS) status = cuCtxPushCurrent_v2(*context);
  if (status != CUDA_SUCCESS) {
    untrackContext(*context);
    *context = nullptr;
  }
  return status;
}

// CUDA 13 headers omit this revision, but callers built against CUDA 11.4 to 12.4 still resolve it.
CUresult cuCtxCreate_v3(CUcontext *context, CUexecAffinityParam *, int, unsigned flags, CUdevice device) {
  return cuCtxCreate_v2(context, flags, device);
}

CUresult cuCtxCreate_v4(CUcontext *context, CUctxCreateParams *, unsigned flags, CUdevice device) {
  return cuCtxCreate_v2(context, flags, device);
}

CUresult cuCtxDestroy_v2(CUcontext context) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  if (!contextIsLive(context)) return CUDA_ERROR_INVALID_CONTEXT;
  untrackContext(context);
  return CUDA_SUCCESS;
}

CUresult cuCtxSynchronize() { return contextIsLive(activeContext()) ? waitCommands() : CUDA_ERROR_INVALID_CONTEXT; }

CUresult cuCtxSynchronize_v2(CUcontext) { return cuCtxSynchronize(); }

CUresult cuCtxGetCurrent(CUcontext *context) {
  if (!context) return CUDA_ERROR_INVALID_VALUE;
  *context = currentContext;
  return CUDA_SUCCESS;
}

CUresult cuCtxSetCurrent(CUcontext context) {
  if (context && !contextIsLive(context)) return CUDA_ERROR_INVALID_VALUE;
  currentContext = context;
  return CUDA_SUCCESS;
}

CUresult cuCtxPushCurrent_v2(CUcontext context) {
  if (!contextIsLive(context)) return CUDA_ERROR_INVALID_VALUE;
  if (contextStackSize == ContextCapacity) return CUDA_ERROR_UNKNOWN;
  contextStack[contextStackSize++] = currentContext;
  currentContext = context;
  return CUDA_SUCCESS;
}

CUresult cuCtxPopCurrent_v2(CUcontext *context) {
  if (!context) return CUDA_ERROR_INVALID_VALUE;
  *context = currentContext;
  currentContext = contextStackSize == 0 ? nullptr : contextStack[--contextStackSize];
  return CUDA_SUCCESS;
}

CUresult cuCtxGetDevice(CUdevice *device) {
  if (!device) return CUDA_ERROR_INVALID_VALUE;
  *device = 0;
  return CUDA_SUCCESS;
}

CUresult cuCtxGetDevice_v2(CUdevice *device, CUcontext) { return cuCtxGetDevice(device); }

CUresult cuCtxGetCacheConfig(CUfunc_cache *config) {
  if (!config) return CUDA_ERROR_INVALID_VALUE;
  *config = CU_FUNC_CACHE_PREFER_NONE;
  return CUDA_SUCCESS;
}

CUresult cuCtxSetCacheConfig(CUfunc_cache) { return CUDA_SUCCESS; }

CUresult cuCtxGetSharedMemConfig(CUsharedconfig *config) {
  if (!config) return CUDA_ERROR_INVALID_VALUE;
  *config = CU_SHARED_MEM_CONFIG_DEFAULT_BANK_SIZE;
  return CUDA_SUCCESS;
}

CUresult cuCtxSetSharedMemConfig(CUsharedconfig) { return CUDA_SUCCESS; }

CUresult cuCtxGetStreamPriorityRange(int *least, int *greatest) {
  if (!least || !greatest) return CUDA_ERROR_INVALID_VALUE;
  *least = 0;
  *greatest = 0;
  return CUDA_SUCCESS;
}

CUresult cuCtxResetPersistingL2Cache() { return CUDA_SUCCESS; }

CUresult cuCtxGetFlags(unsigned *flags) {
  if (!flags) return CUDA_ERROR_INVALID_VALUE;
  *flags = 0;
  return CUDA_SUCCESS;
}

CUresult cuCtxGetApiVersion(CUcontext, unsigned *version) {
  if (!version) return CUDA_ERROR_INVALID_VALUE;
  *version = CUDA_VERSION;
  return CUDA_SUCCESS;
}

CUresult cuCtxGetLimit(size_t *value, CUlimit) {
  if (!value) return CUDA_ERROR_INVALID_VALUE;
  *value = 0;
  return CUDA_SUCCESS;
}

CUresult cuCtxSetLimit(CUlimit, size_t) { return CUDA_SUCCESS; }

CUresult cuDevicePrimaryCtxRetain(CUcontext *context, CUdevice device) {
  if (!context || device != 0) return CUDA_ERROR_INVALID_VALUE;
  std::scoped_lock const lock{primaryContextMutex};
  if (primaryContext && !contextIsLive(primaryContext)) {
    primaryContext = nullptr;
    primaryContextReferences = 0;
  }
  if (!primaryContext) {
    auto status = cuCtxCreate_v2(&primaryContext, 0, device);
    CUcontext created = nullptr;
    if (status == CUDA_SUCCESS) status = cuCtxPopCurrent_v2(&created);
    if (status != CUDA_SUCCESS) return status;
  }
  if (primaryContextReferences == UINT32_MAX) return CUDA_ERROR_UNKNOWN;
  ++primaryContextReferences;
  *context = primaryContext;
  implicitContext.store(primaryContext, std::memory_order_release);
  return CUDA_SUCCESS;
}

CUresult cuDevicePrimaryCtxRelease_v2(CUdevice device) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  if (device != 0) return CUDA_ERROR_INVALID_VALUE;
  std::scoped_lock const lock{primaryContextMutex};
  if (!primaryContext || primaryContextReferences == 0) return CUDA_ERROR_INVALID_VALUE;
  if (--primaryContextReferences != 0) return CUDA_SUCCESS;
  CUcontext context = primaryContext;
  primaryContext = nullptr;
  implicitContext.store(nullptr, std::memory_order_release);
  return cuCtxDestroy_v2(context);
}

CUresult cuDevicePrimaryCtxGetState(CUdevice device, unsigned *flags, int *active) {
  if (device != 0 || !flags || !active) return CUDA_ERROR_INVALID_VALUE;
  *flags = 0;
  std::scoped_lock const lock{primaryContextMutex};
  *active = primaryContextReferences == 0 ? 0 : 1;
  return CUDA_SUCCESS;
}

CUresult cuDevicePrimaryCtxSetFlags_v2(CUdevice device, unsigned) { return device == 0 ? CUDA_SUCCESS : CUDA_ERROR_INVALID_VALUE; }

CUresult cuDevicePrimaryCtxReset_v2(CUdevice device) {
  if (device != 0) return CUDA_ERROR_INVALID_VALUE;
  stickyError.store(CUDA_SUCCESS);
  return CUDA_SUCCESS;
}

CUresult cuMemAlloc_v2(CUdeviceptr *pointer, size_t size) { return allocate(pointer, size, nullptr); }

CUresult cuMemFree_v2(CUdeviceptr pointer) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  if (pointer == 0) return CUDA_SUCCESS;
  if (const auto mirrored = freeMirror(pointer)) return *mirrored;
  const auto binding = pointerBinding(pointer);
  if (!binding || binding->offset != 0) return CUDA_ERROR_INVALID_VALUE;
  if (!releaseAllocation(binding->allocation)) return CUDA_ERROR_UNKNOWN;
  untrackDevice(pointer);
  return CUDA_SUCCESS;
}

CUresult cuMemAllocManaged(CUdeviceptr *pointer, size_t size, unsigned) {
  if (!pointer) return CUDA_ERROR_INVALID_VALUE;
  void *host = nullptr;
  const auto status = mirroredAllocate(&host, size, false);
  if (status == CUDA_SUCCESS) *pointer = reinterpret_cast<uintptr_t>(host);
  return status;
}

CUresult cuMemAllocHost_v2(void **pointer, size_t size) { return mirroredAllocate(pointer, size, true); }

CUresult cuMemAllocHost(void **pointer, unsigned size) { return cuMemAllocHost_v2(pointer, size); }

CUresult cuMemHostAlloc(void **pointer, size_t size, unsigned flags) {
  if (flags & ~(CU_MEMHOSTALLOC_PORTABLE | CU_MEMHOSTALLOC_DEVICEMAP | CU_MEMHOSTALLOC_WRITECOMBINED)) return CUDA_ERROR_INVALID_VALUE;
  return mirroredAllocate(pointer, size, true, flags);
}

CUresult cuMemFreeHost(void *pointer) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  return freeMirror(reinterpret_cast<uintptr_t>(pointer)).value_or(CUDA_ERROR_INVALID_VALUE);
}

CUresult cuMemHostGetDevicePointer_v2(CUdeviceptr *pointer, void *host, unsigned flags) {
  if (flags != 0 || !pointer || !host) return CUDA_ERROR_INVALID_VALUE;
  std::scoped_lock const lock{mirrorMutex};
  if (!mirrors.find(reinterpret_cast<uintptr_t>(host))) return CUDA_ERROR_INVALID_VALUE;
  *pointer = reinterpret_cast<uintptr_t>(host);
  return CUDA_SUCCESS;
}

CUresult cuMemHostGetFlags(unsigned *flags, void *host) {
  if (!flags || !host) return CUDA_ERROR_INVALID_VALUE;
  std::scoped_lock const lock{mirrorMutex};
  const auto *view = mirrors.find(reinterpret_cast<uintptr_t>(host));
  if (!view) return CUDA_ERROR_INVALID_VALUE;
  *flags = static_cast<unsigned>(view->flags);
  return CUDA_SUCCESS;
}

CUresult cuMemHostRegister_v2(void *pointer, size_t size, unsigned flags) {
  const gemstone::MemoryRange range{.base = reinterpret_cast<uintptr_t>(pointer), .size = size};
  if (!range.valid() || (flags & ~15U)) return CUDA_ERROR_INVALID_VALUE;
  std::scoped_lock const lock{mirrorMutex};
  if (mirrors.overlaps(range)) return CUDA_ERROR_INVALID_VALUE;
  if (const auto status = registerMirror(range.base, size, true); status != CUDA_SUCCESS) return status;
  mirrors.add(range, gemstone::MemoryOwnership::Borrowed, flags);
  return CUDA_SUCCESS;
}

CUresult cuMemHostUnregister(void *pointer) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  return freeMirror(reinterpret_cast<uintptr_t>(pointer), gemstone::MemoryOwnership::Borrowed).value_or(CUDA_ERROR_INVALID_VALUE);
}

CUresult cuMemGetInfo_v2(size_t *freeBytes, size_t *totalBytes) {
  if (!freeBytes || !totalBytes) return CUDA_ERROR_INVALID_VALUE;
  *totalBytes = device_limits::TotalGlobalMemoryBytes;
  *freeBytes = *totalBytes;
  return CUDA_SUCCESS;
}

CUresult cuMemGetAddressRange_v2(CUdeviceptr *base, size_t *size, CUdeviceptr pointer) {
  const auto allocation = pointerMetadata(pointer);
  if (!allocation) return CUDA_ERROR_INVALID_VALUE;
  if (base) *base = allocation->base;
  if (size) *size = allocation->size;
  return CUDA_SUCCESS;
}

CUresult cuMemAllocPitch_v2(CUdeviceptr *pointer, size_t *pitch, size_t width, size_t height, unsigned) {
  if (!pointer || !pitch || width == 0 || height == 0 || height > SIZE_MAX / width) return CUDA_ERROR_INVALID_VALUE;
  *pitch = width;
  return cuMemAlloc_v2(pointer, width * height);
}

CUresult cuMemAllocAsync(CUdeviceptr *pointer, size_t size, CUstream) { return cuMemAlloc_v2(pointer, size); }

CUresult cuMemAllocFromPoolAsync(CUdeviceptr *pointer, size_t size, CUmemoryPool, CUstream) { return cuMemAlloc_v2(pointer, size); }

CUresult cuMemFreeAsync(CUdeviceptr pointer, CUstream stream) {
  return enqueueCommand(stream, [=] { return cuMemFree_v2(pointer); });
}

CUresult cuMemPoolCreate(CUmemoryPool *pool, const CUmemPoolProps *) { return cuDeviceGetDefaultMemPool(pool, 0); }

CUresult cuMemPoolDestroy(CUmemoryPool) { return CUDA_SUCCESS; }

CUresult cuMemPoolGetAttribute(CUmemoryPool, CUmemPool_attribute attribute, void *value) {
  if (!value) return CUDA_ERROR_INVALID_VALUE;
  *static_cast<uint64_t *>(value) = attribute == CU_MEMPOOL_ATTR_RELEASE_THRESHOLD ? memPoolReleaseThreshold : 0;
  return CUDA_SUCCESS;
}

CUresult cuMemPoolSetAttribute(CUmemoryPool, CUmemPool_attribute attribute, void *value) {
  if (!value) return CUDA_ERROR_INVALID_VALUE;
  if (attribute == CU_MEMPOOL_ATTR_RELEASE_THRESHOLD) memPoolReleaseThreshold = *static_cast<const uint64_t *>(value);
  return CUDA_SUCCESS;
}

CUresult cuMemPoolTrimTo(CUmemoryPool, size_t) { return CUDA_SUCCESS; }

CUresult cuMemGetDefaultMemPool(CUmemoryPool *pool, CUmemLocation *, CUmemAllocationType) {
  if (!pool) return CUDA_ERROR_INVALID_VALUE;
  *pool = reinterpret_cast<CUmemoryPool>(DefaultMemoryPool);
  return CUDA_SUCCESS;
}

CUresult cuMemSetMemPool(CUmemLocation *, CUmemAllocationType, CUmemoryPool) { return CUDA_SUCCESS; }

CUresult cuPointerGetAttribute(void *data, CUpointer_attribute attribute, CUdeviceptr pointer) {
  if (!data || pointer == 0) return CUDA_ERROR_INVALID_VALUE;
  const auto found = pointerMetadata(pointer);
  if (!found) return CUDA_ERROR_INVALID_VALUE;
  const auto &metadata = *found;
  switch (attribute) {
    case CU_POINTER_ATTRIBUTE_CONTEXT: *static_cast<CUcontext *>(data) = metadata.context; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_MEMORY_TYPE:
      *static_cast<unsigned *>(data) = metadata.host ? CU_MEMORYTYPE_HOST : CU_MEMORYTYPE_DEVICE;
      return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_DEVICE_POINTER:
      if (!currentContext) return CUDA_ERROR_INVALID_CONTEXT;
      *static_cast<CUdeviceptr *>(data) = pointer;
      return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_HOST_POINTER:
      if (!metadata.host && !metadata.managed) return CUDA_ERROR_INVALID_VALUE;
      *static_cast<CUdeviceptr *>(data) = pointer;
      return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_SYNC_MEMOPS:
    case CU_POINTER_ATTRIBUTE_DEVICE_ORDINAL:
    case CU_POINTER_ATTRIBUTE_ALLOWED_HANDLE_TYPES:
    case CU_POINTER_ATTRIBUTE_IS_HW_DECOMPRESS_CAPABLE: *static_cast<unsigned *>(data) = 0; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_MAPPED: *static_cast<unsigned *>(data) = 1; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_IS_MANAGED: *static_cast<unsigned *>(data) = metadata.managed; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_ACCESS_FLAGS:
      *static_cast<unsigned *>(data) = CU_POINTER_ATTRIBUTE_ACCESS_FLAG_READWRITE;
      return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_RANGE_START_ADDR:
    case CU_POINTER_ATTRIBUTE_MAPPING_BASE_ADDR: *static_cast<uint64_t *>(data) = metadata.base; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_RANGE_SIZE:
    case CU_POINTER_ATTRIBUTE_MAPPING_SIZE: *static_cast<uint64_t *>(data) = metadata.size; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_BUFFER_ID:
    case CU_POINTER_ATTRIBUTE_MEMORY_BLOCK_ID: *static_cast<uint64_t *>(data) = metadata.id; return CUDA_SUCCESS;
    case CU_POINTER_ATTRIBUTE_MEMPOOL_HANDLE: *static_cast<CUmemoryPool *>(data) = nullptr; return CUDA_SUCCESS;
    default: return CUDA_ERROR_INVALID_VALUE;
  }
}

CUresult cuPointerGetAttributes(unsigned count, CUpointer_attribute *attributes, void **data, CUdeviceptr pointer) {
  if (count != 0 && (!attributes || !data)) return CUDA_ERROR_INVALID_VALUE;
  const auto found = pointerMetadata(pointer);
  for (unsigned index = 0; index < count; ++index) {
    if (!data[index]) return CUDA_ERROR_INVALID_VALUE;
    if (!found || (attributes[index] == CU_POINTER_ATTRIBUTE_HOST_POINTER && !found->host && !found->managed)) {
      switch (attributes[index]) {
        case CU_POINTER_ATTRIBUTE_CONTEXT:
        case CU_POINTER_ATTRIBUTE_HOST_POINTER:
        case CU_POINTER_ATTRIBUTE_MEMPOOL_HANDLE: *static_cast<void **>(data[index]) = nullptr; break;
        case CU_POINTER_ATTRIBUTE_DEVICE_POINTER:
        case CU_POINTER_ATTRIBUTE_BUFFER_ID:
        case CU_POINTER_ATTRIBUTE_RANGE_START_ADDR:
        case CU_POINTER_ATTRIBUTE_RANGE_SIZE:
        case CU_POINTER_ATTRIBUTE_MAPPING_SIZE:
        case CU_POINTER_ATTRIBUTE_MAPPING_BASE_ADDR:
        case CU_POINTER_ATTRIBUTE_MEMORY_BLOCK_ID: *static_cast<uint64_t *>(data[index]) = 0; break;
        case CU_POINTER_ATTRIBUTE_MEMORY_TYPE:
        case CU_POINTER_ATTRIBUTE_SYNC_MEMOPS:
        case CU_POINTER_ATTRIBUTE_IS_MANAGED:
        case CU_POINTER_ATTRIBUTE_DEVICE_ORDINAL:
        case CU_POINTER_ATTRIBUTE_MAPPED:
        case CU_POINTER_ATTRIBUTE_ALLOWED_HANDLE_TYPES:
        case CU_POINTER_ATTRIBUTE_ACCESS_FLAGS:
        case CU_POINTER_ATTRIBUTE_IS_HW_DECOMPRESS_CAPABLE: *static_cast<unsigned *>(data[index]) = 0; break;
        default: return CUDA_ERROR_INVALID_VALUE;
      }
      continue;
    }
    if (const auto status = cuPointerGetAttribute(data[index], attributes[index], pointer); status != CUDA_SUCCESS) return status;
  }
  return CUDA_SUCCESS;
}

CUresult cuFlushGPUDirectRDMAWrites(CUflushGPUDirectRDMAWritesTarget, CUflushGPUDirectRDMAWritesScope) { return CUDA_SUCCESS; }

CUresult cuMemcpyHtoD_v2(CUdeviceptr destination, const void *source, size_t size) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  if (!source && size != 0) return CUDA_ERROR_INVALID_VALUE;
  const auto binding = pointerBinding(destination);
  if (!binding || size > binding->remaining) return CUDA_ERROR_INVALID_VALUE;
  if (copyMirrored(reinterpret_cast<void *>(destination), source, destination, size)) return CUDA_SUCCESS;
  return upload(*binding, source, size) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
}

CUresult cuMemcpyDtoH_v2(void *destination, CUdeviceptr source, size_t size) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  if (!destination && size != 0) return CUDA_ERROR_INVALID_VALUE;
  if (copyMirrored(destination, reinterpret_cast<const void *>(source), source, size)) return CUDA_SUCCESS;
  const auto binding = pointerBinding(source);
  if (!binding || size > binding->remaining) return CUDA_ERROR_INVALID_VALUE;
  return download(*binding, destination, size) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
}

CUresult cuMemcpyDtoD_v2(CUdeviceptr destination, CUdeviceptr source, size_t size) {
  try {
    std::vector<uint8_t> buffer(size == 0 ? 1 : size);
    auto status = cuMemcpyDtoH_v2(buffer.data(), source, size);
    if (status == CUDA_SUCCESS) status = cuMemcpyHtoD_v2(destination, buffer.data(), size);
    return status;
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

CUresult cuMemsetD8_v2(CUdeviceptr destination, unsigned char value, size_t count) {
  if (count > std::numeric_limits<uint32_t>::max()) return CUDA_ERROR_INVALID_VALUE;
  try {
    std::vector<uint8_t> buffer(count, value);
    return cuMemcpyHtoD_v2(destination, buffer.data(), count);
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

CUresult cuMemcpy(CUdeviceptr destination, CUdeviceptr source, size_t size) {
  const bool toDevice = devicePointer(destination);
  const bool fromDevice = devicePointer(source);
  if (toDevice && !fromDevice) return cuMemcpyHtoD_v2(destination, reinterpret_cast<const void *>(source), size);
  if (!toDevice && fromDevice) return cuMemcpyDtoH_v2(reinterpret_cast<void *>(destination), source, size);
  return cuMemcpyDtoD_v2(destination, source, size);
}

CUresult cuMemcpyAsync(CUdeviceptr destination, CUdeviceptr source, size_t size, CUstream stream) {
  return enqueueCommand(stream, [=] { return cuMemcpy(destination, source, size); });
}

CUresult cuMemcpyHtoDAsync_v2(CUdeviceptr destination, const void *source, size_t size, CUstream stream) {
  return enqueueCommand(stream, [=] { return cuMemcpyHtoD_v2(destination, source, size); });
}

CUresult cuMemcpyDtoHAsync_v2(void *destination, CUdeviceptr source, size_t size, CUstream stream) {
  return enqueueCommand(stream, [=] { return cuMemcpyDtoH_v2(destination, source, size); });
}

CUresult cuMemcpyDtoDAsync_v2(CUdeviceptr destination, CUdeviceptr source, size_t size, CUstream stream) {
  return enqueueCommand(stream, [=] { return cuMemcpyDtoD_v2(destination, source, size); });
}

CUresult cuMemsetD8Async(CUdeviceptr destination, unsigned char value, size_t count, CUstream stream) {
  return enqueueCommand(stream, [=] { return cuMemsetD8_v2(destination, value, count); });
}

CUresult cuMemcpy2D_v2(const CUDA_MEMCPY2D *copy) { return copyNow(copy); }

CUresult cuMemcpy2DAsync_v2(const CUDA_MEMCPY2D *copy, CUstream stream) { return copyAsync(copy, stream); }

CUresult cuMemcpy3D_v2(const CUDA_MEMCPY3D *copy) { return copyNow(copy); }

CUresult cuMemcpy3DAsync_v2(const CUDA_MEMCPY3D *copy, CUstream stream) { return copyAsync(copy, stream); }

CUresult cuMemcpy2D(const CUDA_MEMCPY2D_v1 *copy) { return copyNow(copy); }

CUresult cuMemcpy2DAsync(const CUDA_MEMCPY2D_v1 *copy, CUstream stream) { return copyAsync(copy, stream); }

CUresult cuMemcpy3D(const CUDA_MEMCPY3D_v1 *copy) { return copyNow(copy); }

CUresult cuMemcpy3DAsync(const CUDA_MEMCPY3D_v1 *copy, CUstream stream) { return copyAsync(copy, stream); }

CUresult cuArrayCreate_v2(CUarray *handle, const CUDA_ARRAY_DESCRIPTOR *descriptor) { return createArray(handle, descriptor); }

CUresult cuArray3DCreate_v2(CUarray *handle, const CUDA_ARRAY3D_DESCRIPTOR *descriptor) { return createArray(handle, descriptor); }

CUresult cuArrayCreate(CUarray *handle, const CUDA_ARRAY_DESCRIPTOR_v1 *descriptor) { return createArray(handle, descriptor); }

CUresult cuArray3DCreate(CUarray *handle, const CUDA_ARRAY3D_DESCRIPTOR_v1 *descriptor) { return createArray(handle, descriptor); }

CUresult cuArrayGetDescriptor_v2(CUDA_ARRAY_DESCRIPTOR *descriptor, CUarray handle) { return describeArray(descriptor, handle); }

CUresult cuArray3DGetDescriptor_v2(CUDA_ARRAY3D_DESCRIPTOR *descriptor, CUarray handle) { return describeArray(descriptor, handle); }

CUresult cuArrayGetDescriptor(CUDA_ARRAY_DESCRIPTOR_v1 *descriptor, CUarray handle) { return describeArray(descriptor, handle); }

CUresult cuArray3DGetDescriptor(CUDA_ARRAY3D_DESCRIPTOR_v1 *descriptor, CUarray handle) { return describeArray(descriptor, handle); }

CUresult cuArrayDestroy(CUarray handle) {
  if (!handle) return CUDA_ERROR_INVALID_VALUE;
  auto *array = arrayOf(handle);
  const auto status = cuMemFree_v2(array->device);
  if (status == CUDA_SUCCESS) delete array;
  return status;
}

CUresult cuModuleLoadData(CUmodule *module, const void *image) { return loadModule(module, image, false); }

CUresult cuModuleLoadDataEx(CUmodule *module, const void *image, unsigned, CUjit_option *, void **) {
  return loadModuleImage(module, image, false);
}

CUresult cuModuleLoadFatBinary(CUmodule *module, const void *image) { return loadModuleImage(module, image, false); }

CUresult cuModuleGetFunction(CUfunction *function, CUmodule module, const char *name) {
  try {
    if (!function || !name) return CUDA_ERROR_INVALID_VALUE;
    Module *owner = findModule(module);
    if (!owner) return CUDA_ERROR_INVALID_CONTEXT;
    {
      const std::scoped_lock lock(stateMutex);
      const auto existing =
          functions | find_cref([owner, name](const auto &entry) { return entry->module == owner && entry->name == name; });
      if (existing) {
        *function = reinterpret_cast<CUfunction>(existing->get().get());
        return CUDA_SUCCESS;
      }
    }

    auto described = client->call(DescribeKernel{.program = ProgramId{static_cast<int64_t>(owner->program)}, .name = {name}});
    if (!described) {
      if (gemstone::traceEnabled("cuda"))
        std::fprintf(stderr, "Gemstone CUDA kernel lookup failed for %s: %s\n", name, described.error().detail.c_str());
      return failureStatus(described.error().kind, owner->encoding, CUDA_ERROR_NOT_FOUND);
    }

    auto entry = std::make_unique<Function>();
    entry->module = owner;
    entry->name = name;
    const auto &kernel = described.value();
    if (kernel.registerCount) entry->registerCount = kernel.registerCount->value;
    entry->parameters = kernel.parameters;
    auto *const handle = reinterpret_cast<CUfunction>(entry.get());
    {
      const std::scoped_lock lock(stateMutex);
      functions.push_back(std::move(entry));
    }
    *function = handle;
    return CUDA_SUCCESS;
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

CUresult cuModuleGetGlobal_v2(CUdeviceptr *pointer, size_t *bytes, CUmodule module, const char *name) {
  CUdeviceptr address = 0;
  try {
    if ((!pointer && !bytes) || !name) return CUDA_ERROR_INVALID_VALUE;
    Module *owner = findModule(module);
    if (!owner) return CUDA_ERROR_INVALID_CONTEXT;

    std::unique_lock lock(owner->mutex);
    const auto existing = owner->globals | find_cref([name](const auto &global) { return global->name == name; });
    if (existing) {
      if (pointer) *pointer = existing->get()->address;
      if (bytes) *bytes = existing->get()->size;
      return CUDA_SUCCESS;
    }

    auto described = client->call(DescribeGlobal{.program = ProgramId{static_cast<int64_t>(owner->program)}, .name = {name}});
    if (!described) return CUDA_ERROR_NOT_FOUND;

    const auto &description = described.value();
    const auto size = static_cast<uint64_t>(description.size.value);
    if (description.alias) {
      const auto &alias = *description.alias;
      if (alias.name.value == name || alias.offset.value < 0) return CUDA_ERROR_INVALID_VALUE;
      const std::string canonicalName = alias.name.value;
      const auto offset = static_cast<uint64_t>(alias.offset.value);
      lock.unlock();

      CUdeviceptr canonicalAddress = 0;
      size_t canonicalSize = 0;
      if (const auto status = cuModuleGetGlobal_v2(&canonicalAddress, &canonicalSize, module, canonicalName.c_str());
          status != CUDA_SUCCESS)
        return status;
      if (offset > canonicalSize || size > canonicalSize - offset || offset > std::numeric_limits<uint64_t>::max() - canonicalAddress)
        return CUDA_ERROR_INVALID_VALUE;

      if (pointer) *pointer = canonicalAddress + offset;
      if (bytes) *bytes = static_cast<size_t>(size);
      return CUDA_SUCCESS;
    }

    const auto &initial = description.initial;
    auto global = std::make_unique<Global>();
    global->name = name;

    auto status = allocate(&address, static_cast<size_t>(size), owner);
    if (status == CUDA_SUCCESS && !initial.empty()) status = cuMemcpyHtoD_v2(address, initial.data(), initial.size());

    const auto binding = status == CUDA_SUCCESS ? pointerBinding(address) : std::nullopt;
    const bool bound = binding
                       && client->call(BindProgramGlobal{.program = ProgramId{static_cast<int64_t>(owner->program)},
                                                         .name = {name},
                                                         .allocation = AllocationId{static_cast<int64_t>(binding->allocation)},
                                                         .offset = {0}});
    if (binding) invalidateBuilds(*owner);
    if (!bound) {
      if (address != 0) (void)cuMemFree_v2(address);
      return status == CUDA_SUCCESS ? CUDA_ERROR_UNKNOWN : status;
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

CUresult cuModuleGetGlobal(CUdeviceptr_v1 *pointer, unsigned *bytes, CUmodule module, const char *name) {
  if (!pointer) return CUDA_ERROR_INVALID_VALUE;
  CUdeviceptr address = 0;
  size_t size = 0;
  if (const auto status = cuModuleGetGlobal_v2(&address, &size, module, name); status != CUDA_SUCCESS) return status;
  if (address > UINT32_MAX || size > UINT32_MAX) return CUDA_ERROR_NOT_SUPPORTED;
  *pointer = static_cast<CUdeviceptr_v1>(address);
  if (bytes) *bytes = static_cast<unsigned>(size);
  return CUDA_SUCCESS;
}

CUresult cuModuleUnload(CUmodule module) {
  if (executionShuttingDown.load()) return CUDA_ERROR_DEINITIALIZED;
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return pending;
  try {
    Module *owner = findModule(module);
    if (!owner) return CUDA_ERROR_INVALID_VALUE;
    if (!client->call(UnloadProgram{ProgramId{static_cast<int64_t>(owner->program)}})) return CUDA_ERROR_UNKNOWN;

    std::unique_ptr<Module> removed;
    {
      const std::scoped_lock lock(stateMutex);
      std::erase_if(functions, [owner](const auto &entry) { return entry->module == owner; });
      if (const auto index = modules ^ index_where_maybe([owner](const auto &candidate) { return candidate.get() == owner; })) {
        removed = std::move(modules[*index]);
        modules.erase(modules.begin() + static_cast<ptrdiff_t>(*index));
      }
    }
    if (!removed) return CUDA_ERROR_INVALID_VALUE;

    untrackPointers([owner](const PointerMetadata &p) { return p.module == owner; });
    for (const auto &global : removed->globals)
      (void)releaseAllocation(global->allocation);
    return CUDA_SUCCESS;
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

CUresult cuModuleGetTexRef(CUtexref *reference, CUmodule module, const char *name) { return moduleReference(reference, module, name); }

CUresult cuModuleGetSurfRef(CUsurfref *reference, CUmodule module, const char *name) { return moduleReference(reference, module, name); }

CUresult cuModuleGetLoadingMode(CUmoduleLoadingMode *mode) {
  if (!mode) return CUDA_ERROR_INVALID_VALUE;
  *mode = CU_MODULE_EAGER_LOADING;
  return CUDA_SUCCESS;
}

CUresult cuLibraryLoadData(CUlibrary *library, const void *code, CUjit_option *, void **, unsigned, CUlibraryOption *, void **, unsigned) {
  if (!library) return CUDA_ERROR_INVALID_VALUE;
  CUmodule module = nullptr;
  const auto status = loadModuleImage(&module, code, true);
  if (status == CUDA_SUCCESS) *library = reinterpret_cast<CUlibrary>(module);
  return status;
}

CUresult cuLibraryGetKernel(CUkernel *kernel, CUlibrary library, const char *name) {
  if (!kernel) return CUDA_ERROR_INVALID_VALUE;
  CUfunction function = nullptr;
  const auto status = cuModuleGetFunction(&function, reinterpret_cast<CUmodule>(library), name);
  if (status == CUDA_SUCCESS) *kernel = reinterpret_cast<CUkernel>(function);
  return status;
}

CUresult cuLibraryGetModule(CUmodule *module, CUlibrary library) {
  if (!module || !library) return CUDA_ERROR_INVALID_VALUE;
  *module = reinterpret_cast<CUmodule>(library);
  return CUDA_SUCCESS;
}

CUresult cuLibraryUnload(CUlibrary library) { return cuModuleUnload(reinterpret_cast<CUmodule>(library)); }

CUresult cuLibraryGetGlobal(CUdeviceptr *pointer, size_t *bytes, CUlibrary library, const char *name) {
  return cuModuleGetGlobal_v2(pointer, bytes, reinterpret_cast<CUmodule>(library), name);
}

// cuBLAS probes every library for managed globals while building its dispatch catalogue; a miss is an ordinary
// NOT_FOUND, whereas NOT_SUPPORTED aborts the catalogue with CUDA_ERROR_SYSTEM_DRIVER_MISMATCH.
CUresult cuLibraryGetManaged(CUdeviceptr *pointer, size_t *bytes, CUlibrary, const char *) {
  if (!pointer || !bytes) return CUDA_ERROR_INVALID_VALUE;
  *pointer = 0;
  *bytes = 0;
  return CUDA_ERROR_NOT_FOUND;
}

CUresult cuKernelGetFunction(CUfunction *function, CUkernel kernel) {
  if (!function || !kernel) return CUDA_ERROR_INVALID_VALUE;
  *function = reinterpret_cast<CUfunction>(kernel);
  return CUDA_SUCCESS;
}

CUresult cuKernelGetAttribute(int *value, CUfunction_attribute attribute, CUkernel kernel, CUdevice) {
  return cuFuncGetAttribute(value, attribute, reinterpret_cast<CUfunction>(kernel));
}

CUresult cuKernelSetAttribute(CUfunction_attribute attribute, int value, CUkernel kernel, CUdevice device) {
  if (device != 0) return CUDA_ERROR_INVALID_DEVICE;
  return cuFuncSetAttribute(reinterpret_cast<CUfunction>(kernel), attribute, value);
}

CUresult cuLinkCreate_v2(unsigned, CUjit_option *, void **, CUlinkState *state) {
  if (!state) return CUDA_ERROR_INVALID_VALUE;
  auto *link = new (std::nothrow) LinkState;
  if (!link) return CUDA_ERROR_UNKNOWN;
  *state = reinterpret_cast<CUlinkState>(link);
  return CUDA_SUCCESS;
}

CUresult cuLinkAddData_v2(CUlinkState state, CUjitInputType type, void *data, size_t size, const char *, unsigned, CUjit_option *,
                          void **) {
  if (!state || !data) return CUDA_ERROR_INVALID_VALUE;
  auto *link = reinterpret_cast<LinkState *>(state);
  if (type == CU_JIT_INPUT_PTX) {
    if (sassCodeRequired()) return CUDA_ERROR_NOT_SUPPORTED;
    if (size == 0 || size > MaximumImageSize) return CUDA_ERROR_INVALID_VALUE;
    const auto *source = static_cast<const char *>(data);
    const auto *end = static_cast<const char *>(std::memchr(source, '\0', size));
    const size_t sourceSize = end ? static_cast<size_t>(end - source) : size;
    if (sourceSize == 0) return CUDA_ERROR_INVALID_VALUE;
    return linkAppend(link, source, sourceSize) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
  }
  if (const char *ptx = executablePtx(parseFatbin(data)))
    return linkAppend(link, ptx, std::strlen(ptx)) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
  const bool text = ptxText(data);
  if ((ptxCodeRequired() && !text) || (sassCodeRequired() && text)) return CUDA_ERROR_NOT_SUPPORTED;
  if (size == 0 || size > MaximumImageSize) return CUDA_ERROR_NOT_SUPPORTED;
  return linkAppend(link, static_cast<const char *>(data), size) ? CUDA_SUCCESS : CUDA_ERROR_UNKNOWN;
}

CUresult cuLinkComplete(CUlinkState state, void **image, size_t *size) {
  if (!state || !image || !size) return CUDA_ERROR_INVALID_VALUE;
  auto *link = reinterpret_cast<LinkState *>(state);
  if (link->size == 0) return CUDA_ERROR_INVALID_VALUE;
  *image = link->image.data();
  *size = link->size;
  return CUDA_SUCCESS;
}

CUresult cuLinkDestroy(CUlinkState state) {
  if (!state) return CUDA_ERROR_INVALID_VALUE;
  delete reinterpret_cast<LinkState *>(state);
  return CUDA_SUCCESS;
}

CUresult cuFuncGetAttribute(int *value, CUfunction_attribute attribute, CUfunction function) {
  if (!value || !function) return CUDA_ERROR_INVALID_VALUE;
  switch (attribute) {
    case CU_FUNC_ATTRIBUTE_NUM_REGS: return functionRegisterCount(function, value);
    case CU_FUNC_ATTRIBUTE_MAX_THREADS_PER_BLOCK: *value = device_limits::MaxThreadsPerBlock; return CUDA_SUCCESS;
    case CU_FUNC_ATTRIBUTE_PTX_VERSION:
    case CU_FUNC_ATTRIBUTE_BINARY_VERSION: {
      const auto version = registeredPtxVersion(function);
      if (!version) return CUDA_ERROR_NOT_FOUND;
      *value = *version;
      return CUDA_SUCCESS;
    }
    case CU_FUNC_ATTRIBUTE_MAX_DYNAMIC_SHARED_SIZE_BYTES: *value = device_limits::MaxSharedMemoryPerBlock; return CUDA_SUCCESS;
    case CU_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES:
    case CU_FUNC_ATTRIBUTE_CONST_SIZE_BYTES:
    case CU_FUNC_ATTRIBUTE_LOCAL_SIZE_BYTES:
    case CU_FUNC_ATTRIBUTE_CACHE_MODE_CA:
    case CU_FUNC_ATTRIBUTE_PREFERRED_SHARED_MEMORY_CARVEOUT:
    case CU_FUNC_ATTRIBUTE_CLUSTER_SIZE_MUST_BE_SET:
    case CU_FUNC_ATTRIBUTE_NON_PORTABLE_CLUSTER_SIZE_ALLOWED:
    case CU_FUNC_ATTRIBUTE_CLUSTER_SCHEDULING_POLICY_PREFERENCE:
    case CU_FUNC_ATTRIBUTE_DEVICE_NODE_UPDATE_SUPPORTED: *value = 0; return CUDA_SUCCESS;
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH:
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_HEIGHT:
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_DEPTH: return functionClusterAttribute(function, attribute, value, false);
    default: return CUDA_ERROR_INVALID_VALUE;
  }
}

// Cluster dimensions affect execution; cache and scheduling preferences are advisory.
CUresult cuFuncSetAttribute(CUfunction function, CUfunction_attribute attribute, int value) {
  if (!function) return CUDA_ERROR_INVALID_VALUE;
  switch (attribute) {
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH:
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_HEIGHT:
    case CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_DEPTH:
      if (value < 0) return CUDA_ERROR_INVALID_VALUE;
      if (value > 16) return CUDA_ERROR_INVALID_CLUSTER_SIZE;
      return functionClusterAttribute(function, attribute, &value, true);
    default: return CUDA_SUCCESS;
  }
}

CUresult cuFuncSetCacheConfig(CUfunction function, CUfunc_cache) { return function ? CUDA_SUCCESS : CUDA_ERROR_INVALID_VALUE; }

CUresult cuFuncSetSharedMemConfig(CUfunction function, CUsharedconfig) { return function ? CUDA_SUCCESS : CUDA_ERROR_INVALID_VALUE; }

// Library strategy selection queries occupancy; the independent-workgroup executor reports a conservative bound.
CUresult cuOccupancyMaxActiveBlocksPerMultiprocessor(int *blocks, CUfunction, int blockSize, size_t) {
  return occupancyMaxBlocks(blocks, blockSize);
}

CUresult cuOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(int *blocks, CUfunction, int blockSize, size_t, unsigned) {
  return occupancyMaxBlocks(blocks, blockSize);
}

CUresult cuOccupancyAvailableDynamicSMemPerBlock(size_t *bytes, CUfunction function, int blocks, int blockSize) {
  if (!bytes || !function || blocks <= 0 || blocks > device_limits::MaxBlocksPerMultiprocessor) return CUDA_ERROR_INVALID_VALUE;
  int maximumBlocks = 0;
  if (occupancyMaxBlocks(&maximumBlocks, blockSize) != CUDA_SUCCESS || blocks > maximumBlocks) return CUDA_ERROR_INVALID_VALUE;
  int staticBytes = 0;
  if (const auto status = cuFuncGetAttribute(&staticBytes, CU_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES, function); status != CUDA_SUCCESS)
    return status;
  const auto perBlock = std::min(device_limits::MaxSharedMemoryPerBlockOptin, device_limits::MaxSharedMemoryPerMultiprocessor / blocks);
  *bytes = static_cast<size_t>(std::max(0, perBlock - staticBytes));
  return CUDA_SUCCESS;
}

CUresult cuOccupancyMaxActiveClusters(int *clusters, CUfunction function, const CUlaunchConfig *config) {
  if (!clusters || !config || (config->numAttrs != 0 && !config->attrs) || config->blockDimX == 0 || config->blockDimY == 0
      || config->blockDimZ == 0 || config->blockDimX > device_limits::MaxBlockDimX || config->blockDimY > device_limits::MaxBlockDimY
      || config->blockDimZ > device_limits::MaxBlockDimZ)
    return CUDA_ERROR_INVALID_VALUE;
  const auto threads = static_cast<uint64_t>(config->blockDimX) * config->blockDimY * config->blockDimZ;
  if (threads > device_limits::MaxThreadsPerBlock) return CUDA_ERROR_INVALID_VALUE;
  std::array<uint32_t, 3> required{};
  constexpr std::array attributes{CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_WIDTH, CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_HEIGHT,
                                  CU_FUNC_ATTRIBUTE_REQUIRED_CLUSTER_DEPTH};
  for (size_t axis = 0; axis < required.size(); ++axis) {
    int dimension = 0;
    if (const auto status = cuFuncGetAttribute(&dimension, attributes[axis], function); status != CUDA_SUCCESS) return status;
    required[axis] = static_cast<uint32_t>(dimension);
  }
  const bool hasRequired = required ^ exists([](auto size) { return size != 0; });
  std::optional<std::array<uint32_t, 3>> dimensions;
  if (hasRequired) dimensions = required;
  bool hasAttribute = false;
  for (unsigned index = 0; index < config->numAttrs; ++index) {
    const auto &attribute = config->attrs[index];
    if (attribute.id != CU_LAUNCH_ATTRIBUTE_CLUSTER_DIMENSION) continue;
    if (hasAttribute) return CUDA_ERROR_INVALID_VALUE;
    hasAttribute = true;
    const auto requested = clusterDimension(attribute);
    if (hasRequired && requested != required) return CUDA_ERROR_INVALID_CLUSTER_SIZE;
    dimensions = requested;
  }
  if (!dimensions) return CUDA_ERROR_INVALID_VALUE;
  const std::array grid{config->gridDimX, config->gridDimY, config->gridDimZ};
  for (size_t axis = 0; axis < grid.size(); ++axis)
    if (grid[axis] == 0 || (*dimensions)[axis] == 0 || grid[axis] % (*dimensions)[axis] != 0) return CUDA_ERROR_INVALID_CLUSTER_SIZE;
  const uint64_t volume = static_cast<uint64_t>((*dimensions)[0]) * (*dimensions)[1] * (*dimensions)[2];
  if (volume > 16) return CUDA_ERROR_INVALID_CLUSTER_SIZE;
  int blocks = 0;
  (void)occupancyMaxBlocks(&blocks, static_cast<int>(threads));
  blocks = std::min(blocks, device_limits::MaxBlocksPerMultiprocessor);
  int staticBytes = 0;
  if (const auto status = cuFuncGetAttribute(&staticBytes, CU_FUNC_ATTRIBUTE_SHARED_SIZE_BYTES, function); status != CUDA_SUCCESS)
    return status;
  const auto shared = static_cast<uint64_t>(config->sharedMemBytes) + static_cast<uint32_t>(staticBytes);
  if (shared > device_limits::MaxSharedMemoryPerBlockOptin) blocks = 0;
  else if (shared != 0) blocks = std::min(blocks, static_cast<int>(device_limits::MaxSharedMemoryPerMultiprocessor / shared));
  *clusters = static_cast<int>(static_cast<uint64_t>(blocks) * device_limits::MultiprocessorCount / volume);
  return CUDA_SUCCESS;
}

namespace {
using ClusterShape = std::array<uint32_t, 3>;
// The non-portable cluster size limit that cudaFuncAttributeNonPortableClusterSizeAllowed unlocks.
constexpr uint64_t MaximumClusterSize = 16;

CUresult launchKernel(CUfunction function, unsigned gridX, unsigned gridY, unsigned gridZ, unsigned blockX, unsigned blockY,
                      unsigned blockZ, unsigned sharedMemoryBytes, CUstream stream, void **kernelParams, void **extra,
                      std::optional<ClusterShape> cluster) {
  try {
    if (const auto latched = stickyError.load(); latched != CUDA_SUCCESS) return latched;
    if (!function || (extra && kernelParams)) return CUDA_ERROR_INVALID_VALUE;
    if (gridX == 0 || gridY == 0 || gridZ == 0 || blockX == 0 || blockY == 0 || blockZ == 0
        || blockX > static_cast<unsigned>(device_limits::MaxBlockDimX) || blockY > static_cast<unsigned>(device_limits::MaxBlockDimY)
        || blockZ > static_cast<unsigned>(device_limits::MaxBlockDimZ) || gridY > static_cast<unsigned>(device_limits::MaxGridDimY)
        || gridZ > static_cast<unsigned>(device_limits::MaxGridDimZ)
        || static_cast<uint64_t>(blockX) * blockY * blockZ > static_cast<uint64_t>(device_limits::MaxThreadsPerBlock))
      return InvalidConfiguration;
    const uint64_t globalX = static_cast<uint64_t>(gridX) * blockX;
    const uint64_t globalY = static_cast<uint64_t>(gridY) * blockY;
    const uint64_t globalZ = static_cast<uint64_t>(gridZ) * blockZ;
    constexpr auto maximumDimension = static_cast<uint64_t>(std::numeric_limits<int32_t>::max());
    constexpr auto maximumGlobal = static_cast<uint64_t>(std::numeric_limits<int64_t>::max());
    if (gridX > maximumDimension || gridY > maximumDimension || gridZ > maximumDimension || blockX > maximumDimension
        || blockY > maximumDimension || blockZ > maximumDimension || globalX > maximumGlobal || globalY > maximumGlobal
        || globalZ > maximumGlobal || sharedMemoryBytes > maximumDimension)
      return CUDA_ERROR_INVALID_VALUE;

    const auto [entry, required] = findLaunchFunction(function);
    if (!entry) return CUDA_ERROR_INVALID_VALUE;
    std::vector<void *> packedParameters;
    if (extra) {
      uint8_t *buffer = nullptr;
      size_t size = 0;
      bool hasBuffer = false;
      bool hasSize = false;
      for (size_t index = 0; extra[index] != CU_LAUNCH_PARAM_END; index += 2) {
        if (extra[index] == LaunchParamBufferPointer && !hasBuffer) {
          buffer = static_cast<uint8_t *>(extra[index + 1]);
          hasBuffer = true;
        } else if (extra[index] == LaunchParamBufferSize && !hasSize && extra[index + 1]) {
          std::memcpy(&size, extra[index + 1], sizeof(size));
          hasSize = true;
        } else {
          return CUDA_ERROR_INVALID_VALUE;
        }
      }
      if (size != 0 && !buffer) return CUDA_ERROR_INVALID_VALUE;
      packedParameters.reserve(entry->parameters.size());
      for (const auto &parameter : entry->parameters) {
        const auto offset = static_cast<size_t>(parameter.offset.value);
        const auto width = static_cast<size_t>(parameter.width.value);
        if (!buffer || offset > size || width > size - offset) return CUDA_ERROR_INVALID_VALUE;
        packedParameters.push_back(buffer + offset);
      }
      kernelParams = packedParameters.data();
    }
    if (!entry->parameters.empty() && !kernelParams) return CUDA_ERROR_INVALID_VALUE;
    if (!entry->module->executable) {
      if (gemstone::traceEnabled("cuda"))
        std::fprintf(stderr, "Gemstone CUDA kernel %s has no executable image for the selected code mode\n", entry->name.c_str());
      return CUDA_ERROR_NOT_SUPPORTED;
    }

    std::vector<Argument> arguments;
    arguments.reserve(entry->parameters.size());
    if (systemPagingEnabled() && !client->call(EnableHostMemory{})) return CUDA_ERROR_UNKNOWN;
    for (size_t index = 0; index < entry->parameters.size(); ++index) {
      const auto &parameter = entry->parameters[index];
      const auto width = static_cast<uint32_t>(parameter.width.value);
      if (!kernelParams[index]) return CUDA_ERROR_INVALID_VALUE;
      CUdeviceptr pointer = 0;
      if (parameter.kind == ParameterKind::Allocation && width == sizeof(pointer))
        std::memcpy(&pointer, kernelParams[index], sizeof(pointer));
      const auto binding = parameter.kind == ParameterKind::Allocation ? pointerBinding(pointer) : std::nullopt;
      if (width == sizeof(pointer) && binding) {
        arguments.emplace_back(ArgumentMemory{.region = AllocationId{static_cast<int64_t>(binding->allocation)},
                                              .offset = {static_cast<int64_t>(binding->offset)}});
      } else {
        const auto *bytes = static_cast<const uint8_t *>(kernelParams[index]);
        arguments.emplace_back(ArgumentScalar{std::vector<uint8_t>(bytes, bytes + width)});
      }
    }

    auto launch = Launch{
        .program = ProgramId{static_cast<int64_t>(entry->module->program)},
        .kernelName = {entry->name},
        .groupSize =
            Dimensions{.x = {static_cast<int32_t>(blockX)}, .y = {static_cast<int32_t>(blockY)}, .z = {static_cast<int32_t>(blockZ)}},
        .groupCount =
            Dimensions{.x = {static_cast<int32_t>(gridX)}, .y = {static_cast<int32_t>(gridY)}, .z = {static_cast<int32_t>(gridZ)}},
        .globalSize = GlobalDimensions{.x = {static_cast<int64_t>(globalX)},
                                       .y = {static_cast<int64_t>(globalY)},
                                       .z = {static_cast<int64_t>(globalZ)}},
        .workDim = {3},
        .globalOffset = Coordinates{},
        .privateSegmentBytes = {0},
        .groupSegmentBytes = {static_cast<int32_t>(sharedMemoryBytes)},
        .arguments = std::move(arguments),
        .pointerRelocations = {},
        .discoverArgumentRelocations = true};
    if (!cluster && required ^ exists([](int size) { return size != 0; }))
      cluster = ClusterShape{static_cast<uint32_t>(std::max(1, required[0])), static_cast<uint32_t>(std::max(1, required[1])),
                             static_cast<uint32_t>(std::max(1, required[2]))};
    if (cluster) {
      const auto [x, y, z] = *cluster;
      if (gridX % x != 0 || gridY % y != 0 || gridZ % z != 0) return CUDA_ERROR_INVALID_CLUSTER_SIZE;
      launch.clusterSize = Dimensions{.x = {static_cast<int32_t>(x)}, .y = {static_cast<int32_t>(y)}, .z = {static_cast<int32_t>(z)}};
    }
    if (const auto status = buildFunction(*entry); status != CUDA_SUCCESS) return status;
    return enqueueCommand(stream, [launch = std::move(launch), encoding = entry->module->encoding]() -> int32_t {
      std::string error;
      CompletionResult result;
      if (launchAndCollect(launch, result, error)) return CUDA_SUCCESS;
      if (gemstone::traceEnabled("cuda")) std::fprintf(stderr, "Gemstone CUDA launch failed: %s\n", error.c_str());
      return latchStatus(failureStatus(result.failure, encoding, CUDA_ERROR_LAUNCH_FAILED));
    });
  } catch (...) {
    return CUDA_ERROR_UNKNOWN;
  }
}

} // namespace

CUresult cuLaunchKernel(CUfunction function, unsigned gridX, unsigned gridY, unsigned gridZ, unsigned blockX, unsigned blockY,
                        unsigned blockZ, unsigned sharedMemoryBytes, CUstream stream, void **kernelParams, void **extra) {
  return driverLaunchStatus(
      launchKernel(function, gridX, gridY, gridZ, blockX, blockY, blockZ, sharedMemoryBytes, stream, kernelParams, extra, std::nullopt));
}

CUresult cuLaunchKernelEx(const CUlaunchConfig *config, CUfunction function, void **kernelParams, void **extra) {
  if (!config || (config->numAttrs != 0 && !config->attrs)) return CUDA_ERROR_INVALID_VALUE;
  std::optional<ClusterShape> cluster;
  for (unsigned index = 0; index < config->numAttrs; ++index) {
    const auto &attribute = config->attrs[index];
    switch (attribute.id) {
      // A preferred shape is a scheduling hint over the required one, which the VM always honours.
      case CU_LAUNCH_ATTRIBUTE_PREFERRED_CLUSTER_DIMENSION: break;
      case CU_LAUNCH_ATTRIBUTE_CLUSTER_DIMENSION: {
        const auto [x, y, z] = clusterDimension(attribute);
        if (x == 0 || y == 0 || z == 0 || config->gridDimX % x != 0 || config->gridDimY % y != 0 || config->gridDimZ % z != 0)
          return CUDA_ERROR_INVALID_VALUE;
        if (static_cast<uint64_t>(x) * y * z > MaximumClusterSize) return CUDA_ERROR_INVALID_CLUSTER_SIZE;
        cluster = ClusterShape{x, y, z};
        break;
      }
      case CU_LAUNCH_ATTRIBUTE_COOPERATIVE:
        if (attribute.value.cooperative != 0) return CUDA_ERROR_NOT_SUPPORTED;
        break;
      case CU_LAUNCH_ATTRIBUTE_IGNORE:
      case CU_LAUNCH_ATTRIBUTE_ACCESS_POLICY_WINDOW:
      case CU_LAUNCH_ATTRIBUTE_CLUSTER_SCHEDULING_POLICY_PREFERENCE:
      case CU_LAUNCH_ATTRIBUTE_PROGRAMMATIC_STREAM_SERIALIZATION:
      case CU_LAUNCH_ATTRIBUTE_PRIORITY:
      case CU_LAUNCH_ATTRIBUTE_MEM_SYNC_DOMAIN_MAP:
      case CU_LAUNCH_ATTRIBUTE_MEM_SYNC_DOMAIN:
      case CU_LAUNCH_ATTRIBUTE_PREFERRED_SHARED_MEMORY_CARVEOUT:
        // Scheduling and cache hints preserve the interpreter's serial stream ordering.
        break;
      default: return CUDA_ERROR_NOT_SUPPORTED;
    }
  }
  return driverLaunchStatus(launchKernel(function, config->gridDimX, config->gridDimY, config->gridDimZ, config->blockDimX,
                                         config->blockDimY, config->blockDimZ, config->sharedMemBytes, config->hStream, kernelParams, extra,
                                         cluster));
}

CUresult cuTensorMapEncodeTiled(CUtensorMap *tensorMap, CUtensorMapDataType tensorDataType, cuuint32_t tensorRank, void *globalAddress,
                                const cuuint64_t *globalDim, const cuuint64_t *globalStrides, const cuuint32_t *boxDim,
                                const cuuint32_t *elementStrides, CUtensorMapInterleave interleave, CUtensorMapSwizzle swizzle,
                                CUtensorMapL2promotion l2Promotion, CUtensorMapFloatOOBfill oobFill) {
  if (!boxDim) return CUDA_ERROR_INVALID_VALUE;
  tensor_map::Element element{};
  tensor_map::Swizzle layout{};
  const tensor_map::Common common{.type = tensorDataType,
                                  .rank = tensorRank,
                                  .address = globalAddress,
                                  .dimensions = globalDim,
                                  .strides = globalStrides,
                                  .traversal = elementStrides,
                                  .interleave = interleave,
                                  .swizzle = swizzle,
                                  .promotion = l2Promotion,
                                  .fill = oobFill};
  if (const auto status = tensor_map::encodeCommon(tensorMap, common, 1, tensor_map::Mode::Tile, element, layout); status != CUDA_SUCCESS)
    return status;
  for (cuuint32_t index = 0; index < tensorRank; ++index)
    if (boxDim[index] == 0 || boxDim[index] > 256) return CUDA_ERROR_INVALID_VALUE;
  if (element.padded() && boxDim[0] != 128) return CUDA_ERROR_INVALID_VALUE;
  const uint64_t inner = element.shared(boxDim[0]);
  if (interleave == CU_TENSOR_MAP_INTERLEAVE_NONE && (inner % 16 != 0 || (layout.span != 0 && inner > layout.span)))
    return CUDA_ERROR_INVALID_VALUE;
  auto *bytes = reinterpret_cast<uint8_t *>(tensorMap);
  uint32_t lower = 0;
  for (cuuint32_t index = 1; index < tensorRank; ++index)
    lower |= (boxDim[index] - 1) << ((index - 1) * 8);
  tensor_map::put32(bytes, 0x34, tensor_map::get32(bytes, 0x34) | ((boxDim[0] - 1) << 24));
  tensor_map::put32(bytes, 0x38, lower);
  return CUDA_SUCCESS;
}

namespace {
// Corners are signed and narrow as the rank grows: 16, 8 and 5 bits for 3D, 4D and 5D maps.
int cornerBits(uint32_t rank) {
  if (rank == 3) return 16;
  if (rank == 4) return 8;
  return 5;
}

CUresult encodeColumns(CUtensorMap *tensorMap, const tensor_map::Common &common, tensor_map::Mode mode, const int *lower, const int *upper,
                       cuuint32_t channelsPerPixel, cuuint32_t pixelsPerColumn) {
  tensor_map::Element element{};
  tensor_map::Swizzle layout{};
  if (const auto status = tensor_map::encodeCommon(tensorMap, common, 3, mode, element, layout); status != CUDA_SUCCESS) return status;
  const int bits = cornerBits(common.rank);
  const int spatial = mode == tensor_map::Mode::Im2col ? static_cast<int>(common.rank) - 2 : 1;
  for (int index = 0; index < spatial; ++index)
    if (lower[index] < -(1 << (bits - 1)) || lower[index] >= (1 << (bits - 1)) || upper[index] < -(1 << (bits - 1))
        || upper[index] >= (1 << (bits - 1)))
      return CUDA_ERROR_INVALID_VALUE;
  const uint64_t inner = element.shared(channelsPerPixel);
  if (channelsPerPixel == 0 || channelsPerPixel > 256 || pixelsPerColumn == 0 || pixelsPerColumn > 1024 || inner % 16 != 0
      || (layout.span != 0 && inner > layout.span) || (element.padded() && channelsPerPixel != 128)
      || channelsPerPixel % element.values != 0)
    return CUDA_ERROR_INVALID_VALUE;
  auto *bytes = reinterpret_cast<uint8_t *>(tensorMap);
  tensor_map::put32(bytes, 0x48, channelsPerPixel);
  tensor_map::put32(bytes, 0x4c, pixelsPerColumn);
  for (int index = 0; index < spatial; ++index) {
    tensor_map::put32(bytes, 0x50 + (index * 4), static_cast<uint32_t>(lower[index]));
    tensor_map::put32(bytes, 0x5c + (index * 4), static_cast<uint32_t>(upper[index]));
  }
  return CUDA_SUCCESS;
}
} // namespace

CUresult cuTensorMapEncodeIm2col(CUtensorMap *tensorMap, CUtensorMapDataType tensorDataType, cuuint32_t tensorRank, void *globalAddress,
                                 const cuuint64_t *globalDim, const cuuint64_t *globalStrides, const int *pixelBoxLowerCorner,
                                 const int *pixelBoxUpperCorner, cuuint32_t channelsPerPixel, cuuint32_t pixelsPerColumn,
                                 const cuuint32_t *elementStrides, CUtensorMapInterleave interleave, CUtensorMapSwizzle swizzle,
                                 CUtensorMapL2promotion l2Promotion, CUtensorMapFloatOOBfill oobFill) {
  if (!pixelBoxLowerCorner || !pixelBoxUpperCorner) return CUDA_ERROR_INVALID_VALUE;
  const tensor_map::Common common{.type = tensorDataType,
                                  .rank = tensorRank,
                                  .address = globalAddress,
                                  .dimensions = globalDim,
                                  .strides = globalStrides,
                                  .traversal = elementStrides,
                                  .interleave = interleave,
                                  .swizzle = swizzle,
                                  .promotion = l2Promotion,
                                  .fill = oobFill};
  return encodeColumns(tensorMap, common, tensor_map::Mode::Im2col, pixelBoxLowerCorner, pixelBoxUpperCorner, channelsPerPixel,
                       pixelsPerColumn);
}

CUresult cuTensorMapEncodeIm2colWide(CUtensorMap *tensorMap, CUtensorMapDataType tensorDataType, cuuint32_t tensorRank, void *globalAddress,
                                     const cuuint64_t *globalDim, const cuuint64_t *globalStrides, int pixelBoxLowerCornerWidth,
                                     int pixelBoxUpperCornerWidth, cuuint32_t channelsPerPixel, cuuint32_t pixelsPerColumn,
                                     const cuuint32_t *elementStrides, CUtensorMapInterleave interleave, CUtensorMapIm2ColWideMode mode,
                                     CUtensorMapSwizzle swizzle, CUtensorMapL2promotion l2Promotion, CUtensorMapFloatOOBfill oobFill) {
  if (mode > CU_TENSOR_MAP_IM2COL_WIDE_MODE_W128) return CUDA_ERROR_INVALID_VALUE;
  const tensor_map::Common common{.type = tensorDataType,
                                  .rank = tensorRank,
                                  .address = globalAddress,
                                  .dimensions = globalDim,
                                  .strides = globalStrides,
                                  .traversal = elementStrides,
                                  .interleave = interleave,
                                  .swizzle = swizzle,
                                  .promotion = l2Promotion,
                                  .fill = oobFill};
  return encodeColumns(tensorMap, common,
                       mode == CU_TENSOR_MAP_IM2COL_WIDE_MODE_W128 ? tensor_map::Mode::Im2colWide128 : tensor_map::Mode::Im2colWide,
                       &pixelBoxLowerCornerWidth, &pixelBoxUpperCornerWidth, channelsPerPixel, pixelsPerColumn);
}

CUresult cuTensorMapReplaceAddress(CUtensorMap *tensorMap, void *globalAddress) {
  if (!tensorMap || reinterpret_cast<uintptr_t>(tensorMap) % 64 != 0 || !globalAddress) return CUDA_ERROR_INVALID_VALUE;
  const auto address = reinterpret_cast<uint64_t>(globalAddress);
  const auto interleave = (tensor_map::get32(reinterpret_cast<const uint8_t *>(tensorMap), 8) >> tensor_map::InterleaveShift) & 3;
  if (address % (interleave == CU_TENSOR_MAP_INTERLEAVE_32B ? 32 : 16) != 0) return CUDA_ERROR_INVALID_VALUE;
  std::memcpy(tensorMap, &address, sizeof(address));
  return CUDA_SUCCESS;
}

CUresult cuLaunchHostFunc(CUstream stream, CUhostFn callback, void *userData) {
  if (!contextIsLive(activeContext()) || !callback) return CUDA_ERROR_INVALID_VALUE;
  return enqueueCommand(stream, [=] {
    callback(userData);
    return CUDA_SUCCESS;
  });
}

CUresult cuLaunchHostFunc_v2(CUstream stream, CUhostFn callback, void *userData, unsigned) {
  return cuLaunchHostFunc(stream, callback, userData);
}

CUresult cuStreamAddCallback(CUstream stream, CUstreamCallback callback, void *userData, unsigned flags) {
  if (flags != 0 || !contextIsLive(activeContext()) || !callback) return CUDA_ERROR_INVALID_VALUE;
  return enqueueCommand(stream, [=] {
    callback(stream, CUDA_SUCCESS, userData);
    return CUDA_SUCCESS;
  });
}

CUresult cuStreamCreate(CUstream *stream, unsigned flags) {
  if (!stream) return CUDA_ERROR_INVALID_VALUE;
  *stream = static_cast<CUstream>(commands().createStream((flags & CU_STREAM_NON_BLOCKING) != 0));
  return CUDA_SUCCESS;
}

CUresult cuStreamCreateWithPriority(CUstream *stream, unsigned flags, int) { return cuStreamCreate(stream, flags); }

// Handles this queue did not create are drained like any stream but leave the registry untouched.
CUresult cuStreamDestroy_v2(CUstream stream) {
  const auto status = queueStatus(commands().wait(commands().snapshot(stream)));
  (void)commands().destroyStream(stream);
  return status;
}

CUresult cuStreamSynchronize(CUstream stream) {
  if (!contextIsLive(activeContext())) return CUDA_ERROR_INVALID_CONTEXT;
  return queueStatus(commands().wait(commands().snapshot(stream)));
}

CUresult cuStreamQuery(CUstream stream) { return queueStatus(commands().query(commands().snapshot(stream), CUDA_ERROR_NOT_READY)); }

CUresult cuStreamWaitEvent(CUstream stream, CUevent event, unsigned flags) {
  if (flags != 0) return CUDA_ERROR_INVALID_VALUE;
  return commands().waitEvent(stream, event) ? CUDA_SUCCESS : CUDA_ERROR_INVALID_HANDLE;
}

// Green-context creation is unsupported, so every stream has an ordinary context.
CUresult cuStreamGetGreenCtx(CUstream, CUgreenCtx *context) {
  if (!context) return CUDA_ERROR_INVALID_VALUE;
  if (executionShuttingDown.load()) return CUDA_ERROR_DEINITIALIZED;
  if (!currentContext) return CUDA_ERROR_INVALID_CONTEXT;
  *context = nullptr;
  return CUDA_SUCCESS;
}

// Capture cannot begin in this backend, so every stream reports CU_STREAM_CAPTURE_STATUS_NONE. The other
// capture outputs are unspecified for a non-capturing stream and stay untouched, so callers of any revision
// share this implementation.
CUresult cuStreamIsCapturing(CUstream, CUstreamCaptureStatus *status) {
  if (!status) return CUDA_ERROR_INVALID_VALUE;
  if (executionShuttingDown.load()) return CUDA_ERROR_DEINITIALIZED;
  if (!contextIsLive(activeContext())) return CUDA_ERROR_INVALID_CONTEXT;
  *status = CU_STREAM_CAPTURE_STATUS_NONE;
  return CUDA_SUCCESS;
}

CUresult cuStreamGetCaptureInfo(CUstream stream, CUstreamCaptureStatus *status, cuuint64_t *) {
  return cuStreamIsCapturing(stream, status);
}

CUresult cuStreamGetCaptureInfo_v2(CUstream stream, CUstreamCaptureStatus *status, cuuint64_t *, CUgraph *, const CUgraphNode **,
                                   size_t *) {
  return cuStreamIsCapturing(stream, status);
}

CUresult cuStreamGetCaptureInfo_v3(CUstream stream, CUstreamCaptureStatus *status, cuuint64_t *, CUgraph *, const CUgraphNode **,
                                   const CUgraphEdgeData **, size_t *) {
  return cuStreamIsCapturing(stream, status);
}

CUresult cuEventCreate(CUevent *event, unsigned) {
  if (!event) return CUDA_ERROR_INVALID_VALUE;
  *event = static_cast<CUevent>(commands().createEvent());
  return CUDA_SUCCESS;
}

CUresult cuEventDestroy_v2(CUevent event) { return commands().destroyEvent(event) ? CUDA_SUCCESS : CUDA_ERROR_INVALID_HANDLE; }

CUresult cuEventElapsedTime_v2(float *milliseconds, CUevent, CUevent) {
  if (!milliseconds) return CUDA_ERROR_INVALID_VALUE;
  *milliseconds = 0.0F;
  return CUDA_SUCCESS;
}

CUresult cuEventQuery(CUevent event) {
  const auto ticket = commands().eventTicket(event);
  return ticket ? queueStatus(commands().query(*ticket, CUDA_ERROR_NOT_READY)) : CUDA_ERROR_INVALID_HANDLE;
}

CUresult cuEventRecord(CUevent event, CUstream stream) {
  return commands().recordEvent(event, stream) ? CUDA_SUCCESS : CUDA_ERROR_INVALID_HANDLE;
}

CUresult cuEventRecordWithFlags(CUevent event, CUstream stream, unsigned) { return cuEventRecord(event, stream); }

CUresult cuEventSynchronize(CUevent event) {
  const auto ticket = commands().eventTicket(event);
  return ticket ? queueStatus(commands().wait(*ticket)) : CUDA_ERROR_INVALID_HANDLE;
}

CUresult cuGetErrorString(CUresult status, const char **message) { return describeError(status, message, &ErrorText::message); }

CUresult cuGetErrorName(CUresult status, const char **name) { return describeError(status, name, &ErrorText::driverName); }

CUresult cuGetExportTable(const void **table, const CUuuid *id) {
  if (!table || !id) return CUDA_ERROR_INVALID_VALUE;
  for (const auto &entry : ExportTables)
    if (std::memcmp(id, entry.id, sizeof(CUuuid)) == 0) {
      *table = entry.table;
      return CUDA_SUCCESS;
    }
  return CUDA_ERROR_INVALID_VALUE;
}

CUresult cuGetProcAddress_v2(const char *symbol, void **function, int cudaVersion, cuuint64_t, CUdriverProcAddressQueryResult *status) {
  if (!symbol || !function) return CUDA_ERROR_INVALID_VALUE;
  const auto entryPoint = resolveEntryPoint(symbol, cudaVersion);
  *function = entryPoint.function;
  if (status) *status = entryPoint.status;
  if (entryPoint.status == CU_GET_PROC_ADDRESS_SYMBOL_NOT_FOUND && *symbol != '\0' && gemstone::traceEnabled("cuda"))
    std::fprintf(stderr, "Gemstone CUDA driver symbol unavailable: %s (version %d)\n", symbol, cudaVersion);
  return CUDA_SUCCESS;
}

CUresult cuGetProcAddress(const char *symbol, void **function, int cudaVersion, cuuint64_t flags) {
  if (!symbol || !function) return CUDA_ERROR_INVALID_VALUE;
  void *resolved = nullptr;
  CUdriverProcAddressQueryResult status{};
  if (const auto result = cuGetProcAddress_v2(symbol, &resolved, cudaVersion, flags, &status); result != CUDA_SUCCESS) return result;
  if (status != CU_GET_PROC_ADDRESS_SUCCESS) return CUDA_ERROR_NOT_FOUND;
  *function = resolved;
  return CUDA_SUCCESS;
}

cudaError_t cudaGetDeviceCount(int *count) { return runtimeStatus(cuDeviceGetCount(count)); }

cudaError_t cudaGetDevice(int *device) { return runtimeStatus(cuCtxGetDevice(device)); }

cudaError_t cudaSetDevice(int device) { return device == 0 ? cudaSuccess : cudaErrorInvalidValue; }

cudaError_t cudaDriverGetVersion(int *version) { return runtimeStatus(cuDriverGetVersion(version)); }

cudaError_t cudaRuntimeGetVersion(int *version) { return runtimeStatus(cuDriverGetVersion(version)); }

cudaError_t cudaDeviceSynchronize() { return runtimeStatus(cuCtxSynchronize()); }

cudaError_t cudaDeviceGetAttribute(int *value, cudaDeviceAttr attribute, int device) {
  if (!value || device != 0) return cudaErrorInvalidValue;
  using namespace device_limits;
  switch (attribute) {
    case cudaDevAttrMaxThreadsPerBlock: *value = MaxThreadsPerBlock; break;
    case cudaDevAttrMaxBlockDimX: *value = MaxBlockDimX; break;
    case cudaDevAttrMaxBlockDimY: *value = MaxBlockDimY; break;
    case cudaDevAttrMaxBlockDimZ: *value = MaxBlockDimZ; break;
    case cudaDevAttrMaxGridDimX: *value = MaxGridDimX; break;
    case cudaDevAttrMaxGridDimY: *value = MaxGridDimY; break;
    case cudaDevAttrMaxGridDimZ: *value = MaxGridDimZ; break;
    case cudaDevAttrMaxSharedMemoryPerBlock: *value = MaxSharedMemoryPerBlock; break;
    case cudaDevAttrTotalConstantMemory: *value = TotalConstantMemory; break;
    case cudaDevAttrWarpSize: *value = WarpSize; break;
    case cudaDevAttrMaxPitch: *value = MaxPitch; break;
    case cudaDevAttrMaxRegistersPerBlock: *value = MaxRegistersPerBlock; break;
    case cudaDevAttrClockRate: *value = ClockRateKhz; break;
    case cudaDevAttrTextureAlignment: *value = TextureAlignment; break;
    case cudaDevAttrGpuOverlap: *value = 1; break;
    case cudaDevAttrMultiProcessorCount: *value = MultiprocessorCount; break;
    case cudaDevAttrKernelExecTimeout: *value = 0; break;
    case cudaDevAttrIntegrated: *value = 0; break;
    case cudaDevAttrCanMapHostMemory: *value = 1; break;
    case cudaDevAttrComputeMode: *value = 0; break;
    case cudaDevAttrMaxTexture1DWidth: *value = MaxTexture1DWidth; break;
    case cudaDevAttrMaxTexture2DWidth: *value = MaxTexture2DWidth; break;
    case cudaDevAttrMaxTexture2DHeight: *value = MaxTexture2DHeight; break;
    case cudaDevAttrMaxTexture3DWidth: *value = MaxTexture3DWidth; break;
    case cudaDevAttrMaxTexture3DHeight: *value = MaxTexture3DHeight; break;
    case cudaDevAttrMaxTexture3DDepth: *value = MaxTexture3DDepth; break;
    case cudaDevAttrMaxTexture2DLayeredWidth: *value = MaxTexture2DLayeredWidth; break;
    case cudaDevAttrMaxTexture2DLayeredHeight: *value = MaxTexture2DLayeredHeight; break;
    case cudaDevAttrMaxTexture2DLayeredLayers: *value = MaxTexture2DLayeredLayers; break;
    case cudaDevAttrSurfaceAlignment: *value = SurfaceAlignment; break;
    case cudaDevAttrConcurrentKernels: *value = 1; break;
    case cudaDevAttrEccEnabled: *value = 0; break;
    case cudaDevAttrPciBusId: *value = PciBusId; break;
    case cudaDevAttrPciDeviceId: *value = PciDeviceId; break;
    case cudaDevAttrTccDriver: *value = 0; break;
    case cudaDevAttrMemoryClockRate: *value = MemoryClockRateKhz; break;
    case cudaDevAttrGlobalMemoryBusWidth: *value = GlobalMemoryBusWidth; break;
    case cudaDevAttrL2CacheSize: *value = L2CacheSize; break;
    case cudaDevAttrMaxThreadsPerMultiProcessor: *value = MaxThreadsPerMultiprocessor; break;
    case cudaDevAttrAsyncEngineCount: *value = AsyncEngineCount; break;
    case cudaDevAttrUnifiedAddressing: *value = 1; break;
    case cudaDevAttrMaxTexture1DLayeredWidth: *value = MaxTexture1DLayeredWidth; break;
    case cudaDevAttrMaxTexture1DLayeredLayers: *value = MaxTexture1DLayeredLayers; break;
    case cudaDevAttrMaxTexture2DGatherWidth: *value = MaxTexture2DGatherWidth; break;
    case cudaDevAttrMaxTexture2DGatherHeight: *value = MaxTexture2DGatherHeight; break;
    case cudaDevAttrMaxTexture3DWidthAlt: *value = MaxTexture3DWidth; break;
    case cudaDevAttrMaxTexture3DHeightAlt: *value = MaxTexture3DHeight; break;
    case cudaDevAttrMaxTexture3DDepthAlt: *value = MaxTexture3DDepth; break;
    case cudaDevAttrPciDomainId: *value = PciDomainId; break;
    case cudaDevAttrTexturePitchAlignment: *value = TexturePitchAlignment; break;
    case cudaDevAttrMaxTextureCubemapWidth: *value = MaxTextureCubemapWidth; break;
    case cudaDevAttrMaxTextureCubemapLayeredWidth: *value = MaxTextureCubemapLayeredWidth; break;
    case cudaDevAttrMaxTextureCubemapLayeredLayers: *value = MaxTextureCubemapLayeredLayers; break;
    case cudaDevAttrMaxSurface1DWidth: *value = MaxSurface1DWidth; break;
    case cudaDevAttrMaxSurface2DWidth: *value = MaxSurface2DWidth; break;
    case cudaDevAttrMaxSurface2DHeight: *value = MaxSurface2DHeight; break;
    case cudaDevAttrMaxSurface3DWidth: *value = MaxSurface3DWidth; break;
    case cudaDevAttrMaxSurface3DHeight: *value = MaxSurface3DHeight; break;
    case cudaDevAttrMaxSurface3DDepth: *value = MaxSurface3DDepth; break;
    case cudaDevAttrMaxSurface1DLayeredWidth: *value = MaxSurface1DLayeredWidth; break;
    case cudaDevAttrMaxSurface1DLayeredLayers: *value = MaxSurface1DLayeredLayers; break;
    case cudaDevAttrMaxSurface2DLayeredWidth: *value = MaxSurface2DLayeredWidth; break;
    case cudaDevAttrMaxSurface2DLayeredHeight: *value = MaxSurface2DLayeredHeight; break;
    case cudaDevAttrMaxSurface2DLayeredLayers: *value = MaxSurface2DLayeredLayers; break;
    case cudaDevAttrMaxSurfaceCubemapWidth: *value = MaxSurfaceCubemapWidth; break;
    case cudaDevAttrMaxSurfaceCubemapLayeredWidth: *value = MaxSurfaceCubemapLayeredWidth; break;
    case cudaDevAttrMaxSurfaceCubemapLayeredLayers: *value = MaxSurfaceCubemapLayeredLayers; break;
    case cudaDevAttrMaxTexture1DLinearWidth: *value = MaxTexture1DLinearWidth; break;
    case cudaDevAttrMaxTexture2DLinearWidth: *value = MaxTexture2DLinearWidth; break;
    case cudaDevAttrMaxTexture2DLinearHeight: *value = MaxTexture2DLinearHeight; break;
    case cudaDevAttrMaxTexture2DLinearPitch: *value = MaxTexture2DLinearPitch; break;
    case cudaDevAttrMaxTexture2DMipmappedWidth: *value = MaxTexture2DMipmappedWidth; break;
    case cudaDevAttrMaxTexture2DMipmappedHeight: *value = MaxTexture2DMipmappedHeight; break;
    case cudaDevAttrComputeCapabilityMajor: {
      const auto capability = computeCapability();
      if (!capability) return cudaErrorInitializationError;
      *value = capability->first;
      break;
    }
    case cudaDevAttrComputeCapabilityMinor: {
      const auto capability = computeCapability();
      if (!capability) return cudaErrorInitializationError;
      *value = capability->second;
      break;
    }
    case cudaDevAttrMaxTexture1DMipmappedWidth: *value = MaxTexture1DMipmappedWidth; break;
    case cudaDevAttrStreamPrioritiesSupported: *value = 1; break;
    case cudaDevAttrGlobalL1CacheSupported: *value = 1; break;
    case cudaDevAttrLocalL1CacheSupported: *value = 1; break;
    case cudaDevAttrMaxSharedMemoryPerMultiprocessor: *value = MaxSharedMemoryPerMultiprocessor; break;
    case cudaDevAttrMaxRegistersPerMultiprocessor: *value = MaxRegistersPerMultiprocessor; break;
    case cudaDevAttrManagedMemory: *value = 1; break;
    case cudaDevAttrIsMultiGpuBoard: *value = 0; break;
    case cudaDevAttrMultiGpuBoardGroupID: *value = 0; break;
    case cudaDevAttrHostNativeAtomicSupported: *value = 1; break;
    case cudaDevAttrSingleToDoublePrecisionPerfRatio: *value = SingleToDoublePrecisionPerfRatio; break;
    // Managed, pinned and registered allocations share the native atomic path.
    // Ordinary pageable memory is an experimental opt-in through GEMSTONE_SYSTEM_PAGING.
    case cudaDevAttrPageableMemoryAccess: *value = 0; break;
    case cudaDevAttrConcurrentManagedAccess: *value = 1; break;
    case cudaDevAttrComputePreemptionSupported: *value = 1; break;
    case cudaDevAttrCanUseHostPointerForRegisteredMem: *value = 1; break;
    case cudaDevAttrCooperativeLaunch: *value = 1; break;
    case cudaDevAttrReserved96: *value = 0; break;
    case cudaDevAttrMaxSharedMemoryPerBlockOptin: *value = MaxSharedMemoryPerBlockOptin; break;
    case cudaDevAttrPageableMemoryAccessUsesHostPageTables: *value = 0; break;
    case cudaDevAttrDirectManagedMemAccessFromHost: *value = 0; break;
    case cudaDevAttrMaxPersistingL2CacheSize: *value = MaxPersistingL2CacheSize; break;
    case cudaDevAttrMaxAccessPolicyWindowSize: *value = MaxAccessPolicyWindowSize; break;
    case cudaDevAttrMemoryPoolsSupported: *value = 1; break;
    case cudaDevAttrMaxBlocksPerMultiprocessor: *value = MaxBlocksPerMultiprocessor; break;
    default: *value = 0; break;
  }
  return cudaSuccess;
}

// The symbol is unversioned: older runtimes allocate shorter structures, so only the leading fields are written.
cudaError_t cudaGetDeviceProperties(cudaDeviceProp *properties, int device) {
  if (!properties || device != 0) return cudaErrorInvalidValue;
  std::memset(properties, 0, offsetof(cudaDeviceProp, multiProcessorCount) + sizeof(properties->multiProcessorCount));
  std::memcpy(properties->name, DeviceName, sizeof(DeviceName));
  properties->totalGlobalMem = device_limits::TotalGlobalMemoryBytes;
  properties->sharedMemPerBlock = device_limits::MaxSharedMemoryPerBlock;
  properties->regsPerBlock = device_limits::MaxRegistersPerBlock;
  properties->warpSize = device_limits::WarpSize;
  properties->memPitch = SIZE_MAX;
  properties->maxThreadsPerBlock = device_limits::MaxThreadsPerBlock;
  properties->maxThreadsDim[0] = device_limits::MaxBlockDimX;
  properties->maxThreadsDim[1] = device_limits::MaxBlockDimY;
  properties->maxThreadsDim[2] = device_limits::MaxBlockDimZ;
  properties->maxGridSize[0] = device_limits::MaxGridDimX;
  properties->maxGridSize[1] = device_limits::MaxGridDimY;
  properties->maxGridSize[2] = device_limits::MaxGridDimZ;
  properties->totalConstMem = device_limits::TotalConstantMemory;
  const auto capability = computeCapability();
  if (!capability) return cudaErrorInitializationError;
  properties->major = capability->first;
  properties->minor = capability->second;
  properties->textureAlignment = device_limits::TextureAlignment;
  properties->texturePitchAlignment = device_limits::TexturePitchAlignment;
  properties->multiProcessorCount = device_limits::MultiprocessorCount;
  return cudaSuccess;
}

// Unversioned like cudaGetDeviceProperties; the leading fields are common to every runtime generation.
cudaError_t cudaFuncGetAttributes(cudaFuncAttributes *attributes, const void *function) {
  if (!attributes || !function) return cudaErrorInvalidValue;
  std::memset(attributes, 0, offsetof(cudaFuncAttributes, preferredShmemCarveout) + sizeof(attributes->preferredShmemCarveout));
  attributes->maxThreadsPerBlock = device_limits::MaxThreadsPerBlock;
  if (const auto status = functionRegisterCount(function, &attributes->numRegs); status != CUDA_SUCCESS) return runtimeStatus(status);
  const auto version = registeredPtxVersion(function);
  if (!version) return cudaErrorSymbolNotFound;
  attributes->ptxVersion = *version;
  attributes->binaryVersion = *version;
  attributes->maxDynamicSharedSizeBytes = device_limits::MaxSharedMemoryPerBlock;
  return cudaSuccess;
}

cudaError_t cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(int *blocks, const void *, int blockSize, size_t, unsigned) {
  return runtimeStatus(occupancyMaxBlocks(blocks, blockSize));
}

cudaError_t cudaMalloc(void **pointer, size_t size) {
  if (!pointer) return cudaErrorInvalidValue;
  if (const auto status = ensureRuntimeContext(); status != CUDA_SUCCESS) return runtimeStatus(status);
  CUdeviceptr device = 0;
  const auto status = cuMemAlloc_v2(&device, size);
  if (status == CUDA_SUCCESS) *pointer = reinterpret_cast<void *>(device);
  return runtimeStatus(status);
}

cudaError_t cudaFree(void *pointer) { return runtimeStatus(cuMemFree_v2(reinterpret_cast<uintptr_t>(pointer))); }

cudaError_t cudaMallocHost(void **pointer, size_t size) {
  const auto status = ensureRuntimeContext();
  return runtimeStatus(status == CUDA_SUCCESS ? mirroredAllocate(pointer, size, true) : status);
}

cudaError_t cudaHostAlloc(void **pointer, size_t size, unsigned flags) {
  const auto status = ensureRuntimeContext();
  return runtimeStatus(status == CUDA_SUCCESS ? cuMemHostAlloc(pointer, size, flags) : status);
}

cudaError_t cudaFreeHost(void *pointer) { return runtimeStatus(cuMemFreeHost(pointer)); }

cudaError_t cudaMallocManaged(void **pointer, size_t size, unsigned) {
  const auto status = ensureRuntimeContext();
  return runtimeStatus(status == CUDA_SUCCESS ? mirroredAllocate(pointer, size, false) : status);
}

cudaError_t cudaHostRegister(void *pointer, size_t size, unsigned flags) {
  const auto status = ensureRuntimeContext();
  return runtimeStatus(status == CUDA_SUCCESS ? cuMemHostRegister_v2(pointer, size, flags) : status);
}

cudaError_t cudaHostUnregister(void *pointer) { return runtimeStatus(cuMemHostUnregister(pointer)); }

cudaError_t cudaHostGetDevicePointer(void **pointer, void *host, unsigned flags) {
  if (!pointer) return cudaErrorInvalidValue;
  CUdeviceptr device = 0;
  const auto status = cuMemHostGetDevicePointer_v2(&device, host, flags);
  if (status == CUDA_SUCCESS) *pointer = reinterpret_cast<void *>(device);
  return runtimeStatus(status);
}

cudaError_t cudaHostGetFlags(unsigned *flags, void *host) { return runtimeStatus(cuMemHostGetFlags(flags, host)); }

// The symbol is unversioned: CUDA 11.8 callers allocate the 24-byte common
// prefix, while 13.3 appends reserved fields. Only write the shared prefix.
cudaError_t cudaPointerGetAttributes(cudaPointerAttributes *attributes, const void *pointer) {
  if (!attributes) return cudaErrorInvalidValue;
  const auto metadata = pointer ? pointerMetadata(reinterpret_cast<uintptr_t>(pointer)) : std::nullopt;
  if (!metadata) {
    attributes->type = cudaMemoryTypeUnregistered;
    attributes->device = -1;
    attributes->devicePointer = nullptr;
    attributes->hostPointer = gemstone::apiAddress(pointer);
    return cudaSuccess;
  }
  if (metadata->managed) attributes->type = cudaMemoryTypeManaged;
  else if (metadata->host) attributes->type = cudaMemoryTypeHost;
  else attributes->type = cudaMemoryTypeDevice;
  attributes->device = 0;
  attributes->devicePointer = gemstone::apiAddress(pointer);
  attributes->hostPointer = metadata->host || metadata->managed ? gemstone::apiAddress(pointer) : nullptr;
  return cudaSuccess;
}

cudaError_t cudaMemcpy(void *destination, const void *source, size_t size, cudaMemcpyKind kind) {
  if (const auto pending = waitCommands(); pending != CUDA_SUCCESS) return runtimeStatus(pending);
  switch (kind) {
    case cudaMemcpyHostToDevice: return runtimeStatus(cuMemcpyHtoD_v2(reinterpret_cast<uintptr_t>(destination), source, size));
    case cudaMemcpyDeviceToHost: return runtimeStatus(cuMemcpyDtoH_v2(destination, reinterpret_cast<uintptr_t>(source), size));
    case cudaMemcpyDeviceToDevice:
      return runtimeStatus(cuMemcpyDtoD_v2(reinterpret_cast<uintptr_t>(destination), reinterpret_cast<uintptr_t>(source), size));
    default: return cudaErrorInvalidValue;
  }
}

cudaError_t cudaMemcpyAsync(void *destination, const void *source, size_t size, cudaMemcpyKind kind, cudaStream_t stream) {
  return runtimeStatus(enqueueCommand(stream, [=] { return cudaMemcpy(destination, source, size, kind); }));
}

const char *cudaGetErrorString(cudaError_t status) {
  const auto *text = errorText(status);
  return text ? text->message : "Gemstone CUDA error";
}

const char *cudaGetErrorName(cudaError_t status) {
  const auto *text = errorText(status);
  return text ? text->runtimeName : "cudaErrorUnknown";
}

cudaError_t cudaPeekAtLastError() {
  const auto latched = stickyError.load();
  return latched != CUDA_SUCCESS ? runtimeStatus(latched) : lastError;
}

cudaError_t cudaGetLastError() {
  const auto latched = stickyError.load();
  return latched != CUDA_SUCCESS ? runtimeStatus(latched) : std::exchange(lastError, cudaSuccess);
}

cudaError_t cudaStreamCreate(cudaStream_t *stream) { return runtimeStatus(cuStreamCreate(stream, 0)); }

cudaError_t cudaStreamCreateWithFlags(cudaStream_t *stream, unsigned flags) {
  return (flags & ~cudaStreamNonBlocking) == 0 ? runtimeStatus(cuStreamCreate(stream, flags)) : cudaErrorInvalidValue;
}

cudaError_t cudaStreamCreateWithPriority(cudaStream_t *stream, unsigned flags, int) { return cudaStreamCreateWithFlags(stream, flags); }

cudaError_t cudaStreamDestroy(cudaStream_t stream) { return runtimeStatus(cuStreamDestroy_v2(stream)); }

cudaError_t cudaStreamSynchronize(cudaStream_t stream) { return runtimeStatus(cuStreamSynchronize(stream)); }

cudaError_t cudaStreamQuery(cudaStream_t stream) { return runtimeStatus(cuStreamQuery(stream)); }

cudaError_t cudaStreamWaitEvent(cudaStream_t stream, cudaEvent_t event, unsigned flags) {
  return runtimeStatus(cuStreamWaitEvent(stream, event, flags));
}

cudaError_t cudaStreamIsCapturing(cudaStream_t stream, cudaStreamCaptureStatus *status) {
  const auto initialised = ensureRuntimeContext();
  return runtimeStatus(initialised == CUDA_SUCCESS ? cuStreamIsCapturing(stream, reinterpret_cast<CUstreamCaptureStatus *>(status))
                                                   : initialised);
}

cudaError_t cudaStreamGetCaptureInfo(cudaStream_t stream, cudaStreamCaptureStatus *status, unsigned long long *, cudaGraph_t *,
                                     const cudaGraphNode_t **, const cudaGraphEdgeData **, size_t *) {
  return cudaStreamIsCapturing(stream, status);
}

cudaError_t cudaEventCreate(cudaEvent_t *event) { return runtimeStatus(cuEventCreate(event, 0)); }

cudaError_t cudaEventCreateWithFlags(cudaEvent_t *event, unsigned flags) { return runtimeStatus(cuEventCreate(event, flags)); }

cudaError_t cudaEventDestroy(cudaEvent_t event) { return runtimeStatus(cuEventDestroy_v2(event)); }

cudaError_t cudaEventElapsedTime(float *milliseconds, cudaEvent_t start, cudaEvent_t end) {
  return runtimeStatus(cuEventElapsedTime_v2(milliseconds, start, end));
}

cudaError_t cudaEventQuery(cudaEvent_t event) { return runtimeStatus(cuEventQuery(event)); }

cudaError_t cudaEventRecord(cudaEvent_t event, cudaStream_t stream) { return runtimeStatus(cuEventRecord(event, stream)); }

cudaError_t cudaEventRecordWithFlags(cudaEvent_t event, cudaStream_t stream, unsigned) {
  return runtimeStatus(cuEventRecord(event, stream));
}

cudaError_t cudaEventSynchronize(cudaEvent_t event) { return runtimeStatus(cuEventSynchronize(event)); }

cudaChannelFormatDesc cudaCreateChannelDesc(int x, int y, int z, int w, cudaChannelFormatKind format) {
  return {.x = x, .y = y, .z = z, .w = w, .f = format};
}

void **__cudaRegisterFatBinary(void *image) {
  try {
    auto entry = std::make_unique<FatBinary>();
    entry->token = entry.get();
    const auto fatbin = parseFatbin(image);
    if (const char *ptx = executablePtx(fatbin)) {
      entry->ptx = ptx;
      entry->ptxVersion = std::string_view{ptx} ^ split(".target") ^ drop(1) ^ collect_first(parsePtxTarget);
    } else if (fatbin && !ptxCodeRequired()) {
      entry->cubin = fatbin->cubin;
    }
    void **token = &entry->token;
    std::scoped_lock const lock{registrationMutex};
    fatBinaries.push_back(std::move(entry));
    return token;
  } catch (const std::exception &) {
    return nullptr;
  }
}

void __cudaRegisterFatBinaryEnd(void **) {}

void __cudaRegisterFunction(void **handle, const char *hostFunction, char *, const char *deviceName, int, uint3 *, uint3 *, dim3 *, dim3 *,
                            int *) {
  if (!handle || !*handle || !hostFunction || !deviceName) return;
  try {
    auto entry = std::make_unique<RegisteredFunction>();
    entry->hostFunction = hostFunction;
    entry->deviceName = deviceName;
    entry->fatBinary = static_cast<FatBinary *>(*handle);
    std::scoped_lock const lock{registrationMutex};
    registeredFunctions.push_back(std::move(entry));
  } catch (const std::exception &) {
    std::fprintf(stderr, "Gemstone CUDA cannot register function %s\n", deviceName);
  }
}

void __cudaRegisterVar(void **handle, const char *hostVariable, char *, const char *deviceName, int, size_t size, int, int) {
  if (!handle || !*handle || !deviceName || size == 0) return;
  try {
    auto entry = std::make_unique<RegisteredVariable>();
    entry->initial = hostVariable;
    entry->deviceName = deviceName;
    entry->size = size;
    entry->fatBinary = static_cast<FatBinary *>(*handle);
    std::scoped_lock const lock{registrationMutex};
    registeredVariables.push_back(std::move(entry));
  } catch (const std::exception &) {
    std::fprintf(stderr, "Gemstone CUDA cannot register variable %s\n", deviceName);
  }
}

void __cudaUnregisterFatBinary(void **handle) {
  if (!handle || !*handle) return;
  auto *fat = static_cast<FatBinary *>(*handle);
  std::unique_ptr<FatBinary> removed;
  {
    std::scoped_lock const lock{registrationMutex};
    std::erase_if(registeredFunctions, [fat](const auto &entry) { return entry->fatBinary == fat; });
    std::erase_if(registeredVariables, [fat](const auto &entry) { return entry->fatBinary == fat; });
    const auto index = fatBinaries ^ index_where_maybe([fat](const auto &entry) { return entry.get() == fat; });
    if (!index) return;
    removed = std::move(fatBinaries[*index]);
    fatBinaries.erase(fatBinaries.begin() + static_cast<ptrdiff_t>(*index));
  }
  if (removed->module) (void)cuModuleUnload(removed->module);
  removed->token = nullptr;
}

cudaError_t __cudaGetKernel(cudaKernel_t *kernel, const void *hostFunction) {
  if (!kernel || !hostFunction) return cudaErrorInvalidValue;
  CUfunction function = nullptr;
  const auto status = registeredKernel(hostFunction, &function).value_or(CUDA_ERROR_INVALID_VALUE);
  if (status == CUDA_SUCCESS) *kernel = reinterpret_cast<cudaKernel_t>(function);
  return runtimeStatus(status);
}

unsigned __cudaPushCallConfiguration(dim3 grid, dim3 block, size_t sharedMemory, CUstream_st *stream) {
  launchConfiguration = {.grid = grid, .block = block, .sharedMemory = sharedMemory, .stream = stream, .valid = true};
  return 0;
}

cudaError_t __cudaPopCallConfiguration(dim3 *grid, dim3 *block, size_t *sharedMemory, void *stream) {
  if (!launchConfiguration.valid || !grid || !block || !sharedMemory || !stream) return cudaErrorInvalidValue;
  *grid = launchConfiguration.grid;
  *block = launchConfiguration.block;
  *sharedMemory = launchConfiguration.sharedMemory;
  *static_cast<cudaStream_t *>(stream) = launchConfiguration.stream;
  launchConfiguration.valid = false;
  return cudaSuccess;
}

cudaError_t __cudaLaunchKernel(cudaKernel_t kernel, dim3 grid, dim3 block, void **arguments, size_t sharedMemory, cudaStream_t stream) {
  lastError = runtimeStatus(launchKernel(reinterpret_cast<CUfunction>(kernel), grid.x, grid.y, grid.z, block.x, block.y, block.z,
                                         static_cast<unsigned>(sharedMemory), stream, arguments, nullptr, std::nullopt));
  return lastError;
}

} // extern "C"

// Revisions and per-thread-stream spellings sharing an ABI and implementation are aliases of one definition.
// NOLINTNEXTLINE(bugprone-macro-parentheses)
#define GEMSTONE_CUDA_ALIAS(symbol, target) extern "C" decltype(target) symbol __attribute__((alias(#target)));
GEMSTONE_CUDA_ALIAS(cuDeviceGetUuid, cuDeviceGetUuid_v2)
GEMSTONE_CUDA_ALIAS(cuDeviceGetMemPool, cuDeviceGetDefaultMemPool)
GEMSTONE_CUDA_ALIAS(cuCtxCreate, cuCtxCreate_v2)
GEMSTONE_CUDA_ALIAS(cuCtxDetach, cuCtxDestroy_v2)
GEMSTONE_CUDA_ALIAS(cuCtxPushCurrent, cuCtxPushCurrent_v2)
GEMSTONE_CUDA_ALIAS(cuCtxPopCurrent, cuCtxPopCurrent_v2)
GEMSTONE_CUDA_ALIAS(cuDevicePrimaryCtxRelease, cuDevicePrimaryCtxRelease_v2)
GEMSTONE_CUDA_ALIAS(cuDevicePrimaryCtxReset, cuDevicePrimaryCtxReset_v2)
GEMSTONE_CUDA_ALIAS(cuDevicePrimaryCtxSetFlags, cuDevicePrimaryCtxSetFlags_v2)
GEMSTONE_CUDA_ALIAS(cuMemHostRegister, cuMemHostRegister_v2)
GEMSTONE_CUDA_ALIAS(cuMemGetMemPool, cuMemGetDefaultMemPool)
GEMSTONE_CUDA_ALIAS(cuMemcpyHtoD_ptds, cuMemcpyHtoD_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyHtoD_v2_ptds, cuMemcpyHtoD_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyDtoH_ptds, cuMemcpyDtoH_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyDtoH_v2_ptds, cuMemcpyDtoH_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyDtoD_ptds, cuMemcpyDtoD_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyDtoD_v2_ptds, cuMemcpyDtoD_v2)
GEMSTONE_CUDA_ALIAS(cuMemsetD8_ptds, cuMemsetD8_v2)
GEMSTONE_CUDA_ALIAS(cuMemsetD8_v2_ptds, cuMemsetD8_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpy_ptds, cuMemcpy)
GEMSTONE_CUDA_ALIAS(cuMemcpyAsync_ptsz, cuMemcpyAsync)
GEMSTONE_CUDA_ALIAS(cuMemcpyHtoDAsync_ptsz, cuMemcpyHtoDAsync_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyHtoDAsync_v2_ptsz, cuMemcpyHtoDAsync_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyDtoHAsync_ptsz, cuMemcpyDtoHAsync_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyDtoHAsync_v2_ptsz, cuMemcpyDtoHAsync_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyDtoDAsync_ptsz, cuMemcpyDtoDAsync_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpyDtoDAsync_v2_ptsz, cuMemcpyDtoDAsync_v2)
GEMSTONE_CUDA_ALIAS(cuMemsetD8Async_ptsz, cuMemsetD8Async)
GEMSTONE_CUDA_ALIAS(cuMemcpy2DUnaligned_v2, cuMemcpy2D_v2)
GEMSTONE_CUDA_ALIAS(cuMemcpy2DUnaligned, cuMemcpy2D)
GEMSTONE_CUDA_ALIAS(cuLinkCreate, cuLinkCreate_v2)
GEMSTONE_CUDA_ALIAS(cuLinkAddData, cuLinkAddData_v2)
GEMSTONE_CUDA_ALIAS(cuLaunchKernel_ptsz, cuLaunchKernel)
GEMSTONE_CUDA_ALIAS(cuStreamDestroy, cuStreamDestroy_v2)
GEMSTONE_CUDA_ALIAS(cuEventElapsedTime, cuEventElapsedTime_v2)
