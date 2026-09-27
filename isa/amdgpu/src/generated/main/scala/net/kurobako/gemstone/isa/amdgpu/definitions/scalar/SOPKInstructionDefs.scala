// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object SOPKInstructionDefs {
  case object SAddkCoI32 extends Definition(
        NonEmptyString("s_addk_co_i32"),
        decoder(form(2)((operands, mods) => Instruction.SAddkCoI32(operands(0), operands(1), mods))),
        839
      )
  case object SAddkI32 extends Definition(
        NonEmptyString("s_addk_i32"),
        decoder(form(2)((operands, mods) => Instruction.SAddkI32(operands(0), operands(1), mods))),
        840
      )
  case object SCallB64 extends Definition(
        NonEmptyString("s_call_b64"),
        decoder(
          form(1)((operands, mods) => Instruction.SCallB64_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCallB64_2(operands(0), operands(1), mods))
        ),
        970
      )
  case object SCallI64 extends Definition(
        NonEmptyString("s_call_i64"),
        decoder(form(2)((operands, mods) => Instruction.SCallI64(operands(0), operands(1), mods))),
        971
      )
  case object SCbranchIFork extends Definition(
        NonEmptyString("s_cbranch_i_fork"),
        decoder(form(2)((operands, mods) => Instruction.SCbranchIFork(operands(0), operands(1), mods))),
        979
      )
  case object SCmovkI32 extends Definition(
        NonEmptyString("s_cmovk_i32"),
        decoder(form(2)((operands, mods) => Instruction.SCmovkI32(operands(0), operands(1), mods))),
        994
      )
  case object SCmpkEqI32 extends Definition(
        NonEmptyString("s_cmpk_eq_i32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkEqI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkEqI32_2(operands(0), operands(1), mods))
        ),
        1037
      )
  case object SCmpkEqU32 extends Definition(
        NonEmptyString("s_cmpk_eq_u32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkEqU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkEqU32_2(operands(0), operands(1), mods))
        ),
        1038
      )
  case object SCmpkGeI32 extends Definition(
        NonEmptyString("s_cmpk_ge_i32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkGeI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkGeI32_2(operands(0), operands(1), mods))
        ),
        1039
      )
  case object SCmpkGeU32 extends Definition(
        NonEmptyString("s_cmpk_ge_u32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkGeU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkGeU32_2(operands(0), operands(1), mods))
        ),
        1040
      )
  case object SCmpkGtI32 extends Definition(
        NonEmptyString("s_cmpk_gt_i32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkGtI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkGtI32_2(operands(0), operands(1), mods))
        ),
        1041
      )
  case object SCmpkGtU32 extends Definition(
        NonEmptyString("s_cmpk_gt_u32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkGtU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkGtU32_2(operands(0), operands(1), mods))
        ),
        1042
      )
  case object SCmpkLeI32 extends Definition(
        NonEmptyString("s_cmpk_le_i32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLeI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLeI32_2(operands(0), operands(1), mods))
        ),
        1043
      )
  case object SCmpkLeU32 extends Definition(
        NonEmptyString("s_cmpk_le_u32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLeU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLeU32_2(operands(0), operands(1), mods))
        ),
        1044
      )
  case object SCmpkLgI32 extends Definition(
        NonEmptyString("s_cmpk_lg_i32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLgI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLgI32_2(operands(0), operands(1), mods))
        ),
        1045
      )
  case object SCmpkLgU32 extends Definition(
        NonEmptyString("s_cmpk_lg_u32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLgU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLgU32_2(operands(0), operands(1), mods))
        ),
        1046
      )
  case object SCmpkLtI32 extends Definition(
        NonEmptyString("s_cmpk_lt_i32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLtI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLtI32_2(operands(0), operands(1), mods))
        ),
        1047
      )
  case object SCmpkLtU32 extends Definition(
        NonEmptyString("s_cmpk_lt_u32"),
        decoder(
          form(1)((operands, mods) => Instruction.SCmpkLtU32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SCmpkLtU32_2(operands(0), operands(1), mods))
        ),
        1048
      )
  case object SGetregB32 extends Definition(
        NonEmptyString("s_getreg_b32"),
        decoder(
          form(1)((operands, mods) => Instruction.SGetregB32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SGetregB32_2(operands(0), operands(1), mods))
        ),
        1093
      )
  case object SMovkI32 extends Definition(
        NonEmptyString("s_movk_i32"),
        decoder(
          form(1)((operands, mods) => Instruction.SMovkI32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SMovkI32_2(operands(0), operands(1), mods))
        ),
        1143
      )
  case object SMulkI32 extends Definition(
        NonEmptyString("s_mulk_i32"),
        decoder(form(2)((operands, mods) => Instruction.SMulkI32(operands(0), operands(1), mods))),
        1155
      )
  case object SSetregB32 extends Definition(
        NonEmptyString("s_setreg_b32"),
        decoder(
          form(1)((operands, mods) => Instruction.SSetregB32_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.SSetregB32_2(operands(0), operands(1), mods))
        ),
        1222
      )
  case object SSetregImm32B32 extends Definition(
        NonEmptyString("s_setreg_imm32_b32"),
        decoder(form(2)((operands, mods) => Instruction.SSetregImm32B32(operands(0), operands(1), mods))),
        1223
      )
  case object SSubvectorLoopBegin extends Definition(
        NonEmptyString("s_subvector_loop_begin"),
        decoder(form(2)((operands, mods) => Instruction.SSubvectorLoopBegin(operands(0), operands(1), mods))),
        1241
      )
  case object SSubvectorLoopEnd extends Definition(
        NonEmptyString("s_subvector_loop_end"),
        decoder(form(2)((operands, mods) => Instruction.SSubvectorLoopEnd(operands(0), operands(1), mods))),
        1242
      )
  case object SVersion extends Definition(
        NonEmptyString("s_version"),
        decoder(form(1)((operands, mods) => Instruction.SVersion(operands(0), mods))),
        1250
      )
  case object SWaitcntExpcnt extends Definition(
        NonEmptyString("s_waitcnt_expcnt"),
        decoder(form(2)((operands, mods) => Instruction.SWaitcntExpcnt(operands(0), operands(1), mods))),
        1268
      )
  case object SWaitcntLgkmcnt extends Definition(
        NonEmptyString("s_waitcnt_lgkmcnt"),
        decoder(form(2)((operands, mods) => Instruction.SWaitcntLgkmcnt(operands(0), operands(1), mods))),
        1269
      )
  case object SWaitcntVmcnt extends Definition(
        NonEmptyString("s_waitcnt_vmcnt"),
        decoder(form(2)((operands, mods) => Instruction.SWaitcntVmcnt(operands(0), operands(1), mods))),
        1270
      )
  case object SWaitcntVscnt extends Definition(
        NonEmptyString("s_waitcnt_vscnt"),
        decoder(form(2)((operands, mods) => Instruction.SWaitcntVscnt(operands(0), operands(1), mods))),
        1271
      )

  val definitions: Vec[Definition] = Vec(
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
