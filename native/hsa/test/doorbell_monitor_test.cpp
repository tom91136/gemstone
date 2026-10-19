#include "gemstone/hsa/doorbell_monitor.hpp"

#include <array>
#include <chrono>
#include <condition_variable>
#include <cstdint>
#include <cstring>
#include <fstream>
#include <memory>
#include <mutex>
#include <string>
#include <vector>

#include <catch2/catch_test_macros.hpp>

namespace {

using gemstone::hsa::DoorbellMonitor;
using gemstone::hsa::PacketResult;

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
  std::vector<uint64_t> packetIds;
  std::vector<uint32_t> markers;
};

PacketResult capture(Captures &captures, uint64_t packetId, const void *raw) {
  const auto &packet = *static_cast<const Packet *>(raw);
  {
    std::scoped_lock const guard(captures.lock);
    captures.packetIds.push_back(packetId);
    captures.markers.push_back(packet.marker);
  }
  captures.changed.notify_all();
  return PacketResult::Accepted;
}

bool waitFor(Captures &captures, size_t count, std::chrono::milliseconds timeout = std::chrono::seconds(2)) {
  std::unique_lock<std::mutex> guard(captures.lock);
  return captures.changed.wait_for(guard, timeout, [&] { return captures.packetIds.size() >= count; });
}

void publish(Packet &packet, uint32_t marker) {
  packet.marker = marker;
  __atomic_store_n(&packet.header, Dispatch, __ATOMIC_RELEASE);
}

struct QueueFixture {
  explicit QueueFixture(DoorbellMonitor &monitor, size_t size = 8) : ring(size) {
    for (auto &packet : ring)
      packet.header = Invalid;
    queue = monitor.registerQueue(
        ring.data(), ring.size(), [this](uint64_t, uint64_t packetId, const void *packet) { return capture(captures, packetId, packet); },
        &readIndex, &writeIndex);
    REQUIRE((queue));
  }

  void ringDoorbell(uint64_t value) const { __atomic_store_n(queue->doorbell(), value, __ATOMIC_RELEASE); }
  void setWrite(uint64_t value) { __atomic_store_n(&writeIndex, value, __ATOMIC_RELEASE); }

  std::vector<Packet> ring;
  uint64_t readIndex = 0;
  uint64_t writeIndex = 0;
  Captures captures;
  std::unique_ptr<gemstone::hsa::AqlQueue> queue;
};

void firstRingGatesDispatch(DoorbellMonitor &monitor) {
  QueueFixture queue(monitor);
  publish(queue.ring[0], 10);
  queue.setWrite(1);
  REQUIRE((!waitFor(queue.captures, 1, std::chrono::milliseconds(10))));
  queue.ringDoorbell(0);
  REQUIRE((waitFor(queue.captures, 1)));
}

void repeatedDoorbellValueDoesNotLoseWork(DoorbellMonitor &monitor) {
  QueueFixture queue(monitor);
  publish(queue.ring[0], 20);
  queue.setWrite(1);
  queue.ringDoorbell(0);
  REQUIRE((waitFor(queue.captures, 1)));

  publish(queue.ring[1], 21);
  queue.setWrite(2);
  queue.ringDoorbell(0);
  REQUIRE((waitFor(queue.captures, 2)));
  REQUIRE(((queue.captures.markers == std::vector<uint32_t>{20, 21})));
}

void invalidHoleBlocksLaterPacket(DoorbellMonitor &monitor) {
  QueueFixture queue(monitor);
  publish(queue.ring[1], 31);
  queue.setWrite(2);
  queue.ringDoorbell(1);
  REQUIRE((!waitFor(queue.captures, 1, std::chrono::milliseconds(10))));

  publish(queue.ring[0], 30);
  queue.ringDoorbell(1);
  REQUIRE((waitFor(queue.captures, 2)));
  REQUIRE(((queue.captures.markers == std::vector<uint32_t>{30, 31})));
}

void wraparoundCannotConsumeStaleHeader(DoorbellMonitor &monitor) {
  QueueFixture queue(monitor, 2);
  publish(queue.ring[0], 40);
  publish(queue.ring[1], 41);
  queue.setWrite(2);
  queue.ringDoorbell(1);
  REQUIRE((waitFor(queue.captures, 2)));

  // Reserve the wrapped slot without publishing it. Consuming the old header
  // here would duplicate marker 40.
  queue.setWrite(3);
  queue.ringDoorbell(2);
  REQUIRE((!waitFor(queue.captures, 3, std::chrono::milliseconds(10))));
  publish(queue.ring[0], 42);
  REQUIRE((waitFor(queue.captures, 3)));
  REQUIRE(((queue.captures.markers == std::vector<uint32_t>{40, 41, 42})));
}

void queuesShareWorkerAndUnregisterIsQuiescent(DoorbellMonitor &monitor) {
  QueueFixture first(monitor);
  QueueFixture second(monitor);
  publish(first.ring[0], 50);
  publish(second.ring[0], 60);
  first.setWrite(1);
  second.setWrite(1);
  first.ringDoorbell(0);
  second.ringDoorbell(0);
  REQUIRE((waitFor(first.captures, 1)));
  REQUIRE((waitFor(second.captures, 1)));

  first.queue.reset();
  publish(first.ring[1], 51);
  first.setWrite(2);
  REQUIRE((!waitFor(first.captures, 2, std::chrono::milliseconds(10))));
}

unsigned threadCount() {
  std::ifstream status("/proc/self/status");
  for (std::string line; std::getline(status, line);)
    if (line.starts_with("Threads:")) return static_cast<unsigned>(std::stoul(line.substr(8)));
  return 0;
}

} // namespace

TEST_CASE("HSA doorbell monitor scheduling", "[hsa][doorbell]") {
  const auto threads = threadCount();
  DoorbellMonitor monitor;
  REQUIRE((threads != 0 && threadCount() == threads + 2));
  firstRingGatesDispatch(monitor);
  repeatedDoorbellValueDoesNotLoseWork(monitor);
  invalidHoleBlocksLaterPacket(monitor);
  wraparoundCannotConsumeStaleHeader(monitor);
  queuesShareWorkerAndUnregisterIsQuiescent(monitor);
  REQUIRE((threadCount() == threads + 2));
}
