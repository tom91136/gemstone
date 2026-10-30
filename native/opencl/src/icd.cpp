#include <algorithm>
#include <array>
#include <atomic>
#include <bit>
#include <chrono>
#include <cmath>
#include <condition_variable>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <exception>
#include <functional>
#include <iterator>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <new>
#include <optional>
#include <set>
#include <string>
#include <string_view>
#include <type_traits>
#include <utility>
#include <variant>
#include <vector>

#include <aspartame/array.hpp>
#include <aspartame/string.hpp>
#include <aspartame/string_view.hpp>
#include <aspartame/vector.hpp>

#include "gemstone/command_queue.hpp"
#include "gemstone/environment.hpp"
#include "gemstone_execution_rpc.hpp"
#include "gemstone_image_descriptor.hpp"
#include "gemstone_opencl_dispatch.hpp"
#include "gemstone_pipe_layout.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/copy_out.hpp>
#include <gemstone/live_handles.hpp>
#include <gemstone/trace.hpp>

#include "client.hpp"
#include "compiler.hpp"
#include "completion.hpp"
#include "objects.hpp"
#include "relocations.hpp"
#include "transfer.hpp"
#include <environment_keys.hpp>

namespace {

using namespace aspartame;
using namespace gemstone::execution;

using namespace gemstone::opencl;
namespace descriptor = gemstone::spirv::ImageDescriptor;
namespace pipe = gemstone::spirv::PipeLayout;

gemstone::LiveHandles<Kind> liveObjects;

template <class T> void registerLiveObject(const T *value) { liveObjects.insert(value, T::ObjectKind); }

template <class T> T *checked(T *value) { return liveObjects.contains(value, T::ObjectKind) ? value : nullptr; }

// Host commands treat on-device queues as invalid; queue_t arguments and queue management accept either kind.
_cl_command_queue *anyQueue(cl_command_queue value) { return liveObjects.contains(value, Kind::Queue) ? value : nullptr; }
_cl_command_queue *checked(_cl_command_queue *value) {
  auto *queue = anyQueue(value);
  return queue && queue->deviceSize == 0 ? queue : nullptr;
}

template <class T> void retain(T *value) { value->header.references.fetch_add(1, std::memory_order_relaxed); }

template <class T> bool release(T *value) { return value->header.references.fetch_sub(1, std::memory_order_acq_rel) == 1; }

template <class T> cl_int retainObject(T *value, cl_int invalid) {
  if (!value) return invalid;
  retain(value);
  return CL_SUCCESS;
}

std::nullptr_t fail(cl_int *error, cl_int status) {
  if (error) *error = status;
  return nullptr;
}

template <class T> T *succeed(cl_int *error, T *value) {
  if (error) *error = CL_SUCCESS;
  return value;
}

ClientManager remote;
std::mutex remoteMutex;
bool hostMemoryEnabled = false; // Guarded by remoteMutex.
std::mutex kernelSubmissionMutex;
std::mutex pointerRelocationsMutex;
std::mutex allocationsMutex;
std::mutex eventsMutex;
std::condition_variable eventsCondition;
std::map<uintptr_t, std::shared_ptr<Allocation>> allocations;
std::map<uintptr_t, std::shared_ptr<BufferMapping>> svmMappings;
std::once_flag dispatchInitialised;
cl_icd_dispatch dispatch{};
_cl_platform_id platform;
_cl_device_id device;

template <class T> void destroyObject(T *object) {
  liveObjects.erase(object);
  delete object;
}

template <class T> T *newObject() {
  auto *object = new (std::nothrow) T;
  if (object) {
    registerLiveObject(object);
    object->dispatch = &dispatch;
  }
  return object;
}

std::atomic_uint64_t nextContextIdentity{1};
bool schedulerStopping = false;

// ICD loaders load every installed ICD, so workers start with the first submission. Teardown of layered runtimes may
// call the ICD after the workers stop, so the queue is never destroyed.
gemstone::CommandQueue &scheduler() {
  static auto *const queue = new gemstone::CommandQueue(CL_OUT_OF_RESOURCES);
  return *queue;
}

// Constructed with the library, so it stops the workers after teardown registered later and before the state above
// is destroyed. Jobs must not keep waiting for events the host never completes.
struct SchedulerShutdown {
  SchedulerShutdown() = default;
  SchedulerShutdown(const SchedulerShutdown &) = delete;
  SchedulerShutdown &operator=(const SchedulerShutdown &) = delete;
  ~SchedulerShutdown() {
    {
      std::scoped_lock const lock(eventsMutex);
      schedulerStopping = true;
    }
    eventsCondition.notify_all();
    scheduler().shutdown();
  }
} schedulerShutdown;

constexpr cl_name_version DeviceExtensionVersions[] = {
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_icd"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_il_program"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_byte_addressable_store"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_3d_image_writes"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_image2d_from_buffer"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_depth_images"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_fp16"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_fp64"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_pci_bus_info"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_global_int32_base_atomics"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_global_int32_extended_atomics"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_local_int32_base_atomics"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_local_int32_extended_atomics"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_int64_base_atomics"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_int64_extended_atomics"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_extended_bit_ops"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_suggested_local_work_size"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_create_command_queue"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_device_uuid"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_spirv_queries"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_expect_assume"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_spirv_no_integer_wrap_decoration"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_spirv_linkonce_odr"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_ext_immutable_memory_objects"},
    {.version = CL_MAKE_VERSION(2, 0, 0), .name = "cl_khr_integer_dot_product"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_kernel_clock"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroups"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroup_ballot"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroup_non_uniform_vote"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroup_non_uniform_arithmetic"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroup_shuffle"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroup_shuffle_relative"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroup_extended_types"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroup_clustered_reduce"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_khr_subgroup_rotate"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_subgroups"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_spirv_subgroups"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_bfloat16_conversions"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_split_work_group_barrier"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_media_block_io"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_spirv_media_block_io"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_subgroup_matrix_multiply_accumulate"},
    {.version = CL_MAKE_VERSION(0, 0, 0), .name = "cl_intel_device_attribute_query"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_required_subgroup_size"},
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "cl_intel_unified_shared_memory"}};

constexpr cl_name_version DeviceILVersions[] = {
    {.version = CL_MAKE_VERSION(1, 0, 0), .name = "SPIR-V"}, {.version = CL_MAKE_VERSION(1, 1, 0), .name = "SPIR-V"},
    {.version = CL_MAKE_VERSION(1, 2, 0), .name = "SPIR-V"}, {.version = CL_MAKE_VERSION(1, 3, 0), .name = "SPIR-V"},
    {.version = CL_MAKE_VERSION(1, 4, 0), .name = "SPIR-V"}, {.version = CL_MAKE_VERSION(1, 5, 0), .name = "SPIR-V"},
    {.version = CL_MAKE_VERSION(1, 6, 0), .name = "SPIR-V"}};

const std::string DeviceExtensionString = [] {
  std::string result;
  for (const auto &extension : DeviceExtensionVersions) {
    if (!result.empty()) result += ' ';
    result += extension.name;
  }
  return result;
}();

const std::string DeviceILVersionString = [] {
  std::string result;
  for (const auto &il : DeviceILVersions) {
    if (!result.empty()) result += ' ';
    result +=
        std::string(il.name) + '_' + std::to_string(CL_VERSION_MAJOR(il.version)) + '.' + std::to_string(CL_VERSION_MINOR(il.version));
  }
  return result;
}();

struct OpenCLIdentity {
  const char *platformName;
  const char *deviceName;
  const char *vendor;
  cl_uint vendorId;
};

OpenCLIdentity openclIdentity() {
  const char *profile = gemstone::environment::value(gemstone::environment::ProfileName);
  if (profile) {
    if (std::strcmp(profile, "amd") == 0)
      return {.platformName = "AMD Accelerated Parallel Processing",
              .deviceName = "AMD Gemstone GPU",
              .vendor = "Advanced Micro Devices, Inc.",
              .vendorId = 0x1002};
    if (std::strcmp(profile, "nvidia") == 0)
      return {.platformName = "NVIDIA CUDA", .deviceName = "NVIDIA Gemstone GPU", .vendor = "NVIDIA Corporation", .vendorId = 0x10de};
    if (std::strcmp(profile, "rusticl-amd") == 0)
      return {.platformName = "rusticl", .deviceName = "AMD Gemstone GPU", .vendor = "Advanced Micro Devices, Inc.", .vendorId = 0x1002};
    if (std::strcmp(profile, "rusticl-intel") == 0)
      return {.platformName = "rusticl", .deviceName = "Intel Gemstone GPU", .vendor = "Intel(R) Corporation", .vendorId = 0x8086};
    // llvmpipe has no OpenCL driver of its own; Mesa serves it through rusticl
    if (std::strcmp(profile, "rusticl-llvmpipe") == 0 || std::strcmp(profile, "llvmpipe") == 0)
      return {.platformName = "rusticl", .deviceName = "llvmpipe Gemstone GPU", .vendor = "Mesa", .vendorId = 0x10005};
  }
  return {.platformName = "Intel(R) OpenCL Gemstone Graphics",
          .deviceName = "Gemstone SPIR-V GPU",
          .vendor = "Intel(R) Corporation",
          .vendorId = 0x8086};
}

uint64_t nowNanos() {
  return static_cast<uint64_t>(
      std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now().time_since_epoch()).count());
}

void notifyEventCallbacks(_cl_event *event) {
  std::vector<_cl_event::Callback> ready;
  cl_int status = CL_COMPLETE;
  {
    std::scoped_lock const lock(eventsMutex);
    status = event->status;
    for (auto callback = event->callbacks.begin(); callback != event->callbacks.end();) {
      if (status < 0 || status <= callback->trigger) {
        ready.push_back(*callback);
        callback = event->callbacks.erase(callback);
      } else {
        ++callback;
      }
    }
  }
  for (const auto &callback : ready)
    callback.function(reinterpret_cast<cl_event>(event), status, callback.user);
}

bool writeRemote(Client &client, uint64_t id, uint64_t offset, const void *bytes, size_t size) {
  return uploadAllocation(client, AllocationId{static_cast<int64_t>(id)}, offset, bytes, size, MaximumPayload / 2);
}

bool readRemote(Client &client, uint64_t id, uint64_t offset, void *bytes, size_t size) {
  return downloadAllocation(client, AllocationId{static_cast<int64_t>(id)}, offset, bytes, size, MaximumPayload / 2);
}

bool scanBufferPointerRelocations() { return gemstone::environment::enabled(gemstone::environment::OpenclScanBufferPointersOn); }

// Keep the advertised limit below the remote service's practical per-allocation envelope.
constexpr cl_ulong MaxMemAllocSize = cl_ulong{1} << 28;
constexpr cl_ulong LocalMemSize = cl_ulong{1} << 16;
// Enqueue enforces the advertised bound.
constexpr size_t MaxWorkGroupSize = gemstone::execution::OpenclMaxWorkGroupSize;
constexpr size_t MaxSubgroupSize = 32;
constexpr size_t Image2DMaxSize = 16384;
constexpr size_t Image3DMaxSize = 2048;
constexpr size_t ImageMaxArraySize = 2048;
constexpr size_t ImageMaxBufferSize = size_t{1} << 24;
constexpr cl_uint PipeMaxPacketSize = 1024;
namespace spirv_capability {
constexpr cl_uint Addresses = 4, Linkage = 5, Kernel = 6, Vector16 = 7, Float16Buffer = 8, Float16 = 9, Float64 = 10, Int64 = 11,
                  Int64Atomics = 12, ImageBasic = 13, ImageReadWrite = 14, Pipes = 17, Groups = 18, DeviceEnqueue = 19, LiteralSampler = 20,
                  Int16 = 22, GenericPointer = 38, Int8 = 39, Sampled1D = 43, Image1D = 44, SampledBuffer = 46, ImageBuffer = 47,
                  SubgroupDispatch = 58, GroupNonUniform = 61, GroupNonUniformVote = 62, GroupNonUniformArithmetic = 63,
                  GroupNonUniformBallot = 64, GroupNonUniformShuffle = 65, GroupNonUniformShuffleRelative = 66,
                  GroupNonUniformClustered = 67, UniformDecoration = 71, SubgroupShuffleINTEL = 5568, SubgroupBufferBlockIOINTEL = 5569,
                  SubgroupImageBlockIOINTEL = 5570, ExpectAssumeKHR = 5629, DotProductInput4x8Bit = 6017,
                  DotProductInput4x8BitPacked = 6018, DotProduct = 6019, BitInstructions = 6025, GroupNonUniformRotateKHR = 6026,
                  ShaderClockKHR = 5055, SubgroupImageMediaBlockIOINTEL = 5579, BFloat16ConversionINTEL = 6115, SplitBarrierINTEL = 6141,
                  SubgroupMatrixMultiplyAccumulateINTEL = 6236;
} // namespace spirv_capability

// The SPIR-V the interpreter executes, consistent with the features and extensions above.
constexpr const char *SpirvExtendedInstructionSets[] = {"OpenCL.std"};
constexpr const char *SpirvExtensions[] = {"SPV_KHR_expect_assume",         "SPV_KHR_bit_instructions",
                                           "SPV_KHR_integer_dot_product",   "SPV_KHR_no_integer_wrap_decoration",
                                           "SPV_KHR_subgroup_rotate",       "SPV_INTEL_subgroups",
                                           "SPV_KHR_linkonce_odr",          "SPV_KHR_shader_clock",
                                           "SPV_INTEL_bfloat16_conversion", "SPV_INTEL_split_barrier",
                                           "SPV_INTEL_media_block_io",      "SPV_INTEL_subgroup_matrix_multiply_accumulate"};
constexpr cl_uint SpirvCapabilities[] = {spirv_capability::Addresses,
                                         spirv_capability::Linkage,
                                         spirv_capability::Kernel,
                                         spirv_capability::Vector16,
                                         spirv_capability::Float16Buffer,
                                         spirv_capability::Float16,
                                         spirv_capability::Float64,
                                         spirv_capability::Int64,
                                         spirv_capability::Int64Atomics,
                                         spirv_capability::ImageBasic,
                                         spirv_capability::ImageReadWrite,
                                         spirv_capability::Pipes,
                                         spirv_capability::Groups,
                                         spirv_capability::DeviceEnqueue,
                                         spirv_capability::LiteralSampler,
                                         spirv_capability::Int16,
                                         spirv_capability::GenericPointer,
                                         spirv_capability::Int8,
                                         spirv_capability::Sampled1D,
                                         spirv_capability::Image1D,
                                         spirv_capability::SampledBuffer,
                                         spirv_capability::ImageBuffer,
                                         spirv_capability::SubgroupDispatch,
                                         spirv_capability::GroupNonUniform,
                                         spirv_capability::GroupNonUniformVote,
                                         spirv_capability::GroupNonUniformArithmetic,
                                         spirv_capability::GroupNonUniformBallot,
                                         spirv_capability::GroupNonUniformShuffle,
                                         spirv_capability::GroupNonUniformShuffleRelative,
                                         spirv_capability::GroupNonUniformClustered,
                                         spirv_capability::UniformDecoration,
                                         spirv_capability::SubgroupShuffleINTEL,
                                         spirv_capability::SubgroupBufferBlockIOINTEL,
                                         spirv_capability::SubgroupImageBlockIOINTEL,
                                         spirv_capability::ExpectAssumeKHR,
                                         spirv_capability::DotProductInput4x8Bit,
                                         spirv_capability::DotProductInput4x8BitPacked,
                                         spirv_capability::DotProduct,
                                         spirv_capability::BitInstructions,
                                         spirv_capability::GroupNonUniformRotateKHR,
                                         spirv_capability::ShaderClockKHR,
                                         spirv_capability::SubgroupImageMediaBlockIOINTEL,
                                         spirv_capability::BFloat16ConversionINTEL,
                                         spirv_capability::SplitBarrierINTEL,
                                         spirv_capability::SubgroupMatrixMultiplyAccumulateINTEL};

constexpr cl_uint ImagePitchAlignment = 1;
constexpr cl_uint ImageBaseAddressAlignment = 1;
constexpr cl_uint DeviceQueuePreferredSize = 128 * 1024;
constexpr cl_uint DeviceQueueMaxSize = 8 * 1024 * 1024;
constexpr cl_uint MaxDeviceQueues = 4;
constexpr cl_uint MaxDeviceEvents = 1024;
constexpr size_t MaxGlobalVariableSize = 65536;
constexpr size_t PreferredGlobalVariableTotalSize = 1U << 20;

std::array<size_t, 3> maxWorkItemSizes() {
  const size_t maxGroup = MaxWorkGroupSize;
  return {maxGroup, maxGroup, std::min<size_t>(maxGroup, 64)};
}

std::shared_ptr<Allocation> findPointer(const void *pointer, size_t *offset = nullptr) {
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::scoped_lock const lock(allocationsMutex);
  const auto entry = allocations.upper_bound(address);
  if (entry == allocations.begin()) return {};
  const auto candidate = std::prev(entry);
  const auto start = candidate->first;
  if (address >= start && address - start <= candidate->second->size) {
    if (offset) *offset = address - start;
    return candidate->second;
  }
  return {};
}

bool validRange(const std::shared_ptr<Allocation> &allocation, size_t offset, size_t size) {
  return allocation && offset <= allocation->size && size <= allocation->size - offset;
}

std::shared_ptr<Allocation> findAllocation(uint64_t id) {
  std::scoped_lock const lock(allocationsMutex);
  for (const auto &[_, allocation] : allocations)
    if (allocation->id == id) return allocation;
  return {};
}

bool validMemoryRange(const _cl_mem *mem, size_t offset, size_t size) { return mem && offset <= mem->size && size <= mem->size - offset; }

bool hostReadAllowed(const _cl_mem *mem) { return mem && (mem->flags & (CL_MEM_HOST_WRITE_ONLY | CL_MEM_HOST_NO_ACCESS)) == 0; }

// Immutable objects take their contents from the host at creation and are read-only to the host and device afterwards.
bool immutable(const _cl_mem *mem) { return (mem->flags & CL_MEM_IMMUTABLE_EXT) != 0; }
bool hostWriteAllowed(const _cl_mem *mem) {
  return mem && (mem->flags & (CL_MEM_HOST_READ_ONLY | CL_MEM_HOST_NO_ACCESS)) == 0 && !immutable(mem);
}

cl_int immutableFlags(cl_mem_flags flags) {
  constexpr cl_mem_flags writable = CL_MEM_READ_WRITE | CL_MEM_WRITE_ONLY | CL_MEM_KERNEL_READ_AND_WRITE | CL_MEM_HOST_WRITE_ONLY;
  if (!(flags & CL_MEM_IMMUTABLE_EXT)) return CL_SUCCESS;
  return (flags & (CL_MEM_USE_HOST_PTR | CL_MEM_COPY_HOST_PTR)) && !(flags & writable) ? CL_SUCCESS : CL_INVALID_VALUE;
}

void invalidatePointerRelocations(const std::shared_ptr<Allocation> &allocation, size_t offset, size_t size) {
  if (!allocation || size == 0) return;
  std::scoped_lock const relocationsLock(pointerRelocationsMutex);
  const size_t end = offset > std::numeric_limits<size_t>::max() - size ? std::numeric_limits<size_t>::max() : offset + size;
  for (auto it = allocation->pointerRelocations.begin(); it != allocation->pointerRelocations.end();) {
    const size_t pointerEnd = it->first > std::numeric_limits<size_t>::max() - sizeof(uint64_t) ? std::numeric_limits<size_t>::max()
                                                                                                : it->first + sizeof(uint64_t);
    if (it->first < end && pointerEnd > offset) it = allocation->pointerRelocations.erase(it);
    else ++it;
  }
}

void copyPointerRelocations(const std::shared_ptr<Allocation> &source, size_t sourceOffset, const std::shared_ptr<Allocation> &target,
                            size_t targetOffset, size_t size) {
  if (!source || !target || size < sizeof(uint64_t)) return;
  std::scoped_lock const relocationsLock(pointerRelocationsMutex);
  const size_t sourceEnd =
      sourceOffset > std::numeric_limits<size_t>::max() - size ? std::numeric_limits<size_t>::max() : sourceOffset + size;
  for (const auto &[offset, relocation] : source->pointerRelocations) {
    if (offset >= sourceOffset && offset <= sourceEnd - sizeof(uint64_t)) {
      const size_t targetOffsetForPointer = targetOffset + offset - sourceOffset;
      if (targetOffsetForPointer >= targetOffset) target->pointerRelocations[targetOffsetForPointer] = relocation;
    }
  }
}

size_t allocationOffset(const _cl_mem *mem, size_t offset) { return mem->origin + offset; }

std::shared_ptr<Allocation> allocate(size_t size, cl_int *error, size_t requestedAlignment = 128, bool hostBacked = false) {
  if (size == 0) {
    if (error) *error = CL_INVALID_BUFFER_SIZE;
    return {};
  }
  const size_t requested = requestedAlignment == 0 ? 128 : requestedAlignment;
  if ((requested & (requested - 1)) != 0) {
    if (error) *error = CL_INVALID_VALUE;
    return {};
  }
  const size_t alignment = std::max(sizeof(void *), requested);
  std::unique_ptr<void, Allocation::AlignedDelete> storage(::operator new(size, std::align_val_t{alignment}, std::nothrow),
                                                           Allocation::AlignedDelete{alignment});
  if (!storage) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return {};
  }
  void *base = storage.get();
  std::shared_ptr<Allocation> result;
  try {
    result = std::make_shared<Allocation>();
  } catch (const std::bad_alloc &) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return {};
  }
  result->storage = std::move(storage);
  result->size = size;
  result->hostBacked = hostBacked;
  auto &state = remote;
  if (state.ready()) {
    std::scoped_lock const lock(remoteMutex);
    const auto registered = registerAllocation(*state.client(), size, base, hostBacked);
    if (!registered) {
      if (gemstone::traceEnabled("opencl")) std::fprintf(stderr, "Gemstone OpenCL allocation failed: %s\n", remote.error().c_str());
      if (error) *error = CL_OUT_OF_RESOURCES;
      return {};
    }
    result->id = static_cast<uint64_t>(registered->value);
  }
  bool inserted = false;
  {
    std::scoped_lock const lock(allocationsMutex);
    const auto key = reinterpret_cast<uintptr_t>(base);
    inserted = allocations.emplace(key, result).second;
  }
  if (!inserted) {
    if (state.ready()) {
      std::scoped_lock const lock(remoteMutex);
      (void)state.client()->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(result->id)}});
    }
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return {};
  }
  if (gemstone::traceEnabled("opencl", "allocations"))
    std::fprintf(stderr, "gemstone alloc base=%p id=%llu size=%zu\n", base, static_cast<unsigned long long>(result->id), size);
  return result;
}

cl_int releaseAllocation(const std::shared_ptr<Allocation> &allocation, bool releaseRuntimeBase = false) {
  if (!allocation) return CL_SUCCESS;
  if (allocation->released.exchange(true, std::memory_order_acq_rel)) return CL_SUCCESS;
  {
    std::scoped_lock const lock(allocationsMutex);
    const auto entry = allocations.find(reinterpret_cast<uintptr_t>(allocation->data()));
    if (entry != allocations.end() && entry->second.get() == allocation.get()) allocations.erase(entry);
  }
  auto &state = remote;
  cl_int result = CL_SUCCESS;
  if (state.ready()) {
    std::scoped_lock const lock(remoteMutex);
    if (!state.client()->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(allocation->id)}})) result = CL_OUT_OF_RESOURCES;
  }
  if (!allocation->runtimeOwnsBase || releaseRuntimeBase) allocation->storage.reset();
  return result;
}

cl_int writeAllocation(const std::shared_ptr<Allocation> &allocation, size_t offset, const void *bytes, size_t size) {
  if (!validRange(allocation, offset, size) || (size != 0 && !bytes)) return CL_INVALID_VALUE;
  invalidatePointerRelocations(allocation, offset, size);
  if (size != 0) std::memcpy(static_cast<uint8_t *>(allocation->data()) + offset, bytes, size);
  auto &state = remote;
  if (state.ready() && size != 0 && !allocation->hostBacked) {
    std::scoped_lock const lock(remoteMutex);
    if (!writeRemote(*state.client(), allocation->id, offset, bytes, size)) return CL_OUT_OF_RESOURCES;
  }
  return CL_SUCCESS;
}

// The execution service stores device addresses in linked structures. Kernel
// completion translates them for the host, but a later map reads the raw
// remote bytes again. Reapply the known translations to the mapped range.
void reapplyPointerRelocations(const std::shared_ptr<Allocation> &allocation, size_t offset, void *bytes, size_t size) {
  std::scoped_lock const relocationsLock(pointerRelocationsMutex);
  for (const auto &[pointerOffset, target] : allocation->pointerRelocations) {
    if (pointerOffset < offset || pointerOffset - offset > size || sizeof(uintptr_t) > size - (pointerOffset - offset)) continue;
    if (!target.first || target.first->released.load(std::memory_order_acquire) || target.second > target.first->size) continue;
    const uintptr_t address = reinterpret_cast<uintptr_t>(target.first->data()) + target.second;
    std::memcpy(static_cast<uint8_t *>(allocation->data()) + pointerOffset, &address, sizeof(address));
    std::memcpy(static_cast<uint8_t *>(bytes) + pointerOffset - offset, &address, sizeof(address));
  }
}

cl_int readAllocation(const std::shared_ptr<Allocation> &allocation, size_t offset, void *bytes, size_t size) {
  if (!validRange(allocation, offset, size) || (size != 0 && !bytes)) return CL_INVALID_VALUE;
  auto &state = remote;
  if (state.ready() && size != 0 && !allocation->hostBacked) {
    std::scoped_lock const lock(remoteMutex);
    if (!readRemote(*state.client(), allocation->id, offset, bytes, size)) return CL_OUT_OF_RESOURCES;
    std::memcpy(static_cast<uint8_t *>(allocation->data()) + offset, bytes, size);
  } else if (size != 0) {
    std::memcpy(bytes, static_cast<uint8_t *>(allocation->data()) + offset, size);
  }
  reapplyPointerRelocations(allocation, offset, bytes, size);
  return CL_SUCCESS;
}

bool validRectangle(const std::shared_ptr<Allocation> &allocation, const Rectangle &rectangle) {
  constexpr size_t maximum = std::numeric_limits<size_t>::max();
  if (!allocation || rectangle.rowBytes == 0 || rectangle.rowCount == 0 || rectangle.sliceCount == 0) return false;
  size_t extent = rectangle.rowBytes;
  for (const auto &[count, pitch] :
       {std::pair{rectangle.rowCount, rectangle.rowPitch}, std::pair{rectangle.sliceCount, rectangle.slicePitch}}) {
    if (count > 1 && pitch != 0 && count - 1 > (maximum - extent) / pitch) return false;
    extent += (count - 1) * pitch;
  }
  return validRange(allocation, rectangle.offset, extent);
}

// Rows of the host side sit at hostRowPitch and hostSlicePitch from host; pitches of zero repeat the first row.
cl_int writeAllocationRectangle(const std::shared_ptr<Allocation> &allocation, const Rectangle &rectangle, const void *host,
                                size_t hostRowPitch, size_t hostSlicePitch) {
  if (!validRectangle(allocation, rectangle) || !host) return CL_INVALID_VALUE;
  const auto *source = static_cast<const uint8_t *>(host);
  forEachRectangleRow(rectangle, [&](uint64_t offset, size_t slice, size_t row) {
    invalidatePointerRelocations(allocation, offset, rectangle.rowBytes);
    std::memcpy(static_cast<uint8_t *>(allocation->data()) + offset, source + (slice * hostSlicePitch) + (row * hostRowPitch),
                rectangle.rowBytes);
    return true;
  });
  auto &state = remote;
  if (state.ready() && !allocation->hostBacked) {
    std::scoped_lock const lock(remoteMutex);
    if (!uploadRectangle(*state.client(), AllocationId{static_cast<int64_t>(allocation->id)}, rectangle, host, hostRowPitch, hostSlicePitch,
                         MaximumPayload / 2))
      return CL_OUT_OF_RESOURCES;
  }
  return CL_SUCCESS;
}

cl_int readAllocationRectangle(const std::shared_ptr<Allocation> &allocation, const Rectangle &rectangle, void *host, size_t hostRowPitch,
                               size_t hostSlicePitch) {
  if (!validRectangle(allocation, rectangle) || !host) return CL_INVALID_VALUE;
  auto *target = static_cast<uint8_t *>(host);
  auto *mirror = static_cast<uint8_t *>(allocation->data());
  auto &state = remote;
  const bool fetched = state.ready() && !allocation->hostBacked;
  if (fetched) {
    std::scoped_lock const lock(remoteMutex);
    if (!downloadRectangle(*state.client(), AllocationId{static_cast<int64_t>(allocation->id)}, rectangle, host, hostRowPitch,
                           hostSlicePitch, MaximumPayload / 2))
      return CL_OUT_OF_RESOURCES;
  }
  forEachRectangleRow(rectangle, [&](uint64_t offset, size_t slice, size_t row) {
    auto *hostRow = target + (slice * hostSlicePitch) + (row * hostRowPitch);
    if (fetched) std::memcpy(mirror + offset, hostRow, rectangle.rowBytes);
    else std::memcpy(hostRow, mirror + offset, rectangle.rowBytes);
    reapplyPointerRelocations(allocation, offset, hostRow, rectangle.rowBytes);
    return true;
  });
  return CL_SUCCESS;
}

template <class T> cl_int outputScalar(size_t capacity, void *output, size_t *written, const T &value) {
  return gemstone::copyOutValue(capacity, output, written, value) ? CL_SUCCESS : CL_INVALID_VALUE;
}

cl_int outputBytes(size_t capacity, void *output, size_t *written, const void *bytes, size_t size) {
  return gemstone::copyOut(capacity, output, written, bytes, size) ? CL_SUCCESS : CL_INVALID_VALUE;
}

cl_int outputString(size_t capacity, void *output, size_t *written, const std::string &value) {
  return outputBytes(capacity, output, written, value.c_str(), value.size() + 1);
}

bool validPlatform(cl_platform_id value) { return checked(value) == &platform; }
bool validDevice(cl_device_id value) { return checked(value) == &device; }

void initialiseDispatch();

size_t kernelCount(const std::string &names) {
  if (names.empty()) return 0;
  return static_cast<size_t>(std::count(names.begin(), names.end(), ';')) + 1;
}

bool containsKernelName(const std::string &names, const std::string &name) {
  auto parts = names ^ split(';');
  if (!parts.empty() && parts.back().empty()) parts.pop_back();
  return parts ^ contains(name);
}

bool hasBuildOption(const char *options, std::string_view option) {
  return options && (std::string_view(options) ^ contains_slice(option));
}

bool uniformWorkGroups(const _cl_program &program) {
  const auto &options = program.buildOptions;
  if (options ^ contains_slice("-cl-uniform-work-group-size")) return true;
  if (program.createdWithIL) return false;
  const auto standard = options.rfind("-cl-std=CL");
  return standard == std::string::npos || options.compare(standard + 10, 2, "1.") == 0;
}

size_t largestDivisor(size_t value, size_t limit) {
  for (size_t candidate = std::min(value, limit); candidate > 1; --candidate)
    if (value % candidate == 0) return candidate;
  return 1;
}

constexpr uint64_t ProgramBinaryMagic = uint64_t{0x47454d4f434c4231};
constexpr size_t ProgramBinaryHeaderSize = 32;

std::vector<uint8_t> programBinary(const _cl_program *program) {
  const auto &payload = program->binaryType == CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT ? program->bitcode : program->il;
  if (payload.empty()) return {};
  if (program->binaryType == CL_PROGRAM_BINARY_TYPE_NONE) return payload;

  const std::string &source = program->buildSource.empty() ? program->source : program->buildSource;
  if (source.size() > std::numeric_limits<uint64_t>::max() - ProgramBinaryHeaderSize
      || payload.size() > std::numeric_limits<uint64_t>::max() - ProgramBinaryHeaderSize - source.size())
    return {};
  const size_t size = ProgramBinaryHeaderSize + source.size() + payload.size();
  std::vector<uint8_t> result(size);
  uint32_t type = static_cast<uint32_t>(program->binaryType);
  uint64_t sourceSize = source.size();
  uint64_t ilSize = payload.size();
  std::memcpy(result.data(), &ProgramBinaryMagic, sizeof(ProgramBinaryMagic));
  std::memcpy(result.data() + 8, &type, sizeof(type));
  std::memcpy(result.data() + 16, &sourceSize, sizeof(sourceSize));
  std::memcpy(result.data() + 24, &ilSize, sizeof(ilSize));
  if (!source.empty()) std::memcpy(result.data() + ProgramBinaryHeaderSize, source.data(), source.size());
  std::memcpy(result.data() + ProgramBinaryHeaderSize + source.size(), payload.data(), payload.size());
  return result;
}

