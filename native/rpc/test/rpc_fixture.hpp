#ifndef GEMSTONE_TEST_RPC_FIXTURE_HPP
#define GEMSTONE_TEST_RPC_FIXTURE_HPP

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
  inline static constexpr uint64_t clientId = 42;
  struct Expected {
    uint32_t local, groups, global, offset, seed;
    std::array<uint32_t, 2> local_yz{1, 1}, groups_yz{1, 1}, global_yz{1, 1};
    // Boundary-sized geometry can be inspected without mocking billions of output elements.
    bool geometry_only = false;
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
  explicit RpcFixture(bool unixSocket = false) {
    if (unixSocket) {
      char directory[] = "/tmp/gemstone-rpc-XXXXXX";
      GEMSTONE_RPC_REQUIRE(mkdtemp(directory) != nullptr);
      directory_ = directory;
      path_ = directory_ + "/socket with space";
      sockaddr_un address{};
      address.sun_family = AF_UNIX;
      GEMSTONE_RPC_REQUIRE(path_.size() < sizeof(address.sun_path));
      std::memcpy(address.sun_path, path_.c_str(), path_.size() + 1);
      listener_ = socket(AF_UNIX, SOCK_STREAM, 0);
      GEMSTONE_RPC_REQUIRE(listener_ >= 0);
      GEMSTONE_RPC_REQUIRE(bind(listener_, reinterpret_cast<sockaddr *>(&address), sizeof(address)) == 0);
    } else {
      listener_ = socket(AF_INET, SOCK_STREAM, 0);
      GEMSTONE_RPC_REQUIRE(listener_ >= 0);
      sockaddr_in address{};
      address.sin_family = AF_INET;
      address.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
      GEMSTONE_RPC_REQUIRE(bind(listener_, reinterpret_cast<sockaddr *>(&address), sizeof(address)) == 0);
      socklen_t length = sizeof(address);
      GEMSTONE_RPC_REQUIRE(getsockname(listener_, reinterpret_cast<sockaddr *>(&address), &length) == 0);
      port_ = ntohs(address.sin_port);
    }
    GEMSTONE_RPC_REQUIRE(listen(listener_, 8) == 0);
    acceptor_ = std::thread([this] {
      try {
        while (!stopped_.load()) {
          const int connection = accept(listener_, nullptr, nullptr);
          if (connection < 0) {
            GEMSTONE_RPC_REQUIRE(stopped_.load());
            break;
          }
          const int enabled = 1;
          if (path_.empty()) GEMSTONE_RPC_REQUIRE(setsockopt(connection, IPPROTO_TCP, TCP_NODELAY, &enabled, sizeof(enabled)) == 0);
          std::lock_guard lock(connectionsMutex_);
          connections_.push_back(connection);
          workers_.emplace_back([this, connection] {
            try {
              serve(connection);
            } catch (...) {
              recordFailure(std::current_exception());
            }
            std::lock_guard guard(connectionsMutex_);
            connections_.erase(std::remove(connections_.begin(), connections_.end(), connection), connections_.end());
            close(connection);
          });
        }
      } catch (...) {
        recordFailure(std::current_exception());
      }
    });
  }
  ~RpcFixture() {
    stopped_.store(true);
    shutdown(listener_, SHUT_RDWR);
    close(listener_);
    acceptor_.join();
    {
      std::lock_guard lock(connectionsMutex_);
      for (int connection : connections_)
        shutdown(connection, SHUT_RDWR);
    }
    for (auto &worker : workers_)
      worker.join();
    if (!path_.empty()) {
      unlink(path_.c_str());
      rmdir(directory_.c_str());
    }
  }
  uint16_t port() const { return port_; }
  std::string endpoint() const {
    if (path_.empty()) return "tcp://127.0.0.1:" + std::to_string(port_);
    std::string encoded = "unix://";
    for (char ch : path_)
      encoded += ch == ' ' ? "%20" : std::string(1, ch);
    return encoded;
  }
  void expect(Expected expected) {
    std::lock_guard lock(stateMutex_);
    GEMSTONE_RPC_REQUIRE(!active_);
    expected_ = expected;
    trailingArguments_.reset();
    indirectInput_.reset();
    indirectWrite_.reset();
  }
  void expectTrailingArguments(std::vector<uint8_t> bytes) {
    std::lock_guard lock(stateMutex_);
    trailingArguments_ = std::move(bytes);
  }
  void expectAllocationArguments(std::vector<ExpectedAllocationArgument> arguments) {
    std::lock_guard lock(stateMutex_);
    allocationArguments_ = std::move(arguments);
  }
  void writeAllocationArgument(size_t argument, uint64_t offset, std::vector<uint8_t> bytes) {
    std::lock_guard lock(stateMutex_);
    allocationArgumentWrite_ = AllocationArgumentWrite{argument, offset, std::move(bytes)};
  }
  void kernelMetadata(uint32_t argumentBlockSize, uint32_t explicitParameterCount, std::vector<KernelParameter> parameters) {
    std::lock_guard lock(stateMutex_);
    GEMSTONE_RPC_REQUIRE(explicitParameterCount <= parameters.size());
    kernelArgumentBlockSize_ = argumentBlockSize;
    kernelExplicitParameterCount_ = explicitParameterCount;
    kernelParameters_ = std::move(parameters);
  }
  void programCatalog(std::vector<ProgramKernel> kernels, std::vector<ProgramGlobal> globals) {
    std::lock_guard lock(stateMutex_);
    programKernels_ = std::move(kernels);
    programGlobals_ = std::move(globals);
  }
  void kernelWorkGroup(std::array<uint32_t, 3> size) {
    std::lock_guard lock(stateMutex_);
    requiredWorkGroupSize_ = size;
  }
  void kernelBindings(std::vector<uint32_t> bindings) {
    std::lock_guard lock(stateMutex_);
    bindingIndices_ = std::move(bindings);
  }
  void shaderDescriptors(std::vector<ShaderDescriptor> descriptors) {
    std::lock_guard lock(stateMutex_);
    shaderDescriptors_ = std::move(descriptors);
  }
  void specializationTable(std::map<uint32_t, uint32_t> sizes) {
    std::lock_guard lock(stateMutex_);
    specializationSizes_ = std::move(sizes);
  }
  std::vector<std::pair<uint32_t, std::vector<uint8_t>>> lastSpecialization() {
    std::lock_guard lock(stateMutex_);
    return lastSpecialization_;
  }
  void expectGlobalBinding(std::string name, uint64_t offset, std::vector<uint8_t> initial) {
    std::lock_guard lock(stateMutex_);
    expectedGlobals_.emplace(std::move(name), std::make_pair(offset, std::move(initial)));
  }
  size_t allocationCount() {
    std::lock_guard lock(stateMutex_);
    return allocations_.size();
  }
  void checkAllocationAddress(const void *pointer, bool present = true) {
    std::lock_guard lock(stateMutex_);
    const auto address = reinterpret_cast<uint64_t>(pointer);
    const auto found =
        std::find_if(allocationAddresses_.begin(), allocationAddresses_.end(), [&](const auto &entry) { return entry.second == address; });
    GEMSTONE_RPC_REQUIRE((found != allocationAddresses_.end()) == present);
  }
  void checkAllocationContainingAddress(const void *pointer, bool present = true) {
    std::lock_guard lock(stateMutex_);
    const auto address = reinterpret_cast<uint64_t>(pointer);
    const auto found = std::find_if(allocationAddresses_.begin(), allocationAddresses_.end(), [&](const auto &entry) {
      const auto size = allocations_.at(entry.first).size();
      return address >= entry.second && address - entry.second < size;
    });
    GEMSTONE_RPC_REQUIRE((found != allocationAddresses_.end()) == present);
  }
  void pauseReleaseContaining(const void *pointer) {
    releaseEntered_ = false;
    pauseReleaseAddress_ = reinterpret_cast<uint64_t>(pointer);
  }
  bool releaseEntered() const { return releaseEntered_.load(); }
  void releasePausedRelease() { pauseReleaseAddress_ = 0; }
  void checkHostBacked(const void *pointer) {
    std::lock_guard lock(stateMutex_);
    for (const auto &[id, base] : allocationAddresses_)
      if (base == reinterpret_cast<uintptr_t>(pointer)) {
        GEMSTONE_RPC_REQUIRE(hostBacked_.at(id));
        return;
      }
    GEMSTONE_RPC_REQUIRE(false);
  }
  void checkAllocationWord(const void *base, size_t offset, uint64_t expected) {
    std::lock_guard lock(stateMutex_);
    const auto found = std::find_if(allocationAddresses_.begin(), allocationAddresses_.end(),
                                    [&](const auto &entry) { return entry.second == reinterpret_cast<uint64_t>(base); });
    GEMSTONE_RPC_REQUIRE(found != allocationAddresses_.end());
    const auto &bytes = allocations_.at(found->first);
    GEMSTONE_RPC_REQUIRE(offset <= bytes.size() && bytes.size() - offset >= sizeof(uint64_t));
    uint64_t actual = 0;
    std::memcpy(&actual, hostBacked_[found->first] ? static_cast<const uint8_t *>(base) + offset : bytes.data() + offset, sizeof(actual));
    GEMSTONE_RPC_REQUIRE(actual == expected);
  }
  void expectIndirectInput(const void *table, uint32_t value, std::optional<uint32_t> deviceWrite = std::nullopt) {
    std::lock_guard lock(stateMutex_);
    indirectInput_ = IndirectInput{reinterpret_cast<uint64_t>(table), value, deviceWrite};
  }
  uint64_t writtenBytes(const void *address) {
    std::lock_guard lock(stateMutex_);
    for (const auto &[id, base] : allocationAddresses_)
      if (base == reinterpret_cast<uint64_t>(address)) return writtenBytes_[id];
    GEMSTONE_RPC_REQUIRE(false);
    return 0;
  }
  void checkGlobalBindings() {
    std::lock_guard lock(stateMutex_);
    GEMSTONE_RPC_REQUIRE(expectedGlobals_.empty());
  }
  void check() {
    rethrowFailure();
    std::lock_guard lock(stateMutex_);
    GEMSTONE_RPC_REQUIRE(!active_ && launches_ > 0 && awaited_ == launches_);
  }
  void failNextGraphicsStatistics() { failGraphicsStatistics_ = true; }
  void failNextCompletion() { failCompletion_ = true; }
  void pauseNextCompletion() {
    completionEntered_ = false;
    pauseCompletion_ = true;
  }
  bool completionEntered() const { return completionEntered_.load(); }
  void releasePausedCompletion() { pauseCompletion_ = false; }
  unsigned launches() const { return launches_.load(); }
  unsigned reads() const { return reads_.load(); }
  unsigned programLoads() const { return programLoads_.load(); }
  std::vector<uint8_t> lastProgramImage() {
    std::lock_guard lock(stateMutex_);
    return lastProgramImage_;
  }

  inline static constexpr uint32_t spirv[] = {0x07230203,
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
    std::lock_guard lock(failureMutex_);
    if (!failure_) failure_ = std::move(failure);
  }
  void rethrowFailure() const {
    std::exception_ptr failure;
    {
      std::lock_guard lock(failureMutex_);
      failure = failure_;
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
    metadata.argumentBlockSize = IntPositive0{static_cast<int32_t>(kernelArgumentBlockSize_)};
    metadata.explicitParameterCount = IntPositive0{static_cast<int32_t>(kernelExplicitParameterCount_)};
    for (const auto &parameter : kernelParameters_) {
      Parameter entry{};
      entry.width = IntPositive{static_cast<int32_t>(parameter.width)};
      entry.offset = IntPositive0{static_cast<int32_t>(parameter.offset)};
      entry.kind = parameterKind(parameter.kind);
      entry.containsPointers = parameter.containsPointers != 0;
      metadata.parameters.push_back(entry);
    }
    if (requiredWorkGroupSize_)
      metadata.requiredWorkGroupSize = Dimensions{IntPositive{static_cast<int32_t>((*requiredWorkGroupSize_)[0])},
                                                  IntPositive{static_cast<int32_t>((*requiredWorkGroupSize_)[1])},
                                                  IntPositive{static_cast<int32_t>((*requiredWorkGroupSize_)[2])}};
    for (const auto binding : bindingIndices_)
      metadata.bindingIndices.push_back(IntPositive0{static_cast<int32_t>(binding)});
    encodePayload(result, metadata);
  }
  bool receive(int socket, void *output, size_t size) {
    auto *bytes = static_cast<uint8_t *>(output);
    while (size) {
      const auto count = recv(socket, bytes, size, 0);
      if (count == 0 || stopped_.load()) return false;
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
      GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint64_t>(header.data() + RequestClientOffset) == clientId);
      const auto tagSize = wire::readBigEndian<uint32_t>(header.data() + RequestTagSizeOffset);
      GEMSTONE_RPC_REQUIRE(tagSize > 0 && tagSize <= MaximumTagBytes);
      std::string operationTag(tagSize, '\0');
      GEMSTONE_RPC_REQUIRE(receive(socket, reinterpret_cast<uint8_t *>(operationTag.data()), operationTag.size()));
      const auto size = wire::readBigEndian<uint32_t>(header.data() + RequestPayloadSizeOffset);
      GEMSTONE_RPC_REQUIRE(size <= MaximumPayload);
      std::vector<uint8_t> payload(size);
      if (size && !receive(socket, payload.data(), size)) break;
      std::lock_guard lock(stateMutex_);
      std::vector<uint8_t> result;
      bool failed = false;
      if (operationTag == operation::Ping) {
        encodePayload(result, Unit{});
      } else if (operationTag == operation::EnableHostMemory) {
        hostMemoryEnabled_ = true;
        encodePayload(result, Unit{});
      } else if (operationTag == operation::RegisterAllocation) {
        const auto request = decodePayload<RegisterAllocation>(payload);
        const auto id = nextAllocation_++;
        GEMSTONE_RPC_REQUIRE(id != 0);
        allocations_[id].resize(static_cast<size_t>(request.size.value));
        encodePayload(result, AllocationId{static_cast<int64_t>(id)});
      } else if (operationTag == operation::ReleaseAllocation) {
        const auto request = decodePayload<ReleaseAllocation>(payload);
        const auto id = static_cast<uint64_t>(request.allocation.value);
        const auto address = pauseReleaseAddress_.load();
        const auto alias = allocationAddresses_.find(id);
        if (address != 0 && alias != allocationAddresses_.end() && address >= alias->second
            && address - alias->second < allocations_.at(id).size()) {
          releaseEntered_ = true;
          while (pauseReleaseAddress_.load() != 0 && !stopped_.load())
            std::this_thread::yield();
        }
        GEMSTONE_RPC_REQUIRE(allocations_.erase(id) == 1);
        allocationAddresses_.erase(id);
        hostBacked_.erase(id);
        encodePayload(result, Unit{});
      } else if (operationTag == operation::RegisterAllocationAddress) {
        const auto request = decodePayload<RegisterAllocationAddress>(payload);
        const auto id = static_cast<uint64_t>(request.allocation.value);
        const auto base = static_cast<uint64_t>(request.nativeBase.value);
        GEMSTONE_RPC_REQUIRE(allocations_.count(id) == 1 && base != 0);
        GEMSTONE_RPC_REQUIRE(allocationAddresses_.emplace(id, base).second);
        hostBacked_[id] = request.hostBacked;
        encodePayload(result, Unit{});
      } else if (operationTag == operation::WriteAllocation) {
        const auto request = decodePayload<WriteAllocation>(payload);
        const auto id = static_cast<uint64_t>(request.allocation.value);
        auto &bytes = allocations_.at(id);
        const auto offset = static_cast<uint64_t>(request.offset.value);
        GEMSTONE_RPC_REQUIRE(offset + request.bytes.size() <= bytes.size());
        std::memcpy(bytes.data() + offset, request.bytes.data(), request.bytes.size());
        writtenBytes_[id] += request.bytes.size();
        encodePayload(result, Unit{});
      } else if (operationTag == operation::ReadAllocation) {
        ++reads_;
        GEMSTONE_RPC_REQUIRE(!active_ || completed_);
        const auto request = decodePayload<ReadAllocation>(payload);
        const auto id = static_cast<uint64_t>(request.allocation.value);
        auto &bytes = allocations_.at(id);
        const auto offset = static_cast<uint64_t>(request.offset.value);
        const auto count = static_cast<size_t>(request.size.value);
        GEMSTONE_RPC_REQUIRE(offset + count <= bytes.size());
        encodePayload(result, std::vector<uint8_t>(bytes.begin() + offset, bytes.begin() + offset + count));
      } else if (operationTag == operation::LoadProgram) {
        const auto request = decodePayload<LoadProgram>(payload);
        lastProgramImage_ = request.image;
        ++programLoads_;
        encodePayload(result, ProgramId{91});
      } else if (operationTag == operation::DescribeSpecialization) {
        decodePayload<DescribeSpecialization>(payload);
        std::vector<SpecializationInfo> infos;
        for (const auto &[id, byteSize] : specializationSizes_)
          infos.push_back(SpecializationInfo{Binary32{static_cast<int64_t>(id)}, IntPositive{static_cast<int32_t>(byteSize)}});
        encodePayload(result, infos);
      } else if (operationTag == operation::SpecializeImage) {
        const auto request = decodePayload<SpecializeImage>(payload);
        std::vector<std::pair<uint32_t, std::vector<uint8_t>>> constants;
        for (const auto &constant : request.constants)
          constants.emplace_back(static_cast<uint32_t>(constant.specId.value), constant.value);
        lastSpecialization_ = std::move(constants);
        encodePayload(result, request.image);
      } else if (operationTag == operation::PrepareShader) {
        const auto request = decodePayload<PrepareShader>(payload);
        GEMSTONE_RPC_REQUIRE(shaderDescriptors_.has_value());
        GEMSTONE_RPC_REQUIRE(!request.entry.value.empty());
        lastSpecialization_.clear();
        for (const auto &constant : request.constants)
          lastSpecialization_.emplace_back(static_cast<uint32_t>(constant.specId.value), constant.value);
        PreparedShader prepared{};
        prepared.image = request.image;
        prepared.entry = {request.entry.value};
        prepared.stage = request.stage;
        prepared.descriptors = *shaderDescriptors_;
        if (requiredWorkGroupSize_)
          prepared.workgroup =
              ShaderWorkgroup{static_cast<int32_t>((*requiredWorkGroupSize_)[0]), static_cast<int32_t>((*requiredWorkGroupSize_)[1]),
                              static_cast<int32_t>((*requiredWorkGroupSize_)[2])};
        encodePayload(result, prepared);
      } else if (operationTag == operation::UnloadProgram) {
        encodePayload(result, Unit{});
      } else if (operationTag == operation::DescribeKernel) {
        const auto request = decodePayload<DescribeKernel>(payload);
        GEMSTONE_RPC_REQUIRE(request.program.value == 91 && !request.name.value.empty());
        appendKernelMetadata(result);
      } else if (operationTag == operation::DescribeImage) {
        const auto request = decodePayload<DescribeImage>(payload);
        GEMSTONE_RPC_REQUIRE(!request.name.value.empty());
        appendKernelMetadata(result);
      } else if (operationTag == operation::DescribeProgram) {
        const auto request = decodePayload<DescribeProgram>(payload);
        GEMSTONE_RPC_REQUIRE(request.program.value == 91);
        ProgramSymbols catalog{};
        for (const auto &kernel : programKernels_)
          catalog.kernels.push_back(gemstone::execution::KernelSymbol{{kernel.name}, {kernel.linkageName}});
        for (const auto &global : programGlobals_) {
          GlobalSymbol meta{};
          meta.name = {global.name};
          meta.virtualAddress = GuestAddress{static_cast<int64_t>(global.virtualAddress)};
          meta.size = LongPositive0{static_cast<int64_t>(global.size)};
          for (uint32_t offset : global.pointerOffsets)
            meta.pointerOffsets.push_back(IntPositive0{static_cast<int32_t>(offset)});
          catalog.globals.push_back(std::move(meta));
        }
        programGlobals_.clear();
        encodePayload(result, catalog);
      } else if (operationTag == operation::BindProgramGlobal) {
        const auto request = decodePayload<BindProgramGlobal>(payload);
        const auto name = request.name.value;
        const auto allocation = static_cast<uint64_t>(request.allocation.value);
        const auto offset = static_cast<uint64_t>(request.offset.value);
        auto expected = expectedGlobals_.find(name);
        GEMSTONE_RPC_REQUIRE(expected != expectedGlobals_.end() && expected->second.first == offset);
        auto &memory = allocations_.at(allocation);
        const auto &initial = expected->second.second;
        GEMSTONE_RPC_REQUIRE(offset <= memory.size() && initial.size() <= memory.size() - offset);
        std::copy(initial.begin(), initial.end(), memory.begin() + offset);
        expectedGlobals_.erase(expected);
        encodePayload(result, Unit{});
      } else if (operationTag == operation::Launch) {
        launch(payload);
        encodePayload(result, CompletionId{static_cast<int64_t>(completion_)});
      } else if (operationTag == operation::QueryCompletion) {
        const auto request = decodePayload<QueryCompletion>(payload);
        GEMSTONE_RPC_REQUIRE(request.completion.value == static_cast<int64_t>(completion_));
        if (polls_++ < 2) {
          ++pending_;
          encodePayload(result, CompletionStatus{CompletionStatusPending{}});
        } else {
          completed_ = true;
          if (failCompletion_.exchange(false))
            encodePayload(result, CompletionStatus{CompletionStatusFailed{ExecutionFailure{ExecutionFailureFailed{"completion failed"}}}});
          else encodePayload(result, CompletionStatus{CompletionStatusSucceeded{}});
        }
      } else if (operationTag == operation::PollHostMemory || operationTag == operation::AwaitCompletion) {
        const auto completion = operationTag == operation::PollHostMemory ? decodePayload<PollHostMemory>(payload).completion
                                                                          : decodePayload<AwaitCompletion>(payload).completion;
        GEMSTONE_RPC_REQUIRE(completion.value == static_cast<int64_t>(completion_));
        if (pauseCompletion_.load()) {
          completionEntered_ = true;
          while (pauseCompletion_.load() && !stopped_.load())
            std::this_thread::yield();
        }
        if (!failCompletion_.load()) {
          if (hostBacked_[outputId_])
            for (const auto &range : ranges_)
              std::memcpy(reinterpret_cast<void *>(allocationAddresses_.at(outputId_) + range.first),
                          allocations_.at(outputId_).data() + range.first, range.second);
          if (indirectWrite_ && hostBacked_[indirectWrite_->first])
            std::memcpy(reinterpret_cast<void *>(allocationAddresses_.at(indirectWrite_->first) + indirectWrite_->second),
                        allocations_.at(indirectWrite_->first).data() + indirectWrite_->second, 4);
        }
        ++awaited_;
        completed_ = true;
        failed = failCompletion_.exchange(false);
        if (operationTag == operation::PollHostMemory && !failed) {
          HostMemoryBatch batch{};
          batch.completed = true;
          if (hostOutputAddress_)
            for (const auto &range : ranges_) {
              HostMemoryBytes bytes{};
              bytes.address = GuestAddress{static_cast<int64_t>(hostOutputAddress_ + range.first)};
              bytes.bytes.assign(hostOutputBytes_.begin() + range.first, hostOutputBytes_.begin() + range.first + range.second);
              bytes.atomicWidth = {0};
              batch.writes.push_back(std::move(bytes));
            }
          encodePayload(result, batch);
        } else encodePayload(result, Unit{});
      } else if (operationTag == operation::ReleaseCompletion) {
        const auto request = decodePayload<ReleaseCompletion>(payload);
        GEMSTONE_RPC_REQUIRE(completed_ && request.completion.value == static_cast<int64_t>(completion_));
        active_ = false;
        encodePayload(result, Unit{});
      } else if (operationTag == operation::CompletionDirtyRanges) {
        const auto request = decodePayload<CompletionDirtyRanges>(payload);
        GEMSTONE_RPC_REQUIRE(completed_);
        std::vector<DirtyRange> dirty;
        if (!hostOutputAddress_ && !hostBacked_[outputId_])
          for (const auto &range : ranges_)
            dirty.push_back(DirtyRange{AllocationId{static_cast<int64_t>(outputId_)}, LongPositive0{static_cast<int64_t>(range.first)},
                                       IntPositive{static_cast<int32_t>(range.second)}});
        if (indirectWrite_ && !hostBacked_[indirectWrite_->first])
          dirty.push_back(DirtyRange{AllocationId{static_cast<int64_t>(indirectWrite_->first)},
                                     LongPositive0{static_cast<int64_t>(indirectWrite_->second)}, IntPositive{4}});
        const auto begin = std::min(static_cast<size_t>(request.offset.value), dirty.size());
        const auto end = std::min(begin + MaximumElements, dirty.size());
        encodePayload(result, std::vector<DirtyRange>(dirty.begin() + begin, dirty.begin() + end));
      } else if (operationTag == operation::CompletionPointerRelocations) {
        decodePayload<CompletionPointerRelocations>(payload);
        GEMSTONE_RPC_REQUIRE(completed_);
        encodePayload(result, std::vector<PointerRelocation>{});
      } else if (operationTag == operation::ReadGraphicsStatistics) {
        const auto request = decodePayload<ReadGraphicsStatistics>(payload);
        GEMSTONE_RPC_REQUIRE(completed_ && active_ && request.completion.value == static_cast<int64_t>(completion_));
        failed = failGraphicsStatistics_.exchange(false);
        // A distinctive fixture result, independent of simulated compute writes.
        encodePayload(result, GraphicsStatistics{17});
      } else if (operationTag == operation::CompletionPrints) {
        decodePayload<CompletionPrints>(payload);
        GEMSTONE_RPC_REQUIRE(completed_);
        encodePayload(result, std::string{});
      } else {
        GEMSTONE_RPC_REQUIRE(false);
      }
      std::vector<uint8_t> response;
      wire::appendBigEndian<uint32_t>(response, ResponseMagic);
      wire::appendBigEndian<uint32_t>(response, failed ? 1 : 0);
      if (failed) {
        result.clear();
        encodePayload(result, ExecutionFailure{ExecutionFailureFailed{"completion failed"}});
      }
      wire::appendBigEndian<uint32_t>(response, static_cast<uint32_t>(result.size()));
      response.insert(response.end(), result.begin(), result.end());
      send(socket, response);
    }
  }

  void launch(const std::vector<uint8_t> &payload) {
    GEMSTONE_RPC_REQUIRE(!active_);
    const auto request = decodePayload<Launch>(payload);
    if (indirectInput_) {
      uint64_t child = 0;
      bool foundTable = false;
      for (const auto &[id, address] : allocationAddresses_)
        if (address == indirectInput_->table) {
          const auto &bytes = allocations_.at(id);
          GEMSTONE_RPC_REQUIRE(bytes.size() >= sizeof(child));
          std::memcpy(&child, hostBacked_[id] ? reinterpret_cast<const void *>(address) : bytes.data(), sizeof(child));
          foundTable = true;
        }
      GEMSTONE_RPC_REQUIRE(foundTable);
      bool foundChild = false;
      for (const auto &[id, address] : allocationAddresses_) {
        auto &bytes = allocations_.at(id);
        if (child < address || child - address > bytes.size() || bytes.size() - (child - address) < 4) continue;
        uint32_t value = 0;
        std::memcpy(&value, hostBacked_[id] ? reinterpret_cast<const void *>(child) : bytes.data() + child - address, sizeof(value));
        GEMSTONE_RPC_REQUIRE(value == indirectInput_->value);
        if (indirectInput_->deviceWrite) {
          std::memcpy(bytes.data() + child - address, &*indirectInput_->deviceWrite, 4);
          indirectWrite_ = std::make_pair(id, child - address);
        }
        foundChild = true;
        break;
      }
      GEMSTONE_RPC_REQUIRE(foundChild);
    }
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupSize.x.value) == expected_.local);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupSize.y.value) == expected_.local_yz[0]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupSize.z.value) == expected_.local_yz[1]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupCount.x.value) == expected_.groups);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupCount.y.value) == expected_.groups_yz[0]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.groupCount.z.value) == expected_.groups_yz[1]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint64_t>(request.globalSize.x.value) == expected_.global);
    GEMSTONE_RPC_REQUIRE(static_cast<uint64_t>(request.globalSize.y.value) == expected_.global_yz[0]);
    GEMSTONE_RPC_REQUIRE(static_cast<uint64_t>(request.globalSize.z.value) == expected_.global_yz[1]);
    GEMSTONE_RPC_REQUIRE(request.workDim.value >= 1 && request.workDim.value <= 3);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.globalOffset.x.value) == expected_.offset);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.globalOffset.y.value) == 0);
    GEMSTONE_RPC_REQUIRE(static_cast<uint32_t>(request.globalOffset.z.value) == 0);
    auto arguments = request.arguments;
    if (request.argumentBuffer) {
      GEMSTONE_RPC_REQUIRE(arguments.empty());
      const auto id = static_cast<uint64_t>(request.argumentBuffer->region.value);
      const auto offset = static_cast<uint64_t>(request.argumentBuffer->offset.value);
      const auto &storage = allocations_.at(id);
      // This fixture's kernel takes a pointer and a 32-bit seed, then optional trailing bytes.
      const size_t size = 12 + (trailingArguments_ ? trailingArguments_->size() : 0);
      GEMSTONE_RPC_REQUIRE(offset <= storage.size() && size <= storage.size() - offset);
      const auto *bytes =
          hostBacked_[id] ? reinterpret_cast<const uint8_t *>(allocationAddresses_.at(id) + offset) : storage.data() + offset;
      uint64_t pointer = 0;
      std::memcpy(&pointer, bytes, sizeof(pointer));
      std::optional<ArgumentMemory> output;
      for (const auto &[allocation, address] : allocationAddresses_)
        if (pointer >= address && pointer - address <= allocations_.at(allocation).size()) {
          output = ArgumentMemory{AllocationId{static_cast<int64_t>(allocation)}, static_cast<int64_t>(pointer - address)};
          break;
        }
      if (output) arguments.emplace_back(*output);
      else arguments.emplace_back(ArgumentScalar{std::vector<uint8_t>(bytes, bytes + 8)});
      arguments.emplace_back(ArgumentScalar{std::vector<uint8_t>(bytes + 8, bytes + 12)});
      if (size > 12) arguments.emplace_back(ArgumentScalar{std::vector<uint8_t>(bytes + 12, bytes + size)});
    }
    if (allocationArguments_) {
      GEMSTONE_RPC_REQUIRE(arguments.size() == allocationArguments_->size());
      GEMSTONE_RPC_REQUIRE(request.pointerRelocations.empty());
      std::map<size_t, uint64_t> aliases;
      std::vector<std::pair<uint64_t, uint64_t>> actual;
      for (size_t index = 0; index < arguments.size(); ++index) {
        const auto *memory = std::get_if<ArgumentMemory>(&arguments[index]);
        GEMSTONE_RPC_REQUIRE(memory != nullptr);
        const auto expected = allocationArguments_->at(index);
        const auto id = static_cast<uint64_t>(memory->region.value);
        const auto offset = static_cast<uint64_t>(memory->offset.value);
        GEMSTONE_RPC_REQUIRE((memory->range ? std::optional<uint64_t>(memory->range->value) : std::nullopt) == expected.range);
        const auto [alias, inserted] = aliases.emplace(expected.alias, id);
        GEMSTONE_RPC_REQUIRE((inserted || alias->second == id) && offset == expected.offset);
        actual.emplace_back(id, offset);
      }
      if (allocationArgumentWrite_) {
        GEMSTONE_RPC_REQUIRE(allocationArgumentWrite_->argument < actual.size());
        const auto [id, base] = actual[allocationArgumentWrite_->argument];
        auto &bytes = allocations_.at(id);
        const auto offset = base + allocationArgumentWrite_->offset;
        GEMSTONE_RPC_REQUIRE(offset <= bytes.size() && allocationArgumentWrite_->bytes.size() <= bytes.size() - offset);
        std::copy(allocationArgumentWrite_->bytes.begin(), allocationArgumentWrite_->bytes.end(), bytes.begin() + offset);
        if (hostBacked_[id])
          std::memcpy(reinterpret_cast<void *>(allocationAddresses_.at(id) + offset), allocationArgumentWrite_->bytes.data(),
                      allocationArgumentWrite_->bytes.size());
      }
      outputId_ = actual.empty() ? 0 : actual.front().first;
      ranges_.clear();
      ++completion_;
      ++launches_;
      polls_ = 0;
      active_ = true;
      completed_ = false;
      return;
    }
    GEMSTONE_RPC_REQUIRE(arguments.size() >= 2);
    hostOutputAddress_ = 0;
    uint64_t base = 0;
    if (const auto *memory = std::get_if<ArgumentMemory>(&arguments[0])) {
      outputId_ = static_cast<uint64_t>(memory->region.value);
      base = static_cast<uint64_t>(memory->offset.value);
      GEMSTONE_RPC_REQUIRE(!memory->range);
    } else {
      const auto *scalar = std::get_if<ArgumentScalar>(&arguments[0]);
      GEMSTONE_RPC_REQUIRE(scalar != nullptr && hostMemoryEnabled_ && scalar->bytes.size() == 8);
      std::memcpy(&hostOutputAddress_, scalar->bytes.data(), 8);
      GEMSTONE_RPC_REQUIRE(hostOutputAddress_ != 0);
      hostOutputBytes_.resize(expected_.global * expected_.global_yz[0] * expected_.global_yz[1] * 4);
      outputId_ = 0;
    }
    uint32_t seed = 0;
    {
      const auto *scalar = std::get_if<ArgumentScalar>(&arguments[1]);
      GEMSTONE_RPC_REQUIRE(scalar != nullptr && scalar->bytes.size() == 4);
      std::memcpy(&seed, scalar->bytes.data(), 4);
      GEMSTONE_RPC_REQUIRE(seed == expected_.seed);
    }
    if (trailingArguments_) {
      std::vector<uint8_t> bytes;
      for (size_t index = 2; index < arguments.size(); ++index) {
        const auto *scalar = std::get_if<ArgumentScalar>(&arguments[index]);
        GEMSTONE_RPC_REQUIRE(scalar != nullptr);
        bytes.insert(bytes.end(), scalar->bytes.begin(), scalar->bytes.end());
      }
      GEMSTONE_RPC_REQUIRE(bytes == *trailingArguments_);
    }

    auto &bytes = hostOutputAddress_ ? hostOutputBytes_ : allocations_.at(outputId_);
    ranges_.clear();
    if (!expected_.geometry_only) {
      for (uint32_t groupZ = 0; groupZ < expected_.groups_yz[1]; ++groupZ)
        for (uint32_t groupY = 0; groupY < expected_.groups_yz[0]; ++groupY)
          for (uint32_t groupX = 0; groupX < expected_.groups; ++groupX) {
            const uint32_t firstX = groupX * expected_.local;
            const uint32_t firstY = groupY * expected_.local_yz[0];
            const uint32_t firstZ = groupZ * expected_.local_yz[1];
            const auto countX = std::min(expected_.local, expected_.global - firstX);
            const auto countY = std::min(expected_.local_yz[0], expected_.global_yz[0] - firstY);
            const auto countZ = std::min(expected_.local_yz[1], expected_.global_yz[1] - firstZ);
            for (uint32_t z = 0; z < countZ; ++z)
              for (uint32_t y = 0; y < countY; ++y) {
                const uint64_t first = ((uint64_t(firstZ + z) * expected_.global_yz[0]) + firstY + y) * expected_.global + firstX;
                GEMSTONE_RPC_REQUIRE(base + (first + countX) * 4 <= bytes.size());
                for (uint32_t x = 0; x < countX; ++x) {
                  const uint32_t value = seed + expected_.offset + static_cast<uint32_t>(first + x);
                  std::memcpy(bytes.data() + base + (first + x) * 4, &value, 4);
                }
                ranges_.emplace_back(base + first * 4, countX * 4);
              }
          }
    }
    ++completion_;
    ++launches_;
    polls_ = 0;
    active_ = true;
    completed_ = false;
  }

  std::string path_, directory_;
  int listener_ = -1;
  uint16_t port_ = 0;
  std::atomic<bool> stopped_{false};
  std::atomic<unsigned> reads_{0};
  std::thread acceptor_;
  mutable std::mutex failureMutex_;
  std::exception_ptr failure_;
  std::mutex connectionsMutex_, stateMutex_;
  std::vector<int> connections_;
  std::vector<std::thread> workers_;
  Expected expected_{};
  std::optional<std::vector<uint8_t>> trailingArguments_;
  struct AllocationArgumentWrite {
    size_t argument;
    uint64_t offset;
    std::vector<uint8_t> bytes;
  };
  std::optional<std::vector<ExpectedAllocationArgument>> allocationArguments_;
  std::optional<AllocationArgumentWrite> allocationArgumentWrite_;
  uint32_t kernelArgumentBlockSize_ = 12;
  uint32_t kernelExplicitParameterCount_ = 2;
  std::vector<KernelParameter> kernelParameters_{{8, 0, 2, 0}, {4, 8, 1, 0}};
  std::optional<std::array<uint32_t, 3>> requiredWorkGroupSize_;
  std::optional<std::vector<ShaderDescriptor>> shaderDescriptors_;
  std::vector<uint32_t> bindingIndices_;
  std::vector<ProgramKernel> programKernels_{{"add", "add.kd"}};
  std::vector<ProgramGlobal> programGlobals_;
  struct IndirectInput {
    uint64_t table;
    uint32_t value;
    std::optional<uint32_t> deviceWrite;
  };
  std::optional<IndirectInput> indirectInput_;
  std::optional<std::pair<uint64_t, uint64_t>> indirectWrite_;
  std::map<uint64_t, uint64_t> writtenBytes_;
  std::map<uint64_t, std::vector<uint8_t>> allocations_;
  std::vector<uint8_t> lastProgramImage_;
  std::map<uint32_t, uint32_t> specializationSizes_;
  std::vector<std::pair<uint32_t, std::vector<uint8_t>>> lastSpecialization_;
  std::map<uint64_t, uint64_t> allocationAddresses_;
  std::map<uint64_t, bool> hostBacked_;
  std::map<std::string, std::pair<uint64_t, std::vector<uint8_t>>> expectedGlobals_;
  std::vector<std::pair<uint64_t, uint32_t>> ranges_;
  std::atomic_bool failGraphicsStatistics_{false};
  std::atomic_bool failCompletion_{false};
  std::atomic_bool pauseCompletion_{false}, completionEntered_{false};
  std::atomic_uint64_t pauseReleaseAddress_{0};
  std::atomic_bool releaseEntered_{false};
  bool hostMemoryEnabled_ = false;
  uint64_t hostOutputAddress_ = 0;
  std::vector<uint8_t> hostOutputBytes_;
  uint64_t nextAllocation_ = 1, outputId_ = 0, completion_ = 100;
  uint32_t polls_ = 0;
  std::atomic<unsigned> launches_{0}, pending_{0}, awaited_{0}, programLoads_{0};
  bool active_ = false, completed_ = false;
};
class RpcEnvironment {
  Environment endpoint;
  Environment client;

public:
  explicit RpcEnvironment(const std::string &endpoint)
      : endpoint(gemstone::environment::RpcEndpoint, endpoint.c_str()),
        client(gemstone::environment::RpcClient, std::to_string(RpcFixture::clientId).c_str()) {}
  explicit RpcEnvironment(const RpcFixture &fixture) : RpcEnvironment(fixture.endpoint()) {}
};
} // namespace gemstone_test
#undef GEMSTONE_RPC_REQUIRE
#endif
