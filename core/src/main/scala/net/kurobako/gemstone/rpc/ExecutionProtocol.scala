package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.{GlobalSymbol, KernelMetadata as ISAKernelMetadata, KernelSymbol, ProgramSymbols}
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.rpc.Wire.Codec
import net.kurobako.gemstone.vm.{KernelTask, LaunchGeometry, MemoryRegion}

/** Process-neutral values carried by [[ExecutionRPC]]. */
object ExecutionProtocol {
  export net.kurobako.gemstone.Constraint.NonEmptyString
  export net.kurobako.gemstone.rpc.{AllocationId, CompletionId, ProgramId}

  given Codec[MemoryRegion] =
    Codec.domain("MemoryRegion", Codec[Long], Vector.empty)(value => Right(MemoryRegion(value)), _.value)

  given Codec[LaunchGeometry.Dimensions]       = Codec.derived
  given Codec[LaunchGeometry.Coordinates]      = Codec.derived
  given Codec[LaunchGeometry.GlobalDimensions] = Codec.derived
  given Codec[KernelTask.Argument]             = Codec.derived
  given Codec[KernelTask.RelocationSource]     = Codec.derived
  given Codec[KernelTask.PointerRelocation]    = Codec.derived

  case class HostMemoryRange(address: GuestAddress, size: Positive[Int]) derives Codec
  case class HostMemoryBytes(address: GuestAddress, bytes: Vec[Byte], atomicWidth: Int = 0) derives Codec
  case class HostMemoryAtomic(address: GuestAddress, size: Int, expected: Long, desired: Long) derives Codec
  case class HostMemorySync(ticket: Long, atomic: Option[HostMemoryAtomic]) derives Codec
  case class HostMemoryBatch(
      reads: Vec[HostMemoryRange],
      writes: Vec[HostMemoryBytes],
      completed: Boolean,
      sync: Option[HostMemorySync] = None
  ) derives Codec

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

  object ExecutionFailure {
    def fromFault(fault: net.kurobako.gemstone.vm.Fault): ExecutionFailure = fault match {
      case net.kurobako.gemstone.vm.Fault.InvalidTask(detail) => InvalidRequest(detail)
      case net.kurobako.gemstone.vm.Fault.Unsupported(detail) => Unsupported(detail)
      case _                                                  => Failed(fault.message)
    }
  }

  enum CompletionStatus derives Codec {
    case Pending
    case Succeeded
    case Failed(error: ExecutionFailure)
  }

  case class DirtyRange(
      allocation: AllocationId,
      offset: NonNegative[Long],
      size: Positive[Int]
  ) derives Codec

  given Codec[ISAKernelMetadata.ParameterKind] = Codec.derived
  given Codec[ISAKernelMetadata.Parameter]     = Codec.derived

  given Codec[ISAKernelMetadata] = Codec.validated(Codec.derived[ISAKernelMetadata])(value =>
    ISAKernelMetadata.validated(
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

  case class BlasVector(allocation: AllocationId, offset: NonNegative[Long], increment: Int) derives Codec
  case class BlasMatrix(allocation: AllocationId, offset: NonNegative[Long], leading: Int) derives Codec

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
      specId: NonNegative[Int],
      value: Vec[Byte]
  ) derives Codec

  case class SpecializationInfo(
      specId: NonNegative[Int],
      byteSize: Positive[Int]
  ) derives Codec

  case class ProgramGlobal private (size: NonNegative[Long], initial: Vec[Byte])

  object ProgramGlobal {
    def validated(size: NonNegative[Long], initial: Vec[Byte]): Either[String, ProgramGlobal] =
      Either.cond(
        initial.length.toLong <= size.raw,
        ProgramGlobal(size, initial),
        "program-global initial contents exceed its size"
      )

    given Codec[ProgramGlobal] = Codec.validated(Codec.derived[ProgramGlobal])(value =>
      validated(value.size, value.initial)
    )
  }
}
