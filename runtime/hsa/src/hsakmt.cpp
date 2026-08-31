// SPDX-License-Identifier: Apache-2.0
#include <algorithm>
#include <array>
#include <chrono>
#include <condition_variable>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <memory>
#include <mutex>
#include <new>
#include <string>
#include <string_view>
#include <thread>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>

#include "gemstone/hsa/code_object_metadata.hpp"
#include "gemstone/hsa/hsakmt_runtime.h"
#include "gemstone/hsa/target.h"
#include "gemstone/hsa/virtual_kfd.h"
#include "gemstone_execution_rpc.h"
#include <hsakmt/hsakmt.h>

namespace {

constexpr uint32_t kCpuNode = 0;
constexpr uint32_t kGpuNode = 1;
// The shared execution service currently mirrors allocations densely. Keep the
// advertised virtual aperture below ROCr's eager scratch-reservation threshold;
// actual dispatch scratch remains a separate execution-protocol milestone.
constexpr uint64_t kAvailableMemory = UINT64_C(1) * 1024 * 1024 * 1024;
constexpr uint32_t kPageSize = 4096;
constexpr uint32_t kTransferChunk = 64U * 1024U * 1024U;
constexpr uint32_t kUploadChunk = 64U * 1024U;

struct Allocation {
  Allocation(void *allocation_base, uint64_t allocation_size, uint64_t allocation_id, HsaMemFlags allocation_flags,
             HSA_POINTER_TYPE allocation_type, uint32_t allocation_node, bool allocation_owned)
      : base(allocation_base), size(allocation_size), id(allocation_id), flags(allocation_flags), type(allocation_type),
        node(allocation_node), owned(allocation_owned) {}

