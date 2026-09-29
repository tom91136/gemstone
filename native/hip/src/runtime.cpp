#include <algorithm>
#include <array>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <memory>
#include <mutex>
#include <new>
#include <string>
#include <string_view>
#include <thread>
#include <unordered_map>
#include <utility>
#include <vector>

#include <elf.h>
#include <hsa.h>
#include <hsa_ext_amd.h>
#include <stdint.h>

#include <aspartame/string.hpp>
#include <aspartame/string_view.hpp>
#include <hip/hip_ext.h>
#include <hip/hip_runtime_api.h>
#include <hip/hip_version.h>

#include "gemstone/hip/dispatch.hpp"
#include "gemstone/hsa/executable_metadata.hpp"
#include "gemstone/hsa/global.h"
#include "gemstone/hsa/target.h"
#include "gemstone_hip_api.h"
#include <gemstone/bytes.hpp>
#include <gemstone/command_queue.hpp>
#include <gemstone/export.h>
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

hipError_t enqueueCommand(hipStream_t stream, std::function<int32_t()> work) {
  try {
    commands().submit(stream, std::move(work));
    return hipSuccess;
  } catch (...) {
    return hipErrorOutOfMemory;
  }
}

constexpr uint32_t MaxBlockThreads = 1024;
constexpr uint32_t MaxGridDimension = std::numeric_limits<int32_t>::max();
constexpr size_t DeviceTotalMemoryBytes = size_t(1) << 30U;
constexpr size_t DeviceSharedMemoryPerBlock = 64U * 1024U;
constexpr uint32_t DeviceWarpSize = 64;
constexpr uint32_t DeviceThreadsPerMultiprocessor = 1024;

bool traceHip(std::string_view category) { return gemstone::traceEnabled("hip", category); }

bool validBlock(dim3 block) {
  return block.x != 0 && block.y != 0 && block.z != 0 && block.x <= MaxBlockThreads && block.y <= MaxBlockThreads
         && block.z <= MaxBlockThreads && uint64_t(block.x) * block.y * block.z <= MaxBlockThreads;
}

constexpr char BundleMagic[] = "__CLANG_OFFLOAD_BUNDLE__";
constexpr size_t BundleMagicSize = sizeof(BundleMagic) - 1U;
constexpr char CompressedBundleMagic[] = "CCOB";
constexpr size_t CompressedBundleMagicSize = sizeof(CompressedBundleMagic) - 1U;
constexpr size_t CompressedBundleHeaderV3Size = 32;
constexpr uint64_t MaximumBundleCount = 64;
// HIP fat binaries may contain several AMDGPU images, and the host ROCr agent
// is not necessarily the agent represented by this provider, so selection
// follows the provider's advertised target rather than bundle order.

struct Kernel {
  uint64_t object = 0;
  uint32_t kernarg_size = 0;
  uint32_t explicit_argument_count = 0;
  std::vector<gemstone_hsa_kernarg_field> fields;
};

struct Module;

struct Variable {
  void *host = nullptr;
  std::string device_name;
  size_t size = 0;
  std::shared_ptr<Module> module;
};

struct Module {
  std::mutex lock;
  // The address of this member is the opaque value passed back to the HIP
  // compiler's registration callbacks.
  void *token = nullptr;
  const void *fatbin = nullptr;
  const void *image = nullptr;
  size_t image_size = 0;
  hsa_code_object_reader_t reader{};
  hsa_executable_t executable{};
  bool loaded = false;
  std::string error;
  std::vector<uint8_t> decompressed_image;
  std::vector<gemstone::hsa::ExecutableKernelMetadata> metadata;
  std::unordered_map<std::string, std::shared_ptr<Kernel>> kernels;
  std::vector<const void *> variables;

  void reset() {
    if (executable.handle != 0) hsa_executable_destroy(executable);
    if (reader.handle != 0) hsa_code_object_reader_destroy(reader);
    executable = {};
    reader = {};
    metadata.clear();
    kernels.clear();
    loaded = false;
  }
  ~Module() { reset(); }
};

struct FunctionBinding {
  std::shared_ptr<Module> module;
  std::string name;
  std::shared_ptr<Kernel> kernel;
};

struct ModuleHandle {
  std::shared_ptr<Module> module;
};

struct FunctionHandle {
  std::shared_ptr<Module> module;
  std::shared_ptr<Kernel> kernel;
};

struct Event {
  gemstone::CommandQueue::Ticket ticket;
};

