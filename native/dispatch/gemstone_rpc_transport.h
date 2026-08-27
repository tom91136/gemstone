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

typedef void (*GemstoneHostCallback)(void *user_data);
typedef void (*GemstoneStreamCallback)(void *stream, int32_t status, void *user_data);

uint64_t gemstone_rpc_register_host_callback(GemstoneHostCallback callback, void *user_data);
uint64_t gemstone_rpc_register_stream_callback(GemstoneStreamCallback callback, void *user_data);
void gemstone_rpc_unregister_callback(uint64_t callback_id);

int gemstone_rpc_call(uint64_t message_id_high, uint64_t message_id_low, const char *function_name, const uint8_t *payload,
                   uint32_t payload_size, int32_t *cuda_status, GemstoneRpcBuffer *response);

void gemstone_rpc_buffer_release(GemstoneRpcBuffer *buffer);

#ifdef __cplusplus
}
#endif

#endif
