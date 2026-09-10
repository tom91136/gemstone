// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/doorbell_monitor.h"
#include "gemstone/hsa/trace.h"

#include <algorithm>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <memory>
#include <mutex>
#include <new>
#include <thread>
#include <vector>

namespace {

constexpr uint64_t kUnrungDoorbell = UINT64_MAX;

template <typename T> T load(const T *address, int order) { return __atomic_load_n(address, order); }

template <typename T> void store(T *address, T value, int order) { __atomic_store_n(address, value, order); }

struct alignas(64) DoorbellSlot {
  uint64_t value = kUnrungDoorbell;
};

struct QueueState {
  gemstone_hsa_queue_descriptor descriptor{};
  DoorbellSlot *doorbell = nullptr;
  size_t slot = 0;
  std::atomic<bool> active{true};
  std::atomic<bool> fatal{false};
  std::atomic<uint32_t> scans{0};
  std::atomic<bool> traced_armed{false};
  std::mutex scan_lock;
  std::condition_variable scan_finished;
};

struct ScanGuard {
  explicit ScanGuard(QueueState &state) : queue(state) { queue.scans.fetch_add(1, std::memory_order_acq_rel); }

  ~ScanGuard() {
    if (queue.scans.fetch_sub(1, std::memory_order_acq_rel) == 1) {
      std::lock_guard<std::mutex> guard(queue.scan_lock);
      queue.scan_finished.notify_all();
    }
  }

  QueueState &queue;
};

} // namespace

struct gemstone_hsa_doorbell_monitor {
  explicit gemstone_hsa_doorbell_monitor(const gemstone_hsa_doorbell_monitor_config &requested)
      : config(requested), slots(new DoorbellSlot[config.max_queues]), used(config.max_queues, false),
        worker(&gemstone_hsa_doorbell_monitor::run, this) {}

  ~gemstone_hsa_doorbell_monitor() {
    {
      std::lock_guard<std::mutex> guard(lock);
      stopping = true;
    }
    changed.notify_one();
    worker.join();
  }

  void run() {
    uint32_t empty_passes = 0;
    for (;;) {
      std::vector<std::shared_ptr<QueueState>> snapshot;
      {
        std::unique_lock<std::mutex> guard(lock);
        changed.wait(guard, [&] { return stopping || !queues.empty(); });
        if (stopping) return;
        snapshot = queues;
      }

      bool progressed = false;
      for (const auto &queue : snapshot) {
        if (queue->active.load(std::memory_order_acquire) && scan(*queue)) {
          progressed = true;
        }
      }

      if (progressed) {
        empty_passes = 0;
      } else if (empty_passes++ < config.spin_passes) {
        std::this_thread::yield();
      } else {
        empty_passes = 0;
        std::this_thread::sleep_for(std::chrono::nanoseconds(config.maximum_sleep_ns));
      }
    }
  }

  static bool scan(QueueState &queue) {
    ScanGuard guard(queue);
    if (!queue.active.load(std::memory_order_acquire)) return false;
    const auto &q = queue.descriptor;

    // KFD doorbells are MMIO and notify hardware on every write. A userspace
    // replacement cannot observe a write edge to ordinary memory. The first
    // non-sentinel value arms the queue; thereafter ring publication and the
    // write index are polled so repeated doorbell values cannot lose work.
    const uint64_t doorbell = load(&queue.doorbell->value, __ATOMIC_ACQUIRE);
    if (doorbell == kUnrungDoorbell) return false;

    uint64_t next = load(q.read_index, __ATOMIC_ACQUIRE);
    uint64_t end = load(q.write_index, __ATOMIC_ACQUIRE);
    const bool first_trace =
        gemstone::hsa::trace_enabled("packets") && !queue.traced_armed.exchange(true, std::memory_order_relaxed);
    if (first_trace) {
      const auto *packet = static_cast<const unsigned char *>(q.ring_base) + ((next & (q.ring_size_packets - 1)) * q.packet_size);
      const uint16_t header = load(reinterpret_cast<const uint16_t *>(packet + q.packet_header_offset), __ATOMIC_ACQUIRE);
      std::fprintf(stderr, "Gemstone armed AQL queue=%lu doorbell=%lu read=%lu write=%lu header=0x%04x mask=0x%04x invalid=0x%04x\n",
                   q.queue_id, doorbell, next, end, header, q.packet_type_mask, q.invalid_packet_type);
    }
    end = std::min(end, next + q.ring_size_packets);
    bool progressed = false;

    while (next < end && queue.active.load(std::memory_order_acquire)) {
      auto *packet = static_cast<unsigned char *>(q.ring_base) + ((next & (q.ring_size_packets - 1)) * q.packet_size);
      auto *header_address = reinterpret_cast<uint16_t *>(packet + q.packet_header_offset);

      // AQL producers publish the header last with release ordering. An
      // INVALID header is a legitimate multi-producer hole and stops this
      // in-order consumer until its owner publishes it.
      const uint16_t header = load(header_address, __ATOMIC_ACQUIRE);
      if ((header & q.packet_type_mask) == q.invalid_packet_type) {
        break;
      }

      const auto result = q.consume(q.queue_id, next, packet, q.packet_size, q.user_data);
      if (result == GEMSTONE_HSA_PACKET_RETRY) break;
      if (result == GEMSTONE_HSA_PACKET_FATAL) {
        queue.fatal.store(true, std::memory_order_release);
        queue.active.store(false, std::memory_order_release);
        break;
      }

      // Invalidation is required at wrap-around: a reserved but unpublished
      // packet must not be mistaken for the valid packet from the previous lap.
      const uint16_t invalid = static_cast<uint16_t>((header & static_cast<uint16_t>(~q.packet_type_mask)) | q.invalid_packet_type);
      store(header_address, invalid, __ATOMIC_RELEASE);
      ++next;
      store(q.read_index, next, __ATOMIC_RELEASE);
      if (gemstone::hsa::trace_enabled("packets"))
        std::fprintf(stderr, "Gemstone advanced AQL queue=%lu read=%lu\n", q.queue_id, next);
      progressed = true;
    }
    return progressed;
  }

