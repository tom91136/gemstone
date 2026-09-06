// SPDX-License-Identifier: Apache-2.0
#include <algorithm>
#include <chrono>
#include <cstdint>
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
#include <utility>
#include <vector>

#include <unistd.h>

#include "gemstone/hsa/aql.h"
#include "gemstone/hsa/aql_execution.h"
#include "gemstone/hsa/code_object_metadata.hpp"
#include "gemstone/hsa/global.h"
#include "gemstone/hsa/target.h"
#include "gemstone/hsa/virtual_kfd.h"
#include "gemstone_execution_rpc.h"
#include <hsa/hsa.h>
#include <hsa/hsa_ext_amd.h>
#include <sys/stat.h>

namespace {

constexpr uint64_t kCpuAgent = 1;
constexpr uint64_t kGpuAgent = 2;
constexpr uint64_t kGpuIsa = 1;
constexpr uint64_t kCpuRegion = 1;
constexpr uint64_t kGpuRegion = 2;
constexpr uint64_t kCpuPool = 1;
constexpr uint64_t kGpuPool = 2;
constexpr uint32_t kQueueMinimum = 1;
constexpr uint32_t kQueueMaximum = 4096;
constexpr uint64_t kVirtualMemorySize = UINT64_C(1) * 1024 * 1024 * 1024;
constexpr uint32_t kAllocationGranule = 4096;
constexpr uint32_t kTransferChunk = 64U * 1024U;

struct Signal {
  uint64_t kind = 0;
  alignas(8) int64_t value = 0;
  uint64_t *doorbell = nullptr;
};
static_assert(offsetof(Signal, value) == 8);

struct Allocation {
  void *base = nullptr;
  size_t size = 0;
  uint64_t id = 0;
  uint64_t pool = 0;
  bool owned = false;
};
using AllocationRef = std::shared_ptr<Allocation>;

struct Reader {
  std::vector<uint8_t> bytes;
};

struct Kernel {
  uint64_t object = 0;
  gemstone::hsa::CodeObjectKernelMetadata metadata;
};
using KernelRef = std::shared_ptr<Kernel>;

struct Global {
  uint64_t object = 0;
  std::string name;
  uint32_t size = 0;
  void *address = nullptr;
};
using GlobalRef = std::shared_ptr<Global>;

struct Executable {
  uint64_t handle = 0;
  bool frozen = false;
  std::vector<KernelRef> kernels;
  std::vector<GlobalRef> globals;
};
using ExecutableRef = std::shared_ptr<Executable>;

struct Queue {
  hsa_queue_t value{};
  gemstone_hsa_virtual_queue *virtual_queue = nullptr;
  gemstone_hsa_virtual_queue_resource resource{};
  void *ring = nullptr;
  size_t ring_bytes = 0;
  std::shared_ptr<Signal> doorbell;
  void (*callback)(hsa_status_t, hsa_queue_t *, void *) = nullptr;
  void *callback_data = nullptr;

  ~Queue() {
    if (virtual_queue != nullptr) gemstone_hsa_virtual_kfd_destroy_queue(virtual_queue);
    std::free(ring);
  }
};
using QueueRef = std::shared_ptr<Queue>;

struct RuntimeState {
  std::mutex lock;
  uint32_t references = 0;
  uint64_t next_allocation = 1;
  uint64_t next_handle = 1;
  uint64_t next_kernel = UINT64_C(0x10000);
  gemstone_execution_client *client = nullptr;
  gemstone_hsa_aql_runtime *aql = nullptr;
  gemstone_hsa_virtual_kfd *kfd = nullptr;
  std::unordered_map<uint64_t, std::shared_ptr<Signal>> signals;
  std::unordered_map<hsa_queue_t *, QueueRef> queues;
  std::unordered_map<void *, AllocationRef> allocations;
  std::unordered_map<uint64_t, AllocationRef> allocation_ids;
  std::unordered_map<uint64_t, std::shared_ptr<Reader>> readers;
  std::unordered_map<uint64_t, ExecutableRef> executables;
  std::unordered_map<uint64_t, KernelRef> kernels;
  std::unordered_map<uint64_t, GlobalRef> globals;
};

int mirror_back(RuntimeState &runtime);

RuntimeState &state() {
  // The public runtime can be reached from DSO finalizers. Keep the state alive
  // until process termination, just as the private queue backend does.
  static RuntimeState *value = new RuntimeState();
  return *value;
}

bool initialized(const RuntimeState &runtime) { return runtime.references != 0 && runtime.client != nullptr && runtime.aql != nullptr; }

hsa_status_t require_initialized(const RuntimeState &runtime) {
  return initialized(runtime) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_NOT_INITIALIZED;
}

bool valid_agent(hsa_agent_t agent) { return agent.handle == kCpuAgent || agent.handle == kGpuAgent; }
bool valid_gpu(hsa_agent_t agent) { return agent.handle == kGpuAgent; }
bool valid_isa(hsa_isa_t isa) { return isa.handle == kGpuIsa; }
bool valid_region(hsa_region_t region) { return region.handle == kCpuRegion || region.handle == kGpuRegion; }
bool valid_pool(hsa_amd_memory_pool_t pool) { return pool.handle == kCpuPool || pool.handle == kGpuPool; }

uint32_t known_global_size(const std::string &name) {
  if (name == "__omp_rtl_device_memory_pool_tracker") return 32;
  if (name == "__omp_rtl_device_memory_pool") return 16;
  if (name == "__omp_rtl_device_environment") return 48;
  if (name == "llvm_omp_target_device_environment") return 48;
  return 0;
}

template <typename T> bool copy_value(void *destination, const T &value) {
  if (destination == nullptr) return false;
  std::memcpy(destination, &value, sizeof(value));
  return true;
}

void copy_name(void *destination, const char *name) {
  std::memset(destination, 0, 64);
  std::memcpy(destination, name, std::min<size_t>(std::strlen(name), 63));
}

// LLVM's AMDGPU offload plugin consumes the HSA ISA name as an LLVM target
// triple, rather than the older human-readable AMD:AMDGPU spelling.
std::string isa_name() { return std::string("amdgcn-amd-amdhsa--") + gemstone::hsa::target_arch(); }

bool trace_api() {
  static const bool enabled = std::getenv("GEMSTONE_HSA_TRACE_API") != nullptr;
  return enabled;
}

void trace_api_call(const char *name, uint64_t first = 0, uint64_t second = 0) {
  if (!trace_api()) return;
  std::fprintf(stderr, "[gemstone-hsa] %s(%llu,%llu)\n", name, static_cast<unsigned long long>(first),
               static_cast<unsigned long long>(second));
}

void trace_api_result(const char *name, hsa_status_t status) {
  if (!trace_api()) return;
  std::fprintf(stderr, "[gemstone-hsa] %s -> %d\n", name, static_cast<int>(status));
}

void trace_api_text(const char *name, const std::string &value) {
  if (!trace_api()) return;
  std::fprintf(stderr, "[gemstone-hsa] %s = %s\n", name, value.c_str());
}

bool is_requested_isa(const char *name) {
  if (name == nullptr) return false;
  const std::string requested(name);
  const std::string target = gemstone::hsa::target_arch();
  return requested == std::string("amdgcn-amd-amdhsa--") + target || requested == std::string("AMD:AMDGPU:") + target
      || requested == std::string("AMD:") + target;
}

std::shared_ptr<Signal> find_signal(RuntimeState &runtime, hsa_signal_t signal) {
  if (signal.handle == 0) return {};
  const auto found = runtime.signals.find(signal.handle);
  return found == runtime.signals.end() ? std::shared_ptr<Signal>{} : found->second;
}

int public_signal_load(void *context, uint64_t handle, int64_t *value) {
  if (context == nullptr || value == nullptr || handle == 0) return -1;
  auto &runtime = *static_cast<RuntimeState *>(context);
  std::shared_ptr<Signal> signal;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    signal = find_signal(runtime, hsa_signal_t{handle});
  }
  if (!signal) return -1;
  *value = __atomic_load_n(&signal->value, __ATOMIC_ACQUIRE);
  return 0;
}

int public_signal_complete(void *context, uint64_t handle) {
  if (context == nullptr || handle == 0) return -1;
  auto &runtime = *static_cast<RuntimeState *>(context);
  std::shared_ptr<Signal> signal;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    signal = find_signal(runtime, hsa_signal_t{handle});
  }
  if (!signal) return -1;
  if (mirror_back(runtime) != 0) {
    const int64_t failure = -3;
    __atomic_store_n(&signal->value, failure, __ATOMIC_RELEASE);
    return 0;
  }
  __atomic_fetch_sub(&signal->value, INT64_C(1), __ATOMIC_RELEASE);
  return 0;
}

int public_signal_fail(void *context, uint64_t handle, int32_t error_code) {
  if (context == nullptr || handle == 0) return -1;
  auto &runtime = *static_cast<RuntimeState *>(context);
  std::shared_ptr<Signal> signal;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    signal = find_signal(runtime, hsa_signal_t{handle});
  }
  if (!signal) return -1;
  const int64_t value = error_code < 0 ? error_code : -static_cast<int64_t>(error_code) - 1;
  __atomic_store_n(&signal->value, value, __ATOMIC_RELEASE);
  return 0;
}

