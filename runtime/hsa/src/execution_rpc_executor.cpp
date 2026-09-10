// SPDX-License-Identifier: Apache-2.0
#include <algorithm>
#include <chrono>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <new>
#include <thread>
#include <vector>

#include "gemstone/hsa/aql_execution.h"
#include "gemstone/hsa/trace.h"
#include "gemstone_execution_rpc.h"

namespace {

struct EnvironmentExecutor {
  gemstone_execution_client *client = nullptr;
};

int load_program(void *context, const void *image, uint32_t image_size, uint64_t *program_id) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  static constexpr char raw_magic[8] = {'G', 'E', 'M', 'A', 'M', 'D', '1', '\0'};
  const auto format = image != nullptr && image_size >= sizeof(raw_magic) && std::memcmp(image, raw_magic, sizeof(raw_magic)) == 0
                          ? GEMSTONE_PROGRAM_AMDGPU_RAW_KERNEL
                          : GEMSTONE_PROGRAM_AMDGPU_CODE_OBJECT;
  if (gemstone::hsa::trace_enabled("packets"))
    std::fprintf(stderr, "Gemstone loading execution program format=%d bytes=%u\n", format, image_size);
  return gemstone_execution_load_program(executor->client, format, image, image_size, program_id);
}

int unload_program(void *context, uint64_t program_id) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  return gemstone_execution_unload_program(executor->client, program_id);
}

int bind_global(void *context, uint64_t program_id, const char *name, uint64_t allocation_id, uint64_t offset) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  return gemstone_execution_bind_program_global(executor->client, program_id, name, allocation_id, offset);
}

int write_global(void *context, uint64_t program_id, const char *name, uint64_t offset, const void *bytes, uint32_t size) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  return gemstone_execution_write_program_global(executor->client, program_id, name, offset, bytes, size);
}

int launch(void *context, uint64_t program_id, const char *kernel_name, gemstone_hsa_dimensions group_size,
           gemstone_hsa_dimensions group_count, gemstone_hsa_dimensions global_size, uint32_t private_segment_bytes,
           uint32_t group_segment_bytes, const gemstone_hsa_argument *arguments, uint32_t argument_count, uint64_t *completion_id) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  std::vector<gemstone_kernel_argument> converted;
  converted.reserve(argument_count);
  for (uint32_t index = 0; index < argument_count; ++index) {
    const auto &input = arguments[index];
    gemstone_kernel_argument output{};
    if (input.kind == GEMSTONE_HSA_ARGUMENT_SCALAR) {
      output.kind = GEMSTONE_ARGUMENT_SCALAR;
      output.scalar_bytes = input.scalar_bytes;
      output.scalar_size = input.scalar_size;
    } else if (input.kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION) {
      output.kind = GEMSTONE_ARGUMENT_ALLOCATION;
      output.allocation_id = input.allocation_id;
      output.allocation_offset = input.allocation_offset;
    } else return -1;
    converted.push_back(output);
  }
  return gemstone_execution_launch(executor->client, program_id, kernel_name, gemstone_dimensions{group_size.x, group_size.y, group_size.z},
                                   gemstone_dimensions{group_count.x, group_count.y, group_count.z},
                                   gemstone_dimensions{global_size.x, global_size.y, global_size.z}, private_segment_bytes,
                                   group_segment_bytes, converted.data(),
                                   static_cast<uint32_t>(converted.size()), completion_id);
}

int await_completion(void *context, uint64_t completion_id) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  auto delay = std::chrono::microseconds(50);
  unsigned pending_polls = 0;
  for (;;) {
    const int result = gemstone_execution_query_completion(executor->client, completion_id);
    if (result <= 0) return result;
    // Keep the first status immediate and short completions responsive, while
    // avoiding thousands of RPCs per second for a long-running kernel.
    std::this_thread::sleep_for(delay);
    if (++pending_polls >= 8) delay = std::min(delay * 2, std::chrono::microseconds(1000));
  }
}

int completion_dirty_ranges(void *context, uint64_t completion_id, gemstone_hsa_dirty_range *ranges, uint32_t capacity, uint32_t *count) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  if (count == nullptr || (capacity != 0 && ranges == nullptr)) return -1;
  if (capacity == 0) return gemstone_execution_completion_dirty_ranges(executor->client, completion_id, nullptr, 0, count);
  std::vector<gemstone_dirty_range> converted(capacity);
  const int result = gemstone_execution_completion_dirty_ranges(executor->client, completion_id, converted.data(), capacity, count);
  if (result != 0) return result;
  if (*count > capacity) return -1;
  for (uint32_t index = 0; index < *count; ++index) {
    ranges[index].allocation_id = converted[index].allocation_id;
    ranges[index].offset = converted[index].offset;
    ranges[index].size = converted[index].size;
  }
  return 0;
}

int release_completion(void *context, uint64_t completion_id) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  return gemstone_execution_release_completion(executor->client, completion_id);
}

const char *error(void *context) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  return gemstone_execution_client_error(executor->client);
}

void destroy(void *context) {
  auto *executor = static_cast<EnvironmentExecutor *>(context);
  gemstone_execution_client_destroy(executor->client);
  delete executor;
}

} // namespace

extern "C" gemstone_hsa_aql_runtime *gemstone_hsa_aql_runtime_create_from_environment(const gemstone_hsa_signal_operations *signals) {
  if (signals == nullptr) return nullptr;
  auto *executor = new (std::nothrow) EnvironmentExecutor();
  if (executor == nullptr) return nullptr;
  executor->client = gemstone_execution_client_create_from_environment();
  if (executor->client == nullptr) {
    delete executor;
    return nullptr;
  }
  const gemstone_hsa_executor operations{
      executor,           load_program, unload_program, launch,       await_completion, completion_dirty_ranges,
      release_completion, error,        destroy,        write_global, bind_global};
  auto *runtime = gemstone_hsa_aql_runtime_create(&operations, signals);
  if (runtime == nullptr) destroy(executor);
  return runtime;
}
