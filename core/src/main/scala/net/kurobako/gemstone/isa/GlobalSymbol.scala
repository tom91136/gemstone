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
  private val PointerByteWidth = 8L

  def validated(
      name: GlobalName,
      virtualAddress: Option[GuestAddress],
      size: Long :| Positive0,
      pointerOffsets: Vec[Int :| Positive0]
  ): Either[String, GlobalSymbol] =
    for {
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
    } yield GlobalSymbol(name, virtualAddress, size, pointerOffsets)
}
