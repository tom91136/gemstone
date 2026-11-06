#include <algorithm>
#include <array>
#include <atomic>
#include <cctype>
#include <charconv>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
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
#include <system_error>
#include <type_traits>
#include <unordered_set>
#include <utility>
#include <variant>
#include <vector>

#include <unistd.h>

#include <aspartame/map.hpp>
#include <aspartame/vector.hpp>
#include <ze_ddi.h>
#include <zes_ddi.h>
#include <zet_ddi.h>

#include "gemstone/command_queue.hpp"
#include "gemstone_execution_rpc.hpp"
#include "gemstone_image_descriptor.hpp"
#include <gemstone/copy_out.hpp>
#include <gemstone/devices.hpp>
#include <gemstone/environment.hpp>
#include <gemstone/memory.hpp>
#include <gemstone/program_binary.hpp>
#include <gemstone/trace.hpp>

#include "client.hpp"
#include "completion.hpp"
#include "objects.hpp"
#include "relocations.hpp"
#include "spirv_resources.hpp"
#include "transfer.hpp"
#include <sys/mman.h>
#include <sys/syscall.h>
#include <sys/types.h>

using namespace aspartame;

namespace {

using namespace gemstone::execution;
using namespace gemstone::levelzero;
namespace descriptor = gemstone::spirv::ImageDescriptor;

// A failed allocation or throwing handle constructor must not unwind through the C DDI.
template <class T, class... Args> T *createHandle(Args &&...args) noexcept {
  try {
    return new T(std::forward<Args>(args)...);
  } catch (...) {
    return nullptr;
  }
}

// Build-log handles are created on error paths; a failed allocation must not
// unwind through the C DDI, so leave the optional out-pointer null instead.
void setBuildLog(ze_module_build_log_handle_t *log, const std::string &message) {
  if (!log) return;
  if (auto *created = createHandle<BuildLog>(message)) *log = reinterpret_cast<ze_module_build_log_handle_t>(created);
}

Driver globalDriver;
std::array<Device, gemstone::MaximumDeviceCount> globalDevices;

std::mutex allocationsMutex;
std::map<void *, AllocationRef, std::less<>> allocations;
std::mutex rpcTransactionMutex;

struct PhysicalMemory final : Handle {
  static constexpr Kind HandleKind = Kind::PhysicalMemory;
  PhysicalMemory(Context *owner, int file, size_t bytes, ze_physical_mem_flags_t flagsValue)
      : Handle(HandleKind), context(owner), fd(file), size(bytes), flags(flagsValue) {}
  ~PhysicalMemory() override { close(fd); }
  HandleUse<Context> context;
  int fd;
  size_t size;
  ze_physical_mem_flags_t flags;
};

struct VirtualMapping {
  size_t size;
  ze_memory_access_attribute_t access;
  HandleUse<PhysicalMemory> physical;
};

struct VirtualReservation {
  HandleUse<Context> context;
  size_t size;
  std::map<uintptr_t, VirtualMapping> mappings;
};

std::mutex virtualMemoryMutex;
std::map<uintptr_t, VirtualReservation> virtualReservations;

// The caller holds virtualMemoryMutex.
auto reservationContaining(uintptr_t address) {
  const auto next = virtualReservations.upper_bound(address);
  return next == virtualReservations.begin() ? virtualReservations.end() : std::prev(next);
}

ClientManager remote;

struct BlockedWait {
  Event *event;
  uint64_t target;
  EngineTurn *turn;
};

std::mutex blockedEventsMutex;
std::vector<BlockedWait *> blockedEvents;
std::atomic<bool> schedulerStopping{false};
bool deviceLost(const Device *device) { return device->lost.load(std::memory_order_acquire); }

ze_result_t reportStatus(Device *device, ze_result_t result) {
  if (device && result == ZE_RESULT_ERROR_DEVICE_LOST) device->lost.store(true, std::memory_order_release);
  return result;
}

// The loader loads every installed driver, so workers start with the first submission. Teardown of layered runtimes
// may call the driver after the workers stop, so the queue is never destroyed.
gemstone::CommandQueue &scheduler() {
  static auto *const queue = new gemstone::CommandQueue(ZE_RESULT_ERROR_UNKNOWN);
  return *queue;
}

// Constructed with the library, so it stops the workers after teardown registered later and before the state above
// is destroyed. Jobs must not keep waiting for events the host never signals.
struct SchedulerShutdown {
  SchedulerShutdown() = default;
  SchedulerShutdown(const SchedulerShutdown &) = delete;
  SchedulerShutdown &operator=(const SchedulerShutdown &) = delete;
  ~SchedulerShutdown() {
    std::vector<HandleUse<Event>> blocked;
    {
      std::scoped_lock const lock(blockedEventsMutex);
      schedulerStopping.store(true, std::memory_order_release);
      for (auto *wait : blockedEvents)
        blocked.emplace_back(wait->event);
    }
    PendingJob::stop();
    for (const auto &event : blocked) {
      std::scoped_lock const lock(event->mutex);
      event->changed.notify_all();
    }
    scheduler().shutdown();
  }
} schedulerShutdown;

struct LaunchPerformance {
  uint64_t relocationScanBytes = 0;
  uint64_t relocationCount = 0;
};

// Launches run on their own connections, so the shared client's error can be stale.
std::mutex lastRemoteErrorMutex;
std::string lastRemoteError;

std::string recordRemoteError(std::string message) {
  if (gemstone::traceEnabled("levelzero")) std::fprintf(stderr, "Gemstone Level Zero: %s\n", message.c_str());
  std::scoped_lock const lock(lastRemoteErrorMutex);
  lastRemoteError = message;
  return message;
}

constexpr size_t PageSize = 4096U;
constexpr size_t DeviceAllocationGranularity = size_t{64} * 1024U;
constexpr size_t MirrorChunkSize = size_t{1} * 1024 * 1024;
constexpr uint32_t MaxWorkItems = 1024;
constexpr uint32_t MaxWorkItemsZ = 64;
constexpr uint64_t DeviceMemorySize = 1ULL << 40;
constexpr size_t MaxServerAllocationSize = static_cast<size_t>(std::numeric_limits<int32_t>::max());
constexpr uint32_t MaxImageDims = 16384;
constexpr uint32_t MaxImageDims3D = 2048;
constexpr uint32_t MaxImageArraySlices = 2048;
constexpr uint32_t MaxSamplers = 16;
constexpr uint32_t MaxImageArgs = 128;
constexpr size_t ImageRecordBytes = descriptor::Layout::LevelTable + descriptor::Layout::LevelBytes + descriptor::Layout::PitchBytes;
constexpr size_t SamplerRecordBytes = static_cast<size_t>(descriptor::SamplerWord::Count) * 4;

AllocationRef findAllocationById(uint64_t id) {
  std::scoped_lock const lock(allocationsMutex);
  for (const auto &[_, allocation] : allocations)
    if (allocation->id == id) return allocation;
  return {};
}

bool classifyPointerValueLocked(uintptr_t pointer, uint64_t &allocationId, uint64_t &offset) {
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (allocation->id && pointer >= start && pointer < start + allocation->size) {
      allocationId = *allocation->id;
      offset = pointer - start;
      return true;
    }
  }
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (allocation->id && pointer >= start && pointer - start == allocation->size) {
      allocationId = *allocation->id;
      offset = allocation->size;
      return true;
    }
  }
  return false;
}

std::vector<PointerRelocation> collectPointerRelocations(const KernelLaunch &kernel, LaunchPerformance &performance) {
  std::vector<PointerRelocation> result;
  if (kernel.indirect) {
    std::scoped_lock const lock(allocationsMutex);
    for (const auto &[base, source] : allocations) {
      if (source->type != ZE_MEMORY_TYPE_DEVICE || !source->id || source->size < sizeof(uintptr_t)) continue;
      performance.relocationScanBytes += source->size;
      scanPointers(base, source->size, [&](size_t offset, uint64_t pointer) {
        uint64_t targetId = 0;
        uint64_t targetOffset = 0;
        if (classifyPointerValueLocked(pointer, targetId, targetOffset))
          result.push_back({.source = RelocationSourceMemory{AllocationId{static_cast<int64_t>(*source->id)}},
                            .sourceOffset = LongPositive0{static_cast<int64_t>(offset)},
                            .target = AllocationId{static_cast<int64_t>(targetId)},
                            .targetOffset = LongPositive0{static_cast<int64_t>(targetOffset)}});
      });
    }
  }
  {
    std::scoped_lock const lock(allocationsMutex);
    for (size_t argument = 0; argument < kernel.arguments.size(); ++argument) {
      const auto &input = kernel.arguments[argument];
      if (input.local || input.bytes.size() < sizeof(uintptr_t)) continue;
      if (argument < kernel.parameters.size() && !kernel.parameters[argument].containsPointer) continue;
      performance.relocationScanBytes += input.bytes.size();
      scanPointers(input.bytes.data(), input.bytes.size(), [&](size_t offset, uint64_t pointer) {
        uint64_t targetId = 0;
        uint64_t targetOffset = 0;
        if (classifyPointerValueLocked(pointer, targetId, targetOffset))
          result.push_back({.source = RelocationSourceArgument{IntPositive0{static_cast<int32_t>(argument)}},
                            .sourceOffset = LongPositive0{static_cast<int64_t>(offset)},
                            .target = AllocationId{static_cast<int64_t>(targetId)},
                            .targetOffset = LongPositive0{static_cast<int64_t>(targetOffset)}});
      });
    }
  }
  performance.relocationCount = result.size();
  return result;
}

AllocationRef retainAllocation(const void *pointer, size_t size) {
  if (!pointer) return {};
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::scoped_lock const lock(allocationsMutex);
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (address >= start && address - start <= allocation->size && size <= allocation->size - (address - start)) return allocation;
  }
  return {};
}

std::vector<AllocationRef> retainKernelAllocations(const Kernel &kernel) {
  std::scoped_lock const lock(allocationsMutex);
  std::vector<AllocationRef> retained;
  if (kernel.indirect != 0) {
    retained.reserve(allocations.size());
    for (const auto &[_, allocation] : allocations)
      retained.push_back(allocation);
    return retained;
  }
  std::unordered_set<uint64_t> seen;
  for (size_t index = 0; index < kernel.arguments.size(); ++index) {
    const auto &argument = kernel.arguments[index];
    if (argument.local || argument.bytes.size() < sizeof(uintptr_t)
        || (index < kernel.parameters.size() && !kernel.parameters[index].containsPointer))
      continue;
    scanPointers(argument.bytes.data(), argument.bytes.size(), [&](size_t, uint64_t pointer) {
      uint64_t allocationId = 0;
      uint64_t allocationOffset = 0;
      if (!classifyPointerValueLocked(pointer, allocationId, allocationOffset) || !seen.insert(allocationId).second) return;
      if (const auto found = allocations ^ find([&](void *, const AllocationRef &entry) { return entry->id == allocationId; }))
        retained.push_back(found->second);
    });
  }
  return retained;
}

template <class T, class H> T *cast(H handle) {
  auto *value = reinterpret_cast<T *>(handle);
  return liveHandles.contains(value, T::HandleKind) && !value->destroyRequested.load(std::memory_order_acquire) ? value : nullptr;
}

uint32_t deviceOrdinal(const Device *device) { return device ? static_cast<uint32_t>(device - globalDevices.data()) : 0; }

ze_result_t checkVersion(ze_api_version_t version, const void *table) {
  if (!table) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (ZE_MAJOR_VERSION(version) != ZE_MAJOR_VERSION(ZE_API_VERSION_CURRENT)) return ZE_RESULT_ERROR_UNSUPPORTED_VERSION;
  return ZE_RESULT_SUCCESS;
}

ze_result_t waitEvent(Event *event, uint64_t timeout, std::optional<uint64_t> awaited = std::nullopt) {
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  uint64_t target = 0;
  {
    std::scoped_lock const lock(event->mutex);
    target = awaited.value_or(event->appended);
    if (eventReached(event, target)) return event->completionResult;
    if (timeout == 0) return ZE_RESULT_NOT_READY;
  }
  const EngineTurn::Yield yield;
  std::unique_lock lock(event->mutex);
  // Commands wait here for events that the host or a later submission may signal.
  return scheduler().blocking([&] {
    BlockedWait wait{.event = event, .target = target, .turn = yield.waiter()};
    {
      std::scoped_lock const guard(blockedEventsMutex);
      if (schedulerStopping.load(std::memory_order_acquire)) return ZE_RESULT_ERROR_DEVICE_LOST;
      blockedEvents.push_back(&wait);
    }
    struct Registration {
      BlockedWait *wait;
      explicit Registration(BlockedWait *wait) : wait(wait) {}
      Registration(const Registration &) = delete;
      Registration &operator=(const Registration &) = delete;
      ~Registration() {
        std::scoped_lock const guard(blockedEventsMutex);
        std::erase(blockedEvents, wait);
      }
    } const registration{&wait};
    const auto released = [&] { return eventReached(event, target) || schedulerStopping.load(std::memory_order_acquire); };
    if (timeout == std::numeric_limits<uint64_t>::max()) event->changed.wait(lock, released);
    else if (!gemstone::waitWithin(event->changed, lock, timeout, released)) return ZE_RESULT_NOT_READY;
    return eventReached(event, target) ? event->completionResult : ZE_RESULT_ERROR_DEVICE_LOST;
  });
}

// The caller holds event->mutex while publishing completion state.
void completeEventLocked(Event *event, ze_result_t result) {
  const auto now = std::chrono::steady_clock::now().time_since_epoch();
  event->kernelStart = static_cast<uint64_t>(std::chrono::duration_cast<std::chrono::nanoseconds>(now).count());
  event->kernelEnd = event->kernelStart + 1;
  event->completionResult = result;
  event->signalled = true;
}

// Waiters released together take their queue places here, so priority rather than wake-up order decides who runs.
// The caller holds event->mutex.
void reserveReleasedLocked(Event *event) {
  std::scoped_lock const guard(blockedEventsMutex);
  std::vector<EngineTurn *> parked;
  for (auto *wait : blockedEvents)
    if (wait->event == event && wait->turn && eventReached(event, wait->target)) parked.push_back(wait->turn);
  PendingJob::release(event, parked);
}

// The caller holds event->mutex.
ze_result_t eventKernelTimestampLocked(const Event *event, ze_kernel_timestamp_result_t *result) {
  if (!event->signalled) return ZE_RESULT_NOT_READY;
  if (event->completionResult != ZE_RESULT_SUCCESS) return event->completionResult;
  result->global = {.kernelStart = event->kernelStart, .kernelEnd = event->kernelEnd};
  result->context = result->global;
  return ZE_RESULT_SUCCESS;
}

ze_result_t completeEvent(Event *event, ze_result_t result) {
  if (!event) return result;
  {
    std::scoped_lock const lock(event->mutex);
    completeEventLocked(event, result);
    if (event->counterBased) ++event->completed;
    reserveReleasedLocked(event);
  }
  event->changed.notify_all();
  return result;
}

ze_result_t signalEvent(Event *event) {
  if (event && event->counterBased) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return completeEvent(event, ZE_RESULT_SUCCESS);
}

// Counter-based events wait for the signals appended before the wait itself, so targets are fixed at submission.
struct WaitSet {
  std::vector<HandleUse<Event>> events;

  [[nodiscard]] std::vector<EventWait> snapshot() const {
    std::vector<EventWait> targets;
    targets.reserve(events.size());
    for (const auto &use : events) {
      auto &event = *use.get();
      std::scoped_lock const lock(event.mutex);
      targets.push_back({.event = &event, .target = event.counterBased ? event.appended : 0});
    }
    return targets;
  }
};

using EventUses = std::shared_ptr<WaitSet>;

struct CommandSignal {
  HandleUse<Event> event;
  EventUses waits;
  explicit operator bool() const { return static_cast<bool>(event); }
};

