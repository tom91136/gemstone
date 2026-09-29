package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.Wire.*
import net.kurobako.gemstone.vm.*

/** Process-neutral values carried by [[ExecutionRPC]]. */
object ExecutionProtocol {
  export net.kurobako.gemstone.constraint.NonEmpty
  export net.kurobako.gemstone.rpc.CompletionId
  export net.kurobako.gemstone.rpc.ProgramId

  // Refined text in this protocol names native symbols. Keep the C-string restriction
  // here; ordinary nonempty strings and their codecs permit embedded NUL characters.
  given nativeName: Codec[NonEmpty[String]] = Codec.validated(Codec.domain(
    "String",
    Codec[String],
    Vector(Wire.Constraint.MinimumLength(1), Wire.Constraint.NotContains("\u0000"))
  )(value => value.refine[NonEmpty[String]], identity)) { value =>
    Either.cond(!value.contains('\u0000'), value, "native symbol name must not contain a NUL character")
  }

  given Codec[AllocationKind] = Codec.derived

  given Codec[LaunchGeometry.Dimensions]       = Codec.derived
  given Codec[LaunchGeometry.Coordinates]      = Codec.derived
  given Codec[LaunchGeometry.GlobalDimensions] = Codec.derived
  given Codec[KernelTask.Argument]             = Codec.derived
  given Codec[KernelTask.RelocationSource]     = Codec.derived
  given Codec[KernelTask.PointerRelocation]    = Codec.derived

  case class HostMemoryRange(address: GuestAddress, size: Int > 0) derives Codec
  case class HostMemoryBytes(address: GuestAddress, bytes: Vec[Byte], atomicWidth: Int = 0) derives Codec
  case class HostMemoryAtomic(address: GuestAddress, size: Int, expected: Long, desired: Long) derives Codec
  case class HostMemorySync(ticket: Long, atomic: Option[HostMemoryAtomic]) derives Codec
  case class HostMemoryBatch(
      reads: Vec[HostMemoryRange],
      writes: Vec[HostMemoryBytes],
      completed: Boolean,
      sync: Option[HostMemorySync] = None
  ) derives Codec

  object ExecutionFailure {
    def fromFault(fault: Fault): ExecutionFailure = fault match {
      case Fault.InvalidTask(detail) => InvalidRequest(detail)
      case Fault.Unsupported(detail) => Unsupported(detail)
      case _                         => Failed(fault.message)
    }
  }

  enum ExecutionFailure derives Codec {
    case InvalidRequest(detail: String)
    case NotFound(detail: String)
    case Unsupported(detail: String)
    case Failed(detail: String)

    def message: String = this match {
      case InvalidRequest(value) => value
      case NotFound(value)       => value
      case Unsupported(value)    => value
      case Failed(value)         => value
    }

    def withDetail(value: String): ExecutionFailure = this match {
      case _: InvalidRequest => InvalidRequest(value)
      case _: NotFound       => NotFound(value)
      case _: Unsupported    => Unsupported(value)
      case _: Failed         => Failed(value)
    }
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

  given Codec[KernelMetadata.ParameterKind] = Codec.derived
  given Codec[KernelMetadata.Parameter]     = Codec.derived

  given Codec[KernelMetadata] = Codec.validated(Codec.derived[KernelMetadata])(value =>
    KernelMetadata.validated(
      value.argumentBlockSize,
      value.explicitParameterCount,
      value.parameters,
      value.requiredWorkGroupSize,
      value.workGroupSizeHint,
      value.bindingIndices,
      value.entryPointId
    )
  )

  given Codec[KernelSymbol] = Codec.derived

  given Codec[GlobalSymbol] = Codec.validated(Codec.derived[GlobalSymbol])(value =>
    GlobalSymbol.validated(value.name, value.virtualAddress, value.size, value.pointerOffsets)
  )

  given Codec[ProgramSymbols] = Codec.derived

  enum BlasOperation derives Codec {
    case Axpy
    case Scal
    case Copy
    case Dot
    case Nrm2
    case Asum
    case Gemv
    case Gemm
    case GemmStridedBatched
  }

  enum BlasElement derives Codec {
    case F16
    case F32
    case F64
  }

  case class BlasVector(allocation: AllocationId, offset: Long >= 0, increment: Int) derives Codec
  case class BlasMatrix(allocation: AllocationId, offset: Long >= 0, leading: Int) derives Codec

  case class BlasRequest(
      operation: BlasOperation,
      element: BlasElement,
      count: Int,
      rows: Int,
      columns: Int,
      inner: Int,
      transposeA: Boolean,
      transposeB: Boolean,
      alpha: Vec[Byte],
      beta: Vec[Byte],
      x: Option[BlasVector],
      y: Option[BlasVector],
      a: Option[BlasMatrix],
      b: Option[BlasMatrix],
      c: Option[BlasMatrix],
      strideA: Long,
      strideB: Long,
      strideC: Long,
      batch: Int
  ) derives Codec

  case class BlasResult(scalar: Vec[Byte]) derives Codec

  case class SpecializationConstant(
      specId: Binary32,
      value: Vec[Byte]
  ) derives Codec

  case class SpecializationInfo(
      specId: Binary32,
      byteSize: Int > 0
  ) derives Codec

  given Codec[GlobalImage] = Codec.validated(Codec.product[GlobalImage]("ProgramGlobal"))(value =>
    GlobalImage.validated(value.size, value.initial)
  )
}
