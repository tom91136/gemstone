#include <algorithm>
#include <array>
#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <memory>
#include <mutex>
#include <string>
#include <thread>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>

#include <aspartame/unordered_map.hpp>
#include <aspartame/vector.hpp>
#include <hsakmt/hsakmt.h>

#include "gemstone/hsa/aql.hpp"
#include "gemstone/hsa/aql_execution.hpp"
#include "gemstone/hsa/doorbell_monitor.hpp"
#include "gemstone/hsa/target.hpp"
#include "gemstone_execution_rpc.hpp"
#include <gemstone/trace.hpp>

#ifdef GEMSTONE_HSAKMT_TEST_SUPPORT
  #include <span>

  #include <gemstone/environment.hpp>
  #include <gemstone/process.hpp>

  #include "hsakmt_test_support.hpp"
  #include <environment_keys.hpp>
#endif
#include "transfer.hpp"

namespace {

using namespace aspartame;
using namespace gemstone::execution;
using gemstone::hsa::AqlPacketSize;
using gemstone::hsa::AqlQueue;
using gemstone::hsa::AqlRuntime;
using gemstone::hsa::DoorbellMonitor;
using gemstone::hsa::PacketResult;
using gemstone::hsa::PacketType;

constexpr uint32_t CpuNode = 0;
constexpr uint32_t GpuNode = 1;

constexpr uint32_t memoryBanks(uint32_t node) {
  if (node == CpuNode) return 1;
  if (node == GpuNode) return 3;
  return 0;
}
// Keep the advertised virtual aperture below ROCr's eager scratch-reservation threshold.
constexpr uint64_t AvailableMemory = uint64_t{1} * 1024 * 1024 * 1024;
constexpr uint32_t PageSize = 4096;
constexpr uint32_t UploadChunk = 64U * 1024U;

struct Allocation {
  Allocation(void *allocationBase, uint64_t allocationSize, uint64_t allocationId, HsaMemFlags allocationFlags,
             HSA_POINTER_TYPE allocationType, uint32_t allocationNode, bool allocationOwned)
      : base(allocationBase), size(allocationSize), id(allocationId), flags(allocationFlags), type(allocationType), node(allocationNode),
        owned(allocationOwned) {}

