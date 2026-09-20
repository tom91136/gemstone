#pragma once

#include <climits>
#include <optional>
#include <vector>

#include <stddef.h>

#include <lz4.h>

#include <gemstone/vector.hpp>

namespace gemstone {

template <typename Byte>
std::optional<size_t> decompressLz4(std::vector<Byte> &output, const void *source, size_t sourceSize, size_t maximumOutputSize) noexcept {
  static_assert(sizeof(Byte) == 1);
  if (!source || sourceSize > INT_MAX || maximumOutputSize == 0 || maximumOutputSize > INT_MAX || !tryResize(output, maximumOutputSize)) {
    output.clear();
    return std::nullopt;
  }
  const int result = LZ4_decompress_safe(static_cast<const char *>(source), reinterpret_cast<char *>(output.data()),
                                         static_cast<int>(sourceSize), static_cast<int>(maximumOutputSize));
  if (result < 0) {
    output.clear();
    return std::nullopt;
  }
  output.resize(static_cast<size_t>(result));
  return static_cast<size_t>(result);
}

} // namespace gemstone
