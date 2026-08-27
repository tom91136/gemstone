#ifndef GEMSTONE_PROTOCOL_H
#define GEMSTONE_PROTOCOL_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

enum {
  GEMSTONE_PROTOCOL_VERSION = 1,
  GEMSTONE_OPCODE_SHUTDOWN = 0,
  GEMSTONE_OPCODE_CUDA_GET_DEVICE_COUNT = 1,
  GEMSTONE_OPCODE_GEMSTONE_ADD_I32 = 2
};

enum {
  GEMSTONE_STATUS_SUCCESS = 0,
  GEMSTONE_STATUS_INVALID_VALUE = 1,
  GEMSTONE_STATUS_INITIALIZATION_ERROR = 3,
  GEMSTONE_STATUS_NOT_SUPPORTED = 801,
  GEMSTONE_STATUS_UNKNOWN = 999
};

typedef struct GemstoneWireRequest {
  uint32_t protocol_version;
  uint32_t opcode;
  uint64_t request_id;
  uint64_t foreign_thread_id;
  int64_t argument0;
  int64_t argument1;
} GemstoneWireRequest;

typedef struct GemstoneWireResponse {
  uint32_t protocol_version;
  int32_t status;
  uint64_t request_id;
  int64_t result0;
} GemstoneWireResponse;

#if defined(__cplusplus)
#define GEMSTONE_STATIC_ASSERT(condition, message) static_assert(condition, message)
#else
#define GEMSTONE_STATIC_ASSERT(condition, message) _Static_assert(condition, message)
#endif

GEMSTONE_STATIC_ASSERT(sizeof(GemstoneWireRequest) == 40, "GemstoneWireRequest ABI size changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireRequest, protocol_version) == 0, "request protocol_version offset changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireRequest, opcode) == 4, "request opcode offset changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireRequest, request_id) == 8, "request request_id offset changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireRequest, foreign_thread_id) == 16, "request foreign_thread_id offset changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireRequest, argument0) == 24, "request argument0 offset changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireRequest, argument1) == 32, "request argument1 offset changed");
GEMSTONE_STATIC_ASSERT(sizeof(GemstoneWireResponse) == 24, "GemstoneWireResponse ABI size changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireResponse, protocol_version) == 0, "response protocol_version offset changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireResponse, status) == 4, "response status offset changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireResponse, request_id) == 8, "response request_id offset changed");
GEMSTONE_STATIC_ASSERT(offsetof(GemstoneWireResponse, result0) == 16, "response result0 offset changed");

#undef GEMSTONE_STATIC_ASSERT

static inline const char *gemstone_operation_name(uint32_t opcode) {
  switch (opcode) {
    case GEMSTONE_OPCODE_SHUTDOWN: return "shutdown";
    case GEMSTONE_OPCODE_CUDA_GET_DEVICE_COUNT: return "cudaGetDeviceCount";
    case GEMSTONE_OPCODE_GEMSTONE_ADD_I32: return "gemstoneAddI32";
    default: return "unknown";
  }
}

#ifdef __cplusplus
}
#endif

#endif
