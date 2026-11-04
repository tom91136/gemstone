#include <algorithm>
#include <array>
#include <atomic>
#include <bit>
#include <charconv>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <fstream>
#include <functional>
#include <ios>
#include <iterator>
#include <limits>
#include <memory>
#include <mutex>
#include <new>
#include <optional>
#include <source_location>
#include <string>
#include <string_view>
#include <thread>
#include <type_traits>
#include <unordered_map>
#include <utility>
#include <vector>

#include <elf.h>
#include <hsa.h>
#include <hsa_ext_amd.h>

#include <aspartame/array.hpp>
#include <aspartame/string.hpp>
#include <aspartame/string_view.hpp>
#include <aspartame/unordered_map.hpp>
#include <aspartame/vector.hpp>
#include <aspartame/view.hpp>
#include <hip/hip_ext.h>
#include <hip/hip_runtime_api.h>
#include <hip/hip_version.h>

#include "gemstone/devices.hpp"
#include "gemstone/environment.hpp"
#include "gemstone/hsa/device_heap.hpp"
#include "gemstone/hsa/device_model.hpp"
#include "gemstone/hsa/executable_metadata.hpp"
#include "gemstone/hsa/global.hpp"
#include "gemstone/hsa/hostcall.hpp"
#include "gemstone/hsa/images.hpp"
#include "gemstone/hsa/memory_copy.hpp"
#include "gemstone/hsa/target.hpp"
#include "gemstone_execution_rpc.hpp"
#include "gemstone_hip_api.h"
#include <gemstone/bytes.hpp>
#include <gemstone/command_queue.hpp>
#include <gemstone/elf.hpp>
#include <gemstone/export.h>
#include <gemstone/graph.hpp>
#include <gemstone/memory.hpp>
#include <gemstone/trace.hpp>
#include <gemstone/zstd.hpp>

#include <environment_keys.hpp>

namespace {

using namespace aspartame;
using gemstone::readNative;

gemstone::CommandQueue &commands() {
  static gemstone::CommandQueue queue(hipErrorUnknown);
  return queue;
}

hipError_t waitCommands() { return static_cast<hipError_t>(commands().waitAll()); }

gemstone::graph::Captures<hipStream_t, hipEvent_t> captures;

hipError_t graphResult(gemstone::graph::Outcome outcome) {
  switch (outcome) {
    case gemstone::graph::Outcome::Ok: return hipSuccess;
    case gemstone::graph::Outcome::InvalidValue: return hipErrorInvalidValue;
    case gemstone::graph::Outcome::InvalidHandle: return hipErrorInvalidHandle;
    case gemstone::graph::Outcome::Unsupported: return hipErrorStreamCaptureUnsupported;
    case gemstone::graph::Outcome::Isolation: return hipErrorStreamCaptureIsolation;
    case gemstone::graph::Outcome::Unmatched: return hipErrorStreamCaptureUnmatched;
    case gemstone::graph::Outcome::TopologyChanged: return hipErrorGraphExecUpdateFailure;
  }
  return hipErrorUnknown;
}

hipError_t enqueueCommand(hipStream_t stream, std::function<int32_t()> work) {
  if (gemstone::graph::divert(work) || captures.record(stream, work)) return hipSuccess;
  try {
    commands().submit(stream, std::move(work));
    return hipSuccess;
  } catch (...) {
    return hipErrorOutOfMemory;
  }
}

constexpr int RuntimeVersion = (HIP_VERSION_MAJOR * 10000000) + (HIP_VERSION_MINOR * 100000) + HIP_VERSION_PATCH;
constexpr uint32_t MaxBlockThreads = 1024;
constexpr size_t MultigridRecordBytes = 48;
constexpr uint32_t MaxGridDimension = std::numeric_limits<int32_t>::max();
constexpr uint32_t MaxGridDimensionYZ = std::numeric_limits<uint16_t>::max() + 1U;
constexpr int MaxTexture2DGatherDimension = 32768;
constexpr size_t DeviceTotalMemoryBytes = gemstone::hsa::GlobalMemoryBytes;
constexpr size_t DeviceSharedMemoryPerBlock = gemstone::hsa::GroupMemoryBytes;

bool traceHip(std::string_view category) { return gemstone::traceEnabled("hip", category); }

constexpr uint16_t dispatchDimensions(dim3 grid, dim3 block) {
  if (grid.z > 1 || block.z > 1) return 3;
  if (grid.y > 1 || block.y > 1) return 2;
  return 1;
}

// hip_runtime_api.h spells these sentinels as C-style casts
constexpr uintptr_t LaunchParamBufferPointer = 0x01;
constexpr uintptr_t LaunchParamBufferSize = 0x02;
constexpr uintptr_t LaunchParamEnd = 0x03;
constexpr uintptr_t StreamLegacy = 1;
constexpr uintptr_t StreamPerThread = 2;

bool sentinelStream(hipStream_t stream) {
  const auto value = std::bit_cast<uintptr_t>(stream);
  return value == StreamLegacy || value == StreamPerThread;
}

struct LaunchBuffer {
  const void *data = nullptr;
  size_t size = 0;

  bool copyTo(void *destination, size_t capacity, size_t required) const {
    if (required > capacity || size < required || (size != 0 && !data)) return false;
    // HIP consumes the declared kernargs, not unused bytes at the end of a caller's packed buffer.
    // Tensile can append a four-byte trailer to an otherwise complete argument record.
    if (required != 0) std::memcpy(destination, data, required);
    return true;
  }
};

bool parseLaunchBuffer(void **extra, LaunchBuffer &buffer) {
  buffer = {};
  if (!extra) return true;
  bool foundPointer = false;
  bool foundSize = false;
  size_t index = 0;
  while (index < 16) {
    const auto tag = std::bit_cast<uintptr_t>(extra[index++]);
    if (tag == LaunchParamEnd) return foundPointer && foundSize;
    if (index >= 16 || extra[index] == nullptr) return false;
    if (tag == LaunchParamBufferPointer) {
      if (foundPointer) return false;
      buffer.data = extra[index++];
      foundPointer = true;
    } else if (tag == LaunchParamBufferSize) {
      if (foundSize) return false;
      buffer.size = *static_cast<const size_t *>(extra[index++]);
      foundSize = true;
    } else {
      return false;
    }
  }
  return false;
}

// Blocks of `threads` work-items with `shared` dynamic LDS bytes each that one multiprocessor holds at once: the wave slots
// of its SIMDs and its LDS bound them, as in ROCclr.
int residentBlocks(uint64_t threads, size_t shared) {
  if (threads == 0 || threads > MaxBlockThreads || shared > DeviceSharedMemoryPerBlock) return 0;
  const auto model = gemstone::hsa::deviceModel();
  const uint64_t wavesPerBlock = (threads + model.waveSize - 1) / model.waveSize;
  uint64_t blocks = static_cast<uint64_t>(model.computeUnitsPerProcessor * model.wavesPerComputeUnit()) / wavesPerBlock;
  if (shared != 0) blocks = std::min<uint64_t>(blocks, model.groupMemoryPerProcessor() / shared);
  return static_cast<int>(blocks);
}

bool validBlock(dim3 block) {
  return block.x != 0 && block.y != 0 && block.z != 0 && block.x <= MaxBlockThreads && block.y <= MaxBlockThreads
         && block.z <= MaxBlockThreads && static_cast<uint64_t>(block.x) * block.y * block.z <= MaxBlockThreads;
}

constexpr char BundleMagic[] = "__CLANG_OFFLOAD_BUNDLE__";
constexpr size_t BundleMagicSize = sizeof(BundleMagic) - 1U;
constexpr char CompressedBundleMagic[] = "CCOB";
constexpr size_t CompressedBundleMagicSize = sizeof(CompressedBundleMagic) - 1U;
constexpr size_t CompressedBundleHeaderV3Size = 32;
constexpr uint64_t MaximumBundleCount = 64;

struct Kernel {
  uint64_t object = 0;
  gemstone::hsa::KernelLayout layout;
  std::atomic<bool> built{false};
};

struct Module;

struct Variable {
  void *host = nullptr;
  std::string deviceName;
  size_t size = 0;
  std::shared_ptr<Module> module;
};

struct ModuleInstance {
  hsa_code_object_reader_t reader{};
  hsa_executable_t executable{};
  bool loaded = false;
  std::unordered_map<std::string, std::shared_ptr<Kernel>> kernels;

  void reset() {
    if (executable.handle != 0) hsa_executable_destroy(executable);
    if (reader.handle != 0) hsa_code_object_reader_destroy(reader);
    executable = {};
    reader = {};
    kernels.clear();
    loaded = false;
  }
};

struct Module {
  std::mutex lock;
  // The address of this member is the opaque value passed back to the HIP
  // compiler's registration callbacks.
  void *token = nullptr;
  const void *fatbin = nullptr;
  const void *image = nullptr;
  size_t imageSize = 0;
  std::array<ModuleInstance, gemstone::MaximumDeviceCount> instances;
  bool loaded = false;
  std::string error;
  std::vector<uint8_t> decompressedImage;
  std::vector<uint8_t> fileImage;
  std::vector<gemstone::hsa::ExecutableKernelMetadata> metadata;
  std::vector<const void *> variables;

  void reset() {
    for (auto &instance : instances)
      instance.reset();
    metadata.clear();
    loaded = false;
  }
  ~Module() { reset(); }
};

struct FunctionBinding {
  std::shared_ptr<Module> module;
  std::string name;
  std::array<std::shared_ptr<Kernel>, gemstone::MaximumDeviceCount> kernels;
};

struct ModuleHandle {
  std::shared_ptr<Module> module;
};

struct FunctionHandle {
  std::shared_ptr<Module> module;
  std::shared_ptr<Kernel> kernel;
};

struct Gpu {
  hsa_agent_t agent{};
  hsa_region_t deviceRegion{};
};

struct DeviceAllocation {
  size_t size = 0;
  int device = 0;
};

struct State {
  std::mutex lock;
  bool initialised = false;
  std::array<uint32_t, gemstone::MaximumDeviceCount> primaryContextReferences{};
  bool atexitRegistered = false;
  std::vector<Gpu> gpus;
  hsa_region_t kernargRegion{};
  gemstone::PeerAccess peers;
  std::unordered_map<hipStream_t, int> streamDevices;
  std::unordered_map<void **, std::shared_ptr<Module>> modules;
  std::vector<std::weak_ptr<Module>> driverModules;
  std::unordered_map<const void *, FunctionBinding> kernels;
  std::unordered_map<const void *, Variable> variables;
  gemstone::MemoryRegistrations hostRegistrations;
  std::unordered_map<void *, size_t> managedAllocations;
  std::unordered_map<void *, DeviceAllocation> deviceAllocations;
  // MGSyncInfo records of completed cooperative launches, for the next to reuse
  std::array<std::vector<void *>, gemstone::MaximumDeviceCount> syncRecords;
};

struct LaunchConfiguration {
  dim3 grid{1, 1, 1};
  dim3 block{1, 1, 1};
  size_t shared = 0;
  hipStream_t stream = nullptr;
};

bool traceModules();

State &state() {
  static State *value = new State();
  return *value;
}

bool validStream(hipStream_t stream) { return commands().hasStream(stream); }

thread_local hipError_t lastError = hipSuccess;
thread_local int currentDevice = 0;
thread_local LaunchConfiguration launchConfiguration;

// Queued work reports through its completion; the synchronising caller records that status as its own error.
hipError_t remember(hipError_t error, std::source_location where = std::source_location::current()) {
  static const bool traced = traceHip("errors");
  if (traced && error != hipSuccess)
    std::fprintf(stderr, "Gemstone HIP %s returned error %d\n", where.function_name(), static_cast<int>(error));
  if (!commands().onWorker()) lastError = error;
  return error;
}

hipError_t mapHsa(hsa_status_t status) {
  if (status == HSA_STATUS_SUCCESS) return hipSuccess;
  switch (status) {
    case HSA_STATUS_ERROR_NOT_INITIALIZED: return hipErrorNotInitialized;
    case HSA_STATUS_ERROR_INVALID_ARGUMENT: return hipErrorInvalidValue;
    case HSA_STATUS_ERROR_INVALID_AGENT: return hipErrorInvalidDevice;
    case HSA_STATUS_ERROR_INVALID_ALLOCATION: return hipErrorInvalidDevicePointer;
    case HSA_STATUS_ERROR_OUT_OF_RESOURCES: return hipErrorOutOfMemory;
    case HSA_STATUS_ERROR_INVALID_CODE_OBJECT:
    case HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER: return hipErrorInvalidImage;
    default: return hipErrorUnknown;
  }
}

// The error a launch reports when the service cannot build its kernel.
hipError_t buildError(gemstone::execution::FailureKind kind) {
  using gemstone::execution::FailureKind;
  switch (kind) {
    case FailureKind::InvalidProgram: return hipErrorInvalidImage;
    case FailureKind::Unsupported: return hipErrorNotSupported;
    case FailureKind::InvalidRequest: return hipErrorInvalidValue;
    case FailureKind::NotFound: return hipErrorNotFound;
    case FailureKind::InvalidConfiguration: return hipErrorInvalidConfiguration;
    case FailureKind::OutOfResources: return hipErrorLaunchOutOfResources;
    default: return hipErrorLaunchFailure;
  }
}

hsa_status_t collectGpus(hsa_agent_t agent, void *opaque) {
  hsa_device_type_t type{};
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  if (type == HSA_DEVICE_TYPE_GPU) static_cast<std::vector<Gpu> *>(opaque)->push_back(Gpu{.agent = agent});
  return HSA_STATUS_SUCCESS;
}

template <hsa_device_type_t Type> hsa_status_t findAgent(hsa_agent_t agent, void *opaque) {
  hsa_device_type_t type{};
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  if (type == Type) {
    *static_cast<hsa_agent_t *>(opaque) = agent;
    return HSA_STATUS_INFO_BREAK;
  }
  return HSA_STATUS_SUCCESS;
}

template <hsa_region_global_flag_t Flag> hsa_status_t findRegion(hsa_region_t region, void *opaque) {
  hsa_region_segment_t segment{};
  auto status = hsa_region_get_info(region, HSA_REGION_INFO_SEGMENT, &segment);
  if (status != HSA_STATUS_SUCCESS) return status;
  if (segment != HSA_REGION_SEGMENT_GLOBAL) return HSA_STATUS_SUCCESS;
  uint32_t flags = 0;
  status = hsa_region_get_info(region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags);
  if (status != HSA_STATUS_SUCCESS) return status;
  if ((flags & Flag) == 0) return HSA_STATUS_SUCCESS;
  *static_cast<hsa_region_t *>(opaque) = region;
  return HSA_STATUS_INFO_BREAK;
}

// property queries answer without initialising, so they check the environment hsa_init would
bool environmentAccepted() { return gemstone::environment::accepted(gemstone::hsa::invalidEnvironment()); }

bool ensureInitialised() {
  (void)commands();
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  if (runtime.initialised) return true;
  const hsa_status_t init = hsa_init();
  const hsa_status_t agents = init == HSA_STATUS_SUCCESS ? hsa_iterate_agents(collectGpus, &runtime.gpus) : HSA_STATUS_ERROR;
  const hsa_status_t kernargs =
      agents == HSA_STATUS_SUCCESS && !runtime.gpus.empty()
          ? hsa_agent_iterate_regions(runtime.gpus.front().agent, findRegion<HSA_REGION_GLOBAL_FLAG_KERNARG>, &runtime.kernargRegion)
          : HSA_STATUS_ERROR;
  bool devices = kernargs == HSA_STATUS_INFO_BREAK && runtime.kernargRegion.handle != 0;
  for (auto &gpu : runtime.gpus)
    devices = devices
              && hsa_agent_iterate_regions(gpu.agent, findRegion<HSA_REGION_GLOBAL_FLAG_COARSE_GRAINED>, &gpu.deviceRegion)
                     == HSA_STATUS_INFO_BREAK
              && gpu.deviceRegion.handle != 0;
  if (init != HSA_STATUS_SUCCESS || !devices) {
    hsa_shut_down();
    runtime.gpus.clear();
    runtime.kernargRegion = {};
    return false;
  }
  runtime.initialised = true;
  if (!runtime.atexitRegistered) {
    // Runtime cleanup also drains commands when no compiler fatbinary was registered.
    runtime.atexitRegistered = true;
    std::atexit([] {
      (void)waitCommands();
      auto &value = state();
      std::scoped_lock const exitGuard(value.lock);
      if (value.initialised) {
        hsa_shut_down();
        value.initialised = false;
      }
    });
  }
  return true;
}

bool validDevice(int device) { return device >= 0 && static_cast<uint32_t>(device) < gemstone::deviceCount(); }

int streamDevice(hipStream_t stream) {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  const auto found = runtime.streamDevices.find(stream);
  return found == runtime.streamDevices.end() ? currentDevice : found->second;
}

hsa_agent_t gpuAgent(int device) { return state().gpus[static_cast<size_t>(device)].agent; }
hsa_region_t gpuRegion(int device) { return state().gpus[static_cast<size_t>(device)].deviceRegion; }

int deviceOfAgent(hsa_agent_t agent) {
  const auto &gpus = state().gpus;
  const auto found = std::find_if(gpus.begin(), gpus.end(), [&](const Gpu &gpu) { return gpu.agent.handle == agent.handle; });
  return found == gpus.end() ? 0 : static_cast<int>(found - gpus.begin());
}

hipError_t allocateHost(void **pointer, size_t size, unsigned flags = 0) {
  if (!pointer) return remember(hipErrorInvalidValue);
  if (size == 0) {
    *pointer = nullptr;
    return remember(hipSuccess);
  }
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  void *memory = std::calloc(1, size);
  if (!memory) return remember(hipErrorOutOfMemory);
  const hsa_status_t status = hsa_memory_register(memory, size);
  if (status != HSA_STATUS_SUCCESS) {
    std::free(memory);
    return remember(mapHsa(status));
  }
  {
    std::scoped_lock const guard(state().lock);
    state().hostRegistrations.add({.base = reinterpret_cast<uintptr_t>(memory), .size = size}, gemstone::MemoryOwnership::Owned, flags);
  }
  *pointer = memory;
  return remember(hipSuccess);
}

hipError_t releaseHost(void *pointer) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!pointer) return remember(hipSuccess);
  {
    std::scoped_lock const guard(state().lock);
    const auto *const found = state().hostRegistrations.exact(reinterpret_cast<uintptr_t>(pointer));
    if (!found || found->ownership != gemstone::MemoryOwnership::Owned) return remember(hipErrorInvalidValue);
    const hsa_status_t status = hsa_memory_deregister(pointer, found->range.size);
    if (status != HSA_STATUS_SUCCESS) return remember(mapHsa(status));
    state().hostRegistrations.remove(reinterpret_cast<uintptr_t>(pointer), gemstone::MemoryOwnership::Owned);
  }
  std::free(pointer);
  return remember(hipSuccess);
}

