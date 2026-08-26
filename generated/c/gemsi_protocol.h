#ifndef GEMSI_PROTOCOL_H
#define GEMSI_PROTOCOL_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

enum {
  GEMSI_PROTOCOL_VERSION = 1,
  GEMSI_OPCODE_SHUTDOWN = 0,
  GEMSI_OPCODE_CUDA_GET_DEVICE_COUNT = 1,
  GEMSI_OPCODE_GEMSI_ADD_I32 = 2
};

enum {
  GEMSI_STATUS_SUCCESS = 0,
  GEMSI_STATUS_INVALID_VALUE = 1,
  GEMSI_STATUS_INITIALIZATION_ERROR = 3,
  GEMSI_STATUS_NOT_SUPPORTED = 801,
  GEMSI_STATUS_UNKNOWN = 999
};

typedef struct GemsiWireRequest {
  uint32_t protocol_version;
  uint32_t opcode;
  uint64_t request_id;
  uint64_t foreign_thread_id;
  int64_t argument0;
  int64_t argument1;
} GemsiWireRequest;

typedef struct GemsiWireResponse {
  uint32_t protocol_version;
  int32_t status;
  uint64_t request_id;
  int64_t result0;
} GemsiWireResponse;

#if defined(__cplusplus)
#define GEMSI_STATIC_ASSERT(condition, message) static_assert(condition, message)
#else
#define GEMSI_STATIC_ASSERT(condition, message) _Static_assert(condition, message)
#endif

GEMSI_STATIC_ASSERT(sizeof(GemsiWireRequest) == 40, "GemsiWireRequest ABI size changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, protocol_version) == 0, "request protocol_version offset changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, opcode) == 4, "request opcode offset changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, request_id) == 8, "request request_id offset changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, foreign_thread_id) == 16, "request foreign_thread_id offset changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, argument0) == 24, "request argument0 offset changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireRequest, argument1) == 32, "request argument1 offset changed");
GEMSI_STATIC_ASSERT(sizeof(GemsiWireResponse) == 24, "GemsiWireResponse ABI size changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireResponse, protocol_version) == 0, "response protocol_version offset changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireResponse, status) == 4, "response status offset changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireResponse, request_id) == 8, "response request_id offset changed");
GEMSI_STATIC_ASSERT(offsetof(GemsiWireResponse, result0) == 16, "response result0 offset changed");

#undef GEMSI_STATIC_ASSERT

static inline const char *gemsi_operation_name(uint32_t opcode) {
  switch (opcode) {
    case GEMSI_OPCODE_SHUTDOWN: return "shutdown";
    case GEMSI_OPCODE_CUDA_GET_DEVICE_COUNT: return "cudaGetDeviceCount";
    case GEMSI_OPCODE_GEMSI_ADD_I32: return "gemsiAddI32";
    default: return "unknown";
  }
}

#ifdef __cplusplus
}
#endif

#endif