struct State {
  std::mutex lock;
  bool initialized = false;
  uint32_t primary_context_references = 0;
  bool atexit_registered = false;
  hsa_agent_t gpu{};
  hsa_region_t kernarg_region{};
  hsa_queue_t *queue = nullptr;
  std::unordered_map<void **, std::shared_ptr<Module>> modules;
  std::vector<std::weak_ptr<Module>> driver_modules;
  std::unordered_map<const void *, FunctionBinding> kernels;
  std::unordered_map<const void *, Variable> variables;
  gemstone::MemoryRegistrations host_registrations;
  std::unordered_map<void *, size_t> managed_allocations;
  std::unordered_map<void *, size_t> device_allocations;
  std::unordered_map<hipStream_t, std::unique_ptr<unsigned char>> streams;
  std::unordered_map<hipEvent_t, std::unique_ptr<Event>> events;
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

bool validStream(hipStream_t stream) {
  if (!stream || stream == hipStreamLegacy || stream == hipStreamPerThread) return true;
  std::lock_guard<std::mutex> guard(state().lock);
  return state().streams.count(stream) == 1;
}

thread_local hipError_t lastError = hipSuccess;
thread_local LaunchConfiguration launchConfiguration;

hipError_t remember(hipError_t error) {
  lastError = error;
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

hsa_status_t findGpu(hsa_agent_t agent, void *opaque) {
  hsa_device_type_t type = HSA_DEVICE_TYPE_CPU;
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  if (type == HSA_DEVICE_TYPE_GPU) {
    *static_cast<hsa_agent_t *>(opaque) = agent;
    return HSA_STATUS_INFO_BREAK;
  }
  return HSA_STATUS_SUCCESS;
}

hsa_status_t findCpu(hsa_agent_t agent, void *opaque) {
  hsa_device_type_t type = HSA_DEVICE_TYPE_GPU;
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  if (type == HSA_DEVICE_TYPE_CPU) {
    *static_cast<hsa_agent_t *>(opaque) = agent;
    return HSA_STATUS_INFO_BREAK;
  }
  return HSA_STATUS_SUCCESS;
}

hsa_status_t findRegion(hsa_region_t region, void *opaque) {
  *static_cast<hsa_region_t *>(opaque) = region;
  return HSA_STATUS_INFO_BREAK;
}

bool ensureInitialized() {
  (void)commands();
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (runtime.initialized) return true;
  const hsa_status_t init = hsa_init();
  const hsa_status_t agents = init == HSA_STATUS_SUCCESS ? hsa_iterate_agents(findGpu, &runtime.gpu) : HSA_STATUS_ERROR;
  const hsa_status_t regions =
      agents == HSA_STATUS_INFO_BREAK ? hsa_agent_iterate_regions(runtime.gpu, findRegion, &runtime.kernarg_region) : HSA_STATUS_ERROR;
  const hsa_status_t queue = regions == HSA_STATUS_INFO_BREAK ? hsa_queue_create(runtime.gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr,
                                                                                 UINT32_MAX, UINT32_MAX, &runtime.queue)
                                                              : HSA_STATUS_ERROR;
  if (init != HSA_STATUS_SUCCESS || agents != HSA_STATUS_INFO_BREAK || runtime.gpu.handle == 0 || regions != HSA_STATUS_INFO_BREAK
      || runtime.kernarg_region.handle == 0 || queue != HSA_STATUS_SUCCESS) {
    if (runtime.queue) {
      hsa_queue_destroy(runtime.queue);
      runtime.queue = nullptr;
    }
    hsa_shut_down();
    runtime.gpu = {};
    runtime.kernarg_region = {};
    return false;
  }
  runtime.initialized = true;
  if (!runtime.atexit_registered) {
    // Runtime cleanup also drains commands when no compiler fatbinary was registered.
    (void)commands();
    runtime.atexit_registered = true;
    std::atexit([] {
      (void)waitCommands();
      auto &value = state();
      std::lock_guard<std::mutex> exit_guard(value.lock);
      if (value.queue) {
        hsa_queue_destroy(value.queue);
        value.queue = nullptr;
      }
      if (value.initialized) {
        hsa_shut_down();
        value.initialized = false;
      }
    });
  }
  return true;
}

hipError_t allocateHost(void **pointer, size_t size, unsigned flags = 0) {
  if (!pointer) return remember(hipErrorInvalidValue);
  if (size == 0) {
    *pointer = nullptr;
    return remember(hipSuccess);
  }
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  void *memory = std::malloc(size);
  if (!memory) return remember(hipErrorOutOfMemory);
  const hsa_status_t status = hsa_memory_register(memory, size);
  if (status != HSA_STATUS_SUCCESS) {
    std::free(memory);
    return remember(mapHsa(status));
  }
  {
    std::lock_guard<std::mutex> guard(state().lock);
    state().host_registrations.add({reinterpret_cast<uintptr_t>(memory), size}, gemstone::MemoryOwnership::Owned, flags);
  }
  *pointer = memory;
  return remember(hipSuccess);
}

hipError_t releaseHost(void *pointer) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!pointer) return remember(hipSuccess);
  {
    std::lock_guard<std::mutex> guard(state().lock);
    const auto found = state().host_registrations.exact(reinterpret_cast<uintptr_t>(pointer));
    if (!found || found->ownership != gemstone::MemoryOwnership::Owned) return remember(hipErrorInvalidValue);
    const hsa_status_t status = hsa_memory_deregister(pointer, found->range.size);
    if (status != HSA_STATUS_SUCCESS) return remember(mapHsa(status));
    state().host_registrations.remove(reinterpret_cast<uintptr_t>(pointer), gemstone::MemoryOwnership::Owned);
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
  const auto file_size = readNative<uint64_t>(compressed, 8);
  const auto uncompressed_size = readNative<uint64_t>(compressed, 16);
  if (file_size < CompressedBundleHeaderV3Size || uncompressed_size == 0 || file_size > std::numeric_limits<size_t>::max()
      || uncompressed_size > std::numeric_limits<size_t>::max())
    return false;
  const auto compressed_size = static_cast<size_t>(file_size) - CompressedBundleHeaderV3Size;
  const auto output_size = static_cast<size_t>(uncompressed_size);
  return gemstone::decompressZstd(decompressed, compressed + CompressedBundleHeaderV3Size, compressed_size, output_size);
}

bool bundleTargetCompatible(const std::string &triple);

bool selectBundle(const void *fatbin, const void **image, size_t *image_size, std::vector<uint8_t> *decompressed = nullptr) {
  if (!fatbin || !image || !image_size) return false;
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
    uint64_t triple_size = 0;
    if (!readNativeU64(bundle, std::numeric_limits<size_t>::max(), cursor, offset)
        || !readNativeU64(bundle, std::numeric_limits<size_t>::max(), cursor, size)
        || !readNativeU64(bundle, std::numeric_limits<size_t>::max(), cursor, triple_size) || triple_size == 0 || triple_size > 4096)
      return false;
    const auto *triple = reinterpret_cast<const char *>(bundle + cursor);
    entries.push_back(Entry{offset, size, std::string(triple, static_cast<size_t>(triple_size))});
    cursor += static_cast<size_t>(triple_size);
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
  const Entry *selected = target;
  if (!selected) return false;
  *image = bundle + static_cast<size_t>(selected->offset);
  *image_size = static_cast<size_t>(selected->size);
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
  for (const auto &[prefix, canonical] : families)
    if ((target ^ starts_with(prefix)) && (triple ^ contains_slice(canonical))) return true;
  return false;
}

bool addFileRange(uint64_t offset, uint64_t size, uint64_t &end) {
  if (offset > std::numeric_limits<uint64_t>::max() - size) return false;
  end = std::max(end, offset + size);
  return true;
}

bool elfImageSize(const void *image, size_t &size) {
  if (!imageIsElf(image)) return false;
  const auto header = readNative<Elf64_Ehdr>(image);
  if (header.e_ident[EI_CLASS] != ELFCLASS64 || header.e_ident[EI_DATA] != ELFDATA2LSB || header.e_ehsize < sizeof(Elf64_Ehdr)
      || header.e_phentsize < sizeof(Elf64_Phdr) || header.e_shentsize < sizeof(Elf64_Shdr))
    return false;
  uint64_t end = header.e_ehsize;
  if (header.e_phnum != 0) {
    if (header.e_phoff > std::numeric_limits<uint64_t>::max() - uint64_t(header.e_phentsize) * header.e_phnum) return false;
    end = std::max(end, header.e_phoff + uint64_t(header.e_phentsize) * header.e_phnum);
    for (uint16_t index = 0; index < header.e_phnum; ++index) {
      const auto offset = header.e_phoff + uint64_t(index) * header.e_phentsize;
      const auto program = readNative<Elf64_Phdr>(image, offset);
      if (!addFileRange(program.p_offset, program.p_filesz, end)) return false;
    }
  }
  if (header.e_shnum != 0) {
    if (header.e_shoff > std::numeric_limits<uint64_t>::max() - uint64_t(header.e_shentsize) * header.e_shnum) return false;
    end = std::max(end, header.e_shoff + uint64_t(header.e_shentsize) * header.e_shnum);
    for (uint16_t index = 0; index < header.e_shnum; ++index) {
      const auto offset = header.e_shoff + uint64_t(index) * header.e_shentsize;
      const auto section = readNative<Elf64_Shdr>(image, offset);
      if (section.sh_type != SHT_NOBITS && !addFileRange(section.sh_offset, section.sh_size, end)) return false;
    }
  }
  if (end > std::numeric_limits<size_t>::max()) return false;
  size = static_cast<size_t>(end);
  return size >= sizeof(Elf64_Ehdr);
}

std::shared_ptr<Module> findModule(void **token) {
  auto &runtime = state();
  const auto found = runtime.modules.find(token);
  return found == runtime.modules.end() ? std::shared_ptr<Module>{} : found->second;
}

bool loadModule(Module &module) {
  if (module.loaded) return true;
  if (!module.image && module.fatbin) {
    if (!selectBundle(module.fatbin, &module.image, &module.image_size, &module.decompressed_image)) {
      module.error = std::string("HIP fatbinary has no ") + gemstone::hsa::targetArch() + " bundle";
      return false;
    }
  }
  if (!module.image || module.image_size == 0) {
    module.error = "HIP fatbinary has no AMDGPU code object";
    return false;
  }
  const hsa_status_t reader = hsa_code_object_reader_create_from_memory(module.image, module.image_size, &module.reader);
  const hsa_status_t executable =
      reader == HSA_STATUS_SUCCESS
          ? hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_DEFAULT, nullptr, &module.executable)
          : HSA_STATUS_ERROR;
  const hsa_status_t load = executable == HSA_STATUS_SUCCESS
                                ? hsa_executable_load_agent_code_object(module.executable, state().gpu, module.reader, nullptr, nullptr)
                                : HSA_STATUS_ERROR;
  const hsa_status_t reader_destroy = module.reader.handle == 0 ? HSA_STATUS_SUCCESS : hsa_code_object_reader_destroy(module.reader);
  module.reader = {};
  const hsa_status_t freeze = load == HSA_STATUS_SUCCESS ? hsa_executable_freeze(module.executable, nullptr) : HSA_STATUS_ERROR;
  if (reader != HSA_STATUS_SUCCESS || executable != HSA_STATUS_SUCCESS || load != HSA_STATUS_SUCCESS || reader_destroy != HSA_STATUS_SUCCESS
      || freeze != HSA_STATUS_SUCCESS) {
    module.error = "public HSA rejected the HIP code object";
    if (module.executable.handle != 0) hsa_executable_destroy(module.executable);
    module.executable = {};
    return false;
  }
  if (!gemstone::hsa::copyExecutableKernelMetadata(module.executable, module.metadata) || module.metadata.empty()) {
    module.error = "public HSA returned no AMDGPU kernel metadata";
    hsa_executable_destroy(module.executable);
    module.executable = {};
    return false;
  }
  module.loaded = true;
  return true;
}

