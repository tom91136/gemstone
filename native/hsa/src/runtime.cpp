#include <algorithm>
#include <array>
#include <atomic>
#include <chrono>
#include <condition_variable>
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
#include <unordered_set>
#include <utility>
#include <vector>

#include <hsa.h>
#include <hsa_ext_amd.h>
#include <stdint.h>
#include <unistd.h>

#include <aspartame/string.hpp>
#include <aspartame/vector.hpp>

#include "gemstone/hsa/allocation.hpp"
#include "gemstone/hsa/aql.hpp"
#include "gemstone/hsa/aql_execution.hpp"
#include "gemstone/hsa/doorbell_monitor.hpp"
#include "gemstone/hsa/executable_metadata.hpp"
#include "gemstone/hsa/global.hpp"
#include "gemstone/hsa/target.hpp"
#include "gemstone_execution_rpc.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/export.h>
#include <gemstone/memory.hpp>
#include <gemstone/trace.hpp>

#include "transfer.hpp"
#include <environment_keys.hpp>
#include <sys/stat.h>

namespace {

using namespace aspartame;
using namespace gemstone::execution;
using gemstone::readNative;
using gemstone::hsa::AqlQueue;
using gemstone::hsa::AqlRuntime;
using gemstone::hsa::DoorbellMonitor;
using gemstone::hsa::PacketResult;

constexpr uint64_t CpuAgent = 1;
constexpr uint64_t GpuAgent = 2;
constexpr uint64_t GpuIsa = 1;
constexpr uint64_t CpuRegion = 1;
constexpr uint64_t GpuRegion = 2;
constexpr uint64_t CpuPool = 1;
constexpr uint64_t GpuPool = 2;
constexpr uint64_t GroupPool = 3;
constexpr size_t GroupMemorySize = 64 * 1024;
constexpr uint32_t QueueMinimum = 1;
constexpr uint32_t QueueMaximum = 4096;
constexpr uint64_t VirtualMemorySize = static_cast<uint64_t>(1) * 1024 * 1024 * 1024;
constexpr uint32_t AllocationGranule = 4096;
constexpr uint32_t TransferBatch = 64U * 1024U;
// GCC stack spills form many short dirty intervals; bound extra reads between them.
constexpr uint64_t DirtyReadMaximumGap = 256;

struct Signal {
  uint64_t kind = 0;
  alignas(8) int64_t value = 0;
  uint64_t *doorbell = nullptr;
  std::atomic_bool live{true};
};
static_assert(offsetof(Signal, value) == 8);

struct Allocation {
  void *base = nullptr;
  size_t size = 0;
  uint64_t id = 0;
  uint64_t pool = 0;
  bool owned = false;
  // Transfers borrow native storage while free or deregistration retires it.
  std::mutex transferLock;
  bool retired = false;
  bool freeing = false;

  Allocation(void *baseValue, size_t sizeValue, uint64_t idValue, uint64_t poolValue, bool ownedValue)
      : base(baseValue), size(sizeValue), id(idValue), pool(poolValue), owned(ownedValue) {}
};
using AllocationRef = std::shared_ptr<Allocation>;

struct Reader {
  std::vector<uint8_t> bytes;
  bool legacy = false;
};

struct Kernel {
  uint64_t object = 0;
  gemstone::hsa::ExecutableKernelMetadata metadata;
};
using KernelRef = std::shared_ptr<Kernel>;

struct GlobalStorage {
  AllocationRef allocation;
  uint64_t elfAddress = 0;
  size_t size = 0;
  uint64_t kernelObject = 0;
  std::vector<size_t> pointerOffsets;
};
using GlobalStorageRef = std::shared_ptr<GlobalStorage>;

struct Global {
  uint64_t object = 0;
  std::string name;
  uint32_t size = 0;
  void *address = nullptr;
  GlobalStorageRef storage;
};
using GlobalRef = std::shared_ptr<Global>;

struct CatalogueGlobal {
  std::string name;
  uint64_t address = 0;
  uint32_t size = 0;
  std::vector<uint32_t> pointerOffsets;
};

struct Executable {
  uint64_t handle = 0;
  bool frozen = false;
  std::vector<KernelRef> kernels;
  std::unordered_map<std::string, KernelRef> kernelNames;
  std::vector<GlobalRef> globals;
  std::vector<GlobalStorageRef> globalStorage;
};
using ExecutableRef = std::shared_ptr<Executable>;

struct Queue {
  hsa_queue_t value{};
  std::unique_ptr<AqlQueue> aql;
  void *ring = nullptr;
  size_t ringBytes = 0;
  std::shared_ptr<Signal> doorbell;
  void (*callback)(hsa_status_t, hsa_queue_t *, void *) = nullptr;
  void *callbackData = nullptr;
  bool errorHandlerRunning = false;

  ~Queue() {
    aql.reset();
    std::free(ring);
  }
};
using QueueRef = std::shared_ptr<Queue>;

struct AsyncHandler {
  std::shared_ptr<Signal> signal;
  hsa_signal_condition_t condition;
  hsa_signal_value_t compare;
  hsa_amd_signal_handler handler;
  void *argument;
};

struct QueueError {
  QueueRef queue;
  hsa_status_t status;
};

struct RuntimeState {
  std::mutex lock;
  std::mutex mirrorLock;
  uint32_t references = 0;
  uint64_t nextHandle = 1;
  uint64_t nextKernel = static_cast<uint64_t>(0x10000);
  std::unique_ptr<Client> client;
  std::unique_ptr<AqlRuntime> aql;
  std::unique_ptr<DoorbellMonitor> monitor;
  std::unordered_map<uint64_t, std::shared_ptr<Signal>> signals;
  std::unordered_map<hsa_queue_t *, QueueRef> queues;
  std::unordered_map<void *, AllocationRef> allocations;
  std::unordered_map<uint64_t, AllocationRef> allocationIds;
  std::mutex registrationLock;
  gemstone::MemoryRegistrations registrations;
  std::unordered_map<uint64_t, std::shared_ptr<Reader>> readers;
  std::unordered_map<uint64_t, ExecutableRef> executables;
  std::unordered_map<uint64_t, KernelRef> kernels;
  std::unordered_map<uint64_t, GlobalRef> globals;
  std::unordered_map<uint64_t, GlobalStorageRef> globalStorage;
  std::condition_variable asyncChanged;
  std::vector<std::shared_ptr<AsyncHandler>> asyncHandlers;
  std::vector<QueueError> queueErrors;
  std::vector<std::thread> asyncWorkers;
  bool asyncStopping = false;
};

struct DeferredSignalCompletion {
  uint64_t handle = 0;
  int32_t error = 0;
  bool failed = false;
};

thread_local DeferredSignalCompletion *activeSignalCompletion = nullptr;
thread_local bool runningQueueError = false;

struct SignalCompletionScope {
  DeferredSignalCompletion *previous;
  explicit SignalCompletionScope(DeferredSignalCompletion &completion) : previous(std::exchange(activeSignalCompletion, &completion)) {}
  ~SignalCompletionScope() { activeSignalCompletion = previous; }
};

RuntimeState &state() {
  // Reachable from DSO finalizers, so the state lives until process termination.
  static auto *value = new RuntimeState();
  return *value;
}

bool traceMemory() { return gemstone::traceEnabled("hsa", "memory"); }

bool tracePackets() { return gemstone::traceEnabled("hsa", "packets"); }

bool initialised(const RuntimeState &runtime) { return runtime.references != 0 && runtime.client && runtime.aql; }

hsa_status_t requireInitialised(const RuntimeState &runtime) {
  return initialised(runtime) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_NOT_INITIALIZED;
}

bool validAgent(hsa_agent_t agent) { return agent.handle == CpuAgent || agent.handle == GpuAgent; }
bool validGpu(hsa_agent_t agent) { return agent.handle == GpuAgent; }
bool validIsa(hsa_isa_t isa) { return isa.handle == GpuIsa; }
bool validRegion(hsa_region_t region) { return region.handle == CpuRegion || region.handle == GpuRegion; }
bool validPool(hsa_amd_memory_pool_t pool) { return pool.handle == CpuPool || pool.handle == GpuPool || pool.handle == GroupPool; }

template <typename T> hsa_status_t copyStatus(void *destination, const T &value) {
  if (!destination) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::memcpy(destination, &value, sizeof(value));
  return HSA_STATUS_SUCCESS;
}

void copyName(void *destination, const char *name) {
  std::memset(destination, 0, 64);
  std::memcpy(destination, name, std::min<size_t>(std::strlen(name), 63));
}

bool traceApi() {
  static const bool enabled = gemstone::traceEnabled("hsa", "api");
  return enabled;
}

void traceApiCall(const char *name, uint64_t first = 0, uint64_t second = 0) {
  if (!traceApi()) return;
  std::fprintf(stderr, "[gemstone-hsa] %s(%llu,%llu)\n", name, static_cast<unsigned long long>(first),
               static_cast<unsigned long long>(second));
}

void traceApiResult(const char *name, hsa_status_t status) {
  if (!traceApi()) return;
  std::fprintf(stderr, "[gemstone-hsa] %s -> %d\n", name, static_cast<int>(status));
}

void traceApiText(const char *name, const std::string &value) {
  if (!traceApi()) return;
  std::fprintf(stderr, "[gemstone-hsa] %s = %s\n", name, value.c_str());
}

bool traceLoaders() {
  static const bool enabled = gemstone::traceEnabled("hsa", "loaders");
  return enabled;
}

void traceLoader(const char *operation, uint64_t handle, size_t size = 0) {
  if (!traceLoaders()) return;
  std::fprintf(stderr, "[gemstone-hsa] loader %s handle=%llu size=%zu\n", operation, static_cast<unsigned long long>(handle), size);
}

void traceLoaderSymbol(const char *operation, uint64_t executable, const char *name) {
  if (!traceLoaders()) return;
  std::fprintf(stderr, "[gemstone-hsa] loader %s executable=%llu name=%s\n", operation, static_cast<unsigned long long>(executable),
               name ? name : "<null>");
}

bool isRequestedIsa(const char *name) {
  if (!name) return false;
  const std::string requested(name);
  const std::string target = gemstone::hsa::targetArch();
  return requested == std::string("amdgcn-amd-amdhsa--") + target || requested == std::string("AMD:AMDGPU:") + target
         || requested == std::string("AMD:") + target;
}

std::shared_ptr<Signal> findSignal(RuntimeState &runtime, hsa_signal_t signal) {
  if (signal.handle == 0) return {};
  const auto found = runtime.signals.find(signal.handle);
  return found == runtime.signals.end() ? std::shared_ptr<Signal>{} : found->second;
}

std::shared_ptr<Signal> findSignal(RuntimeState &runtime, uint64_t handle) {
  std::lock_guard<std::mutex> guard(runtime.lock);
  return findSignal(runtime, hsa_signal_t{handle});
}

bool releaseDeferredSignal(RuntimeState &runtime, uint64_t handle) {
  const auto signal = findSignal(runtime, handle);
  if (!signal) return false;
  __atomic_fetch_sub(&signal->value, static_cast<int64_t>(1), __ATOMIC_RELEASE);
  runtime.asyncChanged.notify_all();
  return true;
}

// Completions are deferred until consume has mirrored dirty ranges back into host memory.
class PublicSignals final : public gemstone::hsa::Signals {
public:
  explicit PublicSignals(RuntimeState &runtime) : runtime(runtime) {}

  bool load(uint64_t handle, int64_t &value) override {
    const auto signal = findSignal(runtime, handle);
    if (!signal) return false;
    value = __atomic_load_n(&signal->value, __ATOMIC_ACQUIRE);
    return true;
  }
  bool complete(uint64_t handle) override {
    if (!activeSignalCompletion || activeSignalCompletion->handle != 0 || !findSignal(runtime, handle)) return false;
    activeSignalCompletion->handle = handle;
    return true;
  }
  bool fail(uint64_t handle, int32_t code) override {
    if (!activeSignalCompletion) return false;
    activeSignalCompletion->failed = true;
    activeSignalCompletion->error = code;
    return handle != 0;
  }

private:
  RuntimeState &runtime;
};

AllocationRef findAllocation(RuntimeState &runtime, const void *pointer, size_t size = 0) {
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  AllocationRef endpoint;
  for (const auto &[_, allocation] : runtime.allocations) {
    const auto base = reinterpret_cast<uintptr_t>(allocation->base);
    if (address < base || address - base > allocation->size) continue;
    if (size > allocation->size - (address - base)) continue;
    if (address - base < allocation->size) {
      return allocation;
    }
    endpoint = allocation;
  }
  return endpoint;
}

AllocationRef findAllocationById(RuntimeState &runtime, uint64_t id) {
  const auto found = runtime.allocationIds.find(id);
  return found == runtime.allocationIds.end() ? AllocationRef{} : found->second;
}

uint64_t nativeGlobalPointer(RuntimeState &runtime, const GlobalStorage &storage, uint64_t pointer) {
  for (const auto &[_, target] : runtime.globalStorage) {
    if (target->kernelObject != storage.kernelObject || pointer < target->elfAddress) continue;
    if (pointer - target->elfAddress <= target->size)
      return reinterpret_cast<uint64_t>(target->allocation->base) + pointer - target->elfAddress;
  }
  constexpr uint64_t base = gemstone::hsa::AllocationBase, span = gemstone::hsa::AllocationSpan;
  if (pointer >= base) {
    const uint64_t id = (pointer - base) / span + 1;
    const uint64_t offset = (pointer - base) % span;
    const auto allocation = findAllocationById(runtime, id);
    if (allocation && offset <= allocation->size) return reinterpret_cast<uint64_t>(allocation->base) + offset;
  }
  return pointer;
}

void restoreGlobalPointers(RuntimeState &runtime, const AllocationRef &allocation, size_t offset, size_t size) {
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.globalStorage.find(allocation->id);
  if (found == runtime.globalStorage.end()) return;
  for (size_t position : found->second->pointerOffsets) {
    if (position < offset || position - offset > size || size - (position - offset) < sizeof(uint64_t)) continue;
    auto *address = static_cast<uint8_t *>(allocation->base) + position;
    auto pointer = readNative<uint64_t>(address);
    pointer = nativeGlobalPointer(runtime, *found->second, pointer);
    std::memcpy(address, &pointer, sizeof(pointer));
  }
}

int upload(RuntimeState &runtime, const AllocationRef &allocation, size_t offset, size_t size) {
  std::lock_guard<std::mutex> transfer(allocation->transferLock);
  if (allocation->retired) return -1;
  if (allocation->pool == CpuPool) return 0;
  const bool trace = traceMemory();
  const size_t start = offset;
  const size_t requested = size;
  if (trace) {
    std::fprintf(stderr, "Gemstone HSA upload begin allocation=%lu offset=%zu size=%zu sample=", allocation->id, offset, size);
    const auto *sample = static_cast<const uint8_t *>(allocation->base) + offset;
    for (size_t index = 0; index < std::min<size_t>(16, size); ++index)
      std::fprintf(stderr, "%02x", sample[index]);
    std::fprintf(stderr, "\n");
  }
  auto *bytes = static_cast<const uint8_t *>(allocation->base) + offset;
  std::vector<uint8_t> relocated;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto global = runtime.globalStorage.find(allocation->id);
    if (global != runtime.globalStorage.end()) {
      relocated.assign(bytes, bytes + size);
      for (size_t position = 0; position + sizeof(uint64_t) <= size; ++position) {
        const auto pointer = readNative<uint64_t>(relocated.data(), position);
        const auto target = findAllocation(runtime, reinterpret_cast<void *>(pointer));
        if (!target) continue;
        const uint64_t relative = pointer - reinterpret_cast<uint64_t>(target->base);
        const auto bound = runtime.globalStorage.find(target->id);
        // Ordinary allocations retain their registered native address, matching typed kernel arguments.
        const uint64_t encoded = bound != runtime.globalStorage.end() && bound->second->kernelObject == global->second->kernelObject
                                         && relative <= bound->second->size
                                     ? bound->second->elfAddress + relative
                                     : pointer;
        std::memcpy(relocated.data() + position, &encoded, sizeof(encoded));
        auto &fields = global->second->pointerOffsets;
        if (!(fields ^ contains(offset + position))) fields.push_back(offset + position);
        position += sizeof(uint64_t) - 1;
      }
      bytes = relocated.data();
    }
  }
  if (!transferAllocation(*runtime.client, AllocationId{static_cast<int64_t>(allocation->id)}, offset, bytes, size, true, TransferBatch)) {
    if (trace)
      std::fprintf(stderr, "Gemstone HSA upload failed allocation=%lu offset=%zu size=%zu error=%s\n", allocation->id, offset, size,
                   runtime.client->error().c_str());
    return -1;
  }
  if (trace) std::fprintf(stderr, "Gemstone HSA upload end allocation=%lu offset=%zu size=%zu\n", allocation->id, start, requested);
  return 0;
}

