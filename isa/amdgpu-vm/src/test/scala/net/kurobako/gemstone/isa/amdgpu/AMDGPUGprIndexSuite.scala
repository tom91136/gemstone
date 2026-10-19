package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** GFX9 VGPR indexing mode, which hipcc emits for a dynamic index into a vector held in registers, e.g.
  * `typedef float f16v __attribute__((ext_vector_type(16))); out[i] = v[k & 15];` under
  * `hipcc --offload-arch=gfx906 -O3`, which reads `s_set_gpr_idx_on s4, gpr_idx(SRC0)`, `v_mov_b32_e32 v19, v1`,
  * `s_set_gpr_idx_off` and writes with `gpr_idx(DST)`.
  */
class AMDGPUGprIndexSuite extends munit.FunSuite {
  // llvm-mc -triple=amdgcn -mcpu=gfx906 -show-encoding
  private val onSource        = Vec(0x00, 0x01, 0x11, 0xbf) // s_set_gpr_idx_on s0, gpr_idx(SRC0)
  private val onDestination   = Vec(0x00, 0x08, 0x11, 0xbf) // s_set_gpr_idx_on s0, gpr_idx(DST)
  private val off             = Vec(0x00, 0x00, 0x9c, 0xbf) // s_set_gpr_idx_off
  private val read            = Vec(0x02, 0x03, 0x18, 0x7e) // v_mov_b32_e32 v12, v2
  private val write           = Vec(0x0a, 0x03, 0x04, 0x7e) // v_mov_b32_e32 v2, v10
  private val index           = Vec(0x01, 0x32, 0x80, 0xbe) // s_set_gpr_idx_idx s1
  private val sourceMode      = Vec(0x01, 0x00, 0x9d, 0xbf) // s_set_gpr_idx_mode gpr_idx(SRC0)
  private val destinationMode = Vec(0x08, 0x00, 0x9d, 0xbf) // s_set_gpr_idx_mode gpr_idx(DST)

  private val registers = Vec.tabulate(8)(index => 10L + index) :+ 99L

  private def run(target: Target, s0: Long, s1: Long, result: Int, code: Vec[Int]*)(using munit.Location): Long = {
    import InstructionSource.*
    val setup = Vec(Instruction.SMove(0, Constant(s0)), Instruction.SMove(1, Constant(s1)))
    laneResult(target, registers, setup ++ program(target, octets(code.flatten*)), result.unsafe[VectorRegisterIndex])
  }

  for (target <- Seq(Target("gfx906"), Target("gfx90a"), Target("gfx942"))) {
    test(s"$target reads the VGPR M0 offsets in SRC0 indexing mode") {
      assertEquals(run(target, 5, 0, 12, onSource, read, off), 15L)
      assertEquals(run(target, 0, 0, 12, onSource, read, off), 10L)
      // outside the mode the move reads its own register
      assertEquals(run(target, 5, 0, 12, onSource, off, read), 10L)
    }
    test(s"$target writes the VGPR M0 offsets in DST indexing mode") {
      assertEquals(run(target, 3, 0, 5, onDestination, write, off), 99L)
      assertEquals(run(target, 3, 0, 2, onDestination, write, off), 10L)
    }
    test(s"$target s_set_gpr_idx_idx and s_set_gpr_idx_mode change the index and the indexed operand") {
      assertEquals(run(target, 5, 2, 12, onSource, index, read, off), 12L)
      assertEquals(run(target, 4, 0, 6, onSource, destinationMode, write, off), 99L)
      assertEquals(run(target, 4, 0, 12, onDestination, sourceMode, read, off), 14L)
    }
  }

  // elsewhere hipcc emits v_movrels_b32 for the same read after an s_and_b32 into M0, which RDNA3 onwards encodes as
  // s125 rather than s124
  private val movrels  = Vec(0x02, 0x87, 0x08, 0x7e) // v_movrels_b32_e32 v4, v2
  private val movreld  = Vec(0x04, 0x85, 0x04, 0x7e) // v_movreld_b32_e32 v2, v4
  private val movrelsd = Vec(0x02, 0x89, 0x08, 0x7e) // v_movrelsd_b32_e32 v4, v2

  for (
    (target, m0) <- Seq(
      Target("gfx1030") -> Vec(0xff, 0x03, 0xfc, 0xbe, 0x01, 0x00, 0x00, 0x00),
      Target("gfx1100") -> Vec(0xff, 0x00, 0xfd, 0xbe, 0x01, 0x00, 0x00, 0x00),
      Target("gfx1201") -> Vec(0xff, 0x00, 0xfd, 0xbe, 0x01, 0x00, 0x00, 0x00)
    )
  )
    test(s"$target v_movrels_b32, v_movreld_b32 and v_movrelsd_b32 offset their operands by s_mov_b32 m0, 1") {
      assertEquals(run(target, 0, 0, 4, m0, movrels), 11L)
      assertEquals(run(target, 0, 0, 3, m0, movreld), 12L)
      assertEquals(run(target, 0, 0, 5, m0, movrelsd), 11L)
    }
}
