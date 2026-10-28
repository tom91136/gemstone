#pragma once

#include <algorithm>
#include <cstdint>
#include <cstring>
#include <optional>
#include <vector>

#include "gemstone_execution_rpc.hpp"

namespace gemstone::execution {

/** Largest allocation payload a single RPC message may carry. */
inline constexpr size_t TransferChunk = MaximumPayload / 2;

/** Registers `size` bytes at native `address`; the id is released again when binding the address fails. */
inline std::optional<AllocationId> registerAllocation(Client &client, uint64_t size, const void *address, bool hostBacked = false) {
  const auto id = client.call(RegisterAllocation{{static_cast<int64_t>(size)}});
  if (!id) return std::nullopt;
  const GuestAddress base{static_cast<int64_t>(reinterpret_cast<uintptr_t>(address))};
  if (client.call(RegisterAllocationAddress{.allocation = id.value(), .nativeBase = base, .hostBacked = hostBacked})) return id.value();
  (void)client.call(ReleaseAllocation{id.value()});
  return std::nullopt;
}

/** Writes client bytes to an allocation range in bounded chunks. Returns false on the first transport error. */
inline bool uploadAllocation(Client &client, AllocationId allocation, uint64_t offset, const void *bytes, size_t size, size_t chunkLimit) {
  if (chunkLimit == 0) return false;
  const auto *source = static_cast<const uint8_t *>(bytes);
  while (size != 0) {
    const auto chunk = std::min(size, chunkLimit);
    if (!client.call(WriteAllocation{
            .allocation = allocation, .offset = {static_cast<int64_t>(offset)}, .bytes = std::vector<uint8_t>(source, source + chunk)}))
      return false;
    source += chunk;
    offset += chunk;
    size -= chunk;
  }
  return true;
}

/** Reads an allocation range back into client bytes in bounded chunks. Returns false on the first transport error. */
inline bool downloadAllocation(Client &client, AllocationId allocation, uint64_t offset, void *bytes, size_t size, size_t chunkLimit) {
  if (chunkLimit == 0) return false;
  auto *target = static_cast<uint8_t *>(bytes);
  while (size != 0) {
    const auto chunk = std::min(size, chunkLimit);
    auto result = client.call(
        ReadAllocation{.allocation = allocation, .offset = {static_cast<int64_t>(offset)}, .size = {static_cast<int32_t>(chunk)}});
    if (!result || result.value().size() != chunk) return false;
    std::memcpy(target, result.value().data(), chunk);
    target += chunk;
    offset += chunk;
    size -= chunk;
  }
  return true;
}

/** Rows of `rowBytes` bytes at `rowPitch` intervals, `rowCount` rows per slice and `sliceCount` slices at `slicePitch` intervals. */
struct Rectangle {
  uint64_t offset = 0;
  size_t rowBytes = 0;
  size_t rowCount = 0;
  size_t sliceCount = 0;
  size_t rowPitch = 0;
  size_t slicePitch = 0;
};

/** Visits each row as (allocation offset, slice, row), stopping at the first false. */
template <class Visit> bool forEachRectangleRow(const Rectangle &rectangle, Visit visit) {
  for (size_t slice = 0; slice < rectangle.sliceCount; ++slice)
    for (size_t row = 0; row < rectangle.rowCount; ++row)
      if (!visit(rectangle.offset + (slice * rectangle.slicePitch) + (row * rectangle.rowPitch), slice, row)) return false;
  return true;
}

namespace detail {

/** Visits blocks of whole slices when one slice fits in `chunkLimit`, otherwise of whole rows, as (slice, row, slices, rows). A row
 * pitch above the wire limit limits blocks to single rows, and a slice pitch above it to single slices. */
template <class Send> bool forEachRectangleBlock(const Rectangle &rectangle, size_t chunkLimit, Send send) {
  constexpr size_t wireLimit = INT32_MAX;
  if (rectangle.rowBytes == 0 || rectangle.rowCount == 0 || rectangle.sliceCount == 0 || rectangle.rowBytes > chunkLimit
      || chunkLimit > MaximumPayload)
    return false;
  if (rectangle.rowCount > 1 && rectangle.rowPitch < rectangle.rowBytes) return false;
  const bool rowsFit = rectangle.rowCount == 1 || rectangle.rowPitch <= wireLimit;
  const bool slicesFit = rectangle.sliceCount == 1 || rectangle.slicePitch <= wireLimit;
  const size_t sliceBytes = rectangle.rowBytes * rectangle.rowCount;
  if (rowsFit && sliceBytes <= chunkLimit) {
    const size_t slices = slicesFit ? std::min(rectangle.sliceCount, chunkLimit / sliceBytes) : size_t{1};
    for (size_t slice = 0; slice < rectangle.sliceCount; slice += slices)
      if (!send(slice, size_t{0}, std::min(slices, rectangle.sliceCount - slice), rectangle.rowCount)) return false;
    return true;
  }
  const size_t rows = rowsFit ? chunkLimit / rectangle.rowBytes : size_t{1};
  for (size_t slice = 0; slice < rectangle.sliceCount; ++slice)
    for (size_t row = 0; row < rectangle.rowCount; row += rows)
      if (!send(slice, row, size_t{1}, std::min(rows, rectangle.rowCount - row))) return false;
  return true;
}

inline int32_t wireValue(size_t value) { return static_cast<int32_t>(value); }

} // namespace detail

/** Writes host rows laid out at `hostRowPitch` and `hostSlicePitch` to an allocation rectangle in bounded requests, one per block of whole
 * rows or slices. */
inline bool uploadRectangle(Client &client, AllocationId allocation, const Rectangle &rectangle, const void *host, size_t hostRowPitch,
                            size_t hostSlicePitch, size_t chunkLimit) {
  const auto *source = static_cast<const uint8_t *>(host);
  if (rectangle.rowBytes > chunkLimit)
    return forEachRectangleRow(rectangle, [&](uint64_t offset, size_t slice, size_t row) {
      return uploadAllocation(client, allocation, offset, source + (slice * hostSlicePitch) + (row * hostRowPitch), rectangle.rowBytes,
                              chunkLimit);
    });
  return detail::forEachRectangleBlock(rectangle, chunkLimit, [&](size_t slice, size_t row, size_t slices, size_t rows) {
    std::vector<uint8_t> packed(slices * rows * rectangle.rowBytes);
    auto *target = packed.data();
    for (size_t z = 0; z < slices; ++z)
      for (size_t y = 0; y < rows; ++y, target += rectangle.rowBytes)
        std::memcpy(target, source + ((slice + z) * hostSlicePitch) + ((row + y) * hostRowPitch), rectangle.rowBytes);
    return static_cast<bool>(client.call(WriteAllocationRectangle{
        .allocation = allocation,
        .offset = {static_cast<int64_t>(rectangle.offset + (slice * rectangle.slicePitch) + (row * rectangle.rowPitch))},
        .rowBytes = {detail::wireValue(rectangle.rowBytes)},
        .rowCount = {detail::wireValue(rows)},
        .sliceCount = {detail::wireValue(slices)},
        .rowPitch = {detail::wireValue(rows > 1 ? rectangle.rowPitch : rectangle.rowBytes)},
        .slicePitch = {detail::wireValue(slices > 1 ? rectangle.slicePitch : rectangle.rowBytes * rows)},
        .bytes = std::move(packed)}));
  });
}

/** Reads an allocation rectangle into host rows laid out at `hostRowPitch` and `hostSlicePitch` in bounded requests. */
inline bool downloadRectangle(Client &client, AllocationId allocation, const Rectangle &rectangle, void *host, size_t hostRowPitch,
                              size_t hostSlicePitch, size_t chunkLimit) {
  auto *target = static_cast<uint8_t *>(host);
  if (rectangle.rowBytes > chunkLimit)
    return forEachRectangleRow(rectangle, [&](uint64_t offset, size_t slice, size_t row) {
      return downloadAllocation(client, allocation, offset, target + (slice * hostSlicePitch) + (row * hostRowPitch), rectangle.rowBytes,
                                chunkLimit);
    });
  return detail::forEachRectangleBlock(rectangle, chunkLimit, [&](size_t slice, size_t row, size_t slices, size_t rows) {
    auto result = client.call(ReadAllocationRectangle{
        .allocation = allocation,
        .offset = {static_cast<int64_t>(rectangle.offset + (slice * rectangle.slicePitch) + (row * rectangle.rowPitch))},
        .rowBytes = {detail::wireValue(rectangle.rowBytes)},
        .rowCount = {detail::wireValue(rows)},
        .sliceCount = {detail::wireValue(slices)},
        .rowPitch = {detail::wireValue(rows > 1 ? rectangle.rowPitch : rectangle.rowBytes)},
        .slicePitch = {detail::wireValue(slices > 1 ? rectangle.slicePitch : rectangle.rowBytes * rows)}});
    if (!result || result.value().size() != slices * rows * rectangle.rowBytes) return false;
    const auto *source = result.value().data();
    for (size_t z = 0; z < slices; ++z)
      for (size_t y = 0; y < rows; ++y, source += rectangle.rowBytes)
        std::memcpy(target + ((slice + z) * hostSlicePitch) + ((row + y) * hostRowPitch), source, rectangle.rowBytes);
    return true;
  });
}

/** Batch nearby reads, but publish only the exact dirty intervals. Fetching a gap
 * must never overwrite host bytes which the dispatch did not write.
 */
template <class Apply>
bool readDirtyRanges(Client &client, const std::vector<DirtyRange> &ranges, Apply apply, uint64_t maximumGap = 4096,
                     uint64_t batchBytes = uint64_t{1024} * 1024) {
  if (batchBytes == 0 || batchBytes > TransferChunk) return false;
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
      if (next.allocation.value != first.allocation.value || nextOffset < endOffset || nextOffset - endOffset > maximumGap
          || nextEnd - offset > batchBytes)
        break;
      endOffset = nextEnd;
      ++end;
    }
    // Large single intervals still use bounded transfers.
    for (auto chunkOffset = offset; chunkOffset < endOffset;) {
      const auto chunk = std::min<uint64_t>(batchBytes, endOffset - chunkOffset);
      auto bytes = client.call(ReadAllocation{
          .allocation = first.allocation, .offset = {static_cast<int64_t>(chunkOffset)}, .size = {static_cast<int32_t>(chunk)}});
      if (!bytes || bytes.value().size() != chunk) return false;
      for (size_t index = begin; index < end; ++index) {
        const auto &range = ranges[index];
        const auto start = std::max<uint64_t>(chunkOffset, range.offset.value);
        const auto finish = std::min<uint64_t>(chunkOffset + chunk, static_cast<uint64_t>(range.offset.value) + range.size.value);
        if (start < finish
            && !apply(DirtyRange{.allocation = range.allocation,
                                 .offset = {static_cast<int64_t>(start)},
                                 .size = {static_cast<int32_t>(finish - start)}},
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
