#include <algorithm>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <fstream>
#include <functional>
#include <iterator>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <new>
#include <optional>
#include <string>
#include <string_view>
#include <thread>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>

#include <unistd.h>

#include <ze_ddi.h>
#include <zes_ddi.h>
#include <zet_ddi.h>

#include "gemstone_execution_rpc.hpp"
#include "gemstone_levelzero_ddi.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/copy_out.hpp>
#include <gemstone/trace.hpp>

#include "client.hpp"
#include "completion.hpp"
#include "objects.hpp"
#include "process.hpp"
#include "relocations.hpp"
#include "transfer.hpp"
#include <sys/mman.h>
#include <sys/syscall.h>

namespace {

using namespace gemstone::execution;
using namespace gemstone::levelzero;

// Handle constructors can allocate or start threads; neither a failed
// allocation nor a throwing constructor may unwind through the C DDI.
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
Device globalDevice;
std::mutex allocationsMutex;
std::unordered_map<void *, AllocationRef> allocations;
std::mutex rpcTransactionMutex;

struct PhysicalMemory final : Handle {
  PhysicalMemory(Context *owner, int file, size_t bytes, ze_physical_mem_flags_t flagsValue)
      : Handle(Kind::PhysicalMemory), context(owner), fd(file), size(bytes), flags(flagsValue) {}
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
  uint64_t allocationId;
  std::map<uintptr_t, VirtualMapping> mappings;
};

std::mutex virtualMemoryMutex;
std::map<uintptr_t, VirtualReservation> virtualReservations;

struct RemoteExecution {
  ClientMode mode;
  Client *client;
  std::string error;
};

ClientManager remote;

struct LaunchPerformance {
  uint64_t relocationScanBytes = 0;
  uint64_t relocationCount = 0;
};

RemoteExecution remoteExecution() { return {remote.mode(), remote.client(), remote.error()}; }

std::string remoteError(const RemoteExecution &state) {
  const auto value = remote.lastError();
  if (!value.empty()) return value;
  return state.error.empty() ? "Gemstone execution RPC failed" : state.error;
}

void rememberRemoteError(RemoteExecution &state, std::string message) {
  state.error = std::move(message);
  if (gemstone::traceEnabled("levelzero")) std::fprintf(stderr, "Gemstone Level Zero: %s\n", state.error.c_str());
}

AllocationKind rpcKind(ze_memory_type_t kind) {
  switch (kind) {
    case ZE_MEMORY_TYPE_DEVICE: return AllocationKind::Device;
    case ZE_MEMORY_TYPE_SHARED: return AllocationKind::Shared;
    case ZE_MEMORY_TYPE_HOST: return AllocationKind::Host;
    default: return AllocationKind::Device;
  }
}

constexpr size_t PageSize = 4096U;
constexpr size_t MirrorChunkSize = 1U * 1024U * 1024U;
constexpr uint32_t MaxWorkItems = 1024;
constexpr uint32_t MaxWorkItemsZ = 64;
constexpr uint64_t DeviceMemorySize = 1ULL << 40;

AllocationRef findAllocationById(uint64_t id) {
  std::lock_guard lock(allocationsMutex);
  for (const auto &[_, allocation] : allocations)
    if (allocation->id == id) return allocation;
  return {};
}

bool classifyPointerValueLocked(uintptr_t pointer, uint64_t &allocationId, uint64_t &offset) {
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (pointer >= start && pointer < start + allocation->size) {
      allocationId = allocation->id;
      offset = pointer - start;
      return true;
    }
  }
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (pointer >= start && pointer - start == allocation->size) {
      allocationId = allocation->id;
      offset = allocation->size;
      return true;
    }
  }
  return false;
}

std::vector<PointerRelocation> collectPointerRelocations(const KernelLaunch &kernel, LaunchPerformance &performance) {
  std::vector<PointerRelocation> result;
  if (kernel.indirect) {
    std::lock_guard lock(allocationsMutex);
    for (const auto &[base, source] : allocations) {
      if (source->type != ZE_MEMORY_TYPE_DEVICE || source->size < sizeof(uintptr_t)) continue;
      performance.relocationScanBytes += source->size;
      const auto *bytes = static_cast<const uint8_t *>(base);
      for (size_t offset = 0; offset <= source->size - sizeof(uintptr_t); offset += sizeof(uint32_t)) {
        const auto pointer = gemstone::readNative<uintptr_t>(bytes, offset);
        uint64_t targetId = 0;
        uint64_t targetOffset = 0;
        if (classifyPointerValueLocked(pointer, targetId, targetOffset)) {
          result.push_back({RelocationSourceMemory{AllocationId{static_cast<int64_t>(source->id)}},
                            LongPositive0{static_cast<int64_t>(offset)}, AllocationId{static_cast<int64_t>(targetId)},
                            LongPositive0{static_cast<int64_t>(targetOffset)}});
        }
      }
    }
  }
  {
    std::lock_guard lock(allocationsMutex);
    for (size_t argument = 0; argument < kernel.arguments.size(); ++argument) {
      const auto &input = kernel.arguments[argument];
      if (input.local || input.bytes.size() < sizeof(uintptr_t)) continue;
      if (argument < kernel.parameters.size() && !kernel.parameters[argument].containsPointer) continue;
      performance.relocationScanBytes += input.bytes.size();
      for (size_t offset = 0; offset <= input.bytes.size() - sizeof(uintptr_t); offset += sizeof(uint32_t)) {
        const auto pointer = gemstone::readNative<uintptr_t>(input.bytes.data(), offset);
        uint64_t targetId = 0;
        uint64_t targetOffset = 0;
        if (classifyPointerValueLocked(pointer, targetId, targetOffset)) {
          result.push_back({RelocationSourceArgument{IntPositive0{static_cast<int32_t>(argument)}},
                            LongPositive0{static_cast<int64_t>(offset)}, AllocationId{static_cast<int64_t>(targetId)},
                            LongPositive0{static_cast<int64_t>(targetOffset)}});
        }
      }
    }
  }
  performance.relocationCount = result.size();
  return result;
}

AllocationRef retainAllocation(const void *pointer, size_t size) {
  if (!pointer) return {};
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::lock_guard lock(allocationsMutex);
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (address >= start && address - start <= allocation->size && size <= allocation->size - (address - start)) return allocation;
  }
  return {};
}

std::vector<AllocationRef> retainKernelAllocations(const Kernel &kernel) {
  std::lock_guard lock(allocationsMutex);
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
    for (size_t offset = 0; offset <= argument.bytes.size() - sizeof(uintptr_t); offset += sizeof(uint32_t)) {
      const auto pointer = gemstone::readNative<uintptr_t>(argument.bytes.data(), offset);
      uint64_t allocationId = 0;
      uint64_t allocationOffset = 0;
      if (classifyPointerValueLocked(pointer, allocationId, allocationOffset) && seen.insert(allocationId).second) {
        const auto found =
            std::find_if(allocations.begin(), allocations.end(), [&](const auto &entry) { return entry.second->id == allocationId; });
        if (found != allocations.end()) retained.push_back(found->second);
      }
    }
  }
  return retained;
}

template <class T, class H> T *cast(H handle, Kind expected) {
  auto *value = reinterpret_cast<T *>(handle);
  if (!value) return nullptr;
  return liveHandles.contains(value) && value->magic == gemstone::rpc::HandleMagic && value->kind == expected
                 && !value->destroyRequested.load(std::memory_order_acquire)
             ? value
             : nullptr;
}

ze_result_t checkVersion(ze_api_version_t version, const void *table) {
  if (!table) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (ZE_MAJOR_VERSION(version) != ZE_MAJOR_VERSION(ZE_API_VERSION_CURRENT)) return ZE_RESULT_ERROR_UNSUPPORTED_VERSION;
  return ZE_RESULT_SUCCESS;
}

ze_result_t waitEvent(Event *event, uint64_t timeout) {
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::unique_lock lock(event->mutex);
  if (event->signalled) return event->completionResult;
  if (timeout == 0) return ZE_RESULT_NOT_READY;
  if (timeout == std::numeric_limits<uint64_t>::max()) {
    event->changed.wait(lock, [&] { return event->signalled; });
    return event->completionResult;
  }
  return event->changed.wait_for(lock, std::chrono::nanoseconds(timeout), [&] { return event->signalled; }) ? event->completionResult
                                                                                                            : ZE_RESULT_NOT_READY;
}

// The caller holds event->mutex while publishing completion state.
void completeEventLocked(Event *event, ze_result_t result) {
  const auto now = std::chrono::steady_clock::now().time_since_epoch();
  event->kernelStart = static_cast<uint64_t>(std::chrono::duration_cast<std::chrono::nanoseconds>(now).count());
  event->kernelEnd = event->kernelStart + 1;
  event->completionResult = result;
  event->signalled = true;
}

ze_result_t completeEvent(Event *event, ze_result_t result) {
  if (!event) return result;
  {
    std::lock_guard lock(event->mutex);
    completeEventLocked(event, result);
  }
  event->changed.notify_all();
  return result;
}

ze_result_t signalEvent(Event *event) { return completeEvent(event, ZE_RESULT_SUCCESS); }

using EventUses = std::vector<HandleUse<Event>>;

