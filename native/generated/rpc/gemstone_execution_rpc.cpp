/* Generated from ExecutionRPC.Operation. Regenerate with: sbt codegen */
#define _POSIX_C_SOURCE 200112L
#include "gemstone_execution_rpc.hpp"
#include <gemstone/environment.hpp>
#include "process_identity.hpp"

#include "connection.hpp"
#include <mutex>
#include <string_view>
#include <unistd.h>

namespace gemstone::execution {
namespace {

struct GlazeOptions : glz::opts {
  static constexpr auto format = glz::MSGPACK;
  bool structs_as_arrays = true;
};

std::string failureDetail(const ExecutionFailure &failure) {
  return std::visit([](const auto &value) { return value.detail.value; }, failure);
}

FailureKind failureKind(const ExecutionFailure &failure) { return static_cast<FailureKind>(failure.index() + 1); }

} // namespace

class Client::Impl {
 public:
  Impl(uint64_t client, uint64_t high, uint64_t low) : connection(client, high, low) {}
  std::string error() const { std::lock_guard<std::mutex> guard(mutex); return lastError; }

  template <class T, class Request> Result<T> call(std::string_view operation, const Request &request) {
    std::lock_guard<std::mutex> guard(mutex); lastError.clear();
    if (!wire::valid(request)) return failed<T>("execution request violates wire constraints or encoding limits");
    std::string payload;
    if (glz::write<GlazeOptions{}>(request, payload)) return failed<T>("execution request encoding failed");
    if (payload.size() > MaximumPayload) return failed<T>("execution request exceeds the encoding limits");
    std::vector<uint8_t> response; uint32_t status = 0;
    if (!connection.exchange(operation, payload, status, response)) return failed<T>(connection.error);
    const std::string_view body(reinterpret_cast<const char *>(response.data()), response.size());
    if (status == 1) {
      ExecutionFailure failure{};
      if (glz::read<GlazeOptions{}>(failure, body) || !wire::valid(failure)) { connection.poison(); return failed<T>("execution failure response is invalid"); }
      lastError = failureDetail(failure); return Result<T>::failure(Error{lastError, failureKind(failure)});
    }
    T value{};
    if (glz::read<GlazeOptions{}>(value, body) || !wire::valid(value)) { connection.poison(); return failed<T>("execution response payload is invalid"); }
    return Result<T>::success(std::move(value));
  }

