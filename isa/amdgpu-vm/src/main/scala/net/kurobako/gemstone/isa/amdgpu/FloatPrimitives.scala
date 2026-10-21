package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** Floating-point primitives on register bits that both the integer and the floating-point semantics build on. */
private object FloatPrimitives {
  extension [V](inline algebra: Algebra[V]) {
    inline def saturated(clamp: Boolean, value: V, width: Int in (16, 32, 64)): V =
      if (clamp) algebra.saturate(value, width) else value

    /** A `width`-bit float clamped to [0, 1]: NaN passes, and the negative range, -0 included, becomes +0. */
    inline def saturate(value: V, width: Int in (16, 32, 64)): V =
      algebra.let("x", algebra.truncate(value, bits(width)))(x =>
        algebra.select(
          algebra.isNaN(x, width),
          x,
          algebra.select(
            algebra.notEqual(algebra.and(x, algebra.literal(IEEE754.signBit(width))), algebra.literal(0L)),
            algebra.literal(0L),
            algebra.minimum(x, algebra.literal(IEEE754.one(width)), signed = true)
          )
        )
      )
  }

  def bits(width: Int in (16, 32, 64)): Int in 1 ~ 64 = width.widen[Int in 1 ~ 64]

  /** MODE.FP_DENORM: an operation reads and writes `width`-bit denormals as zeros of their sign unless MODE keeps that
    * width's input or output denormals.
    */
  extension [V](inline algebra: Algebra[V]) {
    inline def denormalInput(mode: V, value: V, width: Int in (16, 32, 64)): V =
      algebra.denormalFlushed(
        mode,
        value,
        width,
        if (width == 32) ModeRegister.Denormal32Input else ModeRegister.Denormal16And64Input
      )

    inline def denormalOutput(mode: V, value: V, width: Int in (16, 32, 64)): V =
      algebra.denormalFlushed(
        mode,
        value,
        width,
        if (width == 32) ModeRegister.Denormal32Output else ModeRegister.Denormal16And64Output
      )

    inline def denormalFlushed(mode: V, value: V, width: Int in (16, 32, 64), keep: Int): V =
      algebra.let("x", value)(x =>
        algebra.select(
          algebra.equal(
            algebra.or(
              algebra.and(x, algebra.literal(IEEE754.infinity(width))),
              algebra.and(mode, algebra.literal(keep.toLong))
            ),
            algebra.literal(0L)
          ),
          algebra.and(x, algebra.literal(~(IEEE754.signBit(width) - 1L))),
          x
        )
      )

    /** A conversion from `from` bits to `to` bits, its operand's and result's denormals as MODE has them. */
    inline def converted[A, B](mode: V, from: Int in (16, 32, 64), to: Int in (16, 32, 64), a: V)(
        inline conversion: A => B
    ): V = algebra.denormalOutput(mode, algebra.primitive(algebra.denormalInput(mode, a, from))(conversion), to)

    /** `operation` on a `width`-bit operand and result as MODE.FP_DENORM has it read and write them. */
    inline def flushedUnary(mode: V, width: Int in (16, 32, 64), a: V)(inline operation: V => V): V =
      algebra.denormalOutput(mode, operation(algebra.denormalInput(mode, a, width)), width)

    inline def flushedBinary(mode: V, width: Int in (16, 32, 64), a: V, b: V)(inline operation: (V, V) => V): V =
      algebra.denormalOutput(
        mode,
        operation(algebra.denormalInput(mode, a, width), algebra.denormalInput(mode, b, width)),
        width
      )

    inline def flushedTernary(mode: V, width: Int in (16, 32, 64), a: V, b: V, c: V)(
        inline operation: (V, V, V) => V
    ): V =
      algebra.denormalOutput(
        mode,
        operation(
          algebra.denormalInput(mode, a, width),
          algebra.denormalInput(mode, b, width),
          algebra.denormalInput(mode, c, width)
        ),
        width
      )
  }

  /** A binary32 operation, saturated when `clamp`. */
  extension [V](inline algebra: Algebra[V]) {
    inline def singleOperation(clamp: Boolean, left: V, right: V)(
        inline operation: (Float32, Float32) => Float32
    ): V = algebra.saturated(clamp, algebra.primitive(left, right)(operation), 32)

    /** A binary16 operation computed in binary32, saturated when `clamp`. */
    inline def halfOperation(clamp: Boolean, left: V, right: V)(
        inline operation: (Float16, Float16) => Float16
    ): V = algebra.saturated(clamp, algebra.primitive(left, right)(operation), 16)

