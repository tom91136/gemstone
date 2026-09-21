package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.NoNul

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

opaque type ProgramEncoding = String :| (MinLength[1] & MaxLength[64] & NoNul)

object ProgramEncoding {
  def from(value: String): Either[String, ProgramEncoding] =
    value.refineEither[MinLength[1] & MaxLength[64] & NoNul]

  extension (encoding: ProgramEncoding) def value: String = encoding
}
