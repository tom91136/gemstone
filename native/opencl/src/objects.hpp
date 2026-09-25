#ifndef GEMSTONE_OPENCL_OBJECTS_HPP
#define GEMSTONE_OPENCL_OBJECTS_HPP

#include <array>
#include <atomic>
#include <condition_variable>
#include <deque>
#include <functional>
#include <map>
#include <memory>
#include <mutex>
#include <new>
#include <set>
#include <string>
#include <utility>
#include <vector>

#include <stddef.h>
#include <stdint.h>

#include <CL/cl_icd.h>

#include "compiler.hpp"

inline constexpr uint64_t Magic = static_cast<uint64_t>(0x47454d53544f4e45);

enum class Kind : uint32_t { Platform, Device, Context, Queue, Memory, Program, Kernel, Event };

struct Header {
  explicit Header(Kind value) : kind(value) {}
  uint64_t magic = Magic;
  Kind kind;
  std::atomic_uint references{1};
};

struct _cl_platform_id {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Platform};
};

struct _cl_device_id {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Device};
  _cl_platform_id *platform = nullptr;
};

struct _cl_context {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Context};
  uint64_t identity = 0;
  _cl_device_id *device = nullptr;
  std::vector<cl_context_properties> properties;
  std::vector<std::pair<void(CL_CALLBACK *)(cl_context, void *), void *>> destructorCallbacks;
};

struct BufferMapping {
  size_t offset = 0;
  size_t size = 0;
  void *pointer = nullptr;
  std::shared_ptr<uint8_t[]> storage;
  bool unmapping = false;
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
  std::atomic_bool externallyMutable{false};
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

struct PendingCommand {
  std::vector<cl_event> dependencies;
  cl_event completion = nullptr;
  std::function<cl_int()> execute;
  bool waitPrior = false;
  bool fence = false;
  std::function<void(cl_int)> finish;
  std::shared_ptr<AllocationUses> uses;
};

struct _cl_command_queue {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Queue};
  _cl_context *context = nullptr;
  _cl_device_id *device = nullptr;
  cl_command_queue_properties properties = 0;
  std::vector<cl_queue_properties> propertiesArray;
  std::mutex pendingMutex;
  std::condition_variable pendingCondition;
  size_t pendingCommands = 0;
  size_t unobservedFailures = 0;
  bool workerRunning = false;
  std::deque<PendingCommand> commands;
};

struct _cl_mem {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Memory};
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
};

struct _cl_program {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Program};
  _cl_context *context = nullptr;
  std::string source;
  std::vector<uint8_t> il;
  std::vector<uint8_t> originalIL;
  std::map<cl_uint, std::vector<uint8_t>> specializationConstants;
  bool createdWithIL = false;
  std::vector<uint8_t> bitcode;
  uint64_t remoteId = 0;
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
  _cl_mem *memory = nullptr; // Non-owning until an enqueue snapshots and retains it.
  size_t offset = 0;
};

struct KernelParameterMetadata {
  bool pointerAggregate = false;
  bool local = false;
  bool containsPointer = false;
};

struct _cl_kernel {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Kernel};
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
};

struct _cl_event {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Event};
  _cl_command_queue *queue = nullptr;
  _cl_context *context = nullptr;
  cl_command_type command = CL_COMMAND_MARKER;
  cl_int status = CL_COMPLETE;
  bool failureObserved = false;
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

#endif