int download(RuntimeState &runtime, const AllocationRef &allocation, size_t offset, size_t size) {
  if (allocation->pool == CpuPool) return 0;
  std::lock_guard<std::mutex> transfer(allocation->transferLock);
  if (allocation->retired) return -1;
  const bool trace = traceMemory();
  const size_t start = offset;
  const size_t requested = size;
  if (trace) std::fprintf(stderr, "Gemstone HSA download begin allocation=%lu offset=%zu size=%zu\n", allocation->id, offset, size);
  if (!transferAllocation(*runtime.client, AllocationId{static_cast<int64_t>(allocation->id)}, offset,
                          static_cast<uint8_t *>(allocation->base) + offset, size, false, TransferBatch))
    return -1;
  restoreGlobalPointers(runtime, allocation, start, requested);
  if (trace) {
    const auto *sample = static_cast<const uint8_t *>(allocation->base) + start;
    std::fprintf(stderr, "Gemstone HSA download end allocation=%lu sample=", allocation->id);
    for (size_t index = 0; index < std::min<size_t>(16, requested); ++index)
      std::fprintf(stderr, "%02x", sample[index]);
    std::fprintf(stderr, "\n");
  }
  return 0;
}

std::vector<gemstone::MemoryRange> liveTransferRanges(RuntimeState &runtime, const AllocationRef &allocation, size_t offset, size_t size) {
  const gemstone::MemoryRange requested{reinterpret_cast<uintptr_t>(allocation->base) + offset, size};
  if (allocation->owned) return {requested};
  std::lock_guard<std::mutex> guard(runtime.lock);
  return gemstone::coveredMemoryRanges(requested, runtime.registrations.ranges());
}

int mirrorBack(RuntimeState &runtime) {
  // A completion may drain several queues' dirty ranges. Serialise the entire
  // drain and copy-back so every queue publishes its signal after those writes.
  std::lock_guard mirrorGuard(runtime.mirrorLock);
  auto ranges = runtime.aql->takeDirtyRanges();
  if (traceMemory()) std::fprintf(stderr, "Gemstone HSA mirror query count=%zu\n", ranges.size());
  std::sort(ranges.begin(), ranges.end(), [](const auto &left, const auto &right) {
    if (left.allocation.value != right.allocation.value) return left.allocation.value < right.allocation.value;
    return left.offset.value < right.offset.value;
  });
  for (size_t begin = 0; begin < ranges.size();) {
    size_t end = begin + 1;
    while (end < ranges.size() && ranges[end].allocation.value == ranges[begin].allocation.value)
      ++end;
    AllocationRef allocation;
    {
      std::lock_guard<std::mutex> guard(runtime.lock);
      allocation = findAllocationById(runtime, static_cast<uint64_t>(ranges[begin].allocation.value));
    }
    if (!allocation) return -1;
    const std::vector<DirtyRange> dirty(ranges.begin() + static_cast<ptrdiff_t>(begin), ranges.begin() + static_cast<ptrdiff_t>(end));
    for (const auto &range : dirty)
      if (static_cast<uint64_t>(range.offset.value) > allocation->size
          || static_cast<uint64_t>(range.size.value) > allocation->size - static_cast<uint64_t>(range.offset.value))
        return -1;
    if (allocation->owned && allocation->pool == GpuPool) {
      std::lock_guard transfer(allocation->transferLock);
      if (allocation->retired) return -1;
      if (!readDirtyRanges(
              *runtime.client, dirty,
              [&](const DirtyRange &range, const uint8_t *bytes) {
                const auto offset = static_cast<size_t>(range.offset.value);
                const auto size = static_cast<size_t>(range.size.value);
                std::memcpy(static_cast<uint8_t *>(allocation->base) + offset, bytes, size);
                restoreGlobalPointers(runtime, allocation, offset, size);
                return true;
              },
              DirtyReadMaximumGap, TransferBatch))
        return -1;
    } else {
      for (const auto &range : dirty)
        for (const auto live : liveTransferRanges(runtime, allocation, range.offset.value, range.size.value))
          if (download(runtime, allocation, live.base - reinterpret_cast<uintptr_t>(allocation->base), live.size) != 0) return -1;
    }
    begin = end;
  }
  return 0;
}

hsa_status_t queueStatus(int32_t code) {
  using gemstone::hsa::PacketFailure;
  switch (static_cast<PacketFailure>(code)) {
    case PacketFailure::KernelNotInstalled: return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
    case PacketFailure::ExecutionFailed: return HSA_STATUS_ERROR_EXCEPTION;
    case PacketFailure::UnreadableDependency:
    case PacketFailure::MalformedPacket: return HSA_STATUS_ERROR_INVALID_PACKET_FORMAT;
    case PacketFailure::InvalidMemoryReference: return static_cast<hsa_status_t>(HSA_STATUS_ERROR_MEMORY_APERTURE_VIOLATION);
    case PacketFailure::InvalidDimensions: return HSA_STATUS_ERROR_INCOMPATIBLE_ARGUMENTS;
    case PacketFailure::InvalidGrid: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
  return HSA_STATUS_ERROR_EXCEPTION;
}

void reportQueueError(RuntimeState &runtime, uint64_t queueId, hsa_status_t status) {
  std::lock_guard<std::mutex> guard(runtime.lock);
  for (const auto &[_, queue] : runtime.queues) {
    if (queue->value.id == queueId) {
      runtime.queueErrors.push_back(QueueError{queue, status});
      runtime.asyncChanged.notify_all();
      break;
    }
  }
}

// Mirrors ROCr's default queue error handler: report and abort unless HSA_ENABLE_QUEUE_FAULT_MESSAGE=0.
void defaultQueueError(hsa_status_t status, hsa_queue_t *queue) {
  const char *enabled = std::getenv("HSA_ENABLE_QUEUE_FAULT_MESSAGE");
  if (enabled && std::strcmp(enabled, "0") == 0) return;
  const char *message = "UNKNOWN ERROR";
  hsa_status_string(status, &message);
  std::fprintf(stderr, "Queue at %p inactivated due to async error:\n\t%s\n", static_cast<void *>(queue), message);
  std::abort();
}

PacketResult consume(RuntimeState &runtime, uint64_t queueId, const void *packet) {
  // A queue producer may recycle a ring slot as soon as it observes the
  // consumer's read index. Keep one immutable packet image for the whole
  // execute/failure path; otherwise a concurrent header update can make the
  // two decodes disagree.
  std::array<uint8_t, gemstone::hsa::AqlPacketSize> stable;
  std::memcpy(stable.data(), packet, stable.size());
  // Device allocations remain remote-authoritative. Explicit copy/fill APIs
  // publish host changes; a dispatch must not re-upload stale shadow bytes over
  // another queue's in-flight device writes. CPU pools use exact host demand.
  DeferredSignalCompletion completion;
  PacketResult result;
  {
    SignalCompletionScope scope(completion);
    result = runtime.aql->consume(stable.data());
  }
  if (result == PacketResult::Retry) return result;
  // A failed packet leaves its completion signal untouched and halts the queue, as the ROCr packet processor does.
  const auto halt = [&](int32_t code) {
    reportQueueError(runtime, queueId, queueStatus(code));
    return PacketResult::Fatal;
  };
  if (completion.failed) return halt(completion.error);
  if (result != PacketResult::Accepted) return halt(static_cast<int32_t>(gemstone::hsa::PacketFailure::ExecutionFailed));
  // Host-demand writes and allocation copy-back must precede the completion signal.
  if (mirrorBack(runtime) != 0) return halt(static_cast<int32_t>(gemstone::hsa::PacketFailure::ExecutionFailed));
  if (completion.handle != 0 && !releaseDeferredSignal(runtime, completion.handle))
    return halt(static_cast<int32_t>(gemstone::hsa::PacketFailure::ExecutionFailed));
  return result;
}

bool signalCondition(int64_t value, hsa_signal_condition_t condition, int64_t compare) {
  switch (condition) {
    case HSA_SIGNAL_CONDITION_EQ: return value == compare;
    case HSA_SIGNAL_CONDITION_NE: return value != compare;
    case HSA_SIGNAL_CONDITION_LT: return value < compare;
    case HSA_SIGNAL_CONDITION_GTE: return value >= compare;
  }
  return false;
}

void runAsyncHandlers(RuntimeState &runtime) {
  std::unique_lock<std::mutex> lock(runtime.lock);
  while (!runtime.asyncStopping) {
    runtime.asyncChanged.wait_for(lock, std::chrono::microseconds(100));
    if (runtime.asyncStopping) break;
    if (!runtime.queueErrors.empty()) {
      auto [queue, status] = std::move(runtime.queueErrors.front());
      runtime.queueErrors.erase(runtime.queueErrors.begin());
      queue->errorHandlerRunning = true;
      runningQueueError = true;
      lock.unlock();
      // Queue callbacks may re-enter the runtime or destroy their queue. Run on
      // the notification pool, never on the queue consumer or under its locks.
      if (queue->callback) queue->callback(status, &queue->value, queue->callbackData);
      else defaultQueueError(status, &queue->value);
      lock.lock();
      runningQueueError = false;
      queue->errorHandlerRunning = false;
      queue.reset();
      runtime.asyncChanged.notify_all();
      continue;
    }
    const auto ready = std::find_if(runtime.asyncHandlers.begin(), runtime.asyncHandlers.end(), [](const auto &entry) {
      const auto current = __atomic_load_n(&entry->signal->value, __ATOMIC_ACQUIRE);
      return entry->signal->live.load(std::memory_order_acquire) && signalCondition(current, entry->condition, entry->compare);
    });
    if (ready == runtime.asyncHandlers.end()) continue;
    const auto entry = *ready;
    runtime.asyncHandlers.erase(ready);
    lock.unlock();
    const auto current = __atomic_load_n(&entry->signal->value, __ATOMIC_ACQUIRE);
    const bool keep = entry->handler(current, entry->argument);
    lock.lock();
    if (keep && !runtime.asyncStopping && entry->signal->live.load(std::memory_order_acquire)) runtime.asyncHandlers.push_back(entry);
    runtime.asyncChanged.notify_all();
  }
}

hsa_signal_value_t waitSignal(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare, uint64_t timeoutHint) {
  auto &runtime = state();
  std::shared_ptr<Signal> value;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    value = findSignal(runtime, signal);
  }
  if (!value) return 0;
  const auto start = std::chrono::steady_clock::now();
  for (;;) {
    const int64_t current = __atomic_load_n(&value->value, __ATOMIC_ACQUIRE);
    if (signalCondition(current, condition, compare)) return current;
    if (timeoutHint != UINT64_MAX) {
      const auto elapsed = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now() - start);
      if (elapsed.count() >= 0 && static_cast<uint64_t>(elapsed.count()) >= timeoutHint) return current;
    }
    std::this_thread::yield();
  }
}

int completeSignal(hsa_signal_t signal) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = findSignal(runtime, signal);
  if (!value) return -1;
  __atomic_fetch_sub(&value->value, static_cast<int64_t>(1), __ATOMIC_RELEASE);
  return 0;
}

// Bounds every wait for a queue to drain under system paging. Defaults to the
// RPC timeout so a wedged queue fails instead of stalling the caller forever.
std::chrono::seconds pagingTimeout() { return std::chrono::seconds(gemstone::environment::hsaPagingTimeoutSeconds()); }

bool awaitQueuedDispatches(RuntimeState &runtime) {
  std::vector<std::pair<QueueRef, uint64_t>> pending;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    pending.reserve(runtime.queues.size());
    for (const auto &[_, queue] : runtime.queues)
      if (queue->aql->active()) pending.emplace_back(queue, __atomic_load_n(queue->aql->writeIndex(), __ATOMIC_ACQUIRE));
  }
  // The queue read index only advances after the worker mirrors writes back.
  // Give up rather than stall an allocation forever on a wedged queue.
  const auto deadline = std::chrono::steady_clock::now() + pagingTimeout();
  for (const auto &[queue, target] : pending) {
    while (queue->aql->active() && __atomic_load_n(queue->aql->readIndex(), __ATOMIC_ACQUIRE) < target) {
      if (std::chrono::steady_clock::now() >= deadline) {
        if (traceMemory())
          std::fprintf(stderr, "Gemstone HSA queue drain timed out waiting for packet %lu\n", static_cast<unsigned long>(target));
        return false;
      }
      std::this_thread::sleep_for(std::chrono::milliseconds(1));
    }
  }
  return true;
}

/** Registers a remote allocation for `[base, base + size)`, rolling back the registration and running `release` on
 * failure.
 */
