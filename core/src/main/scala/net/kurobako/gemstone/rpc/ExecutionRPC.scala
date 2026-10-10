package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.*

object ExecutionRPC {
  val Name: NonEmpty[String]      = "gemstone.execution"
  val RequestMagic: Int           = 0x474d4533
  val ResponseMagic: Int          = 0x474d4633
  val HelloMagic: Int             = 0x474d4833
  val HelloResponseMagic: Int     = 0x474d4933
  val MaximumSchemaBytes: Int > 0 = 1048576
  val MaximumTagBytes: Int > 0    = 256
  val MaximumPayload: Int > 0     = 134217728
  val MaximumElements: Int > 0    = 16384
  val MaximumTextBytes: Int > 0   = 8192
  val Limits: msgpack.Limits      = msgpack.Limits(MaximumElements, MaximumPayload, MaximumTextBytes)

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

  // Refined text in this protocol names native symbols. Keep the C-string restriction
  // here; ordinary nonempty strings and their codecs permit embedded NUL characters.
  @SuppressWarnings(Array("scalafix:GivenPlacement"))
  given nativeName: Codec[NonEmpty[String]] = Codec.domain(
    "String",
    Codec[String],
    Vector(Wire.Constraint.MinimumLength(1), Wire.Constraint.NotContains("\u0000"))
  )(
    value =>
      value.refine[NonEmpty[String]]
        .filterOrElse(!_.contains('\u0000'), "native symbol name must not contain a NUL character"),
    identity
  )

  case class HostMemoryRange(address: GuestAddress, size: Int > 0) derives Codec
  case class HostMemoryBytes(address: GuestAddress, bytes: Vec[Byte], atomicWidth: Int in (0, 1, 2, 4, 8) = 0)
      derives Codec
  enum HostMemoryAtomicOperation derives Codec {
    case CompareExchange
    case Load
    case Store
  }
  case class HostMemoryAtomic(
      address: GuestAddress,
      size: Int in (1, 2, 4, 8),
      expected: Long,
      desired: Long,
      operation: HostMemoryAtomicOperation = HostMemoryAtomicOperation.CompareExchange
  ) derives Codec
  case class HostMemorySync(
      ticket: Long,
      atomic: Option[HostMemoryAtomic],
      publish: Vec[HostMemoryRange] = Vec.empty,
      atomicStores: Vec[HostMemoryAtomic] = Vec.empty,
      relaxed: Boolean = false
  ) derives Codec
  case class HostMemoryBatch(
      reads: Vec[HostMemoryRange],
      writes: Vec[HostMemoryBytes],
      completed: Boolean,
      sync: Option[HostMemorySync] = None
  ) derives Codec

  object ExecutionFailure {
    // Encoding fits every detail to the wire, so failures nested in replies cannot make a response unencodable.
    given Codec[ExecutionFailure] = {
      val derived = Codec.derived[ExecutionFailure]
      Codec.mapped(derived.shape, derived)(Right(_), bounded(_))
    }
  }

  /** Details name no native symbols, but the client reads them as bounded C strings. */
  enum ExecutionFailure(val message: NonEmpty[String]) {
    case InvalidRequest(detail: NonEmpty[String]) extends ExecutionFailure(detail)
    case NotFound(detail: NonEmpty[String])       extends ExecutionFailure(detail)
    case Unsupported(detail: NonEmpty[String])    extends ExecutionFailure(detail)
    case Failed(detail: NonEmpty[String])         extends ExecutionFailure(detail)
  }

  private def bounded(error: ExecutionFailure): ExecutionFailure = {
    val detail = truncateUtf8(error.message.replace('\u0000', '?'), MaximumTextBytes.raw)
      .refine[NonEmpty[String]].getOrElse[NonEmpty[String]]("execution failed")
    error match {
      case ExecutionFailure.InvalidRequest(_) => ExecutionFailure.InvalidRequest(detail)
      case ExecutionFailure.NotFound(_)       => ExecutionFailure.NotFound(detail)
      case ExecutionFailure.Unsupported(_)    => ExecutionFailure.Unsupported(detail)
      case ExecutionFailure.Failed(_)         => ExecutionFailure.Failed(detail)
    }
  }

