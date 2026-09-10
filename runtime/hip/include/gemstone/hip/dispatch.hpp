// SPDX-License-Identifier: Apache-2.0
#ifndef GEMSTONE_HIP_DISPATCH_HPP
#define GEMSTONE_HIP_DISPATCH_HPP

#include <cstddef>
#include <cstdint>
#include <cstring>

#include <hip/hip_runtime_api.h>

namespace gemstone::hip {

constexpr uint16_t dispatch_dimensions(dim3 grid, dim3 block) {
  return static_cast<uint16_t>((grid.z > 1 || block.z > 1) ? 3 : (grid.y > 1 || block.y > 1) ? 2 : 1);
}

struct LaunchBuffer {
  const void *data = nullptr;
  size_t size = 0;

  bool copy_to(void *destination, size_t capacity, size_t required) const {
    if (required > capacity || size < required || (size != 0 && data == nullptr)) return false;
    // HIP consumes the declared kernargs, not unused bytes at the end of a caller's packed buffer.
    // Tensile can append a four-byte trailer to an otherwise complete argument record.
    if (required != 0) std::memcpy(destination, data, required);
    return true;
  }
};

inline bool parse_launch_buffer(void **extra, LaunchBuffer &buffer) {
  buffer = {};
  if (extra == nullptr) return true;
  bool found_pointer = false;
  bool found_size = false;
  size_t index = 0;
  while (index < 16) {
    void *tag = extra[index++];
    if (tag == HIP_LAUNCH_PARAM_END) return found_pointer && found_size;
    if (index >= 16 || extra[index] == nullptr) return false;
    if (tag == HIP_LAUNCH_PARAM_BUFFER_POINTER) {
      if (found_pointer) return false;
      buffer.data = extra[index++];
      found_pointer = true;
    } else if (tag == HIP_LAUNCH_PARAM_BUFFER_SIZE) {
      if (found_size) return false;
      buffer.size = *static_cast<const size_t *>(extra[index++]);
      found_size = true;
    } else return false;
  }
  return false;
}

} // namespace gemstone::hip

#endif
