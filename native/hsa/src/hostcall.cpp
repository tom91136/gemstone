#include "gemstone/hsa/hostcall.hpp"

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <vector>

namespace gemstone::hsa {
namespace {

constexpr size_t Packets = 1024;
constexpr size_t BufferHeaderBytes = 64;
constexpr size_t PacketHeaderBytes = 24;
constexpr size_t PayloadBytes = static_cast<size_t>(64 * 8) * sizeof(uint64_t);
constexpr size_t SignalBytes = 64;
constexpr size_t PayloadStart = (BufferHeaderBytes + (PacketHeaderBytes * Packets) + 63U) & ~size_t{63};
constexpr size_t SignalStart = PayloadStart + (PayloadBytes * Packets);
constexpr size_t MailboxStart = SignalStart + SignalBytes;
constexpr uint64_t SignalKindUser = 1;
constexpr uint64_t SignalInitial = 1;
constexpr uint32_t EventId = 1;

template <typename T> void put(std::vector<uint8_t> &image, size_t offset, T value) {
  std::memcpy(image.data() + offset, &value, sizeof(T));
}

} // namespace

HostcallLayout hostcallLayout() { return {.bytes = MailboxStart + sizeof(uint64_t), .packets = Packets}; }

std::vector<uint8_t> hostcallImage(uint64_t base) {
  std::vector<uint8_t> image(hostcallLayout().bytes);
  const uint64_t mask = Packets - 1;
  uint64_t next = mask + 1;
  for (size_t index = 1; index < Packets; ++index) {
    put<uint64_t>(image, BufferHeaderBytes + (index * PacketHeaderBytes), next);
    next = index;
  }
  put<uint64_t>(image, 0, base + BufferHeaderBytes);
  put<uint64_t>(image, 8, base + PayloadStart);
  put<uint64_t>(image, 16, base + SignalStart);
  put<uint64_t>(image, 24, next);
  put<uint64_t>(image, 32, 0);
  put<uint64_t>(image, 40, mask);
  put<uint64_t>(image, SignalStart, SignalKindUser);
  put<uint64_t>(image, SignalStart + 8, SignalInitial);
  put<uint64_t>(image, SignalStart + 16, base + MailboxStart);
  put<uint32_t>(image, SignalStart + 24, EventId);
  return image;
}

} // namespace gemstone::hsa
