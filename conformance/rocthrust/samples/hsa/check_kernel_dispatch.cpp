#pragma region case: hsa-kernel-dispatch
#pragma region do: {cxx} -std=c++17 -O2 -Wall -Wextra -Werror -Wno-pedantic -Wno-c++20-compat -I{hsa_include} {input} -L{rocr_lib} -Wl,-rpath,{rocr_lib} -lhsa-runtime64 -ldl -o {output}
#pragma region do: {output} {hsa_vector_copy}
#pragma region requires: pass
// SPDX-License-Identifier: Apache-2.0
#include <cassert>
#include <cstdint>
#include <cstdio>
#include <cstring>
#include <fstream>
#include <iterator>
#include <thread>
#include <vector>

#include "hsa.h"

namespace {

struct Agents {
  hsa_agent_t cpu{};
  hsa_agent_t gpu{};
};

hsa_status_t collect_agents(hsa_agent_t agent, void *data) {
  hsa_device_type_t type = HSA_DEVICE_TYPE_CPU;
  if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
  auto &agents = *static_cast<Agents *>(data);
  if (type == HSA_DEVICE_TYPE_CPU) agents.cpu = agent;
  if (type == HSA_DEVICE_TYPE_GPU) agents.gpu = agent;
  return HSA_STATUS_SUCCESS;
}

hsa_status_t find_kernarg(hsa_region_t region, void *data) {
  hsa_region_segment_t segment = HSA_REGION_SEGMENT_READONLY;
  hsa_region_global_flag_t flags{};
  if (hsa_region_get_info(region, HSA_REGION_INFO_SEGMENT, &segment) != HSA_STATUS_SUCCESS || segment != HSA_REGION_SEGMENT_GLOBAL
      || hsa_region_get_info(region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags) != HSA_STATUS_SUCCESS)
    return HSA_STATUS_SUCCESS;
  if ((flags & HSA_REGION_GLOBAL_FLAG_KERNARG) != 0) {
    *static_cast<hsa_region_t *>(data) = region;
    return HSA_STATUS_INFO_BREAK;
  }
  return HSA_STATUS_SUCCESS;
}

} // namespace

int main(int argc, char **argv) {
  assert(argc == 2);
  std::ifstream stream(argv[1], std::ios::binary);
  assert(stream.good());
  const std::vector<uint8_t> image((std::istreambuf_iterator<char>(stream)), std::istreambuf_iterator<char>());
  assert(hsa_init() == HSA_STATUS_SUCCESS);
  Agents agents;
  assert(hsa_iterate_agents(collect_agents, &agents) == HSA_STATUS_SUCCESS);
  assert(agents.cpu.handle != 0 && agents.gpu.handle != 0);

  hsa_region_t kernarg_region{};
  assert(hsa_agent_iterate_regions(agents.cpu, find_kernarg, &kernarg_region) == HSA_STATUS_INFO_BREAK);
  assert(kernarg_region.handle != 0);

  hsa_code_object_reader_t reader{};
  assert(hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS);
  hsa_executable_t executable{};
  assert(hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_DEFAULT, nullptr, &executable) == HSA_STATUS_SUCCESS);
  hsa_loaded_code_object_t loaded{};
  assert(hsa_executable_load_agent_code_object(executable, agents.gpu, reader, nullptr, &loaded) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS);
  hsa_executable_symbol_t symbol{};
  // Code-object v4 exposes the kernel descriptor as `<name>.kd`; follow the
  // same canonical fallback used by ROCr's own rocrtst CodeObject helper.
  hsa_status_t symbol_status = hsa_executable_get_symbol_by_name(executable, "vector_copy", &agents.gpu, &symbol);
  if (symbol_status != HSA_STATUS_SUCCESS)
    symbol_status = hsa_executable_get_symbol_by_name(executable, "vector_copy.kd", &agents.gpu, &symbol);
  assert(symbol_status == HSA_STATUS_SUCCESS);
  uint64_t kernel_object = 0;
  uint32_t kernarg_size = 0;
  assert(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &kernel_object) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_KERNARG_SEGMENT_SIZE, &kernarg_size)
         == HSA_STATUS_SUCCESS);
  assert(kernel_object != 0 && kernarg_size == 80);

  void *kernarg = nullptr;
  void *input = nullptr;
  void *output = nullptr;
  void *unused = nullptr;
  assert(hsa_memory_allocate(kernarg_region, kernarg_size, &kernarg) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_allocate(kernarg_region, 256, &input) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_allocate(kernarg_region, 256, &output) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_allocate(kernarg_region, 256, &unused) == HSA_STATUS_SUCCESS);
  std::vector<int32_t> expected(64);
  for (size_t index = 0; index < expected.size(); ++index)
    expected[index] = static_cast<int32_t>(index * 37) - 511;
  std::memcpy(input, expected.data(), expected.size() * sizeof(expected.front()));
  std::memset(output, 0xa5, 256);
  std::memset(unused, 0x5a, 256);
  std::memset(kernarg, 0, kernarg_size);
  std::memcpy(static_cast<uint8_t *>(kernarg), &input, sizeof(input));
  std::memcpy(static_cast<uint8_t *>(kernarg) + 8, &output, sizeof(output));
  std::memcpy(static_cast<uint8_t *>(kernarg) + 16, &unused, sizeof(unused));

  hsa_signal_t completion{};
  assert(hsa_signal_create(1, 0, nullptr, &completion) == HSA_STATUS_SUCCESS);
  hsa_queue_t *queue = nullptr;
  assert(hsa_queue_create(agents.gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS);
  const uint64_t index = hsa_queue_add_write_index_relaxed(queue, 1);
  auto *packet = &static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address)[index & (queue->size - 1)];
  std::memset(packet, 0, sizeof(*packet));
  packet->setup = 1U << HSA_KERNEL_DISPATCH_PACKET_SETUP_DIMENSIONS;
  packet->workgroup_size_x = 64;
  packet->workgroup_size_y = 1;
  packet->workgroup_size_z = 1;
  packet->grid_size_x = 64;
  packet->grid_size_y = 1;
  packet->grid_size_z = 1;
  packet->kernel_object = kernel_object;
  packet->kernarg_address = kernarg;
  packet->completion_signal = completion;
  const uint16_t header = HSA_PACKET_TYPE_KERNEL_DISPATCH << HSA_PACKET_HEADER_TYPE;
  __atomic_store_n(&packet->header, header, __ATOMIC_RELEASE);
  hsa_signal_store_screlease(queue->doorbell_signal, index);
  while (hsa_signal_load_scacquire(completion) == 1)
    std::this_thread::yield();
  assert(hsa_signal_load_scacquire(completion) == 0);
  const auto *actual = static_cast<const int32_t *>(output);
  for (size_t element = 0; element < expected.size(); ++element) {
    if (actual[element] != expected[element]) {
      std::fprintf(stderr, "vector_copy mismatch at element %zu: expected %d, got %d\n", element, expected[element], actual[element]);
      return 3;
    }
  }
  const auto *unused_bytes = static_cast<const uint8_t *>(unused);
  for (size_t index = 0; index < 256; ++index)
    assert(unused_bytes[index] == 0x5a);

  assert(hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS);
  assert(hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_free(unused) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_free(output) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_free(input) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_free(kernarg) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS);
  assert(hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS);
  assert(hsa_shut_down() == HSA_STATUS_SUCCESS);
  std::puts("pass");
  return 0;
}