AllocationRef find_allocation(RuntimeState &runtime, const void *pointer, size_t size = 0) {
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  for (const auto &[_, allocation] : runtime.allocations) {
    const auto base = reinterpret_cast<uintptr_t>(allocation->base);
    if (address < base || address - base > allocation->size) continue;
    if (size <= allocation->size - (address - base)) return allocation;
  }
  return {};
}

AllocationRef find_allocation_id(RuntimeState &runtime, uint64_t id) {
  const auto found = runtime.allocation_ids.find(id);
  return found == runtime.allocation_ids.end() ? AllocationRef{} : found->second;
}

int upload(const RuntimeState &runtime, const AllocationRef &allocation, size_t offset, size_t size) {
  const bool trace_all = std::getenv("GEMSTONE_HSA_TRACE_MEMORY_ALL") != nullptr;
  const bool trace = std::getenv("GEMSTONE_HSA_TRACE_MEMORY") != nullptr && (trace_all || allocation->id == 4);
  const size_t start = offset;
  const size_t requested = size;
  if (trace) {
    std::fprintf(stderr, "Gemstone HSA upload begin allocation=%lu offset=%zu size=%zu sample=", allocation->id, offset, size);
    const auto *sample = static_cast<const uint8_t *>(allocation->base) + offset;
    for (size_t index = 0; index < std::min<size_t>(16, size); ++index) std::fprintf(stderr, "%02x", sample[index]);
    std::fprintf(stderr, "\n");
    for (size_t probe : {size_t(1280), size_t(1536), size_t(1552), size_t(1792)})
      if (probe >= offset && probe + 8 <= offset + size) {
        std::fprintf(stderr, "Gemstone HSA upload probe allocation=%lu offset=%zu bytes=", allocation->id, probe);
        for (size_t index = 0; index < 8; ++index)
          std::fprintf(stderr, "%02x", static_cast<const uint8_t *>(allocation->base)[probe + index]);
        std::fprintf(stderr, "\n");
      }
  }
  auto *bytes = static_cast<const uint8_t *>(allocation->base) + offset;
  while (size != 0) {
    const uint32_t chunk = static_cast<uint32_t>(std::min<size_t>(size, kTransferChunk));
    if (gemstone_execution_write_allocation(runtime.client, allocation->id, offset, bytes, chunk) != 0) return -1;
    offset += chunk;
    bytes += chunk;
    size -= chunk;
  }
  if (trace) std::fprintf(stderr, "Gemstone HSA upload end allocation=%lu offset=%zu size=%zu\n", allocation->id, start, requested);
  return 0;
}

int download(const RuntimeState &runtime, const AllocationRef &allocation, size_t offset, size_t size) {
  const bool trace_all = std::getenv("GEMSTONE_HSA_TRACE_MEMORY_ALL") != nullptr;
  const bool trace = std::getenv("GEMSTONE_HSA_TRACE_MEMORY") != nullptr && (trace_all || allocation->id == 4);
  const size_t start = offset;
  const size_t requested = size;
  if (trace)
    std::fprintf(stderr, "Gemstone HSA download begin allocation=%lu offset=%zu size=%zu\n", allocation->id, offset, size);
  auto *bytes = static_cast<uint8_t *>(allocation->base) + offset;
  while (size != 0) {
    const uint32_t chunk = static_cast<uint32_t>(std::min<size_t>(size, kTransferChunk));
    if (gemstone_execution_read_allocation(runtime.client, allocation->id, offset, bytes, chunk) != 0) return -1;
    offset += chunk;
    bytes += chunk;
    size -= chunk;
  }
  if (trace) {
    const auto *sample = static_cast<const uint8_t *>(allocation->base) + start;
    std::fprintf(stderr, "Gemstone HSA download end allocation=%lu sample=", allocation->id);
    for (size_t index = 0; index < std::min<size_t>(16, requested); ++index) std::fprintf(stderr, "%02x", sample[index]);
    std::fprintf(stderr, "\n");
  }
  return 0;
}

int upload_dispatch_allocations(RuntimeState &runtime, const void *packet, size_t packet_size) {
  uint32_t count = 0;
  if (gemstone_hsa_aql_runtime_dispatch_allocations(runtime.aql, packet, packet_size, nullptr, 0, &count) != 0) {
    // Let the AQL executor report malformed and unsupported packets through
    // their original completion signal. A valid dispatch that cannot resolve
    // its kernel or allocations must fail before stale host memory is used.
    gemstone_hsa_aql_packet decoded{};
    const auto decode = gemstone_hsa_decode_aql_packet(packet, packet_size, &decoded);
    return decode != GEMSTONE_HSA_AQL_DECODE_SUCCESS || decoded.type != GEMSTONE_HSA_AQL_KERNEL_DISPATCH ? 0 : -1;
  }
  std::vector<uint64_t> ids(count);
  if (count != 0 && gemstone_hsa_aql_runtime_dispatch_allocations(runtime.aql, packet, packet_size, ids.data(), count, &count) != 0) {
    return -1;
  }
  std::vector<AllocationRef> allocations;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    allocations.reserve(ids.size());
    for (uint64_t id : ids) {
      auto allocation = find_allocation_id(runtime, id);
      if (!allocation) {
        return -1;
      }
      allocations.push_back(std::move(allocation));
    }
  }
  for (const auto &allocation : allocations) {
    if (std::getenv("GEMSTONE_HSA_TRACE_MEMORY_ALL") != nullptr ||
        (std::getenv("GEMSTONE_HSA_TRACE_MEMORY") != nullptr && allocation->id == 4))
      std::fprintf(stderr, "Gemstone HSA dispatch upload allocation=%lu size=%zu\n", allocation->id, allocation->size);
    if (upload(runtime, allocation, 0, allocation->size) != 0) {
      return -1;
    }
  }
  return 0;
}

int mirror_back(RuntimeState &runtime) {
  uint32_t count = 0;
  if (gemstone_hsa_aql_runtime_take_dirty_ranges(runtime.aql, nullptr, 0, &count) != 0) return -1;
  if (std::getenv("GEMSTONE_HSA_TRACE_MEMORY") != nullptr)
    std::fprintf(stderr, "Gemstone HSA mirror query count=%u\n", count);
  std::vector<gemstone_hsa_dirty_range> ranges(count);
  if (count != 0 && gemstone_hsa_aql_runtime_take_dirty_ranges(runtime.aql, ranges.data(), count, &count) != 0) return -1;
  for (const auto &range : ranges) {
    AllocationRef allocation;
    {
      std::lock_guard<std::mutex> guard(runtime.lock);
      allocation = find_allocation_id(runtime, range.allocation_id);
    }
    if (!allocation || range.offset > allocation->size || range.size > allocation->size - range.offset) return -1;
    if (std::getenv("GEMSTONE_HSA_TRACE_MEMORY_ALL") != nullptr || std::getenv("GEMSTONE_HSA_TRACE_MEMORY") != nullptr)
      std::fprintf(stderr, "Gemstone HSA mirror range allocation=%lu offset=%lu size=%u\n", range.allocation_id, range.offset, range.size);
    auto *bytes = static_cast<uint8_t *>(allocation->base) + range.offset;
    size_t offset = range.offset;
    size_t remaining = range.size;
    while (remaining != 0) {
      const uint32_t chunk = static_cast<uint32_t>(std::min<size_t>(remaining, kTransferChunk));
      if (gemstone_execution_read_allocation(runtime.client, allocation->id, offset, bytes, chunk) != 0) return -1;
      offset += chunk;
      bytes += chunk;
      remaining -= chunk;
    }
    if (std::getenv("GEMSTONE_HSA_TRACE_MEMORY") != nullptr && allocation->id == 4)
      for (size_t probe : {size_t(1280), size_t(1536), size_t(1552), size_t(1792)})
        if (probe >= range.offset && probe + 8 <= range.offset + range.size) {
          std::fprintf(stderr, "Gemstone HSA mirror probe allocation=%lu offset=%zu bytes=", allocation->id, probe);
          for (size_t index = 0; index < 8; ++index)
            std::fprintf(stderr, "%02x", static_cast<const uint8_t *>(allocation->base)[probe + index]);
          std::fprintf(stderr, "\n");
        }
  }
  return 0;
}

void fail_packet(RuntimeState &runtime, const void *packet, size_t packet_size, int32_t code) {
  gemstone_hsa_aql_packet decoded{};
  if (gemstone_hsa_decode_aql_packet(packet, packet_size, &decoded) != GEMSTONE_HSA_AQL_DECODE_SUCCESS) return;
  uint64_t signal = 0;
  if (decoded.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH) signal = decoded.value.kernel_dispatch.completion_signal;
  else if (decoded.type == GEMSTONE_HSA_AQL_BARRIER_AND || decoded.type == GEMSTONE_HSA_AQL_BARRIER_OR)
    signal = decoded.value.barrier.completion_signal;
  else if (decoded.type == GEMSTONE_HSA_AQL_VENDOR_SPECIFIC) signal = decoded.value.pm4_ib.completion_signal;
  if (signal != 0) public_signal_fail(&runtime, signal, code);
}

