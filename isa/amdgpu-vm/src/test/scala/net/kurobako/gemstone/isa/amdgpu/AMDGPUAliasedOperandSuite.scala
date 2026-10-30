package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Lane kernels that hold their operands in scratch shared between lanes and instructions: each lane must read what its
  * own instruction names, with the destination overlapping a source where the encoding allows. Bytes from LLVM 22
  * `llvm-mc -triple=amdgcn -mcpu=<target> -show-encoding`.
  */
class AMDGPUAliasedOperandSuite extends Suite {
  private val gfx900  = Target("gfx900")
  private val gfx950  = Target("gfx950")
  private val gfx1030 = Target("gfx1030")
  private val gfx1100 = Target("gfx1100")

  for (
    (target, code) <- Seq(
      gfx1030 -> Vec(0x02, 0x00, 0x77, 0xd7, 0x02, 0x01, 0x04, 0x00),
      gfx1100 -> Vec(0x02, 0x00, 0x5b, 0xd6, 0x02, 0x01, 0x04, 0x00)
    )
  )
    test(s"$target v_permlane16_b32 v2, v2, s0, s1 reads every lane's source before any lane writes") {
      import InstructionSource.*
      val select = Vec(Instruction.SMove(0, Constant(0x89abcdefL)), Instruction.SMove(1, Constant(0x01234567L)))
      val result = laneResults(
        target,
        Vec.tabulate(64)(lane => Vec(lane * 10L)),
        select ++ program(target, octets(code*)),
        2
      )
      assertEquals(result.take(32), Vec.tabulate(32)(lane => ((lane & ~15) + 15 - (lane & 15)) * 10L))
    }

  test("a block conversion over its own source register gives each lane what the lane gives alone") {
    // v_cvt_scalef32_pk_f32_fp8 v[2:3], v2, v4
    val code   = program(gfx950, octets(0x02, 0x00, 0x39, 0xd2, 0x02, 0x09, 0x02, 0x00))
    val inputs = Vec.tabulate(64)(lane => Vec(0x3800L + lane * 0x0301L, 0x11111111L * (lane % 7), 0x7cL + lane % 5))
    for (register <- Vec(2, 3)) {
      val together = laneResults(gfx950, inputs, code, register.unsafe[VectorRegisterIndex], wave32 = false)
      val alone    = inputs.map(lane => laneResults(gfx950, Vec(lane), code, register.unsafe[VectorRegisterIndex]).head)
      assertEquals(together, alone)
    }
  }

  test("an SDWA source selects and extends the part of its own lane's register") {
    // v_add_u32_sdwa v2, sext(v3), v4 dst_sel:DWORD dst_unused:UNUSED_PAD src0_sel:BYTE_1 src1_sel:WORD_1
    val code   = program(gfx900, octets(0xf9, 0x08, 0x04, 0x68, 0x03, 0x06, 0x09, 0x05))
    val inputs = Vec.tabulate(64)(lane => (0x01234567L * (lane + 1)) & 0xffffffffL)
    val result = laneResults(gfx900, inputs.map(x => Vec(0L, x, x * 3 & 0xffffffffL)), code, 2)
    assertEquals(
      result,
      inputs.map(x => ((x >>> 8).toByte.toLong + (((x * 3 & 0xffffffffL) >>> 16) & 0xffffL)) & 0xffffffffL)
    )
  }

  test("an image instruction reads the descriptor in the registers when it runs") {
    // image_get_resinfo v2, v0, s[16:23] dmask:0x1 dim:SQ_RSRC_IMG_2D unorm, then into v3 once the width changes
    def descriptor(width: Long): Vec[Instruction] = {
      val words = Vec(0L, (60L << 20) | (((width - 1) & 3) << 30), (width - 1) >>> 2 | (1L << 14), 0xfacL | (9L << 28))
      (words ++ Vec(3L, 0L, 0L, 0L)).zipWithIndex.map((word, index) =>
        Instruction.SMove((16 + index).unsafe[ScalarRegisterIndex], InstructionSource.Constant(word))
      )
    }
    val code = descriptor(3) ++ program(gfx1030, octets(0x08, 0x11, 0x38, 0xf0, 0x00, 0x02, 0x04, 0x00)) ++
      descriptor(7) ++ program(gfx1030, octets(0x08, 0x11, 0x38, 0xf0, 0x00, 0x03, 0x04, 0x00))
    assertEquals(
      Vec(2, 3).map(register => laneResults(gfx1030, Vec(Vec.empty), code, register.unsafe[VectorRegisterIndex]).head),
      Vec(3L, 7L)
    )
  }

  test("a buffer access reads the descriptor in the registers when its instruction runs") {
    val target                                                        = gfx900
    val memory                                                        = RegionMemory()
    val input                                                         = AllocationId(1L)
    val output                                                        = AllocationId(2L)
    def descriptor(start: Long >= 0, records: Long): Vec[Instruction] = {
      val base = memory.address(input, start).value.raw
      Vec(
        Instruction.SMove(8, InstructionSource.Constant(base & 0xffffffffL)),
        Instruction.SMove(9, InstructionSource.Constant(base >>> 32)),
        Instruction.SMove(10, InstructionSource.Constant(records)),
        Instruction.SMove(11, InstructionSource.Constant(0x27000L))
      )
    }
    memory.allocate(input, 256)
    memory.allocate(output, 1024)
    (0 until 64).foreach(index => memory.putInt(input, (index * 4).unsafe[Int >= 0], 100 + index))
    val outputAddress                   = memory.address(output, 0L).value.raw
    val buffer                          = MemoryAddress.Buffer(Some(1), 8, None, 0)
    def store(source: Int, offset: Int) = Instruction.Store(
      source.unsafe[VectorRegisterIndex],
      MemoryAddress.Global(4, Some(6), offset),
      4,
      false
    )
    val operations = descriptor(0L, 40) ++ Vec(
      Instruction.SMove64(6, InstructionSource.Constant(outputAddress)),
      Instruction.VBinary(
        VectorBinaryOperation.ShiftLeft32,
        1,
        InstructionSource.Constant(2),
        InstructionSource.VectorRegister(0),
        false
      ),
      Instruction.VBinary(
        VectorBinaryOperation.ShiftLeft32,
        4,
        InstructionSource.Constant(3),
        InstructionSource.VectorRegister(0),
        false
      ),
      Instruction.Load(2, buffer, 4, false, LoadPlacement.Full)
    ) ++ descriptor(64L, 160) ++ Vec(
      Instruction.Load(3, buffer, 4, false, LoadPlacement.Full),
      store(source = 2, offset = 0),
      store(source = 3, offset = 512),
      Instruction.End
    )
    val task = KernelTask.validated("kernel", Dimensions(64, 1, 1), Dimensions.One, Vec.empty).value
    assertEquals(runIR(operations, memory, codeKernel(target = target), task), Right(()))
    assertEquals(
      (0 until 32).map(lane => memory.getInt(output, (lane * 8).unsafe[Int >= 0])),
      (0 until 32).map(lane => if (lane < 10) 100 + lane else 0)
    )
    assertEquals(
      (0 until 32).map(lane => memory.getInt(output, (512 + lane * 8).unsafe[Int >= 0])),
      (0 until 32).map(lane => if (lane < 40) 116 + lane else 0)
    )
  }
}
