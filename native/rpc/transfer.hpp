#ifndef GEMSTONE_EXECUTION_TRANSFER_HPP
#define GEMSTONE_EXECUTION_TRANSFER_HPP

#include <algorithm>
#include <cstdint>
#include <cstring>
#include <vector>

#include "gemstone_execution_rpc.hpp"

namespace gemstone::execution {

/** Largest allocation payload a single RPC message may carry. */
inline constexpr size_t TransferChunk = MaximumPayload / 2;

/** Copies an allocation range between the client and the execution service in
 * bounded chunks. `upload` writes client bytes to the service; otherwise the
 * service bytes are read back. Returns false on the first transport error.
 */
inline bool transferAllocation(Client &client, AllocationId allocation, uint64_t offset, const void *bytes, size_t size, bool upload,
                               size_t chunkLimit) {
  if (chunkLimit == 0) return false;
  const auto *source = static_cast<const uint8_t *>(bytes);
  while (size != 0) {
    const auto chunk = std::min(size, chunkLimit);
    if (upload) {
      if (!client.call(WriteAllocation{allocation, static_cast<int64_t>(offset), std::vector<uint8_t>(source, source + chunk)}))
        return false;
    } else {
      auto result = client.call(ReadAllocation{allocation, static_cast<int64_t>(offset), static_cast<int32_t>(chunk)});
      if (!result || result.value().size() != chunk) return false;
      std::memcpy(const_cast<uint8_t *>(source), result.value().data(), chunk);
    }
    source += chunk;
    offset += chunk;
    size -= chunk;
  }
  return true;
}

/** Batch nearby reads, but publish only the exact dirty intervals. Fetching a gap
 * must never overwrite host bytes which the dispatch did not write.
 */
template <class Apply> bool readDirtyRanges(Client &client, const std::vector<DirtyRange> &ranges, Apply apply) {
  constexpr uint64_t BatchBytes = 1024 * 1024;
  constexpr uint64_t MaximumGap = 4096; // Bound read amplification across sparse pages.
  for (size_t begin = 0; begin < ranges.size();) {
    const auto &first = ranges[begin];
    const auto offset = static_cast<uint64_t>(first.offset.value);
    if (first.offset.value < 0 || first.size.value <= 0 || offset > INT64_MAX - static_cast<uint64_t>(first.size.value)) return false;
    auto endOffset = offset + static_cast<uint64_t>(first.size.value);
    size_t end = begin + 1;
    while (end < ranges.size()) {
      const auto &next = ranges[end];
      if (next.offset.value < 0 || next.size.value <= 0
          || static_cast<uint64_t>(next.offset.value) > INT64_MAX - static_cast<uint64_t>(next.size.value))
        return false;
      const auto nextOffset = static_cast<uint64_t>(next.offset.value);
      const auto nextEnd = nextOffset + static_cast<uint64_t>(next.size.value);
      if (next.allocation.value != first.allocation.value || nextOffset < endOffset || nextOffset - endOffset > MaximumGap
          || nextEnd - offset > BatchBytes)
        break;
      endOffset = nextEnd;
      ++end;
    }
    // Large single intervals still use bounded transfers.
    for (auto chunkOffset = offset; chunkOffset < endOffset;) {
      const auto chunk = std::min<uint64_t>(BatchBytes, endOffset - chunkOffset);
      auto bytes = client.call(ReadAllocation{first.allocation, static_cast<int64_t>(chunkOffset), static_cast<int32_t>(chunk)});
      if (!bytes || bytes.value().size() != chunk) return false;
      for (size_t index = begin; index < end; ++index) {
        const auto &range = ranges[index];
        const auto start = std::max<uint64_t>(chunkOffset, range.offset.value);
        const auto finish = std::min<uint64_t>(chunkOffset + chunk, static_cast<uint64_t>(range.offset.value) + range.size.value);
        if (start < finish
            && !apply(DirtyRange{range.allocation, static_cast<int64_t>(start), static_cast<int32_t>(finish - start)},
                      bytes.value().data() + (start - chunkOffset)))
          return false;
      }
      chunkOffset += chunk;
    }
    begin = end;
  }
  return true;
}

} // namespace gemstone::execution

#endif
