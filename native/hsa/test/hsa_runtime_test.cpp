#include <algorithm>
#include <array>
#include <atomic>
#include <chrono>
#include <cstdlib>
#include <cstring>
#include <memory>
#include <optional>
#include <string>
#include <thread>
#include <utility>
#include <vector>

#include <hsa.h>
#include <hsa_ext_amd.h>

#include <catch2/catch_test_macros.hpp>

#include "gemstone/hsa/global.h"
#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "code_object_fixture.hpp"
#include <environment_keys.hpp>

TEST_CASE("HSA agent wave size follows the processor while legacy calling convention remains wave64", "[hsa]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((hsa_init() == HSA_STATUS_SUCCESS));
  hsa_agent_t gpu{};
  REQUIRE((hsa_iterate_agents(
               [](hsa_agent_t agent, void *data) {
                 hsa_device_type_t type{};
                 if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
                 if (type == HSA_DEVICE_TYPE_GPU) *static_cast<hsa_agent_t *>(data) = agent;
                 else {
                   uint32_t width = 0;
                   if (hsa_agent_get_info(agent, HSA_AGENT_INFO_WAVEFRONT_SIZE, &width) != HSA_STATUS_SUCCESS || width != 1)
                     return HSA_STATUS_ERROR;
                 }
                 return HSA_STATUS_SUCCESS;
               },
               &gpu)
           == HSA_STATUS_SUCCESS));
  REQUIRE((gpu.handle != 0));
  hsa_isa_t isa{};
  REQUIRE((hsa_agent_iterate_isas(
               gpu,
               [](hsa_isa_t value, void *data) {
                 *static_cast<hsa_isa_t *>(data) = value;
                 return HSA_STATUS_SUCCESS;
               },
               &isa)
           == HSA_STATUS_SUCCESS));
  for (const auto &[target, expected] : std::array<std::pair<const char *, uint32_t>, 6>{
           {{"gfx900", 64}, {"gfx90a", 64}, {"gfx1036", 32}, {"gfx1200", 32}, {"gfx1250", 32}, {"gfx1251", 32}}}) {
    const gemstone_test::Environment selected(gemstone::environment::TargetArch, target);
    INFO(target);
    uint32_t width = 0;
    REQUIRE((hsa_agent_get_info(gpu, HSA_AGENT_INFO_WAVEFRONT_SIZE, &width) == HSA_STATUS_SUCCESS));
    CHECK((width == expected));
    REQUIRE((hsa_isa_get_info_alt(isa, HSA_ISA_INFO_CALL_CONVENTION_INFO_WAVEFRONT_SIZE, &width) == HSA_STATUS_SUCCESS));
    CHECK((width == 64)); // Native gfx1036 also reports 64 for this deprecated ISA property.
  }
  REQUIRE((hsa_shut_down() == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA system paging ISA feature", "[hsa]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((setenv(gemstone::environment::SystemPaging, "1", 1) == 0));
  REQUIRE((hsa_init() == HSA_STATUS_SUCCESS));
  hsa_agent_t gpu{};
  REQUIRE((hsa_iterate_agents(
               [](hsa_agent_t agent, void *data) {
                 hsa_device_type_t type{};
                 if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
                 if (type == HSA_DEVICE_TYPE_GPU) *static_cast<hsa_agent_t *>(data) = agent;
                 return HSA_STATUS_SUCCESS;
               },
               &gpu)
           == HSA_STATUS_SUCCESS));
  REQUIRE((gpu.handle != 0));
  hsa_isa_t isa{};
  REQUIRE((hsa_agent_iterate_isas(
               gpu,
               [](hsa_isa_t value, void *data) {
                 *static_cast<hsa_isa_t *>(data) = value;
                 return HSA_STATUS_SUCCESS;
               },
               &isa)
           == HSA_STATUS_SUCCESS));
  uint32_t length = 0;
  REQUIRE((hsa_isa_get_info_alt(isa, HSA_ISA_INFO_NAME_LENGTH, &length) == HSA_STATUS_SUCCESS));
  std::string name(length, '\0');
  REQUIRE((hsa_isa_get_info_alt(isa, HSA_ISA_INFO_NAME, name.data()) == HSA_STATUS_SUCCESS));
  REQUIRE((name.find("xnack+") != std::string::npos));
  std::array<uint32_t, 2> host_values{41, 43};
  void *agent_pointer = nullptr;
  REQUIRE((hsa_amd_memory_lock(host_values.data(), sizeof(host_values), nullptr, 0, &agent_pointer) == HSA_STATUS_SUCCESS));
  REQUIRE((agent_pointer == host_values.data()));
  void *second_agent_pointer = nullptr;
  REQUIRE((hsa_amd_memory_lock(host_values.data(), sizeof(host_values), nullptr, 0, &second_agent_pointer) == HSA_STATUS_SUCCESS));
  REQUIRE((second_agent_pointer == host_values.data()));
  hsa_amd_pointer_info_t info{};
  info.size = sizeof(info);
  REQUIRE((hsa_amd_pointer_info(host_values.data(), &info, nullptr, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((info.type == HSA_EXT_POINTER_TYPE_LOCKED));
  REQUIRE((hsa_amd_memory_unlock(host_values.data()) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_unlock(host_values.data()) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_shut_down() == HSA_STATUS_SUCCESS));
  unsetenv(gemstone::environment::SystemPaging);
}

TEST_CASE("HSA runtime facade", "[hsa]") {
  // The public provider must fail closed when no identified Gemstone
  // execution endpoint is configured; it must not fall back to a physical
  // ROCr installation in the process.
  const gemstone_test::Environment noEndpoint(gemstone::environment::RpcEndpoint, nullptr);
  const gemstone_test::Environment noClient(gemstone::environment::RpcClient, nullptr);
  REQUIRE((hsa_init() == HSA_STATUS_ERROR_NOT_INITIALIZED));

  const char *message = nullptr;
  REQUIRE((hsa_status_string(HSA_STATUS_ERROR_NOT_INITIALIZED, &message) == HSA_STATUS_SUCCESS));
  REQUIRE((message && std::strcmp(message, "runtime not initialized") == 0));
  uint16_t version_major = 0;
  REQUIRE((hsa_system_get_info(HSA_SYSTEM_INFO_VERSION_MAJOR, &version_major) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  uint32_t validation = 0;
  REQUIRE((hsa_executable_validate(hsa_executable_t{1}, &validation) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  volatile hsa_signal_value_t *signal_value = nullptr;
  REQUIRE((hsa_amd_signal_value_pointer(hsa_signal_t{1}, &signal_value) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE(
      (hsa_amd_signal_async_handler(hsa_signal_t{1}, HSA_SIGNAL_CONDITION_EQ, 0, nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE((hsa_amd_memory_lock(nullptr, 1, nullptr, 0, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE((hsa_amd_memory_unlock(nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE((hsa_amd_pointer_info(nullptr, nullptr, nullptr, nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE((hsa_amd_profiling_async_copy_enable(false) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE((hsa_amd_profiling_get_async_copy_time(hsa_signal_t{1}, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE((hsa_amd_svm_attributes_set(nullptr, 0, nullptr, 0) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE((hsa_amd_register_system_event_handler(nullptr, nullptr) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  REQUIRE((hsa_shut_down() == HSA_STATUS_ERROR_NOT_INITIALIZED));
  std::array<uint8_t, 64> table{};
  table.fill(0xa5);
  REQUIRE(
      (hsa_system_get_major_extension_table(HSA_EXTENSION_AMD_LOADER, 1, table.size(), table.data()) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((hsa_init() == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_init() == HSA_STATUS_SUCCESS));
  // A kernel with no explicit or implicit arguments has no kernarg allocation.
  REQUIRE((hsa_memory_free(nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_shut_down() == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_system_get_info(HSA_SYSTEM_INFO_VERSION_MAJOR, &version_major) == HSA_STATUS_SUCCESS));
  REQUIRE(
      (hsa_system_get_major_extension_table(HSA_EXTENSION_AMD_LOADER, 1, table.size(), table.data()) == HSA_STATUS_ERROR_INVALID_ARGUMENT));
  REQUIRE((hsa_system_get_major_extension_table(UINT16_MAX, 1, 0, nullptr) == HSA_STATUS_ERROR_INVALID_ARGUMENT));
  for (auto byte : table)
    REQUIRE((byte == 0xa5));
  std::vector<hsa_agent_t> agents;
  REQUIRE((hsa_iterate_agents(
               [](hsa_agent_t agent, void *data) {
                 static_cast<std::vector<hsa_agent_t> *>(data)->push_back(agent);
                 return HSA_STATUS_SUCCESS;
               },
               &agents)
           == HSA_STATUS_SUCCESS));
  hsa_agent_t gpu{};
  for (auto agent : agents) {
    hsa_device_type_t type{};
    REQUIRE((hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) == HSA_STATUS_SUCCESS));
    if (type == HSA_DEVICE_TYPE_GPU) gpu = agent;
  }
  REQUIRE((gpu.handle != 0));
  hsa_queue_t *single_queue = nullptr;
  REQUIRE(
      (hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_SINGLE, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &single_queue) == HSA_STATUS_SUCCESS));
  REQUIRE((single_queue->type == HSA_QUEUE_TYPE_SINGLE));
  REQUIRE((hsa_queue_destroy(single_queue) == HSA_STATUS_SUCCESS));
  std::vector<hsa_region_t> regions;
  REQUIRE((hsa_agent_iterate_regions(
               gpu,
               [](hsa_region_t region, void *data) {
                 static_cast<std::vector<hsa_region_t> *>(data)->push_back(region);
                 return HSA_STATUS_SUCCESS;
               },
               &regions)
           == HSA_STATUS_SUCCESS));
  hsa_region_t kernarg{}, coarse{};
  for (auto region : regions) {
    hsa_region_segment_t segment{};
    uint32_t flags = 0;
    REQUIRE((hsa_region_get_info(region, HSA_REGION_INFO_SEGMENT, &segment) == HSA_STATUS_SUCCESS));
    REQUIRE((segment == HSA_REGION_SEGMENT_GLOBAL));
    REQUIRE((hsa_region_get_info(region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags) == HSA_STATUS_SUCCESS));
    REQUIRE((bool(flags & HSA_REGION_GLOBAL_FLAG_FINE_GRAINED) != bool(flags & HSA_REGION_GLOBAL_FLAG_COARSE_GRAINED)));
    if (flags & HSA_REGION_GLOBAL_FLAG_KERNARG) {
      REQUIRE((flags & HSA_REGION_GLOBAL_FLAG_FINE_GRAINED));
      kernarg = region;
    }
    if (flags & HSA_REGION_GLOBAL_FLAG_COARSE_GRAINED) coarse = region;
  }
  REQUIRE((kernarg.handle != 0));
  REQUIRE((coarse.handle != 0));
  REQUIRE((kernarg.handle != coarse.handle));
  for (const auto status : {HSA_STATUS_INFO_BREAK, HSA_STATUS_ERROR}) {
    size_t callbacks = 0;
    std::pair<size_t *, hsa_status_t> callback_state{&callbacks, status};
    REQUIRE((hsa_agent_iterate_regions(
                 gpu,
                 [](hsa_region_t, void *data) {
                   auto &state = *static_cast<std::pair<size_t *, hsa_status_t> *>(data);
                   ++*state.first;
                   return state.second;
                 },
                 &callback_state)
             == status));
    REQUIRE((callbacks == 1));
  }
  std::array<uint32_t, 3> payload{19, 23, 29}, region_copy{};
  void *kernarg_memory = nullptr, *device_memory = nullptr;
  REQUIRE((hsa_memory_allocate(kernarg, sizeof(payload), &kernarg_memory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_allocate(coarse, sizeof(payload), &device_memory) == HSA_STATUS_SUCCESS));
  std::memcpy(kernarg_memory, payload.data(), sizeof(payload));
  REQUIRE((hsa_memory_copy(device_memory, kernarg_memory, sizeof(payload)) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(region_copy.data(), device_memory, sizeof(region_copy)) == HSA_STATUS_SUCCESS));
  REQUIRE((region_copy == payload));
  REQUIRE((hsa_memory_free(device_memory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(kernarg_memory) == HSA_STATUS_SUCCESS));
  hsa_profile_t profile{};
  REQUIRE((hsa_agent_get_info(gpu, HSA_AGENT_INFO_PROFILE, &profile) == HSA_STATUS_SUCCESS));
  REQUIRE((profile == HSA_PROFILE_BASE));
  for (auto attribute : {HSA_AMD_SYSTEM_INFO_SVM_SUPPORTED, HSA_AMD_SYSTEM_INFO_SVM_ACCESSIBLE_BY_DEFAULT}) {
    bool supported = true;
    REQUIRE((hsa_system_get_info(attribute, &supported) == HSA_STATUS_SUCCESS));
    REQUIRE((!supported));
  }
  struct BoolGuard {
    bool value;
    uint8_t guard[3];
  } cooperative{true, {0xa5, 0xa5, 0xa5}};
  REQUIRE((hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COOPERATIVE_QUEUES), &cooperative.value)
           == HSA_STATUS_SUCCESS));
  REQUIRE((!cooperative.value));
  for (auto byte : cooperative.guard)
    REQUIRE((byte == 0xa5));
  // RDNA applications size WGP dispatches from half the advertised CU count.
  for (const auto *target : {"gfx906", "gfx1030", "gfx1100"}) {
    REQUIRE((setenv(gemstone::environment::TargetArch, target, 1) == 0));
    uint32_t compute_units = 0, cooperative_units = 0;
    REQUIRE((hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COMPUTE_UNIT_COUNT), &compute_units)
             == HSA_STATUS_SUCCESS));
    REQUIRE((hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COOPERATIVE_COMPUTE_UNIT_COUNT), &cooperative_units)
             == HSA_STATUS_SUCCESS));
    REQUIRE((compute_units == (std::strcmp(target, "gfx906") == 0 ? 1 : 2)));
    REQUIRE((cooperative_units == compute_units));
  }
  hsa_amd_hdp_flush_t hdp{};
  REQUIRE((hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_HDP_FLUSH), &hdp) == HSA_STATUS_SUCCESS));
  REQUIRE((!hdp.HDP_MEM_FLUSH_CNTL && !hdp.HDP_REG_FLUSH_CNTL));
  for (auto attribute :
       {HSA_AMD_AGENT_INFO_CHIP_ID, HSA_AMD_AGENT_INFO_BDFID, HSA_AMD_AGENT_INFO_DOMAIN, HSA_AMD_AGENT_INFO_MEMORY_WIDTH,
        HSA_AMD_AGENT_INFO_ASIC_REVISION, HSA_AMD_AGENT_INFO_MAX_CLOCK_FREQUENCY, HSA_AMD_AGENT_INFO_MEMORY_MAX_FREQUENCY}) {
    struct WordGuard {
      uint32_t value;
      uint32_t guard;
    } word{UINT32_MAX, 0xa5a5a5a5};
    REQUIRE((hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(attribute), &word.value) == HSA_STATUS_SUCCESS));
    REQUIRE((word.value == 0 && word.guard == 0xa5a5a5a5));
  }
  std::vector<hsa_amd_memory_pool_t> pools;
  REQUIRE((hsa_amd_agent_iterate_memory_pools(
               gpu,
               [](hsa_amd_memory_pool_t pool, void *data) {
                 static_cast<std::vector<hsa_amd_memory_pool_t> *>(data)->push_back(pool);
                 return HSA_STATUS_SUCCESS;
               },
               &pools)
           == HSA_STATUS_SUCCESS));
  REQUIRE((pools.size() == 2));
  for (auto pool : pools) {
    hsa_amd_segment_t segment{};
    REQUIRE((hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_SEGMENT, &segment) == HSA_STATUS_SUCCESS));
    void *allocation = nullptr;
    if (segment == HSA_AMD_SEGMENT_GROUP) {
      bool allowed = true;
      size_t size = 0;
      REQUIRE((hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_RUNTIME_ALLOC_ALLOWED, &allowed) == HSA_STATUS_SUCCESS));
      REQUIRE((hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_SIZE, &size) == HSA_STATUS_SUCCESS));
      REQUIRE((!allowed && size == 65536));
      uint32_t flags = UINT32_MAX;
      REQUIRE((hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_GLOBAL_FLAGS, &flags) == HSA_STATUS_SUCCESS));
      REQUIRE((flags == 0));
      REQUIRE((hsa_amd_memory_pool_allocate(pool, 64, 0, &allocation) == HSA_STATUS_ERROR_INVALID_ALLOCATION));
    } else {
      REQUIRE((segment == HSA_AMD_SEGMENT_GLOBAL));
      bool accessible_by_all = true;
      REQUIRE((hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_ACCESSIBLE_BY_ALL, &accessible_by_all) == HSA_STATUS_SUCCESS));
      REQUIRE((!accessible_by_all));
      REQUIRE(
          (hsa_amd_memory_pool_allocate(pool, 64, HSA_AMD_MEMORY_POOL_CONTIGUOUS_FLAG, &allocation) == HSA_STATUS_ERROR_INVALID_ARGUMENT));
      REQUIRE((hsa_amd_memory_pool_allocate(pool, 64, HSA_AMD_MEMORY_POOL_EXECUTABLE_FLAG, &allocation) == HSA_STATUS_SUCCESS));
      REQUIRE((allocation));
      REQUIRE((hsa_amd_memory_pool_free(allocation) == HSA_STATUS_SUCCESS));
    }
  }
  uint32_t engines = 0;
  REQUIRE((hsa_amd_memory_copy_engine_status(gpu, agents.front(), &engines) == HSA_STATUS_SUCCESS));
  REQUIRE((engines == HSA_AMD_SDMA_ENGINE_0));
  engines = 0;
  REQUIRE((hsa_amd_memory_get_preferred_copy_engine(gpu, agents.front(), &engines) == HSA_STATUS_SUCCESS));
  REQUIRE((engines == HSA_AMD_SDMA_ENGINE_0));
  REQUIRE((hsa_amd_memory_copy_engine_status(gpu, agents.front(), nullptr) == HSA_STATUS_ERROR_INVALID_ARGUMENT));
  REQUIRE((hsa_amd_memory_copy_engine_status(hsa_agent_t{}, agents.front(), &engines) == HSA_STATUS_ERROR_INVALID_AGENT));
  hsa_signal_t signal{};
  REQUIRE((hsa_signal_create(1, 0, nullptr, &signal) == HSA_STATUS_SUCCESS));
  const size_t threads_before = gemstone_test::processThreadCount();
  std::atomic_uint callbacks{0};
  for (size_t index = 0; index < 64; ++index)
    REQUIRE((hsa_amd_signal_async_handler(
                 signal, HSA_SIGNAL_CONDITION_EQ, 7,
                 [](hsa_signal_value_t, void *data) {
                   static_cast<std::atomic_uint *>(data)->fetch_add(1);
                   return false;
                 },
                 &callbacks)
             == HSA_STATUS_SUCCESS));
  std::this_thread::sleep_for(std::chrono::milliseconds(20));
  REQUIRE((gemstone_test::processThreadCount() <= threads_before + 5));
  hsa_signal_silent_store_relaxed(signal, 7);
  const auto callbacks_deadline = std::chrono::steady_clock::now() + std::chrono::seconds(2);
  while (callbacks.load() != 64 && std::chrono::steady_clock::now() < callbacks_deadline)
    std::this_thread::yield();
  REQUIRE((callbacks.load() == 64));
  REQUIRE((hsa_signal_load_relaxed(signal) == 7));
  hsa_signal_silent_store_screlease(signal, -3);
  REQUIRE((hsa_signal_load_scacquire(signal) == -3));
  REQUIRE((hsa_signal_exchange_acq_rel(signal, 4) == -3));
  REQUIRE((hsa_signal_cas_scacq_screl(signal, 4, 5) == 4));
  hsa_signal_add_release(signal, 2);
  REQUIRE((hsa_signal_load_scacquire(signal) == 7));
  hsa_signal_and_relaxed(signal, 3);
  REQUIRE((hsa_signal_load_scacquire(signal) == 3));
  hsa_signal_or_acquire(signal, 4);
  hsa_signal_xor_scacquire(signal, 2);
  REQUIRE((hsa_signal_load_scacquire(signal) == 5));
  hsa_signal_t signals[] = {signal};
  hsa_signal_condition_t conditions[] = {HSA_SIGNAL_CONDITION_EQ};
  hsa_signal_value_t comparisons[] = {5};
  hsa_signal_value_t satisfying[] = {0};
  REQUIRE((hsa_amd_signal_wait_any(1, signals, conditions, comparisons, 0, HSA_WAIT_STATE_ACTIVE, satisfying) == 0));
  REQUIRE((satisfying[0] == 5));
  satisfying[0] = 0;
  REQUIRE((hsa_amd_signal_wait_all(1, signals, conditions, comparisons, 0, HSA_WAIT_STATE_ACTIVE, satisfying) == 0));
  REQUIRE((satisfying[0] == 5));
  REQUIRE((hsa_signal_destroy(signal) == HSA_STATUS_SUCCESS));
  // Vendor runtimes may omit dispatch completion signals and append a later
  // barrier. Before the next packet can upload host mirrors, the consumed
  // packet's device writes must already be visible in those mirrors.
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true);
  hsa_code_object_reader_t reader{};
  hsa_code_object_t legacy_object{};
  hsa_executable_t executable{};
  auto serialized = image;
  REQUIRE((hsa_code_object_deserialize(nullptr, serialized.size(), nullptr, &legacy_object) == HSA_STATUS_ERROR_INVALID_ARGUMENT));
  REQUIRE((hsa_code_object_deserialize(serialized.data(), serialized.size(), nullptr, &legacy_object) == HSA_STATUS_SUCCESS));
  std::fill(serialized.begin(), serialized.end(), 0);
  REQUIRE((hsa_code_object_reader_destroy(hsa_code_object_reader_t{legacy_object.handle}) == HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER));
  REQUIRE((hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_code_object(executable, gpu, hsa_code_object_t{}, nullptr) == HSA_STATUS_ERROR_INVALID_CODE_OBJECT));
  REQUIRE((hsa_executable_load_code_object(executable, gpu, legacy_object, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE(fixture.lastProgramImage() == image);
  REQUIRE((hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_code_object(executable, gpu, legacy_object, nullptr) == HSA_STATUS_ERROR_FROZEN_EXECUTABLE));
  hsa_executable_symbol_t kernel{};
  uint64_t object = 0;
  REQUIRE((hsa_executable_get_symbol(executable, nullptr, "add.kd", gpu, 0, &kernel) == HSA_STATUS_SUCCESS));
  hsa_executable_symbol_t modern_kernel{};
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "add", &gpu, &modern_kernel) == HSA_STATUS_SUCCESS));
  REQUIRE(kernel.handle == modern_kernel.handle);
  REQUIRE((hsa_executable_symbol_get_info(kernel, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS));
  hsa_queue_t *queue = nullptr;
  REQUIRE((hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS));
  // One range per group crosses the RPC page boundary. Verify the final group is copied back.
  constexpr uint32_t outputCount = gemstone::execution::MaximumElements + 1;
  void *output = nullptr;
  REQUIRE((hsa_amd_memory_pool_allocate(pools.front(), outputCount * sizeof(uint32_t), 0, &output) == HSA_STATUS_SUCCESS));
  fixture.checkAllocationAddress(output);
  struct alignas(16) Arguments {
    void *output;
    uint32_t seed;
  };
  void *argument_memory = nullptr;
  REQUIRE((hsa_memory_allocate(kernarg, sizeof(Arguments), &argument_memory) == HSA_STATUS_SUCCESS));
  auto *arguments = static_cast<Arguments *>(argument_memory);
  *arguments = Arguments{output, 37};
  fixture.expect({1, outputCount, outputCount, 0, arguments->seed});
  auto *packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address);
  std::memset(packet, 0, sizeof(*packet));
  packet->setup = 1;
  packet->workgroup_size_x = 1;
  packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = outputCount;
  packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = object;
  packet->kernarg_address = arguments;
  REQUIRE((packet->completion_signal.handle == 0));
  __atomic_store_n(&packet->header, uint16_t(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
  const auto reads_before_dispatch = fixture.reads();
  hsa_queue_store_write_index_screlease(queue, 1);
  hsa_signal_store_screlease(queue->doorbell_signal, 0);
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
  while (hsa_queue_load_read_index_scacquire(queue) != 1 && std::chrono::steady_clock::now() < deadline)
    std::this_thread::yield();
  fixture.check();
  REQUIRE((hsa_queue_load_read_index_scacquire(queue) == 1));
  for (uint32_t index = 0; index < outputCount; ++index)
    REQUIRE((static_cast<uint32_t *>(output)[index] == 37 + index));
  // Exact dirty words share bounded 64KiB reads, including the final partial batch.
  CHECK(fixture.reads() - reads_before_dispatch == (outputCount * sizeof(uint32_t) + 65535) / 65536);
  // A device-to-host copy submitted while a dispatch is still running must
  // observe that dispatch's writes. The mock server pauses completion so the
  // ordering is deterministic even on a fast machine.
  fixture.pauseNextCompletion();
  *arguments = Arguments{output, 73};
  fixture.expect({4, 3, 12, 0, arguments->seed});
  packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address) + 1;
  std::memset(packet, 0, sizeof(*packet));
  packet->setup = 1;
  packet->workgroup_size_x = 4;
  packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = 12;
  packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = object;
  packet->kernarg_address = arguments;
  __atomic_store_n(&packet->header, uint16_t(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
  hsa_queue_store_write_index_screlease(queue, 2);
  hsa_signal_store_screlease(queue->doorbell_signal, 1);
  const auto entered_deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
  while (!fixture.completionEntered() && std::chrono::steady_clock::now() < entered_deadline)
    std::this_thread::yield();
  REQUIRE((fixture.completionEntered()));
  std::array<uint32_t, 12> copied{};
  hsa_signal_t copy_signal{};
  REQUIRE((hsa_signal_create(1, 0, nullptr, &copy_signal) == HSA_STATUS_SUCCESS));
  std::atomic_bool copy_finished{false};
  hsa_status_t copy_status = HSA_STATUS_ERROR;
  std::thread copying([&] {
    copy_status = hsa_amd_memory_async_copy(copied.data(), agents.front(), output, gpu, sizeof(copied), 0, nullptr, copy_signal);
    copy_finished = true;
  });
  std::this_thread::sleep_for(std::chrono::milliseconds(20));
  const bool copied_before_dispatch = copy_finished.load();
  fixture.releasePausedCompletion();
  copying.join();
  REQUIRE((!copied_before_dispatch));
  REQUIRE((copy_status == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_load_scacquire(copy_signal) == 0));
  for (uint32_t index = 0; index < copied.size(); ++index)
    REQUIRE((copied[index] == 73 + index));
  REQUIRE((hsa_signal_destroy(copy_signal) == HSA_STATUS_SUCCESS));
  fixture.check();
  REQUIRE((setenv(gemstone::environment::SystemPaging, "1", 1) == 0));
  for (uint32_t seed = 1; seed <= 16; ++seed) {
    std::array<uint32_t, 12> pageable_output{};
    if (seed == 1) fixture.pauseNextCompletion();
    *arguments = Arguments{pageable_output.data(), seed};
    hsa_signal_t completion{};
    REQUIRE((hsa_signal_create(1, 0, nullptr, &completion) == HSA_STATUS_SUCCESS));
    fixture.expect({4, 3, 12, 0, seed});
    const auto reads_before_host_dispatch = fixture.reads();
    const uint64_t index = hsa_queue_load_write_index_relaxed(queue);
    packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address) + (index & (queue->size - 1));
    std::memset(packet, 0, sizeof(*packet));
    packet->setup = 1;
    packet->workgroup_size_x = 4;
    packet->workgroup_size_y = packet->workgroup_size_z = 1;
    packet->grid_size_x = 12;
    packet->grid_size_y = packet->grid_size_z = 1;
    packet->kernel_object = object;
    packet->kernarg_address = arguments;
    packet->completion_signal = completion;
    __atomic_store_n(&packet->header, uint16_t(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
    hsa_queue_store_write_index_screlease(queue, index + 1);
    hsa_signal_store_screlease(queue->doorbell_signal, static_cast<hsa_signal_value_t>(index));
    if (seed == 1) {
      const auto release_deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
      while (!fixture.completionEntered() && std::chrono::steady_clock::now() < release_deadline)
        std::this_thread::yield();
      REQUIRE((fixture.completionEntered()));
      REQUIRE((hsa_signal_load_scacquire(completion) == 1));
      fixture.releasePausedCompletion();
    }
    REQUIRE((hsa_signal_wait_scacquire(completion, HSA_SIGNAL_CONDITION_EQ, 0, UINT64_MAX, HSA_WAIT_STATE_BLOCKED) == 0));
    for (uint32_t output_index = 0; output_index < pageable_output.size(); ++output_index)
      REQUIRE((pageable_output[output_index] == seed + output_index));
    CHECK(fixture.reads() == reads_before_host_dispatch);
    fixture.checkAllocationContainingAddress(pageable_output.data(), false);
    REQUIRE((hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS));
    fixture.check();
  }
  REQUIRE((unsetenv(gemstone::environment::SystemPaging) == 0));
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_free(output) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(argument_memory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_code_object_destroy(legacy_object) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_code_object_destroy(legacy_object) == HSA_STATUS_ERROR_INVALID_CODE_OBJECT));
  // Generic ELF variables, including initialized data, BSS and aliases, use
  // the same allocation store as ordinary pointer arguments and host copies.
  const auto globals_image = gemstone::hsa::test::variableKernelObjectFixture();
  fixture.programCatalog({{"add", "add.kd"}}, {{"arbitrary_data", 0x1004, 8, {}},
                                               {"data_alias", 0x1004, 8, {}},
                                               {"arbitrary_bss", 0x2000, 32, {}},
                                               {"zero_global", 0x2020, 0, {}}});
  fixture.expectGlobalBinding("arbitrary_data", 0, {5, 6, 7, 8, 9, 10, 11, 12});
  fixture.expectGlobalBinding("data_alias", 0, {5, 6, 7, 8, 9, 10, 11, 12});
  fixture.expectGlobalBinding("arbitrary_bss", 0, std::vector<uint8_t>(32));
  REQUIRE((hsa_code_object_reader_create_from_memory(globals_image.data(), globals_image.size(), &reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS));
  fixture.checkGlobalBindings();
  const auto global_address = [&](const char *name, uint32_t expected_size) {
    hsa_executable_symbol_t symbol{};
    uint64_t address = 0;
    uint32_t size = UINT32_MAX;
    hsa_symbol_kind_t type{};
    REQUIRE((hsa_executable_get_symbol_by_name(executable, name, &gpu, &symbol) == HSA_STATUS_SUCCESS));
    REQUIRE((hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_TYPE, &type) == HSA_STATUS_SUCCESS));
    REQUIRE((type == HSA_SYMBOL_KIND_VARIABLE));
    REQUIRE((hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_SIZE, &size) == HSA_STATUS_SUCCESS));
    REQUIRE((size == expected_size));
    REQUIRE((hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_ADDRESS, &address) == HSA_STATUS_SUCCESS));
    return reinterpret_cast<void *>(address);
  };
  auto *data = static_cast<uint8_t *>(global_address("arbitrary_data", 8));
  REQUIRE((data == global_address("data_alias", 8)));
  for (uint8_t index = 0; index < 8; ++index)
    REQUIRE((data[index] == index + 5));
  auto *bss = static_cast<uint32_t *>(global_address("arbitrary_bss", 32));
  for (uint32_t index = 0; index < 8; ++index)
    REQUIRE((bss[index] == 0));
  REQUIRE((global_address("zero_global", 0) == nullptr));
  hsa_executable_symbol_t absent{};
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "undefined_global", &gpu, &absent) == HSA_STATUS_ERROR_INVALID_SYMBOL_NAME));
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "__omp_rtl_device_memory_pool", &gpu, &absent)
           == HSA_STATUS_ERROR_INVALID_SYMBOL_NAME));
  REQUIRE((hsa_amd_memory_pool_free(data) == HSA_STATUS_ERROR_INVALID_ALLOCATION));
  uint32_t replacement = 0x12345678, observed = 0;
  REQUIRE((hsa_memory_copy(data + 4, &replacement, 4) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(&observed, static_cast<uint8_t *>(global_address("data_alias", 8)) + 4, 4) == HSA_STATUS_SUCCESS));
  REQUIRE((observed == replacement));
  uint64_t fresh_host_value = 0;
  REQUIRE((hsa_memory_register(&fresh_host_value, sizeof(fresh_host_value)) == HSA_STATUS_SUCCESS));
  fixture.checkAllocationAddress(&fresh_host_value);
  fresh_host_value = 0x1122334455667788;
  REQUIRE((gemstone_hsa_executable_write_global(executable, "arbitrary_data", &fresh_host_value, sizeof(fresh_host_value), 0)
           == HSA_STATUS_SUCCESS));
  uint64_t copied_host_value = 0;
  REQUIRE((hsa_memory_copy(&copied_host_value, data, sizeof(copied_host_value)) == HSA_STATUS_SUCCESS));
  REQUIRE((copied_host_value == fresh_host_value));
  // A pointer stored in a program global must match the native address used by typed aliased arguments.
  void *ordinary_pointer = &fresh_host_value;
  REQUIRE((hsa_memory_copy(data, &ordinary_pointer, sizeof(ordinary_pointer)) == HSA_STATUS_SUCCESS));
  fixture.checkAllocationWord(data, 0, reinterpret_cast<uint64_t>(ordinary_pointer));
  REQUIRE((hsa_memory_deregister(&fresh_host_value, sizeof(fresh_host_value)) == HSA_STATUS_SUCCESS));
  fixture.checkAllocationAddress(&fresh_host_value, false);
  void *pointer_value = bss, *pointer_result = nullptr;
  REQUIRE((hsa_memory_copy(data, &pointer_value, sizeof(pointer_value)) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(&pointer_result, global_address("data_alias", 8), sizeof(pointer_result)) == HSA_STATUS_SUCCESS));
  REQUIRE((pointer_result == bss));
  // An ordinary scalar equal to an ELF address is not a pointer initializer.
  uint64_t scalar_address = 0x1004, scalar_result = 0;
  REQUIRE((hsa_memory_copy(bss, &scalar_address, sizeof(scalar_address)) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(&scalar_result, bss, sizeof(scalar_result)) == HSA_STATUS_SUCCESS));
  REQUIRE((scalar_result == scalar_address));
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "add", &gpu, &kernel) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_symbol_get_info(kernel, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_allocate(kernarg, sizeof(Arguments), &argument_memory) == HSA_STATUS_SUCCESS));
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
  REQUIRE((hsa_queue_load_read_index_scacquire(queue) == 1));
  for (uint32_t index = 0; index < 8; ++index)
    REQUIRE((bss[index] == 81 + index));
  fixture.check();
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(argument_memory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_free(data) == HSA_STATUS_ERROR_INVALID_ALLOCATION));
  REQUIRE((hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS));
  // A child is reachable only through pointer data, never as a direct kernarg.
  // Its host writes must be visible without re-uploading unrelated device-only buffers.
  std::vector<uint32_t> child(32768, 0);
  child[0] = 1;
  child[17000] = 37;
  REQUIRE((hsa_memory_register(child.data(), child.size() * sizeof(uint32_t)) == HSA_STATUS_SUCCESS));
  void *table_memory = nullptr, *device_only = nullptr;
  REQUIRE((hsa_amd_memory_pool_allocate(pools.front(), 32, 0, &table_memory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_allocate(pools.front(), 128 * 1024, 0, &device_only) == HSA_STATUS_SUCCESS));
  std::memset(device_only, 0x5a, 128 * 1024);
  REQUIRE((hsa_memory_allocate(kernarg, sizeof(Arguments), &argument_memory) == HSA_STATUS_SUCCESS));
  arguments = static_cast<Arguments *>(argument_memory);
  REQUIRE((hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "add", &gpu, &kernel) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_symbol_get_info(kernel, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS));
  for (uint32_t iteration = 0; iteration < 6; ++iteration) {
    if (iteration == 1) child[17000] = 37; // CPU restores the value preceding the device's write.
    if (iteration == 2) child[17000] = 73;
    if (iteration == 5) child[0] = 101;
    const uint32_t expected = iteration < 2 ? 37 : iteration < 4 ? 73 : iteration == 4 ? 91 : 111;
    void *output_memory = iteration < 4 ? table_memory : child.data();
    auto *nested = &child[17000];
    // table_memory belongs to the device pool: publish its pointer field through
    // the copy API instead of relying on dispatch-time uploads of shadow bytes.
    REQUIRE((hsa_memory_copy(table_memory, &nested, sizeof(nested)) == HSA_STATUS_SUCCESS));
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
    REQUIRE((hsa_queue_load_read_index_scacquire(queue) == iteration + 1));
    for (uint32_t index = 0; index < 8; ++index)
      REQUIRE((static_cast<uint32_t *>(output_memory)[index] == expected + index));
    REQUIRE((child[17000] == (iteration == 0 ? 55 : iteration == 1 ? 37 : 73)));
    fixture.check();
    REQUIRE((fixture.writtenBytes(child.data()) == 0));
    REQUIRE((fixture.writtenBytes(device_only) == 0));
  }
  REQUIRE((hsa_shut_down() == HSA_STATUS_ERROR_INVALID_RUNTIME_STATE));
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_free(table_memory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_free(device_only) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(argument_memory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_deregister(child.data(), child.size() * sizeof(uint32_t)) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_shut_down() == HSA_STATUS_SUCCESS));
}

namespace {
// Explicit registrations must work without the optional process-map fallback.
struct MemoryLockSession {
  gemstone_test::RpcFixture server;
  gemstone_test::RpcEnvironment environment{server};
  gemstone_test::Environment paging{gemstone::environment::SystemPaging, nullptr};
  MemoryLockSession() { REQUIRE((hsa_init() == HSA_STATUS_SUCCESS)); }
  ~MemoryLockSession() { (void)hsa_shut_down(); }
};
} // namespace

TEST_CASE("HSA failed dispatch notifies the queue callback", "[hsa][queue-error]") {
  MemoryLockSession session;
  hsa_agent_t gpu{};
  REQUIRE((hsa_iterate_agents(
               [](hsa_agent_t agent, void *data) {
                 hsa_device_type_t type{};
                 if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
                 if (type == HSA_DEVICE_TYPE_GPU) *static_cast<hsa_agent_t *>(data) = agent;
                 return HSA_STATUS_SUCCESS;
               },
               &gpu)
           == HSA_STATUS_SUCCESS));
  struct Notification {
    std::atomic<unsigned> calls{0};
    hsa_status_t status = HSA_STATUS_SUCCESS;
    hsa_queue_t *queue = nullptr;
    bool reentered = false;
    hsa_status_t destroyed = HSA_STATUS_ERROR;
  } notification;
  hsa_queue_t *queue = nullptr;
  REQUIRE((hsa_queue_create(
               gpu, 64, HSA_QUEUE_TYPE_MULTI,
               [](hsa_status_t status, hsa_queue_t *queue, void *data) {
                 auto &event = *static_cast<Notification *>(data);
                 event.status = status;
                 event.queue = queue;
                 // Callbacks must be able to query the runtime without deadlocking.
                 (void)hsa_queue_load_read_index_scacquire(queue);
                 event.reentered = true;
                 event.destroyed = hsa_queue_destroy(queue);
                 event.calls.fetch_add(1, std::memory_order_release);
               },
               &notification, UINT32_MAX, UINT32_MAX, &queue)
           == HSA_STATUS_SUCCESS));
  hsa_signal_t completion{};
  REQUIRE((hsa_signal_create(1, 0, nullptr, &completion) == HSA_STATUS_SUCCESS));
  auto *packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address);
  *packet = {};
  packet->setup = 1;
  packet->workgroup_size_x = packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = 0xbad; // Unknown kernel: fail without executing RPC work.
  packet->completion_signal = completion;
  __atomic_store_n(&packet->header, uint16_t(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
  hsa_queue_store_write_index_screlease(queue, 1);
  hsa_signal_store_screlease(queue->doorbell_signal, 0);
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
  while (notification.calls.load(std::memory_order_acquire) == 0 && std::chrono::steady_clock::now() < deadline)
    std::this_thread::yield();
  CHECK((notification.calls.load(std::memory_order_acquire) == 1));
  if (notification.calls.load(std::memory_order_acquire) != 0) {
    CHECK((notification.status != HSA_STATUS_SUCCESS));
    CHECK((notification.queue == queue));
    CHECK(notification.reentered);
    CHECK((notification.destroyed == HSA_STATUS_SUCCESS));
  }
  CHECK((hsa_signal_load_scacquire(completion) < 0));
  if (notification.calls.load(std::memory_order_acquire) == 0) REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA locked interior view addresses the requested bytes", "[hsa][unified-memory]") {
  MemoryLockSession session;
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *whole = nullptr, *interior = nullptr;
  REQUIRE((hsa_amd_memory_lock(host.data(), sizeof(host), nullptr, 0, &whole) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_lock(host.data() + 2, 3 * sizeof(uint32_t), nullptr, 0, &interior) == HSA_STATUS_SUCCESS));
  uint32_t observed = 0;
  REQUIRE((hsa_memory_copy(&observed, interior, sizeof(observed)) == HSA_STATUS_SUCCESS));
  CHECK((observed == host[2]));
  CHECK((hsa_amd_memory_unlock(host.data() + 2) == HSA_STATUS_SUCCESS));
  CHECK((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA repeated locks retain memory until the final unlock", "[hsa][unified-memory]") {
  MemoryLockSession session;
  std::array<uint32_t, 8> host{};
  void *first = nullptr, *second = nullptr;
  REQUIRE((hsa_amd_memory_lock(host.data(), sizeof(host), nullptr, 0, &first) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_lock(host.data(), sizeof(host), nullptr, 0, &second) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
  hsa_amd_pointer_info_t info{};
  info.size = sizeof(info);
  REQUIRE((hsa_amd_pointer_info(second, &info, nullptr, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  CHECK((info.type == HSA_EXT_POINTER_TYPE_LOCKED));
  CHECK((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
  host[0] = 91; // Unlock must not free application-owned storage.
  CHECK((host[0] == 91));
}

TEST_CASE("HSA partially overlapping locks preserve both ranges", "[hsa][unified-memory]") {
  MemoryLockSession session;
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *first = nullptr, *second = nullptr;
  REQUIRE((hsa_amd_memory_lock(host.data(), 4 * sizeof(uint32_t), nullptr, 0, &first) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_lock(host.data() + 2, 6 * sizeof(uint32_t), nullptr, 0, &second) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
  std::array<uint32_t, 6> observed{};
  REQUIRE((hsa_memory_copy(observed.data(), second, sizeof(observed)) == HSA_STATUS_SUCCESS));
  CHECK((std::equal(observed.begin(), observed.end(), host.begin() + 2)));
  CHECK((hsa_amd_memory_unlock(host.data() + 2) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA async copy and fill span overlapping lock backing fragments", "[hsa][unified-memory]") {
  MemoryLockSession session;
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *first = nullptr, *second = nullptr;
  REQUIRE(hsa_amd_memory_lock(host.data(), 4 * sizeof(uint32_t), nullptr, 0, &first) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_amd_memory_lock(host.data() + 2, 6 * sizeof(uint32_t), nullptr, 0, &second) == HSA_STATUS_SUCCESS);
  hsa_agent_t cpu{};
  REQUIRE(hsa_iterate_agents(
              [](hsa_agent_t agent, void *data) {
                hsa_device_type_t type{};
                if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
                if (type == HSA_DEVICE_TYPE_CPU) *static_cast<hsa_agent_t *>(data) = agent;
                return HSA_STATUS_SUCCESS;
              },
              &cpu)
          == HSA_STATUS_SUCCESS);
  hsa_signal_t signal{};
  REQUIRE(hsa_signal_create(1, 0, nullptr, &signal) == HSA_STATUS_SUCCESS);
  std::array<uint32_t, 6> observed{};
  CHECK(hsa_amd_memory_async_copy(observed.data(), cpu, second, cpu, sizeof(observed), 0, nullptr, signal) == HSA_STATUS_SUCCESS);
  CHECK(hsa_signal_load_scacquire(signal) == 0);
  CHECK(std::equal(observed.begin(), observed.end(), host.begin() + 2));
  CHECK(hsa_amd_memory_fill(second, 99, 6) == HSA_STATUS_SUCCESS);
  CHECK(std::all_of(host.begin() + 2, host.end(), [](auto value) { return value == 99; }));
  CHECK(host[0] == 11);
  CHECK(host[1] == 22);
  CHECK(hsa_signal_destroy(signal) == HSA_STATUS_SUCCESS);
  CHECK(hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS);
  CHECK(hsa_amd_memory_unlock(host.data() + 2) == HSA_STATUS_SUCCESS);
}

TEST_CASE("HSA nested lock survives release of its enclosing lock", "[hsa][unified-memory]") {
  MemoryLockSession session;
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *whole = nullptr, *interior = nullptr;
  REQUIRE((hsa_amd_memory_lock(host.data(), sizeof(host), nullptr, 0, &whole) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_lock(host.data() + 2, 3 * sizeof(uint32_t), nullptr, 0, &interior) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
  hsa_amd_pointer_info_t info{};
  info.size = sizeof(info);
  REQUIRE((hsa_amd_pointer_info(interior, &info, nullptr, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  CHECK((info.type == HSA_EXT_POINTER_TYPE_LOCKED));
  CHECK((hsa_amd_memory_unlock(host.data() + 2) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA disjoint registrations can be released and reused independently", "[hsa][unified-memory]") {
  MemoryLockSession session;
  std::array<uint32_t, 8> host{};
  for (unsigned iteration = 0; iteration < 3; ++iteration) {
    REQUIRE((hsa_memory_register(host.data(), 2 * sizeof(uint32_t)) == HSA_STATUS_SUCCESS));
    REQUIRE((hsa_memory_register(host.data() + 4, 2 * sizeof(uint32_t)) == HSA_STATUS_SUCCESS));
    REQUIRE((hsa_memory_deregister(host.data(), 2 * sizeof(uint32_t)) == HSA_STATUS_SUCCESS));
    hsa_amd_pointer_info_t info{};
    info.size = sizeof(info);
    REQUIRE((hsa_amd_pointer_info(host.data() + 5, &info, nullptr, nullptr, nullptr) == HSA_STATUS_SUCCESS));
    CHECK((info.type == HSA_EXT_POINTER_TYPE_LOCKED));
    REQUIRE((hsa_memory_deregister(host.data() + 4, 2 * sizeof(uint32_t)) == HSA_STATUS_SUCCESS));
  }
}

TEST_CASE("HSA pool lock preserves an interior view", "[hsa][unified-memory]") {
  MemoryLockSession session;
  hsa_amd_memory_pool_t pool{};
  REQUIRE((hsa_iterate_agents(
               [](hsa_agent_t agent, void *data) {
                 hsa_device_type_t type{};
                 const auto status = hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type);
                 if (status != HSA_STATUS_SUCCESS || type != HSA_DEVICE_TYPE_CPU) return status;
                 return hsa_amd_agent_iterate_memory_pools(
                     agent,
                     [](hsa_amd_memory_pool_t candidate, void *output) {
                       hsa_amd_segment_t segment{};
                       const auto status = hsa_amd_memory_pool_get_info(candidate, HSA_AMD_MEMORY_POOL_INFO_SEGMENT, &segment);
                       if (status == HSA_STATUS_SUCCESS && segment == HSA_AMD_SEGMENT_GLOBAL)
                         *static_cast<hsa_amd_memory_pool_t *>(output) = candidate;
                       return status;
                     },
                     data);
               },
               &pool)
           == HSA_STATUS_SUCCESS));
  REQUIRE((pool.handle != 0));
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *whole = nullptr, *interior = nullptr;
  REQUIRE((hsa_amd_memory_lock_to_pool(host.data(), sizeof(host), nullptr, 0, pool, 0, &whole) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_lock_to_pool(host.data() + 2, 3 * sizeof(uint32_t), nullptr, 0, pool, 0, &interior) == HSA_STATUS_SUCCESS));
  uint32_t observed = 0;
  REQUIRE((hsa_memory_copy(&observed, interior, sizeof(observed)) == HSA_STATUS_SUCCESS));
  CHECK((observed == host[2]));
  CHECK((hsa_amd_memory_unlock(host.data() + 2) == HSA_STATUS_SUCCESS));
  CHECK((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
}