  void *base = nullptr;
  uint64_t size = 0;
  uint64_t id = 0;
  HsaMemFlags flags{};
  HSA_POINTER_TYPE type = HSA_POINTER_UNKNOWN;
  uint32_t node = kCpuNode;
  void *user_data = nullptr;
  bool owned = false;
  uint32_t map_count = 0;
  std::mutex memory_lock;
  std::vector<unsigned char> upload_snapshot;
  bool upload_snapshot_valid = false;
};

using AllocationRef = std::shared_ptr<Allocation>;

struct Event {
  HsaEvent value{};
  alignas(8) uint64_t mailbox = 0;
  bool manual_reset = false;
  bool signaled = false;
  uint64_t age = 0;
  std::mutex lock;
  std::condition_variable changed;
};

struct Queue {
  gemstone_hsa_virtual_queue *queue = nullptr;
  gemstone_hsa_virtual_queue_resource resource{};
  void *ring = nullptr;
  uint64_t ring_size = 0;
  alignas(8) int64_t error_reason = 0;
};

struct alignas(8) DeferredSignal {
  uint64_t kind = 0;
  int64_t value = 1;
};
static_assert(offsetof(DeferredSignal, value) == 8);

struct SignalAbiView {
  uint64_t kind;
  int64_t value;
  uint64_t event_mailbox;
  uint32_t event_id;
  uint32_t reserved;
};
static_assert(offsetof(SignalAbiView, value) == 8);
static_assert(offsetof(SignalAbiView, event_id) == 24);

struct State {
  std::mutex lock;
  uint32_t open_count = 0;
  uint64_t next_allocation = 1;
  uint32_t next_event = 1;
  gemstone_hsa_virtual_kfd *kfd = nullptr;
  gemstone_hsa_aql_runtime *runtime = nullptr;
  gemstone_execution_client *client = nullptr;
  gemstone_hsakmt_memory_transport memory{};
  bool installed_backend = false;
  std::unordered_map<void *, AllocationRef> allocations;
  std::unordered_map<HsaEvent *, std::unique_ptr<Event>> events;
  std::unordered_map<HSA_QUEUEID, std::unique_ptr<Queue>> queues;
  std::unordered_set<uint64_t> notified_kernels;
  std::string loader_error;
};

State &state() {
  // The HSA runtime may call the thunk from executable/DSO finalizers after
  // this library's ordinary C++ static destructors would have run. KFD is a
  // process-lifetime service, so retain its state until process termination
  // instead of exposing a destructed mutex/allocation map during late HSACO
  // teardown.
  static State *value = new State();
  return *value;
}

bool valid_memory_transport(const gemstone_hsakmt_memory_transport &memory) {
  return memory.register_allocation != nullptr && memory.release_allocation != nullptr && memory.write_allocation != nullptr
         && memory.read_allocation != nullptr;
}

int environment_register(void *context, uint64_t id, uint64_t size) {
  return gemstone_execution_register_allocation(static_cast<gemstone_execution_client *>(context), id, size, GEMSTONE_ALLOCATION_SHARED);
}

int environment_release(void *context, uint64_t id) {
  return gemstone_execution_release_allocation(static_cast<gemstone_execution_client *>(context), id);
}

int environment_write(void *context, uint64_t id, uint64_t offset, const void *bytes, uint32_t size) {
  return gemstone_execution_write_allocation(static_cast<gemstone_execution_client *>(context), id, offset, bytes, size);
}

int environment_read(void *context, uint64_t id, uint64_t offset, void *bytes, uint32_t size) {
  return gemstone_execution_read_allocation(static_cast<gemstone_execution_client *>(context), id, offset, bytes, size);
}

bool opened(const State &runtime) { return runtime.open_count != 0; }

const gemstone::hsa::CodeObjectKernelMetadata *find_kernel_metadata(const std::vector<gemstone::hsa::CodeObjectKernelMetadata> &metadata,
                                                                    const char *name) {
  if (name == nullptr) return nullptr;
  const std::string_view loaded(name);
  for (const auto &kernel : metadata) {
    if (kernel.name == loaded || kernel.symbol == loaded) return &kernel;
    constexpr std::string_view suffix = ".kd";
    if (kernel.symbol.size() > suffix.size() && kernel.symbol.compare(kernel.symbol.size() - suffix.size(), suffix.size(), suffix) == 0
        && std::string_view(kernel.symbol).substr(0, kernel.symbol.size() - suffix.size()) == loaded)
      return &kernel;
  }
  return nullptr;
}

HSAKMT_STATUS require_open(const State &runtime) {
  return opened(runtime) ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
}

Allocation *find_allocation(State &runtime, const void *pointer) {
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  for (auto &[_, allocation] : runtime.allocations) {
    const auto base = reinterpret_cast<uintptr_t>(allocation->base);
    if (address >= base && address - base < allocation->size) return allocation.get();
  }
  return nullptr;
}

bool prepare_upload_snapshot(Allocation &allocation) {
  if (allocation.size > std::numeric_limits<size_t>::max()) return false;
  if (allocation.upload_snapshot.size() == allocation.size) return true;
  try {
    allocation.upload_snapshot.resize(static_cast<size_t>(allocation.size));
  } catch (...) {
    allocation.upload_snapshot.clear();
    allocation.upload_snapshot_valid = false;
    return false;
  }
  allocation.upload_snapshot_valid = false;
  return true;
}

bool upload_changed_allocation(const gemstone_hsakmt_memory_transport &memory, Allocation &allocation) {
  std::lock_guard<std::mutex> guard(allocation.memory_lock);
  if (!prepare_upload_snapshot(allocation)) return false;
  const bool snapshot_valid = allocation.upload_snapshot_valid;
  uint64_t offset = 0;
  while (offset < allocation.size) {
    const auto remaining = allocation.size - offset;
    const auto chunk = static_cast<uint32_t>(std::min<uint64_t>(remaining, kUploadChunk));
    const auto *bytes = static_cast<const unsigned char *>(allocation.base) + offset;
    auto *snapshot = allocation.upload_snapshot.data() + offset;
    if (!snapshot_valid || std::memcmp(bytes, snapshot, chunk) != 0) {
      if (memory.write_allocation(memory.context, allocation.id, offset, bytes, chunk) != 0) {
        allocation.upload_snapshot_valid = false;
        return false;
      }
      std::memcpy(snapshot, bytes, chunk);
    }
    offset += chunk;
  }
  allocation.upload_snapshot_valid = true;
  return true;
}

bool download_allocation_range(const gemstone_hsakmt_memory_transport &memory, Allocation &allocation, uint64_t range_offset,
                               uint32_t range_size) {
  std::lock_guard<std::mutex> guard(allocation.memory_lock);
  if (range_size == 0 || range_offset > allocation.size || range_size > allocation.size - range_offset) return false;
  if (!prepare_upload_snapshot(allocation)) return false;
  if (!allocation.upload_snapshot_valid) {
    std::memcpy(allocation.upload_snapshot.data(), allocation.base, static_cast<size_t>(allocation.size));
    allocation.upload_snapshot_valid = true;
  }
  uint64_t consumed = 0;
  while (consumed < range_size) {
    const auto remaining = static_cast<uint64_t>(range_size) - consumed;
    const auto chunk = static_cast<uint32_t>(std::min<uint64_t>(remaining, kTransferChunk));
    const uint64_t offset = range_offset + consumed;
    auto *bytes = static_cast<unsigned char *>(allocation.base) + offset;
    if (memory.read_allocation(memory.context, allocation.id, offset, bytes, chunk) != 0) {
      allocation.upload_snapshot_valid = false;
      return false;
    }
    std::memcpy(allocation.upload_snapshot.data() + offset, bytes, chunk);
    consumed += chunk;
  }
  return true;
}

bool notify_signal_event(uint64_t signal) {
  if (signal == 0) return false;
  const auto *view = reinterpret_cast<const SignalAbiView *>(static_cast<uintptr_t>(signal));
  const uint32_t event_id = __atomic_load_n(&view->event_id, __ATOMIC_ACQUIRE);
  const bool trace_packets = std::getenv("GEMSTONE_HSA_TRACE_PACKETS") != nullptr;
  if (event_id == 0) {
    if (trace_packets) std::fprintf(stderr, "Gemstone completion signal=%lu has no event\n", signal);
    return true;
  }
  auto &runtime = state();
  std::lock_guard<std::mutex> state_guard(runtime.lock);
  const auto found = std::find_if(runtime.events.begin(), runtime.events.end(),
                                  [event_id](const auto &entry) { return entry.second->value.EventId == event_id; });
  if (found == runtime.events.end()) {
    if (trace_packets) std::fprintf(stderr, "Gemstone completion signal=%lu references unknown event=%u\n", signal, event_id);
    return false;
  }
  {
    std::lock_guard<std::mutex> event_guard(found->second->lock);
    __atomic_store_n(&found->second->mailbox, static_cast<uint64_t>(event_id), __ATOMIC_RELEASE);
    found->second->signaled = true;
    ++found->second->age;
  }
  found->second->changed.notify_all();
  if (trace_packets) std::fprintf(stderr, "Gemstone notified completion signal=%lu event=%u\n", signal, event_id);
  return true;
}

gemstone_hsa_packet_result consume_packet(uint64_t queue_id, uint64_t packet_id, const void *packet, size_t packet_size, void *) {
  auto &runtime = state();
  const bool trace_packets = std::getenv("GEMSTONE_HSA_TRACE_PACKETS") != nullptr;
  const bool trace_failures = trace_packets || std::getenv("GEMSTONE_HSA_TRACE_FAILURES") != nullptr;
  if (trace_packets) std::fprintf(stderr, "Gemstone consuming AQL queue=%lu packet=%lu\n", queue_id, packet_id);
  gemstone_hsa_aql_packet decoded{};
  const auto decode = gemstone_hsa_decode_aql_packet(packet, packet_size, &decoded);
  std::vector<AllocationRef> retained;
  std::vector<AllocationRef> accessible;
  gemstone_hsa_aql_runtime *aql = nullptr;
  gemstone_hsakmt_memory_transport memory{};
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    aql = runtime.runtime;
    memory = runtime.memory;
    if (packet_size == 64 && decode == GEMSTONE_HSA_AQL_DECODE_SUCCESS && decoded.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH) {
      retained.reserve(runtime.allocations.size());
      accessible.reserve(runtime.allocations.size());
      for (auto &[_, allocation] : runtime.allocations) {
        retained.push_back(allocation);
        // CPU-node allocations back ROCr's fine-grained system and kernarg
        // regions. They are mutable by the producer between packets and are
        // GPU-visible without hsaKmtMapMemoryToGPU, so refresh them before
        // every kernel dispatch. Barriers and virtual PM4 synchronization do
        // not access memory; the next kernel refresh establishes visibility
        // for any intervening producer writes. GPU-node allocations are
        // uploaded by MapMemoryToGPU and thereafter remain
        // remote-authoritative until completion copy-back; refreshing them
        // here would erase writes made by completion-less AQL dispatches.
        if (allocation->node == kCpuNode) accessible.push_back(allocation);
      }
    }
  }
  if (packet_size != 64 || decode != GEMSTONE_HSA_AQL_DECODE_SUCCESS) {
    const auto result = gemstone_hsa_aql_runtime_consume(queue_id, packet_id, packet, packet_size, aql);
    if (trace_packets) {
      uint16_t header = 0;
      if (packet != nullptr && packet_size >= sizeof(header)) std::memcpy(&header, packet, sizeof(header));
      std::fprintf(stderr, "Gemstone rejected AQL decode queue=%lu packet=%lu size=%zu header=0x%04x decode=%d result=%d error=%s\n",
                   queue_id, packet_id, packet_size, header, decode, result, gemstone_hsa_aql_runtime_error(aql));
      if (packet != nullptr) {
        const auto *bytes = static_cast<const unsigned char *>(packet);
        std::fprintf(stderr, "Gemstone raw AQL queue=%lu packet=%lu bytes=", queue_id, packet_id);
        for (size_t index = 0; index < packet_size; ++index)
          std::fprintf(stderr, "%02x", bytes[index]);
        std::fprintf(stderr, "\n");
      }
    }
    return result;
  }
  for (const auto &allocation : accessible)
    if (!upload_changed_allocation(memory, *allocation)) return GEMSTONE_HSA_PACKET_FATAL;
  if (trace_packets)
    std::fprintf(stderr, "Gemstone uploaded AQL queue=%lu packet=%lu allocations=%zu\n", queue_id, packet_id, accessible.size());
  const uint64_t completion = decoded.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH   ? decoded.value.kernel_dispatch.completion_signal
                              : decoded.type == GEMSTONE_HSA_AQL_VENDOR_SPECIFIC ? decoded.value.pm4_ib.completion_signal
                                                                                 : decoded.value.barrier.completion_signal;
  if (completion == 0) {
    const auto result = gemstone_hsa_aql_runtime_consume(queue_id, packet_id, packet, packet_size, aql);
    if (trace_packets) {
      std::fprintf(stderr, "Gemstone executed AQL queue=%lu packet=%lu result=%d\n", queue_id, packet_id, result);
      if (result != GEMSTONE_HSA_PACKET_ACCEPTED) {
        if (decoded.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH)
          std::fprintf(stderr, "Gemstone rejected completion-less AQL queue=%lu packet=%lu kernel_object=0x%lx error=%s\n", queue_id,
                       packet_id, decoded.value.kernel_dispatch.kernel_object, gemstone_hsa_aql_runtime_error(aql));
        else
          std::fprintf(stderr, "Gemstone rejected completion-less AQL queue=%lu packet=%lu error=%s\n", queue_id, packet_id,
                       gemstone_hsa_aql_runtime_error(aql));
      }
    }
    return result;
  }

