// Focused port of the former direct-HSA dispatch sample. SPDX-License-Identifier: Apache-2.0
#include "../provider.hpp"
#include "dispatch_image.hpp"
#include "unified_suite/runtime_checks.hpp"
#include <chrono>
#include <cstring>
#include <hsa/hsa.h>
#include <hsa/hsa_ext_amd.h>
#include <thread>

namespace {
using namespace unified_suite;
using namespace runtime_checks;
void check(hsa_status_t status, const char *operation) {
  require(status == HSA_STATUS_SUCCESS, std::string(operation) + " returned " + std::to_string(status));
}
struct Agents {
  hsa_agent_t cpu{};
  hsa_agent_t gpu{};
};

hsa_status_t collect_agents(hsa_agent_t agent, void *data) {
  hsa_device_type_t type = HSA_DEVICE_TYPE_CPU;
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS)
    return HSA_STATUS_ERROR;
  auto &agents = *static_cast<Agents *>(data);
  if (type == HSA_DEVICE_TYPE_CPU)
    agents.cpu = agent;
  if (type == HSA_DEVICE_TYPE_GPU)
    agents.gpu = agent;
  return HSA_STATUS_SUCCESS;
}

hsa_status_t find_kernarg(hsa_region_t region, void *data) {
  hsa_region_segment_t segment = HSA_REGION_SEGMENT_READONLY;
  hsa_region_global_flag_t flags{};
  if (hsa_region_get_info(region, HSA_REGION_INFO_SEGMENT, &segment) != HSA_STATUS_SUCCESS ||
      segment != HSA_REGION_SEGMENT_GLOBAL ||
      hsa_region_get_info(region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags) != HSA_STATUS_SUCCESS)
    return HSA_STATUS_SUCCESS;
  if ((flags & HSA_REGION_GLOBAL_FLAG_KERNARG) != 0) {
    *static_cast<hsa_region_t *>(data) = region;
    return HSA_STATUS_INFO_BREAK;
  }
  return HSA_STATUS_SUCCESS;
}

