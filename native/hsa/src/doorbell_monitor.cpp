#include "gemstone/hsa/doorbell_monitor.hpp"

#include <algorithm>
#include <array>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdio>
#include <mutex>
#include <thread>
#include <utility>
#include <vector>

#include <stdint.h>

#include <aspartame/vector.hpp>

#include "gemstone/hsa/aql.hpp"
#include <gemstone/trace.hpp>

namespace gemstone::hsa {

namespace {

using namespace aspartame;

constexpr uint64_t UnrungDoorbell = UINT64_MAX;
constexpr uint16_t PacketTypeMask = 0xff;
constexpr uint32_t SpinPasses = 64;
constexpr auto MaximumSleep = std::chrono::microseconds(50);

struct alignas(64) DoorbellSlot {
  uint64_t value = UnrungDoorbell;
};

} // namespace

struct QueueState {
  uint64_t id = 0;
  void *ring = nullptr;
  size_t ringPackets = 0;
  PacketConsumer consume;
  alignas(8) uint64_t ownedRead = 0;
  alignas(8) uint64_t ownedWrite = 0;
  uint64_t *readIndex = &ownedRead;
  uint64_t *writeIndex = &ownedWrite;
  DoorbellSlot *doorbell = nullptr;
  size_t slot = 0;
  std::atomic<bool> active{true};
  std::atomic<uint32_t> scans{0};
  std::atomic<bool> tracedArmed{false};
  std::mutex consumeLock;
  std::mutex scanLock;
  std::condition_variable scanFinished;
};

namespace {

struct ScanGuard {
  explicit ScanGuard(QueueState &state) : queue(state) { queue.scans.fetch_add(1, std::memory_order_acq_rel); }

  ~ScanGuard() {
    if (queue.scans.fetch_sub(1, std::memory_order_acq_rel) == 1) {
      std::lock_guard<std::mutex> guard(queue.scanLock);
      queue.scanFinished.notify_all();
    }
  }

