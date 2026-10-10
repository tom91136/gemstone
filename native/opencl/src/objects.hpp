#pragma once

#include <array>
#include <atomic>
#include <condition_variable>
#include <map>
#include <memory>
#include <mutex>
#include <new>
#include <optional>
#include <set>
#include <string>
#include <utility>
#include <vector>

#include <stddef.h>
#include <stdint.h>

#include <CL/cl_icd.h>

#include <gemstone/command_queue.hpp>

#include "compiler.hpp"

enum class Kind : uint32_t { Platform, Device, Context, Queue, Memory, Program, Kernel, Event, Sampler };

struct Header {
  std::atomic_uint references{1};
};

struct _cl_platform_id {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Platform;
  Header header;
};

struct _cl_device_id {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Device;
  Header header;
  _cl_platform_id *platform = nullptr;
};

struct _cl_context {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Context;
  Header header;
  uint64_t identity = 0;
  _cl_device_id *device = nullptr;
  std::vector<cl_context_properties> properties;
  std::vector<std::pair<void(CL_CALLBACK *)(cl_context, void *), void *>> destructorCallbacks;
  _cl_command_queue *defaultDeviceQueue = nullptr;
};

// Image mappings copy rows between the tightly packed texels and the mapped pitches.
struct ImageRows {
  std::array<size_t, 3> region{};
  size_t imageRow = 0;
  size_t imageSlice = 0;
  size_t hostRow = 0;
  size_t hostSlice = 0;
};

struct BufferMapping {
  size_t offset = 0;
  size_t size = 0;
  void *pointer = nullptr;
  std::shared_ptr<uint8_t[]> storage;
  bool unmapping = false;
  std::optional<ImageRows> rows;
};

struct Allocation {
  struct AlignedDelete {
    size_t alignment = alignof(max_align_t);
    void operator()(void *value) const noexcept { ::operator delete(value, std::align_val_t{alignment}); }
  };

  [[nodiscard]] void *data() const noexcept { return storage.get(); }

  std::unique_ptr<void, AlignedDelete> storage{nullptr, AlignedDelete{}};
  size_t size = 0;
  uint64_t id = 0;
  bool hostBacked = false;
  std::mutex useMutex;
  std::condition_variable usesFinished;
  size_t pendingUses = 0;

  std::atomic_bool released{false};
  bool runtimeOwnsBase = false;
  uint64_t svmContextIdentity = 0;
  cl_unified_shared_memory_type_intel usmType = CL_MEM_TYPE_UNKNOWN_INTEL;
  // Host-visible buffers can be copied by the SYCL runtime without updating
  // their host mirror.  Preserve pointer identities discovered by the
  // relocation scan so copied aggregate arguments remain relocatable.
  std::map<size_t, std::pair<std::shared_ptr<Allocation>, size_t>> pointerRelocations;
};

// One lease per enqueued command, released on both execution and dependency failure.
struct AllocationUses {
  std::vector<std::shared_ptr<Allocation>> allocations;
  explicit AllocationUses(std::vector<std::shared_ptr<Allocation>> values) : allocations(std::move(values)) {
    for (const auto &allocation : allocations) {
      if (!allocation) continue;
      std::lock_guard lock(allocation->useMutex);
      ++allocation->pendingUses;
    }
  }
  ~AllocationUses() {
    for (const auto &allocation : allocations) {
      if (!allocation) continue;
      {
        std::lock_guard lock(allocation->useMutex);
        --allocation->pendingUses;
      }
      allocation->usesFinished.notify_all();
    }
  }
};

struct _cl_command_queue {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Queue;
  Header header;
  _cl_context *context = nullptr;
  _cl_device_id *device = nullptr;
  cl_command_queue_properties properties = 0;
  std::vector<cl_queue_properties> propertiesArray;
  // Non-zero for on-device queues, which only kernels enqueue to.
  cl_uint deviceSize = 0;
  void *stream = nullptr;
  size_t unobservedFailures = 0;
};

// Texels are tightly packed: rows of `rowPitch` bytes, then slices or array layers of `slicePitch` bytes.
struct ImageShape {
  cl_image_format format{};
  cl_image_desc desc{};
  size_t elementSize = 0;
  size_t rowPitch = 0;
  size_t slicePitch = 0;
  size_t hostRowPitch = 0;
  size_t hostSlicePitch = 0;
  std::shared_ptr<Allocation> record;
};

