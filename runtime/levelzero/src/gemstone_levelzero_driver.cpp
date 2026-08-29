#include "gemstone_levelzero_driver.h"
#include "gemstone_execution_rpc.h"

#include <ze_ddi.h>

#include <aspartame/vector.hpp>

#include <algorithm>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <functional>
#include <limits>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <unordered_map>
#include <utility>
#include <vector>

#include <unistd.h>

namespace {

using namespace aspartame;

constexpr uint64_t kMagic = 0x47454d53544f4e45ULL;

enum class Kind : uint32_t { Driver, Device, Context, Queue, List, EventPool, Event, Fence, Module, BuildLog, Kernel };

struct Handle {
  explicit Handle(Kind value) : kind(value) {}
  virtual ~Handle() = default;
  uint64_t magic = kMagic;
  Kind kind;
  std::atomic<uint32_t> users{0};
};

template <class T> class HandleUse {
public:
  HandleUse() = default;
  explicit HandleUse(T *value) : value_(value) {
    if (value_ != nullptr) value_->users.fetch_add(1, std::memory_order_relaxed);
  }
  HandleUse(const HandleUse &other) : HandleUse(other.value_) {}
  HandleUse(HandleUse &&other) noexcept : value_(std::exchange(other.value_, nullptr)) {}
  HandleUse &operator=(HandleUse other) noexcept {
    swap(other);
    return *this;
  }
  ~HandleUse() {
    if (value_ != nullptr) value_->users.fetch_sub(1, std::memory_order_release);
  }
  T *get() const { return value_; }
  T *operator->() const { return value_; }
  explicit operator bool() const { return value_ != nullptr; }
  void swap(HandleUse &other) noexcept { std::swap(value_, other.value_); }

private:
  T *value_ = nullptr;
};

template <class T> ze_result_t destroy_handle(T *value) {
  if (value == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (value->users.load(std::memory_order_acquire) != 0) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  delete value;
  return ZE_RESULT_SUCCESS;
}

struct Driver final : Handle { Driver() : Handle(Kind::Driver) {} };
struct Device final : Handle { Device() : Handle(Kind::Device) {} };
struct Context final : Handle { Context() : Handle(Kind::Context) {} };

struct Event final : Handle {
  Event() : Handle(Kind::Event) {}
  std::mutex mutex;
  std::condition_variable changed;
  bool signalled = false;
};

struct EventPool final : Handle {
  explicit EventPool(ze_event_pool_flags_t value) : Handle(Kind::EventPool), flags(value) {}
  ze_event_pool_flags_t flags;
};

struct Fence final : Handle {
  Fence() : Handle(Kind::Fence) {}
  Event event;
};

using Command = std::function<ze_result_t()>;

struct CommandList final : Handle {
  CommandList(bool immediate_value, Context *context_value, Device *device_value)
      : Handle(Kind::List), immediate(immediate_value), context(context_value), device(device_value) {}
  bool immediate;
  bool closed = false;
  Context *context;
  Device *device;
  std::vector<Command> commands;
};

struct Queue final : Handle {
  Queue(Context *context_value, Device *device_value, const ze_command_queue_desc_t &value)
      : Handle(Kind::Queue), context(context_value), device(device_value), desc(value) {}
  Context *context;
  Device *device;
  ze_command_queue_desc_t desc;
};

struct BuildLog final : Handle {
  explicit BuildLog(std::string value) : Handle(Kind::BuildLog), text(std::move(value)) {}
  std::string text;
};

enum class ModuleBackend { TransferOnly, External, RPC };

struct Module final : Handle {
  Module(std::vector<uint8_t> value, uint64_t executor_value, ModuleBackend backend_value)
      : Handle(Kind::Module), bytes(std::move(value)), executor_id(executor_value), backend(backend_value) {}
  std::vector<uint8_t> bytes;
  uint64_t executor_id;
  ModuleBackend backend;
};

struct Kernel final : Handle {
  Kernel(Module *module_value, std::string name_value)
      : Handle(Kind::Kernel), module(module_value), name(std::move(name_value)) {
    module->users.fetch_add(1, std::memory_order_relaxed);
  }
  ~Kernel() override { module->users.fetch_sub(1, std::memory_order_release); }
  Module *module;
  std::string name;
  uint32_t group_x = 1;
  uint32_t group_y = 1;
  uint32_t group_z = 1;
  ze_kernel_indirect_access_flags_t indirect = 0;
  struct Argument {
    bool local = false;
    size_t local_size = 0;
    std::vector<uint8_t> bytes;
  };
  std::vector<Argument> arguments;
};

struct Allocation;
using AllocationRef = std::shared_ptr<Allocation>;

struct KernelLaunch {
  HandleUse<Kernel> lifetime;
  Module *module;
  std::string name;
  uint32_t group_x;
  uint32_t group_y;
  uint32_t group_z;
  std::vector<Kernel::Argument> arguments;
  std::vector<AllocationRef> allocations;
};

struct Allocation {
  void *base;
  size_t size;
  ze_memory_type_t type;
  uint64_t id;
};

Driver global_driver;
Device global_device;
std::mutex allocations_mutex;
std::unordered_map<void *, AllocationRef> allocations;
std::atomic<uint64_t> next_allocation_sequence{1};
std::mutex executor_mutex;
gemstone_levelzero_executor_v1 executor{};
std::mutex rpc_transaction_mutex;

enum class RemoteMode { Uninitialized, Disabled, Ready, Misconfigured };

struct RemoteExecution {
  ~RemoteExecution() { gemstone_execution_client_destroy(client); }
  std::once_flag initialized;
  RemoteMode mode = RemoteMode::Uninitialized;
  gemstone_execution_client *client = nullptr;
  std::string error;
};

RemoteExecution remote;

uint64_t next_allocation_id() {
  // Allocation identity is global to the shared JVM execution service, while
  // this driver global is private to each CTest process. Keep concurrently
  // executing oneDPL tests in disjoint positive ID namespaces.
  const auto process = static_cast<uint64_t>(static_cast<uint32_t>(getpid())) & 0x7fffffffULL;
  const auto sequence = next_allocation_sequence.fetch_add(1) & 0xffffffffULL;
  return (process << 32) | sequence;
}

RemoteExecution &remote_execution() {
  std::call_once(remote.initialized, [] {
    const char *port = std::getenv("GEMSTONE_RPC_PORT");
    const char *token = std::getenv("GEMSTONE_RPC_TOKEN");
    const bool has_port = port != nullptr && port[0] != '\0';
    const bool has_token = token != nullptr && token[0] != '\0';
    if (!has_port && !has_token) {
      remote.mode = RemoteMode::Disabled;
      return;
    }
    if (!has_port || !has_token) {
      remote.mode = RemoteMode::Misconfigured;
      remote.error = "GEMSTONE_RPC_PORT and GEMSTONE_RPC_TOKEN must be set together";
      return;
    }
    remote.client = gemstone_execution_client_create_from_environment();
    if (remote.client == nullptr) {
      remote.mode = RemoteMode::Misconfigured;
      remote.error = "GEMSTONE_RPC_TOKEN must contain exactly 64 lowercase hexadecimal characters";
      return;
    }
    if (gemstone_execution_attest(remote.client) != 0) {
      remote.mode = RemoteMode::Misconfigured;
      remote.error = gemstone_execution_client_error(remote.client);
      return;
    }
    remote.mode = RemoteMode::Ready;
  });
  return remote;
}

std::string remote_error(RemoteExecution &state) {
  const char *value = state.client == nullptr ? nullptr : gemstone_execution_client_error(state.client);
  if (value != nullptr && value[0] != '\0') return value;
  return state.error.empty() ? "Gemstone execution RPC failed" : state.error;
}

void remember_remote_error(RemoteExecution &state, std::string message) {
  state.error = std::move(message);
  if (std::getenv("GEMSTONE_TRACE") != nullptr)
    std::fprintf(stderr, "Gemstone Level Zero: %s\n", state.error.c_str());
}

gemstone_allocation_kind rpc_kind(ze_memory_type_t kind) {
  switch (kind) {
  case ZE_MEMORY_TYPE_DEVICE: return GEMSTONE_ALLOCATION_DEVICE;
  case ZE_MEMORY_TYPE_SHARED: return GEMSTONE_ALLOCATION_SHARED;
  case ZE_MEMORY_TYPE_HOST: return GEMSTONE_ALLOCATION_HOST;
  default: return GEMSTONE_ALLOCATION_DEVICE;
  }
}

// Bound the amount of immutable JVM allocation state copied into one response.
// Large oneDPL buffers otherwise create long GC-sensitive gaps inside a single
// response body and can exceed the native transport's progress timeout.
constexpr size_t kMirrorChunkSize = 1U * 1024U * 1024U;

int mirror_allocations_to_server(RemoteExecution &state) {
  std::lock_guard lock(allocations_mutex);
  for (const auto &[base, allocation] : allocations) {
    const auto *bytes = static_cast<const uint8_t *>(base);
    size_t offset = 0;
    while (offset < allocation->size) {
      const auto count = static_cast<uint32_t>(std::min(kMirrorChunkSize, allocation->size - offset));
      if (gemstone_execution_write_allocation(state.client, allocation->id, offset, bytes + offset, count) != 0)
        return -1;
      offset += count;
    }
  }
  return 0;
}

int mirror_allocations_from_server(RemoteExecution &state) {
  std::lock_guard lock(allocations_mutex);
  for (const auto &[base, allocation] : allocations) {
    auto *bytes = static_cast<uint8_t *>(base);
    size_t offset = 0;
    while (offset < allocation->size) {
      const auto count = static_cast<uint32_t>(std::min(kMirrorChunkSize, allocation->size - offset));
      if (gemstone_execution_read_allocation(state.client, allocation->id, offset, bytes + offset, count) != 0)
        return -1;
      offset += count;
    }
  }
  return 0;
}

bool classify_pointer_argument(const std::vector<uint8_t> &bytes, uint64_t &allocation_id, uint64_t &offset) {
  if (bytes.size() != sizeof(void *)) return false;
  uintptr_t pointer = 0;
  std::memcpy(&pointer, bytes.data(), sizeof(pointer));
  std::lock_guard lock(allocations_mutex);
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (pointer >= start && pointer < start + allocation->size) {
      allocation_id = allocation->id;
      offset = pointer - start;
      return true;
    }
  }
  return false;
}

AllocationRef retain_allocation(const void *pointer, size_t size) {
  if (pointer == nullptr) return {};
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::lock_guard lock(allocations_mutex);
  for (const auto &[base, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(base);
    if (address >= start && address - start <= allocation->size && size <= allocation->size - (address - start)) return allocation;
  }
  return {};
}

std::vector<AllocationRef> retain_all_allocations() {
  std::lock_guard lock(allocations_mutex);
  std::vector<AllocationRef> retained;
  retained.reserve(allocations.size());
  for (const auto &[_, allocation] : allocations)
    retained.push_back(allocation);
  return retained;
}

template <class T, class H> T *cast(H handle, Kind expected) {
  auto *value = reinterpret_cast<T *>(handle);
  return value != nullptr && value->magic == kMagic && value->kind == expected ? value : nullptr;
}

ze_result_t check_version(ze_api_version_t version, const void *table) {
  if (table == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (ZE_MAJOR_VERSION(version) != ZE_MAJOR_VERSION(ZE_API_VERSION_CURRENT)) return ZE_RESULT_ERROR_UNSUPPORTED_VERSION;
  return ZE_RESULT_SUCCESS;
}

ze_result_t wait_event(Event *event, uint64_t timeout) {
  if (event == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::unique_lock lock(event->mutex);
  if (event->signalled) return ZE_RESULT_SUCCESS;
  if (timeout == 0) return ZE_RESULT_NOT_READY;
  if (timeout == std::numeric_limits<uint64_t>::max()) {
    event->changed.wait(lock, [&] { return event->signalled; });
    return ZE_RESULT_SUCCESS;
  }
  return event->changed.wait_for(lock, std::chrono::nanoseconds(timeout), [&] { return event->signalled; })
             ? ZE_RESULT_SUCCESS
             : ZE_RESULT_NOT_READY;
}

ze_result_t signal_event(Event *event) {
  if (event == nullptr) return ZE_RESULT_SUCCESS;
  {
    std::lock_guard lock(event->mutex);
    event->signalled = true;
  }
  event->changed.notify_all();
  return ZE_RESULT_SUCCESS;
}

using EventUses = std::vector<HandleUse<Event>>;

ze_result_t retain_events(uint32_t count, ze_event_handle_t *events, EventUses &retained) {
  if (count != 0 && events == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  retained.reserve(count);
  for (uint32_t index = 0; index < count; ++index) {
    auto *event = cast<Event>(events[index], Kind::Event);
    if (event == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    retained.emplace_back(event);
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t retain_signal(ze_event_handle_t handle, HandleUse<Event> &retained) {
  if (handle == nullptr) return ZE_RESULT_SUCCESS;
  auto *event = cast<Event>(handle, Kind::Event);
  if (event == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  retained = HandleUse<Event>(event);
  return ZE_RESULT_SUCCESS;
}

ze_result_t wait_dependencies(const EventUses &events) {
  for (const auto &event : events) {
    const auto result = wait_event(event.get(), std::numeric_limits<uint64_t>::max());
    if (result != ZE_RESULT_SUCCESS) return result;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t append(CommandList *list, Command command) {
  if (list == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (list->immediate) return command();
  if (list->closed) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  list->commands.emplace_back(std::move(command));
  return ZE_RESULT_SUCCESS;
}

ze_result_t retain_command_events(ze_event_handle_t signal_handle, uint32_t wait_count, ze_event_handle_t *wait_handles,
                                  HandleUse<Event> &signal, EventUses &waits) {
  const auto wait_result = retain_events(wait_count, wait_handles, waits);
  return wait_result == ZE_RESULT_SUCCESS ? retain_signal(signal_handle, signal) : wait_result;
}

ze_result_t ZE_APICALL init(ze_init_flags_t) { return ZE_RESULT_SUCCESS; }

ze_result_t ZE_APICALL init_drivers(uint32_t *count, ze_driver_handle_t *drivers,
                                    ze_init_driver_type_desc_t *desc) {
  if (count == nullptr || desc == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if ((desc->flags & ZE_INIT_DRIVER_TYPE_FLAG_GPU) == 0) {
    *count = 0;
    return ZE_RESULT_ERROR_UNINITIALIZED;
  }
  if (drivers == nullptr) {
    *count = 1;
  } else if (*count != 0) {
    drivers[0] = reinterpret_cast<ze_driver_handle_t>(&global_driver);
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driver_get(uint32_t *count, ze_driver_handle_t *drivers) {
  if (count == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (drivers == nullptr) {
    *count = 1;
  } else if (*count != 0) {
    drivers[0] = reinterpret_cast<ze_driver_handle_t>(&global_driver);
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driver_api_version(ze_driver_handle_t, ze_api_version_t *version) {
  if (version == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *version = ZE_API_VERSION_CURRENT;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driver_properties(ze_driver_handle_t, ze_driver_properties_t *properties) {
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->driverVersion = 1;
  std::memset(properties->uuid.id, 0, ZE_MAX_DRIVER_UUID_SIZE);
  std::memcpy(properties->uuid.id, "GEMSTONE", 8);
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driver_ipc_properties(ze_driver_handle_t, ze_driver_ipc_properties_t *properties) {
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->flags = 0;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driver_extensions(ze_driver_handle_t, uint32_t *count,
                                         ze_driver_extension_properties_t *properties) {
  if (count == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *count = 0;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL driver_extension_address(ze_driver_handle_t, const char *, void **address) {
  if (address == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *address = nullptr;
  return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
}

ze_result_t ZE_APICALL driver_last_error(ze_driver_handle_t handle, const char **description) {
  if (cast<Driver>(handle, Kind::Driver) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (description == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  static thread_local std::string text;
  std::lock_guard rpc_lock(rpc_transaction_mutex);
  text = remote_error(remote_execution());
  *description = text.c_str();
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL device_get(ze_driver_handle_t, uint32_t *count, ze_device_handle_t *devices) {
  if (count == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (devices == nullptr) {
    *count = 1;
  } else if (*count != 0) {
    devices[0] = reinterpret_cast<ze_device_handle_t>(&global_device);
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL device_subdevices(ze_device_handle_t, uint32_t *count, ze_device_handle_t *) {
  if (count == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *count = 0;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL device_properties(ze_device_handle_t, ze_device_properties_t *properties) {
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
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

ze_result_t ZE_APICALL device_compute(ze_device_handle_t, ze_device_compute_properties_t *properties) {
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
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

ze_result_t ZE_APICALL device_module(ze_device_handle_t, ze_device_module_properties_t *properties) {
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
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

ze_result_t ZE_APICALL device_queue_groups(ze_device_handle_t, uint32_t *count,
                                           ze_command_queue_group_properties_t *properties) {
  if (count == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (properties == nullptr) {
    *count = 1;
  } else if (*count != 0) {
    properties[0].flags = ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COMPUTE |
                          ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COPY |
                          ZE_COMMAND_QUEUE_GROUP_PROPERTY_FLAG_COOPERATIVE_KERNELS;
    properties[0].maxMemoryFillPatternSize = 128;
    properties[0].numQueues = 1;
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL device_memory(ze_device_handle_t, uint32_t *count,
                                     ze_device_memory_properties_t *properties) {
  if (count == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (properties == nullptr) {
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

ze_result_t ZE_APICALL device_memory_access(ze_device_handle_t, ze_device_memory_access_properties_t *properties) {
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->hostAllocCapabilities = ZE_MEMORY_ACCESS_CAP_FLAG_RW | ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC;
  properties->deviceAllocCapabilities = properties->hostAllocCapabilities;
  properties->sharedSingleDeviceAllocCapabilities = properties->hostAllocCapabilities;
  properties->sharedCrossDeviceAllocCapabilities = ZE_MEMORY_ACCESS_CAP_FLAG_RW;
  properties->sharedSystemAllocCapabilities = ZE_MEMORY_ACCESS_CAP_FLAG_RW | ZE_MEMORY_ACCESS_CAP_FLAG_ATOMIC;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL device_cache(ze_device_handle_t, uint32_t *count, ze_device_cache_properties_t *properties) {
  if (count == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (properties == nullptr) *count = 1;
  else if (*count != 0) {
    properties[0].flags = ZE_DEVICE_CACHE_PROPERTY_FLAG_USER_CONTROL;
    properties[0].cacheSize = 4 * 1024 * 1024;
    *count = 1;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL device_status(ze_device_handle_t) { return ZE_RESULT_SUCCESS; }
ze_result_t ZE_APICALL device_timestamps(ze_device_handle_t, uint64_t *host, uint64_t *device) {
  if (host == nullptr || device == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto now = std::chrono::steady_clock::now().time_since_epoch();
  *host = static_cast<uint64_t>(std::chrono::duration_cast<std::chrono::nanoseconds>(now).count());
  *device = *host;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL context_create(ze_driver_handle_t, const ze_context_desc_t *, ze_context_handle_t *result) {
  if (result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_context_handle_t>(new Context);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL context_destroy(ze_context_handle_t handle) {
  delete cast<Context>(handle, Kind::Context);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL context_status(ze_context_handle_t) { return ZE_RESULT_SUCCESS; }
ze_result_t ZE_APICALL context_memory(ze_context_handle_t, ze_device_handle_t, void *, size_t) {
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL queue_create(ze_context_handle_t context, ze_device_handle_t device,
                                    const ze_command_queue_desc_t *desc, ze_command_queue_handle_t *result) {
  if (desc == nullptr || result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_command_queue_handle_t>(
      new Queue(cast<Context>(context, Kind::Context), cast<Device>(device, Kind::Device), *desc));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL queue_destroy(ze_command_queue_handle_t handle) {
  delete cast<Queue>(handle, Kind::Queue);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL queue_execute(ze_command_queue_handle_t, uint32_t count,
                                     ze_command_list_handle_t *handles, ze_fence_handle_t fence_handle) {
  if (count != 0 && handles == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  for (uint32_t index = 0; index < count; ++index) {
    auto *list = cast<CommandList>(handles[index], Kind::List);
    if (list == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
    for (auto &command : list->commands) {
      const auto result = command();
      if (result != ZE_RESULT_SUCCESS) return result;
    }
  }
  if (auto *fence = cast<Fence>(fence_handle, Kind::Fence)) signal_event(&fence->event);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL queue_sync(ze_command_queue_handle_t, uint64_t) { return ZE_RESULT_SUCCESS; }

ze_result_t ZE_APICALL list_create(ze_context_handle_t context, ze_device_handle_t device,
                                   const ze_command_list_desc_t *, ze_command_list_handle_t *result) {
  if (result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_command_list_handle_t>(
      new CommandList(false, cast<Context>(context, Kind::Context), cast<Device>(device, Kind::Device)));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL list_create_immediate(ze_context_handle_t context, ze_device_handle_t device,
                                             const ze_command_queue_desc_t *, ze_command_list_handle_t *result) {
  if (result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_command_list_handle_t>(
      new CommandList(true, cast<Context>(context, Kind::Context), cast<Device>(device, Kind::Device)));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL list_destroy(ze_command_list_handle_t handle) {
  delete cast<CommandList>(handle, Kind::List);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL list_close(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (list == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  list->closed = true;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL list_reset(ze_command_list_handle_t handle) {
  auto *list = cast<CommandList>(handle, Kind::List);
  if (list == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  list->commands.clear();
  list->closed = false;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL list_barrier(ze_command_list_handle_t handle, ze_event_handle_t signal,
                                    uint32_t wait_count, ze_event_handle_t *waits) {
  HandleUse<Event> retained_signal;
  EventUses retained_waits;
  const auto retained = retain_command_events(signal, wait_count, waits, retained_signal, retained_waits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  return append(cast<CommandList>(handle, Kind::List), [signal = std::move(retained_signal), waits = std::move(retained_waits)] {
    auto result = wait_dependencies(waits);
    return result == ZE_RESULT_SUCCESS ? signal_event(signal.get()) : result;
  });
}

ze_result_t ZE_APICALL list_copy(ze_command_list_handle_t handle, void *destination, const void *source, size_t size,
                                 ze_event_handle_t signal, uint32_t wait_count, ze_event_handle_t *waits) {
  if ((destination == nullptr || source == nullptr) && size != 0) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  HandleUse<Event> retained_signal;
  EventUses retained_waits;
  const auto retained = retain_command_events(signal, wait_count, waits, retained_signal, retained_waits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto destination_allocation = retain_allocation(destination, size);
  auto source_allocation = retain_allocation(source, size);
  return append(cast<CommandList>(handle, Kind::List),
                [=, signal = std::move(retained_signal), waits = std::move(retained_waits),
                 destination_lifetime = std::move(destination_allocation), source_lifetime = std::move(source_allocation)] {
    auto result = wait_dependencies(waits);
    if (result == ZE_RESULT_SUCCESS) std::memmove(destination, source, size);
    return result == ZE_RESULT_SUCCESS ? signal_event(signal.get()) : result;
  });
}

ze_result_t ZE_APICALL list_fill(ze_command_list_handle_t handle, void *destination, const void *pattern,
                                 size_t pattern_size, size_t size, ze_event_handle_t signal,
                                 uint32_t wait_count, ze_event_handle_t *waits) {
  if (destination == nullptr || pattern == nullptr || pattern_size == 0) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::vector<uint8_t> copied_pattern(static_cast<const uint8_t *>(pattern),
                                      static_cast<const uint8_t *>(pattern) + pattern_size);
  HandleUse<Event> retained_signal;
  EventUses retained_waits;
  const auto retained = retain_command_events(signal, wait_count, waits, retained_signal, retained_waits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  auto destination_allocation = retain_allocation(destination, size);
  return append(cast<CommandList>(handle, Kind::List),
                [=, signal = std::move(retained_signal), waits = std::move(retained_waits),
                 destination_lifetime = std::move(destination_allocation)] {
    auto result = wait_dependencies(waits);
    if (result == ZE_RESULT_SUCCESS) {
      auto *bytes = static_cast<uint8_t *>(destination);
      for (size_t offset = 0; offset < size; ++offset) bytes[offset] = copied_pattern[offset % pattern_size];
    }
    return result == ZE_RESULT_SUCCESS ? signal_event(signal.get()) : result;
  });
}

ze_result_t ZE_APICALL list_signal(ze_command_list_handle_t handle, ze_event_handle_t signal) {
  HandleUse<Event> retained_signal;
  const auto retained = retain_signal(signal, retained_signal);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  return append(cast<CommandList>(handle, Kind::List),
                [signal = std::move(retained_signal)] { return signal_event(signal.get()); });
}
ze_result_t ZE_APICALL list_wait(ze_command_list_handle_t handle, uint32_t count, ze_event_handle_t *events) {
  EventUses retained_events;
  const auto retained = retain_events(count, events, retained_events);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  return append(cast<CommandList>(handle, Kind::List),
                [events = std::move(retained_events)] { return wait_dependencies(events); });
}
ze_result_t ZE_APICALL list_reset_event(ze_command_list_handle_t handle, ze_event_handle_t event_handle) {
  auto *event = cast<Event>(event_handle, Kind::Event);
  if (event == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  return append(cast<CommandList>(handle, Kind::List), [event = HandleUse<Event>(event)] {
    std::lock_guard lock(event->mutex);
    event->signalled = false;
    return ZE_RESULT_SUCCESS;
  });
}

ze_result_t launch_rpc(const KernelLaunch &kernel, const ze_group_count_t &groups) {
  // Mirroring, launch and copy-back form one transaction. Serializing the
  // complete sequence prevents concurrent queues from observing one another's
  // partially mirrored allocation state.
  std::lock_guard rpc_lock(rpc_transaction_mutex);
  auto &state = remote_execution();
  if (state.mode == RemoteMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (state.mode != RemoteMode::Ready || kernel.module->backend != ModuleBackend::RPC)
    return ZE_RESULT_ERROR_UNSUPPORTED_FEATURE;
  if (mirror_allocations_to_server(state) != 0) {
    remember_remote_error(state, remote_error(state));
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }

  std::vector<gemstone_kernel_argument> arguments(kernel.arguments.size());
  for (size_t index = 0; index < kernel.arguments.size(); ++index) {
    auto &output = arguments[index];
    const auto &input = kernel.arguments[index];
    if (input.local) {
      output.kind = GEMSTONE_ARGUMENT_LOCAL;
      output.local_size = input.local_size;
      continue;
    }
    uint64_t allocation_id = 0;
    uint64_t allocation_offset = 0;
    if (classify_pointer_argument(input.bytes, allocation_id, allocation_offset)) {
      output.kind = GEMSTONE_ARGUMENT_ALLOCATION;
      output.allocation_id = allocation_id;
      output.allocation_offset = allocation_offset;
    } else {
      output.kind = GEMSTONE_ARGUMENT_SCALAR;
      output.scalar_bytes = input.bytes.data();
      output.scalar_size = static_cast<uint32_t>(input.bytes.size());
    }
  }

  const gemstone_dimensions group_size{kernel.group_x, kernel.group_y, kernel.group_z};
  const gemstone_dimensions group_count{groups.groupCountX, groups.groupCountY, groups.groupCountZ};
  const auto global_x = static_cast<uint64_t>(group_size.x) * group_count.x;
  const auto global_y = static_cast<uint64_t>(group_size.y) * group_count.y;
  const auto global_z = static_cast<uint64_t>(group_size.z) * group_count.z;
  if (global_x > std::numeric_limits<uint32_t>::max() || global_y > std::numeric_limits<uint32_t>::max()
      || global_z > std::numeric_limits<uint32_t>::max())
    return ZE_RESULT_ERROR_INVALID_GROUP_SIZE_DIMENSION;
  const gemstone_dimensions global_size{static_cast<uint32_t>(global_x), static_cast<uint32_t>(global_y),
                                         static_cast<uint32_t>(global_z)};
  uint64_t completion = 0;
  if (gemstone_execution_launch(state.client, kernel.module->executor_id, kernel.name.c_str(), group_size,
                                 group_count, global_size, 0, 0, arguments.data(), static_cast<uint32_t>(arguments.size()),
                                 &completion) != 0) {
    remember_remote_error(state, remote_error(state));
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }

  int status = 1;
  // A command-list worker must not invent a kernel-duration limit. User-facing
  // event/fence/queue synchronization owns the Level Zero timeout contract;
  // each RPC exchange still retains its independent transport-progress timeout.
  while (status == 1) {
    status = gemstone_execution_query_completion(state.client, completion);
    if (status == 1) std::this_thread::sleep_for(std::chrono::microseconds(100));
  }
  const std::string completion_error = status < 0 ? remote_error(state) : std::string{};
  const int released = gemstone_execution_release_completion(state.client, completion);
  if (status != 0 || released != 0) {
    remember_remote_error(state, completion_error.empty() ? remote_error(state) : completion_error);
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  if (mirror_allocations_from_server(state) != 0) {
    remember_remote_error(state, remote_error(state));
    return ZE_RESULT_ERROR_DEVICE_LOST;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL list_launch(ze_command_list_handle_t handle, ze_kernel_handle_t kernel_handle,
                                   const ze_group_count_t *groups, ze_event_handle_t signal,
                                   uint32_t wait_count, ze_event_handle_t *waits) {
  auto *kernel = cast<Kernel>(kernel_handle, Kind::Kernel);
  if (kernel == nullptr || groups == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const auto copied_groups = *groups;
  if (std::getenv("GEMSTONE_TRACE") != nullptr &&
      static_cast<uint64_t>(kernel->group_x) * copied_groups.groupCountX > 1000)
    std::fprintf(stderr, "Gemstone Level Zero: appending %s with group %ux%ux%u count %ux%ux%u\n",
                 kernel->name.c_str(), kernel->group_x, kernel->group_y, kernel->group_z, copied_groups.groupCountX,
                 copied_groups.groupCountY, copied_groups.groupCountZ);
  HandleUse<Event> retained_signal;
  EventUses retained_waits;
  const auto retained = retain_command_events(signal, wait_count, waits, retained_signal, retained_waits);
  if (retained != ZE_RESULT_SUCCESS) return retained;
  KernelLaunch launch{HandleUse<Kernel>(kernel), kernel->module, kernel->name, kernel->group_x, kernel->group_y,
                      kernel->group_z, kernel->arguments, retain_all_allocations()};
  return append(cast<CommandList>(handle, Kind::List),
                [launch = std::move(launch), copied_groups, signal = std::move(retained_signal),
                 waits = std::move(retained_waits)] {
    auto result = wait_dependencies(waits);
    gemstone_levelzero_executor_v1 current{};
    {
      std::lock_guard lock(executor_mutex);
      current = executor;
    }
    if (result == ZE_RESULT_SUCCESS && current.launch != nullptr && launch.module->backend == ModuleBackend::External) {
      const auto [pointers, sizes] =
          launch.arguments
          ^ map([](const Kernel::Argument &argument) {
              return std::pair<const void *, size_t>{argument.local ? nullptr : argument.bytes.data(),
                                                     argument.local ? argument.local_size : argument.bytes.size()};
            })
          ^ unzip();
      result = current.launch(current.user_data, launch.module->executor_id, launch.name.c_str(),
                              copied_groups.groupCountX, copied_groups.groupCountY, copied_groups.groupCountZ,
                              pointers.data(), sizes.data(), pointers.size()) == 0
                   ? ZE_RESULT_SUCCESS
                   : ZE_RESULT_ERROR_DEVICE_LOST;
    } else if (result == ZE_RESULT_SUCCESS) result = launch_rpc(launch, copied_groups);
    return result == ZE_RESULT_SUCCESS ? signal_event(signal.get()) : result;
  });
}

void *aligned_allocate(size_t size, size_t alignment) {
  const size_t actual_alignment = std::max(alignment, alignof(std::max_align_t));
  void *value = nullptr;
  return posix_memalign(&value, actual_alignment, std::max(size, size_t{1})) == 0 ? value : nullptr;
}

ze_result_t allocate(size_t size, size_t alignment, ze_memory_type_t type, void **result) {
  if (result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  auto &state = remote_execution();
  if (state.mode == RemoteMode::Misconfigured) return ZE_RESULT_ERROR_UNINITIALIZED;
  if (state.mode == RemoteMode::Ready && size > static_cast<size_t>(std::numeric_limits<int32_t>::max()))
    return ZE_RESULT_ERROR_UNSUPPORTED_SIZE;
  *result = aligned_allocate(size, alignment);
  if (*result == nullptr) return ZE_RESULT_ERROR_OUT_OF_HOST_MEMORY;
  const uint64_t allocation_id = next_allocation_id();
  if (state.mode == RemoteMode::Ready) {
    std::lock_guard rpc_lock(rpc_transaction_mutex);
    if (gemstone_execution_register_allocation(state.client, allocation_id, size, rpc_kind(type)) != 0) {
      remember_remote_error(state, remote_error(state));
      std::free(*result);
      *result = nullptr;
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
  }
  std::lock_guard lock(allocations_mutex);
  allocations.emplace(*result, std::make_shared<Allocation>(Allocation{*result, size, type, allocation_id}));
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL mem_shared(ze_context_handle_t, const ze_device_mem_alloc_desc_t *,
                                  const ze_host_mem_alloc_desc_t *, size_t size, size_t alignment,
                                  ze_device_handle_t, void **result) {
  return allocate(size, alignment, ZE_MEMORY_TYPE_SHARED, result);
}
ze_result_t ZE_APICALL mem_device(ze_context_handle_t, const ze_device_mem_alloc_desc_t *, size_t size,
                                  size_t alignment, ze_device_handle_t, void **result) {
  return allocate(size, alignment, ZE_MEMORY_TYPE_DEVICE, result);
}
ze_result_t ZE_APICALL mem_host(ze_context_handle_t, const ze_host_mem_alloc_desc_t *, size_t size,
                                size_t alignment, void **result) {
  return allocate(size, alignment, ZE_MEMORY_TYPE_HOST, result);
}
ze_result_t ZE_APICALL mem_free(ze_context_handle_t, void *pointer) {
  if (pointer == nullptr) return ZE_RESULT_SUCCESS;
  AllocationRef allocation;
  {
    std::lock_guard lock(allocations_mutex);
    const auto found = allocations.find(pointer);
    if (found == allocations.end()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
    if (found->second.use_count() != 1) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
    allocation = found->second;
    allocations.erase(found);
  }
  auto &state = remote_execution();
  std::lock_guard rpc_lock(rpc_transaction_mutex);
  const bool released = state.mode != RemoteMode::Ready ||
                        gemstone_execution_release_allocation(state.client, allocation->id) == 0;
  if (!released) remember_remote_error(state, remote_error(state));
  std::free(pointer);
  return released ? ZE_RESULT_SUCCESS : ZE_RESULT_ERROR_DEVICE_LOST;
}
ze_result_t ZE_APICALL mem_properties(ze_context_handle_t, const void *pointer,
                                      ze_memory_allocation_properties_t *properties, ze_device_handle_t *device) {
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::lock_guard lock(allocations_mutex);
  const auto found = allocations.find(const_cast<void *>(pointer));
  if (found == allocations.end()) return ZE_RESULT_ERROR_INVALID_ARGUMENT;
  properties->type = found->second->type;
  properties->id = found->second->id;
  properties->pageSize = 4096;
  if (device != nullptr) *device = reinterpret_cast<ze_device_handle_t>(&global_device);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL mem_range(ze_context_handle_t, const void *pointer, void **base, size_t *size) {
  if (base == nullptr || size == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  std::lock_guard lock(allocations_mutex);
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  for (const auto &[candidate, allocation] : allocations) {
    const auto start = reinterpret_cast<uintptr_t>(candidate);
    if (address >= start && address < start + allocation->size) {
      *base = candidate;
      *size = allocation->size;
      return ZE_RESULT_SUCCESS;
    }
  }
  return ZE_RESULT_ERROR_INVALID_ARGUMENT;
}

ze_result_t ZE_APICALL event_pool_create(ze_context_handle_t, const ze_event_pool_desc_t *desc, uint32_t,
                                         ze_device_handle_t *, ze_event_pool_handle_t *result) {
  if (desc == nullptr || result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_event_pool_handle_t>(new EventPool(desc->flags));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL event_pool_destroy(ze_event_pool_handle_t handle) {
  delete cast<EventPool>(handle, Kind::EventPool);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL event_create(ze_event_pool_handle_t, const ze_event_desc_t *, ze_event_handle_t *result) {
  if (result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_event_handle_t>(new Event);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL event_destroy(ze_event_handle_t handle) {
  return destroy_handle(cast<Event>(handle, Kind::Event));
}
ze_result_t ZE_APICALL event_signal(ze_event_handle_t handle) { return signal_event(cast<Event>(handle, Kind::Event)); }
ze_result_t ZE_APICALL event_sync(ze_event_handle_t handle, uint64_t timeout) {
  return wait_event(cast<Event>(handle, Kind::Event), timeout);
}
ze_result_t ZE_APICALL event_status(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle, Kind::Event);
  if (event == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(event->mutex);
  return event->signalled ? ZE_RESULT_SUCCESS : ZE_RESULT_NOT_READY;
}
ze_result_t ZE_APICALL event_reset(ze_event_handle_t handle) {
  auto *event = cast<Event>(handle, Kind::Event);
  if (event == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(event->mutex);
  event->signalled = false;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL fence_create(ze_command_queue_handle_t, const ze_fence_desc_t *, ze_fence_handle_t *result) {
  if (result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_fence_handle_t>(new Fence);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL fence_destroy(ze_fence_handle_t handle) { delete cast<Fence>(handle, Kind::Fence); return ZE_RESULT_SUCCESS; }
ze_result_t ZE_APICALL fence_sync(ze_fence_handle_t handle, uint64_t timeout) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  return fence == nullptr ? ZE_RESULT_ERROR_INVALID_NULL_HANDLE : wait_event(&fence->event, timeout);
}
ze_result_t ZE_APICALL fence_status(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  if (fence == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(fence->event.mutex);
  return fence->event.signalled ? ZE_RESULT_SUCCESS : ZE_RESULT_NOT_READY;
}
ze_result_t ZE_APICALL fence_reset(ze_fence_handle_t handle) {
  auto *fence = cast<Fence>(handle, Kind::Fence);
  if (fence == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  std::lock_guard lock(fence->event.mutex);
  fence->event.signalled = false;
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL module_create(ze_context_handle_t, ze_device_handle_t, const ze_module_desc_t *desc,
                                     ze_module_handle_t *result, ze_module_build_log_handle_t *log) {
  if (desc == nullptr || result == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (desc->inputSize != 0 && desc->pInputModule == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (desc->format != ZE_MODULE_FORMAT_IL_SPIRV) return ZE_RESULT_ERROR_UNSUPPORTED_ENUMERATION;
  uint64_t module_id = 0;
  ModuleBackend backend = ModuleBackend::TransferOnly;
  gemstone_levelzero_executor_v1 current{};
  {
    std::lock_guard lock(executor_mutex);
    current = executor;
  }
  if (current.load_spirv != nullptr) {
    if (current.load_spirv(current.user_data, desc->pInputModule, desc->inputSize, &module_id) != 0) {
      if (log != nullptr)
        *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog("Gemstone rejected SPIR-V module"));
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
    backend = ModuleBackend::External;
  } else {
    auto &state = remote_execution();
    if (state.mode == RemoteMode::Misconfigured) {
      if (log != nullptr) *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog(state.error));
      return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
    }
    if (state.mode == RemoteMode::Ready) {
      std::lock_guard rpc_lock(rpc_transaction_mutex);
      if (desc->inputSize > std::numeric_limits<uint32_t>::max() ||
          gemstone_execution_load_program(state.client, GEMSTONE_PROGRAM_SPIRV, desc->pInputModule,
                                           static_cast<uint32_t>(desc->inputSize), &module_id) != 0) {
        remember_remote_error(state, remote_error(state));
        if (log != nullptr) *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog(state.error));
        return ZE_RESULT_ERROR_MODULE_BUILD_FAILURE;
      }
      backend = ModuleBackend::RPC;
    }
  }
  std::vector<uint8_t> bytes;
  if (desc->inputSize != 0) bytes.assign(desc->pInputModule, desc->pInputModule + desc->inputSize);
  *result = reinterpret_cast<ze_module_handle_t>(new Module(std::move(bytes), module_id, backend));
  if (log != nullptr) *log = reinterpret_cast<ze_module_build_log_handle_t>(new BuildLog(""));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL module_destroy(ze_module_handle_t handle) {
  auto *module = cast<Module>(handle, Kind::Module);
  if (module == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (module->users.load(std::memory_order_acquire) != 0) return ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE;
  if (module->backend == ModuleBackend::External) {
    std::lock_guard lock(executor_mutex);
    if (executor.unload != nullptr) executor.unload(executor.user_data, module->executor_id);
  } else if (module->backend == ModuleBackend::RPC) {
    auto &state = remote_execution();
    std::lock_guard rpc_lock(rpc_transaction_mutex);
    if (state.mode == RemoteMode::Ready &&
        gemstone_execution_unload_program(state.client, module->executor_id) != 0) {
      remember_remote_error(state, remote_error(state));
      delete module;
      return ZE_RESULT_ERROR_DEVICE_LOST;
    }
  }
  delete module;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL module_properties(ze_module_handle_t handle, ze_module_properties_t *properties) {
  if (cast<Module>(handle, Kind::Module) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->flags = 0;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL build_log_destroy(ze_module_build_log_handle_t handle) {
  delete cast<BuildLog>(handle, Kind::BuildLog);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL build_log_string(ze_module_build_log_handle_t handle, size_t *size, char *text) {
  auto *log = cast<BuildLog>(handle, Kind::BuildLog);
  if (log == nullptr || size == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const size_t required = log->text.size() + 1;
  if (text == nullptr) *size = required;
  else {
    const size_t copied = std::min(*size, required);
    std::memcpy(text, log->text.c_str(), copied);
    if (copied != 0) text[copied - 1] = '\0';
    *size = copied;
  }
  return ZE_RESULT_SUCCESS;
}

ze_result_t ZE_APICALL kernel_create(ze_module_handle_t module_handle, const ze_kernel_desc_t *desc,
                                     ze_kernel_handle_t *result) {
  auto *module = cast<Module>(module_handle, Kind::Module);
  if (module == nullptr || desc == nullptr || result == nullptr || desc->pKernelName == nullptr)
    return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *result = reinterpret_cast<ze_kernel_handle_t>(new Kernel(module, desc->pKernelName));
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernel_destroy(ze_kernel_handle_t handle) {
  return destroy_handle(cast<Kernel>(handle, Kind::Kernel));
}
ze_result_t ZE_APICALL kernel_group(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (kernel == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  kernel->group_x = x; kernel->group_y = y; kernel->group_z = z;
  return ZE_RESULT_SUCCESS;
}
uint32_t largest_divisor_at_most(uint32_t value, uint32_t limit) {
  for (uint32_t candidate = std::min(value, limit); candidate > 1; --candidate)
    if (value % candidate == 0) return candidate;
  return 1;
}

ze_result_t ZE_APICALL kernel_suggest(ze_kernel_handle_t handle, uint32_t x, uint32_t y, uint32_t z,
                                     uint32_t *gx, uint32_t *gy, uint32_t *gz) {
  if (cast<Kernel>(handle, Kind::Kernel) == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (gx == nullptr || gy == nullptr || gz == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  if (x == 0 || y == 0 || z == 0) return ZE_RESULT_ERROR_INVALID_GLOBAL_WIDTH_DIMENSION;

  // Unified Runtime requires every suggested dimension to divide its global
  // dimension when the SYCL nd_range did not provide a local size. Preserve
  // the driver's x-major preference while staying within the advertised
  // 1024-work-item limit.
  *gx = largest_divisor_at_most(x, 256);
  *gy = largest_divisor_at_most(y, std::min(4U, 1024U / *gx));
  *gz = largest_divisor_at_most(z, std::min(4U, 1024U / (*gx * *gy)));
  if (std::getenv("GEMSTONE_TRACE") != nullptr && x > 256)
    std::fprintf(stderr, "Gemstone Level Zero: suggested %ux%ux%u for global %ux%ux%u\n", *gx, *gy, *gz, x, y,
                 z);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernel_argument(ze_kernel_handle_t handle, uint32_t index, size_t size, const void *value) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (kernel == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (kernel->arguments.size() <= index) kernel->arguments.resize(index + 1);
  const auto *bytes = static_cast<const uint8_t *>(value);
  auto &argument = kernel->arguments[index];
  argument.local = value == nullptr;
  argument.local_size = value == nullptr ? size : 0;
  argument.bytes = value == nullptr ? std::vector<uint8_t>{} : std::vector<uint8_t>(bytes, bytes + size);
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernel_properties(ze_kernel_handle_t handle, ze_kernel_properties_t *properties) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (kernel == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  if (properties == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  properties->numKernelArgs = static_cast<uint32_t>(kernel->arguments.size());
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
  while (extension != nullptr) {
    if (extension->stype == ZE_STRUCTURE_TYPE_KERNEL_PREFERRED_GROUP_SIZE_PROPERTIES) {
      auto *preferred = reinterpret_cast<ze_kernel_preferred_group_size_properties_t *>(extension);
      preferred->preferredMultiple = 8;
    }
    extension = static_cast<ze_base_properties_t *>(extension->pNext);
  }
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernel_set_indirect(ze_kernel_handle_t handle, ze_kernel_indirect_access_flags_t flags) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (kernel == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_HANDLE;
  kernel->indirect = flags;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernel_get_indirect(ze_kernel_handle_t handle, ze_kernel_indirect_access_flags_t *flags) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (kernel == nullptr || flags == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  *flags = kernel->indirect;
  return ZE_RESULT_SUCCESS;
}
ze_result_t ZE_APICALL kernel_name(ze_kernel_handle_t handle, size_t *size, char *name) {
  auto *kernel = cast<Kernel>(handle, Kind::Kernel);
  if (kernel == nullptr || size == nullptr) return ZE_RESULT_ERROR_INVALID_NULL_POINTER;
  const size_t required = kernel->name.size() + 1;
  if (name == nullptr) *size = required;
  else { const auto copied = std::min(*size, required); std::memcpy(name, kernel->name.c_str(), copied); *size = copied; }
  return ZE_RESULT_SUCCESS;
}

template <class T> ze_result_t empty_table(ze_api_version_t version, T *table) {
  const auto checked = check_version(version, table);
  if (checked == ZE_RESULT_SUCCESS) std::memset(table, 0, sizeof(*table));
  return checked;
}

} // namespace

extern "C" ZE_DLLEXPORT void gemstone_levelzero_install_executor(const gemstone_levelzero_executor_v1 *value) {
  std::lock_guard lock(executor_mutex);
  executor = value == nullptr ? gemstone_levelzero_executor_v1{} : *value;
}

#define TABLE_BEGIN(name, type) \
  extern "C" ZE_DLLEXPORT ze_result_t ZE_APICALL name(ze_api_version_t version, type *table) { \
    const auto checked = empty_table(version, table); \
    if (checked != ZE_RESULT_SUCCESS) return checked
#define TABLE_END return ZE_RESULT_SUCCESS; }

TABLE_BEGIN(zeGetGlobalProcAddrTable, ze_global_dditable_t);
  table->pfnInit = init; table->pfnInitDrivers = init_drivers;
TABLE_END
TABLE_BEGIN(zeGetDriverProcAddrTable, ze_driver_dditable_t);
  table->pfnGet = driver_get; table->pfnGetApiVersion = driver_api_version; table->pfnGetProperties = driver_properties;
  table->pfnGetIpcProperties = driver_ipc_properties; table->pfnGetExtensionProperties = driver_extensions;
  table->pfnGetExtensionFunctionAddress = driver_extension_address; table->pfnGetLastErrorDescription = driver_last_error;
TABLE_END
TABLE_BEGIN(zeGetDeviceProcAddrTable, ze_device_dditable_t);
  table->pfnGet = device_get; table->pfnGetSubDevices = device_subdevices; table->pfnGetProperties = device_properties;
  table->pfnGetComputeProperties = device_compute; table->pfnGetModuleProperties = device_module;
  table->pfnGetCommandQueueGroupProperties = device_queue_groups; table->pfnGetMemoryProperties = device_memory;
  table->pfnGetMemoryAccessProperties = device_memory_access; table->pfnGetCacheProperties = device_cache;
  table->pfnGetStatus = device_status; table->pfnGetGlobalTimestamps = device_timestamps;
TABLE_END
TABLE_BEGIN(zeGetContextProcAddrTable, ze_context_dditable_t);
  table->pfnCreate = context_create; table->pfnDestroy = context_destroy; table->pfnGetStatus = context_status;
  table->pfnMakeMemoryResident = context_memory; table->pfnEvictMemory = context_memory;
TABLE_END
TABLE_BEGIN(zeGetCommandQueueProcAddrTable, ze_command_queue_dditable_t);
  table->pfnCreate = queue_create; table->pfnDestroy = queue_destroy; table->pfnExecuteCommandLists = queue_execute;
  table->pfnSynchronize = queue_sync;
TABLE_END
TABLE_BEGIN(zeGetCommandListProcAddrTable, ze_command_list_dditable_t);
  table->pfnCreate = list_create; table->pfnCreateImmediate = list_create_immediate; table->pfnDestroy = list_destroy;
  table->pfnClose = list_close; table->pfnReset = list_reset; table->pfnAppendBarrier = list_barrier;
  table->pfnAppendMemoryCopy = list_copy; table->pfnAppendMemoryFill = list_fill;
  table->pfnAppendSignalEvent = list_signal; table->pfnAppendWaitOnEvents = list_wait;
  table->pfnAppendEventReset = list_reset_event; table->pfnAppendLaunchKernel = list_launch;
  table->pfnAppendLaunchCooperativeKernel = list_launch; table->pfnHostSynchronize = [](ze_command_list_handle_t, uint64_t) { return ZE_RESULT_SUCCESS; };
TABLE_END
TABLE_BEGIN(zeGetMemProcAddrTable, ze_mem_dditable_t);
  table->pfnAllocShared = mem_shared; table->pfnAllocDevice = mem_device; table->pfnAllocHost = mem_host;
  table->pfnFree = mem_free; table->pfnGetAllocProperties = mem_properties; table->pfnGetAddressRange = mem_range;
TABLE_END
TABLE_BEGIN(zeGetEventPoolProcAddrTable, ze_event_pool_dditable_t);
  table->pfnCreate = event_pool_create; table->pfnDestroy = event_pool_destroy;
TABLE_END
TABLE_BEGIN(zeGetEventProcAddrTable, ze_event_dditable_t);
  table->pfnCreate = event_create; table->pfnDestroy = event_destroy; table->pfnHostSignal = event_signal;
  table->pfnHostSynchronize = event_sync; table->pfnQueryStatus = event_status; table->pfnHostReset = event_reset;
TABLE_END
TABLE_BEGIN(zeGetFenceProcAddrTable, ze_fence_dditable_t);
  table->pfnCreate = fence_create; table->pfnDestroy = fence_destroy; table->pfnHostSynchronize = fence_sync;
  table->pfnQueryStatus = fence_status; table->pfnReset = fence_reset;
TABLE_END
TABLE_BEGIN(zeGetModuleProcAddrTable, ze_module_dditable_t);
  table->pfnCreate = module_create; table->pfnDestroy = module_destroy; table->pfnGetProperties = module_properties;
TABLE_END
TABLE_BEGIN(zeGetModuleBuildLogProcAddrTable, ze_module_build_log_dditable_t);
  table->pfnDestroy = build_log_destroy; table->pfnGetString = build_log_string;
TABLE_END
TABLE_BEGIN(zeGetKernelProcAddrTable, ze_kernel_dditable_t);
  table->pfnCreate = kernel_create; table->pfnDestroy = kernel_destroy; table->pfnSetGroupSize = kernel_group;
  table->pfnSuggestGroupSize = kernel_suggest; table->pfnSetArgumentValue = kernel_argument;
  table->pfnSetIndirectAccess = kernel_set_indirect; table->pfnGetIndirectAccess = kernel_get_indirect;
  table->pfnGetProperties = kernel_properties; table->pfnGetName = kernel_name;
TABLE_END

#define EMPTY_TABLE(name, type) TABLE_BEGIN(name, type); TABLE_END
EMPTY_TABLE(zeGetImageProcAddrTable, ze_image_dditable_t)
EMPTY_TABLE(zeGetPhysicalMemProcAddrTable, ze_physical_mem_dditable_t)
EMPTY_TABLE(zeGetSamplerProcAddrTable, ze_sampler_dditable_t)
EMPTY_TABLE(zeGetVirtualMemProcAddrTable, ze_virtual_mem_dditable_t)