  QueueState &queue;
};

bool scan(QueueState &queue) {
  ScanGuard guard(queue);
  std::unique_lock consumeGuard(queue.consumeLock, std::try_to_lock);
  if (!consumeGuard.owns_lock() || !queue.active.load(std::memory_order_acquire)) return false;

  // KFD doorbells are MMIO and notify hardware on every write. A userspace
  // replacement cannot observe a write edge to ordinary memory. The first
  // non-sentinel value arms the queue; thereafter ring publication and the
  // write index are polled so repeated doorbell values cannot lose work.
  const uint64_t doorbell = __atomic_load_n(&queue.doorbell->value, __ATOMIC_ACQUIRE);
  if (doorbell == UnrungDoorbell) return false;

  const auto packetAt = [&](uint64_t index) {
    return static_cast<unsigned char *>(queue.ring) + (index & (queue.ringPackets - 1)) * AqlPacketSize;
  };
  uint64_t next = __atomic_load_n(queue.readIndex, __ATOMIC_ACQUIRE);
  uint64_t end = __atomic_load_n(queue.writeIndex, __ATOMIC_ACQUIRE);
  const bool trace = traceEnabled("hsa", "packets");
  if (trace && !queue.tracedArmed.exchange(true, std::memory_order_relaxed)) {
    const uint16_t header = __atomic_load_n(reinterpret_cast<const uint16_t *>(packetAt(next)), __ATOMIC_ACQUIRE);
    std::fprintf(stderr, "Gemstone armed AQL queue=%lu doorbell=%lu read=%lu write=%lu header=0x%04x\n", queue.id, doorbell, next, end,
                 header);
  }
  end = std::min(end, next + queue.ringPackets);
  bool progressed = false;

  while (next < end && queue.active.load(std::memory_order_acquire)) {
    auto *packet = packetAt(next);
    auto *headerAddress = reinterpret_cast<uint16_t *>(packet);

    // AQL producers publish the header last with release ordering. An
    // INVALID header is a legitimate multi-producer hole and stops this
    // in-order consumer until its owner publishes it.
    const uint16_t header = __atomic_load_n(headerAddress, __ATOMIC_ACQUIRE);
    if ((header & PacketTypeMask) == static_cast<uint16_t>(PacketType::Invalid)) break;

    const auto result = queue.consume(queue.id, next, packet);
    if (result == PacketResult::Retry) break;
    if (result == PacketResult::Fatal) {
      queue.active.store(false, std::memory_order_release);
      break;
    }

    // Invalidation is required at wrap-around: a reserved but unpublished
    // packet must not be mistaken for the valid packet from the previous lap.
    const auto invalid =
        static_cast<uint16_t>((header & static_cast<uint16_t>(~PacketTypeMask)) | static_cast<uint16_t>(PacketType::Invalid));
    __atomic_store_n(headerAddress, invalid, __ATOMIC_RELEASE);
    ++next;
    __atomic_store_n(queue.readIndex, next, __ATOMIC_RELEASE);
    if (trace) std::fprintf(stderr, "Gemstone advanced AQL queue=%lu read=%lu\n", queue.id, next);
    progressed = true;
  }
  return progressed;
}

} // namespace

struct DoorbellMonitor::Impl {
  std::array<DoorbellSlot, MaxQueues> slots{};
  std::array<bool, MaxQueues> used{};
  std::atomic<uint64_t> nextQueue{1};
  std::mutex lock;
  std::condition_variable changed;
  std::vector<std::shared_ptr<QueueState>> queues;
  bool stopping = false;
  std::vector<std::thread> workers;
};

AqlQueue::AqlQueue(DoorbellMonitor &monitor, std::shared_ptr<QueueState> state) : monitor(monitor), state(std::move(state)) {}

AqlQueue::~AqlQueue() { monitor.unregister(state); }

uint64_t AqlQueue::id() const { return state->id; }
uint64_t *AqlQueue::doorbell() const { return &state->doorbell->value; }
uint64_t *AqlQueue::readIndex() const { return state->readIndex; }
uint64_t *AqlQueue::writeIndex() const { return state->writeIndex; }
bool AqlQueue::active() const { return state->active.load(std::memory_order_acquire); }
void AqlQueue::inactivate() { state->active.store(false, std::memory_order_release); }

DoorbellMonitor::DoorbellMonitor() : impl(std::make_unique<Impl>()) {
  for (unsigned index = 0; index < 2; ++index)
    impl->workers.emplace_back(&DoorbellMonitor::run, this);
}

DoorbellMonitor::~DoorbellMonitor() {
  {
    std::lock_guard<std::mutex> guard(impl->lock);
    impl->stopping = true;
  }
  impl->changed.notify_all();
  for (auto &worker : impl->workers)
    worker.join();
}

void DoorbellMonitor::run() {
  uint32_t emptyPasses = 0;
  for (;;) {
    std::vector<std::shared_ptr<QueueState>> snapshot;
    {
      std::unique_lock<std::mutex> guard(impl->lock);
      impl->changed.wait(guard, [&] { return impl->stopping || !impl->queues.empty(); });
      if (impl->stopping) return;
      snapshot = impl->queues;
    }
    bool progressed = false;
    for (const auto &queue : snapshot)
      if (queue->active.load(std::memory_order_acquire) && scan(*queue)) progressed = true;
    if (progressed) emptyPasses = 0;
    else if (emptyPasses++ < SpinPasses) std::this_thread::yield();
    else {
      emptyPasses = 0;
      std::this_thread::sleep_for(MaximumSleep);
    }
  }
}

std::unique_ptr<AqlQueue> DoorbellMonitor::registerQueue(void *ring, size_t ringPackets, PacketConsumer consume, uint64_t *readIndex,
                                                         uint64_t *writeIndex) {
  if (!ring || !consume || ringPackets == 0 || (ringPackets & (ringPackets - 1)) != 0) return nullptr;
  try {
    auto state = std::make_shared<QueueState>();
    state->id = impl->nextQueue.fetch_add(1, std::memory_order_relaxed);
    state->ring = ring;
    state->ringPackets = ringPackets;
    state->consume = std::move(consume);
    if (readIndex) state->readIndex = readIndex;
    if (writeIndex) state->writeIndex = writeIndex;
    std::unique_ptr<AqlQueue> queue(new AqlQueue(*this, state));
    {
      std::lock_guard<std::mutex> guard(impl->lock);
      const auto free = std::find(impl->used.begin(), impl->used.end(), false);
      if (free == impl->used.end() || impl->stopping) return nullptr;
      state->slot = static_cast<size_t>(free - impl->used.begin());
      *free = true;
      state->doorbell = &impl->slots[state->slot];
      __atomic_store_n(&state->doorbell->value, UnrungDoorbell, __ATOMIC_RELAXED);
      impl->queues.push_back(state);
    }
    impl->changed.notify_all();
    return queue;
  } catch (...) {
    return nullptr;
  }
}

void DoorbellMonitor::unregister(const std::shared_ptr<QueueState> &state) {
  if (!state->doorbell) return;
  state->active.store(false, std::memory_order_release);
  {
    std::lock_guard<std::mutex> guard(impl->lock);
    impl->queues ^= filter([&](const auto &candidate) { return candidate != state; });
    impl->used[state->slot] = false;
  }
  std::unique_lock<std::mutex> guard(state->scanLock);
  state->scanFinished.wait(guard, [&] { return state->scans.load(std::memory_order_acquire) == 0; });
}

} // namespace gemstone::hsa