gemstone_hsa_packet_result consume(uint64_t queue_id, uint64_t packet_id, const void *packet, size_t packet_size, void *opaque) {
  auto &runtime = *static_cast<RuntimeState *>(opaque);
  // A queue producer may recycle a ring slot as soon as it observes the
  // consumer's read index.  Keep one immutable packet image for the whole
  // preflight/execute/failure path; otherwise a concurrent header update can
  // make the two decodes disagree (for example, a published dispatch being
  // observed as an unsupported transient type).
  if (packet == nullptr || packet_size == 0) {
    fail_packet(runtime, packet, packet_size, -6);
    return GEMSTONE_HSA_PACKET_ACCEPTED;
  }
  std::vector<uint8_t> stable_packet(packet_size);
  std::memcpy(stable_packet.data(), packet, packet_size);
  const void *stable = stable_packet.data();
  if (upload_dispatch_allocations(runtime, stable, packet_size) != 0) {
    fail_packet(runtime, stable, packet_size, -3);
    return GEMSTONE_HSA_PACKET_ACCEPTED;
  }
  const auto result = gemstone_hsa_aql_runtime_consume(queue_id, packet_id, stable, packet_size, runtime.aql);
  return result;
}

bool signal_condition(int64_t value, hsa_signal_condition_t condition, int64_t compare) {
  switch (condition) {
    case HSA_SIGNAL_CONDITION_EQ: return value == compare;
    case HSA_SIGNAL_CONDITION_NE: return value != compare;
    case HSA_SIGNAL_CONDITION_LT: return value < compare;
    case HSA_SIGNAL_CONDITION_GTE: return value >= compare;
  }
  return false;
}

hsa_signal_value_t wait_signal(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare, uint64_t timeout_hint) {
  auto &runtime = state();
  std::shared_ptr<Signal> value;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    value = find_signal(runtime, signal);
  }
  if (!value) return 0;
  const auto start = std::chrono::steady_clock::now();
  for (;;) {
    const int64_t current = __atomic_load_n(&value->value, __ATOMIC_ACQUIRE);
    if (signal_condition(current, condition, compare)) return current;
    if (timeout_hint != UINT64_MAX) {
      const auto elapsed = std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now() - start);
      if (elapsed.count() >= 0 && static_cast<uint64_t>(elapsed.count()) >= timeout_hint) return current;
    }
    std::this_thread::yield();
  }
}

int complete_signal(hsa_signal_t signal) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = find_signal(runtime, signal);
  if (!value) return -1;
  __atomic_fetch_sub(&value->value, INT64_C(1), __ATOMIC_RELEASE);
  return 0;
}

