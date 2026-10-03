package net.kurobako.gemstone.rpc

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

  given Codec[ExecutionFailure] = Codec.derived

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
      value.entryPointId,
      value.registerCount
    )
  )

  given Codec[KernelSymbol] = Codec.derived

  given Codec[GlobalSymbol] = Codec.validated(Codec.derived[GlobalSymbol])(value =>
    GlobalSymbol.validated(value.name, value.virtualAddress, value.size, value.pointerOffsets)
  )

  given Codec[ProgramSymbols] = Codec.derived

  given Codec[SpecializationConstant] = Codec.derived

  given Codec[SpecializationInfo] = Codec.derived

  given Codec[GlobalImage.Alias] = Codec.product[GlobalImage.Alias]("ProgramGlobalAlias")

  given Codec[GlobalImage] = Codec.validated(Codec.product[GlobalImage]("ProgramGlobal"))(value =>
    GlobalImage.validated(value.size, value.initial, value.alias)
  )
}
