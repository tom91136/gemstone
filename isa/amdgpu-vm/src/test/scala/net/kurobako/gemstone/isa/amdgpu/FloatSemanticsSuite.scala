package net.kurobako.gemstone.isa.amdgpu

import FloatPrimitives.*
import FloatSemantics.*
import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import scala.util.Random

class FloatSemanticsSuite extends munit.FunSuite {
  private val random = Random(23)

  private def f32(value: Float): Long  = IEEE754.floatToBinary32(value).toLong
  private def f64(value: Double): Long = IEEE754.doubleToBinary64(value)
  private def f16(value: Float): Long  = Bits.low16(IEEE754.floatToBinary16(value).toLong).toLong

  private val singles: Vector[Long] = Vector(
    0L,
    0x80000000L,
    f32(1.0f),
    f32(-1.0f),
    f32(0.5f),
    f32(0.25f),
    f32(1.5f),
    f32(2.0f),
    f32(-2.5f),
    f32(3.0e9f),
    f32(-3.0e9f),
    f32(2147483648.0f),
    f32(4294967296.0f),
    f32(65504.0f),
    f32(70000.0f),
    f32(1.0e-40f),
    f32(-1.0e-40f),
    f32(java.lang.Float.MIN_NORMAL),
    f32(Float.MaxValue),
    f32(Float.MinPositiveValue),
    0x7f800000L,
    0xff800000L,
    0x7fc00000L,
    0x7f800001L,
    0xffc00001L,
    f32(0.75f),
    f32(1.0e30f),
    f32(-7.25f)
  ) ++ Vector.fill(40)(random.nextLong() & 0xffffffffL) ++ Vector.fill(20)(f32(random.nextFloat() * 4.0f - 2.0f))

  private lazy val singlesAndHalves: Vector[Long] = singles ++ halves

  private val doubles: Vector[Long] = Vector(
    0L,
    Long.MinValue,
    f64(1.0),
    f64(-1.0),
    f64(0.5),
    f64(1.5),
    f64(2.0),
    f64(-2.5),
    f64(3.0e9),
    f64(-3.0e9),
    f64(2147483648.0),
    f64(4294967296.0),
    f64(1.0e-310),
    f64(-1.0e-310),
    f64(java.lang.Double.MIN_NORMAL),
    f64(Double.MaxValue),
    f64(1.0e300),
    f64(1.0e-300),
    0x7ff0000000000000L,
    0xfff0000000000000L,
    0x7ff8000000000000L,
    0x7ff0000000000001L,
    f64(0.75),
    f64(-7.25)
  ) ++ Vector.fill(30)(random.nextLong()) ++ Vector.fill(20)(f64(random.nextDouble() * 4.0 - 2.0))

  private val halves: Vector[Long] =
    Vector(
      0L,
      0x8000L,
      0x3c00L,
      0xbc00L,
      0x3800L,
      0x3e00L,
      0x7bffL,
      0x0001L,
      0x8001L,
      0x0400L,
      0x7c00L,
      0xfc00L,
      0x7e00L,
      0x7c01L,
      f16(0.75f),
      f16(-7.25f)
    ) ++ Vector.fill(30)(random.nextLong() & 0xffffL)

  /** Equal bits, or NaN at `width` on both sides: the JVM leaves the payload of a NaN from NaN operands unspecified. */
  private def same(staged: Long, reference: Long, width: Int in (16, 32, 64), clue: => String)(using
      munit.Location
  )
      : Unit =
    if (!(IEEE754.isNaN(staged & widthMask(width), width) && IEEE754.isNaN(reference & widthMask(width), width)))
      assertEquals(staged, reference, clue)

  private def widthMask(width: Int in (16, 32, 64)): Long = if (width == 64) -1L else (1L << width) - 1L

