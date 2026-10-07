#pragma once

#include <array>

#include <stddef.h>
#include <stdint.h>

namespace gemstone::hsa {

/** Every AQL packet occupies this fixed stride in the ring. */
inline constexpr size_t AqlPacketSize = 64;

enum class PacketType : uint8_t { VendorSpecific = 0, Invalid = 1, KernelDispatch = 2, BarrierAnd = 3, AgentDispatch = 4, BarrierOr = 5 };

enum class DecodeStatus { Success, Truncated, UnsupportedType, ReservedBits, InvalidDimensions, InvalidGrid };

struct KernelDispatch {
  uint8_t dimensions = 0;
  std::array<uint16_t, 3> workgroupSize{};
  std::array<uint32_t, 3> gridSize{};
  uint32_t privateSegmentSize = 0;
  uint32_t groupSegmentSize = 0;
  uint64_t kernelObject = 0;
  uint64_t kernargAddress = 0;
};

struct AqlPacket {
  PacketType type = PacketType::Invalid;
  uint64_t completionSignal = 0;
  KernelDispatch dispatch{};
  std::array<uint64_t, 5> dependencySignals{};
  std::array<uint32_t, 4> indirectBufferCommands{};
};

/** Decodes one `AqlPacketSize`-byte packet. The type and completion signal survive a body validation failure so the
 * consumer can fail the producer's signal instead of stranding its waiter.
 */
DecodeStatus decodeAqlPacket(const void *packet, size_t size, AqlPacket &decoded);

const char *describe(DecodeStatus status);

} // namespace gemstone::hsa
