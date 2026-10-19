package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** SALU forms outside the common arithmetic: GFX12 signed sub-dword loads, which hipcc emits for a uniform
  * `signed char` (`s_load_i8 s0, s[0:1], s4 offset:0x0` under `--offload-arch=gfx1201`), EXEC writes, relative SGPR
  * moves and bit replication; each with its LLVM bytes from `llvm-mc -show-encoding`.
  */
class AMDGPUScalarMiscSuite extends munit.FunSuite {
  import InstructionSource.*

  private val gfx1201 = Target("gfx1201")

  test("gfx1201 s_load_i8 and s_load_i16 sign-extend the byte and short they load") {
    def load(code: Vec[Int]): Long =
      laneResult(gfx1201, Vec(0x800180ffL), program(gfx1201, octets(code*)) :+ Instruction.VMove(2, Scalar(0)))
    assertEquals(load(Vec(0x03, 0x00, 0x01, 0xf4, 0x01, 0x00, 0x00, 0xf8)), 0xffffff80L)
    assertEquals(load(Vec(0x03, 0x40, 0x01, 0xf4, 0x02, 0x00, 0x00, 0xf8)), 0xffff8001L)
    assertEquals(load(Vec(0x03, 0x20, 0x01, 0xf4, 0x01, 0x00, 0x00, 0xf8)), 0x80L)
  }

  /** `code` with EXEC starting at 0xff and s1 or s[2:3] at 0xf0f; afterwards SDST, EXEC and SCC. */
  private def written(target: Target, code: Vec[Int], wave32: Boolean): (Long, Long, Long) = {
    val operations = Vec(
      Instruction.SMove(1, Constant(0xf0fL)),
      Instruction.SMove64(2, Constant(0xf0fL))
    ) ++ program(target, octets(code*)) ++ Vec(
      Instruction.SMove64(10, Scalar(Fixed.ExecLo)),
      Instruction.SCSelect(12, Constant(1L), Constant(0L), 32),
      Instruction.SMove64(Fixed.ExecLo, Constant(-1L)),
      Instruction.VMove(2, Scalar(0)),
      Instruction.VMove(3, Scalar(10)),
      Instruction.VMove(4, Scalar(12))
    )
    def read(result: Int) =
      laneResults(target, Vec.fill(16)(Vec.empty), operations, result.unsafe[VectorRegisterIndex], 0xffL, wave32).head
    (read(2), read(3), read(4))
  }

  test("s_and_not1_wrexec and s_and_not0_wrexec write the new EXEC to the destination and its emptiness to SCC") {
    val target = Target("gfx1100")
    assertEquals(written(target, Vec(0x01, 0x36, 0x80, 0xbe), wave32 = true), (0xf00L, 0xf00L, 1L))
    assertEquals(written(target, Vec(0x01, 0x34, 0x80, 0xbe), wave32 = true), (0xf0L, 0xf0L, 1L))
    val wave64 = Target("gfx1030")
    assertEquals(written(wave64, Vec(0x02, 0x3a, 0x80, 0xbe), wave32 = false), (0xf00L, 0xf00L, 1L))
    assertEquals(written(wave64, Vec(0x02, 0x39, 0x80, 0xbe), wave32 = false), (0xf0L, 0xf0L, 1L))
  }

  /** `code` after `s_mov_b32 m0, m0` with s1 = 11, s4 = 44, s5 = 55, s8 = 88 and s9 = 99; afterwards `result`. RDNA3
    * onwards encodes M0 as s125 rather than s124.
    */
  private def relative(target: Target, code: Vec[Int], m0: Long, result: Int): Long = {
    val move =
      (if (target.isa == ISA.RDNA3) Vec(0xff, 0x00, 0xfd, 0xbe)
       else if (target.isa == ISA.RDNA2) Vec(0xff, 0x03, 0xfc, 0xbe)
       else Vec(0xff, 0x00, 0xfc, 0xbe)) ++ Vec.tabulate(4)(index => ((m0 >>> (index * 8)) & 0xffL).toInt)
    scalarResult(
      target,
      Vec(0L, 11L, 0L, 0L, 44L, 55L),
      Vec(Instruction.SMove(8, Constant(88L)), Instruction.SMove(9, Constant(99L))) ++
        program(target, octets((move ++ code)*)),
      result.unsafe[ScalarRegisterIndex]
    )
  }

  for (
    (target, movrels, movreld) <- Seq(
      (Target("gfx906"), Vec(0x04, 0x2a, 0x80, 0xbe), Vec(0x01, 0x2c, 0x84, 0xbe)),
      (Target("gfx1030"), Vec(0x04, 0x2e, 0x80, 0xbe), Vec(0x01, 0x30, 0x84, 0xbe)),
      (Target("gfx1100"), Vec(0x04, 0x40, 0x80, 0xbe), Vec(0x01, 0x42, 0x84, 0xbe))
    )
  )
    test(s"$target s_movrels_b32 and s_movreld_b32 offset their source or destination by M0") {
      assertEquals(relative(target, movrels, 1L, 0), 55L)
      assertEquals(relative(target, movreld, 1L, 5), 11L)
      assertEquals(relative(target, movreld, 1L, 4), 44L)
    }

  test("gfx1030 s_movrels_b64 and s_movrelsd_2_b32 offset register pairs and both operands") {
    val target = Target("gfx1030")
    assertEquals(relative(target, Vec(0x04, 0x2f, 0x80, 0xbe), 4L, 0), 88L)
    assertEquals(relative(target, Vec(0x04, 0x2f, 0x80, 0xbe), 4L, 1), 99L)
    // M0[9:0] offsets the source and M0[25:16] the destination
    assertEquals(relative(target, Vec(0x04, 0x49, 0x80, 0xbe), (1L << 16) | 1L, 1), 55L)
  }

  test("gfx1201 s_sleep_var and the s_prefetch hints hipcc emits for their builtins leave registers unchanged") {
    // s_sleep_var s2, s_sleep_var 5, then s_prefetch_data, _inst, _data_pc_rel and _inst_pc_rel
    val code = octets(
      (Seq(0x02, 0x58, 0x80, 0xbe, 0x85, 0x58, 0x80, 0xbe) ++
        Seq(0xc0, 0xc0, 0x04, 0xf4, 0x00, 0x00, 0x00, 0xf8, 0xc0, 0x80, 0x04, 0xf4, 0x00, 0x00, 0x00, 0xf8) ++
        Seq(0xc0, 0x00, 0x05, 0xf4, 0x00, 0x00, 0x00, 0xf8, 0xc0, 0xa0, 0x04, 0xf4, 0x00, 0x00, 0x00, 0xf8))*
    )
    assertEquals(scalarResult(gfx1201, Vec(0L, 0L, 7L), program(gfx1201, code), 2), 7L)
  }

  for (
    (
      target,
      code
    ) <- Seq(Target("gfx906") -> Vec(0x01, 0x37, 0x82, 0xbe), Target("gfx1100") -> Vec(0x01, 0x14, 0x82, 0xbe))
  )
    test(s"$target s_bitreplicate_b64_b32 doubles each bit") {
      def replicated(value: Long, result: Int): Long = scalarResult(
        target,
        Vec(0L, value),
        program(target, octets(code*)),
        result.unsafe[ScalarRegisterIndex]
      )
      assertEquals(replicated(5L, 2), 0x33L)
      assertEquals(replicated(0x80000000L, 3), 0xc0000000L)
    }
}
