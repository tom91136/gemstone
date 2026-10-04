package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class AMDGPUArithmeticSuite extends munit.FunSuite {
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
      assertEquals(VMArithmetic.trigPreopF64(input, segment), expected)
    }
  }

  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

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
    assert(math.abs(VMArithmetic.trigF32(VectorConvertOperation.SineF32, 0.25f) - 1.0f) < 1.0e-6f)
    assert(math.abs(VMArithmetic.trigF32(VectorConvertOperation.CosineF32, 0.25f)) < 1.0e-6f)
  }

  test("division fixup recovers a finite quotient after pre-scale approximation overflows") {
    val denominator = -java.lang.Float.MAX_VALUE
    val result      = VMArithmetic.divisionFixupF32(Float.NaN, denominator, 1.0f)
    assertEquals(result, 1.0f / denominator)
    assert(!result.isNaN)
    assertEquals(VMArithmetic.divisionFixupF32(Float.NaN, 0.0f, 1.0f), Float.PositiveInfinity)
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
        case Instruction.VTernary(VectorTernaryOperation.Median3I16, dest, _, _, _) => dest.raw == 5
        case _                                                                      => false
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
        Instruction.VMadF32(
          VectorMadF32Operation.FusedMultiplyAdd,
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(5),
          absA = false,
          absB = false,
          absC = false,
          negateA = false,
          negateB = false,
          negateC = false,
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
      VMArithmetic.fmaF32(VectorMadF32Operation.FusedMultiplyAdd, left.toLong, right.toLong, addend.toLong),
      IEEE754.floatToBinary32(Math.scalb(1.0f, -46))
    )
    assertEquals(
      VMArithmetic.fmaF32(VectorMadF32Operation.MultiplyAdd, left.toLong, right.toLong, addend.toLong),
      IEEE754.floatToBinary32(0.0f)
    )
    assertEquals(
      VMArithmetic.fmaF32(VectorMadF32Operation.MultiplyAdd, left.toLong, right.toLong, addend.toLong),
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
    assertEquals(VMArithmetic.firstBitHigh32(0x80000000L), 0L)
    assertEquals(VMArithmetic.firstBitHigh32(0x00000001L), 31L)
    assertEquals(VMArithmetic.firstBitHigh32(0L), 0xffffffffL)
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
      Bits.low16(IEEE754.floatToBinary16(low).toLong).toLong |
        (Bits.low16(IEEE754.floatToBinary16(high).toLong).toLong << 16)

    val result = VMArithmetic.dot2F32F16(
      packed(1.0f, 2.0f),
      packed(3.0f, 4.0f),
      IEEE754.floatToBinary32(5.0f).toLong,
      accumulate = true
    )
    assertEquals(IEEE754.binary32ToFloat(result), 16.0f)
  }

  test("gfx900 packed-half FMA applies operand selection, negation, and clamp independently") {
    def packed(low: Float, high: Float): Long =
      Bits.low16(IEEE754.floatToBinary16(low).toLong).toLong |
        (Bits.low16(IEEE754.floatToBinary16(high).toLong).toLong << 16)

    assertEquals(
      VMArithmetic.packedFmaF16(
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
      VMArithmetic.packedFmaF16(
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
      name = "linked",
      target = Target("gfx900"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = Vec.empty,
      codeAddress = 0x1000L
    )
    val allocation = AllocationId(1L)
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
      ELF.selectFunctionTarget(Vec("alias", "_Z5logIfv")),
      Right("_Z5logIfv")
    )
    assertEquals(
      ELF.selectFunctionTarget(Vec("_Z9aliasLogIfv", "_Z5logIfv")),
      Right("_Z5logIfv")
    )
    assertEquals(
      ELF.selectFunctionTarget(Vec("_Z5logIfv", "_Z5expIfv")),
      Left(CodeObjectError.Invalid("AMDGPU function aliases have conflicting call semantics: _Z5expIfv, _Z5logIfv"))
    )
    assertEquals(
      ELF.selectFunctionTarget(Vec("first", "second")),
      Right("first")
    )
    assertEquals(ELF.selectFunctionTarget(Vec("second", "first", "second")), Right("first"))
  }

  test("AMDGPU LDS capacity includes fixed and dynamic group segments") {
    assertEquals(VMInterpreter.effectiveGroupSegmentSize(8, 568), 576)
    intercept[ArithmeticException] {
      VMInterpreter.effectiveGroupSegmentSize(Int.MaxValue, 1)
    }
  }

  test("gfx900 V_PERM_B32 selectors address the second source before the first") {
    assertEquals(
      VMArithmetic.permuteBytes(0xa3a2a1a0L, 0xb3b2b1b0L, 0x05040100L),
      0xa1a0b1b0L
    )
  }

  test("gfx900 V_PERM_B32 high selectors produce zero bytes") {
    assertEquals(
      VMArithmetic.permuteBytes(0xa3a2a1a0L, 0xb3b2b1b0L, 0x0c0c0001L),
      0x0000b0b1L
    )
  }

  test("gfx900 scalar unsigned multiply returns the high 32 bits") {
    assertEquals(VMArithmetic.unsignedMultiplyHigh32(0xffffffffL, 0xffffffffL), 0xfffffffeL)
    assertEquals(VMArithmetic.unsignedMultiplyHigh32(0x12345678L, 0x100000000L), 0L)
  }

  test("gfx900 signed multiply returns the high 32 bits") {
    assertEquals(VMArithmetic.signedMultiplyHigh32(0x7fffffffL, 0x7fffffffL), 0x3fffffffL)
    assertEquals(VMArithmetic.signedMultiplyHigh32(0x80000000L, 1L), 0xffffffffL)
    assertEquals(VMArithmetic.signedMultiplyHigh32(0x80000000L, 0x80000000L), 0x40000000L)
  }

  test("gfx900 vector unsigned 24-bit multiply-high returns the high 32 bits") {
    assertEquals(VMArithmetic.unsignedMultiplyHigh24(0xffffffL, 0xffffffL), 0xffffL)
    assertEquals(VMArithmetic.unsignedMultiplyHigh24(0x1000000L, 0xffffffL), 0L)
  }

  test("gfx900 vector signed 24-bit multiply-high sign-extends operands") {
    assertEquals(VMArithmetic.signedMultiplyHigh24(0x7fffffL, 0x7fffffL), 0x3fffL)
    assertEquals(VMArithmetic.signedMultiplyHigh24(0x800000L, 1L), 0xffffffffL)
    assertEquals(VMArithmetic.signedMultiplyHigh24(0x800000L, 0x800000L), 0x4000L)
  }

  test("gfx900 ANDN2_SAVEEXEC uses source AND NOT old EXEC") {
    assertEquals(VMArithmetic.andn2SaveExecMask(0x0f0fL, 0x3333L), 0x3030L)
    assertEquals(
      VMArithmetic.andn2SaveExecMask(0xf000000000000000L, 0x3000000000000000L),
      0L
    )
  }

  test("gfx900 signed first-bit-high counts leading sign bits") {
    assertEquals(VMArithmetic.signedFirstBitHigh32(0L), 0xffffffffL)
    assertEquals(VMArithmetic.signedFirstBitHigh32(0xffffffffL), 0xffffffffL)
    assertEquals(VMArithmetic.signedFirstBitHigh32(1L), 31L)
    assertEquals(VMArithmetic.signedFirstBitHigh32(0x40000000L), 1L)
    assertEquals(VMArithmetic.signedFirstBitHigh32(0x80000000L), 1L)
    assertEquals(VMArithmetic.signedFirstBitHigh32(0xfffffffeL), 31L)
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

  test("gfx900 VOP3 signed maximum decodes all three sources") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](
          0xff.toByte,
          0x00,
          0xd4.toByte,
          0xd1.toByte,
          0x01,
          0x05,
          0x0e,
          0x04
        ),
        Target("gfx900")
      ),
      Right(Instruction.VTernary(
        VectorTernaryOperation.Maximum3I32,
        255,
        InstructionSource.VectorRegister(1),
        InstructionSource.VectorRegister(2),
        InstructionSource.VectorRegister(3)
      ))
    )
  }

  test("gfx900 VOP3 signed multiply-high decodes its operands") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](
          0x00,
          0x00,
          0x87.toByte,
          0xd2.toByte,
          0x01,
          0x05,
          0x02,
          0x00
        ),
        Target("gfx900")
      ),
      Right(Instruction.VBinary(
        VectorBinaryOperation.MultiplyHighI32,
        0,
        InstructionSource.VectorRegister(1),
        InstructionSource.VectorRegister(2),
        false
      ))
    )
  }

  test("gfx900 VOP3 signed 64-bit multiply-add decodes its carry destination") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](
        0xfe.toByte,
        0x0c,
        0xe9.toByte,
        0xd1.toByte,
        0x01,
        0x05,
        0x0e,
        0x04
      ),
      Target("gfx900")
    )

    assertEquals(
      instruction,
      Right(
        Instruction.VMadI64I32(
          254,
          12,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3)
        )
      )
    )
  }

  test("gfx900 VOP1 half-to-single conversion decodes") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0x01, 0x17, 0x0a, 0x7e.toByte),
      Target("gfx900")
    )

    assertEquals(
      instruction,
      Right(Instruction.VConvert(VectorConvertOperation.F32FromF16, 5, InstructionSource.VectorRegister(1)))
    )
  }

  test("gfx1036 VOP1 floor f32 decodes") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x01, 0x49, 0x00, 0x7e.toByte),
        Target("gfx1036")
      ),
      Right(Instruction.VConvert(VectorConvertOperation.FloorF32, 0, InstructionSource.VectorRegister(1)))
    )
  }

  test("gfx1036 VOP1 movrels b32 decodes") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x01, 0x87.toByte, 0x0a, 0x7e),
        Target("gfx1036")
      ),
      Right(Instruction.VMoveRelative(5, InstructionSource.VectorRegister(1)))
    )
  }

  test("gfx1036 VOP1 movreld b32 decodes") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x01, 0x85.toByte, 0x0a, 0x7e),
        Target("gfx1036")
      ),
      Right(Instruction.VMoveRelativeDestination(5, InstructionSource.VectorRegister(1)))
    )
  }

  test("gfx900 VOP1 aperture source encodings decode") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0xeb.toByte, 0x02, 0x00, 0x7e.toByte),
      Target("gfx900")
    )

    assertEquals(instruction, Right(Instruction.VMove(0, InstructionSource.Constant(VMAddressSpace.LocalBase))))
  }

  test("gfx900 VOP1 single-to-half conversion decodes") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0x01, 0x15, 0x0a, 0x7e.toByte),
      Target("gfx900")
    )

    assertEquals(
      instruction,
      Right(Instruction.VConvert(VectorConvertOperation.F16FromF32, 5, InstructionSource.VectorRegister(1)))
    )
  }

  test("gfx900 VOP1 half-to-signed-16 conversion decodes") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0x01, 0x79, 0x0a, 0x7e.toByte),
      Target("gfx900")
    )

    assertEquals(
      instruction,
      Right(Instruction.VConvert(VectorConvertOperation.I16FromF16, 5, InstructionSource.VectorRegister(1)))
    )
  }

  test("unsigned f32 conversion truncates and saturates to the unsigned range") {
    // AMD V_CVT_U32_F32 saturates out-of-range inputs; it does not take their magnitude.
    assertEquals(VMArithmetic.unsignedFloatToInt32(-3.0f), 0L)
    assertEquals(VMArithmetic.unsignedFloatToInt32(5.75f), 5L)
    assertEquals(VMArithmetic.unsignedFloatToInt32(Float.NaN), 0L)
    assertEquals(VMArithmetic.unsignedFloatToInt32(Float.NegativeInfinity), 0L)
    assertEquals(VMArithmetic.unsignedFloatToInt32(Float.PositiveInfinity), 0xffffffffL)
    assertEquals(VMArithmetic.unsignedFloatToInt32(4294967296.0f), 0xffffffffL)
  }

  test("gfx900 VOP3 signed median of three decodes") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0x05, 0x00, 0xd7.toByte, 0xd1.toByte, 0x01, 0x05, 0x0e, 0x04),
      Target("gfx900")
    )

    assertEquals(
      instruction,
      Right(
        Instruction.VTernary(
          VectorTernaryOperation.Median3I32,
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3)
        )
      )
    )
  }

  test("gfx900 VOP3 signed 16-bit maximum of three validates") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xff.toByte,
          0x00,
          0xf8.toByte,
          0xd1.toByte,
          0x01,
          0x05,
          0x0e,
          0x04,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP2 half-precision maximum validates") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x01,
          0x05,
          0x02,
          0x5a,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx1036 VOP2 half-precision fused multiply-add decodes") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x01, 0x05, 0x0a, 0x6c),
        Target("gfx1036")
      ),
      Right(
        Instruction.VBinary(
          VectorBinaryOperation.FusedMultiplyAccumulateF16,
          5,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          clamp = false
        )
      )
    )
  }

  test("gfx900 VOPC half-precision not-less-than validates and includes unordered values") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x01,
          0x05,
          0x5c,
          0x7c,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
    val one     = IEEE754.floatToBinary16(1.0f)
    val two     = IEEE754.floatToBinary16(2.0f)
    val notLess = VectorComparison(
      kind = VectorComparisonKind.Float16,
      relation = VectorComparisonRelation.NotLess,
      updateExec = false
    )
    assert(!VMArithmetic.compare(notLess, one.toLong, two.toLong))
    assert(VMArithmetic.compare(notLess, two.toLong, one.toLong))
    assert(VMArithmetic.compare(notLess, 0x7e00L, one.toLong))
  }
}