hsa_status_t registerRemoteAllocation(RuntimeState &runtime, void *base, size_t size, uint64_t pool, bool owned, bool hostBacked,
                                      const std::function<void()> &release) {
  const auto registered = registerAllocation(*runtime.client, size, base, hostBacked);
  if (!registered) {
    release();
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  const auto id = static_cast<uint64_t>(registered->value);
  const auto releaseRemote = [&] {
    if (traceMemory()) std::fprintf(stderr, "Gemstone HSA release request allocation=%lu\n", id);
    (void)runtime.client->call(ReleaseAllocation{*registered});
    release();
  };
  if (!runtime.aql->registerAllocation(reinterpret_cast<uint64_t>(base), size, id)) {
    releaseRemote();
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  try {
    auto allocation = std::make_shared<Allocation>(base, size, id, pool, owned);
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.allocations.emplace(base, allocation);
    runtime.allocationIds.emplace(id, allocation);
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    runtime.aql->unregisterAllocation(reinterpret_cast<uint64_t>(base));
    releaseRemote();
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

hsa_status_t allocate(RuntimeState &runtime, uint64_t pool, size_t requested, void **result, bool owned) {
  if (!result || requested == 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (requested > std::numeric_limits<size_t>::max() - (AllocationGranule - 1U)) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  const size_t rounded = (requested + AllocationGranule - 1U) & ~(size_t(AllocationGranule) - 1U);
  // ROCr allocations are page-granular and several vendor kernels use the
  // following granule for private algorithmic scratch even when the logical
  // user range ends exactly at the previous page boundary.
  if (rounded > std::numeric_limits<size_t>::max() - 2U * AllocationGranule) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  // Keep two trailing granules. Vendor algorithm kernels commonly perform a
  // masked vector load at the first address beyond a logical allocation; the
  // hardware allocator's page-granular mapping makes that harmless, while
  // the emulator's checked view needs the same padding explicitly.
  const size_t size = rounded + 2U * AllocationGranule;
  void *memory = nullptr;
  if (posix_memalign(&memory, AllocationGranule, size) != 0 || !memory) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  std::memset(memory, 0, size);
  const auto status = registerRemoteAllocation(runtime, memory, size, pool, owned, pool == CpuPool, [memory] { std::free(memory); });
  if (status != HSA_STATUS_SUCCESS) {
    if (traceMemory())
      std::fprintf(stderr, "Gemstone HSA allocation alias failed address=%p size=%zu error=%s\n", memory, size,
                   runtime.client->error().c_str());
    return status;
  }
  *result = memory;
  return HSA_STATUS_SUCCESS;
}

hsa_status_t freeAllocation(RuntimeState &runtime, void *pointer, bool requireOwned) {
  if (!pointer) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  AllocationRef allocation;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.allocations.find(pointer);
    if (found == runtime.allocations.end() || (requireOwned && !found->second->owned)) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    allocation = found->second;
    if (runtime.globalStorage.count(allocation->id) != 0) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
  }
  {
    std::lock_guard<std::mutex> transfer(allocation->transferLock);
    if (allocation->retired || allocation->freeing) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    allocation->freeing = true;
  }
  // A kernel may still hold this allocation after dispatch returns. Let its
  // mirror-back finish before releasing the server allocation. Do not hold
  // transfer_lock while waiting: mirror-back needs it to complete. The bound
  // exceeds the RPC timeout so a live mirror-back is never abandoned early.
  const auto deadline = std::chrono::steady_clock::now() + pagingTimeout() * 2;
  for (;;) {
    auto released = runtime.client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(allocation->id)}});
    if (released) break;
    if (released.error().detail.find("retained by completion") == std::string::npos || std::chrono::steady_clock::now() >= deadline) {
      std::lock_guard<std::mutex> transfer(allocation->transferLock);
      allocation->freeing = false;
      if (traceMemory())
        std::fprintf(stderr, "Gemstone HSA release allocation=%lu failed: %s\n", allocation->id, released.error().detail.c_str());
      return HSA_STATUS_ERROR;
    }
    std::this_thread::sleep_for(std::chrono::milliseconds(1));
  }
  {
    std::lock_guard<std::mutex> transfer(allocation->transferLock);
    allocation->retired = true;
  }
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.allocations.erase(pointer);
    runtime.allocationIds.erase(allocation->id);
  }
  runtime.aql->unregisterAllocation(reinterpret_cast<uint64_t>(allocation->base));
  if (allocation->owned) std::free(allocation->base);
  return HSA_STATUS_SUCCESS;
}

bool validPowerOfTwo(uint32_t value) { return value >= QueueMinimum && value <= QueueMaximum && (value & (value - 1U)) == 0; }

std::shared_ptr<Reader> reader(RuntimeState &runtime, hsa_code_object_reader_t handle) {
  const auto found = runtime.readers.find(handle.handle);
  return found == runtime.readers.end() ? std::shared_ptr<Reader>{} : found->second;
}

ExecutableRef executable(RuntimeState &runtime, hsa_executable_t handle) {
  const auto found = runtime.executables.find(handle.handle);
  return found == runtime.executables.end() ? ExecutableRef{} : found->second;
}

} // namespace

extern "C" hsa_status_t hsa_status_string(hsa_status_t status, const char **statusString) {
  if (!statusString) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (static_cast<int>(status) == HSA_STATUS_ERROR_MEMORY_APERTURE_VIOLATION) {
    *statusString = "memory aperture violation";
    return HSA_STATUS_SUCCESS;
  }
  switch (status) {
    case HSA_STATUS_SUCCESS: *statusString = "success"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_INFO_BREAK: *statusString = "information break"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_NOT_INITIALIZED:
      *statusString = "HSA_STATUS_ERROR_NOT_INITIALIZED: An API other than hsa_init has been invoked while the reference count of the HSA "
                      "runtime is zero.";
      return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_ARGUMENT: *statusString = "invalid argument"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_AGENT: *statusString = "invalid agent"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_REGION: *statusString = "invalid region"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_SIGNAL: *statusString = "invalid signal"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_QUEUE: *statusString = "invalid queue"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_EXECUTABLE: *statusString = "invalid executable"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER: *statusString = "invalid code object reader"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_SYMBOL_NAME: *statusString = "invalid symbol name"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_FROZEN_EXECUTABLE: *statusString = "frozen executable"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INCOMPATIBLE_ARGUMENTS: *statusString = "incompatible arguments"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_PACKET_FORMAT: *statusString = "invalid packet format"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_CODE_OBJECT: *statusString = "invalid code object"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_EXCEPTION: *statusString = "hardware exception"; return HSA_STATUS_SUCCESS;
    default: *statusString = "HSA error"; return HSA_STATUS_SUCCESS;
  }
}

