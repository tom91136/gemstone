package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveInt, PositiveLong, Vec}
import net.kurobako.gemstone.isa.SymbolValidation
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.rpc.Wire.Codec

/** Process-neutral values carried by [[ExecutionRPC]]. */
object ExecutionProtocol {
  export net.kurobako.gemstone.isa.{GlobalName, KernelName, LinkageName}
  export net.kurobako.gemstone.rpc.{AllocationId, CompletionId, ProgramId}

  final case class Dimensions(
      x: PositiveInt,
      y: PositiveInt,
      z: PositiveInt
  ) derives Codec {
    def toGlobalDimensions: GlobalDimensions =
      GlobalDimensions(
        x.toLong,
        y.toLong,
        z.toLong
      )
  }

  object Dimensions {
    val One: Dimensions = Dimensions(PositiveInt.One, PositiveInt.One, PositiveInt.One)
  }

  final case class Coordinates(x: NonNegativeInt, y: NonNegativeInt, z: NonNegativeInt) derives Codec

  final case class GlobalDimensions(
      x: PositiveLong,
      y: PositiveLong,
      z: PositiveLong
  ) derives Codec

  final case class HostMemoryRange(address: GuestAddress, size: PositiveInt) derives Codec
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
    case Allocation(id: AllocationId, offset: NonNegativeLong)
    case Local(size: PositiveLong)
  }

  enum RelocationSource derives Codec {
    case Allocation(id: AllocationId)
    case Argument(index: NonNegativeInt)
  }

  final case class PointerRelocation(
      source: RelocationSource,
      sourceOffset: NonNegativeLong,
      target: AllocationId,
      targetOffset: NonNegativeLong
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

  enum CompletionStatus derives Codec {
    case Pending
    case Succeeded
    case Failed(error: ExecutionFailure)
  }

  final case class DirtyRange(
      allocation: AllocationId,
      offset: NonNegativeLong,
      size: PositiveInt
  ) derives Codec

  enum KernelParameterKind derives Codec {
    case Scalar
    case Allocation
    case Local
  }

  final case class KernelParameterMetadata(
      width: PositiveInt,
      offset: NonNegativeInt,
      kind: KernelParameterKind,
      containsPointers: Boolean
  ) derives Codec

  final case class KernelMetadata private (
      argumentBlockSize: NonNegativeInt,
      explicitParameterCount: NonNegativeInt,
      parameters: Vec[KernelParameterMetadata],
      requiredWorkGroupSize: Option[Dimensions],
      workGroupSizeHint: Option[Dimensions],
      bindingIndices: Vec[NonNegativeInt],
      entryPointId: Option[NonNegativeInt]
  )

  object KernelMetadata {
    def validated(
        argumentBlockSize: NonNegativeInt,
        explicitParameterCount: NonNegativeInt,
        parameters: Vec[KernelParameterMetadata],
        requiredWorkGroupSize: Option[Dimensions] = None,
        workGroupSizeHint: Option[Dimensions] = None,
        bindingIndices: Vec[NonNegativeInt] = Vec.empty,
        entryPointId: Option[NonNegativeInt] = None
    ): Either[String, KernelMetadata] =
      SymbolValidation
        .kernelParameters(
          argumentBlockSize,
          explicitParameterCount,
          parameters.map(parameter => parameter.offset -> parameter.width)
        )
        .map(_ =>
          KernelMetadata(
            argumentBlockSize,
            explicitParameterCount,
            parameters,
            requiredWorkGroupSize,
            workGroupSizeHint,
            bindingIndices,
            entryPointId
          )
        )

    given Codec[KernelMetadata] = Codec.validated(Codec.derived[KernelMetadata])(value =>
      validated(
        value.argumentBlockSize,
        value.explicitParameterCount,
        value.parameters,
        value.requiredWorkGroupSize,
        value.workGroupSizeHint,
        value.bindingIndices,
        value.entryPointId
      )
    )
  }

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

  final case class BlasVector(allocation: AllocationId, offset: NonNegativeLong, increment: Int) derives Codec
  final case class BlasMatrix(allocation: AllocationId, offset: NonNegativeLong, leading: Int) derives Codec

  final case class BlasRequest(
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

  final case class BlasResult(scalar: Vec[Byte]) derives Codec

  final case class SpecializationConstant(
      specId: NonNegativeInt,
      value: Vec[Byte]
  ) derives Codec

  final case class SpecializationInfo(
      specId: NonNegativeInt,
      byteSize: PositiveInt
  ) derives Codec

  final case class ProgramKernel(name: KernelName, linkageName: LinkageName) derives Codec

  final case class ProgramGlobalMetadata private (
      name: GlobalName,
      virtualAddress: Option[GuestAddress],
      size: NonNegativeLong,
      pointerOffsets: Vec[NonNegativeInt]
  )

  object ProgramGlobalMetadata {
    def validated(
        name: GlobalName,
        virtualAddress: Option[GuestAddress],
        size: NonNegativeLong,
        pointerOffsets: Vec[NonNegativeInt]
    ): Either[String, ProgramGlobalMetadata] =
      SymbolValidation
        .globalPointerOffsets(size, pointerOffsets)
        .map(_ => ProgramGlobalMetadata(name, virtualAddress, size, pointerOffsets))

    given Codec[ProgramGlobalMetadata] = Codec.validated(Codec.derived[ProgramGlobalMetadata])(value =>
      validated(value.name, value.virtualAddress, value.size, value.pointerOffsets)
    )
  }

  final case class ProgramCatalog(
      kernels: Vec[ProgramKernel],
      globals: Vec[ProgramGlobalMetadata]
  ) derives Codec

  final case class ProgramGlobal private (size: NonNegativeLong, initial: Vec[Byte])

  object ProgramGlobal {
    def validated(size: NonNegativeLong, initial: Vec[Byte]): Either[String, ProgramGlobal] =
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
