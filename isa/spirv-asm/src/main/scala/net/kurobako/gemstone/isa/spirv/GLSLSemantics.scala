package net.kurobako.gemstone.isa.spirv

import ExtendedInstruction.GlslStd450 as Glsl
import ExtendedInstruction.OpenClStd as OpenCL
import FloatingSemantics.*
import OpenCLSemantics.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** GLSL.std.450 extended instruction semantics by extended opcode. Integer instructions choose their signedness
  * independently of the operand types; floating-point functions the host evaluates in binary64 are named primitives
  * rounded once to the width.
  */
object GLSLSemantics {

  inline def integerUnary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case Glsl.SAbs  => OpenCLSemantics.integerUnary(algebra, sinks, OpenCL.SAbs, width)(ids)(otherwise)
    case Glsl.SSign => sinks.unary(ids)(a =>
        algebra.let("x", algebra.signExtend(a, width))(x =>
          algebra.truncate(
            algebra.select(
              algebra.lessThan(x, algebra.literal(0L), signed = true),
              algebra.literal(-1L),
              algebra.lessThan(algebra.literal(0L), x, signed = true)
            ),
            width
          )
        )
      )
    case Glsl.FindILsb => sinks.unary(ids)(a => algebra.findLeastSignificant(a, width, Bits.mask(width)))
    case Glsl.FindUMsb => sinks.unary(ids)(a =>
        algebra.findMostSignificant(a, width, signed = false, fromTop = false, Bits.mask(width))
      )
    case Glsl.FindSMsb => sinks.unary(ids)(a =>
        algebra.findMostSignificant(a, width, signed = true, fromTop = false, Bits.mask(width))
      )
    case _ => otherwise
  }

  inline def integerBinary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case Glsl.UMin => sinks.binary(ids)((a, b) =>
        algebra.truncate(IntegerSemantics.minimum(algebra, a, b, width, signed = false), width)
      )
    case Glsl.SMin => sinks.binary(ids)((a, b) =>
        algebra.truncate(IntegerSemantics.minimum(algebra, a, b, width, signed = true), width)
      )
    case Glsl.UMax => sinks.binary(ids)((a, b) =>
        algebra.truncate(IntegerSemantics.maximum(algebra, a, b, width, signed = false), width)
      )
    case Glsl.SMax => sinks.binary(ids)((a, b) =>
        algebra.truncate(IntegerSemantics.maximum(algebra, a, b, width, signed = true), width)
      )
    case _ => otherwise
  }

  inline def integerTernary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case Glsl.UClamp => sinks.ternary(ids)((a, b, c) => algebra.clamp(a, b, c, width, signed = false))
    case Glsl.SClamp => sinks.ternary(ids)((a, b, c) => algebra.clamp(a, b, c, width, signed = true))
    case _           => otherwise
  }

  /** Trigonometric and hyperbolic functions, and angle conversions. */
  inline def trigonometric[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case Glsl.Radians => unaryDouble(algebra, sinks, width)(Elementary.radians(_))(ids)
    case Glsl.Degrees => unaryDouble(algebra, sinks, width)(Elementary.degrees(_))(ids)
    case Glsl.Sin     => unaryDouble(algebra, sinks, width)(Elementary.sin(_))(ids)
    case Glsl.Cos     => unaryDouble(algebra, sinks, width)(Elementary.cos(_))(ids)
    case Glsl.Tan     => unaryDouble(algebra, sinks, width)(Elementary.tan(_))(ids)
    case Glsl.Asin    => unaryDouble(algebra, sinks, width)(Elementary.asin(_))(ids)
    case Glsl.Acos    => unaryDouble(algebra, sinks, width)(Elementary.acos(_))(ids)
    case Glsl.Atan    => unaryDouble(algebra, sinks, width)(Elementary.atan(_))(ids)
    case Glsl.Sinh    => unaryDouble(algebra, sinks, width)(Elementary.glslSinh(_, width))(ids)
    case Glsl.Cosh    => unaryDouble(algebra, sinks, width)(Elementary.glslCosh(_, width))(ids)
    case Glsl.Tanh    => unaryDouble(algebra, sinks, width)(Elementary.tanh(_))(ids)
    case Glsl.Asinh   => unaryDouble(algebra, sinks, width)(Elementary.asinh(_))(ids)
    case Glsl.Acosh   => unaryDouble(algebra, sinks, width)(Elementary.acosh(_))(ids)
    case Glsl.Atanh   => unaryDouble(algebra, sinks, width)(Elementary.atanh(_))(ids)
    case _            => otherwise
  }

  /** Magnitude, sign, rounding, exponentials, logarithms and roots. */
  inline def elementary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case Glsl.FAbs        => sinks.unary(ids)(a => algebra.magnitude(a, IEEE754.signBit(width)))
    case Glsl.Round       => unaryDouble(algebra, sinks, width)(Elementary.rint(_))(ids)
    case Glsl.RoundEven   => unaryDouble(algebra, sinks, width)(Elementary.rint(_))(ids)
    case Glsl.Trunc       => unaryDouble(algebra, sinks, width)(Elementary.trunc(_))(ids)
    case Glsl.FSign       => unaryDouble(algebra, sinks, width)(Elementary.sign(_))(ids)
    case Glsl.Floor       => unaryDouble(algebra, sinks, width)(Elementary.floor(_))(ids)
    case Glsl.Ceil        => unaryDouble(algebra, sinks, width)(Elementary.ceil(_))(ids)
    case Glsl.Fract       => unaryDouble(algebra, sinks, width)(Elementary.glslFract(_))(ids)
    case Glsl.Exp         => unaryDouble(algebra, sinks, width)(Elementary.exp(_))(ids)
    case Glsl.Log         => unaryDouble(algebra, sinks, width)(Elementary.log(_))(ids)
    case Glsl.Exp2        => unaryDouble(algebra, sinks, width)(Elementary.exp2(_))(ids)
    case Glsl.Log2        => unaryDouble(algebra, sinks, width)(Elementary.log2(_))(ids)
    case Glsl.Sqrt        => unaryDouble(algebra, sinks, width)(Elementary.sqrt(_))(ids)
    case Glsl.InverseSqrt => unaryDouble(algebra, sinks, width)(Elementary.rsqrt(_))(ids)
    case _                => otherwise
  }

  // FMin and FMax permit the same numeric operand preference for NaNs as NMin and NMax
  inline def floatingBinary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case Glsl.FMin | Glsl.NMin => extremum(algebra, sinks, maximum = false, width)(ids)
    case Glsl.FMax | Glsl.NMax => extremum(algebra, sinks, maximum = true, width)(ids)
    case Glsl.Atan2            => binaryDouble(algebra, sinks, width)(Elementary.atan2(_, _))(ids)
    case Glsl.Pow              => binaryDouble(algebra, sinks, width)(Elementary.pow(_, _))(ids)
    case Glsl.Step             => binaryDouble(algebra, sinks, width)(Elementary.step(_, _))(ids)
    case _                     => otherwise
  }

  inline def floatingTernary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case Glsl.FClamp | Glsl.NClamp => FloatingSemantics.clamp(algebra, sinks, width)(ids)
    case Glsl.FMix                 => ternaryDouble(algebra, sinks, width)(Elementary.glslMix(_, _, _))(ids)
    case Glsl.SmoothStep           => ternaryDouble(algebra, sinks, width)(Elementary.glslSmoothStep(_, _, _))(ids)
    case Glsl.Fma                  => OpenCLSemantics.fusedMultiplyAdd(algebra, sinks, width)(ids)
    case _                         => otherwise
  }

  /** Modf and Frexp, with their struct forms, as the fractional part or significand and the integral part or exponent,
    * the exponent as an `integerWidth`-bit integer.
    */
  inline def decompose[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64),
      integerWidth: Int in 1 ~ 64
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case Glsl.Modf        => OpenCLSemantics.modf(algebra, sinks, width, structure = false)(ids)
    case Glsl.ModfStruct  => OpenCLSemantics.modf(algebra, sinks, width, structure = true)(ids)
    case Glsl.Frexp       => OpenCLSemantics.frexp(algebra, sinks, width, integerWidth, structure = false)(ids)
    case Glsl.FrexpStruct => OpenCLSemantics.frexp(algebra, sinks, width, integerWidth, structure = true)(ids)
    case _                => otherwise
  }

  /** Length is OpenCL's; Distance rounds each difference to the width before accumulating it. */
  inline def distance[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => sinks.binaryFold(ids)(
        algebra.literal(0L),
        (sum, a, b) =>
          algebra.primitive(
            sum,
            algebra.widened(algebra.primitive(a, b)(subtract(_: Float16, _: Float16)), 16)
          )(Elementary.norm(_, _)),
        algebra.rounded(_, 16)
      )
    case 32 => sinks.binaryFold(ids)(
        algebra.literal(0L),
        (sum, a, b) =>
          algebra.primitive(
            sum,
            algebra.widened(algebra.primitive(a, b)(subtract(_: Float32, _: Float32)), 32)
          )(Elementary.norm(_, _)),
        algebra.rounded(_, 32)
      )
    case _ => sinks.binaryFold(ids)(
        algebra.literal(0L),
        (sum, a, b) =>
          algebra.primitive(sum, algebra.primitive(a, b)(subtract(_: Float64, _: Float64)))(Elementary.norm(_, _)),
        sum => sum
      )
  }

  /** Cross: each lane the difference of the products of two other lanes, evaluated in binary64 and rounded once to the
    * width.
    */
  inline def cross[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => crossAt(algebra, sinks, 16)(ids)
    case 32 => crossAt(algebra, sinks, 32)(ids)
    case _  => sinks.cross(ids)((a, b, c, d) => algebra.primitive(a, b, c, d)(Elementary.cross(_, _, _, _)))
  }

  private inline def crossAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      ids: OperandIds
  ): R = sinks.cross(ids)((a, b, c, d) =>
    algebra.rounded(
      algebra.primitive(
        algebra.widened(a, bits),
        algebra.widened(b, bits),
        algebra.widened(c, bits),
        algebra.widened(d, bits)
      )(Elementary.cross(_, _, _, _)),
      bits
    )
  )

  /** FaceForward, Reflect and Refract: a dot product of two operands, accumulated unfused in binary64 from -0, the
    * identity of addition, then each lane evaluated from it in binary64 and rounded once to the width.
    */
  inline def projected[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case Glsl.FaceForward =>
      project(
        algebra,
        sinks,
        width,
        dotted = 2
      )((dot, n, _, _) => algebra.primitive(dot, n)(Elementary.faceForward(_, _)))(ids)
    case Glsl.Reflect =>
      project(
        algebra,
        sinks,
        width,
        dotted = 0
      )((dot, i, n, _) => algebra.primitive(dot, i, n)(Elementary.reflect(_, _, _)))(ids)
    case Glsl.Refract =>
      project(algebra, sinks, width, dotted = 0)((dot, i, n, eta) =>
        algebra.primitive(dot, i, n, eta)(Elementary.refract(_, _, _, _))
      )(ids)
    case _ => otherwise
  }

  /** The binary64 bits of -0. */
  inline val NegativeZero = 0x8000000000000000L

  /** A projection whose element sees the dot product and the operand lanes in binary64. */
  inline def project[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      dotted: Int
  )(
      inline element: (V, V, V, V) => V
  )(ids: OperandIds): R = width.raw match {
    case 16 => projectAt(algebra, sinks, 16, dotted)(element)(ids)
    case 32 => projectAt(algebra, sinks, 32, dotted)(element)(ids)
    case _  => sinks.projection(ids, dotted)(
        algebra.literal(NegativeZero),
        (sum, a, b) => algebra.primitive(sum, a, b)(Elementary.dot(_, _, _)),
        element
      )
  }

  private inline def projectAt[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline bits: Int,
      dotted: Int
  )(
      inline element: (V, V, V, V) => V
  )(ids: OperandIds): R = sinks.projection(ids, dotted)(
    algebra.literal(NegativeZero),
    (sum, a, b) =>
      algebra.primitive(sum, algebra.widened(a, bits), algebra.widened(b, bits))(Elementary.dot(_, _, _)),
    (dot, a, b, c) =>
      algebra.rounded(
        element(dot, algebra.widened(a, bits), algebra.widened(b, bits), algebra.widened(c, bits)),
        bits
      )
  )

  /** Packing: each lane converts to a field that shifts in from the top, so lane 0 ends in the lowest bits. */
  inline def pack[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int)(ids: OperandIds)(
      otherwise: => R
  ): R = opcode match {
    case Glsl.PackSnorm4x8  => packNormalised(algebra, sinks, signed = true, bits = 8)(ids)
    case Glsl.PackUnorm4x8  => packNormalised(algebra, sinks, signed = false, bits = 8)(ids)
    case Glsl.PackSnorm2x16 => packNormalised(algebra, sinks, signed = true, bits = 16)(ids)
    case Glsl.PackUnorm2x16 => packNormalised(algebra, sinks, signed = false, bits = 16)(ids)
    case Glsl.PackHalf2x16  => sinks.fold(ids)(
        algebra.literal(0L),
        (packed, a) =>
          shiftIn(
            algebra,
            packed,
            algebra.rounded(algebra.widened(a, 32), 16),
            bits = 16,
            total = 32
          ),
        packed => packed
      )
    case Glsl.PackDouble2x32 =>
      sinks.fold(ids)(algebra.literal(0L), (packed, a) => shiftIn(algebra, packed, a, bits = 32, total = 64), p => p)
    case _ => otherwise
  }

  /** Unpacking: result lane `lane` converts the field of the packed operand at that position. */
  inline def unpack[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], opcode: Int)(ids: OperandIds)(
      otherwise: => R
  ): R = opcode match {
    case Glsl.UnpackSnorm4x8  => unpackNormalised(algebra, sinks, signed = true, bits = 8)(ids)
    case Glsl.UnpackUnorm4x8  => unpackNormalised(algebra, sinks, signed = false, bits = 8)(ids)
    case Glsl.UnpackSnorm2x16 => unpackNormalised(algebra, sinks, signed = true, bits = 16)(ids)
    case Glsl.UnpackUnorm2x16 => unpackNormalised(algebra, sinks, signed = false, bits = 16)(ids)
    case Glsl.UnpackHalf2x16  => sinks.indexed(ids)((word, lane) =>
        algebra.rounded(algebra.widened(field(algebra, word, lane, 16), 16), 32)
      )
    case Glsl.UnpackDouble2x32 => sinks.indexed(ids)((word, lane) => field(algebra, word, lane, 32))
    case _                     => otherwise
  }

  private inline def packNormalised[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline signed: Boolean,
      inline bits: Int
  )(ids: OperandIds): R = sinks.fold(ids)(
    algebra.literal(0L),
    (packed, a) =>
      shiftIn(
        algebra,
        packed,
        algebra.primitive(a)((x: Float32) => normalise(x, signed = signed, bits = bits)),
        bits = bits,
        total = 32
      ),
    packed => packed
  )

  private inline def unpackNormalised[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline signed: Boolean,
      inline bits: Int
  )(ids: OperandIds): R = sinks.indexed(ids)((word, lane) =>
    algebra.primitive(field(algebra, word, lane, bits))((x: UInt[32]) => denormalise(x, signed = signed, bits = bits))
  )

  inline def shiftIn[V](inline algebra: Algebra[V], packed: V, field: V, inline bits: Int, inline total: Int): V =
    algebra.or(
      algebra.shiftRightLogical(packed, algebra.literal(bits.toLong)),
      algebra.shiftLeft(field, algebra.literal((total - bits).toLong))
    )

  inline def field[V](inline algebra: Algebra[V], word: V, lane: V, inline bits: Int): V = algebra.and(
    algebra.shiftRightLogical(word, algebra.multiply(lane, algebra.literal(bits.toLong))),
    algebra.literal((1L << bits) - 1L)
  )

  /** A binary32 value clamped to [-1, 1] or [0, 1] and scaled to a `bits`-bit normalised integer field. */
  def normalise(x: Float32, signed: Boolean, bits: Int): UInt[32] = {
    val scale = if (signed) (1 << (bits - 1)) - 1 else (1 << bits) - 1
    val value = x.toDouble
    UInt[32](Math.round(Math.max(if (signed) -1.0 else 0.0, Math.min(1.0, value)) * scale) & ((1L << bits) - 1))
  }

  /** The binary32 encoding of a `bits`-bit normalised integer field. */
  def denormalise(field: UInt[32], signed: Boolean, bits: Int): Float32 = {
    val value =
      if (signed) Normalised.decodeSigned(field.raw, bits.unsafe[Int in 2 ~ 32])
      else Normalised.decodeUnsigned(field.raw, bits.unsafe[Int in 1 ~ 32])
    Float32(value)
  }
}
