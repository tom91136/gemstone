#include <algorithm>
#include <array>
#include <atomic>
#include <chrono>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <optional>
#include <string>
#include <thread>
#include <utility>
#include <vector>

#include <hsa.h>
#include <hsa_ext_amd.h>
#include <hsa_ext_image.h>

#include <catch2/catch_test_macros.hpp>

#include "gemstone/hsa/global.hpp"
#include "gemstone/hsa/memory_copy.hpp"
#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "code_object_fixture.hpp"
#include "rpc/gemstone_execution_rpc.hpp"
#include <environment_keys.hpp>

namespace {

hsa_agent_t agentOf(hsa_device_type_t wanted) {
  std::pair<hsa_device_type_t, hsa_agent_t> search{wanted, {}};
  REQUIRE((hsa_iterate_agents(
               [](hsa_agent_t agent, void *data) {
                 auto &[wanted, found] = *static_cast<std::pair<hsa_device_type_t, hsa_agent_t> *>(data);
                 hsa_device_type_t type{};
                 if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
                 if (type == wanted) found = agent;
                 return HSA_STATUS_SUCCESS;
               },
               &search)
           == HSA_STATUS_SUCCESS));
  return search.second;
}

hsa_agent_t gpuAgent() { return agentOf(HSA_DEVICE_TYPE_GPU); }

hsa_region_t regionWithFlag(hsa_agent_t agent, hsa_region_global_flag_t flag) {
  std::pair<hsa_region_global_flag_t, hsa_region_t> search{flag, {}};
  REQUIRE((hsa_agent_iterate_regions(
               agent,
               [](hsa_region_t region, void *data) {
                 auto &[flag, found] = *static_cast<std::pair<hsa_region_global_flag_t, hsa_region_t> *>(data);
                 uint32_t flags = 0;
                 if (hsa_region_get_info(region, HSA_REGION_INFO_GLOBAL_FLAGS, &flags) == HSA_STATUS_SUCCESS && (flags & flag))
                   found = region;
                 return HSA_STATUS_SUCCESS;
               },
               &search)
           == HSA_STATUS_SUCCESS));
  return search.second;
}

struct QueueErrors {
  std::atomic<unsigned> calls{0};
  std::atomic<hsa_status_t> status{HSA_STATUS_SUCCESS};
  std::atomic<hsa_queue_t *> queue{nullptr};

  static void record(hsa_status_t status, hsa_queue_t *queue, void *data) {
    auto &errors = *static_cast<QueueErrors *>(data);
    errors.status = status;
    errors.queue = queue;
    errors.calls.fetch_add(1, std::memory_order_release);
  }
  [[nodiscard]] bool await(unsigned count = 1) const {
    return gemstone_test::waitUntil([&] { return calls.load(std::memory_order_acquire) >= count; }, std::chrono::seconds(5));
  }
};

hsa_queue_t *createQueue(hsa_agent_t gpu, QueueErrors *errors) {
  hsa_queue_t *queue = nullptr;
  REQUIRE((hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, errors ? QueueErrors::record : nullptr, errors, UINT32_MAX, UINT32_MAX, &queue)
           == HSA_STATUS_SUCCESS));
  return queue;
}

hsa_signal_t createSignal(hsa_signal_value_t value = 1) {
  hsa_signal_t signal{};
  REQUIRE((hsa_signal_create(value, 0, nullptr, &signal) == HSA_STATUS_SUCCESS));
  return signal;
}

hsa_kernel_dispatch_packet_t *dispatchPacket(hsa_queue_t *queue, uint64_t index, uint64_t kernel, hsa_signal_t completion) {
  auto *packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address) + index;
  *packet = {};
  packet->setup = 1;
  packet->workgroup_size_x = packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = kernel;
  packet->completion_signal = completion;
  return packet;
}

