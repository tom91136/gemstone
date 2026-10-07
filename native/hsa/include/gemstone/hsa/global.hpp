#pragma once

#include <hsa.h>
#include <stddef.h>

// Gemstone extension used by the HIP facade to implement hipMemcpyToSymbol.
// The named symbol is resolved against the loaded AMDGPU code object rather
// than being exposed as a host pointer.
extern "C" hsa_status_t gemstone_hsa_executable_write_global(hsa_executable_t executable, const char *name, const void *bytes, size_t size,
                                                             size_t offset);