  // AQL execution ordinarily publishes its completion before returning.  The
  // virtual transport must first mirror remote writes back into ROCr's native
  // allocation, so execute against a private signal and forward the result
  // only after copy-back.  This establishes the same release/acquire ordering
  // promised by a physical GPU completion signal.
  DeferredSignal deferred;
  std::array<unsigned char, 64> deferred_packet{};
  std::memcpy(deferred_packet.data(), packet, deferred_packet.size());
  const uint64_t deferred_handle = reinterpret_cast<uint64_t>(&deferred);
  std::memcpy(deferred_packet.data() + 56, &deferred_handle, sizeof(deferred_handle));
  const auto result = gemstone_hsa_aql_runtime_consume(queue_id, packet_id, deferred_packet.data(), deferred_packet.size(), aql);
  if (trace_packets) std::fprintf(stderr, "Gemstone executed AQL queue=%lu packet=%lu result=%d\n", queue_id, packet_id, result);
  if (result != GEMSTONE_HSA_PACKET_ACCEPTED) return result;
  const int64_t outcome = __atomic_load_n(&deferred.value, __ATOMIC_ACQUIRE);
  if (trace_failures && outcome < 0) {
    if (decoded.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH)
      std::fprintf(stderr, "Gemstone rejected AQL queue=%lu packet=%lu kernel_object=0x%lx outcome=%ld error=%s\n", queue_id, packet_id,
                   decoded.value.kernel_dispatch.kernel_object, outcome, gemstone_hsa_aql_runtime_error(aql));
    else
      std::fprintf(stderr, "Gemstone rejected AQL queue=%lu packet=%lu outcome=%ld error=%s\n", queue_id, packet_id, outcome,
                   gemstone_hsa_aql_runtime_error(aql));
  }
  const auto signals = gemstone_hsa_rocr_signal_operations();
  if (outcome < 0)
    return signals.fail_release(signals.context, completion, static_cast<int32_t>(outcome)) == 0 && notify_signal_event(completion)
               ? GEMSTONE_HSA_PACKET_ACCEPTED
               : GEMSTONE_HSA_PACKET_FATAL;
  if (outcome != 0) return GEMSTONE_HSA_PACKET_FATAL;
  uint32_t dirty_count = 0;
  if (gemstone_hsa_aql_runtime_take_dirty_ranges(aql, nullptr, 0, &dirty_count) != 0) return GEMSTONE_HSA_PACKET_FATAL;
  std::vector<gemstone_hsa_dirty_range> dirty_ranges(dirty_count);
  if (dirty_count != 0 && gemstone_hsa_aql_runtime_take_dirty_ranges(aql, dirty_ranges.data(), dirty_count, &dirty_count) != 0)
    return GEMSTONE_HSA_PACKET_FATAL;
  struct DirtyAllocationRange {
    AllocationRef allocation;
    uint64_t offset;
    uint32_t size;
  };
  std::vector<DirtyAllocationRange> dirty;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    dirty.reserve(dirty_ranges.size());
    for (const auto &range : dirty_ranges) {
      const auto found = std::find_if(runtime.allocations.begin(), runtime.allocations.end(),
                                      [id = range.allocation_id](const auto &entry) { return entry.second->id == id; });
      if (found == runtime.allocations.end()) return GEMSTONE_HSA_PACKET_FATAL;
      if (range.size == 0 || range.offset > found->second->size || range.size > found->second->size - range.offset)
        return GEMSTONE_HSA_PACKET_FATAL;
      dirty.push_back(DirtyAllocationRange{found->second, range.offset, range.size});
    }
  }
  if (trace_packets) {
    for (const auto &range : dirty)
      std::fprintf(stderr, "Gemstone dirty AQL queue=%lu packet=%lu allocation=%lu offset=%lu bytes=%u\n", queue_id, packet_id,
                   range.allocation->id, range.offset, range.size);
  }
  for (const auto &range : dirty)
    if (!download_allocation_range(memory, *range.allocation, range.offset, range.size)) return GEMSTONE_HSA_PACKET_FATAL;
  if (trace_packets) std::fprintf(stderr, "Gemstone mirrored AQL queue=%lu packet=%lu ranges=%zu\n", queue_id, packet_id, dirty.size());
  int64_t before = 0;
  int64_t after = 0;
  const int before_loaded = signals.load_acquire(signals.context, completion, &before);
  const int completed = signals.complete_release(signals.context, completion);
  const int after_loaded = signals.load_acquire(signals.context, completion, &after);
  const bool notified = completed == 0 && notify_signal_event(completion);
  if (trace_packets)
    std::fprintf(stderr,
                 "Gemstone forwarded AQL completion queue=%lu packet=%lu signal=%lu before=%s%ld complete=%d after=%s%ld "
                 "notify=%d\n",
                 queue_id, packet_id, completion, before_loaded == 0 ? "" : "error/", before, completed, after_loaded == 0 ? "" : "error/",
                 after, notified ? 1 : 0);
  return completed == 0 && notified ? GEMSTONE_HSA_PACKET_ACCEPTED : GEMSTONE_HSA_PACKET_FATAL;
}

