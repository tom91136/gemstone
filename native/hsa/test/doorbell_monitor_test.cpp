#include "gemstone/hsa/doorbell_monitor.h"

#include <array>
#include <atomic>
#include <chrono>
#include <condition_variable>
#include <cstring>
#include <mutex>
#include <thread>
#include <vector>

#include <stdint.h>

#include <catch2/catch_test_macros.hpp>

namespace {

constexpr uint16_t Invalid = 1;
constexpr uint16_t Dispatch = 2;

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
  REQUIRE((size == sizeof(Packet)));
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

bool waitFor(Captures &captures, size_t count, std::chrono::milliseconds timeout = std::chrono::seconds(2)) {
  std::unique_lock<std::mutex> guard(captures.lock);
  return captures.changed.wait_for(guard, timeout, [&] { return captures.packet_ids.size() >= count; });
}

void publish(Packet &packet, uint32_t marker) {
  packet.marker = marker;
  __atomic_store_n(&packet.header, Dispatch, __ATOMIC_RELEASE);
}

struct QueueFixture {
  explicit QueueFixture(gemstone_hsa_doorbell_monitor *owner, uint64_t id, size_t size = 8) : monitor(owner), ring(size) {
    for (auto &packet : ring)
      packet.header = Invalid;
    gemstone_hsa_queue_descriptor descriptor{};
    descriptor.queue_id = id;
    descriptor.ring_base = ring.data();
    descriptor.ring_size_packets = ring.size();
    descriptor.packet_size = sizeof(Packet);
    descriptor.packet_header_offset = offsetof(Packet, header);
    descriptor.packet_type_mask = 0xff;
    descriptor.invalid_packet_type = Invalid;
    descriptor.read_index = &read_index;
    descriptor.write_index = &write_index;
    descriptor.consume = capture;
    descriptor.user_data = &captures;
    registration = gemstone_hsa_doorbell_monitor_register(monitor, &descriptor, &doorbell);
    REQUIRE((registration));
    REQUIRE((doorbell));
  }

  ~QueueFixture() {
    if (registration) gemstone_hsa_doorbell_monitor_unregister(registration);
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

void firstRingGatesDispatch(gemstone_hsa_doorbell_monitor *monitor) {
  QueueFixture queue(monitor, 1);
  publish(queue.ring[0], 10);
  queue.set_write(1);
  REQUIRE((!waitFor(queue.captures, 1, std::chrono::milliseconds(10))));
  queue.ring_doorbell(0);
  REQUIRE((waitFor(queue.captures, 1)));
}

void repeatedDoorbellValueDoesNotLoseWork(gemstone_hsa_doorbell_monitor *monitor) {
  QueueFixture queue(monitor, 2);
  publish(queue.ring[0], 20);
  queue.set_write(1);
  queue.ring_doorbell(0);
  REQUIRE((waitFor(queue.captures, 1)));

  publish(queue.ring[1], 21);
  queue.set_write(2);
  queue.ring_doorbell(0);
  REQUIRE((waitFor(queue.captures, 2)));
  REQUIRE(((queue.captures.markers == std::vector<uint32_t>{20, 21})));
}

void invalidHoleBlocksLaterPacket(gemstone_hsa_doorbell_monitor *monitor) {
  QueueFixture queue(monitor, 3);
  publish(queue.ring[1], 31);
  queue.set_write(2);
  queue.ring_doorbell(1);
  REQUIRE((!waitFor(queue.captures, 1, std::chrono::milliseconds(10))));

  publish(queue.ring[0], 30);
  queue.ring_doorbell(1);
  REQUIRE((waitFor(queue.captures, 2)));
  REQUIRE(((queue.captures.markers == std::vector<uint32_t>{30, 31})));
}

void wraparoundCannotConsumeStaleHeader(gemstone_hsa_doorbell_monitor *monitor) {
  QueueFixture queue(monitor, 4, 2);
  publish(queue.ring[0], 40);
  publish(queue.ring[1], 41);
  queue.set_write(2);
  queue.ring_doorbell(1);
  REQUIRE((waitFor(queue.captures, 2)));

  // Reserve the wrapped slot without publishing it. Consuming the old header
  // here would duplicate marker 40.
  queue.set_write(3);
  queue.ring_doorbell(2);
  REQUIRE((!waitFor(queue.captures, 3, std::chrono::milliseconds(10))));
  publish(queue.ring[0], 42);
  REQUIRE((waitFor(queue.captures, 3)));
  REQUIRE(((queue.captures.markers == std::vector<uint32_t>{40, 41, 42})));
}

void queuesShareWorkerAndUnregisterIsQuiescent(gemstone_hsa_doorbell_monitor *monitor) {
  REQUIRE((gemstone_hsa_doorbell_monitor_thread_count(monitor) == 1));
  QueueFixture first(monitor, 5);
  QueueFixture second(monitor, 6);
  publish(first.ring[0], 50);
  publish(second.ring[0], 60);
  first.set_write(1);
  second.set_write(1);
  first.ring_doorbell(0);
  second.ring_doorbell(0);
  REQUIRE((waitFor(first.captures, 1)));
  REQUIRE((waitFor(second.captures, 1)));

  gemstone_hsa_doorbell_monitor_unregister(first.registration);
  first.registration = nullptr;
  publish(first.ring[1], 51);
  first.set_write(2);
  REQUIRE((!waitFor(first.captures, 2, std::chrono::milliseconds(10))));
}

} // namespace

TEST_CASE("HSA doorbell monitor scheduling", "[hsa][doorbell]") {
  gemstone_hsa_doorbell_monitor_config config{};
  config.max_queues = 16;
  config.spin_passes = 8;
  config.maximum_sleep_ns = 10000;
  auto *monitor = gemstone_hsa_doorbell_monitor_create(&config);
  REQUIRE((monitor));

  firstRingGatesDispatch(monitor);
  repeatedDoorbellValueDoesNotLoseWork(monitor);
  invalidHoleBlocksLaterPacket(monitor);
  wraparoundCannotConsumeStaleHeader(monitor);
  queuesShareWorkerAndUnregisterIsQuiescent(monitor);

  gemstone_hsa_doorbell_monitor_destroy(monitor);
}
