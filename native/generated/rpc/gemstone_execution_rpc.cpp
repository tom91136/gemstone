/* Generated from ExecutionRPC.Operation. Regenerate with: sbt codegen */
#define _POSIX_C_SOURCE 200112L
#include "gemstone_execution_rpc.hpp"
#include "process_identity.hpp"

#include <arpa/inet.h>
#include <cerrno>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <mutex>
#include <netdb.h>
#include <netinet/tcp.h>
#include <string_view>
#include <sys/socket.h>
#include <sys/time.h>
#include <type_traits>
#include <unistd.h>

namespace gemstone::execution {
namespace {

struct GlazeOptions : glz::opts {
  static constexpr auto format = glz::MSGPACK;
  bool structs_as_arrays = true;
};

int socketTimeoutSeconds() {
  constexpr int fallback = 300;
  const char *raw = std::getenv("GEMSTONE_RPC_TIMEOUT_SECONDS");
  if (!raw || raw[0] == '\0') return fallback;
  char *end = nullptr; errno = 0; const long value = std::strtol(raw, &end, 10);
  return errno == 0 && end && *end == '\0' && value > 0 && value <= std::numeric_limits<int>::max()
             ? static_cast<int>(value) : fallback;
}

void appendU32(std::vector<uint8_t> &out, uint32_t value) {
  out.push_back(static_cast<uint8_t>(value >> 24));
  out.push_back(static_cast<uint8_t>(value >> 16));
  out.push_back(static_cast<uint8_t>(value >> 8));
  out.push_back(static_cast<uint8_t>(value));
}

void appendU64(std::vector<uint8_t> &out, uint64_t value) {
  appendU32(out, static_cast<uint32_t>(value >> 32));
  appendU32(out, static_cast<uint32_t>(value));
}

uint32_t readU32(const uint8_t *data) {
  return (static_cast<uint32_t>(data[0]) << 24) | (static_cast<uint32_t>(data[1]) << 16) | (static_cast<uint32_t>(data[2]) << 8)
         | static_cast<uint32_t>(data[3]);
}

uint64_t readU64(const uint8_t *data) { return (static_cast<uint64_t>(readU32(data)) << 32) | readU32(data + 4); }

std::string failureDetail(const ExecutionFailure &failure) {
  return std::visit([](const auto &value) { return value.detail; }, failure);
}

bool parseClient(const char *text, uint64_t &value) {
  if (text == nullptr || text[0] == '\0' || text[0] == '-') return false;
  errno = 0; char *end = nullptr; const auto parsed = std::strtoull(text, &end, 10);
  if (errno == ERANGE || end == text || *end != '\0' || parsed == 0) return false;
  value = static_cast<uint64_t>(parsed); return true;
}

bool sendAll(int fd, const uint8_t *data, size_t size) {
  while (size != 0) { const auto sent = ::send(fd, data, size, MSG_NOSIGNAL); if (sent < 0 && errno == EINTR) continue; if (sent <= 0) return false; data += sent; size -= static_cast<size_t>(sent); } return true;
}
bool receiveAll(int fd, uint8_t *data, size_t size) {
  while (size != 0) { const auto received = ::recv(fd, data, size, 0); if (received < 0 && errno == EINTR) continue; if (received <= 0) return false; data += received; size -= static_cast<size_t>(received); } return true;
}

} // namespace

class Client::Impl {
 public:
  Impl(uint64_t client, uint64_t high, uint64_t low) : client_(client), sessionHigh_(high), sessionLow_(low) {}
  ~Impl() { if (fd_ >= 0) ::close(fd_); }
  std::string error() const { std::lock_guard<std::mutex> guard(mutex_); return error_; }

  template <class T, class Request> Result<T> call(uint64_t operation, const Request &request) {
    std::lock_guard<std::mutex> guard(mutex_); error_.clear();
    std::string payload;
    if (glz::write<GlazeOptions{}>(request, payload)) return failed<T>("execution request encoding failed");
    if (payload.size() > MaximumPayload) return failed<T>("execution request exceeds the encoding limits");
    std::vector<uint8_t> response; uint32_t status = 0;
    if (!exchange(operation, payload, status, response)) return Result<T>::failure(Error{error_});
    const std::string_view body(reinterpret_cast<const char *>(response.data()), response.size());
    if (status == 1) {
      ExecutionFailure failure{};
      if (glz::read<GlazeOptions{}>(failure, body)) { poison(); return failed<T>("execution failure response is invalid"); }
      error_ = failureDetail(failure); return Result<T>::failure(Error{error_});
    }
    T value{};
    if (glz::read<GlazeOptions{}>(value, body)) { poison(); return failed<T>("execution response payload is invalid"); }
    return Result<T>::success(std::move(value));
  }

