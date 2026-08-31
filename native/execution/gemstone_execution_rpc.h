#ifndef GEMSTONE_EXECUTION_RPC_H
#define GEMSTONE_EXECUTION_RPC_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct gemstone_execution_client gemstone_execution_client;

typedef enum gemstone_program_format {
  GEMSTONE_PROGRAM_PTX = 1,
  GEMSTONE_PROGRAM_AMDGPU_CODE_OBJECT = 2,
  GEMSTONE_PROGRAM_SPIRV = 3,
  GEMSTONE_PROGRAM_AMDGPU_RAW_KERNEL = 4
} gemstone_program_format;

typedef enum gemstone_allocation_kind {
  GEMSTONE_ALLOCATION_DEVICE = 1,
  GEMSTONE_ALLOCATION_SHARED = 2,
  GEMSTONE_ALLOCATION_HOST = 3,
  GEMSTONE_ALLOCATION_MANAGED = 4
} gemstone_allocation_kind;

typedef struct gemstone_dimensions {
  uint32_t x;
  uint32_t y;
  uint32_t z;
} gemstone_dimensions;

typedef enum gemstone_kernel_argument_kind {
  GEMSTONE_ARGUMENT_SCALAR = 1,
  GEMSTONE_ARGUMENT_ALLOCATION = 2,
  GEMSTONE_ARGUMENT_LOCAL = 3
} gemstone_kernel_argument_kind;

typedef struct gemstone_kernel_argument {
  gemstone_kernel_argument_kind kind;
  const uint8_t *scalar_bytes;
  uint32_t scalar_size;
  uint64_t allocation_id;
  uint64_t allocation_offset;
  uint64_t local_size;
} gemstone_kernel_argument;

typedef struct gemstone_pointer_relocation {
  uint64_t source_allocation_id;
  uint32_t source_argument_index;
  uint64_t source_offset;
  uint64_t target_allocation_id;
  uint64_t target_offset;
} gemstone_pointer_relocation;

typedef struct gemstone_dirty_range {
  uint64_t allocation_id;
  uint64_t offset;
  uint32_t size;
} gemstone_dirty_range;

gemstone_execution_client *gemstone_execution_client_create_from_environment(void);
void gemstone_execution_client_destroy(gemstone_execution_client *client);
const char *gemstone_execution_client_error(const gemstone_execution_client *client);
int gemstone_execution_attest(gemstone_execution_client *client);

int gemstone_execution_register_allocation(gemstone_execution_client *client, uint64_t allocation_id,
                                            uint64_t size, gemstone_allocation_kind kind);
int gemstone_execution_release_allocation(gemstone_execution_client *client, uint64_t allocation_id);
int gemstone_execution_write_allocation(gemstone_execution_client *client, uint64_t allocation_id,
                                         uint64_t offset, const void *bytes, uint32_t size);
int gemstone_execution_read_allocation(gemstone_execution_client *client, uint64_t allocation_id,
                                        uint64_t offset, void *bytes, uint32_t size);

int gemstone_execution_load_program(gemstone_execution_client *client, gemstone_program_format format,
                                     const void *image, uint32_t image_size, uint64_t *program_id);
int gemstone_execution_unload_program(gemstone_execution_client *client, uint64_t program_id);
int gemstone_execution_launch(gemstone_execution_client *client, uint64_t program_id,
                               const char *kernel_name, gemstone_dimensions group_size,
                               gemstone_dimensions group_count, gemstone_dimensions global_size,
                               uint32_t private_segment_bytes, uint32_t group_segment_bytes,
                               const gemstone_kernel_argument *arguments, uint32_t argument_count,
                               uint64_t *completion_id);
int gemstone_execution_launch_with_geometry(gemstone_execution_client *client, uint64_t program_id,
                                             const char *kernel_name, gemstone_dimensions group_size,
                                             gemstone_dimensions group_count, gemstone_dimensions global_size,
                                             uint32_t work_dim, gemstone_dimensions global_offset,
                                             uint32_t private_segment_bytes, uint32_t group_segment_bytes,
                                             const gemstone_kernel_argument *arguments, uint32_t argument_count,
                                             uint64_t *completion_id);
int gemstone_execution_launch_with_relocations(
    gemstone_execution_client *client, uint64_t program_id, const char *kernel_name,
    gemstone_dimensions group_size, gemstone_dimensions group_count, gemstone_dimensions global_size,
    uint32_t private_segment_bytes, uint32_t group_segment_bytes,
    const gemstone_kernel_argument *arguments, uint32_t argument_count,
    const gemstone_pointer_relocation *relocations, uint32_t relocation_count,
    uint64_t *completion_id);
int gemstone_execution_launch_with_geometry_and_relocations(
    gemstone_execution_client *client, uint64_t program_id, const char *kernel_name,
    gemstone_dimensions group_size, gemstone_dimensions group_count, gemstone_dimensions global_size,
    uint32_t work_dim, gemstone_dimensions global_offset,
    uint32_t private_segment_bytes, uint32_t group_segment_bytes,
    const gemstone_kernel_argument *arguments, uint32_t argument_count,
    const gemstone_pointer_relocation *relocations, uint32_t relocation_count,
    uint64_t *completion_id);

// Returns 0 when complete, 1 while pending, and -1 on execution or protocol failure.
int gemstone_execution_query_completion(gemstone_execution_client *client, uint64_t completion_id);
// A zero-capacity call reports the required range count without writing ranges.
// Ranges remain available until the completion is released.
int gemstone_execution_completion_dirty_ranges(gemstone_execution_client *client, uint64_t completion_id,
                                                gemstone_dirty_range *ranges, uint32_t capacity, uint32_t *count);
// A zero-capacity call reports the required relocation count without writing records.
// Records remain available until the completion is released.
int gemstone_execution_completion_pointer_relocations(
    gemstone_execution_client *client, uint64_t completion_id,
    gemstone_pointer_relocation *relocations, uint32_t capacity, uint32_t *count);
int gemstone_execution_release_completion(gemstone_execution_client *client, uint64_t completion_id);

#ifdef __cplusplus
}
#endif

#endif
