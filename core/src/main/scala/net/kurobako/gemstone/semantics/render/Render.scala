package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** Semantics rendered as [[Term]]s. */
object Render extends Algebra[Term] {
  import Term.*

  private def signedness(signed: Boolean): String = if (signed) "s" else "u"

  inline def literal(value: Long): Term                                             = Literal(value)
  inline def let(inline name: String, value: Term)(inline body: Term => Term): Term = value match {
    case Variable(_) | Literal(_) => body(value)
    case _                        => Term.let(name, value, body)
  }
  inline def primitive[A, R](value: Term)(inline function: A => R): Term =
    PrimitiveLayout.term(PrimitiveLayout.of(function), Vec(value), Vec(Type.of[A]), Type.of[R])
  inline def primitive[A, B, R](first: Term, second: Term)(inline function: (A, B) => R): Term =
    PrimitiveLayout.term(
      PrimitiveLayout.of(function),
      Vec(first, second),
      Vec(Type.of[A], Type.of[B]),
      Type.of[R]
    )
  inline def primitive[A, B, C, R](first: Term, second: Term, third: Term)(inline function: (A, B, C) => R): Term =
    PrimitiveLayout.term(
      PrimitiveLayout.of(function),
      Vec(first, second, third),
      Vec(Type.of[A], Type.of[B], Type.of[C]),
      Type.of[R]
    )
  inline def primitive[A, B, C, D, R](first: Term, second: Term, third: Term, fourth: Term)(
      inline function: (A, B, C, D) => R
  ): Term = PrimitiveLayout.term(
    PrimitiveLayout.of(function),
    Vec(first, second, third, fourth),
    Vec(Type.of[A], Type.of[B], Type.of[C], Type.of[D]),
    Type.of[R]
  )
  inline def primitive[A, B, C, D, E, R](first: Term, second: Term, third: Term, fourth: Term, fifth: Term)(
      inline function: (A, B, C, D, E) => R
  ): Term = PrimitiveLayout.term(
    PrimitiveLayout.of(function),
    Vec(first, second, third, fourth, fifth),
    Vec(
      Type.of[A],
      Type.of[B],
      Type.of[C],
      Type.of[D],
      Type.of[E]
    ),
    Type.of[R]
  )
  inline def primitive[A, B, C, D, E, F, R](
      first: Term,
      second: Term,
      third: Term,
      fourth: Term,
      fifth: Term,
      sixth: Term
  )(inline function: (A, B, C, D, E, F) => R): Term = PrimitiveLayout.term(
    PrimitiveLayout.of(function),
    Vec(first, second, third, fourth, fifth, sixth),
    Vec(
      Type.of[A],
      Type.of[B],
      Type.of[C],
      Type.of[D],
      Type.of[E],
      Type.of[F]
    ),
    Type.of[R]
  )
  inline def add(left: Term, right: Term): Term                      = Binary("+", left, right)
  inline def subtract(left: Term, right: Term): Term                 = Binary("-", left, right)
  inline def multiply(left: Term, right: Term): Term                 = Binary("*", left, right)
  inline def and(left: Term, right: Term): Term                      = Binary("&", left, right)
  inline def or(left: Term, right: Term): Term                       = Binary("|", left, right)
  inline def xor(left: Term, right: Term): Term                      = Binary("^", left, right)
  inline def not(value: Term): Term                                  = Unary("~", value)
  inline def blend(mask: Term, whenSet: Term, whenClear: Term): Term = Term.blend(mask, whenSet, whenClear)
  inline def shiftLeft(value: Term, amount: Term): Term              = Binary("<<", value, amount)
  inline def shiftRightLogical(value: Term, amount: Term): Term      = Binary(">>u", value, amount)
  inline def shiftRightArithmetic(value: Term, amount: Term): Term   = Binary(">>s", value, amount)
  inline def truncate(value: Term, width: Int in 1 ~ 64): Term       = Term.truncate(value, width)
  inline def signExtend(value: Term, width: Int in 1 ~ 64): Term     =
    if (width.raw == 64) value else Call(s"sext$width", Vec(value))
  inline def equal(left: Term, right: Term): Term                            = Binary("==", left, right)
  inline def lessThan(left: Term, right: Term, inline signed: Boolean): Term =
    Binary(s"<${signedness(signed)}", left, right)
  inline def notEqual(left: Term, right: Term): Term                            = Binary("!=", left, right)
  inline def lessOrEqual(left: Term, right: Term, inline signed: Boolean): Term =
    Binary(s"<=${signedness(signed)}", left, right)
  inline def minimum(left: Term, right: Term, inline signed: Boolean): Term =
    Call(s"min.${signedness(signed)}", Vec(left, right))
  inline def maximum(left: Term, right: Term, inline signed: Boolean): Term =
    Call(s"max.${signedness(signed)}", Vec(left, right))
  inline def select(condition: Term, inline whenTrue: Term, inline whenFalse: Term): Term =
    Select(condition, whenTrue, whenFalse)
  inline def multiplyHigh(left: Term, right: Term, width: Int in 1 ~ 64, inline signed: Boolean): Term =
    Call(s"mulhi.${signedness(signed)}$width", Vec(left, right))
  inline def divide(left: Term, right: Term, inline signed: Boolean): Term =
    Binary(s"/${signedness(signed)}", left, right)
  inline def remainder(left: Term, right: Term, inline signed: Boolean): Term =
    Binary(s"%${signedness(signed)}", left, right)
  inline def populationCount(value: Term): Term                     = Call("popc", Vec(value))
  inline def leadingZeros(value: Term, width: Int in 1 ~ 64): Term  = Call(s"clz$width", Vec(value))
  inline def trailingZeros(value: Term, width: Int in 1 ~ 64): Term = Call(s"ctz$width", Vec(value))
  inline def reverseBits(value: Term, width: Int in 1 ~ 64): Term   = Call(s"brev$width", Vec(value))
  inline def bitFieldExtract(value: Term, offset: Term, count: Term, inline signed: Boolean): Term =
    Call(s"bfe.${signedness(signed)}", Vec(value, offset, count))
}
