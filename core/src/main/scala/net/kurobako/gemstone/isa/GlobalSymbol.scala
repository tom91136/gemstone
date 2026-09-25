package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** One addressable program global. Pointer offsets identify relocation-bearing words in its initial image. */
final case class GlobalSymbol private (
    name: GlobalName,
    virtualAddress: Option[GuestAddress],
    size: Long :| Positive0,
    pointerOffsets: Vec[Int :| Positive0]
)

object GlobalSymbol {
  def validated(
      name: GlobalName,
      virtualAddress: Option[GuestAddress],
      size: Long :| Positive0,
      pointerOffsets: Vec[Int :| Positive0]
  ): Either[String, GlobalSymbol] =
    SymbolValidation.globalPointerOffsets(size, pointerOffsets).map(_ =>
      GlobalSymbol(name, virtualAddress, size, pointerOffsets)
    )
}
