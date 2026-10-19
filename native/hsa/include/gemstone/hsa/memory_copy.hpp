#pragma once

#include <hsa.h>
#include <stddef.h>

// Gemstone extension used by the HIP facade for pitched and array copies: `slices` slices of `rows` rows of `row` bytes,
// each side's span moving between host and device at most once rather than once per row.
extern "C" hsa_status_t gemstone_hsa_memory_copy_pitched(void *destination, size_t destinationPitch, size_t destinationSlicePitch,
                                                         const void *source, size_t sourcePitch, size_t sourceSlicePitch, size_t row,
                                                         size_t rows, size_t slices);
