// SPDX-License-Identifier: Apache-2.0
#include <algorithm>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <memory>
#include <mutex>
#include <new>
#include <string>
#include <thread>
#include <unordered_map>
#include <utility>
#include <vector>

#include <elf.h>
#include <zstd.h>

#include "gemstone/hip/dispatch.hpp"
#include "gemstone/hsa/code_object_metadata.hpp"
#include "gemstone/hsa/target.h"
#include <hip/hip_ext.h>
#include <hip/hip_runtime_api.h>
#include <hsa/hsa.h>
#include <hsa/hsa_ext_amd.h>

namespace {

constexpr char kBundleMagic[] = "__CLANG_OFFLOAD_BUNDLE__";
constexpr size_t kBundleMagicSize = sizeof(kBundleMagic) - 1U;
constexpr char kCompressedBundleMagic[] = "CCOB";
constexpr size_t kCompressedBundleMagicSize = sizeof(kCompressedBundleMagic) - 1U;
constexpr size_t kCompressedBundleHeaderV3Size = 32;
constexpr uint64_t kMaximumBundleCount = 64;
// HIP fatbinaries may contain several AMDGPU images, and the host ROCr agent
// is not necessarily the agent represented by this provider, so selection
// follows the provider's advertised target rather than bundle order.

struct Kernel {
  uint64_t object = 0;
  uint32_t kernarg_size = 0;
  uint32_t explicit_argument_count = 0;
  std::vector<gemstone_hsa_kernarg_field> fields;
};

struct Variable {
  void *host = nullptr;
  std::string device_name;
  size_t size = 0;
};

struct Module {
  // The address of this member is the opaque value passed back to the HIP
  // compiler's registration callbacks.
  void *token = nullptr;
  const void *image = nullptr;
  size_t image_size = 0;
  hsa_code_object_reader_t reader{};
  hsa_executable_t executable{};
  bool loaded = false;
  std::string error;
  std::vector<uint8_t> decompressed_image;
  std::vector<gemstone::hsa::CodeObjectKernelMetadata> metadata;
  std::unordered_map<std::string, std::shared_ptr<Kernel>> kernels;
  std::vector<const void *> variables;

