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
  return std::visit([](const auto &value) { return value.detail; }, failure);
}

} // namespace

class Client::Impl {
 public:
  Impl(uint64_t client, uint64_t high, uint64_t low) : connection_(client, high, low) {}
  std::string error() const { std::lock_guard<std::mutex> guard(mutex_); return error_; }

  template <class T, class Request> Result<T> call(std::string_view operation, const Request &request) {
    std::lock_guard<std::mutex> guard(mutex_); error_.clear();
    std::string payload;
    if (glz::write<GlazeOptions{}>(request, payload)) return failed<T>("execution request encoding failed");
    if (payload.size() > MaximumPayload) return failed<T>("execution request exceeds the encoding limits");
    std::vector<uint8_t> response; uint32_t status = 0;
    if (!connection_.exchange(operation, payload, status, response)) return failed<T>(connection_.error());
    const std::string_view body(reinterpret_cast<const char *>(response.data()), response.size());
    if (status == 1) {
      ExecutionFailure failure{};
      if (glz::read<GlazeOptions{}>(failure, body)) { connection_.poison(); return failed<T>("execution failure response is invalid"); }
      error_ = failureDetail(failure); return Result<T>::failure(Error{error_});
    }
    T value{};
    if (glz::read<GlazeOptions{}>(value, body)) { connection_.poison(); return failed<T>("execution response payload is invalid"); }
    return Result<T>::success(std::move(value));
  }

 private:
  template <class T> Result<T> failed(std::string detail) { error_ = std::move(detail); return Result<T>::failure(Error{error_}); }
  mutable std::mutex mutex_;
  Connection connection_;
  std::string error_;
};

std::unique_ptr<Client> Client::fromEnvironment() {
  const auto client = environment::unsignedValue(environment::RpcClient, 0, std::numeric_limits<int64_t>::max());
  if (client == 0) return nullptr;
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
