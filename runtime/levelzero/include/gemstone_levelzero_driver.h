#pragma once

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Stable seam between the Level Zero ABI process and the Gemstone executor.
// The in-process implementation is deliberately useful without a server for
// enumeration and transfer tests. Kernel submission will use this interface
// once the shared transport grows a SPIR-V launch message.
typedef struct gemstone_levelzero_executor_v1 {
  uint32_t abi_version;
  void *user_data;
  int (*load_spirv)(void *user_data, const uint8_t *bytes, size_t size, uint64_t *module_id);
  int (*launch)(void *user_data, uint64_t module_id, const char *kernel_name,
                uint32_t group_count_x, uint32_t group_count_y, uint32_t group_count_z,
                const void *const *arguments, const size_t *argument_sizes, size_t argument_count);
  void (*unload)(void *user_data, uint64_t module_id);
} gemstone_levelzero_executor_v1;

// Installation is process-global and must happen before the first module is
// created. Passing null restores the transfer-only executor.
void gemstone_levelzero_install_executor(const gemstone_levelzero_executor_v1 *executor);

#ifdef __cplusplus
}
#endif
