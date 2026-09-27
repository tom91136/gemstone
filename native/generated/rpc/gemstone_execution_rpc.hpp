/* Generated from ExecutionRPC.Operation. Regenerate with: sbt codegen */
#ifndef GEMSTONE_EXECUTION_RPC_HPP
#define GEMSTONE_EXECUTION_RPC_HPP

#include <stddef.h>
#include <stdint.h>
#include <array>
#include <memory>
#include <optional>
#include <string>
#include <string_view>
#include <utility>
#include <variant>
#include <vector>

#include <glaze/glaze.hpp>
#include <glaze/msgpack.hpp>

namespace gemstone::execution {

inline constexpr uint32_t RequestMagic = static_cast<uint32_t>(0x474d4532);
inline constexpr uint32_t ResponseMagic = static_cast<uint32_t>(0x474d4632);
inline constexpr uint32_t MaximumPayload = 134217728U;
inline constexpr uint32_t MaximumElements = 16384U;
inline constexpr uint32_t MaximumTextBytes = 8192U;
inline constexpr uint64_t SchemaFingerprint = 0x5972750acc285ed3ULL;
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
inline constexpr uint64_t DescribeSpecialization = 0x9fa04c9645f3afdcULL;
inline constexpr uint64_t SpecializeImage = 0x02d7de1c949c2f70ULL;
inline constexpr uint64_t UnloadProgram = 0xa0921649cb123a5dULL;
inline constexpr uint64_t DescribeProgram = 0xa2a840d71e8875b7ULL;
inline constexpr uint64_t DescribeKernel = 0x93b338a220a4fb14ULL;
inline constexpr uint64_t DescribeImage = 0xe4bd817496f5c2a6ULL;
inline constexpr uint64_t Blas = 0xe75a445a9652b322ULL;
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

struct Unit {};

struct CompletionId {
  int64_t value{};
};

struct GuestAddress {
  int64_t value{};
};

struct IntPositive {
  int32_t value{};
};

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

enum class AllocationKind : uint8_t {
  Device = 0,
  Managed = 1,
  Shared = 2,
  Host = 3
};

struct LongPositive {
  int64_t value{};
};

struct AllocationId {
  int64_t value{};
};

struct LongPositive0 {
  int64_t value{};
};

struct IntPositive0 {
  int32_t value{};
};

struct ProgramId {
  int64_t value{};
};

struct SpecializationInfo {
  IntPositive0 specId{};
  IntPositive byteSize{};
};

struct SpecializationConstant {
  IntPositive0 specId{};
  std::vector<uint8_t> value{};
};

struct NonEmptyString {
  std::string value{};
};

struct KernelSymbol {
  NonEmptyString name{};
  NonEmptyString linkageName{};
};

struct GlobalSymbol {
  NonEmptyString name{};
  std::optional<GuestAddress> virtualAddress{};
  LongPositive0 size{};
  std::vector<IntPositive0> pointerOffsets{};
};

struct ProgramSymbols {
  std::vector<KernelSymbol> kernels{};
  std::vector<GlobalSymbol> globals{};
};

enum class ParameterKind : uint8_t {
  Scalar = 0,
  Allocation = 1,
  Local = 2
};

struct Parameter {
  IntPositive width{};
  IntPositive0 offset{};
  ParameterKind kind{};
  bool containsPointers{};
};

struct Dimensions {
  IntPositive x{};
  IntPositive y{};
  IntPositive z{};
};

struct KernelMetadata {
  IntPositive0 argumentBlockSize{};
  IntPositive0 explicitParameterCount{};
  std::vector<Parameter> parameters{};
  std::optional<Dimensions> requiredWorkGroupSize{};
  std::optional<Dimensions> workGroupSizeHint{};
  std::vector<IntPositive0> bindingIndices{};
  std::optional<IntPositive0> entryPointId{};
};

enum class BlasOperation : uint8_t {
  Axpy = 0,
  Scal = 1,
  Copy = 2,
  Dot = 3,
  Nrm2 = 4,
  Asum = 5,
  Gemv = 6,
  Gemm = 7,
  GemmStridedBatched = 8
};

enum class BlasElement : uint8_t {
  F16 = 0,
  F32 = 1,
  F64 = 2
};

struct BlasVector {
  AllocationId allocation{};
  LongPositive0 offset{};
  int32_t increment{};
};

struct BlasMatrix {
  AllocationId allocation{};
  LongPositive0 offset{};
  int32_t leading{};
};

struct BlasRequest {
  BlasOperation operation{};
  BlasElement element{};
  int32_t count{};
  int32_t rows{};
  int32_t columns{};
  int32_t inner{};
  bool transposeA{};
  bool transposeB{};
  std::vector<uint8_t> alpha{};
  std::vector<uint8_t> beta{};
  std::optional<BlasVector> x{};
  std::optional<BlasVector> y{};
  std::optional<BlasMatrix> a{};
  std::optional<BlasMatrix> b{};
  std::optional<BlasMatrix> c{};
  int64_t strideA{};
  int64_t strideB{};
  int64_t strideC{};
  int32_t batch{};
};

struct BlasResult {
  std::vector<uint8_t> scalar{};
};

struct ProgramGlobal {
  LongPositive0 size{};
  std::vector<uint8_t> initial{};
};

struct GlobalDimensions {
  LongPositive x{};
  LongPositive y{};
  LongPositive z{};
};

struct IntInterval1To3 {
  int32_t value{};
};

struct Coordinates {
  IntPositive0 x{};
  IntPositive0 y{};
  IntPositive0 z{};
};

struct MemoryRegion {
  int64_t value{};
};

struct ArgumentScalar {
  std::vector<uint8_t> bytes{};
};
struct ArgumentMemory {
  MemoryRegion region{};
  LongPositive0 offset{};
};
struct ArgumentLocal {
  LongPositive size{};
};

using Argument = std::variant<ArgumentScalar, ArgumentMemory, ArgumentLocal>;

struct RelocationSourceMemory {
  MemoryRegion region{};
};
struct RelocationSourceArgument {
  IntPositive0 index{};
};

using RelocationSource = std::variant<RelocationSourceMemory, RelocationSourceArgument>;

struct PointerRelocation {
  RelocationSource source{};
  LongPositive0 sourceOffset{};
  MemoryRegion target{};
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

struct CompletionStatusPending {};
struct CompletionStatusSucceeded {};
struct CompletionStatusFailed {
  ExecutionFailure error{};
};

using CompletionStatus = std::variant<CompletionStatusPending, CompletionStatusSucceeded, CompletionStatusFailed>;

struct DirtyRange {
  AllocationId allocation{};
  LongPositive0 offset{};
  IntPositive size{};
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

struct DescribeSpecialization {
  std::vector<uint8_t> image{};
};

struct SpecializeImage {
  std::vector<uint8_t> image{};
  std::vector<SpecializationConstant> constants{};
};

struct UnloadProgram {
  ProgramId program{};
};

struct DescribeProgram {
  ProgramId program{};
};

struct DescribeKernel {
  ProgramId program{};
  NonEmptyString name{};
};

struct DescribeImage {
  std::string encoding{};
  std::vector<uint8_t> image{};
  NonEmptyString name{};
};

struct Blas {
  BlasRequest request{};
};

struct DescribeGlobal {
  ProgramId program{};
  NonEmptyString name{};
};

struct BindProgramGlobal {
  ProgramId program{};
  NonEmptyString name{};
  AllocationId allocation{};
  LongPositive0 offset{};
};

struct WriteProgramGlobal {
  ProgramId program{};
  NonEmptyString name{};
  LongPositive0 offset{};
  std::vector<uint8_t> bytes{};
};

struct Launch {
  ProgramId program{};
  NonEmptyString kernelName{};
  Dimensions groupSize{};
  Dimensions groupCount{};
  GlobalDimensions globalSize{};
  IntInterval1To3 workDim{};
  Coordinates globalOffset{};
  IntPositive0 privateSegmentBytes{};
  IntPositive0 groupSegmentBytes{};
  std::vector<Argument> arguments{};
  std::vector<PointerRelocation> pointerRelocations{};
  bool discoverArgumentRelocations{};
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
  Result<std::vector<SpecializationInfo>> call(const DescribeSpecialization &request);
  Result<std::vector<uint8_t>> call(const SpecializeImage &request);
  Result<Unit> call(const UnloadProgram &request);
  Result<ProgramSymbols> call(const DescribeProgram &request);
  Result<KernelMetadata> call(const DescribeKernel &request);
  Result<KernelMetadata> call(const DescribeImage &request);
  Result<BlasResult> call(const Blas &request);
  Result<ProgramGlobal> call(const DescribeGlobal &request);
  Result<Unit> call(const BindProgramGlobal &request);
  Result<Unit> call(const WriteProgramGlobal &request);
  Result<CompletionId> call(const Launch &request);
  Result<CompletionStatus> call(const QueryCompletion &request);
  Result<Unit> call(const AwaitCompletion &request);
  Result<std::vector<DirtyRange>> call(const CompletionDirtyRanges &request);
  Result<std::vector<PointerRelocation>> call(const CompletionPointerRelocations &request);
  Result<std::string> call(const CompletionPrints &request);
  Result<Unit> call(const ReleaseCompletion &request);