struct _cl_mem {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Memory;
  Header header;
  _cl_context *context = nullptr;
  std::shared_ptr<Allocation> allocation;
  cl_mem_flags flags = 0;
  void *hostPointer = nullptr;
  std::vector<cl_mem_properties> properties;
  _cl_mem *parent = nullptr;
  size_t origin = 0;
  size_t size = 0;
  std::multimap<uintptr_t, std::shared_ptr<BufferMapping>> mappings;
  std::vector<std::pair<void(CL_CALLBACK *)(cl_mem, void *), void *>> destructorCallbacks;
  std::optional<ImageShape> image;
  std::optional<std::pair<cl_uint, cl_uint>> pipe; // Packet size and capacity.
};

struct _cl_sampler {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Sampler;
  Header header;
  _cl_context *context = nullptr;
  cl_bool normalised = CL_TRUE;
  cl_addressing_mode addressing = CL_ADDRESS_CLAMP;
  cl_filter_mode filter = CL_FILTER_NEAREST;
  std::vector<cl_sampler_properties> properties;
  std::shared_ptr<Allocation> record;
};

struct _cl_program {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Program;
  Header header;
  _cl_context *context = nullptr;
  std::string source;
  std::vector<uint8_t> il;
  std::vector<uint8_t> originalIL;
  std::map<cl_uint, std::vector<uint8_t>> specializationConstants;
  std::map<cl_uint, size_t> specializationSizes;
  bool specializationSizesLoaded = false;
  bool createdWithIL = false;
  std::vector<uint8_t> bitcode;
  uint64_t remoteId = 0;
  std::vector<std::shared_ptr<Allocation>> globals;
  size_t globalBytes = 0;
  cl_build_status buildStatus = CL_BUILD_NONE;
  cl_program_binary_type binaryType = CL_PROGRAM_BINARY_TYPE_NONE;
  std::string buildSource;
  std::string kernelNames;
  std::string buildOptions;
  std::string buildLog;
};

struct KernelArgument {
  enum class Kind { Scalar, Allocation, Local, SVM } kind = Kind::Scalar;
  std::vector<uint8_t> bytes;
  std::shared_ptr<Allocation> allocation;
  _cl_mem *memory = nullptr;      // Non-owning until an enqueue snapshots and retains it.
  _cl_sampler *sampler = nullptr; // Likewise.
  size_t offset = 0;
  // Image and sampler records are bounded, and an image record addresses its texels.
  std::optional<size_t> range;
  std::shared_ptr<Allocation> texels;
  size_t texelOffset = 0;
  // A fine-grained system SVM pointer that Gemstone did not allocate, passed by address.
  bool systemPointer = false;
};

struct KernelParameterMetadata {
  bool pointerAggregate = false;
  bool local = false;
  bool containsPointer = false;
};

struct _cl_kernel {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Kernel;
  Header header;
  _cl_program *program = nullptr;
  std::string name;
  size_t expectedArguments = 0;
  std::array<size_t, 3> requiredWorkGroupSize{0, 0, 0};
  size_t staticLocalMemory = 0;
  std::string attributes;
  std::vector<size_t> expectedArgumentSizes;
  std::vector<KernelParameterMetadata> parameterMetadata;
  std::vector<gemstone::opencl::KernelArgumentInfo> argumentInfo;
  std::vector<KernelArgument> arguments;
  std::vector<bool> argumentsSet;
  std::map<cl_kernel_exec_info, std::vector<std::shared_ptr<Allocation>>> indirectAllocations;
  std::set<cl_kernel_exec_info> indirectTypes;
  bool indirectSystem = false;
};

struct _cl_event {
  const cl_icd_dispatch *dispatch = nullptr;
  static constexpr Kind ObjectKind = Kind::Event;
  Header header;
  _cl_command_queue *queue = nullptr;
  _cl_context *context = nullptr;
  cl_command_type command = CL_COMMAND_MARKER;
  cl_int status = CL_COMPLETE;
  bool failureObserved = false;
  gemstone::CommandQueue::Ticket ticket;
  uint64_t queued = 0;
  uint64_t submitted = 0;
  uint64_t started = 0;
  uint64_t ended = 0;
  struct Callback {
    cl_int trigger;
    void(CL_CALLBACK *function)(cl_event, cl_int, void *);
    void *user;
  };
  std::vector<Callback> callbacks;
};
