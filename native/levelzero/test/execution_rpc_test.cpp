#include <array>
#include <atomic>
#include <cstdlib>
#include <cstring>
#include <string>
#include <thread>
#include <vector>

#include <stdint.h>
#include <unistd.h>

#include <catch2/catch_test_macros.hpp>
#include <ze_api.h>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include "../../rpc/test/rpc_fixture.hpp"
#include "../../rpc/test/wire.hpp"
#include <arpa/inet.h>
#include <netinet/in.h>
#include <sys/socket.h>

namespace {

using namespace gemstone::execution;

constexpr uint64_t programId = 91;
constexpr uint64_t completionId = 92;
constexpr uint64_t clientId = 42;

void receiveAll(int socket, void *output, size_t size) {
  auto *bytes = static_cast<uint8_t *>(output);
  while (size != 0) {
    const auto count = recv(socket, bytes, size, 0);
    REQUIRE((count > 0));
    bytes += count;
    size -= static_cast<size_t>(count);
  }
}

void sendAll(int socket, const void *input, size_t size) {
  const auto *bytes = static_cast<const uint8_t *>(input);
  while (size != 0) {
    const auto count = send(socket, bytes, size, MSG_NOSIGNAL);
    REQUIRE((count > 0));
    bytes += count;
    size -= static_cast<size_t>(count);
  }
}

void respond(int socket, const std::vector<uint8_t> &payload = {}, bool failed = false) {
  std::vector<uint8_t> header;
  gemstone_test::wire::appendBigEndian<uint32_t>(header, ResponseMagic);
  gemstone_test::wire::appendBigEndian<uint64_t>(header, SchemaFingerprint);
  gemstone_test::wire::appendBigEndian<uint32_t>(header, failed ? 1 : 0);
  gemstone_test::wire::appendBigEndian<uint32_t>(header, static_cast<uint32_t>(payload.size()));
  sendAll(socket, header.data(), header.size());
  if (!payload.empty()) sendAll(socket, payload.data(), payload.size());
}

struct MockServer {
  MockServer() {
    listener = socket(AF_INET, SOCK_STREAM, 0);
    REQUIRE((listener >= 0));
    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    address.sin_port = 0;
    REQUIRE((bind(listener, reinterpret_cast<const sockaddr *>(&address), sizeof(address)) == 0));
    REQUIRE((listen(listener, 1) == 0));
    socklen_t size = sizeof(address);
    REQUIRE((getsockname(listener, reinterpret_cast<sockaddr *>(&address), &size) == 0));
    port = ntohs(address.sin_port);
    worker = std::thread([this] { serve(); });
  }

  ~MockServer() {
    if (listener >= 0) close(listener);
    if (worker.joinable()) worker.join();
  }

