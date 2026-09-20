#ifndef GEMSTONE_HSA_AQL_H
#define GEMSTONE_HSA_AQL_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef enum gemstone_hsa_aql_packet_type {
  GEMSTONE_HSA_AQL_VENDOR_SPECIFIC = 0,
  GEMSTONE_HSA_AQL_INVALID = 1,
  GEMSTONE_HSA_AQL_KERNEL_DISPATCH = 2,
  GEMSTONE_HSA_AQL_BARRIER_AND = 3,
  GEMSTONE_HSA_AQL_AGENT_DISPATCH = 4,
  GEMSTONE_HSA_AQL_BARRIER_OR = 5
} gemstone_hsa_aql_packet_type;

typedef enum gemstone_hsa_aql_decode_error {
  GEMSTONE_HSA_AQL_DECODE_SUCCESS = 0,
  GEMSTONE_HSA_AQL_DECODE_TRUNCATED = 1,
  GEMSTONE_HSA_AQL_DECODE_UNSUPPORTED_TYPE = 2,
  GEMSTONE_HSA_AQL_DECODE_RESERVED_BITS = 3,
  GEMSTONE_HSA_AQL_DECODE_INVALID_DIMENSIONS = 4,
  GEMSTONE_HSA_AQL_DECODE_INVALID_GRID = 5
} gemstone_hsa_aql_decode_error;

typedef struct gemstone_hsa_aql_header {
  gemstone_hsa_aql_packet_type type;
  uint8_t barrier;
  uint8_t acquire_fence_scope;
  uint8_t release_fence_scope;
} gemstone_hsa_aql_header;

typedef struct gemstone_hsa_aql_kernel_dispatch {
  gemstone_hsa_aql_header header;
  uint8_t dimensions;
  uint16_t workgroup_size_x;
  uint16_t workgroup_size_y;
  uint16_t workgroup_size_z;
  uint32_t grid_size_x;
  uint32_t grid_size_y;
  uint32_t grid_size_z;
  uint32_t private_segment_size;
  uint32_t group_segment_size;
  uint64_t kernel_object;
  uint64_t kernarg_address;
  uint64_t completion_signal;
} gemstone_hsa_aql_kernel_dispatch;

typedef struct gemstone_hsa_aql_barrier {
  gemstone_hsa_aql_header header;
  uint64_t dependency_signals[5];
  uint64_t completion_signal;
} gemstone_hsa_aql_barrier;

typedef struct gemstone_hsa_aql_pm4_ib {
  gemstone_hsa_aql_header header;
  uint32_t indirect_buffer_commands[4];
  uint64_t completion_signal;
} gemstone_hsa_aql_pm4_ib;

typedef struct gemstone_hsa_aql_packet {
  gemstone_hsa_aql_packet_type type;
  union {
    gemstone_hsa_aql_kernel_dispatch kernel_dispatch;
    gemstone_hsa_aql_barrier barrier;
    gemstone_hsa_aql_pm4_ib pm4_ib;
  } value;
} gemstone_hsa_aql_packet;

gemstone_hsa_aql_decode_error gemstone_hsa_decode_aql_packet(const void *packet, size_t packet_size, gemstone_hsa_aql_packet *decoded);

const char *gemstone_hsa_aql_decode_error_string(gemstone_hsa_aql_decode_error error);

#ifdef __cplusplus
}
#endif

#endif
