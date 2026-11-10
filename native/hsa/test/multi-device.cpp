#include <csignal>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <string>
#include <vector>

#include <hsa.h>
#include <hsa_ext_amd.h>

#include <catch2/catch_test_macros.hpp>

#include <gemstone/child_test.hpp>
#include <gemstone/test_support.hpp>

#include "../../rpc/test/rpc_fixture.hpp"
#include "code_object_fixture.hpp"
#include <environment_keys.hpp>

namespace {

gemstone_test::RpcFixture fixture;
const gemstone_test::RpcEnvironment RpcEndpoint(fixture);
const gemstone_test::Environment DeviceCount(gemstone::environment::DeviceCount, "2");

struct Session {
  Session() { REQUIRE(hsa_init() == HSA_STATUS_SUCCESS); }
  ~Session() { hsa_shut_down(); }
  Session(const Session &) = delete;
  Session &operator=(const Session &) = delete;
};

struct Agents {
  hsa_agent_t cpu{};
  std::vector<hsa_agent_t> gpus;
};

Agents agents() {
  Agents found;
  REQUIRE(hsa_iterate_agents(
              [](hsa_agent_t agent, void *data) {
                auto &self = *static_cast<Agents *>(data);
                hsa_device_type_t type{};
                REQUIRE(hsa_agent_get_info(agent, HSA_AGENT_INFO_DEVICE, &type) == HSA_STATUS_SUCCESS);
                if (type == HSA_DEVICE_TYPE_GPU) self.gpus.push_back(agent);
                else self.cpu = agent;
                return HSA_STATUS_SUCCESS;
              },
              &found)
          == HSA_STATUS_SUCCESS);
  return found;
}

hsa_amd_memory_pool_t globalPool(hsa_agent_t agent) {
  hsa_amd_memory_pool_t pool{};
  REQUIRE(hsa_amd_agent_iterate_memory_pools(
              agent,
              [](hsa_amd_memory_pool_t candidate, void *data) {
                hsa_amd_segment_t segment{};
                REQUIRE(hsa_amd_memory_pool_get_info(candidate, HSA_AMD_MEMORY_POOL_INFO_SEGMENT, &segment) == HSA_STATUS_SUCCESS);
                if (segment == HSA_AMD_SEGMENT_GLOBAL) *static_cast<hsa_amd_memory_pool_t *>(data) = candidate;
                return HSA_STATUS_SUCCESS;
              },
              &pool)
          == HSA_STATUS_SUCCESS);
  return pool;
}

uint64_t available(hsa_agent_t agent) {
  uint64_t bytes = 0;
  REQUIRE(hsa_agent_get_info(agent, static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_MEMORY_AVAIL), &bytes) == HSA_STATUS_SUCCESS);
  return bytes;
}

std::string text(hsa_agent_t agent, hsa_agent_info_t attribute) {
  char value[64]{};
  REQUIRE(hsa_agent_get_info(agent, attribute, value) == HSA_STATUS_SUCCESS);
  return value;
}

template <typename T> T scalar(hsa_agent_t agent, hsa_agent_info_t attribute) {
  T value{};
  REQUIRE(hsa_agent_get_info(agent, attribute, &value) == HSA_STATUS_SUCCESS);
  return value;
}

std::vector<uint64_t> reachable(const void *pointer, hsa_agent_t *owner) {
  hsa_amd_pointer_info_t info{};
  info.size = sizeof(info);
  uint32_t count = 0;
  hsa_agent_t *accessible = nullptr;
  REQUIRE(hsa_amd_pointer_info(pointer, &info, [](size_t size) { return std::malloc(size); }, &count, &accessible) == HSA_STATUS_SUCCESS);
  *owner = info.agentOwner;
  std::vector<uint64_t> handles;
  handles.reserve(count);
  for (uint32_t index = 0; index < count; ++index)
    handles.push_back(accessible[index].handle);
  std::free(accessible);
  return handles;
}

struct Kernel {
  hsa_executable_t executable{};
  uint64_t object = 0;
};

Kernel loadKernel(hsa_agent_t agent) {
  const auto image = gemstone::hsa::test::codeObjectFixture(1, true);
  hsa_code_object_reader_t reader{};
  Kernel kernel;
  hsa_executable_symbol_t symbol{};
  REQUIRE(hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &kernel.executable)
          == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_executable_load_agent_code_object(kernel.executable, agent, reader, nullptr, nullptr) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_executable_freeze(kernel.executable, nullptr) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_executable_get_symbol_by_name(kernel.executable, "add.kd", &agent, &symbol) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_KERNEL_OBJECT, &kernel.object) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS);
  return kernel;
}

