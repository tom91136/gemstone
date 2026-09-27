package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Constraint.NonNegative
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress

/** One addressable program global. Pointer offsets identify relocation-bearing words in its initial image. */
case class GlobalSymbol private (
    name: NonEmptyString,
    virtualAddress: Option[GuestAddress],
    size: NonNegative[Long],
    pointerOffsets: Vec[NonNegative[Int]]
)

object GlobalSymbol {
  def validated(
      name: NonEmptyString,
      virtualAddress: Option[GuestAddress],
      size: NonNegative[Long],
      pointerOffsets: Vec[NonNegative[Int]]
  ): Either[String, GlobalSymbol] =
    SymbolValidation.globalPointerOffsets(size, pointerOffsets).map(_ =>
      GlobalSymbol(name, virtualAddress, size, pointerOffsets)
    )
}
