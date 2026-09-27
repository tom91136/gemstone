package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonNegative

import CodeObject.*
import VMIR.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{Bits, ByteRange}
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

final class AMDGPUArithmeticSuite extends munit.FunSuite with CodeObjectTestSupport {
  test("gfx1030 SOP2 shift-left-two and add decodes") {
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0x01, 0x02, 0x80.toByte, 0x97.toByte), Target("gfx1030")),
      Right(Instruction.SBinary(
        ScalarBinaryOperation.ShiftLeftTwoAndAddU32,
        0,
        InstructionSource.Scalar(1),
        InstructionSource.Scalar(2)
      ))
    )
  }

  test("AMDGPU f32 trigonometry consumes turns") {
    assert(math.abs(VMInterpreter.trigF32(VectorConvertOperation.SineF32, 0.25f) - 1.0f) < 1.0e-6f)
    assert(math.abs(VMInterpreter.trigF32(VectorConvertOperation.CosineF32, 0.25f)) < 1.0e-6f)
  }

  test("division fixup recovers a finite quotient after pre-scale approximation overflows") {
    val denominator = -java.lang.Float.MAX_VALUE
    val result      = VMInterpreter.divisionFixupF32(Float.NaN, denominator, 1.0f)
    assertEquals(result, 1.0f / denominator)
    assert(!result.isNaN)
    assertEquals(VMInterpreter.divisionFixupF32(Float.NaN, 0.0f, 1.0f), Float.PositiveInfinity)
  }

  test("gfx1030 VOP3 bitwise operations decode to their VOP2 semantics") {
    for (
      (bytes, operation, destination, left, right) <- Seq(
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
        case Instruction.VTernary(VectorTernaryOperation.Median3I16, 5, _, _, _) => true
        case _                                                                   => false
      },
      decoded.toString
    )
  }

  test("gfx900 packed-half FMA decoding preserves low and high operand selection") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x40, 0x8e.toByte, 0xd3.toByte, 0x02, 0x09, 0x02, 0x0c),
        Target("gfx900")
      ),
      Right(
        Instruction.VPackedFmaF16(
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
      )
    )
  }

  test("gfx1036 VOP3P packed-half FMA decodes all three sources") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x05, 0x40, 0x0e, 0xcc.toByte, 0x01, 0x05, 0x0e, 0x1c),
        Target("gfx1036")
      ),
      Right(
        Instruction.VPackedFmaF16(
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
      )
    )
  }

  test("gfx1036 VOP3P mixed FMA decodes source selection and destination half") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x05, 0x00, 0x21, 0xcc.toByte, 0x01, 0x05, 0x0e, 0x00),
        Target("gfx1036")
      ),
      Right(
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
      )
    )
  }

  test("gfx1036 VOP3P unsigned byte dot product decodes all three sources") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x05, 0x40, 0x17, 0xcc.toByte, 0x01, 0x05, 0x0e, 0x18),
        Target("gfx1036")
      ),
      Right(
        Instruction.VDot4U32U8(
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.Scalar(3)
        )
      )
    )
  }

  test("gfx1036 VOP3 FMAC uses the destination as the accumulator") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x05, 0x00, 0x2b, 0xd5.toByte, 0x01, 0x05, 0x02, 0x00),
        Target("gfx1036")
      ),
      Right(
        Instruction.VBinary(
          VectorBinaryOperation.MultiplyAccumulateF32,
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          clamp = false
        )
      )
    )
  }

  test("f32 FMA preserves the fused cancellation required by compiler math lowering") {
    val onePlusUlp    = java.lang.Float.intBitsToFloat(0x3f800001)
    val onePlusTwoUlp = java.lang.Float.intBitsToFloat(0x3f800002)
    val left          = IEEE754.floatToBinary32(onePlusUlp)
    val right         = IEEE754.floatToBinary32(onePlusUlp)
    val addend        = IEEE754.floatToBinary32(-onePlusTwoUlp)

    assertEquals(
      VMInterpreter.fmaF32(VectorMadF32Operation.FusedMultiplyAdd, left.raw, right.raw, addend.raw),
      IEEE754.floatToBinary32(Math.scalb(1.0f, -46))
    )
    assertEquals(
      VMInterpreter.fmaF32(VectorMadF32Operation.MultiplyAdd, left.raw, right.raw, addend.raw),
      IEEE754.floatToBinary32(0.0f)
    )
    assertEquals(
      VMInterpreter.fmaF32(VectorMadF32Operation.MultiplyAdd, left.raw, right.raw, addend.raw),
      IEEE754.floatToBinary32(0.0f)
    )
  }

  test("gfx1030 division fixup preserves folded absolute-value modifiers") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x18, 0x04, 0x60, 0xd5.toByte, 0x18, 0x25, 0x02, 0x04),
        Target("gfx1030")
      ),
      Right(
        Instruction.VDivFixupF64(
          24,
          InstructionSource.VectorRegister(24),
          InstructionSource.VectorRegister(18),
          InstructionSource.VectorRegister(0),
          absolute = 4,
          negate = 0
        )
      )
    )
  }

  test("gfx1030 f64 FMA preserves folded absolute-value modifiers") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x1a, 0x02, 0x4c, 0xd5.toByte, 0x0a, 0x35, 0x72, 0x04),
        Target("gfx1030")
      ),
      Right(
        Instruction.VMadF64(
          26,
          InstructionSource.VectorRegister(10),
          InstructionSource.VectorRegister(26),
          InstructionSource.VectorRegister(28),
          absA = false,
          absB = true,
          absC = false,
          negateA = false,
          negateB = false,
          negateC = false
        )
      )
    )
  }

  test("FFBH counts leading zeros and returns all ones for zero") {
    assertEquals(VMInterpreter.firstBitHigh32(0x80000000L), 0L)
    assertEquals(VMInterpreter.firstBitHigh32(0x00000001L), 31L)
    assertEquals(VMInterpreter.firstBitHigh32(0L), 0xffffffffL)
  }

  test("gfx1036 VOP3P mixed f32 FMA decodes all three sources") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x05, 0x00, 0x20, 0xcc.toByte, 0x01, 0x05, 0x0e, 0x00),
        Target("gfx1036")
      ),
      Right(
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
      )
    )
  }

  test("gfx1036 VOP2 dot2c f32-f16 accumulates into the destination") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x01, 0x05, 0x0a, 0x04),
        Target("gfx1036")
      ),
      Right(
        Instruction.VDot2F32F16(
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          accumulate = true
        )
      )
    )
  }

  test("dot2c f32-f16 computes both packed halves before accumulation") {
    def packed(low: Float, high: Float): Long =
      Bits.low16(IEEE754.floatToBinary16(low).raw).raw |
        (Bits.low16(IEEE754.floatToBinary16(high).raw).raw << 16)

    val result = VMInterpreter.dot2F32F16(
      packed(1.0f, 2.0f),
      packed(3.0f, 4.0f),
      IEEE754.floatToBinary32(5.0f).raw,
      accumulate = true
    )
    assertEquals(IEEE754.binary32ToFloat(result), 16.0f)
  }

  test("gfx900 packed-half FMA applies operand selection, negation, and clamp independently") {
    def packed(low: Float, high: Float): Long =
      Bits.low16(IEEE754.floatToBinary16(low).raw).raw |
        (Bits.low16(IEEE754.floatToBinary16(high).raw).raw << 16)

    assertEquals(
      VMInterpreter.packedFmaF16(
        packed(2.0f, 3.0f),
        packed(4.0f, 5.0f),
        packed(6.0f, 7.0f),
        lowSelection = 0,
        highSelection = 5,
        negateLow = 0,
        negateHigh = 0,
        clamp = false
      ),
      packed(14.0f, 19.0f)
    )
    assertEquals(
      VMInterpreter.packedFmaF16(
        packed(2.0f, 3.0f),
        packed(4.0f, 5.0f),
        packed(6.0f, 7.0f),
        lowSelection = 0,
        highSelection = 7,
        negateLow = 1,
        negateHigh = 4,
        clamp = true
      ),
      packed(0.0f, 1.0f)
    )
  }

  test("linked code-object kernels infer an omitted kernarg size from typed launch arguments") {
    val kernel = CodeObjectKernel(
      "linked",
      Target("gfx900"),
      0,
      0,
      0,
      0,
      0,
      Vec.empty,
      codeAddress = 0x1000L
    )
    val allocation = MemoryRegion(1L)
    assertEquals(
      VMInterpreter.effectiveKernargSize(
        kernel,
        Vec(
          KernelTask.Argument.Memory(allocation, 0L),
          KernelTask.Argument.Scalar(Vec.fill(12)(0.toByte))
        )
      ),
      Right(20)
    )
    assertEquals(VMInterpreter.effectiveKernargSize(kernel.copy(codeAddress = 0L), Vec.empty), Right(0))
  }

  test("function aliases resolve deterministically and reject conflicting call semantics") {
    assertEquals(
      CodeObject.selectFunctionTarget(Vec("alias", "_Z5logIfv")),
      Right("_Z5logIfv")
    )
    assertEquals(
      CodeObject.selectFunctionTarget(Vec("_Z9aliasLogIfv", "_Z5logIfv")),
      Right("_Z5logIfv")
    )
    assertEquals(
      CodeObject.selectFunctionTarget(Vec("_Z5logIfv", "_Z5expIfv")),
      Left(CodeObjectError.Invalid("AMDGPU function aliases have conflicting call semantics: _Z5expIfv, _Z5logIfv"))
    )
    assertEquals(
      CodeObject.selectFunctionTarget(Vec("first", "second")),
      Left(CodeObjectError.Invalid("AMDGPU function aliases are ambiguous: first, second"))
    )
  }

  test("AMDGPU LDS capacity includes fixed and dynamic group segments") {
    assertEquals(VMInterpreter.effectiveGroupSegmentSize(8, 568), 576)
    intercept[ArithmeticException] {
      VMInterpreter.effectiveGroupSegmentSize(Int.MaxValue, 1)
    }
  }

  test("gfx900 V_PERM_B32 selectors address the second source before the first") {
    assertEquals(
      VMInterpreter.permuteBytes(0xa3a2a1a0L, 0xb3b2b1b0L, 0x05040100L),
      0xa1a0b1b0L
    )
  }

  test("gfx900 V_PERM_B32 high selectors produce zero bytes") {
    assertEquals(
      VMInterpreter.permuteBytes(0xa3a2a1a0L, 0xb3b2b1b0L, 0x0c0c0001L),
      0x0000b0b1L
    )
  }

  test("gfx900 scalar unsigned multiply returns the high 32 bits") {
    assertEquals(VMInterpreter.unsignedMultiplyHigh32(0xffffffffL, 0xffffffffL), 0xfffffffeL)
    assertEquals(VMInterpreter.unsignedMultiplyHigh32(0x12345678L, 0x100000000L), 0L)
  }

  test("gfx900 signed multiply returns the high 32 bits") {
    assertEquals(VMInterpreter.signedMultiplyHigh32(0x7fffffffL, 0x7fffffffL), 0x3fffffffL)
    assertEquals(VMInterpreter.signedMultiplyHigh32(0x80000000L, 1L), 0xffffffffL)
    assertEquals(VMInterpreter.signedMultiplyHigh32(0x80000000L, 0x80000000L), 0x40000000L)
  }

  test("gfx900 vector unsigned 24-bit multiply-high returns the high 32 bits") {
    assertEquals(VMInterpreter.unsignedMultiplyHigh24(0xffffffL, 0xffffffL), 0xffffL)
    assertEquals(VMInterpreter.unsignedMultiplyHigh24(0x1000000L, 0xffffffL), 0L)
  }

  test("gfx900 vector signed 24-bit multiply-high sign-extends operands") {
    assertEquals(VMInterpreter.signedMultiplyHigh24(0x7fffffL, 0x7fffffL), 0x3fffL)
    assertEquals(VMInterpreter.signedMultiplyHigh24(0x800000L, 1L), 0xffffffffL)
    assertEquals(VMInterpreter.signedMultiplyHigh24(0x800000L, 0x800000L), 0x4000L)
  }

  test("gfx900 ANDN2_SAVEEXEC uses source AND NOT old EXEC") {
    assertEquals(VMInterpreter.andn2SaveExecMask(0x0f0fL, 0x3333L), 0x3030L)
    assertEquals(
      VMInterpreter.andn2SaveExecMask(0xf000000000000000L, 0x3000000000000000L),
      0L
    )
  }

  test("gfx900 signed first-bit-high counts leading sign bits") {
    assertEquals(VMInterpreter.signedFirstBitHigh32(0L), 0xffffffffL)
    assertEquals(VMInterpreter.signedFirstBitHigh32(0xffffffffL), 0xffffffffL)
    assertEquals(VMInterpreter.signedFirstBitHigh32(1L), 31L)
    assertEquals(VMInterpreter.signedFirstBitHigh32(0x40000000L), 1L)
    assertEquals(VMInterpreter.signedFirstBitHigh32(0x80000000L), 1L)
    assertEquals(VMInterpreter.signedFirstBitHigh32(0xfffffffeL), 31L)
  }

}
