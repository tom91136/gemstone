package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Constraint.IntBetween
import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress}
import net.kurobako.gemstone.isa.{KernelMetadata as ISAKernelMetadata, ProgramSymbols}
import net.kurobako.gemstone.rpc.ExecutionProtocol.{*, given}
import net.kurobako.gemstone.rpc.Wire.Codec
import net.kurobako.gemstone.vm.{KernelTask, LaunchGeometry}

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

  val Name: NonEmptyString            = NonEmptyString("gemstone.execution")
  val RequestMagic: Int               = 0x474d4532
  val ResponseMagic: Int              = 0x474d4632
  val MaximumPayload: Positive[Int]   = 134217728
  val MaximumElements: Positive[Int]  = 16384
  val MaximumTextBytes: Positive[Int] = 8192
  val Limits: Wire.Limits             = Wire.Limits(
    MaximumElements,
    MaximumPayload,
    MaximumTextBytes
  )

  val RequestFingerprintOffset: Positive[Int] = Positive.unsafe(RequestFingerprintOffsetValue)
  val RequestClientOffset: Positive[Int]      = Positive.unsafe(RequestClientOffsetValue)
  val RequestSessionHighOffset: Positive[Int] = Positive.unsafe(RequestSessionHighOffsetValue)
  val RequestSessionLowOffset: Positive[Int]  = Positive.unsafe(RequestSessionLowOffsetValue)
  val RequestOperationOffset: Positive[Int]   = Positive.unsafe(RequestOperationOffsetValue)
  val RequestPayloadSizeOffset: Positive[Int] = Positive.unsafe(RequestPayloadSizeOffsetValue)
  val RequestHeaderBytes: Positive[Int]       = Positive.unsafe(RequestHeaderBytesValue)

  val ResponseFingerprintOffset: Positive[Int] = Positive.unsafe(ResponseFingerprintOffsetValue)
  val ResponseStatusOffset: Positive[Int]      = Positive.unsafe(ResponseStatusOffsetValue)
  val ResponsePayloadSizeOffset: Positive[Int] = Positive.unsafe(ResponsePayloadSizeOffsetValue)
  val ResponseHeaderBytes: Positive[Int]       = Positive.unsafe(ResponseHeaderBytesValue)

  enum Operation[Reply](using val replyCodec: Codec[Reply]) derives Wire.Protocol {
    case Ping                                                                     extends Operation[Unit]
    case EnableHostMemory                                                         extends Operation[Unit]
    case PollHostMemory(completion: CompletionId)                                 extends Operation[HostMemoryBatch]
    case SupplyHostMemory(completion: CompletionId, ranges: Vec[HostMemoryBytes]) extends Operation[Unit]
    case RegisterAllocation(kind: AllocationKind, size: Positive[Long])
        extends Operation[AllocationId]
    case SupplyHostSync(completion: CompletionId, ticket: Long, observed: Long) extends Operation[Unit]
    case RegisterAllocationAddress(allocation: AllocationId, nativeBase: GuestAddress, hostBacked: Boolean = false)
        extends Operation[Unit]
    case ReleaseAllocation(allocation: AllocationId)
        extends Operation[Unit]
    case WriteAllocation(allocation: AllocationId, offset: NonNegative[Long], bytes: Vec[Byte])
        extends Operation[Unit]
    case ReadAllocation(allocation: AllocationId, offset: NonNegative[Long], size: NonNegative[Int])
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
        extends Operation[ProgramSymbols]
    case DescribeKernel(program: ProgramId, name: NonEmptyString)
        extends Operation[ISAKernelMetadata]
    case DescribeImage(encoding: String, image: Vec[Byte], name: NonEmptyString)
        extends Operation[ISAKernelMetadata]
    case Blas(request: BlasRequest)
        extends Operation[BlasResult]
    case DescribeGlobal(program: ProgramId, name: NonEmptyString)
        extends Operation[ProgramGlobal]
    case BindProgramGlobal(
        program: ProgramId,
        name: NonEmptyString,
        allocation: AllocationId,
        offset: NonNegative[Long]
    ) extends Operation[Unit]
    case WriteProgramGlobal(program: ProgramId, name: NonEmptyString, offset: NonNegative[Long], bytes: Vec[Byte])
        extends Operation[Unit]
    case Launch(
        program: ProgramId,
        kernelName: NonEmptyString,
        groupSize: LaunchGeometry.Dimensions,
        groupCount: LaunchGeometry.Dimensions,
        globalSize: LaunchGeometry.GlobalDimensions,
        workDim: IntBetween[1, 3],
        globalOffset: LaunchGeometry.Coordinates,
        privateSegmentBytes: NonNegative[Int],
        groupSegmentBytes: NonNegative[Int],
        arguments: Vec[KernelTask.Argument],
        pointerRelocations: Vec[KernelTask.PointerRelocation],
        discoverArgumentRelocations: Boolean = false
    ) extends Operation[CompletionId]
    case QueryCompletion(completion: CompletionId)
        extends Operation[CompletionStatus]
    case AwaitCompletion(completion: CompletionId)
        extends Operation[Unit]
    case CompletionDirtyRanges(completion: CompletionId)
        extends Operation[Vec[DirtyRange]]
    case CompletionPointerRelocations(completion: CompletionId)
        extends Operation[Vec[KernelTask.PointerRelocation]]
    case CompletionPrints(completion: CompletionId)
        extends Operation[String]
    case ReleaseCompletion(completion: CompletionId)
        extends Operation[Unit]
  }

  lazy val Schema: Wire.ProtocolShape = Wire.Protocol[Operation].schema[ExecutionFailure](Name)
  lazy val Identity: NonEmptyString   =
    NonEmptyString.unsafe(s"$Name@${java.lang.Long.toUnsignedString(Schema.fingerprint, 16)}")
}
