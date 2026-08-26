#ifndef GEMSI_RPC_TRANSPORT_H
#define GEMSI_RPC_TRANSPORT_H

#include <stdint.h>

#define GEMSI_RPC_MAXIMUM_PAYLOAD_SIZE (64u * 1024u * 1024u)

#ifdef __cplusplus
extern "C" {
#endif

typedef struct GemsiRpcBuffer {
  uint8_t *data;
  uint32_t size;
} GemsiRpcBuffer;

int gemsi_rpc_call(uint64_t message_id_high, uint64_t message_id_low, const char *function_name, const uint8_t *payload,
                   uint32_t payload_size, int32_t *cuda_status, GemsiRpcBuffer *response);

void gemsi_rpc_buffer_release(GemsiRpcBuffer *buffer);

#ifdef __cplusplus
}
#endif

#endif
