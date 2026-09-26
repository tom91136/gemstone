package net.kurobako.gemstone.rpc

import scala.compiletime.{constValue, error}

/** A strictly positive allocation identifier. */
opaque type AllocationId = Long

object AllocationId {
  inline def either(value: Long): Either[String, AllocationId] =
    if (value > 0L) Right(value) else Left(s"allocation id must be strictly positive: $value")

  inline def unsafe(value: Long): AllocationId =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Long & Singleton](inline value: A): AllocationId =
    inline if constValue[A] > 0L then value else error("identifier must be strictly positive")

  given Ordering[AllocationId] = Ordering.Long

  extension (id: AllocationId) {
    inline def raw: Long   = id
    def next: AllocationId = unsafe(Math.addExact(id, 1L))
  }
}
