#include "gemstone_cuda.h"
#include "gemstone_dispatch.h"
#include "gemstone_protocol.h"

#include <stddef.h>

static _Thread_local int32_t gemstone_cuda_last_error = GEMSTONE_STATUS_SUCCESS;

int32_t cudaGetDeviceCount(int32_t *count) {
  if (count == NULL) {
    gemstone_dispatch_record_boundary(GEMSTONE_OPCODE_CUDA_GET_DEVICE_COUNT, GEMSTONE_STATUS_INVALID_VALUE);
    return GEMSTONE_STATUS_INVALID_VALUE;
  }
  GemstoneWireRequest request = {
    .protocol_version = GEMSTONE_PROTOCOL_VERSION,
    .opcode = GEMSTONE_OPCODE_CUDA_GET_DEVICE_COUNT,
    .argument0 = 0,
    .argument1 = 0
  };
  GemstoneWireResponse response = {0};
  if (gemstone_dispatch_request(&request, &response) != 0) {
    gemstone_cuda_last_error = GEMSTONE_STATUS_INITIALIZATION_ERROR;
    return GEMSTONE_STATUS_INITIALIZATION_ERROR;
  }
  gemstone_cuda_last_error = response.status;
  if (response.status == GEMSTONE_STATUS_SUCCESS) {
    *count = (int32_t)response.result0;
  }
  return response.status;
}

int32_t gemstoneAddI32(int32_t left, int32_t right) {
  GemstoneWireRequest request = {
    .protocol_version = GEMSTONE_PROTOCOL_VERSION,
    .opcode = GEMSTONE_OPCODE_GEMSTONE_ADD_I32,
    .argument0 = (int64_t)left,
    .argument1 = (int64_t)right
  };
  GemstoneWireResponse response = {0};
  if (gemstone_dispatch_request(&request, &response) != 0) {
    gemstone_cuda_last_error = GEMSTONE_STATUS_INITIALIZATION_ERROR;
    return 0;
  }
  if (response.status != GEMSTONE_STATUS_SUCCESS) return 0;
  return (int32_t)response.result0;
}