std::shared_ptr<Kernel> resolveModuleKernel(Module &module, const std::string &name) {
  std::lock_guard<std::mutex> guard(module.lock);
  if (!module.loaded) return {};
  if (const auto cached = module.kernels.find(name); cached != module.kernels.end()) return cached->second;
  gemstone::hsa::ExecutableKernelMetadata metadata;
  if (!gemstone::hsa::copyExecutableKernelMetadata(module.executable, name, metadata)) return {};
  hsa_executable_symbol_t symbol{};
  if (hsa_executable_get_symbol_by_name(module.executable, name.c_str(), &state().gpu, &symbol) != HSA_STATUS_SUCCESS) return {};
  uint64_t object = 0;
  uint32_t kernarg_size = 0;
  if (hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) != HSA_STATUS_SUCCESS
      || hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE, &kernarg_size)
             != HSA_STATUS_SUCCESS)
    return {};
  auto kernel = std::make_shared<Kernel>();
  kernel->object = object;
  kernel->kernarg_size = kernarg_size;
  kernel->explicit_argument_count = metadata.explicit_argument_count;
  kernel->fields = metadata.fields;
  module.kernels.emplace(metadata.name, kernel);
  module.kernels.emplace(metadata.symbol, kernel);
  if (metadata.symbol.size() > 3 && (metadata.symbol ^ ends_with(".kd")))
    module.kernels.emplace(metadata.symbol ^ strip_suffix(".kd"), kernel);
  return kernel;
}

bool ensureModuleLoaded(const std::shared_ptr<Module> &module) {
  if (!module) return false;
  std::lock_guard<std::mutex> guard(module->lock);
  return loadModule(*module);
}

std::shared_ptr<Kernel> resolveKernel(const void *function_address) {
  auto &runtime = state();
  FunctionBinding binding;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.kernels.find(function_address);
    if (found == runtime.kernels.end()) return {};
    binding = found->second;
  }
  if (binding.kernel) return binding.kernel;
  if (!ensureModuleLoaded(binding.module)) return {};
  const auto kernel = resolveModuleKernel(*binding.module, binding.name);
  if (!kernel) return {};
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.kernels.find(function_address);
    if (found != runtime.kernels.end() && found->second.module == binding.module && found->second.name == binding.name)
      found->second.kernel = kernel;
  }
  return kernel;
}

hipError_t dispatchKernel(const std::shared_ptr<Kernel> &kernel, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t stream,
                          const gemstone::hip::LaunchBuffer &launch_buffer = {}, const dim3 *global_work_items = nullptr) {
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  if (!kernel || (!args && !launch_buffer.data && kernel->explicit_argument_count != 0)) {
    if (traceModules())
      std::fprintf(stderr, "Gemstone HIP dispatch rejected kernel=%p args=%p\n", static_cast<const void *>(kernel.get()),
                   static_cast<void *>(args));
    return remember(hipErrorInvalidDeviceFunction);
  }
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP dispatch kernel=%p object=%llu explicit=%u kernarg=%u shared=%zu\n",
                 static_cast<const void *>(kernel.get()), static_cast<unsigned long long>(kernel->object), kernel->explicit_argument_count,
                 kernel->kernarg_size, shared);
  if (grid.x == 0 || grid.y == 0 || grid.z == 0 || grid.x > MaxGridDimension || grid.y > MaxGridDimension || grid.z > MaxGridDimension
      || !validBlock(block)
      || (!global_work_items
          && (grid.x > std::numeric_limits<uint32_t>::max() / block.x || grid.y > std::numeric_limits<uint32_t>::max() / block.y
              || grid.z > std::numeric_limits<uint32_t>::max() / block.z))
      || shared > std::numeric_limits<uint32_t>::max())
    return remember(hipErrorInvalidConfiguration);
  const dim3 global = !global_work_items ? dim3(grid.x * block.x, grid.y * block.y, grid.z * block.z) : *global_work_items;
  if (global.x < block.x || global.y < block.y || global.z < block.z) return remember(hipErrorInvalidConfiguration);
  void *kernarg = nullptr;
  hsa_status_t status =
      kernel->kernarg_size == 0 ? HSA_STATUS_SUCCESS : hsa_memory_allocate(state().kernarg_region, kernel->kernarg_size, &kernarg);
  if (status != HSA_STATUS_SUCCESS) return remember(mapHsa(status));
  if (kernel->kernarg_size != 0) std::memset(kernarg, 0, kernel->kernarg_size);
  if (launch_buffer.data) {
    size_t required = 0;
    for (size_t argument = 0; argument < kernel->explicit_argument_count; ++argument) {
      const auto &field = kernel->fields[argument];
      required = std::max(required, static_cast<size_t>(field.offset) + field.size);
    }
    if (!launch_buffer.copyTo(kernarg, kernel->kernarg_size, required)) {
      hsa_memory_free(kernarg);
      return remember(hipErrorInvalidValue);
    }
  } else {
    size_t argument = 0;
    for (const auto &field : kernel->fields) {
      if (argument >= kernel->explicit_argument_count) break;
      if (field.offset > kernel->kernarg_size || field.size > kernel->kernarg_size - field.offset) {
        hsa_memory_free(kernarg);
        return remember(hipErrorInvalidValue);
      }
      if (args[argument] != nullptr) std::memcpy(static_cast<uint8_t *>(kernarg) + field.offset, args[argument], field.size);
      ++argument;
    }
  }
  const auto overlaps_explicit = [&](uint32_t offset, uint32_t size) {
    size_t explicit_fields = 0;
    for (const auto &field : kernel->fields) {
      if (explicit_fields >= kernel->explicit_argument_count) break;
      ++explicit_fields;
      if (field.offset < offset + size && offset < field.offset + field.size) return true;
    }
    return false;
  };
  const auto write_u16 = [&](uint32_t offset, uint32_t value) {
    if (!overlaps_explicit(offset, sizeof(uint16_t)) && offset + sizeof(uint16_t) <= kernel->kernarg_size) {
      const auto narrowed = static_cast<uint16_t>(value);
      std::memcpy(static_cast<uint8_t *>(kernarg) + offset, &narrowed, sizeof(narrowed));
    }
  };
  const auto write_u32 = [&](uint32_t offset, uint32_t value) {
    if (!overlaps_explicit(offset, sizeof(uint32_t)) && offset + sizeof(uint32_t) <= kernel->kernarg_size)
      std::memcpy(static_cast<uint8_t *>(kernarg) + offset, &value, sizeof(value));
  };
  // Code object V5 lays out the implicit dispatch arguments immediately
  // after the explicit arguments: block counts, group sizes, remainders,
  // reserved words, global offsets, and finally the grid dimensionality.
  // Older V4 objects use a different all-64-bit implicit layout; their slots
  // are already zeroed above, which is the only offset configuration this
  // virtual device currently exposes.
  if (kernel->explicit_argument_count < kernel->fields.size()) {
    const auto &first_hidden = kernel->fields[kernel->explicit_argument_count];
    if (first_hidden.size == sizeof(uint32_t)) {
      const uint32_t hidden = first_hidden.offset;
      // V5 counts complete groups separately from the work-items in a partial final group.
      write_u32(hidden + 0, global.x / block.x);
      write_u32(hidden + 4, global.y / block.y);
      write_u32(hidden + 8, global.z / block.z);
      write_u16(hidden + 12, static_cast<uint32_t>(block.x));
      write_u16(hidden + 14, static_cast<uint32_t>(block.y));
      write_u16(hidden + 16, static_cast<uint32_t>(block.z));
      write_u16(hidden + 18, static_cast<uint32_t>(global.x % block.x));
      write_u16(hidden + 20, static_cast<uint32_t>(global.y % block.y));
      write_u16(hidden + 22, static_cast<uint32_t>(global.z % block.z));
      write_u16(hidden + 64, gemstone::hip::dispatchDimensions(grid, block));
    }
  }
  auto owned = std::shared_ptr<void>(kernarg, [](void *memory) {
    if (memory) (void)hsa_memory_free(memory);
  });
  return remember(enqueueCommand(stream, [=]() mutable -> int32_t {
    hsa_queue_t *dispatchQueue{};
    if (hsa_queue_create(state().gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &dispatchQueue)
        != HSA_STATUS_SUCCESS)
      return hipErrorOutOfMemory;
    const auto queueOwner = std::shared_ptr<hsa_queue_t>(dispatchQueue, [](hsa_queue_t *queue) { (void)hsa_queue_destroy(queue); });
    hsa_signal_t completion{};
    hsa_status_t status = hsa_signal_create(1, 0, nullptr, &completion);
    if (status == HSA_STATUS_SUCCESS) {
      const uint64_t index = hsa_queue_add_write_index_relaxed(dispatchQueue, 1);
      auto *packet = &static_cast<hsa_kernel_dispatch_packet_t *>(dispatchQueue->base_address)[index & (dispatchQueue->size - 1U)];
      // Keep the slot unpublished while its body is rebuilt.  Clearing the
      // header with memset after publishing the write index briefly turns the
      // slot into a vendor-specific (type 0) packet, which the doorbell worker
      // is allowed to consume before the final dispatch header is released.
      std::memset(reinterpret_cast<uint8_t *>(packet) + sizeof(packet->header), 0, sizeof(*packet) - sizeof(packet->header));
      packet->setup = gemstone::hip::dispatchDimensions(grid, block) << HSA_KERNEL_DISPATCH_PACKET_SETUP_DIMENSIONS;
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
      if (hsa_signal_load_scacquire(completion) < 0) status = HSA_STATUS_ERROR;
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
    const hsa_status_t free_status = HSA_STATUS_SUCCESS;
    const hsa_status_t destroy_status = hsa_signal_destroy(completion);
    if (status != HSA_STATUS_SUCCESS) {
      if (traceModules()) std::fprintf(stderr, "Gemstone HIP dispatch HSA status=%d\n", static_cast<int>(status));
      return remember(hipErrorLaunchFailure);
    }
    if (free_status != HSA_STATUS_SUCCESS || destroy_status != HSA_STATUS_SUCCESS)
      return remember(mapHsa(free_status != HSA_STATUS_SUCCESS ? free_status : destroy_status));
    return remember(hipSuccess);
  }));
}

