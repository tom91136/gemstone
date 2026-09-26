package net.kurobako.gemstone.memory

import scala.compiletime.{constValue, error}

/** A strictly positive device allocation size in bytes. */
opaque type DeviceAllocationByteSize = Int

object DeviceAllocationByteSize {
  inline def either(value: Int): Either[String, DeviceAllocationByteSize] =
    if (value > 0) Right(value) else Left(s"device allocation size must be strictly positive: $value")

  inline def unsafe(value: Int): DeviceAllocationByteSize =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): DeviceAllocationByteSize =
    inline if constValue[A] > 0 then value else error("device allocation size must be strictly positive")

  def fromLong(value: Long): Either[String, DeviceAllocationByteSize] =
    if (value < 1L || value > Int.MaxValue.toLong)
      Left(s"device allocation size is outside 1 to ${Int.MaxValue}: $value")
    else Right(value.toInt)

  given Ordering[DeviceAllocationByteSize] = Ordering.Int

  extension (value: DeviceAllocationByteSize) {
    inline def raw: Int = value
  }
}
