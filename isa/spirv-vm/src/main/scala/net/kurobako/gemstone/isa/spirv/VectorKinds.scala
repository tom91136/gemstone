package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.vm.*

/** The component types of SPV_NV_cooperative_vector matrices and vectors, which a product names by value, and the
  * conversions between them. Products accumulate in binary64 and round once to the result's type.
  */
private[isa] object VectorKinds {
  val MatrixBSigned: Long = 0x2L

  enum Interpretation(
      val bits: Int in (8, 16, 32, 64),
      val floating: Boolean,
      val signed: Boolean,
      val packed: Boolean
  ) derives CanEqual {
    case Float16     extends Interpretation(bits = 16, floating = true, signed = true, packed = false)
    case Float32     extends Interpretation(bits = 32, floating = true, signed = true, packed = false)
    case Float64     extends Interpretation(bits = 64, floating = true, signed = true, packed = false)
    case E4M3        extends Interpretation(bits = 8, floating = true, signed = true, packed = false)
    case E5M2        extends Interpretation(bits = 8, floating = true, signed = true, packed = false)
    case Int8        extends Interpretation(bits = 8, floating = false, signed = true, packed = false)
    case Int16       extends Interpretation(bits = 16, floating = false, signed = true, packed = false)
    case Int32       extends Interpretation(bits = 32, floating = false, signed = true, packed = false)
    case Int64       extends Interpretation(bits = 64, floating = false, signed = true, packed = false)
    case UInt8       extends Interpretation(bits = 8, floating = false, signed = false, packed = false)
    case UInt16      extends Interpretation(bits = 16, floating = false, signed = false, packed = false)
    case UInt32      extends Interpretation(bits = 32, floating = false, signed = false, packed = false)
    case UInt64      extends Interpretation(bits = 64, floating = false, signed = false, packed = false)
    case Int8Packed  extends Interpretation(bits = 8, floating = false, signed = true, packed = true)
    case UInt8Packed extends Interpretation(bits = 8, floating = false, signed = false, packed = true)

    def bytes: Int in 1 ~ 8  = (bits / 8).unsafe[Int in 1 ~ 8]
    def width: Int in 1 ~ 64 = bits.widen[Int in 1 ~ 64]
  }
  import Interpretation.*

  def interpretation(value: Long): Either[Fault, Interpretation] = value match {
    case 0L          => Right(Float16)
    case 1L          => Right(Float32)
    case 2L          => Right(Float64)
    case 3L          => Right(Int8)
    case 4L          => Right(Int16)
    case 5L          => Right(Int32)
    case 6L          => Right(Int64)
    case 7L          => Right(UInt8)
    case 8L          => Right(UInt16)
    case 9L          => Right(UInt32)
    case 10L         => Right(UInt64)
    case 1000491000L => Right(Int8Packed)
    case 1000491001L => Right(UInt8Packed)
    case 1000491002L => Right(E4M3)
    case 1000491003L => Right(E5M2)
    case _           => Left(Fault.InvalidRequest(s"cooperative vector component type $value is not defined"))
  }

  def unsigned(value: Long): Double =
    if (value >= 0L) value.toDouble else ((value >>> 1) | (value & 1L)).toDouble * 2.0

  def real(raw: Long, kind: Interpretation): Double = kind match {
    case Float16          => IEEE754.toDouble(raw & 0xffffL, 16)
    case Float32          => IEEE754.toDouble(raw & 0xffffffffL, 32)
    case Float64          => IEEE754.toDouble(raw, 64)
    case E4M3             => Minifloat.E4M3.toDouble(raw)
    case E5M2             => Minifloat.E5M2.toDouble(raw)
    case _ if kind.signed => Bits.signExtend(raw, kind.width).toDouble
    case _                => unsigned(Bits.truncate(raw, kind.width))
  }

  def integer(raw: Long, kind: Interpretation): Long =
    if (kind.signed) Bits.signExtend(raw, kind.width) else Bits.truncate(raw, kind.width)

  /** Rounds to nearest, ties to even, and saturates conversions to integers. */
  def round(value: Double, kind: Interpretation): Long = kind match {
    case Float16 => IEEE754.fromDouble(value, 16)
    case Float32 => IEEE754.fromDouble(value, 32)
    case Float64 => IEEE754.fromDouble(value, 64)
    case E4M3    => Minifloat.E4M3.fromDouble(value, saturate = true)
    case E5M2    => Minifloat.E5M2.fromDouble(value)
    case _       => IEEE754.toIntegerSaturated(value, kind.width, kind.signed, IEEE754.Rounding.NearestEven)
  }

  private def flag(value: Boolean): Long = if (value) 1L else 0L

  private def kindOf(code: Long): Interpretation = interpretation(code).getOrElse(Float32)

  private def bits(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  /** 1 where `kind` names a component type. */
  def defined(kind: UInt[64]): UInt[64] = fromRaw[UInt[64]](flag(interpretation(toRaw(kind)).isRight))

  /** 1 where `kind` packs four bytes into each 32-bit lane. */
  def packed(kind: UInt[64]): UInt[64] = fromRaw[UInt[64]](flag(kindOf(toRaw(kind)).packed))

  /** 1 where `kind` is a floating-point type. */
  def floating(kind: UInt[64]): UInt[64] = fromRaw[UInt[64]](flag(kindOf(toRaw(kind)).floating))

  /** The bytes of an element of `kind`. */
  def bytes(kind: UInt[64]): UInt[64] = fromRaw[UInt[64]](kindOf(toRaw(kind)).bytes.toLong)

  /** The element `raw` of `kind` in double precision. */
  def realOf(raw: UInt[64], kind: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(real(toRaw(raw), kindOf(toRaw(kind)))))

  /** The element `raw` of `kind`, extended to 64 bits. */
  def integerOf(raw: UInt[64], kind: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](integer(toRaw(raw), kindOf(toRaw(kind))))

  /** The binary `width` float `lane` as an element of `kind`. */
  def fromFloat(lane: UInt[64], width: UInt[64], kind: UInt[64]): UInt[64] = fromRaw[UInt[64]](
    round(IEEE754.toDouble(toRaw(lane), toRaw(width).toInt.unsafe[Int in (16, 32, 64)]), kindOf(toRaw(kind)))
  )

  /** The integer `value`, signed if `signed` is set, as an element of `kind`. */
  def fromInteger(value: UInt[64], signed: UInt[64], kind: UInt[64]): UInt[64] = {
    val (extended, negative, target) = (toRaw(value), toRaw(signed) != 0L, kindOf(toRaw(kind)))
    fromRaw[UInt[64]](
      if (target.floating) round(if (negative) extended.toDouble else unsigned(extended), target)
      else IntegerArithmetic.clampToWidth(extended, negative, target.width, target.signed)
    )
  }
}
