#pragma once

#include <array>
#include <atomic>
#include <condition_variable>
#include <functional>
#include <memory>
#include <mutex>
#include <string>
#include <utility>
#include <vector>

#include <stddef.h>
#include <stdint.h>

#include <ze_api.h>

#include <gemstone/command_queue.hpp>
#include <gemstone/live_handles.hpp>

namespace gemstone::levelzero {

enum class Kind : uint32_t { Driver, Device, Context, Queue, List, EventPool, Event, Fence, Module, BuildLog, Kernel, PhysicalMemory };

inline gemstone::LiveHandles<Kind> liveHandles;

struct Handle {
  explicit Handle(Kind kind) { liveHandles.insert(this, kind); }
  virtual ~Handle() { liveHandles.erase(this); }
  std::atomic<uint32_t> users{0};
  std::atomic<bool> destroyRequested{false};
  std::mutex usersMutex;
  std::condition_variable usersChanged;
};

template <class T> class HandleUse {
public:
  HandleUse() = default;
  explicit HandleUse(T *value) : handle(value) {
    if (handle) handle->users.fetch_add(1, std::memory_order_relaxed);
  }
  HandleUse(const HandleUse &other) : HandleUse(other.handle) {}
  HandleUse(HandleUse &&other) noexcept : handle(std::exchange(other.handle, nullptr)) {}
  HandleUse &operator=(HandleUse other) noexcept {
    swap(other);
    return *this;
  }
  ~HandleUse() {
    if (!handle) return;
    bool reclaim = false;
    {
      std::lock_guard lock(handle->usersMutex);
      const bool last = handle->users.fetch_sub(1, std::memory_order_acq_rel) == 1;
      reclaim = last && handle->destroyRequested.load(std::memory_order_acquire);
      // A waiter may destroy the handle as soon as this lock is released.
      handle->usersChanged.notify_all();
    }
    if (reclaim) delete handle;
  }
  T *get() const { return handle; }
  T *operator->() const { return handle; }
  explicit operator bool() const { return handle; }
  void swap(HandleUse &other) noexcept { std::swap(handle, other.handle); }

private:
  T *handle = nullptr;
};

template <class T> ze_result_t destroyHandle(T *value) {
  if (!value) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  {
    std::lock_guard lock(value->usersMutex);
    if (value->users.load(std::memory_order_acquire) != 0) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  }
  delete value;
  return ZE_RESULT_SUCCESS;
}

struct Driver final : Handle {
  static constexpr Kind HandleKind = Kind::Driver;
  Driver() : Handle(HandleKind) {}
};
struct Device final : Handle {
  static constexpr Kind HandleKind = Kind::Device;
  Device() : Handle(HandleKind) {}
};
inline std::atomic<uint64_t> nextContextId{1};
struct Context final : Handle {
  static constexpr Kind HandleKind = Kind::Context;
  Context() : Handle(HandleKind), id(nextContextId.fetch_add(1, std::memory_order_relaxed)) {}
  uint64_t id;
};
struct EventPool final : Handle {
  static constexpr Kind HandleKind = Kind::EventPool;
  EventPool(Context *contextValue, ze_event_pool_flags_t value) : Handle(HandleKind), contextId(contextValue->id), flags(value) {}
  uint64_t contextId;
  ze_event_pool_flags_t flags;
};

struct Event final : Handle {
  static constexpr Kind HandleKind = Kind::Event;
  explicit Event(EventPool *poolValue = nullptr) : Handle(HandleKind), pool(poolValue) {}
  HandleUse<EventPool> pool;
  std::mutex mutex;
  std::condition_variable changed;
  bool signalled = false;
  ze_result_t completionResult = ZE_RESULT_SUCCESS;
  uint64_t kernelStart = 0;
  uint64_t kernelEnd = 0;
};

using CommandAction = std::function<ze_result_t()>;

struct RecordedCommand {
  CommandAction action;
  std::function<void()> release;
  std::function<void(ze_result_t)> complete;

  ze_result_t execute(ze_result_t result = ZE_RESULT_SUCCESS) {
    if (result == ZE_RESULT_SUCCESS) {
      try {
        result = action();
      } catch (...) {
        result = ZE_RESULT_ERROR_UNKNOWN;
      }
    }
    // Completion permits callers to free allocations and destroy kernels.
    // Release execution ownership before publishing either success or failure.
    releaseResources();
    if (complete) complete(result);
    return result;
  }

  void releaseResources() {
    if (release) release();
    release = {};
  }
};

gemstone::CommandQueue &commandQueue();

/** FIFO execution on a shared-pool stream, reporting the first failure; without a stream the owner runs work inline. */
struct AsyncStream {
  explicit AsyncStream(bool asynchronous) : stream(asynchronous ? commandQueue().createStream(true) : nullptr) {}
  ~AsyncStream() {
    if (!stream) return;
    commandQueue().wait(commandQueue().snapshot(stream));
    commandQueue().destroyStream(stream);
  }
  AsyncStream(const AsyncStream &) = delete;
  AsyncStream &operator=(const AsyncStream &) = delete;