 private:
  template <class T> Result<T> failed(std::string detail) { lastError = std::move(detail); return Result<T>::failure(Error{lastError}); }
  mutable std::mutex mutex;
  Connection connection;
  std::string lastError;
};

std::unique_ptr<Client> Client::fromEnvironment() {
  const auto client = environment::unsignedValue(environment::RpcClientId, 0, std::numeric_limits<int64_t>::max());
  if (client == 0) return nullptr;
  const auto incarnation = processIncarnation(); if (incarnation == 0) return nullptr;
  return std::unique_ptr<Client>(new Client(std::make_unique<Impl>(client, static_cast<uint32_t>(::getpid()), incarnation)));
}
Client::Client(std::unique_ptr<Impl> implementation) : implementation(std::move(implementation)) {}
Client::~Client() = default;
std::string Client::error() const { return implementation->error(); }

Result<Unit> Client::call(const Ping &request) {
  return implementation->call<Unit>(operation::Ping, request);
}

Result<Unit> Client::call(const EnableHostMemory &request) {
  return implementation->call<Unit>(operation::EnableHostMemory, request);
}

Result<HostMemoryBatch> Client::call(const PollHostMemory &request) {
  return implementation->call<HostMemoryBatch>(operation::PollHostMemory, request);
}

Result<Unit> Client::call(const SupplyHostMemory &request) {
  return implementation->call<Unit>(operation::SupplyHostMemory, request);
}

Result<AllocationId> Client::call(const RegisterAllocation &request) {
  return implementation->call<AllocationId>(operation::RegisterAllocation, request);
}

Result<Unit> Client::call(const SupplyHostSync &request) {
  return implementation->call<Unit>(operation::SupplyHostSync, request);
}

Result<Unit> Client::call(const RegisterAllocationAddress &request) {
  return implementation->call<Unit>(operation::RegisterAllocationAddress, request);
}

Result<Unit> Client::call(const ReleaseAllocation &request) {
  return implementation->call<Unit>(operation::ReleaseAllocation, request);
}

Result<Unit> Client::call(const WriteAllocation &request) {
  return implementation->call<Unit>(operation::WriteAllocation, request);
}

Result<std::vector<uint8_t>> Client::call(const ReadAllocation &request) {
  return implementation->call<std::vector<uint8_t>>(operation::ReadAllocation, request);
}

Result<Unit> Client::call(const WriteAllocationRectangle &request) {
  return implementation->call<Unit>(operation::WriteAllocationRectangle, request);
}

Result<std::vector<uint8_t>> Client::call(const ReadAllocationRectangle &request) {
  return implementation->call<std::vector<uint8_t>>(operation::ReadAllocationRectangle, request);
}

Result<ProgramId> Client::call(const LoadProgram &request) {
  return implementation->call<ProgramId>(operation::LoadProgram, request);
}

Result<std::vector<SpecializationInfo>> Client::call(const DescribeSpecialization &request) {
  return implementation->call<std::vector<SpecializationInfo>>(operation::DescribeSpecialization, request);
}

Result<std::vector<uint8_t>> Client::call(const SpecializeImage &request) {
  return implementation->call<std::vector<uint8_t>>(operation::SpecializeImage, request);
}

Result<PreparedShader> Client::call(const PrepareShader &request) {
  return implementation->call<PreparedShader>(operation::PrepareShader, request);
}

Result<CompletionId> Client::call(const SubmitGraphics &request) {
  return implementation->call<CompletionId>(operation::SubmitGraphics, request);
}

Result<GraphicsStatistics> Client::call(const ReadGraphicsStatistics &request) {
  return implementation->call<GraphicsStatistics>(operation::ReadGraphicsStatistics, request);
}

Result<Unit> Client::call(const UnloadProgram &request) {
  return implementation->call<Unit>(operation::UnloadProgram, request);
}

Result<ProgramSymbols> Client::call(const DescribeProgram &request) {
  return implementation->call<ProgramSymbols>(operation::DescribeProgram, request);
}

Result<KernelMetadata> Client::call(const DescribeKernel &request) {
  return implementation->call<KernelMetadata>(operation::DescribeKernel, request);
}

Result<KernelMetadata> Client::call(const DescribeImage &request) {
  return implementation->call<KernelMetadata>(operation::DescribeImage, request);
}

Result<ProgramGlobal> Client::call(const DescribeGlobal &request) {
  return implementation->call<ProgramGlobal>(operation::DescribeGlobal, request);
}

Result<Unit> Client::call(const BindProgramGlobal &request) {
  return implementation->call<Unit>(operation::BindProgramGlobal, request);
}

Result<Unit> Client::call(const WriteProgramGlobal &request) {
  return implementation->call<Unit>(operation::WriteProgramGlobal, request);
}

Result<Unit> Client::call(const BuildKernel &request) {
  return implementation->call<Unit>(operation::BuildKernel, request);
}

Result<CompletionId> Client::call(const Launch &request) {
  return implementation->call<CompletionId>(operation::Launch, request);
}

Result<CompletionStatus> Client::call(const QueryCompletion &request) {
  return implementation->call<CompletionStatus>(operation::QueryCompletion, request);
}

Result<std::vector<DirtyRange>> Client::call(const CompletionDirtyRanges &request) {
  return implementation->call<std::vector<DirtyRange>>(operation::CompletionDirtyRanges, request);
}

Result<std::vector<PointerRelocation>> Client::call(const CompletionPointerRelocations &request) {
  return implementation->call<std::vector<PointerRelocation>>(operation::CompletionPointerRelocations, request);
}

Result<std::string> Client::call(const CompletionPrints &request) {
  return implementation->call<std::string>(operation::CompletionPrints, request);
}

Result<Unit> Client::call(const ReleaseCompletion &request) {
  return implementation->call<Unit>(operation::ReleaseCompletion, request);
}

} // namespace gemstone::execution