  private val narrowing = Set(
    VectorConvertOperation.FrexpExponentF64,
    VectorConvertOperation.I32FromF64,
    VectorConvertOperation.U32FromF64,
    VectorConvertOperation.F32FromF64
  )
  private val fromPair = Set(
    VectorConvertOperation.FrexpMantissaF64,
    VectorConvertOperation.ReciprocalF64,
    VectorConvertOperation.TruncateF64,
    VectorConvertOperation.FloorF64,
    VectorConvertOperation.FractionF64,
    VectorConvertOperation.RoundNearestEvenF64,
    VectorConvertOperation.ReciprocalSquareRootF64,
    VectorConvertOperation.CeilF64,
    VectorConvertOperation.SquareRootF64,
    VectorConvertOperation.ReciprocalClampF64,
    VectorConvertOperation.ReciprocalSquareRootClampF64
  )
  private val widening = Set(
    VectorConvertOperation.F64FromI32,
    VectorConvertOperation.F64FromU32,
    VectorConvertOperation.F64FromF32,
    VectorConvertOperation.PairF32FromE4M3,
    VectorConvertOperation.PairF32FromE5M2,
    VectorConvertOperation.PairF32FromE4M3Fnuz,
    VectorConvertOperation.PairF32FromE5M2Fnuz
  )
  private val toHalf = Set(
    VectorConvertOperation.F16FromF32,
    VectorConvertOperation.CeilF16,
    VectorConvertOperation.FloorF16,
    VectorConvertOperation.TruncateF16,
    VectorConvertOperation.RoundNearestEvenF16,
    VectorConvertOperation.FractionF16,
    VectorConvertOperation.SquareRootF16,
    VectorConvertOperation.ReciprocalF16,
    VectorConvertOperation.ReciprocalSquareRootF16,
    VectorConvertOperation.ExponentBase2F16,
    VectorConvertOperation.LogarithmBase2F16,
    VectorConvertOperation.SineF16,
    VectorConvertOperation.CosineF16,
    VectorConvertOperation.SineBoundedF16,
    VectorConvertOperation.CosineBoundedF16,
    VectorConvertOperation.FrexpMantissaF16,
    VectorConvertOperation.F16FromI16,
    VectorConvertOperation.F16FromU16
  )
  private val fromHalf = toHalf - VectorConvertOperation.F16FromF32 ++ Set(
    VectorConvertOperation.F32FromF16,
    VectorConvertOperation.I16FromF16,
    VectorConvertOperation.FrexpExponentF16,
    VectorConvertOperation.U16FromF16,
    VectorConvertOperation.SignedNormFromF16,
    VectorConvertOperation.UnsignedNormFromF16,
    VectorConvertOperation.I32FromI16,
    VectorConvertOperation.U32FromU16,
    VectorConvertOperation.F32FromBF16
  )

  private def form(operation: VectorConvertOperation): Long =
    if (narrowing(operation)) 3L
    else if (fromPair(operation)) 4L
    else if (widening(operation)) 2L
    else if (fromHalf(operation)) 16L
    else 32L

  private def resultWidth(operation: VectorConvertOperation): Int in (16, 32, 64) =
    if (fromPair(operation) || widening(operation)) 64
    else if (toHalf(operation)) 16
    else 32

  test("staged conversions read their operand form and match the interpreter's formulas") {
    for {
      operation <- VectorConvertOperation.values.toVector
      clamp     <- Vector(false, true)
      source    <- if (form(operation) >= 3) doubles else singlesAndHalves
    } {
      val (staged, read) =
        FloatSemantics.convert(
          Execute,
          operation,
          clamp
        )(Values(source), 0, Values.Unused, SourceModifiers.Unmodified)
      assertEquals(read, form(operation), s"$operation")
      val reference = references.convert(operation, source, clamp)
      // the integer results are never NaN, so the exemption is for floating-point results alone
      same(staged, reference, resultWidth(operation), s"$operation clamp=$clamp source=0x${source.toHexString}")
    }
  }

