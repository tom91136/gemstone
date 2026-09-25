/* Generated from ExecutionRPC.Operation. Regenerate with: sbt codegen */
#ifndef GEMSTONE_EXECUTION_RPC_HPP
#define GEMSTONE_EXECUTION_RPC_HPP

#include <stddef.h>
#include <stdint.h>
#include <memory>
#include <optional>
#include <string>
#include <utility>
#include <variant>
#include <vector>

namespace gemstone::execution {

inline constexpr uint32_t RequestMagic = static_cast<uint32_t>(0x474d4532);
inline constexpr uint32_t ResponseMagic = static_cast<uint32_t>(0x474d4632);
inline constexpr uint32_t MaximumPayload = 134217728U;
inline constexpr uint32_t MaximumElements = 16384U;
inline constexpr uint32_t MaximumTextBytes = 8192U;
inline constexpr uint64_t SchemaFingerprint = 0x64c8fbb072008b08ULL;
inline constexpr size_t RequestFingerprintOffset = 4U;
inline constexpr size_t RequestClientOffset = 12U;
inline constexpr size_t RequestSessionHighOffset = 20U;
inline constexpr size_t RequestSessionLowOffset = 28U;
inline constexpr size_t RequestOperationOffset = 36U;
inline constexpr size_t RequestPayloadSizeOffset = 44U;
inline constexpr size_t RequestHeaderBytes = 48U;
inline constexpr size_t ResponseFingerprintOffset = 4U;
inline constexpr size_t ResponseStatusOffset = 12U;
inline constexpr size_t ResponsePayloadSizeOffset = 16U;
inline constexpr size_t ResponseHeaderBytes = 20U;
namespace operation {
inline constexpr uint64_t Ping = 0xfe6050c5d9e5b2f0ULL;
inline constexpr uint64_t EnableHostMemory = 0x427c0ec0880d913aULL;
inline constexpr uint64_t PollHostMemory = 0x5f758336f94460f8ULL;
inline constexpr uint64_t SupplyHostMemory = 0xf062de3285328d0eULL;
inline constexpr uint64_t RegisterAllocation = 0x3ab2cfea883bea23ULL;
inline constexpr uint64_t SupplyHostSync = 0xd852c98d040d805aULL;
inline constexpr uint64_t RegisterAllocationAddress = 0xe96fefec41f65bf1ULL;
inline constexpr uint64_t ReleaseAllocation = 0x91cc677536b84617ULL;
inline constexpr uint64_t WriteAllocation = 0x5c2847c6c97d6de9ULL;
inline constexpr uint64_t ReadAllocation = 0x0bda0feff7f85a9aULL;
inline constexpr uint64_t LoadProgram = 0xaae065c73584df40ULL;
inline constexpr uint64_t UnloadProgram = 0xa0921649cb123a5dULL;
inline constexpr uint64_t DescribeProgram = 0xa2a840d71e8875b7ULL;
inline constexpr uint64_t DescribeKernel = 0x93b338a220a4fb14ULL;
inline constexpr uint64_t DescribeGlobal = 0xd1f288733a2ee8faULL;
inline constexpr uint64_t BindProgramGlobal = 0x0c1ffca104520e8aULL;
inline constexpr uint64_t WriteProgramGlobal = 0x6ae071f2e120bb82ULL;
inline constexpr uint64_t Launch = 0x3987746602c2539bULL;
inline constexpr uint64_t QueryCompletion = 0x8f8213a2ccfd27d2ULL;
inline constexpr uint64_t AwaitCompletion = 0x678f0d5d863467d8ULL;
inline constexpr uint64_t CompletionDirtyRanges = 0x45bdd3ff0f446a90ULL;
inline constexpr uint64_t CompletionPointerRelocations = 0x128e5851a288e89aULL;
inline constexpr uint64_t CompletionPrints = 0xfd4bcd954c0c4b36ULL;
inline constexpr uint64_t ReleaseCompletion = 0x7d37075e20a4c797ULL;
}
namespace tag {
inline constexpr uint64_t AllocationKindDevice = 0x550c809331279417ULL;
inline constexpr uint64_t AllocationKindManaged = 0xe3e753f7a49fdebaULL;
inline constexpr uint64_t AllocationKindShared = 0x3cca1c4bb4e27b28ULL;
inline constexpr uint64_t AllocationKindHost = 0x1502c13a23427453ULL;
inline constexpr uint64_t KernelParameterKindScalar = 0x8e8237265871dac9ULL;
inline constexpr uint64_t KernelParameterKindAllocation = 0x003f04bd0f952e6bULL;
inline constexpr uint64_t KernelParameterKindLocal = 0xf746e4b741bcb38cULL;
inline constexpr uint64_t KernelArgumentScalar = 0x4dc1565dba3b9867ULL;
inline constexpr uint64_t KernelArgumentAllocation = 0xc9602c96b7ee44fdULL;
inline constexpr uint64_t KernelArgumentLocal = 0x9fa3570b0ce037faULL;
inline constexpr uint64_t RelocationSourceAllocation = 0x7f6ec03808ee2e12ULL;
inline constexpr uint64_t RelocationSourceArgument = 0xd0123da2fbcfda83ULL;
inline constexpr uint64_t ExecutionFailureInvalidRequest = 0xa706a7cf8cba6a29ULL;
inline constexpr uint64_t ExecutionFailureNotFound = 0x39b74efbac06d31eULL;
inline constexpr uint64_t ExecutionFailureUnsupported = 0x67943c05acc71738ULL;
inline constexpr uint64_t ExecutionFailureFailed = 0xa2f22dde2ffdb70cULL;
inline constexpr uint64_t CompletionStatusPending = 0x4be231fa7a2a5d64ULL;
inline constexpr uint64_t CompletionStatusSucceeded = 0xd5313ba452a59f6aULL;
inline constexpr uint64_t CompletionStatusFailed = 0x4167002bdf4255fcULL;
inline constexpr uint64_t PrintArgumentInteger = 0x2fffc162da8adfbbULL;
inline constexpr uint64_t PrintArgumentFloating = 0x7c64cbc3d8134ee9ULL;
inline constexpr uint64_t PrintArgumentPointer = 0xb57d2dc948534440ULL;
inline constexpr uint64_t PrintArgumentText = 0x722582b3534d844cULL;
}

struct Unit {};

struct CompletionId { int64_t value{}; };

struct GuestAddress { int64_t value{}; };

struct IntPositive { int32_t value{}; };

struct HostMemoryRange {
  GuestAddress address{};
  IntPositive size{};
};

struct HostMemoryBytes {
  GuestAddress address{};
  std::vector<uint8_t> bytes{};
  int32_t atomicWidth{};
};

struct HostMemoryAtomic {
  GuestAddress address{};
  int32_t size{};
  int64_t expected{};
  int64_t desired{};
};

struct HostMemorySync {
  int64_t ticket{};
  std::optional<HostMemoryAtomic> atomic{};
};

struct HostMemoryBatch {
  std::vector<HostMemoryRange> reads{};
  std::vector<HostMemoryBytes> writes{};
  bool completed{};
  std::optional<HostMemorySync> sync{};
};

enum class AllocationKind : uint64_t {
  Device = 0x550c809331279417ULL,
  Managed = 0xe3e753f7a49fdebaULL,
  Shared = 0x3cca1c4bb4e27b28ULL,
  Host = 0x1502c13a23427453ULL
};

struct LongPositive { int64_t value{}; };

struct AllocationId { int64_t value{}; };

struct LongPositive0 { int64_t value{}; };

struct IntPositive0 { int32_t value{}; };

struct ProgramId { int64_t value{}; };

struct KernelName { std::string value{}; };

struct LinkageName { std::string value{}; };

struct ProgramKernel {
  KernelName name{};
  LinkageName linkageName{};
};

struct GlobalName { std::string value{}; };

struct ProgramGlobalMetadata {
  GlobalName name{};
  std::optional<GuestAddress> virtualAddress{};
  LongPositive0 size{};
  std::vector<IntPositive0> pointerOffsets{};
};

struct ProgramCatalog {
  std::vector<ProgramKernel> kernels{};
  std::vector<ProgramGlobalMetadata> globals{};
};

enum class KernelParameterKind : uint64_t {
  Scalar = 0x8e8237265871dac9ULL,
  Allocation = 0x003f04bd0f952e6bULL,
  Local = 0xf746e4b741bcb38cULL
};

struct KernelParameterMetadata {
  IntPositive width{};
  IntPositive0 offset{};
  KernelParameterKind kind{};
  bool containsPointers{};
};

struct KernelMetadata {
  IntPositive0 argumentBlockSize{};
  IntPositive0 explicitParameterCount{};
  std::vector<KernelParameterMetadata> parameters{};
};

struct ProgramGlobal {
  LongPositive0 size{};
  std::vector<uint8_t> initial{};
};

struct Dimensions {
  IntPositive x{};
  IntPositive y{};
  IntPositive z{};
};

struct GlobalDimensions {
  LongPositive x{};
  LongPositive y{};
  LongPositive z{};
};

struct IntInterval1To3 { int32_t value{}; };

struct Coordinates {
  IntPositive0 x{};
  IntPositive0 y{};
  IntPositive0 z{};
};

struct KernelArgumentScalar {
  std::vector<uint8_t> bytes{};
};
struct KernelArgumentAllocation {
  AllocationId id{};
  LongPositive0 offset{};
};
struct KernelArgumentLocal {
  LongPositive size{};
};
using KernelArgument = std::variant<KernelArgumentScalar, KernelArgumentAllocation, KernelArgumentLocal>;

struct RelocationSourceAllocation {
  AllocationId id{};
};
struct RelocationSourceArgument {
  IntPositive0 index{};
};
using RelocationSource = std::variant<RelocationSourceAllocation, RelocationSourceArgument>;

struct PointerRelocation {
  RelocationSource source{};
  LongPositive0 sourceOffset{};
  AllocationId target{};
  LongPositive0 targetOffset{};
};

struct ExecutionFailureInvalidRequest {
  std::string detail{};
};
struct ExecutionFailureNotFound {
  std::string detail{};
};
struct ExecutionFailureUnsupported {
  std::string detail{};
};
struct ExecutionFailureFailed {
  std::string detail{};
};
using ExecutionFailure = std::variant<ExecutionFailureInvalidRequest, ExecutionFailureNotFound, ExecutionFailureUnsupported, ExecutionFailureFailed>;

struct CompletionStatusPending {
};
struct CompletionStatusSucceeded {
};
struct CompletionStatusFailed {
  ExecutionFailure error{};
};
using CompletionStatus = std::variant<CompletionStatusPending, CompletionStatusSucceeded, CompletionStatusFailed>;

struct DirtyRange {
  AllocationId allocation{};
  LongPositive0 offset{};
  IntPositive size{};
};

struct PrintArgumentInteger {
  std::vector<int64_t> bits{};
  int32_t width{};
  bool isSigned{};
};
struct PrintArgumentFloating {
  std::vector<int64_t> bits{};
  int32_t width{};
};
struct PrintArgumentPointer {
  int64_t address{};
};
struct PrintArgumentText {
  std::string value{};
};
using PrintArgument = std::variant<PrintArgumentInteger, PrintArgumentFloating, PrintArgumentPointer, PrintArgumentText>;

struct PrintEvent {
  std::string format{};
  std::vector<PrintArgument> arguments{};
};

struct Ping {
};

struct EnableHostMemory {
};

struct PollHostMemory {
  CompletionId completion{};
};

struct SupplyHostMemory {
  CompletionId completion{};
  std::vector<HostMemoryBytes> ranges{};
};

struct RegisterAllocation {
  AllocationKind kind{};
  LongPositive size{};
};

struct SupplyHostSync {
  CompletionId completion{};
  int64_t ticket{};
  int64_t observed{};
};

struct RegisterAllocationAddress {
  AllocationId allocation{};
  GuestAddress nativeBase{};
  bool hostBacked{};
};

struct ReleaseAllocation {
  AllocationId allocation{};
};

struct WriteAllocation {
  AllocationId allocation{};
  LongPositive0 offset{};
  std::vector<uint8_t> bytes{};
};

struct ReadAllocation {
  AllocationId allocation{};
  LongPositive0 offset{};
  IntPositive0 size{};
};

struct LoadProgram {
  std::string encoding{};
  std::vector<uint8_t> image{};
};

struct UnloadProgram {
  ProgramId program{};
};

struct DescribeProgram {
  ProgramId program{};
};

struct DescribeKernel {
  ProgramId program{};
  KernelName name{};
};

struct DescribeGlobal {
  ProgramId program{};
  GlobalName name{};
};

struct BindProgramGlobal {
  ProgramId program{};
  GlobalName name{};
  AllocationId allocation{};
  LongPositive0 offset{};
};

struct WriteProgramGlobal {
  ProgramId program{};
  GlobalName name{};
  LongPositive0 offset{};
  std::vector<uint8_t> bytes{};
};

struct Launch {
  ProgramId program{};
  KernelName kernelName{};
  Dimensions groupSize{};
  Dimensions groupCount{};
  GlobalDimensions globalSize{};
  IntInterval1To3 workDim{};
  Coordinates globalOffset{};
  IntPositive0 privateSegmentBytes{};
  IntPositive0 groupSegmentBytes{};
  std::vector<KernelArgument> arguments{};
  std::vector<PointerRelocation> pointerRelocations{};
};

struct QueryCompletion {
  CompletionId completion{};
};

struct AwaitCompletion {
  CompletionId completion{};
};

struct CompletionDirtyRanges {
  CompletionId completion{};
};

struct CompletionPointerRelocations {
  CompletionId completion{};
};

struct CompletionPrints {
  CompletionId completion{};
};

struct ReleaseCompletion {
  CompletionId completion{};
};

struct Error {
  std::string detail;
  std::optional<ExecutionFailure> remote;
};

template <class T> class Result {
 public:
  static Result success(T value) { return Result(std::move(value)); }
  static Result failure(Error error) { return Result(std::move(error)); }
  explicit operator bool() const { return std::holds_alternative<T>(value_); }
  T &value() { return std::get<T>(value_); }
  const T &value() const { return std::get<T>(value_); }
  const Error &error() const { return std::get<Error>(value_); }

 private:
  explicit Result(T value) : value_(std::move(value)) {}
  explicit Result(Error error) : value_(std::move(error)) {}
  std::variant<T, Error> value_;
};

class Client {
 public:
  static std::unique_ptr<Client> fromEnvironment();
  ~Client();
  Client(const Client &) = delete;
  Client &operator=(const Client &) = delete;
  std::string error() const;
  Result<Unit> call(const Ping &request);
  Result<Unit> call(const EnableHostMemory &request);
  Result<HostMemoryBatch> call(const PollHostMemory &request);
  Result<Unit> call(const SupplyHostMemory &request);
  Result<AllocationId> call(const RegisterAllocation &request);
  Result<Unit> call(const SupplyHostSync &request);
  Result<Unit> call(const RegisterAllocationAddress &request);
  Result<Unit> call(const ReleaseAllocation &request);
  Result<Unit> call(const WriteAllocation &request);
  Result<std::vector<uint8_t>> call(const ReadAllocation &request);
  Result<ProgramId> call(const LoadProgram &request);
  Result<Unit> call(const UnloadProgram &request);
  Result<ProgramCatalog> call(const DescribeProgram &request);
  Result<KernelMetadata> call(const DescribeKernel &request);
  Result<ProgramGlobal> call(const DescribeGlobal &request);
  Result<Unit> call(const BindProgramGlobal &request);
  Result<Unit> call(const WriteProgramGlobal &request);
  Result<CompletionId> call(const Launch &request);
  Result<CompletionStatus> call(const QueryCompletion &request);
  Result<Unit> call(const AwaitCompletion &request);
  Result<std::vector<DirtyRange>> call(const CompletionDirtyRanges &request);
  Result<std::vector<PointerRelocation>> call(const CompletionPointerRelocations &request);
  Result<std::vector<PrintEvent>> call(const CompletionPrints &request);
  Result<Unit> call(const ReleaseCompletion &request);

 private:
  class Impl;
  explicit Client(std::unique_ptr<Impl> implementation);
  std::unique_ptr<Impl> implementation_;
};

} // namespace gemstone::execution

#endif
