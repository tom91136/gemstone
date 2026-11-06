#pragma once

#include <array>
#include <atomic>
#include <condition_variable>
#include <functional>
#include <map>
#include <memory>
#include <mutex>
#include <optional>
#include <set>
#include <string>
#include <utility>
#include <vector>

#include <stddef.h>
#include <stdint.h>

#include <ze_api.h>

#include <gemstone/command_queue.hpp>
#include <gemstone/live_handles.hpp>

namespace gemstone::levelzero {

enum class Kind : uint32_t {
  Driver,
  Device,
  Context,
  Queue,
  List,
  EventPool,
  Event,
  Fence,
  Module,
  BuildLog,
  Kernel,
  PhysicalMemory,
  Image,
  Sampler
};

inline gemstone::LiveHandles<Kind> liveHandles;

struct QueueGroup {
  ze_command_queue_group_property_flags_t flags;
  size_t maxMemoryFillPatternSize;
  uint32_t numQueues;

  [[nodiscard]] bool compute() const { return (flags & ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COMPUTE) != 0; }
};

inline constexpr std::array<QueueGroup, 2> QueueGroups{{
    {.flags = ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COMPUTE | ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COPY
              | ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COOPERATIVE_KERNELS,
     .maxMemoryFillPatternSize = 128,
     .numQueues = 2},
    {.flags = ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COPY, .maxMemoryFillPatternSize = 128, .numQueues = 2},
}};

inline bool validEngine(uint32_t ordinal, uint32_t index) { return ordinal < QueueGroups.size() && index < QueueGroups[ordinal].numQueues; }

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
  HandleUse &operator=(const HandleUse &other) noexcept {
    HandleUse(other).swap(*this);
    return *this;
  }
  HandleUse &operator=(HandleUse &&other) noexcept {
    HandleUse(std::move(other)).swap(*this);
    return *this;
  }
  ~HandleUse() {
    if (!handle) return;
    bool reclaim = false;
    {
      std::lock_guard const lock(handle->usersMutex);
      const bool last = handle->users.fetch_sub(1, std::memory_order_acq_rel) == 1;
      reclaim = last && handle->destroyRequested.load(std::memory_order_acquire);
      // A waiter may destroy the handle as soon as this lock is released.
      handle->usersChanged.notify_all();
    }
    if (reclaim) delete handle;
  }
  [[nodiscard]] T *get() const { return handle; }
  T *operator->() const { return handle; }
  explicit operator bool() const { return handle; }
  void swap(HandleUse &other) noexcept { std::swap(handle, other.handle); }

private:
  T *handle = nullptr;
};

