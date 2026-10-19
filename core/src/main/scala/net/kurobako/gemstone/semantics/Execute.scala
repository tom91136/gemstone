package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.prelude.*

/** Semantics executed on `Long` lane values. */
@SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
object Execute extends Algebra[Long] {
  inline def literal(value: Long): Long                                             = value
  inline def let(inline name: String, value: Long)(inline body: Long => Long): Long = body(value)
  inline def primitive[A, R](value: Long)(inline function: A => R): Long            =
    toRaw[R](function(fromRaw[A](value)))
  inline def primitive[A, B, R](first: Long, second: Long)(inline function: (A, B) => R): Long =
    toRaw[R](function(fromRaw[A](first), fromRaw[B](second)))
  inline def primitive[A, B, C, R](first: Long, second: Long, third: Long)(inline function: (A, B, C) => R): Long =
    toRaw[R](function(
      fromRaw[A](first),
      fromRaw[B](second),
      fromRaw[C](third)
    ))
  inline def primitive[A, B, C, D, R](first: Long, second: Long, third: Long, fourth: Long)(
      inline function: (A, B, C, D) => R
  ): Long = toRaw[R](function(
    fromRaw[A](first),
    fromRaw[B](second),
    fromRaw[C](third),
    fromRaw[D](fourth)
  ))
  inline def primitive[A, B, C, D, E, R](first: Long, second: Long, third: Long, fourth: Long, fifth: Long)(
      inline function: (A, B, C, D, E) => R
  ): Long = toRaw[R](function(
    fromRaw[A](first),
    fromRaw[B](second),
    fromRaw[C](third),
    fromRaw[D](fourth),
    fromRaw[E](fifth)
  ))
  inline def primitive[A, B, C, D, E, F, R](
      first: Long,
      second: Long,
      third: Long,
      fourth: Long,
      fifth: Long,
      sixth: Long
  )(inline function: (A, B, C, D, E, F) => R): Long = toRaw[R](function(
    fromRaw[A](first),
    fromRaw[B](second),
    fromRaw[C](third),
    fromRaw[D](fourth),
    fromRaw[E](fifth),
    fromRaw[F](sixth)
  ))
  inline def add(left: Long, right: Long): Long                      = left + right
  inline def subtract(left: Long, right: Long): Long                 = left - right
  inline def multiply(left: Long, right: Long): Long                 = left * right
  inline def and(left: Long, right: Long): Long                      = left & right
  inline def or(left: Long, right: Long): Long                       = left | right
  inline def xor(left: Long, right: Long): Long                      = left ^ right
  inline def not(value: Long): Long                                  = ~value
  inline def blend(mask: Long, whenSet: Long, whenClear: Long): Long = whenClear ^ ((whenSet ^ whenClear) & mask)
  inline def shiftLeft(value: Long, amount: Long): Long              = value << amount.toInt
  inline def shiftRightLogical(value: Long, amount: Long): Long      = value >>> amount.toInt
  inline def shiftRightArithmetic(value: Long, amount: Long): Long   = value >> amount.toInt
  inline def truncate(value: Long, width: Int in 1 ~ 64): Long       = Bits.truncate(value, width)
  inline def signExtend(value: Long, width: Int in 1 ~ 64): Long     = Bits.signExtend(value, width)
  inline def equal(left: Long, right: Long): Long                    = if (left == right) 1L else 0L
  inline def notEqual(left: Long, right: Long): Long                 = if (left != right) 1L else 0L
  inline def lessThan(left: Long, right: Long, inline signed: Boolean): Long =
    if (if (signed) left < right else java.lang.Long.compareUnsigned(left, right) < 0) 1L else 0L
  inline def lessOrEqual(left: Long, right: Long, inline signed: Boolean): Long =
    if (if (signed) left <= right else java.lang.Long.compareUnsigned(left, right) <= 0) 1L else 0L
  inline def minimum(left: Long, right: Long, inline signed: Boolean): Long =
    if (signed) Math.min(left, right) else if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
  inline def maximum(left: Long, right: Long, inline signed: Boolean): Long =
    if (signed) Math.max(left, right) else if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
  inline def select(condition: Long, inline whenTrue: Long, inline whenFalse: Long): Long =
    if (condition != 0L) whenTrue else whenFalse
  inline def multiplyHigh(left: Long, right: Long, width: Int in 1 ~ 64, inline signed: Boolean): Long =
    IntegerArithmetic.multiplyHigh(left, right, width, signed)
  inline def divide(left: Long, right: Long, inline signed: Boolean): Long =
    if (signed) left / right else java.lang.Long.divideUnsigned(left, right)
  inline def remainder(left: Long, right: Long, inline signed: Boolean): Long =
    if (signed) left % right else java.lang.Long.remainderUnsigned(left, right)
  inline def populationCount(value: Long): Long                     = Bits.bitCount(value, 64).toLong
  inline def leadingZeros(value: Long, width: Int in 1 ~ 64): Long  = Bits.leadingZeros(value, width).toLong
  inline def trailingZeros(value: Long, width: Int in 1 ~ 64): Long =
    Math.min(java.lang.Long.numberOfTrailingZeros(value), width).toLong
  inline def reverseBits(value: Long, width: Int in 1 ~ 64): Long = Bits.reverseBits(value, width)
  inline def bitFieldExtract(value: Long, offset: Long, count: Long, inline signed: Boolean): Long =
    Bits.bitFieldExtract(value, offset, count, signed)
}
