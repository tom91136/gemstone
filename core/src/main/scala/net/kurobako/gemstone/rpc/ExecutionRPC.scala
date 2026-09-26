package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.{
  NonEmptyString,
  NonNegativeInt,
  NonNegativeLong,
  PositiveInt,
  PositiveLong,
  Vec,
  WorkDimension
}
import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress}
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.Wire.Codec

object ExecutionRPC {
  private inline val IntBytes                       = 4
  private inline val LongBytes                      = 8
  private inline val RequestFingerprintOffsetValue  = IntBytes
  private inline val RequestClientOffsetValue       = RequestFingerprintOffsetValue + LongBytes
  private inline val RequestSessionHighOffsetValue  = RequestClientOffsetValue + LongBytes
  private inline val RequestSessionLowOffsetValue   = RequestSessionHighOffsetValue + LongBytes
  private inline val RequestOperationOffsetValue    = RequestSessionLowOffsetValue + LongBytes
  private inline val RequestPayloadSizeOffsetValue  = RequestOperationOffsetValue + LongBytes
  private inline val RequestHeaderBytesValue        = RequestPayloadSizeOffsetValue + IntBytes
  private inline val ResponseFingerprintOffsetValue = IntBytes
  private inline val ResponseStatusOffsetValue      = ResponseFingerprintOffsetValue + LongBytes
  private inline val ResponsePayloadSizeOffsetValue = ResponseStatusOffsetValue + IntBytes
  private inline val ResponseHeaderBytesValue       = ResponsePayloadSizeOffsetValue + IntBytes

  val Name: NonEmptyString          = NonEmptyString("gemstone.execution")
  val RequestMagic: Int             = 0x474d4532
  val ResponseMagic: Int            = 0x474d4632
  val MaximumPayload: PositiveInt   = PositiveInt(134217728)
  val MaximumElements: PositiveInt  = PositiveInt(16384)
  val MaximumTextBytes: PositiveInt = PositiveInt(8192)
  val Limits: Wire.Limits           = Wire.Limits(
    MaximumElements,
    MaximumPayload,
    MaximumTextBytes
  )

  val RequestFingerprintOffset: PositiveInt = PositiveInt.unsafe(RequestFingerprintOffsetValue)
  val RequestClientOffset: PositiveInt      = PositiveInt.unsafe(RequestClientOffsetValue)
  val RequestSessionHighOffset: PositiveInt = PositiveInt.unsafe(RequestSessionHighOffsetValue)
  val RequestSessionLowOffset: PositiveInt  = PositiveInt.unsafe(RequestSessionLowOffsetValue)
  val RequestOperationOffset: PositiveInt   = PositiveInt.unsafe(RequestOperationOffsetValue)
  val RequestPayloadSizeOffset: PositiveInt = PositiveInt.unsafe(RequestPayloadSizeOffsetValue)
  val RequestHeaderBytes: PositiveInt       = PositiveInt.unsafe(RequestHeaderBytesValue)

  val ResponseFingerprintOffset: PositiveInt = PositiveInt.unsafe(ResponseFingerprintOffsetValue)
  val ResponseStatusOffset: PositiveInt      = PositiveInt.unsafe(ResponseStatusOffsetValue)
  val ResponsePayloadSizeOffset: PositiveInt = PositiveInt.unsafe(ResponsePayloadSizeOffsetValue)
  val ResponseHeaderBytes: PositiveInt       = PositiveInt.unsafe(ResponseHeaderBytesValue)

  enum Operation[Reply](using val replyCodec: Codec[Reply]) derives Wire.Protocol {
    case Ping                                                                     extends Operation[Unit]
    case EnableHostMemory                                                         extends Operation[Unit]
    case PollHostMemory(completion: CompletionId)                                 extends Operation[HostMemoryBatch]
    case SupplyHostMemory(completion: CompletionId, ranges: Vec[HostMemoryBytes]) extends Operation[Unit]
    case RegisterAllocation(kind: AllocationKind, size: PositiveLong)
        extends Operation[AllocationId]
    case SupplyHostSync(completion: CompletionId, ticket: Long, observed: Long) extends Operation[Unit]
    case RegisterAllocationAddress(allocation: AllocationId, nativeBase: GuestAddress, hostBacked: Boolean = false)
        extends Operation[Unit]
    case ReleaseAllocation(allocation: AllocationId)
        extends Operation[Unit]
    case WriteAllocation(allocation: AllocationId, offset: NonNegativeLong, bytes: Vec[Byte])
        extends Operation[Unit]
    case ReadAllocation(allocation: AllocationId, offset: NonNegativeLong, size: NonNegativeInt)
        extends Operation[Vec[Byte]]
    case LoadProgram(encoding: String, image: Vec[Byte])
        extends Operation[ProgramId]
    case DescribeSpecialization(image: Vec[Byte])
        extends Operation[Vec[SpecializationInfo]]
    case SpecializeImage(image: Vec[Byte], constants: Vec[SpecializationConstant])
        extends Operation[Vec[Byte]]
    case UnloadProgram(program: ProgramId)
        extends Operation[Unit]
    case DescribeProgram(program: ProgramId)
        extends Operation[ProgramCatalog]
    case DescribeKernel(program: ProgramId, name: KernelName)
        extends Operation[KernelMetadata]
    case DescribeImage(encoding: String, image: Vec[Byte], name: KernelName)
        extends Operation[KernelMetadata]
    case Blas(request: BlasRequest)
        extends Operation[BlasResult]
    case DescribeGlobal(program: ProgramId, name: GlobalName)
        extends Operation[ProgramGlobal]
    case BindProgramGlobal(program: ProgramId, name: GlobalName, allocation: AllocationId, offset: NonNegativeLong)
        extends Operation[Unit]
    case WriteProgramGlobal(program: ProgramId, name: GlobalName, offset: NonNegativeLong, bytes: Vec[Byte])
        extends Operation[Unit]
    case Launch(
        program: ProgramId,
        kernelName: KernelName,
        groupSize: Dimensions,
        groupCount: Dimensions,
        globalSize: GlobalDimensions,
        workDim: WorkDimension,
        globalOffset: Coordinates,
        privateSegmentBytes: NonNegativeInt,
        groupSegmentBytes: NonNegativeInt,
        arguments: Vec[KernelArgument],
        pointerRelocations: Vec[PointerRelocation],
        discoverArgumentRelocations: Boolean = false
    ) extends Operation[CompletionId]
    case QueryCompletion(completion: CompletionId)
        extends Operation[CompletionStatus]
    case AwaitCompletion(completion: CompletionId)
        extends Operation[Unit]
    case CompletionDirtyRanges(completion: CompletionId)
        extends Operation[Vec[DirtyRange]]
    case CompletionPointerRelocations(completion: CompletionId)
        extends Operation[Vec[PointerRelocation]]
    case CompletionPrints(completion: CompletionId)
        extends Operation[String]
    case ReleaseCompletion(completion: CompletionId)
        extends Operation[Unit]
  }

  lazy val Schema: Wire.ProtocolShape = Wire.Protocol[Operation].schema[ExecutionFailure](Name)
  lazy val Identity: NonEmptyString   =
    NonEmptyString.unsafe(s"$Name@${java.lang.Long.toUnsignedString(Schema.fingerprint, 16)}")
}