 private:
  template <class T> Result<T> failed(std::string detail) { error_ = std::move(detail); return Result<T>::failure(Error{error_}); }
  void poison() { if (fd_ >= 0) ::close(fd_); fd_ = -1; if (established_) poisoned_ = true; }
  bool connectServer() {
    if (fd_ >= 0) return true;
    if (poisoned_) { error_ = "execution session was lost; create a fresh client and rebuild its state"; return false; }
    const char *port = std::getenv("GEMSTONE_RPC_PORT"); if (port == nullptr || port[0] == '\0') { error_ = "GEMSTONE_RPC_PORT is not set"; return false; }
    addrinfo hints{}; hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM; addrinfo *addresses = nullptr;
    if (::getaddrinfo("127.0.0.1", port, &hints, &addresses) != 0) { error_ = "cannot resolve the Gemstone RPC endpoint"; return false; }
    int connected = -1; for (auto *address = addresses; address != nullptr; address = address->ai_next) { connected = ::socket(address->ai_family, address->ai_socktype, address->ai_protocol); if (connected < 0) continue; if (::connect(connected, address->ai_addr, address->ai_addrlen) == 0) break; ::close(connected); connected = -1; }
    ::freeaddrinfo(addresses); if (connected < 0) { error_ = "cannot connect to the Gemstone RPC endpoint"; return false; }
    const timeval timeout{socketTimeoutSeconds(), 0}; const int noDelay = 1;
    if (::setsockopt(connected, IPPROTO_TCP, TCP_NODELAY, &noDelay, sizeof(noDelay)) != 0 ||
        ::setsockopt(connected, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) != 0 ||
        ::setsockopt(connected, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout)) != 0) {
      ::close(connected); error_ = "cannot configure the Gemstone RPC connection"; return false;
    }
    fd_ = connected; established_ = true; return true;
  }
  bool exchange(uint64_t operation, const std::string &payload, uint32_t &status, std::vector<uint8_t> &response) {
    if (!connectServer()) return false;
    std::vector<uint8_t> header;
    appendU32(header, RequestMagic); appendU64(header, SchemaFingerprint); appendU64(header, client_);
    appendU64(header, sessionHigh_); appendU64(header, sessionLow_); appendU64(header, operation);
    appendU32(header, static_cast<uint32_t>(payload.size()));
    if (!sendAll(fd_, header.data(), header.size())
        || (!payload.empty() && !sendAll(fd_, reinterpret_cast<const uint8_t *>(payload.data()), payload.size()))) {
      poison(); error_ = "execution request transport failed"; return false;
    }
    uint8_t headerBytes[ResponseHeaderBytes];
    if (!receiveAll(fd_, headerBytes, sizeof(headerBytes))) { poison(); error_ = "execution response transport failed"; return false; }
    const uint32_t magic = readU32(headerBytes); const uint64_t fingerprint = readU64(headerBytes + 4);
    status = readU32(headerBytes + 12); const uint32_t size = readU32(headerBytes + 16);
    if (magic != ResponseMagic || fingerprint != SchemaFingerprint || status > 1 || size > MaximumPayload) {
      poison(); error_ = "execution response header is invalid"; return false;
    }
    response.resize(size); if (!response.empty() && !receiveAll(fd_, response.data(), response.size())) { poison(); error_ = "execution response body is truncated"; return false; }
    return true;
  }
  mutable std::mutex mutex_; int fd_ = -1; bool established_ = false; bool poisoned_ = false;
  uint64_t client_; uint64_t sessionHigh_; uint64_t sessionLow_; std::string error_;
};

std::unique_ptr<Client> Client::fromEnvironment() {
  uint64_t client = 0; if (!parseClient(std::getenv("GEMSTONE_RPC_CLIENT"), client)) return nullptr;
  const auto incarnation = processIncarnation(); if (incarnation == 0) return nullptr;
  return std::unique_ptr<Client>(new Client(std::make_unique<Impl>(client, static_cast<uint32_t>(::getpid()), incarnation)));
}
Client::Client(std::unique_ptr<Impl> implementation) : implementation_(std::move(implementation)) {}
Client::~Client() = default;
std::string Client::error() const { return implementation_->error(); }