ze_result_t retainEvents(uint32_t count, ze_event_handle_t *events, EventUses &retained) {
  if (count != 0 && !events) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  retained = std::make_shared<WaitSet>();
  retained->events.reserve(count);
  for (uint32_t index = 0; index < count; ++index) {
    auto *event = cast<Event>(events[index]);
    if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    retained->events.emplace_back(event);
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t retainSignal(ze_event_handle_t handle, CommandSignal &retained) {
  if (!handle) return ZE_RESULT_SUCCESS;
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  retained.event = HandleUse<Event>(event);
  return ZE_RESULT_SUCCESS;
}

ze_result_t waitDependencies(const EventUses &waits) {
  if (!waits) return ZE_RESULT_SUCCESS;
  for (size_t index = 0; index < waits->events.size(); ++index) {
    const auto target = activeSubmission && index < activeSubmission->waits.size() ? activeSubmission->waits[index].target : 0;
    const auto result = waitEvent(waits->events[index].get(), std::numeric_limits<uint64_t>::max(), target);
    if (result != ZE_RESULT_SUCCESS) return result;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t append(CommandList *list, CommandAction command, std::function<void()> release = {}, const CommandSignal &signal = {},
                   std::function<void(ze_result_t)> cancel = {}, bool kernel = false) {
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  RecordedCommand recorded{.action = std::move(command),
                           .release = std::move(release),
                           .complete =
                               [event = signal.event, cancel = std::move(cancel)](ze_result_t result) {
                                 if (result != ZE_RESULT_SUCCESS && cancel) cancel(result);
                                 (void)completeEvent(event.get(), result);
                               },
                           .submit =
                               [waits = signal.waits, event = signal.event] {
                                 Submission submission;
                                 if (waits) submission.waits = waits->snapshot();
                                 if (!event || !event->counterBased) return submission;
                                 std::scoped_lock const lock(event->mutex);
                                 ++event->appended;
                                 return submission;
                               }};
  if (list->immediate) {
    std::scoped_lock const order(list->appendMutex);
    const auto submission = recorded.prepare();
    if (!list->async.stream) return recorded.execute(submission);
    list->async.submit(
        [recorded = std::move(recorded), submission](ze_result_t failure) mutable { return recorded.execute(submission, failure); },
        submission.waits, kernel);
    return ZE_RESULT_SUCCESS;
  }
  if (list->closed) return ZE_RESULT_SUCCESS;
  list->commands.push_back(std::move(recorded));
  return ZE_RESULT_SUCCESS;
}

ze_result_t retainCommandEvents(Context *context, ze_event_handle_t signalHandle, uint32_t waitCount, ze_event_handle_t *waitHandles,
                                CommandSignal &signal, EventUses &waits) {
  const auto waitResult = retainEvents(waitCount, waitHandles, waits);
  if (waitResult != ZE_RESULT_SUCCESS) return waitResult;
  const auto signalResult = retainSignal(signalHandle, signal);
  if (signalResult != ZE_RESULT_SUCCESS) return signalResult;
  signal.waits = waits;
  if ((signal && signal.event->pool->contextId != context->id)
      || (waits->events ^ exists([context](const HandleUse<Event> &event) { return event->pool->contextId != context->id; })))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return ZE_RESULT_SUCCESS;
}

bool environmentAccepted() { return gemstone::environment::accepted(gemstone::environment::invalid()); }

ze_result_t ZE_APICALL init(ze_init_flags_t) { return environmentAccepted() ? ZE_RESULT_SUCCESS : ZE_RESULT_ERROR_UNINITIALIZED; }

ze_result_t ZE_APICALL initDrivers(uint32_t *count, ze_driver_handle_t *drivers, ze_init_driver_type_desc_t *desc) {
  if (!count || !desc) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!environmentAccepted()) return ZE_RESULT_ERROR_UNINITIALIZED;
  if ((desc->flags & ZE_INIT_DRIVER_TYPE_FLAG_GPU) == 0) {
    *count = 0;
    return ZE_RESULT_ERROR_UNINITIALIZED;
  }
  if (!drivers) {
    *count = 1;
  } else if (*count != 0) {
    drivers[0] = reinterpret_cast<ze_driver_handle_t>(&globalDriver);
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverGet(uint32_t *count, ze_driver_handle_t *drivers) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!environmentAccepted()) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (!drivers) {
    *count = 1;
  } else if (*count != 0) {
    drivers[0] = reinterpret_cast<ze_driver_handle_t>(&globalDriver);
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverApiVersion(ze_driver_handle_t, ze_api_version_t *version) {
  if (!version) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *version = ZE_API_VERSION_CURRENT;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverProperties(ze_driver_handle_t, ze_driver_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  // Intel's major.minor.build encoding; the SYCL runtime reports sub-group clocks only from build 1.6.34666 on
  properties->driverVersion = (1U << 24) | (6U << 16) | 34666U;
  std::memset(properties->uuid.id, 0, ZE_MAX_DRIVER_UUID_SIZE);
  std::memcpy(properties->uuid.id, "GEMSTONE", 8);
  for (auto *next = static_cast<ze_base_properties_t *>(properties->pNext); next; next = static_cast<ze_base_properties_t *>(next->pNext))
    if (next->stype == ZE_STRUCTURE_TYPE_DRIVER_MEMORY_FREE_EXT_PROPERTIES)
      reinterpret_cast<ze_driver_memory_free_ext_properties_t *>(next)->freePolicies =
          ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_BLOCKING_FREE | ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_DEFER_FREE;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverIpcProperties(ze_driver_handle_t, ze_driver_ipc_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->flags = 0;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL kernelGlobalOffset(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z);

ze_result_t ZE_APICALL driverExtensions(ze_driver_handle_t, uint32_t *count, ze_driver_extension_properties_t *properties) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  constexpr std::pair<const char *, uint32_t> extensions[] = {
      {ZE_MODULE_PROGRAM_EXP_NAME, ZE_MODULE_PROGRAM_EXP_VERSION_CURRENT},
      {ZE_GLOBAL_OFFSET_EXP_NAME, ZE_GLOBAL_OFFSET_EXP_VERSION_CURRENT},
      {ZE_EVENT_QUERY_KERNEL_TIMESTAMPS_EXT_NAME, ZE_EVENT_QUERY_KERNEL_TIMESTAMPS_EXT_VERSION_CURRENT},
      {ZE_MEMORY_FREE_POLICIES_EXT_NAME, ZE_MEMORY_FREE_POLICIES_EXT_VERSION_CURRENT}};
  if (!properties) {
    *count = std::size(extensions);
  } else {
    const uint32_t reported = std::min(*count, static_cast<uint32_t>(std::size(extensions)));
    for (uint32_t index = 0; index < reported; ++index) {
      std::strncpy(properties[index].name, extensions[index].first, ZE_MAX_EXTENSION_NAME - 1);
      properties[index].name[ZE_MAX_EXTENSION_NAME - 1] = '\0';
      properties[index].version = extensions[index].second;
    }
    *count = reported;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverImportExternalPointer(ze_driver_handle_t handle, void *pointer, size_t size) {
  if (cast<Driver>(handle) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer || size == 0) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  // Gemstone executes against a host-backed mirror, so external host memory
  // does not need a second device registration. Allocations made through the
  // Level Zero memory API are already tracked by the mirror layer.
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverReleaseImportedPointer(ze_driver_handle_t handle, void *pointer) {
  if (cast<Driver>(handle) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverExtensionAddress(ze_driver_handle_t, const char *name, void **address) {
  if (!address) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *address = nullptr;
  if (name && std::strcmp(name, "zexDriverImportExternalPointer") == 0) {
    *address = reinterpret_cast<void *>(&driverImportExternalPointer);
    return ZE_RESULT_SUCCESS;
  }
  if (name && std::strcmp(name, "zexDriverReleaseImportedPointer") == 0) {
    *address = reinterpret_cast<void *>(&driverReleaseImportedPointer);
    return ZE_RESULT_SUCCESS;
  }
  if (name && std::strcmp(name, "zeKernelSetGlobalOffsetExp") == 0) {
    *address = reinterpret_cast<void *>(&kernelGlobalOffset);
    return ZE_RESULT_SUCCESS;
  }
  return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
}

ze_result_t ZE_APICALL driverLastError(ze_driver_handle_t handle, const char **description) {
  if (cast<Driver>(handle) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!description) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  static thread_local std::string text;
  {
    std::scoped_lock const lock(lastRemoteErrorMutex);
    text = lastRemoteError;
  }
  if (text.empty()) {
    std::scoped_lock const rpcLock(rpcTransactionMutex);
    text = remote.error();
  }
  *description = text.c_str();
  return ZE_RESULT_SUCCESS;
}

ze_result_t visibleDevices(std::vector<uint32_t> &visible) {
  const auto configured = gemstone::deviceCount();
  const auto *raw = gemstone::environment::value("ZE_AFFINITY_MASK");
  if (!raw) {
    for (uint32_t ordinal = 0; ordinal < configured; ++ordinal)
      visible.push_back(ordinal);
    return ZE_RESULT_SUCCESS;
  }
  for (std::string_view remaining(raw);;) {
    const auto comma = remaining.find(',');
    const auto token = remaining.substr(0, comma);
    uint32_t ordinal = 0;
    const auto parsed = std::from_chars(token.data(), token.data() + token.size(), ordinal);
    if (parsed.ec != std::errc{} || parsed.ptr != token.data() + token.size() || ordinal >= configured)
      return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    visible.push_back(ordinal);
    if (comma == std::string_view::npos) return ZE_RESULT_SUCCESS;
    remaining.remove_prefix(comma + 1);
  }
}

ze_result_t ZE_APICALL deviceGet(ze_driver_handle_t, uint32_t *count, ze_device_handle_t *devices) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<uint32_t> visible;
  if (const auto result = visibleDevices(visible); result != ZE_RESULT_SUCCESS) return result;
  const auto available = static_cast<uint32_t>(visible.size());
  if (!devices) {
    *count = available;
  } else {
    const auto written = std::min(*count, available);
    for (uint32_t i = 0; i < written; ++i)
      devices[i] = reinterpret_cast<ze_device_handle_t>(&globalDevices[visible[i]]);
    *count = written;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceCanAccessPeer(ze_device_handle_t handle, ze_device_handle_t peer, ze_bool_t *value) {
  if (cast<Device>(handle) == nullptr || cast<Device>(peer) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!value) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *value = true;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceGetP2PProperties(ze_device_handle_t handle, ze_device_handle_t peer, ze_device_p2p_properties_t *properties) {
  if (cast<Device>(handle) == nullptr || cast<Device>(peer) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->flags = ZE_DEVICE_P2P_PROPERTY_FLAG_ACCESS | ZE_DEVICE_P2P_PROPERTY_FLAG_ATOMICS;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceSubdevices(ze_device_handle_t, uint32_t *count, ze_device_handle_t *) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *count = 0;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceProperties(ze_device_handle_t handle, ze_device_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto stype = properties->stype;
  auto *const next = properties->pNext;
  std::memset(properties, 0, sizeof(*properties));
  properties->stype = stype;
  properties->pNext = next;
  properties->type = ZE_DEVICE_TYPE_GPU;
  // The stock DPC++ Level Zero backend currently admits Intel vendor IDs.
  // Gemstone is presenting an Intel-compatible virtual Level Zero GPU here;
  // the literal device name and UUID remain project-owned test identities.
  properties->vendorId = 0x8086;
  properties->deviceId = 0x0001;
  properties->flags = ZE_DEVICE_PROPERTY_FLAG_INTEGRATED;
  properties->coreClockRate = 1000;
  properties->maxMemAllocSize = DeviceMemorySize;
  properties->maxHardwareContexts = 1024;
  properties->maxCommandQueuePriority = ZE_COMMAND_QUEUE_PRIORITY_PRIORITY_HIGH;
  properties->numThreadsPerEU = 8;
  properties->physicalEUSimdWidth = 8;
  properties->numEUsPerSubslice = 8;
  properties->numSubslicesPerSlice = 8;
  properties->numSlices = 1;
  properties->timerResolution = stype == ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES_1_2 ? 1000000000ULL : 1ULL;
  properties->timestampValidBits = 64;
  properties->kernelTimestampValidBits = 64;
  std::memcpy(properties->uuid.id, "GEMSTONE-SPIRV", 14);
  properties->uuid.id[ZE_MAX_DEVICE_UUID_SIZE - 1] = static_cast<uint8_t>(deviceOrdinal(cast<Device>(handle)));
  std::strncpy(properties->name, "Gemstone Virtual SPIR-V GPU", ZE_MAX_DEVICE_NAME - 1);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceCompute(ze_device_handle_t, ze_device_compute_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->maxTotalGroupSize = MaxWorkItems;
  properties->maxGroupSizeX = MaxWorkItems;
  properties->maxGroupSizeY = MaxWorkItems;
  properties->maxGroupSizeZ = MaxWorkItemsZ;
  properties->maxGroupCountX = UINT32_MAX;
  properties->maxGroupCountY = UINT32_MAX;
  properties->maxGroupCountZ = UINT32_MAX;
  properties->maxSharedLocalMemory = 64 * 1024;
  properties->numSubGroupSizes = 3;
  properties->subGroupSizes[0] = 8;
  properties->subGroupSizes[1] = 16;
  properties->subGroupSizes[2] = 32;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceModule(ze_device_handle_t, ze_device_module_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->spirvVersionSupported = ZE_MAKE_VERSION(1, 6);
  properties->flags = ZE_DEVICE_MODULE_FLAG_FP16 | ZE_DEVICE_MODULE_FLAG_FP64 | ZE_DEVICE_MODULE_FLAG_INT64_ATOMICS;
  properties->fp16flags = ZE_DEVICE_FP_FLAG_ROUND_TO_NEAREST | ZE_DEVICE_FP_FLAG_INF_NAN;
  properties->fp32flags = properties->fp16flags;
  properties->fp64flags = properties->fp16flags;
  properties->maxArgumentsSize = 4096;
  properties->printfBufferSize = 1024 * 1024;
  std::memset(properties->nativeKernelSupported.id, 0, ZE_MAX_NATIVE_KERNEL_UUID_SIZE);
  auto *extension = static_cast<ze_base_properties_t *>(properties->pNext);
  while (extension) {
    if (extension->stype == ZE_STRUCTURE_TYPE_FLOAT_ATOMIC_EXT_PROPERTIES) {
      auto *atomics = reinterpret_cast<ze_float_atomic_ext_properties_t *>(extension);
      constexpr auto arithmetic = ZE_DEVICE_FP_ATOMIC_EXT_FLAG_GLOBAL_ADD | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_GLOBAL_MIN_MAX
                                  | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_LOCAL_ADD | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_LOCAL_MIN_MAX;
      constexpr auto supported =
          arithmetic | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_GLOBAL_LOAD_STORE | ZE_DEVICE_FP_ATOMIC_EXT_FLAG_LOCAL_LOAD_STORE;
      atomics->fp16Flags = supported;
      atomics->fp32Flags = supported;
      atomics->fp64Flags = supported;
    }
    extension = static_cast<ze_base_properties_t *>(extension->pNext);
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceQueueGroups(ze_device_handle_t, uint32_t *count, ze_command_queue_group_properties_t *properties) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!properties) {
    *count = static_cast<uint32_t>(QueueGroups.size());
  } else {
    *count = std::min(*count, static_cast<uint32_t>(QueueGroups.size()));
    for (uint32_t ordinal = 0; ordinal < *count; ++ordinal) {
      properties[ordinal].flags = QueueGroups[ordinal].flags;
      properties[ordinal].maxMemoryFillPatternSize = QueueGroups[ordinal].maxMemoryFillPatternSize;
      properties[ordinal].numQueues = QueueGroups[ordinal].numQueues;
    }
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceMemory(ze_device_handle_t, uint32_t *count, ze_device_memory_properties_t *properties) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!properties) {
    *count = 1;
  } else if (*count != 0) {
    properties[0].flags = 0;
    properties[0].maxClockRate = 1000;
    properties[0].maxBusWidth = 256;
    properties[0].totalSize = DeviceMemorySize;
    std::strncpy(properties[0].name, "Gemstone unified memory", ZE_MAX_DEVICE_NAME - 1);
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceMemoryAccess(ze_device_handle_t, ze_device_memory_access_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->hostAllocCapabilities = ZE_MEMORY_ACCESS_CAP_FLAG_RW | ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC | ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT
                                      | ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT_ATOMIC;
  properties->deviceAllocCapabilities = ZE_MEMORY_ACCESS_CAP_FLAG_RW | ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC;
  properties->sharedSingleDeviceAllocCapabilities =
      properties->hostAllocCapabilities | ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT | ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT_ATOMIC;
  properties->sharedCrossDeviceAllocCapabilities = ZE_MEMORY_ACCESS_CAP_FLAG_RW;
  properties->sharedSystemAllocCapabilities = ZE_MEMORY_ACCESS_CAP_FLAG_RW | ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceCache(ze_device_handle_t, uint32_t *count, ze_device_cache_properties_t *properties) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!properties) {
    *count = 1;
  } else if (*count != 0) {
    properties[0].flags = ZE_DEVICE_CACHE_PROPERTY_FLAG_USER_CONTROL;
    properties[0].cacheSize = size_t{4} * 1024 * 1024;
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceStatus(ze_device_handle_t) { return ZE_RESULT_SUCCESS; }
uint64_t deviceTimestamp() {
  const auto now = std::chrono::steady_clock::now().time_since_epoch();
  return static_cast<uint64_t>(std::chrono::duration_cast<std::chrono::nanoseconds>(now).count());
}

ze_result_t ZE_APICALL deviceTimestamps(ze_device_handle_t, uint64_t *host, uint64_t *device) {
  if (!host || !device) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *host = deviceTimestamp();
  *device = *host;
  return ZE_RESULT_SUCCESS;
}

template <class T> ze_result_t clearPropertiesKeepHeader(T *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto stype = properties->stype;
  auto *const next = properties->pNext;
  std::memset(properties, 0, sizeof(*properties));
  properties->stype = stype;
  properties->pNext = next;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceImageProperties(ze_device_handle_t, ze_device_image_properties_t *properties) {
  const auto cleared = clearPropertiesKeepHeader(properties);
  if (cleared != ZE_RESULT_SUCCESS) return cleared;
  properties->maxImageDims1D = MaxImageDims;
  properties->maxImageDims2D = MaxImageDims;
  properties->maxImageDims3D = MaxImageDims3D;
  properties->maxImageArraySlices = MaxImageArraySlices;
  properties->maxSamplers = MaxSamplers;
  properties->maxReadImageArgs = MaxImageArgs;
  properties->maxWriteImageArgs = MaxImageArgs;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceExternalMemoryProperties(ze_device_handle_t, ze_device_external_memory_properties_t *properties) {
  return clearPropertiesKeepHeader(properties);
}

ze_result_t ZE_APICALL devicePciProperties(ze_device_handle_t handle, ze_pci_ext_properties_t *properties) {
  const auto *device = cast<Device>(handle);
  if (!device) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->address = {.domain = 0, .bus = deviceOrdinal(device), .device = 1, .function = 0};
  properties->maxSpeed = {.genVersion = -1, .width = -1, .maxBandwidth = -1};
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL contextCreate(ze_driver_handle_t driver, const ze_context_desc_t *desc, ze_context_handle_t *result) {
  if (cast<Driver>(driver) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<uint32_t> visible;
  if (const auto status = visibleDevices(visible); status != ZE_RESULT_SUCCESS) return status;
  auto *created = createHandle<Context>();
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  for (const auto ordinal : visible)
    created->devices.push_back(&globalDevices[ordinal]);
  *result = reinterpret_cast<ze_context_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL contextCreateEx(ze_driver_handle_t driver, const ze_context_desc_t *desc, uint32_t count,
                                       ze_device_handle_t *devices, ze_context_handle_t *result) {
  if (!devices && count != 0) return ZE_RESULT_ERROR_INVALID_SIZE;
  for (uint32_t index = 0; index < count; ++index)
    if (cast<Device>(devices[index]) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (const auto status = contextCreate(driver, desc, result); status != ZE_RESULT_SUCCESS) return status;
  if (count == 0) return ZE_RESULT_SUCCESS;
  auto *context = cast<Context>(*result);
  context->devices.clear();
  for (uint32_t index = 0; index < count; ++index)
    context->devices.push_back(cast<Device>(devices[index]));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL contextDestroy(ze_context_handle_t handle) {
  auto *context = cast<Context>(handle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  // Objects created in the context keep it alive until they are destroyed.
  HandleUse<Context> const lifetime(context);
  context->destroyRequested.store(true, std::memory_order_release);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL contextStatus(ze_context_handle_t context) {
  return cast<Context>(context) == nullptr ? ZE_RESULT_ERROR_INVALID_NULL_HANDLE : ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL contextMemory(ze_context_handle_t context, ze_device_handle_t device, void *, size_t) {
  return cast<Context>(context) == nullptr || cast<Device>(device) == nullptr ? ZE_RESULT_ERROR_INVALID_NULL_HANDLE : ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL queueCreate(ze_context_handle_t context, ze_device_handle_t device, const ze_command_queue_desc_t *desc,
                                   ze_command_queue_handle_t *result) {
  auto *owner = cast<Context>(context);
  auto *target = cast<Device>(device);
  if (!owner || !target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (desc->mode != ZE_COMMAND_QUEUE_MODE_DEFAULT && desc->mode != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS
      && desc->mode != ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if (!validPriority(desc->priority)) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if (!validEngine(desc->ordinal, desc->index)) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto *created = createHandle<Queue>(owner, target, desc->ordinal, desc->index, desc->mode, desc->priority);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_command_queue_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL queueDestroy(ze_command_queue_handle_t handle) {
  auto *queue = cast<Queue>(handle);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!queue->async.idle(1'000'000'000)) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  return destroyHandle(queue);
}
ze_result_t ZE_APICALL queueExecute(ze_command_queue_handle_t queueHandle, uint32_t count, ze_command_list_handle_t *handles,
                                    ze_fence_handle_t fenceHandle) {
  auto *queue = cast<Queue>(queueHandle);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  auto *device = queue->device;
  if (deviceLost(device)) return ZE_RESULT_ERROR_DEVICE_LOST;
  if (count != 0 && !handles) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<HandleUse<CommandList>> lists;
  lists.reserve(count);
  for (uint32_t index = 0; index < count; ++index) {
    auto *list = cast<CommandList>(handles[index]);
    if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (list->immediate || list->ordinal != queue->ordinal) return ZE_RESULT_ERROR_INVALID_COMMAND_LIST_TYPE;
    lists.emplace_back(list);
  }
  HandleUse<Fence> fence;
  Fence *submittedFence = nullptr;
  if (fenceHandle) {
    auto *value = cast<Fence>(fenceHandle);
    if (!value) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (value->queue.get() != queue) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    fence = HandleUse<Fence>(value);
    std::scoped_lock const lock(fence->event.mutex);
    fence->pending = true;
    submittedFence = value;
  }
  std::vector<Submission> submissions;
  for (const auto &retained : lists)
    for (const auto &command : retained->commands)
      submissions.push_back(command.prepare());
  const auto leading = submissions.empty() ? std::vector<EventWait>{} : submissions.front().waits;
  std::function<ze_result_t()> submission = [lists = std::move(lists), submissions = std::move(submissions), fence = std::move(fence),
                                             device]() mutable {
    ze_result_t result = ZE_RESULT_SUCCESS;
    size_t next = 0;
    for (const auto &retained : lists)
      for (auto &command : retained->commands)
        result = command.execute(submissions[next++], result);
    reportStatus(device, result);
    if (fence) {
      {
        std::scoped_lock const lock(fence->event.mutex);
        // A successful list reset can immediately be followed by fence reset
        // or destruction. Publish completion under the same lock as releasing
        // list uses so those calls cannot observe a housekeeping-only pending
        // submission or race a delayed signal from its previous use.
        lists.clear();
        fence->completionResult = result;
        fence->pending = false;
        completeEventLocked(&fence->event, ZE_RESULT_SUCCESS);
        reserveReleasedLocked(&fence->event);
      }
      fence->event.changed.notify_all();
    } else {
      lists.clear();
    }
    return result;
  };
  if (queue->async.stream) {
    // The job completes the fence under its lock, so the ticket is published first.
    std::unique_lock<std::mutex> fenceLock;
    if (submittedFence) fenceLock = std::unique_lock(submittedFence->event.mutex);
    auto ticket = queue->async.submit([submission = std::move(submission)](ze_result_t) mutable { return submission(); }, leading);
    if (submittedFence) submittedFence->submission = std::move(ticket);
    return ZE_RESULT_SUCCESS;
  }
  const std::scoped_lock synchronousLock(queue->synchronousMutex);
  const auto ticket = gemstone::CommandQueue::pending();
  if (submittedFence) {
    std::scoped_lock const lock(submittedFence->event.mutex);
    submittedFence->submission = ticket;
  }
  const auto result = submission();
  submission = {};
  scheduler().complete(ticket, 0);
  return result;
}
ze_result_t ZE_APICALL queueSync(ze_command_queue_handle_t handle, uint64_t timeout) {
  auto *queue = cast<Queue>(handle);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  auto *device = queue->device;
  if (deviceLost(device)) return ZE_RESULT_ERROR_DEVICE_LOST;
  return reportStatus(device, queue->async.waitFor(timeout));
}

ze_result_t ZE_APICALL listCreate(ze_context_handle_t context, ze_device_handle_t device, const ze_command_list_desc_t *desc,
                                  ze_command_list_handle_t *result) {
  auto *owner = cast<Context>(context);
  auto *target = cast<Device>(device);
  if (!owner || !target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!validEngine(desc->commandQueueGroupOrdinal, 0)) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto *created = createHandle<CommandList>(false, owner, target, desc->commandQueueGroupOrdinal);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_command_list_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL listCreateImmediate(ze_context_handle_t context, ze_device_handle_t device, const ze_command_queue_desc_t *desc,
                                           ze_command_list_handle_t *result) {
  auto *owner = cast<Context>(context);
  auto *target = cast<Device>(device);
  if (!owner || !target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (desc->mode != ZE_COMMAND_QUEUE_MODE_DEFAULT && desc->mode != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS
      && desc->mode != ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if (!validPriority(desc->priority)) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if (!validEngine(desc->ordinal, desc->index)) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto *created = createHandle<CommandList>(true, owner, target, desc->ordinal, desc->index, desc->mode, desc->priority);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_command_list_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
bool listUsesReleased(CommandList *list) {
  std::unique_lock lock(list->usersMutex);
  return list->usersChanged.wait_for(lock, std::chrono::seconds(1), [&] { return list->users.load(std::memory_order_acquire) == 0; });
}
ze_result_t ZE_APICALL listDestroy(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return listUsesReleased(list) ? destroyHandle(list) : ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
}
ze_result_t ZE_APICALL listClose(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  list->closed = true;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL listReset(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!listUsesReleased(list)) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  list->commands.clear();
  list->closed = false;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL listBarrier(ze_command_list_handle_t handle, ze_event_handle_t signal, uint32_t waitCount,
                                   ze_event_handle_t *waits) {
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  return append(list, [waits = retainedWaits] { return waitDependencies(waits); }, {}, retainedSignal);
}

ze_result_t ZE_APICALL listMemoryRangesBarrier(ze_command_list_handle_t handle, uint32_t rangeCount, const size_t *rangeSizes,
                                               const void **ranges, ze_event_handle_t signal, uint32_t waitCount,
                                               ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (rangeCount == 0 || !rangeSizes || !ranges) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  for (uint32_t index = 0; index < rangeCount; ++index) {
    if (!ranges[index]) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
    if (rangeSizes[index] == 0 || rangeSizes[index] > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(ranges[index]))
      return ZE_RESULT_ERROR_INVALID_SIZE;
    if (!retainAllocation(ranges[index], rangeSizes[index]) && retainAllocation(ranges[index], 0)) return ZE_RESULT_ERROR_INVALID_SIZE;
  }
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  return append(list, [waits = retainedWaits] { return waitDependencies(waits); }, {}, retainedSignal);
}

ze_result_t ZE_APICALL contextSystemBarrier(ze_context_handle_t context, ze_device_handle_t device) {
  if (!cast<Context>(context) || !cast<Device>(device)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL listWriteGlobalTimestamp(ze_command_list_handle_t handle, uint64_t *destination, ze_event_handle_t signal,
                                                uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!destination) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto lifetime = std::make_shared<std::vector<AllocationRef>>();
  if (auto allocation = retainAllocation(destination, sizeof(uint64_t))) lifetime->push_back(std::move(allocation));
  return append(
      list,
      [lifetime, destination, waits = retainedWaits] {
        const auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS) *destination = deviceTimestamp();
        return result;
      },
      [lifetime] { lifetime->clear(); }, retainedSignal);
}

ze_result_t ZE_APICALL listQueryKernelTimestamps(ze_command_list_handle_t handle, uint32_t count, ze_event_handle_t *events,
                                                 void *destination, const size_t *offsets, ze_event_handle_t signal, uint32_t waitCount,
                                                 ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!events || !destination) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (waitCount != 0 && !waits) return ZE_RESULT_ERROR_INVALID_SIZE;
  std::vector<ze_event_handle_t> handles(events, events + count);
  handles.insert(handles.end(), waits, waits + waitCount);
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, count + waitCount, handles.data(), retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto lifetime = std::make_shared<std::vector<AllocationRef>>();
  std::vector<ze_kernel_timestamp_result_t *> targets;
  targets.reserve(count);
  for (uint32_t index = 0; index < count; ++index) {
    const auto offset = offsets ? offsets[index] : index * sizeof(ze_kernel_timestamp_result_t);
    auto *target = reinterpret_cast<ze_kernel_timestamp_result_t *>(static_cast<std::byte *>(destination) + offset);
    if (auto allocation = retainAllocation(target, sizeof(ze_kernel_timestamp_result_t))) lifetime->push_back(std::move(allocation));
    targets.push_back(target);
  }
  return append(
      list,
      [lifetime, targets, waits = retainedWaits] {
        const auto result = waitDependencies(waits);
        if (result != ZE_RESULT_SUCCESS) return result;
        for (size_t index = 0; index < targets.size(); ++index) {
          auto *event = waits->events[index].get();
          std::scoped_lock const lock(event->mutex);
          if (const auto timestamp = eventKernelTimestampLocked(event, targets[index]); timestamp != ZE_RESULT_SUCCESS) return timestamp;
        }
        return ZE_RESULT_SUCCESS;
      },
      [lifetime] { lifetime->clear(); }, retainedSignal);
}

ze_result_t listCopyInContext(ze_command_list_handle_t handle, void *destination, Context *sourceContext, const void *source, size_t size,
                              ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if ((!destination || !source) && size != 0) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (size > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(destination)
      || size > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(source))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto destinationAllocation = retainAllocation(destination, size);
  auto sourceAllocation = retainAllocation(source, size);
  // Allocations are visible to every context of the driver; only AppendMemoryCopyFromContext names a source context.
  if (sourceContext && sourceAllocation && sourceAllocation->context.get() != sourceContext) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  // A failed full-range lookup must not turn a tracked out-of-bounds pointer into a host pointer.
  if ((!destinationAllocation && retainAllocation(destination, 0)) || (!sourceAllocation && retainAllocation(source, 0)))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>();
  if (destinationAllocation) allocationLifetime->push_back(std::move(destinationAllocation));
  if (sourceAllocation) allocationLifetime->push_back(std::move(sourceAllocation));
  return append(
      list,
      [=, waits = retainedWaits] {
        (void)allocationLifetime;
        auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS && size != 0) std::memmove(destination, source, size);
        return result;
      },
      [allocationLifetime] { allocationLifetime->clear(); }, retainedSignal);
}

ze_result_t ZE_APICALL listCopy(ze_command_list_handle_t handle, void *destination, const void *source, size_t size,
                                ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return listCopyInContext(handle, destination, nullptr, source, size, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listCopyFromContext(ze_command_list_handle_t handle, void *destination, ze_context_handle_t sourceContextHandle,
                                           const void *source, size_t size, ze_event_handle_t signal, uint32_t waitCount,
                                           ze_event_handle_t *waits) {
  auto *sourceContext = cast<Context>(sourceContextHandle);
  if (!sourceContext) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return listCopyInContext(handle, destination, sourceContext, source, size, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listCopyRegion(ze_command_list_handle_t handle, void *destination, const ze_copy_region_t *destinationRegion,
                                      uint32_t destinationRowPitch, uint32_t destinationSlicePitch, const void *source,
                                      const ze_copy_region_t *sourceRegion, uint32_t sourceRowPitch, uint32_t sourceSlicePitch,
                                      ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!destination || !source || !destinationRegion || !sourceRegion) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto dst = *destinationRegion;
  const auto src = *sourceRegion;
  if (dst.width != src.width || dst.height != src.height || dst.depth != src.depth) return ZE_RESULT_ERROR_INVALID_SIZE;
  if (dst.width == 0 || dst.height == 0) return listCopy(handle, destination, source, 0, signal, waitCount, waits);
  const size_t depth = src.depth == 0 ? 1 : src.depth;
  const auto add = [](size_t a, size_t b, size_t &out) {
    if (a > std::numeric_limits<size_t>::max() - b) return false;
    out = a + b;
    return true;
  };
  const auto multiply = [](size_t a, size_t b, size_t &out) {
    if (b != 0 && a > std::numeric_limits<size_t>::max() / b) return false;
    out = a * b;
    return true;
  };
  struct Layout {
    size_t offset, row, slice, span;
  };
  const auto layout = [&](const ze_copy_region_t &region, uint32_t rowPitch, uint32_t slicePitch, Layout &out) {
    out.row = rowPitch == 0 ? region.width : rowPitch;
    size_t endX = 0;
    size_t endY = 0;
    size_t endZ = 0;
    size_t minimumSlice = 0;
    size_t startY = 0;
    size_t startZ = 0;
    size_t endRow = 0;
    size_t endSlice = 0;
    if (!add(region.originX, region.width, endX) || endX > out.row || !add(region.originY, region.height, endY)
        || !multiply(out.row, endY, minimumSlice))
      return false;
    if (region.depth == 0) out.slice = 0;
    else out.slice = slicePitch == 0 ? minimumSlice : slicePitch;
    if (depth > 1 && out.slice < minimumSlice) return false;
    const size_t originZ = region.depth == 0 ? 0 : region.originZ;
    return add(originZ, depth - 1, endZ) && multiply(region.originY, out.row, startY) && multiply(originZ, out.slice, startZ)
           && add(startY, startZ, out.offset) && add(out.offset, region.originX, out.offset) && multiply(endY - 1, out.row, endRow)
           && multiply(endZ, out.slice, endSlice) && add(endRow, endSlice, out.span) && add(out.span, endX, out.span);
  };
  Layout sourceLayout{};
  Layout destinationLayout{};
  if (!layout(src, sourceRowPitch, sourceSlicePitch, sourceLayout)
      || !layout(dst, destinationRowPitch, destinationSlicePitch, destinationLayout))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  const auto sourceAddress = reinterpret_cast<uintptr_t>(source);
  const auto destinationAddress = reinterpret_cast<uintptr_t>(destination);
  if (sourceLayout.span > std::numeric_limits<uintptr_t>::max() - sourceAddress
      || destinationLayout.span > std::numeric_limits<uintptr_t>::max() - destinationAddress)
    return ZE_RESULT_ERROR_INVALID_SIZE;
  auto sourceAllocation = retainAllocation(source, 0);
  auto destinationAllocation = retainAllocation(destination, 0);
  if ((sourceAllocation && !retainAllocation(source, sourceLayout.span))
      || (destinationAllocation && !retainAllocation(destination, destinationLayout.span)))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  size_t rows = 0;
  if (!multiply(src.height, depth, rows)) return ZE_RESULT_ERROR_INVALID_SIZE;
  const auto rowOffset = [height = static_cast<size_t>(src.height)](const Layout &value, size_t row) {
    return value.offset + ((row / height) * value.slice) + ((row % height) * value.row);
  };
  // Test actual row intervals, not their enclosing spans: disjoint columns may share a pitched allocation.
  size_t sourceRow = 0;
  size_t destinationRow = 0;
  while (sourceRow < rows && destinationRow < rows) {
    const auto a = sourceAddress + rowOffset(sourceLayout, sourceRow);
    const auto b = destinationAddress + rowOffset(destinationLayout, destinationRow);
    if (a < b + src.width && b < a + src.width) return ZE_RESULT_ERROR_OVERLAPPING_REGIONS;
    if (a < b) ++sourceRow;
    else ++destinationRow;
  }
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto lifetime = std::make_shared<std::vector<AllocationRef>>();
  if (sourceAllocation) lifetime->push_back(std::move(sourceAllocation));
  if (destinationAllocation) lifetime->push_back(std::move(destinationAllocation));
  return append(
      list,
      [=, waits = retainedWaits] {
        (void)lifetime;
        const auto result = waitDependencies(waits);
        if (result != ZE_RESULT_SUCCESS) return result;
        for (size_t row = 0; row < rows; ++row)
          std::memcpy(static_cast<uint8_t *>(destination) + rowOffset(destinationLayout, row),
                      static_cast<const uint8_t *>(source) + rowOffset(sourceLayout, row), src.width);
        return ZE_RESULT_SUCCESS;
      },
      [lifetime] { lifetime->clear(); }, retainedSignal);
}

ze_result_t ZE_APICALL listFill(ze_command_list_handle_t handle, void *destination, const void *pattern, size_t patternSize, size_t size,
                                ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!destination || !pattern || patternSize == 0) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (size > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(destination)) return ZE_RESULT_ERROR_INVALID_SIZE;
  auto destinationAllocation = retainAllocation(destination, size);
  if (!destinationAllocation && retainAllocation(destination, 0)) return ZE_RESULT_ERROR_INVALID_SIZE;
  std::vector<uint8_t> const copiedPattern(static_cast<const uint8_t *>(pattern), static_cast<const uint8_t *>(pattern) + patternSize);
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>();
  if (destinationAllocation) allocationLifetime->push_back(std::move(destinationAllocation));
  return append(
      list,
      [=, waits = retainedWaits] {
        (void)allocationLifetime;
        auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS) {
          auto *bytes = static_cast<uint8_t *>(destination);
          for (size_t offset = 0; offset < size; ++offset)
            bytes[offset] = copiedPattern[offset % patternSize];
        }
        return result;
      },
      [allocationLifetime] { allocationLifetime->clear(); }, retainedSignal);
}

ze_result_t ZE_APICALL listPrefetch(ze_command_list_handle_t handle, const void *pointer, size_t size) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto allocation = retainAllocation(pointer, size);
  if (!allocation && retainAllocation(pointer, 0)) return ZE_RESULT_ERROR_INVALID_SIZE;
  return append(list, [] { return ZE_RESULT_SUCCESS; }, [allocation = std::move(allocation)]() mutable { allocation.reset(); });
}

ze_result_t ZE_APICALL listAdvise(ze_command_list_handle_t handle, ze_device_handle_t deviceHandle, const void *pointer, size_t size,
                                  ze_memory_advice_t advice) {
  auto *list = cast<CommandList>(handle);
  auto *device = cast<Device>(deviceHandle);
  if (!list || !device) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (advice < ZE_MEMORY_ADVICE_SET_READ_MOSTLY || advice > ZE_MEMORY_ADVICE_CLEAR_SYSTEM_MEMORY_PREFERRED_LOCATION)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  auto allocation = retainAllocation(pointer, size);
  if (!allocation && retainAllocation(pointer, 0)) return ZE_RESULT_ERROR_INVALID_SIZE;
  return append(list, [] { return ZE_RESULT_SUCCESS; }, [allocation = std::move(allocation)]() mutable { allocation.reset(); });
}

ze_result_t ZE_APICALL listSignal(ze_command_list_handle_t handle, ze_event_handle_t signal) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  CommandSignal retainedSignal;
  const auto retained = retainSignal(signal, retainedSignal);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  if (retainedSignal && retainedSignal.event->pool->contextId != list->context->id) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return append(list, [] { return ZE_RESULT_SUCCESS; }, {}, retainedSignal);
}
ze_result_t ZE_APICALL listWait(ze_command_list_handle_t handle, uint32_t count, ze_event_handle_t *events) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  EventUses retainedEvents;
  const auto retained = retainEvents(count, events, retainedEvents);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  if (retainedEvents->events ^ exists([list](const HandleUse<Event> &event) { return event->pool->contextId != list->context->id; }))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return append(
      list, [events = retainedEvents] { return waitDependencies(events); }, {}, CommandSignal{.event = {}, .waits = retainedEvents});
}
ze_result_t ZE_APICALL listResetEvent(ze_command_list_handle_t handle, ze_event_handle_t eventHandle) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  auto *event = cast<Event>(eventHandle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (event->pool->contextId != list->context->id) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return append(list, [event = HandleUse<Event>(event)] {
    std::scoped_lock const lock(event->mutex);
    event->signalled = false;
    event->completionResult = ZE_RESULT_SUCCESS;
    return ZE_RESULT_SUCCESS;
  });
}

bool uploadAllocations(Client &client, const KernelLaunch &kernel, const std::vector<PointerRelocation> &relocations,
                       const std::vector<uint64_t> &moduleGlobals) {
  std::unordered_set<uint64_t> reachable;
  if (!kernel.indirect)
    for (const auto &relocation : relocations)
      reachable.insert(static_cast<uint64_t>(relocation.target.value));
  reachable.insert(moduleGlobals.begin(), moduleGlobals.end());
  std::scoped_lock const lock(allocationsMutex);
  for (const auto &[base, allocation] : allocations) {
    if (allocation->type != ZE_MEMORY_TYPE_DEVICE || !allocation->id) continue;
    if (!kernel.indirect && !reachable.contains(*allocation->id)) continue;
    if (!uploadAllocation(client, AllocationId{static_cast<int64_t>(*allocation->id)}, 0, base, allocation->size, MirrorChunkSize))
      return false;
  }
  return true;
}

bool copyBackDirtyRanges(Client &client, const std::vector<DirtyRange> &ranges) {
  std::scoped_lock const lock(allocationsMutex);
  return readDirtyRanges(client, ranges, [&](const DirtyRange &range, const uint8_t *bytes) {
    const auto id = static_cast<uint64_t>(range.allocation.value);
    const auto offset = static_cast<uint64_t>(range.offset.value);
    const auto size = static_cast<uint64_t>(range.size.value);
    const auto found = allocations ^ find([&](void *, const AllocationRef &entry) { return entry->id == id; });
    if (!found || offset > found->second->size || size > found->second->size - offset) return false;
    std::memcpy(static_cast<uint8_t *>(found->first) + offset, bytes, size);
    return true;
  });
}

// Device faults, traps and hangs all lose the device, as the Level Zero driver reports them.
ze_result_t launchStatus(gemstone::execution::FailureKind kind) {
  using gemstone::execution::FailureKind;
  switch (kind) {
    case FailureKind::InvalidProgram: return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    case FailureKind::InvalidConfiguration: return ZE_RESULT_ERROR_INVALID_GROUP_SIZE_DIMENSION;
    case FailureKind::InvalidRequest: return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    case FailureKind::NotFound: return ZE_RESULT_ERROR_INVALID_KERNEL_NAME;
    case FailureKind::Unsupported: return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
    case FailureKind::OutOfResources: return ZE_RESULT_ERROR_OUT_OF_DEVICE_MEMORY;
    default: return ZE_RESULT_ERROR_DEVICE_LOST;
  }
}

ze_result_t launchRpc(const KernelLaunch &kernel, const ze_group_count_t &groups) {
  // a kernel may wait on work queued behind it, so the engine moves on at launch
  const EngineTurn::Flight inFlight;
  // Serialise metadata preparation and copy-back, but let independent queues
  // execute and pump host-memory exchanges on separate connections.
  std::unique_lock rpcLock(rpcTransactionMutex);
  if (remote.mode() == ClientMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (remote.mode() != ClientMode::Ready) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  // Ordinary host pointers are serviced by exact interpreter accesses.
  if (!remote.client()->call(EnableHostMemory{})) return reportStatus(kernel.device, ZE_RESULT_ERROR_DEVICE_LOST);
  const auto started = std::chrono::steady_clock::now();
  LaunchPerformance performance;
  const auto relocations = collectPointerRelocations(kernel, performance);
  std::vector<uint64_t> opaqueAllocations;
  for (const auto &input : kernel.arguments) {
    if (input.opaque == Kernel::Argument::Opaque::None) continue;
    if (input.recordId == 0) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    opaqueAllocations.push_back(input.recordId);
    if (input.texelsId != 0) opaqueAllocations.push_back(input.texelsId);
  }
  if (!uploadAllocations(*remote.client(), kernel, relocations, opaqueAllocations)) {
    recordRemoteError(remote.error());
    return reportStatus(kernel.device, ZE_RESULT_ERROR_DEVICE_LOST);
  }

  std::vector<gemstone::execution::Argument> arguments;
  arguments.reserve(kernel.arguments.size());
  for (size_t index = 0; index < kernel.arguments.size(); ++index) {
    const auto &input = kernel.arguments[index];
    if (input.local) {
      arguments.emplace_back(ArgumentLocal{static_cast<int64_t>(input.localSize)});
      continue;
    }
    if (input.opaque != Kernel::Argument::Opaque::None) {
      arguments.emplace_back(ArgumentMemory{.region = AllocationId{static_cast<int64_t>(input.recordId)},
                                            .offset = {0},
                                            .range = std::optional<LongPositive>{static_cast<int64_t>(input.recordBytes)}});
      continue;
    }
    const bool pointerAggregate = index < kernel.parameters.size() && kernel.parameters[index].pointerAggregate;
    const auto directPointer = !pointerAggregate && input.bytes.size() == sizeof(uintptr_t)
                                   ? relocations ^ find([index](const PointerRelocation &relocation) {
                                       const auto *source = std::get_if<RelocationSourceArgument>(&relocation.source);
                                       return source && std::cmp_equal(source->index.value, index) && relocation.sourceOffset.value == 0;
                                     })
                                   : std::optional<PointerRelocation>{};
    if (directPointer) {
      arguments.emplace_back(ArgumentMemory{.region = directPointer->target, .offset = directPointer->targetOffset});
    } else {
      arguments.emplace_back(ArgumentScalar{input.bytes});
    }
  }

  const Dimensions groupSize{
      .x = {static_cast<int32_t>(kernel.groupX)}, .y = {static_cast<int32_t>(kernel.groupY)}, .z = {static_cast<int32_t>(kernel.groupZ)}};
  const Dimensions groupCount{.x = {static_cast<int32_t>(groups.groupCountX)},
                              .y = {static_cast<int32_t>(groups.groupCountY)},
                              .z = {static_cast<int32_t>(groups.groupCountZ)}};
  const auto globalX = static_cast<uint64_t>(groupSize.x.value) * groupCount.x.value;
  const auto globalY = static_cast<uint64_t>(groupSize.y.value) * groupCount.y.value;
  const auto globalZ = static_cast<uint64_t>(groupSize.z.value) * groupCount.z.value;
  if (globalX > static_cast<uint64_t>(std::numeric_limits<int64_t>::max())
      || globalY > static_cast<uint64_t>(std::numeric_limits<int64_t>::max())
      || globalZ > static_cast<uint64_t>(std::numeric_limits<int64_t>::max()))
    return ZE_RESULT_ERROR_INVALID_GROUP_SIZE_DIMENSION;
  const GlobalDimensions globalSize{
      .x = {static_cast<int64_t>(globalX)}, .y = {static_cast<int64_t>(globalY)}, .z = {static_cast<int64_t>(globalZ)}};
  auto launchRelocations = relocations ^ filter([&](const PointerRelocation &relocation) {
                             const auto *source = std::get_if<RelocationSourceArgument>(&relocation.source);
                             const bool directPointer = source && relocation.sourceOffset.value == 0
                                                        && static_cast<size_t>(source->index.value) < kernel.arguments.size()
                                                        && (static_cast<size_t>(source->index.value) >= kernel.parameters.size()
                                                            || !kernel.parameters[source->index.value].pointerAggregate)
                                                        && kernel.arguments[source->index.value].bytes.size() == sizeof(uintptr_t);
                             return !directPointer;
                           });
  for (const auto &input : kernel.arguments)
    if (input.opaque == Kernel::Argument::Opaque::Image)
      launchRelocations.push_back({.source = RelocationSourceMemory{AllocationId{static_cast<int64_t>(input.recordId)}},
                                   .sourceOffset = LongPositive0{static_cast<int64_t>(descriptor::Layout::Texels)},
                                   .target = AllocationId{static_cast<int64_t>(input.texelsId)},
                                   .targetOffset = LongPositive0{0}});
  const Coordinates offset{.x = {static_cast<int32_t>(kernel.offsetX)},
                           .y = {static_cast<int32_t>(kernel.offsetY)},
                           .z = {static_cast<int32_t>(kernel.offsetZ)}};
  // A command-list worker must not invent a kernel-duration limit. User-facing
  // event/fence/queue synchronisation owns the Level Zero timeout semantics;
  // each RPC exchange still retains its independent transport-progress timeout.
  gemstone::execution::CompletionResult result;
  std::string completionError;
  const bool collected = gemstone::execution::launchAndCollect(Launch{.program = ProgramId{static_cast<int64_t>(kernel.programId)},
                                                                      .kernelName = {kernel.name},
                                                                      .groupSize = groupSize,
                                                                      .groupCount = groupCount,
                                                                      .globalSize = globalSize,
                                                                      .workDim = {3},
                                                                      .globalOffset = offset,
                                                                      .privateSegmentBytes = {0},
                                                                      .groupSegmentBytes = {0},
                                                                      .arguments = std::move(arguments),
                                                                      .pointerRelocations = std::move(launchRelocations)},
                                                               result, completionError, [&] { rpcLock.unlock(); });
  if (!rpcLock.owns_lock()) rpcLock.lock();
  if (!collected) {
    recordRemoteError(completionError);
    return reportStatus(kernel.device, launchStatus(result.failure));
  }
  {
    std::string relocationError;
    const bool applied = applyPointerRelocations(
        result.pointerRelocations,
        [](uint64_t id) -> RelocationTarget {
          const auto allocation = findAllocationById(id);
          return allocation ? RelocationTarget{.base = allocation->base, .size = allocation->size} : RelocationTarget{};
        },
        [](uint64_t, uint64_t, uint64_t, uint64_t) {}, relocationError);
    if (!applied) {
      recordRemoteError(relocationError);
      return reportStatus(kernel.device, ZE_RESULT_ERROR_DEVICE_LOST);
    }
  }
  if (!copyBackDirtyRanges(*remote.client(), result.dirtyRanges)) {
    recordRemoteError(remote.error());
    return reportStatus(kernel.device, ZE_RESULT_ERROR_DEVICE_LOST);
  }
  if (gemstone::traceEnabled("perf")) {
    const auto elapsed = std::chrono::duration_cast<std::chrono::microseconds>(std::chrono::steady_clock::now() - started).count();
    std::fprintf(stderr, "Gemstone Level Zero perf: kernel=%s scan=%llu relocations=%llu elapsed_us=%lld\n", kernel.name.c_str(),
                 static_cast<unsigned long long>(performance.relocationScanBytes),
                 static_cast<unsigned long long>(performance.relocationCount), static_cast<long long>(elapsed));
  }
  return ZE_RESULT_SUCCESS;
}

void resolveOpaqueArguments(KernelLaunch &launch) {
  for (auto &argument : launch.arguments) {
    if (argument.opaque == Kernel::Argument::Opaque::None) continue;
    void *handle = nullptr;
    std::memcpy(&handle, argument.bytes.data(), sizeof(handle));
    void *record = nullptr;
    void *texels = nullptr;
    if (argument.opaque == Kernel::Argument::Opaque::Image) {
      if (const auto *image = cast<Image>(handle)) {
        record = image->record;
        texels = image->texels;
        argument.recordBytes = ImageRecordBytes;
      }
    } else if (const auto *sampler = cast<Sampler>(handle)) {
      record = sampler->record;
      argument.recordBytes = SamplerRecordBytes;
    }
    if (const auto allocation = retainAllocation(record, 0)) {
      argument.recordId = allocation->id.value_or(0);
      launch.allocations.push_back(allocation);
    }
    if (const auto allocation = retainAllocation(texels, 0)) {
      argument.texelsId = allocation->id.value_or(0);
      launch.allocations.push_back(allocation);
    }
  }
}

KernelLaunch snapshotLaunch(Kernel *kernel) {
  auto lifetime = std::make_shared<HandleUse<Kernel>>(kernel);
  auto launch = KernelLaunch{.lifetime = lifetime,
                             .device = kernel->module->device,
                             .programId = kernel->module->program->id,
                             .name = kernel->linkageName(),
                             .groupX = kernel->groupX,
                             .groupY = kernel->groupY,
                             .groupZ = kernel->groupZ,
                             .offsetX = kernel->offsetX,
                             .offsetY = kernel->offsetY,
                             .offsetZ = kernel->offsetZ,
                             .indirect = kernel->indirect != 0,
                             .arguments = kernel->arguments,
                             .parameters = kernel->parameters,
                             .allocations = retainKernelAllocations(*kernel)};
  resolveOpaqueArguments(launch);
  return launch;
}

ze_result_t recordLaunch(ze_command_list_handle_t handle, ze_kernel_handle_t kernelHandle, const ze_group_count_t *groups,
                         bool indirectGroups, ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *kernel = cast<Kernel>(kernelHandle);
  auto *list = cast<CommandList>(handle);
  if (!kernel || !list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!groups) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!list->computeCapable()) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  const auto copiedGroups = indirectGroups ? ze_group_count_t{} : *groups;
  if (gemstone::traceEnabled("levelzero") && static_cast<uint64_t>(kernel->groupX) * copiedGroups.groupCountX > 1000)
    std::fprintf(stderr, "Gemstone Level Zero: appending %s with group %ux%ux%u count %ux%ux%u\n", kernel->name.c_str(), kernel->groupX,
                 kernel->groupY, kernel->groupZ, copiedGroups.groupCountX, copiedGroups.groupCountY, copiedGroups.groupCountZ);
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  KernelLaunch launch = snapshotLaunch(kernel);
  auto kernelLifetime = launch.lifetime;
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>(std::move(launch.allocations));
  if (indirectGroups) {
    if (auto allocation = retainAllocation(groups, sizeof(*groups))) allocationLifetime->push_back(std::move(allocation));
  }
  return append(
      list,
      [launch = std::move(launch), copiedGroups, groups, indirectGroups, waits = retainedWaits, allocationLifetime] {
        (void)allocationLifetime;
        auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS) result = launchRpc(launch, indirectGroups ? *groups : copiedGroups);
        return result;
      },
      [allocationLifetime, kernelLifetime] {
        allocationLifetime->clear();
        *kernelLifetime = {};
      },
      retainedSignal, {}, true);
}

ze_result_t ZE_APICALL listLaunch(ze_command_list_handle_t handle, ze_kernel_handle_t kernel, const ze_group_count_t *groups,
                                  ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  return recordLaunch(handle, kernel, groups, false, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listLaunchIndirect(ze_command_list_handle_t handle, ze_kernel_handle_t kernel, const ze_group_count_t *groups,
                                          ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  return recordLaunch(handle, kernel, groups, true, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listImmediateAppendCommandLists(ze_command_list_handle_t handle, uint32_t count, ze_command_list_handle_t *lists,
                                                       ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *target = cast<CommandList>(handle);
  if (!target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (count == 0 || !lists) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<HandleUse<CommandList>> sources;
  sources.reserve(count);
  for (uint32_t index = 0; index < count; ++index) {
    auto *source = cast<CommandList>(lists[index]);
    if (!source) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (source->ordinal != target->ordinal) return ZE_RESULT_ERROR_INVALID_COMMAND_LIST_TYPE;
    sources.emplace_back(source);
  }
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(target->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  // The immediate list adopts the recorded commands; cancellation must also
  // complete child events when an earlier immediate command has failed.
  auto commands = std::make_shared<std::vector<HandleUse<CommandList>>>(std::move(sources));
  const auto execute = [commands](ze_result_t result) {
    for (const auto &source : *commands)
      for (auto &command : source->commands)
        result = command.execute(command.prepare(), result);
    commands->clear();
    return result;
  };
  return append(
      target, [execute, waits = retainedWaits] { return execute(waitDependencies(waits)); }, {}, retainedSignal,
      [execute](ze_result_t result) { (void)execute(result); });
}

ze_result_t ZE_APICALL listLaunchMultipleIndirect(ze_command_list_handle_t handle, uint32_t maxCount, ze_kernel_handle_t *handles,
                                                  const uint32_t *count, const ze_group_count_t *groups, ze_event_handle_t signal,
                                                  uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!handles || !count || !groups) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!list->computeCapable()) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  std::vector<KernelLaunch> launches;
  launches.reserve(maxCount);
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>();
  for (uint32_t index = 0; index < maxCount; ++index) {
    auto *kernel = cast<Kernel>(handles[index]);
    if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    launches.push_back(snapshotLaunch(kernel));
    for (auto &allocation : launches.back().allocations)
      allocationLifetime->push_back(std::move(allocation));
    launches.back().allocations.clear();
  }
  if (auto allocation = retainAllocation(count, sizeof(*count))) allocationLifetime->push_back(std::move(allocation));
  if (auto allocation = retainAllocation(groups, maxCount * sizeof(*groups))) allocationLifetime->push_back(std::move(allocation));
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto lifetimes = std::make_shared<std::vector<std::shared_ptr<HandleUse<Kernel>>>>();
  for (const auto &launch : launches)
    lifetimes->push_back(launch.lifetime);
  return append(
      list,
      [launches = std::move(launches), count, groups, maxCount, waits = retainedWaits, allocationLifetime] {
        auto result = waitDependencies(waits);
        if (result != ZE_RESULT_SUCCESS) return result;
        for (uint32_t index = 0; index < std::min(*count, maxCount); ++index) {
          result = launchRpc(launches[index], groups[index]);
          if (result != ZE_RESULT_SUCCESS) return result;
        }
        return ZE_RESULT_SUCCESS;
      },
      [allocationLifetime, lifetimes] {
        allocationLifetime->clear();
        for (auto &lifetime : *lifetimes)
          *lifetime = {};
      },
      retainedSignal, {}, true);
}

// UMF uses the reported allocation page size as its minimum slab alignment.
// Keep the allocator and zeMemGetAllocProperties behaviour in agreement.
void *alignedAllocate(size_t size, size_t alignment) {
  const size_t actualAlignment = std::max(alignment, PageSize);
  void *value = nullptr;
  return posix_memalign(&value, actualAlignment, std::max(size, size_t{1})) == 0 ? value : nullptr;
}

size_t granuleOf(ze_memory_type_t type) { return type == ZE_MEMORY_TYPE_HOST ? PageSize : DeviceAllocationGranularity; }

// compute-runtime rounds a non-power-of-two alignment up to the allocation granularity instead of rejecting it.
size_t granularityAlignment(size_t alignment, ze_memory_type_t type) {
  if (alignment == 0 || (alignment & (alignment - 1)) == 0) return alignment;
  size_t rounded = granuleOf(type);
  while (rounded < alignment)
    rounded <<= 1;
  return rounded;
}

// granule-sized backing keeps in-granule overruns off the host heap
size_t backingSize(size_t size, ze_memory_type_t type) {
  const size_t granule = granuleOf(type);
  return size > std::numeric_limits<size_t>::max() - granule ? size : (size + granule - 1) / granule * granule;
}

std::vector<AllocationRef> deferredFrees;

ze_result_t releaseAllocation(const AllocationRef &allocation) {
  std::scoped_lock const rpcLock(rpcTransactionMutex);
  const bool released = remote.mode() != ClientMode::Ready || !allocation->id
                        || static_cast<bool>(remote.client()->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(*allocation->id)}}));
  if (!released) recordRemoteError(remote.error());
  std::free(allocation->base);
  return released ? ZE_RESULT_SUCCESS : ZE_RESULT_ERROR_DEVICE_LOST;
}

void reapDeferredFrees() {
  std::vector<AllocationRef> unused;
  {
    std::scoped_lock const lock(allocationsMutex);
    const auto idle = std::stable_partition(deferredFrees.begin(), deferredFrees.end(),
                                            [](const AllocationRef &entry) { return entry.use_count() != 1; });
    unused.assign(std::make_move_iterator(idle), std::make_move_iterator(deferredFrees.end()));
    deferredFrees.erase(idle, deferredFrees.end());
  }
  for (const auto &allocation : unused)
    (void)releaseAllocation(allocation);
}

ze_result_t allocate(Context *context, Device *device, size_t size, size_t alignment, ze_memory_type_t type, void **result) {
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  reapDeferredFrees();
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (remote.mode() == ClientMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (remote.mode() == ClientMode::Ready && size > MaxServerAllocationSize) return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  const size_t backing = backingSize(std::max(size, size_t{1}), type);
  *result = alignedAllocate(backing, granularityAlignment(alignment, type));
  if (!*result) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  // Device allocations are uninitialised by the Level Zero specification, but
  // deterministic zero-fill keeps empty-range kernels observable and mirrors
  // the fresh allocation behaviour of the physical oracle used by the suite.
  std::memset(*result, 0, backing);
  std::optional<uint64_t> allocationId;
  if (remote.mode() == ClientMode::Ready) {
    std::scoped_lock const rpcLock(rpcTransactionMutex);
    const auto registered = registerAllocation(*remote.client(), size, *result, true);
    if (!registered) {
      recordRemoteError(remote.error());
      std::free(*result);
      *result = nullptr;
      return reportStatus(device, ZE_RESULT_ERROR_DEVICE_LOST);
    }
    allocationId = static_cast<uint64_t>(registered->value);
  }
  const auto ordinal = deviceOrdinal(device);
  std::scoped_lock const lock(allocationsMutex);
  allocations.emplace(
      *result,
      std::make_shared<Allocation>(Allocation{
          .context = HandleUse<Context>(context), .base = *result, .size = size, .type = type, .id = allocationId, .device = ordinal}));
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL memShared(ze_context_handle_t context, const ze_device_mem_alloc_desc_t *, const ze_host_mem_alloc_desc_t *,
                                 size_t size, size_t alignment, ze_device_handle_t device, void **result) {
  return allocate(cast<Context>(context), cast<Device>(device), size, alignment, ZE_MEMORY_TYPE_SHARED, result);
}
ze_result_t ZE_APICALL memDevice(ze_context_handle_t context, const ze_device_mem_alloc_desc_t *, size_t size, size_t alignment,
                                 ze_device_handle_t device, void **result) {
  return allocate(cast<Context>(context), cast<Device>(device), size, alignment, ZE_MEMORY_TYPE_DEVICE, result);
}
ze_result_t ZE_APICALL memHost(ze_context_handle_t context, const ze_host_mem_alloc_desc_t *, size_t size, size_t alignment,
                               void **result) {
  return allocate(cast<Context>(context), nullptr, size, alignment, ZE_MEMORY_TYPE_HOST, result);
}
ze_result_t ZE_APICALL memFree(ze_context_handle_t contextHandle, void *pointer) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_SUCCESS;
  {
    std::scoped_lock const lock(virtualMemoryMutex);
    if (virtualReservations.contains(reinterpret_cast<uintptr_t>(pointer))) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  }
  reapDeferredFrees();
  AllocationRef allocation;
  {
    std::scoped_lock const lock(allocationsMutex);
    const auto found = allocations.find(pointer);
    if (found == allocations.end()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    if (found->second->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    if (found->second.use_count() != 1) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
    allocation = found->second;
    allocations.erase(found);
  }
  return releaseAllocation(allocation);
}
ze_result_t ZE_APICALL memFreeExt(ze_context_handle_t contextHandle, const ze_memory_free_ext_desc_t *desc, void *pointer) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  constexpr auto supportedPolicies = ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_BLOCKING_FREE | ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_DEFER_FREE;
  if ((desc->freePolicy & ~supportedPolicies) != 0) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if ((desc->freePolicy & ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_DEFER_FREE) == 0) return memFree(contextHandle, pointer);
  {
    std::scoped_lock const lock(virtualMemoryMutex);
    if (virtualReservations.contains(reinterpret_cast<uintptr_t>(pointer))) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  }
  reapDeferredFrees();
  AllocationRef allocation;
  {
    std::scoped_lock const lock(allocationsMutex);
    const auto found = allocations.find(pointer);
    if (found == allocations.end() || found->second->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    allocation = std::move(found->second);
    allocations.erase(found);
    if (allocation.use_count() != 1) {
      deferredFrees.push_back(std::move(allocation));
      return ZE_RESULT_SUCCESS;
    }
  }
  return releaseAllocation(allocation);
}

struct ImageFormatRow {
  uint32_t channels;
  uint32_t channelBytes;
  ze_image_format_type_t type;
  uint32_t code;
};

constexpr ImageFormatRow imageFormatRow(uint32_t channels, uint32_t channelBytes, ze_image_format_type_t type, uint32_t code) {
  return {.channels = channels, .channelBytes = channelBytes, .type = type, .code = code};
}

constexpr auto ImageFormatRows = std::to_array<ImageFormatRow>({
    imageFormatRow(1, 1, ZE_IMAGE_FORMAT_TYPE_UNORM, 15), imageFormatRow(1, 2, ZE_IMAGE_FORMAT_TYPE_UNORM, 14),
    imageFormatRow(1, 1, ZE_IMAGE_FORMAT_TYPE_SNORM, 20), imageFormatRow(1, 2, ZE_IMAGE_FORMAT_TYPE_SNORM, 19),
    imageFormatRow(1, 1, ZE_IMAGE_FORMAT_TYPE_SINT, 29),  imageFormatRow(1, 2, ZE_IMAGE_FORMAT_TYPE_SINT, 28),
    imageFormatRow(1, 4, ZE_IMAGE_FORMAT_TYPE_SINT, 24),  imageFormatRow(1, 1, ZE_IMAGE_FORMAT_TYPE_UINT, 39),
    imageFormatRow(1, 2, ZE_IMAGE_FORMAT_TYPE_UINT, 38),  imageFormatRow(1, 4, ZE_IMAGE_FORMAT_TYPE_UINT, 33),
    imageFormatRow(1, 2, ZE_IMAGE_FORMAT_TYPE_FLOAT, 9),  imageFormatRow(1, 4, ZE_IMAGE_FORMAT_TYPE_FLOAT, 3),
    imageFormatRow(2, 1, ZE_IMAGE_FORMAT_TYPE_UNORM, 13), imageFormatRow(2, 2, ZE_IMAGE_FORMAT_TYPE_UNORM, 12),
    imageFormatRow(2, 1, ZE_IMAGE_FORMAT_TYPE_SNORM, 18), imageFormatRow(2, 2, ZE_IMAGE_FORMAT_TYPE_SNORM, 17),
    imageFormatRow(2, 1, ZE_IMAGE_FORMAT_TYPE_SINT, 27),  imageFormatRow(2, 2, ZE_IMAGE_FORMAT_TYPE_SINT, 26),
    imageFormatRow(2, 4, ZE_IMAGE_FORMAT_TYPE_SINT, 25),  imageFormatRow(2, 1, ZE_IMAGE_FORMAT_TYPE_UINT, 37),
    imageFormatRow(2, 2, ZE_IMAGE_FORMAT_TYPE_UINT, 36),  imageFormatRow(2, 4, ZE_IMAGE_FORMAT_TYPE_UINT, 35),
    imageFormatRow(2, 2, ZE_IMAGE_FORMAT_TYPE_FLOAT, 7),  imageFormatRow(2, 4, ZE_IMAGE_FORMAT_TYPE_FLOAT, 6),
    imageFormatRow(4, 1, ZE_IMAGE_FORMAT_TYPE_UNORM, 4),  imageFormatRow(4, 2, ZE_IMAGE_FORMAT_TYPE_UNORM, 10),
    imageFormatRow(4, 1, ZE_IMAGE_FORMAT_TYPE_SNORM, 5),  imageFormatRow(4, 2, ZE_IMAGE_FORMAT_TYPE_SNORM, 16),
    imageFormatRow(4, 1, ZE_IMAGE_FORMAT_TYPE_SINT, 23),  imageFormatRow(4, 2, ZE_IMAGE_FORMAT_TYPE_SINT, 22),
    imageFormatRow(4, 4, ZE_IMAGE_FORMAT_TYPE_SINT, 21),  imageFormatRow(4, 1, ZE_IMAGE_FORMAT_TYPE_UINT, 32),
    imageFormatRow(4, 2, ZE_IMAGE_FORMAT_TYPE_UINT, 31),  imageFormatRow(4, 4, ZE_IMAGE_FORMAT_TYPE_UINT, 30),
    imageFormatRow(4, 2, ZE_IMAGE_FORMAT_TYPE_FLOAT, 2),  imageFormatRow(4, 4, ZE_IMAGE_FORMAT_TYPE_FLOAT, 1),
});

struct ImageLayoutShape {
  ze_image_format_layout_t layout;
  uint32_t channels;
  uint32_t channelBytes;
};

constexpr ImageLayoutShape imageLayoutShape(ze_image_format_layout_t layout, uint32_t channels, uint32_t channelBytes) {
  return {.layout = layout, .channels = channels, .channelBytes = channelBytes};
}

constexpr auto ImageLayoutShapes = std::to_array<ImageLayoutShape>({
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_8, 1, 1),
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_16, 1, 2),
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_32, 1, 4),
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_8_8, 2, 1),
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_16_16, 2, 2),
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_32_32, 2, 4),
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_8_8_8_8, 4, 1),
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_16_16_16_16, 4, 2),
    imageLayoutShape(ZE_IMAGE_FORMAT_LAYOUT_32_32_32_32, 4, 4),
});

struct ImageFormat {
  uint32_t code;
  uint32_t pixelBytes;
};

ze_result_t resolveImageFormat(const ze_image_format_t &format, ImageFormat &resolved) {
  if (static_cast<uint32_t>(format.layout) > ZE_IMAGE_FORMAT_LAYOUT_32_32_32
      || static_cast<uint32_t>(format.type) > ZE_IMAGE_FORMAT_TYPE_FLOAT)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  const auto shape = ImageLayoutShapes ^ find([&](const ImageLayoutShape &entry) { return entry.layout == format.layout; });
  if (!shape) return ZE_RESULT_ERROR_UNSUPPORTED_IMAGE_FORMAT;
  const auto row = ImageFormatRows ^ find([&](const ImageFormatRow &entry) {
                     return entry.channels == shape->channels && entry.channelBytes == shape->channelBytes && entry.type == format.type;
                   });
  if (!row) return ZE_RESULT_ERROR_UNSUPPORTED_IMAGE_FORMAT;
  const std::array<ze_image_format_swizzle_t, 4> swizzles{format.x, format.y, format.z, format.w};
  constexpr std::array<ze_image_format_swizzle_t, 4> identity{ZE_IMAGE_FORMAT_SWIZZLE_R, ZE_IMAGE_FORMAT_SWIZZLE_G,
                                                              ZE_IMAGE_FORMAT_SWIZZLE_B, ZE_IMAGE_FORMAT_SWIZZLE_A};
  for (uint32_t channel = 0; channel < shape->channels; ++channel)
    if (swizzles[channel] != identity[channel] && swizzles[channel] != ZE_IMAGE_FORMAT_SWIZZLE_X)
      return ZE_RESULT_ERROR_UNSUPPORTED_IMAGE_FORMAT;
  resolved = {.code = row->code, .pixelBytes = shape->channels * shape->channelBytes};
  return ZE_RESULT_SUCCESS;
}

struct ImageGeometry {
  uint32_t width;
  uint32_t height;
  uint32_t slices;
  ImageFormat format;
};

// the layers of a 1D array are stored as extra rows, which keeps one copy path for every image type
ze_result_t resolveImage(const ze_image_desc_t &desc, ImageGeometry &geometry) {
  if (const auto result = resolveImageFormat(desc.format, geometry.format); result != ZE_RESULT_SUCCESS) return result;
  const auto fits = [](uint64_t value, uint32_t limit) { return value >= 1 && value <= limit; };
  const auto within = [](bool valid) { return valid ? ZE_RESULT_SUCCESS : ZE_RESULT_ERROR_UNSUPPORTED_SIZE; };
  geometry.width = static_cast<uint32_t>(std::min<uint64_t>(desc.width, std::numeric_limits<uint32_t>::max()));
  switch (desc.type) {
    case ZE_IMAGE_TYPE_1D: geometry.height = geometry.slices = 1; return within(fits(desc.width, MaxImageDims));
    case ZE_IMAGE_TYPE_1DARRAY:
      geometry.height = desc.arraylevels;
      geometry.slices = 1;
      return within(fits(desc.width, MaxImageDims) && fits(desc.arraylevels, MaxImageArraySlices));
    case ZE_IMAGE_TYPE_2D:
      geometry.height = desc.height;
      geometry.slices = 1;
      return within(fits(desc.width, MaxImageDims) && fits(desc.height, MaxImageDims));
    case ZE_IMAGE_TYPE_2DARRAY:
      geometry.height = desc.height;
      geometry.slices = desc.arraylevels;
      return within(fits(desc.width, MaxImageDims) && fits(desc.height, MaxImageDims) && fits(desc.arraylevels, MaxImageArraySlices));
    case ZE_IMAGE_TYPE_3D:
      geometry.height = desc.height;
      geometry.slices = desc.depth;
      return within(fits(desc.width, MaxImageDims3D) && fits(desc.height, MaxImageDims3D) && fits(desc.depth, MaxImageDims3D));
    case ZE_IMAGE_TYPE_BUFFER: return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
    default: return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  }
}

ze_result_t ZE_APICALL imageProperties(ze_device_handle_t deviceHandle, const ze_image_desc_t *desc, ze_image_properties_t *properties) {
  if (!cast<Device>(deviceHandle)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  ImageGeometry geometry{};
  if (const auto result = resolveImage(*desc, geometry); result != ZE_RESULT_SUCCESS) return result;
  properties->samplerFilterFlags = ZE_IMAGE_SAMPLER_FILTER_FLAG_POINT | ZE_IMAGE_SAMPLER_FILTER_FLAG_LINEAR;
  return ZE_RESULT_SUCCESS;
}

void releaseDriverAllocation(void *pointer) {
  AllocationRef allocation;
  {
    std::scoped_lock const lock(allocationsMutex);
    const auto found = allocations.find(pointer);
    if (found == allocations.end()) return;
    allocation = found->second;
    allocations.erase(found);
  }
  std::scoped_lock const rpcLock(rpcTransactionMutex);
  if (remote.mode() == ClientMode::Ready && allocation->id
      && !remote.client()->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(*allocation->id)}}))
    recordRemoteError(remote.error());
  std::free(pointer);
}

void putWord(void *record, size_t offset, uint64_t value, size_t bytes) {
  std::memcpy(static_cast<uint8_t *>(record) + offset, &value, bytes);
}

ze_result_t ZE_APICALL imageCreate(ze_context_handle_t contextHandle, ze_device_handle_t deviceHandle, const ze_image_desc_t *desc,
                                   ze_image_handle_t *result) {
  auto *context = cast<Context>(contextHandle);
  auto *device = cast<Device>(deviceHandle);
  if (!context || !device) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  ImageGeometry geometry{};
  if (const auto status = resolveImage(*desc, geometry); status != ZE_RESULT_SUCCESS) return status;
  auto *image = createHandle<Image>(context, device, *desc);
  if (!image) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  image->desc.pNext = nullptr;
  image->pixelBytes = geometry.format.pixelBytes;
  image->width = geometry.width;
  image->height = geometry.height;
  image->slices = geometry.slices;
  image->rowBytes = static_cast<size_t>(geometry.width) * geometry.format.pixelBytes;
  image->sliceBytes = image->rowBytes * geometry.height;
  const size_t bytes = image->sliceBytes * geometry.slices;
  auto status = allocate(context, device, bytes, 0, ZE_MEMORY_TYPE_DEVICE, &image->texels);
  if (status == ZE_RESULT_SUCCESS) status = allocate(context, device, ImageRecordBytes, 0, ZE_MEMORY_TYPE_DEVICE, &image->record);
  if (status != ZE_RESULT_SUCCESS) {
    releaseDriverAllocation(image->texels);
    delete image;
    return status == ZE_RESULT_ERROR_UNSUPPORTED_SIZE ? ZE_RESULT_ERROR_OUT_OF_DEVICE_MEMORY : status;
  }
  const bool array = desc->type == ZE_IMAGE_TYPE_1DARRAY;
  const uint32_t recordHeight = array ? 1U : geometry.height;
  const uint32_t recordSlices = array ? geometry.height : geometry.slices;
  const uint32_t format =
      geometry.format.code | descriptor::Flags::LevelTable | (desc->type == ZE_IMAGE_TYPE_3D ? descriptor::Flags::Volume : 0U);
  auto *record = image->record;
  putWord(record, descriptor::Layout::Width, geometry.width, 4);
  putWord(record, descriptor::Layout::Height, recordHeight, 4);
  putWord(record, descriptor::Layout::Slices, recordSlices, 4);
  putWord(record, descriptor::Layout::Format, format, 4);
  putWord(record, descriptor::Layout::Texels, reinterpret_cast<uintptr_t>(image->texels), 8);
  putWord(record, descriptor::Layout::TexelBytes, bytes, 8);
  putWord(record, descriptor::Layout::Levels, 1, 4);
  putWord(record, descriptor::Layout::LevelTable + descriptor::Layout::LevelWidth, geometry.width, 4);
  putWord(record, descriptor::Layout::LevelTable + descriptor::Layout::LevelHeight, recordHeight, 4);
  *result = reinterpret_cast<ze_image_handle_t>(image);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL imageDestroy(ze_image_handle_t handle) {
  auto *image = cast<Image>(handle);
  if (!image) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  void *const texels = image->texels;
  void *const record = image->record;
  const auto result = destroyHandle(image);
  if (result == ZE_RESULT_SUCCESS) {
    releaseDriverAllocation(texels);
    releaseDriverAllocation(record);
  }
  return result;
}

uint32_t samplerAddress(ze_sampler_address_mode_t mode) {
  switch (mode) {
    case ZE_SAMPLER_ADDRESS_MODE_REPEAT: return descriptor::AddressMode::Repeat;
    case ZE_SAMPLER_ADDRESS_MODE_MIRROR: return descriptor::AddressMode::MirroredRepeat;
    case ZE_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER: return descriptor::AddressMode::ClampToBorder;
    default: return descriptor::AddressMode::ClampToEdge;
  }
}

ze_result_t ZE_APICALL samplerCreate(ze_context_handle_t contextHandle, ze_device_handle_t deviceHandle, const ze_sampler_desc_t *desc,
                                     ze_sampler_handle_t *result) {
  auto *context = cast<Context>(contextHandle);
  auto *device = cast<Device>(deviceHandle);
  if (!context || !device) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (static_cast<uint32_t>(desc->addressMode) > ZE_SAMPLER_ADDRESS_MODE_MIRROR
      || static_cast<uint32_t>(desc->filterMode) > ZE_SAMPLER_FILTER_MODE_LINEAR)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  auto *sampler = createHandle<Sampler>(context, device);
  if (!sampler) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  if (const auto status = allocate(context, device, SamplerRecordBytes, 0, ZE_MEMORY_TYPE_DEVICE, &sampler->record);
      status != ZE_RESULT_SUCCESS) {
    delete sampler;
    return status;
  }
  std::array<uint32_t, descriptor::SamplerWord::Count> words{};
  const uint32_t filtering = desc->filterMode == ZE_SAMPLER_FILTER_MODE_LINEAR ? descriptor::Filter::Linear : descriptor::Filter::Nearest;
  const float maxLod = 1000.0F;
  words[descriptor::SamplerWord::MinFilter] = filtering;
  words[descriptor::SamplerWord::MagFilter] = filtering;
  words[descriptor::SamplerWord::AddressU] = samplerAddress(desc->addressMode);
  words[descriptor::SamplerWord::AddressV] = samplerAddress(desc->addressMode);
  words[descriptor::SamplerWord::AddressW] = samplerAddress(desc->addressMode);
  words[descriptor::SamplerWord::Unnormalized] = desc->isNormalized ? 0U : 1U;
  words[descriptor::SamplerWord::BorderColour] = descriptor::BorderColour::FloatTransparentBlack;
  std::memcpy(&words[descriptor::SamplerWord::MaxLod], &maxLod, sizeof(maxLod));
  std::memcpy(sampler->record, words.data(), SamplerRecordBytes);
  *result = reinterpret_cast<ze_sampler_handle_t>(sampler);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL samplerDestroy(ze_sampler_handle_t handle) {
  auto *sampler = cast<Sampler>(handle);
  if (!sampler) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  void *const record = sampler->record;
  const auto result = destroyHandle(sampler);
  if (result == ZE_RESULT_SUCCESS) releaseDriverAllocation(record);
  return result;
}

struct ImageBox {
  uint32_t x = 0;
  uint32_t y = 0;
  uint32_t z = 0;
  uint32_t width = 0;
  uint32_t height = 0;
  uint32_t depth = 0;

  [[nodiscard]] bool empty() const { return width == 0 || height == 0 || depth == 0; }
};

// an all-zero region copies nothing and a missing one covers the whole image
ze_result_t resolveBox(const Image &image, const ze_image_region_t *region, ImageBox &box) {
  if (!region) {
    box = {.x = 0, .y = 0, .z = 0, .width = image.width, .height = image.height, .depth = image.slices};
    return ZE_RESULT_SUCCESS;
  }
  box = {.x = region->originX,
         .y = region->originY,
         .z = region->originZ,
         .width = region->width,
         .height = region->height,
         .depth = std::max(region->depth, 1U)};
  if (image.slices == 1) {
    box.z = 0;
    box.depth = 1;
  }
  if (box.empty()) return ZE_RESULT_SUCCESS;
  const auto inside = [](uint32_t origin, uint32_t extent, uint32_t limit) { return uint64_t{origin} + extent <= limit; };
  return inside(box.x, box.width, image.width) && inside(box.y, box.height, image.height) && inside(box.z, box.depth, image.slices)
             ? ZE_RESULT_SUCCESS
             : ZE_RESULT_ERROR_INVALID_ARGUMENT;
}

template <class Byte> struct StridedBuffer {
  Byte *base;
  size_t rowPitch;
  size_t slicePitch;
};

void copyBox(const StridedBuffer<uint8_t> &destination, const StridedBuffer<const uint8_t> &source, const ImageBox &box, size_t rowBytes) {
  for (uint32_t z = 0; z < box.depth; ++z)
    for (uint32_t y = 0; y < box.height; ++y)
      std::memmove(destination.base + (z * destination.slicePitch) + (y * destination.rowPitch),
                   source.base + (z * source.slicePitch) + (y * source.rowPitch), rowBytes);
}

StridedBuffer<uint8_t> imageBuffer(const Image &image, const ImageBox &box) {
  const size_t offset = (box.z * image.sliceBytes) + (box.y * image.rowBytes) + (static_cast<size_t>(box.x) * image.pixelBytes);
  return {.base = static_cast<uint8_t *>(image.texels) + offset, .rowPitch = image.rowBytes, .slicePitch = image.sliceBytes};
}

template <class Byte>
StridedBuffer<Byte> hostBuffer(Byte *pointer, const ImageBox &box, size_t pixelBytes, uint32_t rowPitch, uint32_t slicePitch) {
  const size_t row = rowPitch == 0 ? box.width * pixelBytes : rowPitch;
  return {.base = pointer, .rowPitch = row, .slicePitch = slicePitch == 0 ? row * box.height : slicePitch};
}

struct ImageCopyLifetime {
  std::vector<HandleUse<Image>> images;
  std::vector<AllocationRef> allocations;
};

template <class Memory> struct ImageCopySide {
  ze_image_handle_t image = nullptr;
  Memory *memory = nullptr;
  const ze_image_region_t *region = nullptr;
  uint32_t rowPitch = 0;
  uint32_t slicePitch = 0;
};

ze_result_t appendImageCopy(ze_command_list_handle_t listHandle, const ImageCopySide<void> &destination,
                            const ImageCopySide<const void> &source, ze_event_handle_t signal, uint32_t waitCount,
                            ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(listHandle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  auto *target = destination.image ? cast<Image>(destination.image) : nullptr;
  auto *origin = source.image ? cast<Image>(source.image) : nullptr;
  if ((destination.image && !target) || (source.image && !origin)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if ((!target && !destination.memory) || (!origin && !source.memory)) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  ImageBox destinationBox;
  ImageBox sourceBox;
  if (target)
    if (const auto status = resolveBox(*target, destination.region, destinationBox); status != ZE_RESULT_SUCCESS) return status;
  if (origin)
    if (const auto status = resolveBox(*origin, source.region, sourceBox); status != ZE_RESULT_SUCCESS) return status;
  if (!target) destinationBox = sourceBox;
  if (!origin) sourceBox = destinationBox;
  if (target && origin
      && (destinationBox.width != sourceBox.width || destinationBox.height != sourceBox.height || destinationBox.depth != sourceBox.depth
          || target->pixelBytes != origin->pixelBytes))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  CommandSignal retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  const size_t pixelBytes = target ? target->pixelBytes : origin->pixelBytes;
  const size_t rowBytes = static_cast<size_t>(destinationBox.width) * pixelBytes;
  const bool empty = destinationBox.empty();
  // a recorded list does not own its images or memory, so they may be released before the list runs or is destroyed
  auto lifetime = std::make_shared<ImageCopyLifetime>();
  if (list->immediate) {
    if (target) lifetime->images.emplace_back(target);
    if (origin) lifetime->images.emplace_back(origin);
    if (!empty && !target)
      if (auto allocation = retainAllocation(destination.memory, 1)) lifetime->allocations.push_back(std::move(allocation));
    if (!empty && !origin)
      if (auto allocation = retainAllocation(source.memory, 1)) lifetime->allocations.push_back(std::move(allocation));
  }
  return append(
      list,
      [=, waits = retainedWaits] {
        const auto result = waitDependencies(waits);
        if (result != ZE_RESULT_SUCCESS || empty) return result;
        auto *to = destination.image ? cast<Image>(destination.image) : nullptr;
        auto *from = source.image ? cast<Image>(source.image) : nullptr;
        if ((destination.image && !to) || (source.image && !from)) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
        const auto destinationBuffer = to ? imageBuffer(*to, destinationBox)
                                          : hostBuffer(static_cast<uint8_t *>(destination.memory), destinationBox, pixelBytes,
                                                       destination.rowPitch, destination.slicePitch);
        const auto sourceBuffer = [&]() -> StridedBuffer<const uint8_t> {
          if (!from)
            return hostBuffer(static_cast<const uint8_t *>(source.memory), sourceBox, pixelBytes, source.rowPitch, source.slicePitch);
          const auto image = imageBuffer(*from, sourceBox);
          return {.base = image.base, .rowPitch = image.rowPitch, .slicePitch = image.slicePitch};
        }();
        copyBox(destinationBuffer, sourceBuffer, destinationBox, rowBytes);
        return result;
      },
      [lifetime] {
        lifetime->images.clear();
        lifetime->allocations.clear();
      },
      retainedSignal);
}

ze_result_t ZE_APICALL listImageCopy(ze_command_list_handle_t list, ze_image_handle_t destination, ze_image_handle_t source,
                                     ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  if (!destination || !source) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return appendImageCopy(list, {.image = destination}, {.image = source}, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listImageCopyRegion(ze_command_list_handle_t list, ze_image_handle_t destination, ze_image_handle_t source,
                                           const ze_image_region_t *destinationRegion, const ze_image_region_t *sourceRegion,
                                           ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  if (!destination || !source) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return appendImageCopy(list, {.image = destination, .region = destinationRegion}, {.image = source, .region = sourceRegion}, signal,
                         waitCount, waits);
}

ze_result_t ZE_APICALL listImageCopyToMemoryExt(ze_command_list_handle_t list, void *destination, ze_image_handle_t source,
                                                const ze_image_region_t *sourceRegion, uint32_t rowPitch, uint32_t slicePitch,
                                                ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  if (!source) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return appendImageCopy(list, {.memory = destination, .rowPitch = rowPitch, .slicePitch = slicePitch},
                         {.image = source, .region = sourceRegion}, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listImageCopyFromMemoryExt(ze_command_list_handle_t list, ze_image_handle_t destination, const void *source,
                                                  const ze_image_region_t *destinationRegion, uint32_t rowPitch, uint32_t slicePitch,
                                                  ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  if (!destination) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return appendImageCopy(list, {.image = destination, .region = destinationRegion},
                         {.memory = source, .rowPitch = rowPitch, .slicePitch = slicePitch}, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listImageCopyToMemory(ze_command_list_handle_t list, void *destination, ze_image_handle_t source,
                                             const ze_image_region_t *sourceRegion, ze_event_handle_t signal, uint32_t waitCount,
                                             ze_event_handle_t *waits) {
  return listImageCopyToMemoryExt(list, destination, source, sourceRegion, 0, 0, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listImageCopyFromMemory(ze_command_list_handle_t list, ze_image_handle_t destination, const void *source,
                                               const ze_image_region_t *destinationRegion, ze_event_handle_t signal, uint32_t waitCount,
                                               ze_event_handle_t *waits) {
  return listImageCopyFromMemoryExt(list, destination, source, destinationRegion, 0, 0, signal, waitCount, waits);
}

ze_result_t ZE_APICALL virtualQueryPageSize(ze_context_handle_t context, ze_device_handle_t device, size_t, size_t *pageSize) {
  if (!cast<Context>(context) || !cast<Device>(device)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pageSize) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *pageSize = PageSize;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualReserve(ze_context_handle_t contextHandle, const void *start, size_t size, void **result) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (size == 0 || size % PageSize != 0) return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  if (start && reinterpret_cast<uintptr_t>(start) % PageSize != 0) return ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT;
  if (remote.mode() == ClientMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  void *address = MAP_FAILED;
  if (start)
    address = mmap(gemstone::apiAddress(start), size, PROT_NONE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_NORESERVE | MAP_FIXED_NOREPLACE, -1, 0);
  if (address == MAP_FAILED) address = mmap(nullptr, size, PROT_NONE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_NORESERVE, -1, 0);
  if (address == MAP_FAILED) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  std::optional<uint64_t> allocationId;
  if (remote.mode() == ClientMode::Ready && size <= MaxServerAllocationSize) {
    std::scoped_lock const rpcLock(rpcTransactionMutex);
    const auto registered = registerAllocation(*remote.client(), size, address);
    if (!registered) {
      recordRemoteError(remote.error());
      munmap(address, size);
      return reportStatus(nullptr, ZE_RESULT_ERROR_DEVICE_LOST);
    }
    allocationId = static_cast<uint64_t>(registered->value);
  }
  {
    std::scoped_lock const allocationLock(allocationsMutex);
    allocations.emplace(address, std::make_shared<Allocation>(Allocation{.context = HandleUse<Context>(context),
                                                                         .base = address,
                                                                         .size = size,
                                                                         .type = ZE_MEMORY_TYPE_DEVICE,
                                                                         .id = allocationId,
                                                                         .device = 0}));
  }
  {
    std::scoped_lock const lock(virtualMemoryMutex);
    virtualReservations.emplace(reinterpret_cast<uintptr_t>(address),
                                VirtualReservation{.context = HandleUse<Context>(context), .size = size, .mappings = {}});
  }
  *result = address;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualFree(ze_context_handle_t contextHandle, const void *pointer, size_t size) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::scoped_lock const lock(virtualMemoryMutex);
  const auto found = virtualReservations.find(reinterpret_cast<uintptr_t>(pointer));
  if (found == virtualReservations.end() || found->second.context.get() != context || found->second.size != size)
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (!found->second.mappings.empty()) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  std::optional<uint64_t> allocationId;
  {
    std::scoped_lock const allocationLock(allocationsMutex);
    auto allocation = allocations.find(pointer);
    if (allocation == allocations.end() || allocation->second.use_count() != 1) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
    allocationId = allocation->second->id;
  }
  if (remote.mode() == ClientMode::Ready && allocationId) {
    std::scoped_lock const rpcLock(rpcTransactionMutex);
    if (!remote.client()->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(*allocationId)}})) {
      recordRemoteError(remote.error());
      return reportStatus(nullptr, ZE_RESULT_ERROR_DEVICE_LOST);
    }
  }
  if (munmap(gemstone::apiAddress(pointer), size) != 0) return ZE_RESULT_ERROR_UNKNOWN;
  {
    std::scoped_lock const allocationLock(allocationsMutex);
    if (const auto allocation = allocations.find(pointer); allocation != allocations.end()) allocations.erase(allocation);
  }
  virtualReservations.erase(found);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL physicalCreate(ze_context_handle_t contextHandle, ze_device_handle_t deviceHandle, ze_physical_mem_desc_t *desc,
                                      ze_physical_mem_handle_t *result) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!(desc->flags & ZE_PHYSICAL_MEM_FLAG_ALLOCATE_ON_HOST) && !cast<Device>(deviceHandle)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if ((desc->flags & ~(ZE_PHYSICAL_MEM_FLAG_ALLOCATE_ON_HOST | ZE_PHYSICAL_MEM_FLAG_ALLOCATE_ON_DEVICE)) != 0
      || (desc->flags & ZE_PHYSICAL_MEM_FLAG_ALLOCATE_ON_HOST && desc->flags & ZE_PHYSICAL_MEM_FLAG_ALLOCATE_ON_DEVICE))
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if (desc->size == 0 || desc->size % PageSize != 0) return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  const int fd = static_cast<int>(syscall(SYS_memfd_create, "gemstone-levelzero-physical", 0x0001U));
  if (fd < 0) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  if (ftruncate(fd, static_cast<off_t>(desc->size)) != 0) {
    close(fd);
    return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  }
  auto *created = createHandle<PhysicalMemory>(context, fd, desc->size, desc->flags);
  if (!created) {
    close(fd);
    return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  }
  *result = reinterpret_cast<ze_physical_mem_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL physicalDestroy(ze_context_handle_t contextHandle, ze_physical_mem_handle_t handle) {
  auto *context = cast<Context>(contextHandle);
  auto *physical = cast<PhysicalMemory>(handle);
  if (!context || !physical) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (physical->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return destroyHandle(physical);
}

ze_result_t ZE_APICALL physicalProperties(ze_context_handle_t contextHandle, ze_physical_mem_handle_t handle,
                                          ze_physical_mem_properties_t *properties) {
  auto *context = cast<Context>(contextHandle);
  auto *physical = cast<PhysicalMemory>(handle);
  if (!context || !physical) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (physical->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  properties->id = reinterpret_cast<uintptr_t>(physical);
  properties->size = physical->size;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualMap(ze_context_handle_t contextHandle, const void *pointer, size_t size,
                                  ze_physical_mem_handle_t physicalHandle, size_t offset, ze_memory_access_attribute_t access) {
  auto *context = cast<Context>(contextHandle);
  auto *physical = cast<PhysicalMemory>(physicalHandle);
  if (!context || !physical) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (physical->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (access > ZE_MEMORY_ACCESS_ATTRIBUTE_READONLY) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if (reinterpret_cast<uintptr_t>(pointer) % PageSize != 0 || offset % PageSize != 0) return ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT;
  if (size == 0 || size % PageSize != 0 || offset > physical->size || size > physical->size - offset)
    return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  if (size > std::numeric_limits<uintptr_t>::max() - address) return ZE_RESULT_ERROR_INVALID_SIZE;
  std::scoped_lock const lock(virtualMemoryMutex);
  const auto reservation = reservationContaining(address);
  if (reservation == virtualReservations.end()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto &record = reservation->second;
  if (record.context.get() != context || address < reservation->first || size > record.size
      || address - reservation->first > record.size - size)
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (remote.mode() == ClientMode::Ready && record.size > MaxServerAllocationSize) return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  auto next = record.mappings.lower_bound(address);
  if ((next != record.mappings.end() && next->first < address + size)
      || (next != record.mappings.begin() && std::prev(next)->first + std::prev(next)->second.size > address))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (mmap(gemstone::apiAddress(pointer), size, PROT_READ | PROT_WRITE, MAP_SHARED | MAP_FIXED, physical->fd, static_cast<off_t>(offset))
      == MAP_FAILED)
    return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  record.mappings.emplace(address, VirtualMapping{.size = size, .access = access, .physical = HandleUse<PhysicalMemory>(physical)});
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualUnmap(ze_context_handle_t contextHandle, const void *pointer, size_t size) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::scoped_lock const lock(virtualMemoryMutex);
  const auto reservation = reservationContaining(address);
  if (reservation == virtualReservations.end() || reservation->second.context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto mapping = reservation->second.mappings.find(address);
  if (mapping == reservation->second.mappings.end() || mapping->second.size != size) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (mmap(gemstone::apiAddress(pointer), size, PROT_NONE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_NORESERVE | MAP_FIXED, -1, 0) == MAP_FAILED)
    return ZE_RESULT_ERROR_UNKNOWN;
  reservation->second.mappings.erase(mapping);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualSetAccess(ze_context_handle_t contextHandle, const void *pointer, size_t size,
                                        ze_memory_access_attribute_t access) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (access > ZE_MEMORY_ACCESS_ATTRIBUTE_READONLY) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  std::scoped_lock const lock(virtualMemoryMutex);
  const auto reservation = reservationContaining(reinterpret_cast<uintptr_t>(pointer));
  if (reservation == virtualReservations.end() || reservation->second.context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto mapping = reservation->second.mappings.find(reinterpret_cast<uintptr_t>(pointer));
  if (mapping == reservation->second.mappings.end() || mapping->second.size != size) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  mapping->second.access = access;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualGetAccess(ze_context_handle_t contextHandle, const void *pointer, size_t size,
                                        ze_memory_access_attribute_t *access, size_t *outSize) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer || !access || !outSize) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::scoped_lock const lock(virtualMemoryMutex);
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  const auto reservation = reservationContaining(address);
  if (reservation == virtualReservations.end() || reservation->second.context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (address < reservation->first || address - reservation->first >= reservation->second.size
      || size > reservation->second.size - (address - reservation->first))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto mapping = reservation->second.mappings.upper_bound(address);
  if (mapping != reservation->second.mappings.begin()) {
    --mapping;
    if (address >= mapping->first && address - mapping->first < mapping->second.size) {
      *access = mapping->second.access;
      *outSize = mapping->second.size - (address - mapping->first);
      return ZE_RESULT_SUCCESS;
    }
    ++mapping;
  }
  *access = ZE_MEMORY_ACCESS_ATTRIBUTE_NONE;
  *outSize = (mapping == reservation->second.mappings.end() ? reservation->first + reservation->second.size : mapping->first) - address;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL memProperties(ze_context_handle_t contextHandle, const void *pointer, ze_memory_allocation_properties_t *properties,
                                     ze_device_handle_t *device) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  properties->type = ZE_MEMORY_TYPE_UNKNOWN;
  properties->id = 0;
  properties->pageSize = 0;
  if (device) *device = nullptr;
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::scoped_lock const lock(allocationsMutex);
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (address >= start && address - start < allocation->size) {
      properties->type = allocation->type;
      properties->id = allocation->id.value_or(0);
      properties->pageSize = PageSize;
      if (device) *device = reinterpret_cast<ze_device_handle_t>(&globalDevices[allocation->device]);
      return ZE_RESULT_SUCCESS;
    }
  }
  return ZE_RESULT_SUCCESS;
}
std::mutex atomicAttributesMutex;
std::map<uintptr_t, std::pair<size_t, ze_memory_atomic_attr_exp_flags_t>> atomicAttributes;

ze_result_t ZE_APICALL memSetAtomicAccess(ze_context_handle_t contextHandle, ze_device_handle_t deviceHandle, const void *pointer,
                                          size_t size, ze_memory_atomic_attr_exp_flags_t attributes) {
  if (!cast<Context>(contextHandle) || !cast<Device>(deviceHandle)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (size == 0 || size > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(pointer)) return ZE_RESULT_ERROR_INVALID_SIZE;
  constexpr ze_memory_atomic_attr_exp_flags_t known =
      ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_ATOMICS | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_HOST_ATOMICS
      | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_HOST_ATOMICS | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_DEVICE_ATOMICS
      | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_DEVICE_ATOMICS | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_SYSTEM_ATOMICS
      | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_SYSTEM_ATOMICS;
  if ((attributes & ~known) != 0) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  const auto excludes = [&](ze_memory_atomic_attr_exp_flags_t off, ze_memory_atomic_attr_exp_flags_t on) {
    return (attributes & off) != 0 && (attributes & on) != 0;
  };
  if (excludes(ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_ATOMICS, ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_HOST_ATOMICS
                                                              | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_DEVICE_ATOMICS
                                                              | ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_SYSTEM_ATOMICS)
      || excludes(ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_HOST_ATOMICS, ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_HOST_ATOMICS)
      || excludes(ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_DEVICE_ATOMICS, ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_DEVICE_ATOMICS)
      || excludes(ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_NO_SYSTEM_ATOMICS, ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_SYSTEM_ATOMICS))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  ze_device_memory_access_properties_t capabilities{};
  (void)deviceMemoryAccess(deviceHandle, &capabilities);
  const auto allocation = retainAllocation(pointer, 0);
  const bool systemAllocation = !allocation;
  const auto sharedCapabilities =
      systemAllocation ? capabilities.sharedSystemAllocCapabilities : capabilities.sharedSingleDeviceAllocCapabilities;
  if (((attributes & ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_HOST_ATOMICS) != 0
       && (capabilities.hostAllocCapabilities & ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC) == 0)
      || ((attributes & ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_DEVICE_ATOMICS) != 0
          && (capabilities.deviceAllocCapabilities & ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC) == 0)
      || ((attributes & ZE_MEMORY_ATOMIC_ATTR_EXP_FLAG_SYSTEM_ATOMICS) != 0
          && (sharedCapabilities & ZE_MEMORY_ACCESS_CAP_FLAG_CONCURRENT_ATOMIC) == 0))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  std::scoped_lock const lock(atomicAttributesMutex);
  atomicAttributes[reinterpret_cast<uintptr_t>(pointer)] = {size, attributes};
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL memGetAtomicAccess(ze_context_handle_t contextHandle, ze_device_handle_t deviceHandle, const void *pointer,
                                          size_t size, ze_memory_atomic_attr_exp_flags_t *attributes) {
  if (!cast<Context>(contextHandle) || !cast<Device>(deviceHandle)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer || !attributes) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (size == 0 || size > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(pointer)) return ZE_RESULT_ERROR_INVALID_SIZE;
  std::scoped_lock const lock(atomicAttributesMutex);
  const auto found = atomicAttributes.find(reinterpret_cast<uintptr_t>(pointer));
  *attributes = found == atomicAttributes.end() ? 0 : found->second.second;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL memRange(ze_context_handle_t contextHandle, const void *pointer, void **base, size_t *size) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!base && !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::scoped_lock const lock(allocationsMutex);
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  for (const auto &[candidate, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(candidate);
    if (address >= start && address < start + allocation->size) {
      if (base) *base = candidate;
      if (size) *size = allocation->size;
      return ZE_RESULT_SUCCESS;
    }
  }
  return ZE_RESULT_ERROR_INVALID_ARGUMENT;
}

std::atomic<uint64_t> nextIpcEventPoolId{1};
std::mutex ipcEventPoolsMutex;
std::map<uint64_t, EventPool *> ipcEventPools;

bool eventDeviceLost(const Event *event) {
  return event && (event->pool->devices ^ exists([](const Device *device) { return deviceLost(device); }));
}

ze_result_t ZE_APICALL eventPoolCreate(ze_context_handle_t contextHandle, const ze_event_pool_desc_t *desc, uint32_t deviceCount,
                                       ze_device_handle_t *devices, ze_event_pool_handle_t *result) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (deviceCount != 0 && !devices) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *created = createHandle<EventPool>(context, desc->flags);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  if (deviceCount == 0) created->devices = context->devices;
  for (uint32_t index = 0; index < deviceCount; ++index)
    created->devices.push_back(cast<Device>(devices[index]));
  if (desc->flags & ZE_EVENT_POOL_FLAG_IPC) {
    created->ipcId = nextIpcEventPoolId.fetch_add(1, std::memory_order_relaxed);
    std::scoped_lock const lock(ipcEventPoolsMutex);
    ipcEventPools.emplace(created->ipcId, created);
  }
  *result = reinterpret_cast<ze_event_pool_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventPoolDestroy(ze_event_pool_handle_t handle) {
  auto *pool = cast<EventPool>(handle);
  if (!pool) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  // Events created from the pool keep it alive until they are destroyed.
  HandleUse<EventPool> const lifetime(pool);
  if (pool->ipcId) {
    std::scoped_lock const lock(ipcEventPoolsMutex);
    if (auto found = ipcEventPools.find(pool->ipcId); found != ipcEventPools.end() && found->second == pool) ipcEventPools.erase(found);
  }
  pool->destroyRequested.store(true, std::memory_order_release);
  return ZE_RESULT_SUCCESS;
}
struct IpcEventPoolHandle {
  uint64_t process;
  uint64_t pool;
};
ze_result_t ZE_APICALL eventPoolGetIpcHandle(ze_event_pool_handle_t poolHandle, ze_ipc_event_pool_handle_t *handle) {
  auto *pool = cast<EventPool>(poolHandle);
  if (!pool) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!handle) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!pool->ipcId) return ZE_RESULT_ERROR_INVALID_SYNCHRONIZATION_OBJECT;
  const IpcEventPoolHandle encoded{.process = static_cast<uint64_t>(getpid()), .pool = pool->ipcId};
  std::memcpy(handle->data, &encoded, sizeof(encoded));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventPoolOpenIpcHandle(ze_context_handle_t contextHandle, ze_ipc_event_pool_handle_t ipc,
                                              ze_event_pool_handle_t *result) {
  auto *context = cast<Context>(contextHandle);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  IpcEventPoolHandle encoded{};
  std::memcpy(&encoded, ipc.data, sizeof(encoded));
  if (std::cmp_not_equal(encoded.process, getpid())) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  std::scoped_lock const lock(ipcEventPoolsMutex);
  const auto found = ipcEventPools.find(encoded.pool);
  if (found == ipcEventPools.end()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto *opened = createHandle<EventPool>(context, found->second->flags);
  if (!opened) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  opened->devices = found->second->devices;
  opened->ipcId = encoded.pool;
  opened->ipcOrigin = HandleUse<EventPool>(found->second);
  *result = reinterpret_cast<ze_event_pool_handle_t>(opened);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventPoolCloseIpcHandle(ze_event_pool_handle_t handle) {
  auto *pool = cast<EventPool>(handle);
  if (!pool) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pool->ipcOrigin) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return eventPoolDestroy(handle);
}
ze_result_t ZE_APICALL eventCreate(ze_event_pool_handle_t poolHandle, const ze_event_desc_t *, ze_event_handle_t *result) {
  auto *pool = cast<EventPool>(poolHandle);
  if (!pool) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *created = createHandle<Event>(pool);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_event_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventDestroy(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  // Recorded lists may still refer to an event that the application destroys
  // before submission. Retire its public handle and defer reclamation until
  // those recorded references have gone away.
  HandleUse<Event> const lifetime(event);
  event->destroyRequested.store(true, std::memory_order_release);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventSignal(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle);
  if (eventDeviceLost(event)) return ZE_RESULT_ERROR_DEVICE_LOST;
  return signalEvent(event);
}
ze_result_t ZE_APICALL eventSync(ze_event_handle_t handle, uint64_t timeout) {
  auto *event = cast<Event>(handle);
  if (eventDeviceLost(event)) return ZE_RESULT_ERROR_DEVICE_LOST;
  return waitEvent(event, timeout);
}
ze_result_t ZE_APICALL eventStatus(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (eventDeviceLost(event)) return ZE_RESULT_ERROR_DEVICE_LOST;
  std::scoped_lock const lock(event->mutex);
  return eventReached(event, event->appended) ? event->completionResult : ZE_RESULT_NOT_READY;
}
ze_result_t ZE_APICALL eventReset(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (eventDeviceLost(event)) return ZE_RESULT_ERROR_DEVICE_LOST;
  std::scoped_lock const lock(event->mutex);
  if (event->counterBased) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  event->signalled = false;
  event->completionResult = ZE_RESULT_SUCCESS;
  event->kernelStart = 0;
  event->kernelEnd = 0;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL eventCounterBasedCreate(ze_context_handle_t contextHandle, ze_device_handle_t deviceHandle,
                                               const ze_event_counter_based_desc_t *desc, ze_event_handle_t *result) {
  auto *context = cast<Context>(contextHandle);
  if (!context || !cast<Device>(deviceHandle)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (desc->stype != ZE_STRUCTURE_TYPE_EVENT_COUNTER_BASED_DESC) return ZE_RESULT_ERROR_UNSUPPORTED_ENUMERATION;
  constexpr ze_event_counter_based_flags_t known =
      ZE_EVENT_COUNTER_BASED_FLAG_IMMEDIATE | ZE_EVENT_COUNTER_BASED_FLAG_NON_IMMEDIATE | ZE_EVENT_COUNTER_BASED_FLAG_HOST_VISIBLE
      | ZE_EVENT_COUNTER_BASED_FLAG_IPC | ZE_EVENT_COUNTER_BASED_FLAG_DEVICE_TIMESTAMP | ZE_EVENT_COUNTER_BASED_FLAG_HOST_TIMESTAMP;
  if ((desc->flags & ~known) != 0) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if ((desc->flags & ZE_EVENT_COUNTER_BASED_FLAG_DEVICE_TIMESTAMP) != 0 && (desc->flags & ZE_EVENT_COUNTER_BASED_FLAG_HOST_TIMESTAMP) != 0)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if ((desc->flags & ZE_EVENT_COUNTER_BASED_FLAG_IPC) != 0) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  auto *pool = createHandle<EventPool>(context, 0);
  if (!pool) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  pool->devices.push_back(cast<Device>(deviceHandle));
  auto *created = createHandle<Event>(pool);
  // The event owns its private pool and releases it on destruction.
  pool->destroyRequested.store(true, std::memory_order_release);
  if (!created) {
    delete pool;
    return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  }
  created->counterBased = true;
  created->counterFlags = desc->flags;
  *result = reinterpret_cast<ze_event_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventCounterBasedDeviceAddress(ze_event_handle_t handle, uint64_t *completionValue, uint64_t *deviceAddress) {
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!completionValue || !deviceAddress) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!event->counterBased) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  std::scoped_lock const lock(event->mutex);
  *completionValue = event->appended;
  *deviceAddress = reinterpret_cast<uint64_t>(&event->completed);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventCounterBasedFlags(ze_event_handle_t handle, ze_event_counter_based_flags_t *flags) {
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!flags) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *flags = event->counterFlags;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventGetPool(ze_event_handle_t handle, ze_event_pool_handle_t *pool) {
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pool) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *pool = event->counterBased ? nullptr : reinterpret_cast<ze_event_pool_handle_t>(event->pool.get());
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL eventKernelTimestamp(ze_event_handle_t handle, ze_kernel_timestamp_result_t *result) {
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (eventDeviceLost(event)) return ZE_RESULT_ERROR_DEVICE_LOST;
  std::scoped_lock const lock(event->mutex);
  return eventKernelTimestampLocked(event, result);
}

ze_result_t ZE_APICALL eventQueryKernelTimestampsExt(ze_event_handle_t handle, ze_device_handle_t, uint32_t *count,
                                                     ze_event_query_kernel_timestamps_results_ext_properties_t *results) {
  auto *event = cast<Event>(handle);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (eventDeviceLost(event)) return ZE_RESULT_ERROR_DEVICE_LOST;
  const auto capacity = *count;
  *count = 1;
  if (capacity == 0) return ZE_RESULT_SUCCESS;
  if (!results) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  ze_kernel_timestamp_result_t kernel{};
  {
    std::scoped_lock const lock(event->mutex);
    if (const auto result = eventKernelTimestampLocked(event, &kernel); result != ZE_RESULT_SUCCESS) return result;
  }
  if (results->pKernelTimestampsBuffer) *results->pKernelTimestampsBuffer = kernel;
  if (results->pSynchronizedTimestampsBuffer) {
    const auto synchronised =
        ze_synchronized_timestamp_data_ext_t{.kernelStart = kernel.global.kernelStart, .kernelEnd = kernel.global.kernelEnd};
    *results->pSynchronizedTimestampsBuffer = {.global = synchronised, .context = synchronised};
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL fenceCreate(ze_command_queue_handle_t queueHandle, const ze_fence_desc_t *, ze_fence_handle_t *result) {
  auto *queue = cast<Queue>(queueHandle);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *created = createHandle<Fence>(queue);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_fence_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL fenceDestroy(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  HandleUse<Fence> const lifetime(fence);
  {
    std::scoped_lock const lock(fence->event.mutex);
    if (fence->pending) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  }
  // Completion is observable before the queue worker drops its submission
  // reference. Retire the public handle now and reclaim it after that release.
  fence->destroyRequested.store(true, std::memory_order_release);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL fenceSync(ze_fence_handle_t handle, uint64_t timeout) {
  auto *fence = cast<Fence>(handle);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (deviceLost(fence->queue->device)) return ZE_RESULT_ERROR_DEVICE_LOST;
  const auto started = std::chrono::steady_clock::now();
  const auto result = waitEvent(&fence->event, timeout);
  if (result != ZE_RESULT_SUCCESS) return result;
  auto remaining = timeout;
  if (timeout != std::numeric_limits<uint64_t>::max()) {
    const auto elapsed = std::chrono::steady_clock::now() - started;
    const auto elapsedNanoseconds = std::chrono::duration_cast<std::chrono::nanoseconds>(elapsed).count();
    if (elapsedNanoseconds < 0 || std::cmp_greater_equal(elapsedNanoseconds, timeout)) remaining = 0;
    else remaining -= static_cast<uint64_t>(elapsedNanoseconds);
  }
  gemstone::CommandQueue::Ticket submission;
  {
    std::scoped_lock const lock(fence->event.mutex);
    submission = fence->submission;
  }
  if (!scheduler().waitFor(submission, remaining)) return ZE_RESULT_NOT_READY;
  std::scoped_lock const lock(fence->event.mutex);
  return fence->completionResult;
}
ze_result_t ZE_APICALL fenceStatus(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (deviceLost(fence->queue->device)) return ZE_RESULT_ERROR_DEVICE_LOST;
  std::scoped_lock const lock(fence->event.mutex);
  if (!fence->event.signalled) return ZE_RESULT_NOT_READY;
  if (!scheduler().ready(fence->submission)) return ZE_RESULT_NOT_READY;
  return fence->completionResult;
}
ze_result_t ZE_APICALL fenceReset(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::scoped_lock const lock(fence->event.mutex);
  fence->event.signalled = false;
  fence->completionResult = ZE_RESULT_SUCCESS;
  return ZE_RESULT_SUCCESS;
}

std::string remoteUnavailableError() {
  return remote.mode() == ClientMode::Misconfigured ? remote.error() : "Gemstone execution service is not configured";
}

bool linkSpirvModules(const std::vector<std::vector<uint8_t>> &modules, std::vector<uint8_t> &linked, std::vector<LinkedKernel> &kernels,
                      std::string &error) {
  if (remote.mode() != ClientMode::Ready) {
    error = remoteUnavailableError();
    return false;
  }
  std::vector<LinkInput> inputs;
  inputs.reserve(modules.size());
  for (const auto &module : modules)
    inputs.push_back(LinkInput{.image = module});
  std::scoped_lock const rpcLock(rpcTransactionMutex);
  auto result = remote.client()->call(LinkImages{.images = std::move(inputs)});
  if (!result) {
    error = recordRemoteError(remote.error());
    return false;
  }
  auto image = std::move(result.value());
  linked = std::move(image.image);
  kernels = std::move(image.kernels);
  return true;
}

std::map<std::string, std::string> linkageNameTable(const std::vector<LinkedKernel> &kernels, std::optional<int32_t> module = {}) {
  std::map<std::string, std::string> names;
  for (const auto &kernel : kernels)
    if (!module || kernel.module == *module) names.emplace(kernel.name, kernel.linkageName);
  return names;
}

std::shared_ptr<LoadedProgram> loadSpirvProgram(const std::vector<uint8_t> &bytes, std::string &error);
bool programTeardown(LoadedProgram &program);

size_t spirvInstructionBytes(const std::vector<uint8_t> &bytes);

ze_result_t ZE_APICALL moduleCreate(ze_context_handle_t contextHandle, ze_device_handle_t deviceHandle, const ze_module_desc_t *desc,
                                    ze_module_handle_t *result, ze_module_build_log_handle_t *log) {
  auto *context = cast<Context>(contextHandle);
  auto *device = cast<Device>(deviceHandle);
  if (!context || !device) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  // The remote service consumes SPIR-V in both cases. Returning the retained
  // SPIR-V bytes from zeModuleGetNativeBinary lets Unified Runtime populate its
  // persistent program cache; a later cache hit presents those bytes back as a
  // native module.
  if (desc->format != ZE_MODULE_FORMAT_IL_SPIRV && desc->format != ZE_MODULE_FORMAT_NATIVE) return ZE_RESULT_ERROR_UNSUPPORTED_ENUMERATION;
  struct InputModule {
    const uint8_t *data;
    size_t size;
    const ze_module_constants_t *constants;
  };
  std::vector<InputModule> inputs;
  const auto *extension = static_cast<const ze_base_desc_t *>(desc->pNext);
  while (extension) {
    if (extension->stype == ZE_STRUCTURE_TYPE_MODULE_PROGRAM_EXP_DESC) {
      const auto *program = reinterpret_cast<const ze_module_program_exp_desc_t *>(extension);
      if (program->count == 0 || !program->inputSizes || !program->pInputModules) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
      inputs.reserve(program->count);
      for (uint32_t index = 0; index < program->count; ++index) {
        if (program->inputSizes[index] == 0 || program->pInputModules[index] == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
        inputs.push_back({.data = program->pInputModules[index],
                          .size = program->inputSizes[index],
                          .constants = program->pConstants ? program->pConstants[index] : nullptr});
      }
      break;
    }
    extension = static_cast<const ze_base_desc_t *>(extension->pNext);
  }
  if (inputs.empty()) {
    if (desc->inputSize == 0 || !desc->pInputModule) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
    inputs.push_back({.data = static_cast<const uint8_t *>(desc->pInputModule), .size = desc->inputSize, .constants = desc->pConstants});
  }
  std::vector<std::vector<uint8_t>> prepared;
  prepared.reserve(inputs.size());
  for (const auto &input : inputs) {
    std::vector<uint8_t> bytes(input.data, input.data + input.size);
    if (desc->format == ZE_MODULE_FORMAT_NATIVE)
      if (auto binary = gemstone::decodeProgramBinary(input.data, input.size)) bytes = std::move(binary->payload);
    if (input.constants && input.constants->numConstants != 0) {
      const auto &constants = *input.constants;
      if (!constants.pConstantIds || !constants.pConstantValues) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
      if (remote.mode() != ClientMode::Ready) {
        setBuildLog(log, "SPIR-V specialization requires the Gemstone execution service");
        return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
      }
      std::scoped_lock const rpcLock(rpcTransactionMutex);
      auto described = remote.client()->call(DescribeSpecialization{bytes});
      if (!described) {
        setBuildLog(log, recordRemoteError(remote.error()));
        return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
      }
      std::map<uint32_t, size_t> sizes;
      for (const auto &info : described.value())
        sizes.emplace(static_cast<uint32_t>(info.specId.value), static_cast<size_t>(info.byteSize.value));
      std::vector<SpecializationConstant> values;
      values.reserve(constants.numConstants);
      for (uint32_t index = 0; index < constants.numConstants; ++index) {
        const auto id = constants.pConstantIds[index];
        const auto found = sizes.find(id);
        const auto *value = static_cast<const uint8_t *>(constants.pConstantValues[index]);
        if (found == sizes.end() || !value) {
          setBuildLog(log, "invalid SPIR-V specialization constant");
          return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
        }
        values.push_back(SpecializationConstant{.specId = Binary32{static_cast<int64_t>(id)},
                                                .value = std::vector<uint8_t>(value, value + found->second)});
      }
      auto specialized = remote.client()->call(SpecializeImage{.image = bytes, .constants = values});
      if (!specialized) {
        setBuildLog(log, recordRemoteError(remote.error()));
        return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
      }
      bytes = std::move(specialized.value());
    }
    prepared.push_back(std::move(bytes));
  }
  std::vector<uint8_t> bytes;
  std::vector<LinkedKernel> kernels;
  if (prepared.size() == 1) {
    bytes = std::move(prepared.front());
  } else {
    std::string linkerError;
    if (!linkSpirvModules(prepared, bytes, kernels, linkerError)) {
      setBuildLog(log, linkerError);
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
  }
  // A native module may be a cached binary carrying an options trailer; reduce
  // it to its instruction stream before loading.
  if (desc->format == ZE_MODULE_FORMAT_NATIVE) {
    const size_t valid = spirvInstructionBytes(bytes);
    if (valid < bytes.size()) bytes.resize(valid);
  }
  std::string loadError;
  auto program = loadSpirvProgram(bytes, loadError);
  if (!program) {
    setBuildLog(log, loadError);
    return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
  }
  auto *created = createHandle<Module>(context, device, std::move(bytes), std::move(program), desc->pBuildFlags ? desc->pBuildFlags : "");
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  created->linkageNames = linkageNameTable(kernels);
  if (log) {
    auto *buildLog = createHandle<BuildLog>("");
    if (!buildLog) {
      delete created;
      return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
    }
    *log = reinterpret_cast<ze_module_build_log_handle_t>(buildLog);
  }
  *result = reinterpret_cast<ze_module_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL moduleFunctionPointer(ze_module_handle_t handle, const char *name, void **pointer) {
  auto *module = cast<Module>(handle);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!name || !pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (remote.mode() != ClientMode::Ready) return reportStatus(module->device, ZE_RESULT_ERROR_DEVICE_LOST);
  auto described = [&] {
    std::scoped_lock const rpcLock(rpcTransactionMutex);
    return remote.client()->call(
        DescribeKernel{.program = ProgramId{static_cast<int64_t>(module->program->id)}, .name = {module->linkageName(name)}});
  }();
  if (!described) {
    recordRemoteError(described.error().detail);
    return ZE_RESULT_ERROR_INVALID_FUNCTION_NAME;
  }
  const auto &metadata = described.value();
  if (!metadata.entryPointId) return ZE_RESULT_ERROR_INVALID_FUNCTION_NAME;
  if (module->program->id > std::numeric_limits<uint32_t>::max()) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  const uint64_t token = (module->program->id << 32) | static_cast<uint32_t>(metadata.entryPointId->value);
  *pointer = reinterpret_cast<void *>(static_cast<uintptr_t>(token));
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL moduleGlobalPointer(ze_module_handle_t handle, const char *name, size_t *size, void **pointer) {
  auto *module = cast<Module>(handle);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!name || (!size && !pointer)) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  {
    std::scoped_lock const globalsLock(module->program->globalsMutex);
    if (const auto global = module->program->globals ^ find_cref([&](const LoadedProgram::Global &entry) { return entry.name == name; })) {
      if (size) *size = global->get().size;
      if (pointer) *pointer = global->get().base;
      return ZE_RESULT_SUCCESS;
    }
  }
  if (remote.mode() != ClientMode::Ready) return reportStatus(module->device, ZE_RESULT_ERROR_DEVICE_LOST);
  auto described = [&] {
    std::scoped_lock const rpcLock(rpcTransactionMutex);
    return remote.client()->call(DescribeGlobal{.program = ProgramId{static_cast<int64_t>(module->program->id)}, .name = {name}});
  }();
  if (!described) {
    recordRemoteError(described.error().detail);
    return ZE_RESULT_ERROR_INVALID_GLOBAL_NAME;
  }
  const auto bytes = static_cast<size_t>(described.value().size.value);
  void *base = nullptr;
  auto result = allocate(module->context.get(), module->device, bytes, 0, ZE_MEMORY_TYPE_DEVICE, &base);
  if (result != ZE_RESULT_SUCCESS) return result;
  const auto discard = [&] {
    std::optional<uint64_t> id;
    {
      std::scoped_lock const lock(allocationsMutex);
      const auto found = allocations.find(base);
      if (found != allocations.end()) {
        id = found->second->id;
        allocations.erase(found);
      }
    }
    if (id) {
      std::scoped_lock const rpcLock(rpcTransactionMutex);
      (void)remote.client()->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(*id)}});
    }
    std::free(base);
  };
  if (described.value().initial.size() > bytes) {
    discard();
    return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
  }
  if (!described.value().initial.empty()) std::memcpy(base, described.value().initial.data(), described.value().initial.size());
  const auto allocation = retainAllocation(base, bytes);
  if (!allocation || !allocation->id) {
    discard();
    return reportStatus(module->device, ZE_RESULT_ERROR_DEVICE_LOST);
  }
  bool bound = false;
  {
    std::scoped_lock const rpcLock(rpcTransactionMutex);
    bound = static_cast<bool>(remote.client()->call(BindProgramGlobal{.program = ProgramId{static_cast<int64_t>(module->program->id)},
                                                                      .name = {name},
                                                                      .allocation = AllocationId{static_cast<int64_t>(*allocation->id)},
                                                                      .offset = LongPositive0{0}}));
    if (!bound) recordRemoteError(remote.error());
  }
  if (!bound) {
    discard();
    return reportStatus(module->device, ZE_RESULT_ERROR_DEVICE_LOST);
  }
  {
    std::scoped_lock const globalsLock(module->program->globalsMutex);
    module->program->globals.push_back({.name = name, .base = base, .size = bytes});
  }
  if (size) *size = bytes;
  if (pointer) *pointer = base;
  return ZE_RESULT_SUCCESS;
}
bool programTeardown(LoadedProgram &program) {
  {
    std::scoped_lock const rpcLock(rpcTransactionMutex);
    if (!remote.client()->call(UnloadProgram{ProgramId{static_cast<int64_t>(program.id)}})) {
      recordRemoteError(remote.error());
      return false;
    }
  }
  std::vector<LoadedProgram::Global> globals;
  {
    std::scoped_lock const globalsLock(program.globalsMutex);
    globals = program.globals;
  }
  bool releasedAll = true;
  for (const auto &global : globals) {
    AllocationRef allocation;
    {
      std::scoped_lock const lock(allocationsMutex);
      const auto found = allocations.find(global.base);
      if (found != allocations.end()) {
        allocation = found->second;
        allocations.erase(found);
      }
    }
    if (allocation) {
      if (remote.mode() == ClientMode::Ready) {
        std::scoped_lock const rpcLock(rpcTransactionMutex);
        if (allocation->id && !remote.client()->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(*allocation->id)}})) {
          recordRemoteError(remote.error());
          releasedAll = false;
        }
      }
      std::free(global.base);
    }
  }
  return releasedAll;
}

std::shared_ptr<LoadedProgram> loadSpirvProgram(const std::vector<uint8_t> &bytes, std::string &error) {
  if (remote.mode() != ClientMode::Ready) {
    error = remoteUnavailableError();
    return nullptr;
  }
  if (bytes.size() > std::numeric_limits<uint32_t>::max()) {
    error = "SPIR-V module is too large";
    return nullptr;
  }
  std::scoped_lock const rpcLock(rpcTransactionMutex);
  auto loaded = remote.client()->call(LoadProgram{.encoding = ProgramEncoding::SPIRVOpenCLCompute, .image = bytes});
  if (!loaded) {
    error = recordRemoteError(remote.error());
    return nullptr;
  }
  auto program = std::make_shared<LoadedProgram>(static_cast<uint64_t>(loaded.value().value));
  program->teardown = programTeardown;
  return program;
}
ze_result_t ZE_APICALL moduleDestroy(ze_module_handle_t handle) {
  auto *module = cast<Module>(handle);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  // Kernels created from the module keep it loaded until they are destroyed.
  HandleUse<Module> const lifetime(module);
  module->destroyRequested.store(true, std::memory_order_release);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL moduleDynamicLink(uint32_t count, ze_module_handle_t *modules, ze_module_build_log_handle_t *log) {
  if (count == 0 || !modules) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<Module *> linked;
  for (uint32_t index = 0; index < count; ++index) {
    auto *module = cast<Module>(modules[index]);
    if (module == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (std::find(linked.begin(), linked.end(), module) == linked.end()) linked.push_back(module);
  }
  if (linked.size() == 1) return ZE_RESULT_SUCCESS;
  std::vector<std::vector<uint8_t>> sources;
  sources.reserve(linked.size());
  for (const auto *module : linked)
    sources.push_back(module->source);
  std::vector<uint8_t> bytes;
  std::vector<LinkedKernel> kernels;
  std::string error;
  auto program = linkSpirvModules(sources, bytes, kernels, error) ? loadSpirvProgram(bytes, error) : nullptr;
  if (!program) {
    setBuildLog(log, error);
    return ZE_RESULT_ERROR_MODULE_LINK_FAILURE;
  }
  for (size_t index = 0; index < linked.size(); ++index) {
    linked[index]->bytes = bytes;
    linked[index]->program = program;
    linked[index]->linkageNames = linkageNameTable(kernels, static_cast<int32_t>(index));
  }
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL moduleProperties(ze_module_handle_t handle, ze_module_properties_t *properties) {
  if (cast<Module>(handle) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->flags = 0;
  return ZE_RESULT_SUCCESS;
}
// IGC normalises the -ze-opt-level flag when it embeds the build options in the
// native binary it returns.
std::string igcOptions(const std::string &flags) {
  const std::string prefix = "-ze-opt-level=";
  std::string result = flags;
  for (size_t position = result.find(prefix); position != std::string::npos; position = result.find(prefix, position + 1)) {
    const size_t digit = position + prefix.size();
    if (digit < result.size() && std::isdigit(static_cast<unsigned char>(result[digit]))) {
      result.insert(digit, "O");
      position = digit + 1;
    }
  }
  return result;
}

// Length of the valid SPIR-V instruction stream, so a native binary that carries
// an options trailer can be reduced back to its module before loading.
size_t spirvInstructionBytes(const std::vector<uint8_t> &bytes) {
  if (bytes.size() < 20 || bytes.size() % 4 != 0) return bytes.size();
  const auto word = [&](size_t index) {
    return static_cast<uint32_t>(bytes[index * 4]) | (static_cast<uint32_t>(bytes[(index * 4) + 1]) << 8)
           | (static_cast<uint32_t>(bytes[(index * 4) + 2]) << 16) | (static_cast<uint32_t>(bytes[(index * 4) + 3]) << 24);
  };
  const size_t count = bytes.size() / 4;
  size_t offset = 5;
  while (offset < count) {
    const uint32_t length = word(offset) >> 16;
    if (length == 0 || offset + length > count) break;
    offset += length;
  }
  return offset * 4;
}

ze_result_t ZE_APICALL moduleNativeBinary(ze_module_handle_t handle, size_t *size, uint8_t *binary) {
  auto *module = cast<Module>(handle);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  // A compiled native binary carries the options it was built with. Append the
  // translated IGC options after a zero-length word so the SPIR-V stream stays
  // intact and recoverable on the way back in.
  std::vector<uint8_t> payload = module->bytes;
  const std::string options = igcOptions(module->buildFlags);
  if (!options.empty()) {
    // The zero-length word terminates the SPIR-V stream; pad the options to a
    // word boundary so the total stays word-aligned.
    payload.insert(payload.end(), 4, 0);
    payload.insert(payload.end(), options.begin(), options.end());
    while (payload.size() % 4 != 0)
      payload.push_back(0);
  }
  const size_t required = payload.size();
  if (!binary) {
    *size = required;
    return ZE_RESULT_SUCCESS;
  }
  if (*size < required) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (required != 0) std::memcpy(binary, payload.data(), required);
  *size = required;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL buildLogDestroy(ze_module_build_log_handle_t handle) { return destroyHandle(cast<BuildLog>(handle)); }
ze_result_t ZE_APICALL buildLogString(ze_module_build_log_handle_t handle, size_t *size, char *text) {
  auto *log = cast<BuildLog>(handle);
  if (!log || !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  return gemstone::copyOut(*size, text, size, log->text.c_str(), log->text.size() + 1) ? ZE_RESULT_SUCCESS : ZE_RESULT_ERROR_INVALID_SIZE;
}

ze_result_t ZE_APICALL kernelGroup(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z);
ze_result_t ZE_APICALL kernelCreate(ze_module_handle_t moduleHandle, const ze_kernel_desc_t *desc, ze_kernel_handle_t *result) {
  auto *module = cast<Module>(moduleHandle);
  if (!module || !desc || !result || !desc->pKernelName) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *kernel = createHandle<Kernel>(module, desc->pKernelName);
  if (!kernel) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  if (remote.mode() != ClientMode::Ready) {
    delete kernel;
    return reportStatus(module->device, ZE_RESULT_ERROR_DEVICE_LOST);
  }
  auto described = remote.client()->call(
      DescribeKernel{.program = ProgramId{static_cast<int64_t>(module->program->id)}, .name = {kernel->linkageName()}});
  if (!described) {
    recordRemoteError(remote.error());
    delete kernel;
    return described.error().kind == FailureKind::NotFound ? ZE_RESULT_ERROR_INVALID_KERNEL_NAME : ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
  }
  const auto &metadata = described.value();
  if (const auto resources = kernelResources(module->bytes, kernel->linkageName())) {
    kernel->localMemSize = resources->localBytes;
    kernel->privateMemSize = resources->privateBytes;
    kernel->spillMemSize = resources->spillBytes;
  }
  kernel->parameters =
      metadata.parameters ^ map([](const auto &parameter) {
        const bool directPointer = parameter.kind == ParameterKind::Allocation;
        return KernelParameterMetadata{.pointerAggregate = parameter.kind == ParameterKind::Scalar && parameter.containsPointers,
                                       .local = parameter.kind == ParameterKind::Local,
                                       .containsPointer = directPointer || parameter.containsPointers};
      });
  if (metadata.requiredWorkGroupSize) {
    const auto &required = *metadata.requiredWorkGroupSize;
    kernel->requiredGroupSize = {static_cast<uint32_t>(required.x.value), static_cast<uint32_t>(required.y.value),
                                 static_cast<uint32_t>(required.z.value)};
    const auto status = kernelGroup(reinterpret_cast<ze_kernel_handle_t>(kernel), kernel->requiredGroupSize[0],
                                    kernel->requiredGroupSize[1], kernel->requiredGroupSize[2]);
    if (status != ZE_RESULT_SUCCESS) {
      delete kernel;
      return status;
    }
  }
  *result = reinterpret_cast<ze_kernel_handle_t>(kernel);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelDestroy(ze_kernel_handle_t handle) { return destroyHandle(cast<Kernel>(handle)); }
ze_result_t ZE_APICALL kernelGroup(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (x == 0 || y == 0 || z == 0 || x > MaxWorkItems || y > MaxWorkItems || z > MaxWorkItemsZ
      || static_cast<uint64_t>(x) * y * z > MaxWorkItems)
    return ZE_RESULT_ERROR_INVALID_GROUP_SIZE_DIMENSION;
  const auto &required = kernel->requiredGroupSize;
  if (required[0] != 0 && (x != required[0] || y != required[1] || z != required[2])) return ZE_RESULT_ERROR_INVALID_GROUP_SIZE_DIMENSION;
  kernel->groupX = x;
  kernel->groupY = y;
  kernel->groupZ = z;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelGlobalOffset(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (x > static_cast<uint32_t>(std::numeric_limits<int32_t>::max()) || y > static_cast<uint32_t>(std::numeric_limits<int32_t>::max())
      || z > static_cast<uint32_t>(std::numeric_limits<int32_t>::max()))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  kernel->offsetX = x;
  kernel->offsetY = y;
  kernel->offsetZ = z;
  return ZE_RESULT_SUCCESS;
}
uint32_t largestDivisorAtMost(uint32_t value, uint32_t limit) {
  for (uint32_t candidate = std::min(value, limit); candidate > 1; --candidate)
    if (value % candidate == 0) return candidate;
  return 1;
}

ze_result_t ZE_APICALL kernelSuggest(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z, uint32_t *gx, uint32_t *gy,
                                     uint32_t *gz) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!gx || !gy || !gz) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (x == 0 || y == 0 || z == 0) return ZE_RESULT_ERROR_INVALID_GLOBAL_WIDTH_DIMENSION;
  const auto &required = kernel->requiredGroupSize;
  if (required[0] != 0) {
    *gx = required[0];
    *gy = required[1];
    *gz = required[2];
    return ZE_RESULT_SUCCESS;
  }

  // Unified Runtime requires every suggested dimension to divide its global
  // dimension when the SYCL nd_range did not provide a local size. Preserve
  // the driver's x-major preference while staying within the advertised
  // 1024-work-item limit.
  *gx = largestDivisorAtMost(x, 256);
  *gy = largestDivisorAtMost(y, std::min(4U, MaxWorkItems / *gx));
  *gz = largestDivisorAtMost(z, std::min(4U, MaxWorkItems / (*gx * *gy)));
  if (gemstone::traceEnabled("levelzero") && x > 256)
    std::fprintf(stderr, "Gemstone Level Zero: suggested %ux%ux%u for global %ux%ux%u\n", *gx, *gy, *gz, x, y, z);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelArgument(ze_kernel_handle_t handle, uint32_t index, size_t size, const void *value) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (kernel->arguments.size() <= index) kernel->arguments.resize(index + 1);
  const auto *bytes = static_cast<const uint8_t *>(value);
  auto &argument = kernel->arguments[index];
  // DPC++ also passes a null host pointer for zero-valued or eliminated ABI
  // fields. Prefer the retained SPIR-V parameter storage class when it is
  // available; only fall back to Level Zero's null marker for opaque images.
  argument.local = index < kernel->parameters.size() ? kernel->parameters[index].local : !value;
  argument.localSize = argument.local ? size : 0;
  argument.bytes = argument.local ? std::vector<uint8_t>{} : std::vector<uint8_t>(size, 0);
  if (!argument.local && value) std::memcpy(argument.bytes.data(), bytes, size);
  argument.opaque = Kernel::Argument::Opaque::None;
  if (!argument.local && value && size == sizeof(void *)) {
    void *handleValue = nullptr;
    std::memcpy(&handleValue, bytes, sizeof(handleValue));
    if (cast<Image>(handleValue)) argument.opaque = Kernel::Argument::Opaque::Image;
    else if (cast<Sampler>(handleValue)) argument.opaque = Kernel::Argument::Opaque::Sampler;
  }
  argument.recordId = 0;
  argument.texelsId = 0;
  argument.recordBytes = 0;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelProperties(ze_kernel_handle_t handle, ze_kernel_properties_t *properties) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->numKernelArgs = static_cast<uint32_t>(kernel->parameters.size());
  properties->requiredGroupSizeX = kernel->requiredGroupSize[0];
  properties->requiredGroupSizeY = kernel->requiredGroupSize[1];
  properties->requiredGroupSizeZ = kernel->requiredGroupSize[2];
  properties->requiredNumSubGroups = 0;
  properties->requiredSubgroupSize = 0;
  properties->maxSubgroupSize = 32;
  properties->maxNumSubgroups = 128;
  properties->localMemSize = kernel->localMemSize;
  properties->privateMemSize = kernel->privateMemSize;
  properties->spillMemSize = kernel->spillMemSize;
  std::memset(&properties->uuid, 0, sizeof(properties->uuid));
  auto *extension = static_cast<ze_base_properties_t *>(properties->pNext);
  while (extension) {
    if (extension->stype == ZE_STRUCTURE_TYPE_KERNEL_PREFERRED_GROUP_SIZE_PROPERTIES) {
      auto *preferred = reinterpret_cast<ze_kernel_preferred_group_size_properties_t *>(extension);
      preferred->preferredMultiple = 8;
    } else if (extension->stype == ZE_STRUCTURE_TYPE_KERNEL_MAX_GROUP_SIZE_EXT_PROPERTIES) {
      auto *maximum = reinterpret_cast<ze_kernel_max_group_size_properties_ext_t *>(extension);
      maximum->maxGroupSize = MaxWorkItems;
    }
    extension = static_cast<ze_base_properties_t *>(extension->pNext);
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL kernelSetIndirect(ze_kernel_handle_t handle, ze_kernel_indirect_access_flags_t flags) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  kernel->indirect = flags;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelGetIndirect(ze_kernel_handle_t handle, ze_kernel_indirect_access_flags_t *flags) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel || !flags) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *flags = kernel->indirect;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelSourceAttributes(ze_kernel_handle_t handle, uint32_t *size, char **attributes) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (remote.mode() != ClientMode::Ready) return reportStatus(kernel->module->device, ZE_RESULT_ERROR_DEVICE_LOST);
  auto described = remote.client()->call(
      DescribeKernel{.program = ProgramId{static_cast<int64_t>(kernel->module->program->id)}, .name = {kernel->linkageName()}});
  if (!described) {
    recordRemoteError(remote.error());
    return reportStatus(kernel->module->device, ZE_RESULT_ERROR_DEVICE_LOST);
  }
  const auto &metadata = described.value();
  std::string source;
  if (metadata.requiredWorkGroupSize)
    source = "reqd_work_group_size(" + std::to_string(metadata.requiredWorkGroupSize->x.value) + ","
             + std::to_string(metadata.requiredWorkGroupSize->y.value) + "," + std::to_string(metadata.requiredWorkGroupSize->z.value)
             + ")";
  if (metadata.workGroupSizeHint) {
    if (!source.empty()) source += "\n";
    source += "work_group_size_hint(" + std::to_string(metadata.workGroupSizeHint->x.value) + ","
              + std::to_string(metadata.workGroupSizeHint->y.value) + "," + std::to_string(metadata.workGroupSizeHint->z.value) + ")";
  }
  const auto required = static_cast<uint32_t>(source.size() + 1);
  if (!attributes) {
    *size = required;
    return ZE_RESULT_SUCCESS;
  }
  if (!*attributes) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (*size < required) {
    *size = required;
    return ZE_RESULT_ERROR_INVALID_SIZE;
  }
  std::memcpy(*attributes, source.c_str(), required);
  *size = required;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelName(ze_kernel_handle_t handle, size_t *size, char *name) {
  auto *kernel = cast<Kernel>(handle);
  if (!kernel || !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  return gemstone::copyOut(*size, name, size, kernel->name.c_str(), kernel->name.size() + 1) ? ZE_RESULT_SUCCESS
                                                                                             : ZE_RESULT_ERROR_INVALID_SIZE;
}

template <class T> ze_result_t emptyTable(ze_api_version_t version, T *table) {
  const auto checked = checkVersion(version, table);
  if (checked == ZE_RESULT_SUCCESS) *table = {};
  return checked;
}

} // namespace

gemstone::CommandQueue &gemstone::levelzero::commandQueue() { return scheduler(); }

gemstone::levelzero::Engine &gemstone::levelzero::engineFor(uint32_t ordinal, uint32_t index) {
  static std::mutex mutex;
  // Never destroyed: workers stopped by the scheduler shutdown may still release their engine.
  static auto *const engines = new std::map<std::pair<uint32_t, uint32_t>, Engine *>();
  std::scoped_lock const lock(mutex);
  auto *&engine = (*engines)[{ordinal, index}];
  if (!engine) engine = new Engine();
  return *engine;
}

extern "C" ZE_DLLEXPORT ze_result_t ZE_APICALL zeKernelSetGlobalOffsetExp(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z) {
  return kernelGlobalOffset(handle, x, y, z);
}

// Loader-facing ABI table wiring lives here so it can bind the facade's
// private implementation functions without a textual include fragment.
#define TABLE_BEGIN(name, type)                                                                                                            \
  extern "C" ZE_DLLEXPORT ze_result_t ZE_APICALL name(ze_api_version_t version, std::add_pointer_t<type> table) {                          \
    const auto checked = emptyTable(version, table);                                                                                       \
    if (checked != ZE_RESULT_SUCCESS) return checked
#define TABLE_END                                                                                                                          \
  return ZE_RESULT_SUCCESS;                                                                                                                \
  }

TABLE_BEGIN(zeGetGlobalProcAddrTable, ze_global_dditable_t);
table->pfnInit = init;
table->pfnInitDrivers = initDrivers;
TABLE_END
TABLE_BEGIN(zeGetDriverProcAddrTable, ze_driver_dditable_t);
table->pfnGet = driverGet;
table->pfnGetApiVersion = driverApiVersion;
table->pfnGetProperties = driverProperties;
table->pfnGetIpcProperties = driverIpcProperties;
table->pfnGetExtensionProperties = driverExtensions;
table->pfnGetExtensionFunctionAddress = driverExtensionAddress;
table->pfnGetLastErrorDescription = driverLastError;
TABLE_END
TABLE_BEGIN(zeGetDeviceProcAddrTable, ze_device_dditable_t);
table->pfnGet = deviceGet;
table->pfnGetSubDevices = deviceSubdevices;
table->pfnGetProperties = deviceProperties;
table->pfnGetComputeProperties = deviceCompute;
table->pfnGetModuleProperties = deviceModule;
table->pfnGetCommandQueueGroupProperties = deviceQueueGroups;
table->pfnGetMemoryProperties = deviceMemory;
table->pfnGetMemoryAccessProperties = deviceMemoryAccess;
table->pfnGetCacheProperties = deviceCache;
table->pfnGetStatus = deviceStatus;
table->pfnGetGlobalTimestamps = deviceTimestamps;
table->pfnGetImageProperties = deviceImageProperties;
table->pfnGetExternalMemoryProperties = deviceExternalMemoryProperties;
table->pfnPciGetPropertiesExt = devicePciProperties;
table->pfnCanAccessPeer = deviceCanAccessPeer;
table->pfnGetP2PProperties = deviceGetP2PProperties;
TABLE_END
TABLE_BEGIN(zeGetContextProcAddrTable, ze_context_dditable_t);
table->pfnCreate = contextCreate;
table->pfnCreateEx = contextCreateEx;
table->pfnDestroy = contextDestroy;
table->pfnGetStatus = contextStatus;
table->pfnMakeMemoryResident = contextMemory;
table->pfnEvictMemory = contextMemory;
table->pfnSystemBarrier = contextSystemBarrier;
TABLE_END
TABLE_BEGIN(zeGetCommandQueueProcAddrTable, ze_command_queue_dditable_t);
table->pfnCreate = queueCreate;
table->pfnDestroy = queueDestroy;
table->pfnExecuteCommandLists = queueExecute;
table->pfnSynchronize = queueSync;
TABLE_END
TABLE_BEGIN(zeGetCommandListProcAddrTable, ze_command_list_dditable_t);
table->pfnCreate = listCreate;
table->pfnCreateImmediate = listCreateImmediate;
table->pfnDestroy = listDestroy;
table->pfnClose = listClose;
table->pfnReset = listReset;
table->pfnAppendBarrier = listBarrier;
table->pfnAppendMemoryRangesBarrier = listMemoryRangesBarrier;
table->pfnAppendWriteGlobalTimestamp = listWriteGlobalTimestamp;
table->pfnAppendQueryKernelTimestamps = listQueryKernelTimestamps;
table->pfnAppendMemoryCopy = listCopy;
table->pfnAppendMemoryCopyFromContext = listCopyFromContext;
table->pfnAppendMemoryCopyRegion = listCopyRegion;
table->pfnAppendImageCopy = listImageCopy;
table->pfnAppendImageCopyRegion = listImageCopyRegion;
table->pfnAppendImageCopyToMemory = listImageCopyToMemory;
table->pfnAppendImageCopyFromMemory = listImageCopyFromMemory;
table->pfnAppendImageCopyToMemoryExt = listImageCopyToMemoryExt;
table->pfnAppendImageCopyFromMemoryExt = listImageCopyFromMemoryExt;
table->pfnAppendMemoryFill = listFill;
table->pfnAppendMemoryPrefetch = listPrefetch;
table->pfnAppendMemAdvise = listAdvise;
table->pfnAppendSignalEvent = listSignal;
table->pfnAppendWaitOnEvents = listWait;
table->pfnAppendEventReset = listResetEvent;
table->pfnAppendLaunchKernel = listLaunch;
table->pfnAppendLaunchCooperativeKernel = listLaunch;
table->pfnAppendLaunchKernelIndirect = listLaunchIndirect;
table->pfnAppendLaunchMultipleKernelsIndirect = listLaunchMultipleIndirect;
table->pfnImmediateAppendCommandListsWithParameters = [](ze_command_list_handle_t listHandle, uint32_t count,
                                                         ze_command_list_handle_t *lists, const void *, ze_event_handle_t signal,
                                                         uint32_t waitCount, ze_event_handle_t *waits) {
  return listImmediateAppendCommandLists(listHandle, count, lists, signal, waitCount, waits);
};
table->pfnHostSynchronize = [](ze_command_list_handle_t handle, uint64_t timeout) {
  auto *list = cast<CommandList>(handle);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return list->async.waitFor(timeout);
};
TABLE_END
TABLE_BEGIN(zeGetMemProcAddrTable, ze_mem_dditable_t);
table->pfnAllocShared = memShared;
table->pfnAllocDevice = memDevice;
table->pfnAllocHost = memHost;
table->pfnFree = memFree;
table->pfnFreeExt = memFreeExt;
table->pfnGetAllocProperties = memProperties;
table->pfnGetAddressRange = memRange;
TABLE_END
TABLE_BEGIN(zeGetEventPoolProcAddrTable, ze_event_pool_dditable_t);
table->pfnCreate = eventPoolCreate;
table->pfnDestroy = eventPoolDestroy;
table->pfnGetIpcHandle = eventPoolGetIpcHandle;
table->pfnOpenIpcHandle = eventPoolOpenIpcHandle;
table->pfnCloseIpcHandle = eventPoolCloseIpcHandle;
TABLE_END
TABLE_BEGIN(zeGetEventProcAddrTable, ze_event_dditable_t);
table->pfnCreate = eventCreate;
table->pfnDestroy = eventDestroy;
table->pfnHostSignal = eventSignal;
table->pfnHostSynchronize = eventSync;
table->pfnCounterBasedCreate = eventCounterBasedCreate;
table->pfnCounterBasedGetDeviceAddress = eventCounterBasedDeviceAddress;
table->pfnGetCounterBasedFlags = eventCounterBasedFlags;
table->pfnGetEventPool = eventGetPool;
table->pfnQueryStatus = eventStatus;
table->pfnHostReset = eventReset;
table->pfnQueryKernelTimestamp = eventKernelTimestamp;
table->pfnQueryKernelTimestampsExt = eventQueryKernelTimestampsExt;
TABLE_END
TABLE_BEGIN(zeGetFenceProcAddrTable, ze_fence_dditable_t);
table->pfnCreate = fenceCreate;
table->pfnDestroy = fenceDestroy;
table->pfnHostSynchronize = fenceSync;
table->pfnQueryStatus = fenceStatus;
table->pfnReset = fenceReset;
TABLE_END
TABLE_BEGIN(zeGetModuleProcAddrTable, ze_module_dditable_t);
table->pfnCreate = moduleCreate;
table->pfnDestroy = moduleDestroy;
table->pfnDynamicLink = moduleDynamicLink;
table->pfnGetNativeBinary = moduleNativeBinary;
table->pfnGetProperties = moduleProperties;
table->pfnGetGlobalPointer = moduleGlobalPointer;
table->pfnGetFunctionPointer = moduleFunctionPointer;
TABLE_END
TABLE_BEGIN(zeGetModuleBuildLogProcAddrTable, ze_module_build_log_dditable_t);
table->pfnDestroy = buildLogDestroy;
table->pfnGetString = buildLogString;
TABLE_END
TABLE_BEGIN(zeGetKernelProcAddrTable, ze_kernel_dditable_t);
table->pfnCreate = kernelCreate;
table->pfnDestroy = kernelDestroy;
table->pfnSetGroupSize = kernelGroup;
table->pfnSuggestGroupSize = kernelSuggest;
table->pfnSetArgumentValue = kernelArgument;
table->pfnSetIndirectAccess = kernelSetIndirect;
table->pfnGetIndirectAccess = kernelGetIndirect;
table->pfnGetSourceAttributes = kernelSourceAttributes;
table->pfnGetProperties = kernelProperties;
table->pfnGetName = kernelName;
TABLE_END
TABLE_BEGIN(zeGetKernelExpProcAddrTable, ze_kernel_exp_dditable_t);
table->pfnSetGlobalOffsetExp = kernelGlobalOffset;
TABLE_END

#define EMPTY_TABLE(name, type)                                                                                                            \
  TABLE_BEGIN(name, type);                                                                                                                 \
  TABLE_END
TABLE_BEGIN(zeGetImageProcAddrTable, ze_image_dditable_t);
table->pfnGetProperties = imageProperties;
table->pfnCreate = imageCreate;
table->pfnDestroy = imageDestroy;
TABLE_END
TABLE_BEGIN(zeGetPhysicalMemProcAddrTable, ze_physical_mem_dditable_t);
table->pfnCreate = physicalCreate;
table->pfnDestroy = physicalDestroy;
table->pfnGetProperties = physicalProperties;
TABLE_END
TABLE_BEGIN(zeGetSamplerProcAddrTable, ze_sampler_dditable_t);
table->pfnCreate = samplerCreate;
table->pfnDestroy = samplerDestroy;
TABLE_END
TABLE_BEGIN(zeGetVirtualMemProcAddrTable, ze_virtual_mem_dditable_t);
table->pfnReserve = virtualReserve;
table->pfnFree = virtualFree;
table->pfnQueryPageSize = virtualQueryPageSize;
table->pfnMap = virtualMap;
table->pfnUnmap = virtualUnmap;
table->pfnSetAccessAttribute = virtualSetAccess;
table->pfnGetAccessAttribute = virtualGetAccess;
TABLE_END

TABLE_BEGIN(zeGetMemExpProcAddrTable, ze_mem_exp_dditable_t);
table->pfnSetAtomicAccessAttributeExp = memSetAtomicAccess;
table->pfnGetAtomicAccessAttributeExp = memGetAtomicAccess;
TABLE_END
TABLE_BEGIN(zeGetCommandListExpProcAddrTable, ze_command_list_exp_dditable_t);
table->pfnImmediateAppendCommandListsExp = listImmediateAppendCommandLists;
TABLE_END
// These API families are intentionally not implemented by the execution facade yet.
// Publishing their DDI entry points with empty tables makes that decision explicit
// while preserving the implementation check for the loader-facing ABI.
EMPTY_TABLE(zeGetDeviceExpProcAddrTable, ze_device_exp_dditable_t)
EMPTY_TABLE(zeGetDriverExpProcAddrTable, ze_driver_exp_dditable_t)
EMPTY_TABLE(zeGetEventExpProcAddrTable, ze_event_exp_dditable_t)
EMPTY_TABLE(zeGetExecutableGraphProcAddrTable, ze_executable_graph_dditable_t)
EMPTY_TABLE(zeGetFabricEdgeExpProcAddrTable, ze_fabric_edge_exp_dditable_t)
EMPTY_TABLE(zeGetFabricVertexExpProcAddrTable, ze_fabric_vertex_exp_dditable_t)
EMPTY_TABLE(zeGetGraphProcAddrTable, ze_graph_dditable_t)
EMPTY_TABLE(zeGetImageExpProcAddrTable, ze_image_exp_dditable_t)
EMPTY_TABLE(zeGetRTASBuilderExpProcAddrTable, ze_rtas_builder_exp_dditable_t)
EMPTY_TABLE(zeGetRTASBuilderProcAddrTable, ze_rtas_builder_dditable_t)
EMPTY_TABLE(zeGetRTASParallelOperationExpProcAddrTable, ze_rtas_parallel_operation_exp_dditable_t)
EMPTY_TABLE(zeGetRTASParallelOperationProcAddrTable, ze_rtas_parallel_operation_dditable_t)

EMPTY_TABLE(zesGetDeviceExpProcAddrTable, zes_device_exp_dditable_t)
EMPTY_TABLE(zesGetDeviceProcAddrTable, zes_device_dditable_t)
EMPTY_TABLE(zesGetDiagnosticsProcAddrTable, zes_diagnostics_dditable_t)
EMPTY_TABLE(zesGetDriverExpProcAddrTable, zes_driver_exp_dditable_t)
EMPTY_TABLE(zesGetDriverProcAddrTable, zes_driver_dditable_t)
EMPTY_TABLE(zesGetEngineProcAddrTable, zes_engine_dditable_t)
EMPTY_TABLE(zesGetFabricPortProcAddrTable, zes_fabric_port_dditable_t)
EMPTY_TABLE(zesGetFanProcAddrTable, zes_fan_dditable_t)
EMPTY_TABLE(zesGetFirmwareExpProcAddrTable, zes_firmware_exp_dditable_t)
EMPTY_TABLE(zesGetFirmwareProcAddrTable, zes_firmware_dditable_t)
EMPTY_TABLE(zesGetFrequencyProcAddrTable, zes_frequency_dditable_t)
EMPTY_TABLE(zesGetGlobalProcAddrTable, zes_global_dditable_t)
EMPTY_TABLE(zesGetLedProcAddrTable, zes_led_dditable_t)
EMPTY_TABLE(zesGetMemoryProcAddrTable, zes_memory_dditable_t)
EMPTY_TABLE(zesGetOverclockProcAddrTable, zes_overclock_dditable_t)
EMPTY_TABLE(zesGetPerformanceFactorProcAddrTable, zes_performance_factor_dditable_t)
EMPTY_TABLE(zesGetPowerProcAddrTable, zes_power_dditable_t)
EMPTY_TABLE(zesGetPsuProcAddrTable, zes_psu_dditable_t)
EMPTY_TABLE(zesGetRasExpProcAddrTable, zes_ras_exp_dditable_t)
EMPTY_TABLE(zesGetRasProcAddrTable, zes_ras_dditable_t)
EMPTY_TABLE(zesGetSchedulerProcAddrTable, zes_scheduler_dditable_t)
EMPTY_TABLE(zesGetStandbyProcAddrTable, zes_standby_dditable_t)
EMPTY_TABLE(zesGetTemperatureProcAddrTable, zes_temperature_dditable_t)
EMPTY_TABLE(zesGetVFManagementExpProcAddrTable, zes_vf_management_exp_dditable_t)

EMPTY_TABLE(zetGetCommandListExpProcAddrTable, zet_command_list_exp_dditable_t)
EMPTY_TABLE(zetGetCommandListProcAddrTable, zet_command_list_dditable_t)
EMPTY_TABLE(zetGetContextProcAddrTable, zet_context_dditable_t)
EMPTY_TABLE(zetGetDebugProcAddrTable, zet_debug_dditable_t)
EMPTY_TABLE(zetGetDeviceExpProcAddrTable, zet_device_exp_dditable_t)
EMPTY_TABLE(zetGetDeviceProcAddrTable, zet_device_dditable_t)
EMPTY_TABLE(zetGetKernelProcAddrTable, zet_kernel_dditable_t)
EMPTY_TABLE(zetGetMetricDecoderExpProcAddrTable, zet_metric_decoder_exp_dditable_t)
EMPTY_TABLE(zetGetMetricExpProcAddrTable, zet_metric_exp_dditable_t)
EMPTY_TABLE(zetGetMetricGroupExpProcAddrTable, zet_metric_group_exp_dditable_t)
EMPTY_TABLE(zetGetMetricGroupProcAddrTable, zet_metric_group_dditable_t)
EMPTY_TABLE(zetGetMetricProcAddrTable, zet_metric_dditable_t)
EMPTY_TABLE(zetGetMetricProgrammableExpProcAddrTable, zet_metric_programmable_exp_dditable_t)
EMPTY_TABLE(zetGetMetricQueryPoolProcAddrTable, zet_metric_query_pool_dditable_t)
EMPTY_TABLE(zetGetMetricQueryProcAddrTable, zet_metric_query_dditable_t)
EMPTY_TABLE(zetGetMetricStreamerProcAddrTable, zet_metric_streamer_dditable_t)
EMPTY_TABLE(zetGetMetricTracerExpProcAddrTable, zet_metric_tracer_exp_dditable_t)
EMPTY_TABLE(zetGetModuleProcAddrTable, zet_module_dditable_t)
EMPTY_TABLE(zetGetTracerExpProcAddrTable, zet_tracer_exp_dditable_t)

#undef EMPTY_TABLE
#undef TABLE_END
#undef TABLE_BEGIN
