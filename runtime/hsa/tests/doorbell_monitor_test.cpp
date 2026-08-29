// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/doorbell_monitor.h"

#include <array>
#include <atomic>
#include <cassert>
#include <chrono>
#include <condition_variable>
#include <cstdint>
#include <cstring>
#include <mutex>
#include <thread>
#include <vector>

namespace {

constexpr uint16_t kInvalid = 1;
constexpr uint16_t kDispatch = 2;

struct alignas(64) Packet {
  uint16_t header;
  uint16_t reserved;
  uint32_t marker;
  std::array<unsigned char, 56> padding;
};
static_assert(sizeof(Packet) == 64);

struct Captures {
  std::mutex lock;
  std::condition_variable changed;
  std::vector<uint64_t> packet_ids;
  std::vector<uint32_t> markers;
};

gemstone_hsa_packet_result capture(uint64_t, uint64_t packet_id, const void *raw, size_t size, void *user_data) {
  assert(size == sizeof(Packet));
  const auto &packet = *static_cast<const Packet *>(raw);
  auto &captures = *static_cast<Captures *>(user_data);
  {
    std::lock_guard<std::mutex> guard(captures.lock);
    captures.packet_ids.push_back(packet_id);
    captures.markers.push_back(packet.marker);
  }
  captures.changed.notify_all();
  return GEMSTONE_HSA_PACKET_ACCEPTED;
}

bool wait_for(Captures &captures, size_t count, std::chrono::milliseconds timeout = std::chrono::seconds(2)) {
  std::unique_lock<std::mutex> guard(captures.lock);
  return captures.changed.wait_for(guard, timeout, [&] { return captures.packet_ids.size() >= count; });
}

void publish(Packet &packet, uint32_t marker) {
  packet.marker = marker;
  __atomic_store_n(&packet.header, kDispatch, __ATOMIC_RELEASE);
}

struct QueueFixture {
  explicit QueueFixture(gemstone_hsa_doorbell_monitor *owner, uint64_t id, size_t size = 8) : monitor(owner), ring(size) {
    for (auto &packet : ring)
      packet.header = kInvalid;
    gemstone_hsa_queue_descriptor descriptor{};
    descriptor.queue_id = id;
    descriptor.ring_base = ring.data();
    descriptor.ring_size_packets = ring.size();
    descriptor.packet_size = sizeof(Packet);
    descriptor.packet_header_offset = offsetof(Packet, header);
    descriptor.packet_type_mask = 0xff;
    descriptor.invalid_packet_type = kInvalid;
    descriptor.read_index = &read_index;
    descriptor.write_index = &write_index;
    descriptor.consume = capture;
    descriptor.user_data = &captures;
    registration = gemstone_hsa_doorbell_monitor_register(monitor, &descriptor, &doorbell);
    assert(registration != nullptr);
    assert(doorbell != nullptr);
  }

  ~QueueFixture() {
    if (registration != nullptr) gemstone_hsa_doorbell_monitor_unregister(registration);
  }

  void ring_doorbell(uint64_t value) { __atomic_store_n(doorbell, value, __ATOMIC_RELEASE); }
  void set_write(uint64_t value) { __atomic_store_n(&write_index, value, __ATOMIC_RELEASE); }

  gemstone_hsa_doorbell_monitor *monitor;
  std::vector<Packet> ring;
  uint64_t read_index = 0;
  uint64_t write_index = 0;
  uint64_t *doorbell = nullptr;
  gemstone_hsa_queue_registration *registration = nullptr;
  Captures captures;
};

void first_ring_gates_dispatch(gemstone_hsa_doorbell_monitor *monitor) {
  QueueFixture queue(monitor, 1);
  publish(queue.ring[0], 10);
  queue.set_write(1);
  assert(!wait_for(queue.captures, 1, std::chrono::milliseconds(10)));
  queue.ring_doorbell(0);
  assert(wait_for(queue.captures, 1));
}

void repeated_doorbell_value_does_not_lose_work(gemstone_hsa_doorbell_monitor *monitor) {
  QueueFixture queue(monitor, 2);
  publish(queue.ring[0], 20);
  queue.set_write(1);
  queue.ring_doorbell(0);
  assert(wait_for(queue.captures, 1));

  publish(queue.ring[1], 21);
  queue.set_write(2);
  queue.ring_doorbell(0);
  assert(wait_for(queue.captures, 2));
  assert((queue.captures.markers == std::vector<uint32_t>{20, 21}));
}

void invalid_hole_blocks_later_packet(gemstone_hsa_doorbell_monitor *monitor) {
  QueueFixture queue(monitor, 3);
  publish(queue.ring[1], 31);
  queue.set_write(2);
  queue.ring_doorbell(1);
  assert(!wait_for(queue.captures, 1, std::chrono::milliseconds(10)));

  publish(queue.ring[0], 30);
  queue.ring_doorbell(1);
  assert(wait_for(queue.captures, 2));
  assert((queue.captures.markers == std::vector<uint32_t>{30, 31}));
}

void wraparound_cannot_consume_stale_header(gemstone_hsa_doorbell_monitor *monitor) {
  QueueFixture queue(monitor, 4, 2);
  publish(queue.ring[0], 40);
  publish(queue.ring[1], 41);
  queue.set_write(2);
  queue.ring_doorbell(1);
  assert(wait_for(queue.captures, 2));

  // Reserve the wrapped slot without publishing it. Consuming the old header
  // here would duplicate marker 40.
  queue.set_write(3);
  queue.ring_doorbell(2);
  assert(!wait_for(queue.captures, 3, std::chrono::milliseconds(10)));
  publish(queue.ring[0], 42);
  assert(wait_for(queue.captures, 3));
  assert((queue.captures.markers == std::vector<uint32_t>{40, 41, 42}));
}

void queues_share_worker_and_unregister_is_quiescent(gemstone_hsa_doorbell_monitor *monitor) {
  assert(gemstone_hsa_doorbell_monitor_thread_count(monitor) == 1);
  QueueFixture first(monitor, 5);
  QueueFixture second(monitor, 6);
  publish(first.ring[0], 50);
  publish(second.ring[0], 60);
  first.set_write(1);
  second.set_write(1);
  first.ring_doorbell(0);
  second.ring_doorbell(0);
  assert(wait_for(first.captures, 1));
  assert(wait_for(second.captures, 1));

  gemstone_hsa_doorbell_monitor_unregister(first.registration);
  first.registration = nullptr;
  publish(first.ring[1], 51);
  first.set_write(2);
  assert(!wait_for(first.captures, 2, std::chrono::milliseconds(10)));
}

} // namespace

int main() {
  gemstone_hsa_doorbell_monitor_config config{};
  config.max_queues = 16;
  config.spin_passes = 8;
  config.maximum_sleep_ns = 10000;
  auto *monitor = gemstone_hsa_doorbell_monitor_create(&config);
  assert(monitor != nullptr);

  first_ring_gates_dispatch(monitor);
  repeated_doorbell_value_does_not_lose_work(monitor);
  invalid_hole_blocks_later_packet(monitor);
  wraparound_cannot_consume_stale_header(monitor);
  queues_share_worker_and_unregister_is_quiescent(monitor);

  gemstone_hsa_doorbell_monitor_destroy(monitor);
  return 0;
}
