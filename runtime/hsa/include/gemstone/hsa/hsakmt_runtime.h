// SPDX-License-Identifier: Apache-2.0
#ifndef GEMSTONE_HSA_HSAKMT_RUNTIME_H
#define GEMSTONE_HSA_HSAKMT_RUNTIME_H

#include "gemstone/hsa/aql_execution.h"

#ifdef __cplusplus
extern "C" {
#endif

// Test and embedding seam for the replacement libhsakmt. The caller retains
// ownership of the AQL runtime and transport context. Installation and removal
// are only valid while hsaKmtOpenKFD has no active users.
typedef struct gemstone_hsakmt_memory_transport {
  void *context;
  int (*register_allocation)(void *context, uint64_t allocation_id, uint64_t size);
  int (*release_allocation)(void *context, uint64_t allocation_id);
  int (*write_allocation)(void *context, uint64_t allocation_id, uint64_t offset, const void *bytes, uint32_t size);
  int (*read_allocation)(void *context, uint64_t allocation_id, uint64_t offset, void *bytes, uint32_t size);
  // Optional for embedding transports; the environment transport always registers native addresses.
  int (*register_allocation_address)(void *context, uint64_t allocation_id, uint64_t native_base);
} gemstone_hsakmt_memory_transport;

int gemstone_hsakmt_install_backend(gemstone_hsa_aql_runtime *runtime, const gemstone_hsakmt_memory_transport *memory);

int gemstone_hsakmt_remove_backend(void);

// Legacy focused-test seam for installing the result of code-object loading.
// The public HSA facade must perform this step internally; applications and
// vendor runtimes must never call this function.
typedef struct gemstone_hsakmt_loaded_kernel {
  uint64_t kernel_object;
  const char *name;
  uint32_t kernarg_size;
} gemstone_hsakmt_loaded_kernel;

int gemstone_hsakmt_notify_code_object(const void *code_object, uint64_t code_object_size, const gemstone_hsakmt_loaded_kernel *kernels,
                                       uint32_t kernel_count);

// Legacy focused-test seam for an internally assembled kernel. The public HSA
// facade owns this operation and does not expose it as a deployment contract.
int gemstone_hsakmt_notify_internal_kernel(const void *code_object, uint64_t code_object_size, uint64_t kernel_object,
                                           const char *target, const char *name, uint32_t kernarg_size,
                                           uint32_t group_segment_size, uint32_t private_segment_size,
                                           uint32_t compute_pgm_rsrc2, uint32_t kernel_code_properties,
                                           const gemstone_hsa_kernarg_field *fields, uint32_t field_count);

const char *gemstone_hsakmt_loader_error(void);

#ifdef __cplusplus
}
#endif

#endif
