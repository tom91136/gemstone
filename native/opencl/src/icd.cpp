#include <algorithm>
#include <array>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <deque>
#include <functional>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <new>
#include <set>
#include <sstream>
#include <string>
#include <string_view>
#include <thread>
#include <utility>
#include <vector>

#include <stdint.h>

#include <aspartame/string.hpp>
#include <aspartame/string_view.hpp>
#include <aspartame/vector.hpp>

#include "gemstone_execution_rpc.hpp"
#include "gemstone_opencl_dispatch.hpp"
#include <gemstone/bytes.hpp>
#include <gemstone/trace.hpp>

#include "../../spirv/specialization.hpp"
#include "compiler.hpp"
#include "completion.hpp"
#include "objects.hpp"

namespace {

using namespace aspartame;
using namespace gemstone::execution;

using namespace gemstone::opencl;

std::mutex liveObjectsMutex;
std::set<const void *> liveObjects;

void registerLiveObject(const void *value) {
  std::lock_guard lock(liveObjectsMutex);
  liveObjects.insert(value);
}

void unregisterLiveObject(const void *value) {
  std::lock_guard lock(liveObjectsMutex);
  liveObjects.erase(value);
}

bool isLiveObject(const void *value) {
  std::lock_guard lock(liveObjectsMutex);
  return liveObjects.find(value) != liveObjects.end();
}

template <class T> T *checked(T *value, Kind kind) {
  if (!value || !isLiveObject(value) || value->header.magic != Magic || value->header.kind != kind) return nullptr;
  return value;
}

template <class T> void retain(T *value) { value->header.references.fetch_add(1, std::memory_order_relaxed); }

template <class T> bool release(T *value) { return value->header.references.fetch_sub(1, std::memory_order_acq_rel) == 1; }

struct Remote {
  std::once_flag initialized;
  std::unique_ptr<Client> client;
  bool ready = false;
  std::string error;
};

Remote remote;
std::mutex remoteMutex;
std::mutex kernelSubmissionMutex;
std::mutex pointerRelocationsMutex;
std::mutex allocationsMutex;
std::mutex eventsMutex;
std::condition_variable eventsCondition;
std::map<uintptr_t, std::shared_ptr<Allocation>> allocations;
std::map<uintptr_t, std::shared_ptr<BufferMapping>> svmMappings;
std::vector<_cl_event *> liveEvents;
std::once_flag dispatchInitialized;
cl_icd_dispatch dispatch{};
_cl_platform_id platform;
_cl_device_id device;
std::atomic_uint64_t nextContextIdentity{1};

struct OpenCLIdentity {
  const char *platformName;
  const char *deviceName;
  const char *vendor;
  cl_uint vendorId;
};

OpenCLIdentity openclIdentity() {
  const char *profile = std::getenv("GEMSTONE_OPENCL_PROFILE");
  if (profile) {
    if (std::strcmp(profile, "amd") == 0)
      return {"AMD Accelerated Parallel Processing", "AMD Gemstone GPU", "Advanced Micro Devices, Inc.", 0x1002};
    if (std::strcmp(profile, "nvidia") == 0) return {"NVIDIA CUDA", "NVIDIA Gemstone GPU", "NVIDIA Corporation", 0x10de};
    if (std::strcmp(profile, "rusticl-amd") == 0) return {"rusticl", "AMD Gemstone GPU", "Advanced Micro Devices, Inc.", 0x1002};
    if (std::strcmp(profile, "rusticl-intel") == 0) return {"rusticl", "Intel Gemstone GPU", "Intel(R) Corporation", 0x8086};
    if (std::strcmp(profile, "rusticl-llvmpipe") == 0) return {"rusticl", "llvmpipe Gemstone GPU", "Mesa", 0x10005};
  }
  return {"Intel(R) OpenCL Gemstone Graphics", "Gemstone SPIR-V GPU", "Intel(R) Corporation", 0x8086};
}

uint64_t nowNanos() {
  return static_cast<uint64_t>(
      std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now().time_since_epoch()).count());
}

void notifyEventCallbacks(_cl_event *event) {
  std::vector<_cl_event::Callback> ready;
  cl_int status = CL_COMPLETE;
  {
    std::lock_guard lock(eventsMutex);
    status = event->status;
    for (auto callback = event->callbacks.begin(); callback != event->callbacks.end();) {
      if (status < 0 || status <= callback->trigger) {
        ready.push_back(*callback);
        callback = event->callbacks.erase(callback);
      } else ++callback;
    }
  }
  for (const auto &callback : ready)
    callback.function(reinterpret_cast<cl_event>(event), status, callback.user);
}

Remote &remoteState() {
  std::call_once(remote.initialized, [] {
    remote.client = Client::fromEnvironment();
    if (!remote.client) {
      remote.error = "GEMSTONE_RPC_CLIENT is absent or invalid";
      return;
    }
    if (!remote.client->call(Ping{})) {
      remote.error = remote.client->error();
      remote.client.reset();
      return;
    }
    remote.ready = true;
  });
  return remote;
}

const char *remoteError() {
  const auto &state = remoteState();
  if (state.client) {
    thread_local std::string message;
    message = state.client->error();
    if (!message.empty()) return message.c_str();
  }
  return state.error.empty() ? "Gemstone execution RPC failed" : state.error.c_str();
}

bool writeRemote(Client &client, uint64_t id, uint64_t offset, const void *bytes, size_t size) {
  constexpr size_t chunkLimit = MaximumPayload / 2;
  const auto *source = static_cast<const uint8_t *>(bytes);
  while (size != 0) {
    const auto chunk = std::min(size, chunkLimit);
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
  constexpr size_t chunkLimit = MaximumPayload / 2;
  auto *destination = static_cast<uint8_t *>(bytes);
  while (size != 0) {
    const auto chunk = std::min(size, chunkLimit);
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

bool scanBufferPointerRelocations() {
  const char *value = std::getenv("GEMSTONE_OPENCL_SCAN_BUFFER_POINTERS");
  return value && std::strcmp(value, "1") == 0;
}

cl_ulong maxMemAllocSize() {
  // Keep the advertised limit below the remote service's practical per-allocation
  // envelope.  The environment override remains available for larger deployments.
  constexpr cl_ulong defaultSize = UINT64_C(0x10000000);
  const char *value = std::getenv("GEMSTONE_OPENCL_MAX_MEM_ALLOC_SIZE");
  if (!value || value[0] == '\0') return defaultSize;
  char *end = nullptr;
  const auto parsed = std::strtoull(value, &end, 0);
  return end != value && *end == '\0' && parsed != 0 ? static_cast<cl_ulong>(parsed) : defaultSize;
}

cl_ulong localMemSize() {
  constexpr cl_ulong defaultSize = UINT64_C(0x10000);
  const char *value = std::getenv("GEMSTONE_OPENCL_LOCAL_MEM_SIZE");
  if (!value || value[0] == '\0') return defaultSize;
  char *end = nullptr;
  const auto parsed = std::strtoull(value, &end, 0);
  return end != value && *end == '\0' && parsed != 0 ? static_cast<cl_ulong>(parsed) : defaultSize;
}

std::shared_ptr<Allocation> findPointer(const void *pointer, size_t *offset = nullptr) {
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::lock_guard lock(allocationsMutex);
  const auto entry = allocations.upper_bound(address);
  if (entry == allocations.begin()) return {};
  const auto candidate = std::prev(entry);
  const auto start = candidate->first;
  if (address >= start && address - start <= candidate->second->size) {
    if (offset) *offset = address - start;
    return candidate->second;
  }
  return {};
}

bool validRange(const std::shared_ptr<Allocation> &allocation, size_t offset, size_t size) {
  return allocation && offset <= allocation->size && size <= allocation->size - offset;
}

std::shared_ptr<Allocation> findAllocation(uint64_t id) {
  std::lock_guard lock(allocationsMutex);
  for (const auto &[_, allocation] : allocations)
    if (allocation->id == id) return allocation;
  return {};
}

bool validMemoryRange(const _cl_mem *mem, size_t offset, size_t size) { return mem && offset <= mem->size && size <= mem->size - offset; }

bool hostReadAllowed(const _cl_mem *mem) { return mem && (mem->flags & (CL_MEM_HOST_WRITE_ONLY | CL_MEM_HOST_NO_ACCESS)) == 0; }

bool hostWriteAllowed(const _cl_mem *mem) { return mem && (mem->flags & (CL_MEM_HOST_READ_ONLY | CL_MEM_HOST_NO_ACCESS)) == 0; }

void invalidatePointerRelocations(const std::shared_ptr<Allocation> &allocation, size_t offset, size_t size) {
  if (!allocation || size == 0) return;
  std::lock_guard relocationsLock(pointerRelocationsMutex);
  const size_t end = offset > std::numeric_limits<size_t>::max() - size ? std::numeric_limits<size_t>::max() : offset + size;
  for (auto it = allocation->pointerRelocations.begin(); it != allocation->pointerRelocations.end();) {
    const size_t pointerEnd = it->first > std::numeric_limits<size_t>::max() - sizeof(uint64_t) ? std::numeric_limits<size_t>::max()
                                                                                                : it->first + sizeof(uint64_t);
    if (it->first < end && pointerEnd > offset) it = allocation->pointerRelocations.erase(it);
    else ++it;
  }
}

void copyPointerRelocations(const std::shared_ptr<Allocation> &source, size_t sourceOffset, const std::shared_ptr<Allocation> &target,
                            size_t targetOffset, size_t size) {
  if (!source || !target || size < sizeof(uint64_t)) return;
  std::lock_guard relocationsLock(pointerRelocationsMutex);
  const size_t sourceEnd =
      sourceOffset > std::numeric_limits<size_t>::max() - size ? std::numeric_limits<size_t>::max() : sourceOffset + size;
  for (const auto &[offset, relocation] : source->pointerRelocations) {
    if (offset >= sourceOffset && offset <= sourceEnd - sizeof(uint64_t)) {
      const size_t targetOffsetForPointer = targetOffset + offset - sourceOffset;
      if (targetOffsetForPointer >= targetOffset) target->pointerRelocations[targetOffsetForPointer] = relocation;
    }
  }
}

size_t allocationOffset(const _cl_mem *mem, size_t offset) { return mem->origin + offset; }

std::shared_ptr<Allocation> allocate(size_t size, cl_int *error, size_t requestedAlignment = 128, bool hostBacked = false) {
  if (size == 0) {
    if (error) *error = CL_INVALID_BUFFER_SIZE;
    return {};
  }
  const size_t requested = requestedAlignment == 0 ? 128 : requestedAlignment;
  if ((requested & (requested - 1)) != 0) {
    if (error) *error = CL_INVALID_VALUE;
    return {};
  }
  const size_t alignment = std::max(sizeof(void *), requested);
  std::unique_ptr<void, Allocation::AlignedDelete> storage(::operator new(size, std::align_val_t{alignment}, std::nothrow),
                                                           Allocation::AlignedDelete{alignment});
  if (!storage) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return {};
  }
  void *base = storage.get();
  std::shared_ptr<Allocation> result;
  try {
    result = std::make_shared<Allocation>();
  } catch (const std::bad_alloc &) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return {};
  }
  result->storage = std::move(storage);
  result->size = size;
  result->hostBacked = hostBacked;
  auto &state = remoteState();
  if (state.ready) {
    std::lock_guard lock(remoteMutex);
    auto registered = state.client->call(RegisterAllocation{AllocationKind::Shared, static_cast<int64_t>(size)});
    if (!registered) {
      if (error) *error = CL_OUT_OF_RESOURCES;
      return {};
    }
    result->id = static_cast<uint64_t>(registered.value().value);
    if (!state.client->call(RegisterAllocationAddress{registered.value(),
                                                      GuestAddress{static_cast<int64_t>(reinterpret_cast<uintptr_t>(base))}, hostBacked})) {
      (void)state.client->call(ReleaseAllocation{registered.value()});
      if (error) *error = CL_OUT_OF_RESOURCES;
      return {};
    }
  }
  bool inserted = false;
  {
    std::lock_guard lock(allocationsMutex);
    const auto key = reinterpret_cast<uintptr_t>(base);
    inserted = allocations.emplace(key, result).second;
  }
  if (!inserted) {
    if (state.ready) {
      std::lock_guard lock(remoteMutex);
      (void)state.client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(result->id)}});
    }
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return {};
  }
  if (gemstone::traceEnabled("GEMSTONE_OPENCL_TRACE", "allocations"))
    std::fprintf(stderr, "gemstone alloc base=%p id=%llu size=%zu\n", base, static_cast<unsigned long long>(result->id), size);
  return result;
}

cl_int releaseAllocation(const std::shared_ptr<Allocation> &allocation, bool releaseRuntimeBase = false) {
  if (!allocation) return CL_SUCCESS;
  if (allocation->released.exchange(true, std::memory_order_acq_rel)) return CL_SUCCESS;
  {
    std::lock_guard lock(allocationsMutex);
    const auto entry = allocations.find(reinterpret_cast<uintptr_t>(allocation->data()));
    if (entry != allocations.end() && entry->second.get() == allocation.get()) allocations.erase(entry);
  }
  auto &state = remoteState();
  cl_int result = CL_SUCCESS;
  if (state.ready) {
    std::lock_guard lock(remoteMutex);
    if (!state.client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(allocation->id)}})) result = CL_OUT_OF_RESOURCES;
  }
  if (!allocation->runtimeOwnsBase || releaseRuntimeBase) allocation->storage.reset();
  return result;
}

cl_int writeAllocation(const std::shared_ptr<Allocation> &allocation, size_t offset, const void *bytes, size_t size) {
  if (!validRange(allocation, offset, size) || (size != 0 && !bytes)) return CL_INVALID_VALUE;
  invalidatePointerRelocations(allocation, offset, size);
  if (size != 0) std::memcpy(static_cast<uint8_t *>(allocation->data()) + offset, bytes, size);
  auto &state = remoteState();
  if (state.ready && size != 0 && !allocation->hostBacked) {
    std::lock_guard lock(remoteMutex);
    if (!writeRemote(*state.client, allocation->id, offset, bytes, size)) return CL_OUT_OF_RESOURCES;
  }
  return CL_SUCCESS;
}

cl_int readAllocation(const std::shared_ptr<Allocation> &allocation, size_t offset, void *bytes, size_t size) {
  if (!validRange(allocation, offset, size) || (size != 0 && !bytes)) return CL_INVALID_VALUE;
  auto &state = remoteState();
  if (state.ready && size != 0 && !allocation->hostBacked) {
    std::lock_guard lock(remoteMutex);
    if (!readRemote(*state.client, allocation->id, offset, bytes, size)) return CL_OUT_OF_RESOURCES;
    std::memcpy(static_cast<uint8_t *>(allocation->data()) + offset, bytes, size);
  } else if (size != 0) {
    std::memcpy(bytes, static_cast<uint8_t *>(allocation->data()) + offset, size);
  }
  std::lock_guard relocationsLock(pointerRelocationsMutex);
  // The execution service stores device addresses in linked structures. Kernel
  // completion translates them for the host, but a later map reads the raw
  // remote bytes again. Reapply the known translations to the mapped range.
  for (const auto &[pointerOffset, target] : allocation->pointerRelocations) {
    if (pointerOffset < offset || pointerOffset - offset > size || sizeof(uintptr_t) > size - (pointerOffset - offset)) continue;
    if (!target.first || target.first->released.load(std::memory_order_acquire) || target.second > target.first->size) continue;
    const uintptr_t address = reinterpret_cast<uintptr_t>(target.first->data()) + target.second;
    std::memcpy(static_cast<uint8_t *>(allocation->data()) + pointerOffset, &address, sizeof(address));
    std::memcpy(static_cast<uint8_t *>(bytes) + pointerOffset - offset, &address, sizeof(address));
  }
  return CL_SUCCESS;
}

template <class T> cl_int outputScalar(size_t capacity, void *output, size_t *written, const T &value) {
  if (written) *written = sizeof(T);
  if (!output) return CL_SUCCESS;
  if (capacity < sizeof(T)) return CL_INVALID_VALUE;
  std::memcpy(output, &value, sizeof(T));
  return CL_SUCCESS;
}

cl_int outputBytes(size_t capacity, void *output, size_t *written, const void *bytes, size_t size) {
  if (written) *written = size;
  if (!output) return CL_SUCCESS;
  if (capacity < size) return CL_INVALID_VALUE;
  if (size != 0) std::memcpy(output, bytes, size);
  return CL_SUCCESS;
}

cl_int outputString(size_t capacity, void *output, size_t *written, const std::string &value) {
  return outputBytes(capacity, output, written, value.c_str(), value.size() + 1);
}

bool validPlatform(cl_platform_id value) { return checked(reinterpret_cast<_cl_platform_id *>(value), Kind::Platform) == &platform; }
bool validDevice(cl_device_id value) { return checked(reinterpret_cast<_cl_device_id *>(value), Kind::Device) == &device; }

void initializeDispatch();

size_t kernelCount(const std::string &names) {
  if (names.empty()) return 0;
  return static_cast<size_t>(std::count(names.begin(), names.end(), ';')) + 1;
}

bool containsKernelName(const std::string &names, const std::string &name) {
  auto parts = names ^ split(';');
  if (!parts.empty() && parts.back().empty()) parts.pop_back();
  return parts ^ contains(name);
}

bool hasBuildOption(const char *options, std::string_view option) {
  return options && (std::string_view(options) ^ contains_slice(option));
}

constexpr uint64_t ProgramBinaryMagic = UINT64_C(0x47454d4f434c4231);
constexpr size_t ProgramBinaryHeaderSize = 32;

std::vector<uint8_t> programBinary(const _cl_program *program) {
  const auto &payload = program->binaryType == CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT ? program->bitcode : program->il;
  if (payload.empty()) return {};
  if (program->binaryType == CL_PROGRAM_BINARY_TYPE_NONE) return payload;

  const std::string &source = program->buildSource.empty() ? program->source : program->buildSource;
  if (source.size() > std::numeric_limits<uint64_t>::max() - ProgramBinaryHeaderSize
      || payload.size() > std::numeric_limits<uint64_t>::max() - ProgramBinaryHeaderSize - source.size())
    return {};
  const size_t size = ProgramBinaryHeaderSize + source.size() + payload.size();
  std::vector<uint8_t> result(size);
  uint32_t type = static_cast<uint32_t>(program->binaryType);
  uint64_t sourceSize = source.size();
  uint64_t ilSize = payload.size();
  std::memcpy(result.data(), &ProgramBinaryMagic, sizeof(ProgramBinaryMagic));
  std::memcpy(result.data() + 8, &type, sizeof(type));
  std::memcpy(result.data() + 16, &sourceSize, sizeof(sourceSize));
  std::memcpy(result.data() + 24, &ilSize, sizeof(ilSize));
  if (!source.empty()) std::memcpy(result.data() + ProgramBinaryHeaderSize, source.data(), source.size());
  std::memcpy(result.data() + ProgramBinaryHeaderSize + source.size(), payload.data(), payload.size());
  return result;
}

bool decodeProgramBinary(const unsigned char *bytes, size_t size, cl_program_binary_type *type, std::string *source,
                         std::vector<uint8_t> *il) {
  if (!bytes || size < ProgramBinaryHeaderSize) return false;
  const auto magic = gemstone::readNative<uint64_t>(bytes);
  if (magic != ProgramBinaryMagic) return false;
  const auto encodedType = gemstone::readNative<uint32_t>(bytes, 8);
  const auto sourceSize = gemstone::readNative<uint64_t>(bytes, 16);
  const auto ilSize = gemstone::readNative<uint64_t>(bytes, 24);
  if (encodedType != CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT && encodedType != CL_PROGRAM_BINARY_TYPE_LIBRARY
      && encodedType != CL_PROGRAM_BINARY_TYPE_EXECUTABLE)
    return false;
  if (sourceSize > size - ProgramBinaryHeaderSize || ilSize > size - ProgramBinaryHeaderSize - sourceSize
      || ProgramBinaryHeaderSize + sourceSize + ilSize != size)
    return false;
  *type = static_cast<cl_program_binary_type>(encodedType);
  source->assign(reinterpret_cast<const char *>(bytes + ProgramBinaryHeaderSize), static_cast<size_t>(sourceSize));
  il->assign(bytes + ProgramBinaryHeaderSize + sourceSize, bytes + ProgramBinaryHeaderSize + sourceSize + ilSize);
  return !il->empty();
}

bool expandHeaders(const std::string &source, cl_uint count, const cl_program *headers, const char **names, std::string *expanded,
                   std::string *error) {
  *expanded = source;
  for (cl_uint index = 0; index < count; ++index) {
    auto *header = checked(reinterpret_cast<_cl_program *>(headers[index]), Kind::Program);
    if (!header || names[index] == nullptr) {
      if (error) *error = "invalid OpenCL embedded header";
      return false;
    }
    const std::string quoted = std::string("#include \"") + names[index] + "\"";
    const std::string angled = std::string("#include <") + names[index] + ">";
    size_t position = 0;
    while ((position = expanded->find(quoted, position)) != std::string::npos) {
      expanded->replace(position, quoted.size(), header->source);
      position += header->source.size();
    }
    position = 0;
    while ((position = expanded->find(angled, position)) != std::string::npos) {
      expanded->replace(position, angled.size(), header->source);
      position += header->source.size();
    }
  }
  return true;
}

cl_int waitQueue(_cl_command_queue *queue) {
  if (!queue) return CL_SUCCESS;
  std::unique_lock lock(queue->pendingMutex);
  queue->pendingCondition.wait(lock, [queue] { return queue->pendingCommands == 0; });
  const bool failed = std::exchange(queue->unobservedFailures, size_t{0}) != 0;
  return failed ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS;
}

cl_int waitList(_cl_command_queue *queue, cl_uint count, const cl_event *events) {
  if (count != 0 && !events) return CL_INVALID_EVENT_WAIT_LIST;
  if (count == 0 && events) return !queue ? CL_INVALID_VALUE : CL_INVALID_EVENT_WAIT_LIST;
  const auto queueResult = waitQueue(queue);
  if (queueResult != CL_SUCCESS) return queueResult;
  std::vector<_cl_event *> checkedEvents;
  checkedEvents.reserve(count);
  for (cl_uint index = 0; index < count; ++index) {
    auto *event = checked(reinterpret_cast<_cl_event *>(events[index]), Kind::Event);
    if (!event) return !queue ? CL_INVALID_EVENT : CL_INVALID_EVENT_WAIT_LIST;
    if (queue && event->context != queue->context) return CL_INVALID_CONTEXT;
    checkedEvents.push_back(event);
  }
  bool failed = false;
  std::vector<_cl_command_queue *> observedQueues;
  {
    std::unique_lock lock(eventsMutex);
    for (auto *event : checkedEvents) {
      eventsCondition.wait(lock, [event] { return event->status == CL_COMPLETE || event->status < 0; });
      if (event->status < 0) {
        failed = true;
        if (event->queue && !event->failureObserved) {
          event->failureObserved = true;
          observedQueues.push_back(event->queue);
        }
      }
    }
  }
  for (auto *observedQueue : observedQueues) {
    std::lock_guard lock(observedQueue->pendingMutex);
    if (observedQueue->unobservedFailures != 0) --observedQueue->unobservedFailures;
  }
  return failed ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS;
}