bool decodeProgramBinary(const unsigned char *bytes, size_t size, cl_program_binary_type *type, std::string *source,
                         std::vector<uint8_t> *il) {
  if (!bytes || size < ProgramBinaryHeaderSize) return false;
  const auto magic = gemstone::readNative<uint64_t>(bytes);
  if (magic != ProgramBinaryMagic) return false;
  const auto encodedType = gemstone::readNative<uint32_t>(bytes, 8);
  const auto sourceSize = gemstone::readNative<uint64_t>(bytes, 16);
  const auto ilSize = gemstone::readNative<uint64_t>(bytes, 24);
  if (encodedType != CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT && encodedType != CL_PROGRAM_BINARY_TYPE_LIBRARY
      && encodedType != CL_PROGRAM_BINARY_TYPE_EXECUTABLE)
    return false;
  if (sourceSize > size - ProgramBinaryHeaderSize || ilSize > size - ProgramBinaryHeaderSize - sourceSize
      || ProgramBinaryHeaderSize + sourceSize + ilSize != size)
    return false;
  *type = static_cast<cl_program_binary_type>(encodedType);
  source->assign(reinterpret_cast<const char *>(bytes + ProgramBinaryHeaderSize), static_cast<size_t>(sourceSize));
  il->assign(bytes + ProgramBinaryHeaderSize + sourceSize, bytes + ProgramBinaryHeaderSize + sourceSize + ilSize);
  return !il->empty();
}

bool expandHeaders(const std::string &source, cl_uint count, const cl_program *headers, const char **names, std::string *expanded,
                   std::string *error) {
  *expanded = source;
  for (cl_uint index = 0; index < count; ++index) {
    auto *header = checked(headers[index]);
    if (!header || names[index] == nullptr) {
      if (error) *error = "invalid OpenCL embedded header";
      return false;
    }
    const std::string quoted = std::string("#include \"") + names[index] + "\"";
    const std::string angled = std::string("#include <") + names[index] + ">";
    for (const auto *include : {&quoted, &angled}) {
      size_t position = 0;
      while ((position = expanded->find(*include, position)) != std::string::npos) {
        expanded->replace(position, include->size(), header->source);
        position += header->source.size();
      }
    }
  }
  return true;
}

cl_int validateWaitList(_cl_command_queue *queue, cl_uint count, const cl_event *events) {
  if (count != 0 && !events) return CL_INVALID_EVENT_WAIT_LIST;
  if (count == 0 && events) return !queue ? CL_INVALID_VALUE : CL_INVALID_EVENT_WAIT_LIST;
  std::scoped_lock const lock(eventsMutex);
  for (cl_uint index = 0; index < count; ++index) {
    auto *event = checked(events[index]);
    if (!event) return !queue ? CL_INVALID_EVENT : CL_INVALID_EVENT_WAIT_LIST;
    if (queue && event->context != queue->context) return CL_INVALID_CONTEXT;
    if (event->status < 0) return CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
  }
  return CL_SUCCESS;
}

cl_int waitForEvents(cl_uint count, const cl_event *events) {
  if (count == 0 || !events) return CL_INVALID_VALUE;
  std::vector<_cl_event *> checkedEvents;
  checkedEvents.reserve(count);
  for (cl_uint index = 0; index < count; ++index) {
    auto *event = checked(events[index]);
    if (!event) return CL_INVALID_EVENT;
    checkedEvents.push_back(event);
  }
  bool failed = false;
  std::unique_lock lock(eventsMutex);
  for (auto *event : checkedEvents) {
    if (event->status > CL_COMPLETE)
      scheduler().blocking([&] { eventsCondition.wait(lock, [event] { return event->status <= CL_COMPLETE || schedulerStopping; }); });
    if (event->status > CL_COMPLETE) {
      failed = true;
    } else if (event->status < 0) {
      failed = true;
      if (event->queue && !event->failureObserved) {
        event->failureObserved = true;
        if (event->queue->unobservedFailures != 0) --event->queue->unobservedFailures;
      }
    }
  }
  return failed ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS;
}

_cl_event *makeEvent(_cl_command_queue *queue, cl_command_type command, cl_event *output) {
  if (!output) return nullptr;
  auto *event = newObject<_cl_event>();
  if (!event) return nullptr;
  event->queue = queue;
  event->context = queue->context;
  event->command = command;
  event->queued = nowNanos();
  event->submitted = event->queued + 1;
  event->started = event->submitted + 1;
  event->ended = event->started + 1;
  retain(queue);
  retain(queue->context);
  *output = reinterpret_cast<cl_event>(event);
  return event;
}

cl_int runGuarded(const std::function<cl_int()> &command) {
  try {
    return command();
  } catch (const std::bad_alloc &) {
    return CL_OUT_OF_HOST_MEMORY;
  } catch (...) {
    return CL_OUT_OF_RESOURCES;
  }
}

// A marker waits for all prior commands; a barrier also holds back later ones.
enum class Sync { None, Marker, Barrier };

struct PendingCommand {
  std::vector<_cl_event *> dependencies;
  _cl_event *completion = nullptr;
  std::function<cl_int()> execute;
  std::function<void(cl_int)> finish;
  std::shared_ptr<AllocationUses> uses;
};

void runPending(_cl_command_queue *queue, PendingCommand &command) {
  cl_int result = CL_SUCCESS;
  {
    std::scoped_lock const lock(eventsMutex);
    if (command.dependencies ^ exists([](const _cl_event *event) { return event->status < 0; }))
      result = CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
  }
  if (result == CL_SUCCESS) result = runGuarded(command.execute);
  if (command.finish) command.finish(result);
  command.finish = {};
  // Release allocation leases before the retained memory objects: their final
  // release decides whether the registry's backing allocation can be reclaimed.
  command.uses.reset();
  command.execute = {};
  for (auto *dependency : command.dependencies)
    clReleaseEvent(reinterpret_cast<cl_event>(dependency));
  {
    std::scoped_lock const lock(eventsMutex);
    if (result != CL_SUCCESS) ++queue->unobservedFailures;
    if (command.completion) {
      command.completion->status = result == CL_SUCCESS ? CL_COMPLETE : result;
      command.completion->ended = nowNanos();
    }
  }
  eventsCondition.notify_all();
  if (command.completion) {
    notifyEventCallbacks(command.completion);
    clReleaseEvent(reinterpret_cast<cl_event>(command.completion));
  }
  clReleaseCommandQueue(reinterpret_cast<cl_command_queue>(queue));
}

cl_int enqueuePending(_cl_command_queue *queue, cl_command_type type, std::vector<_cl_event *> dependencies,
                      std::function<cl_int()> execute, cl_event *output, Sync sync = Sync::None,
                      const std::function<void(cl_int)> &finish = {}, std::shared_ptr<AllocationUses> uses = {}) {
  cl_event completion = nullptr;
  auto *created = output ? makeEvent(queue, type, &completion) : nullptr;
  if (output && !created) {
    if (finish) finish(CL_OUT_OF_HOST_MEMORY);
    return CL_OUT_OF_HOST_MEMORY;
  }
  try {
    auto command = std::make_shared<PendingCommand>(std::move(dependencies), created, std::move(execute), finish, std::move(uses));
    const bool outOfOrder = (queue->properties & CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE) != 0;
    const auto order =
        outOfOrder && sync != Sync::Barrier ? gemstone::CommandQueue::Order::Unordered : gemstone::CommandQueue::Order::Ordered;
    // The job starts by taking eventsMutex, so it cannot run before its references and ticket are published.
    std::scoped_lock const lock(eventsMutex);
    auto tickets = command->dependencies ^ map([](const _cl_event *dependency) { return dependency->ticket; });
    if (outOfOrder && sync == Sync::Marker) tickets.push_back(scheduler().snapshot(queue->stream));
    auto ticket = scheduler().submit(
        queue->stream,
        [queue, command] {
          runPending(queue, *command);
          return 0;
        },
        std::move(tickets), order);
    for (auto *dependency : command->dependencies)
      retain(dependency);
    retain(queue);
    if (created) {
      created->status = CL_QUEUED;
      created->ticket = std::move(ticket);
      retain(created);
      *output = completion;
    }
  } catch (...) {
    if (finish) finish(CL_OUT_OF_HOST_MEMORY);
    if (completion) clReleaseEvent(completion);
    return CL_OUT_OF_HOST_MEMORY;
  }
  eventsCondition.notify_all();
  return CL_SUCCESS;
}

std::shared_ptr<AllocationUses> allocationUses(std::vector<std::shared_ptr<Allocation>> values) {
  return std::make_shared<AllocationUses>(std::move(values));
}

std::shared_ptr<_cl_mem> retainMemory(_cl_mem *memory) {
  auto uses = allocationUses({memory->allocation});
  retain(memory);
  return {memory, [uses = std::move(uses)](_cl_mem *value) mutable {
            uses.reset();
            clReleaseMemObject(reinterpret_cast<cl_mem>(value));
          }};
}

cl_int submitCommand(_cl_command_queue *queue, cl_command_type type, bool blocking, cl_uint count, const cl_event *waits,
                     std::function<cl_int()> execute, cl_event *output, const std::function<void(cl_int)> &finish = {},
                     std::shared_ptr<AllocationUses> uses = {}, Sync sync = Sync::None) {
  if (!queue) {
    if (finish) finish(CL_INVALID_COMMAND_QUEUE);
    return CL_INVALID_COMMAND_QUEUE;
  }
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) {
    if (finish) finish(result);
    return result;
  }
  std::vector<_cl_event *> dependencies(count);
  for (cl_uint index = 0; index < count; ++index)
    dependencies[index] = reinterpret_cast<_cl_event *>(waits[index]);
  bool pending = false;
  {
    std::scoped_lock const lock(eventsMutex);
    pending = dependencies ^ exists([](const _cl_event *event) { return event->status > CL_COMPLETE; });
  }
  if (!pending && scheduler().ready(scheduler().snapshot(queue->stream))) {
    result = runGuarded(execute);
    if (finish) finish(result);
    if (result == CL_SUCCESS && output && makeEvent(queue, type, output) == nullptr) return CL_OUT_OF_HOST_MEMORY;
    return result;
  }
  cl_event completion = nullptr;
  result = enqueuePending(queue, type, std::move(dependencies), std::move(execute), blocking ? &completion : output, sync, finish,
                          std::move(uses));
  if (!blocking || result != CL_SUCCESS) return result;
  result = waitForEvents(1, &completion);
  if (output) *output = completion;
  else clReleaseEvent(completion);
  return result;
}

cl_int completeEvent(_cl_command_queue *queue, cl_command_type command, cl_uint count, const cl_event *waits, cl_event *output) {
  auto sync = Sync::None;
  if (command == CL_COMMAND_BARRIER) sync = Sync::Barrier;
  else if (command == CL_COMMAND_MARKER && count == 0) sync = Sync::Marker;
  return submitCommand(queue, command, false, count, waits, [] { return CL_SUCCESS; }, output, {}, {}, sync);
}

std::vector<PointerRelocation> collectRelocations(uint64_t &scannedBytes) {
  std::scoped_lock const relocationsLock(pointerRelocationsMutex);
  std::vector<PointerRelocation> result;
  std::vector<std::shared_ptr<Allocation>> snapshot;
  {
    std::scoped_lock const lock(allocationsMutex);
    for (const auto &[_, allocation] : allocations)
      if (!allocation->hostBacked && scanBufferPointerRelocations()) snapshot.push_back(allocation);
  }
  for (const auto &source : snapshot) {
    scannedBytes += source->size;
    const auto *bytes = static_cast<const uint8_t *>(source->data());
    for (auto it = source->pointerRelocations.begin(); it != source->pointerRelocations.end();) {
      const auto &target = it->second;
      // Typed relocation metadata must not keep a freed target registered on
      // the next launch. Leave its raw dangling pointer bytes untouched.
      if (!target.first || target.first->released.load(std::memory_order_acquire)) {
        it = source->pointerRelocations.erase(it);
        continue;
      }
      const auto targetAddress = reinterpret_cast<uintptr_t>(target.first->data()) + target.second;
      uint64_t value = 0;
      const bool inRange = it->first <= source->size && sizeof(value) <= source->size - it->first;
      if (!inRange) {
        it = source->pointerRelocations.erase(it);
        continue;
      }
      std::memcpy(&value, bytes + it->first, sizeof(value));
      if (value != static_cast<uint64_t>(targetAddress)) it = source->pointerRelocations.erase(it);
      else ++it;
    }
    scanPointers(bytes, source->size, [&](size_t offset, uint64_t value) {
      size_t targetOffset = 0;
      const auto target = findPointer(reinterpret_cast<const void *>(static_cast<uintptr_t>(value)), &targetOffset);
      if (!target || targetOffset >= target->size) return;
      source->pointerRelocations[offset] = {target, targetOffset};
      result.push_back({.source = RelocationSourceMemory{AllocationId{static_cast<int64_t>(source->id)}},
                        .sourceOffset = LongPositive0{static_cast<int64_t>(offset)},
                        .target = AllocationId{static_cast<int64_t>(target->id)},
                        .targetOffset = LongPositive0{static_cast<int64_t>(targetOffset)}});
    });
  }
  std::set<std::pair<uint64_t, uint64_t>> emitted;
  for (const auto &relocation : result) {
    const auto *sourceId = std::get_if<RelocationSourceMemory>(&relocation.source);
    if (sourceId) emitted.emplace(static_cast<uint64_t>(sourceId->region.value), static_cast<uint64_t>(relocation.sourceOffset.value));
  }
  for (const auto &source : snapshot) {
    for (const auto &[offset, target] : source->pointerRelocations) {
      if (emitted.contains({source->id, offset}) || !target.first || target.second >= target.first->size) continue;
      result.push_back({.source = RelocationSourceMemory{AllocationId{static_cast<int64_t>(source->id)}},
                        .sourceOffset = LongPositive0{static_cast<int64_t>(offset)},
                        .target = AllocationId{static_cast<int64_t>(target.first->id)},
                        .targetOffset = LongPositive0{static_cast<int64_t>(target.second)}});
      emitted.emplace(source->id, offset);
    }
  }
  return result;
}

// The work-group size a launch without an explicit local size uses in one dimension.
size_t implicitGroup(const _cl_kernel &kernel, size_t index, size_t global) {
  if (kernel.requiredWorkGroupSize != std::array<size_t, 3>{0, 0, 0}) return kernel.requiredWorkGroupSize[index];
  if (index != 0) return 1;
  return uniformWorkGroups(*kernel.program) ? largestDivisor(global, 32) : 32;
}

// Device faults surface as CL_OUT_OF_RESOURCES, as the vendor runtimes report them.
cl_int launchStatus(gemstone::execution::FailureKind kind) {
  using gemstone::execution::FailureKind;
  switch (kind) {
    case FailureKind::InvalidProgram: return CL_INVALID_PROGRAM_EXECUTABLE;
    case FailureKind::InvalidConfiguration: return CL_INVALID_WORK_GROUP_SIZE;
    case FailureKind::InvalidRequest: return CL_INVALID_KERNEL_ARGS;
    case FailureKind::NotFound: return CL_INVALID_KERNEL;
    case FailureKind::Unsupported: return CL_INVALID_OPERATION;
    default: return CL_OUT_OF_RESOURCES;
  }
}

