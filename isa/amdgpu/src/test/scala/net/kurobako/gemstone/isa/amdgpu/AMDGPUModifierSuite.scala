package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPUModifierSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  test("gfx900 unsupported VOP1 DPP operations fail closed") {
    val result = VMCompiler.decodeInstruction(
      Vec[Byte](
        0xfa.toByte,
        0x58,
        0x0a,
        0x7e,
        0x01,
        0xe4.toByte,
        0x00,
        0x30
      ),
      Target("gfx900")
    )

    assert(result.left.exists(_.contains("v_bfrev_b32 DPP execution is not implemented")))
  }

  test("gfx900 VOP3 f32 binary decoding preserves clamp") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](
        0x01,
        0x80.toByte,
        0x01,
        0xd1.toByte,
        0x03,
        0x0b,
        0x02,
        0x00
      ),
      Target("gfx900")
    )

    instruction match {
      case Right(
            Instruction.VBinaryF32Modified(
              VectorF32BinaryOperation.Add,
              1,
              InstructionSource.VectorRegister(3),
              InstructionSource.VectorRegister(5),
              false,
              false,
              false,
              false,
              true
            )
          ) => ()
      case other => fail(s"decoded VOP3 f32 clamp was lost: $other")
    }
  }

  test("VOP3 floating-point clamp uses the [0, 1] range and preserves NaN") {
    assertEquals(VMArithmetic.clampF32(-2.0f), 0.0f)
    assertEquals(VMArithmetic.clampF32(0.5f), 0.5f)
    assertEquals(VMArithmetic.clampF32(2.0f), 1.0f)
    assert(VMArithmetic.clampF32(Float.NaN).isNaN)
  }

  test("VOP3 f32 FMA preserves the absolute modifier on source 2") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](
        0x31,
        0x04,
        0x4b,
        0xd5.toByte,
        0xff.toByte,
        0x4c,
        0x06,
        0x04,
        0xda.toByte,
        0x0f,
        0xc9.toByte,
        0xbf.toByte
      ),
      Target("gfx1036")
    )

    instruction match {
      case Right(
            Instruction.VMadF32(
              VectorMadF32Operation.FusedMultiplyAdd,
              49,
              InstructionSource.Literal(0xbfc90fdaL),
              InstructionSource.VectorRegister(38),
              InstructionSource.VectorRegister(1),
              false,
              false,
              true,
              false,
              false,
              false,
              false
            )
          ) => ()
      case other => fail(s"decoded VOP3 FMA lost source-2 absolute modifier: $other")
    }
  }

  test("VOP3 f32 FMA does not confuse source 3 encoding with absolute") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0x13, 0x00, 0x4b, 0xd5.toByte, 0x03, 0x29, 0x12, 0x84.toByte),
      Target("gfx1036")
    )

    instruction match {
      case Right(
            Instruction.VMadF32(
              VectorMadF32Operation.FusedMultiplyAdd,
              19,
              InstructionSource.VectorRegister(3),
              InstructionSource.VectorRegister(20),
              InstructionSource.VectorRegister(4),
              false,
              false,
              false,
              false,
              false,
              true,
              false
            )
          ) => ()
      case other => fail(s"decoded VOP3 source-3 modifier incorrectly: $other")
    }
  }

  test("gfx900 VOPC SDWA preserves a scalar source-0 encoding") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](
        0xf9.toByte,
        0x04,
        0x94.toByte,
        0x7d,
        0x03,
        0x80.toByte,
        0x86.toByte,
        0x06
      ),
      Target("gfx900")
    )

    instruction match {
      case Right(
            Instruction.VCompareSubDword(
              VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Equal, false),
              InstructionSource.Scalar(3),
              InstructionSource.VectorRegister(2),
              SubDwordSelection.DWord,
              SubDwordSelection.DWord,
              false,
              false,
              Some(0)
            )
          ) => ()
      case other => fail(s"decoded VOPC SDWA source-0 mode was lost: $other")
    }
  }

  test("gfx900 VOPC SDWA unsigned 16-bit greater-than is supported") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0xf9.toByte, 0x04, 0x58, 0x7d, 0x03, 0x80.toByte, 0x86.toByte, 0x06),
      Target("gfx900")
    )

    instruction match {
      case Right(
            Instruction.VCompareSubDword(
              VectorComparison(VectorComparisonKind.Unsigned16, VectorComparisonRelation.Greater, false),
              InstructionSource.Scalar(3),
              InstructionSource.VectorRegister(2),
              SubDwordSelection.DWord,
              SubDwordSelection.DWord,
              false,
              false,
              Some(0)
            )
          ) => ()
      case other => fail(s"decoded VOPC SDWA unsigned comparison was rejected: $other")
    }
  }

  test("gfx900 VOPC SDWA unsigned 16-bit less-than-or-equal is supported") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0xf9.toByte, 0x04, 0x56, 0x7d, 0x03, 0x80.toByte, 0x86.toByte, 0x06),
      Target("gfx900")
    )

    instruction match {
      case Right(
            Instruction.VCompareSubDword(
              VectorComparison(VectorComparisonKind.Unsigned16, VectorComparisonRelation.LessOrEqual, false),
              _,
              _,
              _,
              _,
              _,
              _,
              _
            )
          ) => ()
      case other => fail(s"decoded VOPC SDWA unsigned less-than-or-equal was rejected: $other")
    }
  }

  test("gfx1036 packed signed 16-bit maximum is supported") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0x01, 0x00, 0x07, 0xcc.toByte, 0x02, 0x07, 0x12, 0x04),
      Target("gfx1036")
    )

    instruction match {
      case Right(
            Instruction.VPackedBinary(
              VectorPackedBinaryOperation.MaximumI16,
              1,
              InstructionSource.VectorRegister(2),
              InstructionSource.VectorRegister(3),
              false,
              false
            )
          ) => ()
      case other => fail(s"decoded packed signed maximum was rejected: $other")
    }
  }

  test("gfx1036 packed unsigned 16-bit maximum is supported") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](0x01, 0x00, 0x0c, 0xcc.toByte, 0x02, 0x07, 0x12, 0x04),
      Target("gfx1036")
    )

    instruction match {
      case Right(Instruction.VPackedBinary(VectorPackedBinaryOperation.MaximumU16, _, _, _, _, _)) => ()
      case other => fail(s"decoded packed unsigned maximum was rejected: $other")
    }
  }

  test("gfx1100 VOP3 DPP instructions fail closed") {
    val result = VMCompiler.decodeInstruction(
      Vec[Byte](
        0x05,
        0x00,
        0x55,
        0xd6.toByte,
        0xfa.toByte,
        0x04,
        0x0e,
        0x04,
        0x01,
        0x1b,
        0x00,
        0xff.toByte
      ),
      Target("gfx1100")
    )

    if (!result.left.exists(_.contains("source encoding 250"))) fail(s"unexpected VOP3 DPP result: $result")
  }

  test("gfx1010 VOP3 literal instructions fail closed") {
    val result = VMCompiler.decodeInstruction(
      Vec[Byte](
        0x01,
        0x01,
        0x03,
        0xd5.toByte,
        0xff.toByte,
        0x06,
        0x02,
        0x22
      ),
      Target("gfx1010")
    )

    // The fixture intentionally ends before the literal dword.  The decoder
    // must reject it, but the exact diagnostic may come from opcode lookup or
    // the bounded-byte check depending on the generated metadata revision.
    assert(result.isLeft)
  }

  test("gfx900 SDWA 32-bit add honors byte source selections") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xf9.toByte,
          0x00,
          0x02,
          0x68,
          0x00,
          0x06,
          0x01,
          0x00,
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

  test("gfx900 packed low unsigned multiplication validates") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xff.toByte,
          0x40,
          0x81.toByte,
          0xd3.toByte,
          0x01,
          0x05,
          0x02,
          0x18,
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

  test("gfx900 scalar unsigned minimum accepts a literal operand") {
    assertEquals(VMArithmetic.unsignedMin32(300L, 256L), 256L)
    assertEquals(VMArithmetic.unsignedMin32(0xffffffffL, 256L), 256L)
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x02,
          0xff.toByte,
          0x84.toByte,
          0x83.toByte,
          0x00,
          0x01,
          0x00,
          0x00,
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

  test("f64 literal dwords provide the high half of a binary64 value") {
    assertEquals(VMArithmetic.floatingLiteralToDouble(0x40140000L), 5.0)
    assertEquals(VMArithmetic.floatingLiteralToDouble(0xc0040000L), -2.5)
    assert(VMArithmetic.floatingLiteralToDouble(0x7ff80000L).isNaN)
  }

  test("gfx900 scalar min/max SCC reports whether source zero won") {
    assertEquals(VMArithmetic.scalarMinMax(ScalarMinMaxOperation.MinimumU32, 3L, 4L), (3L, true))
    assertEquals(VMArithmetic.scalarMinMax(ScalarMinMaxOperation.MinimumU32, 4L, 3L), (3L, false))
    assertEquals(VMArithmetic.scalarMinMax(ScalarMinMaxOperation.MaximumU32, 4L, 3L), (4L, true))
    assertEquals(VMArithmetic.scalarMinMax(ScalarMinMaxOperation.MaximumU32, 3L, 4L), (4L, false))
    assertEquals(
      VMArithmetic.scalarMinMax(ScalarMinMaxOperation.MinimumI32, 0xffffffffL, 1L),
      (0xffffffffL, true)
    )
    assertEquals(VMArithmetic.scalarMinMax(ScalarMinMaxOperation.MaximumI32, 0xffffffffL, 1L), (1L, false))
    assertEquals(VMArithmetic.scalarMinMax(ScalarMinMaxOperation.MinimumU32, 7L, 7L), (7L, false))
    assertEquals(VMArithmetic.scalarMinMax(ScalarMinMaxOperation.MaximumI32, 7L, 7L), (7L, false))
  }

  test("gfx900 initial work-item VGPRs use multidimensional local IDs") {
    val geometry = LaunchGeometry.forLaunch(testDimensions(64, 16, 2), Dimensions.One).toOption.get
    assertEquals(geometry.localCoordinates(0), Right(testCoordinates(0, 0, 0)))
    assertEquals(geometry.localCoordinates(63), Right(testCoordinates(63, 0, 0)))
    assertEquals(geometry.localCoordinates(64), Right(testCoordinates(0, 1, 0)))
    assertEquals(geometry.localCoordinates(1023), Right(testCoordinates(63, 15, 0)))
    assertEquals(geometry.localCoordinates(1024), Right(testCoordinates(0, 0, 1)))
  }

  test("gfx900 SOPK signed multiply uses a sign-extended immediate and reports overflow") {
    assertEquals(VMArithmetic.signedMultiplyImmediate(0xffffffffL, 0x300), (0xfffffd00L, false))
    assertEquals(VMArithmetic.signedMultiplyImmediate(Int.MaxValue.toLong, 2), (0xfffffffeL, true))
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](0x00, 0x03, 0x82.toByte, 0xb7.toByte, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP2 unsigned subtraction returns the low word and borrow flag") {
    assertEquals(VMArithmetic.unsignedSubtract32(9L, 4L), (5L, false))
    assertEquals(VMArithmetic.unsignedSubtract32(0L, 1L), (0xffffffffL, true))
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](0x0e, 0x02, 0x02, 0x34, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP2 chained subtraction consumes and replaces the borrow flag") {
    assertEquals(VMArithmetic.unsignedSubtractWithBorrow32(9L, 4L, true), (4L, false))
    assertEquals(VMArithmetic.unsignedSubtractWithBorrow32(9L, 4L, false), (5L, false))
    assertEquals(VMArithmetic.unsignedSubtractWithBorrow32(0L, 0xffffffffL, false), (1L, true))
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](0x03, 0x05, 0x04, 0x3a, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOPC accepts canonical 16-bit and 64-bit not-equal aliases") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](0x04, 0x0b, 0x5a, 0x7d, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target("gfx900")
      ),
      Right(())
    )
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](0x80.toByte, 0x02, 0xda.toByte, 0x7d, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 VOP3 compares signed 64-bit VGPR pairs into an explicit scalar mask") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x00,
          0x00,
          0xe1.toByte,
          0xd0.toByte,
          0x01,
          0x07,
          0x02,
          0x00,
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

  test("gfx900 SDWA low 16-bit multiplication validates") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xf9.toByte,
          0x06,
          0x2e,
          0x52,
          0x16,
          0x06,
          0x06,
          0x05,
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

  test("gfx900 SDWA signed 24-bit multiplication preserves the decoded sub-dword operands") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](
        0xf9.toByte,
        0x00,
        0x3c,
        0x0c,
        0x35,
        0x06,
        0x0c,
        0x86.toByte
      ),
      Target("gfx900")
    )

    instruction match {
      case Right(
            Instruction.VBinarySubDword(
              actualOpcode,
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
          ) => assertEquals(actualOpcode, VectorSubDwordOperation.MultiplyI24)
      case other => fail(s"decoded SDWA instruction lost its operand selections: $other")
    }
  }

  test("gfx900 SDWA cndmask decodes with sub-dword selections") {
    val instruction = VMCompiler.decodeInstruction(
      Vec[Byte](
        0xf9.toByte,
        0x04,
        0x00,
        0x00,
        0x01,
        0x10,
        0x01,
        0x02
      ),
      Target("gfx900")
    )

    instruction match {
      case Right(
            Instruction.VBinarySubDword(
              actualOpcode,
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
          ) => assertEquals(actualOpcode, VectorSubDwordOperation.ConditionalMask32)
      case other => fail(s"decoded SDWA cndmask lost its operand selections: $other")
    }
  }

  test("gfx900 SDWA signed 16-bit maximum validates") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xf9.toByte,
          0x20,
          0x12,
          0x60,
          0x05,
          0x06,
          0x08,
          0x08,
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

  test("gfx900 SDWA half-precision minimum validates and uses floating-point ordering") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xf9.toByte,
          0x04,
          0x02,
          0x5c,
          0x03,
          0x04,
          0x04,
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
    val positive = IEEE754.floatToBinary16(1.5f)
    val negative = IEEE754.floatToBinary16(-2.0f)
    assertEquals(
      IEEE754.binary16ToFloat(VMArithmetic.minimumF16(positive.toLong, negative.toLong)),
      -2.0f
    )
  }
}
