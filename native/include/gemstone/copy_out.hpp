#pragma once

#include <cstddef>
#include <cstring>

namespace gemstone {

/** Reports `size` through `written` and copies `bytes` when the caller buffer is large enough. A null output only
 * reports the required size. Returns false when the buffer is too small, which the caller maps to its API's size error.
 */
inline bool copyOut(size_t capacity, void *output, size_t *written, const void *bytes, size_t size) {
  if (written) *written = size;
  if (!output) return true;
  if (capacity < size) return false;
  if (size != 0) std::memcpy(output, bytes, size);
  return true;
}

/** Copies a fixed-size value into a caller buffer; see `copyOut`. */
template <class T> inline bool copyOutValue(size_t capacity, void *output, size_t *written, const T &value) {
  return copyOut(capacity, output, written, &value, sizeof(T));
}

} // namespace gemstone
