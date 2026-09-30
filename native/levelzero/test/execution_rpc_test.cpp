#include <array>
#include <atomic>
#include <chrono>
#include <cstdlib>
#include <cstring>
#include <string>
#include <thread>
#include <vector>

#include <poll.h>
#include <stdint.h>
#include <unistd.h>

#include <catch2/catch_test_macros.hpp>
#include <ze_api.h>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include "../../rpc/test/rpc_fixture.hpp"
#include "../../rpc/test/wire.hpp"
#include <arpa/inet.h>
#include <environment_keys.hpp>
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
  std::vector<uint8_t> body = payload;
  if (body.empty() && !failed) gemstone_test::encodePayload(body, Unit{});
  std::vector<uint8_t> header;
  gemstone_test::wire::appendBigEndian<uint32_t>(header, ResponseMagic);
  gemstone_test::wire::appendBigEndian<uint32_t>(header, failed ? 1 : 0);
  gemstone_test::wire::appendBigEndian<uint32_t>(header, static_cast<uint32_t>(body.size()));
  sendAll(socket, header.data(), header.size());
  if (!body.empty()) sendAll(socket, body.data(), body.size());
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
  std::atomic<bool> pauseCompletion{false};
  std::atomic<bool> completionEntered{false};
  std::atomic<uint8_t> lastLaunchByte8{0};