extern "C" hsa_status_t hsa_system_get_info(hsa_system_info_t attribute, void *value) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!value) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  switch (attribute) {
    case HSA_SYSTEM_INFO_VERSION_MAJOR: return copyStatus(value, uint16_t(1));
    case HSA_SYSTEM_INFO_VERSION_MINOR: return copyStatus(value, uint16_t(0));
    case HSA_SYSTEM_INFO_TIMESTAMP: {
      const auto now = std::chrono::duration_cast<std::chrono::microseconds>(std::chrono::steady_clock::now().time_since_epoch()).count();
      return copyStatus(value, static_cast<uint64_t>(now));
    }
    case HSA_SYSTEM_INFO_TIMESTAMP_FREQUENCY: return copyStatus(value, uint64_t(1000000));
    case HSA_SYSTEM_INFO_SIGNAL_MAX_WAIT: return copyStatus(value, UINT64_MAX);
    case HSA_SYSTEM_INFO_ENDIANNESS: return copyStatus(value, HSA_ENDIANNESS_LITTLE);
    case HSA_SYSTEM_INFO_MACHINE_MODEL: return copyStatus(value, HSA_MACHINE_MODEL_LARGE);
    case HSA_SYSTEM_INFO_EXTENSIONS: std::memset(value, 0, 128); return HSA_STATUS_SUCCESS;
    case HSA_AMD_SYSTEM_INFO_SVM_SUPPORTED: return copyStatus(value, false);
    case HSA_AMD_SYSTEM_INFO_SVM_ACCESSIBLE_BY_DEFAULT: return copyStatus(value, false);
    case HSA_AMD_SYSTEM_INFO_EXT_VERSION_MAJOR: return copyStatus(value, uint16_t(HSA_AMD_INTERFACE_VERSION_MAJOR));
    case HSA_AMD_SYSTEM_INFO_EXT_VERSION_MINOR: return copyStatus(value, uint16_t(HSA_AMD_INTERFACE_VERSION_MINOR));
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

// No public extension tables are advertised by HSA_SYSTEM_INFO_EXTENSIONS yet.
// Callers may probe an optional table; reject it without publishing null function pointers as supported.
extern "C" hsa_status_t hsa_system_get_major_extension_table(uint16_t extension, uint16_t versionMajor, size_t, void *) {
  traceApiCall("hsa_system_get_major_extension_table", extension, versionMajor);
  if (const auto status = requireInitialised(state()); status != HSA_STATUS_SUCCESS) return status;
  return HSA_STATUS_ERROR_INVALID_ARGUMENT;
}

extern "C" hsa_status_t hsa_init() {
  traceApiCall("hsa_init");
  auto &runtime = state();
  std::unique_lock<std::mutex> guard(runtime.lock);
  if (runtime.references == std::numeric_limits<uint32_t>::max()) return HSA_STATUS_ERROR_REFCOUNT_OVERFLOW;
  if (runtime.references++ != 0) return HSA_STATUS_SUCCESS;
  const auto reset = [&](hsa_status_t status) {
    runtime.monitor.reset();
    runtime.aql.reset();
    runtime.client.reset();
    runtime.references = 0;
    traceApiResult("hsa_init", status);
    return status;
  };
  runtime.client = Client::fromEnvironment();
  runtime.aql = AqlRuntime::fromEnvironment(std::make_unique<PublicSignals>(runtime));
  if (!runtime.client || !runtime.aql || !runtime.client->call(Ping{})) return reset(HSA_STATUS_ERROR_NOT_INITIALIZED);
  runtime.asyncStopping = false;
  try {
    runtime.monitor = std::make_unique<DoorbellMonitor>();
    for (size_t index = 0; index < 4; ++index)
      runtime.asyncWorkers.emplace_back(runAsyncHandlers, std::ref(runtime));
  } catch (...) {
    runtime.asyncStopping = true;
    runtime.asyncChanged.notify_all();
    guard.unlock();
    for (auto &worker : runtime.asyncWorkers)
      if (worker.joinable()) worker.join();
    runtime.asyncWorkers.clear();
    guard.lock();
    return reset(HSA_STATUS_ERROR_OUT_OF_RESOURCES);
  }
  traceApiResult("hsa_init", HSA_STATUS_SUCCESS);
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_shut_down() {
  auto &runtime = state();
  std::unique_lock<std::mutex> guard(runtime.lock);
  if (runtime.references == 0) return HSA_STATUS_ERROR_NOT_INITIALIZED;
  if (runtime.references > 1) {
    --runtime.references;
    return HSA_STATUS_SUCCESS;
  }
  if (!runtime.queues.empty() || !runtime.allocations.empty() || !runtime.signals.empty() || !runtime.readers.empty()
      || !runtime.executables.empty())
    return HSA_STATUS_ERROR_INVALID_RUNTIME_STATE;
  runtime.asyncStopping = true;
  runtime.asyncChanged.notify_all();
  guard.unlock();
  for (auto &worker : runtime.asyncWorkers)
    if (worker.joinable()) worker.join();
  runtime.asyncWorkers.clear();
  guard.lock();
  runtime.monitor.reset();
  runtime.aql.reset();
  runtime.client.reset();
  runtime.references = 0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_iterate_agents(hsa_status_t (*callback)(hsa_agent_t, void *), void *data) {
  traceApiCall("hsa_iterate_agents");
  auto &runtime = state();
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
    if (!callback) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
  const auto cpu = callback(hsa_agent_t{CpuAgent}, data);
  traceApiResult("hsa_iterate_agents.cpu", cpu);
  if (cpu != HSA_STATUS_SUCCESS) return cpu;
  const auto gpu = callback(hsa_agent_t{GpuAgent}, data);
  traceApiResult("hsa_iterate_agents.gpu", gpu);
  return gpu;
}

extern "C" hsa_status_t hsa_agent_get_info(hsa_agent_t agent, hsa_agent_info_t attribute, void *value) {
  traceApiCall("hsa_agent_get_info", agent.handle, attribute);
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validAgent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (!value) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const bool gpu = validGpu(agent);
  // Software agents have no physical PCI identity or HDP registers. ROCclr
  // queries these properties during discovery even when it does not use them.
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_CHIP_ID)
      || attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_BDFID)
      || attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_DOMAIN)
      || attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MEMORY_WIDTH)
      || attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_ASIC_REVISION))
    return copyStatus(value, uint32_t(0));
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COOPERATIVE_QUEUES)) return copyStatus(value, false);
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_HDP_FLUSH))
    return copyStatus(value, hsa_amd_hdp_flush_t{nullptr, nullptr});
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_PRODUCT_NAME)) {
    copyName(value, gpu ? gemstone::hsa::targetArch() : "Gemstone CPU");
    return HSA_STATUS_SUCCESS;
  }
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_TIMESTAMP_FREQUENCY)) return copyStatus(value, uint64_t(1000000000));
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_CACHELINE_SIZE)) return copyStatus(value, uint32_t(64));
  // These are software execution engines, with no device or VRAM clock.
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MAX_CLOCK_FREQUENCY)
      || attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MEMORY_MAX_FREQUENCY))
    return copyStatus(value, uint32_t(0));
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_DRIVER_NODE_ID)) return copyStatus(value, uint32_t(gpu ? 1 : 0));
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_SCRATCH_LIMIT_MAX)) return copyStatus(value, uint64_t(0));
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MEMORY_PROPERTIES)) {
    std::memset(value, 0, 8);
    return HSA_STATUS_SUCCESS;
  }
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COMPUTE_UNIT_COUNT)
      || attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COOPERATIVE_COMPUTE_UNIT_COUNT)) {
    // Report one schedulable unit: RDNA's WGP consists of two CUs. ROCclr
    // divides this count by two in WGP mode when sizing its copy dispatches.
    const std::string target = gemstone::hsa::targetArch();
    const bool wgp = (target ^ starts_with("gfx10")) || (target ^ starts_with("gfx11")) || (target ^ starts_with("gfx12"));
    return copyStatus(value, uint32_t(gpu ? (wgp ? 2 : 1) : 0));
  }
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_NUM_SIMDS_PER_CU)) return copyStatus(value, uint32_t(gpu ? 1 : 0));
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MAX_WAVES_PER_CU)) return copyStatus(value, uint32_t(gpu ? 10 : 0));
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_NUM_SDMA_ENG)) return copyStatus(value, uint32_t(gpu ? 1 : 0));
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_NUM_SDMA_XGMI_ENG)) return copyStatus(value, uint32_t(0));
  switch (attribute) {
    case HSA_AGENT_INFO_NAME:
      copyName(value, gpu ? gemstone::hsa::targetArch() : "host");
      traceApiText("hsa_agent_get_info.name", gpu ? gemstone::hsa::targetArch() : "host");
      return HSA_STATUS_SUCCESS;
    case HSA_AGENT_INFO_VENDOR_NAME: copyName(value, "Gemstone"); return HSA_STATUS_SUCCESS;
    case HSA_AGENT_INFO_FEATURE: return copyStatus(value, gpu ? HSA_AGENT_FEATURE_KERNEL_DISPATCH : hsa_agent_feature_t(0));
    case HSA_AGENT_INFO_MACHINE_MODEL: return copyStatus(value, HSA_MACHINE_MODEL_LARGE);
    // Device pointers must be registered; arbitrary host pointers are not
    // executable memory, so advertising the full profile would bypass registration.
    case HSA_AGENT_INFO_PROFILE: return copyStatus(value, HSA_PROFILE_BASE);
    case HSA_AGENT_INFO_DEFAULT_FLOAT_ROUNDING_MODE: return copyStatus(value, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR);
    case HSA_AGENT_INFO_WAVEFRONT_SIZE: return copyStatus(value, uint32_t(gpu ? gemstone::hsa::waveSize() : 1));
    case HSA_AGENT_INFO_WORKGROUP_MAX_DIM: {
      const uint16_t dimensions[3] = {1024, 1024, 1024};
      std::memcpy(value, dimensions, sizeof(dimensions));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_AGENT_INFO_WORKGROUP_MAX_SIZE: return copyStatus(value, uint32_t(gpu ? 1024 : 1));
    case HSA_AGENT_INFO_GRID_MAX_DIM: return copyStatus(value, hsa_dim3_t{UINT32_MAX, UINT32_MAX, UINT32_MAX});
    case HSA_AGENT_INFO_GRID_MAX_SIZE: return copyStatus(value, uint32_t(UINT32_MAX));
    case HSA_AGENT_INFO_FBARRIER_MAX_SIZE: return copyStatus(value, uint32_t(gpu ? 32 : 0));
    case HSA_AGENT_INFO_QUEUES_MAX: return copyStatus(value, uint32_t(gpu ? 128 : 0));
    case HSA_AGENT_INFO_QUEUE_MIN_SIZE: return copyStatus(value, uint32_t(1));
    case HSA_AGENT_INFO_QUEUE_MAX_SIZE: return copyStatus(value, uint32_t(gpu ? QueueMaximum : 0));
    case HSA_AGENT_INFO_QUEUE_TYPE: return copyStatus(value, hsa_queue_type32_t(HSA_QUEUE_TYPE_MULTI));
    case HSA_AGENT_INFO_NODE: return copyStatus(value, uint32_t(gpu ? 1 : 0));
    case HSA_AGENT_INFO_DEVICE: return copyStatus(value, gpu ? HSA_DEVICE_TYPE_GPU : HSA_DEVICE_TYPE_CPU);
    case HSA_AGENT_INFO_CACHE_SIZE: {
      const uint32_t cache[4] = {gpu ? 16U * 1024U : 32U * 1024U, 0, 0, 0};
      std::memcpy(value, cache, sizeof(cache));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_AGENT_INFO_ISA: return copyStatus(value, hsa_isa_t{uint64_t(gpu ? GpuIsa : 0)});
    case HSA_AGENT_INFO_EXTENSIONS: std::memset(value, 0, 128); return HSA_STATUS_SUCCESS;
    case HSA_AGENT_INFO_VERSION_MAJOR: return copyStatus(value, uint16_t(1));
    case HSA_AGENT_INFO_VERSION_MINOR: return copyStatus(value, uint16_t(0));
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_isa_from_name(const char *name, hsa_isa_t *isa) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!name || !isa) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (!isRequestedIsa(name)) return HSA_STATUS_ERROR_INVALID_ISA_NAME;
  *isa = hsa_isa_t{GpuIsa};
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_agent_iterate_isas(hsa_agent_t agent, hsa_status_t (*callback)(hsa_isa_t, void *), void *data) {
  traceApiCall("hsa_agent_iterate_isas", agent.handle);
  auto &runtime = state();
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
    if (!validAgent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
    if (!callback) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
  const auto status = validGpu(agent) ? callback(hsa_isa_t{GpuIsa}, data) : HSA_STATUS_SUCCESS;
  traceApiResult("hsa_agent_iterate_isas", status);
  return status;
}

extern "C" hsa_status_t hsa_isa_get_info_alt(hsa_isa_t isa, hsa_isa_info_t attribute, void *value) {
  traceApiCall("hsa_isa_get_info_alt", isa.handle, attribute);
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validIsa(isa)) return HSA_STATUS_ERROR_INVALID_ISA;
  if (!value) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  // LLVM's AMDGPU offload plugin consumes the HSA ISA name as an LLVM target
  // triple, rather than the older human-readable AMD:AMDGPU spelling.
  const std::string name = std::string("amdgcn-amd-amdhsa--") + gemstone::hsa::targetArch()
                           + (gemstone::environment::enabled(gemstone::environment::SystemPaging) ? ":xnack+" : "");
  switch (attribute) {
    case HSA_ISA_INFO_NAME_LENGTH: return copyStatus(value, static_cast<uint32_t>(name.size()));
    case HSA_ISA_INFO_NAME:
      std::memcpy(value, name.data(), name.size());
      traceApiText("hsa_isa_get_info_alt.name", name);
      return HSA_STATUS_SUCCESS;
    case HSA_ISA_INFO_CALL_CONVENTION_COUNT: return copyStatus(value, uint32_t(1));
    case HSA_ISA_INFO_CALL_CONVENTION_INFO_WAVEFRONT_SIZE: return copyStatus(value, uint32_t(64));
    case HSA_ISA_INFO_CALL_CONVENTION_INFO_WAVEFRONTS_PER_COMPUTE_UNIT: return copyStatus(value, uint32_t(10));
    case HSA_ISA_INFO_MACHINE_MODELS: {
      const bool models[2] = {false, true};
      std::memcpy(value, models, sizeof(models));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_ISA_INFO_PROFILES: {
      const bool profiles[2] = {true, true};
      std::memcpy(value, profiles, sizeof(profiles));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_ISA_INFO_DEFAULT_FLOAT_ROUNDING_MODES: {
      const bool modes[3] = {false, true, false};
      std::memcpy(value, modes, sizeof(modes));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_ISA_INFO_BASE_PROFILE_DEFAULT_FLOAT_ROUNDING_MODES: {
      const bool modes[3] = {false, true, false};
      std::memcpy(value, modes, sizeof(modes));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_ISA_INFO_FAST_F16_OPERATION: return copyStatus(value, true);
    case HSA_ISA_INFO_WORKGROUP_MAX_DIM: {
      const uint16_t dimensions[3] = {1024, 1024, 1024};
      std::memcpy(value, dimensions, sizeof(dimensions));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_ISA_INFO_WORKGROUP_MAX_SIZE: return copyStatus(value, uint32_t(1024));
    case HSA_ISA_INFO_GRID_MAX_DIM: return copyStatus(value, hsa_dim3_t{UINT32_MAX, UINT32_MAX, UINT32_MAX});
    case HSA_ISA_INFO_GRID_MAX_SIZE: return copyStatus(value, uint64_t(UINT64_MAX));
    case HSA_ISA_INFO_FBARRIER_MAX_SIZE: return copyStatus(value, uint32_t(32));
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_isa_get_info(hsa_isa_t isa, hsa_isa_info_t attribute, uint32_t index, void *value) {
  if ((attribute == HSA_ISA_INFO_CALL_CONVENTION_INFO_WAVEFRONT_SIZE
       || attribute == HSA_ISA_INFO_CALL_CONVENTION_INFO_WAVEFRONTS_PER_COMPUTE_UNIT)
      && index != 0)
    return HSA_STATUS_ERROR_INVALID_INDEX;
  return hsa_isa_get_info_alt(isa, attribute, value);
}

extern "C" hsa_status_t hsa_isa_compatible(hsa_isa_t codeObjectIsa, hsa_isa_t agentIsa, bool *result) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validIsa(codeObjectIsa) || !validIsa(agentIsa)) return HSA_STATUS_ERROR_INVALID_ISA;
  if (!result) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  *result = codeObjectIsa.handle == agentIsa.handle;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_agent_iterate_regions(hsa_agent_t agent, hsa_status_t (*callback)(hsa_region_t, void *), void *data) {
  traceApiCall("hsa_agent_iterate_regions", agent.handle);
  auto &runtime = state();
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
    if (!validAgent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
    if (!callback) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
  auto status = callback(hsa_region_t{CpuRegion}, data);
  if (status == HSA_STATUS_SUCCESS && validGpu(agent)) status = callback(hsa_region_t{GpuRegion}, data);
  traceApiResult("hsa_agent_iterate_regions", status);
  return status;
}

extern "C" hsa_status_t hsa_region_get_info(hsa_region_t region, hsa_region_info_t attribute, void *value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validRegion(region)) return HSA_STATUS_ERROR_INVALID_REGION;
  if (!value) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const uint32_t flags = region.handle == CpuRegion ? HSA_REGION_GLOBAL_FLAG_KERNARG | HSA_REGION_GLOBAL_FLAG_FINE_GRAINED
                                                    : HSA_REGION_GLOBAL_FLAG_COARSE_GRAINED;
  switch (attribute) {
    case HSA_REGION_INFO_SEGMENT: return copyStatus(value, HSA_REGION_SEGMENT_GLOBAL);
    case HSA_REGION_INFO_GLOBAL_FLAGS: return copyStatus(value, flags);
    case HSA_REGION_INFO_SIZE: return copyStatus(value, size_t(VirtualMemorySize));
    case HSA_REGION_INFO_ALLOC_MAX_SIZE: return copyStatus(value, size_t(VirtualMemorySize));
    case HSA_REGION_INFO_RUNTIME_ALLOC_ALLOWED: return copyStatus(value, true);
    case HSA_REGION_INFO_RUNTIME_ALLOC_GRANULE: return copyStatus(value, size_t(AllocationGranule));
    case HSA_REGION_INFO_RUNTIME_ALLOC_ALIGNMENT: return copyStatus(value, size_t(AllocationGranule));
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_memory_allocate(hsa_region_t region, size_t size, void **ptr) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validRegion(region)) return HSA_STATUS_ERROR_INVALID_REGION;
  return allocate(runtime, region.handle == CpuRegion ? CpuPool : GpuPool, size, ptr, true);
}

extern "C" hsa_status_t hsa_memory_free(void *ptr) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!ptr) return HSA_STATUS_SUCCESS;
  return freeAllocation(runtime, ptr, true);
}

extern "C" hsa_status_t hsa_memory_copy(void *dst, const void *src, size_t size) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if ((!dst || !src) && size != 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (size == 0) return HSA_STATUS_SUCCESS;
  const gemstone::MemoryRange input{reinterpret_cast<uintptr_t>(src), size}, output{reinterpret_cast<uintptr_t>(dst), size};
  if (!input.valid() || !output.valid()) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::vector<AllocationRef> sources, destinations;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (!findAllocation(runtime, src, size) && !findAllocation(runtime, dst, size) && !runtime.registrations.find(input.base, size)
        && !runtime.registrations.find(output.base, size))
      return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    for (const auto &[base, allocation] : runtime.allocations) {
      const gemstone::MemoryRange backing{reinterpret_cast<uintptr_t>(base), allocation->size};
      if (backing.overlaps(input) && allocation->pool == GpuPool) sources.push_back(allocation);
      if (backing.overlaps(output) && allocation->pool == GpuPool) destinations.push_back(allocation);
    }
  }
  const auto transfer = [&](const AllocationRef &allocation, gemstone::MemoryRange range, bool read) {
    const auto base = reinterpret_cast<uintptr_t>(allocation->base);
    const auto begin = std::max(base, range.base), end = std::min(base + allocation->size, range.end());
    return read ? download(runtime, allocation, begin - base, end - begin) : upload(runtime, allocation, begin - base, end - begin);
  };
  // Only device-owned storage is remote-authoritative between commands.
  for (const auto &allocation : sources)
    if (transfer(allocation, input, true) != 0) return HSA_STATUS_ERROR;
  std::memmove(dst, src, size);
  for (const auto &allocation : destinations)
    if (transfer(allocation, output, false) != 0) return HSA_STATUS_ERROR;
  return HSA_STATUS_SUCCESS;
}

namespace {
hsa_status_t registerHostSegment(void *ptr, size_t size) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!ptr || size == 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (findAllocation(runtime, ptr, size) != nullptr) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
  }
  return registerRemoteAllocation(runtime, ptr, size, CpuPool, false, true, [] {});
}

hsa_status_t registerHostView(RuntimeState &runtime, void *ptr, size_t size, bool allowOverlap) {
  const gemstone::MemoryRange range{reinterpret_cast<uintptr_t>(ptr), size};
  if (!range.valid()) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> transaction(runtime.registrationLock);
  std::vector<gemstone::MemoryRange> covered;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (!allowOverlap && runtime.registrations.overlaps(range)) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    for (const auto &[_, allocation] : runtime.allocations)
      covered.push_back({reinterpret_cast<uintptr_t>(allocation->base), allocation->size});
  }
  std::vector<void *> created;
  for (const auto missing : gemstone::missingMemoryRanges(range, covered)) {
    auto *base = reinterpret_cast<void *>(missing.base);
    const auto status = registerHostSegment(base, missing.size);
    if (status != HSA_STATUS_SUCCESS) {
      for (auto *rollback : created)
        (void)freeAllocation(runtime, rollback, false);
      return status;
    }
    created.push_back(base);
  }
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.registrations.add(range, gemstone::MemoryOwnership::Borrowed, 0, true);
  }
  return HSA_STATUS_SUCCESS;
}

hsa_status_t releaseHostView(RuntimeState &runtime, void *ptr, size_t size) {
  std::lock_guard<std::mutex> transaction(runtime.registrationLock);
  std::vector<void *> unused;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto view = runtime.registrations.exact(reinterpret_cast<uintptr_t>(ptr), size);
    if (!view || (size && view->range.size != size)) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    runtime.registrations.remove(reinterpret_cast<uintptr_t>(ptr), gemstone::MemoryOwnership::Borrowed, size);
    for (const auto &[base, allocation] : runtime.allocations)
      if (!allocation->owned && !runtime.registrations.overlaps({reinterpret_cast<uintptr_t>(base), allocation->size}))
        unused.push_back(base);
  }
  for (auto *base : unused) {
    const auto status = freeAllocation(runtime, base, false);
    if (status != HSA_STATUS_SUCCESS) return status;
  }
  return HSA_STATUS_SUCCESS;
}
} // namespace

extern "C" hsa_status_t hsa_memory_register(void *ptr, size_t size) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  return registerHostView(runtime, ptr, size, false);
}
extern "C" hsa_status_t hsa_memory_deregister(void *ptr, size_t size) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  return releaseHostView(runtime, ptr, size);
}

extern "C" hsa_status_t hsa_memory_assign_agent(void *ptr, hsa_agent_t agent, hsa_access_permission_t access) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validAgent(agent) || !ptr || access > HSA_ACCESS_PERMISSION_RW) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  return findAllocation(runtime, ptr) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ALLOCATION;
}

