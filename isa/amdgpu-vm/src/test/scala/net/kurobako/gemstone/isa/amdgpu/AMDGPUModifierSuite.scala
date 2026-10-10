package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

class AMDGPUModifierSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  for (
    (name, target, bytes, expected) <- Seq[(String, Target, Vec[Int], Instruction)](
      (
        "gfx900 VOP3 f32 binary decoding preserves clamp",
        Target("gfx900"),
        Vec(0x01, 0x80, 0x01, 0xd1, 0x03, 0x0b, 0x02, 0x00),
        Instruction.VBinary(
          VectorBinaryOperation.AddF32,
          1,
          InstructionSource.VectorRegister(3),
          InstructionSource.VectorRegister(5),
          true,
          SourceModifiers.Unmodified
        )
      ),
      (
        "VOP3 f32 FMA preserves the absolute modifier on source 2",
        Target("gfx1036"),
        Vec(0x31, 0x04, 0x4b, 0xd5, 0xff, 0x4c, 0x06, 0x04, 0xda, 0x0f, 0xc9, 0xbf),
        Instruction.VMadF32(
          VectorMadF32Operation.FusedMultiplyAdd,
          49,
          InstructionSource.Literal(0xbfc90fdaL),
          InstructionSource.VectorRegister(38),
          InstructionSource.VectorRegister(1),
          SourceModifiers(absolute = 4, negate = 0),
          false
        )
      ),
      (
        "VOP3 f32 FMA does not confuse source 3 encoding with absolute",
        Target("gfx1036"),
        Vec(0x13, 0x00, 0x4b, 0xd5, 0x03, 0x29, 0x12, 0x84),
        Instruction.VMadF32(
          VectorMadF32Operation.FusedMultiplyAdd,
          19,
          InstructionSource.VectorRegister(3),
          InstructionSource.VectorRegister(20),
          InstructionSource.VectorRegister(4),
          SourceModifiers(absolute = 0, negate = 4),
          false
        )
      ),
      (
        "gfx900 VOPC SDWA preserves a scalar source-0 encoding",
        Target("gfx900"),
        Vec(0xf9, 0x04, 0x94, 0x7d, 0x03, 0x80, 0x86, 0x06),
        Instruction.VCompare(
          VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Equal, false),
          0,
          InstructionSource.Scalar(3),
          InstructionSource.VectorRegister(2),
          selection = Some(SubDwordSources(SubDwordSelection.DWord, SubDwordSelection.DWord, false, false))
        )
      ),
      (
        "gfx900 VOPC SDWA unsigned 16-bit greater-than is supported",
        Target("gfx900"),
        Vec(0xf9, 0x04, 0x58, 0x7d, 0x03, 0x80, 0x86, 0x06),
        Instruction.VCompare(
          VectorComparison(VectorComparisonKind.Unsigned16, VectorComparisonRelation.Greater, false),
          0,
          InstructionSource.Scalar(3),
          InstructionSource.VectorRegister(2),
          selection = Some(SubDwordSources(SubDwordSelection.DWord, SubDwordSelection.DWord, false, false))
        )
      ),
      (
        "gfx1036 packed signed 16-bit maximum is supported",
        Target("gfx1036"),
        Vec(0x01, 0x00, 0x07, 0xcc, 0x02, 0x07, 0x12, 0x04),
        Instruction.VPackedBinary(
          VectorPackedBinaryOperation.MaximumI16,
          1,
          InstructionSource.VectorRegister(2),
          InstructionSource.VectorRegister(3),
          false,
          false
        )
      ),
      (
        "gfx900 SDWA signed 24-bit multiplication preserves the decoded sub-dword operands",
        Target("gfx900"),
        Vec(0xf9, 0x00, 0x3c, 0x0c, 0x35, 0x06, 0x0c, 0x86),
        Instruction.VBinarySubDword(
          VectorSubDwordOperation.MultiplyI24,
          30,
          InstructionSource.VectorRegister(53),
          InstructionSource.Scalar(0),
          SubDwordSelection.DWord,
          SubDwordDestinationUnused.Pad,
          SubDwordSelection.Word0,
          SubDwordSelection.DWord,
          true,
          false
        )
      ),
      (
        "gfx900 SDWA cndmask decodes with sub-dword selections",
        Target("gfx900"),
        Vec(0xf9, 0x04, 0x00, 0x00, 0x01, 0x10, 0x01, 0x02),
        Instruction.VBinarySubDword(
          VectorSubDwordOperation.ConditionalMask32,
          0,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          SubDwordSelection.Byte0,
          SubDwordDestinationUnused.Preserve,
          SubDwordSelection.Byte1,
          SubDwordSelection.Byte2,
          false,
          false
        )
      ),
      (
        "gfx803 MADAK consumes the embedded literal",
        Target("gfx803"),
        Vec(1, 5, 6, 0x30, 0, 0, 0, 0x3f),
        Instruction.VMadF32(
          VectorMadF32Operation.MultiplyAdd,
          3,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.Literal(0x3f000000L),
          SourceModifiers.Unmodified,
          false
        )
      ),
      (
        "gfx1030 FMAAK rounds once",
        Target("gfx1030"),
        Vec(1, 5, 6, 0x5a, 0, 0, 0, 0x3f),
        Instruction.VMadF32(
          VectorMadF32Operation.FusedMultiplyAdd,
          3,
          InstructionSource.VectorRegister(1),
          InstructionSource.VectorRegister(2),
          InstructionSource.Literal(0x3f000000L),
          SourceModifiers.Unmodified,
          false
        )
      ),
      (
        "gfx1030 FMAMK rounds once",
        Target("gfx1030"),
        Vec(1, 5, 6, 0x58, 0, 0, 0, 0x3f),
        Instruction.VMadF32(
          VectorMadF32Operation.FusedMultiplyAdd,
          3,
          InstructionSource.VectorRegister(1),
          InstructionSource.Literal(0x3f000000L),
          InstructionSource.VectorRegister(2),
          SourceModifiers.Unmodified,
          false
        )
      )
    )
  ) test(name) {
    assertEquals(VMCompiler.decodeInstruction(bytes.map(_.toByte), target), Right(expected))
  }

  test("VOP3 floating-point clamp uses the [0, 1] range and preserves NaN") {
    assertEquals(VMArithmetic.clampF32(-2.0f), 0.0f)
    assertEquals(VMArithmetic.clampF32(0.5f), 0.5f)
    assertEquals(VMArithmetic.clampF32(2.0f), 1.0f)
    assert(VMArithmetic.clampF32(Float.NaN).isNaN)
  }

  test("gfx900 VOPC SDWA unsigned 16-bit less-than-or-equal is supported") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0xf9.toByte, 0x04, 0x56, 0x7d, 0x03, 0x80.toByte, 0x86.toByte, 0x06),
      Target("gfx900")
    )

    instruction match {
      case Right(
            Instruction.VCompare(
              VectorComparison(VectorComparisonKind.Unsigned16, VectorComparisonRelation.LessOrEqual, false),
              _,
              _,
              _,
              _,
              Some(_)
            )
          ) => ()
      case other => fail(s"decoded VOPC SDWA unsigned less-than-or-equal was rejected: $other")
    }
  }

  test("gfx1036 packed unsigned 16-bit maximum is supported") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0x01, 0x00, 0x0c, 0xcc.toByte, 0x02, 0x07, 0x12, 0x04),
      Target("gfx1036")
    )

    instruction match {
      case Right(Instruction.VPackedBinary(operation = VectorPackedBinaryOperation.MaximumU16)) => ()
      case other => fail(s"decoded packed unsigned maximum was rejected: $other")
    }
  }

  test("VOP3 float comparisons keep source modifiers") {
    // LLVM gfx900: v_cmp_lt_f32_e64 s[0:1], v1, -v2.
    VMCompiler.decodeInstruction(
      Vec(0x00, 0x00, 0x41, 0xd0, 0x01, 0x05, 0x02, 0x40).map(_.toByte),
      Target("gfx900")
    ) match {
      case Right(Instruction.VCompare(modifiers = modifiers)) =>
        assertEquals(modifiers, SourceModifiers(absolute = 0, negate = 2))
      case other => fail(s"unexpected decoding: $other")
    }
  }

  test("f64 literal dwords provide the high half of a binary64 value") {
    assertEquals(VMArithmetic.floatingLiteralToDouble(0x40140000L), 5.0)
    assertEquals(VMArithmetic.floatingLiteralToDouble(0xc0040000L), -2.5)
    assert(VMArithmetic.floatingLiteralToDouble(0x7ff80000L).isNaN)
  }

  for (
    (name, target, bytes, message) <- Seq(
      (
        "unsupported VOP1 DPP operations",
        Target("gfx900"),
        Vec(0xfa, 0x0a, 0x0a, 0x7e, 0x01, 0xe4, 0x00, 0x30),
        "v_cvt_f32_i32 DPP execution is not implemented"
      ),
      (
        "VOP3 DPP16 instructions",
        Target("gfx1100"),
        Vec(0x05, 0x00, 0x55, 0xd6, 0xfa, 0x04, 0x0e, 0x04, 0x01, 0x1b, 0x00, 0xff),
        "v_add3_u32 DPP execution is not implemented"
      ),
      (
        "VOP3 DPP8 instructions",
        Target("gfx1100"),
        Vec(0x05, 0x00, 0x55, 0xd6, 0xe9, 0x04, 0x0e, 0x04, 0x01, 0x77, 0x39, 0x05),
        "v_add3_u32 DPP execution is not implemented"
      ),
      (
        "VOP3P DPP16 instructions",
        Target("gfx1100"),
        Vec(0x00, 0x00, 0x20, 0xcc, 0xfa, 0x04, 0x0e, 0x04, 0x01, 0xb1, 0x00, 0xff),
        "v_fma_mix_f32 DPP execution is not implemented"
      ),
      (
        "VOP3P DPP8 instructions",
        Target("gfx1100"),
        Vec(0x00, 0x00, 0x20, 0xcc, 0xe9, 0x04, 0x0e, 0x04, 0x01, 0x77, 0x39, 0x05),
        "v_fma_mix_f32 DPP execution is not implemented"
      ),
      (
        "op_sel on VOP3-encoded f16 operations before GFX11",
        Target("gfx900"),
        Vec(0x00, 0x48, 0x1f, 0xd1, 0x01, 0x05, 0x02, 0x00),
        "v_add_f16 op_sel is not implemented"
      ),
      (
        "op_sel on GFX10 VOP3-encoded f16 operations",
        Target("gfx1030"),
        Vec(0x00, 0x48, 0x32, 0xd5, 0x01, 0x05, 0x02, 0x00),
        "v_add_f16 op_sel is not implemented"
      ),
      (
        "clamp on integer VOP3 operations that define none",
        Target("gfx900"),
        Vec(0x02, 0x80, 0xff, 0xd1, 0x03, 0x09, 0x16, 0x04),
        "v_add3_u32 clamp is not implemented"
      ),
      (
        "VOP3 integer results with mul:2",
        Target("gfx900"),
        Vec(0x00, 0x00, 0xff, 0xd1, 0x01, 0x05, 0x0e, 0x0c),
        "v_add3_u32 output modifiers are not implemented"
      ),
      (
        "VOP3 comparisons with mul:2",
        Target("gfx900"),
        Vec(0x00, 0x00, 0x41, 0xd0, 0x01, 0x05, 0x02, 0x08),
        "v_cmp_lt_f32 output modifiers are not implemented"
      ),
      (
        "negation on integer VOP3 comparisons",
        Target("gfx900"),
        Vec(0x00, 0x00, 0xc1, 0xd0, 0x01, 0x05, 0x02, 0x20),
        "v_cmp_lt_i32 source modifiers are not implemented"
      ),
      (
        "absolute value on integer VOP3 comparisons",
        Target("gfx900"),
        Vec(0x00, 0x02, 0xc1, 0xd0, 0x01, 0x05, 0x02, 0x00),
        "v_cmp_lt_i32 source modifiers are not implemented"
      ),
      (
        "negation on the class mask of VOP3 class comparisons",
        Target("gfx900"),
        Vec(0x00, 0x00, 0x10, 0xd0, 0x01, 0x05, 0x02, 0x40),
        "v_cmp_class_f32 source modifiers are not implemented"
      ),
      (
        "negation on the integer exponent of ldexp",
        Target("gfx900"),
        Vec(0x00, 0x00, 0x88, 0xd2, 0x01, 0x05, 0x02, 0x40),
        "v_ldexp_f32 source modifiers are not implemented"
      ),
      (
        "negation on a third source that two-source VOP3 operations lack",
        Target("gfx900"),
        Vec(0x00, 0x00, 0x05, 0xd1, 0x01, 0x05, 0x02, 0x80),
        "v_mul_f32 source modifiers are not implemented"
      ),
      (
        "high-half negation on integer packed operations",
        Target("gfx900"),
        Vec(0x00, 0x41, 0x8a, 0xd3, 0x01, 0x05, 0x02, 0x18),
        "v_pk_add_u16 packed modifiers are not implemented"
      ),
      (
        "high-half negation on float packed operations",
        Target("gfx900"),
        Vec(0x00, 0x42, 0x8f, 0xd3, 0x01, 0x05, 0x02, 0x18),
        "v_pk_add_f16 packed modifiers are not implemented"
      ),
      (
        "negation on integer dot products",
        Target("gfx906"),
        Vec(0x00, 0x40, 0xa9, 0xd3, 0x01, 0x05, 0x0e, 0x3c),
        "v_dot4_u32_u8 packed modifiers are not implemented"
      ),
      (
        "negation on accumulator writes",
        Target("gfx90a"),
        Vec(0x00, 0x40, 0xd9, 0xd3, 0x01, 0x01, 0x00, 0x38),
        "v_accvgpr_write_b32 packed modifiers are not implemented"
      ),
      (
        "negation on integer SDWA comparisons",
        Target("gfx900"),
        Vec(0xf9, 0x08, 0x42, 0x7d, 0x03, 0x00, 0x04, 0x14),
        "v_cmp_lt_i16 SDWA source modifiers are not implemented"
      ),
      (
        "negation on integer VOP3 binary operations",
        Target("gfx900"),
        Vec(0x00, 0x00, 0x34, 0xd1, 0x01, 0x05, 0x02, 0x20),
        "v_add_u32 source modifiers are not implemented"
      ),
      (
        "absolute value on integer VOP3 sources",
        Target("gfx900"),
        Vec(0x00, 0x01, 0xff, 0xd1, 0x01, 0x05, 0x0e, 0x04),
        "v_add3_u32 source modifiers are not implemented"
      ),
      (
        "VOP3 instructions truncated before their literal",
        Target("gfx1010"),
        Vec(0x01, 0x01, 0x03, 0xd5, 0xff, 0x06, 0x02, 0x22),
        ""
      ),
      ("MADAK truncated before its literal", Target("gfx803"), Vec(1, 5, 6, 0x30), ""),
      ("immediate hardware-register writes truncated before their literal", Target("gfx803"), Vec(1, 9, 0, 0xba), "")
    )
  ) test(s"$target rejects $name") {
    val result = VMCompiler.decodeInstruction(bytes.map(_.toByte), target)
    assert(result.left.exists(_.contains(message)), result)
  }

  for (
    (name, target, bytes) <- Seq(
      (
        "SDWA 32-bit add with byte source selections",
        Target("gfx900"),
        Vec(0xf9, 0x00, 0x02, 0x68, 0x00, 0x06, 0x01, 0x00)
      ),
      ("packed low unsigned multiplication", Target("gfx900"), Vec(0xff, 0x40, 0x81, 0xd3, 0x01, 0x05, 0x02, 0x18)),
      (
        "scalar unsigned minimum with a literal operand",
        Target("gfx900"),
        Vec(0x02, 0xff, 0x84, 0x83, 0x00, 0x01, 0x00, 0x00)
      ),
      ("SDWA low 16-bit multiplication", Target("gfx900"), Vec(0xf9, 0x06, 0x2e, 0x52, 0x16, 0x06, 0x06, 0x05)),
      ("SDWA signed 16-bit maximum", Target("gfx900"), Vec(0xf9, 0x20, 0x12, 0x60, 0x05, 0x06, 0x08, 0x08)),
      ("SDWA half-precision minimum", Target("gfx900"), Vec(0xf9, 0x04, 0x02, 0x5c, 0x03, 0x04, 0x04, 0x04)),
      (
        "DPP quad, row-rotate, and row-broadcast sources",
        Target("gfx900"),
        Vec(
          0xfa, 0x02, 0x06, 0x7e, 0x01, 0xb1, 0x00, 0xff, 0xfa, 0x02, 0x0c, 0x7e, 0x05, 0x24, 0x01, 0xff, 0xfa, 0x02,
          0x0c, 0x7e, 0x05, 0x28, 0x01, 0xff, 0xfa, 0x02, 0x0c, 0x7e, 0x05, 0x42, 0x01, 0xff, 0xfa, 0x02, 0x0c, 0x7e,
          0x05, 0x43, 0x01, 0xff
        )
      ),
      // the literal must not be decoded as the next instruction
      ("immediate hardware-register writes with their literal", Target("gfx803"), Vec(1, 9, 0, 0xba, 3, 0, 0, 0)),
      ("MADAK with its literal", Target("gfx803"), Vec(1, 5, 6, 0x30, 0, 0, 0, 0x3f))
    )
  ) test(s"$target accepts $name") {
    assertEquals(fixtures.validate((bytes ++ Vec(0x00, 0x00, 0x81, 0xbf)).map(_.toByte), target), Right(()))
  }

  test("DPP routes quad, row-rotate, and row-broadcast source lanes") {
    assertEquals(
      Vec.range[0, 8].map(lane => VMArithmetic.dppQuadSourceLane(lane, 0xb1).raw),
      Vec(1, 0, 3, 2, 5, 4, 7, 6)
    )
    assert(Vec.range[0, 64].forall(lane => VMArithmetic.dppLaneEnabled(lane, rowMask = 0xf, bankMask = 0xf)))
    assert(!VMArithmetic.dppLaneEnabled(0, 0xe, 0xf))
    assert(VMArithmetic.dppLaneEnabled(1, 0xf, 0xd))
    assert(!VMArithmetic.dppLaneEnabled(4, 0xf, 0xd))
    def sources(lanes: Vec[VMState.Lane], control: Int): Vec[Option[Int]] =
      lanes.map { lane =>
        val source = VMArithmetic.dppSourceLane(lane, control)
        Option.when(source.isValid)(source.lane.raw)
      }
    assertEquals(sources(Vec(0, 4, 15, 16), 0x124), Vec(Some(12), Some(0), Some(11), Some(28)))
    assertEquals(sources(Vec(0, 16, 32, 48), 0x142), Vec(None, Some(15), Some(31), Some(47)))
    assertEquals(sources(Vec(0, 16, 32, 48), 0x143), Vec(None, None, Some(31), Some(31)))
  }
}
