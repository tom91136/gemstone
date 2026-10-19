package net.kurobako.gemstone.isa.amdgpu

import FloatSemantics.*
import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.semantics.*

class AMDGPUArithmeticSuite extends munit.FunSuite {

  /** The dword in `result` after `operations` on one gfx900 lane with `inputs` in v2 onwards. */
  private def run(inputs: Vec[Long], result: VectorRegisterIndex, operations: Instruction*): Int =
    laneRuns.laneResult(Target("gfx900"), inputs, Vec.from(operations), result).toInt

  private def packed(low: Float, high: Float): Long =
    Bits.low16(IEEE754.floatToBinary16(low).toLong).toLong |
      (Bits.low16(IEEE754.floatToBinary16(high).toLong).toLong << 16)

  test("trig preparation retains the 1200-bit reference calculation for every exponent and segment") {
    val reference = BigInt(
      "a2f9836e4e441529fc2757d1f534ddc0db6295993c439041fe5163abdebbc561b7246e3a424dd2e006492eea09d1921cfe1deb1cb129a73ee88235f52ebb4484e99c7026b45f7e413991d639835339f49c845f8bbdf9283b1ff897ffde05980fef2f118b5a0a6d1f6d367ecf27cb09b74f463f669e5fea2d7527bac7ebe5f17b3d0739f78a5292ea6bfb5fb11f8d5d0856033046fc7b",
      16
    )
    val mask = (BigInt(1) << 53) - 1
    for {
      exponent <- 0 until 2048
      segment  <- 0 until 32
    } {
      val shift    = segment * 53 + (exponent - 1077).max(0)
      val expected = Math.scalb(
        ((reference << shift) & mask).toDouble,
        -53 - shift + (if (exponent >= 1968) 128 else 0)
      )
      val input = IEEE754.binary64ToDouble(exponent.toLong << 52)
      assertEquals(
        Execute.trigPreop(IEEE754.doubleToBinary64(input), segment.toLong),
        IEEE754.doubleToBinary64(expected)
      )
    }
  }

  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  for (
    (name, target, bytes, expected) <- Seq[(String, Target, Vec[Int], Instruction)](
      (
        "gfx1030 SOP2 shift-left-two and add decodes",
        Target("gfx1030"),
        Vec(0x01, 0x02, 0x80, 0x97),
        Instruction.SBinary(
          ScalarBinaryOperation.ShiftLeftTwoAndAddU32,
          0,
          InstructionSource.Scalar(1),
          InstructionSource.Scalar(2)
        )
      ),
      (
        "gfx900 packed-half FMA decoding preserves low and high operand selection",
        Target("gfx900"),
        Vec(0x00, 0x40, 0x8e, 0xd3, 0x02, 0x09, 0x02, 0x0c),
        Instruction.VPackedTernary(
          0,
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(4),
          InstructionSource.VectorRegister(0),
          lowSelection = 0,
          highSelection = 5,
          negateLow = 0,
          negateHigh = 0,
          clamp = false
        )
      ),
      (
        "gfx1036 VOP3P packed-half FMA decodes all three sources",
        Target("gfx1036"),
        Vec(0x05, 0x40, 0x0e, 0xcc, 0x01, 0x05, 0x0e, 0x1c),
        Instruction.VPackedTernary(
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3),
          lowSelection = 0,
          highSelection = 7,
          negateLow = 0,
          negateHigh = 0,
          clamp = false
        )
      ),
      (
        "gfx1036 VOP3P mixed FMA decodes source selection and destination half",
        Target("gfx1036"),
        Vec(0x05, 0x00, 0x21, 0xcc, 0x01, 0x05, 0x0e, 0x00),
        Instruction.VFmaMixF16(
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.Scalar(3),
          opSelection = 0,
          opSelectionHi = 0,
          negate = 0,
          absolute = 0,
          clamp = false,
          high = false
        )
      ),
      (
        "gfx1036 VOP3P unsigned byte dot product decodes all three sources",
        Target("gfx1036"),
        Vec(0x05, 0x40, 0x17, 0xcc, 0x01, 0x05, 0x0e, 0x18),
        Instruction.VDot4U32U8(
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.Scalar(3)
        )
      ),
      (
        "gfx1036 VOP3 FMAC uses the destination as the accumulator",
        Target("gfx1036"),
        Vec(0x05, 0x00, 0x2b, 0xd5, 0x01, 0x05, 0x02, 0x00),
        Instruction.VMadF32(
          VectorMadF32Operation.FusedMultiplyAdd,
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(5),
          SourceModifiers.Unmodified,
          false
        )
      ),
      (
        "gfx1030 division fixup preserves folded absolute-value modifiers",
        Target("gfx1030"),
        Vec(0x18, 0x04, 0x60, 0xd5, 0x18, 0x25, 0x02, 0x04),
        Instruction.VDivFixupF64(
          24,
          InstructionSource.VectorRegister(24),
          InstructionSource.VectorRegister(18),
          InstructionSource.VectorRegister(0),
          absolute = 4,
          negate = 0
        )
      ),
      (
        "gfx1030 f64 FMA preserves folded absolute-value modifiers",
        Target("gfx1030"),
        Vec(0x1a, 0x02, 0x4c, 0xd5, 0x0a, 0x35, 0x72, 0x04),
        Instruction.VMadF64(
          26,
          InstructionSource.VectorRegister(10),
          InstructionSource.VectorRegister(26),
          InstructionSource.VectorRegister(28),
          SourceModifiers(absolute = 2, negate = 0)
        )
      ),
      (
        "gfx1036 VOP3P mixed f32 FMA decodes all three sources",
        Target("gfx1036"),
        Vec(0x05, 0x00, 0x20, 0xcc, 0x01, 0x05, 0x0e, 0x00),
        Instruction.VFmaMixF32(
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.Scalar(3),
          opSelection = 0,
          opSelectionHi = 0,
          negate = 0,
          absolute = 0,
          clamp = false
        )
      ),
      (
        "gfx1036 VOP2 dot2c f32-f16 accumulates into the destination",
        Target("gfx1036"),
        Vec(0x01, 0x05, 0x0a, 0x04),
        Instruction.VDot2F32F16(
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          accumulate = true
        )
      ),
      (
        "gfx900 VOP3 signed maximum decodes all three sources",
        Target("gfx900"),
        Vec(0xff, 0x00, 0xd4, 0xd1, 0x01, 0x05, 0x0e, 0x04),
        Instruction.VTernary(
          VectorTernaryOperation.Maximum3I32,
          255,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3)
        )
      ),
      (
        "gfx900 VOP3 signed multiply-high decodes its operands",
        Target("gfx900"),
        Vec(0x00, 0x00, 0x87, 0xd2, 0x01, 0x05, 0x02, 0x00),
        Instruction.VBinary(
          VectorBinaryOperation.MultiplyHighI32,
          0,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          false
        )
      ),
      (
        "gfx900 VOP3 signed 64-bit multiply-add decodes its carry destination",
        Target("gfx900"),
        Vec(0xfe, 0x0c, 0xe9, 0xd1, 0x01, 0x05, 0x0e, 0x04),
        Instruction.VMadI64I32(
          254,
          12,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3)
        )
      ),
      (
        "gfx900 VOP1 half-to-single conversion decodes",
        Target("gfx900"),
        Vec(0x01, 0x17, 0x0a, 0x7e),
        Instruction.VConvert(VectorConvertOperation.F32FromF16, 5, InstructionSource.VectorRegister(1))
      ),
      (
        "gfx1036 VOP1 floor f32 decodes",
        Target("gfx1036"),
        Vec(0x01, 0x49, 0x00, 0x7e),
        Instruction.VConvert(VectorConvertOperation.FloorF32, 0, InstructionSource.VectorRegister(1))
      ),
      (
        "gfx1036 VOP1 movrels b32 decodes",
        Target("gfx1036"),
        Vec(0x01, 0x87, 0x0a, 0x7e),
        Instruction.VMoveRelative(5, InstructionSource.VectorRegister(1), RelativeField.Whole)
      ),
      (
        "gfx1036 VOP1 movreld b32 decodes",
        Target("gfx1036"),
        Vec(0x01, 0x85, 0x0a, 0x7e),
        Instruction.VMoveRelativeDestination(5, InstructionSource.VectorRegister(1), None, RelativeField.Whole)
      ),
      (
        "gfx900 VOP1 aperture source encodings decode",
        Target("gfx900"),
        Vec(0xeb, 0x02, 0x00, 0x7e),
        Instruction.VMove(0, InstructionSource.Constant(VMAddressSpace.LocalBase))
      ),
      (
        "gfx900 VOP1 single-to-half conversion decodes",
        Target("gfx900"),
        Vec(0x01, 0x15, 0x0a, 0x7e),
        Instruction.VConvert(VectorConvertOperation.F16FromF32, 5, InstructionSource.VectorRegister(1))
      ),
      (
        "gfx900 VOP1 half-to-signed-16 conversion decodes",
        Target("gfx900"),
        Vec(0x01, 0x79, 0x0a, 0x7e),
        Instruction.VConvert(VectorConvertOperation.I16FromF16, 5, InstructionSource.VectorRegister(1))
      ),
      (
        "gfx900 VOP3 signed median of three decodes",
        Target("gfx900"),
        Vec(0x05, 0x00, 0xd7, 0xd1, 0x01, 0x05, 0x0e, 0x04),
        Instruction.VTernary(
          VectorTernaryOperation.Median3I32,
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3)
        )
      ),
      (
        "gfx1036 VOP2 half-precision fused multiply-add decodes",
        Target("gfx1036"),
        Vec(0x01, 0x05, 0x0a, 0x6c),
        Instruction.VHalfResult(
          Instruction.VBinary(
            VectorBinaryOperation.FusedMultiplyAccumulateF16,
            5,
            InstructionSource.VectorRegister(1),
            InstructionSource.VectorRegister(2),
            clamp = false
          ),
          5,
          false
        )
      )
    )
  ) test(name) {
    assertEquals(VMCompiler.decodeInstruction(bytes.map(_.toByte), target), Right(expected))
  }

  test("output modifiers flush denormals, force positive zero and precede the clamp") {
    def f32(value: Float): Long  = IEEE754.floatToBinary32(value).toLong
    def f64(value: Double): Long = IEEE754.doubleToBinary64(value)
    def f16(value: Float): Long  = Bits.low16(IEEE754.floatToBinary16(value).toLong).toLong
    def scaled(bits: Long, scale: OutputScale, width: Int in (16, 32, 64), clamp: Boolean): Long =
      Execute.outputModifier(scale, width, clamp, bits)
    assertEquals(scaled(f32(1.5f), OutputScale.Multiply2, 32, false), f32(3.0f))
    assertEquals(scaled(f32(1.5f), OutputScale.Multiply4, 32, false), f32(6.0f))
    assertEquals(scaled(f32(-3.0f), OutputScale.Divide2, 32, false), f32(-1.5f))
    assertEquals(scaled(f32(-0.0f), OutputScale.Multiply2, 32, false), 0L)
    assertEquals(scaled(f32(-1e-40f), OutputScale.Multiply4, 32, false), 0L)
    assertEquals(scaled(f32(java.lang.Float.MIN_NORMAL), OutputScale.Divide2, 32, false), 0L)
    assertEquals(scaled(f32(0.75f), OutputScale.Multiply2, 32, true), f32(1.0f))
    assertEquals(scaled(f32(-0.75f), OutputScale.Multiply2, 32, true), 0L)
    assertEquals(scaled(0x7fc00001L, OutputScale.Multiply2, 32, false), 0x7fc00001L)
    assertEquals(scaled(0x7fc00001L, OutputScale.Multiply2, 32, true), 0x7fc00001L)
    assertEquals(scaled(f64(1.5), OutputScale.Multiply2, 64, false), f64(3.0))
    assertEquals(scaled(f64(-0.0), OutputScale.Divide2, 64, false), 0L)
    assertEquals(scaled(f64(java.lang.Double.MIN_NORMAL), OutputScale.Divide2, 64, false), 0L)
    assertEquals(scaled(f16(1.5f), OutputScale.Multiply2, 16, false), f16(3.0f))
    assertEquals(scaled(f16(-0.0f), OutputScale.Multiply4, 16, false), 0L)
    // binary16's smallest normal is 2^-14; halving it leaves a flushed denormal.
    assertEquals(scaled(f16(6.1035156e-5f), OutputScale.Divide2, 16, false), 0L)
  }

  test("AMDGPU f32 trigonometry consumes turns") {
    def trig(operation: VectorConvertOperation, value: Float): Float = {
      val sinks = Values(IEEE754.floatToBinary32(value).toLong)
      IEEE754.binary32ToFloat(Bits.low32(
        FloatSemantics.convert(Execute, operation, clamp = false)(
          sinks,
          0,
          Values.Unused,
          SourceModifiers.Unmodified
        )._1
      ))
    }
    assert(math.abs(trig(VectorConvertOperation.SineF32, 0.25f) - 1.0f) < 1.0e-6f)
    assert(math.abs(trig(VectorConvertOperation.CosineF32, 0.25f)) < 1.0e-6f)
  }

  test("division fixup recovers a finite quotient after pre-scale approximation overflows") {
    val denominator = -java.lang.Float.MAX_VALUE
    val result      = VMArithmetic.divisionFixupF32(Float.NaN, denominator, 1.0f)
    assertEquals(result, 1.0f / denominator)
    assert(!result.isNaN)
    assertEquals(VMArithmetic.divisionFixupF32(Float.NaN, denominator = 0.0f, numerator = 1.0f), Float.PositiveInfinity)
  }

  test("gfx1030 VOP3 bitwise operations decode to their VOP2 semantics") {
    for (
      (bytes, operation, destination, left, right) <- Vec[(
          Vec[Byte],
          VectorBinaryOperation,
          VectorRegisterIndex,
          VectorRegisterIndex,
          VectorRegisterIndex
      )](
        (Vec[Byte](0, 0, 0x1c, 0xd5.toByte, 1, 5, 2, 0), VectorBinaryOperation.Or32, 0, 1, 2),
        (Vec[Byte](3, 0, 0x1b, 0xd5.toByte, 4, 0x0b, 2, 0), VectorBinaryOperation.And32, 3, 4, 5),
        (Vec[Byte](6, 0, 0x1d, 0xd5.toByte, 7, 0x11, 2, 0), VectorBinaryOperation.Xor32, 6, 7, 8)
      )
    )
      assertEquals(
        VMCompiler.decodeInstruction(bytes, Target("gfx1030")),
        Right(Instruction.VBinary(
          operation,
          destination,
          InstructionSource.VectorRegister(left),
          InstructionSource.VectorRegister(right),
          clamp = false
        ))
      )
  }

  test("gfx1030 VOP3 signed 16-bit median of three lowers") {
    // rocThrust's block merge sort emits this instruction; rejecting it failed
    // every i8 sort dispatch with hipErrorLaunchFailure.
    val decoded = VMCompiler.decodeInstruction(
      Vec[Byte](0x05, 0x00, 0x58, 0xd7.toByte, 0x06, 0x0b, 0x4a, 0x05),
      Target("gfx1030")
    )
    assert(
      decoded.exists {
        case Instruction.VTernary(operation = VectorTernaryOperation.Median3I16, dest = dest) => dest == 5
        case _                                                                                => false
      },
      decoded.toString
    )
  }

  test("dot2c f32-f16 computes both packed halves before accumulation") {
    val result = Execute.dotHalves(
      accumulate = true,
      packed(low = 1.0f, high = 2.0f),
      packed(low = 3.0f, high = 4.0f),
      IEEE754.floatToBinary32(5.0f).toLong
    )
    assertEquals(IEEE754.binary32ToFloat(Bits.low32(result)), 16.0f)
  }

  test("gfx900 packed-half FMA applies operand selection, negation, and clamp independently") {
    def fma(lowSelection: Int, highSelection: Int, negateLow: Int, negateHigh: Int, clamp: Boolean): Long =
      run(
        Vec(packed(low = 2.0f, high = 3.0f), packed(low = 4.0f, high = 5.0f), packed(low = 6.0f, high = 7.0f)),
        5,
        Instruction.VPackedTernary(
          5,
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3),
          InstructionSource.VectorRegister(4),
          lowSelection = lowSelection,
          highSelection = highSelection,
          negateLow = negateLow,
          negateHigh = negateHigh,
          clamp = clamp
        )
      ).toLong & 0xffffffffL
    assertEquals(
      fma(lowSelection = 0, highSelection = 5, negateLow = 0, negateHigh = 0, clamp = false),
      packed(low = 14.0f, high = 19.0f)
    )
    assertEquals(
      fma(lowSelection = 0, highSelection = 7, negateLow = 1, negateHigh = 4, clamp = true),
      packed(low = 0.0f, high = 1.0f)
    )
  }

  test("AMDGPU LDS capacity includes fixed and dynamic group segments") {
    assertEquals(VMInterpreter.effectiveGroupSegmentSize(fixed = 8, dynamic = 568), 576)
    intercept[ArithmeticException] {
      VMInterpreter.effectiveGroupSegmentSize(Int.MaxValue, 1)
    }
  }

  test("gfx900 V_PERM_B32 selectors address the second source before the first and high selectors produce zero") {
    def permute(srcA: Long, srcB: Long, selectors: Long): Long = {
      val sinks = Values(srcA, srcB, selectors)
      IntegerSemantics.vectorTernary(Execute, VectorTernaryOperation.PermuteBytes32, clamp = false)(
        sinks,
        0,
        Values.Unused,
        Values.Unused,
        Values.Unused,
        SourceModifiers.Unmodified
      )(fail("unstaged"))._1
    }
    assertEquals(permute(srcA = 0xa3a2a1a0L, srcB = 0xb3b2b1b0L, selectors = 0x05040100L), 0xa1a0b1b0L)
    assertEquals(permute(srcA = 0xa3a2a1a0L, srcB = 0xb3b2b1b0L, selectors = 0x0c0c0001L), 0x0000b0b1L)
  }

  test("D16 merges ignore input bits above the destination dword and the loaded half") {
    assertEquals(
      Execute.mergeHalf(existing = 0xfeed11223344L, value = 0xffffaabbL, high = false),
      0x1122aabbL
    )
    assertEquals(
      Execute.mergeHalf(existing = 0xfeed11223344L, value = 0xffffaabbL, high = true),
      0xaabb3344L
    )
  }

  private def binary(operation: VectorBinaryOperation, left: Long, right: Long): Long = {
    val sinks = Values(left, right)
    IntegerSemantics.vectorBinary(
      Execute,
      operation,
      clamp = false
    )(sinks, 0, Values.Unused, Values.Unused)(fail("unstaged"))._1
  }

  test("24-bit multiplies mask or sign-extend their multiplicands") {
    assertEquals(binary(VectorBinaryOperation.MultiplyU24, left = 0x1ffffffL, right = 2L), 0x1fffffeL)
    val multiplicands = Values(0xffffffL, 2L, 3L)
    assertEquals(
      IntegerSemantics.vectorTernary(Execute, VectorTernaryOperation.MultiplyAddU24, clamp = false)(
        multiplicands,
        0,
        Values.Unused,
        Values.Unused,
        Values.Unused,
        SourceModifiers.Unmodified
      )(fail("unstaged"))._1,
      0x2000001L
    )
    assertEquals(binary(VectorBinaryOperation.MultiplyHighU24, left = 0xffffffL, right = 0xffffffL), 0xffffL)
    assertEquals(binary(VectorBinaryOperation.MultiplyHighU24, left = 0x1000000L, right = 0xffffffL), 0L)
    assertEquals(binary(VectorBinaryOperation.MultiplyI24, left = 0xffffd8L, right = 0xfffffeL), 80L)
    assertEquals(binary(VectorBinaryOperation.MultiplyI24, left = 0x1000001L, right = 3L), 3L)
    assertEquals(binary(VectorBinaryOperation.MultiplyHighI24, left = 0x7fffffL, right = 0x7fffffL), 0x3fffL)
    assertEquals(binary(VectorBinaryOperation.MultiplyHighI24, left = 0x800000L, right = 1L), 0xffffffffL)
    assertEquals(binary(VectorBinaryOperation.MultiplyHighI24, left = 0x800000L, right = 0x800000L), 0x4000L)
  }

  test("reverse unsigned vector subtraction wraps to 32 bits") {
    assertEquals(binary(VectorBinaryOperation.SubtractReverseU32, left = 20L, right = 19L), 0xffffffffL)
    assertEquals(binary(VectorBinaryOperation.SubtractReverseU32, left = 20L, right = 99L), 79L)
  }

  test("VOP3 mask bit counts compose across both halves of a wave") {
    val mask                                              = 0xf000000f0000000fL
    def count(lane: Int, high: Boolean, base: Long): Long = {
      val sinks = Values(if (high) mask >>> 32 else mask & 0xffffffffL, lane.toLong, base)
      IntegerSemantics.maskBitCount(
        Execute,
        high
      )(sinks, 0, Values.Unused, Values.Unused)._1
    }
    assertEquals(count(0, high = false, base = 0L), 0L)
    assertEquals(count(4, high = false, base = 0L), 4L)
    assertEquals(count(32, high = false, base = 0L), 4L)
    assertEquals(count(60, high = true, base = 8L), 12L)
    assertEquals(count(64 - 1, high = true, base = 8L), 15L)
  }

  test("SOPK signed multiply uses a sign-extended immediate and reports overflow") {
    assertEquals(
      Values.flagged(IntegerSemantics.multiplyImmediate(Execute)(Values(0xffffffffL), 0, 0x300L)),
      (0xfffffd00L, false)
    )
    assertEquals(
      Values.flagged(IntegerSemantics.multiplyImmediate(Execute)(Values(Int.MaxValue.toLong), 0, 2L)),
      (0xfffffffeL, true)
    )
  }

  for (
    (name, bytes) <- Seq(
      "VOP3 mask bit counts" ->
        Vec(0x03, 0x00, 0x8c, 0xd2, 0xc1, 0x00, 0x01, 0x00, 0x03, 0x00, 0x8d, 0xd2, 0xc1, 0x06, 0x02, 0x00),
      "bit reverse of an inline float constant"   -> Vec(0xf0, 0x58, 0x08, 0x7e),
      "unsigned 24-bit multiply and multiply-add" ->
        Vec(0x94, 0x00, 0x08, 0x10, 0x05, 0x00, 0xc3, 0xd1, 0x00, 0x29, 0x15, 0x04),
      "reverse unsigned subtraction"                -> Vec(0x94, 0x06, 0x0a, 0x6c),
      "signed 24-bit multiply with a literal"       -> Vec(0xff, 0x00, 0x12, 0x0c, 0xd8, 0xff, 0xff, 0xff),
      "signed 16-bit maximum of three"              -> Vec(0xff, 0x00, 0xf8, 0xd1, 0x01, 0x05, 0x0e, 0x04),
      "half-precision maximum"                      -> Vec(0x01, 0x05, 0x02, 0x5a),
      "half-precision not-less-than"                -> Vec(0x01, 0x05, 0x5c, 0x7c),
      "SOPK signed multiply"                        -> Vec(0x00, 0x03, 0x82, 0xb7),
      "unsigned subtraction with a borrow output"   -> Vec(0x0e, 0x02, 0x02, 0x34),
      "chained subtraction with a borrow input"     -> Vec(0x03, 0x05, 0x04, 0x3a),
      "16-bit not-equal alias"                      -> Vec(0x04, 0x0b, 0x5a, 0x7d),
      "64-bit not-equal alias"                      -> Vec(0x80, 0x02, 0xda, 0x7d),
      "signed 64-bit comparison into a scalar mask" -> Vec(0x00, 0x00, 0xe1, 0xd0, 0x01, 0x07, 0x02, 0x00)
    )
  ) test(s"gfx900 accepts $name") {
    assertEquals(fixtures.validate((bytes ++ Vec(0x00, 0x00, 0x81, 0xbf)).map(_.toByte), Target("gfx900")), Right(()))
  }

  test("gfx900 ANDN2_SAVEEXEC uses source AND NOT old EXEC") {
    val narrow = Values(0x3333L, 0x0f0fL)
    assertEquals(
      Values.flagged(IntegerSemantics.saveExec(
        Execute,
        SaveExecOperation.AndNot
      )(narrow, 0, Values.Unused, 64)),
      (0x3030L, true)
    )
    val wide = Values(0x3000000000000000L, 0xf000000000000000L)
    assertEquals(
      Values.flagged(
        IntegerSemantics.saveExec(Execute, SaveExecOperation.AndNot)(
          wide,
          0,
          Values.Unused,
          64
        )
      ),
      (0L, false)
    )
  }

  test("gfx900 VOP2 multiply-high and minimum have distinct opcodes and preserve both sources") {
    // Pinned LLVM MC gfx9_asm_vop2.s: v_mul_hi_u32_u24 and v_min_i32 v5, v1, v2.
    for (
      (opcode, operation) <- Vec(
        0x12 -> VectorBinaryOperation.MultiplyHighU24,
        0x18 -> VectorBinaryOperation.MinimumI32
      )
    )
      assertEquals(
        VMCompiler.decodeInstruction(Vec[Byte](0x01, 0x05, 0x0a, opcode.toByte), Target("gfx900")),
        Right(Instruction.VBinary(
          operation,
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          false
        ))
      )
  }

  test("half-precision not-less-than includes unordered values") {
    val one                                       = IEEE754.floatToBinary16(1.0f)
    val two                                       = IEEE754.floatToBinary16(2.0f)
    def notLess(left: Long, right: Long): Boolean = {
      val (test, swap) = (
        VectorComparisonRelation.NotLess.floatTest,
        VectorComparisonRelation.NotLess.floatSwapped
      )
      val sinks = Values(left, right)
      FloatSemantics.compare(Execute, VectorComparisonKind.Float16, test, swap)(
        sinks,
        scalar = false,
        0,
        Values.Unused,
        Values.Unused,
        SourceModifiers.Unmodified,
        None,
        false
      )(fail("unstaged"))._1 != 0L
    }
    assert(!notLess(one.toLong, two.toLong))
    assert(notLess(two.toLong, one.toLong))
    assert(notLess(0x7e00L, one.toLong))
  }
}
