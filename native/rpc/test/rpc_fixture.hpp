#pragma once

#include <algorithm>
#include <array>
#include <atomic>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <exception>
#include <map>
#include <mutex>
#include <optional>
#include <set>
#include <stdexcept>
#include <string>
#include <thread>
#include <vector>

#include <stdint.h>
#include <unistd.h>

#include <gemstone/test_support.hpp>

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include "wire.hpp"
#include <arpa/inet.h>
#include <environment_keys.hpp>
#include <netinet/tcp.h>
#include <sys/socket.h>
#include <sys/un.h>

#define GEMSTONE_RPC_REQUIRE(condition)                                                                                                    \
  do {                                                                                                                                     \
    if (!(condition)) throw std::runtime_error(std::string(__FILE__) + ":" + std::to_string(__LINE__) + ": " + #condition);                \
  } while (false)

namespace gemstone_test {

using namespace gemstone::execution;

struct FixtureOptions : glz::opts {
  static constexpr auto format = glz::MSGPACK;
  bool structs_as_arrays = true;
};

template <class T> T decodePayload(const std::vector<uint8_t> &payload) {
  T value{};
  const std::string_view view(reinterpret_cast<const char *>(payload.data()), payload.size());
  GEMSTONE_RPC_REQUIRE(!glz::read<FixtureOptions{}>(value, view));
  return value;
}

template <class T> void encodePayload(std::vector<uint8_t> &out, const T &value) {
  std::string buffer;
  GEMSTONE_RPC_REQUIRE(!glz::write<FixtureOptions{}>(value, buffer));
  out.assign(buffer.begin(), buffer.end());
}

// Real native API/transport test peer. Kernel execution is deliberately mocked:
// validate launch geometry, publish one range per group, and complete when awaited.
class RpcFixture {
public:
  inline static constexpr uint64_t ClientId = 42;
  struct Expected {
    uint32_t local, groups, global, offset, seed;
    std::array<uint32_t, 2> localYz{1, 1}, groupsYz{1, 1}, globalYz{1, 1};
    // Boundary-sized geometry can be inspected without mocking billions of output elements.
    bool geometryOnly = false;
  };
  struct KernelParameter {
    uint32_t width;
    uint32_t offset;
    uint32_t kind;
    uint32_t containsPointers;
  };
  struct ProgramKernel {
    std::string name;
    std::string linkageName;
  };
  struct ProgramGlobal {
    std::string name;
    uint64_t virtualAddress;
    uint64_t size;
    std::vector<uint32_t> pointerOffsets;
  };
  struct ExpectedAllocationArgument {
    size_t alias;
    uint64_t offset;
    std::optional<uint64_t> range = std::nullopt;
  };
  std::atomic<unsigned> launches{0}, reads{0}, programLoads{0}, programUnloads{0};
  std::atomic_bool completionEntered{false}, releaseEntered{false};

  explicit RpcFixture(bool unixSocket = false) {
    if (unixSocket) {
      char directory[] = "/tmp/gemstone-rpc-XXXXXX";
      GEMSTONE_RPC_REQUIRE(mkdtemp(directory) != nullptr);
      socketDirectory = directory;
      socketPath = socketDirectory + "/socket with space";
      sockaddr_un address{};
      address.sun_family = AF_UNIX;
      GEMSTONE_RPC_REQUIRE(socketPath.size() < sizeof(address.sun_path));
      std::memcpy(address.sun_path, socketPath.c_str(), socketPath.size() + 1);
      listener = socket(AF_UNIX, SOCK_STREAM, 0);
      GEMSTONE_RPC_REQUIRE(listener >= 0);
      GEMSTONE_RPC_REQUIRE(bind(listener, reinterpret_cast<sockaddr *>(&address), sizeof(address)) == 0);
    } else {
      listener = socket(AF_INET, SOCK_STREAM, 0);
      GEMSTONE_RPC_REQUIRE(listener >= 0);
      sockaddr_in address{};
      address.sin_family = AF_INET;
      address.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
      GEMSTONE_RPC_REQUIRE(bind(listener, reinterpret_cast<sockaddr *>(&address), sizeof(address)) == 0);
      socklen_t length = sizeof(address);
      GEMSTONE_RPC_REQUIRE(getsockname(listener, reinterpret_cast<sockaddr *>(&address), &length) == 0);
      port = ntohs(address.sin_port);
    }
    GEMSTONE_RPC_REQUIRE(listen(listener, 8) == 0);
    acceptor = std::thread([this] {
      try {
        while (!stopped.load()) {
          const int connection = accept(listener, nullptr, nullptr);
          if (connection < 0) {
            GEMSTONE_RPC_REQUIRE(stopped.load());
            break;
          }
          const int enabled = 1;
          if (socketPath.empty()) GEMSTONE_RPC_REQUIRE(setsockopt(connection, IPPROTO_TCP, TCP_NODELAY, &enabled, sizeof(enabled)) == 0);
          std::lock_guard lock(connectionsMutex);
          connections.push_back(connection);
          workers.emplace_back([this, connection] {
            try {
              serve(connection);
            } catch (...) {
              recordFailure(std::current_exception());
            }
            std::lock_guard guard(connectionsMutex);
            connections.erase(std::remove(connections.begin(), connections.end(), connection), connections.end());
            close(connection);
          });
        }
      } catch (...) {
        recordFailure(std::current_exception());
      }
    });
  }
  ~RpcFixture() {
    stopped.store(true);
    shutdown(listener, SHUT_RDWR);
    close(listener);
    acceptor.join();
    {
      std::lock_guard lock(connectionsMutex);
      for (int connection : connections)
        shutdown(connection, SHUT_RDWR);
    }
    for (auto &worker : workers)
      worker.join();
    if (!socketPath.empty()) {
      unlink(socketPath.c_str());
      rmdir(socketDirectory.c_str());
    }
  }
  std::string endpoint() const {
    if (socketPath.empty()) return "tcp://127.0.0.1:" + std::to_string(port);
    std::string encoded = "unix://";
    for (char ch : socketPath)
      encoded += ch == ' ' ? "%20" : std::string(1, ch);
    return encoded;
  }
  bool hostMemory() {
    std::lock_guard lock(stateMutex);
    return hostMemoryEnabled;
  }
  void expect(Expected expected) {
    std::lock_guard lock(stateMutex);
    GEMSTONE_RPC_REQUIRE(!active);
    expectation = expected;
    trailingArguments.reset();
    indirectInput.reset();
    indirectWrite.reset();
  }
  void expectTrailingArguments(std::vector<uint8_t> bytes) {
    std::lock_guard lock(stateMutex);
    trailingArguments = std::move(bytes);
  }
  void expectAllocationArguments(std::vector<ExpectedAllocationArgument> arguments) {
    std::lock_guard lock(stateMutex);
    allocationArguments = std::move(arguments);
  }
  void writeAllocationArgument(size_t argument, uint64_t offset, std::vector<uint8_t> bytes) {
    std::lock_guard lock(stateMutex);
    allocationArgumentWrite = AllocationArgumentWrite{argument, offset, std::move(bytes)};
  }
  void kernelMetadata(uint32_t argumentBlockSize, uint32_t explicitParameterCount, std::vector<KernelParameter> parameters) {
    std::lock_guard lock(stateMutex);
    GEMSTONE_RPC_REQUIRE(explicitParameterCount <= parameters.size());
    kernelArgumentBlockSize = argumentBlockSize;
    kernelExplicitParameterCount = explicitParameterCount;
    kernelParameters = std::move(parameters);
  }
  void programCatalogue(std::vector<ProgramKernel> kernels, std::vector<ProgramGlobal> globals) {
    std::lock_guard lock(stateMutex);
    programKernels = std::move(kernels);
    programGlobals = std::move(globals);
  }
  void kernelWorkGroup(std::array<uint32_t, 3> size) {
    std::lock_guard lock(stateMutex);
    requiredWorkGroupSize = size;
  }
  void kernelBindings(std::vector<uint32_t> bindings) {
    std::lock_guard lock(stateMutex);
    bindingIndices = std::move(bindings);
  }
  void shaderDescriptors(std::vector<ShaderDescriptor> descriptors) {
    std::lock_guard lock(stateMutex);
    preparedDescriptors = std::move(descriptors);
  }
  void specializationTable(std::map<uint32_t, uint32_t> sizes) {
    std::lock_guard lock(stateMutex);
    specializationSizes = std::move(sizes);
  }
  std::vector<std::pair<uint32_t, std::vector<uint8_t>>> lastSpecialization() {
    std::lock_guard lock(stateMutex);
    return specialization;
  }
  void expectGlobalBinding(std::string name, uint64_t offset, std::vector<uint8_t> initial) {
    std::lock_guard lock(stateMutex);
    expectedGlobals.emplace(std::move(name), std::make_pair(offset, std::move(initial)));
  }
  size_t allocationCount() {
    std::lock_guard lock(stateMutex);
    return allocations.size();
  }
  void checkAllocationAddress(const void *pointer, bool present = true) {
    std::lock_guard lock(stateMutex);
    const auto address = reinterpret_cast<uint64_t>(pointer);
    const auto found =
        std::find_if(allocationAddresses.begin(), allocationAddresses.end(), [&](const auto &entry) { return entry.second == address; });
    GEMSTONE_RPC_REQUIRE((found != allocationAddresses.end()) == present);
  }
  void checkAllocationContainingAddress(const void *pointer, bool present = true) {
    std::lock_guard lock(stateMutex);
    const auto address = reinterpret_cast<uint64_t>(pointer);
    const auto found = std::find_if(allocationAddresses.begin(), allocationAddresses.end(), [&](const auto &entry) {
      const auto size = allocations.at(entry.first).size();
      return address >= entry.second && address - entry.second < size;
    });
    GEMSTONE_RPC_REQUIRE((found != allocationAddresses.end()) == present);
  }
  void pauseReleaseContaining(const void *pointer) {
    releaseEntered = false;
    pauseReleaseAddress = reinterpret_cast<uint64_t>(pointer);
  }
  void releasePausedRelease() { pauseReleaseAddress = 0; }
  void checkHostBacked(const void *pointer) {
    std::lock_guard lock(stateMutex);
    for (const auto &[id, base] : allocationAddresses)
      if (base == reinterpret_cast<uintptr_t>(pointer)) {
        GEMSTONE_RPC_REQUIRE(hostBacked.at(id));
        return;
      }
    GEMSTONE_RPC_REQUIRE(false);
  }
  void checkAllocationWord(const void *base, size_t offset, uint64_t expected) {
    std::lock_guard lock(stateMutex);
    const auto found = std::find_if(allocationAddresses.begin(), allocationAddresses.end(),
                                    [&](const auto &entry) { return entry.second == reinterpret_cast<uint64_t>(base); });
    GEMSTONE_RPC_REQUIRE(found != allocationAddresses.end());
    const auto &bytes = allocations.at(found->first);
    GEMSTONE_RPC_REQUIRE(offset <= bytes.size() && bytes.size() - offset >= sizeof(uint64_t));
    uint64_t actual = 0;
    std::memcpy(&actual, hostBacked[found->first] ? static_cast<const uint8_t *>(base) + offset : bytes.data() + offset, sizeof(actual));
    GEMSTONE_RPC_REQUIRE(actual == expected);
  }
  void expectIndirectInput(const void *table, uint32_t value, std::optional<uint32_t> deviceWrite = std::nullopt) {
    std::lock_guard lock(stateMutex);
    indirectInput = IndirectInput{reinterpret_cast<uint64_t>(table), value, deviceWrite};
  }
  uint64_t writtenBytes(const void *address) {
    std::lock_guard lock(stateMutex);
    for (const auto &[id, base] : allocationAddresses)
      if (base == reinterpret_cast<uint64_t>(address)) return allocationWrites[id];
    GEMSTONE_RPC_REQUIRE(false);
    return 0;
  }
  void checkGlobalBindings() {
    std::lock_guard lock(stateMutex);
    GEMSTONE_RPC_REQUIRE(expectedGlobals.empty());
  }
  void check() {
    rethrowFailure();
    std::lock_guard lock(stateMutex);
    GEMSTONE_RPC_REQUIRE(!active && launches > 0 && awaited == launches);
  }
  void failNextGraphicsStatistics() { failGraphicsStatistics = true; }
  // The next request with this operation tag fails without changing fixture state.
  void rejectNext(std::string_view operation) {
    std::lock_guard lock(stateMutex);
    rejectedOperation = operation;
  }
  void failNextCompletion() { failCompletion = true; }
  void pauseNextCompletion() {
    completionEntered = false;
    pauseCompletion = true;
  }
  void releasePausedCompletion() { pauseCompletion = false; }
  // Launches are recorded without interpreting their arguments and complete with `dirty`.
  void opaqueLaunches(std::vector<DirtyRange> dirty = {}) {
    std::lock_guard lock(stateMutex);
    opaqueDirty = std::move(dirty);
  }
  Launch lastLaunch() {
    std::lock_guard lock(stateMutex);
    return recordedLaunch;
  }
  std::vector<uint8_t> lastProgramImage() {
    std::lock_guard lock(stateMutex);
    return programImage;
  }

  inline static constexpr uint32_t Spirv[] = {0x07230203,
                                              0x00010600,
                                              0,
                                              10,
                                              0,
                                              (2U << 16) | 17U,
                                              6,
                                              (3U << 16) | 14U,
                                              2,
                                              2,
                                              (5U << 16) | 15U,
                                              6,
                                              5,
                                              0x756f7267,
                                              0x00007370,
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
                                              (5U << 16) | 33U,
                                              4,
                                              1,
                                              3,
                                              2,
                                              (5U << 16) | 54U,
                                              1,
                                              5,
                                              0,
                                              4,
                                              (3U << 16) | 55U,
                                              3,
                                              6,
                                              (3U << 16) | 55U,
                                              2,
                                              7,
                                              (2U << 16) | 248U,
                                              8,
                                              (1U << 16) | 253U,
                                              (1U << 16) | 56U};

private:
  void recordFailure(std::exception_ptr failure) noexcept {
    std::lock_guard lock(failureMutex);
    if (!firstFailure) firstFailure = std::move(failure);
  }
  void rethrowFailure() const {
    std::exception_ptr failure;
    {
      std::lock_guard lock(failureMutex);
      failure = firstFailure;
    }
    if (failure) std::rethrow_exception(failure);
  }

  static ParameterKind parameterKind(uint32_t value) {
    if (value == 1) return ParameterKind::Scalar;
    if (value == 2) return ParameterKind::Allocation;
    if (value == 3) return ParameterKind::Local;
    GEMSTONE_RPC_REQUIRE(false);
    return ParameterKind::Scalar;
  }
  void appendKernelMetadata(std::vector<uint8_t> &result) {
    KernelMetadata metadata{};
    metadata.argumentBlockSize = IntPositive0{static_cast<int32_t>(kernelArgumentBlockSize)};
    metadata.explicitParameterCount = IntPositive0{static_cast<int32_t>(kernelExplicitParameterCount)};
    for (const auto &parameter : kernelParameters) {
      Parameter entry{};
      entry.width = IntPositive{static_cast<int32_t>(parameter.width)};
      entry.offset = IntPositive0{static_cast<int32_t>(parameter.offset)};
      entry.kind = parameterKind(parameter.kind);
      entry.containsPointers = parameter.containsPointers != 0;
      metadata.parameters.push_back(entry);
    }
    if (requiredWorkGroupSize)
      metadata.requiredWorkGroupSize = Dimensions{IntPositive{static_cast<int32_t>((*requiredWorkGroupSize)[0])},
                                                  IntPositive{static_cast<int32_t>((*requiredWorkGroupSize)[1])},
                                                  IntPositive{static_cast<int32_t>((*requiredWorkGroupSize)[2])}};
    for (const auto binding : bindingIndices)
      metadata.bindingIndices.push_back(IntPositive0{static_cast<int32_t>(binding)});
    encodePayload(result, metadata);
  }
  bool receive(int socket, void *output, size_t size) {
    auto *bytes = static_cast<uint8_t *>(output);
    while (size) {
      const auto count = recv(socket, bytes, size, 0);
      if (count == 0 || stopped.load()) return false;
      GEMSTONE_RPC_REQUIRE(count > 0);
      bytes += count;
      size -= count;
    }
    return true;
  }
  static void send(int socket, const std::vector<uint8_t> &bytes) {
    size_t offset = 0;
    while (offset < bytes.size()) {
      auto count = ::send(socket, bytes.data() + offset, bytes.size() - offset, MSG_NOSIGNAL);
      GEMSTONE_RPC_REQUIRE(count > 0);
      offset += count;
    }
  }
  void serve(int socket) {
    std::array<uint8_t, 8> hello{};
    if (!receive(socket, hello.data(), hello.size())) return;
    GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint32_t>(hello.data()) == HelloMagic);
    const auto schemaSize = wire::readBigEndian<uint32_t>(hello.data() + 4);
    GEMSTONE_RPC_REQUIRE(schemaSize == Schema.size());
    std::string schema(schemaSize, '\0');
    GEMSTONE_RPC_REQUIRE(receive(socket, reinterpret_cast<uint8_t *>(schema.data()), schema.size()));
    GEMSTONE_RPC_REQUIRE(schema == Schema);
    std::vector<uint8_t> accepted;
    wire::appendBigEndian<uint32_t>(accepted, HelloResponseMagic);
    wire::appendBigEndian<uint32_t>(accepted, 0);
    send(socket, accepted);
    std::array<uint8_t, RequestHeaderBytes> header{};
    while (receive(socket, header.data(), header.size())) {
      GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint32_t>(header.data()) == RequestMagic);
      GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint64_t>(header.data() + RequestClientOffset) == ClientId);
      const auto tagSize = wire::readBigEndian<uint32_t>(header.data() + RequestTagSizeOffset);
      GEMSTONE_RPC_REQUIRE(tagSize > 0 && tagSize <= MaximumTagBytes);
      std::string operationTag(tagSize, '\0');
      GEMSTONE_RPC_REQUIRE(receive(socket, reinterpret_cast<uint8_t *>(operationTag.data()), operationTag.size()));
      const auto size = wire::readBigEndian<uint32_t>(header.data() + RequestPayloadSizeOffset);
      GEMSTONE_RPC_REQUIRE(size <= MaximumPayload);
      std::vector<uint8_t> payload(size);
      if (size && !receive(socket, payload.data(), size)) break;
      std::lock_guard lock(stateMutex);
      std::vector<uint8_t> result;
      bool failed = false;
      std::string failure = "completion failed";
      if (operationTag == rejectedOperation) {
        rejectedOperation.clear();
        failed = true;
        failure = operationTag + " rejected";
      } else if (operationTag == operation::Ping) {
        encodePayload(result, Unit{});
      } else if (operationTag == operation::EnableHostMemory) {
        hostMemoryEnabled = true;
        encodePayload(result, Unit{});
      } else if (operationTag == operation::RegisterAllocation) {
        const auto request = decodePayload<RegisterAllocation>(payload);
        const auto id = nextAllocation++;
        GEMSTONE_RPC_REQUIRE(id != 0);
        allocations[id].resize(static_cast<size_t>(request.size.value));
        encodePayload(result, AllocationId{static_cast<int64_t>(id)});
      } else if (operationTag == operation::ReleaseAllocation) {
        const auto request = decodePayload<ReleaseAllocation>(payload);
        const auto id = static_cast<uint64_t>(request.allocation.value);
        const auto address = pauseReleaseAddress.load();
        const auto alias = allocationAddresses.find(id);
        if (address != 0 && alias != allocationAddresses.end() && address >= alias->second
            && address - alias->second < allocations.at(id).size()) {
          releaseEntered = true;
          while (pauseReleaseAddress.load() != 0 && !stopped.load())
            std::this_thread::yield();
        }
        GEMSTONE_RPC_REQUIRE(allocations.erase(id) == 1);
        allocationAddresses.erase(id);
        hostBacked.erase(id);
        encodePayload(result, Unit{});
      } else if (operationTag == operation::RegisterAllocationAddress) {
        const auto request = decodePayload<RegisterAllocationAddress>(payload);
        const auto id = static_cast<uint64_t>(request.allocation.value);
        const auto base = static_cast<uint64_t>(request.nativeBase.value);
        GEMSTONE_RPC_REQUIRE(allocations.count(id) == 1 && base != 0);
        GEMSTONE_RPC_REQUIRE(allocationAddresses.emplace(id, base).second);
        hostBacked[id] = request.hostBacked;
        encodePayload(result, Unit{});
      } else if (operationTag == operation::WriteAllocation) {
        const auto request = decodePayload<WriteAllocation>(payload);
        const auto id = static_cast<uint64_t>(request.allocation.value);
        auto &bytes = allocations.at(id);
        const auto offset = static_cast<uint64_t>(request.offset.value);
        GEMSTONE_RPC_REQUIRE(offset + request.bytes.size() <= bytes.size());
        std::memcpy(bytes.data() + offset, request.bytes.data(), request.bytes.size());
        allocationWrites[id] += request.bytes.size();
        encodePayload(result, Unit{});
      } else if (operationTag == operation::ReadAllocation) {
        ++reads;
        GEMSTONE_RPC_REQUIRE(!active || completed);
        const auto request = decodePayload<ReadAllocation>(payload);
        const auto id = static_cast<uint64_t>(request.allocation.value);
        auto &bytes = allocations.at(id);
        const auto offset = static_cast<uint64_t>(request.offset.value);
        const auto count = static_cast<size_t>(request.size.value);
        GEMSTONE_RPC_REQUIRE(offset + count <= bytes.size());
        encodePayload(result, std::vector<uint8_t>(bytes.begin() + offset, bytes.begin() + offset + count));
      } else if (operationTag == operation::LoadProgram) {
        const auto request = decodePayload<LoadProgram>(payload);
        programImage = request.image;
        const auto program = 91 + static_cast<int64_t>(programLoads++);
        livePrograms.insert(program);
        encodePayload(result, ProgramId{program});
      } else if (operationTag == operation::DescribeSpecialization) {
        decodePayload<DescribeSpecialization>(payload);
        std::vector<SpecializationInfo> infos;
        for (const auto &[id, byteSize] : specializationSizes)
          infos.push_back(SpecializationInfo{Binary32{static_cast<int64_t>(id)}, IntPositive{static_cast<int32_t>(byteSize)}});
        encodePayload(result, infos);
      } else if (operationTag == operation::SpecializeImage) {
        const auto request = decodePayload<SpecializeImage>(payload);
        std::vector<std::pair<uint32_t, std::vector<uint8_t>>> constants;
        for (const auto &constant : request.constants)
          constants.emplace_back(static_cast<uint32_t>(constant.specId.value), constant.value);
        specialization = std::move(constants);
        encodePayload(result, request.image);
      } else if (operationTag == operation::PrepareShader) {
        const auto request = decodePayload<PrepareShader>(payload);
        GEMSTONE_RPC_REQUIRE(preparedDescriptors.has_value());
        GEMSTONE_RPC_REQUIRE(!request.entry.value.empty());
        specialization.clear();
        for (const auto &constant : request.constants)
          specialization.emplace_back(static_cast<uint32_t>(constant.specId.value), constant.value);
        PreparedShader prepared{};
        prepared.image = request.image;
        prepared.entry = {request.entry.value};
        prepared.stage = request.stage;
        prepared.descriptors = *preparedDescriptors;
        if (requiredWorkGroupSize)
          prepared.workgroup =
              ShaderWorkgroup{static_cast<int32_t>((*requiredWorkGroupSize)[0]), static_cast<int32_t>((*requiredWorkGroupSize)[1]),
                              static_cast<int32_t>((*requiredWorkGroupSize)[2])};
        encodePayload(result, prepared);
      } else if (operationTag == operation::UnloadProgram) {
        GEMSTONE_RPC_REQUIRE(livePrograms.erase(decodePayload<UnloadProgram>(payload).program.value) == 1);
        ++programUnloads;
        encodePayload(result, Unit{});
      } else if (operationTag == operation::DescribeKernel) {
        const auto request = decodePayload<DescribeKernel>(payload);
        GEMSTONE_RPC_REQUIRE(livePrograms.contains(request.program.value) && !request.name.value.empty());
        appendKernelMetadata(result);
      } else if (operationTag == operation::DescribeImage) {
        const auto request = decodePayload<DescribeImage>(payload);
        GEMSTONE_RPC_REQUIRE(!request.name.value.empty());
        appendKernelMetadata(result);
      } else if (operationTag == operation::DescribeProgram) {
        const auto request = decodePayload<DescribeProgram>(payload);
        GEMSTONE_RPC_REQUIRE(livePrograms.contains(request.program.value));
        ProgramSymbols catalogue{};
        for (const auto &kernel : programKernels)
          catalogue.kernels.push_back(gemstone::execution::KernelSymbol{{kernel.name}, {kernel.linkageName}});
        for (const auto &global : programGlobals) {
          GlobalSymbol meta{};
          meta.name = {global.name};
          meta.virtualAddress = GuestAddress{static_cast<int64_t>(global.virtualAddress)};
          meta.size = LongPositive0{static_cast<int64_t>(global.size)};
          for (uint32_t offset : global.pointerOffsets)
            meta.pointerOffsets.push_back(IntPositive0{static_cast<int32_t>(offset)});
          catalogue.globals.push_back(std::move(meta));
        }
        programGlobals.clear();
        encodePayload(result, catalogue);
      } else if (operationTag == operation::BindProgramGlobal) {
        const auto request = decodePayload<BindProgramGlobal>(payload);
        const auto name = request.name.value;
        const auto allocation = static_cast<uint64_t>(request.allocation.value);
        const auto offset = static_cast<uint64_t>(request.offset.value);
        auto expected = expectedGlobals.find(name);
        GEMSTONE_RPC_REQUIRE(expected != expectedGlobals.end() && expected->second.first == offset);
        auto &memory = allocations.at(allocation);
        const auto &initial = expected->second.second;
        GEMSTONE_RPC_REQUIRE(offset <= memory.size() && initial.size() <= memory.size() - offset);
        std::copy(initial.begin(), initial.end(), memory.begin() + offset);
        expectedGlobals.erase(expected);
        encodePayload(result, Unit{});
      } else if (operationTag == operation::Launch) {
        launch(payload);
        encodePayload(result, CompletionId{static_cast<int64_t>(completionId)});
      } else if (operationTag == operation::QueryCompletion) {
        const auto request = decodePayload<QueryCompletion>(payload);
        GEMSTONE_RPC_REQUIRE(request.completion.value == static_cast<int64_t>(completionId));
        if (polls++ < 2) {
          ++pending;
          encodePayload(result, CompletionStatus{CompletionStatusPending{}});
        } else {
          completed = true;
          if (failCompletion.exchange(false))
            encodePayload(result,
                          CompletionStatus{CompletionStatusFailed{ExecutionFailure{ExecutionFailureFailed{{"completion failed"}}}}});
          else encodePayload(result, CompletionStatus{CompletionStatusSucceeded{}});
        }
      } else if (operationTag == operation::PollHostMemory) {
        const auto completion = decodePayload<PollHostMemory>(payload).completion;
        GEMSTONE_RPC_REQUIRE(completion.value == static_cast<int64_t>(completionId));
        if (pauseCompletion.load()) {
          completionEntered = true;
          while (pauseCompletion.load() && !stopped.load())
            std::this_thread::yield();
        }
        if (!failCompletion.load()) {
          if (hostBacked[outputId])
            for (const auto &range : ranges)
              std::memcpy(reinterpret_cast<void *>(allocationAddresses.at(outputId) + range.first),
                          allocations.at(outputId).data() + range.first, range.second);
          if (indirectWrite && hostBacked[indirectWrite->first])
            std::memcpy(reinterpret_cast<void *>(allocationAddresses.at(indirectWrite->first) + indirectWrite->second),
                        allocations.at(indirectWrite->first).data() + indirectWrite->second, 4);
        }
        ++awaited;
        completed = true;
        failed = failCompletion.exchange(false);
        if (operationTag == operation::PollHostMemory && !failed) {
          HostMemoryBatch batch{};
          batch.completed = true;
          if (hostOutputAddress)
            for (const auto &range : ranges) {
              HostMemoryBytes bytes{};
              bytes.address = GuestAddress{static_cast<int64_t>(hostOutputAddress + range.first)};
              bytes.bytes.assign(hostOutputBytes.begin() + range.first, hostOutputBytes.begin() + range.first + range.second);
              bytes.atomicWidth = {0};
              batch.writes.push_back(std::move(bytes));
            }
          encodePayload(result, batch);
        } else encodePayload(result, Unit{});
      } else if (operationTag == operation::ReleaseCompletion) {
        const auto request = decodePayload<ReleaseCompletion>(payload);
        GEMSTONE_RPC_REQUIRE(completed && request.completion.value == static_cast<int64_t>(completionId));
        active = false;
        encodePayload(result, Unit{});
      } else if (operationTag == operation::CompletionDirtyRanges) {
        const auto request = decodePayload<CompletionDirtyRanges>(payload);
        GEMSTONE_RPC_REQUIRE(completed);
        std::vector<DirtyRange> dirty = opaqueDirty.value_or(std::vector<DirtyRange>{});
        if (!opaqueDirty && !hostOutputAddress && !hostBacked[outputId])
          for (const auto &range : ranges)
            dirty.push_back(DirtyRange{AllocationId{static_cast<int64_t>(outputId)}, LongPositive0{static_cast<int64_t>(range.first)},
                                       IntPositive{static_cast<int32_t>(range.second)}});
        if (indirectWrite && !hostBacked[indirectWrite->first])
          dirty.push_back(DirtyRange{AllocationId{static_cast<int64_t>(indirectWrite->first)},
                                     LongPositive0{static_cast<int64_t>(indirectWrite->second)}, IntPositive{4}});
        const auto begin = std::min(static_cast<size_t>(request.offset.value), dirty.size());
        const auto end = std::min(begin + MaximumElements, dirty.size());
        encodePayload(result, std::vector<DirtyRange>(dirty.begin() + begin, dirty.begin() + end));
      } else if (operationTag == operation::CompletionPointerRelocations) {
        decodePayload<CompletionPointerRelocations>(payload);
        GEMSTONE_RPC_REQUIRE(completed);
        encodePayload(result, std::vector<PointerRelocation>{});
      } else if (operationTag == operation::ReadGraphicsStatistics) {
        const auto request = decodePayload<ReadGraphicsStatistics>(payload);
        GEMSTONE_RPC_REQUIRE(completed && active && request.completion.value == static_cast<int64_t>(completionId));
        failed = failGraphicsStatistics.exchange(false);
        // A distinctive fixture result, independent of simulated compute writes.
        encodePayload(result, GraphicsStatistics{17});
      } else if (operationTag == operation::CompletionPrints) {
        decodePayload<CompletionPrints>(payload);
        GEMSTONE_RPC_REQUIRE(completed);
        encodePayload(result, std::string{});
      } else {
        GEMSTONE_RPC_REQUIRE(false);
      }
      std::vector<uint8_t> response;
      wire::appendBigEndian<uint32_t>(response, ResponseMagic);
      wire::appendBigEndian<uint32_t>(response, failed ? 1 : 0);
      if (failed) {
        result.clear();
        encodePayload(result, ExecutionFailure{ExecutionFailureFailed{{failure}}});
      }
      wire::appendBigEndian<uint32_t>(response, static_cast<uint32_t>(result.size()));
      response.insert(response.end(), result.begin(), result.end());
      send(socket, response);
    }
  }

  void launch(const std::vector<uint8_t> &payload) {
    GEMSTONE_RPC_REQUIRE(!active);
    const auto request = decodePayload<Launch>(payload);
    recordedLaunch = request;
    if (opaqueDirty) {
      outputId = 0;
      ranges.clear();
      ++completionId;
      ++launches;
      polls = 0;
      active = true;
      completed = false;
      return;
    }
    if (indirectInput) {
      uint64_t child = 0;
      bool foundTable = false;
      for (const auto &[id, address] : allocationAddresses)
        if (address == indirectInput->table) {
          const auto &bytes = allocations.at(id);
          GEMSTONE_RPC_REQUIRE(bytes.size() >= sizeof(child));
          std::memcpy(&child, hostBacked[id] ? reinterpret_cast<const void *>(address) : bytes.data(), sizeof(child));
          foundTable = true;
        }
      GEMSTONE_RPC_REQUIRE(foundTable);
      bool foundChild = false;
      for (const auto &[id, address] : allocationAddresses) {
        auto &bytes = allocations.at(id);
        if (child < address || child - address > bytes.size() || bytes.size() - (child - address) < 4) continue;
        uint32_t value = 0;
        std::memcpy(&value, hostBacked[id] ? reinterpret_cast<const void *>(child) : bytes.data() + child - address, sizeof(value));
        GEMSTONE_RPC_REQUIRE(value == indirectInput->value);
        if (indirectInput->deviceWrite) {
          std::memcpy(bytes.data() + child - address, &*indirectInput->deviceWrite, 4);
          indirectWrite = std::make_pair(id, child - address);
        }
        foundChild = true;
        break;
      }
      GEMSTONE_RPC_REQUIRE(foundChild);
    }
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupSize.x.value) == expectation.local);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupSize.y.value) == expectation.localYz[0]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupSize.z.value) == expectation.localYz[1]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupCount.x.value) == expectation.groups);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupCount.y.value) == expectation.groupsYz[0]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupCount.z.value) == expectation.groupsYz[1]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint64_t>(request.globalSize.x.value) == expectation.global);
    GEMSTONE_RPC_REQUIRE(static_cast<uint64_t>(request.globalSize.y.value) == expectation.globalYz[0]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint64_t>(request.globalSize.z.value) == expectation.globalYz[1]);
    GEMSTONE_RPC_REQUIRE(request.workDim.value >= 1 && request.workDim.value <= 3);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.globalOffset.x.value) == expectation.offset);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.globalOffset.y.value) == 0);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.globalOffset.z.value) == 0);
    auto arguments = request.arguments;
    if (request.argumentBuffer) {
      GEMSTONE_RPC_REQUIRE(arguments.empty());
      const auto id = static_cast<uint64_t>(request.argumentBuffer->region.value);
      const auto offset = static_cast<uint64_t>(request.argumentBuffer->offset.value);
      const auto &storage = allocations.at(id);
      // This fixture's kernel takes a pointer and a 32-bit seed, then optional trailing bytes.
      const size_t size = 12 + (trailingArguments ? trailingArguments->size() : 0);
      GEMSTONE_RPC_REQUIRE(offset <= storage.size() && size <= storage.size() - offset);
      const auto *bytes = hostBacked[id] ? reinterpret_cast<const uint8_t *>(allocationAddresses.at(id) + offset) : storage.data() + offset;
      uint64_t pointer = 0;
      std::memcpy(&pointer, bytes, sizeof(pointer));
      std::optional<ArgumentMemory> output;
      for (const auto &[allocation, address] : allocationAddresses)
        if (pointer >= address && pointer - address <= allocations.at(allocation).size()) {
          output = ArgumentMemory{AllocationId{static_cast<int64_t>(allocation)}, static_cast<int64_t>(pointer - address)};
          break;
        }
      if (output) arguments.emplace_back(*output);
      else arguments.emplace_back(ArgumentScalar{std::vector<uint8_t>(bytes, bytes + 8)});
      arguments.emplace_back(ArgumentScalar{std::vector<uint8_t>(bytes + 8, bytes + 12)});
      if (size > 12) arguments.emplace_back(ArgumentScalar{std::vector<uint8_t>(bytes + 12, bytes + size)});
    }
    if (allocationArguments) {
      GEMSTONE_RPC_REQUIRE(arguments.size() == allocationArguments->size());
      GEMSTONE_RPC_REQUIRE(request.pointerRelocations.empty());
      std::map<size_t, uint64_t> aliases;
      std::vector<std::pair<uint64_t, uint64_t>> actual;
      for (size_t index = 0; index < arguments.size(); ++index) {
        const auto *memory = std::get_if<ArgumentMemory>(&arguments[index]);
        GEMSTONE_RPC_REQUIRE(memory != nullptr);
        const auto expected = allocationArguments->at(index);
        const auto id = static_cast<uint64_t>(memory->region.value);
        const auto offset = static_cast<uint64_t>(memory->offset.value);
        GEMSTONE_RPC_REQUIRE((memory->range ? std::optional<uint64_t>(memory->range->value) : std::nullopt) == expected.range);
        const auto [alias, inserted] = aliases.emplace(expected.alias, id);
        GEMSTONE_RPC_REQUIRE((inserted || alias->second == id) && offset == expected.offset);
        actual.emplace_back(id, offset);
      }
      if (allocationArgumentWrite) {
        GEMSTONE_RPC_REQUIRE(allocationArgumentWrite->argument < actual.size());
        const auto [id, base] = actual[allocationArgumentWrite->argument];
        auto &bytes = allocations.at(id);
        const auto offset = base + allocationArgumentWrite->offset;
        GEMSTONE_RPC_REQUIRE(offset <= bytes.size() && allocationArgumentWrite->bytes.size() <= bytes.size() - offset);
        std::copy(allocationArgumentWrite->bytes.begin(), allocationArgumentWrite->bytes.end(), bytes.begin() + offset);
        if (hostBacked[id])
          std::memcpy(reinterpret_cast<void *>(allocationAddresses.at(id) + offset), allocationArgumentWrite->bytes.data(),
                      allocationArgumentWrite->bytes.size());
      }
      outputId = actual.empty() ? 0 : actual.front().first;
      ranges.clear();
      ++completionId;
      ++launches;
      polls = 0;
      active = true;
      completed = false;
      return;
    }
    GEMSTONE_RPC_REQUIRE(arguments.size() >= 2);
    hostOutputAddress = 0;
    uint64_t base = 0;
    if (const auto *memory = std::get_if<ArgumentMemory>(&arguments[0])) {
      outputId = static_cast<uint64_t>(memory->region.value);
      base = static_cast<uint64_t>(memory->offset.value);
      GEMSTONE_RPC_REQUIRE(!memory->range);
    } else {
      const auto *scalar = std::get_if<ArgumentScalar>(&arguments[0]);
      GEMSTONE_RPC_REQUIRE(scalar != nullptr && hostMemoryEnabled && scalar->bytes.value.size() == 8);
      std::memcpy(&hostOutputAddress, scalar->bytes.value.data(), 8);
      GEMSTONE_RPC_REQUIRE(hostOutputAddress != 0);
      hostOutputBytes.resize(expectation.global * expectation.globalYz[0] * expectation.globalYz[1] * 4);
      outputId = 0;
    }
    uint32_t seed = 0;
    {
      const auto *scalar = std::get_if<ArgumentScalar>(&arguments[1]);
      GEMSTONE_RPC_REQUIRE(scalar != nullptr && scalar->bytes.value.size() == 4);
      std::memcpy(&seed, scalar->bytes.value.data(), 4);
      GEMSTONE_RPC_REQUIRE(seed == expectation.seed);
    }
    if (trailingArguments) {
      std::vector<uint8_t> bytes;
      for (size_t index = 2; index < arguments.size(); ++index) {
        const auto *scalar = std::get_if<ArgumentScalar>(&arguments[index]);
        GEMSTONE_RPC_REQUIRE(scalar != nullptr);
        bytes.insert(bytes.end(), scalar->bytes.value.begin(), scalar->bytes.value.end());
      }
      GEMSTONE_RPC_REQUIRE(bytes == *trailingArguments);
    }

    auto &bytes = hostOutputAddress ? hostOutputBytes : allocations.at(outputId);
    ranges.clear();
    if (!expectation.geometryOnly) {
      for (uint32_t groupZ = 0; groupZ < expectation.groupsYz[1]; ++groupZ)
        for (uint32_t groupY = 0; groupY < expectation.groupsYz[0]; ++groupY)
          for (uint32_t groupX = 0; groupX < expectation.groups; ++groupX) {
            const uint32_t firstX = groupX * expectation.local;
            const uint32_t firstY = groupY * expectation.localYz[0];
            const uint32_t firstZ = groupZ * expectation.localYz[1];
            const auto countX = std::min(expectation.local, expectation.global - firstX);
            const auto countY = std::min(expectation.localYz[0], expectation.globalYz[0] - firstY);
            const auto countZ = std::min(expectation.localYz[1], expectation.globalYz[1] - firstZ);
            for (uint32_t z = 0; z < countZ; ++z)
              for (uint32_t y = 0; y < countY; ++y) {
                const uint64_t first = ((uint64_t(firstZ + z) * expectation.globalYz[0]) + firstY + y) * expectation.global + firstX;
                GEMSTONE_RPC_REQUIRE(base + (first + countX) * 4 <= bytes.size());
                for (uint32_t x = 0; x < countX; ++x) {
                  const uint32_t value = seed + expectation.offset + static_cast<uint32_t>(first + x);
                  std::memcpy(bytes.data() + base + (first + x) * 4, &value, 4);
                }
                ranges.emplace_back(base + first * 4, countX * 4);
              }
          }
    }
    ++completionId;
    ++launches;
    polls = 0;
    active = true;
    completed = false;
  }

  std::string socketPath, socketDirectory;
  int listener = -1;
  uint16_t port = 0;
  std::atomic<bool> stopped{false};
  std::thread acceptor;
  mutable std::mutex failureMutex;
  std::exception_ptr firstFailure;
  std::mutex connectionsMutex, stateMutex;
  std::vector<int> connections;
  std::vector<std::thread> workers;
  Expected expectation{};
  std::optional<std::vector<uint8_t>> trailingArguments;
  struct AllocationArgumentWrite {
    size_t argument;
    uint64_t offset;
    std::vector<uint8_t> bytes;
  };
  std::optional<std::vector<ExpectedAllocationArgument>> allocationArguments;
  std::optional<AllocationArgumentWrite> allocationArgumentWrite;
  uint32_t kernelArgumentBlockSize = 12;
  uint32_t kernelExplicitParameterCount = 2;
  std::vector<KernelParameter> kernelParameters{{8, 0, 2, 0}, {4, 8, 1, 0}};
  std::optional<std::array<uint32_t, 3>> requiredWorkGroupSize;
  std::optional<std::vector<ShaderDescriptor>> preparedDescriptors;
  std::vector<uint32_t> bindingIndices;
  std::vector<ProgramKernel> programKernels{{"add", "add.kd"}};
  std::vector<ProgramGlobal> programGlobals;
  std::set<int64_t> livePrograms;
  struct IndirectInput {
    uint64_t table;
    uint32_t value;
    std::optional<uint32_t> deviceWrite;
  };
  std::optional<IndirectInput> indirectInput;
  std::optional<std::pair<uint64_t, uint64_t>> indirectWrite;
  std::map<uint64_t, uint64_t> allocationWrites;
  std::map<uint64_t, std::vector<uint8_t>> allocations;
  std::vector<uint8_t> programImage;
  std::map<uint32_t, uint32_t> specializationSizes;
  std::vector<std::pair<uint32_t, std::vector<uint8_t>>> specialization;
  std::map<uint64_t, uint64_t> allocationAddresses;
  std::map<uint64_t, bool> hostBacked;
  std::map<std::string, std::pair<uint64_t, std::vector<uint8_t>>> expectedGlobals;
  std::vector<std::pair<uint64_t, uint32_t>> ranges;
  std::atomic_bool failGraphicsStatistics{false};
  std::string rejectedOperation;
  std::atomic_bool failCompletion{false};
  std::atomic_bool pauseCompletion{false};
  std::atomic_uint64_t pauseReleaseAddress{0};
  bool hostMemoryEnabled = false;
  uint64_t hostOutputAddress = 0;
  std::vector<uint8_t> hostOutputBytes;
  uint64_t nextAllocation = 1, outputId = 0, completionId = 100;
  uint32_t polls = 0;
  std::atomic<unsigned> pending{0}, awaited{0};
  std::optional<std::vector<DirtyRange>> opaqueDirty;
  Launch recordedLaunch{};
  bool active = false, completed = false;
};
class RpcEnvironment {
  Environment endpoint;
  Environment client;

public:
  explicit RpcEnvironment(const std::string &endpoint)
      : endpoint(gemstone::environment::RpcEndpoint, endpoint.c_str()),
        client(gemstone::environment::RpcClient, std::to_string(RpcFixture::ClientId).c_str()) {}
  explicit RpcEnvironment(const RpcFixture &fixture) : RpcEnvironment(fixture.endpoint()) {}
};
} // namespace gemstone_test
#undef GEMSTONE_RPC_REQUIRE