  ~Module() {
    if (executable.handle != 0) hsa_executable_destroy(executable);
    if (reader.handle != 0) hsa_code_object_reader_destroy(reader);
  }
};

struct ModuleHandle {
  std::shared_ptr<Module> module;
};

struct FunctionHandle {
  std::shared_ptr<Module> module;
  std::shared_ptr<Kernel> kernel;
};

struct State {
  std::mutex lock;
  bool initialized = false;
  bool atexit_registered = false;
  hsa_agent_t gpu{};
  hsa_region_t kernarg_region{};
  hsa_queue_t *queue = nullptr;
  std::unordered_map<void **, std::shared_ptr<Module>> modules;
  std::vector<std::weak_ptr<Module>> driver_modules;
  std::unordered_map<const void *, std::shared_ptr<Kernel>> kernels;
  std::unordered_map<const void *, Variable> variables;
  std::unordered_map<void *, size_t> host_allocations;
};

struct LaunchConfiguration {
  dim3 grid{1, 1, 1};
  dim3 block{1, 1, 1};
  size_t shared = 0;
  hipStream_t stream = nullptr;
};

bool trace_modules();

State &state() {
  static State *value = new State();
  return *value;
}

thread_local hipError_t last_error = hipSuccess;
thread_local LaunchConfiguration launch_configuration;

hipError_t remember(hipError_t error) {
  last_error = error;
  return error;
}

hipError_t map_hsa(hsa_status_t status) {
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

hsa_status_t find_gpu(hsa_agent_t agent, void *opaque) {
  hsa_device_type_t type = HSA_DEVICE_TYPE_CPU;
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  if (type == HSA_DEVICE_TYPE_GPU) {
    *static_cast<hsa_agent_t *>(opaque) = agent;
    return HSA_STATUS_INFO_BREAK;
  }
  return HSA_STATUS_SUCCESS;
}

hsa_status_t find_region(hsa_region_t region, void *opaque) {
  *static_cast<hsa_region_t *>(opaque) = region;
  return HSA_STATUS_INFO_BREAK;
}

bool ensure_initialized() {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (runtime.initialized) return true;
  const hsa_status_t init = hsa_init();
  const hsa_status_t agents = init == HSA_STATUS_SUCCESS ? hsa_iterate_agents(find_gpu, &runtime.gpu) : HSA_STATUS_ERROR;
  const hsa_status_t regions =
      agents == HSA_STATUS_INFO_BREAK ? hsa_agent_iterate_regions(runtime.gpu, find_region, &runtime.kernarg_region) : HSA_STATUS_ERROR;
  const hsa_status_t queue = regions == HSA_STATUS_INFO_BREAK ? hsa_queue_create(runtime.gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr,
                                                                                 UINT32_MAX, UINT32_MAX, &runtime.queue)
                                                              : HSA_STATUS_ERROR;
  if (init != HSA_STATUS_SUCCESS || agents != HSA_STATUS_INFO_BREAK || runtime.gpu.handle == 0 || regions != HSA_STATUS_INFO_BREAK
      || runtime.kernarg_region.handle == 0 || queue != HSA_STATUS_SUCCESS) {
    if (runtime.queue != nullptr) {
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
    runtime.atexit_registered = true;
    std::atexit([] {
      auto &value = state();
      std::lock_guard<std::mutex> exit_guard(value.lock);
      if (value.queue != nullptr) {
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

hipError_t allocate_host(void **pointer, size_t size) {
  if (pointer == nullptr) return remember(hipErrorInvalidValue);
  if (size == 0) {
    *pointer = nullptr;
    return remember(hipSuccess);
  }
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  void *memory = std::malloc(size);
  if (memory == nullptr) return remember(hipErrorOutOfMemory);
  const hsa_status_t status = hsa_memory_register(memory, size);
  if (status != HSA_STATUS_SUCCESS) {
    std::free(memory);
    return remember(map_hsa(status));
  }
  {
    std::lock_guard<std::mutex> guard(state().lock);
    state().host_allocations.emplace(memory, size);
  }
  *pointer = memory;
  return remember(hipSuccess);
}

hipError_t release_host(void *pointer) {
  if (pointer == nullptr) return remember(hipSuccess);
  {
    std::lock_guard<std::mutex> guard(state().lock);
    const auto found = state().host_allocations.find(pointer);
    if (found == state().host_allocations.end()) return remember(hipErrorInvalidValue);
    const hsa_status_t status = hsa_memory_deregister(pointer, found->second);
    if (status != HSA_STATUS_SUCCESS) return remember(map_hsa(status));
    state().host_allocations.erase(found);
  }
  std::free(pointer);
  return remember(hipSuccess);
}

bool read_u64(const uint8_t *bytes, size_t size, size_t &cursor, uint64_t &value) {
  if (cursor > size || size - cursor < sizeof(value)) return false;
  value = 0;
  for (size_t index = 0; index < sizeof(value); ++index)
    value |= uint64_t(bytes[cursor + index]) << (index * 8U);
  cursor += sizeof(value);
  return true;
}

bool decompress_bundle(const uint8_t *compressed, std::vector<uint8_t> &decompressed) {
  if (compressed == nullptr || std::memcmp(compressed, kCompressedBundleMagic, kCompressedBundleMagicSize) != 0) return false;
  uint16_t version = 0;
  uint16_t format = 0;
  std::memcpy(&version, compressed + 4, sizeof(version));
  std::memcpy(&format, compressed + 6, sizeof(format));
  if (version != 3 || format != 1) return false;
  uint64_t file_size = 0;
  uint64_t uncompressed_size = 0;
  std::memcpy(&file_size, compressed + 8, sizeof(file_size));
  std::memcpy(&uncompressed_size, compressed + 16, sizeof(uncompressed_size));
  if (file_size < kCompressedBundleHeaderV3Size || uncompressed_size == 0 || file_size > std::numeric_limits<size_t>::max()
      || uncompressed_size > std::numeric_limits<size_t>::max())
    return false;
  const auto compressed_size = static_cast<size_t>(file_size) - kCompressedBundleHeaderV3Size;
  const auto output_size = static_cast<size_t>(uncompressed_size);
  decompressed.resize(output_size);
  const size_t result = ZSTD_decompress(decompressed.data(), output_size, compressed + kCompressedBundleHeaderV3Size, compressed_size);
  if (ZSTD_isError(result) || result != output_size) {
    decompressed.clear();
    return false;
  }
  return true;
}

bool select_bundle(const void *fatbin, const void **image, size_t *image_size, std::vector<uint8_t> *decompressed = nullptr) {
  if (fatbin == nullptr || image == nullptr || image_size == nullptr) return false;
  const auto *bytes = static_cast<const uint8_t *>(fatbin);
  const auto *bundle = (std::memcmp(bytes, kBundleMagic, kBundleMagicSize) == 0
                        || std::memcmp(bytes, kCompressedBundleMagic, kCompressedBundleMagicSize) == 0)
                           ? bytes
                           : static_cast<const uint8_t *>(static_cast<const void *const *>(fatbin)[1]);
  if (bundle == nullptr) return false;
  if (std::memcmp(bundle, kCompressedBundleMagic, kCompressedBundleMagicSize) == 0) {
    if (decompressed == nullptr || !decompress_bundle(bundle, *decompressed)) return false;
    bundle = decompressed->data();
  }
  if (std::memcmp(bundle, kBundleMagic, kBundleMagicSize) != 0) return false;
  size_t cursor = kBundleMagicSize;
  uint64_t count = 0;
  if (!read_u64(bundle, std::numeric_limits<size_t>::max(), cursor, count) || count == 0 || count > kMaximumBundleCount) return false;
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
    if (!read_u64(bundle, std::numeric_limits<size_t>::max(), cursor, offset)
        || !read_u64(bundle, std::numeric_limits<size_t>::max(), cursor, size)
        || !read_u64(bundle, std::numeric_limits<size_t>::max(), cursor, triple_size) || triple_size == 0 || triple_size > 4096)
      return false;
    const auto *triple = reinterpret_cast<const char *>(bundle + cursor);
    entries.push_back(Entry{offset, size, std::string(triple, static_cast<size_t>(triple_size))});
    cursor += static_cast<size_t>(triple_size);
  }
  const Entry *target = nullptr;
  for (const auto &entry : entries) {
    if (entry.triple.find("amdgcn") == std::string::npos && entry.triple.find("amdhsa") == std::string::npos) continue;
    if (entry.offset == 0 || entry.size == 0 || entry.offset > std::numeric_limits<size_t>::max()
        || entry.size > std::numeric_limits<size_t>::max() - static_cast<size_t>(entry.offset))
      return false;
    if (entry.triple.find(gemstone::hsa::target_arch()) != std::string::npos) target = &entry;
  }
  // Never run an image for a different virtual target. A fallback would make
  // a matrix case appear to work while executing another generation's ISA.
  const Entry *selected = target;
  if (selected == nullptr) return false;
  *image = bundle + static_cast<size_t>(selected->offset);
  *image_size = static_cast<size_t>(selected->size);
  return true;
}

bool image_is_elf(const void *image) {
  if (image == nullptr) return false;
  const auto *bytes = static_cast<const uint8_t *>(image);
  return bytes[EI_MAG0] == ELFMAG0 && bytes[EI_MAG1] == ELFMAG1 && bytes[EI_MAG2] == ELFMAG2 && bytes[EI_MAG3] == ELFMAG3;
}

bool add_file_range(uint64_t offset, uint64_t size, uint64_t &end) {
  if (offset > std::numeric_limits<uint64_t>::max() - size) return false;
  end = std::max(end, offset + size);
  return true;
}

bool elf_image_size(const void *image, size_t &size) {
  if (!image_is_elf(image)) return false;
  Elf64_Ehdr header{};
  std::memcpy(&header, image, sizeof(header));
  if (header.e_ident[EI_CLASS] != ELFCLASS64 || header.e_ident[EI_DATA] != ELFDATA2LSB || header.e_ehsize < sizeof(Elf64_Ehdr)
      || header.e_phentsize < sizeof(Elf64_Phdr) || header.e_shentsize < sizeof(Elf64_Shdr))
    return false;
  uint64_t end = header.e_ehsize;
  if (header.e_phnum != 0) {
    if (header.e_phoff > std::numeric_limits<uint64_t>::max() - uint64_t(header.e_phentsize) * header.e_phnum) return false;
    end = std::max(end, header.e_phoff + uint64_t(header.e_phentsize) * header.e_phnum);
    for (uint16_t index = 0; index < header.e_phnum; ++index) {
      Elf64_Phdr program{};
      const auto offset = header.e_phoff + uint64_t(index) * header.e_phentsize;
      std::memcpy(&program, static_cast<const uint8_t *>(image) + offset, sizeof(program));
      if (!add_file_range(program.p_offset, program.p_filesz, end)) return false;
    }
  }
  if (header.e_shnum != 0) {
    if (header.e_shoff > std::numeric_limits<uint64_t>::max() - uint64_t(header.e_shentsize) * header.e_shnum) return false;
    end = std::max(end, header.e_shoff + uint64_t(header.e_shentsize) * header.e_shnum);
    for (uint16_t index = 0; index < header.e_shnum; ++index) {
      Elf64_Shdr section{};
      const auto offset = header.e_shoff + uint64_t(index) * header.e_shentsize;
      std::memcpy(&section, static_cast<const uint8_t *>(image) + offset, sizeof(section));
      if (section.sh_type != SHT_NOBITS && !add_file_range(section.sh_offset, section.sh_size, end)) return false;
    }
  }
  if (end > std::numeric_limits<size_t>::max()) return false;
  size = static_cast<size_t>(end);
  return size >= sizeof(Elf64_Ehdr);
}

std::shared_ptr<Module> find_module(void **token) {
  auto &runtime = state();
  const auto found = runtime.modules.find(token);
  return found == runtime.modules.end() ? std::shared_ptr<Module>{} : found->second;
}

const gemstone::hsa::CodeObjectKernelMetadata *find_metadata(const Module &module, const char *name) {
  if (name == nullptr) return nullptr;
  for (const auto &candidate : module.metadata)
    if (candidate.name == name || candidate.symbol == name || candidate.symbol + ".kd" == name) return &candidate;
  return nullptr;
}

bool load_module(Module &module) {
  if (module.loaded) return true;
  if (module.image == nullptr || module.image_size == 0) {
    module.error = "HIP fatbinary has no AMDGPU code object";
    return false;
  }
  std::string parse_error;
  if (!gemstone::hsa::parse_code_object_metadata(module.image, module.image_size, module.metadata, parse_error)
      || module.metadata.empty()) {
    module.error = parse_error.empty() ? "AMDGPU code object metadata is absent" : parse_error;
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
  const hsa_status_t freeze = load == HSA_STATUS_SUCCESS ? hsa_executable_freeze(module.executable, nullptr) : HSA_STATUS_ERROR;
  if (reader != HSA_STATUS_SUCCESS || executable != HSA_STATUS_SUCCESS || load != HSA_STATUS_SUCCESS || freeze != HSA_STATUS_SUCCESS) {
    module.error = "public HSA rejected the HIP code object";
    if (module.executable.handle != 0) hsa_executable_destroy(module.executable);
    if (module.reader.handle != 0) hsa_code_object_reader_destroy(module.reader);
    module.executable = {};
    module.reader = {};
    return false;
  }
  for (const auto &metadata : module.metadata) {
    hsa_executable_symbol_t symbol{};
    hsa_status_t status = hsa_executable_get_symbol_by_name(module.executable, metadata.name.c_str(), &state().gpu, &symbol);
    if (status != HSA_STATUS_SUCCESS)
      status = hsa_executable_get_symbol_by_name(module.executable, metadata.symbol.c_str(), &state().gpu, &symbol);
    if (status != HSA_STATUS_SUCCESS) continue;
    uint64_t object = 0;
    uint32_t kernarg_size = 0;
    if (hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) != HSA_STATUS_SUCCESS
        || hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE, &kernarg_size)
               != HSA_STATUS_SUCCESS)
      continue;
    auto kernel = std::make_shared<Kernel>();
    kernel->object = object;
    kernel->kernarg_size = kernarg_size;
    kernel->explicit_argument_count = metadata.explicit_argument_count;
    kernel->fields = metadata.fields;
    module.kernels.emplace(metadata.name, kernel);
    module.kernels.emplace(metadata.symbol, kernel);
  }
  module.loaded = !module.kernels.empty();
  if (!module.loaded) module.error = "HIP code object contains no resolvable kernels";
  return module.loaded;
}

std::shared_ptr<Kernel> find_kernel(const void *function_address) {
  auto &runtime = state();
  const auto found = runtime.kernels.find(function_address);
  return found == runtime.kernels.end() ? std::shared_ptr<Kernel>{} : found->second;
}

hipError_t dispatch_kernel(const std::shared_ptr<Kernel> &kernel, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t,
                           const gemstone::hip::LaunchBuffer &launch_buffer = {}) {
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  if (!kernel || (args == nullptr && launch_buffer.data == nullptr && kernel->explicit_argument_count != 0)) {
    if (trace_modules())
      std::fprintf(stderr, "Gemstone HIP dispatch rejected kernel=%p args=%p\n", static_cast<const void *>(kernel.get()),
                   static_cast<void *>(args));
    return remember(hipErrorInvalidDeviceFunction);
  }
  if (trace_modules())
    std::fprintf(stderr, "Gemstone HIP dispatch kernel=%p object=%llu explicit=%u kernarg=%u\n", static_cast<const void *>(kernel.get()),
                 static_cast<unsigned long long>(kernel->object), kernel->explicit_argument_count, kernel->kernarg_size);
  if (grid.x == 0 || grid.y == 0 || grid.z == 0 || block.x == 0 || block.y == 0 || block.z == 0
      || grid.x > std::numeric_limits<uint32_t>::max() / block.x || grid.y > std::numeric_limits<uint32_t>::max() / block.y
      || grid.z > std::numeric_limits<uint32_t>::max() / block.z || shared > std::numeric_limits<uint32_t>::max())
    return remember(hipErrorInvalidConfiguration);
  void *kernarg = nullptr;
  hsa_status_t status = hsa_memory_allocate(state().kernarg_region, kernel->kernarg_size, &kernarg);
  if (status != HSA_STATUS_SUCCESS) return remember(map_hsa(status));
  std::memset(kernarg, 0, kernel->kernarg_size);
  if (launch_buffer.data != nullptr) {
    if (launch_buffer.size > kernel->kernarg_size) {
      hsa_memory_free(kernarg);
      return remember(hipErrorInvalidValue);
    }
    std::memcpy(kernarg, launch_buffer.data, launch_buffer.size);
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
      write_u32(hidden + 0, static_cast<uint32_t>(grid.x));
      write_u32(hidden + 4, static_cast<uint32_t>(grid.y));
      write_u32(hidden + 8, static_cast<uint32_t>(grid.z));
      write_u16(hidden + 12, static_cast<uint32_t>(block.x));
      write_u16(hidden + 14, static_cast<uint32_t>(block.y));
      write_u16(hidden + 16, static_cast<uint32_t>(block.z));
      write_u16(hidden + 18, static_cast<uint32_t>((grid.x * block.x) % block.x));
      write_u16(hidden + 20, static_cast<uint32_t>((grid.y * block.y) % block.y));
      write_u16(hidden + 22, static_cast<uint32_t>((grid.z * block.z) % block.z));
      write_u16(hidden + 64, gemstone::hip::dispatch_dimensions(grid, block));
    }
  }
  hsa_signal_t completion{};
  status = hsa_signal_create(1, 0, nullptr, &completion);
  if (status == HSA_STATUS_SUCCESS) {
    const uint64_t index = hsa_queue_add_write_index_relaxed(state().queue, 1);
    auto *packet = &static_cast<hsa_kernel_dispatch_packet_t *>(state().queue->base_address)[index & (state().queue->size - 1U)];
    std::memset(packet, 0, sizeof(*packet));
    packet->setup = gemstone::hip::dispatch_dimensions(grid, block) << HSA_KERNEL_DISPATCH_PACKET_SETUP_DIMENSIONS;
    packet->workgroup_size_x = static_cast<uint16_t>(block.x);
    packet->workgroup_size_y = static_cast<uint16_t>(block.y);
    packet->workgroup_size_z = static_cast<uint16_t>(block.z);
    packet->grid_size_x = grid.x * block.x;
    packet->grid_size_y = grid.y * block.y;
    packet->grid_size_z = grid.z * block.z;
    packet->group_segment_size = static_cast<uint32_t>(shared);
    packet->kernel_object = kernel->object;
    packet->kernarg_address = kernarg;
    packet->completion_signal = completion;
    const uint16_t header = HSA_PACKET_TYPE_KERNEL_DISPATCH << HSA_PACKET_HEADER_TYPE;
    __atomic_store_n(&packet->header, header, __ATOMIC_RELEASE);
    hsa_signal_store_screlease(state().queue->doorbell_signal, static_cast<hsa_signal_value_t>(index));
    while (hsa_signal_load_scacquire(completion) == 1)
      std::this_thread::yield();
    if (hsa_signal_load_scacquire(completion) < 0) status = HSA_STATUS_ERROR;
  }
  const hsa_status_t free_status = hsa_memory_free(kernarg);
  const hsa_status_t destroy_status = hsa_signal_destroy(completion);
  if (status != HSA_STATUS_SUCCESS) {
    if (trace_modules()) std::fprintf(stderr, "Gemstone HIP dispatch HSA status=%d\n", static_cast<int>(status));
    return remember(hipErrorLaunchFailure);
  }
  if (free_status != HSA_STATUS_SUCCESS || destroy_status != HSA_STATUS_SUCCESS)
    return remember(map_hsa(free_status != HSA_STATUS_SUCCESS ? free_status : destroy_status));
  return remember(hipSuccess);
}

hipError_t dispatch(const void *function_address, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t stream) {
  std::shared_ptr<Kernel> kernel;
  {
    std::lock_guard<std::mutex> guard(state().lock);
    kernel = find_kernel(function_address);
  }
  if (trace_modules())
    std::fprintf(stderr, "Gemstone HIP direct launch host=%p kernel=%p\n", function_address, static_cast<const void *>(kernel.get()));
  return dispatch_kernel(kernel, grid, block, args, shared, stream);
}

FunctionHandle *function_handle(hipFunction_t function) { return reinterpret_cast<FunctionHandle *>(function); }

ModuleHandle *module_handle(hipModule_t module) { return reinterpret_cast<ModuleHandle *>(module); }

bool trace_modules() {
  static const bool enabled = std::getenv("GEMSTONE_HIP_TRACE_MODULES") != nullptr;
  return enabled;
}

std::shared_ptr<Module> create_module(const void *input) {
  auto module = std::make_shared<Module>();
  const void *image = nullptr;
  size_t image_size = 0;
  if (image_is_elf(input)) {
    if (!elf_image_size(input, image_size)) {
      module->error = "invalid ELF64 AMDGPU code object";
      return module;
    }
    image = input;
  } else if (!select_bundle(input, &image, &image_size, &module->decompressed_image)) {
    module->error = std::string("HIP module has no ") + gemstone::hsa::target_arch() + " code object";
    return module;
  }
  module->image = image;
  module->image_size = image_size;
  if (!load_module(*module)) return module;
  return module;
}

uint32_t workgroup_count(uint32_t global, uint32_t local) { return local == 0 ? 0 : global / local + (global % local == 0 ? 0 : 1); }

} // namespace

extern "C" hipError_t hipInit(unsigned int flags) {
  return remember(flags == 0 && ensure_initialized() ? hipSuccess : (flags != 0 ? hipErrorInvalidValue : hipErrorNoDevice));
}

extern "C" hipError_t hipDriverGetVersion(int *version) {
  if (version == nullptr) return remember(hipErrorInvalidValue);
  *version = 70200;
  return remember(hipSuccess);
}

extern "C" hipError_t hipRuntimeGetVersion(int *version) {
  if (version == nullptr) return remember(hipErrorInvalidValue);
  *version = 70200;
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetDeviceCount(int *count) {
  if (count == nullptr) return remember(hipErrorInvalidValue);
  *count = ensure_initialized() ? 1 : 0;
  return remember(*count == 1 ? hipSuccess : hipErrorNoDevice);
}

extern "C" hipError_t hipGetDevice(int *device) {
  if (device == nullptr) return remember(hipErrorInvalidValue);
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  *device = 0;
  return remember(hipSuccess);
}

extern "C" hipError_t hipSetDevice(int device) {
  return remember(device == 0 && ensure_initialized() ? hipSuccess : hipErrorInvalidDevice);
}

extern "C" hipError_t hipDeviceGet(hipDevice_t *device, int ordinal) {
  if (device == nullptr) return remember(hipErrorInvalidValue);
  if (ordinal != 0 || !ensure_initialized()) return remember(hipErrorInvalidDevice);
  *device = 0;
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceSynchronize(void) { return remember(ensure_initialized() ? hipSuccess : hipErrorNoDevice); }
extern "C" hipError_t hipDeviceReset(void) { return remember(hipSuccess); }

extern "C" hipError_t hipDeviceComputeCapability(int *major, int *minor, hipDevice_t device) {
  if (major == nullptr || minor == nullptr || device != 0) return remember(hipErrorInvalidValue);
  const auto version = gemstone::hsa::target_version();
  *major = static_cast<int>(version.major);
  *minor = static_cast<int>(version.minor);
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGetName(char *name, int length, hipDevice_t device) {
  if (name == nullptr || length <= 0 || device != 0) return remember(hipErrorInvalidValue);
  const std::string value = std::string("Gemstone ") + gemstone::hsa::target_arch();
  std::strncpy(name, value.c_str(), static_cast<size_t>(length));
  name[length - 1] = '\0';
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceTotalMem(size_t *bytes, hipDevice_t device) {
  if (bytes == nullptr || device != 0) return remember(hipErrorInvalidValue);
  *bytes = size_t(1) << 30U;
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemGetInfo(size_t *free, size_t *total) {
  if (free == nullptr || total == nullptr) return remember(hipErrorInvalidValue);
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  *total = size_t(1) << 30U;
  *free = *total;
  return remember(hipSuccess);
}

extern "C" hipError_t hipDeviceGetDefaultMemPool(hipMemPool_t *pool, int device) {
  if (pool == nullptr || device != 0) return remember(hipErrorInvalidValue);
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  *pool = reinterpret_cast<hipMemPool_t>(uintptr_t(1));
  return remember(hipSuccess);
}

extern "C" hipError_t hipMallocAsync(void **pointer, size_t size, hipStream_t) { return hipMalloc(pointer, size); }
extern "C" hipError_t hipFreeAsync(void *pointer, hipStream_t) { return hipFree(pointer); }
extern "C" hipError_t hipMemPoolTrimTo(hipMemPool_t pool, size_t) { return remember(pool == nullptr ? hipErrorInvalidValue : hipSuccess); }

extern "C" hipError_t hipDeviceGetAttribute(int *value, hipDeviceAttribute_t attribute, int device) {
  if (value == nullptr || device != 0) return remember(hipErrorInvalidValue);
  switch (attribute) {
    case hipDeviceAttributeMaxThreadsPerBlock: *value = 1024; break;
    case hipDeviceAttributeWarpSize: *value = 64; break;
    case hipDeviceAttributeMaxBlockDimX: *value = 1024; break;
    case hipDeviceAttributeMaxBlockDimY: *value = 1024; break;
    case hipDeviceAttributeMaxBlockDimZ: *value = 1024; break;
    case hipDeviceAttributeMaxGridDimX: *value = std::numeric_limits<int>::max(); break;
    case hipDeviceAttributeMaxGridDimY: *value = std::numeric_limits<int>::max(); break;
    case hipDeviceAttributeMaxGridDimZ: *value = std::numeric_limits<int>::max(); break;
    default: *value = 0; break;
  }
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetDevicePropertiesR0600(hipDeviceProp_tR0600 *properties, int device) {
  if (properties == nullptr || device != 0) return remember(hipErrorInvalidValue);
  std::memset(properties, 0, sizeof(*properties));
  const std::string target = gemstone::hsa::target_arch();
  const std::string name = std::string("Gemstone ") + target;
  std::strncpy(properties->name, name.c_str(), sizeof(properties->name) - 1U);
  std::strncpy(properties->gcnArchName, target.c_str(), sizeof(properties->gcnArchName) - 1U);
  properties->totalGlobalMem = size_t(1) << 30U;
  properties->sharedMemPerBlock = 64U * 1024U;
  properties->warpSize = 64;
  properties->maxThreadsPerBlock = 1024;
  properties->maxThreadsDim[0] = 1024;
  properties->maxThreadsDim[1] = 1024;
  properties->maxThreadsDim[2] = 1024;
  properties->maxGridSize[0] = std::numeric_limits<int>::max();
  properties->maxGridSize[1] = std::numeric_limits<int>::max();
  properties->maxGridSize[2] = std::numeric_limits<int>::max();
  const auto version = gemstone::hsa::target_version(target.c_str());
  properties->major = static_cast<int>(version.major);
  properties->minor = static_cast<int>(version.minor);
  properties->multiProcessorCount = 1;
  return remember(hipSuccess);
}

extern "C" hipError_t hipMalloc(void **pointer, size_t size) {
  if (pointer == nullptr) return remember(hipErrorInvalidValue);
  if (size == 0) {
    *pointer = nullptr;
    return remember(hipSuccess);
  }
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  const hsa_status_t status = hsa_memory_allocate(state().kernarg_region, size, pointer);
  if (std::getenv("GEMSTONE_HIP_TRACE_ALLOCATIONS") != nullptr)
    std::fprintf(stderr, "Gemstone HIP malloc size=%zu status=%d pointer=%p\n", size, int(status), pointer == nullptr ? nullptr : *pointer);
  return remember(map_hsa(status));
}

extern "C" hipError_t hipFree(void *pointer) {
  if (pointer == nullptr) return remember(hipSuccess);
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  const hsa_status_t status = hsa_memory_free(pointer);
  return remember(map_hsa(status));
}

extern "C" hipError_t hipMemcpy(void *destination, const void *source, size_t size, hipMemcpyKind kind) {
  if (size == 0) return remember(hipSuccess);
  if (destination == nullptr || source == nullptr) return remember(hipErrorInvalidValue);
  if (kind == hipMemcpyHostToHost) {
    std::memmove(destination, source, size);
    return remember(hipSuccess);
  }
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  const hsa_status_t status = hsa_memory_copy(destination, source, size);
  return remember(map_hsa(status));
}

hipError_t copy_symbol(void *destination, const void *source, size_t size, size_t offset) {
  if ((destination == nullptr || source == nullptr) && size != 0) return remember(hipErrorInvalidValue);
  std::lock_guard<std::mutex> guard(state().lock);
  const auto found = state().variables.find(destination);
  if (found == state().variables.end() || offset > found->second.size || size > found->second.size - offset)
    return remember(hipErrorInvalidSymbol);
  if (size != 0) std::memcpy(static_cast<unsigned char *>(found->second.host) + offset, source, size);
  return remember(hipSuccess);
}

const void *symbol_source(const void *symbol, size_t size, size_t offset, hipError_t &error) {
  if (symbol == nullptr && size != 0) {
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
  return copy_symbol(const_cast<void *>(symbol), source, size, offset);
}

extern "C" hipError_t hipMemcpyToSymbolAsync(const void *symbol, const void *source, size_t size, size_t offset, hipMemcpyKind kind,
                                             hipStream_t) {
  return hipMemcpyToSymbol(symbol, source, size, offset, kind);
}

extern "C" hipError_t hipMemcpyFromSymbol(void *destination, const void *symbol, size_t size, size_t offset, hipMemcpyKind kind) {
  hipError_t error = hipSuccess;
  const void *source = symbol_source(symbol, size, offset, error);
  if (error != hipSuccess) return remember(error);
  return hipMemcpy(destination, source, size, kind);
}

extern "C" hipError_t hipMemcpyFromSymbolAsync(void *destination, const void *symbol, size_t size, size_t offset, hipMemcpyKind kind,
                                               hipStream_t) {
  return hipMemcpyFromSymbol(destination, symbol, size, offset, kind);
}

extern "C" hipError_t hipMemcpyAsync(void *destination, const void *source, size_t size, hipMemcpyKind kind, hipStream_t) {
  return hipMemcpy(destination, source, size, kind);
}
extern "C" hipError_t hipMemcpy2D(void *destination, size_t destination_pitch, const void *source, size_t source_pitch, size_t width,
                                  size_t height, hipMemcpyKind kind) {
  if ((destination == nullptr || source == nullptr) && width != 0 && height != 0) return remember(hipErrorInvalidValue);
  for (size_t row = 0; row < height; ++row) {
    const auto *source_row = static_cast<const unsigned char *>(source) + row * source_pitch;
    auto *destination_row = static_cast<unsigned char *>(destination) + row * destination_pitch;
    const hipError_t status = hipMemcpy(destination_row, source_row, width, kind);
    if (status != hipSuccess) return status;
  }
  return remember(hipSuccess);
}
extern "C" hipError_t hipMemcpy2DAsync(void *destination, size_t destination_pitch, const void *source, size_t source_pitch, size_t width,
                                       size_t height, hipMemcpyKind kind, hipStream_t) {
  return hipMemcpy2D(destination, destination_pitch, source, source_pitch, width, height, kind);
}
extern "C" hipError_t hipMemcpyWithStream(void *destination, const void *source, size_t size, hipMemcpyKind kind, hipStream_t) {
  return hipMemcpy(destination, source, size, kind);
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

extern "C" hipError_t hipMallocManaged(void **pointer, size_t size, unsigned int) { return hipMalloc(pointer, size); }
extern "C" hipError_t hipMallocHost(void **pointer, size_t size) { return allocate_host(pointer, size); }
extern "C" hipError_t hipHostMalloc(void **pointer, size_t size, unsigned int) { return allocate_host(pointer, size); }
extern "C" hipError_t hipFreeHost(void *pointer) { return release_host(pointer); }
extern "C" hipError_t hipHostFree(void *pointer) { return hipFreeHost(pointer); }

extern "C" hipError_t hipHostRegister(void *pointer, size_t size, unsigned int) {
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  return remember(map_hsa(hsa_memory_register(pointer, size)));
}
extern "C" hipError_t hipHostUnregister(void *pointer) {
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  return remember(map_hsa(hsa_memory_deregister(pointer, 0)));
}

extern "C" hipError_t hipPointerGetAttributes(hipPointerAttribute_t *attributes, const void *pointer) {
  if (attributes == nullptr || pointer == nullptr) return remember(hipErrorInvalidValue);
  std::memset(attributes, 0, sizeof(*attributes));
  attributes->type = hipMemoryTypeDevice;
  attributes->device = 0;
  attributes->devicePointer = const_cast<void *>(pointer);
  attributes->hostPointer = const_cast<void *>(pointer);
  return remember(hipSuccess);
}

extern "C" hipError_t hipMemset(void *destination, int value, size_t size) {
  if (destination == nullptr && size != 0) return remember(hipErrorInvalidValue);
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  const unsigned char byte = static_cast<unsigned char>(value);
  if (size % 4U == 0) return remember(map_hsa(hsa_amd_memory_fill(destination, uint32_t(byte) * 0x01010101U, size / 4U)));
  std::vector<unsigned char> bytes(size, byte);
  return hipMemcpy(destination, bytes.data(), size, hipMemcpyHostToDevice);
}
extern "C" hipError_t hipMemsetD8(hipDeviceptr_t destination, unsigned char value, size_t count) {
  return hipMemset(reinterpret_cast<void *>(destination), value, count);
}
extern "C" hipError_t hipMemsetD32(hipDeviceptr_t destination, int value, size_t count) {
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  return remember(map_hsa(hsa_amd_memory_fill(reinterpret_cast<void *>(destination), static_cast<uint32_t>(value), count)));
}
extern "C" hipError_t hipMemsetAsync(void *destination, int value, size_t size, hipStream_t) { return hipMemset(destination, value, size); }

extern "C" hipError_t __hipPushCallConfiguration(dim3 grid, dim3 block, size_t shared, hipStream_t stream) {
  launch_configuration = LaunchConfiguration{grid, block, shared, stream};
  return remember(hipSuccess);
}
extern "C" hipError_t __hipPopCallConfiguration(dim3 *grid, dim3 *block, size_t *shared, hipStream_t *stream) {
  if (grid == nullptr || block == nullptr || shared == nullptr || stream == nullptr) return remember(hipErrorInvalidValue);
  *grid = launch_configuration.grid;
  *block = launch_configuration.block;
  *shared = launch_configuration.shared;
  *stream = launch_configuration.stream;
  return remember(hipSuccess);
}
extern "C" hipError_t hipLaunchKernel(const void *function_address, dim3 grid, dim3 block, void **args, size_t shared, hipStream_t stream) {
  return dispatch(function_address, grid, block, args, shared, stream);
}

extern "C" hipError_t hipFuncGetAttributes(struct hipFuncAttributes *attributes, const void *function_address) {
  if (attributes == nullptr || function_address == nullptr) return remember(hipErrorInvalidValue);
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  std::lock_guard<std::mutex> guard(state().lock);
  if (!find_kernel(function_address)) return remember(hipErrorInvalidDeviceFunction);
  std::memset(attributes, 0, sizeof(*attributes));
  attributes->binaryVersion = 900;
  attributes->maxThreadsPerBlock = 1024;
  return remember(hipSuccess);
}

extern "C" hipError_t hipGetLastError(void) {
  const hipError_t result = last_error;
  last_error = hipSuccess;
  return result;
}
extern "C" hipError_t hipExtGetLastError(void) { return hipGetLastError(); }
extern "C" hipError_t hipPeekAtLastError(void) { return last_error; }
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
  if (stream == nullptr) return remember(hipErrorInvalidValue);
  *stream = reinterpret_cast<hipStream_t>(new (std::nothrow) unsigned char[1]);
  return remember(*stream == nullptr ? hipErrorOutOfMemory : hipSuccess);
}
extern "C" hipError_t hipStreamCreateWithFlags(hipStream_t *stream, unsigned int) { return hipStreamCreate(stream); }
extern "C" hipError_t hipStreamCreateWithPriority(hipStream_t *stream, unsigned int, int) { return hipStreamCreate(stream); }
extern "C" hipError_t hipStreamDestroy(hipStream_t stream) {
  if (stream != nullptr && stream != hipStreamLegacy && stream != hipStreamPerThread) delete[] reinterpret_cast<unsigned char *>(stream);
  return remember(hipSuccess);
}
extern "C" hipError_t hipStreamSynchronize(hipStream_t stream) {
  if (stream == hipStreamLegacy || stream == hipStreamPerThread) return remember(hipErrorInvalidResourceHandle);
  return hipDeviceSynchronize();
}
extern "C" hipError_t hipStreamQuery(hipStream_t stream) {
  if (stream == hipStreamLegacy || stream == hipStreamPerThread) return remember(hipErrorInvalidResourceHandle);
  return remember(hipSuccess);
}
extern "C" hipError_t hipStreamWaitEvent(hipStream_t, hipEvent_t event, unsigned int) {
  return remember(event == nullptr ? hipErrorInvalidResourceHandle : hipSuccess);
}
extern "C" hipError_t hipStreamGetDevice(hipStream_t stream, hipDevice_t *device) {
  if (device == nullptr) return remember(hipErrorInvalidValue);
  if (stream == hipStreamLegacy || stream == hipStreamPerThread) return remember(hipErrorInvalidResourceHandle);
  *device = 0;
  return remember(hipSuccess);
}
extern "C" int hipGetStreamDeviceId(hipStream_t) { return 0; }

extern "C" hipError_t hipStreamBeginCapture(hipStream_t, hipStreamCaptureMode) { return remember(hipErrorNotSupported); }
extern "C" hipError_t hipStreamEndCapture(hipStream_t, hipGraph_t *graph) {
  if (graph != nullptr) *graph = nullptr;
  return remember(hipErrorNotSupported);
}
extern "C" hipError_t hipStreamIsCapturing(hipStream_t, hipStreamCaptureStatus *status) {
  if (status == nullptr) return remember(hipErrorInvalidValue);
  *status = hipStreamCaptureStatusNone;
  return remember(hipSuccess);
}
extern "C" hipError_t hipGraphDestroy(hipGraph_t) { return remember(hipErrorNotSupported); }
extern "C" hipError_t hipGraphExecDestroy(hipGraphExec_t) { return remember(hipErrorNotSupported); }
extern "C" hipError_t hipGraphInstantiate(hipGraphExec_t *graph_exec, hipGraph_t, hipGraphNode_t *, char *, size_t) {
  if (graph_exec != nullptr) *graph_exec = nullptr;
  return remember(hipErrorNotSupported);
}
extern "C" hipError_t hipGraphLaunch(hipGraphExec_t, hipStream_t) { return remember(hipErrorNotSupported); }

extern "C" hipError_t hipModuleLoadData(hipModule_t *module, const void *image) {
  if (module == nullptr || image == nullptr) return remember(hipErrorInvalidValue);
  *module = nullptr;
  if (!ensure_initialized()) return remember(hipErrorNoDevice);
  auto loaded = create_module(image);
  if (trace_modules())
    std::fprintf(stderr, "Gemstone HIP module load image=%p loaded=%d kernels=%zu error=%s\n", image, loaded->loaded ? 1 : 0,
                 loaded->kernels.size(), loaded->error.c_str());
  if (!loaded->loaded) return remember(hipErrorInvalidImage);
  auto *handle = new (std::nothrow) ModuleHandle{std::move(loaded)};
  if (handle == nullptr) return remember(hipErrorOutOfMemory);
  {
    std::lock_guard<std::mutex> guard(state().lock);
    state().driver_modules.push_back(handle->module);
  }
  *module = reinterpret_cast<hipModule_t>(handle);
  if (trace_modules()) std::fprintf(stderr, "Gemstone HIP module handle=%p\n", static_cast<void *>(handle));
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleLoadDataEx(hipModule_t *module, const void *image, unsigned int, hipJitOption *, void **) {
  return hipModuleLoadData(module, image);
}

extern "C" hipError_t hipModuleLoadFatBinary(hipModule_t *module, const void *fatbin) { return hipModuleLoadData(module, fatbin); }

extern "C" hipError_t hipModuleUnload(hipModule_t module) {
  if (module == nullptr) return remember(hipErrorInvalidValue);
  delete module_handle(module);
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleGetFunction(hipFunction_t *function, hipModule_t module, const char *name) {
  if (function == nullptr || module == nullptr || name == nullptr) return remember(hipErrorInvalidValue);
  *function = nullptr;
  auto *handle = module_handle(module);
  if (handle->module == nullptr || !handle->module->loaded) return remember(hipErrorInvalidResourceHandle);
  std::shared_ptr<Module> selected = handle->module;
  auto found = selected->kernels.find(name);
  if (found == selected->kernels.end()) {
    std::lock_guard<std::mutex> guard(state().lock);
    auto output = state().driver_modules.begin();
    for (auto current = state().driver_modules.begin(); current != state().driver_modules.end(); ++current) {
      auto candidate = current->lock();
      if (!candidate) continue;
      *output++ = *current;
      const auto global = candidate->kernels.find(name);
      if (global != candidate->kernels.end() && found == selected->kernels.end()) {
        selected = std::move(candidate);
        found = global;
      }
    }
    state().driver_modules.erase(output, state().driver_modules.end());
  }
  if (trace_modules())
    std::fprintf(stderr, "Gemstone HIP module function module=%p name=%s found=%d\n", static_cast<void *>(handle), name,
                 found == selected->kernels.end() ? 0 : 1);
  if (trace_modules() && found == selected->kernels.end()) {
    size_t shown = 0;
    const std::string requested(name);
    const size_t separator = requested.find("_FSSC");
    const std::string prefix = requested.substr(0, separator);
    for (const auto &[candidate, _] : selected->kernels) {
      if (candidate.rfind(prefix, 0) != 0) continue;
      std::fprintf(stderr, "Gemstone HIP nearby module function=%s\n", candidate.c_str());
      if (++shown == 8) break;
    }
  }
  if (found == selected->kernels.end()) return remember(hipErrorInvalidDeviceFunction);
  auto *result = new (std::nothrow) FunctionHandle{std::move(selected), found->second};
  if (result == nullptr) return remember(hipErrorOutOfMemory);
  *function = reinterpret_cast<hipFunction_t>(result);
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleLaunchKernel(hipFunction_t function, unsigned int grid_x, unsigned int grid_y, unsigned int grid_z,
                                            unsigned int block_x, unsigned int block_y, unsigned int block_z, unsigned int shared,
                                            hipStream_t stream, void **args, void **extra) {
  if (function == nullptr) return remember(hipErrorInvalidDeviceFunction);
  gemstone::hip::LaunchBuffer launch_buffer;
  if (!gemstone::hip::parse_launch_buffer(extra, launch_buffer)) return remember(hipErrorInvalidValue);
  if (trace_modules())
    std::fprintf(stderr, "Gemstone HIP module launch f=%p grid=%u,%u,%u block=%u,%u,%u args=%p\n", function, grid_x, grid_y, grid_z,
                 block_x, block_y, block_z, static_cast<void *>(args));
  return dispatch_kernel(function_handle(function)->kernel, dim3(grid_x, grid_y, grid_z), dim3(block_x, block_y, block_z), args, shared,
                         stream, launch_buffer);
}

extern "C" hipError_t hipModuleGetFunctionCount(unsigned int *count, hipModule_t module) {
  if (count == nullptr || module == nullptr) return remember(hipErrorInvalidValue);
  auto *handle = module_handle(module);
  if (handle->module == nullptr) return remember(hipErrorInvalidResourceHandle);
  *count = static_cast<unsigned int>(handle->module->kernels.size());
  return remember(hipSuccess);
}

extern "C" hipError_t hipModuleOccupancyMaxActiveBlocksPerMultiprocessor(int *blocks, hipFunction_t function, int, size_t) {
  if (blocks == nullptr || function == nullptr) return remember(hipErrorInvalidValue);
  if (!function_handle(function)->kernel) return remember(hipErrorInvalidDeviceFunction);
  *blocks = 1;
  return remember(hipSuccess);
}
extern "C" hipError_t hipExtModuleLaunchKernel(hipFunction_t function, uint32_t global_x, uint32_t global_y, uint32_t global_z,
                                               uint32_t local_x, uint32_t local_y, uint32_t local_z, size_t shared, hipStream_t stream,
                                               void **args, void **extra, hipEvent_t, hipEvent_t, uint32_t) {
  if (function == nullptr) return remember(hipErrorInvalidDeviceFunction);
  if (local_x == 0 || local_y == 0 || local_z == 0) return remember(hipErrorInvalidConfiguration);
  gemstone::hip::LaunchBuffer launch_buffer;
  if (!gemstone::hip::parse_launch_buffer(extra, launch_buffer)) return remember(hipErrorInvalidValue);
  if (trace_modules())
    std::fprintf(stderr, "Gemstone HIP ext module launch f=%p global=%u,%u,%u local=%u,%u,%u args=%p\n", function, global_x, global_y,
                 global_z, local_x, local_y, local_z, static_cast<void *>(args));
  return dispatch_kernel(function_handle(function)->kernel,
                         dim3(workgroup_count(global_x, local_x), workgroup_count(global_y, local_y), workgroup_count(global_z, local_z)),
                         dim3(local_x, local_y, local_z), args, shared, stream, launch_buffer);
}

extern "C" hipError_t hipEventCreateWithFlags(hipEvent_t *event, unsigned int) {
  if (event == nullptr) return remember(hipErrorInvalidValue);
  *event = reinterpret_cast<hipEvent_t>(new (std::nothrow) unsigned char[1]);
  return remember(*event == nullptr ? hipErrorOutOfMemory : hipSuccess);
}
extern "C" hipError_t hipEventCreate(hipEvent_t *event) { return hipEventCreateWithFlags(event, 0); }
extern "C" hipError_t hipEventDestroy(hipEvent_t event) {
  if (event != nullptr) delete[] reinterpret_cast<unsigned char *>(event);
  return remember(hipSuccess);
}
extern "C" hipError_t hipEventRecordWithFlags(hipEvent_t event, hipStream_t, unsigned int) {
  return remember(event == nullptr ? hipErrorInvalidResourceHandle : hipSuccess);
}
extern "C" hipError_t hipEventRecord(hipEvent_t event, hipStream_t stream) { return hipEventRecordWithFlags(event, stream, 0); }
extern "C" hipError_t hipEventSynchronize(hipEvent_t event) {
  return remember(event == nullptr ? hipErrorInvalidResourceHandle : hipSuccess);
}
extern "C" hipError_t hipEventQuery(hipEvent_t event) { return remember(event == nullptr ? hipErrorInvalidResourceHandle : hipSuccess); }
extern "C" hipError_t hipEventElapsedTime(float *milliseconds, hipEvent_t start, hipEvent_t stop) {
  if (milliseconds == nullptr) return remember(hipErrorInvalidValue);
  if (start == nullptr || stop == nullptr) return remember(hipErrorInvalidResourceHandle);
  *milliseconds = 0.0f;
  return remember(hipSuccess);
}

extern "C" void **__hipRegisterFatBinary(const void *fatbin) {
  auto module = std::make_shared<Module>();
  const void *image = nullptr;
  size_t image_size = 0;
  const bool selected = select_bundle(fatbin, &image, &image_size, &module->decompressed_image);
  if (!selected) module->error = std::string("HIP fatbinary has no ") + gemstone::hsa::target_arch() + " bundle";
  else {
    module->image = image;
    module->image_size = image_size;
  }
  void **token = &module->token;
  {
    auto &runtime = state();
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.modules.emplace(token, module);
  }
  if (ensure_initialized()) {
    auto &runtime = state();
    std::lock_guard<std::mutex> guard(runtime.lock);
    (void)load_module(*module);
  }
  if (trace_modules())
    std::fprintf(stderr, "Gemstone HIP fatbinary=%p selected=%d loaded=%d kernels=%zu image_size=%zu error=%s\n", fatbin, selected ? 1 : 0,
                 module->loaded ? 1 : 0, module->kernels.size(), module->image_size, module->error.c_str());
  return token;
}

extern "C" void __hipRegisterFunction(void **modules, const void *host_function, char *, const char *device_name, unsigned int, uint3 *,
                                      uint3 *, dim3 *, dim3 *, int *) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto module = find_module(modules);
  if (!module || !module->loaded || host_function == nullptr || device_name == nullptr) return;
  const auto found = module->kernels.find(device_name);
  if (found != module->kernels.end()) runtime.kernels[host_function] = found->second;
  else if (const auto *metadata = find_metadata(*module, device_name)) {
    const auto candidate = module->kernels.find(metadata->name);
    if (candidate != module->kernels.end()) runtime.kernels[host_function] = candidate->second;
  }
}

extern "C" void __hipRegisterVar(void **modules, void *var, char *, char *device_name, int, size_t size, int, int) {
  if (modules == nullptr || var == nullptr || size == 0) return;
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto module = find_module(modules);
  if (!module) return;
  const auto [iterator, inserted] =
      runtime.variables.insert_or_assign(var, Variable{var, device_name == nullptr ? std::string{} : std::string(device_name), size});
  (void)iterator;
  if (inserted) module->variables.push_back(var);
}

extern "C" void __hipUnregisterFatBinary(void **modules) {
  auto &runtime = state();
  std::shared_ptr<Module> module;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.modules.find(modules);
    if (found == runtime.modules.end()) return;
    module = found->second;
    runtime.modules.erase(found);
    if (module) {
      for (const auto &[name, kernel] : module->kernels) {
        (void)name;
        for (auto iterator = runtime.kernels.begin(); iterator != runtime.kernels.end();) {
          if (iterator->second == kernel) iterator = runtime.kernels.erase(iterator);
          else ++iterator;
        }
      }
      for (const void *variable : module->variables)
        runtime.variables.erase(variable);
    }
  }
}