  gemstone_hsa_doorbell_monitor_config config;
  std::unique_ptr<DoorbellSlot[]> slots;
  std::vector<bool> used;
  std::mutex lock;
  std::condition_variable changed;
  std::vector<std::shared_ptr<QueueState>> queues;
  bool stopping = false;
  std::thread worker;
};

struct gemstone_hsa_queue_registration {
  gemstone_hsa_doorbell_monitor *monitor = nullptr;
  std::shared_ptr<QueueState> queue;
};

extern "C" gemstone_hsa_doorbell_monitor *gemstone_hsa_doorbell_monitor_create(const gemstone_hsa_doorbell_monitor_config *requested) {
  gemstone_hsa_doorbell_monitor_config config = requested == nullptr ? gemstone_hsa_doorbell_monitor_config{} : *requested;
  if (config.max_queues == 0) config.max_queues = 4096;
  if (config.spin_passes == 0) config.spin_passes = 64;
  if (config.maximum_sleep_ns == 0) config.maximum_sleep_ns = 50000;
  try {
    return new gemstone_hsa_doorbell_monitor(config);
  } catch (...) {
    return nullptr;
  }
}

extern "C" void gemstone_hsa_doorbell_monitor_destroy(gemstone_hsa_doorbell_monitor *monitor) { delete monitor; }

extern "C" gemstone_hsa_queue_registration *gemstone_hsa_doorbell_monitor_register(gemstone_hsa_doorbell_monitor *monitor,
                                                                                   const gemstone_hsa_queue_descriptor *descriptor,
                                                                                   uint64_t **doorbell_address) {
  if (monitor == nullptr || descriptor == nullptr || doorbell_address == nullptr || descriptor->ring_base == nullptr
      || descriptor->read_index == nullptr || descriptor->write_index == nullptr || descriptor->consume == nullptr
      || descriptor->packet_size < sizeof(uint16_t) || descriptor->packet_header_offset + sizeof(uint16_t) > descriptor->packet_size
      || descriptor->ring_size_packets == 0 || (descriptor->ring_size_packets & (descriptor->ring_size_packets - 1)) != 0
      || descriptor->packet_type_mask == 0) {
    return nullptr;
  }

  try {
    auto state = std::make_shared<QueueState>();
    state->descriptor = *descriptor;
    auto *registration = new gemstone_hsa_queue_registration{monitor, state};

    {
      std::lock_guard<std::mutex> guard(monitor->lock);
      const auto free = std::find(monitor->used.begin(), monitor->used.end(), false);
      if (free == monitor->used.end() || monitor->stopping) {
        delete registration;
        return nullptr;
      }
      const size_t slot = static_cast<size_t>(free - monitor->used.begin());
      monitor->used[slot] = true;
      state->slot = slot;
      state->doorbell = &monitor->slots[slot];
      store(&state->doorbell->value, kUnrungDoorbell, __ATOMIC_RELAXED);
      monitor->queues.push_back(state);
      *doorbell_address = &state->doorbell->value;
    }
    monitor->changed.notify_one();
    return registration;
  } catch (...) {
    return nullptr;
  }
}

extern "C" void gemstone_hsa_doorbell_monitor_unregister(gemstone_hsa_queue_registration *registration) {
  if (registration == nullptr) return;
  auto *monitor = registration->monitor;
  auto state = registration->queue;
  state->active.store(false, std::memory_order_release);
  {
    std::lock_guard<std::mutex> guard(monitor->lock);
    monitor->queues.erase(std::remove(monitor->queues.begin(), monitor->queues.end(), state), monitor->queues.end());
    monitor->used[state->slot] = false;
  }
  {
    std::unique_lock<std::mutex> guard(state->scan_lock);
    state->scan_finished.wait(guard, [&] { return state->scans.load(std::memory_order_acquire) == 0; });
  }
  delete registration;
}

extern "C" uint32_t gemstone_hsa_doorbell_monitor_thread_count(const gemstone_hsa_doorbell_monitor *monitor) {
  return monitor == nullptr ? 0 : 1;
}
