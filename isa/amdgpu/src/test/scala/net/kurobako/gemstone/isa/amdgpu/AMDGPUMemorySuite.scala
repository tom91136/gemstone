package net.kurobako.gemstone.isa.amdgpu

import VMIR.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

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

final class AMDGPUMemorySuite extends munit.FunSuite with CodeObjectTestSupport {
  test("gfx900 DS decoding keeps the opcode separate from the full 16-bit offset") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x90.toByte, 0x6c, 0xd8.toByte, 0x00, 0x00, 0x00, 0x00),
        Target("gfx900")
      ),
      Right(Instruction.DSRead(0, 0, 4, 36864, None))
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0xff.toByte, 0xff.toByte, 0xbe.toByte, 0xd9.toByte, 0x00, 0x01, 0x00, 0x00),
        Target("gfx900")
      ),
      Right(Instruction.DSWrite(0, 1, None, 16, 65535, None))
    )
  }

  test("gfx900 MUBUF cache invalidation instructions validate") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x00,
          0x00,
          0xfc.toByte,
          0xe0.toByte,
          0x00,
          0x00,
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

  test("gfx900 MUBUF offen load validates") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x00,
          0x10,
          0x50,
          0xe0.toByte,
          0x01,
          0x07,
          0x00,
          0x80.toByte,
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

  test("gfx900 MUBUF load retains its scalar resource descriptor") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x10, 0x50, 0xe0.toByte, 0x12, 0x13, 0x05, 0x80.toByte),
        Target("gfx900")
      ),
      Right(Instruction.BufferLoad(19, Some(18), 20, None, 4, 0))
    )
  }

  test("gfx900 MUBUF short D16 decoding preserves resource and half selection") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x10, 0x10, 0x90.toByte, 0xe0.toByte, 0x03, 0x07, 0x05, 0x80.toByte),
        Target("gfx900")
      ),
      Right(Instruction.BufferLoadD16(7, Some(3), 20, None, 2, 16, high = false))
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x10, 0x10, 0x94.toByte, 0xe0.toByte, 0x03, 0x07, 0x05, 0x80.toByte),
        Target("gfx900")
      ),
      Right(Instruction.BufferLoadD16(7, Some(3), 20, None, 2, 16, high = true))
    )
  }

  test("gfx900 MUBUF short D16 loads replace only the selected half") {
    assertEquals(VMInterpreter.mergeD16(0x11223344L, 0xaabbL, high = false), 0x1122aabbL)
    assertEquals(VMInterpreter.mergeD16(0x11223344L, 0xaabbL, high = true), 0xaabb3344L)
    assertEquals(VMInterpreter.mergeD16(0xfeed11223344L, 0xffffaabbL, high = false), 0x1122aabbL)
  }

  test("gfx900 VOP2 unsigned 24-bit multiply-high validates") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x01,
          0x05,
          0x0a,
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

  test("gfx900 VOP3 signed maximum of three validates") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xff.toByte,
          0x00,
          0xd4.toByte,
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

  test("gfx900 VOP3 signed multiply-high validates") {
    assertEquals(
      VMCompiler.validate(
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
      Right(())
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

    instruction match {
      case Right(
            Instruction.VMadI64I32(
              254,
              12,
              InstructionSource.VectorRegister(1),
              InstructionSource.VectorRegister(2),
              InstructionSource.VectorRegister(3)
            )
          ) => ()
      case other => fail(s"decoded VOP3 signed 64-bit multiply-add incorrectly: $other")
    }
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

    assertEquals(instruction, Right(Instruction.VMove(0, InstructionSource.Constant(0L))))
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

  test("unsigned f32 conversion feeds the magnitude used by signed i64 lowering") {
    assertEquals(VMInterpreter.unsignedFloatToInt32(-3.0f), 3L)
    assertEquals(VMInterpreter.unsignedFloatToInt32(5.75f), 5L)
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
      IEEE754.binary16ToFloat(VMInterpreter.minimumF16(positive.value, negative.value)),
      -2.0f
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
    assert(!VMInterpreter.compare(notLess, one.value, two.value))
    assert(VMInterpreter.compare(notLess, two.value, one.value))
    assert(VMInterpreter.compare(notLess, 0x7e00L, one.value))
  }

}
