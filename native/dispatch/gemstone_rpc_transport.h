#ifndef GEMSTONE_RPC_TRANSPORT_H
#define GEMSTONE_RPC_TRANSPORT_H

#include <stdint.h>

#define GEMSTONE_RPC_MAXIMUM_PAYLOAD_SIZE (64u * 1024u * 1024u)

#ifdef __cplusplus
extern "C" {
#endif

typedef struct GemstoneRpcBuffer {
  uint8_t *data;
  uint32_t size;
} GemstoneRpcBuffer;

int gemstone_rpc_call(uint64_t message_id_high, uint64_t message_id_low, const char *function_name, const uint8_t *payload,
                   uint32_t payload_size, int32_t *cuda_status, GemstoneRpcBuffer *response);

void gemstone_rpc_buffer_release(GemstoneRpcBuffer *buffer);

#ifdef __cplusplus
}
#endif

#endif