bool readNativeU64(const uint8_t *bytes, size_t size, size_t &cursor, uint64_t &value) {
  if (cursor > size || size - cursor < sizeof(value)) return false;
  value = readNative<uint64_t>(bytes, cursor);
  cursor += sizeof(value);
  return true;
}

bool decompressBundle(const uint8_t *compressed, std::vector<uint8_t> &decompressed) {
  if (!compressed || std::memcmp(compressed, CompressedBundleMagic, CompressedBundleMagicSize) != 0) return false;
  const auto version = readNative<uint16_t>(compressed, 4);
  const auto format = readNative<uint16_t>(compressed, 6);
  if (version != 3 || format != 1) return false;
  const auto fileSize = readNative<uint64_t>(compressed, 8);
  const auto uncompressedSize = readNative<uint64_t>(compressed, 16);
  if (fileSize < CompressedBundleHeaderV3Size || uncompressedSize == 0 || fileSize > std::numeric_limits<size_t>::max()
      || uncompressedSize > std::numeric_limits<size_t>::max())
    return false;
  const auto compressedSize = static_cast<size_t>(fileSize) - CompressedBundleHeaderV3Size;
  const auto outputSize = static_cast<size_t>(uncompressedSize);
  return gemstone::decompressZstd(decompressed, compressed + CompressedBundleHeaderV3Size, compressedSize, outputSize);
}

bool bundleTargetCompatible(const std::string &triple);

// HIP fat binaries may contain several AMDGPU images, and the host ROCr agent
// is not necessarily the agent represented by this provider, so selection
// follows the provider's advertised target rather than bundle order.
bool selectBundle(const void *fatbin, const void **image, size_t *imageSize, std::vector<uint8_t> *decompressed = nullptr) {
  if (!fatbin || !image || !imageSize) return false;
  const auto *bytes = static_cast<const uint8_t *>(fatbin);
  const auto *bundle =
      (std::memcmp(bytes, BundleMagic, BundleMagicSize) == 0 || std::memcmp(bytes, CompressedBundleMagic, CompressedBundleMagicSize) == 0)
          ? bytes
          : static_cast<const uint8_t *>(static_cast<const void *const *>(fatbin)[1]);
  if (!bundle) return false;
  if (std::memcmp(bundle, CompressedBundleMagic, CompressedBundleMagicSize) == 0) {
    if (!decompressed || !decompressBundle(bundle, *decompressed)) return false;
    bundle = decompressed->data();
  }
  if (std::memcmp(bundle, BundleMagic, BundleMagicSize) != 0) return false;
  size_t cursor = BundleMagicSize;
  uint64_t count = 0;
  if (!readNativeU64(bundle, std::numeric_limits<size_t>::max(), cursor, count) || count == 0 || count > MaximumBundleCount) return false;
  struct Entry {
    uint64_t offset;
    uint64_t size;
    std::string triple;
  };
  std::vector<Entry> entries;
  entries.reserve(static_cast<size_t>(count));
  // The bundle is embedded in a process image and does not carry a total
  // length. Descriptor and triple lengths are bounded before advancing so a
  // malformed registration cannot walk arbitrary memory.
  for (uint64_t index = 0; index < count; ++index) {
    uint64_t offset = 0;
    uint64_t size = 0;
    uint64_t tripleSize = 0;
    if (!readNativeU64(bundle, std::numeric_limits<size_t>::max(), cursor, offset)
        || !readNativeU64(bundle, std::numeric_limits<size_t>::max(), cursor, size)
        || !readNativeU64(bundle, std::numeric_limits<size_t>::max(), cursor, tripleSize) || tripleSize == 0 || tripleSize > 4096)
      return false;
    const auto *triple = reinterpret_cast<const char *>(bundle + cursor);
    entries.push_back(Entry{.offset = offset, .size = size, .triple = std::string(triple, static_cast<size_t>(tripleSize))});
    cursor += static_cast<size_t>(tripleSize);
  }
  const Entry *target = nullptr;
  for (const auto &entry : entries) {
    if (!(entry.triple ^ contains_slice("amdgcn")) && !(entry.triple ^ contains_slice("amdhsa"))) continue;
    if (entry.offset == 0 || entry.size == 0 || entry.offset > std::numeric_limits<size_t>::max()
        || entry.size > std::numeric_limits<size_t>::max() - static_cast<size_t>(entry.offset))
      return false;
    if (bundleTargetCompatible(entry.triple)) target = &entry;
  }
  // Never run an image for a different virtual target. A fallback would make
  // a matrix case appear to work while executing another generation's ISA.
  if (!target) return false;
  *image = bundle + static_cast<size_t>(target->offset);
  *imageSize = static_cast<size_t>(target->size);
  return true;
}

bool imageIsElf(const void *image) {
  if (!image) return false;
  const auto *bytes = static_cast<const uint8_t *>(image);
  return bytes[EI_MAG0] == ELFMAG0 && bytes[EI_MAG1] == ELFMAG1 && bytes[EI_MAG2] == ELFMAG2 && bytes[EI_MAG3] == ELFMAG3;
}

// HIP fatbinaries emitted by ROCm use the first target in an ISA family for
// host-side registration (for example gfx1030), even when the virtual agent
// is exposed as a later ASIC variant (gfx1036).  These variants share the
// decoder/execution profile; accept the canonical family image while still
// rejecting a genuinely different generation.
bool bundleTargetCompatible(const std::string &triple) {
  const std::string target = gemstone::hsa::targetArch();
  if (triple ^ contains_slice(target)) return true;
  constexpr std::array<std::pair<std::string_view, std::string_view>, 4> families = {
      {{"gfx103", "gfx1030"}, {"gfx110", "gfx1100"}, {"gfx115", "gfx1150"}, {"gfx120", "gfx1200"}}};
  return families ^ exists([&](std::string_view prefix, std::string_view canonical) {
           return (target ^ starts_with(prefix)) && (triple ^ contains_slice(canonical));
         });
}

std::shared_ptr<Module> findModule(void **token) {
  auto &runtime = state();
  const auto found = runtime.modules.find(token);
  return found == runtime.modules.end() ? std::shared_ptr<Module>{} : found->second;
}

bool loadModule(Module &module, int device) {
  auto &instance = module.instances[static_cast<size_t>(device)];
  if (instance.loaded) return true;
  if (!module.image && module.fatbin) {
    if (!selectBundle(module.fatbin, &module.image, &module.imageSize, &module.decompressedImage)) {
      module.error = std::string("HIP fatbinary has no ") + gemstone::hsa::targetArch() + " bundle";
      return false;
    }
  }
  if (!module.image || module.imageSize == 0) {
    module.error = "HIP fatbinary has no AMDGPU code object";
    return false;
  }
  const hsa_status_t reader = hsa_code_object_reader_create_from_memory(module.image, module.imageSize, &instance.reader);
  const hsa_status_t executable =
      reader == HSA_STATUS_SUCCESS
          ? hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_DEFAULT, nullptr, &instance.executable)
          : HSA_STATUS_ERROR;
  const hsa_status_t load = executable == HSA_STATUS_SUCCESS ? hsa_executable_load_agent_code_object(instance.executable, gpuAgent(device),
                                                                                                     instance.reader, nullptr, nullptr)
                                                             : HSA_STATUS_ERROR;
  const hsa_status_t readerDestroy = instance.reader.handle == 0 ? HSA_STATUS_SUCCESS : hsa_code_object_reader_destroy(instance.reader);
  instance.reader = {};
  const hsa_status_t freeze = load == HSA_STATUS_SUCCESS ? hsa_executable_freeze(instance.executable, nullptr) : HSA_STATUS_ERROR;
  if (reader != HSA_STATUS_SUCCESS || executable != HSA_STATUS_SUCCESS || load != HSA_STATUS_SUCCESS || readerDestroy != HSA_STATUS_SUCCESS
      || freeze != HSA_STATUS_SUCCESS) {
    module.error = "public HSA rejected the HIP code object";
    if (instance.executable.handle != 0) hsa_executable_destroy(instance.executable);
    instance.executable = {};
    return false;
  }
  if (module.metadata.empty() && !gemstone::hsa::copyExecutableKernelMetadata(instance.executable, module.metadata)) {
    module.error = "public HSA returned no AMDGPU kernel metadata";
    hsa_executable_destroy(instance.executable);
    instance.executable = {};
    return false;
  }
  instance.loaded = true;
  module.loaded = true;
  return true;
}

std::shared_ptr<Kernel> resolveModuleKernel(Module &module, const std::string &name, int device) {
  std::scoped_lock const guard(module.lock);
  if (!loadModule(module, device)) return {};
  auto &instance = module.instances[static_cast<size_t>(device)];
  if (const auto cached = instance.kernels.find(name); cached != instance.kernels.end()) return cached->second;
  gemstone::hsa::ExecutableKernelMetadata metadata;
  if (!gemstone::hsa::copyExecutableKernelMetadata(instance.executable, name, metadata)) return {};
  hsa_executable_symbol_t symbol{};
  if (hsa_executable_get_symbol_by_name(instance.executable, metadata.symbol.c_str(), &state().gpus[static_cast<size_t>(device)].agent,
                                        &symbol)
      != HSA_STATUS_SUCCESS)
    return {};
  uint64_t object = 0;
  if (hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) != HSA_STATUS_SUCCESS) return {};
  auto kernel = std::make_shared<Kernel>();
  kernel->object = object;
  kernel->layout = metadata.layout;
  instance.kernels.emplace(metadata.name, kernel);
  instance.kernels.emplace(metadata.symbol, kernel);
  if (metadata.symbol.size() > 3 && (metadata.symbol ^ ends_with(".kd")))
    instance.kernels.emplace(metadata.symbol ^ strip_suffix(".kd"), kernel);
  return kernel;
}

bool ensureModuleLoaded(const std::shared_ptr<Module> &module, int device) {
  if (!module || !ensureInitialised()) return false;
  std::scoped_lock const guard(module->lock);
  return loadModule(*module, device);
}

std::shared_ptr<Kernel> resolveKernel(const void *functionAddress, int device) {
  auto &runtime = state();
  FunctionBinding binding;
  {
    std::scoped_lock const guard(runtime.lock);
    const auto found = runtime.kernels.find(functionAddress);
    if (found == runtime.kernels.end()) return {};
    binding = found->second;
  }
  if (const auto &cached = binding.kernels[static_cast<size_t>(device)]) return cached;
  if (!ensureModuleLoaded(binding.module, device)) {
    if (traceModules())
      std::fprintf(stderr, "Gemstone HIP kernel load failed name=%s error=%s\n", binding.name.c_str(), binding.module->error.c_str());
    return {};
  }
  auto kernel = resolveModuleKernel(*binding.module, binding.name, device);
  if (!kernel) return {};
  {
    std::scoped_lock const guard(runtime.lock);
    const auto found = runtime.kernels.find(functionAddress);
    if (found != runtime.kernels.end() && found->second.module == binding.module && found->second.name == binding.name)
      found->second.kernels[static_cast<size_t>(device)] = kernel;
  }
  return kernel;
}

/** A device record for HIP's MGSyncInfo: one a completed cooperative launch released, or a fresh one. */
hsa_status_t acquireSyncRecord(void **record, int device) {
  {
    std::scoped_lock const guard(state().lock);
    auto &released = state().syncRecords[static_cast<size_t>(device)];
    if (!released.empty()) {
      *record = released.back();
      released.pop_back();
      return HSA_STATUS_SUCCESS;
    }
  }
  return hsa_memory_allocate(gpuRegion(device), MultigridRecordBytes, record);
}

void releaseSyncRecord(void *record, int device) {
  std::scoped_lock const guard(state().lock);
  state().syncRecords[static_cast<size_t>(device)].push_back(record);
}

