package net.kurobako.gemstone.binary

import scala.compiletime.{constValue, error}

/** An unsigned 32-bit value, from zero to 4294967295. */
private[gemstone] opaque type Binary32 = Long

private[gemstone] object Binary32 {
  inline def either(value: Long): Either[String, Binary32] =
    if (value >= 0L && value <= 4294967295L) Right(value) else Left(s"binary32 value out of range: $value")

  inline def unsafe(value: Long): Binary32 =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Long & Singleton](inline value: A): Binary32 =
    inline if constValue[A] >= 0L && constValue[A] <= 4294967295L then value else error("binary value out of range")

  def fromLow(value: Binary32): Binary32 = value

  def low(value: Long): Binary32 = unsafe(value & Bits.Mask32)

  extension (value: Binary32) {
    inline def raw: Long = value
  }
}
