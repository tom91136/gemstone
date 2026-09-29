package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPUDecodeSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  test("gfx900 conditional scalar move retains the destination when SCC is clear") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x05, 0x02, 0x84.toByte, 0xbe.toByte),
        Target("gfx900")
      ),
      Right(
        Instruction.SCSelect(
          4,
          InstructionSource.Scalar(5),
          InstructionSource.Scalar(4),
          32
        )
      )
    )
  }

  test("gfx900 scalar bit-count b32 decodes as a scalar unary operation") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x01, 0x0c, 0x84.toByte, 0xbe.toByte),
        Target("gfx900")
      ),
      Right(
        Instruction.SUnary(
          ScalarUnaryOperation.CountBits32,
          4,
          InstructionSource.Scalar(1)
        )
      )
    )
  }

  test("gfx1030 explicit LGKM wait executes after synchronous memory operations") {
    // LLVM encoding of s_waitcnt_lgkmcnt null, 0, used by Tensile's gfx1030 GEMM.
    val wait = Vec[Byte](0x00, 0x00, 0x7d, 0xbd.toByte)
    assertEquals(VMCompiler.decodeInstruction(wait, Target("gfx1030")), Right(Instruction.Wait))
    val code   = wait ++ Vec[Byte](0x00, 0x00, 0x81.toByte, 0xbf.toByte)
    val kernel = CodeObjectKernel(
      name = "wait",
      target = Target("gfx1030"),
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = code
    )
    val dispatch = KernelTask.validated(
      "wait",
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory()), Right(()))
  }

  test("gfx1036 scalar OR-NOT b32 decodes as a scalar binary operation") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x05, 0x06, 0x04, 0x8b.toByte),
        Target("gfx1036")
      ),
      Right(
        Instruction.SBinary(
          ScalarBinaryOperation.OrNot32,
          4,
          InstructionSource.Scalar(5),
          InstructionSource.Scalar(6)
        )
      )
    )
  }

  test("gfx1036 scalar NOR b32 decodes as a scalar binary operation") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x05, 0x06, 0x04, 0x8d.toByte),
        Target("gfx1036")
      ),
      Right(
        Instruction.SBinary(
          ScalarBinaryOperation.Nor32,
          4,
          InstructionSource.Scalar(5),
          InstructionSource.Scalar(6)
        )
      )
    )
  }

  test("gfx1036 scalar signed 64-bit bit-field extract decodes") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x04, 0x06, 0x02, 0x95.toByte),
        Target("gfx1036")
      ),
      Right(
        Instruction.SBinary64(
          ScalarBinary64Operation.BitFieldExtractI64,
          2,
          InstructionSource.Scalar(4),
          InstructionSource.Scalar(6)
        )
      )
    )
  }

  test("gfx1036 global atomic increment decodes its modulo operand") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x80.toByte, 0xf0.toByte, 0xdc.toByte, 0x01, 0x02, 0x7d, 0x00),
        Target("gfx1036")
      ),
      Right(
        Instruction.GlobalAtomicInc(
          0,
          1,
          None,
          2,
          4: Int where Between[4, 8],
          0,
          returnValue = false
        )
      )
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x80.toByte, 0xf1.toByte, 0xdc.toByte, 0x02, 0x03, 0x7d, 0x01),
        Target("gfx1036")
      ),
      Right(
        Instruction.GlobalAtomicInc(
          1,
          2,
          None,
          3,
          4: Int where Between[4, 8],
          0,
          returnValue = true
        )
      )
    )
  }

  test("gfx1036 linked-text s_code_end padding decodes as a no-op") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x00, 0x9f.toByte, 0xbf.toByte),
        Target("gfx1036")
      ),
      Right(Instruction.Wait)
    )
  }

  test("gfx1036 SMEM immediate offsets use the second-dword offset field") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x02, 0x02, 0x08, 0xf4.toByte, 0x10, 0x00, 0x00, 0xfa.toByte),
        Target("gfx1036")
      ),
      Right(Instruction.SLoad(dest = 8, count = 4, base = 4, offset = 16, scalarOffset = None))
    )
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x02, 0x00, 0x04, 0xf4.toByte, 0x00, 0x00, 0x00, 0xfa.toByte),
        Target("gfx1036")
      ),
      Right(Instruction.SLoad(dest = 0, count = 2, base = 4, offset = 0, scalarOffset = None))
    )
  }

  test("gfx1036 global dwordx4 retains its vector width") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x80.toByte, 0x78, 0xdc.toByte, 0x17, 0x0d, 0x7d, 0x00),
        Target("gfx1036")
      ),
      Right(Instruction.GlobalStore(23, None, 13, 16: Int where Between[1, 16], 0))
    )
  }

  test("gfx1036 global dword retains its scalar width") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x80.toByte, 0x70, 0xdc.toByte, 0x40, 0x09, 0x7d, 0x00),
        Target("gfx1036")
      ),
      Right(Instruction.GlobalStore(64, None, 9, 4: Int where Between[1, 16], 0))
    )
  }

  test("gfx900 scalar set-PC decodes its 64-bit source") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x1e, 0x1d, 0x80.toByte, 0xbe.toByte),
        Target("gfx900")
      ),
      Right(Instruction.SSetPc(InstructionSource.Scalar(30)))
    )
  }

  test("gfx900 scalar priority hint validates as a virtual no-op") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x41, 0x31, 0x8f.toByte, 0xbf.toByte),
        Target("gfx900")
      ),
      Right(Instruction.Wait)
    )
  }

  test("gfx900 VOP3 CMPX reuses the VOPC opcode and decodes its scalar mask destination") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x36, 0x00, 0xda.toByte, 0xd0.toByte, 0x0c, 0x17, 0x02, 0x00),
        Target("gfx900")
      ),
      Right(
        Instruction.VCompareTo(
          dest = 54,
          operation = VectorComparison(
            kind = VectorComparisonKind.Unsigned32,
            relation = VectorComparisonRelation.Equal,
            updateExec = true
          ),
          srcA = InstructionSource.VectorRegister(12),
          srcB = InstructionSource.VectorRegister(11),
          absA = false,
          absB = false,
          negateA = false,
          negateB = false
        )
      )
    )
  }

  test("gfx1036 VOP3 lg-f32 comparison decodes for miniBUDE") {
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x08, 0x00, 0x05, 0xd4.toByte, 0x80.toByte, 0x38, 0x02, 0x00),
        Target("gfx1036")
      ),
      Right(
        Instruction.VCompareTo(
          dest = 8,
          operation = VectorComparison(
            kind = VectorComparisonKind.Float32,
            relation = VectorComparisonRelation.NotEqual,
            updateExec = false
          ),
          srcA = InstructionSource.Constant(0),
          srcB = InstructionSource.VectorRegister(28),
          absA = false,
          absB = false,
          negateA = false,
          negateB = false
        )
      )
    )
  }

}