cl_int finishRemote(_cl_kernel *kernel, size_t workDimension, const size_t *offset, const size_t *global, const size_t *local) {
  std::unique_lock submissionLock(kernelSubmissionMutex);
  const uint64_t started = nowNanos();
  uint64_t scannedBytes = 0;
  uint64_t downloadedBytes = 0;
  auto &state = remote;
  if (!state.ready() || kernel->program->remoteId == 0) return CL_OUT_OF_RESOURCES;
  if (workDimension == 0 || workDimension > 3 || !global) return CL_INVALID_WORK_DIMENSION;
  size_t group[3] = {1, 1, 1};
  size_t count[3] = {1, 1, 1};
  size_t exact[3] = {1, 1, 1};
  size_t origin[3] = {0, 0, 0};
  for (size_t index = 0; index < workDimension; ++index) {
    if (global[index] == 0 || global[index] > static_cast<uint64_t>(std::numeric_limits<int64_t>::max()))
      return CL_INVALID_GLOBAL_WORK_SIZE;
    if (offset && offset[index] > static_cast<size_t>(std::numeric_limits<int32_t>::max())) return CL_INVALID_GLOBAL_OFFSET;
    exact[index] = global[index];
    if (offset) origin[index] = offset[index];
    group[index] = local ? local[index] : implicitGroup(*kernel, index, global[index]);
    if (group[index] == 0 || group[index] > static_cast<size_t>(std::numeric_limits<int32_t>::max())) return CL_INVALID_WORK_GROUP_SIZE;
    count[index] = (global[index] + group[index] - 1) / group[index];
    if (count[index] > static_cast<size_t>(std::numeric_limits<int32_t>::max())) return CL_INVALID_GLOBAL_WORK_SIZE;
  }
  if (!local && kernel->requiredWorkGroupSize != std::array<size_t, 3>{0, 0, 0}) {
    for (size_t index = workDimension; index < 3; ++index) {
      group[index] = kernel->requiredWorkGroupSize[index];
      if (group[index] == 0 || group[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_WORK_GROUP_SIZE;
    }
  }
  std::vector<gemstone::execution::Argument> arguments;
  arguments.reserve(kernel->arguments.size());
  std::vector<std::vector<uint8_t>> scalarStorage(kernel->arguments.size());
  std::vector<PointerRelocation> relocations;
  for (size_t index = 0; index < kernel->arguments.size(); ++index) {
    const auto &argument = kernel->arguments[index];
    if (argument.kind == ::KernelArgument::Kind::Local) {
      arguments.emplace_back(ArgumentLocal{static_cast<int64_t>(argument.offset)});
    } else if (argument.kind == ::KernelArgument::Kind::Allocation) {
      arguments.emplace_back(
          ArgumentMemory{.region = AllocationId{static_cast<int64_t>(argument.allocation->id)},
                         .offset = {static_cast<int64_t>(argument.offset)},
                         .range = argument.range ? std::optional<LongPositive>{static_cast<int64_t>(*argument.range)} : std::nullopt});
      if (argument.texels)
        relocations.push_back({.source = RelocationSourceMemory{AllocationId{static_cast<int64_t>(argument.allocation->id)}},
                               .sourceOffset = LongPositive0{static_cast<int64_t>(descriptor::Layout::Texels)},
                               .target = AllocationId{static_cast<int64_t>(argument.texels->id)},
                               .targetOffset = LongPositive0{static_cast<int64_t>(argument.texelOffset)}});
    } else if (argument.kind == ::KernelArgument::Kind::SVM) {
      arguments.emplace_back(ArgumentMemory{.region = AllocationId{static_cast<int64_t>(argument.allocation->id)},
                                            .offset = {static_cast<int64_t>(argument.offset)}});
    } else {
      scalarStorage[index] = argument.bytes;
      arguments.emplace_back(ArgumentScalar{scalarStorage[index]});
    }
  }
  auto memoryRelocations = collectRelocations(scannedBytes);
  relocations.insert(relocations.end(), memoryRelocations.begin(), memoryRelocations.end());
  const Dimensions groupSize{
      .x = {static_cast<int32_t>(group[0])}, .y = {static_cast<int32_t>(group[1])}, .z = {static_cast<int32_t>(group[2])}};
  const Dimensions groupCount{
      .x = {static_cast<int32_t>(count[0])}, .y = {static_cast<int32_t>(count[1])}, .z = {static_cast<int32_t>(count[2])}};
  const GlobalDimensions globalSize{
      .x = {static_cast<int64_t>(exact[0])}, .y = {static_cast<int64_t>(exact[1])}, .z = {static_cast<int64_t>(exact[2])}};
  const Coordinates globalOffset{
      .x = {static_cast<int32_t>(origin[0])}, .y = {static_cast<int32_t>(origin[1])}, .z = {static_cast<int32_t>(origin[2])}};
  std::string failureMessage;
  const auto relocationCount = relocations.size();
  std::unique_lock lock(remoteMutex);
  const bool system = kernel->indirectSystem || kernel->indirectTypes.contains(CL_KERNEL_EXEC_INFO_SVM_FINE_GRAIN_SYSTEM)
                      || (kernel->arguments ^ exists([](const auto &argument) { return argument.systemPointer; }));
  // The server keeps host memory enabled for the rest of the session once a launch needs it.
  if (system && !hostMemoryEnabled) {
    if (!state.client()->call(EnableHostMemory{})) return CL_OUT_OF_RESOURCES;
    hostMemoryEnabled = true;
  }
  gemstone::execution::CompletionResult result;
  bool launched = gemstone::execution::launchAndCollect(Launch{.program = ProgramId{static_cast<int64_t>(kernel->program->remoteId)},
                                                               .kernelName = {kernel->name},
                                                               .groupSize = groupSize,
                                                               .groupCount = groupCount,
                                                               .globalSize = globalSize,
                                                               .workDim = {static_cast<int32_t>(workDimension)},
                                                               .globalOffset = globalOffset,
                                                               .privateSegmentBytes = {0},
                                                               .groupSegmentBytes = {0},
                                                               .arguments = std::move(arguments),
                                                               .pointerRelocations = std::move(relocations),
                                                               .discoverArgumentRelocations = true},
                                                        result, failureMessage, [&] {
                                                          lock.unlock();
                                                          submissionLock.unlock();
                                                        });
  if (!submissionLock.owns_lock()) submissionLock.lock();
  if (!lock.owns_lock()) lock.lock();
  if (launched && !readDirtyRanges(*state.client(), result.dirtyRanges, [&](const DirtyRange &range, const uint8_t *bytes) {
        const auto allocation = findAllocation(static_cast<uint64_t>(range.allocation.value));
        const auto rangeOffset = static_cast<size_t>(range.offset.value);
        const auto rangeSize = static_cast<size_t>(range.size.value);
        if (!validRange(allocation, rangeOffset, rangeSize)) return false;
        invalidatePointerRelocations(allocation, rangeOffset, rangeSize);
        std::memcpy(static_cast<uint8_t *>(allocation->data()) + rangeOffset, bytes, rangeSize);
        downloadedBytes += rangeSize;
        return true;
      }))
    launched = false;
  if (launched)
    launched = applyPointerRelocations(
        result.pointerRelocations,
        [](uint64_t id) -> RelocationTarget {
          const auto allocation = findAllocation(id);
          return allocation ? RelocationTarget{.base = allocation->data(), .size = allocation->size} : RelocationTarget{};
        },
        [](uint64_t sourceId, uint64_t targetId, uint64_t sourceOffset, uint64_t targetOffset) {
          const auto source = findAllocation(sourceId);
          const auto target = findAllocation(targetId);
          std::scoped_lock const relocationsLock(pointerRelocationsMutex);
          source->pointerRelocations[sourceOffset] = {target, targetOffset};
        },
        failureMessage);
  lock.unlock();
  if (!launched) {
    std::fprintf(stderr, "Gemstone OpenCL launch failed: %s\n", (failureMessage.empty() ? remote.error() : failureMessage).c_str());
    return launchStatus(result.failure);
  }
  if (gemstone::traceEnabled("perf"))
    std::fprintf(stderr, "Gemstone OpenCL perf: kernel=%s download=%llu scan=%llu relocations=%llu elapsed_us=%llu\n", kernel->name.c_str(),
                 static_cast<unsigned long long>(downloadedBytes), static_cast<unsigned long long>(scannedBytes),
                 static_cast<unsigned long long>(relocationCount), static_cast<unsigned long long>((nowNanos() - started) / 1000));
  return CL_SUCCESS;
}

} // namespace

extern "C" {

CL_API_ENTRY cl_int CL_API_CALL clGetPlatformIDs(cl_uint entries, cl_platform_id *platforms, cl_uint *count) {
  initialiseDispatch();
  if (!gemstone::environment::accepted(gemstone::environment::invalid())) return CL_PLATFORM_NOT_FOUND_KHR;
  (void)remote.mode();
  if (!platforms && !count) return CL_INVALID_VALUE;
  if (entries == 0 && platforms) return CL_INVALID_VALUE;
  if (platforms) platforms[0] = reinterpret_cast<cl_platform_id>(&platform);
  if (count) *count = 1;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clIcdGetPlatformIDsKHR(cl_uint entries, cl_platform_id *platforms, cl_uint *count) {
  initialiseDispatch();
  return clGetPlatformIDs(entries, platforms, count);
}

CL_API_ENTRY void *CL_API_CALL clIcdGetFunctionAddressForPlatformKHR(cl_platform_id value, const char *name) {
  return clGetExtensionFunctionAddressForPlatform(value, name);
}

CL_API_ENTRY cl_int CL_API_CALL clIcdSetPlatformDispatchDataKHR(cl_platform_id value, void *) {
  return validPlatform(value) ? CL_SUCCESS : CL_INVALID_PLATFORM;
}

CL_API_ENTRY cl_int CL_API_CALL clGetPlatformInfo(cl_platform_id value, cl_platform_info name, size_t size, void *output, size_t *written) {
  if (!validPlatform(value)) return CL_INVALID_PLATFORM;
  switch (name) {
    case CL_PLATFORM_PROFILE: return outputString(size, output, written, "FULL_PROFILE");
    case CL_PLATFORM_VERSION: return outputString(size, output, written, "OpenCL 3.0 Gemstone");
    case CL_PLATFORM_NAME: return outputString(size, output, written, openclIdentity().platformName);
    case CL_PLATFORM_VENDOR: return outputString(size, output, written, openclIdentity().vendor);
    case CL_PLATFORM_ICD_SUFFIX_KHR: return outputString(size, output, written, "GEMSTONE");
    case CL_PLATFORM_EXTENSIONS: return outputString(size, output, written, DeviceExtensionString);
    case CL_PLATFORM_EXTENSIONS_WITH_VERSION:
      return outputBytes(size, output, written, DeviceExtensionVersions, sizeof(DeviceExtensionVersions));
    case CL_PLATFORM_HOST_TIMER_RESOLUTION: return outputScalar(size, output, written, cl_ulong{1});
    case CL_PLATFORM_NUMERIC_VERSION: return outputScalar(size, output, written, cl_version{CL_MAKE_VERSION(3, 0, 0)});
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clGetDeviceIDs(cl_platform_id value, cl_device_type type, cl_uint entries, cl_device_id *devices,
                                               cl_uint *count) {
  if (!validPlatform(value)) return CL_INVALID_PLATFORM;
  constexpr cl_device_type validTypes =
      CL_DEVICE_TYPE_DEFAULT | CL_DEVICE_TYPE_CPU | CL_DEVICE_TYPE_GPU | CL_DEVICE_TYPE_ACCELERATOR | CL_DEVICE_TYPE_CUSTOM;
  if (type == 0 || (type != CL_DEVICE_TYPE_ALL && (type & ~validTypes) != 0)) return CL_INVALID_DEVICE_TYPE;
  if (type != CL_DEVICE_TYPE_GPU && type != CL_DEVICE_TYPE_DEFAULT && type != CL_DEVICE_TYPE_ALL && (type & CL_DEVICE_TYPE_GPU) == 0)
    return CL_DEVICE_NOT_FOUND;
  if (entries == 0 && devices) return CL_INVALID_VALUE;
  if (devices) devices[0] = reinterpret_cast<cl_device_id>(&device);
  if (!devices && !count) return CL_INVALID_VALUE;
  if (count) *count = 1;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clRetainDevice(cl_device_id value) {
  // Gemstone exposes one root device. It is owned by the ICD for the
  // lifetime of the process, so retaining it is a successful no-op rather
  // than a reference-count transition that could invalidate the static
  // device object.
  return validDevice(value) ? CL_SUCCESS : CL_INVALID_DEVICE;
}

CL_API_ENTRY cl_int CL_API_CALL clReleaseDevice(cl_device_id value) {
  // The root device remains available until the ICD is unloaded.
  return validDevice(value) ? CL_SUCCESS : CL_INVALID_DEVICE;
}

CL_API_ENTRY cl_int CL_API_CALL clGetDeviceInfo(cl_device_id value, cl_device_info name, size_t size, void *output, size_t *written) {
  if (!validDevice(value)) return CL_INVALID_DEVICE;
  static const auto workSizes = maxWorkItemSizes();
  // Out-of-order execution permits reordering; it does not require concurrent execution.
  static const cl_command_queue_properties queueProperties = CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_PROFILING_ENABLE;
  static const cl_device_svm_capabilities svm =
      CL_DEVICE_SVM_COARSE_GRAIN_BUFFER | CL_DEVICE_SVM_FINE_GRAIN_BUFFER | CL_DEVICE_SVM_FINE_GRAIN_SYSTEM | CL_DEVICE_SVM_ATOMICS;
  static const cl_device_fp_config fp = CL_FP_INF_NAN | CL_FP_ROUND_TO_NEAREST | CL_FP_DENORM;
  static const cl_device_fp_config doubleFp =
      CL_FP_FMA | CL_FP_ROUND_TO_NEAREST | CL_FP_ROUND_TO_ZERO | CL_FP_ROUND_TO_INF | CL_FP_INF_NAN | CL_FP_DENORM;
  switch (name) {
    case CL_DEVICE_TYPE: return outputScalar(size, output, written, cl_device_type{CL_DEVICE_TYPE_GPU});
    case CL_DEVICE_VENDOR_ID: return outputScalar(size, output, written, openclIdentity().vendorId);
    // Keep the IP version consistent with the Arc device ID advertised below.
    case CL_DEVICE_IP_VERSION_INTEL: return outputScalar(size, output, written, cl_uint{0x030dc008});
    case CL_DEVICE_ID_INTEL: return outputScalar(size, output, written, cl_uint{22177});
    case CL_DEVICE_NUM_SLICES_INTEL: return outputScalar(size, output, written, cl_uint{1});
    case CL_DEVICE_NUM_SUB_SLICES_PER_SLICE_INTEL: return outputScalar(size, output, written, cl_uint{2});
    case CL_DEVICE_NUM_EUS_PER_SUB_SLICE_INTEL: return outputScalar(size, output, written, cl_uint{8});
    case CL_DEVICE_NUM_THREADS_PER_EU_INTEL: return outputScalar(size, output, written, cl_uint{7});
    case CL_DEVICE_FEATURE_CAPABILITIES_INTEL: return outputScalar(size, output, written, cl_device_feature_capabilities_intel{0});
    case CL_DEVICE_MAX_COMPUTE_UNITS: return outputScalar(size, output, written, cl_uint{16});
    case CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS: return outputScalar(size, output, written, cl_uint{3});
    case CL_DEVICE_MAX_WORK_GROUP_SIZE: return outputScalar(size, output, written, MaxWorkGroupSize);
    case CL_DEVICE_MAX_WORK_ITEM_SIZES: return outputBytes(size, output, written, workSizes.data(), sizeof(workSizes));
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF: return outputScalar(size, output, written, cl_uint{1});
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_INT:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF: return outputScalar(size, output, written, cl_uint{1});
    case CL_DEVICE_MAX_CLOCK_FREQUENCY: return outputScalar(size, output, written, cl_uint{1000});
    case CL_DEVICE_ADDRESS_BITS: return outputScalar(size, output, written, cl_uint{64});
    case CL_DEVICE_MAX_MEM_ALLOC_SIZE: return outputScalar(size, output, written, MaxMemAllocSize);
    case CL_DEVICE_MAX_READ_IMAGE_ARGS: return outputScalar(size, output, written, cl_uint{128});
    case CL_DEVICE_MAX_WRITE_IMAGE_ARGS:
    case CL_DEVICE_MAX_READ_WRITE_IMAGE_ARGS: return outputScalar(size, output, written, cl_uint{64});
    case CL_DEVICE_IMAGE2D_MAX_WIDTH:
    case CL_DEVICE_IMAGE2D_MAX_HEIGHT: return outputScalar(size, output, written, Image2DMaxSize);
    case CL_DEVICE_IMAGE3D_MAX_WIDTH:
    case CL_DEVICE_IMAGE3D_MAX_HEIGHT:
    case CL_DEVICE_IMAGE3D_MAX_DEPTH: return outputScalar(size, output, written, Image3DMaxSize);
    case CL_DEVICE_IMAGE_MAX_BUFFER_SIZE: return outputScalar(size, output, written, ImageMaxBufferSize);
    case CL_DEVICE_IMAGE_MAX_ARRAY_SIZE: return outputScalar(size, output, written, ImageMaxArraySize);
    case CL_DEVICE_IMAGE_PITCH_ALIGNMENT: return outputScalar(size, output, written, ImagePitchAlignment);
    case CL_DEVICE_IMAGE_BASE_ADDRESS_ALIGNMENT: return outputScalar(size, output, written, ImageBaseAddressAlignment);
    case CL_DEVICE_PARTITION_MAX_SUB_DEVICES: return outputScalar(size, output, written, cl_uint{0});
    case CL_DEVICE_PARTITION_AFFINITY_DOMAIN: return outputScalar(size, output, written, cl_device_affinity_domain{0});
    case CL_DEVICE_PARTITION_PROPERTIES:
    case CL_DEVICE_PARTITION_TYPE: {
      static const cl_device_partition_property none[] = {0};
      return outputBytes(size, output, written, none, sizeof(none));
    }
    case CL_DEVICE_PARENT_DEVICE: return outputScalar(size, output, written, cl_device_id{});
    case CL_DEVICE_REFERENCE_COUNT: return outputScalar(size, output, written, cl_uint{1});
    case CL_DEVICE_IMAGE_SUPPORT: return outputScalar(size, output, written, cl_bool{CL_TRUE});
    case CL_DEVICE_MAX_PARAMETER_SIZE: return outputScalar(size, output, written, size_t{4096});
    case CL_DEVICE_MAX_SAMPLERS: return outputScalar(size, output, written, cl_uint{16});
    case CL_DEVICE_SINGLE_FP_CONFIG: return outputScalar(size, output, written, fp);
    case CL_DEVICE_HALF_FP_CONFIG: return outputScalar(size, output, written, fp);
    case CL_DEVICE_GLOBAL_MEM_CACHE_TYPE: return outputScalar(size, output, written, cl_uint{CL_NONE});
    case CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE: return outputScalar(size, output, written, cl_uint{64});
    case CL_DEVICE_GLOBAL_MEM_CACHE_SIZE: return outputScalar(size, output, written, cl_ulong{0});
    case CL_DEVICE_GLOBAL_MEM_SIZE: return outputScalar(size, output, written, cl_ulong{0x80000000});
    case CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE: return outputScalar(size, output, written, cl_ulong{0x10000});
    case CL_DEVICE_MAX_CONSTANT_ARGS: return outputScalar(size, output, written, cl_uint{8});
    case CL_DEVICE_LOCAL_MEM_TYPE: return outputScalar(size, output, written, cl_uint{CL_LOCAL});
    case CL_DEVICE_LOCAL_MEM_SIZE: return outputScalar(size, output, written, LocalMemSize);
    // This provider is an emulator, so fp64 is a supported device feature.
    // DPC++/IGC lowers it through Intel's DP emulation path, enabled by the
    // explicit compiler configuration.
    case CL_DEVICE_DOUBLE_FP_CONFIG: return outputScalar(size, output, written, doubleFp);
    case CL_DEVICE_MEM_BASE_ADDR_ALIGN: return outputScalar(size, output, written, cl_uint{1024});
    case CL_DEVICE_MIN_DATA_TYPE_ALIGN_SIZE: return outputScalar(size, output, written, cl_uint{16});
    case CL_DEVICE_ERROR_CORRECTION_SUPPORT:
    case CL_DEVICE_PREFERRED_INTEROP_USER_SYNC: return outputScalar(size, output, written, cl_bool{CL_FALSE});
    case CL_DEVICE_PROFILING_TIMER_RESOLUTION: return outputScalar(size, output, written, size_t{1});
    case CL_DEVICE_ENDIAN_LITTLE:
    case CL_DEVICE_AVAILABLE:
    case CL_DEVICE_COMPILER_AVAILABLE:
    case CL_DEVICE_LINKER_AVAILABLE:
    case CL_DEVICE_HOST_UNIFIED_MEMORY: return outputScalar(size, output, written, cl_bool{CL_TRUE});
    case CL_DEVICE_EXECUTION_CAPABILITIES: return outputScalar(size, output, written, cl_device_exec_capabilities{CL_EXEC_KERNEL});
    case CL_DEVICE_QUEUE_PROPERTIES: return outputScalar(size, output, written, queueProperties);
    case CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE: return outputScalar(size, output, written, MaxGlobalVariableSize);
    case CL_DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE: return outputScalar(size, output, written, PreferredGlobalVariableTotalSize);
    case CL_DEVICE_MAX_PIPE_ARGS:
    case CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS: return outputScalar(size, output, written, cl_uint{16});
    case CL_DEVICE_PIPE_MAX_PACKET_SIZE: return outputScalar(size, output, written, PipeMaxPacketSize);
    case CL_DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT:
    case CL_DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT:
    case CL_DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT: return outputScalar(size, output, written, cl_uint{0});
    case CL_DEVICE_NAME: return outputString(size, output, written, openclIdentity().deviceName);
    case CL_DEVICE_VENDOR: return outputString(size, output, written, openclIdentity().vendor);
    case CL_DRIVER_VERSION: return outputString(size, output, written, "26.22.38646.6");
    case CL_DEVICE_PROFILE: return outputString(size, output, written, "FULL_PROFILE");
    case CL_DEVICE_VERSION: return outputString(size, output, written, "OpenCL 3.0 Gemstone");
    case CL_DEVICE_OPENCL_C_VERSION:
      return outputString(size, output, written, deviceEnqueueSupported() ? "OpenCL C 2.0 Gemstone" : "OpenCL C 1.2 Gemstone");
    case CL_DEVICE_EXTENSIONS: return outputString(size, output, written, DeviceExtensionString);
    case CL_DEVICE_NUMERIC_VERSION: return outputScalar(size, output, written, cl_version{CL_MAKE_VERSION(3, 0, 0)});
    case CL_DEVICE_EXTENSIONS_WITH_VERSION:
      return outputBytes(size, output, written, DeviceExtensionVersions, sizeof(DeviceExtensionVersions));
    case CL_DEVICE_PCI_BUS_INFO_KHR: {
      const cl_device_pci_bus_info_khr v{.pci_domain = 0, .pci_bus = 0, .pci_device = 1, .pci_function = 0};
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ILS_WITH_VERSION: return outputBytes(size, output, written, DeviceILVersions, sizeof(DeviceILVersions));
    case CL_DEVICE_BUILT_IN_KERNELS: return outputString(size, output, written, "");
    case CL_DEVICE_BUILT_IN_KERNELS_WITH_VERSION: {
      if (written) *written = 0;
      return !output || size == 0 ? CL_SUCCESS : CL_INVALID_VALUE;
    }
    case CL_DEVICE_OPENCL_C_ALL_VERSIONS: {
      static const cl_name_version versions[] = {{.version = CL_MAKE_VERSION(1, 0, 0), .name = "OpenCL C"},
                                                 {.version = CL_MAKE_VERSION(1, 1, 0), .name = "OpenCL C"},
                                                 {.version = CL_MAKE_VERSION(1, 2, 0), .name = "OpenCL C"},
                                                 {.version = CL_MAKE_VERSION(2, 0, 0), .name = "OpenCL C"},
                                                 {.version = CL_MAKE_VERSION(3, 0, 0), .name = "OpenCL C"}};
      std::vector<cl_name_version> supported;
      for (const auto &version : versions)
        if (deviceEnqueueSupported() || version.version != CL_MAKE_VERSION(2, 0, 0)) supported.push_back(version);
      return outputBytes(size, output, written, supported.data(), supported.size() * sizeof(cl_name_version));
    }
    case CL_DEVICE_OPENCL_C_FEATURES: {
      static const cl_name_version features[] = {
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_int64"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_fp64"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_subgroups"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_generic_address_space"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_work_group_collective_functions"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_program_scope_global_variables"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_images"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_read_write_images"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_3d_image_writes"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_pipes"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_device_enqueue"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_integer_dot_product_input_4x8bit"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_integer_dot_product_input_4x8bit_packed"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_atomic_order_acq_rel"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_atomic_order_seq_cst"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_atomic_scope_device"},
          {.version = CL_MAKE_VERSION(3, 0, 0), .name = "__opencl_c_atomic_scope_all_devices"},
          {.version = CL_MAKE_VERSION(1, 0, 0), .name = "__opencl_c_kernel_clock_scope_device"},
          {.version = CL_MAKE_VERSION(1, 0, 0), .name = "__opencl_c_kernel_clock_scope_work_group"},
          {.version = CL_MAKE_VERSION(1, 0, 0), .name = "__opencl_c_kernel_clock_scope_sub_group"}};
      std::vector<cl_name_version> supported;
      for (const auto &feature : features)
        if (deviceEnqueueSupported() || std::string_view(feature.name) != "__opencl_c_device_enqueue") supported.push_back(feature);
      return outputBytes(size, output, written, supported.data(), supported.size() * sizeof(cl_name_version));
    }
    case CL_DEVICE_KERNEL_CLOCK_CAPABILITIES_KHR: {
      cl_device_kernel_clock_capabilities_khr const v = CL_DEVICE_KERNEL_CLOCK_SCOPE_DEVICE_KHR
                                                        | CL_DEVICE_KERNEL_CLOCK_SCOPE_WORK_GROUP_KHR
                                                        | CL_DEVICE_KERNEL_CLOCK_SCOPE_SUB_GROUP_KHR;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ATOMIC_MEMORY_CAPABILITIES: {
      cl_device_atomic_capabilities const v = CL_DEVICE_ATOMIC_ORDER_RELAXED | CL_DEVICE_ATOMIC_ORDER_ACQ_REL
                                              | CL_DEVICE_ATOMIC_ORDER_SEQ_CST | CL_DEVICE_ATOMIC_SCOPE_WORK_GROUP
                                              | CL_DEVICE_ATOMIC_SCOPE_DEVICE | CL_DEVICE_ATOMIC_SCOPE_ALL_DEVICES;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ATOMIC_FENCE_CAPABILITIES: {
      cl_device_atomic_capabilities const v = CL_DEVICE_ATOMIC_ORDER_RELAXED | CL_DEVICE_ATOMIC_ORDER_ACQ_REL
                                              | CL_DEVICE_ATOMIC_ORDER_SEQ_CST | CL_DEVICE_ATOMIC_SCOPE_WORK_ITEM
                                              | CL_DEVICE_ATOMIC_SCOPE_WORK_GROUP | CL_DEVICE_ATOMIC_SCOPE_DEVICE
                                              | CL_DEVICE_ATOMIC_SCOPE_ALL_DEVICES;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_WORK_GROUP_COLLECTIVE_FUNCTIONS_SUPPORT:
    case CL_DEVICE_GENERIC_ADDRESS_SPACE_SUPPORT:
    case CL_DEVICE_NON_UNIFORM_WORK_GROUP_SUPPORT:
    case CL_DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS: return outputScalar(size, output, written, cl_bool{CL_TRUE});
    case CL_DEVICE_PRINTF_BUFFER_SIZE: return outputScalar(size, output, written, size_t{1024} * 1024);
    // The earliest well-formed version: no Khronos conformance submission has been made.
    case CL_DEVICE_LATEST_CONFORMANCE_VERSION_PASSED: return outputString(size, output, written, "v0000-01-01-00");
    case CL_DEVICE_PLATFORM: return outputScalar(size, output, written, reinterpret_cast<cl_platform_id>(&platform));
    case CL_DEVICE_SVM_CAPABILITIES: return outputScalar(size, output, written, svm);
    case CL_DEVICE_IL_VERSION: return outputString(size, output, written, DeviceILVersionString);
    case CL_DEVICE_MAX_NUM_SUB_GROUPS: return outputScalar(size, output, written, static_cast<cl_uint>(MaxWorkGroupSize / MaxSubgroupSize));
    case CL_DEVICE_SUB_GROUP_SIZES_INTEL: {
      static const size_t sizes[] = {8, 16, 32};
      return outputBytes(size, output, written, sizes, sizeof(sizes));
    }
    case CL_DEVICE_PREFERRED_WORK_GROUP_SIZE_MULTIPLE: return outputScalar(size, output, written, size_t{32});
    case CL_DEVICE_QUEUE_ON_DEVICE_PROPERTIES:
      return outputScalar(size, output, written, deviceEnqueueSupported() ? queueProperties : cl_command_queue_properties{0});
    case CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE:
      return outputScalar(size, output, written, deviceEnqueueSupported() ? DeviceQueuePreferredSize : cl_uint{0});
    case CL_DEVICE_QUEUE_ON_DEVICE_MAX_SIZE:
      return outputScalar(size, output, written, deviceEnqueueSupported() ? DeviceQueueMaxSize : cl_uint{0});
    case CL_DEVICE_MAX_ON_DEVICE_QUEUES:
      return outputScalar(size, output, written, deviceEnqueueSupported() ? MaxDeviceQueues : cl_uint{0});
    case CL_DEVICE_MAX_ON_DEVICE_EVENTS:
      return outputScalar(size, output, written, deviceEnqueueSupported() ? MaxDeviceEvents : cl_uint{0});
    case CL_DEVICE_DEVICE_ENQUEUE_CAPABILITIES:
      return outputScalar(size, output, written,
                          deviceEnqueueSupported()
                              ? cl_device_device_enqueue_capabilities{CL_DEVICE_QUEUE_SUPPORTED | CL_DEVICE_QUEUE_REPLACEABLE_DEFAULT}
                              : cl_device_device_enqueue_capabilities{0});
    case CL_DEVICE_INTEGER_DOT_PRODUCT_CAPABILITIES_KHR: {
      cl_device_integer_dot_product_capabilities_khr const v =
          CL_DEVICE_INTEGER_DOT_PRODUCT_INPUT_4x8BIT_PACKED_KHR | CL_DEVICE_INTEGER_DOT_PRODUCT_INPUT_4x8BIT_KHR;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_INTEGER_DOT_PRODUCT_ACCELERATION_PROPERTIES_8BIT_KHR:
    case CL_DEVICE_INTEGER_DOT_PRODUCT_ACCELERATION_PROPERTIES_4x8BIT_PACKED_KHR: {
      const cl_device_integer_dot_product_acceleration_properties_khr v{};
      return outputScalar(size, output, written, v);
    }
    // The Vulkan facade reports the same identity for this device.
    case CL_DEVICE_UUID_KHR:
    case CL_DRIVER_UUID_KHR: {
      std::array<cl_uchar, CL_UUID_SIZE_KHR> v{};
      for (size_t index = 0; index < v.size(); ++index)
        v[index] = static_cast<cl_uchar>((name == CL_DEVICE_UUID_KHR ? 0x47 : 0x67) + index);
      return outputBytes(size, output, written, v.data(), v.size());
    }
    case CL_DEVICE_LUID_VALID_KHR: return outputScalar(size, output, written, cl_bool{CL_FALSE});
    case CL_DEVICE_LUID_KHR: {
      const std::array<cl_uchar, CL_LUID_SIZE_KHR> v{};
      return outputBytes(size, output, written, v.data(), v.size());
    }
    case CL_DEVICE_NODE_MASK_KHR: return outputScalar(size, output, written, cl_uint{0});
    case CL_DEVICE_SPIRV_EXTENDED_INSTRUCTION_SETS_KHR:
      return outputBytes(size, output, written, SpirvExtendedInstructionSets, sizeof(SpirvExtendedInstructionSets));
    case CL_DEVICE_SPIRV_EXTENSIONS_KHR: return outputBytes(size, output, written, SpirvExtensions, sizeof(SpirvExtensions));
    case CL_DEVICE_SPIRV_CAPABILITIES_KHR: return outputBytes(size, output, written, SpirvCapabilities, sizeof(SpirvCapabilities));
    case CL_DEVICE_PIPE_SUPPORT: return outputScalar(size, output, written, cl_bool{CL_TRUE});
    case CL_DEVICE_SHARED_SYSTEM_MEM_CAPABILITIES_INTEL: {
      const cl_device_unified_shared_memory_capabilities_intel v =
          CL_UNIFIED_SHARED_MEMORY_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_ATOMIC_ACCESS_INTEL
          | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ATOMIC_ACCESS_INTEL;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_HOST_MEM_CAPABILITIES_INTEL:
    case CL_DEVICE_SINGLE_DEVICE_SHARED_MEM_CAPABILITIES_INTEL: {
      const cl_device_unified_shared_memory_capabilities_intel v =
          CL_UNIFIED_SHARED_MEMORY_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL
          | CL_UNIFIED_SHARED_MEMORY_ATOMIC_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ATOMIC_ACCESS_INTEL;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_DEVICE_MEM_CAPABILITIES_INTEL:
    case CL_DEVICE_CROSS_DEVICE_SHARED_MEM_CAPABILITIES_INTEL: {
      cl_device_unified_shared_memory_capabilities_intel const v =
          CL_UNIFIED_SHARED_MEMORY_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL;
      return outputScalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}

// Profiling timestamps come from the same host clock, so the device timer is the host timer.
CL_API_ENTRY cl_int CL_API_CALL clGetDeviceAndHostTimer(cl_device_id value, cl_ulong *deviceTimestamp, cl_ulong *hostTimestamp) {
  if (!validDevice(value)) return CL_INVALID_DEVICE;
  if (!deviceTimestamp || !hostTimestamp) return CL_INVALID_VALUE;
  *deviceTimestamp = nowNanos();
  *hostTimestamp = nowNanos();
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clGetHostTimer(cl_device_id value, cl_ulong *hostTimestamp) {
  if (!validDevice(value)) return CL_INVALID_DEVICE;
  if (!hostTimestamp) return CL_INVALID_VALUE;
  *hostTimestamp = nowNanos();
  return CL_SUCCESS;
}

CL_API_ENTRY cl_context CL_API_CALL clCreateContext(const cl_context_properties *properties, cl_uint count, const cl_device_id *devices,
                                                    void(CL_CALLBACK *notify)(const char *, const void *, size_t, void *), void *user,
                                                    cl_int *error) {
  if (properties) {
    bool platformSeen = false;
    bool interopSeen = false;
    for (size_t index = 0; properties[index] != 0; index += 2) {
      const cl_context_properties property = properties[index];
      const cl_context_properties value = properties[index + 1];
      if (property == CL_CONTEXT_PLATFORM) {
        if (platformSeen) return fail(error, CL_INVALID_PROPERTY);
        if (reinterpret_cast<cl_platform_id>(value) != reinterpret_cast<cl_platform_id>(&platform)) return fail(error, CL_INVALID_PLATFORM);
        platformSeen = true;
      } else if (property == CL_CONTEXT_INTEROP_USER_SYNC) {
        if (interopSeen || (value != CL_TRUE && value != CL_FALSE)) return fail(error, CL_INVALID_PROPERTY);
        interopSeen = true;
      } else {
        return fail(error, CL_INVALID_PROPERTY);
      }
    }
  }
  if (count == 0 || !devices) return fail(error, CL_INVALID_VALUE);
  if (count != 1 || !validDevice(devices[0])) return fail(error, CL_INVALID_DEVICE);
  if (!notify && user) return fail(error, CL_INVALID_VALUE);
  auto *result = newObject<_cl_context>();
  if (!result) return fail(error, CL_OUT_OF_HOST_MEMORY);
  result->identity = nextContextIdentity.fetch_add(1, std::memory_order_relaxed);
  result->device = &device;
  if (properties) {
    try {
      for (size_t index = 0; properties[index] != 0; index += 2) {
        result->properties.push_back(properties[index]);
        result->properties.push_back(properties[index + 1]);
      }
      result->properties.push_back(0);
    } catch (const std::exception &) {
      destroyObject(result);
      return fail(error, CL_OUT_OF_HOST_MEMORY);
    }
  }
  return succeed(error, result);
}

CL_API_ENTRY cl_context CL_API_CALL clCreateContextFromType(const cl_context_properties *properties, cl_device_type type,
                                                            void(CL_CALLBACK *notify)(const char *, const void *, size_t, void *),
                                                            void *user, cl_int *error) {
  cl_device_id selected = nullptr;
  cl_int const result = clGetDeviceIDs(reinterpret_cast<cl_platform_id>(&platform), type, 1, &selected, nullptr);
  if (result != CL_SUCCESS) return fail(error, result);
  return clCreateContext(properties, 1, &selected, notify, user, error);
}

CL_API_ENTRY cl_int CL_API_CALL clRetainContext(cl_context value) { return retainObject(checked(value), CL_INVALID_CONTEXT); }
CL_API_ENTRY cl_int CL_API_CALL clReleaseContext(cl_context value) {
  auto *context = checked(value);
  if (!context) return CL_INVALID_CONTEXT;
  if (release(context)) {
    context->destructorCallbacks ^ reverse() ^ for_each([&](auto callback, void *user) {
      if (callback) callback(value, user);
    });
    destroyObject(context);
  }
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clSetContextDestructorCallback(cl_context value, void(CL_CALLBACK *callback)(cl_context, void *),
                                                               void *user) {
  auto *context = checked(value);
  if (!context) return CL_INVALID_CONTEXT;
  if (!callback) return CL_INVALID_VALUE;
  try {
    context->destructorCallbacks.emplace_back(callback, user);
  } catch (const std::bad_alloc &) {
    return CL_OUT_OF_HOST_MEMORY;
  }
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clGetContextInfo(cl_context value, cl_context_info name, size_t size, void *output, size_t *written) {
  auto *context = checked(value);
  if (!context) return CL_INVALID_CONTEXT;
  switch (name) {
    case CL_CONTEXT_REFERENCE_COUNT: return outputScalar(size, output, written, cl_uint{context->header.references.load()});
    case CL_CONTEXT_NUM_DEVICES: return outputScalar(size, output, written, cl_uint{1});
    case CL_CONTEXT_DEVICES: return outputScalar(size, output, written, reinterpret_cast<cl_device_id>(&device));
    case CL_CONTEXT_PROPERTIES:
      return outputBytes(size, output, written, context->properties.data(), context->properties.size() * sizeof(cl_context_properties));
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_command_queue CL_API_CALL clCreateCommandQueue(cl_context value, cl_device_id selected,
                                                               cl_command_queue_properties properties, cl_int *error) {
  auto *context = checked(value);
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  if (!validDevice(selected) || context->device != &device) return fail(error, CL_INVALID_DEVICE);
  constexpr cl_command_queue_properties knownProperties = CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_PROFILING_ENABLE;
  if ((properties & ~knownProperties) != 0) return fail(error, CL_INVALID_VALUE);
  auto *queue = newObject<_cl_command_queue>();
  if (!queue) return fail(error, CL_OUT_OF_HOST_MEMORY);
  queue->context = context;
  queue->device = &device;
  queue->properties = properties;
  try {
    queue->stream = scheduler().createStream(true);
  } catch (const std::bad_alloc &) {
    destroyObject(queue);
    return fail(error, CL_OUT_OF_HOST_MEMORY);
  }
  retain(context);
  return succeed(error, queue);
}

cl_command_queue createDeviceQueue(_cl_context *context, cl_command_queue_properties properties, cl_uint size, cl_int *error) {
  if ((properties & CL_QUEUE_ON_DEVICE_DEFAULT) && context->defaultDeviceQueue) {
    retain(context->defaultDeviceQueue);
    return succeed(error, context->defaultDeviceQueue);
  }
  auto *queue = newObject<_cl_command_queue>();
  if (!queue) return fail(error, CL_OUT_OF_HOST_MEMORY);
  queue->context = context;
  queue->device = &device;
  queue->properties = properties;
  queue->deviceSize = size ? size : DeviceQueuePreferredSize;
  if (properties & CL_QUEUE_ON_DEVICE_DEFAULT) context->defaultDeviceQueue = queue;
  retain(context);
  return succeed(error, queue);
}

CL_API_ENTRY cl_command_queue CL_API_CALL clCreateCommandQueueWithProperties(cl_context value, cl_device_id deviceValue,
                                                                             const cl_queue_properties *properties, cl_int *error) {
  auto *context = checked(value);
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  if (!validDevice(deviceValue) || context->device != &device) return fail(error, CL_INVALID_DEVICE);
  cl_command_queue_properties queueProperties = 0;
  std::optional<cl_queue_properties> size;
  if (properties)
    for (size_t index = 0; properties[index] != 0; index += 2) {
      if (properties[index] == CL_QUEUE_PROPERTIES) {
        queueProperties = static_cast<cl_command_queue_properties>(properties[index + 1]);
      } else if (properties[index] == CL_QUEUE_SIZE) {
        size = properties[index + 1];
      } else {
        return fail(error, CL_INVALID_VALUE);
      }
    }
  const bool onDevice = queueProperties & CL_QUEUE_ON_DEVICE;
  if (onDevice && !deviceEnqueueSupported()) return fail(error, CL_INVALID_QUEUE_PROPERTIES);
  if ((!onDevice && (size || (queueProperties & CL_QUEUE_ON_DEVICE_DEFAULT)))
      || (onDevice && !(queueProperties & CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE)) || (size && *size > DeviceQueueMaxSize))
    return fail(error, CL_INVALID_VALUE);
  auto *result = onDevice ? createDeviceQueue(context, queueProperties, static_cast<cl_uint>(size.value_or(0)), error)
                          : clCreateCommandQueue(value, deviceValue, queueProperties, error);
  auto *queue = reinterpret_cast<_cl_command_queue *>(result);
  if (queue && properties && queue->propertiesArray.empty()) {
    try {
      for (size_t index = 0; properties[index] != 0; index += 2) {
        queue->propertiesArray.push_back(properties[index]);
        queue->propertiesArray.push_back(properties[index + 1]);
      }
      queue->propertiesArray.push_back(0);
    } catch (const std::exception &) {
      clReleaseCommandQueue(result);
      return fail(error, CL_OUT_OF_HOST_MEMORY);
    }
  }
  return result;
}

CL_API_ENTRY cl_int CL_API_CALL clSetDefaultDeviceCommandQueue(cl_context value, cl_device_id selected, cl_command_queue queueValue) {
  auto *context = checked(value);
  if (!context) return CL_INVALID_CONTEXT;
  if (!validDevice(selected) || context->device != &device) return CL_INVALID_DEVICE;
  auto *queue = anyQueue(queueValue);
  if (!queue || queue->deviceSize == 0 || queue->context != context) return CL_INVALID_COMMAND_QUEUE;
  context->defaultDeviceQueue = queue;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clRetainCommandQueue(cl_command_queue value) {
  return retainObject(anyQueue(value), CL_INVALID_COMMAND_QUEUE);
}
CL_API_ENTRY cl_int CL_API_CALL clReleaseCommandQueue(cl_command_queue value) {
  auto *queue = anyQueue(value);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (release(queue)) {
    auto *context = queue->context;
    liveObjects.erase(queue);
    if (!queue->deviceSize) scheduler().destroyStream(queue->stream);
    else if (context->defaultDeviceQueue == queue) context->defaultDeviceQueue = nullptr;
    delete queue;
    clReleaseContext(reinterpret_cast<cl_context>(context));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetCommandQueueProperty(cl_command_queue value, cl_command_queue_properties, cl_bool,
                                                          cl_command_queue_properties *) {
  return checked(value) == nullptr ? CL_INVALID_COMMAND_QUEUE : CL_INVALID_OPERATION;
}
CL_API_ENTRY cl_int CL_API_CALL clGetCommandQueueInfo(cl_command_queue value, cl_command_queue_info name, size_t size, void *output,
                                                      size_t *written) {
  auto *queue = anyQueue(value);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  switch (name) {
    case CL_QUEUE_CONTEXT: return outputScalar(size, output, written, reinterpret_cast<cl_context>(queue->context));
    case CL_QUEUE_DEVICE: return outputScalar(size, output, written, reinterpret_cast<cl_device_id>(&device));
    case CL_QUEUE_REFERENCE_COUNT: return outputScalar(size, output, written, cl_uint{queue->header.references.load()});
    case CL_QUEUE_PROPERTIES: return outputScalar(size, output, written, queue->properties);
    case CL_QUEUE_PROPERTIES_ARRAY:
      return outputBytes(size, output, written, queue->propertiesArray.data(), queue->propertiesArray.size() * sizeof(cl_queue_properties));
    case CL_QUEUE_SIZE: return queue->deviceSize ? outputScalar(size, output, written, queue->deviceSize) : CL_INVALID_COMMAND_QUEUE;
    case CL_QUEUE_DEVICE_DEFAULT:
      return outputScalar(size, output, written, reinterpret_cast<cl_command_queue>(queue->context->defaultDeviceQueue));
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateBuffer(cl_context value, cl_mem_flags flags, size_t size, void *host, cl_int *error) {
  auto *context = checked(value);
  if (!context || size == 0) return fail(error, context ? CL_INVALID_BUFFER_SIZE : CL_INVALID_CONTEXT);
  constexpr cl_mem_flags accessFlags = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY;
  if (immutableFlags(flags) != CL_SUCCESS) return fail(error, CL_INVALID_VALUE);
  if ((flags & accessFlags) == 0) flags |= flags & CL_MEM_IMMUTABLE_EXT ? CL_MEM_READ_ONLY : CL_MEM_READ_WRITE;
  if ((flags & (CL_MEM_USE_HOST_PTR | CL_MEM_COPY_HOST_PTR)) && !host) return fail(error, CL_INVALID_HOST_PTR);
  size_t hostOffset = 0;
  auto allocation = (flags & CL_MEM_USE_HOST_PTR) != 0 ? findPointer(host, &hostOffset) : nullptr;
  const bool aliasesRuntimeAllocation = allocation && allocation->runtimeOwnsBase;
  if (aliasesRuntimeAllocation && !validRange(allocation, hostOffset, size)) return fail(error, CL_INVALID_BUFFER_SIZE);
  if (!aliasesRuntimeAllocation) {
    hostOffset = 0;
    allocation = allocate(size, error);
  }
  if (!allocation) return nullptr;
  if (!aliasesRuntimeAllocation && host && (flags & (CL_MEM_USE_HOST_PTR | CL_MEM_COPY_HOST_PTR)))
    std::memcpy(allocation->data(), host, size);
  cl_int writeResult = CL_SUCCESS;
  if (remote.ready()) {
    std::scoped_lock const lock(remoteMutex);
    if (!writeRemote(*remote.client(), allocation->id, hostOffset, static_cast<uint8_t *>(allocation->data()) + hostOffset, size)) {
      if (gemstone::traceEnabled("opencl")) std::fprintf(stderr, "Gemstone OpenCL buffer upload failed: %s\n", remote.error().c_str());
      writeResult = CL_OUT_OF_RESOURCES;
    }
  }
  if (writeResult != CL_SUCCESS) {
    if (!aliasesRuntimeAllocation) releaseAllocation(allocation);
    return fail(error, writeResult);
  }
  auto *result = newObject<_cl_mem>();
  if (!result) {
    if (!aliasesRuntimeAllocation) releaseAllocation(allocation);
    return fail(error, CL_OUT_OF_HOST_MEMORY);
  }
  result->context = context;
  result->allocation = std::move(allocation);
  result->flags = flags;
  result->size = size;
  retain(context);
  result->origin = hostOffset;
  result->hostPointer = (flags & CL_MEM_USE_HOST_PTR) == 0 ? nullptr : host;
  return succeed(error, result);
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateBufferWithProperties(cl_context value, const cl_mem_properties *properties, cl_mem_flags flags,
                                                             size_t size, void *host, cl_int *error) {
  if (properties && properties[0] != 0) return fail(error, CL_INVALID_PROPERTY);
  auto *result = clCreateBuffer(value, flags, size, host, error);
  if (result && properties) result->properties.push_back(0);
  return result;
}

CL_API_ENTRY cl_mem CL_API_CALL clCreatePipe(cl_context value, cl_mem_flags flags, cl_uint packetSize, cl_uint packets,
                                             const cl_pipe_properties *properties, cl_int *error) {
  auto *context = checked(value);
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  if ((flags & ~(CL_MEM_READ_WRITE | CL_MEM_HOST_NO_ACCESS)) != 0 || (properties && properties[0] != 0))
    return fail(error, CL_INVALID_VALUE);
  const size_t bytes = pipe::Packets + (size_t{packetSize} * packets);
  if (packetSize == 0 || packetSize > PipeMaxPacketSize || packets == 0 || bytes > MaxMemAllocSize)
    return fail(error, CL_INVALID_PIPE_SIZE);
  cl_int status = CL_SUCCESS;
  auto allocation = allocate(bytes, &status);
  if (!allocation) return fail(error, status);
  const std::array<uint32_t, 4> header{0, 0, packets, packetSize};
  if (writeAllocation(allocation, pipe::ReadIndex, header.data(), sizeof(header)) != CL_SUCCESS) {
    releaseAllocation(allocation);
    return fail(error, CL_OUT_OF_RESOURCES);
  }
  auto *result = newObject<_cl_mem>();
  if (!result) {
    releaseAllocation(allocation);
    return fail(error, CL_OUT_OF_HOST_MEMORY);
  }
  result->context = context;
  result->allocation = std::move(allocation);
  result->flags = CL_MEM_READ_WRITE | CL_MEM_HOST_NO_ACCESS;
  result->size = bytes;
  result->pipe = std::make_pair(packetSize, packets);
  if (properties) result->properties.push_back(0);
  retain(context);
  return succeed(error, result);
}

} // extern "C"

namespace {

enum class Channel { Unorm, Snorm, Signed, Unsigned, Half, Float };

struct ImageFormatInfo {
  cl_channel_order order;
  cl_channel_type type;
  uint32_t descriptor; // A SPIR-V ImageFormat, or a private ImageDescriptor::Format code.
  size_t channels;
  size_t channelBytes;
  Channel channel;
  bool writable;
};

constexpr ImageFormatInfo imageFormat(cl_channel_order order, cl_channel_type type, uint32_t descriptor, size_t channels,
                                      size_t channelBytes, Channel channel, bool writable = true) {
  return {.order = order,
          .type = type,
          .descriptor = descriptor,
          .channels = channels,
          .channelBytes = channelBytes,
          .channel = channel,
          .writable = writable};
}

constexpr ImageFormatInfo ImageFormats[] = {
    imageFormat(CL_R, CL_UNORM_INT8, 15, 1, 1, Channel::Unorm),
    imageFormat(CL_R, CL_UNORM_INT16, 14, 1, 2, Channel::Unorm),
    imageFormat(CL_R, CL_SNORM_INT8, 20, 1, 1, Channel::Snorm),
    imageFormat(CL_R, CL_SNORM_INT16, 19, 1, 2, Channel::Snorm),
    imageFormat(CL_R, CL_SIGNED_INT8, 29, 1, 1, Channel::Signed),
    imageFormat(CL_R, CL_SIGNED_INT16, 28, 1, 2, Channel::Signed),
    imageFormat(CL_R, CL_SIGNED_INT32, 24, 1, 4, Channel::Signed),
    imageFormat(CL_R, CL_UNSIGNED_INT8, 39, 1, 1, Channel::Unsigned),
    imageFormat(CL_R, CL_UNSIGNED_INT16, 38, 1, 2, Channel::Unsigned),
    imageFormat(CL_R, CL_UNSIGNED_INT32, 33, 1, 4, Channel::Unsigned),
    imageFormat(CL_R, CL_HALF_FLOAT, 9, 1, 2, Channel::Half),
    imageFormat(CL_R, CL_FLOAT, 3, 1, 4, Channel::Float),
    imageFormat(CL_RG, CL_UNORM_INT8, 13, 2, 1, Channel::Unorm),
    imageFormat(CL_RG, CL_UNORM_INT16, 12, 2, 2, Channel::Unorm),
    imageFormat(CL_RG, CL_SNORM_INT8, 18, 2, 1, Channel::Snorm),
    imageFormat(CL_RG, CL_SNORM_INT16, 17, 2, 2, Channel::Snorm),
    imageFormat(CL_RG, CL_SIGNED_INT8, 27, 2, 1, Channel::Signed),
    imageFormat(CL_RG, CL_SIGNED_INT16, 26, 2, 2, Channel::Signed),
    imageFormat(CL_RG, CL_SIGNED_INT32, 25, 2, 4, Channel::Signed),
    imageFormat(CL_RG, CL_UNSIGNED_INT8, 37, 2, 1, Channel::Unsigned),
    imageFormat(CL_RG, CL_UNSIGNED_INT16, 36, 2, 2, Channel::Unsigned),
    imageFormat(CL_RG, CL_UNSIGNED_INT32, 35, 2, 4, Channel::Unsigned),
    imageFormat(CL_RG, CL_HALF_FLOAT, 7, 2, 2, Channel::Half),
    imageFormat(CL_RG, CL_FLOAT, 6, 2, 4, Channel::Float),
    imageFormat(CL_RGBA, CL_UNORM_INT8, 4, 4, 1, Channel::Unorm),
    imageFormat(CL_RGBA, CL_UNORM_INT16, 10, 4, 2, Channel::Unorm),
    imageFormat(CL_RGBA, CL_SNORM_INT8, 5, 4, 1, Channel::Snorm),
    imageFormat(CL_RGBA, CL_SNORM_INT16, 16, 4, 2, Channel::Snorm),
    imageFormat(CL_RGBA, CL_SIGNED_INT8, 23, 4, 1, Channel::Signed),
    imageFormat(CL_RGBA, CL_SIGNED_INT16, 22, 4, 2, Channel::Signed),
    imageFormat(CL_RGBA, CL_SIGNED_INT32, 21, 4, 4, Channel::Signed),
    imageFormat(CL_RGBA, CL_UNSIGNED_INT8, 32, 4, 1, Channel::Unsigned),
    imageFormat(CL_RGBA, CL_UNSIGNED_INT16, 31, 4, 2, Channel::Unsigned),
    imageFormat(CL_RGBA, CL_UNSIGNED_INT32, 30, 4, 4, Channel::Unsigned),
    imageFormat(CL_RGBA, CL_HALF_FLOAT, 2, 4, 2, Channel::Half),
    imageFormat(CL_RGBA, CL_FLOAT, 1, 4, 4, Channel::Float),
    imageFormat(CL_BGRA, CL_UNORM_INT8, descriptor::Format::Bgra8Unorm, 4, 1, Channel::Unorm),
    imageFormat(CL_sRGBA, CL_UNORM_INT8, descriptor::Format::Rgba8Srgb, 4, 1, Channel::Unorm, false),
    imageFormat(CL_DEPTH, CL_UNORM_INT16, descriptor::Format::D16Unorm, 1, 2, Channel::Unorm),
    imageFormat(CL_DEPTH, CL_FLOAT, descriptor::Format::D32Sfloat, 1, 4, Channel::Float),
};

// Depth images are two-dimensional, optionally arrayed.
bool formatSupports(const ImageFormatInfo &info, cl_mem_object_type type) {
  return info.order != CL_DEPTH || type == CL_MEM_OBJECT_IMAGE2D || type == CL_MEM_OBJECT_IMAGE2D_ARRAY;
}

constexpr auto ChannelOrders =
    std::to_array<cl_channel_order>({CL_R, CL_A, CL_RG, CL_RA, CL_RGB, CL_RGBA, CL_BGRA, CL_ARGB, CL_INTENSITY, CL_LUMINANCE, CL_Rx, CL_RGx,
                                     CL_RGBx, CL_DEPTH, CL_sRGB, CL_sRGBx, CL_sRGBA, CL_sBGRA, CL_ABGR});
constexpr auto ChannelTypes =
    std::to_array<cl_channel_type>({CL_SNORM_INT8, CL_SNORM_INT16, CL_UNORM_INT8, CL_UNORM_INT16, CL_UNORM_SHORT_565, CL_UNORM_SHORT_555,
                                    CL_UNORM_INT_101010, CL_SIGNED_INT8, CL_SIGNED_INT16, CL_SIGNED_INT32, CL_UNSIGNED_INT8,
                                    CL_UNSIGNED_INT16, CL_UNSIGNED_INT32, CL_HALF_FLOAT, CL_FLOAT, CL_UNORM_INT24, CL_UNORM_INT_101010_2});

constexpr size_t ImageRecordBytes = descriptor::Layout::LevelTable + descriptor::Layout::LevelBytes + descriptor::Layout::PitchBytes;

constexpr size_t SamplerRecordBytes = static_cast<size_t>(descriptor::SamplerWord::Count) * 4;

const ImageFormatInfo *imageFormatInfo(const cl_image_format &format) {
  for (const auto &info : ImageFormats)
    if (info.order == format.image_channel_order && info.type == format.image_channel_data_type) return &info;
  return nullptr;
}

bool knownImageFormat(const cl_image_format &format) {
  return (ChannelOrders ^ contains(format.image_channel_order)) && (ChannelTypes ^ contains(format.image_channel_data_type));
}

bool imageType(cl_mem_object_type type) {
  return type == CL_MEM_OBJECT_IMAGE1D || type == CL_MEM_OBJECT_IMAGE1D_BUFFER || type == CL_MEM_OBJECT_IMAGE1D_ARRAY
         || type == CL_MEM_OBJECT_IMAGE2D || type == CL_MEM_OBJECT_IMAGE2D_ARRAY || type == CL_MEM_OBJECT_IMAGE3D;
}

bool arrayed(cl_mem_object_type type) { return type == CL_MEM_OBJECT_IMAGE1D_ARRAY || type == CL_MEM_OBJECT_IMAGE2D_ARRAY; }

size_t imageHeight(const cl_image_desc &desc) {
  return desc.image_type == CL_MEM_OBJECT_IMAGE2D || desc.image_type == CL_MEM_OBJECT_IMAGE2D_ARRAY
                 || desc.image_type == CL_MEM_OBJECT_IMAGE3D
             ? desc.image_height
             : 1;
}

// Layers of an array, or the depth of a 3D image.
size_t imageSlices(const cl_image_desc &desc) {
  if (desc.image_type == CL_MEM_OBJECT_IMAGE3D) return desc.image_depth;
  if (arrayed(desc.image_type)) return desc.image_array_size;
  return 1;
}

void putWord(uint8_t *record, size_t offset, uint64_t value, size_t bytes) { std::memcpy(record + offset, &value, bytes); }

std::array<uint8_t, ImageRecordBytes> imageRecord(const cl_image_desc &desc, const ImageFormatInfo &info, uintptr_t texels, size_t bytes,
                                                  size_t rowPitch) {
  std::array<uint8_t, ImageRecordBytes> record{};
  const bool pitched = rowPitch != desc.image_width * info.channels * info.channelBytes;
  const uint32_t format = info.descriptor | descriptor::Flags::LevelTable
                          | (desc.image_type == CL_MEM_OBJECT_IMAGE3D ? descriptor::Flags::Volume : 0U)
                          | (pitched ? descriptor::Flags::Pitched : 0U);
  putWord(record.data(), descriptor::Layout::Width, desc.image_width, 4);
  putWord(record.data(), descriptor::Layout::Height, imageHeight(desc), 4);
  putWord(record.data(), descriptor::Layout::Slices, imageSlices(desc), 4);
  putWord(record.data(), descriptor::Layout::Format, format, 4);
  putWord(record.data(), descriptor::Layout::Texels, texels, 8);
  putWord(record.data(), descriptor::Layout::TexelBytes, bytes, 8);
  putWord(record.data(), descriptor::Layout::Levels, 1, 4);
  putWord(record.data(), descriptor::Layout::LevelTable + descriptor::Layout::LevelWidth, desc.image_width, 4);
  putWord(record.data(), descriptor::Layout::LevelTable + descriptor::Layout::LevelHeight, imageHeight(desc), 4);
  if (pitched) putWord(record.data(), descriptor::Layout::LevelTable + descriptor::Layout::LevelBytes, rowPitch, 8);
  return record;
}

cl_int validateImageSize(const cl_image_desc &desc) {
  const auto within = [](size_t value, size_t limit) { return value >= 1 && value <= limit; };
  switch (desc.image_type) {
    case CL_MEM_OBJECT_IMAGE1D: return within(desc.image_width, Image2DMaxSize) ? CL_SUCCESS : CL_INVALID_IMAGE_SIZE;
    case CL_MEM_OBJECT_IMAGE1D_BUFFER: return within(desc.image_width, ImageMaxBufferSize) ? CL_SUCCESS : CL_INVALID_IMAGE_SIZE;
    case CL_MEM_OBJECT_IMAGE1D_ARRAY:
      return within(desc.image_width, Image2DMaxSize) && within(desc.image_array_size, ImageMaxArraySize) ? CL_SUCCESS
                                                                                                          : CL_INVALID_IMAGE_SIZE;
    case CL_MEM_OBJECT_IMAGE2D:
      return within(desc.image_width, Image2DMaxSize) && within(desc.image_height, Image2DMaxSize) ? CL_SUCCESS : CL_INVALID_IMAGE_SIZE;
    case CL_MEM_OBJECT_IMAGE2D_ARRAY:
      return within(desc.image_width, Image2DMaxSize) && within(desc.image_height, Image2DMaxSize)
                     && within(desc.image_array_size, ImageMaxArraySize)
                 ? CL_SUCCESS
                 : CL_INVALID_IMAGE_SIZE;
    case CL_MEM_OBJECT_IMAGE3D:
      return within(desc.image_width, Image3DMaxSize) && within(desc.image_height, Image3DMaxSize)
                     && within(desc.image_depth, Image3DMaxSize)
                 ? CL_SUCCESS
                 : CL_INVALID_IMAGE_SIZE;
    default: return CL_INVALID_IMAGE_DESCRIPTOR;
  }
}

cl_mem createImage(_cl_context *context, cl_mem_flags flags, const cl_image_format *format, const cl_image_desc *desc, void *host,
                   cl_int *error) {
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  constexpr cl_mem_flags accessFlags = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY | CL_MEM_KERNEL_READ_AND_WRITE;
  constexpr cl_mem_flags hostAccessFlags = CL_MEM_HOST_READ_ONLY | CL_MEM_HOST_WRITE_ONLY | CL_MEM_HOST_NO_ACCESS;
  if (!format || !knownImageFormat(*format)) return fail(error, CL_INVALID_IMAGE_FORMAT_DESCRIPTOR);
  if (!desc || !imageType(desc->image_type) || desc->num_mip_levels != 0 || desc->num_samples != 0)
    return fail(error, CL_INVALID_IMAGE_DESCRIPTOR);
  const auto *info = imageFormatInfo(*format);
  if (!info || !formatSupports(*info, desc->image_type)) return fail(error, CL_IMAGE_FORMAT_NOT_SUPPORTED);
  if (const auto status = validateImageSize(*desc); status != CL_SUCCESS) return fail(error, status);
  if (std::popcount(flags & (CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY)) > 1 || std::popcount(flags & hostAccessFlags) > 1
      || ((flags & CL_MEM_USE_HOST_PTR) && (flags & CL_MEM_COPY_HOST_PTR))
      || ((flags & CL_MEM_USE_HOST_PTR) && (flags & CL_MEM_ALLOC_HOST_PTR)))
    return fail(error, CL_INVALID_VALUE);
  if (immutableFlags(flags) != CL_SUCCESS) return fail(error, CL_INVALID_VALUE);
  const bool copiesHost = (flags & (CL_MEM_USE_HOST_PTR | CL_MEM_COPY_HOST_PTR)) != 0;
  if (copiesHost != (host != nullptr)) return fail(error, CL_INVALID_HOST_PTR);
  if ((flags & accessFlags & ~CL_MEM_KERNEL_READ_AND_WRITE) == 0)
    flags |= flags & CL_MEM_IMMUTABLE_EXT ? CL_MEM_READ_ONLY : CL_MEM_READ_WRITE;
  if (!info->writable && (flags & (CL_MEM_READ_WRITE | CL_MEM_WRITE_ONLY | CL_MEM_KERNEL_READ_AND_WRITE)) != 0)
    return fail(error, CL_IMAGE_FORMAT_NOT_SUPPORTED);

  ImageShape shape;
  shape.format = *format;
  shape.desc = *desc;
  shape.elementSize = info->channels * info->channelBytes;
  shape.rowPitch = desc->image_width * shape.elementSize;
  // A 2D image over a buffer or another 2D image addresses the shared texels through their row pitch.
  auto *source = desc->buffer && desc->image_type == CL_MEM_OBJECT_IMAGE2D ? checked(desc->buffer) : nullptr;
  if (desc->buffer && desc->image_type == CL_MEM_OBJECT_IMAGE2D) {
    if (!source || source->context != context || source->pipe) return fail(error, CL_INVALID_IMAGE_DESCRIPTOR);
    if (host) return fail(error, CL_INVALID_HOST_PTR);
    if (const auto &image = source->image) {
      const auto *sourceInfo = imageFormatInfo(image->format);
      if (!sourceInfo || sourceInfo->channels != info->channels || image->format.image_channel_data_type != format->image_channel_data_type)
        return fail(error, CL_INVALID_IMAGE_FORMAT_DESCRIPTOR);
      if (image->desc.image_type != CL_MEM_OBJECT_IMAGE2D || image->desc.image_width != desc->image_width
          || image->desc.image_height != desc->image_height || (desc->image_row_pitch != 0 && desc->image_row_pitch != image->rowPitch))
        return fail(error, CL_INVALID_IMAGE_DESCRIPTOR);
      shape.rowPitch = image->rowPitch;
    } else {
      const size_t pitch = desc->image_row_pitch == 0 ? shape.rowPitch : desc->image_row_pitch;
      if (pitch < shape.rowPitch || pitch % (ImagePitchAlignment * shape.elementSize) != 0
          || source->origin % (ImageBaseAddressAlignment * shape.elementSize) != 0)
        return fail(error, CL_INVALID_IMAGE_FORMAT_DESCRIPTOR);
      shape.rowPitch = pitch;
    }
  }
  shape.slicePitch = shape.rowPitch * imageHeight(*desc);
  const size_t bytes = shape.slicePitch * imageSlices(*desc);
  if (bytes > MaxMemAllocSize) return fail(error, CL_INVALID_IMAGE_SIZE);
  // Host data uses the caller's pitches; the device copy is tightly packed unless it aliases another object.
  const size_t hostRow = source || desc->image_row_pitch == 0 ? shape.rowPitch : desc->image_row_pitch;
  size_t hostSlice = desc->image_slice_pitch;
  if (hostSlice == 0) hostSlice = desc->image_type == CL_MEM_OBJECT_IMAGE1D_ARRAY ? hostRow : hostRow * imageHeight(*desc);
  if (!host && !desc->buffer && (desc->image_row_pitch != 0 || desc->image_slice_pitch != 0))
    return fail(error, CL_INVALID_IMAGE_DESCRIPTOR);
  if (hostRow < shape.rowPitch || hostRow % shape.elementSize != 0) return fail(error, CL_INVALID_IMAGE_DESCRIPTOR);
  const size_t minimumSlice = desc->image_type == CL_MEM_OBJECT_IMAGE1D_ARRAY ? hostRow : hostRow * imageHeight(*desc);
  if ((desc->image_type == CL_MEM_OBJECT_IMAGE3D || arrayed(desc->image_type)) && (hostSlice < minimumSlice || hostSlice % hostRow != 0))
    return fail(error, CL_INVALID_IMAGE_DESCRIPTOR);
  shape.hostRowPitch = hostRow;
  shape.hostSlicePitch = hostSlice;

  std::shared_ptr<Allocation> texels;
  _cl_mem *buffer = nullptr;
  size_t origin = 0;
  if (desc->image_type == CL_MEM_OBJECT_IMAGE1D_BUFFER) {
    buffer = checked(desc->buffer);
    if (!buffer || buffer->image || buffer->context != context) return fail(error, CL_INVALID_IMAGE_DESCRIPTOR);
    if (host) return fail(error, CL_INVALID_HOST_PTR);
    if (bytes > buffer->size) return fail(error, CL_INVALID_IMAGE_SIZE);
    if ((flags & accessFlags) == CL_MEM_READ_WRITE && (buffer->flags & (CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY)) != 0)
      flags = (flags & ~CL_MEM_READ_WRITE) | (buffer->flags & (CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY));
    if ((flags & hostAccessFlags) == 0) flags |= buffer->flags & hostAccessFlags;
    flags |= buffer->flags & (CL_MEM_USE_HOST_PTR | CL_MEM_ALLOC_HOST_PTR | CL_MEM_COPY_HOST_PTR);
    texels = buffer->allocation;
    origin = buffer->origin;
  } else if (source) {
    if (bytes > source->size) return fail(error, CL_INVALID_IMAGE_SIZE);
    if ((flags & accessFlags) == CL_MEM_READ_WRITE && (source->flags & (CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY)) != 0)
      flags = (flags & ~CL_MEM_READ_WRITE) | (source->flags & (CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY));
    if ((flags & hostAccessFlags) == 0) flags |= source->flags & hostAccessFlags;
    flags |= source->flags & (CL_MEM_USE_HOST_PTR | CL_MEM_ALLOC_HOST_PTR | CL_MEM_COPY_HOST_PTR);
    buffer = source;
    texels = source->allocation;
    origin = source->origin;
  } else if (desc->buffer) {
    return fail(error, CL_INVALID_IMAGE_DESCRIPTOR);
  } else {
    cl_int status = CL_SUCCESS;
    texels = allocate(bytes, &status);
    if (!texels) return fail(error, status);
    if (host) {
      auto *packed = static_cast<uint8_t *>(texels->data());
      const auto *source = static_cast<const uint8_t *>(host);
      const size_t rows = imageHeight(*desc);
      const size_t slices = imageSlices(*desc);
      for (size_t z = 0; z < slices; ++z)
        for (size_t y = 0; y < rows; ++y)
          std::memcpy(packed + (z * shape.slicePitch) + (y * shape.rowPitch), source + (z * hostSlice) + (y * hostRow), shape.rowPitch);
      if (writeAllocation(texels, 0, packed, bytes) != CL_SUCCESS) {
        releaseAllocation(texels);
        return fail(error, CL_OUT_OF_RESOURCES);
      }
    }
  }
  cl_int status = CL_SUCCESS;
  shape.record = allocate(ImageRecordBytes, &status);
  const auto record = imageRecord(*desc, *info, reinterpret_cast<uintptr_t>(texels->data()) + origin, bytes, shape.rowPitch);
  if (!shape.record || writeAllocation(shape.record, 0, record.data(), record.size()) != CL_SUCCESS) {
    if (shape.record) releaseAllocation(shape.record);
    if (!buffer) releaseAllocation(texels);
    return fail(error, shape.record ? CL_OUT_OF_RESOURCES : status);
  }
  auto *result = newObject<_cl_mem>();
  if (!result) {
    releaseAllocation(shape.record);
    if (!buffer) releaseAllocation(texels);
    return fail(error, CL_OUT_OF_HOST_MEMORY);
  }
  result->context = context;
  result->allocation = std::move(texels);
  result->flags = flags;
  result->size = bytes;
  result->origin = origin;
  result->hostPointer = (flags & CL_MEM_USE_HOST_PTR) != 0 ? host : nullptr;
  result->parent = buffer;
  result->image = std::move(shape);
  retain(context);
  if (buffer) retain(buffer);
  return succeed(error, result);
}

uint32_t samplerAddress(cl_addressing_mode mode) {
  switch (mode) {
    case CL_ADDRESS_CLAMP: return descriptor::AddressMode::ClampToBorder;
    case CL_ADDRESS_REPEAT: return descriptor::AddressMode::Repeat;
    case CL_ADDRESS_MIRRORED_REPEAT: return descriptor::AddressMode::MirroredRepeat;
    default: return descriptor::AddressMode::ClampToEdge;
  }
}

cl_sampler createSampler(_cl_context *context, cl_bool normalised, cl_addressing_mode addressing, cl_filter_mode filter,
                         std::vector<cl_sampler_properties> properties, cl_int *error) {
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  if ((normalised != CL_TRUE && normalised != CL_FALSE)
      || (addressing != CL_ADDRESS_NONE && addressing != CL_ADDRESS_CLAMP_TO_EDGE && addressing != CL_ADDRESS_CLAMP
          && addressing != CL_ADDRESS_REPEAT && addressing != CL_ADDRESS_MIRRORED_REPEAT)
      || (filter != CL_FILTER_NEAREST && filter != CL_FILTER_LINEAR))
    return fail(error, CL_INVALID_VALUE);
  if (!normalised && (addressing == CL_ADDRESS_REPEAT || addressing == CL_ADDRESS_MIRRORED_REPEAT)) return fail(error, CL_INVALID_VALUE);
  std::array<uint32_t, descriptor::SamplerWord::Count> words{};
  const uint32_t filtering = filter == CL_FILTER_LINEAR ? descriptor::Filter::Linear : descriptor::Filter::Nearest;
  const float maxLod = 1000.0F;
  words[descriptor::SamplerWord::MinFilter] = filtering;
  words[descriptor::SamplerWord::MagFilter] = filtering;
  words[descriptor::SamplerWord::AddressU] = samplerAddress(addressing);
  words[descriptor::SamplerWord::AddressV] = samplerAddress(addressing);
  words[descriptor::SamplerWord::AddressW] = samplerAddress(addressing);
  words[descriptor::SamplerWord::Unnormalized] = normalised ? 0U : 1U;
  words[descriptor::SamplerWord::BorderColour] = descriptor::BorderColour::FloatTransparentBlack;
  std::memcpy(&words[descriptor::SamplerWord::MaxLod], &maxLod, sizeof(maxLod));
  cl_int status = CL_SUCCESS;
  auto record = allocate(SamplerRecordBytes, &status);
  if (!record) return fail(error, status);
  if (writeAllocation(record, 0, words.data(), SamplerRecordBytes) != CL_SUCCESS) {
    releaseAllocation(record);
    return fail(error, CL_OUT_OF_RESOURCES);
  }
  auto *sampler = newObject<_cl_sampler>();
  if (!sampler) {
    releaseAllocation(record);
    return fail(error, CL_OUT_OF_HOST_MEMORY);
  }
  sampler->context = context;
  sampler->normalised = normalised;
  sampler->addressing = addressing;
  sampler->filter = filter;
  sampler->properties = std::move(properties);
  sampler->record = std::move(record);
  retain(context);
  return succeed(error, sampler);
}

} // namespace

extern "C" {

CL_API_ENTRY cl_mem CL_API_CALL clCreateImage(cl_context value, cl_mem_flags flags, const cl_image_format *format,
                                              const cl_image_desc *desc, void *host, cl_int *error) {
  return createImage(checked(value), flags, format, desc, host, error);
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateImageWithProperties(cl_context value, const cl_mem_properties *properties, cl_mem_flags flags,
                                                            const cl_image_format *format, const cl_image_desc *desc, void *host,
                                                            cl_int *error) {
  if (properties && properties[0] != 0) return fail(error, CL_INVALID_PROPERTY);
  auto *result = createImage(checked(value), flags, format, desc, host, error);
  if (result && properties) reinterpret_cast<_cl_mem *>(result)->properties.push_back(0);
  return result;
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateImage2D(cl_context value, cl_mem_flags flags, const cl_image_format *format, size_t width,
                                                size_t height, size_t rowPitch, void *host, cl_int *error) {
  cl_image_desc desc{};
  desc.image_type = CL_MEM_OBJECT_IMAGE2D;
  desc.image_width = width;
  desc.image_height = height;
  desc.image_row_pitch = rowPitch;
  return createImage(checked(value), flags, format, &desc, host, error);
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateImage3D(cl_context value, cl_mem_flags flags, const cl_image_format *format, size_t width,
                                                size_t height, size_t depth, size_t rowPitch, size_t slicePitch, void *host,
                                                cl_int *error) {
  cl_image_desc desc{};
  desc.image_type = CL_MEM_OBJECT_IMAGE3D;
  desc.image_width = width;
  desc.image_height = height;
  desc.image_depth = depth;
  desc.image_row_pitch = rowPitch;
  desc.image_slice_pitch = slicePitch;
  return createImage(checked(value), flags, format, &desc, host, error);
}

CL_API_ENTRY cl_int CL_API_CALL clGetImageInfo(cl_mem value, cl_image_info name, size_t size, void *output, size_t *written) {
  auto *mem = checked(value);
  if (!mem || !mem->image) return CL_INVALID_MEM_OBJECT;
  const auto &image = *mem->image;
  const auto type = image.desc.image_type;
  const auto zero = size_t{0};
  switch (name) {
    case CL_IMAGE_FORMAT: return outputScalar(size, output, written, image.format);
    case CL_IMAGE_ELEMENT_SIZE: return outputScalar(size, output, written, image.elementSize);
    case CL_IMAGE_ROW_PITCH: return outputScalar(size, output, written, image.rowPitch);
    case CL_IMAGE_SLICE_PITCH: {
      const size_t pitch = type == CL_MEM_OBJECT_IMAGE3D || arrayed(type) ? image.slicePitch : 0;
      return outputScalar(size, output, written, pitch);
    }
    case CL_IMAGE_WIDTH: return outputScalar(size, output, written, image.desc.image_width);
    case CL_IMAGE_HEIGHT: {
      const size_t height = type == CL_MEM_OBJECT_IMAGE2D || type == CL_MEM_OBJECT_IMAGE2D_ARRAY || type == CL_MEM_OBJECT_IMAGE3D
                                ? image.desc.image_height
                                : 0;
      return outputScalar(size, output, written, height);
    }
    case CL_IMAGE_DEPTH: return outputScalar(size, output, written, type == CL_MEM_OBJECT_IMAGE3D ? image.desc.image_depth : zero);
    case CL_IMAGE_ARRAY_SIZE: return outputScalar(size, output, written, arrayed(type) ? image.desc.image_array_size : zero);
    case CL_IMAGE_BUFFER: {
      cl_mem buffer = mem->parent && !mem->parent->image ? reinterpret_cast<cl_mem>(mem->parent) : nullptr;
      return outputScalar(size, output, written, buffer);
    }
    case CL_IMAGE_NUM_MIP_LEVELS:
    case CL_IMAGE_NUM_SAMPLES: return outputScalar(size, output, written, cl_uint{0});
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_sampler CL_API_CALL clCreateSampler(cl_context value, cl_bool normalised, cl_addressing_mode addressing,
                                                    cl_filter_mode filter, cl_int *error) {
  return createSampler(checked(value), normalised, addressing, filter, {}, error);
}

CL_API_ENTRY cl_sampler CL_API_CALL clCreateSamplerWithProperties(cl_context value, const cl_sampler_properties *properties,
                                                                  cl_int *error) {
  cl_bool normalised = CL_TRUE;
  cl_addressing_mode addressing = CL_ADDRESS_CLAMP;
  cl_filter_mode filter = CL_FILTER_NEAREST;
  std::vector<cl_sampler_properties> copied;
  std::set<cl_sampler_properties> seen;
  for (size_t index = 0; properties && properties[index] != 0; index += 2) {
    const auto key = properties[index];
    const auto setting = properties[index + 1];
    if (!seen.insert(key).second) return fail(error, CL_INVALID_VALUE);
    if (key == CL_SAMPLER_NORMALIZED_COORDS) {
      normalised = static_cast<cl_bool>(setting);
    } else if (key == CL_SAMPLER_ADDRESSING_MODE) {
      addressing = static_cast<cl_addressing_mode>(setting);
    } else if (key == CL_SAMPLER_FILTER_MODE) {
      filter = static_cast<cl_filter_mode>(setting);
    } else {
      return fail(error, CL_INVALID_VALUE);
    }
    copied.push_back(key);
    copied.push_back(setting);
  }
  if (properties) copied.push_back(0);
  return createSampler(checked(value), normalised, addressing, filter, std::move(copied), error);
}

CL_API_ENTRY cl_int CL_API_CALL clRetainSampler(cl_sampler value) { return retainObject(checked(value), CL_INVALID_SAMPLER); }

CL_API_ENTRY cl_int CL_API_CALL clReleaseSampler(cl_sampler value) {
  auto *sampler = checked(value);
  if (!sampler) return CL_INVALID_SAMPLER;
  if (release(sampler)) {
    auto record = sampler->record;
    auto *context = sampler->context;
    destroyObject(sampler);
    releaseAllocation(record);
    clReleaseContext(reinterpret_cast<cl_context>(context));
  }
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clGetSamplerInfo(cl_sampler value, cl_sampler_info name, size_t size, void *output, size_t *written) {
  auto *sampler = checked(value);
  if (!sampler) return CL_INVALID_SAMPLER;
  switch (name) {
    case CL_SAMPLER_REFERENCE_COUNT: return outputScalar(size, output, written, cl_uint{sampler->header.references.load()});
    case CL_SAMPLER_CONTEXT: {
      auto *const context = reinterpret_cast<cl_context>(sampler->context);
      return outputScalar(size, output, written, context);
    }
    case CL_SAMPLER_NORMALIZED_COORDS: return outputScalar(size, output, written, sampler->normalised);
    case CL_SAMPLER_ADDRESSING_MODE: return outputScalar(size, output, written, sampler->addressing);
    case CL_SAMPLER_FILTER_MODE: return outputScalar(size, output, written, sampler->filter);
    case CL_SAMPLER_PROPERTIES:
      return outputBytes(size, output, written, sampler->properties.data(), sampler->properties.size() * sizeof(cl_sampler_properties));
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clGetPipeInfo(cl_mem value, cl_pipe_info name, size_t size, void *output, size_t *written) {
  auto *mem = checked(value);
  if (!mem || !mem->pipe) return CL_INVALID_MEM_OBJECT;
  switch (name) {
    case CL_PIPE_PACKET_SIZE: return outputScalar(size, output, written, mem->pipe->first);
    case CL_PIPE_MAX_PACKETS: return outputScalar(size, output, written, mem->pipe->second);
    case CL_PIPE_PROPERTIES:
      return outputBytes(size, output, written, mem->properties.data(), mem->properties.size() * sizeof(cl_pipe_properties));
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clRetainMemObject(cl_mem value) { return retainObject(checked(value), CL_INVALID_MEM_OBJECT); }
CL_API_ENTRY cl_int CL_API_CALL clReleaseMemObject(cl_mem value) {
  auto *mem = checked(value);
  if (!mem) return CL_INVALID_MEM_OBJECT;
  if (release(mem)) {
    for (auto callback = mem->destructorCallbacks.rbegin(); callback != mem->destructorCallbacks.rend(); ++callback)
      callback->first(reinterpret_cast<cl_mem>(mem), callback->second);
    auto allocation = mem->allocation;
    auto record = mem->image ? mem->image->record : nullptr;
    auto *context = mem->context;
    auto *parent = mem->parent;
    destroyObject(mem);
    if (record) releaseAllocation(record);
    // The root buffer owns backing storage. Sub-buffers and queued commands
    // retain that object; kernel argument/relocation metadata does not own it.
    if (!allocation->runtimeOwnsBase && !parent) releaseAllocation(allocation);
    clReleaseContext(reinterpret_cast<cl_context>(context));
    if (parent) clReleaseMemObject(reinterpret_cast<cl_mem>(parent));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetMemObjectDestructorCallback(cl_mem value, void(CL_CALLBACK *callback)(cl_mem, void *), void *user) {
  auto *mem = checked(value);
  if (!mem) return CL_INVALID_MEM_OBJECT;
  if (!callback) return CL_INVALID_VALUE;
  mem->destructorCallbacks.emplace_back(callback, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_mem CL_API_CALL clCreateSubBuffer(cl_mem value, cl_mem_flags flags, cl_buffer_create_type type, const void *description,
                                                  cl_int *error) {
  auto *parent = checked(value);
  if (!parent) return fail(error, CL_INVALID_MEM_OBJECT);
  if (type != CL_BUFFER_CREATE_TYPE_REGION || !description) return fail(error, CL_INVALID_VALUE);
  const auto *region = static_cast<const cl_buffer_region *>(description);
  if (!validMemoryRange(parent, region->origin, region->size) || region->size == 0) return fail(error, CL_INVALID_BUFFER_SIZE);
  auto *result = newObject<_cl_mem>();
  if (!result) return fail(error, CL_OUT_OF_HOST_MEMORY);
  result->context = parent->context;
  result->allocation = parent->allocation;
  constexpr cl_mem_flags accessFlags = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY;
  constexpr cl_mem_flags hostAccessFlags = CL_MEM_HOST_READ_ONLY | CL_MEM_HOST_WRITE_ONLY | CL_MEM_HOST_NO_ACCESS;
  cl_mem_flags inheritedFlags = flags;
  if ((flags & accessFlags) == 0) inheritedFlags |= parent->flags & accessFlags;
  inheritedFlags |= parent->flags & (CL_MEM_ALLOC_HOST_PTR | CL_MEM_COPY_HOST_PTR | CL_MEM_USE_HOST_PTR | CL_MEM_IMMUTABLE_EXT);
  if ((flags & hostAccessFlags) == 0) inheritedFlags |= parent->flags & hostAccessFlags;
  result->flags = inheritedFlags;
  result->hostPointer = !parent->hostPointer ? nullptr : static_cast<uint8_t *>(parent->hostPointer) + region->origin;
  result->parent = parent;
  result->origin = allocationOffset(parent, region->origin);
  result->size = region->size;
  retain(parent);
  retain(parent->context);
  return succeed(error, result);
}
CL_API_ENTRY cl_int CL_API_CALL clGetMemObjectInfo(cl_mem value, cl_mem_info name, size_t size, void *output, size_t *written) {
  auto *mem = checked(value);
  if (!mem) return CL_INVALID_MEM_OBJECT;
  switch (name) {
    case CL_MEM_TYPE: {
      cl_mem_object_type v = CL_MEM_OBJECT_BUFFER;
      if (mem->image) v = mem->image->desc.image_type;
      else if (mem->pipe) v = CL_MEM_OBJECT_PIPE;
      return outputScalar(size, output, written, v);
    }
    case CL_MEM_FLAGS: return outputScalar(size, output, written, mem->flags);
    case CL_MEM_SIZE: return outputScalar(size, output, written, mem->size);
    case CL_MEM_HOST_PTR: return outputScalar(size, output, written, mem->hostPointer);
    case CL_MEM_USES_SVM_POINTER: {
      const cl_bool usesSvmPointer = (mem->flags & CL_MEM_USE_HOST_PTR) && findPointer(mem->hostPointer) ? CL_TRUE : CL_FALSE;
      return outputScalar(size, output, written, usesSvmPointer);
    }
    case CL_MEM_REFERENCE_COUNT: return outputScalar(size, output, written, cl_uint{mem->header.references.load()});
    case CL_MEM_CONTEXT: return outputScalar(size, output, written, reinterpret_cast<cl_context>(mem->context));
    case CL_MEM_ASSOCIATED_MEMOBJECT: return outputScalar(size, output, written, reinterpret_cast<cl_mem>(mem->parent));
    case CL_MEM_OFFSET: {
      const size_t offset = mem->image ? 0 : mem->origin;
      return outputScalar(size, output, written, offset);
    }
    case CL_MEM_MAP_COUNT: {
      std::scoped_lock const lock(allocationsMutex);
      cl_uint const v = static_cast<cl_uint>(mem->mappings.size());
      return outputScalar(size, output, written, v);
    }
    case CL_MEM_PROPERTIES:
      return outputBytes(size, output, written, mem->properties.data(), mem->properties.size() * sizeof(cl_mem_properties));
    default: return CL_INVALID_VALUE;
  }
}

} // extern "C"

namespace {

void unloadProgram(_cl_program *program) {
  if (program->remoteId != 0) {
    auto &state = remote;
    if (state.ready()) {
      std::scoped_lock const lock(remoteMutex);
      (void)state.client()->call(UnloadProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
    }
    program->remoteId = 0;
  }
  for (const auto &global : program->globals)
    (void)releaseAllocation(global);
  program->globals.clear();
  program->globalBytes = 0;
}

// Program-scope globals live as long as the program, so every one is bound before any launch.
cl_int bindProgramGlobals(_cl_program *program, const std::vector<GlobalSymbol> &symbols) {
  for (const auto &symbol : symbols) {
    const auto bytes = static_cast<size_t>(symbol.size.value);
    if (bytes > MaxGlobalVariableSize) {
      program->buildLog = "program-scope global " + symbol.name.value + " has " + std::to_string(bytes)
                          + " bytes, above CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE";
      return CL_INVALID_BINARY;
    }
    if (bytes == 0) continue;
    cl_int error = CL_SUCCESS;
    auto allocation = allocate(bytes, &error);
    if (!allocation) return error;
    program->globals.push_back(allocation);
    program->globalBytes += bytes;
    std::scoped_lock const lock(remoteMutex);
    if (!remote.client()->call(BindProgramGlobal{.program = ProgramId{static_cast<int64_t>(program->remoteId)},
                                                 .name = {symbol.name.value},
                                                 .allocation = AllocationId{static_cast<int64_t>(allocation->id)},
                                                 .offset = LongPositive0{0}})) {
      program->buildLog = remote.error();
      return CL_INVALID_BINARY;
    }
  }
  return CL_SUCCESS;
}

cl_int loadProgramIL(_cl_program *program) {
  if (program->remoteId != 0) return CL_SUCCESS;
  if (program->il.empty() || program->il.size() > std::numeric_limits<uint32_t>::max()) return CL_INVALID_BINARY;
  auto &state = remote;
  if (!state.ready()) {
    program->buildLog = remote.error();
    return CL_OUT_OF_RESOURCES;
  }
  std::vector<GlobalSymbol> globals;
  {
    std::scoped_lock const lock(remoteMutex);
    auto loaded = state.client()->call(LoadProgram{.encoding = ProgramEncoding::SPIRVOpenCLCompute, .image = program->il});
    if (!loaded) {
      program->buildLog = remote.error();
      return CL_INVALID_BINARY;
    }
    program->remoteId = static_cast<uint64_t>(loaded.value().value);
    auto catalogue = state.client()->call(DescribeProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
    if (!catalogue) {
      program->buildLog = remote.error();
      (void)state.client()->call(UnloadProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
      program->remoteId = 0;
      return CL_INVALID_BINARY;
    }
    program->kernelNames = catalogue.value().kernels ^ map([](const auto &kernel) { return kernel.name.value; }) ^ mk_string(";");
    globals = std::move(catalogue.value().globals);
  }
  if (const auto status = bindProgramGlobals(program, globals); status != CL_SUCCESS) {
    unloadProgram(program);
    return status;
  }
  return CL_SUCCESS;
}

} // namespace

extern "C" {

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithIL(cl_context value, const void *il, size_t length, cl_int *error) {
  auto *context = checked(value);
  if (!context || !il || length == 0 || length > std::numeric_limits<uint32_t>::max())
    return fail(error, context ? CL_INVALID_VALUE : CL_INVALID_CONTEXT);
  auto *program = newObject<_cl_program>();
  if (!program) return fail(error, CL_OUT_OF_HOST_MEMORY);
  program->context = context;
  program->il.assign(static_cast<const uint8_t *>(il), static_cast<const uint8_t *>(il) + length);
  program->originalIL = program->il;
  program->createdWithIL = true;
  retain(context);
  return succeed(error, program);
}

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithBinary(cl_context context, cl_uint count, const cl_device_id *devices,
                                                              const size_t *lengths, const unsigned char **binaries, cl_int *binaryStatus,
                                                              cl_int *error) {
  auto *contextValue = checked(context);
  if (!contextValue) return fail(error, CL_INVALID_CONTEXT);
  if (count != 1 || !devices || !validDevice(devices[0]) || !lengths || !binaries || binaries[0] == nullptr || lengths[0] == 0)
    return fail(error, CL_INVALID_VALUE);
  if (binaryStatus) binaryStatus[0] = CL_INVALID_BINARY;
  cl_program_binary_type binaryType = CL_PROGRAM_BINARY_TYPE_NONE;
  std::string source;
  std::vector<uint8_t> il;
  if (!decodeProgramBinary(binaries[0], lengths[0], &binaryType, &source, &il)) {
    auto *result = clCreateProgramWithIL(context, binaries[0], lengths[0], error);
    if (result) {
      auto *program = reinterpret_cast<_cl_program *>(result);
      program->createdWithIL = false;
      const auto loaded = loadProgramIL(program);
      if (loaded == CL_SUCCESS) {
        program->buildStatus = CL_BUILD_SUCCESS;
        program->binaryType = CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
        if (binaryStatus) binaryStatus[0] = CL_SUCCESS;
      } else {
        clReleaseProgram(result);
        result = nullptr;
        if (error) *error = loaded;
      }
    }
    return result;
  }

  auto *program = newObject<_cl_program>();
  if (!program) return fail(error, CL_OUT_OF_HOST_MEMORY);
  program->context = contextValue;
  program->source = std::move(source);
  program->buildSource = program->source;
  program->binaryType = binaryType;
  program->buildStatus = CL_BUILD_SUCCESS;
  retain(contextValue);
  if (binaryType == CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT) {
    program->bitcode = std::move(il);
    if (binaryStatus) binaryStatus[0] = CL_SUCCESS;
    return succeed(error, program);
  }
  program->il = std::move(il);
  const auto loaded = loadProgramIL(program);
  if (loaded != CL_SUCCESS) {
    destroyObject(program);
    clReleaseContext(context);
    return fail(error, loaded);
  }
  if (binaryStatus) binaryStatus[0] = CL_SUCCESS;
  return succeed(error, program);
}

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithSource(cl_context value, cl_uint count, const char **strings, const size_t *lengths,
                                                              cl_int *error) {
  auto *context = checked(value);
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  if (count == 0 || !strings) return fail(error, CL_INVALID_VALUE);
  std::string source;
  for (cl_uint index = 0; index < count; ++index) {
    if (strings[index] == nullptr) return fail(error, CL_INVALID_VALUE);
    const size_t length = !lengths || lengths[index] == 0 ? std::strlen(strings[index]) : lengths[index];
    if (length > std::numeric_limits<size_t>::max() - source.size()) return fail(error, CL_OUT_OF_HOST_MEMORY);
    source.append(strings[index], length);
  }
  auto *program = newObject<_cl_program>();
  if (!program) return fail(error, CL_OUT_OF_HOST_MEMORY);
  program->context = context;
  program->source = std::move(source);
  retain(context);
  return succeed(error, program);
}
CL_API_ENTRY cl_int CL_API_CALL clRetainProgram(cl_program value) { return retainObject(checked(value), CL_INVALID_PROGRAM); }
CL_API_ENTRY cl_int CL_API_CALL clReleaseProgram(cl_program value) {
  auto *program = checked(value);
  if (!program) return CL_INVALID_PROGRAM;
  if (release(program)) {
    unloadProgram(program);
    auto *context = program->context;
    destroyObject(program);
    clReleaseContext(reinterpret_cast<cl_context>(context));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetProgramSpecializationConstant(cl_program value, cl_uint specId, size_t specSize,
                                                                   const void *specValue) {
  auto *program = checked(value);
  if (!program || !program->createdWithIL) return CL_INVALID_PROGRAM;
  auto &state = remote;
  if (!state.ready()) return CL_OUT_OF_RESOURCES;
  if (!program->specializationSizesLoaded) {
    std::scoped_lock const lock(remoteMutex);
    auto described = state.client()->call(DescribeSpecialization{program->originalIL});
    if (!described) return CL_INVALID_PROGRAM;
    for (const auto &info : described.value())
      program->specializationSizes.emplace(static_cast<cl_uint>(info.specId.value), static_cast<size_t>(info.byteSize.value));
    program->specializationSizesLoaded = true;
  }
  const auto constant = program->specializationSizes.find(specId);
  if (constant == program->specializationSizes.end()) return CL_INVALID_SPEC_ID;
  if (!specValue || specSize != constant->second) return CL_INVALID_VALUE;
  const auto *bytes = static_cast<const uint8_t *>(specValue);
  program->specializationConstants[specId].assign(bytes, bytes + specSize);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clBuildProgram(cl_program value, cl_uint count, const cl_device_id *devices, const char *options,
                                               void(CL_CALLBACK *notify)(cl_program, void *), void *user) {
  auto *program = checked(value);
  if (!program) return CL_INVALID_PROGRAM;
  if (count != 0 && !devices) return CL_INVALID_VALUE;
  if (count > 1 || (count == 1 && !validDevice(devices[0]))) return CL_INVALID_DEVICE;
  program->buildOptions = !options ? "" : options;
  const auto buildFailed = [&] {
    program->buildStatus = CL_BUILD_ERROR;
    if (notify) notify(value, user);
    return CL_BUILD_PROGRAM_FAILURE;
  };
  if (program->createdWithIL) {
    unloadProgram(program);
    std::vector<SpecializationConstant> constants;
    constants.reserve(program->specializationConstants.size());
    for (const auto &[id, bytes] : program->specializationConstants)
      constants.push_back(SpecializationConstant{.specId = Binary32{static_cast<int64_t>(id)}, .value = bytes});
    if (constants.empty()) {
      program->il = program->originalIL;
    } else {
      auto &state = remote;
      if (!state.ready()) {
        program->buildLog = remote.error();
        return buildFailed();
      }
      std::scoped_lock const lock(remoteMutex);
      auto specialized = state.client()->call(SpecializeImage{.image = program->originalIL, .constants = constants});
      if (!specialized) {
        program->buildLog = remote.error();
        return buildFailed();
      }
      program->il = std::move(specialized.value());
    }
  }
  const std::string source = program->buildSource.empty() ? program->source : program->buildSource;
  if (!source.empty() && program->remoteId != 0) {
    unloadProgram(program);
    program->il.clear();
    program->bitcode.clear();
  }
  if (!source.empty() && program->remoteId == 0) {
    std::vector<uint8_t> compiled;
    std::vector<uint8_t> compiledBitcode;
    if (!compileSource(source, program->buildOptions, compiled, program->buildLog, &compiledBitcode)) return buildFailed();
    program->il = std::move(compiled);
    program->bitcode = std::move(compiledBitcode);
    program->buildSource = source;
    if (loadProgramIL(program) != CL_SUCCESS) return buildFailed();
  }
  if (source.empty() && !program->il.empty() && program->remoteId == 0 && loadProgramIL(program) != CL_SUCCESS) return buildFailed();
  program->buildStatus = program->remoteId == 0 ? CL_BUILD_ERROR : CL_BUILD_SUCCESS;
  if (program->buildStatus != CL_BUILD_SUCCESS) {
    if (program->buildLog.empty()) program->buildLog = remote.error();
    if (notify) notify(value, user);
    return CL_BUILD_PROGRAM_FAILURE;
  }
  program->binaryType = CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
  if (notify) notify(value, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clUnloadCompiler() { return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clUnloadPlatformCompiler(cl_platform_id value) {
  return validPlatform(value) ? CL_SUCCESS : CL_INVALID_PLATFORM;
}
CL_API_ENTRY cl_int CL_API_CALL clCompileProgram(cl_program value, cl_uint count, const cl_device_id *devices, const char *options,
                                                 cl_uint inputHeaderCount, const cl_program *inputHeaders, const char **headerNames,
                                                 void(CL_CALLBACK *notify)(cl_program, void *), void *user) {
  auto *program = checked(value);
  if (!program) return CL_INVALID_PROGRAM;
  if (count != 0 && !devices) return CL_INVALID_VALUE;
  if (count > 1 || (count == 1 && !validDevice(devices[0]))) return CL_INVALID_DEVICE;
  if (inputHeaderCount != 0 && (!inputHeaders || !headerNames)) return CL_INVALID_VALUE;
  if (inputHeaderCount != 0) {
    std::string expanded;
    if (!expandHeaders(program->source, inputHeaderCount, inputHeaders, headerNames, &expanded, &program->buildLog)) {
      program->buildStatus = CL_BUILD_ERROR;
      if (notify) notify(value, user);
      return CL_INVALID_PROGRAM;
    }
    program->buildSource = std::move(expanded);
  }
  {
    const std::string source = program->buildSource.empty() ? program->source : program->buildSource;
    std::string expanded;
    if (!expandFilesystemIncludes(source, !options ? "" : options, expanded, program->buildLog)) {
      program->buildStatus = CL_BUILD_ERROR;
      if (notify) notify(value, user);
      return CL_BUILD_PROGRAM_FAILURE;
    }
    program->buildSource = std::move(expanded);
  }
  program->buildOptions = !options ? "" : options;
  const std::string source = program->buildSource.empty() ? program->source : program->buildSource;
  const bool compilesIL = source.empty() && !program->il.empty();
  unloadProgram(program);
  program->bitcode.clear();
  program->kernelNames.clear();
  if (compilesIL) {
    program->buildStatus = CL_BUILD_SUCCESS;
    program->binaryType = CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT;
    if (notify) notify(value, user);
    return CL_SUCCESS;
  }
  program->il.clear();
  if (!compileSourceToBitcode(source, program->buildOptions, program->bitcode, program->buildLog)) {
    program->buildStatus = CL_BUILD_ERROR;
    program->binaryType = CL_PROGRAM_BINARY_TYPE_NONE;
    if (notify) notify(value, user);
    return CL_BUILD_PROGRAM_FAILURE;
  }
  program->buildStatus = CL_BUILD_SUCCESS;
  program->binaryType = CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT;
  if (notify) notify(value, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_program CL_API_CALL clLinkProgram(cl_context contextValue, cl_uint count, const cl_device_id *devices, const char *options,
                                                  cl_uint inputCount, const cl_program *inputs,
                                                  void(CL_CALLBACK *notify)(cl_program, void *), void *user, cl_int *error) {
  auto *context = checked(contextValue);
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  if (count != 0 && !devices) return fail(error, CL_INVALID_VALUE);
  if (count > 1 || (count == 1 && !validDevice(devices[0]))) return fail(error, CL_INVALID_DEVICE);
  if (inputCount == 0 || !inputs) return fail(error, CL_INVALID_VALUE);
  std::string source;
  std::string buildOptions;
  std::vector<std::vector<uint8_t>> bitcodeModules;
  bool allSourceBitcode = true;
  _cl_program *binaryInput = nullptr;
  std::vector<_cl_program *> binaryInputs;
  for (cl_uint index = 0; index < inputCount; ++index) {
    auto *program = checked(inputs[index]);
    if (!program) return fail(error, CL_INVALID_PROGRAM);
    if (program->context != context || program->buildStatus != CL_BUILD_SUCCESS) return fail(error, CL_INVALID_PROGRAM);
    if (program->source.empty()) {
      if (program->il.empty() || !source.empty() || !bitcodeModules.empty()) return fail(error, CL_INVALID_PROGRAM);
      binaryInputs.push_back(program);
      continue;
    }
    if (!source.empty()) source.push_back('\n');
    source += program->buildSource.empty() ? program->source : program->buildSource;
    if (program->bitcode.empty()) allSourceBitcode = false;
    else bitcodeModules.push_back(program->bitcode);
    if (!program->buildOptions.empty()) {
      if (!buildOptions.empty()) buildOptions.push_back(' ');
      buildOptions += program->buildOptions;
    }
  }
  if (binaryInputs.size() == 1) binaryInput = binaryInputs.front();
  const cl_program_binary_type linkedType =
      hasBuildOption(options, "-create-library") ? CL_PROGRAM_BINARY_TYPE_LIBRARY : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
  const auto linkModules = [&](const std::vector<std::vector<uint8_t>> &modules, std::string linkLog,
                               std::string linkedSource) -> cl_program {
    std::vector<uint8_t> linkedIL;
    std::vector<uint8_t> linkedBitcode;
    if (!linkBitcode(modules, linkedIL, linkedBitcode, linkLog)) return fail(error, CL_BUILD_PROGRAM_FAILURE);
    cl_int linkError = CL_SUCCESS;
    auto *linked = reinterpret_cast<_cl_program *>(clCreateProgramWithIL(contextValue, linkedIL.data(), linkedIL.size(), &linkError));
    if (!linked) return fail(error, linkError == CL_SUCCESS ? CL_BUILD_PROGRAM_FAILURE : linkError);
    linked->source = std::move(linkedSource);
    linked->buildSource = linked->source;
    linked->bitcode = std::move(linkedBitcode);
    linked->buildStatus = CL_BUILD_SUCCESS;
    linked->binaryType = linkedType;
    linked->buildOptions = std::move(buildOptions);
    linked->buildLog = std::move(linkLog);
    if (linked->binaryType == CL_PROGRAM_BINARY_TYPE_EXECUTABLE && loadProgramIL(linked) != CL_SUCCESS) {
      clReleaseProgram(reinterpret_cast<cl_program>(linked));
      return fail(error, CL_BUILD_PROGRAM_FAILURE);
    }
    if (notify) notify(reinterpret_cast<cl_program>(linked), user);
    return succeed(error, linked);
  };
  if (binaryInputs.size() > 1) {
    std::vector<std::vector<uint8_t>> binaryBitcode;
    binaryBitcode.reserve(binaryInputs.size());
    std::string linkLog;
    for (const auto *program : binaryInputs) {
      std::vector<uint8_t> bitcode;
      if (!reverseSpirv(program->il, bitcode, linkLog)) return fail(error, CL_BUILD_PROGRAM_FAILURE);
      binaryBitcode.push_back(std::move(bitcode));
    }
    return linkModules(binaryBitcode, std::move(linkLog), {});
  }
  if (!binaryInput && allSourceBitcode) return linkModules(bitcodeModules, {}, std::move(source));
  if (binaryInput) {
    auto *linked = newObject<_cl_program>();
    if (!linked) return fail(error, CL_OUT_OF_HOST_MEMORY);
    linked->context = context;
    linked->source = binaryInput->source;
    linked->buildSource = binaryInput->buildSource;
    linked->il = binaryInput->il;
    linked->buildStatus = CL_BUILD_SUCCESS;
    linked->binaryType = linkedType;
    retain(context);
    const auto loaded = linked->binaryType == CL_PROGRAM_BINARY_TYPE_EXECUTABLE ? loadProgramIL(linked) : CL_SUCCESS;
    if (loaded != CL_SUCCESS) {
      destroyObject(linked);
      clReleaseContext(context);
      return fail(error, loaded);
    }
    if (notify) notify(reinterpret_cast<cl_program>(linked), user);
    return succeed(error, linked);
  }
  if (options && options[0] != '\0') {
    if (!buildOptions.empty()) buildOptions.push_back(' ');
    buildOptions += withoutLibraryOption(options);
  }
  const char *sourcePointer = source.c_str();
  cl_program result = clCreateProgramWithSource(contextValue, 1, &sourcePointer, nullptr, error);
  if (!result) return nullptr;
  const cl_int build = clBuildProgram(result, count, devices, buildOptions.c_str(), nullptr, nullptr);
  if (build != CL_SUCCESS) {
    clReleaseProgram(result);
    return fail(error, build);
  }
  auto *linked = reinterpret_cast<_cl_program *>(result);
  linked->binaryType = linkedType;
  if (notify) notify(result, user);
  if (error) *error = CL_SUCCESS;
  return result;
}

CL_API_ENTRY cl_int CL_API_CALL clGetSupportedImageFormats(cl_context value, cl_mem_flags flags, cl_mem_object_type type,
                                                           cl_uint entryCount, cl_image_format *imageFormats, cl_uint *imageFormatCount) {
  if (!checked(value)) return CL_INVALID_CONTEXT;
  if (imageFormats && entryCount == 0) return CL_INVALID_VALUE;
  if (!imageType(type)) return CL_INVALID_VALUE;
  const bool writes = (flags & (CL_MEM_WRITE_ONLY | CL_MEM_READ_WRITE | CL_MEM_KERNEL_READ_AND_WRITE)) != 0;
  cl_uint count = 0;
  for (const auto &info : ImageFormats) {
    if ((writes && !info.writable) || !formatSupports(info, type)) continue;
    if (imageFormats && count < entryCount) imageFormats[count] = {.image_channel_order = info.order, .image_channel_data_type = info.type};
    ++count;
  }
  if (imageFormatCount) *imageFormatCount = count;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clGetProgramInfo(cl_program value, cl_program_info name, size_t size, void *output, size_t *written) {
  auto *program = checked(value);
  if (!program) return CL_INVALID_PROGRAM;
  switch (name) {
    case CL_PROGRAM_REFERENCE_COUNT: return outputScalar(size, output, written, cl_uint{program->header.references.load()});
    case CL_PROGRAM_CONTEXT: return outputScalar(size, output, written, reinterpret_cast<cl_context>(program->context));
    case CL_PROGRAM_NUM_DEVICES: return outputScalar(size, output, written, cl_uint{1});
    case CL_PROGRAM_DEVICES: return outputScalar(size, output, written, reinterpret_cast<cl_device_id>(&device));
    case CL_PROGRAM_SOURCE: return outputBytes(size, output, written, program->source.c_str(), program->source.size() + 1);
    case CL_PROGRAM_NUM_KERNELS: {
      if (program->buildStatus != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
      const size_t v = kernelCount(program->kernelNames);
      return outputScalar(size, output, written, v);
    }
    case CL_PROGRAM_KERNEL_NAMES:
      if (program->buildStatus != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
      return outputString(size, output, written, program->kernelNames);
    case CL_PROGRAM_IL: {
      const auto &il = program->createdWithIL ? program->originalIL : program->il;
      return outputBytes(size, output, written, il.data(), il.size());
    }
    case CL_PROGRAM_BINARY_SIZES: {
      const auto binary = programBinary(program);
      return outputScalar(size, output, written, binary.size());
    }
    case CL_PROGRAM_BINARIES:
      if (written) *written = sizeof(unsigned char *);
      if (!output) return CL_SUCCESS;
      if (size < sizeof(unsigned char *)) return CL_INVALID_VALUE;
      {
        const auto binary = programBinary(program);
        auto *destination = *static_cast<unsigned char **>(output);
        if (!destination && !binary.empty()) return CL_INVALID_VALUE;
        if (!binary.empty()) std::memcpy(destination, binary.data(), binary.size());
      }
      return CL_SUCCESS;
    case CL_PROGRAM_SCOPE_GLOBAL_CTORS_PRESENT:
    case CL_PROGRAM_SCOPE_GLOBAL_DTORS_PRESENT: return outputScalar(size, output, written, cl_bool{CL_FALSE});
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetProgramBuildInfo(cl_program value, cl_device_id selected, cl_program_build_info name, size_t size,
                                                      void *output, size_t *written) {
  auto *program = checked(value);
  if (!program || !validDevice(selected)) return CL_INVALID_PROGRAM;
  switch (name) {
    case CL_PROGRAM_BUILD_STATUS: return outputScalar(size, output, written, program->buildStatus);
    case CL_PROGRAM_BUILD_OPTIONS: return outputString(size, output, written, program->buildOptions);
    case CL_PROGRAM_BUILD_LOG: return outputString(size, output, written, program->buildLog);
    case CL_PROGRAM_BINARY_TYPE: return outputScalar(size, output, written, program->binaryType);
    case CL_PROGRAM_BUILD_GLOBAL_VARIABLE_TOTAL_SIZE: return outputScalar(size, output, written, program->globalBytes);
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clSetProgramReleaseCallback(cl_program value, void(CL_CALLBACK *)(cl_program, void *), void *) {
  return checked(value) == nullptr ? CL_INVALID_PROGRAM : CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_kernel CL_API_CALL clCreateKernel(cl_program value, const char *name, cl_int *error) {
  auto *program = checked(value);
  if (!program || !name || name[0] == '\0') return fail(error, program ? CL_INVALID_KERNEL_NAME : CL_INVALID_PROGRAM);
  if (program->buildStatus != CL_BUILD_SUCCESS) return fail(error, CL_INVALID_PROGRAM_EXECUTABLE);
  if (!containsKernelName(program->kernelNames, name)) return fail(error, CL_INVALID_KERNEL_NAME);
  auto *kernel = newObject<_cl_kernel>();
  if (!kernel) return fail(error, CL_OUT_OF_HOST_MEMORY);
  kernel->program = program;
  kernel->name = name;
  const auto kernelSource = sourceKernelText(program->source, kernel->name);
  auto &state = remote;
  if (!state.ready()) {
    destroyObject(kernel);
    return fail(error, CL_INVALID_KERNEL_DEFINITION);
  }
  auto described = state.client()->call(DescribeKernel{.program = ProgramId{static_cast<int64_t>(program->remoteId)}, .name = {name}});
  if (!described) {
    destroyObject(kernel);
    return fail(error, CL_INVALID_KERNEL_DEFINITION);
  }
  const auto &remoteMetadata = described.value();
  kernel->expectedArguments = static_cast<cl_uint>(remoteMetadata.parameters.size());
  kernel->requiredWorkGroupSize = remoteMetadata.requiredWorkGroupSize
                                      ? std::array<size_t, 3>{static_cast<size_t>(remoteMetadata.requiredWorkGroupSize->x.value),
                                                              static_cast<size_t>(remoteMetadata.requiredWorkGroupSize->y.value),
                                                              static_cast<size_t>(remoteMetadata.requiredWorkGroupSize->z.value)}
                                      : std::array<size_t, 3>{0, 0, 0};
  kernel->staticLocalMemory = sourceStaticLocalMemory(kernelSource);
  kernel->attributes = sourceKernelAttributes(kernelSource);
  kernel->expectedArgumentSizes.reserve(remoteMetadata.parameters.size());
  kernel->parameterMetadata.reserve(remoteMetadata.parameters.size());
  for (const auto &parameter : remoteMetadata.parameters) {
    kernel->expectedArgumentSizes.push_back(static_cast<size_t>(parameter.width.value));
    const bool directPointer = parameter.kind == ParameterKind::Allocation;
    kernel->parameterMetadata.push_back({.pointerAggregate = parameter.kind == ParameterKind::Scalar && parameter.containsPointers,
                                         .local = parameter.kind == ParameterKind::Local,
                                         .containsPointer = directPointer || parameter.containsPointers});
  }
  kernel->argumentInfo = sourceKernelArgumentInfo(kernelSource, kernel->name);
  kernel->arguments.resize(kernel->expectedArguments);
  kernel->argumentsSet.resize(kernel->expectedArguments, false);
  retain(program);
  return succeed(error, kernel);
}
CL_API_ENTRY cl_int CL_API_CALL clCreateKernelsInProgram(cl_program value, cl_uint kernelCapacity, cl_kernel *kernels,
                                                         cl_uint *kernelCountOutput) {
  auto *program = checked(value);
  if (!program) return CL_INVALID_PROGRAM;
  if (program->buildStatus != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
  const size_t total = kernelCount(program->kernelNames);
  if (kernelCountOutput) *kernelCountOutput = static_cast<cl_uint>(total);
  if (kernelCapacity == 0 && !kernels) return CL_SUCCESS;
  if (total != 0 && (!kernels || kernelCapacity < total)) return CL_INVALID_VALUE;
  if (!kernels && kernelCapacity != 0) return CL_INVALID_VALUE;
  size_t start = 0;
  for (size_t index = 0; index < total; ++index) {
    const size_t end = program->kernelNames.find(';', start);
    const std::string name = program->kernelNames.substr(start, end == std::string::npos ? end : end - start);
    cl_int error = CL_SUCCESS;
    kernels[index] = clCreateKernel(value, name.c_str(), &error);
    if (kernels[index] == nullptr || error != CL_SUCCESS) {
      for (size_t created = 0; created < index; ++created)
        clReleaseKernel(kernels[created]);
      return error == CL_SUCCESS ? CL_OUT_OF_HOST_MEMORY : error;
    }
    if (end == std::string::npos) break;
    start = end + 1;
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_kernel CL_API_CALL clCloneKernel(cl_kernel value, cl_int *error) {
  auto *source = checked(value);
  if (!source) return fail(error, CL_INVALID_KERNEL);
  auto *clone = newObject<_cl_kernel>();
  if (!clone) return fail(error, CL_OUT_OF_HOST_MEMORY);
  clone->program = source->program;
  clone->name = source->name;
  clone->expectedArguments = source->expectedArguments;
  clone->requiredWorkGroupSize = source->requiredWorkGroupSize;
  clone->staticLocalMemory = source->staticLocalMemory;
  clone->attributes = source->attributes;
  clone->expectedArgumentSizes = source->expectedArgumentSizes;
  clone->parameterMetadata = source->parameterMetadata;
  clone->argumentInfo = source->argumentInfo;
  clone->arguments = source->arguments;
  clone->argumentsSet = source->argumentsSet;
  clone->indirectAllocations = source->indirectAllocations;
  clone->indirectTypes = source->indirectTypes;
  clone->indirectSystem = source->indirectSystem;
  retain(clone->program);
  return succeed(error, clone);
}
CL_API_ENTRY cl_int CL_API_CALL clRetainKernel(cl_kernel value) { return retainObject(checked(value), CL_INVALID_KERNEL); }
CL_API_ENTRY cl_int CL_API_CALL clReleaseKernel(cl_kernel value) {
  auto *kernel = checked(value);
  if (!kernel) return CL_INVALID_KERNEL;
  if (release(kernel)) {
    auto *program = kernel->program;
    destroyObject(kernel);
    clReleaseProgram(reinterpret_cast<cl_program>(program));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArg(cl_kernel value, cl_uint index, size_t size, const void *argument) {
  auto *kernel = checked(value);
  if (!kernel) return CL_INVALID_KERNEL;
  if (index >= kernel->expectedArguments) return CL_INVALID_ARG_INDEX;
  if (size == 0) return CL_INVALID_ARG_SIZE;
  if (gemstone::traceEnabled("perf"))
    std::fprintf(stderr, "Gemstone OpenCL argument kernel=%s index=%u size=%zu expected=%zu local=%d aggregate=%d null=%d\n",
                 kernel->name.c_str(), index, size, index < kernel->expectedArgumentSizes.size() ? kernel->expectedArgumentSizes[index] : 0,
                 index < kernel->parameterMetadata.size() && kernel->parameterMetadata[index].local,
                 index < kernel->parameterMetadata.size() && kernel->parameterMetadata[index].pointerAggregate, !argument);
  auto &output = kernel->arguments[index];
  output.range.reset();
  output.texels.reset();
  output.texelOffset = 0;
  output.sampler = nullptr;
  output.systemPointer = false;
  const bool pointerAggregate = index < kernel->parameterMetadata.size() && kernel->parameterMetadata[index].pointerAggregate;
  const bool localArgument = index < kernel->parameterMetadata.size() && kernel->parameterMetadata[index].local;
  const auto *declared = index < kernel->argumentInfo.size() ? &kernel->argumentInfo[index] : nullptr;
  if (declared && declared->typeName == "sampler_t") {
    cl_sampler handle = nullptr;
    if (size != sizeof(handle)) return CL_INVALID_ARG_SIZE;
    if (argument) std::memcpy(&handle, argument, sizeof(handle));
    if (!checked(handle)) return CL_INVALID_SAMPLER;
  }
  if (declared && imageTypeName(declared->typeName)) {
    cl_mem handle = nullptr;
    if (size != sizeof(handle)) return CL_INVALID_ARG_SIZE;
    if (argument) std::memcpy(&handle, argument, sizeof(handle));
    const auto *image = checked(handle);
    if (!image || !image->image) return CL_INVALID_MEM_OBJECT;
    const bool readOnly = (image->flags & CL_MEM_READ_ONLY) != 0;
    const bool writeOnly = (image->flags & CL_MEM_WRITE_ONLY) != 0;
    if ((declared->access == CL_KERNEL_ARG_ACCESS_READ_ONLY && writeOnly)
        || (declared->access == CL_KERNEL_ARG_ACCESS_WRITE_ONLY && readOnly)
        || (declared->access == CL_KERNEL_ARG_ACCESS_READ_WRITE && (readOnly || writeOnly)))
      return CL_INVALID_ARG_VALUE;
  }
  if (declared && declared->typeName == "queue_t") {
    cl_command_queue handle = nullptr;
    if (size != sizeof(handle)) return CL_INVALID_ARG_SIZE;
    if (argument) std::memcpy(&handle, argument, sizeof(handle));
    const auto *queue = anyQueue(handle);
    if (!queue || queue->deviceSize == 0) return CL_INVALID_DEVICE_QUEUE;
  }
  if (!argument && size == sizeof(cl_mem) && !localArgument) {
    output.kind = ::KernelArgument::Kind::Scalar;
    output.allocation.reset();
    output.offset = 0;
    output.bytes.assign(sizeof(cl_mem), 0);
    output.memory = nullptr;
    kernel->argumentsSet[index] = true;
    return CL_SUCCESS;
  }
  if (!argument) {
    if (!localArgument) return CL_INVALID_ARG_VALUE;
    output.kind = ::KernelArgument::Kind::Local;
    output.bytes.clear();
    output.allocation.reset();
    output.offset = size;
    output.memory = nullptr;
    kernel->argumentsSet[index] = true;
    return CL_SUCCESS;
  }
  if (size >= sizeof(cl_mem)) {
    cl_mem handle = nullptr;
    std::memcpy(&handle, argument, sizeof(handle));
    auto *mem = checked(handle);
    if (mem) {
      if (size != sizeof(cl_mem)) return CL_INVALID_ARG_SIZE;
      if (immutable(mem) && !mem->image && declared && declared->address != CL_KERNEL_ARG_ADDRESS_CONSTANT
          && (declared->type & CL_KERNEL_ARG_TYPE_CONST) == 0)
        return CL_INVALID_ARG_VALUE;
      output.kind = ::KernelArgument::Kind::Allocation;
      output.allocation = mem->image ? mem->image->record : mem->allocation;
      output.offset = mem->image ? 0 : mem->origin;
      if (mem->image) {
        output.range = ImageRecordBytes;
        output.texels = mem->allocation;
        output.texelOffset = mem->origin;
      }
      if (mem->pipe) output.range = mem->size;
      output.bytes.clear();
      output.memory = mem;
      kernel->argumentsSet[index] = true;
      return CL_SUCCESS;
    }
    auto *sampler = checked(reinterpret_cast<cl_sampler>(handle));
    if (sampler) {
      if (size != sizeof(cl_sampler)) return CL_INVALID_ARG_SIZE;
      output.kind = ::KernelArgument::Kind::Allocation;
      output.allocation = sampler->record;
      output.offset = 0;
      output.range = SamplerRecordBytes;
      output.bytes.clear();
      output.memory = nullptr;
      output.sampler = sampler;
      kernel->argumentsSet[index] = true;
      return CL_SUCCESS;
    }
  }
  if (size == sizeof(uintptr_t) && !pointerAggregate) {
    uintptr_t pointer = 0;
    std::memcpy(&pointer, argument, sizeof(pointer));
    size_t pointerOffset = 0;
    auto allocation = findPointer(reinterpret_cast<const void *>(pointer), &pointerOffset);
    if (allocation) {
      output.kind = ::KernelArgument::Kind::Allocation;
      output.allocation = std::move(allocation);
      output.offset = pointerOffset;
      output.bytes.clear();
      output.memory = nullptr;
      kernel->argumentsSet[index] = true;
      return CL_SUCCESS;
    }
  }
  const size_t expected = index < kernel->expectedArgumentSizes.size() ? kernel->expectedArgumentSizes[index] : 0;
  const bool scalarArgument = index < kernel->parameterMetadata.size() && !kernel->parameterMetadata[index].containsPointer;
  if (expected != 0 && !pointerAggregate && (size < expected || (size != expected && !scalarArgument))) return CL_INVALID_ARG_SIZE;
  // Match the Intel OpenCL scalar ABI: callers may supply a wider host slot,
  // and the driver consumes only the declared scalar width. oneMKL uses this
  // for 64-bit host slots carrying 32-bit SPIR-V offsets. Pointer arguments
  // still require their exact width; pointer-bearing aggregates retain their
  // host representation for relocation by the execution service.
  const size_t copiedSize = scalarArgument && expected != 0 ? expected : size;
  output.kind = ::KernelArgument::Kind::Scalar;
  output.allocation.reset();
  output.offset = 0;
  output.bytes.assign(static_cast<const uint8_t *>(argument), static_cast<const uint8_t *>(argument) + copiedSize);
  output.memory = nullptr;
  kernel->argumentsSet[index] = true;
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArgSVMPointer(cl_kernel value, cl_uint index, const void *argument) {
  auto *kernel = checked(value);
  if (!kernel) return CL_INVALID_KERNEL;
  if (index >= kernel->expectedArguments) return CL_INVALID_ARG_INDEX;
  if (!argument) return clSetKernelArg(value, index, sizeof(void *), nullptr);
  size_t offset = 0;
  auto allocation = findPointer(argument, &offset);
  auto &output = kernel->arguments[index];
  output.systemPointer = !allocation;
  output.kind = allocation ? ::KernelArgument::Kind::SVM : ::KernelArgument::Kind::Scalar;
  output.allocation = std::move(allocation);
  output.offset = offset;
  output.bytes.resize(sizeof(uint64_t));
  const uint64_t pointer = reinterpret_cast<uintptr_t>(argument);
  std::memcpy(output.bytes.data(), &pointer, sizeof(pointer));
  output.memory = nullptr;
  kernel->argumentsSet[index] = true;
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelExecInfo(cl_kernel value, cl_kernel_exec_info name, size_t size, const void *valueData) {
  auto *kernel = checked(value);
  if (!kernel) return CL_INVALID_KERNEL;
  if ((name == CL_KERNEL_EXEC_INFO_SVM_PTRS || name == CL_KERNEL_EXEC_INFO_USM_PTRS_INTEL) && size == 0 && !valueData) {
    kernel->indirectAllocations.erase(name);
    kernel->indirectSystem = false;
    return CL_SUCCESS;
  }
  if (!valueData) return CL_INVALID_VALUE;
  if (name == CL_KERNEL_EXEC_INFO_SVM_PTRS || name == CL_KERNEL_EXEC_INFO_USM_PTRS_INTEL) {
    if (size == 0 || size % sizeof(void *) != 0) return CL_INVALID_VALUE;
    std::vector<std::shared_ptr<Allocation>> values;
    bool system = false;
    for (size_t index = 0; index < size / sizeof(void *); ++index) {
      const auto *const pointer = gemstone::readNative<const void *>(valueData, index * sizeof(void *));
      if (!pointer) return CL_INVALID_VALUE;
      size_t offset = 0;
      auto allocation = findPointer(pointer, &offset);
      if (!allocation) {
        system = true;
        continue;
      }
      if (allocation->svmContextIdentity != kernel->program->context->identity || offset >= allocation->size) return CL_INVALID_VALUE;
      values.push_back(std::move(allocation));
    }
    kernel->indirectAllocations[name] = std::move(values);
    kernel->indirectSystem = system;
    return CL_SUCCESS;
  }
  if (name == CL_KERNEL_EXEC_INFO_INDIRECT_HOST_ACCESS_INTEL || name == CL_KERNEL_EXEC_INFO_INDIRECT_DEVICE_ACCESS_INTEL
      || name == CL_KERNEL_EXEC_INFO_INDIRECT_SHARED_ACCESS_INTEL || name == CL_KERNEL_EXEC_INFO_SVM_FINE_GRAIN_SYSTEM) {
    if (size != sizeof(cl_bool)) return CL_INVALID_VALUE;
    const auto enabled = gemstone::readNative<cl_bool>(valueData);
    if (enabled != CL_TRUE && enabled != CL_FALSE) return CL_INVALID_VALUE;
    if (enabled) kernel->indirectTypes.insert(name);
    else kernel->indirectTypes.erase(name);
    return CL_SUCCESS;
  }
  return CL_INVALID_VALUE;
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelInfo(cl_kernel value, cl_kernel_info name, size_t size, void *output, size_t *written) {
  auto *kernel = checked(value);
  if (!kernel) return CL_INVALID_KERNEL;
  switch (name) {
    case CL_KERNEL_FUNCTION_NAME: return outputString(size, output, written, kernel->name);
    case CL_KERNEL_NUM_ARGS: return outputScalar(size, output, written, static_cast<cl_uint>(kernel->arguments.size()));
    case CL_KERNEL_REFERENCE_COUNT: return outputScalar(size, output, written, cl_uint{kernel->header.references.load()});
    case CL_KERNEL_CONTEXT: return outputScalar(size, output, written, reinterpret_cast<cl_context>(kernel->program->context));
    case CL_KERNEL_PROGRAM: return outputScalar(size, output, written, reinterpret_cast<cl_program>(kernel->program));
    case CL_KERNEL_ATTRIBUTES: return outputString(size, output, written, kernel->attributes);
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelArgInfo(cl_kernel value, cl_uint index, cl_kernel_arg_info name, size_t size, void *output,
                                                   size_t *written) {
  auto *kernel = checked(value);
  if (!kernel) return CL_INVALID_KERNEL;
  if (index >= kernel->arguments.size()) return CL_INVALID_ARG_INDEX;
  if (index >= kernel->argumentInfo.size()) return CL_KERNEL_ARG_INFO_NOT_AVAILABLE;
  const auto &info = kernel->argumentInfo[index];
  switch (name) {
    case CL_KERNEL_ARG_ADDRESS_QUALIFIER: return outputScalar(size, output, written, info.address);
    case CL_KERNEL_ARG_ACCESS_QUALIFIER: return outputScalar(size, output, written, info.access);
    case CL_KERNEL_ARG_TYPE_QUALIFIER: return outputScalar(size, output, written, info.type);
    case CL_KERNEL_ARG_TYPE_NAME: return outputString(size, output, written, info.typeName);
    case CL_KERNEL_ARG_NAME: return outputString(size, output, written, info.name);
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelWorkGroupInfo(cl_kernel value, cl_device_id selected, cl_kernel_work_group_info name,
                                                         size_t size, void *output, size_t *written) {
  auto *kernel = checked(value);
  if (!kernel) return CL_INVALID_KERNEL;
  // OpenCL permits a null device when the kernel's program has one device.
  if (selected && !validDevice(selected)) return CL_INVALID_DEVICE;
  switch (name) {
    case CL_KERNEL_WORK_GROUP_SIZE: return outputScalar(size, output, written, MaxWorkGroupSize);
    case CL_KERNEL_COMPILE_WORK_GROUP_SIZE:
      return outputBytes(size, output, written, kernel->requiredWorkGroupSize.data(), sizeof(kernel->requiredWorkGroupSize));
    case CL_KERNEL_LOCAL_MEM_SIZE: {
      cl_ulong v = static_cast<cl_ulong>(kernel->staticLocalMemory);
      for (const auto &argument : kernel->arguments)
        if (argument.kind == ::KernelArgument::Kind::Local) v += static_cast<cl_ulong>(argument.offset);
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE: return outputScalar(size, output, written, size_t{32});
    case CL_KERNEL_PRIVATE_MEM_SIZE: return outputScalar(size, output, written, cl_ulong{0});
    case CL_KERNEL_GLOBAL_WORK_SIZE: {
      size_t v[3] = {0, 0, 0};
      return outputBytes(size, output, written, v, sizeof(v));
    }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clWaitForEvents(cl_uint count, const cl_event *events) { return waitForEvents(count, events); }
CL_API_ENTRY cl_event CL_API_CALL clCreateUserEvent(cl_context value, cl_int *error) {
  auto *context = checked(value);
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  auto *event = newObject<_cl_event>();
  if (!event) return fail(error, CL_OUT_OF_HOST_MEMORY);
  event->context = context;
  event->command = CL_COMMAND_USER;
  event->status = CL_SUBMITTED;
  event->started = nowNanos();
  try {
    event->ticket = gemstone::CommandQueue::pending();
  } catch (const std::bad_alloc &) {
    destroyObject(event);
    return fail(error, CL_OUT_OF_HOST_MEMORY);
  }
  retain(context);
  return succeed(error, event);
}
CL_API_ENTRY cl_int CL_API_CALL clSetUserEventStatus(cl_event value, cl_int status) {
  auto *event = checked(value);
  if (!event) return CL_INVALID_EVENT;
  if (event->command != CL_COMMAND_USER) return CL_INVALID_OPERATION;
  if (status != CL_COMPLETE && status >= 0) return CL_INVALID_VALUE;
  {
    std::scoped_lock const lock(eventsMutex);
    if (event->status != CL_SUBMITTED) return CL_INVALID_OPERATION;
    event->status = status;
    event->ended = nowNanos();
  }
  scheduler().complete(event->ticket, 0);
  eventsCondition.notify_all();
  notifyEventCallbacks(event);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetEventCallback(cl_event value, cl_int trigger, void(CL_CALLBACK *callback)(cl_event, cl_int, void *),
                                                   void *user) {
  auto *event = checked(value);
  if (!event) return CL_INVALID_EVENT;
  if (!callback || (trigger != CL_SUBMITTED && trigger != CL_RUNNING && trigger != CL_COMPLETE)) return CL_INVALID_VALUE;
  bool ready = false;
  cl_int status = CL_COMPLETE;
  {
    std::scoped_lock const lock(eventsMutex);
    status = event->status;
    ready = status < 0 || status <= trigger;
    if (!ready) event->callbacks.push_back({.trigger = trigger, .function = callback, .user = user});
  }
  if (ready) callback(value, status, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clGetEventInfo(cl_event value, cl_event_info name, size_t size, void *output, size_t *written) {
  auto *event = checked(value);
  if (!event) return CL_INVALID_EVENT;
  switch (name) {
    case CL_EVENT_COMMAND_QUEUE: return outputScalar(size, output, written, reinterpret_cast<cl_command_queue>(event->queue));
    case CL_EVENT_COMMAND_TYPE: return outputScalar(size, output, written, event->command);
    case CL_EVENT_REFERENCE_COUNT: return outputScalar(size, output, written, cl_uint{event->header.references.load()});
    case CL_EVENT_COMMAND_EXECUTION_STATUS: {
      std::scoped_lock const lock(eventsMutex);
      return outputScalar(size, output, written, event->status);
    }
    case CL_EVENT_CONTEXT: return outputScalar(size, output, written, reinterpret_cast<cl_context>(event->context));
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clRetainEvent(cl_event value) { return retainObject(checked(value), CL_INVALID_EVENT); }
CL_API_ENTRY cl_int CL_API_CALL clReleaseEvent(cl_event value) {
  auto *event = checked(value);
  if (!event) return CL_INVALID_EVENT;
  if (release(event)) {
    auto *queue = event->queue;
    auto *context = event->context;
    destroyObject(event);
    if (queue) clReleaseCommandQueue(reinterpret_cast<cl_command_queue>(queue));
    if (context) clReleaseContext(reinterpret_cast<cl_context>(context));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clGetEventProfilingInfo(cl_event value, cl_profiling_info name, size_t size, void *output,
                                                        size_t *written) {
  auto *event = checked(value);
  if (!event) return CL_INVALID_EVENT;
  if (!event->queue || (event->queue->properties & CL_QUEUE_PROFILING_ENABLE) == 0) return CL_PROFILING_INFO_NOT_AVAILABLE;
  if (name == CL_PROFILING_COMMAND_QUEUED) return outputScalar(size, output, written, event->queued);
  if (name == CL_PROFILING_COMMAND_SUBMIT) return outputScalar(size, output, written, event->submitted);
  if (name == CL_PROFILING_COMMAND_START) return outputScalar(size, output, written, event->started);
  // Child kernels run within their parent's command, so a command completes when it ends.
  if (name == CL_PROFILING_COMMAND_END || name == CL_PROFILING_COMMAND_COMPLETE) return outputScalar(size, output, written, event->ended);
  return CL_INVALID_VALUE;
}
CL_API_ENTRY cl_int CL_API_CALL clFlush(cl_command_queue value) { return checked(value) ? CL_SUCCESS : CL_INVALID_COMMAND_QUEUE; }
CL_API_ENTRY cl_int CL_API_CALL clFinish(cl_command_queue value) {
  auto *queue = checked(value);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  scheduler().waitCovered(scheduler().snapshot(queue->stream));
  std::scoped_lock const lock(eventsMutex);
  return std::exchange(queue->unobservedFailures, size_t{0}) != 0 ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS;
}

} // extern "C"

namespace {

template <class Host>
cl_int enqueueBufferTransfer(_cl_command_queue *queue, _cl_mem *mem, cl_bool blocking, size_t offset, size_t size, Host *pointer,
                             cl_uint count, const cl_event *waits, cl_event *event) {
  constexpr bool reading = !std::is_const_v<Host>;
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (mem && (mem->image || mem->pipe)) return CL_INVALID_MEM_OBJECT;
  if (!mem || !pointer || !validMemoryRange(mem, offset, size)) return CL_INVALID_VALUE;
  if (mem->context != queue->context) return CL_INVALID_CONTEXT;
  if (reading ? !hostReadAllowed(mem) : !hostWriteAllowed(mem)) return CL_INVALID_OPERATION;
  const auto type = reading ? CL_COMMAND_READ_BUFFER : CL_COMMAND_WRITE_BUFFER;
  auto execute = [=] {
    if constexpr (reading) return readAllocation(mem->allocation, allocationOffset(mem, offset), pointer, size);
    else return writeAllocation(mem->allocation, allocationOffset(mem, offset), pointer, size);
  };
  try {
    auto retained = retainMemory(mem);
    return submitCommand(
        queue, type, blocking, count, waits,
        [execute, retained] {
          (void)retained;
          return execute();
        },
        event);
  } catch (...) {
    return CL_OUT_OF_HOST_MEMORY;
  }
}

} // namespace

extern "C" {

CL_API_ENTRY cl_int CL_API_CALL clEnqueueReadBuffer(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking, size_t offset,
                                                    size_t size, void *output, cl_uint count, const cl_event *waits, cl_event *event) {
  return enqueueBufferTransfer(checked(queueValue), checked(memoryValue), blocking, offset, size, output, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueWriteBuffer(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking, size_t offset,
                                                     size_t size, const void *input, cl_uint count, const cl_event *waits,
                                                     cl_event *event) {
  return enqueueBufferTransfer(checked(queueValue), checked(memoryValue), blocking, offset, size, input, count, waits, event);
}

} // extern "C"

namespace {

bool rectPitch(size_t requestedRow, size_t requestedSlice, const size_t region[3], size_t *row, size_t *slice) {
  if (!region || !row || !slice || region[0] == 0 || region[1] == 0 || region[2] == 0) return false;
  *row = requestedRow == 0 ? region[0] : requestedRow;
  if (*row < region[0] || region[1] > std::numeric_limits<size_t>::max() / *row) return false;
  const size_t minimumSlice = *row * region[1];
  *slice = requestedSlice == 0 ? minimumSlice : requestedSlice;
  return *slice >= minimumSlice;
}

bool rectEnd(const size_t origin[3], const size_t region[3], size_t row, size_t slice, size_t *end) {
  if (!origin || !end || !region) return false;
  size_t value = origin[0];
  if (region[1] - 1 > std::numeric_limits<size_t>::max() / row) return false;
  if (value > std::numeric_limits<size_t>::max() - ((region[1] - 1) * row)) return false;
  value += (region[1] - 1) * row;
  if (region[2] - 1 > std::numeric_limits<size_t>::max() / slice) return false;
  if (value > std::numeric_limits<size_t>::max() - ((region[2] - 1) * slice)) return false;
  value += (region[2] - 1) * slice;
  if (value > std::numeric_limits<size_t>::max() - region[0]) return false;
  *end = value + region[0];
  return true;
}

cl_int enqueueReadBufferRect(_cl_command_queue *queue, _cl_mem *mem, bool blocking, const size_t bufferOrigin[3],
                             const size_t hostOrigin[3], const size_t region[3], size_t bufferRowPitch, size_t bufferSlicePitch,
                             size_t hostRowPitch, size_t hostSlicePitch, void *output, cl_uint count, const cl_event *waits,
                             cl_event *event, cl_command_type type = CL_COMMAND_READ_BUFFER_RECT) {
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!mem || mem->context != queue->context || !output || !bufferOrigin || !hostOrigin || !region) return CL_INVALID_VALUE;
  if (!hostReadAllowed(mem)) return CL_INVALID_OPERATION;
  size_t sourceRow = 0;
  size_t sourceSlice = 0;
  size_t targetRow = 0;
  size_t targetSlice = 0;
  size_t sourceEnd = 0;
  size_t targetEnd = 0;
  if (!rectPitch(bufferRowPitch, bufferSlicePitch, region, &sourceRow, &sourceSlice)
      || !rectPitch(hostRowPitch, hostSlicePitch, region, &targetRow, &targetSlice)
      || !rectEnd(bufferOrigin, region, sourceRow, sourceSlice, &sourceEnd)
      || !rectEnd(hostOrigin, region, targetRow, targetSlice, &targetEnd) || sourceEnd > mem->size)
    return CL_INVALID_VALUE;
  const std::array<size_t, 3> copiedBufferOrigin{bufferOrigin[0], bufferOrigin[1], bufferOrigin[2]};
  const std::array<size_t, 3> copiedHostOrigin{hostOrigin[0], hostOrigin[1], hostOrigin[2]};
  const std::array<size_t, 3> copiedRegion{region[0], region[1], region[2]};
  auto retainedMemory = retainMemory(mem);
  auto execute = [=] {
    (void)retainedMemory;
    const auto *bufferOrigin = copiedBufferOrigin.data();
    const auto *hostOrigin = copiedHostOrigin.data();
    const auto *region = copiedRegion.data();
    const Rectangle rectangle{.offset = mem->origin + (bufferOrigin[2] * sourceSlice) + (bufferOrigin[1] * sourceRow) + bufferOrigin[0],
                              .rowBytes = region[0],
                              .rowCount = region[1],
                              .sliceCount = region[2],
                              .rowPitch = sourceRow,
                              .slicePitch = sourceSlice};
    return readAllocationRectangle(mem->allocation, rectangle,
                                   static_cast<uint8_t *>(output) + (hostOrigin[2] * targetSlice) + (hostOrigin[1] * targetRow)
                                       + hostOrigin[0],
                                   targetRow, targetSlice);
  };
  return submitCommand(queue, type, blocking, count, waits, std::move(execute), event);
}

cl_int enqueueWriteBufferRect(_cl_command_queue *queue, _cl_mem *mem, bool blocking, const size_t bufferOrigin[3],
                              const size_t hostOrigin[3], const size_t region[3], size_t bufferRowPitch, size_t bufferSlicePitch,
                              size_t hostRowPitch, size_t hostSlicePitch, const void *input, cl_uint count, const cl_event *waits,
                              cl_event *event, cl_command_type type = CL_COMMAND_WRITE_BUFFER_RECT) {
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!mem || mem->context != queue->context || !input || !bufferOrigin || !hostOrigin || !region) return CL_INVALID_VALUE;
  if (!hostWriteAllowed(mem)) return CL_INVALID_OPERATION;
  size_t sourceRow = 0;
  size_t sourceSlice = 0;
  size_t targetRow = 0;
  size_t targetSlice = 0;
  size_t sourceEnd = 0;
  size_t targetEnd = 0;
  if (!rectPitch(bufferRowPitch, bufferSlicePitch, region, &targetRow, &targetSlice)
      || !rectPitch(hostRowPitch, hostSlicePitch, region, &sourceRow, &sourceSlice)
      || !rectEnd(bufferOrigin, region, targetRow, targetSlice, &targetEnd)
      || !rectEnd(hostOrigin, region, sourceRow, sourceSlice, &sourceEnd) || targetEnd > mem->size)
    return CL_INVALID_VALUE;
  const std::array<size_t, 3> copiedBufferOrigin{bufferOrigin[0], bufferOrigin[1], bufferOrigin[2]};
  const std::array<size_t, 3> copiedHostOrigin{hostOrigin[0], hostOrigin[1], hostOrigin[2]};
  const std::array<size_t, 3> copiedRegion{region[0], region[1], region[2]};
  auto retainedMemory = retainMemory(mem);
  auto execute = [=] {
    (void)retainedMemory;
    const auto *bufferOrigin = copiedBufferOrigin.data();
    const auto *hostOrigin = copiedHostOrigin.data();
    const auto *region = copiedRegion.data();
    const Rectangle rectangle{.offset = mem->origin + (bufferOrigin[2] * targetSlice) + (bufferOrigin[1] * targetRow) + bufferOrigin[0],
                              .rowBytes = region[0],
                              .rowCount = region[1],
                              .sliceCount = region[2],
                              .rowPitch = targetRow,
                              .slicePitch = targetSlice};
    return writeAllocationRectangle(mem->allocation, rectangle,
                                    static_cast<const uint8_t *>(input) + (hostOrigin[2] * sourceSlice) + (hostOrigin[1] * sourceRow)
                                        + hostOrigin[0],
                                    sourceRow, sourceSlice);
  };
  return submitCommand(queue, type, blocking, count, waits, std::move(execute), event);
}

cl_int enqueueCopyBufferRect(_cl_command_queue *queue, _cl_mem *source, _cl_mem *target, const size_t sourceOrigin[3],
                             const size_t targetOrigin[3], const size_t region[3], size_t sourceRowPitch, size_t sourceSlicePitch,
                             size_t targetRowPitch, size_t targetSlicePitch, cl_uint count, const cl_event *waits, cl_event *event,
                             cl_command_type type = CL_COMMAND_COPY_BUFFER_RECT) {
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!source || !target || source->context != queue->context || target->context != queue->context || !sourceOrigin || !targetOrigin
      || !region)
    return CL_INVALID_VALUE;
  if (immutable(target)) return CL_INVALID_OPERATION;
  size_t sourceRow = 0;
  size_t sourceSlice = 0;
  size_t targetRow = 0;
  size_t targetSlice = 0;
  size_t sourceEnd = 0;
  size_t targetEnd = 0;
  if (!rectPitch(sourceRowPitch, sourceSlicePitch, region, &sourceRow, &sourceSlice)
      || !rectPitch(targetRowPitch, targetSlicePitch, region, &targetRow, &targetSlice)
      || !rectEnd(sourceOrigin, region, sourceRow, sourceSlice, &sourceEnd)
      || !rectEnd(targetOrigin, region, targetRow, targetSlice, &targetEnd) || sourceEnd > source->size || targetEnd > target->size)
    return CL_INVALID_VALUE;
  const size_t rowCount = region[1] * region[2];
  if (region[0] != 0 && rowCount > std::numeric_limits<size_t>::max() / region[0]) return CL_INVALID_VALUE;
  const std::array<size_t, 3> copiedSourceOrigin{sourceOrigin[0], sourceOrigin[1], sourceOrigin[2]};
  const std::array<size_t, 3> copiedTargetOrigin{targetOrigin[0], targetOrigin[1], targetOrigin[2]};
  const std::array<size_t, 3> copiedRegion{region[0], region[1], region[2]};
  auto retainedSource = retainMemory(source);
  auto retainedTarget = retainMemory(target);
  auto execute = [=] {
    (void)retainedSource;
    (void)retainedTarget;
    const auto *sourceOrigin = copiedSourceOrigin.data();
    const auto *targetOrigin = copiedTargetOrigin.data();
    const auto *region = copiedRegion.data();
    const Rectangle from{.offset = source->origin + (sourceOrigin[2] * sourceSlice) + (sourceOrigin[1] * sourceRow) + sourceOrigin[0],
                         .rowBytes = region[0],
                         .rowCount = region[1],
                         .sliceCount = region[2],
                         .rowPitch = sourceRow,
                         .slicePitch = sourceSlice};
    const Rectangle to{.offset = target->origin + (targetOrigin[2] * targetSlice) + (targetOrigin[1] * targetRow) + targetOrigin[0],
                       .rowBytes = region[0],
                       .rowCount = region[1],
                       .sliceCount = region[2],
                       .rowPitch = targetRow,
                       .slicePitch = targetSlice};
    std::vector<uint8_t> packed(region[0] * rowCount);
    auto result = readAllocationRectangle(source->allocation, from, packed.data(), region[0], region[0] * region[1]);
    if (result != CL_SUCCESS) return result;
    result = writeAllocationRectangle(target->allocation, to, packed.data(), region[0], region[0] * region[1]);
    if (result != CL_SUCCESS) return result;
    forEachRectangleRow(from, [&](uint64_t sourceOffset, size_t slice, size_t row) {
      copyPointerRelocations(source->allocation, sourceOffset, target->allocation, to.offset + (slice * targetSlice) + (row * targetRow),
                             region[0]);
      return true;
    });
    return CL_SUCCESS;
  };
  return submitCommand(queue, type, false, count, waits, std::move(execute), event);
}

} // namespace

namespace {

// A region in texels, as (x, y, z) and (width, height, depth) where z selects array layers or 3D slices.
struct ImageBox {
  std::array<size_t, 3> origin{};
  std::array<size_t, 3> region{};
};

cl_int imageBox(const _cl_mem *mem, const size_t *origin, const size_t *region, ImageBox &box) {
  if (!mem || !mem->image) return CL_INVALID_MEM_OBJECT;
  if (!origin || !region || region[0] == 0 || region[1] == 0 || region[2] == 0) return CL_INVALID_VALUE;
  const auto &desc = mem->image->desc;
  switch (desc.image_type) {
    case CL_MEM_OBJECT_IMAGE1D:
    case CL_MEM_OBJECT_IMAGE1D_BUFFER:
      if (origin[1] != 0 || origin[2] != 0 || region[1] != 1 || region[2] != 1) return CL_INVALID_VALUE;
      box = {.origin = {origin[0], 0, 0}, .region = {region[0], 1, 1}};
      break;
    case CL_MEM_OBJECT_IMAGE1D_ARRAY:
      if (origin[2] != 0 || region[2] != 1) return CL_INVALID_VALUE;
      box = {.origin = {origin[0], 0, origin[1]}, .region = {region[0], 1, region[1]}};
      break;
    case CL_MEM_OBJECT_IMAGE2D:
      if (origin[2] != 0 || region[2] != 1) return CL_INVALID_VALUE;
      box = {.origin = {origin[0], origin[1], 0}, .region = {region[0], region[1], 1}};
      break;
    default: box = {.origin = {origin[0], origin[1], origin[2]}, .region = {region[0], region[1], region[2]}};
  }
  const std::array<size_t, 3> extent{desc.image_width, imageHeight(desc), imageSlices(desc)};
  for (size_t axis = 0; axis < 3; ++axis)
    if (box.origin[axis] > extent[axis] || box.region[axis] > extent[axis] - box.origin[axis]) return CL_INVALID_VALUE;
  return CL_SUCCESS;
}

std::array<size_t, 3> byteOrigin(const ImageShape &image, const ImageBox &box) {
  return {box.origin[0] * image.elementSize, box.origin[1], box.origin[2]};
}

std::array<size_t, 3> byteRegion(const ImageShape &image, const ImageBox &box) {
  return {box.region[0] * image.elementSize, box.region[1], box.region[2]};
}

// The host pitch between slices of a region, which a 1D image array spends on layers.
cl_int hostPitches(const ImageShape &image, const ImageBox &box, size_t rowPitch, size_t slicePitch, size_t &row, size_t &slice) {
  const auto type = image.desc.image_type;
  row = rowPitch == 0 ? box.region[0] * image.elementSize : rowPitch;
  if (row < box.region[0] * image.elementSize) return CL_INVALID_VALUE;
  if ((type == CL_MEM_OBJECT_IMAGE1D || type == CL_MEM_OBJECT_IMAGE1D_BUFFER || type == CL_MEM_OBJECT_IMAGE2D) && slicePitch != 0)
    return CL_INVALID_VALUE;
  const size_t minimum = type == CL_MEM_OBJECT_IMAGE1D_ARRAY ? row : row * box.region[1];
  slice = slicePitch == 0 ? minimum : slicePitch;
  return slice < minimum ? CL_INVALID_VALUE : CL_SUCCESS;
}

template <class Host>
cl_int enqueueImageTransfer(_cl_command_queue *queue, _cl_mem *mem, cl_bool blocking, const size_t *origin, const size_t *region,
                            size_t rowPitch, size_t slicePitch, Host *pointer, cl_uint count, const cl_event *waits, cl_event *event) {
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (!mem || !mem->image) return CL_INVALID_MEM_OBJECT;
  if (mem->context != queue->context) return CL_INVALID_CONTEXT;
  ImageBox box;
  if (const auto status = imageBox(mem, origin, region, box); status != CL_SUCCESS) return status;
  size_t row = 0;
  size_t slice = 0;
  if (!pointer || hostPitches(*mem->image, box, rowPitch, slicePitch, row, slice) != CL_SUCCESS) return CL_INVALID_VALUE;
  const auto bufferOrigin = byteOrigin(*mem->image, box);
  const auto bytes = byteRegion(*mem->image, box);
  const size_t hostOrigin[] = {0, 0, 0};
  if constexpr (std::is_const_v<Host>)
    return enqueueWriteBufferRect(queue, mem, blocking, bufferOrigin.data(), hostOrigin, bytes.data(), mem->image->rowPitch,
                                  mem->image->slicePitch, row, slice, pointer, count, waits, event, CL_COMMAND_WRITE_IMAGE);
  else
    return enqueueReadBufferRect(queue, mem, blocking, bufferOrigin.data(), hostOrigin, bytes.data(), mem->image->rowPitch,
                                 mem->image->slicePitch, row, slice, pointer, count, waits, event, CL_COMMAND_READ_IMAGE);
}

bool overlaps(const ImageBox &left, const ImageBox &right) {
  for (size_t axis = 0; axis < 3; ++axis)
    if (left.origin[axis] + left.region[axis] <= right.origin[axis] || right.origin[axis] + right.region[axis] <= left.origin[axis])
      return false;
  return true;
}

uint16_t halfBits(float value) {
  uint32_t bits = 0;
  std::memcpy(&bits, &value, sizeof(bits));
  const uint32_t sign = (bits >> 16) & 0x8000U;
  const int32_t exponent = static_cast<int32_t>((bits >> 23) & 0xffU) - 127 + 15;
  uint32_t mantissa = bits & 0x7fffffU;
  if (((bits >> 23) & 0xffU) == 0xffU) return static_cast<uint16_t>(sign | 0x7c00U | (mantissa ? 0x200U : 0U));
  if (exponent >= 31) return static_cast<uint16_t>(sign | 0x7c00U);
  if (exponent <= 0) {
    if (exponent < -10) return static_cast<uint16_t>(sign);
    mantissa |= 0x800000U;
    const uint32_t shift = static_cast<uint32_t>(14 - exponent);
    uint32_t half = mantissa >> shift;
    const uint32_t rest = mantissa & ((1U << shift) - 1U);
    const uint32_t midpoint = 1U << (shift - 1);
    if (rest > midpoint || (rest == midpoint && (half & 1U))) ++half;
    return static_cast<uint16_t>(sign | half);
  }
  uint32_t half = (static_cast<uint32_t>(exponent) << 10) | (mantissa >> 13);
  const uint32_t rest = mantissa & 0x1fffU;
  if (rest > 0x1000U || (rest == 0x1000U && (half & 1U))) ++half;
  return static_cast<uint16_t>(sign | half);
}

// The fill colour as one texel: float4 for normalised and floating formats, int4 or uint4 for integer formats.
std::vector<uint8_t> fillTexel(const ImageShape &image, const void *colour) {
  const auto *info = imageFormatInfo(image.format);
  std::array<float, 4> floats{};
  std::array<int32_t, 4> signedValues{};
  std::array<uint32_t, 4> unsignedValues{};
  std::memcpy(floats.data(), colour, sizeof(floats));
  std::memcpy(signedValues.data(), colour, sizeof(signedValues));
  std::memcpy(unsignedValues.data(), colour, sizeof(unsignedValues));
  if (info->order == CL_sRGBA)
    for (size_t lane = 0; lane < 3; ++lane) {
      const float linear = std::clamp(floats[lane], 0.0F, 1.0F);
      floats[lane] = linear <= 0.0031308F ? linear * 12.92F : (1.055F * std::pow(linear, 1.0F / 2.4F)) - 0.055F;
    }
  const std::array<size_t, 4> lanes = info->order == CL_BGRA ? std::array<size_t, 4>{2, 1, 0, 3} : std::array<size_t, 4>{0, 1, 2, 3};
  std::vector<uint8_t> texel(info->channels * info->channelBytes);
  for (size_t channel = 0; channel < info->channels; ++channel) {
    const size_t lane = lanes[channel];
    const auto bits = static_cast<int>(info->channelBytes * 8);
    uint64_t encoded = 0;
    switch (info->channel) {
      case Channel::Unorm: {
        const double maximum = static_cast<double>((uint64_t{1} << bits) - 1);
        encoded = static_cast<uint64_t>(std::nearbyint(std::clamp(static_cast<double>(floats[lane]), 0.0, 1.0) * maximum));
        break;
      }
      case Channel::Snorm: {
        const double maximum = static_cast<double>((uint64_t{1} << (bits - 1)) - 1);
        encoded =
            static_cast<uint64_t>(static_cast<int64_t>(std::nearbyint(std::clamp(static_cast<double>(floats[lane]), -1.0, 1.0) * maximum)));
        break;
      }
      case Channel::Signed: {
        const int64_t high = (int64_t{1} << (bits - 1)) - 1;
        encoded = static_cast<uint64_t>(std::clamp<int64_t>(signedValues[lane], -high - 1, high));
        break;
      }
      case Channel::Unsigned: {
        const uint64_t high = bits == 32 ? 0xffffffffU : (uint64_t{1} << bits) - 1;
        encoded = std::min<uint64_t>(unsignedValues[lane], high);
        break;
      }
      case Channel::Half: encoded = halfBits(floats[lane]); break;
      case Channel::Float: {
        uint32_t word = 0;
        std::memcpy(&word, &floats[lane], sizeof(word));
        encoded = word;
        break;
      }
    }
    std::memcpy(texel.data() + (channel * info->channelBytes), &encoded, info->channelBytes);
  }
  return texel;
}

// Copies the rows of an image mapping between the packed texels and the mapped pointer.
cl_int copyMappedRows(const std::shared_ptr<_cl_mem> &mem, const BufferMapping &mapping, const ImageRows &rows, bool toHost) {
  const Rectangle rectangle{.offset = mapping.offset,
                            .rowBytes = rows.region[0],
                            .rowCount = rows.region[1],
                            .sliceCount = rows.region[2],
                            .rowPitch = rows.imageRow,
                            .slicePitch = rows.imageSlice};
  return toHost ? readAllocationRectangle(mem->allocation, rectangle, mapping.pointer, rows.hostRow, rows.hostSlice)
                : writeAllocationRectangle(mem->allocation, rectangle, mapping.pointer, rows.hostRow, rows.hostSlice);
}

} // namespace

extern "C" {

CL_API_ENTRY cl_int CL_API_CALL clEnqueueReadImage(cl_command_queue queue, cl_mem image, cl_bool blocking, const size_t *origin,
                                                   const size_t *region, size_t rowPitch, size_t slicePitch, void *output, cl_uint count,
                                                   const cl_event *waits, cl_event *event) {
  return enqueueImageTransfer(checked(queue), checked(image), blocking, origin, region, rowPitch, slicePitch, output, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueWriteImage(cl_command_queue queue, cl_mem image, cl_bool blocking, const size_t *origin,
                                                    const size_t *region, size_t rowPitch, size_t slicePitch, const void *input,
                                                    cl_uint count, const cl_event *waits, cl_event *event) {
  return enqueueImageTransfer(checked(queue), checked(image), blocking, origin, region, rowPitch, slicePitch, input, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyImage(cl_command_queue queueValue, cl_mem sourceValue, cl_mem targetValue,
                                                   const size_t *sourceOrigin, const size_t *targetOrigin, const size_t *region,
                                                   cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  auto *source = checked(sourceValue);
  auto *target = checked(targetValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (!source || !target || !source->image || !target->image) return CL_INVALID_MEM_OBJECT;
  if (source->context != queue->context || target->context != queue->context) return CL_INVALID_CONTEXT;
  if (immutable(target)) return CL_INVALID_OPERATION;
  if (source->image->format.image_channel_order != target->image->format.image_channel_order
      || source->image->format.image_channel_data_type != target->image->format.image_channel_data_type)
    return CL_IMAGE_FORMAT_MISMATCH;
  ImageBox from;
  ImageBox to;
  if (imageBox(source, sourceOrigin, region, from) != CL_SUCCESS || imageBox(target, targetOrigin, region, to) != CL_SUCCESS
      || from.region != to.region)
    return CL_INVALID_VALUE;
  if (source == target && overlaps(from, to)) return CL_MEM_COPY_OVERLAP;
  return enqueueCopyBufferRect(queue, source, target, byteOrigin(*source->image, from).data(), byteOrigin(*target->image, to).data(),
                               byteRegion(*source->image, from).data(), source->image->rowPitch, source->image->slicePitch,
                               target->image->rowPitch, target->image->slicePitch, count, waits, event, CL_COMMAND_COPY_IMAGE);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyImageToBuffer(cl_command_queue queueValue, cl_mem sourceValue, cl_mem targetValue,
                                                           const size_t *origin, const size_t *region, size_t offset, cl_uint count,
                                                           const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  auto *source = checked(sourceValue);
  auto *target = checked(targetValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (!source || !target || !source->image || target->image) return CL_INVALID_MEM_OBJECT;
  if (source->context != queue->context || target->context != queue->context) return CL_INVALID_CONTEXT;
  if (immutable(target)) return CL_INVALID_OPERATION;
  ImageBox box;
  if (const auto status = imageBox(source, origin, region, box); status != CL_SUCCESS) return status;
  const auto bytes = byteRegion(*source->image, box);
  const size_t bufferOrigin[] = {offset, 0, 0};
  return enqueueCopyBufferRect(queue, source, target, byteOrigin(*source->image, box).data(), bufferOrigin, bytes.data(),
                               source->image->rowPitch, source->image->slicePitch, bytes[0], bytes[0] * bytes[1], count, waits, event,
                               CL_COMMAND_COPY_IMAGE_TO_BUFFER);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyBufferToImage(cl_command_queue queueValue, cl_mem sourceValue, cl_mem targetValue,
                                                           size_t offset, const size_t *origin, const size_t *region, cl_uint count,
                                                           const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  auto *source = checked(sourceValue);
  auto *target = checked(targetValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (!source || !target || source->image || !target->image) return CL_INVALID_MEM_OBJECT;
  if (source->context != queue->context || target->context != queue->context) return CL_INVALID_CONTEXT;
  if (immutable(target)) return CL_INVALID_OPERATION;
  ImageBox box;
  if (const auto status = imageBox(target, origin, region, box); status != CL_SUCCESS) return status;
  const auto bytes = byteRegion(*target->image, box);
  const size_t bufferOrigin[] = {offset, 0, 0};
  return enqueueCopyBufferRect(queue, source, target, bufferOrigin, byteOrigin(*target->image, box).data(), bytes.data(), bytes[0],
                               bytes[0] * bytes[1], target->image->rowPitch, target->image->slicePitch, count, waits, event,
                               CL_COMMAND_COPY_BUFFER_TO_IMAGE);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueFillImage(cl_command_queue queueValue, cl_mem imageValue, const void *colour, const size_t *origin,
                                                   const size_t *region, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  auto *mem = checked(imageValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (!mem || !mem->image) return CL_INVALID_MEM_OBJECT;
  if (mem->context != queue->context) return CL_INVALID_CONTEXT;
  if (immutable(mem)) return CL_INVALID_OPERATION;
  if (!colour) return CL_INVALID_VALUE;
  if (const auto status = validateWaitList(queue, count, waits); status != CL_SUCCESS) return status;
  ImageBox box;
  if (const auto status = imageBox(mem, origin, region, box); status != CL_SUCCESS) return status;
  const auto texel = fillTexel(*mem->image, colour);
  std::vector<uint8_t> row;
  row.reserve(texel.size() * box.region[0]);
  for (size_t x = 0; x < box.region[0]; ++x)
    row.insert(row.end(), texel.begin(), texel.end());
  auto retained = retainMemory(mem);
  return submitCommand(
      queue, CL_COMMAND_FILL_IMAGE, false, count, waits,
      [=] {
        const auto &image = *retained->image;
        const Rectangle rectangle{.offset = retained->origin + (box.origin[2] * image.slicePitch) + (box.origin[1] * image.rowPitch)
                                            + (box.origin[0] * image.elementSize),
                                  .rowBytes = row.size(),
                                  .rowCount = box.region[1],
                                  .sliceCount = box.region[2],
                                  .rowPitch = image.rowPitch,
                                  .slicePitch = image.slicePitch};
        return writeAllocationRectangle(retained->allocation, rectangle, row.data(), 0, 0);
      },
      event);
}

CL_API_ENTRY void *CL_API_CALL clEnqueueMapImage(cl_command_queue queueValue, cl_mem imageValue, cl_bool blocking, cl_map_flags flags,
                                                 const size_t *origin, const size_t *region, size_t *rowPitch, size_t *slicePitch,
                                                 cl_uint count, const cl_event *waits, cl_event *event, cl_int *error) {
  auto *queue = checked(queueValue);
  auto *mem = checked(imageValue);
  if (!queue) return fail(error, CL_INVALID_COMMAND_QUEUE);
  if (!mem || !mem->image) return fail(error, CL_INVALID_MEM_OBJECT);
  if (mem->context != queue->context) return fail(error, CL_INVALID_CONTEXT);
  if (immutable(mem) && (flags & (CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION))) return fail(error, CL_INVALID_OPERATION);
  if (!rowPitch || (flags & ~(CL_MAP_READ | CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) != 0) return fail(error, CL_INVALID_VALUE);
  const auto type = mem->image->desc.image_type;
  if (!slicePitch && (type == CL_MEM_OBJECT_IMAGE3D || arrayed(type))) return fail(error, CL_INVALID_VALUE);
  if (((flags & CL_MAP_READ) != 0 && !hostReadAllowed(mem))
      || ((flags & (CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) != 0 && !hostWriteAllowed(mem)))
    return fail(error, CL_INVALID_OPERATION);
  if (const auto status = validateWaitList(queue, count, waits); status != CL_SUCCESS) return fail(error, status);
  ImageBox box;
  if (const auto status = imageBox(mem, origin, region, box); status != CL_SUCCESS) return fail(error, status);
  const auto &image = *mem->image;
  std::shared_ptr<BufferMapping> mapping;
  try {
    mapping = std::make_shared<BufferMapping>();
  } catch (const std::bad_alloc &) {
    return fail(error, CL_OUT_OF_HOST_MEMORY);
  }
  ImageRows rows;
  rows.region = byteRegion(image, box);
  rows.imageRow = image.rowPitch;
  rows.imageSlice = image.slicePitch;
  // A host-pointer image maps into the application's memory at its own pitches.
  if (mem->hostPointer) {
    rows.hostRow = image.hostRowPitch;
    rows.hostSlice = image.hostSlicePitch;
    mapping->pointer = static_cast<uint8_t *>(mem->hostPointer) + (box.origin[2] * rows.hostSlice) + (box.origin[1] * rows.hostRow)
                       + (box.origin[0] * image.elementSize);
  } else {
    rows.hostRow = rows.region[0];
    rows.hostSlice = rows.region[0] * rows.region[1];
    mapping->storage = std::shared_ptr<uint8_t[]>(new (std::nothrow) uint8_t[rows.hostSlice * rows.region[2]]);
    if (!mapping->storage) return fail(error, CL_OUT_OF_HOST_MEMORY);
    mapping->pointer = mapping->storage.get();
  }
  mapping->offset =
      mem->origin + (box.origin[2] * image.slicePitch) + (box.origin[1] * image.rowPitch) + (box.origin[0] * image.elementSize);
  mapping->size = rows.hostSlice * rows.region[2];
  mapping->rows = rows;
  *rowPitch = rows.hostRow;
  if (slicePitch) *slicePitch = type == CL_MEM_OBJECT_IMAGE3D || arrayed(type) ? rows.hostSlice : 0;
  auto retained = retainMemory(mem);
  const auto key = reinterpret_cast<uintptr_t>(mapping->pointer);
  {
    std::scoped_lock const lock(allocationsMutex);
    mem->mappings.emplace(key, mapping);
  }
  const auto result = submitCommand(
      queue, CL_COMMAND_MAP_IMAGE, blocking, count, waits,
      [=] { return (flags & CL_MAP_WRITE_INVALIDATE_REGION) != 0 ? CL_SUCCESS : copyMappedRows(retained, *mapping, rows, true); }, event,
      [=](cl_int status) {
        if (status == CL_SUCCESS) return;
        std::scoped_lock const lock(allocationsMutex);
        const auto [begin, end] = retained->mappings.equal_range(key);
        const auto found = std::find_if(begin, end, [&](const auto &entry) { return entry.second == mapping; });
        if (found != end) retained->mappings.erase(found);
      });
  if (error) *error = result;
  return result == CL_SUCCESS ? mapping->pointer : nullptr;
}

} // extern "C"

extern "C" {

CL_API_ENTRY cl_int CL_API_CALL clEnqueueReadBufferRect(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking,
                                                        const size_t *bufferOrigin, const size_t *hostOrigin, const size_t *region,
                                                        size_t bufferRowPitch, size_t bufferSlicePitch, size_t hostRowPitch,
                                                        size_t hostSlicePitch, void *output, cl_uint count, const cl_event *waits,
                                                        cl_event *event) {
  return enqueueReadBufferRect(checked(queueValue), checked(memoryValue), blocking, bufferOrigin, hostOrigin, region, bufferRowPitch,
                               bufferSlicePitch, hostRowPitch, hostSlicePitch, output, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueWriteBufferRect(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking,
                                                         const size_t *bufferOrigin, const size_t *hostOrigin, const size_t *region,
                                                         size_t bufferRowPitch, size_t bufferSlicePitch, size_t hostRowPitch,
                                                         size_t hostSlicePitch, const void *input, cl_uint count, const cl_event *waits,
                                                         cl_event *event) {
  return enqueueWriteBufferRect(checked(queueValue), checked(memoryValue), blocking, bufferOrigin, hostOrigin, region, bufferRowPitch,
                                bufferSlicePitch, hostRowPitch, hostSlicePitch, input, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyBufferRect(cl_command_queue queueValue, cl_mem sourceValue, cl_mem targetValue,
                                                        const size_t *sourceOrigin, const size_t *targetOrigin, const size_t *region,
                                                        size_t sourceRowPitch, size_t sourceSlicePitch, size_t targetRowPitch,
                                                        size_t targetSlicePitch, cl_uint count, const cl_event *waits, cl_event *event) {
  return enqueueCopyBufferRect(checked(queueValue), checked(sourceValue), checked(targetValue), sourceOrigin, targetOrigin, region,
                               sourceRowPitch, sourceSlicePitch, targetRowPitch, targetSlicePitch, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyBuffer(cl_command_queue queueValue, cl_mem sourceValue, cl_mem destinationValue,
                                                    size_t sourceOffset, size_t destinationOffset, size_t size, cl_uint count,
                                                    const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  auto *src = checked(sourceValue);
  auto *dst = checked(destinationValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!src || !dst || src->context != queue->context || dst->context != queue->context || !validMemoryRange(src, sourceOffset, size)
      || !validMemoryRange(dst, destinationOffset, size))
    return CL_INVALID_VALUE;
  if (immutable(dst)) return CL_INVALID_OPERATION;
  const size_t allocationSourceOffset = allocationOffset(src, sourceOffset);
  const size_t allocationTargetOffset = allocationOffset(dst, destinationOffset);
  auto source = retainMemory(src);
  auto target = retainMemory(dst);
  return submitCommand(
      queue, CL_COMMAND_COPY_BUFFER, false, count, waits,
      [=] {
        std::memmove(static_cast<uint8_t *>(target->allocation->data()) + allocationTargetOffset,
                     static_cast<uint8_t *>(source->allocation->data()) + allocationSourceOffset, size);
        const auto result = writeAllocation(target->allocation, allocationTargetOffset,
                                            static_cast<uint8_t *>(target->allocation->data()) + allocationTargetOffset, size);
        if (result == CL_SUCCESS)
          copyPointerRelocations(source->allocation, allocationSourceOffset, target->allocation, allocationTargetOffset, size);
        return result;
      },
      event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueFillBuffer(cl_command_queue queueValue, cl_mem memoryValue, const void *pattern,
                                                    size_t patternSize, size_t offset, size_t size, cl_uint count, const cl_event *waits,
                                                    cl_event *event) {
  auto *queue = checked(queueValue);
  auto *mem = checked(memoryValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!mem || mem->context != queue->context || !pattern || patternSize == 0 || size % patternSize != 0
      || !validMemoryRange(mem, offset, size))
    return CL_INVALID_VALUE;
  if (immutable(mem)) return CL_INVALID_OPERATION;
  const size_t targetOffset = allocationOffset(mem, offset);
  auto retained = retainMemory(mem);
  std::vector<uint8_t> const copiedPattern(static_cast<const uint8_t *>(pattern), static_cast<const uint8_t *>(pattern) + patternSize);
  return submitCommand(
      queue, CL_COMMAND_FILL_BUFFER, false, count, waits,
      [=] {
        auto *bytes = static_cast<uint8_t *>(retained->allocation->data());
        for (size_t index = 0; index < size; ++index)
          bytes[targetOffset + index] = copiedPattern[index % patternSize];
        return writeAllocation(retained->allocation, targetOffset, bytes + targetOffset, size);
      },
      event);
}

CL_API_ENTRY void *CL_API_CALL clEnqueueMapBuffer(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking, cl_map_flags flags,
                                                  size_t offset, size_t size, cl_uint count, const cl_event *waits, cl_event *event,
                                                  cl_int *error) {
  auto *queue = checked(queueValue);
  auto *mem = checked(memoryValue);
  if (!queue) return fail(error, CL_INVALID_COMMAND_QUEUE);
  if (!mem || mem->context != queue->context) return fail(error, CL_INVALID_MEM_OBJECT);
  if ((flags & ~(CL_MAP_READ | CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) != 0
      || (flags & (CL_MAP_READ | CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) == 0 || !validMemoryRange(mem, offset, size))
    return fail(error, CL_INVALID_VALUE);
  if (((flags & CL_MAP_READ) != 0 && !hostReadAllowed(mem))
      || ((flags & (CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) != 0 && !hostWriteAllowed(mem)))
    return fail(error, CL_INVALID_OPERATION);
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return fail(error, result);
  const bool directMapping = mem->hostPointer && (mem->flags & CL_MEM_USE_HOST_PTR) != 0;
  std::shared_ptr<BufferMapping> mapping;
  try {
    mapping = std::make_shared<BufferMapping>();
    if (!directMapping) mapping->storage = std::shared_ptr<uint8_t[]>(new (std::nothrow) uint8_t[size == 0 ? 1 : size]);
  } catch (const std::bad_alloc &) {
    return fail(error, CL_OUT_OF_HOST_MEMORY);
  }
  void *pointer = directMapping ? static_cast<uint8_t *>(mem->hostPointer) + offset : mapping->storage.get();
  if (!pointer) return fail(error, CL_OUT_OF_HOST_MEMORY);
  mapping->offset = allocationOffset(mem, offset);
  mapping->size = size;
  mapping->pointer = pointer;
  auto retained = retainMemory(mem);
  const auto key = reinterpret_cast<uintptr_t>(pointer);
  {
    std::scoped_lock const lock(allocationsMutex);
    mem->mappings.emplace(key, mapping);
  }
  result = submitCommand(
      queue, CL_COMMAND_MAP_BUFFER, blocking, count, waits,
      [=] {
        return (flags & CL_MAP_WRITE_INVALIDATE_REGION) != 0
                   ? CL_SUCCESS
                   : readAllocation(retained->allocation, mapping->offset, mapping->pointer, mapping->size);
      },
      event,
      [=](cl_int status) {
        if (status == CL_SUCCESS) return;
        std::scoped_lock const lock(allocationsMutex);
        const auto [begin, end] = retained->mappings.equal_range(key);
        const auto found = std::find_if(begin, end, [&](const auto &entry) { return entry.second == mapping; });
        if (found != end) retained->mappings.erase(found);
      });
  if (error) *error = result;
  return result == CL_SUCCESS ? pointer : nullptr;
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueUnmapMemObject(cl_command_queue queueValue, cl_mem memoryValue, void *pointer, cl_uint count,
                                                        const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  auto *mem = checked(memoryValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (!mem || mem->context != queue->context || !pointer) return CL_INVALID_MEM_OBJECT;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  std::shared_ptr<BufferMapping> mapping;
  const auto key = reinterpret_cast<uintptr_t>(pointer);
  {
    std::scoped_lock const lock(allocationsMutex);
    const auto [begin, end] = mem->mappings.equal_range(key);
    const auto found = std::find_if(begin, end, [](const auto &entry) { return !entry.second->unmapping; });
    if (found == end) return CL_INVALID_VALUE;
    mapping = found->second;
    mapping->unmapping = true;
  }
  auto retained = retainMemory(mem);
  return submitCommand(
      queue, CL_COMMAND_UNMAP_MEM_OBJECT, false, count, waits,
      [=] {
        return mapping->rows ? copyMappedRows(retained, *mapping, *mapping->rows, false)
                             : writeAllocation(retained->allocation, mapping->offset, mapping->pointer, mapping->size);
      },
      event,
      [=](cl_int status) {
        std::scoped_lock const lock(allocationsMutex);
        const auto [begin, end] = retained->mappings.equal_range(key);
        const auto found = std::find_if(begin, end, [&](const auto &entry) { return entry.second == mapping; });
        if (found != end) {
          if (status == CL_SUCCESS) retained->mappings.erase(found);
          else mapping->unmapping = false;
        }
      });
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueMigrateMemObjects(cl_command_queue queueValue, cl_uint count, const cl_mem *memoryObjects,
                                                           cl_mem_migration_flags, cl_uint waitCount, const cl_event *waits,
                                                           cl_event *event) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (count == 0 || !memoryObjects) return CL_INVALID_VALUE;
  std::vector<std::shared_ptr<_cl_mem>> retained;
  for (cl_uint index = 0; index < count; ++index) {
    auto *memory = checked(memoryObjects[index]);
    if (!memory) return CL_INVALID_MEM_OBJECT;
    if (memory->context != queue->context) return CL_INVALID_CONTEXT;
    retained.push_back(retainMemory(memory));
  }
  return submitCommand(
      queue, CL_COMMAND_MIGRATE_MEM_OBJECTS, false, waitCount, waits,
      [retained] {
        (void)retained;
        return CL_SUCCESS;
      },
      event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueNDRangeKernel(cl_command_queue queueValue, cl_kernel kernelValue, cl_uint workDimension,
                                                       const size_t *offset, const size_t *global, const size_t *local, cl_uint count,
                                                       const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  auto *kernel = checked(kernelValue);
  if (!queue || !kernel) return !queue ? CL_INVALID_COMMAND_QUEUE : CL_INVALID_KERNEL;
  if (workDimension == 0 || workDimension > 3) return CL_INVALID_WORK_DIMENSION;
  if (kernel->arguments.size() != kernel->expectedArguments || (kernel->argumentsSet ^ contains(false))) return CL_INVALID_KERNEL_ARGS;
  if (!global || std::any_of(global, global + workDimension, [](size_t value) { return value == 0; })) {
    if (local) {
      for (cl_uint index = 0; index < workDimension; ++index)
        if (local[index] == 0) return CL_INVALID_WORK_GROUP_SIZE;
    }
    return completeEvent(queue, CL_COMMAND_NDRANGE_KERNEL, count, waits, event);
  }
  for (cl_uint index = 0; index < workDimension; ++index) {
    if (global[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_GLOBAL_WORK_SIZE;
    if (offset && offset[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_GLOBAL_OFFSET;
    if (local && local[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if (local) {
    // Enforce the advertised limits so callers cannot request a work-group the
    // device claims it cannot run.
    const auto limits = maxWorkItemSizes();
    size_t total = 1;
    for (cl_uint index = 0; index < workDimension; ++index) {
      if (local[index] > limits[index]) return CL_INVALID_WORK_GROUP_SIZE;
      if (local[index] != 0 && total > std::numeric_limits<size_t>::max() / local[index]) return CL_INVALID_WORK_GROUP_SIZE;
      total *= local[index];
    }
    if (total > MaxWorkGroupSize) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if (local) {
    const bool uniform = uniformWorkGroups(*kernel->program);
    for (cl_uint index = 0; index < workDimension; ++index)
      if (local[index] == 0 || (uniform && global[index] % local[index] != 0)) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if (local && kernel->requiredWorkGroupSize != std::array<size_t, 3>{0, 0, 0}) {
    if (workDimension != 3) return CL_INVALID_WORK_GROUP_SIZE;
    for (size_t index = 0; index < 3; ++index)
      if (local[index] != kernel->requiredWorkGroupSize[index]) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if ((count != 0 && !waits) || (count == 0 && waits)) return CL_INVALID_EVENT_WAIT_LIST;
  std::vector<_cl_event *> dependencies;
  dependencies.reserve(count);
  for (cl_uint index = 0; index < count; ++index) {
    auto *dependency = checked(waits[index]);
    if (!dependency) return CL_INVALID_EVENT_WAIT_LIST;
    if (dependency->context != queue->context) return CL_INVALID_CONTEXT;
    dependencies.push_back(dependency);
  }
  {
    std::scoped_lock const lock(eventsMutex);
    for (auto *dependency : dependencies)
      if (dependency->status < 0) return CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
  }
  // Submission must return before execution: a shared-memory kernel may wait
  // for a host store performed immediately after this call returns.
  try {
    cl_int error = CL_SUCCESS;
    auto *clone = clCloneKernel(kernelValue, &error);
    if (!clone) return error;
    for (const auto &argument : reinterpret_cast<_cl_kernel *>(clone)->arguments) {
      if (argument.memory) retain(argument.memory);
      if (argument.sampler) retain(argument.sampler);
    }
    std::shared_ptr<_cl_kernel> const snapshot(reinterpret_cast<_cl_kernel *>(clone), [](_cl_kernel *value) {
      // Drop allocation references before releasing retained memory objects.
      for (auto &argument : value->arguments)
        argument.allocation.reset();
      for (auto &argument : value->arguments)
        argument.texels.reset();
      for (const auto &argument : value->arguments) {
        if (argument.memory) clReleaseMemObject(reinterpret_cast<cl_mem>(argument.memory));
        if (argument.sampler) clReleaseSampler(reinterpret_cast<cl_sampler>(argument.sampler));
      }
      clReleaseKernel(reinterpret_cast<cl_kernel>(value));
    });
    std::vector<std::shared_ptr<Allocation>> used;
    for (const auto &argument : snapshot->arguments) {
      if (argument.allocation) used.push_back(argument.allocation);
      if (argument.texels) used.push_back(argument.texels);
    }
    for (const auto &[name, values] : snapshot->indirectAllocations)
      used.insert(used.end(), values.begin(), values.end());
    {
      std::scoped_lock const lock(allocationsMutex);
      for (const auto &[address, allocation] : allocations) {
        if (allocation->svmContextIdentity != queue->context->identity) continue;
        cl_kernel_exec_info type = 0;
        if (allocation->usmType == CL_MEM_TYPE_HOST_INTEL) type = CL_KERNEL_EXEC_INFO_INDIRECT_HOST_ACCESS_INTEL;
        else if (allocation->usmType == CL_MEM_TYPE_DEVICE_INTEL) type = CL_KERNEL_EXEC_INFO_INDIRECT_DEVICE_ACCESS_INTEL;
        else if (allocation->usmType == CL_MEM_TYPE_SHARED_INTEL) type = CL_KERNEL_EXEC_INFO_INDIRECT_SHARED_ACCESS_INTEL;
        if (snapshot->indirectTypes.contains(type)) used.push_back(allocation);
      }
    }
    const bool hasOffset = offset;
    const bool hasLocal = local;
    std::array<size_t, 3> deferredOffset{0, 0, 0};
    std::array<size_t, 3> deferredGlobal{1, 1, 1};
    std::array<size_t, 3> deferredLocal{1, 1, 1};
    for (cl_uint index = 0; index < workDimension; ++index) {
      if (hasOffset) deferredOffset[index] = offset[index];
      deferredGlobal[index] = global[index];
      if (hasLocal) deferredLocal[index] = local[index];
    }
    return enqueuePending(
        queue, CL_COMMAND_NDRANGE_KERNEL, std::move(dependencies),
        [snapshot, workDimension, hasOffset, hasLocal, deferredOffset, deferredGlobal, deferredLocal] {
          return finishRemote(snapshot.get(), workDimension, hasOffset ? deferredOffset.data() : nullptr, deferredGlobal.data(),
                              hasLocal ? deferredLocal.data() : nullptr);
        },
        event, Sync::None, {}, allocationUses(std::move(used)));
  } catch (...) {
    return CL_OUT_OF_HOST_MEMORY;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueTask(cl_command_queue queue, cl_kernel kernel, cl_uint count, const cl_event *waits,
                                              cl_event *event) {
  const size_t global = 1;
  return clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &global, nullptr, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMarkerWithWaitList(cl_command_queue queueValue, cl_uint count, const cl_event *waits,
                                                            cl_event *event) {
  return completeEvent(checked(queueValue), CL_COMMAND_MARKER, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueBarrierWithWaitList(cl_command_queue queueValue, cl_uint count, const cl_event *waits,
                                                             cl_event *event) {
  return completeEvent(checked(queueValue), CL_COMMAND_BARRIER, count, waits, event);
}

CL_API_ENTRY void *CL_API_CALL clSVMAlloc(cl_context contextValue, cl_svm_mem_flags flags, size_t size, cl_uint alignment) {
  auto *context = checked(contextValue);
  if (!context) return nullptr;
  constexpr cl_svm_mem_flags access = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY;
  constexpr cl_svm_mem_flags allowed = access | CL_MEM_SVM_FINE_GRAIN_BUFFER | CL_MEM_SVM_ATOMICS;
  const auto mode = flags & access;
  if ((flags & ~allowed) != 0 || (mode != 0 && (mode & (mode - 1)) != 0)
      || ((flags & CL_MEM_SVM_ATOMICS) && !(flags & CL_MEM_SVM_FINE_GRAIN_BUFFER)))
    return nullptr;
  cl_int error = CL_SUCCESS;
  auto allocation = allocate(size, &error, alignment, true);
  if (allocation) {
    allocation->runtimeOwnsBase = true;
    allocation->svmContextIdentity = context->identity;
  }
  return !allocation ? nullptr : allocation->data();
}
CL_API_ENTRY void CL_API_CALL clSVMFree(cl_context, void *pointer) {
  std::shared_ptr<Allocation> allocation;
  {
    std::scoped_lock const lock(allocationsMutex);
    auto found = allocations.find(reinterpret_cast<uintptr_t>(pointer));
    if (found == allocations.end()) return;
    allocation = found->second;
    const auto start = reinterpret_cast<uintptr_t>(allocation->data());
    for (auto mapping = svmMappings.begin(); mapping != svmMappings.end();) {
      if (mapping->first >= start && mapping->first <= start + allocation->size) mapping = svmMappings.erase(mapping);
      else ++mapping;
    }
    allocations.erase(found);
  }
  releaseAllocation(allocation, true);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMFree(cl_command_queue queueValue, cl_uint count, void *pointers[],
                                                 void(CL_CALLBACK *callback)(cl_command_queue, cl_uint, void *[], void *), void *userData,
                                                 cl_uint waitCount, const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if ((count == 0) != (pointers == nullptr)) return CL_INVALID_VALUE;
  auto result = validateWaitList(queue, waitCount, waits);
  if (result != CL_SUCCESS) return result;
  std::vector<void *> copiedPointers;
  if (count != 0) copiedPointers.assign(pointers, pointers + count);
  for (void *pointer : copiedPointers) {
    auto allocation = findPointer(pointer);
    if (allocation && allocation->svmContextIdentity != 0 && allocation->svmContextIdentity != queue->context->identity)
      return CL_INVALID_VALUE;
    if (!callback && (!allocation || allocation->data() != pointer || !allocation->runtimeOwnsBase || allocation->svmContextIdentity == 0))
      return CL_INVALID_VALUE;
  }
  return submitCommand(
      queue, CL_COMMAND_SVM_FREE, false, waitCount, waits,
      [=]() mutable {
        if (callback) callback(queueValue, count, copiedPointers.data(), userData);
        else
          for (void *pointer : copiedPointers)
            clSVMFree(reinterpret_cast<cl_context>(queue->context), pointer);
        return CL_SUCCESS;
      },
      event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMigrateMem(cl_command_queue queueValue, cl_uint count, const void **pointers,
                                                       const size_t *sizes, cl_mem_migration_flags flags, cl_uint waitCount,
                                                       const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (count == 0 || !pointers || (flags & ~(CL_MIGRATE_MEM_OBJECT_HOST | CL_MIGRATE_MEM_OBJECT_CONTENT_UNDEFINED))) return CL_INVALID_VALUE;
  auto result = validateWaitList(queue, waitCount, waits);
  if (result != CL_SUCCESS) return result;
  struct MigrationRange {
    std::shared_ptr<Allocation> allocation;
    size_t offset;
    size_t size;
  };
  std::vector<MigrationRange> ranges;
  ranges.reserve(count);
  for (cl_uint index = 0; index < count; ++index) {
    size_t offset = 0;
    if (!pointers[index]) return CL_INVALID_VALUE;
    auto allocation = findPointer(pointers[index], &offset);
    // Ordinary host memory stays in place; the device reads it on demand.
    if (!allocation) continue;
    if (allocation->svmContextIdentity != queue->context->identity || offset == allocation->size) return CL_INVALID_VALUE;
    const size_t rangeSize = !sizes || sizes[index] == 0 ? allocation->size : sizes[index];
    const size_t rangeOffset = !sizes || sizes[index] == 0 ? 0 : offset;
    if (!validRange(allocation, rangeOffset, rangeSize)) return CL_INVALID_VALUE;
    ranges.push_back({.allocation = std::move(allocation), .offset = rangeOffset, .size = rangeSize});
  }
  auto used = ranges ^ map([](const MigrationRange &range) { return range.allocation; });
  return submitCommand(
      queue, CL_COMMAND_SVM_MIGRATE_MEM, false, waitCount, waits,
      [=] {
        if ((flags & CL_MIGRATE_MEM_OBJECT_HOST) && !(flags & CL_MIGRATE_MEM_OBJECT_CONTENT_UNDEFINED))
          for (const auto &range : ranges) {
            auto *host = static_cast<uint8_t *>(range.allocation->data()) + range.offset;
            const cl_int error = readAllocation(range.allocation, range.offset, host, range.size);
            if (error != CL_SUCCESS) return error;
          }
        return CL_SUCCESS;
      },
      event, {}, allocationUses(std::move(used)));
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMemcpy(cl_command_queue queueValue, cl_bool blocking, void *dst, const void *src, size_t size,
                                                   cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!dst || !src) return CL_INVALID_VALUE;
  const auto destinationAddress = reinterpret_cast<uintptr_t>(dst);
  const auto sourceAddress = reinterpret_cast<uintptr_t>(src);
  if ((destinationAddress >= sourceAddress && destinationAddress - sourceAddress < size)
      || (sourceAddress >= destinationAddress && sourceAddress - destinationAddress < size))
    return CL_MEM_COPY_OVERLAP;
  size_t destinationOffset = 0;
  size_t sourceOffset = 0;
  auto destinationAllocation = findPointer(dst, &destinationOffset);
  auto sourceAllocation = findPointer(src, &sourceOffset);
  if (destinationAllocation && !validRange(destinationAllocation, destinationOffset, size)) return CL_INVALID_VALUE;
  if (sourceAllocation && !validRange(sourceAllocation, sourceOffset, size)) return CL_INVALID_VALUE;
  return submitCommand(
      queue, CL_COMMAND_SVM_MEMCPY, blocking, count, waits,
      [=] {
        cl_int result = CL_SUCCESS;
        // For an SVM-to-host copy, the remote mirror is the source of truth after a
        // kernel has written the allocation.  Keep the host-visible bytes as the
        // source for SVM-to-SVM copies; those are commonly used to upload host-side
        // changes between launches.
        if (sourceAllocation && !destinationAllocation) {
          result = readAllocation(sourceAllocation, sourceOffset, static_cast<uint8_t *>(sourceAllocation->data()) + sourceOffset, size);
          if (result != CL_SUCCESS) return result;
        }
        if (destinationAllocation) {
          const void *source = !sourceAllocation ? src : static_cast<const uint8_t *>(sourceAllocation->data()) + sourceOffset;
          std::memmove(static_cast<uint8_t *>(destinationAllocation->data()) + destinationOffset, source, size);
          result = writeAllocation(destinationAllocation, destinationOffset,
                                   static_cast<uint8_t *>(destinationAllocation->data()) + destinationOffset, size);
          if (result != CL_SUCCESS) return result;
        } else {
          const void *source = !sourceAllocation ? src : static_cast<const uint8_t *>(sourceAllocation->data()) + sourceOffset;
          std::memcpy(dst, source, size);
        }
        return CL_SUCCESS;
      },
      event, {}, allocationUses({sourceAllocation, destinationAllocation}));
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMemFill(cl_command_queue queueValue, void *pointer, const void *pattern, size_t patternSize,
                                                    size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto waited = validateWaitList(queue, count, waits);
  if (waited != CL_SUCCESS) return waited;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  if (!pointer || !pattern || patternSize == 0 || size % patternSize != 0 || (allocation && !validRange(allocation, offset, size)))
    return CL_INVALID_VALUE;
  std::vector<uint8_t> const copiedPattern(static_cast<const uint8_t *>(pattern), static_cast<const uint8_t *>(pattern) + patternSize);
  return submitCommand(
      queue, CL_COMMAND_SVM_MEMFILL, false, count, waits,
      [=] {
        auto *bytes = allocation ? static_cast<uint8_t *>(allocation->data()) + offset : static_cast<uint8_t *>(pointer);
        for (size_t index = 0; index < size; ++index)
          bytes[index] = copiedPattern[index % patternSize];
        if (!allocation) return CL_SUCCESS;
        auto result = writeAllocation(allocation, offset, bytes, size);
        if (result != CL_SUCCESS) return result;
        return CL_SUCCESS;
      },
      event, {}, allocationUses({allocation}));
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMap(cl_command_queue queueValue, cl_bool blocking, cl_map_flags, void *pointer, size_t size,
                                                cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  if (!pointer || size == 0 || (allocation && !validRange(allocation, offset, size))) return CL_INVALID_VALUE;
  auto mapping = std::make_shared<BufferMapping>();
  mapping->offset = offset;
  mapping->size = size;
  mapping->pointer = pointer;
  const auto key = reinterpret_cast<uintptr_t>(pointer);
  {
    std::scoped_lock const lock(allocationsMutex);
    if (!svmMappings.emplace(key, mapping).second) return CL_INVALID_OPERATION;
  }
  return submitCommand(
      queue, CL_COMMAND_SVM_MAP, blocking, count, waits,
      [=] { return allocation ? readAllocation(allocation, offset, pointer, size) : CL_SUCCESS; }, event,
      [=](cl_int status) {
        if (status == CL_SUCCESS) return;
        std::scoped_lock const lock(allocationsMutex);
        const auto found = svmMappings.find(key);
        if (found != svmMappings.end() && found->second == mapping) svmMappings.erase(found);
      },
      allocationUses({allocation}));
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMUnmap(cl_command_queue queueValue, void *pointer, cl_uint count, const cl_event *waits,
                                                  cl_event *event) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  std::shared_ptr<BufferMapping> mapping;
  const auto key = reinterpret_cast<uintptr_t>(pointer);
  {
    std::scoped_lock const lock(allocationsMutex);
    const auto found = svmMappings.find(key);
    if (found == svmMappings.end() || found->second->unmapping) return CL_INVALID_VALUE;
    mapping = found->second;
    if (allocation && (!validRange(allocation, mapping->offset, mapping->size) || offset != mapping->offset)) return CL_INVALID_VALUE;
    mapping->unmapping = true;
  }
  return submitCommand(
      queue, CL_COMMAND_SVM_UNMAP, false, count, waits,
      [=] { return allocation ? writeAllocation(allocation, mapping->offset, mapping->pointer, mapping->size) : CL_SUCCESS; }, event,
      [=](cl_int status) {
        std::scoped_lock const lock(allocationsMutex);
        const auto found = svmMappings.find(key);
        if (found != svmMappings.end() && found->second == mapping) {
          if (status == CL_SUCCESS) svmMappings.erase(found);
          else mapping->unmapping = false;
        }
      },
      allocationUses({allocation}));
}

} // extern "C"
namespace {
void *allocateUSM(cl_context contextValue, cl_device_id deviceValue, cl_unified_shared_memory_type_intel type, size_t size,
                  cl_uint alignment, cl_int *error) {
  auto *context = checked(contextValue);
  if (!context) return fail(error, CL_INVALID_CONTEXT);
  if (type != CL_MEM_TYPE_HOST_INTEL && (deviceValue || type == CL_MEM_TYPE_DEVICE_INTEL) && !validDevice(deviceValue))
    return fail(error, CL_INVALID_DEVICE);
  auto allocation = allocate(size, error, alignment, type != CL_MEM_TYPE_DEVICE_INTEL);
  if (allocation) {
    allocation->runtimeOwnsBase = true;
    allocation->svmContextIdentity = context->identity;
    allocation->usmType = type;
  }
  return !allocation ? nullptr : allocation->data();
}
} // namespace
extern "C" {

CL_API_ENTRY void *CL_API_CALL clHostMemAllocINTEL(cl_context context, const cl_mem_properties_intel *, size_t size, cl_uint alignment,
                                                   cl_int *error) {
  return allocateUSM(context, nullptr, CL_MEM_TYPE_HOST_INTEL, size, alignment, error);
}
CL_API_ENTRY void *CL_API_CALL clDeviceMemAllocINTEL(cl_context context, cl_device_id device, const cl_mem_properties_intel *, size_t size,
                                                     cl_uint alignment, cl_int *error) {
  return allocateUSM(context, device, CL_MEM_TYPE_DEVICE_INTEL, size, alignment, error);
}
CL_API_ENTRY void *CL_API_CALL clSharedMemAllocINTEL(cl_context context, cl_device_id device, const cl_mem_properties_intel *, size_t size,
                                                     cl_uint alignment, cl_int *error) {
  return allocateUSM(context, device, CL_MEM_TYPE_SHARED_INTEL, size, alignment, error);
}
} // extern "C"
namespace {
cl_int freeUSM(cl_context contextValue, void *pointer, bool blocking) {
  auto *context = checked(contextValue);
  if (!context) return CL_INVALID_CONTEXT;
  if (!pointer) return CL_SUCCESS;
  auto allocation = findPointer(pointer);
  if (!allocation || allocation->data() != pointer || allocation->usmType == CL_MEM_TYPE_UNKNOWN_INTEL
      || allocation->svmContextIdentity != context->identity)
    return CL_INVALID_VALUE;
  if (blocking) {
    std::unique_lock lock(allocation->useMutex);
    allocation->usesFinished.wait(lock, [&] { return allocation->pendingUses == 0; });
  }
  return releaseAllocation(allocation, true);
}
} // namespace
extern "C" {

CL_API_ENTRY cl_int CL_API_CALL clMemFreeINTEL(cl_context context, void *pointer) { return freeUSM(context, pointer, false); }
CL_API_ENTRY cl_int CL_API_CALL clMemBlockingFreeINTEL(cl_context context, void *pointer) { return freeUSM(context, pointer, true); }
CL_API_ENTRY cl_int CL_API_CALL clGetMemAllocInfoINTEL(cl_context contextValue, const void *pointer, cl_mem_info_intel name, size_t size,
                                                       void *output, size_t *written) {
  auto *context = checked(contextValue);
  if (!context) return CL_INVALID_CONTEXT;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  if (!allocation || allocation->usmType == CL_MEM_TYPE_UNKNOWN_INTEL || allocation->svmContextIdentity != context->identity
      || offset >= allocation->size) {
    if (name == CL_MEM_ALLOC_TYPE_INTEL)
      return outputScalar(size, output, written, cl_unified_shared_memory_type_intel{CL_MEM_TYPE_UNKNOWN_INTEL});
    if (name == CL_MEM_ALLOC_DEVICE_INTEL) return outputScalar(size, output, written, cl_device_id{});
    return CL_INVALID_VALUE;
  }
  switch (name) {
    case CL_MEM_ALLOC_TYPE_INTEL: return outputScalar(size, output, written, allocation->usmType);
    case CL_MEM_ALLOC_BASE_PTR_INTEL: return outputScalar(size, output, written, allocation->data());
    case CL_MEM_ALLOC_SIZE_INTEL: return outputScalar(size, output, written, allocation->size);
    case CL_MEM_ALLOC_DEVICE_INTEL: {
      cl_device_id v = allocation->usmType == CL_MEM_TYPE_HOST_INTEL ? nullptr : reinterpret_cast<cl_device_id>(&device);
      return outputScalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArgMemPointerINTEL(cl_kernel value, cl_uint index, const void *pointer) {
  return clSetKernelArgSVMPointer(value, index, pointer);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemcpyINTEL(cl_command_queue queueValue, cl_bool blocking, void *dst, const void *src, size_t size,
                                                     cl_uint count, const cl_event *waits, cl_event *event) {
  return clEnqueueSVMMemcpy(queueValue, blocking, dst, src, size, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemFillINTEL(cl_command_queue queueValue, void *dst, const void *pattern, size_t patternSize,
                                                      size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  return clEnqueueSVMMemFill(queueValue, dst, pattern, patternSize, size, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemAdviseINTEL(cl_command_queue queueValue, const void *pointer, size_t size, cl_mem_advice_intel,
                                                        cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  if (!validRange(allocation, offset, size)) return CL_INVALID_VALUE;
  return submitCommand(
      queue, CL_COMMAND_MEMADVISE_INTEL, false, count, waits, [] { return CL_SUCCESS; }, event, {}, allocationUses({allocation}));
}

CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddress(const char *name) {
  if (!name) return nullptr;
#define GEMSTONE_EXTENSION(function)                                                                                                       \
  if (std::strcmp(name, #function) == 0) return reinterpret_cast<void *>(&(function))
  GEMSTONE_EXTENSION(clIcdGetPlatformIDsKHR);
  GEMSTONE_EXTENSION(clGetKernelSubGroupInfoKHR);
  GEMSTONE_EXTENSION(clGetKernelSuggestedLocalWorkSizeKHR);
  GEMSTONE_EXTENSION(clCreateCommandQueueWithPropertiesKHR);
  GEMSTONE_EXTENSION(clHostMemAllocINTEL);
  GEMSTONE_EXTENSION(clDeviceMemAllocINTEL);
  GEMSTONE_EXTENSION(clSharedMemAllocINTEL);
  GEMSTONE_EXTENSION(clMemFreeINTEL);
  GEMSTONE_EXTENSION(clMemBlockingFreeINTEL);
  GEMSTONE_EXTENSION(clGetMemAllocInfoINTEL);
  GEMSTONE_EXTENSION(clSetKernelArgMemPointerINTEL);
  GEMSTONE_EXTENSION(clEnqueueMemFillINTEL);
  GEMSTONE_EXTENSION(clEnqueueMemcpyINTEL);
  GEMSTONE_EXTENSION(clEnqueueMemAdviseINTEL);
  GEMSTONE_EXTENSION(clCreateProgramWithIL);
  GEMSTONE_EXTENSION(clSetKernelArgSVMPointer);
  GEMSTONE_EXTENSION(clSetKernelExecInfo);
#undef GEMSTONE_EXTENSION
  return nullptr;
}
CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddressForPlatform(cl_platform_id value, const char *name) {
  return validPlatform(value) ? clGetExtensionFunctionAddress(name) : nullptr;
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelSubGroupInfo(cl_kernel value, cl_device_id selected, cl_kernel_sub_group_info name,
                                                        size_t inputSize, const void *input, size_t size, void *output, size_t *written) {
  if (!checked(value)) return CL_INVALID_KERNEL;
  // OpenCL permits a null device when the kernel's program has one device.
  if (selected && !validDevice(selected)) return CL_INVALID_DEVICE;
  constexpr size_t subgroupSize = MaxSubgroupSize;
  if (name == CL_KERNEL_MAX_SUB_GROUP_SIZE_FOR_NDRANGE) return outputScalar(size, output, written, subgroupSize);
  if (name == CL_KERNEL_MAX_NUM_SUB_GROUPS) return outputScalar(size, output, written, MaxWorkGroupSize / subgroupSize);
  if (name == CL_KERNEL_COMPILE_NUM_SUB_GROUPS) return outputScalar(size, output, written, size_t{0});
  if (name == CL_KERNEL_COMPILE_SUB_GROUP_SIZE_INTEL) return outputScalar(size, output, written, size_t{0});
  if (name == CL_KERNEL_SUB_GROUP_COUNT_FOR_NDRANGE) {
    if (!input || inputSize == 0 || inputSize % sizeof(size_t) != 0 || inputSize > 3 * sizeof(size_t)) return CL_INVALID_VALUE;
    const size_t dimensions = inputSize / sizeof(size_t);
    const auto *local = static_cast<const size_t *>(input);
    size_t workItems = 1;
    for (size_t index = 0; index < dimensions; ++index) {
      if (local[index] == 0 || workItems > std::numeric_limits<size_t>::max() / local[index]) return CL_INVALID_VALUE;
      workItems *= local[index];
    }
    const size_t v = (workItems + subgroupSize - 1) / subgroupSize;
    return outputScalar(size, output, written, v);
  }
  if (name == CL_KERNEL_LOCAL_SIZE_FOR_SUB_GROUP_COUNT) {
    if (!input || inputSize != sizeof(size_t) || !output || size < sizeof(size_t)) return CL_INVALID_VALUE;
    size_t count = 0;
    std::memcpy(&count, input, sizeof(count));
    size_t local[3] = {0, 0, 0};
    if (count != 0 && count <= MaxWorkGroupSize / subgroupSize) {
      local[0] = count * subgroupSize;
      local[1] = 1;
      local[2] = 1;
    }
    return outputBytes(size, output, written, local, size < 3 * sizeof(size_t) ? size : 3 * sizeof(size_t));
  }
  return CL_INVALID_VALUE;
}

CL_API_ENTRY cl_int CL_API_CALL clGetKernelSuggestedLocalWorkSizeKHR(cl_command_queue queueValue, cl_kernel kernelValue,
                                                                     cl_uint workDimension, const size_t *offset, const size_t *global,
                                                                     size_t *suggested) {
  auto *queue = checked(queueValue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto *kernel = checked(kernelValue);
  if (!kernel) return CL_INVALID_KERNEL;
  if (kernel->program->context != queue->context) return CL_INVALID_CONTEXT;
  if (workDimension == 0 || workDimension > 3) return CL_INVALID_WORK_DIMENSION;
  if (!global || std::any_of(global, global + workDimension, [](size_t size) { return size == 0; })) return CL_INVALID_GLOBAL_WORK_SIZE;
  if (offset && std::any_of(offset, offset + workDimension, [](size_t value) { return value > std::numeric_limits<int32_t>::max(); }))
    return CL_INVALID_GLOBAL_OFFSET;
  if (!suggested) return CL_INVALID_VALUE;
  if (kernel->argumentsSet ^ contains(false)) return CL_INVALID_KERNEL_ARGS;
  for (cl_uint index = 0; index < workDimension; ++index)
    suggested[index] = implicitGroup(*kernel, index, global[index]);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_command_queue CL_API_CALL clCreateCommandQueueWithPropertiesKHR(cl_context context, cl_device_id device,
                                                                                const cl_queue_properties_khr *properties, cl_int *error) {
  return clCreateCommandQueueWithProperties(context, device, properties, error);
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelSubGroupInfoKHR(cl_kernel value, cl_device_id selected, cl_kernel_sub_group_info name,
                                                           size_t inputSize, const void *input, size_t size, void *output,
                                                           size_t *written) {
  return clGetKernelSubGroupInfo(value, selected, name, inputSize, input, size, output, written);
}

} // extern "C"

namespace {

void initialiseDispatch() {
  std::call_once(dispatchInitialised, [] {
    registerLiveObject(&platform);
    registerLiveObject(&device);
    platform.dispatch = &dispatch;
    device.dispatch = &dispatch;
    device.platform = &platform;
    gemstone::opencl::populateDispatch(dispatch);
  });
}

} // namespace