struct alignas(16) Arguments {
  void *output;
  uint32_t seed;
};

hsa_region_t kernargRegion(hsa_agent_t cpu) {
  hsa_region_t region{};
  REQUIRE(hsa_agent_iterate_regions(
              cpu,
              [](hsa_region_t candidate, void *data) {
                uint32_t flags = 0;
                if (hsa_region_get_info(candidate, HSA_REGION_INFO_GLOBAL_FLAGS, &flags) == HSA_STATUS_SUCCESS
                    && (flags & HSA_REGION_GLOBAL_FLAG_KERNARG) != 0)
                  *static_cast<hsa_region_t *>(data) = candidate;
                return HSA_STATUS_SUCCESS;
              },
              &region)
          == HSA_STATUS_SUCCESS);
  return region;
}

/** Publishes a one-item dispatch of `kernel` at ring slot `index` of `queue`, writing into `output`, and returns its completion signal. */
hsa_signal_t dispatchInto(hsa_queue_t *queue, uint64_t index, const Kernel &kernel, hsa_agent_t cpu, void *output, uint32_t seed) {
  void *memory = nullptr;
  REQUIRE(hsa_memory_allocate(kernargRegion(cpu), sizeof(Arguments), &memory) == HSA_STATUS_SUCCESS);
  *static_cast<Arguments *>(memory) = Arguments{.output = output, .seed = seed};
  hsa_signal_t completion{};
  REQUIRE(hsa_signal_create(1, 0, nullptr, &completion) == HSA_STATUS_SUCCESS);
  auto *packet = static_cast<hsa_kernel_dispatch_packet_t *>(queue->base_address) + index;
  *packet = {};
  packet->setup = 1;
  packet->workgroup_size_x = packet->workgroup_size_y = packet->workgroup_size_z = 1;
  packet->grid_size_x = packet->grid_size_y = packet->grid_size_z = 1;
  packet->kernel_object = kernel.object;
  packet->kernarg_address = memory;
  packet->completion_signal = completion;
  __atomic_store_n(&packet->header, static_cast<uint16_t>(HSA_PACKET_TYPE_KERNEL_DISPATCH), __ATOMIC_RELEASE);
  hsa_queue_store_write_index_screlease(queue, index + 1);
  hsa_signal_store_screlease(queue->doorbell_signal, static_cast<hsa_signal_value_t>(index));
  return completion;
}

} // namespace

TEST_CASE("HSA enumerates the configured number of GPU agents", "[hsa][multi-device]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  CHECK(gpus[0].handle != gpus[1].handle);
  CHECK(text(gpus[0], HSA_AGENT_INFO_NAME) == text(gpus[1], HSA_AGENT_INFO_NAME));
  CHECK(scalar<uint32_t>(gpus[0], HSA_AGENT_INFO_NODE) != scalar<uint32_t>(gpus[1], HSA_AGENT_INFO_NODE));
  CHECK(scalar<uint32_t>(gpus[0], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_BDFID))
        != scalar<uint32_t>(gpus[1], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_BDFID)));
  CHECK(text(gpus[0], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_UUID))
        != text(gpus[1], static_cast<hsa_agent_info_t>(HSA_AMD_AGENT_INFO_UUID)));

  const hsa_agent_t unknown{.handle = gpus[1].handle + (uint64_t{1} << 8)};
  uint32_t node = 0;
  CHECK(hsa_agent_get_info(unknown, HSA_AGENT_INFO_NODE, &node) == HSA_STATUS_ERROR_INVALID_AGENT);
  hsa_queue_t *queue = nullptr;
  CHECK(hsa_queue_create(unknown, 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, 0, 0, &queue) == HSA_STATUS_ERROR_INVALID_AGENT);
  REQUIRE(hsa_queue_create(gpus[1], 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, 0, 0, &queue) == HSA_STATUS_SUCCESS);
  CHECK(hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS);
  CHECK(cpu.handle != 0);
}