extern "C" hsa_status_t hsa_signal_create(hsa_signal_value_t initialValue, uint32_t numConsumers, const hsa_agent_t *consumers,
                                          hsa_signal_t *signal) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!signal || (numConsumers != 0 && !consumers)) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  for (uint32_t index = 0; index < numConsumers; ++index)
    if (!validAgent(consumers[index])) return HSA_STATUS_ERROR_INVALID_AGENT;
  try {
    auto value = std::make_shared<Signal>();
    value->value = initialValue;
    std::lock_guard<std::mutex> guard(runtime.lock);
    const uint64_t handle = runtime.nextHandle++;
    runtime.signals.emplace(handle, value);
    signal->handle = handle;
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_amd_signal_create(hsa_signal_value_t initialValue, uint32_t numConsumers, const hsa_agent_t *consumers,
                                              uint64_t, hsa_signal_t *signal) {
  return hsa_signal_create(initialValue, numConsumers, consumers, signal);
}

extern "C" hsa_status_t hsa_signal_destroy(hsa_signal_t signal) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.signals.find(signal.handle);
  if (found == runtime.signals.end() || found->second->doorbell) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  const auto value = found->second;
  value->live.store(false, std::memory_order_release);
  runtime.signals.erase(found);
  runtime.asyncHandlers ^= filter([&](const auto &entry) { return entry->signal != value; });
  runtime.asyncChanged.notify_all();
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_signal_value_t hsa_signal_load_scacquire(hsa_signal_t signal) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = findSignal(runtime, signal);
  return value ? __atomic_load_n(&value->value, __ATOMIC_ACQUIRE) : 0;
}

extern "C" hsa_status_t hsa_amd_signal_value_pointer(hsa_signal_t signal, volatile hsa_signal_value_t **valuePtr) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valuePtr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = findSignal(runtime, signal);
  if (!value) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  *valuePtr = &value->value;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_signal_value_t hsa_signal_load_relaxed(hsa_signal_t signal) { return hsa_signal_load_scacquire(signal); }
extern "C" hsa_signal_value_t hsa_signal_load_acquire(hsa_signal_t signal) { return hsa_signal_load_scacquire(signal); }

namespace {

enum class SignalUpdate { Add, And, Or, Xor };

hsa_signal_value_t updateSignal(hsa_signal_t signal, hsa_signal_value_t value, SignalUpdate operation) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = findSignal(runtime, signal);
  if (!target) return 0;
  hsa_signal_value_t previous = 0;
  switch (operation) {
    case SignalUpdate::Add: previous = __atomic_fetch_add(&target->value, value, __ATOMIC_SEQ_CST); break;
    case SignalUpdate::And: previous = __atomic_fetch_and(&target->value, value, __ATOMIC_SEQ_CST); break;
    case SignalUpdate::Or: previous = __atomic_fetch_or(&target->value, value, __ATOMIC_SEQ_CST); break;
    case SignalUpdate::Xor: previous = __atomic_fetch_xor(&target->value, value, __ATOMIC_SEQ_CST); break;
  }
  const auto current = __atomic_load_n(&target->value, __ATOMIC_RELAXED);
  if (target->doorbell) __atomic_store_n(target->doorbell, static_cast<uint64_t>(current), __ATOMIC_RELEASE);
  runtime.asyncChanged.notify_all();
  return previous;
}

hsa_signal_value_t compareExchangeSignal(hsa_signal_t signal, hsa_signal_value_t expected, hsa_signal_value_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = findSignal(runtime, signal);
  if (!target) return 0;
  auto observed = expected;
  __atomic_compare_exchange_n(&target->value, &observed, value, false, __ATOMIC_SEQ_CST, __ATOMIC_SEQ_CST);
  const auto current = __atomic_load_n(&target->value, __ATOMIC_RELAXED);
  if (target->doorbell) __atomic_store_n(target->doorbell, static_cast<uint64_t>(current), __ATOMIC_RELEASE);
  runtime.asyncChanged.notify_all();
  return observed;
}

hsa_signal_value_t exchangeSignal(hsa_signal_t signal, hsa_signal_value_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = findSignal(runtime, signal);
  if (!target) return 0;
  const auto previous = __atomic_exchange_n(&target->value, value, __ATOMIC_SEQ_CST);
  if (target->doorbell) __atomic_store_n(target->doorbell, static_cast<uint64_t>(value), __ATOMIC_RELEASE);
  runtime.asyncChanged.notify_all();
  return previous;
}

} // namespace

#define GEMSTONE_SIGNAL_UPDATE_VARIANTS(operation, kind)                                                                                   \
  extern "C" void hsa_signal_##operation##_relaxed(hsa_signal_t signal, hsa_signal_value_t value) {                                        \
    (void)updateSignal(signal, value, SignalUpdate::kind);                                                                                 \
  }                                                                                                                                        \
  extern "C" void hsa_signal_##operation##_acquire(hsa_signal_t signal, hsa_signal_value_t value) {                                        \
    (void)updateSignal(signal, value, SignalUpdate::kind);                                                                                 \
  }                                                                                                                                        \
  extern "C" void hsa_signal_##operation##_release(hsa_signal_t signal, hsa_signal_value_t value) {                                        \
    (void)updateSignal(signal, value, SignalUpdate::kind);                                                                                 \
  }                                                                                                                                        \
  extern "C" void hsa_signal_##operation##_acq_rel(hsa_signal_t signal, hsa_signal_value_t value) {                                        \
    (void)updateSignal(signal, value, SignalUpdate::kind);                                                                                 \
  }                                                                                                                                        \
  extern "C" void hsa_signal_##operation##_scacquire(hsa_signal_t signal, hsa_signal_value_t value) {                                      \
    (void)updateSignal(signal, value, SignalUpdate::kind);                                                                                 \
  }                                                                                                                                        \
  extern "C" void hsa_signal_##operation##_screlease(hsa_signal_t signal, hsa_signal_value_t value) {                                      \
    (void)updateSignal(signal, value, SignalUpdate::kind);                                                                                 \
  }                                                                                                                                        \
  extern "C" void hsa_signal_##operation##_scacq_screl(hsa_signal_t signal, hsa_signal_value_t value) {                                    \
    (void)updateSignal(signal, value, SignalUpdate::kind);                                                                                 \
  }

GEMSTONE_SIGNAL_UPDATE_VARIANTS(add, Add)
GEMSTONE_SIGNAL_UPDATE_VARIANTS(and, And)
GEMSTONE_SIGNAL_UPDATE_VARIANTS(or, Or)
GEMSTONE_SIGNAL_UPDATE_VARIANTS(xor, Xor)

#undef GEMSTONE_SIGNAL_UPDATE_VARIANTS

#define GEMSTONE_SIGNAL_VALUE_VARIANTS(operation, implementation)                                                                          \
  extern "C" hsa_signal_value_t hsa_signal_##operation##_relaxed(hsa_signal_t signal, hsa_signal_value_t first,                            \
                                                                 hsa_signal_value_t second) {                                              \
    return implementation(signal, first, second);                                                                                          \
  }                                                                                                                                        \
  extern "C" hsa_signal_value_t hsa_signal_##operation##_acquire(hsa_signal_t signal, hsa_signal_value_t first,                            \
                                                                 hsa_signal_value_t second) {                                              \
    return implementation(signal, first, second);                                                                                          \
  }                                                                                                                                        \
  extern "C" hsa_signal_value_t hsa_signal_##operation##_release(hsa_signal_t signal, hsa_signal_value_t first,                            \
                                                                 hsa_signal_value_t second) {                                              \
    return implementation(signal, first, second);                                                                                          \
  }                                                                                                                                        \
  extern "C" hsa_signal_value_t hsa_signal_##operation##_acq_rel(hsa_signal_t signal, hsa_signal_value_t first,                            \
                                                                 hsa_signal_value_t second) {                                              \
    return implementation(signal, first, second);                                                                                          \
  }                                                                                                                                        \
  extern "C" hsa_signal_value_t hsa_signal_##operation##_scacquire(hsa_signal_t signal, hsa_signal_value_t first,                          \
                                                                   hsa_signal_value_t second) {                                            \
    return implementation(signal, first, second);                                                                                          \
  }                                                                                                                                        \
  extern "C" hsa_signal_value_t hsa_signal_##operation##_screlease(hsa_signal_t signal, hsa_signal_value_t first,                          \
                                                                   hsa_signal_value_t second) {                                            \
    return implementation(signal, first, second);                                                                                          \
  }                                                                                                                                        \
  extern "C" hsa_signal_value_t hsa_signal_##operation##_scacq_screl(hsa_signal_t signal, hsa_signal_value_t first,                        \
                                                                     hsa_signal_value_t second) {                                          \
    return implementation(signal, first, second);                                                                                          \
  }

GEMSTONE_SIGNAL_VALUE_VARIANTS(cas, compareExchangeSignal)

#undef GEMSTONE_SIGNAL_VALUE_VARIANTS

#define GEMSTONE_SIGNAL_EXCHANGE(name)                                                                                                     \
  extern "C" hsa_signal_value_t hsa_signal_exchange_##name(hsa_signal_t signal, hsa_signal_value_t value) {                                \
    return exchangeSignal(signal, value);                                                                                                  \
  }

GEMSTONE_SIGNAL_EXCHANGE(relaxed)
GEMSTONE_SIGNAL_EXCHANGE(acquire)
GEMSTONE_SIGNAL_EXCHANGE(release)
GEMSTONE_SIGNAL_EXCHANGE(acq_rel)
GEMSTONE_SIGNAL_EXCHANGE(scacquire)
GEMSTONE_SIGNAL_EXCHANGE(screlease)
GEMSTONE_SIGNAL_EXCHANGE(scacq_screl)

#undef GEMSTONE_SIGNAL_EXCHANGE

extern "C" void hsa_signal_store_relaxed(hsa_signal_t signal, hsa_signal_value_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = findSignal(runtime, signal);
  if (!target) return;
  __atomic_store_n(&target->value, value, __ATOMIC_RELAXED);
  if (target->doorbell) __atomic_store_n(target->doorbell, static_cast<uint64_t>(value), __ATOMIC_RELEASE);
  runtime.asyncChanged.notify_all();
}

extern "C" void hsa_signal_store_screlease(hsa_signal_t signal, hsa_signal_value_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = findSignal(runtime, signal);
  if (tracePackets())
    std::fprintf(stderr, "Gemstone HSA signal store handle=%lu value=%ld found=%d doorbell=%p\n", signal.handle,
                 static_cast<int64_t>(value), target ? 1 : 0, target ? static_cast<void *>(target->doorbell) : nullptr);
  if (!target) return;
  __atomic_store_n(&target->value, value, __ATOMIC_RELEASE);
  if (target->doorbell) __atomic_store_n(target->doorbell, static_cast<uint64_t>(value), __ATOMIC_RELEASE);
  runtime.asyncChanged.notify_all();
}

extern "C" void hsa_signal_subtract_screlease(hsa_signal_t signal, hsa_signal_value_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = findSignal(runtime, signal);
  if (!target) return;
  const auto result = __atomic_fetch_sub(&target->value, value, __ATOMIC_RELEASE) - value;
  if (target->doorbell) __atomic_store_n(target->doorbell, static_cast<uint64_t>(result), __ATOMIC_RELEASE);
  runtime.asyncChanged.notify_all();
}

#define GEMSTONE_SIGNAL_SUBTRACT(name)                                                                                                     \
  extern "C" void hsa_signal_subtract_##name(hsa_signal_t signal, hsa_signal_value_t value) {                                              \
    hsa_signal_subtract_screlease(signal, value);                                                                                          \
  }

GEMSTONE_SIGNAL_SUBTRACT(relaxed)
GEMSTONE_SIGNAL_SUBTRACT(acquire)
GEMSTONE_SIGNAL_SUBTRACT(release)
GEMSTONE_SIGNAL_SUBTRACT(acq_rel)
GEMSTONE_SIGNAL_SUBTRACT(scacquire)
GEMSTONE_SIGNAL_SUBTRACT(scacq_screl)

#undef GEMSTONE_SIGNAL_SUBTRACT

// Silent stores may notify waiters; the software signal implementation uses the
// same polling mechanism for both forms and preserves the specified ordering.
extern "C" void hsa_signal_silent_store_relaxed(hsa_signal_t signal, hsa_signal_value_t value) { hsa_signal_store_relaxed(signal, value); }
extern "C" void hsa_signal_silent_store_screlease(hsa_signal_t signal, hsa_signal_value_t value) {
  hsa_signal_store_screlease(signal, value);
}

extern "C" void hsa_signal_store_release(hsa_signal_t signal, hsa_signal_value_t value) { hsa_signal_store_screlease(signal, value); }

extern "C" hsa_signal_value_t hsa_signal_wait_scacquire(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare,
                                                        uint64_t timeoutHint, hsa_wait_state_t) {
  return waitSignal(signal, condition, compare, timeoutHint);
}
extern "C" hsa_signal_value_t hsa_signal_wait_relaxed(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare,
                                                      uint64_t timeoutHint, hsa_wait_state_t stateHint) {
  return hsa_signal_wait_scacquire(signal, condition, compare, timeoutHint, stateHint);
}
extern "C" hsa_signal_value_t hsa_signal_wait_acquire(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare,
                                                      uint64_t timeoutHint, hsa_wait_state_t stateHint) {
  return hsa_signal_wait_scacquire(signal, condition, compare, timeoutHint, stateHint);
}

extern "C" uint32_t hsa_amd_signal_wait_all(uint32_t count, hsa_signal_t *signals, hsa_signal_condition_t *conditions,
                                            hsa_signal_value_t *compares, uint64_t timeoutHint, hsa_wait_state_t,
                                            hsa_signal_value_t *satisfyingValues) {
  if (!signals || !conditions || !compares) return UINT32_MAX;
  const auto start = std::chrono::steady_clock::now();
  for (;;) {
    bool satisfied = true;
    for (uint32_t index = 0; index < count; ++index) {
      const auto current = hsa_signal_load_scacquire(signals[index]);
      if (satisfyingValues) satisfyingValues[index] = current;
      if (!signalCondition(current, conditions[index], compares[index])) satisfied = false;
    }
    if (satisfied) return 0;
    if (timeoutHint != UINT64_MAX) {
      const auto elapsed = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now() - start);
      if (elapsed.count() >= 0 && static_cast<uint64_t>(elapsed.count()) >= timeoutHint) return UINT32_MAX;
    }
    std::this_thread::yield();
  }
}

extern "C" uint32_t hsa_amd_signal_wait_any(uint32_t count, hsa_signal_t *signals, hsa_signal_condition_t *conditions,
                                            hsa_signal_value_t *compares, uint64_t timeoutHint, hsa_wait_state_t,
                                            hsa_signal_value_t *satisfyingValue) {
  if (!signals || !conditions || !compares) return UINT32_MAX;
  const auto start = std::chrono::steady_clock::now();
  for (;;) {
    bool valid = false;
    for (uint32_t index = 0; index < count; ++index) {
      auto &runtime = state();
      std::shared_ptr<Signal> signal;
      {
        std::lock_guard<std::mutex> guard(runtime.lock);
        signal = findSignal(runtime, signals[index]);
      }
      if (!signal) continue;
      valid = true;
      const auto current = __atomic_load_n(&signal->value, __ATOMIC_ACQUIRE);
      if (signalCondition(current, conditions[index], compares[index])) {
        if (satisfyingValue) *satisfyingValue = current;
        return index;
      }
    }
    if (!valid) return UINT32_MAX;
    if (timeoutHint != UINT64_MAX) {
      const auto elapsed = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now() - start);
      if (elapsed.count() >= 0 && static_cast<uint64_t>(elapsed.count()) >= timeoutHint) return UINT32_MAX;
    }
    std::this_thread::yield();
  }
}