HSAKMT_STATUS register_allocation(void *base, uint64_t size, HsaMemFlags flags, HSA_POINTER_TYPE type, uint32_t node, bool owned) {
  const auto address = reinterpret_cast<uintptr_t>(base);
  if (base == nullptr || size == 0 || size > std::numeric_limits<uintptr_t>::max() - address) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  uint64_t id = 0;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (require_open(runtime) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
    for (const auto &[_, allocation] : runtime.allocations) {
      const auto other = reinterpret_cast<uintptr_t>(allocation->base);
      if (address < other + allocation->size && other < address + size) return HSAKMT_STATUS_MEMORY_ALREADY_REGISTERED;
    }
    id = runtime.next_allocation++;
  }
  if (runtime.memory.register_allocation(runtime.memory.context, id, size) != 0) return HSAKMT_STATUS_KERNEL_COMMUNICATION_ERROR;
  if (gemstone_hsa_aql_runtime_register_allocation(runtime.runtime, reinterpret_cast<uint64_t>(base), size, id) != 0) {
    runtime.memory.release_allocation(runtime.memory.context, id);
    return HSAKMT_STATUS_ERROR;
  }
  std::lock_guard<std::mutex> guard(runtime.lock);
  runtime.allocations.emplace(base, std::make_shared<Allocation>(base, size, id, flags, type, node, owned));
  return HSAKMT_STATUS_SUCCESS;
}

HSAKMT_STATUS wait_events(HsaEvent *events[], HSAuint32 count, bool all, HSAuint32 milliseconds, uint64_t *age) {
  if (events == nullptr || count == 0) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  const auto deadline =
      std::chrono::steady_clock::now()
      + std::chrono::milliseconds(milliseconds == HSA_EVENTTIMEOUT_INFINITE ? std::numeric_limits<int32_t>::max() : milliseconds);
  for (;;) {
    bool any_signaled = false;
    bool all_signaled = true;
    {
      std::lock_guard<std::mutex> state_guard(runtime.lock);
      for (HSAuint32 index = 0; index < count; ++index) {
        const auto found = runtime.events.find(events[index]);
        if (found == runtime.events.end()) return HSAKMT_STATUS_INVALID_HANDLE;
        std::lock_guard<std::mutex> event_guard(found->second->lock);
        any_signaled = any_signaled || found->second->signaled;
        all_signaled = all_signaled && found->second->signaled;
      }
      if ((all && all_signaled) || (!all && any_signaled)) {
        for (HSAuint32 index = 0; index < count; ++index) {
          auto &event = *runtime.events.at(events[index]);
          std::lock_guard<std::mutex> event_guard(event.lock);
          if (event.signaled && !event.manual_reset) event.signaled = false;
          if (age != nullptr) age[index] = event.age;
        }
        return HSAKMT_STATUS_SUCCESS;
      }
    }
    if (milliseconds == HSA_EVENTTIMEOUT_IMMEDIATE || std::chrono::steady_clock::now() >= deadline) return HSAKMT_STATUS_WAIT_TIMEOUT;
    std::this_thread::sleep_for(std::chrono::microseconds(50));
  }
}

} // namespace

extern "C" int gemstone_hsakmt_install_backend(gemstone_hsa_aql_runtime *runtime, const gemstone_hsakmt_memory_transport *memory) {
  if (runtime == nullptr || memory == nullptr || !valid_memory_transport(*memory)) return -1;
  auto &global = state();
  std::lock_guard<std::mutex> guard(global.lock);
  if (opened(global)) return -1;
  global.runtime = runtime;
  global.memory = *memory;
  global.installed_backend = true;
  return 0;
}

extern "C" int gemstone_hsakmt_remove_backend(void) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (opened(runtime)) return -1;
  runtime.runtime = nullptr;
  runtime.memory = {};
  runtime.installed_backend = false;
  return 0;
}

extern "C" int gemstone_hsakmt_notify_code_object(const void *code_object, uint64_t code_object_size,
                                                  const gemstone_hsakmt_loaded_kernel *kernels, uint32_t kernel_count) {
  auto &runtime = state();
  if (code_object == nullptr || code_object_size == 0 || code_object_size > std::numeric_limits<uint32_t>::max()
      || (kernel_count != 0 && kernels == nullptr)) {
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.loader_error = "invalid ROCr code-object notification";
    return -1;
  }

  std::vector<gemstone::hsa::CodeObjectKernelMetadata> metadata;
  std::string parse_error;
  if (!gemstone::hsa::parse_code_object_metadata(code_object, static_cast<size_t>(code_object_size), metadata, parse_error)) {
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.loader_error = std::move(parse_error);
    return -1;
  }

  gemstone_hsa_aql_runtime *aql = nullptr;
  std::vector<uint32_t> pending;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (!opened(runtime) || runtime.runtime == nullptr) {
      runtime.loader_error = "Gemstone hsakmt is not open";
      return -1;
    }
    aql = runtime.runtime;
    for (uint32_t index = 0; index < kernel_count; ++index) {
      if (kernels[index].kernel_object == 0 || kernels[index].name == nullptr || kernels[index].name[0] == '\0') {
        runtime.loader_error = "ROCr reported an invalid loaded kernel symbol";
        return -1;
      }
      if (runtime.notified_kernels.count(kernels[index].kernel_object) == 0) pending.push_back(index);
    }
  }

  std::vector<gemstone_hsa_kernel_binding> bindings;
  bindings.reserve(pending.size());
  for (uint32_t index : pending) {
    const auto &loaded = kernels[index];
    const auto *kernel = find_kernel_metadata(metadata, loaded.name);
    if (kernel == nullptr || kernel->kernarg_size != loaded.kernarg_size) {
      std::lock_guard<std::mutex> guard(runtime.lock);
      runtime.loader_error = kernel == nullptr ? std::string("AMDGPU metadata is absent for loaded kernel ") + loaded.name
                                               : std::string("AMDGPU kernarg schema disagrees with loaded kernel ") + loaded.name;
      return -1;
    }
    bindings.push_back({loaded.kernel_object, kernel->name.c_str(), kernel->kernarg_size, kernel->fields.data(),
                        static_cast<uint32_t>(kernel->fields.size())});
  }
  if (!bindings.empty()
      && gemstone_hsa_aql_runtime_install_code_object(aql, code_object, static_cast<uint32_t>(code_object_size), bindings.data(),
                                                      static_cast<uint32_t>(bindings.size()))
             != 0) {
    const char *detail = gemstone_hsa_aql_runtime_error(aql);
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.loader_error = detail == nullptr || detail[0] == '\0' ? std::string("execution service rejected AMDGPU code object") : detail;
    const char *dump_path = std::getenv("GEMSTONE_HSA_REJECTED_CODE_OBJECT");
    if (dump_path != nullptr && dump_path[0] != '\0') {
      if (std::FILE *dump = std::fopen(dump_path, "wb")) {
        std::fwrite(code_object, 1, static_cast<size_t>(code_object_size), dump);
        std::fclose(dump);
      }
    }
    std::fprintf(stderr, "Gemstone rejected AMDGPU code object: %s\n", runtime.loader_error.c_str());
    return -1;
  }
  for (uint32_t index : pending) {
    const auto &loaded = kernels[index];
    if (std::getenv("GEMSTONE_HSA_TRACE_LOADERS") != nullptr)
      std::fprintf(stderr, "Gemstone installed AMDGPU kernel name=%s kernel_object=0x%lx\n", loaded.name, loaded.kernel_object);
  }

  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    for (uint32_t index : pending)
      runtime.notified_kernels.insert(kernels[index].kernel_object);
    runtime.loader_error.clear();
  }
  return 0;
}