    /** [[singleOperation]] on operands and result as MODE.FP_DENORM has it read and write them. */
    inline def flushedSingle(mode: V, clamp: Boolean, left: V, right: V)(
        inline operation: (Float32, Float32) => Float32
    ): V = algebra.flushedBinary(mode, 32, left, right)((x, y) => algebra.singleOperation(clamp, x, y)(operation))

    /** [[halfOperation]] on operands and result as MODE.FP_DENORM has it read and write them. */
    inline def flushedHalf(mode: V, clamp: Boolean, left: V, right: V)(
        inline operation: (Float16, Float16) => Float16
    ): V = algebra.flushedBinary(mode, 16, left, right)((x, y) => algebra.halfOperation(clamp, x, y)(operation))

    inline def addSingle(left: V, right: V): V =
      algebra.primitive(left, right)(Floats.add(_: Float32, _: Float32))

    inline def multiplySingle(left: V, right: V): V =
      algebra.primitive(left, right)(Floats.mul(_: Float32, _: Float32))

    inline def fusedMultiplyAdd(a: V, b: V, c: V, width: Int in (16, 32, 64)): V =
      algebra.dispatched(width, a, b, c)(Floats.fma, Floats.fma, Floats.fma)

    /** [[divisionFixup]] with the denominator's and numerator's denormals as MODE has them read; the quotient passes as
      * it is.
      */
    inline def flushedFixup(mode: V, width: Int in (16, 32, 64), q: V, d: V, n: V): V =
      algebra.divisionFixup(width, q, algebra.denormalInput(mode, d, width), algebra.denormalInput(mode, n, width))

    /** V_DIV_FIXUP on the quotient, denominator and numerator. */
    inline def divisionFixup(width: Int in (16, 32, 64), q: V, d: V, n: V): V =
      width.raw match {
        case 64 => algebra.primitive(q, d, n)(Floats.divFixup(_: Float64, _: Float64, _: Float64))
        case 32 => algebra.primitive(q, d, n)(Floats.divFixup(_: Float32, _: Float32, _: Float32))
        case _  => algebra.primitive(q, d, n)(Floats.divFixup(_: Float16, _: Float16, _: Float16))
      }

    inline def minimumNumber(a: V, b: V, width: Int in (16, 32, 64)): V =
      algebra.dispatched(width, a, b)(Float16.minNum, Float32.minNum, Float64.minNum)

    inline def maximumNumber(a: V, b: V, width: Int in (16, 32, 64)): V =
      algebra.dispatched(width, a, b)(Float16.maxNum, Float32.maxNum, Float64.maxNum)

    /** IEEE 754-2019 minimum, which propagates NaN and orders -0 below +0. */
    inline def minimumPropagate(a: V, b: V, width: Int in (16, 32, 64)): V =
      algebra.dispatched(width, a, b)(Float16.minimum, Float32.minimum, Float64.minimum)

    inline def maximumPropagate(a: V, b: V, width: Int in (16, 32, 64)): V =
      algebra.dispatched(width, a, b)(Float16.maximum, Float32.maximum, Float64.maximum)

    /** LDEXP: a `width`-bit float scaled by two to the power of a signed dword. */
    inline def loadExponent(value: V, exponent: V, width: Int in (32, 64)): V =
      if (width == 64) algebra.primitive(value, exponent)(Floats.ldexp(_: Float64, _: SInt[32]))
      else algebra.primitive(value, exponent)(Floats.ldexp(_: Float32, _: SInt[32]))

    inline def reciprocalSingle(value: V): V = algebra.primitive(value)(Floats.rcp(_: Float32))

    inline def truncateSingle(value: V): V = algebra.primitive(value)(Floats.trunc(_: Float32))

    inline def singleToInteger(value: V, inline signed: Boolean): V =
      inline if (signed) algebra.primitive(value)(Floats.cvtI32F32) else algebra.primitive(value)(Floats.cvtU32F32)

    inline def unsignedToSingle(value: V): V = algebra.primitive(value)(Floats.cvtF32U32)

    inline def signedToSingle(value: V): V = algebra.primitive(value)(Floats.cvtF32I32)

    inline def halfToSingle(value: V): V = algebra.primitive(value)(Floats.cvtF32F16)

    inline def singleToHalf(value: V): V = algebra.primitive(value)(Floats.cvtF16F32)
  }
}
