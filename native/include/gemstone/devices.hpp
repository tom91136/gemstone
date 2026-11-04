#pragma once

#include <algorithm>
#include <array>
#include <cstdint>
#include <mutex>
#include <utility>

#include <gemstone/environment.hpp>

namespace gemstone {

inline constexpr uint32_t MaximumDeviceCount = environment::DeviceCountMaximum;

inline uint32_t deviceCount() {
  return static_cast<uint32_t>(environment::unsignedValue(environment::DeviceCount, environment::DeviceCountDefault, MaximumDeviceCount));
}

/** Bytes of device memory charged to each emulated device. */
class DeviceLedger {
public:
  void charge(uint32_t device, uint64_t bytes) {
    std::scoped_lock const lock(mutex);
    usedBytes[device] += bytes;
  }
  void refund(uint32_t device, uint64_t bytes) {
    std::scoped_lock const lock(mutex);
    usedBytes[device] -= std::min(bytes, usedBytes[device]);
  }
  uint64_t used(uint32_t device) const {
    std::scoped_lock const lock(mutex);
    return usedBytes[device];
  }

private:
  mutable std::mutex mutex;
  std::array<uint64_t, MaximumDeviceCount> usedBytes{};
};

/** Directed peer access: `from` may access memory of `to`. */
class PeerAccess {
public:
  bool enabled(uint32_t from, uint32_t to) const {
    std::scoped_lock const lock(mutex);
    return granted[from][to];
  }
  /** False when the grant already exists. */
  bool enable(uint32_t from, uint32_t to) {
    std::scoped_lock const lock(mutex);
    return !std::exchange(granted[from][to], true);
  }
  /** False when there was no grant. */
  bool disable(uint32_t from, uint32_t to) {
    std::scoped_lock const lock(mutex);
    return std::exchange(granted[from][to], false);
  }
  void clear(uint32_t device) {
    std::scoped_lock const lock(mutex);
    for (uint32_t other = 0; other < MaximumDeviceCount; ++other)
      granted[device][other] = granted[other][device] = false;
  }

private:
  mutable std::mutex mutex;
  bool granted[MaximumDeviceCount][MaximumDeviceCount]{};
};

} // namespace gemstone