hipError_t dispatch(const void *function_address, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t stream) {
  const auto kernel = resolveKernel(function_address);
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP direct launch host=%p kernel=%p\n", function_address, static_cast<const void *>(kernel.get()));
  return dispatchKernel(kernel, grid, block, args, shared, stream);
}

FunctionHandle *functionHandle(hipFunction_t function) { return reinterpret_cast<FunctionHandle *>(function); }

ModuleHandle *moduleHandle(hipModule_t module) { return reinterpret_cast<ModuleHandle *>(module); }

bool traceModules() {
  static const bool enabled = traceHip("modules");
  return enabled;
}

std::shared_ptr<Module> createModule(const void *input) {
  auto module = std::make_shared<Module>();
  const void *image = nullptr;
  size_t image_size = 0;
  if (imageIsElf(input)) {
    if (!elfImageSize(input, image_size)) {
      module->error = "invalid ELF64 AMDGPU code object";
      return module;
    }
    image = input;
  } else if (!selectBundle(input, &image, &image_size, &module->decompressed_image)) {
    module->error = std::string("HIP module has no ") + gemstone::hsa::targetArch() + " code object";
    return module;
  }
  module->image = image;
  module->image_size = image_size;
  if (!loadModule(*module)) return module;
  return module;
}

uint32_t workgroupCount(uint32_t global, uint32_t local) { return local == 0 ? 0 : global / local + (global % local == 0 ? 0 : 1); }

} // namespace

extern "C" hipError_t hipInit(unsigned int flags) {
  return remember(flags == 0 && ensureInitialized() ? hipSuccess : (flags != 0 ? hipErrorInvalidValue : hipErrorNoDevice));
}

extern "C" hipError_t hipDriverGetVersion(int *version) {
  if (!version) return remember(hipErrorInvalidValue);
  *version = HIP_VERSION;
  return remember(hipSuccess);
}

