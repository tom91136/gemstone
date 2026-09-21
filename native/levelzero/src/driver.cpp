#include <algorithm>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <functional>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <string>
#include <string_view>
#include <thread>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>

#include <ze_ddi.h>
#include <zes_ddi.h>
#include <zet_ddi.h>

#include "gemstone_execution_rpc.hpp"
#include "gemstone_levelzero_ddi.hpp"
#include <gemstone/bytes.hpp>

#include "completion.hpp"
#include "objects.hpp"

namespace {

using namespace gemstone::execution;
using namespace gemstone::levelzero;

Driver globalDriver;
Device globalDevice;
std::mutex allocationsMutex;
std::unordered_map<void *, AllocationRef> allocations;
std::mutex rpcTransactionMutex;

enum class RemoteMode { Uninitialized, Disabled, Ready, Misconfigured };

struct RemoteExecution {
  std::once_flag initialized;
  RemoteMode mode = RemoteMode::Uninitialized;
  std::unique_ptr<Client> client;
  std::string error;
};

RemoteExecution remote;

struct LaunchPerformance {
  uint64_t uploadedBytes = 0;
  uint64_t downloadedBytes = 0;
  uint64_t relocationScanBytes = 0;
  uint64_t relocationCount = 0;
};

RemoteExecution &remoteExecution() {
  std::call_once(remote.initialized, [] {
    const char *port = std::getenv("GEMSTONE_RPC_PORT");
    const char *client = std::getenv("GEMSTONE_RPC_CLIENT");
    const bool hasPort = port && port[0] != '\0';
    const bool hasClient = client && client[0] != '\0';
    if (!hasPort && !hasClient) {
      remote.mode = RemoteMode::Disabled;
      return;
    }
    if (!hasPort || !hasClient) {
      remote.mode = RemoteMode::Misconfigured;
      remote.error = "GEMSTONE_RPC_PORT and GEMSTONE_RPC_CLIENT must be set together";
      return;
    }
    remote.client = Client::fromEnvironment();
    if (!remote.client) {
      remote.mode = RemoteMode::Misconfigured;
      remote.error = "GEMSTONE_RPC_CLIENT must be a positive integer";
      return;
    }
    if (!remote.client->call(Ping{})) {
      remote.mode = RemoteMode::Misconfigured;
      remote.error = remote.client->error();
      return;
    }
    remote.mode = RemoteMode::Ready;
  });
  return remote;
}

std::string remoteError(RemoteExecution &state) {
  const auto value = !state.client ? std::string{} : state.client->error();
  if (!value.empty()) return value;
  return state.error.empty() ? "Gemstone execution RPC failed" : state.error;
}

void rememberRemoteError(RemoteExecution &state, std::string message) {
  state.error = std::move(message);
  if (std::getenv("GEMSTONE_TRACE") != nullptr) std::fprintf(stderr, "Gemstone Level Zero: %s\n", state.error.c_str());
}

AllocationKind rpcKind(ze_memory_type_t kind) {
  switch (kind) {
    case ZE_MEMORY_TYPE_DEVICE: return AllocationKind::Device;
    case ZE_MEMORY_TYPE_SHARED: return AllocationKind::Shared;
    case ZE_MEMORY_TYPE_HOST: return AllocationKind::Host;
    default: return AllocationKind::Device;
  }
}

// Bound the amount of immutable JVM allocation state copied into one response.
constexpr size_t MirrorChunkSize = 1U * 1024U * 1024U;
constexpr size_t MirrorPageSize = 4096U;

bool writeRemote(Client &client, uint64_t id, uint64_t offset, const void *bytes, size_t size) {
  const auto *source = static_cast<const uint8_t *>(bytes);
  while (size != 0) {
    const auto chunk = std::min(size, MirrorChunkSize);
    if (!client.call(WriteAllocation{AllocationId{static_cast<int64_t>(id)}, static_cast<int64_t>(offset),
                                     std::vector<uint8_t>(source, source + chunk)}))
      return false;
    source += chunk;
    offset += chunk;
    size -= chunk;
  }
  return true;
}

bool readRemote(Client &client, uint64_t id, uint64_t offset, void *bytes, size_t size) {
  auto *destination = static_cast<uint8_t *>(bytes);
  while (size != 0) {
    const auto chunk = std::min(size, MirrorChunkSize);
    auto result =
        client.call(ReadAllocation{AllocationId{static_cast<int64_t>(id)}, static_cast<int64_t>(offset), static_cast<int32_t>(chunk)});
    if (!result || result.value().size() != chunk) return false;
    std::memcpy(destination, result.value().data(), chunk);
    destination += chunk;
    offset += chunk;
    size -= chunk;
  }
  return true;
}

uint64_t mirrorPageFingerprint(const uint8_t *bytes, size_t size) {
  const auto view = std::string_view(reinterpret_cast<const char *>(bytes), size);
  return static_cast<uint64_t>(std::hash<std::string_view>{}(view));
}

void updateSynchronizedFingerprints(const AllocationRef &allocation, size_t offset, size_t size) {
  if (size == 0) return;
  const size_t pageCount = (allocation->size + MirrorPageSize - 1U) / MirrorPageSize;
  allocation->synchronizedPageFingerprints.resize(pageCount);
  const size_t firstPage = offset / MirrorPageSize;
  const size_t lastPage = (offset + size - 1U) / MirrorPageSize;
  const auto *bytes = static_cast<const uint8_t *>(allocation->base);
  for (size_t page = firstPage; page <= lastPage; ++page) {
    const size_t start = page * MirrorPageSize;
    allocation->synchronizedPageFingerprints[page] =
        mirrorPageFingerprint(bytes + start, std::min(MirrorPageSize, allocation->size - start));
  }
  allocation->remoteSynchronized = true;
}

int mirrorAllocationsToServer(RemoteExecution &state, const KernelLaunch &kernel, const std::vector<PointerRelocation> &relocations,
                              LaunchPerformance &performance) {
  std::unordered_set<uint64_t> reachable;
  if (!kernel.indirect)
    for (const auto &relocation : relocations)
      reachable.insert(static_cast<uint64_t>(relocation.target.value));
  std::lock_guard lock(allocationsMutex);
  for (const auto &[base, allocation] : allocations) {
    if (!kernel.indirect && !reachable.contains(allocation->id)) continue;
    const auto *bytes = static_cast<const uint8_t *>(base);
    const size_t pageCount = (allocation->size + MirrorPageSize - 1U) / MirrorPageSize;
    std::vector<uint64_t> fingerprints(pageCount);
    std::vector<bool> changed(pageCount);
    for (size_t page = 0; page < pageCount; ++page) {
      const size_t offset = page * MirrorPageSize;
      fingerprints[page] = mirrorPageFingerprint(bytes + offset, std::min(MirrorPageSize, allocation->size - offset));
      changed[page] = !allocation->remoteSynchronized || page >= allocation->synchronizedPageFingerprints.size()
                      || fingerprints[page] != allocation->synchronizedPageFingerprints[page];
    }
    allocation->synchronizedPageFingerprints.resize(pageCount);
    size_t page = 0;
    while (page < pageCount) {
      while (page < pageCount && !changed[page])
        ++page;
      if (page == pageCount) break;
      const size_t first = page;
      size_t bytesInRange = 0;
      while (page < pageCount && changed[page] && bytesInRange < MirrorChunkSize) {
        bytesInRange += std::min(MirrorPageSize, allocation->size - page * MirrorPageSize);
        ++page;
      }
      const size_t offset = first * MirrorPageSize;
      const auto count = static_cast<uint32_t>(bytesInRange);
      if (!writeRemote(*state.client, allocation->id, offset, bytes + offset, count)) {
        allocation->remoteSynchronized = false;
        return -1;
      }
      performance.uploadedBytes += count;
      for (size_t updated = first; updated < page; ++updated)
        allocation->synchronizedPageFingerprints[updated] = fingerprints[updated];
    }
    allocation->remoteSynchronized = true;
  }
  return 0;
}

int mirrorDirtyRangesFromServer(RemoteExecution &state, const std::vector<DirtyRange> &ranges, LaunchPerformance &performance) {
  std::lock_guard lock(allocationsMutex);
  for (const auto &range : ranges) {
    const auto found = std::find_if(allocations.begin(), allocations.end(),
                                    [&](const auto &entry) { return entry.second->id == static_cast<uint64_t>(range.allocation.value); });
    const auto offset = static_cast<uint64_t>(range.offset.value);
    const auto size = static_cast<uint64_t>(range.size.value);
    if (found == allocations.end() || offset > found->second->size || size > found->second->size - offset) return -1;
    auto *bytes = static_cast<uint8_t *>(found->first);
    if (!readRemote(*state.client, static_cast<uint64_t>(range.allocation.value), offset, bytes + offset, range.size.value)) return -1;
    performance.downloadedBytes += size;
    updateSynchronizedFingerprints(found->second, offset, size);
  }
  return 0;
}

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
      if (source->size < sizeof(uintptr_t)) continue;
      performance.relocationScanBytes += source->size;
      const auto *bytes = static_cast<const uint8_t *>(base);
      for (size_t offset = 0; offset <= source->size - sizeof(uintptr_t); offset += sizeof(uint32_t)) {
        const auto pointer = gemstone::readNative<uintptr_t>(bytes, offset);
        uint64_t targetId = 0;
        uint64_t targetOffset = 0;
        if (classifyPointerValueLocked(pointer, targetId, targetOffset)) {
          result.push_back({RelocationSourceAllocation{AllocationId{static_cast<int64_t>(source->id)}},
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

// Level Zero presents every kernel argument as an opaque byte blob.  The
// server ABI can represent a direct pointer as an allocation argument, while
// a pointer-containing aggregate must remain a scalar blob with relocations.
// The same metadata also distinguishes Workgroup pointers from DPC++'s
// nullptr marker for zero-valued ABI fields.
std::vector<gemstone::levelzero::KernelParameterMetadata> spirvKernelParameterMetadata(const std::vector<uint8_t> &il,
                                                                                       const std::string &name) {
  struct TypeInfo {
    enum class Kind { Aggregate, Pointer } kind = Kind::Aggregate;
    uint32_t pointee = 0;
    uint32_t storage = 0;
    std::vector<uint32_t> members;
  };
  if (il.size() < 5 * sizeof(uint32_t) || il.size() % sizeof(uint32_t) != 0) return {};
  const auto word = [&il](size_t offset) { return gemstone::readNative<uint32_t>(il.data(), offset); };
  std::map<uint32_t, TypeInfo> types;
  uint32_t entry = 0;
  for (size_t offset = 5 * sizeof(uint32_t); offset + sizeof(uint32_t) <= il.size();) {
    const uint32_t instruction = word(offset);
    const size_t words = instruction >> 16;
    const uint32_t opcode = instruction & 0xffff;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if ((opcode == 28 || opcode == 29) && words >= 3) {
      TypeInfo info;
      info.members.push_back(word(offset + 2 * sizeof(uint32_t)));
      types.insert_or_assign(word(offset + sizeof(uint32_t)), std::move(info));
    } else if (opcode == 30 && words >= 2) {
      TypeInfo info;
      for (size_t index = 2; index < words; ++index)
        info.members.push_back(word(offset + index * sizeof(uint32_t)));
      types.insert_or_assign(word(offset + sizeof(uint32_t)), std::move(info));
    } else if (opcode == 32 && words >= 4) {
      TypeInfo info;
      info.kind = TypeInfo::Kind::Pointer;
      info.pointee = word(offset + 3 * sizeof(uint32_t));
      info.storage = word(offset + 2 * sizeof(uint32_t));
      types.insert_or_assign(word(offset + sizeof(uint32_t)), std::move(info));
    } else if (opcode == 15 && words >= 4) {
      const size_t nameOffset = offset + 3 * sizeof(uint32_t);
      const size_t nameLimit = offset + words * sizeof(uint32_t);
      size_t end = nameOffset;
      while (end < nameLimit && il[end] != 0)
        ++end;
      if (end < nameLimit && std::string(reinterpret_cast<const char *>(il.data() + nameOffset), end - nameOffset) == name)
        entry = word(offset + 2 * sizeof(uint32_t));
    }
    offset += words * sizeof(uint32_t);
  }
  if (entry == 0) return {};
  std::function<bool(uint32_t)> containsPointer = [&](uint32_t typeId) {
    const auto found = types.find(typeId);
    if (found == types.end()) return false;
    if (found->second.kind == TypeInfo::Kind::Pointer) return true;
    return std::any_of(found->second.members.begin(), found->second.members.end(), containsPointer);
  };
  std::vector<gemstone::levelzero::KernelParameterMetadata> result;
  bool inFunction = false;
  for (size_t offset = 5 * sizeof(uint32_t); offset + sizeof(uint32_t) <= il.size();) {
    const uint32_t instruction = word(offset);
    const size_t words = instruction >> 16;
    const uint32_t opcode = instruction & 0xffff;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if (opcode == 54 && words >= 3) inFunction = word(offset + 2 * sizeof(uint32_t)) == entry;
    else if (inFunction && opcode == 55 && words >= 2) {
      const auto found = types.find(word(offset + sizeof(uint32_t)));
      const auto pointee =
          found == types.end() || found->second.kind != TypeInfo::Kind::Pointer ? types.end() : types.find(found->second.pointee);
      result.push_back({pointee != types.end() && pointee->second.kind == TypeInfo::Kind::Aggregate,
                        found != types.end() && found->second.kind == TypeInfo::Kind::Pointer && found->second.storage == 4,
                        containsPointer(word(offset + sizeof(uint32_t)))});
    } else if (inFunction && opcode == 56) break;
    offset += words * sizeof(uint32_t);
  }
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
  std::lock_guard lock(liveHandlesMutex);
  return liveHandles.contains(value) && value->magic == Magic && value->kind == expected ? value : nullptr;
}

ze_result_t checkVersion(ze_api_version_t version, const void *table) {
  if (!table) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (ZE_MAJOR_VERSION(version) != ZE_MAJOR_VERSION(ZE_API_VERSION_CURRENT)) return ZE_RESULT_ERROR_UNSUPPORTED_VERSION;
  return ZE_RESULT_SUCCESS;
}

ze_result_t waitEvent(Event *event, uint64_t timeout) {
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::unique_lock lock(event->mutex);
  if (event->signalled) return ZE_RESULT_SUCCESS;
  if (timeout == 0) return ZE_RESULT_NOT_READY;
  if (timeout == std::numeric_limits<uint64_t>::max()) {
    event->changed.wait(lock, [&] { return event->signalled; });
    return ZE_RESULT_SUCCESS;
  }
  return event->changed.wait_for(lock, std::chrono::nanoseconds(timeout), [&] { return event->signalled; }) ? ZE_RESULT_SUCCESS
                                                                                                            : ZE_RESULT_NOT_READY;
}

ze_result_t signalEvent(Event *event) {
  if (!event) return ZE_RESULT_SUCCESS;
  {
    std::lock_guard lock(event->mutex);
    const auto now = std::chrono::steady_clock::now().time_since_epoch();
    event->kernelStart = static_cast<uint64_t>(std::chrono::duration_cast<std::chrono::nanoseconds>(now).count());
    event->kernelEnd = event->kernelStart + 1;
    event->signalled = true;
  }
  event->changed.notify_all();
  return ZE_RESULT_SUCCESS;
}

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

ze_result_t append(CommandList *list, CommandAction command, std::function<void()> release = {}) {
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (list->immediate) {
    const auto result = command();
    if (result == ZE_RESULT_SUCCESS && release) release();
    return result;
  }
  if (list->closed) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  list->commands.push_back({std::move(command), std::move(release)});
  return ZE_RESULT_SUCCESS;
}

ze_result_t retainCommandEvents(Context *context, ze_event_handle_t signalHandle, uint32_t waitCount, ze_event_handle_t *waitHandles,
                                HandleUse<Event> &signal, EventUses &waits) {
  const auto waitResult = retainEvents(waitCount, waitHandles, waits);
  if (waitResult != ZE_RESULT_SUCCESS) return waitResult;
  const auto signalResult = retainSignal(signalHandle, signal);
  if (signalResult != ZE_RESULT_SUCCESS) return signalResult;
  if ((signal && signal->pool->context.get() != context)
      || std::any_of(waits.begin(), waits.end(),
                     [context](const HandleUse<Event> &event) { return event->pool->context.get() != context; }))
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

ze_result_t ZE_APICALL driverExtensions(ze_driver_handle_t, uint32_t *count, ze_driver_extension_properties_t *properties) {
  if (!count) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!properties) {
    *count = 1;
  } else if (*count != 0) {
    std::strncpy(properties[0].name, ZE_MODULE_PROGRAM_EXP_NAME, ZE_MAX_EXTENSION_NAME - 1);
    properties[0].name[ZE_MAX_EXTENSION_NAME - 1] = '\0';
    properties[0].version = ZE_MODULE_PROGRAM_EXP_VERSION_CURRENT;
    *count = 1;
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
  properties->maxMemAllocSize = 1ULL << 40;
  properties->maxHardwareContexts = 1024;
  properties->maxCommandQueuePriority = 0;
  properties->numThreadsPerEU = 8;
  properties->physicalEUSimdWidth = 8;
  properties->numEUsPerSubslice = 8;
  properties->numSubslicesPerSlice = 8;
  properties->numSlices = 1;
  properties->timerResolution = 1000000000ULL;
  properties->timestampValidBits = 64;
  properties->kernelTimestampValidBits = 64;
  std::memcpy(properties->uuid.id, "GEMSTONE-SPIRV", 14);
  std::strncpy(properties->name, "Gemstone Virtual SPIR-V GPU", ZE_MAX_DEVICE_NAME - 1);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceCompute(ze_device_handle_t, ze_device_compute_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->maxTotalGroupSize = 1024;
  properties->maxGroupSizeX = 1024;
  properties->maxGroupSizeY = 1024;
  properties->maxGroupSizeZ = 64;
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
    properties[0].totalSize = 1ULL << 40;
    std::strncpy(properties[0].name, "Gemstone unified memory", ZE_MAX_DEVICE_NAME - 1);
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL deviceMemoryAccess(ze_device_handle_t, ze_device_memory_access_properties_t *properties) {
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->hostAllocCapabilities = ZE_MEMORY_ACCESS_CAP_FLAG_RW | ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC;
  properties->deviceAllocCapabilities = properties->hostAllocCapabilities;
  properties->sharedSingleDeviceAllocCapabilities = properties->hostAllocCapabilities;
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

ze_result_t ZE_APICALL contextCreate(ze_driver_handle_t, const ze_context_desc_t *, ze_context_handle_t *result) {
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_context_handle_t>(new Context);
  return ZE_RESULT_SUCCESS;
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
  *result = reinterpret_cast<ze_command_queue_handle_t>(new Queue(owner, target, *desc));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL queueDestroy(ze_command_queue_handle_t handle) { return destroyHandle(cast<Queue>(handle, Kind::Queue)); }
ze_result_t ZE_APICALL queueExecute(ze_command_queue_handle_t queueHandle, uint32_t count, ze_command_list_handle_t *handles,
                                    ze_fence_handle_t fenceHandle) {
  auto *queue = cast<Queue>(queueHandle, Kind::Queue);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (count != 0 && !handles) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  for (uint32_t index = 0; index < count; ++index) {
    auto *list = cast<CommandList>(handles[index], Kind::List);
    if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (list->context.get() != queue->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    for (auto &command : list->commands) {
      const auto result = command.action();
      if (result != ZE_RESULT_SUCCESS) return result;
      command.releaseResources();
    }
  }
  if (fenceHandle) {
    auto *fence = cast<Fence>(fenceHandle, Kind::Fence);
    if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    if (fence->queue.get() != queue) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    signalEvent(&fence->event);
  }
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL queueSync(ze_command_queue_handle_t handle, uint64_t) {
  return cast<Queue>(handle, Kind::Queue) == nullptr ? ZE_RESULT_ERROR_INVALID_NULL_HANDLE : ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL listCreate(ze_context_handle_t context, ze_device_handle_t device, const ze_command_list_desc_t *,
                                  ze_command_list_handle_t *result) {
  auto *owner = cast<Context>(context, Kind::Context);
  auto *target = cast<Device>(device, Kind::Device);
  if (!owner || !target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_command_list_handle_t>(new CommandList(false, owner, target));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL listCreateImmediate(ze_context_handle_t context, ze_device_handle_t device, const ze_command_queue_desc_t *,
                                           ze_command_list_handle_t *result) {
  auto *owner = cast<Context>(context, Kind::Context);
  auto *target = cast<Device>(device, Kind::Device);
  if (!owner || !target) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_command_list_handle_t>(new CommandList(true, owner, target));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL listDestroy(ze_command_list_handle_t handle) { return destroyHandle(cast<CommandList>(handle, Kind::List)); }
ze_result_t ZE_APICALL listClose(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  list->closed = true;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL listReset(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
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
  return append(list, [signal = std::move(retainedSignal), waits = std::move(retainedWaits)] {
    auto result = waitDependencies(waits);
    return result == ZE_RESULT_SUCCESS ? signalEvent(signal.get()) : result;
  });
}

ze_result_t ZE_APICALL listCopy(ze_command_list_handle_t handle, void *destination, const void *source, size_t size,
                                ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
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
  if ((destinationAllocation && destinationAllocation->context.get() != list->context.get())
      || (sourceAllocation && sourceAllocation->context.get() != list->context.get()))
    return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  // A failed full-range lookup must not turn a tracked out-of-bounds pointer into a host pointer.
  if ((!destinationAllocation && retainAllocation(destination, 0)) || (!sourceAllocation && retainAllocation(source, 0)))
    return ZE_RESULT_ERROR_INVALID_SIZE;
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>();
  if (destinationAllocation) allocationLifetime->push_back(std::move(destinationAllocation));
  if (sourceAllocation) allocationLifetime->push_back(std::move(sourceAllocation));
  return append(
      list,
      [=, signal = std::move(retainedSignal), waits = std::move(retainedWaits)] {
        (void)allocationLifetime;
        auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS && size != 0) std::memmove(destination, source, size);
        return result == ZE_RESULT_SUCCESS ? signalEvent(signal.get()) : result;
      },
      [allocationLifetime] { allocationLifetime->clear(); });
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
    if (region.depth != 0 && out.slice < minimumSlice) return false;
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
      [=, signal = std::move(retainedSignal), waits = std::move(retainedWaits)] {
        (void)lifetime;
        const auto result = waitDependencies(waits);
        if (result != ZE_RESULT_SUCCESS) return result;
        for (size_t row = 0; row < rows; ++row)
          std::memcpy(static_cast<uint8_t *>(destination) + rowOffset(destinationLayout, row),
                      static_cast<const uint8_t *>(source) + rowOffset(sourceLayout, row), src.width);
        return signalEvent(signal.get());
      },
      [lifetime] { lifetime->clear(); });
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
      [=, signal = std::move(retainedSignal), waits = std::move(retainedWaits)] {
        (void)allocationLifetime;
        auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS) {
          auto *bytes = static_cast<uint8_t *>(destination);
          for (size_t offset = 0; offset < size; ++offset)
            bytes[offset] = copiedPattern[offset % patternSize];
        }
        return result == ZE_RESULT_SUCCESS ? signalEvent(signal.get()) : result;
      },
      [allocationLifetime] { allocationLifetime->clear(); });
}

ze_result_t ZE_APICALL listPrefetch(ze_command_list_handle_t handle, const void *pointer, size_t size) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!list) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto allocation = retainAllocation(pointer, size);
  if (!allocation || allocation->context.get() != list->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return append(list, [allocation = std::move(allocation)] {
    (void)allocation;
    return ZE_RESULT_SUCCESS;
  });
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
  if (!allocation || allocation->context.get() != list->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  return append(list, [allocation = std::move(allocation)] {
    (void)allocation;
    return ZE_RESULT_SUCCESS;
  });
}

ze_result_t ZE_APICALL listSignal(ze_command_list_handle_t handle, ze_event_handle_t signal) {
  HandleUse<Event> retainedSignal;
  const auto retained = retainSignal(signal, retainedSignal);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  return append(cast<CommandList>(handle, Kind::List), [signal = std::move(retainedSignal)] { return signalEvent(signal.get()); });
}
ze_result_t ZE_APICALL listWait(ze_command_list_handle_t handle, uint32_t count, ze_event_handle_t *events) {
  EventUses retainedEvents;
  const auto retained = retainEvents(count, events, retainedEvents);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  return append(cast<CommandList>(handle, Kind::List), [events = std::move(retainedEvents)] { return waitDependencies(events); });
}
ze_result_t ZE_APICALL listResetEvent(ze_command_list_handle_t handle, ze_event_handle_t eventHandle) {
  auto *event = cast<Event>(eventHandle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return append(cast<CommandList>(handle, Kind::List), [event = HandleUse<Event>(event)] {
    std::lock_guard lock(event->mutex);
    event->signalled = false;
    return ZE_RESULT_SUCCESS;
  });
}

ze_result_t launchRpc(const KernelLaunch &kernel, const ze_group_count_t &groups) {
  // Mirroring, launch and copy-back form one transaction. Serializing the
  // complete sequence prevents concurrent queues from observing one another's
  // partially mirrored allocation state.
  std::lock_guard rpcLock(rpcTransactionMutex);
  auto &state = remoteExecution();
  if (state.mode == RemoteMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (state.mode != RemoteMode::Ready || kernel.module->backend != ModuleBackend::RPC) return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  const auto started = std::chrono::steady_clock::now();
  LaunchPerformance performance;
  const auto relocations = collectPointerRelocations(kernel, performance);
  if (mirrorAllocationsToServer(state, kernel, relocations, performance) != 0) {
    rememberRemoteError(state, remoteError(state));
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }

  std::vector<gemstone::execution::KernelArgument> arguments;
  arguments.reserve(kernel.arguments.size());
  for (size_t index = 0; index < kernel.arguments.size(); ++index) {
    const auto &input = kernel.arguments[index];
    if (input.local) {
      arguments.emplace_back(KernelArgumentLocal{static_cast<int64_t>(input.localSize)});
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
      arguments.emplace_back(KernelArgumentAllocation{directPointer->target, directPointer->targetOffset});
    } else {
      arguments.emplace_back(KernelArgumentScalar{input.bytes});
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
  auto launched = state.client->call(Launch{ProgramId{static_cast<int64_t>(kernel.module->programId)}, kernel.name, groupSize, groupCount,
                                            globalSize, 3, Coordinates{}, 0, 0, std::move(arguments), std::move(launchRelocations)});
  if (!launched) {
    rememberRemoteError(state, launched.error().detail);
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }

  // A command-list worker must not invent a kernel-duration limit. User-facing
  // event/fence/queue synchronization owns the Level Zero timeout semantics;
  // each RPC exchange still retains its independent transport-progress timeout.
  gemstone::execution::CompletionResult result;
  std::string completionError;
  if (!gemstone::execution::collect_completion(*state.client, launched.value(), result, completionError)) {
    rememberRemoteError(state, completionError);
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  if (mirrorDirtyRangesFromServer(state, result.dirty_ranges, performance) != 0) {
    rememberRemoteError(state, remoteError(state));
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  for (const auto &relocation : result.pointer_relocations) {
    const auto *sourceId = std::get_if<RelocationSourceAllocation>(&relocation.source);
    if (!sourceId) {
      rememberRemoteError(state, "execution returned an invalid pointer relocation");
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
    const auto source = findAllocationById(static_cast<uint64_t>(sourceId->id.value));
    const auto target = findAllocationById(static_cast<uint64_t>(relocation.target.value));
    const auto sourceOffset = static_cast<uint64_t>(relocation.sourceOffset.value);
    const auto targetOffset = static_cast<uint64_t>(relocation.targetOffset.value);
    if (!source || !target || sourceOffset > source->size || sizeof(uintptr_t) > source->size - sourceOffset
        || targetOffset > target->size) {
      rememberRemoteError(state, "execution returned an invalid pointer relocation");
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
    const auto address = reinterpret_cast<uintptr_t>(target->base) + targetOffset;
    std::memcpy(static_cast<uint8_t *>(source->base) + sourceOffset, &address, sizeof(address));
  }
  if (std::getenv("GEMSTONE_PERF_TRACE") != nullptr) {
    const auto elapsed = std::chrono::duration_cast<std::chrono::microseconds>(std::chrono::steady_clock::now() - started).count();
    std::fprintf(stderr, "Gemstone Level Zero perf: kernel=%s upload=%llu download=%llu scan=%llu relocations=%llu elapsed_us=%lld\n",
                 kernel.name.c_str(), static_cast<unsigned long long>(performance.uploadedBytes),
                 static_cast<unsigned long long>(performance.downloadedBytes),
                 static_cast<unsigned long long>(performance.relocationScanBytes),
                 static_cast<unsigned long long>(performance.relocationCount), static_cast<long long>(elapsed));
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL listLaunch(ze_command_list_handle_t handle, ze_kernel_handle_t kernelHandle, const ze_group_count_t *groups,
                                  ze_event_handle_t signal, uint32_t waitCount, ze_event_handle_t *waits) {
  auto *kernel = cast<Kernel>(kernelHandle, Kind::Kernel);
  auto *list = cast<CommandList>(handle, Kind::List);
  if (!kernel || !list || !groups) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (kernel->module->context.get() != list->context.get()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  const auto copiedGroups = *groups;
  if (std::getenv("GEMSTONE_TRACE") != nullptr && static_cast<uint64_t>(kernel->groupX) * copiedGroups.groupCountX > 1000)
    std::fprintf(stderr, "Gemstone Level Zero: appending %s with group %ux%ux%u count %ux%ux%u\n", kernel->name.c_str(), kernel->groupX,
                 kernel->groupY, kernel->groupZ, copiedGroups.groupCountX, copiedGroups.groupCountY, copiedGroups.groupCountZ);
  HandleUse<Event> retainedSignal;
  EventUses retainedWaits;
  const auto retained = retainCommandEvents(list->context.get(), signal, waitCount, waits, retainedSignal, retainedWaits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  KernelLaunch launch{HandleUse<Kernel>(kernel),
                      kernel->module,
                      kernel->name,
                      kernel->groupX,
                      kernel->groupY,
                      kernel->groupZ,
                      kernel->indirect != 0,
                      kernel->arguments,
                      kernel->parameters,
                      retainKernelAllocations(*kernel)};
  auto allocationLifetime = std::make_shared<std::vector<AllocationRef>>(std::move(launch.allocations));
  return append(
      list,
      [launch = std::move(launch), copiedGroups, signal = std::move(retainedSignal), waits = std::move(retainedWaits), allocationLifetime] {
        (void)allocationLifetime;
        auto result = waitDependencies(waits);
        if (result == ZE_RESULT_SUCCESS) result = launchRpc(launch, copiedGroups);
        return result == ZE_RESULT_SUCCESS ? signalEvent(signal.get()) : result;
      },
      [allocationLifetime] { allocationLifetime->clear(); });
}

// UMF uses the reported allocation page size as its minimum slab alignment.
// Keep the allocator and zeMemGetAllocProperties behaviour in agreement.
constexpr size_t AllocationPageSize = 4096U;

void *alignedAllocate(size_t size, size_t alignment) {
  const size_t actualAlignment = std::max(alignment, AllocationPageSize);
  void *value = nullptr;
  return posix_memalign(&value, actualAlignment, std::max(size, size_t{1})) == 0 ? value : nullptr;
}

ze_result_t allocate(Context *context, size_t size, size_t alignment, ze_memory_type_t type, void **result) {
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (alignment != 0 && (alignment & (alignment - 1)) != 0) return ZE_RESULT_ERROR_UNSUPPORTED_ALIGNMENT;
  auto &state = remoteExecution();
  if (state.mode == RemoteMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (state.mode == RemoteMode::Ready && size > static_cast<size_t>(std::numeric_limits<int32_t>::max()))
    return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  *result = alignedAllocate(size, alignment);
  if (!*result) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  // Device allocations are uninitialized by the Level Zero specification, but
  // deterministic zero-fill keeps empty-range kernels observable and mirrors
  // the fresh allocation behaviour of the physical oracle used by the suite.
  std::memset(*result, 0, std::max(size, size_t{1}));
  uint64_t allocationId = 0;
  if (state.mode == RemoteMode::Ready) {
    std::lock_guard rpcLock(rpcTransactionMutex);
    auto registered = state.client->call(RegisterAllocation{rpcKind(type), static_cast<int64_t>(size)});
    if (!registered) {
      rememberRemoteError(state, remoteError(state));
      std::free(*result);
      *result = nullptr;
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
    allocationId = static_cast<uint64_t>(registered.value().value);
  }
  std::lock_guard lock(allocationsMutex);
  allocations.emplace(*result,
                      std::make_shared<Allocation>(Allocation{HandleUse<Context>(context), *result, size, type, allocationId, false, {}}));
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
  auto &state = remoteExecution();
  std::lock_guard rpcLock(rpcTransactionMutex);
  const bool released = state.mode != RemoteMode::Ready
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
ze_result_t ZE_APICALL memProperties(ze_context_handle_t contextHandle, const void *pointer, ze_memory_allocation_properties_t *properties,
                                     ze_device_handle_t *device) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (!pointer) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::lock_guard lock(allocationsMutex);
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (allocation->context.get() == context && address >= start && address - start < allocation->size) {
      properties->type = allocation->type;
      properties->id = allocation->id;
      properties->pageSize = AllocationPageSize;
      if (device) *device = reinterpret_cast<ze_device_handle_t>(&globalDevice);
      return ZE_RESULT_SUCCESS;
    }
  }
  return ZE_RESULT_ERROR_INVALID_ARGUMENT;
}
ze_result_t ZE_APICALL memRange(ze_context_handle_t contextHandle, const void *pointer, void **base, size_t *size) {
  auto *context = cast<Context>(contextHandle, Kind::Context);
  if (!context) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!base || !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::lock_guard lock(allocationsMutex);
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  for (const auto &[candidate, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(candidate);
    if (allocation->context.get() == context && address >= start && address < start + allocation->size) {
      *base = candidate;
      *size = allocation->size;
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
  *result = reinterpret_cast<ze_event_pool_handle_t>(new EventPool(context, desc->flags));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventPoolDestroy(ze_event_pool_handle_t handle) { return destroyHandle(cast<EventPool>(handle, Kind::EventPool)); }
ze_result_t ZE_APICALL eventCreate(ze_event_pool_handle_t poolHandle, const ze_event_desc_t *, ze_event_handle_t *result) {
  auto *pool = cast<EventPool>(poolHandle, Kind::EventPool);
  if (!pool) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_event_handle_t>(new Event(pool));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL eventDestroy(ze_event_handle_t handle) { return destroyHandle(cast<Event>(handle, Kind::Event)); }
ze_result_t ZE_APICALL eventSignal(ze_event_handle_t handle) { return signalEvent(cast<Event>(handle, Kind::Event)); }
ze_result_t ZE_APICALL eventSync(ze_event_handle_t handle, uint64_t timeout) {
  return waitEvent(cast<Event>(handle, Kind::Event), timeout);
}
ze_result_t ZE_APICALL eventStatus(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(event->mutex);
  return event->signalled ? ZE_RESULT_SUCCESS : ZE_RESULT_NOT_READY;
}
ze_result_t ZE_APICALL eventReset(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle, Kind::Event);
  if (!event) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(event->mutex);
  event->signalled = false;
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
  result->global = {event->kernelStart, event->kernelEnd};
  result->context = result->global;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL fenceCreate(ze_command_queue_handle_t queueHandle, const ze_fence_desc_t *, ze_fence_handle_t *result) {
  auto *queue = cast<Queue>(queueHandle, Kind::Queue);
  if (!queue) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!result) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_fence_handle_t>(new Fence(queue));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL fenceDestroy(ze_fence_handle_t handle) { return destroyHandle(cast<Fence>(handle, Kind::Fence)); }
ze_result_t ZE_APICALL fenceSync(ze_fence_handle_t handle, uint64_t timeout) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  return !fence ? ZE_RESULT_ERROR_INVALID_NULL_HANDLE : waitEvent(&fence->event, timeout);
}
ze_result_t ZE_APICALL fenceStatus(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(fence->event.mutex);
  return fence->event.signalled ? ZE_RESULT_SUCCESS : ZE_RESULT_NOT_READY;
}
ze_result_t ZE_APICALL fenceReset(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  if (!fence) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(fence->event.mutex);
  fence->event.signalled = false;
  return ZE_RESULT_SUCCESS;
}

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
  std::vector<std::pair<const uint8_t *, size_t>> inputs;
  const auto *extension = static_cast<const ze_base_desc_t *>(desc->pNext);
  while (extension) {
    if (extension->stype == ZE_STRUCTURE_TYPE_MODULE_PROGRAM_EXP_DESC) {
      const auto *program = reinterpret_cast<const ze_module_program_exp_desc_t *>(extension);
      if (program->count == 0 || !program->inputSizes || !program->pInputModules) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
      inputs.reserve(program->count);
      for (uint32_t index = 0; index < program->count; ++index) {
        if (program->inputSizes[index] == 0 || program->pInputModules[index] == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
        inputs.emplace_back(program->pInputModules[index], program->inputSizes[index]);
      }
      break;
    }
    extension = static_cast<const ze_base_desc_t *>(extension->pNext);
  }
  if (inputs.empty()) {
    if (desc->inputSize == 0 || !desc->pInputModule) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
    inputs.emplace_back(static_cast<const uint8_t *>(desc->pInputModule), desc->inputSize);
  }
  if (inputs.size() != 1) {
    if (log)
      *log = reinterpret_cast<ze_module_build_log_handle_t>(
          new BuildLog("Gemstone does not support composing multiple independent SPIR-V modules"));
    return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  }
  uint64_t programId = 0;
  ModuleBackend backend = ModuleBackend::TransferOnly;
  auto &state = remoteExecution();
  if (state.mode == RemoteMode::Misconfigured) {
    if (log) *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog(state.error));
    return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
  }
  if (state.mode == RemoteMode::Ready) {
    std::lock_guard rpcLock(rpcTransactionMutex);
    const auto [image, size] = inputs.front();
    if (size > std::numeric_limits<uint32_t>::max()) {
      if (log) *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog("SPIR-V module is too large"));
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
    auto loaded = state.client->call(LoadProgram{"spirv-binary", std::vector<uint8_t>(image, image + size)});
    if (!loaded) {
      rememberRemoteError(state, remoteError(state));
      if (log) *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog(state.error));
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
    programId = static_cast<uint64_t>(loaded.value().value);
    backend = ModuleBackend::RPC;
  }
  std::vector<uint8_t> bytes(inputs.front().first, inputs.front().first + inputs.front().second);
  *result = reinterpret_cast<ze_module_handle_t>(new Module(context, std::move(bytes), programId, backend));
  if (log) *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog(""));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL moduleDestroy(ze_module_handle_t handle) {
  auto *module = cast<Module>(handle, Kind::Module);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (module->users.load(std::memory_order_acquire) != 0) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  if (module->backend == ModuleBackend::RPC) {
    auto &state = remoteExecution();
    std::lock_guard rpcLock(rpcTransactionMutex);
    if (state.mode == RemoteMode::Ready) {
      if (!state.client->call(UnloadProgram{ProgramId{static_cast<int64_t>(module->programId)}})) {
        rememberRemoteError(state, remoteError(state));
        delete module;
        return ZE_RESULT_ERROR_DEVICE_LOST;
      }
    }
  }
  delete module;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL moduleDynamicLink(uint32_t count, ze_module_handle_t *modules, ze_module_build_log_handle_t *log) {
  if (count == 0 || !modules) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  for (uint32_t index = 0; index < count; ++index)
    if (cast<Module>(modules[index], Kind::Module) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (log)
    *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog("Gemstone does not support dynamic linking of SPIR-V modules"));
  return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
}
ze_result_t ZE_APICALL moduleProperties(ze_module_handle_t handle, ze_module_properties_t *properties) {
  if (cast<Module>(handle, Kind::Module) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!properties) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->flags = 0;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL moduleNativeBinary(ze_module_handle_t handle, size_t *size, uint8_t *binary) {
  auto *module = cast<Module>(handle, Kind::Module);
  if (!module) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const size_t required = module->bytes.size();
  if (!binary) {
    *size = required;
    return ZE_RESULT_SUCCESS;
  }
  if (*size < required) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  if (required != 0) std::memcpy(binary, module->bytes.data(), required);
  *size = required;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL buildLogDestroy(ze_module_build_log_handle_t handle) {
  return destroyHandle(cast<BuildLog>(handle, Kind::BuildLog));
}
ze_result_t ZE_APICALL buildLogString(ze_module_build_log_handle_t handle, size_t *size, char *text) {
  auto *log = cast<BuildLog>(handle, Kind::BuildLog);
  if (!log || !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const size_t required = log->text.size() + 1;
  if (!text) *size = required;
  else {
    const size_t copied = std::min(*size, required);
    std::memcpy(text, log->text.c_str(), copied);
    if (copied != 0) text[copied - 1] = '\0';
    *size = copied;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL kernelCreate(ze_module_handle_t moduleHandle, const ze_kernel_desc_t *desc, ze_kernel_handle_t *result) {
  auto *module = cast<Module>(moduleHandle, Kind::Module);
  if (!module || !desc || !result || !desc->pKernelName) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto *kernel = new Kernel(module, desc->pKernelName);
  if (module->backend == ModuleBackend::RPC) {
    auto &state = remoteExecution();
    if (state.mode != RemoteMode::Ready) {
      rememberRemoteError(state, remoteError(state));
      delete kernel;
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
    auto described = state.client->call(DescribeKernel{ProgramId{static_cast<int64_t>(module->programId)}, kernel->name});
    if (!described) {
      rememberRemoteError(state, remoteError(state));
      delete kernel;
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
    kernel->parameters.reserve(described.value().parameters.size());
    for (const auto &parameter : described.value().parameters) {
      const bool directPointer = parameter.kind == KernelParameterKind::Allocation;
      kernel->parameters.push_back({parameter.kind == KernelParameterKind::Scalar && parameter.containsPointers,
                                    parameter.kind == KernelParameterKind::Local, directPointer || parameter.containsPointers});
    }
  } else kernel->parameters = spirvKernelParameterMetadata(module->bytes, kernel->name);
  *result = reinterpret_cast<ze_kernel_handle_t>(kernel);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelDestroy(ze_kernel_handle_t handle) { return destroyHandle(cast<Kernel>(handle, Kind::Kernel)); }
ze_result_t ZE_APICALL kernelGroup(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (!kernel) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  kernel->groupX = x;
  kernel->groupY = y;
  kernel->groupZ = z;
  return ZE_RESULT_SUCCESS;
}
uint32_t largestDivisorAtMost(uint32_t value, uint32_t limit) {
  for (uint32_t candidate = std::min(value, limit); candidate > 1; --candidate)
    if (value % candidate == 0) return candidate;
  return 1;
}

ze_result_t ZE_APICALL kernelSuggest(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z, uint32_t *gx, uint32_t *gy,
                                     uint32_t *gz) {
  if (cast<Kernel>(handle, Kind::Kernel) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!gx || !gy || !gz) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (x == 0 || y == 0 || z == 0) return ZE_RESULT_ERROR_INVALID_GLOBAL_WIDTH_DIMENSION;

  // Unified Runtime requires every suggested dimension to divide its global
  // dimension when the SYCL nd_range did not provide a local size. Preserve
  // the driver's x-major preference while staying within the advertised
  // 1024-work-item limit.
  *gx = largestDivisorAtMost(x, 256);
  *gy = largestDivisorAtMost(y, std::min(4U, 1024U / *gx));
  *gz = largestDivisorAtMost(z, std::min(4U, 1024U / (*gx * *gy)));
  if (std::getenv("GEMSTONE_TRACE") != nullptr && x > 256)
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
  properties->requiredGroupSizeX = 0;
  properties->requiredGroupSizeY = 0;
  properties->requiredGroupSizeZ = 0;
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
  if (!cast<Kernel>(handle, Kind::Kernel)) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (!size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  constexpr uint32_t required = 1;
  if (!attributes) {
    *size = required;
    return ZE_RESULT_SUCCESS;
  }
  if (!*attributes) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (*size < required) {
    *size = required;
    return ZE_RESULT_ERROR_INVALID_SIZE;
  }
  **attributes = '\0';
  *size = required;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernelName(ze_kernel_handle_t handle, size_t *size, char *name) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (!kernel || !size) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const size_t required = kernel->name.size() + 1;
  if (!name) *size = required;
  else {
    const auto copied = std::min(*size, required);
    std::memcpy(name, kernel->name.c_str(), copied);
    *size = copied;
  }
  return ZE_RESULT_SUCCESS;
}

template <class T> ze_result_t emptyTable(ze_api_version_t version, T *table) {
  const auto checked = checkVersion(version, table);
  if (checked == ZE_RESULT_SUCCESS) gemstone_levelzero_ddi::clear(table);
  return checked;
}

} // namespace

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
table->pfnAppendMemoryCopyRegion = listCopyRegion;
table->pfnAppendMemoryFill = listFill;
table->pfnAppendMemoryPrefetch = listPrefetch;
table->pfnAppendMemAdvise = listAdvise;
table->pfnAppendSignalEvent = listSignal;
table->pfnAppendWaitOnEvents = listWait;
table->pfnAppendEventReset = listResetEvent;
table->pfnAppendLaunchKernel = listLaunch;
table->pfnAppendLaunchCooperativeKernel = listLaunch;
table->pfnHostSynchronize = [](ze_command_list_handle_t, uint64_t) { return ZE_RESULT_SUCCESS; };
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

#define EMPTY_TABLE(name, type)                                                                                                            \
  TABLE_BEGIN(name, type);                                                                                                                 \
  TABLE_END
EMPTY_TABLE(zeGetImageProcAddrTable, ze_image_dditable_t)
EMPTY_TABLE(zeGetPhysicalMemProcAddrTable, ze_physical_mem_dditable_t)
EMPTY_TABLE(zeGetSamplerProcAddrTable, ze_sampler_dditable_t)
EMPTY_TABLE(zeGetVirtualMemProcAddrTable, ze_virtual_mem_dditable_t)

// These API families are intentionally not implemented by the execution facade yet.
// Publishing their DDI entry points with empty tables makes that decision explicit
// while preserving the implementation check for the loader-facing ABI.
EMPTY_TABLE(zeGetCommandListExpProcAddrTable, ze_command_list_exp_dditable_t)
EMPTY_TABLE(zeGetDeviceExpProcAddrTable, ze_device_exp_dditable_t)
EMPTY_TABLE(zeGetDriverExpProcAddrTable, ze_driver_exp_dditable_t)
EMPTY_TABLE(zeGetEventExpProcAddrTable, ze_event_exp_dditable_t)
EMPTY_TABLE(zeGetExecutableGraphProcAddrTable, ze_executable_graph_dditable_t)
EMPTY_TABLE(zeGetFabricEdgeExpProcAddrTable, ze_fabric_edge_exp_dditable_t)
EMPTY_TABLE(zeGetFabricVertexExpProcAddrTable, ze_fabric_vertex_exp_dditable_t)
EMPTY_TABLE(zeGetGraphProcAddrTable, ze_graph_dditable_t)
EMPTY_TABLE(zeGetImageExpProcAddrTable, ze_image_exp_dditable_t)
EMPTY_TABLE(zeGetKernelExpProcAddrTable, ze_kernel_exp_dditable_t)
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