hipError_t dispatchKernel(const std::shared_ptr<Kernel> &kernel, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t stream,
                          const LaunchBuffer &launchBuffer = {}, const dim3 *globalWorkItems = nullptr, bool cooperative = false) {
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  if (!kernel || (!args && !launchBuffer.data && kernel->layout.explicitArgumentCount != 0)) {
    if (traceModules())
      std::fprintf(stderr, "Gemstone HIP dispatch rejected kernel=%p args=%p\n", static_cast<const void *>(kernel.get()),
                   static_cast<void *>(args));
    return remember(hipErrorInvalidDeviceFunction);
  }
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP dispatch kernel=%p object=%llu explicit=%u kernarg=%u shared=%zu\n",
                 static_cast<const void *>(kernel.get()), static_cast<unsigned long long>(kernel->object),
                 kernel->layout.explicitArgumentCount, kernel->layout.kernargSize, shared);
  if (grid.x == 0 || grid.y == 0 || grid.z == 0 || grid.x > MaxGridDimension || grid.y > MaxGridDimension || grid.z > MaxGridDimension
      || !validBlock(block)
      || (!globalWorkItems
          && (grid.x > std::numeric_limits<uint32_t>::max() / block.x || grid.y > std::numeric_limits<uint32_t>::max() / block.y
              || grid.z > std::numeric_limits<uint32_t>::max() / block.z))
      || shared > std::numeric_limits<uint32_t>::max())
    return remember(hipErrorInvalidConfiguration);
  const dim3 global = !globalWorkItems ? dim3(grid.x * block.x, grid.y * block.y, grid.z * block.z) : *globalWorkItems;
  if (global.x < block.x || global.y < block.y || global.z < block.z) return remember(hipErrorInvalidConfiguration);
  // A kernel is built on its first launch, which reports a build failure the way a lazily loaded module does.
  if (!kernel->built.load()) {
    if (const auto failure = gemstone::hsa::buildExecutableKernel(kernel->object)) {
      if (traceModules()) std::fprintf(stderr, "Gemstone HIP kernel build failed: %s\n", failure->detail.c_str());
      return remember(buildError(failure->kind));
    }
    kernel->built.store(true);
  }
  const int launchDevice = streamDevice(stream);
  void *kernarg = nullptr;
  hsa_status_t status = kernel->layout.kernargSize == 0 ? HSA_STATUS_SUCCESS
                                                        : hsa_memory_allocate(state().kernargRegion, kernel->layout.kernargSize, &kernarg);
  if (status != HSA_STATUS_SUCCESS) return remember(mapHsa(status));
  if (kernel->layout.kernargSize != 0) std::memset(kernarg, 0, kernel->layout.kernargSize);
  if (launchBuffer.data) {
    size_t required = 0;
    for (size_t argument = 0; argument < kernel->layout.explicitArgumentCount; ++argument) {
      const auto &field = kernel->layout.fields[argument];
      required = std::max(required, static_cast<size_t>(field.offset) + field.size);
    }
    if (!launchBuffer.copyTo(kernarg, kernel->layout.kernargSize, required)) {
      hsa_memory_free(kernarg);
      return remember(hipErrorInvalidValue);
    }
  } else {
    size_t argument = 0;
    for (const auto &field : kernel->layout.fields) {
      if (argument >= kernel->layout.explicitArgumentCount) break;
      if (field.offset > kernel->layout.kernargSize || field.size > kernel->layout.kernargSize - field.offset) {
        hsa_memory_free(kernarg);
        return remember(hipErrorInvalidValue);
      }
      if (args[argument] != nullptr) std::memcpy(static_cast<uint8_t *>(kernarg) + field.offset, args[argument], field.size);
      ++argument;
    }
  }
  const auto overlapsExplicit = [&](uint32_t offset, uint32_t size) {
    size_t explicitFields = 0;
    for (const auto &field : kernel->layout.fields) {
      if (explicitFields >= kernel->layout.explicitArgumentCount) break;
      ++explicitFields;
      if (field.offset < offset + size && offset < field.offset + field.size) return true;
    }
    return false;
  };
  const auto hasHiddenField = [&](uint32_t offset, uint32_t size) {
    for (size_t index = kernel->layout.explicitArgumentCount; index < kernel->layout.fields.size(); ++index) {
      const auto &field = kernel->layout.fields[index];
      if (field.offset == offset && field.size == size) return true;
    }
    return false;
  };
  const auto writeU16 = [&](uint32_t offset, uint32_t value) {
    if (!overlapsExplicit(offset, sizeof(uint16_t)) && offset + sizeof(uint16_t) <= kernel->layout.kernargSize) {
      const auto narrowed = static_cast<uint16_t>(value);
      std::memcpy(static_cast<uint8_t *>(kernarg) + offset, &narrowed, sizeof(narrowed));
    }
  };
  const auto writeU32 = [&](uint32_t offset, uint32_t value) {
    if (!overlapsExplicit(offset, sizeof(uint32_t)) && offset + sizeof(uint32_t) <= kernel->layout.kernargSize)
      std::memcpy(static_cast<uint8_t *>(kernarg) + offset, &value, sizeof(value));
  };
  void *syncInfo = nullptr;
  // Code object V5 lays out the implicit dispatch arguments immediately
  // after the explicit arguments: block counts, group sizes, remainders,
  // reserved words, global offsets, and finally the grid dimensionality.
  // Older V4 objects use a different all-64-bit implicit layout; their slots
  // are already zeroed above, which is the only offset configuration this
  // virtual device currently exposes.
  if (kernel->layout.explicitArgumentCount < kernel->layout.fields.size()) {
    const auto &firstHidden = kernel->layout.fields[kernel->layout.explicitArgumentCount];
    if (firstHidden.size == sizeof(uint32_t)) {
      const uint32_t hidden = firstHidden.offset;
      // V5 counts complete groups separately from the work-items in a partial final group.
      writeU32(hidden + 0, global.x / block.x);
      writeU32(hidden + 4, global.y / block.y);
      writeU32(hidden + 8, global.z / block.z);
      writeU16(hidden + 12, block.x);
      writeU16(hidden + 14, block.y);
      writeU16(hidden + 16, block.z);
      writeU16(hidden + 18, static_cast<uint32_t>(global.x % block.x));
      writeU16(hidden + 20, static_cast<uint32_t>(global.y % block.y));
      writeU16(hidden + 22, static_cast<uint32_t>(global.z % block.z));
      writeU16(hidden + 64, dispatchDimensions(grid, block));
      // hidden_hostcall_buffer: the device library's printf and assert packets
      const uint32_t hostcall = hidden + 80;
      if (hasHiddenField(hostcall, sizeof(uint64_t)) && !overlapsExplicit(hostcall, sizeof(uint64_t))) {
        const auto buffer = reinterpret_cast<uint64_t>(gemstone::hsa::hostcallBuffer());
        if (buffer == 0) {
          hsa_memory_free(kernarg);
          return remember(hipErrorOutOfMemory);
        }
        std::memcpy(static_cast<uint8_t *>(kernarg) + hostcall, &buffer, sizeof(buffer));
      }
      // hidden_multigrid_sync_arg: grid.sync() on targets without GWS counts workgroup arrivals in HIP's MGSyncInfo
      // {mgs, grid_id, num_grids, prev_sum, all_sum, the grid's two sync words, num_wg}
      const uint32_t multigrid = hidden + 88;
      if (cooperative && !overlapsExplicit(multigrid, sizeof(uint64_t)) && multigrid + sizeof(uint64_t) <= kernel->layout.kernargSize) {
        const uint64_t groups = static_cast<uint64_t>(grid.x) * grid.y * grid.z;
        std::array<uint8_t, MultigridRecordBytes> record{};
        const uint32_t grids = 1;
        const uint32_t workgroups = static_cast<uint32_t>(groups);
        std::memcpy(record.data() + 12, &grids, sizeof(grids));
        std::memcpy(record.data() + 24, &groups, sizeof(groups));
        std::memcpy(record.data() + 40, &workgroups, sizeof(workgroups));
        status = acquireSyncRecord(&syncInfo, launchDevice);
        if (status == HSA_STATUS_SUCCESS) status = hsa_memory_copy(syncInfo, record.data(), record.size());
        if (status != HSA_STATUS_SUCCESS) {
          if (syncInfo) releaseSyncRecord(syncInfo, launchDevice);
          hsa_memory_free(kernarg);
          return remember(mapHsa(status));
        }
        const auto address = reinterpret_cast<uint64_t>(syncInfo);
        std::memcpy(static_cast<uint8_t *>(kernarg) + multigrid, &address, sizeof(address));
      }
    }
  }
  auto owned = std::shared_ptr<void>(kernarg, [](void *memory) {
    if (memory) (void)hsa_memory_free(memory);
  });
  auto ownedSyncInfo = std::shared_ptr<void>(syncInfo, [launchDevice](void *memory) {
    if (memory) releaseSyncRecord(memory, launchDevice);
  });
  return remember(enqueueCommand(stream, [=]() mutable -> int32_t {
    hsa_queue_t *dispatchQueue{};
    if (hsa_queue_create(gpuAgent(launchDevice), 64, cooperative ? HSA_QUEUE_TYPE_COOPERATIVE : HSA_QUEUE_TYPE_MULTI, nullptr, nullptr,
                         UINT32_MAX, UINT32_MAX, &dispatchQueue)
        != HSA_STATUS_SUCCESS)
      return hipErrorOutOfMemory;
    const auto queueOwner = std::shared_ptr<hsa_queue_t>(dispatchQueue, [](hsa_queue_t *queue) { (void)hsa_queue_destroy(queue); });
    hsa_signal_t completion{};
    hipError_t const dispatchError = hipErrorLaunchFailure;
    hsa_status_t status = hsa_signal_create(1, 0, nullptr, &completion);
    if (status == HSA_STATUS_SUCCESS) {
      const uint64_t index = hsa_queue_add_write_index_relaxed(dispatchQueue, 1);
      auto *packet = &static_cast<hsa_kernel_dispatch_packet_t *>(dispatchQueue->base_address)[index & (dispatchQueue->size - 1U)];
      // Keep the slot unpublished while its body is rebuilt.  Clearing the
      // header with memset after publishing the write index briefly turns the
      // slot into a vendor-specific (type 0) packet, which the doorbell worker
      // is allowed to consume before the final dispatch header is released.
      std::memset(reinterpret_cast<uint8_t *>(packet) + sizeof(packet->header), 0, sizeof(*packet) - sizeof(packet->header));
      packet->setup = dispatchDimensions(grid, block) << HSA_KERNEL_DISPATCH_PACKET_SETUP_DIMENSIONS;
      packet->workgroup_size_x = static_cast<uint16_t>(block.x);
      packet->workgroup_size_y = static_cast<uint16_t>(block.y);
      packet->workgroup_size_z = static_cast<uint16_t>(block.z);
      packet->grid_size_x = global.x;
      packet->grid_size_y = global.y;
      packet->grid_size_z = global.z;
      packet->group_segment_size = static_cast<uint32_t>(shared);
      packet->kernel_object = kernel->object;
      packet->kernarg_address = kernarg;
      packet->completion_signal = completion;
      const uint16_t header = HSA_PACKET_TYPE_KERNEL_DISPATCH << HSA_PACKET_HEADER_TYPE;
      __atomic_store_n(&packet->header, header, __ATOMIC_RELEASE);
      hsa_signal_store_screlease(dispatchQueue->doorbell_signal, static_cast<hsa_signal_value_t>(index));
      while (hsa_signal_load_scacquire(completion) == 1)
        std::this_thread::yield();
      // The queue read index advances after the HSA worker mirrors pageable
      // writes back to host memory. Wait for that handoff before returning to a
      // caller that may immediately read or free a captured host pointer.
      if (status == HSA_STATUS_SUCCESS) {
        const auto deadline = std::chrono::steady_clock::now() + std::chrono::hours(1);
        while (hsa_queue_load_read_index_scacquire(dispatchQueue) <= index) {
          if (std::chrono::steady_clock::now() >= deadline) {
            status = HSA_STATUS_ERROR;
            break;
          }
          std::this_thread::yield();
        }
      }
    }
    owned.reset();
    ownedSyncInfo.reset();
    const hsa_status_t destroyed = hsa_signal_destroy(completion);
    if (status != HSA_STATUS_SUCCESS) {
      if (traceModules()) std::fprintf(stderr, "Gemstone HIP dispatch HSA status=%d\n", static_cast<int>(status));
      return dispatchError;
    }
    return mapHsa(destroyed);
  }));
}

// Every block of a cooperative launch must fit on the device at once.
hipError_t dispatchCooperative(const std::shared_ptr<Kernel> &kernel, dim3 grid, dim3 block, void **args, size_t shared,
                               hipStream_t stream) {
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  if (!kernel) return remember(hipErrorInvalidDeviceFunction);
  const uint64_t blocks = static_cast<uint64_t>(grid.x) * grid.y * grid.z;
  const uint64_t resident = static_cast<uint64_t>(residentBlocks(static_cast<uint64_t>(block.x) * block.y * block.z, shared))
                            * gemstone::hsa::deviceModel().processors();
  if (blocks != 0 && validBlock(block) && blocks > resident) return remember(hipErrorCooperativeLaunchTooLarge);
  return dispatchKernel(kernel, grid, block, args, shared, stream, {}, nullptr, true);
}

hipError_t dispatch(const void *functionAddress, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t stream) {
  const auto kernel = resolveKernel(functionAddress, streamDevice(stream));
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP direct launch host=%p kernel=%p\n", functionAddress, static_cast<const void *>(kernel.get()));
  return dispatchKernel(kernel, grid, block, args, shared, stream);
}

FunctionHandle *functionHandle(hipFunction_t function) { return reinterpret_cast<FunctionHandle *>(function); }

ModuleHandle *moduleHandle(hipModule_t module) { return reinterpret_cast<ModuleHandle *>(module); }

bool traceModules() {
  static const bool enabled = traceHip("modules");
  return enabled;
}

bool selectImage(Module &module, const void *input) {
  if (imageIsElf(input)) {
    const auto size = gemstone::elfImageSize(input);
    if (!size) {
      module.error = "invalid ELF64 AMDGPU code object";
      return false;
    }
    module.image = input;
    module.imageSize = *size;
    return true;
  }
  if (!selectBundle(input, &module.image, &module.imageSize, &module.decompressedImage)) {
    module.error = std::string("HIP module has no ") + gemstone::hsa::targetArch() + " code object";
    return false;
  }
  return true;
}

std::shared_ptr<Module> createModule(const void *input) {
  auto module = std::make_shared<Module>();
  if (selectImage(*module, input)) loadModule(*module, currentDevice);
  return module;
}

hipError_t publishModule(std::shared_ptr<Module> loaded, hipModule_t *module) {
  if (!loaded->loaded) return hipErrorInvalidImage;
  auto *handle = new (std::nothrow) ModuleHandle{std::move(loaded)};
  if (!handle) return hipErrorOutOfMemory;
  {
    std::scoped_lock const guard(state().lock);
    state().driverModules.push_back(handle->module);
  }
  *module = reinterpret_cast<hipModule_t>(handle);
  return hipSuccess;
}

uint32_t workgroupCount(uint32_t global, uint32_t local) { return local == 0 ? 0 : (global / local) + (global % local == 0 ? 0 : 1); }

} // namespace

extern "C" hipError_t hipInit(unsigned int flags) {
  if (flags != 0) return remember(hipErrorInvalidValue);
  return remember(ensureInitialised() ? hipSuccess : hipErrorNoDevice);
}

extern "C" hipError_t hipDriverGetVersion(int *version) {
  if (!version) return remember(hipErrorInvalidValue);
  *version = RuntimeVersion;
  return remember(hipSuccess);
}

extern "C" hipError_t hipRuntimeGetVersion(int *version) { return hipDriverGetVersion(version); }

extern "C" hipError_t hipGetDeviceCount(int *count) {
  if (!count) return remember(hipErrorInvalidValue);
  *count = ensureInitialised() ? static_cast<int>(state().gpus.size()) : 0;
  return remember(*count != 0 ? hipSuccess : hipErrorNoDevice);
}

extern "C" hipError_t hipGetDevice(int *device) {
  if (!device) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  *device = currentDevice;
  return remember(hipSuccess);
}

extern "C" hipError_t hipSetDevice(int device) {
  if (!validDevice(device) || !ensureInitialised()) return remember(hipErrorInvalidDevice);
  currentDevice = device;
  return remember(hipSuccess);
}

namespace {
std::array<unsigned char, gemstone::MaximumDeviceCount> primaryContextTokens;
thread_local hipCtx_t currentContext = nullptr;
thread_local std::vector<hipCtx_t> contextStack;

hipCtx_t primaryContext(int device) { return reinterpret_cast<hipCtx_t>(&primaryContextTokens[static_cast<size_t>(device)]); }

std::optional<int> contextDevice(hipCtx_t context) {
  for (int device = 0; validDevice(device); ++device)
    if (context == primaryContext(device)) return device;
  return std::nullopt;
}
} // namespace

extern "C" hipError_t hipDevicePrimaryCtxRetain(hipCtx_t *context, hipDevice_t device) {
  if (!context || !validDevice(device)) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  auto &references = runtime.primaryContextReferences[static_cast<size_t>(device)];
  if (references == UINT32_MAX) return remember(hipErrorOutOfMemory);
  ++references;
  *context = primaryContext(device);
  return remember(hipSuccess);
}

extern "C" hipError_t hipDevicePrimaryCtxRelease(hipDevice_t device) {
  if (!validDevice(device)) return remember(hipErrorInvalidDevice);
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  auto &references = runtime.primaryContextReferences[static_cast<size_t>(device)];
  if (references == 0) return remember(hipErrorInvalidValue);
  --references;
  return remember(hipSuccess);
}

extern "C" hipError_t hipCtxPushCurrent(hipCtx_t context) {
  const auto device = contextDevice(context);
  if (!device) return remember(hipErrorInvalidContext);
  try {
    contextStack.push_back(currentContext);
  } catch (...) {
    return remember(hipErrorOutOfMemory);
  }
  currentContext = context;
  currentDevice = *device;
  return remember(hipSuccess);
}

