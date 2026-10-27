package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

/** The machine definitions of assembled scalar control instructions, over the operand names the explorer gives them. */
private object MachineDefinitions {
  private val Exec   = "exec" -> "SDST_EXEC"
  private val Scc    = "scc"  -> "SSRC_SPECIAL_SCC"
  private val Vcc    = "vcc"  -> "VCC"
  private val Source = Seq("s[0:1]" -> "SSRC0")
  private val Dest   = Seq("s[0:1]" -> "SDST")

  /** The instances, each as its target, assembly and operand names. */
  val Instances: Vec[(target: Target, text: String, operands: Seq[(String, String)])] = Vec(
    (Target("gfx1030"), "s_branch 0", Seq.empty),
    (Target("gfx906"), "s_branch 0", Seq.empty),
    (Target("gfx1030"), "s_cbranch_scc0 0", Seq(Scc)),
    (Target("gfx1030"), "s_cbranch_scc1 0", Seq(Scc)),
    (Target("gfx1030"), "s_cbranch_vccz 0", Seq(Vcc)),
    (Target("gfx906"), "s_cbranch_vccnz 0", Seq(Vcc)),
    (Target("gfx1030"), "s_cbranch_execz 0", Seq(Exec)),
    (Target("gfx906"), "s_cbranch_execnz 0", Seq(Exec)),
    (Target("gfx1030"), "s_call_b64 s[0:1], 0", Dest),
    (Target("gfx1030"), "s_getpc_b64 s[0:1]", Dest),
    (Target("gfx1030"), "s_setpc_b64 s[0:1]", Source),
    (Target("gfx1030"), "s_rfe_b64 s[0:1]", Source),
    (Target("gfx1030"), "s_swappc_b64 s[0:1], s[2:3]", Seq("s[0:1]" -> "SDST", "s[2:3]" -> "SSRC0")),
    (Target("gfx1030"), "s_endpgm", Seq.empty),
    (Target("gfx1030"), "s_endpgm_saved", Seq.empty),
    (Target("gfx1030"), "s_trap 1", Seq.empty),
    (Target("gfx1030"), "s_code_end", Seq.empty),
    (Target("gfx1030"), "s_waitcnt 0", Seq.empty),
    (Target("gfx1030"), "s_sleep 1", Seq.empty),
    (Target("gfx1030"), "s_nop 0", Seq.empty),
    (Target("gfx1030"), "s_cbranch_cdbguser 0", Seq.empty),
    (Target("gfx1030"), "s_barrier", Seq.empty),
    (Target("gfx1030"), "ds_gws_barrier v1 offset:4 gds", Seq.empty),
    (Target("gfx90a"), "ds_gws_sema_p offset:0 gds", Seq.empty),
    (Target("gfx12"), "ds_append v1 offset:4", Seq.empty),
    (Target("gfx12"), "ds_consume v2 offset:8", Seq.empty),
    (Target("gfx1200"), "ds_append v1 offset:4", Seq.empty),
    (Target("gfx1200"), "ds_consume v2 offset:8", Seq.empty),
    (Target("gfx1201"), "ds_append v1 offset:4", Seq.empty),
    (Target("gfx1201"), "ds_consume v2 offset:8", Seq.empty),
    (Target("gfx1250"), "ds_append v1 offset:4", Seq.empty),
    (Target("gfx1250"), "ds_consume v2 offset:8", Seq.empty),
    (Target("gfx1251"), "ds_append v1 offset:4", Seq.empty),
    (Target("gfx1251"), "ds_consume v2 offset:8", Seq.empty),
    (Target("gfx1250"), "s_barrier_init m0", Seq.empty),
    (Target("gfx1250"), "s_barrier_join -1", Seq.empty),
    (Target("gfx1250"), "s_barrier_leave", Seq.empty),
    (Target("gfx1250"), "s_barrier_signal -1", Seq.empty),
    (Target("gfx1250"), "s_barrier_signal_isfirst -1", Seq.empty),
    (Target("gfx1250"), "s_barrier_signal m0", Seq.empty),
    (Target("gfx1250"), "s_barrier_wait -1", Seq.empty),
    (Target("gfx1030"), "s_getreg_b32 s0, hwreg(HW_REG_MODE, 4, 4)", Seq("s0" -> "SDST")),
    (Target("gfx1030"), "s_setreg_b32 hwreg(HW_REG_MODE, 0, 4), s0", Seq("s0" -> "SSRC0")),
    (Target("gfx1030"), "s_setreg_imm32_b32 hwreg(HW_REG_MODE, 0, 4), 3", Seq.empty),
    (Target("gfx1030"), "s_denorm_mode 3", Seq.empty),
    (Target("gfx1030"), "s_round_mode 3", Seq.empty),
    (Target("gfx1100"), "s_getreg_b32 s2, hwreg(HW_REG_SHADER_CYCLES, 0, 20)", Seq("s2" -> "SDST")),
    (Target("gfx1030"), "s_memtime s[0:1]", Dest),
    (Target("gfx1030"), "s_memrealtime s[0:1]", Dest),
    (Target("gfx1030"), "s_sethalt 1", Seq.empty),
    (Target("gfx1030"), "v_illegal", Seq.empty),
    (Target("gfx1030"), "s_dcache_inv", Seq.empty),
    (Target("gfx1030"), "buffer_gl0_inv", Seq.empty),
    (Target("gfx1010"), "s_get_waveid_in_workgroup s0", Seq("s0" -> "SDATA")),
    (Target("gfx90a"), "s_cbranch_g_fork s[0:1], s[2:3]", Seq("s[0:1]" -> "SSRC0", "s[2:3]" -> "SSRC1")),
    (Target("gfx90a"), "s_cbranch_i_fork s[0:1], 0", Dest),
    (Target("gfx90a"), "s_cbranch_join s0", Seq("s0" -> "SSRC0")),
    (Target("gfx1030"), "s_movreld_b32 s5, s1", Seq("s1" -> "SSRC0")),
    (Target("gfx1030"), "s_movreld_b64 s4, s[2:3]", Seq("s[2:3]" -> "SSRC0")),
    (Target("gfx1030"), "s_movrels_b32 s5, s1", Seq("s1" -> "SSRC0")),
    (Target("gfx1030"), "s_movrels_b64 s[4:5], s[2:3]", Seq("s[2:3]" -> "SSRC0")),
    (Target("gfx1030"), "s_movrelsd_2_b32 s5, s1", Seq("s1" -> "SSRC0")),
    (Target("gfx1030"), "s_subvector_loop_begin s12, 0", Seq("s12" -> "SDST")),
    (Target("gfx1030"), "s_subvector_loop_end s12, 0", Seq("s12" -> "SDST"))
  )

  /** The operand table's order: the immediate first, except after the destination of the SOPK forms. */
  private def order(text: String, operands: Seq[(String, String)]): Vec[String] = {
    val names = operands.map(_._2)
    Vec.from(
      if (text.startsWith("s_call_b64") || text.startsWith("s_cbranch_i_fork") || text.startsWith("s_subvector_loop_"))
        names :+ "SIMM16"
      else if (
        text.startsWith("s_trap") || text.startsWith("s_branch") || text.startsWith("s_cbranch_") && text.endsWith(" 0")
      )
        "SIMM16" +: names
      else names
    )
  }

  def machine(target: Target, text: String, operands: Seq[(String, String)]): Either[String, Definition] =
    InstructionSemantics.machine(
      target,
      text,
      operands.toMap,
      order(text, operands),
      Some(text.takeWhile(!_.isWhitespace))
    ).toRight(s"$text has no machine semantics").flatMap(_.left.map(_.toString))
}
