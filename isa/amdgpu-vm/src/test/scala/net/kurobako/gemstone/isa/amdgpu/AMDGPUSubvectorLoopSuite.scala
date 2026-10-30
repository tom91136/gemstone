package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite

/** GFX10's S_SUBVECTOR_LOOP_BEGIN and S_SUBVECTOR_LOOP_END, which run a wave64's loop body once for each half of its
  * lanes, and S_GET_WAVEID_IN_WORKGROUP. The loop expectations match a gfx1036 (RDNA2) in wave64, running probe kernels
  * of the form `s_subvector_loop_begin s12, .Lend; <body>; s_subvector_loop_end s12, .Lbody` under a range of EXEC
  * values. Bytes from `llvm-mc -arch=amdgcn -mcpu=gfx1030 -mattr=+wavefrontsize64 -show-encoding`.
  */
class AMDGPUSubvectorLoopSuite extends Suite {
  private val gfx1010 = Target("gfx1010")
  private val gfx1030 = Target("gfx1030")

  private val lanes = Vec.fill(64)(Vec.empty[Long])

  // the run places three instructions of set-up before the program, which its jump targets count
  private val Prefix = 3

  private def placed(target: Target, code: Vec[Byte]): Vec[Instruction] = program(target, code).map {
    case Instruction.SubvectorLoop(begin, state, at) => Instruction.SubvectorLoop(begin, state, at + Prefix)
    case Instruction.Branch(condition, at)           => Instruction.Branch(condition, at + Prefix)
    case other                                       => other
  }

  // s10 counts iterations, s12 is the loop state; v36 takes the iteration each lane ran in
  private val body = octets(
    0x80, 0x03, 0x8a, 0xbe,                         // s_mov_b32 s10, 0
    0xff, 0x03, 0x8c, 0xbe, 0x34, 0x12, 0x00, 0x00, // s_mov_b32 s12, 0x1234
    0x03, 0x00, 0x8c, 0xbd,                         // s_subvector_loop_begin s12, .Lend
    0x0a, 0x81, 0x0a, 0x80,                         // s_add_u32 s10, s10, 1
    0x0a, 0x02, 0x48, 0x7e,                         // v_mov_b32 v36, s10
    0xfd, 0xff, 0x0c, 0xbe,                         // s_subvector_loop_end s12, .Lbody
    0x7e, 0x04, 0x94, 0xbe,                         // s_mov_b64 s[20:21], exec
    0xc1, 0x04, 0xfe, 0xbe,                         // s_mov_b64 exec, -1
    0x14, 0x02, 0x4a, 0x7e,                         // v_mov_b32 v37, s20
    0x15, 0x02, 0x4c, 0x7e,                         // v_mov_b32 v38, s21
    0x0c, 0x02, 0x4e, 0x7e,                         // v_mov_b32 v39, s12
    0x0a, 0x02, 0x50, 0x7e                          // v_mov_b32 v40, s10
  )

  private def run(target: Target, exec: Long, result: Int): Vec[Long] =
    laneResults(target, lanes, placed(target, body), result.unsafe[VectorRegisterIndex], exec, wave32 = false)

  private val Iteration = 36
  private val ExecLow   = 37
  private val ExecHigh  = 38
  private val State     = 39
  private val Count     = 40

  private def lowHalf(exec: Long): Vec[Boolean] = Vec.tabulate(64)(lane => (exec & (1L << lane)) != 0)

  for (target <- Vec(gfx1010, gfx1030)) {
    test(s"${target.name} runs the body once for each half of the wave that has active lanes") {
      val exec = 0x0f0f0f0fff00ff00L
      val ran  = lowHalf(exec)
      assertEquals(run(target, exec, Iteration), Vec.tabulate(64)(lane => if (!ran(lane)) 0L else lane / 32 + 1L))
      assertEquals(run(target, exec, Count), Vec.fill(64)(2L))
      assertEquals(run(target, exec, ExecLow), Vec.fill(64)(exec & 0xffffffffL))
      assertEquals(run(target, exec, ExecHigh), Vec.fill(64)(exec >>> 32))
    }

    test(s"${target.name} keeps the other half's EXEC in the loop state and restores EXEC and the state after") {
      assertEquals(run(target, 0x400000003L, State), Vec.fill(64)(3L))
      assertEquals(run(target, 0x400000003L, ExecHigh), Vec.fill(64)(4L))
    }

    test(s"${target.name} runs the body once where only one half has active lanes, leaving EXEC and zeroing the state") {
      for (exec <- Vec(0xffffL, 0xffff00000000L)) {
        assertEquals(run(target, exec, Count), Vec.fill(64)(1L))
        assertEquals(run(target, exec, State), Vec.fill(64)(0L))
        assertEquals(run(target, exec, ExecLow), Vec.fill(64)(exec & 0xffffffffL))
        assertEquals(run(target, exec, ExecHigh), Vec.fill(64)(exec >>> 32))
      }
    }

    test(s"${target.name} skips the body and keeps the state where EXEC is zero") {
      assertEquals(run(target, 0L, Count), Vec.fill(64)(0L))
      assertEquals(run(target, 0L, State), Vec.fill(64)(0x1234L))
    }
  }

  test(
    "gfx1030 s_subvector_loop_end runs the other half from the loop state and a third pass when the body empties EXEC"
  ) {
    val repeated = octets(
      0x80, 0x03, 0x8a, 0xbe, // s_mov_b32 s10, 0
      0x05, 0x00, 0x8c, 0xbd, // s_subvector_loop_begin s12, .Lend
      0x0a, 0x81, 0x0a, 0x80, // s_add_u32 s10, s10, 1
      0x0a, 0x82, 0x06, 0xbf, // s_cmp_eq_u32 s10, 2
      0x01, 0x00, 0x84, 0xbf, // s_cbranch_scc0 .Lskip
      0x80, 0x03, 0xff, 0xbe, // s_mov_b32 exec_hi, 0
      0xfb, 0xff, 0x0c, 0xbe, // s_subvector_loop_end s12, .Lbody
      0xc1, 0x04, 0xfe, 0xbe, // s_mov_b64 exec, -1
      0x0a, 0x02, 0x50, 0x7e  // v_mov_b32 v40, s10
    )
    val operations = placed(gfx1030, repeated)
    assertEquals(
      laneResults(gfx1030, lanes, operations, 40, 0x400000003L, wave32 = false),
      Vec.fill(64)(3L)
    )
  }

  test("gfx1010 s_get_waveid_in_workgroup returns the wave's index in the workgroup") {
    val code = octets(0x40, 0x01, 0xa8, 0xf4, 0x00, 0x00, 0x00, 0x00)
    val read = program(gfx1010, code) :+ Instruction.VMove(2, InstructionSource.Scalar(5))
    assertEquals(
      laneResults(gfx1010, Vec.fill(96)(Vec(0L)), read, 2),
      Vec.tabulate(96)(lane => lane / 32L)
    )
    assertEquals(
      laneResults(gfx1010, Vec.fill(192)(Vec(0L)), read, 2, wave32 = false),
      Vec.tabulate(192)(lane => lane / 64L)
    )
  }
}