extern "C" hipError_t hipCtxPopCurrent(hipCtx_t *context) {
  if (!context || contextStack.empty()) return remember(hipErrorInvalidContext);
  *context = currentContext;
  currentContext = contextStack.back();
  contextStack.pop_back();
  if (const auto device = contextDevice(currentContext)) currentDevice = *device;
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGet(hipDevice_t *device, int ordinal) {
  if (!device) return remember(hipErrorInvalidValue);
  if (!validDevice(ordinal) || !ensureInitialised()) return remember(hipErrorInvalidDevice);
  *device = ordinal;
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceCanAccessPeer(int *canAccess, int device, int peer) {
  if (!canAccess) return remember(hipErrorInvalidValue);
  if (!validDevice(device) || !validDevice(peer)) return remember(hipErrorInvalidDevice);
  *canAccess = device != peer ? 1 : 0;
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceEnablePeerAccess(int peer, unsigned flags) {
  if (flags != 0) return remember(hipErrorInvalidValue);
  if (!validDevice(peer) || peer == currentDevice) return remember(hipErrorInvalidDevice);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  if (!runtime.peers.enable(static_cast<uint32_t>(currentDevice), static_cast<uint32_t>(peer)))
    return remember(hipErrorPeerAccessAlreadyEnabled);
  const hsa_agent_t agent = gpuAgent(currentDevice);
  for (const auto &[pointer, allocation] : runtime.deviceAllocations)
    if (allocation.device == peer) (void)hsa_amd_agents_allow_access(1, &agent, nullptr, pointer);
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceDisablePeerAccess(int peer) {
  if (!validDevice(peer) || peer == currentDevice) return remember(hipErrorInvalidDevice);
  if (!state().peers.disable(static_cast<uint32_t>(currentDevice), static_cast<uint32_t>(peer)))
    return remember(hipErrorPeerAccessNotEnabled);
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGetP2PAttribute(int *value, hipDeviceP2PAttr attribute, int source, int destination) {
  if (!value) return remember(hipErrorInvalidValue);
  if (!validDevice(source) || !validDevice(destination) || source == destination) return remember(hipErrorInvalidDevice);
  switch (attribute) {
    case hipDevP2PAttrPerformanceRank: *value = 0; break;
    case hipDevP2PAttrAccessSupported:
    case hipDevP2PAttrNativeAtomicSupported:
    case hipDevP2PAttrHipArrayAccessSupported: *value = 1; break;
    default: return remember(hipErrorInvalidValue);
  }
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemcpyPeer(void *destination, int destinationDevice, const void *source, int sourceDevice, size_t size) {
  if (!validDevice(destinationDevice) || !validDevice(sourceDevice)) return remember(hipErrorInvalidDevice);
  return hipMemcpy(destination, source, size, hipMemcpyDeviceToDevice);
}

extern "C" hipError_t hipMemcpyPeerAsync(void *destination, int destinationDevice, const void *source, int sourceDevice, size_t size,
                                         hipStream_t stream) {
  if (!validDevice(destinationDevice) || !validDevice(sourceDevice)) return remember(hipErrorInvalidDevice);
  return hipMemcpyAsync(destination, source, size, hipMemcpyDeviceToDevice, stream);
}

static std::string pciBusId(int device) {
  char text[32];
  std::snprintf(text, sizeof(text), "0000:%02x:00.0", static_cast<unsigned>(device));
  return text;
}

extern "C" hipError_t hipDeviceGetPCIBusId(char *busId, int length, int device) {
  if (!busId || length <= 0 || !validDevice(device)) return remember(hipErrorInvalidValue);
  const auto text = pciBusId(device);
  if (text.size() >= static_cast<size_t>(length)) return remember(hipErrorInvalidValue);
  std::memcpy(busId, text.c_str(), text.size() + 1U);
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGetByPCIBusId(int *device, const char *busId) {
  if (!device || !busId) return remember(hipErrorInvalidValue);
  for (int ordinal = 0; validDevice(ordinal); ++ordinal)
    if (pciBusId(ordinal) == busId) {
      *device = ordinal;
      return remember(hipSuccess);
    }
  return remember(hipErrorInvalidValue);
}

extern "C" hipError_t hipDeviceGetUuid(hipUUID *uuid, hipDevice_t device) {
  if (!uuid || !validDevice(device)) return remember(hipErrorInvalidValue);
  std::memset(uuid->bytes, 0, sizeof(uuid->bytes));
  std::memcpy(uuid->bytes, "GEMSTONE-HIP-", 13);
  uuid->bytes[13] = '-';
  uuid->bytes[14] = static_cast<char>('0' + ((device + 1) / 10));
  uuid->bytes[15] = static_cast<char>('0' + ((device + 1) % 10));
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceSynchronize() { return remember(ensureInitialised() ? waitCommands() : hipErrorNoDevice); }
extern "C" hipError_t hipDeviceGetLimit(size_t *value, hipLimit_t limit) {
  if (!value || limit >= hipLimitRange) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  if (limit != hipLimitMallocHeapSize) return remember(hipErrorUnsupportedLimit);
  *value = gemstone::hsa::deviceHeapLimit();
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceSetLimit(hipLimit_t limit, size_t value) {
  if (limit >= hipLimitRange) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  if (limit != hipLimitMallocHeapSize) return remember(hipErrorUnsupportedLimit);
  if (value >= DeviceTotalMemoryBytes) return remember(hipErrorInvalidValue);
  gemstone::hsa::setDeviceHeapLimit(value);
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceReset() {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  auto &runtime = state();
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  std::scoped_lock const guard(runtime.lock);
  for (const auto &[pointer, _] : runtime.deviceAllocations)
    (void)hsa_memory_free(pointer);
  runtime.deviceAllocations.clear();
  runtime.managedAllocations.clear();
  runtime.peers.clear(static_cast<uint32_t>(currentDevice));
  for (auto &records : runtime.syncRecords) {
    for (auto *record : records)
      (void)hsa_memory_free(record);
    records.clear();
  }
  commands().reset();
  for (auto &module : runtime.driverModules)
    if (auto live = module.lock()) live->reset();
  runtime.kernels.clear();
  runtime.variables.clear();
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceComputeCapability(int *major, int *minor, hipDevice_t device) {
  if (!major || !minor || !validDevice(device)) return remember(hipErrorInvalidValue);
  const auto version = gemstone::hsa::targetVersion();
  *major = static_cast<int>(version.major);
  *minor = static_cast<int>(version.minor);
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGetName(char *name, int length, hipDevice_t device) {
  if (!name || length <= 0 || !validDevice(device)) return remember(hipErrorInvalidValue);
  const std::string value = std::string("Gemstone ") + gemstone::hsa::targetArch();
  std::strncpy(name, value.c_str(), static_cast<size_t>(length));
  name[length - 1] = '\0';
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceTotalMem(size_t *bytes, hipDevice_t device) {
  if (!bytes || !validDevice(device)) return remember(hipErrorInvalidValue);
  *bytes = DeviceTotalMemoryBytes;
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemGetInfo(size_t *free, size_t *total) {
  if (!free || !total) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  uint64_t available = 0;
  if (hsa_agent_get_info(gpuAgent(currentDevice), static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MEMORY_AVAIL), &available)
      != HSA_STATUS_SUCCESS)
    return remember(hipErrorInvalidDevice);
  *total = DeviceTotalMemoryBytes;
  *free = available;
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGetDefaultMemPool(hipMemPool_t *pool, int device) {
  if (!pool || !validDevice(device)) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  *pool = reinterpret_cast<hipMemPool_t>(uintptr_t(1));
  return remember(hipSuccess);
}

extern "C" hipError_t hipMallocAsync(void **pointer, size_t size, hipStream_t) { return hipMalloc(pointer, size); }
extern "C" hipError_t hipFreeAsync(void *pointer, hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipFree(pointer); }));
}
extern "C" hipError_t hipMemPoolTrimTo(hipMemPool_t pool, size_t) { return remember(!pool ? hipErrorInvalidValue : hipSuccess); }

extern "C" hipError_t hipDeviceGetAttribute(int *value, hipDeviceAttribute_t attribute, int device) {
  if (!environmentAccepted()) return remember(hipErrorNotInitialized);
  if (!value || !validDevice(device)) return remember(hipErrorInvalidValue);
  const auto model = gemstone::hsa::deviceModel();
  switch (attribute) {
    case hipDeviceAttributeMaxThreadsPerBlock:
    case hipDeviceAttributeMaxBlockDimX:
    case hipDeviceAttributeMaxBlockDimY:
    case hipDeviceAttributeMaxBlockDimZ: *value = MaxBlockThreads; break;
    case hipDeviceAttributePciBusId: *value = device; break;
    case hipDeviceAttributeWarpSize: *value = static_cast<int>(gemstone::hsa::waveSize()); break;
    case hipDeviceAttributeMaxGridDimX: *value = static_cast<int>(MaxGridDimension); break;
    case hipDeviceAttributeMaxGridDimY:
    case hipDeviceAttributeMaxGridDimZ: *value = static_cast<int>(MaxGridDimensionYZ); break;
    case hipDeviceAttributeClockRate: *value = static_cast<int>(model.clockMHz * 1000U); break;
    case hipDeviceAttributeWallClockRate: *value = static_cast<int>(gemstone::hsa::TimestampHz / 1000U); break;
    case hipDeviceAttributeMaxRegistersPerBlock:
    case hipDeviceAttributeMaxRegistersPerMultiprocessor: *value = static_cast<int>(model.registersPerProcessor()); break;
    case hipDeviceAttributeImageSupport: *value = model.imageInstructions ? 1 : 0; break;
    case hipDeviceAttributeMaxTexture2DGather: *value = model.imageGatherLod ? MaxTexture2DGatherDimension : 0; break;
    case hipDeviceAttributeMaxThreadsPerMultiProcessor: *value = static_cast<int>(model.threadsPerProcessor()); break;
    case hipDeviceAttributeMultiprocessorCount: *value = static_cast<int>(model.processors()); break;
    case hipDeviceAttributeMaxSharedMemoryPerMultiprocessor: *value = static_cast<int>(model.groupMemoryPerProcessor()); break;
    case hipDeviceAttributeTotalGlobalMem: *value = static_cast<int>(DeviceTotalMemoryBytes); break;
    case hipDeviceAttributeUnifiedAddressing:
    case hipDeviceAttributeCanMapHostMemory:
    case hipDeviceAttributeHostNativeAtomicSupported:
    case hipDeviceAttributeConcurrentManagedAccess:
    case hipDeviceAttributeManagedMemory:
    case hipDeviceAttributeCooperativeLaunch: *value = 1; break;
    case hipDeviceAttributePageableMemoryAccess:
      *value = gemstone::environment::enabled(gemstone::environment::SystemPagingOn) ? 1 : 0;
      break;
    default: *value = 0; break;
  }
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetDevicePropertiesR0600(hipDeviceProp_tR0600 *properties, int device) {
  if (!environmentAccepted()) return remember(hipErrorNotInitialized);
  if (!properties || !validDevice(device)) return remember(hipErrorInvalidValue);
  std::memset(properties, 0, sizeof(*properties));
  const auto model = gemstone::hsa::deviceModel();
  const std::string target = gemstone::hsa::targetArch();
  const std::string name = std::string("Gemstone ") + target;
  std::strncpy(properties->name, name.c_str(), sizeof(properties->name) - 1U);
  std::strncpy(properties->gcnArchName, target.c_str(), sizeof(properties->gcnArchName) - 1U);
  properties->totalGlobalMem = DeviceTotalMemoryBytes;
  properties->sharedMemPerBlock = DeviceSharedMemoryPerBlock;
  properties->regsPerBlock = static_cast<int>(model.registersPerProcessor());
  properties->warpSize = static_cast<int>(gemstone::hsa::waveSize());
  properties->maxThreadsPerBlock = MaxBlockThreads;
  properties->maxThreadsPerMultiProcessor = static_cast<int>(model.threadsPerProcessor());
  properties->maxSharedMemoryPerMultiProcessor = model.groupMemoryPerProcessor();
  properties->regsPerMultiprocessor = static_cast<int>(model.registersPerProcessor());
  properties->maxThreadsDim[0] = MaxBlockThreads;
  properties->maxThreadsDim[1] = MaxBlockThreads;
  properties->maxThreadsDim[2] = MaxBlockThreads;
  properties->maxGridSize[0] = MaxGridDimension;
  properties->maxGridSize[1] = MaxGridDimensionYZ;
  properties->maxGridSize[2] = MaxGridDimensionYZ;
  properties->clockRate = static_cast<int>(model.clockMHz * 1000U);
  const auto version = gemstone::hsa::targetVersion(target.c_str());
  properties->major = static_cast<int>(version.major);
  properties->minor = static_cast<int>(version.minor);
  properties->multiProcessorCount = static_cast<int>(model.processors());
  properties->pciBusID = device;
  properties->cooperativeLaunch = 1;
  properties->unifiedAddressing = 1;
  properties->canMapHostMemory = 1;
  properties->managedMemory = 1;
  properties->hostNativeAtomicSupported = 1;
  properties->concurrentManagedAccess = 1;
  properties->pageableMemoryAccess = gemstone::environment::enabled(gemstone::environment::SystemPagingOn) ? 1 : 0;
  return remember(hipSuccess);
}

// The unversioned entry point uses the older 792-byte hipDeviceProp_t ABI.
// Its field offsets differ from hipDeviceProp_tR0600, so forwarding directly
// to the versioned implementation would corrupt the caller's properties. The
// pinned headers alias hipDeviceProp_t to hipDeviceProp_tR0600, so the legacy
// layout is reproduced here by explicit offset and pinned by the tests.
namespace legacy_properties {
constexpr size_t Name = 0;
constexpr size_t NameSize = 256;
constexpr size_t TotalGlobalMem = 256;
constexpr size_t SharedMemPerBlock = 264;
constexpr size_t WarpSize = 276;
constexpr size_t MaxThreadsPerBlock = 280;
constexpr size_t MaxThreadsDim = 284;
constexpr size_t MaxGridSize = 296;
constexpr size_t Major = 328;
constexpr size_t Minor = 332;
constexpr size_t MultiProcessorCount = 336;
constexpr size_t Arch = 356;
constexpr size_t MaxSharedMemoryPerMultiProcessor = 376;
constexpr size_t GcnArch = 392;
constexpr size_t ArchName = 396;
constexpr size_t CooperativeLaunch = 656;
constexpr size_t ManagedMemory = 772;
constexpr size_t ConcurrentManagedAccess = 780;
constexpr size_t PageableMemoryAccess = 784;
constexpr size_t Size = 792;
static_assert(Name + NameSize <= TotalGlobalMem, "legacy name overlaps totalGlobalMem");
static_assert(MaxGridSize + (3 * sizeof(int)) <= Major, "legacy layout overlap");
static_assert(MaxSharedMemoryPerMultiProcessor + sizeof(size_t) <= GcnArch, "legacy layout overlap");
static_assert(ArchName + 256 <= CooperativeLaunch, "legacy layout overlap");
static_assert(PageableMemoryAccess + sizeof(int) <= Size, "legacy layout out of bounds");
} // namespace legacy_properties

#undef hipGetDeviceProperties
extern "C" GEMSTONE_PUBLIC_API hipError_t hipGetDeviceProperties(void *properties, int device) {
  using namespace legacy_properties;
  if (!environmentAccepted()) return remember(hipErrorNotInitialized);
  if (!properties || !validDevice(device)) return remember(hipErrorInvalidValue);
  if (sizeof(size_t) != 8) return remember(hipErrorNotSupported);
  auto *bytes = static_cast<unsigned char *>(properties);
  std::memset(bytes, 0, Size);
  const auto model = gemstone::hsa::deviceModel();
  const std::string target = gemstone::hsa::targetArch();
  const std::string name = std::string("Gemstone ") + target;
  std::strncpy(reinterpret_cast<char *>(bytes + Name), name.c_str(), NameSize - 1);
  std::strncpy(reinterpret_cast<char *>(bytes + ArchName), target.c_str(), 255);
  const auto write = [bytes](size_t offset, auto value) { std::memcpy(bytes + offset, &value, sizeof(value)); };
  write(TotalGlobalMem, DeviceTotalMemoryBytes);
  write(SharedMemPerBlock, DeviceSharedMemoryPerBlock);
  write(WarpSize, static_cast<int>(gemstone::hsa::waveSize()));
  write(MaxThreadsPerBlock, int(MaxBlockThreads));
  for (size_t axis = 0; axis < 3; ++axis) {
    write(MaxThreadsDim + (axis * sizeof(int)), int(MaxBlockThreads));
    write(MaxGridSize + (axis * sizeof(int)), int(axis == 0 ? MaxGridDimension : MaxGridDimensionYZ));
  }
  const auto version = gemstone::hsa::targetVersion(target.c_str());
  write(Major, int(version.major));
  write(Minor, int(version.minor));
  write(MultiProcessorCount, int(model.processors()));
  write(Arch, uint32_t((1U << 5) | (1U << 7))); // global int64 atomics and doubles
  write(MaxSharedMemoryPerMultiProcessor, model.groupMemoryPerProcessor());
  int gcnArch = 0;
  if (target.size() > 3) (void)std::from_chars(target.data() + 3, target.data() + target.size(), gcnArch);
  write(GcnArch, gcnArch);
  write(CooperativeLaunch, 1);
  write(ManagedMemory, 1);
  write(ConcurrentManagedAccess, 1);
  write(PageableMemoryAccess, (gemstone::environment::enabled(gemstone::environment::SystemPagingOn) ? 1 : 0));
  return remember(hipSuccess);
}

extern "C" hipError_t hipMalloc(void **pointer, size_t size) {
  if (!pointer) return remember(hipErrorInvalidValue);
  if (size == 0) {
    *pointer = nullptr;
    return remember(hipSuccess);
  }
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  const int device = currentDevice;
  const hsa_status_t status = hsa_memory_allocate(gpuRegion(device), size, pointer);
  if (traceHip("allocations"))
    std::fprintf(stderr, "Gemstone HIP malloc size=%zu status=%d pointer=%p\n", size, int(status), !pointer ? nullptr : *pointer);
  if (status == HSA_STATUS_SUCCESS) {
    std::scoped_lock const guard(state().lock);
    state().deviceAllocations.emplace(*pointer, DeviceAllocation{.size = size, .device = device});
    for (int peer = 0; validDevice(peer); ++peer)
      if (peer != device && state().peers.enabled(static_cast<uint32_t>(peer), static_cast<uint32_t>(device))) {
        const hsa_agent_t agent = gpuAgent(peer);
        (void)hsa_amd_agents_allow_access(1, &agent, nullptr, *pointer);
      }
  }
  return remember(mapHsa(status));
}

extern "C" hipError_t hipFree(void *pointer) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!pointer) return remember(hipSuccess);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  const hsa_status_t status = hsa_memory_free(pointer);
  if (status == HSA_STATUS_SUCCESS) {
    std::scoped_lock const guard(state().lock);
    state().managedAllocations.erase(pointer);
    state().deviceAllocations.erase(pointer);
  }
  return remember(mapHsa(status));
}

extern "C" hipError_t hipMemcpy(void *destination, const void *source, size_t size, hipMemcpyKind kind) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (size == 0) return remember(hipSuccess);
  if (!destination || !source) return remember(hipErrorInvalidValue);
  if (kind == hipMemcpyHostToHost) {
    std::memmove(destination, source, size);
    return remember(hipSuccess);
  }
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  const hsa_status_t status = hsa_memory_copy(destination, source, size);
  return remember(mapHsa(status));
}

static hipError_t copySymbol(const void *symbol, const void *source, size_t size, size_t offset) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if ((!symbol || !source) && size != 0) return remember(hipErrorInvalidValue);
  std::shared_ptr<Module> module;
  std::string deviceName;
  {
    std::scoped_lock const guard(state().lock);
    const auto found = state().variables.find(symbol);
    if (found == state().variables.end() || offset > found->second.size || size > found->second.size - offset)
      return remember(hipErrorInvalidSymbol);
    if (size != 0) std::memcpy(static_cast<unsigned char *>(found->second.host) + offset, source, size);
    module = found->second.module;
    deviceName = found->second.deviceName;
  }
  if (!ensureModuleLoaded(module, currentDevice)) return remember(hipErrorInvalidImage);
  hsa_executable_t executable{};
  {
    std::scoped_lock const guard(module->lock);
    executable = module->instances[static_cast<size_t>(currentDevice)].executable;
  }
  if (executable.handle == 0) return remember(hipErrorInvalidSymbol);
  const hsa_status_t status = gemstone_hsa_executable_write_global(executable, deviceName.c_str(), source, size, offset);
  return remember(mapHsa(status));
}

static hipError_t deviceVariable(const void *symbol, void **address, size_t *size) {
  std::shared_ptr<Module> module;
  std::string deviceName;
  {
    std::scoped_lock const guard(state().lock);
    const auto found = state().variables.find(symbol);
    if (found == state().variables.end()) return hipErrorInvalidSymbol;
    module = found->second.module;
    deviceName = found->second.deviceName;
    *size = found->second.size;
  }
  if (!ensureInitialised()) return hipErrorNoDevice;
  if (!ensureModuleLoaded(module, currentDevice)) return hipErrorInvalidImage;
  hsa_executable_t executable{};
  {
    std::scoped_lock const guard(module->lock);
    executable = module->instances[static_cast<size_t>(currentDevice)].executable;
  }
  hsa_executable_symbol_t variable{};
  const hsa_agent_t agent = gpuAgent(currentDevice);
  if (hsa_executable_get_symbol_by_name(executable, deviceName.c_str(), &agent, &variable) != HSA_STATUS_SUCCESS)
    return hipErrorInvalidSymbol;
  uint64_t value = 0;
  if (hsa_executable_symbol_get_info(variable, HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_ADDRESS, &value) != HSA_STATUS_SUCCESS)
    return hipErrorInvalidSymbol;
  *address = reinterpret_cast<void *>(value);
  return hipSuccess;
}

static hipError_t symbolSource(const void *symbol, size_t size, size_t offset, const void *&source) {
  if (!symbol && size != 0) return hipErrorInvalidValue;
  void *address = nullptr;
  size_t variableSize = 0;
  if (const auto error = deviceVariable(symbol, &address, &variableSize); error != hipSuccess) return error;
  if (offset > variableSize || size > variableSize - offset) return hipErrorInvalidSymbol;
  source = static_cast<const unsigned char *>(address) + offset;
  return hipSuccess;
}

extern "C" hipError_t hipGetSymbolAddress(void **address, const void *symbol) {
  if (!address || !symbol) return remember(hipErrorInvalidValue);
  void *device = nullptr;
  size_t size = 0;
  if (const auto error = deviceVariable(symbol, &device, &size); error != hipSuccess) return remember(error);
  *address = device;
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetSymbolSize(size_t *size, const void *symbol) {
  if (!size || !symbol) return remember(hipErrorInvalidValue);
  std::scoped_lock const guard(state().lock);
  const auto found = state().variables.find(symbol);
  if (found == state().variables.end()) return remember(hipErrorInvalidSymbol);
  *size = found->second.size;
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemcpyToSymbol(const void *symbol, const void *source, size_t size, size_t offset, hipMemcpyKind) {
  return copySymbol(symbol, source, size, offset);
}

extern "C" hipError_t hipMemcpyToSymbolAsync(const void *symbol, const void *source, size_t size, size_t offset, hipMemcpyKind kind,
                                             hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipMemcpyToSymbol(symbol, source, size, offset, kind); }));
}

extern "C" hipError_t hipMemcpyFromSymbol(void *destination, const void *symbol, size_t size, size_t offset, hipMemcpyKind kind) {
  const void *source = nullptr;
  if (const auto error = symbolSource(symbol, size, offset, source); error != hipSuccess) return remember(error);
  return hipMemcpy(destination, source, size, kind);
}

extern "C" hipError_t hipMemcpyFromSymbolAsync(void *destination, const void *symbol, size_t size, size_t offset, hipMemcpyKind kind,
                                               hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipMemcpyFromSymbol(destination, symbol, size, offset, kind); }));
}

extern "C" hipError_t hipMemcpyAsync(void *destination, const void *source, size_t size, hipMemcpyKind kind, hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipMemcpy(destination, source, size, kind); }));
}
namespace {

/** `slices` slices of `rows` rows of `width` bytes between pitched layouts, as one copy rather than one per row. */
hipError_t copyPitched(void *destination, size_t destinationPitch, size_t destinationSlicePitch, const void *source, size_t sourcePitch,
                       size_t sourceSlicePitch, size_t width, size_t rows, size_t slices, hipMemcpyKind kind) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return pending;
  if (width == 0 || rows == 0 || slices == 0) return hipSuccess;
  if (!destination || !source) return hipErrorInvalidValue;
  if (kind == hipMemcpyHostToHost) {
    for (size_t z = 0; z < slices; ++z)
      for (size_t y = 0; y < rows; ++y)
        std::memmove(static_cast<unsigned char *>(destination) + (z * destinationSlicePitch) + (y * destinationPitch),
                     static_cast<const unsigned char *>(source) + (z * sourceSlicePitch) + (y * sourcePitch), width);
    return hipSuccess;
  }
  if (!ensureInitialised()) return hipErrorNoDevice;
  return mapHsa(gemstone_hsa_memory_copy_pitched(destination, destinationPitch, destinationSlicePitch, source, sourcePitch,
                                                 sourceSlicePitch, width, rows, slices));
}

} // namespace