extern "C" hsa_status_t hsa_amd_signal_async_handler(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare,
                                                     hsa_amd_signal_handler handler, void *arg) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!handler) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::shared_ptr<Signal> value;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    value = findSignal(runtime, signal);
  }
  if (!value) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  try {
    auto entry = std::make_shared<AsyncHandler>(AsyncHandler{std::move(value), condition, compare, handler, arg});
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.asyncHandlers.push_back(std::move(entry));
    runtime.asyncChanged.notify_all();
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_queue_create(hsa_agent_t agent, uint32_t size, hsa_queue_type32_t type,
                                         void (*callback)(hsa_status_t, hsa_queue_t *, void *), void *data, uint32_t, uint32_t,
                                         hsa_queue_t **result) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validGpu(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (!result || !validPowerOfTwo(size) || (type != HSA_QUEUE_TYPE_MULTI && type != HSA_QUEUE_TYPE_SINGLE))
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  try {
    auto queue = std::make_shared<Queue>();
    queue->ringBytes = static_cast<size_t>(size) * gemstone::hsa::AqlPacketSize;
    if (posix_memalign(&queue->ring, 64, queue->ringBytes) != 0 || !queue->ring) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
    std::memset(queue->ring, 0, queue->ringBytes);
    for (uint32_t index = 0; index < size; ++index)
      *reinterpret_cast<uint16_t *>(static_cast<uint8_t *>(queue->ring) + static_cast<size_t>(index) * gemstone::hsa::AqlPacketSize) =
          HSA_PACKET_TYPE_INVALID;
    {
      std::lock_guard<std::mutex> guard(runtime.lock);
      queue->aql = runtime.monitor->registerQueue(
          queue->ring, size, [&runtime](uint64_t id, uint64_t, const void *packet) { return consume(runtime, id, packet); });
      if (!queue->aql) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
      queue->doorbell = std::make_shared<Signal>();
      queue->doorbell->doorbell = queue->aql->doorbell();
      const uint64_t signalHandle = runtime.nextHandle++;
      runtime.signals.emplace(signalHandle, queue->doorbell);
      queue->value.type = type;
      queue->value.features = HSA_QUEUE_FEATURE_KERNEL_DISPATCH;
      queue->value.base_address = queue->ring;
      queue->value.doorbell_signal = hsa_signal_t{signalHandle};
      queue->value.size = size;
      queue->value.id = queue->aql->id();
      queue->callback = callback;
      queue->callbackData = data;
      runtime.queues.emplace(&queue->value, queue);
      *result = &queue->value;
      if (tracePackets())
        std::fprintf(stderr, "Gemstone HSA queue create queue=%p id=%lu ring=%p read=%p write=%p doorbell=%p signal=%lu\n",
                     static_cast<void *>(&queue->value), queue->aql->id(), queue->ring, static_cast<void *>(queue->aql->readIndex()),
                     static_cast<void *>(queue->aql->writeIndex()), static_cast<void *>(queue->aql->doorbell()), signalHandle);
    }
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_queue_destroy(hsa_queue_t *queue) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!queue) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  QueueRef value;
  {
    std::unique_lock<std::mutex> guard(runtime.lock);
    const auto found = runtime.queues.find(queue);
    if (found == runtime.queues.end()) return HSA_STATUS_ERROR_INVALID_QUEUE;
    value = found->second;
    value->doorbell->doorbell = nullptr;
    runtime.signals.erase(value->value.doorbell_signal.handle);
    runtime.queues.erase(found);
    runtime.queueErrors ^= filter([&](const auto &entry) { return entry.queue != value; });
    // An error callback never outlives its queue unless an error callback destroys it; waiting there could deadlock
    // with the queue's own callback or with one destroying the caller's queue in turn.
    if (!runningQueueError) runtime.asyncChanged.wait(guard, [&] { return !value->errorHandlerRunning; });
  }
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_queue_inactivate(hsa_queue_t *queue) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(queue);
  if (found == runtime.queues.end()) return HSA_STATUS_ERROR_INVALID_QUEUE;
  found->second->aql->inactivate();
  return HSA_STATUS_SUCCESS;
}

extern "C" uint64_t hsa_queue_load_read_index_scacquire(const hsa_queue_t *queue) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  return found == runtime.queues.end() ? 0 : __atomic_load_n(found->second->aql->readIndex(), __ATOMIC_ACQUIRE);
}
extern "C" uint64_t hsa_queue_load_read_index_relaxed(const hsa_queue_t *queue) { return hsa_queue_load_read_index_scacquire(queue); }
extern "C" uint64_t hsa_queue_load_read_index_acquire(const hsa_queue_t *queue) { return hsa_queue_load_read_index_scacquire(queue); }
extern "C" uint64_t hsa_queue_load_write_index_scacquire(const hsa_queue_t *queue) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  return found == runtime.queues.end() ? 0 : __atomic_load_n(found->second->aql->writeIndex(), __ATOMIC_ACQUIRE);
}
extern "C" uint64_t hsa_queue_load_write_index_relaxed(const hsa_queue_t *queue) { return hsa_queue_load_write_index_scacquire(queue); }
extern "C" uint64_t hsa_queue_load_write_index_acquire(const hsa_queue_t *queue) { return hsa_queue_load_write_index_scacquire(queue); }

extern "C" uint64_t hsa_queue_add_write_index_relaxed(const hsa_queue_t *queue, uint64_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  if (found == runtime.queues.end()) {
    if (tracePackets())
      std::fprintf(stderr, "Gemstone HSA queue reserve queue=%p found=0 increment=%lu\n", static_cast<const void *>(queue), value);
    return 0;
  }
  const uint64_t previous = __atomic_fetch_add(found->second->aql->writeIndex(), value, __ATOMIC_RELAXED);
  if (tracePackets())
    std::fprintf(stderr, "Gemstone HSA queue reserve queue=%p found=1 previous=%lu next=%lu write=%p\n", static_cast<const void *>(queue),
                 previous, previous + value, static_cast<void *>(found->second->aql->writeIndex()));
  return previous;
}
extern "C" uint64_t hsa_queue_add_write_index_scacquire(const hsa_queue_t *queue, uint64_t value) {
  return hsa_queue_add_write_index_relaxed(queue, value);
}
extern "C" uint64_t hsa_queue_add_write_index_scacq_screl(const hsa_queue_t *queue, uint64_t value) {
  return hsa_queue_add_write_index_relaxed(queue, value);
}
extern "C" uint64_t hsa_queue_add_write_index_acquire(const hsa_queue_t *queue, uint64_t value) {
  return hsa_queue_add_write_index_relaxed(queue, value);
}
extern "C" uint64_t hsa_queue_add_write_index_acq_rel(const hsa_queue_t *queue, uint64_t value) {
  return hsa_queue_add_write_index_relaxed(queue, value);
}
extern "C" uint64_t hsa_queue_add_write_index_screlease(const hsa_queue_t *queue, uint64_t value) {
  return hsa_queue_add_write_index_relaxed(queue, value);
}
extern "C" uint64_t hsa_queue_add_write_index_release(const hsa_queue_t *queue, uint64_t value) {
  return hsa_queue_add_write_index_relaxed(queue, value);
}

namespace {

uint64_t queueCompareExchange(const hsa_queue_t *queue, uint64_t expected, uint64_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  if (found == runtime.queues.end()) return 0;
  auto observed = expected;
  __atomic_compare_exchange_n(found->second->aql->writeIndex(), &observed, value, false, __ATOMIC_SEQ_CST, __ATOMIC_SEQ_CST);
  return observed;
}

} // namespace

#define GEMSTONE_QUEUE_CAS(name)                                                                                                           \
  extern "C" uint64_t hsa_queue_cas_write_index_##name(const hsa_queue_t *queue, uint64_t expected, uint64_t value) {                      \
    return queueCompareExchange(queue, expected, value);                                                                                   \
  }

GEMSTONE_QUEUE_CAS(relaxed)
GEMSTONE_QUEUE_CAS(acquire)
GEMSTONE_QUEUE_CAS(release)
GEMSTONE_QUEUE_CAS(acq_rel)
GEMSTONE_QUEUE_CAS(scacquire)
GEMSTONE_QUEUE_CAS(screlease)
GEMSTONE_QUEUE_CAS(scacq_screl)

#undef GEMSTONE_QUEUE_CAS

extern "C" void hsa_queue_store_read_index_relaxed(const hsa_queue_t *queue, uint64_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  if (found != runtime.queues.end()) __atomic_store_n(found->second->aql->readIndex(), value, __ATOMIC_RELAXED);
}
extern "C" void hsa_queue_store_read_index_release(const hsa_queue_t *queue, uint64_t value) {
  hsa_queue_store_read_index_relaxed(queue, value);
}
extern "C" void hsa_queue_store_read_index_screlease(const hsa_queue_t *queue, uint64_t value) {
  hsa_queue_store_read_index_relaxed(queue, value);
}
extern "C" void hsa_queue_store_write_index_relaxed(const hsa_queue_t *queue, uint64_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  if (found != runtime.queues.end()) __atomic_store_n(found->second->aql->writeIndex(), value, __ATOMIC_RELAXED);
}
extern "C" void hsa_queue_store_write_index_release(const hsa_queue_t *queue, uint64_t value) {
  hsa_queue_store_write_index_relaxed(queue, value);
}
extern "C" void hsa_queue_store_write_index_screlease(const hsa_queue_t *queue, uint64_t value) {
  hsa_queue_store_write_index_relaxed(queue, value);
}

namespace {

hsa_status_t createCodeObject(const void *object, size_t size, uint64_t *result, bool legacy) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!object || size == 0 || !result) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  try {
    auto reader = std::make_shared<Reader>();
    reader->legacy = legacy;
    reader->bytes.assign(static_cast<const uint8_t *>(object), static_cast<const uint8_t *>(object) + size);
    std::lock_guard<std::mutex> guard(runtime.lock);
    const uint64_t handle = runtime.nextHandle++;
    runtime.readers.emplace(handle, reader);
    *result = handle;
    traceLoader("reader", handle, size);
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

} // namespace

extern "C" hsa_status_t hsa_code_object_reader_create_from_memory(const void *object, size_t size, hsa_code_object_reader_t *result) {
  return createCodeObject(object, size, result ? &result->handle : nullptr, false);
}

extern "C" hsa_status_t hsa_code_object_deserialize(void *object, size_t size, const char *, hsa_code_object_t *result) {
  return createCodeObject(object, size, result ? &result->handle : nullptr, true);
}

extern "C" hsa_status_t hsa_code_object_reader_create_from_file(hsa_file_t file, hsa_code_object_reader_t *result) {
  if (!result || file < 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  struct stat info{};
  if (fstat(file, &info) != 0 || info.st_size <= 0) return HSA_STATUS_ERROR_INVALID_FILE;
  if (static_cast<uint64_t>(info.st_size) > std::numeric_limits<size_t>::max()) return HSA_STATUS_ERROR_INVALID_FILE;
  std::vector<uint8_t> bytes(static_cast<size_t>(info.st_size));
  size_t offset = 0;
  while (offset != bytes.size()) {
    const ssize_t read = pread(file, bytes.data() + offset, bytes.size() - offset, static_cast<off_t>(offset));
    if (read <= 0) return HSA_STATUS_ERROR_INVALID_FILE;
    offset += static_cast<size_t>(read);
  }
  return hsa_code_object_reader_create_from_memory(bytes.data(), bytes.size(), result);
}

namespace {

hsa_status_t destroyCodeObject(uint64_t handle, bool legacy) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.readers.find(handle);
  if (found == runtime.readers.end() || found->second->legacy != legacy)
    return legacy ? HSA_STATUS_ERROR_INVALID_CODE_OBJECT : HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER;
  runtime.readers.erase(found);
  return HSA_STATUS_SUCCESS;
}

} // namespace

extern "C" hsa_status_t hsa_code_object_reader_destroy(hsa_code_object_reader_t handle) { return destroyCodeObject(handle.handle, false); }

extern "C" hsa_status_t hsa_code_object_destroy(hsa_code_object_t handle) { return destroyCodeObject(handle.handle, true); }

