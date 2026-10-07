#pragma once

#include <functional>
#include <memory>

#include <stddef.h>
#include <stdint.h>

namespace gemstone::hsa {

enum class PacketResult { Accepted, Retry, Fatal };

/** Consumes one published `AqlPacketSize`-byte packet. */
using PacketConsumer = std::function<PacketResult(uint64_t queueId, uint64_t packetId, const void *packet)>;

class DoorbellMonitor;
struct QueueState;

/** An AQL ring watched by a `DoorbellMonitor`; destruction waits for any in-flight scan. */
class AqlQueue {
public:
  ~AqlQueue();
  AqlQueue(const AqlQueue &) = delete;
  AqlQueue &operator=(const AqlQueue &) = delete;

  uint64_t id() const;
  /** Stable, naturally aligned address for ROCr's ordinary doorbell stores. */
  uint64_t *doorbell() const;
  uint64_t *readIndex() const;
  uint64_t *writeIndex() const;
  /** False once a fatal packet halts the ring or `inactivate` stops it; no later packet is consumed. */
  bool active() const;
  void inactivate();

private:
  friend class DoorbellMonitor;
  AqlQueue(DoorbellMonitor &monitor, std::shared_ptr<QueueState> state);

  DoorbellMonitor &monitor;
  std::shared_ptr<QueueState> state;
};

/** Polls every registered AQL ring from two worker threads, regardless of queue count, and sleeps while none exist.
 * Doorbells live in ordinary memory, so the first store arms a queue and the write index is polled thereafter.
 */
class DoorbellMonitor {
public:
  static constexpr size_t MaxQueues = 128;

  DoorbellMonitor();
  ~DoorbellMonitor();
  DoorbellMonitor(const DoorbellMonitor &) = delete;
  DoorbellMonitor &operator=(const DoorbellMonitor &) = delete;

  /** Registers a power-of-two ring; null read or write indices select queue-owned counters. Returns null when no slot is free. */
  std::unique_ptr<AqlQueue> registerQueue(void *ring, size_t ringPackets, PacketConsumer consume, uint64_t *readIndex = nullptr,
                                          uint64_t *writeIndex = nullptr);

private:
  friend class AqlQueue;
  struct Impl;
  void run();
  void unregister(const std::shared_ptr<QueueState> &state);

  std::unique_ptr<Impl> impl;
};

} // namespace gemstone::hsa
