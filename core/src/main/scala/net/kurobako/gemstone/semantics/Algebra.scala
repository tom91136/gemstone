package net.kurobako.gemstone.semantics

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.prelude.*

/** The operations instruction semantics are written in, over lane values of representation `V`: a `Long` to execute, a
  * [[render.Term]] to render. Every method is inline, so semantics written against a statically known algebra expand at
  * compile time into that algebra's code with no dispatch or boxing. Widths and flags are structural: they are fixed
  * per instruction and may steer the semantics directly. Values are two's complement in 64 bits; truncation leaves the
  * low bits zero-extended. The operations from [[extend]] on are built from the others, so each executes and renders as
  * written.
  */
trait Algebra[V] {
  inline def literal(value: Long): V

  /** `body` applied to `value`, which renders under `name`. */
  inline def let(inline name: String, value: V)(inline body: V => V): V

  /** `function` over the operands as the prelude types it takes. It must apply one method of a static object to them,
    * and to values fixed per instruction, so the rendered semantics calls the same code execution runs.
    */
  inline def primitive[A, R](value: V)(inline function: A => R): V
  inline def primitive[A, B, R](first: V, second: V)(inline function: (A, B) => R): V
  inline def primitive[A, B, C, R](first: V, second: V, third: V)(inline function: (A, B, C) => R): V
  inline def primitive[A, B, C, D, R](first: V, second: V, third: V, fourth: V)(
      inline function: (A, B, C, D) => R
  ): V
  inline def primitive[A, B, C, D, E, R](first: V, second: V, third: V, fourth: V, fifth: V)(
      inline function: (A, B, C, D, E) => R
  ): V
  inline def primitive[A, B, C, D, E, F, R](first: V, second: V, third: V, fourth: V, fifth: V, sixth: V)(
      inline function: (A, B, C, D, E, F) => R
  ): V
  inline def add(left: V, right: V): V
  inline def subtract(left: V, right: V): V
  inline def multiply(left: V, right: V): V
  inline def and(left: V, right: V): V
  inline def or(left: V, right: V): V
  inline def xor(left: V, right: V): V
  inline def not(value: V): V

  /** The bits of `whenSet` where `mask` is set, and of `whenClear` elsewhere. */
  inline def blend(mask: V, whenSet: V, whenClear: V): V

  /** Shifts by `amount` modulo 64. */
  inline def shiftLeft(value: V, amount: V): V
  inline def shiftRightLogical(value: V, amount: V): V
  inline def shiftRightArithmetic(value: V, amount: V): V

  inline def truncate(value: V, width: Int in 1 ~ 64): V
  inline def signExtend(value: V, width: Int in 1 ~ 64): V

  /** 1 when the condition holds, otherwise 0. */
  inline def equal(left: V, right: V): V
  inline def notEqual(left: V, right: V): V
  inline def lessThan(left: V, right: V, inline signed: Boolean): V
  inline def lessOrEqual(left: V, right: V, inline signed: Boolean): V

  /** The lesser or greater operand, ordered as [[lessThan]] orders them. */
  inline def minimum(left: V, right: V, inline signed: Boolean): V
  inline def maximum(left: V, right: V, inline signed: Boolean): V

  /** `whenTrue` when `condition` is non-zero, otherwise `whenFalse`; only the chosen value is evaluated. */
  inline def select(condition: V, inline whenTrue: V, inline whenFalse: V): V

  /** The high `width` bits of the `2 * width`-bit product of the operands, each first truncated or sign-extended. */
  inline def multiplyHigh(left: V, right: V, width: Int in 1 ~ 64, inline signed: Boolean): V

  /** 64-bit quotient and remainder by a non-zero divisor; signed `Long.MinValue / -1` wraps to `Long.MinValue`,
    * remainder 0.
    */
  inline def divide(left: V, right: V, inline signed: Boolean): V
  inline def remainder(left: V, right: V, inline signed: Boolean): V

  inline def populationCount(value: V): V
  inline def leadingZeros(value: V, width: Int in 1 ~ 64): V

  /** The trailing zeros of the low `width` bits, or `width` when they are all clear. */
  inline def trailingZeros(value: V, width: Int in 1 ~ 64): V
  inline def reverseBits(value: V, width: Int in 1 ~ 64): V

  /** The low `count` bits of `value` shifted right by `offset` modulo 64, zero- or sign-extended; shifted in bits
    * repeat the sign when `signed`. An empty field is 0, and `count` is at most 64.
    */
  inline def bitFieldExtract(value: V, offset: V, count: V, inline signed: Boolean): V

  /** The low `width` bits of `value`, sign- or zero-extended. */
  inline def extend(value: V, width: Int in 1 ~ 64, inline signed: Boolean): V =
    if (signed) signExtend(value, width) else truncate(value, width)