private:
  void serve() {
    std::vector<pollfd> peers{{listener, POLLIN, 0}};
    bool running = true;
    while (running) {
      REQUIRE(poll(peers.data(), peers.size(), 30000) > 0);
      if (peers[0].revents & POLLIN) {
        const auto connection = accept(listener, nullptr, nullptr);
        REQUIRE(connection >= 0);
        std::array<uint8_t, 8> hello{};
        receiveAll(connection, hello.data(), hello.size());
        REQUIRE(gemstone_test::wire::readBigEndian<uint32_t>(hello.data()) == HelloMagic);
        REQUIRE(gemstone_test::wire::readBigEndian<uint32_t>(hello.data() + 4) == Schema.size());
        std::string schema(Schema.size(), '\0');
        receiveAll(connection, schema.data(), schema.size());
        REQUIRE(schema == Schema);
        std::vector<uint8_t> accepted;
        gemstone_test::wire::appendBigEndian<uint32_t>(accepted, HelloResponseMagic);
        gemstone_test::wire::appendBigEndian<uint32_t>(accepted, 0);
        sendAll(connection, accepted.data(), accepted.size());
        peers.push_back({connection, POLLIN, 0});
        continue;
      }
      size_t ready = 1;
      while (ready < peers.size() && peers[ready].revents == 0)
        ++ready;
      if (ready == peers.size()) continue;
      const int connection = peers[ready].fd;
      uint8_t probe{};
      if (recv(connection, &probe, 1, MSG_PEEK) == 0) {
        close(connection);
        peers.erase(peers.begin() + static_cast<ptrdiff_t>(ready));
        continue;
      }
      std::array<uint8_t, RequestHeaderBytes> header{};
      receiveAll(connection, header.data(), header.size());
      REQUIRE((gemstone_test::wire::readBigEndian<uint32_t>(header.data()) == RequestMagic));
      REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(header.data() + RequestClientOffset) == clientId));
      REQUIRE((gemstone_test::wire::readBigEndian<uint64_t>(header.data() + RequestSessionHighOffset) != 0
               || gemstone_test::wire::readBigEndian<uint64_t>(header.data() + RequestSessionLowOffset) != 0));
      const auto tagSize = gemstone_test::wire::readBigEndian<uint32_t>(header.data() + RequestTagSizeOffset);
      REQUIRE(tagSize > 0);
      REQUIRE(tagSize <= MaximumTagBytes);
      std::string operationTag(tagSize, '\0');
      receiveAll(connection, operationTag.data(), operationTag.size());
      std::vector<uint8_t> payload(gemstone_test::wire::readBigEndian<uint32_t>(header.data() + RequestPayloadSizeOffset));
      if (!payload.empty()) receiveAll(connection, payload.data(), payload.size());

      if (operationTag == operation::EnableHostMemory || operationTag == operation::Ping) {
        respond(connection);
      } else if (operationTag == operation::RegisterAllocation) {
        registerAllocation(connection, payload);
      } else if (operationTag == operation::RegisterAllocationAddress) {
        respond(connection);
      } else if (operationTag == operation::ReleaseAllocation) {
        releaseAllocation(payload);
        respond(connection);
        running = releasedAllocations != 2;
      } else if (operationTag == operation::WriteAllocation) {
        writeAllocation(payload);
        respond(connection);
      } else if (operationTag == operation::ReadAllocation) {
        readAllocation(connection, payload);
      } else if (operationTag == operation::LoadProgram) {
        loadProgram(connection, payload);
      } else if (operationTag == operation::UnloadProgram) {
        const auto request = gemstone_test::decodePayload<UnloadProgram>(payload);
        REQUIRE((request.program.value == static_cast<int64_t>(programId)));
        respond(connection);
      } else if (operationTag == operation::DescribeKernel) {
        describeKernel(connection, payload);
      } else if (operationTag == operation::Launch) {
        launch(connection, payload);
      } else if (operationTag == operation::QueryCompletion) {
        queryCompletion(connection, payload);
      } else if (operationTag == operation::AwaitCompletion) {
        awaitCompletion(connection, payload);
      } else if (operationTag == operation::PollHostMemory) {
        awaitCompletion(connection, payload, true);
      } else if (operationTag == operation::ReleaseCompletion) {
        const auto request = gemstone_test::decodePayload<ReleaseCompletion>(payload);
        REQUIRE((request.completion.value == static_cast<int64_t>(completionId)));
        respond(connection);
      } else if (operationTag == operation::CompletionDirtyRanges) {
        dirtyRanges(connection, payload);
      } else if (operationTag == operation::CompletionPointerRelocations) {
        const auto request = gemstone_test::decodePayload<CompletionPointerRelocations>(payload);
        REQUIRE((request.completion.value == static_cast<int64_t>(completionId)));
        std::vector<uint8_t> response;
        gemstone_test::encodePayload(response, std::vector<PointerRelocation>{});
        respond(connection, response);
      } else if (operationTag == operation::CompletionPrints) {
        const auto request = gemstone_test::decodePayload<CompletionPrints>(payload);
        REQUIRE((request.completion.value == static_cast<int64_t>(completionId)));
        std::vector<uint8_t> response;
        gemstone_test::encodePayload(response, std::string{});
        respond(connection, response);
      } else {
        REQUIRE((false));
      }
    }
    for (size_t i = 1; i < peers.size(); ++i)
      close(peers[i].fd);
  }

  void registerAllocation(int connection, const std::vector<uint8_t> &payload) {
    const auto request = gemstone_test::decodePayload<RegisterAllocation>(payload);
    REQUIRE((request.kind == AllocationKind::Device));
    const auto id = nextAllocationId++;
    if (allocationId == 0) {
      allocationId = id;
      allocation.resize(static_cast<size_t>(request.size.value));
    } else {
      REQUIRE((unrelatedAllocationId == 0));
      unrelatedAllocationId = id;
    }
    std::vector<uint8_t> response;
    gemstone_test::encodePayload(response, AllocationId{static_cast<int64_t>(id)});
    respond(connection, response);
  }

  void releaseAllocation(const std::vector<uint8_t> &payload) {
    const auto request = gemstone_test::decodePayload<ReleaseAllocation>(payload);
    const auto id = static_cast<uint64_t>(request.allocation.value);
    REQUIRE((id == allocationId || id == unrelatedAllocationId));
    ++releasedAllocations;
  }

  void writeAllocation(const std::vector<uint8_t> &payload) {
    // The unrelated allocation is live but unreachable from this non-indirect
    // kernel, so it must not be mirrored for the launch.
    const auto request = gemstone_test::decodePayload<WriteAllocation>(payload);
    REQUIRE((static_cast<uint64_t>(request.allocation.value) == allocationId));
    const auto offset = static_cast<uint64_t>(request.offset.value);
    const auto size = request.bytes.size();
    REQUIRE((offset + size <= allocation.size()));
    std::memcpy(allocation.data() + offset, request.bytes.data(), size);
    writeRequests.fetch_add(1, std::memory_order_relaxed);
  }

  void readAllocation(int connection, const std::vector<uint8_t> &payload) {
    const auto request = gemstone_test::decodePayload<ReadAllocation>(payload);
    REQUIRE((static_cast<uint64_t>(request.allocation.value) == allocationId));
    const auto offset = static_cast<uint64_t>(request.offset.value);
    const auto size = static_cast<size_t>(request.size.value);
    REQUIRE((offset == 4));
    REQUIRE((size == sizeof(uint32_t)));
    REQUIRE((offset + size <= allocation.size()));
    std::vector<uint8_t> response;
    gemstone_test::encodePayload(response, std::vector<uint8_t>(allocation.begin() + offset, allocation.begin() + offset + size));
    respond(connection, response);
  }

  void dirtyRanges(int connection, const std::vector<uint8_t> &payload) {
    gemstone_test::decodePayload<CompletionDirtyRanges>(payload);
    std::vector<uint8_t> response;
    gemstone_test::encodePayload(
        response, std::vector<DirtyRange>{DirtyRange{AllocationId{static_cast<int64_t>(allocationId)}, LongPositive0{4}, IntPositive{4}}});
    respond(connection, response);
  }

  void loadProgram(int connection, const std::vector<uint8_t> &payload) {
    const auto request = gemstone_test::decodePayload<LoadProgram>(payload);
    REQUIRE((request.encoding == "spirv-binary"));
    REQUIRE((request.image.size() >= 4));
    REQUIRE((request.image[0] == 3 && request.image[1] == 2 && request.image[2] == 35 && request.image[3] == 7));
    std::vector<uint8_t> response;
    gemstone_test::encodePayload(response, ProgramId{static_cast<int64_t>(programId)});
    respond(connection, response);
  }

  void describeKernel(int connection, const std::vector<uint8_t> &payload) {
    const auto request = gemstone_test::decodePayload<DescribeKernel>(payload);
    REQUIRE((request.program.value == static_cast<int64_t>(programId)));
    if (request.name.value != "write") {
      std::vector<uint8_t> failure;
      gemstone_test::encodePayload(failure, ExecutionFailure{ExecutionFailureNotFound{"unknown kernel"}});
      respond(connection, failure, true);
      return;
    }
    KernelMetadata metadata{};
    metadata.argumentBlockSize = IntPositive0{28};
    metadata.explicitParameterCount = IntPositive0{4};
    for (const std::array<int32_t, 4> parameter : {std::array<int32_t, 4>{8, 0, 2, 0}, std::array<int32_t, 4>{4, 8, 1, 0},
                                                   std::array<int32_t, 4>{8, 16, 3, 0}, std::array<int32_t, 4>{4, 24, 1, 0}}) {
      Parameter entry{};
      entry.width = IntPositive{parameter[0]};
      entry.offset = IntPositive0{parameter[1]};
      entry.kind = parameter[2] == 1 ? ParameterKind::Scalar : parameter[2] == 2 ? ParameterKind::Allocation : ParameterKind::Local;
      entry.containsPointers = parameter[3] != 0;
      metadata.parameters.push_back(entry);
    }
    metadata.entryPointId = IntPositive0{6};
    std::vector<uint8_t> response;
    gemstone_test::encodePayload(response, metadata);
    respond(connection, response);
  }

  void launch(int connection, const std::vector<uint8_t> &payload) {
    const auto request = gemstone_test::decodePayload<Launch>(payload);
    REQUIRE((request.program.value == static_cast<int64_t>(programId)));
    REQUIRE((request.kernelName.value == "write"));
    REQUIRE((request.groupSize.x.value == 4 && request.groupSize.y.value == 1 && request.groupSize.z.value == 1));
    REQUIRE((request.groupCount.x.value == 2 && request.groupCount.y.value == 1 && request.groupCount.z.value == 1));
    REQUIRE((request.globalSize.x.value == 8 && request.globalSize.y.value == 1 && request.globalSize.z.value == 1));
    REQUIRE((request.workDim.value == 3));
    REQUIRE((request.globalOffset.x.value == 0 && request.globalOffset.y.value == 0 && request.globalOffset.z.value == 0));
    REQUIRE((request.privateSegmentBytes.value == 0 && request.groupSegmentBytes.value == 0));
    REQUIRE((request.arguments.size() == 4));
    const auto *memory = std::get_if<ArgumentMemory>(&request.arguments[0]);
    REQUIRE((memory != nullptr && static_cast<uint64_t>(memory->region.value) == allocationId));
    REQUIRE((memory->offset.value == 4));
    const auto *seed = std::get_if<ArgumentScalar>(&request.arguments[1]);
    REQUIRE((seed != nullptr && seed->bytes.size() == 4));
    const auto *local = std::get_if<ArgumentLocal>(&request.arguments[2]);
    REQUIRE((local != nullptr && local->size.value == 64));
    const auto *tail = std::get_if<ArgumentScalar>(&request.arguments[3]);
    REQUIRE((tail != nullptr && tail->bytes.size() == sizeof(uint32_t)));
    REQUIRE((request.pointerRelocations.empty()));
    REQUIRE((!request.discoverArgumentRelocations));
    lastLaunchByte8 = allocation[8];
    std::memcpy(allocation.data() + 4, seed->bytes.data(), sizeof(uint32_t));
    std::vector<uint8_t> response;
    gemstone_test::encodePayload(response, CompletionId{static_cast<int64_t>(completionId)});
    respond(connection, response);
  }

  void queryCompletion(int connection, const std::vector<uint8_t> &payload) {
    const auto request = gemstone_test::decodePayload<QueryCompletion>(payload);
    REQUIRE((request.completion.value == static_cast<int64_t>(completionId)));
    std::vector<uint8_t> response;
    gemstone_test::encodePayload(response, CompletionStatus{CompletionStatusSucceeded{}});
    respond(connection, response);
  }

  void awaitCompletion(int connection, const std::vector<uint8_t> &payload, bool hostMemory = false) {
    const auto completion = hostMemory ? gemstone_test::decodePayload<PollHostMemory>(payload).completion
                                       : gemstone_test::decodePayload<AwaitCompletion>(payload).completion;
    REQUIRE((completion.value == static_cast<int64_t>(completionId)));
    completionEntered = true;
    while (pauseCompletion.load())
      std::this_thread::yield();
    if (failNextCompletion.exchange(false)) {
      std::vector<uint8_t> failure;
      gemstone_test::encodePayload(failure, ExecutionFailure{ExecutionFailureFailed{"completion failed"}});
      respond(connection, failure, true);
      return;
    }
    if (hostMemory) {
      HostMemoryBatch batch{};
      batch.completed = true;
      std::vector<uint8_t> response;
      gemstone_test::encodePayload(response, batch);
      respond(connection, response);
    } else respond(connection);
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
  const auto port = "tcp://127.0.0.1:" + std::to_string(server.port());
  REQUIRE((setenv(gemstone::environment::RpcEndpoint, port.c_str(), 1) == 0));
  REQUIRE((setenv(gemstone::environment::RpcClient, "42", 1) == 0));
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
          REQUIRE(((offset == 0 ? setGlobalOffset(kernel, offset, 0, 0) : zeKernelSetGlobalOffsetExp(kernel, offset, 0, 0))
                   == ZE_RESULT_SUCCESS));
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
    for (uint32_t index = 0; index < 4; ++index)
      REQUIRE((ordinary[index] == replaySeed + index));
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
  const auto port = "tcp://127.0.0.1:" + std::to_string(server.port);
  REQUIRE((setenv(gemstone::environment::RpcEndpoint, port.c_str(), 1) == 0));
  REQUIRE((setenv(gemstone::environment::RpcClient, "42", 1) == 0));
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
  ze_command_list_handle_t copyList = nullptr;
  REQUIRE(zeCommandListCreateImmediate(context, device, &queueDesc, &copyList) == ZE_RESULT_SUCCESS);
  server.pauseCompletion = true;
  ze_result_t launchStatus = ZE_RESULT_NOT_READY;
  std::thread launchThread([&] { launchStatus = zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr); });
  const auto deadline = std::chrono::steady_clock::now() + std::chrono::seconds(5);
  while (!server.completionEntered.load() && std::chrono::steady_clock::now() < deadline)
    std::this_thread::yield();
  const bool entered = server.completionEntered.load();
  const uint8_t concurrentByte = 0x5a;
  // A legal disjoint copy after upload, but before the kernel's four-byte
  // download, must still be visible to the next kernel on this allocation.
  const auto copyStatus =
      entered ? zeCommandListAppendMemoryCopy(copyList, static_cast<uint8_t *>(memory) + 8, &concurrentByte, 1, nullptr, 0, nullptr)
              : ZE_RESULT_NOT_READY;
  server.pauseCompletion = false;
  launchThread.join();
  REQUIRE(zeCommandListDestroy(copyList) == ZE_RESULT_SUCCESS);
  REQUIRE(entered);
  REQUIRE(copyStatus == ZE_RESULT_SUCCESS);
  REQUIRE(launchStatus == ZE_RESULT_SUCCESS);
  REQUIRE((*reinterpret_cast<uint32_t *>(static_cast<uint8_t *>(memory) + 4) == expected));
  REQUIRE((server.writeRequests.load(std::memory_order_relaxed) == 1));
  // The next upload must preserve the disjoint host copy alongside the kernel result.
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
  CHECK((server.writeRequests.load(std::memory_order_relaxed) == 2));
  CHECK(server.lastLaunchByte8.load() == concurrentByte);
  static_cast<uint8_t *>(memory)[8] = 0x6b;
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
  CHECK((server.writeRequests.load(std::memory_order_relaxed) == 3));
  CHECK(server.lastLaunchByte8.load() == 0x6b);

  ze_fence_desc_t fenceDesc{};
  fenceDesc.stype = ZE_STRUCTURE_TYPE_FENCE_DESC;
  ze_fence_handle_t failedFence = nullptr;
  REQUIRE((zeFenceCreate(queue, &fenceDesc, &failedFence) == ZE_RESULT_SUCCESS));
  server.failNextCompletion.store(true);
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, failedFence) == ZE_RESULT_ERROR_DEVICE_LOST));
  REQUIRE((zeFenceHostSynchronize(failedFence, 1'000'000'000ULL) == ZE_RESULT_ERROR_DEVICE_LOST));
  REQUIRE((zeFenceQueryStatus(failedFence) == ZE_RESULT_ERROR_DEVICE_LOST));
  REQUIRE((zeFenceDestroy(failedFence) == ZE_RESULT_SUCCESS));

  REQUIRE((zeKernelSetGroupSize(kernel, 4, 1, 1) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetArgumentValue(kernel, 1, sizeof(expected), &expected) == ZE_RESULT_SUCCESS));
  // A failed asynchronous launch must complete its event with the error and
  // unblock dependent copies, including events on cancelled later commands.
  for (const unsigned mode : {0U, 1U, 2U, 3U}) {
    CAPTURE(mode);
    const bool immediate = mode == 1;
    ze_event_pool_desc_t poolDesc{};
    poolDesc.stype = ZE_STRUCTURE_TYPE_EVENT_POOL_DESC;
    poolDesc.flags = ZE_EVENT_POOL_FLAG_HOST_VISIBLE | ZE_EVENT_POOL_FLAG_KERNEL_TIMESTAMP;
    poolDesc.count = 3;
    ze_event_pool_handle_t pool = nullptr;
    REQUIRE((zeEventPoolCreate(context, &poolDesc, 0, nullptr, &pool) == ZE_RESULT_SUCCESS));
    std::array<ze_event_handle_t, 3> events{};
    for (uint32_t index = 0; index < events.size(); ++index) {
      ze_event_desc_t desc{};
      desc.stype = ZE_STRUCTURE_TYPE_EVENT_DESC;
      desc.index = index;
      desc.signal = ZE_EVENT_SCOPE_FLAG_HOST;
      desc.wait = ZE_EVENT_SCOPE_FLAG_HOST;
      REQUIRE((zeEventCreate(pool, &desc, &events[index]) == ZE_RESULT_SUCCESS));
    }
    ze_command_queue_desc_t asyncDesc{};
    asyncDesc.stype = ZE_STRUCTURE_TYPE_COMMAND_QUEUE_DESC;
    asyncDesc.mode = ZE_COMMAND_QUEUE_MODE_ASYNCHRONOUS;
    ze_command_queue_handle_t producerQueue = nullptr, consumerQueue = nullptr;
    REQUIRE((zeCommandQueueCreate(context, device, &asyncDesc, &producerQueue) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandQueueCreate(context, device, &asyncDesc, &consumerQueue) == ZE_RESULT_SUCCESS));
    ze_command_list_handle_t producer = nullptr, consumer = nullptr, outer = nullptr;
    if (mode >= 2) REQUIRE((zeCommandListCreateImmediate(context, device, &asyncDesc, &outer) == ZE_RESULT_SUCCESS));
    if (immediate) REQUIRE((zeCommandListCreateImmediate(context, device, &asyncDesc, &producer) == ZE_RESULT_SUCCESS));
    else REQUIRE((zeCommandListCreate(context, device, &listDesc, &producer) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandListCreate(context, device, &listDesc, &consumer) == ZE_RESULT_SUCCESS));
    const uint32_t source = 0xabcdef01;
    uint32_t cancelled = 0, destination = 0;
    REQUIRE(
        (zeCommandListAppendMemoryCopy(consumer, &destination, &source, sizeof(source), events[2], 1, &events[1]) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandListClose(consumer) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandQueueExecuteCommandLists(consumerQueue, 1, &consumer, nullptr) == ZE_RESULT_SUCCESS));
    server.failNextCompletion.store(true);
    REQUIRE((zeCommandListAppendLaunchKernel(mode == 3 ? outer : producer, kernel, &groups, events[0], 0, nullptr) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandListAppendMemoryCopy(producer, &cancelled, &source, sizeof(source), events[1], 0, nullptr) == ZE_RESULT_SUCCESS));
    if (!immediate) {
      REQUIRE((zeCommandListClose(producer) == ZE_RESULT_SUCCESS));
      if (mode == 0) REQUIRE((zeCommandQueueExecuteCommandLists(producerQueue, 1, &producer, nullptr) == ZE_RESULT_SUCCESS));
      else REQUIRE((zeCommandListImmediateAppendCommandListsExp(outer, 1, &producer, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
    }
    std::array<ze_result_t, 3> statuses{};
    for (size_t index = 0; index < events.size(); ++index) {
      statuses[index] = zeEventHostSynchronize(events[index], 1'000'000'000ULL);
      // Unblock the old implementation before reporting failures so the red
      // regression can always clean up its worker threads.
      if (statuses[index] == ZE_RESULT_NOT_READY) REQUIRE((zeEventHostSignal(events[index]) == ZE_RESULT_SUCCESS));
    }
    CHECK((zeCommandQueueSynchronize(consumerQueue, 1'000'000'000ULL) == ZE_RESULT_ERROR_DEVICE_LOST));
    for (size_t index = 0; index < events.size(); ++index) {
      CAPTURE(index);
      CHECK((statuses[index] == ZE_RESULT_ERROR_DEVICE_LOST));
      CHECK((zeEventQueryStatus(events[index]) == ZE_RESULT_ERROR_DEVICE_LOST));
      ze_kernel_timestamp_result_t timestamp{};
      CHECK((zeEventQueryKernelTimestamp(events[index], &timestamp) == ZE_RESULT_ERROR_DEVICE_LOST));
    }
    CHECK((cancelled == 0));
    CHECK((destination == 0));
    if (mode == 0) CHECK((zeCommandQueueSynchronize(producerQueue, 1'000'000'000ULL) == ZE_RESULT_ERROR_DEVICE_LOST));
    if (outer) REQUIRE((zeCommandListDestroy(outer) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandListDestroy(producer) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandListDestroy(consumer) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandQueueDestroy(producerQueue) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandQueueDestroy(consumerQueue) == ZE_RESULT_SUCCESS));
    for (const auto event : events) {
      REQUIRE((zeEventHostReset(event) == ZE_RESULT_SUCCESS));
      CHECK((zeEventQueryStatus(event) == ZE_RESULT_NOT_READY));
      REQUIRE((zeEventHostSignal(event) == ZE_RESULT_SUCCESS));
      CHECK((zeEventHostSynchronize(event, 0) == ZE_RESULT_SUCCESS));
      REQUIRE((zeEventDestroy(event) == ZE_RESULT_SUCCESS));
    }
    REQUIRE((zeEventPoolDestroy(pool) == ZE_RESULT_SUCCESS));
  }

  REQUIRE((zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS));
  REQUIRE((zeModuleDestroy(module) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeMemFree(context, memory) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
}

TEST_CASE("Level Zero cross-workgroup API", "[level-zero][rpc]") { crossWorkgroupApi(); }

namespace {
void ordinaryHostPointer(unsigned storageKind, bool primeBase, bool requiredGroupSize = false) {
  gemstone_test::RpcFixture server;
  const auto port = "tcp://127.0.0.1:" + std::to_string(server.port());
  REQUIRE((setenv(gemstone::environment::RpcEndpoint, port.c_str(), 1) == 0));
  REQUIRE((setenv(gemstone::environment::RpcClient, "42", 1) == 0));
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
  // The Scala backend describes work-group requirements through the shared RPC metadata.
  if (requiredGroupSize) server.kernelWorkGroup({4, 1, 1});
  moduleDesc.inputSize = sizeof(server.spirv);
  moduleDesc.pInputModule = reinterpret_cast<const uint8_t *>(server.spirv);
  ze_module_handle_t module = nullptr;
  REQUIRE((zeModuleCreate(context, device, &moduleDesc, &module, nullptr) == ZE_RESULT_SUCCESS));
  ze_kernel_desc_t kernelDesc{};
  kernelDesc.stype = ZE_STRUCTURE_TYPE_KERNEL_DESC;
  kernelDesc.pKernelName = "groups";
  ze_kernel_handle_t kernel = nullptr;
  REQUIRE((zeKernelCreate(module, &kernelDesc, &kernel) == ZE_RESULT_SUCCESS));

  std::array<uint32_t, 16> stack{};
  static std::array<uint32_t, 16> global{};
  std::unique_ptr<uint32_t, decltype(&std::free)> heap(nullptr, &std::free);
  if (storageKind == 0 || storageKind == 3) heap.reset(static_cast<uint32_t *>(std::malloc(storageKind == 3 ? 2 * 1024 * 1024 : 64)));
  auto *base = storageKind == 1 ? stack.data() : storageKind == 2 ? global.data() : heap.get();
  REQUIRE((base));
  std::fill(base, base + 16, 0U);
  auto *interior = base + 1;
  const uint32_t seed = 29;
  REQUIRE((zeKernelSetArgumentValue(kernel, 0, sizeof(base), &base) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelSetArgumentValue(kernel, 1, sizeof(seed), &seed) == ZE_RESULT_SUCCESS));
  if (!requiredGroupSize) REQUIRE((zeKernelSetGroupSize(kernel, 4, 1, 1) == ZE_RESULT_SUCCESS));

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
  if (primeBase) {
    REQUIRE((zeCommandListAppendLaunchKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
    REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));
    server.expect({4, 1, 4, 0, seed});
    REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
    for (uint32_t index = 0; index < 4; ++index)
      REQUIRE((base[index] == seed + index));
    REQUIRE((zeCommandListReset(list) == ZE_RESULT_SUCCESS));
  }

  // Interior pointers must work even when no earlier launch supplied the base.
  std::fill(base, base + 16, 0U);
  REQUIRE((zeKernelSetArgumentValue(kernel, 0, sizeof(interior), &interior) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListAppendLaunchKernel(list, kernel, &groups, nullptr, 0, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandListClose(list) == ZE_RESULT_SUCCESS));
  server.expect({4, 1, 4, 0, seed});
  REQUIRE((zeCommandQueueExecuteCommandLists(queue, 1, &list, nullptr) == ZE_RESULT_SUCCESS));
  REQUIRE((base[0] == 0));
  for (uint32_t index = 0; index < 4; ++index)
    REQUIRE((interior[index] == seed + index));

  REQUIRE((zeCommandListDestroy(list) == ZE_RESULT_SUCCESS));
  REQUIRE((zeCommandQueueDestroy(queue) == ZE_RESULT_SUCCESS));
  REQUIRE((zeKernelDestroy(kernel) == ZE_RESULT_SUCCESS));
  REQUIRE((zeModuleDestroy(module) == ZE_RESULT_SUCCESS));
  REQUIRE((zeContextDestroy(context) == ZE_RESULT_SUCCESS));
  REQUIRE((server.launches() == (primeBase ? 2U : 1U)));
  server.check();
}
} // namespace

TEST_CASE("Level Zero kernel accepts an interior ordinary host pointer", "[level-zero][rpc][unified-memory]") {
  ordinaryHostPointer(0, true);
}

TEST_CASE("Level Zero kernel defaults to its required group size", "[level-zero][rpc]") { ordinaryHostPointer(0, false, true); }

TEST_CASE("Level Zero first launch accepts an interior heap pointer", "[level-zero][rpc][unified-memory]") {
  ordinaryHostPointer(0, false);
}

TEST_CASE("Level Zero first launch accepts an interior stack pointer", "[level-zero][rpc][unified-memory]") {
  ordinaryHostPointer(1, false);
}

TEST_CASE("Level Zero first launch accepts an interior static pointer", "[level-zero][rpc][unified-memory]") {
  ordinaryHostPointer(2, false);
}

TEST_CASE("Level Zero first launch accepts an interior large heap pointer", "[level-zero][rpc][unified-memory]") {
  ordinaryHostPointer(3, false);
}
