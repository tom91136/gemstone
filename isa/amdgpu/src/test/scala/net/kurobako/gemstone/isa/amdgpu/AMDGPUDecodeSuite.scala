package net.kurobako.gemstone.isa.amdgpu

import CodeObject.*
import ExecutableInstruction.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{Bits, ByteRange}
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

final class AMDGPUDecodeSuite extends munit.FunSuite with CodeObjectTestSupport {
  test("gfx900 conditional scalar move retains the destination when SCC is clear") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x05, 0x02, 0x84.toByte, 0xbe.toByte),
        Target("gfx900")
      ),
      Right(
        DecodedInstruction.SCSelect(
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
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x01, 0x0c, 0x84.toByte, 0xbe.toByte),
        Target("gfx900")
      ),
      Right(
        DecodedInstruction.SUnary(
          "s_bcnt1_i32_b32",
          4,
          InstructionSource.Scalar(1)
        )
      )
    )
  }

  test("gfx1030 explicit LGKM wait executes after synchronous memory operations") {
    // LLVM encoding of s_waitcnt_lgkmcnt null, 0, used by Tensile's gfx1030 GEMM.
    val wait = Vec[Byte](0x00, 0x00, 0x7d, 0xbd.toByte)
    assertEquals(DispatchInterpreter.decodeInstruction(wait, Target("gfx1030")), Right(DecodedInstruction.Wait))
    val code     = wait ++ Vec[Byte](0x00, 0x00, 0x81.toByte, 0xbf.toByte)
    val kernel   = CodeObjectKernel("wait", Target("gfx1030"), 0, 0, 0, 0, 0, code)
    val dispatch = KernelDispatch.validated(
      ProgramId(1L),
      KernelName("wait"),
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(DispatchInterpreter.execute(kernel, dispatch, TestMemory()), Right(()))
  }

  test("gfx1036 scalar OR-NOT b32 decodes as a scalar binary operation") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x05, 0x06, 0x04, 0x8b.toByte),
        Target("gfx1036")
      ),
      Right(
        DecodedInstruction.SBinary(
          "s_orn2_b32",
          4,
          InstructionSource.Scalar(5),
          InstructionSource.Scalar(6)
        )
      )
    )
  }

  test("gfx1036 scalar NOR b32 decodes as a scalar binary operation") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x05, 0x06, 0x04, 0x8d.toByte),
        Target("gfx1036")
      ),
      Right(
        DecodedInstruction.SBinary(
          "s_nor_b32",
          4,
          InstructionSource.Scalar(5),
          InstructionSource.Scalar(6)
        )
      )
    )
  }

  test("gfx1036 scalar signed 64-bit bit-field extract decodes") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x04, 0x06, 0x02, 0x95.toByte),
        Target("gfx1036")
      ),
      Right(
        DecodedInstruction.SBinary64(
          "s_bfe_i64",
          2,
          InstructionSource.Scalar(4),
          InstructionSource.Scalar(6)
        )
      )
    )
  }

  test("gfx1036 global atomic increment decodes its modulo operand") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x00, 0x80.toByte, 0xf0.toByte, 0xdc.toByte, 0x01, 0x02, 0x7d, 0x00),
        Target("gfx1036")
      ),
      Right(
        DecodedInstruction.GlobalAtomicInc(
          0,
          1,
          None,
          2,
          4,
          0,
          returnValue = false
        )
      )
    )
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x00, 0x80.toByte, 0xf1.toByte, 0xdc.toByte, 0x02, 0x03, 0x7d, 0x01),
        Target("gfx1036")
      ),
      Right(
        DecodedInstruction.GlobalAtomicInc(
          1,
          2,
          None,
          3,
          4,
          0,
          returnValue = true
        )
      )
    )
  }

  test("gfx1036 linked-text s_code_end padding decodes as a no-op") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x00, 0x00, 0x9f.toByte, 0xbf.toByte),
        Target("gfx1036")
      ),
      Right(DecodedInstruction.Wait)
    )
  }

  test("gfx1036 SMEM immediate offsets use the second-dword offset field") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x02, 0x02, 0x08, 0xf4.toByte, 0x10, 0x00, 0x00, 0xfa.toByte),
        Target("gfx1036")
      ),
      Right(DecodedInstruction.SLoad(8, 4, 4, 16, None))
    )
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x02, 0x00, 0x04, 0xf4.toByte, 0x00, 0x00, 0x00, 0xfa.toByte),
        Target("gfx1036")
      ),
      Right(DecodedInstruction.SLoad(0, 2, 4, 0, None))
    )
  }

  test("gfx1036 global dwordx4 retains its vector width") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x00, 0x80.toByte, 0x78, 0xdc.toByte, 0x17, 0x0d, 0x7d, 0x00),
        Target("gfx1036")
      ),
      Right(DecodedInstruction.GlobalStore(23, None, 13, 16, 0))
    )
  }

  test("gfx1036 global dword retains its scalar width") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x00, 0x80.toByte, 0x70, 0xdc.toByte, 0x40, 0x09, 0x7d, 0x00),
        Target("gfx1036")
      ),
      Right(DecodedInstruction.GlobalStore(64, None, 9, 4, 0))
    )
  }

  test("gfx900 scalar set-PC decodes its 64-bit source") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x1e, 0x1d, 0x80.toByte, 0xbe.toByte),
        Target("gfx900")
      ),
      Right(DecodedInstruction.SSetPc(InstructionSource.Scalar(30)))
    )
  }

  test("gfx900 scalar priority hint validates as a virtual no-op") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x41, 0x31, 0x8f.toByte, 0xbf.toByte),
        Target("gfx900")
      ),
      Right(DecodedInstruction.Wait)
    )
  }

  test("gfx900 VOP3 CMPX reuses the VOPC opcode and decodes its scalar mask destination") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x36, 0x00, 0xda.toByte, 0xd0.toByte, 0x0c, 0x17, 0x02, 0x00),
        Target("gfx900")
      ),
      Right(
        DecodedInstruction.VCompareTo(
          54,
          "v_cmpx_eq_u32",
          InstructionSource.VectorRegister(12),
          InstructionSource.VectorRegister(11),
          false,
          false,
          false,
          false
        )
      )
    )
  }

  test("gfx1036 VOP3 lg-f32 comparison decodes for miniBUDE") {
    assertEquals(
      DispatchInterpreter.decodeInstruction(
        Vec[Byte](0x08, 0x00, 0x05, 0xd4.toByte, 0x80.toByte, 0x38, 0x02, 0x00),
        Target("gfx1036")
      ),
      Right(
        DecodedInstruction.VCompareTo(
          8,
          "v_cmp_lg_f32",
          InstructionSource.Constant(0),
          InstructionSource.VectorRegister(28),
          false,
          false,
          false,
          false
        )
      )
    )
  }

}