TEST_CASE("HSA charges allocations to the owning agent", "[hsa][multi-device]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  const auto pools = std::vector{globalPool(gpus[0]), globalPool(gpus[1])};
  CHECK(pools[0].handle != pools[1].handle);

  const auto untouched = available(gpus[0]);
  CHECK(available(gpus[1]) == untouched);
  void *remote = nullptr;
  constexpr size_t size = size_t{1} << 20;
  REQUIRE(hsa_amd_memory_pool_allocate(pools[1], size, 0, &remote) == HSA_STATUS_SUCCESS);
  CHECK(available(gpus[1]) == untouched - size);
  CHECK(available(gpus[0]) == untouched);

  hsa_agent_t owner{};
  const auto before = reachable(remote, &owner);
  CHECK(owner.handle == gpus[1].handle);
  CHECK(before == std::vector<uint64_t>{cpu.handle, gpus[1].handle});
  REQUIRE(hsa_amd_agents_allow_access(1, gpus.data(), nullptr, remote) == HSA_STATUS_SUCCESS);
  CHECK(reachable(remote, &owner) == std::vector<uint64_t>{cpu.handle, gpus[0].handle, gpus[1].handle});

  REQUIRE(hsa_amd_memory_pool_free(remote) == HSA_STATUS_SUCCESS);
  CHECK(available(gpus[1]) == untouched);
}

TEST_CASE("HSA sizes GPU pools and free memory from the selected device preset", "[hsa][multi-device]") {
  const gemstone_test::Environment device(gemstone::environment::Device, "Radeon RX 9070 XT");
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  const auto pool = globalPool(gpus[0]);
  size_t total = 0;
  REQUIRE(hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_SIZE, &total) == HSA_STATUS_SUCCESS);
  CHECK(total == size_t{16} << 30);
  size_t maxAllocation = 0;
  REQUIRE(hsa_amd_memory_pool_get_info(pool, HSA_AMD_MEMORY_POOL_INFO_ALLOC_MAX_SIZE, &maxAllocation) == HSA_STATUS_SUCCESS);
  CHECK(maxAllocation == total);
  CHECK(available(gpus[0]) > size_t{15} << 30);

  const auto used = total - available(gpus[0]);
  void *block = nullptr;
  constexpr size_t size = size_t{1} << 20;
  REQUIRE(hsa_amd_memory_pool_allocate(pool, size, 0, &block) == HSA_STATUS_SUCCESS);
  CHECK(total - available(gpus[0]) == used + size);
  CHECK(hsa_amd_memory_pool_free(block) == HSA_STATUS_SUCCESS);
  CHECK(total - available(gpus[0]) == used);
}

TEST_CASE("HSA reports peer pools as reachable only through a grant", "[hsa][multi-device]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  const auto remote = globalPool(gpus[1]);
  hsa_amd_memory_pool_access_t access{};
  REQUIRE(hsa_amd_agent_memory_pool_get_info(gpus[0], remote, HSA_AMD_AGENT_MEMORY_POOL_INFO_ACCESS, &access) == HSA_STATUS_SUCCESS);
  CHECK(access == HSA_AMD_MEMORY_POOL_ACCESS_DISALLOWED_BY_DEFAULT);
  REQUIRE(hsa_amd_agent_memory_pool_get_info(gpus[1], remote, HSA_AMD_AGENT_MEMORY_POOL_INFO_ACCESS, &access) == HSA_STATUS_SUCCESS);
  CHECK(access == HSA_AMD_MEMORY_POOL_ACCESS_ALLOWED_BY_DEFAULT);
  uint32_t hops = 9;
  REQUIRE(hsa_amd_agent_memory_pool_get_info(gpus[0], remote, HSA_AMD_AGENT_MEMORY_POOL_INFO_NUM_LINK_HOPS, &hops) == HSA_STATUS_SUCCESS);
  CHECK(hops == 1);
  REQUIRE(hsa_amd_agent_memory_pool_get_info(gpus[1], remote, HSA_AMD_AGENT_MEMORY_POOL_INFO_NUM_LINK_HOPS, &hops) == HSA_STATUS_SUCCESS);
  CHECK(hops == 0);
}

TEST_CASE("HSA copies bytes between agents", "[hsa][multi-device]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  std::vector<uint8_t> input(4096);
  for (size_t index = 0; index < input.size(); ++index)
    input[index] = static_cast<uint8_t>((index * 5) + 1);

  void *source = nullptr;
  void *destination = nullptr;
  REQUIRE(hsa_amd_memory_pool_allocate(globalPool(gpus[0]), input.size(), 0, &source) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_amd_memory_pool_allocate(globalPool(gpus[1]), input.size(), 0, &destination) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_memory_copy(source, input.data(), input.size()) == HSA_STATUS_SUCCESS);

  hsa_signal_t done{};
  REQUIRE(hsa_signal_create(1, 0, nullptr, &done) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_amd_memory_async_copy(destination, gpus[1], source, gpus[0], input.size(), 0, nullptr, done) == HSA_STATUS_SUCCESS);
  CHECK(hsa_signal_wait_scacquire(done, HSA_SIGNAL_CONDITION_EQ, 0, UINT64_MAX, HSA_WAIT_STATE_BLOCKED) == 0);

  std::vector<uint8_t> output(input.size());
  REQUIRE(hsa_memory_copy(output.data(), destination, output.size()) == HSA_STATUS_SUCCESS);
  CHECK(output == input);
  CHECK(hsa_signal_destroy(done) == HSA_STATUS_SUCCESS);
  CHECK(hsa_amd_memory_pool_free(source) == HSA_STATUS_SUCCESS);
  CHECK(hsa_amd_memory_pool_free(destination) == HSA_STATUS_SUCCESS);
}