extern "C" hsa_status_t hsa_executable_create_alt(hsa_profile_t profile, hsa_default_float_rounding_mode_t, const char *,
                                                  hsa_executable_t *result) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if ((profile != HSA_PROFILE_BASE && profile != HSA_PROFILE_FULL) || !result) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  try {
    auto executable = std::make_shared<Executable>();
    std::lock_guard<std::mutex> guard(runtime.lock);
    executable->handle = runtime.nextHandle++;
    runtime.executables.emplace(executable->handle, executable);
    result->handle = executable->handle;
    traceLoader("create", executable->handle);
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_executable_create(hsa_profile_t profile, hsa_executable_state_t executableState, const char *options,
                                              hsa_executable_t *result) {
  if (executableState != HSA_EXECUTABLE_STATE_UNFROZEN) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  return hsa_executable_create_alt(profile, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, options, result);
}

namespace {

hsa_status_t loadCodeObject(hsa_executable_t executableHandle, hsa_agent_t agent, hsa_code_object_reader_t readerHandle,
                            hsa_loaded_code_object_t *loaded, bool legacy) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validGpu(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  ExecutableRef executableValue;
  std::shared_ptr<Reader> readerValue;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    executableValue = executable(runtime, executableHandle);
    readerValue = reader(runtime, readerHandle);
    if (!executableValue) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
    if (!readerValue || readerValue->legacy != legacy)
      return legacy ? HSA_STATUS_ERROR_INVALID_CODE_OBJECT : HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER;
    if (executableValue->frozen) return HSA_STATUS_ERROR_FROZEN_EXECUTABLE;
  }

  traceLoader("load", executableHandle.handle, readerValue->bytes.size());

  auto loadedProgram = runtime.client->call(LoadProgram{ProgramEncoding::HSACodeObject, readerValue->bytes});
  if (!loadedProgram) {
    if (traceLoaders()) std::fprintf(stderr, "Gemstone HSA module load failed: %s\n", loadedProgram.error().detail.c_str());
    return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
  }
  const uint64_t programId = static_cast<uint64_t>(loadedProgram.value().value);
  struct LoadedProgramGuard {
    Client *client;
    uint64_t program;
    ~LoadedProgramGuard() {
      if (program != 0) (void)client->call(UnloadProgram{ProgramId{static_cast<int64_t>(program)}});
    }
  } programGuard{runtime.client.get(), programId};
  auto described = runtime.client->call(DescribeProgram{ProgramId{static_cast<int64_t>(programId)}});
  if (!described) {
    if (traceLoaders()) std::fprintf(stderr, "Gemstone HSA module description failed: %s\n", described.error().detail.c_str());
    return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
  }
  if (described.value().kernels.empty()) {
    if (traceLoaders()) std::fprintf(stderr, "Gemstone HSA module has no kernels\n");
    return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
  }
  std::vector<gemstone::hsa::ExecutableKernelMetadata> metadata;
  std::vector<CatalogueGlobal> variables;
  try {
    metadata.reserve(described.value().kernels.size());
    for (auto &source : described.value().kernels) {
      gemstone::hsa::ExecutableKernelMetadata kernel;
      kernel.name = std::move(source.name.value);
      kernel.symbol = std::move(source.linkageName.value);
      metadata.push_back(std::move(kernel));
    }
    variables.reserve(described.value().globals.size());
    for (auto &source : described.value().globals) {
      if (!source.virtualAddress || source.size.value > UINT32_MAX) return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
      CatalogueGlobal global;
      global.name = std::move(source.name.value);
      global.address = static_cast<uint64_t>(source.virtualAddress->value);
      global.size = static_cast<uint32_t>(source.size.value);
      global.pointerOffsets.reserve(source.pointerOffsets.size());
      for (const auto offset : source.pointerOffsets)
        global.pointerOffsets.push_back(static_cast<uint32_t>(offset.value));
      variables.push_back(std::move(global));
    }
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    for (const auto &variable : variables)
      for (const auto &global : executableValue->globals)
        if (global->name == variable.name) return HSA_STATUS_ERROR_VARIABLE_ALREADY_DEFINED;
  }
  std::vector<KernelRef> kernels;
  std::vector<gemstone::hsa::KernelSymbol> symbols;
  try {
    kernels.reserve(metadata.size());
    symbols.reserve(metadata.size());
    for (auto &item : metadata) {
      auto kernel = std::make_shared<Kernel>();
      kernel->metadata = std::move(item);
      {
        std::lock_guard<std::mutex> guard(runtime.lock);
        kernel->object = runtime.nextKernel++;
      }
      kernels.push_back(kernel);
      symbols.push_back({kernel->object, kernel->metadata.name});
    }
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  programGuard.program = 0;
  if (!runtime.aql->installLoadedProgram(loadedProgram.value(), symbols)) {
    std::fprintf(stderr, "hsa install failed: %s\n", runtime.aql->lastError().c_str());
    return HSA_STATUS_ERROR_INCOMPATIBLE_ARGUMENTS;
  }
  const auto removeKernels = [&]() {
    for (const auto &kernel : kernels)
      (void)runtime.aql->removeKernel(kernel->object);
  };
  for (const auto &kernel : kernels) {
    auto layout = runtime.aql->layout(kernel->object);
    if (!layout) {
      removeKernels();
      return HSA_STATUS_ERROR_INCOMPATIBLE_ARGUMENTS;
    }
    kernel->metadata.layout = std::move(*layout);
  }
  std::vector<GlobalStorageRef> globalStorage;
  std::vector<GlobalRef> globals;
  const auto rollback = [&]() {
    removeKernels();
    for (const auto &storage : globalStorage) {
      {
        std::lock_guard<std::mutex> guard(runtime.lock);
        runtime.globalStorage.erase(storage->allocation->id);
      }
      (void)freeAllocation(runtime, storage->allocation->base, true);
    }
  };
  try {
    variables ^= sort_by([](const auto &variable) { return variable.address; });
    std::vector<std::pair<uint64_t, uint64_t>> ranges;
    for (const auto &variable : variables) {
      if (variable.size == 0) continue;
      if (variable.address > UINT64_MAX - variable.size) {
        rollback();
        return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
      }
      const uint64_t end = variable.address + variable.size;
      if (ranges.empty() || variable.address > ranges.back().second) ranges.emplace_back(variable.address, end);
      else ranges.back().second = std::max(ranges.back().second, end);
    }
    for (const auto &[begin, end] : ranges) {
      void *address = nullptr;
      if (end - begin > VirtualMemorySize || allocate(runtime, GpuPool, end - begin, &address, true) != HSA_STATUS_SUCCESS) {
        rollback();
        return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
      }
      auto storage = std::make_shared<GlobalStorage>();
      {
        std::lock_guard<std::mutex> guard(runtime.lock);
        storage->allocation = findAllocation(runtime, address);
      }
      storage->elfAddress = begin;
      storage->size = end - begin;
      storage->kernelObject = kernels.front()->object;
      globalStorage.push_back(storage);
      {
        std::lock_guard<std::mutex> guard(runtime.lock);
        runtime.globalStorage.emplace(storage->allocation->id, storage);
      }
    }
    for (const auto &variable : variables) {
      auto global = std::make_shared<Global>();
      {
        std::lock_guard<std::mutex> guard(runtime.lock);
        global->object = runtime.nextKernel++;
      }
      global->name = variable.name;
      global->size = variable.size;
      if (variable.size != 0) {
        const auto found = std::find_if(globalStorage.begin(), globalStorage.end(), [&](const auto &storage) {
          return variable.address >= storage->elfAddress && variable.address - storage->elfAddress < storage->size;
        });
        if (found == globalStorage.end()) {
          rollback();
          return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
        }
        global->storage = *found;
        const auto relative = variable.address - global->storage->elfAddress;
        global->address = static_cast<uint8_t *>(global->storage->allocation->base) + relative;
        for (uint32_t offset : variable.pointerOffsets) {
          auto &fields = global->storage->pointerOffsets;
          if (!(fields ^ contains(relative + offset))) fields.push_back(relative + offset);
        }
        if (!runtime.aql->bindGlobal(kernels.front()->object, variable.name, global->storage->allocation->id, relative)) {
          std::fprintf(stderr, "hsa global binding failed: %s\n", runtime.aql->lastError().c_str());
          rollback();
          return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
        }
      }
      globals.push_back(global);
    }
    // Bind initialises from the server's relocated linked image. All aliases
    // share one allocation, so host and kernel writes cannot diverge.
    for (const auto &storage : globalStorage)
      if (download(runtime, storage->allocation, 0, storage->size) != 0) {
        rollback();
        return HSA_STATUS_ERROR;
      }
  } catch (...) {
    rollback();
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    for (const auto &kernel : kernels) {
      if (runtime.kernels.count(kernel->object) != 0) return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
      runtime.kernels.emplace(kernel->object, kernel);
    }
    executableValue->kernels.insert(executableValue->kernels.end(), kernels.begin(), kernels.end());
    for (const auto &kernel : kernels) {
      executableValue->kernelNames.emplace(kernel->metadata.name, kernel);
      executableValue->kernelNames.emplace(kernel->metadata.symbol, kernel);
      if (kernel->metadata.symbol.size() > 3 && (kernel->metadata.symbol ^ ends_with(".kd")))
        executableValue->kernelNames.emplace(kernel->metadata.symbol ^ strip_suffix(".kd"), kernel);
    }
    executableValue->globals.insert(executableValue->globals.end(), globals.begin(), globals.end());
    executableValue->globalStorage.insert(executableValue->globalStorage.end(), globalStorage.begin(), globalStorage.end());
    for (const auto &global : globals)
      runtime.globals.emplace(global->object, global);
  }
  if (loaded) loaded->handle = executableHandle.handle;
  if (traceLoaders())
    std::fprintf(stderr, "[gemstone-hsa] loader loaded executable=%llu program=%llu kernels=%zu globals=%zu\n",
                 static_cast<unsigned long long>(executableHandle.handle), static_cast<unsigned long long>(programId), kernels.size(),
                 globals.size());
  return HSA_STATUS_SUCCESS;
}

} // namespace

extern "C" hsa_status_t hsa_executable_load_agent_code_object(hsa_executable_t executable, hsa_agent_t agent,
                                                              hsa_code_object_reader_t reader, const char *,
                                                              hsa_loaded_code_object_t *loaded) {
  return loadCodeObject(executable, agent, reader, loaded, false);
}

extern "C" hsa_status_t hsa_executable_load_code_object(hsa_executable_t executable, hsa_agent_t agent, hsa_code_object_t object,
                                                        const char *) {
  return loadCodeObject(executable, agent, hsa_code_object_reader_t{object.handle}, nullptr, true);
}

extern "C" hsa_status_t hsa_executable_load_program_code_object(hsa_executable_t executable, hsa_code_object_reader_t reader,
                                                                const char *options, hsa_loaded_code_object_t *loaded) {
  return hsa_executable_load_agent_code_object(executable, hsa_agent_t{GpuAgent}, reader, options, loaded);
}

extern "C" hsa_status_t hsa_executable_freeze(hsa_executable_t executableHandle, const char *) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = executable(runtime, executableHandle);
  if (!value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
  if (value->frozen) return HSA_STATUS_ERROR_FROZEN_EXECUTABLE;
  value->frozen = true;
  traceLoader("freeze", executableHandle.handle, value->kernels.size());
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_executable_validate(hsa_executable_t executableHandle, uint32_t *result) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!result) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = executable(runtime, executableHandle);
  if (!value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
  if (!value->frozen) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
  *result = 0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_executable_destroy(hsa_executable_t executableHandle) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  ExecutableRef value;
  std::vector<GlobalRef> globals;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.executables.find(executableHandle.handle);
    if (found == runtime.executables.end()) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
    value = found->second;
    runtime.executables.erase(found);
    for (const auto &kernel : value->kernels)
      runtime.kernels.erase(kernel->object);
    globals = value->globals;
    for (const auto &global : globals)
      runtime.globals.erase(global->object);
  }
  for (const auto &kernel : value->kernels)
    if (!runtime.aql->removeKernel(kernel->object)) return HSA_STATUS_ERROR;
  for (const auto &storage : value->globalStorage) {
    {
      std::lock_guard<std::mutex> guard(runtime.lock);
      runtime.globalStorage.erase(storage->allocation->id);
    }
    if (freeAllocation(runtime, storage->allocation->base, true) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  }
  return HSA_STATUS_SUCCESS;
}

#if defined(GEMSTONE_HSA_RUNTIME_LIBRARY)
  #define GEMSTONE_HSA_EXTENSION_API GEMSTONE_PUBLIC_API
#else
  #define GEMSTONE_HSA_EXTENSION_API
#endif

extern "C" GEMSTONE_HSA_EXTENSION_API hsa_status_t gemstone_hsa_executable_write_global(hsa_executable_t executableHandle, const char *name,
                                                                                        const void *bytes, size_t size, size_t offset) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!name || name[0] == '\0' || (size != 0 && !bytes) || size > UINT32_MAX) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  GlobalRef global;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto value = executable(runtime, executableHandle);
    if (!value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
    if (!value->frozen) return HSA_STATUS_ERROR_INVALID_EXECUTABLE_SYMBOL;
    for (const auto &candidate : value->globals)
      if (candidate->name == name) {
        global = candidate;
        break;
      }
  }
  if (!global) return HSA_STATUS_ERROR_INVALID_SYMBOL_NAME;
  if (offset > global->size || size > global->size - offset) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (size == 0) return HSA_STATUS_SUCCESS;
  // This extension is explicitly a host-to-device copy. Registered host
  // buffers may have been modified since their last upload; downloading the
  // source here would erase the caller's newly written bytes.
  std::memmove(static_cast<uint8_t *>(global->address) + offset, bytes, size);
  const auto relative = static_cast<uint8_t *>(global->address) - static_cast<uint8_t *>(global->storage->allocation->base) + offset;
  return upload(runtime, global->storage->allocation, relative, size) == 0 ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR;
}

