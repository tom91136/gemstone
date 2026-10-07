package net.kurobako.gemstone.isa

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object ProgramGlobal {
  def validated(
      size: Long >= 0,
      initial: Vec[Byte],
      alias: Option[ProgramGlobalAlias] = None
  ): Either[NonEmpty[String], ProgramGlobal] =
    Either.cond[NonEmpty[String], ProgramGlobal](
      initial.length.toLong <= size.raw && (alias.isEmpty || initial.isEmpty),
      ProgramGlobal(size, initial, alias),
      "program-global initial contents exceed its size or alias has independent contents"
    )
}

/** Initial contents and the full storage extent of a program global. */
case class ProgramGlobal private (size: Long >= 0, initial: Vec[Byte], alias: Option[ProgramGlobalAlias])