  void *base = nullptr;
  uint64_t size = 0;
  uint64_t id = 0;
  HsaMemFlags flags{};
  HSA_POINTER_TYPE type = HSA_POINTER_UNKNOWN;
  uint32_t node = CpuNode;
  void *userData = nullptr;
  bool owned = false;
  uint32_t mapCount = 0;
  std::mutex memoryLock;
  std::vector<unsigned char> uploadSnapshot;
  bool uploadSnapshotValid = false;
};

using AllocationRef = std::shared_ptr<Allocation>;

struct Event {
  HsaEvent value{};
  alignas(8) uint64_t mailbox = 0;
  bool manualReset = false;
  bool signalled = false;
  uint64_t age = 0;
  std::mutex lock;
};

struct Queue {
  std::unique_ptr<AqlQueue> aql;
  void *ring = nullptr;
  uint64_t ringSize = 0;
  alignas(8) int64_t errorReason = 0;
};

struct alignas(8) DeferredSignal {
  uint64_t kind = 0;
  int64_t value = 1;
};
static_assert(offsetof(DeferredSignal, value) == 8);

struct SignalAbiView {
  uint64_t kind;
  int64_t value;
  uint64_t eventMailbox;
  uint32_t eventId;
  uint32_t reserved;
};
static_assert(offsetof(SignalAbiView, value) == 8);
static_assert(offsetof(SignalAbiView, eventId) == 24);

struct State {
  std::mutex lock;
  uint32_t openCount = 0;
  uint32_t nextEvent = 1;
  std::unique_ptr<Client> client;
  std::unique_ptr<AqlRuntime> runtime;
  std::unique_ptr<DoorbellMonitor> monitor;
  std::unordered_map<void *, AllocationRef> allocations;
  std::unordered_map<HsaEvent *, std::unique_ptr<Event>> events;
  std::unordered_map<HSA_QUEUEID, std::unique_ptr<Queue>> queues;
  std::unordered_set<uint64_t> notifiedKernels;
  std::string loaderError;
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

bool tracePackets() { return gemstone::traceEnabled("hsa", "packets"); }

bool opened(const State &runtime) { return runtime.openCount != 0; }

HSAKMT_STATUS requireOpen(const State &runtime) {
  return opened(runtime) ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
}

Allocation *findAllocation(State &runtime, const void *pointer) {
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  for (auto &[_, allocation] : runtime.allocations) {
    const auto base = reinterpret_cast<uintptr_t>(allocation->base);
    if (address >= base && address - base < allocation->size) return allocation.get();
  }
  return nullptr;
}

bool prepareUploadSnapshot(Allocation &allocation) {
  if (allocation.size > std::numeric_limits<size_t>::max()) return false;
  if (allocation.uploadSnapshot.size() == allocation.size) return true;
  try {
    allocation.uploadSnapshot.resize(static_cast<size_t>(allocation.size));
  } catch (...) {
    allocation.uploadSnapshot.clear();
    allocation.uploadSnapshotValid = false;
    return false;
  }
  allocation.uploadSnapshotValid = false;
  return true;
}

bool uploadChangedAllocation(Client &client, Allocation &allocation) {
  std::scoped_lock const guard(allocation.memoryLock);
  if (!prepareUploadSnapshot(allocation)) return false;
  const bool snapshotValid = allocation.uploadSnapshotValid;
  for (uint64_t offset = 0; offset < allocation.size;) {
    const auto chunk = static_cast<uint32_t>(std::min<uint64_t>(allocation.size - offset, UploadChunk));
    const auto *bytes = static_cast<const unsigned char *>(allocation.base) + offset;
    auto *snapshot = allocation.uploadSnapshot.data() + offset;
    if (!snapshotValid || std::memcmp(bytes, snapshot, chunk) != 0) {
      if (!uploadAllocation(client, AllocationId{static_cast<int64_t>(allocation.id)}, offset, bytes, chunk, chunk)) {
        allocation.uploadSnapshotValid = false;
        return false;
      }
      std::memcpy(snapshot, bytes, chunk);
    }
    offset += chunk;
  }
  allocation.uploadSnapshotValid = true;
  return true;
}

bool downloadAllocationRange(Client &client, Allocation &allocation, uint64_t offset, uint32_t size) {
  std::scoped_lock const guard(allocation.memoryLock);
  if (size == 0 || offset > allocation.size || size > allocation.size - offset) return false;
  if (!prepareUploadSnapshot(allocation)) return false;
  if (!allocation.uploadSnapshotValid) {
    std::memcpy(allocation.uploadSnapshot.data(), allocation.base, static_cast<size_t>(allocation.size));
    allocation.uploadSnapshotValid = true;
  }
  auto *bytes = static_cast<unsigned char *>(allocation.base) + offset;
  if (!downloadAllocation(client, AllocationId{static_cast<int64_t>(allocation.id)}, offset, bytes, size, TransferChunk)) {
    allocation.uploadSnapshotValid = false;
    return false;
  }
  std::memcpy(allocation.uploadSnapshot.data() + offset, bytes, size);
  return true;
}

bool notifySignalEvent(uint64_t signal) {
  if (signal == 0) return false;
  const auto *view = reinterpret_cast<const SignalAbiView *>(static_cast<uintptr_t>(signal));
  const uint32_t eventId = __atomic_load_n(&view->eventId, __ATOMIC_ACQUIRE);
  const bool tracing = tracePackets();
  if (eventId == 0) {
    if (tracing) std::fprintf(stderr, "Gemstone completion signal=%lu has no event\n", signal);
    return true;
  }
  auto &runtime = state();
  std::scoped_lock const stateGuard(runtime.lock);
  const auto found = runtime.events ^ find_cref([eventId](const auto &, const auto &event) { return event->value.EventId == eventId; });
  if (!found) {
    if (tracing) std::fprintf(stderr, "Gemstone completion signal=%lu references unknown event=%u\n", signal, eventId);
    return false;
  }
  auto &event = *found->get().second;
  {
    std::scoped_lock const eventGuard(event.lock);
    __atomic_store_n(&event.mailbox, static_cast<uint64_t>(eventId), __ATOMIC_RELEASE);
    event.signalled = true;
    ++event.age;
  }
  if (tracing) std::fprintf(stderr, "Gemstone notified completion signal=%lu event=%u\n", signal, eventId);
  return true;
}

PacketResult consumePacket(uint64_t queueId, uint64_t packetId, const void *packet) {
  auto &runtime = state();
  const bool tracing = tracePackets();
  if (tracing) std::fprintf(stderr, "Gemstone consuming AQL queue=%lu packet=%lu\n", queueId, packetId);
  gemstone::hsa::AqlPacket decoded;
  const auto decode = gemstone::hsa::decodeAqlPacket(packet, AqlPacketSize, decoded);
  std::vector<AllocationRef> retained;
  std::vector<AllocationRef> accessible;
  AqlRuntime *aql = nullptr;
  Client *client = nullptr;
  {
    std::scoped_lock const guard(runtime.lock);
    aql = runtime.runtime.get();
    client = runtime.client.get();
    if (decode == gemstone::hsa::DecodeStatus::Success && decoded.type == PacketType::KernelDispatch) {
      retained.reserve(runtime.allocations.size());
      accessible.reserve(runtime.allocations.size());
      for (auto &[_, allocation] : runtime.allocations) {
        retained.push_back(allocation);
        // CPU-node allocations back ROCr's fine-grained system and kernarg
        // regions. They are mutable by the producer between packets and are
        // GPU-visible without hsaKmtMapMemoryToGPU, so refresh them before
        // every kernel dispatch. Barriers and virtual PM4 synchronisation do
        // not access memory; the next kernel refresh establishes visibility
        // for any intervening producer writes. GPU-node allocations are
        // uploaded by MapMemoryToGPU and thereafter remain
        // remote-authoritative until completion copy-back; refreshing them
        // here would erase writes made by completion-less AQL dispatches.
        if (allocation->node == CpuNode) accessible.push_back(allocation);
      }
    }
  }
  if (decode != gemstone::hsa::DecodeStatus::Success) {
    const auto result = aql->consume(packet);
    if (tracing)
      std::fprintf(stderr, "Gemstone rejected AQL decode queue=%lu packet=%lu decode=%s error=%s\n", queueId, packetId,
                   gemstone::hsa::describe(decode), aql->lastError().c_str());
    return result;
  }
  for (const auto &allocation : accessible)
    if (!uploadChangedAllocation(*client, *allocation)) return PacketResult::Fatal;
  if (tracing) std::fprintf(stderr, "Gemstone uploaded AQL queue=%lu packet=%lu allocations=%zu\n", queueId, packetId, accessible.size());
  const uint64_t completion = decoded.completionSignal;
  if (completion == 0) {
    const auto result = aql->consume(packet);
    if (tracing && result != PacketResult::Accepted)
      std::fprintf(stderr, "Gemstone rejected completion-less AQL queue=%lu packet=%lu kernel_object=0x%lx error=%s\n", queueId, packetId,
                   decoded.dispatch.kernelObject, aql->lastError().c_str());
    return result;
  }

  // AQL execution ordinarily publishes its completion before returning.  The
  // virtual transport must first mirror remote writes back into ROCr's native
  // allocation, so execute against a private signal and forward the result
  // only after copy-back.  This establishes the same release/acquire ordering
  // promised by a physical GPU completion signal.
  DeferredSignal deferred;
  std::array<unsigned char, AqlPacketSize> deferredPacket{};
  std::memcpy(deferredPacket.data(), packet, deferredPacket.size());
  const uint64_t deferredHandle = reinterpret_cast<uint64_t>(&deferred);
  std::memcpy(deferredPacket.data() + 56, &deferredHandle, sizeof(deferredHandle));
  const auto result = aql->consume(deferredPacket.data());
  if (tracing) std::fprintf(stderr, "Gemstone executed AQL queue=%lu packet=%lu result=%d\n", queueId, packetId, static_cast<int>(result));
  if (result != PacketResult::Accepted) return result;
  const int64_t outcome = __atomic_load_n(&deferred.value, __ATOMIC_ACQUIRE);
  gemstone::hsa::RocrSignals signals;
  if (outcome < 0)
    return signals.fail(completion, static_cast<int32_t>(outcome)) && notifySignalEvent(completion) ? PacketResult::Accepted
                                                                                                    : PacketResult::Fatal;
  if (outcome != 0) return PacketResult::Fatal;
  struct DirtyAllocationRange {
    AllocationRef allocation;
    uint64_t offset;
    uint32_t size;
  };
  std::vector<DirtyAllocationRange> dirty;
  {
    const auto ranges = aql->takeDirtyRanges();
    std::scoped_lock const guard(runtime.lock);
    dirty.reserve(ranges.size());
    for (const auto &range : ranges) {
      const auto id = static_cast<uint64_t>(range.allocation.value);
      const auto found = runtime.allocations ^ find_cref([id](const auto &, const auto &allocation) { return allocation->id == id; });
      if (!found) return PacketResult::Fatal;
      dirty.push_back(DirtyAllocationRange{.allocation = found->get().second,
                                           .offset = static_cast<uint64_t>(range.offset.value),
                                           .size = static_cast<uint32_t>(range.size.value)});
    }
  }
  if (tracing) {
    for (const auto &range : dirty)
      std::fprintf(stderr, "Gemstone dirty AQL queue=%lu packet=%lu allocation=%lu offset=%lu bytes=%u\n", queueId, packetId,
                   range.allocation->id, range.offset, range.size);
  }
  for (const auto &range : dirty)
    if (!downloadAllocationRange(*client, *range.allocation, range.offset, range.size)) return PacketResult::Fatal;
  if (tracing) std::fprintf(stderr, "Gemstone mirrored AQL queue=%lu packet=%lu ranges=%zu\n", queueId, packetId, dirty.size());
  const bool completed = signals.complete(completion);
  const bool notified = completed && notifySignalEvent(completion);
  if (tracing)
    std::fprintf(stderr, "Gemstone forwarded AQL completion queue=%lu packet=%lu signal=%lu complete=%d notify=%d\n", queueId, packetId,
                 completion, completed ? 1 : 0, notified ? 1 : 0);
  return notified ? PacketResult::Accepted : PacketResult::Fatal;
}

HSAKMT_STATUS registerAllocation(void *base, uint64_t size, HsaMemFlags flags, HSA_POINTER_TYPE type, uint32_t node, bool owned) {
  const auto address = reinterpret_cast<uintptr_t>(base);
  if (!base || size == 0 || size > std::numeric_limits<uintptr_t>::max() - address) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  {
    std::scoped_lock const guard(runtime.lock);
    if (requireOpen(runtime) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
    const bool overlaps = runtime.allocations ^ exists([&](const void *, const AllocationRef &allocation) {
                            const auto other = reinterpret_cast<uintptr_t>(allocation->base);
                            return address < other + allocation->size && other < address + size;
                          });
    if (overlaps) return HSAKMT_STATUS_MEMORY_ALREADY_REGISTERED;
  }
  const auto id = gemstone::execution::registerAllocation(*runtime.client, size, base);
  if (!id) return HSAKMT_STATUS_KERNEL_COMMUNICATION_ERROR;
  if (!runtime.runtime->registerAllocation(address, size, static_cast<uint64_t>(id->value))) {
    (void)runtime.client->call(ReleaseAllocation{*id});
    return HSAKMT_STATUS_ERROR;
  }
  std::scoped_lock const guard(runtime.lock);
  runtime.allocations.emplace(base, std::make_shared<Allocation>(base, size, static_cast<uint64_t>(id->value), flags, type, node, owned));
  return HSAKMT_STATUS_SUCCESS;
}

HSAKMT_STATUS releaseAllocation(State &runtime, const Allocation &allocation) {
  runtime.runtime->unregisterAllocation(reinterpret_cast<uint64_t>(allocation.base));
  return runtime.client->call(ReleaseAllocation{AllocationId{static_cast<int64_t>(allocation.id)}})
             ? HSAKMT_STATUS_SUCCESS
             : HSAKMT_STATUS_KERNEL_COMMUNICATION_ERROR;
}

HSAKMT_STATUS waitEvents(HsaEvent *events[], HSAuint32 count, bool all, HSAuint32 milliseconds, uint64_t *age) {
  if (!events || count == 0) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  const auto deadline =
      std::chrono::steady_clock::now()
      + std::chrono::milliseconds(milliseconds == HSA_EVENTTIMEOUT_INFINITE ? std::numeric_limits<int32_t>::max() : milliseconds);
  for (;;) {
    bool anySignalled = false;
    bool allSignalled = true;
    {
      std::scoped_lock const stateGuard(runtime.lock);
      for (HSAuint32 index = 0; index < count; ++index) {
        const auto found = runtime.events.find(events[index]);
        if (found == runtime.events.end()) return HSAKMT_STATUS_INVALID_HANDLE;
        std::scoped_lock const eventGuard(found->second->lock);
        anySignalled = anySignalled || found->second->signalled;
        allSignalled = allSignalled && found->second->signalled;
      }
      if ((all && allSignalled) || (!all && anySignalled)) {
        for (HSAuint32 index = 0; index < count; ++index) {
          auto &event = *runtime.events.at(events[index]);
          std::scoped_lock const eventGuard(event.lock);
          if (event.signalled && !event.manualReset) event.signalled = false;
          if (age) age[index] = event.age;
        }
        return HSAKMT_STATUS_SUCCESS;
      }
    }
    if (milliseconds == HSA_EVENTTIMEOUT_IMMEDIATE || std::chrono::steady_clock::now() >= deadline) return HSAKMT_STATUS_WAIT_TIMEOUT;
    std::this_thread::sleep_for(std::chrono::microseconds(50));
  }
}

} // namespace

#ifdef GEMSTONE_HSAKMT_TEST_SUPPORT
bool gemstone::hsakmt::installCodeObject(std::span<const uint8_t> image, std::span<const LoadedKernel> kernels) {
  auto &runtime = state();
  const auto reject = [&](std::string message) {
    std::scoped_lock const guard(runtime.lock);
    runtime.loaderError = std::move(message);
    return false;
  };
  if (image.empty() || image.size() > std::numeric_limits<uint32_t>::max()) return reject("invalid ROCr code-object notification");
  AqlRuntime *aql = nullptr;
  std::vector<gemstone::hsa::KernelSymbol> pending;
  {
    std::scoped_lock const guard(runtime.lock);
    if (!opened(runtime)) {
      runtime.loaderError = "Gemstone hsakmt is not open";
      return false;
    }
    aql = runtime.runtime.get();
    for (const auto &kernel : kernels) {
      if (kernel.object == 0 || kernel.name.empty()) {
        runtime.loaderError = "ROCr reported an invalid loaded kernel symbol";
        return false;
      }
      if (!runtime.notifiedKernels.contains(kernel.object)) pending.push_back({.object = kernel.object, .name = kernel.name});
    }
  }
  if (!pending.empty() && !aql->installCodeObject(image, pending)) {
    const auto detail = aql->lastError();
    if (const char *dump = gemstone::environment::value(gemstone::environment::HsaRejectedCodeObject); dump && dump[0] != '\0')
      (void)gemstone::writeFile(dump, image.data(), image.size());
    std::fprintf(stderr, "Gemstone rejected AMDGPU code object: %s\n", detail.c_str());
    return reject(detail.empty() ? "execution service rejected AMDGPU code object" : detail);
  }
  for (const auto &kernel : kernels) {
    if (!(pending ^ exists([&](const gemstone::hsa::KernelSymbol &symbol) { return symbol.object == kernel.object; }))) continue;
    const auto layout = aql->layout(kernel.object);
    if (!layout || layout->kernargSize != kernel.kernargSize) {
      for (const auto &symbol : pending)
        (void)aql->removeKernel(symbol.object);
      return reject("execution manifest disagrees with loaded kernarg for kernel " + kernel.name);
    }
  }
  if (gemstone::traceEnabled("hsa", "loaders"))
    for (const auto &symbol : pending)
      std::fprintf(stderr, "Gemstone installed AMDGPU kernel name=%s kernel_object=0x%lx\n", symbol.name.c_str(), symbol.object);
  std::scoped_lock const guard(runtime.lock);
  for (const auto &symbol : pending)
    runtime.notifiedKernels.insert(symbol.object);
  runtime.loaderError.clear();
  return true;
}

std::string gemstone::hsakmt::loaderError() {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  return runtime.loaderError;
}
#endif

extern "C" HSAKMT_STATUS hsaKmtOpenKFD() {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  if (runtime.openCount++ != 0) return HSAKMT_STATUS_SUCCESS;
  const auto reset = [&](HSAKMT_STATUS status) {
    runtime.monitor.reset();
    runtime.runtime.reset();
    runtime.client.reset();
    runtime.openCount = 0;
    return status;
  };
  runtime.client = Client::fromEnvironment();
  runtime.runtime = AqlRuntime::fromEnvironment(std::make_unique<gemstone::hsa::RocrSignals>());
  if (!runtime.client || !runtime.runtime || !runtime.client->call(Ping{})) return reset(HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED);
  try {
    runtime.monitor = std::make_unique<DoorbellMonitor>();
  } catch (...) {
    return reset(HSAKMT_STATUS_NO_MEMORY);
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtCloseKFD() {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  if (!opened(runtime)) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  if (--runtime.openCount != 0) return HSAKMT_STATUS_SUCCESS;
  if (!runtime.queues.empty() || !runtime.allocations.empty() || !runtime.events.empty()) {
    ++runtime.openCount;
    return HSAKMT_STATUS_UNAVAILABLE;
  }
  for (uint64_t const kernelObject : runtime.notifiedKernels)
    runtime.runtime->removeKernel(kernelObject);
  runtime.notifiedKernels.clear();
  runtime.loaderError.clear();
  runtime.monitor.reset();
  runtime.runtime.reset();
  runtime.client.reset();
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetVersion(HsaVersionInfo *version) {
  if (!version) return HSAKMT_STATUS_INVALID_PARAMETER;
  if (requireOpen(state()) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  version->KernelInterfaceMajorVersion = 1;
  // ROCr disables interrupt-event waits for the 1.0 interface and polls the
  // signal value instead. That is the only honest mode until the replacement
  // KMT wires packet completion into KFD exception events.
  version->KernelInterfaceMinorVersion = 0;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtRuntimeEnable(void *, bool) {
  if (requireOpen(state()) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  // This call advertises KFD's runtime-debug/exception-event integration to
  // ROCr.  Returning success while exposing only ordinary userspace events
  // makes ROCr register a second queue exception handler against an event
  // substrate we do not implement.  A 1.0 virtual KFD must honestly decline
  // that capability; normal queue inactive signals remain supported.
  return HSAKMT_STATUS_NOT_SUPPORTED;
}

extern "C" HSAKMT_STATUS hsaKmtRuntimeDisable() { return requireOpen(state()); }

extern "C" HSAKMT_STATUS hsaKmtGetRuntimeCapabilities(HSAuint32 *caps) {
  if (!caps) return HSAKMT_STATUS_INVALID_PARAMETER;
  *caps = 0;
  return requireOpen(state());
}

extern "C" HSAKMT_STATUS hsaKmtAcquireSystemProperties(HsaSystemProperties *properties) {
  if (!properties) return HSAKMT_STATUS_INVALID_PARAMETER;
  if (requireOpen(state()) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  *properties = {};
  properties->NumNodes = 2;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtReleaseSystemProperties() { return requireOpen(state()); }

extern "C" HSAKMT_STATUS hsaKmtGetNodeProperties(HSAuint32 node, HsaNodeProperties *properties) {
  if ((node != CpuNode && node != GpuNode) || !properties) return HSAKMT_STATUS_INVALID_PARAMETER;
  if (requireOpen(state()) != HSAKMT_STATUS_SUCCESS) return HSAKMT_STATUS_KERNEL_IO_CHANNEL_NOT_OPENED;
  *properties = {};
  if (node == CpuNode) {
    properties->NumCPUCores = std::max(1U, std::thread::hardware_concurrency());
    properties->NumMemoryBanks = memoryBanks(CpuNode);
    properties->LocalMemSize = AvailableMemory;
    return HSAKMT_STATUS_SUCCESS;
  }
  properties->NumFComputeCores = 16;
  properties->NumMemoryBanks = memoryBanks(GpuNode);
  properties->MaxWavesPerSIMD = 10;
  properties->LDSSizeInKB = 64;
  properties->WaveFrontSize = gemstone::hsa::waveSize();
  properties->NumShaderBanks = 1;
  properties->NumArrays = 1;
  properties->NumCUPerArray = 4;
  properties->NumSIMDPerCU = 4;
  properties->MaxSlotsScratchCU = 32;
  const auto version = gemstone::hsa::targetVersion();
  properties->EngineId.ui32.Major = version.major;
  properties->EngineId.ui32.Minor = version.minor;
  properties->VendorId = 0x1002;
  properties->DeviceId = 0x6860;
  properties->LocalMemSize = AvailableMemory;
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
  const char *name = gemstone::hsa::targetArch();
  const size_t nameSize = std::min(std::strlen(name), sizeof(properties->AMDName) - 1U);
  std::memcpy(properties->AMDName, name, nameSize);
  properties->AMDName[nameSize] = 0;
  const char *marketing = "Gemstone Virtual AMDGPU";
  for (size_t index = 0; marketing[index] != '\0' && index + 1 < HSA_PUBLIC_NAME_SIZE; ++index)
    properties->MarketingName[index] = static_cast<HSAuint16>(static_cast<unsigned char>(marketing[index]));
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetNodeMemoryProperties(HSAuint32 node, HSAuint32 count, HsaMemoryProperties *properties) {
  const auto expected = memoryBanks(node);
  if (count != expected || count == 0 || !properties) return HSAKMT_STATUS_INVALID_PARAMETER;
  std::fill_n(properties, count, HsaMemoryProperties{});
  properties[0].HeapType = node == CpuNode ? HSA_HEAPTYPE_SYSTEM : HSA_HEAPTYPE_FRAME_BUFFER_PUBLIC;
  properties[0].SizeInBytes = AvailableMemory;
  properties[0].Width = 256;
  properties[0].MemoryClockMax = 1000;
  if (node == GpuNode) {
    properties[1].HeapType = HSA_HEAPTYPE_GPU_LDS;
    properties[1].SizeInBytes = uint64_t{64} * 1024;
    properties[1].VirtualBaseAddress = uint64_t{0x100000000000};
    properties[2].HeapType = HSA_HEAPTYPE_GPU_SCRATCH;
    properties[2].SizeInBytes = uint64_t{4} * 1024 * 1024 * 1024;
    properties[2].VirtualBaseAddress = uint64_t{0x200000000000};
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetNodeCacheProperties(HSAuint32 node, HSAuint32, HSAuint32 count, HsaCacheProperties *) {
  return (node == CpuNode || node == GpuNode) && count == 0 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_PARAMETER;
}

extern "C" HSAKMT_STATUS hsaKmtGetNodeIoLinkProperties(HSAuint32 node, HSAuint32 count, HsaIoLinkProperties *) {
  return (node == CpuNode || node == GpuNode) && count == 0 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_PARAMETER;
}

extern "C" HSAKMT_STATUS hsaKmtSetXNACKMode(HSAint32) { return HSAKMT_STATUS_NOT_SUPPORTED; }

extern "C" HSAKMT_STATUS hsaKmtGetXNACKMode(HSAint32 *enabled) {
  if (!enabled) return HSAKMT_STATUS_INVALID_PARAMETER;
  *enabled = 0;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtModelEnabled(bool *enabled) {
  if (!enabled) return HSAKMT_STATUS_INVALID_PARAMETER;
  *enabled = true;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetClockCounters(HSAuint32 node, HsaClockCounters *counters) {
  if (node != GpuNode || !counters) return HSAKMT_STATUS_INVALID_PARAMETER;
  const auto now = static_cast<uint64_t>(
      std::chrono::duration_cast<std::chrono::nanoseconds>(std::chrono::steady_clock::now().time_since_epoch()).count());
  counters->GPUClockCounter = now;
  counters->CPUClockCounter = now;
  counters->SystemClockCounter = now;
  counters->SystemClockFrequencyHz = uint64_t{1000000000};
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtGetAMDGPUDeviceHandle(HSAuint32 node, HsaAMDGPUDeviceHandle *handle) {
  if (node != GpuNode || !handle) return HSAKMT_STATUS_INVALID_PARAMETER;
  *handle = nullptr;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtSetMemoryPolicy(HSAuint32 node, HSAuint32, HSAuint32, void *, HSAuint64) {
  return node == GpuNode ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_NODE_UNIT;
}

extern "C" HSAKMT_STATUS hsaKmtAvailableMemory(HSAuint32 node, HSAuint64 *available) {
  if (node != GpuNode || !available) return HSAKMT_STATUS_INVALID_PARAMETER;
  *available = AvailableMemory;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtAllocMemoryAlign(HSAuint32 node, HSAuint64 size, HSAuint64 alignment, HsaMemFlags flags, void **address) {
  if ((node != CpuNode && node != GpuNode) || size == 0 || !address || flags.ui32.FixedAddress || flags.ui32.OnlyAddress)
    return HSAKMT_STATUS_INVALID_PARAMETER;
  const uint64_t requestedAlignment = alignment == 0 ? PageSize : alignment;
  if (requestedAlignment < PageSize || (requestedAlignment & (requestedAlignment - 1)) != 0 || size > std::numeric_limits<size_t>::max())
    return HSAKMT_STATUS_MEMORY_ALIGNMENT;
  void *memory = nullptr;
  if (posix_memalign(&memory, static_cast<size_t>(requestedAlignment), static_cast<size_t>(size)) != 0) return HSAKMT_STATUS_NO_MEMORY;
  std::memset(memory, 0, static_cast<size_t>(size));
  const auto result = registerAllocation(memory, size, flags, HSA_POINTER_ALLOCATED, node, true);
  if (result != HSAKMT_STATUS_SUCCESS) {
    std::free(memory);
    return result;
  }
  *address = memory;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtAllocMemory(HSAuint32 node, HSAuint64 size, HsaMemFlags flags, void **address) {
  return hsaKmtAllocMemoryAlign(node, size, PageSize, flags, address);
}

extern "C" HSAKMT_STATUS hsaKmtRegisterMemoryWithFlags(void *address, HSAuint64 size, HsaMemFlags flags) {
  return registerAllocation(address, size, flags, HSA_POINTER_REGISTERED_USER, CpuNode, false);
}

extern "C" HSAKMT_STATUS hsaKmtRegisterMemory(void *address, HSAuint64 size) {
  HsaMemFlags flags{};
  flags.ui32.HostAccess = 1;
  return hsaKmtRegisterMemoryWithFlags(address, size, flags);
}

extern "C" HSAKMT_STATUS hsaKmtRegisterMemoryToNodes(void *address, HSAuint64 size, HSAuint64 nodeCount, HSAuint32 *nodes) {
  if (nodeCount != 1 || !nodes || nodes[0] != GpuNode) return HSAKMT_STATUS_INVALID_NODE_UNIT;
  return hsaKmtRegisterMemory(address, size);
}

extern "C" HSAKMT_STATUS hsaKmtDeregisterMemory(void *address) {
  auto &runtime = state();
  AllocationRef allocation;
  {
    std::scoped_lock const guard(runtime.lock);
    const auto found = runtime.allocations.find(address);
    if (found == runtime.allocations.end() || found->second->owned) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
    if (found->second.use_count() != 1) return HSAKMT_STATUS_UNAVAILABLE;
    allocation = found->second;
    runtime.allocations.erase(found);
  }
  return releaseAllocation(runtime, *allocation);
}

extern "C" HSAKMT_STATUS hsaKmtFreeMemory(void *address, HSAuint64 size) {
  auto &runtime = state();
  AllocationRef allocation;
  {
    std::scoped_lock const guard(runtime.lock);
    const auto found = runtime.allocations.find(address);
    if (found == runtime.allocations.end() || !found->second->owned || found->second->size != size) {
      if (tracePackets())
        std::fprintf(stderr, "Gemstone rejected free address=%p bytes=%lu registered=%d owned=%d registered_bytes=%lu\n", address, size,
                     found != runtime.allocations.end(), found != runtime.allocations.end() && found->second->owned,
                     found == runtime.allocations.end() ? 0 : found->second->size);
      return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
    }
    if (found->second.use_count() != 1) return HSAKMT_STATUS_UNAVAILABLE;
    allocation = found->second;
    runtime.allocations.erase(found);
  }
  const auto released = releaseAllocation(runtime, *allocation);
  std::free(allocation->base);
  return released;
}

extern "C" HSAKMT_STATUS hsaKmtMapMemoryToGPU(void *address, HSAuint64 size, HSAuint64 *alternate) {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  auto *allocation = findAllocation(runtime, address);
  if (!allocation) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  const auto offset = reinterpret_cast<uintptr_t>(address) - reinterpret_cast<uintptr_t>(allocation->base);
  if (size == 0 || size > allocation->size - offset || allocation->mapCount == std::numeric_limits<uint32_t>::max())
    return HSAKMT_STATUS_INVALID_PARAMETER;
  if (alternate) *alternate = 0;
  if (!uploadChangedAllocation(*runtime.client, *allocation)) return HSAKMT_STATUS_KERNEL_COMMUNICATION_ERROR;
  ++allocation->mapCount;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtMapMemoryToGPUNodes(void *address, HSAuint64 size, HSAuint64 *alternate, HsaMemMapFlags, HSAuint64 nodeCount,
                                                   HSAuint32 *nodes) {
  if (nodeCount != 1 || !nodes || nodes[0] != GpuNode) return HSAKMT_STATUS_INVALID_NODE_UNIT;
  return hsaKmtMapMemoryToGPU(address, size, alternate);
}

extern "C" HSAKMT_STATUS hsaKmtUnmapMemoryToGPU(void *address) {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  auto *allocation = findAllocation(runtime, address);
  if (!allocation || allocation->mapCount == 0) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  --allocation->mapCount;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtQueryPointerInfo(const void *pointer, HsaPointerInfo *info) {
  if (!pointer || !info) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  const auto *allocation = findAllocation(runtime, pointer);
  if (!allocation) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  *info = {};
  info->Type = allocation->type;
  info->Node = allocation->node;
  info->MemFlags = allocation->flags;
  info->CPUAddress = allocation->base;
  info->GPUAddress = reinterpret_cast<uint64_t>(allocation->base);
  info->SizeInBytes = allocation->size;
  info->UserData = allocation->userData;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtSetMemoryUserData(const void *pointer, void *userData) {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  auto *allocation = findAllocation(runtime, pointer);
  if (!allocation) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  allocation->userData = userData;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtSVMSetAttr(void *address, HSAuint64 size, unsigned int count, HSA_SVM_ATTRIBUTE *attributes) {
  if (!address || size == 0 || count == 0 || !attributes) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  const auto *allocation = findAllocation(runtime, address);
  if (!allocation) return HSAKMT_STATUS_MEMORY_NOT_REGISTERED;
  const auto offset = reinterpret_cast<uintptr_t>(address) - reinterpret_cast<uintptr_t>(allocation->base);
  return size <= allocation->size - offset ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_PARAMETER;
}

extern "C" HSAKMT_STATUS hsaKmtCreateEvent(HsaEventDescriptor *, bool manualReset, bool signalled, HsaEvent **result) {
  if (!result) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto record = std::make_unique<Event>();
  auto &runtime = state();
  {
    std::scoped_lock const guard(runtime.lock);
    record->value.EventId = runtime.nextEvent++;
    record->value.EventData.HWData2 = reinterpret_cast<uint64_t>(&record->mailbox);
    record->manualReset = manualReset;
    record->signalled = signalled;
    auto *event = &record->value;
    runtime.events.emplace(event, std::move(record));
    *result = event;
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtDestroyEvent(HsaEvent *event) {
  if (!event) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  return runtime.events.erase(event) == 1 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_HANDLE;
}

extern "C" HSAKMT_STATUS hsaKmtSetEvent(HsaEvent *event) {
  auto &runtime = state();
  std::scoped_lock const stateGuard(runtime.lock);
  const auto found = runtime.events.find(event);
  if (found == runtime.events.end()) return HSAKMT_STATUS_INVALID_HANDLE;
  {
    std::scoped_lock const eventGuard(found->second->lock);
    found->second->signalled = true;
    ++found->second->age;
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtResetEvent(HsaEvent *event) {
  auto &runtime = state();
  std::scoped_lock const stateGuard(runtime.lock);
  const auto found = runtime.events.find(event);
  if (found == runtime.events.end()) return HSAKMT_STATUS_INVALID_HANDLE;
  std::scoped_lock const eventGuard(found->second->lock);
  found->second->signalled = false;
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtQueryEventState(HsaEvent *event) {
  auto &runtime = state();
  std::scoped_lock const stateGuard(runtime.lock);
  const auto found = runtime.events.find(event);
  if (found == runtime.events.end()) return HSAKMT_STATUS_INVALID_HANDLE;
  std::scoped_lock const eventGuard(found->second->lock);
  return found->second->signalled ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_WAIT_TIMEOUT;
}

extern "C" HSAKMT_STATUS hsaKmtWaitOnEvent(HsaEvent *event, HSAuint32 milliseconds) {
  HsaEvent *events[]{event};
  return waitEvents(events, 1, true, milliseconds, nullptr);
}

extern "C" HSAKMT_STATUS hsaKmtWaitOnEvent_Ext(HsaEvent *event, HSAuint32 milliseconds, uint64_t *age) {
  HsaEvent *events[]{event};
  return waitEvents(events, 1, true, milliseconds, age);
}

extern "C" HSAKMT_STATUS hsaKmtWaitOnMultipleEvents(HsaEvent *events[], HSAuint32 count, bool all, HSAuint32 milliseconds) {
  return waitEvents(events, count, all, milliseconds, nullptr);
}

extern "C" HSAKMT_STATUS hsaKmtWaitOnMultipleEvents_Ext(HsaEvent *events[], HSAuint32 count, bool all, HSAuint32 milliseconds,
                                                        uint64_t *age) {
  return waitEvents(events, count, all, milliseconds, age);
}

extern "C" HSAKMT_STATUS hsaKmtCreateQueueExt(HSAuint32 node, HSA_QUEUE_TYPE type, HSAuint32, HSA_QUEUE_PRIORITY, HSAuint32, void *ring,
                                              HSAuint64 size, HsaEvent *, HsaQueueResource *resource) {
  if (node != GpuNode || type != HSA_QUEUE_COMPUTE_AQL || !ring || size < AqlPacketSize || size % AqlPacketSize != 0 || !resource)
    return HSAKMT_STATUS_NOT_SUPPORTED;
  auto record = std::make_unique<Queue>();
  record->ring = ring;
  record->ringSize = size;
  // ROCr supplies pointers embedded in its public queue before entering the
  // thunk.  Monitoring private replacement counters would never observe
  // hsa_queue_add_write_index_* and therefore cannot implement stock AQL.
  auto *readPointer = resource->Queue_read_ptr_aql;
  auto *writePointer = resource->Queue_write_ptr_aql;
  // A freshly-created ROCr ring is empty, but its backing pages are commonly
  // zeroed rather than filled with HSA_PACKET_TYPE_INVALID.  Zero is a valid
  // vendor-specific packet type, so initialise empty slots before arming the
  // monitor; otherwise it can consume an unpublished zero packet as PM4.
  const uint64_t initialRead = !readPointer ? 0 : __atomic_load_n(readPointer, __ATOMIC_ACQUIRE);
  const uint64_t initialWrite = !writePointer ? 0 : __atomic_load_n(writePointer, __ATOMIC_ACQUIRE);
  if (initialRead == initialWrite) {
    for (size_t index = 0; index < static_cast<size_t>(size / AqlPacketSize); ++index)
      __atomic_store_n(reinterpret_cast<uint16_t *>(static_cast<uint8_t *>(ring) + (index * AqlPacketSize)),
                       static_cast<uint16_t>(PacketType::Invalid), __ATOMIC_RELAXED);
  }
  auto &runtime = state();
  record->aql = runtime.monitor->registerQueue(ring, static_cast<size_t>(size / AqlPacketSize), consumePacket, readPointer, writePointer);
  if (!record->aql) return HSAKMT_STATUS_OUT_OF_RESOURCES;
  const HSA_QUEUEID id = record->aql->id();
  *resource = {};
  resource->QueueId = id;
  resource->Queue_DoorBell_aql = record->aql->doorbell();
  resource->Queue_write_ptr_aql = record->aql->writeIndex();
  resource->Queue_read_ptr_aql = record->aql->readIndex();
  resource->ErrorReason = &record->errorReason;
  std::scoped_lock const guard(runtime.lock);
  runtime.queues.emplace(id, std::move(record));
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtCreateQueue(HSAuint32 node, HSA_QUEUE_TYPE type, HSAuint32 percentage, HSA_QUEUE_PRIORITY priority,
                                           void *ring, HSAuint64 size, HsaEvent *event, HsaQueueResource *resource) {
  return hsaKmtCreateQueueExt(node, type, percentage, priority, 0, ring, size, event, resource);
}

extern "C" HSAKMT_STATUS hsaKmtDestroyQueue(HSA_QUEUEID id) {
  auto &runtime = state();
  // Released after the lock: unregistering waits for in-flight packets, which take it.
  std::unique_ptr<Queue> queue;
  {
    std::scoped_lock const guard(runtime.lock);
    const auto found = runtime.queues.find(id);
    if (found == runtime.queues.end()) return HSAKMT_STATUS_INVALID_HANDLE;
    queue = std::move(found->second);
    runtime.queues.erase(found);
  }
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtUpdateQueue(HSA_QUEUEID id, HSAuint32, HSA_QUEUE_PRIORITY, void *ring, HSAuint64 size, HsaEvent *) {
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  const auto found = runtime.queues.find(id);
  if (found == runtime.queues.end()) return HSAKMT_STATUS_INVALID_HANDLE;
  return found->second->ring == ring && found->second->ringSize == size ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_NOT_SUPPORTED;
}

extern "C" HSAKMT_STATUS hsaKmtSetQueueCUMask(HSA_QUEUEID id, HSAuint32 count, HSAuint32 *mask) {
  if (count == 0 || !mask) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  return runtime.queues.count(id) == 1 ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_HANDLE;
}

extern "C" HSAKMT_STATUS hsaKmtGetQueueInfo(HSA_QUEUEID id, HsaQueueInfo *info) {
  if (!info) return HSAKMT_STATUS_INVALID_PARAMETER;
  auto &runtime = state();
  std::scoped_lock const guard(runtime.lock);
  if (runtime.queues.count(id) != 1) return HSAKMT_STATUS_INVALID_HANDLE;
  *info = {};
  return HSAKMT_STATUS_SUCCESS;
}

extern "C" HSAKMT_STATUS hsaKmtSetTrapHandler(HSAuint32 node, void *, HSAuint64, void *, HSAuint64) {
  return node == GpuNode ? HSAKMT_STATUS_SUCCESS : HSAKMT_STATUS_INVALID_NODE_UNIT;
}

extern "C" HSAKMT_STATUS hsaKmtGetTileConfig(HSAuint32 node, HsaGpuTileConfig *config) {
  if (node != GpuNode || !config) return HSAKMT_STATUS_INVALID_PARAMETER;
  config->NumTileConfigs = 0;
  config->NumMacroTileConfigs = 0;
  config->GbAddrConfig = 0;
  config->NumBanks = 1;
  config->NumRanks = 1;
  return HSAKMT_STATUS_SUCCESS;
}