extern "C" hipError_t hipMemcpy2D(void *destination, size_t destinationPitch, const void *source, size_t sourcePitch, size_t width,
                                  size_t height, hipMemcpyKind kind) {
  return remember(copyPitched(destination, destinationPitch, 0, source, sourcePitch, 0, width, height, 1, kind));
}
extern "C" hipError_t hipMemcpy2DAsync(void *destination, size_t destinationPitch, const void *source, size_t sourcePitch, size_t width,
                                       size_t height, hipMemcpyKind kind, hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(
      enqueueCommand(stream, [=] { return hipMemcpy2D(destination, destinationPitch, source, sourcePitch, width, height, kind); }));
}
extern "C" hipError_t hipMemcpyWithStream(void *destination, const void *source, size_t size, hipMemcpyKind kind, hipStream_t stream) {
  return validStream(stream) ? hipMemcpy(destination, source, size, kind) : remember(hipErrorInvalidResourceHandle);
}
extern "C" hipError_t hipMemcpyHtoD(hipDeviceptr_t destination, const void *source, size_t size) {
  return hipMemcpy(reinterpret_cast<void *>(destination), source, size, hipMemcpyHostToDevice);
}
extern "C" hipError_t hipMemcpyDtoH(void *destination, hipDeviceptr_t source, size_t size) {
  return hipMemcpy(destination, reinterpret_cast<const void *>(source), size, hipMemcpyDeviceToHost);
}
extern "C" hipError_t hipMemcpyDtoD(hipDeviceptr_t destination, hipDeviceptr_t source, size_t size) {
  return hipMemcpy(reinterpret_cast<void *>(destination), reinterpret_cast<const void *>(source), size, hipMemcpyDeviceToDevice);
}
extern "C" hipError_t hipMemcpyHtoDAsync(hipDeviceptr_t destination, const void *source, size_t size, hipStream_t stream) {
  return validStream(stream) ? hipMemcpyHtoD(destination, source, size) : remember(hipErrorInvalidResourceHandle);
}
extern "C" hipError_t hipMemcpyDtoHAsync(void *destination, hipDeviceptr_t source, size_t size, hipStream_t stream) {
  return validStream(stream) ? hipMemcpyDtoH(destination, source, size) : remember(hipErrorInvalidResourceHandle);
}
extern "C" hipError_t hipMemcpyDtoDAsync(hipDeviceptr_t destination, hipDeviceptr_t source, size_t size, hipStream_t stream) {
  return validStream(stream) ? hipMemcpyDtoD(destination, source, size) : remember(hipErrorInvalidResourceHandle);
}

extern "C" hipError_t hipMallocManaged(void **pointer, size_t size, unsigned int) {
  if (!pointer) return remember(hipErrorInvalidValue);
  *pointer = nullptr;
  if (size == 0) return remember(hipSuccess);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  hsa_agent_t cpu{};
  hsa_region_t region{};
  if (hsa_iterate_agents(findAgent<HSA_DEVICE_TYPE_CPU>, &cpu) != HSA_STATUS_INFO_BREAK
      || hsa_agent_iterate_regions(cpu, findRegion<HSA_REGION_GLOBAL_FLAG_FINE_GRAINED>, &region) != HSA_STATUS_INFO_BREAK)
    return remember(hipErrorNoDevice);
  const auto status = hsa_memory_allocate(region, size, pointer);
  if (status != HSA_STATUS_SUCCESS) return remember(mapHsa(status));
  try {
    std::scoped_lock const guard(state().lock);
    state().deviceAllocations.emplace(*pointer, DeviceAllocation{.size = size, .device = currentDevice});
    state().managedAllocations.emplace(*pointer, size);
  } catch (...) {
    (void)hipFree(*pointer);
    *pointer = nullptr;
    return remember(hipErrorOutOfMemory);
  }
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemAdvise(const void *pointer, size_t size, hipMemoryAdvise advice, int device) {
  if ((!pointer && size != 0) || !validDevice(device)) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);

  // HIP's StdPar allocator obtains ordinary host pages and then marks them
  // as device-accessible with SetAccessedBy.  The emulator must register
  // those pages with the HSA facade before an AQL dispatch can resolve the
  // pointer.  The matching unset advice is issued before the allocator
  // returns the pages to libc; deregister it first so the facade never keeps
  // a dangling host allocation.
  switch (advice) {
    case hipMemAdviseSetAccessedBy: {
      if (!pointer || size == 0) return remember(hipErrorInvalidValue);
      const hsa_status_t status = hsa_memory_register(gemstone::apiAddress(pointer), size);
      return remember(status == HSA_STATUS_ERROR_INVALID_ALLOCATION ? hipSuccess : mapHsa(status));
    }
    case hipMemAdviseUnsetAccessedBy: {
      if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
      if (!pointer) return remember(hipErrorInvalidValue);
      const hsa_status_t status = hsa_memory_deregister(gemstone::apiAddress(pointer), 0);
      return remember(status == HSA_STATUS_ERROR_INVALID_ALLOCATION ? hipSuccess : mapHsa(status));
    }
    default: return remember(hipSuccess);
  }
}
extern "C" hipError_t hipMallocHost(void **pointer, size_t size) { return allocateHost(pointer, size); }
extern "C" hipError_t hipHostMalloc(void **pointer, size_t size, unsigned int flags) { return allocateHost(pointer, size, flags); }
extern "C" hipError_t hipFreeHost(void *pointer) { return releaseHost(pointer); }
extern "C" hipError_t hipHostFree(void *pointer) { return hipFreeHost(pointer); }

extern "C" hipError_t hipHostRegister(void *pointer, size_t size, unsigned int flags) {
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  const gemstone::MemoryRange range{.base = reinterpret_cast<uintptr_t>(pointer), .size = size};
  if (!range.valid() || (flags & ~(hipHostRegisterPortable | hipHostRegisterMapped | hipHostRegisterIoMemory | hipHostRegisterReadOnly)))
    return remember(hipErrorInvalidValue);
  std::scoped_lock const guard(state().lock);
  if (state().hostRegistrations.overlaps(range)) return remember(hipErrorHostMemoryAlreadyRegistered);
  const auto status = hsa_memory_register(pointer, size);
  if (status == HSA_STATUS_SUCCESS) state().hostRegistrations.add(range, gemstone::MemoryOwnership::Borrowed, flags);
  return remember(mapHsa(status));
}
extern "C" hipError_t hipHostUnregister(void *pointer) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  std::scoped_lock const guard(state().lock);
  const auto *const found = state().hostRegistrations.exact(reinterpret_cast<uintptr_t>(pointer));
  if (!found || found->ownership != gemstone::MemoryOwnership::Borrowed) return remember(hipErrorHostMemoryNotRegistered);
  const auto status = hsa_memory_deregister(pointer, found->range.size);
  if (status == HSA_STATUS_SUCCESS)
    state().hostRegistrations.remove(reinterpret_cast<uintptr_t>(pointer), gemstone::MemoryOwnership::Borrowed);
  return remember(mapHsa(status));
}
extern "C" hipError_t hipHostGetDevicePointer(void **device, void *host, unsigned flags) {
  if (!device || !host || flags != 0) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  std::scoped_lock const guard(state().lock);
  if (!state().hostRegistrations.find(reinterpret_cast<uintptr_t>(host))) return remember(hipErrorInvalidValue);
  *device = host;
  return remember(hipSuccess);
}
extern "C" hipError_t hipHostGetFlags(unsigned *flags, void *host) {
  if (!flags || !host) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  std::scoped_lock const guard(state().lock);
  const auto *const found = state().hostRegistrations.find(reinterpret_cast<uintptr_t>(host));
  if (!found) return remember(hipErrorInvalidValue);
  *flags = static_cast<unsigned>(found->flags);
  return remember(hipSuccess);
}

extern "C" hipError_t hipPointerGetAttributes(hipPointerAttribute_t *attributes, const void *pointer) {
  if (!attributes || !pointer) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  hsa_amd_pointer_info_t info{};
  info.size = sizeof(info);
  const auto status = hsa_amd_pointer_info(pointer, &info, nullptr, nullptr, nullptr);
  if (status != HSA_STATUS_SUCCESS) return remember(mapHsa(status));
  if (info.type == HSA_EXT_POINTER_TYPE_UNKNOWN) return remember(hipErrorInvalidValue);
  hsa_device_type_t owner{};
  if (hsa_agent_get_info(info.agentOwner, HSA_AGENT_INFO_DEVICE, &owner) != HSA_STATUS_SUCCESS) return remember(hipErrorInvalidDevice);
  std::memset(attributes, 0, sizeof(*attributes));
  attributes->type = owner == HSA_DEVICE_TYPE_CPU ? hipMemoryTypeHost : hipMemoryTypeDevice;
  attributes->device = owner == HSA_DEVICE_TYPE_CPU ? currentDevice : deviceOfAgent(info.agentOwner);
  attributes->devicePointer = gemstone::apiAddress(pointer);
  attributes->hostPointer = owner == HSA_DEVICE_TYPE_CPU ? gemstone::apiAddress(pointer) : nullptr;
  {
    std::scoped_lock const guard(state().lock);
    const auto address = reinterpret_cast<uintptr_t>(pointer);
    const auto managed = state().managedAllocations ^ exists([&](void *base, size_t size) {
                           const auto begin = reinterpret_cast<uintptr_t>(base);
                           return address >= begin && address - begin < size;
                         });
    if (managed) {
      attributes->isManaged = 1;
      attributes->type = hipMemoryTypeManaged;
      attributes->hostPointer = gemstone::apiAddress(pointer);
    }
  }
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemset(void *destination, int value, size_t size) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!destination && size != 0) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  const unsigned char byte = static_cast<unsigned char>(value);
  if (size % 4U == 0) return remember(mapHsa(hsa_amd_memory_fill(destination, uint32_t(byte) * 0x01010101U, size / 4U)));
  std::vector<unsigned char> bytes(size, byte);
  return hipMemcpy(destination, bytes.data(), size, hipMemcpyHostToDevice);
}
extern "C" hipError_t hipMemsetD8(hipDeviceptr_t destination, unsigned char value, size_t count) {
  return hipMemset(reinterpret_cast<void *>(destination), value, count);
}
extern "C" hipError_t hipMemsetD32(hipDeviceptr_t destination, int value, size_t count) {
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  return remember(mapHsa(hsa_amd_memory_fill(reinterpret_cast<void *>(destination), static_cast<uint32_t>(value), count)));
}
extern "C" hipError_t hipMemsetAsync(void *destination, int value, size_t size, hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipMemset(destination, value, size); }));
}

extern "C" GEMSTONE_PUBLIC_API hipError_t __hipPushCallConfiguration(dim3 grid, dim3 block, size_t shared, hipStream_t stream) {
  launchConfiguration = LaunchConfiguration{.grid = grid, .block = block, .shared = shared, .stream = stream};
  return remember(hipSuccess);
}
extern "C" GEMSTONE_PUBLIC_API hipError_t __hipPopCallConfiguration(dim3 *grid, dim3 *block, size_t *shared, hipStream_t *stream) {
  if (!grid || !block || !shared || !stream) return remember(hipErrorInvalidValue);
  *grid = launchConfiguration.grid;
  *block = launchConfiguration.block;
  *shared = launchConfiguration.shared;
  *stream = launchConfiguration.stream;
  return remember(hipSuccess);
}
extern "C" hipError_t hipLaunchKernel(const void *functionAddress, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t stream) {
  return dispatch(functionAddress, grid, block, args, shared, stream);
}
extern "C" hipError_t hipLaunchCooperativeKernel(const void *functionAddress, dim3 grid, dim3 block, void **args, unsigned int shared,
                                                 hipStream_t stream) {
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  return dispatchCooperative(resolveKernel(functionAddress, streamDevice(stream)), grid, block, args, shared, stream);
}
extern "C" hipError_t hipOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(int *blocks, const void *functionAddress, int blockSize,
                                                                            size_t shared, unsigned int) {
  if (!blocks || !functionAddress || blockSize <= 0) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  if (!resolveKernel(functionAddress, currentDevice)) return remember(hipErrorInvalidDeviceFunction);
  *blocks = residentBlocks(static_cast<uint64_t>(blockSize), shared);
  return remember(hipSuccess);
}
extern "C" hipError_t hipOccupancyMaxActiveBlocksPerMultiprocessor(int *blocks, const void *functionAddress, int blockSize, size_t shared) {
  return hipOccupancyMaxActiveBlocksPerMultiprocessorWithFlags(blocks, functionAddress, blockSize, shared, 0);
}

extern "C" hipError_t hipOccupancyMaxPotentialBlockSize(int *minGridSize, int *blockSize, const void *functionAddress, size_t shared,
                                                        int blockSizeLimit) {
  if (!minGridSize || !blockSize || !functionAddress) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  if (!resolveKernel(functionAddress, currentDevice)) return remember(hipErrorInvalidDeviceFunction);
  const uint32_t limit = blockSizeLimit > 0 ? std::min<uint32_t>(static_cast<uint32_t>(blockSizeLimit), MaxBlockThreads) : MaxBlockThreads;
  uint64_t bestResident = 0;
  uint32_t best = 0;
  for (uint32_t threads = limit; threads >= 1; --threads) {
    const uint64_t resident = static_cast<uint64_t>(residentBlocks(threads, shared)) * threads;
    if (resident > bestResident) {
      bestResident = resident;
      best = threads;
    }
  }
  if (best == 0) return remember(hipErrorLaunchOutOfResources);
  *blockSize = static_cast<int>(best);
  *minGridSize = residentBlocks(best, shared) * static_cast<int>(gemstone::hsa::deviceModel().processors());
  return remember(hipSuccess);
}