cl_int validateWaitList(_cl_command_queue *queue, cl_uint count, const cl_event *events) {
  if (count != 0 && !events) return CL_INVALID_EVENT_WAIT_LIST;
  if (count == 0 && events) return !queue ? CL_INVALID_VALUE : CL_INVALID_EVENT_WAIT_LIST;
  std::lock_guard lock(eventsMutex);
  for (cl_uint index = 0; index < count; ++index) {
    auto *event = checked(reinterpret_cast<_cl_event *>(events[index]), Kind::Event);
    if (!event) return !queue ? CL_INVALID_EVENT : CL_INVALID_EVENT_WAIT_LIST;
    if (queue && event->context != queue->context) return CL_INVALID_CONTEXT;
    if (event->status < 0) return CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
  }
  return CL_SUCCESS;
}

_cl_event *makeEvent(_cl_command_queue *queue, cl_command_type command, cl_event *output) {
  if (!output) return nullptr;
  auto *event = new (std::nothrow) _cl_event;
  if (!event) return nullptr;
  registerLiveObject(event);
  event->dispatch = &dispatch;
  event->queue = queue;
  event->context = queue->context;
  event->command = command;
  event->queued = nowNanos();
  event->submitted = event->queued + 1;
  event->started = event->submitted + 1;
  event->ended = event->started + 1;
  retain(queue);
  retain(queue->context);
  {
    std::lock_guard lock(eventsMutex);
    liveEvents.push_back(event);
  }
  *output = reinterpret_cast<cl_event>(event);
  return event;
}

enum class PendingStep { Blocked, Progressed, Finished };

PendingStep runPendingOnce(_cl_command_queue *queue) {
  PendingCommand command;
  {
    std::lock_guard eventLock(eventsMutex);
    std::lock_guard queueLock(queue->pendingMutex);
    if (queue->commands.empty()) {
      queue->workerRunning = false;
      return PendingStep::Finished;
    }
    auto ready = queue->commands.end();
    for (auto it = queue->commands.begin(); it != queue->commands.end(); ++it) {
      if ((!it->waitPrior || it == queue->commands.begin())
          && (it->dependencies ^ forall([](cl_event value) { return reinterpret_cast<_cl_event *>(value)->status <= CL_COMPLETE; }))) {
        ready = it;
        break;
      }
      if (it->fence || (queue->properties & CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE) == 0) break;
    }
    if (ready == queue->commands.end()) return PendingStep::Blocked;
    command = std::move(*ready);
    queue->commands.erase(ready);
  }
  cl_int result = CL_SUCCESS;
  {
    std::lock_guard lock(eventsMutex);
    for (auto dependency : command.dependencies) {
      const auto status = reinterpret_cast<_cl_event *>(dependency)->status;
      if (status < 0) {
        result = CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
        break;
      }
    }
  }
  if (result == CL_SUCCESS) {
    try {
      result = command.execute();
    } catch (const std::bad_alloc &) {
      result = CL_OUT_OF_HOST_MEMORY;
    } catch (...) {
      result = CL_OUT_OF_RESOURCES;
    }
  }
  if (command.finish) command.finish(result);
  command.finish = {};
  // Release allocation leases before the retained memory objects: their final
  // release decides whether the registry's backing allocation can be reclaimed.
  command.uses.reset();
  command.execute = {};
  for (auto dependency : command.dependencies)
    clReleaseEvent(dependency);
  if (result != CL_SUCCESS) {
    std::lock_guard lock(queue->pendingMutex);
    ++queue->unobservedFailures;
  }
  if (command.completion) {
    std::lock_guard lock(eventsMutex);
    auto *completion = reinterpret_cast<_cl_event *>(command.completion);
    completion->status = result == CL_SUCCESS ? CL_COMPLETE : result;
    completion->ended = nowNanos();
  }
  {
    std::lock_guard lock(queue->pendingMutex);
    --queue->pendingCommands;
  }
  queue->pendingCondition.notify_all();
  eventsCondition.notify_all();
  if (command.completion) {
    notifyEventCallbacks(reinterpret_cast<_cl_event *>(command.completion));
    clReleaseEvent(command.completion);
  }
  return PendingStep::Progressed;
}

class QueueScheduler {
public:
  QueueScheduler() {
    for (unsigned i = 0; i < 2; ++i)
      workers_.emplace_back([this] { run(); });
  }
  ~QueueScheduler() {
    {
      std::lock_guard lock(mutex_);
      stopping_ = true;
    }
    changed_.notify_all();
    for (auto &worker : workers_)
      worker.join();
    for (auto *queue : queues_)
      clReleaseCommandQueue(reinterpret_cast<cl_command_queue>(queue));
  }
  void submit(_cl_command_queue *queue) {
    std::lock_guard lock(mutex_);
    queues_.push_back(queue);
    changed_.notify_one();
  }

private:
  void run() {
    size_t blocked = 0;
    for (;;) {
      _cl_command_queue *queue = nullptr;
      size_t scheduled = 0;
      {
        std::unique_lock lock(mutex_);
        changed_.wait(lock, [&] { return stopping_ || !queues_.empty(); });
        if (stopping_) return;
        queue = queues_.front();
        queues_.pop_front();
        scheduled = queues_.size() + 1;
      }
      const auto step = runPendingOnce(queue);
      if (step == PendingStep::Finished) {
        blocked = 0;
        clReleaseCommandQueue(reinterpret_cast<cl_command_queue>(queue));
      } else {
        {
          std::lock_guard lock(mutex_);
          queues_.push_back(queue);
        }
        blocked = step == PendingStep::Blocked ? blocked + 1 : 0;
        if (blocked >= scheduled) {
          std::unique_lock lock(mutex_);
          changed_.wait_for(lock, std::chrono::microseconds(100));
          blocked = 0;
        }
      }
    }
  }

  std::mutex mutex_;
  std::condition_variable changed_;
  std::deque<_cl_command_queue *> queues_;
  bool stopping_ = false;
  std::vector<std::thread> workers_;
};

QueueScheduler &queueScheduler() {
  static QueueScheduler scheduler;
  return scheduler;
}

cl_int enqueuePending(_cl_command_queue *queue, cl_command_type type, std::vector<cl_event> dependencies, std::function<cl_int()> execute,
                      cl_event *output, bool waitPrior = false, bool fence = false, std::function<void(cl_int)> finish = {},
                      std::shared_ptr<AllocationUses> uses = {}) {
  cl_event completion = nullptr;
  if (output) {
    auto *created = makeEvent(queue, type, &completion);
    if (!created) {
      if (finish) finish(CL_OUT_OF_HOST_MEMORY);
      return CL_OUT_OF_HOST_MEMORY;
    }
    std::lock_guard lock(eventsMutex);
    created->status = CL_QUEUED;
    retain(created); // Separate caller and worker references before publication.
  }
  for (auto dependency : dependencies)
    clRetainEvent(dependency);
  cl_int error = CL_SUCCESS;
  {
    std::lock_guard lock(queue->pendingMutex);
    try {
      queue->commands.push_back({dependencies, completion, std::move(execute), waitPrior, fence, finish, std::move(uses)});
      ++queue->pendingCommands;
      if (!queue->workerRunning) {
        retain(queue);
        queue->workerRunning = true;
        try {
          queueScheduler().submit(queue);
        } catch (...) {
          queue->workerRunning = false;
          clReleaseCommandQueue(reinterpret_cast<cl_command_queue>(queue));
          queue->commands.pop_back();
          --queue->pendingCommands;
          throw;
        }
      }
    } catch (...) {
      error = CL_OUT_OF_HOST_MEMORY;
    }
  }
  if (error != CL_SUCCESS) {
    if (finish) finish(error);
    for (auto dependency : dependencies)
      clReleaseEvent(dependency);
    if (completion) {
      clReleaseEvent(completion);
      clReleaseEvent(completion);
    }
    return error;
  }
  // Acquire the same mutex as the readiness check to avoid lost notifications.
  {
    std::lock_guard lock(eventsMutex);
    if (output) *output = completion;
    eventsCondition.notify_all();
  }
  return CL_SUCCESS;
}

std::shared_ptr<AllocationUses> allocationUses(std::vector<std::shared_ptr<Allocation>> values) {
  return std::make_shared<AllocationUses>(std::move(values));
}

std::shared_ptr<_cl_mem> retainMemory(_cl_mem *memory) {
  auto uses = allocationUses({memory->allocation});
  retain(memory);
  return {memory, [uses = std::move(uses)](_cl_mem *value) mutable {
            uses.reset();
            clReleaseMemObject(reinterpret_cast<cl_mem>(value));
          }};
}

cl_int submitCommand(_cl_command_queue *queue, cl_command_type type, bool blocking, cl_uint count, const cl_event *waits,
                     std::function<cl_int()> execute, cl_event *output, bool waitPrior = false, bool fence = false,
                     std::function<void(cl_int)> finish = {}, std::shared_ptr<AllocationUses> uses = {}) {
  if (!queue) {
    if (finish) finish(CL_INVALID_COMMAND_QUEUE);
    return CL_INVALID_COMMAND_QUEUE;
  }
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) {
    if (finish) finish(result);
    return result;
  }
  bool pending = false;
  {
    std::lock_guard lock(eventsMutex);
    for (cl_uint index = 0; index < count; ++index)
      pending = pending || reinterpret_cast<_cl_event *>(waits[index])->status > CL_COMPLETE;
  }
  {
    std::lock_guard lock(queue->pendingMutex);
    pending = pending || queue->pendingCommands != 0;
  }
  if (!pending) {
    try {
      result = execute();
    } catch (const std::bad_alloc &) {
      result = CL_OUT_OF_HOST_MEMORY;
    } catch (...) {
      result = CL_OUT_OF_RESOURCES;
    }
    if (finish) finish(result);
    if (result == CL_SUCCESS && output && makeEvent(queue, type, output) == nullptr) return CL_OUT_OF_HOST_MEMORY;
    return result;
  }
  try {
    std::vector<cl_event> dependencies;
    for (cl_uint index = 0; index < count; ++index)
      dependencies.push_back(waits[index]);
    cl_event completion = nullptr;
    result = enqueuePending(queue, type, std::move(dependencies), std::move(execute), blocking ? &completion : output, waitPrior, fence,
                            finish, std::move(uses));
    if (!blocking || result != CL_SUCCESS) return result;
    result = clWaitForEvents(1, &completion);
    if (output) *output = completion;
    else clReleaseEvent(completion);
    return result;
  } catch (...) {
    if (finish) finish(CL_OUT_OF_HOST_MEMORY);
    return CL_OUT_OF_HOST_MEMORY;
  }
}

cl_int completeEvent(_cl_command_queue *queue, cl_command_type command, cl_uint count, const cl_event *waits, cl_event *output) {
  const bool marker = command == CL_COMMAND_MARKER;
  const bool barrier = command == CL_COMMAND_BARRIER;
  return submitCommand(queue, command, false, count, waits, [] { return CL_SUCCESS; }, output, (marker || barrier) && count == 0, barrier);
}

struct MirrorResult {
  cl_int status = CL_SUCCESS;
  uint64_t bytes = 0;
};

MirrorResult mirrorAllocations() {
  auto &state = remoteState();
  if (!state.ready) return {};
  MirrorResult result;
  std::lock_guard lock(remoteMutex);
  std::lock_guard memoryLock(allocationsMutex);
  for (const auto &[_, allocation] : allocations) {
    if (allocation->hostBacked || !allocation->externallyMutable.load(std::memory_order_relaxed)) continue;
    if (!writeRemote(*state.client, allocation->id, 0, allocation->data(), allocation->size)) return {CL_OUT_OF_RESOURCES, result.bytes};
    result.bytes += allocation->size;
  }
  return result;
}

std::vector<PointerRelocation> collectRelocations(uint64_t &scannedBytes) {
  std::lock_guard relocationsLock(pointerRelocationsMutex);
  std::vector<PointerRelocation> result;
  std::vector<std::shared_ptr<Allocation>> snapshot;
  {
    std::lock_guard lock(allocationsMutex);
    for (const auto &[_, allocation] : allocations)
      if (!allocation->hostBacked && (allocation->externallyMutable.load(std::memory_order_relaxed) || scanBufferPointerRelocations()))
        snapshot.push_back(allocation);
  }
  for (const auto &source : snapshot) {
    scannedBytes += source->size;
    const auto *bytes = static_cast<const uint8_t *>(source->data());
    for (auto it = source->pointerRelocations.begin(); it != source->pointerRelocations.end();) {
      const auto &target = it->second;
      // Typed relocation metadata must not keep a freed target registered on
      // the next launch. Leave its raw dangling pointer bytes untouched.
      if (!target.first || target.first->released.load(std::memory_order_acquire)) {
        it = source->pointerRelocations.erase(it);
        continue;
      }
      const auto targetAddress = reinterpret_cast<uintptr_t>(target.first->data()) + target.second;
      uint64_t value = 0;
      const bool inRange = it->first <= source->size && sizeof(value) <= source->size - it->first;
      if (!inRange) {
        it = source->pointerRelocations.erase(it);
        continue;
      }
      std::memcpy(&value, bytes + it->first, sizeof(value));
      if (value != static_cast<uint64_t>(targetAddress)) it = source->pointerRelocations.erase(it);
      else ++it;
    }
    for (size_t offset = 0; offset + sizeof(uint64_t) <= source->size; offset += sizeof(uint32_t)) {
      uint64_t value = 0;
      std::memcpy(&value, bytes + offset, sizeof(value));
      size_t targetOffset = 0;
      const auto target = findPointer(reinterpret_cast<const void *>(static_cast<uintptr_t>(value)), &targetOffset);
      if (target && targetOffset < target->size) {
        source->pointerRelocations[offset] = {target, targetOffset};
        result.push_back({RelocationSourceAllocation{AllocationId{static_cast<int64_t>(source->id)}},
                          LongPositive0{static_cast<int64_t>(offset)}, AllocationId{static_cast<int64_t>(target->id)},
                          LongPositive0{static_cast<int64_t>(targetOffset)}});
      }
    }
  }
  for (const auto &source : snapshot) {
    for (const auto &[offset, target] : source->pointerRelocations) {
      const auto found = std::find_if(result.begin(), result.end(), [&](const auto &relocation) {
        const auto *sourceId = std::get_if<RelocationSourceAllocation>(&relocation.source);
        return sourceId && static_cast<uint64_t>(sourceId->id.value) == source->id
               && static_cast<uint64_t>(relocation.sourceOffset.value) == offset;
      });
      if (found == result.end() && target.first && target.second < target.first->size)
        result.push_back({RelocationSourceAllocation{AllocationId{static_cast<int64_t>(source->id)}},
                          LongPositive0{static_cast<int64_t>(offset)}, AllocationId{static_cast<int64_t>(target.first->id)},
                          LongPositive0{static_cast<int64_t>(target.second)}});
    }
  }
  return result;
}