extern "C" hsa_status_t hsa_executable_get_symbol_by_name(hsa_executable_t executableHandle, const char *name, const hsa_agent_t *agent,
                                                          hsa_executable_symbol_t *symbol) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!name || !symbol) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (agent && !validGpu(*agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  const std::string requested(name);
  ExecutableRef value;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    value = executable(runtime, executableHandle);
    if (!value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
    if (!value->frozen) return HSA_STATUS_ERROR_INVALID_EXECUTABLE_SYMBOL;
    const auto kernel = value->kernelNames.find(requested);
    if (kernel != value->kernelNames.end()) {
      symbol->handle = kernel->second->object;
      return HSA_STATUS_SUCCESS;
    }
    for (const auto &global : value->globals) {
      if (global->name == requested) {
        symbol->handle = global->object;
        return HSA_STATUS_SUCCESS;
      }
    }
  }

  traceLoaderSymbol("missing", executableHandle.handle, name);
  return HSA_STATUS_ERROR_INVALID_SYMBOL_NAME;
}

extern "C" hsa_status_t hsa_executable_get_symbol(hsa_executable_t executable, const char *module, const char *name, hsa_agent_t agent,
                                                  int32_t, hsa_executable_symbol_t *symbol) {
  if (module) return static_cast<hsa_status_t>(HSA_STATUS_ERROR_NOT_SUPPORTED);
  return hsa_executable_get_symbol_by_name(executable, name, agent.handle == 0 ? nullptr : &agent, symbol);
}

extern "C" hsa_status_t hsa_executable_symbol_get_info(hsa_executable_symbol_t symbolHandle, hsa_executable_symbol_info_t attribute,
                                                       void *value) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!value) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto kernel = runtime.kernels.find(symbolHandle.handle);
  if (kernel != runtime.kernels.end()) {
    switch (attribute) {
      case HSA_EXECUTABLE_SYMBOL_INFO_TYPE: return copyStatus(value, HSA_SYMBOL_KIND_KERNEL);
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT: return copyStatus(value, kernel->second->object);
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE: return copyStatus(value, kernel->second->metadata.layout.kernargSize);
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_ALIGNMENT: return copyStatus(value, uint32_t(16));
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_GROUP_SEGMENT_SIZE:
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_PRIVATE_SEGMENT_SIZE: return copyStatus(value, uint32_t(0));
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_DYNAMIC_CALLSTACK: return copyStatus(value, false);
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_CALL_CONVENTION: return copyStatus(value, uint32_t(0));
      default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
    }
  }
  const auto global = runtime.globals.find(symbolHandle.handle);
  if (global == runtime.globals.end()) return HSA_STATUS_ERROR_INVALID_EXECUTABLE_SYMBOL;
  switch (attribute) {
    case HSA_EXECUTABLE_SYMBOL_INFO_TYPE: return copyStatus(value, HSA_SYMBOL_KIND_VARIABLE);
    case HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_ADDRESS: return copyStatus(value, reinterpret_cast<uint64_t>(global->second->address));
    case HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_SIZE: return copyStatus(value, global->second->size);
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

bool gemstone::hsa::copyExecutableKernelMetadata(hsa_executable_t executableHandle, std::string_view name,
                                                 ExecutableKernelMetadata &metadata) {
  auto &runtime = state();
  if (requireInitialised(runtime) != HSA_STATUS_SUCCESS || name.empty()) return false;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto executableValue = executable(runtime, executableHandle);
  if (!executableValue) return false;
  const auto found = executableValue->kernelNames.find(std::string(name));
  if (found == executableValue->kernelNames.end()) return false;
  metadata = found->second->metadata;
  return true;
}

bool gemstone::hsa::copyExecutableKernelMetadata(hsa_executable_t executableHandle, std::vector<ExecutableKernelMetadata> &metadata) {
  auto &runtime = state();
  if (requireInitialised(runtime) != HSA_STATUS_SUCCESS) return false;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto executableValue = executable(runtime, executableHandle);
  if (!executableValue) return false;
  metadata.clear();
  metadata.reserve(executableValue->kernels.size());
  for (const auto &kernel : executableValue->kernels)
    metadata.push_back(kernel->metadata);
  return true;
}

extern "C" hsa_status_t hsa_amd_agent_iterate_memory_pools(hsa_agent_t agent, hsa_status_t (*callback)(hsa_amd_memory_pool_t, void *),
                                                           void *data) {
  traceApiCall("hsa_amd_agent_iterate_memory_pools", agent.handle);
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validAgent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (!callback) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  auto status = callback(hsa_amd_memory_pool_t{agent.handle == CpuAgent ? CpuPool : GpuPool}, data);
  if (status == HSA_STATUS_SUCCESS && validGpu(agent)) status = callback(hsa_amd_memory_pool_t{GroupPool}, data);
  traceApiResult("hsa_amd_agent_iterate_memory_pools", status);
  return status;
}

extern "C" hsa_status_t hsa_amd_memory_pool_get_info(hsa_amd_memory_pool_t pool, hsa_amd_memory_pool_info_t attribute, void *value) {
  traceApiCall("hsa_amd_memory_pool_get_info", pool.handle, attribute);
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validPool(pool)) return static_cast<hsa_status_t>(HSA_STATUS_ERROR_INVALID_MEMORY_POOL);
  if (!value) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  // LDS is allocated per workgroup by the executor, never by the host allocator.
  if (pool.handle == GroupPool) {
    switch (attribute) {
      case HSA_AMD_MEMORY_POOL_INFO_SEGMENT: return copyStatus(value, HSA_AMD_SEGMENT_GROUP);
      // ROCr returns a zero mask for non-global pools. LLVM queries this
      // property before deciding whether to retain a pool for allocation.
      case HSA_AMD_MEMORY_POOL_INFO_GLOBAL_FLAGS: return copyStatus(value, uint32_t(0));
      case HSA_AMD_MEMORY_POOL_INFO_SIZE: return copyStatus(value, GroupMemorySize);
      case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_ALLOWED:
      case HSA_AMD_MEMORY_POOL_INFO_ACCESSIBLE_BY_ALL: return copyStatus(value, false);
      default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
    }
  }
  const uint32_t flags =
      HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_KERNARG_INIT
      | (pool.handle == CpuPool ? HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_FINE_GRAINED : HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_COARSE_GRAINED);
  switch (attribute) {
    case HSA_AMD_MEMORY_POOL_INFO_SEGMENT: return copyStatus(value, HSA_AMD_SEGMENT_GLOBAL);
    case HSA_AMD_MEMORY_POOL_INFO_GLOBAL_FLAGS: return copyStatus(value, flags);
    case HSA_AMD_MEMORY_POOL_INFO_SIZE: return copyStatus(value, size_t(VirtualMemorySize));
    case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_ALLOWED: return copyStatus(value, true);
    case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_GRANULE:
    case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_ALIGNMENT:
    case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_REC_GRANULE: return copyStatus(value, size_t(AllocationGranule));
    case HSA_AMD_MEMORY_POOL_INFO_ACCESSIBLE_BY_ALL: return copyStatus(value, pool.handle == CpuPool);
    case HSA_AMD_MEMORY_POOL_INFO_ALLOC_MAX_SIZE: return copyStatus(value, size_t(VirtualMemorySize));
    case HSA_AMD_MEMORY_POOL_INFO_LOCATION:
      return copyStatus(value, pool.handle == CpuPool ? HSA_AMD_MEMORY_POOL_LOCATION_CPU : HSA_AMD_MEMORY_POOL_LOCATION_GPU);
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_amd_memory_pool_allocate(hsa_amd_memory_pool_t pool, size_t size, uint32_t flags, void **ptr) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validPool(pool)) return static_cast<hsa_status_t>(HSA_STATUS_ERROR_INVALID_MEMORY_POOL);
  // Executable means device code; the interpreter never executes host pages.
  if ((flags & ~uint32_t(HSA_AMD_MEMORY_POOL_EXECUTABLE_FLAG)) != 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (pool.handle == GroupPool) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
  return allocate(runtime, pool.handle, size, ptr, true);
}

extern "C" hsa_status_t hsa_amd_memory_pool_free(void *ptr) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  return freeAllocation(runtime, ptr, true);
}

extern "C" hsa_status_t hsa_amd_agents_allow_access(uint32_t count, const hsa_agent_t *agents, const uint32_t *flags, const void *ptr) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (count == 0 || !agents || flags || !ptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  for (uint32_t index = 0; index < count; ++index)
    if (!validAgent(agents[index])) return HSA_STATUS_ERROR_INVALID_AGENT;
  return findAllocation(runtime, ptr) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ALLOCATION;
}

extern "C" hsa_status_t hsa_amd_memory_fill(void *ptr, uint32_t value, size_t count) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!ptr || (reinterpret_cast<uintptr_t>(ptr) & 3U) != 0 || count > std::numeric_limits<size_t>::max() / 4U)
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const size_t bytes = count * 4U;
  const gemstone::MemoryRange range{reinterpret_cast<uintptr_t>(ptr), bytes};
  if (bytes != 0 && !range.valid()) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::vector<AllocationRef> fragments;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (!findAllocation(runtime, ptr, bytes) && !runtime.registrations.find(range.base, bytes)) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    for (const auto &[base, allocation] : runtime.allocations)
      if (gemstone::MemoryRange{reinterpret_cast<uintptr_t>(base), allocation->size}.overlaps(range)) fragments.push_back(allocation);
  }
  std::fill_n(static_cast<uint32_t *>(ptr), count, value);
  for (const auto &allocation : fragments) {
    const auto base = reinterpret_cast<uintptr_t>(allocation->base);
    const auto begin = std::max(base, range.base), end = std::min(base + allocation->size, range.end());
    if (upload(runtime, allocation, begin - base, end - begin) != 0) return HSA_STATUS_ERROR;
  }
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_memory_async_copy(void *dst, hsa_agent_t dstAgent, const void *src, hsa_agent_t srcAgent, size_t size,
                                                  uint32_t dependencyCount, const hsa_signal_t *dependencies, hsa_signal_t completion) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validAgent(dstAgent) || !validAgent(srcAgent) || completion.handle == 0 || (dependencyCount != 0 && !dependencies)
      || (size != 0 && (!dst || !src)))
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  for (uint32_t index = 0; index < dependencyCount; ++index)
    waitSignal(dependencies[index], HSA_SIGNAL_CONDITION_LT, 1, UINT64_MAX);
  AllocationRef destination;
  AllocationRef sourceAllocation;
  bool sourceRegistered = false;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    sourceAllocation = findAllocation(runtime, src, size);
    destination = findAllocation(runtime, dst, size);
    sourceRegistered = sourceAllocation || runtime.registrations.find(reinterpret_cast<uintptr_t>(src), size);
    if (size != 0 && !sourceRegistered && !destination && !runtime.registrations.find(reinterpret_cast<uintptr_t>(dst), size))
      return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    if (!findSignal(runtime, completion)) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  }
  if (traceMemory())
    std::fprintf(stderr, "Gemstone HSA async copy dst=%p src=%p size=%zu dst_alloc=%lu src_alloc=%lu dst_agent=%lu src_agent=%lu\n", dst,
                 src, size, destination ? destination->id : 0, sourceAllocation ? sourceAllocation->id : 0, dstAgent.handle,
                 srcAgent.handle);
  // A device-to-host copy may be submitted while an earlier dispatch is still
  // executing. The queue's read index advances only after its writes have
  // been mirrored into native storage.
  if (size != 0 && validGpu(srcAgent) && sourceRegistered && !awaitQueuedDispatches(runtime)) return HSA_STATUS_ERROR;
  const auto copied = hsa_memory_copy(dst, src, size);
  if (copied != HSA_STATUS_SUCCESS) return copied;
  return completeSignal(completion) == 0 ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_SIGNAL;
}

extern "C" hsa_status_t hsa_amd_memory_copy_engine_status(hsa_agent_t dstAgent, hsa_agent_t srcAgent, uint32_t *engineIdsMask) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validAgent(dstAgent) || !validAgent(srcAgent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (!engineIdsMask) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  // One software copy engine, implemented by memory_async_copy_on_engine.
  *engineIdsMask = HSA_AMD_SDMA_ENGINE_0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_memory_get_preferred_copy_engine(hsa_agent_t dstAgent, hsa_agent_t srcAgent, uint32_t *recommendedIdsMask) {
  return hsa_amd_memory_copy_engine_status(dstAgent, srcAgent, recommendedIdsMask);
}

extern "C" hsa_status_t hsa_amd_memory_async_copy_on_engine(void *dst, hsa_agent_t dstAgent, const void *src, hsa_agent_t srcAgent,
                                                            size_t size, uint32_t dependencyCount, const hsa_signal_t *dependencies,
                                                            hsa_signal_t completion, hsa_amd_sdma_engine_id_t, bool) {
  return hsa_amd_memory_async_copy(dst, dstAgent, src, srcAgent, size, dependencyCount, dependencies, completion);
}

extern "C" hsa_status_t hsa_amd_agent_memory_pool_get_info(hsa_agent_t agent, hsa_amd_memory_pool_t pool,
                                                           hsa_amd_agent_memory_pool_info_t attribute, void *value) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validAgent(agent) || !validPool(pool)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (!value) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  switch (attribute) {
    case HSA_AMD_AGENT_MEMORY_POOL_INFO_ACCESS:
      return copyStatus(value, pool.handle == GroupPool && !validGpu(agent) ? HSA_AMD_MEMORY_POOL_ACCESS_NEVER_ALLOWED
                                                                            : HSA_AMD_MEMORY_POOL_ACCESS_ALLOWED_BY_DEFAULT);
    case HSA_AMD_AGENT_MEMORY_POOL_INFO_NUM_LINK_HOPS: return copyStatus(value, uint32_t(0));
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_amd_memory_lock(void *hostPtr, size_t size, hsa_agent_t *agents, int numAgent, void **agentPtr) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!hostPtr || size == 0 || !agentPtr || numAgent < 0 || (numAgent == 0 && agents) || (numAgent != 0 && !agents))
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  for (int index = 0; index < numAgent; ++index)
    if (!validAgent(agents[index])) return HSA_STATUS_ERROR_INVALID_AGENT;
  const auto status = registerHostView(runtime, hostPtr, size, true);
  if (status != HSA_STATUS_SUCCESS) return status;
  *agentPtr = hostPtr;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_memory_lock_to_pool(void *hostPtr, size_t size, hsa_agent_t *agents, int numAgent,
                                                    hsa_amd_memory_pool_t pool, uint32_t flags, void **agentPtr) {
  if (!validPool(pool) || pool.handle != CpuPool) return static_cast<hsa_status_t>(HSA_STATUS_ERROR_INVALID_MEMORY_POOL);
  if (flags != 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  return hsa_amd_memory_lock(hostPtr, size, agents, numAgent, agentPtr);
}

extern "C" hsa_status_t hsa_amd_memory_unlock(void *hostPtr) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  return releaseHostView(runtime, hostPtr, 0);
}

extern "C" hsa_status_t hsa_amd_pointer_info(const void *ptr, hsa_amd_pointer_info_t *info, void *(*alloc)(size_t),
                                             uint32_t *numAgentsAccessible, hsa_agent_t **accessible) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!ptr || !info) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const uint32_t requestedSize = info->size;
  if (requestedSize == 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  AllocationRef allocation;
  std::optional<gemstone::MemoryRegistrations::View> view;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    allocation = findAllocation(runtime, ptr);
    if (const auto found = runtime.registrations.find(reinterpret_cast<uintptr_t>(ptr))) view = *found;
    if (allocation && !allocation->owned && !view) allocation.reset();
  }
  const uint32_t supportedSize = static_cast<uint32_t>(sizeof(hsa_amd_pointer_info_t));
  std::memset(info, 0, std::min<uint32_t>(requestedSize, supportedSize));
  info->size = std::min<uint32_t>(requestedSize, supportedSize);
  if (!allocation) {
    info->type = HSA_EXT_POINTER_TYPE_UNKNOWN;
    return HSA_STATUS_SUCCESS;
  }
  info->type = allocation->owned ? HSA_EXT_POINTER_TYPE_HSA : HSA_EXT_POINTER_TYPE_LOCKED;
  info->agentBaseAddress = view ? reinterpret_cast<void *>(view->range.base) : allocation->base;
  info->hostBaseAddress = info->agentBaseAddress;
  info->sizeInBytes = view ? view->range.size : allocation->size;
  info->agentOwner = hsa_agent_t{allocation->pool == GpuPool ? GpuAgent : CpuAgent};
  info->global_flags = HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_FINE_GRAINED;
  info->registered = true;
  if (numAgentsAccessible) *numAgentsAccessible = 2;
  if (accessible) {
    if (!alloc) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
    auto *agents = static_cast<hsa_agent_t *>(alloc(2 * sizeof(hsa_agent_t)));
    if (!agents) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
    agents[0] = hsa_agent_t{CpuAgent};
    agents[1] = hsa_agent_t{GpuAgent};
    *accessible = agents;
  }
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_profiling_set_profiler_enabled(hsa_queue_t *queue, int) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!queue) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  return runtime.queues.count(queue) == 1 ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_QUEUE;
}

extern "C" hsa_status_t hsa_amd_profiling_async_copy_enable(bool) {
  auto &runtime = state();
  return requireInitialised(runtime);
}

extern "C" hsa_status_t hsa_amd_profiling_get_dispatch_time(hsa_agent_t agent, hsa_signal_t signal,
                                                            hsa_amd_profiling_dispatch_time_t *time) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!validAgent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (!time) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (!findSignal(runtime, signal)) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  time->start = 0;
  time->end = 0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_profiling_get_async_copy_time(hsa_signal_t signal, hsa_amd_profiling_async_copy_time_t *time) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!time) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (!findSignal(runtime, signal)) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  time->start = 0;
  time->end = 0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_svm_attributes_set(void *ptr, size_t size, hsa_amd_svm_attribute_pair_t *, size_t attributeCount) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!ptr || size == 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  (void)attributeCount;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_register_system_event_handler(hsa_amd_system_event_callback_t callback, void *) {
  auto &runtime = state();
  if (const auto status = requireInitialised(runtime); status != HSA_STATUS_SUCCESS) return status;
  return !callback ? HSA_STATUS_ERROR_INVALID_ARGUMENT : HSA_STATUS_SUCCESS;
}