template <class T> ze_result_t destroyHandle(T *value) {
  if (!value) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  {
    std::lock_guard const lock(value->usersMutex);
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
  std::atomic<bool> lost{false};
};
inline std::atomic<uint64_t> nextContextId{1};
struct Context final : Handle {
  static constexpr Kind HandleKind = Kind::Context;
  Context() : Handle(HandleKind), id(nextContextId.fetch_add(1, std::memory_order_relaxed)) {}
  uint64_t id;
  std::vector<Device *> devices;
};
struct EventPool final : Handle {
  static constexpr Kind HandleKind = Kind::EventPool;
  EventPool(Context *contextValue, ze_event_pool_flags_t value) : Handle(HandleKind), contextId(contextValue->id), flags(value) {}
  uint64_t contextId;
  ze_event_pool_flags_t flags;
  std::vector<Device *> devices;
  uint64_t ipcId = 0;
  HandleUse<EventPool> ipcOrigin;
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
  bool counterBased = false;
  ze_event_counter_based_flags_t counterFlags = 0;
  uint64_t appended = 0;
  uint64_t completed = 0;
};

inline bool eventReached(const Event *event, uint64_t target) {
  return event->counterBased ? event->completed >= target : event->signalled;
}

using CommandAction = std::function<ze_result_t()>;

struct EventWait {
  Event *event;
  uint64_t target;
};

/** Event wait targets fixed at submission, as a recorded command may be submitted again before earlier runs finish. */
struct Submission {
  std::vector<EventWait> waits;
};

inline thread_local const Submission *activeSubmission = nullptr;

struct RecordedCommand {
  CommandAction action;
  std::function<void()> release;
  std::function<void(ze_result_t)> complete;
  std::function<Submission()> submit;

  [[nodiscard]] Submission prepare() const { return submit ? submit() : Submission{}; }

  ze_result_t execute(const Submission &submission, ze_result_t result = ZE_RESULT_SUCCESS) {
    if (result == ZE_RESULT_SUCCESS) {
      const auto *outer = std::exchange(activeSubmission, &submission);
      try {
        result = action();
      } catch (...) {
        result = ZE_RESULT_ERROR_UNKNOWN;
      }
      activeSubmission = outer;
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

constexpr uint32_t priorityRank(ze_command_queue_priority_t priority) {
  if (priority == ZE_COMMAND_QUEUE_PRIORITY_PRIORITY_HIGH) return 2;
  if (priority == ZE_COMMAND_QUEUE_PRIORITY_PRIORITY_LOW) return 0;
  return 1;
}

constexpr bool validPriority(ze_command_queue_priority_t priority) {
  return priority == ZE_COMMAND_QUEUE_PRIORITY_NORMAL || priority == ZE_COMMAND_QUEUE_PRIORITY_PRIORITY_LOW
         || priority == ZE_COMMAND_QUEUE_PRIORITY_PRIORITY_HIGH;
}

class Engine {
public:
  struct Position {
    uint32_t priority;
    uint64_t sequence;
    bool operator<(const Position &other) const {
      return priority != other.priority ? priority > other.priority : sequence < other.sequence;
    }
    bool operator==(const Position &other) const = default;
  };
  Position enqueue(uint32_t priority) {
    std::scoped_lock const lock(mutex);
    return *pending.insert(Position{.priority = priority, .sequence = next++}).first;
  }
  void acquire(Position position, bool drain) {
    std::unique_lock lock(mutex);
    changed.wait(lock, [&] { return !busy && *pending.begin() == position; });
    pending.erase(pending.begin());
    busy = true;
    if (drain) changed.wait(lock, [&] { return flying == 0; });
  }
  void release() {
    {
      std::scoped_lock const lock(mutex);
      busy = false;
    }
    changed.notify_all();
  }
  void beginFlight() {
    std::scoped_lock const lock(mutex);
    ++flying;
  }
  void endFlight() {
    {
      std::scoped_lock const lock(mutex);
      --flying;
    }
    changed.notify_all();
  }

private:
  std::mutex mutex;
  std::condition_variable changed;
  std::set<Position> pending;
  uint64_t next = 0;
  bool busy = false;
  uint64_t flying = 0;
};

Engine &engineFor(uint32_t ordinal, uint32_t index);

class EngineTurn {
public:
  explicit EngineTurn(Engine &engineValue, uint32_t priorityValue, std::optional<Engine::Position> reserved = std::nullopt,
                      bool drainValue = true)
      : engine(engineValue), priority(priorityValue), drain(drainValue) {
    if (outer) return;
    take(reserved ? *reserved : engine.enqueue(priority));
    active = this;
  }
  ~EngineTurn() {
    if (outer) return;
    if (held) engine.release();
    active = nullptr;
  }
  EngineTurn(const EngineTurn &) = delete;
  EngineTurn &operator=(const EngineTurn &) = delete;

  void reserveResume() {
    if (!handoff) handoff = engine.enqueue(priority);
  }
  [[nodiscard]] uint32_t queuePriority() const { return priority; }

  class Yield {
  public:
    Yield() : turn(active && active->held ? active : nullptr) {
      if (!turn) return;
      turn->engine.release();
      turn->held = false;
    }
    ~Yield() {
      if (turn) turn->take(turn->resumePosition());
    }
    Yield(const Yield &) = delete;
    Yield &operator=(const Yield &) = delete;

    [[nodiscard]] EngineTurn *waiter() const { return turn; }

  private:
    EngineTurn *turn;
  };

  class Flight {
  public:
    Flight() : turn(active && active->held ? active : nullptr) {
      if (!turn) return;
      turn->engine.beginFlight();
      turn->engine.release();
      turn->held = false;
    }
    ~Flight() {
      if (turn) turn->engine.endFlight();
    }
    Flight(const Flight &) = delete;
    Flight &operator=(const Flight &) = delete;

  private:
    EngineTurn *turn;
  };

private:
  void take(Engine::Position position) {
    (void)commandQueue().blocking([&] {
      engine.acquire(position, drain);
      return 0;
    });
    held = true;
  }

  Engine::Position resumePosition() {
    if (!handoff) return engine.enqueue(priority);
    const auto position = *handoff;
    handoff.reset();
    return position;
  }

  Engine &engine;
  uint32_t priority;
  bool drain;
  EngineTurn *outer{active};
  bool held = false;
  std::optional<Engine::Position> handoff;
  inline static thread_local EngineTurn *active = nullptr;
};

class PendingJob {
public:
  PendingJob(Engine &engineValue, uint32_t priorityValue, std::vector<EventWait> waitValues)
      : engine(engineValue), priority(priorityValue), waits(std::move(waitValues)), unreached(waits.size()) {
    std::scoped_lock const lock(registryMutex);
    registry.push_back(this);
  }
  ~PendingJob() { retire(); }
  PendingJob(const PendingJob &) = delete;
  PendingJob &operator=(const PendingJob &) = delete;

  static std::shared_ptr<PendingJob> track(Engine &engine, uint32_t priority, std::vector<EventWait> waits) {
    auto job = std::make_shared<PendingJob>(engine, priority, std::move(waits));
    for (size_t index = 0; index < job->waits.size(); ++index) {
      auto &wait = job->waits[index];
      std::scoped_lock const eventLock(wait.event->mutex);
      std::scoped_lock const lock(job->mutex);
      if (!job->reached[index] && eventReached(wait.event, wait.target)) job->reach(index);
    }
    return job;
  }

  void arm() {
    std::scoped_lock const lock(mutex);
    armed = true;
    if (!decided && unreached == 0) reserveLocked();
  }

  std::optional<Engine::Position> claim(bool wait) {
    std::unique_lock lock(mutex);
    armed = true;
    if (!decided && unreached == 0) reserveLocked();
    if (!decided && wait)
      (void)commandQueue().blocking([&] {
        changed.wait(lock, [&] { return decided || stopping.load(std::memory_order_acquire); });
        return 0;
      });
    decided = true;
    auto taken = std::exchange(position, std::nullopt);
    lock.unlock();
    retire();
    return taken;
  }

  static void release(Event *event, const std::vector<EngineTurn *> &parked) {
    struct Released {
      uint32_t priority;
      PendingJob *job;
      EngineTurn *turn;
    };
    std::scoped_lock const guard(registryMutex);
    std::vector<Released> released;
    released.reserve(parked.size());
    for (auto *turn : parked)
      released.push_back({.priority = turn->queuePriority(), .job = nullptr, .turn = turn});
    for (auto *job : registry) {
      std::scoped_lock const lock(job->mutex);
      for (size_t index = 0; index < job->waits.size(); ++index)
        if (!job->reached[index] && job->waits[index].event == event && eventReached(event, job->waits[index].target)) job->reach(index);
      if (job->armed && !job->decided && job->unreached == 0) released.push_back({.priority = job->priority, .job = job, .turn = nullptr});
    }
    std::stable_sort(released.begin(), released.end(),
                     [](const Released &lhs, const Released &rhs) { return lhs.priority > rhs.priority; });
    for (const auto &item : released) {
      if (item.turn) {
        item.turn->reserveResume();
      } else {
        std::scoped_lock const lock(item.job->mutex);
        if (!item.job->decided) item.job->reserveLocked();
      }
    }
  }

  static void stop() {
    stopping.store(true, std::memory_order_release);
    std::scoped_lock const guard(registryMutex);
    for (auto *job : registry) {
      std::scoped_lock const lock(job->mutex);
      job->changed.notify_all();
    }
  }

private:
  void reach(size_t index) {
    reached[index] = true;
    --unreached;
  }
  void reserveLocked() {
    position = engine.enqueue(priority);
    decided = true;
    changed.notify_all();
  }
  void retire() {
    std::scoped_lock const guard(registryMutex);
    std::erase(registry, this);
  }

  Engine &engine;
  uint32_t priority;
  std::vector<EventWait> waits;
  std::vector<bool> reached = std::vector<bool>(waits.size(), false);
  std::mutex mutex;
  std::condition_variable changed;
  size_t unreached;
  bool armed = false;
  bool decided = false;
  std::optional<Engine::Position> position;

  inline static std::mutex registryMutex;
  inline static std::vector<PendingJob *> registry;
  inline static std::atomic<bool> stopping{false};
};

/** FIFO execution on a shared-pool stream, reporting the first failure; without a stream the owner runs work inline. */
struct AsyncStream {
  AsyncStream(Engine &engineValue, uint32_t priorityValue, bool asynchronous)
      : engine(engineValue), priority(priorityValue), stream(asynchronous ? commandQueue().createStream(true) : nullptr) {}
  ~AsyncStream() {
    if (!stream) return;
    commandQueue().wait(commandQueue().snapshot(stream));
    commandQueue().destroyStream(stream);
  }
  AsyncStream(const AsyncStream &) = delete;
  AsyncStream &operator=(const AsyncStream &) = delete;

  /** `job` receives the first failure so far; its captures are released before the returned ticket completes. */
  template <class Job> gemstone::CommandQueue::Ticket submit(Job job, std::vector<EventWait> waits = {}, bool kernel = false) {
    auto pending = waits.empty() ? nullptr : PendingJob::track(engine, priority, std::move(waits));
    std::scoped_lock const order(submitMutex);
    std::optional<Engine::Position> reserved;
    if (idle(0)) {
      if (pending) pending->arm();
      else reserved = engine.enqueue(priority);
    }
    return commandQueue().submit(stream, [this, pending = std::move(pending), reserved, kernel, job = std::move(job)]() mutable {
      if (pending) reserved = pending->claim(failure.load(std::memory_order_acquire) == ZE_RESULT_SUCCESS);
      EngineTurn const turn(engine, priority, reserved, !kernel);
      ze_result_t result{};
      try {
        result = job(failure.load(std::memory_order_acquire));
      } catch (...) {
        result = ZE_RESULT_ERROR_UNKNOWN;
      }
      auto expected = ZE_RESULT_SUCCESS;
      if (result != ZE_RESULT_SUCCESS) failure.compare_exchange_strong(expected, result, std::memory_order_acq_rel);
      return 0;
    });
  }

  /** Waits for work submitted so far; `0` polls and `UINT64_MAX` blocks. */
  [[nodiscard]] bool idle(uint64_t timeout) const {
    return !stream || commandQueue().waitFor(commandQueue().snapshot(stream), timeout).has_value();
  }
  [[nodiscard]] ze_result_t waitFor(uint64_t timeout) const {
    return idle(timeout) ? failure.load(std::memory_order_acquire) : ZE_RESULT_NOT_READY;
  }

  Engine &engine;
  uint32_t const priority;
  void *const stream;
  std::atomic<ze_result_t> failure{ZE_RESULT_SUCCESS};

private:
  std::mutex submitMutex;
};

struct CommandList final : Handle {
  static constexpr Kind HandleKind = Kind::List;
  CommandList(bool immediateValue, Context *contextValue, Device *deviceValue, uint32_t ordinalValue, uint32_t indexValue = 0,
              ze_command_queue_mode_t modeValue = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS,
              ze_command_queue_priority_t priorityValue = ZE_COMMAND_QUEUE_PRIORITY_NORMAL)
      : Handle(HandleKind), immediate(immediateValue), ordinal(ordinalValue), context(contextValue), device(deviceValue),
        async(engineFor(ordinalValue, indexValue), priorityRank(priorityValue),
              immediate && modeValue != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS) {}
  bool immediate;
  uint32_t ordinal;
  [[nodiscard]] bool computeCapable() const { return QueueGroups[ordinal].compute(); }
  std::mutex appendMutex;
  bool closed = false;
  HandleUse<Context> context;
  Device *device;
  std::vector<RecordedCommand> commands;
  // Declared last so outstanding work drains before the other members are destroyed.
  AsyncStream async;
};

struct Queue final : Handle {
  static constexpr Kind HandleKind = Kind::Queue;
  Queue(Context *contextValue, Device *deviceValue, uint32_t ordinalValue, uint32_t indexValue, ze_command_queue_mode_t modeValue,
        ze_command_queue_priority_t priorityValue)
      : Handle(HandleKind), ordinal(ordinalValue), context(contextValue), device(deviceValue),
        async(engineFor(ordinalValue, indexValue), priorityRank(priorityValue), modeValue != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS) {}
  uint32_t ordinal;
  HandleUse<Context> context;
  Device *device;
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

struct LoadedProgram {
  struct Global {
    std::string name;
    void *base;
    size_t size;
  };
  explicit LoadedProgram(uint64_t idValue) : id(idValue) {}
  LoadedProgram(const LoadedProgram &) = delete;
  LoadedProgram &operator=(const LoadedProgram &) = delete;
  uint64_t id;
  std::mutex globalsMutex;
  std::vector<Global> globals;
  std::function<bool(LoadedProgram &)> teardown;
  ~LoadedProgram() {
    if (teardown) (void)teardown(*this);
  }
};

struct Module final : Handle {
  static constexpr Kind HandleKind = Kind::Module;
  Module(Context *contextValue, Device *deviceValue, std::vector<uint8_t> value, std::shared_ptr<LoadedProgram> programValue,
         std::string buildFlagsValue = {})
      : Handle(HandleKind), context(contextValue), device(deviceValue), source(value), bytes(std::move(value)),
        program(std::move(programValue)), buildFlags(std::move(buildFlagsValue)) {}
  HandleUse<Context> context;
  Device *device;
  // the module as created, kept apart from the image of a program shared with the modules it was linked with
  std::vector<uint8_t> source;
  std::vector<uint8_t> bytes;
  std::shared_ptr<LoadedProgram> program;
  std::string buildFlags;
  std::map<std::string, std::string> linkageNames;

  [[nodiscard]] std::string linkageName(const std::string &name) const {
    const auto found = linkageNames.find(name);
    return found == linkageNames.end() ? name : found->second;
  }
};

struct KernelParameterMetadata {
  bool pointerAggregate = false;
  bool local = false;
  bool containsPointer = false;
};

struct Image final : Handle {
  static constexpr Kind HandleKind = Kind::Image;
  Image(Context *contextValue, Device *deviceValue, const ze_image_desc_t &descValue)
      : Handle(HandleKind), context(contextValue), device(deviceValue), desc(descValue) {}
  HandleUse<Context> context;
  Device *device;
  ze_image_desc_t desc;
  uint32_t pixelBytes = 0;
  uint32_t width = 0;
  uint32_t height = 0;
  uint32_t slices = 0;
  size_t rowBytes = 0;
  size_t sliceBytes = 0;
  void *texels = nullptr;
  void *record = nullptr;
};

struct Sampler final : Handle {
  static constexpr Kind HandleKind = Kind::Sampler;
  Sampler(Context *contextValue, Device *deviceValue) : Handle(HandleKind), context(contextValue), device(deviceValue) {}
  HandleUse<Context> context;
  Device *device;
  void *record = nullptr;
};

struct Kernel final : Handle {
  static constexpr Kind HandleKind = Kind::Kernel;
  Kernel(Module *moduleValue, std::string nameValue) : Handle(HandleKind), module(moduleValue), name(std::move(nameValue)) {}
  HandleUse<Module> module;
  std::string name;

  [[nodiscard]] std::string linkageName() const { return module->linkageName(name); }
  uint32_t groupX = 1;
  uint32_t groupY = 1;
  uint32_t groupZ = 1;
  uint32_t offsetX = 0;
  uint32_t offsetY = 0;
  uint32_t offsetZ = 0;
  ze_kernel_indirect_access_flags_t indirect = 0;
  struct Argument {
    enum class Opaque : uint8_t { None, Image, Sampler };
    bool local = false;
    size_t localSize = 0;
    std::vector<uint8_t> bytes;
    Opaque opaque = Opaque::None;
    // resolved from the image or sampler handle when a launch is recorded
    uint64_t recordId = 0;
    uint64_t texelsId = 0;
    size_t recordBytes = 0;
  };
  std::vector<Argument> arguments;
  std::vector<KernelParameterMetadata> parameters;
  std::array<uint32_t, 3> requiredGroupSize{};
  uint32_t localMemSize = 0;
  uint32_t privateMemSize = 0;
  uint32_t spillMemSize = 0;
};

struct Allocation;
using AllocationRef = std::shared_ptr<Allocation>;

struct KernelLaunch {
  std::shared_ptr<HandleUse<Kernel>> lifetime;
  Device *device;
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
  std::optional<uint64_t> id;
  uint32_t device;
};

} // namespace gemstone::levelzero
