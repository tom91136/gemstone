package net.kurobako.gemstone

import scala.compiletime.{constValue, error}

import scala.annotation.targetName

/** A strictly positive `Int`. */
opaque type PositiveInt = Int

object PositiveInt {
  inline def either(value: Int): Either[String, PositiveInt] =
    if (value > 0) Right(value) else Left(s"value must be strictly positive: $value")

  inline def unsafe(value: Int): PositiveInt =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): PositiveInt =
    inline if constValue[A] > 0 then value else error("value must be strictly positive")

  val One: PositiveInt = 1

  given Ordering[PositiveInt] = Ordering.Int

  extension (value: PositiveInt) {
    inline def raw: Int               = value
    def toLong: PositiveLong          = PositiveLong.unsafe(value.toLong)
    def toNonNegative: NonNegativeInt = NonNegativeInt.unsafe(value)

    @targetName("addPositive")
    def +(other: PositiveInt): PositiveInt = PositiveInt.unsafe(Math.addExact(value, other))

    @targetName("addNonNegative")
    def +(other: NonNegativeInt): PositiveInt = PositiveInt.unsafe(Math.addExact(value, other.raw))

    def *(other: PositiveInt): PositiveInt = PositiveInt.unsafe(Math.multiplyExact(value, other))

    def <(other: PositiveInt): Boolean  = value < other
    def <=(other: PositiveInt): Boolean = value <= other
    def >(other: PositiveInt): Boolean  = value > other
    def >=(other: PositiveInt): Boolean = value >= other

    def min(other: PositiveInt): PositiveInt = if (value <= other) value else other
    def max(other: PositiveInt): PositiveInt = if (value >= other) value else other
  }
}

/** A positive or zero `Int`. */
opaque type NonNegativeInt = Int

object NonNegativeInt {
  inline def either(value: Int): Either[String, NonNegativeInt] =
    if (value >= 0) Right(value) else Left(s"value must be positive or zero: $value")

  inline def unsafe(value: Int): NonNegativeInt =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Int & Singleton](inline value: A): NonNegativeInt =
    inline if constValue[A] >= 0 then value else error("value must be positive or zero")

  val Zero: NonNegativeInt = 0

  given Ordering[NonNegativeInt] = Ordering.Int

  extension (value: NonNegativeInt) {
    inline def raw: Int         = value
    def toLong: NonNegativeLong = NonNegativeLong.unsafe(value.toLong)

    @targetName("addNonNegative")
    def +(other: NonNegativeInt): NonNegativeInt = NonNegativeInt.unsafe(Math.addExact(value, other))

    @targetName("addPositive")
    def +(other: PositiveInt): PositiveInt = PositiveInt.unsafe(Math.addExact(value, other.raw))

    def *(other: NonNegativeInt): NonNegativeInt = NonNegativeInt.unsafe(Math.multiplyExact(value, other))
    def -(other: NonNegativeInt): NonNegativeInt = NonNegativeInt.unsafe(Math.subtractExact(value, other))

    def <(other: NonNegativeInt): Boolean  = value < other
    def <=(other: NonNegativeInt): Boolean = value <= other
    def >(other: NonNegativeInt): Boolean  = value > other
    def >=(other: NonNegativeInt): Boolean = value >= other

    def min(other: NonNegativeInt): NonNegativeInt = if (value <= other) value else other
    def max(other: NonNegativeInt): NonNegativeInt = if (value >= other) value else other
  }
}

/** A strictly positive `Long`. */
opaque type PositiveLong = Long

object PositiveLong {
  inline def either(value: Long): Either[String, PositiveLong] =
    if (value > 0L) Right(value) else Left(s"value must be strictly positive: $value")

  inline def unsafe(value: Long): PositiveLong =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Long & Singleton](inline value: A): PositiveLong =
    inline if constValue[A] > 0L then value else error("value must be strictly positive")

  val One: PositiveLong = 1L

  given Ordering[PositiveLong] = Ordering.Long

  extension (value: PositiveLong) {
    inline def raw: Long                           = value
    def toNonNegative: NonNegativeLong             = NonNegativeLong.unsafe(value)
    def toPositiveInt: Either[String, PositiveInt] =
      if (value <= Int.MaxValue.toLong) PositiveInt.either(value.toInt)
      else Left(s"value exceeds Int range: $value")

    def +(other: PositiveLong): PositiveLong = PositiveLong.unsafe(Math.addExact(value, other))
    def *(other: PositiveLong): PositiveLong = PositiveLong.unsafe(Math.multiplyExact(value, other))

    def <(other: PositiveLong): Boolean  = value < other
    def <=(other: PositiveLong): Boolean = value <= other
    def >(other: PositiveLong): Boolean  = value > other
    def >=(other: PositiveLong): Boolean = value >= other

    def min(other: PositiveLong): PositiveLong = if (value <= other) value else other
    def max(other: PositiveLong): PositiveLong = if (value >= other) value else other
  }
}

/** A positive or zero `Long`. */
opaque type NonNegativeLong = Long

object NonNegativeLong {
  inline def either(value: Long): Either[String, NonNegativeLong] =
    if (value >= 0L) Right(value) else Left(s"value must be positive or zero: $value")

  inline def unsafe(value: Long): NonNegativeLong =
    either(value).fold(message => throw IllegalArgumentException(message), identity)

  inline def apply[A <: Long & Singleton](inline value: A): NonNegativeLong =
    inline if constValue[A] >= 0L then value else error("value must be positive or zero")

  val Zero: NonNegativeLong = 0L

  given Ordering[NonNegativeLong] = Ordering.Long

  extension (value: NonNegativeLong) {
    inline def raw: Long                                 = value
    def toNonNegativeInt: Either[String, NonNegativeInt] =
      if (value <= Int.MaxValue.toLong) NonNegativeInt.either(value.toInt)
      else Left(s"value exceeds Int range: $value")

    @targetName("addNonNegative")
    def +(other: NonNegativeLong): NonNegativeLong = NonNegativeLong.unsafe(Math.addExact(value, other))

    @targetName("addPositive")
    def +(other: PositiveLong): PositiveLong = PositiveLong.unsafe(Math.addExact(value, other.raw))

    def *(other: NonNegativeLong): NonNegativeLong = NonNegativeLong.unsafe(Math.multiplyExact(value, other))
    def -(other: NonNegativeLong): NonNegativeLong = NonNegativeLong.unsafe(Math.subtractExact(value, other))

    def <(other: NonNegativeLong): Boolean  = value < other
    def <=(other: NonNegativeLong): Boolean = value <= other
    def >(other: NonNegativeLong): Boolean  = value > other
    def >=(other: NonNegativeLong): Boolean = value >= other

    def min(other: NonNegativeLong): NonNegativeLong = if (value <= other) value else other
    def max(other: NonNegativeLong): NonNegativeLong = if (value >= other) value else other
  }
}
