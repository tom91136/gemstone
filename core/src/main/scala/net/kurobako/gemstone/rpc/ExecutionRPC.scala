package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.{NoNul, Vec}
import net.kurobako.gemstone.memory.{AllocationKind, GuestAddress}
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.Wire.Codec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

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

  val Name: String :| (MinLength[1] & NoNul) = "gemstone.execution"
  val RequestMagic: Int                      = 0x474d4532
  val ResponseMagic: Int                     = 0x474d4632
  val MaximumPayload: Int :| Positive        = 134217728
  val MaximumElements: Int :| Positive       = 16384
  val MaximumTextBytes: Int :| Positive      = 8192
  val Limits: Wire.Limits                    = Wire.Limits(
    MaximumElements,
    MaximumPayload,
    MaximumTextBytes
  )

  val RequestFingerprintOffset: Int :| Positive = RequestFingerprintOffsetValue
  val RequestClientOffset: Int :| Positive      = RequestClientOffsetValue
  val RequestSessionHighOffset: Int :| Positive = RequestSessionHighOffsetValue
  val RequestSessionLowOffset: Int :| Positive  = RequestSessionLowOffsetValue
  val RequestOperationOffset: Int :| Positive   = RequestOperationOffsetValue
  val RequestPayloadSizeOffset: Int :| Positive = RequestPayloadSizeOffsetValue
  val RequestHeaderBytes: Int :| Positive       = RequestHeaderBytesValue

  val ResponseFingerprintOffset: Int :| Positive = ResponseFingerprintOffsetValue
  val ResponseStatusOffset: Int :| Positive      = ResponseStatusOffsetValue
  val ResponsePayloadSizeOffset: Int :| Positive = ResponsePayloadSizeOffsetValue
  val ResponseHeaderBytes: Int :| Positive       = ResponseHeaderBytesValue

  enum Operation[Reply](using val replyCodec: Codec[Reply]) derives Wire.Protocol {
    case Ping                                                                     extends Operation[Unit]
    case EnableHostMemory                                                         extends Operation[Unit]
    case PollHostMemory(completion: CompletionId)                                 extends Operation[HostMemoryBatch]
    case SupplyHostMemory(completion: CompletionId, ranges: Vec[HostMemoryBytes]) extends Operation[Unit]
    case RegisterAllocation(kind: AllocationKind, size: Long :| Positive)
        extends Operation[AllocationId]
    case SupplyHostSync(completion: CompletionId, ticket: Long, observed: Long) extends Operation[Unit]
    case RegisterAllocationAddress(allocation: AllocationId, nativeBase: GuestAddress, hostBacked: Boolean = false)
        extends Operation[Unit]
    case ReleaseAllocation(allocation: AllocationId)
        extends Operation[Unit]
    case WriteAllocation(allocation: AllocationId, offset: Long :| Positive0, bytes: Vec[Byte])
        extends Operation[Unit]
    case ReadAllocation(allocation: AllocationId, offset: Long :| Positive0, size: Int :| Positive0)
        extends Operation[Vec[Byte]]
    case LoadProgram(encoding: String, image: Vec[Byte])
        extends Operation[ProgramId]
    case UnloadProgram(program: ProgramId)
        extends Operation[Unit]
    case DescribeProgram(program: ProgramId)
        extends Operation[ProgramCatalog]
    case DescribeKernel(program: ProgramId, name: KernelName)
        extends Operation[KernelMetadata]
    case DescribeGlobal(program: ProgramId, name: GlobalName)
        extends Operation[ProgramGlobal]
    case BindProgramGlobal(program: ProgramId, name: GlobalName, allocation: AllocationId, offset: Long :| Positive0)
        extends Operation[Unit]
    case WriteProgramGlobal(program: ProgramId, name: GlobalName, offset: Long :| Positive0, bytes: Vec[Byte])
        extends Operation[Unit]
    case Launch(
        program: ProgramId,
        kernelName: KernelName,
        groupSize: Dimensions,
        groupCount: Dimensions,
        globalSize: GlobalDimensions,
        workDim: Int :| Interval.Closed[1, 3],
        globalOffset: Coordinates,
        privateSegmentBytes: Int :| Positive0,
        groupSegmentBytes: Int :| Positive0,
        arguments: Vec[KernelArgument],
        pointerRelocations: Vec[PointerRelocation]
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
        extends Operation[Vec[PrintEvent]]
    case ReleaseCompletion(completion: CompletionId)
        extends Operation[Unit]
  }

  lazy val Schema: Wire.ProtocolShape                 = Wire.Protocol[Operation].schema[ExecutionFailure](Name)
  lazy val Identity: String :| (MinLength[1] & NoNul) =
    s"$Name@${java.lang.Long.toUnsignedString(Schema.fingerprint, 16)}".refineUnsafe[MinLength[1] & NoNul]
}