  /** The `width`-bit field of `value` from bit `offset`, extended; the static counterpart of
    * [[Algebra.bitFieldExtract]].
    */
  inline def field(
      value: V,
      offset: Int,
      width: Int in 1 ~ 64,
      inline signed: Boolean
  ): V =
    extend(
      if (offset == 0) value else shiftRightLogical(value, literal(offset.toLong)),
      width,
      signed
    )

  /** The low `count` bits set, for a count of at most 64. */
  inline def lowMask(count: V): V = select(
    lessThan(count, literal(64L), signed = false),
    subtract(shiftLeft(literal(1L), count), literal(1L)),
    literal(-1L)
  )

  /** `base` with the `count` bits from `offset` replaced by the low bits of `value`; the field must lie within 64 bits.
    */
  inline def insertField(base: V, value: V, offset: V, count: V): V =
    blend(shiftLeft(lowMask(count), offset), shiftLeft(value, offset), base)

  /** The low `width` bits of `base` with the `count` bits from `offset` replaced by the low bits of `value`. */
  inline def placeField(
      base: V,
      value: V,
      offset: Int,
      count: Int in 1 ~ 64,
      width: Int in 1 ~ 64
  ): V = {
    val field = truncate(value, count)
    or(
      and(base, literal(Bits.mask(width) & ~(Bits.mask(count) << offset))),
      if (offset == 0) field else shiftLeft(field, literal(offset.toLong))
    )
  }

  /** The quotient, or the remainder when `remainder`, of the operands at `width` bits; division by zero yields 0. */
  inline def quotient(
      left: V,
      right: V,
      width: Int in 1 ~ 64,
      inline signed: Boolean,
      inline remainder: Boolean
  ): V = let("d", extend(right, width, signed))(d =>
    select(
      equal(d, literal(0L)),
      literal(0L),
      truncate(
        if (remainder) this.remainder(extend(left, width, signed), d, signed)
        else this.divide(extend(left, width, signed), d, signed),
        width
      )
    )
  )

  /** The lesser or, when `maximum`, the greater of the operands at `width` bits, as `width` bits. */
  inline def extremum(
      left: V,
      right: V,
      width: Int in 1 ~ 64,
      inline signed: Boolean,
      inline maximum: Boolean
  ): V = {
    val x      = extend(left, width, signed)
    val y      = extend(right, width, signed)
    val chosen = if (maximum) this.maximum(x, y, signed) else this.minimum(x, y, signed)
    if (signed) truncate(chosen, width) else chosen
  }

  /** The magnitude of the `width`-bit signed `value`, as `width` bits. */
  inline def absolute(value: V, width: Int in 1 ~ 64): V =
    let("x", signExtend(value, width))(x =>
      truncate(select(lessThan(x, literal(0L), signed = true), subtract(literal(0L), x), x), width)
    )

  /** `value` shifted left by `amount`, or zero where `amount` reaches `width`. */
  inline def shiftedLeft(value: V, amount: V, width: Int in 1 ~ 64, inline signed: Boolean): V =
    let("n", amount)(n =>
      select(lessThan(n, literal(width.toLong), signed), truncate(shiftLeft(value, n), width), literal(0L))
    )

  /** The low `width` bits of `value` shifted right by `amount`, or zero where `amount` reaches `width`. */
  inline def shiftedRightLogical(value: V, amount: V, width: Int in 1 ~ 64, inline signed: Boolean): V =
    let("n", amount)(n =>
      select(lessThan(n, literal(width.toLong), signed), shiftRightLogical(truncate(value, width), n), literal(0L))
    )

  /** `value` clamped to `lowest` and `highest`, compared signed. */
  inline def clamp(value: V, lowest: Long, highest: Long): V =
    this.maximum(
      this.minimum(value, literal(highest), signed = true),
      literal(lowest),
      signed = true
    )

  /** `value` clamped to the range of a signed `width`-bit integer. */
  inline def clampSigned(value: V, width: Int in 1 ~ 64): V =
    clamp(value, -1L << (width - 1), ~(-1L << (width - 1)))

  /** `value` clamped to the range of an unsigned `width`-bit integer. */
  inline def clampUnsigned(value: V, width: Int in 1 ~ 63): V =
    clamp(value, 0L, Bits.mask(width.widen[Int in 1 ~ 64]))

  /** A signed 64-bit `result` clamped to `width` bits, or the bound on the side of `left` when `overflow` is negative,
    * as the 64-bit operation overflows exactly then.
    */
  inline def saturated(overflow: V, left: V, result: V, width: Int in 1 ~ 64): V =
    select(
      lessThan(overflow, literal(0L), signed = true),
      select(
        lessThan(left, literal(0L), signed = true),
        literal(-1L << (width - 1)),
        literal(~(-1L << (width - 1)))
      ),
      if (width < 64) clampSigned(result, width) else result
    )

