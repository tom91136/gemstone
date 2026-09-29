package net.kurobako.gemstone.isa

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

object GlobalSymbol {
  def validated(
      name: NonEmpty[String],
      virtualAddress: Option[GuestAddress],
      size: Long >= 0,
      pointerOffsets: Vec[Int >= 0]
  ): Either[NonEmpty[String], GlobalSymbol] =
    for {
      _ <- Either.cond[NonEmpty[String], Unit](
        pointerOffsets.monotonic(_ < _),
        (),
        "program-global pointer offsets must be sorted and unique"
      )
      _ <- Either.cond[NonEmpty[String], Unit](
        pointerOffsets.forall(offset =>
          size.raw >= abi.MachineWordSize && offset.raw.toLong <= size.raw - abi.MachineWordSize
        ),
        (),
        "program-global pointer offset lies outside its 64-bit storage slot"
      )
    } yield GlobalSymbol(name, virtualAddress, size, pointerOffsets)
}

/** One addressable program global. Pointer offsets identify relocation-bearing words in its initial image. */
case class GlobalSymbol private (
    name: NonEmpty[String],
    virtualAddress: Option[GuestAddress],
    size: Long >= 0,
    pointerOffsets: Vec[Int >= 0]
)