  test("staged floating-point comparisons match the interpreter's for every kind and relation") {
    def operands(kind: VectorComparisonKind): Vector[Long] = kind match {
      case VectorComparisonKind.Float16 | VectorComparisonKind.ClassFloat16 => halves
      case VectorComparisonKind.Float64 | VectorComparisonKind.ClassFloat64 => doubles.take(40)
      case _                                                                => singles.take(40)
    }
    for {
      kind <- Vector(
        VectorComparisonKind.Float16,
        VectorComparisonKind.Float32,
        VectorComparisonKind.Float64,
        VectorComparisonKind.ClassFloat16,
        VectorComparisonKind.ClassFloat32,
        VectorComparisonKind.ClassFloat64
      )
      relation <- VectorComparisonRelation.values.toVector
      a        <- operands(kind)
      b        <- if (kind.toString.startsWith("Class")) Vector.tabulate(10)(1L << _) ++ Vector(0x3ffL, 0L, 0x1234L)
      else operands(kind)
    } {
      val (test, swap) = (relation.floatTest, relation.floatSwapped)
      val sinks        = Values(a, b)
      val staged       = FloatSemantics.compare(Execute, kind, test, swap)(
        sinks,
        scalar = false,
        0,
        Values.Unused,
        Values.Unused,
        SourceModifiers.Unmodified,
        None,
        false
      )(fail("unstaged"))._1
      assertEquals(
        staged != 0L,
        references.compare(VectorComparison(kind, relation, updateExec = false), a, b),
        s"$kind $relation"
      )
    }
  }

  // The interpreter's VOP2 float formulas before staging, the source modifiers applied.
  private def floatReference(operation: VectorBinaryOperation, clamp: Boolean, x: Long, y: Long, z: Long): Long = {
    def single(value: Float): Long =
      IEEE754.floatToBinary32(if (clamp) references.clampF32(value) else value).toLong
    def f(bits: Long): Float = IEEE754.binary32ToFloat(Bits.low32(bits))
    def h(bits: Long): Float = IEEE754.binary16ToFloat(Bits.low16(bits))
    operation match {
      case VectorBinaryOperation.AddF32             => single(f(x) + f(y))
      case VectorBinaryOperation.SubtractF32        => single(f(x) - f(y))
      case VectorBinaryOperation.SubtractReverseF32 => single(f(y) - f(x))
      case VectorBinaryOperation.MultiplyF32        => single(f(x) * f(y))
      // DX9 rules: a zero operand gives +0
      case VectorBinaryOperation.MultiplyLegacyF32 =>
        if (f(x) == 0.0f || f(y) == 0.0f) single(0.0f) else single(f(x) * f(y))
      case VectorBinaryOperation.MinimumF32 =>
        if (clamp) single(f(IEEE754.minNum(x, y, 32))) else IEEE754.minNum(x, y, 32)
      case VectorBinaryOperation.MaximumF32 =>
        if (clamp) single(f(IEEE754.maxNum(x, y, 32))) else IEEE754.maxNum(x, y, 32)
      // DX9 selections, a NaN comparing false
      case VectorBinaryOperation.MinimumLegacyF32 =>
        val chosen = Bits.low32(if (f(x) < f(y)) x else y).toLong
        if (clamp) single(f(chosen)) else chosen
      case VectorBinaryOperation.MaximumLegacyF32 =>
        val chosen = Bits.low32(if (f(x) >= f(y)) x else y).toLong
        if (clamp) single(f(chosen)) else chosen
      case VectorBinaryOperation.MultiplyAccumulateF32 => IEEE754.floatToBinary32(f(x) * f(y) + f(z)).toLong
      case VectorBinaryOperation.LoadExponentF32       => single(Math.scalb(f(x), y.toInt))
      case VectorBinaryOperation.MinimumF16            => references.clampF16(IEEE754.minNum(x, y, 16), clamp)
      case VectorBinaryOperation.MaximumF16            => references.clampF16(IEEE754.maxNum(x, y, 16), clamp)
      case VectorBinaryOperation.MinimumPropagateF32   =>
        if (clamp) single(f(IEEE754.minimum(x, y, 32))) else IEEE754.minimum(x, y, 32)
      case VectorBinaryOperation.MaximumPropagateF32 =>
        if (clamp) single(f(IEEE754.maximum(x, y, 32))) else IEEE754.maximum(x, y, 32)
      case VectorBinaryOperation.MinimumPropagateF16 => references.clampF16(IEEE754.minimum(x, y, 16), clamp)
      case VectorBinaryOperation.MaximumPropagateF16 => references.clampF16(IEEE754.maximum(x, y, 16), clamp)
      case VectorBinaryOperation.AddF16              =>
        references.clampF16(IEEE754.floatToBinary16(h(x) + h(y)).toLong, clamp)
      case VectorBinaryOperation.MultiplyF16 =>
        references.clampF16(IEEE754.floatToBinary16(h(x) * h(y)).toLong, clamp)
      case VectorBinaryOperation.FusedMultiplyAccumulateF16 =>
        references.clampF16(IEEE754.fusedMultiplyAdd(x, y, z, 16), clamp)
      case VectorBinaryOperation.PackRoundTowardZeroF16 =>
        IEEE754.fromDouble(f(x).toDouble, 16, IEEE754.Rounding.TowardZero) |
          (IEEE754.fromDouble(f(y).toDouble, 16, IEEE754.Rounding.TowardZero) << 16)
      case VectorBinaryOperation.SubtractF16 =>
        references.clampF16(IEEE754.floatToBinary16(h(x) - h(y)).toLong, clamp)
      case VectorBinaryOperation.SubtractReverseF16 =>
        references.clampF16(IEEE754.floatToBinary16(h(y) - h(x)).toLong, clamp)
      case VectorBinaryOperation.LoadExponentF16 =>
        references.clampF16(IEEE754.floatToBinary16(Math.scalb(h(x).toDouble, y.toShort.toInt).toFloat).toLong, clamp)
      case other => fail(s"$other has no reference")
    }
  }

