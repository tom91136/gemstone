package net.kurobako.gemstone.isa

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object GlobalImage {
  case class Alias(name: NonEmpty[String], offset: Long >= 0)

  def validated(
      size: Long >= 0,
      initial: Vec[Byte],
      alias: Option[Alias] = None
  ): Either[NonEmpty[String], GlobalImage] =
    Either.cond[NonEmpty[String], GlobalImage](
      initial.length.toLong <= size.raw && (alias.isEmpty || initial.isEmpty),
      GlobalImage(size, initial, alias),
      "program-global initial contents exceed its size or alias has independent contents"
    )
}

/** Initial contents and the full storage extent of a program global. */
case class GlobalImage private (size: Long >= 0, initial: Vec[Byte], alias: Option[GlobalImage.Alias])