  uint16_t port = 0;
  std::vector<uint8_t> allocation;
  uint64_t allocationId = 0;
  std::atomic<uint32_t> writeRequests{0};
  std::atomic<bool> failNextCompletion{false};

private:
  void serve() {
    const int connection = accept(listener, nullptr, nullptr);
    REQUIRE((connection >= 0));
    bool running = true;
    while (running) {
      std::array<uint8_t, RequestHeaderBytes> header{};
      receiveAll(connection, header.data(), header.size());
      REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(header.data()) == RequestMagic));
      REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(header.data() + RequestFingerprintOffset) == SchemaFingerprint));
      REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(header.data() + RequestClientOffset) == clientId));
      REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(header.data() + RequestSessionHighOffset) != 0
               || gemstone_test::wire::readBigEndian<uint64_t>(header.data() + RequestSessionLowOffset) != 0));
      const uint64_t operationId = gemstone_test::wire::readBigEndian<uint64_t>(header.data() + RequestOperationOffset);
      std::vector<uint8_t> payload(gemstone_test::wire::readBigEndian<uint32_t>(header.data() + RequestPayloadSizeOffset));
      if (!payload.empty()) receiveAll(connection, payload.data(), payload.size());

      switch (operationId) {
        case operation::Ping:
          REQUIRE((payload.empty()));
          respond(connection);
          break;
        case operation::RegisterAllocation: registerAllocation(connection, payload); break;
        case operation::ReleaseAllocation:
          releaseAllocation(payload);
          respond(connection);
          running = releasedAllocations != 2;
          break;
        case operation::WriteAllocation:
          writeAllocation(payload);
          respond(connection);
          break;
        case operation::ReadAllocation: readAllocation(connection, payload); break;
        case operation::LoadProgram: loadProgram(connection, payload); break;
        case operation::UnloadProgram:
          REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == programId));
          respond(connection);
          break;
        case operation::DescribeKernel: describeKernel(connection, payload); break;
        case operation::Launch: launch(connection, payload); break;
        case operation::QueryCompletion: queryCompletion(connection, payload); break;
        case operation::AwaitCompletion: awaitCompletion(connection, payload); break;
        case operation::ReleaseCompletion:
          REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == completionId));
          respond(connection);
          break;
        case operation::CompletionDirtyRanges: dirtyRanges(connection, payload); break;
        case operation::CompletionPointerRelocations: {
          REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == completionId));
          std::vector<uint8_t> response;
          gemstone_test::wire::appendBigEndian<uint32_t>(response, 0);
          respond(connection, response);
          break;
        }
        case operation::CompletionPrints: {
          REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == completionId));
          std::vector<uint8_t> response;
          gemstone_test::wire::appendBigEndian<uint32_t>(response, 0);
          respond(connection, response);
          break;
        }
        default: REQUIRE((false));
      }
    }
    close(connection);
  }

  void registerAllocation(int connection, const std::vector<uint8_t> &payload) {
    REQUIRE((payload.size() == 16));
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == tag::AllocationKindDevice));
    const auto id = nextAllocationId++;
    if (allocationId == 0) {
      allocationId = id;
      allocation.resize(gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + 8));
    } else {
      REQUIRE((unrelatedAllocationId == 0));
      unrelatedAllocationId = id;
    }
    std::vector<uint8_t> response;
    gemstone_test::wire::appendBigEndian<uint64_t>(response, id);
    respond(connection, response);
  }

  void releaseAllocation(const std::vector<uint8_t> &payload) {
    const auto id = gemstone_test::wire::readBigEndian<uint64_t>(payload.data());
    REQUIRE((id == allocationId || id == unrelatedAllocationId));
    ++releasedAllocations;
  }

  void writeAllocation(const std::vector<uint8_t> &payload) {
    // The unrelated allocation is live but unreachable from this non-indirect
    // kernel, so it must not be mirrored for the launch.
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == allocationId));
    const auto offset = gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + 8);
    const auto size = gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + 16);
    REQUIRE((offset + size <= allocation.size()));
    REQUIRE((payload.size() == 20 + size));
    std::memcpy(allocation.data() + offset, payload.data() + 20, size);
    writeRequests.fetch_add(1, std::memory_order_relaxed);
  }

  void readAllocation(int connection, const std::vector<uint8_t> &payload) {
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == allocationId));
    const auto offset = gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + 8);
    const auto size = gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + 16);
    REQUIRE((offset == 4));
    REQUIRE((size == sizeof(uint32_t)));
    REQUIRE((offset + size <= allocation.size()));
    std::vector<uint8_t> response;
    gemstone_test::wire::appendBigEndian<uint32_t>(response, size);
    response.insert(response.end(), allocation.begin() + offset, allocation.begin() + offset + size);
    respond(connection, response);
  }

  void dirtyRanges(int connection, const std::vector<uint8_t> &payload) {
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == completionId));
    std::vector<uint8_t> response;
    gemstone_test::wire::appendBigEndian<uint32_t>(response, 1);
    gemstone_test::wire::appendBigEndian<uint64_t>(response, allocationId);
    gemstone_test::wire::appendBigEndian<uint64_t>(response, 4);
    gemstone_test::wire::appendBigEndian<uint32_t>(response, sizeof(uint32_t));
    respond(connection, response);
  }

  void loadProgram(int connection, const std::vector<uint8_t> &payload) {
    const auto encodingSize = gemstone_test::wire::readBigEndian<uint32_t>(payload.data());
    REQUIRE((encodingSize == std::string_view{"spirv-binary"}.size()));
    REQUIRE((std::string_view{reinterpret_cast<const char *>(payload.data() + 4), encodingSize} == "spirv-binary"));
    const auto imageOffset = 4 + encodingSize;
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + imageOffset) >= 4));
    REQUIRE((payload[imageOffset + 4] == 3 && payload[imageOffset + 5] == 2 && payload[imageOffset + 6] == 35
             && payload[imageOffset + 7] == 7));
    std::vector<uint8_t> response;
    gemstone_test::wire::appendBigEndian<uint64_t>(response, programId);
    respond(connection, response);
  }

  void describeKernel(int connection, const std::vector<uint8_t> &payload) {
    REQUIRE((payload.size() >= 12 && gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == programId));
    const auto nameSize = gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + 8);
    REQUIRE((payload.size() == 12 + nameSize));
    REQUIRE((std::string(reinterpret_cast<const char *>(payload.data() + 12), nameSize) == "write"));
    std::vector<uint8_t> response;
    gemstone_test::wire::appendBigEndian<uint32_t>(response, 28);
    gemstone_test::wire::appendBigEndian<uint32_t>(response, 4);
    gemstone_test::wire::appendBigEndian<uint32_t>(response, 4);
    for (const std::array<uint32_t, 4> parameter : {
             std::array<uint32_t, 4>{8, 0, 2, 0},
             std::array<uint32_t, 4>{4, 8, 1, 0},
             std::array<uint32_t, 4>{8, 16, 3, 0},
             std::array<uint32_t, 4>{4, 24, 1, 0},
         }) {
      gemstone_test::wire::appendBigEndian<uint32_t>(response, parameter[0]);
      gemstone_test::wire::appendBigEndian<uint32_t>(response, parameter[1]);
      gemstone_test::wire::appendBigEndian<uint64_t>(response, parameter[2] == 1   ? tag::KernelParameterKindScalar
                                                               : parameter[2] == 2 ? tag::KernelParameterKindAllocation
                                                                                   : tag::KernelParameterKindLocal);
      response.push_back(static_cast<uint8_t>(parameter[3]));
    }
    respond(connection, response);
  }

  void launch(int connection, const std::vector<uint8_t> &payload) {
    size_t cursor = 0;
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == programId));
    cursor += 8;
    const uint32_t nameSize = gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor);
    cursor += 4;
    REQUIRE((std::string(reinterpret_cast<const char *>(payload.data() + cursor), nameSize) == "write"));
    cursor += nameSize;
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor) == 4));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 4) == 1));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 8) == 1));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 12) == 2));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 16) == 1));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 20) == 1));
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor + 24) == 8));
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor + 32) == 1));
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor + 40) == 1));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 48) == 3));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 52) == 0));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 56) == 0));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 60) == 0));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 64) == 0));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 68) == 0));
    cursor += 72;
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor) == 4));
    cursor += 4;
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor) == tag::KernelArgumentAllocation));
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor + 8) == allocationId));
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor + 16) == 4));
    cursor += 24;
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor) == tag::KernelArgumentScalar));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 8) == 4));
    const auto *value = payload.data() + cursor + 12;
    cursor += 16;
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor) == tag::KernelArgumentLocal));
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor + 8) == 64));
    cursor += 16;
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data() + cursor) == tag::KernelArgumentScalar));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 8) == sizeof(uint32_t)));
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor + 12) == 0));
    cursor += 16;
    REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(payload.data() + cursor) == 0));
    cursor += 4;
    REQUIRE((cursor == payload.size()));
    std::memcpy(allocation.data() + 4, value, sizeof(uint32_t));
    std::vector<uint8_t> response;
    gemstone_test::wire::appendBigEndian<uint64_t>(response, completionId);
    respond(connection, response);
  }

  void queryCompletion(int connection, const std::vector<uint8_t> &payload) {
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == completionId));
    std::vector<uint8_t> response;
    gemstone_test::wire::appendBigEndian<uint64_t>(response, tag::CompletionStatusSucceeded);
    respond(connection, response);
  }

  void awaitCompletion(int connection, const std::vector<uint8_t> &payload) {
    REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(payload.data()) == completionId));
    if (failNextCompletion.exchange(false)) {
      std::vector<uint8_t> failure;
      gemstone_test::wire::appendBigEndian<uint64_t>(failure, tag::ExecutionFailureFailed);
      constexpr std::string_view message = "completion failed";
      gemstone_test::wire::appendBigEndian<uint32_t>(failure, static_cast<uint32_t>(message.size()));
      failure.insert(failure.end(), message.begin(), message.end());
      respond(connection, failure, true);
      return;
    }
    respond(connection);
  }

  int listener = -1;
  std::thread worker;
  uint64_t nextAllocationId = 1;
  uint64_t unrelatedAllocationId = 0;
  uint32_t releasedAllocations = 0;
};

