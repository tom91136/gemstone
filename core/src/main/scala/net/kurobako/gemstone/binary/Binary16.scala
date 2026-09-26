package net.kurobako.gemstone.binary

import scala.compiletime.{constValue, error}

/** An unsigned 16-bit value, from zero to 65535. */
private[gemstone] opaque type Binary16 = Long

private[gemstone] object Binary16 {
  inline def either(value: Long): Either[String, Binary16] =
    if (value >= 0L && value <= 65535L) Right(value) else Left(s"binary16 value out of range: $value")

  inline def unsafe(value: Long): Binary16 =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Long & Singleton](inline value: A): Binary16 =
    inline if constValue[A] >= 0L && constValue[A] <= 65535L then value else error("binary value out of range")

  def fromLow(value: Binary16): Binary16 = value

  def low(value: Long): Binary16 = unsafe(value & Bits.Mask16)

  extension (value: Binary16) {
    inline def raw: Long = value
  }
}
