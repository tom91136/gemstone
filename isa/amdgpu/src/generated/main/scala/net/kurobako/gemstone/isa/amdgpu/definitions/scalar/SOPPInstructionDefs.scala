// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

private[amdgpu] object SOPPInstructionDefs {
  case object SBarrier extends InstructionDef(
        AMDGPUOpcode.assume("s_barrier"),
        decoder(form(0)((_, mods) => Instruction.SBarrier(mods))),
        896
      )
  case object SBarrierLeave extends InstructionDef(
        AMDGPUOpcode.assume("s_barrier_leave"),
        decoder(form(0)((_, mods) => Instruction.SBarrierLeave(mods))),
        899
      )
  case object SBarrierWait extends InstructionDef(
        AMDGPUOpcode.assume("s_barrier_wait"),
        decoder(form(1)((operands, mods) => Instruction.SBarrierWait(operands(0), mods))),
        902
      )
  case object SBranch extends InstructionDef(
        AMDGPUOpcode.assume("s_branch"),
        decoder(form(1)((operands, mods) => Instruction.SBranch(operands(0), mods))),
        922
      )
  case object SCbranchCdbgsys extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_cdbgsys"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchCdbgsys(operands(0), mods))),
        972
      )
  case object SCbranchCdbgsysAndUser extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_cdbgsys_and_user"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchCdbgsysAndUser(operands(0), mods))),
        973
      )
  case object SCbranchCdbgsysOrUser extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_cdbgsys_or_user"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchCdbgsysOrUser(operands(0), mods))),
        974
      )
  case object SCbranchCdbguser extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_cdbguser"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchCdbguser(operands(0), mods))),
        975
      )
  case object SCbranchExecnz extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_execnz"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchExecnz(operands(0), mods))),
        976
      )
  case object SCbranchExecz extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_execz"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchExecz(operands(0), mods))),
        977
      )
  case object SCbranchScc0 extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_scc0"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchScc0(operands(0), mods))),
        981
      )
  case object SCbranchScc1 extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_scc1"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchScc1(operands(0), mods))),
        982
      )
  case object SCbranchVccnz extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_vccnz"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchVccnz(operands(0), mods))),
        983
      )
  case object SCbranchVccz extends InstructionDef(
        AMDGPUOpcode.assume("s_cbranch_vccz"),
        decoder(form(1)((operands, mods) => Instruction.SCbranchVccz(operands(0), mods))),
        984
      )
  case object SClause extends InstructionDef(
        AMDGPUOpcode.assume("s_clause"),
        decoder(form(1)((operands, mods) => Instruction.SClause(operands(0), mods))),
        987
      )
  case object SCodeEnd extends InstructionDef(
        AMDGPUOpcode.assume("s_code_end"),
        decoder(form(0)((_, mods) => Instruction.SCodeEnd(mods))),
        1049
      )
  case object SDecperflevel extends InstructionDef(
        AMDGPUOpcode.assume("s_decperflevel"),
        decoder(form(1)((operands, mods) => Instruction.SDecperflevel(operands(0), mods))),
        1068
      )
  case object SDelayAlu extends InstructionDef(
        AMDGPUOpcode.assume("s_delay_alu"),
        decoder(form(1)((operands, mods) => Instruction.SDelayAlu(operands(0), mods))),
        1069
      )
  case object SDenormMode extends InstructionDef(
        AMDGPUOpcode.assume("s_denorm_mode"),
        decoder(form(1)((operands, mods) => Instruction.SDenormMode(operands(0), mods))),
        1070
      )
  case object SEndpgm extends InstructionDef(
        AMDGPUOpcode.assume("s_endpgm"),
        decoder(
          form(0)((_, mods) => Instruction.SEndpgm_0(mods)),
          form(1)((operands, mods) => Instruction.SEndpgm_1(operands(0), mods))
        ),
        1071
      )
  case object SEndpgmOrderedPsDone extends InstructionDef(
        AMDGPUOpcode.assume("s_endpgm_ordered_ps_done"),
        decoder(form(0)((_, mods) => Instruction.SEndpgmOrderedPsDone(mods))),
        1072
      )
  case object SEndpgmSaved extends InstructionDef(
        AMDGPUOpcode.assume("s_endpgm_saved"),
        decoder(form(0)((_, mods) => Instruction.SEndpgmSaved(mods))),
        1073
      )
  case object SIcacheInv extends InstructionDef(
        AMDGPUOpcode.assume("s_icache_inv"),
        decoder(form(0)((_, mods) => Instruction.SIcacheInv(mods))),
        1095
      )
  case object SIncperflevel extends InstructionDef(
        AMDGPUOpcode.assume("s_incperflevel"),
        decoder(form(1)((operands, mods) => Instruction.SIncperflevel(operands(0), mods))),
        1096
      )
  case object SInstPrefetch extends InstructionDef(
        AMDGPUOpcode.assume("s_inst_prefetch"),
        decoder(form(1)((operands, mods) => Instruction.SInstPrefetch(operands(0), mods))),
        1097
      )
  case object SMonitorSleep extends InstructionDef(
        AMDGPUOpcode.assume("s_monitor_sleep"),
        decoder(form(1)((operands, mods) => Instruction.SMonitorSleep(operands(0), mods))),
        1139
      )
  case object SNop extends InstructionDef(
        AMDGPUOpcode.assume("s_nop"),
        decoder(form(1)((operands, mods) => Instruction.SNop(operands(0), mods))),
        1160
      )
  case object SRoundMode extends InstructionDef(
        AMDGPUOpcode.assume("s_round_mode"),
        decoder(form(1)((operands, mods) => Instruction.SRoundMode(operands(0), mods))),
        1198
      )
  case object SSendmsg extends InstructionDef(
        AMDGPUOpcode.assume("s_sendmsg"),
        decoder(form(1)((operands, mods) => Instruction.SSendmsg(operands(0), mods))),
        1205
      )
  case object SSendmsghalt extends InstructionDef(
        AMDGPUOpcode.assume("s_sendmsghalt"),
        decoder(form(1)((operands, mods) => Instruction.SSendmsghalt(operands(0), mods))),
        1208
      )
  case object SSetGprIdxMode extends InstructionDef(
        AMDGPUOpcode.assume("s_set_gpr_idx_mode"),
        decoder(form(1)((operands, mods) => Instruction.SSetGprIdxMode(operands(0), mods))),
        1210
      )
  case object SSetGprIdxOff extends InstructionDef(
        AMDGPUOpcode.assume("s_set_gpr_idx_off"),
        decoder(form(0)((_, mods) => Instruction.SSetGprIdxOff(mods))),
        1211
      )
  case object SSetInstPrefetchDistance extends InstructionDef(
        AMDGPUOpcode.assume("s_set_inst_prefetch_distance"),
        decoder(form(1)((operands, mods) => Instruction.SSetInstPrefetchDistance(operands(0), mods))),
        1213
      )
  case object SSetValuCoexecMode extends InstructionDef(
        AMDGPUOpcode.assume("s_set_valu_coexec_mode"),
        decoder(form(1)((operands, mods) => Instruction.SSetValuCoexecMode(operands(0), mods))),
        1215
      )
  case object SSetVgprMsb extends InstructionDef(
        AMDGPUOpcode.assume("s_set_vgpr_msb"),
        decoder(form(1)((operands, mods) => Instruction.SSetVgprMsb(operands(0), mods))),
        1216
      )
  case object SSethalt extends InstructionDef(
        AMDGPUOpcode.assume("s_sethalt"),
        decoder(form(1)((operands, mods) => Instruction.SSethalt(operands(0), mods))),
        1217
      )
  case object SSetkill extends InstructionDef(
        AMDGPUOpcode.assume("s_setkill"),
        decoder(form(1)((operands, mods) => Instruction.SSetkill(operands(0), mods))),
        1218
      )
  case object SSetprio extends InstructionDef(
        AMDGPUOpcode.assume("s_setprio"),
        decoder(form(1)((operands, mods) => Instruction.SSetprio(operands(0), mods))),
        1220
      )
  case object SSetprioIncWg extends InstructionDef(
        AMDGPUOpcode.assume("s_setprio_inc_wg"),
        decoder(form(1)((operands, mods) => Instruction.SSetprioIncWg(operands(0), mods))),
        1221
      )
  case object SSleep extends InstructionDef(
        AMDGPUOpcode.assume("s_sleep"),
        decoder(form(1)((operands, mods) => Instruction.SSleep(operands(0), mods))),
        1227
      )
  case object STrap extends InstructionDef(
        AMDGPUOpcode.assume("s_trap"),
        decoder(form(1)((operands, mods) => Instruction.STrap(operands(0), mods))),
        1245
      )
  case object STtracedata extends InstructionDef(
        AMDGPUOpcode.assume("s_ttracedata"),
        decoder(form(0)((_, mods) => Instruction.STtracedata(mods))),
        1248
      )
  case object STtracedataImm extends InstructionDef(
        AMDGPUOpcode.assume("s_ttracedata_imm"),
        decoder(form(1)((operands, mods) => Instruction.STtracedataImm(operands(0), mods))),
        1249
      )
  case object SWaitAlu extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_alu"),
        decoder(form(1)((operands, mods) => Instruction.SWaitAlu(operands(0), mods))),
        1251
      )
  case object SWaitAsynccnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_asynccnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitAsynccnt(operands(0), mods))),
        1252
      )
  case object SWaitBvhcnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_bvhcnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitBvhcnt(operands(0), mods))),
        1253
      )
  case object SWaitDscnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_dscnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitDscnt(operands(0), mods))),
        1254
      )
  case object SWaitEvent extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_event"),
        decoder(form(1)((operands, mods) => Instruction.SWaitEvent(operands(0), mods))),
        1255
      )
  case object SWaitExpcnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_expcnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitExpcnt(operands(0), mods))),
        1256
      )
  case object SWaitIdle extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_idle"),
        decoder(form(0)((_, mods) => Instruction.SWaitIdle(mods))),
        1257
      )
  case object SWaitKmcnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_kmcnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitKmcnt(operands(0), mods))),
        1258
      )
  case object SWaitLoadcnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_loadcnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitLoadcnt(operands(0), mods))),
        1259
      )
  case object SWaitLoadcntDscnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_loadcnt_dscnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitLoadcntDscnt(operands(0), mods))),
        1260
      )
  case object SWaitSamplecnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_samplecnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitSamplecnt(operands(0), mods))),
        1261
      )
  case object SWaitStorecnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_storecnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitStorecnt(operands(0), mods))),
        1262
      )
  case object SWaitStorecntDscnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_storecnt_dscnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitStorecntDscnt(operands(0), mods))),
        1263
      )
  case object SWaitXcnt extends InstructionDef(
        AMDGPUOpcode.assume("s_wait_xcnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitXcnt(operands(0), mods))),
        1265
      )
  case object SWaitcnt extends InstructionDef(
        AMDGPUOpcode.assume("s_waitcnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitcnt(operands(0), mods))),
        1266
      )
  case object SWaitcntDepctr extends InstructionDef(
        AMDGPUOpcode.assume("s_waitcnt_depctr"),
        decoder(form(1)((operands, mods) => Instruction.SWaitcntDepctr(operands(0), mods))),
        1267
      )
  case object SWakeup extends InstructionDef(
        AMDGPUOpcode.assume("s_wakeup"),
        decoder(form(0)((_, mods) => Instruction.SWakeup(mods))),
        1272
      )

  val definitions: Vec[InstructionDef] = Vec(
    SBarrier,
    SBarrierLeave,
    SBarrierWait,
    SBranch,
    SCbranchCdbgsys,
    SCbranchCdbgsysAndUser,
    SCbranchCdbgsysOrUser,
    SCbranchCdbguser,
    SCbranchExecnz,
    SCbranchExecz,
    SCbranchScc0,
    SCbranchScc1,
    SCbranchVccnz,
    SCbranchVccz,
    SClause,
    SCodeEnd,
    SDecperflevel,
    SDelayAlu,
    SDenormMode,
    SEndpgm,
    SEndpgmOrderedPsDone,
    SEndpgmSaved,
    SIcacheInv,
    SIncperflevel,
    SInstPrefetch,
    SMonitorSleep,
    SNop,
    SRoundMode,
    SSendmsg,
    SSendmsghalt,
    SSetGprIdxMode,
    SSetGprIdxOff,
    SSetInstPrefetchDistance,
    SSetValuCoexecMode,
    SSetVgprMsb,
    SSethalt,
    SSetkill,
    SSetprio,
    SSetprioIncWg,
    SSleep,
    STrap,
    STtracedata,
    STtracedataImm,
    SWaitAlu,
    SWaitAsynccnt,
    SWaitBvhcnt,
    SWaitDscnt,
    SWaitEvent,
    SWaitExpcnt,
    SWaitIdle,
    SWaitKmcnt,
    SWaitLoadcnt,
    SWaitLoadcntDscnt,
    SWaitSamplecnt,
    SWaitStorecnt,
    SWaitStorecntDscnt,
    SWaitXcnt,
    SWaitcnt,
    SWaitcntDepctr,
    SWakeup
  )
}