  private def truncateUtf8(value: String, maximumBytes: Int): String =
    if (value.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= maximumBytes) value
    else {
      val result = StringBuilder()
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var index = 0
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var bytes = 0
      while (index < value.length) {
        val codePoint = value.codePointAt(index)
        val width     =
          if (codePoint <= 0x7f) 1
          else if (codePoint <= 0x7ff) 2
          else if (codePoint <= 0xffff) 3
          else 4
        if (bytes + width > maximumBytes) index = value.length
        else {
          result.appendAll(Character.toChars(codePoint))
          bytes += width
          index += Character.charCount(codePoint)
        }
      }
      result.result()
    }

  enum CompletionStatus derives Codec {
    case Pending
    case Succeeded
    case Failed(error: ExecutionFailure)
  }

  case class DirtyRange(
      allocation: AllocationId,
      offset: Long >= 0,
      size: Int > 0
  ) derives Codec

  // Symbol codecs stay with the protocol so that their names derive through nativeName.
  @SuppressWarnings(Array("scalafix:GivenPlacement"))
  given Codec[KernelSymbol] = Codec.derived

  @SuppressWarnings(Array("scalafix:GivenPlacement"))
  given Codec[GlobalSymbol] = Codec.validated(Codec.derived[GlobalSymbol])(value =>
    GlobalSymbol.validated(value.name, value.virtualAddress, value.size, value.pointerOffsets)
  )

  @SuppressWarnings(Array("scalafix:GivenPlacement"))
  given Codec[ProgramSymbols] = Codec.derived

  @SuppressWarnings(Array("scalafix:GivenPlacement"))
  given Codec[ProgramGlobalAlias] = Codec.derived

  @SuppressWarnings(Array("scalafix:GivenPlacement"))
  given Codec[ProgramGlobal] = Codec.validated(Codec.derived[ProgramGlobal])(value =>
    ProgramGlobal.validated(value.size, value.initial, value.alias)
  )

  enum Operation[Reply](using val replyCodec: Codec[Reply]) derives Wire.Protocol {
    case Ping                                                                     extends Operation[Unit]
    case EnableHostMemory                                                         extends Operation[Unit]
    case PollHostMemory(completion: CompletionId)                                 extends Operation[HostMemoryBatch]
    case SupplyHostMemory(completion: CompletionId, ranges: Vec[HostMemoryBytes]) extends Operation[Unit]
    case RegisterAllocation(size: Long in 1L ~ 2147483647L)                       extends Operation[AllocationId]
    case SupplyHostSync(completion: CompletionId, ticket: Long, observed: Long)   extends Operation[Unit]
    case RegisterAllocationAddress(allocation: AllocationId, nativeBase: GuestAddress, hostBacked: Boolean = false)
        extends Operation[Unit]
    case ReleaseAllocation(allocation: AllocationId)
        extends Operation[Unit]
    case WriteAllocation(allocation: AllocationId, offset: Long >= 0, bytes: Vec[Byte])
        extends Operation[Unit]
    case ReadAllocation(allocation: AllocationId, offset: Long >= 0, size: Int >= 0)
        extends Operation[Vec[Byte]]
    case LoadProgram(encoding: ProgramEncoding, image: Vec[Byte])
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
    case DescribeImage(encoding: ProgramEncoding, image: Vec[Byte], name: NonEmpty[String])
        extends Operation[KernelMetadata]
    case DescribeGlobal(program: ProgramId, name: NonEmpty[String])
        extends Operation[ProgramGlobal]
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
        workDim: Int in 1 ~ 3,
        globalOffset: LaunchGeometry.Coordinates,
        privateSegmentBytes: Int >= 0,
        groupSegmentBytes: Int >= 0,
        arguments: Vec[KernelTask.Argument],
        pointerRelocations: Vec[KernelTask.PointerRelocation],
        discoverArgumentRelocations: Boolean = false,
        descriptorBindings: Vec[DescriptorBinding] = Vec.empty,
        argumentBuffer: Option[KernelTask.ArgumentBuffer] = None
    ) extends Operation[CompletionId]
    case QueryCompletion(completion: CompletionId)
        extends Operation[CompletionStatus]
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
