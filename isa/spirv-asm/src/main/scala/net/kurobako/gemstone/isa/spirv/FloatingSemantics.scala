package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.annotation.targetName

/** SPIR-V floating-point lane semantics on IEEE bits. Arithmetic rounds through the host's binary32 or binary64
  * operation for the width; `fmin` and `fmax` follow IEEE minNum and maxNum. Functions the host evaluates in binary64,
  * such as the transcendental ones, widen their operands exactly and round once to the width. Comparisons and
  * classification are bit tests, under which ordered values compare as sign-magnitude integers.
  */
object FloatingSemantics {

  inline def binary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.FAdd => arithmetic(algebra, sinks, width)(add(_, _), add(_, _), add(_, _))(ids)
    case SPIRV.Opcode.FSub =>
      arithmetic(algebra, sinks, width)(subtract(_, _), subtract(_, _), subtract(_, _))(ids)
    case SPIRV.Opcode.FMul =>
      arithmetic(algebra, sinks, width)(multiply(_, _), multiply(_, _), multiply(_, _))(ids)
    case SPIRV.Opcode.FDiv => arithmetic(algebra, sinks, width)(divide(_, _), divide(_, _), divide(_, _))(ids)
    case SPIRV.Opcode.FRem =>
      arithmetic(algebra, sinks, width)(remainder(_, _), remainder(_, _), remainder(_, _))(ids)
    case SPIRV.Opcode.FMod => arithmetic(algebra, sinks, width)(modulo(_, _), modulo(_, _), modulo(_, _))(ids)
    case _                 => otherwise
  }

  inline def unary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.FNegate => negate(algebra, sinks, width)(ids)
    case _                    => otherwise
  }

  inline def extremum[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline maximum: Boolean,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  ): R = width.raw match {
    case 16 =>
      if (maximum) sinks.binary(ids)((a, b) => algebra.primitive(a, b)(Float16.maxNum(_, _)))
      else sinks.binary(ids)((a, b) => algebra.primitive(a, b)(Float16.minNum(_, _)))
    case 32 =>
      if (maximum) sinks.binary(ids)((a, b) => algebra.primitive(a, b)(Float32.maxNum(_, _)))
      else sinks.binary(ids)((a, b) => algebra.primitive(a, b)(Float32.minNum(_, _)))
    case _ =>
      if (maximum) sinks.binary(ids)((a, b) => algebra.primitive(a, b)(Float64.maxNum(_, _)))
      else sinks.binary(ids)((a, b) => algebra.primitive(a, b)(Float64.minNum(_, _)))
  }

  /** IEEE minNum at `width`, inside a kernel that already chose its width. */
  extension [V](inline algebra: Algebra[V]) {
    inline def minimum(left: V, right: V, width: Int in (16, 32, 64)): V =
      width.raw match {
        case 16 => algebra.primitive(left, right)(Float16.minNum(_, _))
        case 32 => algebra.primitive(left, right)(Float32.minNum(_, _))
        case _  => algebra.primitive(left, right)(Float64.minNum(_, _))
      }

    inline def maximum(left: V, right: V, width: Int in (16, 32, 64)): V =
      width.raw match {
        case 16 => algebra.primitive(left, right)(Float16.maxNum(_, _))
        case 32 => algebra.primitive(left, right)(Float32.maxNum(_, _))
        case _  => algebra.primitive(left, right)(Float64.maxNum(_, _))
      }
  }

  /** fclamp: the operand raised to the lower bound, then lowered to the upper bound, each by minNum and maxNum. */
  inline def clamp[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => sinks.ternary(ids)((a, b, c) =>
        algebra.primitive(algebra.primitive(a, b)(Float16.maxNum(_, _)), c)(
          Float16.minNum(_, _)
        )
      )
    case 32 => sinks.ternary(ids)((a, b, c) =>
        algebra.primitive(algebra.primitive(a, b)(Float32.maxNum(_, _)), c)(
          Float32.minNum(_, _)
        )
      )
    case _ => sinks.ternary(ids)((a, b, c) =>
        algebra.primitive(algebra.primitive(a, b)(Float64.maxNum(_, _)), c)(
          Float64.minNum(_, _)
        )
      )
  }

  inline def negate[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64)
  )(ids: OperandIds): R =
    sinks.unary(ids)(a => algebra.xor(a, algebra.literal(IEEE754.signBit(width))))

  /** A matrix product, each lane the dot product of a row and a column, each operation rounded to the width. */
  inline def product[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 =>
      sinks.product(ids)((sum, a, b) =>
        algebra.primitive(sum, algebra.primitive(a, b)(multiply(_: Float16, _: Float16)))(add(_: Float16, _: Float16))
      )
    case 32 =>
      sinks.product(ids)((sum, a, b) =>
        algebra.primitive(sum, algebra.primitive(a, b)(multiply(_: Float32, _: Float32)))(add(_: Float32, _: Float32))
      )
    case _ =>
      sinks.product(ids)((sum, a, b) =>
        algebra.primitive(sum, algebra.primitive(a, b)(multiply(_: Float64, _: Float64)))(add(_: Float64, _: Float64))
      )
  }

  /** OpOuterProduct, each lane the rounded product of a lane of each operand. */
  inline def outerProduct[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => sinks.outerProduct(ids)((a, b) => algebra.primitive(a, b)(multiply(_: Float16, _: Float16)))
    case 32 => sinks.outerProduct(ids)((a, b) => algebra.primitive(a, b)(multiply(_: Float32, _: Float32)))
    case _  => sinks.outerProduct(ids)((a, b) => algebra.primitive(a, b)(multiply(_: Float64, _: Float64)))
  }

  /** OpDot: the sum of lane products from zero, each operation rounded to the width. */
  inline def dot[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => sinks.binaryFold(ids)(
        algebra.literal(0L),
        (sum, a, b) =>
          algebra.primitive(
            sum,
            algebra.primitive(a, b)(multiply(_: Float16, _: Float16))
          )(add(_: Float16, _: Float16)),
        sum => sum
      )
    case 32 => sinks.binaryFold(ids)(
        algebra.literal(0L),
        (sum, a, b) =>
          algebra.primitive(
            sum,
            algebra.primitive(a, b)(multiply(_: Float32, _: Float32))
          )(add(_: Float32, _: Float32)),
        sum => sum
      )
    case _ => sinks.binaryFold(ids)(
        algebra.literal(0L),
        (sum, a, b) =>
          algebra.primitive(
            sum,
            algebra.primitive(a, b)(multiply(_: Float64, _: Float64))
          )(add(_: Float64, _: Float64)),
        sum => sum
      )
  }

  /** An operation at `width` by the primitive for each width. */
  inline def arithmetic[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      inline half: (Float16, Float16) => Float16,
      inline single: (Float32, Float32) => Float32,
      inline double: (Float64, Float64) => Float64
  )(ids: OperandIds): R = width.raw match {
    case 16 => sinks.binary(ids)((a, b) => algebra.primitive(a, b)(half))
    case 32 => sinks.binary(ids)((a, b) => algebra.primitive(a, b)(single))
    case _  => sinks.binary(ids)((a, b) => algebra.primitive(a, b)(double))
  }

  /** FMod's remainder, which takes the sign of the divisor. */
  private def flooredRemainder(a: Float, b: Float): Float = {
    val remainder = a % b
    if (remainder != 0 && (remainder < 0) != (b < 0)) remainder + b else remainder
  }

  private def flooredRemainder(a: Double, b: Double): Double = {
    val remainder = a % b
    if (remainder != 0 && (remainder < 0) != (b < 0)) remainder + b else remainder
  }

  // binary16 arithmetic rounds through binary32
  @targetName("add16")
  def add(x: Float16, y: Float16): Float16 = Float16(x.toFloat + y.toFloat)
  @targetName("add32")
  def add(x: Float32, y: Float32): Float32 = Float32(x.toFloat + y.toFloat)
  @targetName("add64")
  def add(x: Float64, y: Float64): Float64 = Float64(x.toDouble + y.toDouble)
  @targetName("subtract16")
  def subtract(x: Float16, y: Float16): Float16 = Float16(x.toFloat - y.toFloat)
  @targetName("subtract32")
  def subtract(x: Float32, y: Float32): Float32 = Float32(x.toFloat - y.toFloat)
  @targetName("subtract64")
  def subtract(x: Float64, y: Float64): Float64 = Float64(x.toDouble - y.toDouble)
  @targetName("multiply16")
  def multiply(x: Float16, y: Float16): Float16 = Float16(x.toFloat * y.toFloat)
  @targetName("multiply32")
  def multiply(x: Float32, y: Float32): Float32 = Float32(x.toFloat * y.toFloat)
  @targetName("multiply64")
  def multiply(x: Float64, y: Float64): Float64 = Float64(x.toDouble * y.toDouble)
  @targetName("divide16")
  def divide(x: Float16, y: Float16): Float16 = Float16(x.toFloat / y.toFloat)
  @targetName("divide32")
  def divide(x: Float32, y: Float32): Float32 = Float32(x.toFloat / y.toFloat)
  @targetName("divide64")
  def divide(x: Float64, y: Float64): Float64 = Float64(x.toDouble / y.toDouble)
  @targetName("remainder16")
  def remainder(x: Float16, y: Float16): Float16 = Float16(x.toFloat % y.toFloat)
  @targetName("remainder32")
  def remainder(x: Float32, y: Float32): Float32 = Float32(x.toFloat % y.toFloat)
  @targetName("remainder64")
  def remainder(x: Float64, y: Float64): Float64 = Float64(x.toDouble % y.toDouble)
  @targetName("modulo16")
  def modulo(x: Float16, y: Float16): Float16 = Float16(flooredRemainder(x.toFloat, y.toFloat))
  @targetName("modulo32")
  def modulo(x: Float32, y: Float32): Float32 = Float32(flooredRemainder(x.toFloat, y.toFloat))
  @targetName("modulo64")
  def modulo(x: Float64, y: Float64): Float64 = Float64(flooredRemainder(x.toDouble, y.toDouble))

  /** The next value at `width` after `x` toward `y`. */
  @targetName("nextAfter16")
  def nextAfter(x: Float16, y: Float16): Float16 =
    FloatingPoint[16](DoubleMath.nextAfterBinary16(x.bits.raw, y.bits.raw))
  @targetName("nextAfter32")
  def nextAfter(x: Float32, y: Float32): Float32 = Float32(Math.nextAfter(x.toFloat, y.toDouble))
  @targetName("nextAfter64")
  def nextAfter(x: Float64, y: Float64): Float64 = Float64(Math.nextAfter(x.toDouble, y.toDouble))

  /** `function` of a binary64 operand at `width`: widened exactly, evaluated, and rounded once to the width. */
  inline def unaryDouble[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      inline function: Float64 => Float64
  )(ids: OperandIds): R = width.raw match {
    case 16 => unaryAt(algebra, sinks, 16)(function)(ids)
    case 32 => unaryAt(algebra, sinks, 32)(function)(ids)
    case _  => sinks.unary(ids)(a => algebra.primitive(a)(function))
  }

  private inline def unaryAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      inline function: Float64 => Float64
  )(ids: OperandIds): R =
    sinks.unary(ids)(a => algebra.rounded(algebra.primitive(algebra.widened(a, bits))(function), bits))

  inline def binaryDouble[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      inline function: (Float64, Float64) => Float64
  )(ids: OperandIds): R = width.raw match {
    case 16 => binaryAt(algebra, sinks, 16)(function)(ids)
    case 32 => binaryAt(algebra, sinks, 32)(function)(ids)
    case _  => sinks.binary(ids)((a, b) => algebra.primitive(a, b)(function))
  }

  private inline def binaryAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      inline function: (Float64, Float64) => Float64
  )(ids: OperandIds): R = sinks.binary(ids)((a, b) =>
    algebra.rounded(algebra.primitive(algebra.widened(a, bits), algebra.widened(b, bits))(function), bits)
  )

  inline def ternaryDouble[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64)
  )(
      inline function: (Float64, Float64, Float64) => Float64
  )(ids: OperandIds): R = width.raw match {
    case 16 => ternaryAt(algebra, sinks, 16)(function)(ids)
    case 32 => ternaryAt(algebra, sinks, 32)(function)(ids)
    case _  => sinks.ternary(ids)((a, b, c) => algebra.primitive(a, b, c)(function))
  }

  private inline def ternaryAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      inline function: (Float64, Float64, Float64) => Float64
  )(ids: OperandIds): R = sinks.ternary(ids)((a, b, c) =>
    algebra.rounded(
      algebra.primitive(algebra.widened(a, bits), algebra.widened(b, bits), algebra.widened(c, bits))(function),
      bits
    )
  )

  /** A value at `width` as binary64, exactly; `width` must be a literal so the kernel needs no branch. */
  extension [V](inline algebra: Algebra[V]) {
    inline def widened(value: V, inline width: Int): V = inline width match {
      case 16 => algebra.primitive(value)(Elementary.widen16(_))
      case 32 => algebra.primitive(value)(Elementary.widen32(_))
      case _  => value
    }

    /** A binary64 value rounded to nearest at `width`, which must be a literal. */
    inline def rounded(value: V, inline width: Int): V = inline width match {
      case 16 => algebra.primitive(value)(Elementary.narrow16(_))
      case 32 => algebra.primitive(value)(Elementary.narrow32(_))
      case _  => value
    }

    inline def magnitude(value: V, inline sign: Long): V =
      algebra.and(value, algebra.literal(sign - 1L))

    /** The signed integer order of a value that is not a NaN, in which both zeros are 0. */
    inline def order(value: V, inline sign: Long): V =
      algebra.let("m", algebra.magnitude(value, sign))(m =>
        algebra.select(
          algebra.notEqual(algebra.and(value, algebra.literal(sign)), algebra.literal(0L)),
          algebra.subtract(algebra.literal(0L), m),
          m
        )
      )

    /** `relation` between the orders of the operands, failing when either is a NaN. */
    inline def ordered(left: V, right: V, inline sign: Long, inline infinity: Long)(
        inline relation: (V, V) => V
    ): V = algebra.and(
      algebra.equal(algebra.unordered(left, right, sign, infinity), algebra.literal(0L)),
      relation(algebra.order(left, sign), algebra.order(right, sign))
    )

    /** `relation` between the orders of the operands, holding when either is a NaN. */
    inline def unorderedOr(left: V, right: V, inline sign: Long, inline infinity: Long)(
        inline relation: (V, V) => V
    ): V = algebra.or(
      algebra.unordered(left, right, sign, infinity),
      relation(algebra.order(left, sign), algebra.order(right, sign))
    )
  }

  /** Ordered IEEE comparisons by opcode, as 1 or 0, failing when either operand is a NaN. */
  inline def orderedComparison[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = width.raw match {
    case 16 => orderedComparisonAt(algebra, sinks, opcode, sign = 0x8000L, infinity = 0x7c00L)(ids)(otherwise)
    case 32 => orderedComparisonAt(algebra, sinks, opcode, sign = 0x80000000L, infinity = 0x7f800000L)(ids)(otherwise)
    case _  =>
      orderedComparisonAt(algebra, sinks, opcode, sign = Long.MinValue, infinity = 0x7ff0000000000000L)(ids)(otherwise)
  }

  inline def orderedComparisonAt[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      inline sign: Long,
      inline infinity: Long
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.FOrdEqual =>
      sinks.binary(ids)((a, b) => algebra.ordered(a, b, sign, infinity)(algebra.equal(_, _)))
    case SPIRV.Opcode.FOrdNotEqual | SPIRV.Opcode.LessOrGreater =>
      sinks.binary(ids)((a, b) => algebra.ordered(a, b, sign, infinity)(algebra.notEqual(_, _)))
    case SPIRV.Opcode.FOrdLessThan =>
      sinks.binary(ids)((a, b) => algebra.ordered(a, b, sign, infinity)(algebra.lessThan(_, _, signed = true)))
    case SPIRV.Opcode.FOrdGreaterThan =>
      sinks.binary(ids)((a, b) =>
        algebra.ordered(a, b, sign, infinity)((x, y) => algebra.lessThan(y, x, signed = true))
      )
    case SPIRV.Opcode.FOrdLessThanEqual =>
      sinks.binary(ids)((a, b) => algebra.ordered(a, b, sign, infinity)(algebra.lessOrEqual(_, _, signed = true)))
    case SPIRV.Opcode.FOrdGreaterThanEqual =>
      sinks.binary(ids)((a, b) =>
        algebra.ordered(a, b, sign, infinity)((x, y) => algebra.lessOrEqual(y, x, signed = true))
      )
    case SPIRV.Opcode.Ordered =>
      sinks.binary(ids)((a, b) => algebra.equal(algebra.unordered(a, b, sign, infinity), algebra.literal(0L)))
    case _ => otherwise
  }

  /** Unordered IEEE comparisons by opcode, as 1 or 0, holding when either operand is a NaN. */
  inline def unorderedComparison[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = width.raw match {
    case 16 => unorderedComparisonAt(algebra, sinks, opcode, sign = 0x8000L, infinity = 0x7c00L)(ids)(otherwise)
    case 32 => unorderedComparisonAt(algebra, sinks, opcode, sign = 0x80000000L, infinity = 0x7f800000L)(ids)(otherwise)
    case _  => unorderedComparisonAt(
        algebra,
        sinks,
        opcode,
        sign = Long.MinValue,
        infinity = 0x7ff0000000000000L
      )(ids)(otherwise)
  }

  inline def unorderedComparisonAt[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      inline sign: Long,
      inline infinity: Long
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.FUnordEqual =>
      sinks.binary(ids)((a, b) => algebra.unorderedOr(a, b, sign, infinity)(algebra.equal(_, _)))
    case SPIRV.Opcode.FUnordNotEqual =>
      sinks.binary(ids)((a, b) => algebra.unorderedOr(a, b, sign, infinity)(algebra.notEqual(_, _)))
    case SPIRV.Opcode.FUnordLessThan =>
      sinks.binary(ids)((a, b) => algebra.unorderedOr(a, b, sign, infinity)(algebra.lessThan(_, _, signed = true)))
    case SPIRV.Opcode.FUnordGreaterThan =>
      sinks.binary(ids)((a, b) =>
        algebra.unorderedOr(a, b, sign, infinity)((x, y) => algebra.lessThan(y, x, signed = true))
      )
    case SPIRV.Opcode.FUnordLessThanEqual =>
      sinks.binary(ids)((a, b) => algebra.unorderedOr(a, b, sign, infinity)(algebra.lessOrEqual(_, _, signed = true)))
    case SPIRV.Opcode.FUnordGreaterThanEqual =>
      sinks.binary(ids)((a, b) =>
        algebra.unorderedOr(a, b, sign, infinity)((x, y) => algebra.lessOrEqual(y, x, signed = true))
      )
    case SPIRV.Opcode.Unordered => sinks.binary(ids)((a, b) => algebra.unordered(a, b, sign, infinity))
    case _                      => otherwise
  }

  /** IEEE classification by opcode, as 1 or 0. */
  inline def classify[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = width.raw match {
    case 16 => classifyAt(algebra, sinks, opcode, sign = 0x8000L, infinity = 0x7c00L)(ids)(otherwise)
    case 32 => classifyAt(algebra, sinks, opcode, sign = 0x80000000L, infinity = 0x7f800000L)(ids)(otherwise)
    case _  => classifyAt(algebra, sinks, opcode, sign = Long.MinValue, infinity = 0x7ff0000000000000L)(ids)(otherwise)
  }

  inline def classifyAt[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      inline sign: Long,
      inline infinity: Long
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case SPIRV.Opcode.IsNan => sinks.unary(ids)(a => algebra.isNaN(a, sign, infinity))
    case SPIRV.Opcode.IsInf =>
      sinks.unary(ids)(a => algebra.equal(algebra.magnitude(a, sign), algebra.literal(infinity)))
    case SPIRV.Opcode.IsFinite =>
      sinks.unary(ids)(a => algebra.lessThan(algebra.magnitude(a, sign), algebra.literal(infinity), signed = false))
    case SPIRV.Opcode.IsNormal => sinks.unary(ids)(a =>
        algebra.let("e", algebra.and(a, algebra.literal(infinity)))(e =>
          algebra.and(algebra.notEqual(e, algebra.literal(infinity)), algebra.notEqual(e, algebra.literal(0L)))
        )
      )
    case SPIRV.Opcode.SignBitSet =>
      sinks.unary(ids)(a => algebra.notEqual(algebra.and(a, algebra.literal(sign)), algebra.literal(0L)))
    case _ => otherwise
  }
}
