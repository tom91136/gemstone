#ifndef GEMSTONE_HSA_AQL_EXECUTION_H
#define GEMSTONE_HSA_AQL_EXECUTION_H

#include "gemstone/hsa/aql.h"
#include "gemstone/hsa/doorbell_monitor.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct gemstone_hsa_aql_runtime gemstone_hsa_aql_runtime;

typedef struct gemstone_hsa_dimensions {
  uint32_t x;
  uint32_t y;
  uint32_t z;
} gemstone_hsa_dimensions;

typedef enum gemstone_hsa_argument_kind {
  GEMSTONE_HSA_ARGUMENT_SCALAR = 1,
  GEMSTONE_HSA_ARGUMENT_ALLOCATION = 2
} gemstone_hsa_argument_kind;

typedef struct gemstone_hsa_argument {
  gemstone_hsa_argument_kind kind;
  const uint8_t *scalar_bytes;
  uint32_t scalar_size;
  uint64_t allocation_id;
  uint64_t allocation_offset;
} gemstone_hsa_argument;

typedef struct gemstone_hsa_kernarg_field {
  gemstone_hsa_argument_kind kind;
  uint32_t offset;
  uint32_t size;
  uint32_t contains_pointers;
} gemstone_hsa_kernarg_field;

typedef struct gemstone_hsa_kernel_binding {
  uint64_t kernel_object;
  const char *kernel_name;
  uint32_t kernarg_size;
  const gemstone_hsa_kernarg_field *fields;
  uint32_t field_count;
  uint32_t explicit_argument_count;
} gemstone_hsa_kernel_binding;

typedef struct gemstone_hsa_dirty_range {
  uint64_t allocation_id;
  uint64_t offset;
  uint32_t size;
} gemstone_hsa_dirty_range;

typedef struct gemstone_hsa_executor {
  void *context;
  int (*load_program)(void *context, const void *image, uint32_t image_size, uint64_t *program_id);
  int (*unload_program)(void *context, uint64_t program_id);
  int (*launch)(void *context, uint64_t program_id, const char *kernel_name, gemstone_hsa_dimensions group_size,
                gemstone_hsa_dimensions group_count, gemstone_hsa_dimensions global_size, uint32_t private_segment_bytes,
                uint32_t group_segment_bytes, const gemstone_hsa_argument *arguments, uint32_t argument_count, uint64_t *completion_id);
  int (*await_completion)(void *context, uint64_t completion_id);
  // Must be queried after a successful await and before release. A
  // zero-capacity call reports the stable range count without consuming it.
  int (*completion_dirty_ranges)(void *context, uint64_t completion_id, gemstone_hsa_dirty_range *ranges, uint32_t capacity,
                                 uint32_t *count);
  int (*release_completion)(void *context, uint64_t completion_id);
  const char *(*error)(void *context);
  void (*destroy)(void *context);
  // Optional host-to-device copy for a named program global. Older test
  // executors may leave this null; regular RPC-backed execution supplies it.
  int (*write_global)(void *context, uint64_t program_id, const char *name, uint64_t offset, const void *bytes, uint32_t size);
  int (*bind_global)(void *context, uint64_t program_id, const char *name, uint64_t allocation_id, uint64_t offset);
  // Optional for injected test/external executors. The regular RPC executor
  // supplies the server-owned kernarg manifest after loading the program.
  int (*describe_kernel)(void *context, uint64_t program_id, const char *name, uint32_t *kernarg_size, gemstone_hsa_kernarg_field *fields,
                         uint32_t capacity, uint32_t *field_count, uint32_t *explicit_argument_count);
} gemstone_hsa_executor;

typedef struct gemstone_hsa_signal_operations {
  void *context;
  int (*load_acquire)(void *context, uint64_t signal, int64_t *value);
  int (*complete_release)(void *context, uint64_t signal);
  int (*fail_release)(void *context, uint64_t signal, int32_t error_code);
} gemstone_hsa_signal_operations;

gemstone_hsa_aql_runtime *gemstone_hsa_aql_runtime_create(const gemstone_hsa_executor *executor,
                                                          const gemstone_hsa_signal_operations *signals);

// Creates the same runtime using the shared execution client and
// GEMSTONE_RPC_PORT/GEMSTONE_RPC_CLIENT. Returns null when the environment is
// incomplete. This deliberately has no fallback to a physical GPU.
gemstone_hsa_aql_runtime *gemstone_hsa_aql_runtime_create_from_environment(const gemstone_hsa_signal_operations *signals);