extern "C" hipError_t hipFuncGetAttributes(struct hipFuncAttributes *attributes, const void *functionAddress) {
  if (!attributes || !functionAddress) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  if (!resolveKernel(functionAddress, currentDevice)) return remember(hipErrorInvalidDeviceFunction);
  std::memset(attributes, 0, sizeof(*attributes));
  attributes->binaryVersion = 900;
  attributes->maxThreadsPerBlock = MaxBlockThreads;
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetLastError() {
  const hipError_t result = lastError;
  lastError = hipSuccess;
  return result;
}
extern "C" hipError_t hipExtGetLastError() { return hipGetLastError(); }
extern "C" hipError_t hipPeekAtLastError() { return lastError; }
extern "C" const char *hipGetErrorName(hipError_t error) {
  switch (error) {
    case hipSuccess: return "hipSuccess";
    case hipErrorNoDevice: return "hipErrorNoDevice";
    case hipErrorInvalidValue: return "hipErrorInvalidValue";
    case hipErrorInvalidDeviceFunction: return "hipErrorInvalidDeviceFunction";
    case hipErrorLaunchFailure: return "hipErrorLaunchFailure";
    case hipErrorInvalidConfiguration: return "hipErrorInvalidConfiguration";
    case hipErrorInvalidImage: return "hipErrorInvalidImage";
    case hipErrorIllegalAddress: return "hipErrorIllegalAddress";
    case hipErrorLaunchOutOfResources: return "hipErrorLaunchOutOfResources";
    case hipErrorCooperativeLaunchTooLarge: return "hipErrorCooperativeLaunchTooLarge";
    case hipErrorNotInitialized: return "hipErrorNotInitialized";
    case hipErrorOutOfMemory: return "hipErrorOutOfMemory";
    case hipErrorInvalidContext: return "hipErrorInvalidContext";
    case hipErrorInvalidDevice: return "hipErrorInvalidDevice";
    case hipErrorInvalidDevicePointer: return "hipErrorInvalidDevicePointer";
    case hipErrorInvalidSymbol: return "hipErrorInvalidSymbol";
    case hipErrorFileNotFound: return "hipErrorFileNotFound";
    case hipErrorInvalidHandle: return "hipErrorInvalidHandle";
    case hipErrorNotFound: return "hipErrorNotFound";
    case hipErrorNotReady: return "hipErrorNotReady";
    case hipErrorHostMemoryAlreadyRegistered: return "hipErrorHostMemoryAlreadyRegistered";
    case hipErrorHostMemoryNotRegistered: return "hipErrorHostMemoryNotRegistered";
    case hipErrorNotSupported: return "hipErrorNotSupported";
    case hipErrorStreamCaptureUnsupported: return "hipErrorStreamCaptureUnsupported";
    case hipErrorStreamCaptureIsolation: return "hipErrorStreamCaptureIsolation";
    case hipErrorStreamCaptureUnmatched: return "hipErrorStreamCaptureUnmatched";
    case hipErrorGraphExecUpdateFailure: return "hipErrorGraphExecUpdateFailure";
    default: return "hipErrorUnknown";
  }
}
extern "C" const char *hipGetErrorString(hipError_t error) { return hipGetErrorName(error); }

extern "C" hipError_t hipStreamCreateWithFlags(hipStream_t *stream, unsigned int flags) {
  if (!stream) return remember(hipErrorInvalidValue);
  auto *const created = static_cast<hipStream_t>(commands().createStream((flags & hipStreamNonBlocking) != 0));
  {
    std::scoped_lock const guard(state().lock);
    state().streamDevices.emplace(created, currentDevice);
  }
  *stream = created;
  return remember(hipSuccess);
}
extern "C" hipError_t hipStreamCreate(hipStream_t *stream) { return hipStreamCreateWithFlags(stream, 0); }
extern "C" hipError_t hipStreamCreateWithPriority(hipStream_t *stream, unsigned int, int) { return hipStreamCreate(stream); }
extern "C" hipError_t hipStreamDestroy(hipStream_t stream) {
  if (const auto pending = static_cast<hipError_t>(commands().wait(commands().snapshot(stream))); pending != hipSuccess)
    return remember(pending);
  if (stream && !sentinelStream(stream)) {
    if (!commands().destroyStream(stream)) return remember(hipErrorInvalidResourceHandle);
    std::scoped_lock const guard(state().lock);
    state().streamDevices.erase(stream);
  }
  return remember(hipSuccess);
}
extern "C" hipError_t hipStreamSynchronize(hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(static_cast<hipError_t>(commands().wait(commands().snapshot(stream))));
}
extern "C" hipError_t hipStreamQuery(hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(static_cast<hipError_t>(commands().query(commands().snapshot(stream), hipErrorNotReady)));
}
extern "C" hipError_t hipStreamAddCallback(hipStream_t stream, hipStreamCallback_t callback, void *data, unsigned int flags) {
  if (!callback || flags != 0) return remember(hipErrorInvalidValue);
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] {
    callback(stream, hipSuccess, data);
    return hipSuccess;
  }));
}
extern "C" hipError_t hipStreamWaitEvent(hipStream_t stream, hipEvent_t event, unsigned int) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  if (const auto captured = captures.waitEvent(stream, event)) return remember(graphResult(*captured));
  return remember(commands().waitEvent(stream, event) ? hipSuccess : hipErrorInvalidResourceHandle);
}
extern "C" hipError_t hipStreamGetDevice(hipStream_t stream, hipDevice_t *device) {
  if (!device) return remember(hipErrorInvalidValue);
  if (sentinelStream(stream) || !validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  *device = streamDevice(stream);
  return remember(hipSuccess);
}
extern "C" int hipGetStreamDeviceId(hipStream_t stream) { return stream && !sentinelStream(stream) ? streamDevice(stream) : currentDevice; }

namespace {

using gemstone::graph::Kind;
using gemstone::graph::Outcome;

gemstone::graph::Graph *graphOf(hipGraph_t graph) { return reinterpret_cast<gemstone::graph::Graph *>(graph); }

gemstone::graph::Exec *execOf(hipGraphExec_t exec) { return reinterpret_cast<gemstone::graph::Exec *>(exec); }

hipError_t addNode(hipGraph_t graph, hipGraphNode_t *node, Kind kind, const hipGraphNode_t *dependencies, size_t count,
                   gemstone::graph::Work work) {
  return graphResult(gemstone::graph::add(graphOf(graph), node, kind, dependencies, count, std::move(work)));
}

hipError_t recordKernel(const hipKernelNodeParams &params, gemstone::graph::Work &work) {
  if (!params.func) return hipErrorInvalidDeviceFunction;
  LaunchBuffer launchBuffer;
  if (!parseLaunchBuffer(params.extra, launchBuffer)) return hipErrorInvalidValue;
  gemstone::graph::Recording const recording(work);
  return dispatchKernel(resolveKernel(params.func, currentDevice), params.gridDim, params.blockDim, params.kernelParams,
                        params.sharedMemBytes, nullptr, launchBuffer);
}

hipError_t memsetRows(void *destination, size_t pitch, unsigned element, unsigned value, size_t width, size_t height) {
  if (width == 0 || height == 0) return hipSuccess;
  if (!destination || (height > 1 && pitch < width * element)) return hipErrorInvalidValue;
  std::vector<unsigned char> row(width * element);
  for (size_t index = 0; index < width; ++index)
    std::memcpy(row.data() + (index * element), &value, element);
  for (size_t y = 0; y < height; ++y)
    if (const auto status =
            hipMemcpy(static_cast<unsigned char *>(destination) + (y * pitch), row.data(), row.size(), hipMemcpyHostToDevice);
        status != hipSuccess)
      return status;
  return hipSuccess;
}

hipError_t captureInfo(hipStream_t stream, hipStreamCaptureStatus *status, unsigned long long *id, hipGraph_t *graph,
                       const hipGraphNode_t **dependencies, size_t *count) {
  if (!status) return hipErrorInvalidValue;
  const auto capture = captures.info(stream);
  if (!capture) {
    *status = hipStreamCaptureStatusNone;
    return hipSuccess;
  }
  *status = hipStreamCaptureStatusActive;
  if (id) *id = capture->id;
  if (graph) *graph = reinterpret_cast<hipGraph_t>(capture->graph);
  if (dependencies || count) {
    thread_local std::vector<hipGraphNode_t> frontier;
    frontier.clear();
    for (auto *node : capture->frontier)
      frontier.push_back(reinterpret_cast<hipGraphNode_t>(node));
    if (dependencies) *dependencies = frontier.data();
    if (count) *count = frontier.size();
  }
  return hipSuccess;
}

} // namespace

extern "C" hipError_t hipStreamBeginCapture(hipStream_t stream, hipStreamCaptureMode mode) {
  if (!stream || sentinelStream(stream)) return remember(hipErrorStreamCaptureUnsupported);
  if (mode != hipStreamCaptureModeGlobal && mode != hipStreamCaptureModeThreadLocal && mode != hipStreamCaptureModeRelaxed)
    return remember(hipErrorInvalidValue);
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(graphResult(captures.begin(stream)));
}
extern "C" hipError_t hipStreamEndCapture(hipStream_t stream, hipGraph_t *graph) {
  if (!graph) return remember(hipErrorInvalidValue);
  std::unique_ptr<gemstone::graph::Graph> captured;
  if (const auto outcome = captures.end(stream, captured); outcome != Outcome::Ok) return remember(graphResult(outcome));
  *graph = reinterpret_cast<hipGraph_t>(captured.release());
  return remember(hipSuccess);
}
extern "C" hipError_t hipStreamIsCapturing(hipStream_t stream, hipStreamCaptureStatus *status) {
  return remember(captureInfo(stream, status, nullptr, nullptr, nullptr, nullptr));
}
extern "C" hipError_t hipStreamGetCaptureInfo(hipStream_t stream, hipStreamCaptureStatus *status, unsigned long long *id) {
  return remember(captureInfo(stream, status, id, nullptr, nullptr, nullptr));
}
extern "C" hipError_t hipStreamGetCaptureInfo_v2(hipStream_t stream, hipStreamCaptureStatus *status, unsigned long long *id,
                                                 hipGraph_t *graph, const hipGraphNode_t **dependencies, size_t *count) {
  return remember(captureInfo(stream, status, id, graph, dependencies, count));
}
extern "C" hipError_t hipLaunchHostFunc(hipStream_t stream, hipHostFn_t fn, void *userData) {
  if (!fn) return remember(hipErrorInvalidValue);
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] {
    fn(userData);
    return hipSuccess;
  }));
}

extern "C" hipError_t hipGraphCreate(hipGraph_t *graph, unsigned int flags) {
  if (!graph || flags != 0) return remember(hipErrorInvalidValue);
  *graph = reinterpret_cast<hipGraph_t>(std::make_unique<gemstone::graph::Graph>().release());
  return remember(hipSuccess);
}
extern "C" hipError_t hipGraphDestroy(hipGraph_t graph) {
  delete graphOf(graph);
  return remember(hipSuccess);
}
extern "C" hipError_t hipGraphClone(hipGraph_t *clone, hipGraph_t original) {
  if (!clone || !original) return remember(hipErrorInvalidValue);
  *clone = reinterpret_cast<hipGraph_t>(std::make_unique<gemstone::graph::Graph>(graphOf(original)->clone()).release());
  return remember(hipSuccess);
}
extern "C" hipError_t hipGraphAddKernelNode(hipGraphNode_t *node, hipGraph_t graph, const hipGraphNode_t *dependencies, size_t count,
                                            const hipKernelNodeParams *params) {
  if (!params) return remember(hipErrorInvalidValue);
  gemstone::graph::Work work;
  if (const auto status = recordKernel(*params, work); status != hipSuccess) return remember(status);
  return remember(addNode(graph, node, Kind::Kernel, dependencies, count, std::move(work)));
}
extern "C" hipError_t hipGraphAddMemcpyNode(hipGraphNode_t *node, hipGraph_t graph, const hipGraphNode_t *dependencies, size_t count,
                                            const hipMemcpy3DParms *params) {
  if (!params) return remember(hipErrorInvalidValue);
  return remember(addNode(graph, node, Kind::Memcpy, dependencies, count, [copy = *params] { return hipMemcpy3D(&copy); }));
}
extern "C" hipError_t hipGraphAddMemcpyNode1D(hipGraphNode_t *node, hipGraph_t graph, const hipGraphNode_t *dependencies, size_t count,
                                              void *destination, const void *source, size_t size, hipMemcpyKind kind) {
  return remember(addNode(graph, node, Kind::Memcpy, dependencies, count, [=] { return hipMemcpy(destination, source, size, kind); }));
}
extern "C" hipError_t hipGraphAddMemsetNode(hipGraphNode_t *node, hipGraph_t graph, const hipGraphNode_t *dependencies, size_t count,
                                            const hipMemsetParams *params) {
  if (!params || (params->elementSize != 1 && params->elementSize != 2 && params->elementSize != 4)) return remember(hipErrorInvalidValue);
  auto *const destination = params->dst;
  const auto element = params->elementSize;
  const auto value = params->value;
  const auto width = params->width;
  const auto height = params->height;
  const auto pitch = height == 1 ? width * element : params->pitch;
  return remember(addNode(graph, node, Kind::Memset, dependencies, count,
                          [=] { return memsetRows(destination, pitch, element, value, width, height); }));
}
extern "C" hipError_t hipGraphAddHostNode(hipGraphNode_t *node, hipGraph_t graph, const hipGraphNode_t *dependencies, size_t count,
                                          const hipHostNodeParams *params) {
  if (!params || !params->fn) return remember(hipErrorInvalidValue);
  return remember(addNode(graph, node, Kind::Host, dependencies, count, [fn = params->fn, userData = params->userData] {
    fn(userData);
    return hipSuccess;
  }));
}
extern "C" hipError_t hipGraphAddEmptyNode(hipGraphNode_t *node, hipGraph_t graph, const hipGraphNode_t *dependencies, size_t count) {
  return remember(addNode(graph, node, Kind::Empty, dependencies, count, [] { return hipSuccess; }));
}
extern "C" hipError_t hipGraphAddDependencies(hipGraph_t graph, const hipGraphNode_t *from, const hipGraphNode_t *to, size_t count) {
  return remember(graphResult(gemstone::graph::connect(graphOf(graph), from, to, count)));
}
extern "C" hipError_t hipGraphGetNodes(hipGraph_t graph, hipGraphNode_t *nodes, size_t *count) {
  if (!graph) return remember(hipErrorInvalidValue);
  return remember(graphResult(gemstone::graph::list(*graphOf(graph), nodes, count, false)));
}
extern "C" hipError_t hipGraphGetRootNodes(hipGraph_t graph, hipGraphNode_t *nodes, size_t *count) {
  if (!graph) return remember(hipErrorInvalidValue);
  return remember(graphResult(gemstone::graph::list(*graphOf(graph), nodes, count, true)));
}
extern "C" hipError_t hipGraphInstantiateWithFlags(hipGraphExec_t *exec, hipGraph_t graph, unsigned long long flags) {
  if (!exec || !graph) return remember(hipErrorInvalidValue);
  constexpr unsigned long long inert =
      hipGraphInstantiateFlagAutoFreeOnLaunch | hipGraphInstantiateFlagUpload | hipGraphInstantiateFlagUseNodePriority;
  if ((flags & ~inert) != 0) return remember(hipErrorNotSupported);
  *exec = reinterpret_cast<hipGraphExec_t>(
      std::make_unique<gemstone::graph::Exec>(gemstone::graph::Exec::instantiate(*graphOf(graph))).release());
  return remember(hipSuccess);
}
extern "C" hipError_t hipGraphInstantiate(hipGraphExec_t *exec, hipGraph_t graph, hipGraphNode_t *errorNode, char *log, size_t logSize) {
  const auto status = hipGraphInstantiateWithFlags(exec, graph, 0);
  if (status != hipSuccess) {
    if (errorNode) *errorNode = nullptr;
    if (log && logSize) log[0] = '\0';
  }
  return status;
}
extern "C" hipError_t hipGraphLaunch(hipGraphExec_t exec, hipStream_t stream) {
  if (!exec) return remember(hipErrorInvalidValue);
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  for (const auto &step : execOf(exec)->steps)
    if (const auto status = enqueueCommand(stream, step.work); status != hipSuccess) return remember(status);
  return remember(hipSuccess);
}
extern "C" hipError_t hipGraphExecDestroy(hipGraphExec_t exec) {
  delete execOf(exec);
  return remember(hipSuccess);
}
extern "C" hipError_t hipGraphExecKernelNodeSetParams(hipGraphExec_t exec, hipGraphNode_t node, const hipKernelNodeParams *params) {
  if (!exec || !params) return remember(hipErrorInvalidValue);
  gemstone::graph::Work work;
  if (const auto status = recordKernel(*params, work); status != hipSuccess) return remember(status);
  return remember(graphResult(execOf(exec)->replaceKernel(reinterpret_cast<gemstone::graph::Node *>(node), std::move(work))));
}
extern "C" hipError_t hipGraphExecUpdate(hipGraphExec_t exec, hipGraph_t graph, hipGraphNode_t *errorNode,
                                         hipGraphExecUpdateResult *result) {
  if (!exec || !graph) return remember(hipErrorInvalidValue);
  const auto outcome = execOf(exec)->update(*graphOf(graph));
  if (errorNode) *errorNode = nullptr;
  if (result) *result = outcome == Outcome::Ok ? hipGraphExecUpdateSuccess : hipGraphExecUpdateErrorTopologyChanged;
  return remember(graphResult(outcome));
}