hsa_status_t allocate(RuntimeState &runtime, uint64_t pool, size_t requested, void **result, bool owned) {
  if (result == nullptr || requested == 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (requested > std::numeric_limits<size_t>::max() - (kAllocationGranule - 1U)) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  const size_t rounded = (requested + kAllocationGranule - 1U) & ~(size_t(kAllocationGranule) - 1U);
  // ROCr allocations are page-granular and several vendor kernels use the
  // following granule for private algorithmic scratch even when the logical
  // user range ends exactly at the previous page boundary.
  if (rounded > std::numeric_limits<size_t>::max() - 2U * kAllocationGranule)
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  // Keep two trailing granules. Vendor algorithm kernels commonly perform a
  // masked vector load at the first address beyond a logical allocation; the
  // hardware allocator's page-granular mapping makes that harmless, while
  // the emulator's checked view needs the same padding explicitly.
  const size_t size = rounded + 2U * kAllocationGranule;
  void *memory = nullptr;
  if (posix_memalign(&memory, kAllocationGranule, size) != 0 || memory == nullptr) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  std::memset(memory, 0, size);

  uint64_t id = 0;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    id = runtime.next_allocation++;
  }
  if (gemstone_execution_register_allocation(runtime.client, id, size, GEMSTONE_ALLOCATION_SHARED) != 0) {
    std::free(memory);
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  if (gemstone_hsa_aql_runtime_register_allocation(runtime.aql, reinterpret_cast<uint64_t>(memory), size, id) != 0) {
    gemstone_execution_release_allocation(runtime.client, id);
    std::free(memory);
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  try {
    auto allocation = std::make_shared<Allocation>(Allocation{memory, size, id, pool, owned});
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.allocations.emplace(memory, allocation);
    runtime.allocation_ids.emplace(id, allocation);
    *result = memory;
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    gemstone_hsa_aql_runtime_unregister_allocation(runtime.aql, reinterpret_cast<uint64_t>(memory));
    gemstone_execution_release_allocation(runtime.client, id);
    std::free(memory);
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

hsa_status_t free_allocation(RuntimeState &runtime, void *pointer, bool require_owned) {
  if (pointer == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  AllocationRef allocation;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.allocations.find(pointer);
    if (found == runtime.allocations.end() || (require_owned && !found->second->owned)) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    allocation = found->second;
    runtime.allocations.erase(found);
    runtime.allocation_ids.erase(allocation->id);
  }
  gemstone_hsa_aql_runtime_unregister_allocation(runtime.aql, reinterpret_cast<uint64_t>(allocation->base));
  const int released = gemstone_execution_release_allocation(runtime.client, allocation->id);
  if (allocation->owned) std::free(allocation->base);
  return released == 0 ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR;
}

bool valid_power_of_two(uint32_t value) { return value >= kQueueMinimum && value <= kQueueMaximum && (value & (value - 1U)) == 0; }

std::shared_ptr<Reader> reader(RuntimeState &runtime, hsa_code_object_reader_t handle) {
  const auto found = runtime.readers.find(handle.handle);
  return found == runtime.readers.end() ? std::shared_ptr<Reader>{} : found->second;
}

ExecutableRef executable(RuntimeState &runtime, hsa_executable_t handle) {
  const auto found = runtime.executables.find(handle.handle);
  return found == runtime.executables.end() ? ExecutableRef{} : found->second;
}

} // namespace

extern "C" hsa_status_t hsa_status_string(hsa_status_t status, const char **status_string) {
  if (status_string == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  switch (status) {
    case HSA_STATUS_SUCCESS: *status_string = "success"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_INFO_BREAK: *status_string = "information break"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_NOT_INITIALIZED: *status_string = "runtime not initialized"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_ARGUMENT: *status_string = "invalid argument"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_AGENT: *status_string = "invalid agent"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_REGION: *status_string = "invalid region"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_SIGNAL: *status_string = "invalid signal"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_QUEUE: *status_string = "invalid queue"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_EXECUTABLE: *status_string = "invalid executable"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER: *status_string = "invalid code object reader"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INVALID_SYMBOL_NAME: *status_string = "invalid symbol name"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_FROZEN_EXECUTABLE: *status_string = "frozen executable"; return HSA_STATUS_SUCCESS;
    case HSA_STATUS_ERROR_INCOMPATIBLE_ARGUMENTS: *status_string = "incompatible arguments"; return HSA_STATUS_SUCCESS;
    default: *status_string = "HSA error"; return HSA_STATUS_SUCCESS;
  }
}

extern "C" hsa_status_t hsa_system_get_info(hsa_system_info_t attribute, void *value) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (value == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  switch (attribute) {
    case HSA_SYSTEM_INFO_VERSION_MAJOR: return copy_value(value, uint16_t(1)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_SYSTEM_INFO_VERSION_MINOR: return copy_value(value, uint16_t(0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_SYSTEM_INFO_TIMESTAMP: {
      const auto now = std::chrono::duration_cast<std::chrono::microseconds>(
          std::chrono::steady_clock::now().time_since_epoch()).count();
      return copy_value(value, static_cast<uint64_t>(now)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    }
    case HSA_SYSTEM_INFO_TIMESTAMP_FREQUENCY: return copy_value(value, uint64_t(1000000)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_SYSTEM_INFO_SIGNAL_MAX_WAIT: return copy_value(value, UINT64_MAX) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_SYSTEM_INFO_ENDIANNESS:
      return copy_value(value, HSA_ENDIANNESS_LITTLE) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_SYSTEM_INFO_MACHINE_MODEL:
      return copy_value(value, HSA_MACHINE_MODEL_LARGE) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_SYSTEM_INFO_EXTENSIONS:
      std::memset(value, 0, 128);
      return HSA_STATUS_SUCCESS;
    case HSA_AMD_SYSTEM_INFO_SVM_SUPPORTED:
      return copy_value(value, true) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_SYSTEM_INFO_SVM_ACCESSIBLE_BY_DEFAULT:
      return copy_value(value, true) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_SYSTEM_INFO_EXT_VERSION_MAJOR:
      return copy_value(value, uint16_t(HSA_AMD_INTERFACE_VERSION_MAJOR)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_SYSTEM_INFO_EXT_VERSION_MINOR:
      return copy_value(value, uint16_t(HSA_AMD_INTERFACE_VERSION_MINOR)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_init() {
  trace_api_call("hsa_init");
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (runtime.references == std::numeric_limits<uint32_t>::max()) return HSA_STATUS_ERROR_REFCOUNT_OVERFLOW;
  if (runtime.references++ != 0) return HSA_STATUS_SUCCESS;
  runtime.client = gemstone_execution_client_create_from_environment();
  const gemstone_hsa_signal_operations signals{&runtime, public_signal_load, public_signal_complete, public_signal_fail};
  runtime.aql = gemstone_hsa_aql_runtime_create_from_environment(&signals);
  if (runtime.client == nullptr || runtime.aql == nullptr || gemstone_execution_attest(runtime.client) != 0) {
    if (runtime.aql != nullptr) gemstone_hsa_aql_runtime_destroy(runtime.aql);
    if (runtime.client != nullptr) gemstone_execution_client_destroy(runtime.client);
    runtime.aql = nullptr;
    runtime.client = nullptr;
    runtime.references = 0;
    trace_api_result("hsa_init", HSA_STATUS_ERROR_NOT_INITIALIZED);
    return HSA_STATUS_ERROR_NOT_INITIALIZED;
  }
  runtime.kfd = gemstone_hsa_virtual_kfd_create(128);
  if (runtime.kfd == nullptr) {
    gemstone_hsa_aql_runtime_destroy(runtime.aql);
    gemstone_execution_client_destroy(runtime.client);
    runtime.aql = nullptr;
    runtime.client = nullptr;
    runtime.references = 0;
    trace_api_result("hsa_init", HSA_STATUS_ERROR_OUT_OF_RESOURCES);
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  trace_api_result("hsa_init", HSA_STATUS_SUCCESS);
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_shut_down() {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (runtime.references == 0) return HSA_STATUS_ERROR_NOT_INITIALIZED;
  if (runtime.references > 1) {
    --runtime.references;
    return HSA_STATUS_SUCCESS;
  }
  if (!runtime.queues.empty() || !runtime.allocations.empty() || !runtime.signals.empty() || !runtime.readers.empty()
      || !runtime.executables.empty())
    return HSA_STATUS_ERROR_INVALID_RUNTIME_STATE;
  gemstone_hsa_virtual_kfd_destroy(runtime.kfd);
  gemstone_hsa_aql_runtime_destroy(runtime.aql);
  gemstone_execution_client_destroy(runtime.client);
  runtime.kfd = nullptr;
  runtime.aql = nullptr;
  runtime.client = nullptr;
  runtime.references = 0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_iterate_agents(hsa_status_t (*callback)(hsa_agent_t, void *), void *data) {
  trace_api_call("hsa_iterate_agents");
  auto &runtime = state();
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
    if (callback == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
  const auto cpu = callback(hsa_agent_t{kCpuAgent}, data);
  trace_api_result("hsa_iterate_agents.cpu", cpu);
  if (cpu != HSA_STATUS_SUCCESS) return cpu;
  const auto gpu = callback(hsa_agent_t{kGpuAgent}, data);
  trace_api_result("hsa_iterate_agents.gpu", gpu);
  return gpu;
}

extern "C" hsa_status_t hsa_agent_get_info(hsa_agent_t agent, hsa_agent_info_t attribute, void *value) {
  trace_api_call("hsa_agent_get_info", agent.handle, attribute);
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_agent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (value == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const bool gpu = valid_gpu(agent);
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_PRODUCT_NAME)) {
    copy_name(value, gpu ? gemstone::hsa::target_arch() : "Gemstone CPU");
    return HSA_STATUS_SUCCESS;
  }
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_TIMESTAMP_FREQUENCY))
    return copy_value(value, uint64_t(1000000000)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COMPUTE_UNIT_COUNT))
    return copy_value(value, uint32_t(gpu ? 1 : 0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MAX_WAVES_PER_CU))
    return copy_value(value, uint32_t(gpu ? 10 : 0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_NUM_SDMA_ENG))
    return copy_value(value, uint32_t(gpu ? 1 : 0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (attribute == static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_NUM_SDMA_XGMI_ENG))
    return copy_value(value, uint32_t(0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
  switch (attribute) {
    case HSA_AGENT_INFO_NAME:
      copy_name(value, gpu ? gemstone::hsa::target_arch() : "host");
      trace_api_text("hsa_agent_get_info.name", gpu ? gemstone::hsa::target_arch() : "host");
      return HSA_STATUS_SUCCESS;
    case HSA_AGENT_INFO_VENDOR_NAME: copy_name(value, "Gemstone"); return HSA_STATUS_SUCCESS;
    case HSA_AGENT_INFO_FEATURE:
      return copy_value(value, gpu ? HSA_AGENT_FEATURE_KERNEL_DISPATCH : hsa_agent_feature_t(0)) ? HSA_STATUS_SUCCESS
                                                                                                 : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_MACHINE_MODEL:
      return copy_value(value, HSA_MACHINE_MODEL_LARGE) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_PROFILE:
      return copy_value(value, gpu ? HSA_PROFILE_FULL : HSA_PROFILE_BASE) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_DEFAULT_FLOAT_ROUNDING_MODE:
      return copy_value(value, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_WAVEFRONT_SIZE:
      return copy_value(value, uint32_t(gpu ? 64 : 1)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_WORKGROUP_MAX_DIM: {
      const uint16_t dimensions[3] = {1024, 1024, 1024};
      std::memcpy(value, dimensions, sizeof(dimensions));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_AGENT_INFO_WORKGROUP_MAX_SIZE:
      return copy_value(value, uint32_t(gpu ? 1024 : 1)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_GRID_MAX_DIM:
      return copy_value(value, hsa_dim3_t{UINT32_MAX, UINT32_MAX, UINT32_MAX}) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_GRID_MAX_SIZE:
      return copy_value(value, uint32_t(UINT32_MAX)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_FBARRIER_MAX_SIZE:
      return copy_value(value, uint32_t(gpu ? 32 : 0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_QUEUES_MAX:
      return copy_value(value, uint32_t(gpu ? 128 : 0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_QUEUE_MIN_SIZE: return copy_value(value, uint32_t(1)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_QUEUE_MAX_SIZE:
      return copy_value(value, uint32_t(gpu ? kQueueMaximum : 0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_QUEUE_TYPE:
      return copy_value(value, hsa_queue_type32_t(HSA_QUEUE_TYPE_MULTI)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_NODE: return copy_value(value, uint32_t(gpu ? 1 : 0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_DEVICE:
      return copy_value(value, gpu ? HSA_DEVICE_TYPE_GPU : HSA_DEVICE_TYPE_CPU) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_CACHE_SIZE: {
      const uint32_t cache[4] = {gpu ? 16U * 1024U : 32U * 1024U, 0, 0, 0};
      std::memcpy(value, cache, sizeof(cache));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_AGENT_INFO_ISA:
      return copy_value(value, hsa_isa_t{uint64_t(gpu ? kGpuIsa : 0)}) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_EXTENSIONS: std::memset(value, 0, 128); return HSA_STATUS_SUCCESS;
    case HSA_AGENT_INFO_VERSION_MAJOR: return copy_value(value, uint16_t(1)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AGENT_INFO_VERSION_MINOR: return copy_value(value, uint16_t(0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_isa_from_name(const char *name, hsa_isa_t *isa) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (name == nullptr || isa == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (!is_requested_isa(name)) return HSA_STATUS_ERROR_INVALID_ISA_NAME;
  *isa = hsa_isa_t{kGpuIsa};
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_agent_iterate_isas(hsa_agent_t agent, hsa_status_t (*callback)(hsa_isa_t, void *), void *data) {
  trace_api_call("hsa_agent_iterate_isas", agent.handle);
  auto &runtime = state();
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
    if (!valid_agent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
    if (callback == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
  const auto status = valid_gpu(agent) ? callback(hsa_isa_t{kGpuIsa}, data) : HSA_STATUS_SUCCESS;
  trace_api_result("hsa_agent_iterate_isas", status);
  return status;
}

extern "C" hsa_status_t hsa_isa_get_info_alt(hsa_isa_t isa, hsa_isa_info_t attribute, void *value) {
  trace_api_call("hsa_isa_get_info_alt", isa.handle, attribute);
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_isa(isa)) return HSA_STATUS_ERROR_INVALID_ISA;
  if (value == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const std::string name = isa_name();
  switch (attribute) {
    case HSA_ISA_INFO_NAME_LENGTH:
      return copy_value(value, static_cast<uint32_t>(name.size())) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_ISA_INFO_NAME:
      std::memcpy(value, name.data(), name.size());
      trace_api_text("hsa_isa_get_info_alt.name", name);
      return HSA_STATUS_SUCCESS;
    case HSA_ISA_INFO_CALL_CONVENTION_COUNT: return copy_value(value, uint32_t(1)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_ISA_INFO_CALL_CONVENTION_INFO_WAVEFRONT_SIZE:
      return copy_value(value, uint32_t(64)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_ISA_INFO_CALL_CONVENTION_INFO_WAVEFRONTS_PER_COMPUTE_UNIT:
      return copy_value(value, uint32_t(10)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
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
    case HSA_ISA_INFO_FAST_F16_OPERATION: return copy_value(value, true) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_ISA_INFO_WORKGROUP_MAX_DIM: {
      const uint16_t dimensions[3] = {1024, 1024, 1024};
      std::memcpy(value, dimensions, sizeof(dimensions));
      return HSA_STATUS_SUCCESS;
    }
    case HSA_ISA_INFO_WORKGROUP_MAX_SIZE: return copy_value(value, uint32_t(1024)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_ISA_INFO_GRID_MAX_DIM:
      return copy_value(value, hsa_dim3_t{UINT32_MAX, UINT32_MAX, UINT32_MAX}) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_ISA_INFO_GRID_MAX_SIZE:
      return copy_value(value, uint64_t(UINT64_MAX)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_ISA_INFO_FBARRIER_MAX_SIZE: return copy_value(value, uint32_t(32)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
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

extern "C" hsa_status_t hsa_isa_compatible(hsa_isa_t code_object_isa, hsa_isa_t agent_isa, bool *result) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_isa(code_object_isa) || !valid_isa(agent_isa)) return HSA_STATUS_ERROR_INVALID_ISA;
  if (result == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  *result = code_object_isa.handle == agent_isa.handle;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_agent_iterate_regions(hsa_agent_t agent, hsa_status_t (*callback)(hsa_region_t, void *), void *data) {
  trace_api_call("hsa_agent_iterate_regions", agent.handle);
  auto &runtime = state();
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
    if (!valid_agent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
    if (callback == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
  const auto region = hsa_region_t{agent.handle == kCpuAgent ? kCpuRegion : kGpuRegion};
  const auto status = callback(region, data);
  trace_api_result("hsa_agent_iterate_regions", status);
  return status;
}

extern "C" hsa_status_t hsa_region_get_info(hsa_region_t region, hsa_region_info_t attribute, void *value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_region(region)) return HSA_STATUS_ERROR_INVALID_REGION;
  if (value == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const uint32_t flags = HSA_REGION_GLOBAL_FLAG_KERNARG | HSA_REGION_GLOBAL_FLAG_FINE_GRAINED;
  switch (attribute) {
    case HSA_REGION_INFO_SEGMENT:
      return copy_value(value, HSA_REGION_SEGMENT_GLOBAL) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_REGION_INFO_GLOBAL_FLAGS: return copy_value(value, flags) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_REGION_INFO_SIZE:
      return copy_value(value, size_t(kVirtualMemorySize)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_REGION_INFO_ALLOC_MAX_SIZE:
      return copy_value(value, size_t(kVirtualMemorySize)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_REGION_INFO_RUNTIME_ALLOC_ALLOWED: return copy_value(value, true) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_REGION_INFO_RUNTIME_ALLOC_GRANULE:
      return copy_value(value, size_t(kAllocationGranule)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_REGION_INFO_RUNTIME_ALLOC_ALIGNMENT:
      return copy_value(value, size_t(kAllocationGranule)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_memory_allocate(hsa_region_t region, size_t size, void **ptr) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_region(region)) return HSA_STATUS_ERROR_INVALID_REGION;
  return allocate(runtime, region.handle == kCpuRegion ? kCpuPool : kGpuPool, size, ptr, true);
}

extern "C" hsa_status_t hsa_memory_free(void *ptr) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  return free_allocation(runtime, ptr, true);
}

extern "C" hsa_status_t hsa_memory_copy(void *dst, const void *src, size_t size) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if ((dst == nullptr || src == nullptr) && size != 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (size == 0) return HSA_STATUS_SUCCESS;
  AllocationRef destination;
  AllocationRef source_allocation;
  size_t source_offset = 0;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    destination = find_allocation(runtime, dst, size);
    source_allocation = find_allocation(runtime, src, size);
    if (source_allocation == nullptr && destination == nullptr) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    if (source_allocation != nullptr)
      source_offset = reinterpret_cast<uintptr_t>(src) - reinterpret_cast<uintptr_t>(source_allocation->base);
  }
  // GPU-pool allocations are remote-authoritative after a dispatch.  Pull the
  // source range back before exposing it through the ordinary host pointer;
  // otherwise DtoH and DtoD copies observe stale malloc backing storage.
  if (source_allocation != nullptr && download(runtime, source_allocation, source_offset, size) != 0)
    return HSA_STATUS_ERROR;
  std::memmove(dst, src, size);
  if (destination) {
    if (upload(runtime, destination, reinterpret_cast<uintptr_t>(dst) - reinterpret_cast<uintptr_t>(destination->base), size) != 0)
      return HSA_STATUS_ERROR;
  }
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_memory_register(void *ptr, size_t size) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (ptr == nullptr || size == 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (find_allocation(runtime, ptr, size) != nullptr) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
  }
  uint64_t id = 0;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    id = runtime.next_allocation++;
  }
  if (gemstone_execution_register_allocation(runtime.client, id, size, GEMSTONE_ALLOCATION_SHARED) != 0)
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  if (gemstone_hsa_aql_runtime_register_allocation(runtime.aql, reinterpret_cast<uint64_t>(ptr), size, id) != 0) {
    gemstone_execution_release_allocation(runtime.client, id);
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  try {
    auto allocation = std::make_shared<Allocation>(Allocation{ptr, size, id, kCpuPool, false});
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.allocations.emplace(ptr, allocation);
    runtime.allocation_ids.emplace(id, allocation);
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    gemstone_hsa_aql_runtime_unregister_allocation(runtime.aql, reinterpret_cast<uint64_t>(ptr));
    gemstone_execution_release_allocation(runtime.client, id);
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_memory_deregister(void *ptr, size_t size) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.allocations.find(ptr);
    if (found == runtime.allocations.end() || found->second->owned || found->second->size < size)
      return HSA_STATUS_ERROR_INVALID_ALLOCATION;
  }
  return free_allocation(runtime, ptr, false);
}

extern "C" hsa_status_t hsa_memory_assign_agent(void *ptr, hsa_agent_t agent, hsa_access_permission_t access) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_agent(agent) || ptr == nullptr || access > HSA_ACCESS_PERMISSION_RW) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  return find_allocation(runtime, ptr) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ALLOCATION;
}

extern "C" hsa_status_t hsa_signal_create(hsa_signal_value_t initial_value, uint32_t num_consumers, const hsa_agent_t *consumers,
                                          hsa_signal_t *signal) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (signal == nullptr || (num_consumers != 0 && consumers == nullptr)) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  for (uint32_t index = 0; index < num_consumers; ++index)
    if (!valid_agent(consumers[index])) return HSA_STATUS_ERROR_INVALID_AGENT;
  try {
    auto value = std::make_shared<Signal>();
    value->value = initial_value;
    std::lock_guard<std::mutex> guard(runtime.lock);
    const uint64_t handle = runtime.next_handle++;
    runtime.signals.emplace(handle, value);
    signal->handle = handle;
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_amd_signal_create(hsa_signal_value_t initial_value, uint32_t num_consumers,
                                               const hsa_agent_t *consumers, uint64_t, hsa_signal_t *signal) {
  return hsa_signal_create(initial_value, num_consumers, consumers, signal);
}

extern "C" hsa_status_t hsa_signal_destroy(hsa_signal_t signal) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.signals.find(signal.handle);
  if (found == runtime.signals.end() || found->second->doorbell != nullptr) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  runtime.signals.erase(found);
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_signal_value_t hsa_signal_load_scacquire(hsa_signal_t signal) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = find_signal(runtime, signal);
  return value ? __atomic_load_n(&value->value, __ATOMIC_ACQUIRE) : 0;
}

extern "C" hsa_status_t hsa_amd_signal_value_pointer(hsa_signal_t signal, volatile hsa_signal_value_t **value_ptr) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (value_ptr == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = find_signal(runtime, signal);
  if (!value) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  *value_ptr = &value->value;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_signal_value_t hsa_signal_load_relaxed(hsa_signal_t signal) { return hsa_signal_load_scacquire(signal); }
extern "C" hsa_signal_value_t hsa_signal_load_acquire(hsa_signal_t signal) { return hsa_signal_load_scacquire(signal); }

extern "C" void hsa_signal_store_relaxed(hsa_signal_t signal, hsa_signal_value_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = find_signal(runtime, signal);
  if (!target) return;
  __atomic_store_n(&target->value, value, __ATOMIC_RELAXED);
  if (target->doorbell != nullptr) __atomic_store_n(target->doorbell, static_cast<uint64_t>(value), __ATOMIC_RELEASE);
}

extern "C" void hsa_signal_store_screlease(hsa_signal_t signal, hsa_signal_value_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = find_signal(runtime, signal);
  if (std::getenv("GEMSTONE_HSA_TRACE_PACKETS") != nullptr)
    std::fprintf(stderr, "Gemstone HSA signal store handle=%lu value=%ld found=%d doorbell=%p\n", signal.handle,
                 static_cast<int64_t>(value), target ? 1 : 0, target ? static_cast<void *>(target->doorbell) : nullptr);
  if (!target) return;
  __atomic_store_n(&target->value, value, __ATOMIC_RELEASE);
  if (target->doorbell != nullptr) __atomic_store_n(target->doorbell, static_cast<uint64_t>(value), __ATOMIC_RELEASE);
}

extern "C" void hsa_signal_subtract_screlease(hsa_signal_t signal, hsa_signal_value_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto target = find_signal(runtime, signal);
  if (!target) return;
  const auto result = __atomic_fetch_sub(&target->value, value, __ATOMIC_RELEASE) - value;
  if (target->doorbell != nullptr) __atomic_store_n(target->doorbell, static_cast<uint64_t>(result), __ATOMIC_RELEASE);
}

extern "C" void hsa_signal_store_release(hsa_signal_t signal, hsa_signal_value_t value) { hsa_signal_store_screlease(signal, value); }

extern "C" hsa_signal_value_t hsa_signal_wait_scacquire(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare,
                                                        uint64_t timeout_hint, hsa_wait_state_t) {
  return wait_signal(signal, condition, compare, timeout_hint);
}
extern "C" hsa_signal_value_t hsa_signal_wait_relaxed(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare,
                                                      uint64_t timeout_hint, hsa_wait_state_t state_hint) {
  return hsa_signal_wait_scacquire(signal, condition, compare, timeout_hint, state_hint);
}
extern "C" hsa_signal_value_t hsa_signal_wait_acquire(hsa_signal_t signal, hsa_signal_condition_t condition, hsa_signal_value_t compare,
                                                      uint64_t timeout_hint, hsa_wait_state_t state_hint) {
  return hsa_signal_wait_scacquire(signal, condition, compare, timeout_hint, state_hint);
}

extern "C" hsa_status_t hsa_amd_signal_async_handler(hsa_signal_t signal, hsa_signal_condition_t condition,
                                                      hsa_signal_value_t compare, hsa_amd_signal_handler handler, void *arg) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (handler == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::shared_ptr<Signal> value;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    value = find_signal(runtime, signal);
  }
  if (!value) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  try {
    std::thread([value = std::move(value), condition, compare, handler, arg] {
      for (;;) {
        const auto current = __atomic_load_n(&value->value, __ATOMIC_ACQUIRE);
        if (signal_condition(current, condition, compare) && !handler(current, arg)) return;
        std::this_thread::yield();
      }
    }).detach();
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_queue_create(hsa_agent_t agent, uint32_t size, hsa_queue_type32_t type,
                                         void (*callback)(hsa_status_t, hsa_queue_t *, void *), void *data, uint32_t, uint32_t,
                                         hsa_queue_t **result) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_gpu(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (result == nullptr || !valid_power_of_two(size) || type != HSA_QUEUE_TYPE_MULTI) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  try {
    auto queue = std::make_shared<Queue>();
    queue->ring_bytes = static_cast<size_t>(size) * 64U;
    if (posix_memalign(&queue->ring, 64, queue->ring_bytes) != 0 || queue->ring == nullptr) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
    std::memset(queue->ring, 0, queue->ring_bytes);
    for (uint32_t index = 0; index < size; ++index)
      *reinterpret_cast<uint16_t *>(static_cast<uint8_t *>(queue->ring) + static_cast<size_t>(index) * 64U) = HSA_PACKET_TYPE_INVALID;
    {
      std::lock_guard<std::mutex> guard(runtime.lock);
      queue->virtual_queue =
          gemstone_hsa_virtual_kfd_create_aql_queue(runtime.kfd, queue->ring, size, nullptr, nullptr, consume, &runtime, &queue->resource);
      if (queue->virtual_queue == nullptr) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
      queue->doorbell = std::make_shared<Signal>();
      queue->doorbell->doorbell = queue->resource.doorbell;
      const uint64_t signal_handle = runtime.next_handle++;
      runtime.signals.emplace(signal_handle, queue->doorbell);
      queue->value.type = type;
      queue->value.features = HSA_QUEUE_FEATURE_KERNEL_DISPATCH;
      queue->value.base_address = queue->ring;
      queue->value.doorbell_signal = hsa_signal_t{signal_handle};
      queue->value.size = size;
      queue->value.id = queue->resource.queue_id;
      queue->callback = callback;
      queue->callback_data = data;
      runtime.queues.emplace(&queue->value, queue);
      *result = &queue->value;
      if (std::getenv("GEMSTONE_HSA_TRACE_PACKETS") != nullptr)
        std::fprintf(stderr, "Gemstone HSA queue create queue=%p id=%lu ring=%p read=%p write=%p doorbell=%p signal=%lu\n",
                     static_cast<void *>(&queue->value), queue->resource.queue_id, queue->ring,
                     static_cast<void *>(queue->resource.read_pointer), static_cast<void *>(queue->resource.write_pointer),
                     static_cast<void *>(queue->resource.doorbell), signal_handle);
    }
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_queue_destroy(hsa_queue_t *queue) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (queue == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  QueueRef value;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.queues.find(queue);
    if (found == runtime.queues.end()) return HSA_STATUS_ERROR_INVALID_QUEUE;
    value = found->second;
    value->doorbell->doorbell = nullptr;
    runtime.signals.erase(value->value.doorbell_signal.handle);
    runtime.queues.erase(found);
  }
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_queue_inactivate(hsa_queue_t *queue) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  std::lock_guard<std::mutex> guard(runtime.lock);
  return runtime.queues.count(queue) == 1 ? HSA_STATUS_ERROR : HSA_STATUS_ERROR_INVALID_QUEUE;
}

extern "C" uint64_t hsa_queue_load_read_index_scacquire(const hsa_queue_t *queue) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  return found == runtime.queues.end() ? 0 : __atomic_load_n(found->second->resource.read_pointer, __ATOMIC_ACQUIRE);
}
extern "C" uint64_t hsa_queue_load_read_index_relaxed(const hsa_queue_t *queue) { return hsa_queue_load_read_index_scacquire(queue); }
extern "C" uint64_t hsa_queue_load_read_index_acquire(const hsa_queue_t *queue) { return hsa_queue_load_read_index_scacquire(queue); }
extern "C" uint64_t hsa_queue_load_write_index_scacquire(const hsa_queue_t *queue) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  return found == runtime.queues.end() ? 0 : __atomic_load_n(found->second->resource.write_pointer, __ATOMIC_ACQUIRE);
}
extern "C" uint64_t hsa_queue_load_write_index_relaxed(const hsa_queue_t *queue) { return hsa_queue_load_write_index_scacquire(queue); }
extern "C" uint64_t hsa_queue_load_write_index_acquire(const hsa_queue_t *queue) { return hsa_queue_load_write_index_scacquire(queue); }

extern "C" uint64_t hsa_queue_add_write_index_relaxed(const hsa_queue_t *queue, uint64_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  if (found == runtime.queues.end()) {
    if (std::getenv("GEMSTONE_HSA_TRACE_PACKETS") != nullptr)
      std::fprintf(stderr, "Gemstone HSA queue reserve queue=%p found=0 increment=%lu\n", static_cast<const void *>(queue), value);
    return 0;
  }
  const uint64_t previous = __atomic_fetch_add(found->second->resource.write_pointer, value, __ATOMIC_RELAXED);
  if (std::getenv("GEMSTONE_HSA_TRACE_PACKETS") != nullptr)
    std::fprintf(stderr, "Gemstone HSA queue reserve queue=%p found=1 previous=%lu next=%lu write=%p\n", static_cast<const void *>(queue),
                 previous, previous + value, static_cast<void *>(found->second->resource.write_pointer));
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

extern "C" void hsa_queue_store_read_index_relaxed(const hsa_queue_t *queue, uint64_t value) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(const_cast<hsa_queue_t *>(queue));
  if (found != runtime.queues.end()) __atomic_store_n(found->second->resource.read_pointer, value, __ATOMIC_RELAXED);
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
  if (found != runtime.queues.end()) __atomic_store_n(found->second->resource.write_pointer, value, __ATOMIC_RELAXED);
}
extern "C" void hsa_queue_store_write_index_release(const hsa_queue_t *queue, uint64_t value) {
  hsa_queue_store_write_index_relaxed(queue, value);
}
extern "C" void hsa_queue_store_write_index_screlease(const hsa_queue_t *queue, uint64_t value) {
  hsa_queue_store_write_index_relaxed(queue, value);
}

extern "C" hsa_status_t hsa_code_object_reader_create_from_memory(const void *object, size_t size, hsa_code_object_reader_t *result) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (object == nullptr || size == 0 || result == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  try {
    auto reader = std::make_shared<Reader>();
    reader->bytes.assign(static_cast<const uint8_t *>(object), static_cast<const uint8_t *>(object) + size);
    std::lock_guard<std::mutex> guard(runtime.lock);
    const uint64_t handle = runtime.next_handle++;
    runtime.readers.emplace(handle, reader);
    result->handle = handle;
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_code_object_reader_create_from_file(hsa_file_t file, hsa_code_object_reader_t *result) {
  if (result == nullptr || file < 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
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

extern "C" hsa_status_t hsa_code_object_reader_destroy(hsa_code_object_reader_t reader_handle) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  std::lock_guard<std::mutex> guard(runtime.lock);
  return runtime.readers.erase(reader_handle.handle) == 1 ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER;
}

extern "C" hsa_status_t hsa_executable_create_alt(hsa_profile_t profile, hsa_default_float_rounding_mode_t, const char *,
                                                  hsa_executable_t *result) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if ((profile != HSA_PROFILE_BASE && profile != HSA_PROFILE_FULL) || result == nullptr)
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  try {
    auto executable = std::make_shared<Executable>();
    std::lock_guard<std::mutex> guard(runtime.lock);
    executable->handle = runtime.next_handle++;
    runtime.executables.emplace(executable->handle, executable);
    result->handle = executable->handle;
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_executable_create(hsa_profile_t profile, hsa_executable_state_t executable_state, const char *options,
                                              hsa_executable_t *result) {
  if (executable_state != HSA_EXECUTABLE_STATE_UNFROZEN) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  return hsa_executable_create_alt(profile, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, options, result);
}

extern "C" hsa_status_t hsa_executable_load_agent_code_object(hsa_executable_t executable_handle, hsa_agent_t agent,
                                                              hsa_code_object_reader_t reader_handle, const char *,
                                                              hsa_loaded_code_object_t *loaded) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_gpu(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  ExecutableRef executable_value;
  std::shared_ptr<Reader> reader_value;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    executable_value = executable(runtime, executable_handle);
    reader_value = reader(runtime, reader_handle);
    if (!executable_value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
    if (!reader_value) return HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER;
    if (executable_value->frozen) return HSA_STATUS_ERROR_FROZEN_EXECUTABLE;
  }

  std::vector<gemstone::hsa::CodeObjectKernelMetadata> metadata;
  std::string error;
  if (!gemstone::hsa::parse_code_object_metadata(reader_value->bytes.data(), reader_value->bytes.size(), metadata, error)
      || metadata.empty())
    return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
  std::vector<KernelRef> kernels;
  std::vector<gemstone_hsa_kernel_binding> bindings;
  try {
    kernels.reserve(metadata.size());
    bindings.reserve(metadata.size());
    for (auto &item : metadata) {
      auto kernel = std::make_shared<Kernel>();
      kernel->metadata = std::move(item);
      {
        std::lock_guard<std::mutex> guard(runtime.lock);
        kernel->object = runtime.next_kernel++;
      }
      kernels.push_back(kernel);
      bindings.push_back({kernel->object, kernel->metadata.name.c_str(), kernel->metadata.kernarg_size, kernel->metadata.fields.data(),
                          static_cast<uint32_t>(kernel->metadata.fields.size()), kernel->metadata.explicit_argument_count});
    }
  } catch (...) {
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
  if (gemstone_hsa_aql_runtime_install_code_object(runtime.aql, reader_value->bytes.data(),
                                                   static_cast<uint32_t>(reader_value->bytes.size()), bindings.data(),
                                                   static_cast<uint32_t>(bindings.size()))
      != 0) {
    std::fprintf(stderr, "hsa install failed: %s\n", gemstone_hsa_aql_runtime_error(runtime.aql));
    return HSA_STATUS_ERROR_INCOMPATIBLE_ARGUMENTS;
  }
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    for (const auto &kernel : kernels) {
      if (runtime.kernels.count(kernel->object) != 0) return HSA_STATUS_ERROR_INVALID_CODE_OBJECT;
      runtime.kernels.emplace(kernel->object, kernel);
    }
    executable_value->kernels.insert(executable_value->kernels.end(), kernels.begin(), kernels.end());
  }
  if (loaded != nullptr) loaded->handle = executable_handle.handle;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_executable_load_program_code_object(hsa_executable_t executable, hsa_code_object_reader_t reader,
                                                                const char *options, hsa_loaded_code_object_t *loaded) {
  return hsa_executable_load_agent_code_object(executable, hsa_agent_t{kGpuAgent}, reader, options, loaded);
}

extern "C" hsa_status_t hsa_executable_freeze(hsa_executable_t executable_handle, const char *) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = executable(runtime, executable_handle);
  if (!value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
  if (value->frozen) return HSA_STATUS_ERROR_FROZEN_EXECUTABLE;
  value->frozen = true;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_executable_validate(hsa_executable_t executable_handle, uint32_t *result) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (result == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto value = executable(runtime, executable_handle);
  if (!value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
  if (!value->frozen) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
  *result = 0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_executable_destroy(hsa_executable_t executable_handle) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  ExecutableRef value;
  std::vector<GlobalRef> globals;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.executables.find(executable_handle.handle);
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
    if (gemstone_hsa_aql_runtime_remove_kernel(runtime.aql, kernel->object) != 0) return HSA_STATUS_ERROR;
  for (const auto &global : globals)
    if (free_allocation(runtime, global->address, true) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t gemstone_hsa_executable_write_global(hsa_executable_t executable_handle, const char *name, const void *bytes,
                                                               size_t size, size_t offset) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (name == nullptr || name[0] == '\0' || (size != 0 && bytes == nullptr) || size > UINT32_MAX)
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  uint64_t kernel_object = 0;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto value = executable(runtime, executable_handle);
    if (!value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
    if (!value->frozen || value->kernels.empty()) return HSA_STATUS_ERROR_INVALID_EXECUTABLE_SYMBOL;
    kernel_object = value->kernels.front()->object;
  }
  const int result = gemstone_hsa_aql_runtime_write_global(runtime.aql, kernel_object, name, offset, bytes, static_cast<uint32_t>(size));
  return result == 0 ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_SYMBOL_NAME;
}

extern "C" hsa_status_t hsa_executable_get_symbol_by_name(hsa_executable_t executable_handle, const char *name, const hsa_agent_t *agent,
                                                          hsa_executable_symbol_t *symbol) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (name == nullptr || symbol == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  if (agent != nullptr && !valid_gpu(*agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  const std::string requested(name);
  ExecutableRef value;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    value = executable(runtime, executable_handle);
    if (!value) return HSA_STATUS_ERROR_INVALID_EXECUTABLE;
    if (!value->frozen) return HSA_STATUS_ERROR_INVALID_EXECUTABLE_SYMBOL;
    for (const auto &kernel : value->kernels) {
      if (kernel->metadata.name == requested || kernel->metadata.symbol == requested || (kernel->metadata.symbol == requested + ".kd")) {
        symbol->handle = kernel->object;
        return HSA_STATUS_SUCCESS;
      }
    }
    for (const auto &global : value->globals) {
      if (global->name == requested) {
        symbol->handle = global->object;
        return HSA_STATUS_SUCCESS;
      }
    }
  }

  const uint32_t size = known_global_size(requested);
  if (size == 0) return HSA_STATUS_ERROR_INVALID_SYMBOL_NAME;
  void *address = nullptr;
  if (allocate(runtime, kGpuPool, size, &address, true) != HSA_STATUS_SUCCESS)
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  try {
    auto global = std::make_shared<Global>();
    std::lock_guard<std::mutex> guard(runtime.lock);
    global->object = runtime.next_kernel++;
    global->name = requested;
    global->size = size;
    global->address = address;
    value->globals.push_back(global);
    runtime.globals.emplace(global->object, global);
    symbol->handle = global->object;
    return HSA_STATUS_SUCCESS;
  } catch (...) {
    (void)free_allocation(runtime, address, true);
    return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
  }
}

extern "C" hsa_status_t hsa_executable_symbol_get_info(hsa_executable_symbol_t symbol_handle, hsa_executable_symbol_info_t attribute,
                                                       void *value) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (value == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto kernel = runtime.kernels.find(symbol_handle.handle);
  if (kernel != runtime.kernels.end()) {
    switch (attribute) {
      case HSA_EXECUTABLE_SYMBOL_INFO_TYPE:
        return copy_value(value, HSA_SYMBOL_KIND_KERNEL) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT:
        return copy_value(value, kernel->second->object) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE:
        return copy_value(value, kernel->second->metadata.kernarg_size) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_ALIGNMENT:
        return copy_value(value, uint32_t(16)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_GROUP_SEGMENT_SIZE:
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_PRIVATE_SEGMENT_SIZE:
        return copy_value(value, uint32_t(0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_DYNAMIC_CALLSTACK:
        return copy_value(value, false) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
      case HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_CALL_CONVENTION:
        return copy_value(value, uint32_t(0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
      default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
    }
  }
  const auto global = runtime.globals.find(symbol_handle.handle);
  if (global == runtime.globals.end()) return HSA_STATUS_ERROR_INVALID_EXECUTABLE_SYMBOL;
  switch (attribute) {
    case HSA_EXECUTABLE_SYMBOL_INFO_TYPE:
      return copy_value(value, HSA_SYMBOL_KIND_VARIABLE) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_ADDRESS:
      return copy_value(value, reinterpret_cast<uint64_t>(global->second->address)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_SIZE:
      return copy_value(value, global->second->size) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_amd_agent_iterate_memory_pools(hsa_agent_t agent, hsa_status_t (*callback)(hsa_amd_memory_pool_t, void *),
                                                           void *data) {
  trace_api_call("hsa_amd_agent_iterate_memory_pools", agent.handle);
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_agent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (callback == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const auto status = callback(hsa_amd_memory_pool_t{agent.handle == kCpuAgent ? kCpuPool : kGpuPool}, data);
  trace_api_result("hsa_amd_agent_iterate_memory_pools", status);
  return status;
}

extern "C" hsa_status_t hsa_amd_memory_pool_get_info(hsa_amd_memory_pool_t pool, hsa_amd_memory_pool_info_t attribute, void *value) {
  trace_api_call("hsa_amd_memory_pool_get_info", pool.handle, attribute);
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_pool(pool)) return static_cast<hsa_status_t>(HSA_STATUS_ERROR_INVALID_MEMORY_POOL);
  if (value == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const uint32_t flags = HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_KERNARG_INIT
      | (pool.handle == kCpuPool ? HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_FINE_GRAINED : HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_COARSE_GRAINED);
  switch (attribute) {
    case HSA_AMD_MEMORY_POOL_INFO_SEGMENT:
      return copy_value(value, HSA_AMD_SEGMENT_GLOBAL) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_MEMORY_POOL_INFO_GLOBAL_FLAGS: return copy_value(value, flags) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_MEMORY_POOL_INFO_SIZE:
      return copy_value(value, size_t(kVirtualMemorySize)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_ALLOWED:
      return copy_value(value, true) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_GRANULE:
    case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_ALIGNMENT:
    case HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_REC_GRANULE:
      return copy_value(value, size_t(kAllocationGranule)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_MEMORY_POOL_INFO_ACCESSIBLE_BY_ALL:
      return copy_value(value, true) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_MEMORY_POOL_INFO_ALLOC_MAX_SIZE:
      return copy_value(value, size_t(kVirtualMemorySize)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_MEMORY_POOL_INFO_LOCATION:
      return copy_value(value, pool.handle == kCpuPool ? HSA_AMD_MEMORY_POOL_LOCATION_CPU : HSA_AMD_MEMORY_POOL_LOCATION_GPU)
                 ? HSA_STATUS_SUCCESS
                 : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_amd_memory_pool_allocate(hsa_amd_memory_pool_t pool, size_t size, uint32_t flags, void **ptr) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_pool(pool)) return static_cast<hsa_status_t>(HSA_STATUS_ERROR_INVALID_MEMORY_POOL);
  if (flags != 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  return allocate(runtime, pool.handle, size, ptr, true);
}

extern "C" hsa_status_t hsa_amd_memory_pool_free(void *ptr) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  return free_allocation(runtime, ptr, true);
}

extern "C" hsa_status_t hsa_amd_agents_allow_access(uint32_t count, const hsa_agent_t *agents, const uint32_t *flags, const void *ptr) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (count == 0 || agents == nullptr || flags != nullptr || ptr == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  for (uint32_t index = 0; index < count; ++index)
    if (!valid_agent(agents[index])) return HSA_STATUS_ERROR_INVALID_AGENT;
  return find_allocation(runtime, ptr) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ALLOCATION;
}

extern "C" hsa_status_t hsa_amd_memory_fill(void *ptr, uint32_t value, size_t count) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (ptr == nullptr || (reinterpret_cast<uintptr_t>(ptr) & 3U) != 0 || count > std::numeric_limits<size_t>::max() / 4U)
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const size_t bytes = count * 4U;
  AllocationRef allocation;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    allocation = find_allocation(runtime, ptr, bytes);
  }
  if (!allocation) return HSA_STATUS_ERROR_INVALID_ALLOCATION;
  auto *destination = static_cast<uint32_t *>(ptr);
  std::fill_n(destination, count, value);
  return upload(runtime, allocation, reinterpret_cast<uintptr_t>(ptr) - reinterpret_cast<uintptr_t>(allocation->base), bytes) == 0
             ? HSA_STATUS_SUCCESS
             : HSA_STATUS_ERROR;
}

extern "C" hsa_status_t hsa_amd_memory_async_copy(void *dst, hsa_agent_t dst_agent, const void *src, hsa_agent_t src_agent, size_t size,
                                                  uint32_t dependency_count, const hsa_signal_t *dependencies, hsa_signal_t completion) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_agent(dst_agent) || !valid_agent(src_agent) || completion.handle == 0 || (dependency_count != 0 && dependencies == nullptr)
      || (size != 0 && (dst == nullptr || src == nullptr)))
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  for (uint32_t index = 0; index < dependency_count; ++index)
    wait_signal(dependencies[index], HSA_SIGNAL_CONDITION_LT, 1, UINT64_MAX);
  AllocationRef destination;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (size != 0 && !find_allocation(runtime, src, size) && !find_allocation(runtime, dst, size))
      return HSA_STATUS_ERROR_INVALID_ALLOCATION;
    destination = find_allocation(runtime, dst, size);
    if (!find_signal(runtime, completion)) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  }
  if (size != 0) std::memmove(dst, src, size);
  if (destination
      && upload(runtime, destination, reinterpret_cast<uintptr_t>(dst) - reinterpret_cast<uintptr_t>(destination->base), size) != 0)
    return HSA_STATUS_ERROR;
  return complete_signal(completion) == 0 ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_SIGNAL;
}

extern "C" hsa_status_t hsa_amd_memory_async_copy_on_engine(void *dst, hsa_agent_t dst_agent, const void *src, hsa_agent_t src_agent,
                                                            size_t size, uint32_t dependency_count, const hsa_signal_t *dependencies,
                                                            hsa_signal_t completion, hsa_amd_sdma_engine_id_t, bool) {
  return hsa_amd_memory_async_copy(dst, dst_agent, src, src_agent, size, dependency_count, dependencies, completion);
}

extern "C" hsa_status_t hsa_amd_agent_memory_pool_get_info(hsa_agent_t agent, hsa_amd_memory_pool_t pool,
                                                           hsa_amd_agent_memory_pool_info_t attribute, void *value) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_agent(agent) || !valid_pool(pool)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (value == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  switch (attribute) {
    case HSA_AMD_AGENT_MEMORY_POOL_INFO_ACCESS:
      return copy_value(value, HSA_AMD_MEMORY_POOL_ACCESS_ALLOWED_BY_DEFAULT) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    case HSA_AMD_AGENT_MEMORY_POOL_INFO_NUM_LINK_HOPS:
      return copy_value(value, uint32_t(0)) ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_ARGUMENT;
    default: return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  }
}

extern "C" hsa_status_t hsa_amd_memory_lock(void *host_ptr, size_t size, hsa_agent_t *agents, int num_agent, void **agent_ptr) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (host_ptr == nullptr || size == 0 || agent_ptr == nullptr || num_agent < 0 ||
      (num_agent == 0 && agents != nullptr) || (num_agent != 0 && agents == nullptr))
    return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  for (int index = 0; index < num_agent; ++index)
    if (!valid_agent(agents[index])) return HSA_STATUS_ERROR_INVALID_AGENT;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (const auto allocation = find_allocation(runtime, host_ptr, size)) {
      *agent_ptr = allocation->base;
      return HSA_STATUS_SUCCESS;
    }
  }
  const auto status = hsa_memory_register(host_ptr, size);
  if (status != HSA_STATUS_SUCCESS) return status;
  *agent_ptr = host_ptr;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_memory_lock_to_pool(void *host_ptr, size_t size, hsa_agent_t *agents, int num_agent,
                                                     hsa_amd_memory_pool_t pool, uint32_t flags, void **agent_ptr) {
  if (!valid_pool(pool) || pool.handle != kCpuPool) return static_cast<hsa_status_t>(HSA_STATUS_ERROR_INVALID_MEMORY_POOL);
  if (flags != 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  return hsa_amd_memory_lock(host_ptr, size, agents, num_agent, agent_ptr);
}

extern "C" hsa_status_t hsa_amd_memory_unlock(void *host_ptr) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  return free_allocation(runtime, host_ptr, false);
}

extern "C" hsa_status_t hsa_amd_pointer_info(const void *ptr, hsa_amd_pointer_info_t *info, void *(*alloc)(size_t),
                                               uint32_t *num_agents_accessible, hsa_agent_t **accessible) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (ptr == nullptr || info == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  const uint32_t requested_size = info->size;
  if (requested_size == 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  AllocationRef allocation;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    allocation = find_allocation(runtime, ptr);
  }
  const uint32_t supported_size = static_cast<uint32_t>(sizeof(hsa_amd_pointer_info_t));
  std::memset(info, 0, std::min<uint32_t>(requested_size, supported_size));
  info->size = std::min<uint32_t>(requested_size, supported_size);
  if (!allocation) {
    info->type = HSA_EXT_POINTER_TYPE_UNKNOWN;
    return HSA_STATUS_SUCCESS;
  }
  info->type = allocation->owned ? HSA_EXT_POINTER_TYPE_HSA : HSA_EXT_POINTER_TYPE_LOCKED;
  info->agentBaseAddress = allocation->base;
  info->hostBaseAddress = allocation->base;
  info->sizeInBytes = allocation->size;
  info->agentOwner = hsa_agent_t{allocation->pool == kGpuPool ? kGpuAgent : kCpuAgent};
  info->global_flags = HSA_AMD_MEMORY_POOL_GLOBAL_FLAG_FINE_GRAINED;
  info->registered = true;
  if (num_agents_accessible != nullptr) *num_agents_accessible = 2;
  if (accessible != nullptr) {
    if (alloc == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
    auto *agents = static_cast<hsa_agent_t *>(alloc(2 * sizeof(hsa_agent_t)));
    if (agents == nullptr) return HSA_STATUS_ERROR_OUT_OF_RESOURCES;
    agents[0] = hsa_agent_t{kCpuAgent};
    agents[1] = hsa_agent_t{kGpuAgent};
    *accessible = agents;
  }
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_profiling_set_profiler_enabled(hsa_queue_t *queue, int) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (queue == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  return runtime.queues.count(queue) == 1 ? HSA_STATUS_SUCCESS : HSA_STATUS_ERROR_INVALID_QUEUE;
}

extern "C" hsa_status_t hsa_amd_profiling_async_copy_enable(bool) {
  auto &runtime = state();
  return require_initialized(runtime);
}

extern "C" hsa_status_t hsa_amd_profiling_get_dispatch_time(hsa_agent_t agent, hsa_signal_t signal,
                                                              hsa_amd_profiling_dispatch_time_t *time) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (!valid_agent(agent)) return HSA_STATUS_ERROR_INVALID_AGENT;
  if (time == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (!find_signal(runtime, signal)) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  time->start = 0;
  time->end = 0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_profiling_get_async_copy_time(hsa_signal_t signal, hsa_amd_profiling_async_copy_time_t *time) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (time == nullptr) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (!find_signal(runtime, signal)) return HSA_STATUS_ERROR_INVALID_SIGNAL;
  time->start = 0;
  time->end = 0;
  return HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_svm_attributes_set(void *ptr, size_t size, hsa_amd_svm_attribute_pair_t *, size_t attribute_count) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  if (ptr == nullptr || size == 0) return HSA_STATUS_ERROR_INVALID_ARGUMENT;
  return attribute_count == 0 ? HSA_STATUS_SUCCESS : HSA_STATUS_SUCCESS;
}

extern "C" hsa_status_t hsa_amd_register_system_event_handler(hsa_amd_system_event_callback_t callback, void *) {
  auto &runtime = state();
  if (const auto status = require_initialized(runtime); status != HSA_STATUS_SUCCESS) return status;
  return callback == nullptr ? HSA_STATUS_ERROR_INVALID_ARGUMENT : HSA_STATUS_SUCCESS;
}
