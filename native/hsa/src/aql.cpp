#include "gemstone/hsa/aql.hpp"

#include <array>
#include <cstddef>
#include <cstdint>

#include <gemstone/bytes.hpp>

namespace gemstone::hsa {

DecodeStatus decodeAqlPacket(const void *packet, size_t size, AqlPacket &decoded) {
  if (!packet || size < AqlPacketSize) return DecodeStatus::Truncated;
  const auto *bytes = static_cast<const unsigned char *>(packet);
  const auto header = readNative<uint16_t>(bytes, 0);
  if ((header & 0xe000U) != 0 || ((header >> 9) & 3U) > 2 || ((header >> 11) & 3U) > 2) return DecodeStatus::ReservedBits;
  const auto type = static_cast<PacketType>(header & 0xffU);
  decoded.type = type;
  const bool signalled = type == PacketType::KernelDispatch || type == PacketType::BarrierAnd || type == PacketType::BarrierOr
                         || type == PacketType::VendorSpecific;
  if (signalled) decoded.completionSignal = readNative<uint64_t>(bytes, 56);

  if (type == PacketType::VendorSpecific) {
    // ROCr gfx9+ AqlQueue::ExecutePM4 publishes format 1 as a 64-byte AQL
    // packet. Gemstone has no physical GPU cache to invalidate, but retaining
    // and completing this exact packet preserves its queue/fence semantics.
    if (readNative<uint16_t>(bytes, 2) != 1 || readNative<uint32_t>(bytes, 20) != 0x0a) return DecodeStatus::UnsupportedType;
    for (size_t offset = 24; offset < 56; offset += sizeof(uint64_t))
      if (readNative<uint64_t>(bytes, offset) != 0) return DecodeStatus::ReservedBits;
    for (size_t index = 0; index < 4; ++index)
      decoded.indirectBufferCommands[index] = readNative<uint32_t>(bytes, 4 + (index * 4));
    return DecodeStatus::Success;
  }

  if (type == PacketType::KernelDispatch) {
    const auto setup = readNative<uint16_t>(bytes, 2);
    const auto dimensions = static_cast<uint8_t>(setup & 3U);
    if ((setup & 0xfffcU) != 0 || dimensions == 0) return DecodeStatus::InvalidDimensions;
    const std::array<uint16_t, 3> workgroup{readNative<uint16_t>(bytes, 4), readNative<uint16_t>(bytes, 6), readNative<uint16_t>(bytes, 8)};
    const std::array<uint32_t, 3> grid{readNative<uint32_t>(bytes, 12), readNative<uint32_t>(bytes, 16), readNative<uint32_t>(bytes, 20)};
    if (readNative<uint16_t>(bytes, 10) != 0 || readNative<uint64_t>(bytes, 48) != 0) return DecodeStatus::ReservedBits;
    for (size_t axis = 0; axis < 3; ++axis) {
      const bool unused = axis >= dimensions;
      if (workgroup[axis] == 0 || (unused && workgroup[axis] != 1)) return DecodeStatus::InvalidDimensions;
    }
    for (size_t axis = 0; axis < 3; ++axis) {
      const bool unused = axis >= dimensions;
      if (grid[axis] < workgroup[axis] || (unused && grid[axis] != 1)) return DecodeStatus::InvalidGrid;
    }
    decoded.dispatch = KernelDispatch{.dimensions = dimensions,
                                      .workgroupSize = workgroup,
                                      .gridSize = grid,
                                      .privateSegmentSize = readNative<uint32_t>(bytes, 24),
                                      .groupSegmentSize = readNative<uint32_t>(bytes, 28),
                                      .kernelObject = readNative<uint64_t>(bytes, 32),
                                      .kernargAddress = readNative<uint64_t>(bytes, 40)};
    return DecodeStatus::Success;
  }

  if (type == PacketType::BarrierAnd || type == PacketType::BarrierOr) {
    if (readNative<uint16_t>(bytes, 2) != 0 || readNative<uint32_t>(bytes, 4) != 0 || readNative<uint64_t>(bytes, 48) != 0)
      return DecodeStatus::ReservedBits;
    for (size_t index = 0; index < 5; ++index)
      decoded.dependencySignals[index] = readNative<uint64_t>(bytes, 8 + (index * 8));
    return DecodeStatus::Success;
  }

  return DecodeStatus::UnsupportedType;
}

const char *describe(DecodeStatus status) {
  switch (status) {
    case DecodeStatus::Success: return "success";
    case DecodeStatus::Truncated: return "packet is shorter than 64 bytes";
    case DecodeStatus::UnsupportedType: return "packet type is unsupported";
    case DecodeStatus::ReservedBits: return "reserved packet field is non-zero";
    case DecodeStatus::InvalidDimensions: return "dispatch dimensions are invalid";
    case DecodeStatus::InvalidGrid: return "dispatch grid is invalid";
  }
  return "unknown AQL decode error";
}

} // namespace gemstone::hsa
