package net.kurobako.gemstone.isa.amdgpu

import MachineDefinitions.*
import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite

class MachineSemanticsSuite extends Suite {
  private def shown(text: String): String = {
    val (target, _, operands) = Instances.find(_.text == text).getOrElse(fail(s"missing $text"))
    machine(target, text, operands).fold(failure => fail(failure), ScalaBackend.show(_))
  }

  test("a conditional branch renders its condition and a target relative to the next instruction") {
    assertEquals(
      shown("s_cbranch_scc1 0"),
      """def s_cbranch_scc1(SIMM16: SInt[16], SSRC_SPECIAL_SCC: Bool)(using machine: Machine): Unit = {
        |  machine.branch(SSRC_SPECIAL_SCC, machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_cbranch_scc0 0"),
      """def s_cbranch_scc0(SIMM16: SInt[16], SSRC_SPECIAL_SCC: Bool)(using machine: Machine): Unit = {
        |  machine.branch(!SSRC_SPECIAL_SCC, machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_cbranch_execz 0"),
      """def s_cbranch_execz(SIMM16: SInt[16], SDST_EXEC: UInt[32])(using machine: Machine): Unit = {
        |  machine.branch(SDST_EXEC == UInt[32](0), machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_cbranch_vccnz 0"),
      """def s_cbranch_vccnz(SIMM16: SInt[16], VCC: UInt[64])(using machine: Machine): Unit = {
        |  machine.branch(VCC != UInt[64](0), machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
        |  ()
        |}""".stripMargin
    )
  }

  test("an unconditional branch jumps to the same target") {
    assertEquals(
      shown("s_branch 0"),
      """def s_branch(SIMM16: SInt[16])(using machine: Machine): Unit = {
        |  machine.jump(machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
        |  ()
        |}""".stripMargin
    )
  }

  test("calls save the address after the instruction and jump to a displaced or computed target") {
    assertEquals(
      shown("s_call_b64 s[0:1], 0"),
      """def s_call_b64(SIMM16: SInt[16])(using machine: Machine): (SDST: UInt[64]) = {
        |  val v0 = machine.pc() + 4
        |  machine.jump(machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
        |  (SDST = v0)
        |}""".stripMargin
    )
    assertEquals(
      shown("s_swappc_b64 s[0:1], s[2:3]"),
      """def s_swappc_b64(SSRC0: UInt[64])(using machine: Machine): (SDST: UInt[64]) = {
        |  val v0 = machine.pc() + 4
        |  machine.jumpTo(SSRC0)
        |  (SDST = v0)
        |}""".stripMargin
    )
    assertEquals(
      shown("s_getpc_b64 s[0:1]"),
      "def s_getpc_b64()(using machine: Machine): (SDST: UInt[64]) =\n  (SDST = machine.pc() + 4)"
    )
    assertEquals(
      shown("s_setpc_b64 s[0:1]"),
      """def s_setpc_b64(SSRC0: UInt[64])(using machine: Machine): Unit = {
        |  machine.jumpTo(SSRC0)
        |  ()
        |}""".stripMargin
    )
  }

  test("the end of a wave, a trap and padding render as requests") {
    assertEquals(
      shown("s_endpgm"),
      "def s_endpgm()(using machine: Machine): Unit = {\n  machine.exit()\n  ()\n}"
    )
    assertEquals(
      shown("s_trap 1"),
      "def s_trap(SIMM16: UInt[16])(using control: Control): Unit = {\n  control.trap(SIMM16)\n  ()\n}"
    )
    assertEquals(
      shown("s_code_end"),
      "def s_code_end()(using control: Control): Unit = {\n  control.fenceHost()\n  ()\n}"
    )
  }

  test("a fork requests the split of EXEC and a join the pop of the side it deferred") {
    assertEquals(
      shown("s_cbranch_i_fork s[0:1], 0"),
      """def s_cbranch_i_fork(SDST: UInt[64], SIMM16: SInt[16])(using control: Control, machine: Machine): Unit = {
        |  control.fork(SDST, machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_cbranch_g_fork s[0:1], s[2:3]"),
      """def s_cbranch_g_fork(SSRC0: UInt[64], SSRC1: UInt[64])(using control: Control): Unit = {
        |  control.fork(SSRC0, SSRC1)
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_cbranch_join s0"),
      "def s_cbranch_join(SSRC0: UInt[32])(using control: Control): Unit = {\n  control.join(uniform(SSRC0))\n  ()\n}"
    )
  }

  test("a workgroup barrier and a GWS operation request the arrival or the resource operation") {
    assertEquals(shown("s_barrier"), "def s_barrier()(using control: Control): Unit = {\n  control.barrier()\n  ()\n}")
    assertEquals(
      shown("ds_gws_barrier v1 offset:4 gds"),
      "def ds_gws_barrier(v1: UInt[32])(using control: Control): Unit = {\n" +
        "  control.globalWaveSync(\"ds_gws_barrier\", VMIR.GlobalWaveSyncOperation.Barrier, UInt[32](4), v1)\n  ()\n}"
    )
  }

  test("ds_append and ds_consume request the LDS counter and give each active lane the count before") {
    assertEquals(
      shown("ds_append v1 offset:4"),
      "def ds_append()(using control: Control): (v1: UInt[32]) = {\n" +
        "  val v0 = control.localCounter(false, UInt[32](4))\n  (v1 = v0)\n}"
    )
    assertEquals(
      shown("ds_consume v2 offset:8"),
      "def ds_consume()(using control: Control): (v2: UInt[32]) = {\n" +
        "  val v0 = control.localCounter(true, UInt[32](8))\n  (v2 = v0)\n}"
    )
  }

  test("named barriers request their set-up, signal, wait and leave, and write SCC from their results") {
    assertEquals(
      shown("s_barrier_leave"),
      "def s_barrier_leave()(using control: Control): (scc: Bool) = {\n  val v0 = control.leaveBarrier()\n  (scc = v0)\n}"
    )
    assertEquals(
      shown("s_barrier_wait -1"),
      "def s_barrier_wait()(using control: Control): Unit = {\n  control.awaitBarrier(UInt[64](-1))\n  ()\n}"
    )
  }

  test("named barrier operands decode the barrier they name, and a signal's count comes from M0") {
    assertEquals(
      shown("s_barrier_signal -1"),
      "def s_barrier_signal()(using control: Control): Unit = {\n  val _ = control.signalBarrier(UInt[64](uniform(-1)), UInt[64](0))\n  ()\n}"
    )
    assertEquals(
      shown("s_barrier_signal m0"),
      "def s_barrier_signal(m0: UInt[32])(using control: Control): Unit = {\n" +
        "  val _ = control.signalBarrier(uniform(m0).to[SInt[6]].to[UInt[64]], (uniform(m0) >> 16).to[UInt[7]].to[UInt[64]])\n" +
        "  ()\n}"
    )
    assertEquals(
      shown("s_barrier_init m0"),
      "def s_barrier_init(m0: UInt[32])(using control: Control): Unit = {\n" +
        "  control.initBarrier(uniform(m0).to[SInt[6]].to[UInt[64]], (uniform(m0) >> 16).to[UInt[64]])\n  ()\n}"
    )
    assertEquals(
      shown("s_barrier_join -1"),
      "def s_barrier_join()(using control: Control): Unit = {\n  control.joinBarrier(UInt[64](uniform(-1)))\n  ()\n}"
    )
  }

  test("a signal that reports whether it arrived first writes SCC from the result") {
    assertEquals(
      shown("s_barrier_signal_isfirst -1"),
      "def s_barrier_signal_isfirst()(using control: Control): (scc: Bool) = {\n" +
        "  val v0 = control.signalBarrier(UInt[64](uniform(-1)), UInt[64](0))\n  (scc = v0)\n}"
    )
  }

  test("a mode write keeps the bits outside its field and takes the field from the operand") {
    assertEquals(
      shown("s_setreg_b32 hwreg(HW_REG_MODE, 0, 4), s0"),
      "def s_setreg_b32(SSRC0: UInt[32], mode: UInt[32]): (mode: UInt[32]) =\n" +
        "  (mode = (mode & -16) | (uniform(SSRC0) << 0).to[UInt[4]].to[UInt[32]])"
    )
    assertEquals(
      shown("s_setreg_imm32_b32 hwreg(HW_REG_MODE, 0, 4), 3"),
      "def s_setreg_imm32_b32(mode: UInt[32]): (mode: UInt[32]) =\n" +
        "  (mode = ((mode & -16).to[UInt[64]] | (UInt[64](uniform(3)) << 0)).to[UInt[32]])"
    )
    assertEquals(
      shown("s_denorm_mode 3"),
      "def s_denorm_mode(mode: UInt[32]): (mode: UInt[32]) =\n" +
        "  (mode = ((mode & -241).to[UInt[64]] | ((UInt[64](uniform(3)) << 4) & 240)).to[UInt[32]])"
    )
    assertEquals(
      shown("s_round_mode 3"),
      "def s_round_mode(mode: UInt[32]): (mode: UInt[32]) =\n" +
        "  (mode = ((mode & -16).to[UInt[64]] | (UInt[64](uniform(3)) << 0)).to[UInt[32]])"
    )
  }

  test("a mode read takes its field from the register") {
    assertEquals(
      shown("s_getreg_b32 s0, hwreg(HW_REG_MODE, 4, 4)"),
      "def s_getreg_b32(mode: UInt[32]): (SDST: UInt[32]) =\n  (SDST = (mode >> 4).to[UInt[4]].to[UInt[32]])"
    )
  }

  test("clocks, halts and cache invalidations request their machine operation") {
    assertEquals(
      shown("s_memtime s[0:1]"),
      "def s_memtime()(using control: Control): (SDST: UInt[64]) =\n  (SDST = control.clock(VMIR.Clock.Shader))"
    )
    assertEquals(
      shown("s_memrealtime s[0:1]"),
      "def s_memrealtime()(using control: Control): (SDST: UInt[64]) =\n" +
        "  (SDST = control.clock(VMIR.Clock.RealTime))"
    )
    assertEquals(
      shown("s_sethalt 1"),
      "def s_sethalt()(using control: Control): Unit = {\n  control.halt(\"s_sethalt halts the wave\")\n  ()\n}"
    )
    assertEquals(
      shown("s_dcache_inv"),
      "def s_dcache_inv()(using control: Control): Unit = {\n  control.invalidateHostReads()\n  ()\n}"
    )
  }

  test("a wave id read writes the wave's index among its workgroup's waves") {
    assertEquals(
      shown("s_get_waveid_in_workgroup s0"),
      "def s_get_waveid_in_workgroup()(using control: Control): (SDATA: UInt[32]) =\n  (SDATA = control.waveId())"
    )
  }

  test("a relative move reads and writes the SGPRs M0 offsets through the wave, and faults outside the file") {
    assertEquals(
      shown("s_movrels_b32 s5, s1"),
      """def s_movrels_b32(m0: UInt[32], `s[1:2]`: UInt[64])(using control: Control): Unit = {
        |  val v0 = SInt[32](1) + m0.to[SInt[32]]
        |  control.requireScalars(UInt[64](5), v0.to[UInt[64]], UInt[32](1))
        |  control.writeScalarAt(UInt[64](5), if (v0 < 0) (`s[1:2]` >> 0).to[UInt[32]] else control.scalarAt(v0.to[UInt[64]]))
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_movreld_b32 s5, s1"),
      """def s_movreld_b32(m0: UInt[32])(using control: Control): Unit = {
        |  val v0 = SInt[32](5) + m0.to[SInt[32]]
        |  control.requireScalars(v0.to[UInt[64]], UInt[64](1), UInt[32](1))
        |  control.writeScalarAt(v0.to[UInt[64]], control.scalarAt(UInt[64](1)))
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_movreld_b64 s4, s[2:3]"),
      """def s_movreld_b64(m0: UInt[32])(using control: Control): Unit = {
        |  val v0 = SInt[32](4) + m0.to[SInt[32]]
        |  control.requireScalars(v0.to[UInt[64]], UInt[64](2), UInt[32](2))
        |  control.writeScalarAt(v0.to[UInt[64]], control.scalarAt(UInt[64](2)))
        |  control.writeScalarAt((v0.to[SInt[64]] + 1).to[UInt[64]], control.scalarAt(UInt[64](3)))
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_movrels_b64 s[4:5], s[2:3]"),
      """def s_movrels_b64(SSRC0: UInt[64], m0: UInt[32])(using control: Control): Unit = {
        |  val v0 = SInt[32](2) + m0.to[SInt[32]]
        |  control.requireScalars(UInt[64](4), v0.to[UInt[64]], UInt[32](2))
        |  control.writeScalarAt(UInt[64](4), if (v0 < 0) (SSRC0 >> 0).to[UInt[32]] else control.scalarAt(v0.to[UInt[64]]))
        |  control.writeScalarAt(UInt[64](5), if (v0 < 0) (SSRC0 >> 32).to[UInt[32]] else control.scalarAt((v0.to[SInt[64]] + 1).to[UInt[64]]))
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("s_movrelsd_2_b32 s5, s1"),
      """def s_movrelsd_2_b32(m0: UInt[32], `s[1:2]`: UInt[64])(using control: Control): Unit = {
        |  val v0 = (UInt[10](5).to[UInt[64]] + (m0 >> 16).to[UInt[10]].to[UInt[64]]).to[SInt[32]]
        |  val v1 = (UInt[10](1).to[UInt[64]] + m0.to[UInt[10]].to[UInt[64]]).to[SInt[32]]
        |  control.requireScalars(v0.to[UInt[64]], v1.to[UInt[64]], UInt[32](1))
        |  control.writeScalarAt(v0.to[UInt[64]], if (v1 < 0) (`s[1:2]` >> 0).to[UInt[32]] else control.scalarAt(v1.to[UInt[64]]))
        |  ()
        |}""".stripMargin
    )
  }

  test("a subvector loop reads EXEC once, branches on the snapshot and writes the halves it joins or splits") {
    assertEquals(
      shown("s_subvector_loop_begin s12, 0"),
      """def s_subvector_loop_begin(SDST: UInt[32], SIMM16: SInt[16], exec: UInt[32])(using machine: Machine): (SDST: UInt[32], exec: UInt[32]) = {
        |  val v0 = exec
        |  machine.branch(v0 == UInt[32](0), machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
        |  val v1 = v0 != UInt[32](0)
        |  (
        |    SDST = if (v0 == UInt[32](0)) uniform(SDST) else if (v1) v0 >> 32 else UInt[32](0),
        |    exec = if (v1) v0 else v0
        |  )
        |}""".stripMargin
    )
    assertEquals(
      shown("s_subvector_loop_end s12, 0"),
      """def s_subvector_loop_end(SDST: UInt[32], exec: UInt[32]): (exec: UInt[32]) = {
        |  val v0 = exec
        |  val v1 = uniform(SDST)
        |  val v2 = v0 >> 32
        |  (exec = if (v2 != UInt[32](0)) ((v2.to[UInt[64]] << 32) | v1.to[UInt[64]]).to[UInt[32]] else v0.to[UInt[64]].to[UInt[32]])
        |}""".stripMargin
    )
  }

  test("a wave64 subvector loop's end adds the second pass to the wave32 form") {
    val naming     = RenderContext.Names(Map("s12" -> "SDST").get, 64, Some(Target("gfx1030").isa))
    val machine    = AmdGpuMachineRender(naming, naming.named("SIMM16", AmdGpuMachineRender.Displacement))
    val steps      = ScalarControl.render(Instruction.SubvectorLoopRelative(false, 12, 0), machine, 0, wave64 = true)
    val definition = Definition.from(steps.get, "s_subvector_loop_end", naming, Vec("SDST", "SIMM16"), settle = true)
    assertEquals(
      definition.map(ScalaBackend.show(_)),
      Right(
        """def s_subvector_loop_end(SDST: UInt[32], SIMM16: SInt[16], exec: UInt[64])(using machine: Machine): (SDST: UInt[32], exec: UInt[64]) = {
          |  val v0 = exec
          |  val v1 = uniform(SDST)
          |  val v2 = (v0 >> 32) == UInt[64](0) && v1 != UInt[32](0)
          |  machine.branch(v2, machine.pc() + 4 + UInt[64](4) * SIMM16.to[UInt[64]])
          |  val v3 = v0 >> 32
          |  (
          |    SDST = if (v2) v0.to[UInt[32]] else v1,
          |    exec = if (v2) v1.to[UInt[64]] << 32 else if (v3 != UInt[64](0)) (v3 << 32) | v1.to[UInt[64]] else v0
          |  )
          |}""".stripMargin
      )
    )
  }

  test("instructions without machine semantics have none") {
    assertEquals(
      InstructionSemantics.machine(Target("gfx1030"), "s_setreg_imm32_b32 hwreg(HW_REG_TRAPSTS), 3", Map.empty),
      None
    )
    assertEquals(
      shown("s_waitcnt 0"),
      "def s_waitcnt()(using control: Control): Unit = {\n  control.fenceHost()\n  ()\n}"
    )
    assertEquals(
      shown("s_sleep 1"),
      "def s_sleep()(using control: Control): Unit = {\n  control.fenceHost()\n  ()\n}"
    )
    assertEquals(shown("s_nop 0"), "def s_nop()(using control: Control): Unit = {\n  control.fenceHost()\n  ()\n}")
    assertEquals(InstructionSemantics.machine(Target("gfx1030"), "s_add_u32 s0, s1, s2", Map.empty), None)
  }
}
