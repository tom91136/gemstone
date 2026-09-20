#ifndef GEMSTONE_HSA_HSAKMT_BACKEND_HPP
#define GEMSTONE_HSA_HSAKMT_BACKEND_HPP

#include "gemstone/hsa/aql_execution.h"

struct gemstone_hsakmt_memory_transport {
  void *context;
  int (*register_allocation)(void *context, uint64_t size, uint64_t *allocation_id);
  int (*release_allocation)(void *context, uint64_t allocation_id);
  int (*write_allocation)(void *context, uint64_t allocation_id, uint64_t offset, const void *bytes, uint32_t size);
  int (*read_allocation)(void *context, uint64_t allocation_id, uint64_t offset, void *bytes, uint32_t size);
  int (*register_allocation_address)(void *context, uint64_t allocation_id, uint64_t native_base);
};

#ifdef GEMSTONE_HSAKMT_TEST_SUPPORT
extern "C" {

int gemstone_hsakmt_test_install_backend(gemstone_hsa_aql_runtime *runtime, const gemstone_hsakmt_memory_transport *memory);

int gemstone_hsakmt_test_remove_backend(void);

struct gemstone_hsakmt_test_loaded_kernel {
  uint64_t kernel_object;
  const char *name;
  uint32_t kernarg_size;
};

int gemstone_hsakmt_test_install_code_object(const void *code_object, uint64_t code_object_size,
                                             const gemstone_hsakmt_test_loaded_kernel *kernels, uint32_t kernel_count);

int gemstone_hsakmt_test_install_internal_kernel(const void *code_object, uint64_t code_object_size, uint64_t kernel_object,
                                                 const char *target, const char *name, uint32_t kernarg_size, uint32_t group_segment_size,
                                                 uint32_t private_segment_size, uint32_t compute_pgm_rsrc2, uint32_t kernel_code_properties,
                                                 const gemstone_hsa_kernarg_field *fields, uint32_t field_count);

const char *gemstone_hsakmt_test_loader_error(void);
}
#endif

#endif
