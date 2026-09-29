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

inline constexpr uint32_t RequestMagic = static_cast<uint32_t>(0x474d4533);
inline constexpr uint32_t ResponseMagic = static_cast<uint32_t>(0x474d4633);
inline constexpr uint32_t MaximumPayload = 134217728U;
inline constexpr uint32_t MaximumElements = 16384U;
inline constexpr uint32_t MaximumTextBytes = 8192U;
inline constexpr uint32_t HelloMagic = 1196247091U;
inline constexpr uint32_t HelloResponseMagic = 1196247347U;
inline constexpr uint32_t MaximumSchemaBytes = 1048576U;
inline constexpr uint32_t MaximumTagBytes = 256U;
inline constexpr std::string_view Schema = "protocol(gemstone.execution){Ping:product(Ping){}=>unit,EnableHostMemory:product(EnableHostMemory){}=>unit,PollHostMemory:product(PollHostMemory){completion:alias(CompletionId,i64)[positive]}=>product(HostMemoryBatch){reads:seq[product(HostMemoryRange){address:alias(GuestAddress,i64)[],size:alias(Int,i32)[positive]}],writes:seq[product(HostMemoryBytes){address:alias(GuestAddress,i64)[],bytes:seq[i8],atomicWidth:i32}],completed:bool,sync:option[product(HostMemorySync){ticket:i64,atomic:option[product(HostMemoryAtomic){address:alias(GuestAddress,i64)[],size:i32,expected:i64,desired:i64}]}]},SupplyHostMemory:product(SupplyHostMemory){completion:alias(CompletionId,i64)[positive],ranges:seq[product(HostMemoryBytes){address:alias(GuestAddress,i64)[],bytes:seq[i8],atomicWidth:i32}]}=>unit,RegisterAllocation:product(RegisterAllocation){kind:sum(AllocationKind){Device:product(Device){},Managed:product(Managed){},Shared:product(Shared){},Host:product(Host){}},size:alias(Long,i64)[positive]}=>alias(AllocationId,i64)[positive],SupplyHostSync:product(SupplyHostSync){completion:alias(CompletionId,i64)[positive],ticket:i64,observed:i64}=>unit,RegisterAllocationAddress:product(RegisterAllocationAddress){allocation:alias(AllocationId,i64)[positive],nativeBase:alias(GuestAddress,i64)[],hostBacked:bool}=>unit,ReleaseAllocation:product(ReleaseAllocation){allocation:alias(AllocationId,i64)[positive]}=>unit,WriteAllocation:product(WriteAllocation){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],bytes:seq[i8]}=>unit,ReadAllocation:product(ReadAllocation){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Int,i32)[positive0]}=>seq[i8],LoadProgram:product(LoadProgram){encoding:text,image:seq[i8]}=>alias(ProgramId,i64)[positive],DescribeSpecialization:product(DescribeSpecialization){image:seq[i8]}=>seq[product(SpecializationInfo){specId:alias(Binary32,i64)[long-interval[0,4294967295]],byteSize:alias(Int,i32)[positive]}],SpecializeImage:product(SpecializeImage){image:seq[i8],constants:seq[product(SpecializationConstant){specId:alias(Binary32,i64)[long-interval[0,4294967295]],value:seq[i8]}]}=>seq[i8],UnloadProgram:product(UnloadProgram){program:alias(ProgramId,i64)[positive]}=>unit,DescribeProgram:product(DescribeProgram){program:alias(ProgramId,i64)[positive]}=>product(ProgramSymbols){kernels:seq[product(KernelSymbol){name:alias(String,text)[minimum-length[1],not-contains[00]],linkageName:alias(String,text)[minimum-length[1],not-contains[00]]}],globals:seq[product(GlobalSymbol){name:alias(String,text)[minimum-length[1],not-contains[00]],virtualAddress:option[alias(GuestAddress,i64)[]],size:alias(Long,i64)[positive0],pointerOffsets:seq[alias(Int,i32)[positive0]]}]},DescribeKernel:product(DescribeKernel){program:alias(ProgramId,i64)[positive],name:alias(String,text)[minimum-length[1],not-contains[00]]}=>product(KernelMetadata){argumentBlockSize:alias(Int,i32)[positive0],explicitParameterCount:alias(Int,i32)[positive0],parameters:seq[product(Parameter){width:alias(Int,i32)[positive],offset:alias(Int,i32)[positive0],kind:sum(ParameterKind){Scalar:product(Scalar){},Allocation:product(Allocation){},Local:product(Local){}},containsPointers:bool}],requiredWorkGroupSize:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],workGroupSizeHint:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],bindingIndices:seq[alias(Int,i32)[positive0]],entryPointId:option[alias(Int,i32)[positive0]]},DescribeImage:product(DescribeImage){encoding:text,image:seq[i8],name:alias(String,text)[minimum-length[1],not-contains[00]]}=>product(KernelMetadata){argumentBlockSize:alias(Int,i32)[positive0],explicitParameterCount:alias(Int,i32)[positive0],parameters:seq[product(Parameter){width:alias(Int,i32)[positive],offset:alias(Int,i32)[positive0],kind:sum(ParameterKind){Scalar:product(Scalar){},Allocation:product(Allocation){},Local:product(Local){}},containsPointers:bool}],requiredWorkGroupSize:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],workGroupSizeHint:option[product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]}],bindingIndices:seq[alias(Int,i32)[positive0]],entryPointId:option[alias(Int,i32)[positive0]]},Blas:product(Blas){request:product(BlasRequest){operation:sum(BlasOperation){Axpy:product(Axpy){},Scal:product(Scal){},Copy:product(Copy){},Dot:product(Dot){},Nrm2:product(Nrm2){},Asum:product(Asum){},Gemv:product(Gemv){},Gemm:product(Gemm){},GemmStridedBatched:product(GemmStridedBatched){}},element:sum(BlasElement){F16:product(F16){},F32:product(F32){},F64:product(F64){}},count:i32,rows:i32,columns:i32,inner:i32,transposeA:bool,transposeB:bool,alpha:seq[i8],beta:seq[i8],x:option[product(BlasVector){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],increment:i32}],y:option[product(BlasVector){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],increment:i32}],a:option[product(BlasMatrix){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],leading:i32}],b:option[product(BlasMatrix){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],leading:i32}],c:option[product(BlasMatrix){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],leading:i32}],strideA:i64,strideB:i64,strideC:i64,batch:i32}}=>product(BlasResult){scalar:seq[i8]},DescribeGlobal:product(DescribeGlobal){program:alias(ProgramId,i64)[positive],name:alias(String,text)[minimum-length[1],not-contains[00]]}=>product(ProgramGlobal){size:alias(Long,i64)[positive0],initial:seq[i8]},BindProgramGlobal:product(BindProgramGlobal){program:alias(ProgramId,i64)[positive],name:alias(String,text)[minimum-length[1],not-contains[00]],allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0]}=>unit,WriteProgramGlobal:product(WriteProgramGlobal){program:alias(ProgramId,i64)[positive],name:alias(String,text)[minimum-length[1],not-contains[00]],offset:alias(Long,i64)[positive0],bytes:seq[i8]}=>unit,Launch:product(Launch){program:alias(ProgramId,i64)[positive],kernelName:alias(String,text)[minimum-length[1],not-contains[00]],groupSize:product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]},groupCount:product(Dimensions){x:alias(Int,i32)[positive],y:alias(Int,i32)[positive],z:alias(Int,i32)[positive]},globalSize:product(GlobalDimensions){x:alias(Long,i64)[positive],y:alias(Long,i64)[positive],z:alias(Long,i64)[positive]},workDim:alias(Int,i32)[int-interval[1,3]],globalOffset:product(Coordinates){x:alias(Int,i32)[positive0],y:alias(Int,i32)[positive0],z:alias(Int,i32)[positive0]},privateSegmentBytes:alias(Int,i32)[positive0],groupSegmentBytes:alias(Int,i32)[positive0],arguments:seq[sum(Argument){Scalar:product(Scalar){bytes:seq[i8]},Memory:product(Memory){region:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0]},Local:product(Local){size:alias(Long,i64)[positive]}}],pointerRelocations:seq[product(PointerRelocation){source:sum(RelocationSource){Memory:product(Memory){region:alias(AllocationId,i64)[positive]},Argument:product(Argument){index:alias(Int,i32)[positive0]}},sourceOffset:alias(Long,i64)[positive0],target:alias(AllocationId,i64)[positive],targetOffset:alias(Long,i64)[positive0]}],discoverArgumentRelocations:bool}=>alias(CompletionId,i64)[positive],QueryCompletion:product(QueryCompletion){completion:alias(CompletionId,i64)[positive]}=>sum(CompletionStatus){Pending:product(Pending){},Succeeded:product(Succeeded){},Failed:product(Failed){error:sum(ExecutionFailure){InvalidRequest:product(InvalidRequest){detail:text},NotFound:product(NotFound){detail:text},Unsupported:product(Unsupported){detail:text},Failed:product(Failed){detail:text}}}},AwaitCompletion:product(AwaitCompletion){completion:alias(CompletionId,i64)[positive]}=>unit,CompletionDirtyRanges:product(CompletionDirtyRanges){completion:alias(CompletionId,i64)[positive],offset:alias(Int,i32)[positive0]}=>seq[product(DirtyRange){allocation:alias(AllocationId,i64)[positive],offset:alias(Long,i64)[positive0],size:alias(Int,i32)[positive]}],CompletionPointerRelocations:product(CompletionPointerRelocations){completion:alias(CompletionId,i64)[positive]}=>seq[product(PointerRelocation){source:sum(RelocationSource){Memory:product(Memory){region:alias(AllocationId,i64)[positive]},Argument:product(Argument){index:alias(Int,i32)[positive0]}},sourceOffset:alias(Long,i64)[positive0],target:alias(AllocationId,i64)[positive],targetOffset:alias(Long,i64)[positive0]}],CompletionPrints:product(CompletionPrints){completion:alias(CompletionId,i64)[positive]}=>text,ReleaseCompletion:product(ReleaseCompletion){completion:alias(CompletionId,i64)[positive]}=>unit};failure=sum(ExecutionFailure){InvalidRequest:product(InvalidRequest){detail:text},NotFound:product(NotFound){detail:text},Unsupported:product(Unsupported){detail:text},Failed:product(Failed){detail:text}}";
inline constexpr size_t RequestClientOffset = 4U;
inline constexpr size_t RequestSessionHighOffset = 12U;
inline constexpr size_t RequestSessionLowOffset = 20U;
inline constexpr size_t RequestTagSizeOffset = 28U;
inline constexpr size_t RequestPayloadSizeOffset = 32U;
inline constexpr size_t RequestHeaderBytes = 36U;
inline constexpr size_t ResponseStatusOffset = 4U;
inline constexpr size_t ResponsePayloadSizeOffset = 8U;
inline constexpr size_t ResponseHeaderBytes = 12U;
namespace operation {
inline constexpr std::string_view Ping = "Ping";
inline constexpr std::string_view EnableHostMemory = "EnableHostMemory";
inline constexpr std::string_view PollHostMemory = "PollHostMemory";
inline constexpr std::string_view SupplyHostMemory = "SupplyHostMemory";
inline constexpr std::string_view RegisterAllocation = "RegisterAllocation";
inline constexpr std::string_view SupplyHostSync = "SupplyHostSync";
inline constexpr std::string_view RegisterAllocationAddress = "RegisterAllocationAddress";
inline constexpr std::string_view ReleaseAllocation = "ReleaseAllocation";
inline constexpr std::string_view WriteAllocation = "WriteAllocation";
inline constexpr std::string_view ReadAllocation = "ReadAllocation";
inline constexpr std::string_view LoadProgram = "LoadProgram";
inline constexpr std::string_view DescribeSpecialization = "DescribeSpecialization";
inline constexpr std::string_view SpecializeImage = "SpecializeImage";
inline constexpr std::string_view UnloadProgram = "UnloadProgram";
inline constexpr std::string_view DescribeProgram = "DescribeProgram";
inline constexpr std::string_view DescribeKernel = "DescribeKernel";
inline constexpr std::string_view DescribeImage = "DescribeImage";
inline constexpr std::string_view Blas = "Blas";
inline constexpr std::string_view DescribeGlobal = "DescribeGlobal";
inline constexpr std::string_view BindProgramGlobal = "BindProgramGlobal";
inline constexpr std::string_view WriteProgramGlobal = "WriteProgramGlobal";
inline constexpr std::string_view Launch = "Launch";
inline constexpr std::string_view QueryCompletion = "QueryCompletion";
inline constexpr std::string_view AwaitCompletion = "AwaitCompletion";
inline constexpr std::string_view CompletionDirtyRanges = "CompletionDirtyRanges";
inline constexpr std::string_view CompletionPointerRelocations = "CompletionPointerRelocations";
inline constexpr std::string_view CompletionPrints = "CompletionPrints";
inline constexpr std::string_view ReleaseCompletion = "ReleaseCompletion";
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

struct Binary32 {
  int64_t value{};
};

struct SpecializationInfo {
  Binary32 specId{};
  IntPositive byteSize{};
};

struct SpecializationConstant {
  Binary32 specId{};
  std::vector<uint8_t> value{};
};

struct StringMinimumLength1NotContains00 {
  std::string value{};
};

struct KernelSymbol {
  StringMinimumLength1NotContains00 name{};
  StringMinimumLength1NotContains00 linkageName{};
};

struct GlobalSymbol {
  StringMinimumLength1NotContains00 name{};
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

struct ArgumentScalar {
  std::vector<uint8_t> bytes{};
};
struct ArgumentMemory {
  AllocationId region{};
  LongPositive0 offset{};
};
struct ArgumentLocal {
  LongPositive size{};
};

using Argument = std::variant<ArgumentScalar, ArgumentMemory, ArgumentLocal>;

struct RelocationSourceMemory {
  AllocationId region{};
};
struct RelocationSourceArgument {
  IntPositive0 index{};
};

using RelocationSource = std::variant<RelocationSourceMemory, RelocationSourceArgument>;

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
  StringMinimumLength1NotContains00 name{};
};

struct DescribeImage {
  std::string encoding{};
  std::vector<uint8_t> image{};
  StringMinimumLength1NotContains00 name{};
};

struct Blas {
  BlasRequest request{};
};

struct DescribeGlobal {
  ProgramId program{};
  StringMinimumLength1NotContains00 name{};
};

struct BindProgramGlobal {
  ProgramId program{};
  StringMinimumLength1NotContains00 name{};
  AllocationId allocation{};
  LongPositive0 offset{};
};

struct WriteProgramGlobal {
  ProgramId program{};
  StringMinimumLength1NotContains00 name{};
  LongPositive0 offset{};
  std::vector<uint8_t> bytes{};
};

struct Launch {
  ProgramId program{};
  StringMinimumLength1NotContains00 kernelName{};
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
  IntPositive0 offset{};
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
template <> struct glz::meta<gemstone::execution::Binary32> { static constexpr auto value = &gemstone::execution::Binary32::value; };
template <> struct glz::meta<gemstone::execution::StringMinimumLength1NotContains00> { static constexpr auto value = &gemstone::execution::StringMinimumLength1NotContains00::value; };
template <> struct glz::meta<gemstone::execution::IntInterval1To3> { static constexpr auto value = &gemstone::execution::IntInterval1To3::value; };
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