 private:
  class Impl;
  explicit Client(std::unique_ptr<Impl> implementation);
  std::unique_ptr<Impl> implementation_;
};

} // namespace gemstone::execution

template <> struct glz::meta<gemstone::execution::Unit> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::CompletionId> { static constexpr auto value = &gemstone::execution::CompletionId::value; };
template <> struct glz::meta<gemstone::execution::GuestAddress> { static constexpr auto value = &gemstone::execution::GuestAddress::value; };
template <> struct glz::meta<gemstone::execution::IntPositive> { static constexpr auto value = &gemstone::execution::IntPositive::value; };
template <> struct glz::meta<gemstone::execution::LongPositive> { static constexpr auto value = &gemstone::execution::LongPositive::value; };
template <> struct glz::meta<gemstone::execution::AllocationId> { static constexpr auto value = &gemstone::execution::AllocationId::value; };
template <> struct glz::meta<gemstone::execution::LongPositive0> { static constexpr auto value = &gemstone::execution::LongPositive0::value; };
template <> struct glz::meta<gemstone::execution::IntPositive0> { static constexpr auto value = &gemstone::execution::IntPositive0::value; };
template <> struct glz::meta<gemstone::execution::ProgramId> { static constexpr auto value = &gemstone::execution::ProgramId::value; };
template <> struct glz::meta<gemstone::execution::NonEmptyString> { static constexpr auto value = &gemstone::execution::NonEmptyString::value; };
template <> struct glz::meta<gemstone::execution::IntInterval1To3> { static constexpr auto value = &gemstone::execution::IntInterval1To3::value; };
template <> struct glz::meta<gemstone::execution::MemoryRegion> { static constexpr auto value = &gemstone::execution::MemoryRegion::value; };
template <> struct glz::meta<gemstone::execution::Argument> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 3>{"Scalar", "Memory", "Local"};
};
template <> struct glz::meta<gemstone::execution::RelocationSource> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 2>{"Memory", "Argument"};
};
template <> struct glz::meta<gemstone::execution::ExecutionFailure> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 4>{"InvalidRequest", "NotFound", "Unsupported", "Failed"};
};
template <> struct glz::meta<gemstone::execution::CompletionStatusPending> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::CompletionStatusSucceeded> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::CompletionStatus> {
  static constexpr std::string_view tag = "t";
  static constexpr std::string_view content = "v";
  static constexpr auto ids = std::array<std::string_view, 3>{"Pending", "Succeeded", "Failed"};
};
template <> struct glz::meta<gemstone::execution::Ping> { static constexpr auto value = glz::object(); };
template <> struct glz::meta<gemstone::execution::EnableHostMemory> { static constexpr auto value = glz::object(); };

#endif
