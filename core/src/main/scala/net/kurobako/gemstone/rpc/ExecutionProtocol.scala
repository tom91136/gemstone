package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.rpc.Wire.Codec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Process-neutral values carried by [[ExecutionRPC]]. */
object ExecutionProtocol {
  export net.kurobako.gemstone.rpc.{AllocationId, CompletionId, GlobalName, KernelName, LinkageName, ProgramId}

  final case class Dimensions(
      x: Int :| Positive,
      y: Int :| Positive,
      z: Int :| Positive
  ) derives Codec {
    def toGlobalDimensions: GlobalDimensions =
      GlobalDimensions(
        x.toLong.refineUnsafe[Positive],
        y.toLong.refineUnsafe[Positive],
        z.toLong.refineUnsafe[Positive]
      )
  }

  object Dimensions {
    val One: Dimensions = Dimensions(1, 1, 1)
  }

  final case class Coordinates(x: Int :| Positive0, y: Int :| Positive0, z: Int :| Positive0) derives Codec

  final case class GlobalDimensions(
      x: Long :| Positive,
      y: Long :| Positive,
      z: Long :| Positive
  ) derives Codec

  final case class HostMemoryRange(address: GuestAddress, size: Int :| Positive) derives Codec
  final case class HostMemoryBytes(address: GuestAddress, bytes: Vec[Byte], atomicWidth: Int = 0) derives Codec
  final case class HostMemoryAtomic(address: GuestAddress, size: Int, expected: Long, desired: Long) derives Codec
  final case class HostMemorySync(ticket: Long, atomic: Option[HostMemoryAtomic]) derives Codec
  final case class HostMemoryBatch(
      reads: Vec[HostMemoryRange],
      writes: Vec[HostMemoryBytes],
      completed: Boolean,
      sync: Option[HostMemorySync] = None
  ) derives Codec

  enum KernelArgument derives Codec {
    case Scalar(bytes: Vec[Byte])
    case Allocation(id: AllocationId, offset: Long :| Positive0)
    case Local(size: Long :| Positive)
  }

  enum RelocationSource derives Codec {
    case Allocation(id: AllocationId)
    case Argument(index: Int :| Positive0)
  }

  final case class PointerRelocation(
      source: RelocationSource,
      sourceOffset: Long :| Positive0,
      target: AllocationId,
      targetOffset: Long :| Positive0
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
  }

  enum CompletionStatus derives Codec {
    case Pending
    case Succeeded
    case Failed(error: ExecutionFailure)
  }

  final case class DirtyRange(
      allocation: AllocationId,
      offset: Long :| Positive0,
      size: Int :| Positive
  ) derives Codec

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

  final case class ProgramKernel(name: KernelName, linkageName: LinkageName) derives Codec

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

  final case class ProgramCatalog(
      kernels: Vec[ProgramKernel],
      globals: Vec[ProgramGlobalMetadata]
  ) derives Codec

  final case class ProgramGlobal private (size: Long :| Positive0, initial: Vec[Byte])

  object ProgramGlobal {
    def validated(size: Long :| Positive0, initial: Vec[Byte]): Either[String, ProgramGlobal] =
      Either.cond(
        initial.length.toLong <= size,
        ProgramGlobal(size, initial),
        "program-global initial contents exceed its size"
      )

    given Codec[ProgramGlobal] = Codec.validated(Codec.derived[ProgramGlobal])(value =>
      validated(value.size, value.initial)
    )
  }

  enum PrintArgument derives Codec {
    case Integer(bits: Vec[Long], width: Int, isSigned: Boolean)
    case Floating(bits: Vec[Long], width: Int)
    case Pointer(address: Long)
    case Text(value: String)
  }

  final case class PrintEvent(format: String, arguments: Vec[PrintArgument]) derives Codec
}