void publish(hsa_queue_t *queue, hsa_kernel_dispatch_packet_t *packet, uint64_t count) {
  __atomic_store_n(&packet->header, static_cast<uint16_t>(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
  hsa_queue_store_write_index_screlease(queue, count);
  hsa_signal_store_screlease(queue->doorbell_signal, static_cast<hsa_signal_value_t>(count - 1));
}

void placeBarrier(hsa_queue_t *queue, uint64_t index, hsa_signal_t completion) {
  auto *barrier = static_cast<hsa_barrier_and_packet_t *>(queue->base_address) + index;
  *barrier = {};
  barrier->completion_signal = completion;
  __atomic_store_n(&barrier->header, static_cast<uint16_t>(HSA_PACKET_TYPE_BARRIER_AND), __ATOMIC_RELEASE);
}

void publishBarrier(hsa_queue_t *queue, uint64_t index, hsa_signal_t completion) {
  placeBarrier(queue, index, completion);
  hsa_queue_store_write_index_screlease(queue, index + 1);
  hsa_signal_store_screlease(queue->doorbell_signal, static_cast<hsa_signal_value_t>(index));
}

[[nodiscard]] bool awaitReadIndex(hsa_queue_t *queue, uint64_t index) {
  return gemstone_test::waitUntil([&] { return hsa_queue_load_read_index_scacquire(queue) == index; }, std::chrono::seconds(5));
}

bool settles(hsa_signal_t signal, hsa_signal_value_t value) {
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::milliseconds(200);
  while (std::chrono::steady_clock::now() < deadline) {
    if (hsa_signal_load_scacquire(signal) != value) return false;
    std::this_thread::sleep_for(std::chrono::milliseconds(5));
  }
  return true;
}

} // namespace

TEST_CASE("HSA agent wave size follows the processor while legacy calling convention remains wave64", "[hsa]") {
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((hsa_init() == HSA_STATUS_SUCCESS));
  hsa_agent_t gpu{};
  REQUIRE((hsa_iterate_agents(
               [](hsa_agent_t agent, void *data) {
                 hsa_device_type_t type{};
                 if (hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) != HSA_STATUS_SUCCESS) return HSA_STATUS_ERROR;
                 if (type == HSA_DEVICE_TYPE_GPU) {
                   *static_cast<hsa_agent_t *>(data) = agent;
                 } else {
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
  gemstone_test::RpcFixture const fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  const gemstone_test::Environment paging(gemstone::environment::SystemPagingOn, "1");
  REQUIRE((hsa_init() == HSA_STATUS_SUCCESS));
  const auto gpu = gpuAgent();
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
  REQUIRE((name.contains("xnack+")));
  std::array<uint32_t, 2> hostValues{41, 43};
  void *agentPointer = nullptr;
  REQUIRE((hsa_amd_memory_lock(hostValues.data(), sizeof(hostValues), nullptr, 0, &agentPointer) == HSA_STATUS_SUCCESS));
  REQUIRE((agentPointer == hostValues.data()));
  void *secondAgentPointer = nullptr;
  REQUIRE((hsa_amd_memory_lock(hostValues.data(), sizeof(hostValues), nullptr, 0, &secondAgentPointer) == HSA_STATUS_SUCCESS));
  REQUIRE((secondAgentPointer == hostValues.data()));
  hsa_amd_pointer_info_t info{};
  info.size = sizeof(info);
  REQUIRE((hsa_amd_pointer_info(hostValues.data(), &info, nullptr, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((info.type == HSA_EXT_POINTER_TYPE_LOCKED));
  REQUIRE((hsa_amd_memory_unlock(hostValues.data()) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_unlock(hostValues.data()) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_shut_down() == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA runtime facade", "[hsa]") {
  // The public provider must fail closed when no identified Gemstone
  // execution endpoint is configured; it must not fall back to a physical
  // ROCr installation in the process.
  const gemstone_test::Environment noEndpoint(gemstone::environment::RpcEndpoint, nullptr);
  const gemstone_test::Environment noClient(gemstone::environment::RpcClientId, nullptr);
  REQUIRE((hsa_init() == HSA_STATUS_ERROR_NOT_INITIALIZED));

  const char *message = nullptr;
  REQUIRE((hsa_status_string(HSA_STATUS_ERROR_NOT_INITIALIZED, &message) == HSA_STATUS_SUCCESS));
  REQUIRE(
      (message
       && std::strcmp(message, "HSA_STATUS_ERROR_NOT_INITIALIZED: An API other than hsa_init has been invoked while the reference count "
                               "of the HSA runtime is zero.")
              == 0));
  uint16_t versionMajor = 0;
  REQUIRE((hsa_system_get_info(HSA_SYSTEM_INFO_VERSION_MAJOR, &versionMajor) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  uint32_t validation = 0;
  REQUIRE((hsa_executable_validate(hsa_executable_t{1}, &validation) == HSA_STATUS_ERROR_NOT_INITIALIZED));
  volatile hsa_signal_value_t *signalValue = nullptr;
  REQUIRE((hsa_amd_signal_value_pointer(hsa_signal_t{1}, &signalValue) == HSA_STATUS_ERROR_NOT_INITIALIZED));
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
  REQUIRE((hsa_system_get_info(HSA_SYSTEM_INFO_VERSION_MAJOR, &versionMajor) == HSA_STATUS_SUCCESS));
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
  hsa_queue_t *singleQueue = nullptr;
  REQUIRE((hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_SINGLE, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &singleQueue) == HSA_STATUS_SUCCESS));
  REQUIRE((singleQueue->type == HSA_QUEUE_TYPE_SINGLE));
  REQUIRE((hsa_queue_destroy(singleQueue) == HSA_STATUS_SUCCESS));
  std::vector<hsa_region_t> regions;
  REQUIRE((hsa_agent_iterate_regions(
               gpu,
               [](hsa_region_t region, void *data) {
                 static_cast<std::vector<hsa_region_t> *>(data)->push_back(region);
                 return HSA_STATUS_SUCCESS;
               },
               &regions)
           == HSA_STATUS_SUCCESS));
  hsa_region_t kernarg{};
  hsa_region_t coarse{};
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
    std::pair<size_t *, hsa_status_t> callbackState{&callbacks, status};
    REQUIRE((hsa_agent_iterate_regions(
                 gpu,
                 [](hsa_region_t, void *data) {
                   auto &state = *static_cast<std::pair<size_t *, hsa_status_t> *>(data);
                   ++*state.first;
                   return state.second;
                 },
                 &callbackState)
             == status));
    REQUIRE((callbacks == 1));
  }
  std::array<uint32_t, 3> payload{19, 23, 29};
  std::array<uint32_t, 3> regionCopy{};
  void *kernargMemory = nullptr;
  void *deviceMemory = nullptr;
  REQUIRE((hsa_memory_allocate(kernarg, sizeof(payload), &kernargMemory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_allocate(coarse, sizeof(payload), &deviceMemory) == HSA_STATUS_SUCCESS));
  std::memcpy(kernargMemory, payload.data(), sizeof(payload));
  REQUIRE((hsa_memory_copy(deviceMemory, kernargMemory, sizeof(payload)) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(regionCopy.data(), deviceMemory, sizeof(regionCopy)) == HSA_STATUS_SUCCESS));
  REQUIRE((regionCopy == payload));
  REQUIRE((hsa_memory_free(deviceMemory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(kernargMemory) == HSA_STATUS_SUCCESS));
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
  } cooperative{.value = false, .guard = {0xa5, 0xa5, 0xa5}};
  REQUIRE((hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COOPERATIVE_QUEUES), &cooperative.value)
           == HSA_STATUS_SUCCESS));
  REQUIRE((cooperative.value));
  for (auto byte : cooperative.guard)
    REQUIRE((byte == 0xa5));
  // RDNA applications size WGP dispatches from half the advertised CU count.
  for (const auto *target : {"gfx906", "gfx1030", "gfx1100"}) {
    const gemstone_test::Environment selected(gemstone::environment::TargetArch, target);
    uint32_t computeUnits = 0;
    uint32_t cooperativeUnits = 0;
    REQUIRE((hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COMPUTE_UNIT_COUNT), &computeUnits)
             == HSA_STATUS_SUCCESS));
    REQUIRE((hsa_agent_get_info(gpu, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_COOPERATIVE_COMPUTE_UNIT_COUNT), &cooperativeUnits)
             == HSA_STATUS_SUCCESS));
    REQUIRE((std::cmp_equal(computeUnits, (std::strcmp(target, "gfx906") == 0 ? 1 : 2))));
    REQUIRE((cooperativeUnits == computeUnits));
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
    } word{.value = UINT32_MAX, .guard = 0xa5a5a5a5};
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
      bool accessibleByAll = true;
      REQUIRE((hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_ACCESSIBLE_BY_ALL, &accessibleByAll) == HSA_STATUS_SUCCESS));
      REQUIRE((!accessibleByAll));
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
  const size_t threadsBefore = gemstone_test::processThreadCount();
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
  REQUIRE((gemstone_test::processThreadCount() <= threadsBefore + 5));
  hsa_signal_silent_store_relaxed(signal, 7);
  REQUIRE(gemstone_test::waitUntil([&] { return callbacks.load() == 64; }, std::chrono::seconds(2)));
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
  hsa_code_object_t legacyObject{};
  hsa_executable_t executable{};
  auto serialised = image;
  REQUIRE((hsa_code_object_deserialize(nullptr, serialised.size(), nullptr, &legacyObject) == HSA_STATUS_ERROR_INVALID_ARGUMENT));
  REQUIRE((hsa_code_object_deserialize(serialised.data(), serialised.size(), nullptr, &legacyObject) == HSA_STATUS_SUCCESS));
  std::fill(serialised.begin(), serialised.end(), 0);
  REQUIRE((hsa_code_object_reader_destroy(hsa_code_object_reader_t{legacyObject.handle}) == HSA_STATUS_ERROR_INVALID_CODE_OBJECT_READER));
  REQUIRE((hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_code_object(executable, gpu, hsa_code_object_t{}, nullptr) == HSA_STATUS_ERROR_INVALID_CODE_OBJECT));
  REQUIRE((hsa_executable_load_code_object(executable, gpu, legacyObject, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE(fixture.lastProgramImage() == image);
  REQUIRE((hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_code_object(executable, gpu, legacyObject, nullptr) == HSA_STATUS_ERROR_FROZEN_EXECUTABLE));
  hsa_executable_symbol_t kernel{};
  uint64_t object = 0;
  REQUIRE((hsa_executable_get_symbol(executable, nullptr, "add.kd", gpu, 0, &kernel) == HSA_STATUS_SUCCESS));
  hsa_executable_symbol_t modernKernel{};
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "add", &gpu, &modernKernel) == HSA_STATUS_ERROR_INVALID_SYMBOL_NAME));
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "add.kd", &gpu, &modernKernel) == HSA_STATUS_SUCCESS));
  REQUIRE(kernel.handle == modernKernel.handle);
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
  void *argumentMemory = nullptr;
  REQUIRE((hsa_memory_allocate(kernarg, sizeof(Arguments), &argumentMemory) == HSA_STATUS_SUCCESS));
  auto *arguments = static_cast<Arguments *>(argumentMemory);
  *arguments = Arguments{.output = output, .seed = 37};
  fixture.expect({.local = 1, .groups = outputCount, .global = outputCount, .offset = 0, .seed = arguments->seed});
  auto *packet = dispatchPacket(queue, 0, object, hsa_signal_t{});
  packet->grid_size_x = outputCount;
  packet->kernarg_address = arguments;
  REQUIRE((packet->completion_signal.handle == 0));
  const auto readsBeforeDispatch = fixture.reads.load();
  publish(queue, packet, 1);
  const bool consumed = awaitReadIndex(queue, 1);
  fixture.check();
  REQUIRE(consumed);
  for (uint32_t index = 0; index < outputCount; ++index)
    REQUIRE((static_cast<uint32_t *>(output)[index] == 37 + index));
  // Exact dirty words share bounded 64KiB reads, including the final partial batch.
  CHECK(fixture.reads.load() - readsBeforeDispatch == ((outputCount * sizeof(uint32_t)) + 65535) / 65536);
  // A device-to-host copy submitted while a dispatch is still running must
  // observe that dispatch's writes. The mock server pauses completion so the
  // ordering is deterministic even on a fast machine.
  fixture.pauseNextCompletion();
  *arguments = Arguments{.output = output, .seed = 73};
  fixture.expect({.local = 4, .groups = 3, .global = 12, .offset = 0, .seed = arguments->seed});
  packet = dispatchPacket(queue, 1, object, hsa_signal_t{});
  packet->workgroup_size_x = 4;
  packet->grid_size_x = 12;
  packet->kernarg_address = arguments;
  publish(queue, packet, 2);
  REQUIRE(gemstone_test::waitUntil([&] { return fixture.completionEntered.load(); }, std::chrono::seconds(5)));
  std::array<uint32_t, 12> copied{};
  hsa_signal_t copySignal{};
  REQUIRE((hsa_signal_create(1, 0, nullptr, &copySignal) == HSA_STATUS_SUCCESS));
  std::atomic_bool copyFinished{false};
  hsa_status_t copyStatus = HSA_STATUS_ERROR;
  std::thread copying([&] {
    copyStatus = hsa_amd_memory_async_copy(copied.data(), agents.front(), output, gpu, sizeof(copied), 0, nullptr, copySignal);
    copyFinished = true;
  });
  std::this_thread::sleep_for(std::chrono::milliseconds(20));
  const bool copiedBeforeDispatch = copyFinished.load();
  fixture.releasePausedCompletion();
  copying.join();
  REQUIRE((!copiedBeforeDispatch));
  REQUIRE((copyStatus == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_load_scacquire(copySignal) == 0));
  for (uint32_t index = 0; index < copied.size(); ++index)
    REQUIRE((copied[index] == 73 + index));
  REQUIRE((hsa_signal_destroy(copySignal) == HSA_STATUS_SUCCESS));
  fixture.check();
  {
    const gemstone_test::Environment paging(gemstone::environment::SystemPagingOn, "1");
    for (uint32_t seed = 1; seed <= 16; ++seed) {
      std::array<uint32_t, 12> pageableOutput{};
      if (seed == 1) fixture.pauseNextCompletion();
      *arguments = Arguments{.output = pageableOutput.data(), .seed = seed};
      hsa_signal_t completion{};
      REQUIRE((hsa_signal_create(1, 0, nullptr, &completion) == HSA_STATUS_SUCCESS));
      fixture.expect({.local = 4, .groups = 3, .global = 12, .offset = 0, .seed = seed});
      const auto readsBeforeHostDispatch = fixture.reads.load();
      const uint64_t index = hsa_queue_load_write_index_relaxed(queue);
      packet = dispatchPacket(queue, index & (queue->size - 1), object, completion);
      packet->workgroup_size_x = 4;
      packet->grid_size_x = 12;
      packet->kernarg_address = arguments;
      publish(queue, packet, index + 1);
      if (seed == 1) {
        REQUIRE(gemstone_test::waitUntil([&] { return fixture.completionEntered.load(); }, std::chrono::seconds(5)));
        REQUIRE((hsa_signal_load_scacquire(completion) == 1));
        fixture.releasePausedCompletion();
      }
      REQUIRE((hsa_signal_wait_scacquire(completion, HSA_SIGNAL_CONDITION_EQ, 0, UINT64_MAX, HSA_WAIT_STATE_BLOCKED) == 0));
      for (uint32_t outputIndex = 0; outputIndex < pageableOutput.size(); ++outputIndex)
        REQUIRE((pageableOutput[outputIndex] == seed + outputIndex));
      CHECK(fixture.reads.load() == readsBeforeHostDispatch);
      fixture.checkAllocationContainingAddress(pageableOutput.data(), false);
      REQUIRE((hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS));
      fixture.check();
    }
  }
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_free(output) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(argumentMemory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_code_object_destroy(legacyObject) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_code_object_destroy(legacyObject) == HSA_STATUS_ERROR_INVALID_CODE_OBJECT));
  // Generic ELF variables, including initialised data, BSS and aliases, use
  // the same allocation store as ordinary pointer arguments and host copies.
  const auto globalsImage = gemstone::hsa::test::variableKernelObjectFixture();
  fixture.programCatalogue({{.name = "add", .linkageName = "add.kd"}},
                           {{.name = "arbitrary_data", .virtualAddress = 0x1004, .size = 8, .pointerOffsets = {}},
                            {.name = "data_alias", .virtualAddress = 0x1004, .size = 8, .pointerOffsets = {}},
                            {.name = "arbitrary_bss", .virtualAddress = 0x2000, .size = 32, .pointerOffsets = {}},
                            {.name = "zero_global", .virtualAddress = 0x2020, .size = 0, .pointerOffsets = {}}});
  fixture.expectGlobalBinding("arbitrary_data", 0, {5, 6, 7, 8, 9, 10, 11, 12});
  fixture.expectGlobalBinding("data_alias", 0, {5, 6, 7, 8, 9, 10, 11, 12});
  fixture.expectGlobalBinding("arbitrary_bss", 0, std::vector<uint8_t>(32));
  REQUIRE((hsa_code_object_reader_create_from_memory(globalsImage.data(), globalsImage.size(), &reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS));
  fixture.checkGlobalBindings();
  const auto globalAddress = [&](const char *name, uint32_t expectedSize) {
    hsa_executable_symbol_t symbol{};
    uint64_t address = 0;
    uint32_t size = UINT32_MAX;
    hsa_symbol_kind_t type{};
    REQUIRE((hsa_executable_get_symbol_by_name(executable, name, &gpu, &symbol) == HSA_STATUS_SUCCESS));
    REQUIRE((hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_TYPE, &type) == HSA_STATUS_SUCCESS));
    REQUIRE((type == HSA_SYMBOL_KIND_VARIABLE));
    REQUIRE((hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_SIZE, &size) == HSA_STATUS_SUCCESS));
    REQUIRE((size == expectedSize));
    REQUIRE((hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_ADDRESS, &address) == HSA_STATUS_SUCCESS));
    return reinterpret_cast<void *>(address);
  };
  auto *data = static_cast<uint8_t *>(globalAddress("arbitrary_data", 8));
  REQUIRE((data == globalAddress("data_alias", 8)));
  for (uint8_t index = 0; index < 8; ++index)
    REQUIRE((data[index] == index + 5));
  auto *bss = static_cast<uint32_t *>(globalAddress("arbitrary_bss", 32));
  for (uint32_t index = 0; index < 8; ++index)
    REQUIRE((bss[index] == 0));
  REQUIRE((globalAddress("zero_global", 0) == nullptr));
  hsa_executable_symbol_t absent{};
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "undefined_global", &gpu, &absent) == HSA_STATUS_ERROR_INVALID_SYMBOL_NAME));
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "__omp_rtl_device_memory_pool", &gpu, &absent)
           == HSA_STATUS_ERROR_INVALID_SYMBOL_NAME));
  REQUIRE((hsa_amd_memory_pool_free(data) == HSA_STATUS_ERROR_INVALID_ALLOCATION));
  uint32_t replacement = 0x12345678;
  uint32_t observed = 0;
  REQUIRE((hsa_memory_copy(data + 4, &replacement, 4) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(&observed, static_cast<uint8_t *>(globalAddress("data_alias", 8)) + 4, 4) == HSA_STATUS_SUCCESS));
  REQUIRE((observed == replacement));
  uint64_t freshHostValue = 0;
  REQUIRE((hsa_memory_register(&freshHostValue, sizeof(freshHostValue)) == HSA_STATUS_SUCCESS));
  fixture.checkAllocationAddress(&freshHostValue);
  freshHostValue = 0x1122334455667788;
  REQUIRE((gemstone_hsa_executable_write_global(executable, "arbitrary_data", &freshHostValue, sizeof(freshHostValue), 0)
           == HSA_STATUS_SUCCESS));
  uint64_t copiedHostValue = 0;
  REQUIRE((hsa_memory_copy(&copiedHostValue, data, sizeof(copiedHostValue)) == HSA_STATUS_SUCCESS));
  REQUIRE((copiedHostValue == freshHostValue));
  // A pointer stored in a program global must match the native address used by typed aliased arguments.
  void *ordinaryPointer = &freshHostValue;
  REQUIRE((hsa_memory_copy(data, &ordinaryPointer, sizeof(ordinaryPointer)) == HSA_STATUS_SUCCESS));
  fixture.checkAllocationWord(data, 0, reinterpret_cast<uint64_t>(ordinaryPointer));
  REQUIRE((hsa_memory_deregister(&freshHostValue, sizeof(freshHostValue)) == HSA_STATUS_SUCCESS));
  fixture.checkAllocationAddress(&freshHostValue, false);
  void *pointerValue = bss;
  void *pointerResult = nullptr;
  REQUIRE((hsa_memory_copy(data, &pointerValue, sizeof(pointerValue)) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(&pointerResult, globalAddress("data_alias", 8), sizeof(pointerResult)) == HSA_STATUS_SUCCESS));
  REQUIRE((pointerResult == bss));
  // An ordinary scalar equal to an ELF address is not a pointer initialiser.
  uint64_t scalarAddress = 0x1004;
  uint64_t scalarResult = 0;
  REQUIRE((hsa_memory_copy(bss, &scalarAddress, sizeof(scalarAddress)) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(&scalarResult, bss, sizeof(scalarResult)) == HSA_STATUS_SUCCESS));
  REQUIRE((scalarResult == scalarAddress));
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "add.kd", &gpu, &kernel) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_symbol_get_info(kernel, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_allocate(kernarg, sizeof(Arguments), &argumentMemory) == HSA_STATUS_SUCCESS));
  arguments = static_cast<Arguments *>(argumentMemory);
  *arguments = Arguments{.output = bss, .seed = 81};
  fixture.expect({.local = 4, .groups = 2, .global = 8, .offset = 0, .seed = 81});
  packet = dispatchPacket(queue, 0, object, hsa_signal_t{});
  packet->workgroup_size_x = 4;
  packet->grid_size_x = 8;
  packet->kernarg_address = arguments;
  publish(queue, packet, 1);
  REQUIRE(awaitReadIndex(queue, 1));
  for (uint32_t index = 0; index < 8; ++index)
    REQUIRE((bss[index] == 81 + index));
  fixture.check();
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(argumentMemory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_free(data) == HSA_STATUS_ERROR_INVALID_ALLOCATION));
  REQUIRE((hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS));
  // A child is reachable only through pointer data, never as a direct kernarg.
  // Its host writes must be visible without re-uploading unrelated device-only buffers.
  std::vector<uint32_t> child(32768, 0);
  child[0] = 1;
  child[17000] = 37;
  REQUIRE((hsa_memory_register(child.data(), child.size() * sizeof(uint32_t)) == HSA_STATUS_SUCCESS));
  void *tableMemory = nullptr;
  void *deviceOnly = nullptr;
  REQUIRE((hsa_amd_memory_pool_allocate(pools.front(), 32, 0, &tableMemory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_allocate(pools.front(), size_t{128} * 1024, 0, &deviceOnly) == HSA_STATUS_SUCCESS));
  std::memset(deviceOnly, 0x5a, size_t{128} * 1024);
  REQUIRE((hsa_memory_allocate(kernarg, sizeof(Arguments), &argumentMemory) == HSA_STATUS_SUCCESS));
  arguments = static_cast<Arguments *>(argumentMemory);
  REQUIRE((hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "add.kd", &gpu, &kernel) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_symbol_get_info(kernel, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS));
  constexpr std::array<uint32_t, 6> expectations{37, 37, 73, 73, 91, 111};
  for (uint32_t iteration = 0; iteration < 6; ++iteration) {
    if (iteration == 1) child[17000] = 37; // CPU restores the value preceding the device's write.
    if (iteration == 2) child[17000] = 73;
    if (iteration == 5) child[0] = 101;
    const uint32_t expected = expectations[iteration];
    void *outputMemory = iteration < 4 ? tableMemory : child.data();
    auto *nested = &child[17000];
    // table_memory belongs to the device pool: publish its pointer field through
    // the copy API instead of relying on dispatch-time uploads of shadow bytes.
    REQUIRE((hsa_memory_copy(tableMemory, &nested, sizeof(nested)) == HSA_STATUS_SUCCESS));
    *arguments = Arguments{.output = outputMemory, .seed = expected};
    fixture.expect({.local = 4, .groups = 2, .global = 8, .offset = 0, .seed = expected});
    if (iteration < 4) fixture.expectIndirectInput(tableMemory, expected, iteration == 0 ? std::optional<uint32_t>(55) : std::nullopt);
    packet = dispatchPacket(queue, iteration, object, hsa_signal_t{});
    packet->workgroup_size_x = 4;
    packet->grid_size_x = 8;
    packet->kernarg_address = arguments;
    publish(queue, packet, iteration + 1);
    REQUIRE(awaitReadIndex(queue, iteration + 1));
    for (uint32_t index = 0; index < 8; ++index)
      REQUIRE((static_cast<uint32_t *>(outputMemory)[index] == expected + index));
    REQUIRE((std::cmp_equal(child[17000], (iteration == 0 ? 55 : iteration == 1 ? 37 : 73))));
    fixture.check();
    REQUIRE((fixture.writtenBytes(child.data()) == 0));
    REQUIRE((fixture.writtenBytes(deviceOnly) == 0));
  }
  REQUIRE((hsa_shut_down() == HSA_STATUS_ERROR_INVALID_RUNTIME_STATE));
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_free(tableMemory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_pool_free(deviceOnly) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(argumentMemory) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_deregister(child.data(), child.size() * sizeof(uint32_t)) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_shut_down() == HSA_STATUS_SUCCESS));
}

