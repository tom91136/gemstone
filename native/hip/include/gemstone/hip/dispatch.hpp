#ifndef GEMSTONE_HIP_DISPATCH_HPP
#define GEMSTONE_HIP_DISPATCH_HPP

#include <cstring>

#include <stddef.h>
#include <stdint.h>

#include <hip/hip_runtime_api.h>

namespace gemstone::hip {

constexpr uint16_t dispatchDimensions(dim3 grid, dim3 block) {
  return static_cast<uint16_t>((grid.z > 1 || block.z > 1) ? 3 : (grid.y > 1 || block.y > 1) ? 2 : 1);
}

struct LaunchBuffer {
  const void *data = nullptr;
  size_t size = 0;

  bool copyTo(void *destination, size_t capacity, size_t required) const {
    if (required > capacity || size < required || (size != 0 && !data)) return false;
    // HIP consumes the declared kernargs, not unused bytes at the end of a caller's packed buffer.
    // Tensile can append a four-byte trailer to an otherwise complete argument record.
    if (required != 0) std::memcpy(destination, data, required);
    return true;
  }
};

inline bool parseLaunchBuffer(void **extra, LaunchBuffer &buffer) {
  buffer = {};
  if (!extra) return true;
  bool foundPointer = false;
  bool foundSize = false;
  size_t index = 0;
  while (index < 16) {
    void *tag = extra[index++];
    if (tag == HIP_LAUNCH_PARAM_END) return foundPointer && foundSize;
    if (index >= 16 || extra[index] == nullptr) return false;
    if (tag == HIP_LAUNCH_PARAM_BUFFER_POINTER) {
      if (foundPointer) return false;
      buffer.data = extra[index++];
      foundPointer = true;
    } else if (tag == HIP_LAUNCH_PARAM_BUFFER_SIZE) {
      if (foundSize) return false;
      buffer.size = *static_cast<const size_t *>(extra[index++]);
      foundSize = true;
    } else return false;
  }
  return false;
}

} // namespace gemstone::hip

#endif
