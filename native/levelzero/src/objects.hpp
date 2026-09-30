#ifndef GEMSTONE_LEVELZERO_OBJECTS_HPP
#define GEMSTONE_LEVELZERO_OBJECTS_HPP

#include <array>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <deque>
#include <functional>
#include <limits>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <utility>
#include <vector>

#include <stddef.h>
#include <stdint.h>

#include <ze_api.h>

#include "handles.hpp"

namespace gemstone::levelzero {

enum class Kind : uint32_t { Driver, Device, Context, Queue, List, EventPool, Event, Fence, Module, BuildLog, Kernel, PhysicalMemory };

inline gemstone::rpc::LiveHandles liveHandles;

struct Handle {
  explicit Handle(Kind value) : kind(value) { liveHandles.insert(this); }
  virtual ~Handle() { liveHandles.erase(this); }
  uint64_t magic = gemstone::rpc::HandleMagic;
  Kind kind;
  std::atomic<uint32_t> users{0};
  std::atomic<bool> destroyRequested{false};
  std::mutex usersMutex;
  std::condition_variable usersChanged;
};

template <class T> class HandleUse {
public:
  HandleUse() = default;
  explicit HandleUse(T *value) : value_(value) {
    if (value_) value_->users.fetch_add(1, std::memory_order_relaxed);
  }
  HandleUse(const HandleUse &other) : HandleUse(other.value_) {}
  HandleUse(HandleUse &&other) noexcept : value_(std::exchange(other.value_, nullptr)) {}
  HandleUse &operator=(HandleUse other) noexcept {
    swap(other);
    return *this;
  }
  ~HandleUse() {
    if (!value_) return;
    bool reclaim = false;
    {
      std::lock_guard lock(value_->usersMutex);
      const bool last = value_->users.fetch_sub(1, std::memory_order_acq_rel) == 1;
      reclaim = last && value_->destroyRequested.load(std::memory_order_acquire);
      // A waiter may destroy the handle as soon as this lock is released.
      value_->usersChanged.notify_all();
    }
    if (reclaim) delete value_;
  }
  T *get() const { return value_; }
  T *operator->() const { return value_; }
  explicit operator bool() const { return value_; }
  void swap(HandleUse &other) noexcept { std::swap(value_, other.value_); }

private:
  T *value_ = nullptr;
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
  Driver() : Handle(Kind::Driver) {}
};
struct Device final : Handle {
  Device() : Handle(Kind::Device) {}
};
inline std::atomic<uint64_t> nextContextId{1};
struct Context final : Handle {
  Context() : Handle(Kind::Context), id(nextContextId.fetch_add(1, std::memory_order_relaxed)) {}
  uint64_t id;
};
struct EventPool final : Handle {
  EventPool(Context *contextValue, ze_event_pool_flags_t value) : Handle(Kind::EventPool), contextId(contextValue->id), flags(value) {}
  uint64_t contextId;
  ze_event_pool_flags_t flags;
};

struct Event final : Handle {
  explicit Event(EventPool *poolValue = nullptr) : Handle(Kind::Event), pool(poolValue) {}
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

/** Drains a `pending` queue on a worker thread, recording the first failure and the completed count. */
template <class Derived, class Job> struct WorkerLoop {
  void run() {
    for (;;) {
      Job job;
      {
        std::unique_lock lock(mutex);
        changed.wait(lock, [this] { return stopping || !pending.empty(); });
        if (stopping && pending.empty()) return;
        job = std::move(pending.front());
        pending.pop_front();
      }
      ze_result_t result = ZE_RESULT_ERROR_UNKNOWN;
      try {
        result = static_cast<Derived *>(this)->execute(job);
      } catch (...) {
        result = ZE_RESULT_ERROR_UNKNOWN;
      }
      {
        std::lock_guard lock(mutex);
        if (result != ZE_RESULT_SUCCESS && failure == ZE_RESULT_SUCCESS) failure = result;
        ++completed;
      }
      changed.notify_all();
    }
  }

  /** Waits for the submitted work to drain, honouring `0` (poll) and `UINT64_MAX` (block) timeouts. */
  ze_result_t waitFor(uint64_t timeout) {
    if (!worker.joinable()) return ZE_RESULT_SUCCESS;
    std::unique_lock lock(mutex);
    const auto target = submitted;
    const auto finished = [&] { return completed >= target; };
    if (timeout == 0) {
      if (!finished()) return ZE_RESULT_NOT_READY;
    } else if (timeout == std::numeric_limits<uint64_t>::max()) {
      changed.wait(lock, finished);
    } else if (!changed.wait_for(
                   lock, std::chrono::nanoseconds(static_cast<int64_t>(std::min(timeout, uint64_t{std::numeric_limits<int64_t>::max()}))),
                   finished)) {
      return ZE_RESULT_NOT_READY;
    }
    return failure;
  }

  std::mutex mutex;
  std::condition_variable changed;
  std::deque<Job> pending;
  std::thread worker;
  uint64_t submitted = 0;
  uint64_t completed = 0;
  ze_result_t failure = ZE_RESULT_SUCCESS;
  bool stopping = false;
};

struct CommandList final : Handle, WorkerLoop<CommandList, RecordedCommand> {
  CommandList(bool immediateValue, Context *contextValue, Device *deviceValue,
              ze_command_queue_mode_t modeValue = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS)
      : Handle(Kind::List), immediate(immediateValue), context(contextValue), device(deviceValue) {
    if (immediate && modeValue != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS) worker = std::thread([this] { run(); });
  }
  ~CommandList() override {
    if (!worker.joinable()) return;
    {
      std::lock_guard lock(mutex);
      stopping = true;
    }
    changed.notify_all();
    worker.join();
  }
  ze_result_t execute(RecordedCommand &command) {
    const auto result = command.execute(failure);
    command = {};
    return result;
  }
  bool immediate;
  bool closed = false;
  HandleUse<Context> context;
  Device *device;
  std::vector<RecordedCommand> commands;
};

struct Queue final : Handle, WorkerLoop<Queue, std::function<ze_result_t()>> {
  Queue(Context *contextValue, Device *deviceValue, const ze_command_queue_desc_t &value)
      : Handle(Kind::Queue), context(contextValue), device(deviceValue), desc(value) {
    if (desc.mode != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS) worker = std::thread([this] { run(); });
  }
  ~Queue() override {
    if (!worker.joinable()) return;
    {
      std::lock_guard lock(mutex);
      stopping = true;
    }
    changed.notify_all();
    worker.join();
  }
  ze_result_t execute(std::function<ze_result_t()> &job) {
    ze_result_t result = ZE_RESULT_ERROR_UNKNOWN;
    try {
      result = job();
    } catch (...) {
      result = ZE_RESULT_ERROR_UNKNOWN;
    }
    job = {};
    return result;
  }
  HandleUse<Context> context;
  Device *device;
  ze_command_queue_desc_t desc;
};

struct Fence final : Handle {
  explicit Fence(Queue *queueValue) : Handle(Kind::Fence), queue(queueValue) {}
  HandleUse<Queue> queue;
  Event event;
  // Reset clears the signal, but a completed submission may still retain the
  // fence while its worker releases internal references.
  bool pending = false;
  ze_result_t completionResult = ZE_RESULT_SUCCESS;
};

struct BuildLog final : Handle {
  explicit BuildLog(std::string value) : Handle(Kind::BuildLog), text(std::move(value)) {}
  std::string text;
};

struct Module final : Handle {
  Module(Context *contextValue, std::vector<uint8_t> value, uint64_t programValue, std::string buildFlagsValue = {})
      : Handle(Kind::Module), context(contextValue), bytes(std::move(value)), programId(programValue),
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
  Kernel(Module *moduleValue, std::string nameValue) : Handle(Kind::Kernel), module(moduleValue), name(std::move(nameValue)) {}
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

#endif