Result<Unit> Client::call(const Ping &request) {
  return implementation_->call<Unit>(operation::Ping, request);
}

Result<Unit> Client::call(const EnableHostMemory &request) {
  return implementation_->call<Unit>(operation::EnableHostMemory, request);
}

Result<HostMemoryBatch> Client::call(const PollHostMemory &request) {
  return implementation_->call<HostMemoryBatch>(operation::PollHostMemory, request);
}

Result<Unit> Client::call(const SupplyHostMemory &request) {
  return implementation_->call<Unit>(operation::SupplyHostMemory, request);
}

Result<AllocationId> Client::call(const RegisterAllocation &request) {
  return implementation_->call<AllocationId>(operation::RegisterAllocation, request);
}

Result<Unit> Client::call(const SupplyHostSync &request) {
  return implementation_->call<Unit>(operation::SupplyHostSync, request);
}

Result<Unit> Client::call(const RegisterAllocationAddress &request) {
  return implementation_->call<Unit>(operation::RegisterAllocationAddress, request);
}

Result<Unit> Client::call(const ReleaseAllocation &request) {
  return implementation_->call<Unit>(operation::ReleaseAllocation, request);
}

Result<Unit> Client::call(const WriteAllocation &request) {
  return implementation_->call<Unit>(operation::WriteAllocation, request);
}

Result<std::vector<uint8_t>> Client::call(const ReadAllocation &request) {
  return implementation_->call<std::vector<uint8_t>>(operation::ReadAllocation, request);
}

Result<ProgramId> Client::call(const LoadProgram &request) {
  return implementation_->call<ProgramId>(operation::LoadProgram, request);
}

Result<std::vector<SpecializationInfo>> Client::call(const DescribeSpecialization &request) {
  return implementation_->call<std::vector<SpecializationInfo>>(operation::DescribeSpecialization, request);
}

Result<std::vector<uint8_t>> Client::call(const SpecializeImage &request) {
  return implementation_->call<std::vector<uint8_t>>(operation::SpecializeImage, request);
}

Result<Unit> Client::call(const UnloadProgram &request) {
  return implementation_->call<Unit>(operation::UnloadProgram, request);
}

Result<ProgramSymbols> Client::call(const DescribeProgram &request) {
  return implementation_->call<ProgramSymbols>(operation::DescribeProgram, request);
}

Result<KernelMetadata> Client::call(const DescribeKernel &request) {
  return implementation_->call<KernelMetadata>(operation::DescribeKernel, request);
}

Result<KernelMetadata> Client::call(const DescribeImage &request) {
  return implementation_->call<KernelMetadata>(operation::DescribeImage, request);
}

Result<BlasResult> Client::call(const Blas &request) {
  return implementation_->call<BlasResult>(operation::Blas, request);
}

Result<ProgramGlobal> Client::call(const DescribeGlobal &request) {
  return implementation_->call<ProgramGlobal>(operation::DescribeGlobal, request);
}

Result<Unit> Client::call(const BindProgramGlobal &request) {
  return implementation_->call<Unit>(operation::BindProgramGlobal, request);
}

Result<Unit> Client::call(const WriteProgramGlobal &request) {
  return implementation_->call<Unit>(operation::WriteProgramGlobal, request);
}

Result<CompletionId> Client::call(const Launch &request) {
  return implementation_->call<CompletionId>(operation::Launch, request);
}

Result<CompletionStatus> Client::call(const QueryCompletion &request) {
  return implementation_->call<CompletionStatus>(operation::QueryCompletion, request);
}

Result<Unit> Client::call(const AwaitCompletion &request) {
  return implementation_->call<Unit>(operation::AwaitCompletion, request);
}

Result<std::vector<DirtyRange>> Client::call(const CompletionDirtyRanges &request) {
  return implementation_->call<std::vector<DirtyRange>>(operation::CompletionDirtyRanges, request);
}

Result<std::vector<PointerRelocation>> Client::call(const CompletionPointerRelocations &request) {
  return implementation_->call<std::vector<PointerRelocation>>(operation::CompletionPointerRelocations, request);
}

Result<std::string> Client::call(const CompletionPrints &request) {
  return implementation_->call<std::string>(operation::CompletionPrints, request);
}

Result<Unit> Client::call(const ReleaseCompletion &request) {
  return implementation_->call<Unit>(operation::ReleaseCompletion, request);
}

} // namespace gemstone::execution
