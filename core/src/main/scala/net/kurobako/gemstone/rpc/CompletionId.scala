package net.kurobako.gemstone.rpc

import scala.compiletime.{constValue, error}

/** A strictly positive completion identifier. */
opaque type CompletionId = Long

object CompletionId {
  inline def either(value: Long): Either[String, CompletionId] =
    if (value > 0L) Right(value) else Left(s"completion id must be strictly positive: $value")

  inline def unsafe(value: Long): CompletionId =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Long & Singleton](inline value: A): CompletionId =
    inline if constValue[A] > 0L then value else error("identifier must be strictly positive")

  given Ordering[CompletionId] = Ordering.Long

  extension (id: CompletionId) {
    inline def raw: Long   = id
    def next: CompletionId = unsafe(Math.addExact(id, 1L))
  }
}
