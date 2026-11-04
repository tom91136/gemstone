#pragma once

#include <cstddef>
#include <cstdint>
#include <vector>

namespace gemstone::hsa {

/** The geometry of a hostcall buffer as the device library and the host listener share it. */
struct HostcallLayout {
  size_t bytes = 0;
  size_t packets = 0;
};

HostcallLayout hostcallLayout();

/** The initial contents of a hostcall buffer placed at `base`, including the doorbell that interrupts the host. */
std::vector<uint8_t> hostcallImage(uint64_t base);

/** The device's hostcall buffer, reserved at first use, or null when device memory is exhausted. */
void *hostcallBuffer();

} // namespace gemstone::hsa
