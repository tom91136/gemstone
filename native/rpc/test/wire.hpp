#pragma once

#include <type_traits>
#include <vector>

#include <stddef.h>
#include <stdint.h>

namespace gemstone_test::wire {

template <typename T> [[nodiscard]] T readBigEndian(const uint8_t *input) noexcept {
  static_assert(std::is_unsigned_v<T>, "wire integers must be unsigned");
  static_assert(sizeof(T) == 4 || sizeof(T) == 8, "wire integers must be 32 or 64 bits");
  T value = 0;
  for (size_t index = 0; index < sizeof(T); ++index)
    value = static_cast<T>((value << 8U) | input[index]);
  return value;
}

template <typename T> void appendBigEndian(std::vector<uint8_t> &output, T value) {
  static_assert(std::is_unsigned_v<T>, "wire integers must be unsigned");
  static_assert(sizeof(T) == 4 || sizeof(T) == 8, "wire integers must be 32 or 64 bits");
  for (size_t index = sizeof(T); index != 0; --index)
    output.push_back(static_cast<uint8_t>(value >> ((index - 1U) * 8U)));
}

} // namespace gemstone_test::wire