ze_result_t retainEvents(uint32_t count, ze_event_handle_t *events, EventUses &retained) {
  if (count != 0 && !events) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  retained.reserve(count);
  for (uint32_t index = 0; index < count; ++index) {
    auto *event = cast<Event>(events[index], Kind::Event);
    if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    retained.emplace_back(event);
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t retainSignal(ze_event_handle_t handle, HandleUse<Event> &retained) {
  if (!handle) return ZE_RESULT_SUCCESS;
  auto *event = cast<Event>(handle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  retained = HandleUse<Event>(event);
  return ZE_RESULT_SUCCESS;
}

ze_result_t waitDependencies(const EventUses &events) {
  for (const auto &event : events) {
    const auto result = waitEvent(event.get(), std::numeric_limits<uint64_t>::max());
    if (result != ZE_RESULT_SUCCESS) return result;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t append(CommandList *list, CommandAction command, std::function<void()> release = {}, HandleUse<Event> signal = {},
                   std::function<void(ze_result_t)> cancel = {}) {
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  RecordedCommand recorded{std::move(command), std::move(release),
                           [signal = std::move(signal), cancel = std::move(cancel)](ze_result_t result) {
                             if (result != ZE_RESULT_SUCCESS && cancel) cancel(result);
                             (void)completeEvent(signal.get(), result);
                           }};
  if (list->immediate) {
    if (list->worker.joinable()) {
      {
        std::lock_guard lock(list->mutex);
        list->pending.push_back(std::move(recorded));
        ++list->submitted;
      }
      list->changed.notify_one();
      return ZE_RESULT_SUCCESS;
    }
    return recorded.execute();
  }
  if (list->closed) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  list->commands.push_back(std::move(recorded));
  return ZE_RESULT_SUCCESS;
}

ze_result_t retainCommandEvents(Context *context, ze_event_handle_t signalHandle, uint32_t waitCount, ze_event_handle_t *waitHandles,
                                HandleUse<Event> &signal, EventUses &waits) {
  const auto waitResult = retainEvents(waitCount, waitHandles, waits);
  if (waitResult != ZE_RESULT_SUCCESS) return waitResult;
  const auto signalResult = retainSignal(signalHandle, signal);
  if (signalResult != ZE_RESULT_SUCCESS) return signalResult;
  if ((signal && signal->pool->contextId != context->id)
      || std::any_of(waits.begin(), waits.end(),
                     [context](const HandleUse<Event> &event) { return event->pool->contextId != context->id; }))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL init(ze_init_flags_t) { return ZE_RESULT_SUCCESS; }

ze_result_t ZE_APICALL initDrivers(uint32_t *count, ze_driver_handle_t *drivers, ze_init_driver_type_desc_t *desc) {
  if (!count || !desc) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
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
  properties->driverVersion = 1;
  std::memset(properties->uuid.id, 0, ZE_MAX_DRIVER_UUID_SIZE);
  std::memcpy(properties->uuid.id, "GEMSTONE", 8);
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
  if (!properties) {
    *count = 2;
  } else {
    const uint32_t capacity = *count;
    if (capacity > 0) {
      std::strncpy(properties[0].name, ZE_MODULE_PROGRAM_EXP_NAME, ZE_MAX_EXTENSION_NAME - 1);
      properties[0].name[ZE_MAX_EXTENSION_NAME - 1] = '\0';
      properties[0].version = ZE_MODULE_PROGRAM_EXP_VERSION_CURRENT;
    }
    if (capacity > 1) {
      std::strncpy(properties[1].name, ZE_GLOBAL_OFFSET_EXP_NAME, ZE_MAX_EXTENSION_NAME - 1);
      properties[1].name[ZE_MAX_EXTENSION_NAME - 1] = '\0';
      properties[1].version = ZE_GLOBAL_OFFSET_EXP_VERSION_CURRENT;
    }
    *count = std::min(capacity, 2U);
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverImportExternalPointer(ze_driver_handle_t handle, void *pointer, size_t size) {
  if (cast<Driver>(handle, Kind::Driver) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer || size == 0) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  // Gemstone executes against a host-backed mirror, so external host memory
  // does not need a second device registration. Allocations made through the
  // Level Zero memory API are already tracked by the mirror layer.
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driverReleaseImportedPointer(ze_driver_handle_t handle, void *pointer) {
  if (cast<Driver>(handle, Kind::Driver) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
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
  if (cast<Driver>(handle, Kind::Driver) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!description) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  static thread_local std::string text;
  std::lock_guard rpcLock(rpcTransactionMutex);
  text = remoteError(remoteExecution());
  *description = text.c_str();
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceGet(ze_driver_handle_t, uint32_t *count, ze_device_handle_t *devices) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!devices) {
    *count = 1;
  } else if (*count != 0) {
    devices[0] = reinterpret_cast<ze_device_handle_t>(&globalDevice);
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceSubdevices(ze_device_handle_t, uint32_t *count, ze_device_handle_t *) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *count = 0;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceProperties(ze_device_handle_t, ze_device_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto stype = properties->stype;
  const auto next = properties->pNext;
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
  properties->maxCommandQueuePriority = 0;
  properties->numThreadsPerEU = 8;
  properties->physicalEUSimdWidth = 8;
  properties->numEUsPerSubslice = 8;
  properties->numSubslicesPerSlice = 8;
  properties->numSlices = 1;
  properties->timerResolution = stype == ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES_1_2 ? 1000000000ULL : 1ULL;
  properties->timestampValidBits = 64;
  properties->kernelTimestampValidBits = 64;
  std::memcpy(properties->uuid.id, "GEMSTONE-SPIRV", 14);
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
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceQueueGroups(ze_device_handle_t, uint32_t *count, ze_command_queue_group_properties_t *properties) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!properties) {
    *count = 1;
  } else if (*count != 0) {
    properties[0].flags = ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COMPUTE | ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COPY
                          | ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COOPERATIVE_KERNELS;
    properties[0].maxMemoryFillPatternSize = 128;
    properties[0].numQueues = 1;
    *count = 1;
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
  if (!properties) *count = 1;
  else if (*count != 0) {
    properties[0].flags = ZE_DEVICE_CACHE_PROPERTY_FLAG_USER_CONTROL;
    properties[0].cacheSize = 4 * 1024 * 1024;
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceStatus(ze_device_handle_t) { return ZE_RESULT_SUCCESS; }
ze_result_t ZE_APICALL deviceTimestamps(ze_device_handle_t, uint64_t *host, uint64_t *device) {
  if (!host || !device) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto now = std::chrono::steady_clock::now().time_since_epoch();
  *host = static_cast<uint64_t>(std::chrono::duration_cast<std::chrono::nanoseconds>(now).count());
  *device = *host;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL devicePciProperties(ze_device_handle_t handle, ze_pci_ext_properties_t *properties) {
  if (cast<Device>(handle, Kind::Device) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->address = {0, 0, 1, 0};
  properties->maxSpeed = {-1, -1, -1};
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL contextCreate(ze_driver_handle_t driver, const ze_context_desc_t *desc, ze_context_handle_t *result) {
  if (cast<Driver>(driver, Kind::Driver) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *created = createHandle<Context>();
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_context_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL contextCreateEx(ze_driver_handle_t driver, const ze_context_desc_t *desc, uint32_t count,
                                       ze_device_handle_t *devices, ze_context_handle_t *result) {
  if (!devices && count != 0) return ZE_RESULT_ERROR_INVALID_SIZE;
  for (uint32_t index = 0; index < count; ++index)
    if (cast<Device>(devices[index], Kind::Device) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return contextCreate(driver, desc, result);
}
ze_result_t ZE_APICALL contextDestroy(ze_context_handle_t handle) { return destroyHandle(cast<Context>(handle, Kind::Context)); }
ze_result_t ZE_APICALL contextStatus(ze_context_handle_t context) {
  return cast<Context>(context, Kind::Context) == nullptr ? ZE_RESULT_ERROR_INVALID_NULL_HANDLE : ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL contextMemory(ze_context_handle_t context, ze_device_handle_t device, void *, size_t) {
  return cast<Context>(context, Kind::Context) == nullptr || cast<Device>(device, Kind::Device) == nullptr
             ? ZE_RESULT_ERROR_INVALID_NULL_HANDLE
             : ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL queueCreate(ze_context_handle_t context, ze_device_handle_t device, const ze_command_queue_desc_t *desc,
                                   ze_command_queue_handle_t *result) {
  auto *owner = cast<Context>(context, Kind::Context);
  auto *target = cast<Device>(device, Kind::Device);
  if (!owner || !target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (desc->mode != ZE_COMMAND_QUEUE_MODE_DEFAULT && desc->mode != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS
      && desc->mode != ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  auto *created = createHandle<Queue>(owner, target, *desc);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_command_queue_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL queueDestroy(ze_command_queue_handle_t handle) {
  auto *queue = cast<Queue>(handle, Kind::Queue);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (queue->worker.joinable()) {
    std::unique_lock lock(queue->mutex);
    if (!queue->changed.wait_for(lock, std::chrono::seconds(1), [&] { return queue->completed == queue->submitted; }))
      return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  }
  return destroyHandle(queue);
}
ze_result_t ZE_APICALL queueExecute(ze_command_queue_handle_t queueHandle, uint32_t count, ze_command_list_handle_t *handles,
                                    ze_fence_handle_t fenceHandle) {
  auto *queue = cast<Queue>(queueHandle, Kind::Queue);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (count != 0 && !handles) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<HandleUse<CommandList>> lists;
  lists.reserve(count);
  for (uint32_t index = 0; index < count; ++index) {
    auto *list = cast<CommandList>(handles[index], Kind::List);
    if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (list->context.get() != queue->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    lists.emplace_back(list);
  }
  HandleUse<Fence> fence;
  if (fenceHandle) {
    auto *value = cast<Fence>(fenceHandle, Kind::Fence);
    if (!value) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (value->queue.get() != queue) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    fence = HandleUse<Fence>(value);
    std::lock_guard lock(fence->event.mutex);
    fence->pending = true;
  }
  std::function<ze_result_t()> submission = [lists = std::move(lists), fence = std::move(fence)]() mutable {
    ze_result_t result = ZE_RESULT_SUCCESS;
    for (const auto &retained : lists)
      for (auto &command : retained->commands)
        result = command.execute(result);
    if (fence) {
      {
        std::lock_guard lock(fence->event.mutex);
        // A successful list reset can immediately be followed by fence reset
        // or destruction. Publish completion under the same lock as releasing
        // list uses so those calls cannot observe a housekeeping-only pending
        // submission or race a delayed signal from its previous use.
        lists.clear();
        fence->completionResult = result;
        fence->pending = false;
        completeEventLocked(&fence->event, ZE_RESULT_SUCCESS);
      }
      fence->event.changed.notify_all();
    } else {
      lists.clear();
    }
    return result;
  };
  if (queue->worker.joinable()) {
    {
      std::lock_guard lock(queue->mutex);
      queue->pending.push_back(std::move(submission));
      ++queue->submitted;
    }
    queue->changed.notify_one();
    return ZE_RESULT_SUCCESS;
  }
  return submission();
}
ze_result_t ZE_APICALL queueSync(ze_command_queue_handle_t handle, uint64_t timeout) {
  auto *queue = cast<Queue>(handle, Kind::Queue);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return queue->waitFor(timeout);
}

ze_result_t ZE_APICALL listCreate(ze_context_handle_t context, ze_device_handle_t device, const ze_command_list_desc_t *,
                                  ze_command_list_handle_t *result) {
  auto *owner = cast<Context>(context, Kind::Context);
  auto *target = cast<Device>(device, Kind::Device);
  if (!owner || !target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *created = createHandle<CommandList>(false, owner, target);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_command_list_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL listCreateImmediate(ze_context_handle_t context, ze_device_handle_t device, const ze_command_queue_desc_t *desc,
                                           ze_command_list_handle_t *result) {
  auto *owner = cast<Context>(context, Kind::Context);
  auto *target = cast<Device>(device, Kind::Device);
  if (!owner || !target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (desc->mode != ZE_COMMAND_QUEUE_MODE_DEFAULT && desc->mode != ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS
      && desc->mode != ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  auto *created = createHandle<CommandList>(true, owner, target, desc->mode);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_command_list_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
bool listUsesReleased(CommandList *list) {
  std::unique_lock lock(list->usersMutex);
  return list->usersChanged.wait_for(lock, std::chrono::seconds(1), [&] { return list->users.load(std::memory_order_acquire) == 0; });
}
ze_result_t ZE_APICALL listDestroy(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return listUsesReleased(list) ? destroyHandle(list) : ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
}
ze_result_t ZE_APICALL listClose(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  list->closed = true;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL listReset(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!listUsesReleased(list)) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  list->commands.clear();
  list->closed = false;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL listBarrier(ze_command_list_handle_t handle, ze_event_handle_t signal, uint32_t waitCount,
                                   ze_event_handle_t *waits) {
  HandleUse<Event> retainedSignal;
  EventUses retainedWaits;
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  return append(
      list,
      [waits = std::move(retainedWaits)] {
        auto result = waitDependencies(waits);
        return result;
      },
      {}, retainedSignal);
}

ze_result_t listCopyInContext(ze_command_list_handle_t handle, void *destination, Context *sourceContext, const void *source, size_t size,
                              ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!sourceContext) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if ((!destination || !source) && size != 0) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (size > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(destination)
      || size > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(source))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  HandleUse<Event> retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto destinationAllocation = retainAllocation(destination, size);
  auto sourceAllocation = retainAllocation(source, size);
  // Only the source context is named by AppendMemoryCopyFromContext. The
  // destination may live in another context because every allocation is
  // host-accessible here, and the CTS exercises exactly that.
  if (sourceAllocation && sourceAllocation->context.get() != sourceContext) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  // A failed full-range lookup must not turn a tracked out-of-bounds pointer into a host pointer.
  if ((!destinationAllocation && retainAllocation(destination, 0)) || (!sourceAllocation && retainAllocation(source, 0)))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>();
  if (destinationAllocation) allocationLifetime->push_back(std::move(destinationAllocation));
  if (sourceAllocation) allocationLifetime->push_back(std::move(sourceAllocation));
  return append(
      list,
      [=, waits = std::move(retainedWaits)] {
        (void)allocationLifetime;
        auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS && size != 0) std::memmove(destination, source, size);
        return result;
      },
      [allocationLifetime] { allocationLifetime->clear(); }, retainedSignal);
}

ze_result_t ZE_APICALL listCopy(ze_command_list_handle_t handle, void *destination, const void *source, size_t size,
                                ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return listCopyInContext(handle, destination, list->context.get(), source, size, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listCopyFromContext(ze_command_list_handle_t handle, void *destination, ze_context_handle_t sourceContextHandle,
                                           const void *source, size_t size, ze_event_handle_t signal, uint32_t waitCount,
                                           ze_event_handle_t *waits) {
  return listCopyInContext(handle, destination, cast<Context>(sourceContextHandle, Kind::Context), source, size, signal, waitCount, waits);
}

ze_result_t ZE_APICALL listCopyRegion(ze_command_list_handle_t handle, void *destination, const ze_copy_region_t *destinationRegion,
                                      uint32_t destinationPitch, uint32_t destinationSlice, const void *source,
                                      const ze_copy_region_t *sourceRegion, uint32_t sourcePitch, uint32_t sourceSlice,
                                      ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!destination || !source || !destinationRegion || !sourceRegion) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto dst = *destinationRegion;
  const auto src = *sourceRegion;
  if (dst.width == 0 || dst.height == 0 || dst.width != src.width || dst.height != src.height || dst.depth != src.depth)
    return ZE_RESULT_ERROR_INVALID_SIZE;
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
  const auto layout = [&](const ze_copy_region_t &region, uint32_t pitch, uint32_t slicePitch, Layout &out) {
    out.row = pitch == 0 ? region.width : pitch;
    size_t endX, endY, endZ, minimumSlice, startY, startZ, endRow, endSlice;
    if (!add(region.originX, region.width, endX) || endX > out.row || !add(region.originY, region.height, endY)
        || !multiply(out.row, endY, minimumSlice))
      return false;
    out.slice = region.depth == 0 ? 0 : (slicePitch == 0 ? minimumSlice : slicePitch);
    if (depth > 1 && out.slice < minimumSlice) return false;
    const size_t originZ = region.depth == 0 ? 0 : region.originZ;
    if (!add(originZ, depth - 1, endZ) || !multiply(region.originY, out.row, startY) || !multiply(originZ, out.slice, startZ)
        || !add(startY, startZ, out.offset) || !add(out.offset, region.originX, out.offset) || !multiply(endY - 1, out.row, endRow)
        || !multiply(endZ, out.slice, endSlice) || !add(endRow, endSlice, out.span) || !add(out.span, endX, out.span))
      return false;
    return true;
  };
  Layout sourceLayout{}, destinationLayout{};
  if (!layout(src, sourcePitch, sourceSlice, sourceLayout) || !layout(dst, destinationPitch, destinationSlice, destinationLayout))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  const auto sourceAddress = reinterpret_cast<uintptr_t>(source);
  const auto destinationAddress = reinterpret_cast<uintptr_t>(destination);
  if (sourceLayout.span > std::numeric_limits<uintptr_t>::max() - sourceAddress
      || destinationLayout.span > std::numeric_limits<uintptr_t>::max() - destinationAddress)
    return ZE_RESULT_ERROR_INVALID_SIZE;
  auto sourceAllocation = retainAllocation(source, 0);
  auto destinationAllocation = retainAllocation(destination, 0);
  if ((sourceAllocation && sourceAllocation->context.get() != list->context.get())
      || (destinationAllocation && destinationAllocation->context.get() != list->context.get()))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if ((sourceAllocation && !retainAllocation(source, sourceLayout.span))
      || (destinationAllocation && !retainAllocation(destination, destinationLayout.span)))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  size_t rows;
  if (!multiply(src.height, depth, rows)) return ZE_RESULT_ERROR_INVALID_SIZE;
  const auto rowOffset = [height = size_t(src.height)](const Layout &value, size_t row) {
    return value.offset + (row / height) * value.slice + (row % height) * value.row;
  };
  // Test actual row intervals, not their enclosing spans: disjoint columns may share a pitched allocation.
  size_t sourceRow = 0, destinationRow = 0;
  while (sourceRow < rows && destinationRow < rows) {
    const auto a = sourceAddress + rowOffset(sourceLayout, sourceRow);
    const auto b = destinationAddress + rowOffset(destinationLayout, destinationRow);
    if (a < b + src.width && b < a + src.width) return ZE_RESULT_ERROR_OVERLAPPING_REGIONS;
    if (a < b) ++sourceRow;
    else ++destinationRow;
  }
  HandleUse<Event> retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto lifetime = std::make_shared<std::vector<AllocationRef>>();
  if (sourceAllocation) lifetime->push_back(std::move(sourceAllocation));
  if (destinationAllocation) lifetime->push_back(std::move(destinationAllocation));
  return append(
      list,
      [=, waits = std::move(retainedWaits)] {
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
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!destination || !pattern || patternSize == 0) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (size > std::numeric_limits<uintptr_t>::max() - reinterpret_cast<uintptr_t>(destination)) return ZE_RESULT_ERROR_INVALID_SIZE;
  auto destinationAllocation = retainAllocation(destination, size);
  if (destinationAllocation && destinationAllocation->context.get() != list->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (!destinationAllocation && retainAllocation(destination, 0)) return ZE_RESULT_ERROR_INVALID_SIZE;
  std::vector<uint8_t> copiedPattern(static_cast<const uint8_t *>(pattern), static_cast<const uint8_t *>(pattern) + patternSize);
  HandleUse<Event> retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>();
  if (destinationAllocation) allocationLifetime->push_back(std::move(destinationAllocation));
  return append(
      list,
      [=, waits = std::move(retainedWaits)] {
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
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto allocation = retainAllocation(pointer, size);
  if (allocation && allocation->context.get() != list->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (!allocation && retainAllocation(pointer, 0)) return ZE_RESULT_ERROR_INVALID_SIZE;
  return append(list, [] { return ZE_RESULT_SUCCESS; }, [allocation = std::move(allocation)]() mutable { allocation.reset(); });
}

ze_result_t ZE_APICALL listAdvise(ze_command_list_handle_t handle, ze_device_handle_t deviceHandle, const void *pointer, size_t size,
                                  ze_memory_advice_t advice) {
  auto *list = cast<CommandList>(handle, Kind::List);
  auto *device = cast<Device>(deviceHandle, Kind::Device);
  if (!list || !device) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (advice < ZE_MEMORY_ADVICE_SET_READ_MOSTLY || advice > ZE_MEMORY_ADVICE_CLEAR_SYSTEM_MEMORY_PREFERRED_LOCATION)
    return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  auto allocation = retainAllocation(pointer, size);
  if (allocation && allocation->context.get() != list->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (!allocation && retainAllocation(pointer, 0)) return ZE_RESULT_ERROR_INVALID_SIZE;
  return append(list, [] { return ZE_RESULT_SUCCESS; }, [allocation = std::move(allocation)]() mutable { allocation.reset(); });
}

ze_result_t ZE_APICALL listSignal(ze_command_list_handle_t handle, ze_event_handle_t signal) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  HandleUse<Event> retainedSignal;
  const auto retained = retainSignal(signal, retainedSignal);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  if (retainedSignal && retainedSignal->pool->contextId != list->context->id) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return append(list, [] { return ZE_RESULT_SUCCESS; }, {}, retainedSignal);
}
ze_result_t ZE_APICALL listWait(ze_command_list_handle_t handle, uint32_t count, ze_event_handle_t *events) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  EventUses retainedEvents;
  const auto retained = retainEvents(count, events, retainedEvents);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  if (std::ranges::any_of(retainedEvents, [list](const HandleUse<Event> &event) { return event->pool->contextId != list->context->id; }))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return append(list, [events = std::move(retainedEvents)] { return waitDependencies(events); });
}
ze_result_t ZE_APICALL listResetEvent(ze_command_list_handle_t handle, ze_event_handle_t eventHandle) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  auto *event = cast<Event>(eventHandle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (event->pool->contextId != list->context->id) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return append(list, [event = HandleUse<Event>(event)] {
    std::lock_guard lock(event->mutex);
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
  std::lock_guard lock(allocationsMutex);
  for (const auto &[base, allocation] : allocations) {
    if (allocation->type != ZE_MEMORY_TYPE_DEVICE) continue;
    if (!kernel.indirect && !reachable.contains(allocation->id)) continue;
    if (!transferAllocation(client, AllocationId{static_cast<int64_t>(allocation->id)}, 0, base, allocation->size, true, MirrorChunkSize))
      return false;
  }
  return true;
}

bool copyBackDirtyRanges(Client &client, const std::vector<DirtyRange> &ranges) {
  std::lock_guard lock(allocationsMutex);
  return readDirtyRanges(client, ranges, [&](const DirtyRange &range, const uint8_t *bytes) {
    const auto id = static_cast<uint64_t>(range.allocation.value);
    const auto offset = static_cast<uint64_t>(range.offset.value);
    const auto size = static_cast<uint64_t>(range.size.value);
    const auto found = std::find_if(allocations.begin(), allocations.end(), [&](const auto &entry) { return entry.second->id == id; });
    if (found == allocations.end() || offset > found->second->size || size > found->second->size - offset) return false;
    std::memcpy(static_cast<uint8_t *>(found->first) + offset, bytes, size);
    return true;
  });
}

ze_result_t launchRpc(const KernelLaunch &kernel, const ze_group_count_t &groups) {
  // Serialize metadata preparation and copy-back, but let independent queues
  // execute and pump host-memory exchanges on separate connections.
  std::unique_lock rpcLock(rpcTransactionMutex);
  auto state = remoteExecution();
  if (state.mode == ClientMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (state.mode != ClientMode::Ready) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  // Ordinary host pointers are serviced by exact interpreter accesses.
  if (!state.client->call(EnableHostMemory{})) return ZE_RESULT_ERROR_DEVICE_LOST;
  const auto started = std::chrono::steady_clock::now();
  LaunchPerformance performance;
  const auto relocations = collectPointerRelocations(kernel, performance);
  if (!uploadAllocations(*state.client, kernel, relocations, {})) {
    rememberRemoteError(state, remoteError(state));
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }

  std::vector<gemstone::execution::Argument> arguments;
  arguments.reserve(kernel.arguments.size());
  for (size_t index = 0; index < kernel.arguments.size(); ++index) {
    const auto &input = kernel.arguments[index];
    if (input.local) {
      arguments.emplace_back(ArgumentLocal{static_cast<int64_t>(input.localSize)});
      continue;
    }
    const bool pointerAggregate = index < kernel.parameters.size() && kernel.parameters[index].pointerAggregate;
    const auto directPointer =
        !pointerAggregate && input.bytes.size() == sizeof(uintptr_t)
            ? std::find_if(relocations.begin(), relocations.end(),
                           [index](const auto &relocation) {
                             const auto *source = std::get_if<RelocationSourceArgument>(&relocation.source);
                             return source && static_cast<size_t>(source->index.value) == index && relocation.sourceOffset.value == 0;
                           })
            : relocations.end();
    if (directPointer != relocations.end()) {
      arguments.emplace_back(ArgumentMemory{directPointer->target, directPointer->targetOffset});
    } else {
      arguments.emplace_back(ArgumentScalar{input.bytes});
    }
  }

  const Dimensions groupSize{static_cast<int32_t>(kernel.groupX), static_cast<int32_t>(kernel.groupY), static_cast<int32_t>(kernel.groupZ)};
  const Dimensions groupCount{static_cast<int32_t>(groups.groupCountX), static_cast<int32_t>(groups.groupCountY),
                              static_cast<int32_t>(groups.groupCountZ)};
  const auto globalX = static_cast<uint64_t>(groupSize.x.value) * groupCount.x.value;
  const auto globalY = static_cast<uint64_t>(groupSize.y.value) * groupCount.y.value;
  const auto globalZ = static_cast<uint64_t>(groupSize.z.value) * groupCount.z.value;
  if (globalX > static_cast<uint64_t>(std::numeric_limits<int64_t>::max())
      || globalY > static_cast<uint64_t>(std::numeric_limits<int64_t>::max())
      || globalZ > static_cast<uint64_t>(std::numeric_limits<int64_t>::max()))
    return ZE_RESULT_ERROR_INVALID_GROUP_SIZE_DIMENSION;
  const GlobalDimensions globalSize{static_cast<int64_t>(globalX), static_cast<int64_t>(globalY), static_cast<int64_t>(globalZ)};
  std::vector<PointerRelocation> launchRelocations;
  launchRelocations.reserve(relocations.size());
  for (const auto &relocation : relocations) {
    const auto *source = std::get_if<RelocationSourceArgument>(&relocation.source);
    const bool directPointer =
        source && relocation.sourceOffset.value == 0 && static_cast<size_t>(source->index.value) < kernel.arguments.size()
        && !(static_cast<size_t>(source->index.value) < kernel.parameters.size() && kernel.parameters[source->index.value].pointerAggregate)
        && kernel.arguments[source->index.value].bytes.size() == sizeof(uintptr_t);
    if (!directPointer) launchRelocations.push_back(relocation);
  }
  const Coordinates offset{static_cast<int32_t>(kernel.offsetX), static_cast<int32_t>(kernel.offsetY),
                           static_cast<int32_t>(kernel.offsetZ)};
  auto launched = state.client->call(Launch{ProgramId{static_cast<int64_t>(kernel.programId)}, kernel.name, groupSize, groupCount,
                                            globalSize, 3, offset, 0, 0, std::move(arguments), std::move(launchRelocations)});
  if (!launched) {
    rememberRemoteError(state, launched.error().detail);
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }

  // A command-list worker must not invent a kernel-duration limit. User-facing
  // event/fence/queue synchronization owns the Level Zero timeout semantics;
  // each RPC exchange still retains its independent transport-progress timeout.
  gemstone::execution::CompletionResult result;
  std::string completionError;
  auto completionClient = Client::fromEnvironment();
  rpcLock.unlock();
  const bool collected =
      completionClient && gemstone::execution::collect_completion(*completionClient, launched.value(), result, completionError, true);
  rpcLock.lock();
  if (!collected) {
    rememberRemoteError(state, completionError);
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  {
    std::string relocationError;
    const bool applied = applyPointerRelocations(
        result.pointer_relocations,
        [](uint64_t id) -> RelocationTarget {
          const auto allocation = findAllocationById(id);
          return allocation ? RelocationTarget{allocation->base, allocation->size} : RelocationTarget{};
        },
        [](uint64_t, uint64_t, uint64_t, uint64_t) {}, relocationError);
    if (!applied) {
      rememberRemoteError(state, relocationError);
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
  }
  if (!copyBackDirtyRanges(*state.client, result.dirty_ranges)) {
    rememberRemoteError(state, remoteError(state));
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  if (gemstone::traceEnabled("perf")) {
    const auto elapsed = std::chrono::duration_cast<std::chrono::microseconds>(std::chrono::steady_clock::now() - started).count();
    std::fprintf(stderr, "Gemstone Level Zero perf: kernel=%s scan=%llu relocations=%llu elapsed_us=%lld\n", kernel.name.c_str(),
                 static_cast<unsigned long long>(performance.relocationScanBytes),
                 static_cast<unsigned long long>(performance.relocationCount), static_cast<long long>(elapsed));
  }
  return ZE_RESULT_SUCCESS;
}

KernelLaunch snapshotLaunch(Kernel *kernel) {
  auto lifetime = std::make_shared<HandleUse<Kernel>>(kernel);
  return KernelLaunch{lifetime,
                      kernel->module->context.get(),
                      kernel->module->programId,
                      kernel->name,
                      kernel->groupX,
                      kernel->groupY,
                      kernel->groupZ,
                      kernel->offsetX,
                      kernel->offsetY,
                      kernel->offsetZ,
                      kernel->indirect != 0,
                      kernel->arguments,
                      kernel->parameters,
                      retainKernelAllocations(*kernel)};
}

ze_result_t recordLaunch(ze_command_list_handle_t handle, ze_kernel_handle_t kernelHandle, const ze_group_count_t *groups,
                         bool indirectGroups, ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *kernel = cast<Kernel>(kernelHandle, Kind::Kernel);
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!kernel || !list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!groups) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto copiedGroups = indirectGroups ? ze_group_count_t{} : *groups;
  if (gemstone::traceEnabled("levelzero") && static_cast<uint64_t>(kernel->groupX) * copiedGroups.groupCountX > 1000)
    std::fprintf(stderr, "Gemstone Level Zero: appending %s with group %ux%ux%u count %ux%ux%u\n", kernel->name.c_str(), kernel->groupX,
                 kernel->groupY, kernel->groupZ, copiedGroups.groupCountX, copiedGroups.groupCountY, copiedGroups.groupCountZ);
  HandleUse<Event> retainedSignal;
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
      [launch = std::move(launch), copiedGroups, groups, indirectGroups, waits = std::move(retainedWaits), allocationLifetime] {
        (void)allocationLifetime;
        auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS) result = launchRpc(launch, indirectGroups ? *groups : copiedGroups);
        return result;
      },
      [allocationLifetime, kernelLifetime] {
        allocationLifetime->clear();
        *kernelLifetime = {};
      },
      retainedSignal);
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
  auto *target = cast<CommandList>(handle, Kind::List);
  if (!target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (count == 0 || !lists) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<HandleUse<CommandList>> sources;
  sources.reserve(count);
  for (uint32_t index = 0; index < count; ++index) {
    auto *source = cast<CommandList>(lists[index], Kind::List);
    if (!source) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    sources.emplace_back(source);
  }
  HandleUse<Event> retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(target->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  // The immediate list adopts the recorded commands; cancellation must also
  // complete child events when an earlier immediate command has failed.
  auto commands = std::make_shared<std::vector<HandleUse<CommandList>>>(std::move(sources));
  const auto execute = [commands](ze_result_t result) {
    for (const auto &source : *commands)
      for (auto &command : source->commands)
        result = command.execute(result);
    commands->clear();
    return result;
  };
  return append(
      target,
      [execute, waits = std::move(retainedWaits)] {
        const auto result = execute(waitDependencies(waits));
        return result;
      },
      {}, retainedSignal, [execute](ze_result_t result) { (void)execute(result); });
}

ze_result_t ZE_APICALL listLaunchMultipleIndirect(ze_command_list_handle_t handle, uint32_t maxCount, ze_kernel_handle_t *handles,
                                                  const uint32_t *count, const ze_group_count_t *groups, ze_event_handle_t signal,
                                                  uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!handles || !count || !groups) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<KernelLaunch> launches;
  launches.reserve(maxCount);
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>();
  for (uint32_t index = 0; index < maxCount; ++index) {
    auto *kernel = cast<Kernel>(handles[index], Kind::Kernel);
    if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (kernel->module->context.get() != list->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    launches.push_back(snapshotLaunch(kernel));
    for (auto &allocation : launches.back().allocations)
      allocationLifetime->push_back(std::move(allocation));
    launches.back().allocations.clear();
  }
  if (auto allocation = retainAllocation(count, sizeof(*count))) allocationLifetime->push_back(std::move(allocation));
  if (auto allocation = retainAllocation(groups, maxCount * sizeof(*groups))) allocationLifetime->push_back(std::move(allocation));
  HandleUse<Event> retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto lifetimes = std::make_shared<std::vector<std::shared_ptr<HandleUse<Kernel>>>>();
  for (const auto &launch : launches)
    lifetimes->push_back(launch.lifetime);
  return append(
      list,
      [launches = std::move(launches), count, groups, maxCount, waits = std::move(retainedWaits), allocationLifetime] {
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
      retainedSignal);
}

// UMF uses the reported allocation page size as its minimum slab alignment.
// Keep the allocator and zeMemGetAllocProperties behaviour in agreement.

void *alignedAllocate(size_t size, size_t alignment) {
  const size_t actualAlignment = std::max(alignment, PageSize);
  void *value = nullptr;
  return posix_memalign(&value, actualAlignment, std::max(size, size_t{1})) == 0 ? value : nullptr;
}

ze_result_t allocate(Context *context, size_t size, size_t alignment, ze_memory_type_t type, void **result) {
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (alignment != 0 && (alignment & (alignment - 1)) != 0) return ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT;
  auto state = remoteExecution();
  if (state.mode == ClientMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (state.mode == ClientMode::Ready && size > static_cast<size_t>(std::numeric_limits<int32_t>::max()))
    return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  *result = alignedAllocate(size, alignment);
  if (!*result) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  // Device allocations are uninitialized by the Level Zero specification, but
  // deterministic zero-fill keeps empty-range kernels observable and mirrors
  // the fresh allocation behaviour of the physical oracle used by the suite.
  std::memset(*result, 0, std::max(size, size_t{1}));
  uint64_t allocationId = 0;
  if (state.mode == ClientMode::Ready) {
    std::lock_guard rpcLock(rpcTransactionMutex);
    auto registered = state.client->call(RegisterAllocation{rpcKind(type), static_cast<int64_t>(size)});
    if (!registered) {
      rememberRemoteError(state, remoteError(state));
      std::free(*result);
      *result = nullptr;
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
    allocationId = static_cast<uint64_t>(registered.value().value);
    if (!state.client->call(RegisterAllocationAddress{registered.value(),
                                                      GuestAddress{static_cast<int64_t>(reinterpret_cast<uintptr_t>(*result))}, true})) {
      (void)state.client->call(ReleaseAllocation{registered.value()});
      std::free(*result);
      *result = nullptr;
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
  }
  std::lock_guard lock(allocationsMutex);
  allocations.emplace(*result, std::make_shared<Allocation>(Allocation{HandleUse<Context>(context), *result, size, type, allocationId}));
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL memShared(ze_context_handle_t context, const ze_device_mem_alloc_desc_t *, const ze_host_mem_alloc_desc_t *,
                                 size_t size, size_t alignment, ze_device_handle_t, void **result) {
  return allocate(cast<Context>(context, Kind::Context), size, alignment, ZE_MEMORY_TYPE_SHARED, result);
}
ze_result_t ZE_APICALL memDevice(ze_context_handle_t context, const ze_device_mem_alloc_desc_t *, size_t size, size_t alignment,
                                 ze_device_handle_t, void **result) {
  return allocate(cast<Context>(context, Kind::Context), size, alignment, ZE_MEMORY_TYPE_DEVICE, result);
}
ze_result_t ZE_APICALL memHost(ze_context_handle_t context, const ze_host_mem_alloc_desc_t *, size_t size, size_t alignment,
                               void **result) {
  return allocate(cast<Context>(context, Kind::Context), size, alignment, ZE_MEMORY_TYPE_HOST, result);
}
ze_result_t ZE_APICALL memFree(ze_context_handle_t contextHandle, void *pointer) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_SUCCESS;
  {
    std::lock_guard lock(virtualMemoryMutex);
    if (virtualReservations.contains(reinterpret_cast<uintptr_t>(pointer))) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  }
  AllocationRef allocation;
  {
    std::lock_guard lock(allocationsMutex);
    const auto found = allocations.find(pointer);
    if (found == allocations.end()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    if (found->second->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    if (found->second.use_count() != 1) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
    allocation = found->second;
    allocations.erase(found);
  }
  auto state = remoteExecution();
  std::lock_guard rpcLock(rpcTransactionMutex);
  const bool released = state.mode != ClientMode::Ready
                        || static_cast<bool>(state.client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(allocation->id)}}));
  if (!released) rememberRemoteError(state, remoteError(state));
  std::free(pointer);
  return released ? ZE_RESULT_SUCCESS : ZE_RESULT_ERROR_DEVICE_LOST;
}
ze_result_t ZE_APICALL memFreeExt(ze_context_handle_t context, const ze_memory_free_ext_desc_t *desc, void *pointer) {
  if (cast<Context>(context, Kind::Context) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  constexpr auto supportedPolicies = ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_BLOCKING_FREE | ZE_DRIVER_MEMORY_FREE_POLICY_EXT_FLAG_DEFER_FREE;
  if ((desc->freePolicy & ~supportedPolicies) != 0) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  return memFree(context, pointer);
}

ze_result_t ZE_APICALL virtualQueryPageSize(ze_context_handle_t context, ze_device_handle_t device, size_t, size_t *pageSize) {
  if (!cast<Context>(context, Kind::Context) || !cast<Device>(device, Kind::Device)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pageSize) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *pageSize = PageSize;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualReserve(ze_context_handle_t contextHandle, const void *start, size_t size, void **result) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (size == 0 || size % PageSize != 0) return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  if (start && reinterpret_cast<uintptr_t>(start) % PageSize != 0) return ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT;
  auto state = remoteExecution();
  if (state.mode == ClientMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (state.mode == ClientMode::Ready && size > static_cast<size_t>(std::numeric_limits<int32_t>::max()))
    return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  void *address = MAP_FAILED;
  if (start)
    address = mmap(const_cast<void *>(start), size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED_NOREPLACE, -1, 0);
  if (address == MAP_FAILED) address = mmap(nullptr, size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
  if (address == MAP_FAILED) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  uint64_t allocationId = 0;
  if (state.mode == ClientMode::Ready) {
    std::lock_guard rpcLock(rpcTransactionMutex);
    auto registered = state.client->call(RegisterAllocation{AllocationKind::Device, static_cast<int64_t>(size)});
    if (!registered) {
      rememberRemoteError(state, remoteError(state));
      munmap(address, size);
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
    if (!state.client->call(
            RegisterAllocationAddress{registered.value(), GuestAddress{static_cast<int64_t>(reinterpret_cast<uintptr_t>(address))}})) {
      (void)state.client->call(ReleaseAllocation{registered.value()});
      munmap(address, size);
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
    allocationId = static_cast<uint64_t>(registered.value().value);
  }
  {
    std::lock_guard allocationLock(allocationsMutex);
    allocations.emplace(
        address, std::make_shared<Allocation>(Allocation{HandleUse<Context>(context), address, size, ZE_MEMORY_TYPE_DEVICE, allocationId}));
  }
  {
    std::lock_guard lock(virtualMemoryMutex);
    virtualReservations.emplace(reinterpret_cast<uintptr_t>(address),
                                VirtualReservation{HandleUse<Context>(context), size, allocationId, {}});
  }
  *result = address;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualFree(ze_context_handle_t contextHandle, const void *pointer, size_t size) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::lock_guard lock(virtualMemoryMutex);
  const auto found = virtualReservations.find(reinterpret_cast<uintptr_t>(pointer));
  if (found == virtualReservations.end() || found->second.context.get() != context || found->second.size != size)
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (!found->second.mappings.empty()) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  {
    std::lock_guard allocationLock(allocationsMutex);
    auto allocation = allocations.find(const_cast<void *>(pointer));
    if (allocation == allocations.end() || allocation->second.use_count() != 1) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  }
  auto state = remoteExecution();
  if (state.mode == ClientMode::Ready) {
    std::lock_guard rpcLock(rpcTransactionMutex);
    if (!state.client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(found->second.allocationId)}})) {
      rememberRemoteError(state, remoteError(state));
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
  }
  if (munmap(const_cast<void *>(pointer), size) != 0) return ZE_RESULT_ERROR_UNKNOWN;
  {
    std::lock_guard allocationLock(allocationsMutex);
    allocations.erase(const_cast<void *>(pointer));
  }
  virtualReservations.erase(found);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL physicalCreate(ze_context_handle_t contextHandle, ze_device_handle_t deviceHandle, ze_physical_mem_desc_t *desc,
                                      ze_physical_mem_handle_t *result) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!(desc->flags & ZE_PHYSICAL_MEM_FLAG_ALLOCATE_ON_HOST) && !cast<Device>(deviceHandle, Kind::Device))
    return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
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
  auto *context = cast<Context>(contextHandle, Kind::Context);
  auto *physical = cast<PhysicalMemory>(handle, Kind::PhysicalMemory);
  if (!context || !physical) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (physical->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return destroyHandle(physical);
}

ze_result_t ZE_APICALL physicalProperties(ze_context_handle_t contextHandle, ze_physical_mem_handle_t handle,
                                          ze_physical_mem_properties_t *properties) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  auto *physical = cast<PhysicalMemory>(handle, Kind::PhysicalMemory);
  if (!context || !physical) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (physical->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  properties->id = reinterpret_cast<uintptr_t>(physical);
  properties->size = physical->size;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualMap(ze_context_handle_t contextHandle, const void *pointer, size_t size,
                                  ze_physical_mem_handle_t physicalHandle, size_t offset, ze_memory_access_attribute_t access) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  auto *physical = cast<PhysicalMemory>(physicalHandle, Kind::PhysicalMemory);
  if (!context || !physical) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (physical->context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (access > ZE_MEMORY_ACCESS_ATTRIBUTE_READONLY) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  if (reinterpret_cast<uintptr_t>(pointer) % PageSize != 0 || offset % PageSize != 0) return ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT;
  if (size == 0 || size % PageSize != 0 || offset > physical->size || size > physical->size - offset)
    return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  if (size > std::numeric_limits<uintptr_t>::max() - address) return ZE_RESULT_ERROR_INVALID_SIZE;
  std::lock_guard lock(virtualMemoryMutex);
  auto reservation = virtualReservations.upper_bound(address);
  if (reservation == virtualReservations.begin()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  --reservation;
  auto &record = reservation->second;
  if (record.context.get() != context || address < reservation->first || size > record.size
      || address - reservation->first > record.size - size)
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto next = record.mappings.lower_bound(address);
  if ((next != record.mappings.end() && next->first < address + size)
      || (next != record.mappings.begin() && std::prev(next)->first + std::prev(next)->second.size > address))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (mmap(const_cast<void *>(pointer), size, PROT_READ | PROT_WRITE, MAP_SHARED | MAP_FIXED, physical->fd, static_cast<off_t>(offset))
      == MAP_FAILED)
    return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  record.mappings.emplace(address, VirtualMapping{size, access, HandleUse<PhysicalMemory>(physical)});
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualUnmap(ze_context_handle_t contextHandle, const void *pointer, size_t size) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::lock_guard lock(virtualMemoryMutex);
  auto reservation = virtualReservations.upper_bound(address);
  if (reservation == virtualReservations.begin()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  --reservation;
  if (reservation->second.context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto mapping = reservation->second.mappings.find(address);
  if (mapping == reservation->second.mappings.end() || mapping->second.size != size) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (mmap(const_cast<void *>(pointer), size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS | MAP_FIXED, -1, 0) == MAP_FAILED)
    return ZE_RESULT_ERROR_UNKNOWN;
  reservation->second.mappings.erase(mapping);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualSetAccess(ze_context_handle_t contextHandle, const void *pointer, size_t size,
                                        ze_memory_access_attribute_t access) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (access > ZE_MEMORY_ACCESS_ATTRIBUTE_READONLY) return ZE_RESULT_ERROR_INVALID_ENUMERATION;
  std::lock_guard lock(virtualMemoryMutex);
  auto reservation = virtualReservations.upper_bound(reinterpret_cast<uintptr_t>(pointer));
  if (reservation == virtualReservations.begin()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  --reservation;
  if (reservation->second.context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  auto mapping = reservation->second.mappings.find(reinterpret_cast<uintptr_t>(pointer));
  if (mapping == reservation->second.mappings.end() || mapping->second.size != size) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  mapping->second.access = access;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL virtualGetAccess(ze_context_handle_t contextHandle, const void *pointer, size_t size,
                                        ze_memory_access_attribute_t *access, size_t *outSize) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer || !access || !outSize) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::lock_guard lock(virtualMemoryMutex);
  auto reservation = virtualReservations.upper_bound(reinterpret_cast<uintptr_t>(pointer));
  if (reservation == virtualReservations.begin()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  --reservation;
  if (reservation->second.context.get() != context) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  const auto address = reinterpret_cast<uintptr_t>(pointer);
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
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  properties->type = ZE_MEMORY_TYPE_UNKNOWN;
  properties->id = 0;
  properties->pageSize = 0;
  if (device) *device = nullptr;
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::lock_guard lock(allocationsMutex);
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (allocation->context.get() == context && address >= start && address - start < allocation->size) {
      properties->type = allocation->type;
      properties->id = allocation->id;
      properties->pageSize = PageSize;
      if (device) *device = reinterpret_cast<ze_device_handle_t>(&globalDevice);
      return ZE_RESULT_SUCCESS;
    }
  }
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL memRange(ze_context_handle_t contextHandle, const void *pointer, void **base, size_t *size) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!base && !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::lock_guard lock(allocationsMutex);
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  for (const auto &[candidate, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(candidate);
    if (allocation->context.get() == context && address >= start && address < start + allocation->size) {
      if (base) *base = candidate;
      if (size) *size = allocation->size;
      return ZE_RESULT_SUCCESS;
    }
  }
  return ZE_RESULT_ERROR_INVALID_ARGUMENT;
}

ze_result_t ZE_APICALL eventPoolCreate(ze_context_handle_t contextHandle, const ze_event_pool_desc_t *desc, uint32_t, ze_device_handle_t *,
                                       ze_event_pool_handle_t *result) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!desc || !result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *created = createHandle<EventPool>(context, desc->flags);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_event_pool_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventPoolDestroy(ze_event_pool_handle_t handle) { return destroyHandle(cast<EventPool>(handle, Kind::EventPool)); }
ze_result_t ZE_APICALL eventCreate(ze_event_pool_handle_t poolHandle, const ze_event_desc_t *, ze_event_handle_t *result) {
  auto *pool = cast<EventPool>(poolHandle, Kind::EventPool);
  if (!pool) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *created = createHandle<Event>(pool);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_event_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventDestroy(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  // Recorded lists may still refer to an event that the application destroys
  // before submission. Retire its public handle and defer reclamation until
  // those recorded references have gone away.
  HandleUse<Event> lifetime(event);
  event->destroyRequested.store(true, std::memory_order_release);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventSignal(ze_event_handle_t handle) { return signalEvent(cast<Event>(handle, Kind::Event)); }
ze_result_t ZE_APICALL eventSync(ze_event_handle_t handle, uint64_t timeout) {
  return waitEvent(cast<Event>(handle, Kind::Event), timeout);
}
ze_result_t ZE_APICALL eventStatus(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(event->mutex);
  return event->signalled ? event->completionResult : ZE_RESULT_NOT_READY;
}
ze_result_t ZE_APICALL eventReset(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(event->mutex);
  event->signalled = false;
  event->completionResult = ZE_RESULT_SUCCESS;
  event->kernelStart = 0;
  event->kernelEnd = 0;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL eventKernelTimestamp(ze_event_handle_t handle, ze_kernel_timestamp_result_t *result) {
  auto *event = cast<Event>(handle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::lock_guard lock(event->mutex);
  if (!event->signalled) return ZE_RESULT_NOT_READY;
  if (event->completionResult != ZE_RESULT_SUCCESS) return event->completionResult;
  result->global = {event->kernelStart, event->kernelEnd};
  result->context = result->global;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL fenceCreate(ze_command_queue_handle_t queueHandle, const ze_fence_desc_t *, ze_fence_handle_t *result) {
  auto *queue = cast<Queue>(queueHandle, Kind::Queue);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *created = createHandle<Fence>(queue);
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  *result = reinterpret_cast<ze_fence_handle_t>(created);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL fenceDestroy(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  HandleUse<Fence> lifetime(fence);
  {
    std::lock_guard lock(fence->event.mutex);
    if (fence->pending) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  }
  // Completion is observable before the queue worker drops its submission
  // reference. Retire the public handle now and reclaim it after that release.
  fence->destroyRequested.store(true, std::memory_order_release);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL fenceSync(ze_fence_handle_t handle, uint64_t timeout) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  const auto result = waitEvent(&fence->event, timeout);
  if (result != ZE_RESULT_SUCCESS) return result;
  std::lock_guard lock(fence->event.mutex);
  return fence->completionResult;
}
ze_result_t ZE_APICALL fenceStatus(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(fence->event.mutex);
  if (!fence->event.signalled) return ZE_RESULT_NOT_READY;
  return fence->completionResult;
}
ze_result_t ZE_APICALL fenceReset(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(fence->event.mutex);
  fence->event.signalled = false;
  fence->completionResult = ZE_RESULT_SUCCESS;
  return ZE_RESULT_SUCCESS;
}

bool linkSpirvModules(const std::vector<std::vector<uint8_t>> &modules, std::vector<uint8_t> &linked, std::string &error) {
  std::vector<gemstone::rpc::TemporaryFile> files;
  files.reserve(modules.size() + 1);
  files.emplace_back("gemstone-spirv-XXXXXX");
  for (size_t index = 0; index < modules.size(); ++index)
    files.emplace_back("gemstone-spirv-XXXXXX");
  if (!files[0].valid()) {
    error = "could not create temporary linked module";
    return false;
  }
  for (size_t index = 0; index < modules.size(); ++index) {
    if (!files[index + 1].valid() || !files[index + 1].write(modules[index].data(), modules[index].size())) {
      error = "could not create temporary SPIR-V module";
      return false;
    }
  }
  std::vector<std::string> arguments = {"spirv-link", "--use-highest-version", "--target-env", "opencl2.2", "-o", files[0].path()};
  for (size_t index = 0; index < modules.size(); ++index)
    arguments.push_back(files[index + 1].path());
  const auto result = gemstone::rpc::runProcess(arguments);
  if (result.status == 127) {
    error = "spirv-link is unavailable";
    return false;
  }
  if (result.status != 0) {
    error = "spirv-link rejected the SPIR-V modules";
    return false;
  }
  std::ifstream stream(files[0].path(), std::ios::binary);
  linked.assign(std::istreambuf_iterator<char>(stream), std::istreambuf_iterator<char>());
  const bool loaded = !stream.bad() && linked.size() >= 20;
  if (!loaded) error = "could not read linked SPIR-V module";
  return loaded;
}

size_t spirvInstructionBytes(const std::vector<uint8_t> &bytes);

ze_result_t ZE_APICALL moduleCreate(ze_context_handle_t contextHandle, ze_device_handle_t, const ze_module_desc_t *desc,
                                    ze_module_handle_t *result, ze_module_build_log_handle_t *log) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
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
        inputs.push_back(
            {program->pInputModules[index], program->inputSizes[index], program->pConstants ? program->pConstants[index] : nullptr});
      }
      break;
    }
    extension = static_cast<const ze_base_desc_t *>(extension->pNext);
  }
  if (inputs.empty()) {
    if (desc->inputSize == 0 || !desc->pInputModule) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
    inputs.push_back({static_cast<const uint8_t *>(desc->pInputModule), desc->inputSize, desc->pConstants});
  }
  std::vector<std::vector<uint8_t>> prepared;
  prepared.reserve(inputs.size());
  auto state = remoteExecution();
  for (const auto &input : inputs) {
    std::vector<uint8_t> bytes(input.data, input.data + input.size);
    if (input.constants && input.constants->numConstants != 0) {
      const auto &constants = *input.constants;
      if (!constants.pConstantIds || !constants.pConstantValues) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
      if (state.mode != ClientMode::Ready) {
        setBuildLog(log, "SPIR-V specialization requires the Gemstone execution service");
        return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
      }
      std::lock_guard rpcLock(rpcTransactionMutex);
      auto described = state.client->call(DescribeSpecialization{bytes});
      if (!described) {
        rememberRemoteError(state, remoteError(state));
        setBuildLog(log, state.error);
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
        values.push_back(SpecializationConstant{Binary32{static_cast<int64_t>(id)}, std::vector<uint8_t>(value, value + found->second)});
      }
      auto specialized = state.client->call(SpecializeImage{bytes, values});
      if (!specialized) {
        rememberRemoteError(state, remoteError(state));
        setBuildLog(log, state.error);
        return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
      }
      bytes = std::move(specialized.value());
    }
    prepared.push_back(std::move(bytes));
  }
  std::vector<uint8_t> bytes;
  if (prepared.size() == 1) bytes = std::move(prepared.front());
  else {
    std::string linkerError;
    if (!linkSpirvModules(prepared, bytes, linkerError)) {
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
  uint64_t programId = 0;
  if (state.mode != ClientMode::Ready) {
    setBuildLog(log, state.mode == ClientMode::Misconfigured ? state.error : "Gemstone execution service is not configured");
    return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
  }
  {
    std::lock_guard rpcLock(rpcTransactionMutex);
    if (bytes.size() > std::numeric_limits<uint32_t>::max()) {
      setBuildLog(log, "SPIR-V module is too large");
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
    auto loaded = state.client->call(LoadProgram{"spirv-binary", bytes});
    if (!loaded) {
      rememberRemoteError(state, remoteError(state));
      setBuildLog(log, state.error);
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
    programId = static_cast<uint64_t>(loaded.value().value);
  }
  auto *created = createHandle<Module>(context, std::move(bytes), programId, desc->pBuildFlags ? desc->pBuildFlags : "");
  if (!created) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
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
ze_result_t allocate(Context *context, size_t size, size_t alignment, ze_memory_type_t type, void **result);

ze_result_t ZE_APICALL moduleFunctionPointer(ze_module_handle_t handle, const char *name, void **pointer) {
  auto *module = cast<Module>(handle, Kind::Module);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!name || !pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto state = remoteExecution();
  if (state.mode != ClientMode::Ready) return ZE_RESULT_ERROR_DEVICE_LOST;
  auto described = [&] {
    std::lock_guard rpcLock(rpcTransactionMutex);
    return state.client->call(DescribeKernel{ProgramId{static_cast<int64_t>(module->programId)}, {name}});
  }();
  if (!described) {
    rememberRemoteError(state, described.error().detail);
    return ZE_RESULT_ERROR_INVALID_FUNCTION_NAME;
  }
  const auto &metadata = described.value();
  if (!metadata.entryPointId) return ZE_RESULT_ERROR_INVALID_FUNCTION_NAME;
  if (module->programId > std::numeric_limits<uint32_t>::max()) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  const uint64_t token = (module->programId << 32) | static_cast<uint32_t>(metadata.entryPointId->value);
  *pointer = reinterpret_cast<void *>(static_cast<uintptr_t>(token));
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL moduleGlobalPointer(ze_module_handle_t handle, const char *name, size_t *size, void **pointer) {
  auto *module = cast<Module>(handle, Kind::Module);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!name || (!size && !pointer)) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  {
    std::lock_guard globalsLock(module->globalsMutex);
    for (const auto &global : module->globals) {
      if (global.name != name) continue;
      if (size) *size = global.size;
      if (pointer) *pointer = global.base;
      return ZE_RESULT_SUCCESS;
    }
  }
  auto state = remoteExecution();
  if (state.mode != ClientMode::Ready) return ZE_RESULT_ERROR_DEVICE_LOST;
  auto described = [&] {
    std::lock_guard rpcLock(rpcTransactionMutex);
    return state.client->call(DescribeGlobal{ProgramId{static_cast<int64_t>(module->programId)}, name});
  }();
  if (!described) {
    rememberRemoteError(state, described.error().detail);
    return ZE_RESULT_ERROR_INVALID_GLOBAL_NAME;
  }
  const auto bytes = static_cast<size_t>(described.value().size.value);
  void *base = nullptr;
  auto result = allocate(module->context.get(), bytes, 0, ZE_MEMORY_TYPE_DEVICE, &base);
  if (result != ZE_RESULT_SUCCESS) return result;
  const auto discard = [&] {
    uint64_t id = 0;
    {
      std::lock_guard lock(allocationsMutex);
      const auto found = allocations.find(base);
      if (found != allocations.end()) {
        id = found->second->id;
        allocations.erase(found);
      }
    }
    if (id != 0) {
      std::lock_guard rpcLock(rpcTransactionMutex);
      (void)state.client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(id)}});
    }
    std::free(base);
  };
  if (described.value().initial.size() > bytes) {
    discard();
    return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
  }
  if (!described.value().initial.empty()) std::memcpy(base, described.value().initial.data(), described.value().initial.size());
  const auto allocation = retainAllocation(base, bytes);
  if (!allocation) {
    discard();
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  bool bound = false;
  {
    std::lock_guard rpcLock(rpcTransactionMutex);
    bound = static_cast<bool>(state.client->call(BindProgramGlobal{ProgramId{static_cast<int64_t>(module->programId)}, name,
                                                                   AllocationId{static_cast<int64_t>(allocation->id)}, LongPositive0{0}}));
    if (!bound) rememberRemoteError(state, remoteError(state));
  }
  if (!bound) {
    discard();
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  {
    std::lock_guard globalsLock(module->globalsMutex);
    module->globals.push_back({name, base, bytes, allocation->id});
  }
  if (size) *size = bytes;
  if (pointer) *pointer = base;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL moduleDestroy(ze_module_handle_t handle) {
  auto *module = cast<Module>(handle, Kind::Module);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (module->users.load(std::memory_order_acquire) != 0) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  {
    auto state = remoteExecution();
    std::lock_guard rpcLock(rpcTransactionMutex);
    if (!state.client->call(UnloadProgram{ProgramId{static_cast<int64_t>(module->programId)}})) {
      rememberRemoteError(state, remoteError(state));
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
  }
  std::vector<Module::Global> globals;
  {
    std::lock_guard globalsLock(module->globalsMutex);
    globals = module->globals;
  }
  bool releasedAll = true;
  for (const auto &global : globals) {
    AllocationRef allocation;
    {
      std::lock_guard lock(allocationsMutex);
      const auto found = allocations.find(global.base);
      if (found != allocations.end()) {
        allocation = found->second;
        allocations.erase(found);
      }
    }
    if (allocation) {
      auto state = remoteExecution();
      if (state.mode == ClientMode::Ready) {
        std::lock_guard rpcLock(rpcTransactionMutex);
        if (!state.client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(allocation->id)}})) {
          rememberRemoteError(state, remoteError(state));
          releasedAll = false;
        }
      }
      std::free(global.base);
    }
  }
  delete module;
  return releasedAll ? ZE_RESULT_SUCCESS : ZE_RESULT_ERROR_DEVICE_LOST;
}
ze_result_t ZE_APICALL moduleDynamicLink(uint32_t count, ze_module_handle_t *modules, ze_module_build_log_handle_t *log) {
  if (count == 0 || !modules) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  for (uint32_t index = 0; index < count; ++index)
    if (cast<Module>(modules[index], Kind::Module) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  setBuildLog(log, "Gemstone does not support dynamic linking of SPIR-V modules");
  return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
}
ze_result_t ZE_APICALL moduleProperties(ze_module_handle_t handle, ze_module_properties_t *properties) {
  if (cast<Module>(handle, Kind::Module) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
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
    return static_cast<uint32_t>(bytes[index * 4]) | (static_cast<uint32_t>(bytes[index * 4 + 1]) << 8)
           | (static_cast<uint32_t>(bytes[index * 4 + 2]) << 16) | (static_cast<uint32_t>(bytes[index * 4 + 3]) << 24);
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
  auto *module = cast<Module>(handle, Kind::Module);
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
ze_result_t ZE_APICALL buildLogDestroy(ze_module_build_log_handle_t handle) {
  return destroyHandle(cast<BuildLog>(handle, Kind::BuildLog));
}
ze_result_t ZE_APICALL buildLogString(ze_module_build_log_handle_t handle, size_t *size, char *text) {
  auto *log = cast<BuildLog>(handle, Kind::BuildLog);
  if (!log || !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  return gemstone::copyOut(*size, text, size, log->text.c_str(), log->text.size() + 1) ? ZE_RESULT_SUCCESS : ZE_RESULT_ERROR_INVALID_SIZE;
}

ze_result_t ZE_APICALL kernelGroup(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z);
ze_result_t ZE_APICALL kernelCreate(ze_module_handle_t moduleHandle, const ze_kernel_desc_t *desc, ze_kernel_handle_t *result) {
  auto *module = cast<Module>(moduleHandle, Kind::Module);
  if (!module || !desc || !result || !desc->pKernelName) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *kernel = createHandle<Kernel>(module, desc->pKernelName);
  if (!kernel) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  auto state = remoteExecution();
  if (state.mode != ClientMode::Ready) {
    delete kernel;
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  auto described = state.client->call(DescribeKernel{ProgramId{static_cast<int64_t>(module->programId)}, kernel->name});
  if (!described) {
    rememberRemoteError(state, remoteError(state));
    delete kernel;
    return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
  }
  kernel->parameters.reserve(described.value().parameters.size());
  for (const auto &parameter : described.value().parameters) {
    const bool directPointer = parameter.kind == ParameterKind::Allocation;
    kernel->parameters.push_back({parameter.kind == ParameterKind::Scalar && parameter.containsPointers,
                                  parameter.kind == ParameterKind::Local, directPointer || parameter.containsPointers});
  }
  if (described.value().requiredWorkGroupSize) {
    const auto &required = *described.value().requiredWorkGroupSize;
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
ze_result_t ZE_APICALL kernelDestroy(ze_kernel_handle_t handle) { return destroyHandle(cast<Kernel>(handle, Kind::Kernel)); }
ze_result_t ZE_APICALL kernelGroup(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
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
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
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
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
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
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
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
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelProperties(ze_kernel_handle_t handle, ze_kernel_properties_t *properties) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
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
  properties->localMemSize = 0;
  properties->privateMemSize = 0;
  properties->spillMemSize = 0;
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
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  kernel->indirect = flags;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelGetIndirect(ze_kernel_handle_t handle, ze_kernel_indirect_access_flags_t *flags) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (!kernel || !flags) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *flags = kernel->indirect;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelSourceAttributes(ze_kernel_handle_t handle, uint32_t *size, char **attributes) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto state = remoteExecution();
  if (state.mode != ClientMode::Ready) return ZE_RESULT_ERROR_DEVICE_LOST;
  auto described = state.client->call(DescribeKernel{ProgramId{static_cast<int64_t>(kernel->module->programId)}, kernel->name});
  if (!described) {
    rememberRemoteError(state, remoteError(state));
    return ZE_RESULT_ERROR_DEVICE_LOST;
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
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (!kernel || !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  return gemstone::copyOut(*size, name, size, kernel->name.c_str(), kernel->name.size() + 1) ? ZE_RESULT_SUCCESS
                                                                                             : ZE_RESULT_ERROR_INVALID_SIZE;
}

template <class T> ze_result_t emptyTable(ze_api_version_t version, T *table) {
  const auto checked = checkVersion(version, table);
  if (checked == ZE_RESULT_SUCCESS) gemstone_levelzero_ddi::clear(table);
  return checked;
}

} // namespace

extern "C" ZE_DLLEXPORT ze_result_t ZE_APICALL zeKernelSetGlobalOffsetExp(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z) {
  return kernelGlobalOffset(handle, x, y, z);
}

// Loader-facing ABI table wiring lives here so it can bind the facade's
// private implementation functions without a textual include fragment.
#define TABLE_BEGIN(name, type)                                                                                                            \
  extern "C" ZE_DLLEXPORT ze_result_t ZE_APICALL name(ze_api_version_t version, type *table) {                                             \
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
table->pfnPciGetPropertiesExt = devicePciProperties;
TABLE_END
TABLE_BEGIN(zeGetContextProcAddrTable, ze_context_dditable_t);
table->pfnCreate = contextCreate;
table->pfnCreateEx = contextCreateEx;
table->pfnDestroy = contextDestroy;
table->pfnGetStatus = contextStatus;
table->pfnMakeMemoryResident = contextMemory;
table->pfnEvictMemory = contextMemory;
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
table->pfnAppendMemoryCopy = listCopy;
table->pfnAppendMemoryCopyFromContext = listCopyFromContext;
table->pfnAppendMemoryCopyRegion = listCopyRegion;
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
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return list->waitFor(timeout);
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
TABLE_END
TABLE_BEGIN(zeGetEventProcAddrTable, ze_event_dditable_t);
table->pfnCreate = eventCreate;
table->pfnDestroy = eventDestroy;
table->pfnHostSignal = eventSignal;
table->pfnHostSynchronize = eventSync;
table->pfnQueryStatus = eventStatus;
table->pfnHostReset = eventReset;
table->pfnQueryKernelTimestamp = eventKernelTimestamp;
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
EMPTY_TABLE(zeGetImageProcAddrTable, ze_image_dditable_t)
TABLE_BEGIN(zeGetPhysicalMemProcAddrTable, ze_physical_mem_dditable_t);
table->pfnCreate = physicalCreate;
table->pfnDestroy = physicalDestroy;
table->pfnGetProperties = physicalProperties;
TABLE_END
EMPTY_TABLE(zeGetSamplerProcAddrTable, ze_sampler_dditable_t)
TABLE_BEGIN(zeGetVirtualMemProcAddrTable, ze_virtual_mem_dditable_t);
table->pfnReserve = virtualReserve;
table->pfnFree = virtualFree;
table->pfnQueryPageSize = virtualQueryPageSize;
table->pfnMap = virtualMap;
table->pfnUnmap = virtualUnmap;
table->pfnSetAccessAttribute = virtualSetAccess;
table->pfnGetAccessAttribute = virtualGetAccess;
TABLE_END

// These API families are intentionally not implemented by the execution facade yet.
// Publishing their DDI entry points with empty tables makes that decision explicit
// while preserving the implementation check for the loader-facing ABI.
TABLE_BEGIN(zeGetCommandListExpProcAddrTable, ze_command_list_exp_dditable_t);
table->pfnImmediateAppendCommandListsExp = listImmediateAppendCommandLists;
TABLE_END
EMPTY_TABLE(zeGetDeviceExpProcAddrTable, ze_device_exp_dditable_t)
EMPTY_TABLE(zeGetDriverExpProcAddrTable, ze_driver_exp_dditable_t)
EMPTY_TABLE(zeGetEventExpProcAddrTable, ze_event_exp_dditable_t)
EMPTY_TABLE(zeGetExecutableGraphProcAddrTable, ze_executable_graph_dditable_t)
EMPTY_TABLE(zeGetFabricEdgeExpProcAddrTable, ze_fabric_edge_exp_dditable_t)
EMPTY_TABLE(zeGetFabricVertexExpProcAddrTable, ze_fabric_vertex_exp_dditable_t)
EMPTY_TABLE(zeGetGraphProcAddrTable, ze_graph_dditable_t)
EMPTY_TABLE(zeGetImageExpProcAddrTable, ze_image_exp_dditable_t)
EMPTY_TABLE(zeGetMemExpProcAddrTable, ze_mem_exp_dditable_t)
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