void gemstone_hsa_aql_runtime_destroy(gemstone_hsa_aql_runtime *runtime);
const char *gemstone_hsa_aql_runtime_error(const gemstone_hsa_aql_runtime *runtime);

// Installs the metadata normally supplied by the ROCR loader. `kernel_object`
// is the exact handle placed in an AQL dispatch packet. The code object is sent
// to the shared execution service once at installation.
int gemstone_hsa_aql_runtime_install_kernel(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object, const void *code_object,
                                            uint32_t code_object_size, const char *kernel_name, uint32_t kernarg_size,
                                            const gemstone_hsa_kernarg_field *fields, uint32_t field_count);

// Loads one code object once and binds every relocated ROCr kernel-object
// address to that shared program. This is the normal path for large HIP
// fatbinaries containing many kernel symbols.
int gemstone_hsa_aql_runtime_install_code_object(gemstone_hsa_aql_runtime *runtime, const void *code_object, uint32_t code_object_size,
                                                 const gemstone_hsa_kernel_binding *bindings, uint32_t binding_count);

// Transfers an already loaded shared-session program to the AQL runtime. The
// runtime unloads program_id on every return path after validating runtime and
// program_id, and retains it only while at least one installed kernel remains.
int gemstone_hsa_aql_runtime_install_loaded_program(gemstone_hsa_aql_runtime *runtime, uint64_t program_id,
                                                    const gemstone_hsa_kernel_binding *bindings, uint32_t binding_count);

// Returns the effective launch layout retained for an installed kernel. For
// RPC-backed execution this is the server-owned manifest, not the bootstrap
// metadata supplied by the native loader.
int gemstone_hsa_aql_runtime_describe_kernel(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object, uint32_t *kernarg_size,
                                             gemstone_hsa_kernarg_field *fields, uint32_t capacity, uint32_t *field_count,
                                             uint32_t *explicit_argument_count);

// Nonzero when install operations replace bootstrap fields with metadata from
// the execution service. Injected executors without that callback retain the
// process-local fallback behaviour.
int gemstone_hsa_aql_runtime_uses_server_kernel_metadata(const gemstone_hsa_aql_runtime *runtime);

int gemstone_hsa_aql_runtime_remove_kernel(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object);

// Updates a named global belonging to the code object that owns kernel_object.
// This is used by HIP's symbol-copy API for constant-memory launch state.
int gemstone_hsa_aql_runtime_bind_global(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object, const char *name,
                                         uint64_t allocation_id, uint64_t offset);
int gemstone_hsa_aql_runtime_write_global(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object, const char *name, uint64_t offset,
                                          const void *bytes, uint32_t size);

// Maps host pointer values embedded in kernarg buffers to allocations already
// registered with the shared execution service.
int gemstone_hsa_aql_runtime_register_allocation(gemstone_hsa_aql_runtime *runtime, uint64_t native_base, uint64_t size,
                                                 uint64_t allocation_id);

int gemstone_hsa_aql_runtime_unregister_allocation(gemstone_hsa_aql_runtime *runtime, uint64_t native_base);

// Reports the registered allocations directly reachable by a kernel dispatch:
// the kernarg buffer itself and every allocation pointer described by metadata
// (including pointers nested in opaque scalar fields). A zero-capacity call
// reports the required count. This lets a fine-grained host runtime synchronize
// only memory visible to the dispatched kernel.
int gemstone_hsa_aql_runtime_dispatch_allocations(gemstone_hsa_aql_runtime *runtime, const void *packet, size_t packet_size,
                                                  uint64_t *allocation_ids, uint32_t capacity, uint32_t *count);

// Returns exact executor-written ranges from successful kernel launches since
// the previous successful take. A zero-capacity call reports the required
// count without consuming it. The single process-wide AQL worker serializes
// this handoff with packet completion and native mirror-back.
int gemstone_hsa_aql_runtime_take_dirty_ranges(gemstone_hsa_aql_runtime *runtime, gemstone_hsa_dirty_range *ranges, uint32_t capacity,
                                               uint32_t *count);

// Callback compatible with gemstone_hsa_queue_descriptor.consume.
gemstone_hsa_packet_result gemstone_hsa_aql_runtime_consume(uint64_t queue_id, uint64_t packet_id, const void *packet, size_t packet_size,
                                                            void *runtime);

// ROCR 7.x ordinary signal ABI helper. Signal handles point at amd_signal_t;
// the value is the signed 64-bit word at offset 8. Use only below stock ROCR.
gemstone_hsa_signal_operations gemstone_hsa_rocr_signal_operations(void);

#ifdef __cplusplus
}
#endif

#endif
