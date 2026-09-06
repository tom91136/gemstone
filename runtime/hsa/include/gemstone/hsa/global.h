// SPDX-License-Identifier: Apache-2.0
#ifndef GEMSTONE_HSA_GLOBAL_H
#define GEMSTONE_HSA_GLOBAL_H

#include <stddef.h>

#include <hsa/hsa.h>

#ifdef __cplusplus
extern "C" {
#endif

// Gemstone extension used by the HIP facade to implement hipMemcpyToSymbol.
// The named symbol is resolved against the loaded AMDGPU code object rather
// than being exposed as a host pointer.
hsa_status_t gemstone_hsa_executable_write_global(hsa_executable_t executable, const char *name, const void *bytes, size_t size,
                                                   size_t offset);

#ifdef __cplusplus
}
#endif

#endif
