package net.kurobako.gemstone.isa

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, Vec}
import net.kurobako.gemstone.memory.GuestAddress

/** One addressable program global. Pointer offsets identify relocation-bearing words in its initial image. */
final case class GlobalSymbol private (
    name: GlobalName,
    virtualAddress: Option[GuestAddress],
    size: NonNegativeLong,
    pointerOffsets: Vec[NonNegativeInt]
)

object GlobalSymbol {
  def validated(
      name: GlobalName,
      virtualAddress: Option[GuestAddress],
      size: NonNegativeLong,
      pointerOffsets: Vec[NonNegativeInt]
  ): Either[String, GlobalSymbol] =
    SymbolValidation.globalPointerOffsets(size, pointerOffsets).map(_ =>
      GlobalSymbol(name, virtualAddress, size, pointerOffsets)
    )
}
