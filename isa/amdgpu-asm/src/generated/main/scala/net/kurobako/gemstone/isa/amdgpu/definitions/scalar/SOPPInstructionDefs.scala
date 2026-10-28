// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object SOPPInstructionDefs {
  case object SBarrier extends Opcode("s_barrier", 897)

  case object SBarrierLeave extends Opcode("s_barrier_leave", 900)

  case object SBarrierWait extends Opcode("s_barrier_wait", 903)

  case object SBranch extends Opcode("s_branch", 923)

  case object SCbranchCdbgsys extends Opcode("s_cbranch_cdbgsys", 973)

  case object SCbranchCdbgsysAndUser extends Opcode("s_cbranch_cdbgsys_and_user", 974)

  case object SCbranchCdbgsysOrUser extends Opcode("s_cbranch_cdbgsys_or_user", 975)

  case object SCbranchCdbguser extends Opcode("s_cbranch_cdbguser", 976)

  case object SCbranchExecnz extends Opcode("s_cbranch_execnz", 977)

  case object SCbranchExecz extends Opcode("s_cbranch_execz", 978)

  case object SCbranchScc0 extends Opcode("s_cbranch_scc0", 982)

  case object SCbranchScc1 extends Opcode("s_cbranch_scc1", 983)

  case object SCbranchVccnz extends Opcode("s_cbranch_vccnz", 984)

  case object SCbranchVccz extends Opcode("s_cbranch_vccz", 985)

  case object SClause extends Opcode("s_clause", 988)

  case object SCodeEnd extends Opcode("s_code_end", 1050)

  case object SDecperflevel extends Opcode("s_decperflevel", 1069)

  case object SDelayAlu extends Opcode("s_delay_alu", 1070)

  case object SDenormMode extends Opcode("s_denorm_mode", 1071)

  case object SEndpgm extends Opcode("s_endpgm", 1072)

  case object SEndpgmOrderedPsDone extends Opcode("s_endpgm_ordered_ps_done", 1073)

  case object SEndpgmSaved extends Opcode("s_endpgm_saved", 1074)

  case object SIcacheInv extends Opcode("s_icache_inv", 1096)

  case object SIncperflevel extends Opcode("s_incperflevel", 1097)

  case object SInstPrefetch extends Opcode("s_inst_prefetch", 1098)

  case object SMonitorSleep extends Opcode("s_monitor_sleep", 1140)

  case object SNop extends Opcode("s_nop", 1161)

  case object SRoundMode extends Opcode("s_round_mode", 1199)

  case object SSendmsg extends Opcode("s_sendmsg", 1206)

  case object SSendmsghalt extends Opcode("s_sendmsghalt", 1209)

  case object SSetGprIdxMode extends Opcode("s_set_gpr_idx_mode", 1211)

  case object SSetGprIdxOff extends Opcode("s_set_gpr_idx_off", 1212)

  case object SSetInstPrefetchDistance extends Opcode("s_set_inst_prefetch_distance", 1214)

  case object SSetValuCoexecMode extends Opcode("s_set_valu_coexec_mode", 1216)

  case object SSetVgprMsb extends Opcode("s_set_vgpr_msb", 1217)

  case object SSethalt extends Opcode("s_sethalt", 1218)

  case object SSetkill extends Opcode("s_setkill", 1219)

  case object SSetprio extends Opcode("s_setprio", 1221)

  case object SSetprioIncWg extends Opcode("s_setprio_inc_wg", 1222)

  case object SSingleuseVdst extends Opcode("s_singleuse_vdst", 1228)

  case object SSleep extends Opcode("s_sleep", 1229)

  case object STrap extends Opcode("s_trap", 1247)

  case object STtracedata extends Opcode("s_ttracedata", 1250)

  case object STtracedataImm extends Opcode("s_ttracedata_imm", 1251)

  case object SWaitAlu extends Opcode("s_wait_alu", 1253)

  case object SWaitAsynccnt extends Opcode("s_wait_asynccnt", 1254)

  case object SWaitBvhcnt extends Opcode("s_wait_bvhcnt", 1255)

  case object SWaitDscnt extends Opcode("s_wait_dscnt", 1256)

  case object SWaitEvent extends Opcode("s_wait_event", 1257)

  case object SWaitExpcnt extends Opcode("s_wait_expcnt", 1258)

  case object SWaitIdle extends Opcode("s_wait_idle", 1259)

  case object SWaitKmcnt extends Opcode("s_wait_kmcnt", 1260)

  case object SWaitLoadcnt extends Opcode("s_wait_loadcnt", 1261)

  case object SWaitLoadcntDscnt extends Opcode("s_wait_loadcnt_dscnt", 1262)

  case object SWaitSamplecnt extends Opcode("s_wait_samplecnt", 1263)

  case object SWaitStorecnt extends Opcode("s_wait_storecnt", 1264)

  case object SWaitStorecntDscnt extends Opcode("s_wait_storecnt_dscnt", 1265)

  case object SWaitXcnt extends Opcode("s_wait_xcnt", 1267)

  case object SWaitcnt extends Opcode("s_waitcnt", 1268)

  case object SWaitcntDepctr extends Opcode("s_waitcnt_depctr", 1269)

  case object SWakeup extends Opcode("s_wakeup", 1274)

  val definitions: Vec[Opcode] = Vec(
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
    SSingleuseVdst,
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