  /** The sum of the operands at `width` bits, clamped to the range of `width` bits. */
  inline def addSaturated(
      left: V,
      right: V,
      width: Int in 1 ~ 64,
      inline signed: Boolean
  ): V =
    if (signed)
      let("l", signExtend(left, width))(l =>
        let("r", signExtend(right, width))(r =>
          let("s", add(l, r))(s =>
            if (width < 64) clampSigned(s, width)
            else saturated(and(xor(l, s), xor(r, s)), l, s, width)
          )
        )
      )
    else
      let("l", truncate(left, width))(l =>
        let("s", truncate(add(l, truncate(right, width)), width))(s =>
          select(lessThan(s, l, signed = false), literal(Bits.mask(width)), s)
        )
      )

  /** The difference of the operands at `width` bits, clamped to the range of `width` bits. */
  inline def subtractSaturated(
      left: V,
      right: V,
      width: Int in 1 ~ 64,
      inline signed: Boolean
  ): V =
    if (signed)
      let("l", signExtend(left, width))(l =>
        let("r", signExtend(right, width))(r =>
          let("s", subtract(l, r))(s =>
            if (width < 64) clampSigned(s, width)
            else saturated(and(xor(l, s), xor(not(r), s)), l, s, width)
          )
        )
      )
    else
      let("l", truncate(left, width))(l =>
        let("r", truncate(right, width))(r =>
          select(
            lessThan(l, r, signed = false),
            literal(0L),
            truncate(subtract(l, r), width)
          )
        )
      )

  /** The magnitude of the difference of the operands at `width` bits. */
  inline def absoluteDifference(
      left: V,
      right: V,
      width: Int in 1 ~ 64,
      inline signed: Boolean
  ): V = let("x", extend(left, width, signed))(x =>
    let("y", extend(right, width, signed))(y =>
      select(lessThan(x, y, signed), subtract(y, x), subtract(x, y))
    )
  )

  /** `sum` plus the products of the low `lanes` corresponding `width`-bit lanes of the operands, each extended. */
  inline def dot(
      sum: V,
      left: V,
      right: V,
      inline width: Int,
      inline lanes: Int,
      inline leftSigned: Boolean,
      inline rightSigned: Boolean
  ): V =
    inline if (lanes == 0) sum
    else
      add(
        dot(sum, left, right, width, lanes - 1, leftSigned, rightSigned),
        product(left, right, width, lanes - 1, leftSigned, rightSigned)
      )

  /** The sum of the products of the low `lanes` corresponding `width`-bit lanes of the operands, each extended. */
  inline def products(
      left: V,
      right: V,
      inline width: Int,
      inline lanes: Int,
      inline leftSigned: Boolean,
      inline rightSigned: Boolean
  ): V =
    inline if (lanes == 1) product(left, right, width, 0, leftSigned, rightSigned)
    else
      add(
        products(left, right, width, lanes - 1, leftSigned, rightSigned),
        product(left, right, width, lanes - 1, leftSigned, rightSigned)
      )

  protected inline def product(
      left: V,
      right: V,
      inline width: Int,
      inline lane: Int,
      inline leftSigned: Boolean,
      inline rightSigned: Boolean
  ): V = multiply(
    field(left, lane * width, width.unsafe[Int in 1 ~ 64], leftSigned),
    field(right, lane * width, width.unsafe[Int in 1 ~ 64], rightSigned)
  )

  /** The position of the highest set bit of the low `width` bits, or when `signed` of the highest bit unlike the sign;
    * counted from bit 0 up, or from bit `width - 1` down when `fromTop`. `none` when there is no such bit.
    */
  inline def findMostSignificant(
      value: V,
      width: Int in 1 ~ 64,
      inline signed: Boolean,
      inline fromTop: Boolean,
      none: Long
  ): V = let(
    "s",
    if (signed)
      let("x", signExtend(value, width))(x =>
        truncate(xor(x, shiftRightArithmetic(x, literal(63L))), width)
      )
    else truncate(value, width)
  )(bits =>
    select(
      equal(bits, literal(0L)),
      literal(none),
      if (fromTop) leadingZeros(bits, width)
      else subtract(literal(width - 1L), leadingZeros(bits, width))
    )
  )

  /** The position of the lowest set bit of the low `width` bits, or `none` when there is none. */
  inline def findLeastSignificant(value: V, width: Int in 1 ~ 64, none: Long): V =
    let("x", truncate(value, width))(x =>
      select(equal(x, literal(0L)), literal(none), trailingZeros(x, width))
    )

  /** 1 when the `width`-bit float `value` is a NaN. */
  inline def isNaN(value: V, width: Int in (16, 32, 64)): V =
    isNaN(value, IEEE754.signBit(width), IEEE754.infinity(width))