TEST_CASE("HSA loads executables onto the requested agent", "[hsa][multi-device]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  const auto image = gemstone::hsa::test::variableKernelObjectFixture();
  const auto untouched = available(gpus[0]);

  for (const size_t device : {size_t{1}, size_t{0}}) {
    CAPTURE(device);
    fixture.programCatalogue({{.name = "add", .linkageName = "add.kd"}},
                             {{.name = "arbitrary_bss", .virtualAddress = 0x2000, .size = 32, .pointerOffsets = {}}});
    fixture.expectGlobalBinding("arbitrary_bss", 0, std::vector<uint8_t>(32));
    hsa_code_object_reader_t reader{};
    hsa_executable_t executable{};
    hsa_executable_symbol_t symbol{};
    uint64_t address = 0;
    REQUIRE(hsa_code_object_reader_create_from_memory(image.data(), image.size(), &reader) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_executable_create_alt(HSA_PROFILE_BASE, HSA_DEFAULT_FLOAT_ROUNDING_MODE_NEAR, nullptr, &executable) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_executable_load_agent_code_object(executable, gpus[device], reader, nullptr, nullptr) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_executable_freeze(executable, nullptr) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_executable_get_symbol_by_name(executable, "arbitrary_bss", &gpus[device], &symbol) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_executable_symbol_get_info(symbol, HSA_EXECUTABLE_SYMBOL_INFO_VARIABLE_ADDRESS, &address) == HSA_STATUS_SUCCESS);

    hsa_agent_t owner{};
    const auto reach = reachable(reinterpret_cast<void *>(address), &owner);
    CHECK(owner.handle == gpus[device].handle);
    CHECK(reach == std::vector<uint64_t>{cpu.handle, gpus[device].handle});
    CHECK(available(gpus[device]) < untouched);
    CHECK(available(gpus[1 - device]) == untouched);
    REQUIRE(hsa_executable_destroy(executable) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_code_object_reader_destroy(reader) == HSA_STATUS_SUCCESS);
  }
}

TEST_CASE("HSA dispatches reach a peer agent's memory only through a grant", "[hsa][multi-device]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  const auto kernel = loadKernel(gpus[1]);
  hsa_queue_t *queue = nullptr;
  REQUIRE(hsa_queue_create(gpus[1], 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS);

  void *own = nullptr;
  void *peer = nullptr;
  REQUIRE(hsa_amd_memory_pool_allocate(globalPool(gpus[1]), 4096, 0, &own) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_amd_memory_pool_allocate(globalPool(gpus[0]), 4096, 0, &peer) == HSA_STATUS_SUCCESS);

  uint64_t published = 0;
  const auto run = [&](void *output, uint32_t seed) {
    fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = seed});
    const auto completion = dispatchInto(queue, published++, kernel, cpu, output, seed);
    const auto outcome = hsa_signal_wait_scacquire(completion, HSA_SIGNAL_CONDITION_EQ, 0, UINT64_MAX, HSA_WAIT_STATE_BLOCKED);
    fixture.check();
    REQUIRE(hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS);
    return outcome;
  };
  CHECK(run(own, 11) == 0);

  REQUIRE(hsa_amd_agents_allow_access(1, &gpus[1], nullptr, peer) == HSA_STATUS_SUCCESS);
  CHECK(run(peer, 12) == 0);

  REQUIRE(hsa_queue_destroy(queue) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_amd_memory_pool_free(own) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_amd_memory_pool_free(peer) == HSA_STATUS_SUCCESS);
  REQUIRE(hsa_executable_destroy(kernel.executable) == HSA_STATUS_SUCCESS);
}

namespace {

struct IndirectPeer {
  static constexpr uint32_t Seed = 41;
  static constexpr uint32_t Pointee = 77;
  Kernel kernel;
  hsa_queue_t *queue = nullptr;
  hsa_agent_t cpu{};
  hsa_agent_t peerOwner{};
  void *own = nullptr;
  void *table = nullptr;
  void *peer = nullptr;