void execute(bool asynchronous_copy) {
  Cleanup cleanup;
  check(hsa_init(), "init");
  cleanup.add([] { hsa_shut_down(); });
  Agents agents;
  check(hsa_iterate_agents(collect_agents, &agents), "agents");
  require(agents.cpu.handle && agents.gpu.handle, "HSA CPU/GPU agents absent");
  hsa_region_t region{};
  auto found = hsa_agent_iterate_regions(agents.cpu, find_kernarg, &region);
  require((found == HSA_STATUS_SUCCESS || found == HSA_STATUS_INFO_BREAK) && region.handle,
          "kernarg region absent");
  hsa_code_object_reader_t reader{};
  check(hsa_code_object_reader_create_from_memory(dispatch_image, sizeof(dispatch_image), &reader), "reader");
  cleanup.add([=] { hsa_code_object_reader_destroy(reader); });
  hsa_profile_t profile{};
  check(hsa_agent_get_info(agents.gpu, HSA_AGENT_INFO_PROFILE, &profile), "profile");
  hsa_executable_t executable{};
  check(hsa_executable_create_alt(profile, HSA_DEFAULT_FLOAT_ROUNDING_MODE_DEFAULT, nullptr, &executable),
        "executable");
  cleanup.add([=] { hsa_executable_destroy(executable); });
  check(hsa_executable_load_agent_code_object(executable, agents.gpu, reader, nullptr, nullptr), "load");
  check(hsa_executable_freeze(executable, nullptr), "freeze");
  hsa_executable_symbol_t symbol{};
  auto status = hsa_executable_get_symbol_by_name(executable, "vector_copy", &agents.gpu, &symbol);
  if (status != HSA_STATUS_SUCCESS)
    status = hsa_executable_get_symbol_by_name(executable, "vector_copy.kd", &agents.gpu, &symbol);
  check(status, "symbol");
  uint64_t kernel_object = 0;
  uint32_t argument_size = 0, group_size = 0, private_size = 0;
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &kernel_object),
        "kernel object");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE,
                                       &argument_size),
        "argument size");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_GROUP_SEGMENT_SIZE,
                                       &group_size),
        "group size");
  check(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_PRIVATE_SEGMENT_SIZE,
                                       &private_size),
        "private size");
  require(kernel_object && argument_size >= 24, "invalid descriptor");
  auto allocate = [&](std::size_t size) {
    void *result = nullptr;
    check(hsa_memory_allocate(region, size, &result), "allocate");
    cleanup.add([=] { hsa_memory_free(result); });
    return result;
  };
  auto *arguments = allocate(argument_size);
  auto *source = allocate(256);
  auto *destination = allocate(256);
  auto *guard = allocate(256);
  const auto expected = input(64);
  check(hsa_memory_copy(source, expected.data(), 256), "upload");
  std::memset(destination, 0xa5, 256);
  std::memset(guard, 0x5a, 256);
  std::memset(arguments, 0, argument_size);
  std::memcpy(arguments, &source, 8);
  std::memcpy(static_cast<char *>(arguments) + 8, &destination, 8);
  std::memcpy(static_cast<char *>(arguments) + 16, &guard, 8);
  hsa_signal_t completion{};
  check(hsa_signal_create(1, 0, nullptr, &completion), "completion signal");
  cleanup.add([=] { hsa_signal_destroy(completion); });
  hsa_queue_t *queue = nullptr;
  check(hsa_queue_create(agents.gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX,
                         &queue),
        "queue");
  cleanup.add([=] { hsa_queue_destroy(queue); });
  const auto index = hsa_queue_add_write_index_relaxed(queue, 1);
  auto *packet = &static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address)[index & (queue->size - 1)];
  std::memset(packet, 0, sizeof(*packet));
  packet->setup = 1U << HSA_KERNEL_DISPATCH_PACKET_SETUP_DIMENSIONS;
  packet->workgroup_size_x = 64;
  packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = 64;
  packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = kernel_object;
  packet->kernarg_address = arguments;
  packet->completion_signal = completion;
  packet->group_segment_size = group_size;
  packet->private_segment_size = private_size;
  const uint16_t header = (HSA_PACKET_TYPE_KERNEL_DISPATCH << HSA_PACKET_HEADER_TYPE) |
                          (HSA_FENCE_SCOPE_SYSTEM << HSA_PACKET_HEADER_SCACQUIRE_FENCE_SCOPE) |
                          (HSA_FENCE_SCOPE_SYSTEM << HSA_PACKET_HEADER_SCRELEASE_FENCE_SCOPE);
  __atomic_store_n(&packet->header, header, __ATOMIC_RELEASE);
  hsa_signal_store_screlease(queue->doorbell_signal, index);
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(10);
  while (hsa_signal_load_scacquire(completion) == 1 && std::chrono::steady_clock::now() < deadline)
    std::this_thread::yield();
  require(hsa_signal_load_scacquire(completion) == 0, "HSA dispatch failed or timed out");
  if (asynchronous_copy) {
    std::memset(source, 0x93, 256); // A no-op copy must not pass using the original upload.
    hsa_signal_t copied{};
    check(hsa_signal_create(1, 0, nullptr, &copied), "copy signal");
    cleanup.add([=] { hsa_signal_destroy(copied); });
    check(hsa_amd_memory_async_copy(source, agents.cpu, destination, agents.cpu, 256, 1, &completion, copied),
          "dependent asynchronous copy");
    const auto copy_deadline = std::chrono::steady_clock::now() + std::chrono::seconds(10);
    while (hsa_signal_load_scacquire(copied) == 1 && std::chrono::steady_clock::now() < copy_deadline)
      std::this_thread::yield();
    require(hsa_signal_load_scacquire(copied) == 0, "dependent HSA copy failed or timed out");
  }
  std::vector<int> actual(64);
  check(hsa_memory_copy(actual.data(), asynchronous_copy ? source : destination, 256), "download");
  require(actual == expected, "HSA dispatch result mismatch");
  for (std::size_t i = 0; i < 256; ++i)
    require(static_cast<unsigned char *>(guard)[i] == 0x5a, "unused argument was modified");
}
} // namespace
namespace unified_suite::program {
// Fixed API contract probes: their named geometry is independent of the portable size matrix.
std::vector<Case> edge_case_cases(const Configuration &) {
  return {
      {"edge-cases/runtime/hsa/code-object-dispatch/i32/n64", [] { return capture([] { execute(false); }); }},
      {"edge-cases/runtime/hsa/async-copy-after-dispatch/i32/n64",
       [] { return capture([] { execute(true); }); }}};
}
} // namespace unified_suite::program
