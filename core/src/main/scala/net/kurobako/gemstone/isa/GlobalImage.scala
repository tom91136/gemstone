package net.kurobako.gemstone.isa

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object GlobalImage {
  def validated(size: Long >= 0, initial: Vec[Byte]): Either[NonEmpty[String], GlobalImage] =
    Either.cond[NonEmpty[String], GlobalImage](
      initial.length.toLong <= size.raw,
      GlobalImage(size, initial),
      "program-global initial contents exceed its size"
    )
}

/** Initial contents and the full storage extent of a program global. */
case class GlobalImage private (size: Long >= 0, initial: Vec[Byte])