extern "C" hipError_t hipModuleLoadData(hipModule_t *module, const void *image) {
  if (!module || !image) return remember(hipErrorInvalidValue);
  *module = nullptr;
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  auto loaded = createModule(image);
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP module load image=%p loaded=%d error=%s\n", image, loaded->loaded ? 1 : 0, loaded->error.c_str());
  return remember(publishModule(std::move(loaded), module));
}

extern "C" hipError_t hipModuleLoad(hipModule_t *module, const char *fname) {
  if (!module || !fname) return remember(hipErrorInvalidValue);
  *module = nullptr;
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  std::ifstream file(fname, std::ios::binary);
  if (!file) return remember(hipErrorFileNotFound);
  auto loaded = std::make_shared<Module>();
  loaded->fileImage.assign(std::istreambuf_iterator<char>(file), std::istreambuf_iterator<char>());
  if (loaded->fileImage.size() < EI_NIDENT) return remember(hipErrorInvalidImage);
  if (selectImage(*loaded, loaded->fileImage.data())) loadModule(*loaded, currentDevice);
  return remember(publishModule(std::move(loaded), module));
}

extern "C" hipError_t hipModuleLoadDataEx(hipModule_t *module, const void *image, unsigned int, hipJitOption *, void **) {
  return hipModuleLoadData(module, image);
}

extern "C" hipError_t hipModuleLoadFatBinary(hipModule_t *module, const void *fatbin) { return hipModuleLoadData(module, fatbin); }

extern "C" hipError_t hipModuleUnload(hipModule_t module) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!module) return remember(hipErrorInvalidValue);
  delete moduleHandle(module);
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleGetFunction(hipFunction_t *function, hipModule_t module, const char *name) {
  if (!function || !module || !name) return remember(hipErrorInvalidValue);
  *function = nullptr;
  auto *handle = moduleHandle(module);
  if (!handle->module || !handle->module->loaded) return remember(hipErrorInvalidResourceHandle);
  std::shared_ptr<Module> selected = handle->module;
  auto kernel = resolveModuleKernel(*selected, name, currentDevice);
  if (!kernel) {
    std::vector<std::weak_ptr<Module>> candidates;
    {
      std::scoped_lock const guard(state().lock);
      candidates = state().driverModules;
    }
    std::vector<std::weak_ptr<Module>> live;
    for (auto &candidate : candidates) {
      auto module = candidate.lock();
      if (!module) continue;
      live.push_back(candidate);
      const auto global = resolveModuleKernel(*module, name, currentDevice);
      if (global && !kernel) {
        selected = module;
        kernel = global;
      }
    }
    {
      std::scoped_lock const guard(state().lock);
      // Keep modules registered while this thread was resolving.
      for (const auto &entry : state().driverModules) {
        const bool present = live ^ exists([&](const auto &kept) { return !kept.owner_before(entry) && !entry.owner_before(kept); });
        if (!present) live.push_back(entry);
      }
      state().driverModules = std::move(live);
    }
  }
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP module function module=%p name=%s found=%d\n", static_cast<void *>(handle), name, kernel ? 1 : 0);
  if (traceModules() && !kernel) {
    const std::string requested(name);
    const size_t separator = requested.find("_FSSC");
    const std::string prefix = requested.substr(0, separator);
    selected->metadata | filter([&](const auto &candidate) { return candidate.name ^ starts_with(prefix); }) | take(8)
        | for_each([](const auto &candidate) { std::fprintf(stderr, "Gemstone HIP nearby module function=%s\n", candidate.name.c_str()); });
  }
  if (!kernel) return remember(hipErrorNotFound);
  auto *result = new (std::nothrow) FunctionHandle{.module = std::move(selected), .kernel = std::move(kernel)};
  if (!result) return remember(hipErrorOutOfMemory);
  *function = reinterpret_cast<hipFunction_t>(result);
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleLaunchKernel(hipFunction_t function, unsigned int gridX, unsigned int gridY, unsigned int gridZ,
                                            unsigned int blockX, unsigned int blockY, unsigned int blockZ, unsigned int shared,
                                            hipStream_t stream, void **args, void **extra) {
  if (!function) return remember(hipErrorInvalidDeviceFunction);
  auto *functionState = functionHandle(function);
  if (!functionState->module || !functionState->module->loaded) return remember(hipErrorInvalidResourceHandle);
  LaunchBuffer launchBuffer;
  if (!parseLaunchBuffer(extra, launchBuffer)) return remember(hipErrorInvalidValue);
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP module launch f=%p grid=%u,%u,%u block=%u,%u,%u args=%p\n", static_cast<void *>(function), gridX,
                 gridY, gridZ, blockX, blockY, blockZ, static_cast<void *>(args));
  return dispatchKernel(functionState->kernel, dim3(gridX, gridY, gridZ), dim3(blockX, blockY, blockZ), args, shared, stream, launchBuffer);
}

extern "C" hipError_t hipModuleGetFunctionCount(unsigned int *count, hipModule_t module) {
  if (!count || !module) return remember(hipErrorInvalidValue);
  auto *handle = moduleHandle(module);
  if (!handle->module) return remember(hipErrorInvalidResourceHandle);
  *count = static_cast<unsigned int>(handle->module->metadata.size());
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(int *blocks, hipFunction_t function, int blockSize,
                                                                         size_t shared) {
  if (!blocks || !function || blockSize <= 0) return remember(hipErrorInvalidValue);
  if (!functionHandle(function)->kernel || !functionHandle(function)->module->loaded) return remember(hipErrorInvalidDeviceFunction);
  *blocks = residentBlocks(static_cast<uint64_t>(blockSize), shared);
  return remember(hipSuccess);
}
extern "C" hipError_t hipModuleLaunchCooperativeKernel(hipFunction_t function, unsigned int gridX, unsigned int gridY, unsigned int gridZ,
                                                       unsigned int blockX, unsigned int blockY, unsigned int blockZ, unsigned int shared,
                                                       hipStream_t stream, void **args) {
  if (!function) return remember(hipErrorInvalidDeviceFunction);
  auto *functionState = functionHandle(function);
  if (!functionState->module || !functionState->module->loaded) return remember(hipErrorInvalidResourceHandle);
  return dispatchCooperative(functionState->kernel, dim3(gridX, gridY, gridZ), dim3(blockX, blockY, blockZ), args, shared, stream);
}
extern "C" GEMSTONE_PUBLIC_API hipError_t hipExtModuleLaunchKernel(hipFunction_t function, uint32_t globalX, uint32_t globalY,
                                                                   uint32_t globalZ, uint32_t localX, uint32_t localY, uint32_t localZ,
                                                                   size_t shared, hipStream_t stream, void **args, void **extra, hipEvent_t,
                                                                   hipEvent_t, uint32_t) {
  if (!function) return remember(hipErrorInvalidDeviceFunction);
  auto *functionState = functionHandle(function);
  if (!functionState->module || !functionState->module->loaded) return remember(hipErrorInvalidResourceHandle);
  if (globalX == 0 || globalY == 0 || globalZ == 0 || globalX > MaxGridDimension || globalY > MaxGridDimension || globalZ > MaxGridDimension
      || !validBlock(dim3(localX, localY, localZ)))
    return remember(hipErrorInvalidConfiguration);
  LaunchBuffer launchBuffer;
  if (!parseLaunchBuffer(extra, launchBuffer)) return remember(hipErrorInvalidValue);
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP ext module launch f=%p global=%u,%u,%u local=%u,%u,%u args=%p\n", static_cast<void *>(function),
                 globalX, globalY, globalZ, localX, localY, localZ, static_cast<void *>(args));
  const dim3 global(globalX, globalY, globalZ);
  // Like the vendor extended launch path, fit a nominal workgroup inside a smaller whole grid.
  const dim3 local(std::min(localX, globalX), std::min(localY, globalY), std::min(localZ, globalZ));
  return dispatchKernel(functionState->kernel,
                        dim3(workgroupCount(globalX, local.x), workgroupCount(globalY, local.y), workgroupCount(globalZ, local.z)), local,
                        args, shared, stream, launchBuffer, &global);
}

extern "C" hipError_t hipEventCreateWithFlags(hipEvent_t *event, unsigned int) {
  if (!event) return remember(hipErrorInvalidValue);
  *event = static_cast<hipEvent_t>(commands().createEvent());
  return remember(hipSuccess);
}
extern "C" hipError_t hipEventCreate(hipEvent_t *event) { return hipEventCreateWithFlags(event, 0); }
extern "C" hipError_t hipEventDestroy(hipEvent_t event) {
  return remember(commands().destroyEvent(event) ? hipSuccess : hipErrorInvalidResourceHandle);
}
extern "C" hipError_t hipEventRecordWithFlags(hipEvent_t event, hipStream_t stream, unsigned int) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  if (const auto captured = captures.recordEvent(event, stream)) return remember(graphResult(*captured));
  return remember(commands().recordEvent(event, stream) ? hipSuccess : hipErrorInvalidResourceHandle);
}
extern "C" hipError_t hipEventRecord(hipEvent_t event, hipStream_t stream) { return hipEventRecordWithFlags(event, stream, 0); }
extern "C" hipError_t hipEventSynchronize(hipEvent_t event) {
  const auto ticket = commands().eventTicket(event);
  if (!ticket) return remember(hipErrorInvalidResourceHandle);
  return remember(static_cast<hipError_t>(commands().wait(*ticket)));
}
extern "C" hipError_t hipEventQuery(hipEvent_t event) {
  const auto ticket = commands().eventTicket(event);
  if (!ticket) return remember(hipErrorInvalidResourceHandle);
  return remember(static_cast<hipError_t>(commands().query(*ticket, hipErrorNotReady)));
}
extern "C" hipError_t hipEventElapsedTime(float *milliseconds, hipEvent_t start, hipEvent_t stop) {
  if (!milliseconds) return remember(hipErrorInvalidValue);
  if (!commands().eventTicket(start) || !commands().eventTicket(stop)) return remember(hipErrorInvalidResourceHandle);
  *milliseconds = 0.0F;
  return remember(hipSuccess);
}

extern "C" GEMSTONE_PUBLIC_API void **__hipRegisterFatBinary(const void *fatbin) {
  // Compiler-generated unregister callbacks are registered after this call.
  // Construct the queue first so its destructor runs after those callbacks.
  (void)commands();
  auto module = std::make_shared<Module>();
  module->fatbin = fatbin;
  void **token = &module->token;
  {
    auto &runtime = state();
    std::scoped_lock const guard(runtime.lock);
    runtime.modules.emplace(token, module);
  }
  if (traceModules()) std::fprintf(stderr, "Gemstone HIP fatbinary=%p registered lazily\n", fatbin);
  return token;
}

extern "C" GEMSTONE_PUBLIC_API void __hipRegisterFunction(void **modules, const void *hostFunction, char *, const char *deviceName,
                                                          unsigned int, uint3 *, uint3 *, dim3 *, dim3 *, int *) {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  const auto module = findModule(modules);
  if (!module || !hostFunction || !deviceName) return;
  runtime.kernels.insert_or_assign(hostFunction, FunctionBinding{.module = module, .name = deviceName, .kernels = {}});
}

extern "C" GEMSTONE_PUBLIC_API void __hipRegisterVar(void **modules, void *var, char *, char *deviceName, int, size_t size, int, int) {
  if (!modules || !var || size == 0) return;
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  const auto module = findModule(modules);
  if (!module) return;
  const auto [iterator, inserted] = runtime.variables.insert_or_assign(
      var, Variable{.host = var, .deviceName = !deviceName ? std::string{} : std::string(deviceName), .size = size, .module = module});
  (void)iterator;
  if (inserted) module->variables.push_back(var);
}

extern "C" GEMSTONE_PUBLIC_API void __hipUnregisterFatBinary(void **modules) {
  (void)waitCommands();
  auto &runtime = state();
  std::shared_ptr<Module> module;
  {
    std::scoped_lock const guard(runtime.lock);
    const auto found = runtime.modules.find(modules);
    if (found == runtime.modules.end()) return;
    module = found->second;
    runtime.modules.erase(found);
    if (module) {
      for (auto iterator = runtime.kernels.begin(); iterator != runtime.kernels.end();) {
        if (iterator->second.module == module) iterator = runtime.kernels.erase(iterator);
        else ++iterator;
      }
      for (const void *variable : module->variables)
        runtime.variables.erase(variable);
    }
  }
}

/** A linear image in device memory, 256-byte aligned as image descriptors address it, with its mip levels after it. */
struct hipArray {
  void *allocation = nullptr;
  void *data = nullptr;
  hipChannelFormatDesc desc{};
  gemstone::hsa::images::Format format{};
  gemstone::hsa::images::ImageType type{};
  size_t width = 0, height = 0, depth = 0, pitch = 0;
  unsigned flags = 0;
  uint32_t levels = 1;
};

namespace {

/** The image a mipmapped array's data points at, which owns the allocation, and a view of each of its levels. */
struct MipmappedImage {
  hipArray image;
  std::vector<std::unique_ptr<hipArray>> levels;
};

const hipArray *mipmapBase(hipMipmappedArray_const_t mipmappedArray) {
  if (!mipmappedArray || !mipmappedArray->data) return nullptr;
  return &static_cast<const MipmappedImage *>(mipmappedArray->data)->image;
}

namespace images = gemstone::hsa::images;

images::Generation imageGeneration() { return images::generation(gemstone::hsa::targetArch()); }

std::optional<images::Format> imageFormat(const hipChannelFormatDesc &desc, bool normalised) {
  images::Kind kind{};
  switch (desc.f) {
    case hipChannelFormatKindSigned: kind = images::Kind::Signed; break;
    case hipChannelFormatKindUnsigned: kind = images::Kind::Unsigned; break;
    case hipChannelFormatKindFloat: kind = images::Kind::Float; break;
    default: return std::nullopt;
  }
  return images::format(images::Channels{.x = desc.x, .y = desc.y, .z = desc.z, .w = desc.w, .kind = kind}, normalised);
}

/** Allocates `image`'s levels, its fields but its storage set. */
hipError_t allocateImage(hipArray &image) {
  const size_t bytes = images::level(image.format, image.type, image.width, image.height, image.depth, image.levels).offset;
  if (const auto status = hipMalloc(&image.allocation, bytes + images::RowAlignment); status != hipSuccess) return status;
  const auto address = reinterpret_cast<uintptr_t>(image.allocation);
  image.data = reinterpret_cast<void *>((address + images::RowAlignment - 1) / images::RowAlignment * images::RowAlignment);
  image.pitch = images::rowPitch(image.width, image.format.bytes);
  return hipSuccess;
}

std::optional<hipArray> describeArray(const hipChannelFormatDesc *desc, images::ImageType type, size_t width, size_t height, size_t depth,
                                      unsigned flags) {
  if (!desc || width == 0) return std::nullopt;
  const auto format = imageFormat(*desc, false);
  if (!format) return std::nullopt;
  hipArray described{};
  described.desc = *desc;
  described.format = *format;
  described.type = type;
  described.width = width;
  described.height = height;
  described.depth = depth;
  described.flags = flags;
  return described;
}

hipError_t allocateArray(hipArray_t *array, const hipChannelFormatDesc *desc, images::ImageType type, size_t width, size_t height,
                         size_t depth, unsigned flags) {
  auto described = describeArray(desc, type, width, height, depth, flags);
  if (!array || !described) return hipErrorInvalidValue;
  if (const auto status = allocateImage(*described); status != hipSuccess) return status;
  *array = new hipArray(*described);
  return hipSuccess;
}

/** The array type an extent and flags name, as hipMalloc3DArray takes them. */
std::optional<images::ImageType> arrayType(const hipExtent &extent, unsigned flags) {
  const bool layered = (flags & hipArrayLayered) != 0;
  if (layered && extent.depth == 0) return std::nullopt;
  if (layered) return extent.height == 0 ? images::ImageType::Lines : images::ImageType::Planes;
  if (extent.depth != 0) return images::ImageType::Volume;
  return extent.height != 0 ? images::ImageType::Plane : images::ImageType::Line;
}

/** Copies `bytes` row-major into `array` from const memory, or out of it otherwise, from byte `x` of row `y`, wrapping at
 * the end of each row: a partial first row, the whole rows, then a partial last row.
 */
template <typename Byte> hipError_t copyRows(hipArray_const_t array, size_t x, size_t y, Byte *other, size_t bytes, hipMemcpyKind kind) {
  const size_t rowBytes = array ? array->width * array->format.bytes : 0;
  if (!array || (!other && bytes != 0) || x >= rowBytes) return hipErrorInvalidValue;
  const size_t head = std::min(bytes, rowBytes - x);
  const size_t whole = (bytes - head) / rowBytes;
  const size_t tail = (bytes - head) % rowBytes;
  if (bytes != 0 && y + 1 + whole + (tail != 0 ? 1 : 0) > array->height * array->depth) return hipErrorInvalidValue;
  auto *image = static_cast<unsigned char *>(array->data) + (y * array->pitch);
  const auto copy = [&](size_t offset, Byte *memory, size_t width, size_t height, size_t memoryPitch) {
    if constexpr (std::is_const_v<Byte>)
      return copyPitched(image + offset, array->pitch, 0, memory, memoryPitch, 0, width, height, 1, kind);
    else return copyPitched(memory, memoryPitch, 0, image + offset, array->pitch, 0, width, height, 1, kind);
  };
  if (const auto status = copy(x, other, head, 1, head); status != hipSuccess) return status;
  if (const auto status = copy(array->pitch, other + head, rowBytes, whole, rowBytes); status != hipSuccess) return status;
  return copy((1 + whole) * array->pitch, other + head + (whole * rowBytes), tail, 1, tail);
}

/** A device copy of `words`, the object handle HIP's device functions read descriptors through. */
template <size_t N> hipError_t deviceObject(const std::array<uint32_t, N> &words, void **object) {
  if (const auto status = hipMalloc(object, sizeof(words)); status != hipSuccess) return status;
  // no queued work can read a fresh object, so the write need not wait for it
  return remember(mapHsa(hsa_memory_copy(*object, words.data(), sizeof(words))));
}

images::AddressMode addressMode(hipTextureAddressMode mode) {
  switch (mode) {
    case hipAddressModeClamp: return images::AddressMode::ClampLastTexel;
    case hipAddressModeMirror: return images::AddressMode::Mirror;
    case hipAddressModeBorder: return images::AddressMode::ClampBorder;
    default: return images::AddressMode::Wrap;
  }
}

/** The sampler a texture description asks for over an image of `levels` mip levels; without a description, point sampling clamped. */
images::Sampler samplerOf(const hipTextureDesc *desc, uint32_t levels) {
  if (!desc)
    return images::Sampler{
        .address = {images::AddressMode::ClampLastTexel, images::AddressMode::ClampLastTexel, images::AddressMode::ClampLastTexel},
        .filter = images::Filter::Point,
        .mip = images::MipFilter::None,
        .normalised = false,
        .minLod = 0.0F,
        .maxLod = 0.0F,
        .lodBias = 0.0F};
  const auto mip = [&] {
    if (levels <= 1) return images::MipFilter::None;
    if (desc->mipmapFilterMode == hipFilterModeLinear) return images::MipFilter::Linear;
    return images::MipFilter::Point;
  }();
  return images::Sampler{
      .address = {addressMode(desc->addressMode[0]), addressMode(desc->addressMode[1]), addressMode(desc->addressMode[2])},
      .filter = desc->filterMode == hipFilterModeLinear ? images::Filter::Linear : images::Filter::Point,
      .mip = mip,
      .normalised = desc->normalizedCoords != 0,
      .minLod = desc->minMipmapLevelClamp,
      .maxLod = desc->maxMipmapLevelClamp,
      .lodBias = desc->mipmapLevelBias};
}

/** An image object: the descriptor, then the channel type, channel order and width the device libraries read back, then the sampler. */
std::array<uint32_t, HIP_TEXTURE_OBJECT_SIZE_DWORD> arrayObject(const hipArray &array, const hipTextureDesc *texture = nullptr) {
  const auto descriptor = images::image(imageGeneration(), reinterpret_cast<uint64_t>(array.data), array.format, array.type, array.width,
                                        array.height, array.depth, array.pitch, array.levels);
  std::array<uint32_t, HIP_TEXTURE_OBJECT_SIZE_DWORD> words{};
  std::copy(descriptor.begin(), descriptor.end(), words.begin());
  words[8] = array.format.channelType;
  words[9] = array.format.channelOrder;
  words[10] = static_cast<uint32_t>(array.width);
  const auto sampler = images::sampler(imageGeneration(), samplerOf(texture, array.levels));
  std::copy(sampler.begin(), sampler.end(), words.begin() + HIP_SAMPLER_OBJECT_OFFSET_DWORD);
  return words;
}

} // namespace

