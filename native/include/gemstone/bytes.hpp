#pragma once

#include <cstring>
#include <type_traits>

#include <stddef.h>

namespace gemstone {

template <typename T> [[nodiscard]] T readNative(const void *source, size_t offset = 0) noexcept {
  static_assert(std::is_trivially_copyable_v<T>, "native byte reads require a trivially-copyable type");
  T value{};
  std::memcpy(&value, static_cast<const unsigned char *>(source) + offset, sizeof(value));
  return value;
}

template <typename T> void writeNative(void *destination, size_t offset, const T &value) noexcept {
  static_assert(std::is_trivially_copyable_v<T>, "native byte writes require a trivially-copyable type");
  std::memcpy(static_cast<unsigned char *>(destination) + offset, &value, sizeof(value));
}

} // namespace gemstone
