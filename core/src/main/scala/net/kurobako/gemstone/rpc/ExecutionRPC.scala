package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.{*, given}
import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.*

object ExecutionRPC {
  val Name: NonEmpty[String]      = "gemstone.execution"
  val Identity: NonEmpty[String]  = "gemstone.execution/3"
  val RequestMagic: Int           = 0x474d4533
  val ResponseMagic: Int          = 0x474d4633
  val HelloMagic: Int             = 0x474d4833
  val HelloResponseMagic: Int     = 0x474d4933
  val MaximumSchemaBytes: Int > 0 = 1048576
  val MaximumTagBytes: Int > 0    = 256
  val MaximumPayload: Int > 0     = 134217728
  val MaximumElements: Int > 0    = 16384
  val MaximumTextBytes: Int > 0   = 8192
  val Limits: Wire.Limits         = Wire.Limits(MaximumElements, MaximumPayload, MaximumTextBytes)

  // Fixed prefix, followed by the UTF-8 operation tag and then the MessagePack payload.
  val RequestClientOffset: Int > 0       = 4
  val RequestSessionHighOffset: Int > 0  = 12
  val RequestSessionLowOffset: Int > 0   = 20
  val RequestTagSizeOffset: Int > 0      = 28
  val RequestPayloadSizeOffset: Int > 0  = 32
  val RequestHeaderBytes: Int > 0        = 36
  val ResponseStatusOffset: Int > 0      = 4
  val ResponsePayloadSizeOffset: Int > 0 = 8
  val ResponseHeaderBytes: Int > 0       = 12

  enum Operation[Reply](using val replyCodec: Codec[Reply]) derives Wire.Protocol {
    case Ping                                                                     extends Operation[Unit]
    case EnableHostMemory                                                         extends Operation[Unit]
    case PollHostMemory(completion: CompletionId)                                 extends Operation[HostMemoryBatch]
    case SupplyHostMemory(completion: CompletionId, ranges: Vec[HostMemoryBytes]) extends Operation[Unit]
    case RegisterAllocation(kind: AllocationKind, size: Long > 0)
        extends Operation[AllocationId]
    case SupplyHostSync(completion: CompletionId, ticket: Long, observed: Long) extends Operation[Unit]
    case RegisterAllocationAddress(allocation: AllocationId, nativeBase: GuestAddress, hostBacked: Boolean = false)
        extends Operation[Unit]
    case ReleaseAllocation(allocation: AllocationId)
        extends Operation[Unit]
    case WriteAllocation(allocation: AllocationId, offset: Long >= 0, bytes: Vec[Byte])
        extends Operation[Unit]
    case ReadAllocation(allocation: AllocationId, offset: Long >= 0, size: Int >= 0)
        extends Operation[Vec[Byte]]
    case LoadProgram(encoding: String, image: Vec[Byte])
        extends Operation[ProgramId]
    case DescribeSpecialization(image: Vec[Byte])
        extends Operation[Vec[SpecializationInfo]]
    case SpecializeImage(image: Vec[Byte], constants: Vec[SpecializationConstant])
        extends Operation[Vec[Byte]]
    case PrepareShader(
        image: Vec[Byte],
        entry: NonEmpty[String],
        stage: ShaderStageKind,
        constants: Vec[SpecializationConstant]
    )                                                     extends Operation[PreparedShader]
    case SubmitGraphics(submission: GraphicsSubmission)   extends Operation[CompletionId]
    case ReadGraphicsStatistics(completion: CompletionId) extends Operation[GraphicsStatistics]
    case UnloadProgram(program: ProgramId)
        extends Operation[Unit]
    case DescribeProgram(program: ProgramId)
        extends Operation[ProgramSymbols]
    case DescribeKernel(program: ProgramId, name: NonEmpty[String])
        extends Operation[KernelMetadata]
    case DescribeImage(encoding: String, image: Vec[Byte], name: NonEmpty[String])
        extends Operation[KernelMetadata]
    case DescribeGlobal(program: ProgramId, name: NonEmpty[String])
        extends Operation[GlobalImage]
    case BindProgramGlobal(
        program: ProgramId,
        name: NonEmpty[String],
        allocation: AllocationId,
        offset: Long >= 0
    ) extends Operation[Unit]
    case WriteProgramGlobal(program: ProgramId, name: NonEmpty[String], offset: Long >= 0, bytes: Vec[Byte])
        extends Operation[Unit]
    case Launch(
        program: ProgramId,
        kernelName: NonEmpty[String],
        groupSize: LaunchGeometry.Dimensions,
        groupCount: LaunchGeometry.Dimensions,
        globalSize: LaunchGeometry.GlobalDimensions,
        workDim: Int where Between[1, 3],
        globalOffset: LaunchGeometry.Coordinates,
        privateSegmentBytes: Int >= 0,
        groupSegmentBytes: Int >= 0,
        arguments: Vec[KernelTask.Argument],
        pointerRelocations: Vec[KernelTask.PointerRelocation],
        discoverArgumentRelocations: Boolean = false,
        descriptorBindings: Vec[DescriptorBinding] = Vec.empty
    ) extends Operation[CompletionId]
    case QueryCompletion(completion: CompletionId)
        extends Operation[CompletionStatus]
    case AwaitCompletion(completion: CompletionId)
        extends Operation[Unit]
    // Stable pages of at most MaximumElements records; a short page ends the result.
    case CompletionDirtyRanges(completion: CompletionId, offset: Int >= 0)
        extends Operation[Vec[DirtyRange]]
    case CompletionPointerRelocations(completion: CompletionId, offset: Int >= 0)
        extends Operation[Vec[KernelTask.PointerRelocation]]
    case CompletionPrints(completion: CompletionId)
        extends Operation[String]
    case ReleaseCompletion(completion: CompletionId)
        extends Operation[Unit]
  }

  lazy val Schema: Wire.ProtocolShape = Wire.Protocol[Operation].schema[ExecutionFailure](Name)
}