ze_device_handle_t gemstoneDevice(ze_driver_handle_t *selectedDriver) {
  uint32_t driver_count = 0;
  REQUIRE((zeDriverGet(&driver_count, nullptr) == ZE_RESULT_SUCCESS));
  std::vector<ze_driver_handle_t> drivers(driver_count);
  REQUIRE((zeDriverGet(&driver_count, drivers.data()) == ZE_RESULT_SUCCESS));
  for (const auto driver : drivers) {
    uint32_t device_count = 0;
    REQUIRE((zeDeviceGet(driver, &device_count, nullptr) == ZE_RESULT_SUCCESS));
    std::vector<ze_device_handle_t> devices(device_count);
    REQUIRE((zeDeviceGet(driver, &device_count, devices.data()) == ZE_RESULT_SUCCESS));
    for (const auto device : devices) {
      ze_device_properties_t properties{};
      properties.stype = ZE_STRUCTURE_TYPE_DEVICE_PROPERTIES;
      REQUIRE((zeDeviceGetProperties(device, &properties) == ZE_RESULT_SUCCESS));
      if (std::strstr(properties.name, "Gemstone") != nullptr) {
        *selectedDriver = driver;
        return device;
      }
    }
  }
  return nullptr;
}

void crossWorkgroupApi() {
  gemstone_test::RpcFixture server;
  const auto port = std::to_string(server.port());
  REQUIRE((setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0));
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));
  ze_driver_handle_t driver = nullptr;
  auto device = gemstoneDevice(&driver);
  REQUIRE((device));
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS));
  ze_device_mem_alloc_desc_t deviceDesc{};
  deviceDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  ze_host_mem_alloc_desc_t hostDesc{};
  hostDesc.stype = ZE_STRUCTURE_TYPE_HOST_MEM_ALLOC_DESC;
  uint32_t *memory = nullptr;
  constexpr size_t capacity = 9 * 33 + 2;
  REQUIRE((zeMemAllocShared(context, &deviceDesc, &hostDesc, capacity * 4, 8, device, reinterpret_cast<void **>(&memory))
           == ZE_RESULT_SUCCESS));
  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  moduleDesc.inputSize = sizeof(server.spirv);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(server.spirv);
  ze_module_handle_t module = nullptr;
  REQUIRE((zeModuleCreate(context, device, &moduleDesc, &module, nullptr) == ZE_RESULT_SUCCESS));
  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "groups";
  ze_kernel_handle_t kernel = nullptr;
  REQUIRE((zeKernelCreate(module, &kernelDesc, &kernel) == ZE_RESULT_SUCCESS));
  void *globalOffsetAddress = nullptr;
  REQUIRE((zeDriverGetExtensionFunctionAddress(driver, "zeKernelSetGlobalOffsetExp", &globalOffsetAddress) == ZE_RESULT_SUCCESS));
  using SetGlobalOffset = ze_result_t(ZE_APICALL *)(ze_kernel_handle_t, uint32_t, uint32_t, uint32_t);
  auto setGlobalOffset = reinterpret_cast<SetGlobalOffset>(globalOffsetAddress);
  REQUIRE((setGlobalOffset));
  auto *output = memory + 1;
  REQUIRE((zeKernelSetArgumentValue(kernel, 0, sizeof(output), &output) == ZE_RESULT_SUCCESS));
  ze_event_pool_desc_t poolDesc{};
  poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
  poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE;
  poolDesc.count = 1;
  ze_event_pool_handle_t pool = nullptr;
  REQUIRE((zeEventPoolCreate(context, &poolDesc, 1, &device, &pool) == ZE_RESULT_SUCCESS));
  ze_event_desc_t eventDesc{};
  eventDesc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
  eventDesc.signal = ZE_EVENT_SCOPE_FLAG_HOST;
  eventDesc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
  ze_event_handle_t event = nullptr;
  REQUIRE((zeEventCreate(pool, &eventDesc, &event) == ZE_RESULT_SUCCESS));
  for (bool immediate : {false, true}) {
    ze_command_queue_desc_t queueDesc{};
    queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
    queueDesc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
    ze_command_queue_handle_t queue = nullptr;
    ze_command_list_handle_t list = nullptr;
    if (immediate) REQUIRE((zeCommandListCreateImmediate(context, device, &queueDesc, &list) == ZE_RESULT_SUCCESS));
    else {
      REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
      ze_command_list_desc_t listDesc{};
      listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
      REQUIRE((zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS));
    }
    for (uint32_t groups : {1U, 2U, 3U, 5U, 9U})
      for (uint32_t local : {31U, 32U, 33U})
        for (uint32_t seed : {17U, 91U}) {
          const auto count = groups * local;
          const uint32_t offset = seed == 17U ? 7U : 0U;
          std::fill(memory, memory + capacity, 0U);
          server.expect({local, groups, count, offset, seed});
          REQUIRE((zeEventHostReset(event) == ZE_RESULT_SUCCESS));
          REQUIRE((zeEventQueryStatus(event) == ZE_RESULT_NOT_READY));
          REQUIRE((zeKernelSetGroupSize(kernel, local, 1, 1) == ZE_RESULT_SUCCESS));
          REQUIRE(((offset == 0 ? setGlobalOffset(kernel, offset, 0, 0)
                                : zeKernelSetGlobalOffsetExp(kernel, offset, 0, 0)) == ZE_RESULT_SUCCESS));
          REQUIRE((zeKernelSetArgumentValue(kernel, 1, sizeof(seed), &seed) == ZE_RESULT_SUCCESS));
          const ze_group_count_t geometry{groups, 1, 1};
          REQUIRE((zeCommandListAppendLaunchKernel(list, kernel, &geometry, event, 0, nullptr) == ZE_RESULT_SUCCESS));
          if (!immediate) {
            REQUIRE((zeEventQueryStatus(event) == ZE_RESULT_NOT_READY));
            REQUIRE((std::all_of(memory, memory + capacity, [](uint32_t value) { return value == 0; })));
            REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));
            REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
          }
          REQUIRE((zeEventHostSynchronize(event, 5000000000ULL) == ZE_RESULT_SUCCESS));
          REQUIRE((zeEventQueryStatus(event) == ZE_RESULT_SUCCESS));
          for (uint32_t index = 0; index < count; ++index)
            REQUIRE((output[index] == seed + offset + index));
          REQUIRE((memory[0] == 0));
          REQUIRE((std::all_of(memory + count + 1, memory + capacity, [](uint32_t value) { return value == 0; })));
          if (!immediate) REQUIRE((zeCommandListReset(list) == ZE_RESULT_SUCCESS));
        }
    REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
    if (queue) REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  }
  // A recorded launch can be replayed with an ordinary malloc allocation.
  auto *ordinary = static_cast<uint32_t *>(std::malloc(64));
  REQUIRE((ordinary));
  REQUIRE((zeKernelSetArgumentValue(kernel, 0, sizeof(ordinary), &ordinary) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetGroupSize(kernel, 4, 1, 1) == ZE_RESULT_SUCCESS));
  REQUIRE((setGlobalOffset(kernel, 0, 0, 0) == ZE_RESULT_SUCCESS));
  const uint32_t replaySeed = 29;
  REQUIRE((zeKernelSetArgumentValue(kernel, 1, sizeof(replaySeed), &replaySeed) == ZE_RESULT_SUCCESS));
  ze_command_queue_desc_t replayQueueDesc{};
  replayQueueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  replayQueueDesc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  ze_command_queue_handle_t replayQueue = nullptr;
  REQUIRE((zeCommandQueueCreate(context, device, &replayQueueDesc, &replayQueue) == ZE_RESULT_SUCCESS));
  ze_command_list_desc_t replayListDesc{};
  replayListDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t replayList = nullptr;
  REQUIRE((zeCommandListCreate(context, device, &replayListDesc, &replayList) == ZE_RESULT_SUCCESS));
  const ze_group_count_t replayGroups{1, 1, 1};
  REQUIRE((zeCommandListAppendLaunchKernel(replayList, kernel, &replayGroups, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(replayList) == ZE_RESULT_SUCCESS));
  for (unsigned replay = 0; replay < 2; ++replay) {
    std::fill(ordinary, ordinary + 16, 0U);
    server.expect({4, 1, 4, 0, replaySeed});
    REQUIRE((zeCommandQueueExecuteCommandLists(replayQueue, 1, &replayList, nullptr) == ZE_RESULT_SUCCESS));
    for (uint32_t index = 0; index < 4; ++index) REQUIRE((ordinary[index] == replaySeed + index));
  }
  REQUIRE((zeCommandListDestroy(replayList) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(replayQueue) == ZE_RESULT_SUCCESS));
  std::free(ordinary);
  REQUIRE((zeEventDestroy(event) == ZE_RESULT_SUCCESS));
  REQUIRE((zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS));
  REQUIRE((zeModuleDestroy(module) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(context, memory) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
  REQUIRE((server.launches() == 62));
  server.check();
}

} // namespace

TEST_CASE("Level Zero execution RPC", "[level-zero][rpc]") {
  MockServer server;
  const auto port = std::to_string(server.port);
  REQUIRE((setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0));
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));

  ze_driver_handle_t driver = nullptr;
  const auto device = gemstoneDevice(&driver);
  REQUIRE((device && driver));
  const char *driverError = nullptr;
  REQUIRE((zeDriverGetLastErrorDescription(driver, &driverError) == ZE_RESULT_SUCCESS));
  REQUIRE((driverError && driverError[0] != '\0'));
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS));

  ze_device_mem_alloc_desc_t allocationDesc{};
  allocationDesc.stype = ZE_STRUCTURE_TYPE_DEVICE_MEM_ALLOC_DESC;
  void *memory = nullptr;
  REQUIRE((zeMemAllocDevice(context, &allocationDesc, 16, 8, device, &memory) == ZE_RESULT_SUCCESS));
  std::memset(memory, 0, 16);
  void *unrelatedMemory = nullptr;
  REQUIRE((zeMemAllocDevice(context, &allocationDesc, 16, 8, device, &unrelatedMemory) == ZE_RESULT_SUCCESS));
  std::memset(unrelatedMemory, 0, 16);

  // A minimal SPIR-V kernel signature with a CrossWorkgroup pointer, scalar,
  // Workgroup pointer, and scalar. DPC++ uses a null host pointer for some
  // zero-valued ABI scalars, so metadata must distinguish those from local
  // memory arguments.
  const uint32_t image[] = {
      0x07230203,
      0x00010600,
      0,
      12,
      0,
      (5U << 16) | 15U,
      6,
      6,
      0x74697277,
      0x00000065,
      (6U << 16) | 5U,
      2,
      0x5f746f6e,
      0x636e7566,
      0x6e6f6974,
      0,
      (4U << 16) | 5U,
      6,
      0x74697277,
      0x00000065,
      (2U << 16) | 19U,
      1,
      (4U << 16) | 21U,
      2,
      32,
      0,
      (4U << 16) | 32U,
      3,
      5,
      2,
      (4U << 16) | 32U,
      4,
      4,
      2,
      (7U << 16) | 33U,
      5,
      1,
      3,
      2,
      4,
      2,
      (5U << 16) | 54U,
      1,
      6,
      0,
      5,
      (3U << 16) | 55U,
      3,
      7,
      (3U << 16) | 55U,
      2,
      8,
      (3U << 16) | 55U,
      4,
      9,
      (3U << 16) | 55U,
      2,
      10,
      (2U << 16) | 248U,
      11,
      (1U << 16) | 253U,
      (1U << 16) | 56U,
  };
  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  moduleDesc.inputSize = sizeof(image);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(image);
  ze_module_handle_t module = nullptr;
  ze_module_build_log_handle_t log = nullptr;
  REQUIRE((zeModuleCreate(context, device, &moduleDesc, &module, &log) == ZE_RESULT_SUCCESS));
  void *functionPointer = nullptr;
  REQUIRE((zeModuleGetFunctionPointer(module, "not_function", &functionPointer) == ZE_RESULT_ERROR_INVALID_FUNCTION_NAME));
  REQUIRE((zeModuleGetFunctionPointer(module, "write", &functionPointer) == ZE_RESULT_SUCCESS));
  REQUIRE(((reinterpret_cast<uintptr_t>(functionPointer) & 0xffffffffU) == 6U));
  ze_module_properties_t moduleProperties{};
  moduleProperties.stype = ZE_STRUCTURE_TYPE_MODULE_PROPERTIES;
  REQUIRE((zeModuleGetProperties(module, &moduleProperties) == ZE_RESULT_SUCCESS));
  REQUIRE((moduleProperties.flags == 0));
  size_t nativeBinarySize = 0;
  REQUIRE((zeModuleGetNativeBinary(module, &nativeBinarySize, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((nativeBinarySize == sizeof(image)));
  std::vector<uint8_t> nativeBinary(nativeBinarySize);
  REQUIRE((zeModuleGetNativeBinary(module, &nativeBinarySize, nativeBinary.data()) == ZE_RESULT_SUCCESS));
  const auto *imageBytes = reinterpret_cast<const uint8_t *>(image);
  REQUIRE((nativeBinary == std::vector<uint8_t>(imageBytes, imageBytes + sizeof(image))));
  REQUIRE((zeModuleBuildLogDestroy(log) == ZE_RESULT_SUCCESS));

  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "write";
  ze_kernel_handle_t kernel = nullptr;
  REQUIRE((zeKernelCreate(module, &kernelDesc, &kernel) == ZE_RESULT_SUCCESS));
  ze_kernel_preferred_group_size_properties_t preferred{};
  preferred.stype = ZE_STRUCTURE_TYPE_KERNEL_PREFERRED_GROUP_SIZE_PROPERTIES;
  ze_kernel_properties_t kernelProperties{};
  kernelProperties.stype = ZE_STRUCTURE_TYPE_KERNEL_PROPERTIES;
  kernelProperties.pNext = &preferred;
  REQUIRE((zeKernelGetProperties(kernel, &kernelProperties) == ZE_RESULT_SUCCESS));
  REQUIRE((kernelProperties.maxSubgroupSize == 32));
  REQUIRE((kernelProperties.maxNumSubgroups == 128));
  REQUIRE((preferred.preferredMultiple == 8));
  uint32_t sourceAttributesSize = 0;
  REQUIRE((zeKernelGetSourceAttributes(kernel, &sourceAttributesSize, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((sourceAttributesSize == 1));
  std::vector<char> sourceAttributes(sourceAttributesSize);
  auto *sourceAttributesData = sourceAttributes.data();
  REQUIRE((zeKernelGetSourceAttributes(kernel, &sourceAttributesSize, &sourceAttributesData) == ZE_RESULT_SUCCESS));
  REQUIRE((sourceAttributesSize == 1));
  REQUIRE((sourceAttributes.front() == '\0'));
  uint32_t suggestedX = 0;
  uint32_t suggestedY = 0;
  uint32_t suggestedZ = 0;
  REQUIRE((zeKernelSuggestGroupSize(kernel, 521, 7, 3, &suggestedX, &suggestedY, &suggestedZ) == ZE_RESULT_SUCCESS));
  REQUIRE((suggestedX > 0 && 521 % suggestedX == 0));
  REQUIRE((suggestedY > 0 && 7 % suggestedY == 0));
  REQUIRE((suggestedZ > 0 && 3 % suggestedZ == 0));
  REQUIRE((static_cast<uint64_t>(suggestedX) * suggestedY * suggestedZ <= 1024));
  REQUIRE((zeKernelSetGroupSize(kernel, 4, 1, 1) == ZE_RESULT_SUCCESS));
  void *interior = static_cast<uint8_t *>(memory) + 4;
  const uint32_t expected = 0x78563412;
  REQUIRE((zeKernelSetArgumentValue(kernel, 0, sizeof(interior), &interior) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetArgumentValue(kernel, 1, sizeof(expected), &expected) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetArgumentValue(kernel, 2, 64, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetArgumentValue(kernel, 3, sizeof(uint32_t), nullptr) == ZE_RESULT_SUCCESS));

  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  REQUIRE((zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS));
  const ze_group_count_t groups{2, 1, 1};
  REQUIRE((zeCommandListAppendLaunchKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(context, memory) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
  REQUIRE((zeMemFree(context, unrelatedMemory) == ZE_RESULT_SUCCESS));
  unrelatedMemory = nullptr;
  REQUIRE((zeKernelDestroy(kernel) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
  REQUIRE((zeModuleDestroy(module) == ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE));
  const uint32_t changedAfterRecording = 0xabcdef01;
  REQUIRE((zeKernelSetArgumentValue(kernel, 1, sizeof(changedAfterRecording), &changedAfterRecording) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetGroupSize(kernel, 8, 1, 1) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((*reinterpret_cast<uint32_t *>(static_cast<uint8_t *>(memory) + 4) == expected));
  REQUIRE((server.writeRequests.load(std::memory_order_relaxed) == 1));
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((server.writeRequests.load(std::memory_order_relaxed) == 1));
  static_cast<uint8_t *>(memory)[8] = 0x5a;
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((server.writeRequests.load(std::memory_order_relaxed) == 2));

  ze_fence_desc_t fenceDesc{};
  fenceDesc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  ze_fence_handle_t failedFence = nullptr;
  REQUIRE((zeFenceCreate(queue, &fenceDesc, &failedFence) == ZE_RESULT_SUCCESS));
  server.failNextCompletion.store(true);
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, failedFence) == ZE_RESULT_ERROR_DEVICE_LOST));
  REQUIRE((zeFenceHostSynchronize(failedFence, 1'000'000'000ULL) == ZE_RESULT_ERROR_DEVICE_LOST));
  REQUIRE((zeFenceQueryStatus(failedFence) == ZE_RESULT_ERROR_DEVICE_LOST));
  REQUIRE((zeFenceDestroy(failedFence) == ZE_RESULT_SUCCESS));

  REQUIRE((zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS));
  REQUIRE((zeModuleDestroy(module) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(context, memory) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
}

TEST_CASE("Level Zero cross-workgroup API", "[level-zero][rpc]") { crossWorkgroupApi(); }

TEST_CASE("Level Zero kernel accepts an interior ordinary host pointer", "[level-zero][rpc]") {
  gemstone_test::RpcFixture server;
  const auto port = std::to_string(server.port());
  REQUIRE((setenv("GEMSTONE_RPC_PORT", port.c_str(), 1) == 0));
  REQUIRE((setenv("GEMSTONE_RPC_CLIENT", "42", 1) == 0));
  REQUIRE((zeInit(ZE_INIT_FLAG_GPU_ONLY) == ZE_RESULT_SUCCESS));

  ze_driver_handle_t driver = nullptr;
  const auto device = gemstoneDevice(&driver);
  REQUIRE((device));
  ze_context_desc_t contextDesc{};
  contextDesc.stype = ZE_STRUCTURE_TYPE_CONTEXT_DESC;
  ze_context_handle_t context = nullptr;
  REQUIRE((zeContextCreate(driver, &contextDesc, &context) == ZE_RESULT_SUCCESS));

  ze_module_desc_t moduleDesc{};
  moduleDesc.stype = ZE_STRUCTURE_TYPE_MODULE_DESC;
  moduleDesc.format = ZE_MODULE_FORMAT_IL_SPIRV;
  moduleDesc.inputSize = sizeof(server.spirv);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(server.spirv);
  ze_module_handle_t module = nullptr;
  REQUIRE((zeModuleCreate(context, device, &moduleDesc, &module, nullptr) == ZE_RESULT_SUCCESS));
  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "groups";
  ze_kernel_handle_t kernel = nullptr;
  REQUIRE((zeKernelCreate(module, &kernelDesc, &kernel) == ZE_RESULT_SUCCESS));

  auto *base = static_cast<uint32_t *>(std::malloc(64));
  REQUIRE((base));
  std::fill(base, base + 16, 0U);
  auto *interior = base + 1;
  const uint32_t seed = 29;
  REQUIRE((zeKernelSetArgumentValue(kernel, 0, sizeof(base), &base) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetArgumentValue(kernel, 1, sizeof(seed), &seed) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetGroupSize(kernel, 4, 1, 1) == ZE_RESULT_SUCCESS));

  ze_command_queue_desc_t queueDesc{};
  queueDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
  queueDesc.mode = ZE_COMMAND_QUEUE_MODE_SYNCHRONOUS;
  ze_command_queue_handle_t queue = nullptr;
  REQUIRE((zeCommandQueueCreate(context, device, &queueDesc, &queue) == ZE_RESULT_SUCCESS));
  ze_command_list_desc_t listDesc{};
  listDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_LIST_DESC;
  ze_command_list_handle_t list = nullptr;
  REQUIRE((zeCommandListCreate(context, device, &listDesc, &list) == ZE_RESULT_SUCCESS));
  const ze_group_count_t groups{1, 1, 1};
  REQUIRE((zeCommandListAppendLaunchKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));

  server.expect({4, 1, 4, 0, seed});
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
  for (uint32_t index = 0; index < 4; ++index) REQUIRE((base[index] == seed + index));

  // The same kernel and malloc allocation should also accept an offset pointer.
  REQUIRE((zeCommandListReset(list) == ZE_RESULT_SUCCESS));
  std::fill(base, base + 16, 0U);
  REQUIRE((zeKernelSetArgumentValue(kernel, 0, sizeof(interior), &interior) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendLaunchKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));
  server.expect({4, 1, 4, 0, seed});
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((base[0] == 0));
  for (uint32_t index = 0; index < 4; ++index) REQUIRE((interior[index] == seed + index));

  REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS));
  REQUIRE((zeModuleDestroy(module) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
  std::free(base);
  REQUIRE((server.launches() == 2));
  server.check();
}