extern "C" hipError_t hipRuntimeGetVersion(int *version) {
  if (!version) return remember(hipErrorInvalidValue);
  *version = HIP_VERSION;
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetDeviceCount(int *count) {
  if (!count) return remember(hipErrorInvalidValue);
  *count = ensureInitialized() ? 1 : 0;
  return remember(*count == 1 ? hipSuccess : hipErrorNoDevice);
}

extern "C" hipError_t hipGetDevice(int *device) {
  if (!device) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  *device = 0;
  return remember(hipSuccess);
}

extern "C" hipError_t hipSetDevice(int device) { return remember(device == 0 && ensureInitialized() ? hipSuccess : hipErrorInvalidDevice); }

namespace {
unsigned char primary_context_token;
thread_local hipCtx_t current_context = nullptr;
thread_local std::vector<hipCtx_t> context_stack;

hipCtx_t primaryContext() { return reinterpret_cast<hipCtx_t>(&primary_context_token); }
} // namespace

extern "C" hipError_t hipDevicePrimaryCtxRetain(hipCtx_t *context, hipDevice_t device) {
  if (!context || device != 0) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (runtime.primary_context_references == UINT32_MAX) return remember(hipErrorOutOfMemory);
  ++runtime.primary_context_references;
  *context = primaryContext();
  return remember(hipSuccess);
}

extern "C" hipError_t hipDevicePrimaryCtxRelease(hipDevice_t device) {
  if (device != 0) return remember(hipErrorInvalidDevice);
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (runtime.primary_context_references == 0) return remember(hipErrorInvalidValue);
  --runtime.primary_context_references;
  return remember(hipSuccess);
}

extern "C" hipError_t hipCtxPushCurrent(hipCtx_t context) {
  if (context != primaryContext()) return remember(hipErrorInvalidContext);
  try {
    context_stack.push_back(current_context);
  } catch (...) {
    return remember(hipErrorOutOfMemory);
  }
  current_context = context;
  return remember(hipSuccess);
}

extern "C" hipError_t hipCtxPopCurrent(hipCtx_t *context) {
  if (!context || context_stack.empty()) return remember(hipErrorInvalidContext);
  *context = current_context;
  current_context = context_stack.back();
  context_stack.pop_back();
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGet(hipDevice_t *device, int ordinal) {
  if (!device) return remember(hipErrorInvalidValue);
  if (ordinal != 0 || !ensureInitialized()) return remember(hipErrorInvalidDevice);
  *device = 0;
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceSynchronize() { return remember(ensureInitialized() ? waitCommands() : hipErrorNoDevice); }
extern "C" hipError_t hipDeviceReset() {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  auto &runtime = state();
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  std::lock_guard<std::mutex> guard(runtime.lock);
  for (const auto &[pointer, _] : runtime.device_allocations)
    (void)hsa_memory_free(pointer);
  runtime.device_allocations.clear();
  runtime.managed_allocations.clear();
  runtime.streams.clear();
  runtime.events.clear();
  for (auto &module : runtime.driver_modules)
    if (auto live = module.lock()) live->reset();
  runtime.kernels.clear();
  runtime.variables.clear();
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceComputeCapability(int *major, int *minor, hipDevice_t device) {
  if (!major || !minor || device != 0) return remember(hipErrorInvalidValue);
  const auto version = gemstone::hsa::targetVersion();
  *major = static_cast<int>(version.major);
  *minor = static_cast<int>(version.minor);
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGetName(char *name, int length, hipDevice_t device) {
  if (!name || length <= 0 || device != 0) return remember(hipErrorInvalidValue);
  const std::string value = std::string("Gemstone ") + gemstone::hsa::targetArch();
  std::strncpy(name, value.c_str(), static_cast<size_t>(length));
  name[length - 1] = '\0';
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceTotalMem(size_t *bytes, hipDevice_t device) {
  if (!bytes || device != 0) return remember(hipErrorInvalidValue);
  *bytes = DeviceTotalMemoryBytes;
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemGetInfo(size_t *free, size_t *total) {
  if (!free || !total) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  *total = DeviceTotalMemoryBytes;
  *free = *total;
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGetDefaultMemPool(hipMemPool_t *pool, int device) {
  if (!pool || device != 0) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
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
  if (!value || device != 0) return remember(hipErrorInvalidValue);
  switch (attribute) {
    case hipDeviceAttributeMaxThreadsPerBlock: *value = MaxBlockThreads; break;
    case hipDeviceAttributeWarpSize: *value = DeviceWarpSize; break;
    case hipDeviceAttributeMaxBlockDimX: *value = MaxBlockThreads; break;
    case hipDeviceAttributeMaxBlockDimY: *value = MaxBlockThreads; break;
    case hipDeviceAttributeMaxBlockDimZ: *value = MaxBlockThreads; break;
    case hipDeviceAttributeMaxGridDimX: *value = static_cast<int>(MaxGridDimension); break;
    case hipDeviceAttributeMaxGridDimY: *value = static_cast<int>(MaxGridDimension); break;
    case hipDeviceAttributeMaxGridDimZ: *value = static_cast<int>(MaxGridDimension); break;
    case hipDeviceAttributeMaxThreadsPerMultiProcessor: *value = static_cast<int>(DeviceThreadsPerMultiprocessor); break;
    case hipDeviceAttributeMaxSharedMemoryPerMultiprocessor: *value = static_cast<int>(DeviceSharedMemoryPerBlock); break;
    case hipDeviceAttributeTotalGlobalMem: *value = static_cast<int>(DeviceTotalMemoryBytes); break;
    case hipDeviceAttributeUnifiedAddressing:
    case hipDeviceAttributeCanMapHostMemory:
    case hipDeviceAttributeHostNativeAtomicSupported:
    case hipDeviceAttributeConcurrentManagedAccess:
    case hipDeviceAttributeManagedMemory: *value = 1; break;
    case hipDeviceAttributePageableMemoryAccess:
      *value = gemstone::environment::enabled(gemstone::environment::SystemPaging) ? 1 : 0;
      break;
    default: *value = 0; break;
  }
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetDevicePropertiesR0600(hipDeviceProp_tR0600 *properties, int device) {
  if (!properties || device != 0) return remember(hipErrorInvalidValue);
  std::memset(properties, 0, sizeof(*properties));
  const std::string target = gemstone::hsa::targetArch();
  const std::string name = std::string("Gemstone ") + target;
  std::strncpy(properties->name, name.c_str(), sizeof(properties->name) - 1U);
  std::strncpy(properties->gcnArchName, target.c_str(), sizeof(properties->gcnArchName) - 1U);
  properties->totalGlobalMem = DeviceTotalMemoryBytes;
  properties->sharedMemPerBlock = DeviceSharedMemoryPerBlock;
  properties->warpSize = static_cast<int>(DeviceWarpSize);
  properties->maxThreadsPerBlock = MaxBlockThreads;
  properties->maxThreadsPerMultiProcessor = static_cast<int>(DeviceThreadsPerMultiprocessor);
  properties->maxSharedMemoryPerMultiProcessor = DeviceSharedMemoryPerBlock;
  properties->maxThreadsDim[0] = MaxBlockThreads;
  properties->maxThreadsDim[1] = MaxBlockThreads;
  properties->maxThreadsDim[2] = MaxBlockThreads;
  properties->maxGridSize[0] = MaxGridDimension;
  properties->maxGridSize[1] = MaxGridDimension;
  properties->maxGridSize[2] = MaxGridDimension;
  const auto version = gemstone::hsa::targetVersion(target.c_str());
  properties->major = static_cast<int>(version.major);
  properties->minor = static_cast<int>(version.minor);
  properties->multiProcessorCount = 1;
  properties->unifiedAddressing = 1;
  properties->canMapHostMemory = 1;
  properties->managedMemory = 1;
  properties->hostNativeAtomicSupported = 1;
  properties->concurrentManagedAccess = 1;
  properties->pageableMemoryAccess = gemstone::environment::enabled(gemstone::environment::SystemPaging) ? 1 : 0;
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
constexpr size_t RegsPerBlock = 272;
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
constexpr size_t ManagedMemory = 772;
constexpr size_t ConcurrentManagedAccess = 780;
constexpr size_t PageableMemoryAccess = 784;
constexpr size_t Size = 792;
static_assert(Name + NameSize <= TotalGlobalMem, "legacy name overlaps totalGlobalMem");
static_assert(MaxGridSize + 3 * sizeof(int) <= Major, "legacy layout overlap");
static_assert(MaxSharedMemoryPerMultiProcessor + sizeof(size_t) <= GcnArch, "legacy layout overlap");
static_assert(PageableMemoryAccess + sizeof(int) <= Size, "legacy layout out of bounds");
} // namespace legacy_properties

#undef hipGetDeviceProperties
extern "C" GEMSTONE_PUBLIC_API hipError_t hipGetDeviceProperties(void *properties, int device) {
  using namespace legacy_properties;
  if (!properties || device != 0) return remember(hipErrorInvalidValue);
  if (sizeof(size_t) != 8) return remember(hipErrorNotSupported);
  auto *bytes = static_cast<unsigned char *>(properties);
  std::memset(bytes, 0, Size);
  const std::string target = gemstone::hsa::targetArch();
  const std::string name = std::string("Gemstone ") + target;
  std::strncpy(reinterpret_cast<char *>(bytes + Name), name.c_str(), NameSize - 1);
  std::strncpy(reinterpret_cast<char *>(bytes + ArchName), target.c_str(), 255);
  const auto write = [bytes](size_t offset, auto value) { std::memcpy(bytes + offset, &value, sizeof(value)); };
  write(TotalGlobalMem, DeviceTotalMemoryBytes);
  write(SharedMemPerBlock, DeviceSharedMemoryPerBlock);
  write(WarpSize, static_cast<int>(DeviceWarpSize));
  write(MaxThreadsPerBlock, int(MaxBlockThreads));
  for (size_t axis = 0; axis < 3; ++axis) {
    write(MaxThreadsDim + axis * sizeof(int), int(MaxBlockThreads));
    write(MaxGridSize + axis * sizeof(int), int(MaxGridDimension));
  }
  const auto version = gemstone::hsa::targetVersion(target.c_str());
  write(Major, int(version.major));
  write(Minor, int(version.minor));
  write(MultiProcessorCount, int(1));
  write(Arch, uint32_t((1U << 5) | (1U << 7))); // global int64 atomics and doubles
  write(MaxSharedMemoryPerMultiProcessor, DeviceSharedMemoryPerBlock);
  write(GcnArch, int(std::atoi(target.c_str() + 3)));
  write(ManagedMemory, int(1));
  write(ConcurrentManagedAccess, int(1));
  write(PageableMemoryAccess, int(gemstone::environment::enabled(gemstone::environment::SystemPaging) ? 1 : 0));
  return remember(hipSuccess);
}

extern "C" hipError_t hipMalloc(void **pointer, size_t size) {
  if (!pointer) return remember(hipErrorInvalidValue);
  if (size == 0) {
    *pointer = nullptr;
    return remember(hipSuccess);
  }
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  const hsa_status_t status = hsa_memory_allocate(state().kernarg_region, size, pointer);
  if (traceHip("allocations"))
    std::fprintf(stderr, "Gemstone HIP malloc size=%zu status=%d pointer=%p\n", size, int(status), !pointer ? nullptr : *pointer);
  if (status == HSA_STATUS_SUCCESS) {
    std::lock_guard<std::mutex> guard(state().lock);
    state().device_allocations.emplace(*pointer, size);
  }
  return remember(mapHsa(status));
}

extern "C" hipError_t hipFree(void *pointer) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!pointer) return remember(hipSuccess);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  const hsa_status_t status = hsa_memory_free(pointer);
  if (status == HSA_STATUS_SUCCESS) {
    std::lock_guard<std::mutex> guard(state().lock);
    state().managed_allocations.erase(pointer);
    state().device_allocations.erase(pointer);
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
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  const hsa_status_t status = hsa_memory_copy(destination, source, size);
  return remember(mapHsa(status));
}

hipError_t copySymbol(void *destination, const void *source, size_t size, size_t offset) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if ((!destination || !source) && size != 0) return remember(hipErrorInvalidValue);
  std::shared_ptr<Module> module;
  std::string device_name;
  {
    std::lock_guard<std::mutex> guard(state().lock);
    const auto found = state().variables.find(destination);
    if (found == state().variables.end() || offset > found->second.size || size > found->second.size - offset)
      return remember(hipErrorInvalidSymbol);
    if (size != 0) std::memcpy(static_cast<unsigned char *>(found->second.host) + offset, source, size);
    module = found->second.module;
    device_name = found->second.device_name;
  }
  if (!ensureModuleLoaded(module)) return remember(hipErrorInvalidImage);
  hsa_executable_t executable{};
  {
    std::lock_guard<std::mutex> guard(module->lock);
    executable = module->executable;
  }
  if (executable.handle == 0) return remember(hipErrorInvalidSymbol);
  const hsa_status_t status = gemstone_hsa_executable_write_global(executable, device_name.c_str(), source, size, offset);
  return remember(mapHsa(status));
}

const void *symbolSource(const void *symbol, size_t size, size_t offset, hipError_t &error) {
  if (!symbol && size != 0) {
    error = hipErrorInvalidValue;
    return nullptr;
  }
  std::lock_guard<std::mutex> guard(state().lock);
  const auto found = state().variables.find(symbol);
  if (found == state().variables.end() || offset > found->second.size || size > found->second.size - offset) {
    error = hipErrorInvalidSymbol;
    return nullptr;
  }
  error = hipSuccess;
  return static_cast<const unsigned char *>(found->second.host) + offset;
}

extern "C" hipError_t hipMemcpyToSymbol(const void *symbol, const void *source, size_t size, size_t offset, hipMemcpyKind) {
  return copySymbol(const_cast<void *>(symbol), source, size, offset);
}

extern "C" hipError_t hipMemcpyToSymbolAsync(const void *symbol, const void *source, size_t size, size_t offset, hipMemcpyKind kind,
                                             hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipMemcpyToSymbol(symbol, source, size, offset, kind); }));
}

extern "C" hipError_t hipMemcpyFromSymbol(void *destination, const void *symbol, size_t size, size_t offset, hipMemcpyKind kind) {
  hipError_t error = hipSuccess;
  const void *source = symbolSource(symbol, size, offset, error);
  if (error != hipSuccess) return remember(error);
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
extern "C" hipError_t hipMemcpy2D(void *destination, size_t destination_pitch, const void *source, size_t source_pitch, size_t width,
                                  size_t height, hipMemcpyKind kind) {
  if ((!destination || !source) && width != 0 && height != 0) return remember(hipErrorInvalidValue);
  for (size_t row = 0; row < height; ++row) {
    const auto *source_row = static_cast<const unsigned char *>(source) + row * source_pitch;
    auto *destination_row = static_cast<unsigned char *>(destination) + row * destination_pitch;
    const hipError_t status = hipMemcpy(destination_row, source_row, width, kind);
    if (status != hipSuccess) return status;
  }
  return remember(hipSuccess);
}
extern "C" hipError_t hipMemcpy2DAsync(void *destination, size_t destination_pitch, const void *source, size_t source_pitch, size_t width,
                                       size_t height, hipMemcpyKind kind, hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(
      enqueueCommand(stream, [=] { return hipMemcpy2D(destination, destination_pitch, source, source_pitch, width, height, kind); }));
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
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  hsa_agent_t cpu{};
  hsa_region_t region{};
  if (hsa_iterate_agents(findCpu, &cpu) != HSA_STATUS_INFO_BREAK
      || hsa_agent_iterate_regions(cpu, findRegion, &region) != HSA_STATUS_INFO_BREAK)
    return remember(hipErrorNoDevice);
  const auto status = hsa_memory_allocate(region, size, pointer);
  if (status != HSA_STATUS_SUCCESS) return remember(mapHsa(status));
  try {
    std::lock_guard<std::mutex> guard(state().lock);
    state().device_allocations.emplace(*pointer, size);
    state().managed_allocations.emplace(*pointer, size);
  } catch (...) {
    (void)hipFree(*pointer);
    *pointer = nullptr;
    return remember(hipErrorOutOfMemory);
  }
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemAdvise(const void *pointer, size_t size, hipMemoryAdvise advice, int device) {
  if ((!pointer && size != 0) || device != 0) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);

  // HIP's StdPar allocator obtains ordinary host pages and then marks them
  // as device-accessible with SetAccessedBy.  The emulator must register
  // those pages with the HSA facade before an AQL dispatch can resolve the
  // pointer.  The matching unset advice is issued before the allocator
  // returns the pages to libc; deregister it first so the facade never keeps
  // a dangling host allocation.
  switch (advice) {
    case hipMemAdviseSetAccessedBy: {
      if (!pointer || size == 0) return remember(hipErrorInvalidValue);
      const hsa_status_t status = hsa_memory_register(const_cast<void *>(pointer), size);
      return remember(status == HSA_STATUS_ERROR_INVALID_ALLOCATION ? hipSuccess : mapHsa(status));
    }
    case hipMemAdviseUnsetAccessedBy: {
      if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
      if (!pointer) return remember(hipErrorInvalidValue);
      const hsa_status_t status = hsa_memory_deregister(const_cast<void *>(pointer), 0);
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
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  const gemstone::MemoryRange range{reinterpret_cast<uintptr_t>(pointer), size};
  if (!range.valid() || (flags & ~(hipHostRegisterPortable | hipHostRegisterMapped | hipHostRegisterIoMemory | hipHostRegisterReadOnly)))
    return remember(hipErrorInvalidValue);
  std::lock_guard<std::mutex> guard(state().lock);
  if (state().host_registrations.overlaps(range)) return remember(hipErrorHostMemoryAlreadyRegistered);
  const auto status = hsa_memory_register(pointer, size);
  if (status == HSA_STATUS_SUCCESS) state().host_registrations.add(range, gemstone::MemoryOwnership::Borrowed, flags);
  return remember(mapHsa(status));
}
extern "C" hipError_t hipHostUnregister(void *pointer) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  std::lock_guard<std::mutex> guard(state().lock);
  const auto found = state().host_registrations.exact(reinterpret_cast<uintptr_t>(pointer));
  if (!found || found->ownership != gemstone::MemoryOwnership::Borrowed) return remember(hipErrorHostMemoryNotRegistered);
  const auto status = hsa_memory_deregister(pointer, found->range.size);
  if (status == HSA_STATUS_SUCCESS)
    state().host_registrations.remove(reinterpret_cast<uintptr_t>(pointer), gemstone::MemoryOwnership::Borrowed);
  return remember(mapHsa(status));
}
extern "C" hipError_t hipHostGetDevicePointer(void **device, void *host, unsigned flags) {
  if (!device || !host || flags != 0) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  std::lock_guard<std::mutex> guard(state().lock);
  if (!state().host_registrations.find(reinterpret_cast<uintptr_t>(host))) return remember(hipErrorInvalidValue);
  *device = host;
  return remember(hipSuccess);
}
extern "C" hipError_t hipHostGetFlags(unsigned *flags, void *host) {
  if (!flags || !host) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  std::lock_guard<std::mutex> guard(state().lock);
  const auto found = state().host_registrations.find(reinterpret_cast<uintptr_t>(host));
  if (!found) return remember(hipErrorInvalidValue);
  *flags = static_cast<unsigned>(found->flags);
  return remember(hipSuccess);
}

extern "C" hipError_t hipPointerGetAttributes(hipPointerAttribute_t *attributes, const void *pointer) {
  if (!attributes || !pointer) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  hsa_amd_pointer_info_t info{};
  info.size = sizeof(info);
  const auto status = hsa_amd_pointer_info(pointer, &info, nullptr, nullptr, nullptr);
  if (status != HSA_STATUS_SUCCESS) return remember(mapHsa(status));
  if (info.type == HSA_EXT_POINTER_TYPE_UNKNOWN) return remember(hipErrorInvalidValue);
  hsa_device_type_t owner{};
  if (hsa_agent_get_info(info.agentOwner, HSA_AGENT_INFO_DEVICE, &owner) != HSA_STATUS_SUCCESS) return remember(hipErrorInvalidDevice);
  std::memset(attributes, 0, sizeof(*attributes));
  attributes->type = owner == HSA_DEVICE_TYPE_CPU ? hipMemoryTypeHost : hipMemoryTypeDevice;
  attributes->device = 0;
  attributes->devicePointer = const_cast<void *>(pointer);
  attributes->hostPointer = owner == HSA_DEVICE_TYPE_CPU ? const_cast<void *>(pointer) : nullptr;
  {
    std::lock_guard<std::mutex> guard(state().lock);
    for (const auto &[base, size] : state().managed_allocations) {
      const auto address = reinterpret_cast<uintptr_t>(pointer);
      const auto begin = reinterpret_cast<uintptr_t>(base);
      if (address >= begin && address - begin < size) {
        attributes->isManaged = 1;
        attributes->type = hipMemoryTypeManaged;
        attributes->hostPointer = const_cast<void *>(pointer);
        break;
      }
    }
  }
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemset(void *destination, int value, size_t size) {
  if (const auto pending = waitCommands(); pending != hipSuccess) return remember(pending);
  if (!destination && size != 0) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  const unsigned char byte = static_cast<unsigned char>(value);
  if (size % 4U == 0) return remember(mapHsa(hsa_amd_memory_fill(destination, uint32_t(byte) * 0x01010101U, size / 4U)));
  std::vector<unsigned char> bytes(size, byte);
  return hipMemcpy(destination, bytes.data(), size, hipMemcpyHostToDevice);
}
extern "C" hipError_t hipMemsetD8(hipDeviceptr_t destination, unsigned char value, size_t count) {
  return hipMemset(reinterpret_cast<void *>(destination), value, count);
}
extern "C" hipError_t hipMemsetD32(hipDeviceptr_t destination, int value, size_t count) {
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  return remember(mapHsa(hsa_amd_memory_fill(reinterpret_cast<void *>(destination), static_cast<uint32_t>(value), count)));
}
extern "C" hipError_t hipMemsetAsync(void *destination, int value, size_t size, hipStream_t stream) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  return remember(enqueueCommand(stream, [=] { return hipMemset(destination, value, size); }));
}

extern "C" GEMSTONE_PUBLIC_API hipError_t __hipPushCallConfiguration(dim3 grid, dim3 block, size_t shared, hipStream_t stream) {
  launchConfiguration = LaunchConfiguration{grid, block, shared, stream};
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
extern "C" hipError_t hipLaunchKernel(const void *function_address, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t stream) {
  return dispatch(function_address, grid, block, args, shared, stream);
}

extern "C" hipError_t hipFuncGetAttributes(struct hipFuncAttributes *attributes, const void *function_address) {
  if (!attributes || !function_address) return remember(hipErrorInvalidValue);
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  if (!resolveKernel(function_address)) return remember(hipErrorInvalidDeviceFunction);
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
    default: return "hipErrorUnknown";
  }
}
extern "C" const char *hipGetErrorString(hipError_t error) { return hipGetErrorName(error); }

extern "C" hipError_t hipStreamCreate(hipStream_t *stream) {
  if (!stream) return remember(hipErrorInvalidValue);
  auto storage = std::unique_ptr<unsigned char>(new (std::nothrow) unsigned char);
  if (!storage) return remember(hipErrorOutOfMemory);
  *stream = reinterpret_cast<hipStream_t>(storage.get());
  {
    std::lock_guard<std::mutex> guard(state().lock);
    state().streams.emplace(*stream, std::move(storage));
  }
  return remember(hipSuccess);
}
extern "C" hipError_t hipStreamCreateWithFlags(hipStream_t *stream, unsigned int flags) {
  const auto status = hipStreamCreate(stream);
  if (status == hipSuccess) commands().configure(*stream, (flags & hipStreamNonBlocking) != 0);
  return status;
}
extern "C" hipError_t hipStreamCreateWithPriority(hipStream_t *stream, unsigned int, int) { return hipStreamCreate(stream); }
extern "C" hipError_t hipStreamDestroy(hipStream_t stream) {
  if (const auto pending = static_cast<hipError_t>(commands().wait(commands().snapshot(stream))); pending != hipSuccess)
    return remember(pending);
  if (stream && stream != hipStreamLegacy && stream != hipStreamPerThread) {
    std::lock_guard<std::mutex> guard(state().lock);
    if (state().streams.erase(stream) != 1) return remember(hipErrorInvalidResourceHandle);
    commands().forget(stream);
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
  std::lock_guard<std::mutex> guard(state().lock);
  const auto found = state().events.find(event);
  if (found == state().events.end()) return remember(hipErrorInvalidResourceHandle);
  commands().submit(stream, [] { return hipSuccess; }, {found->second->ticket});
  return remember(hipSuccess);
}
extern "C" hipError_t hipStreamGetDevice(hipStream_t stream, hipDevice_t *device) {
  if (!device) return remember(hipErrorInvalidValue);
  if (stream == hipStreamLegacy || stream == hipStreamPerThread) return remember(hipErrorInvalidResourceHandle);
  if (stream) {
    std::lock_guard<std::mutex> guard(state().lock);
    if (state().streams.count(stream) != 1) return remember(hipErrorInvalidResourceHandle);
  }
  *device = 0;
  return remember(hipSuccess);
}
extern "C" int hipGetStreamDeviceId(hipStream_t) { return 0; }

extern "C" hipError_t hipStreamBeginCapture(hipStream_t, hipStreamCaptureMode) { return remember(hipErrorNotSupported); }
extern "C" hipError_t hipStreamEndCapture(hipStream_t, hipGraph_t *graph) {
  if (graph) *graph = nullptr;
  return remember(hipErrorNotSupported);
}
extern "C" hipError_t hipStreamIsCapturing(hipStream_t, hipStreamCaptureStatus *status) {
  if (!status) return remember(hipErrorInvalidValue);
  *status = hipStreamCaptureStatusNone;
  return remember(hipSuccess);
}
extern "C" hipError_t hipGraphDestroy(hipGraph_t) { return remember(hipErrorNotSupported); }
extern "C" hipError_t hipGraphExecDestroy(hipGraphExec_t) { return remember(hipErrorNotSupported); }
extern "C" hipError_t hipGraphInstantiate(hipGraphExec_t *graph_exec, hipGraph_t, hipGraphNode_t *, char *, size_t) {
  if (graph_exec) *graph_exec = nullptr;
  return remember(hipErrorNotSupported);
}
extern "C" hipError_t hipGraphLaunch(hipGraphExec_t, hipStream_t) { return remember(hipErrorNotSupported); }

extern "C" hipError_t hipModuleLoadData(hipModule_t *module, const void *image) {
  if (!module || !image) return remember(hipErrorInvalidValue);
  *module = nullptr;
  if (!ensureInitialized()) return remember(hipErrorNoDevice);
  auto loaded = createModule(image);
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP module load image=%p loaded=%d kernels=%zu error=%s\n", image, loaded->loaded ? 1 : 0,
                 loaded->kernels.size(), loaded->error.c_str());
  if (!loaded->loaded) return remember(hipErrorInvalidImage);
  auto *handle = new (std::nothrow) ModuleHandle{std::move(loaded)};
  if (!handle) return remember(hipErrorOutOfMemory);
  {
    std::lock_guard<std::mutex> guard(state().lock);
    state().driver_modules.push_back(handle->module);
  }
  *module = reinterpret_cast<hipModule_t>(handle);
  if (traceModules()) std::fprintf(stderr, "Gemstone HIP module handle=%p\n", static_cast<void *>(handle));
  return remember(hipSuccess);
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
  auto kernel = resolveModuleKernel(*selected, name);
  if (!kernel) {
    std::vector<std::weak_ptr<Module>> candidates;
    {
      std::lock_guard<std::mutex> guard(state().lock);
      candidates = state().driver_modules;
    }
    std::vector<std::weak_ptr<Module>> live;
    for (auto &candidate : candidates) {
      auto module = candidate.lock();
      if (!module) continue;
      live.push_back(candidate);
      const auto global = resolveModuleKernel(*module, name);
      if (global && !kernel) {
        selected = module;
        kernel = global;
      }
    }
    {
      std::lock_guard<std::mutex> guard(state().lock);
      // Keep modules registered while this thread was resolving.
      for (const auto &entry : state().driver_modules) {
        const bool present =
            std::any_of(live.begin(), live.end(), [&](const auto &kept) { return !kept.owner_before(entry) && !entry.owner_before(kept); });
        if (!present) live.push_back(entry);
      }
      state().driver_modules = std::move(live);
    }
  }
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP module function module=%p name=%s found=%d\n", static_cast<void *>(handle), name, kernel ? 1 : 0);
  if (traceModules() && !kernel) {
    size_t shown = 0;
    const std::string requested(name);
    const size_t separator = requested.find("_FSSC");
    const std::string prefix = requested.substr(0, separator);
    for (const auto &candidate : selected->metadata) {
      if (!(candidate.name ^ starts_with(prefix))) continue;
      std::fprintf(stderr, "Gemstone HIP nearby module function=%s\n", candidate.name.c_str());
      if (++shown == 8) break;
    }
  }
  if (!kernel) return remember(hipErrorInvalidDeviceFunction);
  auto *result = new (std::nothrow) FunctionHandle{std::move(selected), std::move(kernel)};
  if (!result) return remember(hipErrorOutOfMemory);
  *function = reinterpret_cast<hipFunction_t>(result);
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleLaunchKernel(hipFunction_t function, unsigned int grid_x, unsigned int grid_y, unsigned int grid_z,
                                            unsigned int block_x, unsigned int block_y, unsigned int block_z, unsigned int shared,
                                            hipStream_t stream, void **args, void **extra) {
  if (!function) return remember(hipErrorInvalidDeviceFunction);
  auto *function_state = functionHandle(function);
  if (!function_state->module || !function_state->module->loaded) return remember(hipErrorInvalidResourceHandle);
  gemstone::hip::LaunchBuffer launch_buffer;
  if (!gemstone::hip::parseLaunchBuffer(extra, launch_buffer)) return remember(hipErrorInvalidValue);
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP module launch f=%p grid=%u,%u,%u block=%u,%u,%u args=%p\n", function, grid_x, grid_y, grid_z,
                 block_x, block_y, block_z, static_cast<void *>(args));
  return dispatchKernel(function_state->kernel, dim3(grid_x, grid_y, grid_z), dim3(block_x, block_y, block_z), args, shared, stream,
                        launch_buffer);
}

extern "C" hipError_t hipModuleGetFunctionCount(unsigned int *count, hipModule_t module) {
  if (!count || !module) return remember(hipErrorInvalidValue);
  auto *handle = moduleHandle(module);
  if (!handle->module) return remember(hipErrorInvalidResourceHandle);
  *count = static_cast<unsigned int>(handle->module->metadata.size());
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(int *blocks, hipFunction_t function, int, size_t) {
  if (!blocks || !function) return remember(hipErrorInvalidValue);
  if (!functionHandle(function)->kernel || !functionHandle(function)->module->loaded) return remember(hipErrorInvalidDeviceFunction);
  *blocks = 1;
  return remember(hipSuccess);
}
extern "C" GEMSTONE_PUBLIC_API hipError_t hipExtModuleLaunchKernel(hipFunction_t function, uint32_t global_x, uint32_t global_y,
                                                                   uint32_t global_z, uint32_t local_x, uint32_t local_y, uint32_t local_z,
                                                                   size_t shared, hipStream_t stream, void **args, void **extra, hipEvent_t,
                                                                   hipEvent_t, uint32_t) {
  if (!function) return remember(hipErrorInvalidDeviceFunction);
  auto *function_state = functionHandle(function);
  if (!function_state->module || !function_state->module->loaded) return remember(hipErrorInvalidResourceHandle);
  if (global_x == 0 || global_y == 0 || global_z == 0 || global_x > MaxGridDimension || global_y > MaxGridDimension
      || global_z > MaxGridDimension || !validBlock(dim3(local_x, local_y, local_z)))
    return remember(hipErrorInvalidConfiguration);
  gemstone::hip::LaunchBuffer launch_buffer;
  if (!gemstone::hip::parseLaunchBuffer(extra, launch_buffer)) return remember(hipErrorInvalidValue);
  if (traceModules())
    std::fprintf(stderr, "Gemstone HIP ext module launch f=%p global=%u,%u,%u local=%u,%u,%u args=%p\n", function, global_x, global_y,
                 global_z, local_x, local_y, local_z, static_cast<void *>(args));
  const dim3 global(global_x, global_y, global_z);
  // Like the vendor extended launch path, fit a nominal workgroup inside a smaller whole grid.
  const dim3 local(std::min(local_x, global_x), std::min(local_y, global_y), std::min(local_z, global_z));
  return dispatchKernel(function_state->kernel,
                        dim3(workgroupCount(global_x, local.x), workgroupCount(global_y, local.y), workgroupCount(global_z, local.z)),
                        local, args, shared, stream, launch_buffer, &global);
}

extern "C" hipError_t hipEventCreateWithFlags(hipEvent_t *event, unsigned int) {
  if (!event) return remember(hipErrorInvalidValue);
  auto storage = std::unique_ptr<Event>(new (std::nothrow) Event);
  if (!storage) return remember(hipErrorOutOfMemory);
  *event = reinterpret_cast<hipEvent_t>(storage.get());
  std::lock_guard<std::mutex> guard(state().lock);
  state().events.emplace(*event, std::move(storage));
  return remember(hipSuccess);
}
extern "C" hipError_t hipEventCreate(hipEvent_t *event) { return hipEventCreateWithFlags(event, 0); }
extern "C" hipError_t hipEventDestroy(hipEvent_t event) {
  std::lock_guard<std::mutex> guard(state().lock);
  return remember(state().events.erase(event) == 1 ? hipSuccess : hipErrorInvalidResourceHandle);
}
extern "C" hipError_t hipEventRecordWithFlags(hipEvent_t event, hipStream_t stream, unsigned int) {
  if (!validStream(stream)) return remember(hipErrorInvalidResourceHandle);
  std::lock_guard<std::mutex> guard(state().lock);
  const auto found = state().events.find(event);
  if (found == state().events.end()) return remember(hipErrorInvalidResourceHandle);
  found->second->ticket = commands().submit(stream, [] { return hipSuccess; });
  return remember(hipSuccess);
}
extern "C" hipError_t hipEventRecord(hipEvent_t event, hipStream_t stream) { return hipEventRecordWithFlags(event, stream, 0); }
extern "C" hipError_t hipEventSynchronize(hipEvent_t event) {
  gemstone::CommandQueue::Ticket ticket;
  {
    std::lock_guard<std::mutex> guard(state().lock);
    const auto found = state().events.find(event);
    if (found == state().events.end()) return remember(hipErrorInvalidResourceHandle);
    ticket = found->second->ticket;
  }
  return remember(static_cast<hipError_t>(commands().wait(ticket)));
}
extern "C" hipError_t hipEventQuery(hipEvent_t event) {
  std::lock_guard<std::mutex> guard(state().lock);
  const auto found = state().events.find(event);
  if (found == state().events.end()) return remember(hipErrorInvalidResourceHandle);
  return remember(static_cast<hipError_t>(commands().query(found->second->ticket, hipErrorNotReady)));
}
extern "C" hipError_t hipEventElapsedTime(float *milliseconds, hipEvent_t start, hipEvent_t stop) {
  if (!milliseconds) return remember(hipErrorInvalidValue);
  {
    std::lock_guard<std::mutex> guard(state().lock);
    if (!start || !stop || state().events.count(start) != 1 || state().events.count(stop) != 1)
      return remember(hipErrorInvalidResourceHandle);
  }
  *milliseconds = 0.0f;
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
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.modules.emplace(token, module);
  }
  if (traceModules()) std::fprintf(stderr, "Gemstone HIP fatbinary=%p registered lazily\n", fatbin);
  return token;
}

extern "C" GEMSTONE_PUBLIC_API void __hipRegisterFunction(void **modules, const void *host_function, char *, const char *device_name,
                                                          unsigned int, uint3 *, uint3 *, dim3 *, dim3 *, int *) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto module = findModule(modules);
  if (!module || !host_function || !device_name) return;
  runtime.kernels.insert_or_assign(host_function, FunctionBinding{module, device_name, nullptr});
}

extern "C" GEMSTONE_PUBLIC_API void __hipRegisterVar(void **modules, void *var, char *, char *device_name, int, size_t size, int, int) {
  if (!modules || !var || size == 0) return;
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto module = findModule(modules);
  if (!module) return;
  const auto [iterator, inserted] =
      runtime.variables.insert_or_assign(var, Variable{var, !device_name ? std::string{} : std::string(device_name), size, module});
  (void)iterator;
  if (inserted) module->variables.push_back(var);
}

extern "C" GEMSTONE_PUBLIC_API void __hipUnregisterFatBinary(void **modules) {
  (void)waitCommands();
  auto &runtime = state();
  std::shared_ptr<Module> module;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
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