cl_int finishRemote(_cl_command_queue *queue, _cl_kernel *kernel, size_t workDimension, const size_t *offset, const size_t *global,
                    const size_t *local, cl_event *output) {
  std::unique_lock submissionLock(kernelSubmissionMutex);
  const uint64_t started = nowNanos();
  uint64_t scannedBytes = 0;
  uint64_t downloadedBytes = 0;
  auto &state = remoteState();
  if (!state.ready || kernel->program->remoteId == 0) return CL_OUT_OF_RESOURCES;
  if (workDimension == 0 || workDimension > 3 || !global) return CL_INVALID_WORK_DIMENSION;
  size_t group[3] = {1, 1, 1};
  size_t count[3] = {1, 1, 1};
  size_t exact[3] = {1, 1, 1};
  size_t origin[3] = {0, 0, 0};
  for (size_t index = 0; index < workDimension; ++index) {
    if (global[index] == 0 || global[index] > static_cast<uint64_t>(std::numeric_limits<int64_t>::max()))
      return CL_INVALID_GLOBAL_WORK_SIZE;
    if (offset && offset[index] > static_cast<size_t>(std::numeric_limits<int32_t>::max())) return CL_INVALID_GLOBAL_OFFSET;
    exact[index] = global[index];
    if (offset) origin[index] = offset[index];
    group[index] = !local ? (kernel->requiredWorkGroupSize != std::array<size_t, 3>{0, 0, 0} ? kernel->requiredWorkGroupSize[index]
                                                                                             : (index == 0 ? 32 : 1))
                          : local[index];
    if (group[index] == 0 || group[index] > static_cast<size_t>(std::numeric_limits<int32_t>::max())) return CL_INVALID_WORK_GROUP_SIZE;
    count[index] = (global[index] + group[index] - 1) / group[index];
    if (count[index] > static_cast<size_t>(std::numeric_limits<int32_t>::max())) return CL_INVALID_GLOBAL_WORK_SIZE;
  }
  if (!local && kernel->requiredWorkGroupSize != std::array<size_t, 3>{0, 0, 0}) {
    for (size_t index = workDimension; index < 3; ++index) {
      group[index] = kernel->requiredWorkGroupSize[index];
      if (group[index] == 0 || group[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_WORK_GROUP_SIZE;
    }
  }
  std::vector<gemstone::execution::KernelArgument> arguments;
  arguments.reserve(kernel->arguments.size());
  std::vector<std::vector<uint8_t>> scalarStorage(kernel->arguments.size());
  std::vector<PointerRelocation> relocations;
  for (size_t index = 0; index < kernel->arguments.size(); ++index) {
    const auto &argument = kernel->arguments[index];
    if (argument.kind == ::KernelArgument::Kind::Local) {
      arguments.emplace_back(KernelArgumentLocal{static_cast<int64_t>(argument.offset)});
    } else if (argument.kind == ::KernelArgument::Kind::Allocation) {
      arguments.emplace_back(
          KernelArgumentAllocation{AllocationId{static_cast<int64_t>(argument.allocation->id)}, static_cast<int64_t>(argument.offset)});
    } else if (argument.kind == ::KernelArgument::Kind::SVM) {
      arguments.emplace_back(
          KernelArgumentAllocation{AllocationId{static_cast<int64_t>(argument.allocation->id)}, static_cast<int64_t>(argument.offset)});
    } else {
      scalarStorage[index] = argument.bytes;
      arguments.emplace_back(KernelArgumentScalar{scalarStorage[index]});
      if (index < kernel->parameterMetadata.size() && !kernel->parameterMetadata[index].containsPointer) continue;
      scannedBytes += scalarStorage[index].size();
      for (size_t offset = 0; offset + sizeof(uint64_t) <= scalarStorage[index].size(); offset += sizeof(uint32_t)) {
        uint64_t raw = 0;
        std::memcpy(&raw, scalarStorage[index].data() + offset, sizeof(raw));
        size_t targetOffset = 0;
        const auto target = findPointer(reinterpret_cast<const void *>(static_cast<uintptr_t>(raw)), &targetOffset);
        if (target)
          relocations.push_back({RelocationSourceArgument{IntPositive0{static_cast<int32_t>(index)}},
                                 LongPositive0{static_cast<int64_t>(offset)}, AllocationId{static_cast<int64_t>(target->id)},
                                 LongPositive0{static_cast<int64_t>(targetOffset)}});
      }
    }
  }
  auto memoryRelocations = collectRelocations(scannedBytes);
  relocations.insert(relocations.end(), memoryRelocations.begin(), memoryRelocations.end());
  const auto mirrored = mirrorAllocations();
  if (mirrored.status != CL_SUCCESS) return mirrored.status;
  const uint64_t uploadedBytes = mirrored.bytes;
  const Dimensions groupSize{static_cast<int32_t>(group[0]), static_cast<int32_t>(group[1]), static_cast<int32_t>(group[2])};
  const Dimensions groupCount{static_cast<int32_t>(count[0]), static_cast<int32_t>(count[1]), static_cast<int32_t>(count[2])};
  const GlobalDimensions globalSize{static_cast<int64_t>(exact[0]), static_cast<int64_t>(exact[1]), static_cast<int64_t>(exact[2])};
  const Coordinates globalOffset{static_cast<int32_t>(origin[0]), static_cast<int32_t>(origin[1]), static_cast<int32_t>(origin[2])};
  CompletionId completion{};
  int launched;
  std::string failureMessage;
  {
    std::unique_lock lock(remoteMutex);
    auto launch = state.client->call(Launch{ProgramId{static_cast<int64_t>(kernel->program->remoteId)}, kernel->name, groupSize, groupCount,
                                            globalSize, static_cast<int32_t>(workDimension), globalOffset, 0, 0, std::move(arguments),
                                            std::move(relocations)});
    launched = launch ? 0 : -1;
    if (launch) completion = launch.value();
    else failureMessage = launch.error().detail;
    if (launched == 0) {
      auto completionClient = Client::fromEnvironment();
      gemstone::execution::CompletionResult result;
      lock.unlock();
      submissionLock.unlock();
      if (!completionClient || !gemstone::execution::collect_completion(*completionClient, completion, result, failureMessage, true))
        launched = -1;
      submissionLock.lock();
      lock.lock();
      for (const auto &range : result.dirty_ranges) {
        if (launched != 0) break;
        const auto allocation = findAllocation(static_cast<uint64_t>(range.allocation.value));
        const auto rangeOffset = static_cast<size_t>(range.offset.value);
        const auto rangeSize = static_cast<size_t>(range.size.value);
        if (!validRange(allocation, rangeOffset, rangeSize)) {
          launched = -1;
          break;
        }
        invalidatePointerRelocations(allocation, rangeOffset, rangeSize);
        if (!readRemote(*state.client, allocation->id, rangeOffset, static_cast<uint8_t *>(allocation->data()) + rangeOffset,
                        range.size.value)) {
          launched = -1;
          break;
        }
        downloadedBytes += rangeSize;
      }
      for (const auto &relocation : result.pointer_relocations) {
        if (launched != 0) break;
        const auto *sourceId = std::get_if<RelocationSourceAllocation>(&relocation.source);
        if (!sourceId) {
          launched = -1;
          break;
        }
        const auto source = findAllocation(static_cast<uint64_t>(sourceId->id.value));
        const auto target = findAllocation(static_cast<uint64_t>(relocation.target.value));
        const auto sourceOffset = static_cast<size_t>(relocation.sourceOffset.value);
        const auto targetOffset = static_cast<size_t>(relocation.targetOffset.value);
        if (!validRange(source, sourceOffset, sizeof(uintptr_t)) || !target || targetOffset > target->size) {
          launched = -1;
          break;
        }
        const auto address = reinterpret_cast<uintptr_t>(target->data()) + targetOffset;
        std::memcpy(static_cast<uint8_t *>(source->data()) + sourceOffset, &address, sizeof(address));
        std::lock_guard relocationsLock(pointerRelocationsMutex);
        source->pointerRelocations[sourceOffset] = {target, targetOffset};
      }
    }
  }
  if (launched != 0) {
    std::fprintf(stderr, "Gemstone OpenCL launch failed: %s\n", failureMessage.empty() ? remoteError() : failureMessage.c_str());
    return CL_OUT_OF_RESOURCES;
  }
  if (std::getenv("GEMSTONE_PERF_TRACE") != nullptr)
    std::fprintf(stderr, "Gemstone OpenCL perf: kernel=%s upload=%llu download=%llu scan=%llu relocations=%llu elapsed_us=%llu\n",
                 kernel->name.c_str(), static_cast<unsigned long long>(uploadedBytes), static_cast<unsigned long long>(downloadedBytes),
                 static_cast<unsigned long long>(scannedBytes), static_cast<unsigned long long>(relocations.size()),
                 static_cast<unsigned long long>((nowNanos() - started) / 1000));
  if (output && makeEvent(queue, CL_COMMAND_NDRANGE_KERNEL, output) == nullptr) return CL_OUT_OF_HOST_MEMORY;
  return CL_SUCCESS;
}

} // namespace

extern "C" {

CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddress(const char *name);
CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddressForPlatform(cl_platform_id value, const char *name);

CL_API_ENTRY cl_int CL_API_CALL clGetPlatformIDs(cl_uint entries, cl_platform_id *platforms, cl_uint *count) {
  initializeDispatch();
  (void)remoteState();
  if (!platforms && !count) return CL_INVALID_VALUE;
  if (entries == 0 && platforms) return CL_INVALID_VALUE;
  if (platforms) platforms[0] = reinterpret_cast<cl_platform_id>(&platform);
  if (count) *count = 1;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clIcdGetPlatformIDsKHR(cl_uint entries, cl_platform_id *platforms, cl_uint *count) {
  initializeDispatch();
  return clGetPlatformIDs(entries, platforms, count);
}

CL_API_ENTRY void *CL_API_CALL clIcdGetFunctionAddressForPlatformKHR(cl_platform_id value, const char *name) {
  return clGetExtensionFunctionAddressForPlatform(value, name);
}

CL_API_ENTRY cl_int CL_API_CALL clIcdSetPlatformDispatchDataKHR(cl_platform_id value, void *) {
  return validPlatform(value) ? CL_SUCCESS : CL_INVALID_PLATFORM;
}

CL_API_ENTRY cl_int CL_API_CALL clGetPlatformInfo(cl_platform_id value, cl_platform_info name, size_t size, void *output, size_t *written) {
  if (!validPlatform(value)) return CL_INVALID_PLATFORM;
  switch (name) {
    case CL_PLATFORM_PROFILE: return outputString(size, output, written, "FULL_PROFILE");
    case CL_PLATFORM_VERSION: return outputString(size, output, written, "OpenCL 3.0 Gemstone");
    case CL_PLATFORM_NAME: return outputString(size, output, written, openclIdentity().platformName);
    case CL_PLATFORM_VENDOR: return outputString(size, output, written, openclIdentity().vendor);
    case CL_PLATFORM_ICD_SUFFIX_KHR: return outputString(size, output, written, "GEMSTONE");
    case CL_PLATFORM_EXTENSIONS:
      return outputString(size, output, written,
                          "cl_khr_icd cl_khr_il_program cl_khr_byte_addressable_store "
                          "cl_khr_fp16 cl_khr_fp64 cl_khr_pci_bus_info "
                          "cl_khr_global_int32_base_atomics cl_khr_global_int32_extended_atomics "
                          "cl_khr_local_int32_base_atomics cl_khr_local_int32_extended_atomics "
                          "cl_khr_int64_base_atomics cl_khr_int64_extended_atomics "
                          "cl_intel_subgroups cl_intel_spirv_subgroups cl_intel_device_attribute_query "
                          "cl_intel_required_subgroup_size "
                          "cl_intel_unified_shared_memory");
    case CL_PLATFORM_EXTENSIONS_WITH_VERSION: {
      static const cl_name_version extensions[] = {{CL_MAKE_VERSION(1, 0, 0), "cl_khr_icd"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_il_program"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_byte_addressable_store"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_fp16"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_fp64"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_pci_bus_info"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_global_int32_base_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_global_int32_extended_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_local_int32_base_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_local_int32_extended_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_int64_base_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_int64_extended_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_intel_subgroups"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_intel_spirv_subgroups"},
                                                   {CL_MAKE_VERSION(0, 0, 0), "cl_intel_device_attribute_query"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_intel_required_subgroup_size"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_intel_unified_shared_memory"}};
      return outputBytes(size, output, written, extensions, sizeof(extensions));
    }
    case CL_PLATFORM_HOST_TIMER_RESOLUTION: {
      cl_ulong value = 0;
      return outputScalar(size, output, written, value);
    }
    case CL_PLATFORM_NUMERIC_VERSION: {
      cl_version value = CL_MAKE_VERSION(3, 0, 0);
      return outputScalar(size, output, written, value);
    }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clGetDeviceIDs(cl_platform_id value, cl_device_type type, cl_uint entries, cl_device_id *devices,
                                               cl_uint *count) {
  if (!validPlatform(value)) return CL_INVALID_PLATFORM;
  constexpr cl_device_type validTypes =
      CL_DEVICE_TYPE_DEFAULT | CL_DEVICE_TYPE_CPU | CL_DEVICE_TYPE_GPU | CL_DEVICE_TYPE_ACCELERATOR | CL_DEVICE_TYPE_CUSTOM;
  if (type == 0 || (type != CL_DEVICE_TYPE_ALL && (type & ~validTypes) != 0)) return CL_INVALID_DEVICE_TYPE;
  if (type != CL_DEVICE_TYPE_GPU && type != CL_DEVICE_TYPE_DEFAULT && type != CL_DEVICE_TYPE_ALL && (type & CL_DEVICE_TYPE_GPU) == 0)
    return CL_DEVICE_NOT_FOUND;
  if (entries == 0 && devices) return CL_INVALID_VALUE;
  if (devices) devices[0] = reinterpret_cast<cl_device_id>(&device);
  if (!devices && !count) return CL_INVALID_VALUE;
  if (count) *count = 1;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clRetainDevice(cl_device_id value) {
  // Gemstone exposes one root device. It is owned by the ICD for the
  // lifetime of the process, so retaining it is a successful no-op rather
  // than a reference-count transition that could invalidate the static
  // device object.
  return validDevice(value) ? CL_SUCCESS : CL_INVALID_DEVICE;
}

CL_API_ENTRY cl_int CL_API_CALL clReleaseDevice(cl_device_id value) {
  // The root device remains available until the ICD is unloaded.
  return validDevice(value) ? CL_SUCCESS : CL_INVALID_DEVICE;
}

CL_API_ENTRY cl_int CL_API_CALL clGetDeviceInfo(cl_device_id value, cl_device_info name, size_t size, void *output, size_t *written) {
  if (!validDevice(value)) return CL_INVALID_DEVICE;
  static const size_t workSizes[] = {64, 64, 64};
  // The provider completes each enqueue synchronously through the execution
  // RPC.  It therefore remains serialized even when an out-of-order queue is
  // requested, but it can still honor the OpenCL queue property: out-of-order
  // execution permits reordering; it does not require concurrent execution.
  static const cl_command_queue_properties queueProperties = CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_PROFILING_ENABLE;
  static const cl_device_svm_capabilities svm = CL_DEVICE_SVM_COARSE_GRAIN_BUFFER | CL_DEVICE_SVM_FINE_GRAIN_BUFFER | CL_DEVICE_SVM_ATOMICS;
  static const cl_device_fp_config fp = CL_FP_INF_NAN | CL_FP_ROUND_TO_NEAREST | CL_FP_DENORM;
  static const cl_device_fp_config doubleFp =
      CL_FP_FMA | CL_FP_ROUND_TO_NEAREST | CL_FP_ROUND_TO_ZERO | CL_FP_ROUND_TO_INF | CL_FP_INF_NAN | CL_FP_DENORM;
  switch (name) {
    case CL_DEVICE_TYPE: {
      cl_device_type v = CL_DEVICE_TYPE_GPU;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_VENDOR_ID: {
      cl_uint v = openclIdentity().vendorId;
      return outputScalar(size, output, written, v);
    }
    // Keep the IP version consistent with the Arc device ID advertised below.
    case CL_DEVICE_IP_VERSION_INTEL: {
      cl_uint v = 0x030dc008;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ID_INTEL: {
      cl_uint v = 22177;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_NUM_SLICES_INTEL: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_NUM_SUB_SLICES_PER_SLICE_INTEL: {
      cl_uint v = 2;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_NUM_EUS_PER_SUB_SLICE_INTEL: {
      cl_uint v = 8;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_NUM_THREADS_PER_EU_INTEL: {
      cl_uint v = 7;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_FEATURE_CAPABILITIES_INTEL: {
      cl_device_feature_capabilities_intel v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_COMPUTE_UNITS: {
      cl_uint v = 16;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS: {
      cl_uint v = 3;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_WORK_GROUP_SIZE: {
      size_t v = 64;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_WORK_ITEM_SIZES: return outputBytes(size, output, written, workSizes, sizeof(workSizes));
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_INT:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_CLOCK_FREQUENCY: {
      cl_uint v = 1000;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ADDRESS_BITS: {
      cl_uint v = 64;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_MEM_ALLOC_SIZE: {
      const cl_ulong v = maxMemAllocSize();
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_READ_IMAGE_ARGS:
    case CL_DEVICE_MAX_WRITE_IMAGE_ARGS:
    case CL_DEVICE_MAX_READ_WRITE_IMAGE_ARGS: {
      cl_uint v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_IMAGE2D_MAX_WIDTH:
    case CL_DEVICE_IMAGE2D_MAX_HEIGHT:
    case CL_DEVICE_IMAGE3D_MAX_WIDTH:
    case CL_DEVICE_IMAGE3D_MAX_HEIGHT:
    case CL_DEVICE_IMAGE3D_MAX_DEPTH:
    case CL_DEVICE_IMAGE_MAX_BUFFER_SIZE:
    case CL_DEVICE_IMAGE_MAX_ARRAY_SIZE: {
      size_t v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_IMAGE_PITCH_ALIGNMENT:
    case CL_DEVICE_IMAGE_BASE_ADDRESS_ALIGNMENT: {
      cl_uint v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PARTITION_MAX_SUB_DEVICES: {
      cl_uint v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PARTITION_AFFINITY_DOMAIN: {
      cl_device_affinity_domain v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PARTITION_PROPERTIES: {
      static const cl_device_partition_property properties[] = {0};
      return outputBytes(size, output, written, properties, sizeof(properties));
    }
    case CL_DEVICE_PARTITION_TYPE: {
      static const cl_device_partition_property type[] = {0};
      return outputBytes(size, output, written, type, sizeof(type));
    }
    case CL_DEVICE_PARENT_DEVICE: {
      cl_device_id v = nullptr;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_REFERENCE_COUNT: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_IMAGE_SUPPORT: {
      cl_bool v = CL_FALSE;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_PARAMETER_SIZE: {
      size_t v = 4096;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_SAMPLERS: {
      cl_uint v = 16;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_SINGLE_FP_CONFIG: return outputScalar(size, output, written, fp);
    case CL_DEVICE_HALF_FP_CONFIG: return outputScalar(size, output, written, fp);
    case CL_DEVICE_GLOBAL_MEM_CACHE_TYPE: {
      cl_uint v = CL_NONE;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE: {
      cl_uint v = 64;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_GLOBAL_MEM_CACHE_SIZE: {
      cl_ulong v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_GLOBAL_MEM_SIZE: {
      cl_ulong v = UINT64_C(0x80000000);
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE: {
      cl_ulong v = UINT64_C(0x10000);
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_CONSTANT_ARGS: {
      cl_uint v = 8;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_LOCAL_MEM_TYPE: {
      cl_uint v = CL_LOCAL;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_LOCAL_MEM_SIZE: {
      const cl_ulong v = localMemSize();
      return outputScalar(size, output, written, v);
    }
    // This provider is an emulator, so fp64 is a supported device feature.
    // DPC++/IGC lowers it through Intel's DP emulation path, enabled by the
    // explicit compiler configuration.
    case CL_DEVICE_DOUBLE_FP_CONFIG: return outputScalar(size, output, written, doubleFp);
    case CL_DEVICE_MEM_BASE_ADDR_ALIGN: {
      cl_uint v = 1024;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MIN_DATA_TYPE_ALIGN_SIZE: {
      cl_uint v = 16;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ERROR_CORRECTION_SUPPORT:
    case CL_DEVICE_PREFERRED_INTEROP_USER_SYNC: {
      cl_bool v = CL_FALSE;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PROFILING_TIMER_RESOLUTION: {
      size_t v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ENDIAN_LITTLE:
    case CL_DEVICE_AVAILABLE:
    case CL_DEVICE_COMPILER_AVAILABLE:
    case CL_DEVICE_LINKER_AVAILABLE:
    case CL_DEVICE_HOST_UNIFIED_MEMORY: {
      cl_bool v = CL_TRUE;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_EXECUTION_CAPABILITIES: {
      cl_device_exec_capabilities v = CL_EXEC_KERNEL;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_QUEUE_PROPERTIES: return outputScalar(size, output, written, queueProperties);
    case CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE:
    case CL_DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE: {
      size_t v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_PIPE_ARGS:
    case CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS:
    case CL_DEVICE_PIPE_MAX_PACKET_SIZE: {
      cl_uint v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT:
    case CL_DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT:
    case CL_DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT: {
      cl_uint v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_NAME: return outputString(size, output, written, openclIdentity().deviceName);
    case CL_DEVICE_VENDOR: return outputString(size, output, written, openclIdentity().vendor);
    case CL_DRIVER_VERSION: return outputString(size, output, written, "26.22.38646.6");
    case CL_DEVICE_PROFILE: return outputString(size, output, written, "FULL_PROFILE");
    case CL_DEVICE_VERSION: return outputString(size, output, written, "OpenCL 3.0 Gemstone");
    case CL_DEVICE_OPENCL_C_VERSION: return outputString(size, output, written, "OpenCL C 1.2 Gemstone");
    case CL_DEVICE_EXTENSIONS:
      return outputString(size, output, written,
                          "cl_khr_icd cl_khr_il_program cl_khr_byte_addressable_store "
                          "cl_khr_fp16 cl_khr_fp64 cl_khr_pci_bus_info "
                          "cl_khr_global_int32_base_atomics cl_khr_global_int32_extended_atomics "
                          "cl_khr_local_int32_base_atomics cl_khr_local_int32_extended_atomics "
                          "cl_khr_int64_base_atomics cl_khr_int64_extended_atomics "
                          "cl_intel_subgroups cl_intel_spirv_subgroups cl_intel_device_attribute_query "
                          "cl_intel_required_subgroup_size "
                          "cl_intel_unified_shared_memory");
    case CL_DEVICE_NUMERIC_VERSION: {
      cl_version v = CL_MAKE_VERSION(3, 0, 0);
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_EXTENSIONS_WITH_VERSION: {
      static const cl_name_version extensions[] = {{CL_MAKE_VERSION(1, 0, 0), "cl_khr_icd"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_il_program"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_byte_addressable_store"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_fp16"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_fp64"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_pci_bus_info"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_global_int32_base_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_global_int32_extended_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_local_int32_base_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_local_int32_extended_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_int64_base_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_khr_int64_extended_atomics"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_intel_subgroups"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_intel_spirv_subgroups"},
                                                   {CL_MAKE_VERSION(0, 0, 0), "cl_intel_device_attribute_query"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_intel_required_subgroup_size"},
                                                   {CL_MAKE_VERSION(1, 0, 0), "cl_intel_unified_shared_memory"}};
      return outputBytes(size, output, written, extensions, sizeof(extensions));
    }
    case CL_DEVICE_PCI_BUS_INFO_KHR: {
      const cl_device_pci_bus_info_khr v{.pci_domain = 0, .pci_bus = 0, .pci_device = 1, .pci_function = 0};
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ILS_WITH_VERSION: {
      static const cl_name_version ilVersions[] = {{CL_MAKE_VERSION(1, 0, 0), "SPIR-V"}};
      return outputBytes(size, output, written, ilVersions, sizeof(ilVersions));
    }
    case CL_DEVICE_BUILT_IN_KERNELS: return outputString(size, output, written, "");
    case CL_DEVICE_BUILT_IN_KERNELS_WITH_VERSION: {
      if (written) *written = 0;
      return !output || size == 0 ? CL_SUCCESS : CL_INVALID_VALUE;
    }
    case CL_DEVICE_OPENCL_C_ALL_VERSIONS: {
      static const cl_name_version versions[] = {{CL_MAKE_VERSION(1, 0, 0), "OpenCL C"},
                                                 {CL_MAKE_VERSION(1, 1, 0), "OpenCL C"},
                                                 {CL_MAKE_VERSION(1, 2, 0), "OpenCL C"},
                                                 {CL_MAKE_VERSION(3, 0, 0), "OpenCL C"}};
      return outputBytes(size, output, written, versions, sizeof(versions));
    }
    case CL_DEVICE_OPENCL_C_FEATURES: {
      static const cl_name_version features[] = {{CL_MAKE_VERSION(3, 0, 0), "__opencl_c_int64"},
                                                 {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_fp64"},
                                                 {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_subgroups"},
                                                 {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_atomic_order_acq_rel"},
                                                 {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_atomic_order_seq_cst"},
                                                 {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_atomic_scope_device"},
                                                 {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_atomic_scope_all_devices"}};
      return outputBytes(size, output, written, features, sizeof(features));
    }
    case CL_DEVICE_ATOMIC_MEMORY_CAPABILITIES: {
      cl_device_atomic_capabilities v = CL_DEVICE_ATOMIC_ORDER_RELAXED | CL_DEVICE_ATOMIC_ORDER_ACQ_REL | CL_DEVICE_ATOMIC_ORDER_SEQ_CST
                                        | CL_DEVICE_ATOMIC_SCOPE_WORK_GROUP | CL_DEVICE_ATOMIC_SCOPE_DEVICE
                                        | CL_DEVICE_ATOMIC_SCOPE_ALL_DEVICES;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_ATOMIC_FENCE_CAPABILITIES: {
      cl_device_atomic_capabilities v = CL_DEVICE_ATOMIC_ORDER_RELAXED | CL_DEVICE_ATOMIC_ORDER_ACQ_REL | CL_DEVICE_ATOMIC_ORDER_SEQ_CST
                                        | CL_DEVICE_ATOMIC_SCOPE_WORK_ITEM | CL_DEVICE_ATOMIC_SCOPE_WORK_GROUP
                                        | CL_DEVICE_ATOMIC_SCOPE_DEVICE | CL_DEVICE_ATOMIC_SCOPE_ALL_DEVICES;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_WORK_GROUP_COLLECTIVE_FUNCTIONS_SUPPORT:
    case CL_DEVICE_GENERIC_ADDRESS_SPACE_SUPPORT:
    case CL_DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS: {
      cl_bool v = CL_FALSE;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PRINTF_BUFFER_SIZE: {
      size_t v = 1024U * 1024U;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_LATEST_CONFORMANCE_VERSION_PASSED: return outputString(size, output, written, "v2026-07-09-00");
    case CL_DEVICE_PLATFORM: {
      cl_platform_id v = reinterpret_cast<cl_platform_id>(&platform);
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_SVM_CAPABILITIES: return outputScalar(size, output, written, svm);
    case CL_DEVICE_IL_VERSION: return outputString(size, output, written, "SPIR-V_1.0");
    case CL_DEVICE_MAX_NUM_SUB_GROUPS: {
      cl_uint v = 32;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_SUB_GROUP_SIZES_INTEL: {
      static const size_t sizes[] = {8, 16, 32};
      return outputBytes(size, output, written, sizes, sizeof(sizes));
    }
    case CL_DEVICE_NON_UNIFORM_WORK_GROUP_SUPPORT: {
      cl_bool v = CL_FALSE;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PREFERRED_WORK_GROUP_SIZE_MULTIPLE: {
      size_t v = 32;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_QUEUE_ON_DEVICE_PROPERTIES: {
      cl_command_queue_properties v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE:
    case CL_DEVICE_QUEUE_ON_DEVICE_MAX_SIZE:
    case CL_DEVICE_MAX_ON_DEVICE_QUEUES:
    case CL_DEVICE_MAX_ON_DEVICE_EVENTS: {
      cl_uint v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_DEVICE_ENQUEUE_CAPABILITIES: {
      cl_device_device_enqueue_capabilities v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_PIPE_SUPPORT: {
      cl_bool v = CL_FALSE;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_SHARED_SYSTEM_MEM_CAPABILITIES_INTEL: {
      // clSetKernelArgMemPointerINTEL requires a known SVM/USM allocation.
      const cl_device_unified_shared_memory_capabilities_intel v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_HOST_MEM_CAPABILITIES_INTEL:
    case CL_DEVICE_SINGLE_DEVICE_SHARED_MEM_CAPABILITIES_INTEL: {
      const cl_device_unified_shared_memory_capabilities_intel v =
          CL_UNIFIED_SHARED_MEMORY_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL
          | CL_UNIFIED_SHARED_MEMORY_ATOMIC_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ATOMIC_ACCESS_INTEL;
      return outputScalar(size, output, written, v);
    }
    case CL_DEVICE_DEVICE_MEM_CAPABILITIES_INTEL:
    case CL_DEVICE_CROSS_DEVICE_SHARED_MEM_CAPABILITIES_INTEL: {
      cl_device_unified_shared_memory_capabilities_intel v =
          CL_UNIFIED_SHARED_MEMORY_ACCESS_INTEL | CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL;
      return outputScalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clGetDeviceAndHostTimer(cl_device_id value, cl_ulong *deviceTimestamp, cl_ulong *hostTimestamp) {
  if (!validDevice(value)) return CL_INVALID_DEVICE;
  return CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_int CL_API_CALL clGetHostTimer(cl_device_id value, cl_ulong *hostTimestamp) {
  if (!validDevice(value)) return CL_INVALID_DEVICE;
  return CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_context CL_API_CALL clCreateContext(const cl_context_properties *properties, cl_uint count, const cl_device_id *devices,
                                                    void(CL_CALLBACK *notify)(const char *, const void *, size_t, void *), void *user,
                                                    cl_int *error) {
  if (properties) {
    bool platformSeen = false;
    bool interopSeen = false;
    for (size_t index = 0; properties[index] != 0; index += 2) {
      const cl_context_properties property = properties[index];
      const cl_context_properties value = properties[index + 1];
      if (property == CL_CONTEXT_PLATFORM) {
        if (platformSeen) {
          if (error) *error = CL_INVALID_PROPERTY;
          return nullptr;
        }
        if (reinterpret_cast<cl_platform_id>(value) != reinterpret_cast<cl_platform_id>(&platform)) {
          if (error) *error = CL_INVALID_PLATFORM;
          return nullptr;
        }
        platformSeen = true;
      } else if (property == CL_CONTEXT_INTEROP_USER_SYNC) {
        if (interopSeen || (value != CL_TRUE && value != CL_FALSE)) {
          if (error) *error = CL_INVALID_PROPERTY;
          return nullptr;
        }
        interopSeen = true;
      } else {
        if (error) *error = CL_INVALID_PROPERTY;
        return nullptr;
      }
    }
  }
  if (count == 0 || !devices) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  if (count != 1 || !validDevice(devices[0])) {
    if (error) *error = CL_INVALID_DEVICE;
    return nullptr;
  }
  if (!notify && user) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  auto *result = new (std::nothrow) _cl_context;
  if (!result) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(result);
  result->dispatch = &dispatch;
  result->identity = nextContextIdentity.fetch_add(1, std::memory_order_relaxed);
  result->device = &device;
  if (properties) {
    for (size_t index = 0; properties[index] != 0; index += 2) {
      result->properties.push_back(properties[index]);
      result->properties.push_back(properties[index + 1]);
    }
    result->properties.push_back(0);
  }
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_context>(result);
}

CL_API_ENTRY cl_context CL_API_CALL clCreateContextFromType(const cl_context_properties *properties, cl_device_type type,
                                                            void(CL_CALLBACK *notify)(const char *, const void *, size_t, void *),
                                                            void *user, cl_int *error) {
  cl_device_id selected = nullptr;
  cl_int result = clGetDeviceIDs(reinterpret_cast<cl_platform_id>(&platform), type, 1, &selected, nullptr);
  if (result != CL_SUCCESS) {
    if (error) *error = result;
    return nullptr;
  }
  return clCreateContext(properties, 1, &selected, notify, user, error);
}

CL_API_ENTRY cl_int CL_API_CALL clRetainContext(cl_context value) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  retain(context);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clReleaseContext(cl_context value) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  if (release(context)) {
    for (auto callback = context->destructorCallbacks.rbegin(); callback != context->destructorCallbacks.rend(); ++callback)
      if (callback->first) callback->first(value, callback->second);
    unregisterLiveObject(context);
    delete context;
  }
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clSetContextDestructorCallback(cl_context value, void(CL_CALLBACK *callback)(cl_context, void *),
                                                               void *user) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  if (!callback) return CL_INVALID_VALUE;
  try {
    context->destructorCallbacks.emplace_back(callback, user);
  } catch (const std::bad_alloc &) {
    return CL_OUT_OF_HOST_MEMORY;
  }
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clGetContextInfo(cl_context value, cl_context_info name, size_t size, void *output, size_t *written) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  switch (name) {
    case CL_CONTEXT_REFERENCE_COUNT: {
      cl_uint v = context->header.references.load();
      return outputScalar(size, output, written, v);
    }
    case CL_CONTEXT_NUM_DEVICES: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_CONTEXT_DEVICES: {
      cl_device_id v = reinterpret_cast<cl_device_id>(&device);
      return outputScalar(size, output, written, v);
    }
    case CL_CONTEXT_PROPERTIES:
      return outputBytes(size, output, written, context->properties.data(), context->properties.size() * sizeof(cl_context_properties));
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_command_queue CL_API_CALL clCreateCommandQueue(cl_context value, cl_device_id selected,
                                                               cl_command_queue_properties properties, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (!validDevice(selected) || context->device != &device) {
    if (error) *error = CL_INVALID_DEVICE;
    return nullptr;
  }
  constexpr cl_command_queue_properties knownProperties = CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_PROFILING_ENABLE;
  if ((properties & ~knownProperties) != 0) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  auto *queue = new (std::nothrow) _cl_command_queue;
  if (!queue) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(queue);
  queue->dispatch = &dispatch;
  queue->context = context;
  queue->device = &device;
  queue->properties = properties;
  retain(context);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_command_queue>(queue);
}

CL_API_ENTRY cl_command_queue CL_API_CALL clCreateCommandQueueWithProperties(cl_context context, cl_device_id deviceValue,
                                                                             const cl_queue_properties *properties, cl_int *error) {
  if (!checked(reinterpret_cast<_cl_context *>(context), Kind::Context)) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (!validDevice(deviceValue)) {
    if (error) *error = CL_INVALID_DEVICE;
    return nullptr;
  }
  cl_command_queue_properties queueProperties = 0;
  if (properties)
    for (size_t index = 0; properties[index] != 0; index += 2) {
      if (properties[index] != CL_QUEUE_PROPERTIES) {
        if (error) *error = CL_INVALID_VALUE;
        return nullptr;
      }
      queueProperties = static_cast<cl_command_queue_properties>(properties[index + 1]);
    }
  auto result = clCreateCommandQueue(context, deviceValue, queueProperties, error);
  if (result && properties) {
    auto *queue = reinterpret_cast<_cl_command_queue *>(result);
    for (size_t index = 0; properties[index] != 0; index += 2) {
      queue->propertiesArray.push_back(properties[index]);
      queue->propertiesArray.push_back(properties[index + 1]);
    }
    queue->propertiesArray.push_back(0);
  }
  return result;
}

CL_API_ENTRY cl_int CL_API_CALL clSetDefaultDeviceCommandQueue(cl_context value, cl_device_id selected, cl_command_queue) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  if (!validDevice(selected) || context->device != &device) return CL_INVALID_DEVICE;
  return CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_int CL_API_CALL clRetainCommandQueue(cl_command_queue value) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  retain(queue);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clReleaseCommandQueue(cl_command_queue value) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (release(queue)) {
    auto *context = queue->context;
    unregisterLiveObject(queue);
    delete queue;
    clReleaseContext(reinterpret_cast<cl_context>(context));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetCommandQueueProperty(cl_command_queue value, cl_command_queue_properties, cl_bool,
                                                          cl_command_queue_properties *) {
  return checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue) == nullptr ? CL_INVALID_COMMAND_QUEUE : CL_INVALID_OPERATION;
}
CL_API_ENTRY cl_int CL_API_CALL clGetCommandQueueInfo(cl_command_queue value, cl_command_queue_info name, size_t size, void *output,
                                                      size_t *written) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  switch (name) {
    case CL_QUEUE_CONTEXT: {
      cl_context v = reinterpret_cast<cl_context>(queue->context);
      return outputScalar(size, output, written, v);
    }
    case CL_QUEUE_DEVICE: {
      cl_device_id v = reinterpret_cast<cl_device_id>(&device);
      return outputScalar(size, output, written, v);
    }
    case CL_QUEUE_REFERENCE_COUNT: {
      cl_uint v = queue->header.references.load();
      return outputScalar(size, output, written, v);
    }
    case CL_QUEUE_PROPERTIES: return outputScalar(size, output, written, queue->properties);
    case CL_QUEUE_PROPERTIES_ARRAY:
      return outputBytes(size, output, written, queue->propertiesArray.data(), queue->propertiesArray.size() * sizeof(cl_queue_properties));
    case CL_QUEUE_SIZE: return CL_INVALID_COMMAND_QUEUE;
    case CL_QUEUE_DEVICE_DEFAULT: {
      cl_command_queue v = nullptr;
      return outputScalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateBuffer(cl_context value, cl_mem_flags flags, size_t size, void *host, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context || size == 0) {
    if (error) *error = context ? CL_INVALID_BUFFER_SIZE : CL_INVALID_CONTEXT;
    return nullptr;
  }
  constexpr cl_mem_flags accessFlags = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY;
  if ((flags & accessFlags) == 0) flags |= CL_MEM_READ_WRITE;
  if ((flags & CL_MEM_USE_HOST_PTR) && !host) {
    if (error) *error = CL_INVALID_HOST_PTR;
    return nullptr;
  }
  if ((flags & CL_MEM_COPY_HOST_PTR) && !host) {
    if (error) *error = CL_INVALID_HOST_PTR;
    return nullptr;
  }
  size_t hostOffset = 0;
  auto allocation = (flags & CL_MEM_USE_HOST_PTR) != 0 ? findPointer(host, &hostOffset) : nullptr;
  const bool aliasesRuntimeAllocation = allocation && allocation->runtimeOwnsBase;
  if (aliasesRuntimeAllocation && !validRange(allocation, hostOffset, size)) {
    if (error) *error = CL_INVALID_BUFFER_SIZE;
    return nullptr;
  }
  if (!aliasesRuntimeAllocation) {
    hostOffset = 0;
    allocation = allocate(size, error);
  }
  if (!allocation) return nullptr;
  if (!aliasesRuntimeAllocation && host && (flags & (CL_MEM_USE_HOST_PTR | CL_MEM_COPY_HOST_PTR))) {
    std::memcpy(allocation->data(), host, size);
  }
  cl_int writeResult = CL_SUCCESS;
  if (remoteState().ready) {
    std::lock_guard lock(remoteMutex);
    if (!writeRemote(*remote.client, allocation->id, hostOffset, static_cast<uint8_t *>(allocation->data()) + hostOffset, size))
      writeResult = CL_OUT_OF_RESOURCES;
  }
  if (writeResult != CL_SUCCESS) {
    if (!aliasesRuntimeAllocation) releaseAllocation(allocation);
    if (error) *error = writeResult;
    return nullptr;
  }
  auto *result = new (std::nothrow) _cl_mem;
  if (!result) {
    if (!aliasesRuntimeAllocation) releaseAllocation(allocation);
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(result);
  result->dispatch = &dispatch;
  result->context = context;
  result->allocation = std::move(allocation);
  result->flags = flags;
  result->size = size;
  retain(context);
  result->origin = hostOffset;
  result->hostPointer = (flags & CL_MEM_USE_HOST_PTR) == 0 ? nullptr : host;
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_mem>(result);
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateBufferWithProperties(cl_context value, const cl_mem_properties *properties, cl_mem_flags flags,
                                                             size_t size, void *host, cl_int *error) {
  if (properties) {
    for (size_t index = 0; properties[index] != 0; index += 2)
      return (error ? (*error = CL_INVALID_PROPERTY) : 0), nullptr;
  }
  auto result = clCreateBuffer(value, flags, size, host, error);
  if (result && properties) {
    auto *buffer = reinterpret_cast<_cl_mem *>(result);
    buffer->properties.push_back(0);
  }
  return result;
}

CL_API_ENTRY cl_mem CL_API_CALL clCreatePipe(cl_context value, cl_mem_flags, cl_uint, cl_uint, const cl_pipe_properties *, cl_int *error) {
  if (!checked(reinterpret_cast<_cl_context *>(value), Kind::Context)) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateImage(cl_context value, cl_mem_flags, const cl_image_format *, const cl_image_desc *, void *,
                                              cl_int *error) {
  if (!checked(reinterpret_cast<_cl_context *>(value), Kind::Context)) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateImageWithProperties(cl_context value, const cl_mem_properties *, cl_mem_flags,
                                                            const cl_image_format *, const cl_image_desc *, void *, cl_int *error) {
  return clCreateImage(value, 0, nullptr, nullptr, nullptr, error);
}

CL_API_ENTRY cl_int CL_API_CALL clGetImageInfo(cl_mem value, cl_image_info, size_t, void *, size_t *) {
  return checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory) == nullptr ? CL_INVALID_MEM_OBJECT : CL_INVALID_MEM_OBJECT;
}

CL_API_ENTRY cl_int CL_API_CALL clGetPipeInfo(cl_mem value, cl_pipe_info, size_t, void *, size_t *) {
  if (!checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory)) return CL_INVALID_MEM_OBJECT;
  return CL_INVALID_MEM_OBJECT;
}

CL_API_ENTRY cl_int CL_API_CALL clRetainMemObject(cl_mem value) {
  auto *mem = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory);
  if (!mem) return CL_INVALID_MEM_OBJECT;
  retain(mem);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clReleaseMemObject(cl_mem value) {
  auto *mem = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory);
  if (!mem) return CL_INVALID_MEM_OBJECT;
  if (release(mem)) {
    for (auto callback = mem->destructorCallbacks.rbegin(); callback != mem->destructorCallbacks.rend(); ++callback)
      callback->first(reinterpret_cast<cl_mem>(mem), callback->second);
    auto allocation = mem->allocation;
    auto *context = mem->context;
    auto *parent = mem->parent;
    unregisterLiveObject(mem);
    delete mem;
    // The root buffer owns backing storage. Sub-buffers and queued commands
    // retain that object; kernel argument/relocation metadata does not own it.
    if (!allocation->runtimeOwnsBase && !parent) releaseAllocation(allocation);
    clReleaseContext(reinterpret_cast<cl_context>(context));
    if (parent) clReleaseMemObject(reinterpret_cast<cl_mem>(parent));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetMemObjectDestructorCallback(cl_mem value, void(CL_CALLBACK *callback)(cl_mem, void *), void *user) {
  auto *mem = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory);
  if (!mem) return CL_INVALID_MEM_OBJECT;
  if (!callback) return CL_INVALID_VALUE;
  mem->destructorCallbacks.emplace_back(callback, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_mem CL_API_CALL clCreateSubBuffer(cl_mem value, cl_mem_flags flags, cl_buffer_create_type type, const void *description,
                                                  cl_int *error) {
  auto *parent = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory);
  if (!parent) {
    if (error) *error = CL_INVALID_MEM_OBJECT;
    return nullptr;
  }
  if (type != CL_BUFFER_CREATE_TYPE_REGION || !description) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  const auto *region = static_cast<const cl_buffer_region *>(description);
  if (!validMemoryRange(parent, region->origin, region->size) || region->size == 0) {
    if (error) *error = CL_INVALID_BUFFER_SIZE;
    return nullptr;
  }
  auto *result = new (std::nothrow) _cl_mem;
  if (!result) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(result);
  result->dispatch = &dispatch;
  result->context = parent->context;
  result->allocation = parent->allocation;
  constexpr cl_mem_flags accessFlags = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY;
  constexpr cl_mem_flags hostAccessFlags = CL_MEM_HOST_READ_ONLY | CL_MEM_HOST_WRITE_ONLY | CL_MEM_HOST_NO_ACCESS;
  cl_mem_flags inheritedFlags = flags;
  if ((flags & accessFlags) == 0) inheritedFlags |= parent->flags & accessFlags;
  inheritedFlags |= parent->flags & (CL_MEM_ALLOC_HOST_PTR | CL_MEM_COPY_HOST_PTR | CL_MEM_USE_HOST_PTR);
  if ((flags & hostAccessFlags) == 0) inheritedFlags |= parent->flags & hostAccessFlags;
  result->flags = inheritedFlags;
  result->hostPointer = !parent->hostPointer ? nullptr : static_cast<uint8_t *>(parent->hostPointer) + region->origin;
  result->parent = parent;
  result->origin = allocationOffset(parent, region->origin);
  result->size = region->size;
  retain(parent);
  retain(parent->context);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_mem>(result);
}
CL_API_ENTRY cl_int CL_API_CALL clGetMemObjectInfo(cl_mem value, cl_mem_info name, size_t size, void *output, size_t *written) {
  auto *mem = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory);
  if (!mem) return CL_INVALID_MEM_OBJECT;
  switch (name) {
    case CL_MEM_TYPE: {
      cl_mem_object_type v = CL_MEM_OBJECT_BUFFER;
      return outputScalar(size, output, written, v);
    }
    case CL_MEM_FLAGS: return outputScalar(size, output, written, mem->flags);
    case CL_MEM_SIZE: return outputScalar(size, output, written, mem->size);
    case CL_MEM_HOST_PTR: return outputScalar(size, output, written, mem->hostPointer);
    case CL_MEM_USES_SVM_POINTER: {
      const cl_bool usesSvmPointer = (mem->flags & CL_MEM_USE_HOST_PTR) && findPointer(mem->hostPointer) ? CL_TRUE : CL_FALSE;
      return outputScalar(size, output, written, usesSvmPointer);
    }
    case CL_MEM_REFERENCE_COUNT: {
      cl_uint v = mem->header.references.load();
      return outputScalar(size, output, written, v);
    }
    case CL_MEM_CONTEXT: {
      cl_context v = reinterpret_cast<cl_context>(mem->context);
      return outputScalar(size, output, written, v);
    }
    case CL_MEM_ASSOCIATED_MEMOBJECT: {
      cl_mem v = reinterpret_cast<cl_mem>(mem->parent);
      return outputScalar(size, output, written, v);
    }
    case CL_MEM_OFFSET: return outputScalar(size, output, written, mem->origin);
    case CL_MEM_MAP_COUNT: {
      std::lock_guard lock(allocationsMutex);
      cl_uint v = static_cast<cl_uint>(mem->mappings.size());
      return outputScalar(size, output, written, v);
    }
    case CL_MEM_PROPERTIES:
      return outputBytes(size, output, written, mem->properties.data(), mem->properties.size() * sizeof(cl_mem_properties));
    default: return CL_INVALID_VALUE;
  }
}

} // extern "C"

namespace {

cl_int loadProgramIL(_cl_program *program) {
  if (program->remoteId != 0) return CL_SUCCESS;
  if (program->il.empty() || program->il.size() > std::numeric_limits<uint32_t>::max()) return CL_INVALID_BINARY;
  auto &state = remoteState();
  if (!state.ready) {
    program->buildLog = remoteError();
    return CL_OUT_OF_RESOURCES;
  }
  std::lock_guard lock(remoteMutex);
  auto loaded = state.client->call(LoadProgram{"spirv-binary", program->il});
  if (!loaded) {
    program->buildLog = remoteError();
    return CL_INVALID_BINARY;
  }
  program->remoteId = static_cast<uint64_t>(loaded.value().value);
  std::vector<std::string> kernelNames;
  auto catalog = state.client->call(DescribeProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
  if (!catalog) {
    program->buildLog = remoteError();
    (void)state.client->call(UnloadProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
    program->remoteId = 0;
    return CL_INVALID_BINARY;
  }
  kernelNames.reserve(catalog.value().kernels.size());
  for (const auto &kernel : catalog.value().kernels)
    kernelNames.push_back(kernel.name.value);
  program->kernelNames = kernelNames ^ mk_string(";");
  return CL_SUCCESS;
}

} // namespace

extern "C" {

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithIL(cl_context value, const void *il, size_t length, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context || !il || length == 0 || length > std::numeric_limits<uint32_t>::max()) {
    if (error) *error = context ? CL_INVALID_VALUE : CL_INVALID_CONTEXT;
    return nullptr;
  }
  auto *program = new (std::nothrow) _cl_program;
  if (!program) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(program);
  program->dispatch = &dispatch;
  program->context = context;
  program->il.assign(static_cast<const uint8_t *>(il), static_cast<const uint8_t *>(il) + length);
  program->originalIL = program->il;
  program->createdWithIL = true;
  retain(context);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_program>(program);
}

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithBinary(cl_context context, cl_uint count, const cl_device_id *devices,
                                                              const size_t *lengths, const unsigned char **binaries, cl_int *binaryStatus,
                                                              cl_int *error) {
  auto *contextValue = checked(reinterpret_cast<_cl_context *>(context), Kind::Context);
  if (!contextValue) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (count != 1 || !devices || !validDevice(devices[0]) || !lengths || !binaries || binaries[0] == nullptr || lengths[0] == 0) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  if (binaryStatus) binaryStatus[0] = CL_INVALID_BINARY;
  cl_program_binary_type binaryType = CL_PROGRAM_BINARY_TYPE_NONE;
  std::string source;
  std::vector<uint8_t> il;
  if (!decodeProgramBinary(binaries[0], lengths[0], &binaryType, &source, &il)) {
    auto result = clCreateProgramWithIL(context, binaries[0], lengths[0], error);
    if (result) {
      auto *program = reinterpret_cast<_cl_program *>(result);
      program->createdWithIL = false;
      const auto loaded = loadProgramIL(program);
      if (loaded == CL_SUCCESS) {
        program->buildStatus = CL_BUILD_SUCCESS;
        program->binaryType = CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
        if (binaryStatus) binaryStatus[0] = CL_SUCCESS;
      } else {
        clReleaseProgram(result);
        result = nullptr;
        if (error) *error = loaded;
      }
    }
    return result;
  }

  auto *program = new (std::nothrow) _cl_program;
  if (!program) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(program);
  program->dispatch = &dispatch;
  program->context = contextValue;
  program->source = std::move(source);
  program->buildSource = program->source;
  program->binaryType = binaryType;
  program->buildStatus = CL_BUILD_SUCCESS;
  retain(contextValue);
  if (binaryType == CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT) {
    program->bitcode = std::move(il);
    if (binaryStatus) binaryStatus[0] = CL_SUCCESS;
    if (error) *error = CL_SUCCESS;
    return reinterpret_cast<cl_program>(program);
  }
  program->il = std::move(il);
  const auto loaded = loadProgramIL(program);
  if (loaded != CL_SUCCESS) {
    unregisterLiveObject(program);
    delete program;
    clReleaseContext(context);
    if (error) *error = loaded;
    return nullptr;
  }
  if (binaryStatus) binaryStatus[0] = CL_SUCCESS;
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_program>(program);
}

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithSource(cl_context value, cl_uint count, const char **strings, const size_t *lengths,
                                                              cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (count == 0 || !strings) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  std::string source;
  for (cl_uint index = 0; index < count; ++index) {
    if (strings[index] == nullptr) {
      if (error) *error = CL_INVALID_VALUE;
      return nullptr;
    }
    const size_t length = !lengths || lengths[index] == 0 ? std::strlen(strings[index]) : lengths[index];
    if (length > std::numeric_limits<size_t>::max() - source.size()) {
      if (error) *error = CL_OUT_OF_HOST_MEMORY;
      return nullptr;
    }
    source.append(strings[index], length);
  }
  auto *program = new (std::nothrow) _cl_program;
  if (!program) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(program);
  program->dispatch = &dispatch;
  program->context = context;
  program->source = std::move(source);
  retain(context);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_program>(program);
}
CL_API_ENTRY cl_int CL_API_CALL clRetainProgram(cl_program value) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program) return CL_INVALID_PROGRAM;
  retain(program);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clReleaseProgram(cl_program value) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program) return CL_INVALID_PROGRAM;
  if (release(program)) {
    auto &state = remoteState();
    if (state.ready && program->remoteId != 0) {
      std::lock_guard lock(remoteMutex);
      (void)state.client->call(UnloadProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
    }
    auto *context = program->context;
    unregisterLiveObject(program);
    delete program;
    clReleaseContext(reinterpret_cast<cl_context>(context));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetProgramSpecializationConstant(cl_program value, cl_uint specId, size_t specSize,
                                                                   const void *specValue) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program || !program->createdWithIL) return CL_INVALID_PROGRAM;
  const auto constant = gemstone::spirv::findSpecializationConstant(program->originalIL, specId);
  if (!constant) return CL_INVALID_SPEC_ID;
  if (!specValue || specSize != constant->byteSize) return CL_INVALID_VALUE;
  const auto *bytes = static_cast<const uint8_t *>(specValue);
  program->specializationConstants[specId].assign(bytes, bytes + specSize);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clBuildProgram(cl_program value, cl_uint count, const cl_device_id *devices, const char *options,
                                               void(CL_CALLBACK *notify)(cl_program, void *), void *user) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program) return CL_INVALID_PROGRAM;
  if (count != 0 && !devices) return CL_INVALID_VALUE;
  if (count > 1 || (count == 1 && !validDevice(devices[0]))) return CL_INVALID_DEVICE;
  program->buildOptions = !options ? "" : options;
  if (program->createdWithIL) {
    if (program->remoteId != 0) {
      auto &state = remoteState();
      if (state.ready) {
        std::lock_guard lock(remoteMutex);
        (void)state.client->call(UnloadProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
      }
      program->remoteId = 0;
    }
    if (!gemstone::spirv::specializeSpirv(program->originalIL, program->specializationConstants, program->il)) {
      program->buildStatus = CL_BUILD_ERROR;
      program->buildLog = "invalid SPIR-V specialization constant";
      if (notify) notify(value, user);
      return CL_BUILD_PROGRAM_FAILURE;
    }
  }
  const std::string source = program->buildSource.empty() ? program->source : program->buildSource;
  if (!source.empty() && program->remoteId != 0) {
    auto &state = remoteState();
    if (state.ready) {
      std::lock_guard lock(remoteMutex);
      (void)state.client->call(UnloadProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
    }
    program->remoteId = 0;
    program->il.clear();
    program->bitcode.clear();
  }
  if (!source.empty() && program->remoteId == 0) {
    std::vector<uint8_t> compiled;
    std::vector<uint8_t> compiledBitcode;
    if (!compileSource(source, program->buildOptions, compiled, program->buildLog, &compiledBitcode)) {
      program->buildStatus = CL_BUILD_ERROR;
      if (notify) notify(value, user);
      return CL_BUILD_PROGRAM_FAILURE;
    }
    program->il = std::move(compiled);
    program->bitcode = std::move(compiledBitcode);
    program->buildSource = source;
    if (loadProgramIL(program) != CL_SUCCESS) {
      program->buildStatus = CL_BUILD_ERROR;
      if (notify) notify(value, user);
      return CL_BUILD_PROGRAM_FAILURE;
    }
  }
  if (source.empty() && !program->il.empty() && program->remoteId == 0) {
    if (loadProgramIL(program) != CL_SUCCESS) {
      program->buildStatus = CL_BUILD_ERROR;
      if (notify) notify(value, user);
      return CL_BUILD_PROGRAM_FAILURE;
    }
  }
  program->buildStatus = program->remoteId == 0 ? CL_BUILD_ERROR : CL_BUILD_SUCCESS;
  if (program->buildStatus != CL_BUILD_SUCCESS) {
    if (program->buildLog.empty()) program->buildLog = remoteError();
    if (notify) notify(value, user);
    return CL_BUILD_PROGRAM_FAILURE;
  }
  program->binaryType = CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
  if (notify) notify(value, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clUnloadCompiler() { return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clUnloadPlatformCompiler(cl_platform_id value) {
  return validPlatform(value) ? CL_SUCCESS : CL_INVALID_PLATFORM;
}
CL_API_ENTRY cl_int CL_API_CALL clCompileProgram(cl_program value, cl_uint count, const cl_device_id *devices, const char *options,
                                                 cl_uint inputHeaderCount, const cl_program *inputHeaders, const char **headerNames,
                                                 void(CL_CALLBACK *notify)(cl_program, void *), void *user) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program) return CL_INVALID_PROGRAM;
  if (count != 0 && !devices) return CL_INVALID_VALUE;
  if (count > 1 || (count == 1 && !validDevice(devices[0]))) return CL_INVALID_DEVICE;
  if (inputHeaderCount != 0 && (!inputHeaders || !headerNames)) return CL_INVALID_VALUE;
  if (inputHeaderCount != 0) {
    std::string expanded;
    if (!expandHeaders(program->source, inputHeaderCount, inputHeaders, headerNames, &expanded, &program->buildLog)) {
      program->buildStatus = CL_BUILD_ERROR;
      if (notify) notify(value, user);
      return CL_INVALID_PROGRAM;
    }
    program->buildSource = std::move(expanded);
  }
  {
    const std::string source = program->buildSource.empty() ? program->source : program->buildSource;
    std::string expanded;
    if (!expandFilesystemIncludes(source, !options ? "" : options, expanded, program->buildLog)) {
      program->buildStatus = CL_BUILD_ERROR;
      if (notify) notify(value, user);
      return CL_BUILD_PROGRAM_FAILURE;
    }
    program->buildSource = std::move(expanded);
  }
  program->buildOptions = !options ? "" : options;
  const std::string source = program->buildSource.empty() ? program->source : program->buildSource;
  const bool compilesIL = source.empty() && !program->il.empty();
  if (program->remoteId != 0) {
    auto &state = remoteState();
    if (state.ready) {
      std::lock_guard lock(remoteMutex);
      (void)state.client->call(UnloadProgram{ProgramId{static_cast<int64_t>(program->remoteId)}});
    }
  }
  program->remoteId = 0;
  program->bitcode.clear();
  program->kernelNames.clear();
  if (compilesIL) {
    program->buildStatus = CL_BUILD_SUCCESS;
    program->binaryType = CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT;
    if (notify) notify(value, user);
    return CL_SUCCESS;
  }
  program->il.clear();
  if (!compileSourceToBitcode(source, program->buildOptions, program->bitcode, program->buildLog)) {
    program->buildStatus = CL_BUILD_ERROR;
    program->binaryType = CL_PROGRAM_BINARY_TYPE_NONE;
    if (notify) notify(value, user);
    return CL_BUILD_PROGRAM_FAILURE;
  }
  program->buildStatus = CL_BUILD_SUCCESS;
  program->binaryType = CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT;
  if (notify) notify(value, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_program CL_API_CALL clLinkProgram(cl_context contextValue, cl_uint count, const cl_device_id *devices, const char *options,
                                                  cl_uint inputCount, const cl_program *inputs,
                                                  void(CL_CALLBACK *notify)(cl_program, void *), void *user, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(contextValue), Kind::Context);
  if (!context) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (count != 0 && !devices) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  if (count > 1 || (count == 1 && !validDevice(devices[0]))) {
    if (error) *error = CL_INVALID_DEVICE;
    return nullptr;
  }
  if (inputCount == 0 || !inputs) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  std::string source;
  std::string buildOptions;
  std::vector<std::vector<uint8_t>> bitcodeModules;
  bool allSourceBitcode = true;
  _cl_program *binaryInput = nullptr;
  std::vector<_cl_program *> binaryInputs;
  for (cl_uint index = 0; index < inputCount; ++index) {
    auto *program = checked(reinterpret_cast<_cl_program *>(inputs[index]), Kind::Program);
    if (!program) {
      if (error) *error = CL_INVALID_PROGRAM;
      return nullptr;
    }
    if (program->context != context || program->buildStatus != CL_BUILD_SUCCESS) {
      if (error) *error = CL_INVALID_PROGRAM;
      return nullptr;
    }
    if (program->source.empty()) {
      if (program->il.empty() || !source.empty() || !bitcodeModules.empty()) {
        if (error) *error = CL_INVALID_PROGRAM;
        return nullptr;
      }
      binaryInputs.push_back(program);
      continue;
    }
    if (!source.empty()) source.push_back('\n');
    source += program->buildSource.empty() ? program->source : program->buildSource;
    if (program->bitcode.empty()) allSourceBitcode = false;
    else bitcodeModules.push_back(program->bitcode);
    if (!program->buildOptions.empty()) {
      if (!buildOptions.empty()) buildOptions.push_back(' ');
      buildOptions += program->buildOptions;
    }
  }
  if (binaryInputs.size() == 1) binaryInput = binaryInputs.front();
  if (binaryInputs.size() > 1) {
    std::vector<std::vector<uint8_t>> binaryBitcode;
    binaryBitcode.reserve(binaryInputs.size());
    std::string linkLog;
    for (const auto *program : binaryInputs) {
      std::vector<uint8_t> bitcode;
      if (!reverseSpirv(program->il, bitcode, linkLog)) {
        if (error) *error = CL_BUILD_PROGRAM_FAILURE;
        return nullptr;
      }
      binaryBitcode.push_back(std::move(bitcode));
    }
    std::vector<uint8_t> linkedIL;
    std::vector<uint8_t> linkedBitcode;
    if (!linkBitcode(binaryBitcode, linkedIL, linkedBitcode, linkLog)) {
      if (error) *error = CL_BUILD_PROGRAM_FAILURE;
      return nullptr;
    }
    cl_int linkError = CL_SUCCESS;
    auto *linked = reinterpret_cast<_cl_program *>(clCreateProgramWithIL(contextValue, linkedIL.data(), linkedIL.size(), &linkError));
    if (!linked) {
      if (error) *error = linkError == CL_SUCCESS ? CL_BUILD_PROGRAM_FAILURE : linkError;
      return nullptr;
    }
    linked->bitcode = std::move(linkedBitcode);
    linked->buildStatus = CL_BUILD_SUCCESS;
    linked->binaryType = hasBuildOption(options, "-create-library") ? CL_PROGRAM_BINARY_TYPE_LIBRARY : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
    linked->buildOptions = std::move(buildOptions);
    linked->buildLog = std::move(linkLog);
    if (loadProgramIL(linked) != CL_SUCCESS) {
      clReleaseProgram(reinterpret_cast<cl_program>(linked));
      if (error) *error = CL_BUILD_PROGRAM_FAILURE;
      return nullptr;
    }
    if (notify) notify(reinterpret_cast<cl_program>(linked), user);
    if (error) *error = CL_SUCCESS;
    return reinterpret_cast<cl_program>(linked);
  }
  if (!binaryInput && allSourceBitcode) {
    std::vector<uint8_t> linkedIL;
    std::vector<uint8_t> linkedBitcode;
    std::string linkLog;
    if (!linkBitcode(bitcodeModules, linkedIL, linkedBitcode, linkLog)) {
      if (error) *error = CL_BUILD_PROGRAM_FAILURE;
      return nullptr;
    }
    cl_int linkError = CL_SUCCESS;
    auto *linked = reinterpret_cast<_cl_program *>(clCreateProgramWithIL(contextValue, linkedIL.data(), linkedIL.size(), &linkError));
    if (!linked) {
      if (error) *error = linkError == CL_SUCCESS ? CL_BUILD_PROGRAM_FAILURE : linkError;
      return nullptr;
    }
    linked->source = std::move(source);
    linked->buildSource = linked->source;
    linked->bitcode = std::move(linkedBitcode);
    linked->buildStatus = CL_BUILD_SUCCESS;
    linked->binaryType = hasBuildOption(options, "-create-library") ? CL_PROGRAM_BINARY_TYPE_LIBRARY : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
    linked->buildOptions = std::move(buildOptions);
    linked->buildLog = std::move(linkLog);
    if (loadProgramIL(linked) != CL_SUCCESS) {
      clReleaseProgram(reinterpret_cast<cl_program>(linked));
      if (error) *error = CL_BUILD_PROGRAM_FAILURE;
      return nullptr;
    }
    if (notify) notify(reinterpret_cast<cl_program>(linked), user);
    if (error) *error = CL_SUCCESS;
    return reinterpret_cast<cl_program>(linked);
  }
  if (binaryInput) {
    auto *linked = new (std::nothrow) _cl_program;
    if (!linked) {
      if (error) *error = CL_OUT_OF_HOST_MEMORY;
      return nullptr;
    }
    registerLiveObject(linked);
    linked->dispatch = &dispatch;
    linked->context = context;
    linked->source = binaryInput->source;
    linked->buildSource = binaryInput->buildSource;
    linked->il = binaryInput->il;
    linked->buildStatus = CL_BUILD_SUCCESS;
    linked->binaryType = hasBuildOption(options, "-create-library") ? CL_PROGRAM_BINARY_TYPE_LIBRARY : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
    retain(context);
    const auto loaded = loadProgramIL(linked);
    if (loaded != CL_SUCCESS) {
      unregisterLiveObject(linked);
      delete linked;
      clReleaseContext(context);
      if (error) *error = loaded;
      return nullptr;
    }
    if (notify) notify(reinterpret_cast<cl_program>(linked), user);
    if (error) *error = CL_SUCCESS;
    return reinterpret_cast<cl_program>(linked);
  }
  if (options && options[0] != '\0') {
    if (!buildOptions.empty()) buildOptions.push_back(' ');
    buildOptions += withoutLibraryOption(options);
  }
  const char *sourcePointer = source.c_str();
  cl_program result = clCreateProgramWithSource(contextValue, 1, &sourcePointer, nullptr, error);
  if (!result) return nullptr;
  const cl_int build = clBuildProgram(result, count, devices, buildOptions.c_str(), nullptr, nullptr);
  if (build != CL_SUCCESS) {
    clReleaseProgram(result);
    if (error) *error = build;
    return nullptr;
  }
  auto *linked = reinterpret_cast<_cl_program *>(result);
  linked->binaryType = hasBuildOption(options, "-create-library") ? CL_PROGRAM_BINARY_TYPE_LIBRARY : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
  if (notify) notify(result, user);
  if (error) *error = CL_SUCCESS;
  return result;
}

CL_API_ENTRY cl_int CL_API_CALL clGetSupportedImageFormats(cl_context value, cl_mem_flags, cl_mem_object_type, cl_uint entryCount,
                                                           cl_image_format *imageFormats, cl_uint *imageFormatCount) {
  if (!checked(reinterpret_cast<_cl_context *>(value), Kind::Context)) return CL_INVALID_CONTEXT;
  if (imageFormats && entryCount == 0) return CL_INVALID_VALUE;
  if (imageFormatCount) *imageFormatCount = 0;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clGetProgramInfo(cl_program value, cl_program_info name, size_t size, void *output, size_t *written) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program) return CL_INVALID_PROGRAM;
  switch (name) {
    case CL_PROGRAM_REFERENCE_COUNT: {
      cl_uint v = program->header.references.load();
      return outputScalar(size, output, written, v);
    }
    case CL_PROGRAM_CONTEXT: {
      cl_context v = reinterpret_cast<cl_context>(program->context);
      return outputScalar(size, output, written, v);
    }
    case CL_PROGRAM_NUM_DEVICES: {
      cl_uint v = 1;
      return outputScalar(size, output, written, v);
    }
    case CL_PROGRAM_DEVICES: {
      cl_device_id v = reinterpret_cast<cl_device_id>(&device);
      return outputScalar(size, output, written, v);
    }
    case CL_PROGRAM_SOURCE: return outputBytes(size, output, written, program->source.c_str(), program->source.size() + 1);
    case CL_PROGRAM_NUM_KERNELS: {
      if (program->buildStatus != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
      const size_t v = kernelCount(program->kernelNames);
      return outputScalar(size, output, written, v);
    }
    case CL_PROGRAM_KERNEL_NAMES:
      if (program->buildStatus != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
      return outputString(size, output, written, program->kernelNames);
    case CL_PROGRAM_IL: {
      const auto &il = program->createdWithIL ? program->originalIL : program->il;
      return outputBytes(size, output, written, il.data(), il.size());
    }
    case CL_PROGRAM_BINARY_SIZES: {
      const auto binary = programBinary(program);
      return outputScalar(size, output, written, binary.size());
    }
    case CL_PROGRAM_BINARIES:
      if (written) *written = sizeof(unsigned char *);
      if (!output) return CL_SUCCESS;
      if (size < sizeof(unsigned char *)) return CL_INVALID_VALUE;
      {
        const auto binary = programBinary(program);
        auto *destination = *static_cast<unsigned char **>(output);
        if (!destination && !binary.empty()) return CL_INVALID_VALUE;
        if (!binary.empty()) std::memcpy(destination, binary.data(), binary.size());
      }
      return CL_SUCCESS;
    case CL_PROGRAM_SCOPE_GLOBAL_CTORS_PRESENT:
    case CL_PROGRAM_SCOPE_GLOBAL_DTORS_PRESENT: {
      cl_bool v = CL_FALSE;
      return outputScalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetProgramBuildInfo(cl_program value, cl_device_id selected, cl_program_build_info name, size_t size,
                                                      void *output, size_t *written) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program || !validDevice(selected)) return CL_INVALID_PROGRAM;
  switch (name) {
    case CL_PROGRAM_BUILD_STATUS: return outputScalar(size, output, written, program->buildStatus);
    case CL_PROGRAM_BUILD_OPTIONS: return outputString(size, output, written, program->buildOptions);
    case CL_PROGRAM_BUILD_LOG: return outputString(size, output, written, program->buildLog);
    case CL_PROGRAM_BINARY_TYPE: return outputScalar(size, output, written, program->binaryType);
    case CL_PROGRAM_BUILD_GLOBAL_VARIABLE_TOTAL_SIZE: {
      size_t v = 0;
      return outputScalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clSetProgramReleaseCallback(cl_program value, void(CL_CALLBACK *)(cl_program, void *), void *) {
  return checked(reinterpret_cast<_cl_program *>(value), Kind::Program) == nullptr ? CL_INVALID_PROGRAM : CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_kernel CL_API_CALL clCreateKernel(cl_program value, const char *name, cl_int *error) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program || !name || name[0] == '\0') {
    if (error) *error = program ? CL_INVALID_KERNEL_NAME : CL_INVALID_PROGRAM;
    return nullptr;
  }
  if (program->buildStatus != CL_BUILD_SUCCESS) {
    if (error) *error = CL_INVALID_PROGRAM_EXECUTABLE;
    return nullptr;
  }
  if (!containsKernelName(program->kernelNames, name)) {
    if (error) *error = CL_INVALID_KERNEL_NAME;
    return nullptr;
  }
  auto *kernel = new (std::nothrow) _cl_kernel;
  if (!kernel) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(kernel);
  kernel->dispatch = &dispatch;
  kernel->program = program;
  kernel->name = name;
  const auto kernelSource = sourceKernelText(program->source, kernel->name);
  auto &state = remoteState();
  if (!state.ready) {
    unregisterLiveObject(kernel);
    delete kernel;
    if (error) *error = CL_INVALID_KERNEL_DEFINITION;
    return nullptr;
  }
  auto described = state.client->call(DescribeKernel{ProgramId{static_cast<int64_t>(program->remoteId)}, name});
  if (!described) {
    unregisterLiveObject(kernel);
    delete kernel;
    if (error) *error = CL_INVALID_KERNEL_DEFINITION;
    return nullptr;
  }
  const auto &remoteMetadata = described.value();
  kernel->expectedArguments = static_cast<cl_uint>(remoteMetadata.parameters.size());
  kernel->requiredWorkGroupSize = sourceRequiredWorkGroupSize(kernelSource);
  kernel->staticLocalMemory = sourceStaticLocalMemory(kernelSource);
  kernel->attributes = sourceKernelAttributes(kernelSource);
  kernel->expectedArgumentSizes.reserve(remoteMetadata.parameters.size());
  kernel->parameterMetadata.reserve(remoteMetadata.parameters.size());
  for (const auto &parameter : remoteMetadata.parameters) {
    kernel->expectedArgumentSizes.push_back(static_cast<size_t>(parameter.width.value));
    const bool directPointer = parameter.kind == KernelParameterKind::Allocation;
    kernel->parameterMetadata.push_back({parameter.kind == KernelParameterKind::Scalar && parameter.containsPointers,
                                         parameter.kind == KernelParameterKind::Local, directPointer || parameter.containsPointers});
  }
  kernel->argumentInfo = sourceKernelArgumentInfo(kernelSource, kernel->name);
  kernel->arguments.resize(kernel->expectedArguments);
  kernel->argumentsSet.resize(kernel->expectedArguments, false);
  retain(program);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_kernel>(kernel);
}
CL_API_ENTRY cl_int CL_API_CALL clCreateKernelsInProgram(cl_program value, cl_uint kernelCapacity, cl_kernel *kernels,
                                                         cl_uint *kernelCountOutput) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program) return CL_INVALID_PROGRAM;
  if (program->buildStatus != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
  const size_t total = kernelCount(program->kernelNames);
  if (kernelCountOutput) *kernelCountOutput = static_cast<cl_uint>(total);
  if (kernelCapacity == 0 && !kernels) return CL_SUCCESS;
  if (total != 0 && (!kernels || kernelCapacity < total)) return CL_INVALID_VALUE;
  if (!kernels && kernelCapacity != 0) return CL_INVALID_VALUE;
  size_t start = 0;
  for (size_t index = 0; index < total; ++index) {
    const size_t end = program->kernelNames.find(';', start);
    const std::string name = program->kernelNames.substr(start, end == std::string::npos ? end : end - start);
    cl_int error = CL_SUCCESS;
    kernels[index] = clCreateKernel(value, name.c_str(), &error);
    if (kernels[index] == nullptr || error != CL_SUCCESS) {
      for (size_t created = 0; created < index; ++created)
        clReleaseKernel(kernels[created]);
      return error == CL_SUCCESS ? CL_OUT_OF_HOST_MEMORY : error;
    }
    if (end == std::string::npos) break;
    start = end + 1;
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_kernel CL_API_CALL clCloneKernel(cl_kernel value, cl_int *error) {
  auto *source = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!source) {
    if (error) *error = CL_INVALID_KERNEL;
    return nullptr;
  }
  auto *clone = new (std::nothrow) _cl_kernel;
  if (!clone) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(clone);
  clone->dispatch = &dispatch;
  clone->program = source->program;
  clone->name = source->name;
  clone->expectedArguments = source->expectedArguments;
  clone->requiredWorkGroupSize = source->requiredWorkGroupSize;
  clone->staticLocalMemory = source->staticLocalMemory;
  clone->attributes = source->attributes;
  clone->expectedArgumentSizes = source->expectedArgumentSizes;
  clone->parameterMetadata = source->parameterMetadata;
  clone->argumentInfo = source->argumentInfo;
  clone->arguments = source->arguments;
  clone->argumentsSet = source->argumentsSet;
  clone->indirectAllocations = source->indirectAllocations;
  clone->indirectTypes = source->indirectTypes;
  retain(clone->program);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_kernel>(clone);
}
CL_API_ENTRY cl_int CL_API_CALL clRetainKernel(cl_kernel value) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  retain(kernel);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clReleaseKernel(cl_kernel value) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  if (release(kernel)) {
    auto *program = kernel->program;
    unregisterLiveObject(kernel);
    delete kernel;
    clReleaseProgram(reinterpret_cast<cl_program>(program));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArg(cl_kernel value, cl_uint index, size_t size, const void *argument) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  if (index >= kernel->expectedArguments) return CL_INVALID_ARG_INDEX;
  if (!argument && size == 0) return CL_INVALID_ARG_SIZE;
  if (argument && size == 0) return CL_INVALID_ARG_SIZE;
  if (std::getenv("GEMSTONE_PERF_TRACE") != nullptr)
    std::fprintf(stderr, "Gemstone OpenCL argument kernel=%s index=%u size=%zu expected=%zu local=%d aggregate=%d null=%d\n",
                 kernel->name.c_str(), index, size, index < kernel->expectedArgumentSizes.size() ? kernel->expectedArgumentSizes[index] : 0,
                 index < kernel->parameterMetadata.size() && kernel->parameterMetadata[index].local,
                 index < kernel->parameterMetadata.size() && kernel->parameterMetadata[index].pointerAggregate, !argument);
  auto &output = kernel->arguments[index];
  const bool pointerAggregate = index < kernel->parameterMetadata.size() && kernel->parameterMetadata[index].pointerAggregate;
  const bool localArgument = index < kernel->parameterMetadata.size() && kernel->parameterMetadata[index].local;
  if (!argument && size == sizeof(cl_mem) && !localArgument) {
    output.kind = ::KernelArgument::Kind::Scalar;
    output.allocation.reset();
    output.offset = 0;
    output.bytes.assign(sizeof(cl_mem), 0);
    output.memory = nullptr;
    kernel->argumentsSet[index] = true;
    return CL_SUCCESS;
  }
  if (!argument) {
    if (!localArgument) return CL_INVALID_ARG_VALUE;
    output.kind = ::KernelArgument::Kind::Local;
    output.bytes.clear();
    output.allocation.reset();
    output.offset = size;
    output.memory = nullptr;
    kernel->argumentsSet[index] = true;
    return CL_SUCCESS;
  }
  if (size >= sizeof(cl_mem)) {
    cl_mem handle = nullptr;
    std::memcpy(&handle, argument, sizeof(handle));
    auto *mem = checked(reinterpret_cast<_cl_mem *>(handle), Kind::Memory);
    if (mem) {
      if (size != sizeof(cl_mem)) return CL_INVALID_ARG_SIZE;
      output.kind = ::KernelArgument::Kind::Allocation;
      output.allocation = mem->allocation;
      output.offset = mem->origin;
      output.bytes.clear();
      output.memory = mem;
      kernel->argumentsSet[index] = true;
      return CL_SUCCESS;
    }
  }
  if (size == sizeof(uintptr_t) && !pointerAggregate) {
    uintptr_t pointer = 0;
    std::memcpy(&pointer, argument, sizeof(pointer));
    size_t pointerOffset = 0;
    auto allocation = findPointer(reinterpret_cast<const void *>(pointer), &pointerOffset);
    if (allocation) {
      output.kind = ::KernelArgument::Kind::Allocation;
      output.allocation = std::move(allocation);
      output.offset = pointerOffset;
      output.bytes.clear();
      output.memory = nullptr;
      kernel->argumentsSet[index] = true;
      return CL_SUCCESS;
    }
  }
  const size_t expected = index < kernel->expectedArgumentSizes.size() ? kernel->expectedArgumentSizes[index] : 0;
  const bool scalarArgument = index < kernel->parameterMetadata.size() && !kernel->parameterMetadata[index].containsPointer;
  if (expected != 0 && !pointerAggregate && (size < expected || (size != expected && !scalarArgument))) return CL_INVALID_ARG_SIZE;
  // Match the Intel OpenCL scalar ABI: callers may supply a wider host slot,
  // and the driver consumes only the declared scalar width. oneMKL uses this
  // for 64-bit host slots carrying 32-bit SPIR-V offsets. Pointer arguments
  // still require their exact width; pointer-bearing aggregates retain their
  // host representation for relocation by the execution service.
  const size_t copiedSize = scalarArgument && expected != 0 ? expected : size;
  output.kind = ::KernelArgument::Kind::Scalar;
  output.allocation.reset();
  output.offset = 0;
  output.bytes.assign(static_cast<const uint8_t *>(argument), static_cast<const uint8_t *>(argument) + copiedSize);
  output.memory = nullptr;
  kernel->argumentsSet[index] = true;
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArgSVMPointer(cl_kernel value, cl_uint index, const void *argument) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  if (index >= kernel->expectedArguments) return CL_INVALID_ARG_INDEX;
  if (!argument) return clSetKernelArg(value, index, sizeof(void *), nullptr);
  size_t offset = 0;
  auto allocation = findPointer(argument, &offset);
  if (!allocation) {
    if (std::getenv("GEMSTONE_PERF_TRACE") != nullptr)
      std::fprintf(stderr, "Gemstone OpenCL SVM argument kernel=%s index=%u pointer=%p\n", kernel->name.c_str(), index, argument);
    return CL_INVALID_ARG_VALUE;
  }
  if (index >= kernel->expectedArguments) return CL_INVALID_ARG_INDEX;
  auto &output = kernel->arguments[index];
  output.kind = ::KernelArgument::Kind::SVM;
  output.allocation = std::move(allocation);
  output.offset = offset;
  output.bytes.resize(sizeof(uint64_t));
  const uint64_t pointer = reinterpret_cast<uintptr_t>(argument);
  std::memcpy(output.bytes.data(), &pointer, sizeof(pointer));
  output.memory = nullptr;
  kernel->argumentsSet[index] = true;
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelExecInfo(cl_kernel value, cl_kernel_exec_info name, size_t size, const void *valueData) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  if (!valueData) return CL_INVALID_VALUE;
  if (name == CL_KERNEL_EXEC_INFO_SVM_PTRS || name == CL_KERNEL_EXEC_INFO_USM_PTRS_INTEL) {
    if (size == 0 || size % sizeof(void *) != 0) return CL_INVALID_VALUE;
    std::vector<std::shared_ptr<Allocation>> values;
    for (size_t index = 0; index < size / sizeof(void *); ++index) {
      const auto pointer = gemstone::readNative<const void *>(valueData, index * sizeof(void *));
      size_t offset = 0;
      auto allocation = findPointer(pointer, &offset);
      if (!allocation || allocation->svmContextIdentity != kernel->program->context->identity || offset >= allocation->size)
        return CL_INVALID_VALUE;
      values.push_back(std::move(allocation));
    }
    kernel->indirectAllocations[name] = std::move(values);
    return CL_SUCCESS;
  }
  if (name == CL_KERNEL_EXEC_INFO_INDIRECT_HOST_ACCESS_INTEL || name == CL_KERNEL_EXEC_INFO_INDIRECT_DEVICE_ACCESS_INTEL
      || name == CL_KERNEL_EXEC_INFO_INDIRECT_SHARED_ACCESS_INTEL || name == CL_KERNEL_EXEC_INFO_SVM_FINE_GRAIN_SYSTEM) {
    if (size != sizeof(cl_bool)) return CL_INVALID_VALUE;
    const auto enabled = gemstone::readNative<cl_bool>(valueData);
    if (enabled != CL_TRUE && enabled != CL_FALSE) return CL_INVALID_VALUE;
    if (name == CL_KERNEL_EXEC_INFO_SVM_FINE_GRAIN_SYSTEM) return enabled ? CL_INVALID_OPERATION : CL_SUCCESS;
    if (enabled) kernel->indirectTypes.insert(name);
    else kernel->indirectTypes.erase(name);
    return CL_SUCCESS;
  }
  return CL_INVALID_VALUE;
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelInfo(cl_kernel value, cl_kernel_info name, size_t size, void *output, size_t *written) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  switch (name) {
    case CL_KERNEL_FUNCTION_NAME: return outputString(size, output, written, kernel->name);
    case CL_KERNEL_NUM_ARGS: {
      cl_uint v = static_cast<cl_uint>(kernel->arguments.size());
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_REFERENCE_COUNT: {
      cl_uint v = kernel->header.references.load();
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_CONTEXT: {
      cl_context v = reinterpret_cast<cl_context>(kernel->program->context);
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_PROGRAM: {
      cl_program v = reinterpret_cast<cl_program>(kernel->program);
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_ATTRIBUTES: return outputString(size, output, written, kernel->attributes);
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelArgInfo(cl_kernel value, cl_uint index, cl_kernel_arg_info name, size_t size, void *output,
                                                   size_t *written) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  if (index >= kernel->arguments.size()) return CL_INVALID_ARG_INDEX;
  if (index >= kernel->argumentInfo.size()) return CL_KERNEL_ARG_INFO_NOT_AVAILABLE;
  const auto &info = kernel->argumentInfo[index];
  switch (name) {
    case CL_KERNEL_ARG_ADDRESS_QUALIFIER: return outputScalar(size, output, written, info.address);
    case CL_KERNEL_ARG_ACCESS_QUALIFIER: return outputScalar(size, output, written, info.access);
    case CL_KERNEL_ARG_TYPE_QUALIFIER: return outputScalar(size, output, written, info.type);
    case CL_KERNEL_ARG_TYPE_NAME: return outputString(size, output, written, info.typeName);
    case CL_KERNEL_ARG_NAME: return outputString(size, output, written, info.name);
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelWorkGroupInfo(cl_kernel value, cl_device_id selected, cl_kernel_work_group_info name,
                                                         size_t size, void *output, size_t *written) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  // OpenCL permits a null device when the kernel's program has one device.
  if (selected && !validDevice(selected)) return CL_INVALID_DEVICE;
  switch (name) {
    case CL_KERNEL_WORK_GROUP_SIZE: {
      size_t v = 1024;
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_COMPILE_WORK_GROUP_SIZE:
      return outputBytes(size, output, written, kernel->requiredWorkGroupSize.data(), sizeof(kernel->requiredWorkGroupSize));
    case CL_KERNEL_LOCAL_MEM_SIZE: {
      cl_ulong v = static_cast<cl_ulong>(kernel->staticLocalMemory);
      for (const auto &argument : kernel->arguments)
        if (argument.kind == ::KernelArgument::Kind::Local) v += static_cast<cl_ulong>(argument.offset);
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE: {
      size_t v = 32;
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_PRIVATE_MEM_SIZE: {
      cl_ulong v = 0;
      return outputScalar(size, output, written, v);
    }
    case CL_KERNEL_GLOBAL_WORK_SIZE: {
      size_t v[3] = {0, 0, 0};
      return outputBytes(size, output, written, v, sizeof(v));
    }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clWaitForEvents(cl_uint count, const cl_event *events) { return waitList(nullptr, count, events); }
CL_API_ENTRY cl_event CL_API_CALL clCreateUserEvent(cl_context value, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  auto *event = new (std::nothrow) _cl_event;
  if (!event) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  registerLiveObject(event);
  event->dispatch = &dispatch;
  event->context = context;
  event->command = CL_COMMAND_USER;
  event->status = CL_SUBMITTED;
  event->started = nowNanos();
  retain(context);
  {
    std::lock_guard lock(eventsMutex);
    liveEvents.push_back(event);
  }
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_event>(event);
}
CL_API_ENTRY cl_int CL_API_CALL clSetUserEventStatus(cl_event value, cl_int status) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (!event) return CL_INVALID_EVENT;
  if (event->command != CL_COMMAND_USER) return CL_INVALID_OPERATION;
  if (status != CL_COMPLETE && status >= 0) return CL_INVALID_VALUE;
  {
    std::lock_guard lock(eventsMutex);
    if (event->status != CL_SUBMITTED) return CL_INVALID_OPERATION;
    event->status = status;
    event->ended = nowNanos();
  }
  eventsCondition.notify_all();
  notifyEventCallbacks(event);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetEventCallback(cl_event value, cl_int trigger, void(CL_CALLBACK *callback)(cl_event, cl_int, void *),
                                                   void *user) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (!event) return CL_INVALID_EVENT;
  if (!callback || (trigger != CL_SUBMITTED && trigger != CL_RUNNING && trigger != CL_COMPLETE)) return CL_INVALID_VALUE;
  bool ready = false;
  cl_int status = CL_COMPLETE;
  {
    std::lock_guard lock(eventsMutex);
    status = event->status;
    ready = status < 0 || status <= trigger;
    if (!ready) event->callbacks.push_back({trigger, callback, user});
  }
  if (ready) callback(value, status, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clGetEventInfo(cl_event value, cl_event_info name, size_t size, void *output, size_t *written) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (!event) return CL_INVALID_EVENT;
  switch (name) {
    case CL_EVENT_COMMAND_QUEUE: {
      cl_command_queue v = reinterpret_cast<cl_command_queue>(event->queue);
      return outputScalar(size, output, written, v);
    }
    case CL_EVENT_COMMAND_TYPE: return outputScalar(size, output, written, event->command);
    case CL_EVENT_REFERENCE_COUNT: {
      cl_uint v = event->header.references.load();
      return outputScalar(size, output, written, v);
    }
    case CL_EVENT_COMMAND_EXECUTION_STATUS: {
      std::lock_guard lock(eventsMutex);
      return outputScalar(size, output, written, event->status);
    }
    case CL_EVENT_CONTEXT: {
      cl_context v = reinterpret_cast<cl_context>(event->context);
      return outputScalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clRetainEvent(cl_event value) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (!event) return CL_INVALID_EVENT;
  retain(event);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clReleaseEvent(cl_event value) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (!event) return CL_INVALID_EVENT;
  if (release(event)) {
    auto *queue = event->queue;
    auto *context = event->context;
    {
      std::lock_guard lock(eventsMutex);
      liveEvents ^= filter([event](const _cl_event *candidate) { return candidate != event; });
    }
    unregisterLiveObject(event);
    delete event;
    if (queue) clReleaseCommandQueue(reinterpret_cast<cl_command_queue>(queue));
    if (context) clReleaseContext(reinterpret_cast<cl_context>(context));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clGetEventProfilingInfo(cl_event value, cl_profiling_info name, size_t size, void *output,
                                                        size_t *written) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (!event) return CL_INVALID_EVENT;
  if (!event->queue || (event->queue->properties & CL_QUEUE_PROFILING_ENABLE) == 0) return CL_PROFILING_INFO_NOT_AVAILABLE;
  if (name == CL_PROFILING_COMMAND_QUEUED) return outputScalar(size, output, written, event->queued);
  if (name == CL_PROFILING_COMMAND_SUBMIT) return outputScalar(size, output, written, event->submitted);
  if (name == CL_PROFILING_COMMAND_START) return outputScalar(size, output, written, event->started);
  if (name == CL_PROFILING_COMMAND_END) return outputScalar(size, output, written, event->ended);
  return CL_INVALID_VALUE;
}
CL_API_ENTRY cl_int CL_API_CALL clFlush(cl_command_queue value) {
  return checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue) ? CL_SUCCESS : CL_INVALID_COMMAND_QUEUE;
}
CL_API_ENTRY cl_int CL_API_CALL clFinish(cl_command_queue value) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  return waitQueue(queue);
}

} // extern "C"

namespace {

cl_int enqueueBufferTransfer(_cl_command_queue *queue, _cl_mem *mem, bool reading, cl_bool blocking, size_t offset, size_t size,
                             void *pointer, cl_uint count, const cl_event *waits, cl_event *event) {
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!mem || !pointer || !validMemoryRange(mem, offset, size)) return CL_INVALID_VALUE;
  if (mem->context != queue->context) return CL_INVALID_CONTEXT;
  if (reading ? !hostReadAllowed(mem) : !hostWriteAllowed(mem)) return CL_INVALID_OPERATION;
  const auto type = reading ? CL_COMMAND_READ_BUFFER : CL_COMMAND_WRITE_BUFFER;
  auto execute = [=] {
    return reading ? readAllocation(mem->allocation, allocationOffset(mem, offset), pointer, size)
                   : writeAllocation(mem->allocation, allocationOffset(mem, offset), pointer, size);
  };
  try {
    auto retained = retainMemory(mem);
    return submitCommand(
        queue, type, blocking, count, waits,
        [execute, retained] {
          (void)retained;
          return execute();
        },
        event);
  } catch (...) {
    return CL_OUT_OF_HOST_MEMORY;
  }
}

} // namespace

extern "C" {

CL_API_ENTRY cl_int CL_API_CALL clEnqueueReadBuffer(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking, size_t offset,
                                                    size_t size, void *output, cl_uint count, const cl_event *waits, cl_event *event) {
  return enqueueBufferTransfer(checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue),
                               checked(reinterpret_cast<_cl_mem *>(memoryValue), Kind::Memory), true, blocking, offset, size, output, count,
                               waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueWriteBuffer(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking, size_t offset,
                                                     size_t size, const void *input, cl_uint count, const cl_event *waits,
                                                     cl_event *event) {
  return enqueueBufferTransfer(checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue),
                               checked(reinterpret_cast<_cl_mem *>(memoryValue), Kind::Memory), false, blocking, offset, size,
                               const_cast<void *>(input), count, waits, event);
}

} // extern "C"

namespace {

bool rectPitch(size_t requestedRow, size_t requestedSlice, const size_t region[3], size_t *row, size_t *slice) {
  if (!region || !row || !slice || region[0] == 0 || region[1] == 0 || region[2] == 0) return false;
  *row = requestedRow == 0 ? region[0] : requestedRow;
  if (*row < region[0] || region[1] > std::numeric_limits<size_t>::max() / *row) return false;
  const size_t minimumSlice = *row * region[1];
  *slice = requestedSlice == 0 ? minimumSlice : requestedSlice;
  return *slice >= minimumSlice;
}

bool rectEnd(const size_t origin[3], const size_t region[3], size_t row, size_t slice, size_t *end) {
  if (!origin || !end || !region) return false;
  size_t value = origin[0];
  if (region[1] - 1 > std::numeric_limits<size_t>::max() / row) return false;
  if (value > std::numeric_limits<size_t>::max() - (region[1] - 1) * row) return false;
  value += (region[1] - 1) * row;
  if (region[2] - 1 > std::numeric_limits<size_t>::max() / slice) return false;
  if (value > std::numeric_limits<size_t>::max() - (region[2] - 1) * slice) return false;
  value += (region[2] - 1) * slice;
  if (value > std::numeric_limits<size_t>::max() - region[0]) return false;
  *end = value + region[0];
  return true;
}

cl_int enqueueReadBufferRect(_cl_command_queue *queue, _cl_mem *mem, bool blocking, const size_t bufferOrigin[3],
                             const size_t hostOrigin[3], const size_t region[3], size_t bufferRowPitch, size_t bufferSlicePitch,
                             size_t hostRowPitch, size_t hostSlicePitch, void *output, cl_uint count, const cl_event *waits,
                             cl_event *event) {
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!mem || mem->context != queue->context || !output || !bufferOrigin || !hostOrigin || !region) return CL_INVALID_VALUE;
  if (!hostReadAllowed(mem)) return CL_INVALID_OPERATION;
  size_t sourceRow = 0, sourceSlice = 0, targetRow = 0, targetSlice = 0, sourceEnd = 0, targetEnd = 0;
  if (!rectPitch(bufferRowPitch, bufferSlicePitch, region, &sourceRow, &sourceSlice)
      || !rectPitch(hostRowPitch, hostSlicePitch, region, &targetRow, &targetSlice)
      || !rectEnd(bufferOrigin, region, sourceRow, sourceSlice, &sourceEnd)
      || !rectEnd(hostOrigin, region, targetRow, targetSlice, &targetEnd) || sourceEnd > mem->size)
    return CL_INVALID_VALUE;
  const std::array<size_t, 3> copiedBufferOrigin{bufferOrigin[0], bufferOrigin[1], bufferOrigin[2]};
  const std::array<size_t, 3> copiedHostOrigin{hostOrigin[0], hostOrigin[1], hostOrigin[2]};
  const std::array<size_t, 3> copiedRegion{region[0], region[1], region[2]};
  auto retainedMemory = retainMemory(mem);
  auto execute = [=] {
    (void)retainedMemory;
    const auto *bufferOrigin = copiedBufferOrigin.data();
    const auto *hostOrigin = copiedHostOrigin.data();
    const auto *region = copiedRegion.data();
    cl_int result = CL_SUCCESS;
    std::vector<uint8_t> row(region[0]);
    for (size_t z = 0; z < region[2]; ++z) {
      for (size_t y = 0; y < region[1]; ++y) {
        const size_t sourceOffset = mem->origin + (bufferOrigin[2] + z) * sourceSlice + (bufferOrigin[1] + y) * sourceRow + bufferOrigin[0];
        const size_t targetOffset = (hostOrigin[2] + z) * targetSlice + (hostOrigin[1] + y) * targetRow + hostOrigin[0];
        result = readAllocation(mem->allocation, sourceOffset, row.data(), row.size());
        if (result != CL_SUCCESS) return result;
        std::memcpy(static_cast<uint8_t *>(output) + targetOffset, row.data(), row.size());
      }
    }
    return CL_SUCCESS;
  };
  return submitCommand(queue, CL_COMMAND_READ_BUFFER_RECT, blocking, count, waits, std::move(execute), event);
}

cl_int enqueueWriteBufferRect(_cl_command_queue *queue, _cl_mem *mem, bool blocking, const size_t bufferOrigin[3],
                              const size_t hostOrigin[3], const size_t region[3], size_t bufferRowPitch, size_t bufferSlicePitch,
                              size_t hostRowPitch, size_t hostSlicePitch, const void *input, cl_uint count, const cl_event *waits,
                              cl_event *event) {
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!mem || mem->context != queue->context || !input || !bufferOrigin || !hostOrigin || !region) return CL_INVALID_VALUE;
  if (!hostWriteAllowed(mem)) return CL_INVALID_OPERATION;
#ifdef CL_MEM_IMMUTABLE_EXT
  if ((mem->flags & CL_MEM_IMMUTABLE_EXT) != 0) return CL_INVALID_OPERATION;
#endif
  size_t sourceRow = 0, sourceSlice = 0, targetRow = 0, targetSlice = 0, sourceEnd = 0, targetEnd = 0;
  if (!rectPitch(bufferRowPitch, bufferSlicePitch, region, &targetRow, &targetSlice)
      || !rectPitch(hostRowPitch, hostSlicePitch, region, &sourceRow, &sourceSlice)
      || !rectEnd(bufferOrigin, region, targetRow, targetSlice, &targetEnd)
      || !rectEnd(hostOrigin, region, sourceRow, sourceSlice, &sourceEnd) || targetEnd > mem->size)
    return CL_INVALID_VALUE;
  const std::array<size_t, 3> copiedBufferOrigin{bufferOrigin[0], bufferOrigin[1], bufferOrigin[2]};
  const std::array<size_t, 3> copiedHostOrigin{hostOrigin[0], hostOrigin[1], hostOrigin[2]};
  const std::array<size_t, 3> copiedRegion{region[0], region[1], region[2]};
  auto retainedMemory = retainMemory(mem);
  auto execute = [=] {
    (void)retainedMemory;
    const auto *bufferOrigin = copiedBufferOrigin.data();
    const auto *hostOrigin = copiedHostOrigin.data();
    const auto *region = copiedRegion.data();
    cl_int result = CL_SUCCESS;
    std::vector<uint8_t> row(region[0]);
    for (size_t z = 0; z < region[2]; ++z) {
      for (size_t y = 0; y < region[1]; ++y) {
        const size_t sourceOffset = (hostOrigin[2] + z) * sourceSlice + (hostOrigin[1] + y) * sourceRow + hostOrigin[0];
        const size_t targetOffset = mem->origin + (bufferOrigin[2] + z) * targetSlice + (bufferOrigin[1] + y) * targetRow + bufferOrigin[0];
        std::memcpy(row.data(), static_cast<const uint8_t *>(input) + sourceOffset, row.size());
        result = writeAllocation(mem->allocation, targetOffset, row.data(), row.size());
        if (result != CL_SUCCESS) return result;
      }
    }
    return CL_SUCCESS;
  };
  return submitCommand(queue, CL_COMMAND_WRITE_BUFFER_RECT, blocking, count, waits, std::move(execute), event);
}

cl_int enqueueCopyBufferRect(_cl_command_queue *queue, _cl_mem *source, _cl_mem *target, const size_t sourceOrigin[3],
                             const size_t targetOrigin[3], const size_t region[3], size_t sourceRowPitch, size_t sourceSlicePitch,
                             size_t targetRowPitch, size_t targetSlicePitch, cl_uint count, const cl_event *waits, cl_event *event) {
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!source || !target || source->context != queue->context || target->context != queue->context || !sourceOrigin || !targetOrigin
      || !region)
    return CL_INVALID_VALUE;
  size_t sourceRow = 0, sourceSlice = 0, targetRow = 0, targetSlice = 0, sourceEnd = 0, targetEnd = 0;
  if (!rectPitch(sourceRowPitch, sourceSlicePitch, region, &sourceRow, &sourceSlice)
      || !rectPitch(targetRowPitch, targetSlicePitch, region, &targetRow, &targetSlice)
      || !rectEnd(sourceOrigin, region, sourceRow, sourceSlice, &sourceEnd)
      || !rectEnd(targetOrigin, region, targetRow, targetSlice, &targetEnd) || sourceEnd > source->size || targetEnd > target->size)
    return CL_INVALID_VALUE;
  const size_t rowCount = region[1] * region[2];
  if (region[0] != 0 && rowCount > std::numeric_limits<size_t>::max() / region[0]) return CL_INVALID_VALUE;
  const std::array<size_t, 3> copiedSourceOrigin{sourceOrigin[0], sourceOrigin[1], sourceOrigin[2]};
  const std::array<size_t, 3> copiedTargetOrigin{targetOrigin[0], targetOrigin[1], targetOrigin[2]};
  const std::array<size_t, 3> copiedRegion{region[0], region[1], region[2]};
  auto retainedSource = retainMemory(source), retainedTarget = retainMemory(target);
  auto execute = [=] {
    (void)retainedSource;
    (void)retainedTarget;
    const auto *sourceOrigin = copiedSourceOrigin.data();
    const auto *targetOrigin = copiedTargetOrigin.data();
    const auto *region = copiedRegion.data();
    cl_int result = CL_SUCCESS;
    std::vector<uint8_t> packed(region[0] * rowCount);
    size_t packedOffset = 0;
    for (size_t z = 0; z < region[2]; ++z) {
      for (size_t y = 0; y < region[1]; ++y) {
        const size_t sourceOffset =
            source->origin + (sourceOrigin[2] + z) * sourceSlice + (sourceOrigin[1] + y) * sourceRow + sourceOrigin[0];
        result = readAllocation(source->allocation, sourceOffset, packed.data() + packedOffset, region[0]);
        if (result != CL_SUCCESS) return result;
        packedOffset += region[0];
      }
    }
    packedOffset = 0;
    for (size_t z = 0; z < region[2]; ++z) {
      for (size_t y = 0; y < region[1]; ++y) {
        const size_t sourceOffset =
            source->origin + (sourceOrigin[2] + z) * sourceSlice + (sourceOrigin[1] + y) * sourceRow + sourceOrigin[0];
        const size_t targetOffset =
            target->origin + (targetOrigin[2] + z) * targetSlice + (targetOrigin[1] + y) * targetRow + targetOrigin[0];
        result = writeAllocation(target->allocation, targetOffset, packed.data() + packedOffset, region[0]);
        if (result != CL_SUCCESS) return result;
        copyPointerRelocations(source->allocation, sourceOffset, target->allocation, targetOffset, region[0]);
        packedOffset += region[0];
      }
    }
    return CL_SUCCESS;
  };
  return submitCommand(queue, CL_COMMAND_COPY_BUFFER_RECT, false, count, waits, std::move(execute), event);
}

} // namespace

extern "C" {

CL_API_ENTRY cl_int CL_API_CALL clEnqueueReadBufferRect(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking,
                                                        const size_t *bufferOrigin, const size_t *hostOrigin, const size_t *region,
                                                        size_t bufferRowPitch, size_t bufferSlicePitch, size_t hostRowPitch,
                                                        size_t hostSlicePitch, void *output, cl_uint count, const cl_event *waits,
                                                        cl_event *event) {
  return enqueueReadBufferRect(checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue),
                               checked(reinterpret_cast<_cl_mem *>(memoryValue), Kind::Memory), blocking, bufferOrigin, hostOrigin, region,
                               bufferRowPitch, bufferSlicePitch, hostRowPitch, hostSlicePitch, output, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueWriteBufferRect(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking,
                                                         const size_t *bufferOrigin, const size_t *hostOrigin, const size_t *region,
                                                         size_t bufferRowPitch, size_t bufferSlicePitch, size_t hostRowPitch,
                                                         size_t hostSlicePitch, const void *input, cl_uint count, const cl_event *waits,
                                                         cl_event *event) {
  return enqueueWriteBufferRect(checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue),
                                checked(reinterpret_cast<_cl_mem *>(memoryValue), Kind::Memory), blocking, bufferOrigin, hostOrigin, region,
                                bufferRowPitch, bufferSlicePitch, hostRowPitch, hostSlicePitch, input, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyBufferRect(cl_command_queue queueValue, cl_mem sourceValue, cl_mem targetValue,
                                                        const size_t *sourceOrigin, const size_t *targetOrigin, const size_t *region,
                                                        size_t sourceRowPitch, size_t sourceSlicePitch, size_t targetRowPitch,
                                                        size_t targetSlicePitch, cl_uint count, const cl_event *waits, cl_event *event) {
  return enqueueCopyBufferRect(checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue),
                               checked(reinterpret_cast<_cl_mem *>(sourceValue), Kind::Memory),
                               checked(reinterpret_cast<_cl_mem *>(targetValue), Kind::Memory), sourceOrigin, targetOrigin, region,
                               sourceRowPitch, sourceSlicePitch, targetRowPitch, targetSlicePitch, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyBuffer(cl_command_queue queueValue, cl_mem sourceValue, cl_mem destinationValue,
                                                    size_t sourceOffset, size_t destinationOffset, size_t size, cl_uint count,
                                                    const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  auto *src = checked(reinterpret_cast<_cl_mem *>(sourceValue), Kind::Memory);
  auto *dst = checked(reinterpret_cast<_cl_mem *>(destinationValue), Kind::Memory);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!src || !dst || src->context != queue->context || dst->context != queue->context || !validMemoryRange(src, sourceOffset, size)
      || !validMemoryRange(dst, destinationOffset, size))
    return CL_INVALID_VALUE;
  const size_t allocationSourceOffset = allocationOffset(src, sourceOffset);
  const size_t allocationTargetOffset = allocationOffset(dst, destinationOffset);
  auto source = retainMemory(src), target = retainMemory(dst);
  return submitCommand(
      queue, CL_COMMAND_COPY_BUFFER, false, count, waits,
      [=] {
        std::memmove(static_cast<uint8_t *>(target->allocation->data()) + allocationTargetOffset,
                     static_cast<uint8_t *>(source->allocation->data()) + allocationSourceOffset, size);
        const auto result = writeAllocation(target->allocation, allocationTargetOffset,
                                            static_cast<uint8_t *>(target->allocation->data()) + allocationTargetOffset, size);
        if (result == CL_SUCCESS)
          copyPointerRelocations(source->allocation, allocationSourceOffset, target->allocation, allocationTargetOffset, size);
        return result;
      },
      event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueFillBuffer(cl_command_queue queueValue, cl_mem memoryValue, const void *pattern,
                                                    size_t patternSize, size_t offset, size_t size, cl_uint count, const cl_event *waits,
                                                    cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  auto *mem = checked(reinterpret_cast<_cl_mem *>(memoryValue), Kind::Memory);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!mem || mem->context != queue->context || !pattern || patternSize == 0 || size % patternSize != 0
      || !validMemoryRange(mem, offset, size))
    return CL_INVALID_VALUE;
  const size_t targetOffset = allocationOffset(mem, offset);
  auto retained = retainMemory(mem);
  std::vector<uint8_t> copiedPattern(static_cast<const uint8_t *>(pattern), static_cast<const uint8_t *>(pattern) + patternSize);
  return submitCommand(
      queue, CL_COMMAND_FILL_BUFFER, false, count, waits,
      [=] {
        auto *bytes = static_cast<uint8_t *>(retained->allocation->data());
        for (size_t index = 0; index < size; ++index)
          bytes[targetOffset + index] = copiedPattern[index % patternSize];
        return writeAllocation(retained->allocation, targetOffset, bytes + targetOffset, size);
      },
      event);
}

CL_API_ENTRY void *CL_API_CALL clEnqueueMapBuffer(cl_command_queue queueValue, cl_mem memoryValue, cl_bool blocking, cl_map_flags flags,
                                                  size_t offset, size_t size, cl_uint count, const cl_event *waits, cl_event *event,
                                                  cl_int *error) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  auto *mem = checked(reinterpret_cast<_cl_mem *>(memoryValue), Kind::Memory);
  if (!queue) {
    if (error) *error = CL_INVALID_COMMAND_QUEUE;
    return nullptr;
  }
  if (!mem || mem->context != queue->context) {
    if (error) *error = CL_INVALID_MEM_OBJECT;
    return nullptr;
  }
  if ((flags & ~(CL_MAP_READ | CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) != 0
      || (flags & (CL_MAP_READ | CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) == 0 || !validMemoryRange(mem, offset, size)) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  if (((flags & CL_MAP_READ) != 0 && !hostReadAllowed(mem))
      || ((flags & (CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) != 0 && !hostWriteAllowed(mem))) {
    if (error) *error = CL_INVALID_OPERATION;
    return nullptr;
  }
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) {
    if (error) *error = result;
    return nullptr;
  }
  const bool directMapping = mem->hostPointer && (mem->flags & CL_MEM_USE_HOST_PTR) != 0;
  std::shared_ptr<BufferMapping> mapping;
  try {
    mapping = std::make_shared<BufferMapping>();
    if (!directMapping) mapping->storage = std::shared_ptr<uint8_t[]>(new (std::nothrow) uint8_t[size == 0 ? 1 : size]);
  } catch (const std::bad_alloc &) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  void *pointer = directMapping ? static_cast<uint8_t *>(mem->hostPointer) + offset : mapping->storage.get();
  if (!pointer) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  mapping->offset = allocationOffset(mem, offset);
  mapping->size = size;
  mapping->pointer = pointer;
  auto retained = retainMemory(mem);
  const auto key = reinterpret_cast<uintptr_t>(pointer);
  {
    std::lock_guard lock(allocationsMutex);
    mem->mappings.emplace(key, mapping);
  }
  result = submitCommand(
      queue, CL_COMMAND_MAP_BUFFER, blocking, count, waits,
      [=] {
        return (flags & CL_MAP_WRITE_INVALIDATE_REGION) != 0
                   ? CL_SUCCESS
                   : readAllocation(retained->allocation, mapping->offset, mapping->pointer, mapping->size);
      },
      event, false, false,
      [=](cl_int status) {
        if (status == CL_SUCCESS) return;
        std::lock_guard lock(allocationsMutex);
        const auto [begin, end] = retained->mappings.equal_range(key);
        const auto found = std::find_if(begin, end, [&](const auto &entry) { return entry.second == mapping; });
        if (found != end) retained->mappings.erase(found);
      });
  if (error) *error = result;
  return result == CL_SUCCESS ? pointer : nullptr;
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueUnmapMemObject(cl_command_queue queueValue, cl_mem memoryValue, void *pointer, cl_uint count,
                                                        const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  auto *mem = checked(reinterpret_cast<_cl_mem *>(memoryValue), Kind::Memory);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (!mem || mem->context != queue->context || !pointer) return CL_INVALID_MEM_OBJECT;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  std::shared_ptr<BufferMapping> mapping;
  const auto key = reinterpret_cast<uintptr_t>(pointer);
  {
    std::lock_guard lock(allocationsMutex);
    const auto [begin, end] = mem->mappings.equal_range(key);
    const auto found = std::find_if(begin, end, [](const auto &entry) { return !entry.second->unmapping; });
    if (found == end) return CL_INVALID_VALUE;
    mapping = found->second;
    mapping->unmapping = true;
  }
  auto retained = retainMemory(mem);
  return submitCommand(
      queue, CL_COMMAND_UNMAP_MEM_OBJECT, false, count, waits,
      [=] { return writeAllocation(retained->allocation, mapping->offset, mapping->pointer, mapping->size); }, event, false, false,
      [=](cl_int status) {
        std::lock_guard lock(allocationsMutex);
        const auto [begin, end] = retained->mappings.equal_range(key);
        const auto found = std::find_if(begin, end, [&](const auto &entry) { return entry.second == mapping; });
        if (found != end) {
          if (status == CL_SUCCESS) retained->mappings.erase(found);
          else mapping->unmapping = false;
        }
      });
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueMigrateMemObjects(cl_command_queue queueValue, cl_uint count, const cl_mem *memoryObjects,
                                                           cl_mem_migration_flags, cl_uint waitCount, const cl_event *waits,
                                                           cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (count == 0 || !memoryObjects) return CL_INVALID_VALUE;
  std::vector<std::shared_ptr<_cl_mem>> retained;
  for (cl_uint index = 0; index < count; ++index) {
    auto *memory = checked(reinterpret_cast<_cl_mem *>(memoryObjects[index]), Kind::Memory);
    if (!memory) return CL_INVALID_MEM_OBJECT;
    if (memory->context != queue->context) return CL_INVALID_CONTEXT;
    retained.push_back(retainMemory(memory));
  }
  return submitCommand(
      queue, CL_COMMAND_MIGRATE_MEM_OBJECTS, false, waitCount, waits,
      [retained] {
        (void)retained;
        return CL_SUCCESS;
      },
      event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueNDRangeKernel(cl_command_queue queueValue, cl_kernel kernelValue, cl_uint workDimension,
                                                       const size_t *offset, const size_t *global, const size_t *local, cl_uint count,
                                                       const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(kernelValue), Kind::Kernel);
  if (!queue || !kernel) return !queue ? CL_INVALID_COMMAND_QUEUE : CL_INVALID_KERNEL;
  if (workDimension == 0 || workDimension > 3) return CL_INVALID_WORK_DIMENSION;
  if (kernel->arguments.size() != kernel->expectedArguments || (kernel->argumentsSet ^ contains(false))) return CL_INVALID_KERNEL_ARGS;
  if (!global || std::any_of(global, global + workDimension, [](size_t value) { return value == 0; })) {
    if (local) {
      for (cl_uint index = 0; index < workDimension; ++index)
        if (local[index] == 0) return CL_INVALID_WORK_GROUP_SIZE;
    }
    return completeEvent(queue, CL_COMMAND_NDRANGE_KERNEL, count, waits, event);
  }
  for (cl_uint index = 0; index < workDimension; ++index) {
    if (global[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_GLOBAL_WORK_SIZE;
    if (offset && offset[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_GLOBAL_OFFSET;
    if (local && local[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if (local && global) {
    for (cl_uint index = 0; index < workDimension; ++index)
      if (local[index] == 0 || global[index] % local[index] != 0) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if (local && kernel->requiredWorkGroupSize != std::array<size_t, 3>{0, 0, 0}) {
    if (workDimension != 3) return CL_INVALID_WORK_GROUP_SIZE;
    for (size_t index = 0; index < 3; ++index)
      if (local[index] != kernel->requiredWorkGroupSize[index]) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if ((kernel->program->buildOptions ^ contains_slice("-cl-uniform-work-group-size")) && local) {
    for (cl_uint index = 0; index < workDimension; ++index)
      if (local[index] == 0 || !global || global[index] % local[index] != 0) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if ((count != 0 && !waits) || (count == 0 && waits)) return CL_INVALID_EVENT_WAIT_LIST;
  std::vector<_cl_event *> dependencies;
  dependencies.reserve(count);
  for (cl_uint index = 0; index < count; ++index) {
    auto *dependency = checked(reinterpret_cast<_cl_event *>(waits[index]), Kind::Event);
    if (!dependency) return CL_INVALID_EVENT_WAIT_LIST;
    if (dependency->context != queue->context) return CL_INVALID_CONTEXT;
    dependencies.push_back(dependency);
  }
  {
    std::lock_guard lock(eventsMutex);
    for (auto *dependency : dependencies)
      if (dependency->status < 0) return CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
  }
  // Submission must return before execution: a shared-memory kernel may wait
  // for a host store performed immediately after this call returns.
  try {
    cl_int error = CL_SUCCESS;
    auto clone = clCloneKernel(kernelValue, &error);
    if (!clone) return error;
    for (const auto &argument : reinterpret_cast<_cl_kernel *>(clone)->arguments)
      if (argument.memory) retain(argument.memory);
    std::shared_ptr<_cl_kernel> snapshot(reinterpret_cast<_cl_kernel *>(clone), [](_cl_kernel *value) {
      // Drop allocation references before releasing retained memory objects.
      for (auto &argument : value->arguments)
        argument.allocation.reset();
      for (const auto &argument : value->arguments)
        if (argument.memory) clReleaseMemObject(reinterpret_cast<cl_mem>(argument.memory));
      clReleaseKernel(reinterpret_cast<cl_kernel>(value));
    });
    std::vector<std::shared_ptr<Allocation>> used;
    for (const auto &argument : snapshot->arguments)
      if (argument.allocation) used.push_back(argument.allocation);
    for (const auto &[name, values] : snapshot->indirectAllocations)
      used.insert(used.end(), values.begin(), values.end());
    {
      std::lock_guard lock(allocationsMutex);
      for (const auto &[address, allocation] : allocations) {
        if (allocation->svmContextIdentity != queue->context->identity) continue;
        const cl_kernel_exec_info type =
            allocation->usmType == CL_MEM_TYPE_HOST_INTEL     ? CL_KERNEL_EXEC_INFO_INDIRECT_HOST_ACCESS_INTEL
            : allocation->usmType == CL_MEM_TYPE_DEVICE_INTEL ? CL_KERNEL_EXEC_INFO_INDIRECT_DEVICE_ACCESS_INTEL
            : allocation->usmType == CL_MEM_TYPE_SHARED_INTEL ? CL_KERNEL_EXEC_INFO_INDIRECT_SHARED_ACCESS_INTEL
                                                              : 0;
        if (snapshot->indirectTypes.contains(type)) used.push_back(allocation);
      }
    }
    const bool hasOffset = offset;
    const bool hasLocal = local;
    std::array<size_t, 3> deferredOffset{0, 0, 0};
    std::array<size_t, 3> deferredGlobal{1, 1, 1};
    std::array<size_t, 3> deferredLocal{1, 1, 1};
    for (cl_uint index = 0; index < workDimension; ++index) {
      if (hasOffset) deferredOffset[index] = offset[index];
      deferredGlobal[index] = global[index];
      if (hasLocal) deferredLocal[index] = local[index];
    }
    std::vector<cl_event> retainedWaits;
    for (auto *dependency : dependencies)
      retainedWaits.push_back(reinterpret_cast<cl_event>(dependency));
    return enqueuePending(
        queue, CL_COMMAND_NDRANGE_KERNEL, std::move(retainedWaits),
        [queue, snapshot, workDimension, hasOffset, hasLocal, deferredOffset, deferredGlobal, deferredLocal] {
          return finishRemote(queue, snapshot.get(), workDimension, hasOffset ? deferredOffset.data() : nullptr, deferredGlobal.data(),
                              hasLocal ? deferredLocal.data() : nullptr, nullptr);
        },
        event, false, false, {}, allocationUses(std::move(used)));
  } catch (...) {
    return CL_OUT_OF_HOST_MEMORY;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueTask(cl_command_queue queue, cl_kernel kernel, cl_uint count, const cl_event *waits,
                                              cl_event *event) {
  const size_t global = 1;
  return clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &global, nullptr, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMarkerWithWaitList(cl_command_queue queueValue, cl_uint count, const cl_event *waits,
                                                            cl_event *event) {
  return completeEvent(checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue), CL_COMMAND_MARKER, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueBarrierWithWaitList(cl_command_queue queueValue, cl_uint count, const cl_event *waits,
                                                             cl_event *event) {
  return completeEvent(checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue), CL_COMMAND_BARRIER, count, waits, event);
}

CL_API_ENTRY void *CL_API_CALL clSVMAlloc(cl_context contextValue, cl_svm_mem_flags flags, size_t size, cl_uint alignment) {
  auto *context = checked(reinterpret_cast<_cl_context *>(contextValue), Kind::Context);
  if (!context) return nullptr;
  constexpr cl_svm_mem_flags access = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY;
  constexpr cl_svm_mem_flags allowed = access | CL_MEM_SVM_FINE_GRAIN_BUFFER | CL_MEM_SVM_ATOMICS;
  const auto mode = flags & access;
  if ((flags & ~allowed) != 0 || (mode != 0 && (mode & (mode - 1)) != 0)
      || ((flags & CL_MEM_SVM_ATOMICS) && !(flags & CL_MEM_SVM_FINE_GRAIN_BUFFER)))
    return nullptr;
  cl_int error = CL_SUCCESS;
  auto allocation = allocate(size, &error, alignment, true);
  if (allocation) {
    allocation->externallyMutable.store(true, std::memory_order_relaxed);
    allocation->runtimeOwnsBase = true;
    allocation->svmContextIdentity = context->identity;
  }
  return !allocation ? nullptr : allocation->data();
}
CL_API_ENTRY void CL_API_CALL clSVMFree(cl_context, void *pointer) {
  std::shared_ptr<Allocation> allocation;
  {
    std::lock_guard lock(allocationsMutex);
    auto found = allocations.find(reinterpret_cast<uintptr_t>(pointer));
    if (found == allocations.end()) return;
    allocation = found->second;
    const auto start = reinterpret_cast<uintptr_t>(allocation->data());
    for (auto mapping = svmMappings.begin(); mapping != svmMappings.end();) {
      if (mapping->first >= start && mapping->first <= start + allocation->size) mapping = svmMappings.erase(mapping);
      else ++mapping;
    }
    allocations.erase(found);
  }
  releaseAllocation(allocation, true);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMFree(cl_command_queue queueValue, cl_uint count, void *pointers[],
                                                 void(CL_CALLBACK *callback)(cl_command_queue, cl_uint, void *[], void *), void *userData,
                                                 cl_uint waitCount, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if ((count == 0) != (pointers == nullptr)) return CL_INVALID_VALUE;
  auto result = validateWaitList(queue, waitCount, waits);
  if (result != CL_SUCCESS) return result;
  std::vector<void *> copiedPointers;
  if (count != 0) copiedPointers.assign(pointers, pointers + count);
  for (void *pointer : copiedPointers) {
    auto allocation = findPointer(pointer);
    if (allocation && allocation->svmContextIdentity != 0 && allocation->svmContextIdentity != queue->context->identity)
      return CL_INVALID_VALUE;
    if (!callback && (!allocation || allocation->data() != pointer || !allocation->runtimeOwnsBase || allocation->svmContextIdentity == 0))
      return CL_INVALID_VALUE;
  }
  return submitCommand(
      queue, CL_COMMAND_SVM_FREE, false, waitCount, waits,
      [=]() mutable {
        if (callback) callback(queueValue, count, copiedPointers.data(), userData);
        else
          for (void *pointer : copiedPointers)
            clSVMFree(reinterpret_cast<cl_context>(queue->context), pointer);
        return CL_SUCCESS;
      },
      event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMigrateMem(cl_command_queue queueValue, cl_uint count, const void **pointers,
                                                       const size_t *sizes, cl_mem_migration_flags flags, cl_uint waitCount,
                                                       const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  if (count == 0 || !pointers || (flags & ~(CL_MIGRATE_MEM_OBJECT_HOST | CL_MIGRATE_MEM_OBJECT_CONTENT_UNDEFINED))) return CL_INVALID_VALUE;
  auto result = validateWaitList(queue, waitCount, waits);
  if (result != CL_SUCCESS) return result;
  struct MigrationRange {
    std::shared_ptr<Allocation> allocation;
    size_t offset;
    size_t size;
  };
  std::vector<MigrationRange> ranges;
  ranges.reserve(count);
  for (cl_uint index = 0; index < count; ++index) {
    size_t offset = 0;
    auto allocation = findPointer(pointers[index], &offset);
    if (!allocation || allocation->svmContextIdentity != queue->context->identity || offset == allocation->size) return CL_INVALID_VALUE;
    const size_t rangeSize = !sizes || sizes[index] == 0 ? allocation->size : sizes[index];
    const size_t rangeOffset = !sizes || sizes[index] == 0 ? 0 : offset;
    if (!validRange(allocation, rangeOffset, rangeSize)) return CL_INVALID_VALUE;
    ranges.push_back({std::move(allocation), rangeOffset, rangeSize});
  }
  std::vector<std::shared_ptr<Allocation>> used;
  for (const auto &range : ranges)
    used.push_back(range.allocation);
  return submitCommand(
      queue, CL_COMMAND_SVM_MIGRATE_MEM, false, waitCount, waits,
      [=] {
        if ((flags & CL_MIGRATE_MEM_OBJECT_HOST) && !(flags & CL_MIGRATE_MEM_OBJECT_CONTENT_UNDEFINED))
          for (const auto &range : ranges) {
            auto *host = static_cast<uint8_t *>(range.allocation->data()) + range.offset;
            const cl_int error = readAllocation(range.allocation, range.offset, host, range.size);
            if (error != CL_SUCCESS) return error;
          }
        return CL_SUCCESS;
      },
      event, false, false, {}, allocationUses(std::move(used)));
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMemcpy(cl_command_queue queueValue, cl_bool blocking, void *dst, const void *src, size_t size,
                                                   cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  if (!dst || !src) return CL_INVALID_VALUE;
  const auto destinationAddress = reinterpret_cast<uintptr_t>(dst);
  const auto sourceAddress = reinterpret_cast<uintptr_t>(src);
  if ((destinationAddress >= sourceAddress && destinationAddress - sourceAddress < size)
      || (sourceAddress >= destinationAddress && sourceAddress - destinationAddress < size))
    return CL_MEM_COPY_OVERLAP;
  size_t destinationOffset = 0, sourceOffset = 0;
  auto destinationAllocation = findPointer(dst, &destinationOffset);
  auto sourceAllocation = findPointer(src, &sourceOffset);
  if (destinationAllocation && !validRange(destinationAllocation, destinationOffset, size)) return CL_INVALID_VALUE;
  if (sourceAllocation && !validRange(sourceAllocation, sourceOffset, size)) return CL_INVALID_VALUE;
  return submitCommand(
      queue, CL_COMMAND_SVM_MEMCPY, blocking, count, waits,
      [=] {
        cl_int result = CL_SUCCESS;
        // For an SVM-to-host copy, the remote mirror is the source of truth after a
        // kernel has written the allocation.  Keep the host-visible bytes as the
        // source for SVM-to-SVM copies; those are commonly used to upload host-side
        // changes between launches.
        if (sourceAllocation && !destinationAllocation) {
          result = readAllocation(sourceAllocation, sourceOffset, static_cast<uint8_t *>(sourceAllocation->data()) + sourceOffset, size);
          if (result != CL_SUCCESS) return result;
        }
        if (destinationAllocation) {
          const void *source = !sourceAllocation ? src : static_cast<const uint8_t *>(sourceAllocation->data()) + sourceOffset;
          std::memmove(static_cast<uint8_t *>(destinationAllocation->data()) + destinationOffset, source, size);
          result = writeAllocation(destinationAllocation, destinationOffset,
                                   static_cast<uint8_t *>(destinationAllocation->data()) + destinationOffset, size);
          if (result != CL_SUCCESS) return result;
        } else {
          const void *source = !sourceAllocation ? src : static_cast<const uint8_t *>(sourceAllocation->data()) + sourceOffset;
          std::memcpy(dst, source, size);
        }
        return CL_SUCCESS;
      },
      event, false, false, {}, allocationUses({sourceAllocation, destinationAllocation}));
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMemFill(cl_command_queue queueValue, void *pointer, const void *pattern, size_t patternSize,
                                                    size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto waited = validateWaitList(queue, count, waits);
  if (waited != CL_SUCCESS) return waited;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  if (!allocation || !pattern || patternSize == 0 || size % patternSize != 0 || !validRange(allocation, offset, size))
    return CL_INVALID_VALUE;
  std::vector<uint8_t> copiedPattern(static_cast<const uint8_t *>(pattern), static_cast<const uint8_t *>(pattern) + patternSize);
  return submitCommand(
      queue, CL_COMMAND_SVM_MEMFILL, false, count, waits,
      [=] {
        auto *bytes = static_cast<uint8_t *>(allocation->data());
        for (size_t index = 0; index < size; ++index)
          bytes[offset + index] = copiedPattern[index % patternSize];
        auto result = writeAllocation(allocation, offset, bytes + offset, size);
        if (result != CL_SUCCESS) return result;
        return CL_SUCCESS;
      },
      event, false, false, {}, allocationUses({allocation}));
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMap(cl_command_queue queueValue, cl_bool blocking, cl_map_flags, void *pointer, size_t size,
                                                cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  if (!validRange(allocation, offset, size)) return CL_INVALID_VALUE;
  auto mapping = std::make_shared<BufferMapping>();
  mapping->offset = offset;
  mapping->size = size;
  mapping->pointer = pointer;
  const auto key = reinterpret_cast<uintptr_t>(pointer);
  {
    std::lock_guard lock(allocationsMutex);
    if (!svmMappings.emplace(key, mapping).second) return CL_INVALID_OPERATION;
  }
  return submitCommand(
      queue, CL_COMMAND_SVM_MAP, blocking, count, waits, [=] { return readAllocation(allocation, offset, pointer, size); }, event, false,
      false,
      [=](cl_int status) {
        if (status == CL_SUCCESS) return;
        std::lock_guard lock(allocationsMutex);
        const auto found = svmMappings.find(key);
        if (found != svmMappings.end() && found->second == mapping) svmMappings.erase(found);
      },
      allocationUses({allocation}));
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMUnmap(cl_command_queue queueValue, void *pointer, cl_uint count, const cl_event *waits,
                                                  cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto result = validateWaitList(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  std::shared_ptr<BufferMapping> mapping;
  const auto key = reinterpret_cast<uintptr_t>(pointer);
  {
    std::lock_guard lock(allocationsMutex);
    const auto found = svmMappings.find(key);
    if (found == svmMappings.end() || found->second->unmapping) return CL_INVALID_VALUE;
    mapping = found->second;
    if (!validRange(allocation, mapping->offset, mapping->size) || offset != mapping->offset) return CL_INVALID_VALUE;
    mapping->unmapping = true;
  }
  return submitCommand(
      queue, CL_COMMAND_SVM_UNMAP, false, count, waits,
      [=] { return writeAllocation(allocation, mapping->offset, mapping->pointer, mapping->size); }, event, false, false,
      [=](cl_int status) {
        std::lock_guard lock(allocationsMutex);
        const auto found = svmMappings.find(key);
        if (found != svmMappings.end() && found->second == mapping) {
          if (status == CL_SUCCESS) svmMappings.erase(found);
          else mapping->unmapping = false;
        }
      },
      allocationUses({allocation}));
}

namespace {
void *allocateUSM(cl_context contextValue, cl_device_id deviceValue, cl_unified_shared_memory_type_intel type, size_t size,
                  cl_uint alignment, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(contextValue), Kind::Context);
  if (!context) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (type != CL_MEM_TYPE_HOST_INTEL && (deviceValue || type == CL_MEM_TYPE_DEVICE_INTEL) && !validDevice(deviceValue)) {
    if (error) *error = CL_INVALID_DEVICE;
    return nullptr;
  }
  auto allocation = allocate(size, error, alignment, type != CL_MEM_TYPE_DEVICE_INTEL);
  if (allocation) {
    allocation->externallyMutable.store(type != CL_MEM_TYPE_DEVICE_INTEL, std::memory_order_relaxed);
    allocation->runtimeOwnsBase = true;
    allocation->svmContextIdentity = context->identity;
    allocation->usmType = type;
  }
  return !allocation ? nullptr : allocation->data();
}
} // namespace

CL_API_ENTRY void *CL_API_CALL clHostMemAllocINTEL(cl_context context, const cl_mem_properties_intel *, size_t size, cl_uint alignment,
                                                   cl_int *error) {
  return allocateUSM(context, nullptr, CL_MEM_TYPE_HOST_INTEL, size, alignment, error);
}
CL_API_ENTRY void *CL_API_CALL clDeviceMemAllocINTEL(cl_context context, cl_device_id device, const cl_mem_properties_intel *, size_t size,
                                                     cl_uint alignment, cl_int *error) {
  return allocateUSM(context, device, CL_MEM_TYPE_DEVICE_INTEL, size, alignment, error);
}
CL_API_ENTRY void *CL_API_CALL clSharedMemAllocINTEL(cl_context context, cl_device_id device, const cl_mem_properties_intel *, size_t size,
                                                     cl_uint alignment, cl_int *error) {
  return allocateUSM(context, device, CL_MEM_TYPE_SHARED_INTEL, size, alignment, error);
}
namespace {
cl_int freeUSM(cl_context contextValue, void *pointer, bool blocking) {
  auto *context = checked(reinterpret_cast<_cl_context *>(contextValue), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  if (!pointer) return CL_SUCCESS;
  auto allocation = findPointer(pointer);
  if (!allocation || allocation->data() != pointer || allocation->usmType == CL_MEM_TYPE_UNKNOWN_INTEL
      || allocation->svmContextIdentity != context->identity)
    return CL_INVALID_VALUE;
  if (blocking) {
    std::unique_lock lock(allocation->useMutex);
    allocation->usesFinished.wait(lock, [&] { return allocation->pendingUses == 0; });
  }
  return releaseAllocation(allocation, true);
}
} // namespace

CL_API_ENTRY cl_int CL_API_CALL clMemFreeINTEL(cl_context context, void *pointer) { return freeUSM(context, pointer, false); }
CL_API_ENTRY cl_int CL_API_CALL clMemBlockingFreeINTEL(cl_context context, void *pointer) { return freeUSM(context, pointer, true); }
CL_API_ENTRY cl_int CL_API_CALL clGetMemAllocInfoINTEL(cl_context contextValue, const void *pointer, cl_mem_info_intel name, size_t size,
                                                       void *output, size_t *written) {
  auto *context = checked(reinterpret_cast<_cl_context *>(contextValue), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  if (!allocation || allocation->usmType == CL_MEM_TYPE_UNKNOWN_INTEL || allocation->svmContextIdentity != context->identity
      || offset >= allocation->size) {
    if (name == CL_MEM_ALLOC_TYPE_INTEL) {
      cl_unified_shared_memory_type_intel type = CL_MEM_TYPE_UNKNOWN_INTEL;
      return outputScalar(size, output, written, type);
    }
    if (name == CL_MEM_ALLOC_DEVICE_INTEL) {
      cl_device_id deviceValue = nullptr;
      return outputScalar(size, output, written, deviceValue);
    }
    return CL_INVALID_VALUE;
  }
  switch (name) {
    case CL_MEM_ALLOC_TYPE_INTEL: {
      cl_unified_shared_memory_type_intel v = allocation->usmType;
      return outputScalar(size, output, written, v);
    }
    case CL_MEM_ALLOC_BASE_PTR_INTEL: return outputScalar(size, output, written, allocation->data());
    case CL_MEM_ALLOC_SIZE_INTEL: return outputScalar(size, output, written, allocation->size);
    case CL_MEM_ALLOC_DEVICE_INTEL: {
      cl_device_id v = allocation->usmType == CL_MEM_TYPE_HOST_INTEL ? nullptr : reinterpret_cast<cl_device_id>(&device);
      return outputScalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArgMemPointerINTEL(cl_kernel value, cl_uint index, const void *pointer) {
  return clSetKernelArgSVMPointer(value, index, pointer);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemcpyINTEL(cl_command_queue queueValue, cl_bool blocking, void *dst, const void *src, size_t size,
                                                     cl_uint count, const cl_event *waits, cl_event *event) {
  return clEnqueueSVMMemcpy(queueValue, blocking, dst, src, size, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemFillINTEL(cl_command_queue queueValue, void *dst, const void *pattern, size_t patternSize,
                                                      size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  return clEnqueueSVMMemFill(queueValue, dst, pattern, patternSize, size, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemAdviseINTEL(cl_command_queue queueValue, const void *pointer, size_t size, cl_mem_advice_intel,
                                                        cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queueValue), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  size_t offset = 0;
  auto allocation = findPointer(pointer, &offset);
  if (!validRange(allocation, offset, size)) return CL_INVALID_VALUE;
  return submitCommand(
      queue, CL_COMMAND_MEMADVISE_INTEL, false, count, waits, [] { return CL_SUCCESS; }, event, false, false, {},
      allocationUses({allocation}));
}

CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddress(const char *name) {
  if (!name) return nullptr;
#define GEMSTONE_EXTENSION(function)                                                                                                       \
  if (std::strcmp(name, #function) == 0) return reinterpret_cast<void *>(&function)
  GEMSTONE_EXTENSION(clIcdGetPlatformIDsKHR);
  GEMSTONE_EXTENSION(clHostMemAllocINTEL);
  GEMSTONE_EXTENSION(clDeviceMemAllocINTEL);
  GEMSTONE_EXTENSION(clSharedMemAllocINTEL);
  GEMSTONE_EXTENSION(clMemFreeINTEL);
  GEMSTONE_EXTENSION(clMemBlockingFreeINTEL);
  GEMSTONE_EXTENSION(clGetMemAllocInfoINTEL);
  GEMSTONE_EXTENSION(clSetKernelArgMemPointerINTEL);
  GEMSTONE_EXTENSION(clEnqueueMemFillINTEL);
  GEMSTONE_EXTENSION(clEnqueueMemcpyINTEL);
  GEMSTONE_EXTENSION(clEnqueueMemAdviseINTEL);
  GEMSTONE_EXTENSION(clCreateProgramWithIL);
  GEMSTONE_EXTENSION(clSetKernelArgSVMPointer);
  GEMSTONE_EXTENSION(clSetKernelExecInfo);
#undef GEMSTONE_EXTENSION
  return nullptr;
}
CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddressForPlatform(cl_platform_id value, const char *name) {
  return validPlatform(value) ? clGetExtensionFunctionAddress(name) : nullptr;
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelSubGroupInfo(cl_kernel value, cl_device_id selected, cl_kernel_sub_group_info name,
                                                        size_t inputSize, const void *input, size_t size, void *output, size_t *written) {
  if (!checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel)) return CL_INVALID_KERNEL;
  // OpenCL permits a null device when the kernel's program has one device.
  if (selected && !validDevice(selected)) return CL_INVALID_DEVICE;
  constexpr size_t subgroupSize = 32;
  constexpr size_t maxWorkGroupSize = 1024;
  if (name == CL_KERNEL_MAX_SUB_GROUP_SIZE_FOR_NDRANGE) {
    size_t v = subgroupSize;
    return outputScalar(size, output, written, v);
  }
  if (name == CL_KERNEL_MAX_NUM_SUB_GROUPS) {
    size_t v = maxWorkGroupSize / subgroupSize;
    return outputScalar(size, output, written, v);
  }
  if (name == CL_KERNEL_COMPILE_NUM_SUB_GROUPS) {
    size_t v = 0;
    return outputScalar(size, output, written, v);
  }
  if (name == CL_KERNEL_COMPILE_SUB_GROUP_SIZE_INTEL) {
    size_t v = 0;
    return outputScalar(size, output, written, v);
  }
  if (name == CL_KERNEL_SUB_GROUP_COUNT_FOR_NDRANGE) {
    if (!input || inputSize == 0 || inputSize % sizeof(size_t) != 0 || inputSize > 3 * sizeof(size_t)) return CL_INVALID_VALUE;
    const size_t dimensions = inputSize / sizeof(size_t);
    const auto *local = static_cast<const size_t *>(input);
    size_t workItems = 1;
    for (size_t index = 0; index < dimensions; ++index) {
      if (local[index] == 0 || workItems > std::numeric_limits<size_t>::max() / local[index]) return CL_INVALID_VALUE;
      workItems *= local[index];
    }
    const size_t v = (workItems + subgroupSize - 1) / subgroupSize;
    return outputScalar(size, output, written, v);
  }
  if (name == CL_KERNEL_LOCAL_SIZE_FOR_SUB_GROUP_COUNT) {
    if (!input || inputSize != sizeof(size_t) || !output || size < sizeof(size_t)) return CL_INVALID_VALUE;
    size_t count = 0;
    std::memcpy(&count, input, sizeof(count));
    size_t local[3] = {0, 0, 0};
    if (count != 0 && count <= maxWorkGroupSize / subgroupSize) {
      local[0] = count * subgroupSize;
      local[1] = 1;
      local[2] = 1;
    }
    return outputBytes(size, output, written, local, size < 3 * sizeof(size_t) ? size : 3 * sizeof(size_t));
  }
  return CL_INVALID_VALUE;
}

CL_API_ENTRY cl_int CL_API_CALL clGetKernelSubGroupInfoKHR(cl_kernel value, cl_device_id selected, cl_kernel_sub_group_info name,
                                                           size_t inputSize, const void *input, size_t size, void *output,
                                                           size_t *written) {
  return clGetKernelSubGroupInfo(value, selected, name, inputSize, input, size, output, written);
}

} // extern "C"

namespace {

void initializeDispatch() {
  std::call_once(dispatchInitialized, [] {
    registerLiveObject(&platform);
    registerLiveObject(&device);
    platform.dispatch = &dispatch;
    device.dispatch = &dispatch;
    device.platform = &platform;
    gemstone::opencl::populateDispatch(dispatch);
  });
}

} // namespace