extern "C" struct hipChannelFormatDesc hipCreateChannelDesc(int x, int y, int z, int w, enum hipChannelFormatKind f) {
  return hipChannelFormatDesc{.x = x, .y = y, .z = z, .w = w, .f = f};
}

extern "C" hipError_t hipMallocArray(hipArray_t *array, const hipChannelFormatDesc *desc, size_t width, size_t height, unsigned int flags) {
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  return remember(allocateArray(array, desc, height == 0 ? images::ImageType::Line : images::ImageType::Plane, width,
                                std::max<size_t>(height, 1), 1, flags));
}

extern "C" hipError_t hipMalloc3DArray(hipArray_t *array, const struct hipChannelFormatDesc *desc, struct hipExtent extent,
                                       unsigned int flags) {
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  const auto type = arrayType(extent, flags);
  if (!type) return remember(hipErrorInvalidValue);
  return remember(
      allocateArray(array, desc, *type, extent.width, std::max<size_t>(extent.height, 1), std::max<size_t>(extent.depth, 1), flags));
}

extern "C" hipError_t hipMallocMipmappedArray(hipMipmappedArray_t *mipmappedArray, const struct hipChannelFormatDesc *desc,
                                              struct hipExtent extent, unsigned int numLevels, unsigned int flags) {
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  const auto type = arrayType(extent, flags);
  if (!mipmappedArray || !type) return remember(hipErrorInvalidValue);
  const size_t height = std::max<size_t>(extent.height, 1);
  const size_t depth = std::max<size_t>(extent.depth, 1);
  auto described = describeArray(desc, *type, extent.width, height, depth, flags);
  if (!described) return remember(hipErrorInvalidValue);
  described->levels = std::clamp(numLevels, 1U, images::levels(*type, extent.width, height, depth));
  auto image = std::make_unique<MipmappedImage>();
  image->image = *described;
  if (const auto status = allocateImage(image->image); status != hipSuccess) return remember(status);
  for (uint32_t index = 0; index < image->image.levels; ++index) {
    const auto found = images::level(image->image.format, *type, extent.width, height, depth, index);
    auto view = std::make_unique<hipArray>(image->image);
    view->allocation = nullptr;
    view->data = static_cast<unsigned char *>(image->image.data) + found.offset;
    view->width = found.width;
    view->height = found.height;
    view->depth = found.depth;
    view->pitch = found.pitch;
    view->levels = 1;
    image->levels.push_back(std::move(view));
  }
  auto *created = new hipMipmappedArray{};
  created->desc = *desc;
  created->type = static_cast<unsigned>(*type);
  created->width = static_cast<unsigned>(extent.width);
  created->height = static_cast<unsigned>(extent.height);
  created->depth = static_cast<unsigned>(extent.depth);
  created->max_mipmap_level = image->image.levels - 1;
  created->flags = flags;
  created->num_channels = image->image.format.channels;
  created->data = image.release();
  *mipmappedArray = created;
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetMipmappedArrayLevel(hipArray_t *levelArray, hipMipmappedArray_const_t mipmappedArray, unsigned int level) {
  if (!levelArray || !mipmappedArray || !mipmappedArray->data) return remember(hipErrorInvalidValue);
  const auto *image = static_cast<const MipmappedImage *>(mipmappedArray->data);
  if (level >= image->levels.size()) return remember(hipErrorInvalidValue);
  *levelArray = image->levels[level].get();
  return remember(hipSuccess);
}

extern "C" hipError_t hipFreeMipmappedArray(hipMipmappedArray_t mipmappedArray) {
  if (!mipmappedArray || !mipmappedArray->data) return remember(hipErrorInvalidValue);
  std::unique_ptr<MipmappedImage> image(static_cast<MipmappedImage *>(mipmappedArray->data));
  delete mipmappedArray;
  return remember(hipFree(image->image.allocation));
}

extern "C" hipError_t hipFreeArray(hipArray_t array) {
  if (!array) return remember(hipErrorInvalidValue);
  const auto status = hipFree(array->allocation);
  delete array;
  return remember(status);
}

extern "C" hipError_t hipGetChannelDesc(hipChannelFormatDesc *desc, hipArray_const_t array) {
  if (!desc || !array) return remember(hipErrorInvalidValue);
  *desc = array->desc;
  return remember(hipSuccess);
}

extern "C" hipError_t hipArrayGetInfo(hipChannelFormatDesc *desc, hipExtent *extent, unsigned int *flags, hipArray_t array) {
  if (!array) return remember(hipErrorInvalidValue);
  if (desc) *desc = array->desc;
  if (extent) {
    const bool line = array->type == images::ImageType::Line || array->type == images::ImageType::Lines;
    const bool flat = array->type == images::ImageType::Line || array->type == images::ImageType::Plane;
    *extent = hipExtent{.width = array->width, .height = line ? 0 : array->height, .depth = flat ? 0 : array->depth};
  }
  if (flags) *flags = array->flags;
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemcpy2DToArray(hipArray_t dst, size_t wOffset, size_t hOffset, const void *src, size_t spitch, size_t width,
                                         size_t height, hipMemcpyKind kind) {
  if (!dst || wOffset + width > dst->pitch || hOffset + height > dst->height * dst->depth) return remember(hipErrorInvalidValue);
  return hipMemcpy2D(static_cast<unsigned char *>(dst->data) + (hOffset * dst->pitch) + wOffset, dst->pitch, src, spitch, width, height,
                     kind);
}

extern "C" hipError_t hipMemcpy2DToArrayAsync(hipArray_t dst, size_t wOffset, size_t hOffset, const void *src, size_t spitch, size_t width,
                                              size_t height, hipMemcpyKind kind, hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipMemcpy2DToArray(dst, wOffset, hOffset, src, spitch, width, height, kind); }));
}

extern "C" hipError_t hipMemcpy2DFromArray(void *dst, size_t dpitch, hipArray_const_t src, size_t wOffset, size_t hOffset, size_t width,
                                           size_t height, hipMemcpyKind kind) {
  if (!src || wOffset + width > src->pitch || hOffset + height > src->height * src->depth) return remember(hipErrorInvalidValue);
  return hipMemcpy2D(dst, dpitch, static_cast<const unsigned char *>(src->data) + (hOffset * src->pitch) + wOffset, src->pitch, width,
                     height, kind);
}

extern "C" hipError_t hipMemcpy2DFromArrayAsync(void *dst, size_t dpitch, hipArray_const_t src, size_t wOffset, size_t hOffset,
                                                size_t width, size_t height, hipMemcpyKind kind, hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipMemcpy2DFromArray(dst, dpitch, src, wOffset, hOffset, width, height, kind); }));
}

extern "C" hipError_t hipMemcpyToArray(hipArray_t dst, size_t wOffset, size_t hOffset, const void *src, size_t count, hipMemcpyKind kind) {
  return remember(copyRows(dst, wOffset, hOffset, static_cast<const unsigned char *>(src), count, kind));
}

extern "C" hipError_t hipMemcpyFromArray(void *dst, hipArray_const_t srcArray, size_t wOffset, size_t hOffset, size_t count,
                                         hipMemcpyKind kind) {
  return remember(copyRows(srcArray, wOffset, hOffset, static_cast<unsigned char *>(dst), count, kind));
}

extern "C" hipError_t hipMemcpyHtoA(hipArray_t dstArray, size_t dstOffset, const void *srcHost, size_t count) {
  const size_t row = dstArray ? dstArray->width * dstArray->format.bytes : 1;
  return remember(
      copyRows(dstArray, dstOffset % row, dstOffset / row, static_cast<const unsigned char *>(srcHost), count, hipMemcpyHostToDevice));
}

extern "C" hipError_t hipMemcpyAtoH(void *dst, hipArray_t srcArray, size_t srcOffset, size_t count) {
  const size_t row = srcArray ? srcArray->width * srcArray->format.bytes : 1;
  return remember(copyRows(srcArray, srcOffset % row, srcOffset / row, static_cast<unsigned char *>(dst), count, hipMemcpyDeviceToHost));
}

namespace {

/** One side of a 3D copy: an array, addressed in its elements, or pitched memory, addressed in bytes. */
struct CopySide {
  unsigned char *base;
  size_t pitch, slice, element;
};

std::optional<CopySide> copySide(hipArray_t array, const hipPos &position, const hipPitchedPtr &pointer) {
  if (array) {
    const size_t element = array->format.bytes;
    return CopySide{.base = static_cast<unsigned char *>(array->data) + (position.z * array->pitch * array->height)
                            + (position.y * array->pitch) + (position.x * element),
                    .pitch = array->pitch,
                    .slice = array->pitch * array->height,
                    .element = element};
  }
  if (!pointer.ptr) return std::nullopt;
  return CopySide{.base = static_cast<unsigned char *>(pointer.ptr) + (position.z * pointer.pitch * pointer.ysize)
                          + (position.y * pointer.pitch) + position.x,
                  .pitch = pointer.pitch,
                  .slice = pointer.pitch * pointer.ysize,
                  .element = 1};
}

} // namespace

extern "C" hipError_t hipMemcpy3D(const struct hipMemcpy3DParms *p) {
  if (!p) return remember(hipErrorInvalidValue);
  const auto source = copySide(p->srcArray, p->srcPos, p->srcPtr);
  const auto destination = copySide(p->dstArray, p->dstPos, p->dstPtr);
  if (!source || !destination) return remember(hipErrorInvalidValue);
  // an array participating counts the width in its elements
  const size_t width = p->extent.width * (p->srcArray ? source->element : destination->element);
  return remember(copyPitched(destination->base, destination->pitch, destination->slice, source->base, source->pitch, source->slice, width,
                              std::max<size_t>(p->extent.height, 1), std::max<size_t>(p->extent.depth, 1), p->kind));
}

extern "C" hipError_t hipMemcpy3DAsync(const struct hipMemcpy3DParms *p, hipStream_t stream) {
  if (!p) return remember(hipErrorInvalidValue);
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  const hipMemcpy3DParms copied = *p;
  return remember(enqueueCommand(stream, [=] { return hipMemcpy3D(&copied); }));
}

extern "C" hipError_t hipCreateSurfaceObject(hipSurfaceObject_t *pSurfObject, const hipResourceDesc *pResDesc) {
  if (!pSurfObject || !pResDesc || pResDesc->resType != hipResourceTypeArray || !pResDesc->res.array.array)
    return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  void *object = nullptr;
  const auto status = deviceObject(arrayObject(*pResDesc->res.array.array), &object);
  if (status == hipSuccess) *pSurfObject = static_cast<hipSurfaceObject_t>(object);
  return remember(status);
}

extern "C" hipError_t hipDestroySurfaceObject(hipSurfaceObject_t surfaceObject) { return hipFree(surfaceObject); }

extern "C" hipError_t hipCreateTextureObject(hipTextureObject_t *pTexObject, const hipResourceDesc *pResDesc,
                                             const hipTextureDesc *pTexDesc, const struct hipResourceViewDesc *) {
  if (!pTexObject || !pResDesc) return remember(hipErrorInvalidValue);
  if (!ensureInitialised()) return remember(hipErrorNoDevice);
  const bool normalised = pTexDesc && pTexDesc->readMode == hipReadModeNormalizedFloat;
  std::array<uint32_t, HIP_TEXTURE_OBJECT_SIZE_DWORD> words{};
  switch (pResDesc->resType) {
    case hipResourceTypeArray:
    case hipResourceTypeMipmappedArray: {
      const hipArray *source =
          pResDesc->resType == hipResourceTypeArray ? pResDesc->res.array.array : mipmapBase(pResDesc->res.mipmap.mipmap);
      if (!source) return remember(hipErrorInvalidValue);
      hipArray array = *source;
      const auto format = imageFormat(array.desc, normalised);
      if (!format) return remember(hipErrorInvalidValue);
      array.format = *format;
      words = arrayObject(array, pTexDesc);
      break;
    }
    case hipResourceTypeLinear: {
      const auto &linear = pResDesc->res.linear;
      const auto format = imageFormat(linear.desc, normalised);
      if (!linear.devPtr || !format) return remember(hipErrorInvalidValue);
      const auto descriptor =
          images::buffer(imageGeneration(), reinterpret_cast<uint64_t>(linear.devPtr), *format, linear.sizeInBytes / format->bytes);
      std::copy(descriptor.begin(), descriptor.end(), words.begin());
      words[8] = format->channelType;
      words[9] = format->channelOrder;
      words[10] = static_cast<uint32_t>(linear.sizeInBytes / format->bytes);
      break;
    }
    case hipResourceTypePitch2D: {
      const auto &pitched = pResDesc->res.pitch2D;
      const auto format = imageFormat(pitched.desc, normalised);
      if (!pitched.devPtr || !format || reinterpret_cast<uintptr_t>(pitched.devPtr) % images::RowAlignment != 0
          || pitched.pitchInBytes % images::RowAlignment != 0 || pitched.width * format->bytes > pitched.pitchInBytes)
        return remember(hipErrorInvalidValue);
      hipArray array{};
      array.data = pitched.devPtr;
      array.format = *format;
      array.type = images::ImageType::Plane;
      array.width = pitched.width;
      array.height = pitched.height;
      array.depth = 1;
      array.pitch = pitched.pitchInBytes;
      words = arrayObject(array, pTexDesc);
      break;
    }
    default: return remember(hipErrorNotSupported);
  }
  void *object = nullptr;
  const auto status = deviceObject(words, &object);
  if (status == hipSuccess) *pTexObject = static_cast<hipTextureObject_t>(object);
  return remember(status);
}

extern "C" hipError_t hipDestroyTextureObject(hipTextureObject_t textureObject) { return hipFree(textureObject); }