namespace {
// Explicit registrations must work without the optional process-map fallback.
struct MemoryLockSession {
  gemstone_test::RpcFixture server;
  gemstone_test::RpcEnvironment environment{server};
  gemstone_test::Environment paging{gemstone::environment::SystemPagingOn, nullptr};
  MemoryLockSession() { REQUIRE((hsa_init() == HSA_STATUS_SUCCESS)); }
  ~MemoryLockSession() { (void)hsa_shut_down(); }
};
} // namespace

TEST_CASE("HSA failed dispatch notifies the queue callback", "[hsa][queue-error]") {
  MemoryLockSession const session;
  const auto gpu = gpuAgent();
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
  const auto completion = createSignal();
  publish(queue, dispatchPacket(queue, 0, 0xbad, completion), 1); // unknown kernel: fails without executing RPC work
  CHECK(gemstone_test::waitUntil([&] { return notification.calls.load(std::memory_order_acquire) == 1; }, std::chrono::seconds(5)));
  if (notification.calls.load(std::memory_order_acquire) != 0) {
    CHECK((notification.status == HSA_STATUS_ERROR_INVALID_CODE_OBJECT));
    CHECK((notification.queue == queue));
    CHECK(notification.reentered);
    CHECK((notification.destroyed == HSA_STATUS_SUCCESS));
  }
  CHECK((hsa_signal_load_scacquire(completion) == 1));
  if (notification.calls.load(std::memory_order_acquire) == 0) REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA queue error leaves the completion signal and stops the queue", "[hsa][queue-error]") {
  MemoryLockSession const session;
  const auto gpu = gpuAgent();

  QueueErrors healthyErrors;
  auto *healthy = createQueue(gpu, &healthyErrors);
  const auto healthyCompletion = createSignal();
  publishBarrier(healthy, 0, healthyCompletion);
  hsa_signal_wait_scacquire(healthyCompletion, HSA_SIGNAL_CONDITION_EQ, 0, UINT64_MAX, HSA_WAIT_STATE_BLOCKED);
  CHECK((healthyErrors.calls.load() == 0));
  REQUIRE((hsa_queue_destroy(healthy) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(healthyCompletion) == HSA_STATUS_SUCCESS));

  QueueErrors errors;
  auto *queue = createQueue(gpu, &errors);
  const auto failing = createSignal();
  const auto following = createSignal();
  const auto later = createSignal();
  auto *bad = dispatchPacket(queue, 0, 0xbad, failing);
  placeBarrier(queue, 1, following);
  publish(queue, bad, 2);
  REQUIRE(errors.await());
  CHECK((errors.status.load() == HSA_STATUS_ERROR_INVALID_CODE_OBJECT));
  CHECK((errors.queue.load() == queue));
  CHECK(settles(failing, 1));
  CHECK(settles(following, 1));

  publishBarrier(queue, 2, later);
  CHECK(settles(later, 1));
  CHECK((errors.calls.load() == 1));
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(failing) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(following) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(later) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA malformed packets report the matching queue status", "[hsa][queue-error]") {
  MemoryLockSession const session;
  const auto gpu = gpuAgent();
  struct Case {
    const char *name;
    hsa_status_t expected;
    void (*corrupt)(hsa_kernel_dispatch_packet_t &);
    bool completion;
  };
  const Case cases[] = {
      {.name = "dimensions",
       .expected = HSA_STATUS_ERROR_INCOMPATIBLE_ARGUMENTS,
       .corrupt = [](auto &packet) { packet.workgroup_size_y = 4; },
       .completion = true},
      {.name = "grid smaller than workgroup",
       .expected = HSA_STATUS_ERROR_INVALID_ARGUMENT,
       .corrupt = [](auto &packet) { packet.workgroup_size_x = 4; },
       .completion = true},
      {.name = "reserved setup bits",
       .expected = HSA_STATUS_ERROR_INVALID_PACKET_FORMAT,
       .corrupt = [](auto &packet) { packet.reserved0 = 1; },
       .completion = true},
      {.name = "unknown kernel without completion",
       .expected = HSA_STATUS_ERROR_INVALID_CODE_OBJECT,
       .corrupt = [](auto &) {},
       .completion = false},
  };
  for (const auto &entry : cases) {
    INFO(entry.name);
    QueueErrors errors;
    auto *queue = createQueue(gpu, &errors);
    const auto completion = createSignal();
    auto *packet = dispatchPacket(queue, 0, 0xbad, entry.completion ? completion : hsa_signal_t{});
    entry.corrupt(*packet);
    publish(queue, packet, 1);
    REQUIRE(errors.await());
    CHECK((errors.status.load() == entry.expected));
    CHECK(settles(completion, 1));
    REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
    REQUIRE((hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS));
  }
}