  /** A kernel on `gpus[1]` that finds `peer`, owned by `gpus[0]`, only through a pointer stored in `table`. */
  IndirectPeer(hsa_agent_t cpuAgent, const std::vector<hsa_agent_t> &gpus)
      : kernel(loadKernel(gpus[1])), cpu(cpuAgent), peerOwner(gpus[0]) {
    REQUIRE(hsa_queue_create(gpus[1], 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_amd_memory_pool_allocate(globalPool(gpus[1]), 4096, 0, &own) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_amd_memory_pool_allocate(globalPool(gpus[1]), 4096, 0, &table) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_amd_memory_pool_allocate(globalPool(gpus[0]), 4096, 0, &peer) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_memory_copy(peer, &Pointee, sizeof(Pointee)) == HSA_STATUS_SUCCESS);
    REQUIRE(hsa_memory_copy(table, &peer, sizeof(peer)) == HSA_STATUS_SUCCESS);
  }
  IndirectPeer(const IndirectPeer &) = delete;
  IndirectPeer &operator=(const IndirectPeer &) = delete;
  ~IndirectPeer() {
    (void)hsa_queue_destroy(queue);
    (void)hsa_amd_memory_pool_free(own);
    (void)hsa_amd_memory_pool_free(table);
    (void)hsa_amd_memory_pool_free(peer);
    (void)hsa_executable_destroy(kernel.executable);
  }

  [[nodiscard]] int64_t run(uint64_t index) const {
    fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = Seed});
    fixture.expectIndirectInput(table, Pointee);
    const auto completion = dispatchInto(queue, index, kernel, cpu, own, Seed);
    const auto outcome = hsa_signal_wait_scacquire(completion, HSA_SIGNAL_CONDITION_EQ, 0, UINT64_MAX, HSA_WAIT_STATE_BLOCKED);
    REQUIRE(hsa_signal_destroy(completion) == HSA_STATUS_SUCCESS);
    return outcome;
  }
};

} // namespace

TEST_CASE("HSA dispatches load a peer pointer stored in device memory only through a grant", "[hsa][multi-device]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  const IndirectPeer peer(cpu, gpus);
  REQUIRE(hsa_amd_agents_allow_access(1, &gpus[1], nullptr, peer.peer) == HSA_STATUS_SUCCESS);
  CHECK(fixture.grants() > 0);
  CHECK(peer.run(0) == 0);
  fixture.check();
}

TEST_CASE("HSA ungranted indirect peer dispatch child", "[.]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  const IndirectPeer peer(cpu, gpus);
  (void)peer.run(0);
}

TEST_CASE("HSA dispatch loading an ungranted peer pointer from device memory aborts the process", "[hsa][multi-device]") {
  const auto outcome = gemstone_test::runChildTest("HSA ungranted indirect peer dispatch child");
  CHECK(outcome.signal == SIGABRT);
  CHECK(outcome.output.contains("inactivated due to async error"));
  CHECK(outcome.output.contains("cannot access an allocation"));
}

TEST_CASE("HSA ungranted peer dispatch child", "[.]") {
  const Session session;
  const auto [cpu, gpus] = agents();
  REQUIRE(gpus.size() == 2);
  const auto kernel = loadKernel(gpus[1]);
  hsa_queue_t *queue = nullptr;
  REQUIRE(hsa_queue_create(gpus[1], 64, HSA_QUEUE_TYPE_MULTI, nullptr, nullptr, UINT32_MAX, UINT32_MAX, &queue) == HSA_STATUS_SUCCESS);
  void *peer = nullptr;
  REQUIRE(hsa_amd_memory_pool_allocate(globalPool(gpus[0]), 4096, 0, &peer) == HSA_STATUS_SUCCESS);
  fixture.expect({.local = 1, .groups = 1, .global = 1, .offset = 0, .seed = 13});
  const auto completion = dispatchInto(queue, 0, kernel, cpu, peer, 13);
  (void)hsa_signal_wait_scacquire(completion, HSA_SIGNAL_CONDITION_EQ, 0, UINT64_MAX, HSA_WAIT_STATE_BLOCKED);
}

TEST_CASE("HSA dispatch into a peer agent's ungranted memory aborts the process", "[hsa][multi-device]") {
  const auto outcome = gemstone_test::runChildTest("HSA ungranted peer dispatch child");
  CHECK(outcome.signal == SIGABRT);
  CHECK(outcome.output.contains("inactivated due to async error"));
  CHECK(outcome.output.contains("cannot access an allocation"));
}