extern "C" int gemstone_hsakmt_notify_internal_kernel(const void *code_object, uint64_t code_object_size, uint64_t kernel_object,
                                                      const char *target, const char *name, uint32_t kernarg_size,
                                                      uint32_t group_segment_size, uint32_t private_segment_size,
                                                      uint32_t compute_pgm_rsrc2, uint32_t kernel_code_properties,
                                                      const gemstone_hsa_kernarg_field *fields, uint32_t field_count) {
  auto &runtime = state();
  const auto printable = [](const char *value) {
    if (value == nullptr || value[0] == '\0') return false;
    for (const unsigned char *cursor = reinterpret_cast<const unsigned char *>(value); *cursor != '\0'; ++cursor)
      if (*cursor < 0x20 || *cursor > 0x7e) return false;
    return true;
  };
  if (code_object == nullptr || code_object_size == 0 || code_object_size > std::numeric_limits<uint32_t>::max() || kernel_object == 0
      || !printable(target) || !printable(name) || kernarg_size == 0 || (field_count != 0 && fields == nullptr)) {
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.loader_error = "invalid ROCr internal-kernel notification";
    return -1;
  }

  gemstone_hsa_aql_runtime *aql = nullptr;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    if (!opened(runtime) || runtime.runtime == nullptr) {
      runtime.loader_error = "Gemstone hsakmt is not open";
      return -1;
    }
    if (runtime.notified_kernels.count(kernel_object) != 0) return 0;
    aql = runtime.runtime;
  }

  const size_t target_size = std::strlen(target);
  const size_t name_size = std::strlen(name);
  constexpr size_t header_size = 40;
  if (target_size > std::numeric_limits<uint32_t>::max() || name_size > std::numeric_limits<uint32_t>::max()
      || code_object_size > std::numeric_limits<size_t>::max() - header_size - target_size - name_size) {
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.loader_error = "ROCr internal-kernel envelope is too large";
    return -1;
  }
  const size_t envelope_size = header_size + target_size + name_size + static_cast<size_t>(code_object_size);
  if (envelope_size > std::numeric_limits<uint32_t>::max()) {
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.loader_error = "ROCr internal-kernel envelope is too large";
    return -1;
  }
  std::vector<unsigned char> envelope(envelope_size);
  const unsigned char magic[8] = {'G', 'E', 'M', 'A', 'M', 'D', '1', '\0'};
  std::memcpy(envelope.data(), magic, sizeof(magic));
  const auto write_u32 = [&envelope](size_t offset, uint32_t value) {
    for (uint32_t index = 0; index < 4; ++index)
      envelope[offset + index] = static_cast<unsigned char>((value >> (index * 8)) & 0xff);
  };
  write_u32(8, static_cast<uint32_t>(target_size));
  write_u32(12, static_cast<uint32_t>(name_size));
  write_u32(16, kernarg_size);
  write_u32(20, group_segment_size);
  write_u32(24, private_segment_size);
  write_u32(28, compute_pgm_rsrc2);
  write_u32(32, kernel_code_properties);
  write_u32(36, static_cast<uint32_t>(code_object_size));
  std::memcpy(envelope.data() + header_size, target, target_size);
  std::memcpy(envelope.data() + header_size + target_size, name, name_size);
  std::memcpy(envelope.data() + header_size + target_size + name_size, code_object, static_cast<size_t>(code_object_size));

  if (gemstone_hsa_aql_runtime_install_kernel(aql, kernel_object, envelope.data(), static_cast<uint32_t>(envelope.size()), name,
                                              kernarg_size, fields, field_count)
      != 0) {
    const char *detail = gemstone_hsa_aql_runtime_error(aql);
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.loader_error = detail == nullptr || detail[0] == '\0' ? std::string("execution service rejected ROCr internal kernel") : detail;
    return -1;
  }

  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    runtime.notified_kernels.insert(kernel_object);
    runtime.loader_error.clear();
  }
  if (std::getenv("GEMSTONE_HSA_TRACE_LOADERS") != nullptr)
    std::fprintf(stderr, "Gemstone installed ROCr internal kernel target=%s name=%s kernel_object=0x%lx\n", target, name, kernel_object);
  return 0;
}

extern "C" const char *gemstone_hsakmt_loader_error(void) {
  thread_local std::string snapshot;
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  snapshot = runtime.loader_error;
  return snapshot.c_str();
}

