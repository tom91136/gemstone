#ifndef GEMSTONE_CUDA_H
#define GEMSTONE_CUDA_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

int32_t cudaGetDeviceCount(int32_t *count);
int32_t gemstoneAddI32(int32_t left, int32_t right);

#ifdef __cplusplus
}
#endif

#endif
