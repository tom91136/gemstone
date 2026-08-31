// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/aql.h"

#include <cstring>

namespace {

constexpr size_t kPacketSize = 64;

uint16_t read_u16(const unsigned char *bytes, size_t offset) {
  uint16_t value;
  std::memcpy(&value, bytes + offset, sizeof(value));
  return value;
}

uint32_t read_u32(const unsigned char *bytes, size_t offset) {
  uint32_t value;
  std::memcpy(&value, bytes + offset, sizeof(value));
  return value;
}

uint64_t read_u64(const unsigned char *bytes, size_t offset) {
  uint64_t value;
  std::memcpy(&value, bytes + offset, sizeof(value));
  return value;
}

gemstone_hsa_aql_header decode_header(uint16_t raw) {
  gemstone_hsa_aql_header result{};
  result.type = static_cast<gemstone_hsa_aql_packet_type>(raw & 0xffU);
  result.barrier = static_cast<uint8_t>((raw >> 8) & 1U);
  result.acquire_fence_scope = static_cast<uint8_t>((raw >> 9) & 3U);
  result.release_fence_scope = static_cast<uint8_t>((raw >> 11) & 3U);
  return result;
}

} // namespace

extern "C" gemstone_hsa_aql_decode_error gemstone_hsa_decode_aql_packet(const void *packet, size_t packet_size,
                                                                        gemstone_hsa_aql_packet *decoded) {
  if (packet == nullptr || decoded == nullptr || packet_size < kPacketSize) return GEMSTONE_HSA_AQL_DECODE_TRUNCATED;
  const auto *bytes = static_cast<const unsigned char *>(packet);
  const uint16_t raw_header = read_u16(bytes, 0);
  if ((raw_header & 0xe000U) != 0) return GEMSTONE_HSA_AQL_DECODE_RESERVED_BITS;
  const auto header = decode_header(raw_header);
  if (header.acquire_fence_scope > 2 || header.release_fence_scope > 2) return GEMSTONE_HSA_AQL_DECODE_RESERVED_BITS;
  decoded->type = header.type;

  // Preserve the completion signal before validating the packet body. The
  // queue consumer can then report a malformed packet to its producer instead
  // of permanently stranding a waiter on a packet that can never execute.
  if (header.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH) decoded->value.kernel_dispatch.completion_signal = read_u64(bytes, 56);
  else if (header.type == GEMSTONE_HSA_AQL_BARRIER_AND || header.type == GEMSTONE_HSA_AQL_BARRIER_OR)
    decoded->value.barrier.completion_signal = read_u64(bytes, 56);
  else if (header.type == GEMSTONE_HSA_AQL_VENDOR_SPECIFIC) decoded->value.pm4_ib.completion_signal = read_u64(bytes, 56);

  if (header.type == GEMSTONE_HSA_AQL_VENDOR_SPECIFIC) {
    // ROCr gfx9+ AqlQueue::ExecutePM4 publishes format 1 as a 64-byte AQL
    // packet. Gemstone has no physical GPU cache to invalidate, but retaining
    // and completing this exact packet preserves its queue/fence semantics.
    if (read_u16(bytes, 2) != 1 || read_u32(bytes, 20) != 0x0a) return GEMSTONE_HSA_AQL_DECODE_UNSUPPORTED_TYPE;
    for (size_t offset = 24; offset < 56; offset += sizeof(uint64_t))
      if (read_u64(bytes, offset) != 0) return GEMSTONE_HSA_AQL_DECODE_RESERVED_BITS;
    auto &result = decoded->value.pm4_ib;
    result.header = header;
    for (size_t index = 0; index < 4; ++index)
      result.indirect_buffer_commands[index] = read_u32(bytes, 4 + index * 4);
    result.completion_signal = read_u64(bytes, 56);
    return GEMSTONE_HSA_AQL_DECODE_SUCCESS;
  }

  if (header.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH) {
    const uint16_t setup = read_u16(bytes, 2);
    const uint8_t dimensions = static_cast<uint8_t>(setup & 3U);
    if ((setup & 0xfffcU) != 0 || dimensions == 0) return GEMSTONE_HSA_AQL_DECODE_INVALID_DIMENSIONS;
    const uint16_t workgroup_x = read_u16(bytes, 4);
    const uint16_t workgroup_y = read_u16(bytes, 6);
    const uint16_t workgroup_z = read_u16(bytes, 8);
    const uint32_t grid_x = read_u32(bytes, 12);
    const uint32_t grid_y = read_u32(bytes, 16);
    const uint32_t grid_z = read_u32(bytes, 20);
    if (read_u16(bytes, 10) != 0 || read_u64(bytes, 48) != 0) return GEMSTONE_HSA_AQL_DECODE_RESERVED_BITS;
    if (workgroup_x == 0 || workgroup_y == 0 || workgroup_z == 0 || (dimensions == 1 && (workgroup_y != 1 || workgroup_z != 1))
        || (dimensions == 2 && workgroup_z != 1))
      return GEMSTONE_HSA_AQL_DECODE_INVALID_DIMENSIONS;
    if (grid_x < workgroup_x || grid_y < workgroup_y || grid_z < workgroup_z || (dimensions == 1 && (grid_y != 1 || grid_z != 1))
        || (dimensions == 2 && grid_z != 1))
      return GEMSTONE_HSA_AQL_DECODE_INVALID_GRID;

    auto &result = decoded->value.kernel_dispatch;
    result.header = header;
    result.dimensions = dimensions;
    result.workgroup_size_x = workgroup_x;
    result.workgroup_size_y = workgroup_y;
    result.workgroup_size_z = workgroup_z;
    result.grid_size_x = grid_x;
    result.grid_size_y = grid_y;
    result.grid_size_z = grid_z;
    result.private_segment_size = read_u32(bytes, 24);
    result.group_segment_size = read_u32(bytes, 28);
    result.kernel_object = read_u64(bytes, 32);
    result.kernarg_address = read_u64(bytes, 40);
    result.completion_signal = read_u64(bytes, 56);
    return GEMSTONE_HSA_AQL_DECODE_SUCCESS;
  }

  if (header.type == GEMSTONE_HSA_AQL_BARRIER_AND || header.type == GEMSTONE_HSA_AQL_BARRIER_OR) {
    if (read_u16(bytes, 2) != 0 || read_u32(bytes, 4) != 0 || read_u64(bytes, 48) != 0) return GEMSTONE_HSA_AQL_DECODE_RESERVED_BITS;
    auto &result = decoded->value.barrier;
    result.header = header;
    for (size_t index = 0; index < 5; ++index)
      result.dependency_signals[index] = read_u64(bytes, 8 + index * 8);
    result.completion_signal = read_u64(bytes, 56);
    return GEMSTONE_HSA_AQL_DECODE_SUCCESS;
  }

  return GEMSTONE_HSA_AQL_DECODE_UNSUPPORTED_TYPE;
}

extern "C" const char *gemstone_hsa_aql_decode_error_string(gemstone_hsa_aql_decode_error error) {
  switch (error) {
    case GEMSTONE_HSA_AQL_DECODE_SUCCESS: return "success";
    case GEMSTONE_HSA_AQL_DECODE_TRUNCATED: return "packet is shorter than 64 bytes";
    case GEMSTONE_HSA_AQL_DECODE_UNSUPPORTED_TYPE: return "packet type is unsupported";
    case GEMSTONE_HSA_AQL_DECODE_RESERVED_BITS: return "reserved packet field is non-zero";
    case GEMSTONE_HSA_AQL_DECODE_INVALID_DIMENSIONS: return "dispatch dimensions are invalid";
    case GEMSTONE_HSA_AQL_DECODE_INVALID_GRID: return "dispatch grid is invalid";
  }
  return "unknown AQL decode error";
}
