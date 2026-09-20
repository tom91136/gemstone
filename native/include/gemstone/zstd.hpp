#pragma once

#include <optional>
#include <vector>

#include <stddef.h>

#include <zstd.h>

#include <gemstone/vector.hpp>

namespace gemstone {

template <typename Byte> bool decompressZstd(std::vector<Byte> &output, const void *source, size_t sourceSize, size_t outputSize) noexcept {
  static_assert(sizeof(Byte) == 1);
  if (!source || outputSize == 0 || !tryResize(output, outputSize)) {
    output.clear();
    return false;
  }
  const auto result = ZSTD_decompress(output.data(), outputSize, source, sourceSize);
  if (!ZSTD_isError(result) && result == outputSize) return true;
  output.clear();
  return false;
}

template <typename Byte>
std::optional<size_t> decompressZstdFrame(std::vector<Byte> &output, const void *source, size_t sourceSize,
                                          size_t maximumOutputSize) noexcept {
  if (!source) return std::nullopt;
  const auto outputSize = ZSTD_getFrameContentSize(source, sourceSize);
  const auto compressedSize = ZSTD_findFrameCompressedSize(source, sourceSize);
  if (outputSize == ZSTD_CONTENTSIZE_ERROR || outputSize == ZSTD_CONTENTSIZE_UNKNOWN || outputSize == 0 || outputSize > maximumOutputSize
      || ZSTD_isError(compressedSize) || compressedSize > sourceSize
      || !decompressZstd(output, source, compressedSize, static_cast<size_t>(outputSize)))
    return std::nullopt;
  return static_cast<size_t>(outputSize);
}

} // namespace gemstone
