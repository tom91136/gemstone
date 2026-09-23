#ifndef GEMSTONE_LEVELZERO_OBJECTS_HPP
#define GEMSTONE_LEVELZERO_OBJECTS_HPP

#include <atomic>
#include <condition_variable>
#include <deque>
#include <functional>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <unordered_set>
#include <utility>
#include <vector>

#include <stddef.h>
#include <stdint.h>

#include <ze_api.h>

namespace gemstone::levelzero {

inline constexpr uint64_t Magic = 0x47454d53544f4e45ULL;

enum class Kind : uint32_t { Driver, Device, Context, Queue, List, EventPool, Event, Fence, Module, BuildLog, Kernel, PhysicalMemory };

inline std::mutex liveHandlesMutex;
inline std::unordered_set<const void *> liveHandles;

struct Handle {
  explicit Handle(Kind value) : kind(value) {
    std::lock_guard lock(liveHandlesMutex);
    liveHandles.insert(this);
  }
  virtual ~Handle() {
    std::lock_guard lock(liveHandlesMutex);
    liveHandles.erase(this);
  }
  uint64_t magic = Magic;
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
    bool last = false;
    {
      std::lock_guard lock(value_->usersMutex);
      last = value_->users.fetch_sub(1, std::memory_order_acq_rel) == 1;
    }
    value_->usersChanged.notify_all();
    if (last && value_->destroyRequested.load(std::memory_order_acquire)) delete value_;
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
  if (value->users.load(std::memory_order_acquire) != 0) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
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
  uint64_t kernelStart = 0;
  uint64_t kernelEnd = 0;
};

using CommandAction = std::function<ze_result_t()>;

struct RecordedCommand {
  CommandAction action;
  std::function<void()> release;

  void releaseResources() {
    if (release) release();
    release = {};
  }
};

struct CommandList final : Handle {
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
  void run() {
    for (;;) {
      RecordedCommand command;
      {
        std::unique_lock lock(mutex);
        changed.wait(lock, [this] { return stopping || !pending.empty(); });
        if (stopping && pending.empty()) return;
        command = std::move(pending.front());
        pending.pop_front();
      }
      ze_result_t result = ZE_RESULT_ERROR_UNKNOWN;
      try {
        result = command.action();
      } catch (...) {
        result = ZE_RESULT_ERROR_UNKNOWN;
      }
      command.releaseResources();
      command = {};
      {
        std::lock_guard lock(mutex);
        if (result != ZE_RESULT_SUCCESS && failure == ZE_RESULT_SUCCESS) failure = result;
        ++completed;
      }
      changed.notify_all();
    }
  }
  bool immediate;
  bool closed = false;
  HandleUse<Context> context;
  Device *device;
  std::vector<RecordedCommand> commands;
  std::mutex mutex;
  std::condition_variable changed;
  std::deque<RecordedCommand> pending;
  std::thread worker;
  uint64_t submitted = 0;
  uint64_t completed = 0;
  ze_result_t failure = ZE_RESULT_SUCCESS;
  bool stopping = false;
};

struct Queue final : Handle {
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
  void run() {
    for (;;) {
      std::function<ze_result_t()> job;
      {
        std::unique_lock lock(mutex);
        changed.wait(lock, [this] { return stopping || !pending.empty(); });
        if (stopping && pending.empty()) return;
        job = std::move(pending.front());
        pending.pop_front();
      }
      ze_result_t result = ZE_RESULT_ERROR_UNKNOWN;
      try {
        result = job();
      } catch (...) {
        result = ZE_RESULT_ERROR_UNKNOWN;
      }
      job = {};
      {
        std::lock_guard lock(mutex);
        if (result != ZE_RESULT_SUCCESS && failure == ZE_RESULT_SUCCESS) failure = result;
        ++completed;
      }
      changed.notify_all();
    }
  }
  HandleUse<Context> context;
  Device *device;
  ze_command_queue_desc_t desc;
  std::mutex mutex;
  std::condition_variable changed;
  std::deque<std::function<ze_result_t()>> pending;
  std::thread worker;
  uint64_t submitted = 0;
  uint64_t completed = 0;
  ze_result_t failure = ZE_RESULT_SUCCESS;
  bool stopping = false;
};

struct Fence final : Handle {
  explicit Fence(Queue *queueValue) : Handle(Kind::Fence), queue(queueValue) {}
  HandleUse<Queue> queue;
  Event event;
  ze_result_t completionResult = ZE_RESULT_SUCCESS;
};

struct BuildLog final : Handle {
  explicit BuildLog(std::string value) : Handle(Kind::BuildLog), text(std::move(value)) {}
  std::string text;
};

enum class ModuleBackend { TransferOnly, RPC };

struct ModuleGlobalsSnapshot {
  std::mutex mutex;
  std::vector<uint64_t> allocationIds;
};

struct Module final : Handle {
  Module(Context *contextValue, std::vector<uint8_t> value, uint64_t programValue, ModuleBackend backendValue)
      : Handle(Kind::Module), context(contextValue), bytes(std::move(value)), programId(programValue), backend(backendValue),
        globalAllocations(std::make_shared<ModuleGlobalsSnapshot>()) {}
  HandleUse<Context> context;
  std::vector<uint8_t> bytes;
  uint64_t programId;
  ModuleBackend backend;
  std::shared_ptr<ModuleGlobalsSnapshot> globalAllocations;
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
};

struct Allocation;
using AllocationRef = std::shared_ptr<Allocation>;

struct KernelLaunch {
  std::shared_ptr<HandleUse<Kernel>> lifetime;
  Context *context;
  std::shared_ptr<ModuleGlobalsSnapshot> moduleGlobals;
  uint64_t programId;
  ModuleBackend backend;
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
  bool remoteSynchronized = false;
  std::vector<uint64_t> synchronizedPageFingerprints;
};

} // namespace gemstone::levelzero

#endif