  /** 1 when `value` is a NaN of the float whose sign bit and exponent field are `sign` and `infinity`. */
  inline def isNaN(value: V, inline sign: Long, inline infinity: Long): V =
    lessThan(literal(infinity), and(value, literal(sign - 1L)), signed = true)

  /** 1 when either `width`-bit float operand is a NaN. */
  inline def unordered(left: V, right: V, width: Int in (16, 32, 64)): V =
    or(isNaN(left, width), isNaN(right, width))

  /** 1 when either operand is a NaN of the float whose sign bit and exponent field are `sign` and `infinity`. */
  inline def unordered(left: V, right: V, inline sign: Long, inline infinity: Long): V =
    or(isNaN(left, sign, infinity), isNaN(right, sign, infinity))

  /** `value` as the IEEE value of `width` bits, which must be a literal, that each function takes. */
  inline def floating(inline width: Int, value: V)(
      inline half: Float16 => Float16,
      inline single: Float32 => Float32,
      inline double: Float64 => Float64
  ): V = inline width match {
    case 16 => primitive(value)(half)
    case 32 => primitive(value)(single)
    case _  => primitive(value)(double)
  }

  inline def floating(inline width: Int, left: V, right: V)(
      inline half: (Float16, Float16) => Float16,
      inline single: (Float32, Float32) => Float32,
      inline double: (Float64, Float64) => Float64
  ): V = inline width match {
    case 16 => primitive(left, right)(half)
    case 32 => primitive(left, right)(single)
    case _  => primitive(left, right)(double)
  }

  inline def floating(inline width: Int, first: V, second: V, third: V)(
      inline half: (Float16, Float16, Float16) => Float16,
      inline single: (Float32, Float32, Float32) => Float32,
      inline double: (Float64, Float64, Float64) => Float64
  ): V = inline width match {
    case 16 => primitive(first, second, third)(half)
    case 32 => primitive(first, second, third)(single)
    case _  => primitive(first, second, third)(double)
  }

  /** The relation of the `width`, which must be a literal, that each function decides between `left` and `right`. */
  inline def relation(inline width: Int, left: V, right: V)(
      inline half: (Float16, Float16) => Bool,
      inline single: (Float32, Float32) => Bool,
      inline double: (Float64, Float64) => Bool
  ): V = inline width match {
    case 16 => primitive(left, right)(half)
    case 32 => primitive(left, right)(single)
    case _  => primitive(left, right)(double)
  }

  /** The function of the `width` an instruction selects, applied to `value`. */
  inline def dispatched(width: Int in (16, 32, 64), value: V)(
      inline half: Float16 => Float16,
      inline single: Float32 => Float32,
      inline double: Float64 => Float64
  ): V = width.raw match {
    case 16 => primitive(value)(half)
    case 32 => primitive(value)(single)
    case _  => primitive(value)(double)
  }

  /** The function of the `width` an instruction selects, applied to the operands. */
  inline def dispatched(width: Int in (16, 32, 64), left: V, right: V)(
      inline half: (Float16, Float16) => Float16,
      inline single: (Float32, Float32) => Float32,
      inline double: (Float64, Float64) => Float64
  ): V = width.raw match {
    case 16 => primitive(left, right)(half)
    case 32 => primitive(left, right)(single)
    case _  => primitive(left, right)(double)
  }

  inline def dispatched(width: Int in (16, 32, 64), first: V, second: V, third: V)(
      inline half: (Float16, Float16, Float16) => Float16,
      inline single: (Float32, Float32, Float32) => Float32,
      inline double: (Float64, Float64, Float64) => Float64
  ): V = width.raw match {
    case 16 => primitive(first, second, third)(half)
    case 32 => primitive(first, second, third)(single)
    case _  => primitive(first, second, third)(double)
  }

  /** `element` of the low `part` bits of `left` and `right`, below `element` of their next `part` bits. */
  inline def perHalf(part: Int in 1 ~ 64, left: V, right: V)(
      inline element: (V, V) => V
  ): V = {
    val shift = literal(part.toLong)
    or(
      element(truncate(left, part), truncate(right, part)),
      shiftLeft(
        element(
          truncate(shiftRightLogical(left, shift), part),
          truncate(shiftRightLogical(right, shift), part)
        ),
        shift
      )
    )
  }

  /** `element` of the low 16-bit halves of three operands, below `element` of their high halves. */
  inline def perHalf(
      first: V,
      second: V,
      third: V
  )(inline element: (V, V, V) => V): V = {
    val shift = literal(16L)
    or(
      element(truncate(first, 16), truncate(second, 16), truncate(third, 16)),
      shiftLeft(
        element(
          truncate(shiftRightLogical(first, shift), 16),
          truncate(shiftRightLogical(second, shift), 16),
          truncate(shiftRightLogical(third, shift), 16)
        ),
        shift
      )
    )
  }
}
