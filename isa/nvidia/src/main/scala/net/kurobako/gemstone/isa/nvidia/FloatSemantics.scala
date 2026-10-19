package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** NVIDIA floating-point semantics on register bits. IEEE arithmetic and the format's rounding are primitives of
  * [[FloatArithmetic]]; the subnormal flush, saturation and NaN canonicalisation around them apply only where the
  * instruction asks for them. Each instruction's width selects its kernel once, so a lane computes at one width; packed
  * formats apply the scalar semantics to each element.
  */
private[isa] object FloatSemantics {

  inline def binary[V, R](
      inline algebra: Algebra[V],
      operation: FloatOperation,
      width: Int in (16, 32, 64),
      control: FloatControl
  )(inline sinks: NvidiaSinks[V, R], destination: Register, left: Value, right: Value): R = width.raw match {
    case 16 => binaryAt(algebra, operation, 16, control)(sinks, destination, left, right)
    case 32 => binaryAt(algebra, operation, 32, control)(sinks, destination, left, right)
    case _  => binaryAt(algebra, operation, 64, control)(sinks, destination, left, right)
  }

  inline def binaryAt[V, R](
      inline algebra: Algebra[V],
      operation: FloatOperation,
      inline width: Int,
      control: FloatControl
  )(inline sinks: NvidiaSinks[V, R], destination: Register, left: Value, right: Value): R = operation match {
    case FloatOperation.Add      => sinks.binary(destination, left, right)(add(algebra, width, control, _, _))
    case FloatOperation.Subtract => sinks.binary(destination, left, right)(subtract(algebra, width, control, _, _))
    case FloatOperation.Multiply => sinks.binary(destination, left, right)(multiply(algebra, width, control, _, _))
    case FloatOperation.Divide   => sinks.binary(destination, left, right)(divide(algebra, width, control, _, _))
    case FloatOperation.CopySign => sinks.binary(destination, left, right)(copySign(algebra, width, control, _, _))
    case FloatOperation.Minimum | FloatOperation.MinimumNumber =>
      val propagate = control.propagateNaN || operation == FloatOperation.Minimum
      sinks.binary(destination, left, right)(extremum(algebra, width, control, maximum = false, propagate, _, _))
    case FloatOperation.Maximum | FloatOperation.MaximumNumber =>
      val propagate = control.propagateNaN || operation == FloatOperation.Maximum
      sinks.binary(destination, left, right)(extremum(algebra, width, control, maximum = true, propagate, _, _))
  }

  inline def add[V](inline algebra: Algebra[V], inline width: Int, control: FloatControl, left: V, right: V): V =
    result(
      algebra,
      width,
      control,
      canonical(
        algebra,
        width,
        algebra.floating(width, input(algebra, width, control, left), input(algebra, width, control, right))(
          FloatArithmetic.add(_, _),
          FloatArithmetic.add(_, _),
          FloatArithmetic.add(_, _)
        )
      )
    )

  inline def subtract[V](inline algebra: Algebra[V], inline width: Int, control: FloatControl, left: V, right: V): V =
    result(
      algebra,
      width,
      control,
      canonical(
        algebra,
        width,
        algebra.floating(
          width,
          input(algebra, width, control, left),
          algebra.xor(input(algebra, width, control, right), algebra.literal(1L << (width - 1)))
        )(FloatArithmetic.add(_, _), FloatArithmetic.add(_, _), FloatArithmetic.add(_, _))
      )
    )

  inline def multiply[V](inline algebra: Algebra[V], inline width: Int, control: FloatControl, left: V, right: V): V =
    result(
      algebra,
      width,
      control,
      canonical(
        algebra,
        width,
        algebra.floating(width, input(algebra, width, control, left), input(algebra, width, control, right))(
          FloatArithmetic.multiply(_, _),
          FloatArithmetic.multiply(_, _),
          FloatArithmetic.multiply(_, _)
        )
      )
    )

  inline def divide[V](inline algebra: Algebra[V], inline width: Int, control: FloatControl, left: V, right: V): V =
    result(
      algebra,
      width,
      control,
      canonical(
        algebra,
        width,
        algebra.floating(width, input(algebra, width, control, left), input(algebra, width, control, right))(
          FloatArithmetic.divide(_, _),
          FloatArithmetic.divide(_, _),
          FloatArithmetic.divide(_, _)
        )
      )
    )

  inline def copySign[V](inline algebra: Algebra[V], inline width: Int, control: FloatControl, left: V, right: V): V = {
    val sign = 1L << (width - 1)
    result(
      algebra,
      width,
      control,
      bits(
        algebra,
        width,
        algebra.or(
          algebra.and(input(algebra, width, control, right), algebra.literal(~sign)),
          algebra.and(input(algebra, width, control, left), algebra.literal(sign))
        )
      )
    )
  }

  /** The lesser or, when `maximum`, the greater operand; a NaN operand wins when `propagate`. */
  inline def extremum[V](
      inline algebra: Algebra[V],
      inline width: Int,
      control: FloatControl,
      inline maximum: Boolean,
      propagate: Boolean,
      left: V,
      right: V
  ): V = {
    val a = input(algebra, width, control, left)
    val b = input(algebra, width, control, right)
    result(
      algebra,
      width,
      control,
      if (maximum)
        if (propagate)
          algebra.floating(width, a, b)(
            FloatArithmetic.maximum(_, _, absolute = control.absolute, xorSign = control.xorSign),
            FloatArithmetic.maximum(_, _, absolute = control.absolute, xorSign = control.xorSign),
            FloatArithmetic.maximum(_, _, absolute = control.absolute, xorSign = control.xorSign)
          )
        else
          algebra.floating(width, a, b)(
            FloatArithmetic.maximumNumber(_, _, absolute = control.absolute, xorSign = control.xorSign),
            FloatArithmetic.maximumNumber(_, _, absolute = control.absolute, xorSign = control.xorSign),
            FloatArithmetic.maximumNumber(_, _, absolute = control.absolute, xorSign = control.xorSign)
          )
      else if (propagate)
        algebra.floating(width, a, b)(
          FloatArithmetic.minimum(_, _, absolute = control.absolute, xorSign = control.xorSign),
          FloatArithmetic.minimum(_, _, absolute = control.absolute, xorSign = control.xorSign),
          FloatArithmetic.minimum(_, _, absolute = control.absolute, xorSign = control.xorSign)
        )
      else
        algebra.floating(width, a, b)(
          FloatArithmetic.minimumNumber(_, _, absolute = control.absolute, xorSign = control.xorSign),
          FloatArithmetic.minimumNumber(_, _, absolute = control.absolute, xorSign = control.xorSign),
          FloatArithmetic.minimumNumber(_, _, absolute = control.absolute, xorSign = control.xorSign)
        )
    )
  }

  inline def unary[V, R](
      inline algebra: Algebra[V],
      operation: FloatUnaryOperation,
      width: Int in (16, 32, 64),
      control: FloatControl
  )(inline sinks: NvidiaSinks[V, R], destination: Register, source: Value): R = width.raw match {
    case 16 => unaryAt(algebra, operation, 16, control)(sinks, destination, source)
    case 32 => unaryAt(algebra, operation, 32, control)(sinks, destination, source)
    case _  => unaryAt(algebra, operation, 64, control)(sinks, destination, source)
  }

  inline def unaryAt[V, R](
      inline algebra: Algebra[V],
      operation: FloatUnaryOperation,
      inline width: Int,
      control: FloatControl
  )(inline sinks: NvidiaSinks[V, R], destination: Register, source: Value): R = {
    val sign = 1L << (width - 1)
    operation match {
      case FloatUnaryOperation.Negate => sinks.unary(destination, source)(v =>
          result(
            algebra,
            width,
            control,
            bits(algebra, width, algebra.xor(input(algebra, width, control, v), algebra.literal(sign)))
          )
        )
      case FloatUnaryOperation.Absolute => sinks.unary(destination, source)(v =>
          result(
            algebra,
            width,
            control,
            bits(algebra, width, algebra.and(input(algebra, width, control, v), algebra.literal(~sign)))
          )
        )
      case FloatUnaryOperation.Reciprocal => sinks.unary(destination, source)(v =>
          approximate(algebra, width, control, v)(
            FloatArithmetic.reciprocal(_),
            FloatArithmetic.reciprocal(_),
            FloatArithmetic.reciprocal(_)
          )
        )
      case FloatUnaryOperation.SquareRoot => sinks.unary(destination, source)(v =>
          approximate(algebra, width, control, v)(
            FloatArithmetic.squareRoot(_),
            FloatArithmetic.squareRoot(_),
            FloatArithmetic.squareRoot(_)
          )
        )
      case FloatUnaryOperation.ReciprocalSquareRoot => sinks.unary(destination, source)(v =>
          approximate(algebra, width, control, v)(
            FloatArithmetic.reciprocalSquareRoot(_),
            FloatArithmetic.reciprocalSquareRoot(_),
            FloatArithmetic.reciprocalSquareRoot(_)
          )
        )
      case FloatUnaryOperation.Exponent2 => sinks.unary(destination, source)(v =>
          approximate(algebra, width, control, v)(
            FloatArithmetic.exponent2(_),
            FloatArithmetic.exponent2(_),
            FloatArithmetic.exponent2(_)
          )
        )
      case FloatUnaryOperation.Log2 => sinks.unary(destination, source)(v =>
          approximate(algebra, width, control, v)(
            FloatArithmetic.log2(_),
            FloatArithmetic.log2(_),
            FloatArithmetic.log2(_)
          )
        )
      case FloatUnaryOperation.Sine => sinks.unary(destination, source)(v =>
          approximate(algebra, width, control, v)(
            FloatArithmetic.sine(_),
            FloatArithmetic.sine(_),
            FloatArithmetic.sine(_)
          )
        )
      case FloatUnaryOperation.Cosine => sinks.unary(destination, source)(v =>
          approximate(algebra, width, control, v)(
            FloatArithmetic.cosine(_),
            FloatArithmetic.cosine(_),
            FloatArithmetic.cosine(_)
          )
        )
      case FloatUnaryOperation.Tanh => sinks.unary(destination, source)(v =>
          approximate(algebra, width, control, v)(
            FloatArithmetic.tanh(_),
            FloatArithmetic.tanh(_),
            FloatArithmetic.tanh(_)
          )
        )
    }
  }

  inline def approximate[V](inline algebra: Algebra[V], inline width: Int, control: FloatControl, value: V)(
      inline half: Float16 => Float16,
      inline single: Float32 => Float32,
      inline double: Float64 => Float64
  ): V =
    result(
      algebra,
      width,
      control,
      algebra.floating(width, input(algebra, width, control, value))(half, single, double)
    )

  inline def fusedMultiplyAdd[V, R](
      inline algebra: Algebra[V],
      width: Int in (16, 32, 64),
      control: FloatControl,
      rounding: ConversionRounding
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    width.raw match {
      case 16 => fusedMultiplyAddAt(algebra, 16, control, rounding)(sinks, destination, first, second, third)
      case 32 => fusedMultiplyAddAt(algebra, 32, control, rounding)(sinks, destination, first, second, third)
      case _  => fusedMultiplyAddAt(algebra, 64, control, rounding)(sinks, destination, first, second, third)
    }

  inline def fusedMultiplyAddAt[V, R](
      inline algebra: Algebra[V],
      inline width: Int,
      control: FloatControl,
      rounding: ConversionRounding
  )(inline sinks: NvidiaSinks[V, R], destination: Register, first: Value, second: Value, third: Value): R =
    sinks.ternary(destination, first, second, third)((a, b, c) =>
      result(
        algebra,
        width,
        control,
        canonical(
          algebra,
          width,
          algebra.floating(
            width,
            input(algebra, width, control, a),
            input(algebra, width, control, b),
            input(algebra, width, control, c)
          )(
            FloatArithmetic.fusedMultiplyAdd(_, _, _, rounding),
            FloatArithmetic.fusedMultiplyAdd(_, _, _, rounding),
            FloatArithmetic.fusedMultiplyAdd(_, _, _, rounding)
          )
        )
      )
    )

  /** Two elements per register, each under the scalar semantics: binary16 and binary32 elements at their width, and
    * bfloat16 ones as binary32 values rounded back to bfloat16.
    */
  inline def binaryPacked[V, R](
      inline algebra: Algebra[V],
      operation: FloatOperation,
      format: PackedFloatFormat,
      control: FloatControl
  )(inline sinks: NvidiaSinks[V, R], destination: Register, left: Value, right: Value): R = operation match {
    case FloatOperation.Add =>
      packed(algebra, format)(sinks, destination, left, right)(
        add(algebra, 16, control, _, _),
        add(algebra, 32, control, _, _)
      )
    case FloatOperation.Subtract =>
      packed(algebra, format)(sinks, destination, left, right)(
        subtract(algebra, 16, control, _, _),
        subtract(algebra, 32, control, _, _)
      )
    case FloatOperation.Multiply =>
      packed(algebra, format)(sinks, destination, left, right)(
        multiply(algebra, 16, control, _, _),
        multiply(algebra, 32, control, _, _)
      )
    case FloatOperation.Divide =>
      packed(algebra, format)(sinks, destination, left, right)(
        divide(algebra, 16, control, _, _),
        divide(algebra, 32, control, _, _)
      )
    case FloatOperation.CopySign =>
      packed(algebra, format)(sinks, destination, left, right)(
        copySign(algebra, 16, control, _, _),
        copySign(algebra, 32, control, _, _)
      )
    case FloatOperation.Minimum | FloatOperation.MinimumNumber =>
      extremumPacked(algebra, format)(
        sinks,
        destination,
        left,
        right,
        control,
        maximum = false,
        propagate = control.propagateNaN || operation == FloatOperation.Minimum
      )
    case FloatOperation.Maximum | FloatOperation.MaximumNumber =>
      extremumPacked(algebra, format)(
        sinks,
        destination,
        left,
        right,
        control,
        maximum = true,
        propagate = control.propagateNaN || operation == FloatOperation.Maximum
      )
  }

  private inline def extremumPacked[V, R](inline algebra: Algebra[V], format: PackedFloatFormat)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      left: Value,
      right: Value,
      control: FloatControl,
      inline maximum: Boolean,
      propagate: Boolean
  ): R = packed(algebra, format)(sinks, destination, left, right)(
    extremum(algebra, 16, control, maximum, propagate, _, _),
    extremum(algebra, 32, control, maximum, propagate, _, _)
  )

  /** `half` of each pair of binary16 elements, or `single` of each pair of binary32 elements, or of bfloat16 elements
    * as binary32 values.
    */
  inline def packed[V, R](inline algebra: Algebra[V], format: PackedFloatFormat)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      left: Value,
      right: Value
  )(inline half: (V, V) => V, inline single: (V, V) => V): R = format match {
    case PackedFloatFormat.F16x2  => sinks.binary(destination, left, right)((a, b) => algebra.perHalf(16, a, b)(half))
    case PackedFloatFormat.F32x2  => sinks.binary(destination, left, right)((a, b) => algebra.perHalf(32, a, b)(single))
    case PackedFloatFormat.BF16x2 =>
      sinks.binary(destination, left, right)((a, b) =>
        algebra.perHalf(16, a, b)((x, y) =>
          algebra.primitive(
            single(algebra.shiftLeft(x, algebra.literal(16L)), algebra.shiftLeft(y, algebra.literal(16L)))
          )(FloatArithmetic.roundBfloat16(_))
        )
      )
  }

  /** Each element's fused multiply-add rounded to nearest; binary16 elements are canonical. */
  inline def fusedMultiplyAddPacked[V, R](inline algebra: Algebra[V], bf16: Boolean, saturate: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    if (bf16)
      sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.perHalf(a, b, c)((x, y, z) =>
          algebra.primitive(x, y, z)(FloatArithmetic.fusedMultiplyAddBfloat16(_, _, _, saturate = saturate))
        )
      )
    else
      sinks.ternary(destination, first, second, third)((a, b, c) =>
        algebra.perHalf(a, b, c)((x, y, z) =>
          saturated(
            algebra,
            16,
            saturate,
            canonical(
              algebra,
              16,
              algebra.primitive(x, y, z)((p: Float16, q: Float16, r: Float16) =>
                FloatArithmetic.fusedMultiplyAdd(p, q, r, ConversionRounding.RN)
              )
            )
          )
        )
      )

  inline def fusedMultiplyAddBf16[V, R](inline algebra: Algebra[V])(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      first: Value,
      second: Value,
      third: Value
  ): R =
    sinks.ternary(destination, first, second, third)((a, b, c) =>
      algebra.primitive(a, b, c)(FloatArithmetic.fusedMultiplyAddBfloat16(_, _, _, saturate = false))
    )

  /** A comparison of binary16, binary32, binary64 or, when `bfloat16`, bfloat16 values; `flushSubnormal` flushes each
    * operand first.
    */
  inline def comparison[V, R](
      inline algebra: Algebra[V],
      comparison: Comparison,
      width: Int in (16, 32, 64),
      bfloat16: Boolean,
      flushSubnormal: Boolean
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      left: Value,
      right: Value
  ): R =
    if (bfloat16)
      sinks.compare(destination, left, right)((l, r) =>
        algebra.primitive(flushBfloat16(algebra, flushSubnormal, l), flushBfloat16(algebra, flushSubnormal, r))(
          FloatArithmetic.compareBfloat16(_, _, comparison)
        )
      )
    else
      width.raw match {
        case 16 => comparisonAt(algebra, comparison, 16, flushSubnormal)(sinks, destination, left, right)
        case 32 => comparisonAt(algebra, comparison, 32, flushSubnormal)(sinks, destination, left, right)
        case _  => comparisonAt(algebra, comparison, 64, flushSubnormal)(sinks, destination, left, right)
      }

  inline def comparisonAt[V, R](
      inline algebra: Algebra[V],
      comparison: Comparison,
      inline width: Int,
      flushSubnormal: Boolean
  )(
      inline sinks: NvidiaSinks[V, R],
      destination: Predicate,
      left: Value,
      right: Value
  ): R = sinks.compare(destination, left, right)((l, r) =>
    algebra.relation(width, flushed(algebra, width, flushSubnormal, l), flushed(algebra, width, flushSubnormal, r))(
      FloatArithmetic.compare(_, _, comparison),
      FloatArithmetic.compare(_, _, comparison),
      FloatArithmetic.compare(_, _, comparison)
    )
  )

  inline def flushBfloat16[V](inline algebra: Algebra[V], flush: Boolean, value: V): V =
    if (flush) algebra.primitive(value)(FloatArithmetic.flushBfloat16(_)) else value

  inline def input[V](inline algebra: Algebra[V], inline width: Int, control: FloatControl, value: V): V =
    flushed(algebra, width, control.flushSubnormal, value)

  inline def flushed[V](inline algebra: Algebra[V], inline width: Int, flush: Boolean, value: V): V =
    if (flush)
      algebra.floating(width, value)(FloatArithmetic.flush(_), FloatArithmetic.flush(_), FloatArithmetic.flush(_))
    else value

  /** The value saturated, then flushed, as the instruction asks. */
  inline def result[V](inline algebra: Algebra[V], inline width: Int, control: FloatControl, value: V): V =
    flushed(algebra, width, control.flushSubnormal, saturated(algebra, width, control.saturate, value))

  inline def saturated[V](inline algebra: Algebra[V], inline width: Int, saturate: Boolean, value: V): V =
    if (saturate)
      algebra.floating(width, value)(
        FloatArithmetic.saturate(_),
        FloatArithmetic.saturate(_),
        FloatArithmetic.saturate(_)
      )
    else value

  inline def canonical[V](inline algebra: Algebra[V], inline width: Int, value: V): V = inline width match {
    case 16 => algebra.primitive(value)((x: Float16) => FloatArithmetic.canonical(x))
    case 32 => algebra.primitive(value)((x: Float32) => FloatArithmetic.canonical(x))
    case _  => value
  }

  /** The low `width` bits of `value`. */
  inline def bits[V](inline algebra: Algebra[V], inline width: Int, value: V): V = inline width match {
    case 16 => algebra.truncate(value, 16)
    case 32 => algebra.truncate(value, 32)
    case _  => value
  }
}