extern "C" HSAKMT_STATUS hsaKmtOpenKFD(void) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (runtime.open_count++ != 0) return HSAKMT_STATUS_SUCCESS;
  if (!runtime.installed_backend) {
    runtime.client = gemstone_execution_client_create_from_environment();
    const auto signals = gemstone_hsa_rocr_signal_operations();
    runtime.runtime = gemstone_hsa_aql_runtime_create_from_environment(&signals);
    if (runtime.client == nullptr || runtime.runtime == nullptr) {
      if (runtime.client != nullptr) gemstone_execution_client_destroy(runtime.client);
      if (runtime.runtime != nullptr) gemstone_hsa_aql_runtime_destroy(runtime.runtime);
      runtime.client = nullptr;
      runtime.runtime = nullptr;
      runtime.open_count = 0;
      return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
    }
    if (gemstone_execution_attest(runtime.client) != 0) {
      gemstone_hsa_aql_runtime_destroy(runtime.runtime);
      gemstone_execution_client_destroy(runtime.client);
      runtime.runtime = nullptr;
      runtime.client = nullptr;
      runtime.open_count = 0;
      return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
    }
    runtime.memory =
        gemstone_hsakmt_memory_transport{runtime.client, environment_register, environment_release, environment_write, environment_read};
  }
  runtime.kfd = gemstone_hsa_virtual_kfd_create(128);
  if (runtime.kfd == nullptr) {
    runtime.open_count = 0;
    if (!runtime.installed_backend) {
      gemstone_hsa_aql_runtime_destroy(runtime.runtime);
      gemstone_execution_client_destroy(runtime.client);
      runtime.runtime = nullptr;
      runtime.client = nullptr;
      runtime.memory = {};
    }
    return HSAKMT_STATUS_NO_MEMORY;
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtCloseKFD(void) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (!opened(runtime)) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  if (--runtime.open_count != 0) return HSAKMT_STATUS_SUCCESS;
  if (!runtime.queues.empty() || !runtime.allocations.empty() || !runtime.events.empty()) {
    ++runtime.open_count;
    return HSAKMT_STATUS_UNAVAILABLE;
  }
  for (uint64_t kernel_object : runtime.notified_kernels)
    gemstone_hsa_aql_runtime_remove_kernel(runtime.runtime, kernel_object);
  runtime.notified_kernels.clear();
  runtime.loader_error.clear();
  gemstone_hsa_virtual_kfd_destroy(runtime.kfd);
  runtime.kfd = nullptr;
  if (!runtime.installed_backend) {
    gemstone_hsa_aql_runtime_destroy(runtime.runtime);
    gemstone_execution_client_destroy(runtime.client);
    runtime.runtime = nullptr;
    runtime.client = nullptr;
    runtime.memory = {};
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetVersion(HsaVersionInfo *version) {
  if (version == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  if (require_open(state()) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  version->KernelInterfaceMajorVersion = 1;
  // ROCr disables interrupt-event waits for the 1.0 interface and polls the
  // signal value instead. That is the only honest mode until the replacement
  // KMT wires packet completion into KFD exception events.
  version->KernelInterfaceMinorVersion = 0;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtRuntimeEnable(void *, bool) {
  if (require_open(state()) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  // This call advertises KFD's runtime-debug/exception-event integration to
  // ROCr.  Returning success while exposing only ordinary userspace events
  // makes ROCr register a second queue exception handler against an event
  // substrate we do not implement.  A 1.0 virtual KFD must honestly decline
  // that capability; normal queue inactive signals remain supported.
  return HSAKMT_STATUS_NOT_SUPPORTED;
}

extern "C" HSAKMT_STATUS hsaKmtRuntimeDisable(void) { return require_open(state()); }

extern "C" HSAKMT_STATUS hsaKmtGetRuntimeCapabilities(HSAuint32 *caps) {
  if (caps == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  *caps = 0;
  return require_open(state());
}

extern "C" HSAKMT_STATUS hsaKmtAcquireSystemProperties(HsaSystemProperties *properties) {
  if (properties == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  if (require_open(state()) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  *properties = {};
  properties->NumNodes = 2;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtReleaseSystemProperties(void) { return require_open(state()); }

extern "C" HSAKMT_STATUS hsaKmtGetNodeProperties(HSAuint32 node, HsaNodeProperties *properties) {
  if ((node != kCpuNode && node != kGpuNode) || properties == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  if (require_open(state()) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  *properties = {};
  if (node == kCpuNode) {
    properties->NumCPUCores = std::max(1U, std::thread::hardware_concurrency());
    properties->NumMemoryBanks = 1;
    properties->LocalMemSize = kAvailableMemory;
    return HSAKMT_STATUS_SUCCESS;
  }
  properties->NumFComputeCores = 16;
  properties->NumMemoryBanks = 3;
  properties->MaxWavesPerSIMD = 10;
  properties->LDSSizeInKB = 64;
  properties->WaveFrontSize = 64;
  properties->NumShaderBanks = 1;
  properties->NumArrays = 1;
  properties->NumCUPerArray = 4;
  properties->NumSIMDPerCU = 4;
  properties->MaxSlotsScratchCU = 32;
  const auto version = gemstone::hsa::target_version();
  properties->EngineId.ui32.Major = version.major;
  properties->EngineId.ui32.Minor = version.minor;
  properties->VendorId = 0x1002;
  properties->DeviceId = 0x6860;
  properties->LocalMemSize = kAvailableMemory;
  properties->MaxEngineClockMhzFCompute = 1500;
  properties->MaxEngineClockMhzCCompute = 3000;
  properties->DrmRenderMinor = -1;
  properties->Integrated = 0;
  properties->NumCpQueues = 128;
  properties->NumXcc = 1;
  properties->KFDGpuID = 1;
  properties->Capability.ui32.HSAMMUPresent = 1;
  properties->Capability.ui32.QueueSizePowerOfTwo = 1;
  properties->Capability.ui32.DoorbellType = 2;
  properties->Capability.ui32.CoherentHostAccess = 1;
  const char *name = gemstone::hsa::target_arch();
  const size_t name_size = std::min(std::strlen(name), sizeof(properties->AMDName) - 1U);
  std::memcpy(properties->AMDName, name, name_size);
  properties->AMDName[name_size] = 0;
  const char *marketing = "Gemstone Virtual AMDGPU";
  for (size_t index = 0; marketing[index] != '\0' && index + 1 < HSA_PUBLIC_NAME_SIZE; ++index)
    properties->MarketingName[index] = static_cast<HSAuint16>(marketing[index]);
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetNodeMemoryProperties(HSAuint32 node, HSAuint32 count, HsaMemoryProperties *properties) {
  const auto expected = node == kCpuNode ? 1U : node == kGpuNode ? 3U : 0U;
  if (count != expected || count == 0 || properties == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  std::fill_n(properties, count, HsaMemoryProperties{});
  if (node == kCpuNode) {
    properties[0].HeapType = HSA_HEAPTYPE_SYSTEM;
    properties[0].SizeInBytes = kAvailableMemory;
    properties[0].Width = 256;
    properties[0].MemoryClockMax = 1000;
  } else {
    properties[0].HeapType = HSA_HEAPTYPE_FRAME_BUFFER_PUBLIC;
    properties[0].SizeInBytes = kAvailableMemory;
    properties[0].Width = 256;
    properties[0].MemoryClockMax = 1000;
    properties[1].HeapType = HSA_HEAPTYPE_GPU_LDS;
    properties[1].SizeInBytes = UINT64_C(64) * 1024;
    properties[1].VirtualBaseAddress = UINT64_C(0x100000000000);
    properties[2].HeapType = HSA_HEAPTYPE_GPU_SCRATCH;
    properties[2].SizeInBytes = UINT64_C(4) * 1024 * 1024 * 1024;
    properties[2].VirtualBaseAddress = UINT64_C(0x200000000000);
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetNodeCacheProperties(HSAuint32 node, HSAuint32, HSAuint32 count, HsaCacheProperties *properties) {
  (void)properties;
  return (node == kCpuNode || node == kGpuNode) && count == 0 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_PARAMETER;
}

extern "C" HSAKMT_STATUS hsaKmtGetNodeIoLinkProperties(HSAuint32 node, HSAuint32 count, HsaIoLinkProperties *properties) {
  (void)properties;
  return (node == kCpuNode || node == kGpuNode) && count == 0 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_PARAMETER;
}

extern "C" HSAKMT_STATUS hsaKmtSetXNACKMode(HSAint32) { return HSAKMT_STATUS_NOT_SUPPORTED; }

extern "C" HSAKMT_STATUS hsaKmtGetXNACKMode(HSAint32 *enabled) {
  if (enabled == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  *enabled = 0;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtModelEnabled(bool *enabled) {
  if (enabled == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  *enabled = true;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetClockCounters(HSAuint32 node, HsaClockCounters *counters) {
  if (node != kGpuNode || counters == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  const auto now = static_cast<uint64_t>(
      std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now().time_since_epoch()).count());
  counters->GPUClockCounter = now;
  counters->CPUClockCounter = now;
  counters->SystemClockCounter = now;
  counters->SystemClockFrequencyHz = UINT64_C(1000000000);
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetAMDGPUDeviceHandle(HSAuint32 node, HsaAMDGPUDeviceHandle *handle) {
  if (node != kGpuNode || handle == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  *handle = nullptr;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtSetMemoryPolicy(HSAuint32 node, HSAuint32, HSAuint32, void *, HSAuint64) {
  return node == kGpuNode ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_NODE_UNIT;
}

extern "C" HSAKMT_STATUS hsaKmtAvailableMemory(HSAuint32 node, HSAuint64 *available) {
  if (node != kGpuNode || available == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  *available = kAvailableMemory;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtAllocMemoryAlign(HSAuint32 node, HSAuint64 size, HSAuint64 alignment, HsaMemFlags flags, void **address) {
  if ((node != kCpuNode && node != kGpuNode) || size == 0 || address == nullptr || flags.ui32.FixedAddress || flags.ui32.OnlyAddress)
    return HSAKMT_STATUS_INVALID_PARAMETER;
  const uint64_t requested_alignment = alignment == 0 ? kPageSize : alignment;
  if (requested_alignment < kPageSize || (requested_alignment & (requested_alignment - 1)) != 0
      || size > std::numeric_limits<size_t>::max())
    return HSAKMT_STATUS_MEMORY_ALIGNMENT;
  void *memory = nullptr;
  if (posix_memalign(&memory, static_cast<size_t>(requested_alignment), static_cast<size_t>(size)) != 0) return HSAKMT_STATUS_NO_MEMORY;
  std::memset(memory, 0, static_cast<size_t>(size));
  const auto result = register_allocation(memory, size, flags, HSA_POINTER_ALLOCATED, node, true);
  if (result != HSAKMT_STATUS_SUCCESS) {
    std::free(memory);
    return result;
  }
  *address = memory;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtAllocMemory(HSAuint32 node, HSAuint64 size, HsaMemFlags flags, void **address) {
  return hsaKmtAllocMemoryAlign(node, size, kPageSize, flags, address);
}

extern "C" HSAKMT_STATUS hsaKmtRegisterMemoryWithFlags(void *address, HSAuint64 size, HsaMemFlags flags) {
  return register_allocation(address, size, flags, HSA_POINTER_REGISTERED_USER, kCpuNode, false);
}

extern "C" HSAKMT_STATUS hsaKmtRegisterMemory(void *address, HSAuint64 size) {
  HsaMemFlags flags{};
  flags.ui32.HostAccess = 1;
  return hsaKmtRegisterMemoryWithFlags(address, size, flags);
}

extern "C" HSAKMT_STATUS hsaKmtRegisterMemoryToNodes(void *address, HSAuint64 size, HSAuint64 node_count, HSAuint32 *nodes) {
  if (node_count != 1 || nodes == nullptr || nodes[0] != kGpuNode) return HSAKMT_STATUS_INVALID_NODE_UNIT;
  return hsaKmtRegisterMemory(address, size);
}

extern "C" HSAKMT_STATUS hsaKmtDeregisterMemory(void *address) {
  auto &runtime = state();
  AllocationRef allocation;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.allocations.find(address);
    if (found == runtime.allocations.end() || found->second->owned) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
    if (found->second.use_count() != 1) return HSAKMT_STATUS_UNAVAILABLE;
    allocation = found->second;
    runtime.allocations.erase(found);
  }
  gemstone_hsa_aql_runtime_unregister_allocation(runtime.runtime, reinterpret_cast<uint64_t>(allocation->base));
  return runtime.memory.release_allocation(runtime.memory.context, allocation->id) == 0 ? HSAKMT_STATUS_SUCCESS
                                                                                        : HSAKMT_STATUS_KERNEL_COMMUNICATION_ERROR;
}

extern "C" HSAKMT_STATUS hsaKmtFreeMemory(void *address, HSAuint64 size) {
  auto &runtime = state();
  AllocationRef allocation;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.allocations.find(address);
    if (found == runtime.allocations.end() || !found->second->owned || found->second->size != size) {
      if (std::getenv("GEMSTONE_HSA_TRACE_PACKETS") != nullptr)
        std::fprintf(stderr, "Gemstone rejected free address=%p bytes=%lu registered=%d owned=%d registered_bytes=%lu\n", address, size,
                     found != runtime.allocations.end(), found != runtime.allocations.end() && found->second->owned,
                     found == runtime.allocations.end() ? 0 : found->second->size);
      return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
    }
    if (found->second.use_count() != 1) return HSAKMT_STATUS_UNAVAILABLE;
    allocation = found->second;
    runtime.allocations.erase(found);
  }
  gemstone_hsa_aql_runtime_unregister_allocation(runtime.runtime, reinterpret_cast<uint64_t>(allocation->base));
  const int released = runtime.memory.release_allocation(runtime.memory.context, allocation->id);
  std::free(allocation->base);
  return released == 0 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_KERNEL_COMMUNICATION_ERROR;
}

extern "C" HSAKMT_STATUS hsaKmtMapMemoryToGPU(void *address, HSAuint64 size, HSAuint64 *alternate) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  auto *allocation = find_allocation(runtime, address);
  if (allocation == nullptr) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  const auto offset = reinterpret_cast<uintptr_t>(address) - reinterpret_cast<uintptr_t>(allocation->base);
  if (size == 0 || size > allocation->size - offset || allocation->map_count == std::numeric_limits<uint32_t>::max())
    return HSAKMT_STATUS_INVALID_PARAMETER;
  if (alternate != nullptr) *alternate = 0;
  if (!upload_changed_allocation(runtime.memory, *allocation)) return HSAKMT_STATUS_KERNEL_COMMUNICATION_ERROR;
  ++allocation->map_count;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtMapMemoryToGPUNodes(void *address, HSAuint64 size, HSAuint64 *alternate, HsaMemMapFlags,
                                                   HSAuint64 node_count, HSAuint32 *nodes) {
  if (node_count != 1 || nodes == nullptr || nodes[0] != kGpuNode) return HSAKMT_STATUS_INVALID_NODE_UNIT;
  return hsaKmtMapMemoryToGPU(address, size, alternate);
}

extern "C" HSAKMT_STATUS hsaKmtUnmapMemoryToGPU(void *address) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  auto *allocation = find_allocation(runtime, address);
  if (allocation == nullptr || allocation->map_count == 0) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  --allocation->map_count;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtQueryPointerInfo(const void *pointer, HsaPointerInfo *info) {
  if (pointer == nullptr || info == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto *allocation = find_allocation(runtime, pointer);
  if (allocation == nullptr) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  *info = {};
  info->Type = allocation->type;
  info->Node = allocation->node;
  info->MemFlags = allocation->flags;
  info->CPUAddress = allocation->base;
  info->GPUAddress = reinterpret_cast<uint64_t>(allocation->base);
  info->SizeInBytes = allocation->size;
  info->UserData = allocation->user_data;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtSetMemoryUserData(const void *pointer, void *user_data) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  auto *allocation = find_allocation(runtime, pointer);
  if (allocation == nullptr) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  allocation->user_data = user_data;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtSVMSetAttr(void *address, HSAuint64 size, unsigned int count, HSA_SVM_ATTRIBUTE *attributes) {
  if (address == nullptr || size == 0 || count == 0 || attributes == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto *allocation = find_allocation(runtime, address);
  if (allocation == nullptr) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  const auto offset = reinterpret_cast<uintptr_t>(address) - reinterpret_cast<uintptr_t>(allocation->base);
  return size <= allocation->size - offset ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_PARAMETER;
}

extern "C" HSAKMT_STATUS hsaKmtCreateEvent(HsaEventDescriptor *, bool manual_reset, bool signaled, HsaEvent **result) {
  if (result == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto record = std::make_unique<Event>();
  auto &runtime = state();
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    record->value.EventId = runtime.next_event++;
    record->value.EventData.HWData2 = reinterpret_cast<uint64_t>(&record->mailbox);
    record->manual_reset = manual_reset;
    record->signaled = signaled;
    auto *event = &record->value;
    runtime.events.emplace(event, std::move(record));
    *result = event;
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtDestroyEvent(HsaEvent *event) {
  if (event == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  return runtime.events.erase(event) == 1 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_HANDLE;
}

extern "C" HSAKMT_STATUS hsaKmtSetEvent(HsaEvent *event) {
  auto &runtime = state();
  std::lock_guard<std::mutex> state_guard(runtime.lock);
  const auto found = runtime.events.find(event);
  if (found == runtime.events.end()) return HSAKMT_STATUS_INVALID_HANDLE;
  {
    std::lock_guard<std::mutex> event_guard(found->second->lock);
    found->second->signaled = true;
    ++found->second->age;
  }
  found->second->changed.notify_all();
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtResetEvent(HsaEvent *event) {
  auto &runtime = state();
  std::lock_guard<std::mutex> state_guard(runtime.lock);
  const auto found = runtime.events.find(event);
  if (found == runtime.events.end()) return HSAKMT_STATUS_INVALID_HANDLE;
  std::lock_guard<std::mutex> event_guard(found->second->lock);
  found->second->signaled = false;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtQueryEventState(HsaEvent *event) {
  auto &runtime = state();
  std::lock_guard<std::mutex> state_guard(runtime.lock);
  const auto found = runtime.events.find(event);
  if (found == runtime.events.end()) return HSAKMT_STATUS_INVALID_HANDLE;
  std::lock_guard<std::mutex> event_guard(found->second->lock);
  return found->second->signaled ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_WAIT_TIMEOUT;
}

extern "C" HSAKMT_STATUS hsaKmtWaitOnEvent(HsaEvent *event, HSAuint32 milliseconds) {
  HsaEvent *events[]{event};
  return wait_events(events, 1, true, milliseconds, nullptr);
}

extern "C" HSAKMT_STATUS hsaKmtWaitOnEvent_Ext(HsaEvent *event, HSAuint32 milliseconds, uint64_t *age) {
  HsaEvent *events[]{event};
  return wait_events(events, 1, true, milliseconds, age);
}

extern "C" HSAKMT_STATUS hsaKmtWaitOnMultipleEvents(HsaEvent *events[], HSAuint32 count, bool all, HSAuint32 milliseconds) {
  return wait_events(events, count, all, milliseconds, nullptr);
}

extern "C" HSAKMT_STATUS hsaKmtWaitOnMultipleEvents_Ext(HsaEvent *events[], HSAuint32 count, bool all, HSAuint32 milliseconds,
                                                        uint64_t *age) {
  return wait_events(events, count, all, milliseconds, age);
}

extern "C" HSAKMT_STATUS hsaKmtCreateQueueExt(HSAuint32 node, HSA_QUEUE_TYPE type, HSAuint32, HSA_QUEUE_PRIORITY, HSAuint32, void *ring,
                                              HSAuint64 size, HsaEvent *, HsaQueueResource *resource) {
  if (node != kGpuNode || type != HSA_QUEUE_COMPUTE_AQL || ring == nullptr || size < 64 || size % 64 != 0 || resource == nullptr)
    return HSAKMT_STATUS_NOT_SUPPORTED;
  auto record = std::make_unique<Queue>();
  record->ring = ring;
  record->ring_size = size;
  // ROCr supplies pointers embedded in its public queue before entering the
  // thunk.  Monitoring private replacement counters would never observe
  // hsa_queue_add_write_index_* and therefore cannot implement stock AQL.
  auto *read_pointer = resource->Queue_read_ptr_aql;
  auto *write_pointer = resource->Queue_write_ptr_aql;
  auto &runtime = state();
  record->queue = gemstone_hsa_virtual_kfd_create_aql_queue(runtime.kfd, ring, static_cast<size_t>(size / 64), read_pointer, write_pointer,
                                                            consume_packet, record.get(), &record->resource);
  if (record->queue == nullptr) return HSAKMT_STATUS_OUT_OF_RESOURCES;
  const HSA_QUEUEID id = record->resource.queue_id;
  *resource = {};
  resource->QueueId = id;
  resource->Queue_DoorBell_aql = record->resource.doorbell;
  resource->Queue_write_ptr_aql = record->resource.write_pointer;
  resource->Queue_read_ptr_aql = record->resource.read_pointer;
  resource->ErrorReason = &record->error_reason;
  std::lock_guard<std::mutex> guard(runtime.lock);
  runtime.queues.emplace(id, std::move(record));
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtCreateQueue(HSAuint32 node, HSA_QUEUE_TYPE type, HSAuint32 percentage, HSA_QUEUE_PRIORITY priority,
                                           void *ring, HSAuint64 size, HsaEvent *event, HsaQueueResource *resource) {
  return hsaKmtCreateQueueExt(node, type, percentage, priority, 0, ring, size, event, resource);
}

extern "C" HSAKMT_STATUS hsaKmtDestroyQueue(HSA_QUEUEID id) {
  auto &runtime = state();
  std::unique_ptr<Queue> queue;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    const auto found = runtime.queues.find(id);
    if (found == runtime.queues.end()) return HSAKMT_STATUS_INVALID_HANDLE;
    queue = std::move(found->second);
    runtime.queues.erase(found);
  }
  gemstone_hsa_virtual_kfd_destroy_queue(queue->queue);
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtUpdateQueue(HSA_QUEUEID id, HSAuint32, HSA_QUEUE_PRIORITY, void *ring, HSAuint64 size, HsaEvent *) {
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  const auto found = runtime.queues.find(id);
  if (found == runtime.queues.end()) return HSAKMT_STATUS_INVALID_HANDLE;
  return found->second->ring == ring && found->second->ring_size == size ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_NOT_SUPPORTED;
}

extern "C" HSAKMT_STATUS hsaKmtSetQueueCUMask(HSA_QUEUEID id, HSAuint32 count, HSAuint32 *mask) {
  if (count == 0 || mask == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  return runtime.queues.count(id) == 1 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_HANDLE;
}

extern "C" HSAKMT_STATUS hsaKmtGetQueueInfo(HSA_QUEUEID id, HsaQueueInfo *info) {
  if (info == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::lock_guard<std::mutex> guard(runtime.lock);
  if (runtime.queues.count(id) != 1) return HSAKMT_STATUS_INVALID_HANDLE;
  *info = {};
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtSetTrapHandler(HSAuint32 node, void *, HSAuint64, void *, HSAuint64) {
  return node == kGpuNode ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_NODE_UNIT;
}

extern "C" HSAKMT_STATUS hsaKmtGetTileConfig(HSAuint32 node, HsaGpuTileConfig *config) {
  if (node != kGpuNode || config == nullptr) return HSAKMT_STATUS_INVALID_PARAMETER;
  config->NumTileConfigs = 0;
  config->NumMacroTileConfigs = 0;
  config->GbAddrConfig = 0;
  config->NumBanks = 1;
  config->NumRanks = 1;
  return HSAKMT_STATUS_SUCCESS;
}
