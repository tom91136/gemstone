package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** NVIDIA integer semantics, written once against an [[Algebra]]: executed lane by lane and rendered for description.
  * The operation is matched once per instruction, and each case expands into its own kernel.
  */
private[isa] object IntegerSemantics {

  inline def move[V, R](
      inline algebra: Algebra[V],
      width: Int in 1 ~ 64
  )(inline sinks: NvidiaSinks[V, R], destination: Register, source: Value): R =
    sinks.unary(destination, source)(value => algebra.truncate(value, width))

  inline def moveVector[V, R](inline algebra: Algebra[V], width: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destinations: Vec[Register],
      sources: Vec[Value]
  ): R = sinks.elements(destinations, sources)(value => algebra.truncate(value, width))

  /** `mov` from the warp's lowest active lane, as SASS uniform registers hold one value per warp. */
  inline def uniformMove[V, R](inline algebra: Algebra[V])(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value
  ): R = sinks.broadcast(destination, source)(value => algebra.truncate(value, 32))

  inline def subtractAdd[V, R](
      inline algebra: Algebra[V],
      width: Int in 1 ~ 64
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    sinks.ternary(destination, first, second, third)((left, right, addend) =>
      algebra.truncate(algebra.add(algebra.subtract(left, right), addend), width)
    )

  /** `mad.lo` and IMAD without carries: the low product of the operands at `operand` bits plus the addend. */
  inline def multiplyAddLow[V, R](inline algebra: Algebra[V], operand: Int in 1 ~ 64, result: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R = sinks.ternary(destination, first, second, third)((left, right, addend) =>
    algebra.truncate(
      algebra.add(
        algebra.multiply(algebra.truncate(left, operand), algebra.truncate(right, operand)),
        algebra.truncate(addend, result)
      ),
      result
    )
  )

  /** `add.cc` and `addc`: the operands at `width` bits plus the lane's carry when `carryIn`, setting it when
    * `carryOut`.
    */
  inline def addWithCarry[V, R](inline algebra: Algebra[V], width: Int in 1 ~ 64, carryIn: Boolean, carryOut: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      left: Value,
      right: Value
  ): R =
    if (carryIn) {
      if (carryOut)
        sinks.carryInOut(destination, left, right)(
          (l, r, c) => algebra.carriedSum(l, r, c, width),
          (l, r, c, _) => algebra.carryFrom(l, r, c, width)
        )
      else sinks.carryIn(destination, left, right)((l, r, c) => algebra.carriedSum(l, r, c, width))
    } else if (carryOut)
      sinks.carryInOut(destination, left, right)(
        (l, r, _) => algebra.carriedSum(l, r, algebra.literal(0L), width),
        (l, r, _, _) => algebra.carryFrom(l, r, algebra.literal(0L), width)
      )
    else sinks.binary(destination, left, right)((l, r) => algebra.carriedSum(l, r, algebra.literal(0L), width))

  /** `sub.cc` and `subc`: the operands at `width` bits less the lane's borrow when `borrowIn`, setting it when
    * `borrowOut`.
    */
  inline def subtractWithBorrow[V, R](
      inline algebra: Algebra[V],
      width: Int in 1 ~ 64,
      borrowIn: Boolean,
      borrowOut: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, left: Value, right: Value): R =
    if (borrowIn) {
      if (borrowOut)
        sinks.carryInOut(destination, left, right)(
          (l, r, c) => algebra.borrowedDifference(l, r, c, width),
          (l, r, c, _) => algebra.borrowFrom(l, r, c, width)
        )
      else sinks.carryIn(destination, left, right)((l, r, c) => algebra.borrowedDifference(l, r, c, width))
    } else if (borrowOut)
      sinks.carryInOut(destination, left, right)(
        (l, r, _) => algebra.borrowedDifference(l, r, algebra.literal(0L), width),
        (l, r, _, _) => algebra.borrowFrom(l, r, algebra.literal(0L), width)
      )
    else sinks.binary(destination, left, right)((l, r) => algebra.borrowedDifference(l, r, algebra.literal(0L), width))

  /** `mad.lo` and `madc.lo` with carries: the low product plus the addend and, when `carryIn`, the lane's carry. The
    * carry out is any part of the full sum beyond `result` bits.
    */
  inline def multiplyAddLowCarry[V, R](
      inline algebra: Algebra[V],
      operand: Int in 1 ~ 64,
      result: Int in 1 ~ 64,
      carryIn: Boolean,
      carryOut: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    if (carryIn) {
      if (carryOut)
        sinks.carryInOut(destination, first, second, third)(
          (l, r, a, c) => algebra.truncate(algebra.lowSum(l, r, a, c, operand, result), result),
          (l, r, a, c, _) => algebra.lowCarry(l, r, a, c, operand, result)
        )
      else
        sinks.carryIn(destination, first, second, third)((l, r, a, c) =>
          algebra.truncate(algebra.lowSum(l, r, a, c, operand, result), result)
        )
    } else if (carryOut)
      sinks.carryInOut(destination, first, second, third)(
        (l, r, a, _) => algebra.truncate(algebra.lowSum(l, r, a, algebra.literal(0L), operand, result), result),
        (l, r, a, _, _) => algebra.lowCarry(l, r, a, algebra.literal(0L), operand, result)
      )
    else
      sinks.ternary(destination, first, second, third)((l, r, a) =>
        algebra.truncate(algebra.lowSum(l, r, a, algebra.literal(0L), operand, result), result)
      )

  /** `madc.hi`: the high product half at `operand` bits plus the addend and the lane's carry, setting it when
    * `carryOut`. The product is signed where `signed`.
    */
  inline def multiplyAddHigh[V, R](
      inline algebra: Algebra[V],
      operand: Int in 1 ~ 64,
      result: Int in 1 ~ 64,
      carryOut: Boolean,
      signed: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    if (carryOut)
      sinks.carryInOut(destination, first, second, third)(
        (l, r, a, c) => algebra.carriedSum(algebra.highProduct(l, r, operand, signed), a, c, result),
        (l, r, a, c, _) => algebra.carryFrom(algebra.highProduct(l, r, operand, signed), a, c, result)
      )
    else
      sinks.carryIn(destination, first, second, third)((l, r, a, c) =>
        algebra.carriedSum(algebra.highProduct(l, r, operand, signed), a, c, result)
      )

  /** `mad.hi`: the high half of the product plus the addend at `width` bits, clamped to the signed 32-bit range where
    * `saturate`.
    */
  inline def multiplyAddHighSum[V, R](
      inline algebra: Algebra[V],
      width: Int in 1 ~ 64,
      signed: Boolean,
      saturate: Boolean
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    if (saturate) sinks.ternary(destination, first, second, third)((l, r, a) =>
      algebra.truncate(
        algebra.minimum(
          algebra.maximum(
            algebra.add(
              algebra.signExtend(algebra.multiplyHigh(l, r, 32, signed = true), 32),
              algebra.signExtend(a, 32)
            ),
            algebra.literal(Int.MinValue.toLong),
            signed = true
          ),
          algebra.literal(Int.MaxValue.toLong),
          signed = true
        ),
        32
      )
    )
    else if (signed) sinks.ternary(destination, first, second, third)((l, r, a) =>
      algebra.truncate(
        algebra.add(
          algebra.multiplyHigh(l, r, width, signed = true),
          a
        ),
        width
      )
    )
    else sinks.ternary(destination, first, second, third)((l, r, a) =>
      algebra.truncate(algebra.add(algebra.highProduct(l, r, width, signed = false), a), width)
    )

  /** `mad.hi.cc`: the high half of the product plus the addend at `width` bits, which sets the carry flag. The flag
    * leaves the addition of the addend to the high half, which the lane's own carry does not join.
    */
  inline def multiplyAddHighSumCarry[V, R](
      inline algebra: Algebra[V],
      width: Int in 1 ~ 64,
      signed: Boolean
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    sinks.carryInOut(destination, first, second, third)(
      (l, r, a, _) =>
        algebra.truncate(
          algebra.add(
            algebra.highProduct(l, r, width, signed),
            a
          ),
          width
        ),
      (l, r, a, _, _) =>
        algebra.carryFrom(
          algebra.highProduct(l, r, width, signed),
          a,
          algebra.literal(0L),
          width
        )
    )

  /** `mad.wide`: the full product of the operands extended from `operand` bits plus the addend. */
  inline def multiplyAddWide[V, R](inline algebra: Algebra[V], operand: Int in 1 ~ 64, signed: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    if (signed) sinks.ternary(destination, first, second, third)((l, r, a) =>
      algebra.truncate(
        algebra.add(algebra.multiply(algebra.signExtend(l, operand), algebra.signExtend(r, operand)), a),
        (2 * operand).unsafe[Int in 1 ~ 64]
      )
    )
    else sinks.ternary(destination, first, second, third)((l, r, a) =>
      algebra.truncate(
        algebra.add(algebra.multiply(algebra.truncate(l, operand), algebra.truncate(r, operand)), a),
        (2 * operand).unsafe[Int in 1 ~ 64]
      )
    )

  /** `mul24` and `mad24`: the 48-bit product of the low 24 bits of the operands, its low word or bits 16 to 47, plus
    * the addend, clamped to the signed 32-bit range where `saturate`.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def multiplyAddNarrow[V, R](inline algebra: Algebra[V], high: Boolean, signed: Boolean, saturate: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    if (signed) {
      if (saturate) sinks.ternary(destination, first, second, third)((l, r, a) =>
        algebra.truncate(
          algebra.minimum(
            algebra.maximum(
              algebra.add(
                algebra.signExtend(algebra.narrowProduct(l, r, high, signed = true), 32),
                algebra.signExtend(a, 32)
              ),
              algebra.literal(Int.MinValue.toLong),
              signed = true
            ),
            algebra.literal(Int.MaxValue.toLong),
            signed = true
          ),
          32
        )
      )
      else sinks.ternary(destination, first, second, third)((l, r, a) =>
        algebra.truncate(algebra.add(algebra.narrowProduct(l, r, high, signed = true), a), 32)
      )
    } else sinks.ternary(destination, first, second, third)((l, r, a) =>
      algebra.truncate(algebra.add(algebra.narrowProduct(l, r, high, signed = false), a), 32)
    )

  extension [V](inline algebra: Algebra[V]) {
    inline def narrowProduct(
        left: V,
        right: V,
        high: Boolean,
        inline signed: Boolean
    ): V = {
      val product =
        algebra.multiply(algebra.extend(left, 24, signed), algebra.extend(right, 24, signed))
      algebra.truncate(
        if (!high) product
        else if (signed) algebra.shiftRightArithmetic(product, algebra.literal(16L))
        else algebra.shiftRightLogical(product, algebra.literal(16L)),
        32
      )
    }

    inline def lowSum(
        left: V,
        right: V,
        addend: V,
        carry: V,
        operand: Int in 1 ~ 64,
        result: Int in 1 ~ 64
    ): V = algebra.add(
      algebra.add(
        algebra.multiply(algebra.truncate(left, operand), algebra.truncate(right, operand)),
        algebra.truncate(addend, result)
      ),
      carry
    )

    /** The carry out of the low product plus addend and carry: the bit of the sum beyond `result` ones, or, where the
      * result is as wide as the sum, the bit the sum wraps into.
      */
    inline def lowCarry(
        left: V,
        right: V,
        addend: V,
        carry: V,
        operand: Int in 1 ~ 64,
        result: Int in 1 ~ 64
    ): V = {
      val product = algebra.multiply(algebra.truncate(left, operand), algebra.truncate(right, operand))
      if (result == 64)
        algebra.let("p", product)(product =>
          algebra.let("f", algebra.add(product, algebra.truncate(addend, result)))(first =>
            algebra.let("s", algebra.add(first, carry))(sum =>
              algebra.or(
                algebra.lessThan(first, product, signed = false),
                algebra.and(carry, algebra.lessThan(sum, first, signed = false))
              )
            )
          )
        )
      else
        algebra.let("p", algebra.truncate(product, result))(product =>
          algebra.let("f", algebra.add(product, algebra.truncate(addend, result)))(first =>
            algebra.let("s", algebra.add(first, carry))(sum =>
              algebra.notEqual(
                algebra.and(algebra.shiftRightLogical(sum, algebra.literal(result.toLong)), algebra.literal(1L)),
                algebra.literal(0L)
              )
            )
          )
        )
    }

    /** The high `operand` bits of the `2 * operand`-bit product, signed where `signed`. */
    inline def highProduct(left: V, right: V, operand: Int in 1 ~ 64, signed: Boolean): V =
      algebra.multiplyHigh(algebra.truncate(left, operand), algebra.truncate(right, operand), operand, signed = signed)

    inline def carriedSum(left: V, right: V, carry: V, width: Int in 1 ~ 64): V =
      algebra.truncate(
        algebra.add(algebra.add(algebra.truncate(left, width), algebra.truncate(right, width)), carry),
        width
      )

    /** The carry out of `width` bits: an unsigned wrap at 64 bits, otherwise any bit above `width`. */
    inline def carryFrom(left: V, right: V, carry: V, width: Int in 1 ~ 64): V =
      algebra.let("a", algebra.truncate(left, width))(a =>
        algebra.let("s", algebra.add(a, algebra.truncate(right, width)))(sum =>
          if (width == 64)
            algebra.or(
              algebra.lessThan(sum, a, signed = false),
              algebra.and(carry, algebra.equal(algebra.add(sum, carry), algebra.literal(0L)))
            )
          else
            algebra.notEqual(
              algebra.shiftRightLogical(algebra.add(sum, carry), algebra.literal(width.toLong)),
              algebra.literal(0L)
            )
        )
      )

    inline def borrowedDifference(left: V, right: V, borrow: V, width: Int in 1 ~ 64): V =
      algebra.truncate(
        algebra.subtract(algebra.subtract(algebra.truncate(left, width), algebra.truncate(right, width)), borrow),
        width
      )

    inline def borrowFrom(left: V, right: V, borrow: V, width: Int in 1 ~ 64): V =
      algebra.let("a", algebra.truncate(left, width))(a =>
        algebra.let("b", algebra.truncate(right, width))(b =>
          algebra.or(algebra.lessThan(a, b, signed = false), algebra.and(borrow, algebra.equal(a, b)))
        )
      )
  }

  /** IADD3 without carries; a negated operand contributes its ones' complement. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def addThree[V, R](
      inline algebra: Algebra[V],
      width: Int in 1 ~ 64,
      negateFirst: Boolean,
      negateSecond: Boolean,
      negateThird: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    sinks.ternary(destination, first, second, third)((a, b, c) =>
      algebra.truncate(
        algebra.add(
          algebra.add(algebra.complement(a, negateFirst), algebra.complement(b, negateSecond)),
          algebra.complement(c, negateThird)
        ),
        width
      )
    )

  extension [V](inline algebra: Algebra[V]) {
    inline def complement(value: V, negate: Boolean): V =
      if (negate) algebra.not(value) else value
  }

  inline def unary[V, R](inline algebra: Algebra[V], operation: IntegerUnaryOperation, width: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value
  ): R = operation match {
    case IntegerUnaryOperation.Negate =>
      sinks.unary(destination, source)(v => algebra.truncate(algebra.subtract(algebra.literal(0L), v), width))
    case IntegerUnaryOperation.BitwiseNot =>
      sinks.unary(destination, source)(v => algebra.truncate(algebra.not(v), width))
    case IntegerUnaryOperation.LogicalNot =>
      sinks.unary(destination, source)(v => algebra.equal(algebra.truncate(v, width), algebra.literal(0L)))
    case IntegerUnaryOperation.Absolute        => sinks.unary(destination, source)(v => algebra.absolute(v, width))
    case IntegerUnaryOperation.PopulationCount =>
      sinks.unary(destination, source)(v => algebra.populationCount(algebra.truncate(v, width)))
    case IntegerUnaryOperation.LeadingZeroCount =>
      if (width == 32) sinks.unary(destination, source)(v => algebra.leadingZeros(v, 32))
      else sinks.unary(destination, source)(v => algebra.truncate(algebra.leadingZeros(v, 64), width))
    case IntegerUnaryOperation.BitReverse => sinks.unary(destination, source)(v => algebra.reverseBits(v, width))
  }

  /** `dp2a`: `addend` plus the products of the 16-bit halves of `left` and the low or, when `high`, the high 8-bit
    * lanes of `right`.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def dotProduct2[V, R](inline algebra: Algebra[V], high: Boolean, leftSigned: Boolean, rightSigned: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R = sinks.ternary(destination, first, second, third)((left, right, addend) =>
    algebra.truncate(
      algebra.add(
        algebra.add(
          addend,
          algebra.multiply(
            algebra.field(left, 0, 16, leftSigned),
            algebra.field(right, if (high) 16 else 0, 8, rightSigned)
          )
        ),
        algebra.multiply(
          algebra.field(left, 16, 16, leftSigned),
          algebra.field(right, if (high) 24 else 8, 8, rightSigned)
        )
      ),
      32
    )
  )

  /** `sad`: `addend` plus the absolute difference of `left` and `right`. */
  inline def sumAbsoluteDifference[V, R](inline algebra: Algebra[V], width: Int in 1 ~ 64, signed: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    if (signed) sinks.ternary(destination, first, second, third)((left, right, addend) =>
      algebra.truncate(
        algebra.add(addend, algebra.absoluteDifference(left, right, width, signed = true)),
        width
      )
    )
    else sinks.ternary(destination, first, second, third)((left, right, addend) =>
      algebra.truncate(
        algebra.add(addend, algebra.absoluteDifference(left, right, width, signed = false)),
        width
      )
    )

  /** `cvt.pack.sat`: two signed words clamped to the range of a `bits`-bit integer and packed, the second lowest, over
    * the low bits of `rest`.
    */
  inline def packSaturated[V, R](inline algebra: Algebra[V], bits: Int in 1 ~ 16, signed: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      high: Value,
      low: Value,
      rest: Value
  ): R = {
    val lowest  = if (signed) -(1L << (bits - 1)) else 0L
    val highest = if (signed) (1L << (bits - 1)) - 1L else (1L << bits) - 1L
    sinks.ternary(destination, high, low, rest)((a, b, c) =>
      algebra.truncate(
        algebra.or(
          algebra.or(
            algebra.clamped(b, lowest, highest, bits),
            algebra.shiftLeft(algebra.clamped(a, lowest, highest, bits), algebra.literal(bits.toLong))
          ),
          if (bits == 16) algebra.literal(0L) else algebra.shiftLeft(c, algebra.literal(2L * bits))
        ),
        32
      )
    )
  }

  extension [V](inline algebra: Algebra[V]) {
    inline def clamped(value: V, lowest: Long, highest: Long, bits: Int in 1 ~ 16): V =
      algebra.truncate(algebra.clamp(algebra.signExtend(value, 32), lowest, highest), bits.widen[Int in 1 ~ 64])
  }

  inline def convertToFloat8[V, R](inline algebra: Algebra[V], format: Float8Format, halves: Boolean, relu: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      high: Value,
      low: Value,
      rest: Value
  ): R = (format, halves) match {
    case (Float8Format.E4M3, true) => sinks.ternary(destination, high, low, rest)((a, b, c) =>
        algebra.packedFloat8(
          algebra.primitive(algebra.truncate(algebra.shiftRightLogical(a, algebra.literal(16L)), 16))(
            (x: prelude.Float16) => IntegerArithmetic.toE4M3(x, relu)
          ),
          algebra.primitive(algebra.truncate(b, 16))((x: prelude.Float16) => IntegerArithmetic.toE4M3(x, relu)),
          c
        )
      )
    case (Float8Format.E5M2, true) => sinks.ternary(destination, high, low, rest)((a, b, c) =>
        algebra.packedFloat8(
          algebra.primitive(algebra.truncate(algebra.shiftRightLogical(a, algebra.literal(16L)), 16))(
            (x: prelude.Float16) => IntegerArithmetic.toE5M2(x, relu)
          ),
          algebra.primitive(algebra.truncate(b, 16))((x: prelude.Float16) => IntegerArithmetic.toE5M2(x, relu)),
          c
        )
      )
    case (Float8Format.E4M3, false) => sinks.ternary(destination, high, low, rest)((a, b, c) =>
        algebra.packedFloat8(
          algebra.primitive(a)((x: prelude.Float32) => IntegerArithmetic.toE4M3(x, relu)),
          algebra.primitive(b)((x: prelude.Float32) => IntegerArithmetic.toE4M3(x, relu)),
          c
        )
      )
    case (Float8Format.E5M2, false) => sinks.ternary(destination, high, low, rest)((a, b, c) =>
        algebra.packedFloat8(
          algebra.primitive(a)((x: prelude.Float32) => IntegerArithmetic.toE5M2(x, relu)),
          algebra.primitive(b)((x: prelude.Float32) => IntegerArithmetic.toE5M2(x, relu)),
          c
        )
      )
  }

  extension [V](inline algebra: Algebra[V]) {
    inline def packedFloat8(high: V, low: V, rest: V): V = algebra.or(
      algebra.and(rest, algebra.literal(0xffff0000L)),
      algebra.or(algebra.shiftLeft(high, algebra.literal(8L)), low)
    )
  }

  /** The paired narrow conversions, through one primitive of the whole pair. */
  inline def convertNarrow[V, R](
      inline algebra: Algebra[V],
      format: NarrowFormat,
      halves: Boolean,
      bf16: Boolean,
      rounding: ConversionRounding,
      relu: Boolean,
      saturate: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, high: Value, low: Value, scale: Value): R =
    if (halves) sinks.binary(destination, high, scale)((pair, factors) =>
      algebra.primitive(algebra.truncate(pair, 32), algebra.truncate(factors, 16))(
        (a: prelude.UInt[32], s: prelude.UInt[16]) =>
          IntegerArithmetic.narrowHalves(a, s, format, bf16, rounding, relu, saturate)
      )
    )
    else sinks.ternary(destination, high, low, scale)((a, b, factors) =>
      algebra.primitive(algebra.truncate(a, 32), algebra.truncate(b, 32), algebra.truncate(factors, 16))(
        (x: prelude.Float32, y: prelude.Float32, s: prelude.UInt[16]) =>
          IntegerArithmetic.narrowFloats(x, y, s, format, rounding, relu, saturate)
      )
    )

  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def convertWiden[V, R](
      inline algebra: Algebra[V],
      format: NarrowFormat,
      bf16: Boolean,
      relu: Boolean,
      saturate: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, source: Value, scale: Value): R =
    sinks.binary(destination, source, scale)((pair, factors) =>
      algebra.primitive(algebra.truncate(pair, 16), algebra.truncate(factors, 16))(
        (a: prelude.UInt[16], s: prelude.UInt[16]) => IntegerArithmetic.widen(a, s, format, bf16, relu, saturate)
      )
    )

  inline def convertFromFloat8[V, R](inline algebra: Algebra[V], format: Float8Format)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value
  ): R = format match {
    case Float8Format.E4M3 => sinks.unary(destination, source)(value =>
        algebra.or(
          algebra.shiftLeft(
            algebra.primitive(algebra.truncate(algebra.shiftRightLogical(value, algebra.literal(8L)), 8))(
              (x: prelude.Float8E4M3) => IntegerArithmetic.fromE4M3(x)
            ),
            algebra.literal(16L)
          ),
          algebra.primitive(algebra.truncate(value, 8))((x: prelude.Float8E4M3) => IntegerArithmetic.fromE4M3(x))
        )
      )
    case Float8Format.E5M2 => sinks.unary(destination, source)(value =>
        algebra.or(
          algebra.shiftLeft(
            algebra.primitive(algebra.truncate(algebra.shiftRightLogical(value, algebra.literal(8L)), 8))(
              (x: prelude.Float8E5M2) => IntegerArithmetic.fromE5M2(x)
            ),
            algebra.literal(16L)
          ),
          algebra.primitive(algebra.truncate(value, 8))((x: prelude.Float8E5M2) => IntegerArithmetic.fromE5M2(x))
        )
      )
  }

  private inline def stochastic[V](
      inline algebra: Algebra[V],
      format: StochasticFormat,
      relu: Boolean,
      saturate: Boolean
  )(
      value: V,
      random: V,
      offset: Long
  ): V =
    algebra.primitive(value, algebra.truncate(algebra.shiftRightLogical(random, algebra.literal(offset)), 16))(
      (x: prelude.Float32, bits: prelude.UInt[16]) =>
        IntegerArithmetic.toStochastic(x, bits, format, relu, saturate)
    )

  /** `cvt.rs`: `high` and `low` rounded with their random bits and packed into the upper or lower half of the
    * destination over the other half of `rest`.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def convertStochastic[V, R](
      inline algebra: Algebra[V],
      format: StochasticFormat,
      upper: Boolean,
      relu: Boolean,
      saturate: Boolean
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      high: Value,
      low: Value,
      bits: Value,
      rest: Value
  ): R = {
    val width: Int = format match {
      case StochasticFormat.Half | StochasticFormat.BFloat16 => 16
      case StochasticFormat.E2M1                             => 4
      case _                                                 => 8
    }
    val pair  = 2 * width
    val shift = if (upper) pair else 0
    val keep  = if (pair == 32) 0L else 0xffffffffL & ~(((1L << pair) - 1L) << shift)
    // binary16 and bfloat16 take a half of the random bits each, E2M1 a byte each, and the rest share the half of the
    // pair they fill
    val (highBits, lowBits) =
      if (width == 16) (16L, 0L)
      else if (width == 4) (if (upper) 24L else 16L, if (upper) 8L else 0L)
      else if (upper) (16L, 16L)
      else (0L, 0L)
    sinks.quaternary(destination, high, low, bits, rest)((a, b, r, c) =>
      algebra.or(
        algebra.and(c, algebra.literal(keep)),
        algebra.shiftLeft(
          algebra.or(
            algebra.shiftLeft(
              stochastic(algebra, format, relu, saturate)(a, r, highBits),
              algebra.literal(width.toLong)
            ),
            stochastic(algebra, format, relu, saturate)(b, r, lowBits)
          ),
          algebra.literal(shift.toLong)
        )
      )
    )
  }

  /** A scalar video operation. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def videoScalar[V, R](
      inline algebra: Algebra[V],
      operation: VideoOperation,
      leftSelect: VideoSelect,
      rightSelect: VideoSelect,
      leftSigned: Boolean,
      rightSigned: Boolean,
      resultSigned: Boolean,
      saturate: Boolean,
      secondary: VideoSecondary,
      merge: VideoSelect,
      wrap: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    sinks.ternary(destination, first, second, third)((left, right, addend) =>
      algebra.primitive(left, right, addend)((a: prelude.UInt[32], b: prelude.UInt[32], c: prelude.UInt[32]) =>
        IntegerArithmetic.video(
          a,
          b,
          c,
          operation,
          leftSelect,
          rightSelect,
          leftSigned,
          rightSigned,
          resultSigned,
          saturate,
          secondary,
          merge,
          wrap
        )
      )
    )

  /** `vmad`. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def videoMultiplyAdd[V, R](
      inline algebra: Algebra[V],
      leftSelect: VideoSelect,
      rightSelect: VideoSelect,
      leftSigned: Boolean,
      rightSigned: Boolean,
      negateProduct: Boolean,
      negateAddend: Boolean,
      addendSigned: Boolean,
      plusOne: Boolean,
      shift: Int in (0, 7, 15),
      saturate: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    sinks.ternary(destination, first, second, third)((left, right, addend) =>
      algebra.primitive(left, right, addend)((a: prelude.UInt[32], b: prelude.UInt[32], c: prelude.UInt[32]) =>
        IntegerArithmetic.videoMultiplyAdd(
          a,
          b,
          c,
          leftSelect,
          rightSelect,
          leftSigned,
          rightSigned,
          negateProduct,
          negateAddend,
          addendSigned,
          plusOne,
          shift,
          saturate
        )
      )
    )

  /** `slct`: the first value where the condition is not negative, as a signed word or, flushed where `flush`, a
    * binary32 value, of which NaN is negative and either zero is not.
    */
  inline def selectSign[V, R](inline algebra: Algebra[V], width: Int in 1 ~ 64, floating: Boolean, flush: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      whenNonNegative: Value,
      whenNegative: Value,
      condition: Value
  ): R =
    if (floating) sinks.ternary(destination, whenNonNegative, whenNegative, condition)((a, b, c) =>
      algebra.truncate(
        algebra.select(
          algebra.primitive(c)((x: prelude.Float32) => IntegerArithmetic.nonNegative(x, flush)),
          a,
          b
        ),
        width
      )
    )
    else sinks.ternary(destination, whenNonNegative, whenNegative, condition)((a, b, c) =>
      algebra.truncate(
        algebra.select(algebra.lessOrEqual(algebra.literal(0L), algebra.signExtend(c, 32), signed = true), a, b),
        width
      )
    )

  inline def testProperty[V, R](inline algebra: Algebra[V], property: FloatProperty, width: Int in (32, 64))(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      source: Value
  ): R =
    if (width == 32)
      sinks.test(destination, source)(value =>
        algebra.primitive(value)((x: prelude.Float32) => IntegerArithmetic.test(x, property))
      )
    else
      sinks.test(destination, source)(value =>
        algebra.primitive(value)((x: prelude.Float64) => IntegerArithmetic.test(x, property))
      )

  /** `set` with a Boolean operator. */
  inline def compareSetCombined[V, R](
      inline algebra: Algebra[V],
      comparison: Comparison,
      format: NumericFormat,
      truth: Long,
      flush: Boolean,
      operation: PredicateOperation,
      negated: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, left: Value, right: Value, predicate: Predicate): R =
    sinks.ternary(destination, left, right, Value.Predicate(predicate))((l, r, p) =>
      algebra.primitive(
        algebra.combined(operation, negated, algebra.compared(comparison, format, flush, l, r), p)
      )((holds: prelude.Bool) => IntegerArithmetic.truth(holds, truth))
    )

  /** `setp` with a Boolean operator. */
  inline def setPredicateCombined[V, R](
      inline algebra: Algebra[V],
      comparison: Comparison,
      format: NumericFormat,
      flush: Boolean,
      operation: PredicateOperation,
      negated: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Predicate, left: Value, right: Value, predicate: Predicate): R =
    sinks.compare(destination, left, right, predicate)((l, r, p) =>
      algebra.combined(operation, negated, algebra.compared(comparison, format, flush, l, r), p)
    )

  extension [V](inline algebra: Algebra[V]) {
    inline def combined(
        operation: PredicateOperation,
        negated: Boolean,
        comparison: V,
        predicate: V
    ): V = {
      val other = if (negated) algebra.equal(predicate, algebra.literal(0L)) else predicate
      operation match {
        case PredicateOperation.And => algebra.and(comparison, other)
        case PredicateOperation.Or  => algebra.or(comparison, other)
        case PredicateOperation.Xor => algebra.xor(comparison, other)
      }
    }
  }

  inline def spaceTest[V, R](inline algebra: Algebra[V], space: AddressKind)(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      address: Value
  ): R = sinks.test(destination, address)(value =>
    algebra.primitive(value)((pointer: prelude.UInt[64]) => IntegerArithmetic.within(pointer, space))
  )

  inline def findNthSet[V, R](inline algebra: Algebra[V])(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      mask: Value,
      base: Value,
      offset: Value
  ): R = sinks.ternary(destination, mask, base, offset)((m, b, o) =>
    algebra.primitive(m, b, o)(IntegerArithmetic.findNthSet(_, _, _))
  )

  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def simd[V, R](
      inline algebra: Algebra[V],
      operation: VideoOperation,
      lanes: Int in (2, 4),
      leftSigned: Boolean,
      rightSigned: Boolean,
      resultSigned: Boolean,
      saturate: Boolean,
      accumulate: Boolean,
      leftElements: Option[VideoElements],
      rightElements: Option[VideoElements]
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    sinks.ternary(destination, first, second, third)((left, right, addend) =>
      algebra.primitive(left, right, addend)((a: prelude.UInt[32], b: prelude.UInt[32], c: prelude.UInt[32]) =>
        IntegerArithmetic.simd(
          a,
          b,
          c,
          operation,
          lanes,
          leftSigned,
          rightSigned,
          resultSigned,
          saturate,
          accumulate,
          leftElements,
          rightElements
        )
      )
    )

  /** The legacy IADD.SAT: `lanes` lanes of `left` and `right` added, each clamped to the signed range of a lane, with
    * the carry out of the low lane's addition added to the high lane and the lanes of `right` negated exactly where
    * `negate`.
    */
  inline def packedSaturatingAdd[V, R](
      inline algebra: Algebra[V],
      lanes: Int in (2, 4),
      negate: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, left: Value, right: Value): R =
    sinks.binary(destination, left, right)((l, r) =>
      algebra.primitive(l, r)((a: prelude.UInt[32], b: prelude.UInt[32]) =>
        IntegerArithmetic.packedSaturatingAdd(a, b, lanes, negate)
      )
    )

  /** `cvt` of two binary32 sources into binary16 or bfloat16 halves of one register, the first into the upper half,
    * each clamped at zero where `relu`.
    */
  inline def convertPacked[V, R](
      inline algebra: Algebra[V],
      bfloat16: Boolean,
      mode: ConversionRounding,
      relu: Boolean
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      high: Value,
      low: Value
  ): R =
    if (bfloat16) sinks.binary(destination, high, low)((upper, lower) =>
      algebra.or(
        algebra.shiftLeft(algebra.narrowedBfloat16(mode, relu, upper), algebra.literal(16L)),
        algebra.narrowedBfloat16(mode, relu, lower)
      )
    )
    else sinks.binary(destination, high, low)((upper, lower) =>
      algebra.or(
        algebra.shiftLeft(algebra.narrowedHalf(mode, relu, upper), algebra.literal(16L)),
        algebra.narrowedHalf(mode, relu, lower)
      )
    )

  extension [V](inline algebra: Algebra[V]) {
    inline def narrowedHalf(mode: ConversionRounding, relu: Boolean, value: V): V = {
      val converted = algebra.primitive(value)((x: prelude.Float32) => Conversions.toFloating(x, mode, 16))
      if (relu) algebra.primitive(converted)((x: prelude.Float16) => Conversions.relu(x)) else converted
    }

    inline def narrowedBfloat16(
        mode: ConversionRounding,
        relu: Boolean,
        value: V
    ): V = {
      val converted = algebra.primitive(value)((x: prelude.Float32) => Conversions.toBfloat16(x, mode))
      if (relu) algebra.primitive(converted)(Conversions.reluBfloat16(_)) else converted
    }
  }

  /** `set`: `truth` where the sources compare as `comparison` says, otherwise 0, for each half where `packed`. */
  inline def compareSet[V, R](
      inline algebra: Algebra[V],
      comparison: Comparison,
      format: NumericFormat,
      truth: Long,
      packed: Boolean,
      flush: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, left: Value, right: Value): R =
    if (packed) sinks.binary(destination, left, right)((l, r) =>
      algebra.or(
        algebra.primitive(
          algebra.compared(comparison, format, flush, algebra.truncate(l, 16), algebra.truncate(r, 16))
        )((holds: prelude.Bool) => IntegerArithmetic.truth(holds, truth)),
        algebra.shiftLeft(
          algebra.primitive(
            algebra.compared(
              comparison,
              format,
              flush,
              algebra.truncate(algebra.shiftRightLogical(l, algebra.literal(16L)), 16),
              algebra.truncate(algebra.shiftRightLogical(r, algebra.literal(16L)), 16)
            )
          )((holds: prelude.Bool) => IntegerArithmetic.truth(holds, truth)),
          algebra.literal(16L)
        )
      )
    )
    else sinks.binary(destination, left, right)((l, r) =>
      algebra.primitive(algebra.compared(comparison, format, flush, l, r))((holds: prelude.Bool) =>
        IntegerArithmetic.truth(holds, truth)
      )
    )

  /** Whether `left` and `right`, of `format`, compare as `comparison` says. */
  extension [V](inline algebra: Algebra[V]) {
    inline def compared(
        comparison: Comparison,
        format: NumericFormat,
        flush: Boolean,
        left: V,
        right: V
    ): V = format match {
      case NumericFormat.IEEE(width) if width == 16 =>
        algebra.relation(
          16,
          FloatSemantics.flushed(algebra, 16, flush, left),
          FloatSemantics.flushed(algebra, 16, flush, right)
        )(
          FloatArithmetic.compare(_, _, comparison),
          FloatArithmetic.compare(_, _, comparison),
          FloatArithmetic.compare(_, _, comparison)
        )
      case NumericFormat.IEEE(width) if width == 32 =>
        algebra.relation(
          32,
          FloatSemantics.flushed(algebra, 32, flush, left),
          FloatSemantics.flushed(algebra, 32, flush, right)
        )(
          FloatArithmetic.compare(_, _, comparison),
          FloatArithmetic.compare(_, _, comparison),
          FloatArithmetic.compare(_, _, comparison)
        )
      case NumericFormat.IEEE(_) =>
        algebra.relation(64, left, right)(
          FloatArithmetic.compare(_, _, comparison),
          FloatArithmetic.compare(_, _, comparison),
          FloatArithmetic.compare(_, _, comparison)
        )
      case NumericFormat.BF16 => algebra.primitive(left, right)(FloatArithmetic.compareBfloat16(_, _, comparison))
      case NumericFormat.Signed(width) =>
        algebra.integerCompared(comparison, signed = true, width.widen[Int in 1 ~ 64], left, right)
      case other => algebra.integerCompared(comparison, signed = false, other.width.widen[Int in 1 ~ 64], left, right)
    }

    inline def integerCompared(
        comparison: Comparison,
        inline signed: Boolean,
        width: Int in 1 ~ 64,
        left: V,
        right: V
    ): V = {
      val l = algebra.extend(left, width, signed)
      val r = algebra.extend(right, width, signed)
      comparison match {
        case Comparison.Ordered                                                     => algebra.literal(1L)
        case Comparison.Unordered                                                   => algebra.literal(0L)
        case Comparison.Equal | Comparison.EqualUnordered                           => algebra.equal(l, r)
        case Comparison.NotEqual | Comparison.NotEqualUnordered                     => algebra.notEqual(l, r)
        case Comparison.LessThan | Comparison.LessThanUnordered                     => algebra.lessThan(l, r, signed)
        case Comparison.LessThanOrEqual | Comparison.LessThanOrEqualUnordered       => algebra.lessOrEqual(l, r, signed)
        case Comparison.GreaterThan | Comparison.GreaterThanUnordered               => algebra.lessThan(r, l, signed)
        case Comparison.GreaterThanOrEqual | Comparison.GreaterThanOrEqualUnordered => algebra.lessOrEqual(r, l, signed)
      }
    }
  }

  /** `cvt.tf32.f32`: to nearest with ties away as ptxas lowers it, or to nearest even or toward zero. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def convertTF32[V, R](
      inline algebra: Algebra[V],
      away: Boolean,
      towardZero: Boolean,
      relu: Boolean,
      saturate: Boolean
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value
  ): R =
    if (away)
      sinks.unary(destination, source)(bits =>
        algebra.primitive(bits)((x: prelude.Float32) => IntegerArithmetic.toTensorFloat32Away(x, saturate))
      )
    else
      sinks.unary(destination, source)(bits =>
        algebra.primitive(bits)((x: prelude.Float32) =>
          IntegerArithmetic.toTensorFloat32(x, towardZero, relu, saturate)
        )
      )

  /** `bfind`: the position of the highest bit unlike the sign, or its distance from the top when `shiftAmount`; all
    * ones when there is none.
    */
  inline def bitFind[V, R](inline algebra: Algebra[V], width: Int in 1 ~ 64, signed: Boolean, shiftAmount: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      source: Value
  ): R =
    if (signed) {
      if (shiftAmount)
        sinks.unary(destination, source)(v =>
          algebra.findMostSignificant(v, width, signed = true, fromTop = true, none = 0xffffffffL)
        )
      else sinks.unary(destination, source)(v =>
        algebra.findMostSignificant(v, width, signed = true, fromTop = false, none = 0xffffffffL)
      )
    } else if (shiftAmount)
      sinks.unary(destination, source)(v =>
        algebra.findMostSignificant(v, width, signed = false, fromTop = true, none = 0xffffffffL)
      )
    else sinks.unary(destination, source)(v =>
      algebra.findMostSignificant(v, width, signed = false, fromTop = false, none = 0xffffffffL)
    )

  /** `dp4a`: the addend plus the products of the operands' corresponding bytes, in 32 bits. */
  inline def dotProduct4[V, R](inline algebra: Algebra[V], leftSigned: Boolean, rightSigned: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    if (leftSigned) {
      if (rightSigned) sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.truncate(
          algebra.dot(c, l, r, width = 8, lanes = 4, leftSigned = true, rightSigned = true),
          32
        )
      )
      else sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.truncate(
          algebra.dot(c, l, r, width = 8, lanes = 4, leftSigned = true, rightSigned = false),
          32
        )
      )
    } else if (rightSigned) sinks.ternary(destination, first, second, third)((l, r, c) =>
      algebra.truncate(algebra.dot(c, l, r, width = 8, lanes = 4, leftSigned = false, rightSigned = true), 32)
    )
    else sinks.ternary(destination, first, second, third)((l, r, c) =>
      algebra.truncate(
        algebra.dot(c, l, r, width = 8, lanes = 4, leftSigned = false, rightSigned = false),
        32
      )
    )

  /** `bfe`: the field of `count` bits from `start`, each taken from its low byte, clipped to `width` and extended;
    * empty fields are 0 and a field wholly beyond `width` is the extended sign.
    */
  inline def bitFieldExtract[V, R](inline algebra: Algebra[V], width: Int in 1 ~ 64, signed: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    if (signed) sinks.ternary(destination, first, second, third)((source, start, count) =>
      algebra.extract(source, start, count, width, signed = true)
    )
    else sinks.ternary(destination, first, second, third)((source, start, count) =>
      algebra.extract(source, start, count, width, signed = false)
    )

  extension [V](inline algebra: Algebra[V]) {
    inline def extract(
        source: V,
        start: V,
        count: V,
        width: Int in 1 ~ 64,
        inline signed: Boolean
    ): V = algebra.let("s", algebra.and(start, algebra.literal(0xffL)))(s =>
      algebra.let("n", algebra.and(count, algebra.literal(0xffL)))(n =>
        algebra.select(
          algebra.lessThan(s, algebra.literal(width.toLong), signed = true),
          algebra.truncate(
            algebra.bitFieldExtract(
              algebra.extend(source, width, signed),
              s,
              algebra.fieldWidth(s, n, width),
              signed
            ),
            width
          ),
          if (signed)
            algebra.truncate(
              algebra.select(
                algebra.equal(n, algebra.literal(0L)),
                algebra.literal(0L),
                algebra.shiftRightArithmetic(algebra.signExtend(source, width), algebra.literal(63L))
              ),
              width
            )
          else algebra.literal(0L)
        )
      )
    )

    /** The bits of a field from `start`, below `width`, at most `count` of them. */
    inline def fieldWidth(start: V, count: V, width: Int in 1 ~ 64): V =
      algebra.minimum(count, algebra.subtract(algebra.literal(width.toLong), start), signed = true)
  }

  /** `bfi`: `base` with the field of `count` bits from `start` replaced by the low bits of `insert`, clipped to
    * `width`.
    */
  inline def bitFieldInsert[V, R](inline algebra: Algebra[V], width: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value,
      fourth: Value
  ): R = sinks.quaternary(destination, first, second, third, fourth)((insert, base, start, count) =>
    algebra.let("s", algebra.and(start, algebra.literal(0xffL)))(s =>
      algebra.let("n", algebra.and(count, algebra.literal(0xffL)))(n =>
        algebra.truncate(
          algebra.select(
            algebra.or(
              algebra.equal(n, algebra.literal(0L)),
              algebra.lessOrEqual(algebra.literal(width.toLong), s, signed = true)
            ),
            base,
            algebra.let(
              "field",
              algebra.shiftLeft(
                algebra.shiftRightLogical(
                  algebra.literal(-1L),
                  algebra.subtract(algebra.literal(64L), algebra.fieldWidth(s, n, width))
                ),
                s
              )
            )(field => algebra.blend(field, algebra.shiftLeft(insert, s), base))
          ),
          width
        )
      )
    )
  )

  /** `bmsk`: ones from bit `start` for `count` bits in 32. Each wraps modulo 32, unless clamped: a start beyond the
    * register is empty and a count beyond it fills the rest.
    */
  extension [V](inline algebra: Algebra[V]) {
    inline def bitMask[R](mode: BitMaskMode)(
        inline sinks: NvidiaSinks[V, R],
        destination: Register,
        left: Value,
        right: Value
    ): R =
      mode match {
        case BitMaskMode.Clamp =>
          sinks.binary(destination, left, right)((start, count) => algebra.bitMask(start, count, clamp = true))
        case BitMaskMode.Wrap =>
          sinks.binary(destination, left, right)((start, count) => algebra.bitMask(start, count, clamp = false))
      }

    inline def bitMask(start: V, count: V, inline clamp: Boolean): V =
      algebra.let("s", algebra.and(start, algebra.literal(31L)))(s =>
        algebra.let("e", algebra.add(s, algebra.and(count, algebra.literal(31L))))(end =>
          if (clamp)
            algebra.select(
              algebra.lessOrEqual(
                algebra.literal(32L),
                algebra.and(start, algebra.literal(0xffffffffL)),
                signed = true
              ),
              algebra.literal(0L),
              algebra.truncate(
                algebra.and(
                  algebra.shiftLeft(algebra.literal(-1L), s),
                  algebra.select(
                    algebra.or(
                      algebra.lessOrEqual(algebra.literal(32L), end, signed = true),
                      algebra.lessOrEqual(
                        algebra.literal(32L),
                        algebra.and(count, algebra.literal(0xffffffffL)),
                        signed = true
                      )
                    ),
                    algebra.literal(-1L),
                    algebra.not(algebra.shiftLeft(algebra.literal(-1L), end))
                  )
                ),
                32
              )
            )
          else
            algebra.truncate(
              algebra.and(
                algebra.shiftLeft(algebra.literal(-1L), s),
                algebra.select(
                  algebra.lessOrEqual(algebra.literal(32L), end, signed = true),
                  algebra.literal(-1L),
                  algebra.not(algebra.shiftLeft(algebra.literal(-1L), end))
                )
              ),
              32
            )
        )
      )
  }

  /** `prmt`: four bytes chosen from the eight of `right:left`, by the control's nibbles or by its low two bits and a
    * mode.
    */
  inline def bytePermute[V, R](inline algebra: Algebra[V], mode: BytePermuteMode)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R = mode match {
    case BytePermuteMode.Default => sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.bytePair(l, r)(pair => algebra.bytes(index => algebra.nibbleByte(pair, c, index)))
      )
    case BytePermuteMode.F4E => sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.bytePair(l, r)(pair =>
          algebra.let("s", algebra.and(c, algebra.literal(3L)))(s =>
            algebra.bytes(index =>
              algebra.pairByte(
                pair,
                if (index == 0) s else algebra.and(algebra.add(s, algebra.literal(index.toLong)), algebra.literal(7L))
              )
            )
          )
        )
      )
    case BytePermuteMode.B4E => sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.bytePair(l, r)(pair =>
          algebra.let("s", algebra.and(c, algebra.literal(3L)))(s =>
            algebra.bytes(index =>
              algebra.pairByte(
                pair,
                if (index == 0) s
                else algebra.and(algebra.subtract(s, algebra.literal(index.toLong)), algebra.literal(7L))
              )
            )
          )
        )
      )
    case BytePermuteMode.RC8 => sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.bytePair(l, r)(pair =>
          algebra.let("b", algebra.pairByte(pair, algebra.and(c, algebra.literal(3L))))(selected =>
            algebra.bytes(_ => selected)
          )
        )
      )
    case BytePermuteMode.ECL => sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.bytePair(l, r)(pair =>
          algebra.let("s", algebra.and(c, algebra.literal(3L)))(s =>
            algebra.bytes(index =>
              algebra.pairByte(
                pair,
                algebra.maximum(s, algebra.literal(index.toLong), signed = true)
              )
            )
          )
        )
      )
    case BytePermuteMode.ECR => sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.bytePair(l, r)(pair =>
          algebra.let("s", algebra.and(c, algebra.literal(3L)))(s =>
            algebra.bytes(index =>
              algebra.pairByte(
                pair,
                algebra.minimum(s, algebra.literal(index.toLong), signed = true)
              )
            )
          )
        )
      )
    case BytePermuteMode.RC16 => sinks.ternary(destination, first, second, third)((l, r, c) =>
        algebra.bytePair(l, r)(pair =>
          algebra.let("h", algebra.shiftLeft(algebra.and(c, algebra.literal(1L)), algebra.literal(1L)))(half =>
            algebra.bytes(index =>
              algebra.pairByte(pair, if ((index & 1) == 0) half else algebra.or(half, algebra.literal(1L)))
            )
          )
        )
      )
  }

  extension [V](inline algebra: Algebra[V]) {
    inline def bytePair(left: V, right: V)(inline body: V => V): V =
      algebra.let("pair", algebra.or(algebra.truncate(left, 32), algebra.shiftLeft(right, algebra.literal(32L))))(body)

    /** Four bytes, byte `index` of the result from `byte(index)`. */
    inline def bytes(inline byte: Int => V): V =
      algebra.or(
        algebra.or(
          algebra.or(byte(0), algebra.shiftLeft(byte(1), algebra.literal(8L))),
          algebra.shiftLeft(byte(2), algebra.literal(16L))
        ),
        algebra.shiftLeft(byte(3), algebra.literal(24L))
      )

    /** Byte `selection` of the eight in `pair`. */
    inline def pairByte(pair: V, selection: V): V =
      algebra.and(
        algebra.shiftRightLogical(pair, algebra.shiftLeft(selection, algebra.literal(3L))),
        algebra.literal(0xffL)
      )

    /** The byte the control's nibble `index` selects: its low three bits pick a byte, and its high bit replicates that
      * byte's sign instead.
      */
    inline def nibbleByte(pair: V, control: V, inline index: Int): V =
      algebra.let(
        s"n$index",
        if (index == 0) control else algebra.shiftRightLogical(control, algebra.literal(4L * index))
      )(nibble =>
        algebra.let(s"b$index", algebra.pairByte(pair, algebra.and(nibble, algebra.literal(7L))))(selected =>
          algebra.select(
            algebra.and(nibble, algebra.literal(8L)),
            algebra.and(
              algebra.subtract(algebra.literal(0L), algebra.shiftRightLogical(selected, algebra.literal(7L))),
              algebra.literal(0xffL)
            ),
            selected
          )
        )
      )
  }

  /** Conversions between integer formats are truncations and extensions, saturating when asked to; any conversion
    * involving a floating-point format is a named primitive.
    */
  inline def convert[V, R](
      inline algebra: Algebra[V],
      from: NumericFormat,
      to: NumericFormat,
      rounding: Option[ConversionRounding],
      clamp: Option[ConversionClamp]
  )(inline sinks: NvidiaSinks[V, R], destination: Register, source: Value): R =
    if (integral(from) && integral(to))
      convertInteger(algebra, from, to, saturate = clamp.contains(ConversionClamp.Saturate))(sinks, destination, source)
    else {
      val mode = rounding.getOrElse(if (integral(to)) ConversionRounding.RZ else ConversionRounding.RN)
      to match {
        case NumericFormat.Signed(_) =>
          sinks.unary(destination, source)(value =>
            algebra.truncate(Conversions.staged(algebra, from, to, mode, value), to.width.widen[Int in 1 ~ 64])
          )
        case NumericFormat.IEEE(width) if clamp.contains(ConversionClamp.Saturate) =>
          sinks.unary(destination, source)(value =>
            algebra.dispatched(width, Conversions.staged(algebra, from, to, mode, value))(
              Conversions.saturate(_),
              Conversions.saturate(_),
              Conversions.saturate(_)
            )
          )
        case NumericFormat.IEEE(width) if clamp.contains(ConversionClamp.Relu) =>
          sinks.unary(destination, source)(value =>
            algebra.dispatched(width, Conversions.staged(algebra, from, to, mode, value))(
              Conversions.relu(_),
              Conversions.relu(_),
              Conversions.relu(_)
            )
          )
        case NumericFormat.BF16 if clamp.contains(ConversionClamp.Saturate) =>
          sinks.unary(destination, source)(value =>
            algebra.primitive(Conversions.staged(algebra, from, to, mode, value))(Conversions.saturateBfloat16(_))
          )
        case NumericFormat.BF16 if clamp.contains(ConversionClamp.Relu) =>
          sinks.unary(destination, source)(value =>
            algebra.primitive(Conversions.staged(algebra, from, to, mode, value))(Conversions.reluBfloat16(_))
          )
        case _ => sinks.unary(destination, source)(value => Conversions.staged(algebra, from, to, mode, value))
      }
    }

  def integral(format: NumericFormat): Boolean = format match {
    case NumericFormat.Bits(_) | NumericFormat.Signed(_) | NumericFormat.Unsigned(_) => true
    case NumericFormat.IEEE(_) | NumericFormat.BF16                                  => false
  }

  /** A saturating conversion bounds the source value to the destination's range; a `Bits` source counts as signed. */
  inline def convertInteger[V, R](
      inline algebra: Algebra[V],
      sourceFormat: NumericFormat,
      destinationFormat: NumericFormat,
      saturate: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, source: Value): R = {
    val from         = sourceFormat.width.widen[Int in 1 ~ 64]
    val to           = destinationFormat.width.widen[Int in 1 ~ 64]
    val signedSource = sourceFormat match {
      case NumericFormat.Signed(_) => true
      case _                       => false
    }
    if (saturate) {
      val unsigned = sourceFormat match {
        case NumericFormat.Unsigned(_) => true
        case _                         => false
      }
      val signedDestination = destinationFormat match {
        case NumericFormat.Signed(_) => true
        case _                       => false
      }
      val maximum =
        if (signedDestination) (1L << (to - 1)) - 1L else if (to == 64) -1L else (1L << to) - 1L
      sinks.unary(destination, source)(value =>
        algebra.let("x", algebra.extend(value, from, signedSource))(x =>
          algebra.truncate(
            algebra.select(
              if (unsigned) algebra.literal(0L) else algebra.lessThan(x, algebra.literal(0L), signed = true),
              if (signedDestination) algebra.maximum(x, algebra.literal(-maximum - 1L), signed = true)
              else algebra.literal(0L),
              algebra.minimum(x, algebra.literal(maximum), signed = false)
            ),
            to
          )
        )
      )
    } else if (to <= from) sinks.unary(destination, source)(value => algebra.truncate(value, to))
    else if (signedSource)
      sinks.unary(destination, source)(value => algebra.truncate(algebra.signExtend(value, from), to))
    else sinks.unary(destination, source)(value => algebra.truncate(value, from))
  }

  /** Whether a cluster cancellation response reports success. */
  inline def cancelled[V, R](inline algebra: Algebra[V])(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      source: Value
  ): R =
    sinks.test(destination, source)(response =>
      algebra.notEqual(algebra.and(response, algebra.literal(1L)), algebra.literal(0L))
    )

  /** A cluster cancellation response's CTA coordinate on `axis`, from its low and high words. */
  inline def cancelledCoordinate[V, R](
      inline algebra: Algebra[V],
      axis: Axis
  )(inline sinks: NvidiaSinks[V, R], destination: Register, low: Value, high: Value): R =
    axis match {
      case Axis.X => sinks.cancelledCta(destination, low, high)((l, _) => algebra.truncate(l, 32))
      case Axis.Y =>
        sinks.cancelledCta(destination, low, high)((l, _) => algebra.shiftRightLogical(l, algebra.literal(32L)))
      case Axis.Z =>
        sinks.cancelledCta(destination, low, high)((_, h) => algebra.shiftRightLogical(h, algebra.literal(32L)))
    }

  /** `mov` of a pair of `part`-bit halves into one register. */
  inline def combine[V, R](
      inline algebra: Algebra[V],
      part: Int in 1 ~ 64
  )(inline sinks: NvidiaSinks[V, R], destination: Register, left: Value, right: Value): R =
    sinks.binary(destination, left, right)((low, high) =>
      algebra.or(
        algebra.truncate(low, part),
        algebra.shiftLeft(algebra.truncate(high, part), algebra.literal(part.toLong))
      )
    )

  /** `mov` of a register into its two `part`-bit halves. */
  inline def split[V, R](
      inline algebra: Algebra[V],
      part: Int in 1 ~ 64
  )(inline sinks: NvidiaSinks[V, R], low: Option[Register], high: Option[Register], source: Value): R =
    sinks.split(low, high, source)(
      value => algebra.truncate(value, part),
      value => algebra.truncate(algebra.shiftRightLogical(value, algebra.literal(part.toLong)), part)
    )

  /** IADD3 with a carry chain. Producing, a negated operand is its two's complement and the carry count is the sum's
    * bits above `width`; consuming, a negated operand is its ones' complement and the incoming count is added.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def addThreeCarry[V, R](
      inline algebra: Algebra[V],
      width: Int in 1 ~ 64,
      produce: Boolean,
      negateFirst: Boolean,
      negateSecond: Boolean,
      negateThird: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R = {
    val negations = (if (negateFirst) 1L else 0L) + (if (negateSecond) 1L else 0L) + (if (negateThird) 1L else 0L)
    if (!produce)
      sinks.consumeCarries(destination, first, second, third)((a, b, c, carry) =>
        algebra.truncate(
          algebra.add(
            algebra.threeSum(a, b, c, width, negateFirst, negateSecond, negateThird),
            carry
          ),
          width
        )
      )
    else if (width == 64)
      sinks.produceCarries(destination, first, second, third)(
        (a, b, c) =>
          algebra.add(
            algebra.threeSum(a, b, c, width, negateFirst, negateSecond, negateThird),
            algebra.literal(negations)
          ),
        (a, b, c) =>
          algebra.let("x", algebra.complemented(a, width, negateFirst))(x =>
            algebra.let("s1", algebra.add(x, algebra.complemented(b, width, negateSecond)))(partial =>
              algebra.let("s2", algebra.add(partial, algebra.complemented(c, width, negateThird)))(total =>
                algebra.add(
                  algebra.add(
                    algebra.lessThan(partial, x, signed = false),
                    algebra.lessThan(total, partial, signed = false)
                  ),
                  algebra.lessThan(algebra.add(total, algebra.literal(negations)), total, signed = false)
                )
              )
            )
          )
      )
    else
      sinks.produceCarries(destination, first, second, third)(
        (a, b, c) =>
          algebra.truncate(
            algebra.add(
              algebra.threeSum(a, b, c, width, negateFirst, negateSecond, negateThird),
              algebra.literal(negations)
            ),
            width
          ),
        (a, b, c) =>
          algebra.shiftRightLogical(
            algebra.add(
              algebra.threeSum(a, b, c, width, negateFirst, negateSecond, negateThird),
              algebra.literal(negations)
            ),
            algebra.literal(width.toLong)
          )
      )
  }

  // call sites name these arguments, which the argument checks enforce
  extension [V](inline algebra: Algebra[V]) {
    @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
    inline def threeSum(
        a: V,
        b: V,
        c: V,
        width: Int in 1 ~ 64,
        negateFirst: Boolean,
        negateSecond: Boolean,
        negateThird: Boolean
    ): V = algebra.add(
      algebra.add(algebra.complemented(a, width, negateFirst), algebra.complemented(b, width, negateSecond)),
      algebra.complemented(c, width, negateThird)
    )

    inline def complemented(value: V, width: Int in 1 ~ 64, negate: Boolean): V =
      algebra.truncate(algebra.complement(value, negate), width)
  }

  inline def binary[V, R](inline algebra: Algebra[V], operation: IntegerOperation, width: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      left: Value,
      right: Value
  ): R = operation match {
    case IntegerOperation.Add =>
      sinks.binary(destination, left, right)((l, r) => algebra.truncate(algebra.add(l, r), width))
    case IntegerOperation.Subtract =>
      sinks.binary(destination, left, right)((l, r) => algebra.truncate(algebra.subtract(l, r), width))
    case IntegerOperation.AddSaturated =>
      sinks.binary(destination, left, right)((l, r) =>
        algebra.truncate(algebra.addSaturated(l, r, width, signed = true), width)
      )
    case IntegerOperation.SubtractSaturated =>
      sinks.binary(destination, left, right)((l, r) =>
        algebra.truncate(algebra.subtractSaturated(l, r, width, signed = true), width)
      )
    case IntegerOperation.BitwiseOr =>
      sinks.binary(destination, left, right)((l, r) => algebra.truncate(algebra.or(l, r), width))
    case IntegerOperation.BitwiseAnd =>
      sinks.binary(destination, left, right)((l, r) => algebra.truncate(algebra.and(l, r), width))
    case IntegerOperation.BitwiseXor =>
      sinks.binary(destination, left, right)((l, r) => algebra.truncate(algebra.xor(l, r), width))
    case IntegerOperation.MultiplyLow =>
      sinks.binary(destination, left, right)((l, r) => algebra.truncate(algebra.multiply(l, r), width))
    case IntegerOperation.CarrylessMultiply(high) =>
      if (high)
        sinks.binary(destination, left, right)((l, r) => algebra.primitive(l, r)(IntegerArithmetic.carrylessHigh))
      else sinks.binary(destination, left, right)((l, r) => algebra.primitive(l, r)(IntegerArithmetic.carrylessLow))
    case IntegerOperation.MultiplyHigh(signed) =>
      if (signed) sinks.binary(destination, left, right)((l, r) =>
        algebra.truncate(algebra.multiplyHigh(l, r, width, signed = true), width)
      )
      else sinks.binary(destination, left, right)((l, r) =>
        algebra.truncate(algebra.multiplyHigh(l, r, width, signed = false), width)
      )
    case IntegerOperation.Divide(signed) =>
      if (signed) sinks.binary(destination, left, right)((l, r) =>
        algebra.quotient(l, r, width, signed = true, remainder = false)
      )
      else sinks.binary(destination, left, right)((l, r) =>
        algebra.quotient(l, r, width, signed = false, remainder = false)
      )
    case IntegerOperation.Remainder(signed) =>
      if (signed) sinks.binary(destination, left, right)((l, r) =>
        algebra.quotient(l, r, width, signed = true, remainder = true)
      )
      else sinks.binary(destination, left, right)((l, r) =>
        algebra.quotient(l, r, width, signed = false, remainder = true)
      )
    case IntegerOperation.MultiplyWideUnsigned16 =>
      sinks.binary(destination, left, right)((l, r) =>
        algebra.truncate(algebra.multiply(algebra.truncate(l, 16), algebra.truncate(r, 16)), width)
      )
    case IntegerOperation.MultiplyWideSigned16 =>
      sinks.binary(destination, left, right)((l, r) =>
        algebra.truncate(algebra.multiply(algebra.signExtend(l, 16), algebra.signExtend(r, 16)), width)
      )
    case IntegerOperation.MultiplyWideUnsigned32 =>
      sinks.binary(destination, left, right)((l, r) =>
        algebra.truncate(algebra.multiply(algebra.truncate(l, 32), algebra.truncate(r, 32)), width)
      )
    case IntegerOperation.MultiplyWideSigned32 =>
      sinks.binary(destination, left, right)((l, r) =>
        algebra.truncate(algebra.multiply(algebra.signExtend(l, 32), algebra.signExtend(r, 32)), width)
      )
    case IntegerOperation.Minimum(signed) =>
      if (signed)
        sinks.binary(destination, left, right)((l, r) =>
          algebra.extremum(l, r, width, signed = true, maximum = false)
        )
      else sinks.binary(destination, left, right)((l, r) =>
        algebra.extremum(l, r, width, signed = false, maximum = false)
      )
    case IntegerOperation.Maximum(signed) =>
      if (signed)
        sinks.binary(destination, left, right)((l, r) =>
          algebra.extremum(l, r, width, signed = true, maximum = true)
        )
      else
        sinks.binary(destination, left, right)((l, r) =>
          algebra.extremum(l, r, width, signed = false, maximum = true)
        )
    case IntegerOperation.ShiftLeft =>
      sinks.binary(destination, left, right)((l, r) =>
        algebra.shiftedLeft(l, algebra.shiftAmount(r), width, signed = true)
      )
    case IntegerOperation.ShiftRightLogical =>
      sinks.binary(destination, left, right)((l, r) =>
        algebra.shiftedRightLogical(l, algebra.shiftAmount(r), width, signed = true)
      )
    case IntegerOperation.ShiftRightArithmetic => sinks.binary(destination, left, right)((l, r) =>
        algebra.let("n", algebra.shiftAmount(r))(n =>
          algebra.truncate(
            algebra.shiftRightArithmetic(
              algebra.signExtend(l, width),
              algebra.minimum(n, algebra.literal(width - 1L), signed = true)
            ),
            width
          )
        )
      )
  }

  /** A three-input truth table indexed by `a << 2 | b << 1 | c`, as in `a = 0xf0`, `b = 0xcc`, `c = 0xaa`. */
  inline def logic3[V, R](inline algebra: Algebra[V], table: Int, width: Int in 1 ~ 64)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R = table match {
    case 0x80 => sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(algebra.and(algebra.and(a, b), c), width)
      )
    case 0xc0 =>
      sinks.ternary(destination, first, second, third)((a, b, _) => algebra.truncate(algebra.and(a, b), width))
    case 0xfc =>
      sinks.ternary(destination, first, second, third)((a, b, _) => algebra.truncate(algebra.or(a, b), width))
    case 0xfe => sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(algebra.or(algebra.or(a, b), c), width)
      )
    case 0x3c =>
      sinks.ternary(destination, first, second, third)((a, b, _) => algebra.truncate(algebra.xor(a, b), width))
    case 0x96 => sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(algebra.xor(algebra.xor(a, b), c), width)
      )
    case 0xea => sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(algebra.or(algebra.and(a, b), c), width)
      )
    case 0xf8 => sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(algebra.or(a, algebra.and(b, c)), width)
      )
    case 0x6a => sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(algebra.xor(algebra.and(a, b), c), width)
      )
    case 0x78 => sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(algebra.xor(a, algebra.and(b, c)), width)
      )
    case 0xe8 =>
      sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(algebra.or(algebra.and(a, b), algebra.and(c, algebra.or(a, b))), width)
      )
    case _ => sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.truncate(
          algebra.blend(
            a,
            algebra.blend(b, algebra.minterms(table, c, 6), algebra.minterms(table, c, 4)),
            algebra.blend(b, algebra.minterms(table, c, 2), algebra.minterms(table, c, 0))
          ),
          width
        )
      )
  }

  /** Table entries `low` and `low + 1`, selected by `c`. */
  extension [V](inline algebra: Algebra[V]) {
    inline def minterms(table: Int, c: V, low: Int): V =
      algebra.blend(
        c,
        algebra.literal(-((table >>> (low + 1)) & 1).toLong),
        algebra.literal(-((table >>> low) & 1).toLong)
      )
  }

  /** The high or low 32 bits of the pair `high:low` shifted by `amount`. The wrap form takes the shift modulo 32; the
    * clamp form saturates it at 32, so a clamped shift of 32 selects the opposite half rather than the input.
    */
  inline def funnel[V, R](inline algebra: Algebra[V], left: Boolean, clamp: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    if (left) {
      if (clamp) sinks.ternary(destination, first, second, third)((low, high, amount) =>
        algebra.funnelPair(low, high, amount, left = true, clamp = true)
      )
      else sinks.ternary(destination, first, second, third)((low, high, amount) =>
        algebra.funnelPair(low, high, amount, left = true, clamp = false)
      )
    } else if (clamp) sinks.ternary(destination, first, second, third)((low, high, amount) =>
      algebra.funnelPair(low, high, amount, left = false, clamp = true)
    )
    else sinks.ternary(destination, first, second, third)((low, high, amount) =>
      algebra.funnelPair(low, high, amount, left = false, clamp = false)
    )

  extension [V](inline algebra: Algebra[V]) {
    inline def funnelPair(
        low: V,
        high: V,
        amount: V,
        inline left: Boolean,
        inline clamp: Boolean
    ): V =
      algebra.let("pair", algebra.or(algebra.truncate(low, 32), algebra.shiftLeft(high, algebra.literal(32L))))(pair =>
        if (clamp)
          algebra.let("n", algebra.shiftAmount(amount))(n =>
            algebra.let("s", algebra.minimum(n, algebra.literal(32L), signed = true))(s =>
              algebra.funnelHalf(pair, s, left)
            )
          )
        else algebra.let("s", algebra.and(amount, algebra.literal(31L)))(s => algebra.funnelHalf(pair, s, left))
      )

    inline def funnelHalf(pair: V, shift: V, inline left: Boolean): V =
      if (left) algebra.truncate(algebra.shiftRightLogical(algebra.shiftLeft(pair, shift), algebra.literal(32L)), 32)
      else algebra.truncate(algebra.shiftRightLogical(pair, shift), 32)
  }

  /** Integer formats are never unordered, so each unordered comparison is its ordered form. A flushing comparison first
    * flushes each operand as a binary32 subnormal would be.
    */
  inline def integerComparison[V, R](
      inline algebra: Algebra[V],
      comparison: Comparison,
      signed: Boolean,
      width: Int in 1 ~ 64,
      flush: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Predicate, left: Value, right: Value): R =
    if (flush) {
      if (signed)
        integerRelation(algebra, comparison, signed = true, width, flush = true)(sinks, destination, left, right)
      else integerRelation(algebra, comparison, signed = false, width, flush = true)(sinks, destination, left, right)
    } else if (signed)
      integerRelation(algebra, comparison, signed = true, width, flush = false)(sinks, destination, left, right)
    else integerRelation(algebra, comparison, signed = false, width, flush = false)(sinks, destination, left, right)

  inline def integerRelation[V, R](
      inline algebra: Algebra[V],
      comparison: Comparison,
      inline signed: Boolean,
      width: Int in 1 ~ 64,
      inline flush: Boolean
  )(inline sinks: NvidiaSinks[V, R], destination: Predicate, left: Value, right: Value): R = comparison match {
    case Comparison.Ordered   => sinks.compare(destination, left, right)((_, _) => algebra.literal(1L))
    case Comparison.Unordered => sinks.compare(destination, left, right)((_, _) => algebra.literal(0L))
    case Comparison.Equal | Comparison.EqualUnordered => sinks.compare(destination, left, right)((l, r) =>
        algebra.equal(
          algebra.truncate(algebra.flushed(l, flush), width),
          algebra.truncate(algebra.flushed(r, flush), width)
        )
      )
    case Comparison.NotEqual | Comparison.NotEqualUnordered => sinks.compare(destination, left, right)((l, r) =>
        algebra.notEqual(
          algebra.truncate(algebra.flushed(l, flush), width),
          algebra.truncate(algebra.flushed(r, flush), width)
        )
      )
    case Comparison.LessThan | Comparison.LessThanUnordered => sinks.compare(destination, left, right)((l, r) =>
        algebra.lessThan(
          algebra.extend(algebra.flushed(l, flush), width, signed),
          algebra.extend(algebra.flushed(r, flush), width, signed),
          signed
        )
      )
    case Comparison.LessThanOrEqual | Comparison.LessThanOrEqualUnordered => sinks.compare(destination, left, right)(
        (l, r) =>
          algebra.lessOrEqual(
            algebra.extend(algebra.flushed(l, flush), width, signed),
            algebra.extend(algebra.flushed(r, flush), width, signed),
            signed
          )
      )
    case Comparison.GreaterThan | Comparison.GreaterThanUnordered => sinks.compare(destination, left, right)((l, r) =>
        algebra.lessThan(
          algebra.extend(algebra.flushed(r, flush), width, signed),
          algebra.extend(algebra.flushed(l, flush), width, signed),
          signed
        )
      )
    case Comparison.GreaterThanOrEqual | Comparison.GreaterThanOrEqualUnordered =>
      sinks.compare(destination, left, right)((l, r) =>
        algebra.lessOrEqual(
          algebra.extend(algebra.flushed(r, flush), width, signed),
          algebra.extend(algebra.flushed(l, flush), width, signed),
          signed
        )
      )
  }

  /** `value` with a binary32 subnormal's exponent and mantissa cleared when `flush`. */
  extension [V](inline algebra: Algebra[V]) {
    inline def flushed(value: V, inline flush: Boolean): V =
      if (flush)
        algebra.let("f", value)(bits =>
          algebra.select(
            algebra.equal(algebra.and(bits, algebra.literal(0x7f800000L)), algebra.literal(0L)),
            algebra.and(bits, algebra.literal(0x80000000L)),
            bits
          )
        )
      else value

    inline def shiftAmount(amount: V): V =
      algebra.and(amount, algebra.literal(0xffffffffL))
  }
}
