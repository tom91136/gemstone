package net.kurobako.gemstone.isa.spirv

import ExtendedInstruction.OpenClStd as OpenCL
import FloatingSemantics.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** OpenCL.std extended instruction semantics by extended opcode. Integer instructions wrap to the result width;
  * floating-point functions the host evaluates in binary64 are named primitives rounded once to the width.
  */
object OpenCLSemantics {

  inline def integerUnary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.SAbs     => sinks.unary(ids)(a => algebra.absolute(a, width))
    case OpenCL.UAbs     => sinks.unary(ids)(a => algebra.truncate(a, width))
    case OpenCL.Clz      => sinks.unary(ids)(a => algebra.leadingZeros(a, width))
    case OpenCL.Ctz      => sinks.unary(ids)(a => algebra.trailingZeros(a, width))
    case OpenCL.Popcount => sinks.unary(ids)(a => algebra.populationCount(algebra.truncate(a, width)))
    case _               => otherwise
  }

  /** The mean of two `width`-bit operands, rounded down or, with `round`, up, without overflowing. */
  inline def halving[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in 1 ~ 64,
      inline signed: Boolean,
      inline round: Boolean
  )(ids: OperandIds): R = sinks.binary(ids)((a, b) =>
    algebra.halvingAdd(
      inline if (signed) algebra.signExtend(a, width) else algebra.truncate(a, width),
      inline if (signed) algebra.signExtend(b, width) else algebra.truncate(b, width),
      width,
      signed = signed,
      round = round
    )
  )

  /** Two-operand integer instructions with `width`-bit results; upsampling reads `inputWidth`-bit halves. */
  inline def integerBinary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in 1 ~ 64,
      inputWidth: Int in 1 ~ 64
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case OpenCL.SAbsDiff => sinks.binary(ids)(algebra.absoluteDifference(_, _, width, signed = true))
    case OpenCL.UAbsDiff => sinks.binary(ids)(algebra.absoluteDifference(_, _, width, signed = false))
    case OpenCL.SAddSat  =>
      sinks.binary(ids)((a, b) =>
        algebra.truncate(algebra.addSaturated(a, b, width, signed = true), width)
      )
    case OpenCL.UAddSat =>
      sinks.binary(ids)((a, b) => algebra.addSaturated(a, b, width, signed = false))
    case OpenCL.SSubSat =>
      sinks.binary(ids)((a, b) =>
        algebra.truncate(algebra.subtractSaturated(a, b, width, signed = true), width)
      )
    case OpenCL.USubSat =>
      sinks.binary(ids)((a, b) => algebra.subtractSaturated(a, b, width, signed = false))
    case OpenCL.SHadd  => halving(algebra, sinks, width, signed = true, round = false)(ids)
    case OpenCL.UHadd  => halving(algebra, sinks, width, signed = false, round = false)(ids)
    case OpenCL.SRhadd => halving(algebra, sinks, width, signed = true, round = true)(ids)
    case OpenCL.URhadd => halving(algebra, sinks, width, signed = false, round = true)(ids)
    case OpenCL.SMulHi =>
      sinks.binary(ids)((a, b) => algebra.truncate(algebra.multiplyHigh(a, b, width, signed = true), width))
    case OpenCL.UMulHi =>
      sinks.binary(ids)((a, b) => algebra.truncate(algebra.multiplyHigh(a, b, width, signed = false), width))
    case OpenCL.Rotate => sinks.binary(ids)((a, b) =>
        algebra.let("x", algebra.truncate(a, width))(x =>
          algebra.let("n", algebra.and(b, algebra.literal(width - 1L)))(n =>
            algebra.truncate(
              algebra.or(
                algebra.shiftLeft(x, n),
                algebra.shiftRightLogical(x, algebra.subtract(algebra.literal(width.toLong), n))
              ),
              width
            )
          )
        )
      )
    case OpenCL.SUpsample => sinks.binary(ids)((a, b) =>
        algebra.truncate(
          algebra.or(
            algebra.shiftLeft(algebra.signExtend(a, inputWidth), algebra.literal(inputWidth.toLong)),
            algebra.truncate(b, inputWidth)
          ),
          width
        )
      )
    case OpenCL.UUpsample => sinks.binary(ids)((a, b) =>
        algebra.truncate(
          algebra.or(
            algebra.shiftLeft(algebra.truncate(a, inputWidth), algebra.literal(inputWidth.toLong)),
            algebra.truncate(b, inputWidth)
          ),
          width
        )
      )
    case OpenCL.SMul24 =>
      sinks.binary(ids)((a, b) =>
        algebra.truncate(algebra.multiply(algebra.signExtend(a, 24), algebra.signExtend(b, 24)), width)
      )
    case OpenCL.UMul24 =>
      sinks.binary(ids)((a, b) =>
        algebra.truncate(algebra.multiply(algebra.truncate(a, 24), algebra.truncate(b, 24)), width)
      )
    case OpenCL.SMax => sinks.binary(ids)((a, b) =>
        algebra.truncate(IntegerSemantics.maximum(algebra, a, b, width, signed = true), width)
      )
    case OpenCL.UMax => sinks.binary(ids)((a, b) =>
        algebra.truncate(IntegerSemantics.maximum(algebra, a, b, width, signed = false), width)
      )
    case OpenCL.SMin => sinks.binary(ids)((a, b) =>
        algebra.truncate(IntegerSemantics.minimum(algebra, a, b, width, signed = true), width)
      )
    case OpenCL.UMin => sinks.binary(ids)((a, b) =>
        algebra.truncate(IntegerSemantics.minimum(algebra, a, b, width, signed = false), width)
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
    case OpenCL.SClamp => sinks.ternary(ids)((a, b, c) => algebra.clamp(a, b, c, width, signed = true))
    case OpenCL.UClamp => sinks.ternary(ids)((a, b, c) => algebra.clamp(a, b, c, width, signed = false))
    case OpenCL.SMadHi => sinks.ternary(ids)((a, b, c) =>
        algebra.truncate(
          algebra.add(algebra.multiplyHigh(a, b, width, signed = true), algebra.signExtend(c, width)),
          width
        )
      )
    case OpenCL.UMadHi => sinks.ternary(ids)((a, b, c) =>
        algebra.truncate(
          algebra.add(algebra.multiplyHigh(a, b, width, signed = false), algebra.truncate(c, width)),
          width
        )
      )
    case OpenCL.SMadSat =>
      sinks.ternary(ids)((a, b, c) => algebra.truncate(algebra.saturatedMad(a, b, c, width, signed = true), width))
    case OpenCL.UMadSat => sinks.ternary(ids)((a, b, c) => algebra.saturatedMad(a, b, c, width, signed = false))
    case OpenCL.SMad24  => sinks.ternary(ids)((a, b, c) =>
        algebra.truncate(
          algebra.add(
            algebra.multiply(algebra.signExtend(a, 24), algebra.signExtend(b, 24)),
            algebra.signExtend(c, width)
          ),
          width
        )
      )
    case OpenCL.UMad24 => sinks.ternary(ids)((a, b, c) =>
        algebra.truncate(
          algebra.add(algebra.multiply(algebra.truncate(a, 24), algebra.truncate(b, 24)), algebra.truncate(c, width)),
          width
        )
      )
    case _ => otherwise
  }

  /** `(left + right) / 2` without overflow, rounding up when `round`. */
  extension [V](inline algebra: Algebra[V]) {
    inline def halvingAdd(
        left: V,
        right: V,
        width: Int in 1 ~ 64,
        inline signed: Boolean,
        inline round: Boolean
    ): V = algebra.let("x", algebra.xor(left, right))(x =>
      algebra.let(
        "h",
        algebra.add(
          algebra.and(left, right),
          if (signed) algebra.shiftRightArithmetic(x, algebra.literal(1L))
          else algebra.shiftRightLogical(x, algebra.literal(1L))
        )
      )(h => algebra.truncate(if (round) algebra.add(h, algebra.and(x, algebra.literal(1L))) else h, width))
    )

    /** `min(max(a, b), c)` at `width` bits. */
    inline def clamp(
        value: V,
        lower: V,
        upper: V,
        width: Int in 1 ~ 64,
        inline signed: Boolean
    ): V = algebra.truncate(
      algebra.minimum(
        algebra.maximum(
          algebra.extend(value, width, signed),
          algebra.extend(lower, width, signed),
          signed
        ),
        algebra.extend(upper, width, signed),
        signed
      ),
      width
    )
  }

  /** Inverse trigonometric and hyperbolic functions. */
  inline def inverseTrigonometric[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.Acos   => unaryDouble(algebra, sinks, width)(Elementary.acos(_))(ids)
    case OpenCL.Acosh  => unaryDouble(algebra, sinks, width)(Elementary.acosh(_))(ids)
    case OpenCL.Acospi => unaryDouble(algebra, sinks, width)(Elementary.acospi(_))(ids)
    case OpenCL.Asin   => unaryDouble(algebra, sinks, width)(Elementary.asin(_))(ids)
    case OpenCL.Asinh  => unaryDouble(algebra, sinks, width)(Elementary.asinh(_))(ids)
    case OpenCL.Asinpi => unaryDouble(algebra, sinks, width)(Elementary.asinpi(_))(ids)
    case OpenCL.Atan   => unaryDouble(algebra, sinks, width)(Elementary.atan(_))(ids)
    case OpenCL.Atanh  => unaryDouble(algebra, sinks, width)(Elementary.atanh(_))(ids)
    case OpenCL.Atanpi => unaryDouble(algebra, sinks, width)(Elementary.atanpi(_))(ids)
    case _             => otherwise
  }

  /** Trigonometric and hyperbolic functions. */
  inline def trigonometric[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.Cos   => unaryDouble(algebra, sinks, width)(Elementary.cos(_))(ids)
    case OpenCL.Cosh  => unaryDouble(algebra, sinks, width)(Elementary.cosh(_))(ids)
    case OpenCL.Cospi => unaryDouble(algebra, sinks, width)(Elementary.cospi(_))(ids)
    case OpenCL.Sin   => unaryDouble(algebra, sinks, width)(Elementary.sin(_))(ids)
    case OpenCL.Sinh  => unaryDouble(algebra, sinks, width)(Elementary.sinh(_))(ids)
    case OpenCL.Sinpi => unaryDouble(algebra, sinks, width)(Elementary.sinpi(_))(ids)
    case OpenCL.Tan   => unaryDouble(algebra, sinks, width)(Elementary.tan(_))(ids)
    case OpenCL.Tanh  => unaryDouble(algebra, sinks, width)(Elementary.tanh(_))(ids)
    case OpenCL.Tanpi => unaryDouble(algebra, sinks, width)(Elementary.tanpi(_))(ids)
    case _            => otherwise
  }

  /** Exponentials, logarithms and roots. */
  inline def exponential[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.Exp   => unaryDouble(algebra, sinks, width)(Elementary.exp(_))(ids)
    case OpenCL.Exp2  => unaryDouble(algebra, sinks, width)(Elementary.exp2(_))(ids)
    case OpenCL.Exp10 => unaryDouble(algebra, sinks, width)(Elementary.exp10(_))(ids)
    case OpenCL.Expm1 => unaryDouble(algebra, sinks, width)(Elementary.expm1(_))(ids)
    case OpenCL.Log   => unaryDouble(algebra, sinks, width)(Elementary.log(_))(ids)
    case OpenCL.Log2  => unaryDouble(algebra, sinks, width)(Elementary.log2(_))(ids)
    case OpenCL.Log10 => unaryDouble(algebra, sinks, width)(Elementary.log10(_))(ids)
    case OpenCL.Log1p => unaryDouble(algebra, sinks, width)(Elementary.log1p(_))(ids)
    case OpenCL.Logb  => unaryDouble(algebra, sinks, width)(Elementary.logb(_))(ids)
    case OpenCL.Cbrt  => unaryDouble(algebra, sinks, width)(Elementary.cbrt(_))(ids)
    case OpenCL.Sqrt  => unaryDouble(algebra, sinks, width)(Elementary.sqrt(_))(ids)
    case OpenCL.Rsqrt => unaryDouble(algebra, sinks, width)(Elementary.rsqrt(_))(ids)
    case _            => otherwise
  }

  /** Magnitude, rounding, error and gamma functions, and angle conversions. */
  inline def elementary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.Fabs    => sinks.unary(ids)(a => algebra.magnitude(a, IEEE754.signBit(width)))
    case OpenCL.Ceil    => unaryDouble(algebra, sinks, width)(Elementary.ceil(_))(ids)
    case OpenCL.Floor   => unaryDouble(algebra, sinks, width)(Elementary.floor(_))(ids)
    case OpenCL.Rint    => unaryDouble(algebra, sinks, width)(Elementary.rint(_))(ids)
    case OpenCL.Round   => unaryDouble(algebra, sinks, width)(Elementary.round(_))(ids)
    case OpenCL.Trunc   => unaryDouble(algebra, sinks, width)(Elementary.trunc(_))(ids)
    case OpenCL.Erf     => unaryDouble(algebra, sinks, width)(Elementary.erf(_))(ids)
    case OpenCL.Erfc    => unaryDouble(algebra, sinks, width)(Elementary.erfc(_))(ids)
    case OpenCL.Tgamma  => unaryDouble(algebra, sinks, width)(Elementary.tgamma(_))(ids)
    case OpenCL.Lgamma  => unaryDouble(algebra, sinks, width)(Elementary.lgamma(_))(ids)
    case OpenCL.Degrees => unaryDouble(algebra, sinks, width)(Elementary.degrees(_))(ids)
    case OpenCL.Radians => unaryDouble(algebra, sinks, width)(Elementary.radians(_))(ids)
    case OpenCL.Sign    => unaryDouble(algebra, sinks, width)(Elementary.sign(_))(ids)
    case _              => otherwise
  }

  /** The native_ functions, which the host evaluates at full precision. */
  inline def native[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.NativeCos   => unaryDouble(algebra, sinks, width)(Elementary.cos(_))(ids)
    case OpenCL.NativeExp   => unaryDouble(algebra, sinks, width)(Elementary.exp(_))(ids)
    case OpenCL.NativeExp2  => unaryDouble(algebra, sinks, width)(Elementary.exp2(_))(ids)
    case OpenCL.NativeExp10 => unaryDouble(algebra, sinks, width)(Elementary.exp10(_))(ids)
    case OpenCL.NativeLog   => unaryDouble(algebra, sinks, width)(Elementary.log(_))(ids)
    case OpenCL.NativeLog2  => unaryDouble(algebra, sinks, width)(Elementary.log2(_))(ids)
    case OpenCL.NativeLog10 => unaryDouble(algebra, sinks, width)(Elementary.log10(_))(ids)
    case OpenCL.NativeRecip => unaryDouble(algebra, sinks, width)(Elementary.recip(_))(ids)
    case OpenCL.NativeRsqrt => unaryDouble(algebra, sinks, width)(Elementary.rsqrt(_))(ids)
    case OpenCL.NativeSin   => unaryDouble(algebra, sinks, width)(Elementary.sin(_))(ids)
    case OpenCL.NativeSqrt  => unaryDouble(algebra, sinks, width)(Elementary.sqrt(_))(ids)
    case OpenCL.NativeTan   => unaryDouble(algebra, sinks, width)(Elementary.tan(_))(ids)
    case _                  => otherwise
  }

  inline def floatingBinary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.Atan2        => binaryDouble(algebra, sinks, width)(Elementary.atan2(_, _))(ids)
    case OpenCL.Atan2pi      => binaryDouble(algebra, sinks, width)(Elementary.atan2pi(_, _))(ids)
    case OpenCL.Hypot        => binaryDouble(algebra, sinks, width)(Elementary.hypot(_, _))(ids)
    case OpenCL.Pow          => binaryDouble(algebra, sinks, width)(Elementary.pow(_, _))(ids)
    case OpenCL.Fdim         => binaryDouble(algebra, sinks, width)(Elementary.fdim(_, _))(ids)
    case OpenCL.Fmod         => binaryDouble(algebra, sinks, width)(Elementary.fmod(_, _))(ids)
    case OpenCL.Remainder    => binaryDouble(algebra, sinks, width)(Elementary.remainder(_, _))(ids)
    case OpenCL.NativeDivide => binaryDouble(algebra, sinks, width)(Elementary.divide(_, _))(ids)
    case OpenCL.NativePowr   => binaryDouble(algebra, sinks, width)(Elementary.powr(_, _))(ids)
    case OpenCL.Powr         => binaryDouble(algebra, sinks, width)(Elementary.powr(_, _))(ids)
    case OpenCL.Step         => binaryDouble(algebra, sinks, width)(Elementary.step(_, _))(ids)
    case _                   => otherwise
  }

  /** Sign transfer, ordering, magnitude selection and adjacency of two operands. */
  inline def floatingOrder[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.Copysign   => sinks.binary(ids)((a, b) => algebra.blend(algebra.literal(IEEE754.signBit(width)), b, a))
    case OpenCL.FmaxCommon => extremum(algebra, sinks, maximum = true, width)(ids)
    case OpenCL.FminCommon => extremum(algebra, sinks, maximum = false, width)(ids)
    case OpenCL.Maxmag     => magnitudeSelect(algebra, sinks, width, maximum = true)(ids)
    case OpenCL.Minmag     => magnitudeSelect(algebra, sinks, width, maximum = false)(ids)
    case OpenCL.Nextafter  =>
      arithmetic(algebra, sinks, width)(nextAfter(_, _), nextAfter(_, _), nextAfter(_, _))(ids)
    case _ => otherwise
  }

  inline def floatingTernary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.Fma        => fusedMultiplyAdd(algebra, sinks, width)(ids)
    case OpenCL.Mad        => multiplyAdd(algebra, sinks, width)(ids)
    case OpenCL.Fclamp     => FloatingSemantics.clamp(algebra, sinks, width)(ids)
    case OpenCL.Mix        => ternaryDouble(algebra, sinks, width)(Elementary.mix(_, _, _))(ids)
    case OpenCL.Smoothstep => ternaryDouble(algebra, sinks, width)(Elementary.smoothstep(_, _, _, width))(ids)
    case _                 => otherwise
  }

  /** `a * b + c` rounded once to the width. */
  inline def fusedMultiplyAdd[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => sinks.ternary(ids)((a, b, c) => algebra.primitive(a, b, c)(Float16.fma(_, _, _)))
    case 32 => sinks.ternary(ids)((a, b, c) => algebra.primitive(a, b, c)(Float32.fma(_, _, _)))
    case _  => sinks.ternary(ids)((a, b, c) => algebra.primitive(a, b, c)(Float64.fma(_, _, _)))
  }

  /** `a * b + c` with the product rounded to the width before the sum. */
  inline def multiplyAdd[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 =>
      sinks.ternary(ids)((a, b, c) =>
        algebra.primitive(algebra.primitive(a, b)(multiply(_: Float16, _: Float16)), c)(add(_: Float16, _: Float16))
      )
    case 32 =>
      sinks.ternary(ids)((a, b, c) =>
        algebra.primitive(algebra.primitive(a, b)(multiply(_: Float32, _: Float32)), c)(add(_: Float32, _: Float32))
      )
    case _ =>
      sinks.ternary(ids)((a, b, c) =>
        algebra.primitive(algebra.primitive(a, b)(multiply(_: Float64, _: Float64)), c)(add(_: Float64, _: Float64))
      )
  }

  /** maxmag and minmag: the operand of greater or smaller magnitude, or fmax or fmin of equal magnitudes. */
  inline def magnitudeSelect[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      inline maximum: Boolean
  )(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => magnitudeBinary(algebra, sinks, sign = 0x8000L, infinity = 0x7c00L, 16, maximum)(ids)
    case 32 => magnitudeBinary(algebra, sinks, sign = 0x80000000L, infinity = 0x7f800000L, 32, maximum)(ids)
    case _  => magnitudeBinary(algebra, sinks, sign = Long.MinValue, infinity = 0x7ff0000000000000L, 64, maximum)(ids)
  }

  private inline def magnitudeBinary[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline sign: Long,
      inline infinity: Long,
      inline bits: Int,
      inline maximum: Boolean
  )(ids: OperandIds): R =
    sinks.binary(ids)((a, b) => algebra.magnitudeSelectAt(a, b, sign = sign, infinity = infinity, bits, maximum))

  extension [V](inline algebra: Algebra[V]) {
    private inline def saturatedMad(
        a: V,
        b: V,
        c: V,
        width: Int in 1 ~ 64,
        inline signed: Boolean
    ): V = algebra.primitive(a, b, c)((x: UInt[64], y: UInt[64], z: UInt[64]) =>
      multiplyAddSaturated(x, y, z, width, signed = signed)
    )
  }

  /** `x * y + z` clamped to the range of `width` bits, each read at `width` bits. */
  def multiplyAddSaturated(x: UInt[64], y: UInt[64], z: UInt[64], width: Int in 1 ~ 64, signed: Boolean): UInt[64] =
    UInt[64](IntegerArithmetic.multiplyAddSaturated(
      left = x.raw,
      right = y.raw,
      addend = z.raw,
      width = width,
      signed = signed
    ))

  extension [V](inline algebra: Algebra[V]) {
    inline def magnitudeSelectAt(
        left: V,
        right: V,
        inline sign: Long,
        inline infinity: Long,
        width: Int in (16, 32, 64),
        inline maximum: Boolean
    ): V = algebra.let("o", algebra.equal(algebra.unordered(left, right, sign, infinity), algebra.literal(0L)))(o =>
      algebra.let("ma", algebra.magnitude(left, sign))(ma =>
        algebra.let("mb", algebra.magnitude(right, sign))(mb =>
          algebra.select(
            algebra.and(o, algebra.lessThan(mb, ma, signed = false)),
            if (maximum) left else right,
            algebra.select(
              algebra.and(o, algebra.lessThan(ma, mb, signed = false)),
              if (maximum) right else left,
              if (maximum) algebra.maximum(left, right, width)
              else algebra.minimum(left, right, width)
            )
          )
        )
      )
    )
  }

  /** ldexp: the operand scaled by a power of two read at `exponentWidth` bits and clamped to the `Int` range. */
  inline def ldexp[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      exponentWidth: Int in 1 ~ 64
  )(
      ids: OperandIds
  ): R = binaryDoubleInteger(algebra, sinks, width, exponentWidth)(Elementary.ldexp(_, _))(ids)

  /** pown and rootn, whose integer operand is read at `exponentWidth` bits and wraps to an `Int`. */
  inline def integerPower[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64),
      exponentWidth: Int in 1 ~ 64
  )(ids: OperandIds)(otherwise: => R): R = opcode match {
    case OpenCL.Pown  => binaryDoubleInteger(algebra, sinks, width, exponentWidth)(Elementary.pown(_, _))(ids)
    case OpenCL.Rootn => binaryDoubleInteger(algebra, sinks, width, exponentWidth)(Elementary.rootn(_, _))(ids)
    case _            => otherwise
  }

  /** `function` of a floating operand and a signed `exponentWidth`-bit integer, rounded once to `width`. */
  inline def binaryDoubleInteger[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      exponentWidth: Int in 1 ~ 64
  )(inline function: (Float64, SInt[64]) => Float64)(ids: OperandIds): R = width.raw match {
    case 16 => doubleIntegerAt(algebra, sinks, 16, exponentWidth)(function)(ids)
    case 32 => doubleIntegerAt(algebra, sinks, 32, exponentWidth)(function)(ids)
    case _  => sinks.binary(ids)((a, b) => algebra.primitive(a, algebra.signExtend(b, exponentWidth))(function))
  }

  private inline def doubleIntegerAt[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline bits: Int,
      exponentWidth: Int in 1 ~ 64
  )(inline function: (Float64, SInt[64]) => Float64)(ids: OperandIds): R = sinks.binary(ids)((a, b) =>
    algebra.rounded(
      algebra.primitive(algebra.widened(a, bits), algebra.signExtend(b, exponentWidth))(function),
      bits
    )
  )

  /** ilogb as a 32-bit integer. */
  inline def ilogb[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64)
  )(ids: OperandIds): R =
    width.raw match {
      case 16 => sinks.unary(ids)(a => algebra.primitive(algebra.widened(a, 16))(Elementary.ilogb(_)))
      case 32 => sinks.unary(ids)(a => algebra.primitive(algebra.widened(a, 32))(Elementary.ilogb(_)))
      case _  => sinks.unary(ids)(a => algebra.primitive(a)(Elementary.ilogb(_)))
    }

  /** nan: a quiet NaN carrying the low bits of an integer payload. */
  inline def nan[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64)
  )(ids: OperandIds): R =
    width.raw match {
      case 16 =>
        sinks.broadcast(ids)(a => algebra.or(algebra.literal(0x7e00L), algebra.and(a, algebra.literal(0x1ffL))))
      case 32 =>
        sinks.broadcast(ids)(a => algebra.or(algebra.literal(0x7fc00000L), algebra.and(a, algebra.literal(0x3fffffL))))
      case _ => sinks.broadcast(ids)(a =>
          algebra.or(algebra.literal(0x7ff8000000000000L), algebra.and(a, algebra.literal(0x7ffffffffffffL)))
        )
    }

  /** bitselect over a, b and c: the bits of b where c is set, and of a elsewhere. */
  inline def bitselect[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R])(ids: OperandIds): R =
    sinks.ternary(ids)((a, b, c) => algebra.blend(c, b, a))

  /** select over a, b and c: b where c holds, and a elsewhere. A vector condition holds when its sign bit at
    * `conditionWidth` is set, a scalar one when it is non-zero.
    */
  inline def select[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      conditionWidth: Int in 1 ~ 64,
      scalar: Boolean
  )(
      ids: OperandIds
  ): R =
    if (scalar) sinks.ternary(ids)((a, b, c) => algebra.select(c, b, a))
    else sinks.ternary(ids)((a, b, c) =>
      algebra.select(algebra.and(c, algebra.literal(1L << (conditionWidth - 1))), b, a)
    )

  /** Functions of one operand with a second result that a pointer receives. */
  inline def pointer[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      opcode: Int,
      width: Int in (16, 32, 64)
  )(
      ids: OperandIds
  )(otherwise: => R): R = opcode match {
    case OpenCL.Sincos =>
      pairDouble(algebra, sinks, width, structure = false)(Elementary.sin(_), Elementary.cos(_))(ids)
    case OpenCL.LgammaR => width.raw match {
        case 16 => lgammaAt(algebra, sinks, 16)(ids)
        case 32 => lgammaAt(algebra, sinks, 32)(ids)
        case _  => sinks.store(ids)(
            a => algebra.primitive(a)(Elementary.lgamma(_)),
            a => algebra.primitive(a)(Elementary.lgammaSign(_))
          )
      }
    case OpenCL.Frexp => frexp(algebra, sinks, width, 32, structure = false)(ids)
    case OpenCL.Modf  => modf(algebra, sinks, width, structure = false)(ids)
    case OpenCL.Fract =>
      pairDouble(algebra, sinks, width, structure = false)(Elementary.fract(_, width), Elementary.floor(_))(ids)
    case _ => otherwise
  }

  private inline def lgammaAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      ids: OperandIds
  ): R = sinks.store(ids)(
    a => algebra.rounded(algebra.primitive(algebra.widened(a, bits))(Elementary.lgamma(_)), bits),
    a => algebra.primitive(algebra.widened(a, bits))(Elementary.lgammaSign(_))
  )

  /** Two results per lane, as a structure or with the second stored through a pointer. */
  inline def pair[V, R](inline sinks: SPIRVSinks[V, R], ids: OperandIds, inline structure: Boolean)(
      inline first: V => V,
      inline second: V => V
  ): R = inline if (structure) sinks.structure(ids)(first, second) else sinks.store(ids)(first, second)

  /** Two binary64 functions of one operand, each rounded once to `width`, as a [[pair]]. */
  inline def pairDouble[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      inline structure: Boolean
  )(inline first: Float64 => Float64, inline second: Float64 => Float64)(ids: OperandIds): R = width.raw match {
    case 16 => pairDoubleAt(algebra, sinks, 16, structure)(first, second)(ids)
    case 32 => pairDoubleAt(algebra, sinks, 32, structure)(first, second)(ids)
    case _  => pair(sinks, ids, structure)(a => algebra.primitive(a)(first), a => algebra.primitive(a)(second))
  }

  private inline def pairDoubleAt[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline bits: Int,
      inline structure: Boolean
  )(inline first: Float64 => Float64, inline second: Float64 => Float64)(ids: OperandIds): R =
    pair(sinks, ids, structure)(
      a => algebra.rounded(algebra.primitive(algebra.widened(a, bits))(first), bits),
      a => algebra.rounded(algebra.primitive(algebra.widened(a, bits))(second), bits)
    )

  /** frexp: the significand in [0.5, 1) and the exponent as an `integerWidth`-bit integer. */
  inline def frexp[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      integerWidth: Int in 1 ~ 64,
      inline structure: Boolean
  )(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => frexpAt(algebra, sinks, 16, integerWidth, structure)(ids)
    case 32 => frexpAt(algebra, sinks, 32, integerWidth, structure)(ids)
    case _  => pair(sinks, ids, structure)(
        a => algebra.primitive(a)(Elementary.frexp(_)),
        a => algebra.truncate(algebra.primitive(a)(Elementary.frexpExponent(_)), integerWidth)
      )
  }

  private inline def frexpAt[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      inline bits: Int,
      integerWidth: Int in 1 ~ 64,
      inline structure: Boolean
  )(ids: OperandIds): R = pair(sinks, ids, structure)(
    a => algebra.rounded(algebra.primitive(algebra.widened(a, bits))(Elementary.frexp(_)), bits),
    a => algebra.truncate(algebra.primitive(algebra.widened(a, bits))(Elementary.frexpExponent(_)), integerWidth)
  )

  /** modf: the fractional part and the integral part, each with the operand's sign. */
  inline def modf[V, R](
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      width: Int in (16, 32, 64),
      inline structure: Boolean
  )(
      ids: OperandIds
  ): R = pairDouble(algebra, sinks, width, structure)(Elementary.modf(_), Elementary.trunc(_))(ids)

  /** remquo: the IEEE remainder, and the low bits of the quotient with its sign that a pointer receives. */
  inline def remquo[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => remquoAt(algebra, sinks, 16)(ids)
    case 32 => remquoAt(algebra, sinks, 32)(ids)
    case _  => sinks.binaryStore(ids)(
        (a, b) => algebra.primitive(a, b)(Elementary.remainder(_, _)),
        (a, b) => algebra.primitive(a, b)(Elementary.remquo(_, _))
      )
  }

  private inline def remquoAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      ids: OperandIds
  ): R = sinks.binaryStore(ids)(
    (a, b) =>
      algebra.rounded(
        algebra.primitive(algebra.widened(a, bits), algebra.widened(b, bits))(Elementary.remainder(_, _)),
        bits
      ),
    (a, b) => algebra.primitive(algebra.widened(a, bits), algebra.widened(b, bits))(Elementary.remquo(_, _))
  )

  /** length: the Euclidean norm, accumulated in binary64 and rounded once to the width. */
  inline def length[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => lengthAt(algebra, sinks, 16)(ids)
    case 32 => lengthAt(algebra, sinks, 32)(ids)
    case _  =>
      sinks.fold(ids)(algebra.literal(0L), (sum, a) => algebra.primitive(sum, a)(Elementary.norm(_, _)), sum => sum)
  }

  private inline def lengthAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      ids: OperandIds
  ): R = sinks.fold(ids)(
    algebra.literal(0L),
    (sum, a) => algebra.primitive(sum, algebra.widened(a, bits))(Elementary.norm(_, _)),
    algebra.rounded(_, bits)
  )

  /** normalize: each lane over the Euclidean norm, scaled by the largest magnitude so the norm neither overflows nor
    * underflows, in binary64 and rounded once to the width.
    */
  inline def normalize[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => normalizeAt(algebra, sinks, 16)(ids)
    case 32 => normalizeAt(algebra, sinks, 32)(ids)
    case _  => sinks.normalised(ids)(
        (scale, a) => algebra.primitive(scale, a)(Elementary.magnitude(_, _)),
        (sum, a, scale) => algebra.primitive(sum, a, scale)(Elementary.scaledNorm(_, _, _)),
        (a, scale, norm) => algebra.primitive(a, scale, norm)(Elementary.normalised(_, _, _))
      )
  }

  private inline def normalizeAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      ids: OperandIds
  ): R = sinks.normalised(ids)(
    (scale, a) => algebra.primitive(scale, algebra.widened(a, bits))(Elementary.magnitude(_, _)),
    (sum, a, scale) => algebra.primitive(sum, algebra.widened(a, bits), scale)(Elementary.scaledNorm(_, _, _)),
    (a, scale, norm) =>
      algebra.rounded(algebra.primitive(algebra.widened(a, bits), scale, norm)(Elementary.normalised(_, _, _)), bits)
  )

  /** distance: the Euclidean norm of the difference, accumulated in binary64 and rounded once to the width. */
  inline def distance[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], width: Int in (16, 32, 64))(
      ids: OperandIds
  ): R = width.raw match {
    case 16 => distanceAt(algebra, sinks, 16)(ids)
    case 32 => distanceAt(algebra, sinks, 32)(ids)
    case _  => sinks.binaryFold(ids)(
        algebra.literal(0L),
        (sum, a, b) =>
          algebra.primitive(sum, algebra.primitive(a, b)(Elementary.difference(_, _)))(Elementary.norm(_, _)),
        sum => sum
      )
  }

  private inline def distanceAt[V, R](inline algebra: Algebra[V], inline sinks: SPIRVSinks[V, R], inline bits: Int)(
      ids: OperandIds
  ): R = sinks.binaryFold(ids)(
    algebra.literal(0L),
    (sum, a, b) =>
      algebra.primitive(
        sum,
        algebra.primitive(algebra.widened(a, bits), algebra.widened(b, bits))(Elementary.difference(_, _))
      )(Elementary.norm(_, _)),
    algebra.rounded(_, bits)
  )
}