  test("staged VOP2 float operations match the interpreter's formulas, saturated or not") {
    val zeros = Values(0L, 0L, 0L)
    val ones  = (-1L, -1L)
    for {
      operation <- VectorBinaryOperation.values.toVector
      clamp     <- Vector(false, true)
      if FloatSemantics.vectorFloat(Execute, operation, clamp)(
        zeros,
        0,
        Values.Unused,
        Values.Unused,
        SourceModifiers.Unmodified
      )(ones)._2 != -1L
      width  = FloatSemantics.floatWidth(operation)
      inputs = if (width == 16) halves else singles.take(36)
      a <- inputs
      b <- if (operation == VectorBinaryOperation.LoadExponentF32) Vector(0L, 1L, 0xffffffffL, 200L, 0xffffff00L)
      else inputs
      c <- inputs.take(3)
    } {
      val sinks  = Values(a, b, c)
      val staged = FloatSemantics.vectorFloat(Execute, operation, clamp)(
        sinks,
        0,
        Values.Unused,
        Values.Unused,
        SourceModifiers.Unmodified
      )(fail("unstaged"))._1
      same(staged, floatReference(operation, clamp, a, b, c), width, s"$operation clamp=$clamp a=$a b=$b c=$c")
    }
  }

  test("staged 64-bit binaries match the interpreter's formulas") {
    def reference(operation: VectorBinary64Operation, a: Long, b: Long): Long = operation match {
      case VectorBinary64Operation.LoadExponent =>
        IEEE754.doubleToBinary64(Math.scalb(IEEE754.binary64ToDouble(a), b.toInt))
      case VectorBinary64Operation.Add =>
        IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(a) + IEEE754.binary64ToDouble(b))
      case VectorBinary64Operation.Multiply =>
        IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(a) * IEEE754.binary64ToDouble(b))
      case VectorBinary64Operation.Maximum          => IEEE754.maxNum(a, b, 64)
      case VectorBinary64Operation.Minimum          => IEEE754.minNum(a, b, 64)
      case VectorBinary64Operation.MaximumPropagate => IEEE754.maximum(a, b, 64)
      case VectorBinary64Operation.MinimumPropagate => IEEE754.minimum(a, b, 64)
      case VectorBinary64Operation.AddU64           => a + b
      case VectorBinary64Operation.SubtractU64      => a - b
      case VectorBinary64Operation.MultiplyLowU64   => a * b
      case VectorBinary64Operation.MaximumI64       => math.max(a, b)
      case VectorBinary64Operation.MinimumI64       => math.min(a, b)
      case VectorBinary64Operation.MaximumU64       => if (java.lang.Long.compareUnsigned(a, b) >= 0) a else b
      case VectorBinary64Operation.MinimumU64       => if (java.lang.Long.compareUnsigned(a, b) <= 0) a else b
    }
    for {
      operation <- VectorBinary64Operation.values.toVector
      a         <- doubles
      b <- if (operation == VectorBinary64Operation.LoadExponent) Vector(0L, 1L, -1L, 2000L, -2000L, 0xffffffffL)
      else doubles
      sinks = Values(a, b)
    } same(
      FloatSemantics.binary64(
        Execute,
        operation
      )(sinks, 0, Values.Unused, Values.Unused, SourceModifiers.Unmodified)._1,
      reference(operation, a, b),
      64,
      s"$operation $a $b"
    )
  }

  test("staged multiply-adds match the interpreter's formulas") {
    val inputs = singles.take(24)
    for {
      operation <- VectorMadF32Operation.values.toVector
      clamp     <- Vector(false, true)
      a         <- inputs
      b         <- inputs
      c         <- inputs.take(8)
    } {
      val (x, y, z) = (
        IEEE754.binary32ToFloat(Bits.low32(a)),
        IEEE754.binary32ToFloat(Bits.low32(b)),
        IEEE754.binary32ToFloat(Bits.low32(c))
      )
      // DX9 rules take a zero factor's product as +0
      val zero   = x == 0f || y == 0f
      val result = operation match {
        case VectorMadF32Operation.FusedMultiplyAdd       => IEEE754.fma(x, y, z)
        case VectorMadF32Operation.FusedMultiplyAddLegacy => if (zero) 0f + z else IEEE754.fma(x, y, z)
        case VectorMadF32Operation.MultiplyAddLegacy      => (if (zero) 0f else x * y) + z
        case VectorMadF32Operation.MultiplyAdd            => x * y + z
        // R600's MUL_LIT
        case VectorMadF32Operation.MultiplyLighting =>
          if (y == -Float.MaxValue || y == Float.NegativeInfinity || y.isNaN || !(z > 0f)) -Float.MaxValue
          else if (zero) 0f
          else x * y
      }
      val sinks = Values(a, b, c)
      same(
        FloatSemantics.multiplyAdd(Execute, operation, clamp)(
          sinks,
          0,
          Values.Unused,
          Values.Unused,
          Values.Unused,
          SourceModifiers.Unmodified
        )._1,
        IEEE754.floatToBinary32(if (clamp) references.clampF32(result) else result).toLong,
        32,
        s"$operation clamp=$clamp"
      )
    }
    for {
      a <- doubles.take(24)
      b <- doubles.take(24)
      c <- doubles.take(8)
    } same(
      Execute.fusedMultiplyAdd(a, b, c, 64),
      IEEE754.doubleToBinary64(
        IEEE754.fma(IEEE754.binary64ToDouble(a), IEEE754.binary64ToDouble(b), IEEE754.binary64ToDouble(c))
      ),
      64,
      s"fma.f64 $a $b $c"
    )
  }

  test("staged division steps match the interpreter's formulas") {
    def float(bits: Long): Float = IEEE754.binary32ToFloat(Bits.low32(bits))
    val inputs                   = singles.take(28) ++ Vector(f32(1.0e-38f), f32(1.0e38f), f32(1.0e-30f), f32(3.0e-39f))
    for {
      a <- inputs
      b <- inputs
      c <- inputs
    } {
      same(
        Execute.divisionFixup(32, a, b, c),
        VMArithmetic.divisionFixup(a, b, c, 32),
        32,
        s"fixup $a $b $c"
      )
      val (scaled, flag) = (
        Execute.divisionScale(32, a, b, c),
        Execute.divisionScaleFlag(32, b, c) != 0L
      )
      val reference = references.divisionScaleF32(float(a), float(b), float(c))
      same(scaled, IEEE754.floatToBinary32(reference._1).toLong, 32, s"scale $a $b $c")
      assertEquals(flag, reference._2, s"scale flag $a $b $c")
      for (vcc <- Vector(0L, 1L)) {
        val fused = IEEE754.floatToBinary32(IEEE754.fma(float(a), float(b), float(c))).toLong
        same(
          Execute.divisionFusedMultiplyAdd(32, a, b, c, vcc),
          if (vcc != 0L && Vector(a, b, c).forall(x => float(x).isFinite))
            references.divisionFusedMultiplyAdd(float(a).toDouble, float(b).toDouble, float(c).toDouble, 32)
          else fused,
          32,
          s"fmas $a $b $c $vcc"
        )
      }
    }
    def double(bits: Long): Double = IEEE754.binary64ToDouble(bits)
    val wide                       = doubles.take(24) ++ Vector(f64(1.0e-307), f64(1.0e307), f64(1.0e-300))
    for {
      a <- wide
      b <- wide
      c <- wide
    } {
      same(
        Execute.divisionFixup(64, a, b, c),
        references.divisionFixupF64(a, b, c),
        64,
        s"fixup $a $b $c"
      )
      val (scaled, flag) = (
        Execute.divisionScale(64, a, b, c),
        Execute.divisionScaleFlag(64, b, c) != 0L
      )
      val reference = references.divisionScaleF64(double(a), double(b), double(c))
      same(scaled, IEEE754.doubleToBinary64(reference._1), 64, s"scale $a $b $c")
      assertEquals(flag, reference._2, s"scale flag $a $b $c")
      for (vcc <- Vector(0L, 1L)) {
        val fused = IEEE754.doubleToBinary64(IEEE754.fma(double(a), double(b), double(c)))
        same(
          Execute.divisionFusedMultiplyAdd(64, a, b, c, vcc),
          if (vcc != 0L && Vector(a, b, c).forall(x => double(x).isFinite))
            references.divisionFusedMultiplyAdd(double(a), double(b), double(c), 64)
          else fused,
          64,
          s"fmas $a $b $c $vcc"
        )
      }
    }
  }

  test("staged dot products and packed operations match the interpreter's formulas") {
    def packed: Long = (random.nextLong() & 0xffffL) | ((halves(random.nextInt(halves.size)) & 0xffffL) << 16)
    for (_ <- 0 until 2000) {
      val (a, b, d) = (packed, packed, singles(random.nextInt(singles.size)))
      same(
        toRaw(Dots.halvesToFloat(fromRaw[UInt[32]](a), fromRaw[UInt[32]](b), fromRaw[Float32](d))),
        references.dot2F32F16(a, b, d).toLong,
        32,
        s"dot2 $a $b $d"
      )
      val (x, y, z) = (random.nextLong(), random.nextLong(), random.nextLong())
      assertEquals(Execute.dotBytes(x, y, z), references.dot4U32U8(x, y, z))
    }
    for {
      operation <- PackedF32Operation.values.toVector
      instruction: Instruction.VPackedF32 =
        Instruction.VPackedF32(operation, 0, Values.Unused, Values.Unused, Values.Unused, 0, 0, 0, 0)
      selection <- 0 until 8
      negate    <- 0 until 8
      _         <- 0 until 20
    } {
      val (a, b, c) = (
        singles(random.nextInt(singles.size)) | (singles(random.nextInt(singles.size)) << 32),
        singles(random.nextInt(singles.size)) | (singles(random.nextInt(singles.size)) << 32),
        singles(random.nextInt(singles.size)) | (singles(random.nextInt(singles.size)) << 32)
      )
      // the lane loop's operand selection and negation
      def operand(value: Long, index: Int): Long =
        Bits.low32(value >>> (((selection >>> index) & 1) * 32)).toLong ^
          (if ((negate & (1 << index)) != 0) 0x80000000L else 0L)
      val sinks = Values(operand(a, 0), operand(b, 1), operand(c, 2))
      same(
        FloatSemantics.packedSingle(Execute, instruction)(sinks)._1,
        references.packedSingle(operation, a, b, c, selection, negate),
        32,
        s"$operation $selection $negate"
      )
    }
    for {
      lowSelection  <- 0 until 8
      highSelection <- 0 until 8
      negate        <- Vector(0, 1, 2, 4, 7)
      clamp         <- Vector(false, true)
      _             <- 0 until 6
    } {
      val (a, b, c)                                                          = (packed, packed, packed)
      def half(value: Long, selection: Int, negation: Int, index: Int): Long =
        Bits.low16(if ((selection & (1 << index)) != 0) value >>> 16 else value).toLong ^
          (if ((negation & (1 << index)) != 0) 0x8000L else 0L)
      def fma(selection: Int, negation: Int): Long =
        Execute.packedHalfFusedMultiplyAdd(
          (0xf << ModeRegister.DenormalShift).toLong,
          clamp,
          half(a, selection, negation, 0),
          half(b, selection, negation, 1),
          half(c, selection, negation, 2)
        )
      val reference = references.packedFmaF16(a, b, c, lowSelection, highSelection, negate, 7 - negate, clamp)
      same(fma(lowSelection, negate), reference & 0xffffL, 16, s"pk_fma low $lowSelection")
      same(fma(highSelection, 7 - negate), reference >>> 16, 16, s"pk_fma high $highSelection")
    }
  }

  test("staged half packing matches the interpreter's formula") {
    for {
      selection <- 0 until 4
      absolute  <- 0 until 4
      negate    <- 0 until 4
      modifiers = SourceModifiers(absolute, negate)
      clamp <- Vector(false, true)
      _     <- 0 until 20
    } {
      val (a, b)                               = (random.nextLong() & 0xffffffffL, random.nextLong() & 0xffffffffL)
      def half(source: Long, index: Int): Long =
        modifiers(source >>> (if ((selection & (1 << index)) != 0) 16 else 0), index, 16)
      val reference = Bits.low16(references.clampF16(half(a, 0), clamp)).toLong |
        (Bits.low16(references.clampF16(half(b, 1), clamp)).toLong << 16)
      val staged = Execute.packHalves(clamp, half(a, 0), half(b, 1))
      same(staged & 0xffffL, reference & 0xffffL, 16, "low")
      same(staged >>> 16, reference >>> 16, 16, "high")
    }
  }

  test("staged mixed-precision FMAs match the interpreter's formulas") {
    def mixedSource(): Long = (random.nextInt(3): @unchecked) match {
      case 0 => singles(random.nextInt(singles.size))
      case 1 => (halves(random.nextInt(halves.size)) << 16) | halves(random.nextInt(halves.size))
      case 2 => random.nextLong() & 0xffffffffL
    }
    for {
      selection     <- 0 until 8
      selectionHigh <- 0 until 8
      negate        <- Vector(0, 5, 2)
      absolute      <- Vector(0, 3, 4)
      clamp         <- Vector(false, true)
      fused         <- Vector(false, true)
      _             <- 0 until 3
    } {
      val (a, b, c, d) = (mixedSource(), mixedSource(), mixedSource(), random.nextLong() & 0xffffffffL)
      for (high <- Vector(false, true)) {
        val staged = Execute.mixedHalf(
          selection = selection,
          selectionHigh = selectionHigh,
          negate = negate,
          absolute = absolute,
          clamp = clamp,
          high = high,
          fused = fused,
          a = a,
          b = b,
          c = c,
          d = d
        )
        val reference = references.fmaMixF16(
          existing = d,
          srcA = a,
          srcB = b,
          srcC = c,
          opSelection = selection,
          opSelectionHi = selectionHigh,
          negate = negate,
          absolute = absolute,
          clamp = clamp,
          high = high,
          fused = fused
        )
        val shift = if (high) 16 else 0
        same((staged >>> shift) & 0xffffL, (reference >>> shift) & 0xffffL, 16, s"mix f16 $selection $selectionHigh")
        assertEquals(staged & ~(0xffffL << shift), reference & ~(0xffffL << shift))
      }
      same(
        Execute.mixedSingle(
          selection = selection,
          selectionHigh = selectionHigh,
          negate = negate,
          absolute = absolute,
          clamp = clamp,
          fused = fused,
          a = a,
          b = b,
          c = c
        ),
        references.fmaMixF32(
          srcA = a,
          srcB = b,
          srcC = c,
          opSelection = selection,
          opSelectionHi = selectionHigh,
          negate = negate,
          absolute = absolute,
          clamp = clamp,
          fused = fused
        ),
        32,
        s"mix f32 $selection $selectionHigh"
      )
    }
  }

  test("staged dual-issue halves match the interpreter's formula") {
    val operands = singles.take(30) ++ Vector(-1L, 0x1_0000_0005L, 31L, 32L)
    for {
      operation <- DualVectorOperation.values.toVector
      half = DualVector(operation, 0, Values.Unused, Values.Unused, Values.Unused)
      a     <- operands
      b     <- operands
      carry <- Vector(false, true)
    } {
      val addend = singles(random.nextInt(singles.size))
      val sinks  = Values(a, b, addend, if (carry) 1L else 0L)
      same(
        FloatSemantics.dual(Execute, half, half)(sinks, second = false)._1,
        references.dual(operation, a, b, addend, carry),
        if (operation.wide) 64 else 32,
        s"$operation $a $b $carry"
      )
    }
  }

  test("staged output modifiers and result clamps match the interpreter's formulas") {
    for {
      width <- Vector[Int in (16, 32, 64)](16, 32, 64)
      bits  <- if (width == 64) doubles else if (width == 32) singles else halves
      clamp <- Vector(false, true)
      scale <- OutputScale.values.toVector
    } {
      val mask = widthMask(width)
      same(
        Execute.outputModifier(scale, width, clamp, bits) & mask,
        references.outputModified(bits, scale, width, clamp) & mask,
        width,
        s"omod $scale $width $clamp $bits"
      )
      same(
        Execute.saturate(bits, width) & mask,
        references.clampFloat(bits, width) & mask,
        width,
        s"clamp $width $bits"
      )
    }
  }

  test("staged trig preparation and reciprocal match the interpreter's formulas") {
    for {
      a       <- doubles
      segment <- Vector(0L, 1L, 5L, 31L, 32L, 0xffffffffL)
    } assertEquals(
      Execute.trigPreop(a, segment),
      IEEE754.doubleToBinary64(VMArithmetic.trigPreop(IEEE754.binary64ToDouble(a), segment.toInt))
    )
    for {
      a     <- singles
      clamp <- Vector(false, true)
    } {
      val value = references.reciprocalF32(IEEE754.binary32ToFloat(Bits.low32(a)))
      same(
        Execute.reciprocal(clamp, a),
        IEEE754.floatToBinary32(if (clamp) references.clampF32(value) else value).toLong,
        32,
        s"rcp $a"
      )
    }
  }
}