  /** `job` receives the first failure so far; its captures are released before the returned ticket completes. */
  template <class Job> gemstone::CommandQueue::Ticket submit(Job job) {
    return commandQueue().submit(stream, [this, job = std::move(job)]() mutable {
      ze_result_t result = ZE_RESULT_ERROR_UNKNOWN;
      try {
        result = job(failure.load(std::memory_order_acquire));
      } catch (...) {
      }
      auto expected = ZE_RESULT_SUCCESS;
      if (result != ZE_RESULT_SUCCESS) failure.compare_exchange_strong(expected, result, std::memory_order_acq_rel);
      return 0;
    });
  }

  /** Waits for work submitted so far; `0` polls and `UINT64_MAX` blocks. */
  bool idle(uint64_t timeout) { return !stream || commandQueue().waitFor(commandQueue().snapshot(stream), timeout).has_value(); }
  ze_result_t waitFor(uint64_t timeout) { return idle(timeout) ? failure.load(std::memory_order_acquire) : ZE_RESULT_NOT_READY; }

  void *const stream;
  std::atomic<ze_result_t> failure{ZE_RESULT_SUCCESS};
};

struct CommandList final : Handle {
  static constexpr Kind HandleKind = Kind::List;
  CommandList(bool immediateValue, Context *contextValue, Device *deviceValue,
              ze_command_queue_mode_t modeValue = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS)
      : Handle(HandleKind), immediate(immediateValue), context(contextValue), device(deviceValue),
        async(immediate && modeValue != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS) {}
  bool immediate;
  bool closed = false;
  HandleUse<Context> context;
  Device *device;
  std::vector<RecordedCommand> commands;
  // Declared last so outstanding work drains before the other members are destroyed.
  AsyncStream async;
};

struct Queue final : Handle {
  static constexpr Kind HandleKind = Kind::Queue;
  Queue(Context *contextValue, Device *deviceValue, const ze_command_queue_desc_t &value)
      : Handle(HandleKind), context(contextValue), device(deviceValue), desc(value), async(desc.mode != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS) {
  }
  HandleUse<Context> context;
  Device *device;
  ze_command_queue_desc_t desc;
  std::mutex synchronousMutex;
  // Declared last so outstanding work drains before the other members are destroyed.
  AsyncStream async;
};

struct Fence final : Handle {
  static constexpr Kind HandleKind = Kind::Fence;
  explicit Fence(Queue *queueValue) : Handle(HandleKind), queue(queueValue) {}
  HandleUse<Queue> queue;
  // Guarded by `event.mutex`; completes once the submission has released its references.
  gemstone::CommandQueue::Ticket submission;
  Event event;
  // Reset clears the signal, but a completed submission may still retain the
  // fence while its worker releases internal references.
  bool pending = false;
  ze_result_t completionResult = ZE_RESULT_SUCCESS;
};

struct BuildLog final : Handle {
  static constexpr Kind HandleKind = Kind::BuildLog;
  explicit BuildLog(std::string value) : Handle(HandleKind), text(std::move(value)) {}
  std::string text;
};

struct Module final : Handle {
  static constexpr Kind HandleKind = Kind::Module;
  Module(Context *contextValue, std::vector<uint8_t> value, uint64_t programValue, std::string buildFlagsValue = {})
      : Handle(HandleKind), context(contextValue), bytes(std::move(value)), programId(programValue),
        buildFlags(std::move(buildFlagsValue)) {}
  HandleUse<Context> context;
  std::vector<uint8_t> bytes;
  uint64_t programId;
  std::string buildFlags;
  struct Global {
    std::string name;
    void *base;
    size_t size;
    uint64_t allocationId;
  };
  std::mutex globalsMutex;
  std::vector<Global> globals;
};

struct KernelParameterMetadata {
  bool pointerAggregate = false;
  bool local = false;
  bool containsPointer = false;
};

struct Kernel final : Handle {
  static constexpr Kind HandleKind = Kind::Kernel;
  Kernel(Module *moduleValue, std::string nameValue) : Handle(HandleKind), module(moduleValue), name(std::move(nameValue)) {}
  HandleUse<Module> module;
  std::string name;
  uint32_t groupX = 1;
  uint32_t groupY = 1;
  uint32_t groupZ = 1;
  uint32_t offsetX = 0;
  uint32_t offsetY = 0;
  uint32_t offsetZ = 0;
  ze_kernel_indirect_access_flags_t indirect = 0;
  struct Argument {
    bool local = false;
    size_t localSize = 0;
    std::vector<uint8_t> bytes;
  };
  std::vector<Argument> arguments;
  std::vector<KernelParameterMetadata> parameters;
  std::array<uint32_t, 3> requiredGroupSize{};
};

struct Allocation;
using AllocationRef = std::shared_ptr<Allocation>;

struct KernelLaunch {
  std::shared_ptr<HandleUse<Kernel>> lifetime;
  Context *context;
  uint64_t programId;
  std::string name;
  uint32_t groupX;
  uint32_t groupY;
  uint32_t groupZ;
  uint32_t offsetX;
  uint32_t offsetY;
  uint32_t offsetZ;
  bool indirect;
  std::vector<Kernel::Argument> arguments;
  std::vector<KernelParameterMetadata> parameters;
  std::vector<AllocationRef> allocations;
};

struct Allocation {
  HandleUse<Context> context;
  void *base;
  size_t size;
  ze_memory_type_t type;
  uint64_t id;
};

} // namespace gemstone::levelzero
