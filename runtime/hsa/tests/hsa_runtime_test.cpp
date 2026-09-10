// SPDX-License-Identifier: Apache-2.0
#include <algorithm>
#include <array>
#include <atomic>
#include <cassert>
#include <chrono>
#include <cstdlib>
#include <cstring>
#include <optional>
#include <string>
#include <thread>
#include <vector>

#include <dirent.h>

#include "../../../native/execution/test/rpc_fixture.hpp"
#include "code_object_fixture.hpp"
#include "gemstone/hsa/global.h"
#include <hsa/hsa.h>
#include <hsa/hsa_ext_amd.h>

namespace {
size_t thread_count() {
  DIR *directory = opendir("/proc/self/task");
  assert(directory != nullptr);
  size_t count = 0;
  while (const auto *entry = readdir(directory))
    if (entry->d_name[0] != '.') ++count;
  closedir(directory);
  return count;
}
} // namespace

int main() {
  // The public provider must fail closed when no authenticated Gemstone
  // execution endpoint is configured; it must not fall back to a physical
  // ROCr installation in the process.
  unsetenv("GEMSTONE_RPC_PORT");
  unsetenv("GEMSTONE_RPC_TOKEN");
  assert(hsa_init() == HSA_STATUS_ERROR_NOT_INITIALIZED);

  const char *message = nullptr;
  assert(hsa_status_string(HSA_STATUS_ERROR_NOT_INITIALIZED, &message) == HSA_STATUS_SUCCESS);
  assert(message != nullptr && std::strcmp(message, "runtime not initialized") == 0);
  uint16_t version_major = 0;
  assert(hsa_system_get_info(HSA_SYSTEM_INFO_VERSION_MAJOR, &version_major) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  uint32_t validation = 0;
  assert(hsa_executable_validate(hsa_executable_t{1}, &validation) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  volatile hsa_signal_value_t *signal_value = nullptr;
  assert(hsa_amd_signal_value_pointer(hsa_signal_t{1}, &signal_value) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_signal_async_handler(hsa_signal_t{1}, HSA_SIGNAL_CONDITION_EQ, 0, nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_memory_lock(nullptr, 1, nullptr, 0, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_memory_unlock(nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_pointer_info(nullptr, nullptr, nullptr, nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_profiling_async_copy_enable(false) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_profiling_get_async_copy_time(hsa_signal_t{1}, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_svm_attributes_set(nullptr, 0, nullptr, 0) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_amd_register_system_event_handler(nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  assert(hsa_shut_down() == HSA_STATUS_ERROR_NOT_INITIALIZED);
  std::array<uint8_t, 64> table{};
  table.fill(0xa5);
  assert(hsa_system_get_major_extension_table(HSA_EXTENSION_AMD_LOADER, 1, table.size(), table.data()) == HSA_STATUS_ERROR_NOT_INITIALIZED);
  gemstone_test::RpcFixture fixture;
  const auto port = std::to_string(fixture.port());
  assert(setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0);
  assert(setenv("GEMSTONE_RPC_TOKEN", gemstone_test::RpcFixture::token, 1) == 0);
  assert(hsa_init() == HSA_STATUS_SUCCESS);
  assert(hsa_init() == HSA_STATUS_SUCCESS);
  assert(hsa_shut_down() == HSA_STATUS_SUCCESS);
  assert(hsa_system_get_info(HSA_SYSTEM_INFO_VERSION_MAJOR, &version_major) == HSA_STATUS_SUCCESS);
  assert(hsa_system_get_major_extension_table(HSA_EXTENSION_AMD_LOADER, 1, table.size(), table.data())
         == HSA_STATUS_ERROR_INVALID_ARGUMENT);
  assert(hsa_system_get_major_extension_table(UINT16_MAX, 1, 0, nullptr) == HSA_STATUS_ERROR_INVALID_ARGUMENT);
  for (auto byte : table)
    assert(byte == 0xa5);
  std::vector<hsa_agent_t> agents;
  assert(hsa_iterate_agents(
             [](hsa_agent_t agent, void *data) {
               static_cast<std::vector<hsa_agent_t> *>(data)->push_back(agent);
               return HSA_STATUS_SUCCESS;
             },
             &agents)
         == HSA_STATUS_SUCCESS);
  hsa_agent_t gpu{};
  for (auto agent : agents) {
    hsa_device_type_t type{};
    assert(hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) == HSA_STATUS_SUCCESS);
    if (type == HSA_DEVICE_TYPE_GPU) gpu = agent;
  }
  assert(gpu.handle != 0);
  hsa_profile_t profile{};
  assert(hsa_agent_get_info(gpu, HSA_AGENT_INFO_PROFILE, &profile) == HSA_STATUS_SUCCESS);
  assert(profile == HSA_PROFILE_BASE);
  for (auto attribute : {HSA_AMD_SYSTEM_INFO_SVM_SUPPORTED, HSA_AMD_SYSTEM_INFO_SVM_ACCESSIBLE_BY_DEFAULT}) {
    bool supported = true;
    assert(hsa_system_get_info(attribute, &supported) == HSA_STATUS_SUCCESS);
    assert(!supported);
  }
  struct BoolGuard {
    bool value;
    uint8_t guard[3];
  } cooperative{true, {0xa5, 0xa5, 0xa5}};
  assert(hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COOPERATIVE_QUEUES), &cooperative.value)
         == HSA_STATUS_SUCCESS);
  assert(!cooperative.value);
  for (auto byte : cooperative.guard)
    assert(byte == 0xa5);
  // RDNA applications size WGP dispatches from half the advertised CU count.
  for (const auto *target : {"gfx906", "gfx1030", "gfx1100"}) {
    assert(setenv("GEMSTONE_AMDGPU_TARGET", target, 1) == 0);
    uint32_t compute_units = 0, cooperative_units = 0;
    assert(hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COMPUTE_UNIT_COUNT), &compute_units)
           == HSA_STATUS_SUCCESS);
    assert(hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COOPERATIVE_COMPUTE_UNIT_COUNT), &cooperative_units)
           == HSA_STATUS_SUCCESS);
    assert(compute_units == (std::strcmp(target, "gfx906") == 0 ? 1 : 2));
    assert(cooperative_units == compute_units);
  }
  hsa_amd_hdp_flush_t hdp{};
  assert(hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_HDP_FLUSH), &hdp) == HSA_STATUS_SUCCESS);
  assert(hdp.HDP_MEM_FLUSH_CNTL == nullptr && hdp.HDP_REG_FLUSH_CNTL == nullptr);
  for (auto attribute :
       {HSA_AMD_AGENT_INFO_CHIP_ID, HSA_AMD_AGENT_INFO_BDFID, HSA_AMD_AGENT_INFO_DOMAIN, HSA_AMD_AGENT_INFO_MEMORY_WIDTH,
        HSA_AMD_AGENT_INFO_ASIC_REVISION, HSA_AMD_AGENT_INFO_MAX_CLOCK_FREQUENCY, HSA_AMD_AGENT_INFO_MEMORY_MAX_FREQUENCY}) {
    struct WordGuard {
      uint32_t value;
      uint32_t guard;
    } word{UINT32_MAX, 0xa5a5a5a5};
    assert(hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(attribute), &word.value) == HSA_STATUS_SUCCESS);
    assert(word.value == 0 && word.guard == 0xa5a5a5a5);
  }
  std::vector<hsa_amd_memory_pool_t> pools;
  assert(hsa_amd_agent_iterate_memory_pools(
             gpu,
             [](hsa_amd_memory_pool_t pool, void *data) {
               static_cast<std::vector<hsa_amd_memory_pool_t> *>(data)->push_back(pool);
               return HSA_STATUS_SUCCESS;
             },
             &pools)
         == HSA_STATUS_SUCCESS);
  assert(pools.size() == 2);
  for (auto pool : pools) {
    hsa_amd_segment_t segment{};
    assert(hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_SEGMENT, &segment) == HSA_STATUS_SUCCESS);
    void *allocation = nullptr;
    if (segment == HSA_AMD_SEGMENT_GROUP) {
      bool allowed = true;
      size_t size = 0;
      assert(hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_ALLOWED, &allowed) == HSA_STATUS_SUCCESS);
      assert(hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_SIZE, &size) == HSA_STATUS_SUCCESS);
      assert(!allowed && size == 65536);
      uint32_t flags = UINT32_MAX;
      assert(hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_GLOBAL_FLAGS, &flags) == HSA_STATUS_SUCCESS);
      assert(flags == 0);
      assert(hsa_amd_memory_pool_allocate(pool, 64, 0, &allocation) == HSA_STATUS_ERROR_INVALID_ALLOCATION);
    } else {
      assert(segment == HSA_AMD_SEGMENT_GLOBAL);
      assert(hsa_amd_memory_pool_allocate(pool, 64, HSA_AMD_MEMORY_POOL_CONTIGUOUS_FLAG, &allocation) == HSA_STATUS_ERROR_INVALID_ARGUMENT);
      assert(hsa_amd_memory_pool_allocate(pool, 64, HSA_AMD_MEMORY_POOL_EXECUTABLE_FLAG, &allocation) == HSA_STATUS_SUCCESS);
      assert(allocation != nullptr);
      assert(hsa_amd_memory_pool_free(allocation) == HSA_STATUS_SUCCESS);
    }
  }
  uint32_t engines = 0;
  assert(hsa_amd_memory_copy_engine_status(gpu, agents.front(), &engines) == HSA_STATUS_SUCCESS);
  assert(engines == HSA_AMD_SDMA_ENGINE_0);
  engines = 0;
  assert(hsa_amd_memory_get_preferred_copy_engine(gpu, agents.front(), &engines) == HSA_STATUS_SUCCESS);
  assert(engines == HSA_AMD_SDMA_ENGINE_0);
  assert(hsa_amd_memory_copy_engine_status(gpu, agents.front(), nullptr) == HSA_STATUS_ERROR_INVALID_ARGUMENT);
  assert(hsa_amd_memory_copy_engine_status(hsa_agent_t{}, agents.front(), &engines) == HSA_STATUS_ERROR_INVALID_AGENT);
  hsa_signal_t signal{};
  assert(hsa_signal_create(1, 0, nullptr, &signal) == HSA_STATUS_SUCCESS);
  const size_t threads_before = thread_count();
  std::atomic_uint callbacks{0};
  for (size_t index = 0; index < 64; ++index)
    assert(hsa_amd_signal_async_handler(
               signal, HSA_SIGNAL_CONDITION_EQ, 7,
               [](hsa_signal_value_t, void *data) {
                 static_cast<std::atomic_uint *>(data)->fetch_add(1);
                 return false;
               },
               &callbacks)
           == HSA_STATUS_SUCCESS);
  std::this_thread::sleep_for(std::chrono::milliseconds(20));
  assert(thread_count() <= threads_before + 5);
  hsa_signal_silent_store_relaxed(signal, 7);
  const auto callbacks_deadline = std::chrono::steady_clock::now() + std::chrono::seconds(2);
  while (callbacks.load() != 64 && std::chrono::steady_clock::now() < callbacks_deadline)
    std::this_thread::yield();
  assert(callbacks.load() == 64);
  assert(hsa_signal_load_relaxed(signal) == 7);
  hsa_signal_silent_store_screlease(signal, -3);
  assert(hsa_signal_load_scacquire(signal) == -3);
  assert(hsa_signal_destroy(signal) == HSA_STATUS_SUCCESS);
  // Vendor runtimes may omit dispatch completion signals and append a later
  // barrier. Before the next packet can upload host mirrors, the consumed
  // packet's device writes must already be visible in those mirrors.
  const auto image = gemstone::hsa::test::code_object_fixture(1, true);
  hsa_code_object_reader_t reader{};
  hsa_executable_t executable{};
  assert(hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS);
  hsa_executable_symbol_t kernel{};
  uint64_t object = 0;
  assert(hsa_executable_get_symbol_by_name(executable, "add", &gpu, &kernel) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_symbol_get_info(kernel, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS);
  hsa_queue_t *queue = nullptr;
  assert(hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS);
  void *output = nullptr;
  assert(hsa_amd_memory_pool_allocate(pools.front(), 12 * sizeof(uint32_t), 0, &output) == HSA_STATUS_SUCCESS);
  fixture.checkAllocationAddress(output);
  struct alignas(16) Arguments {
    void *output;
    uint32_t seed;
  };
  void *argument_memory = nullptr;
  assert(hsa_amd_memory_pool_allocate(pools.front(), sizeof(Arguments), 0, &argument_memory) == HSA_STATUS_SUCCESS);
  auto *arguments = static_cast<Arguments *>(argument_memory);
  *arguments = Arguments{output, 37};
  fixture.expect({4, 3, 12, 0, arguments->seed});
  auto *packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address);
  std::memset(packet, 0, sizeof(*packet));
  packet->setup = 1;
  packet->workgroup_size_x = 4;
  packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = 12;
  packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = object;
  packet->kernarg_address = arguments;
  assert(packet->completion_signal.handle == 0);
  __atomic_store_n(&packet->header, uint16_t(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
  hsa_queue_store_write_index_screlease(queue, 1);
  hsa_signal_store_screlease(queue->doorbell_signal, 0);
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
  while (hsa_queue_load_read_index_scacquire(queue) != 1 && std::chrono::steady_clock::now() < deadline)
    std::this_thread::yield();
  assert(hsa_queue_load_read_index_scacquire(queue) == 1);
  for (uint32_t index = 0; index < 12; ++index)
    assert(static_cast<uint32_t *>(output)[index] == 37 + index);
  fixture.check();
  assert(hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_free(output) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_free(argument_memory) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS);
  assert(hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS);
  // Generic ELF variables, including initialized data, BSS and aliases, use
  // the same allocation store as ordinary pointer arguments and host copies.
  const auto globals_image = gemstone::hsa::test::variable_kernel_object_fixture();
  fixture.expectGlobalBinding("arbitrary_data", 0, {5, 6, 7, 8, 9, 10, 11, 12});
  fixture.expectGlobalBinding("data_alias", 0, {5, 6, 7, 8, 9, 10, 11, 12});
  fixture.expectGlobalBinding("arbitrary_bss", 0, std::vector<uint8_t>(32));
  assert(hsa_code_object_reader_create_from_memory(globals_image.data(), globals_image.size(), &reader) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS);
  fixture.checkGlobalBindings();
  const auto global_address = [&](const char *name, uint32_t expected_size) {
    hsa_executable_symbol_t symbol{};
    uint64_t address = 0;
    uint32_t size = UINT32_MAX;
    hsa_symbol_kind_t type{};
    assert(hsa_executable_get_symbol_by_name(executable, name, &gpu, &symbol) == HSA_STATUS_SUCCESS);
    assert(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_TYPE, &type) == HSA_STATUS_SUCCESS);
    assert(type == HSA_SYMBOL_KIND_VARIABLE);
    assert(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_SIZE, &size) == HSA_STATUS_SUCCESS);
    assert(size == expected_size);
    assert(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_ADDRESS, &address) == HSA_STATUS_SUCCESS);
    return reinterpret_cast<void *>(address);
  };
  auto *data = static_cast<uint8_t *>(global_address("arbitrary_data", 8));
  assert(data == global_address("data_alias", 8));
  for (uint8_t index = 0; index < 8; ++index)
    assert(data[index] == index + 5);
  auto *bss = static_cast<uint32_t *>(global_address("arbitrary_bss", 32));
  for (uint32_t index = 0; index < 8; ++index)
    assert(bss[index] == 0);
  assert(global_address("zero_global", 0) == nullptr);
  hsa_executable_symbol_t absent{};
  assert(hsa_executable_get_symbol_by_name(executable, "undefined_global", &gpu, &absent) == HSA_STATUS_ERROR_INVALID_SYMBOL_NAME);
  assert(hsa_executable_get_symbol_by_name(executable, "__omp_rtl_device_memory_pool", &gpu, &absent)
         == HSA_STATUS_ERROR_INVALID_SYMBOL_NAME);
  assert(hsa_amd_memory_pool_free(data) == HSA_STATUS_ERROR_INVALID_ALLOCATION);
  uint32_t replacement = 0x12345678, observed = 0;
  assert(hsa_memory_copy(data + 4, &replacement, 4) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_copy(&observed, static_cast<uint8_t *>(global_address("data_alias", 8)) + 4, 4) == HSA_STATUS_SUCCESS);
  assert(observed == replacement);
  uint64_t fresh_host_value = 0;
  assert(hsa_memory_register(&fresh_host_value, sizeof(fresh_host_value)) == HSA_STATUS_SUCCESS);
  fixture.checkAllocationAddress(&fresh_host_value);
  fresh_host_value = 0x1122334455667788;
  assert(gemstone_hsa_executable_write_global(executable, "arbitrary_data", &fresh_host_value, sizeof(fresh_host_value), 0)
         == HSA_STATUS_SUCCESS);
  uint64_t copied_host_value = 0;
  assert(hsa_memory_copy(&copied_host_value, data, sizeof(copied_host_value)) == HSA_STATUS_SUCCESS);
  assert(copied_host_value == fresh_host_value);
  // A pointer stored in a program global must match the native address used by typed aliased arguments.
  void *ordinary_pointer = &fresh_host_value;
  assert(hsa_memory_copy(data, &ordinary_pointer, sizeof(ordinary_pointer)) == HSA_STATUS_SUCCESS);
  fixture.checkAllocationWord(data, 0, reinterpret_cast<uint64_t>(ordinary_pointer));
  assert(hsa_memory_deregister(&fresh_host_value, sizeof(fresh_host_value)) == HSA_STATUS_SUCCESS);
  fixture.checkAllocationAddress(&fresh_host_value, false);
  void *pointer_value = bss, *pointer_result = nullptr;
  assert(hsa_memory_copy(data, &pointer_value, sizeof(pointer_value)) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_copy(&pointer_result, global_address("data_alias", 8), sizeof(pointer_result)) == HSA_STATUS_SUCCESS);
  assert(pointer_result == bss);
  // An ordinary scalar equal to an ELF address is not a pointer initializer.
  uint64_t scalar_address = 0x1004, scalar_result = 0;
  assert(hsa_memory_copy(bss, &scalar_address, sizeof(scalar_address)) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_copy(&scalar_result, bss, sizeof(scalar_result)) == HSA_STATUS_SUCCESS);
  assert(scalar_result == scalar_address);
  assert(hsa_executable_get_symbol_by_name(executable, "add", &gpu, &kernel) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_symbol_get_info(kernel, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS);
  assert(hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_allocate(pools.front(), sizeof(Arguments), 0, &argument_memory) == HSA_STATUS_SUCCESS);
  arguments = static_cast<Arguments *>(argument_memory);
  *arguments = Arguments{bss, 81};
  fixture.expect({4, 2, 8, 0, 81});
  packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address);
  std::memset(packet, 0, sizeof(*packet));
  packet->setup = 1;
  packet->workgroup_size_x = 4;
  packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = 8;
  packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = object;
  packet->kernarg_address = arguments;
  __atomic_store_n(&packet->header, uint16_t(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
  hsa_queue_store_write_index_screlease(queue, 1);
  hsa_signal_store_screlease(queue->doorbell_signal, 0);
  const auto global_deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
  while (hsa_queue_load_read_index_scacquire(queue) != 1 && std::chrono::steady_clock::now() < global_deadline)
    std::this_thread::yield();
  assert(hsa_queue_load_read_index_scacquire(queue) == 1);
  for (uint32_t index = 0; index < 8; ++index)
    assert(bss[index] == 81 + index);
  fixture.check();
  assert(hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_free(argument_memory) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_free(data) == HSA_STATUS_ERROR_INVALID_ALLOCATION);
  assert(hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS);
  // A child is reachable only through pointer data, never as a direct kernarg.
  // Its host writes must be visible without re-uploading unrelated device-only buffers.
  std::vector<uint32_t> child(32768, 0);
  child[0] = 1;
  child[17000] = 37;
  assert(hsa_memory_register(child.data(), child.size() * sizeof(uint32_t)) == HSA_STATUS_SUCCESS);
  void *table_memory = nullptr, *device_only = nullptr;
  assert(hsa_amd_memory_pool_allocate(pools.front(), 32, 0, &table_memory) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_allocate(pools.front(), 128 * 1024, 0, &device_only) == HSA_STATUS_SUCCESS);
  std::memset(device_only, 0x5a, 128 * 1024);
  assert(hsa_amd_memory_pool_allocate(pools.front(), sizeof(Arguments), 0, &argument_memory) == HSA_STATUS_SUCCESS);
  arguments = static_cast<Arguments *>(argument_memory);
  assert(hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_get_symbol_by_name(executable, "add", &gpu, &kernel) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_symbol_get_info(kernel, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS);
  assert(hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS);
  for (uint32_t iteration = 0; iteration < 6; ++iteration) {
    if (iteration == 1) child[17000] = 37; // CPU restores the value preceding the device's write.
    if (iteration == 2) child[17000] = 73;
    if (iteration == 5) child[0] = 101;
    const uint32_t expected = iteration < 2 ? 37 : iteration < 4 ? 73 : iteration == 4 ? 91 : 111;
    void *output_memory = iteration < 4 ? table_memory : child.data();
    auto *nested = &child[17000];
    std::memcpy(table_memory, &nested, sizeof(nested));
    *arguments = Arguments{output_memory, expected};
    fixture.expect({4, 2, 8, 0, expected});
    if (iteration < 4) fixture.expectIndirectInput(table_memory, expected, iteration == 0 ? std::optional<uint32_t>(55) : std::nullopt);
    packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address) + iteration;
    std::memset(packet, 0, sizeof(*packet));
    packet->setup = 1;
    packet->workgroup_size_x = 4;
    packet->workgroup_size_y = packet->workgroup_size_z = 1;
    packet->grid_size_x = 8;
    packet->grid_size_y = packet->grid_size_z = 1;
    packet->kernel_object = object;
    packet->kernarg_address = arguments;
    __atomic_store_n(&packet->header, uint16_t(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
    hsa_queue_store_write_index_screlease(queue, iteration + 1);
    hsa_signal_store_screlease(queue->doorbell_signal, iteration);
    const auto end = std::chrono::steady_clock::now() + std::chrono::seconds(5);
    while (hsa_queue_load_read_index_scacquire(queue) != iteration + 1 && std::chrono::steady_clock::now() < end)
      std::this_thread::yield();
    assert(hsa_queue_load_read_index_scacquire(queue) == iteration + 1);
    for (uint32_t index = 0; index < 8; ++index)
      assert(static_cast<uint32_t *>(output_memory)[index] == expected + index);
    assert(child[17000] == (iteration == 0 ? 55 : iteration == 1 ? 37 : 73));
    fixture.check();
    assert(fixture.writtenBytes(child.data()) == uint64_t(128 + 64 * std::min(iteration, 2U) + (iteration == 5 ? 64 : 0)) * 1024);
    assert(fixture.writtenBytes(device_only) == 0);
  }
  assert(hsa_shut_down() == HSA_STATUS_ERROR_INVALID_RUNTIME_STATE);
  assert(hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS);
  assert(hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS);
  assert(hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_free(table_memory) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_free(device_only) == HSA_STATUS_SUCCESS);
  assert(hsa_amd_memory_pool_free(argument_memory) == HSA_STATUS_SUCCESS);
  assert(hsa_memory_deregister(child.data(), child.size() * sizeof(uint32_t)) == HSA_STATUS_SUCCESS);
  assert(hsa_shut_down() == HSA_STATUS_SUCCESS);
  return 0;
}
