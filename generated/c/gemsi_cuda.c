#include "gemsi_cuda.h"
#include "gemsi_dispatch.h"
#include "gemsi_protocol.h"

#include <stddef.h>

static _Thread_local int32_t gemsi_cuda_last_error = GEMSI_STATUS_SUCCESS;

int32_t cudaGetDeviceCount(int32_t *count) {
  if (count == NULL) {
    gemsi_dispatch_record_boundary(GEMSI_OPCODE_CUDA_GET_DEVICE_COUNT, GEMSI_STATUS_INVALID_VALUE);
    return GEMSI_STATUS_INVALID_VALUE;
  }
  GemsiWireRequest request = {
    .protocol_version = GEMSI_PROTOCOL_VERSION,
    .opcode = GEMSI_OPCODE_CUDA_GET_DEVICE_COUNT,
    .argument0 = 0,
    .argument1 = 0
  };
  GemsiWireResponse response = {0};
  if (gemsi_dispatch_request(&request, &response) != 0) {
    gemsi_cuda_last_error = GEMSI_STATUS_INITIALIZATION_ERROR;
    return GEMSI_STATUS_INITIALIZATION_ERROR;
  }
  gemsi_cuda_last_error = response.status;
  if (response.status == GEMSI_STATUS_SUCCESS) {
    *count = (int32_t)response.result0;
  }
  return response.status;
}

int32_t gemsiAddI32(int32_t left, int32_t right) {
  GemsiWireRequest request = {
    .protocol_version = GEMSI_PROTOCOL_VERSION,
    .opcode = GEMSI_OPCODE_GEMSI_ADD_I32,
    .argument0 = (int64_t)left,
    .argument1 = (int64_t)right
  };
  GemsiWireResponse response = {0};
  if (gemsi_dispatch_request(&request, &response) != 0) {
    gemsi_cuda_last_error = GEMSI_STATUS_INITIALIZATION_ERROR;
    return 0;
  }
  if (response.status != GEMSI_STATUS_SUCCESS) return 0;
  return (int32_t)response.result0;
}
