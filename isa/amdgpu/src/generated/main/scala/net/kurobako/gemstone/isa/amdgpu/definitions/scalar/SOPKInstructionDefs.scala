// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object SOPKInstructionDefs {
  case object SAddkCoI32 extends Opcode("s_addk_co_i32", 840)

  case object SAddkI32 extends Opcode("s_addk_i32", 841)

  case object SCallB64 extends Opcode("s_call_b64", 971)

  case object SCallI64 extends Opcode("s_call_i64", 972)

  case object SCbranchIFork extends Opcode("s_cbranch_i_fork", 980)

  case object SCmovkI32 extends Opcode("s_cmovk_i32", 995)

  case object SCmpkEqI32 extends Opcode("s_cmpk_eq_i32", 1038)

  case object SCmpkEqU32 extends Opcode("s_cmpk_eq_u32", 1039)

  case object SCmpkGeI32 extends Opcode("s_cmpk_ge_i32", 1040)

  case object SCmpkGeU32 extends Opcode("s_cmpk_ge_u32", 1041)

  case object SCmpkGtI32 extends Opcode("s_cmpk_gt_i32", 1042)

  case object SCmpkGtU32 extends Opcode("s_cmpk_gt_u32", 1043)

  case object SCmpkLeI32 extends Opcode("s_cmpk_le_i32", 1044)

  case object SCmpkLeU32 extends Opcode("s_cmpk_le_u32", 1045)

  case object SCmpkLgI32 extends Opcode("s_cmpk_lg_i32", 1046)

  case object SCmpkLgU32 extends Opcode("s_cmpk_lg_u32", 1047)

  case object SCmpkLtI32 extends Opcode("s_cmpk_lt_i32", 1048)

  case object SCmpkLtU32 extends Opcode("s_cmpk_lt_u32", 1049)

  case object SGetregB32 extends Opcode("s_getreg_b32", 1094)

  case object SMovkI32 extends Opcode("s_movk_i32", 1144)

  case object SMulkI32 extends Opcode("s_mulk_i32", 1156)

  case object SSetregB32 extends Opcode("s_setreg_b32", 1223)

  case object SSetregImm32B32 extends Opcode("s_setreg_imm32_b32", 1224)

  case object SSubvectorLoopBegin extends Opcode("s_subvector_loop_begin", 1243)

  case object SSubvectorLoopEnd extends Opcode("s_subvector_loop_end", 1244)

  case object SVersion extends Opcode("s_version", 1252)

  case object SWaitcntExpcnt extends Opcode("s_waitcnt_expcnt", 1270)

  case object SWaitcntLgkmcnt extends Opcode("s_waitcnt_lgkmcnt", 1271)

  case object SWaitcntVmcnt extends Opcode("s_waitcnt_vmcnt", 1272)

  case object SWaitcntVscnt extends Opcode("s_waitcnt_vscnt", 1273)

  val definitions: Vec[Opcode] = Vec(
    SAddkCoI32,
    SAddkI32,
    SCallB64,
    SCallI64,
    SCbranchIFork,
    SCmovkI32,
    SCmpkEqI32,
    SCmpkEqU32,
    SCmpkGeI32,
    SCmpkGeU32,
    SCmpkGtI32,
    SCmpkGtU32,
    SCmpkLeI32,
    SCmpkLeU32,
    SCmpkLgI32,
    SCmpkLgU32,
    SCmpkLtI32,
    SCmpkLtU32,
    SGetregB32,
    SMovkI32,
    SMulkI32,
    SSetregB32,
    SSetregImm32B32,
    SSubvectorLoopBegin,
    SSubvectorLoopEnd,
    SVersion,
    SWaitcntExpcnt,
    SWaitcntLgkmcnt,
    SWaitcntVmcnt,
    SWaitcntVscnt
  )
}
