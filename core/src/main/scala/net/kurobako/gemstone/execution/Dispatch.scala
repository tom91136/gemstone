package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.rpc.Wire.Codec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

enum KernelArgument derives Codec {
  case Scalar(bytes: Vec[Byte])
  case Allocation(id: AllocationId, offset: Long :| Positive0)
  case Local(size: Long :| Positive)
}

enum RelocationSource derives Codec {
  case Allocation(id: AllocationId)
  case Argument(index: Int :| Positive0)
}

/** A pointer-sized word in mirrored allocation memory that must be interpreted as an allocation pointer. */
final case class PointerRelocation(
    source: RelocationSource,
    sourceOffset: Long :| Positive0,
    target: AllocationId,
    targetOffset: Long :| Positive0
) derives Codec

final case class KernelDispatch private (
    program: ProgramId,
    kernelName: KernelName,
    geometry: LaunchGeometry,
    privateSegmentBytes: Int :| Positive0,
    groupSegmentBytes: Int :| Positive0,
    arguments: Vec[KernelArgument],
    pointerRelocations: Vec[PointerRelocation]
)

object KernelDispatch {
  def validated(
      program: ProgramId,
      kernelName: KernelName,
      groupSize: Dimensions,
      groupCount: Dimensions,
      arguments: Vec[KernelArgument],
      globalSize: Option[GlobalDimensions] = None,
      privateSegmentBytes: Int :| Positive0 = 0,
      groupSegmentBytes: Int :| Positive0 = 0,
      pointerRelocations: Vec[PointerRelocation] = Vec.empty,
      workDim: Int :| Interval.Closed[1, 3] = 3,
      globalOffset: Coordinates = Coordinates(0, 0, 0)
  ): Either[String, KernelDispatch] = for {
    geometry <- LaunchGeometry.forLaunch(groupSize, groupCount, globalSize, workDim, globalOffset)
    _        <- Either.cond(
      pointerRelocations.forall {
        case PointerRelocation(RelocationSource.Argument(index), _, _, _) => index < arguments.size
        case PointerRelocation(RelocationSource.Allocation(_), _, _, _)   => true
      },
      (),
      "pointer relocations must have an in-range source argument"
    )
  } yield KernelDispatch(
    program,
    kernelName,
    geometry,
    privateSegmentBytes,
    groupSegmentBytes,
    arguments,
    pointerRelocations
  )

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
}

enum CompletionStatus derives Codec {
  case Pending
  case Succeeded
  case Failed(error: ExecutionFailure)
}

final case class DirtyRange(allocation: AllocationId, offset: Long :| Positive0, size: Int :| Positive) derives Codec

enum KernelParameterKind derives Codec {
  case Scalar
  case Allocation
  case Local
}

final case class KernelParameterMetadata(
    width: Int :| Positive,
    offset: Int :| Positive0,
    kind: KernelParameterKind,
    containsPointers: Boolean
) derives Codec

/** Format-neutral information needed by a native facade to marshal a kernel launch. */
final case class KernelMetadata private (
    argumentBlockSize: Int :| Positive0,
    explicitParameterCount: Int :| Positive0,
    parameters: Vec[KernelParameterMetadata]
)

object KernelMetadata {
  def validated(
      argumentBlockSize: Int :| Positive0,
      explicitParameterCount: Int :| Positive0,
      parameters: Vec[KernelParameterMetadata]
  ): Either[String, KernelMetadata] = for {
    _ <- Either.cond(
      explicitParameterCount <= parameters.size,
      (),
      "explicit parameter count exceeds the parameter count"
    )
    _ <- Either.cond(
      parameters.forall(parameter =>
        parameter.offset.toLong + parameter.width.toLong <= argumentBlockSize.toLong
      ),
      (),
      "kernel parameter lies outside the argument block"
    )
    _ <- Either.cond(
      parameters.zip(parameters.drop(1)).forall { case (previous, current) =>
        previous.offset.toLong + previous.width.toLong <= current.offset.toLong
      },
      (),
      "kernel parameters must be ordered and non-overlapping"
    )
  } yield KernelMetadata(argumentBlockSize, explicitParameterCount, parameters)

  given Codec[KernelMetadata] = Codec.validated(Codec.derived[KernelMetadata])(value =>
    validated(value.argumentBlockSize, value.explicitParameterCount, value.parameters)
  )
}

/** One callable entry in a loaded program. The linkage name may differ from the source/API name. */
final case class ProgramKernel(name: KernelName, linkageName: LinkageName) derives Codec

/** One addressable program global. Pointer offsets identify relocation-bearing words in its initial image. */
final case class ProgramGlobalMetadata private (
    name: GlobalName,
    virtualAddress: Option[GuestAddress],
    size: Long :| Positive0,
    pointerOffsets: Vec[Int :| Positive0]
)

object ProgramGlobalMetadata {
  private val PointerByteWidth = 8L

  def validated(
      name: GlobalName,
      virtualAddress: Option[GuestAddress],
      size: Long :| Positive0,
      pointerOffsets: Vec[Int :| Positive0]
  ): Either[String, ProgramGlobalMetadata] = for {
    _ <- Either.cond(
      pointerOffsets.zip(pointerOffsets.drop(1)).forall { case (previous, current) => previous < current },
      (),
      "program-global pointer offsets must be sorted and unique"
    )
    _ <- Either.cond(
      pointerOffsets.forall(offset => size >= PointerByteWidth && offset.toLong <= size - PointerByteWidth),
      (),
      "program-global pointer offset lies outside its 64-bit storage slot"
    )
  } yield ProgramGlobalMetadata(name, virtualAddress, size, pointerOffsets)

  given Codec[ProgramGlobalMetadata] = Codec.validated(Codec.derived[ProgramGlobalMetadata])(value =>
    validated(value.name, value.virtualAddress, value.size, value.pointerOffsets)
  )
}

/** Format-neutral symbol information needed to construct process-local API handles. */
final case class ProgramCatalog(kernels: Vec[ProgramKernel], globals: Vec[ProgramGlobalMetadata]) derives Codec

/** Initial contents and extent of a named program global. */
final case class ProgramGlobal private (size: Long :| Positive0, initial: Vec[Byte])

object ProgramGlobal {
  def validated(
      size: Long :| Positive0,
      initial: Vec[Byte]
  ): Either[String, ProgramGlobal] =
    Either.cond(
      initial.length.toLong <= size,
      ProgramGlobal(size, initial),
      "program-global initial contents exceed its size"
    )

  given Codec[ProgramGlobal] = Codec.validated(Codec.derived[ProgramGlobal])(value =>
    validated(value.size, value.initial)
  )
}

/** Immutable dispatch snapshot of a live allocation and its optional client address alias. */
final case class AllocationAddress(
    allocation: AllocationId,
    size: Long :| Positive,
    address: Option[GuestAddress]
)
