package net.kurobako.gemstone.rpc

import scala.compiletime.{constValue, error}

/** A strictly positive program identifier. */
opaque type ProgramId = Long

object ProgramId {
  inline def either(value: Long): Either[String, ProgramId] =
    if (value > 0L) Right(value) else Left(s"program id must be strictly positive: $value")

  inline def unsafe(value: Long): ProgramId =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Long & Singleton](inline value: A): ProgramId =
    inline if constValue[A] > 0L then value else error("identifier must be strictly positive")

  given Ordering[ProgramId] = Ordering.Long

  extension (id: ProgramId) {
    inline def raw: Long = id
    def next: ProgramId  = unsafe(Math.addExact(id, 1L))
  }
}