TEST_CASE("HSA rejected launch reports an exception and leaves the completion signal", "[hsa][queue-error]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((hsa_init() == HSA_STATUS_SUCCESS));
  const auto gpu = gpuAgent();
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true);
  hsa_code_object_reader_t reader{};
  hsa_executable_t executable{};
  hsa_executable_symbol_t symbol{};
  uint64_t object = 0;
  REQUIRE((hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_get_symbol_by_name(executable, "add.kd", &gpu, &symbol) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &object) == HSA_STATUS_SUCCESS));

  const auto kernarg = regionWithFlag(gpu, HSA_REGION_GLOBAL_FLAG_KERNARG);
  void *arguments = nullptr;
  REQUIRE((hsa_memory_allocate(kernarg, 16, &arguments) == HSA_STATUS_SUCCESS));
  std::memset(arguments, 0, 16);

  QueueErrors errors;
  auto *queue = createQueue(gpu, &errors);
  const auto completion = createSignal();
  const auto following = createSignal();
  fixture.rejectNext(gemstone::execution::operation::Launch);
  auto *packet = dispatchPacket(queue, 0, object, completion);
  packet->kernarg_address = arguments;
  placeBarrier(queue, 1, following);
  publish(queue, packet, 2);
  REQUIRE(errors.await());
  CHECK((errors.status.load() == HSA_STATUS_ERROR_EXCEPTION));
  CHECK(settles(completion, 1));
  CHECK(settles(following, 1));
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(following) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(arguments) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_shut_down() == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA executable loading reports a rejected code object synchronously", "[hsa]") {
  gemstone_test::RpcFixture fixture;
  const gemstone_test::RpcEnvironment environment(fixture);
  REQUIRE((hsa_init() == HSA_STATUS_SUCCESS));
  const auto gpu = gpuAgent();
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true);
  hsa_code_object_reader_t reader{};
  REQUIRE((hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS));
  for (const auto operation : {gemstone::execution::operation::LoadProgram, gemstone::execution::operation::DescribeProgram,
                               gemstone::execution::operation::DescribeKernel}) {
    CAPTURE(std::string(operation));
    hsa_executable_t executable{};
    REQUIRE(
        (hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS));
    fixture.rejectNext(operation, gemstone::execution::FailureKind::InvalidProgram);
    CHECK((hsa_executable_load_agent_code_object(executable, gpu, reader, nullptr, nullptr) == HSA_STATUS_ERROR_INVALID_CODE_OBJECT));
    REQUIRE((hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS));
  }
  REQUIRE((hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_shut_down() == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA queue without an error callback stops quietly when queue fault messages are disabled", "[hsa][queue-error]") {
  MemoryLockSession const session;
  const gemstone_test::Environment quiet("HSA_ENABLE_QUEUE_FAULT_MESSAGE", "0");
  const auto gpu = gpuAgent();
  auto *queue = createQueue(gpu, nullptr);
  const auto failing = createSignal();
  const auto following = createSignal();
  publish(queue, dispatchPacket(queue, 0, 0xbad, failing), 1);
  CHECK(settles(failing, 1));
  publishBarrier(queue, 1, following);
  CHECK(settles(following, 1));
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(failing) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(following) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA halted queue does not stall device-to-host copies", "[hsa][queue-error]") {
  const gemstone_test::Environment timeout(gemstone::environment::HsaPagingTimeoutSeconds, "1");
  MemoryLockSession const session;
  const auto gpu = gpuAgent();
  QueueErrors errors;
  auto *queue = createQueue(gpu, &errors);
  const auto failing = createSignal();
  publish(queue, dispatchPacket(queue, 0, 0xbad, failing), 1);
  REQUIRE(errors.await());

  const auto coarse = regionWithFlag(gpu, HSA_REGION_GLOBAL_FLAG_COARSE_GRAINED);
  void *device = nullptr;
  REQUIRE((hsa_memory_allocate(coarse, 16, &device) == HSA_STATUS_SUCCESS));
  const std::array<uint32_t, 4> expected{1, 2, 3, 4};
  REQUIRE((hsa_memory_copy(device, expected.data(), sizeof(expected)) == HSA_STATUS_SUCCESS));
  const auto cpu = agentOf(HSA_DEVICE_TYPE_CPU);
  const auto copied = createSignal();
  std::array<uint32_t, 4> observed{};
  CHECK((hsa_amd_memory_async_copy(observed.data(), cpu, device, gpu, sizeof(observed), 0, nullptr, copied) == HSA_STATUS_SUCCESS));
  CHECK((hsa_signal_load_scacquire(copied) == 0));
  CHECK((observed == expected));
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(device) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(failing) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(copied) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA queue error callbacks may destroy each other's queues", "[hsa][queue-error]") {
  MemoryLockSession const session;
  const auto gpu = gpuAgent();
  struct Pair {
    std::atomic<hsa_queue_t *> queues[2]{};
    std::atomic<unsigned> entered{0};
    std::atomic<unsigned> finished{0};
    hsa_status_t destroyed[2]{HSA_STATUS_ERROR, HSA_STATUS_ERROR};
  } pair;
  struct Side {
    Pair *pair;
    unsigned index;
  } sides[2]{{.pair = &pair, .index = 0}, {.pair = &pair, .index = 1}};
  const auto destroyOther = [](hsa_status_t, hsa_queue_t *, void *data) {
    auto &[pair, index] = *static_cast<Side *>(data);
    pair->entered.fetch_add(1, std::memory_order_acq_rel);
    const auto deadline = std::chrono::steady_clock::now() + gemstone_test::scaled(std::chrono::seconds(5));
    while (pair->entered.load(std::memory_order_acquire) < 2 && std::chrono::steady_clock::now() < deadline)
      std::this_thread::yield();
    pair->destroyed[1 - index] = hsa_queue_destroy(pair->queues[1 - index].load());
    pair->finished.fetch_add(1, std::memory_order_release);
  };
  const auto failing = createSignal();
  for (unsigned index = 0; index < 2; ++index) {
    hsa_queue_t *queue = nullptr;
    REQUIRE((hsa_queue_create(gpu, 64, HSA_QUEUE_TYPE_MULTI, destroyOther, &sides[index], UINT32_MAX, UINT32_MAX, &queue)
             == HSA_STATUS_SUCCESS));
    pair.queues[index] = queue;
  }
  for (const auto &queue : pair.queues)
    publish(queue.load(), dispatchPacket(queue.load(), 0, 0xbad, failing), 1);
  REQUIRE(gemstone_test::waitUntil([&] { return pair.finished.load(std::memory_order_acquire) >= 2; }, std::chrono::seconds(10)));
  CHECK((pair.entered.load() == 2));
  CHECK((pair.destroyed[0] == HSA_STATUS_SUCCESS));
  CHECK((pair.destroyed[1] == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(failing) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA inactivated queue ignores later packets", "[hsa][queue-error]") {
  MemoryLockSession const session;
  const auto gpu = gpuAgent();
  QueueErrors errors;
  auto *queue = createQueue(gpu, &errors);
  const auto first = createSignal();
  const auto second = createSignal();
  publishBarrier(queue, 0, first);
  hsa_signal_wait_scacquire(first, HSA_SIGNAL_CONDITION_EQ, 0, UINT64_MAX, HSA_WAIT_STATE_BLOCKED);
  REQUIRE((hsa_queue_inactivate(queue) == HSA_STATUS_SUCCESS));
  publishBarrier(queue, 1, second);
  CHECK(settles(second, 1));
  CHECK((hsa_queue_load_read_index_scacquire(queue) == 1));
  CHECK((errors.calls.load() == 0));
  REQUIRE((hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(first) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_signal_destroy(second) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA locked interior view addresses the requested bytes", "[hsa][unified-memory]") {
  MemoryLockSession const session;
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *whole = nullptr;
  void *interior = nullptr;
  REQUIRE((hsa_amd_memory_lock(host.data(), sizeof(host), nullptr, 0, &whole) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_lock(host.data() + 2, 3 * sizeof(uint32_t), nullptr, 0, &interior) == HSA_STATUS_SUCCESS));
  uint32_t observed = 0;
  REQUIRE((hsa_memory_copy(&observed, interior, sizeof(observed)) == HSA_STATUS_SUCCESS));
  CHECK((observed == host[2]));
  CHECK((hsa_amd_memory_unlock(host.data() + 2) == HSA_STATUS_SUCCESS));
  CHECK((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA repeated locks retain memory until the final unlock", "[hsa][unified-memory]") {
  MemoryLockSession const session;
  std::array<uint32_t, 8> host{};
  void *first = nullptr;
  void *second = nullptr;
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
  MemoryLockSession const session;
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *first = nullptr;
  void *second = nullptr;
  REQUIRE((hsa_amd_memory_lock(host.data(), 4 * sizeof(uint32_t), nullptr, 0, &first) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_lock(host.data() + 2, 6 * sizeof(uint32_t), nullptr, 0, &second) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
  std::array<uint32_t, 6> observed{};
  REQUIRE((hsa_memory_copy(observed.data(), second, sizeof(observed)) == HSA_STATUS_SUCCESS));
  CHECK((std::equal(observed.begin(), observed.end(), host.begin() + 2)));
  CHECK((hsa_amd_memory_unlock(host.data() + 2) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA async copy and fill span overlapping lock backing fragments", "[hsa][unified-memory]") {
  MemoryLockSession const session;
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *first = nullptr;
  void *second = nullptr;
  REQUIRE(hsa_amd_memory_lock(host.data(), 4 * sizeof(uint32_t), nullptr, 0, &first) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_amd_memory_lock(host.data() + 2, 6 * sizeof(uint32_t), nullptr, 0, &second) == HSA_STATUS_SUCCESS);
  const auto cpu = agentOf(HSA_DEVICE_TYPE_CPU);
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
  MemoryLockSession const session;
  std::array<uint32_t, 8> host{11, 22, 33, 44, 55, 66, 77, 88};
  void *whole = nullptr;
  void *interior = nullptr;
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
  MemoryLockSession const session;
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
  MemoryLockSession const session;
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
  void *whole = nullptr;
  void *interior = nullptr;
  REQUIRE((hsa_amd_memory_lock_to_pool(host.data(), sizeof(host), nullptr, 0, pool, 0, &whole) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_amd_memory_lock_to_pool(host.data() + 2, 3 * sizeof(uint32_t), nullptr, 0, pool, 0, &interior) == HSA_STATUS_SUCCESS));
  uint32_t observed = 0;
  REQUIRE((hsa_memory_copy(&observed, interior, sizeof(observed)) == HSA_STATUS_SUCCESS));
  CHECK((observed == host[2]));
  CHECK((hsa_amd_memory_unlock(host.data() + 2) == HSA_STATUS_SUCCESS));
  CHECK((hsa_amd_memory_unlock(host.data()) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA images are linear over their data and read through a descriptor object", "[hsa][images]") {
  MemoryLockSession const session;
  const auto gpu = gpuAgent();
  bool supported = false;
  uint16_t minor = 1;
  REQUIRE((hsa_system_major_extension_supported(HSA_EXTENSION_IMAGES, 1, &minor, &supported) == HSA_STATUS_SUCCESS));
  REQUIRE((supported && minor == 0));
  hsa_ext_images_1_pfn_t table{};
  REQUIRE((hsa_system_get_major_extension_table(HSA_EXTENSION_IMAGES, 1, sizeof(table), &table) == HSA_STATUS_SUCCESS));
  REQUIRE((table.hsa_ext_image_create == hsa_ext_image_create && table.hsa_ext_image_clear == hsa_ext_image_clear));

  // UNSIGNED_INT16 RG, 70 by 3: rows of 280 bytes pad to 512
  const hsa_ext_image_format_t format{.channel_type = HSA_EXT_IMAGE_CHANNEL_TYPE_UNSIGNED_INT16,
                                      .channel_order = HSA_EXT_IMAGE_CHANNEL_ORDER_RG};
  const hsa_ext_image_descriptor_t descriptor{
      .geometry = HSA_EXT_IMAGE_GEOMETRY_2D, .width = 70, .height = 3, .depth = 0, .array_size = 0, .format = format};
  hsa_ext_image_data_info_t info{};
  REQUIRE((hsa_ext_image_data_get_info(gpu, &descriptor, HSA_ACCESS_PERMISSION_RW, &info) == HSA_STATUS_SUCCESS));
  REQUIRE((info.size == size_t{512} * 3 && info.alignment == 256));
  REQUIRE(
      (hsa_ext_image_data_get_info_with_layout(gpu, &descriptor, HSA_ACCESS_PERMISSION_RW, HSA_EXT_IMAGE_DATA_LAYOUT_LINEAR, 280, 0, &info)
       == static_cast<hsa_status_t>(HSA_EXT_STATUS_ERROR_IMAGE_PITCH_UNSUPPORTED)));
  const hsa_ext_image_format_t rgb{.channel_type = HSA_EXT_IMAGE_CHANNEL_TYPE_UNSIGNED_INT8,
                                   .channel_order = HSA_EXT_IMAGE_CHANNEL_ORDER_RGB};
  uint32_t capabilities = 0xff;
  REQUIRE((hsa_ext_image_get_capability(gpu, HSA_EXT_IMAGE_GEOMETRY_2D, &rgb, &capabilities) == HSA_STATUS_SUCCESS));
  REQUIRE((capabilities == HSA_EXT_IMAGE_CAPABILITY_NOT_SUPPORTED));

  const auto coarse = regionWithFlag(gpu, HSA_REGION_GLOBAL_FLAG_COARSE_GRAINED);
  void *data = nullptr;
  REQUIRE((hsa_memory_allocate(coarse, info.size, &data) == HSA_STATUS_SUCCESS));
  hsa_ext_image_t image{};
  REQUIRE((hsa_ext_image_create(gpu, &descriptor, data, HSA_ACCESS_PERMISSION_RW, &image) == HSA_STATUS_SUCCESS));
  std::array<uint32_t, 12> object{};
  REQUIRE((hsa_memory_copy(object.data(), reinterpret_cast<void *>(image.handle), sizeof(object)) == HSA_STATUS_SUCCESS));
  REQUIRE((object[0] == static_cast<uint32_t>(reinterpret_cast<uintptr_t>(data) >> 8)));
  REQUIRE((object[8] == HSA_EXT_IMAGE_CHANNEL_TYPE_UNSIGNED_INT16 && object[9] == HSA_EXT_IMAGE_CHANNEL_ORDER_RG && object[10] == 70));

  // a 2 by 2 block imported from rows 12 bytes apart lands 512 bytes a row from texel (3, 1), and exports back
  const std::array<uint16_t, 12> block{1, 2, 3, 4, 0xeeee, 0xeeee, 5, 6, 7, 8, 0xeeee, 0xeeee};
  const hsa_ext_image_region_t region{.offset = {.x = 3, .y = 1, .z = 0}, .range = {.x = 2, .y = 2, .z = 1}};
  REQUIRE((hsa_ext_image_import(gpu, block.data(), 12, 0, image, &region) == HSA_STATUS_SUCCESS));
  std::array<uint16_t, 4> row{};
  REQUIRE((hsa_memory_copy(row.data(), static_cast<char *>(data) + 512 + (size_t{3} * 4), sizeof(row)) == HSA_STATUS_SUCCESS));
  REQUIRE((row == std::array<uint16_t, 4>{1, 2, 3, 4}));
  const uint32_t fill[4] = {9, 70000, 0, 0};
  const hsa_ext_image_region_t corner{.offset = {.x = 4, .y = 2, .z = 0}, .range = {.x = 1, .y = 1, .z = 1}};
  REQUIRE((hsa_ext_image_clear(gpu, image, fill, &corner) == HSA_STATUS_SUCCESS));
  std::array<uint16_t, 8> exported{};
  REQUIRE((hsa_ext_image_export(gpu, image, exported.data(), 0, 0, &region) == HSA_STATUS_SUCCESS));
  REQUIRE((exported == std::array<uint16_t, 8>{1, 2, 3, 4, 5, 6, 9, 0xffff}));
  REQUIRE((hsa_ext_image_destroy(gpu, image) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_ext_image_destroy(gpu, image) == HSA_STATUS_ERROR_INVALID_ARGUMENT));
  REQUIRE((hsa_memory_free(data) == HSA_STATUS_SUCCESS));
}

TEST_CASE("HSA pitched copies move only the rows between device and host", "[hsa][pitched]") {
  MemoryLockSession const session;
  const auto gpu = agentOf(HSA_DEVICE_TYPE_GPU);
  const auto coarse = regionWithFlag(gpu, HSA_REGION_GLOBAL_FLAG_COARSE_GRAINED);
  constexpr size_t row = 4;
  constexpr size_t rows = 3;
  constexpr size_t devicePitch = 8;
  constexpr size_t deviceSlice = 32;
  constexpr size_t hostPitch = 6;
  constexpr size_t hostSlice = 24;
  constexpr size_t peerPitch = 12;
  constexpr size_t peerSlice = 40;
  constexpr uint8_t gap = 0xee;
  const auto value = [](size_t z, size_t y, size_t c) { return static_cast<uint8_t>((z * 40) + (y * 9) + c + 1); };
  std::vector<uint8_t> host(2 * hostSlice, gap);
  for (size_t z = 0; z < 2; ++z)
    for (size_t y = 0; y < rows; ++y)
      for (size_t c = 0; c < row; ++c)
        host[(z * hostSlice) + (y * hostPitch) + c] = value(z, y, c);
  void *device = nullptr;
  void *peer = nullptr;
  REQUIRE((hsa_memory_allocate(coarse, 2 * deviceSlice, &device) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_allocate(coarse, 2 * peerSlice, &peer) == HSA_STATUS_SUCCESS));

  std::vector<uint8_t> background(2 * deviceSlice, 0xaa);
  REQUIRE((hsa_memory_copy(device, background.data(), background.size()) == HSA_STATUS_SUCCESS));
  REQUIRE((gemstone_hsa_memory_copy_pitched(device, devicePitch, deviceSlice, host.data(), hostPitch, hostSlice, row, rows, 2)
           == HSA_STATUS_SUCCESS));
  std::vector<uint8_t> stored(2 * deviceSlice);
  REQUIRE((hsa_memory_copy(stored.data(), device, stored.size()) == HSA_STATUS_SUCCESS));
  std::vector<uint8_t> expectedStored(2 * deviceSlice, 0xaa);
  for (size_t z = 0; z < 2; ++z)
    for (size_t y = 0; y < rows; ++y)
      for (size_t c = 0; c < row; ++c)
        expectedStored[(z * deviceSlice) + (y * devicePitch) + c] = value(z, y, c);
  CHECK(stored == expectedStored);

  std::vector<uint8_t> out(2 * hostSlice, gap);
  REQUIRE((gemstone_hsa_memory_copy_pitched(out.data(), hostPitch, hostSlice, device, devicePitch, deviceSlice, row, rows, 2)
           == HSA_STATUS_SUCCESS));
  CHECK(out == host);

  std::vector<uint8_t> flat(2 * hostSlice, gap);
  REQUIRE((gemstone_hsa_memory_copy_pitched(flat.data(), hostPitch, hostSlice, device, devicePitch, deviceSlice, row, rows, 1)
           == HSA_STATUS_SUCCESS));
  CHECK(std::vector<uint8_t>(flat.begin(), flat.begin() + hostSlice) == std::vector<uint8_t>(host.begin(), host.begin() + hostSlice));
  CHECK(std::vector<uint8_t>(flat.begin() + hostSlice, flat.end()) == std::vector<uint8_t>(hostSlice, gap));

  std::vector<uint8_t> seed(2 * peerSlice, 0xbb);
  std::vector<uint8_t> peerBytes(seed.size());
  REQUIRE((hsa_memory_copy(peer, seed.data(), seed.size()) == HSA_STATUS_SUCCESS));
  REQUIRE(
      (gemstone_hsa_memory_copy_pitched(peer, peerPitch, peerSlice, device, devicePitch, deviceSlice, row, rows, 2) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(peerBytes.data(), peer, peerBytes.size()) == HSA_STATUS_SUCCESS));
  std::vector<uint8_t> expectedPeer(seed);
  for (size_t z = 0; z < 2; ++z)
    for (size_t y = 0; y < rows; ++y)
      for (size_t c = 0; c < row; ++c)
        expectedPeer[(z * peerSlice) + (y * peerPitch) + c] = value(z, y, c);
  CHECK(peerBytes == expectedPeer);

  REQUIRE((hsa_memory_copy(peer, seed.data(), seed.size()) == HSA_STATUS_SUCCESS));
  REQUIRE(
      (gemstone_hsa_memory_copy_pitched(peer, peerPitch, peerSlice, device, devicePitch, deviceSlice, row, rows, 1) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_copy(peerBytes.data(), peer, peerBytes.size()) == HSA_STATUS_SUCCESS));
  expectedPeer = seed;
  for (size_t y = 0; y < rows; ++y)
    for (size_t c = 0; c < row; ++c)
      expectedPeer[(y * peerPitch) + c] = value(0, y, c);
  CHECK(peerBytes == expectedPeer);

  REQUIRE((hsa_memory_free(device) == HSA_STATUS_SUCCESS));
  REQUIRE((hsa_memory_free(peer) == HSA_STATUS_SUCCESS));
}
