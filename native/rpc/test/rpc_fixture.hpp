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

#include "../../generated/rpc/gemstone_execution_rpc.hpp"
#include "wire.hpp"
#include <arpa/inet.h>
#include <netinet/tcp.h>
#include <sys/socket.h>

#define GEMSTONE_RPC_REQUIRE(condition)                                                                                                    \
  do {                                                                                                                                     \
    if (!(condition)) throw std::runtime_error(std::string(__FILE__) + ":" + std::to_string(__LINE__) + ": " + #condition);                \
  } while (false)

namespace gemstone_test {

using namespace gemstone::execution;

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
  RpcFixture() {
    listener_ = socket(AF_INET, SOCK_STREAM, 0);
    GEMSTONE_RPC_REQUIRE(listener_ >= 0);
    sockaddr_in address{};
    address.sin_family = AF_INET;
    address.sin_addr.s_addr = htonl(INADDR_LOOPBACK);
    GEMSTONE_RPC_REQUIRE(bind(listener_, reinterpret_cast<sockaddr *>(&address), sizeof(address)) == 0);
    GEMSTONE_RPC_REQUIRE(listen(listener_, 8) == 0);
    socklen_t length = sizeof(address);
    GEMSTONE_RPC_REQUIRE(getsockname(listener_, reinterpret_cast<sockaddr *>(&address), &length) == 0);
    port_ = ntohs(address.sin_port);
    acceptor_ = std::thread([this] {
      try {
        while (!stopped_.load()) {
          const int connection = accept(listener_, nullptr, nullptr);
          if (connection < 0) {
            GEMSTONE_RPC_REQUIRE(stopped_.load());
            break;
          }
          const int enabled = 1;
          GEMSTONE_RPC_REQUIRE(setsockopt(connection, IPPROTO_TCP, TCP_NODELAY, &enabled, sizeof(enabled)) == 0);
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
  }
  uint16_t port() const { return port_; }
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
  void expectGlobalBinding(std::string name, uint64_t offset, std::vector<uint8_t> initial) {
    std::lock_guard lock(stateMutex_);
    expectedGlobals_.emplace(std::move(name), std::make_pair(offset, std::move(initial)));
  }
  void checkAllocationAddress(const void *pointer, bool present = true) {
    std::lock_guard lock(stateMutex_);
    const auto address = reinterpret_cast<uint64_t>(pointer);
    const auto found =
        std::find_if(allocationAddresses_.begin(), allocationAddresses_.end(), [&](const auto &entry) { return entry.second == address; });
    GEMSTONE_RPC_REQUIRE((found != allocationAddresses_.end()) == present);
  }
  void checkAllocationWord(const void *base, size_t offset, uint64_t expected) {
    std::lock_guard lock(stateMutex_);
    const auto found = std::find_if(allocationAddresses_.begin(), allocationAddresses_.end(),
                                    [&](const auto &entry) { return entry.second == reinterpret_cast<uint64_t>(base); });
    GEMSTONE_RPC_REQUIRE(found != allocationAddresses_.end());
    const auto &bytes = allocations_.at(found->first);
    GEMSTONE_RPC_REQUIRE(offset <= bytes.size() && bytes.size() - offset >= sizeof(uint64_t));
    uint64_t actual = 0;
    std::memcpy(&actual, bytes.data() + offset, sizeof(actual));
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
  void failNextCompletion() { failCompletion_ = true; }
  unsigned launches() const { return launches_.load(); }
  unsigned programLoads() const { return programLoads_.load(); }

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

  static void putString(std::vector<uint8_t> &v, const std::string &value) {
    wire::appendBigEndian<uint32_t>(v, static_cast<uint32_t>(value.size()));
    v.insert(v.end(), value.begin(), value.end());
  }
  static uint64_t parameterKind(uint32_t value) {
    if (value == 1) return static_cast<uint64_t>(KernelParameterKind::Scalar);
    if (value == 2) return static_cast<uint64_t>(KernelParameterKind::Allocation);
    if (value == 3) return static_cast<uint64_t>(KernelParameterKind::Local);
    GEMSTONE_RPC_REQUIRE(false);
    return 0;
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
    std::array<uint8_t, RequestHeaderBytes> header{};
    while (receive(socket, header.data(), header.size())) {
      GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint32_t>(header.data()) == RequestMagic
                           && wire::readBigEndian<uint64_t>(header.data() + RequestFingerprintOffset) == SchemaFingerprint);
      GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint64_t>(header.data() + RequestClientOffset) == clientId);
      const auto operationId = wire::readBigEndian<uint64_t>(header.data() + RequestOperationOffset);
      const auto size = wire::readBigEndian<uint32_t>(header.data() + RequestPayloadSizeOffset);
      GEMSTONE_RPC_REQUIRE(size <= MaximumPayload);
      std::vector<uint8_t> payload(size);
      if (size && !receive(socket, payload.data(), size)) break;
      std::lock_guard lock(stateMutex_);
      std::vector<uint8_t> result;
      bool failed = false;
      switch (operationId) {
        case operation::Ping: break;
        case operation::RegisterAllocation: {
          GEMSTONE_RPC_REQUIRE(size == 16);
          const auto id = nextAllocation_++;
          GEMSTONE_RPC_REQUIRE(id != 0);
          allocations_[id].resize(wire::readBigEndian<uint64_t>(payload.data() + 8));
          wire::appendBigEndian<uint64_t>(result, id);
          break;
        }
        case operation::ReleaseAllocation:
          GEMSTONE_RPC_REQUIRE(allocations_.erase(wire::readBigEndian<uint64_t>(payload.data())) == 1);
          allocationAddresses_.erase(wire::readBigEndian<uint64_t>(payload.data()));
          break;
        case operation::RegisterAllocationAddress: {
          GEMSTONE_RPC_REQUIRE(size == 16);
          const auto id = wire::readBigEndian<uint64_t>(payload.data()), base = wire::readBigEndian<uint64_t>(payload.data() + 8);
          GEMSTONE_RPC_REQUIRE(allocations_.count(id) == 1 && base != 0);
          GEMSTONE_RPC_REQUIRE(allocationAddresses_.emplace(id, base).second);
          break;
        }
        case operation::WriteAllocation: {
          auto &bytes = allocations_.at(wire::readBigEndian<uint64_t>(payload.data()));
          const auto offset = wire::readBigEndian<uint64_t>(payload.data() + 8);
          const auto count = wire::readBigEndian<uint32_t>(payload.data() + 16);
          GEMSTONE_RPC_REQUIRE(offset + count <= bytes.size() && size == 20 + count);
          std::memcpy(bytes.data() + offset, payload.data() + 20, count);
          writtenBytes_[wire::readBigEndian<uint64_t>(payload.data())] += count;
          break;
        }
        case operation::ReadAllocation: {
          GEMSTONE_RPC_REQUIRE(!active_ || completed_);
          auto &bytes = allocations_.at(wire::readBigEndian<uint64_t>(payload.data()));
          const auto offset = wire::readBigEndian<uint64_t>(payload.data() + 8);
          const auto count = wire::readBigEndian<uint32_t>(payload.data() + 16);
          GEMSTONE_RPC_REQUIRE(offset + count <= bytes.size());
          wire::appendBigEndian<uint32_t>(result, count);
          result.insert(result.end(), bytes.begin() + offset, bytes.begin() + offset + count);
          break;
        }
        case operation::LoadProgram:
          ++programLoads_;
          wire::appendBigEndian<uint64_t>(result, 91);
          break;
        case operation::UnloadProgram: break;
        case operation::DescribeKernel: {
          GEMSTONE_RPC_REQUIRE(size >= 12 && wire::readBigEndian<uint64_t>(payload.data()) == 91);
          const auto nameSize = wire::readBigEndian<uint32_t>(payload.data() + 8);
          GEMSTONE_RPC_REQUIRE(nameSize != 0 && size == 12 + nameSize);
          wire::appendBigEndian<uint32_t>(result, kernelArgumentBlockSize_);
          wire::appendBigEndian<uint32_t>(result, kernelExplicitParameterCount_);
          wire::appendBigEndian<uint32_t>(result, static_cast<uint32_t>(kernelParameters_.size()));
          for (const auto &parameter : kernelParameters_) {
            wire::appendBigEndian<uint32_t>(result, parameter.width);
            wire::appendBigEndian<uint32_t>(result, parameter.offset);
            wire::appendBigEndian<uint64_t>(result, parameterKind(parameter.kind));
            result.push_back(static_cast<uint8_t>(parameter.containsPointers));
          }
          break;
        }
        case operation::DescribeProgram: {
          GEMSTONE_RPC_REQUIRE(size == 8 && wire::readBigEndian<uint64_t>(payload.data()) == 91);
          wire::appendBigEndian<uint32_t>(result, static_cast<uint32_t>(programKernels_.size()));
          for (const auto &kernel : programKernels_) {
            putString(result, kernel.name);
            putString(result, kernel.linkageName);
          }
          wire::appendBigEndian<uint32_t>(result, static_cast<uint32_t>(programGlobals_.size()));
          for (const auto &global : programGlobals_) {
            putString(result, global.name);
            result.push_back(1);
            wire::appendBigEndian<uint64_t>(result, global.virtualAddress);
            wire::appendBigEndian<uint64_t>(result, global.size);
            wire::appendBigEndian<uint32_t>(result, static_cast<uint32_t>(global.pointerOffsets.size()));
            for (uint32_t offset : global.pointerOffsets)
              wire::appendBigEndian<uint32_t>(result, offset);
          }
          programGlobals_.clear();
          break;
        }
        case operation::BindProgramGlobal: {
          GEMSTONE_RPC_REQUIRE(size >= 28 && wire::readBigEndian<uint64_t>(payload.data()) == 91);
          const auto nameSize = wire::readBigEndian<uint32_t>(payload.data() + 8);
          GEMSTONE_RPC_REQUIRE(size == 28 + nameSize);
          const std::string name(reinterpret_cast<const char *>(payload.data() + 12), nameSize);
          const auto allocation = wire::readBigEndian<uint64_t>(payload.data() + 12 + nameSize);
          const auto offset = wire::readBigEndian<uint64_t>(payload.data() + 20 + nameSize);
          auto expected = expectedGlobals_.find(name);
          GEMSTONE_RPC_REQUIRE(expected != expectedGlobals_.end() && expected->second.first == offset);
          auto &memory = allocations_.at(allocation);
          const auto &initial = expected->second.second;
          GEMSTONE_RPC_REQUIRE(offset <= memory.size() && initial.size() <= memory.size() - offset);
          std::copy(initial.begin(), initial.end(), memory.begin() + offset);
          expectedGlobals_.erase(expected);
          break;
        }
        case operation::Launch:
          launch(payload);
          wire::appendBigEndian<uint64_t>(result, completion_);
          break;
        case operation::QueryCompletion:
          GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint64_t>(payload.data()) == completion_);
          if (polls_++ < 2) {
            ++pending_;
            wire::appendBigEndian<uint64_t>(result, tag::CompletionStatusPending);
          } else {
            completed_ = true;
            if (failCompletion_.exchange(false)) {
              wire::appendBigEndian<uint64_t>(result, tag::CompletionStatusFailed);
              wire::appendBigEndian<uint64_t>(result, tag::ExecutionFailureFailed);
              putString(result, "completion failed");
            } else wire::appendBigEndian<uint64_t>(result, tag::CompletionStatusSucceeded);
          }
          break;
        case operation::AwaitCompletion:
          GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint64_t>(payload.data()) == completion_);
          ++awaited_;
          completed_ = true;
          failed = failCompletion_.exchange(false);
          break;
        case operation::ReleaseCompletion:
          GEMSTONE_RPC_REQUIRE(completed_ && wire::readBigEndian<uint64_t>(payload.data()) == completion_);
          active_ = false;
          break;
        case operation::CompletionDirtyRanges:
          GEMSTONE_RPC_REQUIRE(completed_);
          wire::appendBigEndian<uint32_t>(result, static_cast<uint32_t>(ranges_.size() + (indirectWrite_ ? 1 : 0)));
          for (const auto &range : ranges_) {
            wire::appendBigEndian<uint64_t>(result, outputId_);
            wire::appendBigEndian<uint64_t>(result, range.first);
            wire::appendBigEndian<uint32_t>(result, range.second);
          }
          if (indirectWrite_) {
            wire::appendBigEndian<uint64_t>(result, indirectWrite_->first);
            wire::appendBigEndian<uint64_t>(result, indirectWrite_->second);
            wire::appendBigEndian<uint32_t>(result, 4);
          }
          break;
        case operation::CompletionPointerRelocations:
          GEMSTONE_RPC_REQUIRE(completed_);
          wire::appendBigEndian<uint32_t>(result, 0);
          break;
        case operation::CompletionPrints:
          GEMSTONE_RPC_REQUIRE(completed_);
          wire::appendBigEndian<uint32_t>(result, 0);
          break;
        default: GEMSTONE_RPC_REQUIRE(false);
      }
      std::vector<uint8_t> response;
      wire::appendBigEndian<uint32_t>(response, ResponseMagic);
      wire::appendBigEndian<uint64_t>(response, SchemaFingerprint);
      wire::appendBigEndian<uint32_t>(response, failed ? 1 : 0);
      if (failed) {
        result.clear();
        wire::appendBigEndian<uint64_t>(result, tag::ExecutionFailureFailed);
        putString(result, "completion failed");
      }
      wire::appendBigEndian<uint32_t>(response, static_cast<uint32_t>(result.size()));
      response.insert(response.end(), result.begin(), result.end());
      send(socket, response);
    }
  }
  void launch(const std::vector<uint8_t> &payload) {
    GEMSTONE_RPC_REQUIRE(!active_);
    if (indirectInput_) {
      uint64_t child = 0;
      bool foundTable = false;
      for (const auto &[id, address] : allocationAddresses_)
        if (address == indirectInput_->table) {
          const auto &bytes = allocations_.at(id);
          GEMSTONE_RPC_REQUIRE(bytes.size() >= sizeof(child));
          std::memcpy(&child, bytes.data(), sizeof(child));
          foundTable = true;
        }
      GEMSTONE_RPC_REQUIRE(foundTable);
      bool foundChild = false;
      for (const auto &[id, address] : allocationAddresses_) {
        auto &bytes = allocations_.at(id);
        if (child < address || child - address > bytes.size() || bytes.size() - (child - address) < 4) continue;
        uint32_t value = 0;
        std::memcpy(&value, bytes.data() + child - address, sizeof(value));
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
    size_t cursor = 8;
    const auto nameSize = wire::readBigEndian<uint32_t>(payload.data() + cursor);
    cursor += 4 + nameSize;
    auto take = [&]() {
      GEMSTONE_RPC_REQUIRE(cursor + 4 <= payload.size());
      auto value = wire::readBigEndian<uint32_t>(payload.data() + cursor);
      cursor += 4;
      return value;
    };
    GEMSTONE_RPC_REQUIRE(take() == expected_.local);
    GEMSTONE_RPC_REQUIRE(take() == expected_.local_yz[0]);
    GEMSTONE_RPC_REQUIRE(take() == expected_.local_yz[1]);
    GEMSTONE_RPC_REQUIRE(take() == expected_.groups);
    GEMSTONE_RPC_REQUIRE(take() == expected_.groups_yz[0]);
    GEMSTONE_RPC_REQUIRE(take() == expected_.groups_yz[1]);
    auto takeGlobal = [&]() {
      GEMSTONE_RPC_REQUIRE(cursor + 8 <= payload.size());
      auto value = wire::readBigEndian<uint64_t>(payload.data() + cursor);
      cursor += 8;
      return value;
    };
    GEMSTONE_RPC_REQUIRE(takeGlobal() == expected_.global);
    GEMSTONE_RPC_REQUIRE(takeGlobal() == expected_.global_yz[0]);
    GEMSTONE_RPC_REQUIRE(takeGlobal() == expected_.global_yz[1]);
    const auto dim = take();
    GEMSTONE_RPC_REQUIRE(dim >= 1 && dim <= 3);
    GEMSTONE_RPC_REQUIRE(take() == expected_.offset);
    GEMSTONE_RPC_REQUIRE(take() == 0);
    GEMSTONE_RPC_REQUIRE(take() == 0);
    take();
    take();
    const auto argumentCount = take();
    GEMSTONE_RPC_REQUIRE(argumentCount >= 2);
    GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint64_t>(payload.data() + cursor) == tag::KernelArgumentAllocation);
    cursor += 8;
    outputId_ = wire::readBigEndian<uint64_t>(payload.data() + cursor);
    cursor += 8;
    const auto base = wire::readBigEndian<uint64_t>(payload.data() + cursor);
    cursor += 8;
    GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint64_t>(payload.data() + cursor) == tag::KernelArgumentScalar);
    cursor += 8;
    GEMSTONE_RPC_REQUIRE(take() == 4);
    uint32_t seed = 0;
    std::memcpy(&seed, payload.data() + cursor, 4);
    GEMSTONE_RPC_REQUIRE(seed == expected_.seed);
    cursor += 4;
    if (trailingArguments_) {
      std::vector<uint8_t> bytes;
      for (uint32_t index = 2; index < argumentCount; ++index) {
        GEMSTONE_RPC_REQUIRE(wire::readBigEndian<uint64_t>(payload.data() + cursor) == tag::KernelArgumentScalar);
        cursor += 8;
        const auto size = take();
        GEMSTONE_RPC_REQUIRE(cursor + size <= payload.size());
        bytes.insert(bytes.end(), payload.begin() + cursor, payload.begin() + cursor + size);
        cursor += size;
      }
      GEMSTONE_RPC_REQUIRE(bytes == *trailingArguments_);
    }
    const auto relocationCount = take();
    for (uint32_t index = 0; index < relocationCount; ++index) {
      GEMSTONE_RPC_REQUIRE(cursor < payload.size());
      if (payload[cursor++] != 0) cursor += 8;
      GEMSTONE_RPC_REQUIRE(cursor < payload.size());
      if (payload[cursor++] != 0) cursor += 4;
      cursor += 24;
      GEMSTONE_RPC_REQUIRE(cursor <= payload.size());
    }
    GEMSTONE_RPC_REQUIRE(cursor == payload.size());

    auto &bytes = allocations_.at(outputId_);
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
  int listener_ = -1;
  uint16_t port_ = 0;
  std::atomic<bool> stopped_{false};
  std::thread acceptor_;
  mutable std::mutex failureMutex_;
  std::exception_ptr failure_;
  std::mutex connectionsMutex_, stateMutex_;
  std::vector<int> connections_;
  std::vector<std::thread> workers_;
  Expected expected_{};
  std::optional<std::vector<uint8_t>> trailingArguments_;
  uint32_t kernelArgumentBlockSize_ = 12;
  uint32_t kernelExplicitParameterCount_ = 2;
  std::vector<KernelParameter> kernelParameters_{{8, 0, 2, 0}, {4, 8, 1, 0}};
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
  std::map<uint64_t, uint64_t> allocationAddresses_;
  std::map<std::string, std::pair<uint64_t, std::vector<uint8_t>>> expectedGlobals_;
  std::vector<std::pair<uint64_t, uint32_t>> ranges_;
  std::atomic_bool failCompletion_{false};
  uint64_t nextAllocation_ = 1, outputId_ = 0, completion_ = 100;
  uint32_t polls_ = 0;
  std::atomic<unsigned> launches_{0}, pending_{0}, awaited_{0}, programLoads_{0};
  bool active_ = false, completed_ = false;
};
} // namespace gemstone_test
#undef GEMSTONE_RPC_REQUIRE
#endif
